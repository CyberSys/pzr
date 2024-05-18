package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.commands.BaseCommand;

public class AddEnemy extends BaseCommand {
   String owner;
   String say;
   String Other;
   IsoGameCharacter chr;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;
      String var3 = "";
      this.Other = var2[0].trim();
   }

   public void begin() {
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         this.chr = this.currentinstance.getAlias(this.owner);
      } else {
         if (this.module.getCharacter(this.owner) == null) {
            return;
         }

         if (this.module.getCharacter(this.owner).Actual == null) {
            return;
         }

         this.chr = this.module.getCharacter(this.owner).Actual;
      }

      IsoGameCharacter var1;
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.Other)) {
         var1 = this.currentinstance.getAlias(this.Other);
      } else {
         if (this.module.getCharacter(this.Other) == null) {
            return;
         }

         if (this.module.getCharacter(this.Other).Actual == null) {
            return;
         }

         var1 = this.module.getCharacter(this.Other).Actual;
      }

      if (this.chr instanceof IsoSurvivor) {
         if (this.chr.getDescriptor().getGroup().isMember(var1)) {
            if (this.chr.getDescriptor().getGroup().Leader == var1.getDescriptor()) {
               this.chr.getDescriptor().getGroup().Members.remove(this.chr);
            } else {
               this.chr.getDescriptor().getGroup().Members.remove(var1);
            }
         }

         ((IsoSurvivor)this.chr).getEnemyList().add(var1);
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
