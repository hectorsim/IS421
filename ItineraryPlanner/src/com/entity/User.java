package com.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ItineraryPlanner.DataParameters;
import com.heuristic.Solutions;

/**
 * Object for User Object
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
		solutionList = new ArrayList<Solutions>();
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
	 * Calculate recommended cost
	 * @return
	 */
	public double getRecommendedTotalCost(ArrayList<Vertex> recommendPath) {
		double totalCost = 0.0;
		int day = 0;
		
		for (int i = 0; i < recommendPath.size(); i++) {
			Vertex current = recommendPath.get(i);
			
			totalCost += current.getLivingCost() * current.getNoOfDays();
			day += current.getNoOfDays();
			
			if (i < recommendPath.size()-1) {
				Vertex next = recommendPath.get(i+1);
				System.out.println(current.getLocationName() + " - " + next.getLocationName());
				HashMap<Integer, Double[]> adjList = current.getAdjList();
				
				Double[] priceList = adjList.get(next.getId());
				totalCost += priceList[day];
			}
		}
		
		return totalCost;
	}
	
	/**
	 * Calculate and get the total satisfaction for the recommended itinerary
	 * @return
	 */
	public int getRecommendedTotalSatisfaction(ArrayList<Vertex> recommendPath) {
		int totalSatistaction = 0;
		
		for (Vertex visited : recommendPath) {
			int noOfDays = visited.getNoOfDays();
			int satisfaction = satisfactionLevels.get(visited.getId()); 
			
			totalSatistaction += noOfDays*satisfaction - (((noOfDays*(noOfDays-1)) / 2) * DataParameters.unitDecreasePerLocationByIndex.get(visited.getId()));
		}
		
		return totalSatistaction;
	}
	
	/**
	 * Retrieve the number of days spents on the recommended tour
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
	 * @return
	 */
	public ArrayList<Solutions> getSolutionList() {
		return solutionList;
	}

	/**
	 * Set solution list
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

	public JSONObject retrieveOptimalSolution() {
		Solutions bestSolution = null;
		
		for (Solutions solution : this.solutionList) {
			if (bestSolution == null) {
				bestSolution = solution;
			} else {
				if (bestSolution.getTotalPreferences() < solution.getTotalPreferences()) {
					bestSolution = solution;
				}
			}
		}
		
		return generateResults(bestSolution);
	}
	
	/**
	 * Generate overall results for the optimal path for user iitnerary
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
}