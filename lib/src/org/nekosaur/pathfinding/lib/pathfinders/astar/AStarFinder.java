package org.nekosaur.pathfinding.lib.pathfinders.astar;

import java.util.ArrayList;

import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.datastructures.BinaryHashHeap;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;

/**
 * http://www.redblobgames.com/pathfinding/a-star/introduction.html
 * https://en.wikipedia.org/wiki/A*_search_algorithm
 *
 * @author nekosaur
 */
public class AStarFinder extends AbstractPathfinder {

	@Override
	public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException {
		
		Node startNode = map.getNode(start.x, start.y);
		Node goalNode = map.getNode(goal.x, goal.y);
		
		if (startNode == null || goalNode == null)
			throw new NodeNotFoundException("Start or Goal node not found in SearchSpace");

    	// Hash heap seems to net ~10ms on 512x512 maps, but what is the memory impact?
        BinaryHashHeap<Node> openList = new BinaryHashHeap<>(Node.class, map.getWidth() * map.getHeight());
        
        startClock();
        
        // Initialize search by setting G score of start to 0 and adding it to open list
        startNode.g = 0;
        openList.add(startNode);        

        // While we still have nodes to check in the open list
        while (openList.size() > 0) {

            operations++;

            // Get node with best score from open list
            Node currentNode = openList.remove();
                      
            // If we've reached the goal, reconstruct complete path
            if (currentNode.equals(goalNode)) {
                System.out.println("Found goal!");
                return new Result(reconstructPath(currentNode), currentNode.g, stopClock(), operations);
            }

            // Close node to indicate it has been checked
            currentNode.status = NodeStatus.CLOSED;

            addToHistory(currentNode);

            // Go through all node neighbours
            for (Node neighbourNode : map.getNeighbours(currentNode)) {
            	
            	// If neighbour is already closed, skip it
            	if (neighbourNode.status == NodeStatus.CLOSED)
                    continue;
               
                // Calculate G cost for neighbour node
                double g = currentNode.g + map.getMovementCost(currentNode, neighbourNode);

                // Use node's own status instead of openList.contains() which is O(n) when using binary heap
                boolean isInOpenList = neighbourNode.status == NodeStatus.OPEN;
                                
                // If neighbour node is not on the open list, or the current G cost of neighbour
                // node is higher than newly calculated G cost
                if (!isInOpenList || g < neighbourNode.g) {
                    // Set new G and H costs, and parent
                    neighbourNode.g = g;
                    neighbourNode.h = weight * heuristic.calculate(neighbourNode.delta(goalNode));
                    neighbourNode.parent = currentNode;
                    
                    // Add node to open list if it's not on it already
                    if (!isInOpenList) {
                        neighbourNode.status = NodeStatus.OPEN;
                        openList.add(neighbourNode);
                    } else {
                    	// If node is already on the open list, update it so list gets resorted
                    	openList.update(neighbourNode);
                    }

                    addToHistory(neighbourNode);

                }
                
            }

        }

        System.out.println("Could not find goal!");
        return new Result(new ArrayList<Vertex>(), Double.MAX_VALUE, stopClock(), operations);
	}
	
}
