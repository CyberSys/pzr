package zombie.iso;

import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.ColorInfo;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.weather.ClimateManager;
import zombie.meta.Meta;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;

public final class LightingJNI {
   public static boolean newLightingMethod = true;
   public static boolean init = false;
   private static ArrayList torches = new ArrayList();
   private static ArrayList activeTorches = new ArrayList();
   private static ArrayList JNILights = new ArrayList();
   private static int[] updateCounter = new int[4];
   private static boolean bWasElecShut = false;
   private static boolean bWasNight = false;
   public static int[][] ForcedVis = new int[][]{{-1, 0, -1, -1, 0, -1, 1, -1, 1, 0, -2, -2, -1, -2, 0, -2, 1, -2, 2, -2}, {-1, 1, -1, 0, -1, -1, 0, -1, 1, -1, -2, 0, -2, -1, -2, -2, -1, -2, 0, -2}, {0, 1, -1, 1, -1, 0, -1, -1, 0, -1, -2, 2, -2, 1, -2, 0, -2, -1, -2, -2}, {1, 1, 0, 1, -1, 1, -1, 0, -1, -1, 0, 2, -1, 2, -2, 2, -2, 1, -2, 0}, {1, 0, 1, 1, 0, 1, -1, 1, -1, 0, 2, 2, 1, 2, 0, 2, -1, 2, -2, 2}, {-1, 1, 0, 1, 1, 1, 1, 0, 1, -1, 2, 0, 2, 1, 2, 2, 1, 2, 0, 2}, {0, 1, 1, 1, 1, 0, 1, -1, 0, -1, 2, -2, 2, -1, 2, 0, 2, 1, 2, 2}, {-1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, -2, 1, -2, 2, -2, 2, -1, 2, 0}};
   public static final int ROOM_SPAWN_DIST = 50;

   public static void init() {
      if (!init) {
         String var0 = "";

         try {
            if (System.getProperty("os.name").contains("OS X")) {
               System.loadLibrary("Lighting");
            } else if (System.getProperty("os.name").startsWith("Win")) {
               if (System.getProperty("sun.arch.data.model").equals("64")) {
                  System.loadLibrary("Lighting64" + var0);
               } else {
                  System.loadLibrary("Lighting32" + var0);
               }
            } else if (System.getProperty("sun.arch.data.model").equals("64")) {
               System.loadLibrary("Lighting64");
            } else {
               System.loadLibrary("Lighting32");
            }

            for(int var1 = 0; var1 < 4; ++var1) {
               updateCounter[var1] = -1;
            }

            configure(0.005F);
            init = true;
         } catch (UnsatisfiedLinkError var4) {
            var4.printStackTrace();

            try {
               Thread.sleep(3000L);
            } catch (InterruptedException var3) {
            }

            System.exit(1);
         }

      }
   }

   private static void checkTorch(IsoPlayer var0, int var1) {
      if (var0 != null && !var0.isDead() && var0.getTorchStrength() > 0.0F && var0.getVehicle() == null) {
         IsoGameCharacter.TorchInfo var4 = null;

         for(int var5 = 0; var5 < torches.size(); ++var5) {
            var4 = (IsoGameCharacter.TorchInfo)torches.get(var5);
            if (var4.id == var1) {
               break;
            }

            var4 = null;
         }

         if (var4 == null) {
            var4 = IsoGameCharacter.TorchInfo.alloc();
            torches.add(var4);
         }

         var4.set(var0);
         if (var4.id == 0) {
            var4.id = var1;
         }

         updateTorch(var4.id, var4.x, var4.y, var4.z, var4.angleX, var4.angleY, var4.dist, var4.strength, var4.bCone, var4.dot, var4.focusing);
         activeTorches.add(var4);
      } else {
         for(int var2 = 0; var2 < torches.size(); ++var2) {
            IsoGameCharacter.TorchInfo var3 = (IsoGameCharacter.TorchInfo)torches.get(var2);
            if (var3.id == var1) {
               removeTorch(var3.id);
               var3.id = 0;
               IsoGameCharacter.TorchInfo.release(var3);
               torches.remove(var2--);
            }
         }
      }

   }

