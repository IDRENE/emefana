/**
 * 
 */
package com.idrene.emefana.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.rest.resources.ListingResource;
import com.idrene.emefana.service.events.ListingCreatedEvent;
import com.idrene.emefana.util.UtilityBean;
import com.mongodb.BasicDBObject;

/**
 * @author iddymagohe
 * @since 1.0
 */
public interface ListingRegistrationService {
	public void registerListing(ListingResource listing);

}

/**
 * @author iddymagohe
 * @since 1.0
 */
/**
 * @author iddymagohe
 * @since 1.0
 */
@Component
class ListingRegistrationServiceImpl implements ListingRegistrationService{
	
	private static final Logger logger = LoggerFactory.getLogger(ListingRegistrationServiceImpl.class);
	
	private final ApplicationEventPublisher publisher;
	
	@Autowired
	private EmefanaService service;
	
	@Autowired
	private GridFsService imageService;
	
	@Autowired
	@Qualifier("conversionService")
	private ConversionService converter;
	
	
    @Autowired
	public ListingRegistrationServiceImpl(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	/* (non-Javadoc)
	 * @see com.idrene.emefana.service.ListingRegistrationService#registerListing(com.idrene.emefana.rest.resources.ListingResource)
	 * 1 . Convert ListingResource to Provider and persist to db
	 * 2 . Convert Persist provider-user and listing-photo  in one goal
	 * 3 . Publish CreatedEvent<ListingResource> or ListingExistEvent
	 */
	@Override
	public void registerListing(ListingResource listing) {
		
		Provider provider = converter.convert(listing, Provider.class);
		User providerUser = provider.getProviderUser();

		try {
			Optional<Provider> dbProvier = service.registerProvider(provider);
			if (dbProvier.isPresent()) {
				String providerId = dbProvier.get().getPid();
				logger.info("Registered Provider  : " + providerId + " and Fired event to send email");
				publisher.publishEvent(new ListingCreatedEvent(listing));
				
				// update user with providerId Persist
				providerUser.setAssociatedProvider(providerId);
				Optional<User> dbUser = service.registerListingContactPerson(providerUser);
				if (!dbUser.isPresent()) logger.warn("Failed to retrieve Provider User after saving  : "+ listing);
				
				// Store listing photo
				storeProviderThumbnailImage(providerId, listing);
			} else {
				logger.warn("Failed to retrieve Provider for listing after saving  : " + listing);
			}
		} catch (EntityExists e) {
			logger.info("Failed to Register listing , for the reason  : "+ e.getMessage());
			publisher.publishEvent(new ListingCreatedEvent(listing,true));
		}

		
	}
	
	private void storeProviderThumbnailImage(String providerId, ListingResource listing ){
		
		try {
			Optional<InputStream> inputStream = UtilityBean.Base64ToInputStream(Optional.ofNullable(listing.getPhoto().getBase64()));
			if (inputStream.isPresent()){
				Map<String, String> photoMeta = UtilityBean.photoMetadata(providerId, listing.getPhoto());
				imageService.storeFiles(inputStream.get(), photoMeta, BasicDBObject::new);
			}
			
		} catch (IOException e) {
			logger.error("Exception occered while saving provider thumbnail [ provider :" + providerId + " ]" , e.getCause());
		}
	}
	
	
	
	
	
}
