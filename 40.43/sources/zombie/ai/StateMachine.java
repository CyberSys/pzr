package zombie.ai;

import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;


public class StateMachine {
	public boolean Lock = false;
	State CurrentState;
	State GlobalState;
	State NextState;
	IsoGameCharacter Owner;
	State PreviousState;

	public StateMachine(IsoGameCharacter gameCharacter) {
		this.Owner = gameCharacter;
	}

	public void changeState(State state) {
		if (!this.Lock) {
			if (this.CurrentState != state) {
				this.PreviousState = this.CurrentState;
				if (this.CurrentState != null) {
					this.CurrentState.exit(this.Owner);
				}

				this.CurrentState = state;
				this.NextState = null;
				if (this.CurrentState != null) {
					this.CurrentState.enter(this.Owner);
				}
			}
		}
	}

	public void changeState(State state, State state2) {
		if (state != this.CurrentState) {
			if (!this.Lock) {
				if (state != this.CurrentState) {
					LuaEventManager.triggerEvent("OnAIStateChange", this.Owner, state, this.CurrentState);
				}

				this.PreviousState = this.CurrentState;
				if (this.CurrentState != null) {
					this.CurrentState.exit(this.Owner);
				}

				this.CurrentState = state;
				this.NextState = state2;
				if (this.CurrentState != null) {
					this.CurrentState.enter(this.Owner);
				}
			}
		}
	}

	public State getCurrent() {
		return this.CurrentState;
	}

	public State getGlobal() {
		return this.GlobalState;
	}

	public State getPrevious() {
		return this.PreviousState;
	}

	public void RevertToPrevious() {
		if (!this.Lock) {
			this.changeState(this.PreviousState);
		}
	}

	public void setCurrent(State state) {
		if (!this.Lock) {
			this.CurrentState = state;
		}
	}

	public void setGlobal(State state) {
		this.GlobalState = state;
	}

	public void setPrevious(State state) {
		if (!this.Lock) {
			this.PreviousState = state;
		}
	}

	public void update() {
		if (this.GlobalState != null) {
			this.GlobalState.execute(this.Owner);
		}

		if (this.CurrentState != null) {
			this.CurrentState.execute(this.Owner);
		}
	}
}
