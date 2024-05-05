package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class RDSFootballNight extends RandomizedDeadSurvivorBase {
	private final ArrayList items = new ArrayList();
	private final ArrayList otherItems = new ArrayList();

	public RDSFootballNight() {
		this.name = "Football Night";
		this.setChance(5);
		this.setMaximumDays(60);
		this.otherItems.add("Base.Cigarettes");
		this.otherItems.add("Base.WhiskeyFull");
		this.otherItems.add("Base.Wine");
		this.otherItems.add("Base.Wine2");
		this.items.add("Base.Crisps");
		this.items.add("Base.Crisps2");
		this.items.add("Base.Crisps3");
		this.items.add("Base.Pop");
		this.items.add("Base.Pop2");
		this.items.add("Base.Pop3");
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		this.debugLine = "";
		if (GameClient.bClient) {
			return false;
		} else if (buildingDef.isAllExplored() && !boolean1) {
			return false;
		} else {
			if (!boolean1) {
				for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
					if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == buildingDef) {
						return false;
					}
				}
			}

			if (this.getRoom(buildingDef, "livingroom") != null) {
				return true;
			} else {
				this.debugLine = "No living room";
				return false;
			}
		}
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getRoom(buildingDef, "livingroom");
		this.addZombies(buildingDef, Rand.Next(3, 6), "SportsFan", 10, roomDef);
		this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(3, 7));
		this.addRandomItemsOnGround(roomDef, this.otherItems, Rand.Next(2, 6));
		buildingDef.bAlarmed = false;
	}
}
