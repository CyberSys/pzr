package zombie.inventory;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameApplet;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.inventory.types.InventoryContainer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;


public class CompressIdenticalItems {
	private static final int BLOCK_SIZE = 1024;
	private static ThreadLocal perThreadVars = new ThreadLocal(){
    
    protected CompressIdenticalItems.PerThreadData initialValue() {
        return new CompressIdenticalItems.PerThreadData();
    }
};

	private static int bufferSize(int int1) {
		return (int1 + 1024 - 1) / 1024 * 1024;
	}

	private static ByteBuffer ensureCapacity(ByteBuffer byteBuffer, int int1) {
		if (byteBuffer == null || byteBuffer.capacity() < int1) {
			byteBuffer = ByteBuffer.allocate(bufferSize(int1));
		}

		return byteBuffer;
	}

	private static ByteBuffer ensureCapacity(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			return ByteBuffer.allocate(1024);
		} else if (byteBuffer.capacity() - byteBuffer.position() < 1024) {
			ByteBuffer byteBuffer2 = ensureCapacity((ByteBuffer)null, byteBuffer.position() + 1024);
			return byteBuffer2.put(byteBuffer.array(), 0, byteBuffer.position());
		} else {
			return byteBuffer;
		}
	}

	private static boolean areItemsIdentical(CompressIdenticalItems.PerThreadData perThreadData, InventoryItem inventoryItem, InventoryItem inventoryItem2) throws IOException {
		if (inventoryItem instanceof InventoryContainer) {
			ItemContainer itemContainer = ((InventoryContainer)inventoryItem).getInventory();
			ItemContainer itemContainer2 = ((InventoryContainer)inventoryItem2).getInventory();
			if (!itemContainer.getItems().isEmpty() || !itemContainer2.getItems().isEmpty()) {
				return false;
			}
		}

		ByteBuffer byteBuffer = perThreadData.itemCompareBuffer;
		byteBuffer.clear();
		long long1 = inventoryItem.id;
		long long2 = inventoryItem2.id;
		inventoryItem.id = 0L;
		inventoryItem2.id = 0L;
		while (true) {
			try {
				byte byte1 = 0;
				inventoryItem.save(byteBuffer, false);
				int int1 = byteBuffer.position();
				int int2 = byteBuffer.position();
				inventoryItem2.save(byteBuffer, false);
				int int3 = byteBuffer.position();
				if (int3 - int2 != int1 - byte1) {
					boolean boolean1 = false;
					return boolean1;
				}

				for (int int4 = 0; int4 < int1 - byte1; ++int4) {
					if (byteBuffer.get(byte1 + int4) != byteBuffer.get(int2 + int4)) {
						boolean boolean2 = false;
						return boolean2;
					}
				}

				return true;
			} catch (BufferOverflowException bufferOverflowException) {
				byteBuffer = ensureCapacity(byteBuffer);
				byteBuffer.clear();
				perThreadData.itemCompareBuffer = byteBuffer;
			} finally {
				inventoryItem.id = long1;
				inventoryItem2.id = long2;
			}
		}
	}

	public static ArrayList save(ByteBuffer byteBuffer, ArrayList arrayList, IsoGameCharacter gameCharacter) throws IOException {
		CompressIdenticalItems.PerThreadData perThreadData = (CompressIdenticalItems.PerThreadData)perThreadVars.get();
		CompressIdenticalItems.PerCallData perCallData = perThreadData.allocSaveVars();
		HashMap hashMap = perCallData.typeToItems;
		ArrayList arrayList2 = perCallData.types;
		try {
			int int1;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				String string = ((InventoryItem)arrayList.get(int1)).getFullType();
				if (!hashMap.containsKey(string)) {
					hashMap.put(string, perCallData.allocItemList());
					arrayList2.add(string);
				}

				((ArrayList)hashMap.get(string)).add(arrayList.get(int1));
			}

			int1 = byteBuffer.position();
			byteBuffer.putShort((short)0);
			int int2 = 0;
			int int3;
			for (int3 = 0; int3 < arrayList2.size(); ++int3) {
				ArrayList arrayList3 = (ArrayList)hashMap.get(arrayList2.get(int3));
				for (int int4 = 0; int4 < arrayList3.size(); ++int4) {
					InventoryItem inventoryItem = (InventoryItem)arrayList3.get(int4);
					perCallData.savedItems.add(inventoryItem);
					int int5 = 1;
					int int6 = int4 + 1;
					if (gameCharacter == null || !gameCharacter.isEquipped(inventoryItem)) {
						while (int4 + 1 < arrayList3.size() && areItemsIdentical(perThreadData, inventoryItem, (InventoryItem)arrayList3.get(int4 + 1))) {
							perCallData.savedItems.add(arrayList3.get(int4 + 1));
							++int4;
							++int5;
						}
					}

					byteBuffer.putShort((short)int5);
					inventoryItem.saveWithSize(byteBuffer, false);
					if (int5 > 1) {
						for (int int7 = int6; int7 <= int4; ++int7) {
							byteBuffer.putLong(((InventoryItem)arrayList3.get(int7)).id);
						}
					}

					++int2;
				}
			}

			int3 = byteBuffer.position();
			byteBuffer.position(int1);
			byteBuffer.putShort((short)int2);
			byteBuffer.position(int3);
		} finally {
			perCallData.next = perThreadData.saveVars;
			perThreadData.saveVars = perCallData;
		}

		return perCallData.savedItems;
	}

	public static ArrayList load(ByteBuffer byteBuffer, int int1, ArrayList arrayList, ArrayList arrayList2) throws IOException {
		CompressIdenticalItems.PerThreadData perThreadData = (CompressIdenticalItems.PerThreadData)perThreadVars.get();
		CompressIdenticalItems.PerCallData perCallData = perThreadData.allocSaveVars();
		if (arrayList != null) {
			arrayList.clear();
		}

		if (arrayList2 != null) {
			arrayList2.clear();
		}

		try {
			short short1 = byteBuffer.getShort();
			for (int int2 = 0; int2 < short1; ++int2) {
				short short2 = int1 >= 128 ? byteBuffer.getShort() : 1;
				int int3 = -1;
				if (int1 >= 54) {
					int3 = int1 >= 72 ? byteBuffer.getInt() : byteBuffer.getShort();
				}

				int int4 = byteBuffer.position();
				String string = GameWindow.ReadString(byteBuffer);
				byte byte1 = -1;
				if (int1 >= 70) {
					byte1 = byteBuffer.get();
					if (byte1 < 0) {
						throw new IOException("invalid item save-type " + byte1);
					}
				}

				if (GameWindow.DEBUG_SAVE) {
					DebugLog.log(string);
				}

				if (string.contains("..")) {
					string = string.replace("..", ".");
				}

				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
				int int5;
				int int6;
				if (inventoryItem == null && int3 != -1) {
					if (string.length() > 40) {
						string = "<unknown>";
					}

					DebugLog.log("Cannot load \"" + string + "\" item. Make sure all mods used in save are installed.");
					int6 = short2 > 1 ? (short2 - 1) * 8 : 0;
					byteBuffer.position(int4 + int3 + int6);
					for (int5 = 0; int5 < short2; ++int5) {
						if (arrayList2 != null) {
							arrayList2.add((Object)null);
						}

						perCallData.savedItems.add((Object)null);
					}
				} else {
					if (inventoryItem == null) {
						if (string.length() > 40) {
							string = "<unknown>";
						}

						Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Cannot load \"" + string + "\" item. Make sure all mods used in save are installed.", (Object)null);
						throw new RuntimeException("Cannot load " + string + " item");
					}

					if (byte1 != -1 && inventoryItem.getSaveType() != byte1) {
						DebugLog.log("ignoring \"" + string + "\" because type changed from " + byte1 + " to " + inventoryItem.getSaveType());
						int6 = short2 > 1 ? (short2 - 1) * 8 : 0;
						byteBuffer.position(int4 + int3 + int6);
						for (int5 = 0; int5 < short2; ++int5) {
							if (arrayList2 != null) {
								arrayList2.add((Object)null);
							}

							perCallData.savedItems.add((Object)null);
						}
					} else {
						Item item = ScriptManager.instance.FindItem(string);
						int5 = byteBuffer.position();
						int int7;
						for (int7 = 0; int7 < short2; ++int7) {
							if (int7 > 0) {
								inventoryItem = InventoryItemFactory.CreateItem(string);
								byteBuffer.position(int5);
							}

							inventoryItem.load(byteBuffer, int1, false);
							if (int3 != -1 && byteBuffer.position() != int4 + int3) {
								throw new IOException("item load() read " + (byteBuffer.position() - int4) + " but save() wrote " + int3 + " (" + string + ")");
							}

							if (item != null && item.getObsolete()) {
								if (arrayList2 != null) {
									arrayList2.add((Object)null);
								}

								perCallData.savedItems.add((Object)null);
							} else {
								if (arrayList != null) {
									arrayList.add(inventoryItem);
								}

								if (arrayList2 != null) {
									arrayList2.add(inventoryItem);
								}

								perCallData.savedItems.add(inventoryItem);
							}
						}

						if (int1 >= 128) {
							for (int7 = 1; int7 < short2; ++int7) {
								long long1 = byteBuffer.getLong();
								inventoryItem = (InventoryItem)perCallData.savedItems.get(perCallData.savedItems.size() - short2 + int7);
								if (inventoryItem != null) {
									inventoryItem.id = long1;
								}
							}
						}
					}
				}
			}
		} finally {
			perCallData.next = perThreadData.saveVars;
			perThreadData.saveVars = perCallData;
		}

		return perCallData.savedItems;
	}

	public static void save(ByteBuffer byteBuffer, InventoryItem inventoryItem) throws IOException {
		byteBuffer.putShort((short)1);
		byteBuffer.putShort((short)1);
		inventoryItem.saveWithSize(byteBuffer, false);
	}

	private static class PerThreadData {
		CompressIdenticalItems.PerCallData saveVars;
		ByteBuffer itemCompareBuffer;

		private PerThreadData() {
			this.itemCompareBuffer = ByteBuffer.allocate(1024);
		}

		CompressIdenticalItems.PerCallData allocSaveVars() {
			if (this.saveVars == null) {
				return new CompressIdenticalItems.PerCallData();
			} else {
				CompressIdenticalItems.PerCallData perCallData = this.saveVars;
				perCallData.reset();
				this.saveVars = this.saveVars.next;
				return perCallData;
			}
		}

		PerThreadData(Object object) {
			this();
		}
	}

	private static class PerCallData {
		final ArrayList types;
		final HashMap typeToItems;
		final ArrayDeque itemLists;
		final ArrayList savedItems;
		CompressIdenticalItems.PerCallData next;

		private PerCallData() {
			this.types = new ArrayList();
			this.typeToItems = new HashMap();
			this.itemLists = new ArrayDeque();
			this.savedItems = new ArrayList();
		}

		void reset() {
			for (int int1 = 0; int1 < this.types.size(); ++int1) {
				ArrayList arrayList = (ArrayList)this.typeToItems.get(this.types.get(int1));
				arrayList.clear();
				this.itemLists.push(arrayList);
			}

			this.types.clear();
			this.typeToItems.clear();
			this.savedItems.clear();
		}

		ArrayList allocItemList() {
			return this.itemLists.isEmpty() ? new ArrayList() : (ArrayList)this.itemLists.pop();
		}

		PerCallData(Object object) {
			this();
		}
	}
}
