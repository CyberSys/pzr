package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoPlayer;


public final class ParameterMusicWakeState extends FMODGlobalParameter {
	private int m_playerIndex = -1;
	private ParameterMusicWakeState.State m_state;

	public ParameterMusicWakeState() {
		super("MusicWakeState");
		this.m_state = ParameterMusicWakeState.State.Awake;
	}

	public float calculateCurrentValue() {
		IsoPlayer player = this.choosePlayer();
		if (player != null && this.m_state == ParameterMusicWakeState.State.Awake && player.isAsleep()) {
			this.m_state = ParameterMusicWakeState.State.Sleeping;
		}

		return (float)this.m_state.label;
	}

	public void setState(IsoPlayer player, ParameterMusicWakeState.State state) {
		if (player == this.choosePlayer()) {
			this.m_state = state;
		}
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
					this.m_state = player2.isAsleep() ? ParameterMusicWakeState.State.Sleeping : ParameterMusicWakeState.State.Awake;
					return player2;
				}
			}

			return null;
		}
	}

	public static enum State {

		Awake,
		Sleeping,
		WakeNormal,
		WakeNightmare,
		WakeZombies,
		label;

		private State(int int1) {
			this.label = int1;
		}
		private static ParameterMusicWakeState.State[] $values() {
			return new ParameterMusicWakeState.State[]{Awake, Sleeping, WakeNormal, WakeNightmare, WakeZombies};
		}
	}
}
