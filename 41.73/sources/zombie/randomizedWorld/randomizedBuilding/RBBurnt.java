package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;


public final class RBBurnt extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && Rand.Next(100) < 90) {
						square.Burn(false);
					}
				}
			}
		}

		buildingDef.setAllExplored(true);
		buildingDef.bAlarmed = false;
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		if (!super.isValid(buildingDef, boolean1)) {
			return false;
		} else {
			return buildingDef.getRooms().size() <= 10;
		}
	}

	public RBBurnt() {
		this.name = "Burnt";
		this.setChance(3);
	}
}
