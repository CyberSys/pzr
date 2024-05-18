package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.scripting.commands.BaseCommand;

public class EquipItem extends BaseCommand {
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
         String var3 = this.item;
         if (var3.contains(".")) {
            var3 = var3.substring(var3.lastIndexOf(".") + 1);
         }

         if (var2.contains(var3)) {
            var1.setPrimaryHandItem(var2.FindAndReturn(var3));
         }
      }

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
