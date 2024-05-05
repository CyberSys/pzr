package zombie.core.textures;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL21;
import org.lwjgl.system.MemoryUtil;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.bucket.BucketManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.ImageUtils;
import zombie.core.utils.WrappedBuffer;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.interfaces.IDestroyable;
import zombie.interfaces.ITexture;
import zombie.iso.Vector2;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.util.StringUtils;
import zombie.util.Type;


public class Texture extends Asset implements IDestroyable,ITexture,Serializable {
	public static final HashSet nullTextures = new HashSet();
	private static final long serialVersionUID = 7472363451408935314L;
	private static final ObjectRenderEffects objRen = ObjectRenderEffects.alloc();
	public static int BindCount = 0;
	public static boolean bDoingQuad = false;
	public static float lr;
	public static float lg;
	public static float lb;
	public static float la;
	public static int lastlastTextureID = -2;
	public static int totalTextureID = 0;
	private static Texture white = null;
	private static Texture errorTexture = null;
	private static Texture mipmap = null;
	public static int lastTextureID = -1;
	public static boolean WarnFailFindTexture = true;
	private static final HashMap textures = new HashMap();
	private static final HashMap s_sharedTextureTable = new HashMap();
	private static final HashMap steamAvatarMap = new HashMap();
	public boolean flip;
	public float offsetX;
	public float offsetY;
	public boolean bindAlways;
	public float xEnd;
	public float yEnd;
	public float xStart;
	public float yStart;
	protected TextureID dataid;
	protected Mask mask;
	protected String name;
	protected boolean solid;
	protected int width;
	protected int height;
	protected int heightOrig;
	protected int widthOrig;
	private int realWidth;
	private int realHeight;
	private boolean destroyed;
	private Texture splitIconTex;
	private int splitX;
	private int splitY;
	private int splitW;
	private int splitH;
	protected FileSystem.SubTexture subTexture;
	public Texture.TextureAssetParams assetParams;
	private static final ThreadLocal pngSize = ThreadLocal.withInitial(PNGSize::new);
	public static final AssetType ASSET_TYPE = new AssetType("Texture");

	public Texture(AssetPath assetPath, AssetManager assetManager, Texture.TextureAssetParams textureAssetParams) {
		super(assetPath, assetManager);
		this.flip = false;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.bindAlways = false;
		this.xEnd = 1.0F;
		this.yEnd = 1.0F;
		this.xStart = 0.0F;
		this.yStart = 0.0F;
		this.realWidth = 0;
		this.realHeight = 0;
		this.destroyed = false;
		this.splitX = -1;
		this.assetParams = textureAssetParams;
		this.name = assetPath == null ? null : assetPath.getPath();
		if (textureAssetParams != null && textureAssetParams.subTexture != null) {
			FileSystem.SubTexture subTexture = textureAssetParams.subTexture;
			this.splitX = subTexture.m_info.x;
			this.splitY = subTexture.m_info.y;
			this.splitW = subTexture.m_info.w;
			this.splitH = subTexture.m_info.h;
			this.width = this.splitW;
			this.height = this.splitH;
			this.offsetX = (float)subTexture.m_info.ox;
			this.offsetY = (float)subTexture.m_info.oy;
			this.widthOrig = subTexture.m_info.fx;
			this.heightOrig = subTexture.m_info.fy;
			this.name = subTexture.m_info.name;
			this.subTexture = subTexture;
		}

		TextureID.TextureIDAssetParams textureIDAssetParams = new TextureID.TextureIDAssetParams();
		if (this.assetParams != null && this.assetParams.subTexture != null) {
			textureIDAssetParams.subTexture = this.assetParams.subTexture;
			String string = textureIDAssetParams.subTexture.m_pack_name;
			String string2 = textureIDAssetParams.subTexture.m_page_name;
			FileSystem fileSystem = this.getAssetManager().getOwner().getFileSystem();
			textureIDAssetParams.flags = fileSystem.getTexturePackFlags(string);
			textureIDAssetParams.flags |= fileSystem.getTexturePackAlpha(string, string2) ? 8 : 0;
			AssetPath assetPath2 = new AssetPath("@pack@/" + string + "/" + string2);
			this.dataid = (TextureID)TextureIDAssetManager.instance.load(assetPath2, textureIDAssetParams);
		} else {
			if (this.assetParams == null) {
				textureIDAssetParams.flags |= TextureID.bUseCompressionOption ? 4 : 0;
			} else {
				textureIDAssetParams.flags = this.assetParams.flags;
			}

			this.dataid = (TextureID)this.getAssetManager().getOwner().get(TextureID.ASSET_TYPE).load(this.getPath(), textureIDAssetParams);
		}

		this.onCreated(Asset.State.EMPTY);
		if (this.dataid != null) {
			this.addDependency(this.dataid);
		}
	}

	public Texture(TextureID textureID, String string) {
		super((AssetPath)null, TextureAssetManager.instance);
		this.flip = false;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.bindAlways = false;
		this.xEnd = 1.0F;
		this.yEnd = 1.0F;
		this.xStart = 0.0F;
		this.yStart = 0.0F;
		this.realWidth = 0;
		this.realHeight = 0;
		this.destroyed = false;
		this.splitX = -1;
		this.dataid = textureID;
		++this.dataid.referenceCount;
		if (textureID.isReady()) {
			this.solid = this.dataid.solid;
			this.width = textureID.width;
			this.height = textureID.height;
			this.xEnd = (float)this.width / (float)textureID.widthHW;
			this.yEnd = (float)this.height / (float)textureID.heightHW;
		} else {
			assert false;
		}

		this.name = string;
		this.assetParams = null;
		this.onCreated(textureID.getState());
		this.addDependency(textureID);
	}

	public Texture(String string) throws Exception {
		this(new TextureID(string), string);
		this.setUseAlphaChannel(true);
	}

	public Texture(String string, BufferedInputStream bufferedInputStream, boolean boolean1, Texture.PZFileformat pZFileformat) {
		this(new TextureID(bufferedInputStream, string, boolean1, pZFileformat), string);
		if (boolean1 && this.dataid.mask != null) {
			this.createMask(this.dataid.mask);
			this.dataid.mask = null;
			this.dataid.data = null;
		}
	}

