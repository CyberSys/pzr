package zombie.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import zombie.characters.IsoPlayer;
import zombie.core.Styles.AdditiveStyle;
import zombie.core.Styles.AlphaOp;
import zombie.core.Styles.FloatList;
import zombie.core.Styles.GeometryData;
import zombie.core.Styles.LightingStyle;
import zombie.core.Styles.ShortList;
import zombie.core.Styles.Style;
import zombie.core.Styles.TransparentStyle;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;

public class SpriteRenderer {
   public static SpriteRenderer instance;
   static final int VERTEX_SIZE = 20;
   static final int TEXTURE0_COORD_OFFSET = 8;
   static final int COLOR_OFFSET = 16;
   public static SpriteRenderer.RingBuffer ringBuffer;
   public volatile TextureDraw[] sprite;
   public volatile TextureDraw[] drawsprite;
   public volatile Style[] style;
   public volatile Style[] drawstyle;
   public volatile int numSprites = 0;
   public volatile int drawNumSprites = 0;
   public boolean bDoAdditive = false;
   public static boolean GL_BLENDFUNC_ENABLED = true;
   public boolean DoingRender = false;
   public static final int NUM_RENDER_STATES = 3;
   public SpriteRenderer.RenderState state = null;
   public SpriteRenderer.RenderState[] states = new SpriteRenderer.RenderState[3];
   private static int discardedFrameCount = 0;

   public SpriteRenderer() {
      for(int var1 = 0; var1 < this.states.length; ++var1) {
         this.states[var1] = new SpriteRenderer.RenderState(var1);
      }

      this.state = this.states[0];
      this.style = this.state.style;
      this.sprite = this.state.sprite;
   }

   public void CheckSpriteSlots() {
      if (this.numSprites == this.sprite.length) {
         TextureDraw[] var1 = this.sprite;
         this.state.sprite = this.sprite = new TextureDraw[this.numSprites * 2];

         for(int var2 = this.numSprites; var2 < this.sprite.length; ++var2) {
            this.sprite[var2] = new TextureDraw();
         }

         System.arraycopy(var1, 0, this.sprite, 0, this.numSprites);
         var1 = null;
         Style[] var3 = this.style;
         this.state.style = this.style = new Style[this.numSprites * 2];
         System.arraycopy(var3, 0, this.style, 0, this.numSprites);
         var3 = null;
      }

   }

   public void create() {
      if (ringBuffer == null) {
         ringBuffer = new SpriteRenderer.RingBuffer();
         ringBuffer.create();
      }

   }

   public void clearSprites() {
      for(int var1 = 0; var1 < this.state.drawnModels.size(); ++var1) {
         ModelManager.ModelSlot var2 = (ModelManager.ModelSlot)this.state.drawnModels.get(var1);
         ModelManager.instance.DoneRendering(var2.ID);
      }

      this.state.drawnModels.clear();
   }

