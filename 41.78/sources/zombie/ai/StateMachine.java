package zombie.ai;

import java.util.ArrayList;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;


public final class StateMachine {
	private boolean m_isLocked = false;
	public int activeStateChanged = 0;
	private State m_currentState;
	private State m_previousState;
	private final IsoGameCharacter m_owner;
	private final List m_subStates = new ArrayList();

	public StateMachine(IsoGameCharacter gameCharacter) {
		this.m_owner = gameCharacter;
	}

	public void changeState(State state, Iterable iterable) {
		this.changeState(state, iterable, false);
	}

	public void changeState(State state, Iterable iterable, boolean boolean1) {
		if (!this.m_isLocked) {
			this.changeRootState(state, boolean1);
			PZArrayUtil.forEach(this.m_subStates, (var0)->{
				var0.shouldBeActive = false;
			});

			PZArrayUtil.forEach(iterable, Lambda.consumer(this, (var0,statex)->{
				if (var0 != null) {
					statex.ensureSubstateActive(var0);
				}
			}));

			Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.m_subStates, this, (var0,statex)->{
				if (!var0.shouldBeActive && !var0.isEmpty()) {
					statex.removeSubstate(var0);
				}
			});
		}
	}

	private void changeRootState(State state, boolean boolean1) {
		if (this.m_currentState == state) {
			if (boolean1) {
				this.stateEnter(this.m_currentState);
			}
		} else {
			State state2 = this.m_currentState;
			if (state2 != null) {
				this.stateExit(state2);
			}

			this.m_previousState = state2;
			this.m_currentState = state;
			if (state != null) {
				this.stateEnter(state);
			}

			LuaEventManager.triggerEvent("OnAIStateChange", this.m_owner, this.m_currentState, this.m_previousState);
		}
	}

	private void ensureSubstateActive(State state) {
		StateMachine.SubstateSlot substateSlot = this.getExistingSlot(state);
		if (substateSlot != null) {
			substateSlot.shouldBeActive = true;
		} else {
			StateMachine.SubstateSlot substateSlot2 = (StateMachine.SubstateSlot)PZArrayUtil.find(this.m_subStates, StateMachine.SubstateSlot::isEmpty);
			if (substateSlot2 != null) {
				substateSlot2.setState(state);
				substateSlot2.shouldBeActive = true;
			} else {
				StateMachine.SubstateSlot substateSlot3 = new StateMachine.SubstateSlot(state);
				this.m_subStates.add(substateSlot3);
			}

			this.stateEnter(state);
		}
	}

	private StateMachine.SubstateSlot getExistingSlot(State state) {
		return (StateMachine.SubstateSlot)PZArrayUtil.find(this.m_subStates, Lambda.predicate(state, (var0,statex)->{
			return var0.getState() == statex;
		}));
	}

	private void removeSubstate(State state) {
		StateMachine.SubstateSlot substateSlot = this.getExistingSlot(state);
		if (substateSlot != null) {
			this.removeSubstate(substateSlot);
		}
	}

	private void removeSubstate(StateMachine.SubstateSlot substateSlot) {
		State state = substateSlot.getState();
		substateSlot.setState((State)null);
		if (state != this.m_currentState || state != SwipeStatePlayer.instance()) {
			this.stateExit(state);
		}
	}

	public boolean isSubstate(State state) {
		return PZArrayUtil.contains(this.m_subStates, Lambda.predicate(state, (var0,statex)->{
			return var0.getState() == statex;
		}));
	}

	public State getCurrent() {
		return this.m_currentState;
	}

	public State getPrevious() {
		return this.m_previousState;
	}

	public int getSubStateCount() {
		return this.m_subStates.size();
	}

	public State getSubStateAt(int int1) {
		return ((StateMachine.SubstateSlot)this.m_subStates.get(int1)).getState();
	}

	public void revertToPreviousState(State state) {
		if (this.isSubstate(state)) {
			this.removeSubstate(state);
		} else if (this.m_currentState != state) {
			DebugLog.ActionSystem.warn("The sender $s is not an active state in this state machine.", String.valueOf(state));
		} else {
			this.changeRootState(this.m_previousState, false);
		}
	}

	public void update() {
		if (this.m_currentState != null) {
			this.m_currentState.execute(this.m_owner);
		}

		Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.m_subStates, this.m_owner, (var0,var1)->{
			if (!var0.isEmpty()) {
				var0.state.execute(var1);
			}
		});
		this.logCurrentState();
	}

	private void logCurrentState() {
		if (this.m_owner.isAnimationRecorderActive()) {
			this.m_owner.getAnimationPlayerRecorder().logAIState(this.m_currentState, this.m_subStates);
		}
	}

	private void stateEnter(State state) {
		state.enter(this.m_owner);
	}

	private void stateExit(State state) {
		state.exit(this.m_owner);
	}

	public final void stateAnimEvent(int int1, AnimEvent animEvent) {
		if (int1 == 0) {
			if (this.m_currentState != null) {
				this.m_currentState.animEvent(this.m_owner, animEvent);
			}
		} else {
			Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.m_subStates, this.m_owner, animEvent, (var0,int1x,animEventx)->{
				if (!var0.isEmpty()) {
					var0.state.animEvent(int1x, animEventx);
				}
			});
		}
	}

	public boolean isLocked() {
		return this.m_isLocked;
	}

	public void setLocked(boolean boolean1) {
		this.m_isLocked = boolean1;
	}

	public static class SubstateSlot {
		private State state;
		boolean shouldBeActive;

		SubstateSlot(State state) {
			this.state = state;
			this.shouldBeActive = true;
		}

		public State getState() {
			return this.state;
		}

		void setState(State state) {
			this.state = state;
		}

		public boolean isEmpty() {
			return this.state == null;
		}
	}
}
