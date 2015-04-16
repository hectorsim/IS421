package com.ItineraryPlanner;

import java.util.ArrayList;
import java.util.HashMap;

public class DataParameters {

	public static HashMap<String, Integer> countryIndex() {
		HashMap<String, Integer> countryIndex = new HashMap<String, Integer>();
		
		Location[] locationList = Location.values();
		for (int i = 0; i < locationList.length; i++) {
			countryIndex.put(locationList[i].toString(), (i+1));
		}
		
		return countryIndex;
	}
 	@SuppressWarnings("serial")
	private HashMap<String, Integer> countryIndex = new HashMap<String, Integer>() {
		{
			put("SIN", 1);
			put("PEK", 2);
			put("PVG", 3);
			put("HKG", 4);
			put("KIX", 5);
			put("HND", 6);
			put("ICN", 7);
			put("KHH", 8);
			put("TPE", 9);
			put("PNH", 10);
			put("REP", 11);
			put("DPS", 12);
			put("CGK", 13);
			put("KUL", 14);
			put("PEN", 15);
			put("RGN", 16);
			put("MNL", 17);
			put("BKK", 18);
			put("HKT", 19);
			put("HAN", 20);
			put("SGN", 21);
		}
	};
	
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
	
	/*
	 * Locations Information 
	 */
	public static ArrayList<String> getAllLocations() {
		ArrayList<String> allDestinations = new ArrayList<String>();
		
		for (Location value : Location.values()) {
			allDestinations.add(value.toString());
		}
		
		return allDestinations;
	}
	
	public static int getNumberOfLocations() {
		return Location.values().length;
	}
	
	public static String getLocationName(String locationId) {
		
		Location loc = Location.valueOf(locationId);
		
		switch (loc) {
			case SIN: return "Singapore - Singapore Changi Airport";
			case PEK: return "Beijing - Beijing Capital International Airport";
			case PVG: return "Shanghai - Pudong International Airport";
			case HKG: return "Hong Kong - Hong Kong International Airport";
			case KIX: return "Osaka - Kansai International Airport";
			case HND: return "Tokyo - Tokyo International Airport";
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
