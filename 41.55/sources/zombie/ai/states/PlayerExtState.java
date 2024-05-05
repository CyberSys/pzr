package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class PlayerExtState extends State {
	private static final PlayerExtState _instance = new PlayerExtState();

	public static PlayerExtState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setVariable("ExtPlaying", true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("ExtPlaying");
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if ("ExtFinishing".equalsIgnoreCase(animEvent.m_EventName)) {
			gameCharacter.setVariable("ExtPlaying", false);
		}
	}
}
