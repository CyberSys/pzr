package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;


public final class ZombieFaceTargetState extends State {
	private static final ZombieFaceTargetState _instance = new ZombieFaceTargetState();

	public static ZombieFaceTargetState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (zombie.getTarget() != null) {
			zombie.faceThisObject(zombie.getTarget());
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}
}