	public Texture(String string, BufferedInputStream bufferedInputStream, boolean boolean1) throws Exception {
		this(new TextureID(bufferedInputStream, string, boolean1), string);
		if (boolean1) {
			this.createMask(this.dataid.mask);
			this.dataid.mask = null;
			this.dataid.data = null;
		}
	}

	public Texture(String string, boolean boolean1, boolean boolean2) throws Exception {
		this(new TextureID(string), string);
		this.setUseAlphaChannel(boolean2);
		if (boolean1) {
			this.dataid.data = null;
		}
	}

	public Texture(String string, String string2) {
		this(new TextureID(string, string2), string);
		this.setUseAlphaChannel(true);
	}

	public Texture(String string, int[] intArray) {
		this(new TextureID(string, intArray), string);
		if (string.contains("drag")) {
			boolean boolean1 = false;
		}

		this.setUseAlphaChannel(true);
	}

	public Texture(String string, boolean boolean1) throws Exception {
		this(new TextureID(string), string);
		this.setUseAlphaChannel(boolean1);
	}

	public Texture(int int1, int int2, String string, int int3) {
		this(new TextureID(int1, int2, int3), string);
	}

	public Texture(int int1, int int2, int int3) {
		this((TextureID)(new TextureID(int1, int2, int3)), (String)null);
	}

	public Texture(String string, int int1, int int2, int int3) throws Exception {
		this(new TextureID(string, int1, int2, int3), string);
	}

	public Texture(Texture texture) {
		this(texture.dataid, texture.name + "(copy)");
		this.width = texture.width;
		this.height = texture.height;
		this.name = texture.name;
		this.xStart = texture.xStart;
		this.yStart = texture.yStart;
		this.xEnd = texture.xEnd;
		this.yEnd = texture.yEnd;
		this.solid = texture.solid;
	}

	public Texture() {
		super((AssetPath)null, TextureAssetManager.instance);
		this.flip = false;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.bindAlways = false;
		this.xEnd = 1.0F;
		this.yEnd = 1.0F;
		this.xStart = 0.0F;
		this.yStart = 0.0F;
		this.realWidth = 0;
		this.realHeight = 0;
		this.destroyed = false;
		this.splitX = -1;
		this.assetParams = null;
		this.onCreated(Asset.State.EMPTY);
	}

	public static String processFilePath(String string) {
		string = string.replaceAll("\\\\", "/");
		return string;
	}

	public static void bindNone() {
		IndieGL.glDisable(3553);
		lastTextureID = -1;
		--BindCount;
	}

	public static Texture getWhite() {
		if (white == null) {
			white = new Texture(32, 32, "white", 0);
			RenderThread.invokeOnRenderContext(()->{
				GL11.glBindTexture(3553, lastTextureID = white.getID());
				GL11.glTexParameteri(3553, 10241, 9728);
				GL11.glTexParameteri(3553, 10240, 9728);
				ByteBuffer byteBuffer = MemoryUtil.memAlloc(white.width * white.height * 4);
				for (int int1 = 0; int1 < white.width * white.height * 4; ++int1) {
					byteBuffer.put((byte)-1);
				}

				byteBuffer.flip();
				GL11.glTexImage2D(3553, 0, 6408, white.width, white.height, 0, 6408, 5121, byteBuffer);
				MemoryUtil.memFree(byteBuffer);
			});

			s_sharedTextureTable.put("white.png", white);
			s_sharedTextureTable.put("media/white.png", white);
			s_sharedTextureTable.put("media/ui/white.png", white);
		}

		return white;
	}

	public static Texture getErrorTexture() {
		if (errorTexture == null) {
			errorTexture = new Texture(32, 32, "EngineErrorTexture", 0);
			RenderThread.invokeOnRenderContext(()->{
				GL11.glBindTexture(3553, lastTextureID = errorTexture.getID());
				GL11.glTexParameteri(3553, 10241, 9728);
				GL11.glTexParameteri(3553, 10240, 9728);
				byte byte1 = 4;
				ByteBuffer byteBuffer = MemoryUtil.memAlloc(errorTexture.width * errorTexture.height * byte1);
				byteBuffer.position(errorTexture.width * errorTexture.height * byte1);
				int int1 = errorTexture.width * byte1;
				boolean boolean1 = true;
				boolean boolean2 = boolean1;
				byte byte2 = 8;
				int int2 = errorTexture.width / byte2;
				for (int int3 = 0; int3 < byte2 * byte2; ++int3) {
					int int4 = int3 / byte2;
					int int5 = int3 % byte2;
					if (int4 > 0 && int5 == 0) {
						boolean1 = !boolean1;
						boolean2 = boolean1;
					}

					int int6 = boolean2 ? -16776961 : -1;
					boolean2 = !boolean2;
					for (int int7 = 0; int7 < int2; ++int7) {
						for (int int8 = 0; int8 < int2; ++int8) {
							byteBuffer.putInt((int4 * int2 + int7) * int1 + (int5 * int2 + int8) * byte1, int6);
						}
					}
				}

				byteBuffer.flip();
				GL11.glTexImage2D(3553, 0, 6408, errorTexture.width, errorTexture.height, 0, 6408, 5121, byteBuffer);
				MemoryUtil.memFree(byteBuffer);
			});

			s_sharedTextureTable.put("EngineErrorTexture.png", errorTexture);
		}

		return errorTexture;
	}

	private static void initEngineMipmapTextureLevel(int int1, int int2, int int3, int int4, int int5, int int6, int int7) {
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(int2 * int3 * 4);
		MemoryUtil.memSet(byteBuffer, 255);
		for (int int8 = 0; int8 < int2 * int3; ++int8) {
			byteBuffer.put((byte)(int4 & 255));
			byteBuffer.put((byte)(int5 & 255));
			byteBuffer.put((byte)(int6 & 255));
			byteBuffer.put((byte)(int7 & 255));
		}

		byteBuffer.flip();
		GL11.glTexImage2D(3553, int1, 6408, int2, int3, 0, 6408, 5121, byteBuffer);
		MemoryUtil.memFree(byteBuffer);
	}

