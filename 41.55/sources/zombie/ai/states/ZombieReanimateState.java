package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class ZombieReanimateState extends State {
	private static final ZombieReanimateState _instance = new ZombieReanimateState();

	public static ZombieReanimateState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.clearVariable("ReanimateAnim");
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.setReanimate(false);
		zombie.clearVariable("ReanimateAnim");
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (animEvent.m_EventName.equalsIgnoreCase("ReanimateAnimFinishing")) {
			zombie.setReanimate(false);
		}
	}
}
