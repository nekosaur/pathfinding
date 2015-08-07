package org.nekosaur.pathfinding.lib;

import java.util.HashMap;
import java.util.Map;

import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.Graph;
import org.nekosaur.pathfinding.lib.searchspaces.Grid;
import org.nekosaur.pathfinding.lib.searchspaces.QuadTree;

public class SearchSpaceFactory {

	private SearchSpaceFactory() {};
	
	public static SearchSpace create(Class<SearchSpace> clazz) {
		try {
            return (SearchSpace)clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	public static Map<String, Class<? extends SearchSpace>> getAvailableClasses() {
		
		Map<String, Class<? extends SearchSpace>> map = new HashMap<>();
		
		map.put("Graph", Graph.class);
		map.put("Grid", Grid.class);
		map.put("QuadTree", QuadTree.class);
		
		return map;
		
	}
}
