package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.movingai.MovingAI;
import org.nekosaur.pathfinding.lib.searchspaces.quadtree.LinearQuadTree;

import java.io.File;

/**
 * @author nekosaur
 */
public class LinearQuadTreeTest {

    public static void main(String[] args) {

        int[][] data = new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };

        MapData mapData = new MapData(data);

        LinearQuadTree lqt = new LinearQuadTree(mapData);

    }
}
