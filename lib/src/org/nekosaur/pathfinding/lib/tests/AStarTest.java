package org.nekosaur.pathfinding.lib.tests;

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
		/*
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
		
		*/
		
	}
}
