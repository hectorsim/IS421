package com.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.ItineraryPlanner.Constants;
import com.ItineraryPlanner.DataParameters;
import com.ItineraryPlanner.SystemFactory;
import com.ItineraryPlanner.TimeTracker;
import com.entity.User;
import com.heuristic.HeuristicFactory;
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
		ServletContext context = getServletContext();
		String processingOption = req.getParameter("processMethod");

		/* Initialization of user account */
		int tripLength = Integer.parseInt(req.getParameter("noOfDays"));
		String strBudget = req.getParameter("budget");
		String startLocation = req.getParameter("startLocation");

		String isDestination = req.getParameter("isDestination");

		/*
		 * Preparation of heuristic data and OPL data Requires to create user
		 * object to generate results from OPL and Heuristics
		 */
		User user;
		String[] selectedLocations = null;

		if (isDestination != null) {
			selectedLocations = req.getParameterValues("locationList");

			user = SystemFactory.intializesUser(Double.valueOf(strBudget),
					tripLength, Integer.valueOf(startLocation),
					selectedLocations);
		} else {
			user = SystemFactory.intializesUser(Double.valueOf(strBudget),
					tripLength, Integer.valueOf(startLocation));
		}
		context.setAttribute("User", user);

		/* Run optimal results */
		JSONObject results = new JSONObject();

		// Run algorithm for optimal solution
		if (processingOption.equalsIgnoreCase("heuristic")) {
			// Heuristic Execution
			System.out.println(Constants.GRAPH.toJSON().toJSONString());
			results = runHeuristic(user);
		} else {

			// Preparation of OPL data
			ArrayList<String> destinations = new ArrayList<String>();
			ArrayList<String> processedDestinations = new ArrayList<String>();
			ArrayList<Integer> satisfactionArray = new ArrayList<Integer>();

			if (selectedLocations == null) {
				destinations = DataParameters.getAllLocations();
				for (int i = 0; i < destinations.size(); i++) {
					String airport = destinations.get(i);
					System.out.println("startLocation: \t" + startLocation);
					System.out.print(airport + "\t");
					if (i == (Integer.parseInt(startLocation) - 1)) {
						processedDestinations.add(0, airport);
					} else {
						processedDestinations.add(airport);
					}
				}
			} else {
				for (String locations : selectedLocations) {
					String[] values = locations.split(":");
					int countryIndex = Integer.valueOf(values[0]);
					int satisfactionValue = Integer.valueOf(values[1]);
					String country = DataParameters.countryIdByIndex
							.get(countryIndex);
					processedDestinations.add(country);
					satisfactionArray.add(satisfactionValue);
				}

				// insert start location
				int startCountryIndex = Integer.valueOf(startLocation);
				String country = DataParameters.countryIdByIndex
						.get(startCountryIndex);
				processedDestinations.add(0, country);
				satisfactionArray.add(0, 100);
			}

			// OPL execution
			results = runOPL(user, tripLength, strBudget, processedDestinations,
					satisfactionArray, startLocation);

		}

		// Store in servlet context and navigate to results.html
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
		HeuristicFactory factory = new HeuristicFactory(user);
		TimeTracker timer = new TimeTracker();
		factory.GRASPConstruction();
		timer.addLap("GRASP Constructor");
		
		user.setProcessingTime(timer.timeToHtml());
		return user.retrieveOptimalSolution();
	}

	public JSONObject runOPL(User user, int tripLength, String budget,
			ArrayList<String> selectedDestination,
			ArrayList<Integer> satisfactionArray, String startLocation) {
		
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

		File datFile = OPLFactory.generateDat(tripLength, budget,
				selectedDestination, satisfactionArray, startDestination);
		try {
			TimeTracker timer = new TimeTracker();
			OPLFactory.runOPL(user, datFile);
			user.setProcessingTime(timer.timeToHtml());
			//OPLFactory.cleanup(datFile);
			return OPLFactory.runOPL(user,datFile);
//			 OPLFactory.cleanup(datFile);
		} catch (Exception e) {
			JSONObject json_results = new JSONObject();
			json_results.put("Error", e.getMessage());
			
			return json_results;
		}
	}
}
