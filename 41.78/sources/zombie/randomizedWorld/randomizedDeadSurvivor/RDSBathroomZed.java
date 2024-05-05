package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;


public final class RDSBathroomZed extends RandomizedDeadSurvivorBase {
	private final ArrayList items = new ArrayList();

	public RDSBathroomZed() {
		this.name = "Bathroom Zed";
		this.setChance(12);
		this.items.add("Base.BathTowel");
		this.items.add("Base.Razor");
		this.items.add("Base.Lipstick");
		this.items.add("Base.Comb");
		this.items.add("Base.Hairspray");
		this.items.add("Base.Toothbrush");
		this.items.add("Base.Cologne");
		this.items.add("Base.Perfume");
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getRoom(buildingDef, "bathroom");
		int int1 = 1;
		if (roomDef.area > 6) {
			int1 = Rand.Next(1, 3);
		}

		this.addZombies(buildingDef, int1, Rand.Next(2) == 0 ? "Bathrobe" : "Naked", (Integer)null, roomDef);
		this.addRandomItemsOnGround(roomDef, this.items, Rand.Next(2, 5));
	}
}
