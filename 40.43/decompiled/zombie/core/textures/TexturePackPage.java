package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import zombie.core.Core;
import zombie.scripting.commands.LoadTexturePage;
import zombie.ui.TextManager;

public class TexturePackPage {
   public static HashMap FoundTextures = new HashMap();
   public static HashMap subTextureMap = new HashMap();
   public static HashMap subTextureMap2 = new HashMap();
   public static HashMap texturePackPageMap = new HashMap();
   public static HashMap TexturePackPageNameMap = new HashMap();
   public HashMap subTextures = new HashMap();
   public Texture tex = null;
   static ByteBuffer SliceBuffer = null;
   static boolean bHasCache = false;
   static int percent = 0;
   public static int chl1 = 0;
   public static int chl2 = 0;
   public static int chl3 = 0;
   public static int chl4 = 0;
   static StringBuilder v = new StringBuilder(50);
   public static ArrayList TempSubTextureInfo = new ArrayList();
   public static ArrayList tempFilenameCheck = new ArrayList();
   public static boolean bIgnoreWorldItemTextures = true;

   public static void LoadDir(String var0) throws URISyntaxException {
   }

   public static void searchFolders(File var0) {
   }

   public static void LoadDirListing(String var0) throws URISyntaxException {
      bHasCache = false;
      File var1 = new File("media/" + var0);
      Object var2 = null;
      String[] var3 = var1.list();
      int var4 = var3.length;
      int var5 = -1;

      for(int var6 = 0; var6 < var3.length; ++var6) {
         if (percent > var5) {
            Core.getInstance().StartFrame();
            Core.getInstance().EndFrame();
            Core.getInstance().StartFrameUI();
            TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), "Loading... " + percent + "%", 1.0D, 1.0D, 1.0D, 1.0D);
            Core.getInstance().EndFrameUI();
            Display.update();
            var5 = percent;
         }

         String var7 = var3[var6];
         File var8 = new File(var7);
         searchFoldersListing(var8);
         float var9 = (float)var6 / (float)var4 * 100.0F;
         percent = (int)var9;
         ++percent;
         if (percent > 100) {
            percent = 100;
         }

         if (percent < 1) {
            percent = 1;
         }

