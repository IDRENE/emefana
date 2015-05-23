/**
 * 
 */
package com.idrene.emefana.rest.controllers;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiHeader;
import org.jsondoc.core.annotation.ApiHeaders;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.idrene.emefana.domain.MetaResource;
import com.idrene.emefana.service.MetaService;

/**
 * @author iddymagohe
 * @since 1.0
 */
@Api(description = "Provides a set APIs to retrieve metadata values to be used by providers/listings apps ", name = "Metadata Service")
@Controller
public class MetaResourceController {
	
	@Autowired
	private MetaService metaService;
	
	@ApiMethod(path="api/metadata",  produces={ MediaType.APPLICATION_JSON_VALUE},description= " List of application metadata, Ex drop down values ")
	@ApiHeaders(headers={@ApiHeader(name="X-Auth-Token", description = "Authentication Token")})
	@ApiErrors(apierrors={@ApiError(code="401",description = "Access Denied ")})
	@ApiResponseObject(clazz = MetaResource.class)
	@RequestMapping(value={"api/metadata","app/providers/api/metadata"}, method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<MetaResource<?>>> retrieveMetadata(){
		return new ResponseEntity<>(metaService.retrieveMeta(),HttpStatus.OK);
		
	}

}
