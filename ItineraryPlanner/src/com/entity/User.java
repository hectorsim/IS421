package com.entity;

import ilog.concert.IloException;
import ilog.concert.IloMap;
import ilog.concert.IloTuple;
import ilog.opl.IloOplElement;
import ilog.opl.IloOplModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ItineraryPlanner.DataParameters;
import com.ItineraryPlanner.DataParameters;
import com.heuristic.Solutions;

/**
 * Object for User Object
 * 
 * @author Leon
 *
 */
public class User {
	private Graph graph;
	private double budget;
	private int noOfStays;
	private HashMap<Integer, Integer> satisfactionLevels;

	public ArrayList<Solutions> solutionList;

	/**
	 * Constructor to initialize the User object
	 * 
	 * @param budget
	 * @param noOfStays
	 * @param satisfactionLevels
	 * @param graph
	 */
	public User(double budget, int noOfStays,
			HashMap<Integer, Integer> satisfactionLevels, Graph graph) {
		this.budget = budget;
		this.noOfStays = noOfStays;
		this.satisfactionLevels = satisfactionLevels;

		this.graph = graph;
		solutionList = new ArrayList<Solutions>();
	}

	/**
	 * Get the graph to plot
	 * 
	 * @return
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Set the graph to plot
	 * 
	 * @param graph
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Get the budget for the itinerary
	 * 
	 * @return
	 */
	public double getBudget() {
		return budget;
	}

	/**
	 * Set the budget for the itinerary
	 * 
	 * @param budget
	 */
	public void setBudget(double budget) {
		this.budget = budget;
	}

	/**
	 * Get the number of stays in total
	 * 
	 * @return
	 */
	public int getNoOfStays() {
		return noOfStays;
	}

	/**
	 * Set number of stays in total
	 * 
	 * @param noOfStays
	 */
	public void setNoOfStays(int noOfStays) {
		this.noOfStays = noOfStays;
	}

	/**
	 * Get satisfaction for all locations
	 * 
	 * @return
	 */
	public HashMap<Integer, Integer> getSatisfactionLevels() {
		return satisfactionLevels;
	}

	/**
	 * Set satisfaction level or all locations
	 * 
	 * @param satisfactionLevels
	 */
	public void setSatisfactionLevels(
			HashMap<Integer, Integer> satisfactionLevels) {
		this.satisfactionLevels = satisfactionLevels;
	}

	/**
	 * Calculate recommended cost
	 * 
	 * @return
	 */
	public double getRecommendedTotalCost(ArrayList<Vertex> recommendPath) {
		double totalCost = 0.0;
		int day = 0;

		for (int i = 0; i < recommendPath.size(); i++) {
			Vertex current = recommendPath.get(i);

			totalCost += current.getLivingCost() * current.getNoOfDays();
			day += current.getNoOfDays();

			if (i < recommendPath.size() - 1) {
				Vertex next = recommendPath.get(i + 1);
				
				HashMap<Integer, Double[]> adjList = current.getAdjList();
				
				Double[] priceList = adjList.get(next.getId());
				totalCost += priceList[day];
			}
		}

		return totalCost;
	}

	/**
	 * Calculate and get the total satisfaction for the recommended itinerary
	 * 
	 * @return
	 */
	public int getRecommendedTotalSatisfaction(ArrayList<Vertex> recommendPath) {
		int totalSatistaction = 0;

		for (Vertex visited : recommendPath) {
			int noOfDays = visited.getNoOfDays();
			int satisfaction = satisfactionLevels.get(visited.getId());

			int decreaseInUnit = DataParameters.unitDecreasePerLocationByIndex.get(visited.getId());
			;
			totalSatistaction += noOfDays*satisfaction - (((noOfDays*(noOfDays-1)) / 2) * decreaseInUnit);
		}

		return totalSatistaction;
	}

	/**
	 * Retrieve the number of days spents on the recommended tour
	 * 
	 * @param tour
	 * @return
	 */
	public int getRecommendedTotalDays(ArrayList<Vertex> recommendPath) {
		int numberOfDays = 0;

		for (Vertex visited : recommendPath) {
			numberOfDays += visited.getNoOfDays();
		}

		return numberOfDays;
	}

