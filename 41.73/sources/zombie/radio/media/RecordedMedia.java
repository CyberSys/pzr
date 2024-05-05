package zombie.radio.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.world.WorldDictionary;


public class RecordedMedia {
	public static boolean DISABLE_LINE_LEARNING = false;
	private static final int SPAWN_COMMON = 0;
	private static final int SPAWN_RARE = 1;
	private static final int SPAWN_EXCEPTIONAL = 2;
	public static final int VERSION1 = 1;
	public static final int VERSION2 = 2;
	public static final int VERSION = 2;
	public static final String SAVE_FILE = "recorded_media.bin";
	private final ArrayList indexes = new ArrayList();
	private static final ArrayList indexesFromServer = new ArrayList();
	private final Map mediaDataMap = new HashMap();
	private final Map categorizedMap = new HashMap();
	private final ArrayList categories = new ArrayList();
	private final ArrayList legacyListenedLines = new ArrayList();
	private final HashSet homeVhsSpawned = new HashSet();
	private final Map retailVhsSpawnTable = new HashMap();
	private final Map retailCdSpawnTable = new HashMap();
	private boolean REQUIRES_SAVING = true;

	public void init() {
		try {
			this.load();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		LuaEventManager.triggerEvent("OnInitRecordedMedia", this);
		this.retailCdSpawnTable.put(0, new ArrayList());
		this.retailCdSpawnTable.put(1, new ArrayList());
		this.retailCdSpawnTable.put(2, new ArrayList());
		this.retailVhsSpawnTable.put(0, new ArrayList());
		this.retailVhsSpawnTable.put(1, new ArrayList());
		this.retailVhsSpawnTable.put(2, new ArrayList());
		ArrayList arrayList = (ArrayList)this.categorizedMap.get("CDs");
		Iterator iterator;
		MediaData mediaData;
		if (arrayList != null) {
			iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				mediaData = (MediaData)iterator.next();
				if (mediaData.getSpawning() == 1) {
					((ArrayList)this.retailCdSpawnTable.get(1)).add(mediaData);
				} else if (mediaData.getSpawning() == 2) {
					((ArrayList)this.retailCdSpawnTable.get(2)).add(mediaData);
				} else {
					((ArrayList)this.retailCdSpawnTable.get(0)).add(mediaData);
				}
			}
		} else {
			DebugLog.General.error("categorizedMap with CDs is empty");
		}

		arrayList = (ArrayList)this.categorizedMap.get("Retail-VHS");
		if (arrayList != null) {
			iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				mediaData = (MediaData)iterator.next();
				if (mediaData.getSpawning() == 1) {
					((ArrayList)this.retailVhsSpawnTable.get(1)).add(mediaData);
				} else if (mediaData.getSpawning() == 2) {
					((ArrayList)this.retailVhsSpawnTable.get(2)).add(mediaData);
				} else {
					((ArrayList)this.retailVhsSpawnTable.get(0)).add(mediaData);
				}
			}
		} else {
			DebugLog.General.error("categorizedMap with Retail-VHS is empty");
		}

