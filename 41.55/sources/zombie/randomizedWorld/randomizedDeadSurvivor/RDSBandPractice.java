package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import java.util.List;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.list.PZArrayUtil;


public final class RDSBandPractice extends RandomizedDeadSurvivorBase {
	private final ArrayList instrumentsList = new ArrayList();

	public RDSBandPractice() {
		this.name = "Band Practice";
		this.setChance(10);
		this.setMaximumDays(60);
		this.instrumentsList.add("GuitarAcoustic");
		this.instrumentsList.add("GuitarElectricBlack");
		this.instrumentsList.add("GuitarElectricBlue");
		this.instrumentsList.add("GuitarElectricRed");
		this.instrumentsList.add("GuitarElectricBassBlue");
		this.instrumentsList.add("GuitarElectricBassBlack");
		this.instrumentsList.add("GuitarElectricBassRed");
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		this.spawnItemsInContainers(buildingDef, "BandPractice", 90);
		RoomDef roomDef = this.getRoom(buildingDef, "garagestorage");
		if (roomDef == null) {
			roomDef = this.getRoom(buildingDef, "shed");
		}

		if (roomDef == null) {
			roomDef = this.getRoom(buildingDef, "garage");
		}

		this.addZombies(buildingDef, Rand.Next(2, 4), "Rocker", 20, roomDef);
		IsoGridSquare square = getRandomSpawnSquare(roomDef);
		if (square != null) {
			square.AddWorldInventoryItem((String)PZArrayUtil.pickRandom((List)this.instrumentsList), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			if (Rand.Next(4) == 0) {
				square.AddWorldInventoryItem((String)PZArrayUtil.pickRandom((List)this.instrumentsList), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (Rand.Next(4) == 0) {
				square.AddWorldInventoryItem((String)PZArrayUtil.pickRandom((List)this.instrumentsList), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
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
			if (!boolean1) {
				for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
					IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
					if (player.getSquare() != null && player.getSquare().getBuilding() != null && player.getSquare().getBuilding().def == buildingDef) {
						return false;
					}
				}
			}

			boolean boolean2 = false;
			for (int int2 = 0; int2 < buildingDef.rooms.size(); ++int2) {
				RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int2);
				if (("garagestorage".equals(roomDef.name) || "shed".equals(roomDef.name) || "garage".equals(roomDef.name)) && roomDef.area >= 9) {
					boolean2 = true;
					break;
				}
			}

			if (!boolean2) {
				this.debugLine = "No shed/garage or is too small";
			}

			return boolean2;
		}
	}
}
