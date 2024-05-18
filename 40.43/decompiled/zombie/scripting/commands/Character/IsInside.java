package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class IsInside extends BaseCommand {
   String owner;
   boolean invert = false;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      if (this.owner.indexOf("!") == 0) {
         this.invert = true;
         this.owner = this.owner.substring(1);
      }

   }

   public boolean getValue() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      if (var1 == null) {
         return false;
      } else if (this.invert) {
         return var1.getCurrentSquare().getRoom() == null;
      } else {
         return var1.getCurrentSquare().getRoom() != null;
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
