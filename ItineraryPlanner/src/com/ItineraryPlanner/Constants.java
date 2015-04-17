package com.ItineraryPlanner;

import com.entity.Graph;

public class Constants {
	
	/**
	 * Data path for heuristics
	 */
	public final static String FLIGHTDATAPATH = "/data/";
	public final static String FLIGHTDATSTRING = "flightdata.dat";
	
	/**
	 * Data path for OPL
	 */
	public static final int satisfactionDecreaseStep = 10;
	public static final String PRICEMATRIXCSV = "/data/price_matrix.csv";
	public static final int dashValue = 99999;
	
	public static final String OPL_DATADIR = "/itineraryplanner/";
	public static final String OPL_MODSTRING = "warehouse.mod";
	public static final String OPL_DATSTRING = "travel_main.dat";

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
}
