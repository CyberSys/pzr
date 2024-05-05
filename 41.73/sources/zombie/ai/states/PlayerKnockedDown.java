package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class PlayerKnockedDown extends State {
	private static final PlayerKnockedDown _instance = new PlayerKnockedDown();

	public static PlayerKnockedDown instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		((IsoPlayer)gameCharacter).setBlockMovement(true);
		gameCharacter.setHitReaction("");
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.isDead()) {
			if (!GameServer.bServer && !GameClient.bClient) {
				gameCharacter.Kill((IsoGameCharacter)null);
			}
		} else {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("FallOnBack")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("setSitOnGround")) {
			gameCharacter.setSitOnGround(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		((IsoPlayer)gameCharacter).setBlockMovement(false);
		((IsoPlayer)gameCharacter).setKnockedDown(false);
		gameCharacter.setOnFloor(true);
	}
}
