package se.krka.kahlua.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public interface KahluaTable {

	void setMetatable(KahluaTable kahluaTable);

	KahluaTable getMetatable();

	void rawset(Object object, Object object2);

	Object rawget(Object object);

	void rawset(int int1, Object object);

	Object rawget(int int1);

	int len();

	KahluaTableIterator iterator();

	boolean isEmpty();

	void wipe();

	int size();

	void save(ByteBuffer byteBuffer) throws IOException;

	void load(ByteBuffer byteBuffer, int int1) throws IOException;

	void save(DataOutputStream dataOutputStream) throws IOException;

	void load(DataInputStream dataInputStream, int int1) throws IOException;

	String getString(String string);
}
