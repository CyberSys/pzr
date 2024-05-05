package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoPlayer;


public final class ParameterLocalPlayer extends FMODLocalParameter {
	private final IsoPlayer player;

	public ParameterLocalPlayer(IsoPlayer player) {
		super("LocalPlayer");
		this.player = player;
	}

	public float calculateCurrentValue() {
		return this.player.isLocalPlayer() ? 1.0F : 0.0F;
	}
}
