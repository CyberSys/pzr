package zombie.core.stash;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class StashSystem {
	public static ArrayList allStashes;
	public static ArrayList possibleStashes;
	public static ArrayList buildingsToDo;
	private static final ArrayList possibleTrap = new ArrayList();
	private static ArrayList alreadyReadMap = new ArrayList();

	public static void init() {
		if (possibleStashes == null) {
			initAllStashes();
			buildingsToDo = new ArrayList();
			possibleTrap.add("Base.FlameTrapSensorV1");
			possibleTrap.add("Base.SmokeBombSensorV1");
			possibleTrap.add("Base.NoiseTrapSensorV1");
			possibleTrap.add("Base.NoiseTrapSensorV2");
			possibleTrap.add("Base.AerosolbombSensorV1");
		}
	}

	public static void initAllStashes() {
		allStashes = new ArrayList();
		possibleStashes = new ArrayList();
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("stash");
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTableIterator.getValue();
			Stash stash = new Stash(kahluaTableImpl.rawgetStr("name"));
			stash.load(kahluaTableImpl);
			allStashes.add(stash);
		}
	}

	public static void checkStashItem(InventoryItem inventoryItem) {
		if (!GameClient.bClient && !possibleStashes.isEmpty()) {
			int int1 = 80;
			switch (SandboxOptions.instance.AnnotatedMapChance.getValue()) {
			case 1: 
				return;
			
			case 2: 
				int1 += 15;
				break;
			
			case 3: 
				int1 += 10;
			
			case 4: 
			
			default: 
				break;
			
			case 5: 
				int1 -= 10;
				break;
			
			case 6: 
				int1 -= 20;
			
			}

			if (Rand.Next(100) <= 100 - int1) {
				ArrayList arrayList = new ArrayList();
				for (int int2 = 0; int2 < allStashes.size(); ++int2) {
					Stash stash = (Stash)allStashes.get(int2);
					if (stash.item.equals(inventoryItem.getFullType()) && checkSpecificSpawnProperties(stash, inventoryItem)) {
						boolean boolean1 = false;
						for (int int3 = 0; int3 < possibleStashes.size(); ++int3) {
							StashBuilding stashBuilding = (StashBuilding)possibleStashes.get(int3);
							if (stashBuilding.stashName.equals(stash.name)) {
								boolean1 = true;
								break;
							}
						}

						if (boolean1) {
							arrayList.add(stash);
						}
					}
				}

				if (!arrayList.isEmpty()) {
					Stash stash2 = (Stash)arrayList.get(Rand.Next(0, arrayList.size()));
					doStashItem(stash2, inventoryItem);
				}
			}
		}
	}

	public static void doStashItem(Stash stash, InventoryItem inventoryItem) {
		if (stash.customName != null) {
			inventoryItem.setName(stash.customName);
		}

		if ("Map".equals(stash.type)) {
			if (stash.annotations != null) {
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				KahluaTable kahluaTable2 = LuaManager.platform.newTable();
				KahluaTableIterator kahluaTableIterator = stash.annotations.iterator();
				while (kahluaTableIterator.advance()) {
					KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTableIterator.getValue();
					if (kahluaTableImpl.rawget("symbol") != null) {
						kahluaTable.rawset(kahluaTable.size() + 1, kahluaTableImpl);
					}

					if (kahluaTableImpl.rawget("text") != null) {
						kahluaTableImpl.rawset("text", Translator.getText(kahluaTableImpl.rawgetStr("text")));
						kahluaTable2.rawset(kahluaTable2.size() + 1, kahluaTableImpl);
					}
				}

				inventoryItem.getModData().rawset("symbols", kahluaTable);
				inventoryItem.getModData().rawset("notes", kahluaTable2);
			}

			removeFromPossibleStash(stash);
			inventoryItem.setStashMap(stash.name);
		}
	}

	public static void prepareBuildingStash(String string) {
		if (string != null) {
			Stash stash = getStash(string);
			if (stash != null && !alreadyReadMap.contains(string)) {
				alreadyReadMap.add(string);
				buildingsToDo.add(new StashBuilding(stash.name, stash.buildingX, stash.buildingY));
			}
		}
	}

	private static boolean checkSpecificSpawnProperties(Stash stash, InventoryItem inventoryItem) {
		if (!stash.spawnOnlyOnZed || inventoryItem.getContainer() != null && inventoryItem.getContainer().getParent() instanceof IsoDeadBody) {
			return (stash.minDayToSpawn <= -1 || GameTime.instance.getDaysSurvived() >= stash.minDayToSpawn) && (stash.maxDayToSpawn <= -1 || GameTime.instance.getDaysSurvived() <= stash.maxDayToSpawn);
		} else {
			return false;
		}
	}

	private static void removeFromPossibleStash(Stash stash) {
		for (int int1 = 0; int1 < possibleStashes.size(); ++int1) {
			StashBuilding stashBuilding = (StashBuilding)possibleStashes.get(int1);
			if (stashBuilding.buildingX == stash.buildingX && stashBuilding.buildingY == stash.buildingY) {
				possibleStashes.remove(int1);
				--int1;
			}
		}
	}

	public static void doBuildingStash(BuildingDef buildingDef) {
		if (buildingsToDo == null) {
			init();
		}

		for (int int1 = 0; int1 < buildingsToDo.size(); ++int1) {
			StashBuilding stashBuilding = (StashBuilding)buildingsToDo.get(int1);
			if (stashBuilding.buildingX > buildingDef.x && stashBuilding.buildingX < buildingDef.x2 && stashBuilding.buildingY > buildingDef.y && stashBuilding.buildingY < buildingDef.y2) {
				if (buildingDef.hasBeenVisited) {
					buildingsToDo.remove(int1);
					--int1;
				} else {
					Stash stash = getStash(stashBuilding.stashName);
					if (stash != null) {
						KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
						KahluaTable kahluaTable2 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget(stash.spawnTable);
						buildingDef.setAllExplored(true);
						doSpecificBuildingProperties(stash, buildingDef);
						for (int int2 = buildingDef.x - 1; int2 < buildingDef.x2 + 1; ++int2) {
							for (int int3 = buildingDef.y - 1; int3 < buildingDef.y2 + 1; ++int3) {
								for (int int4 = 0; int4 < 8; ++int4) {
									IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
									if (square != null) {
										for (int int5 = 0; int5 < square.getObjects().size(); ++int5) {
											IsoObject object = (IsoObject)square.getObjects().get(int5);
											if (object.getContainer() != null && square.getRoom() != null && square.getRoom().getBuilding().getDef() == buildingDef && square.getRoom().getName() != null && kahluaTable2.rawget(object.getContainer().getType()) != null) {
												KahluaTable kahluaTable3 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget(square.getRoom().getName());
												boolean boolean1 = false;
												if (kahluaTable3 == null || kahluaTable3.rawget(object.getContainer().getType()) == null) {
													object.getContainer().clear();
													boolean1 = true;
												}

												LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("fillContainerType"), kahluaTable2, object.getContainer(), "", null);
												if (boolean1) {
													object.getContainer().setExplored(true);
												}
											}

											if (stash.barricades > -1 && (object instanceof IsoDoor || object instanceof IsoWindow) && Rand.Next(100) < stash.barricades) {
												if (object instanceof IsoDoor) {
													((IsoDoor)object).addRandomBarricades();
												} else {
													((IsoWindow)object).addRandomBarricades();
												}
											}
										}
									}
								}
							}
						}

						buildingsToDo.remove(int1);
						--int1;
					}
				}
			}
		}
	}

	private static void doSpecificBuildingProperties(Stash stash, BuildingDef buildingDef) {
		int int1;
		if (stash.containers != null) {
			ArrayList arrayList = new ArrayList();
			for (int int2 = 0; int2 < stash.containers.size(); ++int2) {
				StashContainer stashContainer = (StashContainer)stash.containers.get(int2);
				IsoGridSquare square = null;
				if (!"all".equals(stashContainer.room)) {
					for (int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
						RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
						if (stashContainer.room.equals(roomDef.name)) {
							arrayList.add(roomDef);
						}
					}
				} else if (stashContainer.contX > -1 && stashContainer.contY > -1 && stashContainer.contZ > -1) {
					square = IsoWorld.instance.getCell().getGridSquare(stashContainer.contX, stashContainer.contY, stashContainer.contZ);
				} else {
					square = buildingDef.getFreeSquareInRoom();
				}

				if (!arrayList.isEmpty()) {
					RoomDef roomDef2 = (RoomDef)arrayList.get(Rand.Next(0, arrayList.size()));
					square = roomDef2.getFreeSquare();
				}

				if (square != null) {
					if (stashContainer.containerItem != null && !stashContainer.containerItem.isEmpty()) {
						KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
						KahluaTable kahluaTable2 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget(stash.spawnTable);
						if (kahluaTable2 == null) {
							DebugLog.log("Container distribution " + stash.spawnTable + " not found");
							return;
						}

						InventoryItem inventoryItem = InventoryItemFactory.CreateItem(stashContainer.containerItem);
						if (inventoryItem == null) {
							DebugLog.log("Item " + stashContainer.containerItem + " Doesn\'t exist.");
							return;
						}

						KahluaTable kahluaTable3 = (KahluaTable)kahluaTable2.rawget(inventoryItem.getType());
						LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("rollContainerItem"), inventoryItem, null, kahluaTable3);
						square.AddWorldInventoryItem(inventoryItem, 0.0F, 0.0F, 0.0F);
					} else {
						IsoThumpable thumpable = new IsoThumpable(square.getCell(), square, stashContainer.containerSprite, false, (KahluaTable)null);
						thumpable.setIsThumpable(false);
						thumpable.container = new ItemContainer(stashContainer.containerType, square, thumpable, 10, 10);
						square.AddSpecialObject(thumpable);
						square.RecalcAllWithNeighbours(true);
					}
				} else {
					DebugLog.log("No free room was found to spawn special container for stash " + stash.name);
				}
			}
		}

		int int3;
		if (stash.minTrapToSpawn > -1) {
			for (int3 = stash.minTrapToSpawn; int3 < stash.maxTrapToSpawn; ++int3) {
				IsoGridSquare square2 = buildingDef.getFreeSquareInRoom();
				if (square2 != null) {
					HandWeapon handWeapon = (HandWeapon)InventoryItemFactory.CreateItem((String)possibleTrap.get(Rand.Next(0, possibleTrap.size())));
					if (GameServer.bServer) {
						GameServer.AddExplosiveTrap(handWeapon, square2, handWeapon.getSensorRange() > 0);
					} else {
						IsoTrap trap = new IsoTrap(handWeapon, square2.getCell(), square2);
						square2.AddTileObject(trap);
					}
				}
			}
		}

		if (stash.zombies > -1) {
			for (int3 = 0; int3 < buildingDef.rooms.size(); ++int3) {
				RoomDef roomDef3 = (RoomDef)buildingDef.rooms.get(int3);
				if (IsoWorld.getZombiesEnabled()) {
					byte byte1 = 1;
					int int4 = 0;
					for (int1 = 0; int1 < roomDef3.area; ++int1) {
						if (Rand.Next(100) < stash.zombies) {
							++int4;
						}
					}

					if (SandboxOptions.instance.Zombies.getValue() == 1) {
						int4 += 4;
					} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
						int4 += 2;
					} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
						int4 -= 4;
					}

					if (int4 > roomDef3.area / 2) {
						int4 = roomDef3.area / 2;
					}

					if (int4 < byte1) {
						int4 = byte1;
					}

					VirtualZombieManager.instance.addZombiesToMap(int4, roomDef3, false);
				}
			}
		}
	}

	public static Stash getStash(String string) {
		for (int int1 = 0; int1 < allStashes.size(); ++int1) {
			Stash stash = (Stash)allStashes.get(int1);
			if (stash.name.equals(string)) {
				return stash;
			}
		}

		return null;
	}

	public static void visitedBuilding(BuildingDef buildingDef) {
		if (!GameClient.bClient) {
			for (int int1 = 0; int1 < possibleStashes.size(); ++int1) {
				StashBuilding stashBuilding = (StashBuilding)possibleStashes.get(int1);
				if (stashBuilding.buildingX > buildingDef.x && stashBuilding.buildingX < buildingDef.x2 && stashBuilding.buildingY > buildingDef.y && stashBuilding.buildingY < buildingDef.y2) {
					possibleStashes.remove(int1);
					--int1;
				}
			}
		}
	}

	public static void load(ByteBuffer byteBuffer, int int1) {
		init();
		alreadyReadMap = new ArrayList();
		possibleStashes = new ArrayList();
		buildingsToDo = new ArrayList();
		int int2 = byteBuffer.getInt();
		int int3;
		for (int3 = 0; int3 < int2; ++int3) {
			possibleStashes.add(new StashBuilding(GameWindow.ReadString(byteBuffer), byteBuffer.getInt(), byteBuffer.getInt()));
		}

		int3 = byteBuffer.getInt();
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			buildingsToDo.add(new StashBuilding(GameWindow.ReadString(byteBuffer), byteBuffer.getInt(), byteBuffer.getInt()));
		}

		if (int1 >= 109) {
			int4 = byteBuffer.getInt();
			for (int int5 = 0; int5 < int4; ++int5) {
				alreadyReadMap.add(GameWindow.ReadString(byteBuffer));
			}
		}
	}

	public static void save(ByteBuffer byteBuffer) {
		if (allStashes != null) {
			byteBuffer.putInt(possibleStashes.size());
			int int1;
			StashBuilding stashBuilding;
			for (int1 = 0; int1 < possibleStashes.size(); ++int1) {
				stashBuilding = (StashBuilding)possibleStashes.get(int1);
				GameWindow.WriteString(byteBuffer, stashBuilding.stashName);
				byteBuffer.putInt(stashBuilding.buildingX);
				byteBuffer.putInt(stashBuilding.buildingY);
			}

			byteBuffer.putInt(buildingsToDo.size());
			for (int1 = 0; int1 < buildingsToDo.size(); ++int1) {
				stashBuilding = (StashBuilding)buildingsToDo.get(int1);
				GameWindow.WriteString(byteBuffer, stashBuilding.stashName);
				byteBuffer.putInt(stashBuilding.buildingX);
				byteBuffer.putInt(stashBuilding.buildingY);
			}

			byteBuffer.putInt(alreadyReadMap.size());
			for (int1 = 0; int1 < alreadyReadMap.size(); ++int1) {
				GameWindow.WriteString(byteBuffer, (String)alreadyReadMap.get(int1));
			}
		}
	}

	public static ArrayList getPossibleStashes() {
		return possibleStashes;
	}

	public static void reinit() {
		possibleStashes = null;
		init();
	}

	public static void Reset() {
		allStashes = null;
		possibleStashes = null;
		buildingsToDo = null;
		possibleTrap.clear();
		alreadyReadMap.clear();
	}
}
