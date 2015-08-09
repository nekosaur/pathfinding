package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.lib.common.MapData;

public class SearchMapLoadEvent {
	private final MapData data;

    public SearchMapLoadEvent(MapData data) {
        this.data = data;
    }

    public MapData getData() {
        return data;
    }
}
