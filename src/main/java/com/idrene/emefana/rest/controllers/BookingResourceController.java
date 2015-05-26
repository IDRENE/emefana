/**
 * 
 */
package com.idrene.emefana.rest.controllers;

import java.net.URI;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiHeader;
import org.jsondoc.core.annotation.ApiHeaders;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParams;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.idrene.emefana.domain.SearchCriteria;
import com.idrene.emefana.rest.resources.ResponseStatus;
import com.idrene.emefana.service.EmefanaService;
import com.idrene.emefana.service.EntityExists;

/**
 * @author iddymagohe
 * @since 1.0
 */

@Api(description = "Provides a set APIs to interact with Bookings  ", name = "Booking Service")
@Controller
public class BookingResourceController {
	private final static  Logger logger = LoggerFactory.getLogger(BookingResourceController.class);
	@Autowired
	@Qualifier("searchCriteriaValidator")
	private Validator searchValidator;
	
	@Autowired
	private EmefanaService emefanaService;
	
	@ApiMethod(path="/api/providers/{provider-referenceId}/bookings", description="Used to book service(s) offered by a particular provider")
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@ApiParams(pathparams={@ApiPathParam(name = "provider-referenceId",  description ="provider reference identifier ")})
	@ApiBodyObject(clazz=SearchCriteria.class)
	@ApiResponseObject(clazz=ResponseStatus.class)
	@RequestMapping(value="/api/providers/{provider-referenceId}/bookings", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> bookProvider(@PathVariable("provider-referenceId") String providerId, @RequestBody SearchCriteria bookingCriteria ,BindingResult result) {
		
		ResponseEntity<ResponseStatus> response = null;
		bookingCriteria.setAssociatedProvider(providerId);
		searchValidator.validate(bookingCriteria, result);
		if (result.hasErrors()) {
			ResponseStatus body = new ResponseStatus(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase());
			result.getAllErrors().forEach(e -> body.addMessage(e.getDefaultMessage()));
			response = ResponseEntity.badRequest().body(body);
		}else{
			try {
				emefanaService.bookProvider(bookingCriteria);
				response = ResponseEntity.created(new URI("http://api/provider/bookis")).body(new ResponseStatus(HttpStatus.CREATED.value(),HttpStatus.CREATED.getReasonPhrase()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				HttpStatus status = e instanceof EntityExists ? HttpStatus.CONFLICT : HttpStatus.INTERNAL_SERVER_ERROR;
			response = ResponseEntity.status(status).body(new ResponseStatus(status.value(), status.getReasonPhrase(),e.getMessage()));
			}
		}
		
		return response;
	}
	
	@ApiMethod(path="/api/providers/{provider-referenceId}/bookings/{booking-referenceId}", description="Retrieve  a booking details  to a particular provider")
	@ApiParams(pathparams={@ApiPathParam(name = "provider-referenceId",  description ="provider reference identifier "),
			@ApiPathParam(name = "booking-referenceId",  description ="booking reference identifier ")})
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@RequestMapping(value="/api/providers/{provider-referenceId}/bookings/{booking-referenceId}", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?>retrieveProviderBooking(@PathVariable("provider-referenceId") String providerId, @PathVariable String bookingId){
		return null;
	}
	
	@ApiMethod(path="/api/providers/{provider-referenceId}/bookings", description="Retrieve  all bookings  to a particular provider")
	@ApiParams(pathparams={@ApiPathParam(name = "provider-referenceId",  description ="provider reference identifier ")},
	         queryparams={@ApiQueryParam(name="bookingState", defaultvalue="NEW", description = "Retrive bookings with in this particular state")})
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@RequestMapping(value="/api/providers/{provider-referenceId}/bookings", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?>retrieveProviderBookings(@PathVariable("provider-referenceId") String providerId , @RequestParam(required =false, defaultValue="NEW") String bookingState ){
		return null;
	}
	
	
	
	
}
