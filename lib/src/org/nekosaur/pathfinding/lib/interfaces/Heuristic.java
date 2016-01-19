package org.nekosaur.pathfinding.lib.interfaces;

import org.nekosaur.pathfinding.lib.common.Point;

@FunctionalInterface
public interface Heuristic {
	double calculate(Point d);
}
