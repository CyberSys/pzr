package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;


public final class ZombieTurnAlerted extends State {
	private static final ZombieTurnAlerted _instance = new ZombieTurnAlerted();
	public static final Integer PARAM_TARGET_ANGLE = 0;

	public static ZombieTurnAlerted instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		float float1 = (Float)hashMap.get(PARAM_TARGET_ANGLE);
		gameCharacter.getAnimationPlayer().setTargetAngle(float1);
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.pathToSound(gameCharacter.getPathTargetX(), gameCharacter.getPathTargetY(), gameCharacter.getPathTargetZ());
		((IsoZombie)gameCharacter).alerted = false;
	}

	public void setParams(IsoGameCharacter gameCharacter, float float1) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.clear();
		hashMap.put(PARAM_TARGET_ANGLE, float1);
	}
}
