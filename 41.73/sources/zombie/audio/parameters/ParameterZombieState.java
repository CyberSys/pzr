package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;
import zombie.characters.IsoZombie;


public final class ParameterZombieState extends FMODLocalParameter {
	private final IsoZombie zombie;
	private ParameterZombieState.State state;

	public ParameterZombieState(IsoZombie zombie) {
		super("ZombieState");
		this.state = ParameterZombieState.State.Idle;
		this.zombie = zombie;
	}

	public float calculateCurrentValue() {
		if (this.zombie.target == null) {
			if (this.state == ParameterZombieState.State.SearchTarget) {
				this.setState(ParameterZombieState.State.Idle);
			}
		} else if (this.state == ParameterZombieState.State.Idle) {
			this.setState(ParameterZombieState.State.SearchTarget);
		}

		return (float)this.state.index;
	}

	public void setState(ParameterZombieState.State state) {
		if (state != this.state) {
			this.state = state;
		}
	}

	public boolean isState(ParameterZombieState.State state) {
		return this.state == state;
	}

	public static enum State {

		Idle,
		Eating,
		SearchTarget,
		LockTarget,
		AttackScratch,
		AttackLacerate,
		AttackBite,
		Hit,
		Death,
		Reanimate,
		Pushed,
		GettingUp,
		Attack,
		RunOver,
		index;

		private State(int int1) {
			this.index = int1;
		}
		private static ParameterZombieState.State[] $values() {
			return new ParameterZombieState.State[]{Idle, Eating, SearchTarget, LockTarget, AttackScratch, AttackLacerate, AttackBite, Hit, Death, Reanimate, Pushed, GettingUp, Attack, RunOver};
		}
	}
}
