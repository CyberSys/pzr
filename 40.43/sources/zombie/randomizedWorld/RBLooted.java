package zombie.randomizedWorld;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;


public class RBLooted extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.Next(100) >= 85 && object instanceof IsoDoor && ((IsoDoor)object).isExteriorDoor((IsoGameCharacter)null)) {
								((IsoDoor)object).destroy();
							}

							if (Rand.Next(100) >= 85 && object instanceof IsoWindow) {
								((IsoWindow)object).smashWindow(false, false);
							}

							if (object.getContainer() != null && object.getContainer().getItems() != null) {
								for (int int5 = 0; int5 < object.getContainer().getItems().size(); ++int5) {
									if (Rand.Next(100) < 80) {
										object.getContainer().getItems().remove(int5);
										--int5;
									}
								}

								object.getContainer().setExplored(true);
							}
						}
					}
				}
			}
		}

		buildingDef.setAllExplored(true);
		buildingDef.bAlarmed = false;
	}
}
