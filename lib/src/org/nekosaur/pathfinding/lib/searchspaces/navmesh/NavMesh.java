package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.awt.*;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.exceptions.MissingMapDataException;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.searchspaces.AbstractSearchSpace;

import javafx.scene.image.Image;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

public class NavMesh extends AbstractSearchSpace {

	private HashMap<DelaunayTriangle, List<Node>> triangleMap = new HashMap<>();
	private HashMap<DelaunayTriangle, Triangle> delaunayMap = new HashMap<>();
	private Map<Node, List<Node>> triangleAdjacencyMap = new HashMap<>();
	private MapData originalData;

	public NavMesh(int width, int height) {
		super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
	}
	
	public static SearchSpace create(MapData data) {
		if (!data.getVertices().isPresent())
			throw new MissingMapDataException("Vertices missing");

		int[][] vertices = data.getVertices().get();
		
		NavMesh navMesh = new NavMesh(vertices[0].length, vertices[1].length);

		navMesh.originalData = data;
		
		// Do MarchingSquares on all obstacles to get outlines
		// Reduce vertices with VisvalingamWhyatt
		MarchingSquares ms = new MarchingSquares(vertices);

		/*
		Set<List<Vertex>> obstaclePerimeters = new HashSet<>();
		List<Vertex> obstacle;
		while ((obstacle = MooreNeighbour.trace(vertices)) != null) {
			System.out.println("ASD");
			obstaclePerimeters.add(obstacle);
		}*/
		Set<List<Point>> obstaclePerimeters = new HashSet<>();

		for (List<Point> perimeter : ms.identifyAll()) {
			System.out.println(perimeter);
			obstaclePerimeters.add(VisvalingamWhyatt.reduce(perimeter, (int)(perimeter.size() * 0.7)));
			//obstaclePerimeters.add(DouglasPeucker.reduce(perimeter, 0.5f));
			//obstaclePerimeters.add(perimeter);
		}

		System.out.println(obstaclePerimeters.size());
		
		// Triangulate complete mesh
		System.out.println(navMesh.getWidth());
		System.out.println(navMesh.getHeight());
		List<DelaunayTriangle> triangles = Triangulation.triangulate(navMesh.getWidth(), navMesh.getHeight(), obstaclePerimeters);

		navMesh.addTriangles(triangles);


		
		return navMesh;
	}

	public static SearchSpace create(MapData data, EnumSet<Option> options) {
		NavMesh n = (NavMesh)NavMesh.create(data);
		n.options = options;
		return n;
	}

	private void addTriangles(List<DelaunayTriangle> triangles) {
		for (DelaunayTriangle dt : triangles) {
			Triangle t = new Triangle(dt);
			triangleAdjacencyMap.put(t, new LinkedList<>());
			delaunayMap.put(dt, t);
		}

		for (DelaunayTriangle dt : triangles) {
			List<Node> adjacentTriangles = triangleAdjacencyMap.get(delaunayMap.get(dt));

			for (DelaunayTriangle nt : dt.neighbors) {
				Node n = delaunayMap.get(nt);
				if (n != null)
					adjacentTriangles.add(n);
			}
		}
	}

	@Override
	public List<Node> getNeighbours(Node n) {
		return triangleAdjacencyMap.get(n);
	}

	@Override
	public double getMovementCost(Node n1, Node n2) {
		Point d = n1.delta(n2);

		return Math.sqrt(d.x*d.x + d.y*d.y);
	}

	@Override
	public Node getNode(double x, double y) {
		for (Map.Entry<DelaunayTriangle, Triangle> e : delaunayMap.entrySet()) {
			DelaunayTriangle dt = e.getKey();
			if (pointInTriangle(dt.points, x, y))
				return e.getValue();
		}
		return null;
	}

	public DelaunayTriangle getTriangle(double x, double y) {
		for (Map.Entry<DelaunayTriangle, Triangle> e : delaunayMap.entrySet()) {
			if ((new Point(x, y)).equals((Point)e.getValue()))
				return e.getKey();
		}
		return null;
	}

	public DelaunayTriangle getTriangle(Node n) {
		return getTriangle(n.x, n.y);
	}

	/*
	private boolean pointInTriangle(TriangulationPoint[] points, int x, int y) {
		double denominator = ((points[1].getY() - points[2].getY())*(points[0].getX() - points[2].getX()) + (points[2].getX() - points[1].getX())*(points[0].getY() - points[2].getY()));
		double a = ((points[1].getY() - points[2].getY())*(x - points[2].getX()) + (points[2].getX() - points[1].getX())*(y - points[2].getY())) / denominator;
		double b = ((points[2].getY() - points[0].getY())*(x - points[2].getX()) + (points[0].getX() - points[2].getX())*(y - points[2].getY())) / denominator;
		double c = 1 - a - b;

		return 0 <= a && a <= 1 && 0 <= b && b <= 1 && 0 <= c && c <= 1;
	}*/

