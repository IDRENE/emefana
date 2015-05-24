/**
 * 
 */
package com.idrene.emefana.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;
import com.idrene.emefana.domain.FileMetadata;
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
import com.idrene.emefana.util.UtilityBean;
import com.mongodb.gridfs.GridFSDBFile;
import com.mysema.query.BooleanBuilder;


/**
 * @author iddymagohe
 * @since 1.0
 */
public interface EmefanaService {

	public Optional<GeoResults<Provider>> searchProvidersByCriteria(SearchCriteria criteria);

	public Optional<Provider> findProviderById(String providerId);
	
	public Optional<Page<Provider>> findProvider(boolean active , LocalDate startDate,LocalDate toDate);

	public Optional<User> findUser(User user);

	public Optional<List<Booking>> retriveUserBookings(User user);

	public Optional<User> registerUser(User user) throws EntityExists;
	
	public Optional<User> registerListingContactPerson(User user) ;
	
	public List<User> findProviderUsers(String provider);
	
	public Optional<Provider> registerProvider(Provider provider) throws EntityExists;
	
	public void activateProvider(String providerId, boolean status);
	//TODO add , retrieval and associated files contents to providers via #{@link #GridFsService}


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
		return Optional.ofNullable(providerRepository.findAllProviders(criteria, bookingsByDates(criteria)));
	}

	@Override
	public Optional<Provider> findProviderById(String providerId) {
		Assert.notNull(providerId, "providerId must not be null");
		Provider provider = providerRepository.findOne(providerId);
	    provider.setThumnailPhoto(retriveProviderThumbnail(provider.getPid()));
		return Optional.ofNullable(provider);
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
	 * Returns a list of booked providers on that date
	 * @param criteria
	 * @return
	 */
	private Iterable<Booking> bookingsByDates(SearchCriteria criteria){
		final QBooking qbooking = QBooking.booking;
		BooleanBuilder bookingCriteria = new BooleanBuilder();
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
		bookingCriteria.and(criteria.getProviderType().equals(ProviderCategories.Venues.name()) ?
				qbooking.venueBooking.isTrue() : qbooking.venueBooking.isTrue());
		
		bookingCriteria.andNot(qbooking.status.currentState.in(BOOKINGSTATE.CONFIRMED,BOOKINGSTATE.FULFILLMENT));
		
		return bookingRepository.findAll(bookingCriteria.getValue());
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
		Date date1 = startDate != null ? DateConvertUtil.asUtilDate(startDate) : DateConvertUtil.asUtilDate(LocalDate.now().minusWeeks(1));
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
	

}