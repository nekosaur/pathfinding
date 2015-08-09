package org.nekosaur.pathfinding.gui.business.events;

import javafx.concurrent.Task;
import org.nekosaur.pathfinding.gui.business.TriFunction;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.ISearchableMap;
import org.nekosaur.pathfinding.lib.common.MapData;

/**
 * @author nekosaur
 */
public class ChangeSearchableMapTypeEvent {
    TriFunction<Double, Double, MapData, ISearchableMap> function;

    public ChangeSearchableMapTypeEvent(TriFunction<Double, Double, MapData, ISearchableMap> function) {
        this.function = function;
    }

    public TriFunction<Double, Double, MapData, ISearchableMap> getFunction() {
        return function;
    }
}
