package com.sixlegs.png;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


class RegisteredChunks {
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final String US_ASCII = "US-ASCII";
	private static final String UTF_8 = "UTF-8";

	public static boolean read(int int1, DataInput dataInput, int int2, PngImage pngImage) throws IOException {
		Map map = pngImage.getProperties();
		switch (int1) {
		case 1229278788: 
			checkLength(1229278788, int2, 0);
			break;
		
		case 1229472850: 
			read_IHDR(dataInput, int2, map);
			break;
		
		case 1347179589: 
			read_PLTE(dataInput, int2, map, pngImage);
			break;
		
		case 1649100612: 
			read_bKGD(dataInput, int2, map, pngImage);
			break;
		
		case 1665684045: 
			read_cHRM(dataInput, int2, map);
			break;
		
		case 1732332865: 
			read_gAMA(dataInput, int2, map);
			break;
		
		case 1732855399: 
			read_gIFg(dataInput, int2, map);
			break;
		
		case 1749635924: 
			read_hIST(dataInput, int2, map, pngImage);
			break;
		
		case 1766015824: 
			read_iCCP(dataInput, int2, map);
			break;
		
		case 1767135348: 
		
		case 1950701684: 
		
		case 2052348020: 
			readText(int1, dataInput, int2, map, pngImage);
			break;
		
		case 1866876531: 
			read_oFFs(dataInput, int2, map);
			break;
		
		case 1883789683: 
			read_pHYs(dataInput, int2, map);
			break;
		
		case 1933723988: 
			read_sBIT(dataInput, int2, map, pngImage);
			break;
		
		case 1933787468: 
			read_sCAL(dataInput, int2, map);
			break;
		
		case 1934642260: 
			read_sPLT(dataInput, int2, map, pngImage);
			break;
		
		case 1934772034: 
			read_sRGB(dataInput, int2, map);
			break;
		
		case 1934902610: 
			read_sTER(dataInput, int2, map);
			break;
		
		case 1950960965: 
			read_tIME(dataInput, int2, map);
			break;
		
		case 1951551059: 
			read_tRNS(dataInput, int2, map, pngImage);
			break;
		
		default: 
			return false;
		
		}
		return true;
	}

