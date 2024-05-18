package zombie.ui;

import zombie.core.Core;
import zombie.scripting.ScriptManager;

public class CharacterCreationPanel extends NewWindow {
   public static CharacterCreationPanel instance;

   public CharacterCreationPanel(int var1, int var2) {
      super(var1, var2, 10, 10, false);
      this.ResizeToFitY = false;
      this.visible = false;
      instance = this;
      this.width = 750.0F;
      this.height = 570.0F;
      this.Movable = false;
      boolean var3 = true;
      boolean var4 = true;
   }

   public void enter() {
      ScriptManager.instance.Trigger("OnPreCharacterCreation");
   }

   public void render() {
      if (this.isVisible()) {
         super.render();
         this.DrawTextCentre("Create your character", this.getWidth() / 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawTextCentre("Viewer", 430.0D, 33.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawText("Forename", 532.0D, 31.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawText("Surname", 532.0D, 72.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawTextRight("Face", 624.0D, 133.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawTextRight("Skin-tone", 624.0D, 150.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawText("Select a profession (many more to come in future updates)", 30.0D, 194.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawTextCentre("Available traits", 438.0D, 194.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         this.DrawTextCentre("Chosen traits", 626.0D, 194.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      }
   }

   public void update() {
      if (this.isVisible()) {
         super.update();
         float var1 = (float)this.getAbsoluteY().intValue();
         float var2 = var1 - 40.0F;
         float var3 = (float)Core.getInstance().getOffscreenHeight(0) - var1;
         if (var3 > 0.0F) {
            var2 /= var3;
         } else {
            var2 = 1.0F;
         }

         var2 *= 4.0F;
         var2 = 1.0F - var2;
         if (var2 < 0.0F) {
            var2 = 0.0F;
         }

      }
   }
}