   public void PushIso() {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.PushIso(this.sprite[this.numSprites]);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void PopIso() {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.PopIso(this.sprite[this.numSprites]);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void renderflipped(Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      this.render(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.sprite[this.numSprites - 1].flipped = true;
   }

   public void renderflipped(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.render(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.sprite[this.numSprites - 1].flipped = true;
   }

   public void renderflipped(Texture var1, int var2, int var3, int var4, int var5, int var6) {
      this.render(var1, var2, var3, var4, var5, var6);
      this.sprite[this.numSprites - 1].flipped = true;
   }

   public void renderflipped(Texture var1, float var2, float var3, int var4, int var5, int var6) {
      this.render(var1, var2, var3, (float)var4, (float)var5, var6);
      this.sprite[this.numSprites - 1].flipped = true;
   }

   public void drawModel(ModelManager.ModelSlot var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      if (!this.state.drawnModels.contains(var1)) {
         assert var1.renderRefCount < this.states.length;

         this.state.drawnModels.add(var1);
         ++var1.renderRefCount;
      }

      TextureDraw.drawModel(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void drawSkyBox(Shader var1, int var2, int var3, int var4) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.drawSkyBox(this.sprite[this.numSprites], var1, var2, var3, var4);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void toBodyAtlas(DeadBodyAtlas.RenderJob var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.toBodyAtlas(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void drawGeneric(TextureDraw.GenericDrawer var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].type = TextureDraw.Type.DrawModel;
      this.sprite[this.numSprites].drawer = var1;
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void glDisable(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glDisable(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void doAdditive(boolean var1) {
      this.bDoAdditive = false;
   }

   public void glEnable(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glEnable(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glStencilMask(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glStencilMask(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glClear(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glClear(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glClearColor(int var1, int var2, int var3, int var4) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glClearColor(this.sprite[this.numSprites], var1, var2, var3, var4);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glStencilFunc(int var1, int var2, int var3) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glStencilFunc(this.sprite[this.numSprites], var1, var2, var3);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glStencilOp(int var1, int var2, int var3) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glStencilOp(this.sprite[this.numSprites], var1, var2, var3);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glColorMask(int var1, int var2, int var3, int var4) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glColorMask(this.sprite[this.numSprites], var1, var2, var3, var4);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glAlphaFunc(int var1, float var2) {
      if (GL_BLENDFUNC_ENABLED) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         TextureDraw.glAlphaFunc(this.sprite[this.numSprites], var1, var2);
         this.style[this.numSprites] = TransparentStyle.instance;
         ++this.numSprites;
      }
   }

   public void glBlendFunc(int var1, int var2) {
      if (GL_BLENDFUNC_ENABLED) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         TextureDraw.glBlendFunc(this.sprite[this.numSprites], var1, var2);
         this.style[this.numSprites] = TransparentStyle.instance;
         ++this.numSprites;
      }
   }

   public void glBlendFuncSeparate(int var1, int var2, int var3, int var4) {
      if (GL_BLENDFUNC_ENABLED) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         TextureDraw.glBlendFuncSeparate(this.sprite[this.numSprites], var1, var2, var3, var4);
         this.style[this.numSprites] = TransparentStyle.instance;
         ++this.numSprites;
      }
   }

   public void glBlendEquation(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glBlendEquation(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void render(Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20, float var21, float var22, float var23, float var24, float var25) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22, var23, var24, var25);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void render(Texture var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, float var18, float var19, float var20, float var21) {
      this.render(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var19, var20, var21, var18, var19, var20, var21, var18, var19, var20, var21, var18, var19, var20, var21);
   }

   public void render(Texture var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, float var18, float var19, float var20, float var21, float var22, float var23, float var24, float var25, float var26, float var27, float var28, float var29, float var30, float var31, float var32, float var33) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, (int)var2, (int)var4, (int)var6, (int)var8, (int)var10, (int)var12, (int)var14, (int)var16, var18, var19, var20, var21, var22, var23, var24, var25, var26, var27, var28, var29, var30, var31, var32, var33);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void renderdebug(Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17, float var18, float var19, float var20, float var21, float var22, float var23, float var24, float var25) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var22, var23, var24, var25);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void render(Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.style[this.numSprites] = LightingStyle.instance;
      ++this.numSprites;
   }

   public void renderline(Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9, int var10) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      if (var2 <= var4 && var3 <= var5) {
         TextureDraw.Create(this.sprite[this.numSprites], var1, var2 + var10, var3 - var10, var4 + var10, var5 - var10, var4 - var10, var5 + var10, var2 - var10, var3 + var10, var6, var7, var8, var9);
      } else if (var2 >= var4 && var3 >= var5) {
         TextureDraw.Create(this.sprite[this.numSprites], var1, var2 + var10, var3 - var10, var2 - var10, var3 + var10, var4 - var10, var5 + var10, var4 + var10, var5 - var10, var6, var7, var8, var9);
      } else if (var2 >= var4 && var3 <= var5) {
         TextureDraw.Create(this.sprite[this.numSprites], var1, var4 - var10, var5 - var10, var2 - var10, var3 - var10, var2 + var10, var3 + var10, var4 + var10, var5 + var10, var6, var7, var8, var9);
      } else if (var2 <= var4 && var3 >= var5) {
         TextureDraw.Create(this.sprite[this.numSprites], var1, var2 - var10, var3 - var10, var2 + var10, var3 + var10, var4 + var10, var5 + var10, var4 - var10, var5 - var10, var6, var7, var8, var9);
      }

      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void renderline(Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      this.renderline(var1, var2, var3, var4, var5, var6, var7, var8, var9, 1);
   }

   public void render(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, int var11, int var12, int var13) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, (int)var2, (int)var3, (int)var4, (int)var5, (int)var6, (int)var7, (int)var8, (int)var9, var10, var11, var12, var13);
      this.style[this.numSprites] = LightingStyle.instance;
      ++this.numSprites;
   }

   public void render(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      if (var9 != 0.0F) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         this.sprite[this.numSprites].reset();
         TextureDraw.Create(this.sprite[this.numSprites], var1, (int)var2, (int)var3, (int)var4, (int)var5, var6, var7, var8, var9);
         this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
         ++this.numSprites;
      }
   }

   public void render(Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6, var7, var8, var9);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void renderRect(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      if (var8 != 0.0F) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         this.sprite[this.numSprites].reset();
         TextureDraw.Create(this.sprite[this.numSprites], (Texture)null, var1, var2, var3, var4, var5, var6, var7, var8);
         this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
         ++this.numSprites;
      }
   }

   public void renderPoly(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, float var9, float var10, float var11, float var12) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], (Texture)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void renderPoly(Texture var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      if (var1 != null) {
         float var14 = var1.getXEnd();
         float var15 = var1.getXStart();
         float var16 = var1.getYEnd();
         float var17 = var1.getYStart();
         TextureDraw var18 = this.sprite[this.numSprites];
         var18.u[0] = var15;
         var18.u[1] = var14;
         var18.u[2] = var14;
         var18.u[3] = var15;
         var18.v[0] = var17;
         var18.v[1] = var17;
         var18.v[2] = var16;
         var18.v[3] = var16;
      }

      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void render(Texture var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17) {
      if (var9 != 0.0F) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         this.sprite[this.numSprites].reset();
         TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17);
         this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
         ++this.numSprites;
      }
   }

   public void render(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16, float var17) {
      if (var9 != 0.0F) {
         if (this.numSprites == this.sprite.length) {
            this.CheckSpriteSlots();
         }

         this.sprite[this.numSprites].reset();
         TextureDraw.Create(this.sprite[this.numSprites], var1, (int)var2, (int)var3, (int)var4, (int)var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17);
         this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
         ++this.numSprites;
      }
   }

   public void render(Texture var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, var2, var3, var4, var5, var6);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void render(Texture var1, float var2, float var3, float var4, float var5, int var6) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      this.sprite[this.numSprites].reset();
      TextureDraw.Create(this.sprite[this.numSprites], var1, (int)var2, (int)var3, (int)var4, (int)var5, var6);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   private void build() {
      for(int var1 = 0; var1 < this.drawNumSprites; ++var1) {
         TextureDraw var2 = this.drawsprite[var1];
         Style var3 = this.drawstyle[var1];
         TextureDraw var4 = null;
         if (var1 > 0) {
            var4 = this.drawsprite[var1 - 1];
         }

         ringBuffer.add(var2, var4, var3, 1.0F);
      }

   }

   public void preRender() {
      this.clearSprites();
      this.numSprites = 0;
      this.state.numSprites = 0;
      this.state.fbo = Core.getInstance().getOffscreenBuffer();

      for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
         IsoPlayer var2 = IsoPlayer.players[var1];
         if (var2 != null) {
            this.state.offscreenWidth[var1] = Core.getInstance().getOffscreenWidth(var1);
            this.state.offscreenHeight[var1] = Core.getInstance().getOffscreenHeight(var1);
            this.state.camOffX[var1] = IsoCamera.RightClickX[var1] + (float)IsoCamera.PLAYER_OFFSET_X;
            this.state.camOffY[var1] = IsoCamera.RightClickY[var1] + (float)IsoCamera.PLAYER_OFFSET_Y;
            float var3 = var2.x + IsoCamera.DeferedX[var1];
            float var4 = var2.y + IsoCamera.DeferedY[var1];
            float[] var10000 = this.state.camOffX;
            var10000[var1] += IsoUtils.XToScreen(var3 - (float)((int)var3), var4 - (float)((int)var4), 0.0F, 0);
            var10000 = this.state.camOffY;
            var10000[var1] += IsoUtils.YToScreen(var3 - (float)((int)var3), var4 - (float)((int)var4), 0.0F, 0);
            this.state.drawOffsetX[var1] = (int)var3;
            this.state.drawOffsetY[var1] = (int)var4;
         }
      }

   }

   public void postRender() {
      if (!GLContext.getCapabilities().OpenGL21 || !GLContext.getCapabilities().GL_ARB_fragment_shader) {
         PerformanceSettings.numberOf3D = 0;
         PerformanceSettings.numberOf3DAlt = 0;
         PerformanceSettings.support3D = false;
      }

      this.drawsprite = this.states[2].sprite;
      this.drawstyle = this.states[2].style;
      this.drawNumSprites = this.states[2].numSprites;
      if (this.drawNumSprites != 0) {
         this.DoingRender = true;
         ringBuffer.begin();
         this.build();
         this.DoingRender = false;
         ringBuffer.finish();
      }
   }

   public void glBuffer(int var1, int var2) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glBuffer(this.sprite[this.numSprites], var1, var2);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glDoStartFrame(int var1, int var2, int var3) {
      this.glDoStartFrame(var1, var2, var3, false);
   }

   public void glDoStartFrame(int var1, int var2, int var3, boolean var4) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glDoStartFrame(this.sprite[this.numSprites], var1, var2, var3, var4);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glDoStartFrameFx(int var1, int var2, int var3) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glDoStartFrameFx(this.sprite[this.numSprites], var1, var2, var3);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glIgnoreStyles(boolean var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glIgnoreStyles(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glDoEndFrame() {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glDoEndFrame(this.sprite[this.numSprites]);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glDoEndFrameFx(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glDoEndFrameFx(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glTexParameteri(int var1, int var2, int var3) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glTexParameteri(this.sprite[this.numSprites], var1, var2, var3);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void StartShader(int var1, int var2) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.StartShader(this.sprite[this.numSprites], var1);
      if (var1 != 0 && Shader.ShaderMap.containsKey(var1)) {
         ((Shader)Shader.ShaderMap.get(var1)).startMainThread(this.sprite[this.numSprites], var2);
      }

      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void ShaderUpdate(int var1, int var2, float var3) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.ShaderUpdate(this.sprite[this.numSprites], var1, var2, var3);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glLoadIdentity() {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glLoadIdentity(this.sprite[this.numSprites]);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glGenerateMipMaps(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glGenerateMipMaps(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = TransparentStyle.instance;
      ++this.numSprites;
   }

   public void glBind(int var1) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glBind(this.sprite[this.numSprites], var1);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void glViewport(int var1, int var2, int var3, int var4) {
      if (this.numSprites == this.sprite.length) {
         this.CheckSpriteSlots();
      }

      TextureDraw.glViewport(this.sprite[this.numSprites], var1, var2, var3, var4);
      this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
      ++this.numSprites;
   }

   public void pushFrameDown() {
      String var1 = null;
      synchronized(this.states) {
         this.state.numSprites = this.numSprites;
         this.states[0].bRendered = false;
         this.states[0].time = System.nanoTime();
         SpriteRenderer.RenderState var3 = this.states[0];
         this.states[0] = this.states[1];
         this.states[1] = var3;
         if (Core.bDebug && !this.states[0].bRendered && discardedFrameCount < 100) {
            ++discardedFrameCount;
            float var4 = (float)(System.nanoTime() - this.states[0].time) / 1000000.0F;
            var1 = "discarding frame (" + discardedFrameCount + ") age " + var4 + " numSprites " + this.states[0].numSprites;
         }

         this.state = this.states[0];
         this.numSprites = this.state.numSprites;
         this.style = this.state.style;
         this.sprite = this.state.sprite;
      }

      if (var1 != null) {
         DebugLog.log(var1);
      }

   }

   public class RenderState {
      public int index;
      public TextureDraw[] sprite = new TextureDraw[20000];
      public Style[] style = new Style[20000];
      public int numSprites = 0;
      public TextureFBO fbo = null;
      public boolean bRendered;
      public long time;
      public final ArrayList drawnModels = new ArrayList();
      public int playerIndex;
      public int[] offscreenWidth = new int[4];
      public int[] offscreenHeight = new int[4];
      public float[] camOffX = new float[4];
      public float[] camOffY = new float[4];
      public int[] drawOffsetX = new int[4];
      public int[] drawOffsetY = new int[4];

      public RenderState(int var2) {
         this.index = var2;

         for(int var3 = 0; var3 < this.sprite.length; ++var3) {
            this.sprite[var3] = new TextureDraw();
         }

      }
   }

   public static class RingBuffer {
      GLVertexBufferObject[] vbo;
      GLVertexBufferObject[] ibo;
      long bufferSize;
      long bufferSizeInVertices;
      long indexBufferSize;
      int numBuffers;
      int sequence = -1;
      int mark = -1;
      FloatBuffer currentVertices;
      ShortBuffer currentIndices;
      FloatBuffer[] vertices;
      ByteBuffer[] verticesBytes;
      ShortBuffer[] indices;
      ByteBuffer[] indicesBytes;
      Texture lastRenderedTexture0;
      Texture currentTexture0;
      Style lastRenderedStyle;
      Style currentStyle;
      SpriteRenderer.RingBuffer.StateRun[] stateRun;
      public boolean restoreVBOs;
      int vertexCursor;
      int indexCursor;
      int numRuns;
      SpriteRenderer.RingBuffer.StateRun currentRun;
      public static boolean IGNORE_STYLES = false;

      RingBuffer() {
      }

      void create() {
         GL11.glEnableClientState(32884);
         GL11.glEnableClientState(32886);
         GL11.glEnableClientState(32888);
         this.bufferSize = 65536L;
         this.numBuffers = 128;
         this.bufferSizeInVertices = this.bufferSize / 20L;
         this.indexBufferSize = this.bufferSizeInVertices * 3L;
         this.vertices = new FloatBuffer[this.numBuffers];
         this.verticesBytes = new ByteBuffer[this.numBuffers];
         this.indices = new ShortBuffer[this.numBuffers];
         this.indicesBytes = new ByteBuffer[this.numBuffers];
         this.stateRun = new SpriteRenderer.RingBuffer.StateRun[5000];

         int var1;
         for(var1 = 0; var1 < 5000; ++var1) {
            this.stateRun[var1] = new SpriteRenderer.RingBuffer.StateRun();
         }

         this.vbo = new GLVertexBufferObject[this.numBuffers];
         this.ibo = new GLVertexBufferObject[this.numBuffers];

         for(var1 = 0; var1 < this.numBuffers; ++var1) {
            this.vbo[var1] = new GLVertexBufferObject(this.bufferSize, GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW());
            this.vbo[var1].create();
            this.ibo[var1] = new GLVertexBufferObject(this.indexBufferSize, GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW());
            this.ibo[var1].create();
         }

      }

      void add(TextureDraw var1, TextureDraw var2, Style var3, float var4) {
         if (var3 != null) {
            if ((long)(this.vertexCursor + 4) > this.bufferSizeInVertices || (long)(this.indexCursor + 6) > this.indexBufferSize) {
               this.render();
               this.next();
            }

            Texture var5 = var1.tex;
            if ((var1 == null || var1.type != TextureDraw.Type.DrawModel && var1.type != TextureDraw.Type.ToBodyAtlas) && (var2 == null || var2.type != TextureDraw.Type.DrawModel && var2.type != TextureDraw.Type.ToBodyAtlas) && (var1.type != TextureDraw.Type.glDraw || var2 == null || var2.type == TextureDraw.Type.glDraw) && (var1.type == TextureDraw.Type.glDraw || var2 == null || var2.type != TextureDraw.Type.glDraw) && this.currentRun != null && (var3 == this.currentStyle || this.currentStyle != null && var3.getStyleID() == this.currentStyle.getStyleID()) && var5 == this.currentTexture0) {
               if (var1.type != TextureDraw.Type.glDraw) {
                  this.currentRun.ops.add(var1);
                  return;
               }
            } else {
               this.currentRun = this.stateRun[this.numRuns];
               this.currentRun.start = this.vertexCursor;
               this.currentRun.length = 0;
               this.currentRun.style = var3;
               this.currentRun.texture0 = var5;
               this.currentRun.indices = this.currentIndices;
               this.currentRun.startIndex = this.indexCursor;
               this.currentRun.endIndex = this.indexCursor;
               ++this.numRuns;
               if (this.numRuns == this.stateRun.length) {
                  this.growStateRuns();
               }

               this.currentStyle = var3;
               this.currentTexture0 = var5;
               if (var1.type != TextureDraw.Type.glDraw) {
                  this.currentRun.ops.add(var1);
                  return;
               }
            }

            FloatBuffer var6 = this.currentVertices;
            AlphaOp var7;
            if (var3 == null) {
               var7 = AlphaOp.KEEP;
            } else {
               var7 = var3.getAlphaOp();
            }

            var6.put((float)var1.x[0]);
            var6.put((float)var1.y[0]);
            if (var1.tex == null) {
               var6.put(0.0F);
               var6.put(0.0F);
            } else {
               if (var1.flipped) {
                  var6.put(var1.u[1]);
               } else {
                  var6.put(var1.u[0]);
               }

               var6.put(var1.v[0]);
            }

            int var8 = var1.getColor(0);
            var7.op(var8, 255, var6);
            var6.put((float)var1.x[1]);
            var6.put((float)var1.y[1]);
            if (var1.tex == null) {
               var6.put(0.0F);
               var6.put(0.0F);
            } else {
               if (var1.flipped) {
                  var6.put(var1.u[0]);
               } else {
                  var6.put(var1.u[1]);
               }

               var6.put(var1.v[1]);
            }

            var8 = var1.getColor(1);
            var7.op(var8, 255, var6);
            var6.put((float)var1.x[2]);
            var6.put((float)var1.y[2]);
            if (var1.tex == null) {
               var6.put(0.0F);
               var6.put(0.0F);
            } else {
               if (var1.flipped) {
                  var6.put(var1.u[3]);
               } else {
                  var6.put(var1.u[2]);
               }

               var6.put(var1.v[2]);
            }

            var8 = var1.getColor(2);
            var7.op(var8, 255, var6);
            var6.put((float)var1.x[3]);
            var6.put((float)var1.y[3]);
            if (var1.tex == null) {
               var6.put(0.0F);
               var6.put(0.0F);
            } else {
               if (var1.flipped) {
                  var6.put(var1.u[2]);
               } else {
                  var6.put(var1.u[3]);
               }

               var6.put(var1.v[3]);
            }

            var8 = var1.getColor(3);
            var7.op(var8, 255, var6);
            this.currentIndices.put((short)this.vertexCursor);
            this.currentIndices.put((short)(this.vertexCursor + 1));
            this.currentIndices.put((short)(this.vertexCursor + 2));
            this.currentIndices.put((short)this.vertexCursor);
            this.currentIndices.put((short)(this.vertexCursor + 2));
            this.currentIndices.put((short)(this.vertexCursor + 3));
            this.indexCursor += 6;
            this.vertexCursor += 4;
            SpriteRenderer.RingBuffer.StateRun var10000 = this.currentRun;
            var10000.endIndex += 6;
            var10000 = this.currentRun;
            var10000.length += 4;
         }
      }

      void add(Style var1) {
         GeometryData var2 = var1.build();
         if (var2 == null) {
            this.currentRun = this.stateRun[this.numRuns];
            this.currentRun.start = this.vertexCursor;
            this.currentRun.length = -1;
            this.currentRun.style = var1;
            this.currentRun.texture0 = null;
            this.currentRun.startIndex = 0;
            ++this.numRuns;
            if (this.numRuns == this.stateRun.length) {
               this.growStateRuns();
            }

            this.currentStyle = null;
            this.currentTexture0 = null;
         } else {
            FloatList var3 = var2.getVertexData();
            ShortList var4 = var2.getIndexData();
            int var5 = var3.size() / 5;
            if ((long)(this.vertexCursor + var5) > this.bufferSizeInVertices) {
               this.render();
               this.next();
            }

            this.currentRun = this.stateRun[this.numRuns];
            this.currentRun.start = this.vertexCursor;
            this.currentRun.length = var5;
            this.currentRun.style = var1;
            this.currentRun.texture0 = null;
            this.currentRun.startIndex = this.indexCursor;
            ++this.numRuns;
            if (this.numRuns == this.stateRun.length) {
               this.growStateRuns();
            }

            this.currentVertices.position(this.vertexCursor * 20 >> 2);
            this.currentVertices.put(var3.array(), 0, var3.size());
            this.currentIndices.position(this.indexCursor);
            short[] var6 = var4.array();
            int var7 = var4.size();

            for(int var8 = 0; var8 < var7; ++var8) {
               var6[var8] = (short)(var6[var8] + this.vertexCursor);
            }

            this.currentIndices.put(var6, 0, var7);
            this.vertexCursor += var5;
            this.indexCursor += var7;
            this.currentStyle = null;
            this.currentTexture0 = null;
         }
      }

      public void rebind() {
         this.vbo[this.sequence].render();
         this.ibo[this.sequence].render();
      }

      private void next() {
         ++this.sequence;
         if (this.sequence == this.numBuffers) {
            this.sequence = 0;
         }

         if (this.sequence == this.mark) {
            DebugLog.log("Buffer overrun");
         }

         this.vbo[this.sequence].render();
         ByteBuffer var1 = this.vbo[this.sequence].map();
         if (this.vertices[this.sequence] == null || this.verticesBytes[this.sequence] != var1) {
            this.verticesBytes[this.sequence] = var1;
            this.vertices[this.sequence] = var1.asFloatBuffer();
         }

         this.ibo[this.sequence].render();
         ByteBuffer var2 = this.ibo[this.sequence].map();
         if (this.indices[this.sequence] == null || this.indicesBytes[this.sequence] != var2) {
            this.indicesBytes[this.sequence] = var2;
            this.indices[this.sequence] = var2.asShortBuffer();
         }

         this.currentVertices = this.vertices[this.sequence];
         this.currentVertices.clear();
         this.currentIndices = this.indices[this.sequence];
         this.currentIndices.clear();
         this.vertexCursor = 0;
         this.indexCursor = 0;
         this.numRuns = 0;
         this.currentRun = null;
      }

      void begin() {
         this.currentStyle = null;
         this.currentTexture0 = null;
         this.next();
         this.mark = this.sequence;
      }

      void render() {
         this.vbo[this.sequence].unmap();
         this.ibo[this.sequence].unmap();
         this.restoreVBOs = true;

         for(int var1 = 0; var1 < this.numRuns; ++var1) {
            this.stateRun[var1].render();
         }

      }

      void finish() {
         this.render();
      }

      void growStateRuns() {
         SpriteRenderer.RingBuffer.StateRun[] var1 = new SpriteRenderer.RingBuffer.StateRun[(int)((float)this.stateRun.length * 1.5F)];
         System.arraycopy(this.stateRun, 0, var1, 0, this.stateRun.length);

         for(int var2 = this.numRuns; var2 < var1.length; ++var2) {
            var1[var2] = new SpriteRenderer.RingBuffer.StateRun();
         }

         this.stateRun = var1;
      }

      private class StateRun {
         Texture texture0;
         Style style;
         int start;
         int length;
         ShortBuffer indices;
         int startIndex;
         int endIndex;
         ArrayList ops;

         private StateRun() {
            this.ops = new ArrayList();
         }

         void render() {
            if (this.style != null) {
               int var1 = this.ops.size();
               if (var1 > 0) {
                  for(int var2 = 0; var2 < var1; ++var2) {
                     ((TextureDraw)this.ops.get(var2)).run();
                  }

                  this.ops.clear();
               } else {
                  if (this.style != RingBuffer.this.lastRenderedStyle) {
                     if (RingBuffer.this.lastRenderedStyle != null && (!SpriteRenderer.RingBuffer.IGNORE_STYLES || RingBuffer.this.lastRenderedStyle != AdditiveStyle.instance && RingBuffer.this.lastRenderedStyle != TransparentStyle.instance && RingBuffer.this.lastRenderedStyle != LightingStyle.instance)) {
                        RingBuffer.this.lastRenderedStyle.resetState();
                     }

                     if (this.style != null && (!SpriteRenderer.RingBuffer.IGNORE_STYLES || this.style != AdditiveStyle.instance && this.style != TransparentStyle.instance && this.style != LightingStyle.instance)) {
                        this.style.setupState();
                     }

                     RingBuffer.this.lastRenderedStyle = this.style;
                  }

                  if (this.texture0 != RingBuffer.this.lastRenderedTexture0) {
                     if (this.texture0 != null) {
                        if (RingBuffer.this.lastRenderedTexture0 == null) {
                           GL11.glEnable(3553);
                        }

                        this.texture0.bind();
                     } else if (RingBuffer.this.lastRenderedTexture0 != null) {
                        GL11.glDisable(3553);
                     }

                     RingBuffer.this.lastRenderedTexture0 = this.texture0;
                  }

                  if (this.length != 0) {
                     if (this.length == -1) {
                        RingBuffer.this.restoreVBOs = true;
                     } else {
                        if (RingBuffer.this.restoreVBOs) {
                           RingBuffer.this.restoreVBOs = false;
                           GL11.glVertexPointer(2, 5126, 20, 0L);
                           GL11.glTexCoordPointer(2, 5126, 20, 8L);
                           GL11.glColorPointer(4, 5121, 20, 16L);
                        }

                        if (this.style.getRenderSprite()) {
                           GL12.glDrawRangeElements(4, this.start, this.start + this.length, this.endIndex - this.startIndex, 5123, (long)(this.startIndex * 2));
                        } else {
                           this.style.render(this.start, this.startIndex);
                        }

                     }
                  }
               }
            }
         }

         // $FF: synthetic method
         StateRun(Object var2) {
            this();
         }
      }
   }
}
