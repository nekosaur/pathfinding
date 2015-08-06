package org.nekosaur.pathfinding.gui.business;



/**
 * @author nekosaur
 */
public class MapData {

    public final int[][] vertices;
    public final int[][] edges;
    //public final Vertex start;
    //public final Vertex goal;

    public MapData(int[][] vertices, int[][] edges/*, Vertex start, Vertex goal*/) {
        this.vertices = vertices;
        this.edges = edges;
        //this.start = start;
        //this.goal = goal;
    }
}
