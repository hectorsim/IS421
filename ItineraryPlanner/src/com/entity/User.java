package com.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ItineraryPlanner.Constants;

/**
 * Object for User Object
 * @author Leon
 *
 */
public class User {
	public Graph graph;
	public double budget;
	public int noOfStays;
	public HashMap<Integer, Integer> satisfactionLevels;
	
	public ArrayList<Vertex> path;
	
	/**
	 * Constructor to initialize the User object
	 * @param budget
	 * @param noOfStays 
	 * @param satisfactionLevels
	 * @param graph 
	 */
	public User(double budget, int noOfStays, HashMap<Integer, Integer> satisfactionLevels, Graph graph) {
		this.budget = budget;
		this.noOfStays = noOfStays;
		this.satisfactionLevels = satisfactionLevels;
		
		this.graph = graph;
		
		path = new ArrayList<Vertex>();
		/** Dummy data **/
		path.add(this.graph.getVertex(1));
		
		Vertex v2 = this.graph.getVertex(2);
		v2.setNoOfDays(2);
		path.add(v2);
		
		Vertex v3 = this.graph.getVertex(3);
		v3.setNoOfDays(2);
		path.add(v3);
		
		Vertex v4 = this.graph.getVertex(4);
		v4.setNoOfDays(2);
		path.add(v4);
		
		Vertex v5 = this.graph.getVertex(5);
		v5.setNoOfDays(2);
		path.add(v5);
		
		Vertex v6 = this.graph.getVertex(7);
		v6.setNoOfDays(1);
		path.add(v6);
		
		path.add(this.graph.getVertex(1));
	}

	/** 
	 * Get the graph to plot
	 * @return
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Set the graph to plot
	 * @param graph
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Get the budget for the itinerary
	 * @return
	 */
	public double getBudget() {
		return budget;
	}

	/** 
	 * Set the budget for the itinerary
	 * @param budget
	 */
	public void setBudget(double budget) {
		this.budget = budget;
	}

	/**
	 * Get the number of stays in total
	 * @return
	 */
	public int getNoOfStays() {
		return noOfStays;
	}

	/**
	 * Set number of stays in total
	 * @param noOfStays
	 */
	public void setNoOfStays(int noOfStays) {
		this.noOfStays = noOfStays;
	}

	/**
	 * Get satisfaction for all locations
	 * @return
	 */
	public HashMap<Integer, Integer> getSatisfactionLevels() {
		return satisfactionLevels;
	}

	/**
	 * Set satisfaction level or all locations
	 * @param satisfactionLevels
	 */
	public void setSatisfactionLevels(HashMap<Integer, Integer> satisfactionLevels) {
		this.satisfactionLevels = satisfactionLevels;
	}
	
	/**
	 * Convert the graph object of the user to String format
	 * @return
	 */
	public String graphToString() {
		return this.graph.toString();
	}
	

	/**
	 * Calculate and retrieve the total cost for the itinerary
	 * @return
	 */
	public double getTotalCost() {
		double totalCost = 0.0;
		int day = 0;
		
		for (int i = 0; i < path.size(); i++) {
			Vertex current = path.get(i);
			
			totalCost += current.getLivingCost() * current.getNoOfDays();
			day += current.getNoOfDays();
			
			if (i < path.size()-1) {
				Vertex next = path.get(i+1);
				HashMap<Integer, Double[]> adjList = current.getAdjList();
				
				Double[] priceList = adjList.get(next.getId());
				totalCost += priceList[day];
			}
		}
		
		return totalCost;
	}
	
	/**
	 * Calculate and get the total satisfaction for the itinerary
	 * @return
	 */
	public int getTotalSatisfaction() {
		int totalSatistaction = 0;
		
		for (int i = 0; i < path.size(); i++) {
			Vertex visited = path.get(i);
			
			int noOfDays = visited.getNoOfDays();
			int satisfaction = satisfactionLevels.get(visited.getId()); 
			
			totalSatistaction += noOfDays*satisfaction - (((noOfDays*(noOfDays-1)) / 2) * Constants.DECREASE_IN_UNIT);
		}
		
		return totalSatistaction;
	}
	
	/**
	 * Convert object to string
	 */
	public String toString() {
		String text = "";
		
		text += "Maximum Budget: " + this.budget + "\n";
		text += "Number of stays: " + this.noOfStays + "\n";
		text += "Satisfaction For Locations:" + "\n";
		
		Iterator<Entry<Integer, Integer>> iter = this.satisfactionLevels.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<Integer, Integer> values = iter.next();
			int locationId = values.getKey();
			int satisfactionLevel = values.getValue();
			
			text += this.graph.getVertex(locationId).getLocationName() + " - " + satisfactionLevel + "\n";  
		}
		
		return text;
	}
	
	/**
	 * Convert object to JSONObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json_User = new JSONObject();
		
		json_User.put("Budget", this.budget);
		json_User.put("NoOfStays", this.noOfStays);
		
		JSONObject json_satis = new JSONObject();
		Iterator<Entry<Integer, Integer>> iter = this.satisfactionLevels.entrySet().iterator();
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

	/**
	 * Generate overall results for the optimal path for user iitnerary
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject generateResults() {
		JSONObject jsonHeuristicResults = new JSONObject();
		
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
		jsonHeuristicResults.put("totalCost", getTotalCost());
		jsonHeuristicResults.put("totalSatisfaction", getTotalSatisfaction());
		
		return jsonHeuristicResults;
	}
}