package com.entity;

import java.util.HashMap;

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
	
	/**
	 * get the vertex from the graph
	 * @param key
	 * @return
	 */
	public Vertex getVertex(int key) {
		return this.vertices.get(key);
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
}
