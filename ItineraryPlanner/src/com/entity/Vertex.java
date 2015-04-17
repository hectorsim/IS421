package com.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ItineraryPlanner.Constants;

/**
 * A location node for the graph
 * @author Leon
 */
public class Vertex {
	int id;
	String locationId;
	String locationName;
	double livingCost;
	double flightOutCost;
	HashMap<Integer, Double[]> adjList;
	
	private int minDays;
	public int noOfDays;
	private boolean isVisited;
	
	/**
	 * Constructor to initialize a Vertex Object
	 * @param id
	 * @param locationId
	 * @param locationName
	 * @param livingCost
	 */
	public Vertex(int id, String locationId, String locationName, double livingCost) {
		this.id = id;
		this.locationId = locationId;
		this.locationName = locationName;
		this.livingCost = livingCost;
		this.adjList = new HashMap<Integer, Double[]>();
		
		minDays = 0;
		noOfDays = 0;
		this.isVisited = false;
	}
	
	/**
	 * Constructor to initialize a Vertex Object
	 * @param id
	 * @param locationId
	 * @param locationName
	 * @param livingCost
	 * @param isVisited
	 */
	public Vertex(int id, String locationId, String locationName, double livingCost, boolean isVisited) {
		this.id = id;
		this.locationId = locationId;
		this.locationName = locationName;
		this.livingCost = livingCost;
		this.adjList = new HashMap<Integer, Double[]>();
		
		minDays = 0;
		noOfDays = 0;
		this.isVisited = isVisited;
	}

	/**
	 * Get the id of the vertice
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id for the vertex
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	/**
	 * Get location id
	 * @return
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * Set location id
	 * @param locationId
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * Get location name for location search
	 * @return
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * Set location name for location search
	 * @param locationName
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * Get the living cost of the vertex
	 * @return
	 */
	public double getLivingCost() {
		return livingCost;
	}

	/**
	 * Set the living cost of the vertex
	 * @param livingCost
	 */
	public void setLivingCost(double livingCost) {
		this.livingCost = livingCost;
	}

	/**
	 * Get all adjacent vertex
	 * @return
	 */
	public HashMap<Integer, Double[]> getAdjList() {
		return this.adjList;
	}

	/**
	 * Add a new adjacent vertex connected to current
	 * @param id
	 * @param flightCost
	 */
	public void setAdj(int id, Double[] flightCosts) {
		this.adjList.put(id, flightCosts);
	}
	
	/**
	 * Add a list of adjacent vertex connected to current
	 * @param adjList
	 */
	public void setAllAdj(HashMap<Integer, Double[]> adjList) {
		this.adjList = adjList;
	}
	
	/**
	 * Check whether location is connect to this vertex
	 * @param locationId
	 * @return
	 */
	public boolean isConnected(int locationId) {
		return this.adjList.containsKey(locationId);
	}
	/**
	 * Delete a adjacent vertex connected to current
	 * @param id
	 */
	public void deleteAdj(int id) {
		this.adjList.remove(id);
	}
	
	/**
	 * @return the minDays
	 */
	public int getMinDays() {
		return minDays;
	}

	/**
	 * @param minDays the minDays to set
	 */
	public void setMinDays(int minDays) {
		this.minDays = minDays;
	}

	/**
	 * Get the number of days to stay in the vertex
	 * @return
	 */
	public int getNoOfDays() {
		return noOfDays;
	}

	/**
	 * Set the number of days to stay in the vertex
	 * @param noOfDays
	 */
	public int setNoOfDays(int noOfDays) {
		if (noOfDays <= this.minDays) {
			this.noOfDays = this.minDays;
			return this.noOfDays;
		} else {
			this.noOfDays = noOfDays;
			return this.noOfDays;
		}
	}

	/** 
	 * Reset the value of number of days
	 */
	public void resetNoOfDay() {
		this.noOfDays = 0;
	}
	
	/**
	 * Get the status whether is the vertex visited
	 * @return
	 */
	public boolean isVisited() {
		return isVisited;
	}

	/**
	 * Set whether the vertex is being visited
	 * @param isVisited
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
	/**
	 * Print out vertex information
	 */
	public String toString() {
		String text = "Id Number: " + this.id + "\n";
		text += "Location Id: " + this.locationId + "\n";
		text += "Location Address: " + this.locationName + "\n";
		text += "Living Cost: " + this.livingCost + "\n";
		text += "Flight Cost: " + this.flightOutCost + "\n";
		text += "Adjacent Locations: \n";
		
		if (this.adjList.size() > 0) {
			Iterator<Entry<Integer, Double[]>> iter = this.adjList.entrySet().iterator();
			 
			while (iter.hasNext()) {
				Map.Entry<Integer, Double[]> value = (Map.Entry<Integer, Double[]>) iter.next();
				
				int vertexId = value.getKey();
				Double[] costArray = value.getValue();
				
				Vertex v = Constants.GRAPH.getVertex(vertexId);
				
				text += v.getLocationId() + " : ";
				
				for (int i=0; i < costArray.length; i++) {
					text += "Day " + (i+1) + " - " + costArray[i] + ", ";
				}
				text += "\n";
			}
			
		} else {
			text += "No adjacent location found \n";
		}
		
		return text;
	}
	
	/**
	 * Retrieve an json object for the current vertex
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject vertexLocation = new JSONObject();
		vertexLocation.put("id", this.id);
		vertexLocation.put("locationId", this.locationId);
		vertexLocation.put("locationAddress", this.locationName);
		vertexLocation.put("livingCost", this.livingCost);
		vertexLocation.put("flightCostOut", this.flightOutCost);
		
		JSONArray adjList_json = new JSONArray();
		
		if (this.adjList.size() > 0) {
			Iterator<Entry<Integer, Double[]>> iter = this.adjList.entrySet().iterator();
			 
			while (iter.hasNext()) {
				JSONObject adjLocation = new JSONObject();
				
				Map.Entry<Integer, Double[]> value = (Map.Entry<Integer, Double[]>) iter.next();
				
				int vertexId = value.getKey();
				Double[] costArray = value.getValue();
				
				adjLocation.put("VertexId", vertexId);
				
				JSONArray flightCosts = new JSONArray();
				for (double cost : costArray) {
					flightCosts.add(cost);
				}
				adjLocation.put("flightCost", flightCosts);
				
				adjList_json.add(adjLocation);
			}
		} 
		
		vertexLocation.put("adjacentList", adjList_json);
		
		return vertexLocation;
	}
}
