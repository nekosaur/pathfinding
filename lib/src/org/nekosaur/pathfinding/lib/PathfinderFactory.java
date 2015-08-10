package org.nekosaur.pathfinding.lib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.pathfinders.bfs.BFSFinder;
import org.nekosaur.pathfinding.lib.pathfinders.dfs.DFSFinder;
import org.nekosaur.pathfinding.lib.pathfinders.dijkstra.DijkstraFinder;
import org.nekosaur.pathfinding.lib.pathfinders.fringe.FringeFinder;
import org.nekosaur.pathfinding.lib.pathfinders.ida.IDAFinder;
import org.nekosaur.pathfinding.lib.pathfinders.iddfs.IDDFSFinder;
import org.nekosaur.pathfinding.lib.pathfinders.jps.JPSFinder;
import org.nekosaur.pathfinding.lib.pathfinders.theta.LazyThetaFinder;
import org.nekosaur.pathfinding.lib.pathfinders.theta.ThetaFinder;

public class PathfinderFactory {
	
	private PathfinderFactory() {};

	public static Map<String, Supplier<Pathfinder>> getPathfinders() {
		Map<String, Supplier<Pathfinder>> map = new LinkedHashMap<>();

		map.put("A*", AStarFinder::new);
		map.put("BFS", BFSFinder::new);
		map.put("DFS", DFSFinder::new);
		map.put("Dijkstra", DijkstraFinder::new);
		map.put("Fringe", FringeFinder::new);
		map.put("IDA*", IDAFinder::new);
		map.put("IDDFS", IDDFSFinder::new);
		map.put("JPS", JPSFinder::new);
		map.put("Theta*", ThetaFinder::new);
		map.put("Lazy Theta*", LazyThetaFinder::new);

		return map;
	}
}
