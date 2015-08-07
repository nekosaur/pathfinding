package org.nekosaur.pathfinding.lib.common;

import org.nekosaur.pathfinding.lib.interfaces.Heuristic;

public class Heuristics {
	public static final Heuristic euclidean = d -> Math.sqrt(d.x * d.x + d.y * d.y);
	public static final Heuristic octile = d -> Math.max(d.x, d.y) + (Math.sqrt(2) - 1) * Math.min(d.x, d.y);
	public static final Heuristic manhattan = d -> d.x + d.y;
	public static final Heuristic chebyshev = d -> d.x + d.y + (Math.sqrt(2) - 2) * Math.min(d.x, d.y);
}
