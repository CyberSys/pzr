package zombie.core.textures;

import com.evildevil.engines.bubble.texture.DDSLoader;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.ImageUtils;
import zombie.core.utils.WrappedBuffer;
import zombie.core.znet.SteamFriends;
import zombie.debug.DebugLog;


public class ImageData implements Serializable {
	private static final long serialVersionUID = -7893392091273534932L;
	public static WrappedBuffer data;
	private int height;
	private int heightHW;
	private boolean solid = true;
	private int width;
	private int widthHW;
	public boolean[] mask;
	public static int BufferSize = 67108864;
	static final DDSLoader dds = new DDSLoader();
	public int id = -1;

	public ImageData(TextureID textureID, WrappedBuffer wrappedBuffer) {
		data = wrappedBuffer;
		this.width = textureID.width;
		this.widthHW = textureID.widthHW;
		this.height = textureID.height;
		this.heightHW = textureID.heightHW;
		this.solid = textureID.solid;
	}

	public void Load(BufferedImage bufferedImage) {
		byte[] byteArray = (byte[])((byte[])bufferedImage.getRaster().getDataElements(0, 0, this.width, this.height, (Object)null));
		int int1 = 0;
		int int2 = 0;
		ByteBuffer byteBuffer;
		int int3;
		int int4;
		if (4 * this.widthHW * this.heightHW == byteArray.length) {
			for (int4 = 0; int4 < byteArray.length; ++int4) {
				byteBuffer = data.getBuffer().put(byteArray[int4]);
				++int4;
				byteBuffer = byteBuffer.put(byteArray[int4]);
				++int4;
				byteBuffer = byteBuffer.put(byteArray[int4]);
				++int4;
				byteBuffer.put(byteArray[int4]);
				++int1;
				if (int1 == this.width) {
					byteBuffer = data.getBuffer();
					int3 = this.widthHW * 4;
					++int2;
					byteBuffer.position(int3 * int2);
					int1 = 0;
				}
			}
		} else {
			for (int4 = 0; int4 < byteArray.length; ++int4) {
				byteBuffer = data.getBuffer().put(byteArray[int4]);
				++int4;
				byteBuffer = byteBuffer.put(byteArray[int4]);
				++int4;
				byteBuffer.put(byteArray[int4]).put((byte)-1);
				++int1;
				if (int1 == this.width) {
					byteBuffer = data.getBuffer();
					int3 = this.widthHW * 4;
					++int2;
					byteBuffer.position(int3 * int2);
					int1 = 0;
				}
			}
		}

		data.getBuffer().rewind();
	}

	public ImageData(BufferedImage bufferedImage) {
		this.width = bufferedImage.getWidth();
		this.height = bufferedImage.getHeight();
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		if (data == null) {
			data = DirectBufferAllocator.allocate(BufferSize);
		}

		ByteBuffer byteBuffer = data.getBuffer();
		byte[] byteArray = (byte[])((byte[])bufferedImage.getRaster().getDataElements(0, 0, this.width, this.height, (Object)null));
		int int1 = 0;
		int int2 = 0;
		ByteBuffer byteBuffer2;
		int int3;
		int int4;
		if (4 * this.widthHW * this.heightHW == byteArray.length) {
			for (int4 = 0; int4 < byteArray.length; ++int4) {
				byteBuffer2 = byteBuffer.put(byteArray[int4]);
				++int4;
				byteBuffer2 = byteBuffer2.put(byteArray[int4]);
				++int4;
				byteBuffer2 = byteBuffer2.put(byteArray[int4]);
				++int4;
				byteBuffer2.put(byteArray[int4]);
				++int1;
				if (int1 == this.width) {
					int3 = this.widthHW * 4;
					++int2;
					byteBuffer.position(int3 * int2);
					int1 = 0;
				}
			}
		} else {
			for (int4 = 0; int4 < byteArray.length; ++int4) {
				byteBuffer2 = byteBuffer.put(byteArray[int4]);
				++int4;
				byteBuffer2 = byteBuffer2.put(byteArray[int4]);
				++int4;
				byteBuffer2.put(byteArray[int4]).put((byte)-1);
				++int1;
				if (int1 == this.width) {
					int3 = this.widthHW * 4;
					++int2;
					byteBuffer.position(int3 * int2);
					int1 = 0;
				}
			}
		}

		byteBuffer.rewind();
	}

