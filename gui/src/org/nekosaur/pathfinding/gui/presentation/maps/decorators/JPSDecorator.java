package org.nekosaur.pathfinding.gui.presentation.maps.decorators;

import javafx.scene.paint.Color;

import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;

@SuppressWarnings("restriction")
public class JPSDecorator {

	public static void accept(SearchSpace map, MapCanvas canvas, Node node) {
		if (node.parent == null)
			return;
		
		double cellWidth = canvas.getWidth() / map.getWidth();
		double angle = Math.toDegrees(Math.atan2(node.parent.x - node.x, -(node.parent.y - node.y)));
		
		Vertex position = new Vertex((int)((node.x * cellWidth) + cellWidth / 2), (int)((node.y * cellWidth) + cellWidth/2));
		
    	canvas.drawTriangle(position, cellWidth / 2, angle + 180, Color.BLACK);
	}

}
