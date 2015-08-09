package org.nekosaur.pathfinding.lib.pathfinders.ida;

import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;

import java.util.concurrent.TimeUnit;

/**
 * IDA* pathfinder
 *
 * Based partly on Enhanced Iterative-Deepening Search* by Alexander Reinefeld and T.A. Marsland
 * (http://webdocs.cs.ualberta.ca/~tony/RecentPapers/pami94.pdf)
 *
 *
 * @author nekosaur
 */
public class IDAFinder extends AbstractPathfinder {

    private boolean pathSolved = false;
    private SearchSpace map;
    private Heuristic heuristic;

    @Override
    public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {
        this.map = map;
        this.heuristic = heuristic;

        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);

        startClock();

        // Set values for start Node so that we have an inital bound
        startNode.g = 0;
        startNode.h = heuristic.calculate(startNode.delta(goalNode));

        double bound = startNode.getF();
        pathSolved = false;

        while (!pathSolved) {

            // We reset map before each iteration to make sure graph is cleared of previous values
            map = map.copy();

            System.out.println("# Starting search with new bound = " + bound);

            bound = depthFirstSearch(startNode, goalNode, bound);

            // If bound is MAX_VALUE then we've either hit the execution limit or not found a solution
            if (bound == Double.MAX_VALUE)
                break;

        }

        return new Result(reconstructPath(goalNode), goalNode.g, stopClock(), operations);
    }

    private double depthFirstSearch(Node currentNode, Node goalNode, double bound) {
        operations++;

        System.out.println("Current node: " + currentNode);

        // Sanity check for execution time
        long time = stopClock();
        if (TimeUnit.NANOSECONDS.toSeconds(time) > 10) {
            return Double.MAX_VALUE;
        }

        if (currentNode.equals(goalNode)) {
            pathSolved = true;
            return 0f;
        }

        double newBound = Double.MAX_VALUE;

        for (Node neighbourNode : map.getNeighbours(currentNode)) {
            // Skip Node if we've already visited it, it is not walkable, or it is the parent of current Node
            if (neighbourNode.status == NodeStatus.VISITED)
                continue;

            if (currentNode.parent != null && currentNode.parent.equals(neighbourNode))
                continue;

            // Calculate stuff
            neighbourNode.status = NodeStatus.VISITED;
            neighbourNode.parent = currentNode;
            neighbourNode.g = currentNode.g + map.getMovementCost(currentNode, neighbourNode);
            neighbourNode.h = heuristic.calculate(neighbourNode.delta(goalNode));

            System.out.println("Checking neighbour: " + neighbourNode);

            addToHistory(neighbourNode);

            /* This is the important bit. If the the Node's F score is above the current bound, we abandon the subtree
             * and try to set the score to our new bound.
             */
            double b;
            if (!Node.areEqualDouble(neighbourNode.getF(), bound, 6) && neighbourNode.getF() > bound) {
                System.out.println("Neighbour is above bound, use it as possible new bound");
                b = neighbourNode.getF();
            } else {
                System.out.println("Neighbour is below bound, expand it!");
                System.out.println("--> Recursing to node " + neighbourNode);
                b = depthFirstSearch(neighbourNode, goalNode, bound);
                System.out.println("<-- Decursing to node " + currentNode);
            }

            if (pathSolved)
                return b;

            // The new bound will be the smallest F score above the current bound
            newBound = Math.min(newBound, b);

            neighbourNode.status = NodeStatus.INACTIVE;
            neighbourNode.parent = null;

            addToHistory(neighbourNode);

        }

        return newBound;
    }
}
