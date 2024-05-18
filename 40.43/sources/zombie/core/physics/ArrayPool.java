package zombie.core.physics;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ArrayPool {
	private Class componentType;
	private ObjectArrayList list = new ObjectArrayList();
	private Comparator comparator;
	private ArrayPool.IntValue key = new ArrayPool.IntValue();
	private static Comparator floatComparator = new Comparator(){
    
    public int compare(Object var1, Object var2) {
        int var3 = var1 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var1).value : ((float[])((float[])var1)).length;
        int var4 = var2 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var2).value : ((float[])((float[])var2)).length;
        return var3 > var4 ? 1 : (var3 < var4 ? -1 : 0);
    }
};
	private static Comparator intComparator = new Comparator(){
    
    public int compare(Object var1, Object var2) {
        int var3 = var1 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var1).value : ((int[])((int[])var1)).length;
        int var4 = var2 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var2).value : ((int[])((int[])var2)).length;
        return var3 > var4 ? 1 : (var3 < var4 ? -1 : 0);
    }
};
	private static Comparator objectComparator = new Comparator(){
    
    public int compare(Object var1, Object var2) {
        int var3 = var1 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var1).value : ((Object[])((Object[])var1)).length;
        int var4 = var2 instanceof ArrayPool.IntValue ? ((ArrayPool.IntValue)var2).value : ((Object[])((Object[])var2)).length;
        return var3 > var4 ? 1 : (var3 < var4 ? -1 : 0);
    }
};
	private static ThreadLocal threadLocal = new ThreadLocal(){
    
    protected Map initialValue() {
        return new HashMap();
    }
};

	public ArrayPool(Class javaClass) {
		this.componentType = javaClass;
		if (javaClass == Float.TYPE) {
			this.comparator = floatComparator;
		} else if (javaClass == Integer.TYPE) {
			this.comparator = intComparator;
		} else {
			if (javaClass.isPrimitive()) {
				throw new UnsupportedOperationException("unsupported type " + javaClass);
			}

			this.comparator = objectComparator;
		}
	}

	private Object create(int int1) {
		return Array.newInstance(this.componentType, int1);
	}

	public Object getFixed(int int1) {
		this.key.value = int1;
		int int2 = Collections.binarySearch(this.list, this.key, this.comparator);
		return int2 < 0 ? this.create(int1) : this.list.remove(int2);
	}

	public Object getAtLeast(int int1) {
		this.key.value = int1;
		int int2 = Collections.binarySearch(this.list, this.key, this.comparator);
		if (int2 < 0) {
			int2 = -int2 - 1;
			return int2 < this.list.size() ? this.list.remove(int2) : this.create(int1);
		} else {
			return this.list.remove(int2);
		}
	}

	public void release(Object object) {
		int int1 = Collections.binarySearch(this.list, object, this.comparator);
		if (int1 < 0) {
			int1 = -int1 - 1;
		}

		this.list.add(int1, object);
		if (this.comparator == objectComparator) {
			Object[] objectArray = (Object[])((Object[])object);
			for (int int2 = 0; int2 < objectArray.length; ++int2) {
				objectArray[int2] = null;
			}
		}
	}

	public static ArrayPool get(Class javaClass) {
		Map map = (Map)threadLocal.get();
		ArrayPool arrayPool = (ArrayPool)map.get(javaClass);
		if (arrayPool == null) {
			arrayPool = new ArrayPool(javaClass);
			map.put(javaClass, arrayPool);
		}

		return arrayPool;
	}

	public static void cleanCurrentThread() {
		threadLocal.remove();
	}

	private static class IntValue {
		public int value;

		private IntValue() {
		}

		IntValue(Object object) {
			this();
		}
	}
}
