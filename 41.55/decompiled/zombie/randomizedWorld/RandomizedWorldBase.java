package zombie.randomizedWorld;

import java.util.ArrayList;
import java.util.List;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.Lua.MapObjects;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclesDB2;

public class RandomizedWorldBase {
   protected int minimumDays = 0;
   protected int maximumDays = 0;
   protected int minimumRooms = 0;
   protected boolean unique = false;
   private boolean rvsVehicleKeyAddedToZombie = false;
   protected String name = null;
   protected String debugLine = "";

   public BaseVehicle addVehicle(IsoMetaGrid.Zone var1, IsoGridSquare var2, IsoChunk var3, String var4, String var5, IsoDirections var6) {
      return this.addVehicle(var1, var2, var3, var4, var5, (Integer)null, var6, (String)null);
   }

   public BaseVehicle addVehicleFlipped(IsoMetaGrid.Zone var1, IsoGridSquare var2, IsoChunk var3, String var4, String var5, Integer var6, IsoDirections var7, String var8) {
      if (StringUtils.isNullOrEmpty(var4)) {
         var4 = "junkyard";
      }

      if (var7 == null) {
         var7 = IsoDirections.getRandom();
      }

      BaseVehicle var9 = new BaseVehicle(IsoWorld.instance.CurrentCell);
      var9.specificDistributionId = var8;
      VehicleType var10 = VehicleType.getRandomVehicleType(var4, false);
      if (!StringUtils.isNullOrEmpty(var5)) {
         var9.setScriptName(var5);
         var9.setScript();
         if (var6 != null) {
            var9.setSkinIndex(var6);
         }
      } else {
         if (var10 == null) {
            return null;
         }

         var9.setVehicleType(var10.name);
         if (!var3.RandomizeModel(var9, var1, var4, var10)) {
            return null;
         }
      }

      if (var10.isSpecialCar) {
         var9.setDoColor(false);
      }

      var9.setDir(var7);
      float var11 = 3.0F;
      var9.savedRot.setAngleAxis(var11, 1.0F, 0.0F, 0.0F);
      var9.jniTransform.setRotation(var9.savedRot);
      var9.setX((float)var2.x);
      var9.setY((float)var2.y);
      var9.setZ((float)var2.z);
      if (IsoChunk.doSpawnedVehiclesInInvalidPosition(var9)) {
         var9.setSquare(var2);
         var2.chunk.vehicles.add(var9);
         var9.chunk = var2.chunk;
         var9.addToWorld();
         VehiclesDB2.instance.addVehicle(var9);
      }

      var9.setGeneralPartCondition(0.2F, 70.0F);
      var9.rust = Rand.Next(100) < 70 ? 1.0F : 0.0F;
      return var9;
   }

   public BaseVehicle addVehicle(IsoMetaGrid.Zone var1, IsoGridSquare var2, IsoChunk var3, String var4, String var5, Integer var6, IsoDirections var7, String var8) {
      if (StringUtils.isNullOrEmpty(var4)) {
         var4 = "junkyard";
      }

      if (var2 == null) {
         return null;
      } else {
         if (var7 == null) {
            var7 = IsoDirections.getRandom();
         }

         BaseVehicle var9 = new BaseVehicle(IsoWorld.instance.CurrentCell);
         var9.specificDistributionId = var8;
         VehicleType var10 = VehicleType.getRandomVehicleType(var4, false);
         if (!StringUtils.isNullOrEmpty(var5)) {
            var9.setScriptName(var5);
            var9.setScript();
            if (var6 != null) {
               var9.setSkinIndex(var6);
            }
         } else {
            if (var10 == null) {
               return null;
            }

            var9.setVehicleType(var10.name);
            if (!var3.RandomizeModel(var9, var1, var4, var10)) {
               return null;
            }
         }

         if (var10.isSpecialCar) {
            var9.setDoColor(false);
         }

         var9.setDir(var7);

         float var11;
         for(var11 = var7.toAngle() + 3.1415927F + Rand.Next(-0.5F, 0.5F); (double)var11 > 6.283185307179586D; var11 = (float)((double)var11 - 6.283185307179586D)) {
         }

         var9.savedRot.setAngleAxis(var11, 0.0F, 1.0F, 0.0F);
         var9.jniTransform.setRotation(var9.savedRot);
         var9.setX((float)var2.x);
         var9.setY((float)var2.y);
         var9.setZ((float)var2.z);
         if (IsoChunk.doSpawnedVehiclesInInvalidPosition(var9)) {
            var9.setSquare(var2);
            var2.chunk.vehicles.add(var9);
            var9.chunk = var2.chunk;
            var9.addToWorld();
            VehiclesDB2.instance.addVehicle(var9);
         }

         var9.setGeneralPartCondition(0.2F, 70.0F);
         var9.rust = Rand.Next(100) < 70 ? 1.0F : 0.0F;
         return var9;
      }
   }

