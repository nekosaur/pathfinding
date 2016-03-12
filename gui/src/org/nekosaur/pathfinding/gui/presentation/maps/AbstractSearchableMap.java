package org.nekosaur.pathfinding.gui.presentation.maps;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;

import org.nekosaur.pathfinding.gui.business.TriConsumer;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.ISearchableMap;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public abstract class AbstractSearchableMap extends Pane implements ISearchableMap {

    protected MapCanvas canvas;

    protected final double paneWidth;
    protected final double paneHeight;
    protected double lineWidth;

    protected SearchSpace searchSpace;
    
    protected TriConsumer<SearchSpace, MapCanvas, Node> mapDecorator;

    protected final ObjectProperty<Point> start = new SimpleObjectProperty<>();
    protected final ObjectProperty<Point> goal = new SimpleObjectProperty<>();

    public AbstractSearchableMap(double width, double height) {

        this.paneWidth = width;
        this.paneHeight = height;
        this.lineWidth = 1;

        this.canvas = new MapCanvas(width, height);

        this.getChildren().add(canvas);
    }

    public ObjectProperty<Point> getStartProperty() {
        return start;
    }

    public ObjectProperty<Point> getGoalProperty() {
        return goal;
    }

    @Override
    public SearchSpace getSearchSpace() {
        return searchSpace;
    }

    @Override
	public void setMapDecorator(TriConsumer<SearchSpace, MapCanvas, Node> decorator) {
		mapDecorator = decorator;
	}

	@Override
    public void reset() {
        searchSpace = searchSpace.copy();
        canvas.reset();
        canvas.drawImage(searchSpace.draw((int) paneWidth));
        Point s = start.get();
        Point g = goal.get();
        if (s != null)
            update(searchSpace.getNode(s.x, s.y));
        if (g != null)
            update(searchSpace.getNode(g.x, g.y));
    }

}
