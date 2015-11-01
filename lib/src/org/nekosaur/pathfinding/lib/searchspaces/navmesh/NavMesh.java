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
import javafx.scene.image.WritableImage;
import javafx.scene.paint.*;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.MissingMapDataException;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.searchspaces.AbstractSearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;

import javafx.scene.image.Image;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;

public class NavMesh extends AbstractSearchSpace {

	private HashMap<DelaunayTriangle, List<Node>> triangleMap = new HashMap<>();
	private HashMap<DelaunayTriangle, Triangle> delaunayMap = new HashMap<>();
	private Map<Node, List<Node>> triangleAdjacencyMap = new HashMap<>();

	public NavMesh(int width, int height) {
		super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
	}
	
	public static SearchSpace create(MapData data) {
		if (!data.getVertices().isPresent())
			throw new MissingMapDataException("Vertices missing");

		int[][] vertices = data.getVertices().get();
		
		NavMesh navMesh = new NavMesh(vertices[0].length, vertices[1].length);
		
		// Do MarchingSquares on all obstacles to get outlines
		// Reduce vertices with VisvalingamWhyatt
		MarchingSquares ms = new MarchingSquares(vertices);
		Set<List<Vertex>> obstaclePerimeters = new HashSet<>();
		List<Vertex> obstacle;
		while ((obstacle = MooreNeighbour.trace(vertices)) != null) {
			System.out.println("ASD");
			obstaclePerimeters.add(obstacle);
		}
		
		System.out.println(obstaclePerimeters.size());

		for (List<Vertex> perimeter : obstaclePerimeters) {
			System.out.println(perimeter);
		}
		
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

			for (DelaunayTriangle n : dt.neighbors) {
				adjacentTriangles.add(delaunayMap.get(n));
			}
		}
	}

	@Override
	public List<Node> getNeighbours(Node n) {
		return triangleAdjacencyMap.get(n);
	}

	@Override
	public double getMovementCost(Node n1, Node n2) {
		Vertex d = n1.delta(n2);

		return Math.sqrt(d.x*d.x + d.y*d.y);
	}

	@Override
	public Node getNode(int x, int y) {
		for (Map.Entry<DelaunayTriangle, Triangle> e : delaunayMap.entrySet()) {
			DelaunayTriangle dt = e.getKey();
			if (dt.contains(new TPoint(x, y)))
				return e.getValue();
		}
		return null;
	}

	@Override
	public boolean isWalkableAt(int x, int y) {
		Node n = getNode(x, y);
		return n != null && n.state != NodeState.WALL;
	}

	@Override
	public Image draw(int side) {
		System.out.println("NavMesh draw");
		//BufferedImage image = new BufferedImage(this.width, this.height, 3);
		WritableImage image = new WritableImage(side, side);
		System.out.println(width + " " + height);

		//Graphics2D g2d = (Graphics2D)image.getGraphics();
		PixelWriter pw = image.getPixelWriter();

		int scale = side / this.width;

		for (Map.Entry<DelaunayTriangle, Triangle> e : delaunayMap.entrySet()) {
			DelaunayTriangle dt = e.getKey();
			Triangle t = e.getValue();

			pw.setColor((int)(dt.centroid().getX() * scale), (int)(dt.centroid().getY() * scale), NodeState.color(t.state));

			System.out.println(dt.points.length);

			drawLine(pw, (int)(dt.points[0].getX() * scale), (int)(dt.points[0].getY() * scale), (int)(dt.points[1].getX() * scale), (int)(dt.points[1].getY() * scale) );
			drawLine(pw, (int)(dt.points[1].getX() * scale), (int)(dt.points[1].getY() * scale), (int)(dt.points[2].getX() * scale), (int)(dt.points[2].getY() * scale) );
			drawLine(pw, (int)(dt.points[2].getX() * scale), (int)(dt.points[2].getY() * scale), (int)(dt.points[0].getX() * scale), (int)(dt.points[0].getY() * scale) );
			//drawTriangle(g2d, dt);
		}

		//return resample(SwingFXUtils.toFXImage(image, null), side);
		return image;

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
		// TODO Auto-generated method stub
		return null;
	}

	private class Triangle extends Node {

		private DelaunayTriangle dt;

		public Triangle(DelaunayTriangle dt) {
			super((int)dt.centroid().getX(), (int)dt.centroid().getY());
			System.out.println("Creating Triangle [x="+(int)dt.centroid().getX()+", y="+(int)dt.centroid().getY()+"]");

			this.dt = dt;
		}
		
	}
}
