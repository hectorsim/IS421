package com.heuristic;

import java.util.ArrayList;

import com.entity.Graph;
import com.entity.Vertex;

public class Solutions {
	private ArrayList<Vertex> path;
	private double totalCost;
	private int totalPreferences;
	private Vertex potentialVertex;
	private Graph graph;
	
	public Solutions(ArrayList<Vertex> path, double totalCost,
			int totalPreferences, Vertex potentialVertex, Graph graph) {
		
		this.path = path;
		this.totalCost = totalCost;
		this.totalPreferences = totalPreferences;
		this.potentialVertex = potentialVertex;
		this.graph = graph;
	}

	public ArrayList<Vertex> getPath() {
		return path;
	}

	public void setPath(ArrayList<Vertex> path) {
		this.path = path;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public int getTotalPreferences() {
		return totalPreferences;
	}

	public void setTotalPreferences(int totalPreferences) {
		this.totalPreferences = totalPreferences;
	}

	public Vertex getPotentialVertex() {
		return potentialVertex;
	}

	public void setPotentialVertex(Vertex potentialVertex) {
		this.potentialVertex = potentialVertex;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public int numberOfPath() {
		return this.path.size();
	}

	@SuppressWarnings("unchecked")
	protected Solutions clone() {
		// TODO Auto-generated method stub
		return new Solutions((ArrayList<Vertex>) this.path.clone(), this.totalCost,
			this.totalPreferences, this.potentialVertex, this.graph);
	}

	
}
