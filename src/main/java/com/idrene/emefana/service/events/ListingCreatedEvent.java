/**
 * 
 */
package com.idrene.emefana.service.events;

import com.idrene.emefana.rest.resources.ListingResource;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ListingCreatedEvent implements CreationEvent<ListingResource>{
	
	private final ListingResource listingResource;

	/**
	 * @param listingResource
	 */
	public ListingCreatedEvent(ListingResource listingResource) {
		this.listingResource = listingResource;
	}



	@Override
	public ListingResource get() {
		return listingResource;
	}

}
