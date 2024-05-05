package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;


public final class PlayerOnGroundState extends State {
	private static final PlayerOnGroundState _instance = new PlayerOnGroundState();

	public static PlayerOnGroundState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(true);
		((IsoPlayer)gameCharacter).setBlockMovement(true);
		gameCharacter.setVariable("bAnimEnd", false);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (!GameServer.bServer && gameCharacter.isDead()) {
			gameCharacter.die();
		} else {
			gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovement(false);
		((IsoPlayer)gameCharacter).setBlockMovement(false);
	}
}
