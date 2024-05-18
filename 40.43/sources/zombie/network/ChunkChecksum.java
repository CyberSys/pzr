package zombie.network;

import gnu.trove.map.hash.TIntLongHashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import zombie.GameWindow;
import zombie.core.Core;


public class ChunkChecksum {
	private static final TIntLongHashMap checksumCache = new TIntLongHashMap();
	private static final StringBuilder stringBuilder = new StringBuilder(128);
	private static final CRC32 crc32 = new CRC32();
	private static final byte[] bytes = new byte[1024];

	private static void noise(String string) {
		if (Core.bDebug) {
		}
	}

	public static long getChecksum(int int1, int int2) throws IOException {
		synchronized (checksumCache) {
			int int3 = int1 + int2 * 30 * 1000;
			if (checksumCache.containsKey(int3)) {
				noise(int1 + "," + int2 + " found in cache crc=" + checksumCache.get(int3));
				return checksumCache.get(int3);
			} else {
				stringBuilder.setLength(0);
				stringBuilder.append(GameWindow.getGameModeCacheDir());
				stringBuilder.append(File.separator);
				stringBuilder.append(Core.GameSaveWorld);
				stringBuilder.append(File.separator);
				stringBuilder.append("map_");
				stringBuilder.append(int1);
				stringBuilder.append("_");
				stringBuilder.append(int2);
				stringBuilder.append(".bin");
				long long1 = createChecksum(stringBuilder.toString());
				checksumCache.put(int3, long1);
				noise(int1 + "," + int2 + " read from disk crc=" + long1);
				return long1;
			}
		}
	}

	public static long getChecksumIfExists(int int1, int int2) throws IOException {
		synchronized (checksumCache) {
			int int3 = int1 + int2 * 30 * 1000;
			return checksumCache.containsKey(int3) ? checksumCache.get(int3) : 0L;
		}
	}

	public static void setChecksum(int int1, int int2, long long1) {
		synchronized (checksumCache) {
			int int3 = int1 + int2 * 30 * 1000;
			checksumCache.put(int3, long1);
			noise(int1 + "," + int2 + " set crc=" + long1);
		}
	}

	public static long createChecksum(String string) throws IOException {
		File file = new File(string);
		if (!file.exists()) {
			return 0L;
		} else {
			FileInputStream fileInputStream = new FileInputStream(string);
			Throwable throwable = null;
			try {
				crc32.reset();
				int int1;
				while ((int1 = fileInputStream.read(bytes)) != -1) {
					crc32.update(bytes, 0, int1);
				}

				long long1 = crc32.getValue();
				return long1;
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (fileInputStream != null) {
					if (throwable != null) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						fileInputStream.close();
					}
				}
			}
		}
	}

	public static void Reset() {
		checksumCache.clear();
	}
}
