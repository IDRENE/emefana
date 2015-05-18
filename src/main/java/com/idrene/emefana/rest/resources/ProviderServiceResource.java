package com.idrene.emefana.rest.resources;

import com.idrene.emefana.domain.Price;
import com.idrene.emefana.domain.ProviderService;

public class ProviderServiceResource {
	
	public String serviceId;
	public String description;
	public Price price;
	
	public ProviderServiceResource(ProviderService pservice) {
		serviceId = pservice.getService().getSid();
		description = pservice.getDescription();
		price = pservice.getPrice();
	}
}
