package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.RoomDef;


public final class ParameterRoomSize extends FMODGlobalParameter {

	public ParameterRoomSize() {
		super("RoomSize");
	}

	public float calculateCurrentValue() {
		IsoGameCharacter gameCharacter = this.getCharacter();
		if (gameCharacter == null) {
			return 0.0F;
		} else {
			RoomDef roomDef = gameCharacter.getCurrentRoomDef();
			return roomDef == null ? 0.0F : (float)roomDef.getArea();
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
