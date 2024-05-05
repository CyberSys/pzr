package zombie.worldMap;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;


public final class WorldMapImages {
	private static final HashMap s_filenameToImages = new HashMap();
	private String m_directory;
	private ImagePyramid m_pyramid;

	public static WorldMapImages getOrCreate(String string) {
		String string2 = ZomboidFileSystem.instance.getString(string + "/pyramid.zip");
		if (!Files.exists(Paths.get(string2), new LinkOption[0])) {
			return null;
		} else {
			WorldMapImages worldMapImages = (WorldMapImages)s_filenameToImages.get(string2);
			if (worldMapImages == null) {
				worldMapImages = new WorldMapImages();
				worldMapImages.m_directory = string;
				worldMapImages.m_pyramid = new ImagePyramid();
				worldMapImages.m_pyramid.setZipFile(string2);
				s_filenameToImages.put(string2, worldMapImages);
			}

			return worldMapImages;
		}
	}

	public ImagePyramid getPyramid() {
		return this.m_pyramid;
	}

	public int getMinX() {
		return this.m_pyramid.m_minX;
	}

	public int getMinY() {
		return this.m_pyramid.m_minY;
	}

	public int getMaxX() {
		return this.m_pyramid.m_maxX;
	}

	public int getMaxY() {
		return this.m_pyramid.m_maxY;
	}

	public int getZoom(float float1) {
		byte byte1 = 4;
		if ((double)float1 >= 16.0) {
			byte1 = 0;
		} else if (float1 >= 15.0F) {
			byte1 = 1;
		} else if (float1 >= 14.0F) {
			byte1 = 2;
		} else if (float1 >= 13.0F) {
			byte1 = 3;
		}

		int int1 = PZMath.clamp(byte1, this.m_pyramid.m_minZ, this.m_pyramid.m_maxZ);
		return int1;
	}

	public float getResolution() {
		return this.m_pyramid.m_resolution;
	}

	private void destroy() {
		this.m_pyramid.destroy();
	}

	public static void Reset() {
		Iterator iterator = s_filenameToImages.values().iterator();
		while (iterator.hasNext()) {
			WorldMapImages worldMapImages = (WorldMapImages)iterator.next();
			worldMapImages.destroy();
		}

		s_filenameToImages.clear();
	}
}