	public ImageData(String string) {
		if (string.contains(".txt")) {
			string = string.replace(".txt", ".png");
		}

		FileInputStream fileInputStream;
		int int1;
		for (fileInputStream = null; (int1 = string.indexOf("\\")) != -1; string = string.substring(0, int1) + '/' + string.substring(int1 + 1)) {
		}

		if (fileInputStream == null) {
			try {
				fileInputStream = new FileInputStream(ZomboidFileSystem.instance.getString(string));
			} catch (FileNotFoundException fileNotFoundException) {
			}

			if (fileInputStream == null) {
				this.width = this.height = -1;
				if (Texture.WarnFailFindTexture && Core.bDebug) {
				}

				return;
			}
		}

		assert fileInputStream != null;
		try {
			PNGDecoder pNGDecoder = new PNGDecoder(fileInputStream, false);
			this.width = pNGDecoder.getWidth();
			this.height = pNGDecoder.getHeight();
			this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
			this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
			if (data == null) {
				data = DirectBufferAllocator.allocate(BufferSize);
			}

			ByteBuffer byteBuffer = data.getBuffer();
			byteBuffer.rewind();
			pNGDecoder.decode(data.getBuffer(), 4 * ImageUtils.getNextPowerOfTwoHW(pNGDecoder.getWidth()), PNGDecoder.Format.RGBA);
		} catch (IOException ioException) {
			this.width = this.height = -1;
			ioException.printStackTrace();
		} catch (UnsupportedOperationException unsupportedOperationException) {
			this.width = this.height = -1;
			DebugLog.log(unsupportedOperationException.getMessage());
		}

		try {
			fileInputStream.close();
		} catch (IOException ioException2) {
			ioException2.printStackTrace();
		}
	}

	public ImageData(int int1, int int2) {
		this.width = int1;
		this.height = int2;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(int1);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(int2);
		if (data == null) {
			data = DirectBufferAllocator.allocate(BufferSize);
		}
	}

	ImageData(String string, String string2) {
		Pcx pcx = new Pcx(string, string2);
		this.width = pcx.imageWidth;
		this.height = pcx.imageHeight;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		if (data == null) {
			data = DirectBufferAllocator.allocate(BufferSize);
		}

		this.setData(pcx);
		this.makeTransp((byte)pcx.palette[762], (byte)pcx.palette[763], (byte)pcx.palette[764], (byte)0);
	}

	ImageData(String string, int[] intArray) {
		Pcx pcx = new Pcx(string, intArray);
		this.width = pcx.imageWidth;
		this.height = pcx.imageHeight;
		this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
		this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		if (data == null) {
			data = DirectBufferAllocator.allocate(BufferSize);
		}

		this.setData(pcx);
		this.makeTransp((byte)pcx.palette[762], (byte)pcx.palette[763], (byte)pcx.palette[764], (byte)0);
	}

	public ImageData(BufferedInputStream bufferedInputStream, boolean boolean1, Texture.PZFileformat pZFileformat) {
		if (pZFileformat == Texture.PZFileformat.DDS) {
			this.id = dds.loadDDSFile(bufferedInputStream);
			this.width = DDSLoader.lastWid;
			DDSLoader dDSLoader = dds;
			this.height = DDSLoader.lastHei;
			this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
			this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
		}
	}

	public ImageData(BufferedInputStream bufferedInputStream, boolean boolean1) {
		Object object = null;
		try {
			PNGDecoder pNGDecoder = new PNGDecoder(bufferedInputStream, boolean1);
			this.width = pNGDecoder.getWidth();
			this.height = pNGDecoder.getHeight();
			this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
			this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
			if (data == null) {
				data = DirectBufferAllocator.allocate(BufferSize);
			}

			ByteBuffer byteBuffer = data.getBuffer();
			byteBuffer.rewind();
			pNGDecoder.decode(data.getBuffer(), 4 * ImageUtils.getNextPowerOfTwoHW(pNGDecoder.getWidth()), PNGDecoder.Format.RGBA);
			if (boolean1) {
				this.mask = pNGDecoder.mask;
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static ImageData createSteamAvatar(long long1) {
		if (data == null) {
			data = DirectBufferAllocator.allocate(BufferSize);
		}

		int int1 = SteamFriends.CreateSteamAvatar(long1, data.getBuffer());
		if (int1 <= 0) {
			return null;
		} else {
			int int2 = data.getBuffer().position() / (int1 * 4);
			data.getBuffer().clear();
			ImageData imageData = new ImageData(int1, int2);
			return imageData;
		}
	}

	public WrappedBuffer getData() {
		if (data == null) {
			data = DirectBufferAllocator.allocate(BufferSize);
		}

		data.getBuffer().rewind();
		return data;
	}

	public void makeTransp(byte byte1, byte byte2, byte byte3) {
		this.makeTransp(byte1, byte2, byte3, (byte)0);
	}

	public void makeTransp(byte byte1, byte byte2, byte byte3, byte byte4) {
		this.solid = false;
		ByteBuffer byteBuffer = data.getBuffer();
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
				ByteBuffer byteBuffer = data.getBuffer();
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
		data = DirectBufferAllocator.allocate(4 * this.widthHW * this.heightHW);
		ByteBuffer byteBuffer = data.getBuffer();
		for (int int1 = 0; int1 < this.widthHW * this.heightHW; ++int1) {
			byteBuffer.put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte());
		}

		byteBuffer.flip();
	}

	private void setData(Pcx pcx) {
		this.width = pcx.imageWidth;
		this.height = pcx.imageHeight;
		if (this.width <= this.widthHW && this.height <= this.heightHW) {
			ByteBuffer byteBuffer = data.getBuffer();
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
		ByteBuffer byteBuffer = data.getBuffer();
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
}
