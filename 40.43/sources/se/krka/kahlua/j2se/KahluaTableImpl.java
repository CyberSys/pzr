package se.krka.kahlua.j2se;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.ui.UIManager;


public class KahluaTableImpl implements KahluaTable {
	public final Map delegate;
	private KahluaTable metatable;
	private KahluaTable reloadReplace;
	private static final byte SBYT_NO_SAVE = -1;
	private static final byte SBYT_STRING = 0;
	private static final byte SBYT_DOUBLE = 1;
	private static final byte SBYT_TABLE = 2;
	private static final byte SBYT_BOOLEAN = 3;

	public KahluaTableImpl(Map map) {
		this.delegate = map;
	}

	public void setMetatable(KahluaTable kahluaTable) {
		this.metatable = kahluaTable;
	}

	public KahluaTable getMetatable() {
		return this.metatable;
	}

	public int size() {
		return this.delegate.size();
	}

	public void rawset(Object object, Object object2) {
		if (this.reloadReplace != null) {
			this.reloadReplace.rawset(object, object2);
		}

		Object object3 = null;
		if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, object)) {
			object3 = this.rawget(object);
		}

		if (object2 == null) {
			if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, object) && object3 != null) {
				UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)LuaManager.thread.lastLine);
			}

			this.delegate.remove(object);
		} else {
			if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasDataBreakpoint(this, object) && !object2.equals(object3)) {
				int int1 = LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
				if (int1 < 0) {
					int1 = 0;
				}

				UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)(LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[int1] - 1));
			}

			this.delegate.put(object, object2);
		}
	}

	public Object rawget(Object object) {
		if (this.reloadReplace != null) {
			return this.reloadReplace.rawget(object);
		} else if (object == null) {
			return null;
		} else {
			if (Core.bDebug && LuaManager.thread != null && LuaManager.thread.hasReadDataBreakpoint(this, object)) {
				int int1 = LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().pc;
				if (int1 < 0) {
					int1 = 0;
				}

				UIManager.debugBreakpoint(LuaManager.thread.currentfile, (long)(LuaManager.GlobalObject.getCurrentCoroutine().currentCallFrame().closure.prototype.lines[int1] - 1));
			}

			return !this.delegate.containsKey(object) && this.metatable != null ? this.metatable.rawget(object) : this.delegate.get(object);
		}
	}

	public void rawset(int int1, Object object) {
		this.rawset(KahluaUtil.toDouble((long)int1), object);
	}

	public String rawgetStr(Object object) {
		return (String)this.rawget(object);
	}

	public int rawgetInt(Object object) {
		return this.rawget(object) != null ? ((Double)this.rawget(object)).intValue() : -1;
	}

	public boolean rawgetBool(Object object) {
		return this.rawget(object) != null ? (Boolean)this.rawget(object) : false;
	}

	public float rawgetFloat(Object object) {
		return this.rawget(object) != null ? ((Double)this.rawget(object)).floatValue() : -1.0F;
	}

	public Object rawget(int int1) {
		return this.rawget(KahluaUtil.toDouble((long)int1));
	}

	public int len() {
		return KahluaUtil.len(this, 0, 2 * this.delegate.size());
	}

	public KahluaTableIterator iterator() {
		final Object[] objectArray = this.delegate.isEmpty() ? null : this.delegate.keySet().toArray();
		return new KahluaTableIterator(){
			private Object curKey;
			private Object curValue;
			private int keyIndex;
			
			public int call(LuaCallFrame objectArrayx, int var2) {
				return this.advance() ? objectArrayx.push(this.getKey(), this.getValue()) : 0;
			}

			
			public boolean advance() {
				if (objectArray != null && this.keyIndex < objectArray.length) {
					this.curKey = objectArray[this.keyIndex];
					this.curValue = KahluaTableImpl.this.delegate.get(this.curKey);
					++this.keyIndex;
					return true;
				} else {
					this.curKey = null;
					this.curValue = null;
					return false;
				}
			}

			
			public Object getKey() {
				return this.curKey;
			}

			
			public Object getValue() {
				return this.curValue;
			}
		};
	}

	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	public void wipe() {
		this.delegate.clear();
	}

	public String toString() {
		return "table 0x" + System.identityHashCode(this);
	}

	public void save(ByteBuffer byteBuffer) {
		KahluaTableIterator kahluaTableIterator = this.iterator();
		int int1 = 0;
		while (kahluaTableIterator.advance()) {
			if (canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
				++int1;
			}
		}

		kahluaTableIterator = this.iterator();
		byteBuffer.putInt(int1);
		while (kahluaTableIterator.advance()) {
			byte byte1 = getKeyByte(kahluaTableIterator.getKey());
			byte byte2 = getValueByte(kahluaTableIterator.getValue());
			if (byte1 != -1 && byte2 != -1) {
				this.save(byteBuffer, byte1, kahluaTableIterator.getKey());
				this.save(byteBuffer, byte2, kahluaTableIterator.getValue());
			}
		}
	}

	private void save(ByteBuffer byteBuffer, byte byte1, Object object) throws RuntimeException {
		byteBuffer.put(byte1);
		if (byte1 == 0) {
			GameWindow.WriteString(byteBuffer, (String)object);
		} else if (byte1 == 1) {
			byteBuffer.putDouble((Double)object);
		} else if (byte1 == 3) {
			byteBuffer.put((byte)((Boolean)object ? 1 : 0));
		} else {
			if (byte1 != 2) {
				throw new RuntimeException("invalid lua table type " + byte1);
			}

			((KahluaTableImpl)object).save(byteBuffer);
		}
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		KahluaTableIterator kahluaTableIterator = this.iterator();
		int int1 = 0;
		while (kahluaTableIterator.advance()) {
			if (canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
				++int1;
			}
		}

		kahluaTableIterator = this.iterator();
		dataOutputStream.writeInt(int1);
		while (kahluaTableIterator.advance()) {
			byte byte1 = getKeyByte(kahluaTableIterator.getKey());
			byte byte2 = getValueByte(kahluaTableIterator.getValue());
			if (byte1 != -1 && byte2 != -1) {
				this.save(dataOutputStream, byte1, kahluaTableIterator.getKey());
				this.save(dataOutputStream, byte2, kahluaTableIterator.getValue());
			}
		}
	}

	private void save(DataOutputStream dataOutputStream, byte byte1, Object object) throws IOException, RuntimeException {
		dataOutputStream.writeByte(byte1);
		if (byte1 == 0) {
			GameWindow.WriteString(dataOutputStream, (String)object);
		} else if (byte1 == 1) {
			dataOutputStream.writeDouble((Double)object);
		} else if (byte1 == 3) {
			dataOutputStream.writeByte((Boolean)object ? 1 : 0);
		} else {
			if (byte1 != 2) {
				throw new RuntimeException("invalid lua table type " + byte1);
			}

			((KahluaTableImpl)object).save(dataOutputStream);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		int int2 = byteBuffer.getInt();
		this.wipe();
		int int3;
		byte byte1;
		if (int1 >= 25) {
			for (int3 = 0; int3 < int2; ++int3) {
				byte1 = byteBuffer.get();
				Object object = this.load(byteBuffer, int1, byte1);
				byte byte2 = byteBuffer.get();
				Object object2 = this.load(byteBuffer, int1, byte2);
				this.rawset(object, object2);
			}
		} else {
			for (int3 = 0; int3 < int2; ++int3) {
				byte1 = byteBuffer.get();
				String string = GameWindow.ReadString(byteBuffer);
				Object object3 = this.load(byteBuffer, int1, byte1);
				this.rawset(string, object3);
			}
		}
	}

	public Object load(ByteBuffer byteBuffer, int int1, byte byte1) throws RuntimeException {
		if (byte1 == 0) {
			return GameWindow.ReadString(byteBuffer);
		} else if (byte1 == 1) {
			return byteBuffer.getDouble();
		} else if (byte1 == 3) {
			return byteBuffer.get() == 1;
		} else if (byte1 == 2) {
			KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
			kahluaTableImpl.load(byteBuffer, int1);
			return kahluaTableImpl;
		} else {
			throw new RuntimeException("invalid lua table type " + byte1);
		}
	}

	public void load(DataInputStream dataInputStream, int int1) throws IOException {
		int int2 = dataInputStream.readInt();
		int int3;
		byte byte1;
		if (int1 >= 25) {
			for (int3 = 0; int3 < int2; ++int3) {
				byte1 = dataInputStream.readByte();
				Object object = this.load(dataInputStream, int1, byte1);
				byte byte2 = dataInputStream.readByte();
				Object object2 = this.load(dataInputStream, int1, byte2);
				this.rawset(object, object2);
			}
		} else {
			for (int3 = 0; int3 < int2; ++int3) {
				byte1 = dataInputStream.readByte();
				String string = GameWindow.ReadString(dataInputStream);
				Object object3 = this.load(dataInputStream, int1, byte1);
				this.rawset(string, object3);
			}
		}
	}

	public Object load(DataInputStream dataInputStream, int int1, byte byte1) throws IOException, RuntimeException {
		if (byte1 == 0) {
			return GameWindow.ReadString(dataInputStream);
		} else if (byte1 == 1) {
			return dataInputStream.readDouble();
		} else if (byte1 == 3) {
			return dataInputStream.readByte() == 1;
		} else if (byte1 == 2) {
			KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.platform.newTable();
			kahluaTableImpl.load(dataInputStream, int1);
			return kahluaTableImpl;
		} else {
			throw new RuntimeException("invalid lua table type " + byte1);
		}
	}

	public String getString(String string) {
		return (String)this.rawget(string);
	}

	public KahluaTableImpl getRewriteTable() {
		return (KahluaTableImpl)this.reloadReplace;
	}

	public void setRewriteTable(Object object) {
		for (KahluaTableImpl kahluaTableImpl = this; kahluaTableImpl != null; kahluaTableImpl = kahluaTableImpl.getRewriteTable()) {
		}

		this.reloadReplace = (KahluaTableImpl)object;
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
		} else {
			return (byte)(object instanceof KahluaTableImpl ? 2 : -1);
		}
	}

	public static boolean canSave(Object object, Object object2) {
		return getKeyByte(object) != -1 && getValueByte(object2) != -1;
	}
}
