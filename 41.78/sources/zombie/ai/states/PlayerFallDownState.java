package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class PlayerFallDownState extends State {
	private static final PlayerFallDownState _instance = new PlayerFallDownState();

	public static PlayerFallDownState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.clearVariable("bKnockedDown");
		if (gameCharacter.isDead() && !GameServer.bServer && !GameClient.bClient) {
			gameCharacter.Kill((IsoGameCharacter)null);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setOnFloor(true);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (GameClient.bClient && animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}
	}
}
