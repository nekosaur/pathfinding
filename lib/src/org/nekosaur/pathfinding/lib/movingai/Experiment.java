package org.nekosaur.pathfinding.lib.movingai;

import org.nekosaur.pathfinding.lib.common.Vertex;

/**
 * @author nekosaur
 */
public class Experiment {

    int bucket;
    String mapName;
    int mapWidth;
    int mapHeight;
    Vertex startPosition;
    Vertex goalPosition;
    double optimalLength;

    public Experiment(Vertex startPosition, Vertex goalPosition, int mapWidth, int mapHeight, int bucket, double optimalLength, String mapName) {
        this.bucket = bucket;
        this.mapName = mapName;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.startPosition = startPosition;
        this.goalPosition = goalPosition;
        this.optimalLength = optimalLength;
    }

    public Experiment(Vertex startPosition, Vertex goalPosition, int bucket, double optimalLength, String mapName) {
        this.startPosition = startPosition;
        this.goalPosition = goalPosition;
        this.optimalLength = optimalLength;
        this.mapName = mapName;
        this.bucket = bucket;
    }

    public Vertex getStartPosition() {
        return startPosition;
    }

    public Vertex getGoalPosition() {
        return goalPosition;
    }

	@Override
	public String toString() {
		return String.format("Experiment S{%d,%d} G{%d,%d}", startPosition.x, startPosition.y, goalPosition.x, goalPosition.y);
	}
    
    
}
