package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;


public final class BurntToDeath extends State {
	private static final BurntToDeath _instance = new BurntToDeath();

	public static BurntToDeath instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoSurvivor) {
			gameCharacter.getDescriptor().bDead = true;
		}

		if (!(gameCharacter instanceof IsoZombie)) {
			gameCharacter.PlayAnimUnlooped("Die");
		} else {
			gameCharacter.PlayAnimUnlooped("ZombieDeath");
		}

		gameCharacter.def.AnimFrameIncrease = 0.25F;
		gameCharacter.setStateMachineLocked(true);
		String string = gameCharacter.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
		gameCharacter.getEmitter().playVocals(string);
		if (GameServer.bServer && gameCharacter instanceof IsoZombie) {
			GameServer.sendZombieSound(IsoZombie.ZombieSound.Burned, (IsoZombie)gameCharacter);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if ((int)gameCharacter.def.Frame == gameCharacter.sprite.CurrentAnim.Frames.size() - 1) {
			if (gameCharacter == TutorialManager.instance.wife) {
				gameCharacter.dir = IsoDirections.S;
			}

			gameCharacter.RemoveAttachedAnims();
			if (GameServer.bServer && gameCharacter instanceof IsoZombie) {
				GameServer.sendZombieDeath((IsoZombie)gameCharacter);
			}

			new IsoDeadBody(gameCharacter);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}
}
