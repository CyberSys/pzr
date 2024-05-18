package zombie.debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;

public class LineDrawer {
   private static final long serialVersionUID = -8792265397633463907L;
   public static int red = 0;
   public static int green = 255;
   public static int blue = 0;
   public static int alpha = 255;
   static int idLayer = -1;
   static ArrayList lines = new ArrayList();
   static ArrayDeque pool = new ArrayDeque();
   private static int layer;
   static Vector2 tempo = new Vector2();
   static Vector2 tempo2 = new Vector2();

   static void DrawTexturedRect(Texture var0, float var1, float var2, float var3, float var4, int var5, float var6, float var7, float var8) {
      var1 = (float)((int)var1);
      var2 = (float)((int)var2);
      Vector2 var9 = new Vector2(var1, var2);
      Vector2 var10 = new Vector2(var1 + var3, var2);
      Vector2 var11 = new Vector2(var1 + var3, var2 + var4);
      Vector2 var12 = new Vector2(var1, var2 + var4);
      Vector2 var13 = new Vector2(IsoUtils.XToScreen(var9.x, var9.y, (float)var5, 0), IsoUtils.YToScreen(var9.x, var9.y, (float)var5, 0));
      Vector2 var14 = new Vector2(IsoUtils.XToScreen(var10.x, var10.y, (float)var5, 0), IsoUtils.YToScreen(var10.x, var10.y, (float)var5, 0));
      Vector2 var15 = new Vector2(IsoUtils.XToScreen(var11.x, var11.y, (float)var5, 0), IsoUtils.YToScreen(var11.x, var11.y, (float)var5, 0));
      Vector2 var16 = new Vector2(IsoUtils.XToScreen(var12.x, var12.y, (float)var5, 0), IsoUtils.YToScreen(var12.x, var12.y, (float)var5, 0));
      var13.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
      var14.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
      var15.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
      var16.x -= IsoCamera.OffX[IsoPlayer.getPlayerIndex()];
      var13.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
      var14.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
      var15.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
      var16.y -= IsoCamera.OffY[IsoPlayer.getPlayerIndex()];
      float var17 = -240.0F;
      var17 -= 128.0F;
      float var18 = -32.0F;
      var13.y -= var17;
      var14.y -= var17;
      var15.y -= var17;
      var16.y -= var17;
      var13.x -= var18;
      var14.x -= var18;
      var15.x -= var18;
      var16.x -= var18;
      SpriteRenderer.instance.renderdebug(var0, (int)var13.x, (int)var13.y, (int)var14.x, (int)var14.y, (int)var15.x, (int)var15.y, (int)var16.x, (int)var16.y, var6, var7, var8, 1.0F, var6, var7, var8, 1.0F, var6, var7, var8, 1.0F, var6, var7, var8, 1.0F);
   }

