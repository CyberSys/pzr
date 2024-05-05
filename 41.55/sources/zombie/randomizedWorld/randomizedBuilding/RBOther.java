package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBOther extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		buildingDef.setAllExplored(true);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (object.getContainer() != null) {
								object.getContainer().emptyIt();
								object.getContainer().AddItems("Base.ToiletPaper", Rand.Next(10, 30));
								ItemPickerJava.updateOverlaySprite(object);
							}
						}
					}
				}
			}
		}
	}

	public RBOther() {
		this.name = "Other";
		this.setChance(1);
	}
}
