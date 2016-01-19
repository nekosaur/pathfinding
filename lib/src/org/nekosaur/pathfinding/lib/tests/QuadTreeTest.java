package org.nekosaur.pathfinding.lib.tests;

import java.util.List;

import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.movingai.MovingAI;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class QuadTreeTest {

	
	public static void main(String[] args) {
		
int[][] data = MovingAI.loadMap(System.getProperty("user.dir") + "\\" + "brc000d.map");

		/*
		SearchSpace map = QuadTree.create(data, null);
		
		Pathfinder f = new AStarFinder();
		
		Result r;
		try {
			r = f.findPath(map, new Vertex(28,16), new Vertex(30,33), Heuristics.euclidean, 1);
			
			for (Vertex v : r.path()) {
				System.out.println(v);
			}
			
			System.out.println(String.format("%d ms", r.duration()));
			
		} catch (NodeNotFoundException | SearchSpaceNotSupportedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		Result r;
		try {
			r = f.findPath(map, new Vertex(3,1), new Vertex(1,7), Heuristics.euclidean, 1);
			
			try {
			    // retrieve image
			    WritableImage bi = qt.draw();
			    bi = drawPath(bi, r.path());
			    File outputfile = new File("saved.png");
			    ImageIO.write(SwingFXUtils.fromFXImage(resample(bi, 32), null), "png", outputfile);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
		} catch (NodeNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
	}
	
	public static WritableImage drawPath(WritableImage image, List<Point> path) {
		PixelWriter pw = image.getPixelWriter();
		
		Color c = Color.rgb(0, 0, 255);
		for (Point v : path) {
			c = c.deriveColor(0, 1, 0.8, 1);
			pw.setColor((int)v.x, (int)v.y, c);
		}
		
		return image;
		
	}
	
	private static Image resample(Image input, int scaleFactor) {
	    final int W = (int) input.getWidth();
	    final int H = (int) input.getHeight();
	    final int S = scaleFactor;
	    
	    WritableImage output = new WritableImage(
	      W * S,
	      H * S
	    );
	    
	    PixelReader reader = input.getPixelReader();
	    PixelWriter writer = output.getPixelWriter();
	    
	    for (int y = 0; y < H; y++) {
	      for (int x = 0; x < W; x++) {
	        final int argb = reader.getArgb(x, y);
	        for (int dy = 0; dy < S; dy++) {
	          for (int dx = 0; dx < S; dx++) {
	            writer.setArgb(x * S + dx, y * S + dy, argb);
	          }
	        }
	      }
	    }
	    
	    return output;
	  }
}
