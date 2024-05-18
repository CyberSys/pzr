package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class IsOnFloor extends BaseCommand {
   String owner;
   int min = 0;
   int max = 0;
   boolean invert = false;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      if (this.owner.indexOf("!") == 0) {
         this.invert = true;
         this.owner = this.owner.substring(1);
      }

      if (var2.length == 1) {
         this.min = this.max = Integer.parseInt(var2[0].trim());
      }

      if (var2.length == 2) {
         this.min = Integer.parseInt(var2[0].trim());
         this.max = Integer.parseInt(var2[1].trim());
      }

   }

   public boolean getValue() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      if (var1 == null) {
         return false;
      } else if (this.invert) {
         return !(var1.getZ() >= (float)this.min) || !(var1.getZ() <= (float)this.max);
      } else {
         return var1.getZ() >= (float)this.min && var1.getZ() <= (float)this.max;
      }
   }

   public void begin() {
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
