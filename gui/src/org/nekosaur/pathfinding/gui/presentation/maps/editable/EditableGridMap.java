package org.nekosaur.pathfinding.gui.presentation.maps.editable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.nekosaur.pathfinding.gui.business.MapData;
import org.nekosaur.pathfinding.gui.business.exceptions.MapDataIncompleteException;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractMap;
import org.nekosaur.pathfinding.gui.presentation.maps.IEditableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;
import org.nekosaur.pathfinding.lib.node.NodeState;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class EditableGridMap extends AbstractMap implements IEditableMap {

    private MapCanvas canvas;

    private boolean isDirty = false;
    private int columns;
    private int rows;
    private double cellWidth;
    private double cellHeight;
    private double cellPadding = 1;

    private int[][] data;

	public EditableGridMap(double width, double height, MapData mapData) {
        super();

        System.out.println(width + " " + height);

        this.width = width;
        this.height = height;

        this.canvas = new MapCanvas(width, height);

        if (!mapData.getVertices().isPresent())
        	throw new MapDataIncompleteException("Vertices missing");
        
        load(mapData.getVertices().get());

        final ObjectProperty<Integer> startValue = new SimpleObjectProperty<>();
        //final ObjectProperty<Vertex> currentPosition = new SimpleObjectProperty<>();

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            System.out.println("Clicked!");

            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);

            if (x >= columns || y >= rows)
                return;

            data[y][x] = data[y][x] == 1 ? 0 : 1;

            draw(x, y, NodeState.parse(data[y][x]));

            startValue.set(data[y][x]);

        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);

            if (x >= columns || y >= rows)
                return;

            data[y][x] = startValue.get();
            draw(x, y, NodeState.parse(data[y][x]));

            
        });

        this.getChildren().add(canvas);

    }

    public void load(int[][] data) {
        this.columns = data[0].length;
        this.rows = data.length;
        double cellSize = Math.min((width - (columns * cellPadding)) / columns, (height - (rows * cellPadding)) / rows);
        this.cellWidth = cellSize;
        this.cellHeight = cellSize;
        
        System.out.println("width="+width);
        System.out.println("height="+height);
        System.out.println("cellsize="+cellSize);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
            	draw(x, y, NodeState.parse(data[y][x]));
            }
        }

        this.data = data;
    }
    
    private void draw(int x, int y, NodeState state) {
    	Color c = NodeState.color(state);
        canvas.drawRect(x * cellWidth, y * cellHeight, cellWidth - cellPadding, cellHeight - cellPadding, c);
        isDirty = true;
    }

	@Override
	public MapData getData() {
		return new MapData(data, null);
	}

	@Override
	public boolean isDirty() {
		isDirty = !isDirty;
		return !isDirty;
	}
	
	


}
