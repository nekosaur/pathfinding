package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.util.LinkedList;
import java.util.List;

import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.datastructures.BinaryHashHeap;

public class VisvalingamWhyatt {

	private VisvalingamWhyatt() {};
	
	public static List<Vertex> reduce(List<Vertex> list, int verticesToKeep) {
		BinaryHashHeap<Point> heap = new BinaryHashHeap<Point>(Point.class, list.size()*2);
		
		LinkedList<Point> points = new LinkedList<>();
		
		Point n = null;
		Point s = null;
		Point w = null;
		Point e = null;
		
		for (int i = 0; i < list.size(); i++) {
			Point point = new Point(list.get(i).x, list.get(i).y);
			points.add(point);
			
			// This is not part of proper algorithm, own attempt to prettify hull
			if (w == null)
				w = point;
			else
				if (point.x <= w.x)
					w = point;
			if (n == null)
				n = point;
			else
				if (point.y <= n.y)
					n = point;
			if (s == null)
				s = point;
			else
				if (point.y >= s.y)
					s = point;
			if (e == null)
				e = point;
			else
				if (point.x >= e.x)
					e = point;
			
		}
		
		for (int i = 1; i < points.size() - 1; i++) {
			points.get(i).area = VisvalingamWhyatt.computeArea(points, i);
			System.out.println("Triangle area for point " + points.get(i) + " is " + points.get(i).area);
			heap.add(points.get(i));
		}
		
		//System.out.println(heap.size());
		double minArea = 2;
		//while (points.size() > verticesToKeep) {
		Point point = null;
		while (heap.size() > 0 && points.size() > 5) {
			point = heap.remove();
			if (point.area > minArea)
				continue;
			// Continuation of attempt to prettify hull
			if (point.equals(n) || point.equals(w) || point.equals(e) || point.equals(s))
				continue;
			//Point smallestPoint = heap.remove();
			//System.out.println("point="+point);
			int i = points.indexOf(point);
			//System.out.println("index="+i);
			points.remove(point);
			if (i - 1 > 0) {
				Point previous = points.get(i - 1);
				previous.area = VisvalingamWhyatt.computeArea(points, i - 1);
				//System.out.println("New triangle area for point " + previous + " is " + previous.area);
				heap.update(previous);
			}
			if (i < points.size() - 1) {
				Point next = points.get(i);		
				next.area = VisvalingamWhyatt.computeArea(points, i);
				//System.out.println("New triangle area for point " + next + " is " + next.area);				
				heap.update(next);
			}
			
		}
				
		List<Vertex> reduced = new LinkedList<>();
		
		for (Point p : points) {
			reduced.add(new Vertex(p.x, p.y));
		}
		
		return reduced;
		
	}
	
	private static double computeArea(LinkedList<Point> points, int i) {
		Point a = points.get(i - 1);
		Point b = points.get(i);
		Point c = points.get(i + 1);
		return Math.abs((a.x*b.y + b.x*c.y + c.x*a.y - a.x*c.y - c.x*b.y - b.x*a.y) / (double)2);
	}
	
	private static double computeArea(Vertex a, Vertex b, Vertex c) {
		return Math.abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2);
	}
	
	static class Point implements Comparable<Point> {
		public final int x;
		public final int y;
		public double area;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(Point p) {
			if (this.area < p.area)
				return -1;
			else if (this.area > p.area)
				return 1;
			
			/*
			if (this.x <= p.x)
				return -1;
			if (this.y < p.y)
				return -1;
			*/
			
			return 0;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
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
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return String.format("{x=%d, y=%d, a=%.1f}", x, y, area);
		}
		
		
		
	}
}
