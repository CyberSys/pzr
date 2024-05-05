package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;


public final class ParameterHardOfHearing extends FMODGlobalParameter {
	private int m_playerIndex = -1;

	public ParameterHardOfHearing() {
		super("HardOfHearing");
	}

	public float calculateCurrentValue() {
		IsoPlayer player = this.choosePlayer();
		if (player != null) {
			return player.getCharacterTraits().HardOfHearing.isSet() ? 1.0F : 0.0F;
		} else {
			return 0.0F;
		}
	}

	private IsoPlayer choosePlayer() {
		if (this.m_playerIndex != -1) {
			IsoPlayer player = IsoPlayer.players[this.m_playerIndex];
			if (player == null) {
				this.m_playerIndex = -1;
			}
		}

		if (this.m_playerIndex != -1) {
			return IsoPlayer.players[this.m_playerIndex];
		} else {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player2 = IsoPlayer.players[int1];
				if (player2 != null) {
					this.m_playerIndex = int1;
					return player2;
				}
			}

			return null;
		}
	}
}
