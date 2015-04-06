package com.ItineraryPlanner;

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
	
	public final static int[] INDEX_FOR_LOCATION = {0,1,2};
	public final static int UNIT_DECREASE = 3;
	public final static int FLIGHT_COST = 4;
}
