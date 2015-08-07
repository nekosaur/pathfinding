package org.nekosaur.pathfinding.lib.common;

public class AABB {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final int cx;
    public final int cy;
    
    public AABB(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cx = x + width / 2;
        this.cy = y + height / 2;
    }
    
    public boolean contains(Vertex v) {
        return (v.x >= x && v.x < x + width) && (v.y >= y && v.y < y + width);
    }
    
    public boolean contains(int vx, int vy) {
    	return (vx >= x && vx < x + width) && (vy >= y && vy < y + width); 
    }
    
    @Override
    public String toString() {
        return String.format("{x=%d, y=%d, w=%d, h=%d}", x, y, width, height);
    }
    
}
