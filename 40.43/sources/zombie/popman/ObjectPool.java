package zombie.popman;

import java.util.ArrayDeque;
import java.util.ArrayList;


abstract class ObjectPool {
	private ArrayDeque pool = new ArrayDeque();

	public Object alloc() {
		return this.pool.isEmpty() ? this.makeObject() : this.pool.pop();
	}

	public void release(Object object) {
		assert object != null;
		assert !this.pool.contains(object);
		this.pool.push(object);
	}

	public void release(ArrayList arrayList) {
		if (!arrayList.isEmpty()) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				assert !this.pool.contains(arrayList.get(int1));
			}

			this.pool.addAll(arrayList);
		}
	}

	public void clear() {
		this.pool.clear();
	}

	protected abstract Object makeObject();
}
