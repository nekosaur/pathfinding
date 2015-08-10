package org.nekosaur.pathfinding.gui.presentation.maps.decorators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.nekosaur.pathfinding.gui.business.TriConsumer;
import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.pathfinders.jps.JPSFinder;

public class DecoratorFactory {
	
	private static Map<Class<? extends Pathfinder>, TriConsumer<SearchSpace, MapCanvas, Node>> map = new LinkedHashMap<>();
		
	static {
		map.put(JPSFinder.class, JPSDecorator::accept);
	}
	
	public static TriConsumer<SearchSpace, MapCanvas, Node> getDecorator(Class<? extends Pathfinder> pathfinderClass) {
		return map.get(pathfinderClass);
	}
}
