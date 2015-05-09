/**
 * 
 */
package com.idrene.emefana.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.StringUtils;

import com.idrene.emefana.rest.resources.ListingResource;
import com.idrene.emefana.service.events.CreationEvent;
import com.idrene.emefana.util.UtilityBean;

/**
 * @author iddymagohe
 * @since 1.0
 */
public interface MailService {
	public void sendMail(ListingResource provider);
	public void sendMailForSuggestedListing(CreationEvent<ListingResource> listingCreatedEvent);

}

@Service
class MailServiceImpl implements MailService{
	private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
	/*
	 * TODO observer for email notification
	 */
	protected static final Resource resource = new ClassPathResource("templates/logo.png");
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Override
	public void sendMail(ListingResource provider) {
		simplemail();
	}
	
	@Value("${email.bcc}")
	private String bccEmail;
	
	/**Listing for listingCreationEvent
	 * @param listingCreatedEvent
	 */
	
	@EventListener
	@Async
	public void sendMailForSuggestedListing(CreationEvent<ListingResource> listingCreatedEvent)  {
		final ListingResource provider = listingCreatedEvent.get();
		logger.info("Sending email for Listing :" + provider.getName());
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
	        @SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
	             MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
	             message.setTo(provider.getUser().getEmailaddress());
	             
	             if(StringUtils.hasText(provider.getEmailaddress())){
	            	  message.setCc(provider.getEmailaddress());
	             }
	            	 message.setBcc(bccEmail);
	            	// message.setFrom(new InternetAddress(suggestedPodcast.getEmail()) );
	             
	             message.setSentDate(new Date());
	             Map model = new HashMap();	             
	             model.put("listing", provider);
	             model.put("logo", MailServiceImpl.getBase64Logo());
	             
	             String text="";
	          	
					if (listingCreatedEvent.isAnExistingListing()) {
						message.setSubject("Existing Listing - "+ provider.getName());
						text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/suggestedListingExistsNotificationMessage.vm", "UTF-8", model);
					} else {
						message.setSubject("New suggested Listing - " + provider.getName());
						text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,  "templates/suggestedListingNotificationMessage.vm", "UTF-8", model);
					}
	                
	             message.setText(text, true);
	          }
	       };
	       mailSender.send(preparator);	
	}
	
	void simplemail(){

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		try {
			helper.setTo(bccEmail);
			helper.setText("Thank you for Registering with Emefana! test");
			helper.setSubject("Emefana Hiyoooo");
		} catch (MessagingException e) {
			// TODO log this exception
			//Notify listeners for dbLogging & later re-try
			e.printStackTrace();
		}
		mailSender.send(message);
	}
	
	public static String getBase64Logo(){
		String logo="";
		try {
		logo =	UtilityBean.InputStreamToBase64(Optional.of(resource.getInputStream()), "png").get();
		} catch (IOException e) {
			// TODO Log this 
			e.printStackTrace();
		}
		return logo;
	}
	
}
