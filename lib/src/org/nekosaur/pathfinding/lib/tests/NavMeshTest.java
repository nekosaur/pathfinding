package org.nekosaur.pathfinding.lib.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.DouglasPeucker;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.MarchingSquares;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.VisvalingamWhyatt;

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
				{0, 0, 1, 1, 1, 1, 1, 1},
				{0, 1, 1, 1, 1, 0, 0, 0},
				{1, 1, 1, 0, 1, 1, 0, 0},
				{0, 0, 0, 0, 1, 1, 1, 1},
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
		
		int imageSize = 1024;

		Image gridImage = grid.draw(imageSize);
		
		BufferedImage img = SwingFXUtils.fromFXImage(gridImage, new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB));
		
		Graphics2D g2d = img.createGraphics();
		
		MarchingSquares ms = new MarchingSquares(data);
		
		int cellSize = 1024 / data.length; 
		
		g2d.setStroke(new BasicStroke(5));
		g2d.setColor(Color.PINK);
		LinkedList<Vertex> l = (LinkedList<Vertex>)ms.identifyPerimeter(1, 1);
		Vertex start = null;
		for (Vertex v : l) {
			
			g2d.drawOval(v.x * cellSize, v.y * cellSize, 10, 10);
			
			if (start == null) {
				start = v;
				continue;
			}
				
						
			g2d.drawLine(start.x * cellSize, start.y * cellSize, v.x * cellSize, v.y * cellSize);
			start = v;
		}
		System.out.println(l);
		System.out.println(l.size());
		System.out.println("DouglasPeuckering");
		//Vertex[] reduced = l.toArray(new Vertex[l.size()]);
		//Vertex[] reduced = DouglasPeucker.reduce(Arrays.copyOfRange(l.toArray(new Vertex[l.size()]), l.indexOf(DouglasPeucker.topRight(l)), l.indexOf(DouglasPeucker.bottomRight(l))), 10);
		Vertex[] reduced = DouglasPeucker.reduce(l, 3.6);
		System.out.println(reduced.length);
		
		
		List<Vertex> r = VisvalingamWhyatt.reduce(l, (l.size()/3)*2);
		System.out.println(r.size());
		
		g2d.setColor(Color.CYAN);
		
		start = null;
		for (Vertex v : r) {
			if (start == null) {
				start = v;
				continue;
			}
			
			g2d.drawLine(start.x * cellSize, start.y * cellSize, v.x * cellSize, v.y * cellSize);
			start = v;
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
