package com.idrene.emefana;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.MultipartConfigElement;

import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author iddymagohe
 * @since 1.0
 */
@Controller
@SpringBootApplication
@EnableAsync
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableJSONDoc
public class EmefanaApp {

	@Value("${mail.smtp.host}")
	private String mailHost;

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello EmefanaApp!" + mailHost;
	}

	@RequestMapping(value = {"/api/**","/api/provider"}, method = RequestMethod.OPTIONS)
	ResponseEntity<Void> getProposalsOptions() {
		return allows(HttpMethod.GET, HttpMethod.POST,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS);
	}

	public static ResponseEntity<Void> allows(HttpMethod... methods) {
		HttpHeaders headers = new HttpHeaders();
		Set<HttpMethod> allow = new HashSet<>();
		for (HttpMethod method : methods) {
			allow.add(method);
		}
		headers.setAllow(allow);

		return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(EmefanaApp.class, args);
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		DispatcherServlet servlet = new DispatcherServlet();
		servlet.setDispatchOptionsRequest(true);
		return servlet;
	}

	@Bean
	MultipartConfigElement multipartConfigElement() {
		return new MultipartConfigElement("");
	}
}
