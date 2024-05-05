package zombie.vehicles;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TShortShortHashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.physics.Transform;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.packets.VehicleAuthorizationPacket;
import zombie.network.packets.vehicle.Physics;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;


public final class VehicleManager {
	public static VehicleManager instance;
	private final VehicleIDMap IDToVehicle;
	private final ArrayList vehicles;
	private boolean idMapDirty;
	private final Transform tempTransform;
	private final ArrayList sendReliable;
	private final ArrayList sendUnreliable;
	private final TShortArrayList vehiclesWaitUpdates;
	private final TShortShortHashMap towedVehicleMap;
	public static HashMap vehiclePacketTypes = new HashMap();
	public final UdpConnection[] connected;
	private final float[] tempFloats;
	private final float[] engineSound;
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
		this.sendReliable = new ArrayList();
		this.sendUnreliable = new ArrayList();
		this.vehiclesWaitUpdates = new TShortArrayList(128);
		this.towedVehicleMap = new TShortShortHashMap();
		this.connected = new UdpConnection[512];
		this.tempFloats = new float[27];
		this.engineSound = new float[2];
		this.posUpdateVars = new VehicleManager.PosUpdateVars();
		this.vehiclesWaitUpdatesFrequency = new UpdateLimit(1000L);
		this.oldModels = new ArrayList();
		this.curModels = new ArrayList();
		this.sendRequestGetPositionFrequency = new UpdateLimit(500L);
		this.VehiclePhysicSyncPacketLimit = new UpdateLimit(500L);
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
			DebugLog.Vehicle.trace("removeFromWorld vehicle id=%d", baseVehicle.VehicleID);
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
					baseVehicle.interpolation.clear();
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
				DebugLog.Vehicle.trace("vehicles: dropped connection %d", int1);
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
			this.sendVehicles(udpConnection, PacketTypes.PacketType.VehiclesUnreliable.getId());
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

