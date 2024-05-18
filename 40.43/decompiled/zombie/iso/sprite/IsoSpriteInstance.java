package zombie.iso.sprite;

import java.util.Stack;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;

public class IsoSpriteInstance {
   public static Stack pool = new Stack();
   public IsoSprite parentSprite;
   public float tintb = 1.0F;
   public float tintg = 1.0F;
   public float tintr = 1.0F;
   public float Frame = 0.0F;
   public float alpha = 1.0F;
   public float targetAlpha = 1.0F;
   public boolean bCopyTargetAlpha = true;
   public boolean Flip;
   public float offZ = 0.0F;
   public float offX = 0.0F;
   public float offY = 0.0F;
   public float AnimFrameIncrease = 1.0F;
   static float multiplier = 1.0F;
   public boolean Looped = true;
   public boolean Finished = false;
   public boolean NextFrame;
   public float scaleX = 1.0F;
   public float scaleY = 1.0F;

   public static IsoSpriteInstance get(IsoSprite var0) {
      IsoSpriteInstance var1;
      if (pool.isEmpty()) {
         var1 = new IsoSpriteInstance(var0);
         return var1;
      } else {
         var1 = (IsoSpriteInstance)pool.pop();
         var1.parentSprite = var0;
         var1.reset();
         return var1;
      }
   }

   private void reset() {
      this.alpha = 1.0F;
      this.tintb = 1.0F;
      this.tintg = 1.0F;
      this.tintr = 1.0F;
      this.Frame = 0.0F;
      this.alpha = 1.0F;
      this.targetAlpha = 1.0F;
      this.bCopyTargetAlpha = true;
      this.Flip = false;
      this.offZ = 0.0F;
      this.offX = 0.0F;
      this.offY = 0.0F;
      this.AnimFrameIncrease = 1.0F;
      multiplier = 1.0F;
      this.Looped = true;
      this.Finished = false;
      this.NextFrame = false;
   }

   public IsoSpriteInstance() {
   }

   public void setFrameSpeedPerFrame(float var1) {
      this.AnimFrameIncrease = var1 * multiplier;
   }

   public int getID() {
      return this.parentSprite.ID;
   }

   public String getName() {
      return this.parentSprite.getName();
   }

   public IsoSprite getParentSprite() {
      return this.parentSprite;
   }

   public IsoSpriteInstance(IsoSprite var1) {
      this.parentSprite = var1;
   }

   public void render(IsoObject var1, float var2, float var3, float var4, IsoDirections var5, float var6, float var7, ColorInfo var8) {
      this.parentSprite.render(this, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void SetAlpha(float var1) {
      this.alpha = var1;
      this.bCopyTargetAlpha = false;
   }

   public void SetTargetAlpha(float var1) {
      this.targetAlpha = var1;
      this.bCopyTargetAlpha = false;
   }

   public void update() {
   }

   protected void renderprep(IsoObject var1) {
      if (var1 != null && this.bCopyTargetAlpha) {
         this.targetAlpha = var1.targetAlpha[IsoCamera.frameState.playerIndex];
         this.alpha = var1.alpha[IsoCamera.frameState.playerIndex];
      } else {
         if (this.alpha < this.targetAlpha) {
            this.alpha += IsoSprite.alphaStep;
            if (this.alpha > this.targetAlpha) {
               this.alpha = this.targetAlpha;
            }
         } else if (this.alpha > this.targetAlpha) {
            this.alpha -= IsoSprite.alphaStep;
            if (this.alpha < this.targetAlpha) {
               this.alpha = this.targetAlpha;
            }
         }

         if (this.alpha < 0.0F) {
            this.alpha = 0.0F;
         }

         if (this.alpha > 1.0F) {
            this.alpha = 1.0F;
         }
      }

   }

   public float getFrame() {
      return this.Frame;
   }

   public int getFrameCount() {
      return this.parentSprite != null && this.parentSprite.CurrentAnim != null ? this.parentSprite.CurrentAnim.Frames.size() : 0;
   }

   public boolean isFinished() {
      return this.Finished;
   }

   public void Dispose() {
   }

   public void RenderGhostTileColor(int var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      if (this.parentSprite != null) {
         IsoSpriteInstance var8 = get(this.parentSprite);
         var8.Frame = this.Frame;
         var8.tintr = var4;
         var8.tintg = var5;
         var8.tintb = var6;
         var8.alpha = var8.targetAlpha = var7;
         IsoGridSquare.getDefColorInfo().r = IsoGridSquare.getDefColorInfo().g = IsoGridSquare.getDefColorInfo().b = IsoGridSquare.getDefColorInfo().a = 1.0F;
         this.parentSprite.render(var8, (IsoObject)null, (float)var1, (float)var2, (float)var3, IsoDirections.N, 0.0F, -144.0F, IsoGridSquare.getDefColorInfo());
      }
   }

   public void setScale(float var1, float var2) {
      this.scaleX = var1;
      this.scaleY = var2;
   }

   public float getScaleX() {
      return this.scaleX;
   }

   public float getScaleY() {
      return this.scaleY;
   }

   public void scaleAspect(float var1, float var2, float var3, float var4) {
      if (var1 > 0.0F && var2 > 0.0F && var3 > 0.0F && var4 > 0.0F) {
         float var5 = var4 * var1 / var2;
         float var6 = var3 * var2 / var1;
         boolean var7 = var5 <= var3;
         if (var7) {
            var3 = var5;
         } else {
            var4 = var6;
         }

         this.scaleX = var3 / var1;
         this.scaleY = var4 / var2;
      }

   }

   public static void add(IsoSpriteInstance var0) {
      var0.reset();
      pool.push(var0);
   }
}