   private static void checkTorch(VehiclePart var0, int var1) {
      VehicleLight var2 = var0.getLight();
      if (var2 != null && var2.getActive()) {
         IsoGameCharacter.TorchInfo var5 = null;

         for(int var6 = 0; var6 < torches.size(); ++var6) {
            var5 = (IsoGameCharacter.TorchInfo)torches.get(var6);
            if (var5.id == var1) {
               break;
            }

            var5 = null;
         }

         if (var5 == null) {
            var5 = IsoGameCharacter.TorchInfo.alloc();
            torches.add(var5);
         }

         var5.set(var0);
         if (var5.id == 0) {
            var5.id = var1;
         }

         updateTorch(var5.id, var5.x, var5.y, var5.z, var5.angleX, var5.angleY, var5.dist, var5.strength, var5.bCone, var5.dot, var5.focusing);
         activeTorches.add(var5);
      } else {
         for(int var3 = 0; var3 < torches.size(); ++var3) {
            IsoGameCharacter.TorchInfo var4 = (IsoGameCharacter.TorchInfo)torches.get(var3);
            if (var4.id == var1) {
               removeTorch(var4.id);
               var4.id = 0;
               IsoGameCharacter.TorchInfo.release(var4);
               torches.remove(var3--);
            }
         }
      }

   }

