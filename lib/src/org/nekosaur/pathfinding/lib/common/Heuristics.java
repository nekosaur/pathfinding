package org.nekosaur.pathfinding.lib.common;

import org.nekosaur.pathfinding.lib.interfaces.Heuristic;

import java.util.HashMap;
import java.util.Map;

public class Heuristics {
	public static final Heuristic euclidean = d -> Math.sqrt(d.x * d.x + d.y * d.y);
	public static final Heuristic octile = d -> Math.max(d.x, d.y) + (Math.sqrt(2) - 1) * Math.min(d.x, d.y);
	public static final Heuristic manhattan = d -> d.x + d.y;
	public static final Heuristic chebyshev = d -> d.x + d.y + (Math.sqrt(2) - 2) * Math.min(d.x, d.y);

	public static Map<String, Heuristic> getHeuristics() {
		Map<String, Heuristic> map = new HashMap<>();

		map.put("Euclidean", euclidean);
		map.put("Octile", octile);
		map.put("Manhattan", manhattan);
		map.put("Chebyshev", chebyshev);

		return map;
	}
}
