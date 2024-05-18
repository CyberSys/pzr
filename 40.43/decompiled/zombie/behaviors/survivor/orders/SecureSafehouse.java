package zombie.behaviors.survivor.orders;

import zombie.behaviors.survivor.orders.LittleTasks.CloseDoor;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoObject;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoomExit;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;

public class SecureSafehouse extends OrderSequence {
   public SecureSafehouse(IsoGameCharacter var1) {
      super(var1);
      IsoBuilding var2 = ((IsoSurvivor)var1).getDescriptor().getGroup().Safehouse;

      for(int var3 = 0; var3 < var2.Exits.size(); ++var3) {
         IsoRoomExit var4 = (IsoRoomExit)var2.Exits.get(var3);
         IsoObject var5 = var4.getDoor(var1.getCell());
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         int var9;
         int var10;
         int var11;
         if (var4.From == null) {
            var9 = var4.To.x;
            var10 = var4.To.y;
            var11 = var4.To.layer;
         } else {
            var9 = var4.x;
            var10 = var4.y;
            var11 = var4.layer;
         }

         this.Orders.add(new GotoOrder(var1, var9, var10, var11));
         if (var5 instanceof IsoDoor) {
            this.Orders.add(new CloseDoor(var1, (IsoDoor)var5));
         } else if (var5 instanceof IsoThumpable) {
            this.Orders.add(new CloseDoor(var1, (IsoThumpable)var5));
         }

         this.Orders.add(new WaitUntilFollowersArrive(var1));
         this.Orders.add(new BlockWindows(var1, var2));
      }

   }
}
