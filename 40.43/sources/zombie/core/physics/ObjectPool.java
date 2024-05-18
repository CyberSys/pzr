package zombie.core.physics;

import java.util.HashMap;
import java.util.Map;


public class ObjectPool {
	private Class cls;
	private ObjectArrayList list = new ObjectArrayList();
	private static ThreadLocal threadLocal = new ThreadLocal(){
    
    protected Map initialValue() {
        return new HashMap();
    }
};

	public ObjectPool(Class javaClass) {
		this.cls = javaClass;
	}

	private Object create() {
		try {
			return this.cls.newInstance();
		} catch (InstantiationException instantiationException) {
			throw new IllegalStateException(instantiationException);
		} catch (IllegalAccessException illegalAccessException) {
			throw new IllegalStateException(illegalAccessException);
		}
	}

	public Object get() {
		return this.list.size() > 0 ? this.list.remove(this.list.size() - 1) : this.create();
	}

	public void release(Object object) {
		this.list.add(object);
	}

	public static ObjectPool get(Class javaClass) {
		Map map = (Map)threadLocal.get();
		ObjectPool objectPool = (ObjectPool)map.get(javaClass);
		if (objectPool == null) {
			objectPool = new ObjectPool(javaClass);
			map.put(javaClass, objectPool);
		}

		return objectPool;
	}

	public static void cleanCurrentThread() {
		threadLocal.remove();
	}
}
