/**
 * 
 */
package com.idrene.emefana.rest.converters.response;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.idrene.emefana.domain.Booking;
import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;
import com.idrene.emefana.rest.controllers.BookingResourceController;
import com.idrene.emefana.rest.controllers.ListingResourceController;
import com.idrene.emefana.rest.resources.BookingResource;
import com.idrene.emefana.rest.resources.ResourceUtil;
import com.idrene.emefana.rest.resources.ResourceView;
import com.idrene.emefana.util.DateConvertUtil;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class BookingResourceAssembler extends ResourceAssemblerSupport<Booking, BookingResource>{

	private final ResourceView view;
	private final boolean isProvider;
	
	
	public BookingResourceAssembler(ResourceView view, boolean isProvider) {
		super(BookingResourceController.class, BookingResource.class);
		this.view = view;
		this.isProvider = isProvider;
	}

	@Override
	public BookingResource toResource(Booking entity) {
		BookingResource resource = new BookingResource();
		resource.bookingId = entity.getBid();
		resource.bookingStartDate = DateConvertUtil.asLocalDateTime(entity.getStartDate());
		resource.bookingEndDate = DateConvertUtil.asLocalDateTime(entity.getEndDate());
		resource.providerName = entity.getProvider().getName();
		resource.bookingStatus = entity.getStatus();
		
		if(!ResourceUtil.isSummaryView(view)){
			resource.bookedServices = entity.getBookedServices();
		}
		resource.add(buildBookingLinks(entity));
		
		return resource;
	}
	
	private List<Link> buildBookingLinks(Booking entity){
		//TODO build manipulation links to Provider and User resources
		List<Link> links = new ArrayList<>();
		Link selfLink = isProvider ? 
				linkTo(methodOn(BookingResourceController.class).retrieveProviderBooking(entity.getProvider().getPid(), entity.getBid())).withSelfRel()
				: null; //TODO user link
				
				links.add(selfLink);
				
		Link providerLink = linkTo(methodOn(ListingResourceController.class).retrieveProvider(entity.getProvider().getPid())).withRel("provider");
		links.add(providerLink);
		
		BOOKINGSTATE state = entity.getStatus().getCurrentState();
		//TODO state links based on user and provider
				
		return links;
		
	}

}
