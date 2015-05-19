/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

/**
 * @author iddymagohe
 * @since 1.0
 */
@ApiObject
public class ResponseStatus {
	
	@ApiObjectField(name = "statusCode" , required=true)
	@Getter @Setter int  statusCode;
	
	@ApiObjectField(name = "statusPhrase" , required=true)
	@Getter @Setter private String statusPhrase;
	
	@ApiObjectField
	@Getter @Setter private List<String> messages = new ArrayList<>();
	
	public ResponseStatus(int statusCode, String statusPhrase) {
		this.statusCode = statusCode;
		this.statusPhrase = statusPhrase;
	}
	
	public void addMessage(String message){
		messages.add(message);
	}
	
	
}