   private static void checkLights() {
      if (IsoWorld.instance.CurrentCell != null) {
         if (GameClient.bClient) {
            IsoGenerator.updateSurroundingNow();
         }

         boolean var0 = GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
         Stack var1 = IsoWorld.instance.CurrentCell.getLamppostPositions();

         int var2;
         IsoLightSource var3;
         int var5;
         boolean var13;
         for(var2 = 0; var2 < var1.size(); ++var2) {
            var3 = (IsoLightSource)var1.get(var2);
            IsoChunk var4 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(var3.x, var3.y, var3.z);
            if (var4 != null && var3.chunk != null && var3.chunk != var4) {
               var3.life = 0;
            }

            if (var3.life != 0 && var3.isInBounds()) {
               if (var3.bHydroPowered) {
                  if (var3.switches.isEmpty()) {
                     assert false;

                     var13 = var0;
                     if (!var0) {
                        IsoGridSquare var16 = IsoWorld.instance.CurrentCell.getGridSquare(var3.x, var3.y, var3.z);
                        var13 = var16 != null && var16.haveElectricity();
                     }

                     if (var3.bActive != var13) {
                        var3.bActive = var13;
                        GameTime.instance.lightSourceUpdate = 100.0F;
                     }
                  } else {
                     IsoLightSwitch var6 = (IsoLightSwitch)var3.switches.get(0);
                     var13 = var6.canSwitchLight();
                     if (var6.bStreetLight && GameTime.getInstance().getNight() < 0.5F) {
                        var13 = false;
                     }

                     if (var3.bActive && !var13) {
                        var3.bActive = false;
                        GameTime.instance.lightSourceUpdate = 100.0F;
                     } else if (!var3.bActive && var13 && var6.isActivated()) {
                        var3.bActive = true;
                        GameTime.instance.lightSourceUpdate = 100.0F;
                     }
                  }
               }

               if (var3.ID == 0) {
                  var3.ID = IsoLightSource.NextID++;
                  if (var3.life != -1) {
                     addTempLight(var3.ID, var3.x, var3.y, var3.z, var3.radius, var3.r, var3.g, var3.b, (int)((float)(var3.life * PerformanceSettings.LockFPS) / 30.0F));
                     var1.remove(var2--);
                  } else {
                     var3.rJNI = var3.r;
                     var3.gJNI = var3.g;
                     var3.bJNI = var3.b;
                     var3.bActiveJNI = var3.bActive;
                     JNILights.add(var3);
                     addLight(var3.ID, var3.x, var3.y, var3.z, var3.radius, var3.r, var3.g, var3.b, var3.localToBuilding == null ? -1 : var3.localToBuilding.ID, var3.bActive);
                  }
               } else {
                  if (var3.r != var3.rJNI || var3.g != var3.gJNI || var3.b != var3.bJNI) {
                     var3.rJNI = var3.r;
                     var3.gJNI = var3.g;
                     var3.bJNI = var3.b;
                     setLightColor(var3.ID, var3.r, var3.g, var3.b);
                  }

                  if (var3.bActiveJNI != var3.bActive) {
                     var3.bActiveJNI = var3.bActive;
                     setLightActive(var3.ID, var3.bActive);
                  }
               }
            } else {
               var1.remove(var2);
               if (var3.ID != 0) {
                  var5 = var3.ID;
                  var3.ID = 0;
                  JNILights.remove(var3);
                  removeLight(var5);
                  GameTime.instance.lightSourceUpdate = 100.0F;
               }

               --var2;
            }
         }

         int var9;
         for(var2 = 0; var2 < JNILights.size(); ++var2) {
            var3 = (IsoLightSource)JNILights.get(var2);
            if (!var1.contains(var3)) {
               var9 = var3.ID;
               var3.ID = 0;
               JNILights.remove(var2--);
               removeLight(var9);
            }
         }

         ArrayList var11 = IsoWorld.instance.CurrentCell.roomLights;

         int var8;
         for(var8 = 0; var8 < var11.size(); ++var8) {
            IsoRoomLight var10 = (IsoRoomLight)var11.get(var8);
            if (!var10.isInBounds()) {
               var11.remove(var8--);
               if (var10.ID != 0) {
                  var5 = var10.ID;
                  var10.ID = 0;
                  removeRoomLight(var5);
               }
            } else {
               var10.bActive = var10.room.def.bLightsActive;
               if (!var0) {
                  var13 = false;

                  for(int var18 = 0; !var13 && var18 < var10.room.lightSwitches.size(); ++var18) {
                     IsoLightSwitch var7 = (IsoLightSwitch)var10.room.lightSwitches.get(var18);
                     if (var7.square != null && var7.square.haveElectricity()) {
                        var13 = true;
                     }
                  }

                  if (!var13 && var10.bActive) {
                     var10.bActive = false;
                     if (var10.bActiveJNI) {
                        IsoGridSquare.RecalcLightTime = -1;
                        GameTime.instance.lightSourceUpdate = 100.0F;
                     }
                  } else if (var13 && var10.bActive && !var10.bActiveJNI) {
                     IsoGridSquare.RecalcLightTime = -1;
                     GameTime.instance.lightSourceUpdate = 100.0F;
                  }
               }

               if (var10.ID == 0) {
                  var10.ID = IsoRoomLight.NextID++;
                  addRoomLight(var10.ID, var10.room.building.def.ID * 1000 + var10.room.def.ID, var10.x, var10.y, var10.z, var10.width, var10.height, var10.bActive);
                  var10.bActiveJNI = var10.bActive;
               } else if (var10.bActiveJNI != var10.bActive) {
                  setRoomLightActive(var10.ID, var10.bActive);
                  var10.bActiveJNI = var10.bActive;
               }
            }
         }

         activeTorches.clear();
         if (GameClient.bClient) {
            ArrayList var12 = GameClient.instance.getPlayers();

            for(var9 = 0; var9 < var12.size(); ++var9) {
               IsoPlayer var20 = (IsoPlayer)var12.get(var9);
               checkTorch(var20, var20.OnlineID + 1);
            }
         } else {
            for(var8 = 0; var8 < IsoPlayer.numPlayers; ++var8) {
               IsoPlayer var14 = IsoPlayer.players[var8];
               checkTorch(var14, var8 + 1);
            }
         }

         for(var8 = 0; var8 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++var8) {
            BaseVehicle var15 = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(var8);
            if (var15.VehicleID != -1) {
               for(var5 = 0; var5 < var15.getLightCount(); ++var5) {
                  VehiclePart var19 = var15.getLightByIndex(var5);
                  checkTorch(var19, 256 + var15.VehicleID * 10 + var5);
               }
            }
         }

         for(var8 = 0; var8 < torches.size(); ++var8) {
            IsoGameCharacter.TorchInfo var17 = (IsoGameCharacter.TorchInfo)torches.get(var8);
            if (!activeTorches.contains(var17)) {
               removeTorch(var17.id);
               var17.id = 0;
               IsoGameCharacter.TorchInfo.release(var17);
               torches.remove(var8--);
            }
         }

      }
   }

