package zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.ai.states.ZombieStandState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ZombiePopulationManager;
import zombie.vehicles.BaseVehicle;

public class VirtualZombieManager {
   private ArrayDeque ReusableZombies = new ArrayDeque();
   private HashSet ReusableZombieSet = new HashSet();
   public ArrayList ReusedThisFrame = new ArrayList();
   public static VirtualZombieManager instance = new VirtualZombieManager();
   public int MaxRealZombies = 250;
   public ArrayList choices = new ArrayList();
   ArrayList bestchoices = new ArrayList();
   HandWeapon w = null;

   public boolean removeZombieFromWorld(IsoZombie var1) {
      if (GameServer.bServer) {
         for(int var2 = 0; var2 < GameServer.Players.size(); ++var2) {
            if (((IsoPlayer)GameServer.Players.get(var2)).DistTo(var1) < 10.0F) {
            }
         }
      }

      boolean var3 = var1.getCurrentSquare() != null;
      var1.getEmitter().unregister();
      var1.removeFromWorld();
      var1.removeFromSquare();
      return var3;
   }

   private void reuseZombie(IsoZombie var1) {
      if (var1 != null) {
         assert !IsoWorld.instance.CurrentCell.getObjectList().contains(var1);

         assert !IsoWorld.instance.CurrentCell.getZombieList().contains(var1);

         assert var1.getCurrentSquare() == null || !var1.getCurrentSquare().getMovingObjects().contains(var1);

         if (!this.isReused(var1)) {
            if (ModelManager.instance.Contains.contains(var1) && !ModelManager.instance.ToRemove.contains(var1)) {
               ModelManager.instance.Remove((IsoGameCharacter)var1);
            }

            var1.setCurrent((IsoGridSquare)null);
            var1.setLast((IsoGridSquare)null);
            var1.getStateMachine().Lock = false;
            var1.getStateMachine().setCurrent(ZombieStandState.instance());
            var1.setIgnoreMovementForDirection(true);
            var1.bCrawling = false;
            var1.setOnFloor(false);
            var1.PlayAnim("ZombieIdle");
            var1.strength = -1;
            var1.cognition = -1;
            var1.speedType = -1;
            var1.DoZombieStats();
            if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
               var1.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
            }

            if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
               var1.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
            }

            if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
               var1.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
            }

