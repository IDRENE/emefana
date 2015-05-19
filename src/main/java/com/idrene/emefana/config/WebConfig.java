/**
 * 
 */
package com.idrene.emefana.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author iddymagohe
 * @since 1.0
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("**/*.css", "**/*.js", "**/*.map", "*.html")
				.addResourceLocations("classpath:META-INF/resources/")
				.setCachePeriod(0);
	}

}
