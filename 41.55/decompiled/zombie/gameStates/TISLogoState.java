package zombie.gameStates;

import java.util.function.Consumer;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;

public final class TISLogoState extends GameState {
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
         SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
         Core.getInstance().EndFrame();
      } else {
         Core.getInstance().StartFrame();
         Core.getInstance().EndFrame();
         boolean var1 = UIManager.useUIFBO;
         UIManager.useUIFBO = false;
         Core.getInstance().StartFrameUI();
         SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
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
         SpriteRenderer.instance.renderi(var2, var3, var4, var2.getWidth(), var2.getHeight(), 1.0F, 1.0F, 1.0F, this.alpha, (Consumer)null);
         this.renderAttribution();
         Core.getInstance().EndFrameUI();
         UIManager.useUIFBO = var1;
      }
   }

   private void renderAttribution() {
      Texture var1 = Texture.getSharedTexture("media/ui/FMODLogo.png");
      if (var1 != null && var1.isReady()) {
         int var2 = TextManager.instance.getFontHeight(UIFont.Small);
         byte var3 = 32;
         int var4 = (int)((float)var1.getWidth() * ((float)var3 / (float)var1.getHeight()));
         int var5 = Core.getInstance().getScreenWidth() / 2 - var4 / 2;
         int var6 = Core.getInstance().getScreenHeight() - 24 - var2;
         int var7 = var6 - 16 - var3;
         SpriteRenderer.instance.renderi(var1, var5, var7, var4, var3, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
         String var8 = "Made with FMOD Studio by Firelight Technologies Pty Ltd.";
         TextManager.instance.DrawStringCentre((double)Core.getInstance().getScreenWidth() / 2.0D, (double)var6, var8, 1.0D, 1.0D, 1.0D, 1.0D);
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
            SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
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
