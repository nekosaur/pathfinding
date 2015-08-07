package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.gui.business.MapData;

public class SearchMapLoadEvent {
	private final MapData data;

    public SearchMapLoadEvent(MapData data) {
        this.data = data;
    }

    public MapData getData() {
        return data;
    }
}
