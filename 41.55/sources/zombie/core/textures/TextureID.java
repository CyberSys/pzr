package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import zombie.IndieGL;
import zombie.SystemDisabler;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.WrappedBuffer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.interfaces.IDestroyable;


public final class TextureID extends Asset implements IDestroyable,Serializable {
	private static final long serialVersionUID = 4409253583065563738L;
	public static long totalGraphicMemory = 0L;
	public static boolean UseFiltering = false;
	public static boolean bUseCompression = true;
	public static boolean bUseCompressionOption = true;
	public static float totalMemUsed = 0.0F;
	private static boolean FREE_MEMORY = true;
	private static final HashMap TextureIDMap = new HashMap();
	protected String pathFileName;
	protected boolean solid;
	protected int width;
	protected int widthHW;
	protected int height;
	protected int heightHW;
	protected transient ImageData data;
	protected transient int id = -1;
	private int m_glMagFilter = -1;
	private int m_glMinFilter = -1;
	ArrayList alphaList;
	int referenceCount = 0;
	BooleanGrid mask;
	protected int flags = 0;
	public TextureID.TextureIDAssetParams assetParams;
	public static final IntBuffer deleteTextureIDS = BufferUtils.createIntBuffer(20);
	public static final AssetType ASSET_TYPE = new AssetType("TextureID");

	public TextureID(AssetPath assetPath, AssetManager assetManager, TextureID.TextureIDAssetParams textureIDAssetParams) {
		super(assetPath, assetManager);
		this.assetParams = textureIDAssetParams;
		this.flags = textureIDAssetParams == null ? 0 : this.assetParams.flags;
	}

	protected TextureID() {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		this.onCreated(Asset.State.READY);
	}

	public TextureID(int int1, int int2, int int3) {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = new TextureID.TextureIDAssetParams();
		this.assetParams.flags = int3;
		if ((int3 & 16) != 0) {
			if ((int3 & 4) != 0) {
				DebugLog.General.warn("FBO incompatible with COMPRESS");
				TextureID.TextureIDAssetParams textureIDAssetParams = this.assetParams;
				textureIDAssetParams.flags &= -5;
			}

			this.data = new ImageData(int1, int2, (WrappedBuffer)null);
		} else {
			this.data = new ImageData(int1, int2);
		}

		this.width = this.data.getWidth();
		this.height = this.data.getHeight();
		this.widthHW = this.data.getWidthHW();
		this.heightHW = this.data.getHeightHW();
		this.solid = this.data.isSolid();
		RenderThread.queueInvokeOnRenderContext(()->{
			this.createTexture(false);
		});
		this.onCreated(Asset.State.READY);
	}

	public TextureID(ImageData imageData) {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		this.data = imageData;
		RenderThread.invokeOnRenderContext(this::createTexture);
		this.onCreated(Asset.State.READY);
	}

	public TextureID(String string, String string2) {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		this.data = new ImageData(string, string2);
		this.pathFileName = string;
		RenderThread.invokeOnRenderContext(this::createTexture);
		this.onCreated(Asset.State.READY);
	}

	public TextureID(String string, int[] intArray) {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		this.data = new ImageData(string, intArray);
		this.pathFileName = string;
		RenderThread.invokeOnRenderContext(this::createTexture);
		this.onCreated(Asset.State.READY);
	}

	public TextureID(String string, int int1, int int2, int int3) throws Exception {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		if (string.startsWith("/")) {
			string = string.substring(1);
		}

		int int4;
		while ((int4 = string.indexOf("\\")) != -1) {
			String string2 = string.substring(0, int4);
			string = string2 + "/" + string.substring(int4 + 1);
		}

		(this.data = new ImageData(string)).makeTransp((byte)int1, (byte)int2, (byte)int3);
		if (this.alphaList == null) {
			this.alphaList = new ArrayList();
		}

		this.alphaList.add(new AlphaColorIndex(int1, int2, int3, 0));
		this.pathFileName = string;
		RenderThread.invokeOnRenderContext(this::createTexture);
		this.onCreated(Asset.State.READY);
	}

	public TextureID(String string) throws Exception {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		if (string.toLowerCase().contains(".pcx")) {
			this.data = new ImageData(string, string);
		} else {
			this.data = new ImageData(string);
		}

		if (this.data.getHeight() != -1) {
			this.pathFileName = string;
			RenderThread.invokeOnRenderContext(this::createTexture);
			this.onCreated(Asset.State.READY);
		}
	}

	public TextureID(BufferedInputStream bufferedInputStream, String string, boolean boolean1, Texture.PZFileformat pZFileformat) {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		this.data = new ImageData(bufferedInputStream, boolean1, pZFileformat);
		if (this.data.id != -1) {
			this.id = this.data.id;
			this.width = this.data.getWidth();
			this.height = this.data.getHeight();
			this.widthHW = this.data.getWidthHW();
			this.heightHW = this.data.getHeightHW();
			this.solid = this.data.isSolid();
		} else {
			if (boolean1) {
				this.mask = this.data.mask;
				this.data.mask = null;
			}

			this.createTexture();
		}

		this.pathFileName = string;
		this.onCreated(Asset.State.READY);
	}

