package zombie.ui;

import java.io.FileNotFoundException;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;

public class DraggableWindow extends UIElement {
   public float alpha = 1.0F;
   public int clickX = 0;
   public int clickY = 0;
   public boolean closing = false;
   public boolean moving = false;
   public int rwidth = 256;
   Texture background = null;
   AngelCodeFont font = new AngelCodeFont("media/zombiefont2.fnt", "zombiefont2_0");
   Texture titlebar = null;

   public DraggableWindow(int var1, int var2, String var3, String var4) throws FileNotFoundException {
      this.background = TexturePackPage.getTexture(var4);
      this.titlebar = TexturePackPage.getTexture(var3);
      this.x = (double)var1;
      this.y = (double)var2;
      this.width = 256.0F;
      this.height = 256.0F;
      this.alpha = 0.75F;
      this.visible = false;
   }

   public Boolean onMouseDown(double var1, double var3) {
      super.onMouseDown(var1, var3);
      if (var3 < 16.0D && var1 < (double)(this.rwidth - 20)) {
         this.clickX = (int)var1;
         this.clickY = (int)var3;
         this.moving = true;
         this.setCapture(true);
      } else if (var3 < 16.0D && var1 < this.getWidth()) {
         this.closing = true;
      }

      return Boolean.TRUE;
   }

   public Boolean onMouseMove(double var1, double var3) {
      super.onMouseMove(var1, var3);
      if (this.moving) {
         this.setX(this.getX() + var1);
         this.setY(this.getY() + var3);
      }

      return Boolean.FALSE;
   }

   public Boolean onMouseUp(double var1, double var3) {
      super.onMouseUp(var1, var3);
      if (var3 < 16.0D && var1 >= (double)(this.rwidth - 20) && this.closing) {
         this.setVisible(false);
      }

      this.moving = false;
      this.setCapture(false);
      return Boolean.TRUE;
   }

   public void render() {
      if (this.background != null) {
         if (this.moving) {
            this.DrawTexture(this.titlebar, 0.0D, 0.0D, 1.0D);
         } else {
            this.DrawTexture(this.titlebar, 0.0D, 0.0D, 0.75D);
         }

         this.DrawTexture(this.background, 0.0D, 16.0D, 0.75D);
         super.render();
      }
   }

   public void update() {
      super.update();
   }
}
