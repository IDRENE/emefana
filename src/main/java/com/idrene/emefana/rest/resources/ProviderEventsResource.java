/**
 * 
 */
package com.idrene.emefana.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idrene.emefana.domain.ProviderEvents;

/**
 * @author iddymagohe
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderEventsResource {

	public String eventId;
	public String description;
	
	public ProviderEventsResource(ProviderEvents pevent, ResourceView view){
		eventId = pevent.getEvent().getEid();
		description = !ResourceUtil.isSummaryView(view) ? pevent.getDescription():null;
	}
}