   public static void updatePlayer(int var0) {
      IsoPlayer var1 = IsoPlayer.players[var0];
      if (var1 != null) {
         float var2 = var1.getStats().fatigue - 0.6F;
         if (var2 < 0.0F) {
            var2 = 0.0F;
         }

         var2 *= 2.5F;
         if (var1.HasTrait("HardOfHearing") && var2 < 0.7F) {
            var2 = 0.7F;
         }

         float var3 = 2.0F;
         if (var1.HasTrait("KeenHearing")) {
            var3 += 3.0F;
         }

         float var4;
         if (var1.getVehicle() == null) {
            var4 = -0.2F;
            var4 -= var1.getStats().fatigue - 0.6F;
            if (var4 > -0.2F) {
               var4 = -0.2F;
            }

            if (var1.getStats().fatigue >= 1.0F) {
               var4 -= 0.2F;
            }

            if (var1.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
               var4 -= 0.2F;
            }

            if (var1.isInARoom()) {
               var4 -= 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            } else {
               var4 -= 0.7F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            }

            if (var4 < -0.9F) {
               var4 = -0.9F;
            }

            if (var1.HasTrait("EagleEyed")) {
               var4 += 0.2F * ClimateManager.getInstance().getDayLightStrength();
            }

            if (var1.HasTrait("NightVision")) {
               var4 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            }

            if (var4 > 0.0F) {
               var4 = 0.0F;
            }
         } else {
            if (var1.getVehicle().getHeadlightsOn() && var1.getVehicle().getHeadlightCanEmmitLight()) {
               var4 = 1.5F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
               if (var4 < -0.8F) {
                  var4 = -0.8F;
               }
            } else {
               var4 = 1.5F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
               if (var4 < -0.99F) {
                  var4 = -0.99F;
               }
            }

            if (var1.HasTrait("NightVision")) {
               var4 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            }

            if (var4 > 1.0F) {
               var4 = 1.0F;
            }
         }

         playerSet(var1.x, var1.y, var1.z, var1.getAngle().getX(), var1.getAngle().getY(), var1.isDead(), var1.ReanimatedCorpse != null, var1.GhostMode, var1.HasTrait("ShortSighted"), var2, var3, var4);
      }

   }

