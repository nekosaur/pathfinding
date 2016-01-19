package org.nekosaur.pathfinding.lib.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.*;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import javafx.scene.image.Image;
import javafx.embed.swing.*;

@SuppressWarnings("restriction")
public class NavMeshTest {

	
	public static void main(String[] args) {
		
		/*
		int[][] data = {
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
				{1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
				{1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
				{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
		*/
		
		
		int[][] data = {
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 1, 1, 1, 1, 0, 0},
				{0, 0, 1, 1, 1, 1, 0, 0},
				{0, 1, 1, 1, 1, 0, 0, 0},
				{0, 1, 1, 0, 1, 1, 0, 0},
				{0, 0, 0, 0, 1, 1, 1, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		
		
		/*
		int[][] data = {
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 1, 0, 0, 0, 0, 0},
				{1, 1, 1, 0, 0, 0, 0, 0},
				{1, 0, 1, 1, 1, 1, 0, 0},
				{1, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		*/
		
		/*
		int[][] data = {
				{0, 0, 1, 0},
				{0, 1, 1, 0},
				{0, 0, 1, 0},
				{0, 0, 1, 0},
		};
		*/
		
		SearchSpace grid = Grid.create(new MapData(data, null));
		
		int imageSize = 512;
		int cellSize = imageSize / data.length;

		Image gridImage = grid.draw(imageSize);
		
		BufferedImage img = SwingFXUtils.fromFXImage(gridImage, new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB));
		
		Graphics2D g2d = img.createGraphics();
		g2d.setStroke(new BasicStroke(5));
		g2d.setColor(Color.RED);

		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[0].length; x++) {
				g2d.drawOval(x * cellSize + (cellSize/2), y * cellSize + (cellSize/2), 5, 5);
			}
		}


		/*
		MarchingSquares ms = new MarchingSquares(data);
		LinkedList<Vertex> l = null;
		try {
			l = (LinkedList<Vertex>)ms.identifyPerimeter(1, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		System.out.println("Getting contour");
		List<Point> l = MooreNeighbour.trace(data);

		g2d.setColor(Color.PINK);
		Point start = null;
		for (Point v : l) {
			
			g2d.drawOval((int)v.x * cellSize - 5, (int)v.y * cellSize - 5, 10, 10);
			
			if (start == null) {
				start = v;
				continue;
			}
						
			g2d.drawLine((int)start.x * cellSize + (cellSize/2), (int)start.y * cellSize + (cellSize/2), (int)v.x * cellSize + (cellSize/2), (int)v.y * cellSize + (cellSize/2));
			start = v;
		}
		System.out.println(l);
		System.out.println(l.size());
		System.out.println("DouglasPeuckering");
		//Vertex[] reduced = l.toArray(new Vertex[l.size()]);
		//Vertex[] reduced = DouglasPeucker.reduce(Arrays.copyOfRange(l.toArray(new Vertex[l.size()]), l.indexOf(DouglasPeucker.topRight(l)), l.indexOf(DouglasPeucker.bottomRight(l))), 10);
		//Vertex[] reduced = DouglasPeucker.reduce(l, 3.6);
		//System.out.println(reduced.length);
		
		
		List<Point> r = VisvalingamWhyatt.reduce(l, (l.size()/3)*2);
		System.out.println(r.size());
		System.out.println(r);
		
		g2d.setColor(Color.CYAN);

		start = null;
		for (Point v : r) {
			if (start == null) {
				start = v;
				continue;
			}
			
			g2d.drawLine((int)start.x * cellSize + (cellSize/2), (int)start.y * cellSize + (cellSize/2), (int)v.x * cellSize + (cellSize/2), (int)v.y * cellSize + (cellSize/2));
			start = v;
		}
		
		// Remove previously added duplicate vertex, we don't want it when triangulating
		//r.remove(0);
		
		//Delaunay.triangulate(r);
		//Triangulation.triangulate(r);
		Set<List<Point>> set = new HashSet<>();
		set.add(l);
		List<DelaunayTriangle> triangles = Triangulation.triangulate(8, 8, set);
		
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(2));


		for (DelaunayTriangle t : triangles) {
			int x1 = (int)t.points[0].getX();
			int y1 = (int)t.points[0].getY();
			int x2 = (int)t.points[1].getX();
			int y2 = (int)t.points[1].getY();
			int x3 = (int)t.points[2].getX();
			int y3 = (int)t.points[2].getY();
			System.out.println(t.points[0] + ", " + t.points[1] + ", " + t.points[2]);
			g2d.drawLine(x1 * cellSize + (cellSize/2), y1 * cellSize + (cellSize/2), x2 * cellSize + (cellSize/2), y2 * cellSize + (cellSize/2));
			g2d.drawLine(x2 * cellSize + (cellSize/2), y2 * cellSize + (cellSize/2), x3 * cellSize + (cellSize/2), y3 * cellSize + (cellSize/2));
			g2d.drawLine(x3 * cellSize + (cellSize/2), y3 * cellSize + (cellSize/2), x1 * cellSize + (cellSize/2), y1 * cellSize + (cellSize/2));
		}

		
		/*
		start = null;

		for (int i = 0; i < reduced.length; i++) {
			System.out.println(reduced[i]);
			if (start == null) {
				start = reduced[i];
				continue;
			}
		
			g2d.drawOval(reduced[i].x * cellSize, reduced[i].y * cellSize, 10, 10);
			
			g2d.drawLine(start.x * cellSize, start.y * cellSize, reduced[i].x * cellSize, reduced[i].y * cellSize);
			start = reduced[i];
		}
		*/
		
		try {
		    File outputfile = new File("savednew.png");
		    ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}	
		
	}
}
