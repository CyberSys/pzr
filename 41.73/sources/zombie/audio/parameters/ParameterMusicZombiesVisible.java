package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;


public final class ParameterMusicZombiesVisible extends FMODGlobalParameter {
	private int m_playerIndex = -1;

	public ParameterMusicZombiesVisible() {
		super("MusicZombiesVisible");
	}

	public float calculateCurrentValue() {
		IsoPlayer player = this.choosePlayer();
		return player != null ? (float)PZMath.clamp(player.getStats().MusicZombiesVisible, 0, 50) : 0.0F;
	}

	private IsoPlayer choosePlayer() {
		if (this.m_playerIndex != -1) {
			IsoPlayer player = IsoPlayer.players[this.m_playerIndex];
			if (player == null || player.isDead()) {
				this.m_playerIndex = -1;
			}
		}

		if (this.m_playerIndex != -1) {
			return IsoPlayer.players[this.m_playerIndex];
		} else {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player2 = IsoPlayer.players[int1];
				if (player2 != null && !player2.isDead()) {
					this.m_playerIndex = int1;
					return player2;
				}
			}

			return null;
		}
	}
}