   public static void updateChunk(IsoChunk var0) {
      chunkBeginUpdate(var0.wx, var0.wy);

      for(int var1 = 0; var1 < IsoCell.MaxHeight; ++var1) {
         for(int var2 = 0; var2 < 10; ++var2) {
            for(int var3 = 0; var3 < 10; ++var3) {
               IsoGridSquare var4 = var0.getGridSquare(var3, var2, var1);
               if (var4 == null) {
                  squareSetNull(var3, var2, var1);
               } else {
                  squareBeginUpdate(var3, var2, var1);
                  int var5 = 0;

                  int var7;
                  for(int var6 = 0; var6 < 3; ++var6) {
                     for(var7 = 0; var7 < 3; ++var7) {
                        for(int var8 = 0; var8 < 3; ++var8) {
                           if (var4.visionMatrix[var6][var7][var8]) {
                              var5 |= 1 << var6 + var7 * 3 + var8 * 9;
                           }
                        }
                     }
                  }

                  boolean var14 = var4.Has(IsoObjectType.stairsTN) || var4.Has(IsoObjectType.stairsMN) || var4.Has(IsoObjectType.stairsTW) || var4.Has(IsoObjectType.stairsMW);
                  squareSet(var4.w != null, var4.n != null, var4.e != null, var4.s != null, var14, var5, var4.getRoom() != null ? var4.getBuilding().ID : -1, var4.getRoomID());

                  for(var7 = 0; var7 < var4.getSpecialObjects().size(); ++var7) {
                     IsoObject var15 = (IsoObject)var4.getSpecialObjects().get(var7);
                     if (var15 instanceof IsoCurtain) {
                        IsoCurtain var18 = (IsoCurtain)var15;
                        int var21 = 0;
                        if (var18.getType() == IsoObjectType.curtainW) {
                           var21 |= 4;
                        } else if (var18.getType() == IsoObjectType.curtainN) {
                           var21 |= 8;
                        } else if (var18.getType() == IsoObjectType.curtainE) {
                           var21 |= 16;
                        } else if (var18.getType() == IsoObjectType.curtainS) {
                           var21 |= 32;
                        }

                        squareAddCurtain(var21, var18.open);
                     } else {
                        boolean var10;
                        IsoBarricade var11;
                        IsoBarricade var12;
                        if (!(var15 instanceof IsoDoor)) {
                           if (var15 instanceof IsoThumpable) {
                              IsoThumpable var16 = (IsoThumpable)var15;
                              squareAddThumpable(var16.north, var16.open, var16.isDoor, var16.getSprite().getProperties().Is("doorTrans"));
                              IsoThumpable var20 = (IsoThumpable)var15;
                              boolean var19 = false;
                              var12 = var20.getBarricadeOnSameSquare();
                              IsoBarricade var13 = var20.getBarricadeOnOppositeSquare();
                              if (var12 != null) {
                                 var19 |= var12.isBlockVision();
                              }

                              if (var13 != null) {
                                 var19 |= var13.isBlockVision();
                              }

                              squareAddWindow(var20.north, var20.open, var19);
                           } else if (var15 instanceof IsoWindow) {
                              IsoWindow var17 = (IsoWindow)var15;
                              var10 = false;
                              var11 = var17.getBarricadeOnSameSquare();
                              var12 = var17.getBarricadeOnOppositeSquare();
                              if (var11 != null) {
                                 var10 |= var11.isBlockVision();
                              }

                              if (var12 != null) {
                                 var10 |= var12.isBlockVision();
                              }

                              squareAddWindow(var17.north, var17.open, var10);
                           }
                        } else {
                           IsoDoor var9 = (IsoDoor)var15;
                           var10 = var9.sprite != null && var9.sprite.getProperties().Is("doorTrans");
                           var10 = var10 && (var9.HasCurtains() == null || var9.isCurtainOpen());
                           var11 = var9.getBarricadeOnSameSquare();
                           var12 = var9.getBarricadeOnOppositeSquare();
                           if (var11 != null && var11.isBlockVision()) {
                              var10 = false;
                           }

                           if (var12 != null && var12.isBlockVision()) {
                              var10 = false;
                           }

                           squareAddDoor(var9.north, var9.open, var10);
                        }
                     }
                  }

                  squareEndUpdate();
               }
            }
         }
      }

      chunkEndUpdate();
   }

