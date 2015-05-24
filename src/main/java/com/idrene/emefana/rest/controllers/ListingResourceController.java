/**
 * 
 */
package com.idrene.emefana.rest.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiHeader;
import org.jsondoc.core.annotation.ApiHeaders;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParams;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.jsondoc.core.pojo.ApiVerb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.idrene.emefana.domain.Provider;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.rest.converters.response.ProviderResourceAssembler;
import com.idrene.emefana.rest.converters.response.UserResourceAssembler;
import com.idrene.emefana.rest.resources.ListingResource;
import com.idrene.emefana.rest.resources.ProviderResource;
import com.idrene.emefana.rest.resources.ResourceUtil.STATUS;
import com.idrene.emefana.rest.resources.ResourceView;
import com.idrene.emefana.rest.resources.ResponseStatus;
import com.idrene.emefana.service.EmefanaService;
import com.idrene.emefana.service.ListingRegistrationService;

/**
 * @author iddymagohe
 * @since 1.0
 */
@Api(description = "Provides a set APIs to interact with providers/listings  ", name = "Provider Service")
@Controller
public class ListingResourceController {
	
	private final static  Logger logger = LoggerFactory.getLogger(ListingResourceController.class);

	@Autowired
	private ListingRegistrationService listingService;

	@Autowired
	private EmefanaService emefanaService;

	private final PagedResourcesAssembler<Provider> pagedAssembler = new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(), null);
	//private final 
	@ApiMethod(path="api/provider",verb=ApiVerb.POST, consumes={ MediaType.APPLICATION_JSON_VALUE}, produces={ MediaType.APPLICATION_JSON_VALUE},
			description= " Privider/Listing registration " , responsestatuscode ="201" )
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@ApiBodyObject(clazz=ListingResource.class)
	@ApiResponseObject(clazz = ResponseStatus.class)
	@ApiErrors(apierrors={@ApiError(code="400",description = "Bad request, correct the errors and retry"), 
			@ApiError(code="401",description = "Access Denied "), @ApiError(code="500",description = "Server error, try again later")})
	@RequestMapping(value = { "api/provider" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseStatus> registerListing(@RequestBody @Valid ListingResource listing, BindingResult result)throws URISyntaxException {
		ResponseEntity<ResponseStatus> response = null;
		if (result.hasErrors()) {
			ResponseStatus body = new ResponseStatus(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase());
			result.getAllErrors().forEach(e -> body.addMessage(e.getDefaultMessage()));
			response = ResponseEntity.badRequest().body(body);
		} else {
			try {
				listingService.registerListing(listing);
				response = ResponseEntity.created(new URI("http://api/provider")).body(new ResponseStatus(HttpStatus.CREATED.value(),
								HttpStatus.CREATED.getReasonPhrase()));
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR
								.getReasonPhrase()));
			}
		}

		return response;
	}
    
	@ApiMethod(path="api/providers",  produces={ MediaType.APPLICATION_JSON_VALUE},description= " List of providers ")
	@ApiParams(queryparams={@ApiQueryParam(name = "active",  description ="classification of providers to return ", required =true, allowedvalues={"0", "1", "true", "false"}, defaultvalue="0")})
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@RequestMapping(value = "api/providers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiErrors(apierrors={@ApiError(code="401",description = "Access Denied ")})
	public ResponseEntity<PagedResources<?>> retriveProviders(@RequestParam boolean active) {
		Optional<Page<Provider>> providers = emefanaService.findProvider(active, null, null);
		ProviderResourceAssembler providerAssembler = new ProviderResourceAssembler(ResourceView.SUMMARY);
		return ResponseEntity.ok(pagedAssembler.toResource(providers.get(),providerAssembler));
	}

	@ApiMethod(path="api/providers/{referenceId}",  produces={ MediaType.APPLICATION_JSON_VALUE},description= " Retrieve a provider/listing  by a referenceId ")
	@ApiParams(pathparams={@ApiPathParam(name = "referenceId",  description ="provider reference identifier ")})
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@ApiResponseObject(clazz = ProviderResource.class)
	@ApiErrors(apierrors={@ApiError(code="401",description = "Access Denied "), 
			@ApiError(code="404",description = "Resource not found"), @ApiError(code="500",description = "Server error, try again later")})
	@RequestMapping(value = "api/providers/{referenceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> retrieveProvider(@PathVariable String referenceId) {
		Optional<Provider> provider = emefanaService.findProviderById(referenceId);
		ProviderResourceAssembler providerAssembler = new ProviderResourceAssembler(ResourceView.DETAILS);
		return provider.isPresent() ? ResponseEntity.ok(providerAssembler.toResource(provider.get())) 
				: ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseStatus(HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND.getReasonPhrase()));
	}

	
	@ApiMethod(path="api/providers/{referenceId}/users",  produces={ MediaType.APPLICATION_JSON_VALUE},description= " Retrieve a provider/listing  users by a referenceId ")
	@ApiParams(pathparams={@ApiPathParam(name = "referenceId",  description ="provider reference identifier ")})
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@ApiErrors(apierrors={@ApiError(code="500",description = "Server error, try again later")})
	@RequestMapping(value = "api/providers/{referenceId}/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> providerUsers(@PathVariable String referenceId) {
		UserResourceAssembler assembler = new UserResourceAssembler(ResourceView.SUMMARY);
		List<User> providerUsers = emefanaService.findProviderUsers(referenceId);
		return ResponseEntity.ok(assembler.toResources(providerUsers)); 
	}
	
   
	@ApiMethod(path="api/providers/{referenceId}/{status}",  produces={ MediaType.APPLICATION_JSON_VALUE},description= " Activates/Deactivates a provider/listing  by a referenceId ")
	@ApiParams(pathparams={@ApiPathParam(name = "referenceId",  description ="provider reference identifier "), 
			@ApiPathParam(name = "status",  description =" new status of a provider/listing", allowedvalues={"activate","deactivate"})})
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@ApiErrors(apierrors={@ApiError(code="400",description = "Bad request, correct the errors and retry"),
			@ApiError(code="401",description = "Access Denied "), @ApiError(code="500",description = "Server error, try again later")})
	@RequestMapping(value = "api/providers/{referenceId}/{status}", method = RequestMethod.PUT , produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activateProvider(@PathVariable String referenceId, @PathVariable String status ) {
		
		if(!status.equals(STATUS.activate.name()) && !status.equals(STATUS.deactivate.name())){
			ResponseStatus body = new ResponseStatus(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.getReasonPhrase());
			body.addMessage("Unrecognized  action status : " + status + " try with one of [ activate , deactivate ]");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
		}
		
		boolean activate = status.equals("activate");
		emefanaService.activateProvider(referenceId, activate);
		return ResponseEntity.ok().build();
	}

}
