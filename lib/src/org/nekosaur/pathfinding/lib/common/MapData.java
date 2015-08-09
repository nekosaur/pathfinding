package org.nekosaur.pathfinding.lib.common;

import java.util.Optional;

import org.nekosaur.pathfinding.lib.common.Vertex;

/**
 * @author nekosaur
 */
public class MapData {

    private final int[][] vertices;
    private final int[][] edges;
    public final Vertex start;
    public final Vertex goal;

    public MapData(int[][] vertices, int[][] edges) {
        this.vertices = vertices;
        this.edges = edges;
        this.start = null;
        this.goal = null;
    }
    
    public MapData(int[][] vertices, int[][] edges, Vertex start, Vertex goal) {
        this.vertices = vertices;
        this.edges = edges;
        this.start = start;
        this.goal = goal;
    }
    
    public Optional<int[][]> getVertices() {
    	return Optional.ofNullable(vertices);
    }
    
    public Optional<int[][]> getEdges() {
    	return Optional.of(edges);
    }
}
