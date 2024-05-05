package de.jarnbjo.util.io;

import java.io.IOException;


public interface BitInputStream {
	int LITTLE_ENDIAN = 0;
	int BIG_ENDIAN = 1;

	boolean getBit() throws IOException;

	int getInt(int int1) throws IOException;

	int getSignedInt(int int1) throws IOException;

	int getInt(HuffmanNode huffmanNode) throws IOException;

	int readSignedRice(int int1) throws IOException;

	void readSignedRice(int int1, int[] intArray, int int2, int int3) throws IOException;

	long getLong(int int1) throws IOException;

	void align();

	void setEndian(int int1);
}
