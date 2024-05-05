package zombie.world;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionRegions;
import zombie.erosion.categories.ErosionCategory;
import zombie.gameStates.ChooseGameInfo;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.objects.Item;
import zombie.world.logger.Log;
import zombie.world.logger.WorldDictionaryLogger;


public class WorldDictionary {
	public static final String SAVE_FILE_READABLE = "WorldDictionaryReadable.lua";
	public static final String SAVE_FILE_LOG = "WorldDictionaryLog.lua";
	public static final String SAVE_FILE = "WorldDictionary";
	public static final String SAVE_EXT = ".bin";
	public static final boolean logUnset = false;
	public static final boolean logMissingObjectID = false;
	private static final Map itemLoadList = new HashMap();
	private static final List objNameLoadList = new ArrayList();
	private static DictionaryData data;
	private static boolean isNewGame = true;
	private static boolean allowScriptItemLoading = false;
	private static final String netValidator = "DICTIONARY_PACKET_END";
	private static byte[] clientRemoteData;

	protected static void log(String string) {
		log(string, true);
	}

	protected static void log(String string, boolean boolean1) {
		if (boolean1) {
			DebugLog.log("WorldDictionary: " + string);
		}
	}

	public static void setIsNewGame(boolean boolean1) {
		isNewGame = boolean1;
	}

	public static boolean isIsNewGame() {
		return isNewGame;
	}

	public static void StartScriptLoading() {
		allowScriptItemLoading = true;
		itemLoadList.clear();
	}

	public static void ScriptsLoaded() {
		allowScriptItemLoading = false;
	}

	public static void onLoadItem(Item item) {
		if (!GameClient.bClient) {
			if (!allowScriptItemLoading) {
				log("Warning script item loaded after WorldDictionary is initialised");
				if (Core.bDebug) {
					throw new RuntimeException("This shouldn\'t be happening.");
				}
			}

			ItemInfo itemInfo = (ItemInfo)itemLoadList.get(item.getFullName());
			if (itemInfo == null) {
				itemInfo = new ItemInfo();
				itemInfo.itemName = item.getName();
				itemInfo.moduleName = item.getModuleName();
				itemInfo.fullType = item.getFullName();
				itemLoadList.put(item.getFullName(), itemInfo);
			}

			if (itemInfo.modID != null && !item.getModID().equals(itemInfo.modID)) {
				if (itemInfo.modOverrides == null) {
					itemInfo.modOverrides = new ArrayList();
				}

				if (!itemInfo.modOverrides.contains(itemInfo.modID)) {
					itemInfo.modOverrides.add(itemInfo.modID);
				} else {
					log("modOverrides for item \'" + itemInfo.fullType + "\' already contains mod id: " + itemInfo.modID);
				}
			}

			itemInfo.modID = item.getModID();
			if (itemInfo.modID.equals("pz-vanilla")) {
				itemInfo.existsAsVanilla = true;
			}

			itemInfo.isModded = !itemInfo.modID.equals("pz-vanilla");
			itemInfo.obsolete = item.getObsolete();
			itemInfo.scriptItem = item;
		}
	}

	private static void collectObjectNames() {
		objNameLoadList.clear();
		if (!GameClient.bClient) {
			ArrayList arrayList = new ArrayList();
			for (int int1 = 0; int1 < ErosionRegions.regions.size(); ++int1) {
				for (int int2 = 0; int2 < ((ErosionRegions.Region)ErosionRegions.regions.get(int1)).categories.size(); ++int2) {
					ErosionCategory erosionCategory = (ErosionCategory)((ErosionRegions.Region)ErosionRegions.regions.get(int1)).categories.get(int2);
					arrayList.clear();
					erosionCategory.getObjectNames(arrayList);
					Iterator iterator = arrayList.iterator();
					while (iterator.hasNext()) {
						String string = (String)iterator.next();
						if (!objNameLoadList.contains(string)) {
							objNameLoadList.add(string);
						}
					}
				}
			}
		}
	}

	public static void loadDataFromServer(ByteBuffer byteBuffer) throws IOException {
		if (GameClient.bClient) {
			int int1 = byteBuffer.getInt();
			clientRemoteData = new byte[int1];
			byteBuffer.get(clientRemoteData, 0, clientRemoteData.length);
		}
	}

	public static void saveDataForClient(ByteBuffer byteBuffer) throws IOException {
		if (GameServer.bServer) {
			int int1 = byteBuffer.position();
			byteBuffer.putInt(0);
			int int2 = byteBuffer.position();
			if (data.serverDataCache != null) {
				byteBuffer.put(data.serverDataCache);
			} else {
				if (Core.bDebug) {
					throw new RuntimeException("Should be sending data from the serverDataCache here.");
				}

				data.saveToByteBuffer(byteBuffer);
			}

			GameWindow.WriteString(byteBuffer, "DICTIONARY_PACKET_END");
			int int3 = byteBuffer.position();
			byteBuffer.position(int1);
			byteBuffer.putInt(int3 - int2);
			byteBuffer.position(int3);
		}
	}

