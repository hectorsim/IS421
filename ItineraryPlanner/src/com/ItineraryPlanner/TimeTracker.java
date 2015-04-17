package com.ItineraryPlanner;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TimeTracker {
	public long startTime;
	public TreeMap<String, Long> lapList;
	
	public TimeTracker() {
		this.startTime = System.currentTimeMillis();
		this.lapList = new TreeMap<String, Long>();
	}
	
	public void addLap(String message) {
		this.lapList.put(message, System.currentTimeMillis());
	}
	public String shortTime(){
		long lastTiming = 0;
		
		if (!lapList.isEmpty()) {
			lastTiming = this.startTime;
			Iterator<Entry<String, Long>> iter = lapList.descendingMap().entrySet().iterator();
			
			while (iter.hasNext()) {
				Map.Entry<String, Long> values = iter.next();
				String message = values.getKey();
				long date = values.getValue();
				
				// Print out individual time laps
				long diff = date - lastTiming;
				lastTiming = date;
			}
		} else {
			lastTiming = System.currentTimeMillis();
		}
		
		long totalTimeDiff = lastTiming - startTime;
		
		return "" + totalTimeDiff;
	}
	public String timeToString() {
		String text = "Processing time Details: \n";
		long lastTiming = 0;
		
		if (!lapList.isEmpty()) {
			lastTiming = this.startTime;
			Iterator<Entry<String, Long>> iter = lapList.descendingMap().entrySet().iterator();
			
			while (iter.hasNext()) {
				Map.Entry<String, Long> values = iter.next();
				String message = values.getKey();
				long date = values.getValue();
				
				// Print out individual time laps
				long diff = date - lastTiming;
				lastTiming = date;
				text += message + "..............." + diff + " ms\n";
			}
		} else {
			lastTiming = System.currentTimeMillis();
		}
		
		long totalTimeDiff = lastTiming - startTime;
		
		text += "Total Processing Time: " + totalTimeDiff + " ms";
		return text;
	}
}
