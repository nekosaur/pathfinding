package org.nekosaur.pathfinding.lib.pathfinders.theta;

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

import java.util.ArrayList;

/**
 * @author nekosaur
 */
public class LazyThetaFinder extends AbstractPathfinder {
    @Override
    public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {

        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);

        BinaryHashHeap<Node> openList = new BinaryHashHeap<Node>(Node.class, map.getWidth() * map.getHeight());

        startClock();

        startNode.g = 0;
        openList.add(startNode);

        // While we still have nodes to check in the open list
        while (openList.size() > 0) {

            operations++;

            // Get node with best score from open list
            Node currentNode = openList.remove();

            setNode(map, currentNode);

            // If we've reached the goal, reconstruct complete path
            if (currentNode.equals(goalNode)) {
                return new Result(reconstructPath(goalNode), stopClock(), operations);
            }

            currentNode.status = NodeStatus.CLOSED;
            addToHistory(currentNode);

            for (Node neighbourNode : map.getNeighbours(currentNode)) {

                if (neighbourNode == null)
                    continue;

                // If neighbour is already closed, skip it
                if (neighbourNode.status == NodeStatus.CLOSED)
                    continue;

                // ... or if it's not walkable, then skip it.
                if (neighbourNode.state == NodeState.WALL)
                    continue;

                boolean isInOpenList = neighbourNode.status == NodeStatus.OPEN;

                if (!isInOpenList) {
                    neighbourNode.g = Double.MAX_VALUE;
                    neighbourNode.parent = null;
                }

                double g = neighbourNode.g;

                computeCost(currentNode, neighbourNode);

                if (neighbourNode.g < g) {
                    neighbourNode.h = weight * heuristic.calculate(neighbourNode.delta(goalNode));

                    // Add node to open list if it's not on it already
                    if (!isInOpenList) {
                        neighbourNode.status = NodeStatus.OPEN;
                        openList.add(neighbourNode);
                    } else {
                        openList.update(neighbourNode);
                    }

                    // Node has been changed, add copy to history
                    addToHistory(neighbourNode);

                }
            }

        }

        return new Result(new ArrayList<Vertex>(), stopClock(), operations);
    }

    /**
     * Compared to Theta*, Lazy Theta* starts by assuming a node can always be reached by line of sight
     */
    private void computeCost(Node currentNode, Node neighbourNode) {
        Node parent = currentNode.parent != null ? currentNode.parent : currentNode;
        double g = parent.g + Heuristics.euclidean.calculate(parent.delta(neighbourNode));
        if (g < neighbourNode.g) {
            neighbourNode.g = g;
            neighbourNode.parent = parent;
        }
    }

    /**
     * If a node can't be reached by line of sight (which is only checked when node is processed from open list), we need to
     * calculate the normal g score and set correct parent
     */
    private void setNode(SearchSpace map, Node node) {
        if (!hasLineOfSight(map, node.parent, node)) {
            double minG = Double.MAX_VALUE;
            Node minNode = null;
            for (Node neighbourNode : map.getNeighbours(node)) {
                if (neighbourNode.status != NodeStatus.CLOSED)
                    continue;

                double g = neighbourNode.g + Heuristics.euclidean.calculate(node.delta(neighbourNode));

                if (g < minG) {
                    minNode = neighbourNode;
                    minG = g;
                }
            }

            if (minNode != null) {
                node.parent = minNode;
                node.g = minG;
            }
        }
    }

    private boolean hasLineOfSight(SearchSpace map, Node n1, Node n2) {
        if (n1 == null || n2 == null)
            return false;

        return hasLineOfSight(map, n1.x, n1.y, n2.x, n2.y);
    }

    /**
     * Code from http://playtechs.blogspot.se/2007/03/raytracing-on-grid.html
     *
     */
    private boolean hasLineOfSight(SearchSpace map, int x0, int y0, int x1, int y1)
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
