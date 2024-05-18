package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;


public class DieState extends State {
	static DieState _instance = new DieState();

	public static DieState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoPlayer) {
			DebugLog.log("DieState enter " + ((IsoPlayer)gameCharacter).username);
		}

		gameCharacter.getStateMachine().Lock = true;
		if (gameCharacter instanceof IsoPlayer) {
			gameCharacter.DoDeath((HandWeapon)null, (IsoGameCharacter)null);
		}

		if (gameCharacter instanceof IsoSurvivor) {
			((IsoSurvivor)gameCharacter).getDescriptor().bDead = true;
		}

		gameCharacter.PlayAnimUnlooped("ZombieDeath");
		gameCharacter.def.Frame = 0.0F;
		gameCharacter.def.AnimFrameIncrease = 0.25F;
		gameCharacter.setAnimated(true);
		gameCharacter.setDefaultState(this);
		if (gameCharacter instanceof IsoSurvivor && gameCharacter.getTimeSinceZombieAttack() < 10) {
			((IsoSurvivor)gameCharacter).ChewedByZombies();
		}

		if (GameServer.bServer && gameCharacter instanceof IsoPlayer) {
			IsoDeadBody deadBody = new IsoDeadBody(gameCharacter);
			GameServer.PlayerToBody.put((IsoPlayer)gameCharacter, deadBody);
			GameServer.SendDeath((IsoPlayer)gameCharacter);
			if (gameCharacter.shouldBecomeZombieAfterDeath()) {
				deadBody.reanimateLater();
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getCurrentSquare() != null) {
			if ((int)gameCharacter.def.Frame == gameCharacter.sprite.CurrentAnim.Frames.size() - 1) {
				if (gameCharacter instanceof IsoSurvivor) {
					((IsoSurvivor)gameCharacter).SetAllFrames((short)((int)gameCharacter.def.Frame));
				}

				if (gameCharacter == TutorialManager.instance.wife) {
					gameCharacter.dir = IsoDirections.S;
				}

				if (GameServer.bServer && gameCharacter instanceof IsoZombie) {
					GameServer.sendDeadZombie((IsoZombie)gameCharacter);
				}

				if (!GameServer.bServer) {
					IsoDeadBody deadBody = new IsoDeadBody(gameCharacter);
					if (GameClient.bClient && gameCharacter != IsoPlayer.instance) {
						DebugLog.log("DieState adding " + ((IsoPlayer)gameCharacter).username + " to PlayerToBody");
						GameClient.instance.PlayerToBody.put((IsoPlayer)gameCharacter, deadBody);
					}

					if (gameCharacter.shouldBecomeZombieAfterDeath()) {
						deadBody.reanimateLater();
					}
				}
			}
		}
	}
}
