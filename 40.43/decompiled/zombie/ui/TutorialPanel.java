package zombie.ui;

import java.io.FileNotFoundException;
import zombie.core.Core;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;

public class TutorialPanel extends UIElement {
   public float alpha = 0.0F;
   public float alphaStep = 0.05F;
   public boolean hiding = false;
   public int MaxMessageTime = 20;
   public String Message;
   public float MessageTime = 0.0F;
   public String NextMessage = null;
   public String NextMessage2 = null;
   public boolean showing = false;
   public float targetAlpha = 0.0F;
   Texture background = null;
   AngelCodeFont font = new AngelCodeFont("media/zombiefont2.fnt", "zombiefont2_0");
   private String Message2;

   public TutorialPanel() throws FileNotFoundException {
      this.background = TexturePackPage.getTexture("black");
      this.width = 650.0F;
      this.height = 50.0F;
      this.x = (double)(Core.getInstance().getOffscreenWidth(0) / 2);
      this.x -= (double)(this.width / 2.0F);
      this.y = (double)Core.getInstance().getOffscreenHeight(0);
      this.y -= 60.0D;
   }

   public void hide() {
      this.hiding = true;
   }

   public void render() {
      if (this.showing && this.alpha > 0.0F) {
         this.DrawTextureScaled(this.background, 0.0D, 0.0D, this.getWidth(), this.getHeight(), (double)(this.alpha * 0.75F));
         int var1 = Core.getInstance().getOffscreenHeight(0) - 47;
         if (this.Message2 != null) {
            var1 -= 10;
         }

         TextManager.instance.DrawStringCentre(UIFont.Medium, (double)(Core.getInstance().getOffscreenWidth(0) / 2), (double)var1, this.Message, 1.0D, 1.0D, 1.0D, (double)this.alpha);
         if (this.Message2 != null) {
            TextManager.instance.DrawStringCentre(UIFont.Medium, (double)(Core.getInstance().getOffscreenWidth(0) / 2), (double)(var1 + 20), this.Message2, 1.0D, 1.0D, 1.0D, (double)this.alpha);
         }
      }

   }

   public void ShowMessage(String var1, String var2) {
      if (var1 == null || !var1.equals(this.Message) || var2 == null || !var2.equals(this.Message2)) {
         this.MessageTime = 0.0F;
         this.NextMessage = var1;
         this.NextMessage2 = var2;
         this.showing = true;
         this.hiding = false;
      }
   }

   public void update() {
      if (this.NextMessage != null) {
         this.targetAlpha = 0.0F;
      } else if (this.Message != null) {
         this.targetAlpha = 1.0F;
      }

      if (this.hiding) {
         this.targetAlpha = 0.0F;
      }

      if (this.alpha <= 0.0F && this.NextMessage != null) {
         this.Message = this.NextMessage;
         this.Message2 = this.NextMessage2;
         this.NextMessage = null;
         this.NextMessage2 = null;
      }

      if (this.alpha < this.targetAlpha) {
         this.alpha += this.alphaStep;
         if (this.alpha > this.targetAlpha) {
            this.alpha = this.targetAlpha;
         }
      } else if (this.alpha > this.targetAlpha) {
         this.alpha -= this.alphaStep;
         if (this.alpha < this.targetAlpha) {
            this.alpha = this.targetAlpha;
         }
      }

   }
}