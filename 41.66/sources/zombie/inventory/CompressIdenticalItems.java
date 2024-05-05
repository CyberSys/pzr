package zombie.inventory;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.InventoryContainer;


public final class CompressIdenticalItems {
	private static final int BLOCK_SIZE = 1024;
	private static final ThreadLocal perThreadVars = new ThreadLocal(){
    
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
		} else {
			ByteBuffer byteBuffer2;
			if (byteBuffer.capacity() - byteBuffer.position() < 1024) {
				byteBuffer2 = ensureCapacity((ByteBuffer)null, byteBuffer.position() + 1024);
				return byteBuffer2.put(byteBuffer.array(), 0, byteBuffer.position());
			} else {
				byteBuffer2 = ensureCapacity((ByteBuffer)null, byteBuffer.capacity() + 1024);
				return byteBuffer2.put(byteBuffer.array(), 0, byteBuffer.position());
			}
		}
	}

	private static boolean setCompareItem(CompressIdenticalItems.PerThreadData perThreadData, InventoryItem inventoryItem) throws IOException {
		ByteBuffer byteBuffer = perThreadData.itemCompareBuffer;
		byteBuffer.clear();
		int int1 = inventoryItem.id;
		inventoryItem.id = 0;
		try {
			while (true) {
				try {
					byteBuffer.putInt(0);
					inventoryItem.save(byteBuffer, false);
					int int2 = byteBuffer.position();
					byteBuffer.position(0);
					byteBuffer.putInt(int2);
					byteBuffer.position(int2);
					return true;
				} catch (BufferOverflowException bufferOverflowException) {
					byteBuffer = ensureCapacity(byteBuffer);
					byteBuffer.clear();
					perThreadData.itemCompareBuffer = byteBuffer;
				}
			}
		} finally {
			inventoryItem.id = int1;
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

		ByteBuffer byteBuffer = inventoryItem.getByteData();
		ByteBuffer byteBuffer2 = inventoryItem2.getByteData();
		if (byteBuffer != null) {
			assert byteBuffer.position() == 0;
			if (!byteBuffer.equals(byteBuffer2)) {
				return false;
			}
		} else if (byteBuffer2 != null) {
			return false;
		}

		ByteBuffer byteBuffer3 = null;
		int int1 = inventoryItem2.id;
		inventoryItem2.id = 0;
		while (true) {
			boolean boolean1;
			try {
				byteBuffer3 = perThreadData.itemCompareBuffer;
				byteBuffer3.position(0);
				int int2 = byteBuffer3.getInt();
				int int3 = byteBuffer3.position();
				byteBuffer3.position(int2);
				int int4 = byteBuffer3.position();
				inventoryItem2.save(byteBuffer3, false);
				int int5 = byteBuffer3.position();
				if (int5 - int4 == int2 - int3) {
					for (int int6 = 0; int6 < int2 - int3; ++int6) {
						if (byteBuffer3.get(int3 + int6) != byteBuffer3.get(int4 + int6)) {
							boolean boolean2 = false;
							return boolean2;
						}
					}

					return true;
				}

				boolean1 = false;
			} catch (BufferOverflowException bufferOverflowException) {
				byteBuffer3 = ensureCapacity(byteBuffer3);
				byteBuffer3.clear();
				perThreadData.itemCompareBuffer = byteBuffer3;
				setCompareItem(perThreadData, inventoryItem);
				continue;
			} finally {
				inventoryItem2.id = int1;
			}

			return boolean1;
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

				((ArrayList)hashMap.get(string)).add((InventoryItem)arrayList.get(int1));
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
						setCompareItem(perThreadData, inventoryItem);
						while (int4 + 1 < arrayList3.size() && areItemsIdentical(perThreadData, inventoryItem, (InventoryItem)arrayList3.get(int4 + 1))) {
							perCallData.savedItems.add((InventoryItem)arrayList3.get(int4 + 1));
							++int4;
							++int5;
						}
					}

					byteBuffer.putInt(int5);
					inventoryItem.saveWithSize(byteBuffer, false);
					if (int5 > 1) {
						for (int int7 = int6; int7 <= int4; ++int7) {
							byteBuffer.putInt(((InventoryItem)arrayList3.get(int7)).id);
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
				int int3 = 1;
				if (int1 >= 149) {
					int3 = byteBuffer.getInt();
				} else if (int1 >= 128) {
					int3 = byteBuffer.getShort();
				}

				int int4 = byteBuffer.position();
				InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, int1);
				int int5;
				int int6;
				if (inventoryItem == null) {
					int5 = int3 > 1 ? (int3 - 1) * 4 : 0;
					byteBuffer.position(byteBuffer.position() + int5);
					for (int6 = 0; int6 < int3; ++int6) {
						if (arrayList2 != null) {
							arrayList2.add((Object)null);
						}

						perCallData.savedItems.add((Object)null);
					}
				} else {
					for (int5 = 0; int5 < int3; ++int5) {
						if (int5 > 0) {
							byteBuffer.position(int4);
							inventoryItem = InventoryItem.loadItem(byteBuffer, int1);
						}

						if (arrayList != null) {
							arrayList.add(inventoryItem);
						}

						if (arrayList2 != null) {
							arrayList2.add(inventoryItem);
						}

						perCallData.savedItems.add(inventoryItem);
					}

					if (int1 >= 128) {
						for (int5 = 1; int5 < int3; ++int5) {
							int6 = byteBuffer.getInt();
							inventoryItem = (InventoryItem)perCallData.savedItems.get(perCallData.savedItems.size() - int3 + int5);
							if (inventoryItem != null) {
								inventoryItem.id = int6;
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
		byteBuffer.putInt(1);
		inventoryItem.saveWithSize(byteBuffer, false);
	}

	private static class PerThreadData {
		CompressIdenticalItems.PerCallData saveVars;
		ByteBuffer itemCompareBuffer = ByteBuffer.allocate(1024);

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
	}

	private static class PerCallData {
		final ArrayList types = new ArrayList();
		final HashMap typeToItems = new HashMap();
		final ArrayDeque itemLists = new ArrayDeque();
		final ArrayList savedItems = new ArrayList();
		CompressIdenticalItems.PerCallData next;

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
	}
}
