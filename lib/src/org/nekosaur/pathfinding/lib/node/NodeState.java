package org.nekosaur.pathfinding.lib.node;

import java.util.Map;
import java.util.HashMap;
import javafx.scene.paint.Color;

@SuppressWarnings("restriction")
public enum NodeState {
	EMPTY(0), WALL(1), START(10), GOAL(20);

	public int value;

	private static final Map<Integer, NodeState> intToEnum = new HashMap<Integer, NodeState>();
	private static final Map<NodeState, Color> enumToColor = new HashMap<>();

	static {
		for (NodeState e : NodeState.values()) {
			intToEnum.put(e.value, e);
		}
	}
	
	static {
		enumToColor.put(EMPTY, Color.LIGHTGRAY);
		enumToColor.put(WALL, Color.DARKGRAY);
	}

	NodeState(int value) {
		this.value = value;
	}

	public static NodeState parse(int value) {
		return intToEnum.containsKey(value) ? intToEnum.get(value) : EMPTY;
	}
	
	public static Color color(NodeState state) {
		return enumToColor.get(state);
	}
}
