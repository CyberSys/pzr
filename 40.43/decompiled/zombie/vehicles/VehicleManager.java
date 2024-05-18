package zombie.vehicles;

import gnu.trove.list.array.TShortArrayList;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.physics.Transform;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.scripting.objects.VehicleScript;

public class VehicleManager {
   public static VehicleManager instance;
   private final VehicleIDMap IDToVehicle;
   private final ArrayList vehicles;
   private boolean idMapDirty;
   private Transform tempTransform;
   private ArrayList send;
   private TShortArrayList vehiclesWaitUpdates;
   public static short physicsDelay = 100;
   public UdpConnection[] connected;
   private final float[] tempFloats;
   private final VehicleManager.PosUpdateVars posUpdateVars;
   private UpdateLimit vehiclesWaitUpdatesFrequency;
   private BaseVehicle tempVehicle;
   private ArrayList oldModels;
   private ArrayList curModels;
   private static UpdateLimit sendReqestGetPositionFrequency = new UpdateLimit(500L);

   public VehicleManager() {
      this.IDToVehicle = VehicleIDMap.instance;
      this.vehicles = new ArrayList();
      this.idMapDirty = true;
      this.tempTransform = new Transform();
      this.send = new ArrayList();
      this.vehiclesWaitUpdates = new TShortArrayList(128);
      this.connected = new UdpConnection[512];
      this.tempFloats = new float[23];
      this.posUpdateVars = new VehicleManager.PosUpdateVars();
      this.vehiclesWaitUpdatesFrequency = new UpdateLimit(1000L);
      this.oldModels = new ArrayList();
      this.curModels = new ArrayList();
   }

   private void noise(String var1) {
      if (Core.bDebug) {
      }

   }

   public void registerVehicle(BaseVehicle var1) {
      this.IDToVehicle.put(var1.VehicleID, var1);
      this.idMapDirty = true;
   }

   public void unregisterVehicle(BaseVehicle var1) {
      this.IDToVehicle.remove(var1.VehicleID);
      this.idMapDirty = true;
   }

   public BaseVehicle getVehicleByID(short var1) {
      return this.IDToVehicle.get(var1);
   }

   public ArrayList getVehicles() {
      if (this.idMapDirty) {
         this.vehicles.clear();
         this.IDToVehicle.toArrayList(this.vehicles);
         this.idMapDirty = false;
      }

      return this.vehicles;
   }

   public void removeFromWorld(BaseVehicle var1) {
      if (var1.VehicleID != -1) {
         short var2 = var1.VehicleID;
         if (var1.trace) {
            this.noise("removeFromWorld vehicle id=" + var1.VehicleID);
         }

         this.unregisterVehicle(var1);
         if (GameServer.bServer) {
            for(int var3 = 0; var3 < GameServer.udpEngine.connections.size(); ++var3) {
               UdpConnection var4 = (UdpConnection)GameServer.udpEngine.connections.get(var3);
               if (var1.connectionState[var4.index] != null) {
                  ByteBufferWriter var5 = var4.startPacket();
                  PacketTypes.doPacket((short)5, var5);
                  var5.bb.put((byte)8);
                  var5.bb.putShort(var1.VehicleID);
                  var4.endPacketImmediate();
               }
            }
         }

         if (GameClient.bClient) {
            var1.serverRemovedFromWorld = false;
            if (var1.interpolation != null) {
               var1.interpolation.poolData();
            }
         }
      }

   }

   public void serverUpdate() {
      ArrayList var1 = IsoWorld.instance.CurrentCell.getVehicles();

      int var2;
      for(var2 = 0; var2 < this.connected.length; ++var2) {
         int var3;
         if (this.connected[var2] != null && !GameServer.udpEngine.connections.contains(this.connected[var2])) {
            this.noise("vehicles: dropped connection " + var2);

            for(var3 = 0; var3 < var1.size(); ++var3) {
               ((BaseVehicle)var1.get(var3)).connectionState[var2] = null;
            }

            this.connected[var2] = null;
         } else {
            for(var3 = 0; var3 < var1.size(); ++var3) {
               if (((BaseVehicle)var1.get(var3)).connectionState[var2] != null) {
                  BaseVehicle.ServerVehicleState var10000 = ((BaseVehicle)var1.get(var3)).connectionState[var2];
                  var10000.flags |= ((BaseVehicle)var1.get(var3)).updateFlags;
               }
            }
         }
      }

      for(var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
         UdpConnection var6 = (UdpConnection)GameServer.udpEngine.connections.get(var2);
         this.sendVehicles(var6);
         this.connected[var6.index] = var6;
      }

      for(var2 = 0; var2 < var1.size(); ++var2) {
         BaseVehicle var7 = (BaseVehicle)var1.get(var2);
         if ((var7.updateFlags & 19440) != 0) {
            for(int var4 = 0; var4 < var7.getPartCount(); ++var4) {
               VehiclePart var5 = var7.getPartByIndex(var4);
               var5.updateFlags = 0;
            }
         }

         var7.updateFlags = 0;
      }

   }

   private void sendVehicles(UdpConnection var1) {
      this.send.clear();
      ArrayList var2 = IsoWorld.instance.CurrentCell.getVehicles();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         BaseVehicle var4 = (BaseVehicle)var2.get(var3);
         if (var4.VehicleID == -1) {
            var4.VehicleID = this.IDToVehicle.allocateID();
            this.registerVehicle(var4);
         }

         boolean var5 = var1.vehicles.contains(var4.VehicleID);
         if (var5 && !var1.RelevantTo(var4.x, var4.y, (float)(var1.ReleventRange * 10) * 2.0F)) {
            DebugLog.log("removed out-of-bounds vehicle.id=" + var4.VehicleID + " connection=" + var1.index);
            var1.vehicles.remove(var4.VehicleID);
            var5 = false;
         }

         if (var5 || var1.ReleventTo(var4.x, var4.y)) {
            if (var4.connectionState[var1.index] == null) {
               var4.connectionState[var1.index] = new BaseVehicle.ServerVehicleState();
            }

            BaseVehicle.ServerVehicleState var6 = var4.connectionState[var1.index];
            if (!var5 || var6.shouldSend(var4)) {
               this.send.add(var4);
               var1.vehicles.add(var4.VehicleID);
            }
         }
      }

