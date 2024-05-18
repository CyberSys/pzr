package zombie.core.textures;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import zombie.IndieGL;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.ui.UIManager;

public class TextureDraw {
   public TextureDraw.Type type;
   public int a;
   public int b;
   public float f1;
   public float[] vars;
   public int c;
   public int d;
   public int[] col;
   public short[] x;
   public short[] y;
   public float[] u;
   public float[] v;
   public Texture tex;
   public boolean bSingleCol;
   public DeadBodyAtlas.RenderJob job;
   public boolean flipped;
   public TextureDraw.GenericDrawer drawer;

   public TextureDraw() {
      this.type = TextureDraw.Type.glDraw;
      this.a = 0;
      this.b = 0;
      this.f1 = 0.0F;
      this.c = 0;
      this.d = 0;
      this.col = new int[4];
      this.x = new short[4];
      this.y = new short[4];
      this.u = new float[4];
      this.v = new float[4];
      this.bSingleCol = false;
      this.flipped = false;
   }

   public static void Create(TextureDraw var0, Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20, float var21, float var22, float var23, float var24, float var25) {
      var0.bSingleCol = false;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      if (var11 > 1.0F) {
         var11 = 1.0F;
      }

      if (var12 > 1.0F) {
         var12 = 1.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      if (var14 > 1.0F) {
         var14 = 1.0F;
      }

      if (var15 > 1.0F) {
         var15 = 1.0F;
      }

      if (var16 > 1.0F) {
         var16 = 1.0F;
      }

      if (var17 > 1.0F) {
         var17 = 1.0F;
      }

      if (var18 > 1.0F) {
         var18 = 1.0F;
      }

      if (var19 > 1.0F) {
         var19 = 1.0F;
      }

      if (var20 > 1.0F) {
         var20 = 1.0F;
      }

      if (var21 > 1.0F) {
         var21 = 1.0F;
      }

      if (var22 > 1.0F) {
         var22 = 1.0F;
      }

      if (var23 > 1.0F) {
         var23 = 1.0F;
      }

      if (var24 > 1.0F) {
         var24 = 1.0F;
      }

      if (var25 > 1.0F) {
         var25 = 1.0F;
      }

      var0.tex = var1;
      var0.x[0] = (short)var2;
      var0.y[0] = (short)var3;
      var0.x[1] = (short)var4;
      var0.y[1] = (short)var5;
      var0.x[2] = (short)var6;
      var0.y[2] = (short)var7;
      var0.x[3] = (short)var8;
      var0.y[3] = (short)var9;
      if (var1 != null) {
         float var26 = var1.getXEnd();
         float var27 = var1.getXStart();
         float var28 = var1.getYEnd();
         float var29 = var1.getYStart();
         var0.u[0] = var27;
         var0.u[1] = var26;
         var0.u[2] = var26;
         var0.u[3] = var27;
         var0.v[0] = var29;
         var0.v[1] = var29;
         var0.v[2] = var28;
         var0.v[3] = var28;
      }

      var0.col[0] = (int)(var10 * 255.0F) << 0 | (int)(var11 * 255.0F) << 8 | (int)(var12 * 255.0F) << 16 | (int)(var13 * 255.0F) << 24;
      var0.col[1] = (int)(var14 * 255.0F) << 0 | (int)(var15 * 255.0F) << 8 | (int)(var16 * 255.0F) << 16 | (int)(var17 * 255.0F) << 24;
      var0.col[2] = (int)(var18 * 255.0F) << 0 | (int)(var19 * 255.0F) << 8 | (int)(var20 * 255.0F) << 16 | (int)(var21 * 255.0F) << 24;
      var0.col[3] = (int)(var22 * 255.0F) << 0 | (int)(var23 * 255.0F) << 8 | (int)(var24 * 255.0F) << 16 | (int)(var25 * 255.0F) << 24;
   }

   public static void Create(TextureDraw var0, Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13) {
      var0.bSingleCol = false;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      if (var11 > 1.0F) {
         var11 = 1.0F;
      }

      if (var12 > 1.0F) {
         var12 = 1.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      var0.tex = var1;
      var0.x[0] = (short)var2;
      var0.y[0] = (short)var3;
      var0.x[1] = (short)var4;
      var0.y[1] = (short)var5;
      var0.x[2] = (short)var6;
      var0.y[2] = (short)var7;
      var0.x[3] = (short)var8;
      var0.y[3] = (short)var9;
      var0.col[3] = var0.col[2] = var0.col[1] = var0.col[0] = (int)(var10 * 255.0F) << 0 | (int)(var11 * 255.0F) << 8 | (int)(var12 * 255.0F) << 16 | (int)(var13 * 255.0F) << 24;
   }

   public static void Create(TextureDraw var0, Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
      var0.bSingleCol = false;
      var0.tex = var1;
      var0.x[0] = (short)var2;
      var0.y[0] = (short)var3;
      var0.x[1] = (short)var4;
      var0.y[1] = (short)var5;
      var0.x[2] = (short)var6;
      var0.y[2] = (short)var7;
      var0.x[3] = (short)var8;
      var0.y[3] = (short)var9;
      var0.col[0] = var10;
      var0.col[1] = var11;
      var0.col[2] = var12;
      var0.col[3] = var13;
   }

   public static void glStencilFunc(TextureDraw var0, int var1, int var2, int var3) {
      var0.type = TextureDraw.Type.glStencilFunc;
      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
   }

   public static void glBuffer(TextureDraw var0, int var1, int var2) {
      var0.type = TextureDraw.Type.glBuffer;
      var0.a = var1;
      var0.b = var2;
   }

   public static void glStencilOp(TextureDraw var0, int var1, int var2, int var3) {
      var0.type = TextureDraw.Type.glStencilOp;
      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
   }

   public static void glDisable(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glDisable;
      var0.a = var1;
   }

   public static void glClear(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glClear;
      var0.a = var1;
   }

   public static void glClearColor(TextureDraw var0, int var1, int var2, int var3, int var4) {
      var0.type = TextureDraw.Type.glClearColor;
      var0.col[0] = var1;
      var0.col[1] = var2;
      var0.col[2] = var3;
      var0.col[3] = var4;
   }

   public static void glEnable(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glEnable;
      var0.a = var1;
   }

   public static void glAlphaFunc(TextureDraw var0, int var1, float var2) {
      var0.type = TextureDraw.Type.glAlphaFunc;
      var0.a = var1;
      var0.f1 = var2;
   }

   public static void glColorMask(TextureDraw var0, int var1, int var2, int var3, int var4) {
      var0.type = TextureDraw.Type.glColorMask;
      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
      var0.x[0] = (short)var4;
   }

   public static void glStencilMask(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glStencilMask;
      var0.a = var1;
   }

   public static void glBlendFunc(TextureDraw var0, int var1, int var2) {
      var0.type = TextureDraw.Type.glBlendFunc;
      var0.a = var1;
      var0.b = var2;
   }

   public static void glBlendFuncSeparate(TextureDraw var0, int var1, int var2, int var3, int var4) {
      var0.type = TextureDraw.Type.glBlendFuncSeparate;
      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
      var0.d = var4;
   }

   public static void glBlendEquation(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glBlendEquation;
      var0.a = var1;
   }

   public static void glDoEndFrame(TextureDraw var0) {
      var0.type = TextureDraw.Type.glDoEndFrame;
   }

   public static void glDoEndFrameFx(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glDoEndFrameFx;
      var0.c = var1;
   }

   public static void glIgnoreStyles(TextureDraw var0, boolean var1) {
      var0.type = TextureDraw.Type.glIgnoreStyles;
      var0.a = var1 ? 1 : 0;
   }

   public static void glDoStartFrame(TextureDraw var0, int var1, int var2, int var3) {
      glDoStartFrame(var0, var1, var2, var3, false);
   }

   public static void glDoStartFrame(TextureDraw var0, int var1, int var2, int var3, boolean var4) {
      if (var4) {
         var0.type = TextureDraw.Type.glDoStartFrameText;
      } else {
         var0.type = TextureDraw.Type.glDoStartFrame;
      }

      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
   }

   public static void glDoStartFrameFx(TextureDraw var0, int var1, int var2, int var3) {
      var0.type = TextureDraw.Type.glDoStartFrameFx;
      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
   }

   public static void glTexParameteri(TextureDraw var0, int var1, int var2, int var3) {
      var0.type = TextureDraw.Type.glTexParameteri;
      var0.a = var1;
      var0.b = var2;
      var0.c = var3;
   }

   public static void drawModel(TextureDraw var0, ModelManager.ModelSlot var1) {
      var0.type = TextureDraw.Type.DrawModel;
      var0.a = var1.ID;
      var0.drawer = null;
   }

   public static void drawSkyBox(TextureDraw var0, Shader var1, int var2, int var3, int var4) {
      var0.type = TextureDraw.Type.DrawSkyBox;
      var0.a = var1.ShaderID;
      var0.b = var2;
      var0.c = var3;
      var0.d = var4;
      var0.drawer = null;
   }

   public static void toBodyAtlas(TextureDraw var0, DeadBodyAtlas.RenderJob var1) {
      var0.type = TextureDraw.Type.ToBodyAtlas;
      var0.job = var1;
   }

   public static void StartShader(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.StartShader;
      var0.a = var1;
   }

   public static void ShaderUpdate(TextureDraw var0, int var1, int var2, float var3) {
      var0.type = TextureDraw.Type.ShaderUpdate;
      var0.a = var1;
      var0.b = var2;
      var0.f1 = var3;
   }

   public void run() {
      switch(this.type) {
      case StartShader:
         ARBShaderObjects.glUseProgramObjectARB(this.a);
         if (Shader.ShaderMap.containsKey(this.a)) {
            ((Shader)Shader.ShaderMap.get(this.a)).updateParams(this);
         }
         break;
      case ShaderUpdate:
         ARBShaderObjects.glUniform1fARB(this.b, this.f1);
         break;
      case DrawModel:
         if (this.drawer != null) {
            this.drawer.render();
         } else {
            try {
               ModelManager.instance.DoRender(this.a);
            } catch (Exception var4) {
               var4.printStackTrace();
            }
         }
         break;
      case DrawSkyBox:
         try {
            ModelManager.instance.RenderSkyBox(this, this.a, this.b, this.c, this.d);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
         break;
      case ToBodyAtlas:
         try {
            DeadBodyAtlas.instance.toBodyAtlas(this.job);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
         break;
      case glClear:
         IndieGL.glClearA(this.a);
         break;
      case glClearColor:
         GL11.glClearColor((float)this.col[0] / 255.0F, (float)this.col[1] / 255.0F, (float)this.col[2] / 255.0F, (float)this.col[3] / 255.0F);
         break;
      case glBlendFunc:
         IndieGL.glBlendFuncA(this.a, this.b);
         break;
      case glBlendFuncSeparate:
         GL14.glBlendFuncSeparate(this.a, this.b, this.c, this.d);
         break;
      case glColorMask:
         IndieGL.glColorMaskA(this.a == 1, this.b == 1, this.c == 1, this.x[0] == 1);
         break;
      case glTexParameteri:
         IndieGL.glTexParameteriActual(this.a, this.b, this.c);
         break;
      case glStencilMask:
         IndieGL.glStencilMaskA(this.a);
         break;
      case glDoEndFrame:
         Core.getInstance().DoEndFrameStuff(this.a, this.b);
         break;
      case glDoEndFrameFx:
         Core.getInstance().DoEndFrameStuffFx(this.a, this.b, this.c);
         break;
      case glDoStartFrame:
         Core.getInstance().DoStartFrameStuff(this.a, this.b, this.c);
         break;
      case glDoStartFrameText:
         Core.getInstance().DoStartFrameStuff(this.a, this.b, this.c, true);
         break;
      case glDoStartFrameFx:
         Core.getInstance().DoStartFrameStuffFx(this.a, this.b, this.c);
         break;
      case PopIso:
         Core.getInstance().DoPopIsoStuff();
         break;
      case PushIso:
         Core.getInstance().DoPushIsoStuff();
         break;
      case glStencilFunc:
         IndieGL.glStencilFuncA(this.a, this.b, this.c);
         break;
      case glBuffer:
         if (Core.getInstance().supportsFBO()) {
            if (this.a == 1) {
               SpriteRenderer.instance.states[2].fbo.startDrawing(false, false);
            } else if (this.a == 2) {
               UIManager.UIFBO.startDrawing(true, true);
            } else if (this.a == 3) {
               UIManager.UIFBO.endDrawing();
            } else if (this.a == 4) {
               WeatherFxMask.getFboMask().startDrawing(true, true);
            } else if (this.a == 5) {
               WeatherFxMask.getFboMask().endDrawing();
            } else if (this.a == 6) {
               WeatherFxMask.getFboParticles().startDrawing(true, true);
            } else if (this.a == 7) {
               WeatherFxMask.getFboParticles().endDrawing();
            } else {
               SpriteRenderer.instance.states[2].fbo.endDrawing();
            }
         }
         break;
      case glStencilOp:
         IndieGL.glStencilOpA(this.a, this.b, this.c);
         break;
      case glLoadIdentity:
         GL11.glLoadIdentity();
         break;
      case glBind:
         GL11.glBindTexture(3553, this.a);
         Texture.lastlastTextureID = Texture.lastTextureID;
         Texture.lastTextureID = this.a;
         break;
      case glViewport:
         GL11.glViewport(0, 0, this.a, this.b);
         break;
      case glGenerateMipMaps:
         Core.getInstance().OffscreenBuffer.updateMipMaps();
         break;
      case glAlphaFunc:
         IndieGL.glAlphaFuncA(this.a, this.f1);
         break;
      case glEnable:
         IndieGL.glEnableA(this.a);
         break;
      case glDisable:
         IndieGL.glDisableA(this.a);
         break;
      case glBlendEquation:
         GL14.glBlendEquation(this.a);
         break;
      case glIgnoreStyles:
         SpriteRenderer.RingBuffer.IGNORE_STYLES = this.a == 1;
      }

   }

   public static void PopIso(TextureDraw var0) {
      var0.type = TextureDraw.Type.PopIso;
   }

   public static void PushIso(TextureDraw var0) {
      var0.type = TextureDraw.Type.PushIso;
   }

   public static TextureDraw Create(TextureDraw var0, Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      var0.bSingleCol = true;
      var0.tex = var1;
      var0.x[0] = var0.x[3] = (short)var2;
      var0.y[0] = var0.y[1] = (short)var3;
      var0.x[1] = var0.x[2] = (short)(var2 + var4);
      var0.y[2] = var0.y[3] = (short)(var3 + var5);
      var0.col[0] = var0.col[1] = var0.col[2] = var0.col[3] = (int)(var6 * 255.0F) << 0 | (int)(var7 * 255.0F) << 8 | (int)(var8 * 255.0F) << 16 | (int)(var9 * 255.0F) << 24;
      if (var1 != null) {
         float var10 = var1.getXEnd();
         float var11 = var1.getXStart();
         float var12 = var1.getYEnd();
         float var13 = var1.getYStart();
         var0.u[0] = var11;
         var0.u[1] = var10;
         var0.u[2] = var10;
         var0.u[3] = var11;
         var0.v[0] = var13;
         var0.v[1] = var13;
         var0.v[2] = var12;
         var0.v[3] = var12;
      }

      return var0;
   }

   public static TextureDraw Create(TextureDraw var0, Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17) {
      var0.bSingleCol = true;
      var0.tex = var1;
      var0.x[0] = var0.x[3] = (short)var2;
      var0.y[0] = var0.y[1] = (short)var3;
      var0.x[1] = var0.x[2] = (short)(var2 + var4);
      var0.y[2] = var0.y[3] = (short)(var3 + var5);
      if (var1 != null) {
         var0.flipped = var1.flip;
      }

      var0.col[0] = var0.col[1] = var0.col[2] = var0.col[3] = (int)(var6 * 255.0F) << 0 | (int)(var7 * 255.0F) << 8 | (int)(var8 * 255.0F) << 16 | (int)(var9 * 255.0F) << 24;
      if (var1 != null) {
         var0.u[0] = var10;
         var0.u[1] = var12;
         var0.u[2] = var14;
         var0.u[3] = var16;
         var0.v[0] = var11;
         var0.v[1] = var13;
         var0.v[2] = var15;
         var0.v[3] = var17;
      }

      return var0;
   }

   public static TextureDraw Create(TextureDraw var0, Texture var1, int var2, int var3, int var4, int var5, int var6) {
      var0.bSingleCol = true;
      var0.tex = var1;
      var0.x[0] = var0.x[3] = (short)var2;
      var0.y[0] = var0.y[1] = (short)var3;
      var0.x[1] = var0.x[2] = (short)(var2 + var4);
      var0.y[2] = var0.y[3] = (short)(var3 + var5);
      if (var1 != null) {
         var0.flipped = var1.flip;
      }

      var0.col[0] = var6;
      var0.col[1] = var6;
      var0.col[2] = var6;
      var0.col[3] = var6;
      if (var1 != null) {
         var0.u[0] = var1.getXStart();
         var0.u[1] = var1.getXEnd();
         var0.u[2] = var1.getXEnd();
         var0.u[3] = var1.getXStart();
         var0.v[0] = var1.getYStart();
         var0.v[1] = var1.getYStart();
         var0.v[2] = var1.getYEnd();
         var0.v[3] = var1.getYEnd();
      }

      return var0;
   }

   public int getColor(int var1) {
      if (this.bSingleCol) {
         return this.col[0];
      } else if (var1 == 0) {
         return this.col[0];
      } else if (var1 == 1) {
         return this.col[1];
      } else if (var1 == 2) {
         return this.col[2];
      } else {
         return var1 == 3 ? this.col[3] : this.col[0];
      }
   }

   public void reset() {
      this.type = TextureDraw.Type.glDraw;
      this.flipped = false;
      this.tex = null;
      this.col[0] = -1;
      this.col[1] = -1;
      this.col[2] = -1;
      this.col[3] = -1;
      this.bSingleCol = true;
      this.x[0] = this.x[1] = this.x[2] = this.x[3] = this.y[0] = this.y[1] = this.y[2] = this.y[3] = -1;
      this.drawer = null;
   }

   public static void glLoadIdentity(TextureDraw var0) {
      var0.type = TextureDraw.Type.glLoadIdentity;
   }

   public static void glGenerateMipMaps(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glGenerateMipMaps;
      var0.a = var1;
   }

   public static void glBind(TextureDraw var0, int var1) {
      var0.type = TextureDraw.Type.glBind;
      var0.a = var1;
   }

   public static void glViewport(TextureDraw var0, int var1, int var2, int var3, int var4) {
      var0.type = TextureDraw.Type.glViewport;
      var0.a = var3;
      var0.b = var4;
   }

   public static class GenericDrawer {
      public void render() {
      }
   }

   public static enum Type {
      glDraw,
      glBuffer,
      glStencilFunc,
      glAlphaFunc,
      glStencilOp,
      glEnable,
      glDisable,
      glColorMask,
      glStencilMask,
      glClear,
      glBlendFunc,
      glDoStartFrame,
      glDoStartFrameText,
      glDoEndFrame,
      glTexParameteri,
      StartShader,
      glLoadIdentity,
      glGenerateMipMaps,
      glBind,
      glViewport,
      DrawModel,
      DrawSkyBox,
      PushIso,
      PopIso,
      ToBodyAtlas,
      ShaderUpdate,
      glBlendEquation,
      glDoStartFrameFx,
      glDoEndFrameFx,
      glIgnoreStyles,
      glClearColor,
      glBlendFuncSeparate;
   }
}
