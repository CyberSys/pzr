package zombie.core.textures;

import com.evildevil.engines.bubble.texture.DDSLoader;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.ImageUtils;
import zombie.core.utils.WrappedBuffer;
import zombie.core.znet.SteamFriends;
import zombie.debug.DebugOptions;
import zombie.util.list.PZArrayUtil;


public final class ImageData implements Serializable {
	private static final long serialVersionUID = -7893392091273534932L;
	public MipMapLevel data;
	private MipMapLevel[] mipMaps;
	private int height;
	private int heightHW;
	private boolean solid = true;
	private int width;
	private int widthHW;
	private int mipMapCount = -1;
	public boolean alphaPaddingDone = false;
	public boolean bPreserveTransparentColor = false;
	public BooleanGrid mask;
	private static final int BufferSize = 67108864;
	static final DDSLoader dds = new DDSLoader();
	public int id = -1;
	public static final int MIP_LEVEL_IDX_OFFSET = 0;
	private static final ThreadLocal TL_generateMipMaps = ThreadLocal.withInitial(ImageData.L_generateMipMaps::new);
	private static final ThreadLocal TL_performAlphaPadding = ThreadLocal.withInitial(ImageData.L_performAlphaPadding::new);

	public ImageData(TextureID textureID, WrappedBuffer wrappedBuffer) {
		this.data = new MipMapLevel(textureID.widthHW, textureID.heightHW, wrappedBuffer);
		this.width = textureID.width;
		this.widthHW = textureID.widthHW;
		this.height = textureID.height;
		this.heightHW = textureID.heightHW;
		this.solid = textureID.solid;
	}

