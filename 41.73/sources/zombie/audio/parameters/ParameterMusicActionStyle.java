package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.core.Core;


public final class ParameterMusicActionStyle extends FMODGlobalParameter {

	public ParameterMusicActionStyle() {
		super("MusicActionStyle");
	}

	public float calculateCurrentValue() {
		return Core.getInstance().getOptionMusicActionStyle() == 2 ? (float)ParameterMusicActionStyle.State.Legacy.label : (float)ParameterMusicActionStyle.State.Official.label;
	}

	public static enum State {

		Official,
		Legacy,
		label;

		private State(int int1) {
			this.label = int1;
		}
		private static ParameterMusicActionStyle.State[] $values() {
			return new ParameterMusicActionStyle.State[]{Official, Legacy};
		}
	}
}
