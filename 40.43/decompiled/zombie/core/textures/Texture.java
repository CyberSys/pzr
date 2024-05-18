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

public class Texture implements IDestroyable, ITexture, Serializable {
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

   public Texture(TextureID var1, String var2) {
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
      this.dataid = var1;
      ++this.dataid.referenceCount;
      this.solid = this.dataid.solid;
      this.width = var1.width;
      this.height = var1.height;
      this.xEnd = (float)this.width / (float)var1.widthHW;
      this.yEnd = (float)this.height / (float)var1.heightHW;
      this.name = var2;
   }

   public Texture(BufferedImage var1, String var2) {
      this(new TextureID(new ImageData(var1)), var2);
   }

   public void Load(BufferedImage var1) {
      if (this.dataid.data == null) {
         this.dataid.data = new ImageData(this.width, this.height);
      }

      this.dataid.data.Load(var1);
   }

   public Texture(String var1) {
      this(new TextureID(var1), var1);
      this.setUseAlphaChannel(true);
   }

   public Texture(String var1, BufferedInputStream var2, boolean var3, Texture.PZFileformat var4) {
      this(new TextureID(var2, var1, var3, var4), var1);
      if (var3 && this.dataid.mask != null) {
         this.createMask(this.dataid.mask);
         this.dataid.mask = null;
         this.dataid.data = null;
      }

   }

   public Texture(String var1, BufferedInputStream var2, boolean var3) {
      this(new TextureID(var2, var1, var3), var1);
      if (var3) {
         this.createMask(this.dataid.mask);
         this.dataid.mask = null;
         this.dataid.data = null;
      }

   }

   public Texture(String var1, boolean var2, boolean var3) {
      this(new TextureID(var1), var1);
      this.setUseAlphaChannel(var3);
      if (var2) {
         this.dataid.data = null;
      }

   }

   public Texture(String var1, String var2) {
      this(new TextureID(var1, var2), var1);
      this.setUseAlphaChannel(true);
   }

   public Texture(String var1, int[] var2) {
      this(new TextureID(var1, var2), var1);
      if (var1.contains("drag")) {
         boolean var3 = false;
      }

      this.setUseAlphaChannel(true);
   }

   public Texture(String var1, boolean var2) {
      this(new TextureID(var1), var1);
      this.setUseAlphaChannel(var2);
   }

   public Texture(int var1, int var2, String var3) {
      this(new TextureID(var1, var2), var3);
   }

   public Texture(int var1, int var2) {
      this((TextureID)(new TextureID(var1, var2)), (String)null);
   }

   public Texture(String var1, int var2, int var3, int var4) {
      this(new TextureID(var1, var2, var3, var4), var1);
   }

