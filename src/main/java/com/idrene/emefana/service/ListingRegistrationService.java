/**
 * 
 */
package com.idrene.emefana.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.idrene.emefana.rest.resources.ListingResource;
import com.idrene.emefana.service.events.ListingCreatedEvent;

/**
 * @author iddymagohe
 * @since 1.0
 */
public interface ListingRegistrationService {
	public void registerListing(ListingResource listing);

}

@Component
class ListingRegistrationServiceImpl implements ListingRegistrationService{
	
	private final ApplicationEventPublisher publisher;
	
	@Autowired
	private EmefanaService service;
	
	
    @Autowired
	public ListingRegistrationServiceImpl(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}



	@Override
	public void registerListing(ListingResource listing) {
		//TODO 1 . Convert ListingResource to Provider and persist to db
		//TODO 2 . Convert Persist provider-user and listing-photo  in one goal
		//TODO 3 . Publish CreatedEvent<ListingResource>
		publisher.publishEvent(new ListingCreatedEvent(listing));
		System.out.println("Event above...");
	}
	
	
	
}
