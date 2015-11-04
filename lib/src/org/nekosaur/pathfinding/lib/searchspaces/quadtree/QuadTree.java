package org.nekosaur.pathfinding.lib.searchspaces.quadtree;

import java.util.*;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import org.nekosaur.pathfinding.lib.common.AABB;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;

import javafx.scene.image.WritableImage;
import org.nekosaur.pathfinding.lib.searchspaces.AbstractSearchSpace;

@SuppressWarnings("restriction")
public class QuadTree extends AbstractSearchSpace {
	
	private QuadNode root = null;
	
	private QuadTree(int width, int height, EnumSet<Option> options) {
		super(width, height, options);
	}
	
	public static SearchSpace create(MapData data, EnumSet<Option> options) {
		int[][] nodes = data.getVertices().get();

		QuadTree qt = new QuadTree(nodes[0].length, nodes.length, options);
		
		qt.root = new QuadNode(0, 0, qt.width, qt.height, nodes);
		
		return (SearchSpace)qt;
	}

	@Override
	public Image draw(int side) {
		System.out.println("drawing");
		WritableImage image = new WritableImage(this.width, this.height);
		return resample(root.printQuad(image), side);
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

	@Override
	public Node getNode(double x, double y) {
		return null;
	}

	public Node getNode(int x, int y) {
		return root.getNearestLeaf(x, y);
	}

	public QuadNode getQuadNode(int x, int y) {
		return root.getNearestNode(x, y);
	}

	public boolean isWalkableAt(int x, int y) {
		if (getQuadNode(x, y).isObstructed())
			return false;

		return getNode(x, y).state != NodeState.WALL;
	}

	public MapData getMapData() {
		int[][] vertices = new int[height][width];

		List<Node> nodes = root.getNodes(new ArrayList<>());

		for (Node n : nodes) {
			vertices[n.y][n.x] = n.state.value;
		}

		return new MapData(vertices, null);
	}

	@Override
	public SearchSpace copy() {
		return QuadTree.create(getMapData(), options);
	}

	public static class QuadNode {

		enum Direction {NORTH, EAST, SOUTH, WEST};
		enum Quadrant {NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST};
		enum State {FREE, MIXED, OBSTRUCTED};

		private static int NODE_RESOLUTION = 1;

		private final AABB region;

		private Node leaf = null;

		private QuadNode northWest;
		private QuadNode northEast;
		private QuadNode southEast;
		private QuadNode southWest;

		private QuadNode parent;

		private State state = State.MIXED;

		public QuadNode(int x, int y, int width, int height, int[][] data) {
			this.region = new AABB(x, y, width, height);

			Object2ObjectOpenHashMap<Vertex, Node> obstacles = new Object2ObjectOpenHashMap<>(width * height);
			//List<Node> obstacles = new ArrayList<>();

			for (int ry = y; ry < y + height; ry++) {
				for (int rx = x ; rx < x + width; rx++) {
					if (data[ry][rx] > 0) {
//    				System.out.println("Found obstacle " + rx + " " + ry);
						Node n = new Node(rx, ry, NodeState.WALL);
						obstacles.put(n, n);
					}
				}
			}

			subdivide(obstacles);

		}

		public QuadNode(int x, int y, int width, int height, Object2ObjectOpenHashMap<Vertex, Node> obstacles, QuadNode parent) {
			this.region = new AABB(x, y, width, height);
			this.parent = parent;

//    	System.out.println("Creating new node " + region + " with parent " + parent);

			for (int ix = x; ix < x + width; ix++) {
				for (int iy = y; iy < y + height; iy++) {
					Node n = obstacles.get(new Vertex(ix, iy));
					if (n == null)
						continue;

					if (region.width <= NODE_RESOLUTION || height <= NODE_RESOLUTION) {
						state = State.OBSTRUCTED;
						leaf = n;
					} else {
						subdivide(obstacles);
					}
					return;
				}
			}

			state = State.FREE;
			leaf = new Node(region.cx, region.cy, NodeState.EMPTY);
		}

		private void subdivide(Object2ObjectOpenHashMap<Vertex, Node> obstacles) {
//    	System.out.println("Subdividing " + region);

			state = State.MIXED;

			int newWidth = region.width / 2;
			int newHeight = region.height / 2;

			northWest = new QuadNode(region.x, region.y, newWidth, newHeight, obstacles, this);
			northEast = new QuadNode(region.cx, region.y, newWidth, newHeight, obstacles, this);
			southWest = new QuadNode(region.x, region.cy, newWidth, newHeight, obstacles, this);
			southEast = new QuadNode(region.cx, region.cy, newWidth, newHeight, obstacles, this);

			// recombine node if all children are obstructed
			if (northWest.state == State.OBSTRUCTED
					&& northEast.state == State.OBSTRUCTED
					&& southWest.state == State.OBSTRUCTED
					&& southEast.state == State.OBSTRUCTED) {
				northWest = null;
				northEast = null;
				southWest = null;
				southEast = null;
				state = State.OBSTRUCTED;
			}
		}

		public AABB getRegion() {
			return region;
		}

		public List<Node> getNodes(List<Node> nodes) {

			if (state == State.OBSTRUCTED) {
				if (leaf != null) {
					nodes.add(leaf);
				} else {
					for (int x = region.x; x < region.x + region.width; x++)
						for (int y = region.y; y < region.y + region.width; y++)
							nodes.add(new Node(x, y, NodeState.WALL));
				}
				return nodes;
			} else if (state == State.MIXED){
				nodes = northWest.getNodes(nodes);
				nodes = northEast.getNodes(nodes);
				nodes = southWest.getNodes(nodes);
				nodes = southEast.getNodes(nodes);
			}

			return nodes;
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

		public boolean isObstructed() {
			return state == State.OBSTRUCTED;
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
				if (q.state == State.FREE)
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
			else if (x < region.cx)
				return southWest;
			else
				return southEast;
		}

		public WritableImage printQuad(WritableImage image) {
			Random rand = new Random();

			PixelWriter pw = image.getPixelWriter();

			double b = 0.6 + Math.max(rand.nextDouble(), 0.2);

			Color c = Color.PINK;
			if (state == State.OBSTRUCTED) {
				c = NodeState.color(NodeState.WALL);
				c = c.deriveColor(0, 1, b, 1);
			} else if (leaf != null) {
				c = NodeState.color(leaf.state);
				c = c.deriveColor(0, 1, b, 1);

			}

			for (int x = region.x; x < region.x + region.width; x++) {
				for (int y = region.y; y < region.y + region.height; y++) {

					pw.setColor(x, y, c);
				}
			}

		/*
    	if (leaf != null && leaf.state == NodeState.EMPTY) {
    		pw.setArgb(leaf.x, leaf.y, new Color(255,0,0).getRGB());
    	}*/

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

