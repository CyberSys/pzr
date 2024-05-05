package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;


public final class FakeDeadZombieState extends State {
	private static final FakeDeadZombieState _instance = new FakeDeadZombieState();

	public static FakeDeadZombieState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setVisibleToNPCs(false);
		gameCharacter.setCollidable(false);
		((IsoZombie)gameCharacter).setFakeDead(true);
		gameCharacter.setOnFloor(true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter.isDead()) {
			if (GameServer.bServer) {
				GameServer.sendDeadZombie((IsoZombie)gameCharacter);
			}

			new IsoDeadBody(gameCharacter);
		} else {
			if (Core.bLastStand) {
				((IsoZombie)gameCharacter).setFakeDead(false);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		((IsoZombie)gameCharacter).setFakeDead(false);
	}
}