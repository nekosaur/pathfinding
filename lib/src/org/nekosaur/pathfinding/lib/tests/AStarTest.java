package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.searchspaces.Grid;
import org.nekosaur.pathfinding.lib.searchspaces.QuadTree;

public class AStarTest {

	public static void main(String[] args) {
		
		int[][] vertices = new int[][] {
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,1,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0}
		};
		
		int[][] edges = new int[][] {
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1}
		};
		
		//SearchSpace map = Grid.create(vertices, null, true);
		SearchSpace map = QuadTree.create(vertices, null);
		
		Pathfinder f = new AStarFinder();
		
		Result r;
		try {
			r = f.findPath(map, new Vertex(3,1), new Vertex(3,7), Heuristics.euclidean, 1);
			
			for (Vertex v : r.path()) {
				System.out.println(v);
			}
			
			System.out.println(String.format("%d ms", r.duration()));
			
		} catch (NodeNotFoundException | SearchSpaceNotSupportedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
