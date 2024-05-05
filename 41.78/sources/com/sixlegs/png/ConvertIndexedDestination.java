package com.sixlegs.png;

import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;


class ConvertIndexedDestination extends Destination {
	private final Destination dst;
	private final IndexColorModel srcColorModel;
	private final int srcSamples;
	private final int dstSamples;
	private final int sampleDiff;
	private final int[] row;

	public ConvertIndexedDestination(Destination destination, int int1, IndexColorModel indexColorModel, ComponentColorModel componentColorModel) {
		this.dst = destination;
		this.srcColorModel = indexColorModel;
		this.srcSamples = indexColorModel.getNumComponents();
		this.dstSamples = componentColorModel.getNumComponents();
		this.sampleDiff = this.srcSamples - this.dstSamples;
		this.row = new int[int1 * this.dstSamples + this.sampleDiff];
	}

	public void setPixels(int int1, int int2, int int3, int[] intArray) {
		int int4 = int3 - 1;
		for (int int5 = this.dstSamples * int4; int4 >= 0; int5 -= this.dstSamples) {
			this.srcColorModel.getComponents(intArray[int4], this.row, int5);
			--int4;
		}

		if (this.sampleDiff != 0) {
			System.arraycopy(this.row, this.sampleDiff, this.row, 0, this.dstSamples * int3);
		}

		this.dst.setPixels(int1, int2, int3, this.row);
	}

	public void setPixel(int int1, int int2, int[] intArray) {
		this.setPixels(int1, int2, 1, intArray);
	}

	public void getPixel(int int1, int int2, int[] intArray) {
		throw new UnsupportedOperationException("implement me");
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
