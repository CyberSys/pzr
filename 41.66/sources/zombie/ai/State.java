package zombie.ai;

import zombie.ai.permission.DefaultStatePermissions;
import zombie.ai.permission.IStatePermissions;
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

	public boolean isIgnoreCollide(IsoGameCharacter gameCharacter, int int1, int int2, int int3, int int4, int int5, int int6) {
		return false;
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public IStatePermissions getStatePermissions() {
		return DefaultStatePermissions.Instance;
	}
}
