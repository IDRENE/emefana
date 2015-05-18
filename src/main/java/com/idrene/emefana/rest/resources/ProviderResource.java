/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.hateoas.ResourceSupport;

import com.idrene.emefana.domain.Address;
import com.idrene.emefana.domain.Feature;
import com.idrene.emefana.domain.FileMetadata;
import com.idrene.emefana.domain.ProviderType;
import com.idrene.emefana.domain.VenuesDetail;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ProviderResource extends ResourceSupport {

	/**
	 * Code:Id
	 */
	public String providerId;
	public String providerName;
	public String businessDescription;
	public boolean providerActive;
	public LocalDate registereDate;
	public double[] providerLocation;
	public Address providerAddress;
	public List<ProviderServiceResource> providerServices;
	public List<Feature> providerFeatures;
	public Set<VenuesDetail> providerVenues;
	public Set<ProviderType> providerCategories;
	public List<ProviderEventsResource> providerEvents;
	public FileMetadata thumnailPhoto;
}
