package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class RDSPokerNight extends RandomizedDeadSurvivorBase {
	private final ArrayList items = new ArrayList();
	private String money = null;
	private String card = null;

	public RDSPokerNight() {
		this.name = "Poker Night";
		this.setChance(4);
		this.setMaximumDays(60);
		this.items.add("Base.Cigarettes");
		this.items.add("Base.WhiskeyFull");
		this.items.add("Base.Wine");
		this.items.add("Base.Wine2");
		this.items.add("Base.Crisps");
		this.items.add("Base.Crisps2");
		this.items.add("Base.Crisps3");
		this.items.add("Base.Pop");
		this.items.add("Base.Pop2");
		this.items.add("Base.Pop3");
		this.money = "Base.Money";
		this.card = "Base.CardDeck";
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

			if (this.getRoom(buildingDef, "kitchen") != null) {
				return true;
			} else {
				this.debugLine = "No kitchen";
				return false;
			}
		}
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getRoom(buildingDef, "kitchen");
		this.addZombies(buildingDef, Rand.Next(3, 5), (String)null, 10, roomDef);
		this.addZombies(buildingDef, 1, "PokerDealer", 0, roomDef);
		this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(3, 7));
		this.addRandomItemsOnGround(roomDef, this.money, Rand.Next(8, 13));
		this.addRandomItemsOnGround(roomDef, this.card, 1);
		buildingDef.bAlarmed = false;
	}
}
