package zombie;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import zombie.core.Core;


public class ChunkMapFilenames {
	public static ChunkMapFilenames instance = new ChunkMapFilenames();
	public ConcurrentHashMap Map = new ConcurrentHashMap();
	public ConcurrentHashMap HeaderMap = new ConcurrentHashMap();
	String prefix = "map_";
	private File dirFile;
	private String cacheDir;

	public void clear() {
		this.dirFile = null;
		this.cacheDir = null;
		this.Map.clear();
		this.HeaderMap.clear();
	}

	public File getFilename(int int1, int int2) {
		long long1 = (long)int1 << 32 | (long)int2;
		if (this.Map.containsKey(long1)) {
			return (File)this.Map.get(long1);
		} else {
			if (this.cacheDir == null) {
				this.cacheDir = GameWindow.getGameModeCacheDir();
			}

			String string = this.cacheDir + File.separator + Core.GameSaveWorld + File.separator + this.prefix + int1 + "_" + int2 + ".bin";
			File file = new File(string);
			this.Map.put(long1, file);
			return file;
		}
	}

	public File getDir(String string) {
		if (this.cacheDir == null) {
			this.cacheDir = GameWindow.getGameModeCacheDir();
		}

		if (this.dirFile == null) {
			this.dirFile = new File(this.cacheDir + File.separator + string);
		}

		return this.dirFile;
	}

	public String getHeader(int int1, int int2) {
		long long1 = (long)int1 << 32 | (long)int2;
		if (this.HeaderMap.containsKey(long1)) {
			return this.HeaderMap.get(long1).toString();
		} else {
			String string = int1 + "_" + int2 + ".lotheader";
			this.HeaderMap.put(long1, string);
			return string;
		}
	}
}