      if (!this.send.isEmpty()) {
         ByteBufferWriter var17 = var1.startPacket();
         PacketTypes.doPacket((short)5, var17);

         try {
            ByteBuffer var18 = var17.bb;
            var18.put((byte)5);
            var18.putShort((short)this.send.size());

            for(int var19 = 0; var19 < this.send.size(); ++var19) {
               BaseVehicle var20 = (BaseVehicle)this.send.get(var19);
               BaseVehicle.ServerVehicleState var7 = var20.connectionState[var1.index];
               var18.putShort(var20.VehicleID);
               var18.putShort(var7.flags);
               var18.putFloat(var20.x);
               var18.putFloat(var20.y);
               var18.putFloat(var20.z);
               int var8 = var18.position();
               var18.putShort((short)0);
               int var9 = var18.position();
               boolean var10 = (var7.flags & 1) != 0;
               int var13;
               int var22;
               int var26;
               if (var10) {
                  var20.updateFlags = (short)(var20.updateFlags & -2);
                  var18.putFloat(var20.x);
                  var18.putFloat(var20.y);
                  var18.putFloat(var20.z);
                  var20.netPlayerServerSendAuthorisation(var18);
                  var7.setAuthorization(var20);
                  var22 = var18.position();
                  var18.putShort((short)0);
                  var20.save(var18);
                  var26 = var18.position();
                  var18.position(var22);
                  var18.putShort((short)(var26 - var22));
                  var18.position(var26);
                  var13 = var18.position();
                  int var14 = var18.position() - var9;
                  var18.position(var8);
                  var18.putShort((short)var14);
                  var18.position(var13);
                  var7.x = var20.x;
                  var7.y = var20.y;
                  var7.z = var20.z;
                  var7.orient.set((Quaternionfc)var20.savedRot);
               } else {
                  if ((var7.flags & 16384) != 0) {
                     var20.netPlayerServerSendAuthorisation(var18);
                     var7.setAuthorization(var20);
                  }

                  if ((var7.flags & 2) != 0) {
                     var18.putLong(WorldSimulation.instance.time);
                     var18.putFloat(var20.x);
                     var18.putFloat(var20.y);
                     var18.putFloat(var20.z);
                     Quaternionf var11 = var20.savedRot;
                     Transform var12 = var20.getWorldTransform(this.tempTransform);
                     var12.getRotation(var11);
                     var18.putFloat(var11.x);
                     var18.putFloat(var11.y);
                     var18.putFloat(var11.z);
                     var18.putFloat(var11.w);
                     var18.putFloat(var20.netLinearVelocity.x);
                     var18.putFloat(var20.netLinearVelocity.y);
                     var18.putFloat(var20.netLinearVelocity.z);
                     var18.putShort((short)var20.wheelInfo.length);

                     for(var13 = 0; var13 < var20.wheelInfo.length; ++var13) {
                        var18.putFloat(var20.wheelInfo[var13].steering);
                        var18.putFloat(var20.wheelInfo[var13].rotation);
                        var18.putFloat(var20.wheelInfo[var13].skidInfo);
                     }

                     var7.x = var20.x;
                     var7.y = var20.y;
                     var7.z = var20.z;
                     var7.orient.set((Quaternionfc)var20.savedRot);
                  }

                  if ((var7.flags & 4) != 0) {
                     var18.put((byte)var20.engineState.ordinal());
                  }

                  if ((var7.flags & 4096) != 0) {
                     var18.put((byte)(var20.isHotwired() ? 1 : 0));
                     var18.put((byte)(var20.isHotwiredBroken() ? 1 : 0));
                     var18.put((byte)(var20.isKeysInIgnition() ? 1 : 0));
                     var18.put((byte)(var20.isKeyIsOnDoor() ? 1 : 0));
                     InventoryItem var21 = var20.getCurrentKey();
                     if (var21 == null) {
                        var18.put((byte)0);
                     } else {
                        var18.put((byte)1);
                        var21.save(var18, false);
                     }
                  }

                  if ((var7.flags & 8) != 0) {
                     var18.put((byte)(var20.getHeadlightsOn() ? 1 : 0));
                     var18.put((byte)(var20.getStoplightsOn() ? 1 : 0));

                     for(var22 = 0; var22 < var20.getLightCount(); ++var22) {
                        var18.put((byte)(var20.getLightByIndex(var22).getLight().getActive() ? 1 : 0));
                     }
                  }

                  if ((var7.flags & 1024) != 0) {
                     var18.put((byte)(var20.soundHornOn ? 1 : 0));
                     var18.put((byte)(var20.soundBackMoveOn ? 1 : 0));
                     var18.put((byte)var20.lightbarLightsMode.get());
                     var18.put((byte)var20.lightbarSirenMode.get());
                  }

                  VehiclePart var23;
                  if ((var7.flags & 2048) != 0) {
                     for(var22 = 0; var22 < var20.getPartCount(); ++var22) {
                        var23 = var20.getPartByIndex(var22);
                        if ((var23.updateFlags & 2048) != 0) {
                           var18.put((byte)var22);
                           var18.putInt(var23.getCondition());
                        }
                     }

                     var18.put((byte)-1);
                  }

                  if ((var7.flags & 16) != 0) {
                     for(var22 = 0; var22 < var20.getPartCount(); ++var22) {
                        var23 = var20.getPartByIndex(var22);
                        if ((var23.updateFlags & 16) != 0) {
                           var18.put((byte)var22);
                           var23.getModData().save(var18);
                        }
                     }

                     var18.put((byte)-1);
                  }

                  InventoryItem var24;
                  if ((var7.flags & 32) != 0) {
                     for(var22 = 0; var22 < var20.getPartCount(); ++var22) {
                        var23 = var20.getPartByIndex(var22);
                        if ((var23.updateFlags & 32) != 0) {
                           var24 = var23.getInventoryItem();
                           if (var24 instanceof DrainableComboItem) {
                              var18.put((byte)var22);
                              var18.putFloat(((DrainableComboItem)var24).getUsedDelta());
                           }
                        }
                     }

                     var18.put((byte)-1);
                  }

                  if ((var7.flags & 128) != 0) {
                     for(var22 = 0; var22 < var20.getPartCount(); ++var22) {
                        var23 = var20.getPartByIndex(var22);
                        if ((var23.updateFlags & 128) != 0) {
                           var18.put((byte)var22);
                           var24 = var23.getInventoryItem();
                           if (var24 == null) {
                              var18.put((byte)0);
                           } else {
                              var18.put((byte)1);

                              try {
                                 var23.getInventoryItem().save(var18, false);
                              } catch (Exception var15) {
                                 var15.printStackTrace();
                              }
                           }
                        }
                     }

                     var18.put((byte)-1);
                  }

                  if ((var7.flags & 512) != 0) {
                     for(var22 = 0; var22 < var20.getPartCount(); ++var22) {
                        var23 = var20.getPartByIndex(var22);
                        if ((var23.updateFlags & 512) != 0) {
                           var18.put((byte)var22);
                           var23.getDoor().save(var18);
                        }
                     }

                     var18.put((byte)-1);
                  }

                  if ((var7.flags & 256) != 0) {
                     for(var22 = 0; var22 < var20.getPartCount(); ++var22) {
                        var23 = var20.getPartByIndex(var22);
                        if ((var23.updateFlags & 256) != 0) {
                           var18.put((byte)var22);
                           var23.getWindow().save(var18);
                        }
                     }

                     var18.put((byte)-1);
                  }

                  if ((var7.flags & 64) != 0) {
                     var18.put((byte)var20.models.size());

                     for(var22 = 0; var22 < var20.models.size(); ++var22) {
                        BaseVehicle.ModelInfo var25 = (BaseVehicle.ModelInfo)var20.models.get(var22);
                        var18.put((byte)var25.part.getIndex());
                        var18.put((byte)var25.part.getScriptPart().models.indexOf(var25.scriptModel));
                     }
                  }

                  if ((var7.flags & 8192) != 0) {
                     var18.putFloat((float)var20.engineSpeed);
                     var18.putFloat(var20.throttle);
                  }

                  var22 = var18.position();
                  var26 = var18.position() - var9;
                  var18.position(var8);
                  var18.putShort((short)var26);
                  var18.position(var22);
               }
            }

            var1.endPacketImmediate();
         } catch (Exception var16) {
            var1.cancelPacket();
            var16.printStackTrace();
         }

      }
   }

   public void serverPacket(ByteBuffer var1, UdpConnection var2) {
      byte var3 = var1.get();
      short var4;
      byte var5;
      BaseVehicle var6;
      int var7;
      UdpConnection var8;
      int var11;
      IsoPlayer var12;
      int var13;
      BaseVehicle var15;
      boolean var16;
      short var18;
      BaseVehicle var20;
      IsoGameCharacter var23;
      int var25;
      IsoPlayer var26;
      UdpConnection var27;
      switch(var3) {
      case 1:
         var4 = var1.getShort();
         var5 = var1.get();
         String var22 = GameWindow.ReadString(var1);
         var20 = this.IDToVehicle.get(var4);
         if (var20 != null) {
            var23 = var20.getCharacter(var5);
            if (var23 != null) {
               var20.setCharacterPosition(var23, var5, var22);
               this.sendPassengerPosition(var20, var5, var22, var2);
            }
         }
         break;
      case 2:
         var4 = var1.getShort();
         var5 = var1.get();
         var18 = var1.getShort();
         var20 = this.IDToVehicle.get(var4);
         if (var20 != null) {
            var23 = var20.getCharacter(var5);
            if (var23 != null) {
               var26 = (IsoPlayer)GameServer.IDToPlayerMap.get(Integer.valueOf(var18));
               DebugLog.log((var26 == null ? "unknown player" : var26.getUsername()) + " got in same seat as " + ((IsoPlayer)var23).getUsername());
               return;
            }

            for(var25 = 0; var25 < GameServer.udpEngine.connections.size(); ++var25) {
               var27 = (UdpConnection)GameServer.udpEngine.connections.get(var25);

               for(var11 = 0; var11 < 4; ++var11) {
                  var12 = var27.players[var11];
                  if (var12 != null && var12.OnlineID == var18) {
                     this.noise(var12.getUsername() + " got in vehicle " + var20.VehicleID + " seat " + var5);
                     var20.enter(var5, var12);
                     this.sendREnter(var20, var5, var12);
                     var20.authorizationServerOnSeat();
                     break;
                  }
               }
            }
         }
         break;
      case 3:
         var4 = var1.getShort();
         short var19 = var1.getShort();
         var6 = this.IDToVehicle.get(var4);
         if (var6 != null) {
            for(var7 = 0; var7 < GameServer.udpEngine.connections.size(); ++var7) {
               var8 = (UdpConnection)GameServer.udpEngine.connections.get(var7);

               for(var25 = 0; var25 < 4; ++var25) {
                  IsoPlayer var28 = var8.players[var25];
                  if (var28 != null && var28.OnlineID == var19) {
                     var6.exit(var28);
                     this.sendRExit(var6, var28);
                     var6.authorizationServerOnSeat();
                     break;
                  }
               }
            }
         }
         break;
      case 4:
         var4 = var1.getShort();
         var5 = var1.get();
         var18 = var1.getShort();
         var20 = this.IDToVehicle.get(var4);
         if (var20 != null) {
            var23 = var20.getCharacter(var5);
            if (var23 != null) {
               var26 = (IsoPlayer)GameServer.IDToPlayerMap.get(Integer.valueOf(var18));
               DebugLog.log((var26 == null ? "unknown player" : var26.getUsername()) + " switched to same seat as " + ((IsoPlayer)var23).getUsername());
               return;
            }

            for(var25 = 0; var25 < GameServer.udpEngine.connections.size(); ++var25) {
               var27 = (UdpConnection)GameServer.udpEngine.connections.get(var25);

               for(var11 = 0; var11 < 4; ++var11) {
                  var12 = var27.players[var11];
                  if (var12 != null && var12.OnlineID == var18) {
                     var20.switchSeat(var12, var5);
                     this.sendSwichSeat(var20, var5, var12);
                     var20.authorizationServerOnSeat();
                     break;
                  }
               }
            }
         }
         break;
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      case 13:
      default:
         this.noise("unknown vehicle packet " + var3);
         break;
      case 9:
         var4 = var1.getShort();
         var15 = this.IDToVehicle.get(var4);
         if (var15 != null) {
            var16 = var15.authorizationServerOnOwnerData(var2);
            if (var16) {
               float[] var24 = this.tempFloats;
               long var21 = var1.getLong();
               var15.physics.clientForce = var1.getFloat();

               for(int var10 = 0; var10 < var24.length; ++var10) {
                  var24[var10] = var1.getFloat();
               }

               var15.netLinearVelocity.x = var24[7];
               var15.netLinearVelocity.y = var24[8];
               var15.netLinearVelocity.z = var24[9];
               WorldSimulation.instance.setOwnVehiclePhysics(var4, var24);
            }
         }
         break;
      case 11:
         var4 = var1.getShort();

         for(var13 = 0; var13 < var4; ++var13) {
            var18 = var1.getShort();
            var20 = this.IDToVehicle.get(var18);
            if (var20 != null) {
               var20.updateFlags = (short)(var20.updateFlags | 1);
               this.sendVehicles(var2);
            }
         }

         return;
      case 12:
         var4 = var1.getShort();
         var15 = this.IDToVehicle.get(var4);
         if (var15 != null) {
            var15.updateFlags = (short)(var15.updateFlags | 2);
            this.sendVehicles(var2);
         }
         break;
      case 14:
         var4 = var1.getShort();
         float var14 = var1.getFloat();
         float var17 = var1.getFloat();
         var20 = this.IDToVehicle.get(var4);
         if (var20 != null) {
            var20.engineSpeed = (double)var14;
            var20.throttle = var17;
            var20.updateFlags = (short)(var20.updateFlags | 8192);
         }
         break;
      case 15:
         var4 = var1.getShort();
         var13 = var1.getInt();
         var16 = var1.get() == 1;
         var20 = this.IDToVehicle.get(var4);
         if (var20 != null) {
            var20.authorizationServerCollide(var13, var16);
         }
         break;
      case 16:
         var4 = var1.getShort();
         var5 = var1.get();
         var6 = this.IDToVehicle.get(var4);
         if (var6 != null) {
            for(var7 = 0; var7 < GameServer.udpEngine.connections.size(); ++var7) {
               var8 = (UdpConnection)GameServer.udpEngine.connections.get(var7);
               if (var8 != var2) {
                  ByteBufferWriter var9 = var8.startPacket();
                  PacketTypes.doPacket((short)5, var9);
                  var9.bb.put((byte)16);
                  var9.bb.putShort(var6.VehicleID);
                  var9.bb.put(var5);
                  var8.endPacketImmediate();
               }
            }
         }
      }

   }

   public static void serverSendVehiclesConfig(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)5, var1);
      var1.bb.put((byte)10);
      var1.bb.putShort((short)ServerOptions.getInstance().PhysicsDelay.getValue());
      var0.endPacket();
   }

   private void vehiclePosUpdate(BaseVehicle var1, float[] var2) {
      byte var3 = 0;
      Transform var4 = this.posUpdateVars.transform;
      Vector3f var5 = this.posUpdateVars.vector3f;
      Quaternionf var6 = this.posUpdateVars.quatf;
      float[] var7 = this.posUpdateVars.wheelSteer;
      float[] var8 = this.posUpdateVars.wheelRotation;
      float[] var9 = this.posUpdateVars.wheelSkidInfo;
      int var22 = var3 + 1;
      float var10 = var2[var3] - WorldSimulation.instance.offsetX;
      float var11 = var2[var22++] - WorldSimulation.instance.offsetY;
      float var12 = var2[var22++];
      var4.origin.set(var10, var12, var11);
      float var13 = var2[var22++];
      float var14 = var2[var22++];
      float var15 = var2[var22++];
      float var16 = var2[var22++];
      var6.set(var13, var14, var15, var16);
      var6.normalize();
      var4.setRotation(var6);
      float var17 = var2[var22++];
      float var18 = var2[var22++];
      float var19 = var2[var22++];
      var5.set(var17, var18, var19);
      float var20 = var2[var22++];

      int var21;
      for(var21 = 0; var21 < 4; ++var21) {
         var7[var21] = var2[var22++];
         var8[var21] = var2[var22++];
         var9[var21] = var2[var22++];
      }

      var1.jniTransform.set(var4);
      var1.jniLinearVelocity.set((Vector3fc)var5);
      var1.netLinearVelocity.set((Vector3fc)var5);
      var1.jniTransform.basis.getScale(var5);
      if ((double)var5.x < 0.99D || (double)var5.y < 0.99D || (double)var5.z < 0.99D) {
         var1.jniTransform.basis.scale(1.0F / var5.x, 1.0F / var5.y, 1.0F / var5.z);
      }

      var1.jniSpeed = var1.jniLinearVelocity.length();

      for(var21 = 0; var21 < 4; ++var21) {
         var1.wheelInfo[var21].steering = var7[var21];
         var1.wheelInfo[var21].rotation = var8[var21];
         var1.wheelInfo[var21].skidInfo = var9[var21];
      }

      var1.polyDirty = true;
   }

   public void clientUpdate() {
      if (this.vehiclesWaitUpdatesFrequency.Check()) {
         if (this.vehiclesWaitUpdates.size() > 0) {
            UdpConnection var1 = GameClient.connection;
            ByteBufferWriter var2 = var1.startPacket();
            PacketTypes.doPacket((short)5, var2);
            var2.bb.put((byte)11);
            var2.bb.putShort((short)this.vehiclesWaitUpdates.size());

            for(int var3 = 0; var3 < this.vehiclesWaitUpdates.size(); ++var3) {
               var2.bb.putShort(this.vehiclesWaitUpdates.get(var3));
            }

            var1.endPacketImmediate();
         }

         this.vehiclesWaitUpdates.clear();
      }

      ArrayList var10 = this.getVehicles();

      for(int var11 = 0; var11 < var10.size(); ++var11) {
         BaseVehicle var12 = (BaseVehicle)var10.get(var11);
         if (!var12.isKeyboardControlled() && var12.getJoypad() == -1) {
            float[] var4 = this.tempFloats;
            if (var12.interpolation.interpolationDataGetPR(var4) && var12.netPlayerAuthorization != 3 && var12.netPlayerAuthorization != 1) {
               Bullet.setOwnVehiclePhysics(var12.VehicleID, var4);
               byte var5 = 0;
               int var13 = var5 + 1;
               float var6 = var4[var5];
               float var7 = var4[var13++];
               float var8 = var4[var13++];
               IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare((double)var6, (double)var7, (double)var8);
               this.clientUpdateVehiclePos(var12, var6, var7, var8, var9);
               var12.limitPhysicValid.BlockCheck();
               if (GameClient.bClient) {
                  this.vehiclePosUpdate(var12, var4);
               }
            }
         } else {
            var12.interpolation.setVehicleData(var12);
         }
      }

   }

   private void clientUpdateVehiclePos(BaseVehicle var1, float var2, float var3, float var4, IsoGridSquare var5) {
      var1.setX(var2);
      var1.setY(var3);
      var1.setZ(var4);
      var1.square = var5;
      var1.setCurrent(var5);
      if (var5 != null) {
         if (var1.chunk != null && var1.chunk != var5.chunk) {
            var1.chunk.vehicles.remove(var1);
         }

         var1.chunk = var1.square.chunk;
         if (!var1.chunk.vehicles.contains(var1)) {
            var1.chunk.vehicles.add(var1);
            IsoChunk.addFromCheckedVehicles(var1);
         }

         if (!var1.addedToWorld) {
            var1.addToWorld();
         }
      } else {
         var1.removeFromWorld();
         var1.removeFromSquare();
      }

      var1.polyDirty = true;
   }

   private void clientReceiveUpdateFull(ByteBuffer var1, short var2) throws IOException {
      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      float var5 = var1.getFloat();
      byte var6 = var1.get();
      int var7 = var1.getInt();
      short var8 = var1.getShort();
      IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare((double)var3, (double)var4, (double)var5);
      if (this.IDToVehicle.containsKey(var2)) {
         BaseVehicle var16 = this.IDToVehicle.get(var2);
         this.noise("ERROR: got full update for KNOWN vehicle id=" + var2);
         var1.get();
         var1.getInt();
         this.tempVehicle.parts.clear();
         this.tempVehicle.load(var1, 143);
         if (var16.physics != null) {
            this.tempTransform.setRotation(this.tempVehicle.savedRot);
            this.tempTransform.origin.set(var3 - WorldSimulation.instance.offsetX, var5, var4 - WorldSimulation.instance.offsetY);
            var16.setWorldTransform(this.tempTransform);
         }

         var16.netPlayerFromServerUpdate(var6, var7);
         this.clientUpdateVehiclePos(var16, var3, var4, var5, var9);
      } else {
         boolean var10 = var1.get() != 0;
         int var11 = var1.getInt();
         if (!var10 || var11 != "Vehicle".hashCode()) {
            DebugLog.log("Error: clientReceiveUpdateFull: packet broken");
         }

         BaseVehicle var12 = new BaseVehicle(IsoWorld.instance.CurrentCell);
         if (var12 == null || !(var12 instanceof BaseVehicle)) {
            return;
         }

         BaseVehicle var13 = (BaseVehicle)var12;
         var13.VehicleID = var2;
         var13.square = var9;
         var13.setCurrent(var9);
         var13.load(var1, 143);
         if (var9 != null) {
            var13.chunk = var13.square.chunk;
            var13.chunk.vehicles.add(var13);
            var13.addToWorld();
         }

         IsoChunk.addFromCheckedVehicles(var13);
         var13.netPlayerFromServerUpdate(var6, var7);
         this.registerVehicle(var13);

         for(int var14 = 0; var14 < IsoPlayer.numPlayers; ++var14) {
            IsoPlayer var15 = IsoPlayer.players[var14];
            if (var15 != null && !var15.isDead() && var15.getVehicle() == null) {
               IsoWorld.instance.CurrentCell.putInVehicle(var15);
            }
         }

         if (var13.trace) {
            this.noise("added vehicle id=" + var13.VehicleID + (var9 == null ? " (delayed)" : ""));
         }
      }

   }

   private void clientReceiveUpdate(ByteBuffer var1) throws IOException {
      short var2 = var1.getShort();
      short var3 = var1.getShort();
      float var4 = var1.getFloat();
      float var5 = var1.getFloat();
      float var6 = var1.getFloat();
      short var7 = var1.getShort();
      VehicleCache.vehicleUpdate(var2, var4, var5, var6);
      IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare((double)var4, (double)var5, (double)var6);
      BaseVehicle var9 = this.IDToVehicle.get(var2);
      if (var9 == null && var8 == null) {
         if (var1.limit() > var1.position() + var7) {
            var1.position(var1.position() + var7);
         }

      } else {
         int var11;
         boolean var18;
         if (var9 != null && var8 == null) {
            var18 = true;

            for(var11 = 0; var11 < IsoPlayer.numPlayers; ++var11) {
               IsoPlayer var31 = IsoPlayer.players[var11];
               if (var31 != null && var31.getVehicle() == var9) {
                  var18 = false;
                  var31.setPosition(var4, var5, var6);
                  this.sendReqestGetPosition(var2);
               }
            }

            if (var18) {
               var9.removeFromWorld();
               var9.removeFromSquare();
            }

            if (var1.limit() > var1.position() + var7) {
               var1.position(var1.position() + var7);
            }

         } else {
            int var19;
            if ((var3 & 1) != 0) {
               this.clientReceiveUpdateFull(var1, var2);
               var19 = this.vehiclesWaitUpdates.indexOf(var2);
               if (var19 >= 0) {
                  this.vehiclesWaitUpdates.removeAt(var19);
               }

            } else if (var9 == null && var8 != null) {
               this.sendReqestGetFull(var2);
               if (var1.limit() > var1.position() + var7) {
                  var1.position(var1.position() + var7);
               }

            } else {
               byte var10;
               if ((var3 & 16384) != 0) {
                  var10 = var1.get();
                  var11 = var1.getInt();
                  if (var9 != null) {
                     var9.netPlayerFromServerUpdate(var10, var11);
                  }
               }

               if ((var3 & 2) != 0) {
                  if (!var9.isKeyboardControlled() && var9.getJoypad() == -1) {
                     var9.interpolation.interpolationDataAdd(var1);
                  } else if (var1.limit() > var1.position() + 98) {
                     var1.position(var1.position() + 98);
                  }
               }

               if ((var3 & 4) != 0) {
                  this.noise("received update Engine id=" + var2);
                  var10 = var1.get();
                  if (var10 >= 0 && var10 < BaseVehicle.engineStateTypes.Values.length) {
                     switch(BaseVehicle.engineStateTypes.Values[var10]) {
                     case Idle:
                        var9.engineDoIdle();
                     case Starting:
                     default:
                        break;
                     case RetryingStarting:
                        var9.engineDoRetryingStarting();
                        break;
                     case StartingSuccess:
                        var9.engineDoStartingSuccess();
                        break;
                     case StartingFailed:
                        var9.engineDoStartingFailed();
                        break;
                     case StartingFailedNoPower:
                        var9.engineDoStartingFailedNoPower();
                        break;
                     case Running:
                        var9.engineDoRunning();
                        break;
                     case Stalling:
                        var9.engineDoStalling();
                        break;
                     case ShutingDown:
                        var9.engineDoShutingDown();
                     }
                  } else {
                     DebugLog.log("ERROR: VehicleManager.clientReceiveUpdate get invalid data");
                  }
               }

               String var13;
               byte var14;
               boolean var20;
               if ((var3 & 4096) != 0) {
                  this.noise("received car properties update id=" + var2);
                  var9.setHotwired(var1.get() == 1);
                  var9.setHotwiredBroken(var1.get() == 1);
                  var18 = var1.get() == 1;
                  var20 = var1.get() == 1;
                  InventoryItem var12 = null;
                  if (var1.get() == 1) {
                     var13 = GameWindow.ReadStringUTF(var1);
                     var14 = var1.get();
                     var12 = InventoryItemFactory.CreateItem(var13);
                     if (var12 != null) {
                        var12.load(var1, 143, false);
                     }
                  }

                  var9.syncKeyInIgnition(var18, var20, var12);
               }

               if ((var3 & 8) != 0) {
                  this.noise("received update Lights id=" + var2);
                  var9.setHeadlightsOn(var1.get() == 1);
                  var9.setStoplightsOn(var1.get() == 1);

                  for(var19 = 0; var19 < var9.getLightCount(); ++var19) {
                     var20 = var1.get() == 1;
                     var9.getLightByIndex(var19).getLight().setActive(var20);
                  }
               }

               byte var22;
               byte var24;
               if ((var3 & 1024) != 0) {
                  this.noise("received update Sounds id=" + var2);
                  var18 = var1.get() == 1;
                  var20 = var1.get() == 1;
                  var22 = var1.get();
                  var24 = var1.get();
                  if (var18 != var9.soundHornOn) {
                     if (var18) {
                        var9.onHornStart();
                     } else {
                        var9.onHornStop();
                     }
                  }

                  if (var20 != var9.soundBackMoveOn) {
                     if (var20) {
                        var9.onBackMoveSignalStart();
                     } else {
                        var9.onBackMoveSignalStop();
                     }
                  }

                  if (var9.lightbarLightsMode.get() != var22) {
                     var9.setLightbarLightsMode(var22);
                  }

                  if (var9.lightbarSirenMode.get() != var24) {
                     var9.setLightbarSirenMode(var24);
                  }
               }

               VehiclePart var21;
               if ((var3 & 2048) != 0) {
                  for(var10 = var1.get(); var10 != -1; var10 = var1.get()) {
                     var21 = var9.getPartByIndex(var10);
                     this.noise("received update PartCondition id=" + var2 + " part=" + var21.getId());
                     var21.updateFlags = (short)(var21.updateFlags | 2048);
                     var21.setCondition(var1.getInt());
                  }

                  var9.doDamageOverlay();
               }

               if ((var3 & 16) != 0) {
                  for(var10 = var1.get(); var10 != -1; var10 = var1.get()) {
                     var21 = var9.getPartByIndex(var10);
                     this.noise("received update PartModData id=" + var2 + " part=" + var21.getId());
                     var21.getModData().load((ByteBuffer)var1, 143);
                     if (var21.isContainer()) {
                        var21.setContainerContentAmount(var21.getContainerContentAmount());
                     }
                  }
               }

               float var23;
               VehiclePart var25;
               if ((var3 & 32) != 0) {
                  for(var10 = var1.get(); var10 != -1; var10 = var1.get()) {
                     var23 = var1.getFloat();
                     var25 = var9.getPartByIndex(var10);
                     this.noise("received update PartUsedDelta id=" + var2 + " part=" + var25.getId());
                     InventoryItem var26 = var25.getInventoryItem();
                     if (var26 instanceof DrainableComboItem) {
                        ((DrainableComboItem)var26).setUsedDelta(var23);
                     }
                  }
               }

               if ((var3 & 128) != 0) {
                  for(var10 = var1.get(); var10 != -1; var10 = var1.get()) {
                     var21 = var9.getPartByIndex(var10);
                     this.noise("received update PartItem id=" + var2 + " part=" + var21.getId());
                     var21.updateFlags = (short)(var21.updateFlags | 128);
                     boolean var27 = var1.get() != 0;
                     if (var27) {
                        var13 = GameWindow.ReadStringUTF(var1);
                        var14 = var1.get();
                        InventoryItem var15 = InventoryItemFactory.CreateItem(var13);
                        if (var15 == null) {
                           this.noise("unknown item type \"" + var13 + "\"");
                           return;
                        }

                        try {
                           var15.load(var1, 143, false);
                        } catch (Exception var17) {
                           var17.printStackTrace();
                           return;
                        }

                        var21.setInventoryItem(var15);
                     } else {
                        var21.setInventoryItem((InventoryItem)null);
                     }

                     int var28 = var21.getWheelIndex();
                     if (var28 != -1) {
                        var9.setTireRemoved(var28, !var27);
                     }

                     if (var21.isContainer()) {
                        LuaEventManager.triggerEvent("OnContainerUpdate");
                     }
                  }
               }

               if ((var3 & 512) != 0) {
                  for(var10 = var1.get(); var10 != -1; var10 = var1.get()) {
                     var21 = var9.getPartByIndex(var10);
                     this.noise("received update PartDoor id=" + var2 + " part=" + var21.getId());
                     var21.getDoor().load(var1, 143);
                  }

                  LuaEventManager.triggerEvent("OnContainerUpdate");
                  var9.doDamageOverlay();
               }

               if ((var3 & 256) != 0) {
                  for(var10 = var1.get(); var10 != -1; var10 = var1.get()) {
                     var21 = var9.getPartByIndex(var10);
                     this.noise("received update PartWindow id=" + var2 + " part=" + var21.getId());
                     var21.getWindow().load(var1, 143);
                  }

                  var9.doDamageOverlay();
               }

               if ((var3 & 64) != 0) {
                  this.oldModels.clear();
                  this.oldModels.addAll(var9.models);
                  this.curModels.clear();
                  var10 = var1.get();

                  for(var11 = 0; var11 < var10; ++var11) {
                     var22 = var1.get();
                     var24 = var1.get();
                     VehiclePart var30 = var9.getPartByIndex(var22);
                     VehicleScript.Model var32 = (VehicleScript.Model)var30.getScriptPart().models.get(var24);
                     BaseVehicle.ModelInfo var16 = var9.setModelVisible(var30, var32, true);
                     this.curModels.add(var16);
                  }

                  for(var11 = 0; var11 < this.oldModels.size(); ++var11) {
                     BaseVehicle.ModelInfo var29 = (BaseVehicle.ModelInfo)this.oldModels.get(var11);
                     if (!this.curModels.contains(var29)) {
                        var9.setModelVisible(var29.part, var29.scriptModel, false);
                     }
                  }

                  var9.doDamageOverlay();
               }

               if ((var3 & 8192) != 0) {
                  float var33 = var1.getFloat();
                  var23 = var1.getFloat();
                  if (!(var9.getDriver() instanceof IsoPlayer) || !((IsoPlayer)var9.getDriver()).isLocalPlayer()) {
                     var9.engineSpeed = (double)var33;
                     var9.throttle = var23;
                  }
               }

               var18 = false;

               for(var11 = 0; var11 < var9.getPartCount(); ++var11) {
                  var25 = var9.getPartByIndex(var11);
                  if (var25.updateFlags != 0) {
                     if ((var25.updateFlags & 2048) != 0 && (var25.updateFlags & 128) == 0) {
                        var25.doInventoryItemStats(var25.getInventoryItem(), var25.getMechanicSkillInstaller());
                        var18 = true;
                     }

                     var25.updateFlags = 0;
                  }
               }

               if (var18) {
                  var9.updatePartStats();
                  var9.updateBulletStats();
               }

            }
         }
      }
   }

   public void clientPacket(ByteBuffer var1) {
      byte var2 = var1.get();
      short var3;
      byte var4;
      BaseVehicle var5;
      BaseVehicle var6;
      IsoGameCharacter var8;
      short var18;
      IsoPlayer var21;
      switch(var2) {
      case 1:
         var3 = var1.getShort();
         var4 = var1.get();
         String var20 = GameWindow.ReadString(var1);
         var6 = this.IDToVehicle.get(var3);
         if (var6 != null) {
            IsoGameCharacter var22 = var6.getCharacter(var4);
            if (var22 != null) {
               var6.setCharacterPosition(var22, var4, var20);
            }
         }
         break;
      case 2:
      case 3:
      case 9:
      case 10:
      case 11:
      case 12:
      case 14:
      case 15:
      default:
         this.noise("unknown vehicle packet " + var2);
         break;
      case 4:
         var3 = var1.getShort();
         var4 = var1.get();
         var18 = var1.getShort();
         var6 = this.IDToVehicle.get(var3);
         if (var6 != null) {
            var21 = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(var18));
            if (var21 != null) {
               var8 = var6.getCharacter(var4);
               if (var8 == null) {
                  var6.switchSeatRSync(var21, var4);
               } else if (var21 != var8) {
                  DebugLog.log(var21.getUsername() + " switched to same seat as " + ((IsoPlayer)var8).getUsername());
               }
            }
         }
         break;
      case 5:
         if (this.tempVehicle == null || this.tempVehicle.getCell() != IsoWorld.instance.CurrentCell) {
            this.tempVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
         }

         var3 = var1.getShort();

         for(int var17 = 0; var17 < var3; ++var17) {
            try {
               this.clientReceiveUpdate(var1);
            } catch (Exception var12) {
               var12.printStackTrace();
               return;
            }
         }

         return;
      case 6:
         var3 = var1.getShort();
         var4 = var1.get();
         var18 = var1.getShort();
         var6 = this.IDToVehicle.get(var3);
         if (var6 != null) {
            var21 = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(var18));
            if (var21 != null) {
               var8 = var6.getCharacter(var4);
               if (var8 == null) {
                  DebugLog.log(var21.getUsername() + " got in vehicle " + var6.VehicleID + " seat " + var4);
                  var6.enterRSync(var4, var21, var6);
               } else if (var21 != var8) {
                  DebugLog.log(var21.getUsername() + " got in same seat as " + ((IsoPlayer)var8).getUsername());
               }
            }
         }
         break;
      case 7:
         var3 = var1.getShort();
         short var15 = var1.getShort();
         var5 = this.IDToVehicle.get(var3);
         if (var5 != null) {
            IsoPlayer var19 = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(var15));
            if (var19 != null) {
               var5.exitRSync(var19);
            }
         }
         break;
      case 8:
         var3 = var1.getShort();
         if (this.IDToVehicle.containsKey(var3)) {
            BaseVehicle var14 = this.IDToVehicle.get(var3);
            if (var14.trace) {
               this.noise("server removed vehicle id=" + var3);
            }

            var14.serverRemovedFromWorld = true;

            try {
               var14.removeFromWorld();
               var14.removeFromSquare();
            } finally {
               if (this.IDToVehicle.containsKey(var3)) {
                  this.unregisterVehicle(var14);
               }

            }
         }

         VehicleCache.remove(var3);
         break;
      case 13:
         var3 = var1.getShort();
         Vector3f var13 = new Vector3f();
         Vector3f var16 = new Vector3f();
         var13.x = var1.getFloat();
         var13.y = var1.getFloat();
         var13.z = var1.getFloat();
         var16.x = var1.getFloat();
         var16.y = var1.getFloat();
         var16.z = var1.getFloat();
         var6 = this.IDToVehicle.get(var3);
         if (var6 != null) {
            Bullet.applyCentralForceToVehicle(var6.VehicleID, var13.x, var13.y, var13.z);
            Vector3f var7 = var16.cross(var13);
            Bullet.applyTorqueToVehicle(var6.VehicleID, var7.x, var7.y, var7.z);
         }
         break;
      case 16:
         var3 = var1.getShort();
         var4 = var1.get();
         var5 = this.IDToVehicle.get(var3);
         if (var5 != null) {
            SoundManager.instance.PlayWorldSound("VehicleCrash", var5.square, 1.0F, 20.0F, 1.0F, true);
         }
      }

   }

   public static void loadingClientPacket(ByteBuffer var0) {
      int var1 = var0.position();
      byte var2 = var0.get();
      switch(var2) {
      case 10:
         physicsDelay = var0.getShort();
      default:
         var0.position(var1);
      }
   }

   public void sendCollide(BaseVehicle var1, IsoGameCharacter var2, boolean var3) {
      if (var2 != null) {
         UdpConnection var4 = GameClient.connection;
         ByteBufferWriter var5 = var4.startPacket();
         PacketTypes.doPacket((short)5, var5);
         var5.bb.put((byte)15);
         var5.bb.putShort(var1.VehicleID);
         var5.bb.putInt(((IsoPlayer)var2).OnlineID);
         var5.bb.put((byte)(var3 ? 1 : 0));
         var4.endPacketImmediate();
      }
   }

   public void sendEnter(BaseVehicle var1, int var2, IsoGameCharacter var3) {
      UdpConnection var4 = GameClient.connection;
      ByteBufferWriter var5 = var4.startPacket();
      PacketTypes.doPacket((short)5, var5);
      var5.bb.put((byte)2);
      var5.bb.putShort(var1.VehicleID);
      var5.bb.put((byte)var2);
      var5.bb.putShort((short)((IsoPlayer)var3).OnlineID);
      var4.endPacketImmediate();
   }

   public static void sendSound(BaseVehicle var0, byte var1) {
      UdpConnection var2 = GameClient.connection;
      ByteBufferWriter var3 = var2.startPacket();
      PacketTypes.doPacket((short)5, var3);
      var3.bb.put((byte)16);
      var3.bb.putShort(var0.VehicleID);
      var3.bb.put(var1);
      var2.endPacketImmediate();
   }

   public static void sendSoundFromServer(BaseVehicle var0, byte var1) {
      for(int var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)5, var4);
         var4.bb.put((byte)16);
         var4.bb.putShort(var0.VehicleID);
         var4.bb.put(var1);
         var3.endPacketImmediate();
      }

   }

   public void sendPassengerPosition(BaseVehicle var1, int var2, String var3) {
      UdpConnection var4 = GameClient.connection;
      ByteBufferWriter var5 = var4.startPacket();
      PacketTypes.doPacket((short)5, var5);
      var5.bb.put((byte)1);
      var5.bb.putShort(var1.VehicleID);
      var5.bb.put((byte)var2);
      var5.putUTF(var3);
      var4.endPacketImmediate();
   }

   public void sendPassengerPosition(BaseVehicle var1, int var2, String var3, UdpConnection var4) {
      for(int var5 = 0; var5 < GameServer.udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)GameServer.udpEngine.connections.get(var5);
         if (var6 != var4) {
            ByteBufferWriter var7 = var6.startPacket();
            PacketTypes.doPacket((short)5, var7);
            var7.bb.put((byte)1);
            var7.bb.putShort(var1.VehicleID);
            var7.bb.put((byte)var2);
            var7.putUTF(var3);
            var6.endPacket();
         }
      }

   }

   public void sendReqestGetFull(short var1) {
      if (!this.vehiclesWaitUpdates.contains(var1)) {
         UdpConnection var2 = GameClient.connection;
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)5, var3);
         var3.bb.put((byte)11);
         var3.bb.putShort((short)1);
         var3.bb.putShort(var1);
         var2.endPacketImmediate();
         this.vehiclesWaitUpdates.add(var1);
      }
   }

   public void sendReqestGetFull(List var1) {
      if (var1 != null) {
         UdpConnection var2 = GameClient.connection;
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)5, var3);
         var3.bb.put((byte)11);
         var3.bb.putShort((short)var1.size());

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            var3.bb.putShort(((VehicleCache)var1.get(var4)).id);
            this.vehiclesWaitUpdates.add(((VehicleCache)var1.get(var4)).id);
         }

         var2.endPacketImmediate();
      }
   }

   public void sendReqestGetPosition(short var1) {
      if (sendReqestGetPositionFrequency.Check()) {
         UdpConnection var2 = GameClient.connection;
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)5, var3);
         var3.bb.put((byte)12);
         var3.bb.putShort(var1);
         var2.endPacketImmediate();
         this.vehiclesWaitUpdates.add(var1);
      }
   }

   public void sendAddImpulse(BaseVehicle var1, Vector3f var2, Vector3f var3) {
      UdpConnection var4 = null;

      for(int var5 = 0; var5 < GameServer.udpEngine.connections.size() && var4 == null; ++var5) {
         UdpConnection var6 = (UdpConnection)GameServer.udpEngine.connections.get(var5);

         for(int var7 = 0; var7 < var6.players.length; ++var7) {
            IsoPlayer var8 = var6.players[var7];
            if (var8 != null && var8.getVehicle() != null && var8.getVehicle().VehicleID == var1.VehicleID) {
               var4 = var6;
               break;
            }
         }
      }

      if (var4 != null) {
         ByteBufferWriter var9 = var4.startPacket();
         PacketTypes.doPacket((short)5, var9);
         var9.bb.put((byte)13);
         var9.bb.putShort(var1.VehicleID);
         var9.bb.putFloat(var2.x);
         var9.bb.putFloat(var2.y);
         var9.bb.putFloat(var2.z);
         var9.bb.putFloat(var3.x);
         var9.bb.putFloat(var3.y);
         var9.bb.putFloat(var3.z);
         var4.endPacketImmediate();
      }

   }

   public void sendREnter(BaseVehicle var1, int var2, IsoGameCharacter var3) {
      for(int var4 = 0; var4 < GameServer.udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)GameServer.udpEngine.connections.get(var4);
         ByteBufferWriter var6 = var5.startPacket();
         PacketTypes.doPacket((short)5, var6);
         var6.bb.put((byte)6);
         var6.bb.putShort(var1.VehicleID);
         var6.bb.put((byte)var2);
         var6.bb.putShort((short)((IsoPlayer)var3).OnlineID);
         var5.endPacket();
      }

   }

   public void sendSwichSeat(BaseVehicle var1, int var2, IsoGameCharacter var3) {
      if (GameClient.bClient) {
         UdpConnection var4 = GameClient.connection;
         ByteBufferWriter var5 = var4.startPacket();
         PacketTypes.doPacket((short)5, var5);
         var5.bb.put((byte)4);
         var5.bb.putShort(var1.VehicleID);
         var5.bb.put((byte)var2);
         var5.bb.putShort((short)((IsoPlayer)var3).OnlineID);
         var4.endPacketImmediate();
      }

      if (GameServer.bServer) {
         for(int var7 = 0; var7 < GameServer.udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)GameServer.udpEngine.connections.get(var7);
            ByteBufferWriter var6 = var8.startPacket();
            PacketTypes.doPacket((short)5, var6);
            var6.bb.put((byte)4);
            var6.bb.putShort(var1.VehicleID);
            var6.bb.put((byte)var2);
            var6.bb.putShort((short)((IsoPlayer)var3).OnlineID);
            var8.endPacket();
         }
      }

   }

   public void sendExit(BaseVehicle var1, IsoGameCharacter var2) {
      UdpConnection var3 = GameClient.connection;
      ByteBufferWriter var4 = var3.startPacket();
      PacketTypes.doPacket((short)5, var4);
      var4.bb.put((byte)3);
      var4.bb.putShort(var1.VehicleID);
      var4.bb.putShort((short)((IsoPlayer)var2).OnlineID);
      var3.endPacketImmediate();
   }

   public void sendRExit(BaseVehicle var1, IsoGameCharacter var2) {
      for(int var3 = 0; var3 < GameServer.udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)GameServer.udpEngine.connections.get(var3);
         ByteBufferWriter var5 = var4.startPacket();
         PacketTypes.doPacket((short)5, var5);
         var5.bb.put((byte)7);
         var5.bb.putShort(var1.VehicleID);
         var5.bb.putShort((short)((IsoPlayer)var2).OnlineID);
         var4.endPacket();
      }

   }

   public void sendPhysic(BaseVehicle var1) {
      UdpConnection var2 = GameClient.connection;
      ByteBufferWriter var3 = var2.startPacket();
      PacketTypes.doPacket((short)5, var3);
      var3.bb.put((byte)9);
      var3.bb.putShort(var1.VehicleID);
      ByteBuffer var10000 = var3.bb;
      GameTime.getInstance();
      var10000.putLong(GameTime.getServerTime());
      var3.bb.putFloat(var1.physics.EngineForce - var1.physics.BrakingForce);
      if (WorldSimulation.instance.getOwnVehiclePhysics(var1.VehicleID, var3) != 1) {
         var2.cancelPacket();
      } else {
         var2.endPacket();
      }
   }

   public void sendEngineSound(BaseVehicle var1, float var2, float var3) {
      UdpConnection var4 = GameClient.connection;
      ByteBufferWriter var5 = var4.startPacket();
      PacketTypes.doPacket((short)5, var5);
      var5.bb.put((byte)14);
      var5.bb.putShort(var1.VehicleID);
      var5.bb.putFloat(var2);
      var5.bb.putFloat(var3);
      var4.endPacket();
   }

   public static class PosUpdateVars {
      final Transform transform = new Transform();
      final Vector3f vector3f = new Vector3f();
      final Quaternionf quatf = new Quaternionf();
      final float[] wheelSteer = new float[4];
      final float[] wheelRotation = new float[4];
      final float[] wheelSkidInfo = new float[4];
   }

   public static class VehiclePacket {
      public static final byte PassengerPosition = 1;
      public static final byte Enter = 2;
      public static final byte Exit = 3;
      public static final byte SwichSeat = 4;
      public static final byte Update = 5;
      public static final byte REnter = 6;
      public static final byte RExit = 7;
      public static final byte Remove = 8;
      public static final byte Physic = 9;
      public static final byte Config = 10;
      public static final byte ReqestGetFull = 11;
      public static final byte ReqestGetPosition = 12;
      public static final byte AddImpulse = 13;
      public static final byte EngineSound = 14;
      public static final byte Collide = 15;
      public static final byte Sound = 16;
      public static final byte Sound_Crash = 1;
   }
}
