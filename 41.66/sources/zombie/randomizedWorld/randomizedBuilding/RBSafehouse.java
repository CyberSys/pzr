package zombie.randomizedWorld.randomizedBuilding;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemPickerJava;
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


public final class RBSafehouse extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get("SafehouseLoot");
		IsoCell cell = IsoWorld.instance.CurrentCell;
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
							if (object instanceof IsoDoor && ((IsoDoor)object).isBarricadeAllowed()) {
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
								if (((IsoWindow)object).isBarricadeAllowed() && int3 == 0 && square2 != null && square2.getRoom() == null) {
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

							if (object.getContainer() != null && square.getRoom() != null && square.getRoom().getBuilding().getDef() == buildingDef && Rand.Next(100) <= 70 && square.getRoom().getName() != null && itemPickerRoom.Containers.containsKey(object.getContainer().getType())) {
								object.getContainer().clear();
								ItemPickerJava.fillContainerType(itemPickerRoom, object.getContainer(), "", (IsoGameCharacter)null);
								ItemPickerJava.updateOverlaySprite(object);
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
		this.addZombies(buildingDef, 0, (String)null, (Integer)null, (RoomDef)null);
		if (Rand.Next(5) == 0) {
			this.addZombies(buildingDef, 1, "Survivalist", (Integer)null, (RoomDef)null);
		}

		if (Rand.Next(100) <= 60) {
			RandomizedBuildingBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), Rand.Next(3, 7));
		}

		if (Rand.Next(100) <= 60) {
			RandomizedBuildingBase.createRandomDeadBody(this.getLivingRoomOrKitchen(buildingDef), Rand.Next(3, 7));
		}
	}

	public RBSafehouse() {
		this.name = "Safehouse";
		this.setChance(10);
	}
}
