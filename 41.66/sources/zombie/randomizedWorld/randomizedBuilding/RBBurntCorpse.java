package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RBBurntCorpse extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		buildingDef.bAlarmed = false;
		buildingDef.setHasBeenVisited(true);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int1;
		for (int int2 = buildingDef.x - 1; int2 < buildingDef.x2 + 1; ++int2) {
			for (int1 = buildingDef.y - 1; int1 < buildingDef.y2 + 1; ++int1) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int2, int1, int3);
					if (square != null && Rand.Next(100) < 60) {
						square.Burn(false);
					}
				}
			}
		}

		buildingDef.setAllExplored(true);
		buildingDef.bAlarmed = false;
		ArrayList arrayList = this.addZombies(buildingDef, Rand.Next(3, 7), (String)null, (Integer)null, (RoomDef)null);
		if (arrayList != null) {
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				IsoZombie zombie = (IsoZombie)arrayList.get(int1);
				zombie.setSkeleton(true);
				zombie.getHumanVisual().setSkinTextureIndex(0);
				new IsoDeadBody(zombie, false);
			}
		}
	}

	public RBBurntCorpse() {
		this.name = "Burnt with corpses";
		this.setChance(3);
	}
}
