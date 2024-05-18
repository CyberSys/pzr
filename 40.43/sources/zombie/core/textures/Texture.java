package zombie.core.textures;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL11;
import zombie.GameApplet;
import zombie.IndieGL;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.bucket.BucketManager;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.ImageUtils;
import zombie.core.utils.WrappedBuffer;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.interfaces.IDestroyable;
import zombie.interfaces.ITexture;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.network.GameServer;
import zombie.network.ServerGUI;


public class Texture implements IDestroyable,ITexture,Serializable {
	private static HashMap textures = new HashMap();
	private static final long serialVersionUID = 7472363451408935314L;
	public static boolean autoCreateMask = false;
	public static int BindCount = 0;
	public static int renderQuadBatchCount = 0;
	public static int startStack = 0;
	public static boolean bDoingQuad = false;
	public static float lr;
	public static float lg;
	public static float lb;
	public static float la;
	public static int lastlastTextureID = -2;
	public static int totalTextureID = 0;
	public boolean flip;
	public float offsetX;
	public float offsetY;
	public boolean bindAlways;
	private int realWidth;
	private int realHeight;
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
	private boolean destroyed;
	public static int lastTextureID = -1;
	static HashMap texmap = new HashMap();
	public static final HashSet nullTextures = new HashSet();
	public static boolean WarnFailFindTexture = true;
	private static HashMap steamAvatarMap = new HashMap();
	static int maxbinds = 0;
	static int binds = 0;
	static String lasttex = "";
	private static final ObjectRenderEffects objRen = ObjectRenderEffects.alloc();
	public static boolean bWallColors = false;
	private Texture splitIconTex;

	public Texture(TextureID textureID, String string) {
		this.flip = false;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.bindAlways = false;
		this.realWidth = 0;
		this.realHeight = 0;
		this.xEnd = 1.0F;
		this.yEnd = 1.0F;
		this.xStart = 0.0F;
		this.yStart = 0.0F;
		this.destroyed = false;
		this.dataid = textureID;
		++this.dataid.referenceCount;
		this.solid = this.dataid.solid;
		this.width = textureID.width;
		this.height = textureID.height;
		this.xEnd = (float)this.width / (float)textureID.widthHW;
		this.yEnd = (float)this.height / (float)textureID.heightHW;
		this.name = string;
	}

	public Texture(BufferedImage bufferedImage, String string) {
		this(new TextureID(new ImageData(bufferedImage)), string);
	}

	public void Load(BufferedImage bufferedImage) {
		if (this.dataid.data == null) {
			this.dataid.data = new ImageData(this.width, this.height);
		}

		this.dataid.data.Load(bufferedImage);
	}

	public Texture(String string) {
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

	public Texture(String string, BufferedInputStream bufferedInputStream, boolean boolean1) {
		this(new TextureID(bufferedInputStream, string, boolean1), string);
		if (boolean1) {
			this.createMask(this.dataid.mask);
			this.dataid.mask = null;
			this.dataid.data = null;
		}
	}

	public Texture(String string, boolean boolean1, boolean boolean2) {
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

	public Texture(String string, boolean boolean1) {
		this(new TextureID(string), string);
		this.setUseAlphaChannel(boolean1);
	}

	public Texture(int int1, int int2, String string) {
		this(new TextureID(int1, int2), string);
	}

	public Texture(int int1, int int2) {
		this((TextureID)(new TextureID(int1, int2)), (String)null);
	}

	public Texture(String string, int int1, int int2, int int3) {
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
		this.flip = false;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.bindAlways = false;
		this.realWidth = 0;
		this.realHeight = 0;
		this.xEnd = 1.0F;
		this.yEnd = 1.0F;
		this.xStart = 0.0F;
		this.yStart = 0.0F;
		this.destroyed = false;
	}

	public static void bindNone() {
		IndieGL.glDisable(3553);
		lastTextureID = -1;
		--BindCount;
	}

	public static void clearTextures() {
		textures.clear();
	}

	public static Texture getSharedTexture(String string) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return null;
		} else {
			try {
				return getSharedTextureInternal(string, true);
			} catch (Exception exception) {
				return null;
			}
		}
	}

