/**
 * 
 */
package com.ItineraryPlanner;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
		TimeTracker timer = new TimeTracker();
		
//		ServletContext servletContext = arg0.getServletContext();
//		System.out.println(this.getClass().getResource("/data").getPath());
//		String contextPath = servletContext.getRealPath(Constants.FILESEPARATOR);

		// Formatting for raw data into proper data
		//JSONObject rawData = SystemFactory.datRetrieval();
		//timer.addLap("Retrieval of data from DAT file");
		
		DataParameters.LocationIndexing();
		DataParameters.generateMinStayForLocation(1);
		DataParameters.generateDefaultSatisfactionValue(1);
		// Generate min stay and randomize satisfaction value for per location
		DataParameters.setUnitDecrement(1, 
						DataParameters.defaultSatisfactionValueByIndex, 
						DataParameters.minDayStayByIndex);
		
		SystemFactory.formatGraph();
		timer.addLap("Initialization of all data");
		
		System.out.println(Constants.GRAPH.toJSON().toJSONString());
		System.out.println();
		
		System.out.println("DATA INITIALIZATION Completed");
		System.out.println(timer.timeToString());
	}
}