   public Texture(Texture var1) {
      this(var1.dataid, var1.name + "(copy)");
      this.width = var1.width;
      this.height = var1.height;
      this.name = var1.name;
      this.xStart = var1.xStart;
      this.yStart = var1.yStart;
      this.xEnd = var1.xEnd;
      this.yEnd = var1.yEnd;
      this.solid = var1.solid;
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

   public static Texture getSharedTexture(String var0) {
      if (GameServer.bServer && !ServerGUI.isCreated()) {
         return null;
      } else {
         try {
            return getSharedTextureInternal(var0, true);
         } catch (Exception var2) {
            return null;
         }
      }
   }

   public static Texture getSharedTexture(String var0, boolean var1) {
      if (GameServer.bServer && !ServerGUI.isCreated()) {
         return null;
      } else {
         try {
            Texture var2 = getSharedTextureInternal(var0, var1);
            return var2;
         } catch (Exception var3) {
            return null;
         }
      }
   }

   public static Texture trygetTexture(String var0) {
      if (GameServer.bServer && !ServerGUI.isCreated()) {
         return null;
      } else {
         Texture var1 = getSharedTexture(var0);
         if (var1 == null) {
            String var2 = "media/textures/" + var0;
            if (!var0.endsWith(".png")) {
               var2 = var2 + ".png";
            }

            var1 = (Texture)texmap.get(var2);
            if (var1 != null) {
               return var1;
            }

            String var3 = ZomboidFileSystem.instance.getString(var2);
            if (!var3.equals(var2)) {
               boolean var4 = true;
               var1 = new Texture(var3, var4, true);
               if (var1.dataid.width == 0) {
                  return null;
               }

               BucketManager.Shared().AddTexture(var2, var1);
               texmap.put(var2, var1);
            }

            if (var1 == null && var0.endsWith("_White")) {
               var1 = trygetTexture(var0.substring(0, var0.length() - "_White".length()));
               if (var1 != null) {
                  texmap.put(var2, var1);
               }
            }
         }

         return var1;
      }
   }

   static Texture getSharedTextureInternal(String var0, boolean var1) {
      if (GameServer.bServer && !ServerGUI.isCreated()) {
         return null;
      } else if (nullTextures.contains(var0)) {
         return null;
      } else if (texmap.containsKey(var0)) {
         return (Texture)texmap.get(var0);
      } else {
         if (!var0.contains(".txt")) {
            String var2 = var0;
            if (var0.contains(".pcx") || var0.contains(".png")) {
               var2 = var0.substring(0, var0.lastIndexOf("."));
            }

            var2 = var2.substring(var0.lastIndexOf("/") + 1);
            Texture var3 = TexturePackPage.getTexture(var2);
            if (var3 != null) {
               texmap.put(var0, var3);
               return var3;
            }
         }

         if (TexturePackPage.subTextureMap.containsKey(var0)) {
            return (Texture)TexturePackPage.subTextureMap.get(var0);
         } else {
            Texture var4;
            if (BucketManager.Shared().HasTexture(var0)) {
               var4 = BucketManager.Shared().getTexture(var0);
               texmap.put(var0, var4);
               return var4;
            } else if (var0.toLowerCase().contains(".pcx")) {
               nullTextures.add(var0);
               return null;
            } else if (TexturePackPage.TexturePackPageNameMap.containsKey(var0)) {
               TexturePackPage.getPackPage((String)TexturePackPage.TexturePackPageNameMap.get(var0));
               return getSharedTextureInternal(var0, var1);
            } else if (var0.lastIndexOf(46) == -1) {
               nullTextures.add(var0);
               return null;
            } else {
               var4 = new Texture(var0, var1, true);
               if (var4.dataid.width == 0) {
                  nullTextures.add(var0);
                  return null;
               } else {
                  BucketManager.Shared().AddTexture(var0, var4);
                  texmap.put(var0, var4);
                  return var4;
               }
            }
         }
      }
   }

   public static Texture getSharedTexture(String var0, String var1) {
      if (BucketManager.Shared().HasTexture(var0 + var1)) {
         return BucketManager.Shared().getTexture(var0 + var1);
      } else {
         Texture var2 = new Texture(var0, var1);
         if (autoCreateMask) {
         }

         BucketManager.Shared().AddTexture(var0 + var1, var2);
         return var2;
      }
   }

   public static Texture getSharedTexture(String var0, int[] var1, String var2) {
      if (BucketManager.Shared().HasTexture(var0 + var2)) {
         return BucketManager.Shared().getTexture(var0 + var2);
      } else {
         Texture var3 = new Texture(var0, var1);
         if (autoCreateMask) {
         }

         BucketManager.Shared().AddTexture(var0 + var2, var3);
         return var3;
      }
   }

   public static Texture getTexture(String var0) {
      if (!var0.contains(".txt")) {
         String var1 = var0.replace(".png", "");
         var1 = var1.replace(".pcx", "");
         var1 = var1.substring(var0.lastIndexOf("/") + 1);
         Texture var2 = TexturePackPage.getTexture(var1);
         if (var2 != null) {
            return var2;
         }
      }

      if (BucketManager.Active().HasTexture(var0)) {
         return BucketManager.Active().getTexture(var0);
      } else {
         try {
            Texture var4 = new Texture(var0);
            if (autoCreateMask) {
            }

            BucketManager.Active().AddTexture(var0, var4);
            return var4;
         } catch (TextureNotFoundException var3) {
            return null;
         }
      }
   }

   public static Texture getSteamAvatar(long var0) {
      if (steamAvatarMap.containsKey(var0)) {
         return (Texture)steamAvatarMap.get(var0);
      } else {
         TextureID var2 = TextureID.createSteamAvatar(var0);
         if (var2 == null) {
            return null;
         } else {
            Texture var3 = new Texture(var2, "SteamAvatar" + SteamUtils.convertSteamIDToString(var0));
            steamAvatarMap.put(var0, var3);
            return var3;
         }
      }
   }

   public static void steamAvatarChanged(long var0) {
      Texture var2 = (Texture)steamAvatarMap.get(var0);
      if (var2 != null) {
         steamAvatarMap.remove(var0);
      }

   }

   public static void forgetTexture(String var0) {
      BucketManager.Shared().forgetTexture(var0);
      texmap.remove(var0);
   }

   public static void reload(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         Texture var1 = (Texture)texmap.get(var0);
         if (var1 != null) {
            File var2 = new File(var0);
            if (var2.exists()) {
               ImageData var3 = new ImageData(var2.getAbsolutePath());
               if (var3.getWidthHW() == var1.getWidthHW() && var3.getHeightHW() == var1.getHeightHW()) {
                  RenderThread.borrowContext();
                  GL11.glBindTexture(3553, lastTextureID = var1.dataid.id);
                  short var4 = 6408;
                  GL11.glTexImage2D(3553, 0, var4, var1.getWidthHW(), var1.getHeightHW(), 0, 6408, 5121, var3.getData().getBuffer());
                  RenderThread.returnContext();
               }
            }
         }
      }
   }

