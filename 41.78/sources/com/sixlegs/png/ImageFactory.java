package com.sixlegs.png;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


class ImageFactory {
	private static short[] GAMMA_TABLE_45455 = PngImage.createGammaTable(0.45455F, 2.2F, false);
	private static short[] GAMMA_TABLE_100000 = PngImage.createGammaTable(1.0F, 2.2F, false);

	public static BufferedImage createImage(PngImage pngImage, InputStream inputStream) throws IOException {
		return createImage(pngImage, inputStream, new Dimension(pngImage.getWidth(), pngImage.getHeight()));
	}

	public static BufferedImage createImage(PngImage pngImage, InputStream inputStream, Dimension dimension) throws IOException {
		PngConfig pngConfig = pngImage.getConfig();
		int int1 = dimension.width;
		int int2 = dimension.height;
		int int3 = pngImage.getBitDepth();
		int int4 = pngImage.getSamples();
		boolean boolean1 = pngImage.isInterlaced();
		boolean boolean2 = isIndexed(pngImage);
		boolean boolean3 = boolean2 && pngConfig.getConvertIndexed();
		short[] shortArray = pngConfig.getGammaCorrect() ? getGammaTable(pngImage) : null;
		ColorModel colorModel = createColorModel(pngImage, shortArray, boolean3);
		int int5 = int1;
		int int6 = int2;
		Rectangle rectangle = pngConfig.getSourceRegion();
		if (rectangle != null) {
			if (!(new Rectangle(int1, int2)).contains(rectangle)) {
				throw new IllegalStateException("Source region " + rectangle + " falls outside of " + int1 + "x" + int2 + " image");
			}

			int5 = rectangle.width;
			int6 = rectangle.height;
		}

		int int7 = pngConfig.getSourceXSubsampling();
		int int8 = pngConfig.getSourceYSubsampling();
		Object object;
		int int9;
		if (int7 == 1 && int8 == 1) {
			object = new RasterDestination(colorModel.createCompatibleWritableRaster(int5, int6), int1);
		} else {
			int int10 = pngConfig.getSubsamplingXOffset();
			int int11 = pngConfig.getSubsamplingYOffset();
			int int12 = calcSubsamplingSize(int5, int7, int10, 'X');
			int9 = calcSubsamplingSize(int6, int8, int11, 'Y');
			WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(int12, int9);
			object = new SubsamplingDestination(writableRaster, int1, int7, int8, int10, int11);
		}

		if (rectangle != null) {
			object = new SourceRegionDestination((Destination)object, rectangle);
		}

		BufferedImage bufferedImage = new BufferedImage(colorModel, ((Destination)object).getRaster(), false, (Hashtable)null);
		Object object2 = null;
		if (!boolean2) {
			int[] intArray = (int[])pngImage.getProperty("transparency", int[].class, false);
			int9 = int3 == 16 && pngConfig.getReduce16() ? 8 : 0;
			if (int9 != 0 || intArray != null || shortArray != null) {
				if (shortArray == null) {
					shortArray = getIdentityTable(int3 - int9);
				}

				if (intArray != null) {
					object2 = new TransGammaPixelProcessor((Destination)object, shortArray, intArray, int9);
				} else {
					object2 = new GammaPixelProcessor((Destination)object, shortArray, int9);
				}
			}
		}

		if (boolean3) {
			IndexColorModel indexColorModel = (IndexColorModel)createColorModel(pngImage, shortArray, false);
			object = new ConvertIndexedDestination((Destination)object, int1, indexColorModel, (ComponentColorModel)colorModel);
		}

		if (object2 == null) {
			object2 = new BasicPixelProcessor((Destination)object, int4);
		}

		if (pngConfig.getProgressive() && boolean1 && !boolean3) {
			object2 = new ProgressivePixelProcessor((Destination)object, (PixelProcessor)object2, int1, int2);
		}

		ProgressUpdater progressUpdater = new ProgressUpdater(pngImage, bufferedImage, (PixelProcessor)object2);
		InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream, new Inflater(), 4096);
		Defilterer defilterer = new Defilterer(inflaterInputStream, int3, int4, int1, progressUpdater);
		boolean boolean4;
		if (boolean1) {
			if (defilterer.defilter(0, 0, 8, 8, (int1 + 7) / 8, (int2 + 7) / 8) && pngImage.handlePass(bufferedImage, 0) && defilterer.defilter(4, 0, 8, 8, (int1 + 3) / 8, (int2 + 7) / 8) && pngImage.handlePass(bufferedImage, 1) && defilterer.defilter(0, 4, 4, 8, (int1 + 3) / 4, (int2 + 3) / 8) && pngImage.handlePass(bufferedImage, 2) && defilterer.defilter(2, 0, 4, 4, (int1 + 1) / 4, (int2 + 3) / 4) && pngImage.handlePass(bufferedImage, 3) && defilterer.defilter(0, 2, 2, 4, (int1 + 1) / 2, (int2 + 1) / 4) && pngImage.handlePass(bufferedImage, 4) && defilterer.defilter(1, 0, 2, 2, int1 / 2, (int2 + 1) / 2) && pngImage.handlePass(bufferedImage, 5) && defilterer.defilter(0, 1, 1, 2, int1, int2 / 2) && pngImage.handlePass(bufferedImage, 6)) {
				boolean4 = true;
			} else {
				boolean4 = false;
			}
		} else if (defilterer.defilter(0, 0, 1, 1, int1, int2) && pngImage.handlePass(bufferedImage, 0)) {
			boolean4 = true;
		} else {
			boolean4 = false;
		}

