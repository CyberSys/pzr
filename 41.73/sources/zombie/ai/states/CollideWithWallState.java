package zombie.ai.states;

import fmod.fmod.FMODManager;
import zombie.ai.State;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;


public final class CollideWithWallState extends State {
	private static final CollideWithWallState _instance = new CollideWithWallState();

	public static CollideWithWallState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		if (gameCharacter instanceof IsoPlayer) {
			((IsoPlayer)gameCharacter).setIsAiming(false);
		}

		if (gameCharacter.isCollidedN()) {
			gameCharacter.setDir(IsoDirections.N);
		}

		if (gameCharacter.isCollidedS()) {
			gameCharacter.setDir(IsoDirections.S);
		}

		if (gameCharacter.isCollidedE()) {
			gameCharacter.setDir(IsoDirections.E);
		}

		if (gameCharacter.isCollidedW()) {
			gameCharacter.setDir(IsoDirections.W);
		}

		gameCharacter.setCollideType("wall");
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.setLastCollideTime(70.0F);
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setCollideType((String)null);
		gameCharacter.setIgnoreMovement(false);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if ("PlayCollideSound".equalsIgnoreCase(animEvent.m_EventName)) {
			long long1 = gameCharacter.playSound(animEvent.m_ParameterValue);
			ParameterCharacterMovementSpeed parameterCharacterMovementSpeed = ((IsoPlayer)gameCharacter).getParameterCharacterMovementSpeed();
			gameCharacter.getEmitter().setParameterValue(long1, parameterCharacterMovementSpeed.getParameterDescription(), (float)ParameterCharacterMovementSpeed.MovementType.Sprint.label);
			gameCharacter.getEmitter().setParameterValue(long1, FMODManager.instance.getParameterDescription("TripObstacleType"), 7.0F);
		}
	}
}
