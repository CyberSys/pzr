package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.network.GameClient;
import zombie.popman.ZombiePopulationManager;


public final class ZombieSittingState extends State {
	private static final ZombieSittingState _instance = new ZombieSittingState();

	public static ZombieSittingState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (GameClient.bClient && gameCharacter.getCurrentSquare() != null) {
			ZombiePopulationManager.instance.sitAgainstWall(zombie, zombie.getCurrentSquare());
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}
}
