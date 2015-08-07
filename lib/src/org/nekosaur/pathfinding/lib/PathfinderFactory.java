package org.nekosaur.pathfinding.lib;

import java.util.HashMap;
import java.util.Map;

import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.pathfinders.bfs.BFSFinder;

public class PathfinderFactory {
	
	private PathfinderFactory() {};
	
	public static Pathfinder create(Class<Pathfinder> clazz) {
		try {
            return (Pathfinder)clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	public static Map<String, Class<? extends Pathfinder>> getAvailableClasses() {
		
		Map<String, Class<? extends Pathfinder>> map = new HashMap<>();
		
		map.put("A*", AStarFinder.class);
		map.put("BFS", BFSFinder.class);
		
		return map;
		
	}
}
