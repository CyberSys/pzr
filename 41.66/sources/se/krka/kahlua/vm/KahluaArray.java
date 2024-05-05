package se.krka.kahlua.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class KahluaArray implements KahluaTable {
	private KahluaTable metatable;
	private Object[] data = new Object[16];
	private int len = 0;
	private boolean recalculateLen;

	public String getString(String string) {
		return (String)this.rawget(string);
	}

	public int size() {
		return this.len();
	}

	public int len() {
		if (this.recalculateLen) {
			int int1 = this.len - 1;
			for (Object[] objectArray = this.data; int1 >= 0 && objectArray[int1] == null; --int1) {
			}

			this.len = int1 + 1;
			this.recalculateLen = false;
		}

		return this.len;
	}

	public KahluaTableIterator iterator() {
		return new KahluaTableIterator(){
			private Double curKey;
			private Object curValue;
			private int index = 1;
			
			public int call(LuaCallFrame var1, int var2) {
				return this.advance() ? var1.push(this.getKey(), this.getValue()) : 0;
			}

			
			public boolean advance() {
				while (this.index <= KahluaArray.this.len()) {
					Object var1 = KahluaArray.this.rawget(this.index);
					if (var1 != null) {
						int var2 = this.index++;
						this.curKey = KahluaUtil.toDouble((long)var2);
						this.curValue = var1;
						return true;
					}

					++this.index;
				}

				return false;
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
		return this.len() == 0;
	}

	public void wipe() {
		for (int int1 = 0; int1 < this.data.length; ++int1) {
			this.data[int1] = null;
		}

		this.len = 0;
	}

	public Object rawget(int int1) {
		return int1 >= 1 && int1 <= this.len ? this.data[int1 - 1] : null;
	}

	public void rawset(int int1, Object object) {
		if (int1 <= 0) {
			KahluaUtil.fail("Index out of range: " + int1);
		}

		if (int1 >= this.len) {
			if (object == null) {
				if (int1 == this.len) {
					this.data[int1 - 1] = object;
					this.recalculateLen = true;
				}

				return;
			}

			if (this.data.length < int1) {
				int int2 = 2 * int1;
				int int3 = int2 - 1;
				Object[] objectArray = new Object[int3];
				System.arraycopy(this.data, 0, objectArray, 0, this.len);
				this.data = objectArray;
			}

			this.len = int1;
		}

		this.data[int1 - 1] = object;
	}

	private int getKeyIndex(Object object) {
		if (object instanceof Double) {
			Double Double1 = (Double)object;
			return Double1.intValue();
		} else {
			return -1;
		}
	}

	public Object rawget(Object object) {
		int int1 = this.getKeyIndex(object);
		return this.rawget(int1);
	}

	public void rawset(Object object, Object object2) {
		int int1 = this.getKeyIndex(object);
		if (int1 == -1) {
			KahluaUtil.fail("Invalid table key: " + object);
		}

		this.rawset(int1, object2);
	}

	public Object next(Object object) {
		int int1;
		if (object == null) {
			int1 = 0;
		} else {
			int1 = this.getKeyIndex(object);
			if (int1 <= 0 || int1 > this.len) {
				KahluaUtil.fail("invalid key to \'next\'");
				return null;
			}
		}

		while (int1 < this.len) {
			if (this.data[int1] != null) {
				return KahluaUtil.toDouble((long)(int1 + 1));
			}

			++int1;
		}

		return null;
	}

	public KahluaTable getMetatable() {
		return this.metatable;
	}

	public void setMetatable(KahluaTable kahluaTable) {
		this.metatable = kahluaTable;
	}

	public Class getJavaClass() {
		return null;
	}

	public void save(ByteBuffer byteBuffer) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void load(DataInputStream dataInputStream, int int1) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
