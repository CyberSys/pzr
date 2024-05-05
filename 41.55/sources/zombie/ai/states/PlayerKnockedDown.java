package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.objects.IsoDeadBody;
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
		if (!GameServer.bServer && gameCharacter.isDead()) {
			if (!gameCharacter.isOnDeathDone()) {
				gameCharacter.setOnDeathDone(true);
				gameCharacter.DoDeath((HandWeapon)null, (IsoGameCharacter)null);
			}

			IsoDeadBody deadBody = new IsoDeadBody(gameCharacter);
			if (GameClient.bClient) {
				DebugLog.log("DieState adding " + ((IsoPlayer)gameCharacter).username + " to PlayerToBody");
				GameClient.instance.PlayerToBody.put((IsoPlayer)gameCharacter, deadBody);
			}

			if (gameCharacter.shouldBecomeZombieAfterDeath()) {
				deadBody.reanimateLater();
			}
		} else {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
		}
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
			gameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
			gameCharacter.setOnFloor(gameCharacter.isFallOnFront());
		}

		if (animEvent.m_EventName.equalsIgnoreCase("FallOnBack")) {
			gameCharacter.setOnFloor(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("setSitOnGround")) {
			gameCharacter.setSitOnGround(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		((IsoPlayer)gameCharacter).setBlockMovement(false);
		((IsoPlayer)gameCharacter).setM_bKnockedDown(false);
	}
}
