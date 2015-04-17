package com.heuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.entity.Graph;
import com.entity.User;
import com.entity.Vertex;

public class HeuristicFactory {
	
	private User user;
	private HashMap<Integer, Double> candidates;
	
	public HeuristicFactory(User user) {
		this.user = user;
		this.candidates = null;
	}
	
	public Graph Dijkstra(Graph graph, int index) {
		return null;
	}
	
	public void GRASPConstruction() {
		Graph graph = user.getGraph();
		candidateList(graph);
		
		// Attributes required
		Solutions solution = null;
		boolean firstNode = false;
		double tempCost = 0;
		
		if (candidates != null || !candidates.isEmpty()) {
			Vertex startLocation = graph.getStartVertex();
		
			ArrayList<Vertex> tempPath = new ArrayList<Vertex>();
			tempPath.add(startLocation);
			
			while (!firstNode) {
				
				Random ran = new Random();
				Integer[] idList = (Integer[]) candidates.keySet().toArray(new Integer[candidates.size()]);
				int potentialIndex = idList[ran.nextInt(idList.length)];
				Vertex potentialVertex = graph.getVertex(potentialIndex);
				System.out.println(candidates.size());
				System.out.println("Fuck this shit-1");
				if (startLocation.isConnected(potentialIndex)) {
					System.out.println("Fuck this shit-2");
					potentialVertex.setNoOfDays(user.getNoOfStays()-1);
					System.out.println("Fuck this shit-3");
					tempPath.add(potentialVertex);
					tempPath.add(startLocation);
					
					tempCost = user.getRecommendedTotalCost(tempPath);
					int totalDays = user.getRecommendedTotalDays(tempPath);
					System.out.println("Fuck this shit-4");
					if (tempCost <= user.getBudget() && totalDays <= user.getNoOfStays()) {
						solution = new Solutions((ArrayList<Vertex>) tempPath, tempCost, user.getRecommendedTotalSatisfaction(tempPath), potentialVertex, graph);
						candidates.remove(potentialIndex);
						firstNode = true;
						user.addSolution(solution);
					}
				} else {
					tempPath.clear();
				}
			}
			
			while (!candidates.isEmpty()) {
				Solutions biggerTour = PathSingleInsertion(solution);
				System.out.println("Fuck this shit");
				if (biggerTour.numberOfPath() == solution.numberOfPath()) {
					System.out.println("Solution Found");
					break;
				} else {
					user.addSolution(biggerTour);
					solution = biggerTour;
				}
			}
		}
	}
	
	public Solutions PathSingleInsertion(Solutions solution) {
		Solutions newSolution =  solution.clone();

		Graph graph = newSolution.getGraph();
		ArrayList<Vertex> newPath = newSolution.getPath();		
		Vertex lastNode = newSolution.getPotentialVertex();
		boolean inserted = false;
		// Maximum of run
		int count = 0;
		
		while (!inserted) {
			System.out.println("Fuck this shit2");
			Random ran = new Random();
			Integer[] idList = (Integer[]) candidates.keySet().toArray(new Integer[candidates.size()]);
			int potentialIndex = idList[ran.nextInt(idList.length)];
			Vertex potentialVertex = graph.getVertex(potentialIndex);
			
			if (lastNode.isConnected(potentialIndex) && potentialVertex.isConnected(graph.getStartLocationId())) {

				// Divided by total node available excluding start node (Existing node - start node + new node)
				// Remove the end location for the next insertion
				newPath.remove(newPath.size()-1);
				int averageDuration = (user.getNoOfStays()-1)/(newPath.size());
				
				if (averageDuration > 1) {
					int durationLeft = user.getNoOfStays()-1;
					
					for (int i=0; i < newPath.size(); i++) {
						if (i != 0) { // Ignore the first node
							Vertex v = newPath.get(i);
							int daysUsed = v.setNoOfDays(averageDuration);
							durationLeft -= daysUsed;
						}
					}
					
					if (durationLeft > 0) {
						int daysUsed = potentialVertex.setNoOfDays(durationLeft);
						
						if (daysUsed <= durationLeft) {
							newPath.add(potentialVertex);
							newPath.add(graph.getStartVertex());
							double totalCost = user.getRecommendedTotalCost(newPath);
							
							if (totalCost <= user.getBudget()) {
								newSolution = new Solutions((ArrayList<Vertex>) newPath, totalCost, 
										user.getRecommendedTotalSatisfaction(newPath), potentialVertex, graph);
								candidates.remove(potentialVertex.getId());
								inserted = true;
							}
						} else {
							potentialVertex.resetNoOfDay();
						}
					} else {
						newPath.add(graph.getStartVertex());
						break;
					}
				} else {
					newPath.add(graph.getStartVertex());
					break;
				}
			}
			
			if (count >= candidates.size()*2) {
				newPath.add(graph.getStartVertex());
				break;
			} else{
				count++;
			}
		}
		
		return newSolution;
	}
	
	@SuppressWarnings("unchecked")
	public void candidateList(Graph graph) {
		
		// Sort list by satisfaction from smaller to biggest and remove those larger than mean value
		HashMap<Integer, Double> prefScore = (HashMap<Integer, Double>) graph.getPreferenecScores().clone();
		prefScore.remove(graph.getStartLocationId());
		double meanValue = calculateMean(prefScore);
		
		candidates = selectCandidates(prefScore, meanValue);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LinkedHashMap<Integer, Double> selectCandidates(HashMap<Integer, Double> map, double mean) {
		List list = new LinkedList(map.entrySet());
		
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return (int) ((Comparable)((Map.Entry<Integer, Double>)(o1)).getValue())
						.compareTo(((Map.Entry<Integer, Double>)(o2)).getValue());
			}
		});
		
		LinkedHashMap<Integer, Double> linkedMap = new LinkedHashMap<Integer, Double>();
		
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Map.Entry<Integer, Double> entry = (Map.Entry<Integer, Double>) iter.next();
			double score= entry.getValue();
			
			if (score <= mean) {
				linkedMap.put(entry.getKey(), score);
			} else {
				break;
			}
		}
	
		return linkedMap;
	}
	
	public static double calculateMean(HashMap<Integer, Double> map) {
		double totalScore = 0;
		
		for (Iterator<Double> iter = map.values().iterator(); iter.hasNext();) {
			totalScore += iter.next(); 
		}
		
		return totalScore/map.size();
	}
}
