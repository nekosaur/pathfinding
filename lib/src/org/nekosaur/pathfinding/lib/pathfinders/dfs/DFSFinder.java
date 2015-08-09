package org.nekosaur.pathfinding.lib.pathfinders.dfs;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nekosaur
 */
public class DFSFinder extends AbstractPathfinder {

    @Override
    public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {
        startClock();

        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);

        if (startNode == null || goalNode == null)
            throw new NodeNotFoundException("Start or Goal node not found in SearchSpace");

        return new Result(depthFirstSearch(map, startNode, goalNode), stopClock(), operations);

    }

    private List<Vertex> depthFirstSearch(SearchSpace map, Node currentNode, Node goalNode) throws InterruptedException {
        operations++;

        currentNode.status = NodeStatus.VISITED;
        addToHistory(currentNode);

        if (currentNode.equals(goalNode))
            return reconstructPath(currentNode);

        for (Node neighbourNode : map.getNeighbours(currentNode)) {
            if (neighbourNode.status != NodeStatus.VISITED) {
                neighbourNode.parent = currentNode;

                List<Vertex> result = depthFirstSearch(map, neighbourNode, goalNode);

                if (result != null && !result.isEmpty())
                    return result;

                neighbourNode.parent = null;

            }
        }

        currentNode.status = NodeStatus.INACTIVE;
        addToHistory(currentNode);

        return new ArrayList<>();
    }
}
