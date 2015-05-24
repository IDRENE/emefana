/**
 * 
 */
package com.idrene.emefana.domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author iddymagohe
 *
 */
@Document(collection="bookings")
@TypeAlias("booking")
public class Booking {
	
	@Id
	@Getter @Setter private String bid;
	
	@DBRef
	@Getter @Setter User customer;
	
	@DBRef
	@Getter @Setter private Provider provider;
	
	@DBRef
	@Getter @Setter private EventType event;
	
	@Indexed
	@Getter @Setter private Date startDate;
	
	@Indexed
	@Getter @Setter private  Date endDate;
	
	@Getter @Setter private BookingStatus status;
	
	@Getter @Setter private  boolean venueBooking;
	
	@Getter @Setter private VenuesDetail  venueDetail;
	
	@Getter @Setter private List<BookedService> bookedServices = new LinkedList<>();

	@Override
	public String toString() {
		return "Booking [bid=" + bid + ", customer=" + customer + ", provider="
				+ provider + ", event=" + event + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", status=" + status + "]";
	}
	
	
}
