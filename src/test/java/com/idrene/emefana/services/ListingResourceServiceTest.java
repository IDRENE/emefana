/**
 * 
 */
package com.idrene.emefana.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.idrene.emefana.AbstractIntegrationTest;
import com.idrene.emefana.service.ListingRegistrationService;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ListingResourceServiceTest extends AbstractIntegrationTest{
	
	@Autowired
	private ListingRegistrationService listingService;
	
	@Test
	public void testFiringOneEvent(){
		//listingService.registerListing(null);
	}

}
