/**
 * 
 */
package com.idrene.emefana.rest.converters.response;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.idrene.emefana.domain.User;
import com.idrene.emefana.rest.controllers.UserResourceController;
import com.idrene.emefana.rest.resources.ResourceView;
import com.idrene.emefana.rest.resources.UserResource;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource>{
	
	private ResourceView view;

	public UserResourceAssembler(ResourceView view) {
		super(UserResourceController.class, UserResource.class);
		this.view = view;
	}

	@Override
	public UserResource toResource(User entity) {
		UserResource resource = new UserResource(entity, view);
		return resource;
	}

}