	public static Texture getEngineMipmapTexture() {
		if (mipmap == null) {
			mipmap = new Texture(256, 256, "EngineMipmapTexture", 0);
			mipmap.dataid.setMinFilter(9984);
			RenderThread.invokeOnRenderContext(()->{
				GL11.glBindTexture(3553, lastTextureID = mipmap.getID());
				GL11.glTexParameteri(3553, 10241, 9984);
				GL11.glTexParameteri(3553, 10240, 9728);
				GL11.glTexParameteri(3553, 33085, 6);
				initEngineMipmapTextureLevel(0, mipmap.width, mipmap.height, 255, 0, 0, 255);
				initEngineMipmapTextureLevel(1, mipmap.width / 2, mipmap.height / 2, 0, 255, 0, 255);
				initEngineMipmapTextureLevel(2, mipmap.width / 4, mipmap.height / 4, 0, 0, 255, 255);
				initEngineMipmapTextureLevel(3, mipmap.width / 8, mipmap.height / 8, 255, 255, 0, 255);
				initEngineMipmapTextureLevel(4, mipmap.width / 16, mipmap.height / 16, 255, 0, 255, 255);
				initEngineMipmapTextureLevel(5, mipmap.width / 32, mipmap.height / 32, 0, 0, 0, 255);
				initEngineMipmapTextureLevel(6, mipmap.width / 64, mipmap.height / 64, 255, 255, 255, 255);
			});
		}

		return mipmap;
	}

	public static void clearTextures() {
		textures.clear();
	}

	public static Texture getSharedTexture(String string) {
		byte byte1 = 0;
		int int1 = byte1 | (TextureID.bUseCompression ? 4 : 0);
		return getSharedTexture(string, int1);
	}

