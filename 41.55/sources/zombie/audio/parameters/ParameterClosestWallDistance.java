package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.NearestWalls;


public final class ParameterClosestWallDistance extends FMODGlobalParameter {
	public ParameterClosestWallDistance() {
		super("ClosestWallDistance");
	}

	public float calculateCurrentValue() {
		IsoGameCharacter gameCharacter = this.getCharacter();
		return gameCharacter == null ? 127.0F : (float)NearestWalls.ClosestWallDistance(gameCharacter.getCurrentSquare());
	}

	private IsoGameCharacter getCharacter() {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && (player == null || player.isDead() && player2.isAlive())) {
				player = player2;
			}
		}

		return player;
	}
}
