package com.ItineraryPlanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.entity.Graph;
import com.entity.Vertex;
import com.opencsv.CSVReader;

public class DataParameters {
	public static HashMap<Integer, String> countryIdByIndex;
	public static HashMap<String, Integer> countryIndexById;
	/*
	 * Default satisfaction and minimum day stay used by OPL computations
	 */
	public static HashMap<String, Integer> defaultSatisfactionValue;
	public static HashMap<String, Integer> minDayStay;
	public static HashMap<String, Integer> unitDecreasePerLocation;
	
	/*
	 *  Default satisfaction and minimum day stay used by Heuristics
	 */
	public static HashMap<Integer, Integer> defaultSatisfactionValueByIndex;
	public static HashMap<Integer, Integer> minDayStayByIndex;
	public static HashMap<Integer, Integer> unitDecreasePerLocationByIndex;
	
	/**
	 * Set the unit decrease for per location
	 * @param startLocationId
	 */
	public static void setUnitDecrement(int startLocationId, 
			HashMap<Integer, Integer> satisfactionValues, HashMap<Integer, Integer> minDatStay) {
		
		unitDecreasePerLocationByIndex = new HashMap<Integer, Integer>();
		unitDecreasePerLocation = new HashMap<String, Integer>();
		
		Location[] locationList = Location.values();
		
		for (int i = 0; i < locationList.length; i++) {
			String locationId = locationList[i].toString();
			int locationIndex = countryIndexById.get(locationId);
					
			if (locationIndex == startLocationId) {
				unitDecreasePerLocationByIndex.put(locationIndex, 100);
				unitDecreasePerLocation.put(locationId, 100);
			} else {
				int unitDecrease = -1;
				if (satisfactionValues.containsKey(locationIndex)) {
					unitDecrease = generateUnitDecrease(satisfactionValues.get(locationIndex), 
						minDatStay.get(locationIndex));
				} 
				
				unitDecreasePerLocationByIndex.put(locationIndex, unitDecrease);
				unitDecreasePerLocation.put(locationId, unitDecrease);				
			}
		}
	}
	
	/**
	 * Generate unit decrease for per location
	 * @param satValue
	 * @param minStayValue
	 * @return
	 */
	public static int generateUnitDecrease(int satValue, int minStayValue) {
		return (int) (Math.random()*((satValue / minStayValue) - 1));
	}
	
