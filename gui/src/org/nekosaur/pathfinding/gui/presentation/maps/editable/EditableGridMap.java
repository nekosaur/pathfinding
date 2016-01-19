package org.nekosaur.pathfinding.gui.presentation.maps.editable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.exceptions.MissingMapDataException;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractMap;
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
    private double cellPadding = 0;

    private MapData mapData;

	public EditableGridMap(double width, double height, MapData mapData) {
        super();

        System.out.println(width + " " + height);

        this.width = width;
        this.height = height;

        this.canvas = new MapCanvas(width, height);

        if (mapData.data == null)
        	throw new MissingMapDataException("Data missing");

        load(mapData);

        final ObjectProperty<Byte> startValue = new SimpleObjectProperty<>();
        //final ObjectProperty<Vertex> currentPosition = new SimpleObjectProperty<>();

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            System.out.println("Clicked!");

            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);

            if (x < 0 || x >= columns || y < 0 || y >= rows)
                return;

            int i = y * mapData.width + x;

            mapData.data[i] = mapData.data[i] == 1 ? (byte)0 : (byte)1;
            /*
            int nx, ny;
            if (vertices[y][x] == 1) {
                vertices[y][x] = 0;

                for (int i = 0; i < DIRECTIONS.length; i++) {
                    nx = x + DIRECTIONS[i][0];
                    ny = y + DIRECTIONS[i][1];

                    if (nx > 0 && nx < vertices.length && ny > 0 && ny < vertices.length) {
                        if (vertices[ny][nx] == 1) {
                            edges[]
                        }

                    }

                }
            }*/

            draw(x, y, NodeState.parse(mapData.data[i]));

            startValue.set(mapData.data[i]);

        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);

            if (x < 0 || x >= columns || y < 0 || y >= rows)
                return;

            int i = y * mapData.width + x;

            mapData.data[i] = startValue.get();
            draw(x, y, NodeState.parse(mapData.data[i]));
            
        });

        this.getChildren().add(canvas);

    }

    public void load(MapData mapData) {
        this.mapData = mapData;

        this.columns = mapData.width;
        this.rows = mapData.height;
        double cellSize = Math.min(width / columns, height / rows);
        this.cellWidth = cellSize;
        this.cellHeight = cellSize;
        
        System.out.println("paneWidth="+width);
        System.out.println("paneHeight="+height);
        System.out.println("cellsize="+cellSize);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
            	draw(x, y, NodeState.parse(this.mapData.data[y * mapData.width + x]));
            }
        }


    }
    
    private void draw(int x, int y, NodeState state) {
    	Color c = NodeState.color(state);
        canvas.drawRect(x * cellWidth, y * cellHeight, cellWidth - cellPadding, cellHeight - cellPadding, c);
        isDirty = true;
    }

	public MapData getMapData() {
		return mapData;
	}

	@Override
	public boolean isDirty() {
		isDirty = !isDirty;
		return !isDirty;
	}
	
	


}