            if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
               var1.setHealth(Rand.Next(0.5F, 3.5F) + Rand.Next(0.0F, 0.3F));
            }

            var1.setCollidable(true);
            var1.setShootable(true);
            if (var1.isOnFire()) {
               IsoFireManager.RemoveBurningCharacter(var1);
               var1.setOnFire(false);
            }

            if (var1.AttachedAnimSprite != null) {
               var1.AttachedAnimSprite.clear();
            }

            if (var1.AttachedAnimSpriteActual != null) {
               var1.AttachedAnimSpriteActual.clear();
            }

            var1.OnlineID = -1;
            var1.bIndoorZombie = false;
            var1.setVehicle4TestCollision((BaseVehicle)null);
            var1.clearItemsToSpawnAtDeath();
            this.addToReusable(var1);
         }
      }
   }

   public void addToReusable(IsoZombie var1) {
      if (var1 != null && !this.ReusableZombieSet.contains(var1)) {
         this.ReusableZombies.addLast(var1);
         this.ReusableZombieSet.add(var1);
      }

   }

   public boolean isReused(IsoZombie var1) {
      return this.ReusableZombieSet.contains(var1);
   }

   public void init() {
      if (!GameClient.bClient) {
         IsoZombie var1 = null;
         if (SystemDisabler.doZombieCreation) {
            for(int var2 = 0; var2 < this.MaxRealZombies + 100; ++var2) {
               SharedDescriptors.Descriptor var3 = SharedDescriptors.pickRandomDescriptor();
               if (var3 != null) {
                  var1 = new IsoZombie(IsoWorld.instance.CurrentCell, var3.desc, var3.palette);
               } else {
                  var1 = new IsoZombie(IsoWorld.instance.CurrentCell);
               }

               var1.getEmitter().unregister();
               this.addToReusable(var1);
            }

         }
      }
   }

   public void update() {
      int var1;
      IsoZombie var2;
      if (!GameClient.bClient && !GameServer.bServer) {
         for(var1 = 0; var1 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++var1) {
            var2 = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(var1);
            if (!var2.KeepItReal && var2.getCurrentSquare() == null) {
               var2.removeFromWorld();
               var2.removeFromSquare();

               assert this.ReusedThisFrame.contains(var2);

               assert !IsoWorld.instance.CurrentCell.getZombieList().contains(var2);

               --var1;
            }
         }

         for(var1 = 0; var1 < this.ReusedThisFrame.size(); ++var1) {
            var2 = (IsoZombie)this.ReusedThisFrame.get(var1);
            this.reuseZombie(var2);
         }

         this.ReusedThisFrame.clear();
      } else {
         for(var1 = 0; var1 < this.ReusedThisFrame.size(); ++var1) {
            var2 = (IsoZombie)this.ReusedThisFrame.get(var1);
            this.reuseZombie(var2);
         }

         this.ReusedThisFrame.clear();
      }
   }

   public IsoZombie createRealZombieAlways(int var1, boolean var2) {
      return this.createRealZombieAlways(var1, var2, (SurvivorDesc)null, -1);
   }

   public IsoZombie createRealZombieAlways(int var1, int var2, boolean var3) {
      SharedDescriptors.Descriptor var4 = SharedDescriptors.getDescriptor(var1);
      return var4 == null ? this.createRealZombieAlways(var2, var3, (SurvivorDesc)null, -1) : this.createRealZombieAlways(var2, var3, var4.desc, var4.palette);
   }

   public IsoZombie createRealZombieAlways(int var1, boolean var2, SurvivorDesc var3, int var4) {
      IsoZombie var5 = null;
      if (!SystemDisabler.doZombieCreation) {
         return null;
      } else {
         if (this.w == null) {
            this.w = (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe");
         }

         Vector2 var13;
         if (this.ReusableZombies.isEmpty()) {
            if (var3 != null) {
               var5 = new IsoZombie(IsoWorld.instance.CurrentCell, var3, var4);
            } else {
               SharedDescriptors.Descriptor var6 = SharedDescriptors.pickRandomDescriptor();
               if (var6 == null) {
                  var5 = new IsoZombie(IsoWorld.instance.CurrentCell);
               } else {
                  var5 = new IsoZombie(IsoWorld.instance.CurrentCell, var6.desc, var6.palette);
               }
            }

            IsoWorld.instance.CurrentCell.getObjectList().add(var5);
         } else {
            var5 = (IsoZombie)this.ReusableZombies.removeFirst();
            this.ReusableZombieSet.remove(var5);
            var5.useDescriptor(var3, var4);
            var5.bDead = false;
            var5.setFakeDead(false);
            var5.setReanimatedPlayer(false);
            var5.getStateMachine().Lock = false;
            var13 = var5.dir.ToVector();
            var5.angle.x = var13.x;
            var5.angle.y = var13.y;
            Vector2 var10000 = var5.angle;
            var10000.x += (float)Rand.Next(200) / 100.0F - 0.5F;
            var10000 = var5.angle;
            var10000.y += (float)Rand.Next(200) / 100.0F - 0.5F;
            var5.angle.normalize();
            var5.getStateMachine().changeState(ZombieStandState.instance());
            var5.PlayAnim("ZombieIdle");
            IsoWorld.instance.CurrentCell.getObjectList().add(var5);
            var5.walkVariant = "ZombieWalk";
            var5.DoZombieStats();
            if (var5.isOnFire()) {
               IsoFireManager.RemoveBurningCharacter(var5);
               var5.setOnFire(false);
            }

            if (var5.AttachedAnimSprite != null) {
               var5.AttachedAnimSprite.clear();
            }

            if (var5.AttachedAnimSpriteActual != null) {
               var5.AttachedAnimSpriteActual.clear();
            }

            var5.bx = 0.0F;
            var5.thumpFlag = 0;
            var5.thumpSent = false;
            var5.mpIdleSound = false;
            var5.soundSourceTarget = null;
            var5.soundAttract = 0.0F;
            var5.soundAttractTimeout = 0.0F;
            var5.clearItemsToSpawnAtDeath();
         }

         var5.dir = IsoDirections.fromIndex(var1);
         var13 = var5.dir.ToVector();
         var5.angle.x = var13.x;
         var5.angle.y = var13.y;
         var5.target = null;
         var5.TimeSinceSeenFlesh = 100000.0F;
         var5.nextRallyTime = -1.0F;
         if (!var5.isFakeDead()) {
            if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
               var5.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
            }

            if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
               var5.setHealth(1.5F + Rand.Next(0.0F, 0.3F));
            }

            if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
               var5.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
            }

            if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
               var5.setHealth(Rand.Next(0.5F, 3.5F) + Rand.Next(0.0F, 0.3F));
            }
         } else {
            var5.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
         }

         float var7 = (float)Rand.Next(0, 1000);
         float var8 = (float)Rand.Next(0, 1000);
         var7 /= 1000.0F;
         var8 /= 1000.0F;
         IsoGridSquare var9 = (IsoGridSquare)this.choices.get(Rand.Next(this.choices.size()));
         if (var9 == null) {
            return null;
         } else {
            if (var9 == null) {
               for(int var10 = 0; var10 < this.choices.size(); ++var10) {
                  if (this.choices.get(var10) != null) {
                     var9 = (IsoGridSquare)this.choices.get(var10);
                     break;
                  }
               }

               if (var9 == null) {
                  DebugLog.log("ERROR: createRealZombieAlways can not create zombie");
                  return null;
               }
            }

            var7 += (float)var9.getX();
            var8 += (float)var9.getY();
            var5.setCurrent(var9);
            var5.setX(var7);
            var5.setY(var8);
            var5.setZ((float)var9.getZ());
            var5.upKillCount = true;
            if (var2) {
               var5.setDir(IsoDirections.fromIndex(Rand.Next(8)));
               var13 = var5.dir.ToVector();
               var5.angle.x = var13.x;
               var5.angle.y = var13.y;
               var5.setFakeDead(false);
               var5.setHealth(0.0F);
               var5.upKillCount = false;
               var5.DoZombieInventory();
               new IsoDeadBody(var5, true);
               return var5;
            } else {
               synchronized(IsoWorld.instance.CurrentCell.getZombieList()) {
                  var5.getEmitter().register();
                  IsoWorld.instance.CurrentCell.getZombieList().add(var5);
                  if (GameClient.bClient) {
                     var5.bRemote = true;
                  }

                  if (GameServer.bServer) {
                     var5.OnlineID = ServerMap.instance.getUniqueZombieId();
                     if (var5.OnlineID == -1) {
                        IsoWorld.instance.CurrentCell.getZombieList().remove(var5);
                        IsoWorld.instance.CurrentCell.getObjectList().remove(var5);
                        this.ReusedThisFrame.add(var5);
                        return null;
                     }

                     ServerMap.instance.ZombieMap.put(var5.OnlineID, var5);
                  }

                  return var5;
               }
            }
         }
      }
   }

   private IsoZombie createRealZombie(int var1, boolean var2) {
      Object var3 = null;
      if (GameClient.bClient) {
         return null;
      } else {
         return !SystemDisabler.doZombieCreation ? null : this.createRealZombieAlways(var1, var2);
      }
   }

   public void AddBloodToMap(int var1, IsoChunk var2) {
      for(int var3 = 0; var3 < var1; ++var3) {
         IsoGridSquare var4 = null;
         int var5 = 0;

         int var7;
         do {
            int var6 = Rand.Next(10);
            var7 = Rand.Next(10);
            var4 = var2.getGridSquare(var6, var7, 0);
            ++var5;
         } while(var5 < 100 && (var4 == null || !var4.isFree(false)));

         if (var4 != null) {
            byte var10 = 5;
            if (Rand.Next(10) == 0) {
               var10 = 10;
            }

            if (Rand.Next(40) == 0) {
               var10 = 20;
            }

            for(var7 = 0; var7 < var10; ++var7) {
               float var8 = (float)Rand.Next(3000) / 1000.0F;
               float var9 = (float)Rand.Next(3000) / 1000.0F;
               --var8;
               --var9;
               var2.addBloodSplat((float)var4.getX() + var8, (float)var4.getY() + var9, (float)var4.getZ(), Rand.Next(12) + 8);
            }
         }
      }

   }

   public void addZombiesToMap(int var1, RoomDef var2) {
      this.addZombiesToMap(var1, var2, true);
   }

   public void addZombiesToMap(int var1, RoomDef var2, boolean var3) {
      this.choices.clear();
      this.bestchoices.clear();
      IsoGridSquare var4 = null;

      int var5;
      int var6;
      for(var5 = 0; var5 < var2.rects.size(); ++var5) {
         var6 = var2.level;
         RoomDef.RoomRect var7 = (RoomDef.RoomRect)var2.rects.get(var5);

         for(int var8 = var7.x; var8 < var7.getX2(); ++var8) {
            for(int var9 = var7.y; var9 < var7.getY2(); ++var9) {
               var4 = IsoWorld.instance.CurrentCell.getGridSquare(var8, var9, var6);
               if (var4 != null && this.canSpawnAt(var8, var9, var6)) {
                  this.choices.add(var4);
                  boolean var10 = false;

                  for(int var11 = 0; var11 < IsoPlayer.numPlayers; ++var11) {
                     if (IsoPlayer.players[var11] != null && var4.isSeen(var11)) {
                        var10 = true;
                     }
                  }

                  if (!var10) {
                     this.bestchoices.add(var4);
                  }
               }
            }
         }
      }

      var1 = Math.min(var1, this.choices.size());
      if (!this.bestchoices.isEmpty()) {
         this.choices.addAll(this.bestchoices);
         this.choices.addAll(this.bestchoices);
      }

      for(var5 = 0; var5 < var1; ++var5) {
         if (!this.choices.isEmpty()) {
            var2.building.bAlarmed = false;
            var6 = Rand.Next(8);
            byte var12 = 4;
            IsoZombie var13 = this.createRealZombie(var6, var3 ? Rand.Next(var12) == 0 : false);
            if (var13 != null && var13.getSquare() != null) {
               var13.setX((float)((int)var13.getX()) + (float)Rand.Next(2, 8) / 10.0F);
               var13.setY((float)((int)var13.getY()) + (float)Rand.Next(2, 8) / 10.0F);
               this.choices.remove(var13.getSquare());
               this.choices.remove(var13.getSquare());
               this.choices.remove(var13.getSquare());
            }
         } else {
            System.out.println("No choices for zombie.");
         }
      }

      this.bestchoices.clear();
      this.choices.clear();
   }

   public void tryAddIndoorZombies(RoomDef var1, boolean var2) {
      if (GameServer.bServer) {
         if (!IsoWorld.getZombiesDisabled()) {
            if (var1.getBuilding() != null && var1.getBuilding().getRooms().size() > 100 && var1.getArea() >= 20) {
               int var4 = var1.getBuilding().getRooms().size() - 95;
               if (var4 > 20) {
                  var4 = 20;
               }

               if (SandboxOptions.instance.Zombies.getValue() == 1) {
                  var4 += 10;
               } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                  var4 += 5;
               } else if (SandboxOptions.instance.Zombies.getValue() == 4) {
                  var4 -= 10;
               }

               if (var1.getArea() < 30) {
                  var4 -= 6;
               }

               if (var1.getArea() < 50) {
                  var4 -= 10;
               }

               if (var1.getArea() < 70) {
                  var4 -= 13;
               }

               DebugLog.log(DebugType.Zombie, "addIndoorZombies " + var1.ID);
               this.addIndoorZombies(Rand.Next(var4, var4 + 10), var1, false);
            } else {
               byte var3 = 7;
               if (SandboxOptions.instance.Zombies.getValue() == 1) {
                  var3 = 3;
               } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                  var3 = 6;
               } else if (SandboxOptions.instance.Zombies.getValue() == 4) {
                  var3 = 15;
               }

               if (Rand.Next(var3) == 0) {
                  DebugLog.log(DebugType.Zombie, "addIndoorZombies " + var1.ID);
                  this.addIndoorZombies(Rand.Next(1, 3), var1, var2);
               }

            }
         }
      }
   }

   private void addIndoorZombies(int var1, RoomDef var2, boolean var3) {
      this.choices.clear();
      this.bestchoices.clear();
      IsoGridSquare var4 = null;

      int var5;
      int var6;
      for(var5 = 0; var5 < var2.rects.size(); ++var5) {
         var6 = var2.level;
         RoomDef.RoomRect var7 = (RoomDef.RoomRect)var2.rects.get(var5);

         for(int var8 = var7.x; var8 < var7.getX2(); ++var8) {
            for(int var9 = var7.y; var9 < var7.getY2(); ++var9) {
               var4 = IsoWorld.instance.CurrentCell.getGridSquare(var8, var9, var6);
               if (var4 != null && this.canSpawnAt(var8, var9, var6)) {
                  this.choices.add(var4);
               }
            }
         }
      }

      var1 = Math.min(var1, this.choices.size());
      if (!this.bestchoices.isEmpty()) {
         this.choices.addAll(this.bestchoices);
         this.choices.addAll(this.bestchoices);
      }

      for(var5 = 0; var5 < var1; ++var5) {
         if (!this.choices.isEmpty()) {
            var2.building.bAlarmed = false;
            var6 = Rand.Next(8);
            byte var10 = 4;
            IsoZombie var11 = this.createRealZombie(var6, var3 ? Rand.Next(var10) == 0 : false);
            if (var11 != null && var11.getSquare() != null) {
               var11.bIndoorZombie = true;
               var11.setX((float)((int)var11.getX()) + (float)Rand.Next(2, 8) / 10.0F);
               var11.setY((float)((int)var11.getY()) + (float)Rand.Next(2, 8) / 10.0F);
               this.choices.remove(var11.getSquare());
               this.choices.remove(var11.getSquare());
               this.choices.remove(var11.getSquare());
            }
         } else {
            System.out.println("No choices for zombie.");
         }
      }

      this.bestchoices.clear();
      this.choices.clear();
   }

   public void addDeadZombiesToMap(int var1, RoomDef var2) {
      boolean var3 = false;
      this.choices.clear();
      this.bestchoices.clear();
      IsoGridSquare var4 = null;

      int var5;
      int var6;
      for(var5 = 0; var5 < var2.rects.size(); ++var5) {
         var6 = var2.level;
         RoomDef.RoomRect var7 = (RoomDef.RoomRect)var2.rects.get(var5);

         for(int var8 = var7.x; var8 < var7.getX2(); ++var8) {
            for(int var9 = var7.y; var9 < var7.getY2(); ++var9) {
               var4 = IsoWorld.instance.CurrentCell.getGridSquare(var8, var9, var6);
               if (var4 != null && var4.isFree(false)) {
                  this.choices.add(var4);
                  if (!GameServer.bServer) {
                     boolean var10 = false;

                     for(int var11 = 0; var11 < IsoPlayer.numPlayers; ++var11) {
                        if (IsoPlayer.players[var11] != null && var4.isSeen(var11)) {
                           var10 = true;
                        }
                     }

                     if (!var10) {
                        this.bestchoices.add(var4);
                     }
                  }
               }
            }
         }
      }

      var1 = Math.min(var1, this.choices.size());
      if (!this.bestchoices.isEmpty()) {
         this.choices.addAll(this.bestchoices);
         this.choices.addAll(this.bestchoices);
      }

      for(var5 = 0; var5 < var1; ++var5) {
         if (!this.choices.isEmpty()) {
            var6 = Rand.Next(8);
            this.createRealZombie(var6, true);
         }
      }

      this.bestchoices.clear();
      this.choices.clear();
   }

   public void RemoveZombie(IsoZombie var1) {
      if (var1.isReanimatedPlayer()) {
         ReanimatedPlayers.instance.removeReanimatedPlayerFromWorld(var1);
      } else {
         if (!this.ReusedThisFrame.contains(var1)) {
            this.ReusedThisFrame.add(var1);
         }

      }
   }

   public void createHordeFromTo(float var1, float var2, float var3, float var4, int var5) {
      ZombiePopulationManager.instance.createHordeFromTo((int)var1, (int)var2, (int)var3, (int)var4, var5);
   }

   public IsoZombie createRealZombie(float var1, float var2, float var3) {
      this.choices.clear();
      this.choices.add(IsoWorld.instance.CurrentCell.getGridSquare((double)var1, (double)var2, (double)var3));
      if (!this.choices.isEmpty()) {
         int var4 = Rand.Next(8);
         return this.createRealZombie(var4, true);
      } else {
         return null;
      }
   }

   public IsoZombie createRealZombieNow(float var1, float var2, float var3) {
      this.choices.clear();
      IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare((double)var1, (double)var2, (double)var3);
      if (var4 == null) {
         return null;
      } else {
         this.choices.add(var4);
         if (!this.choices.isEmpty()) {
            int var5 = Rand.Next(8);
            return this.createRealZombie(var5, false);
         } else {
            return null;
         }
      }
   }

   public void roomSpotted(IsoRoom var1) {
      if (!IsoWorld.getZombiesDisabled()) {
         if (!GameServer.bServer && !GameClient.bClient) {
            if (!Core.bLastStand) {
               byte var2 = 7;
               if (SandboxOptions.instance.Zombies.getValue() == 1) {
                  var2 = 3;
               } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                  var2 = 6;
               } else if (SandboxOptions.instance.Zombies.getValue() == 4) {
                  var2 = 15;
               }

               if (var1.getBuilding() != null && var1.getBuilding().getRoomsNumber() > 100 && var1.getSquares() != null && var1.getSquares().size() >= 20) {
                  int var3 = var1.getBuilding().getRoomsNumber() - 95;
                  if (var3 > 20) {
                     var3 = 20;
                  }

                  if (SandboxOptions.instance.Zombies.getValue() == 1) {
                     var3 += 10;
                  } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                     var3 += 5;
                  } else if (SandboxOptions.instance.Zombies.getValue() == 4) {
                     var3 -= 10;
                  }

                  if (var1.getSquares() != null && var1.getSquares().size() < 30) {
                     var3 -= 6;
                  }

                  if (var1.getSquares() != null && var1.getSquares().size() < 50) {
                     var3 -= 10;
                  }

                  if (var1.getSquares() != null && var1.getSquares().size() < 70) {
                     var3 -= 13;
                  }

                  this.addZombiesToMap(Rand.Next(var3, var3 + 10), var1.def, false);
               } else {
                  if (Rand.Next(var2) == 0) {
                     this.addZombiesToMap(Rand.Next(1, 3), var1.def, false);
                  }

               }
            }
         }
      }
   }

   private boolean isBlockedInAllDirections(int var1, int var2, int var3) {
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      IsoGridSquare var5 = var4.getGridSquare(var1, var2, var3);
      if (var5 == null) {
         return false;
      } else {
         boolean var6 = var5.pathMatrix[1][0][1] && var5.nav[IsoDirections.N.index()] != null;
         boolean var7 = var5.pathMatrix[1][2][1] && var5.nav[IsoDirections.S.index()] != null;
         boolean var8 = var5.pathMatrix[0][1][1] && var5.nav[IsoDirections.W.index()] != null;
         boolean var9 = var5.pathMatrix[2][1][1] && var5.nav[IsoDirections.E.index()] != null;
         return var6 && var7 && var8 && var9;
      }
   }

   private boolean canPathOnlyN(int var1, int var2, int var3) {
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      IsoGridSquare var5 = var4.getGridSquare(var1, var2, var3);
      if (var5 == null) {
         return false;
      } else {
         boolean var6 = var5.pathMatrix[1][0][1] && var5.nav[IsoDirections.N.index()] != null;
         boolean var7 = var5.pathMatrix[1][2][1] && var5.nav[IsoDirections.S.index()] != null;
         boolean var8 = var5.pathMatrix[0][1][1] && var5.nav[IsoDirections.W.index()] != null;
         boolean var9 = var5.pathMatrix[2][1][1] && var5.nav[IsoDirections.E.index()] != null;
         return !var6 && var7 && var8 && var9;
      }
   }

   private boolean canPathOnlyS(int var1, int var2, int var3) {
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      IsoGridSquare var5 = var4.getGridSquare(var1, var2, var3);
      if (var5 == null) {
         return false;
      } else {
         boolean var6 = var5.pathMatrix[1][0][1] && var5.nav[IsoDirections.N.index()] != null;
         boolean var7 = var5.pathMatrix[1][2][1] && var5.nav[IsoDirections.S.index()] != null;
         boolean var8 = var5.pathMatrix[0][1][1] && var5.nav[IsoDirections.W.index()] != null;
         boolean var9 = var5.pathMatrix[2][1][1] && var5.nav[IsoDirections.E.index()] != null;
         return var6 && !var7 && var8 && var9;
      }
   }

   private boolean canPathOnlyW(int var1, int var2, int var3) {
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      IsoGridSquare var5 = var4.getGridSquare(var1, var2, var3);
      if (var5 == null) {
         return false;
      } else {
         boolean var6 = var5.pathMatrix[1][0][1] && var5.nav[IsoDirections.N.index()] != null;
         boolean var7 = var5.pathMatrix[1][2][1] && var5.nav[IsoDirections.S.index()] != null;
         boolean var8 = var5.pathMatrix[0][1][1] && var5.nav[IsoDirections.W.index()] != null;
         boolean var9 = var5.pathMatrix[2][1][1] && var5.nav[IsoDirections.E.index()] != null;
         return var6 && var7 && !var8 && var9;
      }
   }

   private boolean canPathOnlyE(int var1, int var2, int var3) {
      IsoCell var4 = IsoWorld.instance.CurrentCell;
      IsoGridSquare var5 = var4.getGridSquare(var1, var2, var3);
      if (var5 == null) {
         return false;
      } else {
         boolean var6 = var5.pathMatrix[1][0][1] && var5.nav[IsoDirections.N.index()] != null;
         boolean var7 = var5.pathMatrix[1][2][1] && var5.nav[IsoDirections.S.index()] != null;
         boolean var8 = var5.pathMatrix[0][1][1] && var5.nav[IsoDirections.W.index()] != null;
         boolean var9 = var5.pathMatrix[2][1][1] && var5.nav[IsoDirections.E.index()] != null;
         return var6 && var7 && var8 && !var9;
      }
   }

   public boolean canSpawnAt(int var1, int var2, int var3) {
      IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var4 != null && var4.isFree(false)) {
         if (this.isBlockedInAllDirections(var1, var2, var3)) {
            return false;
         } else if (this.canPathOnlyE(var1, var2, var3) && this.canPathOnlyW(var1 + 1, var2, var3)) {
            return false;
         } else if (this.canPathOnlyE(var1 - 1, var2, var3) && this.canPathOnlyW(var1, var2, var3)) {
            return false;
         } else if (this.canPathOnlyS(var1, var2, var3) && this.canPathOnlyN(var1, var2 + 1, var3)) {
            return false;
         } else {
            return !this.canPathOnlyS(var1, var2 - 1, var3) || !this.canPathOnlyN(var1, var2, var3);
         }
      } else {
         return false;
      }
   }
}
