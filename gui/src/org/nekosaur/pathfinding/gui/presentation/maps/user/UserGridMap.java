package org.nekosaur.pathfinding.gui.presentation.maps.user;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.nekosaur.pathfinding.gui.business.MapData;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractMap;
import org.nekosaur.pathfinding.gui.presentation.maps.IMap;
import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;

/**
 * @author nekosaur
 */
public class UserGridMap extends AbstractMap implements IMap {

    private MapCanvas canvas;

    private int columns;
    private int rows;
    private double cellWidth;
    private double cellHeight;
    private double cellPadding = 1;

    private int[][] data;

    public UserGridMap(double width, double height, MapData mapData) {
        super();

        System.out.println(width + " " + height);

        this.width = width;
        this.height = height;

        this.canvas = new MapCanvas(width, height);

        load(mapData.vertices);

        final ObjectProperty<Integer> startValue = new SimpleObjectProperty<>();
        //final ObjectProperty<Vertex> currentPosition = new SimpleObjectProperty<>();

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            System.out.println("Clicked!");

            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);

            if (x >= columns || y >= rows)
                return;

            data[y][x] = data[y][x] == 1 ? 0 : 1;

            Color c = data[y][x] == 1 ? Color.DARKGRAY : Color.WHITESMOKE;
            canvas.drawRect(x * cellWidth, y * cellHeight, cellWidth - cellPadding, cellHeight - cellPadding, c);

            startValue.set(data[y][x]);

        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);

            if (x >= columns || y >= rows)
                return;

            data[y][x] = startValue.get();

            Color c = data[y][x] == 1 ? Color.DARKGRAY : Color.WHITESMOKE;
            canvas.drawRect(x * cellWidth, y * cellHeight, cellWidth - cellPadding, cellHeight - cellPadding, c);
        });

        this.getChildren().add(canvas);

    }

    public void load(int[][] data) {
        this.columns = data[0].length;
        this.rows = data.length;
        double cellSize = Math.min((width - (columns * cellPadding)) / columns, (height - (rows * cellPadding)) / rows);
        this.cellWidth = cellSize;
        this.cellHeight = cellSize;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                Color c = data[y][x] == 1 ? Color.DARKGRAY : Color.WHITESMOKE;
                canvas.drawRect(x * cellWidth, y * cellWidth, cellWidth - cellPadding, cellHeight - cellPadding, c);

            }
        }

        this.data = data;
    }


}
