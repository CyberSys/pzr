package zombie.randomizedWorld;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class RandomizedBuildingBase {
   private int minimumDays = 0;
   private int minimumRooms = 0;
   private static ArrayList squareChoices = new ArrayList();

   public void randomizeBuilding(BuildingDef var1) {
      var1.bAlarmed = false;
   }

   public void init() {
   }

   public boolean isValid(BuildingDef var1) {
      if (GameClient.bClient) {
         return false;
      } else if (var1.isAllExplored()) {
         return false;
      } else {
         if (!GameServer.bServer) {
            if (IsoPlayer.instance.getSquare() != null && IsoPlayer.instance.getSquare().getBuilding() != null && IsoPlayer.instance.getSquare().getBuilding().def == var1) {
               this.customizeStartingHouse(IsoPlayer.instance.getSquare().getBuilding().def);
               return false;
            }
         } else {
            for(int var2 = 0; var2 < GameServer.Players.size(); ++var2) {
               IsoPlayer var3 = (IsoPlayer)GameServer.Players.get(var2);
               if (var3.getSquare() != null && var3.getSquare().getBuilding() != null && var3.getSquare().getBuilding().def == var1) {
                  return false;
               }
            }
         }

         boolean var7 = false;
         boolean var8 = false;
         boolean var4 = false;

         for(int var5 = 0; var5 < var1.rooms.size(); ++var5) {
            RoomDef var6 = (RoomDef)var1.rooms.get(var5);
            if ("bedroom".equals(var6.name)) {
               var7 = true;
            }

            if ("kitchen".equals(var6.name)) {
               var8 = true;
            }

            if ("bathroom".equals(var6.name)) {
               var4 = true;
            }
         }

         return var7 && var4 && var8;
      }
   }

   private void customizeStartingHouse(BuildingDef var1) {
      if (!IsoWorld.instance.getGameMode().equals("The First Week") && !IsoWorld.instance.getGameMode().equals("Initial Infection")) {
         InventoryItem var2 = InventoryItemFactory.CreateItem("Base.Plank");
         IsoGridSquare var3 = IsoPlayer.instance.getCurrentSquare();

         for(int var4 = var1.x - 1; var4 < var1.x2 + 1; ++var4) {
            for(int var5 = var1.y - 1; var5 < var1.y2 + 1; ++var5) {
               for(int var6 = 0; var6 < 8; ++var6) {
                  IsoGridSquare var7 = var3.getCell().getGridSquare(var4, var5, var6);
                  if (var7 != null) {
                     for(int var8 = 0; var8 < var7.getObjects().size(); ++var8) {
                        IsoObject var9 = (IsoObject)var7.getObjects().get(var8);
                        if (var9 instanceof IsoWindow) {
                           if (var6 == 0) {
                              IsoGridSquare var10 = var7.getRoom() == null ? var7 : ((IsoWindow)var9).getOppositeSquare();
                              if (var10 != null && var10.getRoom() == null && Rand.Next(100) <= 20) {
                                 boolean var11 = var10 != var7;
                                 IsoBarricade var12 = IsoBarricade.AddBarricadeToObject((IsoWindow)var9, var11);
                                 if (var12 != null) {
                                    var12.addPlank((IsoGameCharacter)null, (InventoryItem)null);
                                    if (GameServer.bServer) {
                                       var12.transmitCompleteItemToClients();
                                    }
                                 }
                              }

                              ((IsoWindow)var9).addSheet((IsoGameCharacter)null);
                              ((IsoWindow)var9).HasCurtains().ToggleDoor((IsoGameCharacter)null);
                           } else {
                              ((IsoWindow)var9).addSheet((IsoGameCharacter)null);
                              ((IsoWindow)var9).HasCurtains().ToggleDoor((IsoGameCharacter)null);
                           }
                        }

                        if (var9 instanceof IsoLightSwitch) {
                           ((IsoLightSwitch)var9).setActive(true);
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public int getMinimumDays() {
      return this.minimumDays;
   }

   public void setMinimumDays(int var1) {
      this.minimumDays = var1;
   }

   public int getMinimumRooms() {
      return this.minimumRooms;
   }

   public void setMinimumRooms(int var1) {
      this.minimumRooms = var1;
   }

   public static IsoDeadBody createRandomDeadBody(RoomDef var0) {
      squareChoices.clear();

      for(int var1 = 0; var1 < var0.rects.size(); ++var1) {
         RoomDef.RoomRect var2 = (RoomDef.RoomRect)var0.rects.get(var1);

         for(int var3 = var2.getX(); var3 < var2.getX2(); ++var3) {
            for(int var4 = var2.getY(); var4 < var2.getY2(); ++var4) {
               IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var0.getZ());
               if (var5 != null && var5.isFree(false)) {
                  squareChoices.add(var5);
               }
            }
         }
      }

      if (squareChoices.isEmpty()) {
         return null;
      } else {
         IsoGridSquare var6 = (IsoGridSquare)squareChoices.get(Rand.Next(squareChoices.size()));
         return createRandomDeadBody(var6.getX(), var6.getY(), var6.getZ());
      }
   }

   public static IsoDeadBody createRandomDeadBody(int var0, int var1, int var2) {
      KahluaTable var3 = (KahluaTable)LuaManager.env.rawget("ItemPicker");
      KahluaTable var4 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget("all");
      IsoGameCharacter var5 = new IsoGameCharacter(IsoWorld.instance.getCell(), (float)var0, (float)var1, (float)var2);
      var5.setDescriptor(SurvivorFactory.CreateSurvivor());
      var5.setFemale(var5.getDescriptor().isFemale());
      var5.setDir(IsoDirections.fromIndex(Rand.Next(8)));
      var5.initSpritePartsEmpty();
      var5.Dressup(var5.getDescriptor());

      for(int var6 = 0; var6 < 6; ++var6) {
         var5.splatBlood(Rand.Next(1, 4), 0.3F);
      }

      IsoDeadBody var8 = new IsoDeadBody(var5, true);
      KahluaTable var7 = null;
      if (var5.isFemale()) {
         var7 = (KahluaTable)var4.rawget("inventoryfemale");
      } else {
         var7 = (KahluaTable)var4.rawget("inventorymale");
      }

      LuaManager.caller.pcall(LuaManager.thread, var3.rawget("fillContainerType"), var7, var8.getContainer(), "", null);
      return var8;
   }

   public static void ChunkLoaded(IsoBuilding var0) {
      if (!GameClient.bClient && var0.def != null && !var0.def.seen && var0.def.isFullyStreamedIn()) {
         if (GameServer.bServer && GameServer.Players.isEmpty()) {
            return;
         }

         for(int var1 = 0; var1 < var0.Rooms.size(); ++var1) {
            if (((IsoRoom)var0.Rooms.get(var1)).def.bExplored) {
               return;
            }
         }

         var0.def.seen = true;
         if (GameServer.bServer && GameServer.isSpawnBuilding(var0.getDef())) {
            return;
         }

         RandomizedBuildingBase var4 = IsoWorld.instance.getRBBasic();

         try {
            int var2 = 10;
            switch(SandboxOptions.instance.SurvivorHouseChance.getValue()) {
            case 1:
               return;
            case 2:
               var2 -= 5;
            case 3:
            default:
               break;
            case 4:
               var2 += 5;
               break;
            case 5:
               var2 += 10;
               break;
            case 6:
               var2 += 20;
            }

            if (Rand.Next(100) <= var2) {
               var4 = (RandomizedBuildingBase)IsoWorld.instance.getRandomizedBuildingList().get(Rand.Next(0, IsoWorld.instance.getRandomizedBuildingList().size()));
            }

            if (var4.isValid(var0.def)) {
               var4.randomizeBuilding(var0.def);
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }
}
