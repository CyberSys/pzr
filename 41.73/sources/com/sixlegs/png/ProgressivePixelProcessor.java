package com.sixlegs.png;


final class ProgressivePixelProcessor extends PixelProcessor {
	private final PixelProcessor pp;
	private final int imgWidth;
	private final int imgHeight;
	private final Destination dst;
	private final int samples;
	private final int[] pixels;

	public ProgressivePixelProcessor(Destination destination, PixelProcessor pixelProcessor, int int1, int int2) {
		this.pp = pixelProcessor;
		this.imgWidth = int1;
		this.imgHeight = int2;
		this.dst = destination;
		this.samples = destination.getRaster().getNumBands();
		this.pixels = new int[this.samples * 8];
	}

	public boolean process(int[] intArray, int int1, int int2, int int3, int int4, int int5) {
		this.pp.process(intArray, int1, int2, int3, int4, int5);
		int int6 = int2 - int1;
		if (int6 > 1 || int2 > 1) {
			int int7 = Math.min(int4 + int2, this.imgHeight);
			int int8 = 0;
			for (int int9 = int1; int8 < int5; ++int8) {
				this.dst.getPixel(int9, int4, this.pixels);
				int int10 = Math.min(int9 + int6, this.imgWidth);
				int int11 = int10 - int9;
				int int12 = this.samples;
				for (int int13 = int11 * this.samples; int12 < int13; ++int12) {
					this.pixels[int12] = this.pixels[int12 - this.samples];
				}

				for (int12 = int4; int12 < int7; ++int12) {
					this.dst.setPixels(int9, int12, int11, this.pixels);
				}

				int9 += int2;
			}
		}

		return true;
	}
}
