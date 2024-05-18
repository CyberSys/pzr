package zombie.randomizedWorld;

import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;

public class RBSafehouse extends RandomizedBuildingBase {
   public void randomizeBuilding(BuildingDef var1) {
      var1.bAlarmed = false;
      var1.setHasBeenVisited(true);
      KahluaTable var2 = (KahluaTable)LuaManager.env.rawget("ItemPicker");
      KahluaTable var3 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget("SafehouseLoot");
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      InventoryItem var5 = InventoryItemFactory.CreateItem("Base.Plank");

      for(int var6 = var1.x - 1; var6 < var1.x2 + 1; ++var6) {
         for(int var7 = var1.y - 1; var7 < var1.y2 + 1; ++var7) {
            for(int var8 = 0; var8 < 8; ++var8) {
               IsoGridSquare var9 = var4.getGridSquare(var6, var7, var8);
               if (var9 != null) {
                  for(int var10 = 0; var10 < var9.getObjects().size(); ++var10) {
                     IsoObject var11 = (IsoObject)var9.getObjects().get(var10);
                     IsoGridSquare var12;
                     boolean var13;
                     IsoBarricade var14;
                     int var15;
                     int var16;
                     if (var11 instanceof IsoDoor) {
                        var12 = var9.getRoom() == null ? var9 : ((IsoDoor)var11).getOppositeSquare();
                        if (var12 != null && var12.getRoom() == null) {
                           var13 = var12 != var9;
                           var14 = IsoBarricade.AddBarricadeToObject((IsoDoor)var11, var13);
                           if (var14 != null) {
                              var15 = Rand.Next(1, 4);

                              for(var16 = 0; var16 < var15; ++var16) {
                                 var14.addPlank((IsoGameCharacter)null, (InventoryItem)null);
                              }

                              if (GameServer.bServer) {
                                 var14.transmitCompleteItemToClients();
                              }
                           }
                        }
                     }

                     if (var11 instanceof IsoWindow) {
                        var12 = var9.getRoom() == null ? var9 : ((IsoWindow)var11).getOppositeSquare();
                        if (var8 == 0 && var12 != null && var12.getRoom() == null) {
                           var13 = var12 != var9;
                           var14 = IsoBarricade.AddBarricadeToObject((IsoWindow)var11, var13);
                           if (var14 != null) {
                              var15 = Rand.Next(1, 4);

                              for(var16 = 0; var16 < var15; ++var16) {
                                 var14.addPlank((IsoGameCharacter)null, (InventoryItem)null);
                              }

                              if (GameServer.bServer) {
                                 var14.transmitCompleteItemToClients();
                              }
                           }
                        } else {
                           ((IsoWindow)var11).addSheet((IsoGameCharacter)null);
                           ((IsoWindow)var11).HasCurtains().ToggleDoor((IsoGameCharacter)null);
                        }
                     }

                     if (var11.getContainer() != null && var9.getRoom() != null && var9.getRoom().getBuilding().getDef() == var1 && Rand.Next(100) <= 70 && var9.getRoom().getName() != null && var3.rawget(var11.getContainer().getType()) != null) {
                        var11.getContainer().clear();
                        LuaManager.caller.pcall(LuaManager.thread, var2.rawget("fillContainerType"), var3, var11.getContainer(), "", null);
                        var11.getContainer().setExplored(true);
                     }
                  }
               }
            }
         }
      }

      var1.setAllExplored(true);
      var1.bAlarmed = false;
      this.addZombies(var1);
   }

   private void addZombies(BuildingDef var1) {
      for(int var2 = 0; var2 < var1.rooms.size(); ++var2) {
         RoomDef var3 = (RoomDef)var1.rooms.get(var2);
         if (Rand.Next(100) <= 80 && IsoWorld.getZombiesEnabled()) {
            byte var4 = 2;
            int var5 = var3.area;
            if (SandboxOptions.instance.Zombies.getValue() == 1) {
               var5 += 4;
            } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
               var5 += 2;
            } else if (SandboxOptions.instance.Zombies.getValue() == 4) {
               var5 -= 4;
            }

            if (var5 > 8) {
               var5 = 8;
            }

            if (var5 < var4) {
               var5 = var4 + 1;
            }

            VirtualZombieManager.instance.addZombiesToMap(Rand.Next(var4, var5), var3, false);
         }

         if (Rand.Next(100) <= 60) {
            RandomizedBuildingBase.createRandomDeadBody(var3);
         }
      }

   }
}
