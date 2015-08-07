package org.nekosaur.pathfinding.lib.node;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

@SuppressWarnings("restriction")
public enum NodeStatus {
    OPEN, CLOSED, INACTIVE, JUMPED, PEEKED, VISITED;
    
    private static final Map<NodeStatus, Color> enumToColor = new HashMap<>();
    
    static {
		enumToColor.put(INACTIVE, Color.WHITESMOKE);
		enumToColor.put(OPEN, Color.LIGHTBLUE);
		enumToColor.put(CLOSED, Color.DARKBLUE);
		enumToColor.put(JUMPED, Color.MEDIUMPURPLE);
		enumToColor.put(PEEKED, Color.HOTPINK);
		enumToColor.put(VISITED, Color.SALMON);
	}
    
    public static Color color(NodeStatus status) {
		return enumToColor.get(status);
	}
}
