package com.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.ItineraryPlanner.DataParameters;
import com.ItineraryPlanner.SystemFactory;
import com.entity.User;
import com.opl.OPLFactory;

/**
 * Planner Servlet for web service call
 * 
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

		String processingOption = req.getParameter("processMethod");

		/* Initialization of user account */
		String strBudget = req.getParameter("budget");
		String strNoOfDays = req.getParameter("noOfDays");
		String startLocation = req.getParameter("startLocation");

		String isDestination = req.getParameter("isDestination");

		User user;
		String[] selectedLocations = null;
		if (isDestination != null) {
			selectedLocations = req.getParameterValues("locationList");
			user = SystemFactory.intializesUser(Double.valueOf(strBudget),
					Integer.valueOf(strNoOfDays),
					Integer.valueOf(startLocation), selectedLocations);
		} else {
			user = SystemFactory.intializesUser(Double.valueOf(strBudget),
					Integer.valueOf(strNoOfDays),
					Integer.valueOf(startLocation));
		}

		/* Run optimal results */
		JSONObject results = new JSONObject();

		// Run algorithm for optimal solution
		if (processingOption.equalsIgnoreCase("heuristic")) {
			results = runHeuristic(user);
		} else {
			
			ArrayList<String> destinations = new ArrayList<String>();
			
			if (selectedLocations == null) {
				destinations = DataParameters.getAllLocations();
			} else {
				destinations = new ArrayList<String>(
						Arrays.asList(selectedLocations));
			}
			int tripLength = Integer.parseInt(strNoOfDays);
			runOPL(tripLength, strBudget, destinations, startLocation);
		}

		// Store in servlet context and navigate to results.html
		ServletContext context = getServletContext();
		context.setAttribute("Results", results.toJSONString());

		resp.sendRedirect("results.html");
	}

	/**
	 * Run results via Heuristics
	 * 
	 * @param user
	 * @return
	 */
	public JSONObject runHeuristic(User user) {
		return user.generateResults();
	}

	public void runOPL(int tripLength, String budget,
			ArrayList<String> selectedDestination, String startLocation) {
		String startDestination = null;
		ArrayList<String> airportCodes = new ArrayList<String>();
		for (int i = 0; i < selectedDestination.size(); i++) {
			String airport = selectedDestination.get(i);
			System.out.println(airport + "\t" + startLocation);
			if (i == Integer.parseInt(startLocation)) {
				startDestination = airport;
				airportCodes.add(0, airport);
			} else {
				airportCodes.add(airport);
			}
		}

		for (String codes : airportCodes)
			System.out.print(codes + "\t");

		File datFile = OPLFactory.generateDat(tripLength, budget, airportCodes,
				startDestination);
		try {
			OPLFactory.runOPL(datFile);
			// OPLFactory.cleanup(datFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
