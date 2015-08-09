package org.nekosaur.pathfinding.gui.business.events;

import org.nekosaur.pathfinding.gui.business.TriFunction;
import org.nekosaur.pathfinding.gui.presentation.maps.editable.IEditableMap;
import org.nekosaur.pathfinding.lib.common.MapData;

/**
 * @author nekosaur
 */
public class ChangeEditableMapTypeEvent {

    private TriFunction<Double, Double, MapData, IEditableMap> function;

    public ChangeEditableMapTypeEvent(TriFunction<Double, Double, MapData, IEditableMap> function) {
        this.function = function;
    }

    public TriFunction<Double, Double, MapData, IEditableMap> getFunction() {
        return function;
    }
}
