package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.ItineraryPlanner.Constants;
import com.ItineraryPlanner.SystemFactory;
import com.entity.User;
import com.opl.OPLFactory;

/**
 * Planner Servlet for web service call
 * @author Leon
 *
 */
public class PlannerServlet extends HttpServlet {

	/**
	 * Auto-generated serial version id for PlannerServlet ID
	 */
	private static final long serialVersionUID = 3155262073919874299L;

	/**
	 * Do Get for HTTPServlet
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		ServletContext context = getServletContext();
		String str = (String) context.getAttribute("Results");
		PrintWriter out = resp.getWriter();  
		out.write(str);
	}

	/**
	 * Do Post for HTTPServlet
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String processingOption = req.getParameter("processMethod");
		
		/* Initialization of user account */
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
		
		/* Run optimal results */
		JSONObject results = new JSONObject();
		
		// Run algorithm for optimal solution
		if (processingOption.equalsIgnoreCase("heuristic")) {
			results = runHeuristic(user);
		} else {
			results = runOPL();
		}
		
		// Store in servlet context and navigate to results.html
		ServletContext context = getServletContext();
		context.setAttribute("Results", results.toJSONString());
		
		resp.sendRedirect("results.html");
	}
	
	/**
	 * Run results via Heuristics
	 * @param user
	 * @return
	 */
	public JSONObject runHeuristic(User user) {
		return user.generateResults();
	}

	/**
	 * Run result via OPL
	 * @return
	 */
	public JSONObject runOPL() {
		ServletContext context = getServletContext();
		JSONObject jsonOPLResults = new JSONObject();
		
		try {
			jsonOPLResults = OPLFactory.runOPL(context.getRealPath(Constants.FILESEPARATOR));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonOPLResults;
	}
}
