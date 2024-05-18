package zombie.ui;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;

public class ClothingPanel extends NewWindow {
   boolean DisableHands = true;
   public static ClothingPanel instance;
   VirtualItemSlot HeadItem = null;
   VirtualItemSlot TorsoItem = null;
   VirtualItemSlot HandsItem = null;
   VirtualItemSlot LegsItem = null;
   VirtualItemSlot FeetItem = null;
   IsoGameCharacter ParentChar;

   public ClothingPanel(int var1, int var2, IsoGameCharacter var3) {
      super(var1, var2, 10, 10, true);
      if (this.DisableHands) {
         this.HeadItem = new VirtualItemSlot("Head_Clothing", 25, 28, "media/ui/ClothingIcons_Head.png", IsoPlayer.getInstance());
         this.TorsoItem = new VirtualItemSlot("Torso_Clothing", 25, 71, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
         this.HandsItem = new VirtualItemSlot("Hands_Clothing", 25, 234523345, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
         this.LegsItem = new VirtualItemSlot("Legs_Clothing", 25, 114, "media/ui/ClothingIcons_Legs.png", IsoPlayer.getInstance());
         this.FeetItem = new VirtualItemSlot("Feet_Clothing", 25, 157, "media/ui/ClothingIcons_Feet.png", IsoPlayer.getInstance());
      } else {
         this.HeadItem = new VirtualItemSlot("Head_Clothing", 25, 28, "media/ui/ClothingIcons_Head.png", IsoPlayer.getInstance());
         this.TorsoItem = new VirtualItemSlot("Torso_Clothing", 25, 71, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
         this.HandsItem = new VirtualItemSlot("Hands_Clothing", 25, 114, "media/ui/ClothingIcons_Torso.png", IsoPlayer.getInstance());
         this.LegsItem = new VirtualItemSlot("Legs_Clothing", 25, 157, "media/ui/ClothingIcons_Legs.png", IsoPlayer.getInstance());
         this.FeetItem = new VirtualItemSlot("Feet_Clothing", 25, 200, "media/ui/ClothingIcons_Feet.png", IsoPlayer.getInstance());
      }

      this.ParentChar = var3;
      this.ResizeToFitY = false;
      this.visible = false;
      instance = this;
      this.width = 82.0F;
      if (this.DisableHands) {
         this.height = (float)(170 + this.titleRight.getHeight() + 5);
      } else {
         this.height = (float)(210 + this.titleRight.getHeight() + 5);
      }

      boolean var4 = true;
      boolean var5 = true;
      this.AddChild(this.HeadItem);
      this.AddChild(this.TorsoItem);
      this.AddChild(this.HandsItem);
      this.AddChild(this.LegsItem);
      this.AddChild(this.FeetItem);
   }

   public void render() {
      if (this.isVisible()) {
         super.render();
         this.DrawTextCentre("Clothing", 40.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      }
   }

   public void update() {
      if (this.isVisible()) {
         super.update();
         float var1 = (float)this.getAbsoluteY().intValue();
         Sidebar var10001 = Sidebar.instance;
         float var2 = var1 - (float)(Sidebar.Clothing.getY().intValue() - 70);
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
