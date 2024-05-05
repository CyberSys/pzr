package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class ZombieFallDownState extends State {
	private static final ZombieFallDownState _instance = new ZombieFallDownState();

	public static ZombieFallDownState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.blockTurning = true;
		gameCharacter.setHitReaction("");
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.blockTurning = false;
		gameCharacter.setOnFloor(true);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("PlayDeathSound")) {
			gameCharacter.setDoDeathSound(false);
			gameCharacter.playDeadSound();
		}
	}
}
