package org.daisy.reader.util;

import java.util.ArrayList;

public class FifoStack<E> {
	private final int MAX_SIZE;
	private ArrayList<E> innerList;
	
	public FifoStack(int size) {
		MAX_SIZE = size;
		innerList = new ArrayList<E>(MAX_SIZE);
	}
	
	public boolean isEmpty() {
		return innerList.isEmpty();
	}
	
    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     */
	public E pop() {
		if(innerList.size()==0) return null;
		return innerList.remove(innerList.size()-1);
	}
	
    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     */
	public E peek() {
		if(innerList.size()==0) return null;
		return innerList.get(innerList.size()-1);
	}
	
    /**
     * Pushes an item onto the top of this stack.
     * If this makes the stack size larger than max, 
     * FIFO will be executed.
     */
	public void push(E item) {
		if(item==null)return;
		innerList.add(item);
		while(innerList.size()>MAX_SIZE) {
			innerList.remove(0);
		}
	}
	
    /**
     * Removes all of the elements from this stack.
     */
	public void clear() {
		innerList.clear();
	}
	
	/**
	 * Get the maximum size of the stack
	 */
	public int getMaxSize() {
		return MAX_SIZE;
	}
	
	/**
	 * Get the current size of the stack
	 */
	public int getSize() {
		return innerList.size();
	}
}
