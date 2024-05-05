package com.sixlegs.png;

import java.awt.image.WritableRaster;


class RasterDestination extends Destination {
	protected final WritableRaster raster;
	protected final int sourceWidth;

	public RasterDestination(WritableRaster writableRaster, int int1) {
		this.raster = writableRaster;
		this.sourceWidth = int1;
	}

	public void setPixels(int int1, int int2, int int3, int[] intArray) {
		this.raster.setPixels(int1, int2, int3, 1, intArray);
	}

	public void setPixel(int int1, int int2, int[] intArray) {
		this.raster.setPixel(int1, int2, intArray);
	}

	public void getPixel(int int1, int int2, int[] intArray) {
		this.raster.getPixel(int1, int2, intArray);
	}

	public WritableRaster getRaster() {
		return this.raster;
	}

	public int getSourceWidth() {
		return this.sourceWidth;
	}

	public void done() {
	}
}
