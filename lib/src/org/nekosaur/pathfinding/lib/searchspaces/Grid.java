package org.nekosaur.pathfinding.lib.searchspaces;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;

public class Grid extends AbstractSearchSpace {
	
	private boolean ALLOW_DIAGONAL_MOVEMENT = true;
	private boolean ALLOW_WALL_CORNER_MOVEMENT = true;

	private final SecureRandom rand = new SecureRandom();
	private Long2ObjectOpenHashMap<Node> grid;
	private long[] keys;
	
	public static SearchSpace create(int[][] vertices, int[][] edges) {
		Grid grid = new Grid(vertices[0].length, vertices.length);
		
		for (int y = 0; y < vertices.length; y++) {
            for (int x = 0; x < vertices[0].length; x++) {
                    grid.addNode(new Node(x, y, NodeState.parse(vertices[y][x])));
            }
        }
		
		return (SearchSpace)grid;
	}
	
	private Grid(int width, int height) {
		super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
		this.grid = new Long2ObjectOpenHashMap<Node>(width * height);
		this.keys = new long[Math.max(width, height) * Math.max(width, height)];
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	private void addNode(Node node) {
		long hash = hash(node);
		keys[node.y * height + node.x] = hash;
		grid.put(hash, node);
	}
	
	public Node getNode(int x, int y) {
		if (!isInsideGrid(x, y))
			return null;
		return grid.get(keys[y * height + x]);
	}
	
	public List<Node> getNeighbours(Node node) {
        ArrayList<Node> neighbours = new ArrayList<>();
        int x = node.x;
        int y = node.y;

        ArrayList<Vertex> directions = new ArrayList<>();
        
        boolean u = false, 
        		r = false, 
        		d = false, 
        		l = false;
        
        boolean ul = false, 
        		ur = false, 
        		dr = false, 
        		dl = false;

        if (isWalkableAt(x, y - 1)) {
        	directions.add(new Vertex(x, y - 1)); // up
        	u = true;
        }
        if (isWalkableAt(x + 1, y)) {
        	directions.add(new Vertex(x + 1, y)); // right
        	r = true;
        }
        if (isWalkableAt(x, y + 1)) {
        	directions.add(new Vertex(x, y + 1)); // down
        	d = true;
        }
        if (isWalkableAt(x - 1, y)) {
        	directions.add(new Vertex(x - 1, y)); // left
        	l = true;
        }
        
        ul = u && l;
        ur = u && r;
        dr = d && r;
        dl = d && l;

        if (ALLOW_DIAGONAL_MOVEMENT) {
            if (ALLOW_WALL_CORNER_MOVEMENT) {
                if (ul && isWalkableAt(x - 1, y - 1))
                    directions.add(new Vertex(x - 1, y - 1)); // upleft
                if (ur && isWalkableAt(x + 1, y - 1))
                    directions.add(new Vertex(x + 1, y - 1)); // upright
                if (dr && isWalkableAt(x + 1, y + 1))
                    directions.add(new Vertex(x + 1, y + 1)); // downright
                if (dl && isWalkableAt(x - 1, y + 1))
                    directions.add(new Vertex(x - 1, y + 1)); // downleft
            } else {
            	if (isWalkableAt(x - 1, y - 1))
            		directions.add(new Vertex(x - 1, y - 1)); // upleft
            	if (isWalkableAt(x + 1, y - 1))
            		directions.add(new Vertex(x + 1, y - 1)); // upright
            	if (isWalkableAt(x + 1, y + 1))
            		directions.add(new Vertex(x + 1, y + 1)); // downright
            	if (isWalkableAt(x - 1, y + 1))
            		directions.add(new Vertex(x - 1, y + 1)); // downleft
            }
        }

        for (Vertex p : directions) {
            neighbours.add(grid.get(keys[p.y * height + p.x]));
        }

        return neighbours;
    }

    public boolean isWalkableAt(int x, int y) {
        return (isInsideGrid(x, y) && grid.get(keys[y * height + x]).state != NodeState.WALL);
    }

    private boolean isInsideGrid(int x, int y) {
        return (x >= 0 && x < width) && (y >= 0 && y < height);
    }

    /**
     * Calculates movement cost from one node to another. This is currently hard coded to be 1 for hor/vert, and
     * sqrt(2) for diagonal movement, resulting in a minimum D cost of 1
     * @param n1
     * @param n2
     * @return
     */
    public double getMovementCost(Node n1, Node n2) {
        double dx = Math.abs(n1.x - n2.x);
        double dy = Math.abs(n1.y - n2.y);

        return (dx == 0 || dy == 0) ? 1 : Math.sqrt(2);
    }
	
	private long hash(Node node) {
		long hash = rand.nextLong();
		
		hash ^= node.x;
		hash ^= node.y;
		
		return hash;
	}
		
}
