package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class PlayerHitReactionState extends State {
	private static final PlayerHitReactionState _instance = new PlayerHitReactionState();

	public static PlayerHitReactionState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		if (!gameCharacter.getCharacterActions().isEmpty()) {
			((BaseAction)gameCharacter.getCharacterActions().get(0)).forceStop();
		}

		gameCharacter.setIsAiming(false);
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setHitReaction("");
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (gameCharacter.getAttackedBy() != null && gameCharacter.getAttackedBy() instanceof IsoZombie) {
			if (animEvent.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
				gameCharacter.getAttackedBy().setHitForce(0.03F);
				((IsoZombie)gameCharacter.getAttackedBy()).setPlayerAttackPosition((String)null);
				((IsoZombie)gameCharacter.getAttackedBy()).setStaggerBack(true);
			}

			if (animEvent.m_EventName.equalsIgnoreCase("Defend")) {
				gameCharacter.getAttackedBy().setHitReaction("BiteDefended");
				if (GameClient.bClient) {
					GameClient.sendHitCharacter(gameCharacter.getAttackedBy(), gameCharacter, (HandWeapon)null, 0.0F, false, 1.0F, false, false, false);
				}
			}

			if (animEvent.m_EventName.equalsIgnoreCase("DeathSound")) {
				if (gameCharacter.isPlayingDeathSound()) {
					return;
				}

				gameCharacter.setPlayingDeathSound(true);
				String string = "Male";
				if (gameCharacter.isFemale()) {
					string = "Female";
				}

				string = string + "BeingEatenDeath";
				gameCharacter.playSound(string);
			}

			if (animEvent.m_EventName.equalsIgnoreCase("Death")) {
				gameCharacter.setOnFloor(true);
				if (!GameServer.bServer) {
					gameCharacter.Kill(gameCharacter.getAttackedBy());
				}
			}
		} else {
			DebugLog.log("PlayerHitReactionState.animEvent (" + animEvent.m_EventName + ") zombie is null");
		}
	}
}