   public static void removeAllVehiclesOnZone(IsoMetaGrid.Zone var0) {
      for(int var1 = var0.x; var1 < var0.x + var0.w; ++var1) {
         for(int var2 = var0.y; var2 < var0.y + var0.h; ++var2) {
            IsoGridSquare var3 = IsoCell.getInstance().getGridSquare(var1, var2, 0);
            if (var3 != null) {
               BaseVehicle var4 = var3.getVehicleContainer();
               if (var4 != null) {
                  var4.permanentlyRemove();
               }
            }
         }
      }

   }

   public ArrayList addZombiesOnVehicle(int var1, String var2, Integer var3, BaseVehicle var4) {
      ArrayList var5 = new ArrayList();
      if (var4 == null) {
         return var5;
      } else {
         int var6 = 100;
         IsoGridSquare var7 = var4.getSquare();
         if (var7 != null && var7.getCell() != null) {
            for(; var1 > 0; var6 = 100) {
               while(var6 > 0) {
                  IsoGridSquare var8 = var7.getCell().getGridSquare(Rand.Next(var7.x - 4, var7.x + 4), Rand.Next(var7.y - 4, var7.y + 4), var7.z);
                  if (var8 != null && var8.getVehicleContainer() == null) {
                     --var1;
                     var5.addAll(this.addZombiesOnSquare(1, var2, var3, var8));
                     break;
                  }

                  --var6;
               }
            }

            if (!this.rvsVehicleKeyAddedToZombie && !var5.isEmpty()) {
               IsoZombie var9 = (IsoZombie)var5.get(Rand.Next(0, var5.size()));
               var9.addItemToSpawnAtDeath(var4.createVehicleKey());
               this.rvsVehicleKeyAddedToZombie = true;
            }

            return var5;
         } else {
            return var5;
         }
      }
   }

   public static IsoDeadBody createRandomDeadBody(RoomDef var0, int var1) {
      if (IsoWorld.getZombiesDisabled()) {
         return null;
      } else if (var0 == null) {
         return null;
      } else {
         IsoGridSquare var2 = getRandomSquareForCorpse(var0);
         return var2 == null ? null : createRandomDeadBody(var2, (IsoDirections)null, var1, 0, (String)null);
      }
   }

   public ArrayList addZombiesOnSquare(int var1, String var2, Integer var3, IsoGridSquare var4) {
      ArrayList var5 = new ArrayList();
      if (IsoWorld.getZombiesDisabled()) {
         return var5;
      } else if (var4 == null) {
         return var5;
      } else {
         for(int var6 = 0; var6 < var1; ++var6) {
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(var4);
            IsoZombie var7 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
            if (var7 != null) {
               if (var3 != null) {
                  var7.setFemaleEtc(Rand.Next(100) < var3);
               }

               if (var2 != null) {
                  var7.dressInPersistentOutfit(var2);
                  var7.bDressInRandomOutfit = false;
               } else {
                  var7.dressInRandomOutfit();
                  var7.bDressInRandomOutfit = false;
               }

               var5.add(var7);
            }
         }

         ZombieSpawnRecorder.instance.record(var5, this.getClass().getSimpleName());
         return var5;
      }
   }

   public static IsoDeadBody createRandomDeadBody(int var0, int var1, int var2, IsoDirections var3, int var4) {
      return createRandomDeadBody(var0, var1, var2, var3, var4, 0);
   }

   public static IsoDeadBody createRandomDeadBody(int var0, int var1, int var2, IsoDirections var3, int var4, int var5) {
      IsoGridSquare var6 = IsoCell.getInstance().getGridSquare(var0, var1, var2);
      return createRandomDeadBody(var6, var3, var4, var5, (String)null);
   }

