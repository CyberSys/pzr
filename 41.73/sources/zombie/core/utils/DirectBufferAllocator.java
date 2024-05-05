package zombie.core.utils;

import java.util.ArrayList;


public final class DirectBufferAllocator {
	private static final Object LOCK = "DirectBufferAllocator.LOCK";
	private static final ArrayList ALL = new ArrayList();

	public static WrappedBuffer allocate(int int1) {
		synchronized (LOCK) {
			destroyDisposed();
			WrappedBuffer wrappedBuffer = new WrappedBuffer(int1);
			ALL.add(wrappedBuffer);
			return wrappedBuffer;
		}
	}

	private static void destroyDisposed() {
		synchronized (LOCK) {
			for (int int1 = ALL.size() - 1; int1 >= 0; --int1) {
				WrappedBuffer wrappedBuffer = (WrappedBuffer)ALL.get(int1);
				if (wrappedBuffer.isDisposed()) {
					ALL.remove(int1);
				}
			}
		}
	}

	public static long getBytesAllocated() {
		synchronized (LOCK) {
			destroyDisposed();
			long long1 = 0L;
			for (int int1 = 0; int1 < ALL.size(); ++int1) {
				WrappedBuffer wrappedBuffer = (WrappedBuffer)ALL.get(int1);
				if (!wrappedBuffer.isDisposed()) {
					long1 += (long)wrappedBuffer.capacity();
				}
			}

			return long1;
		}
	}
}
