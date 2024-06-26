package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class IsNumberOfEnemiesOver extends BaseCommand {
   String owner;
   String stat;
   int number = 0;
   IsoGameCharacter chr;
   boolean invert = false;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      if (var1.indexOf("!") == 0) {
         this.invert = true;
         var1 = var1.substring(1);
      }

      this.number = Integer.parseInt(var2[0].trim());
      this.owner = var1;
   }

   public boolean getValue() {
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         this.chr = this.currentinstance.getAlias(this.owner);
      } else {
         this.chr = this.module.getCharacter(this.owner).Actual;
      }

      if (this.chr == null) {
         return false;
      } else if (this.number < this.chr.getLocalRelevantEnemyList().size()) {
         return !this.invert;
      } else {
         return this.invert;
      }
   }

   public void begin() {
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return true;
   }

   public boolean DoesInstantly() {
      return true;
   }
}
