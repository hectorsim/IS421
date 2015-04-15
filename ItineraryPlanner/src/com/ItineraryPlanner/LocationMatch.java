package com.ItineraryPlanner;

public class LocationMatch {
	public static String getLocationName(String locationId) {
		
		Location loc = Location.valueOf(locationId);
		
		switch (loc) {
			case PEK: 
				return "Beijing - Beijing Capital International Airport";
			case PVG:
				return "Shanghai - Pudong International Airport";
			case HKG:
				return "Hong Kong - Hong Kong International Airport";
			case KIX:
				return "Osaka - Kansai International Airport";
			case HND:
				return "Tokyo - Tokyo International Airport";
			case ICN:
				return "Seoul - Incheon International Airport";
			case KHH:
				return "Kaohsiung - Kaohsiung International Airport";
			case TPE:
				return "Taipei - Taiwan Taoyuan International Airport";
			case PNH:
				return "Phnom Penh - Phnom Penh International Airport";
			case REP:
				return "Siem Reap - Siem Reap International Airport";
			case DPS:
				return "Bali - Ngurah Rai International Airport";
			case CGK:
				return "Jakarta - Jakarta Airport";
			case KUL:
				return "Kuala Lumpur - Kuala Lumpur International Airport";
			case PEN:
				return "Penang - Penang International Airport";
			case RGN:
				return "Yangon - Yangon International Airport";
			case MNL:
				return "Manila - Ninoy Aquino International Airport";
			case BKK:
				return "Bangkok - Suvarnabhumi Airport";
			case HKT:
				return "Phuket - Phuket International Airport";
			case HAN:
				return "Hanoi - Noi Bai International Airport";
			case SGN:
				return "Ho Chi Minh City - Ho Chi Minh City Airport";
			case SIN:
				return "Singapore - Singapore Changi Airport";
			default:
				return "";
		}
	}
	
	public enum Location {
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
		SGN,
		SIN
	}
}
