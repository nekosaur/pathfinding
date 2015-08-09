package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.lib.node.Node;

/**
 * @author nekosaur
 */
public class HistoryUpdatedEvent {

    private Node node;

    public HistoryUpdatedEvent(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