	public ImageData(String string) throws Exception {
		if (string.contains(".txt")) {
			string = string.replace(".txt", ".png");
		}

		string = Texture.processFilePath(string);
		string = ZomboidFileSystem.instance.getString(string);
		try {
			FileInputStream fileInputStream = new FileInputStream(string);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					PNGDecoder pNGDecoder = new PNGDecoder(bufferedInputStream, false);
					this.width = pNGDecoder.getWidth();
					this.height = pNGDecoder.getHeight();
					this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
					this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
					this.data = new MipMapLevel(this.widthHW, this.heightHW);
					ByteBuffer byteBuffer = this.data.getBuffer();
					byteBuffer.rewind();
					int int1 = this.widthHW * 4;
					int int2;
					int int3;
					if (this.width != this.widthHW) {
						for (int2 = this.width * 4; int2 < this.widthHW * 4; ++int2) {
							for (int3 = 0; int3 < this.heightHW; ++int3) {
								byteBuffer.put(int2 + int3 * int1, (byte)0);
							}
						}
					}

					if (this.height != this.heightHW) {
						for (int2 = this.height; int2 < this.heightHW; ++int2) {
							for (int3 = 0; int3 < this.width * 4; ++int3) {
								byteBuffer.put(int3 + int2 * int1, (byte)0);
							}
						}
					}

					pNGDecoder.decode(this.data.getBuffer(), int1, PNGDecoder.Format.RGBA);
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (Exception exception) {
			this.dispose();
			this.width = this.height = -1;
		}
	}

	public ImageData(int int1, int int2) {
		this.width = int1;
		this.height = int2;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(int1);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(int2);
		this.data = new MipMapLevel(this.widthHW, this.heightHW);
	}

	public ImageData(int int1, int int2, WrappedBuffer wrappedBuffer) {
		this.width = int1;
		this.height = int2;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(int1);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(int2);
		this.data = new MipMapLevel(this.widthHW, this.heightHW, wrappedBuffer);
	}

	ImageData(String string, String string2) {
		Pcx pcx = new Pcx(string, string2);
		this.width = pcx.imageWidth;
		this.height = pcx.imageHeight;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		this.data = new MipMapLevel(this.widthHW, this.heightHW);
		this.setData(pcx);
		this.makeTransp((byte)pcx.palette[762], (byte)pcx.palette[763], (byte)pcx.palette[764], (byte)0);
	}

	ImageData(String string, int[] intArray) {
		Pcx pcx = new Pcx(string, intArray);
		this.width = pcx.imageWidth;
		this.height = pcx.imageHeight;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		this.data = new MipMapLevel(this.widthHW, this.heightHW);
		this.setData(pcx);
		this.makeTransp((byte)pcx.palette[762], (byte)pcx.palette[763], (byte)pcx.palette[764], (byte)0);
	}

	public ImageData(BufferedInputStream bufferedInputStream, boolean boolean1, Texture.PZFileformat pZFileformat) {
		if (pZFileformat == Texture.PZFileformat.DDS) {
			RenderThread.invokeOnRenderContext(()->{
				this.id = dds.loadDDSFile(bufferedInputStream);
			});

			this.width = DDSLoader.lastWid;
			this.height = DDSLoader.lastHei;
			this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
			this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		}
	}

	public ImageData(InputStream inputStream, boolean boolean1) throws Exception {
		Object object = null;
		PNGDecoder pNGDecoder = new PNGDecoder(inputStream, boolean1);
		this.width = pNGDecoder.getWidth();
		this.height = pNGDecoder.getHeight();
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		this.data = new MipMapLevel(this.widthHW, this.heightHW);
		this.data.rewind();
		pNGDecoder.decode(this.data.getBuffer(), 4 * this.widthHW, PNGDecoder.Format.RGBA);
		if (boolean1) {
			this.mask = pNGDecoder.mask;
		}
	}

	public static ImageData createSteamAvatar(long long1) {
		WrappedBuffer wrappedBuffer = DirectBufferAllocator.allocate(65536);
		int int1 = SteamFriends.CreateSteamAvatar(long1, wrappedBuffer.getBuffer());
		if (int1 <= 0) {
			return null;
		} else {
			int int2 = wrappedBuffer.getBuffer().position() / (int1 * 4);
			wrappedBuffer.getBuffer().flip();
			ImageData imageData = new ImageData(int1, int2, wrappedBuffer);
			return imageData;
		}
	}

	public MipMapLevel getData() {
		if (this.data == null) {
			this.data = new MipMapLevel(this.widthHW, this.heightHW, DirectBufferAllocator.allocate(67108864));
		}

		this.data.rewind();
		return this.data;
	}

	public void makeTransp(byte byte1, byte byte2, byte byte3) {
		this.makeTransp(byte1, byte2, byte3, (byte)0);
	}

	public void makeTransp(byte byte1, byte byte2, byte byte3, byte byte4) {
		this.solid = false;
		ByteBuffer byteBuffer = this.data.getBuffer();
		byteBuffer.rewind();
		int int1 = this.widthHW * 4;
		for (int int2 = 0; int2 < this.heightHW; ++int2) {
			int int3 = byteBuffer.position();
			for (int int4 = 0; int4 < this.widthHW; ++int4) {
				byte byte5 = byteBuffer.get();
				byte byte6 = byteBuffer.get();
				byte byte7 = byteBuffer.get();
				if (byte5 == byte1 && byte6 == byte2 && byte7 == byte3) {
					byteBuffer.put(byte4);
				} else {
					byteBuffer.get();
				}

				if (int4 == this.width) {
					byteBuffer.position(int3 + int1);
					break;
				}
			}

			if (int2 == this.height) {
				break;
			}
		}

		byteBuffer.rewind();
	}

	public void setData(BufferedImage bufferedImage) {
		if (bufferedImage != null) {
			this.setData(bufferedImage.getData());
		}
	}

	public void setData(Raster raster) {
		if (raster == null) {
			(new Exception()).printStackTrace();
		} else {
			this.width = raster.getWidth();
			this.height = raster.getHeight();
			if (this.width <= this.widthHW && this.height <= this.heightHW) {
				int[] intArray = raster.getPixels(0, 0, this.width, this.height, (int[])null);
				ByteBuffer byteBuffer = this.data.getBuffer();
				byteBuffer.rewind();
				int int1 = 0;
				int int2 = byteBuffer.position();
				int int3 = this.widthHW * 4;
				for (int int4 = 0; int4 < intArray.length; ++int4) {
					++int1;
					if (int1 > this.width) {
						byteBuffer.position(int2 + int3);
						int2 = byteBuffer.position();
						int1 = 1;
					}

					byteBuffer.put((byte)intArray[int4]);
					++int4;
					byteBuffer.put((byte)intArray[int4]);
					++int4;
					byteBuffer.put((byte)intArray[int4]);
					++int4;
					byteBuffer.put((byte)intArray[int4]);
				}

				byteBuffer.rewind();
				this.solid = false;
			} else {
				(new Exception()).printStackTrace();
			}
		}
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.data = new MipMapLevel(this.widthHW, this.heightHW);
		ByteBuffer byteBuffer = this.data.getBuffer();
		for (int int1 = 0; int1 < this.widthHW * this.heightHW; ++int1) {
			byteBuffer.put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte());
		}

		byteBuffer.flip();
	}

