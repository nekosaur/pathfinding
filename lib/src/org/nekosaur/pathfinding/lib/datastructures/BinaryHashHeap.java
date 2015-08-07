package org.nekosaur.pathfinding.lib.datastructures;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class BinaryHashHeap<T extends Comparable<T>> implements Queue<T> {
	
	private T[] heap;
	private int heapCount;
	private Object2IntOpenHashMap<T> hash;
	
	@SuppressWarnings("unchecked")
	public BinaryHashHeap(Class<T> type, int maxSize) {
		heap = (T[])Array.newInstance(type, maxSize + 1);
		heapCount = 0;
		hash = new Object2IntOpenHashMap<T>(maxSize);
	}

	@Override
	public int size() {
		return heapCount;
	}

	@Override
	public boolean isEmpty() {
		return heapCount == 0;
	}

	@Override
	public boolean contains(Object o) {
		for (int i = 1; i <= heapCount / 2; i++) {
			if (heap[i].equals(o))
				return true;
			if (heap[heapCount - i].equals(o))
				return true;
		}
		if (heap[heapCount].equals(o))
			return true;
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		heapCount = 0;
	}

	@Override
	public boolean add(T e) {
		heapCount++;
		
		heap[heapCount] = e;
		
		hash.put(e, heapCount);
		//hash.put(e, e.hashCode());
		
		//System.out.println("Added " + e + " to heap position " + heapCount);
		
		sortUp(heapCount);
		
		//System.out.println("Newly sorted heap:");
		//System.out.println(this.toString());
		
		return true;
	}

	@Override
	public boolean offer(T e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T remove() {
		T item = heap[1];
		
		//System.out.println("Removed " + item + " from the heap");
		
		heap[1] = heap[heapCount];
		
		sortDown();
		
		heapCount--;
		
		//System.out.println("Newly sorted heap:");
		//System.out.println(this.toString());
		
		
		
		return item;
	}

	@Override
	public T poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T peek() {
		return heap[1];
	}
	
	private void sortUp(int index) {
		
		while (index > 1) {	
			int c = heap[index].compareTo(heap[index/2]); 
			
			if (c < 0) {
				T temp = heap[index/2];
				heap[index/2] = heap[index];
				hash.put(heap[index/2], index);
				heap[index] = temp;
				hash.put(temp, index);
				index = index/2;
			} else {
				break;
			}
		}
		
	}
	
	private void sortDown() {
		int i = 1;
		
		while(true) {
			int u = i;
			
			if (u*2+1 <= heapCount) {
				if (heap[u].compareTo(heap[u*2]) >= 0)
					i = 2*u;
				if (heap[i].compareTo(heap[2*u+1]) >= 0)
					i = 2*u + 1;
			} else if (u*2 <= heapCount) {
				if (heap[u].compareTo(heap[u*2]) >= 0)
					i = 2*u;
			}
			
			if (u != i) {
				T temp = heap[u];
				heap[u] = heap[i];
				hash.put(heap[i], u);
				heap[i] = temp;
				hash.put(temp, i);
			} else {
				break;
			}
		}
		
	}
	
	public void update(T e) {
		if (heap[hash.get(e)].equals(e)) {
			sortUp(hash.get(e));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int depth = 0;
		int width = 20;
		for (int i = 1; i <= heapCount; i++) {
			if (depth(i) > depth) {
				sb.append('\n');
				depth = depth(i);
				width = width /2;
			}
			sb.append(pad(width)).append(heap[i]).append(pad(width));
		}
		
		return sb.toString();
	}
	
	private String pad(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
			sb.append(' ');
		return sb.toString();
	}
	
	private int depth(int index) {
		return (int)Math.floor(log2(index));
	}
	
	public static double log2(int n)
	{
	    return (Math.log(n) / Math.log(2));
	}
	
	
	
}
