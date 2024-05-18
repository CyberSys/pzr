package zombie.randomizedWorld;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class RandomizedBuildingBase {
	private int minimumDays = 0;
	private int minimumRooms = 0;
	private static ArrayList squareChoices = new ArrayList();

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
	}

	public void init() {
	}

	public boolean isValid(BuildingDef buildingDef) {
		if (GameClient.bClient) {
			return false;
		} else if (buildingDef.isAllExplored()) {
			return false;
		} else {
			if (!GameServer.bServer) {
				if (IsoPlayer.instance.getSquare() != null && IsoPlayer.instance.getSquare().getBuilding() != null && IsoPlayer.instance.getSquare().getBuilding().def == buildingDef) {
					this.customizeStartingHouse(IsoPlayer.instance.getSquare().getBuilding().def);
					return false;
				}
			} else {
				for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
					if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == buildingDef) {
						return false;
					}
				}
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3 = false;
			for (int int2 = 0; int2 < buildingDef.rooms.size(); ++int2) {
				RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int2);
				if ("bedroom".equals(roomDef.name)) {
					boolean1 = true;
				}

				if ("kitchen".equals(roomDef.name)) {
					boolean2 = true;
				}

				if ("bathroom".equals(roomDef.name)) {
					boolean3 = true;
				}
			}

			return boolean1 && boolean3 && boolean2;
		}
	}

	private void customizeStartingHouse(BuildingDef buildingDef) {
		if (!IsoWorld.instance.getGameMode().equals("The First Week") && !IsoWorld.instance.getGameMode().equals("Initial Infection")) {
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.Plank");
			IsoGridSquare square = IsoPlayer.instance.getCurrentSquare();
			for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
				for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
					for (int int3 = 0; int3 < 8; ++int3) {
						IsoGridSquare square2 = square.getCell().getGridSquare(int1, int2, int3);
						if (square2 != null) {
							for (int int4 = 0; int4 < square2.getObjects().size(); ++int4) {
								IsoObject object = (IsoObject)square2.getObjects().get(int4);
								if (object instanceof IsoWindow) {
									if (int3 == 0) {
										IsoGridSquare square3 = square2.getRoom() == null ? square2 : ((IsoWindow)object).getOppositeSquare();
										if (square3 != null && square3.getRoom() == null && Rand.Next(100) <= 20) {
											boolean boolean1 = square3 != square2;
											IsoBarricade barricade = IsoBarricade.AddBarricadeToObject((IsoWindow)object, boolean1);
											if (barricade != null) {
												barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
												if (GameServer.bServer) {
													barricade.transmitCompleteItemToClients();
												}
											}
										}

										((IsoWindow)object).addSheet((IsoGameCharacter)null);
										((IsoWindow)object).HasCurtains().ToggleDoor((IsoGameCharacter)null);
									} else {
										((IsoWindow)object).addSheet((IsoGameCharacter)null);
										((IsoWindow)object).HasCurtains().ToggleDoor((IsoGameCharacter)null);
									}
								}

								if (object instanceof IsoLightSwitch) {
									((IsoLightSwitch)object).setActive(true);
								}
							}
						}
					}
				}
			}
		}
	}

	public int getMinimumDays() {
		return this.minimumDays;
	}

	public void setMinimumDays(int int1) {
		this.minimumDays = int1;
	}

	public int getMinimumRooms() {
		return this.minimumRooms;
	}

	public void setMinimumRooms(int int1) {
		this.minimumRooms = int1;
	}

	public static IsoDeadBody createRandomDeadBody(RoomDef roomDef) {
		squareChoices.clear();
		for (int int1 = 0; int1 < roomDef.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int1);
			for (int int2 = roomRect.getX(); int2 < roomRect.getX2(); ++int2) {
				for (int int3 = roomRect.getY(); int3 < roomRect.getY2(); ++int3) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, roomDef.getZ());
					if (square != null && square.isFree(false)) {
						squareChoices.add(square);
					}
				}
			}
		}

		if (squareChoices.isEmpty()) {
			return null;
		} else {
			IsoGridSquare square2 = (IsoGridSquare)squareChoices.get(Rand.Next(squareChoices.size()));
			return createRandomDeadBody(square2.getX(), square2.getY(), square2.getZ());
		}
	}

	public static IsoDeadBody createRandomDeadBody(int int1, int int2, int int3) {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
		KahluaTable kahluaTable2 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget("all");
		IsoGameCharacter gameCharacter = new IsoGameCharacter(IsoWorld.instance.getCell(), (float)int1, (float)int2, (float)int3);
		gameCharacter.setDescriptor(SurvivorFactory.CreateSurvivor());
		gameCharacter.setFemale(gameCharacter.getDescriptor().isFemale());
		gameCharacter.setDir(IsoDirections.fromIndex(Rand.Next(8)));
		gameCharacter.initSpritePartsEmpty();
		gameCharacter.Dressup(gameCharacter.getDescriptor());
		for (int int4 = 0; int4 < 6; ++int4) {
			gameCharacter.splatBlood(Rand.Next(1, 4), 0.3F);
		}

		IsoDeadBody deadBody = new IsoDeadBody(gameCharacter, true);
		KahluaTable kahluaTable3 = null;
		if (gameCharacter.isFemale()) {
			kahluaTable3 = (KahluaTable)kahluaTable2.rawget("inventoryfemale");
		} else {
			kahluaTable3 = (KahluaTable)kahluaTable2.rawget("inventorymale");
		}

		LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("fillContainerType"), kahluaTable3, deadBody.getContainer(), "", null);
		return deadBody;
	}

	public static void ChunkLoaded(IsoBuilding building) {
		if (!GameClient.bClient && building.def != null && !building.def.seen && building.def.isFullyStreamedIn()) {
			if (GameServer.bServer && GameServer.Players.isEmpty()) {
				return;
			}

			for (int int1 = 0; int1 < building.Rooms.size(); ++int1) {
				if (((IsoRoom)building.Rooms.get(int1)).def.bExplored) {
					return;
				}
			}

			building.def.seen = true;
			if (GameServer.bServer && GameServer.isSpawnBuilding(building.getDef())) {
				return;
			}

			RandomizedBuildingBase randomizedBuildingBase = IsoWorld.instance.getRBBasic();
			try {
				int int2 = 10;
				switch (SandboxOptions.instance.SurvivorHouseChance.getValue()) {
				case 1: 
					return;
				
				case 2: 
					int2 -= 5;
				
				case 3: 
				
				default: 
					break;
				
				case 4: 
					int2 += 5;
					break;
				
				case 5: 
					int2 += 10;
					break;
				
				case 6: 
					int2 += 20;
				
				}

				if (Rand.Next(100) <= int2) {
					randomizedBuildingBase = (RandomizedBuildingBase)IsoWorld.instance.getRandomizedBuildingList().get(Rand.Next(0, IsoWorld.instance.getRandomizedBuildingList().size()));
				}

				if (randomizedBuildingBase.isValid(building.def)) {
					randomizedBuildingBase.randomizeBuilding(building.def);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
}
