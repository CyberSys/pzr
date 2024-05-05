package zombie.popman;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;


public class ObjectPool {
	private final ObjectPool.Allocator allocator;
	private final ArrayList pool;

	public ObjectPool() {
		this((ObjectPool.Allocator)null);
	}

	public ObjectPool(ObjectPool.Allocator allocator) {
		this.pool = new ArrayList(){
			
			public boolean contains(Object allocator) {
				for (int var2 = 0; var2 < ObjectPool.this.pool.size(); ++var2) {
					if (ObjectPool.this.pool.get(var2) == allocator) {
						return true;
					}
				}

				return false;
			}
		};
		this.allocator = allocator;
	}

	public Object alloc() {
		return this.pool.isEmpty() ? this.makeObject() : this.pool.remove(this.pool.size() - 1);
	}

	public void release(Object object) {
		assert object != null;
		assert !this.pool.contains(object);
		this.pool.add(object);
	}

	public void release(List list) {
		for (int int1 = 0; int1 < list.size(); ++int1) {
			if (list.get(int1) != null) {
				this.release(list.get(int1));
			}
		}
	}

	public void release(Iterable iterable) {
		Iterator iterator = iterable.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object != null) {
				this.release(object);
			}
		}
	}

	public void release(Object[] objectArray) {
		if (objectArray != null) {
			for (int int1 = 0; int1 < objectArray.length; ++int1) {
				if (objectArray[int1] != null) {
					this.release(objectArray[int1]);
				}
			}
		}
	}

	public void releaseAll(List list) {
		for (int int1 = 0; int1 < list.size(); ++int1) {
			if (list.get(int1) != null) {
				this.release(list.get(int1));
			}
		}
	}

	public void clear() {
		this.pool.clear();
	}

	protected Object makeObject() {
		if (this.allocator != null) {
			return this.allocator.allocate();
		} else {
			throw new UnsupportedOperationException("Allocator is null. The ObjectPool is intended to be used with an allocator, or with the function makeObject overridden in a subclass.");
		}
	}

	public void forEach(Consumer consumer) {
		for (int int1 = 0; int1 < this.pool.size(); ++int1) {
			consumer.accept(this.pool.get(int1));
		}
	}

	public interface Allocator {

		Object allocate();
	}
}
