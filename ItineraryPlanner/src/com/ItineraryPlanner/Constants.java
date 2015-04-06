package com.ItineraryPlanner;

import org.json.simple.JSONObject;

public class Constants {
	public final static String DATAPATH = "data\\";
	public final static String DATSTRING = "flightdata.dat";
	
	// Retrieval of data must be in sequence according to .dat file
	public final static String[] PARAMS = {
		"Locations",
		"CostOfLivingOfLocation",
		"InitialSatisfactionOfLocation",
		"UnitDecreaseInSatisfactionPerDay",
		"PriceFromToOnDay"
	};
	
	public final static int INDEX_FOR_LOC = 0;
	public final static int[] INDEX_FOR_LOCATION_INFO = {1,2};
	public final static int UNIT_DECREASE = 3;
	public final static int FLIGHT_COST = 4;
	
	/**
	 * Constant Data Variables
	 */
	public static JSONObject JSONDATA;
	public static int DECREASE_IN_UNIT;
}
