package org.nekosaur.pathfinding.lib.tests;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.NavMesh;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NavMeshTestNew {
	
	public static void main(String[] args) {

		/*
		int data[][] = {
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,1,1,0,0,0,0,0,0,0},
				{0,1,1,1,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
		};
		*/

		int[][] data = {
				{0,0,0,0,0,0,0,0},
				{0,1,1,0,0,0,0,0},
				{0,1,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}
		};


		/*
		int[][] data = {
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
				{0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
				{0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};*/

		
		NavMesh navMesh = new NavMesh(10, 10);
		
		SearchSpace ss = NavMesh.create(new MapData(data));

		System.out.println(ss.getNode(0, 1));

		int imageSize = 512;
		Image gridImage = ss.draw(imageSize);

		BufferedImage img = SwingFXUtils.fromFXImage(gridImage, new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB));

		try {
			File outputfile = new File("navmesh.png");
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
