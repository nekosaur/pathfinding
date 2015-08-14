package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nekosaur.pathfinding.lib.common.Vertex;

public class Delaunay {

	private Delaunay() {}
	
	public static void triangulate(List<Vertex> points) {
		
		points.sort((p1, p2) -> {
			if (p1.x < p2.x)
				return -1;
			else if (p1.x > p2.x)
				return 1;
			else {
				if (p1.y < p2.y)
					return -1;
				else 
					return 1;
			}
		});
		
		System.out.println(Delaunay.divide(points, new ArrayList<Triangle>()));
		
		
	}
	
	private static List<List<Vertex>> divide(List<Vertex> points, List<Triangle> triangles) {
		System.out.println("Checking list " + points + " with " + points.size() + " elements");
		if (points.size() <= 3)
			return Arrays.asList(points);
		
		System.out.println("Dividing list from 0 to " + (points.size() - 1) / 2 + " and " + points.size() / 2 + " to " + points.size());
		List<List<Vertex>> left = Delaunay.divide(points.subList(0, points.size() / 2), triangles);
		List<List<Vertex>> right = Delaunay.divide(points.subList(points.size() / 2, points.size()), triangles);
		
		List<List<Vertex>> sides = new ArrayList<List<Vertex>>();
		
		for (List<Vertex> list : left)
			sides.add(list);
		for (List<Vertex> list : right)
			sides.add(list);
		
		// here we have two sides with vertex lists, lets merge them
		
		
		
		return sides;
		
	}
	
	private class Triangle {
		
	}
	
	private class Edge {
		
	}
}
