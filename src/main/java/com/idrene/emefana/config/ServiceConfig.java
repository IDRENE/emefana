/**
 * 
 */
package com.idrene.emefana.config;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import com.idrene.emefana.rest.converters.request.ListingResourceToProvider;
import com.idrene.emefana.util.UtilityBean;

/**
 * @author iddymagohe
 * @since 1.0
 * 
 */
@Configuration
@ComponentScan(basePackages = { "com.idrene.emefana.domain",
		"com.idrene.emefana.repositories", "com.idrene.emefana.service",
		"com.idrene.emefana.security" ,"com.idrene.emefana.rest.controllers","com.idrene.emefana.rest.tokens"})
// @PropertySource(value = { "classpath:application.properties" })
public class ServiceConfig {

//	 @Value("${app.encoder.key}")
//	 private String app_encrypt;

	// @Value("${mail.smtp.host}")
	// private String mailHost;
	//
	// @Value("${mail.smtp.port}")
	// private int mailPort;
	//
	// @Value("${mail.smtp.protocal}")
	// private String mailProtocal;
	//
	// @Value("${mail.smtp.user}")
	// private String mailUser;
	//
	// @Value("${mail.smtp.password}")
	// private String mailPassword;
	//
	// @Value("${mail.smtp.auth}")
	// private boolean mailAuth;
	//
	// @Value("${mail.smtp.starttls.enable}")
	// private boolean mailStarttls;

	// @Bean
	// public static PropertySourcesPlaceholderConfigurer
	// placeHolderConfigurer() {
	// return new PropertySourcesPlaceholderConfigurer();
	// }

	/*
	 * https://support.google.com/mail/answer/13287?hl=en
	 */
	@Bean
	@Autowired
	@Lazy(true)
	public JavaMailSenderImpl javaMailSenderImpl(UtilityBean utilityBean,
			@Value("${mail.smtp.host}") String mailHost,
			@Value("${mail.smtp.port}") int mailPort,
			@Value("${mail.smtp.user}") String mailUser,
			@Value("${mail.smtp.password}") String mailPassword,
			@Value("${mail.smtp.auth}") boolean mailAuth,
			@Value("${mail.smtp.starttls.enable}") boolean mailStarttls) throws Exception {
		JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
		mailSenderImpl.setHost(mailHost);
		mailSenderImpl.setPort(mailPort);
		
		// mailSenderImpl.setProtocol(mailProtocal); // use default
		mailSenderImpl.setUsername(mailUser);
		mailSenderImpl.setPassword(UtilityBean.decrypt(mailPassword, utilityBean.getSECRET_KEY()));

		Properties javaMailProps = new Properties();
		javaMailProps.put("mail.smtp.auth", mailAuth);
		javaMailProps.put("mail.smtp.starttls.enable", mailStarttls);

		mailSenderImpl.setJavaMailProperties(javaMailProps);

		return mailSenderImpl;
	}

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}

	@Bean
	@Autowired
	public UtilityBean utilityBean(@Value("${app.encoder.key}") String app_encrypt) {
		UtilityBean utlityBean = new UtilityBean();
		utlityBean.setSECRET_KEY(app_encrypt);
		return utlityBean;
	}


	@Bean
	public PasswordEncryptor passwordEncryptor() {
		return new BasicPasswordEncryptor();
	}

	@Bean
	public VelocityEngineFactoryBean velocityEngine() {
		VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
		Properties velocityProperties = new Properties();
		velocityProperties.setProperty("resource.loader", "class");
		velocityProperties
				.setProperty("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngineFactoryBean.setVelocityProperties(velocityProperties);
		return velocityEngineFactoryBean;
	}

	@Bean(name = "conversionService")
	public ConversionService conversionService() {
		ConversionServiceFactoryBean conversionService = new ConversionServiceFactoryBean();
		conversionService.setConverters(converters());
		conversionService.afterPropertiesSet();
		return conversionService.getObject();
	}

	@SuppressWarnings({ "rawtypes" })
	private Set<Converter> converters() {
		Set<Converter> converters = new HashSet<>();
		converters.add(new ListingResourceToProvider());
		return converters;
	}
}
