package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;


public final class RDSBedroomZed extends RandomizedDeadSurvivorBase {
	private final ArrayList pantsMaleItems = new ArrayList();
	private final ArrayList pantsFemaleItems = new ArrayList();
	private final ArrayList topItems = new ArrayList();
	private final ArrayList shoesItems = new ArrayList();

	public RDSBedroomZed() {
		this.name = "Bedroom Zed";
		this.setChance(7);
		this.shoesItems.add("Base.Shoes_Random");
		this.shoesItems.add("Base.Shoes_TrainerTINT");
		this.pantsMaleItems.add("Base.TrousersMesh_DenimLight");
		this.pantsMaleItems.add("Base.Trousers_DefaultTEXTURE_TINT");
		this.pantsMaleItems.add("Base.Trousers_Denim");
		this.pantsFemaleItems.add("Base.Skirt_Knees");
		this.pantsFemaleItems.add("Base.Skirt_Long");
		this.pantsFemaleItems.add("Base.Skirt_Short");
		this.pantsFemaleItems.add("Base.Skirt_Normal");
		this.topItems.add("Base.Shirt_FormalWhite");
		this.topItems.add("Base.Shirt_FormalWhite_ShortSleeve");
		this.topItems.add("Base.Tshirt_DefaultTEXTURE_TINT");
		this.topItems.add("Base.Tshirt_PoloTINT");
		this.topItems.add("Base.Tshirt_WhiteLongSleeveTINT");
		this.topItems.add("Base.Tshirt_WhiteTINT");
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getRoom(buildingDef, "bedroom");
		boolean boolean1 = Rand.Next(7) == 0;
		boolean boolean2 = Rand.Next(7) == 0;
		if (boolean1) {
			this.addZombies(buildingDef, 2, "Naked", 0, roomDef);
			this.addItemsOnGround(roomDef, true);
			this.addItemsOnGround(roomDef, true);
		} else if (boolean2) {
			this.addZombies(buildingDef, 2, "Naked", 100, roomDef);
			this.addItemsOnGround(roomDef, false);
			this.addItemsOnGround(roomDef, false);
		} else {
			this.addZombies(buildingDef, 1, "Naked", 0, roomDef);
			this.addItemsOnGround(roomDef, true);
			this.addZombies(buildingDef, 1, "Naked", 100, roomDef);
			this.addItemsOnGround(roomDef, false);
		}
	}

	private void addItemsOnGround(RoomDef roomDef, boolean boolean1) {
		IsoGridSquare square = getRandomSpawnSquare(roomDef);
		this.addRandomItemOnGround(square, this.shoesItems);
		this.addRandomItemOnGround(square, this.topItems);
		this.addRandomItemOnGround(square, boolean1 ? this.pantsMaleItems : this.pantsFemaleItems);
	}
}