   public static void update() {
      if (newLightingMethod) {
         if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
            checkLights();
            GameTime var0 = GameTime.getInstance();
            RenderSettings var1 = RenderSettings.getInstance();
            boolean var2 = var0.getNightsSurvived() < SandboxOptions.instance.getElecShutModifier();
            boolean var3 = GameTime.getInstance().getNight() < 0.5F;
            if (var2 != bWasElecShut || var3 != bWasNight) {
               bWasElecShut = var2;
               bWasNight = var3;
               IsoGridSquare.RecalcLightTime = -1;
               var0.lightSourceUpdate = 100.0F;
            }

            for(int var4 = 0; var4 < IsoPlayer.numPlayers; ++var4) {
               IsoChunkMap var5 = IsoWorld.instance.CurrentCell.ChunkMap[var4];
               if (var5 != null && !var5.ignore) {
                  RenderSettings.PlayerRenderSettings var6 = var1.getPlayerSettings(var4);
                  stateBeginUpdate(var4, var5.getWorldXMin(), var5.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
                  updatePlayer(var4);
                  stateEndFrame(var6.getRmod(), var6.getGmod(), var6.getBmod(), var6.getAmbient(), var6.getNight(), var6.getViewDistance(), var0.getViewDistMax(), LosUtil.cachecleared[var4], var0.lightSourceUpdate);
                  LosUtil.cachecleared[var4] = false;

                  for(int var7 = 0; var7 < IsoChunkMap.ChunkGridWidth; ++var7) {
                     for(int var8 = 0; var8 < IsoChunkMap.ChunkGridWidth; ++var8) {
                        IsoChunk var9 = var5.getChunk(var8, var7);
                        if (var9 != null && var9.lightCheck[var4]) {
                           updateChunk(var9);
                           var9.lightCheck[var4] = false;
                        }

                        if (var9 != null) {
                           var9.bLightingNeverDone[var4] = !chunkLightingDone(var9.wx, var9.wy);
                        }
                     }
                  }

                  stateEndUpdate();
                  updateCounter[var4] = stateUpdateCounter(var4);
                  if (var0.lightSourceUpdate > 0.0F && IsoPlayer.players[var4] != null) {
                     IsoPlayer.players[var4].dirtyRecalcGridStackTime = 20.0F;
                  }
               }
            }

            var0.lightSourceUpdate = 0.0F;
         }
      }
   }

   public static void getTorches(ArrayList var0) {
      if (newLightingMethod) {
         var0.addAll(torches);
      }
   }

   public static void stop() {
      torches.clear();
      JNILights.clear();
      destroy();

      for(int var0 = 0; var0 < updateCounter.length; ++var0) {
         updateCounter[var0] = -1;
      }

      bWasElecShut = false;
      bWasNight = false;
   }

   public static native void configure(float var0);

   public static native void scrollLeft(int var0);

   public static native void scrollRight(int var0);

   public static native void scrollUp(int var0);

   public static native void scrollDown(int var0);

   public static native void stateBeginUpdate(int var0, int var1, int var2, int var3, int var4);

   public static native void stateEndFrame(float var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, float var8);

   public static native void stateEndUpdate();

   public static native int stateUpdateCounter(int var0);

   public static native void teleport(int var0, int var1, int var2);

   public static native void DoLightingUpdateNew(long var0);

   public static native boolean WaitingForMain();

   public static native void playerBeginUpdate();

   public static native void playerSet(float var0, float var1, float var2, float var3, float var4, boolean var5, boolean var6, boolean var7, boolean var8, float var9, float var10, float var11);

   public static native void playerEndUpdate();

   public static native boolean chunkLightingDone(int var0, int var1);

   public static native void chunkBeginUpdate(int var0, int var1);

   public static native void chunkEndUpdate();

   public static native void squareSetNull(int var0, int var1, int var2);

   public static native void squareBeginUpdate(int var0, int var1, int var2);

   public static native void squareSet(boolean var0, boolean var1, boolean var2, boolean var3, boolean var4, int var5, int var6, int var7);

   public static native void squareAddCurtain(int var0, boolean var1);

   public static native void squareAddDoor(boolean var0, boolean var1, boolean var2);

   public static native void squareAddThumpable(boolean var0, boolean var1, boolean var2, boolean var3);

   public static native void squareAddWindow(boolean var0, boolean var1, boolean var2);

   public static native void squareEndUpdate();

   public static native int getVertLight(int var0, int var1, int var2, int var3, int var4);

