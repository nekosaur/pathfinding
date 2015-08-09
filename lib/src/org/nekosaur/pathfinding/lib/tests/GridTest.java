package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.movingai.MovingAI;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;

public class GridTest {
	
	public static void main(String[] args) {
		
		int[][] data = MovingAI.loadMap(System.getProperty("user.dir") + "\\" + "brc000d.map");
		
		SearchSpace map = Grid.create(new MapData(data, null));
		
		Pathfinder f = new AStarFinder();
		
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
