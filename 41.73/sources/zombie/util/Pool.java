package zombie.util;

import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import zombie.util.list.PZArrayUtil;


public final class Pool {
	private final Supplier m_allocator;
	private final ThreadLocal m_stacks = ThreadLocal.withInitial(Pool.PoolStacks::new);

	public Pool(Supplier supplier) {
		this.m_allocator = supplier;
	}

	public final IPooledObject alloc() {
		Supplier supplier = this.m_allocator;
		Pool.PoolStacks poolStacks = (Pool.PoolStacks)this.m_stacks.get();
		THashSet tHashSet = poolStacks.inUse;
		List list = poolStacks.released;
		IPooledObject iPooledObject;
		if (!list.isEmpty()) {
			iPooledObject = (IPooledObject)list.remove(list.size() - 1);
		} else {
			iPooledObject = (IPooledObject)supplier.get();
			if (iPooledObject == null) {
				throw new NullPointerException("Allocator returned a nullPtr. This is not allowed.");
			}

			iPooledObject.setPool(this);
		}

		iPooledObject.setFree(false);
		tHashSet.add(iPooledObject);
		return iPooledObject;
	}

	public final void release(IPooledObject iPooledObject) {
		Pool.PoolStacks poolStacks = (Pool.PoolStacks)this.m_stacks.get();
		THashSet tHashSet = poolStacks.inUse;
		List list = poolStacks.released;
		if (iPooledObject.getPool() != this) {
			throw new UnsupportedOperationException("Cannot release item. Not owned by this pool.");
		} else if (iPooledObject.isFree()) {
			throw new UnsupportedOperationException("Cannot release item. Already released.");
		} else {
			tHashSet.remove(iPooledObject);
			iPooledObject.setFree(true);
			list.add(iPooledObject);
			iPooledObject.onReleased();
		}
	}

	public static Object tryRelease(Object object) {
		IPooledObject iPooledObject = (IPooledObject)Type.tryCastTo(object, IPooledObject.class);
		if (iPooledObject != null && !iPooledObject.isFree()) {
			iPooledObject.release();
		}

		return null;
	}

	public static IPooledObject tryRelease(IPooledObject iPooledObject) {
		if (iPooledObject != null && !iPooledObject.isFree()) {
			iPooledObject.release();
		}

		return null;
	}

	public static IPooledObject[] tryRelease(IPooledObject[] iPooledObjectArray) {
		PZArrayUtil.forEach((Object[])iPooledObjectArray, Pool::tryRelease);
		return null;
	}

	private static final class PoolStacks {
		final THashSet inUse = new THashSet();
		final List released = new ArrayList();

		PoolStacks() {
			this.inUse.setAutoCompactionFactor(0.0F);
		}
	}
}
