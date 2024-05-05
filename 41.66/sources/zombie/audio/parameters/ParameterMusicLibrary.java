package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.core.Core;


public final class ParameterMusicLibrary extends FMODGlobalParameter {

	public ParameterMusicLibrary() {
		super("MusicLibrary");
	}

	public float calculateCurrentValue() {
		float float1;
		switch (Core.getInstance().getOptionMusicLibrary()) {
		case 2: 
			float1 = (float)ParameterMusicLibrary.Library.EarlyAccess.label;
			break;
		
		case 3: 
			float1 = (float)ParameterMusicLibrary.Library.Random.label;
			break;
		
		default: 
			float1 = (float)ParameterMusicLibrary.Library.Official.label;
		
		}
		return float1;
	}

	public static enum Library {

		Official,
		EarlyAccess,
		Random,
		label;

		private Library(int int1) {
			this.label = int1;
		}
		private static ParameterMusicLibrary.Library[] $values() {
			return new ParameterMusicLibrary.Library[]{Official, EarlyAccess, Random};
		}
	}
}
