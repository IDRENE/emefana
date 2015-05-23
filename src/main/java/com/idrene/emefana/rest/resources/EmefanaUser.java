/**
 * 
 */
package com.idrene.emefana.rest.resources;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author iddymagohe
 * @since 1.0
 */
@ApiObject
public class EmefanaUser {
	
	@ApiObjectField(name ="userId", description ="Email address", required=true)
	@Getter @Setter private String userId;
	
	@ApiObjectField(name ="credential", description="password key" , required=true)
	@Getter @Setter private String credential;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return userId;
	}
	
}
