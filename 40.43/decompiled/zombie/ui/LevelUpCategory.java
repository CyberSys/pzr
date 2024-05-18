package zombie.ui;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.core.Color;
import zombie.core.textures.Texture;

public class LevelUpCategory extends UIElement implements UIEventHandler {
   public PerkFactory.Perk perk;
   public PerkFactory.Perks perkType;
   public ArrayList subPerks = new ArrayList();
   PerkButton[][] perkbuttons;
   private String chosenButtonPerk = "";
   static Texture white;
   PerkFactory.Perks chosenperk;

   public PerkButton[][] getPerks() {
      return this.perkbuttons;
   }

   public void reset() {
      if (white == null) {
         white = Texture.getSharedTexture("white.png");
      }

      this.subPerks.clear();
      this.getControls().clear();
      this.perk = (PerkFactory.Perk)PerkFactory.PerkMap.get(this.perkType);
      this.setWidth(800.0D);
      this.setHeight(80.0D);

      int var1;
      for(var1 = 0; var1 < PerkFactory.PerkList.size(); ++var1) {
         PerkFactory.Perk var2 = (PerkFactory.Perk)PerkFactory.PerkList.get(var1);
         if (var2.parent == this.perkType) {
            this.subPerks.add(var2);
         }
      }

      this.perkbuttons = new PerkButton[this.subPerks.size()][5];

      for(var1 = 0; var1 < this.subPerks.size(); ++var1) {
         for(int var6 = 0; var6 < 5; ++var6) {
            boolean var3 = IsoPlayer.getInstance().getCanUpgradePerk().contains(((PerkFactory.Perk)this.subPerks.get(var1)).type);
            int var4 = IsoPlayer.getInstance().getPerkLevel(((PerkFactory.Perk)this.subPerks.get(var1)).type);
            if (var6 > var4) {
               var3 = false;
            }

            if (IsoPlayer.getInstance().getNumberOfPerksToPick() <= 0) {
               var3 = false;
            }

            boolean var5 = var4 > var6;
            this.perkbuttons[var1][var6] = new PerkButton(((PerkFactory.Perk)this.subPerks.get(var1)).name, 0, 0, white, var3, var5, this);
            this.perkbuttons[var1][var6].setWidth(16.0D);
            this.perkbuttons[var1][var6].setHeight(16.0D);
            this.AddChild(this.perkbuttons[var1][var6]);
         }
      }

   }

   public LevelUpCategory(PerkFactory.Perks var1) {
      this.perkType = var1;
      this.reset();
   }

   public void render() {
      this.DrawTextureScaledCol(white, 0.0D, 0.0D, this.getWidth(), this.getHeight(), new Color(0.3F, 0.3F, 0.3F, 0.2F));
      this.DrawTextureScaledCol(white, 0.0D, 0.0D, 172.0D, this.getHeight(), new Color(0.3F, 0.3F, 0.3F, 0.2F));
      this.DrawText(UIFont.Large, this.perk.name.toUpperCase(), 64.0D, this.getHeight() / 2.0D - 12.0D, 1.0D, 1.0D, 1.0D, 1.0D);
      int var1 = 288;
      int var2 = 20;

      for(int var3 = 0; var3 < this.subPerks.size(); ++var3) {
         PerkFactory.Perk var4 = (PerkFactory.Perk)this.subPerks.get(var3);
         this.DrawTextRight(UIFont.Small, var4.name.toUpperCase(), (double)(var1 + 3), (double)var2, 1.0D, 1.0D, 1.0D, 1.0D);
         int var5 = var1 + 8;

         for(int var6 = 0; var6 < 5; ++var6) {
            this.perkbuttons[var3][var6].setX((double)(var5 + 5));
            this.perkbuttons[var3][var6].setY((double)(var2 - 3));
            var5 += 17;
         }

         var2 += 30;
         if ((double)var2 > this.getHeight() - 24.0D) {
            var2 = 20;
            var1 += 192;
         }
      }

      this.DrawTextureScaledCol(white, 0.0D, 78.0D, this.getWidth(), 1.0D, new Color(0.2F, 0.2F, 0.2F, 0.8F));
      this.DrawTextureScaledCol(white, 0.0D, 79.0D, this.getWidth(), 1.0D, new Color(0.0F, 0.0F, 0.0F, 1.0F));
      super.render();
   }

   public void DoubleClick(String var1, int var2, int var3) {
   }

   public void ModalClick(String var1, String var2) {
      if (var1.equals("chooseperk") && var2.equals("Yes")) {
         IsoPlayer.getInstance().LevelPerk(this.chosenperk);
         this.updateButton();

         for(int var3 = 0; var3 < IsoPlayer.getInstance().getCanUpgradePerk().size(); ++var3) {
            if (IsoPlayer.getInstance().getCanUpgradePerk().get(var3) == this.chosenperk) {
               IsoPlayer.getInstance().getCanUpgradePerk().remove(var3);
               PerkFactory.CheckForUnlockedPerks(IsoPlayer.getInstance());
               break;
            }
         }
      }

   }

   private void updateButton() {
      for(int var1 = 0; var1 < this.perkbuttons.length; ++var1) {
         PerkButton[] var2 = this.perkbuttons[var1];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            PerkButton var4 = var2[var3];
            if (!var4.name.equals(this.chosenButtonPerk)) {
               var4.bAvailable = false;
               break;
            }

            if (var4.bAvailable && !var4.bPicked) {
               var4.bAvailable = false;
               var4.bPicked = true;
               break;
            }
         }
      }

   }

   public void Selected(String var1, int var2, int var3) {
      PerkFactory.Perks var4 = PerkFactory.getPerkFromName(var1);
      PerkFactory.Perk var5 = (PerkFactory.Perk)PerkFactory.PerkMap.get(var4);
      this.chosenperk = var4;
      this.chosenButtonPerk = var5.name;
      UIManager.DoModal("chooseperk", "Upgrade this skill?", true, this);
   }
}
