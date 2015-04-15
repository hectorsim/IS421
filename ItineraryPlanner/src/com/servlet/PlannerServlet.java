package com.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ItineraryPlanner.Constants;
import com.ItineraryPlanner.SystemFactory;
import com.entity.User;
import com.opl.OPLFactory;

public class PlannerServlet extends HttpServlet {

	/**
	 * Auto-generated serial version id for PlannerServlet ID
	 */
	private static final long serialVersionUID = 3155262073919874299L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String processingOption = req.getParameter("processMethod");
		
		/** Initialization of user account **/
		String strBudget = req.getParameter("budget");
		String strNoOfDays = req.getParameter("noOfDays");
		String startLocation = req.getParameter("startLocation");
		
		String isDestination = req.getParameter("isDestination");
		
		User user;
		if (isDestination != null) {
			String[] selectedLocations = req.getParameterValues("locationList");
			user = SystemFactory.intializesUser(Double.valueOf(strBudget), Integer.valueOf(strNoOfDays), 
					Integer.valueOf(startLocation), selectedLocations);
		} else {
			user = SystemFactory.intializesUser(Double.valueOf(strBudget), Integer.valueOf(strNoOfDays), 
					Integer.valueOf(startLocation));
		}
		
		// Run algorithm for optimal solution
		if (processingOption.equalsIgnoreCase("heuristic")) {
			runHeuristic(user);
		} else {
			runOPL();
		}
	}
	
	public void runHeuristic(User user) {
		
	}

	public void runOPL() {
		ServletContext context = getServletContext();
		
		try {
			OPLFactory.runOPL(context.getRealPath(Constants.FILESEPARATOR));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
