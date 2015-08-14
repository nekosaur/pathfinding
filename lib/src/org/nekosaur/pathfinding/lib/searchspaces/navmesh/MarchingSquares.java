package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.util.LinkedList;
import java.util.List;
import org.nekosaur.pathfinding.lib.common.Vertex;

public class MarchingSquares {
	
	private final int[][] table = {
		 /*  ◻, ◱, ◲, ⬓, ◳, ⍂, ◨, ◰, ◰, ◧, ⍁, ◳, ⬒, ◲, ◱, ◼ */
	   /*◻*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	   /*◱*/{0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
	   /*◲*/{0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
	   /*⬓*/{0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0},
	   /*◳*/{0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
	   /*⍂*/{0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0},
	   /*◨*/{0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0},
	   /*◰*/{0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
	   /*◰*/{0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0},
	   /*◧*/{0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0},
	   /*⍁*/{0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0},
	   /*◳*/{0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
	   /*⬒*/{0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0},
	   /*◲*/{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0},
	   /*◱*/{0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0},
	   /*◼*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	};
	
	private int width;
	private int height;
	
	private int[][] data;
	
	public MarchingSquares(int[][] data) {
		this.width = data[0].length;
		this.height = data.length;
		this.data = data;
	}
	
	public List<Vertex> identifyPerimeter(int x, int y) {
		if (!valueExists(x, y))
			throw new IllegalArgumentException("Vertex " + new Vertex(x, y) + " does not exist in data set");
		
		int index = calculateIndex(x, y);
		
		if (index <= 0 || index == 15)
			throw new IllegalArgumentException("Vertex " + new Vertex(x, y) + " does not lie on a perimeter");
		
		int startx = x;
		int starty = y;
		List<Vertex> perimeter = new LinkedList<>();
		Direction previous = null;
		int previousIndex = 0;
		
		do {
			System.out.println("Adding " + new Vertex(x + 1, y + 1) + " to perimeter");
			Direction direction = null;
			
			index = calculateIndex(x, y);
			System.out.println("Index="+index);
			switch (index) {
				case 1:
					direction = Direction.S;
					break;
				case 2:
					direction = Direction.E;
					break;
				case 3:
					direction = previous == Direction.W ? Direction.W : Direction.E;
					break;
				case 4:
					direction = Direction.N;
					break;
				case 5:
					direction = previous == Direction.E ? Direction.S : Direction.N;
					break;
				case 6:
					direction = previous == Direction.S ? Direction.S : Direction.N;
					break;
				case 7:
					direction = Direction.N;
					break;
				case 8:
					direction = Direction.W;
					break;
				case 9:
					direction = previous == Direction.S ? Direction.S : Direction.N;
					break;
				case 10:
					direction = previous == Direction.E ? Direction.S : Direction.N;
					break;
				case 11:
					direction = Direction.E;
					break;
				case 12:
					direction = previous == Direction.E ? Direction.E : Direction.W;
					break;
				case 13:
					direction = Direction.S;
					break;
				case 14:
					direction = Direction.W;
					break;
			}

			if (previousIndex != index)
				perimeter.add(new Vertex(x + 1, y + 1));
			
			x += direction.x;
			y += direction.y;
			previous = direction;
			previousIndex = index;
			
			System.out.println("New direction is " + direction);
			
		} while (x != startx || y != starty);
		
		// We add the start vertex again to close the loop
		perimeter.add(new Vertex(startx + 1, starty + 1));
		
		return perimeter;
	}
	
	public int calculateIndex(int x, int y) {
		int index = 0;
				
		if (valueExists(x, y) && data[y][x] != 0)
			index |= 8;
		if (valueExists(x + 1, y) && data[y][x + 1] != 0)
			index |= 4;
		if (valueExists(x + 1, y + 1) && data[y + 1][x + 1] != 0)
			index |= 2;
		if (valueExists(x, y + 1) && data[y + 1][x] != 0)
			index |= 1;
		
		return index;
	}
	
	private boolean valueExists(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return false;
		else
			return true;
	}
}

enum Direction {
	N(0, -1), E(1, 0), S(0, 1), W(-1, 0);
	
	public final int x;
	public final int y;
	
	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class Edge {
	private final Vertex start;
	private final Vertex end;
	
	public Edge(int sx, int sy, int ex, int ey) {
		start = new Vertex(sx, sy);
		end = new Vertex(ex, ey);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

}
