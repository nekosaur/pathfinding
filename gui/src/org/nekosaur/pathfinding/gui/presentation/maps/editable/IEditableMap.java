package org.nekosaur.pathfinding.gui.presentation.maps.editable;

import org.nekosaur.pathfinding.lib.common.MapData;

/**
 * @author nekosaur
 */
public interface IEditableMap {
	MapData getMapData();
	boolean isDirty();
}
