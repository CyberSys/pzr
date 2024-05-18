package zombie;

import org.lwjgl.opengl.GL11;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public class IndieGL {
   public static int nCount = 0;
   static int glBlendFuncA = -12345;
   static int glBlendFuncB = -12345;
   static int glStencilFuncA = -12345;
   static int glStencilFuncB = -12345;
   static int glStencilFuncC = -12345;
   static int glStencilOpA = -12345;
   static int glStencilOpB = -12345;
   static int glStencilOpC = -12345;
   private static boolean glColorMaskB0 = true;
   private static boolean glColorMaskB1 = true;
   private static boolean glColorMaskB2 = true;
   private static boolean glColorMaskB3 = true;
   static int glAlphaFuncAA = -1;
   static float glAlphaFuncBB = -1.0F;
   private static boolean ALPHA_TEST = false;
   private static boolean STENCIL_TEST = false;

   public static void glBlendFunc(int var0, int var1) {
      if (SpriteRenderer.instance != null) {
         SpriteRenderer.instance.glBlendFunc(var0, var1);
      }

   }

   public static void StartShader(int var0, int var1) {
      SpriteRenderer.instance.StartShader(var0, var1);
   }

   public static void glBlendFuncA(int var0, int var1) {
      glBlendFuncA = var0;
      glBlendFuncB = var1;
      GL11.glBlendFunc(var0, var1);
   }

   public static void glEnable(int var0) {
      SpriteRenderer.instance.glEnable(var0);
   }

   public static void glDoStartFrame(int var0, int var1, int var2) {
      glDoStartFrame(var0, var1, var2, false);
   }

   public static void glDoStartFrame(int var0, int var1, int var2, boolean var3) {
      SpriteRenderer.instance.glDoStartFrame(var0, var1, var2, var3);
   }

   public static void glDoEndFrame() {
      SpriteRenderer.instance.glDoEndFrame();
   }

   public static void glColorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      if (var0 != glColorMaskB0 || var1 != glColorMaskB1 || var2 != glColorMaskB2 || var3 != glColorMaskB3) {
         glColorMaskB0 = var0;
         glColorMaskB1 = var1;
         glColorMaskB2 = var2;
         glColorMaskB3 = var3;
         SpriteRenderer.instance.glColorMask(var0 ? 1 : 0, var1 ? 1 : 0, var2 ? 1 : 0, var3 ? 1 : 0);
      }
   }

   public static void glColorMaskA(boolean var0, boolean var1, boolean var2, boolean var3) {
      GL11.glColorMask(var0, var0, var3, var3);
   }

   public static void glEnableA(int var0) {
      GL11.glEnable(var0);
   }

   public static void glAlphaFunc(int var0, float var1) {
      if (var0 != glAlphaFuncAA || var1 != glAlphaFuncBB) {
         SpriteRenderer.instance.glAlphaFunc(var0, var1);
         glAlphaFuncAA = var0;
         glAlphaFuncBB = var1;
      }
   }

   public static void glAlphaFuncA(int var0, float var1) {
      GL11.glAlphaFunc(var0, var1);
   }

   public static void glStencilFunc(int var0, int var1, int var2) {
      if (var0 != glStencilFuncA || var1 != glStencilFuncB || var2 != glStencilFuncC) {
         SpriteRenderer.instance.glStencilFunc(var0, var1, var2);
         glStencilFuncA = var0;
         glStencilFuncB = var1;
         glStencilFuncC = var2;
      }
   }

   public static void glStencilFuncA(int var0, int var1, int var2) {
      GL11.glStencilFunc(var0, var1, var2);
   }

   public static void glStencilOp(int var0, int var1, int var2) {
      if (var0 != glStencilOpA || var1 != glStencilOpB || var2 != glStencilOpC) {
         SpriteRenderer.instance.glStencilOp(var0, var1, var2);
         glStencilOpA = var0;
         glStencilOpB = var1;
         glStencilOpC = var2;
      }
   }

   public static void glStencilOpA(int var0, int var1, int var2) {
      GL11.glStencilOp(var0, var1, var2);
   }

   public static void glTexParameteri(int var0, int var1, int var2) {
      SpriteRenderer.instance.glTexParameteri(var0, var1, var2);
   }

   public static void glTexParameteriActual(int var0, int var1, int var2) {
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static void glStencilMask(int var0) {
      SpriteRenderer.instance.glStencilMask(var0);
   }

   public static void glStencilMaskA(int var0) {
      GL11.glStencilMask(var0);
   }

   public static void glDisable(int var0) {
      SpriteRenderer.instance.glDisable(var0);
   }

   public static void glClear(int var0) {
      SpriteRenderer.instance.glClear(var0);
   }

   public static void glClearA(int var0) {
      GL11.glClear(var0);
   }

   public static void glDisableA(int var0) {
      GL11.glDisable(var0);
   }

   public static void End() {
   }

   public static void Begin() {
   }

   public static void BeginLine() {
   }

   public static void glLoadIdentity() {
      SpriteRenderer.instance.glLoadIdentity();
   }

   public static void glGenerateMipMaps(int var0) {
      SpriteRenderer.instance.glGenerateMipMaps(var0);
   }

   public static void glBind(Texture var0) {
      SpriteRenderer.instance.glBind(var0.getID());
   }

   public static void enableAlphaTest() {
      if (!ALPHA_TEST) {
         ALPHA_TEST = true;
         SpriteRenderer.instance.glEnable(3008);
      }
   }

   public static void disableAlphaTest() {
      if (ALPHA_TEST) {
         ALPHA_TEST = false;
         SpriteRenderer.instance.glDisable(3008);
      }
   }

   public static void enableStencilTest() {
      if (!STENCIL_TEST) {
         STENCIL_TEST = true;
         SpriteRenderer.instance.glEnable(2960);
      }
   }

   public static void disableStencilTest() {
      if (STENCIL_TEST) {
         STENCIL_TEST = false;
         SpriteRenderer.instance.glDisable(2960);
      }
   }
}