	public static Texture getSharedTexture(String string, int int1) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return null;
		} else {
			try {
				return getSharedTextureInternal(string, int1);
			} catch (Exception exception) {
				return null;
			}
		}
	}

	public static Texture trygetTexture(String string) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return null;
		} else {
			Texture texture = getSharedTexture(string);
			if (texture == null) {
				String string2 = "media/textures/" + string;
				if (!string.endsWith(".png")) {
					string2 = string2 + ".png";
				}

				texture = (Texture)s_sharedTextureTable.get(string2);
				if (texture != null) {
					return texture;
				}

				String string3 = ZomboidFileSystem.instance.getString(string2);
				if (!string3.equals(string2)) {
					byte byte1 = 0;
					int int1 = byte1 | (TextureID.bUseCompression ? 4 : 0);
					Texture.TextureAssetParams textureAssetParams = new Texture.TextureAssetParams();
					textureAssetParams.flags = int1;
					texture = (Texture)TextureAssetManager.instance.load(new AssetPath(string3), textureAssetParams);
					BucketManager.Shared().AddTexture(string2, texture);
					setSharedTextureInternal(string2, texture);
				}
			}

			return texture;
		}
	}

	private static void onTextureFileChanged(String string) {
		DebugLog.General.println("Texture.onTextureFileChanged> " + string);
	}

	public static void onTexturePacksChanged() {
		nullTextures.clear();
		s_sharedTextureTable.clear();
	}

	private static void setSharedTextureInternal(String string, Texture texture) {
		s_sharedTextureTable.put(string, texture);
	}

	private static Texture getSharedTextureInternal(String string, int int1) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return null;
		} else if (nullTextures.contains(string)) {
			return null;
		} else {
			Texture texture = (Texture)s_sharedTextureTable.get(string);
			if (texture != null) {
				return texture;
			} else {
				Texture texture2;
				Texture.TextureAssetParams textureAssetParams;
				if (!string.endsWith(".txt")) {
					String string2 = string;
					if (string.endsWith(".pcx") || string.endsWith(".png")) {
						string2 = string.substring(0, string.lastIndexOf("."));
					}

					string2 = string2.substring(string.lastIndexOf("/") + 1);
					texture2 = TexturePackPage.getTexture(string2);
					if (texture2 != null) {
						setSharedTextureInternal(string, texture2);
						return texture2;
					}

					FileSystem.SubTexture subTexture = (FileSystem.SubTexture)GameWindow.texturePackTextures.get(string2);
					if (subTexture != null) {
						textureAssetParams = new Texture.TextureAssetParams();
						textureAssetParams.subTexture = subTexture;
						String string3 = "@pack/" + subTexture.m_pack_name + "/" + subTexture.m_page_name + "/" + subTexture.m_info.name;
						Texture texture3 = (Texture)TextureAssetManager.instance.load(new AssetPath(string3), textureAssetParams);
						if (texture3 == null) {
							nullTextures.add(string);
						} else {
							setSharedTextureInternal(string, texture3);
						}

						return texture3;
					}
				}

				if (TexturePackPage.subTextureMap.containsKey(string)) {
					return (Texture)TexturePackPage.subTextureMap.get(string);
				} else {
					FileSystem.SubTexture subTexture2 = (FileSystem.SubTexture)GameWindow.texturePackTextures.get(string);
					if (subTexture2 != null) {
						Texture.TextureAssetParams textureAssetParams2 = new Texture.TextureAssetParams();
						textureAssetParams2.subTexture = subTexture2;
						String string4 = "@pack/" + subTexture2.m_pack_name + "/" + subTexture2.m_page_name + "/" + subTexture2.m_info.name;
						Texture texture4 = (Texture)TextureAssetManager.instance.load(new AssetPath(string4), textureAssetParams2);
						if (texture4 == null) {
							nullTextures.add(string);
						} else {
							setSharedTextureInternal(string, texture4);
						}

						return texture4;
					} else if (BucketManager.Shared().HasTexture(string)) {
						texture2 = BucketManager.Shared().getTexture(string);
						setSharedTextureInternal(string, texture2);
						return texture2;
					} else if (StringUtils.endsWithIgnoreCase(string, ".pcx")) {
						nullTextures.add(string);
						return null;
					} else if (string.lastIndexOf(46) == -1) {
						nullTextures.add(string);
						return null;
					} else {
						String string5 = ZomboidFileSystem.instance.getString(string);
						boolean boolean1 = string5 != string;
						if (!boolean1 && !(new File(string5)).exists()) {
							nullTextures.add(string);
							return null;
						} else {
							textureAssetParams = new Texture.TextureAssetParams();
							textureAssetParams.flags = int1;
							Texture texture5 = (Texture)TextureAssetManager.instance.load(new AssetPath(string5), textureAssetParams);
							BucketManager.Shared().AddTexture(string, texture5);
							setSharedTextureInternal(string, texture5);
							return texture5;
						}
					}
				}
			}
		}
	}

	public static Texture getSharedTexture(String string, String string2) {
		if (BucketManager.Shared().HasTexture(string + string2)) {
			return BucketManager.Shared().getTexture(string + string2);
		} else {
			Texture texture = new Texture(string, string2);
			BucketManager.Shared().AddTexture(string + string2, texture);
			return texture;
		}
	}

	public static Texture getSharedTexture(String string, int[] intArray, String string2) {
		if (BucketManager.Shared().HasTexture(string + string2)) {
			return BucketManager.Shared().getTexture(string + string2);
		} else {
			Texture texture = new Texture(string, intArray);
			BucketManager.Shared().AddTexture(string + string2, texture);
			return texture;
		}
	}

	public static Texture getTexture(String string) {
		if (!string.contains(".txt")) {
			String string2 = string.replace(".png", "");
			string2 = string2.replace(".pcx", "");
			string2 = string2.substring(string.lastIndexOf("/") + 1);
			Texture texture = TexturePackPage.getTexture(string2);
			if (texture != null) {
				return texture;
			}
		}

		if (BucketManager.Active().HasTexture(string)) {
			return BucketManager.Active().getTexture(string);
		} else {
			try {
				Texture texture2 = new Texture(string);
				BucketManager.Active().AddTexture(string, texture2);
				return texture2;
			} catch (Exception exception) {
				return null;
			}
		}
	}

	public static Texture getSteamAvatar(long long1) {
		if (steamAvatarMap.containsKey(long1)) {
			return (Texture)steamAvatarMap.get(long1);
		} else {
			TextureID textureID = TextureID.createSteamAvatar(long1);
			if (textureID == null) {
				return null;
			} else {
				Texture texture = new Texture(textureID, "SteamAvatar" + SteamUtils.convertSteamIDToString(long1));
				steamAvatarMap.put(long1, texture);
				return texture;
			}
		}
	}

	public static void steamAvatarChanged(long long1) {
		Texture texture = (Texture)steamAvatarMap.get(long1);
		if (texture != null) {
			steamAvatarMap.remove(long1);
		}
	}

	public static void forgetTexture(String string) {
		BucketManager.Shared().forgetTexture(string);
		s_sharedTextureTable.remove(string);
	}

	public static void reload(String string) {
		if (string != null && !string.isEmpty()) {
			Texture texture = (Texture)s_sharedTextureTable.get(string);
			if (texture == null) {
				texture = (Texture)Type.tryCastTo((Asset)TextureAssetManager.instance.getAssetTable().get(string), Texture.class);
				if (texture == null) {
					return;
				}
			}

			texture.reloadFromFile(string);
		}
	}

	public static int[] flipPixels(int[] intArray, int int1, int int2) {
		int[] intArray2 = null;
		if (intArray != null) {
			intArray2 = new int[int1 * int2];
			for (int int3 = 0; int3 < int2; ++int3) {
				for (int int4 = 0; int4 < int1; ++int4) {
					intArray2[(int2 - int3 - 1) * int1 + int4] = intArray[int3 * int1 + int4];
				}
			}
		}

		return intArray2;
	}

	public void reloadFromFile(String string) {
		if (this.dataid != null) {
			TextureID.TextureIDAssetParams textureIDAssetParams = new TextureID.TextureIDAssetParams();
			textureIDAssetParams.flags = this.dataid.flags;
			this.dataid.getAssetManager().reload(this.dataid, textureIDAssetParams);
		} else if (string != null && !string.isEmpty()) {
			File file = new File(string);
			if (file.exists()) {
				try {
					ImageData imageData = new ImageData(file.getAbsolutePath());
					if (imageData.getWidthHW() != this.getWidthHW() || imageData.getHeightHW() != this.getHeightHW()) {
						return;
					}

					RenderThread.invokeOnRenderContext(imageData, (stringx)->{
						GL11.glBindTexture(3553, lastTextureID = this.dataid.id);
						short file = 6408;
						GL11.glTexImage2D(3553, 0, file, this.getWidthHW(), this.getHeightHW(), 0, 6408, 5121, stringx.getData().getBuffer());
					});
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable, string);
				}
			}
		}
	}

	public void bind() {
		this.bind(3553);
	}

	public void bind(int int1) {
		if (!this.isDestroyed() && this.isValid() && this.isReady()) {
			if (this.bindAlways) {
				this.dataid.bindalways();
			} else {
				this.dataid.bind();
			}
		} else {
			getErrorTexture().bind(int1);
		}
	}

	public void copyMaskRegion(Texture texture, int int1, int int2, int int3, int int4) {
		if (texture.getMask() != null) {
			new Mask(texture, this, int1, int2, int3, int4);
		}
	}

	public void createMask() {
		new Mask(this);
	}

	public void createMask(boolean[] booleanArray) {
		new Mask(this, booleanArray);
	}

	public void createMask(BooleanGrid booleanGrid) {
		new Mask(this, booleanGrid);
	}

	public void createMask(WrappedBuffer wrappedBuffer) {
		new Mask(this, wrappedBuffer);
	}

	public void destroy() {
		if (!this.destroyed) {
			if (this.dataid != null && --this.dataid.referenceCount == 0) {
				if (lastTextureID == this.dataid.id) {
					lastTextureID = -1;
				}

				this.dataid.destroy();
			}

			this.destroyed = true;
		}
	}

	public boolean equals(Texture texture) {
		return texture.xStart == this.xStart && texture.xEnd == this.xEnd && texture.yStart == this.yStart && texture.yEnd == this.yEnd && texture.width == this.width && texture.height == this.height && texture.solid == this.solid && (this.dataid == null || texture.dataid == null || texture.dataid.pathFileName == null || this.dataid.pathFileName == null || texture.dataid.pathFileName.equals(this.dataid.pathFileName));
	}

	public WrappedBuffer getData() {
		return this.dataid.getData();
	}

	public void setData(ByteBuffer byteBuffer) {
		this.dataid.setData(byteBuffer);
	}

	public int getHeight() {
		if (!this.isReady() && this.height <= 0 && !(this instanceof SmartTexture)) {
			this.syncReadSize();
		}

		return this.height;
	}

	public void setHeight(int int1) {
		this.height = int1;
	}

	public int getHeightHW() {
		if (!this.isReady() && this.height <= 0 && !(this instanceof SmartTexture)) {
			this.syncReadSize();
		}

		return this.dataid.heightHW;
	}

	public int getHeightOrig() {
		return this.heightOrig == 0 ? this.getHeight() : this.heightOrig;
	}

	public int getID() {
		return this.dataid.id;
	}

	public Mask getMask() {
		return this.mask;
	}

	public void setMask(Mask mask) {
		this.mask = mask;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		if (string != null) {
			if (string.equals(this.name)) {
				if (!textures.containsKey(string)) {
					textures.put(string, this);
				}
			} else {
				if (textures.containsKey(string)) {
				}

				if (textures.containsKey(this.name)) {
					textures.remove(this.name);
				}

				this.name = string;
				textures.put(string, this);
			}
		}
	}

	public TextureID getTextureId() {
		return this.dataid;
	}

	public boolean getUseAlphaChannel() {
		return !this.solid;
	}

	public void setUseAlphaChannel(boolean boolean1) {
		this.dataid.solid = this.solid = !boolean1;
	}

	public int getWidth() {
		if (!this.isReady() && this.width <= 0 && !(this instanceof SmartTexture)) {
			this.syncReadSize();
		}

		return this.width;
	}

	public void setWidth(int int1) {
		this.width = int1;
	}

	public int getWidthHW() {
		if (!this.isReady() && this.width <= 0 && !(this instanceof SmartTexture)) {
			this.syncReadSize();
		}

		return this.dataid.widthHW;
	}

	public int getWidthOrig() {
		return this.widthOrig == 0 ? this.getWidth() : this.widthOrig;
	}

	public float getXEnd() {
		return this.xEnd;
	}

	public float getXStart() {
		return this.xStart;
	}

	public float getYEnd() {
		return this.yEnd;
	}

	public float getYStart() {
		return this.yStart;
	}

	public float getOffsetX() {
		return this.offsetX;
	}

	public void setOffsetX(int int1) {
		this.offsetX = (float)int1;
	}

	public float getOffsetY() {
		return this.offsetY;
	}

	public void setOffsetY(int int1) {
		this.offsetY = (float)int1;
	}

	public boolean isCollisionable() {
		return this.mask != null;
	}

	public boolean isDestroyed() {
		return this.destroyed;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public boolean isValid() {
		return this.dataid != null;
	}

	public void makeTransp(int int1, int int2, int int3) {
		this.setAlphaForeach(int1, int2, int3, 0);
	}

	public void render(float float1, float float2, float float3, float float4) {
		this.render(float1, float2, float3, float4, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
	}

	public void render(float float1, float float2) {
		this.render(float1, float2, (float)this.width, (float)this.height, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
	}

	public void render(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		float1 += this.offsetX;
		float2 += this.offsetY;
		SpriteRenderer.instance.render(this, float1, float2, float3, float4, float5, float6, float7, float8, consumer);
	}

	public void render(ObjectRenderEffects objectRenderEffects, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		float float9 = this.offsetX + float1;
		float float10 = this.offsetY + float2;
		objRen.x1 = (double)float9 + objectRenderEffects.x1 * (double)float3;
		objRen.y1 = (double)float10 + objectRenderEffects.y1 * (double)float4;
		objRen.x2 = (double)(float9 + float3) + objectRenderEffects.x2 * (double)float3;
		objRen.y2 = (double)float10 + objectRenderEffects.y2 * (double)float4;
		objRen.x3 = (double)(float9 + float3) + objectRenderEffects.x3 * (double)float3;
		objRen.y3 = (double)(float10 + float4) + objectRenderEffects.y3 * (double)float4;
		objRen.x4 = (double)float9 + objectRenderEffects.x4 * (double)float3;
		objRen.y4 = (double)(float10 + float4) + objectRenderEffects.y4 * (double)float4;
		SpriteRenderer.instance.render(this, objRen.x1, objRen.y1, objRen.x2, objRen.y2, objRen.x3, objRen.y3, objRen.x4, objRen.y4, float5, float6, float7, float8, consumer);
	}

	public void rendershader2(float float1, float float2, float float3, float float4, int int1, int int2, int int3, int int4, float float5, float float6, float float7, float float8) {
		if (float8 != 0.0F) {
			float float9 = (float)int1 / (float)this.getWidthHW();
			float float10 = (float)int2 / (float)this.getHeightHW();
			float float11 = (float)(int1 + int3) / (float)this.getWidthHW();
			float float12 = (float)(int2 + int4) / (float)this.getHeightHW();
			if (this.flip) {
				float float13 = float11;
				float11 = float9;
				float9 = float13;
				float1 += (float)this.widthOrig - this.offsetX - (float)this.width;
				float2 += this.offsetY;
			} else {
				float1 += this.offsetX;
				float2 += this.offsetY;
			}

			if (float5 > 1.0F) {
				float5 = 1.0F;
			}

			if (float6 > 1.0F) {
				float6 = 1.0F;
			}

			if (float7 > 1.0F) {
				float7 = 1.0F;
			}

			if (float8 > 1.0F) {
				float8 = 1.0F;
			}

			if (float5 < 0.0F) {
				float5 = 0.0F;
			}

			if (float6 < 0.0F) {
				float6 = 0.0F;
			}

			if (float7 < 0.0F) {
				float7 = 0.0F;
			}

			if (float8 < 0.0F) {
				float8 = 0.0F;
			}

			if (!(float1 + float3 <= 0.0F)) {
				if (!(float2 + float4 <= 0.0F)) {
					if (!(float1 >= (float)Core.getInstance().getScreenWidth())) {
						if (!(float2 >= (float)Core.getInstance().getScreenHeight())) {
							lr = float5;
							lg = float6;
							lb = float7;
							la = float8;
							SpriteRenderer.instance.render(this, float1, float2, float3, float4, float5, float6, float7, float8, float9, float12, float11, float12, float11, float10, float9, float10);
						}
					}
				}
			}
		}
	}

	public void renderdiamond(float float1, float float2, float float3, float float4, int int1, int int2, int int3, int int4) {
		SpriteRenderer.instance.render((Texture)null, float1, float2, float1 + float3 / 2.0F, float2 - float4 / 2.0F, float1 + float3, float2, float1 + float3 / 2.0F, float2 + float4 / 2.0F, int1, int2, int3, int4);
	}

	public void renderwallnw(float float1, float float2, float float3, float float4, int int1, int int2, int int3, int int4, int int5, int int6) {
		lr = -1.0F;
		lg = -1.0F;
		lb = -1.0F;
		la = -1.0F;
		if (this.flip) {
			float1 += (float)this.widthOrig - this.offsetX - (float)this.width;
			float2 += this.offsetY;
		} else {
			float1 += this.offsetX;
			float2 += this.offsetY;
		}

		int int7 = Core.TileScale;
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
			int4 = -65536;
			int2 = -65536;
			int3 = -65536;
			int1 = -65536;
		}

		float float5 = float1 - float3 / 2.0F - 0.0F;
		float float6 = float2 - (float)(96 * int7) + float4 / 2.0F - 1.0F - 0.0F;
		float float7 = float1 + 0.0F;
		float float8 = float2 - (float)(96 * int7) - 2.0F - 0.0F;
		float float9 = float1 + 0.0F;
		float float10 = float2 + 4.0F + 0.0F;
		float float11 = float1 - float3 / 2.0F - 0.0F;
		float float12 = float2 + float4 / 2.0F + 4.0F + 0.0F;
		SpriteRenderer.instance.render(this, float5, float6, float7, float8, float9, float10, float11, float12, int4, int3, int1, int2);
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
			int6 = -256;
			int5 = -256;
			int3 = -256;
			int1 = -256;
		}

		float5 = float1 - 0.0F;
		float6 = float2 - (float)(96 * int7) - 0.0F;
		float7 = float1 + float3 / 2.0F + 0.0F;
		float8 = float2 - (float)(96 * int7) + float4 / 2.0F - 0.0F;
		float9 = float1 + float3 / 2.0F + 0.0F;
		float10 = float2 + float4 / 2.0F + 5.0F + 0.0F;
		float11 = float1 - 0.0F;
		float12 = float2 + 5.0F + 0.0F;
		SpriteRenderer.instance.render(this, float5, float6, float7, float8, float9, float10, float11, float12, int3, int6, int5, int1);
	}

	public void renderwallw(float float1, float float2, float float3, float float4, int int1, int int2, int int3, int int4) {
		lr = -1.0F;
		lg = -1.0F;
		lb = -1.0F;
		la = -1.0F;
		if (this.flip) {
			float1 += (float)this.widthOrig - this.offsetX - (float)this.width;
			float2 += this.offsetY;
		} else {
			float1 += this.offsetX;
			float2 += this.offsetY;
		}

		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
			int2 = -16711936;
			int1 = -16711936;
			int4 = -16728064;
			int3 = -16728064;
		}

		int int5 = Core.TileScale;
		float float5 = float1 - float3 / 2.0F - 0.0F;
		float float6 = float2 - (float)(96 * int5) + float4 / 2.0F - 1.0F - 0.0F;
		float float7 = float1 + (float)int5 + 0.0F;
		float float8 = float2 - (float)(96 * int5) - 3.0F - 0.0F;
		float float9 = float1 + (float)int5 + 0.0F;
		float float10 = float2 + 3.0F + 0.0F;
		float float11 = float1 - float3 / 2.0F - 0.0F;
		float float12 = float2 + float4 / 2.0F + 4.0F + 0.0F;
		SpriteRenderer.instance.render(this, float5, float6, float7, float8, float9, float10, float11, float12, int4, int3, int1, int2);
	}

	public void renderwalln(float float1, float float2, float float3, float float4, int int1, int int2, int int3, int int4) {
		lr = -1.0F;
		lg = -1.0F;
		lb = -1.0F;
		la = -1.0F;
		if (this.flip) {
			float1 += (float)this.widthOrig - this.offsetX - (float)this.width;
			float2 += this.offsetY;
		} else {
			float1 += this.offsetX;
			float2 += this.offsetY;
		}

		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
			int2 = -16776961;
			int1 = -16776961;
			int4 = -16777024;
			int3 = -16777024;
		}

		int int5 = Core.TileScale;
		float float5 = float1 - 6.0F - 0.0F;
		float float6 = float2 - (float)(96 * int5) - 3.0F - 0.0F;
		float float7 = float1 + float3 / 2.0F + 0.0F;
		float float8 = float2 - (float)(96 * int5) + float4 / 2.0F - 0.0F;
		float float9 = float1 + float3 / 2.0F + 0.0F;
		float float10 = float2 + float4 / 2.0F + 5.0F + 0.0F;
		float float11 = float1 - 6.0F - 0.0F;
		float float12 = float2 + 2.0F + 0.0F;
		SpriteRenderer.instance.render(this, float5, float6, float7, float8, float9, float10, float11, float12, int3, int4, int2, int1);
	}

	public void renderstrip(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, Consumer consumer) {
		try {
			if (float4 <= 0.0F) {
				return;
			}

			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			if (float2 > 1.0F) {
				float2 = 1.0F;
			}

			if (float3 > 1.0F) {
				float3 = 1.0F;
			}

			if (float4 > 1.0F) {
				float4 = 1.0F;
			}

			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			if (float2 < 0.0F) {
				float2 = 0.0F;
			}

			if (float3 < 0.0F) {
				float3 = 0.0F;
			}

			if (float4 < 0.0F) {
				float4 = 0.0F;
			}

			float float5 = this.getXStart();
			float float6 = this.getYStart();
			float float7 = this.getXEnd();
			float float8 = this.getYEnd();
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			SpriteRenderer.instance.renderi(this, int1, int2, int3, int4, float1, float2, float3, float4, consumer);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void setAlphaForeach(int int1, int int2, int int3, int int4) {
		ImageData imageData = this.getTextureId().getImageData();
		if (imageData != null) {
			imageData.makeTransp((byte)int1, (byte)int2, (byte)int3, (byte)int4);
		} else {
			WrappedBuffer wrappedBuffer = this.getData();
			this.setData(ImageUtils.makeTransp(wrappedBuffer.getBuffer(), int1, int2, int3, int4, this.getWidthHW(), this.getHeightHW()));
			wrappedBuffer.dispose();
		}

		AlphaColorIndex alphaColorIndex = new AlphaColorIndex(int1, int2, int3, int4);
		if (this.dataid.alphaList == null) {
			this.dataid.alphaList = new ArrayList();
		}

		if (!this.dataid.alphaList.contains(alphaColorIndex)) {
			this.dataid.alphaList.add(alphaColorIndex);
		}
	}

	public void setCustomizedTexture() {
		this.dataid.pathFileName = null;
	}

	public void setNameOnly(String string) {
		this.name = string;
	}

	public void setRegion(int int1, int int2, int int3, int int4) {
		if (int1 >= 0 && int1 <= this.getWidthHW()) {
			if (int2 >= 0 && int2 <= this.getHeightHW()) {
				if (int3 > 0) {
					if (int4 > 0) {
						if (int3 + int1 > this.getWidthHW()) {
							int3 = this.getWidthHW() - int1;
						}

						if (int4 > this.getHeightHW()) {
							int4 = this.getHeightHW() - int2;
						}

						this.xStart = (float)int1 / (float)this.getWidthHW();
						this.yStart = (float)int2 / (float)this.getHeightHW();
						this.xEnd = (float)(int1 + int3) / (float)this.getWidthHW();
						this.yEnd = (float)(int2 + int4) / (float)this.getHeightHW();
						this.width = int3;
						this.height = int4;
					}
				}
			}
		}
	}

	public Texture splitIcon() {
		if (this.splitIconTex == null) {
			if (!this.dataid.isReady()) {
				this.splitIconTex = new Texture();
				this.splitIconTex.name = this.name + "_Icon";
				this.splitIconTex.dataid = this.dataid;
				++this.splitIconTex.dataid.referenceCount;
				this.splitIconTex.splitX = this.splitX;
				this.splitIconTex.splitY = this.splitY;
				this.splitIconTex.splitW = this.splitW;
				this.splitIconTex.splitH = this.splitH;
				this.splitIconTex.width = this.width;
				this.splitIconTex.height = this.height;
				this.splitIconTex.offsetX = 0.0F;
				this.splitIconTex.offsetY = 0.0F;
				this.splitIconTex.widthOrig = 0;
				this.splitIconTex.heightOrig = 0;
				this.splitIconTex.addDependency(this.dataid);
				setSharedTextureInternal(this.splitIconTex.name, this.splitIconTex);
				return this.splitIconTex;
			}

			this.splitIconTex = new Texture(this.getTextureId(), this.name + "_Icon");
			float float1 = this.xStart * (float)this.getWidthHW();
			float float2 = this.yStart * (float)this.getHeightHW();
			float float3 = this.xEnd * (float)this.getWidthHW() - float1;
			float float4 = this.yEnd * (float)this.getHeightHW() - float2;
			this.splitIconTex.setRegion((int)float1, (int)float2, (int)float3, (int)float4);
			this.splitIconTex.offsetX = 0.0F;
			this.splitIconTex.offsetY = 0.0F;
			setSharedTextureInternal(this.name + "_Icon", this.splitIconTex);
		}

		return this.splitIconTex;
	}

	public Texture split(int int1, int int2, int int3, int int4) {
		Texture texture = new Texture(this.getTextureId(), this.name + "_" + int1 + "_" + int2);
		this.splitX = int1;
		this.splitY = int2;
		this.splitW = int3;
		this.splitH = int4;
		if (this.getTextureId().isReady()) {
			texture.setRegion(int1, int2, int3, int4);
		} else {
			assert false;
		}

		return texture;
	}

	public Texture split(String string, int int1, int int2, int int3, int int4) {
		Texture texture = new Texture(this.getTextureId(), string);
		texture.setRegion(int1, int2, int3, int4);
		return texture;
	}

	public Texture[] split(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
		Texture[] textureArray = new Texture[int3 * int4];
		for (int int9 = 0; int9 < int3; ++int9) {
			for (int int10 = 0; int10 < int4; ++int10) {
				textureArray[int10 + int9 * int4] = new Texture(this.getTextureId(), this.name + "_" + int3 + "_" + int4);
				textureArray[int10 + int9 * int4].setRegion(int1 + int10 * int5 + int7 * int10, int2 + int9 * int6 + int8 * int9, int5, int6);
				textureArray[int10 + int9 * int4].copyMaskRegion(this, int1 + int10 * int5 + int7 * int10, int2 + int9 * int6 + int8 * int9, int5, int6);
			}
		}

		return textureArray;
	}

	public Texture[][] split2D(int[] intArray, int[] intArray2) {
		if (intArray != null && intArray2 != null) {
			Texture[][] textureArrayArray = new Texture[intArray.length][intArray2.length];
			float float1 = 0.0F;
			float float2 = 0.0F;
			float float3 = 0.0F;
			for (int int1 = 0; int1 < intArray2.length; ++int1) {
				float2 += float1;
				float1 = (float)intArray2[int1] / (float)this.getHeightHW();
				float3 = 0.0F;
				for (int int2 = 0; int2 < intArray.length; ++int2) {
					float float4 = (float)intArray[int2] / (float)this.getWidthHW();
					Texture texture = textureArrayArray[int2][int1] = new Texture(this);
					texture.width = intArray[int2];
					texture.height = intArray2[int1];
					texture.xStart = float3;
					texture.xEnd = float3 += float4;
					texture.yStart = float2;
					texture.yEnd = float2 + float1;
				}
			}

			return textureArrayArray;
		} else {
			return null;
		}
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{ name:\"" + this.name + "\", w:" + this.getWidth() + ", h:" + this.getHeight() + " }";
	}

	public void saveMask(String string) {
		this.mask.save(string);
	}

	public void saveToZomboidDirectory(String string) {
		if (!string.contains("..")) {
			String string2 = ZomboidFileSystem.instance.getCacheDirSub(string);
			RenderThread.invokeOnRenderContext(()->{
				this.saveOnRenderThread(string2);
			});
		}
	}

	public void saveToCurrentSavefileDirectory(String string) {
		if (!string.contains("..")) {
			String string2 = ZomboidFileSystem.instance.getFileNameInCurrentSave(string);
			RenderThread.invokeOnRenderContext(()->{
				this.saveOnRenderThread(string2);
			});
		}
	}

	public void saveOnRenderThread(String string) {
		if (this.getID() == -1) {
			throw new IllegalStateException("texture hasn\'t been uploaded to the GPU");
		} else {
			GL11.glPixelStorei(3333, 1);
			GL13.glActiveTexture(33984);
			GL11.glEnable(3553);
			this.bind();
			int int1 = this.getWidth();
			int int2 = this.getHeight();
			int int3 = this.getWidthHW();
			int int4 = this.getHeightHW();
			byte byte1 = 4;
			ByteBuffer byteBuffer = MemoryUtil.memAlloc(int3 * int4 * byte1);
			GL21.glGetTexImage(3553, 0, 6408, 5121, byteBuffer);
			int[] intArray = new int[int1 * int2];
			int int5 = (int)PZMath.floor(this.getXStart() * (float)int3);
			int int6 = (int)PZMath.floor(this.getYStart() * (float)int4);
			for (int int7 = 0; int7 < intArray.length; ++int7) {
				int int8 = int5 + int7 % int1;
				int int9 = int6 + int7 / int1;
				int int10 = (int8 + int9 * int3) * byte1;
				intArray[int7] = (byteBuffer.get(int10 + 3) & 255) << 24 | (byteBuffer.get(int10) & 255) << 16 | (byteBuffer.get(int10 + 1) & 255) << 8 | (byteBuffer.get(int10 + 2) & 255) << 0;
			}

			MemoryUtil.memFree(byteBuffer);
			BufferedImage bufferedImage = new BufferedImage(int1, int2, 2);
			bufferedImage.setRGB(0, 0, int1, int2, intArray, 0, int1);
			try {
				File file = new File(string);
				file.getParentFile().mkdirs();
				ImageIO.write(bufferedImage, "png", file);
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}

			SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		}
	}

	public void loadMaskRegion(ByteBuffer byteBuffer) {
		if (byteBuffer != null) {
			this.mask = new Mask();
			this.mask.mask = new BooleanGrid(this.width, this.height);
			this.mask.mask.LoadFromByteBuffer(byteBuffer);
		}
	}

	public void saveMaskRegion(ByteBuffer byteBuffer) {
		if (byteBuffer != null) {
			this.mask.mask.PutToByteBuffer(byteBuffer);
		}
	}

	public int getRealWidth() {
		return this.realWidth;
	}

	public void setRealWidth(int int1) {
		this.realWidth = int1;
	}

	public int getRealHeight() {
		return this.realHeight;
	}

	public void setRealHeight(int int1) {
		this.realHeight = int1;
	}

	public Vector2 getUVScale(Vector2 vector2) {
		vector2.set(1.0F, 1.0F);
		if (this.dataid == null) {
			return vector2;
		} else {
			if (this.dataid.heightHW != this.dataid.height || this.dataid.widthHW != this.dataid.width) {
				vector2.x = (float)this.dataid.width / (float)this.dataid.widthHW;
				vector2.y = (float)this.dataid.height / (float)this.dataid.heightHW;
			}

			return vector2;
		}
	}

	private void syncReadSize() {
		PNGSize pNGSize = (PNGSize)pngSize.get();
		pNGSize.readSize(this.name);
		this.width = pNGSize.width;
		this.height = pNGSize.height;
	}

	public AssetType getType() {
		return ASSET_TYPE;
	}

	public void onBeforeReady() {
		if (this.assetParams != null) {
			this.assetParams.subTexture = null;
			this.assetParams = null;
		}

		this.solid = this.dataid.solid;
		if (this.splitX == -1) {
			this.width = this.dataid.width;
			this.height = this.dataid.height;
			this.xEnd = (float)this.width / (float)this.dataid.widthHW;
			this.yEnd = (float)this.height / (float)this.dataid.heightHW;
			if (this.dataid.mask != null) {
				this.createMask(this.dataid.mask);
			}
		} else {
			this.setRegion(this.splitX, this.splitY, this.splitW, this.splitH);
			if (this.dataid.mask != null) {
				this.mask = new Mask(this.dataid.mask, this.dataid.width, this.dataid.height, this.splitX, this.splitY, this.splitW, this.splitH);
			}
		}
	}

	public static void collectAllIcons(HashMap hashMap, HashMap hashMap2) {
		Iterator iterator = s_sharedTextureTable.entrySet().iterator();
		while (true) {
			Entry entry;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				entry = (Entry)iterator.next();
			}	 while (!((String)entry.getKey()).startsWith("media/ui/Container_") && !((String)entry.getKey()).startsWith("Item_"));

			String string = "";
			if (((String)entry.getKey()).startsWith("Item_")) {
				string = ((String)entry.getKey()).replaceFirst("Item_", "");
			} else if (((String)entry.getKey()).startsWith("media/ui/Container_")) {
				string = ((String)entry.getKey()).replaceFirst("media/ui/Container_", "");
				string = string.replaceAll("\\.png", "");
				String string2 = string.toLowerCase();
				DebugLog.log("Adding " + string2 + ", value = " + (String)entry.getKey());
			}

			hashMap.put(string.toLowerCase(), string);
			hashMap2.put(string.toLowerCase(), (String)entry.getKey());
		}
	}

	public static final class TextureAssetParams extends AssetManager.AssetParams {
		int flags = 0;
		FileSystem.SubTexture subTexture;
	}

	public static enum PZFileformat {

		PNG,
		DDS;

		private static Texture.PZFileformat[] $values() {
			return new Texture.PZFileformat[]{PNG, DDS};
		}
	}
}
