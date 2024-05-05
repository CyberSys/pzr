package zombie.world;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.world.logger.Log;
import zombie.world.logger.WorldDictionaryLogger;


public class DictionaryData {
	protected final Map itemIdToInfoMap = new HashMap();
	protected final Map itemTypeToInfoMap = new HashMap();
	protected final Map spriteNameToIdMap = new HashMap();
	protected final Map spriteIdToNameMap = new HashMap();
	protected final Map objectNameToIdMap = new HashMap();
	protected final Map objectIdToNameMap = new HashMap();
	protected final ArrayList unsetObject = new ArrayList();
	protected final ArrayList unsetSprites = new ArrayList();
	protected short NextItemID = 0;
	protected int NextSpriteNameID = 0;
	protected byte NextObjectNameID = 0;
	protected byte[] serverDataCache;
	private File dataBackupPath;

	protected boolean isClient() {
		return false;
	}

	protected void reset() {
		this.NextItemID = 0;
		this.NextSpriteNameID = 0;
		this.NextObjectNameID = 0;
		this.itemIdToInfoMap.clear();
		this.itemTypeToInfoMap.clear();
		this.objectIdToNameMap.clear();
		this.objectNameToIdMap.clear();
		this.spriteIdToNameMap.clear();
		this.spriteNameToIdMap.clear();
	}

	protected final ItemInfo getItemInfoFromType(String string) {
		return (ItemInfo)this.itemTypeToInfoMap.get(string);
	}

	protected final ItemInfo getItemInfoFromID(short short1) {
		return (ItemInfo)this.itemIdToInfoMap.get(short1);
	}

	protected final short getItemRegistryID(String string) {
		ItemInfo itemInfo = (ItemInfo)this.itemTypeToInfoMap.get(string);
		if (itemInfo != null) {
			return itemInfo.registryID;
		} else {
			if (Core.bDebug) {
				DebugLog.log("WARNING: Cannot get registry id for item: " + string);
			}

			return -1;
		}
	}

	protected final String getItemTypeFromID(short short1) {
		ItemInfo itemInfo = (ItemInfo)this.itemIdToInfoMap.get(short1);
		return itemInfo != null ? itemInfo.fullType : null;
	}

	protected final String getItemTypeDebugString(short short1) {
		String string = this.getItemTypeFromID(short1);
		if (string == null) {
			string = "Unknown";
		}

		return string;
	}

	protected final String getSpriteNameFromID(int int1) {
		if (int1 >= 0) {
			if (this.spriteIdToNameMap.containsKey(int1)) {
				return (String)this.spriteIdToNameMap.get(int1);
			}

			IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, int1);
			if (sprite != null && sprite.name != null) {
				return sprite.name;
			}
		}

