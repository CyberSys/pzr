package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.network.GameClient;


public final class PlayerGetUpState extends State {
	private static final PlayerGetUpState _instance = new PlayerGetUpState();

	public static PlayerGetUpState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.getStateMachineParams(this);
		gameCharacter.setIgnoreMovement(true);
		IsoPlayer player = (IsoPlayer)gameCharacter;
		player.setInitiateAttack(false);
		player.attackStarted = false;
		player.setAttackType((String)null);
		player.setBlockMovement(true);
		player.setForceRun(false);
		player.setForceSprint(false);
		gameCharacter.setVariable("getUpQuick", gameCharacter.getVariableBoolean("pressedRunButton"));
		if (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
			gameCharacter.setVariable("getUpQuick", true);
		}

		if (gameCharacter.getVariableBoolean("pressedMovement")) {
			gameCharacter.setVariable("getUpWalk", true);
		}

		if (GameClient.bClient) {
			gameCharacter.setKnockedDown(false);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.clearVariable("getUpWalk");
		if (gameCharacter.getVariableBoolean("sitonground")) {
			gameCharacter.setHideWeaponModel(false);
		}

		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setFallOnFront(false);
		gameCharacter.setOnFloor(false);
		((IsoPlayer)gameCharacter).setBlockMovement(false);
		gameCharacter.setSitOnGround(false);
	}
}
