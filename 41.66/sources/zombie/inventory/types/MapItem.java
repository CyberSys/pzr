package zombie.inventory.types;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.inventory.InventoryItem;
import zombie.iso.SliceY;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.worldMap.symbols.WorldMapSymbols;


public class MapItem extends InventoryItem {
	public static MapItem WORLD_MAP_INSTANCE;
	private static final byte[] FILE_MAGIC = new byte[]{87, 77, 83, 89};
	private String m_mapID;
	private final WorldMapSymbols m_symbols = new WorldMapSymbols();

	public static MapItem getSingleton() {
		if (WORLD_MAP_INSTANCE == null) {
			Item item = ScriptManager.instance.FindItem("Base.Map");
			if (item == null) {
				return null;
			}

			WORLD_MAP_INSTANCE = new MapItem("Base", "World Map", "WorldMap", item);
		}

		return WORLD_MAP_INSTANCE;
	}

	public static void SaveWorldMap() {
		if (WORLD_MAP_INSTANCE != null) {
			try {
				ByteBuffer byteBuffer = SliceY.SliceBuffer;
				byteBuffer.clear();
				byteBuffer.put(FILE_MAGIC);
				byteBuffer.putInt(186);
				WORLD_MAP_INSTANCE.getSymbols().save(byteBuffer);
				String string = ZomboidFileSystem.instance.getGameModeCacheDir();
				File file = new File(string + File.separator + Core.GameSaveWorld + File.separator + "map_symbols.bin");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				try {
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					try {
						bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
					} catch (Throwable throwable) {
						try {
							bufferedOutputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedOutputStream.close();
				} catch (Throwable throwable3) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileOutputStream.close();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	public static void LoadWorldMap() {
		if (getSingleton() != null) {
			String string = ZomboidFileSystem.instance.getGameModeCacheDir();
			File file = new File(string + File.separator + Core.GameSaveWorld + File.separator + "map_symbols.bin");
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				try {
					BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
					try {
						ByteBuffer byteBuffer = SliceY.SliceBuffer;
						byteBuffer.clear();
						int int1 = bufferedInputStream.read(byteBuffer.array());
						byteBuffer.limit(int1);
						byte[] byteArray = new byte[4];
						byteBuffer.get(byteArray);
						if (!Arrays.equals(byteArray, FILE_MAGIC)) {
							throw new IOException(file.getAbsolutePath() + " does not appear to be map_symbols.bin");
						}

						int int2 = byteBuffer.getInt();
						getSingleton().getSymbols().load(byteBuffer, int2);
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
			} catch (FileNotFoundException fileNotFoundException) {
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	public static void Reset() {
		if (WORLD_MAP_INSTANCE != null) {
			WORLD_MAP_INSTANCE.getSymbols().clear();
			WORLD_MAP_INSTANCE = null;
		}
	}

	public MapItem(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
	}

	public MapItem(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
	}

	public int getSaveType() {
		return Item.Type.Map.ordinal();
	}

	public boolean IsMap() {
		return true;
	}

	public void setMapID(String string) {
		this.m_mapID = string;
	}

	public String getMapID() {
		return this.m_mapID;
	}

	public WorldMapSymbols getSymbols() {
		return this.m_symbols;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		GameWindow.WriteString(byteBuffer, this.m_mapID);
		this.m_symbols.save(byteBuffer);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.m_mapID = GameWindow.ReadString(byteBuffer);
		this.m_symbols.load(byteBuffer, int1);
	}
}
