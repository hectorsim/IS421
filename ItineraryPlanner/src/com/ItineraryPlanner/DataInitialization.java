/**
 * 
 */
package com.ItineraryPlanner;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.simple.JSONObject;

/**
 * @author Leon
 * Initialization of DAT data
 */
public class DataInitialization implements ServletContextListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("DATA INITIALIZATION PROCESSING...");
		
		ServletContext servletContext = arg0.getServletContext();
		String contextPath = servletContext.getRealPath(File.separator);
		
		JSONObject data = SystemFactory.datRetrieval(contextPath);
		
		System.out.println(data.toJSONString());
	}

}
