package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.lib.common.Point;

import java.util.List;

/**
 * @author nekosaur
 */
public class PathFoundEvent {

    private final List<Point> path;

    public PathFoundEvent(List<Point> path) {
        this.path = path;
    }

    public List<Point> getPath() {
        return path;
    }
}
