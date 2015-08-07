package org.nekosaur.pathfinding.lib.common;

import java.util.LinkedList;

public class Buffer<T> {
	private final LinkedList<T> buffer = new LinkedList<T>();
	
	public synchronized void put(T obj) {
		buffer.addLast(obj);
		notifyAll();
	}
	
	public synchronized T get() throws InterruptedException {
		while(buffer.isEmpty()) {
			wait();
		}
		return buffer.removeFirst();
	}
	
	public synchronized void clear() {
		if (!buffer.isEmpty()) {
			buffer.clear();
		}
	}
	
	public synchronized int size() {
		return buffer.size();
	}
}
