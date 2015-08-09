package org.nekosaur.pathfinding.gui.business.events;

/**
 * @author nekosaur
 */
public class ChangeMapSizeEvent {
    private int mapSize;

    public ChangeMapSizeEvent(int mapSize) {
        this.mapSize = mapSize;
    }

    public int getMapSize() {
        return mapSize;
    }
}
