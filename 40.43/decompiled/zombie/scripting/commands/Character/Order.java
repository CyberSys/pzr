package zombie.scripting.commands.Character;

import zombie.behaviors.survivor.orders.FollowOrder;
import zombie.behaviors.survivor.orders.IdleOrder;
import zombie.behaviors.survivor.orders.LittleTasks.FaceOrder;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class Order extends BaseCommand {
   String owner;
   boolean bGory = false;
   String[] params;
   String order = null;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      this.params = new String[var2.length - 1];
      int var3 = 0;
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         if (var3 > 0) {
            this.params[var3 - 1] = var7.trim();
         }

         ++var3;
      }

      this.order = var2[0].trim();
   }

   public zombie.behaviors.survivor.orders.Order orderInfo(IsoGameCharacter var1) {
      if (this.order.equals("Idle")) {
         var1.getOrders().push(new IdleOrder(var1));
      }

      IsoGameCharacter var2;
      int var3;
      if (this.order.equals("Follow")) {
         var2 = null;
         if (this.currentinstance.HasAlias(this.params[0])) {
            var2 = this.currentinstance.getAlias(this.params[0]);
         } else {
            var2 = this.module.getCharacterActual(this.params[0]);
         }

         var3 = Integer.parseInt(this.params[1]);
         var1.getOrders().push(new FollowOrder(var1, var2, var3));
      } else if (this.order.equals("Face")) {
         var2 = null;
         if (this.currentinstance.HasAlias(this.params[0])) {
            var2 = this.currentinstance.getAlias(this.params[0]);
         } else {
            var2 = this.module.getCharacterActual(this.params[0]);
         }

         var1.getOrders().push(new FaceOrder(var1, var2));
      } else if (this.order.equals("FollowStrict")) {
         var2 = null;
         if (this.currentinstance.HasAlias(this.params[0])) {
            var2 = this.currentinstance.getAlias(this.params[0]);
         } else {
            var2 = this.module.getCharacterActual(this.params[0]);
         }

         var3 = Integer.parseInt(this.params[1]);
         var1.getOrders().push(new FollowOrder(var1, var2, var3, true));
      }

      var1.setOrder((zombie.behaviors.survivor.orders.Order)var1.getOrders().peek());
      return (zombie.behaviors.survivor.orders.Order)var1.getOrders().peek();
   }

   public void begin() {
      IsoGameCharacter var1 = null;
      if (this.currentinstance.HasAlias(this.owner)) {
         var1 = this.currentinstance.getAlias(this.owner);
      } else {
         var1 = this.module.getCharacterActual(this.owner);
      }

      this.orderInfo(var1);
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