	private int side(int x1, int y1, int x2, int y2, int x, int y) {
		return (y2 - y1)*(x - x1) + (-x2 + x1)*(y - y1);
	}

	private boolean pointInTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int x, int y) {
		boolean checkSide1 = side(x1, y1, x2, y2, x, y) >= 0;
		boolean checkSide2 = side(x2, y2, x3, y3, x, y) >= 0;
		boolean checkSide3 = side(x3, y3, x1, y1, x, y) >= 0;
		return checkSide1 && checkSide2 && checkSide3;
	}

	private boolean pointInTriangle(TriangulationPoint[] points, double x, double y) {
		int x1 = (int)(points[0].getX());
		int y1 = (int)(points[0].getY());
		int x2 = (int)(points[1].getX());
		int y2 = (int)(points[1].getY());
		int x3 = (int)(points[2].getX());
		int y3 = (int)(points[2].getY());

		Polygon p = new Polygon(new int[] {x1, x2, x3}, new int[] {y1, y2, y3}, 3);

		return p.contains(x, y);
	}


	@Override
	public boolean isWalkableAt(double x, double y) {
		Node n = getNode(x, y);
		return n != null && n.state != NodeState.WALL;
	}

	@Override
	public Image draw(int side) {
		System.out.println("NavMesh draw");
		BufferedImage bi = new BufferedImage(side, side, 3);
		System.out.println(width + " " + height);

		int scale = side / this.width;

		Graphics2D g2d = (Graphics2D)bi.getGraphics();

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, side, side);

		for (Map.Entry<DelaunayTriangle, Triangle> e : delaunayMap.entrySet()) {
			DelaunayTriangle dt = e.getKey();

			int[] xPoints = new int[] { (int)(dt.points[0].getX() * scale), (int)(dt.points[1].getX() * scale), (int)(dt.points[2].getX() * scale)};
			int[] yPoints = new int[] { (int)(dt.points[0].getY() * scale), (int)(dt.points[1].getY() * scale), (int)(dt.points[2].getY() * scale)};

			g2d.setColor(new Color(0.9607843f, 0.9607843f, 0.9607843f));
			g2d.fillPolygon(xPoints, yPoints, 3);
			g2d.setColor(Color.CYAN);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
			g2d.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
			g2d.drawLine(xPoints[2], yPoints[2], xPoints[0], yPoints[0]);
		}

		return SwingFXUtils.toFXImage(bi, null);

	}

	/**
	 * Code from http://playtechs.blogspot.se/2007/03/raytracing-on-grid.html
	 *
	 */
	private void drawLine(PixelWriter pw, int x0, int y0, int x1, int y1)
	{
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int x = x0;
		int y = y0;
		int n = 1 + dx + dy;
		int x_inc = (x1 > x0) ? 1 : -1;
		int y_inc = (y1 > y0) ? 1 : -1;
		int error = dx - dy;
		dx *= 2;
		dy *= 2;

		for (; n > 0; --n)
		{
			//if (!map.isWalkableAt(x, y))
				//return false;

			pw.setColor(x, y, javafx.scene.paint.Color.CYAN);

			if (error > 0)
			{
				x += x_inc;
				error -= dy;
			}
			else
			{
				y += y_inc;
				error += dx;
			}
		}
	}

	private void drawTriangle(Graphics2D g2d, DelaunayTriangle dt) {
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.RED);
		g2d.draw(new Line2D.Double(dt.points[0].getX(), dt.points[0].getY(), dt.points[1].getX(), dt.points[1].getY()));
		g2d.draw(new Line2D.Double(dt.points[1].getX(), dt.points[0].getY(), dt.points[2].getX(), dt.points[2].getY()));
		g2d.draw(new Line2D.Double(dt.points[2].getX(), dt.points[0].getY(), dt.points[0].getX(), dt.points[0].getY()));
		g2d.setColor(Color.CYAN);
		g2d.draw(new Rectangle2D.Double(dt.points[0].getX(), dt.points[0].getY(), 1f, 1f));
	}


	@Override
	public SearchSpace copy() {
		return NavMesh.create(originalData, options);
	}

	private class Triangle extends Node {

		private DelaunayTriangle dt;

		public Triangle(DelaunayTriangle dt) {
			super(dt.centroid().getX(), dt.centroid().getY());
			System.out.println("Creating Triangle [x="+(int)dt.centroid().getX()+", y="+(int)dt.centroid().getY()+"]");

			this.dt = dt;
		}
		
	}
}
