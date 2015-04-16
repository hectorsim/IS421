package com.ItineraryPlanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import com.entity.Graph;
import com.entity.User;
import com.entity.Vertex;

public class SystemFactory {
	
	@SuppressWarnings("unchecked")
	public static JSONObject datRetrieval() {
		String datString = Constants.FLIGHTDATSTRING;
		String dataPath = Constants.FLIGHTDATAPATH;
		String[] params = Constants.PARAMS;
		
		JSONObject data = new JSONObject();
		
		try {
//			DataInputStream input = new DataInputStream(new FileInputStream(context + dataPath + datString));
//			BufferedReader datReader = new BufferedReader(new InputStreamReader(input));
			
			InputStream input = SystemFactory.class.getResourceAsStream(dataPath+datString);
			BufferedReader datReader = new BufferedReader(new InputStreamReader(input));
			
			boolean isIncluded = false;
			boolean readNextLine = false;
			int paramCount = 0;
			
			String value = "";
			String strLine = "";
			
			// Convert all string from .dat into json
			while ((strLine = datReader.readLine()) != null) {
				if (readNextLine) {
					value += strLine.trim();
				} else {
					if (paramCount < params.length && strLine.split("=")[0].trim().equalsIgnoreCase(params[paramCount])) {
						value = strLine.split("=")[1].trim();
						isIncluded = true;
					}
				}
				
				
				if (isIncluded) {
					// Check whether is the data marks the end of the line
					if (strLine.contains(";")) {
						data.put(params[paramCount], value.split(";")[0]);
						paramCount++;
						
						// Reset all boolean
						readNextLine = false;
						isIncluded = false;
					} else {
						// Read the next line
						readNextLine = true;
						continue;
					}
				}
			}
			
			input.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(dataPath + datString + " cannot be found in the system");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public static void formatData(JSONObject rawData) {
		
		String[] params = Constants.PARAMS;
		
		JSONObject json_Satis = new JSONObject();
		
		// Format for individual location information
		String listOfLocation = removeBrackets((String) rawData.get(params[Constants.INDEX_FOR_LOC]));
		String[] locationArray = listOfLocation.split(",");
		
		Graph graph = new Graph();
		
		// Format location data and flight path into proper structure
		for (int i = 0; i < locationArray.length; i++) {
		
			// Retrieve cost of living
			String livingCostArray = removeBrackets((String) rawData.get(params[Constants.INDEX_FOR_COSTLIVING]));
			double costOfLiving = Double.valueOf(livingCostArray.split(",")[i]);
			
			// Creation of vertex according to the given data
			Vertex vertex = new Vertex((i+1), locationArray[i], 
					DataParameters.getLocationName(locationArray[i]), costOfLiving);
			
			// Add vertex into the graph
			graph.addVertex(vertex.getId(), vertex);
			
			// Retrieve Satisfaction
			String SatisfactionArray = removeBrackets((String) rawData.get(params[Constants.INDEX_FOR_SATISFACTION]));
			int location_Satis = Integer.valueOf(SatisfactionArray.split(",")[i]);
			json_Satis.put(vertex.getId(), location_Satis);
		}

		// Format for flight path and flight cost from location l to location j
		String flightDetails = removeBrackets((String) rawData.get(params[Constants.FLIGHT_COST]));
		String[] flightDetailsArray = flightDetails.split("]]");
				
		// Format flight cost according to adjacent list using Graph object
		graph = formatFlightcost(graph, locationArray, flightDetailsArray);
		
		Constants.GRAPH = graph;
		Constants.DEFAULT_LOCATION_SATISFACTION = json_Satis;
		
		Constants.DECREASE_IN_UNIT = Integer.valueOf((String) rawData.get(params[Constants.UNIT_DECREASE]));
	}
	
	public static Graph formatFlightcost(Graph graph, String[] locationArray, String[] flightDetailsArray) {
		
		// Individual vertex
		for (int i = 0; i < locationArray.length; i++) {
			Vertex vertex_i = graph.getVertexByLocationId(locationArray[i]);
			
			// Retrieve flight cost for per day
			String flightCosts = flightDetailsArray[i] + "]";
			flightCosts = flightCosts.substring(flightCosts.indexOf("[")+1, flightCosts.length());
			String[] flightArray = flightCosts.split("]");
			
			NextVertex: for (int j = 0; j < locationArray.length; j++) {
				Vertex vertex_j = graph.getVertexByLocationId(locationArray[j]);
				
				String daysPrices = removeBrackets(flightArray[j]);
				String[] daysArray = daysPrices.split(" ");
				
				// Convert string of flight prices to double of flight prices
				Double[] priceArray = new Double[daysArray.length];
				for (int k = 0; k < priceArray.length; k++) {
					double priceDay = Double.valueOf(daysArray[k]);
					
					// If any value in the array is on max value, vertex j does not link to vertex i
					if (priceDay >= Constants.maxValue) {
						continue NextVertex;
					} else {
						priceArray[k] = priceDay;
					}
				}
				
				vertex_i.setAdj(vertex_j.getId(), priceArray);
			}
		}
		
		return graph;
	}
	
	public static String removeBrackets(String value) {
		
		if (value.contains("{")) {
			value = value.substring(value.indexOf("{")+1, value.length());
			
			if (value.contains("}")) {
				value = value.substring(0, value.lastIndexOf("}"));
			}
			
		} else if  (value.contains("[")) {
			value = value.substring(value.indexOf("[")+1, value.length());
			
			if (value.contains("]")) {
				value = value.substring(0, value.lastIndexOf("]"));
			}			
			
		}
		
		return value;
	}
	
	/**
	 * Default initialization of User
	 * @param budget
	 * @param noOfStays
	 * @param startLocation
	 * @return User
	 */
	public static User intializesUser(double budget, int noOfStays, int startLocation) {
		// Get default satisfaction level
		Graph graph = Constants.GRAPH;
		graph.setStartLocationId(startLocation);
		
		return new User(budget, noOfStays, getDefaultSatisfactionLevel(), graph);
	}
	
	/**
	 * Initialization of user with their preferences
	 * @param budget
	 * @param noOfStays
	 * @param startLocation
	 * @param preferences
	 * @return User
	 */
	public static User intializesUser(double budget, int noOfStays, int startLocation, String[] preferences) {
		Graph graph = new Graph();
		
		HashMap<Integer, Integer> satisfactionValue = new HashMap<Integer, Integer>();
		
		// Set start location
		Vertex startVertex = Constants.GRAPH.getVertex(startLocation);
		satisfactionValue.put(startVertex.getId(), 0);
		
		graph.addVertex(startVertex.getId(), startVertex);
		graph.setStartLocationId(startVertex.getId());
		
		for (String value : preferences) {
			String[] values = value.split(":");
			satisfactionValue.put(Integer.valueOf(values[0]), Integer.valueOf(values[1]));
			
			Vertex v = Constants.GRAPH.getVertex(Integer.valueOf(values[0]));
			graph.addVertex(v.getId(), v);
		}
		
		return new User(budget, noOfStays, satisfactionValue, graph);
	}
	
	public static HashMap<Integer, Integer> getDefaultSatisfactionLevel() {
		HashMap<Integer, Integer> satisfactionValue = new HashMap<Integer, Integer>();
		
		 @SuppressWarnings("unchecked")
		Iterator<Entry<Integer,Integer>> iter = Constants.DEFAULT_LOCATION_SATISFACTION.entrySet().iterator();
		 while(iter.hasNext()) {
			 Map.Entry<Integer,Integer> values = iter.next();
			 satisfactionValue.put(values.getKey(), values.getValue());
		 }
		 
		 return satisfactionValue;
	}
}
