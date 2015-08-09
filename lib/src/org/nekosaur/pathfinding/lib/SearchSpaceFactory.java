package org.nekosaur.pathfinding.lib;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;

public class SearchSpaceFactory {

	private SearchSpaceFactory() {};
	
	public static Map<String, Function<MapData, SearchSpace>> getSearchSpaces() {
		Map<String, Function<MapData, SearchSpace>> map = new LinkedHashMap<>();

		map.put("Grid", Grid::create);
		//map.put("Graph", Graph::create);
		//map.put("QuadTree", QuadTree::create);

		return map;
	}
}
