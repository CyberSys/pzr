package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptContainer;

public class HasInventory extends BaseCommand {
   boolean invert = false;
   String character;
   String item;

   public void begin() {
   }

   public boolean getValue() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.character);
      ItemContainer var2 = null;
      if (var1 != null) {
         var2 = var1.getInventory();
      } else {
         ScriptContainer var3 = this.module.getScriptContainer(this.character);
         if (var3 == null) {
            return false;
         }

         var2 = var3.getActual();
         if (var2 == null) {
            return false;
         }
      }

      if (this.invert) {
         return !var2.contains(this.item);
      } else {
         return var2.contains(this.item);
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.character = var1;
      this.item = var2[0].replace("\"", "");
      if (this.character.indexOf("!") == 0) {
         this.invert = true;
         this.character = this.character.substring(1);
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
