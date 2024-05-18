package zombie.ui;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;

public class LevelUpScreen extends NewWindow implements UIEventHandler {
   ArrayList cats = new ArrayList();
   DialogButton but;
   boolean bDirty = true;

   public LevelUpScreen(int var1, int var2) {
      super(var1, var2, 10, 10, true);
      this.IgnoreLossControl = true;
      this.ResizeToFitY = false;
      this.width = 820.0F;
      this.height = 600.0F;
      int var3 = 40;

      for(int var4 = 0; var4 < PerkFactory.PerkList.size(); ++var4) {
         PerkFactory.Perk var5 = (PerkFactory.Perk)PerkFactory.PerkList.get(var4);
         if (var5.parent == PerkFactory.Perks.None) {
            LevelUpCategory var6 = new LevelUpCategory(var5.type);
            var6.x = 10.0D;
            var6.y = (double)var3;
            this.AddChild(var6);
            var3 += 80;
            this.cats.add(var6);
         }
      }

      this.height = (float)(var3 + 8 + 24);
      this.but = new DialogButton(this, (float)(this.getWidth().intValue() - 30), (float)(this.getHeight().intValue() - 24), "Done", "done");
      this.AddChild(this.but);
   }

   public void ButtonClicked(String var1) {
      if (var1.equals("done") || var1.equals("close")) {
         if (!IsoPlayer.instance.getCanUpgradePerk().isEmpty() && IsoPlayer.getInstance().getNumberOfPerksToPick() > 0) {
            UIManager.DoModal("close", "You still have skills points available.", false, this);
         } else {
            this.setVisible(false);
            UIManager.getSpeedControls().SetCurrentGameSpeed(3);
         }
      }

   }

   public void init() {
   }

   public void update() {
      if (this.bDirty && this.visible) {
         this.reset();
      }

      super.update();
   }

   public void render() {
      super.render();
      if (this.isVisible()) {
         this.DrawText(UIFont.Small, "Skills to pick: " + IsoPlayer.getInstance().getNumberOfPerksToPick(), 10.0D, 23.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      }

   }

   public void DoubleClick(String var1, int var2, int var3) {
   }

   public void Selected(String var1, int var2, int var3) {
   }

   public void reset() {
      if (!this.visible) {
         this.bDirty = true;
      } else if (this.bDirty) {
         for(int var1 = 0; var1 < this.cats.size(); ++var1) {
            ((LevelUpCategory)this.cats.get(var1)).reset();
         }

         this.bDirty = false;
      }
   }

   public void ModalClick(String var1, String var2) {
      if (var1.equals("close") && var2.equals("Yes")) {
         this.setVisible(false);
         UIManager.getSpeedControls().SetCurrentGameSpeed(3);
      }

   }

   public void resetAllButton() {
      for(int var1 = 0; var1 < this.cats.size(); ++var1) {
         LevelUpCategory var2 = (LevelUpCategory)this.cats.get(var1);

         for(int var3 = 0; var3 < var2.getPerks().length; ++var3) {
            PerkButton[] var4 = var2.getPerks()[var3];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               PerkButton var6 = var4[var5];
               if (!var6.bPicked) {
                  var6.bAvailable = false;
               }
            }
         }
      }

   }
}