	public static void init() throws WorldDictionaryException {
		boolean boolean1 = true;
		collectObjectNames();
		WorldDictionaryLogger.startLogging();
		WorldDictionaryLogger.log("-------------------------------------------------------", false);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		WorldDictionaryLogger.log("Time: " + simpleDateFormat.format(new Date()), false);
		log("Checking dictionary...");
		Log.Info info = null;
		try {
			if (!GameClient.bClient) {
				if (data == null || data.isClient()) {
					data = new DictionaryData();
				}
			} else if (data == null || !data.isClient()) {
				data = new DictionaryDataClient();
			}

			data.reset();
			if (GameClient.bClient) {
				if (clientRemoteData == null) {
					throw new WorldDictionaryException("WorldDictionary data not received from server.");
				}

				ByteBuffer byteBuffer = ByteBuffer.wrap(clientRemoteData);
				data.loadFromByteBuffer(byteBuffer);
				String string = GameWindow.ReadString(byteBuffer);
				if (!string.equals("DICTIONARY_PACKET_END")) {
					throw new WorldDictionaryException("WorldDictionary data received from server is corrupt.");
				}

				clientRemoteData = null;
			}

			data.backupCurrentDataSet();
			data.load();
			ArrayList arrayList = new ArrayList();
			info = new Log.Info(simpleDateFormat.format(new Date()), Core.GameSaveWorld, 194, arrayList);
			WorldDictionaryLogger.log((Log.BaseLog)info);
			data.parseItemLoadList(itemLoadList);
			data.parseCurrentItemSet();
			itemLoadList.clear();
			data.parseObjectNameLoadList(objNameLoadList);
			objNameLoadList.clear();
			data.getItemMods(arrayList);
			data.saveAsText("WorldDictionaryReadable.lua");
			data.save();
			data.deleteBackupCurrentDataSet();
		} catch (Exception exception) {
			boolean1 = false;
			exception.printStackTrace();
			log("Warning: error occurred loading dictionary!");
			if (info != null) {
				info.HasErrored = true;
			}

			if (data != null) {
				data.createErrorBackups();
			}
		}

		try {
			WorldDictionaryLogger.saveLog("WorldDictionaryLog.lua");
			WorldDictionaryLogger.reset();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		if (!boolean1) {
			throw new WorldDictionaryException("WorldDictionary: Cannot load world due to WorldDictionary error.");
		}
	}

	public static void onWorldLoaded() {
	}

	public static ItemInfo getItemInfoFromType(String string) {
		return data.getItemInfoFromType(string);
	}

	public static ItemInfo getItemInfoFromID(short short1) {
		return data.getItemInfoFromID(short1);
	}

	public static short getItemRegistryID(String string) {
		return data.getItemRegistryID(string);
	}

	public static String getItemTypeFromID(short short1) {
		return data.getItemTypeFromID(short1);
	}

	public static String getItemTypeDebugString(short short1) {
		return data.getItemTypeDebugString(short1);
	}

	public static String getSpriteNameFromID(int int1) {
		return data.getSpriteNameFromID(int1);
	}

	public static int getIdForSpriteName(String string) {
		return data.getIdForSpriteName(string);
	}

	public static String getObjectNameFromID(byte byte1) {
		return data.getObjectNameFromID(byte1);
	}

	public static byte getIdForObjectName(String string) {
		return data.getIdForObjectName(string);
	}

	public static String getItemModID(short short1) {
		ItemInfo itemInfo = getItemInfoFromID(short1);
		return itemInfo != null ? itemInfo.modID : null;
	}

	public static String getItemModID(String string) {
		ItemInfo itemInfo = getItemInfoFromType(string);
		return itemInfo != null ? itemInfo.modID : null;
	}

	public static String getModNameFromID(String string) {
		if (string != null) {
			if (string.equals("pz-vanilla")) {
				return "Project Zomboid";
			}

			ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
			if (mod != null && mod.getName() != null) {
				return mod.getName();
			}
		}

		return "Unknown mod";
	}

	public static void DebugPrintItem(InventoryItem inventoryItem) {
		Item item = inventoryItem.getScriptItem();
		if (item != null) {
			DebugPrintItem(item);
		} else {
			String string = inventoryItem.getFullType();
			ItemInfo itemInfo = null;
			if (string != null) {
				itemInfo = getItemInfoFromType(string);
			}

			if (itemInfo == null && inventoryItem.getRegistry_id() >= 0) {
				itemInfo = getItemInfoFromID(inventoryItem.getRegistry_id());
			}

			if (itemInfo != null) {
				itemInfo.DebugPrint();
			} else {
				DebugLog.log("WorldDictionary: Cannot debug print item: " + (string != null ? string : "unknown"));
			}
		}
	}

	public static void DebugPrintItem(Item item) {
		String string = item.getFullName();
		ItemInfo itemInfo = null;
		if (string != null) {
			itemInfo = getItemInfoFromType(string);
		}

		if (itemInfo == null && item.getRegistry_id() >= 0) {
			itemInfo = getItemInfoFromID(item.getRegistry_id());
		}

		if (itemInfo != null) {
			itemInfo.DebugPrint();
		} else {
			DebugLog.log("WorldDictionary: Cannot debug print item: " + (string != null ? string : "unknown"));
		}
	}

	public static void DebugPrintItem(String string) {
		ItemInfo itemInfo = getItemInfoFromType(string);
		if (itemInfo != null) {
			itemInfo.DebugPrint();
		} else {
			DebugLog.log("WorldDictionary: Cannot debug print item: " + string);
		}
	}

	public static void DebugPrintItem(short short1) {
		ItemInfo itemInfo = getItemInfoFromID(short1);
		if (itemInfo != null) {
			itemInfo.DebugPrint();
		} else {
			DebugLog.log("WorldDictionary: Cannot debug print item id: " + short1);
		}
	}
}
