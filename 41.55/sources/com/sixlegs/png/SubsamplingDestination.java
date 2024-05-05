package com.sixlegs.png;

import java.awt.image.WritableRaster;


final class SubsamplingDestination extends RasterDestination {
	private final int xsub;
	private final int ysub;
	private final int xoff;
	private final int yoff;
	private final int[] singlePixel;

	public SubsamplingDestination(WritableRaster writableRaster, int int1, int int2, int int3, int int4, int int5) {
		super(writableRaster, int1);
		this.xsub = int2;
		this.ysub = int3;
		this.xoff = int4;
		this.yoff = int5;
		this.singlePixel = new int[writableRaster.getNumBands()];
	}

	public void setPixels(int int1, int int2, int int3, int[] intArray) {
		if ((int2 - this.yoff) % this.ysub == 0) {
			int int4 = (int1 - this.xoff) / this.xsub;
			int int5 = (int2 - this.yoff) / this.ysub;
			int int6 = int4 * this.xsub + this.xoff;
			if (int6 < int1) {
				++int4;
				int6 += this.xsub;
			}

			int int7 = this.raster.getNumBands();
			int int8 = int6 - int1;
			for (int int9 = int1 + int3; int8 < int9; int8 += this.xsub) {
				System.arraycopy(intArray, int8 * int7, this.singlePixel, 0, int7);
				super.setPixel(int4++, int5, this.singlePixel);
			}
		}
	}

	public void setPixel(int int1, int int2, int[] intArray) {
		int1 -= this.xoff;
		int2 -= this.yoff;
		if (int1 % this.xsub == 0 && int2 % this.ysub == 0) {
			super.setPixel(int1 / this.xsub, int2 / this.ysub, intArray);
		}
	}

	public void getPixel(int int1, int int2, int[] intArray) {
		throw new UnsupportedOperationException();
	}
}