   public static IsoDeadBody createRandomDeadBody(IsoGridSquare var0, IsoDirections var1, int var2, int var3, String var4) {
      if (IsoWorld.getZombiesDisabled()) {
         return null;
      } else {
         boolean var5 = var1 == null;
         if (var1 == null) {
            var1 = IsoDirections.getRandom();
         }

         if (var0 == null) {
            return null;
         } else {
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(var0);
            IsoZombie var6 = VirtualZombieManager.instance.createRealZombieAlways(var1.index(), false);
            if (var6 == null) {
               return null;
            } else {
               if (var4 != null) {
                  var6.dressInPersistentOutfit(var4);
                  var6.bDressInRandomOutfit = false;
               } else {
                  var6.dressInRandomOutfit();
               }

               if (Rand.Next(100) < var3) {
                  var6.setFakeDead(true);
                  var6.bCrawling = true;
                  var6.setCanWalk(false);
                  var6.setCrawlerType(1);
               } else {
                  var6.setFakeDead(false);
                  var6.setHealth(0.0F);
               }

               var6.upKillCount = false;
               var6.getHumanVisual().zombieRotStage = ((HumanVisual)var6.getVisual()).pickRandomZombieRotStage();

               for(int var7 = 0; var7 < var2; ++var7) {
                  var6.addBlood((BloodBodyPartType)null, false, true, true);
               }

               var6.DoZombieInventory();
               if (var5) {
                  alignCorpseToSquare(var6, var0);
               }

               IsoDeadBody var8 = new IsoDeadBody(var6, true);
               return var8;
            }
         }
      }
   }

