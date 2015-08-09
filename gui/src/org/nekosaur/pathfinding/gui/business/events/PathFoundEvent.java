package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.lib.common.Vertex;

import java.util.List;

/**
 * @author nekosaur
 */
public class PathFoundEvent {

    private final List<Vertex> path;

    public PathFoundEvent(List<Vertex> path) {
        this.path = path;
    }

    public List<Vertex> getPath() {
        return path;
    }
}