	private void setData(Pcx pcx) {
		this.width = pcx.imageWidth;
		this.height = pcx.imageHeight;
		if (this.width <= this.widthHW && this.height <= this.heightHW) {
			ByteBuffer byteBuffer = this.data.getBuffer();
			byteBuffer.rewind();
			int int1 = 0;
			int int2 = byteBuffer.position();
			int int3 = this.widthHW * 4;
			for (int int4 = 0; int4 < this.heightHW * this.widthHW * 3; ++int4) {
				++int1;
				if (int1 > this.width) {
					int2 = byteBuffer.position();
					int1 = 1;
				}

				byteBuffer.put(pcx.imageData[int4]);
				++int4;
				byteBuffer.put(pcx.imageData[int4]);
				++int4;
				byteBuffer.put(pcx.imageData[int4]);
				byteBuffer.put((byte)-1);
			}

			byteBuffer.rewind();
			this.solid = false;
		} else {
			(new Exception()).printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
		ByteBuffer byteBuffer = this.data.getBuffer();
		byteBuffer.rewind();
		for (int int1 = 0; int1 < this.widthHW * this.heightHW; ++int1) {
			objectOutputStream.writeByte(byteBuffer.get());
			objectOutputStream.writeByte(byteBuffer.get());
			objectOutputStream.writeByte(byteBuffer.get());
			objectOutputStream.writeByte(byteBuffer.get());
		}
	}

	public int getHeight() {
		return this.height;
	}

	public int getHeightHW() {
		return this.heightHW;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public int getWidth() {
		return this.width;
	}

	public int getWidthHW() {
		return this.widthHW;
	}

	public int getMipMapCount() {
		if (this.data == null) {
			return 0;
		} else {
			if (this.mipMapCount < 0) {
				this.mipMapCount = calculateNumMips(this.widthHW, this.heightHW);
			}

			return this.mipMapCount;
		}
	}

	public MipMapLevel getMipMapData(int int1) {
		if (this.data != null && !this.alphaPaddingDone) {
			this.performAlphaPadding();
		}

		if (int1 == 0) {
			return this.getData();
		} else {
			if (this.mipMaps == null) {
				this.generateMipMaps();
			}

			int int2 = int1 - 1;
			MipMapLevel mipMapLevel = this.mipMaps[int2];
			mipMapLevel.rewind();
			return mipMapLevel;
		}
	}

	public void initMipMaps() {
		int int1 = this.getMipMapCount();
		int int2 = PZMath.min(0, int1 - 1);
		int int3 = int1;
		for (int int4 = int2; int4 < int3; ++int4) {
			this.getMipMapData(int4);
		}
	}

	public void dispose() {
		if (this.data != null) {
			this.data.dispose();
			this.data = null;
		}

		if (this.mipMaps != null) {
			for (int int1 = 0; int1 < this.mipMaps.length; ++int1) {
				this.mipMaps[int1].dispose();
				this.mipMaps[int1] = null;
			}

			this.mipMaps = null;
		}
	}

	private void generateMipMaps() {
		this.mipMapCount = calculateNumMips(this.widthHW, this.heightHW);
		int int1 = this.mipMapCount - 1;
		this.mipMaps = new MipMapLevel[int1];
		MipMapLevel mipMapLevel = this.getData();
		int int2 = this.widthHW;
		int int3 = this.heightHW;
		MipMapLevel mipMapLevel2 = mipMapLevel;
		int int4 = getNextMipDimension(int2);
		int int5 = getNextMipDimension(int3);
		for (int int6 = 0; int6 < int1; ++int6) {
			MipMapLevel mipMapLevel3 = new MipMapLevel(int4, int5);
			if (int6 < 2) {
				this.scaleMipLevelMaxAlpha(mipMapLevel2, mipMapLevel3, int6);
			} else {
				this.scaleMipLevelAverage(mipMapLevel2, mipMapLevel3, int6);
			}

			this.performAlphaPadding(mipMapLevel3);
			this.mipMaps[int6] = mipMapLevel3;
			mipMapLevel2 = mipMapLevel3;
			int4 = getNextMipDimension(int4);
			int5 = getNextMipDimension(int5);
		}
	}

	private void scaleMipLevelMaxAlpha(MipMapLevel mipMapLevel, MipMapLevel mipMapLevel2, int int1) {
		ImageData.L_generateMipMaps l_generateMipMaps = (ImageData.L_generateMipMaps)TL_generateMipMaps.get();
		ByteBuffer byteBuffer = mipMapLevel2.getBuffer();
		byteBuffer.rewind();
		int int2 = mipMapLevel.width;
		int int3 = mipMapLevel.height;
		ByteBuffer byteBuffer2 = mipMapLevel.getBuffer();
		int int4 = mipMapLevel2.width;
		int int5 = mipMapLevel2.height;
		for (int int6 = 0; int6 < int5; ++int6) {
			for (int int7 = 0; int7 < int4; ++int7) {
				int[] intArray = l_generateMipMaps.pixelBytes;
				int[] intArray2 = l_generateMipMaps.originalPixel;
				int[] intArray3 = l_generateMipMaps.resultPixelBytes;
				getPixelClamped(byteBuffer2, int2, int3, int7 * 2, int6 * 2, intArray2);
				byte byte1;
				if (!this.bPreserveTransparentColor && intArray2[3] <= 0) {
					PZArrayUtil.arraySet(intArray3, 0);
					byte1 = 0;
				} else {
					PZArrayUtil.arrayCopy((int[])intArray3, (int[])intArray2, 0, 4);
					byte1 = 1;
				}

				int int8 = byte1 + this.sampleNeighborPixelDiscard(byteBuffer2, int2, int3, int7 * 2 + 1, int6 * 2, intArray, intArray3);
				int8 += this.sampleNeighborPixelDiscard(byteBuffer2, int2, int3, int7 * 2, int6 * 2 + 1, intArray, intArray3);
				int8 += this.sampleNeighborPixelDiscard(byteBuffer2, int2, int3, int7 * 2 + 1, int6 * 2 + 1, intArray, intArray3);
				if (int8 > 0) {
					intArray3[0] /= int8;
					intArray3[1] /= int8;
					intArray3[2] /= int8;
					intArray3[3] /= int8;
					if (DebugOptions.instance.IsoSprite.WorldMipmapColors.getValue()) {
						setMipmapDebugColors(int1, intArray3);
					}
				}

				setPixel(byteBuffer, int4, int5, int7, int6, intArray3);
			}
		}
	}

	private void scaleMipLevelAverage(MipMapLevel mipMapLevel, MipMapLevel mipMapLevel2, int int1) {
		ImageData.L_generateMipMaps l_generateMipMaps = (ImageData.L_generateMipMaps)TL_generateMipMaps.get();
		ByteBuffer byteBuffer = mipMapLevel2.getBuffer();
		byteBuffer.rewind();
		int int2 = mipMapLevel.width;
		int int3 = mipMapLevel.height;
		ByteBuffer byteBuffer2 = mipMapLevel.getBuffer();
		int int4 = mipMapLevel2.width;
		int int5 = mipMapLevel2.height;
		for (int int6 = 0; int6 < int5; ++int6) {
			for (int int7 = 0; int7 < int4; ++int7) {
				int[] intArray = l_generateMipMaps.resultPixelBytes;
				byte byte1 = 1;
				getPixelClamped(byteBuffer2, int2, int3, int7 * 2, int6 * 2, intArray);
				int int8 = byte1 + getPixelDiscard(byteBuffer2, int2, int3, int7 * 2 + 1, int6 * 2, intArray);
				int8 += getPixelDiscard(byteBuffer2, int2, int3, int7 * 2, int6 * 2 + 1, intArray);
				int8 += getPixelDiscard(byteBuffer2, int2, int3, int7 * 2 + 1, int6 * 2 + 1, intArray);
				intArray[0] /= int8;
				intArray[1] /= int8;
				intArray[2] /= int8;
				intArray[3] /= int8;
				if (intArray[3] != 0 && DebugOptions.instance.IsoSprite.WorldMipmapColors.getValue()) {
					setMipmapDebugColors(int1, intArray);
				}

				setPixel(byteBuffer, int4, int5, int7, int6, intArray);
			}
		}
	}

	public static int calculateNumMips(int int1, int int2) {
		int int3 = calculateNumMips(int1);
		int int4 = calculateNumMips(int2);
		return PZMath.max(int3, int4);
	}

	private static int calculateNumMips(int int1) {
		int int2 = 0;
		for (int int3 = int1; int3 > 0; ++int2) {
			int3 >>= 1;
		}

		return int2;
	}

	private void performAlphaPadding() {
		MipMapLevel mipMapLevel = this.data;
		if (mipMapLevel != null && mipMapLevel.data != null) {
			this.performAlphaPadding(mipMapLevel);
			this.alphaPaddingDone = true;
		}
	}

	private void performAlphaPadding(MipMapLevel mipMapLevel) {
		ImageData.L_performAlphaPadding l_performAlphaPadding = (ImageData.L_performAlphaPadding)TL_performAlphaPadding.get();
		ByteBuffer byteBuffer = mipMapLevel.getBuffer();
		int int1 = mipMapLevel.width;
		int int2 = mipMapLevel.height;
		for (int int3 = 0; int3 < int2; ++int3) {
			for (int int4 = 0; int4 < int1; ++int4) {
				int int5 = (int3 * int1 + int4) * 4;
				int int6 = byteBuffer.get(int5 + 3) & 255;
				if (int6 != 255 && int6 == 0) {
					int[] intArray = getPixelClamped(byteBuffer, int1, int2, int4, int3, l_performAlphaPadding.pixelRGBA);
					int[] intArray2 = l_performAlphaPadding.newPixelRGBA;
					PZArrayUtil.arraySet(intArray2, 0);
					intArray2[3] = intArray[3];
					byte byte1 = 0;
					int int7 = byte1 + this.sampleNeighborPixelDiscard(byteBuffer, int1, int2, int4 - 1, int3, l_performAlphaPadding.pixelRGBA_neighbor, intArray2);
					int7 += this.sampleNeighborPixelDiscard(byteBuffer, int1, int2, int4, int3 - 1, l_performAlphaPadding.pixelRGBA_neighbor, intArray2);
					int7 += this.sampleNeighborPixelDiscard(byteBuffer, int1, int2, int4 - 1, int3 - 1, l_performAlphaPadding.pixelRGBA_neighbor, intArray2);
					int7 += this.sampleNeighborPixelDiscard(byteBuffer, int1, int2, int4 + 1, int3, l_performAlphaPadding.pixelRGBA_neighbor, intArray2);
					int7 += this.sampleNeighborPixelDiscard(byteBuffer, int1, int2, int4, int3 + 1, l_performAlphaPadding.pixelRGBA_neighbor, intArray2);
					int7 += this.sampleNeighborPixelDiscard(byteBuffer, int1, int2, int4 + 1, int3 + 1, l_performAlphaPadding.pixelRGBA_neighbor, intArray2);
					if (int7 > 0) {
						intArray2[0] /= int7;
						intArray2[1] /= int7;
						intArray2[2] /= int7;
						intArray2[3] = intArray[3];
						setPixel(byteBuffer, int1, int2, int4, int3, intArray2);
					}
				}
			}
		}
	}

	private int sampleNeighborPixelDiscard(ByteBuffer byteBuffer, int int1, int int2, int int3, int int4, int[] intArray, int[] intArray2) {
		if (int3 >= 0 && int3 < int1 && int4 >= 0 && int4 < int2) {
			getPixelClamped(byteBuffer, int1, int2, int3, int4, intArray);
			if (intArray[3] > 0) {
				intArray2[0] += intArray[0];
				intArray2[1] += intArray[1];
				intArray2[2] += intArray[2];
				intArray2[3] += intArray[3];
				return 1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static int getPixelDiscard(ByteBuffer byteBuffer, int int1, int int2, int int3, int int4, int[] intArray) {
		if (int3 >= 0 && int3 < int1 && int4 >= 0 && int4 < int2) {
			int int5 = (int3 + int4 * int1) * 4;
			intArray[0] += byteBuffer.get(int5) & 255;
			intArray[1] += byteBuffer.get(int5 + 1) & 255;
			intArray[2] += byteBuffer.get(int5 + 2) & 255;
			intArray[3] += byteBuffer.get(int5 + 3) & 255;
			return 1;
		} else {
			return 0;
		}
	}

	public static int[] getPixelClamped(ByteBuffer byteBuffer, int int1, int int2, int int3, int int4, int[] intArray) {
		int3 = PZMath.clamp(int3, 0, int1 - 1);
		int4 = PZMath.clamp(int4, 0, int2 - 1);
		int int5 = (int3 + int4 * int1) * 4;
		intArray[0] = byteBuffer.get(int5) & 255;
		intArray[1] = byteBuffer.get(int5 + 1) & 255;
		intArray[2] = byteBuffer.get(int5 + 2) & 255;
		intArray[3] = byteBuffer.get(int5 + 3) & 255;
		return intArray;
	}

	public static void setPixel(ByteBuffer byteBuffer, int int1, int int2, int int3, int int4, int[] intArray) {
		int int5 = (int3 + int4 * int1) * 4;
		byteBuffer.put(int5, (byte)(intArray[0] & 255));
		byteBuffer.put(int5 + 1, (byte)(intArray[1] & 255));
		byteBuffer.put(int5 + 2, (byte)(intArray[2] & 255));
		byteBuffer.put(int5 + 3, (byte)(intArray[3] & 255));
	}

	public static int getNextMipDimension(int int1) {
		if (int1 > 1) {
			int1 >>= 1;
		}

		return int1;
	}

	private static void setMipmapDebugColors(int int1, int[] intArray) {
		switch (int1) {
		case 0: 
			intArray[0] = 255;
			intArray[1] = 0;
			intArray[2] = 0;
			break;
		
		case 1: 
			intArray[0] = 0;
			intArray[1] = 255;
			intArray[2] = 0;
			break;
		
		case 2: 
			intArray[0] = 0;
			intArray[1] = 0;
			intArray[2] = 255;
			break;
		
		case 3: 
			intArray[0] = 255;
			intArray[1] = 255;
			intArray[2] = 0;
			break;
		
		case 4: 
			intArray[0] = 255;
			intArray[1] = 0;
			intArray[2] = 255;
			break;
		
		case 5: 
			intArray[0] = 0;
			intArray[1] = 0;
			intArray[2] = 0;
			break;
		
		case 6: 
			intArray[0] = 255;
			intArray[1] = 255;
			intArray[2] = 255;
			break;
		
		case 7: 
			intArray[0] = 128;
			intArray[1] = 128;
			intArray[2] = 128;
		
		}
	}

	private static final class L_generateMipMaps {
		final int[] pixelBytes = new int[4];
		final int[] originalPixel = new int[4];
		final int[] resultPixelBytes = new int[4];
	}

	static final class L_performAlphaPadding {
		final int[] pixelRGBA = new int[4];
		final int[] newPixelRGBA = new int[4];
		final int[] pixelRGBA_neighbor = new int[4];
	}
}
