package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;

public class NamedOrder extends Order {
   String name;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      this.params = new String[var2.length - 2];
      int var3 = 0;
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         if (var3 > 1) {
            this.params[var3 - 2] = var7.trim();
         }

         ++var3;
      }

      this.name = var2[0].trim();
      this.order = var2[1].trim();
   }

   public void begin() {
      IsoGameCharacter var1 = null;
      if (this.currentinstance.HasAlias(this.owner)) {
         var1 = this.currentinstance.getAlias(this.owner);
      } else {
         var1 = this.module.getCharacterActual(this.owner);
      }

      zombie.behaviors.survivor.orders.Order var2 = this.orderInfo(var1);
      var2.name = this.name;
   }
}
