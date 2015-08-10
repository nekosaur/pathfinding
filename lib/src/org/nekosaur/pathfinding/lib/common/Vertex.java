package org.nekosaur.pathfinding.lib.common;

import org.nekosaur.pathfinding.lib.node.Node;

public class Vertex /*implements Comparable<Vertex>*/ {
	
	private int cachedHash;
    public final int x;
    public final int y;
       
    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Vertex delta(Vertex v) {
    	return new Vertex(Math.abs(this.x - v.x), Math.abs(this.y - v.y));
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
        if (o == null || !(o instanceof Vertex)) return false;
        Vertex position = (Vertex) o;
        if (x != position.x || y != position.y) return false;
        return true;
    }

    @Override
    public int hashCode() {
    	int result = cachedHash;
    	if (result == 0) {
    		result = 1 + x;
    		result = 31 * result + y;
    	} 
        return result;
    }

    @Override
    public String toString() {
        return String.format("{x=%d,y=%d}", x, y);
    }

	public int compareTo(Node node) {
		return 0;
	}
	
	public Vertex copy() {
		return new Vertex(x, y);
	}
    
}
