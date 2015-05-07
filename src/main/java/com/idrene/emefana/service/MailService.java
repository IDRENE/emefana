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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

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
	
	/**Listing for listingCreationEvent
	 * @param listingCreatedEvent
	 */
	
	@EventListener
	@Async
	public void sendMailForSuggestedListing(CreationEvent<ListingResource> listingCreatedEvent)  {
		System.out.println("Event fired..."); //TODO log this event
		final ListingResource provider = listingCreatedEvent.get();
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
	        @SuppressWarnings({ "rawtypes", "unchecked" })
			public void prepare(MimeMessage mimeMessage) throws Exception {
	             MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
	             message.setTo(provider.getUser().getEmailaddress());
	             message.setBcc("iddy85@gmail.com");
	            // message.setFrom(new InternetAddress(suggestedPodcast.getEmail()) );
	             message.setSubject("New suggested Listing - " + provider.getName() );
	             message.setSentDate(new Date());
	             Map model = new HashMap();	             
	             model.put("listing", provider);
	             model.put("logo", MailServiceImpl.getBase64Logo());
	             
	             String text = VelocityEngineUtils.mergeTemplateIntoString(
	                velocityEngine, "templates/suggestedListingNotificationMessage.vm", "UTF-8", model);
	             message.setText(text, true);
	          }
	       };
	       mailSender.send(preparator);	
	}
	
	void simplemail(){

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		try {
			helper.setTo("iddyiam@gmail.com");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logo;
	}
	
}
