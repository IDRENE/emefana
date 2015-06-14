/**
 * 
 */
package com.idrene.emefana.rest.converters.response;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.geo.GeoResult;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.rest.controllers.BookingResourceController;
import com.idrene.emefana.rest.controllers.ListingResourceController;
import com.idrene.emefana.rest.resources.ProviderResource;
import com.idrene.emefana.rest.resources.ResourceUtil;
import com.idrene.emefana.rest.resources.ResourceView;

/**
 * @author iddymagohe
 * @since 1.0
 */

/*
 * public provider search result builder
 */
public class SearchProviderResourceAssembler extends ResourceAssemblerSupport<GeoResult<Provider>,ProviderResource>{
	
	private ResourceView view;
	final ProviderResourceAssembler providerAssembler;

	public SearchProviderResourceAssembler(ResourceView view) {
		super(ListingResourceController.class, ProviderResource.class);
		this.view = view;
		providerAssembler = new ProviderResourceAssembler(this.view);
	}

	@Override
	public ProviderResource toResource(GeoResult<Provider> entity) {
		ProviderResource resource = providerAssembler.toResource(entity.getContent());
		resource.distance = entity.getDistance();
		
		if(!ResourceUtil.isSummaryView(view)){
			resource.gallaryPhotos = entity.getContent().getGallaryPhotos();
		}
		
		// recreate only links allowed to public facing
		if (resource.hasLinks()) resource.removeLinks();
		 resource.add(buildProviderLinks(entity.getContent()));
		 
		return resource;
	}
	
	private List<Link> buildProviderLinks(Provider provider){
		List<Link> links = new ArrayList<>();
		Link selfLink = linkTo(methodOn(ListingResourceController.class).searchProvider(provider.getPid())).withSelfRel();
		links.add(selfLink);
		//Detailed resource view
				if(!ResourceUtil.isSummaryView(view)){
					Link bookingsLink = linkTo(methodOn(BookingResourceController.class).retrieveProviderBookings(provider.getPid(), BOOKINGSTATE.CONFIRMED.name())).withRel("bookings");
					links.add(bookingsLink);
				}
		
		return links;
	}

	

}