	public TextureID(BufferedInputStream bufferedInputStream, String string, boolean boolean1) throws Exception {
		super((AssetPath)null, TextureIDAssetManager.instance);
		this.assetParams = null;
		this.data = new ImageData(bufferedInputStream, boolean1);
		if (boolean1) {
			this.mask = this.data.mask;
			this.data.mask = null;
		}

		this.pathFileName = string;
		RenderThread.invokeOnRenderContext(this::createTexture);
		this.onCreated(Asset.State.READY);
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
		if (this.id == -1 && this.data == null) {
			Texture.getErrorTexture().bind();
			return true;
		} else {
			this.debugBoundTexture();
			return this.id != -1 && this.id == Texture.lastTextureID ? false : this.bindalways();
		}
	}

	public boolean bindalways() {
		this.bindInternal();
		return true;
	}

	private void bindInternal() {
		if (this.id == -1) {
			this.generateHwId(this.data != null && this.data.data != null);
		}

		this.assignFilteringFlags();
		Texture.lastlastTextureID = Texture.lastTextureID;
		Texture.lastTextureID = this.id;
		++Texture.BindCount;
	}

	private void debugBoundTexture() {
		if (DebugOptions.instance.Checks.BoundTextures.getValue() && Texture.lastTextureID != -1) {
			int int1 = GL11.glGetInteger(34016);
			if (int1 == 33984) {
				int int2 = GL11.glGetInteger(32873);
				if (int2 != Texture.lastTextureID) {
					String string = null;
					Iterator iterator = TextureIDAssetManager.instance.getAssetTable().values().iterator();
					while (iterator.hasNext()) {
						Asset asset = (Asset)iterator.next();
						TextureID textureID = (TextureID)asset;
						if (textureID.id == Texture.lastTextureID) {
							string = textureID.getPath().getPath();
							break;
						}
					}

					DebugLog.General.error("Texture.lastTextureID %d != GL_TEXTURE_BINDING_2D %d name=%s", Texture.lastTextureID, int2, string);
				}
			}
		}
	}

