package com.idrene.emefana.rest.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class RequestResponseLogger  extends HandlerInterceptorAdapter{
	Logger logger= LoggerFactory.getLogger(RequestResponseLogger.class);


	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)throws Exception {
		logger.info(" --response " + response.getStatus());
	}

	

}
