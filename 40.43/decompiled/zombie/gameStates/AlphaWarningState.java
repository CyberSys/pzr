package zombie.gameStates;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.UIManager;

public class AlphaWarningState extends GameState {
   public float alpha = 0.0F;
   public float alpha2 = 0.0F;
   public float alphaStep = 0.02F;
   public boolean bFixLogo = false;
   public boolean bFixLogo2 = false;
   public int delay = 20;
   public int leavedelay = 0;
   public float logoUseAlpha = 0.0F;
   public float logoUseAlpha2 = 0.0F;
   public float messageTime = 850.0F;
   public float messageTimeMax = 850.0F;
   public float logoDelay = 20.0F;
   public int stage = 0;
   public float targetAlpha = 0.0F;
   private boolean bNoRender = false;

   public void enter() {
      UIManager.bSuspend = true;
      this.alpha = 0.0F;
      this.targetAlpha = 1.0F;
   }

   public void exit() {
      UIManager.bSuspend = false;
   }

   public void render() {
      if (this.bNoRender) {
         Core.getInstance().StartFrame();
         SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F);
         Core.getInstance().EndFrame();
      } else {
         Core.getInstance().StartFrame();
         Core.getInstance().EndFrame();
         boolean var1 = UIManager.useUIFBO;
         UIManager.useUIFBO = false;
         Core.getInstance().StartFrameUI();
         SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F);
         if (this.logoDelay <= 0.0F) {
            ++this.stage;
            this.messageTime = this.messageTimeMax;
            this.targetAlpha = 1.0F;
            this.bFixLogo2 = false;
         }

         if (!this.bFixLogo) {
            this.logoUseAlpha = this.alpha;
         }

         if (this.alpha == 1.0F) {
            this.bFixLogo = true;
         }

         Texture var2 = Texture.getSharedTexture("media/ui/TheIndieStoneLogo_Lineart_White.png");
         int var3 = Core.getInstance().getOffscreenWidth(0) / 2;
         var3 -= var2.getWidth() / 2;
         int var4 = Core.getInstance().getOffscreenHeight(0) / 2;
         var4 -= var2.getHeight() / 2;
         SpriteRenderer.instance.render(var2, var3, var4, var2.getWidth(), var2.getHeight(), 1.0F, 1.0F, 1.0F, this.alpha);
         Core.getInstance().EndFrameUI();
         UIManager.useUIFBO = var1;
      }
   }

   public GameStateMachine.StateAction update() {
      if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
         this.targetAlpha = 0.0F;
         this.stage = 2;
      }

      if (this.stage < 2 && this.alpha == 1.0F && this.targetAlpha == 1.0F) {
         --this.logoDelay;
      }

      if (this.stage >= 1) {
         this.targetAlpha = 0.0F;
         this.bFixLogo = false;
         this.bFixLogo2 = false;
         if (this.leavedelay > 0) {
            --this.leavedelay;
         } else if (this.alpha == 0.0F) {
            Core.getInstance().StartFrame();
            SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 1.0F, 1.0F, 1.0F, 1.0F);
            this.bNoRender = true;
            return GameStateMachine.StateAction.Continue;
         }
      }

      if (this.alpha < this.targetAlpha) {
         this.alpha += this.alphaStep * GameTime.getInstance().getMultiplier();
         if (this.alpha > this.targetAlpha) {
            this.alpha = this.targetAlpha;
         }
      } else if (this.alpha > this.targetAlpha) {
         this.alpha -= this.alphaStep * GameTime.getInstance().getMultiplier();
         if (this.alpha < this.targetAlpha) {
            this.alpha = this.targetAlpha;
         }
      }

      if (this.alpha2 < this.targetAlpha) {
         this.alpha2 += this.alphaStep;
         if (this.alpha2 > this.targetAlpha) {
            this.alpha2 = this.targetAlpha;
         }
      } else if (this.alpha2 > this.targetAlpha) {
         this.alpha2 -= this.alphaStep * 2.0F;
         if (this.alpha2 < this.targetAlpha) {
            this.alpha2 = this.targetAlpha;
         }
      }

      return GameStateMachine.StateAction.Remain;
   }
}
