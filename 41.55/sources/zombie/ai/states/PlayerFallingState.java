package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;


public final class PlayerFallingState extends State {
	private static final PlayerFallingState _instance = new PlayerFallingState();

	public static PlayerFallingState instance() {
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
}
