package zombie.randomizedWorld.randomizedBuilding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBandPractice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBathroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBedroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSCorpsePsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSFootballNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHenDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHockeyPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHouseParty;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPokerNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPoliceAtHouse;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscape;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscapeWithPolice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSkeletonPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStagDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStudentNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSuicidePact;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSTinFoilHat;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombiesEating;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;

public final class RBBasic extends RandomizedBuildingBase {
   private final ArrayList specificProfessionDistribution = new ArrayList();
   private final Map specificProfessionRoomDistribution = new HashMap();
   private final ArrayList coldFood = new ArrayList();
   private final Map plankStash = new HashMap();
   private final ArrayList deadSurvivorsStory = new ArrayList();
   private int totalChanceRDS = 0;
   private static final HashMap rdsMap = new HashMap();
   private static final ArrayList uniqueRDSSpawned = new ArrayList();
   private ArrayList tablesDone = new ArrayList();
   private boolean doneTable = false;

   public void randomizeBuilding(BuildingDef var1) {
      this.tablesDone = new ArrayList();
      boolean var2 = Rand.Next(100) <= 20;
      ArrayList var3 = new ArrayList();
      String var4 = (String)this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size()));
      ItemPickerJava.ItemPickerRoom var5 = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get(var4);
      IsoCell var6 = IsoWorld.instance.CurrentCell;
      boolean var7 = Rand.NextBool(9);

      int var8;
      for(var8 = var1.x - 1; var8 < var1.x2 + 1; ++var8) {
         for(int var9 = var1.y - 1; var9 < var1.y2 + 1; ++var9) {
            for(int var10 = 0; var10 < 8; ++var10) {
               IsoGridSquare var11 = var6.getGridSquare(var8, var9, var10);
               if (var11 != null) {
                  if (var7 && var11.getFloor() != null && this.plankStash.containsKey(var11.getFloor().getSprite().getName())) {
                     IsoThumpable var12 = new IsoThumpable(var11.getCell(), var11, (String)this.plankStash.get(var11.getFloor().getSprite().getName()), false, (KahluaTable)null);
                     var12.setIsThumpable(false);
                     var12.container = new ItemContainer("plankstash", var11, var12);
                     var11.AddSpecialObject(var12);
                     var11.RecalcAllWithNeighbours(true);
                     var7 = false;
                  }

                  for(int var17 = 0; var17 < var11.getObjects().size(); ++var17) {
                     IsoObject var13 = (IsoObject)var11.getObjects().get(var17);
                     if (Rand.Next(100) <= 65 && var13 instanceof IsoDoor && !((IsoDoor)var13).isExteriorDoor((IsoGameCharacter)null)) {
                        ((IsoDoor)var13).ToggleDoorSilent();
                        ((IsoDoor)var13).syncIsoObject(true, (byte)1, (UdpConnection)null, (ByteBuffer)null);
                     }

                     if (var13 instanceof IsoWindow) {
                        IsoWindow var14 = (IsoWindow)var13;
                        if (Rand.NextBool(80)) {
                           var1.bAlarmed = false;
                           var14.ToggleWindow((IsoGameCharacter)null);
                        }

                        IsoCurtain var15 = var14.HasCurtains();
                        if (var15 != null && Rand.NextBool(15)) {
                           var15.ToggleDoorSilent();
                        }
                     }

                     if (var2 && Rand.Next(100) <= 70 && var13.getContainer() != null && var11.getRoom() != null && var11.getRoom().getName() != null && ((String)this.specificProfessionRoomDistribution.get(var4)).contains(var11.getRoom().getName()) && var5.Containers.containsKey(var13.getContainer().getType())) {
                        var13.getContainer().clear();
                        var3.add(var13.getContainer());
                        var13.getContainer().setExplored(true);
                     }

                     if (Rand.Next(100) < 15 && var13.getContainer() != null && var13.getContainer().getType().equals("stove")) {
                        InventoryItem var18 = var13.getContainer().AddItem((String)this.coldFood.get(Rand.Next(0, this.coldFood.size())));
                        var18.setCooked(true);
                        var18.setAutoAge();
                     }

                     if (!this.tablesDone.contains(var13) && var13.getProperties().isTable() && var13.getProperties().getSurface() == 34 && var13.getContainer() == null && !this.doneTable) {
                        this.checkForTableSpawn(var1, var13);
                     }
                  }
               }
            }
         }
      }

      for(var8 = 0; var8 < var3.size(); ++var8) {
         ItemContainer var16 = (ItemContainer)var3.get(var8);
         ItemPickerJava.fillContainerType(var5, var16, "", (IsoGameCharacter)null);
         ItemPickerJava.updateOverlaySprite(var16.getParent());
         if (GameServer.bServer) {
            GameServer.sendItemsInContainer(var16.getParent(), var16);
         }
      }

      if (!var2 && Rand.Next(100) < 25) {
         this.addRandomDeadSurvivorStory(var1);
         var1.setAllExplored(true);
         var1.bAlarmed = false;
      }

      this.doneTable = false;
   }

   private void checkForTableSpawn(BuildingDef var1, IsoObject var2) {
      IsoObject var3 = null;
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      var3 = this.checkForTable(var4.getGridSquare((double)((int)var2.getX() - 1), (double)((int)var2.getY()), (double)var2.getZ()), var2);
      if (var3 == null) {
         var3 = this.checkForTable(var4.getGridSquare((double)((int)var2.getX() + 1), (double)((int)var2.getY()), (double)var2.getZ()), var2);
      }

      if (var3 == null) {
         var3 = this.checkForTable(var4.getGridSquare((double)((int)var2.getX()), (double)((int)var2.getY() - 1), (double)var2.getZ()), var2);
      }

      if (var3 == null) {
         var3 = this.checkForTable(var4.getGridSquare((double)((int)var2.getX()), (double)((int)var2.getY() + 1), (double)var2.getZ()), var2);
      }

      if (var3 != null && Rand.NextBool(8)) {
         this.tablesDone.add(var2);
         this.tablesDone.add(var3);
         RBTableStory var5 = new RBTableStory();
         var5.table1 = var2;
         var5.table2 = var3;
         var5.randomizeBuilding(var1);
         var5 = null;
         this.doneTable = true;
      }

   }

   private IsoObject checkForTable(IsoGridSquare var1, IsoObject var2) {
      if (!this.tablesDone.contains(var2) && var1 != null) {
         for(int var3 = 0; var3 < var1.getObjects().size(); ++var3) {
            IsoObject var4 = (IsoObject)var1.getObjects().get(var3);
            if (!this.tablesDone.contains(var4) && var4.getProperties().isTable() && var4.getProperties().getSurface() == 34 && var4.getContainer() == null && var4 != var2) {
               return var4;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void doProfessionStory(BuildingDef var1, String var2) {
      this.spawnItemsInContainers(var1, var2, 70);
   }

   private void addRandomDeadSurvivorStory(BuildingDef var1) {
      this.initRDSMap(var1);
      int var2 = Rand.Next(this.totalChanceRDS);
      Iterator var3 = rdsMap.keySet().iterator();
      int var4 = 0;

      while(var3.hasNext()) {
         RandomizedDeadSurvivorBase var5 = (RandomizedDeadSurvivorBase)var3.next();
         var4 += (Integer)rdsMap.get(var5);
         if (var2 < var4) {
            var5.randomizeDeadSurvivor(var1);
            if (var5.isUnique()) {
               getUniqueRDSSpawned().add(var5.getName());
            }
            break;
         }
      }

   }

   private void initRDSMap(BuildingDef var1) {
      this.totalChanceRDS = 0;
      rdsMap.clear();

      for(int var2 = 0; var2 < this.deadSurvivorsStory.size(); ++var2) {
         RandomizedDeadSurvivorBase var3 = (RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(var2);
         if (var3.isValid(var1, false) && var3.isTimeValid(false) && (var3.isUnique() && !getUniqueRDSSpawned().contains(var3.getName()) || !var3.isUnique())) {
            this.totalChanceRDS += ((RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(var2)).getChance();
            rdsMap.put((RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(var2), ((RandomizedDeadSurvivorBase)this.deadSurvivorsStory.get(var2)).getChance());
         }
      }

   }

   public void doRandomDeadSurvivorStory(BuildingDef var1, RandomizedDeadSurvivorBase var2) {
      var2.randomizeDeadSurvivor(var1);
   }

   public RBBasic() {
      this.name = "RBBasic";
      this.deadSurvivorsStory.add(new RDSBleach());
      this.deadSurvivorsStory.add(new RDSGunslinger());
      this.deadSurvivorsStory.add(new RDSGunmanInBathroom());
      this.deadSurvivorsStory.add(new RDSZombieLockedBathroom());
      this.deadSurvivorsStory.add(new RDSDeadDrunk());
      this.deadSurvivorsStory.add(new RDSSpecificProfession());
      this.deadSurvivorsStory.add(new RDSZombiesEating());
      this.deadSurvivorsStory.add(new RDSBandPractice());
      this.deadSurvivorsStory.add(new RDSBathroomZed());
      this.deadSurvivorsStory.add(new RDSBedroomZed());
      this.deadSurvivorsStory.add(new RDSFootballNight());
      this.deadSurvivorsStory.add(new RDSHenDo());
      this.deadSurvivorsStory.add(new RDSStagDo());
      this.deadSurvivorsStory.add(new RDSStudentNight());
      this.deadSurvivorsStory.add(new RDSPokerNight());
      this.deadSurvivorsStory.add(new RDSSuicidePact());
      this.deadSurvivorsStory.add(new RDSPrisonEscape());
      this.deadSurvivorsStory.add(new RDSPrisonEscapeWithPolice());
      this.deadSurvivorsStory.add(new RDSSkeletonPsycho());
      this.deadSurvivorsStory.add(new RDSCorpsePsycho());
      this.deadSurvivorsStory.add(new RDSPoliceAtHouse());
      this.deadSurvivorsStory.add(new RDSHouseParty());
      this.deadSurvivorsStory.add(new RDSTinFoilHat());
      this.deadSurvivorsStory.add(new RDSHockeyPsycho());
      this.specificProfessionDistribution.add("Carpenter");
      this.specificProfessionDistribution.add("Electrician");
      this.specificProfessionDistribution.add("Farmer");
      this.specificProfessionDistribution.add("Nurse");
      this.specificProfessionRoomDistribution.put("Carpenter", "kitchen");
      this.specificProfessionRoomDistribution.put("Electrician", "kitchen");
      this.specificProfessionRoomDistribution.put("Farmer", "kitchen");
      this.specificProfessionRoomDistribution.put("Nurse", "kitchen");
      this.specificProfessionRoomDistribution.put("Nurse", "bathroom");
      this.coldFood.add("Base.Chicken");
      this.coldFood.add("Base.Steak");
      this.coldFood.add("Base.PorkChop");
      this.coldFood.add("Base.MuttonChop");
      this.coldFood.add("Base.MeatPatty");
      this.coldFood.add("Base.FishFillet");
      this.coldFood.add("Base.Salmon");
      this.plankStash.put("floors_interior_tilesandwood_01_40", "floors_interior_tilesandwood_01_56");
      this.plankStash.put("floors_interior_tilesandwood_01_41", "floors_interior_tilesandwood_01_57");
      this.plankStash.put("floors_interior_tilesandwood_01_42", "floors_interior_tilesandwood_01_58");
      this.plankStash.put("floors_interior_tilesandwood_01_43", "floors_interior_tilesandwood_01_59");
      this.plankStash.put("floors_interior_tilesandwood_01_44", "floors_interior_tilesandwood_01_60");
      this.plankStash.put("floors_interior_tilesandwood_01_45", "floors_interior_tilesandwood_01_61");
      this.plankStash.put("floors_interior_tilesandwood_01_46", "floors_interior_tilesandwood_01_62");
      this.plankStash.put("floors_interior_tilesandwood_01_47", "floors_interior_tilesandwood_01_63");
      this.plankStash.put("floors_interior_tilesandwood_01_52", "floors_interior_tilesandwood_01_68");
   }

   public ArrayList getSurvivorStories() {
      return this.deadSurvivorsStory;
   }

   public ArrayList getSurvivorProfession() {
      return this.specificProfessionDistribution;
   }

   public static ArrayList getUniqueRDSSpawned() {
      return uniqueRDSSpawned;
   }
}
