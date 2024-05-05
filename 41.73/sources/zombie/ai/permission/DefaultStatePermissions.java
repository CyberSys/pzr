package zombie.ai.permission;

import zombie.characters.IsoGameCharacter;


public class DefaultStatePermissions implements IStatePermissions {
	public static final DefaultStatePermissions Instance = new DefaultStatePermissions();

	public boolean isDeferredMovementAllowed(IsoGameCharacter gameCharacter) {
		return true;
	}

	public boolean isPlayerInputAllowed(IsoGameCharacter gameCharacter) {
		return true;
	}
}
