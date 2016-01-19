package org.nekosaur.pathfinding.lib.pathfinders.jps;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;


public class JPSFinder extends AbstractPathfinder {
	
	private PriorityQueue<Node> jumpList = new PriorityQueue<>();
    //private ArrayList<Node> closedList = new ArrayList<>();
    
    private SearchSpace map;
    private double weight;
    private Heuristic heuristic;

	@Override
	public Result findPath(SearchSpace map, Point start, Point goal, Heuristic heuristic, double weight)
			throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {


		if (!(map instanceof Grid))
			throw new SearchSpaceNotSupportedException("JPSFinder can only run on Grid");
		
		this.map = map;
		this.weight = weight;
		this.heuristic = heuristic;

		Node startNode = map.getNode(start.x, start.y);
		Node goalNode = map.getNode(goal.x, goal.y);
		
		if (startNode == null || goalNode == null)
			throw new NodeNotFoundException("Start or Goal node not found in SearchSpace");

        jumpList.clear();
        //closedList.clear();
        
        startClock();

        // Initialize search by setting start node G cost and adding it to jump list
        startNode.g = 0;
        jumpList.add(startNode);

        while (!jumpList.isEmpty()) {

            operations++;

            Node currentNode = jumpList.remove();

            if (currentNode == goalNode) {
                return new Result(reconstructPath(currentNode), currentNode.g, stopClock(), operations);
            }

            currentNode.status = NodeStatus.CLOSED;
            addToHistory(currentNode);
            //closedList.add(currentNode);

            identifySuccessors(currentNode, startNode, goalNode);

        }

        return new Result(new ArrayList<Point>(), stopClock(), operations);
		
	}
	
	
	private void identifySuccessors(Node currentNode, Node startNode, Node goalNode) throws InterruptedException {
		
        // Start node has no parent, so we can't prune anything		
		List<Node> neighbours = currentNode.equals(startNode) ? map.getNeighbours(currentNode) : pruneNeighbours(currentNode);

        for (Node neighbourNode : neighbours) {

            if (neighbourNode == null)
                continue;
            
            Node jumpNode = jump(currentNode, getDirection(currentNode, neighbourNode), goalNode);

            if (jumpNode == null || jumpNode.status == NodeStatus.CLOSED)
                continue;

            // include heuristic distance, as parent might not be immediately adjacent
            double d = Heuristics.octile.calculate(currentNode.delta(jumpNode));
            double g = currentNode.g + d;

            if (!jumpList.contains(jumpNode) || g < jumpNode.g) {
                jumpNode.g = g;
                jumpNode.h = weight * heuristic.calculate(jumpNode.delta(goalNode));

                jumpNode.parent = currentNode;

                if (!jumpList.contains(jumpNode)) {
                    jumpList.add(jumpNode);
                    jumpNode.status = NodeStatus.JUMPED;
                } else {
                    // Since scores has been recalculated, we remove node from openlist and add it again
                    jumpList.remove(jumpNode);
                    jumpList.add(jumpNode);
                }
            }

            addToHistory(jumpNode);

        }

    }
	
	/**
     * When finding neighbours in JPS we only want the node directly in front of the current node, going in the
     * direction of movement, plus any forced neighbours
     *
     * @param node Jump Vertex Successor node to find neighbours of
     * @return A pruned set of neighbours
     */
    private List<Node> pruneNeighbours(Node node) {

        List<Node> neighbours = new ArrayList<>();

        Node parentNode = node.parent;

        Point direction = getDirection(parentNode, node);

        double dx = direction.x;
        double dy = direction.y;
        double x = node.x;
        double y = node.y;
        //map.getMovementCost(parentNode, Node) > 1
        if (dx != 0 && dy != 0) {
            // diagonal movement
            if (map.isWalkableAt(x + dx, y))
                neighbours.add(map.getNode(x + dx, y));

            if (map.isWalkableAt(x, y + dy))
                neighbours.add(map.getNode(x, y + dy));

            if (map.isWalkableAt(x + dx, y + dy))
                neighbours.add(map.getNode(x + dx, y + dy));

            if (map.allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                if (!map.isWalkableAt(x - dx, y))
                    neighbours.add(map.getNode(x - dx, y + dy));
    
                if (!map.isWalkableAt(x, y - dy))
                    neighbours.add(map.getNode(x + dx, y - dy));
            }

        } else {
            // vertical
            if (dx == 0) {
                // Add node right in front of direction
                if (map.isWalkableAt(x, y + dy)) {
                    neighbours.add(map.getNode(x, y + dy));
                }
                // Pruning changes if corner crossing is allowed
                if (!map.allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                    if (!map.isWalkableAt(x - 1, y - dy)) {
                        neighbours.add(map.getNode(x - 1, y));
                        neighbours.add(map.getNode(x - 1, y + dy));
                    }
                    if (!map.isWalkableAt(x + 1, y - dy)) {
                        neighbours.add(map.getNode(x + 1, y));
                        neighbours.add(map.getNode(x + 1, y + dy));
                    }
                } else {
                    if (!map.isWalkableAt(x - 1, y)) {
                        neighbours.add(map.getNode(x - 1, y + dy));
                    }
                    if (!map.isWalkableAt(x + 1, y)) {
                        neighbours.add(map.getNode(x + 1, y + dy));
                    }
                }
            // horizontal
            } else {
                // Add node right in front of direction
                if (map.isWalkableAt(x + dx, y)) {
                    neighbours.add(map.getNode(x + dx, y));
                }
                // Pruning changes if corner crossing is allowed or not
                if (!map.allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                    if (!map.isWalkableAt(x - dx, y - 1)) {
                        neighbours.add(map.getNode(x, y - 1));
                        neighbours.add(map.getNode(x + dx, y - 1));
                    }
                    if (!map.isWalkableAt(x - dx, y + 1)) {
                        neighbours.add(map.getNode(x, y + 1));
                        neighbours.add(map.getNode(x + dx, y + 1));
                    }
                } else {
                    if (!map.isWalkableAt(x, y - 1)) {
                        neighbours.add(map.getNode(x + dx, y - 1));
                    }
                    if (!map.isWalkableAt(x, y + 1)) {
                        neighbours.add(map.getNode(x + dx, y + 1));
                    }
                }
            }
        }

        return neighbours;
    }
    
