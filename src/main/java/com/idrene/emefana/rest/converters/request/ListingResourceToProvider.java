/**
 * 
 */
package com.idrene.emefana.rest.converters.request;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import com.idrene.emefana.domain.Address;
import com.idrene.emefana.domain.City;
import com.idrene.emefana.domain.Contact;
import com.idrene.emefana.domain.Contact.ContactTypeEnum;
import com.idrene.emefana.domain.Feature;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.domain.ProviderEvents;
import com.idrene.emefana.domain.ProviderService;
import com.idrene.emefana.domain.ProviderType;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.domain.VenuesDetail;
import com.idrene.emefana.rest.resources.ListingResource;
import com.idrene.emefana.security.EMEFANA_ROLES;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ListingResourceToProvider implements Converter<ListingResource, Provider> {
	/**
	 * 
	 */

	@Override
	public Provider convert(ListingResource resource) {
		Provider provider = new Provider();
		provider.setName(resource.getName());
		provider.setDescription(resource.getDescription());
		provider.getCategories().add(new ProviderType(resource.getCategory().getType()));//TODO this could be Array from resource
		provider.setAddress(extractAddress(resource));
		provider.setLocation(extractLocation(resource,provider.getAddress().getCity().getLocation()));
		provider.setProviderUser(extractProviderUser(resource));
		provider.setContacts(extractContacts(resource));
		provider.setServices(extarctListItems(resource.getServices(),offering -> new ProviderService(offering,"")));
		provider.setFeatures(extarctListItems(resource.getFeatures(),feature -> new Feature(feature.getName(),feature.getDescription())));
		provider.setEvents(new HashSet<>(extarctListItems(resource.getEvents(), event -> new ProviderEvents(event, event.getDescription()))));
		
		if (resource.getCategory().getType().equalsIgnoreCase("Venues")) {
			provider.setVenuesDetails(new HashSet<>(extarctListItems(
					resource.getVenues(),
					venue -> new VenuesDetail(venue.getName(), venue
							.getCapacity(), new Double(venue.getPrice()), venue
							.getCurrency()))));
		}
		
		return provider;
	}
	
	/**
	 * @param resource
	 * @return
	 */
	private Address extractAddress(ListingResource resource) {
		double[] location = new double[2];
		location[0] = resource.getCity().getLocation().get(0);
		location[1] = resource.getCity().getLocation().get(1);
		City city = new City();
		city.setCid(resource.getCity().getCid());
		city.setLocation(location);
		Address address = new Address(resource.getStreetaddress(), city);
		address.setStreetLine2(resource.getAdditionalstreetaddress());
		return address;

	}
	
	/**
	 * @param resource
	 * @param defaultLocation
	 * @return
	 */
	private double[] extractLocation(ListingResource resource, double[] defaultLocation){
		double[] location = new double[2];
		if(resource.getUselocation()){
			location[0] = resource.getLocation().getLatitude();
			location[1] = resource.getLocation().getLongitude();
		}else{
			location = defaultLocation;
		}
		return location;
	}
	
	
	/**
	 * TODO To be persisted after setting provider(providerId)
	 * @param resource
	 * @return
	 */
	private User extractProviderUser(ListingResource resource){
		User usr = new User();
		usr.setEmailAddress(resource.getUser().getEmailaddress());
		usr.setId(resource.getUser().getEmailaddress());
		usr.setFirstName(resource.getUser().getFirstname());
		usr.setLastName(resource.getUser().getLastname());
		usr.setListingRole(resource.getUser().getRole().getValue());
		usr.getRoles().addAll(Arrays.asList(EMEFANA_ROLES.PROVIDER,EMEFANA_ROLES.PROVIDER_USER));
		return usr;
	}
	
	/**
	 * TO Extract List<ProviderService> service
	 * TO Extract List<Contact> contacts 
	 * TO Extract List<Feature> features
	 * TO Extract Set<ProviderEvents> events
	 * TO Extract Set<VenuesDetail> venuesDetails
	 */
	private <T,R> List<R> extarctListItems(List<T> items , Function<T,R> function) {
		return items.stream()
				.map(t -> function.apply(t))
				.collect(toList());
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	private List<Contact> extractContacts(ListingResource resource) {
		List<Contact> contacts = new ArrayList<>();
		
		Contact phone = new Contact(ContactTypeEnum.Mobile.name(),resource.getPhonenumber(), "");
		contacts.add(phone);

		if (StringUtils.hasText(resource.getEmailaddress())) {
			contacts.add(new Contact(ContactTypeEnum.Email.name(), resource.getEmailaddress(), ""));
		}

		if (StringUtils.hasText(resource.getWebsite())) {
			contacts.add(new Contact(ContactTypeEnum.Website.name(), resource.getWebsite(), ""));
		}

		if (StringUtils.hasText(resource.getFacebook())) {
			contacts.add(new Contact(ContactTypeEnum.Facebook.name(), resource.getFacebook(), ""));
		}

		return contacts;
	}
	
	/**
	 * TODO extract photo
	 */

}
