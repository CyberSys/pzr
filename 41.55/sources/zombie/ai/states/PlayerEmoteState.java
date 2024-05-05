package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class PlayerEmoteState extends State {
	private static final PlayerEmoteState _instance = new PlayerEmoteState();

	public static PlayerEmoteState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setVariable("EmotePlaying", true);
		gameCharacter.resetModelNextFrame();
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoPlayer player = (IsoPlayer)gameCharacter;
		if (player.pressedCancelAction()) {
			gameCharacter.setVariable("EmotePlaying", false);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("EmotePlaying");
		gameCharacter.resetModelNextFrame();
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if ("EmoteFinishing".equalsIgnoreCase(animEvent.m_EventName)) {
			gameCharacter.setVariable("EmotePlaying", false);
		}

		if ("EmoteLooped".equalsIgnoreCase(animEvent.m_EventName)) {
		}
	}

	public boolean isDoingActionThatCanBeCancelled() {
		return true;
	}
}
