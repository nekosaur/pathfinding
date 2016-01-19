package org.nekosaur.pathfinding.lib.interfaces;

import org.nekosaur.pathfinding.lib.common.Buffer;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.node.Node;

public interface Pathfinder {
	Result findPath(SearchSpace map, Point start, Point goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException;
	Buffer<Node> getHistory();
}
