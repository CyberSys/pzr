package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;


public final class ParameterCameraZoom extends FMODGlobalParameter {

	public ParameterCameraZoom() {
		super("CameraZoom");
	}

	public float calculateCurrentValue() {
		IsoPlayer player = this.getPlayer();
		if (player == null) {
			return 0.0F;
		} else {
			float float1 = Core.getInstance().getZoom(player.PlayerIndex) - Core.getInstance().OffscreenBuffer.getMinZoom();
			float float2 = Core.getInstance().OffscreenBuffer.getMaxZoom() - Core.getInstance().OffscreenBuffer.getMinZoom();
			return float1 / float2;
		}
	}

	private IsoPlayer getPlayer() {
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
