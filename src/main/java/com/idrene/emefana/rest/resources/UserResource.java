/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.util.Set;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.security.EMEFANA_ROLES;

/**
 * @author iddymagohe
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResource extends ResourceSupport {
	
	public String userReferenceId;
	public String firstName;
	public String lastName;
	public String emailAddress;
	public String associatedProvider;
	public String listingRole;
	public Set<EMEFANA_ROLES> appRoles;
	
	public UserResource(User user, ResourceView view){
		userReferenceId = user.getId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		emailAddress = user.getEmailAddress();
		associatedProvider = user.getAssociatedProvider();
		listingRole = user.getListingRole();
		appRoles = user.getRoles();
		
	}
}
