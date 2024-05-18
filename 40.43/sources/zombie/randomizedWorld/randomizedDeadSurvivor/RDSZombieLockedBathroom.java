package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.VirtualZombieManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.network.GameServer;


public class RDSZombieLockedBathroom extends RandomizedDeadSurvivorBase {
	private ArrayList weaponsList = new ArrayList();
	private ArrayList ammoList = new ArrayList();

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		IsoDeadBody deadBody = null;
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
			IsoGridSquare square = null;
			if ("bathroom".equals(roomDef.name)) {
				if (IsoWorld.getZombiesEnabled()) {
					IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(roomDef.getX(), roomDef.getY(), roomDef.getZ());
					if (square2 != null && square2.getRoom() != null) {
						IsoRoom room = square2.getRoom();
						square2 = room.getRandomFreeSquare();
						if (square2 != null) {
							VirtualZombieManager.instance.choices.clear();
							VirtualZombieManager.instance.choices.add(square2);
							VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
						}
					}
				}

				int int2;
				int int3;
				for (int2 = roomDef.x - 1; int2 < roomDef.x2 + 1; ++int2) {
					for (int3 = roomDef.y - 1; int3 < roomDef.y2 + 1; ++int3) {
						square = IsoWorld.instance.getCell().getGridSquare(int2, int3, roomDef.getZ());
						if (square != null) {
							IsoDoor door = square.getIsoDoor();
							if (door != null && this.isDoorToRoom(door, roomDef)) {
								if (door.IsOpen()) {
									door.ToggleDoor((IsoGameCharacter)null);
								}

								IsoBarricade barricade = IsoBarricade.AddBarricadeToObject(door, square.getRoom().def == roomDef);
								if (barricade != null) {
									barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
									if (GameServer.bServer) {
										barricade.transmitCompleteItemToClients();
									}
								}

								deadBody = this.addDeadBodyTheOtherSide(door);
								break;
							}
						}
					}

					if (deadBody != null) {
						break;
					}
				}

				int2 = Rand.Next(5, 10);
				if (deadBody != null) {
					deadBody.getContainer().addItem(InventoryItemFactory.CreateItem("Base.Pistol"));
					for (int3 = 0; int3 < int2; ++int3) {
						deadBody.getContainer().addItem(InventoryItemFactory.CreateItem("Base.Bullets9mm"));
					}
				}

				return;
			}
		}
	}

	private boolean isDoorToRoom(IsoDoor door, RoomDef roomDef) {
		if (door != null && roomDef != null) {
			IsoGridSquare square = door.getSquare();
			IsoGridSquare square2 = door.getOppositeSquare();
			if (square != null && square2 != null) {
				return square.getRoomID() == roomDef.ID != (square2.getRoomID() == roomDef.ID);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean checkIsBathroom(IsoGridSquare square) {
		return square.getRoom() != null && "bathroom".equals(square.getRoom().getName());
	}

	private IsoDeadBody addDeadBodyTheOtherSide(IsoDoor door) {
		IsoGridSquare square = null;
		if (door.north) {
			square = IsoWorld.instance.getCell().getGridSquare((double)door.getX(), (double)door.getY(), (double)door.getZ());
			if (this.checkIsBathroom(square)) {
				square = IsoWorld.instance.getCell().getGridSquare((double)door.getX(), (double)(door.getY() - 1.0F), (double)door.getZ());
			}
		} else {
			square = IsoWorld.instance.getCell().getGridSquare((double)door.getX(), (double)door.getY(), (double)door.getZ());
			if (this.checkIsBathroom(square)) {
				square = IsoWorld.instance.getCell().getGridSquare((double)(door.getX() - 1.0F), (double)door.getY(), (double)door.getZ());
			}
		}

		return super.createRandomDeadBody(square.getX(), square.getY(), square.getZ());
	}

	public RDSZombieLockedBathroom() {
		this.weaponsList.add("Base.Pistol");
		this.ammoList.add("Bullets9mm");
	}
}
