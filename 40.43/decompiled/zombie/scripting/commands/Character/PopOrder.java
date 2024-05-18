package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class PopOrder extends BaseCommand {
   String owner;
   int index = -1;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      if (var2.length == 1) {
         this.index = Integer.parseInt(var2[0].trim());
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
         if (this.index == -1) {
            var1.getOrders().pop();
         } else {
            this.index = var1.getOrders().size() - this.index - 1;
            if (this.index < var1.getOrders().size() && this.index >= 0) {
               var1.getOrders().remove(this.index);
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
