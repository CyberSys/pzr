package zombie.ai;

import zombie.characters.IsoGameCharacter;
import zombie.characters.MoveDeltaModifiers;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;


public abstract class State {

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}

	public boolean isAttacking(IsoGameCharacter gameCharacter) {
		return false;
	}

	public boolean isMoving(IsoGameCharacter gameCharacter) {
		return false;
	}

	public boolean isDoingActionThatCanBeCancelled() {
		return false;
	}

	public void getDeltaModifiers(IsoGameCharacter gameCharacter, MoveDeltaModifiers moveDeltaModifiers) {
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}
}
