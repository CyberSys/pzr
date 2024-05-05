package zombie.network;

import gnu.trove.map.hash.TIntLongHashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import zombie.ZomboidFileSystem;
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
		MPStatistic.getInstance().ChunkChecksum.Start();
		long long1 = 0L;
		synchronized (checksumCache) {
			int int3 = int1 + int2 * 30 * 1000;
			if (checksumCache.containsKey(int3)) {
				noise(int1 + "," + int2 + " found in cache crc=" + checksumCache.get(int3));
				long1 = checksumCache.get(int3);
			} else {
				stringBuilder.setLength(0);
				stringBuilder.append(ZomboidFileSystem.instance.getGameModeCacheDir());
				stringBuilder.append(File.separator);
				stringBuilder.append(Core.GameSaveWorld);
				stringBuilder.append(File.separator);
				stringBuilder.append("map_");
				stringBuilder.append(int1);
				stringBuilder.append("_");
				stringBuilder.append(int2);
				stringBuilder.append(".bin");
				long1 = createChecksum(stringBuilder.toString());
				checksumCache.put(int3, long1);
				noise(int1 + "," + int2 + " read from disk crc=" + long1);
			}
		}
		MPStatistic.getInstance().ChunkChecksum.End();
		return long1;
	}

	public static long getChecksumIfExists(int int1, int int2) throws IOException {
		long long1 = 0L;
		MPStatistic.getInstance().ChunkChecksum.Start();
		synchronized (checksumCache) {
			int int3 = int1 + int2 * 30 * 1000;
			if (checksumCache.containsKey(int3)) {
				long1 = checksumCache.get(int3);
			}
		}
		MPStatistic.getInstance().ChunkChecksum.End();
		return long1;
	}

	public static void setChecksum(int int1, int int2, long long1) {
		MPStatistic.getInstance().ChunkChecksum.Start();
		synchronized (checksumCache) {
			int int3 = int1 + int2 * 30 * 1000;
			checksumCache.put(int3, long1);
			noise(int1 + "," + int2 + " set crc=" + long1);
		}
		MPStatistic.getInstance().ChunkChecksum.End();
	}

	public static long createChecksum(String string) throws IOException {
		MPStatistic.getInstance().ChunkChecksum.Start();
		File file = new File(string);
		if (!file.exists()) {
			MPStatistic.getInstance().ChunkChecksum.End();
			return 0L;
		} else {
			FileInputStream fileInputStream = new FileInputStream(string);
			long long1;
			try {
				crc32.reset();
				while (true) {
					int int1;
					if ((int1 = fileInputStream.read(bytes)) == -1) {
						long long2 = crc32.getValue();
						MPStatistic.getInstance().ChunkChecksum.End();
						long1 = long2;
						break;
					}

					crc32.update(bytes, 0, int1);
				}
			} catch (Throwable throwable) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			fileInputStream.close();
			return long1;
		}
	}

	public static void Reset() {
		MPStatistic.getInstance().ChunkChecksum.Start();
		checksumCache.clear();
		MPStatistic.getInstance().ChunkChecksum.End();
	}
}
