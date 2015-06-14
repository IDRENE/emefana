/**
 * 
 */
package com.idrene.emefana.service;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.GeoResults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.idrene.emefana.domain.Booking;
import com.idrene.emefana.domain.BookingStatus;
import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;
import com.idrene.emefana.domain.FileMetadata;
import com.idrene.emefana.domain.Price;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.domain.ProviderCategories;
import com.idrene.emefana.domain.QBooking;
import com.idrene.emefana.domain.QUser;
import com.idrene.emefana.domain.SearchCriteria;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.repositories.BookingRepository;
import com.idrene.emefana.repositories.CityRepository;
import com.idrene.emefana.repositories.EventTypeRepository;
import com.idrene.emefana.repositories.PersonRepository;
import com.idrene.emefana.repositories.ProviderRepository;
import com.idrene.emefana.repositories.ProviderTypeRepository;
import com.idrene.emefana.repositories.ServiceOfferingRepository;
import com.idrene.emefana.security.EMEFANA_ROLES;
import com.idrene.emefana.util.DateConvertUtil;
import com.idrene.emefana.util.EmefanaIDGenerator;
import com.idrene.emefana.util.UtilityBean;
import com.mongodb.gridfs.GridFSDBFile;
import com.mysema.query.BooleanBuilder;


/**
 * @author iddymagohe
 * @since 1.0
 */
public interface EmefanaService {

	//TODO Method level security, ProviderUsers, Admin Users & Customer Users
	
	public Optional<GeoResults<Provider>> searchProvidersByCriteria(SearchCriteria criteria);

	public Optional<Provider> findProviderById(String providerId);
	
	public Optional<Provider> findActiveProviderById(String providerId);
	
	
	public Optional<Page<Provider>> findProvider(boolean active , LocalDate startDate,LocalDate toDate);

	public Optional<User> findUser(User user);

	public Optional<List<Booking>> retriveUserBookings(User user);

	public Optional<User> registerUser(User user) throws EntityExists;
	
	public Optional<User> registerListingContactPerson(User user) ;
	
	public List<User> findProviderUsers(String provider);
	
	
	
	/**
	 * Provider or Listing registration
	 * @param provider
	 * @return
	 * @throws EntityExists
	 */
	public Optional<Provider> registerProvider(Provider provider) throws EntityExists;
	
	
	/**
	 * Provider activation 
	 * @param providerId
	 * @param status
	 */
	public void activateProvider(String providerId, boolean status);
	//TODO add , retrieval and associated files contents to providers via #{@link #GridFsService}


	/**
	 * No new booking for #BOOKINGSTATE.FULFILLMENT, #BOOKINGSTATE.CONFIRMED, status
	 * @param bookingCriteria
	 * @return
	 * @throws EntityExists
	 */
	public Optional<Booking> bookProvider(SearchCriteria bookingCriteria) throws EntityExists;
	
	public Optional<Booking> updateBookingStatus(String bookingId,String providerOrcustomerId ,BOOKINGSTATE newState );
	
	public  List<Booking> retrieveProviderBookingsByStatus(String providerId, BOOKINGSTATE state);
	
	public Optional<Booking> retrieveProviderBooking(String bookingId, String providerId);
	
	public List<Booking> retrieveUserBookingsByStatus(String userId, BOOKINGSTATE state);
	
	public Optional<Booking> retrieveUserBooking(String bookingId, String userId);

}

@Service
class EmefanaServiceImpl implements EmefanaService {
	@Autowired
	@Lazy(true)
	PasswordEncoder passwordEncryptor;
	
	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private ProviderTypeRepository providerTypeRepository;

	@Autowired
	private ServiceOfferingRepository offeringTypeRepository;

	@Autowired
	private EventTypeRepository eventTypeRepository;

	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private PersonRepository userRepository;

	@Autowired
	private UtilityBean utilityBean;
	
	@Autowired
	private GridFsService imageService;

	@Override
	public Optional<GeoResults<Provider>> searchProvidersByCriteria(SearchCriteria criteria) {
		Assert.notNull(criteria, "Criteria must not be null");
		return Optional.ofNullable(providerRepository.findAllProviders(criteria, bookingsByDates(criteria, false,BOOKINGSTATE.DONE, BOOKINGSTATE.NEW,BOOKINGSTATE.CANCELLED)));
	}