		((Destination)object).done();
		return bufferedImage;
	}

	private static short[] getGammaTable(PngImage pngImage) {
		PngConfig pngConfig = pngImage.getConfig();
		if ((pngImage.getBitDepth() != 16 || pngConfig.getReduce16()) && pngConfig.getDisplayExponent() == 2.2F) {
			float float1 = pngImage.getGamma();
			if (float1 == 0.45455F) {
				return GAMMA_TABLE_45455;
			}

			if (float1 == 1.0F) {
				return GAMMA_TABLE_100000;
			}
		}

		return pngImage.getGammaTable();
	}

	private static int calcSubsamplingSize(int int1, int int2, int int3, char char1) {
		int int4 = (int1 - int3 + int2 - 1) / int2;
		if (int4 == 0) {
			throw new IllegalStateException("Source " + char1 + " subsampling " + int2 + ", offset " + int3 + " is invalid for image dimension " + int1);
		} else {
			return int4;
		}
	}

	private static boolean isIndexed(PngImage pngImage) {
		int int1 = pngImage.getColorType();
		return int1 == 3 || int1 == 0 && pngImage.getBitDepth() < 16;
	}

	private static ColorModel createColorModel(PngImage pngImage, short[] shortArray, boolean boolean1) throws PngException {
		Map map = pngImage.getProperties();
		int int1 = pngImage.getColorType();
		int int2 = pngImage.getBitDepth();
		int int3 = int2 == 16 && pngImage.getConfig().getReduce16() ? 8 : int2;
		if (isIndexed(pngImage) && !boolean1) {
			byte[] byteArray;
			int int4;
			byte[] byteArray2;
			byte[] byteArray3;
			byte[] byteArray4;
			int int5;
			if (int1 == 3) {
				byteArray = (byte[])pngImage.getProperty("palette", byte[].class, true);
				int4 = byteArray.length / 3;
				byteArray2 = new byte[int4];
				byteArray3 = new byte[int4];
				byteArray4 = new byte[int4];
				int int6 = 0;
				for (int int7 = 0; int6 < int4; ++int6) {
					byteArray2[int6] = byteArray[int7++];
					byteArray3[int6] = byteArray[int7++];
					byteArray4[int6] = byteArray[int7++];
				}

				applyGamma(byteArray2, shortArray);
				applyGamma(byteArray3, shortArray);
				applyGamma(byteArray4, shortArray);
			} else {
				int5 = 1 << int2;
				byteArray2 = byteArray3 = byteArray4 = new byte[int5];
				for (int4 = 0; int4 < int5; ++int4) {
					byteArray2[int4] = (byte)(int4 * 255 / (int5 - 1));
				}

				applyGamma(byteArray2, shortArray);
			}

			if (map.containsKey("palette_alpha")) {
				byteArray = (byte[])pngImage.getProperty("palette_alpha", byte[].class, true);
				byte[] byteArray5 = new byte[byteArray2.length];
				Arrays.fill(byteArray5, byteArray.length, byteArray2.length, (byte)-1);
				System.arraycopy(byteArray, 0, byteArray5, 0, byteArray.length);
				return new IndexColorModel(int3, byteArray2.length, byteArray2, byteArray3, byteArray4, byteArray5);
			} else {
				int5 = -1;
				if (map.containsKey("transparency")) {
					int5 = ((int[])pngImage.getProperty("transparency", int[].class, true))[0];
				}

				return new IndexColorModel(int3, byteArray2.length, byteArray2, byteArray3, byteArray4, int5);
			}
		} else {
			int int8 = int3 == 16 ? 1 : 0;
			int int9 = int1 != 0 && int1 != 4 ? 1000 : 1003;
			int int10 = pngImage.getTransparency();
			return new ComponentColorModel(ColorSpace.getInstance(int9), (int[])null, int10 != 1, false, int10, int8);
		}
	}

	private static void applyGamma(byte[] byteArray, short[] shortArray) {
		if (shortArray != null) {
			for (int int1 = 0; int1 < byteArray.length; ++int1) {
				byteArray[int1] = (byte)shortArray[255 & byteArray[int1]];
			}
		}
	}

	private static short[] getIdentityTable(int int1) {
		int int2 = 1 << int1;
		short[] shortArray = new short[int2];
		for (int int3 = 0; int3 < int2; ++int3) {
			shortArray[int3] = (short)int3;
		}

		return shortArray;
	}
}
