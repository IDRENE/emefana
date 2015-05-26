/**
 * 
 */
package com.idrene.emefana.rest.validators;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.idrene.emefana.AbstractIntegrationTest;
import com.idrene.emefana.domain.EventType;
import com.idrene.emefana.domain.SearchCriteria;
import com.idrene.emefana.domain.User;
import com.idrene.emefana.util.DateConvertUtil;

/**
 * @author iddymagohe
 * @since 1.0
 */
public class SearchCriteriaValidatorTest extends AbstractIntegrationTest{
	
	@Autowired
	@Qualifier("searchCriteriaValidator")
	private Validator searchValidator;
	
	@Test
	public void requiredUsedAsValueTest(){
		SearchCriteria criteria = new SearchCriteria();
		BindingResult result = new BindException(criteria,"SearchCriteria");
		searchValidator.validate(criteria, result);
		assertTrue(result.hasErrors());
		assertTrue(result.getErrorCount() == 1);
	}
	
	@Test
	public void requiredDateValueTest(){
		SearchCriteria criteria = new SearchCriteria();
		criteria.setUsedAs("booking");
		BindingResult result = new BindException(criteria,"SearchCriteria");
		searchValidator.validate(criteria, result);
		assertTrue(result.hasErrors());
		assertTrue(result.getErrorCount() == 2);
	}
	
	@Test
	public void validatePastDatesTest(){
		SearchCriteria criteria = new SearchCriteria();
		criteria.setUsedAs("booking");
		criteria.setFromDate(DateConvertUtil.asUtilDate(LocalDateTime.now().minusDays(1)));
		criteria.setToDate(DateConvertUtil.asUtilDate(LocalDateTime.now().minusDays(2)));
		BindingResult result = new BindException(criteria,"SearchCriteria");
		searchValidator.validate(criteria, result);
		assertTrue(result.hasErrors());
		assertTrue(result.getErrorCount() == 2);
	}
	
	
	@Test
	public void validatefromAfterToDatesTest(){
		SearchCriteria criteria = new SearchCriteria();
		criteria.setUsedAs("booking");
		criteria.setFromDate(DateConvertUtil.asUtilDate(LocalDateTime.now().plusDays(1)));
		criteria.setToDate(DateConvertUtil.asUtilDate(LocalDateTime.now().plusMinutes(1)));
		BindingResult result = new BindException(criteria,"SearchCriteria");
		searchValidator.validate(criteria, result);
		assertTrue(result.hasErrors());
		//result.getAllErrors().forEach(System.out::println);
		assertTrue(result.getErrorCount() == 1);
		
	}
	
	@Test
	public void validBookingNonVenueWithUnkownProvidertest(){
		SearchCriteria criteria = new SearchCriteria();
		EventType ev = new EventType();
		ev.setEid("SomeEvent");
		criteria.setUsedAs("booking");
		criteria.setFromDate(DateConvertUtil.asUtilDate(LocalDateTime.now().plusMinutes(1)));
		criteria.setToDate(DateConvertUtil.asUtilDate(LocalDateTime.now().plusDays(1)));
		criteria.setProviderType("SomeCategory");
		criteria.setProviderType("some provider");
		criteria.setCustomer(new User("id", "some@email.com",""));
		criteria.setAssociatedProvider("someProvider");
		criteria.setEvent(ev);
		BindingResult result = new BindException(criteria,"SearchCriteria");
		searchValidator.validate(criteria, result);
		result.getAllErrors().forEach(System.out::println);
		assertTrue(result.hasErrors());
		assertTrue(result.getErrorCount() == 1);
	}
	
	
}
