package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class ZombieFallingState extends State {
	private static final ZombieFallingState _instance = new ZombieFallingState();

	public static ZombieFallingState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setVariable("bHardFall", false);
		gameCharacter.clearVariable("bLandAnimFinished");
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("bHardFall");
		gameCharacter.clearVariable("bLandAnimFinished");
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}
	}
}
