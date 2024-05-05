package com.sixlegs.png;


final class TransGammaPixelProcessor extends BasicPixelProcessor {
	private final short[] gammaTable;
	private final int[] trans;
	private final int shift;
	private final int max;
	private final int samplesNoAlpha;
	private final int[] temp;

	public TransGammaPixelProcessor(Destination destination, short[] shortArray, int[] intArray, int int1) {
		super(destination, destination.getRaster().getNumBands());
		this.gammaTable = shortArray;
		this.trans = intArray;
		this.shift = int1;
		this.max = shortArray.length - 1;
		this.samplesNoAlpha = this.samples - 1;
		this.temp = new int[this.samples * destination.getSourceWidth()];
	}

	public boolean process(int[] intArray, int int1, int int2, int int3, int int4, int int5) {
		int int6 = int5 * this.samplesNoAlpha;
		int int7 = 0;
		for (int int8 = 0; int7 < int6; int8 += this.samples) {
			boolean boolean1 = false;
			for (int int9 = 0; int9 < this.samplesNoAlpha; ++int9) {
				int int10 = intArray[int7 + int9];
				boolean1 = boolean1 || int10 != this.trans[int9];
				this.temp[int8 + int9] = '￿' & this.gammaTable[int10 >> this.shift];
			}

			this.temp[int8 + this.samplesNoAlpha] = boolean1 ? this.max : 0;
			int7 += this.samplesNoAlpha;
		}

		return super.process(this.temp, int1, int2, int3, int4, int5);
	}
}
