/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.hateoas.ResourceSupport;

import com.idrene.emefana.domain.BookedService;
import com.idrene.emefana.domain.BookingStatus;
import com.idrene.emefana.domain.VenuesDetail;

/**
 * @author iddymagohe
 * @since 1.0
 */
@ApiObject
public class BookingResource extends ResourceSupport {

	@ApiObjectField(required = true)
	public String bookingId;

	@ApiObjectField(required = true , description = "provider of the booked services") //TODO Link to Provider from here
	public String providerName;

	@ApiObjectField(required = true , description="Event start date and time / booking-date")
	public LocalDateTime bookingStartDate;

	@ApiObjectField(required = true , description= "Event end date and time / booking end-date and time")
	public LocalDateTime bookingEndDate;

	@ApiObjectField(required = true , description= "show the current state or status of a aprticular booking")
	public BookingStatus bookingStatus;

	@ApiObjectField(description = "Booked Venue, applicable to Venues only")
	public VenuesDetail venueDetail;

	@ApiObjectField(description = "A list of services to be randered by a provider to fulfilliment of a particular booking")
	public List<BookedService> bookedServices;

}