	private static void read_IHDR(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1229472850, int1, 13);
		int int2 = dataInput.readInt();
		int int3 = dataInput.readInt();
		if (int2 > 0 && int3 > 0) {
			byte byte1 = dataInput.readByte();
			switch (byte1) {
			case 1: 
			
			case 2: 
			
			case 4: 
			
			case 8: 
			
			case 16: 
				Object object = null;
				int int4 = dataInput.readUnsignedByte();
				switch (int4) {
				case 0: 
				
				case 2: 
					break;
				
				case 1: 
				
				case 5: 
				
				default: 
					throw new PngException("Bad color type: " + int4, true);
				
				case 3: 
					if (byte1 == 16) {
						throw new PngException("Bad bit depth for color type " + int4 + ": " + byte1, true);
					}

					break;
				
				case 4: 
				
				case 6: 
					if (byte1 <= 4) {
						throw new PngException("Bad bit depth for color type " + int4 + ": " + byte1, true);
					}

				
				}

				int int5 = dataInput.readUnsignedByte();
				if (int5 != 0) {
					throw new PngException("Unrecognized compression method: " + int5, true);
				} else {
					int int6 = dataInput.readUnsignedByte();
					if (int6 != 0) {
						throw new PngException("Unrecognized filter method: " + int6, true);
					} else {
						int int7 = dataInput.readUnsignedByte();
						switch (int7) {
						case 0: 
						
						case 1: 
							map.put("width", Integers.valueOf(int2));
							map.put("height", Integers.valueOf(int3));
							map.put("bit_depth", Integers.valueOf(byte1));
							map.put("interlace", Integers.valueOf(int7));
							map.put("compression", Integers.valueOf(int5));
							map.put("filter", Integers.valueOf(int6));
							map.put("color_type", Integers.valueOf(int4));
							return;
						
						default: 
							throw new PngException("Unrecognized interlace method: " + int7, true);
						
						}
					}
				}

			
			default: 
				throw new PngException("Bad bit depth: " + byte1, true);
			
			}
		} else {
			throw new PngException("Bad image size: " + int2 + "x" + int3, true);
		}
	}

	private static void read_PLTE(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
		if (int1 == 0) {
			throw new PngException("PLTE chunk cannot be empty", true);
		} else if (int1 % 3 != 0) {
			throw new PngException("PLTE chunk length indivisible by 3: " + int1, true);
		} else {
			int int2 = int1 / 3;
			if (int2 > 256) {
				throw new PngException("Too many palette entries: " + int2, true);
			} else {
				switch (pngImage.getColorType()) {
				case 0: 
				
				case 4: 
					throw new PngException("PLTE chunk found in grayscale image", false);
				
				case 3: 
					if (int2 > 2 << pngImage.getBitDepth() - 1) {
						throw new PngException("Too many palette entries: " + int2, true);
					}

				
				case 1: 
				
				case 2: 
				
				default: 
					byte[] byteArray = new byte[int1];
					dataInput.readFully(byteArray);
					map.put("palette", byteArray);
				
				}
			}
		}
	}

	private static void read_tRNS(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
		switch (pngImage.getColorType()) {
		case 0: 
			checkLength(1951551059, int1, 2);
			map.put("transparency", new int[]{dataInput.readUnsignedShort()});
			break;
		
		case 1: 
		
		default: 
			throw new PngException("tRNS prohibited for color type " + pngImage.getColorType(), true);
		
		case 2: 
			checkLength(1951551059, int1, 6);
			map.put("transparency", new int[]{dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedShort()});
			break;
		
		case 3: 
			int int2 = ((byte[])pngImage.getProperty("palette", byte[].class, true)).length / 3;
			if (int1 > int2) {
				throw new PngException("Too many transparency palette entries (" + int1 + " > " + int2 + ")", true);
			}

			byte[] byteArray = new byte[int1];
			dataInput.readFully(byteArray);
			map.put("palette_alpha", byteArray);
		
		}
	}

	private static void read_bKGD(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
		int[] intArray;
		switch (pngImage.getColorType()) {
		case 0: 
		
		case 4: 
			checkLength(1649100612, int1, 2);
			intArray = new int[]{dataInput.readUnsignedShort()};
			break;
		
		case 1: 
		
		case 2: 
		
		default: 
			checkLength(1649100612, int1, 6);
			intArray = new int[]{dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedShort()};
			break;
		
		case 3: 
			checkLength(1649100612, int1, 1);
			intArray = new int[]{dataInput.readUnsignedByte()};
		
		}
		map.put("background_rgb", intArray);
	}

	private static void read_cHRM(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1665684045, int1, 32);
		float[] floatArray = new float[8];
		for (int int2 = 0; int2 < 8; ++int2) {
			floatArray[int2] = (float)dataInput.readInt() / 100000.0F;
		}

		if (!map.containsKey("chromaticity")) {
			map.put("chromaticity", floatArray);
		}
	}

	private static void read_gAMA(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1732332865, int1, 4);
		int int2 = dataInput.readInt();
		if (int2 == 0) {
			throw new PngException("Meaningless zero gAMA chunk value", false);
		} else {
			if (!map.containsKey("rendering_intent")) {
				map.put("gamma", new Float((float)int2 / 100000.0F));
			}
		}
	}

	private static void read_hIST(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
		int int2 = ((byte[])pngImage.getProperty("palette", byte[].class, true)).length / 3;
		checkLength(1749635924, int1, int2 * 2);
		int[] intArray = new int[int2];
		for (int int3 = 0; int3 < int2; ++int3) {
			intArray[int3] = dataInput.readUnsignedShort();
		}

		map.put("histogram", intArray);
	}

	private static void read_iCCP(DataInput dataInput, int int1, Map map) throws IOException {
		String string = readKeyword(dataInput, int1);
		byte[] byteArray = readCompressed(dataInput, int1 - string.length() - 1, true);
		map.put("icc_profile_name", string);
		map.put("icc_profile", byteArray);
	}

	private static void read_pHYs(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1883789683, int1, 9);
		int int2 = dataInput.readInt();
		int int3 = dataInput.readInt();
		int int4 = dataInput.readUnsignedByte();
		if (int4 != 0 && int4 != 1) {
			throw new PngException("Illegal pHYs chunk unit specifier: " + int4, false);
		} else {
			map.put("pixels_per_unit_x", Integers.valueOf(int2));
			map.put("pixels_per_unit_y", Integers.valueOf(int3));
			map.put("unit", Integers.valueOf(int4));
		}
	}

	private static void read_sBIT(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
		boolean boolean1 = pngImage.getColorType() == 3;
		int int2 = boolean1 ? 3 : pngImage.getSamples();
		checkLength(1933723988, int1, int2);
		int int3 = boolean1 ? 8 : pngImage.getBitDepth();
		byte[] byteArray = new byte[int2];
		for (int int4 = 0; int4 < int2; ++int4) {
			byte byte1 = dataInput.readByte();
			if (byte1 <= 0 || byte1 > int3) {
				throw new PngException("Illegal sBIT sample depth", false);
			}

			byteArray[int4] = byte1;
		}

		map.put("significant_bits", byteArray);
	}

	private static void read_sRGB(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1934772034, int1, 1);
		byte byte1 = dataInput.readByte();
		map.put("rendering_intent", Integers.valueOf(byte1));
		map.put("gamma", new Float(0.45455));
		map.put("chromaticity", new float[]{0.3127F, 0.329F, 0.64F, 0.33F, 0.3F, 0.6F, 0.15F, 0.06F});
	}

	private static void read_tIME(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1950960965, int1, 7);
		Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.set(dataInput.readUnsignedShort(), check(dataInput.readUnsignedByte(), 1, 12, "month") - 1, check(dataInput.readUnsignedByte(), 1, 31, "day"), check(dataInput.readUnsignedByte(), 0, 23, "hour"), check(dataInput.readUnsignedByte(), 0, 59, "minute"), check(dataInput.readUnsignedByte(), 0, 60, "second"));
		map.put("time", calendar.getTime());
	}

	private static int check(int int1, int int2, int int3, String string) throws PngException {
		if (int1 >= int2 && int1 <= int3) {
			return int1;
		} else {
			throw new PngException("tIME " + string + " value " + int1 + " is out of bounds (" + int2 + "-" + int3 + ")", false);
		}
	}

	private static void read_sPLT(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
		String string = readKeyword(dataInput, int1);
		byte byte1 = dataInput.readByte();
		if (byte1 != 8 && byte1 != 16) {
			throw new PngException("Sample depth must be 8 or 16", false);
		} else {
			int1 -= string.length() + 2;
			if (int1 % (byte1 == 8 ? 6 : 10) != 0) {
				throw new PngException("Incorrect sPLT data length for given sample depth", false);
			} else {
				byte[] byteArray = new byte[int1];
				dataInput.readFully(byteArray);
				Object object = (List)pngImage.getProperty("suggested_palettes", List.class, false);
				if (object == null) {
					map.put("suggested_palettes", object = new ArrayList());
				}

				Iterator iterator = ((List)object).iterator();
				do {
					if (!iterator.hasNext()) {
						((List)object).add(new SuggestedPaletteImpl(string, byte1, byteArray));
						return;
					}
				}		 while (!string.equals(((SuggestedPalette)iterator.next()).getName()));

				throw new PngException("Duplicate suggested palette name " + string, false);
			}
		}
	}

	private static void readText(int int1, DataInput dataInput, int int2, Map map, PngImage pngImage) throws IOException {
		byte[] byteArray = new byte[int2];
		dataInput.readFully(byteArray);
		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byteArray));
		String string = readKeyword(dataInputStream, int2);
		String string2 = "ISO-8859-1";
		boolean boolean1 = false;
		boolean boolean2 = true;
		String string3 = null;
		String string4 = null;
		switch (int1) {
		case 1767135348: 
			string2 = "UTF-8";
			byte byte1 = dataInputStream.readByte();
			byte byte2 = dataInputStream.readByte();
			if (byte1 == 1) {
				boolean1 = true;
				boolean2 = false;
				if (byte2 != 0) {
					throw new PngException("Unrecognized " + PngConstants.getChunkName(int1) + " compression method: " + byte2, false);
				}
			} else if (byte1 != 0) {
				throw new PngException("Illegal " + PngConstants.getChunkName(int1) + " compression flag: " + byte1, false);
			}

			string3 = readString(dataInputStream, dataInputStream.available(), "US-ASCII");
			string4 = readString(dataInputStream, dataInputStream.available(), "UTF-8");
		
		case 1950701684: 
		
		default: 
			break;
		
		case 2052348020: 
			boolean1 = true;
		
		}
		String string5;
		if (boolean1) {
			string5 = new String(readCompressed(dataInputStream, dataInputStream.available(), boolean2), string2);
		} else {
			string5 = new String(byteArray, byteArray.length - dataInputStream.available(), dataInputStream.available(), string2);
		}

		if (string5.indexOf(0) >= 0) {
			throw new PngException("Text value contains null", false);
		} else {
			Object object = (List)pngImage.getProperty("text_chunks", List.class, false);
			if (object == null) {
				map.put("text_chunks", object = new ArrayList());
			}

			((List)object).add(new TextChunkImpl(string, string5, string3, string4, int1));
		}
	}

	private static void read_gIFg(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1732855399, int1, 4);
		int int2 = dataInput.readUnsignedByte();
		int int3 = dataInput.readUnsignedByte();
		int int4 = dataInput.readUnsignedShort();
		map.put("gif_disposal_method", Integers.valueOf(int2));
		map.put("gif_user_input_flag", Integers.valueOf(int3));
		map.put("gif_delay_time", Integers.valueOf(int4));
	}

	private static void read_oFFs(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1866876531, int1, 9);
		int int2 = dataInput.readInt();
		int int3 = dataInput.readInt();
		byte byte1 = dataInput.readByte();
		if (byte1 != 0 && byte1 != 1) {
			throw new PngException("Illegal oFFs chunk unit specifier: " + byte1, false);
		} else {
			map.put("position_x", Integers.valueOf(int2));
			map.put("position_y", Integers.valueOf(int3));
			map.put("position_unit", Integers.valueOf(byte1));
		}
	}

	private static void read_sCAL(DataInput dataInput, int int1, Map map) throws IOException {
		byte[] byteArray = new byte[int1];
		dataInput.readFully(byteArray);
		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byteArray));
		byte byte1 = dataInputStream.readByte();
		if (byte1 != 1 && byte1 != 2) {
			throw new PngException("Illegal sCAL chunk unit specifier: " + byte1, false);
		} else {
			double double1 = readFloatingPoint(dataInputStream, dataInputStream.available());
			double double2 = readFloatingPoint(dataInputStream, dataInputStream.available());
			if (!(double1 <= 0.0) && !(double2 <= 0.0)) {
				map.put("scale_unit", Integers.valueOf(byte1));
				map.put("pixel_width", new Double(double1));
				map.put("pixel_height", new Double(double2));
			} else {
				throw new PngException("sCAL measurements must be >= 0", false);
			}
		}
	}

	private static void read_sTER(DataInput dataInput, int int1, Map map) throws IOException {
		checkLength(1934902610, int1, 1);
		byte byte1 = dataInput.readByte();
		switch (byte1) {
		case 0: 
		
		case 1: 
			map.put("stereo_mode", Integers.valueOf(byte1));
			return;
		
		default: 
			throw new PngException("Unknown sTER mode: " + byte1, false);
		
		}
	}

	public static void checkLength(int int1, int int2, int int3) throws PngException {
		if (int2 != int3) {
			throw new PngException("Bad " + PngConstants.getChunkName(int1) + " chunk length: " + int2 + " (expected " + int3 + ")", true);
		}
	}

	private static byte[] readCompressed(DataInput dataInput, int int1, boolean boolean1) throws IOException {
		if (boolean1) {
			byte byte1 = dataInput.readByte();
			if (byte1 != 0) {
				throw new PngException("Unrecognized compression method: " + byte1, false);
			}

			--int1;
		}

		byte[] byteArray = new byte[int1];
		dataInput.readFully(byteArray);
		byte[] byteArray2 = new byte[4096];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Inflater inflater = new Inflater();
		inflater.reset();
		inflater.setInput(byteArray, 0, int1);
		try {
			while (!inflater.needsInput()) {
				byteArrayOutputStream.write(byteArray2, 0, inflater.inflate(byteArray2));
			}
		} catch (DataFormatException dataFormatException) {
			throw new PngException("Error reading compressed data", dataFormatException, false);
		}

		return byteArrayOutputStream.toByteArray();
	}

	private static String readString(DataInput dataInput, int int1, String string) throws IOException {
		return new String(readToNull(dataInput, int1), string);
	}

	private static String readKeyword(DataInput dataInput, int int1) throws IOException {
		String string = readString(dataInput, int1, "ISO-8859-1");
		if (string.length() != 0 && string.length() <= 79) {
			return string;
		} else {
			throw new PngException("Invalid keyword length: " + string.length(), false);
		}
	}

	private static byte[] readToNull(DataInput dataInput, int int1) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = dataInput.readUnsignedByte();
			if (int3 == 0) {
				return byteArrayOutputStream.toByteArray();
			}

			byteArrayOutputStream.write(int3);
		}

		return byteArrayOutputStream.toByteArray();
	}

	private static double readFloatingPoint(DataInput dataInput, int int1) throws IOException {
		String string = readString(dataInput, int1, "US-ASCII");
		int int2 = Math.max(string.indexOf(101), string.indexOf(69));
		double double1 = Double.valueOf(string.substring(0, int2 < 0 ? string.length() : int2));
		if (int2 >= 0) {
			double1 *= Math.pow(10.0, Double.valueOf(string.substring(int2 + 1)));
		}

		return double1;
	}
}
