package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.lighti.clipper.*;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.primitives.Point;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.sets.PointSet;

import de.lighti.clipper.Clipper.ClipType;
import de.lighti.clipper.Clipper.PolyType;
import de.lighti.clipper.Point.LongPoint;

/**
 * http://www.gamedev.net/page/resources/_/technical/artificial-intelligence/generating-2d-navmeshes-r3393
 * 
 * @author Albert Kaaman
 *
 */
public class Triangulation {
	
	public static List<DelaunayTriangle> triangulate(int width, int height, Set<List<Vertex>> obstacles) {
		Clipper clipper = new DefaultClipper();
		
		Path base = new Path(4);
		base.add(new LongPoint(0, 0));
		base.add(new LongPoint(width, 0));
		base.add(new LongPoint(width, height));
		base.add(new LongPoint(0, height));

		clipper.addPath(base, PolyType.SUBJECT, true);

		for (List<Vertex> obstacle : obstacles) {
			System.out.println("Creating path for obstacle " + obstacle);
			Path shape = new Path(obstacle.size());
			for (Vertex v : obstacle)
				shape.add(new LongPoint(v.x, v.y));

			clipper.addPath(shape, PolyType.CLIP, true);
		}

		Paths solution = new Paths();
		
		clipper.execute(ClipType.DIFFERENCE, solution);
		
		System.out.println(solution);

		ClipperOffset offset = new ClipperOffset();
		
		List<Path> holes = solution.stream().filter(path -> !path.orientation()).collect(Collectors.toList());
		List<Path> blocks = solution.stream().filter(path -> path.orientation()).collect(Collectors.toList());

		Paths s = new Paths();
		for (Path h : holes) {
			offset.addPath(h, Clipper.JoinType.SQUARE, Clipper.EndType.CLOSED_POLYGON);
		}
		offset.execute(s, 0f);
		
		System.out.println("holes: " + holes.size());
		System.out.println("blocks: " + blocks.size());
		
		List<DelaunayTriangle> triangles = new LinkedList<>();
		for (Path p : blocks) {
			Polygon polygon = pathToPolygon(p);
			for (Path h : s) {
				if (p.isPointInPolygon(h.get(0)) >= 1)
					polygon.addHole(pathToPolygon(h));
			}
			Poly2Tri.triangulate(polygon);
			triangles.addAll(polygon.getTriangles());
		}
		System.out.println("triangles: " + triangles.size());
		
		return triangles;
	}
	
	public static Polygon pathToPolygon(Path path) {
		List<PolygonPoint> points = new LinkedList<>();
		for (LongPoint p : path) {
			points.add(new PolygonPoint(p.getX(), p.getY()));
		}
		
		return new Polygon(points);
	}

	public static void triangulate(List<Vertex> vertices) {
		List<PolygonPoint> points = new ArrayList<>();
		
		points.add(new PolygonPoint(0, 0));
		points.add(new PolygonPoint(10, 0));
		points.add(new PolygonPoint(10, 10));
		points.add(new PolygonPoint(0, 10));
		
		points.add(new PolygonPoint(2, 2));
		points.add(new PolygonPoint(6, 2));
		points.add(new PolygonPoint(6, 6));
		points.add(new PolygonPoint(8, 7));
		/*
		for (Vertex v : vertices) {
			Double x, y;
			if (v.x == 0)
				x = (double)v.x + 1;
			else
				x = (double)v.x;
			if (v.y == 0)
				y = v.y + 0.1;
			else
				y = (double)v.y;
			
			points.add(new PolygonPoint(new Double(v.x), new Double(v.y)));
		}
		*/
		
		
		Polygon p = new Polygon(points);
		
		Poly2Tri.triangulate(p);
		List<DelaunayTriangle> triangles = p.getTriangles();
		
		for (DelaunayTriangle dt : triangles) {
			System.out.println(dt);
		}
	}
}
