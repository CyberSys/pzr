package zombie.randomizedWorld;

import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;


public class RBSafehouse extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
		KahluaTable kahluaTable2 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("SuburbsDistributions")).rawget("SafehouseLoot");
		IsoCell cell = IsoWorld.instance.CurrentCell;
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.Plank");
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							IsoGridSquare square2;
							boolean boolean1;
							IsoBarricade barricade;
							int int5;
							int int6;
							if (object instanceof IsoDoor) {
								square2 = square.getRoom() == null ? square : ((IsoDoor)object).getOppositeSquare();
								if (square2 != null && square2.getRoom() == null) {
									boolean1 = square2 != square;
									barricade = IsoBarricade.AddBarricadeToObject((IsoDoor)object, boolean1);
									if (barricade != null) {
										int5 = Rand.Next(1, 4);
										for (int6 = 0; int6 < int5; ++int6) {
											barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
										}

										if (GameServer.bServer) {
											barricade.transmitCompleteItemToClients();
										}
									}
								}
							}

							if (object instanceof IsoWindow) {
								square2 = square.getRoom() == null ? square : ((IsoWindow)object).getOppositeSquare();
								if (int3 == 0 && square2 != null && square2.getRoom() == null) {
									boolean1 = square2 != square;
									barricade = IsoBarricade.AddBarricadeToObject((IsoWindow)object, boolean1);
									if (barricade != null) {
										int5 = Rand.Next(1, 4);
										for (int6 = 0; int6 < int5; ++int6) {
											barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
										}

										if (GameServer.bServer) {
											barricade.transmitCompleteItemToClients();
										}
									}
								} else {
									((IsoWindow)object).addSheet((IsoGameCharacter)null);
									((IsoWindow)object).HasCurtains().ToggleDoor((IsoGameCharacter)null);
								}
							}

							if (object.getContainer() != null && square.getRoom() != null && square.getRoom().getBuilding().getDef() == buildingDef && Rand.Next(100) <= 70 && square.getRoom().getName() != null && kahluaTable2.rawget(object.getContainer().getType()) != null) {
								object.getContainer().clear();
								LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("fillContainerType"), kahluaTable2, object.getContainer(), "", null);
								object.getContainer().setExplored(true);
							}
						}
					}
				}
			}
		}

		buildingDef.setAllExplored(true);
		buildingDef.bAlarmed = false;
		this.addZombies(buildingDef);
	}

	private void addZombies(BuildingDef buildingDef) {
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
			if (Rand.Next(100) <= 80 && IsoWorld.getZombiesEnabled()) {
				byte byte1 = 2;
				int int2 = roomDef.area;
				if (SandboxOptions.instance.Zombies.getValue() == 1) {
					int2 += 4;
				} else if (SandboxOptions.instance.Zombies.getValue() == 2) {
					int2 += 2;
				} else if (SandboxOptions.instance.Zombies.getValue() == 4) {
					int2 -= 4;
				}

				if (int2 > 8) {
					int2 = 8;
				}

				if (int2 < byte1) {
					int2 = byte1 + 1;
				}

				VirtualZombieManager.instance.addZombiesToMap(Rand.Next(byte1, int2), roomDef, false);
			}

			if (Rand.Next(100) <= 60) {
				RandomizedBuildingBase.createRandomDeadBody(roomDef);
			}
		}
	}
}
