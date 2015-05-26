/**
 * 
 */
package com.idrene.emefana.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.idrene.emefana.domain.Booking;
import com.idrene.emefana.domain.BookingStatus.BOOKINGSTATE;

/**
 * @author iddymagohe
 *
 */
public interface BookingRepository extends MongoRepository<Booking, String>,QueryDslPredicateExecutor<Booking>{
	
	public List<Booking> findByProviderPidAndStatusCurrentState(String pid, BOOKINGSTATE state);

}
