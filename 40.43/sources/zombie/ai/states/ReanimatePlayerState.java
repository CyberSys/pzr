package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;


public class ReanimatePlayerState extends State {
	private static ReanimatePlayerState _instance = new ReanimatePlayerState();

	public static ReanimatePlayerState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.getStateMachine().Lock = true;
		gameCharacter.PlayAnimUnlooped("ZombieGetUp");
		gameCharacter.def.setFrameSpeedPerFrame(0.2F);
		if (gameCharacter instanceof IsoPlayer) {
			((IsoPlayer)gameCharacter).setForceOverrideAnim(true);
		}

		gameCharacter.setOnFloor(false);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getSpriteDef().Finished) {
			gameCharacter.getStateMachine().Lock = false;
			if (gameCharacter instanceof IsoPlayer) {
				((IsoPlayer)gameCharacter).setForceOverrideAnim(false);
			}

			gameCharacter.setDefaultState();
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}
}
