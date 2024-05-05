package zombie.worldMap;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.util.SharedStrings;


public final class WorldMapBinary {
	private static final int VERSION1 = 1;
	private static final int VERSION_LATEST = 1;
	private final SharedStrings m_sharedStrings = new SharedStrings();
	private final TIntObjectHashMap m_stringTable = new TIntObjectHashMap();
	private final WorldMapProperties m_properties = new WorldMapProperties();
	private final ArrayList m_sharedProperties = new ArrayList();

	public boolean read(String string, WorldMapData worldMapData) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(string);
		boolean boolean1;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			try {
				label68: {
					int int1 = bufferedInputStream.read();
					int int2 = bufferedInputStream.read();
					int int3 = bufferedInputStream.read();
					int int4 = bufferedInputStream.read();
					if (int1 == 73 && int2 == 71 && int3 == 77 && int4 == 66) {
						int int5 = this.readInt(bufferedInputStream);
						if (int5 >= 1 && int5 <= 1) {
							int int6 = this.readInt(bufferedInputStream);
							int int7 = this.readInt(bufferedInputStream);
							this.readStringTable(bufferedInputStream);
							for (int int8 = 0; int8 < int7; ++int8) {
								for (int int9 = 0; int9 < int6; ++int9) {
									WorldMapCell worldMapCell = this.parseCell(bufferedInputStream);
									if (worldMapCell != null) {
										worldMapData.m_cells.add(worldMapCell);
									}
								}
							}

							boolean1 = true;
							break label68;
						}

						throw new IOException("unrecognized version " + int5);
					}

					throw new IOException("invalid format (magic doesn\'t match)");
				}
			} catch (Throwable throwable) {
				try {
					bufferedInputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			bufferedInputStream.close();
		} catch (Throwable throwable3) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable4) {
				throwable3.addSuppressed(throwable4);
			}

			throw throwable3;
		}

		fileInputStream.close();
		return boolean1;
	}

	private int readByte(InputStream inputStream) throws IOException {
		return inputStream.read();
	}

	private int readInt(InputStream inputStream) throws IOException {
		int int1 = inputStream.read();
		int int2 = inputStream.read();
		int int3 = inputStream.read();
		int int4 = inputStream.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	private int readShort(InputStream inputStream) throws IOException {
		int int1 = inputStream.read();
		int int2 = inputStream.read();
		if ((int1 | int2) < 0) {
			throw new EOFException();
		} else {
			return (short)((int1 << 0) + (int2 << 8));
		}
	}

	private void readStringTable(InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byte[] byteArray = new byte[1024];
		int int1 = this.readInt(inputStream);
		for (int int2 = 0; int2 < int1; ++int2) {
			byteBuffer.clear();
			int int3 = this.readShort(inputStream);
			byteBuffer.putShort((short)int3);
			inputStream.read(byteArray, 0, int3);
			byteBuffer.put(byteArray, 0, int3);
			byteBuffer.flip();
			this.m_stringTable.put(int2, GameWindow.ReadStringUTF(byteBuffer));
		}
	}

	private String readStringIndexed(InputStream inputStream) throws IOException {
		int int1 = this.readShort(inputStream);
		if (!this.m_stringTable.containsKey(int1)) {
			throw new IOException("invalid string-table index " + int1);
		} else {
			return (String)this.m_stringTable.get(int1);
		}
	}

	private WorldMapCell parseCell(InputStream inputStream) throws IOException {
		int int1 = this.readInt(inputStream);
		if (int1 == -1) {
			return null;
		} else {
			int int2 = this.readInt(inputStream);
			WorldMapCell worldMapCell = new WorldMapCell();
			worldMapCell.m_x = int1;
			worldMapCell.m_y = int2;
			int int3 = this.readInt(inputStream);
			for (int int4 = 0; int4 < int3; ++int4) {
				WorldMapFeature worldMapFeature = this.parseFeature(worldMapCell, inputStream);
				worldMapCell.m_features.add(worldMapFeature);
			}

			return worldMapCell;
		}
	}

	private WorldMapFeature parseFeature(WorldMapCell worldMapCell, InputStream inputStream) throws IOException {
		WorldMapFeature worldMapFeature = new WorldMapFeature(worldMapCell);
		WorldMapGeometry worldMapGeometry = this.parseGeometry(inputStream);
		worldMapFeature.m_geometries.add(worldMapGeometry);
		this.parseFeatureProperties(inputStream, worldMapFeature);
		return worldMapFeature;
	}

	private void parseFeatureProperties(InputStream inputStream, WorldMapFeature worldMapFeature) throws IOException {
		this.m_properties.clear();
		int int1 = this.readByte(inputStream);
		for (int int2 = 0; int2 < int1; ++int2) {
			String string = this.m_sharedStrings.get(this.readStringIndexed(inputStream));
			String string2 = this.m_sharedStrings.get(this.readStringIndexed(inputStream));
			this.m_properties.put(string, string2);
		}

		worldMapFeature.m_properties = this.getOrCreateProperties(this.m_properties);
	}

	private WorldMapProperties getOrCreateProperties(WorldMapProperties worldMapProperties) {
		for (int int1 = 0; int1 < this.m_sharedProperties.size(); ++int1) {
			if (((WorldMapProperties)this.m_sharedProperties.get(int1)).equals(worldMapProperties)) {
				return (WorldMapProperties)this.m_sharedProperties.get(int1);
			}
		}

		WorldMapProperties worldMapProperties2 = new WorldMapProperties();
		worldMapProperties2.putAll(worldMapProperties);
		this.m_sharedProperties.add(worldMapProperties2);
		return worldMapProperties2;
	}

	private WorldMapGeometry parseGeometry(InputStream inputStream) throws IOException {
		WorldMapGeometry worldMapGeometry = new WorldMapGeometry();
		worldMapGeometry.m_type = WorldMapGeometry.Type.valueOf(this.readStringIndexed(inputStream));
		int int1 = this.readByte(inputStream);
		for (int int2 = 0; int2 < int1; ++int2) {
			WorldMapPoints worldMapPoints = new WorldMapPoints();
			this.parseGeometryCoordinates(inputStream, worldMapPoints);
			worldMapGeometry.m_points.add(worldMapPoints);
		}

		worldMapGeometry.calculateBounds();
		return worldMapGeometry;
	}

	private void parseGeometryCoordinates(InputStream inputStream, WorldMapPoints worldMapPoints) throws IOException {
		int int1 = this.readShort(inputStream);
		for (int int2 = 0; int2 < int1; ++int2) {
			worldMapPoints.add(this.readShort(inputStream));
			worldMapPoints.add(this.readShort(inputStream));
		}
	}
}
