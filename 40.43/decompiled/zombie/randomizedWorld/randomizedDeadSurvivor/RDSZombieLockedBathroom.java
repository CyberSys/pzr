package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.VirtualZombieManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.network.GameServer;

public class RDSZombieLockedBathroom extends RandomizedDeadSurvivorBase {
   private ArrayList weaponsList = new ArrayList();
   private ArrayList ammoList = new ArrayList();

   public void randomizeDeadSurvivor(BuildingDef var1) {
      IsoDeadBody var2 = null;

      for(int var3 = 0; var3 < var1.rooms.size(); ++var3) {
         RoomDef var4 = (RoomDef)var1.rooms.get(var3);
         IsoGridSquare var5 = null;
         if ("bathroom".equals(var4.name)) {
            if (IsoWorld.getZombiesEnabled()) {
               IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var4.getX(), var4.getY(), var4.getZ());
               if (var6 != null && var6.getRoom() != null) {
                  IsoRoom var7 = var6.getRoom();
                  var6 = var7.getRandomFreeSquare();
                  if (var6 != null) {
                     VirtualZombieManager.instance.choices.clear();
                     VirtualZombieManager.instance.choices.add(var6);
                     VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
                  }
               }
            }

            int var10;
            int var11;
            for(var10 = var4.x - 1; var10 < var4.x2 + 1; ++var10) {
               for(var11 = var4.y - 1; var11 < var4.y2 + 1; ++var11) {
                  var5 = IsoWorld.instance.getCell().getGridSquare(var10, var11, var4.getZ());
                  if (var5 != null) {
                     IsoDoor var8 = var5.getIsoDoor();
                     if (var8 != null && this.isDoorToRoom(var8, var4)) {
                        if (var8.IsOpen()) {
                           var8.ToggleDoor((IsoGameCharacter)null);
                        }

                        IsoBarricade var9 = IsoBarricade.AddBarricadeToObject(var8, var5.getRoom().def == var4);
                        if (var9 != null) {
                           var9.addPlank((IsoGameCharacter)null, (InventoryItem)null);
                           if (GameServer.bServer) {
                              var9.transmitCompleteItemToClients();
                           }
                        }

                        var2 = this.addDeadBodyTheOtherSide(var8);
                        break;
                     }
                  }
               }

               if (var2 != null) {
                  break;
               }
            }

            var10 = Rand.Next(5, 10);
            if (var2 != null) {
               var2.getContainer().addItem(InventoryItemFactory.CreateItem("Base.Pistol"));

               for(var11 = 0; var11 < var10; ++var11) {
                  var2.getContainer().addItem(InventoryItemFactory.CreateItem("Base.Bullets9mm"));
               }
            }

            return;
         }
      }

   }

   private boolean isDoorToRoom(IsoDoor var1, RoomDef var2) {
      if (var1 != null && var2 != null) {
         IsoGridSquare var3 = var1.getSquare();
         IsoGridSquare var4 = var1.getOppositeSquare();
         if (var3 != null && var4 != null) {
            return var3.getRoomID() == var2.ID != (var4.getRoomID() == var2.ID);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean checkIsBathroom(IsoGridSquare var1) {
      return var1.getRoom() != null && "bathroom".equals(var1.getRoom().getName());
   }

   private IsoDeadBody addDeadBodyTheOtherSide(IsoDoor var1) {
      IsoGridSquare var2 = null;
      if (var1.north) {
         var2 = IsoWorld.instance.getCell().getGridSquare((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
         if (this.checkIsBathroom(var2)) {
            var2 = IsoWorld.instance.getCell().getGridSquare((double)var1.getX(), (double)(var1.getY() - 1.0F), (double)var1.getZ());
         }
      } else {
         var2 = IsoWorld.instance.getCell().getGridSquare((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
         if (this.checkIsBathroom(var2)) {
            var2 = IsoWorld.instance.getCell().getGridSquare((double)(var1.getX() - 1.0F), (double)var1.getY(), (double)var1.getZ());
         }
      }

      return super.createRandomDeadBody(var2.getX(), var2.getY(), var2.getZ());
   }

   public RDSZombieLockedBathroom() {
      this.weaponsList.add("Base.Pistol");
      this.ammoList.add("Bullets9mm");
   }
}
