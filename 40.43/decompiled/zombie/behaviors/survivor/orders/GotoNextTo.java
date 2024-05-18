package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.LittleTasks.FaceOrder;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class GotoNextTo extends Order {
   OrderSequence order = null;

   public GotoNextTo(IsoGameCharacter var1, int var2, int var3, int var4) {
      super(var1);
      this.order = new OrderSequence(var1);
      Stack var5 = new Stack();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);

      for(int var7 = -1; var7 <= 1; ++var7) {
         for(int var8 = -1; var8 <= 1; ++var8) {
            if ((var7 == 0 || var8 == 0) && (var7 != 0 || var8 != 0)) {
               IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare(var2 + var7, var3 + var8, var4);
               if (var9 != null && var9.isFree(false) && (var6 == null || var9.getRoom() == var6.getRoom())) {
                  var5.add(var9);
               }
            }
         }
      }

      if (!var5.isEmpty()) {
         IsoGridSquare var10 = (IsoGridSquare)var5.get(Rand.Next(var5.size()));
         this.order.Orders.add(new GotoOrder(var1, var10.getX(), var10.getY(), var10.getZ()));
         Vector2 var11 = new Vector2(0.0F, 0.0F);
         var11.x = (float)(var2 - var10.getX());
         var11.y = (float)(var3 - var10.getY());
         this.order.Orders.add(new FaceOrder(var1, IsoDirections.fromAngle(var11)));
      }
   }

   public int getAttackIfEnemiesAroundBias() {
      return this.character.getCurrentSquare().getRoom() != null ? -1000 : 0;
   }

   public void update() {
      if (this.order != null) {
         this.order.update();
      }

   }

   public boolean complete() {
      return this.order == null ? true : this.order.complete();
   }

   public Behavior.BehaviorResult process() {
      return this.order == null ? Behavior.BehaviorResult.Failed : this.order.process();
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "GotoNextTo", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      if (this.order != null) {
         this.order.renderDebug(var1);
      }

      return var1;
   }

   public void initOrder() {
      if (this.order != null) {
         this.order.initOrder();
      }

   }

   public float getPriority(IsoGameCharacter var1) {
      return this.order == null ? -100000.0F : this.order.getPriority(var1);
   }
}
