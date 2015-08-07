package org.nekosaur.pathfinding.lib.pathfinders.bfs;

import java.util.*;

import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;

/**
 * @author nekosaur
 */
public class BFSFinder extends AbstractPathfinder {

    Queue<Node> unvisitedNodes;

    @Override
    public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException {
        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);
        
        if (startNode == null || goalNode == null)
        	throw new NodeNotFoundException("Start or Goal node not found in SearchSpace");
        
        unvisitedNodes = new LinkedList<>();
        
        startClock();

        // We add the start node to unvisited list, and set it to visited
        unvisitedNodes.add(startNode);
        startNode.status = NodeStatus.VISITED;        

        while (unvisitedNodes.size() > 0) {

            operations++;

            Node currentNode = unvisitedNodes.remove();

            addToHistory(currentNode);

            if (currentNode.equals(goalNode))
                return new Result(reconstructPath(currentNode), stopClock(), operations);         

            for (Node neighbourNode : map.getNeighbours(currentNode)) {

                if (neighbourNode.status == NodeStatus.VISITED || unvisitedNodes.contains(neighbourNode))
                    continue;

                neighbourNode.status = NodeStatus.VISITED;
                neighbourNode.parent = currentNode;

                unvisitedNodes.add(neighbourNode);

            }

        }

        return new Result(new ArrayList<Vertex>(), stopClock(), operations);
    }
}
