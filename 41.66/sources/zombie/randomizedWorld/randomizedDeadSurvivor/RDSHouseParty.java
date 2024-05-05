package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class RDSHouseParty extends RandomizedDeadSurvivorBase {
	final ArrayList items = new ArrayList();

	public RDSHouseParty() {
		this.name = "House Party";
		this.setChance(4);
		this.items.add("Base.Crisps");
		this.items.add("Base.Crisps2");
		this.items.add("Base.Crisps3");
		this.items.add("Base.Pop");
		this.items.add("Base.Pop2");
		this.items.add("Base.Pop3");
		this.items.add("Base.Cupcake");
		this.items.add("Base.Cupcake");
		this.items.add("Base.CakeSlice");
		this.items.add("Base.CakeSlice");
		this.items.add("Base.CakeSlice");
		this.items.add("Base.CakeSlice");
		this.items.add("Base.CakeSlice");
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
		this.addZombies(buildingDef, Rand.Next(5, 8), "Party", (Integer)null, roomDef);
		this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(4, 7));
	}
}
