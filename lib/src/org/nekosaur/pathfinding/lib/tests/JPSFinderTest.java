package org.nekosaur.pathfinding.lib.tests;

import java.util.concurrent.TimeUnit;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.movingai.MovingAI;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.pathfinders.jps.JPSFinder;
import org.nekosaur.pathfinding.lib.searchspaces.Grid;
import org.nekosaur.pathfinding.lib.searchspaces.QuadTree;

public class JPSFinderTest {
	
	public static void main(String[] args) {
		
		int[][] data = MovingAI.loadMap(System.getProperty("user.dir") + "\\" + "brc000d.map");
		
		SearchSpace map = QuadTree.create(data, null);
		
		Pathfinder f = new JPSFinder();
		
		Result r;
		try {
			r = f.findPath(map, new Vertex(28,16), new Vertex(30,33), Heuristics.euclidean, 1);
			
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
