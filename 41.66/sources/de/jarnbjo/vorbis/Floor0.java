package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;


class Floor0 extends Floor {
	private int order;
	private int rate;
	private int barkMapSize;
	private int amplitudeBits;
	private int amplitudeOffset;
	private int[] bookList;

	protected Floor0(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
		this.order = bitInputStream.getInt(8);
		this.rate = bitInputStream.getInt(16);
		this.barkMapSize = bitInputStream.getInt(16);
		this.amplitudeBits = bitInputStream.getInt(6);
		this.amplitudeOffset = bitInputStream.getInt(8);
		int int1 = bitInputStream.getInt(4) + 1;
		this.bookList = new int[int1];
		for (int int2 = 0; int2 < this.bookList.length; ++int2) {
			this.bookList[int2] = bitInputStream.getInt(8);
			if (this.bookList[int2] > setupHeader.getCodeBooks().length) {
				throw new VorbisFormatException("A floor0_book_list entry is higher than the code book count.");
			}
		}
	}

	protected int getType() {
		return 0;
	}

	protected Floor decodeFloor(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
		throw new UnsupportedOperationException();
	}

	protected void computeFloor(float[] floatArray) {
		throw new UnsupportedOperationException();
	}
}
