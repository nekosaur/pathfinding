package org.nekosaur.pathfinding.lib.searchspaces;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.nekosaur.pathfinding.lib.common.AABB;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class QuadTree extends AbstractSearchSpace {
	
	private QuadNode root = null;
	
	private QuadTree(int width, int height) {
		super(width, height, EnumSet.noneOf(Option.class));
	}
	
	public static SearchSpace create(int[][] nodes, int[][] edges) {
		QuadTree qt = new QuadTree(nodes[0].length, nodes.length);
		
		qt.root = new QuadNode(0, 0, qt.width, qt.height, nodes);
		
		return (SearchSpace)qt;
	}
	
	public WritableImage draw() {
		WritableImage image = new WritableImage(width, height);
		return root.printQuad(image);
	}

	public List<Node> getNeighbours(Node n) {
		QuadNode q = root.getNearestNode(n.x, n.y);
		return q.getLeafNeighbours(n);
	}

	public double getMovementCost(Node n1, Node n2) {
		Vertex d = n1.delta(n2);
		
		return Math.sqrt(d.x*d.x + d.y*d.y);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Node getNode(int x, int y) {
		return root.getNearestLeaf(x, y);
	}

	public boolean isWalkableAt(int x, int y) {
		return getNode(x, y).state != NodeState.WALL;
	}
	
}


class QuadNode {
	
	enum Direction {NORTH, EAST, SOUTH, WEST};
	enum Quadrant {NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST};
	enum State {FREE, MIXED, OBSTRUCTED};
	
	private static int NODE_RESOLUTION = 1;
    
    private AABB region;
    
    private Node leaf = null;
    
    private QuadNode northWest;
    private QuadNode northEast;
    private QuadNode southEast;
    private QuadNode southWest;
    
    private QuadNode parent;
    
    private State state = State.MIXED;
                   
    public QuadNode(int x, int y, int width, int height, int[][] data) {
    	this.region = new AABB(x, y, width, height);
    	
    	List<Node> obstacles = new ArrayList<>();
    	
    	for (int ry = y; ry < y + height; ry++) {
    		for (int rx = x ; rx < x + width; rx++) {
    			if (data[ry][rx] > 0) {
//    				System.out.println("Found obstacle " + rx + " " + ry);
    				obstacles.add(new Node(rx, ry, NodeState.WALL));
    			}
    		}
    	}
    	
    	subdivide(obstacles);
    
    }
    
    public QuadNode(int x, int y, int width, int height, List<Node> obstacles, QuadNode parent) {
    	this.region = new AABB(x, y, width, height);
    	this.parent = parent;
    	
//    	System.out.println("Creating new node " + region + " with parent " + parent);
    	
    	for (Node o : obstacles) {
    		if (region.contains(o.x, o.y)) {
//    			System.out.println("Found obstacle in region " + o);
    			if (region.width <= NODE_RESOLUTION || height <= NODE_RESOLUTION) {
    				state = State.OBSTRUCTED;
    				leaf = o;
    			} else {
    				subdivide(obstacles);
    			}
    			return;
    		}
    	}
    	
    	state = State.FREE;
    	leaf = new Node(region.cx, region.cy, NodeState.EMPTY);
    }
    
    static boolean[][] adjacent = {
    	   /*NW,   NE,   SW,    SE */
    /*N*/	{true, true, false, false},
    /*E*/	{false, true, false, true},
    /*S*/	{false, false, true, true},
    /*W*/	{true, false, true, false}
    };
    
    static Quadrant[][] mirror = {
    		{Quadrant.SOUTHWEST, Quadrant.SOUTHEAST, Quadrant.NORTHWEST, Quadrant.NORTHEAST},
    		{Quadrant.NORTHEAST, Quadrant.NORTHWEST, Quadrant.SOUTHEAST, Quadrant.SOUTHWEST},
    		{Quadrant.SOUTHWEST, Quadrant.SOUTHEAST, Quadrant.NORTHWEST, Quadrant.NORTHEAST},
    		{Quadrant.NORTHEAST, Quadrant.NORTHWEST, Quadrant.SOUTHEAST, Quadrant.SOUTHWEST}
    };
    
    static Direction[] opposite = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
   
    /**
     * Returns true if the supplied Quadrant touches the specified Direction
     * @param dir
     * @param quad
     * @return
     */
    private boolean adjacent(Direction dir, Quadrant quad) {
    	return adjacent[dir.ordinal()][quad.ordinal()];
    }
    
    private Quadrant mirror(Direction dir, Quadrant quad) {
    	return mirror[dir.ordinal()][quad.ordinal()];
    }
    
    private Direction opposite(Direction dir) {
    	return opposite[dir.ordinal()];
    }
    
    /**
     * Returns which Quadrant of parent this QuadNode sits in
     * @return
     */
    private Quadrant quadrant() {
    	if (parent == null)
    		return null;
    	
    	if (parent.northEast.equals(this))
    		return Quadrant.NORTHEAST;
    	else if (parent.northWest.equals(this))
    		return Quadrant.NORTHWEST;
    	else if (parent.southWest.equals(this))
    		return Quadrant.SOUTHWEST;
    	else 
    		return Quadrant.SOUTHEAST;
    }
    
    private QuadNode child(Quadrant quad) {
    	switch (quad) {
	    	case NORTHWEST:
	    		return northWest;
	    	case NORTHEAST:
	    		return northEast;
	    	case SOUTHWEST:
	    		return southWest;
	    	case SOUTHEAST:
	    		return southEast;
	    	default:
	    		return null;
    	}
    }
    
    private void children(Direction direction, List<QuadNode> children) {
    	
    	if (isLeaf())
    		return;
    	
    	QuadNode q1 = null;
    	QuadNode q2 = null;
    	
    	switch (direction) {
	    	case NORTH:
	    		q1 = northWest;
	    		q2 = northEast;
	    		break;
	    	case EAST:
	    		q1 = northEast;
	    		q2 = southEast;
	    		break;
	    	case SOUTH:
	    		q1 = southEast;
	    		q2 = southWest;
	    		break;
	    	case WEST:
	    		q1 = southWest;
	    		q2 = northWest;
	    		break;
    	}
    	
    	if (q1.isLeaf()) {
    		children.add(q1);
    	} else {
    		q1.children(direction, children);
    	}
    	
    	if (q2.isLeaf()) {
    		children.add(q2);
    	} else {
    		q2.children(direction, children);
    	}
    }
    
    public QuadNode neighbour(Direction direction) {
    	QuadNode neighbour = null;
    	
    	// Find common ancestor. Common ancestor is the
    	if (parent != null && adjacent(direction, quadrant()))
    		neighbour = parent.neighbour(direction);
    	else
    		neighbour = parent;
    	
    	if (neighbour != null && !neighbour.isLeaf())
    		return neighbour.child(mirror(direction, quadrant()));
    	else
    		return neighbour;
    }
    
    public QuadNode neighbour(Direction direction, Quadrant corner) {
    	
    	QuadNode q = neighbour(direction);
    	
    	if (q == null)
    		return null;
    	
    	while (!q.isLeaf())
    		q = q.child(mirror(direction, corner));
    	
    	return q;
    }
    
    public void neighbours(Direction direction, List<QuadNode> neighbours) {
    	
    	QuadNode q = neighbour(direction);
    	
    	if (q != null) {
    		if (q.isLeaf()) {
    			neighbours.add(q);
    		} else {
    			q.children(opposite(direction), neighbours);
    		}
    	}
    }
    
    public List<QuadNode> neighbours() {
    	List<QuadNode> neighbours = new ArrayList<>();
    	neighbours(Direction.NORTH, neighbours);
    	neighbours(Direction.EAST, neighbours);
    	neighbours(Direction.SOUTH, neighbours);
    	neighbours(Direction.WEST, neighbours);
    	return neighbours;
    }
    
    public boolean isLeaf() {
    	return state != State.MIXED;
    }
        
    public Node getNearestLeaf(int x, int y) {
    	if (!region.contains(x, y))
    		return null;
    	
    	if (state != State.MIXED)
    		return leaf;
    	
    	QuadNode qn = getRegion(x, y);
    	
    	return qn.getNearestLeaf(x, y);
    		
    }
    
    QuadNode getNearestNode(int x, int y) {
    	if (!region.contains(x, y))
    		return null;
    	
    	if (state != State.MIXED)
    		return this;
    	
    	QuadNode qn = getRegion(x, y);
    	
    	return qn.getNearestNode(x, y);
    }
    
    List<Node> getLeafNeighbours(Node n) {
    	List<Node> leafNeighbours = new ArrayList<>();
    	List<QuadNode> neighbours = neighbours();
    	
    	for (QuadNode q : neighbours) {
    		if (q.leaf.state != NodeState.WALL)
    			leafNeighbours.add(q.leaf);
    	}
    	
    	return leafNeighbours;
    }
    
    private QuadNode getRegion(int x, int y) {
    	if (y < region.cy)
    		if (x < region.cx)
    			return northWest;
    		else
    			return northEast;
    	else
    		if (x < region.cx)
    			return southWest;
    		else
    			return southEast;
    }
    
    private void subdivide(List<Node> obstacles) {
//    	System.out.println("Subdividing " + region);
    	
    	state = State.MIXED;
    	
    	int newWidth = region.width / 2;
        int newHeight = region.height / 2;
    	
    	northWest = new QuadNode(region.x, region.y, newWidth, newHeight, obstacles, this);
    	northEast = new QuadNode(region.cx, region.y, newWidth, newHeight, obstacles, this);
    	southWest = new QuadNode(region.x, region.cy, newWidth, newHeight, obstacles, this);
    	southEast = new QuadNode(region.cx, region.cy, newWidth, newHeight, obstacles, this);
    }
        
    public WritableImage printQuad(WritableImage image) {
    	Random rand = new Random();
    	Color c = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    	
    	PixelWriter pw = image.getPixelWriter();
    	
    	int base;
    	if (leaf != null && leaf.state == NodeState.WALL) {
    		base = rand.nextInt(70) + 100;
    		c = new Color(base - 32, base, base);
    	} else {
    		base = rand.nextInt(100) + 120;
    		c = new Color(base, base - 32, base);
    	}    
    	
    	for (int x = region.x; x < region.x + region.width; x++) {
    		for (int y = region.y; y < region.y + region.height; y++) {
    			
    			pw.setArgb(x, y, c.getRGB());
    		}
    	}
    	
    	if (leaf != null && leaf.state == NodeState.EMPTY) {
    		pw.setArgb(leaf.x, leaf.y, new Color(255,0,0).getRGB());
    	}
    	
    	if (northWest != null) image = northWest.printQuad(image);
    	if (northEast != null) image = northEast.printQuad(image);
    	if (southEast != null) image = southEast.printQuad(image);
    	if (southWest != null) image = southWest.printQuad(image);
    	
    	return image;
    }
    
    @Override
    public String toString() {
    	return region.toString();
    }
}

enum QuadNodeDirection {
	W(0), NW(1), N(2), NE(3), E(4), SE(5), S(6), SW(7);
	
	int direction;
	
	private static final Map<Integer, QuadNodeDirection> intToEnum = new HashMap<>();

	static {
		for (QuadNodeDirection e : QuadNodeDirection.values()) {
			intToEnum.put(e.direction, e);
		}
	}
	
	QuadNodeDirection(int direction) {
		this.direction = direction;
	}
	
	public static QuadNodeDirection parse(int value) {
		return intToEnum.containsKey(value) ? intToEnum.get(value) : W;
	}
	
	public QuadNodeDirection opposite() {
		return QuadNodeDirection.parse((direction + 5) % 8);
	}
	
	int[][] mirror = {
		  /* W  NW N  NE E  SE S  SW */
	/*W*/	{4, 3, 2, 1, 0, 7, 6, 5},
	/*NW*/	{6, 5, 4, 3, 2, 1, 0, 7},
	/*N*/	{0, 7, 6, 5, 4, 3, 2, 1},
	/*NE*/	{2, 1, 0, 7, 6, 5, 4, 3},
	/*E*/	{4, 3, 2, 1, 0, 7, 6, 5},
	/*SE*/	{6, 5, 4, 3, 2, 1, 0, 7},
	/*S*/	{0, 7, 6, 5, 4, 3, 2, 1},
	/*SW*/	{2, 1, 0, 7, 6, 5, 4, 3}
	};
	
	public QuadNodeDirection mirror(QuadNodeDirection plane) {
		return QuadNodeDirection.parse(mirror[plane.direction][this.direction]);
	}
}