		try {
			this.save();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}
	}

	public static byte getMediaTypeForCategory(String string) {
		if (string == null) {
			return -1;
		} else {
			return (byte)(string.equalsIgnoreCase("cds") ? 0 : 1);
		}
	}

	public ArrayList getCategories() {
		return this.categories;
	}

	public ArrayList getAllMediaForType(byte byte1) {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.mediaDataMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (((MediaData)entry.getValue()).getMediaType() == byte1) {
				arrayList.add((MediaData)entry.getValue());
			}
		}

		arrayList.sort(new RecordedMedia.MediaNameSorter());
		return arrayList;
	}

	public ArrayList getAllMediaForCategory(String string) {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.mediaDataMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (string.equalsIgnoreCase(((MediaData)entry.getValue()).getCategory())) {
				arrayList.add((MediaData)entry.getValue());
			}
		}

		arrayList.sort(new RecordedMedia.MediaNameSorter());
		return arrayList;
	}

	public MediaData register(String string, String string2, String string3, int int1) {
		if (this.mediaDataMap.containsKey(string2)) {
			DebugLog.log("RecordeMedia -> MediaData id already exists : " + string2);
			return null;
		} else {
			if (int1 < 0) {
				int1 = 0;
			}

			MediaData mediaData = new MediaData(string2, string3, int1);
			this.mediaDataMap.put(string2, mediaData);
			mediaData.setCategory(string);
			if (!this.categorizedMap.containsKey(string)) {
				this.categorizedMap.put(string, new ArrayList());
				this.categories.add(string);
			}

			((ArrayList)this.categorizedMap.get(string)).add(mediaData);
			short short1;
			if (this.indexes.contains(string2)) {
				short1 = (short)this.indexes.indexOf(string2);
			} else {
				short1 = (short)this.indexes.size();
				this.indexes.add(string2);
			}

			mediaData.setIndex(short1);
			this.REQUIRES_SAVING = true;
			return mediaData;
		}
	}

	public MediaData getMediaDataFromIndex(short short1) {
		return short1 >= 0 && short1 < this.indexes.size() ? this.getMediaData((String)this.indexes.get(short1)) : null;
	}

	public short getIndexForMediaData(MediaData mediaData) {
		return (short)this.indexes.indexOf(mediaData.getId());
	}

	public MediaData getMediaData(String string) {
		return (MediaData)this.mediaDataMap.get(string);
	}

	public MediaData getRandomFromCategory(String string) {
		if (this.categorizedMap.containsKey(string)) {
			MediaData mediaData = null;
			int int1;
			if (string.equalsIgnoreCase("cds")) {
				int1 = Rand.Next(0, 1000);
				if (int1 < 100) {
					if (((ArrayList)this.retailCdSpawnTable.get(2)).size() > 0) {
						mediaData = (MediaData)((ArrayList)this.retailCdSpawnTable.get(2)).get(Rand.Next(0, ((ArrayList)this.retailCdSpawnTable.get(2)).size()));
					}
				} else if (int1 < 400) {
					if (((ArrayList)this.retailCdSpawnTable.get(1)).size() > 0) {
						mediaData = (MediaData)((ArrayList)this.retailCdSpawnTable.get(1)).get(Rand.Next(0, ((ArrayList)this.retailCdSpawnTable.get(1)).size()));
					}
				} else {
					mediaData = (MediaData)((ArrayList)this.retailCdSpawnTable.get(0)).get(Rand.Next(0, ((ArrayList)this.retailCdSpawnTable.get(0)).size()));
				}

				if (mediaData != null) {
					return mediaData;
				}

				return (MediaData)((ArrayList)this.retailCdSpawnTable.get(0)).get(Rand.Next(0, ((ArrayList)this.retailCdSpawnTable.get(0)).size()));
			}

			if (string.equalsIgnoreCase("retail-vhs")) {
				int1 = Rand.Next(0, 1000);
				if (int1 < 100) {
					if (((ArrayList)this.retailVhsSpawnTable.get(2)).size() > 0) {
						mediaData = (MediaData)((ArrayList)this.retailVhsSpawnTable.get(2)).get(Rand.Next(0, ((ArrayList)this.retailVhsSpawnTable.get(2)).size()));
					}
				} else if (int1 < 400) {
					if (((ArrayList)this.retailVhsSpawnTable.get(1)).size() > 0) {
						mediaData = (MediaData)((ArrayList)this.retailVhsSpawnTable.get(1)).get(Rand.Next(0, ((ArrayList)this.retailVhsSpawnTable.get(1)).size()));
					}
				} else {
					mediaData = (MediaData)((ArrayList)this.retailVhsSpawnTable.get(0)).get(Rand.Next(0, ((ArrayList)this.retailVhsSpawnTable.get(0)).size()));
				}

				if (mediaData != null) {
					return mediaData;
				}

				return (MediaData)((ArrayList)this.retailVhsSpawnTable.get(0)).get(Rand.Next(0, ((ArrayList)this.retailVhsSpawnTable.get(0)).size()));
			}

			if (string.equalsIgnoreCase("home-vhs")) {
				int1 = Rand.Next(0, 1000);
				if (int1 < 200) {
					ArrayList arrayList = (ArrayList)this.categorizedMap.get("Home-VHS");
					mediaData = (MediaData)arrayList.get(Rand.Next(0, arrayList.size()));
					if (!this.homeVhsSpawned.contains(mediaData.getIndex())) {
						this.homeVhsSpawned.add(mediaData.getIndex());
						this.REQUIRES_SAVING = true;
						return mediaData;
					}
				}
			}
		}

		return null;
	}

	public void load() throws IOException {
		this.indexes.clear();
		if (GameClient.bClient) {
			this.indexes.addAll(indexesFromServer);
			indexesFromServer.clear();
		}

		if (!Core.getInstance().isNoSave()) {
			String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("recorded_media.bin");
			File file = new File(string);
			if (!file.exists()) {
				if (!WorldDictionary.isIsNewGame()) {
					DebugLog.log("RecordedMedia data file is missing from world folder.");
				}
			} else {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					try {
						DebugLog.log("Loading Recorded Media:" + string);
						ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
						byteBuffer.clear();
						int int1 = fileInputStream.read(byteBuffer.array());
						byteBuffer.limit(int1);
						int int2 = byteBuffer.getInt();
						int int3 = byteBuffer.getInt();
						int int4;
						String string2;
						for (int4 = 0; int4 < int3; ++int4) {
							string2 = GameWindow.ReadString(byteBuffer);
							if (!GameClient.bClient) {
								this.indexes.add(string2);
							}
						}

						if (int2 == 1) {
							int3 = byteBuffer.getInt();
							for (int4 = 0; int4 < int3; ++int4) {
								string2 = GameWindow.ReadString(byteBuffer);
								this.legacyListenedLines.add(string2);
							}
						}

						int3 = byteBuffer.getInt();
						for (int4 = 0; int4 < int3; ++int4) {
							this.homeVhsSpawned.add(byteBuffer.getShort());
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
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public void save() throws IOException {
		if (!Core.getInstance().isNoSave() && this.REQUIRES_SAVING) {
			try {
				byte byte1 = 0;
				int int1 = byte1 + this.indexes.size() * 40;
				int1 += this.homeVhsSpawned.size() * 2;
				int1 += 512;
				byte[] byteArray = new byte[int1];
				ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
				byteBuffer.putInt(2);
				byteBuffer.putInt(this.indexes.size());
				for (int int2 = 0; int2 < this.indexes.size(); ++int2) {
					GameWindow.WriteString(byteBuffer, (String)this.indexes.get(int2));
				}

				byteBuffer.putInt(this.homeVhsSpawned.size());
				Short[] shortArray = (Short[])this.homeVhsSpawned.toArray(new Short[0]);
				for (int int3 = 0; int3 < shortArray.length; ++int3) {
					byteBuffer.putShort(shortArray[int3]);
				}

				byteBuffer.flip();
				String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("recorded_media.bin");
				File file = new File(string);
				DebugLog.log("Saving Recorded Media:" + string);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.getChannel().truncate(0L);
				fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			this.REQUIRES_SAVING = false;
		}
	}

	public static String toAscii(String string) {
		StringBuilder stringBuilder = new StringBuilder(string.length());
		string = Normalizer.normalize(string, Form.NFD);
		char[] charArray = string.toCharArray();
		int int1 = charArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			char char1 = charArray[int2];
			if (char1 <= 127) {
				stringBuilder.append(char1);
			}
		}

		return stringBuilder.toString();
	}

	public boolean hasListenedToLine(IsoPlayer player, String string) {
		return player.isKnownMediaLine(string);
	}

	public boolean hasListenedToAll(IsoPlayer player, MediaData mediaData) {
		if (player == null) {
			player = IsoPlayer.players[0];
		}

		if (player != null && mediaData != null) {
			for (int int1 = 0; int1 < mediaData.getLineCount(); ++int1) {
				MediaData.MediaLineData mediaLineData = mediaData.getLine(int1);
				if (!player.isKnownMediaLine(mediaLineData.getTextGuid())) {
					return false;
				}
			}

			return mediaData.getLineCount() > 0;
		} else {
			return false;
		}
	}

	public void sendRequestData(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.indexes.size());
		for (int int1 = 0; int1 < this.indexes.size(); ++int1) {
			byteBufferWriter.putUTF((String)this.indexes.get(int1));
		}
	}

	public static void receiveRequestData(ByteBuffer byteBuffer) {
		indexesFromServer.clear();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			indexesFromServer.add(GameWindow.ReadStringUTF(byteBuffer));
		}
	}

	public void handleLegacyListenedLines(IsoPlayer player) {
		if (!this.legacyListenedLines.isEmpty()) {
			if (player != null) {
				Iterator iterator = this.legacyListenedLines.iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					player.addKnownMediaLine(string);
				}
			}

			this.legacyListenedLines.clear();
		}
	}

	public static class MediaNameSorter implements Comparator {

		public int compare(MediaData mediaData, MediaData mediaData2) {
			return mediaData.getTranslatedItemDisplayName().compareToIgnoreCase(mediaData2.getTranslatedItemDisplayName());
		}
	}
}
