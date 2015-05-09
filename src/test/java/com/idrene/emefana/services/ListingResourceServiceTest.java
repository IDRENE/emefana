/**
 * 
 */
package com.idrene.emefana.services;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.idrene.emefana.AbstractIntegrationTest;
import com.idrene.emefana.rest.resources.ListingResource;
import com.idrene.emefana.service.ListingRegistrationService;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ListingResourceServiceTest extends AbstractIntegrationTest{
	
	@Autowired
	private ListingRegistrationService listingService;
	
	@Test
	public void listingRegistrationTest() throws JsonParseException, JsonMappingException, IOException{
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ListingResource providerFromJson = mapper.readValue(lisingResource.getFile(), ListingResource.class);
		assertNotNull(providerFromJson);
		listingService.registerListing(providerFromJson);
	}

}
