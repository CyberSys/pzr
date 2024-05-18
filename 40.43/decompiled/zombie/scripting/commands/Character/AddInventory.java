package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptContainer;

public class AddInventory extends BaseCommand {
   String owner;
   String item;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      this.item = var2[0];
   }

   public void begin() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      ItemContainer var2 = null;
      if (var1 != null) {
         var2 = var1.getInventory();
      } else {
         ScriptContainer var3 = this.module.getScriptContainer(this.owner);
         if (var3 == null) {
            return;
         }

         var2 = var3.getActual();
         if (var2 == null) {
            return;
         }
      }

      String var4 = this.item;
      if (!var4.contains(".")) {
         var4 = this.module.name + "." + var4;
      }

      var2.AddItem(var4);
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
