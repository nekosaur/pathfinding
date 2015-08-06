package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.gui.business.MapData;

/**
 * @author nekosaur
 */
public class EditMapLoadEvent {

    private final MapData data;

    public EditMapLoadEvent(MapData data) {
        this.data = data;
    }

    public MapData getData() {
        return data;
    }
}
