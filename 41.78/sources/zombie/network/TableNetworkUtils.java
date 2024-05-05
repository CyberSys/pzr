package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoDirections;


public final class TableNetworkUtils {
	private static final byte SBYT_NO_SAVE = -1;
	private static final byte SBYT_STRING = 0;
	private static final byte SBYT_DOUBLE = 1;
	private static final byte SBYT_TABLE = 2;
	private static final byte SBYT_BOOLEAN = 3;
	private static final byte SBYT_ITEM = 4;
	private static final byte SBYT_DIRECTION = 5;

	public static void save(KahluaTable kahluaTable, ByteBuffer byteBuffer) throws IOException {
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		int int1 = 0;
		while (kahluaTableIterator.advance()) {
			if (canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
				++int1;
			}
		}

		kahluaTableIterator = kahluaTable.iterator();
		byteBuffer.putInt(int1);
		while (kahluaTableIterator.advance()) {
			byte byte1 = getKeyByte(kahluaTableIterator.getKey());
			byte byte2 = getValueByte(kahluaTableIterator.getValue());
			if (byte1 != -1 && byte2 != -1) {
				save(byteBuffer, byte1, kahluaTableIterator.getKey());
				save(byteBuffer, byte2, kahluaTableIterator.getValue());
			}
		}
	}

	public static void saveSome(KahluaTable kahluaTable, ByteBuffer byteBuffer, HashSet hashSet) throws IOException {
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		int int1 = 0;
		while (kahluaTableIterator.advance()) {
			if (hashSet.contains(kahluaTableIterator.getKey()) && canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
				++int1;
			}
		}

		kahluaTableIterator = kahluaTable.iterator();
		byteBuffer.putInt(int1);
		while (kahluaTableIterator.advance()) {
			if (hashSet.contains(kahluaTableIterator.getKey())) {
				byte byte1 = getKeyByte(kahluaTableIterator.getKey());
				byte byte2 = getValueByte(kahluaTableIterator.getValue());
				if (byte1 != -1 && byte2 != -1) {
					save(byteBuffer, byte1, kahluaTableIterator.getKey());
					save(byteBuffer, byte2, kahluaTableIterator.getValue());
				}
			}
		}
	}

	private static void save(ByteBuffer byteBuffer, byte byte1, Object object) throws IOException, RuntimeException {
		byteBuffer.put(byte1);
		if (byte1 == 0) {
			GameWindow.WriteString(byteBuffer, (String)object);
		} else if (byte1 == 1) {
			byteBuffer.putDouble((Double)object);
		} else if (byte1 == 3) {
			byteBuffer.put((byte)((Boolean)object ? 1 : 0));
		} else if (byte1 == 2) {
			save((KahluaTable)object, byteBuffer);
		} else if (byte1 == 4) {
			((InventoryItem)object).saveWithSize(byteBuffer, false);
		} else {
			if (byte1 != 5) {
				throw new RuntimeException("invalid lua table type " + byte1);
			}

			byteBuffer.put((byte)((IsoDirections)object).index());
		}
	}

	public static void load(KahluaTable kahluaTable, ByteBuffer byteBuffer) throws IOException {
		int int1 = byteBuffer.getInt();
		kahluaTable.wipe();
		for (int int2 = 0; int2 < int1; ++int2) {
			byte byte1 = byteBuffer.get();
			Object object = load(byteBuffer, byte1);
			byte byte2 = byteBuffer.get();
			Object object2 = load(byteBuffer, byte2);
			kahluaTable.rawset(object, object2);
		}
	}

	public static Object load(ByteBuffer byteBuffer, byte byte1) throws IOException, RuntimeException {
		if (byte1 == 0) {
			return GameWindow.ReadString(byteBuffer);
		} else if (byte1 == 1) {
			return byteBuffer.getDouble();
		} else if (byte1 == 3) {
			return byteBuffer.get() == 1;
		} else if (byte1 == 2) {
			KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
			load(kahluaTableImpl, byteBuffer);
			return kahluaTableImpl;
		} else if (byte1 == 4) {
			InventoryItem inventoryItem = null;
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return inventoryItem;
		} else if (byte1 == 5) {
			return IsoDirections.fromIndex(byteBuffer.get());
		} else {
			throw new RuntimeException("invalid lua table type " + byte1);
		}
	}

	private static byte getKeyByte(Object object) {
		if (object instanceof String) {
			return 0;
		} else {
			return (byte)(object instanceof Double ? 1 : -1);
		}
	}

	private static byte getValueByte(Object object) {
		if (object instanceof String) {
			return 0;
		} else if (object instanceof Double) {
			return 1;
		} else if (object instanceof Boolean) {
			return 3;
		} else if (object instanceof KahluaTableImpl) {
			return 2;
		} else if (object instanceof InventoryItem) {
			return 4;
		} else {
			return (byte)(object instanceof IsoDirections ? 5 : -1);
		}
	}

	public static boolean canSave(Object object, Object object2) {
		return getKeyByte(object) != -1 && getValueByte(object2) != -1;
	}
}
