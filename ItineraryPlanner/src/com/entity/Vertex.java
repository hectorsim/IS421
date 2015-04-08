package com.entity;

import java.util.HashMap;

/**
 * A location node for the graph
 * @author Leon
 */
public class Vertex {
	int id;
	double livingCost;
	HashMap<Integer, Double> adjList;
	boolean isVisited;
	
	/**
	 * Constructor to initialize a Vertex Object
	 * @param id
	 * @param livingCost
	 * @param isVisited
	 */
	public Vertex(int id, double livingCost) {
		this.id = id;
		this.livingCost = livingCost;
		this.adjList = new HashMap<Integer, Double>();
		this.isVisited = false;
	}
	
	/**
	 * Constructor to initialize a Vertex Object
	 * @param id
	 * @param livingCost
	 * @param isVisited
	 */
	public Vertex(int id, double livingCost, boolean isVisited) {
		this.id = id;
		this.livingCost = livingCost;
		this.adjList = new HashMap<Integer, Double>();
		this.isVisited = isVisited;
	}

	/**
	 * Get the id of the vertice
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id for the vertex
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the living cost of the vertex
	 * @return
	 */
	public double getLivingCost() {
		return livingCost;
	}

	/**
	 * Set the living cost of the vertex
	 * @param livingCost
	 */
	public void setLivingCost(double livingCost) {
		this.livingCost = livingCost;
	}

	/**
	 * Get all adjacent vertex
	 * @return
	 */
	public HashMap<Integer, Double> getVertices() {
		return this.adjList;
	}

	/**
	 * Add a new adjacent vertex connected to current
	 * @param id
	 * @param flightCost
	 */
	public void setAdj(int id, double flightCost) {
		this.adjList.put(id, flightCost);
	}
	
	/**
	 * Delete a adjacent vertex connected to current
	 * @param id
	 */
	public void deleteAdj(int id) {
		this.adjList.remove(id);
	}
	
	/**
	 * Get the status whether is the vertex visited
	 * @return
	 */
	public boolean isVisited() {
		return isVisited;
	}

	/**
	 * Set whether the vertex is being visited
	 * @param isVisited
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
}
