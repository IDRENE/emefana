/**
 * 
 */
package com.idrene.emefana.services;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.jgroups.tests.bla;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.geo.GeoResults;
import org.springframework.util.CollectionUtils;

import com.idrene.emefana.AbstractIntegrationTest;
import com.idrene.emefana.domain.Booking;
import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.domain.ProviderCategories;
import com.idrene.emefana.domain.SearchCriteria;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.domain.VenuesDetail;
import com.idrene.emefana.repositories.BookingRepository;
import com.idrene.emefana.repositories.PersonRepository;
import com.idrene.emefana.repositories.ProviderRepository;
import com.idrene.emefana.repositories.ProviderTypeRepository;
import com.idrene.emefana.service.EmefanaService;
import com.idrene.emefana.service.EntityExists;
import com.idrene.emefana.util.DateConvertUtil;
import com.idrene.emefana.util.UtilityBean;


/**
 * @author iddymagohe
 *
 */
public class EmafanaServiceTest extends AbstractIntegrationTest{
	@Autowired
	UtilityBean utilBean;
	
	@Autowired
	EmefanaService service;
	
	@Autowired
	PersonRepository userRepository;
	
	@Autowired
	private ProviderRepository providerRepository;
	
	@Autowired
	private ProviderTypeRepository providerTypeRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	
	
	//@Test
	public void encodeDecodeTest(){
		assertEquals("Winter11!",utilBean.decodePropertyValue(utilBean.encodePropertyValue("Winter11!")));
	}
	
	@Test(expected = EntityExists.class)
	public void saveUserTest() throws EntityExists{
		userRepository.delete("testUser");
		User usr = new User("testUser","testUser@emefana.com","test_user_pss");
		service.registerUser(usr).get();
		User usr2 = new User("rtretetet","testUser@emefana.com","test_user_pss2");
		service.registerUser(usr2);
		User usr3 = new User("testUser","testUser3@emefana.com","test_user_pss3");
		service.registerUser(usr3);
		
	}
	
	//@Test
	public void retrieveBookingsByUserTest(){
		Optional<List<Booking>> bookings= service.retriveUserBookings(service.findUser(new User("IDRENE",null,null)).get());
	    bookings.ifPresent(booking -> booking.forEach(System.out::println));
		assertTrue(bookings.isPresent());
	}
	
	//@Test //569c2dc5-ac3d-4c30-8901-70d115047b79
	public void retrieveBookingsByProviderTest(){
		User usr = new User();
		usr.setAssociatedProvider("569c2dc5-ac3d-4c30-8901-70d115047b79");
		Optional<List<Booking>> bookings= service.retriveUserBookings(usr);
	    bookings.ifPresent(booking -> booking.forEach(System.out::println));
		assertTrue(bookings.isPresent());
	}
	
	@Test //569c2dc5-ac3d-4c30-8901-70d115047b79
	public void SearchProvidersTest() {
		SearchCriteria criteria = new SearchCriteria();
		criteria.setNearLocation(new double[] {-6.769280, 39.229809 });
		criteria.setCity("Dar es Salaam");
		criteria.setProviderType(ProviderCategories.Venues.name());
		criteria.setMaxDistance(300);
		//criteria.setPriceFrom(100000.0);
		//criteria.setPriceTo(2000000.0);
		criteria.setCapacityFrom(200);
		criteria.setProviderType(ProviderCategories.Venues.name());
		Optional<GeoResults<Provider>> geoResults = service
				.searchProvidersByCriteria(criteria);
		assertNotNull(geoResults);
		geoResults.get().forEach(
				gp -> System.out.println(gp.getContent().getName() + " - "
						+ gp.getDistance() + " - "
						+ gp.getContent().getCapacity() + " - "
						+ gp.getContent().getPrice().getPrice() + " - "
						+ gp.getContent().getPid() + " - "
						+ gp.getContent().getCategories().toString()
						));
		assertTrue(geoResults.get().getAverageDistance().getValue() > 0);
	}
	
	@Test
	public void findProviderTest(){
		Optional<Page<Provider>> oproviders = service.findProvider(true, null, null);
		assertTrue(oproviders.isPresent());
		Page<Provider> providers = oproviders.get();
		assertNotNull(providers);
		assertTrue(providers.getContent().size() > 0);
		/*providers.forEach(p -> {
			System.out.println(p.getName()  + "  " + p.getRegistrationDate() + " " + p.getCode() );
		});*/
	}
	