   public void bind() {
      this.bind(3553);
   }

   public void bind(int var1) {
      if (!this.isDestroyed() && this.isValid()) {
         if (this.bindAlways) {
            this.dataid.bindalways();
         } else {
            this.dataid.bind();
         }

      }
   }

   public void bindstrip(float var1, float var2, float var3, float var4) {
      this.bindstrip(3553, var1, var2, var3, var4);
   }

   public void bindstrip(int var1, float var2, float var3, float var4, float var5) {
      try {
         if (this.isDestroyed() || !this.isValid()) {
            return;
         }

         if (this.dataid.id != lastTextureID) {
            binds = 0;
            if (bDoingQuad && IndieGL.nCount == 1) {
               try {
                  IndieGL.End();
               } catch (Exception var7) {
                  Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var7);
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
      } catch (Exception var8) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var8);
      }

      IndieGL.Begin();
   }

   public void copyMaskRegion(Texture var1, int var2, int var3, int var4, int var5) {
      if (var1.getMask() != null) {
         new Mask(var1, this, var2, var3, var4, var5);
      }
   }

   public void createMask() {
      new Mask(this);
   }

   public void createMask(boolean[] var1) {
      new Mask(this, var1);
   }

   public void createMask(WrappedBuffer var1) {
      new Mask(this, var1);
   }

   public void destroy() {
      if (!this.destroyed) {
         if (this.dataid != null && --this.dataid.referenceCount == 0) {
            this.dataid.destroy();
         }

         this.destroyed = true;
      }
   }

