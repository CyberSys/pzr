package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class PlayerOnGroundState extends State {
	private static final PlayerOnGroundState _instance = new PlayerOnGroundState();

	public static PlayerOnGroundState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		((IsoPlayer)gameCharacter).setBlockMovement(true);
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

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		((IsoPlayer)gameCharacter).setBlockMovement(false);
	}
}
