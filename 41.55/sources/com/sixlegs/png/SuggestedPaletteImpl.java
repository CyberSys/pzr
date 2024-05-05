package com.sixlegs.png;


class SuggestedPaletteImpl implements SuggestedPalette {
	private final String name;
	private final int sampleDepth;
	private final byte[] bytes;
	private final int entrySize;
	private final int sampleCount;

	public SuggestedPaletteImpl(String string, int int1, byte[] byteArray) {
		this.name = string;
		this.sampleDepth = int1;
		this.bytes = byteArray;
		this.entrySize = int1 == 8 ? 6 : 10;
		this.sampleCount = byteArray.length / this.entrySize;
	}

	public String getName() {
		return this.name;
	}

	public int getSampleCount() {
		return this.sampleCount;
	}

	public int getSampleDepth() {
		return this.sampleDepth;
	}

	public void getSample(int int1, short[] shortArray) {
		int int2 = int1 * this.entrySize;
		int int3;
		int int4;
		if (this.sampleDepth == 8) {
			for (int3 = 0; int3 < 4; ++int3) {
				int4 = 255 & this.bytes[int2++];
				shortArray[int3] = (short)int4;
			}
		} else {
			for (int3 = 0; int3 < 4; ++int3) {
				int4 = 255 & this.bytes[int2++];
				int int5 = 255 & this.bytes[int2++];
				shortArray[int3] = (short)(int4 << 8 | int5);
			}
		}
	}

	public int getFrequency(int int1) {
		int int2 = (int1 + 1) * this.entrySize - 2;
		int int3 = 255 & this.bytes[int2];
		int int4 = 255 & this.bytes[int2 + 1];
		return int3 << 8 | int4;
	}
}
