package com.idrene.emefana.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idrene.emefana.domain.Price;
import com.idrene.emefana.domain.ProviderService;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderServiceResource {
	
	public String serviceId;
	public String description;
	public Price price;
	
	public ProviderServiceResource(ProviderService pservice , ResourceView view) {
		serviceId = pservice.getService().getSid();
		description = !ResourceUtil.isSummaryView(view)? pservice.getDescription() : null;
		price = pservice.getPrice();
	}
}