	public static Texture getSharedTexture(String string, boolean boolean1) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return null;
		} else {
			try {
				Texture texture = getSharedTextureInternal(string, boolean1);
				return texture;
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

				texture = (Texture)texmap.get(string2);
				if (texture != null) {
					return texture;
				}

				String string3 = ZomboidFileSystem.instance.getString(string2);
				if (!string3.equals(string2)) {
					boolean boolean1 = true;
					texture = new Texture(string3, boolean1, true);
					if (texture.dataid.width == 0) {
						return null;
					}

					BucketManager.Shared().AddTexture(string2, texture);
					texmap.put(string2, texture);
				}

				if (texture == null && string.endsWith("_White")) {
					texture = trygetTexture(string.substring(0, string.length() - "_White".length()));
					if (texture != null) {
						texmap.put(string2, texture);
					}
				}
			}

			return texture;
		}
	}

	static Texture getSharedTextureInternal(String string, boolean boolean1) {
		if (GameServer.bServer && !ServerGUI.isCreated()) {
			return null;
		} else if (nullTextures.contains(string)) {
			return null;
		} else if (texmap.containsKey(string)) {
			return (Texture)texmap.get(string);
		} else {
			if (!string.contains(".txt")) {
				String string2 = string;
				if (string.contains(".pcx") || string.contains(".png")) {
					string2 = string.substring(0, string.lastIndexOf("."));
				}

				string2 = string2.substring(string.lastIndexOf("/") + 1);
				Texture texture = TexturePackPage.getTexture(string2);
				if (texture != null) {
					texmap.put(string, texture);
					return texture;
				}
			}

			if (TexturePackPage.subTextureMap.containsKey(string)) {
				return (Texture)TexturePackPage.subTextureMap.get(string);
			} else {
				Texture texture2;
				if (BucketManager.Shared().HasTexture(string)) {
					texture2 = BucketManager.Shared().getTexture(string);
					texmap.put(string, texture2);
					return texture2;
				} else if (string.toLowerCase().contains(".pcx")) {
					nullTextures.add(string);
					return null;
				} else if (TexturePackPage.TexturePackPageNameMap.containsKey(string)) {
					TexturePackPage.getPackPage((String)TexturePackPage.TexturePackPageNameMap.get(string));
					return getSharedTextureInternal(string, boolean1);
				} else if (string.lastIndexOf(46) == -1) {
					nullTextures.add(string);
					return null;
				} else {
					texture2 = new Texture(string, boolean1, true);
					if (texture2.dataid.width == 0) {
						nullTextures.add(string);
						return null;
					} else {
						BucketManager.Shared().AddTexture(string, texture2);
						texmap.put(string, texture2);
						return texture2;
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
			if (autoCreateMask) {
			}

			BucketManager.Shared().AddTexture(string + string2, texture);
			return texture;
		}
	}

	public static Texture getSharedTexture(String string, int[] intArray, String string2) {
		if (BucketManager.Shared().HasTexture(string + string2)) {
			return BucketManager.Shared().getTexture(string + string2);
		} else {
			Texture texture = new Texture(string, intArray);
			if (autoCreateMask) {
			}

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
				if (autoCreateMask) {
				}

				BucketManager.Active().AddTexture(string, texture2);
				return texture2;
			} catch (TextureNotFoundException textureNotFoundException) {
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
		texmap.remove(string);
	}

	public static void reload(String string) {
		if (string != null && !string.isEmpty()) {
			Texture texture = (Texture)texmap.get(string);
			if (texture != null) {
				File file = new File(string);
				if (file.exists()) {
					ImageData imageData = new ImageData(file.getAbsolutePath());
					if (imageData.getWidthHW() == texture.getWidthHW() && imageData.getHeightHW() == texture.getHeightHW()) {
						RenderThread.borrowContext();
						GL11.glBindTexture(3553, lastTextureID = texture.dataid.id);
						short short1 = 6408;
						GL11.glTexImage2D(3553, 0, short1, texture.getWidthHW(), texture.getHeightHW(), 0, 6408, 5121, imageData.getData().getBuffer());
						RenderThread.returnContext();
					}
				}
			}
		}
	}

	public void bind() {
		this.bind(3553);
	}

	public void bind(int int1) {
		if (!this.isDestroyed() && this.isValid()) {
			if (this.bindAlways) {
				this.dataid.bindalways();
			} else {
				this.dataid.bind();
			}
		}
	}

	public void bindstrip(float float1, float float2, float float3, float float4) {
		this.bindstrip(3553, float1, float2, float3, float4);
	}

	public void bindstrip(int int1, float float1, float float2, float float3, float float4) {
		try {
			if (this.isDestroyed() || !this.isValid()) {
				return;
			}

			if (this.dataid.id != lastTextureID) {
				binds = 0;
				if (bDoingQuad && IndieGL.nCount == 1) {
					try {
						IndieGL.End();
					} catch (Exception exception) {
						Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
					}

					bDoingQuad = false;
					this.dataid.bindalways();
					IndieGL.Begin();
					lasttex = this.dataid.getPathFileName();
					bDoingQuad = true;
					return;
				}
			} else {
				++binds;
				if (binds > maxbinds) {
					maxbinds = binds;
				}
			}

			if (this.dataid.bind()) {
				IndieGL.Begin();
				lasttex = this.dataid.getPathFileName();
				bDoingQuad = true;
			}
		} catch (Exception exception2) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception2);
		}

		IndieGL.Begin();
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

	public void createMask(WrappedBuffer wrappedBuffer) {
		new Mask(this, wrappedBuffer);
	}

	public void destroy() {
		if (!this.destroyed) {
			if (this.dataid != null && --this.dataid.referenceCount == 0) {
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

	public int getHeight() {
		return this.height;
	}

	public int getHeightHW() {
		return this.dataid.heightHW;
	}

	public int getHeightOrig() {
		return this.heightOrig == 0 ? this.height : this.heightOrig;
	}

	public int getID() {
		return this.dataid.id;
	}

	public Mask getMask() {
		return this.mask;
	}

	public String getName() {
		return this.name;
	}

	public TextureID getTextureId() {
		return this.dataid;
	}

	public boolean getUseAlphaChannel() {
		return !this.solid;
	}

	public int getWidth() {
		return this.width;
	}

	public int getWidthHW() {
		return this.dataid.widthHW;
	}

	public int getWidthOrig() {
		return this.widthOrig == 0 ? this.width : this.widthOrig;
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

	public float getOffsetY() {
		return this.offsetY;
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

	public void render(int int1, int int2, int int3, int int4) {
		this.render(int1, int2, int3, int4, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void render(int int1, int int2) {
		this.render(int1, int2, this.width, this.height, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void render(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		int1 = (int)((float)int1 + this.offsetX);
		int2 = (int)((float)int2 + this.offsetY);
		SpriteRenderer.instance.render(this, int1, int2, int3, int4, float1, float2, float3, float4);
	}

	public void render(ObjectRenderEffects objectRenderEffects, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		float float5 = this.offsetX + (float)int1;
		float float6 = this.offsetY + (float)int2;
		objRen.x1 = (double)float5 + objectRenderEffects.x1 * (double)int3;
		objRen.y1 = (double)float6 + objectRenderEffects.y1 * (double)int4;
		objRen.x2 = (double)(float5 + (float)int3) + objectRenderEffects.x2 * (double)int3;
		objRen.y2 = (double)float6 + objectRenderEffects.y2 * (double)int4;
		objRen.x3 = (double)(float5 + (float)int3) + objectRenderEffects.x3 * (double)int3;
		objRen.y3 = (double)(float6 + (float)int4) + objectRenderEffects.y3 * (double)int4;
		objRen.x4 = (double)float5 + objectRenderEffects.x4 * (double)int3;
		objRen.y4 = (double)(float6 + (float)int4) + objectRenderEffects.y4 * (double)int4;
		SpriteRenderer.instance.render(this, objRen.x1, objRen.y1, objRen.x2, objRen.y2, objRen.x3, objRen.y3, objRen.x4, objRen.y4, float1, float2, float3, float4);
	}

	public void rendershader(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		try {
			if (float4 == 0.0F) {
				return;
			}

			float float5 = this.getXStart();
			float float6 = this.getYStart();
			float float7 = this.getXEnd();
			float float8 = this.getYEnd();
			if (this.flip) {
				float float9 = float7;
				float7 = float5;
				float5 = float9;
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
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

			if (int1 + int3 <= 0) {
				return;
			}

			if (int2 + int4 <= 0) {
				return;
			}

			if (int1 >= Core.getInstance().getScreenWidth()) {
				return;
			}

			if (int2 >= Core.getInstance().getScreenHeight()) {
				return;
			}

			if (Core.getInstance().bUseShaders) {
			}

			lr = float1;
			lg = float2;
			lb = float3;
			la = float4;
			SpriteRenderer.instance.render(this, int1, int2, int3, int4, float1, float2, float3, float4, float5, float8, float7, float8, float7, float6, float5, float6);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void rendershader2(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4) {
		try {
			if (float4 == 0.0F) {
				return;
			}

			float float5 = (float)int5 / (float)this.getWidthHW();
			float float6 = (float)int6 / (float)this.getHeightHW();
			float float7 = (float)(int5 + int7) / (float)this.getWidthHW();
			float float8 = (float)(int6 + int8) / (float)this.getHeightHW();
			if (this.flip) {
				float float9 = float7;
				float7 = float5;
				float5 = float9;
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
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

			if (int1 + int3 <= 0) {
				return;
			}

			if (int2 + int4 <= 0) {
				return;
			}

			if (int1 >= Core.getInstance().getScreenWidth()) {
				return;
			}

			if (int2 >= Core.getInstance().getScreenHeight()) {
				return;
			}

			if (Core.getInstance().bUseShaders) {
			}

			lr = float1;
			lg = float2;
			lb = float3;
			la = float4;
			SpriteRenderer.instance.render(this, int1, int2, int3, int4, float1, float2, float3, float4, float5, float8, float7, float8, float7, float6, float5, float6);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderdiamond(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5) {
		this.renderdiamond(int1, int2, int3, int4, float1, float1, float1, float1 * float5, float2, float2, float2, float2 * float5, float3, float3, float3, float3 * float5, float4, float4, float4, float4 * float5);
	}

	public void renderdiamond(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		--int1;
		SpriteRenderer.instance.render((Texture)null, int1, int2, int1 + int3 / 2, int2 - int4 / 2, int1 + int3, int2, int1 + int3 / 2, int2 + int4 / 2, float9, float10, float11, float12, float1, float2, float3, float4, float13, float14, float15, float16, float5, float6, float7, float8);
	}

	public void renderdiamond(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
		--int1;
		SpriteRenderer.instance.render((Texture)null, int1, int2, int1 + int3 / 2, int2 - int4 / 2, int1 + int3, int2, int1 + int3 / 2, int2 + int4 / 2, int7, int5, int8, int6);
	}

	public void renderwallw(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		try {
			float float17 = this.getXStart();
			float float18 = this.getYStart();
			float float19 = this.getXEnd();
			float float20 = this.getYEnd();
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			float17 += (float19 - float17) * 0.01F;
			float18 += (float20 - float18) * 0.01F;
			float19 -= (float19 - float17) * 0.01F;
			float float21 = float20 - (float20 - float18) * 0.01F;
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			int3 -= 4;
			if (Core.getInstance().bUseShaders) {
			}

			SpriteRenderer.instance.render((Texture)null, int1, int2 + 4, int1, int2 - 118 + 17, int1 + int3 / 2 + 4, int2 - int4 / 2 + 1 - 118 + 17, int1 + int3 / 2 + 4, int2 - int4 / 2 + 4, float5, float6, float7, float8, float13, float14, float15, float16, float9, float10, float11, float12, float1, float2, float3, float4);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderwallw(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
		try {
			float float1 = this.getXStart();
			float float2 = this.getYStart();
			float float3 = this.getXEnd();
			float float4 = this.getYEnd();
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			float1 += (float3 - float1) * 0.01F;
			float2 += (float4 - float2) * 0.01F;
			float3 -= (float3 - float1) * 0.01F;
			float float5 = float4 - (float4 - float2) * 0.01F;
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			if (Core.getInstance().bUseShaders) {
			}

			if (Core.bDebug && bWallColors) {
				int6 = -16711936;
				int5 = -16711936;
				int8 = -16728064;
				int7 = -16728064;
			}

			int int9 = Core.TileScale;
			SpriteRenderer.instance.render((Texture)null, int1 - int3 / 2, int2 - 96 * int9 + int4 / 2 - 1, int1 + int9, int2 - 96 * int9 - 3, int1 + int9, int2 + 3, int1 - int3 / 2, int2 + int4 / 2 + 4, int8, int7, int5, int6);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderwallnw(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10) {
		try {
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			if (Core.bDebug && bWallColors) {
				int8 = -65536;
				int6 = -65536;
				int7 = -65536;
				int5 = -65536;
			}

			int int11 = Core.TileScale;
			SpriteRenderer.instance.render((Texture)null, int1 - int3 / 2, int2 - 96 * int11 + int4 / 2 - 1, int1, int2 - 96 * int11 - 2, int1, int2 + 4, int1 - int3 / 2, int2 + int4 / 2 + 4, int8, int7, int5, int6);
			if (Core.bDebug && bWallColors) {
				int10 = -256;
				int9 = -256;
				int7 = -256;
				int5 = -256;
			}

			SpriteRenderer.instance.render((Texture)null, int1, int2 - 96 * int11, int1 + int3 / 2, int2 - 96 * int11 + int4 / 2, int1 + int3 / 2, int2 + int4 / 2 + 5, int1, int2 + 5, int7, int10, int9, int5);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderroofw(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		try {
			float float17 = this.getXStart();
			float float18 = this.getYStart();
			float float19 = this.getXEnd();
			float float20 = this.getYEnd();
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			float17 += (float19 - float17) * 0.01F;
			float18 += (float20 - float18) * 0.01F;
			float19 -= (float19 - float17) * 0.01F;
			float20 -= (float20 - float18) * 0.01F;
			int1 = (int)((float)int1 + this.offsetX);
			int2 = (int)((float)int2 + this.offsetY);
			int3 -= 4;
			if (Core.getInstance().bUseShaders) {
			}

			this.bindstrip(1.0F, 1.0F, 1.0F, 1.0F);
			int int5 = int2 + int4;
			int int6 = int1 + 32;
			int5 -= 50;
			int5 -= 32;
			GL11.glColor4f(float5, float6, float7, float8);
			GL11.glTexCoord2f(float17, float20);
			GL11.glVertex2i(int6, int5);
			GL11.glColor4f(float13, float14, float15, float16);
			GL11.glTexCoord2f(float17, float18);
			int6 += 32;
			int5 -= 16;
			GL11.glVertex2i(int6, int5);
			GL11.glColor4f(float9, float10, float11, float12);
			GL11.glTexCoord2f(float19, float18);
			int6 += 32;
			int5 += 48;
			GL11.glVertex2i(int6, int5);
			GL11.glColor4f(float1, float2, float3, float4);
			GL11.glTexCoord2f(float19, float20);
			int6 -= 32;
			int5 += 16;
			GL11.glVertex2i(int6, int5);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderwalln(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		try {
			float float17 = this.getXStart();
			float float18 = this.getYStart();
			float float19 = this.getXEnd();
			float float20 = this.getYEnd();
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			float17 += (float19 - float17) * 0.01F;
			float18 += (float20 - float18) * 0.01F;
			float19 -= (float19 - float17) * 0.01F;
			float float21 = float20 - (float20 - float18) * 0.01F;
			int2 += 4;
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			if (Core.getInstance().bUseShaders) {
			}

			int1 -= 4;
			int1 += int3 / 2;
			int3 -= 2;
			int4 += 3;
			SpriteRenderer.instance.render((Texture)null, int1, int2 - 17, int1, int2 - 119, int1 + int3 / 2 + 4, int2 + int4 / 2 + 1 - 119, int1 + int3 / 2 + 4, int2 + int4 / 2 + 4 - 17, float1, float2, float3, float4, float9, float10, float11, float12, float13, float14, float15, float16, float5, float6, float7, float8);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderwalln(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8) {
		try {
			float float1 = this.getXStart();
			float float2 = this.getYStart();
			float float3 = this.getXEnd();
			float float4 = this.getYEnd();
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			float1 += (float3 - float1) * 0.01F;
			float2 += (float4 - float2) * 0.01F;
			float3 -= (float3 - float1) * 0.01F;
			float float5 = float4 - (float4 - float2) * 0.01F;
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			if (Core.getInstance().bUseShaders) {
			}

			if (Core.bDebug && bWallColors) {
				int6 = -16776961;
				int5 = -16776961;
				int8 = -16777024;
				int7 = -16777024;
			}

			int int9 = Core.TileScale;
			SpriteRenderer.instance.render((Texture)null, int1 - 6, int2 - 96 * int9 - 3, int1 + int3 / 2, int2 - 96 * int9 + int4 / 2, int1 + int3 / 2, int2 + int4 / 2 + 5, int1 - 6, int2 + 2, int7, int8, int6, int5);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderwallncutoff(int int1, int int2, int int3, int int4) {
		try {
			float float1 = this.getXStart();
			float float2 = this.getYStart();
			float float3 = this.getXEnd();
			float float4 = this.getYEnd();
			lr = -1.0F;
			lg = -1.0F;
			lb = -1.0F;
			la = -1.0F;
			float1 += (float3 - float1) * 0.01F;
			float2 += (float4 - float2) * 0.01F;
			float3 -= (float3 - float1) * 0.01F;
			float float5 = float4 - (float4 - float2) * 0.01F;
			int2 += 4;
			if (this.flip) {
				int1 = (int)((float)int1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
				int2 = (int)((float)int2 + this.offsetY);
			} else {
				int1 = (int)((float)int1 + this.offsetX);
				int2 = (int)((float)int2 + this.offsetY);
			}

			if (Core.getInstance().bUseShaders) {
			}

			int1 -= 4;
			int1 += int3 / 2;
			int3 -= 2;
			SpriteRenderer.instance.render((Texture)null, int1 - 1, int2 - 16, int1 - 1, int2 - 15, int1 + int3 / 2 + 4, int2 + int4 / 2 + 1 - 15, int1 + int3 / 2 + 4, int2 + int4 / 2 + 4 - 17, -1, -1, -1, -1);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}
	}

	public void renderstrip(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
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

			SpriteRenderer.instance.render(this, int1, int2, int3, int4, float1, float2, float3, float4);
		} catch (Exception exception) {
			bDoingQuad = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
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

	public void setData(ByteBuffer byteBuffer) {
		this.dataid.setData(byteBuffer);
	}

	public void setMask(Mask mask) {
		this.mask = mask;
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

	public void setUseAlphaChannel(boolean boolean1) {
		this.dataid.solid = this.solid = !boolean1;
	}

	public Texture splitIcon() {
		if (this.splitIconTex == null) {
			this.splitIconTex = new Texture(this.getTextureId(), this.name + "_Icon");
			float float1 = this.xStart * (float)this.getWidthHW();
			float float2 = this.yStart * (float)this.getHeightHW();
			float float3 = this.xEnd * (float)this.getWidthHW() - float1;
			float float4 = this.yEnd * (float)this.getHeightHW() - float2;
			this.splitIconTex.setRegion((int)float1, (int)float2, (int)float3, (int)float4);
			this.splitIconTex.offsetX = 0.0F;
			this.splitIconTex.offsetY = 0.0F;
			texmap.put(this.name + "_Icon", this.splitIconTex);
		}

		return this.splitIconTex;
	}

	public Texture split(int int1, int int2, int int3, int int4) {
		Texture texture = new Texture(this.getTextureId(), this.name + "_" + int1 + "_" + int2);
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
			return (Texture[][])null;
		}
	}

	public String toString() {
		return this.name;
	}

	private void readVersion3(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		if (textures.containsKey(this.name)) {
			DebugLog.log("ERROR: Texture\'s name already loaded");
		} else {
			textures.put(this.name, this);
		}

		boolean boolean1 = objectInputStream.readBoolean();
		if (boolean1) {
			String string = (String)objectInputStream.readObject();
			boolean boolean2 = !objectInputStream.readBoolean();
			this.xStart = objectInputStream.readFloat();
			this.xEnd = objectInputStream.readFloat();
			this.yStart = objectInputStream.readFloat();
			this.yEnd = objectInputStream.readFloat();
			DebugLog.log("path: " + string);
			this.dataid = new TextureID(string);
			++this.dataid.referenceCount;
			if (boolean2) {
				ArrayList arrayList = (ArrayList)objectInputStream.readObject();
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					AlphaColorIndex alphaColorIndex = (AlphaColorIndex)arrayList.get(int1);
					this.makeTransp(alphaColorIndex.red, alphaColorIndex.green, alphaColorIndex.blue);
				}
			}
		} else {
			DebugLog.log("Loading runtime customized texture");
			this.dataid = (TextureID)objectInputStream.readObject();
		}

		if (objectInputStream.readBoolean()) {
			this.mask = (Mask)objectInputStream.readObject();
		}
	}

	public void saveMask(String string) {
		this.mask.save(string);
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

	public void setWidth(int int1) {
		this.width = int1;
	}

	public void setHeight(int int1) {
		this.height = int1;
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

	public void setOffsetX(int int1) {
		this.offsetX = (float)int1;
	}

	public void setOffsetY(int int1) {
		this.offsetY = (float)int1;
	}
	public static enum PZFileformat {

		PNG,
		DDS;
	}
}