	@Ignore
	public void updateProviderCategories(){
		List<Provider> prvs = providerRepository.findAll();
		updateProvidersLambda(prvs,
				p -> CollectionUtils.isEmpty(p.getCategories()),
				p -> p.getCategories().add(providerTypeRepository.findOne(ProviderCategories.Venues.name())));
	}
	
	
	@Ignore
	public void updateProviderVenueDetails(){
		List<Provider> prvs = providerRepository.findAll();
		Random random = new Random();
		IntStream intStream = random.ints(200, 500);

		List<Integer> randomBetweenCapacity = intStream.limit(10).boxed().collect(toList());
		List<Double> randomPrice = Arrays.asList(500000.0,800000.0,100000.0,600000.0);
		updateProvidersLambda(prvs,
				p -> CollectionUtils.isEmpty(p.getVenuesDetails()),
				p ->{ 
				for (int i = 0; i <= 3; i++) {
					p.getVenuesDetails().add(
							new VenuesDetail(RandomStringUtils.random(3, 'A',
									'B', 'C', 'D', 'E'), randomBetweenCapacity
									.get(i),randomPrice.get(i), null));
				}
				});
	}
	
	
	/**
	 * TODO move this to main service layer
	 * @param providers
	 * @param predicate
	 * @param setValues
	 */
	void updateProvidersLambda(List<Provider> providers, Predicate<Provider> predicate, Consumer<Provider> setValues){
		assertNotNull(providers);
		assertTrue(providers.size() > 1);
		if (providers.stream().anyMatch(p -> predicate.test(p))) {
			providers.forEach(p -> setValues.accept(p));
			List<Provider> savedPvrs = providerRepository.save(providers);
			assertFalse(savedPvrs.stream().allMatch(predicate));
		}
	}
	
	@Ignore
	public void activateAll(){
		List<Provider> prvs = providerRepository.findAll();
		updateProvidersLambda(prvs,p -> !p.isActivated() ,p -> p.setActivated(true));
	}
	
	void updateBookingsLambda(List<Booking> bookings, Predicate<Booking> predicate, Consumer<Booking> setValues){
		assertNotNull(bookings);
		assertTrue(bookings.size() > 1);
		bookings.stream().filter(predicate).forEach(b -> {
			setValues.accept(b);
			bookingRepository.save(b);
		});
	}
	
	@Ignore
	public void updateToAllProviderBookingsToDone(){
		List<Booking> bookings = bookingRepository.findAll();
		updateBookingsLambda(bookings,
				b -> b.getStatus().getCurrentState() == BOOKINGSTATE.DONE,
				b -> b.getStatus().setCurrentState(BOOKINGSTATE.CONFIRMED));
		
	}
	
	@Ignore
	public void updateToOneProviderBookingsToDone(){
		List<Booking> bookings = bookingRepository.findAll();
		Booking b = bookings.stream().findAny().get();
		b.getStatus().setCurrentState(BOOKINGSTATE.DONE);;
		bookingRepository.save(b);
		
		
	}
	
	
	//@Test(expected = EntityExists.class )
	public void bookProviderTest() throws EntityExists{
		bookingRepository.deleteAll();
		SearchCriteria bookingCriteria = new SearchCriteria();
		Date fromDate = DateConvertUtil.asUtilDate(LocalDateTime.now());
		Date toDate = DateConvertUtil.asUtilDate(LocalDateTime.now().plusDays(1));
		bookingCriteria.setFromDate(fromDate);
		bookingCriteria.setToDate(toDate);
		bookingCriteria.setAssociatedProvider("569c2dc5-ac3d-4c30-8901-70d115047b79");
		Provider  prov = providerRepository.findOne("569c2dc5-ac3d-4c30-8901-70d115047b79");
		bookingCriteria.setProviderType(ProviderCategories.Venues.name());
		bookingCriteria.setVenue(prov.getVenuesDetails().stream().findAny().get());
		bookingCriteria.setCustomer(userRepository.findAll().get(0));
		bookingCriteria.setEvent(prov.getEvents().stream().findAny().get().getEvent());
		service.bookProvider(bookingCriteria).ifPresent(b -> { 
			assertNotNull(b);
			assertNotNull(b.getBid());
			assertNotNull(b.getProvider());
			assertNotNull(b.getStatus());
			assertNotNull(b.getVenueDetail());
		});
		
		
		service.bookProvider(bookingCriteria);
	}
	
	@Ignore
	public void bookAllVenuesForProvider(){
		Provider prov = providerRepository.findOne("569c2dc5-ac3d-4c30-8901-70d115047b79");
		prov.getVenuesDetails().forEach(v -> {
			if (!v.getName().equals("DAA")) {
				SearchCriteria bookingCriteria = new SearchCriteria();
				Date fromDate = DateConvertUtil.asUtilDate(LocalDateTime.now());
				Date toDate = DateConvertUtil.asUtilDate(LocalDateTime.now().plusDays(1));
				bookingCriteria.setFromDate(fromDate);
				bookingCriteria.setToDate(toDate);
				bookingCriteria.setAssociatedProvider(prov.getPid());
				bookingCriteria.setProviderType(ProviderCategories.Venues.name());
				bookingCriteria.setVenue(v);
				bookingCriteria.setCustomer(userRepository.findAll().get(0));
				bookingCriteria.setEvent(prov.getEvents().stream().findAny().get().getEvent());
				try {
					service.bookProvider(bookingCriteria);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	@Test
	public void findBoookingByProvider(){
		List<Booking> bookings = service.retrieveProviderBookingByStatus("569c2dc5-ac3d-4c30-8901-70d115047b79", BOOKINGSTATE.CONFIRMED);
		assertNotNull(bookings);
		assertFalse(bookings.isEmpty());
	}

}
