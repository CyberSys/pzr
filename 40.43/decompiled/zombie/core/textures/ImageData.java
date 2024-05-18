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

   public ImageData(TextureID var1, WrappedBuffer var2) {
      data = var2;
      this.width = var1.width;
      this.widthHW = var1.widthHW;
      this.height = var1.height;
      this.heightHW = var1.heightHW;
      this.solid = var1.solid;
   }

   public void Load(BufferedImage var1) {
      byte[] var2 = (byte[])((byte[])var1.getRaster().getDataElements(0, 0, this.width, this.height, (Object)null));
      int var3 = 0;
      int var4 = 0;
      ByteBuffer var10000;
      int var10001;
      int var5;
      if (4 * this.widthHW * this.heightHW == var2.length) {
         for(var5 = 0; var5 < var2.length; ++var5) {
            var10000 = data.getBuffer().put(var2[var5]);
            ++var5;
            var10000 = var10000.put(var2[var5]);
            ++var5;
            var10000 = var10000.put(var2[var5]);
            ++var5;
            var10000.put(var2[var5]);
            ++var3;
            if (var3 == this.width) {
               var10000 = data.getBuffer();
               var10001 = this.widthHW * 4;
               ++var4;
               var10000.position(var10001 * var4);
               var3 = 0;
            }
         }
      } else {
         for(var5 = 0; var5 < var2.length; ++var5) {
            var10000 = data.getBuffer().put(var2[var5]);
            ++var5;
            var10000 = var10000.put(var2[var5]);
            ++var5;
            var10000.put(var2[var5]).put((byte)-1);
            ++var3;
            if (var3 == this.width) {
               var10000 = data.getBuffer();
               var10001 = this.widthHW * 4;
               ++var4;
               var10000.position(var10001 * var4);
               var3 = 0;
            }
         }
      }

      data.getBuffer().rewind();
   }

   public ImageData(BufferedImage var1) {
      this.width = var1.getWidth();
      this.height = var1.getHeight();
      this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
      this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
      if (data == null) {
         data = DirectBufferAllocator.allocate(BufferSize);
      }

      ByteBuffer var2 = data.getBuffer();
      byte[] var3 = (byte[])((byte[])var1.getRaster().getDataElements(0, 0, this.width, this.height, (Object)null));
      int var4 = 0;
      int var5 = 0;
      ByteBuffer var10000;
      int var10001;
      int var6;
      if (4 * this.widthHW * this.heightHW == var3.length) {
         for(var6 = 0; var6 < var3.length; ++var6) {
            var10000 = var2.put(var3[var6]);
            ++var6;
            var10000 = var10000.put(var3[var6]);
            ++var6;
            var10000 = var10000.put(var3[var6]);
            ++var6;
            var10000.put(var3[var6]);
            ++var4;
            if (var4 == this.width) {
               var10001 = this.widthHW * 4;
               ++var5;
               var2.position(var10001 * var5);
               var4 = 0;
            }
         }
      } else {
         for(var6 = 0; var6 < var3.length; ++var6) {
            var10000 = var2.put(var3[var6]);
            ++var6;
            var10000 = var10000.put(var3[var6]);
            ++var6;
            var10000.put(var3[var6]).put((byte)-1);
            ++var4;
            if (var4 == this.width) {
               var10001 = this.widthHW * 4;
               ++var5;
               var2.position(var10001 * var5);
               var4 = 0;
            }
         }
      }

      var2.rewind();
   }

   public ImageData(String var1) {
      if (var1.contains(".txt")) {
         var1 = var1.replace(".txt", ".png");
      }

      FileInputStream var2;
      int var3;
      for(var2 = null; (var3 = var1.indexOf("\\")) != -1; var1 = var1.substring(0, var3) + '/' + var1.substring(var3 + 1)) {
      }

      if (var2 == null) {
         try {
            var2 = new FileInputStream(ZomboidFileSystem.instance.getString(var1));
         } catch (FileNotFoundException var8) {
         }

         if (var2 == null) {
            this.width = this.height = -1;
            if (Texture.WarnFailFindTexture && Core.bDebug) {
            }

            return;
         }
      }

      assert var2 != null;

      try {
         PNGDecoder var9 = new PNGDecoder(var2, false);
         this.width = var9.getWidth();
         this.height = var9.getHeight();
         this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
         this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
         if (data == null) {
            data = DirectBufferAllocator.allocate(BufferSize);
         }

         ByteBuffer var4 = data.getBuffer();
         var4.rewind();
         var9.decode(data.getBuffer(), 4 * ImageUtils.getNextPowerOfTwoHW(var9.getWidth()), PNGDecoder.Format.RGBA);
      } catch (IOException var6) {
         this.width = this.height = -1;
         var6.printStackTrace();
      } catch (UnsupportedOperationException var7) {
         this.width = this.height = -1;
         DebugLog.log(var7.getMessage());
      }

      try {
         var2.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public ImageData(int var1, int var2) {
      this.width = var1;
      this.height = var2;
      this.widthHW = ImageUtils.getNextPowerOfTwoHW(var1);
      this.heightHW = ImageUtils.getNextPowerOfTwoHW(var2);
      if (data == null) {
         data = DirectBufferAllocator.allocate(BufferSize);
      }

   }

   ImageData(String var1, String var2) {
      Pcx var3 = new Pcx(var1, var2);
      this.width = var3.imageWidth;
      this.height = var3.imageHeight;
      this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
      this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
      if (data == null) {
         data = DirectBufferAllocator.allocate(BufferSize);
      }

      this.setData(var3);
      this.makeTransp((byte)var3.palette[762], (byte)var3.palette[763], (byte)var3.palette[764], (byte)0);
   }

   ImageData(String var1, int[] var2) {
      Pcx var3 = new Pcx(var1, var2);
      this.width = var3.imageWidth;
      this.height = var3.imageHeight;
      this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
      this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
      if (data == null) {
         data = DirectBufferAllocator.allocate(BufferSize);
      }

      this.setData(var3);
      this.makeTransp((byte)var3.palette[762], (byte)var3.palette[763], (byte)var3.palette[764], (byte)0);
   }

   public ImageData(BufferedInputStream var1, boolean var2, Texture.PZFileformat var3) {
      if (var3 == Texture.PZFileformat.DDS) {
         this.id = dds.loadDDSFile(var1);
         this.width = DDSLoader.lastWid;
         DDSLoader var10001 = dds;
         this.height = DDSLoader.lastHei;
         this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
         this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
      }

   }

   public ImageData(BufferedInputStream var1, boolean var2) {
      Object var3 = null;

      try {
         PNGDecoder var4 = new PNGDecoder(var1, var2);
         this.width = var4.getWidth();
         this.height = var4.getHeight();
         this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
         this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
         if (data == null) {
            data = DirectBufferAllocator.allocate(BufferSize);
         }

         ByteBuffer var5 = data.getBuffer();
         var5.rewind();
         var4.decode(data.getBuffer(), 4 * ImageUtils.getNextPowerOfTwoHW(var4.getWidth()), PNGDecoder.Format.RGBA);
         if (var2) {
            this.mask = var4.mask;
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public static ImageData createSteamAvatar(long var0) {
      if (data == null) {
         data = DirectBufferAllocator.allocate(BufferSize);
      }

      int var2 = SteamFriends.CreateSteamAvatar(var0, data.getBuffer());
      if (var2 <= 0) {
         return null;
      } else {
         int var3 = data.getBuffer().position() / (var2 * 4);
         data.getBuffer().clear();
         ImageData var4 = new ImageData(var2, var3);
         return var4;
      }
   }

   public WrappedBuffer getData() {
      if (data == null) {
         data = DirectBufferAllocator.allocate(BufferSize);
      }

      data.getBuffer().rewind();
      return data;
   }

   public void makeTransp(byte var1, byte var2, byte var3) {
      this.makeTransp(var1, var2, var3, (byte)0);
   }

   public void makeTransp(byte var1, byte var2, byte var3, byte var4) {
      this.solid = false;
      ByteBuffer var5 = data.getBuffer();
      var5.rewind();
      int var10 = this.widthHW * 4;

      for(int var11 = 0; var11 < this.heightHW; ++var11) {
         int var9 = var5.position();

         for(int var12 = 0; var12 < this.widthHW; ++var12) {
            byte var6 = var5.get();
            byte var7 = var5.get();
            byte var8 = var5.get();
            if (var6 == var1 && var7 == var2 && var8 == var3) {
               var5.put(var4);
            } else {
               var5.get();
            }

            if (var12 == this.width) {
               var5.position(var9 + var10);
               break;
            }
         }

         if (var11 == this.height) {
            break;
         }
      }

      var5.rewind();
   }

   public void setData(BufferedImage var1) {
      if (var1 != null) {
         this.setData(var1.getData());
      }

   }

   public void setData(Raster var1) {
      if (var1 == null) {
         (new Exception()).printStackTrace();
      } else {
         this.width = var1.getWidth();
         this.height = var1.getHeight();
         if (this.width <= this.widthHW && this.height <= this.heightHW) {
            int[] var2 = var1.getPixels(0, 0, this.width, this.height, (int[])null);
            ByteBuffer var3 = data.getBuffer();
            var3.rewind();
            int var4 = 0;
            int var5 = var3.position();
            int var6 = this.widthHW * 4;

            for(int var7 = 0; var7 < var2.length; ++var7) {
               ++var4;
               if (var4 > this.width) {
                  var3.position(var5 + var6);
                  var5 = var3.position();
                  var4 = 1;
               }

               var3.put((byte)var2[var7]);
               ++var7;
               var3.put((byte)var2[var7]);
               ++var7;
               var3.put((byte)var2[var7]);
               ++var7;
               var3.put((byte)var2[var7]);
            }

            var3.rewind();
            this.solid = false;
         } else {
            (new Exception()).printStackTrace();
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      data = DirectBufferAllocator.allocate(4 * this.widthHW * this.heightHW);
      ByteBuffer var2 = data.getBuffer();

      for(int var3 = 0; var3 < this.widthHW * this.heightHW; ++var3) {
         var2.put(var1.readByte()).put(var1.readByte()).put(var1.readByte()).put(var1.readByte());
      }

      var2.flip();
   }

   private void setData(Pcx var1) {
      this.width = var1.imageWidth;
      this.height = var1.imageHeight;
      if (this.width <= this.widthHW && this.height <= this.heightHW) {
         ByteBuffer var2 = data.getBuffer();
         var2.rewind();
         int var3 = 0;
         int var4 = var2.position();
         int var5 = this.widthHW * 4;

         for(int var6 = 0; var6 < this.heightHW * this.widthHW * 3; ++var6) {
            ++var3;
            if (var3 > this.width) {
               var4 = var2.position();
               var3 = 1;
            }

            var2.put(var1.imageData[var6]);
            ++var6;
            var2.put(var1.imageData[var6]);
            ++var6;
            var2.put(var1.imageData[var6]);
            var2.put((byte)-1);
         }

         var2.rewind();
         this.solid = false;
      } else {
         (new Exception()).printStackTrace();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      ByteBuffer var2 = data.getBuffer();
      var2.rewind();

      for(int var3 = 0; var3 < this.widthHW * this.heightHW; ++var3) {
         var1.writeByte(var2.get());
         var1.writeByte(var2.get());
         var1.writeByte(var2.get());
         var1.writeByte(var2.get());
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
