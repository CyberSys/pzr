package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;

public class StatBar extends UIElement {
   Texture background;
   Color col;
   float deltaValue = 1.0F;
   Texture foreground;
   boolean vertical = false;

   public StatBar(int var1, int var2, Texture var3, Texture var4, boolean var5, Color var6) {
      this.col = var6;
      this.vertical = var5;
      this.background = var3;
      this.foreground = var4;
      this.x = (double)var1;
      this.y = (double)var2;
      this.width = (float)var3.getWidth();
      this.height = (float)var3.getHeight();
   }

   public void render() {
      this.DrawUVSliceTexture(this.background, 0.0D, 0.0D, (double)this.background.getWidth(), (double)this.background.getHeight(), this.col, 0.0D, 0.0D, 1.0D, 1.0D);
      if (this.vertical) {
         this.DrawUVSliceTexture(this.foreground, 0.0D, 0.0D, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), this.col, 0.0D, (double)(1.0F - this.deltaValue), 1.0D, 1.0D);
      } else {
         this.DrawUVSliceTexture(this.foreground, 0.0D, 0.0D, (double)this.foreground.getWidth(), (double)this.foreground.getHeight(), this.col, 0.0D, 0.0D, (double)(1.0F - this.deltaValue), 1.0D);
      }

   }

   public void setValue(float var1) {
      this.deltaValue = var1;
   }
}
