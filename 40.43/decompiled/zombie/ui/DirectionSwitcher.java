package zombie.ui;

import zombie.core.Core;
import zombie.core.textures.Texture;

public class DirectionSwitcher extends UIElement {
   Texture texture = Texture.getSharedTexture("media/ui/Controls_Large_TypeA.png");
   Texture texture2 = Texture.getSharedTexture("media/ui/Controls_Large_TypeB.png");
   Texture texture_small = Texture.getSharedTexture("media/ui/Controls_Small_TypeA.png");
   Texture texture2_small = Texture.getSharedTexture("media/ui/Controls_Small_TypeB.png");

   public DirectionSwitcher(int var1, int var2) {
      this.x = (double)var1;
      this.y = (double)var2;
      this.width = (float)(this.texture.getWidth() - 8);
      this.height = (float)this.texture.getHeight();
   }

   public Boolean onMouseDown(int var1, int var2) {
      Core.getInstance().MoveMethodToggle();
      return Boolean.TRUE;
   }

   public Boolean onMouseMove(int var1, int var2) {
      return Boolean.TRUE;
   }

   public void onMouseMoveOutside(int var1, int var2) {
   }

   public Boolean onMouseUp(int var1, int var2) {
      return Boolean.TRUE;
   }

   public void render() {
      if (Core.bAltMoveMethod) {
         this.DrawTextureScaled(this.texture2, 0.0D, 0.0D, (double)this.texture2.getWidth(), (double)this.texture2.getHeight(), 0.8500000238418579D);
      } else {
         this.DrawTextureScaled(this.texture, 0.0D, 0.0D, (double)this.texture.getWidth(), (double)this.texture.getHeight(), 0.8500000238418579D);
      }

      super.render();
   }

   public void update() {
      super.update();
   }
}
