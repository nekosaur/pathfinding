package org.nekosaur.pathfinding.lib.common;

public class AABB {
    public final double x;
    public final double y;
    public final double width;
    public final double height;
    public final double cx;
    public final double cy;
    
    public AABB(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cx = x + width / 2;
        this.cy = y + height / 2;
    }
    
    public boolean contains(Point v) {
        return (v.x >= x && v.x < x + width) && (v.y >= y && v.y < y + width);
    }
    
    public boolean contains(double vx, double vy) {
    	return (vx >= x && vx < x + width) && (vy >= y && vy < y + width); 
    }
    
    @Override
    public String toString() {
        return String.format("{x=%.1f, y=%.1f, w=%.1f, h=%.1f}", x, y, width, height);
    }
    
}
