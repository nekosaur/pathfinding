package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.searchspaces.graph.Graph;

public class GraphTest {
	
	public static void main(String[] args) {
		
		int[][] vertices = {
				{1,0,0,0,0,0,0,0},
				{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
		};
		
		int[][] edges = {
				{0,1,1,0,0},
				{1,0,1,1,0},
				{1,1,0,1,1},
				{0,1,1,0,1},
				{0,0,1,1,0}
		};
		
		SearchSpace g = Graph.create(new MapData(vertices, edges));
		
		Pathfinder p = new AStarFinder();
		
		try {
			p.findPath(g, new Point(0,0), new Point(2,6), Heuristics.euclidean, 1);
		} catch (NodeNotFoundException | SearchSpaceNotSupportedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

}
