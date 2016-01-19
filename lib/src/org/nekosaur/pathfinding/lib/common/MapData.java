package org.nekosaur.pathfinding.lib.common;

/**
 * @author nekosaur
 */
public class MapData {

    public final byte[] data;
    public final int width;
    public final int height;
    public final Point start;
    public final Point goal;

    public MapData(int[][] data) {
        this.width = data[0].length;
        this.height = data.length;
        this.data = new byte[width*height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.data[y * width + x] = (byte)data[y][x];
            }
        }

        this.start = null;
        this.goal = null;
    }

    public MapData(int width, int height) {
        this.data = new byte[width * height];
        this.width = width;
        this.height = height;
        this.start = null;
        this.goal = null;
    }

    public MapData(byte[] data, int width, int height) {
        this(data, width, height, null, null);
    }
    
    public MapData(byte[] data, int width, int height, Point start, Point goal) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.start = start;
        this.goal = goal;
    }

}
