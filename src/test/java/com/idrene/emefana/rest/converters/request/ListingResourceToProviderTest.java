/**
 * 
 */
package com.idrene.emefana.rest.converters.request;

import static org.junit.Assert.*;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.idrene.emefana.AbstractIntegrationTest;
import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.rest.resources.ListingResource;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ListingResourceToProviderTest extends AbstractIntegrationTest{
	
	@Autowired 
	private ConversionService converter;
	
	@Test
	public void listingResourceToProviderTest() throws JsonParseException, JsonMappingException, IOException{
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ListingResource providerFromJson = mapper.readValue(lisingResource.getFile(), ListingResource.class);
		assertNotNull(providerFromJson);
		Provider provider = converter.convert(providerFromJson, Provider.class);
		assertNotNull(provider);
		assertTrue(provider.getEvents().size() > 0);
		assertTrue(provider.getVenuesDetails().size() > 0);
		assertTrue(provider.getContacts().size() > 0);
		assertTrue(provider.getFeatures().size() > 0);
		assertTrue(provider.getServices().size() > 0);
		assertNotNull(provider.getProviderUser());
		System.out.println(provider.getAddress().getCity());
		
		//provider.getServices().stream().forEach(s -> System.out.println(s.getService().getSid() + " - " + s.getDescription() + " " + s.getPrice().getCurrency() + " " + s.getPrice().getPrice()));
		
	}

}
