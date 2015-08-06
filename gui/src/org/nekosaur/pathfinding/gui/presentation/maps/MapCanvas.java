package org.nekosaur.pathfinding.gui.presentation.maps;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author nekosaur
 */
public class MapCanvas extends Canvas {

    private final GraphicsContext gc;

    public MapCanvas(double width, double height) {
        super(width, height);

        this.gc = this.getGraphicsContext2D();

    }

    public void drawRect(double x, double y, double width, double height, Color color) {
        gc.save();
        gc.setFill(color);
        gc.fillRect(snap(x), snap(y), snap(width), snap(height));
        gc.restore();
    }

    private double snap(double v) {
        return snap(v, 0f);
    }

    private double snap(double v, double snap) {
        return ((int) v) + snap;
    }
}
