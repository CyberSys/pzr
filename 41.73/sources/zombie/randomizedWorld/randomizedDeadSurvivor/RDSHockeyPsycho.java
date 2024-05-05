package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoDirections;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public final class RDSHockeyPsycho extends RandomizedDeadSurvivorBase {

	public RDSHockeyPsycho() {
		this.name = "Hockey Psycho (friday 13th!)";
		this.setUnique(true);
		this.setChance(1);
	}

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		RoomDef roomDef = this.getLivingRoomOrKitchen(buildingDef);
		ArrayList arrayList = this.addZombies(buildingDef, 1, "HockeyPsycho", 0, roomDef);
		if (arrayList != null && !arrayList.isEmpty()) {
			IsoZombie zombie = (IsoZombie)arrayList.get(0);
			zombie.addBlood(BloodBodyPartType.Head, true, true, true);
			for (int int1 = 0; int1 < 10; ++int1) {
				zombie.addBlood((BloodBodyPartType)null, true, false, true);
				zombie.addDirt((BloodBodyPartType)null, Rand.Next(0, 3), true);
			}
		}

		for (int int2 = 0; int2 < 10; ++int2) {
			IsoDeadBody deadBody = createRandomDeadBody(this.getRandomRoom(buildingDef, 2), Rand.Next(5, 20));
			if (deadBody != null) {
				this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)deadBody.x, (int)deadBody.y, (int)deadBody.z);
				this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)deadBody.x, (int)deadBody.y, (int)deadBody.z);
				this.addTraitOfBlood(IsoDirections.getRandom(), 15, (int)deadBody.x, (int)deadBody.y, (int)deadBody.z);
			}
		}
	}
}