    /**
     * Method jumps from initialNode in direction, until it hits the goal Node, an unwalkable Node, or
     * detects a forced neighbour.
     *
     * The presence of a forced neighbour (depending on the direction of travel, a horizontal or vertical neighbour
     * that is not walkable) means we've found a jump successor.
     *
     * @param initialNode Initial Node to jump from
     * @param direction Direction to jump in
     * @param goalNode Goal Node to look for
     * @return Returns the goal Node, a jump Vertex successor when a forced neighbour is detected, or null if neither
     * was found
     * @throws InterruptedException
     */
    private Node jump(Node initialNode, Point direction, Node goalNode) throws InterruptedException {

        Node node = map.getNode(initialNode.x + direction.x, initialNode.y + direction.y);

        if (node == null || node.state == NodeState.WALL)
            return null;

        if (node == goalNode)
            return node;
        
        if (node.status == NodeStatus.CLOSED || node.status == NodeStatus.JUMPED)
        	return null;

        node.parent = initialNode;
        node.status = NodeStatus.PEEKED;
        addToHistory(node);

        double x = node.x;
        double y = node.y;
        double dx = direction.x;
        double dy = direction.y;
        
        // Horizontal
        if (dy == 0) {
            if (!map.allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                if ((map.isWalkableAt(x, y - 1) && !map.isWalkableAt(x - dx, y - 1))
                    || (map.isWalkableAt(x, y + 1) && !map.isWalkableAt(x - dx, y + 1)))
                    return node;    
            } else {
                if ((map.isWalkableAt(x + dx, y - 1) && !map.isWalkableAt(x, y - 1))
                    || (map.isWalkableAt(x + dx, y + 1) && !map.isWalkableAt(x, y + 1)))
                    return node;
            }
        // Vertical
        } else if (dx == 0) {
            if (!map.allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                if ((map.isWalkableAt(x - 1, y) && !map.isWalkableAt(x - 1, y - dy))
                    || (map.isWalkableAt(x + 1, y) && !map.isWalkableAt(x + 1, y - dy)))
                    return node;
            } else {
                if ((map.isWalkableAt(x - 1, y + dy) && !map.isWalkableAt(x - 1, y))
                    || (map.isWalkableAt(x + 1, y + dy) && !map.isWalkableAt(x + 1, y)))
                    return node;
            }
        } else {
            // If we're moving in a diagonal direction, first check for forced neighbours ...
            if (map.allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                if ((!map.isWalkableAt(x - dx, y) && map.isWalkableAt(x - dx, y + dy))
                        || (!map.isWalkableAt(x, y - dy) && map.isWalkableAt(x + dx, y - dy)))
                    return node;
            } else {
            	if (!map.isWalkableAt(x - dx,  y) && !map.isWalkableAt(x, y - dy))
            		return null;
            }

            // ... then jump horizontally and vertically from diagonal position
            if (jump(node, new Point(dx, 0), goalNode) != null
                    || jump(node, new Point(0, dy), goalNode) != null)
                return node;
        }

        return jump(node, direction, goalNode);
    }

    private Point getDirection(Node from, Node to) {
    	
    	int dx = (int)Math.floor(Math.max(-1, Math.min(1, to.x - from.x)));
        int dy = (int)Math.floor(Math.max(-1, Math.min(1, to.y - from.y)));

        return new Point(dx, dy);

    }

}
