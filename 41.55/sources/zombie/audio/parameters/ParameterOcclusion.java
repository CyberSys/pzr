package zombie.audio.parameters;

import fmod.fmod.FMODSoundEmitter;
import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;


public final class ParameterOcclusion extends FMODLocalParameter {
	private final FMODSoundEmitter emitter;
	private float currentValue = Float.NaN;

	public ParameterOcclusion(FMODSoundEmitter fMODSoundEmitter) {
		super("Occlusion");
		this.emitter = fMODSoundEmitter;
	}

	public float calculateCurrentValue() {
		float float1 = 1.0F;
		for (int int1 = 0; int1 < 4; ++int1) {
			float float2 = this.calculateValueForPlayer(int1);
			float1 = PZMath.min(float1, float2);
		}

		this.currentValue = float1;
		return (float)((int)(this.currentValue * 1000.0F)) / 1000.0F;
	}

	public void resetToDefault() {
		this.currentValue = Float.NaN;
	}

	private float calculateValueForPlayer(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player == null) {
			return 1.0F;
		} else {
			IsoGridSquare square = player.getCurrentSquare();
			IsoGridSquare square2 = IsoWorld.instance.getCell().getGridSquare((double)this.emitter.x, (double)this.emitter.y, (double)this.emitter.z);
			if (square2 == null) {
				boolean boolean1 = true;
			}

			float float1 = 0.0F;
			if (square != null && square2 != null && !square2.isCouldSee(int1)) {
				float1 = 0.3F;
			}

			return float1;
		}
	}
}