	/**
	 * Retrieve solution list
	 * 
	 * @return
	 */
	public ArrayList<Solutions> getSolutionList() {
		return solutionList;
	}

	/**
	 * Set solution list
	 * 
	 * @param solutionList
	 */
	public void setSolutionList(ArrayList<Solutions> solutionList) {
		this.solutionList = solutionList;
	}

	public void addSolution(Solutions solution) {
		this.solutionList.add(solution);
	}

	/**
	 * Convert the graph object of the user to String format
	 * 
	 * @return
	 */
	public String graphToString() {
		return this.graph.toString();
	}

	/**
	 * Convert object to string
	 */
	public String toString() {
		String text = "";

		text += "Maximum Budget: " + this.budget + "\n";
		text += "Number of stays: " + this.noOfStays + "\n";
		text += "Satisfaction For Locations:" + "\n";

		Iterator<Entry<Integer, Integer>> iter = this.satisfactionLevels
				.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry<Integer, Integer> values = iter.next();
			int locationId = values.getKey();
			int satisfactionLevel = values.getValue();

			text += this.graph.getVertex(locationId).getLocationName() + " - "
					+ satisfactionLevel + "\n";
		}

		return text;
	}

	/**
	 * Convert object to JSONObject
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json_User = new JSONObject();

		json_User.put("Budget", this.budget);
		json_User.put("NoOfStays", this.noOfStays);

		JSONObject json_satis = new JSONObject();
		Iterator<Entry<Integer, Integer>> iter = this.satisfactionLevels
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Integer> values = iter.next();
			int locationId = values.getKey();
			int satisfactionLevel = values.getValue();
			json_satis.put(locationId, satisfactionLevel);
		}
		json_User.put("Satisfaction", json_satis);
		json_User.put("GraphDetails", this.graph.toJSON());

		return json_User;
	}

	public JSONObject retrieveOptimalSolution() {
		Solutions bestSolution = null;

		for (Solutions solution : this.solutionList) {
			if (bestSolution == null) {
				bestSolution = solution;
			} else {
				if (bestSolution.getTotalPreferences() < solution
						.getTotalPreferences()) {
					bestSolution = solution;
				}
			}
		}

		return generateResults(bestSolution);
	}

	/**
	 * Generate overall results for the optimal path for user iitnerary
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject generateResults(Solutions solution) {
		JSONObject jsonHeuristicResults = new JSONObject();

		if (solution != null) {
			ArrayList<Vertex> path = solution.getPath();
			Graph graph = solution.getGraph();
			
			JSONArray jsonPath = new JSONArray();
			int day = 0;
			for (int i = 0; i < path.size(); i++) {
				JSONObject jsonVertex = new JSONObject();
				
				Vertex current = path.get(i);
				jsonVertex.put("location", current.getLocationName());
				jsonVertex.put("costOfLiving", "$" + current.getLivingCost() + " per day");
				jsonVertex.put("noOfDaysStay", current.getNoOfDays() + " Days");
				
				day += current.getNoOfDays();
				
				if (i < path.size()-1) {
					Vertex next = path.get(i+1);
					HashMap<Integer, Double[]> adjList = current.getAdjList();
					
					Double[] priceList = adjList.get(next.getId());
					double flightPrice = priceList[day];
					
					jsonVertex.put("flightPrice", "$" + flightPrice);
				} else {
					jsonVertex.put("flightPrice", "$0");
				}
				
				jsonPath.add(jsonVertex);
			}
			
			jsonHeuristicResults.put("path", jsonPath);
			jsonHeuristicResults.put("totalCost", solution.getTotalCost());
			jsonHeuristicResults.put("totalSatisfaction", solution.getTotalPreferences());
			jsonHeuristicResults.put("allLocations", graph.getAllLocationToJSON());
		} else {
			jsonHeuristicResults.put("Error", "No Optimal Solution Found!");
		}
		return jsonHeuristicResults;
	}
	public JSONObject generateOPL(Solutions solution) {
		ArrayList<Vertex> path = solution.getPath();
		JSONObject jsonHeuristicResults = new JSONObject();

		JSONArray jsonPath = new JSONArray();
		int day = 0;
		for (int i = 0; i < path.size(); i++) {
			JSONObject jsonVertex = new JSONObject();

			Vertex current = path.get(i);
			jsonVertex.put("location", current.getLocationName());
			jsonVertex.put("costOfLiving", "$" + current.getLivingCost()
					+ " per day");
			jsonVertex.put("noOfDaysStay", current.getNoOfDays() + " Days");

			day += current.getNoOfDays();

			if (i < path.size() - 1) {
				jsonVertex.put("flightPrice", "$" + current.flightOutCost);
			} else {
				jsonVertex.put("flightPrice", "$0");
			}

			jsonPath.add(jsonVertex);
		}

		jsonHeuristicResults.put("path", jsonPath);
		jsonHeuristicResults.put("totalCost", solution.getTotalCost());
		jsonHeuristicResults.put("totalSatisfaction", solution.getTotalPreferences());

		return jsonHeuristicResults;
	}

	public Solutions processOPL(IloOplModel opl){
		IloMap u_raw = opl.getElement("U").asIntMap();
		IloMap c_raw = opl.getElement("CostOfLivingOfLocation").asNumMap();
		IloMap f_raw = opl.getElement("PriceFromToOnDay").asIntMap();
		int enddate = opl.getElement("enddate").asInt();
		double budget = opl.getElement("budget").asNum();
				
		IloOplElement locations_raw = opl.getElement("Locations");
		String location_text = locations_raw.toStringDisplay();
		Pattern p = Pattern.compile("([A-Z]+)");
        Matcher m = p.matcher(location_text);
        ArrayList<String> locations = new ArrayList<String>();
        while(m.find()) {
        	locations.add(m.group());
        }
		
		String ujsontext = u_raw.toString();
		String cjsontext = c_raw.toString();
		String fjsontext = f_raw.toString();
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		try {
			JSONArray u = (JSONArray)(new JSONParser().parse(ujsontext));
			JSONArray c = (JSONArray)(new JSONParser().parse(cjsontext));
			JSONArray f = (JSONArray)(new JSONParser().parse(fjsontext));
			double flight = 0.0;
			double living = 0.0;
		    for(int d=0; d<enddate;d++) { // from 1..D-1
		        for(int l=0;l<locations.size();l++){
		        	long going = (long)((JSONArray)(u.get(l))).get(d);
		        	if(going==1){ // user is at location
		        		Vertex v = null;
		        		if(path.size()==0 || path.get(path.size()-1).id!=l){
			    			Vertex vadd = new Vertex(l, locations.get(l), DataParameters.getLocationName(locations.get(l)), (long)c.get(l), true);
			    			path.add(vadd);
			    			v = vadd;
			    			v.noOfDays = 1;
		        		} else {
		        			v = path.get(path.size()-1);
		        		}
		        		JSONArray currentLocation = (JSONArray)(u.get(l));
		        		living += (long)c.get(l);
		        		if(d+1>=enddate){
		        			continue;
		        		}
			     		if(((long)currentLocation.get(d+1))==1){ // the next day is in the same place
			    			v.noOfDays++;
			     		} else { // if not same place tmr, where are you flying to?
			     		     for(int j=0; j<locations.size();j++){
			     		    	long jgoing = (long)((JSONArray)(u.get(j))).get(d+1);
			     		     	if(jgoing==1){
			     		     		JSONArray where = (JSONArray)((JSONArray)f.get(l)).get(j);
			     		         	flight += (long)where.get(d);
			     		         	v.flightOutCost = (long)where.get(d);
			     		         	System.out.println(v.flightOutCost+"asds");
			              		}             		         
			     		     }        		
			     		}
		        	}
		        }   
		    }
		    for(Vertex z:path){
		    	System.out.println(z);
		    }
			Solutions solut = new Solutions(path, flight+living, (int) opl.getCplex().getObjValue());
		    return solut;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}