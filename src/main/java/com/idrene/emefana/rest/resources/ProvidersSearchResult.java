/**
 * 
 */
package com.idrene.emefana.rest.resources;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;

import org.springframework.data.geo.Distance;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class ProvidersSearchResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter  List<ProviderResource> providers;
	@Getter  Distance everageDistance;
	@Getter int size;
	
	public ProvidersSearchResult(List<ProviderResource> providers,Distance everageDistance) {
		this.providers = providers;
		this.everageDistance = everageDistance;
		this.size = providers.size();
	}
	
	

}
