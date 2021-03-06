/**
 * 
 */
package com.idrene.emefana.repositories;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.util.Assert;

import com.idrene.emefana.domain.Booking;
import com.idrene.emefana.domain.City;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.domain.ProviderCategories;
import com.idrene.emefana.domain.SearchCriteria;
import com.idrene.emefana.domain.VenuesDetail;

/**
 * @author iddymagohe
 *
 */
public interface ProviderRepositoryCustom {
	
	/**
	 * @param criteria
	 * @param providers booked providers on #SearchCriteria.fromDate and #SearchCriteria.toDate
	 * @return
	 */
	public GeoResults<Provider> findAllProviders(SearchCriteria criteria, List<Booking> bookings);
	
	/**
	 * Mainly Search provider term  in #Provider[name, businessDescription]
	 * @param SearchingTerm TODO
	 * @return
	 */
	public GeoResults<Provider> findAllProviders(String SearchingTerm);
}


class ProviderRepositoryImpl implements ProviderRepositoryCustom{
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Autowired
	private CityRepository cityRepository;

	@Override
	public GeoResults<Provider> findAllProviders(SearchCriteria searchCriteria,List<Booking> bookingList) {
		/*
		 * Due to limitations of the com.mongodb.BasicDBObject, you can't add a second 
		 * 'field' expression specified as 'capacity : { "$lte" : 0}'. 
		 * Criteria already contains 'field : { "$gte" : 210}'.
		 * 
		 * TODO Use Stream API to filter by fields that did not participate to create a query
		 * TODO Mark  booked for available venues
		 * 
		 */
		
		Map<String, Set<VenuesDetail>> bookedVenuebyprovider = bookingList.stream()
				.collect(groupingBy( booking -> booking.getProvider().getPid(), mapping((Booking b) -> b.getVenueDetail(), toSet())));
		
		Optional<Set<String>> providers = searchCriteria.getProviderType().equals(ProviderCategories.Venues.name()) ?
				Optional.empty() : Optional.ofNullable(bookedVenuebyprovider.keySet()); 
				
		Criteria criteria = Criteria.where("activated").is(true); //Only active providers
		
		providers.ifPresent(prvs -> criteria.and("pid").nin(prvs)); //don`t consider booked providers (non venues) on the date
		
		searchCriteria.getOCapacityFrom().ifPresent( // Minimum capacity
				capacity -> criteria.and("capacity").gte(capacity));
		
		searchCriteria.getOpriceFrom().ifPresent( // Minimum price
				minPrice -> criteria.and("price.price").gte(minPrice));

//		searchCriteria.getOCity().ifPresent(
//				city -> criteria.and("address.city.$id").is(city)); Only closest providers by distance
		
		
        //criteria.and("address.city.country").is(searchCriteria.getCountry());
		
		searchCriteria.getOProviderType().ifPresent( // provider type
				type -> criteria.and("categories.$id").is(type));
		/*
		 * Nearest point of interest, if not provided, 
		 * City location is used by default
		 */
		Point location = null;
		if(searchCriteria.getONearLocation().isPresent()){
			location = new Point(searchCriteria.getNearLocation()[1], searchCriteria.getNearLocation()[0]);
		}else{
			City city= cityRepository.findByCidIgnoreCase(searchCriteria.getCity());
			location = new Point(city.getLocation()[0], city.getLocation()[1]);
		}
		NearQuery near = NearQuery.near(location).maxDistance(
				new Distance(searchCriteria.getMaxDistance(),
						Metrics.KILOMETERS));
		near.query(new Query(criteria));
		
		//TODO set max result for pagination
		GeoResults<Provider> results = mongoOperations.geoNear(near,Provider.class);
		
		/*
		 * Filter-out full booked providers
		 */
		if (searchCriteria.getProviderType().equals(ProviderCategories.Venues.name()) && !bookedVenuebyprovider.isEmpty()) {
			List<GeoResult<Provider>> result = results.getContent().stream()
					 .filter(r ->{
						 int sizeBooked = bookedVenuebyprovider.get(r.getContent().getPid()) != null ?
								 bookedVenuebyprovider.get(r.getContent().getPid()).size() : 0;
						 return r.getContent().getVenuesDetails().size() > sizeBooked ;
					 }).collect(toList());
			return new GeoResults<Provider>(result,Metrics.KILOMETERS);
		}
		
		//TODO Filter by Country here
		
		return results;
	}

	@Override
	public GeoResults<Provider> findAllProviders(String searchingTerm) {
		Assert.hasText(searchingTerm, "Search term can not be null");
		TextCriteria criteria = TextCriteria.forDefaultLanguage().matching(searchingTerm);
		Query query = TextQuery.queryText(criteria).sortByScore();
		
		List<Provider> providers = mongoOperations.find(query, Provider.class);
		List<GeoResult<Provider>> result = providers.stream().filter(Provider::isActivated).map(p -> new GeoResult<Provider>(p,new Distance(0))).collect(toList());
		return  new GeoResults<Provider>(result,Metrics.KILOMETERS);
	}


}