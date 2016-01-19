package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.MooreNeighbour;

import java.util.List;

/**
 * @author nekosaur
 */
public class MooreNeighbourTest {

    public static void main(String[] args) {

        /*
        int[][] data = new int[][] {
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };*/

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

        List<Point> contour = MooreNeighbour.trace(data);

        System.out.println("contour");
        for (Point v : contour)
            System.out.println(v);

    }
}
