package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;


public final class PlayerFallDownState extends State {
	private static final PlayerFallDownState _instance = new PlayerFallDownState();

	public static PlayerFallDownState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		gameCharacter.clearVariable("bKnockedDown");
		if (gameCharacter instanceof IsoPlayer && gameCharacter.isDead()) {
			if (!gameCharacter.isOnDeathDone()) {
				gameCharacter.setOnDeathDone(true);
				gameCharacter.DoDeath((HandWeapon)null, (IsoGameCharacter)null);
			}

			if (GameServer.bServer) {
				IsoDeadBody deadBody = new IsoDeadBody(gameCharacter);
				GameServer.PlayerToBody.put((IsoPlayer)gameCharacter, deadBody);
				GameServer.SendDeath((IsoPlayer)gameCharacter);
				if (gameCharacter.shouldBecomeZombieAfterDeath()) {
					deadBody.reanimateLater();
				}
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
	}
}
