package zombie.vehicles;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TShortShortHashMap;
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
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.objects.VehicleScript;


public final class VehicleManager {
	public static VehicleManager instance;
	private final VehicleIDMap IDToVehicle;
	private final ArrayList vehicles;
	private boolean idMapDirty;
	private final Transform tempTransform;
	private final ArrayList send;
	private final TShortArrayList vehiclesWaitUpdates;
	private final TShortShortHashMap towedVehicleMap;
	public static short physicsDelay = 500;
	public final UdpConnection[] connected;
	private final float[] tempFloats;
	private final VehicleManager.PosUpdateVars posUpdateVars;
	private final UpdateLimit vehiclesWaitUpdatesFrequency;
	private BaseVehicle tempVehicle;
	private final ArrayList oldModels;
	private final ArrayList curModels;
	private final UpdateLimit sendRequestGetPositionFrequency;
	private final UpdateLimit VehiclePhysicSyncPacketLimit;

	public VehicleManager() {
		this.IDToVehicle = VehicleIDMap.instance;
		this.vehicles = new ArrayList();
		this.idMapDirty = true;
		this.tempTransform = new Transform();
		this.send = new ArrayList();
		this.vehiclesWaitUpdates = new TShortArrayList(128);
		this.towedVehicleMap = new TShortShortHashMap();
		this.connected = new UdpConnection[512];
		this.tempFloats = new float[27];
		this.posUpdateVars = new VehicleManager.PosUpdateVars();
		this.vehiclesWaitUpdatesFrequency = new UpdateLimit(1000L);
		this.oldModels = new ArrayList();
		this.curModels = new ArrayList();
		this.sendRequestGetPositionFrequency = new UpdateLimit(500L);
		this.VehiclePhysicSyncPacketLimit = new UpdateLimit(500L);
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
						PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
						byteBufferWriter.bb.put((byte)8);
						byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
						PacketTypes.PacketType.Vehicles.send(udpConnection);
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
		if (udpConnection.isFullyConnected()) {
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

				if (boolean1 || udpConnection.RelevantTo(baseVehicle.x, baseVehicle.y)) {
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
				PacketTypes.PacketType packetType;
				if (this.VehiclePhysicSyncPacketLimit.Check()) {
					packetType = PacketTypes.PacketType.Vehicles;
				} else {
					packetType = PacketTypes.PacketType.VehiclesUnreliable;
				}

				packetType.doPacket(byteBufferWriter);
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
						byteBuffer.putFloat(baseVehicle2.jniTransform.origin.y);
						int int3 = byteBuffer.position();
						byteBuffer.putShort((short)0);
						int int4 = byteBuffer.position();
						boolean boolean2 = (serverVehicleState2.flags & 1) != 0;
						int int5;
						int int6;
						if (boolean2) {
							serverVehicleState2.flags = (short)(serverVehicleState2.flags & -2);
							baseVehicle2.netPlayerServerSendAuthorisation(byteBuffer);
							serverVehicleState2.setAuthorization(baseVehicle2);
							int5 = byteBuffer.position();
							byteBuffer.putShort((short)0);
							baseVehicle2.save(byteBuffer);
							int6 = byteBuffer.position();
							byteBuffer.position(int5);
							byteBuffer.putShort((short)(int6 - int5));
							byteBuffer.position(int6);
							int int7 = byteBuffer.position();
							int int8 = byteBuffer.position() - int4;
							byteBuffer.position(int3);
							byteBuffer.putShort((short)int8);
							byteBuffer.position(int7);
							this.writePositionOrientation(byteBuffer, baseVehicle2);
							serverVehicleState2.x = baseVehicle2.x;
							serverVehicleState2.y = baseVehicle2.y;
							serverVehicleState2.z = baseVehicle2.jniTransform.origin.y;
							serverVehicleState2.orient.set((Quaternionfc)baseVehicle2.savedRot);
						} else {
							if ((serverVehicleState2.flags & 16384) != 0) {
								baseVehicle2.netPlayerServerSendAuthorisation(byteBuffer);
								serverVehicleState2.setAuthorization(baseVehicle2);
							}

							if ((serverVehicleState2.flags & 2) != 0) {
								this.writePositionOrientation(byteBuffer, baseVehicle2);
								serverVehicleState2.x = baseVehicle2.x;
								serverVehicleState2.y = baseVehicle2.y;
								serverVehicleState2.z = baseVehicle2.jniTransform.origin.y;
								serverVehicleState2.orient.set((Quaternionfc)baseVehicle2.savedRot);
							}

							if ((serverVehicleState2.flags & 4) != 0) {
								byteBuffer.put((byte)baseVehicle2.engineState.ordinal());
								byteBuffer.putInt(baseVehicle2.engineLoudness);
								byteBuffer.putInt(baseVehicle2.enginePower);
								byteBuffer.putInt(baseVehicle2.engineQuality);
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
									inventoryItem.saveWithSize(byteBuffer, false);
								}

								byteBuffer.putFloat(baseVehicle2.getRust());
								byteBuffer.putFloat(baseVehicle2.getBloodIntensity("Front"));
								byteBuffer.putFloat(baseVehicle2.getBloodIntensity("Rear"));
								byteBuffer.putFloat(baseVehicle2.getBloodIntensity("Left"));
								byteBuffer.putFloat(baseVehicle2.getBloodIntensity("Right"));
							}

							if ((serverVehicleState2.flags & 8) != 0) {
								byteBuffer.put((byte)(baseVehicle2.getHeadlightsOn() ? 1 : 0));
								byteBuffer.put((byte)(baseVehicle2.getStoplightsOn() ? 1 : 0));
								for (int5 = 0; int5 < baseVehicle2.getLightCount(); ++int5) {
									byteBuffer.put((byte)(baseVehicle2.getLightByIndex(int5).getLight().getActive() ? 1 : 0));
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
								for (int5 = 0; int5 < baseVehicle2.getPartCount(); ++int5) {
									vehiclePart = baseVehicle2.getPartByIndex(int5);
									if ((vehiclePart.updateFlags & 2048) != 0) {
										byteBuffer.put((byte)int5);
										byteBuffer.putInt(vehiclePart.getCondition());
									}
								}

								byteBuffer.put((byte)-1);
							}

							if ((serverVehicleState2.flags & 16) != 0) {
								for (int5 = 0; int5 < baseVehicle2.getPartCount(); ++int5) {
									vehiclePart = baseVehicle2.getPartByIndex(int5);
									if ((vehiclePart.updateFlags & 16) != 0) {
										byteBuffer.put((byte)int5);
										vehiclePart.getModData().save(byteBuffer);
									}
								}

								byteBuffer.put((byte)-1);
							}

							InventoryItem inventoryItem2;
							if ((serverVehicleState2.flags & 32) != 0) {
								for (int5 = 0; int5 < baseVehicle2.getPartCount(); ++int5) {
									vehiclePart = baseVehicle2.getPartByIndex(int5);
									if ((vehiclePart.updateFlags & 32) != 0) {
										inventoryItem2 = vehiclePart.getInventoryItem();
										if (inventoryItem2 instanceof DrainableComboItem) {
											byteBuffer.put((byte)int5);
											byteBuffer.putFloat(((DrainableComboItem)inventoryItem2).getUsedDelta());
										}
									}
								}

								byteBuffer.put((byte)-1);
							}

							if ((serverVehicleState2.flags & 128) != 0) {
								for (int5 = 0; int5 < baseVehicle2.getPartCount(); ++int5) {
									vehiclePart = baseVehicle2.getPartByIndex(int5);
									if ((vehiclePart.updateFlags & 128) != 0) {
										byteBuffer.put((byte)int5);
										inventoryItem2 = vehiclePart.getInventoryItem();
										if (inventoryItem2 == null) {
											byteBuffer.put((byte)0);
										} else {
											byteBuffer.put((byte)1);
											try {
												vehiclePart.getInventoryItem().saveWithSize(byteBuffer, false);
											} catch (Exception exception) {
												exception.printStackTrace();
											}
										}
									}
								}

								byteBuffer.put((byte)-1);
							}

							if ((serverVehicleState2.flags & 512) != 0) {
								for (int5 = 0; int5 < baseVehicle2.getPartCount(); ++int5) {
									vehiclePart = baseVehicle2.getPartByIndex(int5);
									if ((vehiclePart.updateFlags & 512) != 0) {
										byteBuffer.put((byte)int5);
										vehiclePart.getDoor().save(byteBuffer);
									}
								}

								byteBuffer.put((byte)-1);
							}

							if ((serverVehicleState2.flags & 256) != 0) {
								for (int5 = 0; int5 < baseVehicle2.getPartCount(); ++int5) {
									vehiclePart = baseVehicle2.getPartByIndex(int5);
									if ((vehiclePart.updateFlags & 256) != 0) {
										byteBuffer.put((byte)int5);
										vehiclePart.getWindow().save(byteBuffer);
									}
								}

								byteBuffer.put((byte)-1);
							}

							if ((serverVehicleState2.flags & 64) != 0) {
								byteBuffer.put((byte)baseVehicle2.models.size());
								for (int5 = 0; int5 < baseVehicle2.models.size(); ++int5) {
									BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)baseVehicle2.models.get(int5);
									byteBuffer.put((byte)modelInfo.part.getIndex());
									byteBuffer.put((byte)modelInfo.part.getScriptPart().models.indexOf(modelInfo.scriptModel));
								}
							}

							if ((serverVehicleState2.flags & 8192) != 0) {
								byteBuffer.putFloat((float)baseVehicle2.engineSpeed);
								byteBuffer.putFloat(baseVehicle2.throttle);
							}

							int5 = byteBuffer.position();
							int6 = byteBuffer.position() - int4;
							byteBuffer.position(int3);
							byteBuffer.putShort((short)int6);
							byteBuffer.position(int5);
						}
					}

					packetType.send(udpConnection);
				} catch (Exception exception2) {
					udpConnection.cancelPacket();
					exception2.printStackTrace();
				}
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
		short short2;
		BaseVehicle baseVehicle2;
		boolean boolean1;
		short short3;
		BaseVehicle baseVehicle3;
		IsoGameCharacter gameCharacter;
		int int3;
		IsoPlayer player2;
		UdpConnection udpConnection3;
		String string;
		switch (byte1) {
		case 1: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			String string2 = GameWindow.ReadString(byteBuffer);
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					baseVehicle3.setCharacterPosition(gameCharacter, byte2, string2);
					this.sendPassengerPosition(baseVehicle3, byte2, string2, udpConnection);
				}
			}

			break;
		
		case 2: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			short3 = byteBuffer.getShort();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					player2 = (IsoPlayer)GameServer.IDToPlayerMap.get(short3);
					string = player2 == null ? "unknown player" : player2.getUsername();
					DebugLog.log(string + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					return;
				}

				for (int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
					udpConnection3 = (UdpConnection)GameServer.udpEngine.connections.get(int3);
					for (int2 = 0; int2 < 4; ++int2) {
						player = udpConnection3.players[int2];
						if (player != null && player.OnlineID == short3) {
							this.noise(player.getUsername() + " got in vehicle " + baseVehicle3.VehicleID + " seat " + byte2);
							baseVehicle3.enter(byte2, player);
							this.sendREnter(baseVehicle3, byte2, player);
							baseVehicle3.authorizationServerOnSeat();
							break;
						}
					}
				}

				player2 = (IsoPlayer)GameServer.IDToPlayerMap.get(short3);
				if (baseVehicle3.getVehicleTowing() != null && baseVehicle3.getDriver() == player2) {
					baseVehicle3.getVehicleTowing().setNetPlayerAuthorization((byte)3);
					baseVehicle3.getVehicleTowing().netPlayerId = player2.OnlineID;
					baseVehicle3.getVehicleTowing().netPlayerTimeout = 30;
				} else if (baseVehicle3.getVehicleTowedBy() != null) {
					if (baseVehicle3.getVehicleTowedBy().getDriver() != null) {
						baseVehicle3.setNetPlayerAuthorization((byte)3);
						baseVehicle3.netPlayerId = baseVehicle3.getVehicleTowedBy().getDriver().getOnlineID();
						baseVehicle3.netPlayerTimeout = 30;
					} else {
						baseVehicle3.setNetPlayerAuthorization((byte)0);
						baseVehicle3.netPlayerId = -1;
					}
				}
			}

			break;
		
		case 3: 
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			baseVehicle = this.IDToVehicle.get(short1);
			if (baseVehicle != null) {
				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					for (int3 = 0; int3 < 4; ++int3) {
						IsoPlayer player3 = udpConnection2.players[int3];
						if (player3 != null && player3.OnlineID == short2) {
							baseVehicle.exit(player3);
							this.sendRExit(baseVehicle, player3);
							if (baseVehicle.getVehicleTowedBy() == null) {
								baseVehicle.authorizationServerOnSeat();
							}

							break;
						}
					}
				}
			}

			break;
		
		case 4: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			short3 = byteBuffer.getShort();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					player2 = (IsoPlayer)GameServer.IDToPlayerMap.get(short3);
					string = player2 == null ? "unknown player" : player2.getUsername();
					DebugLog.log(string + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					return;
				}

				for (int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
					udpConnection3 = (UdpConnection)GameServer.udpEngine.connections.get(int3);
					for (int2 = 0; int2 < 4; ++int2) {
						player = udpConnection3.players[int2];
						if (player != null && player.OnlineID == short3) {
							baseVehicle3.switchSeat(player, byte2);
							this.sendSwitchSeat(baseVehicle3, byte2, player);
							if (baseVehicle3.getDriver() == player) {
								baseVehicle3.authorizationServerOnSeat();
							}

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
					for (int int4 = 0; int4 < floatArray.length; ++int4) {
						floatArray[int4] = byteBuffer.getFloat();
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
			for (int int5 = 0; int5 < short1; ++int5) {
				short3 = byteBuffer.getShort();
				DebugLog.log(DebugType.Vehicle, "send full update for vehicle #" + short3 + " due to request");
				baseVehicle3 = this.IDToVehicle.get(short3);
				if (baseVehicle3 != null) {
					BaseVehicle.ServerVehicleState serverVehicleState = baseVehicle3.connectionState[udpConnection.index];
					serverVehicleState.flags = (short)(serverVehicleState.flags | 1);
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
			short2 = byteBuffer.getShort();
			boolean1 = byteBuffer.get() == 1;
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				baseVehicle3.authorizationServerCollide(short2, boolean1);
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
						PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
						byteBufferWriter.bb.put((byte)16);
						byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
						byteBufferWriter.bb.put(byte2);
						PacketTypes.PacketType.Vehicles.send(udpConnection2);
					}
				}
			}

		
		}
	}

	public static void serverSendVehiclesConfig(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)10);
		byteBufferWriter.bb.putShort((short)500);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void serverSendInitialWorldState(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)19);
		byteBufferWriter.bb.putShort((short)this.towedVehicleMap.size());
		this.towedVehicleMap.forEachEntry((udpConnectionx,byteBufferWriterx)->{
			byteBufferWriter.putShort(udpConnectionx);
			byteBufferWriter.putShort(byteBufferWriterx);
			return true;
		});
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	private void vehiclePosUpdate(BaseVehicle baseVehicle, float[] floatArray) {
		byte byte1 = 0;
		Transform transform = this.posUpdateVars.transform;
		Vector3f vector3f = this.posUpdateVars.vector3f;
		Quaternionf quaternionf = this.posUpdateVars.quatf;
		float[] floatArray2 = this.posUpdateVars.wheelSteer;
		float[] floatArray3 = this.posUpdateVars.wheelRotation;
		float[] floatArray4 = this.posUpdateVars.wheelSkidInfo;
		float[] floatArray5 = this.posUpdateVars.wheelSuspensionLength;
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
		int int2 = (int)floatArray[int1++];
		for (int int3 = 0; int3 < int2; ++int3) {
			floatArray2[int3] = floatArray[int1++];
			floatArray3[int3] = floatArray[int1++];
			floatArray4[int3] = floatArray[int1++];
			floatArray5[int3] = floatArray[int1++];
		}

		baseVehicle.jniTransform.set(transform);
		baseVehicle.jniLinearVelocity.set((Vector3fc)vector3f);
		baseVehicle.netLinearVelocity.set((Vector3fc)vector3f);
		baseVehicle.jniTransform.basis.getScale(vector3f);
		if ((double)vector3f.x < 0.99 || (double)vector3f.y < 0.99 || (double)vector3f.z < 0.99) {
			baseVehicle.jniTransform.basis.scale(1.0F / vector3f.x, 1.0F / vector3f.y, 1.0F / vector3f.z);
		}

		baseVehicle.jniSpeed = baseVehicle.jniLinearVelocity.length() * 3.6F;
		Vector3f vector3f2 = baseVehicle.getForwardVector(BaseVehicle.allocVector3f());
		if (vector3f2.dot(baseVehicle.jniLinearVelocity) < 0.0F) {
			baseVehicle.jniSpeed *= -1.0F;
		}

		BaseVehicle.releaseVector3f(vector3f2);
		for (int int4 = 0; int4 < 4; ++int4) {
			baseVehicle.wheelInfo[int4].steering = floatArray2[int4];
			baseVehicle.wheelInfo[int4].rotation = floatArray3[int4];
			baseVehicle.wheelInfo[int4].skidInfo = floatArray4[int4];
			baseVehicle.wheelInfo[int4].suspensionLength = floatArray5[int4];
		}

		baseVehicle.polyDirty = true;
	}

	public void clientUpdate() {
		int int1;
		if (this.vehiclesWaitUpdatesFrequency.Check()) {
			if (this.vehiclesWaitUpdates.size() > 0) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
				byteBufferWriter.bb.put((byte)11);
				byteBufferWriter.bb.putShort((short)this.vehiclesWaitUpdates.size());
				for (int1 = 0; int1 < this.vehiclesWaitUpdates.size(); ++int1) {
					byteBufferWriter.bb.putShort(this.vehiclesWaitUpdates.get(int1));
				}

				PacketTypes.PacketType.Vehicles.send(GameClient.connection);
			}

			this.vehiclesWaitUpdates.clear();
		}

		ArrayList arrayList = this.getVehicles();
		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
			if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
				float[] floatArray = this.tempFloats;
				if (baseVehicle.interpolation.interpolationDataGetPR(floatArray) && baseVehicle.netPlayerAuthorization != 3 && baseVehicle.netPlayerAuthorization != 1) {
					Bullet.setOwnVehiclePhysics(baseVehicle.VehicleID, floatArray);
					byte byte1 = 0;
					int int2 = byte1 + 1;
					float float1 = floatArray[byte1];
					float float2 = floatArray[int2++];
					float float3 = floatArray[int2++];
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, 0.0);
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
		baseVehicle.setZ(0.0F);
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

	private void clientReceiveUpdateFull(ByteBuffer byteBuffer, short short1, float float1, float float2, float float3) throws IOException {
		byte byte1 = byteBuffer.get();
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, 0.0);
		if (this.IDToVehicle.containsKey(short1)) {
			BaseVehicle baseVehicle = this.IDToVehicle.get(short1);
			this.noise("ERROR: got full update for KNOWN vehicle id=" + short1);
			byteBuffer.get();
			byteBuffer.get();
			this.tempVehicle.parts.clear();
			this.tempVehicle.load(byteBuffer, 186);
			if (baseVehicle.physics != null && (baseVehicle.getDriver() == null || !baseVehicle.getDriver().isLocal())) {
				this.tempTransform.setRotation(this.tempVehicle.savedRot);
				this.tempTransform.origin.set(float1 - WorldSimulation.instance.offsetX, float3, float2 - WorldSimulation.instance.offsetY);
				baseVehicle.setWorldTransform(this.tempTransform);
			}

			baseVehicle.netPlayerFromServerUpdate(byte1, short2);
			this.clientUpdateVehiclePos(baseVehicle, float1, float2, float3, square);
		} else {
			boolean boolean1 = byteBuffer.get() != 0;
			byte byte2 = byteBuffer.get();
			if (!boolean1 || byte2 != IsoObject.getFactoryVehicle().getClassID()) {
				DebugLog.log("Error: clientReceiveUpdateFull: packet broken");
			}

			BaseVehicle baseVehicle2 = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle2.VehicleID = short1;
			baseVehicle2.square = square;
			baseVehicle2.setCurrent(square);
			baseVehicle2.load(byteBuffer, 186);
			if (square != null) {
				baseVehicle2.chunk = baseVehicle2.square.chunk;
				baseVehicle2.chunk.vehicles.add(baseVehicle2);
				baseVehicle2.addToWorld();
			}

			IsoChunk.addFromCheckedVehicles(baseVehicle2);
			baseVehicle2.netPlayerFromServerUpdate(byte1, short2);
			this.registerVehicle(baseVehicle2);
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.isDead() && player.getVehicle() == null) {
					IsoWorld.instance.CurrentCell.putInVehicle(player);
				}
			}

			if (baseVehicle2.trace) {
				this.noise("added vehicle id=" + baseVehicle2.VehicleID + (square == null ? " (delayed)" : ""));
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
		VehicleCache.vehicleUpdate(short1, float1, float2, 0.0F);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, 0.0);
		BaseVehicle baseVehicle = this.IDToVehicle.get(short1);
		if (baseVehicle == null && square == null) {
			if (byteBuffer.limit() > byteBuffer.position() + short3) {
				byteBuffer.position(byteBuffer.position() + short3);
			}
		} else {
			boolean boolean1;
			int int1;
			if (baseVehicle != null && square == null) {
				boolean1 = true;
				for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && player.getVehicle() == baseVehicle) {
						boolean1 = false;
						player.setPosition(float1, float2, 0.0F);
						this.sendRequestGetPosition(short1);
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
				int int3;
				if ((short2 & 1) != 0) {
					DebugLog.Vehicle.debugln("Receive full update for vehicle #" + short1);
					this.clientReceiveUpdateFull(byteBuffer, short1, float1, float2, float3);
					if (baseVehicle == null) {
						baseVehicle = this.IDToVehicle.get(short1);
					}

					if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
						byteBuffer.getLong();
						byte byte1 = 0;
						float[] floatArray = this.tempFloats;
						int2 = byte1 + 1;
						floatArray[byte1] = float1;
						floatArray[int2++] = float2;
						for (floatArray[int2++] = float3; int2 < 10; floatArray[int2++] = byteBuffer.getFloat()) {
						}

						short short4 = byteBuffer.getShort();
						floatArray[int2++] = (float)short4;
						for (int3 = 0; int3 < short4; ++int3) {
							floatArray[int2++] = byteBuffer.getFloat();
							floatArray[int2++] = byteBuffer.getFloat();
							floatArray[int2++] = byteBuffer.getFloat();
							floatArray[int2++] = byteBuffer.getFloat();
						}

						Bullet.setOwnVehiclePhysics(short1, floatArray);
					} else if (byteBuffer.limit() > byteBuffer.position() + 102) {
						byteBuffer.position(byteBuffer.position() + 102);
					}

					int2 = this.vehiclesWaitUpdates.indexOf(short1);
					if (int2 >= 0) {
						this.vehiclesWaitUpdates.removeAt(int2);
					}
				} else if (baseVehicle == null && square != null) {
					this.sendRequestGetFull(short1);
					if (byteBuffer.limit() > byteBuffer.position() + short3) {
						byteBuffer.position(byteBuffer.position() + short3);
					}
				} else {
					byte byte2;
					if ((short2 & 16384) != 0) {
						byte2 = byteBuffer.get();
						short short5 = byteBuffer.getShort();
						if (baseVehicle != null) {
							baseVehicle.netPlayerFromServerUpdate(byte2, short5);
						}
					}

					if ((short2 & 2) != 0) {
						if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
							baseVehicle.interpolation.interpolationDataAdd(byteBuffer, float1, float2, float3);
						} else if (byteBuffer.limit() > byteBuffer.position() + 102) {
							byteBuffer.position(byteBuffer.position() + 102);
						}
					}

					if ((short2 & 4) != 0) {
						this.noise("received update Engine id=" + short1);
						byte2 = byteBuffer.get();
						if (byte2 >= 0 && byte2 < BaseVehicle.engineStateTypes.Values.length) {
							switch (BaseVehicle.engineStateTypes.Values[byte2]) {
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
								baseVehicle.engineDoShuttingDown();
							
							}

							baseVehicle.engineLoudness = byteBuffer.getInt();
							baseVehicle.enginePower = byteBuffer.getInt();
							baseVehicle.engineQuality = byteBuffer.getInt();
						} else {
							DebugLog.log("ERROR: VehicleManager.clientReceiveUpdate get invalid data");
						}
					}

					boolean boolean2;
					if ((short2 & 4096) != 0) {
						this.noise("received car properties update id=" + short1);
						baseVehicle.setHotwired(byteBuffer.get() == 1);
						baseVehicle.setHotwiredBroken(byteBuffer.get() == 1);
						boolean1 = byteBuffer.get() == 1;
						boolean2 = byteBuffer.get() == 1;
						InventoryItem inventoryItem = null;
						if (byteBuffer.get() == 1) {
							try {
								inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}

						baseVehicle.syncKeyInIgnition(boolean1, boolean2, inventoryItem);
						baseVehicle.setRust(byteBuffer.getFloat());
						baseVehicle.setBloodIntensity("Front", byteBuffer.getFloat());
						baseVehicle.setBloodIntensity("Rear", byteBuffer.getFloat());
						baseVehicle.setBloodIntensity("Left", byteBuffer.getFloat());
						baseVehicle.setBloodIntensity("Right", byteBuffer.getFloat());
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
						byte4 = byteBuffer.get();
						byte3 = byteBuffer.get();
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

						if (baseVehicle.lightbarLightsMode.get() != byte4) {
							baseVehicle.setLightbarLightsMode(byte4);
						}

						if (baseVehicle.lightbarSirenMode.get() != byte3) {
							baseVehicle.setLightbarSirenMode(byte3);
						}
					}

					VehiclePart vehiclePart;
					if ((short2 & 2048) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							this.noise("received update PartCondition id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 2048);
							vehiclePart.setCondition(byteBuffer.getInt());
						}

						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 16) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							this.noise("received update PartModData id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.getModData().load((ByteBuffer)byteBuffer, 186);
							if (vehiclePart.isContainer()) {
								vehiclePart.setContainerContentAmount(vehiclePart.getContainerContentAmount());
							}
						}
					}

					float float4;
					VehiclePart vehiclePart2;
					InventoryItem inventoryItem2;
					if ((short2 & 32) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							float4 = byteBuffer.getFloat();
							vehiclePart2 = baseVehicle.getPartByIndex(byte2);
							this.noise("received update PartUsedDelta id=" + short1 + " part=" + vehiclePart2.getId());
							inventoryItem2 = vehiclePart2.getInventoryItem();
							if (inventoryItem2 instanceof DrainableComboItem) {
								((DrainableComboItem)inventoryItem2).setUsedDelta(float4);
							}
						}
					}

					if ((short2 & 128) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							this.noise("received update PartItem id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 128);
							boolean boolean3 = byteBuffer.get() != 0;
							if (boolean3) {
								try {
									inventoryItem2 = InventoryItem.loadItem(byteBuffer, 186);
								} catch (Exception exception2) {
									exception2.printStackTrace();
									return;
								}

								if (inventoryItem2 != null) {
									vehiclePart.setInventoryItem(inventoryItem2);
								}
							} else {
								vehiclePart.setInventoryItem((InventoryItem)null);
							}

							int3 = vehiclePart.getWheelIndex();
							if (int3 != -1) {
								baseVehicle.setTireRemoved(int3, !boolean3);
							}

							if (vehiclePart.isContainer()) {
								LuaEventManager.triggerEvent("OnContainerUpdate");
							}
						}
					}

					if ((short2 & 512) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							this.noise("received update PartDoor id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.getDoor().load(byteBuffer, 186);
						}

						LuaEventManager.triggerEvent("OnContainerUpdate");
						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 256) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							this.noise("received update PartWindow id=" + short1 + " part=" + vehiclePart.getId());
							vehiclePart.getWindow().load(byteBuffer, 186);
						}

						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 64) != 0) {
						this.oldModels.clear();
						this.oldModels.addAll(baseVehicle.models);
						this.curModels.clear();
						byte2 = byteBuffer.get();
						for (int1 = 0; int1 < byte2; ++int1) {
							byte4 = byteBuffer.get();
							byte3 = byteBuffer.get();
							VehiclePart vehiclePart3 = baseVehicle.getPartByIndex(byte4);
							VehicleScript.Model model = (VehicleScript.Model)vehiclePart3.getScriptPart().models.get(byte3);
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
		int int1;
		short short2;
		BaseVehicle baseVehicle;
		BaseVehicle baseVehicle2;
		short short3;
		byte byte2;
		String string;
		String string2;
		BaseVehicle baseVehicle3;
		BaseVehicle baseVehicle4;
		IsoPlayer player;
		IsoGameCharacter gameCharacter;
		switch (byte1) {
		case 1: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			string2 = GameWindow.ReadString(byteBuffer);
			baseVehicle4 = this.IDToVehicle.get(short1);
			if (baseVehicle4 != null) {
				IsoGameCharacter gameCharacter2 = baseVehicle4.getCharacter(byte2);
				if (gameCharacter2 != null) {
					baseVehicle4.setCharacterPosition(gameCharacter2, byte2, string2);
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
			baseVehicle4 = this.IDToVehicle.get(short1);
			if (baseVehicle4 != null) {
				player = (IsoPlayer)GameClient.IDToPlayerMap.get(short2);
				if (player != null) {
					gameCharacter = baseVehicle4.getCharacter(byte2);
					if (gameCharacter == null) {
						baseVehicle4.switchSeatRSync(player, byte2);
					} else if (player != gameCharacter) {
						string = player.getUsername();
						DebugLog.log(string + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					}
				}
			}

			break;
		
		case 5: 
			if (this.tempVehicle == null || this.tempVehicle.getCell() != IsoWorld.instance.CurrentCell) {
				this.tempVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			}

			short1 = byteBuffer.getShort();
			for (int1 = 0; int1 < short1; ++int1) {
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
			baseVehicle4 = this.IDToVehicle.get(short1);
			if (baseVehicle4 != null) {
				player = (IsoPlayer)GameClient.IDToPlayerMap.get(short2);
				if (player != null) {
					gameCharacter = baseVehicle4.getCharacter(byte2);
					if (gameCharacter == null) {
						DebugLog.log(player.getUsername() + " got in vehicle " + baseVehicle4.VehicleID + " seat " + byte2);
						baseVehicle4.enterRSync(byte2, player, baseVehicle4);
					} else if (player != gameCharacter) {
						string = player.getUsername();
						DebugLog.log(string + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					}
				}
			}

			break;
		
		case 7: 
			short1 = byteBuffer.getShort();
			short3 = byteBuffer.getShort();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				IsoPlayer player2 = (IsoPlayer)GameClient.IDToPlayerMap.get(short3);
				if (player2 != null) {
					baseVehicle3.exitRSync(player2);
				}
			}

			break;
		
		case 8: 
			short1 = byteBuffer.getShort();
			if (this.IDToVehicle.containsKey(short1)) {
				BaseVehicle baseVehicle5 = this.IDToVehicle.get(short1);
				if (baseVehicle5.trace) {
					this.noise("server removed vehicle id=" + short1);
				}

				baseVehicle5.serverRemovedFromWorld = true;
				try {
					baseVehicle5.removeFromWorld();
					baseVehicle5.removeFromSquare();
				} finally {
					if (this.IDToVehicle.containsKey(short1)) {
						this.unregisterVehicle(baseVehicle5);
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
			baseVehicle4 = this.IDToVehicle.get(short1);
			if (baseVehicle4 != null) {
				Bullet.applyCentralForceToVehicle(baseVehicle4.VehicleID, vector3f.x, vector3f.y, vector3f.z);
				Vector3f vector3f3 = vector3f2.cross(vector3f);
				Bullet.applyTorqueToVehicle(baseVehicle4.VehicleID, vector3f3.x, vector3f3.y, vector3f3.z);
			}

			break;
		
		case 16: 
			short1 = byteBuffer.getShort();
			byte2 = byteBuffer.get();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				SoundManager.instance.PlayWorldSound("VehicleCrash", baseVehicle3.square, 1.0F, 20.0F, 1.0F, true);
			}

			break;
		
		case 17: 
			short1 = byteBuffer.getShort();
			short3 = byteBuffer.getShort();
			string2 = GameWindow.ReadString(byteBuffer);
			String string3 = GameWindow.ReadString(byteBuffer);
			this.towedVehicleMap.put(short1, short3);
			baseVehicle = this.IDToVehicle.get(short1);
			baseVehicle2 = this.IDToVehicle.get(short3);
			if (baseVehicle != null && baseVehicle2 != null) {
				baseVehicle.addPointConstraint(baseVehicle2, string2, string3, (Float)null, true);
			}

			break;
		
		case 18: 
			boolean boolean1 = byteBuffer.get() == 1;
			short3 = -1;
			short2 = -1;
			if (boolean1) {
				short3 = byteBuffer.getShort();
			}

			boolean boolean2 = byteBuffer.get() == 1;
			if (boolean2) {
				short2 = byteBuffer.getShort();
			}

			if (this.towedVehicleMap.containsKey(short3)) {
				this.towedVehicleMap.remove(short3);
			}

			if (this.towedVehicleMap.containsKey(short2)) {
				this.towedVehicleMap.remove(short2);
			}

			baseVehicle = this.IDToVehicle.get(short3);
			baseVehicle2 = this.IDToVehicle.get(short2);
			if (baseVehicle != null || baseVehicle2 != null) {
				if (baseVehicle != null) {
					baseVehicle.breakConstraint(true, true);
				}

				if (baseVehicle2 != null) {
					baseVehicle2.breakConstraint(true, true);
				}
			}

			break;
		
		case 19: 
			short1 = byteBuffer.getShort();
			for (int1 = 0; int1 < short1; ++int1) {
				short2 = byteBuffer.getShort();
				short short4 = byteBuffer.getShort();
				this.towedVehicleMap.put(short2, short4);
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
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)15);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
			byteBufferWriter.bb.put((byte)(boolean1 ? 1 : 0));
			PacketTypes.PacketType.Vehicles.send(GameClient.connection);
		}
	}

	public void sendEnter(BaseVehicle baseVehicle, int int1, IsoGameCharacter gameCharacter) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)2);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.put((byte)int1);
		byteBufferWriter.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
		PacketTypes.PacketType.Vehicles.send(GameClient.connection);
	}

	public static void sendSound(BaseVehicle baseVehicle, byte byte1) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)16);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.put(byte1);
		PacketTypes.PacketType.Vehicles.send(GameClient.connection);
	}

	public static void sendSoundFromServer(BaseVehicle baseVehicle, byte byte1) {
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)16);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.put(byte1);
			PacketTypes.PacketType.Vehicles.send(udpConnection);
		}
	}

	public void sendPassengerPosition(BaseVehicle baseVehicle, int int1, String string) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)1);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.put((byte)int1);
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.Vehicles.send(GameClient.connection);
	}

	public void sendPassengerPosition(BaseVehicle baseVehicle, int int1, String string, UdpConnection udpConnection) {
		for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
			if (udpConnection2 != udpConnection) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
				byteBufferWriter.bb.put((byte)1);
				byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
				byteBufferWriter.bb.put((byte)int1);
				byteBufferWriter.putUTF(string);
				PacketTypes.PacketType.Vehicles.send(udpConnection2);
			}
		}
	}

	public void sendRequestGetFull(short short1) {
		if (!this.vehiclesWaitUpdates.contains(short1)) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)11);
			byteBufferWriter.bb.putShort((short)1);
			byteBufferWriter.bb.putShort(short1);
			PacketTypes.PacketType.Vehicles.send(GameClient.connection);
			this.vehiclesWaitUpdates.add(short1);
			DebugLog.log(DebugType.Vehicle, "Send get full request for vehicle #" + short1);
		}
	}

	public void sendRequestGetFull(List list) {
		if (list != null) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)11);
			byteBufferWriter.bb.putShort((short)list.size());
			for (int int1 = 0; int1 < list.size(); ++int1) {
				byteBufferWriter.bb.putShort(((VehicleCache)list.get(int1)).id);
				this.vehiclesWaitUpdates.add(((VehicleCache)list.get(int1)).id);
			}

			PacketTypes.PacketType.Vehicles.send(GameClient.connection);
		}
	}

	public void sendRequestGetPosition(short short1) {
		if (this.sendRequestGetPositionFrequency.Check()) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)12);
			byteBufferWriter.bb.putShort(short1);
			PacketTypes.PacketType.Vehicles.send(GameClient.connection);
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
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)13);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.putFloat(vector3f.x);
			byteBufferWriter.bb.putFloat(vector3f.y);
			byteBufferWriter.bb.putFloat(vector3f.z);
			byteBufferWriter.bb.putFloat(vector3f2.x);
			byteBufferWriter.bb.putFloat(vector3f2.y);
			byteBufferWriter.bb.putFloat(vector3f2.z);
			PacketTypes.PacketType.Vehicles.send(udpConnection);
		}
	}

	public void sendREnter(BaseVehicle baseVehicle, int int1, IsoGameCharacter gameCharacter) {
		for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)6);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.put((byte)int1);
			byteBufferWriter.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
			PacketTypes.PacketType.Vehicles.send(udpConnection);
		}
	}

	public void sendSwitchSeat(BaseVehicle baseVehicle, int int1, IsoGameCharacter gameCharacter) {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)4);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.put((byte)int1);
			byteBufferWriter.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
			PacketTypes.PacketType.Vehicles.send(GameClient.connection);
		}

		if (GameServer.bServer) {
			for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
				ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
				PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter2);
				byteBufferWriter2.bb.put((byte)4);
				byteBufferWriter2.bb.putShort(baseVehicle.VehicleID);
				byteBufferWriter2.bb.put((byte)int1);
				byteBufferWriter2.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
				PacketTypes.PacketType.Vehicles.send(udpConnection);
			}
		}
	}

	public void sendExit(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)3);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
		PacketTypes.PacketType.Vehicles.send(GameClient.connection);
	}

	public void sendRExit(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)7);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
			byteBufferWriter.bb.putShort(((IsoPlayer)gameCharacter).OnlineID);
			PacketTypes.PacketType.Vehicles.send(udpConnection);
		}
	}

	public void sendPhysic(BaseVehicle baseVehicle) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType packetType;
		if (this.VehiclePhysicSyncPacketLimit.Check()) {
			packetType = PacketTypes.PacketType.Vehicles;
		} else {
			packetType = PacketTypes.PacketType.VehiclesUnreliable;
		}

		packetType.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)9);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putLong(GameTime.getServerTime());
		byteBufferWriter.bb.putFloat(baseVehicle.physics.EngineForce - baseVehicle.physics.BrakingForce);
		if (WorldSimulation.instance.getOwnVehiclePhysics(baseVehicle.VehicleID, byteBufferWriter) != 1) {
			GameClient.connection.cancelPacket();
		} else {
			packetType.send(GameClient.connection);
		}
	}

	public void sendEngineSound(BaseVehicle baseVehicle, float float1, float float2) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)14);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putFloat(float1);
		byteBufferWriter.bb.putFloat(float2);
		PacketTypes.PacketType.Vehicles.send(GameClient.connection);
	}

	public void sendTowing(UdpConnection udpConnection, BaseVehicle baseVehicle, BaseVehicle baseVehicle2, String string, String string2, Float Float1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)17);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putShort(baseVehicle2.VehicleID);
		GameWindow.WriteString(byteBufferWriter.bb, string);
		GameWindow.WriteString(byteBufferWriter.bb, string2);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendTowing(BaseVehicle baseVehicle, BaseVehicle baseVehicle2, String string, String string2, Float Float1) {
		if (!this.towedVehicleMap.containsKey(baseVehicle.VehicleID)) {
			this.towedVehicleMap.put(baseVehicle.VehicleID, baseVehicle2.VehicleID);
			if (baseVehicle.getDriver() != null && baseVehicle.getVehicleTowing() != null) {
				baseVehicle.getVehicleTowing().setNetPlayerAuthorization((byte)3);
				baseVehicle.getVehicleTowing().netPlayerId = baseVehicle.getDriver().getOnlineID();
				baseVehicle.getVehicleTowing().netPlayerTimeout = 30;
			}

			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				this.sendTowing(udpConnection, baseVehicle, baseVehicle2, string, string2, Float1);
			}
		}
	}

	public void sendDetachTowing(UdpConnection udpConnection, BaseVehicle baseVehicle, BaseVehicle baseVehicle2) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)18);
		if (baseVehicle != null) {
			byteBufferWriter.bb.put((byte)1);
			byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		} else {
			byteBufferWriter.bb.put((byte)0);
		}

		if (baseVehicle2 != null) {
			byteBufferWriter.bb.put((byte)1);
			byteBufferWriter.bb.putShort(baseVehicle2.VehicleID);
		} else {
			byteBufferWriter.bb.put((byte)0);
		}

		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendDetachTowing(BaseVehicle baseVehicle, BaseVehicle baseVehicle2) {
		if (baseVehicle != null) {
			if (this.towedVehicleMap.containsKey(baseVehicle.VehicleID)) {
				this.towedVehicleMap.remove(baseVehicle.VehicleID);
			}

			if (baseVehicle.getDriver() == null) {
				baseVehicle.setNetPlayerAuthorization((byte)0);
				baseVehicle.netPlayerId = -1;
			} else {
				baseVehicle.setNetPlayerAuthorization((byte)3);
				baseVehicle.netPlayerId = baseVehicle.getDriver().getOnlineID();
				baseVehicle.netPlayerTimeout = 30;
			}
		}

		if (baseVehicle2 != null) {
			if (this.towedVehicleMap.containsKey(baseVehicle2.VehicleID)) {
				this.towedVehicleMap.remove(baseVehicle2.VehicleID);
			}

			if (baseVehicle2.getDriver() == null) {
				baseVehicle2.setNetPlayerAuthorization((byte)0);
				baseVehicle2.netPlayerId = -1;
			} else {
				baseVehicle2.setNetPlayerAuthorization((byte)3);
				baseVehicle2.netPlayerId = baseVehicle2.getDriver().getOnlineID();
				baseVehicle2.netPlayerTimeout = 30;
			}
		}

		for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
			this.sendDetachTowing(udpConnection, baseVehicle, baseVehicle2);
		}
	}

	public short getTowedVehicleID(short short1) {
		return this.towedVehicleMap.containsKey(short1) ? this.towedVehicleMap.get(short1) : -1;
	}

	private void writePositionOrientation(ByteBuffer byteBuffer, BaseVehicle baseVehicle) {
		byteBuffer.putLong(WorldSimulation.instance.time);
		Quaternionf quaternionf = baseVehicle.savedRot;
		Transform transform = baseVehicle.getWorldTransform(this.tempTransform);
		transform.getRotation(quaternionf);
		byteBuffer.putFloat(quaternionf.x);
		byteBuffer.putFloat(quaternionf.y);
		byteBuffer.putFloat(quaternionf.z);
		byteBuffer.putFloat(quaternionf.w);
		byteBuffer.putFloat(baseVehicle.netLinearVelocity.x);
		byteBuffer.putFloat(baseVehicle.netLinearVelocity.y);
		byteBuffer.putFloat(baseVehicle.netLinearVelocity.z);
		byteBuffer.putShort((short)baseVehicle.wheelInfo.length);
		for (int int1 = 0; int1 < baseVehicle.wheelInfo.length; ++int1) {
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].steering);
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].rotation);
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].skidInfo);
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].suspensionLength);
		}
	}

	public static final class PosUpdateVars {
		final Transform transform = new Transform();
		final Vector3f vector3f = new Vector3f();
		final Quaternionf quatf = new Quaternionf();
		final float[] wheelSteer = new float[4];
		final float[] wheelRotation = new float[4];
		final float[] wheelSkidInfo = new float[4];
		final float[] wheelSuspensionLength = new float[4];
	}

	public static final class VehiclePacket {
		public static final byte PassengerPosition = 1;
		public static final byte Enter = 2;
		public static final byte Exit = 3;
		public static final byte SwitchSeat = 4;
		public static final byte Update = 5;
		public static final byte REnter = 6;
		public static final byte RExit = 7;
		public static final byte Remove = 8;
		public static final byte Physic = 9;
		public static final byte Config = 10;
		public static final byte RequestGetFull = 11;
		public static final byte RequestGetPosition = 12;
		public static final byte AddImpulse = 13;
		public static final byte EngineSound = 14;
		public static final byte Collide = 15;
		public static final byte Sound = 16;
		public static final byte TowingCar = 17;
		public static final byte DetachTowingCar = 18;
		public static final byte InitialWorldState = 19;
		public static final byte Sound_Crash = 1;
	}
}
