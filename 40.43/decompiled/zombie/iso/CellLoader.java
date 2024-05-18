package zombie.iso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import zombie.FrameLoader;
import zombie.Lua.LuaEventManager;
import zombie.ai.astar.AStarPathMap;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.ChunkRevisions;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;

public class CellLoader {
   static int wanderX = 0;
   static int wanderY = 0;
   static IsoRoom wanderRoom = null;
   static HashSet missingTiles = new HashSet();
   public static Stack isoObjectCache = new Stack();
   public static Stack isoTreeCache = new Stack();

   public static void DoTileObjectCreation(IsoSprite var0, IsoObjectType var1, IsoGridSquare var2, IsoCell var3, int var4, int var5, int var6, Stack var7, boolean var8, String var9) throws NumberFormatException {
      Object var10 = null;
      boolean var11 = false;
      if (var8) {
         var11 = true;
      }

      if (var2 != null) {
         if (var1 != IsoObjectType.doorW && var1 != IsoObjectType.doorN) {
            float var14;
            if (var1 == IsoObjectType.lightswitch) {
               var10 = new IsoLightSwitch(var3, var2, var0, var2.getRoomID());
               AddObject(var2, (IsoObject)var10);
               GameClient.instance.objectSyncReq.putRequest(var2, (IsoObject)var10);
               if (((IsoObject)var10).sprite.getProperties().Is("lightR")) {
                  float var26 = Float.parseFloat(((IsoObject)var10).sprite.getProperties().Val("lightR")) / 255.0F;
                  float var24 = Float.parseFloat(((IsoObject)var10).sprite.getProperties().Val("lightG")) / 255.0F;
                  var14 = Float.parseFloat(((IsoObject)var10).sprite.getProperties().Val("lightB")) / 255.0F;
                  IsoLightSource var25 = new IsoLightSource(((IsoObject)var10).square.getX(), ((IsoObject)var10).square.getY(), ((IsoObject)var10).square.getZ(), var26, var24, var14, 10);
                  var25.bActive = true;
                  var25.bHydroPowered = true;
                  var25.switches.add((IsoLightSwitch)var10);
                  ((IsoLightSwitch)var10).lights.add(var25);
               } else {
                  ((IsoLightSwitch)var10).lightRoom = true;
               }
            } else if (var1 != IsoObjectType.curtainN && var1 != IsoObjectType.curtainS && var1 != IsoObjectType.curtainE && var1 != IsoObjectType.curtainW) {
               if (!var0.getProperties().Is(IsoFlagType.windowW) && !var0.getProperties().Is(IsoFlagType.windowN)) {
                  if (var0.getProperties().Is("container") && var0.getProperties().Val("container").equals("barbecue")) {
                     var10 = new IsoBarbecue(var3, var2, var0);
                     AddObject(var2, (IsoObject)var10);
                  } else if (var0.getProperties().Is("container") && var0.getProperties().Val("container").equals("fireplace")) {
                     var10 = new IsoFireplace(var3, var2, var0);
                     AddObject(var2, (IsoObject)var10);
                  } else if (var0.getProperties().Is("container") && var0.getProperties().Val("container").equals("woodstove")) {
                     var10 = new IsoFireplace(var3, var2, var0);
                     AddObject(var2, (IsoObject)var10);
                  } else if (var0.getProperties().Is("container") && (var0.getProperties().Val("container").equals("stove") || var0.getProperties().Val("container").equals("microwave"))) {
                     var10 = new IsoStove(var3, var2, var0);
                     AddObject(var2, (IsoObject)var10);
                     GameClient.instance.objectSyncReq.putRequest(var2, (IsoObject)var10);
                  } else if (var1 == IsoObjectType.jukebox) {
                     var10 = new IsoJukebox(var3, var2, var0);
                     ((IsoObject)var10).OutlineOnMouseover = true;
                     var3.getStaticUpdaterObjectList().add(var10);
                     AddObject(var2, (IsoObject)var10);
                  } else if (var1 == IsoObjectType.radio) {
                     var10 = new IsoRadio(var3, var2, var0);
                     AddObject(var2, (IsoObject)var10);
                  } else {
                     String var17;
                     if (var0.getProperties().Is("signal")) {
                        var17 = var0.getProperties().Val("signal");
                        if ("radio".equals(var17)) {
                           var10 = new IsoRadio(var3, var2, var0);
                        } else if ("tv".equals(var17)) {
                           var10 = new IsoTelevision(var3, var2, var0);
                        }

                        AddObject(var2, (IsoObject)var10);
                     } else {
                        IsoObject var18;
                        if (var0.getProperties().Is(IsoFlagType.WallOverlay)) {
                           var18 = null;
                           if (var0.getProperties().Is(IsoFlagType.attachedW)) {
                              var18 = var2.getWall(false);
                           } else if (var0.getProperties().Is(IsoFlagType.attachedN)) {
                              var18 = var2.getWall(true);
                           } else {
                              for(int var22 = var2.getObjects().size() - 1; var22 >= 0; --var22) {
                                 IsoObject var23 = (IsoObject)var2.getObjects().get(var22);
                                 if (var23.sprite.getProperties().Is(IsoFlagType.cutW) || var23.sprite.getProperties().Is(IsoFlagType.cutN)) {
                                    var18 = var23;
                                    break;
                                 }
                              }
                           }

                           if (var18 != null) {
                              if (var18.AttachedAnimSprite == null) {
                                 var18.AttachedAnimSprite = new ArrayList(4);
                                 var18.AttachedAnimSpriteActual = new ArrayList(4);
                              }

                              var18.AttachedAnimSprite.add(IsoSpriteInstance.get(var0));
                              var18.AttachedAnimSpriteActual.add(var0);
                           } else {
                              IsoObject var20;
                              if (isoObjectCache.isEmpty()) {
                                 var20 = new IsoObject(var3, var2, var0);
                              } else {
                                 var20 = (IsoObject)isoObjectCache.pop();
                                 var20.sx = 0;
                              }

                              var20.sprite = var0;
                              var20.square = var2;
                              AddObject(var2, var20);
                           }

                           return;
                        }

                        if (var0.getProperties().Is(IsoFlagType.FloorOverlay)) {
                           var18 = var2.getFloor();
                           if (var18 != null) {
                              if (var18.AttachedAnimSprite == null) {
                                 var18.AttachedAnimSprite = new ArrayList(4);
                                 var18.AttachedAnimSpriteActual = new ArrayList(4);
                              }

                              var18.AttachedAnimSpriteActual.add(var0);
                              var18.AttachedAnimSprite.add(IsoSpriteInstance.get(var0));
                           }
                        } else if (var1 == IsoObjectType.tree) {
                           if (var0.getName() != null && var0.getName().startsWith("vegetation_trees")) {
                              var18 = var2.getFloor();
                              if (var18 == null || var18.getSprite() == null || var18.getSprite().getName() == null || !var18.getSprite().getName().startsWith("blends_natural")) {
                                 DebugLog.log("ERROR: removed tree at " + var2.x + "," + var2.y + "," + var2.z + " because floor is not blends_natural");
                                 return;
                              }
                           }

                           if (isoTreeCache.isEmpty()) {
                              var10 = new IsoTree(var2, var0);
                           } else {
                              var10 = (IsoObject)isoTreeCache.pop();
                              ((IsoObject)var10).sprite = var0;
                              ((IsoObject)var10).square = var2;
                              ((IsoObject)var10).sx = 0;
                              ((IsoTree)var10).initTree();
                           }

                           for(int var19 = 0; var19 < var2.getObjects().size(); ++var19) {
                              IsoObject var21 = (IsoObject)var2.getObjects().get(var19);
                              if (var21 instanceof IsoTree) {
                                 var2.getObjects().remove(var19);
                                 var21.reset();
                                 isoTreeCache.push((IsoTree)var21);
                                 break;
                              }
                           }

                           AddObject(var2, (IsoObject)var10);
                        } else {
                           if ((var0.CurrentAnim.Frames.isEmpty() || ((IsoDirectionFrame)var0.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) == null) && !GameServer.bServer) {
                              if (!missingTiles.contains(var9)) {
                                 if (Core.bDebug) {
                                    DebugLog.log("ERROR: missing tile " + var9);
                                 }

                                 missingTiles.add(var9);
                              }

                              var0.LoadFramesNoDirPageSimple(Core.bDebug ? "media/ui/missing-tile-debug.png" : "media/ui/missing-tile.png");
                              if (var0.CurrentAnim.Frames.isEmpty() || ((IsoDirectionFrame)var0.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) == null) {
                                 return;
                              }
                           }

                           var17 = GameServer.bServer ? null : ((IsoDirectionFrame)var0.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
                           boolean var13 = true;
                           if (!GameServer.bServer && var17.contains("TileObjectsExt") && (var17.contains("_5") || var17.contains("_6") || var17.contains("_7") || var17.contains("_8"))) {
                              var10 = new IsoWheelieBin(var3, var4, var5, var6);
                              if (var17.contains("_5")) {
                                 ((IsoObject)var10).dir = IsoDirections.S;
                              }

                              if (var17.contains("_6")) {
                                 ((IsoObject)var10).dir = IsoDirections.W;
                              }

                              if (var17.contains("_7")) {
                                 ((IsoObject)var10).dir = IsoDirections.N;
                              }

                              if (var17.contains("_8")) {
                                 ((IsoObject)var10).dir = IsoDirections.E;
                              }

                              var13 = false;
                           }

                           if (var13) {
                              if (isoObjectCache.isEmpty()) {
                                 var10 = new IsoObject(var3, var2, var0);
                              } else {
                                 var10 = (IsoObject)isoObjectCache.pop();
                                 ((IsoObject)var10).sx = 0;
                              }

                              ((IsoObject)var10).sprite = var0;
                              ((IsoObject)var10).square = var2;
                              AddObject(var2, (IsoObject)var10);
                              if (((IsoObject)var10).sprite.getProperties().Is("lightR")) {
                                 var14 = Float.parseFloat(((IsoObject)var10).sprite.getProperties().Val("lightR"));
                                 float var15 = Float.parseFloat(((IsoObject)var10).sprite.getProperties().Val("lightG"));
                                 float var16 = Float.parseFloat(((IsoObject)var10).sprite.getProperties().Val("lightB"));
                                 var3.getLamppostPositions().add(new IsoLightSource(((IsoObject)var10).square.getX(), ((IsoObject)var10).square.getY(), ((IsoObject)var10).square.getZ(), var14, var15, var16, 8));
                              }
                           }
                        }
                     }
                  }
               } else {
                  var10 = new IsoWindow(var3, var2, var0, var0.getProperties().Is(IsoFlagType.windowN));
                  AddSpecialObject(var2, (IsoObject)var10);
                  GameClient.instance.objectSyncReq.putRequest(var2, (IsoObject)var10);
               }
            } else {
               boolean var12 = Integer.parseInt(var9.substring(var9.lastIndexOf("_") + 1)) % 8 <= 3;
               var10 = new IsoCurtain(var3, var2, var0, var1 == IsoObjectType.curtainN || var1 == IsoObjectType.curtainS, var12);
               AddSpecialObject(var2, (IsoObject)var10);
               GameClient.instance.objectSyncReq.putRequest(var2, (IsoObject)var10);
            }
         } else {
            var10 = new IsoDoor(var3, var2, var0, var1 == IsoObjectType.doorN);
            AddSpecialObject(var2, (IsoObject)var10);
            GameClient.instance.objectSyncReq.putRequest(var2, (IsoObject)var10);
         }

         if (var10 != null) {
            ((IsoObject)var10).tile = var9;
            ((IsoObject)var10).createContainersFromSpriteProperties();
            if (Rand.Next(70) == 0) {
            }

            if (((IsoObject)var10).sprite.getProperties().Is(IsoFlagType.vegitation)) {
               ((IsoObject)var10).tintr = 0.7F + (float)Rand.Next(30) / 100.0F;
               ((IsoObject)var10).tintg = 0.7F + (float)Rand.Next(30) / 100.0F;
               ((IsoObject)var10).tintb = 0.7F + (float)Rand.Next(30) / 100.0F;
               if (Rand.Next(10) == 0) {
               }
            }
         }

      }
   }

   public static boolean LoadCellBinaryChunk(IsoCell var0, int var1, int var2, IsoChunk var3) {
      Integer var4 = var1 / 30;
      Integer var5 = var2 / 30;
      float var6 = (float)var1;
      float var7 = (float)var2;
      boolean var8 = false;
      var6 /= 30.0F;
      var7 /= 30.0F;
      int var9 = (int)Math.floor((double)var6);
      int var10 = (int)Math.floor((double)var7);
      String var11 = "world_" + var1 / 30 + "_" + var2 / 30 + ".lotpack";
      if (!IsoLot.InfoFileNames.containsKey(var11)) {
         DebugLog.log("LoadCellBinaryChunk: NO SUCH LOT " + var11);
         return false;
      } else {
         File var12 = new File((String)IsoLot.InfoFileNames.get(var11));
         if (var12.exists()) {
            IsoLot var13 = IsoLot.get(var1 / 30, var2 / 30, var1, var2, var3);
            var0.PlaceLot(var13, 0, 0, 0, var3, var1, var2, false);
            IsoLot.put(var13);
            return true;
         } else {
            DebugLog.log("LoadCellBinaryChunk: NO SUCH LOT " + var11);
            return false;
         }
      }
   }

   public static IsoCell LoadCellBinaryChunk(IsoSpriteManager var0, int var1, int var2) throws IOException {
      wanderX = 0;
      wanderY = 0;
      wanderRoom = null;
      wanderX = 0;
      wanderY = 0;
      IsoCell var3 = new IsoCell(var0, 300, 300);
      int var4 = IsoPlayer.numPlayers;
      if (!FrameLoader.bDedicated) {
         var4 = 1;
      }

      int var12;
      if (!GameServer.bServer) {
         if (ChunkRevisions.USE_CHUNK_REVISIONS) {
            if (GameClient.bClient) {
               ByteBufferWriter var5 = GameClient.connection.startPacket();
               PacketTypes.doPacket((short)24, var5);
               var5.putInt(var1);
               var5.putInt(var2);
               var5.putInt(IsoChunkMap.ChunkGridWidth);
               GameClient.connection.endPacketImmediate();
               ChunkRevisions.instance.requestStartupChunkRevisions(var1 - ChunkRevisions.UpdateArea / 2, var2 - ChunkRevisions.UpdateArea / 2, ChunkRevisions.UpdateArea, ChunkRevisions.UpdateArea);
            }
         } else if (GameClient.bClient) {
            WorldStreamer.instance.requestLargeAreaZip(var1, var2, IsoChunkMap.ChunkGridWidth / 2 + 2);
            IsoChunk.bDoServerRequests = false;
         }

         for(var12 = 0; var12 < var4; ++var12) {
            var3.ChunkMap[var12].setInitialPos(var1, var2);
            IsoPlayer.assumedPlayer = var12;
            IsoChunkMap var10001 = var3.ChunkMap[var12];
            int var6 = var1 - IsoChunkMap.ChunkGridWidth / 2;
            var10001 = var3.ChunkMap[var12];
            int var7 = var2 - IsoChunkMap.ChunkGridWidth / 2;
            var10001 = var3.ChunkMap[var12];
            int var8 = var1 + IsoChunkMap.ChunkGridWidth / 2 + 1;
            var10001 = var3.ChunkMap[var12];
            int var9 = var2 + IsoChunkMap.ChunkGridWidth / 2 + 1;

            for(int var10 = var6; var10 < var8; ++var10) {
               for(int var11 = var7; var11 < var9; ++var11) {
                  if (IsoWorld.instance.getMetaGrid().isValidChunk(var10, var11)) {
                     var3.ChunkMap[var12].LoadChunk(var10, var11, var10 - var6, var11 - var7);
                  }
               }
            }
         }
      }

      IsoPlayer.assumedPlayer = 0;
      ScriptManager.instance.Trigger("OnPostMapLoad");
      LuaEventManager.triggerEvent("OnPostMapLoad", var3, var1, var2);
      ConnectMultitileObjects(var3);
      if (!GameServer.bServer) {
         var3.setPathMap(new AStarPathMap(var3));
      }

      for(var12 = 0; var12 < var3.roomDefs.size(); ++var12) {
         IsoGridSquare var13 = var3.getGridSquare(((IsoCell.Zone)var3.roomDefs.get(var12)).X, ((IsoCell.Zone)var3.roomDefs.get(var12)).Y, ((IsoCell.Zone)var3.roomDefs.get(var12)).Z);
         if (var13 != null && var13.room != null) {
            var13.room.RoomDef = ((IsoCell.Zone)var3.roomDefs.get(var12)).Name;
         }
      }

      return var3;
   }

   private static void RecurseMultitileObjects(IsoCell var0, IsoGridSquare var1, IsoGridSquare var2, ArrayList var3) {
      Iterator var4 = var2.getMovingObjects().iterator();
      IsoPushableObject var5 = null;
      boolean var6 = false;

      while(var4 != null && var4.hasNext()) {
         IsoMovingObject var7 = (IsoMovingObject)var4.next();
         if (var7 instanceof IsoPushableObject) {
            IsoPushableObject var8 = (IsoPushableObject)var7;
            int var9 = var1.getX() - var2.getX();
            int var10 = var1.getY() - var2.getY();
            int var11;
            if (var10 != 0 && var7.sprite.getProperties().Is("connectY")) {
               var11 = Integer.parseInt(var7.sprite.getProperties().Val("connectY"));
               if (var11 == var10) {
                  var8.connectList = var3;
                  var3.add(var8);
                  var5 = var8;
                  var6 = false;
                  break;
               }
            }

            if (var9 != 0 && var7.sprite.getProperties().Is("connectX")) {
               var11 = Integer.parseInt(var7.sprite.getProperties().Val("connectX"));
               if (var11 == var9) {
                  var8.connectList = var3;
                  var3.add(var8);
                  var5 = var8;
                  var6 = true;
                  break;
               }
            }
         }
      }

      if (var5 != null) {
         int var12;
         IsoGridSquare var13;
         if (var5.sprite.getProperties().Is("connectY") && var6) {
            var12 = Integer.parseInt(var5.sprite.getProperties().Val("connectY"));
            var13 = var0.getGridSquare(var5.getCurrentSquare().getX(), var5.getCurrentSquare().getY() + var12, var5.getCurrentSquare().getZ());
            RecurseMultitileObjects(var0, var5.getCurrentSquare(), var13, var5.connectList);
         }

         if (var5.sprite.getProperties().Is("connectX") && !var6) {
            var12 = Integer.parseInt(var5.sprite.getProperties().Val("connectX"));
            var13 = var0.getGridSquare(var5.getCurrentSquare().getX() + var12, var5.getCurrentSquare().getY(), var5.getCurrentSquare().getZ());
            RecurseMultitileObjects(var0, var5.getCurrentSquare(), var13, var5.connectList);
         }
      }

   }

   private static void ConnectMultitileObjects(IsoCell var0) {
      Iterator var1 = var0.getObjectList().iterator();

      while(var1 != null && var1.hasNext()) {
         IsoMovingObject var2 = (IsoMovingObject)var1.next();
         if (var2 instanceof IsoPushableObject) {
            IsoPushableObject var3 = (IsoPushableObject)var2;
            if ((var2.sprite.getProperties().Is("connectY") || var2.sprite.getProperties().Is("connectX")) && var3.connectList == null) {
               var3.connectList = new ArrayList();
               var3.connectList.add(var3);
               int var4;
               IsoGridSquare var5;
               if (var2.sprite.getProperties().Is("connectY")) {
                  var4 = Integer.parseInt(var2.sprite.getProperties().Val("connectY"));
                  var5 = var0.getGridSquare(var2.getCurrentSquare().getX(), var2.getCurrentSquare().getY() + var4, var2.getCurrentSquare().getZ());
                  if (var5 == null) {
                     boolean var6 = false;
                  }

                  RecurseMultitileObjects(var0, var3.getCurrentSquare(), var5, var3.connectList);
               }

               if (var2.sprite.getProperties().Is("connectX")) {
                  var4 = Integer.parseInt(var2.sprite.getProperties().Val("connectX"));
                  var5 = var0.getGridSquare(var2.getCurrentSquare().getX() + var4, var2.getCurrentSquare().getY(), var2.getCurrentSquare().getZ());
                  RecurseMultitileObjects(var0, var3.getCurrentSquare(), var5, var3.connectList);
               }
            }
         }
      }

   }

   private static void AddObject(IsoGridSquare var0, IsoObject var1) {
      var0.getObjects().add(var1);
   }

   private static void AddSpecialObject(IsoGridSquare var0, IsoObject var1) {
      var0.getObjects().add(var1);
      var0.getSpecialObjects().add(var1);
   }
}
