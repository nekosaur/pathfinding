package org.nekosaur.pathfinding.lib.searchspaces.grid;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.exceptions.MissingMapDataException;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.searchspaces.AbstractSearchSpace;

@SuppressWarnings("restriction")
public class Grid extends AbstractSearchSpace {

	private final SecureRandom rand = new SecureRandom();
	private Long2ObjectOpenHashMap<Node> grid;
	private long[] keys;

	private Grid(int width, int height) {
		super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
		this.grid = new Long2ObjectOpenHashMap<Node>(width * height);
		this.keys = new long[Math.max(width, height) * Math.max(width, height)];
	}

	public static SearchSpace create(MapData data) {
		if (!data.getVertices().isPresent())
			throw new MissingMapDataException("Vertices missing");

		int[][] vertices = data.getVertices().get();

		Grid grid = new Grid(vertices[0].length, vertices.length);
		
		for (int y = 0; y < vertices.length; y++) {
            for (int x = 0; x < vertices[0].length; x++) {
                    grid.addNode(new Node(x, y, NodeState.parse(vertices[y][x])));
            }
        }
		
		return (SearchSpace)grid;
	}

	public static SearchSpace create(MapData data, EnumSet<Option> options) {
		Grid g = (Grid)Grid.create(data);
		g.options = options;
		return g;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	private void addNode(Node node) {
		long hash = hash(node);
		keys[(int)node.y * height + (int)node.x] = hash;
		grid.put(hash, node);
	}

	public Node getNode(double x, double y) {
		if (!isInsideGrid(x, y))
			return null;
		return grid.get(keys[(int)y * height + (int)x]);
	}
	
	public List<Node> getNeighbours(Node node) {
        ArrayList<Node> neighbours = new ArrayList<>();
        double x = node.x;
        double y = node.y;

        ArrayList<Point> directions = new ArrayList<>();
        
        boolean u = false, 
        		r = false, 
        		d = false, 
        		l = false;
        
        boolean ul = false, 
        		ur = false, 
        		dr = false, 
        		dl = false;

        if (isWalkableAt(x, y - 1)) {
        	directions.add(new Point(x, y - 1)); // up
        	u = true;
        }
        if (isWalkableAt(x + 1, y)) {
        	directions.add(new Point(x + 1, y)); // right
        	r = true;
        }
        if (isWalkableAt(x, y + 1)) {
        	directions.add(new Point(x, y + 1)); // down
        	d = true;
        }
        if (isWalkableAt(x - 1, y)) {
        	directions.add(new Point(x - 1, y)); // left
        	l = true;
        }
        
        ul = u && l;
        ur = u && r;
        dr = d && r;
        dl = d && l;

        if (allows(Option.DIAGONAL_MOVEMENT)) {
            if (!allows(Option.MOVING_THROUGH_WALL_CORNERS)) {
                if (ul && isWalkableAt(x - 1, y - 1))
                    directions.add(new Point(x - 1, y - 1)); // upleft
                if (ur && isWalkableAt(x + 1, y - 1))
                    directions.add(new Point(x + 1, y - 1)); // upright
                if (dr && isWalkableAt(x + 1, y + 1))
                    directions.add(new Point(x + 1, y + 1)); // downright
                if (dl && isWalkableAt(x - 1, y + 1))
                    directions.add(new Point(x - 1, y + 1)); // downleft
            } else {
            	if (isWalkableAt(x - 1, y - 1))
            		directions.add(new Point(x - 1, y - 1)); // upleft
            	if (isWalkableAt(x + 1, y - 1))
            		directions.add(new Point(x + 1, y - 1)); // upright
            	if (isWalkableAt(x + 1, y + 1))
            		directions.add(new Point(x + 1, y + 1)); // downright
            	if (isWalkableAt(x - 1, y + 1))
            		directions.add(new Point(x - 1, y + 1)); // downleft
            }
        }

        for (Point p : directions) {
            neighbours.add(grid.get(keys[(int)p.y * height + (int)p.x]));
        }

        return neighbours;
    }

    public boolean isWalkableAt(double x, double y) {
        return (isInsideGrid(x, y) && grid.get(keys[(int)y * height + (int)x]).state != NodeState.WALL);
    }

    private boolean isInsideGrid(double x, double y) {
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
		
		hash ^= Double.doubleToLongBits(node.x);
		hash ^= Double.doubleToLongBits(node.y);
		
		return hash;
	}

	@Override
	public Image draw(int side) {
		System.out.println("Grid draw");
		WritableImage image = new WritableImage(this.width, this.height);
		System.out.println(width + " " + height);

		PixelWriter pw = image.getPixelWriter();

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				pw.setColor(x, y, NodeState.color(grid.get(keys[y * this.height + x]).state));
			}
		}

		return resample(image, side);
	}

	public MapData getMapData() {
		int[][] vertices = new int[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				vertices[y][x] = grid.get(keys[y * height + x]).state.value;
			}
		}

		return new MapData(vertices, null);
	}

	@Override
	public SearchSpace copy() {
		return Grid.create(getMapData(), options);
	}
}