	private void sendVehicles(UdpConnection udpConnection, short short1) {
		if (udpConnection.isFullyConnected()) {
			this.sendReliable.clear();
			this.sendUnreliable.clear();
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
				if (baseVehicle.VehicleID == -1) {
					baseVehicle.VehicleID = this.IDToVehicle.allocateID();
					this.registerVehicle(baseVehicle);
				}

				if (udpConnection.RelevantTo(baseVehicle.x, baseVehicle.y)) {
					if (baseVehicle.connectionState[udpConnection.index] == null) {
						baseVehicle.connectionState[udpConnection.index] = new BaseVehicle.ServerVehicleState();
					}

					BaseVehicle.ServerVehicleState serverVehicleState = baseVehicle.connectionState[udpConnection.index];
					if (serverVehicleState.shouldSend(baseVehicle)) {
						if (!baseVehicle.isReliable && PacketTypes.PacketType.Vehicles.getId() != short1) {
							this.sendUnreliable.add(baseVehicle);
						} else {
							this.sendReliable.add(baseVehicle);
						}
					}
				}
			}

			this.sendVehiclesInternal(udpConnection, this.sendReliable, PacketTypes.PacketType.Vehicles);
			this.sendVehiclesInternal(udpConnection, this.sendUnreliable, PacketTypes.PacketType.VehiclesUnreliable);
		}
	}

	private void sendVehiclesInternal(UdpConnection udpConnection, ArrayList arrayList, PacketTypes.PacketType packetType) {
		if (!arrayList.isEmpty()) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			packetType.doPacket(byteBufferWriter);
			try {
				ByteBuffer byteBuffer = byteBufferWriter.bb;
				byteBuffer.put((byte)5);
				byteBuffer.putShort((short)arrayList.size());
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int1);
					BaseVehicle.ServerVehicleState serverVehicleState = baseVehicle.connectionState[udpConnection.index];
					byteBuffer.putShort(baseVehicle.VehicleID);
					byteBuffer.putShort(serverVehicleState.flags);
					byteBuffer.putFloat(baseVehicle.x);
					byteBuffer.putFloat(baseVehicle.y);
					byteBuffer.putFloat(baseVehicle.jniTransform.origin.y);
					int int2 = byteBuffer.position();
					byteBuffer.putShort((short)0);
					int int3 = byteBuffer.position();
					boolean boolean1 = (serverVehicleState.flags & 1) != 0;
					int int4;
					int int5;
					if (boolean1) {
						serverVehicleState.flags = (short)(serverVehicleState.flags & -2);
						baseVehicle.netPlayerServerSendAuthorisation(byteBuffer);
						serverVehicleState.setAuthorization(baseVehicle);
						int4 = byteBuffer.position();
						byteBuffer.putShort((short)0);
						baseVehicle.save(byteBuffer);
						int5 = byteBuffer.position();
						byteBuffer.position(int4);
						byteBuffer.putShort((short)(int5 - int4));
						byteBuffer.position(int5);
						int int6 = byteBuffer.position();
						int int7 = byteBuffer.position() - int3;
						byteBuffer.position(int2);
						byteBuffer.putShort((short)int7);
						byteBuffer.position(int6);
						this.writePositionOrientation(byteBuffer, baseVehicle);
						serverVehicleState.x = baseVehicle.x;
						serverVehicleState.y = baseVehicle.y;
						serverVehicleState.z = baseVehicle.jniTransform.origin.y;
						serverVehicleState.orient.set((Quaternionfc)baseVehicle.savedRot);
					} else {
						if ((serverVehicleState.flags & 2) != 0) {
							this.writePositionOrientation(byteBuffer, baseVehicle);
							serverVehicleState.x = baseVehicle.x;
							serverVehicleState.y = baseVehicle.y;
							serverVehicleState.z = baseVehicle.jniTransform.origin.y;
							serverVehicleState.orient.set((Quaternionfc)baseVehicle.savedRot);
						}

						if ((serverVehicleState.flags & 4) != 0) {
							byteBuffer.put((byte)baseVehicle.engineState.ordinal());
							byteBuffer.putInt(baseVehicle.engineLoudness);
							byteBuffer.putInt(baseVehicle.enginePower);
							byteBuffer.putInt(baseVehicle.engineQuality);
						}

						if ((serverVehicleState.flags & 4096) != 0) {
							byteBuffer.put((byte)(baseVehicle.isHotwired() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle.isHotwiredBroken() ? 1 : 0));
							byteBuffer.putFloat(baseVehicle.getRegulatorSpeed());
							byteBuffer.put((byte)(baseVehicle.isKeysInIgnition() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle.isKeyIsOnDoor() ? 1 : 0));
							InventoryItem inventoryItem = baseVehicle.getCurrentKey();
							if (inventoryItem == null) {
								byteBuffer.put((byte)0);
							} else {
								byteBuffer.put((byte)1);
								inventoryItem.saveWithSize(byteBuffer, false);
							}

							byteBuffer.putFloat(baseVehicle.getRust());
							byteBuffer.putFloat(baseVehicle.getBloodIntensity("Front"));
							byteBuffer.putFloat(baseVehicle.getBloodIntensity("Rear"));
							byteBuffer.putFloat(baseVehicle.getBloodIntensity("Left"));
							byteBuffer.putFloat(baseVehicle.getBloodIntensity("Right"));
							byteBuffer.putFloat(baseVehicle.getColorHue());
							byteBuffer.putFloat(baseVehicle.getColorSaturation());
							byteBuffer.putFloat(baseVehicle.getColorValue());
							byteBuffer.putInt(baseVehicle.getSkinIndex());
						}

						if ((serverVehicleState.flags & 8) != 0) {
							byteBuffer.put((byte)(baseVehicle.getHeadlightsOn() ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle.getStoplightsOn() ? 1 : 0));
							for (int4 = 0; int4 < baseVehicle.getLightCount(); ++int4) {
								byteBuffer.put((byte)(baseVehicle.getLightByIndex(int4).getLight().getActive() ? 1 : 0));
							}
						}

						if ((serverVehicleState.flags & 1024) != 0) {
							byteBuffer.put((byte)(baseVehicle.soundHornOn ? 1 : 0));
							byteBuffer.put((byte)(baseVehicle.soundBackMoveOn ? 1 : 0));
							byteBuffer.put((byte)baseVehicle.lightbarLightsMode.get());
							byteBuffer.put((byte)baseVehicle.lightbarSirenMode.get());
						}

						VehiclePart vehiclePart;
						if ((serverVehicleState.flags & 2048) != 0) {
							for (int4 = 0; int4 < baseVehicle.getPartCount(); ++int4) {
								vehiclePart = baseVehicle.getPartByIndex(int4);
								if ((vehiclePart.updateFlags & 2048) != 0) {
									byteBuffer.put((byte)int4);
									byteBuffer.putInt(vehiclePart.getCondition());
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState.flags & 16) != 0) {
							for (int4 = 0; int4 < baseVehicle.getPartCount(); ++int4) {
								vehiclePart = baseVehicle.getPartByIndex(int4);
								if ((vehiclePart.updateFlags & 16) != 0) {
									byteBuffer.put((byte)int4);
									vehiclePart.getModData().save(byteBuffer);
								}
							}

							byteBuffer.put((byte)-1);
						}

						InventoryItem inventoryItem2;
						if ((serverVehicleState.flags & 32) != 0) {
							for (int4 = 0; int4 < baseVehicle.getPartCount(); ++int4) {
								vehiclePart = baseVehicle.getPartByIndex(int4);
								if ((vehiclePart.updateFlags & 32) != 0) {
									inventoryItem2 = vehiclePart.getInventoryItem();
									if (inventoryItem2 instanceof DrainableComboItem) {
										byteBuffer.put((byte)int4);
										byteBuffer.putFloat(((DrainableComboItem)inventoryItem2).getUsedDelta());
									}
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState.flags & 128) != 0) {
							for (int4 = 0; int4 < baseVehicle.getPartCount(); ++int4) {
								vehiclePart = baseVehicle.getPartByIndex(int4);
								if ((vehiclePart.updateFlags & 128) != 0) {
									byteBuffer.put((byte)int4);
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

						if ((serverVehicleState.flags & 512) != 0) {
							for (int4 = 0; int4 < baseVehicle.getPartCount(); ++int4) {
								vehiclePart = baseVehicle.getPartByIndex(int4);
								if ((vehiclePart.updateFlags & 512) != 0) {
									byteBuffer.put((byte)int4);
									vehiclePart.getDoor().save(byteBuffer);
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState.flags & 256) != 0) {
							for (int4 = 0; int4 < baseVehicle.getPartCount(); ++int4) {
								vehiclePart = baseVehicle.getPartByIndex(int4);
								if ((vehiclePart.updateFlags & 256) != 0) {
									byteBuffer.put((byte)int4);
									vehiclePart.getWindow().save(byteBuffer);
								}
							}

							byteBuffer.put((byte)-1);
						}

						if ((serverVehicleState.flags & 64) != 0) {
							byteBuffer.put((byte)baseVehicle.models.size());
							for (int4 = 0; int4 < baseVehicle.models.size(); ++int4) {
								BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)baseVehicle.models.get(int4);
								byteBuffer.put((byte)modelInfo.part.getIndex());
								byteBuffer.put((byte)modelInfo.part.getScriptPart().models.indexOf(modelInfo.scriptModel));
							}
						}

						int4 = byteBuffer.position();
						int5 = byteBuffer.position() - int3;
						byteBuffer.position(int2);
						byteBuffer.putShort((short)int5);
						byteBuffer.position(int4);
					}
				}

				packetType.send(udpConnection);
			} catch (Exception exception2) {
				udpConnection.cancelPacket();
				exception2.printStackTrace();
			}

			for (int int8 = 0; int8 < arrayList.size(); ++int8) {
				BaseVehicle baseVehicle2 = (BaseVehicle)arrayList.get(int8);
				BaseVehicle.ServerVehicleState serverVehicleState2 = baseVehicle2.connectionState[udpConnection.index];
				if ((serverVehicleState2.flags & 16384) != 0) {
					VehicleAuthorizationPacket vehicleAuthorizationPacket = new VehicleAuthorizationPacket();
					vehicleAuthorizationPacket.set(baseVehicle2, udpConnection);
					ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
					PacketTypes.PacketType.VehicleAuthorization.doPacket(byteBufferWriter2);
					vehicleAuthorizationPacket.write(byteBufferWriter2);
					PacketTypes.PacketType.VehicleAuthorization.send(udpConnection);
				}
			}
		}
	}

	public void serverPacket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		short short2;
		byte byte2;
		short short3;
		int int1;
		String string;
		byte byte3;
		BaseVehicle baseVehicle;
		IsoPlayer player;
		IsoPlayer player2;
		DebugLogStream debugLogStream;
		switch (byte1) {
		case 1: 
			short2 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short2);
			byte2 = byteBuffer.get();
			String string2 = GameWindow.ReadString(byteBuffer);
			baseVehicle = this.IDToVehicle.get(short2);
			if (baseVehicle != null) {
				IsoGameCharacter gameCharacter = baseVehicle.getCharacter(byte2);
				if (gameCharacter != null) {
					baseVehicle.setCharacterPosition(gameCharacter, byte2, string2);
					this.sendPassengerPosition(baseVehicle, byte2, string2, udpConnection);
				}
			}

			break;
		
		case 2: 
			short2 = byteBuffer.getShort();
			short3 = byteBuffer.getShort();
			byte3 = byteBuffer.get();
			DebugLog.Vehicle.trace("Vehicle enter vid=%d pid=%d seat=%d", short2, short3, Integer.valueOf(byte3));
			baseVehicle = this.IDToVehicle.get(short2);
			if (baseVehicle == null) {
				DebugLog.Vehicle.warn("Vehicle vid=%d not found", short2);
			} else {
				player = (IsoPlayer)GameServer.IDToPlayerMap.get(short3);
				if (player == null) {
					DebugLog.Vehicle.warn("Player pid=%d not found", short3);
				} else {
					player2 = (IsoPlayer)Type.tryCastTo(baseVehicle.getCharacter(byte3), IsoPlayer.class);
					if (player2 != null && player2 != player) {
						debugLogStream = DebugLog.Vehicle;
						string = player.getUsername();
						debugLogStream.warn(string + " got in same seat as " + player2.getUsername());
					} else {
						baseVehicle.enter(byte3, player);
						if (byte3 == 0 && baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
							baseVehicle.authorizationServerOnSeat(player, true);
						}

						this.sendEnter(baseVehicle, player, byte3);
					}
				}
			}

			break;
		
		case 3: 
			short2 = byteBuffer.getShort();
			short3 = byteBuffer.getShort();
			byte3 = byteBuffer.get();
			DebugLog.Vehicle.trace("Vehicle exit vid=%d pid=%d seat=%d", short2, short3, Integer.valueOf(byte3));
			baseVehicle = this.IDToVehicle.get(short2);
			if (baseVehicle == null) {
				DebugLog.Vehicle.warn("Vehicle vid=%d not found", short2);
			} else {
				player = (IsoPlayer)GameServer.IDToPlayerMap.get(short3);
				if (player == null) {
					DebugLog.Vehicle.warn("Player pid=%d not found", short3);
				} else {
					baseVehicle.exit(player);
					if (byte3 == 0) {
						baseVehicle.authorizationServerOnSeat(player, false);
					}

					this.sendExit(baseVehicle, player, byte3);
				}
			}

			break;
		
		case 4: 
			short2 = byteBuffer.getShort();
			short3 = byteBuffer.getShort();
			byte3 = byteBuffer.get();
			byte byte4 = byteBuffer.get();
			DebugLog.Vehicle.trace("Vehicle switch seat vid=%d pid=%d seats=%d=>%d", short2, short3, Integer.valueOf(byte3), Integer.valueOf(byte4));
			BaseVehicle baseVehicle2 = this.IDToVehicle.get(short2);
			if (baseVehicle2 == null) {
				DebugLog.Vehicle.warn("Vehicle vid=%d not found", short2);
			} else {
				player2 = (IsoPlayer)GameServer.IDToPlayerMap.get(short3);
				if (player2 == null) {
					DebugLog.Vehicle.warn("Player pid=%d not found", short3);
				} else {
					IsoPlayer player3 = (IsoPlayer)Type.tryCastTo(baseVehicle2.getCharacter(byte4), IsoPlayer.class);
					if (player3 != null && player3 != player2) {
						debugLogStream = DebugLog.Vehicle;
						string = player2.getUsername();
						debugLogStream.warn(string + " switched to same seat as " + player3.getUsername());
					} else {
						baseVehicle2.switchSeat(player2, byte4);
						if (byte4 == 0 && baseVehicle2.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
							baseVehicle2.authorizationServerOnSeat(player2, true);
						} else if (byte3 == 0) {
							baseVehicle2.authorizationServerOnSeat(player2, false);
						}

						this.sendSwitchSeat(baseVehicle2, player2, byte3, byte4);
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
		
		case 14: 
		
		default: 
			DebugLog.Vehicle.warn("Unknown vehicle packet %d", byte1);
			break;
		
		case 9: 
			Physics physics = new Physics();
			physics.parse(byteBuffer, udpConnection);
			physics.process();
			for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection != udpConnection2 && physics.isRelevant(udpConnection2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType packetType = (PacketTypes.PacketType)PacketTypes.packetTypes.get(short1);
					packetType.doPacket(byteBufferWriter);
					byteBufferWriter.bb.put((byte)9);
					physics.write(byteBufferWriter);
					packetType.send(udpConnection2);
				}
			}

			return;
		
		case 11: 
			short2 = byteBuffer.getShort();
			for (int1 = 0; int1 < short2; ++int1) {
				short short4 = byteBuffer.getShort();
				DebugLog.Vehicle.trace("Vehicle vid=%d full update response ", short4);
				baseVehicle = this.IDToVehicle.get(short4);
				if (baseVehicle != null) {
					if (baseVehicle.connectionState[udpConnection.index] == null) {
						baseVehicle.connectionState[udpConnection.index] = new BaseVehicle.ServerVehicleState();
					}

					BaseVehicle.ServerVehicleState serverVehicleState = baseVehicle.connectionState[udpConnection.index];
					serverVehicleState.flags = (short)(serverVehicleState.flags | 1);
					this.sendVehicles(udpConnection, short1);
				}
			}

			return;
		
		case 12: 
			short2 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short2);
			BaseVehicle baseVehicle3 = this.IDToVehicle.get(short2);
			if (baseVehicle3 != null) {
				baseVehicle3.updateFlags = (short)(baseVehicle3.updateFlags | 2);
				this.sendVehicles(udpConnection, short1);
			}

			break;
		
		case 15: 
			short2 = byteBuffer.getShort();
			short3 = byteBuffer.getShort();
			boolean boolean1 = byteBuffer.get() == 1;
			DebugLog.Vehicle.trace("%s vid=%d pid=%d %b", vehiclePacketTypes.get(byte1), short2, short3, boolean1);
			baseVehicle = this.IDToVehicle.get(short2);
			if (baseVehicle != null) {
				baseVehicle.authorizationServerCollide(short3, boolean1);
			}

			break;
		
		case 16: 
			short2 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short2);
			byte2 = byteBuffer.get();
			BaseVehicle baseVehicle4 = this.IDToVehicle.get(short2);
			if (baseVehicle4 != null) {
				for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
					UdpConnection udpConnection3 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
					if (udpConnection3 != udpConnection) {
						ByteBufferWriter byteBufferWriter2 = udpConnection3.startPacket();
						PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter2);
						byteBufferWriter2.bb.put((byte)16);
						byteBufferWriter2.bb.putShort(baseVehicle4.VehicleID);
						byteBufferWriter2.bb.put(byte2);
						PacketTypes.PacketType.Vehicles.send(udpConnection3);
					}
				}
			}

		
		}
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
			if (GameClient.bClient) {
				if (baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Local) || baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.LocalCollide)) {
					baseVehicle.interpolation.clear();
					continue;
				}
			} else if (baseVehicle.isKeyboardControlled() || baseVehicle.getJoypad() != -1) {
				baseVehicle.interpolation.clear();
				continue;
			}

			float[] floatArray = this.tempFloats;
			if (baseVehicle.interpolation.interpolationDataGet(floatArray, this.engineSound)) {
				if (!baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Local) && !baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.LocalCollide)) {
					Bullet.setOwnVehiclePhysics(baseVehicle.VehicleID, floatArray);
					float float1 = floatArray[0];
					float float2 = floatArray[1];
					float float3 = floatArray[2];
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, 0.0);
					this.clientUpdateVehiclePos(baseVehicle, float1, float2, float3, square);
					baseVehicle.limitPhysicValid.BlockCheck();
					if (GameClient.bClient) {
						this.vehiclePosUpdate(baseVehicle, floatArray);
					}

					baseVehicle.engineSpeed = (double)this.engineSound[0];
					baseVehicle.throttle = this.engineSound[1];
				}
			} else {
				baseVehicle.getController().control_NoControl();
				baseVehicle.throttle = 0.0F;
				baseVehicle.jniSpeed = 0.0F;
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
		BaseVehicle.Authorization authorization = BaseVehicle.Authorization.valueOf(byteBuffer.get());
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, 0.0);
		if (this.IDToVehicle.containsKey(short1)) {
			BaseVehicle baseVehicle = this.IDToVehicle.get(short1);
			DebugLog.Vehicle.noise("ERROR: got full update for KNOWN vehicle id=%d", short1);
			byteBuffer.get();
			byteBuffer.get();
			this.tempVehicle.parts.clear();
			this.tempVehicle.load(byteBuffer, 194);
			if (baseVehicle.physics != null && (baseVehicle.getDriver() == null || !baseVehicle.getDriver().isLocal())) {
				this.tempTransform.setRotation(this.tempVehicle.savedRot);
				this.tempTransform.origin.set(float1 - WorldSimulation.instance.offsetX, float3, float2 - WorldSimulation.instance.offsetY);
				baseVehicle.setWorldTransform(this.tempTransform);
			}

			baseVehicle.netPlayerFromServerUpdate(authorization, short2);
			this.clientUpdateVehiclePos(baseVehicle, float1, float2, float3, square);
		} else {
			boolean boolean1 = byteBuffer.get() != 0;
			byte byte1 = byteBuffer.get();
			if (!boolean1 || byte1 != IsoObject.getFactoryVehicle().getClassID()) {
				DebugLog.Vehicle.error("clientReceiveUpdateFull: packet broken");
			}

			BaseVehicle baseVehicle2 = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle2.VehicleID = short1;
			baseVehicle2.square = square;
			baseVehicle2.setCurrent(square);
			baseVehicle2.load(byteBuffer, 194);
			if (square != null) {
				baseVehicle2.chunk = baseVehicle2.square.chunk;
				baseVehicle2.chunk.vehicles.add(baseVehicle2);
				baseVehicle2.addToWorld();
			}

			IsoChunk.addFromCheckedVehicles(baseVehicle2);
			baseVehicle2.netPlayerFromServerUpdate(authorization, short2);
			this.registerVehicle(baseVehicle2);
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.isDead() && player.getVehicle() == null) {
					IsoWorld.instance.CurrentCell.putInVehicle(player);
				}
			}

			DebugLog.Vehicle.trace("added vehicle id=%d %s", baseVehicle2.VehicleID, square == null ? " (delayed)" : "");
		}
	}

	private void clientReceiveUpdate(ByteBuffer byteBuffer) throws IOException {
		short short1 = byteBuffer.getShort();
		DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get((byte)5), short1);
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
						this.sendRequestGetPosition(short1, PacketTypes.PacketType.VehiclesUnreliable);
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
					DebugLog.Vehicle.trace("Vehicle vid=%d full update received", short1);
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

						float float4 = byteBuffer.getFloat();
						float float5 = byteBuffer.getFloat();
						short short4 = byteBuffer.getShort();
						floatArray[int2++] = (float)short4;
						for (int int3 = 0; int3 < short4; ++int3) {
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
					this.sendRequestGetFull(short1, PacketTypes.PacketType.Vehicles);
					if (byteBuffer.limit() > byteBuffer.position() + short3) {
						byteBuffer.position(byteBuffer.position() + short3);
					}
				} else {
					if ((short2 & 2) != 0) {
						if (!baseVehicle.isKeyboardControlled() && baseVehicle.getJoypad() == -1) {
							baseVehicle.interpolation.interpolationDataAdd(byteBuffer, byteBuffer.getLong(), float1, float2, float3, GameTime.getServerTimeMills());
						} else if (byteBuffer.limit() > byteBuffer.position() + 102) {
							byteBuffer.position(byteBuffer.position() + 102);
						}
					}

					byte byte2;
					if ((short2 & 4) != 0) {
						DebugLog.Vehicle.trace("received update Engine id=%d", short1);
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
							DebugLog.Vehicle.error("VehicleManager.clientReceiveUpdate get invalid data");
						}
					}

					boolean boolean2;
					if ((short2 & 4096) != 0) {
						DebugLog.Vehicle.trace("received car properties update id=%d", short1);
						baseVehicle.setHotwired(byteBuffer.get() == 1);
						baseVehicle.setHotwiredBroken(byteBuffer.get() == 1);
						baseVehicle.setRegulatorSpeed(byteBuffer.getFloat());
						boolean1 = byteBuffer.get() == 1;
						boolean2 = byteBuffer.get() == 1;
						InventoryItem inventoryItem = null;
						if (byteBuffer.get() == 1) {
							try {
								inventoryItem = InventoryItem.loadItem(byteBuffer, 194);
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
						baseVehicle.setColorHSV(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
						baseVehicle.setSkinIndex(byteBuffer.getInt());
						baseVehicle.updateSkin();
					}

					if ((short2 & 8) != 0) {
						DebugLog.Vehicle.trace("received update Lights id=%d", short1);
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
						DebugLog.Vehicle.trace("received update Sounds id=%d", short1);
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
							DebugLog.Vehicle.trace("received update PartCondition id=%d part=%s", short1, vehiclePart.getId());
							vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 2048);
							vehiclePart.setCondition(byteBuffer.getInt());
						}

						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 16) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							DebugLog.Vehicle.trace("received update PartModData id=%d part=%s", short1, vehiclePart.getId());
							vehiclePart.getModData().load((ByteBuffer)byteBuffer, 194);
							if (vehiclePart.isContainer()) {
								vehiclePart.setContainerContentAmount(vehiclePart.getContainerContentAmount());
							}
						}
					}

					VehiclePart vehiclePart2;
					InventoryItem inventoryItem2;
					if ((short2 & 32) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							float float6 = byteBuffer.getFloat();
							vehiclePart2 = baseVehicle.getPartByIndex(byte2);
							DebugLog.Vehicle.trace("received update PartUsedDelta id=%d part=%s", short1, vehiclePart2.getId());
							inventoryItem2 = vehiclePart2.getInventoryItem();
							if (inventoryItem2 instanceof DrainableComboItem) {
								((DrainableComboItem)inventoryItem2).setUsedDelta(float6);
							}
						}
					}

					if ((short2 & 128) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							DebugLog.Vehicle.trace("received update PartItem id=%d part=%s", short1, vehiclePart.getId());
							vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 128);
							boolean boolean3 = byteBuffer.get() != 0;
							if (boolean3) {
								try {
									inventoryItem2 = InventoryItem.loadItem(byteBuffer, 194);
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

							int int4 = vehiclePart.getWheelIndex();
							if (int4 != -1) {
								baseVehicle.setTireRemoved(int4, !boolean3);
							}

							if (vehiclePart.isContainer()) {
								LuaEventManager.triggerEvent("OnContainerUpdate");
							}
						}
					}

					if ((short2 & 512) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							DebugLog.Vehicle.trace("received update PartDoor id=%d part=%s", short1, vehiclePart.getId());
							vehiclePart.getDoor().load(byteBuffer, 194);
						}

						LuaEventManager.triggerEvent("OnContainerUpdate");
						baseVehicle.doDamageOverlay();
					}

					if ((short2 & 256) != 0) {
						for (byte2 = byteBuffer.get(); byte2 != -1; byte2 = byteBuffer.get()) {
							vehiclePart = baseVehicle.getPartByIndex(byte2);
							DebugLog.Vehicle.trace("received update PartWindow id=%d part=%s", short1, vehiclePart.getId());
							vehiclePart.getWindow().load(byteBuffer, 194);
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
		BaseVehicle baseVehicle;
		short short2;
		DebugLogStream debugLogStream;
		byte byte2;
		BaseVehicle baseVehicle2;
		String string;
		String string2;
		BaseVehicle baseVehicle3;
		byte byte3;
		IsoPlayer player;
		IsoPlayer player2;
		switch (byte1) {
		case 1: 
			short1 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short1);
			byte2 = byteBuffer.get();
			string2 = GameWindow.ReadString(byteBuffer);
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				IsoGameCharacter gameCharacter = baseVehicle3.getCharacter(byte2);
				if (gameCharacter != null) {
					baseVehicle3.setCharacterPosition(gameCharacter, byte2, string2);
				}
			}

			break;
		
		case 2: 
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			byte3 = byteBuffer.get();
			DebugLog.Vehicle.trace("Vehicle enter vid=%d pid=%d seat=%d", short1, short2, Integer.valueOf(byte3));
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 == null) {
				DebugLog.Vehicle.warn("Vehicle vid=%d not found", short1);
			} else {
				player = (IsoPlayer)GameClient.IDToPlayerMap.get(short2);
				if (player == null) {
					DebugLog.Vehicle.warn("Player pid=%d not found", short2);
				} else {
					player2 = (IsoPlayer)Type.tryCastTo(baseVehicle3.getCharacter(byte3), IsoPlayer.class);
					if (player2 != null && player2 != player) {
						debugLogStream = DebugLog.Vehicle;
						string = player.getUsername();
						debugLogStream.warn(string + " got in same seat as " + player2.getUsername());
					} else {
						baseVehicle3.enterRSync(byte3, player, baseVehicle3);
					}
				}
			}

			break;
		
		case 3: 
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			byte3 = byteBuffer.get();
			DebugLog.Vehicle.trace("Vehicle exit vid=%d pid=%d seat=%d", short1, short2, Integer.valueOf(byte3));
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 == null) {
				DebugLog.Vehicle.warn("Vehicle vid=%d not found", short1);
			} else {
				player = (IsoPlayer)GameClient.IDToPlayerMap.get(short2);
				if (player == null) {
					DebugLog.Vehicle.warn("Player pid=%d not found", short2);
				} else {
					baseVehicle3.exitRSync(player);
				}
			}

			break;
		
		case 4: 
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			byte3 = byteBuffer.get();
			byte byte4 = byteBuffer.get();
			DebugLog.Vehicle.trace("Vehicle switch seat vid=%d pid=%d seats=%d=>%d", short1, short2, Integer.valueOf(byte3), Integer.valueOf(byte4));
			baseVehicle = this.IDToVehicle.get(short1);
			if (baseVehicle == null) {
				DebugLog.Vehicle.warn("Vehicle vid=%d not found", short1);
			} else {
				player2 = (IsoPlayer)GameClient.IDToPlayerMap.get(short2);
				if (player2 == null) {
					DebugLog.Vehicle.warn("Player pid=%d not found", short2);
				} else {
					IsoPlayer player3 = (IsoPlayer)Type.tryCastTo(baseVehicle.getCharacter(byte4), IsoPlayer.class);
					if (player3 != null && player3 != player2) {
						debugLogStream = DebugLog.Vehicle;
						string = player2.getUsername();
						debugLogStream.warn(string + " switched to same seat as " + player3.getUsername());
					} else {
						baseVehicle.switchSeat(player2, byte4);
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
		
		case 7: 
		
		case 10: 
		
		case 11: 
		
		case 12: 
		
		case 14: 
		
		case 15: 
		
		default: 
			DebugLog.Vehicle.warn("Unknown vehicle packet %d", byte1);
			break;
		
		case 8: 
			short1 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short1);
			if (this.IDToVehicle.containsKey(short1)) {
				BaseVehicle baseVehicle4 = this.IDToVehicle.get(short1);
				baseVehicle4.serverRemovedFromWorld = true;
				try {
					baseVehicle4.removeFromWorld();
					baseVehicle4.removeFromSquare();
				} finally {
					if (this.IDToVehicle.containsKey(short1)) {
						this.unregisterVehicle(baseVehicle4);
					}
				}
			}

			VehicleCache.remove(short1);
			break;
		
		case 9: 
			Physics physics = new Physics();
			physics.parse(byteBuffer, GameClient.connection);
			physics.process();
			break;
		
		case 13: 
			short1 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short1);
			Vector3f vector3f = new Vector3f();
			Vector3f vector3f2 = new Vector3f();
			vector3f.x = byteBuffer.getFloat();
			vector3f.y = byteBuffer.getFloat();
			vector3f.z = byteBuffer.getFloat();
			vector3f2.x = byteBuffer.getFloat();
			vector3f2.y = byteBuffer.getFloat();
			vector3f2.z = byteBuffer.getFloat();
			baseVehicle3 = this.IDToVehicle.get(short1);
			if (baseVehicle3 != null) {
				Bullet.applyCentralForceToVehicle(baseVehicle3.VehicleID, vector3f.x, vector3f.y, vector3f.z);
				Vector3f vector3f3 = vector3f2.cross(vector3f);
				Bullet.applyTorqueToVehicle(baseVehicle3.VehicleID, vector3f3.x, vector3f3.y, vector3f3.z);
			}

			break;
		
		case 16: 
			short1 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte1), short1);
			byte2 = byteBuffer.get();
			baseVehicle2 = this.IDToVehicle.get(short1);
			if (baseVehicle2 != null) {
				SoundManager.instance.PlayWorldSound("VehicleCrash", baseVehicle2.square, 1.0F, 20.0F, 1.0F, true);
			}

			break;
		
		case 17: 
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			string2 = GameWindow.ReadString(byteBuffer);
			String string3 = GameWindow.ReadString(byteBuffer);
			DebugLog.Vehicle.trace("Vehicle attach A=%d/%s B=%d/%s", short1, string2, short2, string3);
			this.towedVehicleMap.put(short1, short2);
			baseVehicle = this.IDToVehicle.get(short1);
			BaseVehicle baseVehicle5 = this.IDToVehicle.get(short2);
			if (baseVehicle != null && baseVehicle5 != null) {
				baseVehicle.addPointConstraint((IsoPlayer)null, baseVehicle5, string2, string3);
			}

			break;
		
		case 18: 
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			DebugLog.Vehicle.trace("Vehicle detach A=%d B=%d", short1, short2);
			if (this.towedVehicleMap.containsKey(short1)) {
				this.towedVehicleMap.remove(short1);
			}

			if (this.towedVehicleMap.containsKey(short2)) {
				this.towedVehicleMap.remove(short2);
			}

			baseVehicle2 = this.IDToVehicle.get(short1);
			baseVehicle3 = this.IDToVehicle.get(short2);
			if (baseVehicle2 != null) {
				baseVehicle2.breakConstraint(true, true);
			}

			if (baseVehicle3 != null) {
				baseVehicle3.breakConstraint(true, true);
			}

			break;
		
		case 19: 
			short1 = byteBuffer.getShort();
			for (int1 = 0; int1 < short1; ++int1) {
				short short3 = byteBuffer.getShort();
				short short4 = byteBuffer.getShort();
				this.towedVehicleMap.put(short3, short4);
			}

		
		}
	}

	public void sendCollide(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, boolean boolean1) {
		short short1 = gameCharacter == null ? -1 : ((IsoPlayer)gameCharacter).OnlineID;
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)15);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putShort(short1);
		byteBufferWriter.bb.put((byte)(boolean1 ? 1 : 0));
		PacketTypes.PacketType.Vehicles.send(GameClient.connection);
		DebugLog.Vehicle.trace("vid=%d pid=%d collide=%b", baseVehicle.VehicleID, short1, boolean1);
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

	public void sendRequestGetFull(short short1, PacketTypes.PacketType packetType) {
		if (!this.vehiclesWaitUpdates.contains(short1)) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)11);
			byteBufferWriter.bb.putShort((short)1);
			byteBufferWriter.bb.putShort(short1);
			PacketTypes.PacketType.Vehicles.send(GameClient.connection);
			this.vehiclesWaitUpdates.add(short1);
		}
	}

	public void sendRequestGetFull(List list) {
		if (list != null && !list.isEmpty()) {
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

	public void sendRequestGetPosition(short short1, PacketTypes.PacketType packetType) {
		if (this.sendRequestGetPositionFrequency.Check()) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			packetType.doPacket(byteBufferWriter);
			byteBufferWriter.bb.put((byte)12);
			byteBufferWriter.bb.putShort(short1);
			packetType.send(GameClient.connection);
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

	public void sendSwitchSeat(UdpConnection udpConnection, BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1, int int2) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)4);
		byteBufferWriter.bb.putShort(baseVehicle.getId());
		byteBufferWriter.bb.putShort(gameCharacter.getOnlineID());
		byteBufferWriter.bb.put((byte)int1);
		byteBufferWriter.bb.put((byte)int2);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendSwitchSeat(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1, int int2) {
		Iterator iterator = GameServer.udpEngine.connections.iterator();
		while (iterator.hasNext()) {
			UdpConnection udpConnection = (UdpConnection)iterator.next();
			this.sendSwitchSeat(udpConnection, baseVehicle, gameCharacter, int1, int2);
		}
	}

	public void sendEnter(UdpConnection udpConnection, BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)2);
		byteBufferWriter.bb.putShort(baseVehicle.getId());
		byteBufferWriter.bb.putShort(gameCharacter.getOnlineID());
		byteBufferWriter.bb.put((byte)int1);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendEnter(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1) {
		Iterator iterator = GameServer.udpEngine.connections.iterator();
		while (iterator.hasNext()) {
			UdpConnection udpConnection = (UdpConnection)iterator.next();
			this.sendEnter(udpConnection, baseVehicle, gameCharacter, int1);
		}
	}

	public void sendExit(UdpConnection udpConnection, BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)3);
		byteBufferWriter.bb.putShort(baseVehicle.getId());
		byteBufferWriter.bb.putShort(gameCharacter.getOnlineID());
		byteBufferWriter.bb.put((byte)int1);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendExit(BaseVehicle baseVehicle, IsoGameCharacter gameCharacter, int int1) {
		Iterator iterator = GameServer.udpEngine.connections.iterator();
		while (iterator.hasNext()) {
			UdpConnection udpConnection = (UdpConnection)iterator.next();
			this.sendExit(udpConnection, baseVehicle, gameCharacter, (byte)int1);
		}
	}

	public void sendPhysic(BaseVehicle baseVehicle) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.PacketType packetType = baseVehicle.isReliable ? PacketTypes.PacketType.Vehicles : PacketTypes.PacketType.VehiclesUnreliable;
		packetType.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)9);
		Physics physics = new Physics();
		if (physics.set(baseVehicle)) {
			physics.write(byteBufferWriter);
			packetType.send(GameClient.connection);
		} else {
			GameClient.connection.cancelPacket();
		}
	}

	public void sendTowing(UdpConnection udpConnection, BaseVehicle baseVehicle, BaseVehicle baseVehicle2, String string, String string2) {
		DebugLog.Vehicle.trace("vidA=%d vidB=%d", baseVehicle.VehicleID, baseVehicle2.VehicleID);
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)17);
		byteBufferWriter.bb.putShort(baseVehicle.VehicleID);
		byteBufferWriter.bb.putShort(baseVehicle2.VehicleID);
		GameWindow.WriteString(byteBufferWriter.bb, string);
		GameWindow.WriteString(byteBufferWriter.bb, string2);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendTowing(BaseVehicle baseVehicle, BaseVehicle baseVehicle2, String string, String string2) {
		if (!this.towedVehicleMap.containsKey(baseVehicle.VehicleID)) {
			this.towedVehicleMap.put(baseVehicle.VehicleID, baseVehicle2.VehicleID);
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				this.sendTowing(udpConnection, baseVehicle, baseVehicle2, string, string2);
			}
		}
	}

	public void sendDetachTowing(UdpConnection udpConnection, BaseVehicle baseVehicle, BaseVehicle baseVehicle2) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
		byteBufferWriter.bb.put((byte)18);
		byteBufferWriter.bb.putShort(baseVehicle == null ? -1 : baseVehicle.VehicleID);
		byteBufferWriter.bb.putShort(baseVehicle2 == null ? -1 : baseVehicle2.VehicleID);
		PacketTypes.PacketType.Vehicles.send(udpConnection);
	}

	public void sendDetachTowing(BaseVehicle baseVehicle, BaseVehicle baseVehicle2) {
		if (baseVehicle != null && this.towedVehicleMap.containsKey(baseVehicle.VehicleID)) {
			this.towedVehicleMap.remove(baseVehicle.VehicleID);
		}

		if (baseVehicle2 != null && this.towedVehicleMap.containsKey(baseVehicle2.VehicleID)) {
			this.towedVehicleMap.remove(baseVehicle2.VehicleID);
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
		byteBuffer.putFloat(baseVehicle.jniLinearVelocity.x);
		byteBuffer.putFloat(baseVehicle.jniLinearVelocity.y);
		byteBuffer.putFloat(baseVehicle.jniLinearVelocity.z);
		byteBuffer.putFloat((float)baseVehicle.engineSpeed);
		byteBuffer.putFloat(baseVehicle.throttle);
		byteBuffer.putShort((short)baseVehicle.wheelInfo.length);
		for (int int1 = 0; int1 < baseVehicle.wheelInfo.length; ++int1) {
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].steering);
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].rotation);
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].skidInfo);
			byteBuffer.putFloat(baseVehicle.wheelInfo[int1].suspensionLength);
		}
	}

	static  {
		vehiclePacketTypes.put((byte)1, "PassengerPosition");
		vehiclePacketTypes.put((byte)2, "Enter");
		vehiclePacketTypes.put((byte)3, "Exit");
		vehiclePacketTypes.put((byte)4, "SwitchSeat");
		vehiclePacketTypes.put((byte)5, "Update");
		vehiclePacketTypes.put((byte)8, "Remove");
		vehiclePacketTypes.put((byte)9, "Physic");
		vehiclePacketTypes.put((byte)10, "Config");
		vehiclePacketTypes.put((byte)11, "RequestGetFull");
		vehiclePacketTypes.put((byte)12, "RequestGetPosition");
		vehiclePacketTypes.put((byte)13, "AddImpulse");
		vehiclePacketTypes.put((byte)15, "Collide");
		vehiclePacketTypes.put((byte)16, "Sound");
		vehiclePacketTypes.put((byte)17, "TowingCar");
		vehiclePacketTypes.put((byte)18, "DetachTowingCar");
		vehiclePacketTypes.put((byte)19, "InitialWorldState");
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
		public static final byte Remove = 8;
		public static final byte Physic = 9;
		public static final byte Config = 10;
		public static final byte RequestGetFull = 11;
		public static final byte RequestGetPosition = 12;
		public static final byte AddImpulse = 13;
		public static final byte Collide = 15;
		public static final byte Sound = 16;
		public static final byte TowingCar = 17;
		public static final byte DetachTowingCar = 18;
		public static final byte InitialWorldState = 19;
		public static final byte Sound_Crash = 1;
	}
}
