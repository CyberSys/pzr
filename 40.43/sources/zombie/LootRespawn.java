package zombie;

import java.util.ArrayList;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.BuildingDef;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoThumpable;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;


public class LootRespawn {
	private static int LastRespawnHour = -1;
	private static final ArrayList existingItems = new ArrayList();
	private static final ArrayList newItems = new ArrayList();

	public static void update() {
		if (!GameClient.bClient) {
			int int1 = getRespawnInterval();
			if (int1 > 0) {
				int int2 = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / (double)int1) * int1;
				if (LastRespawnHour < int2) {
					LastRespawnHour = int2;
					int int3;
					int int4;
					int int5;
					IsoChunk chunk;
					if (GameServer.bServer) {
						for (int3 = 0; int3 < ServerMap.instance.LoadedCells.size(); ++int3) {
							ServerMap.ServerCell serverCell = (ServerMap.ServerCell)ServerMap.instance.LoadedCells.get(int3);
							if (serverCell.bLoaded) {
								for (int4 = 0; int4 < 7; ++int4) {
									for (int5 = 0; int5 < 7; ++int5) {
										chunk = serverCell.chunks[int5][int4];
										checkChunk(chunk);
									}
								}
							}
						}
					} else {
						for (int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
							IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int3];
							if (!chunkMap.ignore) {
								for (int4 = 0; int4 < IsoChunkMap.ChunkGridWidth; ++int4) {
									for (int5 = 0; int5 < IsoChunkMap.ChunkGridWidth; ++int5) {
										chunk = chunkMap.getChunk(int5, int4);
										checkChunk(chunk);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void Reset() {
		LastRespawnHour = -1;
	}

	public static void chunkLoaded(IsoChunk chunk) {
		if (!GameClient.bClient) {
			checkChunk(chunk);
		}
	}

	private static void checkChunk(IsoChunk chunk) {
		if (chunk != null) {
			int int1 = getRespawnInterval();
			if (int1 > 0) {
				if (!(GameTime.getInstance().getWorldAgeHours() < (double)int1)) {
					int int2 = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / (double)int1) * int1;
					if (chunk.lootRespawnHour > int2) {
						chunk.lootRespawnHour = int2;
					}

					if (chunk.lootRespawnHour < int2) {
						chunk.lootRespawnHour = int2;
						respawnInChunk(chunk);
					}
				}
			}
		}
	}

	private static int getRespawnInterval() {
		if (GameServer.bServer) {
			return ServerOptions.instance.HoursForLootRespawn.getValue();
		} else {
			if (!GameClient.bClient) {
				int int1 = SandboxOptions.instance.LootRespawn.getValue();
				if (int1 == 1) {
					return 0;
				}

				if (int1 == 2) {
					return 24;
				}

				if (int1 == 3) {
					return 168;
				}

				if (int1 == 4) {
					return 720;
				}

				if (int1 == 5) {
					return 1440;
				}
			}

			return 0;
		}
	}

	private static void respawnInChunk(IsoChunk chunk) {
		boolean boolean1 = GameServer.bServer && ServerOptions.instance.ConstructionPreventsLootRespawn.getValue();
		int int1 = SandboxOptions.instance.SeenHoursPreventLootRespawn.getValue();
		double double1 = GameTime.getInstance().getWorldAgeHours();
		for (int int2 = 0; int2 < 10; ++int2) {
			for (int int3 = 0; int3 < 10; ++int3) {
				IsoGridSquare square = chunk.getGridSquare(int3, int2, 0);
				IsoMetaGrid.Zone zone = square == null ? null : square.getZone();
				if (zone != null && ("TownZone".equals(zone.getType()) || "TownZones".equals(zone.getType()) || "TrailerPark".equals(zone.getType())) && (!boolean1 || !zone.haveConstruction) && (int1 <= 0 || !(zone.getHoursSinceLastSeen() <= (float)int1))) {
					if (square.getBuilding() != null) {
						BuildingDef buildingDef = square.getBuilding().getDef();
						if (buildingDef != null) {
							if ((double)buildingDef.lootRespawnHour > double1) {
								buildingDef.lootRespawnHour = 0;
							}

							if (buildingDef.lootRespawnHour < chunk.lootRespawnHour) {
								buildingDef.setKeySpawned(0);
								buildingDef.lootRespawnHour = chunk.lootRespawnHour;
							}
						}
					}

					for (int int4 = 0; int4 < 8; ++int4) {
						square = chunk.getGridSquare(int3, int2, int4);
						if (square != null) {
							int int5 = square.getObjects().size();
							IsoObject[] objectArray = (IsoObject[])square.getObjects().getElements();
							for (int int6 = 0; int6 < int5; ++int6) {
								IsoObject object = objectArray[int6];
								if (!(object instanceof IsoDeadBody) && !(object instanceof IsoThumpable) && !(object instanceof IsoCompost)) {
									for (int int7 = 0; int7 < object.getContainerCount(); ++int7) {
										ItemContainer itemContainer = object.getContainerByIndex(int7);
										if (itemContainer.bExplored && itemContainer.isHasBeenLooted()) {
											respawnInContainer(object, itemContainer);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static void respawnInContainer(IsoObject object, ItemContainer itemContainer) {
		if (itemContainer != null && itemContainer.getItems() != null) {
			int int1 = itemContainer.getItems().size();
			int int2 = 5;
			if (GameServer.bServer) {
				int2 = ServerOptions.instance.MaxItemsForLootRespawn.getValue();
			}

			if (int1 < int2) {
				existingItems.clear();
				existingItems.addAll(itemContainer.getItems());
				LuaManager.fillContainer(itemContainer, (IsoPlayer)null);
				ArrayList arrayList = itemContainer.getItems();
				if (arrayList != null && int1 != arrayList.size()) {
					itemContainer.setHasBeenLooted(false);
					newItems.clear();
					int int3;
					for (int3 = 0; int3 < arrayList.size(); ++int3) {
						InventoryItem inventoryItem = (InventoryItem)arrayList.get(int3);
						if (!existingItems.contains(inventoryItem)) {
							newItems.add(inventoryItem);
							inventoryItem.setAge(0.0F);
						}
					}

					LuaManager.updateOverlaySprite(object);
					if (GameServer.bServer) {
						for (int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
							UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int3);
							if (udpConnection.ReleventTo((float)object.square.x, (float)object.square.y)) {
								ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
								PacketTypes.doPacket((short)20, byteBufferWriter);
								byteBufferWriter.putShort((short)2);
								byteBufferWriter.putInt((int)object.getX());
								byteBufferWriter.putInt((int)object.getY());
								byteBufferWriter.putInt((int)object.getZ());
								byteBufferWriter.putByte((byte)object.getObjectIndex());
								byteBufferWriter.putByte((byte)object.getContainerIndex(itemContainer));
								try {
									CompressIdenticalItems.save(byteBufferWriter.bb, newItems, (IsoGameCharacter)null);
								} catch (Exception exception) {
									exception.printStackTrace();
								}

								udpConnection.endPacketUnordered();
							}
						}
					}
				}
			}
		}
	}
}
