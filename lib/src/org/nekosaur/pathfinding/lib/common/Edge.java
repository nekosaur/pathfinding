package org.nekosaur.pathfinding.lib.common;

public class Edge<T extends Point> {

	public final T start;
	public final T end;
	public final double weight;
	
	public Edge(T start, T end, double weight) {
		this.start = start;
		this.end = end;
		this.weight = weight;
	}

	public Edge(T start, T end) {
		super();
		this.start = start;
		this.end = end;
		this.weight = 1;
	}
	
}
