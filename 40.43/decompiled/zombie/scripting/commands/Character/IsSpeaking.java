package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class IsSpeaking extends BaseCommand {
   boolean invert = false;
   String character;

   public void begin() {
   }

   public boolean getValue() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.character);
      if (var1 == null) {
         return false;
      } else if (this.invert) {
         return !var1.IsSpeaking();
      } else {
         return var1.IsSpeaking();
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.character = var1;
      if (this.character.indexOf("!") == 0) {
         this.invert = true;
         this.character = this.character.substring(1);
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
