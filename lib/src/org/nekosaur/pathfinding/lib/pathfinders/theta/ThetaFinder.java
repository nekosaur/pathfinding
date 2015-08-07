package org.nekosaur.pathfinding.lib.pathfinders.theta;

import java.util.ArrayList;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.datastructures.BinaryHashHeap;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;
import org.nekosaur.pathfinding.lib.searchspaces.Grid;

/**
 * Theta* pathfinder. 
 * 
 * Based on 
 * http://aigamedev.com/open/tutorials/theta-star-any-angle-paths/
 * 'Theta*: Any-Angle Path Planning on Grids' by K. Daniel, A. Nash, S. Koenig and A. Felner (http://idm-lab.org/bib/abstracts/papers/jair10b.pdf)
 * 
 * @author nekosaur
 *
 */
public class ThetaFinder extends AbstractPathfinder {

	private SearchSpace map;

	@Override
	public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight)
			throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {	
	
		this.map = map;

		Node startNode = map.getNode(start.x, start.y);
		Node goalNode = map.getNode(goal.x, goal.y);
		
		if (startNode == null || goalNode == null)
			throw new NodeNotFoundException("Start or Goal node not found in SearchSpace");
        
        BinaryHashHeap<Node> openList = new BinaryHashHeap<Node>(Node.class, map.getWidth()*map.getHeight());
        
		startClock();
        
        // Initialize search by setting G score of start to 0 and adding it to open list
        startNode.g = 0;
        openList.add(startNode);

        while (openList.size() > 0) {
            operations++;

            // Get node with best score from open list
            Node currentNode = openList.remove();
            
            if (currentNode == goalNode) {
                return new Result(reconstructPath(goalNode), stopClock(), operations);
            }

            // Close node to indicate it has been checked
            currentNode.status = NodeStatus.CLOSED;

            addToHistory(currentNode);

            // Go through all node neighbours
            for (Node neighbourNode : map.getNeighbours(currentNode)) {

            	if (neighbourNode.status == NodeStatus.CLOSED)
                    continue;

//            	if (neighbourNode.state == NodeState.WALL);
//                	continue;
                
                boolean isInOpenList = neighbourNode.status == NodeStatus.OPEN;
                
                if (!isInOpenList) {
                	neighbourNode.g = Double.MAX_VALUE;
                	neighbourNode.parent = null;
                }
                
                double g = neighbourNode.g;
                
                computeCost(currentNode, neighbourNode);
                
                // If new G cost is lower than current, update node
                if (neighbourNode.g < g) {
                    neighbourNode.h = weight * heuristic.calculate(neighbourNode.delta(goalNode));
                    neighbourNode.f = neighbourNode.g + neighbourNode.h;

                    // Add node to open list if it's not on it already
                    if (!isInOpenList) {
                        neighbourNode.status = NodeStatus.OPEN;
                        openList.add(neighbourNode);
                    } else {
                    	openList.update(neighbourNode);
                    }

                    addToHistory(neighbourNode);

                }
            }

        }

        return new Result(new ArrayList<Vertex>(), stopClock(), operations);
	}
	
	/**
	 * computeCost calculates the G cost of a node as follows:
	 * 
	 * If neighbourNode has line of sight to the parent of currentNode,
	 * then the G cost is the straight line distance between neighbour and parent.
	 * 
	 * If there is no line of sight, G cost is simply the distance from current
	 * to neighbour.
	 * 
	 */
	private void computeCost(Node currentNode, Node neighbourNode) {
        Node parent = currentNode.parent;
        double g;
        if (hasLineOfSight(parent, neighbourNode)) {
			g = parent.g + Heuristics.euclidean.calculate(parent.delta(neighbourNode));
        	if (g < neighbourNode.g) {
        		neighbourNode.g = g;
        		neighbourNode.parent = parent;
        	}
        } else {
        	g = currentNode.g + map.getMovementCost(currentNode, neighbourNode);
        	if (g < neighbourNode.g) {
        		neighbourNode.g = g;
        		neighbourNode.parent = currentNode;
        	}
        }
	}
	
	private boolean hasLineOfSight(Node n1, Node n2) {
		if (n1 == null || n2 == null)
			return false;

		return hasLineOfSight(n1.x, n1.y, n2.x, n2.y);
	}
	
	/**
	 * Code from http://playtechs.blogspot.se/2007/03/raytracing-on-grid.html
	 * 
	 */
	private boolean hasLineOfSight(int x0, int y0, int x1, int y1)
	{
	    int dx = Math.abs(x1 - x0);
	    int dy = Math.abs(y1 - y0);
	    int x = x0;
	    int y = y0;
	    int n = 1 + dx + dy;
	    int x_inc = (x1 > x0) ? 1 : -1;
	    int y_inc = (y1 > y0) ? 1 : -1;
	    int error = dx - dy;
	    dx *= 2;
	    dy *= 2;

	    for (; n > 0; --n)
	    {
	        if (!map.isWalkableAt(x, y))
	        	return false;

	        if (error > 0)
	        {
	            x += x_inc;
	            error -= dy;
	        }
	        else
	        {
	            y += y_inc;
	            error += dx;
	        }
	    }
	    
	    return true;
	}

}