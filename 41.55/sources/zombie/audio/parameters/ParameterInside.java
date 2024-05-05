package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;


public final class ParameterInside extends FMODGlobalParameter {

	public ParameterInside() {
		super("Inside");
	}

	public float calculateCurrentValue() {
		IsoGameCharacter gameCharacter = this.getCharacter();
		if (gameCharacter == null) {
			return 0.0F;
		} else if (gameCharacter.getVehicle() == null) {
			return gameCharacter.getCurrentBuilding() == null ? 0.0F : 1.0F;
		} else {
			return -1.0F;
		}
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
