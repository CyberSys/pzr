package zombie.ui;

import zombie.core.Core;
import zombie.core.textures.Texture;

public class DoubleSizer extends UIElement {
   boolean clicked = false;
   UIElement display;
   Texture highlight;
   Texture highlight2;
   boolean mouseOver = false;
   Texture texture;
   Texture texture2;

   public DoubleSizer(int var1, int var2, String var3, String var4, String var5, String var6) {
      this.display = this.display;
      this.texture = Texture.getSharedTexture("media/ui/" + var3 + ".png");
      this.highlight2 = Texture.getSharedTexture("media/ui/" + var6 + ".png");
      this.texture2 = Texture.getSharedTexture("media/ui/" + var5 + ".png");
      this.highlight = Texture.getSharedTexture("media/ui/" + var4 + ".png");
      this.x = (double)var1;
      this.y = (double)var2;
      this.width = (float)this.texture.getWidth();
      this.height = (float)this.texture.getHeight();
   }

   public Boolean onMouseDown(int var1, int var2) {
      this.clicked = true;
      return Boolean.TRUE;
   }

   public Boolean onMouseMove(int var1, int var2) {
      this.mouseOver = true;
      return Boolean.TRUE;
   }

   public void onMouseMoveOutside(int var1, int var2) {
      this.clicked = false;
      this.mouseOver = false;
   }

   public Boolean onMouseUp(int var1, int var2) {
      if (this.clicked) {
         Core.getInstance().doubleSizeToggle();
      }

      this.clicked = false;
      return Boolean.TRUE;
   }

   public void render() {
      if (this.clicked) {
         this.DrawTextureScaled(this.highlight, 0.0D, 0.0D, (double)this.highlight.getWidth(), (double)this.highlight.getHeight(), 1.0D);
      } else if (this.mouseOver) {
         this.DrawTextureScaled(this.texture, 0.0D, 0.0D, (double)this.texture.getWidth(), (double)this.texture.getHeight(), 1.0D);
      } else {
         this.DrawTextureScaled(this.texture, 0.0D, 0.0D, (double)this.texture.getWidth(), (double)this.texture.getHeight(), 0.8500000238418579D);
      }

      super.render();
   }

   public void update() {
      super.update();
   }
}
