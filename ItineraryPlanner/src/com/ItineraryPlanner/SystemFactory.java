package com.ItineraryPlanner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SystemFactory {
	
	public static JSONObject datRetrieval(String context) {
		String datString = Constants.DATSTRING;
		String dataPath = Constants.DATAPATH;
		String[] params = Constants.PARAMS;
		
		JSONObject data = new JSONObject();
		
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(context + dataPath + datString));
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
		
		
		return formatData(data);
	}
	
	public static JSONObject formatData(JSONObject rawData) {
		
		String[] params = Constants.PARAMS;
		
		int satisfactionUnitDecrease = Constants.UNIT_DECREASE;
		int locationName = Constants.INDEX_FOR_LOC;
		int[] locationInformationIndices = Constants.INDEX_FOR_LOCATION_INFO;
		int flightCost = Constants.FLIGHT_COST;
		
		JSONObject locationDatas = new JSONObject();
		
		// Format for individual location information
		String listOfLocation = removeBrackets((String) rawData.get(params[locationName]));
		String[] locationArray = listOfLocation.split(",");
		
		// Format for flight path and flight cost from location l to location j
		String flightDetails = removeBrackets((String) rawData.get(params[flightCost]));
		String[] flightDetailsArray = flightDetails.split("]]");
		
		// Format location data and flight path into proper structure
		for (int i = 0; i < locationArray.length; i++) {
			JSONObject locationInfo = new JSONObject();
			
			for (int j = 0; j < locationInformationIndices.length; j++) {
				
				String value = removeBrackets((String) rawData.get(params[locationInformationIndices[j]]));
				locationInfo.put(params[locationInformationIndices[j]], value.split(",")[i]);
			}
			
			JSONObject jsonFlightCost = formatFlightCost(locationArray, flightDetailsArray[i] + "]");
			locationInfo.put("adjList", jsonFlightCost);
			
			locationDatas.put(locationArray[i], locationInfo);
		}

		// Parse all information formatted into one JSON
		JSONObject formattedData = new JSONObject();
		formattedData.put("DATAS", locationDatas);
		formattedData.put("DecreaseInUnit", rawData.get(params[satisfactionUnitDecrease]));
		
		return formattedData;
	}
	
	public static JSONObject formatFlightCost(String[] locationArray, String flightCosts) {
		JSONObject jsonFlightCost = new JSONObject();
		
		flightCosts = flightCosts.substring(flightCosts.indexOf("[")+1, flightCosts.length());
		String[] flightArray = flightCosts.split("]");
		
		for (int i = 0; i < locationArray.length; i++) {
			String days = removeBrackets(flightArray[i]);
			String[] daysArray = days.split(" ");
			
			JSONArray json_daysArray = new JSONArray();
			for (String value : daysArray) {
				json_daysArray.add(value);
			}
			
			jsonFlightCost.put(locationArray[i], json_daysArray);
		}
		
		return jsonFlightCost;
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
}
