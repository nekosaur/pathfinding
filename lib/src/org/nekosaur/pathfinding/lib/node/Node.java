package org.nekosaur.pathfinding.lib.node;

import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.Copy;

public class Node extends Vertex implements Comparable<Node>, Copy<Node> {

	public Node parent;
	public double g;
	public double h;
	public double f;
	public NodeState state;
	public NodeStatus status;
	
	public Node(int x, int y) {
		super(x, y);
		state = NodeState.EMPTY;
		status = NodeStatus.INACTIVE;
	}
	
	public Node(int x, int y, NodeState state) {
		this(x, y);
		this.state = state;
	}
		
	@Override
    public int compareTo(Node node) {
        if (areEqualDouble(this.f, node.f, 6)) {
            if (areEqualDouble(this.g, node.g, 6))
                return 0;
            else if (this.g > node.g)
                return -1;
            else if (this.g < node.g)
                return 1;
        }
        else if (this.f < node.f)
            return -1;
        else if (this.f > node.f)
            return 1;

        return 0;
    }
	
	private static boolean areEqualDouble(double a, double b, int precision) {
        return Math.abs(a - b) <= Math.pow(10, -precision);
    }
	
	public Node copy() {
		Node copy = new Node(x, y);
		copy.parent = parent;
		copy.g = g;
		copy.h = h;
		copy.f = f;
		copy.state = state;
		copy.status = status;
		return copy;
	}
	
	@Override
    public String toString() {
        return String.format("%s G=%.2f H=%.2f F=%.2f STATE=%s STATUS=%s", super.toString(), g, h, f, state, status);
    }
	
}