	@Override
	public Optional<Provider> findProviderById(String providerId) {
		Assert.notNull(providerId, "providerId must not be null");
		Provider provider = providerRepository.findOne(providerId);
		Optional<Provider> oProvider = Optional.ofNullable(provider);
		oProvider.ifPresent(p-> {
			 p.setThumnailPhoto(retriveProviderThumbnail(provider.getPid()));
		});
	   
		return oProvider;
	}
	
	@Override
	public Optional<Provider> findActiveProviderById(String providerId) {
		Assert.notNull(providerId, "providerId must not be null");
		Provider provider = providerRepository.findByPidAndActivatedIsTrue(providerId);
		Optional<Provider> oProvider = Optional.ofNullable(provider);
		oProvider.ifPresent(p-> {
			 //p.setThumnailPhoto(retriveProviderThumbnail(provider.getPid())); don`t need this for provider details of public site
			//TODO build photo gallery
		});
		return oProvider;
	}

	@Override
	public Optional<User> findUser(User user) {
	    Assert.isTrue(user.getOemailAddress().isPresent() || user.getOuserId().isPresent(),"userId or emailAddresss must not be empty");
		QUser quser = QUser.user;
		BooleanBuilder userCriteria = new BooleanBuilder(quser.password.eq(""));
		user.getOuserId().ifPresent(id -> userCriteria.and(quser.id.eq(id)));
		user.getOemailAddress().ifPresent(email -> userCriteria.and(quser.emailAddress.eq(email)));
		User dbUser = userRepository.findOne(userCriteria.getValue());
		return Optional.ofNullable(dbUser);
	}

	
	@Override
	public Optional<List<Booking>> retriveUserBookings(User user) {
		final QBooking qbooking = QBooking.booking;
		BooleanBuilder bookingCriteria = new BooleanBuilder();
		user.getOAssociatedProvider().ifPresent(provider -> {
			Provider dbProvider = providerRepository.findOne(provider);
					bookingCriteria.and(qbooking.provider.eq(dbProvider)).or(
							qbooking.provider.parent.eq(dbProvider));
				});

		if (!user.getOAssociatedProvider().isPresent()) {
			user.getOuserId().ifPresent(
					userId -> bookingCriteria.and(qbooking.customer
							.eq(user)));
		}
		Iterable<Booking> bookings = bookingRepository.findAll(bookingCriteria.getValue());
		return Optional.ofNullable(UtilityBean.toList(bookings));
	}

	/* (non-Javadoc)
	 * @see com.idrene.emefana.service.EmefanaService#registerUser(com.idrene.emefana.domain.User)
	 * Provider user's will have provider specific accounts, their personal account is required for personal use(not provider related)
	 */
	@Override
	public Optional<User> registerUser(User user) throws EntityExists{
		Assert.isTrue(user.getOemailAddress().isPresent() && user.getOuserId().isPresent(),"UserId and Email are mandatory fields");
		Optional<User> dbUser=Optional.ofNullable(userRepository.findByIdOrEmailAddressAllIgnoreCase(user.getId(), user.getEmailAddress()));
		if(dbUser.isPresent()) throw new EntityExists(user + " exists");
		user.setPassword(passwordEncryptor.encode(user.getPassword()));
		user.addRoles(EMEFANA_ROLES.USER);
		dbUser = Optional.of(userRepository.save(user));
		return dbUser;
	}

