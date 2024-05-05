package com.sixlegs.png;


class BasicPixelProcessor extends PixelProcessor {
	protected final Destination dst;
	protected final int samples;

	public BasicPixelProcessor(Destination destination, int int1) {
		this.dst = destination;
		this.samples = int1;
	}

	public boolean process(int[] intArray, int int1, int int2, int int3, int int4, int int5) {
		if (int2 == 1) {
			this.dst.setPixels(int1, int4, int5, intArray);
		} else {
			int int6 = int1;
			int int7 = 0;
			for (int int8 = this.samples * int5; int7 < int8; int7 += this.samples) {
				for (int int9 = 0; int9 < this.samples; ++int9) {
					intArray[int9] = intArray[int7 + int9];
				}

				this.dst.setPixel(int6, int4, intArray);
				int6 += int2;
			}
		}

		return true;
	}
}
