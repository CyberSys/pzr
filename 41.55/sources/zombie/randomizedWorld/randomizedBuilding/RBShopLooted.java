package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class RBShopLooted extends RandomizedBuildingBase {
	private final ArrayList buildingList = new ArrayList();

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setAllExplored(true);
		RoomDef roomDef = null;
		int int1;
		for (int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			RoomDef roomDef2 = (RoomDef)buildingDef.rooms.get(int1);
			if (this.buildingList.contains(roomDef2.name)) {
				roomDef = roomDef2;
				break;
			}
		}

		if (roomDef != null) {
			int1 = Rand.Next(3, 8);
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				this.addZombiesOnSquare(1, "Bandit", (Integer)null, roomDef.getFreeSquare());
			}

			this.addZombiesOnSquare(2, "Police", (Integer)null, roomDef.getFreeSquare());
			int2 = Rand.Next(3, 8);
			for (int int3 = 0; int3 < int2; ++int3) {
				IsoGridSquare square = getRandomSquareForCorpse(roomDef);
				createRandomDeadBody(square, (IsoDirections)null, Rand.Next(5, 10), 5, (String)null);
			}
		}
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		this.debugLine = "";
		if (GameClient.bClient) {
			return false;
		} else if (buildingDef.isAllExplored() && !boolean1) {
			return false;
		} else {
			int int1;
			if (!boolean1) {
				for (int1 = 0; int1 < GameServer.Players.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
					if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == buildingDef) {
						return false;
					}
				}
			}

			for (int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
				RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
				if (this.buildingList.contains(roomDef.name)) {
					return true;
				}
			}

			this.debugLine = this.debugLine + "not a shop";
			return false;
		}
	}

	public RBShopLooted() {
		this.name = "Looted Shop";
		this.setChance(5);
		this.buildingList.add("conveniencestore");
		this.buildingList.add("warehouse");
		this.buildingList.add("medclinic");
		this.buildingList.add("grocery");
		this.buildingList.add("zippeestore");
		this.buildingList.add("gigamart");
		this.buildingList.add("fossoil");
		this.buildingList.add("spiffoskitchen");
		this.buildingList.add("pizzawhirled");
		this.buildingList.add("bookstore");
		this.buildingList.add("grocers");
		this.buildingList.add("library");
		this.buildingList.add("toolstore");
		this.buildingList.add("bar");
		this.buildingList.add("pharmacy");
		this.buildingList.add("gunstore");
		this.buildingList.add("mechanic");
		this.buildingList.add("bakery");
		this.buildingList.add("aesthetic");
		this.buildingList.add("clothesstore");
		this.buildingList.add("restaurant");
		this.buildingList.add("poststorage");
		this.buildingList.add("generalstore");
		this.buildingList.add("furniturestore");
		this.buildingList.add("fishingstorage");
		this.buildingList.add("cornerstore");
		this.buildingList.add("housewarestore");
		this.buildingList.add("shoestore");
		this.buildingList.add("sportstore");
		this.buildingList.add("giftstore");
		this.buildingList.add("candystore");
		this.buildingList.add("toystore");
		this.buildingList.add("electronicsstore");
		this.buildingList.add("sewingstore");
		this.buildingList.add("medical");
		this.buildingList.add("medicaloffice");
		this.buildingList.add("jewelrystore");
		this.buildingList.add("musicstore");
		this.buildingList.add("departmentstore");
		this.buildingList.add("gasstore");
		this.buildingList.add("gardenstore");
		this.buildingList.add("farmstorage");
		this.buildingList.add("hunting");
		this.buildingList.add("camping");
		this.buildingList.add("butcher");
		this.buildingList.add("optometrist");
		this.buildingList.add("knoxbutcher");
	}
}