	@Override
	public Optional<Provider> registerProvider(Provider provider) throws EntityExists {
		Assert.notNull(provider, "Provider must not be null");
		Optional<Provider> dbProvider = Optional.ofNullable(providerRepository.findByNameIgnoreCase(provider.getName()));
		if(dbProvider.isPresent()) throw new EntityExists(provider.getName() + " exists");
		provider.setPid(UtilityBean.generateProviderId());
		provider.setRegistrationDate(DateConvertUtil.asUtilDate(LocalDate.now()));
		provider.setCode(UtilityBean.generateProviderCode(provider.getPid()));
		dbProvider = Optional.of(providerRepository.save(provider));
		return  dbProvider;
	}
	
	
	/**
	 * Returns a list of bookings  on that date
	 * @param criteria
	 * @param isBooking 
	 * @return
	 */
	private List<Booking> bookingsByDates(SearchCriteria criteria, boolean isBooking, BOOKINGSTATE ... exludeStates){
		final QBooking qbooking = QBooking.booking;
		BooleanBuilder bookingCriteria = new BooleanBuilder();
		boolean isVenueBooking = criteria.getProviderType().equals(ProviderCategories.Venues.name());
		criteria.getOFromDate()
				.ifPresent(
						from -> {
							LocalDate todate = criteria.getOToDate().isPresent() ? 
							criteria.getOToDate().get(): criteria.getOFromDate().get();
							bookingCriteria.and(qbooking.startDate
									.eq(DateConvertUtil.asUtilDate(from))
									.or(qbooking.startDate.between(
											DateConvertUtil.asUtilDate(from),
											DateConvertUtil.asUtilDate(todate))));
						});
		bookingCriteria.and(isVenueBooking ? qbooking.venueBooking.isTrue() : qbooking.venueBooking.isTrue());
		
		bookingCriteria.and(qbooking.status.currentState.notIn(exludeStates));
		
		List<Booking> bookings = UtilityBean.toList(bookingRepository.findAll(bookingCriteria.getValue()));
		
		/*
		 * For booking purpose, return bookings associated with a provider (filter 1 below)
		 * for a venuebooking consider that venue only (filter 2 below)
		 */
		if (isBooking && CollectionUtils.isNotEmpty(bookings)) {
			bookings = bookings.stream()
					.filter(b -> b.getProvider().getPid().equals(criteria.getAssociatedProvider()))
					.filter(b -> {
						if (!isVenueBooking) return true;
						return b.isVenueBooking() && b.getVenueDetail().equals(criteria.getVenue());
					}).collect(toList());
		}
		return bookings;
	}

	@Override
	public Optional<User> registerListingContactPerson(User user) {
		Assert.isTrue(user.getOemailAddress().isPresent()
				&& user.getOuserId().isPresent(),"UserId and Email are mandatory fields");
		Optional<User> dbUser = Optional.ofNullable(userRepository
				.findByIdOrEmailAddressAllIgnoreCase(user.getId(),user.getEmailAddress()));
		if (!dbUser.isPresent()) {
			dbUser = Optional.of(userRepository.save(user));
		}
		return dbUser;
	}

	@Override
	public Optional<Page<Provider>> findProvider(boolean active,LocalDate startDate, LocalDate toDate) {
		Date date1 = startDate != null ? DateConvertUtil.asUtilDate(startDate) : DateConvertUtil.asUtilDate(LocalDate.now().minusWeeks(4));
		Date date2 = toDate != null ? DateConvertUtil.asUtilDate(toDate): DateConvertUtil.asUtilDate(LocalDate.now().plusDays(1));
		Pageable page = new PageRequest(0, 50);//TODO change the hard-coded 50 records 
		Page<Provider> providers = active ? 
				 providerRepository.findByActivatedIsTrueAndRegistrationDateBetweenOrderByRegistrationDateAsc(date1, date2, page)
				: providerRepository.findByActivatedIsFalseAndRegistrationDateBetweenOrderByRegistrationDateAsc(date1, date2, page);
				 
		return Optional.ofNullable(providers);
	}

	@Override
	public List<User> findProviderUsers(String provider) {
		return userRepository.findByassociatedProvider(provider);
	}

	@Override
	public void activateProvider(String providerId, boolean status) {
		Optional<Provider> provider = findProviderById(providerId);
		provider.ifPresent(p -> {
			p.setActivated(status);
			providerRepository.save(p);
		});
		
	}
	
	private  FileMetadata retriveProviderThumbnail(String providerId){
		GridFSDBFile thumbnail = imageService.getThumbnailOrVedeoFile(new FileMetadata(providerId, null, null));
		FileMetadata fileMeta = new FileMetadata(Optional.ofNullable(thumbnail));
		return fileMeta;
		
	}

