/**
 * 
 */
package com.idrene.emefana.rest.resources;

import lombok.Getter;
import lombok.Setter;

/**
 * @author iddymagohe
 * @since 1.0
 */
@Getter @Setter
public class PriceRangeResource {
	
	private String currency;
	private double priceFrom;
	private double priceTo;
	
	public PriceRangeResource(String currency, double priceFrom, double priceTo) {
		this.currency = currency ;
		this.priceFrom = priceFrom > 0 ? priceFrom :0 ;
		this.priceTo = priceTo > 0 ? priceTo : 0;
	}
	

}
