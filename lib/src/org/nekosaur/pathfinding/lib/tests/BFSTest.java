package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.pathfinders.bfs.BFSFinder;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;

public class BFSTest {
	public static void main(String[] args) {
		
		int[][] vertices = new int[][] {
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{1,1,1,0,0,0,0,0},
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
		
		SearchSpace map = Grid.create(new MapData(vertices, null));
		
		Pathfinder f = new BFSFinder();
		
		Result r;
		try {
			r = f.findPath(map, new Vertex(1,1), new Vertex(2,7), Heuristics.euclidean, 1);
			
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
