package zombie.core.utils;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.textures.Texture;


public class ImageUtils {
	public static boolean USE_MIPMAP = true;

	private ImageUtils() {
	}

	public static void depureTexture(Texture texture, float float1) {
		WrappedBuffer wrappedBuffer = texture.getData();
		ByteBuffer byteBuffer = wrappedBuffer.getBuffer();
		byteBuffer.rewind();
		int int1 = (int)(float1 * 255.0F);
		long long1 = (long)(texture.getWidthHW() * texture.getHeightHW());
		for (int int2 = 0; (long)int2 < long1; ++int2) {
			byteBuffer.mark();
			byteBuffer.get();
			byteBuffer.get();
			byteBuffer.get();
			byte byte1 = byteBuffer.get();
			int int3;
			if (byte1 < 0) {
				int3 = 256 + byte1;
			} else {
				int3 = byte1;
			}

			if (int3 < int1) {
				byteBuffer.reset();
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
			}
		}

		byteBuffer.flip();
		texture.setData(byteBuffer);
		wrappedBuffer.dispose();
	}

	public static int getNextPowerOfTwo(int int1) {
		int int2;
		for (int2 = 2; int2 < int1; int2 += int2) {
		}

		return int2;
	}

	public static int getNextPowerOfTwoHW(int int1) {
		int int2;
		for (int2 = 2; int2 < int1; int2 += int2) {
		}

		return int2;
	}

	public static Texture getScreenShot() {
		Texture texture = new Texture(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0);
		IntBuffer intBuffer = org.lwjglx.BufferUtils.createIntBuffer(4);
		texture.bind();
		intBuffer.rewind();
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexParameteri(3553, 10240, 9729);
		GL11.glCopyTexImage2D(3553, 0, 6408, 0, 0, texture.getWidthHW(), texture.getHeightHW(), 0);
		return texture;
	}

	public static ByteBuffer makeTransp(ByteBuffer byteBuffer, int int1, int int2, int int3, int int4, int int5) {
		return makeTransp(byteBuffer, int1, int2, int3, 0, int4, int5);
	}

	public static ByteBuffer makeTransp(ByteBuffer byteBuffer, int int1, int int2, int int3, int int4, int int5, int int6) {
		byteBuffer.rewind();
		for (int int7 = 0; int7 < int6; ++int7) {
			for (int int8 = 0; int8 < int5; ++int8) {
				byte byte1 = byteBuffer.get();
				byte byte2 = byteBuffer.get();
				byte byte3 = byteBuffer.get();
				if (byte1 == (byte)int1 && byte2 == (byte)int2 && byte3 == (byte)int3) {
					byteBuffer.put((byte)int4);
				} else {
					byteBuffer.get();
				}
			}
		}

		byteBuffer.rewind();
		return byteBuffer;
	}

	public static void saveBmpImage(Texture texture, String string) {
		saveImage(texture, string, "bmp");
	}

	public static void saveImage(Texture texture, String string, String string2) {
		BufferedImage bufferedImage = new BufferedImage(texture.getWidth(), texture.getHeight(), 1);
		WritableRaster writableRaster = bufferedImage.getRaster();
		WrappedBuffer wrappedBuffer = texture.getData();
		ByteBuffer byteBuffer = wrappedBuffer.getBuffer();
		byteBuffer.rewind();
		for (int int1 = 0; int1 < texture.getHeightHW() && int1 < texture.getHeight(); ++int1) {
			for (int int2 = 0; int2 < texture.getWidthHW(); ++int2) {
				if (int2 >= texture.getWidth()) {
					byteBuffer.get();
					byteBuffer.get();
					byteBuffer.get();
					byteBuffer.get();
				} else {
					writableRaster.setPixel(int2, texture.getHeight() - 1 - int1, new int[]{byteBuffer.get(), byteBuffer.get(), byteBuffer.get()});
					byteBuffer.get();
				}
			}
		}

		wrappedBuffer.dispose();
		try {
			ImageIO.write(bufferedImage, "png", new File(string));
		} catch (IOException ioException) {
		}
	}

	public static void saveJpgImage(Texture texture, String string) {
		saveImage(texture, string, "jpg");
	}

	public static void savePngImage(Texture texture, String string) {
		saveImage(texture, string, "png");
	}
}
