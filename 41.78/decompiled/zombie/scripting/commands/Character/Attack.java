package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.scripting.commands.BaseCommand;

public class Attack extends BaseCommand {
   String owner;

   public void init(String var1, String[] var2) {
      this.owner = var1;
   }

   public void begin() {
      IsoGameCharacter var1 = null;
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         var1 = this.currentinstance.getAlias(this.owner);
      } else {
         if (this.module.getCharacter(this.owner) == null) {
            return;
         }

         if (this.module.getCharacter(this.owner).Actual == null) {
            return;
         }

         var1 = this.module.getCharacter(this.owner).Actual;
      }

      ((IsoLivingCharacter)var1).AttemptAttack(1.0F);
   }

   public void Finish() {
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }
}
