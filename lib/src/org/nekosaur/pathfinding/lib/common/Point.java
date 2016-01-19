package org.nekosaur.pathfinding.lib.common;

import org.nekosaur.pathfinding.lib.node.Node;

public class Point /*implements Comparable<Vertex>*/ {
	
	private int cachedHash;
    public final double x;
    public final double y;
       
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Point delta(Point v) {
    	return new Point(Math.abs(this.x - v.x), Math.abs(this.y - v.y));
    }
    
    /*
    @Override
    public int compareTo(Vertex p) {
        if (p != null) {
            if (this.equals(p)) {
                return 0;
            } else if (y < p.y) {
                return -1;
            } else if (y > p.y) {
                return 1;
            }else if (x < p.x) {
                return -1;
            } else if (x > p.x) {
                return 1;
            }
        }

        return 0;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Point)) return false;
        Point position = (Point) o;
        if (x != position.x || y != position.y) return false;
        return true;
    }

    @Override
    public int hashCode() {
    	long result = cachedHash;
    	if (result == 0) {
    		result = 1 + Double.doubleToLongBits(x);
    		result = 31 * result + Double.doubleToLongBits(y);
    	} 
        return (int)result;
    }

    @Override
    public String toString() {
        return String.format("{x=%.1f,y=%.1f}", x, y);
    }

	public int compareTo(Node node) {
		return 0;
	}
	
	public Point copy() {
		return new Point(x, y);
	}
    
}