	public void destroy() {
		assert Thread.currentThread() == RenderThread.RenderThread;
		if (this.id != -1) {
			if (deleteTextureIDS.position() == deleteTextureIDS.capacity()) {
				deleteTextureIDS.flip();
				GL11.glDeleteTextures(deleteTextureIDS);
				deleteTextureIDS.clear();
			}

			deleteTextureIDS.put(this.id);
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

	public void setData(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			this.freeMemory();
		} else {
			this.bind();
			GL11.glTexSubImage2D(3553, 0, 0, 0, this.widthHW, this.heightHW, 6408, 5121, byteBuffer);
			if (this.data != null) {
				MipMapLevel mipMapLevel = this.data.getData();
				ByteBuffer byteBuffer2 = mipMapLevel.getBuffer();
				byteBuffer.flip();
				byteBuffer2.clear();
				byteBuffer2.put(byteBuffer);
				byteBuffer2.flip();
			}
		}
	}

	public ImageData getImageData() {
		return this.data;
	}

	public void setImageData(ImageData imageData) {
		this.data = imageData;
		this.width = imageData.getWidth();
		this.height = imageData.getHeight();
		this.widthHW = imageData.getWidthHW();
		this.heightHW = imageData.getHeightHW();
		if (imageData.mask != null) {
			this.mask = imageData.mask;
			imageData.mask = null;
		}

		RenderThread.queueInvokeOnRenderContext(this::createTexture);
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

	private void createTexture() {
		if (this.data != null) {
			this.createTexture(true);
		}
	}

	private void createTexture(boolean boolean1) {
		if (this.id == -1) {
			this.width = this.data.getWidth();
			this.height = this.data.getHeight();
			this.widthHW = this.data.getWidthHW();
			this.heightHW = this.data.getHeightHW();
			this.solid = this.data.isSolid();
			this.generateHwId(boolean1);
		}
	}

	private void generateHwId(boolean boolean1) {
		this.id = GL11.glGenTextures();
		++Texture.totalTextureID;
		GL11.glBindTexture(3553, Texture.lastTextureID = this.id);
		SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		int int1;
		if (this.assetParams == null) {
			int1 = bUseCompressionOption ? 4 : 0;
		} else {
			int1 = this.assetParams.flags;
		}

		boolean boolean2 = (int1 & 1) != 0;
		boolean boolean3 = (int1 & 2) != 0;
		boolean boolean4 = (int1 & 16) != 0;
		boolean boolean5 = (int1 & 64) != 0 && !boolean4 && boolean1;
		boolean boolean6 = (int1 & 4) != 0;
		char char1;
		if (boolean6 && GL.getCapabilities().GL_ARB_texture_compression) {
			char1 = '蓮';
		} else {
			char1 = 6408;
		}

		this.m_glMagFilter = boolean3 ? 9728 : 9729;
		this.m_glMinFilter = boolean5 ? 9987 : (boolean2 ? 9728 : 9729);
		GL11.glTexParameteri(3553, 10241, this.m_glMinFilter);
		GL11.glTexParameteri(3553, 10240, this.m_glMagFilter);
		if ((int1 & 32) != 0) {
			GL11.glTexParameteri(3553, 10242, 33071);
			GL11.glTexParameteri(3553, 10243, 33071);
		} else {
			GL11.glTexParameteri(3553, 10242, 10497);
			GL11.glTexParameteri(3553, 10243, 10497);
		}

		if (boolean1) {
			if (boolean5) {
				PZGLUtil.checkGLErrorThrow("TextureID.mipMaps.start");
				int int2 = this.data.getMipMapCount();
				int int3 = PZMath.min(0, int2 - 1);
				int int4 = int2;
				for (int int5 = int3; int5 < int4; ++int5) {
					MipMapLevel mipMapLevel = this.data.getMipMapData(int5);
					int int6 = mipMapLevel.width;
					int int7 = mipMapLevel.height;
					totalMemUsed += (float)mipMapLevel.getDataSize();
					GL11.glTexImage2D(3553, int5 - int3, char1, int6, int7, 0, 6408, 5121, mipMapLevel.getBuffer());
					PZGLUtil.checkGLErrorThrow("TextureID.mipMaps[%d].end", int5);
				}

				PZGLUtil.checkGLErrorThrow("TextureID.mipMaps.end");
			} else {
				PZGLUtil.checkGLErrorThrow("TextureID.noMips.start");
				totalMemUsed += (float)(this.widthHW * this.heightHW * 4);
				GL11.glTexImage2D(3553, 0, char1, this.widthHW, this.heightHW, 0, 6408, 5121, this.data.getData().getBuffer());
				PZGLUtil.checkGLErrorThrow("TextureID.noMips.end");
			}
		} else {
			GL11.glTexImage2D(3553, 0, char1, this.widthHW, this.heightHW, 0, 6408, 5121, (ByteBuffer)null);
			totalMemUsed += (float)(this.widthHW * this.heightHW * 4);
		}

		if (FREE_MEMORY) {
			if (this.data != null) {
				this.data.dispose();
			}

			this.data = null;
			if (this.assetParams != null) {
				this.assetParams.subTexture = null;
				this.assetParams = null;
			}
		}

		TextureIDMap.put(this.id, this.pathFileName);
		if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
			PZGLUtil.checkGLErrorThrow("generateHwId id:%d pathFileName:%s", this.id, this.pathFileName);
		}
	}

	private void assignFilteringFlags() {
		GL11.glBindTexture(3553, this.id);
		if (this.width == 1 && this.height == 1) {
			GL11.glTexParameteri(3553, 10241, 9728);
			GL11.glTexParameteri(3553, 10240, 9728);
		} else {
			GL11.glTexParameteri(3553, 10241, this.m_glMinFilter);
			GL11.glTexParameteri(3553, 10240, this.m_glMagFilter);
			if (DebugOptions.instance.IsoSprite.NearestMagFilterAtMinZoom.getValue() && this.isMinZoomLevel() && this.m_glMagFilter != 9728) {
				GL11.glTexParameteri(3553, 10240, 9728);
			}

			if (DebugOptions.instance.IsoSprite.ForceLinearMagFilter.getValue() && this.m_glMagFilter != 9729) {
				GL11.glTexParameteri(3553, 10240, 9729);
			}

			if (DebugOptions.instance.IsoSprite.ForceNearestMagFilter.getValue() && this.m_glMagFilter != 9728) {
				GL11.glTexParameteri(3553, 10240, 9728);
			}

			if (DebugOptions.instance.IsoSprite.ForceNearestMipMapping.getValue() && this.m_glMinFilter == 9987) {
				GL11.glTexParameteri(3553, 10241, 9986);
			}

			if (DebugOptions.instance.IsoSprite.TextureWrapClampToEdge.getValue()) {
				GL11.glTexParameteri(3553, 10242, 33071);
				GL11.glTexParameteri(3553, 10243, 33071);
			}

			if (DebugOptions.instance.IsoSprite.TextureWrapRepeat.getValue()) {
				GL11.glTexParameteri(3553, 10242, 10497);
				GL11.glTexParameteri(3553, 10243, 10497);
			}

			if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
				PZGLUtil.checkGLErrorThrow("assignFilteringFlags id:%d pathFileName:%s", this.id, this.pathFileName);
			}
		}
	}

	public void setMagFilter(int int1) {
		this.m_glMagFilter = int1;
	}

	public void setMinFilter(int int1) {
		this.m_glMinFilter = int1;
	}

	public boolean hasMipMaps() {
		return this.m_glMinFilter == 9987;
	}

	private boolean isMaxZoomLevel() {
		return IndieGL.isMaxZoomLevel();
	}

	private boolean isMinZoomLevel() {
		return IndieGL.isMinZoomLevel();
	}

	public void setAssetParams(AssetManager.AssetParams assetParams) {
		this.assetParams = (TextureID.TextureIDAssetParams)assetParams;
		this.flags = this.assetParams == null ? 0 : this.assetParams.flags;
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	public static final class TextureIDAssetParams extends AssetManager.AssetParams {
		FileSystem.SubTexture subTexture;
		int flags = 0;
	}
}
