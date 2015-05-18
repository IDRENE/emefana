/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.io.Serializable;

import com.idrene.emefana.domain.ProviderEvents;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ProviderEventsResource {

	public String eventId;
	public String description;
	
	public ProviderEventsResource(ProviderEvents pevent){
		eventId = pevent.getEvent().getEid();
		description = pevent.getDescription();
	}
}
