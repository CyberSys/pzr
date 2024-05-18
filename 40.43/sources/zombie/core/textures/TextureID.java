package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import zombie.FrameLoader;
import zombie.IndieGL;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.WrappedBuffer;
import zombie.interfaces.IDestroyable;


public class TextureID implements IDestroyable,Serializable {
	private static final long serialVersionUID = 4409253583065563738L;
	public static boolean USE_MIPMAP = false;
	public static boolean FREE_MEMORY = true;
	public static HashMap TextureIDMap = new HashMap();
	public static Stack TextureIDStack = new Stack();
	static boolean bAlt = false;
	protected transient ImageData data;
	protected int height;
	protected int heightHW;
	protected transient int id = -1;
	protected transient IntBuffer idBuffer;
	protected String pathFileName;
	protected boolean solid;
	protected int width;
	protected int widthHW;
	ArrayList alphaList;
	int referenceCount = 0;
	boolean[] mask;
	public static long totalGraphicMemory = 0L;
	public static boolean UseFiltering = false;
	public static boolean bUseCompression = true;
	public static boolean bUseCompressionOption = true;
	public static float totalMemUsed = 0.0F;

	protected TextureID() {
	}

	public TextureID(int int1, int int2) {
		this.data = new ImageData(int1, int2);
		RenderThread.borrowContext();
		this.createTexture(false);
		RenderThread.returnContext();
	}

	public TextureID(ImageData imageData) {
		this.data = imageData;
		RenderThread.borrowContext();
		this.createTexture();
		RenderThread.returnContext();
	}

	public TextureID(String string, String string2) {
		this.data = new ImageData(string, string2);
		this.pathFileName = string;
		RenderThread.borrowContext();
		this.createTexture();
		RenderThread.returnContext();
	}

	public TextureID(String string, int[] intArray) {
		this.data = new ImageData(string, intArray);
		this.pathFileName = string;
		RenderThread.borrowContext();
		this.createTexture();
		RenderThread.returnContext();
	}

	public TextureID(String string, int int1, int int2, int int3) {
		if (string.startsWith("/")) {
			string = string.substring(1);
		}

		int int4;
		while ((int4 = string.indexOf("\\")) != -1) {
			string = string.substring(0, int4) + '/' + string.substring(int4 + 1);
		}

		(this.data = new ImageData(string)).makeTransp((byte)int1, (byte)int2, (byte)int3);
		if (this.alphaList == null) {
			this.alphaList = new ArrayList();
		}

		this.alphaList.add(new AlphaColorIndex(int1, int2, int3, 0));
		this.pathFileName = string;
		this.createTexture();
	}

	public TextureID(String string) {
		if (string.toLowerCase().contains(".pcx")) {
			this.data = new ImageData(string, string);
		} else {
			this.data = new ImageData(string);
		}

		if (this.data.getHeight() != -1) {
			this.pathFileName = string;
			RenderThread.borrowContext();
			this.createTexture();
			RenderThread.returnContext();
		}
	}

	public TextureID(BufferedInputStream bufferedInputStream, String string, boolean boolean1, Texture.PZFileformat pZFileformat) {
		this.data = new ImageData(bufferedInputStream, boolean1, pZFileformat);
		if (this.data.id != -1) {
			this.id = this.data.id;
			this.width = this.data.getWidth();
			this.height = this.data.getHeight();
			this.widthHW = this.data.getWidthHW();
			this.heightHW = this.data.getHeightHW();
			totalGraphicMemory += (long)(this.widthHW * this.heightHW * 8);
			this.solid = this.data.isSolid();
		} else {
			if (boolean1) {
				this.mask = this.data.mask;
				this.data.mask = null;
			}

			this.createTexture();
		}

		this.pathFileName = string;
	}

	public TextureID(BufferedInputStream bufferedInputStream, String string, boolean boolean1) {
		this.data = new ImageData(bufferedInputStream, boolean1);
		if (boolean1) {
			this.mask = this.data.mask;
			this.data.mask = null;
		}

		this.pathFileName = string;
		this.createTexture();
	}

	public static TextureID createSteamAvatar(long long1) {
		ImageData imageData = ImageData.createSteamAvatar(long1);
		if (imageData == null) {
			return null;
		} else {
			TextureID textureID = new TextureID(imageData);
			return textureID;
		}
	}

