/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.data.geo.Distance;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idrene.emefana.domain.Address;
import com.idrene.emefana.domain.Feature;
import com.idrene.emefana.domain.FileMetadata;
import com.idrene.emefana.domain.ProviderType;
import com.idrene.emefana.domain.VenuesDetail;

/**
 * @author iddymagohe
 * @since 1.0
 */
@ApiObject
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderResource extends ResourceSupport {

	/**
	 * Code:Id
	 */
	@ApiObjectField(name = "providerId" , description="provider Identifier" , required=true)
	public String providerId;
	
	@ApiObjectField(name = "providerName" , description="provider name" , required=true)
	public String providerName;
	
	@ApiObjectField(name = "businessDescription" , description="provider description" , required=true)
	public String businessDescription;
	
	@ApiObjectField(name = "providerActive" , description="whether provider activated " , required=true)
	public boolean providerActive;
	
	@ApiObjectField(name = "registeredDate", description="provider registration date" , required=true)
	public LocalDate registeredDate;
	
	@ApiObjectField(name = "providerLocation" , description="lattitude and longtude")
	public double[] providerLocation;
	
	public PriceRangeResource priceRange;
	
	@ApiObjectField(name = "providerAddress", description="provider physical address" , required=true)
	public Address providerAddress;
	
	@ApiObjectField(name = "providerServices", description= "provider services offering")
	public List<ProviderServiceResource> providerServices;
	
	@ApiObjectField(name = "providerFeatures", description= "provider features")
	public List<Feature> providerFeatures;
	
	@ApiObjectField(name = "providerVenues", description= "provider Venues , only Venues providers category ")
	public Set<VenuesDetail> providerVenues;
	
	@ApiObjectField(name = "providerCategories", description= "provider in categories")
	public Set<ProviderType> providerCategories;
	
	@ApiObjectField(name = "providerEvents", description= "events types , served by a provider")
	public List<ProviderEventsResource> providerEvents;
	
	@ApiObjectField(name = "distance", description= "distance , to provider location")
	public Distance distance;
	
	@ApiObjectField(name = "thumnailPhoto", description= "events types , thumbnail photo")
	public FileMetadata thumnailPhoto;
	
	@ApiObjectField(name = "photo gallery", description= "provider's  , photos")
	public List<FileMetadata> gallaryPhotos;
}
