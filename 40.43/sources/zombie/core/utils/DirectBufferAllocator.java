package zombie.core.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


public final class DirectBufferAllocator {
	private static final boolean DEBUG = false;
	private static final Map MEMMAP = new TreeMap();

	private DirectBufferAllocator() {
	}

	public static WrappedBuffer allocate(int int1) {
		Integer integer = new Integer(int1);
		Iterator iterator = MEMMAP.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			Integer integer2 = (Integer)entry.getKey();
			WrappedBuffer wrappedBuffer = (WrappedBuffer)entry.getValue();
			if (wrappedBuffer.isDisposed()) {
				if (integer2 >= int1) {
					wrappedBuffer.allocate();
					return wrappedBuffer;
				}

				iterator.remove();
				wrappedBuffer.clear();
			}
		}

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(int1).order(ByteOrder.nativeOrder());
		WrappedBuffer wrappedBuffer2 = new WrappedBuffer(byteBuffer);
		MEMMAP.put(integer, wrappedBuffer2);
		return wrappedBuffer2;
	}
}
