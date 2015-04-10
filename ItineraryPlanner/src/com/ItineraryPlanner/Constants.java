package com.ItineraryPlanner;
import org.json.simple.JSONObject;

import com.entity.Graph;

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
	public final static int INDEX_FOR_COSTLIVING = 1;
	public final static int INDEX_FOR_SATISFACTION = 2;
	public final static int UNIT_DECREASE = 3;
	public final static int FLIGHT_COST = 4;
	
	public final static double maxValue = 99999.0;
	
	/**
	 * Constant Data Variables
	 */
	public static Graph GRAPH;
	public static JSONObject DEFAULT_LOCATION_SATISFACTION;
	
	public static int DECREASE_IN_UNIT;
}
