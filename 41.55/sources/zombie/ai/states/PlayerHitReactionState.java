package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.network.GameClient;


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

		((IsoPlayer)gameCharacter).setIsAiming(false);
		if (gameCharacter.getAttackingZombie() != null) {
			if (GameClient.bClient) {
				GameClient.sendHitReactionFromZombie((IsoPlayer)gameCharacter);
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setHitReaction("");
		gameCharacter.setAttackingZombie((IsoZombie)null);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (gameCharacter.getAttackingZombie() == null) {
			DebugLog.log("PlayerHitReactionState.animEvent (" + animEvent.m_EventName + ") zombie is null");
		} else {
			if (animEvent.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
				gameCharacter.getAttackingZombie().setHitForce(0.03F);
				gameCharacter.getAttackingZombie().setPlayerAttackPosition((String)null);
				gameCharacter.getAttackingZombie().bStaggerBack = true;
			}

			if (animEvent.m_EventName.equalsIgnoreCase("Defend")) {
				gameCharacter.getAttackingZombie().setHitReaction("BiteDefended");
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
				gameCharacter.Kill(gameCharacter.getAttackingZombie());
			}
		}
	}
}
