package zombie.ui;

import zombie.core.textures.Texture;
import zombie.network.GameServer;

public class PerkButton extends UIElement {
   boolean clicked = false;
   UIElement display;
   Texture overicon = null;
   boolean mouseOver = false;
   String name;
   Texture texture;
   boolean bPicked = false;
   boolean bAvailable = false;
   UIEventHandler handler;
   public float notclickedAlpha = 0.85F;
   public float clickedalpha = 1.0F;

   public PerkButton(String var1, int var2, int var3, Texture var4, boolean var5, boolean var6, UIEventHandler var7) {
      if (!GameServer.bServer) {
         this.bAvailable = var5;
         this.bPicked = var6;
         this.texture = var4;
         this.handler = var7;
         this.name = var1;
         if (this.texture == null) {
            this.texture = var4;
         }

         this.x = (double)var2;
         this.y = (double)var3;
         this.width = (float)this.texture.getWidth();
         this.height = (float)this.texture.getHeight();
      }
   }

   public Boolean onMouseDown(double var1, double var3) {
      if (this.bAvailable && !this.bPicked) {
         this.clicked = true;
      }

      return Boolean.TRUE;
   }

   public Boolean onMouseMove(double var1, double var3) {
      this.mouseOver = true;
      return Boolean.TRUE;
   }

   public void onMouseMoveOutside(double var1, double var3) {
      this.clicked = false;
      if (this.display != null) {
         if (!this.name.equals(this.display.getClickedValue())) {
            this.mouseOver = false;
         }

      }
   }

   public Boolean onMouseUp(double var1, double var3) {
      if (this.clicked) {
         if (this.display != null) {
            this.display.ButtonClicked(this.name);
         } else if (this.handler != null) {
            this.handler.Selected(this.name, 0, 0);
         }
      }

      this.clicked = false;
      return Boolean.TRUE;
   }

   public void render() {
      int var1 = 0;
      if (this.clicked) {
         ++var1;
      }

      float var2 = 1.0F;
      if (this.bAvailable && !this.bPicked) {
         var2 = 0.5F;
      }

      if (!this.bAvailable) {
         var2 = 0.2F;
      }

      if (this.bPicked) {
         var2 = 1.0F;
      }

      this.DrawTextureScaled(this.texture, 0.0D, (double)var1, this.getWidth(), this.getHeight(), (double)var2);
      super.render();
   }

   public void update() {
      super.update();
   }
}