	public boolean bind() {
		if (this.id != Texture.lastTextureID) {
			if (this.id == -1) {
				this.generateHwId(true);
			}

			GL11.glBindTexture(3553, this.id);
			Texture.lastlastTextureID = Texture.lastTextureID;
			Texture.lastTextureID = this.id;
			++Texture.BindCount;
			return true;
		} else {
			return false;
		}
	}

	public boolean bindalways() {
		IndieGL.End();
		if (this.id == -1) {
			this.generateHwId(true);
		}

		GL11.glBindTexture(3553, this.id);
		Texture.lastlastTextureID = Texture.lastTextureID;
		Texture.lastTextureID = this.id;
		++Texture.BindCount;
		return true;
	}

	public void destroy() {
		if (this.id != -1) {
			this.id = -1;
		}
	}

	public void freeMemory() {
		this.data = null;
	}

	public WrappedBuffer getData() {
		this.bind();
		WrappedBuffer wrappedBuffer = DirectBufferAllocator.allocate(this.heightHW * this.widthHW * 4);
		GL11.glGetTexImage(3553, 0, 6408, 5121, wrappedBuffer.getBuffer());
		Texture.lastTextureID = 0;
		GL11.glBindTexture(3553, 0);
		return wrappedBuffer;
	}

	public ImageData getImageData() {
		return this.data;
	}

	public String getPathFileName() {
		return this.pathFileName;
	}

	public boolean isDestroyed() {
		return this.id == -1;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public void setData(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			this.freeMemory();
		} else {
			this.bind();
			GL11.glTexSubImage2D(3553, 0, 0, 0, this.widthHW, this.heightHW, 6408, 5121, byteBuffer);
			if (this.data != null) {
				WrappedBuffer wrappedBuffer = this.data.getData();
				ByteBuffer byteBuffer2 = wrappedBuffer.getBuffer();
				byteBuffer.flip();
				byteBuffer2.clear();
				byteBuffer2.put(byteBuffer);
				byteBuffer2.flip();
			}

			if (USE_MIPMAP) {
			}
		}
	}

	public void setImageData(ImageData imageData) {
		this.data = imageData;
	}

	private void createTexture() {
		this.createTexture(true);
	}

	private void createTexture(boolean boolean1) {
		this.width = this.data.getWidth();
		this.height = this.data.getHeight();
		this.widthHW = this.data.getWidthHW();
		this.heightHW = this.data.getHeightHW();
		totalGraphicMemory += (long)(this.widthHW * this.heightHW * 8);
		this.solid = this.data.isSolid();
		if (!FrameLoader.bDedicated) {
			this.generateHwId(boolean1);
		}
	}

	private void generateHwId(boolean boolean1) {
		this.id = GL11.glGenTextures();
		++Texture.totalTextureID;
		GL11.glBindTexture(3553, Texture.lastTextureID = this.id);
		if (UseFiltering) {
			GL11.glTexParameteri(3553, 10241, 9729);
			GL11.glTexParameteri(3553, 10240, 9729);
		} else {
			GL11.glTexParameteri(3553, 10241, 9728);
			GL11.glTexParameteri(3553, 10240, 9728);
		}

		totalMemUsed += (float)(this.widthHW * this.heightHW * 4);
		char char1 = 6408;
		if (bUseCompression && GLContext.getCapabilities().GL_ARB_texture_compression) {
			char1 = '蓮';
		}

		GL11.glTexImage2D(3553, 0, char1, this.widthHW, this.heightHW, 0, 6408, 5121, boolean1 ? this.data.getData().getBuffer() : null);
		if (FREE_MEMORY) {
			this.data = null;
		}

		TextureIDMap.put(this.id, this.pathFileName);
	}

	public void generateMipmap() {
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		boolean boolean1 = objectInputStream.readBoolean();
		if (!boolean1) {
			this.data = (ImageData)objectInputStream.readObject();
			objectInputStream.defaultReadObject();
		} else {
			this.data = new ImageData(this.pathFileName);
			objectInputStream.defaultReadObject();
		}

		this.createTexture();
	}

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		boolean boolean1 = this.pathFileName == null;
		if (!boolean1) {
			if (this.data == null) {
				this.data = new ImageData(this, (WrappedBuffer)null);
			}

			objectOutputStream.writeBoolean(false);
			objectOutputStream.writeObject(this.data);
			objectOutputStream.defaultWriteObject();
		} else {
			objectOutputStream.writeBoolean(true);
			objectOutputStream.defaultWriteObject();
		}
	}
}