   static void DrawIsoLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      tempo = new Vector2(var0, var1);
      tempo2 = new Vector2(var2, var3);
      Vector2 var9 = new Vector2(IsoUtils.XToScreen(tempo.x, tempo.y, 0.0F, 0), IsoUtils.YToScreen(tempo.x, tempo.y, 0.0F, 0));
      Vector2 var10 = new Vector2(IsoUtils.XToScreen(tempo2.x, tempo2.y, 0.0F, 0), IsoUtils.YToScreen(tempo2.x, tempo2.y, 0.0F, 0));
      var9.x -= IsoCamera.getOffX();
      var10.x -= IsoCamera.getOffX();
      var9.y -= IsoCamera.getOffY();
      var10.y -= IsoCamera.getOffY();
      drawLine(var9.x, var9.y, var10.x, var10.y, var4, var5, var6, var7, var8);
   }

   static void DrawIsoRect(float var0, float var1, float var2, float var3, int var4, float var5, float var6, float var7) {
      if (var2 < 0.0F) {
         var2 = -var2;
         var0 -= var2;
      }

      if (var3 < 0.0F) {
         var3 = -var3;
         var1 -= var3;
      }

      float var8 = IsoUtils.XToScreen(var0, var1, (float)var4, 0);
      float var9 = IsoUtils.YToScreen(var0, var1, (float)var4, 0);
      float var10 = IsoUtils.XToScreen(var0 + var2, var1, (float)var4, 0);
      float var11 = IsoUtils.YToScreen(var0 + var2, var1, (float)var4, 0);
      float var12 = IsoUtils.XToScreen(var0 + var2, var1 + var3, (float)var4, 0);
      float var13 = IsoUtils.YToScreen(var0 + var2, var1 + var3, (float)var4, 0);
      float var14 = IsoUtils.XToScreen(var0, var1 + var3, (float)var4, 0);
      float var15 = IsoUtils.YToScreen(var0, var1 + var3, (float)var4, 0);
      var8 -= IsoCamera.getOffX();
      var10 -= IsoCamera.getOffX();
      var12 -= IsoCamera.getOffX();
      var14 -= IsoCamera.getOffX();
      var9 -= IsoCamera.getOffY();
      var11 -= IsoCamera.getOffY();
      var13 -= IsoCamera.getOffY();
      var15 -= IsoCamera.getOffY();
      drawLine(var8, var9, var10, var11, var5, var6, var7);
      drawLine(var10, var11, var12, var13, var5, var6, var7);
      drawLine(var12, var13, var14, var15, var5, var6, var7);
      drawLine(var14, var15, var8, var9, var5, var6, var7);
   }

   public static void DrawIsoLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      float var11 = IsoUtils.XToScreenExact(var0, var1, var2, 0);
      float var12 = IsoUtils.YToScreenExact(var0, var1, var2, 0);
      float var13 = IsoUtils.XToScreenExact(var3, var4, var5, 0);
      float var14 = IsoUtils.YToScreenExact(var3, var4, var5, 0);
      drawLine(var11, var12, var13, var14, var6, var7, var8, var9, var10);
   }

   static void drawLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      SpriteRenderer.instance.renderline((Texture)null, (int)var0 - 1, (int)var1 - 1, (int)var2 - 1, (int)var3 - 1, 0.0F, 0.0F, 0.0F, 0.5F);
      SpriteRenderer.instance.renderline((Texture)null, (int)var0, (int)var1, (int)var2, (int)var3, var4, var5, var6, 1.0F);
   }

   public static void drawLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      SpriteRenderer.instance.renderline((Texture)null, (int)var0, (int)var1, (int)var2, (int)var3, var4, var5, var6, var7);
   }

   public static void drawRect(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      SpriteRenderer.instance.render((Texture)null, var0, var1 + (float)var8, (float)var8, var3 - (float)(var8 * 2), var4, var5, var6, var7);
      SpriteRenderer.instance.render((Texture)null, var0, var1, var2, (float)var8, var4, var5, var6, var7);
      SpriteRenderer.instance.render((Texture)null, var0 + var2 - (float)var8, var1 + (float)var8, 1.0F, var3 - (float)(var8 * 2), var4, var5, var6, var7);
      SpriteRenderer.instance.render((Texture)null, var0, var1 + var3 - (float)var8, var2, (float)var8, var4, var5, var6, var7);
   }

   public static void addLine(float var0, float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, String var9) {
      addLine(var0, var1, var2, var3, var4, var5, (float)var6, (float)var7, (float)var8, var9, true);
   }

   public static void addLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, String var9, boolean var10) {
      LineDrawer.DrawableLine var11 = pool.isEmpty() ? new LineDrawer.DrawableLine() : (LineDrawer.DrawableLine)pool.pop();
      lines.add(var11.init(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   public static void clear() {
      pool.addAll(lines);
      lines.clear();
   }

   public void removeLine(String var1) {
      for(int var2 = 0; var2 < lines.size(); ++var2) {
         if (((LineDrawer.DrawableLine)lines.get(var2)).name.equals(var1)) {
            lines.remove(lines.get(var2));
            --var2;
         }
      }

   }

   public static void render() {
      for(int var0 = 0; var0 < lines.size(); ++var0) {
         LineDrawer.DrawableLine var1 = (LineDrawer.DrawableLine)lines.get(var0);
         if (!var1.bLine) {
            DrawIsoRect(var1.xstart, var1.ystart, var1.xend - var1.xstart, var1.yend - var1.ystart, (int)var1.zstart, var1.red, var1.green, var1.blue);
         } else {
            DrawIsoLine(var1.xstart, var1.ystart, var1.zstart, var1.xend, var1.yend, var1.zend, var1.red, var1.green, var1.blue, 1.0F, 1);
         }
      }

   }

   public static void drawLines() {
      clear();
   }

   static class DrawableLine {
      public boolean bLine = false;
      String name;
      float red;
      float green;
      float blue;
      float xstart;
      float ystart;
      float zstart;
      float xend;
      float yend;
      float zend;

      public LineDrawer.DrawableLine init(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, String var10) {
         this.xstart = var1;
         this.ystart = var2;
         this.zstart = var3;
         this.xend = var4;
         this.yend = var5;
         this.zend = var6;
         this.red = var7;
         this.green = var8;
         this.blue = var9;
         this.name = var10;
         return this;
      }

      public LineDrawer.DrawableLine init(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, String var10, boolean var11) {
         this.xstart = var1;
         this.ystart = var2;
         this.zstart = var3;
         this.xend = var4;
         this.yend = var5;
         this.zend = var6;
         this.red = var7;
         this.green = var8;
         this.blue = var9;
         this.name = var10;
         this.bLine = var11;
         return this;
      }

      public boolean equals(Object var1) {
         if (var1 instanceof LineDrawer.DrawableLine) {
            return ((LineDrawer.DrawableLine)var1).name.equals(this.name);
         } else {
            return var1.equals(this);
         }
      }
   }
}
