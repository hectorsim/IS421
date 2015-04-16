package com.ItineraryPlanner;
import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.entity.Graph;

public class Constants {
	public static final String FILESEPARATOR = File.separator;
	/**
	 * Data path for heuristics
	 */
	public final static String FLIGHTDATAPATH = "/data/";
	public final static String FLIGHTDATSTRING = "flightdata.dat";
	
	/**
	 * Data path for OPL
	 */
	public static final int dashValue = 99999;
	public static final int satisfactionDecreaseStep = 10;
	public static final String PRICEMATRIXCSV = "/data/price_matrix.csv";
	
	public static final String OPL_DATADIR = "/itineraryplanner/";
	public static final String OPL_MODSTRING = "warehouse.mod";
	public static final String OPL_DATSTRING = "travel_main.dat";
	
	public static final ArrayList<String> ALL_DESTINATIONS = new ArrayList<String>() {
		{
			add("SIN");
			add("PEK");
			add("PVG");
			add("HKG");
			add("KIX");
			add("HND");
			add("ICN");
			add("KHH");
			add("TPE");
			add("PNH");
			add("REP");
			add("DPS");
			add("CGK");
			add("KUL");
			add("PEN");
			add("RGN");
			add("MNL");
			add("BKK");
			add("HKT");
			add("HAN");
			add("SGN");
		}
	};

	
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
