package org.nekosaur.pathfinding.lib.tests;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.MarchingSquares;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author nekosaur
 */
public class MarchingSquaresTest {

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
                {0, 1, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
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
                g2d.drawOval(x * cellSize + (cellSize / 2), y * cellSize + (cellSize / 2), 5, 5);
            }
        }



		MarchingSquares ms = new MarchingSquares(data);

        Set<List<Point>> perimeters = ms.identifyAll();

        g2d.setColor(Color.PINK);

        System.out.println(perimeters.size());

        for (List<Point> perimeter : perimeters) {
            Point start = null;
            for (Point v : perimeter) {

                g2d.drawOval((int)v.x * cellSize - 5, (int)v.y * cellSize - 5, 10, 10);

                if (start == null) {
                    start = v;
                    continue;
                }

                g2d.drawLine((int)start.x * cellSize, (int)start.y * cellSize, (int)v.x * cellSize, (int)v.y * cellSize);
                start = v;
            }
        }

        try {
            File outputfile = new File("marchingsquares.png");
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