   public boolean equals(Texture var1) {
      return var1.xStart == this.xStart && var1.xEnd == this.xEnd && var1.yStart == this.yStart && var1.yEnd == this.yEnd && var1.width == this.width && var1.height == this.height && var1.solid == this.solid && (this.dataid == null || var1.dataid == null || var1.dataid.pathFileName == null || this.dataid.pathFileName == null || var1.dataid.pathFileName.equals(this.dataid.pathFileName));
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

   public void makeTransp(int var1, int var2, int var3) {
      this.setAlphaForeach(var1, var2, var3, 0);
   }

   public void render(int var1, int var2, int var3, int var4) {
      this.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(int var1, int var2) {
      this.render(var1, var2, this.width, this.height, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      var1 = (int)((float)var1 + this.offsetX);
      var2 = (int)((float)var2 + this.offsetY);
      SpriteRenderer.instance.render(this, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void render(ObjectRenderEffects var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      float var10 = this.offsetX + (float)var2;
      float var11 = this.offsetY + (float)var3;
      objRen.x1 = (double)var10 + var1.x1 * (double)var4;
      objRen.y1 = (double)var11 + var1.y1 * (double)var5;
      objRen.x2 = (double)(var10 + (float)var4) + var1.x2 * (double)var4;
      objRen.y2 = (double)var11 + var1.y2 * (double)var5;
      objRen.x3 = (double)(var10 + (float)var4) + var1.x3 * (double)var4;
      objRen.y3 = (double)(var11 + (float)var5) + var1.y3 * (double)var5;
      objRen.x4 = (double)var10 + var1.x4 * (double)var4;
      objRen.y4 = (double)(var11 + (float)var5) + var1.y4 * (double)var5;
      SpriteRenderer.instance.render(this, objRen.x1, objRen.y1, objRen.x2, objRen.y2, objRen.x3, objRen.y3, objRen.x4, objRen.y4, var6, var7, var8, var9);
   }

   public void rendershader(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      try {
         if (var8 == 0.0F) {
            return;
         }

         float var9 = this.getXStart();
         float var10 = this.getYStart();
         float var11 = this.getXEnd();
         float var12 = this.getYEnd();
         if (this.flip) {
            float var13 = var11;
            var11 = var9;
            var9 = var13;
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         if (var6 > 1.0F) {
            var6 = 1.0F;
         }

         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         if (var8 > 1.0F) {
            var8 = 1.0F;
         }

         if (var5 < 0.0F) {
            var5 = 0.0F;
         }

         if (var6 < 0.0F) {
            var6 = 0.0F;
         }

         if (var7 < 0.0F) {
            var7 = 0.0F;
         }

         if (var8 < 0.0F) {
            var8 = 0.0F;
         }

         if (var1 + var3 <= 0) {
            return;
         }

         if (var2 + var4 <= 0) {
            return;
         }

         if (var1 >= Core.getInstance().getScreenWidth()) {
            return;
         }

         if (var2 >= Core.getInstance().getScreenHeight()) {
            return;
         }

         if (Core.getInstance().bUseShaders) {
         }

         lr = var5;
         lg = var6;
         lb = var7;
         la = var8;
         SpriteRenderer.instance.render(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var12, var11, var12, var11, var10, var9, var10);
      } catch (Exception var14) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var14);
      }

   }

   public void rendershader2(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, float var9, float var10, float var11, float var12) {
      try {
         if (var12 == 0.0F) {
            return;
         }

         float var13 = (float)var5 / (float)this.getWidthHW();
         float var14 = (float)var6 / (float)this.getHeightHW();
         float var15 = (float)(var5 + var7) / (float)this.getWidthHW();
         float var16 = (float)(var6 + var8) / (float)this.getHeightHW();
         if (this.flip) {
            float var17 = var15;
            var15 = var13;
            var13 = var17;
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (var9 > 1.0F) {
            var9 = 1.0F;
         }

         if (var10 > 1.0F) {
            var10 = 1.0F;
         }

         if (var11 > 1.0F) {
            var11 = 1.0F;
         }

         if (var12 > 1.0F) {
            var12 = 1.0F;
         }

         if (var9 < 0.0F) {
            var9 = 0.0F;
         }

         if (var10 < 0.0F) {
            var10 = 0.0F;
         }

         if (var11 < 0.0F) {
            var11 = 0.0F;
         }

         if (var12 < 0.0F) {
            var12 = 0.0F;
         }

         if (var1 + var3 <= 0) {
            return;
         }

         if (var2 + var4 <= 0) {
            return;
         }

         if (var1 >= Core.getInstance().getScreenWidth()) {
            return;
         }

         if (var2 >= Core.getInstance().getScreenHeight()) {
            return;
         }

         if (Core.getInstance().bUseShaders) {
         }

         lr = var9;
         lg = var10;
         lb = var11;
         la = var12;
         SpriteRenderer.instance.render(this, var1, var2, var3, var4, var9, var10, var11, var12, var13, var16, var15, var16, var15, var14, var13, var14);
      } catch (Exception var18) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var18);
      }

   }

   public void renderdiamond(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9) {
      this.renderdiamond(var1, var2, var3, var4, var5, var5, var5, var5 * var9, var6, var6, var6, var6 * var9, var7, var7, var7, var7 * var9, var8, var8, var8, var8 * var9);
   }

   public void renderdiamond(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20) {
      --var1;
      SpriteRenderer.instance.render((Texture)null, var1, var2, var1 + var3 / 2, var2 - var4 / 2, var1 + var3, var2, var1 + var3 / 2, var2 + var4 / 2, var13, var14, var15, var16, var5, var6, var7, var8, var17, var18, var19, var20, var9, var10, var11, var12);
   }

   public void renderdiamond(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      --var1;
      SpriteRenderer.instance.render((Texture)null, var1, var2, var1 + var3 / 2, var2 - var4 / 2, var1 + var3, var2, var1 + var3 / 2, var2 + var4 / 2, var7, var5, var8, var6);
   }

   public void renderwallw(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20) {
      try {
         float var21 = this.getXStart();
         float var22 = this.getYStart();
         float var23 = this.getXEnd();
         float var24 = this.getYEnd();
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         var21 += (var23 - var21) * 0.01F;
         var22 += (var24 - var22) * 0.01F;
         var23 -= (var23 - var21) * 0.01F;
         float var10000 = var24 - (var24 - var22) * 0.01F;
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         var3 -= 4;
         if (Core.getInstance().bUseShaders) {
         }

         SpriteRenderer.instance.render((Texture)null, var1, var2 + 4, var1, var2 - 118 + 17, var1 + var3 / 2 + 4, var2 - var4 / 2 + 1 - 118 + 17, var1 + var3 / 2 + 4, var2 - var4 / 2 + 4, var9, var10, var11, var12, var17, var18, var19, var20, var13, var14, var15, var16, var5, var6, var7, var8);
      } catch (Exception var26) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var26);
      }

   }

   public void renderwallw(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      try {
         float var9 = this.getXStart();
         float var10 = this.getYStart();
         float var11 = this.getXEnd();
         float var12 = this.getYEnd();
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         var9 += (var11 - var9) * 0.01F;
         var10 += (var12 - var10) * 0.01F;
         var11 -= (var11 - var9) * 0.01F;
         float var10000 = var12 - (var12 - var10) * 0.01F;
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (Core.getInstance().bUseShaders) {
         }

         if (Core.bDebug && bWallColors) {
            var6 = -16711936;
            var5 = -16711936;
            var8 = -16728064;
            var7 = -16728064;
         }

         int var13 = Core.TileScale;
         SpriteRenderer.instance.render((Texture)null, var1 - var3 / 2, var2 - 96 * var13 + var4 / 2 - 1, var1 + var13, var2 - 96 * var13 - 3, var1 + var13, var2 + 3, var1 - var3 / 2, var2 + var4 / 2 + 4, var8, var7, var5, var6);
      } catch (Exception var14) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var14);
      }

   }

   public void renderwallnw(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      try {
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (Core.bDebug && bWallColors) {
            var8 = -65536;
            var6 = -65536;
            var7 = -65536;
            var5 = -65536;
         }

         int var11 = Core.TileScale;
         SpriteRenderer.instance.render((Texture)null, var1 - var3 / 2, var2 - 96 * var11 + var4 / 2 - 1, var1, var2 - 96 * var11 - 2, var1, var2 + 4, var1 - var3 / 2, var2 + var4 / 2 + 4, var8, var7, var5, var6);
         if (Core.bDebug && bWallColors) {
            var10 = -256;
            var9 = -256;
            var7 = -256;
            var5 = -256;
         }

         SpriteRenderer.instance.render((Texture)null, var1, var2 - 96 * var11, var1 + var3 / 2, var2 - 96 * var11 + var4 / 2, var1 + var3 / 2, var2 + var4 / 2 + 5, var1, var2 + 5, var7, var10, var9, var5);
      } catch (Exception var12) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var12);
      }

   }

   public void renderroofw(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20) {
      try {
         float var21 = this.getXStart();
         float var22 = this.getYStart();
         float var23 = this.getXEnd();
         float var24 = this.getYEnd();
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         var21 += (var23 - var21) * 0.01F;
         var22 += (var24 - var22) * 0.01F;
         var23 -= (var23 - var21) * 0.01F;
         var24 -= (var24 - var22) * 0.01F;
         var1 = (int)((float)var1 + this.offsetX);
         var2 = (int)((float)var2 + this.offsetY);
         var3 -= 4;
         if (Core.getInstance().bUseShaders) {
         }

         this.bindstrip(1.0F, 1.0F, 1.0F, 1.0F);
         int var26 = var2 + var4;
         int var25 = var1 + 32;
         var26 -= 50;
         var26 -= 32;
         GL11.glColor4f(var9, var10, var11, var12);
         GL11.glTexCoord2f(var21, var24);
         GL11.glVertex2i(var25, var26);
         GL11.glColor4f(var17, var18, var19, var20);
         GL11.glTexCoord2f(var21, var22);
         var25 += 32;
         var26 -= 16;
         GL11.glVertex2i(var25, var26);
         GL11.glColor4f(var13, var14, var15, var16);
         GL11.glTexCoord2f(var23, var22);
         var25 += 32;
         var26 += 48;
         GL11.glVertex2i(var25, var26);
         GL11.glColor4f(var5, var6, var7, var8);
         GL11.glTexCoord2f(var23, var24);
         var25 -= 32;
         var26 += 16;
         GL11.glVertex2i(var25, var26);
      } catch (Exception var27) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var27);
      }

   }

   public void renderwalln(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20) {
      try {
         float var21 = this.getXStart();
         float var22 = this.getYStart();
         float var23 = this.getXEnd();
         float var24 = this.getYEnd();
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         var21 += (var23 - var21) * 0.01F;
         var22 += (var24 - var22) * 0.01F;
         var23 -= (var23 - var21) * 0.01F;
         float var10000 = var24 - (var24 - var22) * 0.01F;
         var2 += 4;
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (Core.getInstance().bUseShaders) {
         }

         var1 -= 4;
         var1 += var3 / 2;
         var3 -= 2;
         var4 += 3;
         SpriteRenderer.instance.render((Texture)null, var1, var2 - 17, var1, var2 - 119, var1 + var3 / 2 + 4, var2 + var4 / 2 + 1 - 119, var1 + var3 / 2 + 4, var2 + var4 / 2 + 4 - 17, var5, var6, var7, var8, var13, var14, var15, var16, var17, var18, var19, var20, var9, var10, var11, var12);
      } catch (Exception var26) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var26);
      }

   }

   public void renderwalln(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      try {
         float var9 = this.getXStart();
         float var10 = this.getYStart();
         float var11 = this.getXEnd();
         float var12 = this.getYEnd();
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         var9 += (var11 - var9) * 0.01F;
         var10 += (var12 - var10) * 0.01F;
         var11 -= (var11 - var9) * 0.01F;
         float var10000 = var12 - (var12 - var10) * 0.01F;
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (Core.getInstance().bUseShaders) {
         }

         if (Core.bDebug && bWallColors) {
            var6 = -16776961;
            var5 = -16776961;
            var8 = -16777024;
            var7 = -16777024;
         }

         int var13 = Core.TileScale;
         SpriteRenderer.instance.render((Texture)null, var1 - 6, var2 - 96 * var13 - 3, var1 + var3 / 2, var2 - 96 * var13 + var4 / 2, var1 + var3 / 2, var2 + var4 / 2 + 5, var1 - 6, var2 + 2, var7, var8, var6, var5);
      } catch (Exception var14) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var14);
      }

   }

   public void renderwallncutoff(int var1, int var2, int var3, int var4) {
      try {
         float var5 = this.getXStart();
         float var6 = this.getYStart();
         float var7 = this.getXEnd();
         float var8 = this.getYEnd();
         lr = -1.0F;
         lg = -1.0F;
         lb = -1.0F;
         la = -1.0F;
         var5 += (var7 - var5) * 0.01F;
         var6 += (var8 - var6) * 0.01F;
         var7 -= (var7 - var5) * 0.01F;
         float var10000 = var8 - (var8 - var6) * 0.01F;
         var2 += 4;
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         if (Core.getInstance().bUseShaders) {
         }

         var1 -= 4;
         var1 += var3 / 2;
         var3 -= 2;
         SpriteRenderer.instance.render((Texture)null, var1 - 1, var2 - 16, var1 - 1, var2 - 15, var1 + var3 / 2 + 4, var2 + var4 / 2 + 1 - 15, var1 + var3 / 2 + 4, var2 + var4 / 2 + 4 - 17, -1, -1, -1, -1);
      } catch (Exception var10) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var10);
      }

   }

   public void renderstrip(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      try {
         if (var8 <= 0.0F) {
            return;
         }

         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         if (var6 > 1.0F) {
            var6 = 1.0F;
         }

         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         if (var8 > 1.0F) {
            var8 = 1.0F;
         }

         if (var5 < 0.0F) {
            var5 = 0.0F;
         }

         if (var6 < 0.0F) {
            var6 = 0.0F;
         }

         if (var7 < 0.0F) {
            var7 = 0.0F;
         }

         if (var8 < 0.0F) {
            var8 = 0.0F;
         }

         float var9 = this.getXStart();
         float var10 = this.getYStart();
         float var11 = this.getXEnd();
         float var12 = this.getYEnd();
         if (this.flip) {
            var1 = (int)((float)var1 + ((float)this.widthOrig - this.offsetX - (float)this.width));
            var2 = (int)((float)var2 + this.offsetY);
         } else {
            var1 = (int)((float)var1 + this.offsetX);
            var2 = (int)((float)var2 + this.offsetY);
         }

         SpriteRenderer.instance.render(this, var1, var2, var3, var4, var5, var6, var7, var8);
      } catch (Exception var14) {
         bDoingQuad = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var14);
      }

   }

   public void setAlphaForeach(int var1, int var2, int var3, int var4) {
      ImageData var5 = this.getTextureId().getImageData();
      if (var5 != null) {
         var5.makeTransp((byte)var1, (byte)var2, (byte)var3, (byte)var4);
      } else {
         WrappedBuffer var6 = this.getData();
         this.setData(ImageUtils.makeTransp(var6.getBuffer(), var1, var2, var3, var4, this.getWidthHW(), this.getHeightHW()));
         var6.dispose();
      }

      AlphaColorIndex var7 = new AlphaColorIndex(var1, var2, var3, var4);
      if (this.dataid.alphaList == null) {
         this.dataid.alphaList = new ArrayList();
      }

      if (!this.dataid.alphaList.contains(var7)) {
         this.dataid.alphaList.add(var7);
      }

   }

   public void setCustomizedTexture() {
      this.dataid.pathFileName = null;
   }

   public void setData(ByteBuffer var1) {
      this.dataid.setData(var1);
   }

   public void setMask(Mask var1) {
      this.mask = var1;
   }

   public void setName(String var1) {
      if (var1 != null) {
         if (var1.equals(this.name)) {
            if (!textures.containsKey(var1)) {
               textures.put(var1, this);
            }

         } else {
            if (textures.containsKey(var1)) {
            }

            if (textures.containsKey(this.name)) {
               textures.remove(this.name);
            }

            this.name = var1;
            textures.put(var1, this);
         }
      }
   }

   public void setRegion(int var1, int var2, int var3, int var4) {
      if (var1 >= 0 && var1 <= this.getWidthHW()) {
         if (var2 >= 0 && var2 <= this.getHeightHW()) {
            if (var3 > 0) {
               if (var4 > 0) {
                  if (var3 + var1 > this.getWidthHW()) {
                     var3 = this.getWidthHW() - var1;
                  }

                  if (var4 > this.getHeightHW()) {
                     var4 = this.getHeightHW() - var2;
                  }

                  this.xStart = (float)var1 / (float)this.getWidthHW();
                  this.yStart = (float)var2 / (float)this.getHeightHW();
                  this.xEnd = (float)(var1 + var3) / (float)this.getWidthHW();
                  this.yEnd = (float)(var2 + var4) / (float)this.getHeightHW();
                  this.width = var3;
                  this.height = var4;
               }
            }
         }
      }
   }

   public void setUseAlphaChannel(boolean var1) {
      this.dataid.solid = this.solid = !var1;
   }

   public Texture splitIcon() {
      if (this.splitIconTex == null) {
         this.splitIconTex = new Texture(this.getTextureId(), this.name + "_Icon");
         float var1 = this.xStart * (float)this.getWidthHW();
         float var2 = this.yStart * (float)this.getHeightHW();
         float var3 = this.xEnd * (float)this.getWidthHW() - var1;
         float var4 = this.yEnd * (float)this.getHeightHW() - var2;
         this.splitIconTex.setRegion((int)var1, (int)var2, (int)var3, (int)var4);
         this.splitIconTex.offsetX = 0.0F;
         this.splitIconTex.offsetY = 0.0F;
         texmap.put(this.name + "_Icon", this.splitIconTex);
      }

      return this.splitIconTex;
   }

   public Texture split(int var1, int var2, int var3, int var4) {
      Texture var5 = new Texture(this.getTextureId(), this.name + "_" + var1 + "_" + var2);
      var5.setRegion(var1, var2, var3, var4);
      return var5;
   }

   public Texture[] split(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      Texture[] var9 = new Texture[var3 * var4];

      for(int var10 = 0; var10 < var3; ++var10) {
         for(int var11 = 0; var11 < var4; ++var11) {
            var9[var11 + var10 * var4] = new Texture(this.getTextureId(), this.name + "_" + var3 + "_" + var4);
            var9[var11 + var10 * var4].setRegion(var1 + var11 * var5 + var7 * var11, var2 + var10 * var6 + var8 * var10, var5, var6);
            var9[var11 + var10 * var4].copyMaskRegion(this, var1 + var11 * var5 + var7 * var11, var2 + var10 * var6 + var8 * var10, var5, var6);
         }
      }

      return var9;
   }

   public Texture[][] split2D(int[] var1, int[] var2) {
      if (var1 != null && var2 != null) {
         Texture[][] var3 = new Texture[var1.length][var2.length];
         float var8 = 0.0F;
         float var6 = 0.0F;
         float var5 = 0.0F;

         for(int var9 = 0; var9 < var2.length; ++var9) {
            var6 += var8;
            var8 = (float)var2[var9] / (float)this.getHeightHW();
            var5 = 0.0F;

            for(int var10 = 0; var10 < var1.length; ++var10) {
               float var7 = (float)var1[var10] / (float)this.getWidthHW();
               Texture var4 = var3[var10][var9] = new Texture(this);
               var4.width = var1[var10];
               var4.height = var2[var9];
               var4.xStart = var5;
               var4.xEnd = var5 += var7;
               var4.yStart = var6;
               var4.yEnd = var6 + var8;
            }
         }

         return var3;
      } else {
         return (Texture[][])null;
      }
   }

   public String toString() {
      return this.name;
   }

   private void readVersion3(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (textures.containsKey(this.name)) {
         DebugLog.log("ERROR: Texture's name already loaded");
      } else {
         textures.put(this.name, this);
      }

      boolean var2 = var1.readBoolean();
      if (var2) {
         String var3 = (String)var1.readObject();
         boolean var4 = !var1.readBoolean();
         this.xStart = var1.readFloat();
         this.xEnd = var1.readFloat();
         this.yStart = var1.readFloat();
         this.yEnd = var1.readFloat();
         DebugLog.log("path: " + var3);
         this.dataid = new TextureID(var3);
         ++this.dataid.referenceCount;
         if (var4) {
            ArrayList var5 = (ArrayList)var1.readObject();

            for(int var7 = 0; var7 < var5.size(); ++var7) {
               AlphaColorIndex var6 = (AlphaColorIndex)var5.get(var7);
               this.makeTransp(var6.red, var6.green, var6.blue);
            }
         }
      } else {
         DebugLog.log("Loading runtime customized texture");
         this.dataid = (TextureID)var1.readObject();
      }

      if (var1.readBoolean()) {
         this.mask = (Mask)var1.readObject();
      }

   }

   public void saveMask(String var1) {
      this.mask.save(var1);
   }

   public void loadMaskRegion(ByteBuffer var1) {
      if (var1 != null) {
         this.mask = new Mask();
         this.mask.mask = new BooleanGrid(this.width, this.height);
         this.mask.mask.LoadFromByteBuffer(var1);
      }
   }

   public void saveMaskRegion(ByteBuffer var1) {
      if (var1 != null) {
         this.mask.mask.PutToByteBuffer(var1);
      }
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public void setHeight(int var1) {
      this.height = var1;
   }

   public int getRealWidth() {
      return this.realWidth;
   }

   public void setRealWidth(int var1) {
      this.realWidth = var1;
   }

   public int getRealHeight() {
      return this.realHeight;
   }

   public void setRealHeight(int var1) {
      this.realHeight = var1;
   }

   public void setOffsetX(int var1) {
      this.offsetX = (float)var1;
   }

   public void setOffsetY(int var1) {
      this.offsetY = (float)var1;
   }

   public static enum PZFileformat {
      PNG,
      DDS;
   }
}
