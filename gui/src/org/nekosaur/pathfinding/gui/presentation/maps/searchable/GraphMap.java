package org.nekosaur.pathfinding.gui.presentation.maps.searchable;

import org.nekosaur.pathfinding.gui.presentation.maps.AbstractSearchableMap;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.searchspaces.graph.Graph;

import java.util.List;

/**
 * @author nekosaur
 */
public class GraphMap extends AbstractSearchableMap {

    public GraphMap(double width, double height, MapData data) {
        super(width, height);

        searchSpace = Graph.create(data);

        canvas.drawImage(searchSpace.draw((int)width));
    }

    @Override
    public void update(Node node) {

    }

    @Override
    public void drawPath(List<Vertex> path) {

    }
}
