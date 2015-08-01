/**
 * 
 */
package com.idrene.emefana.rest.converters.response;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.rest.controllers.BookingResourceController;
import com.idrene.emefana.rest.controllers.ListingResourceController;
import com.idrene.emefana.rest.resources.PriceRangeResource;
import com.idrene.emefana.rest.resources.ProviderEventsResource;
import com.idrene.emefana.rest.resources.ProviderResource;
import com.idrene.emefana.rest.resources.ProviderServiceResource;
import com.idrene.emefana.rest.resources.ResourceUtil;
import com.idrene.emefana.rest.resources.ResourceUtil.STATUS;
import com.idrene.emefana.rest.resources.ResourceView;
import com.idrene.emefana.util.DateConvertUtil;

/**
 * @author iddymagohe
 * @since 1.0
 */


public class ProviderResourceAssembler extends ResourceAssemblerSupport<Provider, ProviderResource>{
	
	private ResourceView view;

	public ProviderResourceAssembler(ResourceView view) {
		super(ListingResourceController.class, ProviderResource.class);
		this.view = view;
	}

	@Override
	public ProviderResource toResource(Provider entity) {
		ProviderResource resource = new ProviderResource();
		resource.providerId = entity.getPid();
		resource.providerName = entity.getName();
		resource.providerActive = entity.isActivated();
		resource.providerAddress = entity.getAddress();
		resource.providerLocation = entity.getLocation();
		resource.businessDescription = entity.getDescription();
		resource.providerCategories = entity.getCategories();
		resource.registeredDate = DateConvertUtil.asLocalDate(entity.getRegistrationDate());
		resource.contacts = entity.getContacts();// TODO UI consume this
		
		resource.providerEvents = entity.getEvents().stream().map(e -> new ProviderEventsResource(e, view)).collect(toList());
		resource.providerServices =entity.getServices().stream().map(s -> new ProviderServiceResource(s, view)).collect(toList());
		resource.providerFeatures = entity.getFeatures();
		resource.providerVenues = entity.getVenuesDetails();
		resource.thumnailPhoto = entity.getThumnailPhoto();
		
		//Price Range
		DoubleSummaryStatistics ps = entity.getPriceStatistics();
		resource.priceRange = new PriceRangeResource(entity.getCurrency(), ps.getMin(), ps.getMax());
		
		resource.add(buildProviderLinks(entity));
		
		return resource;
	}
	
	private List<Link> buildProviderLinks(Provider provider){
		List<Link> links = new ArrayList<>();
		Link selfLink = linkTo(methodOn(ListingResourceController.class).retrieveProvider(provider.getPid())).withSelfRel();
		links.add(selfLink);
		
		Link usersLink = linkTo(methodOn(ListingResourceController.class).providerUsers(provider.getPid())).withRel("users");
		links.add(usersLink);
		
		//Detailed resource view
		if(!ResourceUtil.isSummaryView(view)){
			Link activationLink = provider.isActivated()?
					linkTo(methodOn(ListingResourceController.class).activateProvider(provider.getPid(), STATUS.deactivate.name())).withRel("deactivation") :
					linkTo(methodOn(ListingResourceController.class).activateProvider(provider.getPid(), STATUS.activate.name())).withRel("activation");
					
			Link bookingsLink = linkTo(methodOn(BookingResourceController.class).retrieveProviderBookings(provider.getPid(), BOOKINGSTATE.NEW.name())).withRel("bookings");
			links.add(activationLink);
			links.add(bookingsLink);
		}
		
		return links;
		
	}

}
