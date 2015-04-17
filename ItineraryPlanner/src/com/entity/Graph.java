package com.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ItineraryPlanner.DataParameters;

public class Graph {
	
	/**
	 * Hash that contains vertices
	 */
	private HashMap<Integer, Vertex> vertices;
	/**
	 * Get start location
	 */
	private int startLocationId;
	/**
	 * Constructor to initialize the graph object
	 */
	private HashMap<Integer, Double> prefScore = null; 
	
	public Graph() {
		this.vertices = new HashMap<Integer, Vertex>();
	}
	
	/**
	 * Retrieve hash of vertices
	 * @return
	 */
	public HashMap<Integer, Vertex> getVertices() {
		return this.vertices;
	}
	
	/**
	 * Retrieve an array of vertices
	 * @return
	 */
	public ArrayList<Vertex> getListOfVertex() {
		ArrayList<Vertex> nameList = new ArrayList<Vertex>();
		
		Iterator<Vertex> iter = this.vertices.values().iterator();
		while (iter.hasNext()) {
			nameList.add(iter.next());
		}
		
		return nameList;
	}
	
	/**
	 * get the vertex from the graph
	 * @param key
	 * @return
	 */
	public Vertex getVertex(int key) {
		return this.vertices.get(key);
	}
	
	public Vertex getVertexByLocationId(String locationId) {
		Iterator<Vertex> iter = this.vertices.values().iterator();
		
		while (iter.hasNext()) {
			Vertex v = iter.next();
			
			if (v.getLocationId().equalsIgnoreCase(locationId)) {
				return v;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a new vertex into the graph
	 * @param key
	 * @param value
	 */
	public void addVertex(int key, Vertex value) {
		this.vertices.put(key, value);
	}
	
	/**
	 * Remove a vertex from the graph by the key
	 * @param key
	 */
	public void removeVertex(int key) {
		this.vertices.remove(key);
	}
	
	/**
	 * Get start location id
	 * @return
	 */
	public int getStartLocationId() {
		return startLocationId;
	}

	/**
	 * Get the vertex for the start locatio
	 * @return
	 */
	public Vertex getStartVertex() {
		return this.vertices.get(this.startLocationId);
	}
	
	/**
	 * Set the start location id
	 * @param startLocationId
	 */
	public void setStartLocationId(int startLocationId) {
		this.startLocationId = startLocationId;
	}
	
	/**
	 * Retrieve the preference scoring
	 */
	public HashMap<Integer, Double> getPreferenecScores() {
		return this.prefScore;
	}
	
	/**
	 * Generate preferences score
	 * @param noOfDays
	 * @param preferences
	 */
	public void generatePreferenceScore(int noOfDays, HashMap<Integer, Integer> preferences) {
		this.prefScore = new HashMap<Integer, Double>();
		
		Iterator<Entry<Integer, Vertex>> iter = this.vertices.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<Integer, Vertex> values = iter.next();
			int index = values.getKey();
			Vertex v = values.getValue();
			
			double preferenceScore = calculateCPScore(index, noOfDays, preferences.get(index), v.getLivingCost()); 
			this.prefScore.put(index, preferenceScore);
		}
	}
	
	/**
	 * Calculation of preference score for individual locations
	 * @param noOfDays
	 * @param preferenceScore
	 * @param livingCost
	 * @return
	 */
	public double calculateCPScore(int locationIndex, int noOfDays, int preferenceScore, double livingCost) {
		double score = 0;
		
		for (int i=1; i<=noOfDays; i++) {
			score += Math.round((i * livingCost * 100 / ((i * preferenceScore)-(i * DataParameters.unitDecreasePerLocationByIndex.get(locationIndex) * (i-1) / 2))));
		}
		
		return score/noOfDays;
	}
	
	/**
	 * Reset number of days for all locations
	 */
	public void resetNoOfDays() {
		for(Vertex v : this.vertices.values()) {
			v.resetNoOfDay();
		}
	}
	
	/**
	 * Print out graph data structure
	 */
	public String toString() {
		String text = "";
				
		Iterator<Entry<Integer, Vertex>> iter = this.vertices.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<Integer, Vertex> value = (Map.Entry<Integer, Vertex>) iter.next();
			
			int vertexId = value.getKey();
			Vertex v = value.getValue();
			
			text += "Location " + vertexId + " : \n";
			text += v.toString() + "\n\n";
		}
		
		return text;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSON() {
		JSONArray dataArray = new JSONArray();
		
		Iterator<Vertex> iter = this.vertices.values().iterator();
		
		while (iter.hasNext()) {
			Vertex v = iter.next();
			dataArray.add(v.toJSON());
		}
		
		return dataArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getAllLocationToJSON() {
		JSONArray jsonArray = new JSONArray();
		
		for (Vertex v : this.vertices.values()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("location", v.getLocationName());
			jsonObject.put("costOfLiving", v.getLivingCost());
			jsonObject.put("noOfDaysStay", v.getNoOfDays());
			
			jsonArray.add(jsonObject);
		}
		
		return jsonArray;
	}
}
