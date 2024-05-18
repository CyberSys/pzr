package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.survivor.orders.LittleTasks.AquireSheetAndBlockWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoWindow;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class BlockWindows extends OrderSequence {
   Stack windowsAll = new Stack();
   Stack windowsFloorAB = new Stack();
   IsoBuilding b;

   public BlockWindows(IsoGameCharacter var1, IsoBuilding var2) {
      super(var1);
      this.b = var2;
   }

   public int renderDebug(int var1) {
      byte var2 = 50;
      TextManager.instance.DrawString(UIFont.Small, (double)var2, (double)var1, "BlockWindows", 1.0D, 1.0D, 1.0D, 1.0D);
      var1 += 30;
      if (this.ID < this.Orders.size()) {
         ((Order)this.Orders.get(this.ID)).renderDebug(var1);
      }

      return var1;
   }

   public void initOrder() {
      int var1;
      if (((IsoSurvivor)this.character).getDescriptor().getGroup().Leader == this.character.getDescriptor()) {
         for(var1 = 0; var1 < this.character.getDescriptor().getGroup().Members.size(); ++var1) {
            SurvivorDesc var2 = (SurvivorDesc)this.character.getDescriptor().getGroup().Members.get(var1);
            if (var2.getInstance() != null && (var2.getInstance().getOrder() == null || var2.getInstance().getOrder() instanceof FollowOrder)) {
               var2.getInstance().GiveOrder(new BlockWindows(var2.getInstance(), this.b), false);
            }
         }
      }

      if (this.b.Windows.size() > 0) {
         IsoWindow var4;
         for(; !this.b.Windows.isEmpty(); this.b.Windows.remove(var4)) {
            var4 = (IsoWindow)this.b.Windows.get(Rand.Next(this.b.Windows.size()));
            if (!this.windowsAll.contains(var4)) {
               IsoCurtain var5 = var4.HasCurtains();
               this.windowsAll.add(var4);
               if (var4.square.getZ() < 2 && (var5 == null || var5.open)) {
                  this.windowsFloorAB.add(var4);
               }
            }
         }

         this.b.Windows.addAll(this.windowsAll);

         for(var1 = 0; var1 < this.windowsFloorAB.size(); ++var1) {
            IsoWindow var6 = (IsoWindow)this.windowsFloorAB.get(var1);
            IsoCurtain var3 = var6.HasCurtains();
            this.Orders.add(new AquireSheetAndBlockWindow(this.character, var6, var3));
         }

      }
   }
}
