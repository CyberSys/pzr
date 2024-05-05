package com.sixlegs.png;

import java.awt.image.WritableRaster;


abstract class Destination {

	public abstract void setPixels(int int1, int int2, int int3, int[] intArray);

	public abstract void setPixel(int int1, int int2, int[] intArray);

	public abstract void getPixel(int int1, int int2, int[] intArray);

	public abstract WritableRaster getRaster();

	public abstract int getSourceWidth();

	public abstract void done();
}