	@Override
	public Optional<Booking> bookProvider(SearchCriteria bookingCriteria)  throws EntityExists{
		// TODO Notifications (email, text messages) , to provider & consumer
		Booking booking = createBooking(bookingCriteria);
		synchronized(this){
			List<Booking> similarBookings = bookingsByDates(bookingCriteria, true, BOOKINGSTATE.NEW, BOOKINGSTATE.CANCELLED,BOOKINGSTATE.DONE);
			if (CollectionUtils.isNotEmpty(similarBookings)) throw  new EntityExists(similarBookings.get(0) + " Booking exists");
				return Optional.ofNullable(bookingRepository.save(booking));
		}
		
	}
	
	private Booking createBooking(SearchCriteria bookingCriteria){
		Assert.notNull(bookingCriteria, "Booking criteria can not be null for a booking");
		Assert.hasText(bookingCriteria.getAssociatedProvider(), "provider ID can not be null for a booking");
		Assert.hasText(bookingCriteria.getProviderType(), "Provider type can not br null for a booking");
		Assert.notNull(bookingCriteria.getEvent(), " Event type can not be null for a booking");
		boolean isVenue = bookingCriteria.getProviderType().equals(ProviderCategories.Venues.name());
		if (isVenue) Assert.notNull(bookingCriteria.getVenue(), "Venue details can not be null for a Venue booking");
		Assert.notNull(bookingCriteria.getCustomer(), "Customer can not be null for a booking");
		Assert.hasText(bookingCriteria.getCustomer().getEmailAddress(), "Customer EmailAddress can not be null for a booking ");
		
		Booking booking = new Booking();
		booking.setBid(EmefanaIDGenerator.generateProviderId());
		booking.setCustomer(userRepository.findOne(bookingCriteria.getCustomer().getEmailAddress()));
		booking.setStartDate(bookingCriteria.getFromDate());
		booking.setEndDate(bookingCriteria.getToDate());
		booking.setEvent(bookingCriteria.getEvent());
		booking.setVenueBooking(isVenue);
		booking.setProvider(providerRepository.findOne(bookingCriteria.getAssociatedProvider()));
		
		Price price = new Price(bookingCriteria.getPrice(),null);
		
		if (booking.isVenueBooking()) {
			booking.setVenueDetail(bookingCriteria.getVenue());
			int c = comparingDouble(Price::getPrice).compare(price, booking.getVenueDetail().getPrice());
			price =  c > 0? price : booking.getVenueDetail().getPrice();
		}else{
			//TODO associate #BookedService here and calculate price
		}
		
		booking.setStatus(new BookingStatus(price, new Price(BigDecimal.ZERO.doubleValue(),null), BOOKINGSTATE.NEW));
		
		return booking;
		
	}

	
	@Override
	public List<Booking> retrieveProviderBookingsByStatus(String providerId,BOOKINGSTATE state) {
		return bookingRepository.findByProviderPidAndStatusCurrentState(providerId, state);
	}

	@Override
	public Optional<Booking> retrieveProviderBooking(String bookingId,String providerId) {
		return Optional.ofNullable(bookingRepository.findByBidAndProviderPid(bookingId, providerId));
	}

	@Override
	public List<Booking> retrieveUserBookingsByStatus(String userId,BOOKINGSTATE state) {
		return bookingRepository.findByCustomerIdAndStatusCurrentState(userId, state);
	}

	@Override
	public Optional<Booking> retrieveUserBooking(String bookingId,String userId) {
		return Optional.ofNullable(bookingRepository.findByBidAndCustomerId(bookingId, userId));
	}

	@Override
	public Optional<Booking> updateBookingStatus(String bookingId,String providerOrcustomerId ,BOOKINGSTATE newState) {
		// TODO Validate state specific changes
		Optional<Booking> bookingToUpdate = Optional.empty();
		Booking booking = bookingRepository.findOne(bookingId);
		if (null != booking ){
			 if (booking.getProvider().getPid().equals(providerOrcustomerId) || booking.getCustomer().getId().equals(providerOrcustomerId) ){
				 booking.getStatus().setCurrentState(newState);
				 bookingRepository.save(booking);
				 bookingToUpdate = Optional.of(booking);
			 }
		}
		return bookingToUpdate;
	}

	

}