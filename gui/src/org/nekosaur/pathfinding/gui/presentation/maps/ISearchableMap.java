package org.nekosaur.pathfinding.gui.presentation.maps;

import javafx.beans.property.ObjectProperty;

import org.nekosaur.pathfinding.gui.business.MapData;
import org.nekosaur.pathfinding.lib.common.Vertex;

@SuppressWarnings("restriction")
public interface ISearchableMap {
	MapData getData();
	ObjectProperty<Vertex> getStartProperty();
	ObjectProperty<Vertex> getGoalProperty();
}