		DebugLog.log("WorldDictionary, Couldnt find sprite name for ID \'" + int1 + "\'.");
		return null;
	}

	protected final int getIdForSpriteName(String string) {
		if (string != null) {
			if (this.spriteNameToIdMap.containsKey(string)) {
				return (Integer)this.spriteNameToIdMap.get(string);
			}

			IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
			if (sprite != null && sprite.ID >= 0 && sprite.ID != 20000000 && sprite.name.equals(string)) {
				return sprite.ID;
			}
		}

		return -1;
	}

	protected final String getObjectNameFromID(byte byte1) {
		if (byte1 >= 0) {
			if (this.objectIdToNameMap.containsKey(byte1)) {
				return (String)this.objectIdToNameMap.get(byte1);
			}

			if (Core.bDebug) {
				DebugLog.log("WorldDictionary, Couldnt find object name for ID \'" + byte1 + "\'.");
			}
		}

		return null;
	}

	protected final byte getIdForObjectName(String string) {
		if (string != null) {
			if (this.objectNameToIdMap.containsKey(string)) {
				return (Byte)this.objectNameToIdMap.get(string);
			}

			if (Core.bDebug) {
			}
		}

		return -1;
	}

	protected final void getItemMods(List list) {
		list.clear();
		Iterator iterator = this.itemIdToInfoMap.entrySet().iterator();
		while (true) {
			Entry entry;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				entry = (Entry)iterator.next();
				if (!list.contains(((ItemInfo)entry.getValue()).modID)) {
					list.add(((ItemInfo)entry.getValue()).modID);
				}
			}	 while (((ItemInfo)entry.getValue()).modOverrides == null);

			List list2 = ((ItemInfo)entry.getValue()).modOverrides;
			for (int int1 = 0; int1 < list2.size(); ++int1) {
				if (!list.contains(list2.get(int1))) {
					list.add((String)list2.get(int1));
				}
			}
		}
	}

	protected final void getModuleList(List list) {
		Iterator iterator = this.itemIdToInfoMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (!list.contains(((ItemInfo)entry.getValue()).moduleName)) {
				list.add(((ItemInfo)entry.getValue()).moduleName);
			}
		}
	}

	protected void parseItemLoadList(Map map) throws WorldDictionaryException {
		Iterator iterator = map.entrySet().iterator();
		while (true) {
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				ItemInfo itemInfo = (ItemInfo)entry.getValue();
				ItemInfo itemInfo2 = (ItemInfo)this.itemTypeToInfoMap.get(itemInfo.fullType);
				if (itemInfo2 == null) {
					if (!itemInfo.obsolete) {
						if (this.NextItemID >= 32767) {
							throw new WorldDictionaryException("Max item ID value reached for WorldDictionary!");
						}

						short short1 = this.NextItemID;
						this.NextItemID = (short)(short1 + 1);
						itemInfo.registryID = short1;
						itemInfo.isLoaded = true;
						this.itemTypeToInfoMap.put(itemInfo.fullType, itemInfo);
						this.itemIdToInfoMap.put(itemInfo.registryID, itemInfo);
						WorldDictionaryLogger.log((Log.BaseLog)(new Log.RegisterItem(itemInfo.copy())));
					}
				} else {
					if (itemInfo2.removed && !itemInfo.obsolete) {
						itemInfo2.removed = false;
						WorldDictionaryLogger.log((Log.BaseLog)(new Log.ReinstateItem(itemInfo2.copy())));
					}

					if (!itemInfo2.modID.equals(itemInfo.modID)) {
						String string = itemInfo2.modID;
						itemInfo2.modID = itemInfo.modID;
						itemInfo2.isModded = !itemInfo.modID.equals("pz-vanilla");
						WorldDictionaryLogger.log((Log.BaseLog)(new Log.ModIDChangedItem(itemInfo2.copy(), string, itemInfo2.modID)));
					}

					if (itemInfo.obsolete && (!itemInfo2.obsolete || !itemInfo2.removed)) {
						itemInfo2.obsolete = true;
						itemInfo2.removed = true;
						WorldDictionaryLogger.log((Log.BaseLog)(new Log.ObsoleteItem(itemInfo2.copy())));
					}

					itemInfo2.isLoaded = true;
				}
			}

			return;
		}
	}

	protected void parseCurrentItemSet() throws WorldDictionaryException {
		Iterator iterator = this.itemTypeToInfoMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			ItemInfo itemInfo = (ItemInfo)entry.getValue();
			if (!itemInfo.isLoaded) {
				itemInfo.removed = true;
				WorldDictionaryLogger.log((Log.BaseLog)(new Log.RemovedItem(itemInfo.copy(), false)));
			}

			if (itemInfo.scriptItem == null) {
				itemInfo.scriptItem = ScriptManager.instance.getSpecificItem(itemInfo.fullType);
			}

			if (itemInfo.scriptItem != null) {
				itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
			} else {
				itemInfo.removed = true;
				WorldDictionaryLogger.log((Log.BaseLog)(new Log.RemovedItem(itemInfo.copy(), true)));
			}
		}
	}

	protected void parseObjectNameLoadList(List list) throws WorldDictionaryException {
		for (int int1 = 0; int1 < list.size(); ++int1) {
			String string = (String)list.get(int1);
			if (!this.objectNameToIdMap.containsKey(string)) {
				if (this.NextObjectNameID >= 127) {
					WorldDictionaryLogger.log("Max value for object names reached.");
					if (Core.bDebug) {
						throw new WorldDictionaryException("Max value for object names reached.");
					}
				} else {
					byte byte1 = this.NextObjectNameID;
					this.NextObjectNameID = (byte)(byte1 + 1);
					byte byte2 = byte1;
					this.objectIdToNameMap.put(byte2, string);
					this.objectNameToIdMap.put(string, byte2);
					WorldDictionaryLogger.log((Log.BaseLog)(new Log.RegisterObject(string, byte2)));
				}
			}
		}
	}

	protected void backupCurrentDataSet() throws IOException {
		this.dataBackupPath = null;
		if (!Core.getInstance().isNoSave()) {
			String string = ZomboidFileSystem.instance.getGameModeCacheDir();
			File file = new File(string + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionary.bin");
			if (file.exists()) {
				long long1 = Instant.now().getEpochSecond();
				String string2 = ZomboidFileSystem.instance.getGameModeCacheDir();
				this.dataBackupPath = new File(string2 + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionary_" + long1 + ".bak");
				Files.copy(file, this.dataBackupPath);
			}
		}
	}

	protected void deleteBackupCurrentDataSet() throws IOException {
		if (Core.getInstance().isNoSave()) {
			this.dataBackupPath = null;
		} else {
			if (this.dataBackupPath != null) {
				this.dataBackupPath.delete();
			}

			this.dataBackupPath = null;
		}
	}

	protected void createErrorBackups() {
		if (!Core.getInstance().isNoSave()) {
			try {
				WorldDictionary.log("Attempting to copy WorldDictionary backups...");
				long long1 = Instant.now().getEpochSecond();
				String string = ZomboidFileSystem.instance.getGameModeCacheDir();
				String string2 = string + File.separator + Core.GameSaveWorld + File.separator + "WD_ERROR_" + long1 + File.separator;
				WorldDictionary.log("path = " + string2);
				File file = new File(string2);
				boolean boolean1 = true;
				if (!file.exists()) {
					boolean1 = file.mkdir();
				}

				if (!boolean1) {
					WorldDictionary.log("Could not create backup folder folder.");
					return;
				}

				File file2;
				if (this.dataBackupPath != null) {
					file2 = new File(string2 + "WorldDictionary_backup.bin");
					if (this.dataBackupPath.exists()) {
						Files.copy(this.dataBackupPath, file2);
					}
				}

				String string3 = ZomboidFileSystem.instance.getGameModeCacheDir();
				file2 = new File(string3 + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionaryLog.lua");
				File file3 = new File(string2 + "WorldDictionaryLog.lua");
				if (file2.exists()) {
					Files.copy(file2, file3);
				}

				string3 = ZomboidFileSystem.instance.getGameModeCacheDir();
				File file4 = new File(string3 + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionaryReadable.lua");
				File file5 = new File(string2 + "WorldDictionaryReadable.lua");
				if (file4.exists()) {
					Files.copy(file4, file5);
				}

				string3 = ZomboidFileSystem.instance.getGameModeCacheDir();
				File file6 = new File(string3 + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionary.bin");
				File file7 = new File(string2 + "WorldDictionary.bin");
				if (file6.exists()) {
					Files.copy(file6, file7);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	protected void load() throws IOException, WorldDictionaryException {
		if (!Core.getInstance().isNoSave()) {
			String string = ZomboidFileSystem.instance.getGameModeCacheDir();
			String string2 = string + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionary.bin";
			File file = new File(string2);
			if (!file.exists()) {
				if (!WorldDictionary.isIsNewGame()) {
					throw new WorldDictionaryException("WorldDictionary data file is missing from world folder.");
				}
			} else {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					try {
						DebugLog.log("Loading WorldDictionary:" + string2);
						ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
						byteBuffer.clear();
						int int1 = fileInputStream.read(byteBuffer.array());
						byteBuffer.limit(int1);
						this.loadFromByteBuffer(byteBuffer);
					} catch (Throwable throwable) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					fileInputStream.close();
				} catch (Exception exception) {
					exception.printStackTrace();
					throw new WorldDictionaryException("Error loading WorldDictionary.", exception);
				}
			}
		}
	}

	protected void loadFromByteBuffer(ByteBuffer byteBuffer) throws IOException {
		this.NextItemID = byteBuffer.getShort();
		this.NextObjectNameID = byteBuffer.get();
		this.NextSpriteNameID = byteBuffer.getInt();
		ArrayList arrayList = new ArrayList();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			arrayList.add(GameWindow.ReadString(byteBuffer));
		}

		ArrayList arrayList2 = new ArrayList();
		int int3 = byteBuffer.getInt();
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			arrayList2.add(GameWindow.ReadString(byteBuffer));
		}

		int4 = byteBuffer.getInt();
		int int5;
		for (int5 = 0; int5 < int4; ++int5) {
			ItemInfo itemInfo = new ItemInfo();
			itemInfo.load(byteBuffer, 186, arrayList, arrayList2);
			this.itemIdToInfoMap.put(itemInfo.registryID, itemInfo);
			this.itemTypeToInfoMap.put(itemInfo.fullType, itemInfo);
		}

		int5 = byteBuffer.getInt();
		int int6;
		for (int6 = 0; int6 < int5; ++int6) {
			byte byte1 = byteBuffer.get();
			String string = GameWindow.ReadString(byteBuffer);
			this.objectIdToNameMap.put(byte1, string);
			this.objectNameToIdMap.put(string, byte1);
		}

		int6 = byteBuffer.getInt();
		for (int int7 = 0; int7 < int6; ++int7) {
			int int8 = byteBuffer.getInt();
			String string2 = GameWindow.ReadString(byteBuffer);
			this.spriteIdToNameMap.put(int8, string2);
			this.spriteNameToIdMap.put(string2, int8);
		}
	}

	protected void save() throws IOException, WorldDictionaryException {
		if (!Core.getInstance().isNoSave()) {
			try {
				byte[] byteArray = new byte[5242880];
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
				this.saveToByteBuffer(byteBuffer);
				byteBuffer.flip();
				if (GameServer.bServer) {
					byteArray = new byte[byteBuffer.limit()];
					byteBuffer.get(byteArray, 0, byteArray.length);
					this.serverDataCache = byteArray;
				}

				String string = ZomboidFileSystem.instance.getGameModeCacheDir();
				File file = new File(string + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionary.tmp");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.getChannel().truncate(0L);
				fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
				fileOutputStream.flush();
				fileOutputStream.close();
				string = ZomboidFileSystem.instance.getGameModeCacheDir();
				File file2 = new File(string + File.separator + Core.GameSaveWorld + File.separator + "WorldDictionary.bin");
				Files.copy(file, file2);
				file.delete();
			} catch (Exception exception) {
				exception.printStackTrace();
				throw new WorldDictionaryException("Error saving WorldDictionary.", exception);
			}
		}
	}

	protected void saveToByteBuffer(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putShort(this.NextItemID);
		byteBuffer.put(this.NextObjectNameID);
		byteBuffer.putInt(this.NextSpriteNameID);
		ArrayList arrayList = new ArrayList();
		this.getItemMods(arrayList);
		byteBuffer.putInt(arrayList.size());
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
		}

		ArrayList arrayList2 = new ArrayList();
		this.getModuleList(arrayList2);
		byteBuffer.putInt(arrayList2.size());
		Iterator iterator2 = arrayList2.iterator();
		while (iterator2.hasNext()) {
			String string2 = (String)iterator2.next();
			GameWindow.WriteString(byteBuffer, string2);
		}

		byteBuffer.putInt(this.itemIdToInfoMap.size());
		iterator2 = this.itemIdToInfoMap.entrySet().iterator();
		Entry entry;
		while (iterator2.hasNext()) {
			entry = (Entry)iterator2.next();
			ItemInfo itemInfo = (ItemInfo)entry.getValue();
			itemInfo.save(byteBuffer, arrayList, arrayList2);
		}

		byteBuffer.putInt(this.objectIdToNameMap.size());
		iterator2 = this.objectIdToNameMap.entrySet().iterator();
		while (iterator2.hasNext()) {
			entry = (Entry)iterator2.next();
			byteBuffer.put((Byte)entry.getKey());
			GameWindow.WriteString(byteBuffer, (String)entry.getValue());
		}

		byteBuffer.putInt(this.spriteIdToNameMap.size());
		iterator2 = this.spriteIdToNameMap.entrySet().iterator();
		while (iterator2.hasNext()) {
			entry = (Entry)iterator2.next();
			byteBuffer.putInt((Integer)entry.getKey());
			GameWindow.WriteString(byteBuffer, (String)entry.getValue());
		}
	}

	protected void saveAsText(String string) throws IOException, WorldDictionaryException {
		if (!Core.getInstance().isNoSave()) {
			String string2 = ZomboidFileSystem.instance.getGameModeCacheDir();
			File file = new File(string2 + File.separator + Core.GameSaveWorld + File.separator);
			if (file.exists() && file.isDirectory()) {
				String string3 = ZomboidFileSystem.instance.getGameModeCacheDir();
				String string4 = string3 + File.separator + Core.GameSaveWorld + File.separator + string;
				File file2 = new File(string4);
				try {
					FileWriter fileWriter = new FileWriter(file2, false);
					try {
						fileWriter.write("--[[ ---- ITEMS ---- --]]" + System.lineSeparator());
						fileWriter.write("items = {" + System.lineSeparator());
						Iterator iterator = this.itemIdToInfoMap.entrySet().iterator();
						Entry entry;
						while (iterator.hasNext()) {
							entry = (Entry)iterator.next();
							fileWriter.write("\t{" + System.lineSeparator());
							((ItemInfo)entry.getValue()).saveAsText(fileWriter, "\t\t");
							fileWriter.write("\t}," + System.lineSeparator());
						}

						fileWriter.write("}" + System.lineSeparator());
						fileWriter.write(System.lineSeparator().makeConcatWithConstants < invokedynamic > (System.lineSeparator()));
						fileWriter.write("--[[ ---- OBJECTS ---- --]]" + System.lineSeparator());
						fileWriter.write("objects = {" + System.lineSeparator());
						iterator = this.objectIdToNameMap.entrySet().iterator();
						Object object;
						while (iterator.hasNext()) {
							entry = (Entry)iterator.next();
							object = entry.getKey();
							fileWriter.write("\t" + object + " = \"" + (String)entry.getValue() + "\"," + System.lineSeparator());
						}

						fileWriter.write("}" + System.lineSeparator());
						fileWriter.write(System.lineSeparator().makeConcatWithConstants < invokedynamic > (System.lineSeparator()));
						fileWriter.write("--[[ ---- SPRITES ---- --]]" + System.lineSeparator());
						fileWriter.write("sprites = {" + System.lineSeparator());
						iterator = this.spriteIdToNameMap.entrySet().iterator();
						while (iterator.hasNext()) {
							entry = (Entry)iterator.next();
							object = entry.getKey();
							fileWriter.write("\t" + object + " = \"" + (String)entry.getValue() + "\"," + System.lineSeparator());
						}

						fileWriter.write("}" + System.lineSeparator());
					} catch (Throwable throwable) {
						try {
							fileWriter.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					fileWriter.close();
				} catch (Exception exception) {
					exception.printStackTrace();
					throw new WorldDictionaryException("Error saving WorldDictionary as text.", exception);
				}
			}
		}
	}
}
