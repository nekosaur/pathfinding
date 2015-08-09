package org.nekosaur.pathfinding.gui.presentation.maps;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.ISearchableMap;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;

/**
 * @author nekosaur
 */
public abstract class AbstractSearchableMap extends Pane implements ISearchableMap {

    protected MapCanvas canvas;

    protected final double paneWidth;
    protected final double paneHeight;
    protected double lineWidth;

    protected SearchSpace searchSpace;

    protected final ObjectProperty<Vertex> start = new SimpleObjectProperty<>();
    protected final ObjectProperty<Vertex> goal = new SimpleObjectProperty<>();

    public AbstractSearchableMap(double width, double height) {

        this.paneWidth = width;
        this.paneHeight = height;
        this.lineWidth = 1;

        this.canvas = new MapCanvas(width, height);

        this.getChildren().add(canvas);
    }

    @SuppressWarnings("unchecked")
    public ObjectProperty<Vertex> getStartProperty() {
        return start;
    }

    @SuppressWarnings("unchecked")
    public ObjectProperty<Vertex> getGoalProperty() {
        return goal;
    }

    @Override
    public SearchSpace getSearchSpace() {
        return searchSpace;
    }

    @Override
    public void reset() {
        searchSpace = searchSpace.copy();
        canvas.drawImage(searchSpace.draw((int) paneWidth));
        update(searchSpace.getNode(start.get().x, start.get().y));
        update(searchSpace.getNode(goal.get().x, goal.get().y));
    }

}
