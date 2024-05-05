package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSSkeletonPsycho extends RandomizedDeadSurvivorBase {

	public RDSSkeletonPsycho() {
		this.name = "Skeleton Psycho";
		this.setChance(1);
		this.setMinimumDays(120);
		this.setUnique(true);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getRoom(buildingDef, "bedroom");
		int int1 = Rand.Next(3, 7);
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoDeadBody deadBody = super.createSkeletonCorpse(roomDef);
			if (deadBody != null) {
				super.addBloodSplat(deadBody.getCurrentSquare(), Rand.Next(7, 12));
			}
		}

		ArrayList arrayList = super.addZombies(buildingDef, 1, "Doctor", (Integer)null, roomDef);
		if (!arrayList.isEmpty()) {
			for (int int3 = 0; int3 < 8; ++int3) {
				((IsoZombie)arrayList.get(0)).addBlood((BloodBodyPartType)null, false, true, false);
			}
		}
	}
}
