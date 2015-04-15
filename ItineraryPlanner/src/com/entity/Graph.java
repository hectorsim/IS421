package com.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;

public class Graph {
	
	/**
	 * Hash that contains vertices
	 */
	HashMap<Integer, Vertex> vertices;
	
	/**
	 * Constructor to initialize the graph object
	 */
	public Graph() {
		this.vertices = new HashMap<Integer, Vertex>();
	}
	
	public ArrayList<Vertex> getListOfVertex() {
		ArrayList<Vertex> nameList = new ArrayList<Vertex>();
		
		Iterator<Vertex> iter = this.vertices.values().iterator();
		while (iter.hasNext()) {
			nameList.add(iter.next());
		}
		
		return nameList;
	}
	/**
	 * get the vertex from the graph
	 * @param key
	 * @return
	 */
	public Vertex getVertex(int key) {
		return this.vertices.get(key);
	}
	
	public Vertex getVertexByLocationId(String locationId) {
		Iterator<Vertex> iter = this.vertices.values().iterator();
		
		while (iter.hasNext()) {
			Vertex v = iter.next();
			
			if (v.getLocationId().equalsIgnoreCase(locationId)) {
				return v;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a new vertex into the graph
	 * @param key
	 * @param value
	 */
	public void addVertex(int key, Vertex value) {
		this.vertices.put(key, value);
	}
	
	/**
	 * Remove a vertex from the graph by the key
	 * @param key
	 */
	public void removeVertex(int key) {
		this.vertices.remove(key);
	}
	
	/**
	 * Print out graph data structure
	 */
	public String toString() {
		String text = "";
				
		Iterator<Entry<Integer, Vertex>> iter = this.vertices.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<Integer, Vertex> value = (Map.Entry<Integer, Vertex>) iter.next();
			
			int vertexId = value.getKey();
			Vertex v = value.getValue();
			
			text += "Location " + vertexId + " : \n";
			text += v.toString() + "\n\n";
		}
		
		return text;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSON() {
		JSONArray dataArray = new JSONArray();
		
		Iterator<Vertex> iter = this.vertices.values().iterator();
		
		while (iter.hasNext()) {
			Vertex v = iter.next();
			dataArray.add(v.toJSON());
		}
		
		return dataArray;
	}
}
