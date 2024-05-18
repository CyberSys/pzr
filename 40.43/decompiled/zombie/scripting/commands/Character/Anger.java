package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class Anger extends BaseCommand {
   String owner;
   String say;
   String Other;
   IsoGameCharacter chr;
   int amount = 0;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;
      String var3 = "";
      this.amount = Integer.parseInt(var2[0].trim());
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

      this.chr.Anger(this.amount);
   }

   public boolean DoesInstantly() {
      return true;
   }
}