	/* Set price matrix */
	/**
	 * Set flight price from location i to location j according to the pricematrix.csv
	 * @param graph
	 * @param totalNumberOfDays
	 */
	public static void setPriceMatrix(Graph graph, int totalNumberOfDays) {
		CSVReader reader = null;
		
		try {
			// Read CSV file
			InputStream is = DataParameters.class.getResourceAsStream(Constants.PRICEMATRIXCSV);
			InputStreamReader isr = new InputStreamReader(is);
			reader = new CSVReader(isr);
			String[] record = null;
			
			while ((record = reader.readNext()) != null) {
				HashMap<Integer, Vertex> vertices = graph.getVertices();
				
				if (vertices.containsKey(DataParameters.countryIndexById.get(record[0]))) {
					Vertex currentCountry = graph.getVertexByLocationId(record[0]);
					
					Iterator<Integer> iter = vertices.keySet().iterator();
					
					while(iter.hasNext()) {
						Integer toLocationIndex = iter.next();
						String value = record[toLocationIndex];
						
						if (!value.equalsIgnoreCase("-")) {
							double flightPrice = Double.valueOf(value);
							
							Double[] priceRange = new Double[totalNumberOfDays];
							
							for (int i=0; i < totalNumberOfDays; i++) {
								priceRange[i] = flightPrice + DataParameters.generateSatisfationValue();
							}
							
							currentCountry.setAdj(toLocationIndex, priceRange);
						}
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Generate minimum stays for per location
	 */
	public static void generateMinStayForLocation(int startLocationId) {
		minDayStay =  new HashMap<String, Integer>();
		minDayStayByIndex = new HashMap<Integer, Integer>();
		
		Location[] locationList = Location.values();
		for (int i = 0; i < locationList.length; i++) {
			String locationId = locationList[i].toString();
			int locationIndex = countryIndexById.get(locationId);
			
			if (locationIndex == startLocationId) {
				minDayStay.put(locationId, 1);
				minDayStayByIndex.put(locationIndex, 0);
			} else {
				int minStay = generateMinStay();
				
				minDayStay.put(locationId, minStay);
				minDayStayByIndex.put(locationIndex, minStay);
			}
		}
	}
	
	/**
	 * Randomize minimum stay for per location
	 * @return
	 */
	private static int generateMinStay() {
		int range = (5 - 3) + 1;
		return (int) (Math.random() * range) + 3;
	}
	
	/**
	 * Allocate satisfaction value per location
	 */
	public static void generateDefaultSatisfactionValue(int startLocationId) {
		defaultSatisfactionValue =  new HashMap<String, Integer>();
		defaultSatisfactionValueByIndex	= new HashMap<Integer, Integer>();
		
		Location[] locationList = Location.values();
		for (int i = 0; i < locationList.length; i++) {
			String locationId = locationList[i].toString();
			int locationIndex = countryIndexById.get(locationId);
			
			if (locationIndex == startLocationId) {
				defaultSatisfactionValue.put(locationId, 100);
				defaultSatisfactionValueByIndex.put(countryIndexById.get(locationId), 100);
			} else {
				int satisfactionLevel = generateSatisfationValue();
			
				defaultSatisfactionValue.put(locationId, satisfactionLevel);
				defaultSatisfactionValueByIndex.put(countryIndexById.get(locationId), satisfactionLevel);
			}
			
		}
	}
	
	/**
	 * Randomize satisfaction value for per location
	 * @return
	 */
	// f(x) = -0.65x^2+5x+2
	public static int generateSatisfationValue() {
		double x = -1;
		while (!(1 <= x && x <= 2.) && !(5.6 <= x && x <= 7)) {
			x = (Math.random() * 10);
			// System.out.println(x);
		}
		int value = (int) (((-0.65) * Math.pow(x, 2) + 5 * x + 2) * 10);
		// System.out.println(x + "\t" + value);
		return value;
	}
	
 	
	/**
	 * Retrieve cost of living according to a location id
	 * @param locationId
	 * @return
	 */
	/* Get cost of living */
	public static double getCostOfLiving(String locationId) {
		
		Location loc = Location.valueOf(locationId);
		
		switch (loc) {
			case SIN: return 93;
			case PEK: return 48;
			case PVG: return 48;
			case HKG: return 75;
			case KIX: return 80;
			case HND: return 80;
			case ICN: return 82;
			case KHH: return 57;
			case TPE: return 57;
			case PNH: return 57;
			case REP: return 57;
			case DPS: return 39;
			case CGK: return 39;
			case KUL: return 45;
			case PEN: return 45;
			case RGN: return 53;
			case MNL: return 40;
			case BKK: return 46;
			case HKT: return 46;
			case HAN: return 41;
			case SGN: return 41;
			default: return 99999;
		}
	}
	
	/**
	 * Index all location in the list
	 * @return
	 */
	/* Location Indexing */
	public static void LocationIndexing() {
		countryIdByIndex = new HashMap<Integer, String>();
		countryIndexById = new HashMap<String, Integer>();
		
		Location[] locationList = Location.values();
		for (int i = 0; i < locationList.length; i++) {
			countryIndexById.put(locationList[i].toString(), (i+1));
			countryIdByIndex.put((i+1), locationList[i].toString());
		}
	}
	
	/**
	 * Retrieve all location id
	 * @return
	 */
	public static ArrayList<String> getAllLocations() {
		ArrayList<String> allDestinations = new ArrayList<String>();
		
		for (Location value : Location.values()) {
			allDestinations.add(value.toString());
		}
		
		return allDestinations;
	}
	
	/**
	 * Calculate and get the total number of locations
	 * @return
	 */
	public static int getNumberOfLocations() {
		return Location.values().length;
	}
	
	/**
	 * Retrieve the location full address by the location id
	 * @param locationId
	 * @return
	 */
	public static String getLocationName(String locationId) {
		if(locationId.endsWith("TWO")){
			locationId = locationId.replace("TWO", "");
		}
		Location loc = Location.valueOf(locationId);
		
		switch (loc) {
			case SIN: return "Singapore - Singapore Changi Airport";
			case PEK: return "Beijing - Beijing Capital International Airport";
			case PVG: return "Shanghai - Pudong International Airport";
			case HKG: return "Hong Kong - Hong Kong International Airport";
			case KIX: return "Osaka - Kansai International Airport";
			case HND: return "Tokyo - Tokyo International Airport";
			case ICN: return "Seoul - Incheon International Airport";
			case KHH: return "Kaohsiung - Kaohsiung International Airport";
			case TPE: return "Taipei - Taiwan Taoyuan International Airport";
			case PNH: return "Phnom Penh - Phnom Penh International Airport";
			case REP: return "Siem Reap - Siem Reap International Airport";
			case DPS: return "Bali - Ngurah Rai International Airport";
			case CGK: return "Jakarta - Jakarta Airport";
			case KUL: return "Kuala Lumpur - Kuala Lumpur International Airport";
			case PEN: return "Penang - Penang International Airport";
			case RGN: return "Yangon - Yangon International Airport";
			case MNL: return "Manila - Ninoy Aquino International Airport";
			case BKK: return "Bangkok - Suvarnabhumi Airport";
			case HKT: return "Phuket - Phuket International Airport";
			case HAN: return "Hanoi - Noi Bai International Airport";
			case SGN: return "Ho Chi Minh City - Ho Chi Minh City Airport";
			default: return "Location code cannot be found";
		}
	}

	/**
	 * Enumeration object for location
	 * @author Leon
	 *
	 */
	public enum Location {
		SIN,
		PEK,
		PVG,
		HKG,
		KIX,
		HND,
		ICN,
		KHH,
		TPE,
		PNH,
		REP,
		DPS,
		CGK,
		KUL,
		PEN,
		RGN,
		MNL,
		BKK,
		HKT,
		HAN,
		SGN
		
	}
}