   public static native float getLightInfo(int var0, int var1, int var2, int var3, int var4);

   public static native float getDarkMulti(int var0, int var1, int var2, int var3);

   public static native float getTargetDarkMulti(int var0, int var1, int var2, int var3);

   public static native boolean getSeen(int var0, int var1, int var2, int var3);

   public static native boolean getCanSee(int var0, int var1, int var2, int var3);

   public static native boolean getCouldSee(int var0, int var1, int var2, int var3);

   public static native boolean getSquareLighting(int var0, int var1, int var2, int var3, int[] var4);

   public static native void addLight(int var0, int var1, int var2, int var3, int var4, float var5, float var6, float var7, int var8, boolean var9);

   public static native void addTempLight(int var0, int var1, int var2, int var3, int var4, float var5, float var6, float var7, int var8);

   public static native void removeLight(int var0);

   public static native void setLightActive(int var0, boolean var1);

   public static native void setLightColor(int var0, float var1, float var2, float var3);

   public static native void addRoomLight(int var0, int var1, int var2, int var3, int var4, int var5, int var6, boolean var7);

   public static native void removeRoomLight(int var0);

   public static native void setRoomLightActive(int var0, boolean var1);

   public static native void updateTorch(int var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8, float var9, int var10);

   public static native void removeTorch(int var0);

   public static native void destroy();

   public static final class JNILighting implements IsoGridSquare.ILighting {
      private int playerIndex;
      private int x;
      private int y;
      private int z;
      private ColorInfo lightInfo = new ColorInfo();
      private byte vis;
      private float cacheDarkMulti;
      private float cacheTargetDarkMulti;
      private int[] cacheVertLight;
      private int updateTick = -1;
      private static final int[] lightInts = new int[12];

      public JNILighting(int var1, int var2, int var3, int var4) {
         this.playerIndex = var1;
         this.x = var2;
         this.y = var3;
         this.z = var4;
         this.cacheDarkMulti = 0.0F;
         this.cacheTargetDarkMulti = 0.0F;
         this.cacheVertLight = new int[8];

         for(int var5 = 0; var5 < 8; ++var5) {
            this.cacheVertLight[var5] = 0;
         }

      }

      public int lightverts(int var1) {
         return this.cacheVertLight[var1];
      }

      public float lampostTotalR() {
         return 0.0F;
      }

      public float lampostTotalG() {
         return 0.0F;
      }

      public float lampostTotalB() {
         return 0.0F;
      }

      public boolean bSeen() {
         this.update();
         return (this.vis & 1) != 0;
      }

      public boolean bCanSee() {
         this.update();
         return (this.vis & 2) != 0;
      }

      public boolean bCouldSee() {
         this.update();
         return (this.vis & 4) != 0;
      }

      public float darkMulti() {
         return this.cacheDarkMulti;
      }

      public float targetDarkMulti() {
         return this.cacheTargetDarkMulti;
      }

      public ColorInfo lightInfo() {
         this.update();
         return this.lightInfo;
      }

      public void lightverts(int var1, int var2) {
         throw new IllegalStateException();
      }

      public void lampostTotalR(float var1) {
         throw new IllegalStateException();
      }

      public void lampostTotalG(float var1) {
         throw new IllegalStateException();
      }

      public void lampostTotalB(float var1) {
         throw new IllegalStateException();
      }

      public void bSeen(boolean var1) {
         throw new IllegalStateException();
      }

      public void bCanSee(boolean var1) {
         throw new IllegalStateException();
      }

      public void bCouldSee(boolean var1) {
         throw new IllegalStateException();
      }

      public void darkMulti(float var1) {
         throw new IllegalStateException();
      }

      public void targetDarkMulti(float var1) {
         throw new IllegalStateException();
      }

      public void setPos(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }

      public void reset() {
         this.updateTick = -1;
      }

