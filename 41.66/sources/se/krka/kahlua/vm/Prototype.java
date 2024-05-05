package se.krka.kahlua.vm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class Prototype {
	public int[] code;
	public Object[] constants;
	public Prototype[] prototypes;
	public int numParams;
	public boolean isVararg;
	public String name;
	public int[] lines;
	public int numUpvalues;
	public int maxStacksize;
	public String file;
	public String filename;
	public String[] locvars;
	public int[] locvarlines;

	public Prototype() {
	}

	public Prototype(DataInputStream dataInputStream, boolean boolean1, String string, int int1) throws IOException {
		this.name = readLuaString(dataInputStream, int1, boolean1);
		if (this.name == null) {
			this.name = string;
		}

		dataInputStream.readInt();
		dataInputStream.readInt();
		this.numUpvalues = dataInputStream.read();
		this.numParams = dataInputStream.read();
		int int2 = dataInputStream.read();
		this.isVararg = (int2 & 2) != 0;
		this.maxStacksize = dataInputStream.read();
		int int3 = toInt(dataInputStream.readInt(), boolean1);
		this.code = new int[int3];
		int int4;
		int int5;
		for (int4 = 0; int4 < int3; ++int4) {
			int5 = toInt(dataInputStream.readInt(), boolean1);
			this.code[int4] = int5;
		}

		int4 = toInt(dataInputStream.readInt(), boolean1);
		this.constants = new Object[int4];
		int int6;
		for (int5 = 0; int5 < int4; ++int5) {
			Object object = null;
			int6 = dataInputStream.read();
			switch (int6) {
			case 0: 
				break;
			
			case 1: 
				int int7 = dataInputStream.read();
				object = int7 == 0 ? Boolean.FALSE : Boolean.TRUE;
				break;
			
			case 2: 
			
			default: 
				throw new IOException("unknown constant type: " + int6);
			
			case 3: 
				long long1 = dataInputStream.readLong();
				if (boolean1) {
					long1 = rev(long1);
				}

				object = KahluaUtil.toDouble(Double.longBitsToDouble(long1));
				break;
			
			case 4: 
				object = readLuaString(dataInputStream, int1, boolean1);
			
			}

			this.constants[int5] = object;
		}

		int5 = toInt(dataInputStream.readInt(), boolean1);
		this.prototypes = new Prototype[int5];
		int int8;
		for (int8 = 0; int8 < int5; ++int8) {
			this.prototypes[int8] = new Prototype(dataInputStream, boolean1, this.name, int1);
		}

		int int9 = toInt(dataInputStream.readInt(), boolean1);
		this.lines = new int[int9];
		for (int8 = 0; int8 < int9; ++int8) {
			int6 = toInt(dataInputStream.readInt(), boolean1);
			this.lines[int8] = int6;
		}

		int9 = toInt(dataInputStream.readInt(), boolean1);
		for (int8 = 0; int8 < int9; ++int8) {
			readLuaString(dataInputStream, int1, boolean1);
			dataInputStream.readInt();
			dataInputStream.readInt();
		}

		int9 = toInt(dataInputStream.readInt(), boolean1);
		for (int8 = 0; int8 < int9; ++int8) {
			readLuaString(dataInputStream, int1, boolean1);
		}
	}

	public String toString() {
		return this.name;
	}

	private static String readLuaString(DataInputStream dataInputStream, int int1, boolean boolean1) throws IOException {
		long long1 = 0L;
		int int2;
		if (int1 == 4) {
			int2 = dataInputStream.readInt();
			long1 = (long)toInt(int2, boolean1);
		} else if (int1 == 8) {
			long1 = toLong(dataInputStream.readLong(), boolean1);
		} else {
			loadAssert(false, "Bad string size");
		}

		if (long1 == 0L) {
			return null;
		} else {
			--long1;
			loadAssert(long1 < 65536L, "Too long string:" + long1);
			int2 = (int)long1;
			byte[] byteArray = new byte[3 + int2];
			byteArray[0] = (byte)(int2 >> 8 & 255);
			byteArray[1] = (byte)(int2 & 255);
			dataInputStream.readFully(byteArray, 2, int2 + 1);
			loadAssert(byteArray[2 + int2] == 0, "String loading");
			DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream(byteArray));
			String string = dataInputStream2.readUTF();
			dataInputStream2.close();
			return string;
		}
	}

	public static int rev(int int1) {
		int int2 = int1 >>> 24 & 255;
		int int3 = int1 >>> 16 & 255;
		int int4 = int1 >>> 8 & 255;
		int int5 = int1 & 255;
		return int5 << 24 | int4 << 16 | int3 << 8 | int2;
	}

	public static long rev(long long1) {
		long long2 = long1 >>> 56 & 255L;
		long long3 = long1 >>> 48 & 255L;
		long long4 = long1 >>> 40 & 255L;
		long long5 = long1 >>> 32 & 255L;
		long long6 = long1 >>> 24 & 255L;
		long long7 = long1 >>> 16 & 255L;
		long long8 = long1 >>> 8 & 255L;
		long long9 = long1 & 255L;
		return long9 << 56 | long8 << 48 | long7 << 40 | long6 << 32 | long5 << 24 | long4 << 16 | long3 << 8 | long2;
	}

	public static int toInt(int int1, boolean boolean1) {
		return boolean1 ? rev(int1) : int1;
	}

	public static long toLong(long long1, boolean boolean1) {
		return boolean1 ? rev(long1) : long1;
	}

	public static LuaClosure loadByteCode(DataInputStream dataInputStream, KahluaTable kahluaTable) throws IOException {
		int int1 = dataInputStream.read();
		loadAssert(int1 == 27, "Signature 1");
		int1 = dataInputStream.read();
		loadAssert(int1 == 76, "Signature 2");
		int1 = dataInputStream.read();
		loadAssert(int1 == 117, "Signature 3");
		int1 = dataInputStream.read();
		loadAssert(int1 == 97, "Signature 4");
		int1 = dataInputStream.read();
		loadAssert(int1 == 81, "Version");
		int1 = dataInputStream.read();
		loadAssert(int1 == 0, "Format");
		boolean boolean1 = dataInputStream.read() == 1;
		int1 = dataInputStream.read();
		loadAssert(int1 == 4, "Size int");
		int int2 = dataInputStream.read();
		loadAssert(int2 == 4 || int2 == 8, "Size t");
		int1 = dataInputStream.read();
		loadAssert(int1 == 4, "Size instr");
		int1 = dataInputStream.read();
		loadAssert(int1 == 8, "Size number");
		int1 = dataInputStream.read();
		loadAssert(int1 == 0, "Integral");
		Prototype prototype = new Prototype(dataInputStream, boolean1, (String)null, int2);
		LuaClosure luaClosure = new LuaClosure(prototype, kahluaTable);
		return luaClosure;
	}

	private static void loadAssert(boolean boolean1, String string) throws IOException {
		if (!boolean1) {
			throw new IOException("Could not load bytecode:" + string);
		}
	}

	public static LuaClosure loadByteCode(InputStream inputStream, KahluaTable kahluaTable) throws IOException {
		if (!(inputStream instanceof DataInputStream)) {
			inputStream = new DataInputStream((InputStream)inputStream);
		}

		return loadByteCode((DataInputStream)inputStream, kahluaTable);
	}

	public void dump(OutputStream outputStream) throws IOException {
		DataOutputStream dataOutputStream;
		if (outputStream instanceof DataOutputStream) {
			dataOutputStream = (DataOutputStream)outputStream;
		} else {
			dataOutputStream = new DataOutputStream(outputStream);
		}

		dataOutputStream.write(27);
		dataOutputStream.write(76);
		dataOutputStream.write(117);
		dataOutputStream.write(97);
		dataOutputStream.write(81);
		dataOutputStream.write(0);
		dataOutputStream.write(0);
		dataOutputStream.write(4);
		dataOutputStream.write(4);
		dataOutputStream.write(4);
		dataOutputStream.write(8);
		dataOutputStream.write(0);
		this.dumpPrototype(dataOutputStream);
	}

	private void dumpPrototype(DataOutputStream dataOutputStream) throws IOException {
		dumpString(this.name, dataOutputStream);
		dataOutputStream.writeInt(0);
		dataOutputStream.writeInt(0);
		dataOutputStream.write(this.numUpvalues);
		dataOutputStream.write(this.numParams);
		dataOutputStream.write(this.isVararg ? 2 : 0);
		dataOutputStream.write(this.maxStacksize);
		int int1 = this.code.length;
		dataOutputStream.writeInt(int1);
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			dataOutputStream.writeInt(this.code[int2]);
		}

		int2 = this.constants.length;
		dataOutputStream.writeInt(int2);
		int int3;
		for (int3 = 0; int3 < int2; ++int3) {
			Object object = this.constants[int3];
			if (object == null) {
				dataOutputStream.write(0);
			} else if (object instanceof Boolean) {
				dataOutputStream.write(1);
				dataOutputStream.write((Boolean)object ? 1 : 0);
			} else if (object instanceof Double) {
				dataOutputStream.write(3);
				Double Double1 = (Double)object;
				dataOutputStream.writeLong(Double.doubleToLongBits(Double1));
			} else {
				if (!(object instanceof String)) {
					throw new RuntimeException("Bad type in constant pool");
				}

				dataOutputStream.write(4);
				dumpString((String)object, dataOutputStream);
			}
		}

		int3 = this.prototypes.length;
		dataOutputStream.writeInt(int3);
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			this.prototypes[int4].dumpPrototype(dataOutputStream);
		}

		int4 = this.lines.length;
		dataOutputStream.writeInt(int4);
		for (int int5 = 0; int5 < int4; ++int5) {
			dataOutputStream.writeInt(this.lines[int5]);
		}

		dataOutputStream.writeInt(0);
		dataOutputStream.writeInt(0);
	}

	private static void dumpString(String string, DataOutputStream dataOutputStream) throws IOException {
		if (string == null) {
			dataOutputStream.writeShort(0);
		} else {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			(new DataOutputStream(byteArrayOutputStream)).writeUTF(string);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			int int1 = byteArray.length - 2;
			dataOutputStream.writeInt(int1 + 1);
			dataOutputStream.write(byteArray, 2, int1);
			dataOutputStream.write(0);
		}
	}
}
