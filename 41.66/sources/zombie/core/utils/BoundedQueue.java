package zombie.core.utils;

import java.util.NoSuchElementException;


public class BoundedQueue {
	private int numElements;
	private int front;
	private int rear;
	private Object[] elements;

	public BoundedQueue(int int1) {
		this.numElements = int1;
		int int2 = Math.max(int1, 16);
		int2 = Integer.highestOneBit(int2 - 1) << 1;
		this.elements = new Object[int2];
	}

	public void add(Object object) {
		if (object == null) {
			throw new NullPointerException();
		} else {
			if (this.size() == this.numElements) {
				this.removeFirst();
			}

			this.elements[this.rear] = object;
			this.rear = this.rear + 1 & this.elements.length - 1;
		}
	}

	public Object removeFirst() {
		Object object = this.elements[this.front];
		if (object == null) {
			throw new NoSuchElementException();
		} else {
			this.elements[this.front] = null;
			this.front = this.front + 1 & this.elements.length - 1;
			return object;
		}
	}

	public Object remove(int int1) {
		int int2 = this.front + int1 & this.elements.length - 1;
		Object object = this.elements[int2];
		if (object == null) {
			throw new NoSuchElementException();
		} else {
			int int3;
			int int4;
			for (int3 = int2; int3 != this.front; int3 = int4) {
				int4 = int3 - 1 & this.elements.length - 1;
				this.elements[int3] = this.elements[int4];
			}

			this.front = this.front + 1 & this.elements.length - 1;
			this.elements[int3] = null;
			return object;
		}
	}

	public Object get(int int1) {
		int int2 = this.front + int1 & this.elements.length - 1;
		Object object = this.elements[int2];
		if (object == null) {
			throw new NoSuchElementException();
		} else {
			return object;
		}
	}

	public void clear() {
		while (this.front != this.rear) {
			this.elements[this.front] = null;
			this.front = this.front + 1 & this.elements.length - 1;
		}

		this.front = this.rear = 0;
	}

	public int capacity() {
		return this.numElements;
	}

	public int size() {
		return this.front <= this.rear ? this.rear - this.front : this.rear + this.elements.length - this.front;
	}

	public boolean isEmpty() {
		return this.front == this.rear;
	}

	public boolean isFull() {
		return this.size() == this.capacity();
	}
}