         if (var5 == 100) {
            percent = 100;
         }
      }

      Core.getInstance().StartFrame();
      Core.getInstance().EndFrame();
      Core.getInstance().StartFrameUI();
      TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), "Loading completed.", 1.0D, 1.0D, 1.0D, 1.0D);
      Core.getInstance().EndFrameUI();
      File var10 = new File(Core.getMyDocumentFolder() + File.separator + "mods" + var0);
      searchFoldersListing(var10);
   }

   public static void searchFoldersListing(File var0) {
      if (var0.isDirectory()) {
         String[] var1 = var0.list();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            searchFoldersListing(new File(var0.getAbsolutePath() + "\\" + var1[var2]));
         }
      } else if (var0.getAbsolutePath().toLowerCase().contains(".txt")) {
         getPackPageListing(var0.getName().replace(".txt", ""));
      }

   }

   public static void getPackPageListing(String var0) {
      TexturePackPage var1 = new TexturePackPage();

      try {
         var1.loadlisting("media/texturepacks/" + var0 + ".txt", var0, (Stack)null, SliceBuffer, bHasCache);
      } catch (IOException var3) {
         Logger.getLogger(TexturePackPage.class.getName()).log(Level.SEVERE, (String)null, var3);
      }
   }

   public static TexturePackPage getPackPage(String var0, Stack var1) {
      return texturePackPageMap.containsKey(var0) ? (TexturePackPage)texturePackPageMap.get(var0) : null;
   }

   public static TexturePackPage getPackPage(String var0) {
      if (!var0.equals("ui")) {
         TextureID.UseFiltering = true;
      } else {
         TextureID.UseFiltering = false;
      }

      return getPackPage(var0, (Stack)null);
   }

   public static Texture getTexture(String var0) {
      if (var0.contains(".png")) {
         return Texture.getSharedTexture(var0);
      } else {
         return subTextureMap.containsKey(var0) ? (Texture)subTextureMap.get(var0) : null;
      }
   }

   public static int readInt(BufferedInputStream var0) throws EOFException, IOException {
      int var1 = var0.read();
      int var2 = var0.read();
      int var3 = var0.read();
      int var4 = var0.read();
      chl1 = var1;
      chl2 = var2;
      chl3 = var3;
      chl4 = var4;
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24);
      }
   }

   public static int readInt(ByteBuffer var0) throws EOFException, IOException {
      byte var1 = var0.get();
      byte var2 = var0.get();
      byte var3 = var0.get();
      byte var4 = var0.get();
      chl1 = var1;
      chl2 = var2;
      chl3 = var3;
      chl4 = var4;
      return (var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24);
   }

   public static int readIntByte(BufferedInputStream var0) throws EOFException, IOException {
      int var1 = chl2;
      int var2 = chl3;
      int var3 = chl4;
      int var4 = var0.read();
      chl1 = var1;
      chl2 = var2;
      chl3 = var3;
      chl4 = var4;
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24);
      }
   }

   public static String ReadString(BufferedInputStream var0) throws IOException {
      v.setLength(0);
      int var1 = readInt(var0);

      for(int var2 = 0; var2 < var1; ++var2) {
         v.append((char)var0.read());
      }

      return v.toString();
   }

   public void loadFromPackFileDDS(BufferedInputStream var1) throws IOException {
      String var2 = ReadString(var1);
      tempFilenameCheck.add(var2);
      int var3 = readInt(var1);
      boolean var4 = readInt(var1) != 0;
      TempSubTextureInfo.clear();

      for(int var5 = 0; var5 < var3; ++var5) {
         String var6 = ReadString(var1);
         int var7 = readInt(var1);
         int var8 = readInt(var1);
         int var9 = readInt(var1);
         int var10 = readInt(var1);
         int var11 = readInt(var1);
         int var12 = readInt(var1);
         int var13 = readInt(var1);
         int var14 = readInt(var1);
         TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(var7, var8, var9, var10, var11, var12, var13, var14, var6));
      }

      Texture var15 = new Texture(var2, var1, var4, Texture.PZFileformat.DDS);

      int var16;
      for(var16 = 0; var16 < TempSubTextureInfo.size(); ++var16) {
         TexturePackPage.SubTextureInfo var18 = (TexturePackPage.SubTextureInfo)TempSubTextureInfo.get(var16);
         Texture var19 = var15.split(var18.x, var18.y, var18.w, var18.h);
         var19.copyMaskRegion(var15, var18.x, var18.y, var18.w, var18.h);
         var19.setName(var18.name);
         this.subTextures.put(var18.name, var19);
         subTextureMap.put(var18.name, var19);
         var19.offsetX = (float)var18.ox;
         var19.offsetY = (float)var18.oy;
         var19.widthOrig = var18.fx;
         var19.heightOrig = var18.fy;
      }

      var15.mask = null;
      texturePackPageMap.put(var2, this);
      boolean var17 = false;

      do {
         var16 = readIntByte(var1);
      } while(var16 != -559038737);

   }

   public void loadFromPackFile(BufferedInputStream var1) throws IOException {
      String var2 = ReadString(var1);
      tempFilenameCheck.add(var2);
      int var3 = readInt(var1);
      boolean var4 = readInt(var1) != 0;
      if (var4) {
         boolean var5 = false;
      }

      TempSubTextureInfo.clear();

      for(int var15 = 0; var15 < var3; ++var15) {
         String var6 = ReadString(var1);
         int var7 = readInt(var1);
         int var8 = readInt(var1);
         int var9 = readInt(var1);
         int var10 = readInt(var1);
         int var11 = readInt(var1);
         int var12 = readInt(var1);
         int var13 = readInt(var1);
         int var14 = readInt(var1);
         if (!bIgnoreWorldItemTextures || !var6.startsWith("WItem_")) {
            TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(var7, var8, var9, var10, var11, var12, var13, var14, var6));
         }
      }

      Texture var16 = new Texture(var2, var1, var4);

      int var17;
      for(var17 = 0; var17 < TempSubTextureInfo.size(); ++var17) {
         TexturePackPage.SubTextureInfo var19 = (TexturePackPage.SubTextureInfo)TempSubTextureInfo.get(var17);
         Texture var20 = var16.split(var19.x, var19.y, var19.w, var19.h);
         var20.copyMaskRegion(var16, var19.x, var19.y, var19.w, var19.h);
         var20.setName(var19.name);
         this.subTextures.put(var19.name, var20);
         subTextureMap.put(var19.name, var20);
         var20.offsetX = (float)var19.ox;
         var20.offsetY = (float)var19.oy;
         var20.widthOrig = var19.fx;
         var20.heightOrig = var19.fy;
      }

      var16.mask = null;
      texturePackPageMap.put(var2, this);
      boolean var18 = false;

      do {
         var17 = readIntByte(var1);
      } while(var17 != -559038737);

   }

   public void load(String var1, Stack var2, ByteBuffer var3, boolean var4) throws IOException, FileNotFoundException {
      this.tex = Texture.getSharedTexture(var1.replace(".txt", ".png"), false);
      if (this.tex != null) {
         FileInputStream var5 = new FileInputStream(var1);
         InputStreamReader var6 = new InputStreamReader(var5);
         BufferedReader var7 = new BufferedReader(var6);
         boolean var9 = false;

         while(true) {
            String var8;
            while((var8 = var7.readLine()) != null) {
               if (var8.contains("##nomask")) {
                  var9 = true;
                  this.tex.dataid.data = null;
               } else {
                  if (!var9 && this.tex.getMask() == null) {
                     if (!var4) {
                     }

                     this.tex.dataid.data = null;
                  }

                  String[] var10 = var8.split("=");
                  if (var10.length != 1) {
                     String var11 = var10[0].trim();
                     String var12 = var10[1].trim();
                     String[] var13 = var12.split(" ");
                     if (var11 != null) {
                        int var14 = Integer.parseInt(var13[0]);
                        int var15 = Integer.parseInt(var13[1]);
                        int var16 = Integer.parseInt(var13[2]);
                        int var17 = Integer.parseInt(var13[3]);
                        int var18 = Integer.parseInt(var13[4]);
                        int var19 = Integer.parseInt(var13[5]);
                        int var20 = Integer.parseInt(var13[6]);
                        int var21 = Integer.parseInt(var13[7]);
                        Texture var22 = new Texture(this.tex.dataid, var11);
                        var22.offsetX = (float)var18;
                        var22.offsetY = (float)var19;
                        var22.widthOrig = var20;
                        var22.heightOrig = var21;
                        var22.width = var16;
                        var22.height = var17;
                        var22.xStart = (float)var14 / (float)this.tex.getWidthHW();
                        var22.yStart = (float)var15 / (float)this.tex.getHeightHW();
                        var22.xEnd = (float)(var14 + var16) / (float)this.tex.getWidthHW();
                        var22.yEnd = (float)(var15 + var17) / (float)this.tex.getHeightHW();
                        this.subTextures.put(var11, var22);
                        Integer var23 = var22.dataid.id;
                        if (var2 != null) {
                           for(int var24 = 0; var24 < var2.size(); ++var24) {
                              LoadTexturePage.WatchPair var25 = (LoadTexturePage.WatchPair)var2.get(var24);
                              if (var11.contains(var25.token)) {
                                 if (FoundTextures.containsKey(var25.name)) {
                                    ((Stack)FoundTextures.get(var25.name)).add(var11);
                                 } else {
                                    FoundTextures.put(var25.name, new Stack());
                                    ((Stack)FoundTextures.get(var25.name)).add(var11);
                                 }
                              }
                           }
                        }

                        subTextureMap.put(var11, var22);
                        subTextureMap2.put(var11 + "_" + var23.toString(), var22);
                        if (var3 == null) {
                           var4 = false;
                        }

                        if (!var9) {
                           if (!var4) {
                              var22.copyMaskRegion(this.tex, var14, var15, var16, var17);
                              var22.saveMaskRegion(var3);
                           } else {
                              var22.loadMaskRegion(var3);
                           }
                        }
                     }
                  }
               }
            }

            var7.close();
            var6.close();
            var5.close();
            return;
         }
      }
   }

   public void loadlisting(String var1, String var2, Stack var3, ByteBuffer var4, boolean var5) throws IOException, FileNotFoundException {
      FileInputStream var6 = new FileInputStream(var1);
      InputStreamReader var7 = new InputStreamReader(var6);
      BufferedReader var8 = new BufferedReader(var7);
      boolean var10 = false;

      String var9;
      while((var9 = var8.readLine()) != null) {
         if (!var9.contains("##nomask")) {
            String[] var11 = var9.split("=");
            if (var11.length != 1) {
               String var12 = var11[0].trim();
               TexturePackPageNameMap.put(var12, var2);
            }
         }
      }

      var8.close();
      var7.close();
      var6.close();
   }

   public static class SubTextureInfo {
      public int w;
      public int h;
      public int x;
      public int y;
      public int ox;
      public int oy;
      public int fx;
      public int fy;
      public String name;

      public SubTextureInfo(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, String var9) {
         this.x = var1;
         this.y = var2;
         this.w = var3;
         this.h = var4;
         this.ox = var5;
         this.oy = var6;
         this.fx = var7;
         this.fy = var8;
         this.name = var9;
      }
   }
}
