package com.idrene.emefana;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author iddymagohe
 * @since 1.0
 */
@Controller
@SpringBootApplication
public class EmefanaApp {
	
	@Value("${mail.smtp.host}")
	private String mailHost;
	
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello EmefanaApp!" + mailHost;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(EmefanaApp.class, args);
	}
}
