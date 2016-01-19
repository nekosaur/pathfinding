package org.nekosaur.pathfinding.lib.node;

import java.util.Map;
import java.util.HashMap;
import javafx.scene.paint.Color;

@SuppressWarnings("restriction")
public enum NodeState {
	EMPTY((byte)0), WALL((byte)1), START((byte)10), GOAL((byte)20);

	public byte value;

	private static final Map<Byte, NodeState> intToEnum = new HashMap<>();
	private static final Map<NodeState, Color> enumToColor = new HashMap<>();

	static {
		for (NodeState e : NodeState.values()) {
			intToEnum.put(e.value, e);
		}
	}
	
	static {
		enumToColor.put(EMPTY, Color.WHITESMOKE);
		enumToColor.put(WALL, Color.rgb(20, 20, 20));
	}

	NodeState(byte value) {
		this.value = value;
	}

	public static NodeState parse(byte value) {
		return intToEnum.containsKey(value) ? intToEnum.get(value) : EMPTY;
	}
	
	public static Color color(NodeState state) {
		return enumToColor.get(state);
	}
}
