package com.sixlegs.png;


final class GammaPixelProcessor extends BasicPixelProcessor {
	private final short[] gammaTable;
	private final int shift;
	private final int samplesNoAlpha;
	private final boolean hasAlpha;
	private final boolean shiftAlpha;

	public GammaPixelProcessor(Destination destination, short[] shortArray, int int1) {
		super(destination, destination.getRaster().getNumBands());
		this.gammaTable = shortArray;
		this.shift = int1;
		this.hasAlpha = this.samples % 2 == 0;
		this.samplesNoAlpha = this.hasAlpha ? this.samples - 1 : this.samples;
		this.shiftAlpha = this.hasAlpha && int1 > 0;
	}

	public boolean process(int[] intArray, int int1, int int2, int int3, int int4, int int5) {
		int int6 = this.samples * int5;
		int int7;
		for (int7 = 0; int7 < this.samplesNoAlpha; ++int7) {
			for (int int8 = int7; int8 < int6; int8 += this.samples) {
				intArray[int8] = '￿' & this.gammaTable[intArray[int8] >> this.shift];
			}
		}

		if (this.shiftAlpha) {
			for (int7 = this.samplesNoAlpha; int7 < int6; int7 += this.samples) {
				intArray[int7] >>= this.shift;
			}
		}

		return super.process(intArray, int1, int2, int3, int4, int5);
	}
}