      private void update() {
         if (LightingJNI.updateCounter[this.playerIndex] != -1) {
            if (this.updateTick != LightingJNI.updateCounter[this.playerIndex] && LightingJNI.getSquareLighting(this.playerIndex, this.x, this.y, this.z, lightInts)) {
               IsoPlayer var1 = IsoPlayer.players[this.playerIndex];
               boolean var2 = (this.vis & 1) != 0;
               byte var3 = 0;
               int var13 = var3 + 1;
               this.vis = (byte)(lightInts[var3] & 7);
               this.lightInfo.r = (float)(lightInts[var13] & 255) / 255.0F;
               this.lightInfo.g = (float)(lightInts[var13] >> 8 & 255) / 255.0F;
               this.lightInfo.b = (float)(lightInts[var13++] >> 16 & 255) / 255.0F;
               this.cacheDarkMulti = (float)lightInts[var13++] / 100000.0F;
               this.cacheTargetDarkMulti = (float)lightInts[var13++] / 100000.0F;
               float var4 = 1.0F;
               float var5 = 1.0F;
               int var6;
               int var7;
               if (var1 != null) {
                  var6 = this.z - (int)var1.z;
                  if (var6 == -1) {
                     var4 = 1.0F;
                     var5 = 0.85F;
                  } else if (var6 < -1) {
                     var4 = 0.85F;
                     var5 = 0.85F;
                  }

                  if ((this.vis & 2) == 0 && (this.vis & 4) != 0) {
                     var7 = (int)var1.x;
                     int var8 = (int)var1.y;
                     int var9 = this.x - var7;
                     int var10 = this.y - var8;
                     if (var1.dir != IsoDirections.Max && Math.abs(var9) <= 2 && Math.abs(var10) <= 2) {
                        int[] var11 = LightingJNI.ForcedVis[var1.dir.index()];

                        for(int var12 = 0; var12 < var11.length; var12 += 2) {
                           if (var9 == var11[var12] && var10 == var11[var12 + 1]) {
                              this.vis = (byte)(this.vis | 2);
                              break;
                           }
                        }
                     }
                  }
               }

               float var16;
               float var18;
               float var19;
               for(var6 = 0; var6 < 4; ++var6) {
                  var7 = lightInts[var13++];
                  var16 = (float)(var7 & 255) * var5;
                  var18 = (float)((var7 & '\uff00') >> 8) * var5;
                  var19 = (float)((var7 & 16711680) >> 16) * var5;
                  this.cacheVertLight[var6] = (int)var16 << 0 | (int)var18 << 8 | (int)var19 << 16 | -16777216;
               }

               for(var6 = 4; var6 < 8; ++var6) {
                  var7 = lightInts[var13++];
                  var16 = (float)(var7 & 255) * var4;
                  var18 = (float)((var7 & '\uff00') >> 8) * var4;
                  var19 = (float)((var7 & 16711680) >> 16) * var4;
                  this.cacheVertLight[var6] = (int)var16 << 0 | (int)var18 << 8 | (int)var19 << 16 | -16777216;
               }

               this.updateTick = LightingJNI.updateCounter[this.playerIndex];
               if (!var2 && (this.vis & 1) != 0) {
                  IsoGridSquare var14 = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
                  if (var14 == null) {
                     return;
                  }

                  IsoRoom var15 = var14.getRoom();

                  assert !GameServer.bServer;

                  if (var15 != null && var15.def != null && !var15.def.bExplored) {
                     byte var17 = 10;
                     if (var1 != null && var1.getBuilding() == var15.building) {
                        var17 = 50;
                     }

                     if (var1 != null && IsoUtils.DistanceManhatten(var1.x, var1.y, (float)this.x, (float)this.y) < (float)var17 && this.z == (int)var1.z) {
                        var15.def.bExplored = true;
                        var15.onSee();
                        var15.seen = 0;
                     }
                  }

                  if (!GameClient.bClient) {
                     Meta.instance.dealWithSquareSeen(var14);
                  }
               }
            }

         }
      }
   }
}
