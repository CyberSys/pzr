package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;


public final class ParameterMusicState extends FMODGlobalParameter {
	private ParameterMusicState.State state;

	public ParameterMusicState() {
		super("MusicState");
		this.state = ParameterMusicState.State.MainMenu;
	}

	public float calculateCurrentValue() {
		return (float)this.state.label;
	}

	public void setState(ParameterMusicState.State state) {
		this.state = state;
	}

	public static enum State {

		MainMenu,
		Loading,
		InGame,
		PauseMenu,
		Tutorial,
		label;

		private State(int int1) {
			this.label = int1;
		}
		private static ParameterMusicState.State[] $values() {
			return new ParameterMusicState.State[]{MainMenu, Loading, InGame, PauseMenu, Tutorial};
		}
	}
}
