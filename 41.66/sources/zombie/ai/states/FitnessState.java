package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;


public final class FitnessState extends State {
	private static final FitnessState _instance = new FitnessState();

	public static FitnessState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.setVariable("FitnessFinished", false);
		gameCharacter.clearVariable("ExerciseStarted");
		gameCharacter.clearVariable("ExerciseEnded");
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
			GameClient.sendEvent((IsoPlayer)gameCharacter, "EventUpdateFitness");
		}

		gameCharacter.setIgnoreMovement(false);
		gameCharacter.clearVariable("FitnessFinished");
		gameCharacter.clearVariable("ExerciseStarted");
		gameCharacter.clearVariable("ExerciseHand");
		gameCharacter.clearVariable("FitnessStruggle");
		gameCharacter.setVariable("ExerciseEnded", true);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}
}
