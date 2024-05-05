package com.sixlegs.png;

import java.awt.Rectangle;
import java.awt.image.WritableRaster;


final class SourceRegionDestination extends Destination {
	private final Destination dst;
	private final int xoff;
	private final int yoff;
	private final int xlen;
	private final int ylen;
	private final int samples;

	public SourceRegionDestination(Destination destination, Rectangle rectangle) {
		this.dst = destination;
		this.xoff = rectangle.x;
		this.yoff = rectangle.y;
		this.xlen = rectangle.width;
		this.ylen = rectangle.height;
		this.samples = destination.getRaster().getNumBands();
	}

	public void setPixels(int int1, int int2, int int3, int[] intArray) {
		if (int2 >= this.yoff && int2 < this.yoff + this.ylen) {
			int int4 = Math.max(int1, this.xoff);
			int int5 = Math.min(int1 + int3, this.xoff + this.xlen) - int4;
			if (int5 > 0) {
				if (int4 > int1) {
					System.arraycopy(intArray, int4 * this.samples, intArray, 0, int5 * this.samples);
				}

				this.dst.setPixels(int4 - this.xoff, int2 - this.yoff, int5, intArray);
			}
		}
	}

	public void setPixel(int int1, int int2, int[] intArray) {
		int1 -= this.xoff;
		int2 -= this.yoff;
		if (int1 >= 0 && int2 >= 0 && int1 < this.xlen && int2 < this.ylen) {
			this.dst.setPixel(int1, int2, intArray);
		}
	}

	public void getPixel(int int1, int int2, int[] intArray) {
		throw new UnsupportedOperationException();
	}

	public WritableRaster getRaster() {
		return this.dst.getRaster();
	}

	public int getSourceWidth() {
		return this.dst.getSourceWidth();
	}

	public void done() {
		this.dst.done();
	}
}
