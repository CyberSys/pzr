package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBPileOCrepe extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.NextBool(3) && this.isTableFor3DItems(object, square)) {
								if (Rand.NextBool(2)) {
									this.addWorldItem("Waffles", square, object);
								} else {
									this.addWorldItem("Pancakes", square, object);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("Fork", square, object);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("ButterKnife", square, object);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && ("pileocrepe".equals(square.getRoom().getName()) || "kitchen_crepe".equals(square.getRoom().getName()));
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("pileocrepe") != null || buildingDef.getRoom("kitchen_crepe") != null || boolean1;
	}

	public RBPileOCrepe() {
		this.name = "PileOCrepe Restaurant";
		this.setAlwaysDo(true);
	}
}
