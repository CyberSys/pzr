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

public class TextureID implements IDestroyable, Serializable {
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

   public TextureID(int var1, int var2) {
      this.data = new ImageData(var1, var2);
      RenderThread.borrowContext();
      this.createTexture(false);
      RenderThread.returnContext();
   }

   public TextureID(ImageData var1) {
      this.data = var1;
      RenderThread.borrowContext();
      this.createTexture();
      RenderThread.returnContext();
   }

   public TextureID(String var1, String var2) {
      this.data = new ImageData(var1, var2);
      this.pathFileName = var1;
      RenderThread.borrowContext();
      this.createTexture();
      RenderThread.returnContext();
   }

   public TextureID(String var1, int[] var2) {
      this.data = new ImageData(var1, var2);
      this.pathFileName = var1;
      RenderThread.borrowContext();
      this.createTexture();
      RenderThread.returnContext();
   }

   public TextureID(String var1, int var2, int var3, int var4) {
      if (var1.startsWith("/")) {
         var1 = var1.substring(1);
      }

      int var5;
      while((var5 = var1.indexOf("\\")) != -1) {
         var1 = var1.substring(0, var5) + '/' + var1.substring(var5 + 1);
      }

      (this.data = new ImageData(var1)).makeTransp((byte)var2, (byte)var3, (byte)var4);
      if (this.alphaList == null) {
         this.alphaList = new ArrayList();
      }

      this.alphaList.add(new AlphaColorIndex(var2, var3, var4, 0));
      this.pathFileName = var1;
      this.createTexture();
   }

   public TextureID(String var1) {
      if (var1.toLowerCase().contains(".pcx")) {
         this.data = new ImageData(var1, var1);
      } else {
         this.data = new ImageData(var1);
      }

      if (this.data.getHeight() != -1) {
         this.pathFileName = var1;
         RenderThread.borrowContext();
         this.createTexture();
         RenderThread.returnContext();
      }
   }

   public TextureID(BufferedInputStream var1, String var2, boolean var3, Texture.PZFileformat var4) {
      this.data = new ImageData(var1, var3, var4);
      if (this.data.id != -1) {
         this.id = this.data.id;
         this.width = this.data.getWidth();
         this.height = this.data.getHeight();
         this.widthHW = this.data.getWidthHW();
         this.heightHW = this.data.getHeightHW();
         totalGraphicMemory += (long)(this.widthHW * this.heightHW * 8);
         this.solid = this.data.isSolid();
      } else {
         if (var3) {
            this.mask = this.data.mask;
            this.data.mask = null;
         }

         this.createTexture();
      }

      this.pathFileName = var2;
   }

   public TextureID(BufferedInputStream var1, String var2, boolean var3) {
      this.data = new ImageData(var1, var3);
      if (var3) {
         this.mask = this.data.mask;
         this.data.mask = null;
      }

      this.pathFileName = var2;
      this.createTexture();
   }

   public static TextureID createSteamAvatar(long var0) {
      ImageData var2 = ImageData.createSteamAvatar(var0);
      if (var2 == null) {
         return null;
      } else {
         TextureID var3 = new TextureID(var2);
         return var3;
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
      WrappedBuffer var1 = DirectBufferAllocator.allocate(this.heightHW * this.widthHW * 4);
      GL11.glGetTexImage(3553, 0, 6408, 5121, var1.getBuffer());
      Texture.lastTextureID = 0;
      GL11.glBindTexture(3553, 0);
      return var1;
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

   public void setData(ByteBuffer var1) {
      if (var1 == null) {
         this.freeMemory();
      } else {
         this.bind();
         GL11.glTexSubImage2D(3553, 0, 0, 0, this.widthHW, this.heightHW, 6408, 5121, var1);
         if (this.data != null) {
            WrappedBuffer var2 = this.data.getData();
            ByteBuffer var3 = var2.getBuffer();
            var1.flip();
            var3.clear();
            var3.put(var1);
            var3.flip();
         }

         if (USE_MIPMAP) {
         }

      }
   }

   public void setImageData(ImageData var1) {
      this.data = var1;
   }

   private void createTexture() {
      this.createTexture(true);
   }

   private void createTexture(boolean var1) {
      this.width = this.data.getWidth();
      this.height = this.data.getHeight();
      this.widthHW = this.data.getWidthHW();
      this.heightHW = this.data.getHeightHW();
      totalGraphicMemory += (long)(this.widthHW * this.heightHW * 8);
      this.solid = this.data.isSolid();
      if (!FrameLoader.bDedicated) {
         this.generateHwId(var1);
      }

   }

   private void generateHwId(boolean var1) {
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
      char var2 = 6408;
      if (bUseCompression && GLContext.getCapabilities().GL_ARB_texture_compression) {
         var2 = '蓮';
      }

      GL11.glTexImage2D(3553, 0, var2, this.widthHW, this.heightHW, 0, 6408, 5121, var1 ? this.data.getData().getBuffer() : null);
      if (FREE_MEMORY) {
         this.data = null;
      }

      TextureIDMap.put(this.id, this.pathFileName);
   }

   public void generateMipmap() {
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      boolean var2 = var1.readBoolean();
      if (!var2) {
         this.data = (ImageData)var1.readObject();
         var1.defaultReadObject();
      } else {
         this.data = new ImageData(this.pathFileName);
         var1.defaultReadObject();
      }

      this.createTexture();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      boolean var2 = this.pathFileName == null;
      if (!var2) {
         if (this.data == null) {
            this.data = new ImageData(this, (WrappedBuffer)null);
         }

         var1.writeBoolean(false);
         var1.writeObject(this.data);
         var1.defaultWriteObject();
      } else {
         var1.writeBoolean(true);
         var1.defaultWriteObject();
      }

   }
}
