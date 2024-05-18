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

	private void noise(String string) {
		if (Core.bDebug) {
		}
	}

	public void registerVehicle(BaseVehicle baseVehicle) {
		this.IDToVehicle.put(baseVehicle.VehicleID, baseVehicle);
		this.idMapDirty = true;
	}

	public void unregisterVehicle(BaseVehicle baseVehicle) {
		this.IDToVehicle.remove(baseVehicle.VehicleID);
		this.idMapDirty = true;
	}

	public BaseVehicle getVehicleByID(short short1) {
		return this.IDToVehicle.get(short1);
	}

	public ArrayList getVehicles() {
		if (this.idMapDirty) {
			this.vehicles.clear();
			this.IDToVehicle.toArrayList(this.vehicles);
			this.idMapDirty = false;
		}

		return this.vehicles;
	}

	public void removeFromWorld(BaseVehicle baseVehicle) {
		if (baseVehicle.VehicleID != -1) {
			short short1 = baseVehicle.VehicleID;
			if (baseVehicle.trace) {
				this.noise("removeFromWorld vehicle id=" + baseVehicle.VehicleID);
			}

			this.unregisterVehicle(baseVehicle);
			if (GameServer.bServer) {
				for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (baseVehicle.connectionState[udpConnection.index] != null) {
						ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
						PacketTypes.doPacket((short)5, byteBufferWriter);
						byteBufferWriter.bb.put((byte)8);
						byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
						udpConnection.endPacketImmediate();
					}
				}
			}

			if (GameClient.bClient) {
				baseVehicle.serverRemovedFromWorld = false;
				if (baseVehicle.interpolation != null) {
					baseVehicle.interpolation.poolData();
				}
			}
		}
	}

	public void serverUpdate() {
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();
		int int1;
		for (int1 = 0; int1 < this.connected.length; ++int1) {
			int int2;
			if (this.connected[int1] != null && !GameServer.udpEngine.connections.contains(this.connected[int1])) {
				this.noise("vehicles: dropped connection " + int1);
				for (int2 = 0; int2 < arrayList.size(); ++int2) {
					((BaseVehicle)arrayList.get(int2)).connectionState[int1] = null;
				}

				this.connected[int1] = null;
			} else {
				for (int2 = 0; int2 < arrayList.size(); ++int2) {
					if (((BaseVehicle)arrayList.get(int2)).connectionState[int1] != null) {
						BaseVehicle.ServerVehicleState serverVehicleState = ((BaseVehicle)arrayList.get(int2)).connectionState[int1];
						serverVehicleState.flags |= ((BaseVehicle)arrayList.get(int2)).updateFlags;
					}
				}
			}
		}

		for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			this.sendVehicles(udpConnection);
			this.connected[udpConnection.index] = udpConnection;
		}

		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
			if ((baseVehicle.updateFlags & 19440) != 0) {
				for (int int3 = 0; int3 < baseVehicle.getPartCount(); ++int3) {
					VehiclePart vehiclePart = baseVehicle.getPartByIndex(int3);
					vehiclePart.updateFlags = 0;
				}
			}

			baseVehicle.updateFlags = 0;
		}
	}

	private void sendVehicles(UdpConnection udpConnection) {
		this.send.clear();
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
			if (baseVehicle.VehicleID == -1) {
				baseVehicle.VehicleID = this.IDToVehicle.allocateID();
				this.registerVehicle(baseVehicle);
			}

			boolean boolean1 = udpConnection.vehicles.contains(baseVehicle.VehicleID);
			if (boolean1 && !udpConnection.RelevantTo(baseVehicle.x, baseVehicle.y, (float)(udpConnection.ReleventRange * 10) * 2.0F)) {
				DebugLog.log("removed out-of-bounds vehicle.id=" + baseVehicle.VehicleID + " connection=" + udpConnection.index);
				udpConnection.vehicles.remove(baseVehicle.VehicleID);
				boolean1 = false;
			}

			if (boolean1 || udpConnection.ReleventTo(baseVehicle.x, baseVehicle.y)) {
				if (baseVehicle.connectionState[udpConnection.index] == null) {
					baseVehicle.connectionState[udpConnection.index] = new BaseVehicle.ServerVehicleState();
				}

				BaseVehicle.ServerVehicleState serverVehicleState = baseVehicle.connectionState[udpConnection.index];
				if (!boolean1 || serverVehicleState.shouldSend(baseVehicle)) {
					this.send.add(baseVehicle);
					udpConnection.vehicles.add(baseVehicle.VehicleID);
				}
			}
		}

		if (!this.send.isEmpty()) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			try {
				ByteBuffer byteBuffer = byteBufferWriter.bb;
				byteBuffer.put((byte)5);
				byteBuffer.putShort((short)this.send.size());
				for (int int2 = 0; int2 < this.send.size(); ++int2) {
					BaseVehicle baseVehicle2 = (BaseVehicle)this.send.get(int2);
					BaseVehicle.ServerVehicleState serverVehicleState2 = baseVehicle2.connectionState[udpConnection.index];
					byteBuffer.putShort(baseVehicle2.VehicleID);
					byteBuffer.putShort(serverVehicleState2.flags);
					byteBuffer.putFloat(baseVehicle2.x);
					byteBuffer.putFloat(baseVehicle2.y);
					byteBuffer.putFloat(baseVehicle2.z);
					int int3 = byteBuffer.position();
					byteBuffer.putShort((short)0);
					int int4 = byteBuffer.position();
					boolean boolean2 = (serverVehicleState2.flags & 1) != 0;
					int int5;
					int int6;
					int int7;
					if (boolean2) {
						baseVehicle2.updateFlags = (short)(baseVehicle2.updateFlags & -2);
						byteBuffer.putFloat(baseVehicle2.x);
						byteBuffer.putFloat(baseVehicle2.y);
						byteBuffer.putFloat(baseVehicle2.z);
						baseVehicle2.netPlayerServerSendAuthorisation(byteBuffer);
						serverVehicleState2.setAuthorization(baseVehicle2);
						int6 = byteBuffer.position();
						byteBuffer.putShort((short)0);
						baseVehicle2.save(byteBuffer);
						int7 = byteBuffer.position();
						byteBuffer.position(int6);
						byteBuffer.putShort((short)(int7 - int6));
						byteBuffer.position(int7);
						int5 = byteBuffer.position();
						int int8 = byteBuffer.position() - int4;
						byteBuffer.position(int3);
						byteBuffer.putShort((short)int8);
						byteBuffer.position(int5);
						serverVehicleState2.x = baseVehicle2.x;
						serverVehicleState2.y = baseVehicle2.y;
						serverVehicleState2.z = baseVehicle2.z;
						serverVehicleState2.orient.set((Quaternionfc)baseVehicle2.savedRot);
					} else {
						if ((serverVehicleState2.flags & 16384) != 0) {
							baseVehicle2.netPlayerServerSendAuthorisation(byteBuffer);
							serverVehicleState2.setAuthorization(baseVehicle2);
						}

						if ((serverVehicleState2.flags & 2) != 0) {
							byteBuffer.putLong(WorldSimulation.instance.time);
							byteBuffer.putFloat(baseVehicle2.x);
							byteBuffer.putFloat(baseVehicle2.y);
							byteBuffer.putFloat(baseVehicle2.z);
							Quaternionf quaternionf = baseVehicle2.savedRot;
							Transform transform = baseVehicle2.getWorldTransform(this.tempTransform);
							transform.getRotation(quaternionf);
							byteBuffer.putFloat(quaternionf.x);
							byteBuffer.putFloat(quaternionf.y);
							byteBuffer.putFloat(quaternionf.z);
							byteBuffer.putFloat(quaternionf.w);
							byteBuffer.putFloat(baseVehicle2.netLinearVelocity.x);
							byteBuffer.putFloat(baseVehicle2.netLinearVelocity.y);
							byteBuffer.putFloat(baseVehicle2.netLinearVelocity.z);
							byteBuffer.putShort((short)baseVehicle2.wheelInfo.length);
							for (int5 = 0; int5 < baseVehicle2.wheelInfo.length; ++int5) {
								byteBuffer.putFloat(baseVehicle2.wheelInfo[int5].steering);
								byteBuffer.putFloat(baseVehicle2.wheelInfo[int5].rotation);
								byteBuffer.putFloat(baseVehicle2.wheelInfo[int5].skidInfo);
							}

							serverVehicleState2.x = baseVehicle2.x;
							serverVehicleState2.y = baseVehicle2.y;
							serverVehicleState2.z = baseVehicle2.z;
							serverVehicleState2.orient.set((Quaternionfc)baseVehicle2.savedRot);
						}

						if ((serverVehicleState2.flags & 4) != 0) {
							byteBuffer.put((byte)baseVehicle2.engineState.ordinal());
						}

						if ((serverVehicleState2.flags & 4096) != 0) {
							byteBuffer.put((byte)(baseVehicle2.isHotwired() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle2.isHotwiredBroken() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle2.isKeysInIgnition() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle2.isKeyIsOnDoor() ? 1 : 0));
							InventoryItem inventoryItem = baseVehicle2.getCurrentKey();
							if (inventoryItem == null) {
								byteBuffer.put((byte)0);
							} else {
								byteBuffer.put((byte)1);
								inventoryItem.save(byteBuffer, false);
							}
						}

						if ((serverVehicleState2.flags & 8) != 0) {
							byteBuffer.put((byte)(baseVehicle2.getHeadlightsOn() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle2.getStoplightsOn() ? 1 : 0));
							for (int6 = 0; int6 < baseVehicle2.getLightCount(); ++int6) {
								byteBuffer.put((byte)(baseVehicle2.getLightByIndex(int6).getLight().getActive() ? 1 : 0));
							}
						}

						if ((serverVehicleState2.flags & 1024) != 0) {
							byteBuffer.put((byte)(baseVehicle2.soundHornOn ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle2.soundBackMoveOn ? 1 : 0));
							byteBuffer.put((byte)baseVehicle2.lightbarLightsMode.get());
							byteBuffer.put((byte)baseVehicle2.lightbarSirenMode.get());
						}

						VehiclePart vehiclePart;
						if ((serverVehicleState2.flags & 2048) != 0) {
							for (int6 = 0; int6 < baseVehicle2.getPartCount(); ++int6) {
								vehiclePart = baseVehicle2.getPartByIndex(int6);
								if ((vehiclePart.updateFlags & 2048) != 0) {
									byteBuffer.put((byte)int6);
									byteBuffer.putInt(vehiclePart.getCondition());
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState2.flags & 16) != 0) {
							for (int6 = 0; int6 < baseVehicle2.getPartCount(); ++int6) {
								vehiclePart = baseVehicle2.getPartByIndex(int6);
								if ((vehiclePart.updateFlags & 16) != 0) {
									byteBuffer.put((byte)int6);
									vehiclePart.getModData().save(byteBuffer);
								}
							}

							byteBuffer.put((byte)-1);
						}

						InventoryItem inventoryItem2;
						if ((serverVehicleState2.flags & 32) != 0) {
							for (int6 = 0; int6 < baseVehicle2.getPartCount(); ++int6) {
								vehiclePart = baseVehicle2.getPartByIndex(int6);
								if ((vehiclePart.updateFlags & 32) != 0) {
									inventoryItem2 = vehiclePart.getInventoryItem();
									if (inventoryItem2 instanceof DrainableComboItem) {
										byteBuffer.put((byte)int6);
										byteBuffer.putFloat(((DrainableComboItem)inventoryItem2).getUsedDelta());
									}
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState2.flags & 128) != 0) {
							for (int6 = 0; int6 < baseVehicle2.getPartCount(); ++int6) {
								vehiclePart = baseVehicle2.getPartByIndex(int6);
								if ((vehiclePart.updateFlags & 128) != 0) {
									byteBuffer.put((byte)int6);
									inventoryItem2 = vehiclePart.getInventoryItem();
									if (inventoryItem2 == null) {
										byteBuffer.put((byte)0);
									} else {
										byteBuffer.put((byte)1);
										try {
											vehiclePart.getInventoryItem().save(byteBuffer, false);
										} catch (Exception exception) {
											exception.printStackTrace();
										}
									}
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState2.flags & 512) != 0) {
							for (int6 = 0; int6 < baseVehicle2.getPartCount(); ++int6) {
								vehiclePart = baseVehicle2.getPartByIndex(int6);
								if ((vehiclePart.updateFlags & 512) != 0) {
									byteBuffer.put((byte)int6);
									vehiclePart.getDoor().save(byteBuffer);
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState2.flags & 256) != 0) {
							for (int6 = 0; int6 < baseVehicle2.getPartCount(); ++int6) {
								vehiclePart = baseVehicle2.getPartByIndex(int6);
								if ((vehiclePart.updateFlags & 256) != 0) {
									byteBuffer.put((byte)int6);
									vehiclePart.getWindow().save(byteBuffer);
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState2.flags & 64) != 0) {
							byteBuffer.put((byte)baseVehicle2.models.size());
							for (int6 = 0; int6 < baseVehicle2.models.size(); ++int6) {
								BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)baseVehicle2.models.get(int6);
								byteBuffer.put((byte)modelInfo.part.getIndex());
								byteBuffer.put((byte)modelInfo.part.getScriptPart().models.indexOf(modelInfo.scriptModel));
							}
						}

						if ((serverVehicleState2.flags & 8192) != 0) {
							byteBuffer.putFloat((float)baseVehicle2.engineSpeed);
							byteBuffer.putFloat(baseVehicle2.throttle);
						}

						int6 = byteBuffer.position();
						int7 = byteBuffer.position() - int4;
						byteBuffer.position(int3);
						byteBuffer.putShort((short)int7);
						byteBuffer.position(int6);
					}
				}

				udpConnection.endPacketImmediate();
			} catch (Exception exception2) {
				udpConnection.cancelPacket();
				exception2.printStackTrace();
			}
		}
	}

	public void serverPacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		short short1;
		byte byte2;
		BaseVehicle baseVehicle;
		int int1;
		UdpConnection udpConnection2;
		int int2;
		IsoPlayer player;
		int int3;
		BaseVehicle baseVehicle2;
		boolean boolean1;
		short short2;
		BaseVehicle baseVehicle3;
		IsoGameCharacter gameCharacter;
		int int4;
		IsoPlayer player2;
		UdpConnection udpConnection3;
		switch (byte1) {
		case 1: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			String string = GameWindow.ReadString(byteBuffer);
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					baseVehicle3.setCharacterPosition(gameCharacter, byte2, string);
					this.sendPassengerPosition(baseVehicle3, byte2, string, udpConnection);
				}
			}

			break;
		
		case 2: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			short2 = byteBuffer.getShort();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					player2 = (IsoPlayer)GameServer.IDToPlayerMap.get(Integer.valueOf(short2));
					DebugLog.log((player2 == null ? "unknown player" : player2.getUsername()) + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					return;
				}

				for (int4 = 0; int4 < GameServer.udpEngine.connections.size(); ++int4) {
					udpConnection3 = (UdpConnection)GameServer.udpEngine.connections.get(int4);
					for (int2 = 0; int2 < 4; ++int2) {
						player = udpConnection3.players[int2];
						if (player != null && player.OnlineID == short2) {
							this.noise(player.getUsername() + " got in vehicle " + baseVehicle3.VehicleID + " seat " + byte2);
							baseVehicle3.enter(byte2, player);
							this.sendREnter(baseVehicle3, byte2, player);
							baseVehicle3.authorizationServerOnSeat();
							break;
						}
					}
				}
			}

			break;
		
		case 3: 
			short1 = byteBuffer.getShort();
			short short3 = byteBuffer.getShort();
			baseVehicle = this.IDToVehicle.get(short1);
			if (baseVehicle != null) {
				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					for (int4 = 0; int4 < 4; ++int4) {
						IsoPlayer player3 = udpConnection2.players[int4];
						if (player3 != null && player3.OnlineID == short3) {
							baseVehicle.exit(player3);
							this.sendRExit(baseVehicle, player3);
							baseVehicle.authorizationServerOnSeat();
							break;
						}
					}
				}
			}

			break;
		
		case 4: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			short2 = byteBuffer.getShort();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					player2 = (IsoPlayer)GameServer.IDToPlayerMap.get(Integer.valueOf(short2));
					DebugLog.log((player2 == null ? "unknown player" : player2.getUsername()) + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					return;
				}

				for (int4 = 0; int4 < GameServer.udpEngine.connections.size(); ++int4) {
					udpConnection3 = (UdpConnection)GameServer.udpEngine.connections.get(int4);
					for (int2 = 0; int2 < 4; ++int2) {
						player = udpConnection3.players[int2];
						if (player != null && player.OnlineID == short2) {
							baseVehicle3.switchSeat(player, byte2);
							this.sendSwichSeat(baseVehicle3, byte2, player);
							baseVehicle3.authorizationServerOnSeat();
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
			this.noise("unknown vehicle packet " + byte1);
			break;
		
		case 9: 
			short1 = byteBuffer.getShort();
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				boolean1 = baseVehicle2.authorizationServerOnOwnerData(udpConnection);
				if (boolean1) {
					float[] floatArray = this.tempFloats;
					long long1 = byteBuffer.getLong();
					baseVehicle2.physics.clientForce = byteBuffer.getFloat();
					for (int int5 = 0; int5 < floatArray.length; ++int5) {
						floatArray[int5] = byteBuffer.getFloat();
					}

					baseVehicle2.netLinearVelocity.x = floatArray[7];
					baseVehicle2.netLinearVelocity.y = floatArray[8];
					baseVehicle2.netLinearVelocity.z = floatArray[9];
					WorldSimulation.instance.setOwnVehiclePhysics(short1, floatArray);
				}
			}

			break;
		
		case 11: 
			short1 = byteBuffer.getShort();
			for (int3 = 0; int3 < short1; ++int3) {
				short2 = byteBuffer.getShort();
				baseVehicle3 = this.IDToVehicle.get(short2);
				if (baseVehicle3 != null) {
					baseVehicle3.updateFlags = (short)(baseVehicle3.updateFlags | 1);
					this.sendVehicles(udpConnection);
				}
			}

			return;
		
		case 12: 
			short1 = byteBuffer.getShort();
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				baseVehicle2.updateFlags = (short)(baseVehicle2.updateFlags | 2);
				this.sendVehicles(udpConnection);
			}

			break;
		
		case 14: 
			short1 = byteBuffer.getShort();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				baseVehicle3.engineSpeed = (double)float1;
				baseVehicle3.throttle = float2;
				baseVehicle3.updateFlags = (short)(baseVehicle3.updateFlags | 8192);
			}

			break;
		
		case 15: 
			short1 = byteBuffer.getShort();
			int3 = byteBuffer.getInt();
			boolean1 = byteBuffer.get() == 1;
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				baseVehicle3.authorizationServerCollide(int3, boolean1);
			}

			break;
		
		case 16: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			baseVehicle = this.IDToVehicle.get(short1);
			if (baseVehicle != null) {
				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (udpConnection2 != udpConnection) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)5, byteBufferWriter);
						byteBufferWriter.bb.put((byte)16);
						byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
						byteBufferWriter.bb.put(byte2);
						udpConnection2.endPacketImmediate();
					}
				}
			}

		
		}
	}

	public static void serverSendVehiclesConfig(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)10);
		byteBufferWriter.bb.putShort((short)ServerOptions.getInstance().PhysicsDelay.getValue());
		udpConnection.endPacket();
	}

	private void vehiclePosUpdate(BaseVehicle baseVehicle, float[] floatArray) {
		byte byte1 = 0;
		Transform transform = this.posUpdateVars.transform;
		Vector3f vector3f = this.posUpdateVars.vector3f;
		Quaternionf quaternionf = this.posUpdateVars.quatf;
		float[] floatArray2 = this.posUpdateVars.wheelSteer;
		float[] floatArray3 = this.posUpdateVars.wheelRotation;
		float[] floatArray4 = this.posUpdateVars.wheelSkidInfo;
		int int1 = byte1 + 1;
		float float1 = floatArray[byte1] - WorldSimulation.instance.offsetX;
		float float2 = floatArray[int1++] - WorldSimulation.instance.offsetY;
		float float3 = floatArray[int1++];
		transform.origin.set(float1, float3, float2);
		float float4 = floatArray[int1++];
		float float5 = floatArray[int1++];
		float float6 = floatArray[int1++];
		float float7 = floatArray[int1++];
		quaternionf.set(float4, float5, float6, float7);
		quaternionf.normalize();
		transform.setRotation(quaternionf);
		float float8 = floatArray[int1++];
		float float9 = floatArray[int1++];
		float float10 = floatArray[int1++];
		vector3f.set(float8, float9, float10);
		float float11 = floatArray[int1++];
		int int2;
		for (int2 = 0; int2 < 4; ++int2) {
			floatArray2[int2] = floatArray[int1++];
			floatArray3[int2] = floatArray[int1++];
			floatArray4[int2] = floatArray[int1++];
		}

		baseVehicle.jniTransform.set(transform);
		baseVehicle.jniLinearVelocity.set((Vector3fc)vector3f);
		baseVehicle.netLinearVelocity.set((Vector3fc)vector3f);
		baseVehicle.jniTransform.basis.getScale(vector3f);
		if ((double)vector3f.x < 0.99 || (double)vector3f.y < 0.99 || (double)vector3f.z < 0.99) {
			baseVehicle.jniTransform.basis.scale(1.0F / vector3f.x, 1.0F / vector3f.y, 1.0F / vector3f.z);
		}

		baseVehicle.jniSpeed = baseVehicle.jniLinearVelocity.length();
		for (int2 = 0; int2 < 4; ++int2) {
			baseVehicle.wheelInfo[int2].steering = floatArray2[int2];
			baseVehicle.wheelInfo[int2].rotation = floatArray3[int2];
			baseVehicle.wheelInfo[int2].skidInfo = floatArray4[int2];
		}

		baseVehicle.polyDirty = true;
	}

	public void clientUpdate() {
		if (this.vehiclesWaitUpdatesFrequency.Check()) {
			if (this.vehiclesWaitUpdates.size() > 0) {
				UdpConnection udpConnection = GameClient.connection;
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)5, byteBufferWriter);
				byteBufferWriter.bb.put((byte)11);
				byteBufferWriter.bb.putShort((short)this.vehiclesWaitUpdates.size());
				for (int int1 = 0; int1 < this.vehiclesWaitUpdates.size(); ++int1) {
					byteBufferWriter.bb.putShort(this.vehiclesWaitUpdates.get(int1));
				}

				udpConnection.endPacketImmediate();
			}

			this.vehiclesWaitUpdates.clear();
		}

		ArrayList arrayList = this.getVehicles();
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int2);
			if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
				float[] floatArray = this.tempFloats;
				if (baseVehicle.interpolation.interpolationDataGetPR(floatArray) && baseVehicle.netPlayerAuthorization != 3 && baseVehicle.netPlayerAuthorization != 1) {
					Bullet.setOwnVehiclePhysics(baseVehicle.VehicleID, floatArray);
					byte byte1 = 0;
					int int3 = byte1 + 1;
					float float1 = floatArray[byte1];
					float float2 = floatArray[int3++];
					float float3 = floatArray[int3++];
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
					this.clientUpdateVehiclePos(baseVehicle, float1, float2, float3, square);
					baseVehicle.limitPhysicValid.BlockCheck();
					if (GameClient.bClient) {
						this.vehiclePosUpdate(baseVehicle, floatArray);
					}
				}
			} else {
				baseVehicle.interpolation.setVehicleData(baseVehicle);
			}
		}
	}

	private void clientUpdateVehiclePos(BaseVehicle baseVehicle, float float1, float float2, float float3, IsoGridSquare square) {
		baseVehicle.setX(float1);
		baseVehicle.setY(float2);
		baseVehicle.setZ(float3);
		baseVehicle.square = square;
		baseVehicle.setCurrent(square);
		if (square != null) {
			if (baseVehicle.chunk != null && baseVehicle.chunk != square.chunk) {
				baseVehicle.chunk.vehicles.remove(baseVehicle);
			}

			baseVehicle.chunk = baseVehicle.square.chunk;
			if (!baseVehicle.chunk.vehicles.contains(baseVehicle)) {
				baseVehicle.chunk.vehicles.add(baseVehicle);
				IsoChunk.addFromCheckedVehicles(baseVehicle);
			}

			if (!baseVehicle.addedToWorld) {
				baseVehicle.addToWorld();
			}
		} else {
			baseVehicle.removeFromWorld();
			baseVehicle.removeFromSquare();
		}

		baseVehicle.polyDirty = true;
	}

	private void clientReceiveUpdateFull(ByteBuffer byteBuffer, short short1) throws IOException {
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		short short2 = byteBuffer.getShort();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
		if (this.IDToVehicle.containsKey(short1)) {
			BaseVehicle baseVehicle = this.IDToVehicle.get(short1);
			this.noise("ERROR: got full update for KNOWN vehicle id=" + short1);
			byteBuffer.get();
			byteBuffer.getInt();
			this.tempVehicle.parts.clear();
			this.tempVehicle.load(byteBuffer, 143);
			if (baseVehicle.physics != null) {
				this.tempTransform.setRotation(this.tempVehicle.savedRot);
				this.tempTransform.origin.set(float1 - WorldSimulation.instance.offsetX, float3, float2 - WorldSimulation.instance.offsetY);
				baseVehicle.setWorldTransform(this.tempTransform);
			}

			baseVehicle.netPlayerFromServerUpdate(byte1, int1);
			this.clientUpdateVehiclePos(baseVehicle, float1, float2, float3, square);
		} else {
			boolean boolean1 = byteBuffer.get() != 0;
			int int2 = byteBuffer.getInt();
			if (!boolean1 || int2 != "Vehicle".hashCode()) {
				DebugLog.log("Error: clientReceiveUpdateFull: packet broken");
			}

			BaseVehicle baseVehicle2 = new BaseVehicle(IsoWorld.instance.CurrentCell);
			if (baseVehicle2 == null || !(baseVehicle2 instanceof BaseVehicle)) {
				return;
			}

			BaseVehicle baseVehicle3 = (BaseVehicle)baseVehicle2;
			baseVehicle3.VehicleID = short1;
			baseVehicle3.square = square;
			baseVehicle3.setCurrent(square);
			baseVehicle3.load(byteBuffer, 143);
			if (square != null) {
				baseVehicle3.chunk = baseVehicle3.square.chunk;
				baseVehicle3.chunk.vehicles.add(baseVehicle3);
				baseVehicle3.addToWorld();
			}

			IsoChunk.addFromCheckedVehicles(baseVehicle3);
			baseVehicle3.netPlayerFromServerUpdate(byte1, int1);
			this.registerVehicle(baseVehicle3);
			for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
				IsoPlayer player = IsoPlayer.players[int3];
				if (player != null && !player.isDead() && player.getVehicle() == null) {
					IsoWorld.instance.CurrentCell.putInVehicle(player);
				}
			}

			if (baseVehicle3.trace) {
				this.noise("added vehicle id=" + baseVehicle3.VehicleID + (square == null ? " (delayed)" : ""));
			}
		}
	}

	private void clientReceiveUpdate(ByteBuffer byteBuffer) throws IOException {
		short short1 = byteBuffer.getShort();
		short short2 = byteBuffer.getShort();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		short short3 = byteBuffer.getShort();
		VehicleCache.vehicleUpdate(short1, float1, float2, float3);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
		BaseVehicle baseVehicle = this.IDToVehicle.get(short1);
		if (baseVehicle == null && square == null) {
			if (byteBuffer.limit() > byteBuffer.position() + short3) {
				byteBuffer.position(byteBuffer.position() + short3);
			}
		} else {
			int int1;
			boolean boolean1;
			if (baseVehicle != null && square == null) {
				boolean1 = true;
				for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && player.getVehicle() == baseVehicle) {
						boolean1 = false;
						player.setPosition(float1, float2, float3);
						this.sendReqestGetPosition(short1);
					}
				}

				if (boolean1) {
					baseVehicle.removeFromWorld();
					baseVehicle.removeFromSquare();
				}

				if (byteBuffer.limit() > byteBuffer.position() + short3) {
					byteBuffer.position(byteBuffer.position() + short3);
				}
			} else {
				int int2;
				if ((short2 & 1) != 0) {
					this.clientReceiveUpdateFull(byteBuffer, short1);
					int2 = this.vehiclesWaitUpdates.indexOf(short1);
					if (int2 >= 0) {
						this.vehiclesWaitUpdates.removeAt(int2);
					}
				} else if (baseVehicle == null && square != null) {
					this.sendReqestGetFull(short1);
					if (byteBuffer.limit() > byteBuffer.position() + short3) {
						byteBuffer.position(byteBuffer.position() + short3);
					}
				} else {
					byte byte1;
					if ((short2 & 16384) != 0) {
						byte1 = byteBuffer.get();
						int1 = byteBuffer.getInt();
						if (baseVehicle != null) {
							baseVehicle.netPlayerFromServerUpdate(byte1, int1);
						}
					}

					if ((short2 & 2) != 0) {
						if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
							baseVehicle.interpolation.interpolationDataAdd(byteBuffer);
						} else if (byteBuffer.limit() > byteBuffer.position() + 98) {
							byteBuffer.position(byteBuffer.position() + 98);
						}
					}

					if ((short2 & 4) != 0) {
						this.noise("received update Engine id=" + short1);
						byte1 = byteBuffer.get();
						if (byte1 >= 0 && byte1 < BaseVehicle.engineStateTypes.Values.length) {
							switch (BaseVehicle.engineStateTypes.Values[byte1]) {
							case Idle: 
								baseVehicle.engineDoIdle();
							
							case Starting: 
							
							default: 
								break;
							
							case RetryingStarting: 
								baseVehicle.engineDoRetryingStarting();
								break;
							
							case StartingSuccess: 
								baseVehicle.engineDoStartingSuccess();
								break;
							
							case StartingFailed: 
								baseVehicle.engineDoStartingFailed();
								break;
							
							case StartingFailedNoPower: 
								baseVehicle.engineDoStartingFailedNoPower();
								break;
							
							case Running: 
								baseVehicle.engineDoRunning();
								break;
							
							case Stalling: 
								baseVehicle.engineDoStalling();
								break;
							
							case ShutingDown: 
								baseVehicle.engineDoShutingDown();
							
							}
						} else {
							DebugLog.log("ERROR: VehicleManager.clientReceiveUpdate get invalid data");
						}
					}

					String string;
					byte byte2;
					boolean boolean2;
					if ((short2 & 4096) != 0) {
						this.noise("received car properties update id=" + short1);
						baseVehicle.setHotwired(byteBuffer.get() == 1);
						baseVehicle.setHotwiredBroken(byteBuffer.get() == 1);
						boolean1 = byteBuffer.get() == 1;
						boolean2 = byteBuffer.get() == 1;
						InventoryItem inventoryItem = null;
						if (byteBuffer.get() == 1) {
							string = GameWindow.ReadStringUTF(byteBuffer);
							byte2 = byteBuffer.get();
							inventoryItem = InventoryItemFactory.CreateItem(string);
							if (inventoryItem != null) {
								inventoryItem.load(byteBuffer, 143, false);
							}
						}

						baseVehicle.syncKeyInIgnition(boolean1, boolean2, inventoryItem);
					}

					if ((short2 & 8) != 0) {
						this.noise("received update Lights id=" + short1);
						baseVehicle.setHeadlightsOn(byteBuffer.get() == 1);
						baseVehicle.setStoplightsOn(byteBuffer.get() == 1);
						for (int2 = 0; int2 < baseVehicle.getLightCount(); ++int2) {
							boolean2 = byteBuffer.get() == 1;
							baseVehicle.getLightByIndex(int2).getLight().setActive(boolean2);
						}
					}

					byte byte3;
					byte byte4;
					if ((short2 & 1024) != 0) {
						this.noise("received update Sounds id=" + short1);
						boolean1 = byteBuffer.get() == 1;
						boolean2 = byteBuffer.get() == 1;
						byte3 = byteBuffer.get();
						byte4 = byteBuffer.get();
						if (boolean1 != baseVehicle.soundHornOn) {
							if (boolean1) {
								baseVehicle.onHornStart();
							} else {
								baseVehicle.onHornStop();
							}
						}

						if (boolean2 != baseVehicle.soundBackMoveOn) {
							if (boolean2) {
								baseVehicle.onBackMoveSignalStart();
							} else {
								baseVehicle.onBackMoveSignalStop();
							}
						}

						if (baseVehicle.lightbarLightsMode.get() != byte3) {
							baseVehicle.setLightbarLightsMode(byte3);
						}

						if (baseVehicle.lightbarSirenMode.get() != byte4) {
							baseVehicle.setLightbarSirenMode(byte4);
						}
					}

					VehiclePart vehiclePart;
					if ((short2 & 2048) != 0) {
						for (byte1 = byteBuffer.get(); byte1 != -1; byte1 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte1);
							this.noise("received update PartCondition id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 2048);
							vehiclePart.setCondition(byteBuffer.getInt());
						}

						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 16) != 0) {
						for (byte1 = byteBuffer.get(); byte1 != -1; byte1 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte1);
							this.noise("received update PartModData id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.getModData().load((ByteBuffer)byteBuffer, 143);
							if (vehiclePart.isContainer()) {
								vehiclePart.setContainerContentAmount(vehiclePart.getContainerContentAmount());
							}
						}
					}

					float float4;
					VehiclePart vehiclePart2;
					if ((short2 & 32) != 0) {
						for (byte1 = byteBuffer.get(); byte1 != -1; byte1 = byteBuffer.get()) {
							float4 = byteBuffer.getFloat();
							vehiclePart2 = baseVehicle.getPartByIndex(byte1);
							this.noise("received update PartUsedDelta id=" + short1 + " part=" + vehiclePart2.getId());
							InventoryItem inventoryItem2 = vehiclePart2.getInventoryItem();
							if (inventoryItem2 instanceof DrainableComboItem) {
								((DrainableComboItem)inventoryItem2).setUsedDelta(float4);
							}
						}
					}

					if ((short2 & 128) != 0) {
						for (byte1 = byteBuffer.get(); byte1 != -1; byte1 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte1);
							this.noise("received update PartItem id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 128);
							boolean boolean3 = byteBuffer.get() != 0;
							if (boolean3) {
								string = GameWindow.ReadStringUTF(byteBuffer);
								byte2 = byteBuffer.get();
								InventoryItem inventoryItem3 = InventoryItemFactory.CreateItem(string);
								if (inventoryItem3 == null) {
									this.noise("unknown item type \"" + string + "\"");
									return;
								}

								try {
									inventoryItem3.load(byteBuffer, 143, false);
								} catch (Exception exception) {
									exception.printStackTrace();
									return;
								}

								vehiclePart.setInventoryItem(inventoryItem3);
							} else {
								vehiclePart.setInventoryItem((InventoryItem)null);
							}

							int int3 = vehiclePart.getWheelIndex();
							if (int3 != -1) {
								baseVehicle.setTireRemoved(int3, !boolean3);
							}

							if (vehiclePart.isContainer()) {
								LuaEventManager.triggerEvent("OnContainerUpdate");
							}
						}
					}

					if ((short2 & 512) != 0) {
						for (byte1 = byteBuffer.get(); byte1 != -1; byte1 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte1);
							this.noise("received update PartDoor id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.getDoor().load(byteBuffer, 143);
						}

						LuaEventManager.triggerEvent("OnContainerUpdate");
						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 256) != 0) {
						for (byte1 = byteBuffer.get(); byte1 != -1; byte1 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte1);
							this.noise("received update PartWindow id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.getWindow().load(byteBuffer, 143);
						}

						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 64) != 0) {
						this.oldModels.clear();
						this.oldModels.addAll(baseVehicle.models);
						this.curModels.clear();
						byte1 = byteBuffer.get();
						for (int1 = 0; int1 < byte1; ++int1) {
							byte3 = byteBuffer.get();
							byte4 = byteBuffer.get();
							VehiclePart vehiclePart3 = baseVehicle.getPartByIndex(byte3);
							VehicleScript.Model model = (VehicleScript.Model)vehiclePart3.getScriptPart().models.get(byte4);
							BaseVehicle.ModelInfo modelInfo = baseVehicle.setModelVisible(vehiclePart3, model, true);
							this.curModels.add(modelInfo);
						}

						for (int1 = 0; int1 < this.oldModels.size(); ++int1) {
							BaseVehicle.ModelInfo modelInfo2 = (BaseVehicle.ModelInfo)this.oldModels.get(int1);
							if (!this.curModels.contains(modelInfo2)) {
								baseVehicle.setModelVisible(modelInfo2.part, modelInfo2.scriptModel, false);
							}
						}

						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 8192) != 0) {
						float float5 = byteBuffer.getFloat();
						float4 = byteBuffer.getFloat();
						if (!(baseVehicle.getDriver() instanceof IsoPlayer) || !((IsoPlayer)baseVehicle.getDriver()).isLocalPlayer()) {
							baseVehicle.engineSpeed = (double)float5;
							baseVehicle.throttle = float4;
						}
					}

					boolean1 = false;
					for (int1 = 0; int1 < baseVehicle.getPartCount(); ++int1) {
						vehiclePart2 = baseVehicle.getPartByIndex(int1);
						if (vehiclePart2.updateFlags != 0) {
							if ((vehiclePart2.updateFlags & 2048) != 0 && (vehiclePart2.updateFlags & 128) == 0) {
								vehiclePart2.doInventoryItemStats(vehiclePart2.getInventoryItem(), vehiclePart2.getMechanicSkillInstaller());
								boolean1 = true;
							}

							vehiclePart2.updateFlags = 0;
						}
					}

					if (boolean1) {
						baseVehicle.updatePartStats();
						baseVehicle.updateBulletStats();
					}
				}
			}
		}
	}

	public void clientPacket(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		short short1;
		byte byte2;
		BaseVehicle baseVehicle;
		BaseVehicle baseVehicle2;
		IsoGameCharacter gameCharacter;
		short short2;
		IsoPlayer player;
		switch (byte1) {
		case 1: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			String string = GameWindow.ReadString(byteBuffer);
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				IsoGameCharacter gameCharacter2 = baseVehicle2.getCharacter(byte2);
				if (gameCharacter2 != null) {
					baseVehicle2.setCharacterPosition(gameCharacter2, byte2, string);
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
			this.noise("unknown vehicle packet " + byte1);
			break;
		
		case 4: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			short2 = byteBuffer.getShort();
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				player = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(short2));
				if (player != null) {
					gameCharacter = baseVehicle2.getCharacter(byte2);
					if (gameCharacter == null) {
						baseVehicle2.switchSeatRSync(player, byte2);
					} else if (player != gameCharacter) {
						DebugLog.log(player.getUsername() + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					}
				}
			}

			break;
		
		case 5: 
			if (this.tempVehicle == null || this.tempVehicle.getCell() != IsoWorld.instance.CurrentCell) {
				this.tempVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			}

			short1 = byteBuffer.getShort();
			for (int int1 = 0; int1 < short1; ++int1) {
				try {
					this.clientReceiveUpdate(byteBuffer);
				} catch (Exception exception) {
					exception.printStackTrace();
					return;
				}
			}

			return;
		
		case 6: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			short2 = byteBuffer.getShort();
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				player = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(short2));
				if (player != null) {
					gameCharacter = baseVehicle2.getCharacter(byte2);
					if (gameCharacter == null) {
						DebugLog.log(player.getUsername() + " got in vehicle " + baseVehicle2.VehicleID + " seat " + byte2);
						baseVehicle2.enterRSync(byte2, player, baseVehicle2);
					} else if (player != gameCharacter) {
						DebugLog.log(player.getUsername() + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					}
				}
			}

			break;
		
		case 7: 
			short1 = byteBuffer.getShort();
			short short3 = byteBuffer.getShort();
			baseVehicle = this.IDToVehicle.get(short1);
			if (baseVehicle != null) {
				IsoPlayer player2 = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(short3));
				if (player2 != null) {
					baseVehicle.exitRSync(player2);
				}
			}

			break;
		
		case 8: 
			short1 = byteBuffer.getShort();
			if (this.IDToVehicle.containsKey(short1)) {
				BaseVehicle baseVehicle3 = this.IDToVehicle.get(short1);
				if (baseVehicle3.trace) {
					this.noise("server removed vehicle id=" + short1);
				}

				baseVehicle3.serverRemovedFromWorld = true;
				try {
					baseVehicle3.removeFromWorld();
					baseVehicle3.removeFromSquare();
				} finally {
					if (this.IDToVehicle.containsKey(short1)) {
						this.unregisterVehicle(baseVehicle3);
					}
				}
			}

			VehicleCache.remove(short1);
			break;
		
		case 13: 
			short1 = byteBuffer.getShort();
			Vector3f vector3f = new Vector3f();
			Vector3f vector3f2 = new Vector3f();
			vector3f.x = byteBuffer.getFloat();
			vector3f.y = byteBuffer.getFloat();
			vector3f.z = byteBuffer.getFloat();
			vector3f2.x = byteBuffer.getFloat();
			vector3f2.y = byteBuffer.getFloat();
			vector3f2.z = byteBuffer.getFloat();
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				Bullet.applyCentralForceToVehicle(baseVehicle2.VehicleID, vector3f.x, vector3f.y, vector3f.z);
				Vector3f vector3f3 = vector3f2.cross(vector3f);
				Bullet.applyTorqueToVehicle(baseVehicle2.VehicleID, vector3f3.x, vector3f3.y, vector3f3.z);
			}

			break;
		
		case 16: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			baseVehicle = this.IDToVehicle.get(short1);
			if (baseVehicle != null) {
				SoundManager.instance.PlayWorldSound("VehicleCrash", baseVehicle.square, 1.0F, 20.0F, 1.0F, true);
			}

		
		}
	}

	public static void loadingClientPacket(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.position();
		byte byte1 = byteBuffer.get();
		switch (byte1) {
		case 10: 
			physicsDelay = byteBuffer.getShort();
		
		default: 
			byteBuffer.position(int1);
		
		}
	}

	public void sendCollide(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, boolean boolean1) {
		if (gameCharacter != null) {
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)15);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.putInt(((IsoPlayer)gameCharacter).OnlineID);
			byteBufferWriter.bb.put((byte)(boolean1 ? 1 : 0));
			udpConnection.endPacketImmediate();
		}
	}

	public void sendEnter(BaseVehicle baseVehicle, int int1, IsoGameCharacter gameCharacter) {
		UdpConnection udpConnection = GameClient.connection;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)2);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.put((byte)int1);
		byteBufferWriter.bb.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
		udpConnection.endPacketImmediate();
	}

	public static void sendSound(BaseVehicle baseVehicle, byte byte1) {
		UdpConnection udpConnection = GameClient.connection;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)16);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.put(byte1);
		udpConnection.endPacketImmediate();
	}

	public static void sendSoundFromServer(BaseVehicle baseVehicle, byte byte1) {
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)16);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.put(byte1);
			udpConnection.endPacketImmediate();
		}
	}

	public void sendPassengerPosition(BaseVehicle baseVehicle, int int1, String string) {
		UdpConnection udpConnection = GameClient.connection;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)1);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.put((byte)int1);
		byteBufferWriter.putUTF(string);
		udpConnection.endPacketImmediate();
	}

	public void sendPassengerPosition(BaseVehicle baseVehicle, int int1, String string, UdpConnection udpConnection) {
		for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
			if (udpConnection2 != udpConnection) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)5, byteBufferWriter);
				byteBufferWriter.bb.put((byte)1);
				byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
				byteBufferWriter.bb.put((byte)int1);
				byteBufferWriter.putUTF(string);
				udpConnection2.endPacket();
			}
		}
	}

	public void sendReqestGetFull(short short1) {
		if (!this.vehiclesWaitUpdates.contains(short1)) {
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)11);
			byteBufferWriter.bb.putShort((short)1);
			byteBufferWriter.bb.putShort(short1);
			udpConnection.endPacketImmediate();
			this.vehiclesWaitUpdates.add(short1);
		}
	}

	public void sendReqestGetFull(List list) {
		if (list != null) {
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)11);
			byteBufferWriter.bb.putShort((short)list.size());
			for (int int1 = 0; int1 < list.size(); ++int1) {
				byteBufferWriter.bb.putShort(((VehicleCache)list.get(int1)).id);
				this.vehiclesWaitUpdates.add(((VehicleCache)list.get(int1)).id);
			}

			udpConnection.endPacketImmediate();
		}
	}

	public void sendReqestGetPosition(short short1) {
		if (sendReqestGetPositionFrequency.Check()) {
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)12);
			byteBufferWriter.bb.putShort(short1);
			udpConnection.endPacketImmediate();
			this.vehiclesWaitUpdates.add(short1);
		}
	}

	public void sendAddImpulse(BaseVehicle baseVehicle, Vector3f vector3f, Vector3f vector3f2) {
		UdpConnection udpConnection = null;
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size() && udpConnection == null; ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < udpConnection2.players.length; ++int2) {
				IsoPlayer player = udpConnection2.players[int2];
				if (player != null && player.getVehicle() != null && player.getVehicle().VehicleID == baseVehicle.VehicleID) {
					udpConnection = udpConnection2;
					break;
				}
			}
		}

		if (udpConnection != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)13);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.putFloat(vector3f.x);
			byteBufferWriter.bb.putFloat(vector3f.y);
			byteBufferWriter.bb.putFloat(vector3f.z);
			byteBufferWriter.bb.putFloat(vector3f2.x);
			byteBufferWriter.bb.putFloat(vector3f2.y);
			byteBufferWriter.bb.putFloat(vector3f2.z);
			udpConnection.endPacketImmediate();
		}
	}

	public void sendREnter(BaseVehicle baseVehicle, int int1, IsoGameCharacter gameCharacter) {
		for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)6);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.put((byte)int1);
			byteBufferWriter.bb.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
			udpConnection.endPacket();
		}
	}

	public void sendSwichSeat(BaseVehicle baseVehicle, int int1, IsoGameCharacter gameCharacter) {
		if (GameClient.bClient) {
			UdpConnection udpConnection = GameClient.connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)4);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.put((byte)int1);
			byteBufferWriter.bb.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
			udpConnection.endPacketImmediate();
		}

		if (GameServer.bServer) {
			for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
				ByteBufferWriter byteBufferWriter2 = udpConnection2.startPacket();
				PacketTypes.doPacket((short)5, byteBufferWriter2);
				byteBufferWriter2.bb.put((byte)4);
				byteBufferWriter2.bb.putShort(baseVehicle.VehicleID);
				byteBufferWriter2.bb.put((byte)int1);
				byteBufferWriter2.bb.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
				udpConnection2.endPacket();
			}
		}
	}

	public void sendExit(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter) {
		UdpConnection udpConnection = GameClient.connection;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)3);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
		udpConnection.endPacketImmediate();
	}

	public void sendRExit(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)5, byteBufferWriter);
			byteBufferWriter.bb.put((byte)7);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
			udpConnection.endPacket();
		}
	}

	public void sendPhysic(BaseVehicle baseVehicle) {
		UdpConnection udpConnection = GameClient.connection;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)9);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		ByteBuffer byteBuffer = byteBufferWriter.bb;
		GameTime.getInstance();
		byteBuffer.putLong(GameTime.getServerTime());
		byteBufferWriter.bb.putFloat(baseVehicle.physics.EngineForce - baseVehicle.physics.BrakingForce);
		if (WorldSimulation.instance.getOwnVehiclePhysics(baseVehicle.VehicleID, byteBufferWriter) != 1) {
			udpConnection.cancelPacket();
		} else {
			udpConnection.endPacket();
		}
	}

	public void sendEngineSound(BaseVehicle baseVehicle, float float1, float float2) {
		UdpConnection udpConnection = GameClient.connection;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)5, byteBufferWriter);
		byteBufferWriter.bb.put((byte)14);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putFloat(float1);
		byteBufferWriter.bb.putFloat(float2);
		udpConnection.endPacket();
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
