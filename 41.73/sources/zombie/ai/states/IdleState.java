package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.StringUtils;


public final class IdleState extends State {
	private static final IdleState _instance = new IdleState();

	public static IdleState instance() {
		return _instance;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("PlaySound") && !StringUtils.isNullOrEmpty(animEvent.m_ParameterValue)) {
			gameCharacter.getSquare().playSound(animEvent.m_ParameterValue);
		}
	}
}
