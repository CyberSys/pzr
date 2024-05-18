package zombie.characters.personalities;

import zombie.behaviors.survivor.MasterSurvivorBehavior;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorPersonality;


public class Cowardly extends SurvivorPersonality {
	public void CreateBehaviours(IsoSurvivor survivor) {
		survivor.setMasterProper(new MasterSurvivorBehavior(survivor));
		survivor.getMasterBehaviorList().addChild(survivor.getMasterProper());
		survivor.getMasterBehaviorList().addChild(survivor.behaviours);
	}

	public int getHuntZombieRange() {
		return 10;
	}

	public int getZombieFleeAmount() {
		return 1;
	}
}
