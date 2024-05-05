package zombie.ai.permission;

import zombie.characters.IsoGameCharacter;


public class GenericStatePermissions implements IStatePermissions {
	private boolean m_deferredMovement = false;
	private boolean m_playerInput = false;

	public void setDeferredMovementAllowed(boolean boolean1) {
		this.m_deferredMovement = boolean1;
	}

	public boolean isDeferredMovementAllowed(IsoGameCharacter gameCharacter) {
		return this.m_deferredMovement;
	}

	public void setPlayerInputAllowed(boolean boolean1) {
		this.m_playerInput = boolean1;
	}

	public boolean isPlayerInputAllowed(IsoGameCharacter gameCharacter) {
		return this.m_playerInput;
	}
}