   public void addTraitOfBlood(IsoDirections var1, int var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < var2; ++var6) {
         float var7 = 0.0F;
         float var8 = 0.0F;
         if (var1 == IsoDirections.S) {
            var8 = Rand.Next(-2.0F, 0.5F);
         }

         if (var1 == IsoDirections.N) {
            var8 = Rand.Next(-0.5F, 2.0F);
         }

         if (var1 == IsoDirections.E) {
            var7 = Rand.Next(-2.0F, 0.5F);
         }

         if (var1 == IsoDirections.W) {
            var7 = Rand.Next(-0.5F, 2.0F);
         }

         new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), (float)var3, (float)var4, (float)var5 + 0.2F, var7, var8);
      }

   }

   public void addBloodSplat(IsoGridSquare var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var1.getChunk().addBloodSplat((float)var1.x + Rand.Next(-0.5F, 0.5F), (float)var1.y + Rand.Next(-0.5F, 0.5F), (float)var1.z, Rand.Next(8));
      }

   }

   public void setAttachedItem(IsoZombie var1, String var2, String var3, String var4) {
      InventoryItem var5 = InventoryItemFactory.CreateItem(var3);
      if (var5 != null) {
         var5.setCondition(Rand.Next(Math.max(2, var5.getConditionMax() - 5), var5.getConditionMax()));
         if (var5 instanceof HandWeapon) {
            ((HandWeapon)var5).randomizeBullets();
         }

         var1.setAttachedItem(var2, var5);
         if (!StringUtils.isNullOrEmpty(var4)) {
            var1.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(var4));
         }

      }
   }

   public static IsoGameCharacter createRandomZombie(RoomDef var0) {
      IsoGridSquare var1 = getRandomSpawnSquare(var0);
      return createRandomZombie(var1.getX(), var1.getY(), var1.getZ());
   }

   public static IsoGameCharacter createRandomZombieForCorpse(RoomDef var0) {
      IsoGridSquare var1 = getRandomSquareForCorpse(var0);
      if (var1 == null) {
         return null;
      } else {
         IsoGameCharacter var2 = createRandomZombie(var1.getX(), var1.getY(), var1.getZ());
         if (var2 != null) {
            alignCorpseToSquare(var2, var1);
         }

         return var2;
      }
   }

   public static IsoDeadBody createBodyFromZombie(IsoGameCharacter var0) {
      if (IsoWorld.getZombiesDisabled()) {
         return null;
      } else {
         for(int var1 = 0; var1 < 6; ++var1) {
            var0.splatBlood(Rand.Next(1, 4), 0.3F);
         }

         IsoDeadBody var2 = new IsoDeadBody(var0, true);
         return var2;
      }
   }

   public static IsoGameCharacter createRandomZombie(int var0, int var1, int var2) {
      RandomizedBuildingBase.HumanCorpse var3 = new RandomizedBuildingBase.HumanCorpse(IsoWorld.instance.getCell(), (float)var0, (float)var1, (float)var2);
      var3.setDescriptor(SurvivorFactory.CreateSurvivor());
      var3.setFemale(var3.getDescriptor().isFemale());
      var3.setDir(IsoDirections.fromIndex(Rand.Next(8)));
      var3.initWornItems("Human");
      var3.initAttachedItems("Human");
      Outfit var4 = var3.getRandomDefaultOutfit();
      var3.dressInNamedOutfit(var4.m_Name);
      var3.initSpritePartsEmpty();
      var3.Dressup(var3.getDescriptor());
      return var3;
   }

   private static boolean isSquareClear(IsoGridSquare var0) {
      return var0 != null && canSpawnAt(var0) && !var0.HasStairs() && !var0.HasTree() && !var0.getProperties().Is(IsoFlagType.bed) && !var0.getProperties().Is(IsoFlagType.waterPiped);
   }

   private static boolean isSquareClear(IsoGridSquare var0, IsoDirections var1) {
      IsoGridSquare var2 = var0.getAdjacentSquare(var1);
      return isSquareClear(var2) && !var0.isSomethingTo(var2) && var0.getRoomID() == var2.getRoomID();
   }

   public static boolean is1x2AreaClear(IsoGridSquare var0) {
      return isSquareClear(var0) && isSquareClear(var0, IsoDirections.N);
   }

   public static boolean is2x1AreaClear(IsoGridSquare var0) {
      return isSquareClear(var0) && isSquareClear(var0, IsoDirections.W);
   }

   public static boolean is2x1or1x2AreaClear(IsoGridSquare var0) {
      return isSquareClear(var0) && (isSquareClear(var0, IsoDirections.W) || isSquareClear(var0, IsoDirections.N));
   }

   public static boolean is2x2AreaClear(IsoGridSquare var0) {
      return isSquareClear(var0) && isSquareClear(var0, IsoDirections.N) && isSquareClear(var0, IsoDirections.W) && isSquareClear(var0, IsoDirections.NW);
   }

   public static void alignCorpseToSquare(IsoGameCharacter var0, IsoGridSquare var1) {
      int var2 = var1.x;
      int var3 = var1.y;
      IsoDirections var4 = IsoDirections.fromIndex(Rand.Next(8));
      boolean var5 = is1x2AreaClear(var1);
      boolean var6 = is2x1AreaClear(var1);
      if (var5 && var6) {
         var5 = Rand.Next(2) == 0;
         var6 = !var5;
      }

      if (is2x2AreaClear(var1)) {
         var0.setX((float)var2);
         var0.setY((float)var3);
      } else if (var5) {
         var0.setX((float)var2 + 0.5F);
         var0.setY((float)var3);
         var4 = Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S;
      } else if (var6) {
         var0.setX((float)var2);
         var0.setY((float)var3 + 0.5F);
         var4 = Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E;
      } else if (is1x2AreaClear(var1.getAdjacentSquare(IsoDirections.S))) {
         var0.setX((float)var2 + 0.5F);
         var0.setY((float)var3 + 0.99F);
         var4 = Rand.Next(2) == 0 ? IsoDirections.N : IsoDirections.S;
      } else if (is2x1AreaClear(var1.getAdjacentSquare(IsoDirections.E))) {
         var0.setX((float)var2 + 0.99F);
         var0.setY((float)var3 + 0.5F);
         var4 = Rand.Next(2) == 0 ? IsoDirections.W : IsoDirections.E;
      }

      var0.setDir(var4);
      var0.lx = var0.nx = var0.x;
      var0.ly = var0.ny = var0.y;
      var0.setScriptnx(var0.x);
      var0.setScriptny(var0.y);
   }

   public RoomDef getRandomRoom(BuildingDef var1, int var2) {
      RoomDef var3 = (RoomDef)var1.getRooms().get(Rand.Next(0, var1.getRooms().size()));
      if (var2 > 0 && var3.area >= var2) {
         return var3;
      } else {
         int var4 = 0;

         do {
            if (var4 > 20) {
               return var3;
            }

            ++var4;
            var3 = (RoomDef)var1.getRooms().get(Rand.Next(0, var1.getRooms().size()));
         } while(var3.area < var2);

         return var3;
      }
   }

   public RoomDef getRoom(BuildingDef var1, String var2) {
      for(int var3 = 0; var3 < var1.rooms.size(); ++var3) {
         RoomDef var4 = (RoomDef)var1.rooms.get(var3);
         if (var4.getName().equalsIgnoreCase(var2)) {
            return var4;
         }
      }

      return null;
   }

   public RoomDef getLivingRoomOrKitchen(BuildingDef var1) {
      RoomDef var2 = this.getRoom(var1, "livingroom");
      if (var2 == null) {
         var2 = this.getRoom(var1, "kitchen");
      }

      return var2;
   }

   private static boolean canSpawnAt(IsoGridSquare var0) {
      if (var0 == null) {
         return false;
      } else {
         return var0.HasStairs() ? false : VirtualZombieManager.instance.canSpawnAt(var0.x, var0.y, var0.z);
      }
   }

   public static IsoGridSquare getRandomSpawnSquare(RoomDef var0) {
      return var0 == null ? null : var0.getRandomSquare(RandomizedWorldBase::canSpawnAt);
   }

   public static IsoGridSquare getRandomSquareForCorpse(RoomDef var0) {
      IsoGridSquare var1 = var0.getRandomSquare(RandomizedWorldBase::is2x2AreaClear);
      IsoGridSquare var2 = var0.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
      if (var1 == null || var2 != null && Rand.Next(4) == 0) {
         var1 = var2;
      }

      return var1;
   }

   public BaseVehicle spawnCarOnNearestNav(String var1, BuildingDef var2) {
      IsoGridSquare var3 = null;
      int var4 = (var2.x + var2.x2) / 2;
      int var5 = (var2.y + var2.y2) / 2;

      int var6;
      IsoGridSquare var7;
      for(var6 = var4; var6 < var4 + 20; ++var6) {
         var7 = IsoCell.getInstance().getGridSquare(var6, var5, 0);
         if (var7 != null && "Nav".equals(var7.getZoneType())) {
            var3 = var7;
            break;
         }
      }

      if (var3 != null) {
         return this.spawnCar(var1, var3);
      } else {
         for(var6 = var4; var6 > var4 - 20; --var6) {
            var7 = IsoCell.getInstance().getGridSquare(var6, var5, 0);
            if (var7 != null && "Nav".equals(var7.getZoneType())) {
               var3 = var7;
               break;
            }
         }

         if (var3 != null) {
            return this.spawnCar(var1, var3);
         } else {
            for(var6 = var5; var6 < var5 + 20; ++var6) {
               var7 = IsoCell.getInstance().getGridSquare(var4, var6, 0);
               if (var7 != null && "Nav".equals(var7.getZoneType())) {
                  var3 = var7;
                  break;
               }
            }

            if (var3 != null) {
               return this.spawnCar(var1, var3);
            } else {
               for(var6 = var5; var6 > var5 - 20; --var6) {
                  var7 = IsoCell.getInstance().getGridSquare(var4, var6, 0);
                  if (var7 != null && "Nav".equals(var7.getZoneType())) {
                     var3 = var7;
                     break;
                  }
               }

               return var3 != null ? this.spawnCar(var1, var3) : null;
            }
         }
      }
   }

   private BaseVehicle spawnCar(String var1, IsoGridSquare var2) {
      BaseVehicle var3 = new BaseVehicle(IsoWorld.instance.CurrentCell);
      var3.setScriptName(var1);
      var3.setX((float)var2.x + 0.5F);
      var3.setY((float)var2.y + 0.5F);
      var3.setZ(0.0F);
      var3.savedRot.setAngleAxis(Rand.Next(0.0F, 6.2831855F), 0.0F, 1.0F, 0.0F);
      var3.jniTransform.setRotation(var3.savedRot);
      if (IsoChunk.doSpawnedVehiclesInInvalidPosition(var3)) {
         var3.keySpawned = 1;
         var3.setSquare(var2);
         var3.square.chunk.vehicles.add(var3);
         var3.chunk = var3.square.chunk;
         var3.addToWorld();
         VehiclesDB2.instance.addVehicle(var3);
      }

      var3.setGeneralPartCondition(0.3F, 70.0F);
      return var3;
   }

   public InventoryItem addItemOnGround(IsoGridSquare var1, String var2) {
      return var1 != null && !StringUtils.isNullOrWhitespace(var2) ? var1.AddWorldInventoryItem(var2, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F) : null;
   }

   public InventoryItem addItemOnGround(IsoGridSquare var1, InventoryItem var2) {
      return var1 != null && var2 != null ? var1.AddWorldInventoryItem(var2, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F) : null;
   }

   public void addRandomItemsOnGround(RoomDef var1, String var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         IsoGridSquare var5 = getRandomSpawnSquare(var1);
         this.addItemOnGround(var5, var2);
      }

   }

   public void addRandomItemsOnGround(RoomDef var1, ArrayList var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         IsoGridSquare var5 = getRandomSpawnSquare(var1);
         this.addRandomItemOnGround(var5, var2);
      }

   }

   public InventoryItem addRandomItemOnGround(IsoGridSquare var1, ArrayList var2) {
      if (var1 != null && !var2.isEmpty()) {
         String var3 = (String)PZArrayUtil.pickRandom((List)var2);
         return this.addItemOnGround(var1, var3);
      } else {
         return null;
      }
   }

   public HandWeapon addWeapon(String var1, boolean var2) {
      HandWeapon var3 = (HandWeapon)InventoryItemFactory.CreateItem(var1);
      if (var3 == null) {
         return null;
      } else {
         if (var3.isRanged() && var2) {
            if (!StringUtils.isNullOrWhitespace(var3.getMagazineType())) {
               var3.setContainsClip(true);
            }

            var3.setCurrentAmmoCount(Rand.Next(Math.max(var3.getMaxAmmo() - 8, 0), var3.getMaxAmmo() - 2));
         }

         return var3;
      }
   }

   public IsoDeadBody createSkeletonCorpse(RoomDef var1) {
      if (var1 == null) {
         return null;
      } else {
         IsoGridSquare var2 = var1.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
         if (var2 == null) {
            return null;
         } else {
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(var2);
            IsoZombie var3 = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
            if (var3 == null) {
               return null;
            } else {
               ZombieSpawnRecorder.instance.record(var3, this.getClass().getSimpleName());
               alignCorpseToSquare(var3, var2);
               var3.setFakeDead(false);
               var3.setHealth(0.0F);
               var3.upKillCount = false;
               var3.setSkeleton(true);
               var3.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
               return new IsoDeadBody(var3, true);
            }
         }
      }
   }

   public boolean isTimeValid(boolean var1) {
      if (this.minimumDays != 0 && this.maximumDays != 0) {
         float var2 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
         var2 += (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
         if (this.minimumDays > 0 && var2 < (float)this.minimumDays) {
            return false;
         } else {
            return this.maximumDays <= 0 || !(var2 > (float)this.maximumDays);
         }
      } else {
         return true;
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDebugLine() {
      return this.debugLine;
   }

   public void setDebugLine(String var1) {
      this.debugLine = var1;
   }

   public int getMaximumDays() {
      return this.maximumDays;
   }

   public void setMaximumDays(int var1) {
      this.maximumDays = var1;
   }

   public boolean isUnique() {
      return this.unique;
   }

   public void setUnique(boolean var1) {
      this.unique = var1;
   }

   public IsoGridSquare getSq(int var1, int var2, int var3) {
      return IsoWorld.instance.getCell().getGridSquare(var1, var2, var3);
   }

   public IsoObject addTileObject(int var1, int var2, int var3, String var4) {
      return this.addTileObject(this.getSq(var1, var2, var3), var4);
   }

   public IsoObject addTileObject(IsoGridSquare var1, String var2) {
      if (var1 == null) {
         return null;
      } else {
         IsoObject var3 = IsoObject.getNew(var1, var2, (String)null, false);
         var1.AddTileObject(var3);
         MapObjects.newGridSquare(var1);
         MapObjects.loadGridSquare(var1);
         return var3;
      }
   }

   public IsoObject addTentNorthSouth(int var1, int var2, int var3) {
      this.addTileObject(var1, var2 - 1, var3, "camping_01_1");
      return this.addTileObject(var1, var2, var3, "camping_01_0");
   }

   public IsoObject addTentWestEast(int var1, int var2, int var3) {
      this.addTileObject(var1 - 1, var2, var3, "camping_01_2");
      return this.addTileObject(var1, var2, var3, "camping_01_3");
   }

   public BaseVehicle addTrailer(BaseVehicle var1, IsoMetaGrid.Zone var2, IsoChunk var3, String var4, String var5, String var6) {
      IsoGridSquare var7 = var1.getSquare();
      IsoDirections var8 = var1.getDir();
      byte var9 = 0;
      byte var10 = 0;
      if (var8 == IsoDirections.S) {
         var10 = -3;
      }

      if (var8 == IsoDirections.N) {
         var10 = 3;
      }

      if (var8 == IsoDirections.W) {
         var9 = 3;
      }

      if (var8 == IsoDirections.E) {
         var9 = -3;
      }

      BaseVehicle var11 = this.addVehicle(var2, this.getSq(var7.x + var9, var7.y + var10, var7.z), var3, var4, var6, (Integer)null, var8, var5);
      if (var11 != null) {
         var1.positionTrailer(var11);
      }

      return var11;
   }
}
