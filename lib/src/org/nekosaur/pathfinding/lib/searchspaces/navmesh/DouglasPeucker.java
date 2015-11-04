package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.nekosaur.pathfinding.lib.common.Vertex;

/**
 * http://karthaus.nl/rdp/js/rdp2.js
 * http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
 * http://www.tomgibara.com/computer-vision/MarchingSquares.java
 * 
 * @author nekosaur
 *
 */
public class DouglasPeucker {
	
	private DouglasPeucker() {}
	
	public static List<Vertex> reduce(List<Vertex> list, double epsilon) {
		Point[] points = new Point[list.size()];
		for (int i = 0; i < list.size(); i++) {
			points[i] = new Point(list.get(i).x, list.get(i).y);
		}

		Point[] reduced = DouglasPeucker.reduce(points, epsilon);
		//Vertex[] vertices = new Vertex[reduced.length];
		List<Vertex> vertices = new LinkedList<>();

		for (int i = 0; i < reduced.length; i++)
			vertices.add(new Vertex((int)Math.round(reduced[i].x), (int)Math.round(reduced[i].y)));
			//vertices[i] = new Vertex((int)Math.round(reduced[i].x), (int)Math.round(reduced[i].y));

		return vertices;
	}
	
	public static Point[] reduce(Point[] list, double epsilon) {
		Point firstPoint = list[0];
		Point lastPoint = list[list.length - 1];
		if (list.length < 3)
			return list;
		
		int index = -1;
		double dmax = 0;
		
		for (int i = 1; i < list.length - 1; i++) {
			double d = shortestDistanceToLineSegment(firstPoint, lastPoint, list[i]);
			
			if (d > dmax) {
				dmax = d;
				index = i;
			}
		}
		
		System.out.println("Point furthest from line " + firstPoint + " to " + lastPoint + " is " + list[index] + " with d " + dmax);
		
		Point[] result = null;
		if (dmax > epsilon) {
			System.out.println("dmax is larger than epsilon, splitting line 0-" + (index + 1) + ", " + index + "-" + list.length);
			Point[] res1 = DouglasPeucker.reduce(Arrays.copyOfRange(list, 0, index + 1), epsilon);
			Point[] res2 = DouglasPeucker.reduce(Arrays.copyOfRange(list, index, list.length), epsilon);
			
			result = DouglasPeucker.concat(res1, res2);
		} else {
			result = new Point[] {firstPoint, lastPoint};
		}
		
		return result;
	}
		
	private static double sqr(double x ) { return x * x; }
	private static double dist2(Point v, Point w) { return DouglasPeucker.sqr(v.x - w.x) + DouglasPeucker.sqr(v.y - w.y); }
	
	public static double shortestDistanceToLineSegmentSquared(Point v, Point w, Point p) {
		double l2 = DouglasPeucker.dist2(v, w);
		if (l2 == 0) return DouglasPeucker.dist2(p, v);
		double  t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
		if (t < 0) return dist2(p, v);
		if (t > 0) return dist2(p, w);

		return dist2(p, new Point(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y)));
	}
	
	public static double shortestDistanceToLineSegment(Point start, Point end, Point point) {
		return Math.sqrt(DouglasPeucker.shortestDistanceToLineSegmentSquared(start, end, point));
	}
	
	private static Point[] concat(Point[] a, Point[] b) {
	   int aLen = a.length - 1;
	   int bLen = b.length;
	   Point[] c= new Point[aLen+bLen];
	   System.arraycopy(a, 0, c, 0, aLen);
	   System.arraycopy(b, 0, c, aLen, bLen);
	   return c;
	}
	
	public static Vertex topRight(List<Vertex> list) {
		Vertex topRight = list.get(0);
		
		for (int i = 1; i < list.size() - 1; i++) {
				
			Vertex v = list.get(i);
			
			if (v.x >= topRight.x)
				if (v.y <= topRight.y)
					topRight = v;
			
		}
		
		return topRight;
	}
	
	public static Vertex bottomRight(List<Vertex> list) {
		Vertex bottomRight = list.get(0);
		
		for (int i = 1; i < list.size() - 1; i++) {
			Vertex v = list.get(i);
			
			if (v.y >= bottomRight.y) {
				bottomRight = v;
			} else if (v.y == bottomRight.y) {
				if (v.x >= bottomRight.x)
					bottomRight = v;
			}
		}
		
		return bottomRight;
	}
	
	static class Point {
		public final double x;
		public final double y;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return String.format("{x=%f,y=%f}", x, y);
		}
	}
}
