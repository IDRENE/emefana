/**
 * 
 */
package com.idrene.emefana.service.events;

import lombok.Getter;

import com.idrene.emefana.rest.resources.ListingResource;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ListingCreatedEvent implements CreationEvent<ListingResource>{
	
	private final ListingResource listingResource;
	
	@Getter private final boolean anExistingListing;

	/**
	 * @param listingResource
	 */
	public ListingCreatedEvent(ListingResource listingResource) {
		this.listingResource = listingResource;
		this.anExistingListing = false;
	}
	
	/**
	 * @param listingResource
	 */
	public ListingCreatedEvent(ListingResource listingResource, boolean listingExists) {
		this.listingResource = listingResource;
		this.anExistingListing = listingExists;
	}



	@Override
	public ListingResource get() {
		return listingResource;
	}

}
