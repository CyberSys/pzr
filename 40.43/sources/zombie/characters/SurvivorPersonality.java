package zombie.characters;

import zombie.characters.personalities.Cowardly;
import zombie.characters.personalities.FriendlyArmed;
import zombie.characters.personalities.GunNut;


public class SurvivorPersonality {
	public SurvivorPersonality.Personality type;

	public int getZombieFleeAmount() {
		return 10;
	}

	public float getPlayerDistanceComfort() {
		return 5.0F;
	}

	public int getZombieIgnoreOrdersCount() {
		return 4;
	}

	public static SurvivorPersonality CreatePersonality(SurvivorPersonality.Personality personality) {
		Object object = null;
		if (personality == SurvivorPersonality.Personality.GunNut) {
			object = new GunNut();
		}

		if (personality == SurvivorPersonality.Personality.FriendlyArmed) {
			object = new FriendlyArmed();
		}

		if (personality == SurvivorPersonality.Personality.Cowardly) {
			object = new Cowardly();
		}

		if (object != null) {
			((SurvivorPersonality)object).type = personality;
		}

		return (SurvivorPersonality)object;
	}

	public void CreateBehaviours(IsoSurvivor survivor) {
	}

	public int getHuntZombieRange() {
		return 5;
	}
	public static enum Personality {

		GunNut,
		Kate,
		FriendlyArmed,
		Cowardly;
	}
}
