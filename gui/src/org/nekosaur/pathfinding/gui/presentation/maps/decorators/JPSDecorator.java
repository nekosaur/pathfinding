package org.nekosaur.pathfinding.gui.presentation.maps.decorators;

import javafx.scene.paint.Color;

import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeStatus;

@SuppressWarnings("restriction")
public class JPSDecorator {

	public static void accept(SearchSpace map, MapCanvas canvas, Node node) {
		if (node.parent == null)
			return;

		/*
		Node rootParent = node.parent;
		while (rootParent.parent != null) {
			if (rootParent.status == NodeStatus.CLOSED)
				break;
			rootParent = rootParent.parent;
		}


		double cellWidth = canvas.getWidth() / map.getWidth();
		Vertex p1 = new Vertex((int)((rootParent.x * cellWidth) + cellWidth / 2), (int)((rootParent.y * cellWidth) + cellWidth/2));
		Vertex p2 = new Vertex((int)((node.x * cellWidth) + cellWidth / 2), (int)((node.y * cellWidth) + cellWidth/2));

		canvas.drawLine(p1, p2, Color.WHITE, 10, true);
		*/


		double cellWidth = canvas.getWidth() / map.getWidth();
		if (cellWidth < 32)
			return;
		double angle = Math.toDegrees(Math.atan2(node.parent.x - node.x, -(node.parent.y - node.y)));
		
		Vertex position = new Vertex((int)((node.x * cellWidth) + cellWidth / 2), (int)((node.y * cellWidth) + cellWidth/2));
		
    	canvas.drawTriangle(position, cellWidth / 2, angle + 180, Color.BLACK);

	}

}
