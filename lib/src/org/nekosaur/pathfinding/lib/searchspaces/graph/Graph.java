package org.nekosaur.pathfinding.lib.searchspaces.graph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.nekosaur.pathfinding.lib.searchspaces.AbstractSearchSpace;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

@SuppressWarnings("restriction")
public class Graph extends AbstractSearchSpace {

    private final static int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {-1, -1},
            {0, -1},
            {1, -1},
            {1, 0},
            {1, 1},
            {0, 1},
            {-1, 1}
    };
	
	private final SecureRandom rand = new SecureRandom();
    private Long2ObjectOpenHashMap<GraphNode<Node>> nodes;
    private long[] keys;
    
    private Graph(int width, int height) {
    	super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
        this.nodes = new Long2ObjectOpenHashMap<>(width * height);
        this.keys = new long[width * height];
    }
    
    public static SearchSpace create(MapData mapData) {

        // TODO: sanity check
        byte[] data = mapData.data;

    	Graph graph = new Graph(mapData.width, mapData.height);

        ArrayList<Node> indexToVertex = new ArrayList<>();
        
        // Add nodes
        int x, y;
        for (int i = 0; i < data.length; i++) {
            if (data[i] <= 0) {
                x = i % mapData.width;
                y = i / mapData.width;

                Node n = new Node(x, y, NodeState.EMPTY);
                graph.addNode(n);
                indexToVertex.add(n);
            }
        }

        int nx, ny;
        Node n2;
        for (int i = 0; i < data.length; i++) {
            if (data[i] <= 0) {
                x = i % mapData.width;
                y = i / mapData.width;

                Node n1 = graph.getNode(x, y);

                for (int j = 0; j < DIRECTIONS.length; j++) {
                    nx = x + DIRECTIONS[j][0];
                    ny = y + DIRECTIONS[j][1];

                    if (nx >= 0 && nx < mapData.width && ny >= 0 && ny < mapData.height && data[ny * mapData.width + nx] <= 0) {
                        n2 = graph.getNode(nx, ny);
                        graph.addEdge(n1, n2, 1, true);
                    }
                }
            }
        }

        return (SearchSpace)graph;
    }

    private boolean containsNode(Point p) {
        return nodes.containsKey(keys[(int)p.y * height + (int)p.x]);
    }

    private boolean addNode(Node node) {
        if (containsNode(node))
            return false;

        // TODO: Check spread of hash function and index
        long hash = hash(node);
        keys[(int)node.y * height + (int)node.x] = hash;
        nodes.put(hash, new GraphNode<>(node));

        return true;
    }

    public Node getNode(double x, double y) {
        return getNode(new Point(x, y));
    }

    public Node getNode(Point p) {
        if (!containsNode(p))
            return null;

        return nodes.get(keys[(int)p.y * height + (int)p.x]).getNode();
    }

    private GraphNode<Node> getGraphNode(Node n) {
        return nodes.get(keys[(int)n.y * height + (int)n.x]);
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
	
	public long getHash(double x, double y) {
		return keys[(int)y * height + (int)x];
	}
	
	public long getHash(Point p) {
		return getHash(p.x, p.y);
	}
    
    public boolean isWalkableAt(double x, double y) {
        return (isInsideGrid(x, y) && nodes.containsKey(keys[(int)y * height + (int)x])/* && searchable.get(gridHash[y * height + x]).isWalkable()*/ );
    }

    @Override
    public Image draw(int side) {
        System.out.println("Graph draw");
        BufferedImage bi = new BufferedImage(side, side, 3);
        System.out.println(width + " " + height);

        int scale = side / this.width;
        int size = (int)((scale/(double)4)*3);
        int offset = (scale - size)/2;

        Graphics2D g2d = (Graphics2D)bi.getGraphics();

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, side, side);
        g2d.setColor(Color.WHITE);

        int x, y;
        GraphNode<Node> node;
        for (int i = 0;  i < keys.length; i++) {
            node = nodes.get(keys[i]);

            if (node != null) {
                x = (int) node.getNode().x;
                y = (int) node.getNode().y;

                g2d.fillOval(x * scale + offset, y * scale + offset, size, size);
            }

        }

        /*
        for (Map.Entry<DelaunayTriangle, Triangle> e : delaunayMap.entrySet()) {
            DelaunayTriangle dt = e.getKey();

            int[] xPoints = new int[] { (int)(dt.points[0].getX() * scale), (int)(dt.points[1].getX() * scale), (int)(dt.points[2].getX() * scale)};
            int[] yPoints = new int[] { (int)(dt.points[0].getY() * scale), (int)(dt.points[1].getY() * scale), (int)(dt.points[2].getY() * scale)};

            g2d.setColor(new Color(0.9607843f, 0.9607843f, 0.9607843f));
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.CYAN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            g2d.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
            g2d.drawLine(xPoints[2], yPoints[2], xPoints[0], yPoints[0]);
        }
        */

        return SwingFXUtils.toFXImage(bi, null);

    }

    public boolean isWalkableAt(Point p) {
        return isWalkableAt(p.x, p.y);
    }

    private boolean isInsideGrid(double x, double y) {
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

        hash ^= Double.doubleToLongBits(node.x);
        hash ^= Double.doubleToLongBits(node.y);

        return hash;
    }

    @Override
    public SearchSpace copy() {
        return null;
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