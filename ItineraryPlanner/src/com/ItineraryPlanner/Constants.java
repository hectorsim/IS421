package com.ItineraryPlanner;
import java.io.File;

import org.json.simple.JSONObject;

import com.entity.Graph;

public class Constants {
	public static final String FILESEPARATOR = File.separator;
	/**
	 * Data path for heuristics
	 */
	public final static String DATAPATH = "data\\";
	public final static String DATSTRING = "flightdata.dat";
	
	/**
	 * Data path for OPL
	 */
	public static final String MODSTRING = "warehouse.mod";
	public static final String DATSTRING2 = "simpleWarehouse.dat";
	
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
	 * Constant Data Variables template
	 */
	public static Graph GRAPH;
	public static JSONObject DEFAULT_LOCATION_SATISFACTION;
	public static int DECREASE_IN_UNIT;
}
