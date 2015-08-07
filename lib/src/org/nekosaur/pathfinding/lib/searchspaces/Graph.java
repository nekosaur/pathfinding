package org.nekosaur.pathfinding.lib.searchspaces;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class Graph extends AbstractSearchSpace {
	
	private final SecureRandom rand = new SecureRandom();
    private Long2ObjectOpenHashMap<GraphNode<Node>> nodes;
    private long[] keys;
    
    private Graph(int width, int height) {
    	super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
        this.nodes = new Long2ObjectOpenHashMap<>(width * height);
        this.keys = new long[width * height];
    }
    
    public static SearchSpace create(int[][] nodes, int[][] edges) {
    	
    	Graph graph = new Graph(nodes[0].length, nodes.length);
       
        ArrayList<Node> indexToVertex = new ArrayList<>();
        
        // Add nodes
		for (int y = 0; y < nodes.length; y++) {
            for (int x = 0; x < nodes[0].length; x++) {
            	if (nodes[y][x] > 0) {
            		Node n = new Node(x, y, NodeState.EMPTY);
	                graph.addNode(n);
	                indexToVertex.add(n);
            	}
            }
        }

        // Add edges
        for (int i = 0; i < edges.length; i++) {
            Node n1 = indexToVertex.get(i);
            
            for (int e = 0; e < edges[i].length; e++) {
                if (edges[i][e] > 0) {
                    Node n2 = indexToVertex.get(e);
                    System.out.println("Adding edge from " + n1 + " to " + n2);
                    graph.addEdge(n1, n2, edges[i][e], true);
                }
            }
        }
        
        return (SearchSpace)graph;
    }

    private boolean containsNode(Vertex p) {
        return nodes.containsKey(keys[p.y * height + p.x]);
    }

    private boolean addNode(Node node) {
        if (containsNode(node))
            return false;

        // TODO: Check spread of hash function and index
        long hash = hash(node);
        keys[node.y * height + node.x] = hash;
        nodes.put(hash, new GraphNode<>(node));

        return true;
    }
    
    public Node getNode(int x, int y) {
        return getNode(new Vertex(x, y));
    }

    public Node getNode(Vertex p) {
        if (!containsNode(p))
            return null;

        return nodes.get(keys[p.y * height + p.x]).getNode();
    }

    private GraphNode<Node> getGraphNode(Node n) {
        return nodes.get(keys[n.y * height + n.x]);
    }

    public boolean addEdge(Node n1, Node n2, double weight, boolean directed) {
        if (!containsNode(n1) || !containsNode(n2))
            return false;

        GraphNode<Node> g1 = getGraphNode(n1);
        GraphNode<Node> g2 = getGraphNode(n2);

        g1.addEdge(g2, weight);
        if (!directed)
            g2.addEdge(g1, weight);

        return true;
    }   
  
    public Node getByHash(long hash) {
		return nodes.get(hash).getNode();
	}
	
	public long getHash(int x, int y) {
		return keys[y * height + x];
	}
	
	public long getHash(Vertex p) {
		return getHash(p.x, p.y);
	}
    
    public boolean isWalkableAt(int x, int y) {
        return (isInsideGrid(x, y) && nodes.containsKey(keys[y * height + x])/* && grid.get(gridHash[y * height + x]).isWalkable()*/ );
    }

    public boolean isWalkableAt(Vertex p) {
        return isWalkableAt(p.x, p.y);
    }

    private boolean isInsideGrid(int x, int y) {
        return (x >= 0 && x < width) && (y >= 0 && y < height);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public ArrayList<Node> getNeighbours(Node node) {
        if (!containsNode(node))
            return new ArrayList<>();

        GraphNode<Node> g = getGraphNode(node);

        ArrayList<Node> neighbours = new ArrayList<>();
        
        g.getEdges().forEach(edge -> {
            neighbours.add(edge.getEnd());
        });

        return neighbours;
    }

    public double getMovementCost(Node start, Node end) {
        GraphNode<Node> g1 = getGraphNode(start);

        Optional<GraphEdge<Node>> opt = g1
                .getEdges()
                .stream()
                .filter(e -> e.getEnd().equals(end))
                .findFirst();

        if (opt.isPresent())
            return opt.get().getWeight();
        else
            return Double.MAX_VALUE;
    }

    private long hash(Node node) {
        long hash = rand.nextLong();

        hash ^= node.x;
        hash ^= node.y;

        return hash;
    }

}

class GraphNode<T extends Node> implements Comparable<Node> {

    private final T node;
    private final List<GraphEdge<T>> edges;

    public GraphNode(T node) {
        this.node = node;
        this.edges = new ArrayList<>();
    }

    public Node getNode() {
        return node;
    }

    public boolean addEdge(GraphNode<T> end, double weight) {
        edges.add(new GraphEdge<T>(node, end.node, weight));

        return true;
    }

    public List<GraphEdge<T>> getEdges() {
        return edges;
    }

    @Override
    public int compareTo(Node n) {
        return node.compareTo(n);
    }
}

class GraphEdge<T> {

    private final T start;
    private final T end;
    private double weight;

    public GraphEdge(T start, T end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }
}