/**
 * 
 */
package com.idrene.emefana.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.OptionalInt;

import lombok.Getter;
import lombok.Setter;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.idrene.emefana.rest.resources.ResourceUtil;
import com.idrene.emefana.util.DateConvertUtil;

/**
 * @author iddymagohe
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiObject
public class SearchCriteria {
	@JsonIgnore
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	
	@JsonIgnore
	@Setter private Date fromDate;
	
	@Setter @Getter boolean uselocation;

	@JsonIgnore
	@Setter private Date toDate;
	
	@Getter @Setter String eventDate;
	
	@Getter @Setter Integer eventDays;
	
	@Getter private double[] nearLocation;// longitude, latitude
	@Getter @Setter private Double priceFrom;
	@Getter @Setter private Double priceTo;
	@Getter private String city;
	@Getter @Setter private int capacityFrom;
	@Getter @Setter private int capacityTo;
	@Getter @Setter private String[] features; 
	@Getter @Setter private String[] services;
	@Getter @Setter private String providerType;//category
	@Setter @Getter private Pageable page;
	@Setter @Getter private int maxDistance = 15;
	@Setter @Getter private String country = "Tanzania";
	
	@Getter @Setter private String nearLocationStr;
	
	@ApiObjectField(name ="fromDateAsString", description="event start date", format="yyyy-MM-dd HH:mm:ss.SSS")
	@Getter @Setter String fromDateAsString;
	
	@ApiObjectField(name ="toDateAsString", description="event end date", format="yyyy-MM-dd HH:mm:ss.SSS")
	@Getter @Setter String toDateAsString;
	
	
	/**
	 * indicates a context in which the {@link #SearchCriteria()} is used
	 * between Searching or Booking
	 * 
	 */
	@JsonIgnore
	@Setter @Getter private String usedAs; 

	@Getter @Setter String name;

	/**
	 * @param nearLocation the nearLocation to set
	 */
	public void setNearLocation(double[] nearLocation) {
		if (StringUtils.hasText(nearLocationStr) && ! uselocation)
			this.nearLocation = ResourceUtil.nearLocationString(nearLocationStr);
		else
		this.nearLocation = nearLocation;
	}
	
	

	/*
	 * #Booking related fields  
	 */
	@ApiObjectField(name ="customer", description="A user or customer who booking a service")
	@Getter @Setter private User customer;
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		if (city.contains(",")) 
			this.city = city.split(",")[0]; //TODO correct this
		else
		this.city = city;
	}



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
		if (StringUtils.hasText(nearLocationStr))
			this.nearLocation = ResourceUtil.nearLocationString(nearLocationStr); //TODO correct this after asking user`s location
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
		return Optional.ofNullable(StringUtils.trimWhitespace(providerType));
	}
	

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		if (fromDate == null && StringUtils.hasText(fromDateAsString)){
			 try {
				return sf.parse(fromDateAsString);
			} catch (ParseException e) {
				// return null;
			}
		}
		return fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		if (toDate == null && StringUtils.hasText(toDateAsString)){
			try {
				return sf.parse(toDateAsString);
			} catch (ParseException e) {
				return null;
			}
		}
		return toDate;
	}
}

