package org.nekosaur.pathfinding.gui.presentation.maps.grid;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractMap;
import org.nekosaur.pathfinding.gui.presentation.maps.IMap;

/**
 * @author nekosaur
 */
public class GridMap extends AbstractMap implements IMap {

    private Canvas canvas;
    private GraphicsContext gc;

    private int columns;
    private int rows;
    private double cellWidth;
    private double cellHeight;



}
