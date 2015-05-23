/**
 * 
 */
package com.idrene.emefana.rest.resources;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import lombok.Getter;

/**
 * @author iddymagohe
 * @since 1.0
 */
@ApiObject
public class TokenResource {

	@ApiObjectField(name ="token", description ="Token key", required=true)
	@Getter public final String token;

	public TokenResource(String token) {
		this.token = token;
	}
	
	
}
