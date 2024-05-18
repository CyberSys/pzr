package zombie.core.textures;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


public class PNGDecoder {
	private static final byte[] SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
	private static final int IHDR = 1229472850;
	private static final int PLTE = 1347179589;
	private static final int tRNS = 1951551059;
	private static final int IDAT = 1229209940;
	private static final int IEND = 1229278788;
	private static final byte COLOR_GREYSCALE = 0;
	private static final byte COLOR_TRUECOLOR = 2;
	private static final byte COLOR_INDEXED = 3;
	private static final byte COLOR_GREYALPHA = 4;
	private static final byte COLOR_TRUEALPHA = 6;
	private final InputStream input;
	private final CRC32 crc;
	private final byte[] buffer;
	private int chunkLength;
	private int chunkType;
	private int chunkRemaining;
	private int width;
	private int height;
	private int bitdepth;
	private int colorType;
	private int bytesPerPixel;
	private byte[] palette;
	private byte[] paletteA;
	private byte[] transPixel;
	int maskM = 0;
	public int maskID = 0;
	public boolean[] mask;
	public boolean bDoMask = false;
	public long readTotal = 0L;

	public PNGDecoder(InputStream inputStream, boolean boolean1) throws IOException {
		this.input = inputStream;
		this.crc = new CRC32();
		this.buffer = new byte[4096];
		this.bDoMask = boolean1;
		this.readFully(this.buffer, 0, SIGNATURE.length);
		if (!checkSignature(this.buffer)) {
			throw new IOException("Not a valid PNG file");
		} else {
			this.openChunk(1229472850);
			this.readIHDR();
			this.closeChunk();
			while (true) {
				this.openChunk();
				switch (this.chunkType) {
				case 1229209940: 
					if (this.colorType == 3 && this.palette == null) {
						throw new IOException("Missing PLTE chunk");
					}

					if (boolean1) {
						this.mask = new boolean[this.width * this.height];
					}

					return;
				
				case 1347179589: 
					this.readPLTE();
					break;
				
				case 1951551059: 
					this.readtRNS();
				
				}

				this.closeChunk();
			}
		}
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public boolean hasAlphaChannel() {
		return this.colorType == 6 || this.colorType == 4;
	}

	public boolean hasAlpha() {
		return this.hasAlphaChannel() || this.paletteA != null || this.transPixel != null;
	}

	public boolean isRGB() {
		return this.colorType == 6 || this.colorType == 2 || this.colorType == 3;
	}

	public void overwriteTRNS(byte byte1, byte byte2, byte byte3) {
		if (this.hasAlphaChannel()) {
			throw new UnsupportedOperationException("image has an alpha channel");
		} else {
			byte[] byteArray = this.palette;
			if (byteArray == null) {
				this.transPixel = new byte[]{0, byte1, 0, byte2, 0, byte3};
			} else {
				this.paletteA = new byte[byteArray.length / 3];
				int int1 = 0;
				for (int int2 = 0; int1 < byteArray.length; ++int2) {
					if (byteArray[int1] != byte1 || byteArray[int1 + 1] != byte2 || byteArray[int1 + 2] != byte3) {
						this.paletteA[int2] = -1;
					}

					int1 += 3;
				}
			}
		}
	}

	public PNGDecoder.Format decideTextureFormat(PNGDecoder.Format format) {
		switch (this.colorType) {
		case 0: 
			switch (format) {
			case LUMINANCE: 
			
			case ALPHA: 
				return format;
			
			default: 
				return PNGDecoder.Format.LUMINANCE;
			
			}

		
		case 1: 
		
		case 5: 
		
		default: 
			throw new UnsupportedOperationException("Not yet implemented");
		
		case 2: 
			switch (format) {
			case ABGR: 
			
			case RGBA: 
			
			case BGRA: 
			
			case RGB: 
				return format;
			
			default: 
				return PNGDecoder.Format.RGB;
			
			}

		
		case 3: 
			switch (format) {
			case ABGR: 
			
			case RGBA: 
			
			case BGRA: 
				return format;
			
			default: 
				return PNGDecoder.Format.RGBA;
			
			}

		
		case 4: 
			return PNGDecoder.Format.LUMINANCE_ALPHA;
		
		case 6: 
			switch (format) {
			case ABGR: 
			
			case RGBA: 
			
			case BGRA: 
			
			case RGB: 
				return format;
			
			default: 
				return PNGDecoder.Format.RGBA;
			
			}

		
		}
	}

	public void decode(ByteBuffer byteBuffer, int int1, PNGDecoder.Format format) throws IOException {
		int int2 = byteBuffer.position();
		int int3 = (this.width * this.bitdepth + 7) / 8 * this.bytesPerPixel;
		byte[] byteArray = new byte[int3 + 1];
		byte[] byteArray2 = new byte[int3 + 1];
		byte[] byteArray3 = this.bitdepth < 8 ? new byte[this.width + 1] : null;
		this.maskM = 0;
		Inflater inflater = new Inflater();
		try {
			for (int int4 = 0; int4 < this.height; ++int4) {
				this.readChunkUnzip(inflater, byteArray, 0, byteArray.length);
				this.unfilter(byteArray, byteArray2);
				byteBuffer.position(int2 + int4 * int1);
				label121: switch (this.colorType) {
				case 0: 
					switch (format) {
					case LUMINANCE: 
					
					case ALPHA: 
						this.copy(byteBuffer, byteArray);
						break label121;
					
					default: 
						throw new UnsupportedOperationException("Unsupported format for this image");
					
					}

				
				case 1: 
				
				case 5: 
				
				default: 
					throw new UnsupportedOperationException("Not yet implemented");
				
				case 2: 
					switch (format) {
					case ABGR: 
						this.copyRGBtoABGR(byteBuffer, byteArray);
						break label121;
					
					case RGBA: 
						this.copyRGBtoRGBA(byteBuffer, byteArray);
						break label121;
					
					case BGRA: 
						this.copyRGBtoBGRA(byteBuffer, byteArray);
						break label121;
					
					case RGB: 
						this.copy(byteBuffer, byteArray);
						break label121;
					
					default: 
						throw new UnsupportedOperationException("Unsupported format for this image");
					
					}

				
				case 3: 
					switch (this.bitdepth) {
					case 1: 
						this.expand1(byteArray, byteArray3);
						break;
					
					case 2: 
						this.expand2(byteArray, byteArray3);
						break;
					
					case 3: 
					
					case 5: 
					
					case 6: 
					
					case 7: 
					
					default: 
						throw new UnsupportedOperationException("Unsupported bitdepth for this image");
					
					case 4: 
						this.expand4(byteArray, byteArray3);
						break;
					
					case 8: 
						byteArray3 = byteArray;
					
					}

					switch (format) {
					case ABGR: 
						this.copyPALtoABGR(byteBuffer, byteArray3);
						break label121;
					
					case RGBA: 
						this.copyPALtoRGBA(byteBuffer, byteArray3);
						break label121;
					
					case BGRA: 
						this.copyPALtoBGRA(byteBuffer, byteArray3);
						break label121;
					
					default: 
						throw new UnsupportedOperationException("Unsupported format for this image");
					
					}

				
				case 4: 
					switch (format) {
					case RGBA: 
						this.copyGREYALPHAtoRGBA(byteBuffer, byteArray);
						break label121;
					
					case LUMINANCE_ALPHA: 
						this.copy(byteBuffer, byteArray);
						break label121;
					
					default: 
						throw new UnsupportedOperationException("Unsupported format for this image");
					
					}

				
				case 6: 
					switch (format) {
					case ABGR: 
						this.copyRGBAtoABGR(byteBuffer, byteArray);
						break;
					
					case RGBA: 
						this.copy(byteBuffer, byteArray);
						break;
					
					case BGRA: 
						this.copyRGBAtoBGRA(byteBuffer, byteArray);
						break;
					
					case RGB: 
						this.copyRGBAtoRGB(byteBuffer, byteArray);
						break;
					
					default: 
						throw new UnsupportedOperationException("Unsupported format for this image");
					
					}

				
				}

				byte[] byteArray4 = byteArray;
				byteArray = byteArray2;
				byteArray2 = byteArray4;
			}
		} finally {
			inflater.end();
		}
	}

	public void decodeFlipped(ByteBuffer byteBuffer, int int1, PNGDecoder.Format format) throws IOException {
		if (int1 <= 0) {
			throw new IllegalArgumentException("stride");
		} else {
			int int2 = byteBuffer.position();
			int int3 = (this.height - 1) * int1;
			byteBuffer.position(int2 + int3);
			this.decode(byteBuffer, -int1, format);
			byteBuffer.position(byteBuffer.position() + int3);
		}
	}

	private void copy(ByteBuffer byteBuffer, byte[] byteArray) {
		if (this.bDoMask) {
			int int1 = 1;
			for (int int2 = byteArray.length; int1 < int2; int1 += 4) {
				if (byteArray[int1 + 3] % 255 != 0) {
					this.mask[this.maskM] = true;
				}

				++this.maskM;
			}
		}

		byteBuffer.put(byteArray, 1, byteArray.length - 1);
	}

	private void copyRGBtoABGR(ByteBuffer byteBuffer, byte[] byteArray) {
		if (this.transPixel != null) {
			byte byte1 = this.transPixel[1];
			byte byte2 = this.transPixel[3];
			byte byte3 = this.transPixel[5];
			int int1 = 1;
			for (int int2 = byteArray.length; int1 < int2; int1 += 3) {
				byte byte4 = byteArray[int1];
				byte byte5 = byteArray[int1 + 1];
				byte byte6 = byteArray[int1 + 2];
				byte byte7 = -1;
				if (byte4 == byte1 && byte5 == byte2 && byte6 == byte3) {
					byte7 = 0;
				}

				byteBuffer.put(byte7).put(byte6).put(byte5).put(byte4);
			}
		} else {
			int int3 = 1;
			for (int int4 = byteArray.length; int3 < int4; int3 += 3) {
				byteBuffer.put((byte)-1).put(byteArray[int3 + 2]).put(byteArray[int3 + 1]).put(byteArray[int3]);
			}
		}
	}

	private void copyRGBtoRGBA(ByteBuffer byteBuffer, byte[] byteArray) {
		if (this.transPixel != null) {
			byte byte1 = this.transPixel[1];
			byte byte2 = this.transPixel[3];
			byte byte3 = this.transPixel[5];
			int int1 = 1;
			for (int int2 = byteArray.length; int1 < int2; int1 += 3) {
				byte byte4 = byteArray[int1];
				byte byte5 = byteArray[int1 + 1];
				byte byte6 = byteArray[int1 + 2];
				byte byte7 = -1;
				if (byte4 == byte1 && byte5 == byte2 && byte6 == byte3) {
					byte7 = 0;
				}

				if (this.bDoMask && byte7 == 0) {
					this.mask[this.maskID] = true;
					++this.maskID;
				}

				byteBuffer.put(byte4).put(byte5).put(byte6).put(byte7);
			}
		} else {
			int int3 = 1;
			for (int int4 = byteArray.length; int3 < int4; int3 += 3) {
				byteBuffer.put(byteArray[int3]).put(byteArray[int3 + 1]).put(byteArray[int3 + 2]).put((byte)-1);
			}
		}
	}

	private void copyRGBtoBGRA(ByteBuffer byteBuffer, byte[] byteArray) {
		if (this.transPixel != null) {
			byte byte1 = this.transPixel[1];
			byte byte2 = this.transPixel[3];
			byte byte3 = this.transPixel[5];
			int int1 = 1;
			for (int int2 = byteArray.length; int1 < int2; int1 += 3) {
				byte byte4 = byteArray[int1];
				byte byte5 = byteArray[int1 + 1];
				byte byte6 = byteArray[int1 + 2];
				byte byte7 = -1;
				if (byte4 == byte1 && byte5 == byte2 && byte6 == byte3) {
					byte7 = 0;
				}

				byteBuffer.put(byte6).put(byte5).put(byte4).put(byte7);
			}
		} else {
			int int3 = 1;
			for (int int4 = byteArray.length; int3 < int4; int3 += 3) {
				byteBuffer.put(byteArray[int3 + 2]).put(byteArray[int3 + 1]).put(byteArray[int3]).put((byte)-1);
			}
		}
	}

	private void copyRGBAtoABGR(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1 = 1;
		for (int int2 = byteArray.length; int1 < int2; int1 += 4) {
			byteBuffer.put(byteArray[int1 + 3]).put(byteArray[int1 + 2]).put(byteArray[int1 + 1]).put(byteArray[int1]);
		}
	}

	private void copyRGBAtoBGRA(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1 = 1;
		for (int int2 = byteArray.length; int1 < int2; int1 += 4) {
			byteBuffer.put(byteArray[int1 + 2]).put(byteArray[int1 + 1]).put(byteArray[int1]).put(byteArray[int1 + 3]);
		}
	}

	private void copyRGBAtoRGB(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1 = 1;
		for (int int2 = byteArray.length; int1 < int2; int1 += 4) {
			byteBuffer.put(byteArray[int1]).put(byteArray[int1 + 1]).put(byteArray[int1 + 2]);
		}
	}

	private void copyPALtoABGR(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1;
		int int2;
		int int3;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		if (this.paletteA != null) {
			int1 = 1;
			for (int2 = byteArray.length; int1 < int2; ++int1) {
				int3 = byteArray[int1] & 255;
				byte1 = this.palette[int3 * 3 + 0];
				byte2 = this.palette[int3 * 3 + 1];
				byte3 = this.palette[int3 * 3 + 2];
				byte4 = this.paletteA[int3];
				byteBuffer.put(byte4).put(byte3).put(byte2).put(byte1);
			}
		} else {
			int1 = 1;
			for (int2 = byteArray.length; int1 < int2; ++int1) {
				int3 = byteArray[int1] & 255;
				byte1 = this.palette[int3 * 3 + 0];
				byte2 = this.palette[int3 * 3 + 1];
				byte3 = this.palette[int3 * 3 + 2];
				byte4 = -1;
				byteBuffer.put(byte4).put(byte3).put(byte2).put(byte1);
			}
		}
	}

	private void copyPALtoRGBA(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1;
		int int2;
		int int3;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		if (this.paletteA != null) {
			int1 = 1;
			for (int2 = byteArray.length; int1 < int2; ++int1) {
				int3 = byteArray[int1] & 255;
				byte1 = this.palette[int3 * 3 + 0];
				byte2 = this.palette[int3 * 3 + 1];
				byte3 = this.palette[int3 * 3 + 2];
				byte4 = this.paletteA[int3];
				byteBuffer.put(byte1).put(byte2).put(byte3).put(byte4);
			}
		} else {
			int1 = 1;
			for (int2 = byteArray.length; int1 < int2; ++int1) {
				int3 = byteArray[int1] & 255;
				byte1 = this.palette[int3 * 3 + 0];
				byte2 = this.palette[int3 * 3 + 1];
				byte3 = this.palette[int3 * 3 + 2];
				byte4 = -1;
				byteBuffer.put(byte1).put(byte2).put(byte3).put(byte4);
			}
		}
	}

	private void copyPALtoBGRA(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1;
		int int2;
		int int3;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		if (this.paletteA != null) {
			int1 = 1;
			for (int2 = byteArray.length; int1 < int2; ++int1) {
				int3 = byteArray[int1] & 255;
				byte1 = this.palette[int3 * 3 + 0];
				byte2 = this.palette[int3 * 3 + 1];
				byte3 = this.palette[int3 * 3 + 2];
				byte4 = this.paletteA[int3];
				byteBuffer.put(byte3).put(byte2).put(byte1).put(byte4);
			}
		} else {
			int1 = 1;
			for (int2 = byteArray.length; int1 < int2; ++int1) {
				int3 = byteArray[int1] & 255;
				byte1 = this.palette[int3 * 3 + 0];
				byte2 = this.palette[int3 * 3 + 1];
				byte3 = this.palette[int3 * 3 + 2];
				byte4 = -1;
				byteBuffer.put(byte3).put(byte2).put(byte1).put(byte4);
			}
		}
	}

	private void copyGREYALPHAtoRGBA(ByteBuffer byteBuffer, byte[] byteArray) {
		int int1 = 1;
		for (int int2 = byteArray.length; int1 < int2; int1 += 2) {
			byte byte1 = byteArray[int1];
			byte byte2 = byteArray[int1 + 1];
			byteBuffer.put(byte1).put(byte1).put(byte1).put(byte2);
		}
	}

	private void expand4(byte[] byteArray, byte[] byteArray2) {
		int int1 = 1;
		int int2 = byteArray2.length;
		while (int1 < int2) {
			int int3 = byteArray[1 + (int1 >> 1)] & 255;
			switch (int2 - int1) {
			default: 
				byteArray2[int1 + 1] = (byte)(int3 & 15);
			
			case 1: 
				byteArray2[int1] = (byte)(int3 >> 4);
				int1 += 2;
			
			}
		}
	}

	private void expand2(byte[] byteArray, byte[] byteArray2) {
		int int1 = 1;
		int int2 = byteArray2.length;
		while (int1 < int2) {
			int int3 = byteArray[1 + (int1 >> 2)] & 255;
			switch (int2 - int1) {
			default: 
				byteArray2[int1 + 3] = (byte)(int3 & 3);
			
			case 3: 
				byteArray2[int1 + 2] = (byte)(int3 >> 2 & 3);
			
			case 2: 
				byteArray2[int1 + 1] = (byte)(int3 >> 4 & 3);
			
			case 1: 
				byteArray2[int1] = (byte)(int3 >> 6);
				int1 += 4;
			
			}
		}
	}

	private void expand1(byte[] byteArray, byte[] byteArray2) {
		int int1 = 1;
		int int2 = byteArray2.length;
		while (int1 < int2) {
			int int3 = byteArray[1 + (int1 >> 3)] & 255;
			switch (int2 - int1) {
			default: 
				byteArray2[int1 + 7] = (byte)(int3 & 1);
			
			case 7: 
				byteArray2[int1 + 6] = (byte)(int3 >> 1 & 1);
			
			case 6: 
				byteArray2[int1 + 5] = (byte)(int3 >> 2 & 1);
			
			case 5: 
				byteArray2[int1 + 4] = (byte)(int3 >> 3 & 1);
			
			case 4: 
				byteArray2[int1 + 3] = (byte)(int3 >> 4 & 1);
			
			case 3: 
				byteArray2[int1 + 2] = (byte)(int3 >> 5 & 1);
			
			case 2: 
				byteArray2[int1 + 1] = (byte)(int3 >> 6 & 1);
			
			case 1: 
				byteArray2[int1] = (byte)(int3 >> 7);
				int1 += 8;
			
			}
		}
	}

	private void unfilter(byte[] byteArray, byte[] byteArray2) throws IOException {
		switch (byteArray[0]) {
		case 0: 
			break;
		
		case 1: 
			this.unfilterSub(byteArray);
			break;
		
		case 2: 
			this.unfilterUp(byteArray, byteArray2);
			break;
		
		case 3: 
			this.unfilterAverage(byteArray, byteArray2);
			break;
		
		case 4: 
			this.unfilterPaeth(byteArray, byteArray2);
			break;
		
		default: 
			throw new IOException("invalide filter type in scanline: " + byteArray[0]);
		
		}
	}

	private void unfilterSub(byte[] byteArray) {
		int int1 = this.bytesPerPixel;
		int int2 = int1 + 1;
		for (int int3 = byteArray.length; int2 < int3; ++int2) {
			byteArray[int2] += byteArray[int2 - int1];
		}
	}

	private void unfilterUp(byte[] byteArray, byte[] byteArray2) {
		int int1 = this.bytesPerPixel;
		int int2 = 1;
		for (int int3 = byteArray.length; int2 < int3; ++int2) {
			byteArray[int2] += byteArray2[int2];
		}
	}

	private void unfilterAverage(byte[] byteArray, byte[] byteArray2) {
		int int1 = this.bytesPerPixel;
		int int2;
		for (int2 = 1; int2 <= int1; ++int2) {
			byteArray[int2] += (byte)((byteArray2[int2] & 255) >>> 1);
		}

		for (int int3 = byteArray.length; int2 < int3; ++int2) {
			byteArray[int2] += (byte)((byteArray2[int2] & 255) + (byteArray[int2 - int1] & 255) >>> 1);
		}
	}

	private void unfilterPaeth(byte[] byteArray, byte[] byteArray2) {
		int int1 = this.bytesPerPixel;
		int int2;
		for (int2 = 1; int2 <= int1; ++int2) {
			byteArray[int2] += byteArray2[int2];
		}

		for (int int3 = byteArray.length; int2 < int3; ++int2) {
			int int4 = byteArray[int2 - int1] & 255;
			int int5 = byteArray2[int2] & 255;
			int int6 = byteArray2[int2 - int1] & 255;
			int int7 = int4 + int5 - int6;
			int int8 = int7 - int4;
			if (int8 < 0) {
				int8 = -int8;
			}

			int int9 = int7 - int5;
			if (int9 < 0) {
				int9 = -int9;
			}

			int int10 = int7 - int6;
			if (int10 < 0) {
				int10 = -int10;
			}

			if (int8 <= int9 && int8 <= int10) {
				int6 = int4;
			} else if (int9 <= int10) {
				int6 = int5;
			}

			byteArray[int2] += (byte)int6;
		}
	}

	private void readIHDR() throws IOException {
		this.checkChunkLength(13);
		this.readChunk(this.buffer, 0, 13);
		this.width = this.readInt(this.buffer, 0);
		this.height = this.readInt(this.buffer, 4);
		this.bitdepth = this.buffer[8] & 255;
		this.colorType = this.buffer[9] & 255;
		label43: switch (this.colorType) {
		case 0: 
			if (this.bitdepth != 8) {
				throw new IOException("Unsupported bit depth: " + this.bitdepth);
			}

			this.bytesPerPixel = 1;
			break;
		
		case 1: 
		
		case 5: 
		
		default: 
			throw new IOException("unsupported color format: " + this.colorType);
		
		case 2: 
			if (this.bitdepth != 8) {
				throw new IOException("Unsupported bit depth: " + this.bitdepth);
			}

			this.bytesPerPixel = 3;
			break;
		
		case 3: 
			switch (this.bitdepth) {
			case 1: 
			
			case 2: 
			
			case 4: 
			
			case 8: 
				this.bytesPerPixel = 1;
				break label43;
			
			case 3: 
			
			case 5: 
			
			case 6: 
			
			case 7: 
			
			default: 
				throw new IOException("Unsupported bit depth: " + this.bitdepth);
			
			}

		
		case 4: 
			if (this.bitdepth != 8) {
				throw new IOException("Unsupported bit depth: " + this.bitdepth);
			}

			this.bytesPerPixel = 2;
			break;
		
		case 6: 
			if (this.bitdepth != 8) {
				throw new IOException("Unsupported bit depth: " + this.bitdepth);
			}

			this.bytesPerPixel = 4;
		
		}
		if (this.buffer[10] != 0) {
			throw new IOException("unsupported compression method");
		} else if (this.buffer[11] != 0) {
			throw new IOException("unsupported filtering method");
		} else if (this.buffer[12] != 0) {
			throw new IOException("unsupported interlace method");
		}
	}

	private void readPLTE() throws IOException {
		int int1 = this.chunkLength / 3;
		if (int1 >= 1 && int1 <= 256 && this.chunkLength % 3 == 0) {
			this.palette = new byte[int1 * 3];
			this.readChunk(this.palette, 0, this.palette.length);
		} else {
			throw new IOException("PLTE chunk has wrong length");
		}
	}

	private void readtRNS() throws IOException {
		switch (this.colorType) {
		case 0: 
			this.checkChunkLength(2);
			this.transPixel = new byte[2];
			this.readChunk(this.transPixel, 0, 2);
		
		case 1: 
		
		default: 
			break;
		
		case 2: 
			this.checkChunkLength(6);
			this.transPixel = new byte[6];
			this.readChunk(this.transPixel, 0, 6);
			break;
		
		case 3: 
			if (this.palette == null) {
				throw new IOException("tRNS chunk without PLTE chunk");
			}

			this.paletteA = new byte[this.palette.length / 3];
			Arrays.fill(this.paletteA, (byte)-1);
			this.readChunk(this.paletteA, 0, this.paletteA.length);
		
		}
	}

	private void closeChunk() throws IOException {
		if (this.chunkRemaining > 0) {
			this.skip((long)(this.chunkRemaining + 4));
		} else {
			this.readFully(this.buffer, 0, 4);
			int int1 = this.readInt(this.buffer, 0);
			int int2 = (int)this.crc.getValue();
			if (int2 != int1) {
				throw new IOException("Invalid CRC");
			}
		}

		this.chunkRemaining = 0;
		this.chunkLength = 0;
		this.chunkType = 0;
	}

	private void openChunk() throws IOException {
		this.readFully(this.buffer, 0, 8);
		this.chunkLength = this.readInt(this.buffer, 0);
		this.chunkType = this.readInt(this.buffer, 4);
		this.chunkRemaining = this.chunkLength;
		this.crc.reset();
		this.crc.update(this.buffer, 4, 4);
	}

	private void openChunk(int int1) throws IOException {
		this.openChunk();
		if (this.chunkType != int1) {
			throw new IOException("Expected chunk: " + Integer.toHexString(int1));
		}
	}

	private void checkChunkLength(int int1) throws IOException {
		if (this.chunkLength != int1) {
			throw new IOException("Chunk has wrong size");
		}
	}

	private int readChunk(byte[] byteArray, int int1, int int2) throws IOException {
		if (int2 > this.chunkRemaining) {
			int2 = this.chunkRemaining;
		}

		this.readFully(byteArray, int1, int2);
		this.crc.update(byteArray, int1, int2);
		this.chunkRemaining -= int2;
		return int2;
	}

	private void refillInflater(Inflater inflater) throws IOException {
		while (this.chunkRemaining == 0) {
			this.closeChunk();
			this.openChunk(1229209940);
		}

		int int1 = this.readChunk(this.buffer, 0, this.buffer.length);
		inflater.setInput(this.buffer, 0, int1);
	}

	private void readChunkUnzip(Inflater inflater, byte[] byteArray, int int1, int int2) throws IOException {
		assert byteArray != this.buffer;
		try {
			do {
				int int3 = inflater.inflate(byteArray, int1, int2);
				if (int3 <= 0) {
					if (inflater.finished()) {
						throw new EOFException();
					}

					if (!inflater.needsInput()) {
						throw new IOException("Can\'t inflate " + int2 + " bytes");
					}

					this.refillInflater(inflater);
				} else {
					int1 += int3;
					int2 -= int3;
				}
			}	 while (int2 > 0);
		} catch (DataFormatException dataFormatException) {
			throw (IOException)((IOException)(new IOException("inflate error")).initCause(dataFormatException));
		}
	}

	private void readFully(byte[] byteArray, int int1, int int2) throws IOException {
		do {
			int int3 = this.input.read(byteArray, int1, int2);
			if (int3 < 0) {
				throw new EOFException();
			}

			int1 += int3;
			int2 -= int3;
			this.readTotal += (long)int3;
		} while (int2 > 0);
	}

	private int readInt(byte[] byteArray, int int1) {
		return byteArray[int1] << 24 | (byteArray[int1 + 1] & 255) << 16 | (byteArray[int1 + 2] & 255) << 8 | byteArray[int1 + 3] & 255;
	}

	private void skip(long long1) throws IOException {
		while (long1 > 0L) {
			long long2 = this.input.skip(long1);
			if (long2 < 0L) {
				throw new EOFException();
			}

			long1 -= long2;
		}
	}

	private static boolean checkSignature(byte[] byteArray) {
		for (int int1 = 0; int1 < SIGNATURE.length; ++int1) {
			if (byteArray[int1] != SIGNATURE[int1]) {
				return false;
			}
		}

		return true;
	}

	public static enum Format {

		ALPHA,
		LUMINANCE,
		LUMINANCE_ALPHA,
		RGB,
		RGBA,
		BGRA,
		ABGR,
		numComponents,
		hasAlpha;

		private Format(int int1, boolean boolean1) {
			this.numComponents = int1;
			this.hasAlpha = boolean1;
		}
		public int getNumComponents() {
			return this.numComponents;
		}
		public boolean isHasAlpha() {
			return this.hasAlpha;
		}
	}
}
