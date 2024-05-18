package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

public class RDSGunmanInBathroom extends RandomizedDeadSurvivorBase {
   private ArrayList weaponsList = new ArrayList();
   private ArrayList ammoList = new ArrayList();

   public void randomizeDeadSurvivor(BuildingDef var1) {
      for(int var2 = 0; var2 < var1.rooms.size(); ++var2) {
         RoomDef var3 = (RoomDef)var1.rooms.get(var2);
         if ("bathroom".equals(var3.name)) {
            IsoDeadBody var4 = super.createRandomDeadBody(var3);
            if (var4 != null) {
               int var5 = Rand.Next(5, 10);
               var4.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.weaponsList.get(Rand.Next(0, this.weaponsList.size()))));

               for(int var6 = 0; var6 < var5; ++var6) {
                  var4.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.ammoList.get(Rand.Next(0, this.ammoList.size()))));
               }

               return;
            }
         }
      }

   }

   public RDSGunmanInBathroom() {
      this.weaponsList.add("Base.Shotgun");
      this.weaponsList.add("Base.Pistol");
      this.ammoList.add("Base.ShotgunShells");
      this.ammoList.add("Bullets9mm");
      this.ammoList.add("BulletsBox");
   }
}
