package zombie.ai.states;

import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public final class StaggerBackState extends State {
	private static final StaggerBackState _instance = new StaggerBackState();

	public static StaggerBackState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setStateEventDelayTimer(this.getMaxStaggerTime(gameCharacter));
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.hasAnimationPlayer()) {
			gameCharacter.getAnimationPlayer().setTargetToAngle();
		}

		gameCharacter.getVectorFromDirection(gameCharacter.getForwardDirection());
	}

	public void exit(IsoGameCharacter gameCharacter) {
		if (gameCharacter.isZombie()) {
			((IsoZombie)gameCharacter).setStaggerBack(false);
		}

		gameCharacter.setShootable(true);
	}

	private float getMaxStaggerTime(IsoGameCharacter gameCharacter) {
		float float1 = 35.0F * gameCharacter.getHitForce() * gameCharacter.getStaggerTimeMod();
		if (float1 < 20.0F) {
			return 20.0F;
		} else {
			return float1 > 30.0F ? 30.0F : float1;
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
			IsoZombie zombie = (IsoZombie)gameCharacter;
			zombie.parameterZombieState.setState(ParameterZombieState.State.Pushed);
		}
	}
}
