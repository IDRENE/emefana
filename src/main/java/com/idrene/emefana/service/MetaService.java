/**
 * 
 */
package com.idrene.emefana.service;

import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idrene.emefana.domain.City;
import com.idrene.emefana.domain.EventType;
import com.idrene.emefana.domain.LabelValue;
import com.idrene.emefana.domain.MetaResource;
import com.idrene.emefana.domain.ProviderType;
import com.idrene.emefana.domain.ServiceOffering;
import com.idrene.emefana.repositories.CityRepository;
import com.idrene.emefana.repositories.EventTypeRepository;
import com.idrene.emefana.repositories.ProviderTypeRepository;
import com.idrene.emefana.repositories.ServiceOfferingRepository;

/**
 * @author iddymagohe
 * @since 1.0
 */
public interface MetaService {
	
	/**
	 * @return meta-data for client
	 */
	public List<MetaResource<?>>retrieveMeta();

}

@Service
class MetaServiceImpl implements MetaService{
	
	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private EventTypeRepository eventRepository;
	
	@Autowired
	private ServiceOfferingRepository serviceOfferingRepository;
	
	@Autowired
	private ProviderTypeRepository providerCategoryRepository;
	
	public enum METATYPES{
		PROVIDER_REP_ROLES ,
		PROVIDER_CATEGORIES,
		CITIES,
		COUNTRIES,
		EVENTS,
		SERVICES
	}

	@Override
	public List<MetaResource<?>> retrieveMeta() {
		List<MetaResource<?>> metaResources = new ArrayList<>();
		
		/*
		 * Listing representative role
		 * {value : 'MANAGER',label : 'Manager'}, 
		 * {value : 'OWNER',label : 'Owner'}, 
		 * {value : 'MARKETING_AGENCY',label : 'Marketing Agency'}, 
		 * {value : 'OTHER',label : 'Other'}
		 */
		List<LabelValue> providerRepRoles = Arrays.asList(
				new LabelValue(	"Manager", "MANAGER"), 
				new LabelValue("Owner", "OWNER"),
				new LabelValue("Marketing Agency", "MARKETING_AGENCY"),
				new LabelValue("OTHER", "Other")
				);
		
		metaResources.add(new MetaResource<LabelValue>(METATYPES.PROVIDER_REP_ROLES.name(),providerRepRoles));
		
		/*
		 * Cities
		 * lsof -n -i4TCP:$PORT | grep LISTEN
		 */
		
		//Comparator<City> cityCompare = Comparator.comparing(City::getCid);
		List<City> cities = cityRepository.findAll();
		metaResources.add(new MetaResource<City>(METATYPES.CITIES.name(),cities.stream().sorted(comparing(City::getCid)).collect(toList())));
		
		/*
		 * Countries
		 */
		List<String> countries = Arrays.asList("Tanzania");
		metaResources.add(new MetaResource<String>(METATYPES.COUNTRIES.name(),countries));
		
		/*
		 *ServiceOffering
		 */
		List<ServiceOffering> services = serviceOfferingRepository.findAll();
		metaResources.add(new MetaResource<ServiceOffering>(METATYPES.SERVICES.name(),services.stream().sorted(comparing(ServiceOffering::getSid)).collect(toList())));
		
		/*
		 * Event types
		 */
		List<EventType> events = eventRepository.findAll();
		metaResources.add(new MetaResource<EventType>(METATYPES.EVENTS.name(),events.stream().sorted(comparing(EventType::getEid)).collect(toList())));
		
		/*
		 * Provider categories
		 */
		List<ProviderType> providerCategories = providerCategoryRepository.findAll();
		metaResources.add(new MetaResource<ProviderType>(METATYPES.PROVIDER_CATEGORIES.name(),providerCategories.stream().sorted(comparing(ProviderType::getType)).collect(toList())));
		
		return metaResources;
	}
}
