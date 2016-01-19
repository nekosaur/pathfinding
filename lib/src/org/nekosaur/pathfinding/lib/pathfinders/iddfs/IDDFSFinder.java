package org.nekosaur.pathfinding.lib.pathfinders.iddfs;

import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Point;
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
public class IDDFSFinder extends AbstractPathfinder {

    private SearchSpace map;

    @Override
    public Result findPath(SearchSpace map, Point start, Point goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {

        this.map = map;

        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);

        int depthLimit = 1;

        List<Point> foundPath = new ArrayList<>();

        startClock();

        startNode.status = NodeStatus.VISITED;

        while (foundPath.isEmpty()) {

            foundPath = depthFirstSearch(startNode, goalNode, 0, depthLimit);

            depthLimit++;
        }

        return new Result(foundPath, stopClock(), operations);
    }

    private List<Point> depthFirstSearch(Node currentNode, Node goalNode, int currentDepth, int depthLimit) throws InterruptedException {
        operations++;

        currentNode.status = NodeStatus.VISITED;
        addToHistory(currentNode);

        if (currentNode.equals(goalNode))
            return reconstructPath(currentNode);

        currentDepth++;

        if (currentDepth <= depthLimit) {
            for (Node neighbourNode : map.getNeighbours(currentNode)) {
                if (neighbourNode.status != NodeStatus.VISITED) {

                    neighbourNode.parent = currentNode;

                    List<Point> result = depthFirstSearch(neighbourNode, goalNode, currentDepth, depthLimit);

                    if (result != null && !result.isEmpty())
                        return result;

                    neighbourNode.parent = null;

                }
            }
        }

        currentNode.status = NodeStatus.INACTIVE;
        addToHistory(currentNode);

        return new ArrayList<>();
    }
}
