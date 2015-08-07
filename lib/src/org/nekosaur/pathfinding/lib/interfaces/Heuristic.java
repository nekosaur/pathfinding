package org.nekosaur.pathfinding.lib.interfaces;

import org.nekosaur.pathfinding.lib.common.Vertex;

@FunctionalInterface
public interface Heuristic {
	double calculate(Vertex d);
}
