package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.commands.BaseCommand;

public class AimWhileStationary extends BaseCommand {
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
      if (this.Other.equals("null")) {
         this.Other = null;
      }

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

      if (this.Other == null && this.chr instanceof IsoSurvivor) {
         ((IsoSurvivor)this.chr).Aim((IsoGameCharacter)null);
      } else {
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
            ((IsoSurvivor)this.chr).Aim(var1);
         }

      }
   }

   public boolean DoesInstantly() {
      return true;
   }
}
