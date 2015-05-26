/**
 * 
 */
package com.idrene.emefana.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.OptionalInt;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idrene.emefana.util.DateConvertUtil;

/**
 * @author iddymagohe
 *
 */
@ApiObject
public class SearchCriteria {
	@ApiObjectField(name ="fromDate", description="event end date")
	@Getter @Setter private Date fromDate;
	@ApiObjectField(name ="fromDate", description="event start date")
	@Getter @Setter private Date toDate;
	@Getter @Setter private double[] nearLocation;
	@Getter @Setter private Double priceFrom;
	@Getter @Setter private Double priceTo;
	@Getter @Setter private String city;
	@Getter @Setter private int capacityFrom;
	@Getter @Setter private int capacityTo;
	@Getter @Setter private String[] features; 
	@Getter @Setter private String[] services;
	@Getter @Setter private String providerType;//category
	@Setter @Getter private Pageable page;
	@Setter @Getter private int maxDistance = 15;
	
	@JsonIgnore
	@Setter @Getter private String usedAs;
	
	/*
	 * #Booking related fields  
	 */
	@ApiObjectField(name ="customer", description="A user or customer who booking a service")
	@Getter @Setter private User customer;
	@ApiObjectField(name ="venue", description="A venue to book, applicable to venue booking")
	@Getter @Setter private VenuesDetail venue;
	
	@ApiObjectField(name ="event", description="An event type, a booking is made for, Ex: Birthdays, Weddings")
	@Getter @Setter private EventType event;
	
	@ApiObjectField(name ="associatedProvider", description="A provider to rander/ fulfill the booking")
	@Getter @Setter private String associatedProvider;
	
	@ApiObjectField(name ="price", description="Price details associated with this booking")
	@Getter @Setter double price;
	
	public Optional<LocalDate> getOFromDate(){
		return Optional.ofNullable(DateConvertUtil.asLocalDate(fromDate));
	}
	
	public Optional<LocalDate> getOToDate(){
		return Optional.ofNullable(DateConvertUtil.asLocalDate(toDate));
	}
	
	public Optional<double[]> getONearLocation(){
		return Optional.ofNullable(nearLocation);
	}
	
	public Optional<Double> getOpriceFrom(){
		return Optional.ofNullable(priceFrom);
	}
	
	public Optional<Double> getOpriceTo(){
		return Optional.ofNullable(priceTo);
	}
	
	public Optional<String> getOCity(){
		return Optional.ofNullable(city);
	}
	
	public OptionalInt getOCapacityFrom(){
		return OptionalInt.of(capacityFrom);
	}
	
	public OptionalInt getOCapacityTo(){
		return OptionalInt.of(capacityTo);
	}
	
	public Optional<String[]> getOFeatures(){
		return Optional.ofNullable(features);
	}
	
	public Optional<String[]> getOServices(){
		return Optional.ofNullable(services);
	}
	
	public Optional<String> getOProviderType(){
		return Optional.ofNullable(StringUtils.trimToNull(providerType));
	}
}

