package org.nekosaur.pathfinding.gui.presentation.maps.searchable;

import javafx.beans.property.ObjectProperty;

import org.nekosaur.pathfinding.gui.business.TriConsumer;
import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;

import java.util.List;

@SuppressWarnings("restriction")
public interface ISearchableMap {
	SearchSpace getSearchSpace();
	ObjectProperty<Vertex> getStartProperty();
	ObjectProperty<Vertex> getGoalProperty();
	void update(Node node);
	void drawPath(List<Vertex> path);
	void reset();
	void setMapDecorator(TriConsumer<SearchSpace, MapCanvas, Node> decorator);
}
