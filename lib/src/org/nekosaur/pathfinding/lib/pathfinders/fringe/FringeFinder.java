package org.nekosaur.pathfinding.lib.pathfinders.fringe;

import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.NodeNotFoundException;
import org.nekosaur.pathfinding.lib.exceptions.SearchSpaceNotSupportedException;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.pathfinders.AbstractPathfinder;

import java.util.LinkedList;

/**
 * Fringe Search: Beating A* at Pathfinding on Game Maps
 * (http://webdocs.cs.ualberta.ca/~holte/Publications/fringe.pdf)
 *
 * @author nekosaur
 *
 */
public class FringeFinder extends AbstractPathfinder {
    @Override
    public Result findPath(SearchSpace map, Vertex start, Vertex goal, Heuristic heuristic, double weight) throws NodeNotFoundException, SearchSpaceNotSupportedException, InterruptedException {

        Node startNode = map.getNode(start.x, start.y);
        Node goalNode = map.getNode(goal.x, goal.y);

        Int2DoubleOpenHashMap cache = new Int2DoubleOpenHashMap(map.getWidth() * map.getHeight(), 1f);
        LinkedList<Node> nowList = new LinkedList<>();
        LinkedList<Node> laterList = new LinkedList<>();

        startClock();

        startNode.g = 0;
        startNode.h = weight * heuristic.calculate(startNode.delta(goalNode));
        startNode.parent = null;

        boolean solved = false;
        double bound = startNode.h;

        cache.put(startNode.hashCode(), 0d);
        nowList.push(startNode);

        //TODO: this whole finder needs to be checked. not sure it's accurate

        while (!solved) {
//			System.out.println("New iteration, bound is " + bound);
            double newBound = Double.MAX_VALUE;

//			System.out.println("++++ NEW ITERATION!");

            while (!nowList.isEmpty()) {
                operations++;

                Node node = nowList.pop();

//				System.out.println("Getting node with hash " + nodeHash);
//				System.out.println("Current node: " + node);

                double g = cache.get(node.hashCode());
                node.h = heuristic.calculate(node.delta(goalNode));

                if (!Node.areEqualDouble(g + node.h, bound, 6) && g + node.h > bound) {
//					System.out.println("Node has higher F score than bound, saving for later");
                    newBound = Math.min(newBound, node.getF());

                    laterList.push(node);

                    if (node.status != NodeStatus.CLOSED) {
                        node.status = NodeStatus.CLOSED;
                        addToHistory(node);
                    }

                    continue;
                }

                if (node.equals(goalNode)) {
                    solved = true;
                    break;
                }

                node.status = NodeStatus.OPEN;
                addToHistory(node);

                for (Node neighbour : map.getNeighbours(node)) {

                    if (node.parent != null && node.parent.equals(neighbour)) {
//						System.out.println("Neighbour " + neighbour + " is node's parent, skipping!");
                        continue;
                    }

                    double gs = node.g + map.getMovementCost(node, neighbour);

//					System.out.println("Found neighbour: " + neighbour + " with new G score " + gs);

                    //long neighbourHash = map.getHash(neighbour);

                    if (cache.containsKey(neighbour.hashCode())) {
                        if (gs >= cache.get(neighbour.hashCode())) {
//							System.out.println("We've already found a cheaper route to this node");
                            continue;
                        }
                    }

                    neighbour.g = gs;
                    neighbour.parent = node;

                    if (!nowList.contains(neighbour))
                        nowList.push(neighbour);

//					System.out.println("Adding " + neighbour + " to now list");
                    cache.put(neighbour.hashCode(), neighbour.g);
                }

                nowList.remove(node);

            }

            bound = newBound == Double.MAX_VALUE ? bound : newBound;

            LinkedList<Node> tmpList = nowList;
            nowList = laterList;
            laterList = tmpList;

        }

        return new Result(reconstructPath(goalNode), stopClock(), operations);
    }

}
