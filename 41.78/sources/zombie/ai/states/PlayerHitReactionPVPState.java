package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameServer;


public final class PlayerHitReactionPVPState extends State {
	private static final PlayerHitReactionPVPState _instance = new PlayerHitReactionPVPState();

	public static PlayerHitReactionPVPState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (!gameCharacter.getCharacterActions().isEmpty()) {
			((BaseAction)gameCharacter.getCharacterActions().get(0)).forceStop();
		}

		gameCharacter.setSitOnGround(false);
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setHitReaction("");
		gameCharacter.setVariable("hitpvp", false);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		if (animEvent.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
			gameCharacter.getAttackedBy().setHitForce(0.03F);
			if (gameCharacter.getAttackedBy() instanceof IsoZombie) {
				((IsoZombie)gameCharacter.getAttackedBy()).setPlayerAttackPosition((String)null);
				((IsoZombie)gameCharacter.getAttackedBy()).setStaggerBack(true);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("Defend")) {
			gameCharacter.getAttackedBy().setHitReaction("BiteDefended");
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
	}
}
