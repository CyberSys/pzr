package zombie.ai.permission;

import zombie.characters.IsoGameCharacter;


public interface IStatePermissions {

	boolean isDeferredMovementAllowed(IsoGameCharacter gameCharacter);

	boolean isPlayerInputAllowed(IsoGameCharacter gameCharacter);
}
