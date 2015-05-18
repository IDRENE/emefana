/**
 * 
 */
package com.idrene.emefana.rest.converters.response;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.rest.controllers.ListingResourceController;
import com.idrene.emefana.rest.resources.ProviderEventsResource;
import com.idrene.emefana.rest.resources.ProviderResource;
import com.idrene.emefana.rest.resources.ProviderServiceResource;
import com.idrene.emefana.util.DateConvertUtil;

/**
 * @author iddymagohe
 * @since 1.0
 */


public class ProviderResourceAssembler extends ResourceAssemblerSupport<Provider, ProviderResource>{
	
	//private ResourceView view;

	public ProviderResourceAssembler() {
		super(ListingResourceController.class, ProviderResource.class);
	}

	@Override
	public ProviderResource toResource(Provider entity) {
		ProviderResource resource = new ProviderResource();
		resource.providerId = entity.getPid();
		resource.providerName = entity.getName();
		resource.providerActive = entity.isActivated();
		resource.providerAddress = entity.getAddress();
		resource.providerLocation = entity.getLocation();
		resource.businessDescription = entity.getDescription();
		resource.providerCategories = entity.getCategories();
		resource.registereDate = DateConvertUtil.asLocalDate(entity.getRegistrationDate());
		resource.providerEvents = entity.getEvents().stream().map(ProviderEventsResource::new).collect(toList());
		resource.providerServices =entity.getServices().stream().map(ProviderServiceResource::new).collect(toList());
		resource.providerFeatures = entity.getFeatures();
		resource.providerVenues = entity.getVenuesDetails();
		resource.thumnailPhoto = entity.getThumnailPhoto();
		
		resource.add(buildProviderLinks(entity));
		
		return resource;
	}
	
	private List<Link> buildProviderLinks(Provider provider){
		List<Link> links = new ArrayList<>();
		Link selfLink = linkTo(methodOn(ListingResourceController.class).retrieveProvider(provider.getPid())).withSelfRel();
		links.add(selfLink);
		
		Link usersLink = linkTo(methodOn(ListingResourceController.class).providerUsers(provider.getPid())).withRel("users");
		links.add(usersLink);
		return links;
		
	}

}