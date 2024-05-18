package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class RemoveNamedOrder extends BaseCommand {
   String owner;
   String name;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      if (var2.length == 1) {
         this.name = var2[0].trim();
      }

   }

   public void begin() {
      IsoGameCharacter var1 = null;
      if (this.currentinstance.HasAlias(this.owner)) {
         var1 = this.currentinstance.getAlias(this.owner);
      } else {
         var1 = this.module.getCharacterActual(this.owner);
      }

      if (!var1.getOrders().empty()) {
         for(int var2 = 0; var2 < var1.getOrders().size(); ++var2) {
            if (((zombie.behaviors.survivor.orders.Order)var1.getOrders().get(var2)).name.equals(this.name)) {
               var1.getOrders().remove(var2);
               --var2;
            }
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
