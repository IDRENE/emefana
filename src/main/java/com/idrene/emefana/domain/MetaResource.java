/**
 * 
 */
package com.idrene.emefana.domain;

import java.io.Serializable;
import java.util.List;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

/**
 * @author iddymagohe
 * @since 1.0
 */
@ApiObject
public class MetaResource<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiObjectField(name = "key")
	private String key;
	
	//@ApiObjectField(name = "list of values")
	private List<T> value;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value
	 */
	public List<T> getValue() {
		return value;
	}

	public MetaResource(String key, List<T> value) {
		this.key = key;
		this.value = value;
	}
	
}
