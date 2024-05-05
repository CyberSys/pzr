package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
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
			if (roomDef != null) {
				return (float)roomDef.getArea();
			} else {
				IsoGridSquare square = gameCharacter.getCurrentSquare();
				return square != null && square.isInARoom() ? (float)square.getRoomSize() : 0.0F;
			}
		}
	}

	private IsoGameCharacter getCharacter() {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && (player == null || player.isDead() && player2.isAlive() || player.Traits.Deaf.isSet() && !player2.Traits.Deaf.isSet())) {
				player = player2;
			}
		}

		return player;
	}
}
