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

import com.ItineraryPlanner.Constants;
import com.ItineraryPlanner.DataParameters;
import com.ItineraryPlanner.SystemFactory;
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
		
		/* Preparation of heuristic data and OPL data
		 * Requires to create user object to generate results from OPL and Heuristics
		 */
		User user;
		String[] selectedLocations = null;
		
		if (isDestination != null) {
			selectedLocations = req.getParameterValues("locationList");
			
			user = SystemFactory.intializesUser(Double.valueOf(strBudget),
					tripLength,
					Integer.valueOf(startLocation), selectedLocations);
		} else {
			user = SystemFactory.intializesUser(Double.valueOf(strBudget),
					tripLength,
					Integer.valueOf(startLocation));
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
			ArrayList<String> satisfactionArray = new ArrayList<String>();
			
			if (selectedLocations == null) {
				destinations = DataParameters.getAllLocations();
			} else {
				for (String locations : selectedLocations) {
					String[] values = locations.split(":");
					processedDestinations.add(Integer.valueOf(values[0]));
					satisfactionArray.add(Integer.valueOf(values[1]));
					
					Vertex v = Constants.GRAPH.getVertex(Integer.valueOf(values[0]));
					graph.addVertex(v.getId(), v);
				}
				// String code = DataParameters.locationIdByIndex(Integer.parseInt());

				// destinations.add()
			}


			for (int i = 0; i < destinations.size(); i++) {
				String airport = destinations.get(i);
				System.out.println(airport + "\t" + startLocation);
				if (i == Integer.parseInt(startLocation)) {
					startDestination = airport;
					processedDestinations.add(0, airport);
				} else {
					processedDestinations.add(airport);
				}
			}
			
			// OPL execution
			runOPL(tripLength, strBudget, processedDestinations, satisfactionArray, startLocation);
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
		factory.GRASPConstruction();
		return user.retrieveOptimalSolution();
	}

	public void runOPL(int tripLength, String budget,
			ArrayList<String> selectedDestination, ArrayList<String> satisfactionArray, String startLocation) {
		
		String startDestination = null;

		for (String codes : airportCodes)
			System.out.print(codes + "\t");

		File datFile = OPLFactory.generateDat(tripLength, budget, selectedDestination, satisfactionArray,
				startDestination);
		try {
			OPLFactory.runOPL(datFile);
			// OPLFactory.cleanup(datFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
