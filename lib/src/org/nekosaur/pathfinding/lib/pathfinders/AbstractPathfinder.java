package org.nekosaur.pathfinding.lib.pathfinders;

import java.util.ArrayList;
import java.util.List;

import org.nekosaur.pathfinding.lib.common.Buffer;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.node.Node;

public abstract class AbstractPathfinder implements Pathfinder {

	private final Buffer<Node> history = new Buffer<>();
    private long startTime;
    protected long operations;
    
    public Buffer<Node> getHistory() {
        return history;
    }

    protected void startClock() {
        startTime = System.nanoTime();
    }
    
    protected long stopClock() {
        return System.nanoTime() - startTime;
    }
    
    protected List<Point> reconstructPath(Node goal) {
        List<Point> reconstructedPath = new ArrayList<>();

        Node current = goal;
        while (current != null) {
            reconstructedPath.add(current);
            current = current.parent;
        }

        return reconstructedPath;
    }
    
	protected void addToHistory(Node node) {
		history.put(node.copy());
	}
    
	
}
