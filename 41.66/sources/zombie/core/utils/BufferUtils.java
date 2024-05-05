package zombie.core.utils;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class BufferUtils {
	private static boolean trackDirectMemory = false;
	private static final ReferenceQueue removeCollected = new ReferenceQueue();
	private static final ConcurrentHashMap trackedBuffers = new ConcurrentHashMap();
	static BufferUtils.ClearReferences cleanupThread;
	private static final AtomicBoolean loadedMethods = new AtomicBoolean(false);
	private static Method cleanerMethod = null;
	private static Method cleanMethod = null;
	private static Method viewedBufferMethod = null;
	private static Method freeMethod = null;

	public static void setTrackDirectMemoryEnabled(boolean boolean1) {
		trackDirectMemory = boolean1;
	}

	private static void onBufferAllocated(Buffer buffer) {
		if (trackDirectMemory) {
			if (cleanupThread == null) {
				cleanupThread = new BufferUtils.ClearReferences();
				cleanupThread.start();
			}

			BufferUtils.BufferInfo bufferInfo;
			if (buffer instanceof ByteBuffer) {
				bufferInfo = new BufferUtils.BufferInfo(ByteBuffer.class, buffer.capacity(), buffer, removeCollected);
				trackedBuffers.put(bufferInfo, bufferInfo);
			} else if (buffer instanceof FloatBuffer) {
				bufferInfo = new BufferUtils.BufferInfo(FloatBuffer.class, buffer.capacity() * 4, buffer, removeCollected);
				trackedBuffers.put(bufferInfo, bufferInfo);
			} else if (buffer instanceof IntBuffer) {
				bufferInfo = new BufferUtils.BufferInfo(IntBuffer.class, buffer.capacity() * 4, buffer, removeCollected);
				trackedBuffers.put(bufferInfo, bufferInfo);
			} else if (buffer instanceof ShortBuffer) {
				bufferInfo = new BufferUtils.BufferInfo(ShortBuffer.class, buffer.capacity() * 2, buffer, removeCollected);
				trackedBuffers.put(bufferInfo, bufferInfo);
			} else if (buffer instanceof DoubleBuffer) {
				bufferInfo = new BufferUtils.BufferInfo(DoubleBuffer.class, buffer.capacity() * 8, buffer, removeCollected);
				trackedBuffers.put(bufferInfo, bufferInfo);
			}
		}
	}

	public static void printCurrentDirectMemory(StringBuilder stringBuilder) {
		long long1 = 0L;
		long long2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		boolean boolean1 = stringBuilder == null;
		if (stringBuilder == null) {
			stringBuilder = new StringBuilder();
		}

		if (trackDirectMemory) {
			int int1 = 0;
			int int2 = 0;
			int int3 = 0;
			int int4 = 0;
			int int5 = 0;
			int int6 = 0;
			int int7 = 0;
			int int8 = 0;
			int int9 = 0;
			int int10 = 0;
			Iterator iterator = trackedBuffers.values().iterator();
			while (iterator.hasNext()) {
				BufferUtils.BufferInfo bufferInfo = (BufferUtils.BufferInfo)iterator.next();
				if (bufferInfo.type == ByteBuffer.class) {
					long1 += (long)bufferInfo.size;
					int7 += bufferInfo.size;
					++int2;
				} else if (bufferInfo.type == FloatBuffer.class) {
					long1 += (long)bufferInfo.size;
					int6 += bufferInfo.size;
					++int1;
				} else if (bufferInfo.type == IntBuffer.class) {
					long1 += (long)bufferInfo.size;
					int8 += bufferInfo.size;
					++int3;
				} else if (bufferInfo.type == ShortBuffer.class) {
					long1 += (long)bufferInfo.size;
					int9 += bufferInfo.size;
					++int4;
				} else if (bufferInfo.type == DoubleBuffer.class) {
					long1 += (long)bufferInfo.size;
					int10 += bufferInfo.size;
					++int5;
				}
			}

			stringBuilder.append("Existing buffers: ").append(trackedBuffers.size()).append("\n");
			stringBuilder.append("(b: ").append(int2).append("  f: ").append(int1).append("  i: ").append(int3).append("  s: ").append(int4).append("  d: ").append(int5).append(")").append("\n");
			stringBuilder.append("Total   heap memory held: ").append(long2 / 1024L).append("kb\n");
			stringBuilder.append("Total direct memory held: ").append(long1 / 1024L).append("kb\n");
			stringBuilder.append("(b: ").append(int7 / 1024).append("kb  f: ").append(int6 / 1024).append("kb  i: ").append(int8 / 1024).append("kb  s: ").append(int9 / 1024).append("kb  d: ").append(int10 / 1024).append("kb)").append("\n");
		} else {
			stringBuilder.append("Total   heap memory held: ").append(long2 / 1024L).append("kb\n");
			stringBuilder.append("Only heap memory available, if you want to monitor direct memory use BufferUtils.setTrackDirectMemoryEnabled(true) during initialization.").append("\n");
		}

		if (boolean1) {
			System.out.println(stringBuilder.toString());
		}
	}

	private static Method loadMethod(String string, String string2) {
		try {
			Method method = Class.forName(string).getMethod(string2);
			method.setAccessible(true);
			return method;
		} catch (SecurityException | ClassNotFoundException | NoSuchMethodException error) {
			return null;
		}
	}

	public static ByteBuffer createByteBuffer(int int1) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(int1).order(ByteOrder.nativeOrder());
		byteBuffer.clear();
		onBufferAllocated(byteBuffer);
		return byteBuffer;
	}

	private static void loadCleanerMethods() {
		if (!loadedMethods.getAndSet(true)) {
			synchronized (loadedMethods) {
				cleanerMethod = loadMethod("sun.nio.ch.DirectBuffer", "cleaner");
				viewedBufferMethod = loadMethod("sun.nio.ch.DirectBuffer", "viewedBuffer");
				if (viewedBufferMethod == null) {
					viewedBufferMethod = loadMethod("sun.nio.ch.DirectBuffer", "attachment");
				}

				ByteBuffer byteBuffer = createByteBuffer(1);
				Class javaClass = byteBuffer.getClass();
				try {
					freeMethod = javaClass.getMethod("free");
				} catch (SecurityException | NoSuchMethodException error) {
				}
			}
		}
	}

	public static void destroyDirectBuffer(Buffer buffer) {
		if (buffer.isDirect()) {
			loadCleanerMethods();
			try {
				if (freeMethod != null) {
					freeMethod.invoke(buffer);
				} else {
					Object object = cleanerMethod.invoke(buffer);
					if (object == null) {
						Object object2 = viewedBufferMethod.invoke(buffer);
						if (object2 != null) {
							destroyDirectBuffer((Buffer)object2);
						} else {
							Logger.getLogger(BufferUtils.class.getName()).log(Level.SEVERE, "Buffer cannot be destroyed: {0}", buffer);
						}
					}
				}
			} catch (IllegalArgumentException | InvocationTargetException | SecurityException | IllegalAccessException error) {
				Logger.getLogger(BufferUtils.class.getName()).log(Level.SEVERE, "{0}", error);
			}
		}
	}

	private static class ClearReferences extends Thread {

		ClearReferences() {
			this.setDaemon(true);
		}

		public void run() {
			try {
				while (true) {
					Reference reference = BufferUtils.removeCollected.remove();
					BufferUtils.trackedBuffers.remove(reference);
				}
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	private static class BufferInfo extends PhantomReference {
		private final Class type;
		private final int size;

		public BufferInfo(Class javaClass, int int1, Buffer buffer, ReferenceQueue referenceQueue) {
			super(buffer, referenceQueue);
			this.type = javaClass;
			this.size = int1;
		}
	}
}
