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
		System.out.println(rawData.toJSONString());
		
		String[] params = Constants.PARAMS;
		int[] locationInformationIndices = Constants.INDEX_FOR_LOCATION;
		int satisfactionUnitDecrease = Constants.UNIT_DECREASE;
		int flightCost = Constants.FLIGHT_COST;
		
		JSONObject formattedData = new JSONObject();
		
		JSONArray locationsArray = new JSONArray();
		
		String firstIndex = (String) rawData.get(params[locationInformationIndices[0]]);
		String[] stringArray = firstIndex.split(",");
		
		for (int i = 0; i < stringArray.length; i++) {
			JSONObject locationInfo = new JSONObject();
			
			for (int j = 0; j < locationInformationIndices.length; j++) {
				
				String value = (String) rawData.get(params[locationInformationIndices[j]]);
				locationInfo.put(params[locationInformationIndices[j]], value.split(",")[i]);
			}
			
			locationsArray.add(locationInfo);
		}
				
		System.out.println(locationsArray);
		return formattedData;
	}
}
