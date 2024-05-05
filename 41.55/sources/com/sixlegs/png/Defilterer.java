package com.sixlegs.png;

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


class Defilterer {
	private final InputStream in;
	private final int width;
	private final int bitDepth;
	private final int samples;
	private final PixelProcessor pp;
	private final int bpp;
	private final int[] row;
	private static int[][] bandOffsets = new int[][]{null, {0}, {0, 1}, {0, 1, 2}, {0, 1, 2, 3}};

	public Defilterer(InputStream inputStream, int int1, int int2, int int3, PixelProcessor pixelProcessor) {
		this.in = inputStream;
		this.bitDepth = int1;
		this.samples = int2;
		this.width = int3;
		this.pp = pixelProcessor;
		this.bpp = Math.max(1, int1 * int2 >> 3);
		this.row = new int[int2 * int3];
	}

	public boolean defilter(int int1, int int2, int int3, int int4, int int5, int int6) throws IOException {
		if (int5 != 0 && int6 != 0) {
			int int7 = (this.bitDepth * this.samples * int5 + 7) / 8;
			boolean boolean1 = this.bitDepth == 16;
			WritableRaster writableRaster = createInputRaster(this.bitDepth, this.samples, this.width);
			DataBuffer dataBuffer = writableRaster.getDataBuffer();
			byte[] byteArray = boolean1 ? null : ((DataBufferByte)dataBuffer).getData();
			short[] shortArray = boolean1 ? ((DataBufferUShort)dataBuffer).getData() : null;
			int int8 = int7 + this.bpp;
			byte[] byteArray2 = new byte[int8];
			byte[] byteArray3 = new byte[int8];
			int int9 = 0;
			for (int int10 = int2; int9 < int6; int10 += int4) {
				int int11 = this.in.read();
				if (int11 == -1) {
					throw new EOFException("Unexpected end of image data");
				}

				readFully(this.in, byteArray3, this.bpp, int7);
				defilter(byteArray3, byteArray2, this.bpp, int11);
				if (boolean1) {
					int int12 = 0;
					for (int int13 = this.bpp; int13 < int8; int13 += 2) {
						shortArray[int12] = (short)(byteArray3[int13] << 8 | 255 & byteArray3[int13 + 1]);
						++int12;
					}
				} else {
					System.arraycopy(byteArray3, this.bpp, byteArray, 0, int7);
				}

				writableRaster.getPixels(0, 0, int5, 1, this.row);
				if (!this.pp.process(this.row, int1, int3, int4, int10, int5)) {
					return false;
				}

				byte[] byteArray4 = byteArray3;
				byteArray3 = byteArray2;
				byteArray2 = byteArray4;
				++int9;
			}

			return true;
		} else {
			return true;
		}
	}

	private static void defilter(byte[] byteArray, byte[] byteArray2, int int1, int int2) throws PngException {
		int int3 = byteArray.length;
		int int4;
		int int5;
		switch (int2) {
		case 0: 
			return;
		
		case 1: 
			int4 = int1;
			for (int5 = 0; int4 < int3; ++int5) {
				byteArray[int4] += byteArray[int5];
				++int4;
			}

			return;
		
		case 2: 
			for (int4 = int1; int4 < int3; ++int4) {
				byteArray[int4] += byteArray2[int4];
			}

			return;
		
		case 3: 
			int4 = int1;
			for (int5 = 0; int4 < int3; ++int5) {
				byteArray[int4] = (byte)(byteArray[int4] + ((255 & byteArray[int5]) + (255 & byteArray2[int4])) / 2);
				++int4;
			}

			return;
		
		case 4: 
			int4 = int1;
			for (int5 = 0; int4 < int3; ++int5) {
				byte byte1 = byteArray[int5];
				byte byte2 = byteArray2[int4];
				byte byte3 = byteArray2[int5];
				int int6 = 255 & byte1;
				int int7 = 255 & byte2;
				int int8 = 255 & byte3;
				int int9 = int6 + int7 - int8;
				int int10 = int9 - int6;
				if (int10 < 0) {
					int10 = -int10;
				}

				int int11 = int9 - int7;
				if (int11 < 0) {
					int11 = -int11;
				}

				int int12 = int9 - int8;
				if (int12 < 0) {
					int12 = -int12;
				}

				int int13;
				if (int10 <= int11 && int10 <= int12) {
					int13 = int6;
				} else if (int11 <= int12) {
					int13 = int7;
				} else {
					int13 = int8;
				}

				byteArray[int4] = (byte)(byteArray[int4] + int13);
				++int4;
			}

			return;
		
		default: 
			throw new PngException("Unrecognized filter type " + int2, true);
		
		}
	}

	private static WritableRaster createInputRaster(int int1, int int2, int int3) {
		int int4 = (int1 * int2 * int3 + 7) / 8;
		Point point = new Point(0, 0);
		DataBufferByte dataBufferByte;
		if (int1 < 8 && int2 == 1) {
			dataBufferByte = new DataBufferByte(int4);
			return Raster.createPackedRaster(dataBufferByte, int3, 1, int1, point);
		} else if (int1 <= 8) {
			dataBufferByte = new DataBufferByte(int4);
			return Raster.createInterleavedRaster(dataBufferByte, int3, 1, int4, int2, bandOffsets[int2], point);
		} else {
			DataBufferUShort dataBufferUShort = new DataBufferUShort(int4 / 2);
			return Raster.createInterleavedRaster(dataBufferUShort, int3, 1, int4 / 2, int2, bandOffsets[int2], point);
		}
	}

	private static void readFully(InputStream inputStream, byte[] byteArray, int int1, int int2) throws IOException {
		int int3;
		for (int int4 = 0; int4 < int2; int4 += int3) {
			int3 = inputStream.read(byteArray, int1 + int4, int2 - int4);
			if (int3 == -1) {
				throw new EOFException("Unexpected end of image data");
			}
		}
	}
}
