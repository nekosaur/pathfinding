package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.datastructures.BinaryHashHeap;
import org.nekosaur.pathfinding.lib.node.Node;

public class BinaryHashHeapTest {

	public static void main(String[] args) {
		
		BinaryHashHeap<Node> heap = new BinaryHashHeap<Node>(Node.class, 20);
		
		Node n1 = new Node(1, 0);
		n1.g = 1;
		n1.h = 9.2;
		Node n2 = new Node(0, 1);
		n2.g = 1;
		n2.h = 9.2;
		Node n3 = new Node(1, 1);
		n3.g = 1;
		n3.h = 8.2;

		heap.add(n1);
		heap.add(n2);
		heap.add(n3);
		
		System.out.println(heap);
		
		while (heap.size() > 0) {
			System.out.println("removed " + heap.remove());
			System.out.println(heap);
		}
	}
	
	/*
	private static class Node implements Comparable<Node> {
		private int cost;
		
		public Node(int cost) {
			this.cost = cost;
		}
		
		public void setCost(int cost) {
			this.cost = cost;
		}

		@Override
		public int compareTo(Node o) {
			return new Integer(cost).compareTo(o.cost);
		}
		
		@Override
		public String toString() {
			return String.format("C=%d", cost);
		}
	}*/
}
