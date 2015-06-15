/**
 * 
 */
package com.idrene.emefana.rest.resources.validators;

import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.idrene.emefana.domain.ProviderCategories;
import com.idrene.emefana.domain.SearchCriteria;
import com.idrene.emefana.repositories.ProviderRepository;

/**
 * 
 * @author iddymagohe
 * @since 1.0
 * 
 * Validate Search or Booking (Criteria)
 */
@Service("searchCriteriaValidator")
public class SearchCriteriaValidator implements Validator{
	
	@Autowired
	private  ProviderRepository providerRepo;

	@Override
	public boolean supports(Class<?> clazz) {
		return SearchCriteria.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "usedAs", null,"usedAs is a required field, to distinguish Search and Booking requests ");
		SearchCriteria bookingCriteria = (SearchCriteria)target;
		
		if(errors.hasErrors() )return;
		if (bookingCriteria.getUsedAs().equals("booking")) {
			if (bookingCriteria.getFromDate() == null) {
				errors.rejectValue("fromDate", null,"fromDate is a required field");
			}

			if (bookingCriteria.getToDate() == null) {
				errors.rejectValue("toDate", null, "toDate is a required field");
				return;
			}

			if (bookingCriteria.getFromDate().before(new Date()) || bookingCriteria.getToDate().before(new Date())) {
				errors.reject(null, "Date and time  can not be in past");
			}

			if (bookingCriteria.getFromDate().after(bookingCriteria.getToDate())) {
				errors.reject(null, " fromDate can not be after toDate");
				return;
			}

			if (bookingCriteria.getCustomer() == null|| !StringUtils.hasText(bookingCriteria.getCustomer().getEmailAddress())) {
				errors.reject("customer", "Customer Email is a required field");
			}

			if (!StringUtils.hasText(bookingCriteria.getProviderType())) {
				errors.rejectValue("providerType", null,"Provider category is a required field ");
				return;
			}

			if (bookingCriteria.getEvent() == null || !StringUtils.hasText(bookingCriteria.getEvent().getEid())) {
				errors.rejectValue("event", null, "Event is a required field");
			}

			if (bookingCriteria.getProviderType().equals(ProviderCategories.Venues.name())) {
				if (bookingCriteria.getVenue() == null || !StringUtils.hasText(bookingCriteria.getVenue().getName())) {
					errors.rejectValue("venue", null,"Venue details is a requied field");
				}
			}
			
			if (!StringUtils.hasText(bookingCriteria.getAssociatedProvider())){
				errors.rejectValue("associatedProvider", null, "a provider of this booking is a required field");
				return;
			}else{
				String providerId = StringUtils.trimAllWhitespace(bookingCriteria.getAssociatedProvider());
				if (! providerRepo.exists(providerId)) 
					errors.rejectValue("associatedProvider", null, "Unknow provider supplied");
			}

		}else{
			if (!StringUtils.hasText(bookingCriteria.getCity())){
				errors.rejectValue("city", null, "a city for  availble providers is a required field");
			}
			
			if (!StringUtils.hasText(bookingCriteria.getProviderType())){
				errors.rejectValue("providerType", null, "provider category is a required field");
			}
			
			if(!StringUtils.hasText(bookingCriteria.getNearLocationStr()) && CollectionUtils.sizeIsEmpty(bookingCriteria.getNearLocation())){
				errors.rejectValue("nearLocation", null, "nearest Location is a required field");
			}

			return;
            
		}
	}

}
