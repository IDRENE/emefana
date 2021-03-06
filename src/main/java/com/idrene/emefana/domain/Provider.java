/**
 * 
 */
package com.idrene.emefana.domain;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

/**
 * @author iddymagohe
 *
 */
@Document(collection="providers")
@TypeAlias("providers")
public class Provider {
	@Id
	@Getter @Setter private String pid;
	
	@Getter @Setter boolean activated=false;
	
	@DBRef
	@Getter @Setter Provider parent;
	
	@TextIndexed(weight = 5)
	@Getter @Setter private String name;
	
	@TextIndexed(weight = 2)
	@Getter @Setter private String description;
	
	@GeoSpatialIndexed(type=GeoSpatialIndexType.GEO_2DSPHERE)
	@Getter @Setter private double[] location;
	
	@DBRef
	@Getter @Setter private Set<ProviderType> categories = new HashSet<>();
	
	/*
	 * used to easy provider search
	 */
	@Getter @Setter private int capacity; 
	@Getter @Setter private Price price; 
	
	@Indexed
	@Getter @Setter Date registrationDate;
	
	@Getter @Setter private String code;
	
	@Getter @Setter private Address address;
	
	@Getter @Setter private String hours;
	
	@Transient
	@Getter @Setter private User providerUser;
	
	@Getter @Setter private Set<ProviderEvents> events = new HashSet<>();
	@Getter @Setter private Set<VenuesDetail> venuesDetails = new HashSet<>();
	
	@Getter @Setter private List<Feature> features = new LinkedList<>();
	
	@Getter @Setter private List<Contact> contacts = new LinkedList<>();
	
	@Getter @Setter private List<ProviderService> services = new LinkedList<>();
	
	@Transient
	@Getter @Setter FileMetadata thumnailPhoto;
	
	@Transient
	@Getter @Setter List<FileMetadata> gallaryPhotos;
	
	@TextScore 
	@Getter @Setter Float score;
	
	@Transient
	@Getter @Setter String currency;
	
     /**
      * Check if it`s a Venue first
     * @return
     */
    public DoubleSummaryStatistics getPriceStatistics(){

    	 if (categories.stream().anyMatch(type-> type.getType().equals(ProviderCategories.Venues.name()))){
	    		 venuesDetails.stream().findAny().ifPresent(v -> { this. currency = v.getPrice().getCurrency();});
    		return  venuesDetails.stream().mapToDouble(v -> v.getPrice().getPrice()).summaryStatistics();
    	 }

    	 
    	 services.stream().filter(s-> null != s.getPrice()).findAny().ifPresent(s -> {
			 this. currency = s.getPrice().getCurrency();
		 });
    	 
		return  services.stream().filter(s-> null != s.getPrice()).findAny().isPresent()?
				services.stream().mapToDouble(s -> s.getPrice().getPrice()).summaryStatistics():
					new DoubleSummaryStatistics();
    	 
     }
}
