package org.nekosaur.pathfinding.gui.presentation.maps.searchable;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tooltip;

import org.nekosaur.pathfinding.gui.presentation.maps.AbstractSearchableMap;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class GridMap extends AbstractSearchableMap {

    private double cellWidth;
    private double cellHeight;

	public GridMap(double width, double height, MapData data) {
        super(width, height);

		this.searchSpace = Grid.create(data);

		double cellSize = Math.min(width / searchSpace.getWidth(), height / searchSpace.getHeight());
		this.cellWidth = cellSize;
		this.cellHeight = cellSize;
		this.lineWidth = 1 + cellSize / 10;

		canvas.drawImage(searchSpace.draw((int)width));

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Clicked!");
            
            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);
            
            System.out.println(x + " " + y);

			Node node = searchSpace.getNode(x, y);

			if (node.state == NodeState.WALL)
				return;
            
            if (event.isPopupTrigger()) {
            	if (node.equals(start.get()))
            		return;
            	
            	if (goal.isNotNull().get()) {
            		Node old = searchSpace.getNode(goal.get().x, goal.get().y);
					goal.set(node);
					update(old);
            	} else {
					goal.set(node);
				}

            	update(node);
            } else {
            	if (node.equals(goal.get()))
            		return;
            	
            	if (start.isNotNull().get()) {
            		Node old= searchSpace.getNode(start.get().x, start.get().y);
					start.set(node);
					update(old);
            	} else {
					start.set(node);
				}

				update(node);
            }

        });
        
        final ObjectProperty<Node> mouseOverNode = new SimpleObjectProperty<>();
        final Tooltip t = new Tooltip("ASD");
        
        canvas.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
        	int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);
        	
        	if (x < 0 || x > searchSpace.getWidth() || y < 0 || y > searchSpace.getHeight())
        		return;
        	
        	Node node = searchSpace.getNode(x, y);
        	
        	if (node.equals(mouseOverNode.get())) {
        		t.setX(event.getX());
        		t.setY(event.getY());
        		return;
        	}
        	        	
        	mouseOverNode.set(node);
        	        	
        	t.setText(node.toString().replace(' ', '\n'));
        	t.show(canvas, event.getX(), event.getY());
        });
        
        canvas.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
        	t.hide();
        });

    }

	@Override
	public void drawPath(List<Point> path) {
		List<Point> centeredPath = new ArrayList<>();

		path.forEach(v -> {
			centeredPath.add(new Point((int)((v.x * cellWidth) + cellWidth / 2), (int)((v.y * cellHeight) + cellHeight / 2)));
		});

		canvas.drawLine(centeredPath, Color.YELLOW, lineWidth, false);
	}

	public void update(Node node) {

		Color cstate = NodeState.color(node.state);
		Color cstatus = NodeStatus.color(node.status);

		Color c = cstate.interpolate(cstatus, 0.5);

		if (node.equals(goal.get()))
			c = Color.ORANGERED;
		else if (node.equals(start.get()))
			c = Color.LIMEGREEN;

		canvas.drawRect(node.x * cellWidth, node.y * cellHeight, cellWidth, cellHeight, c);
		
		if (mapDecorator != null)
			mapDecorator.accept(searchSpace, canvas, node);
	}

}




