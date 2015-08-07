package org.nekosaur.pathfinding.gui.presentation.maps;

import org.nekosaur.pathfinding.gui.business.MapData;

/**
 * @author nekosaur
 */
public interface IEditableMap {
	MapData getData();
	boolean isDirty();
}
