package org.nekosaur.pathfinding.lib.pathfinders.dijkstra;

import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author nekosaur
 */
public class DijkstraFinder extends AbstractPathfinder {

    @Override
    public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {

        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);

        if (startNode == null || goalNode == null)
            throw new NodeNotFoundException("Start or Goal node not found in SearchSpace");

        PriorityQueue<Node> unvisitedNodes = new PriorityQueue<>();
        Set<Node> visitedNodes = new HashSet<Node>();

        startClock();

        // Initialize search by setting start node to cost 0 and adding it to unvisited list
        startNode.g = 0;
        unvisitedNodes.add(startNode);

        while (!unvisitedNodes.isEmpty()) {

            Node currentNode = unvisitedNodes.remove();

            operations++;

            if (currentNode.equals(goalNode)) {
                return new Result(reconstructPath(currentNode), currentNode.g, stopClock(), operations);
            }

            currentNode.status = NodeStatus.CLOSED;
            visitedNodes.add(currentNode);

            addToHistory(currentNode);

            for (Node neighbourNode : map.getNeighbours(currentNode)) {

                if (neighbourNode.state == NodeState.WALL)
                    continue;

                if (visitedNodes.contains(neighbourNode))
                    continue;

                double distance = currentNode.g + map.getMovementCost(currentNode, neighbourNode);

                if (!unvisitedNodes.contains(neighbourNode) || neighbourNode.g > distance) {
                    neighbourNode.g = distance;
                    neighbourNode.parent = currentNode;

                    neighbourNode.status = NodeStatus.OPEN;
                    addToHistory(neighbourNode);

                    if (!unvisitedNodes.contains(neighbourNode)) {
                        unvisitedNodes.add(neighbourNode);
                    } else {
                        unvisitedNodes.remove(neighbourNode);
                        unvisitedNodes.add(neighbourNode);
                    }
                }

            }

        }

        return new Result(new ArrayList<Vertex>(), 0, stopClock(), operations);
        
    }
}
