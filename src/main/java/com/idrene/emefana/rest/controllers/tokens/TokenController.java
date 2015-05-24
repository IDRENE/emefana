/**
 * 
 */
package com.idrene.emefana.rest.controllers.tokens;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiError;
import org.jsondoc.core.annotation.ApiErrors;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.jsondoc.core.pojo.ApiVerb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.idrene.emefana.rest.resources.EmefanaUser;
import com.idrene.emefana.rest.resources.TokenResource;
import com.idrene.emefana.security.TokenUtils;
import com.idrene.emefana.util.UtilityBean;

/**
 * @author iddymagohe
 * @since 1.0
 */
@Api(description = "Generates an X-Auth-Token , that expires after 1 hour of generation", name = "Tokens Service")
@Controller
@RequestMapping(value="/api")
public class TokenController {
	
	
	@Autowired
	@Lazy
	private  AuthenticationManager authenticationManager;
	
	@Autowired
	private  UtilityBean utilityBean;
	
	@Autowired
	private  UserDetailsService userDetailsService;
	
	@ApiMethod(path="api/authenticate",verb=ApiVerb.POST, consumes={ MediaType.APPLICATION_JSON_VALUE}, produces={ MediaType.APPLICATION_JSON_VALUE},
			description= " Generates a token, used to interact with other APIs " )
	@ApiErrors(apierrors={@ApiError(code="401",description = "Access Denied ")})
	@ApiBodyObject(clazz=EmefanaUser.class)
	@ApiResponseObject(clazz = TokenResource.class)
	@RequestMapping(value="/authenticate", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> generateToken(@RequestBody EmefanaUser user){
		ResponseEntity<?> response = null;
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserId(),user.getCredential());
		
		try {
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			/*
			 * Reload user as password of authentication principal will be null
			 * after authorization and password is needed for token generation
			 * 
			 */
			UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserId());
			String token = TokenUtils.createToken(userDetails);
			response = ResponseEntity.ok(new TokenResource(UtilityBean.encrypt(token, utilityBean.getSECRET_KEY())));
			//TODO Future, allow clients to store their own SecretKey
		} catch (Exception e) {
			response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return response;
	} 

}
