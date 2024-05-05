package zombie.characters.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.action.conditions.CharacterVariableCondition;
import zombie.characters.action.conditions.EventNotOccurred;
import zombie.characters.action.conditions.EventOccurred;
import zombie.characters.action.conditions.LuaCall;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.GameClient;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public final class ActionContext {
	private final IAnimatable m_owner;
	private ActionGroup m_stateGroup;
	private ActionState m_currentState;
	private final ArrayList m_childStates = new ArrayList();
	private String m_previousStateName = null;
	private boolean m_statesChanged = false;
	public final ArrayList onStateChanged = new ArrayList();
	private final ActionContextEvents occurredEvents = new ActionContextEvents();

	public ActionContext(IAnimatable iAnimatable) {
		this.m_owner = iAnimatable;
	}

	public IAnimatable getOwner() {
		return this.m_owner;
	}

	public void update() {
		ActionContext.s_performance.update.invokeAndMeasure(this, ActionContext::updateInternal);
	}

	private void updateInternal() {
		if (this.m_currentState == null) {
			this.logCurrentState();
		} else {
			ActionContext.s_performance.evaluateCurrentStateTransitions.invokeAndMeasure(this, ActionContext::evaluateCurrentStateTransitions);
			ActionContext.s_performance.evaluateSubStateTransitions.invokeAndMeasure(this, ActionContext::evaluateSubStateTransitions);
			this.invokeAnyStateChangedEvents();
			this.logCurrentState();
		}
	}

	private void evaluateCurrentStateTransitions() {
		for (int int1 = 0; int1 < this.m_currentState.transitions.size(); ++int1) {
			ActionTransition actionTransition = (ActionTransition)this.m_currentState.transitions.get(int1);
			if (actionTransition.passes(this, 0)) {
				if (StringUtils.isNullOrWhitespace(actionTransition.transitionTo)) {
					DebugLog.ActionSystem.warn("%s> Transition\'s target state not specified: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
				} else {
					ActionState actionState = this.m_stateGroup.get(actionTransition.transitionTo);
					if (actionState == null) {
						DebugLog.ActionSystem.warn("%s> Transition\'s target state not found: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
					} else if (!this.hasChildState(actionState)) {
						if (!actionTransition.asSubstate || !this.currentStateSupportsChildState(actionState)) {
							if (this.m_owner instanceof IsoPlayer) {
								DebugType debugType = DebugType.ActionSystem;
								String string = ((IsoPlayer)this.m_owner).getUsername();
								DebugLog.log(debugType, "Player \'" + string + "\' transits from " + this.m_currentState.getName() + " to " + actionTransition.transitionTo);
							}

							this.setCurrentState(actionState);
							break;
						}

						this.tryAddChildState(actionState);
					}
				}
			}
		}
	}

	private void evaluateSubStateTransitions() {
		for (int int1 = 0; int1 < this.childStateCount(); ++int1) {
			ActionState actionState = null;
			ActionState actionState2 = this.getChildStateAt(int1);
			for (int int2 = 0; int2 < actionState2.transitions.size(); ++int2) {
				ActionTransition actionTransition = (ActionTransition)actionState2.transitions.get(int2);
				if (actionTransition.passes(this, 1)) {
					if (actionTransition.transitionOut) {
						this.removeChildStateAt(int1);
						--int1;
						break;
					}

					if (!StringUtils.isNullOrWhitespace(actionTransition.transitionTo)) {
						ActionState actionState3 = this.m_stateGroup.get(actionTransition.transitionTo);
						if (actionState3 == null) {
							DebugLog.ActionSystem.warn("%s> Transition\'s target state not found: \"%s\"", this.getOwner().getUID(), actionTransition.transitionTo);
						} else if (!this.hasChildState(actionState3)) {
							if (this.currentStateSupportsChildState(actionState3)) {
								this.m_childStates.set(int1, actionState3);
								this.onStatesChanged();
								break;
							}

							if (actionTransition.forceParent) {
								actionState = actionState3;
								break;
							}
						}
					}
				}
			}

			if (actionState != this.m_currentState && actionState != null) {
				this.setCurrentState(actionState);
			}
		}
	}

	protected boolean currentStateSupportsChildState(ActionState actionState) {
		return this.m_currentState == null ? false : this.m_currentState.canHaveSubState(actionState);
	}

	private boolean hasChildState(ActionState actionState) {
		int int1 = this.indexOfChildState((actionStatex)->{
    return actionStatex == actionState;
});
		return int1 > -1;
	}

	public void setPlaybackStateSnapshot(ActionStateSnapshot actionStateSnapshot) {
		if (this.m_stateGroup != null) {
			if (actionStateSnapshot.stateName == null) {
				DebugLog.General.warn("Snapshot not valid. Missing root state name.");
			} else {
				ActionState actionState = this.m_stateGroup.get(actionStateSnapshot.stateName);
				this.setCurrentState(actionState);
				if (PZArrayUtil.isNullOrEmpty((Object[])actionStateSnapshot.childStateNames)) {
					while (this.childStateCount() > 0) {
						this.removeChildStateAt(0);
					}
				} else {
					int int1;
					String string;
					for (int1 = 0; int1 < this.childStateCount(); ++int1) {
						string = this.getChildStateAt(int1).name;
						boolean boolean1 = StringUtils.contains(actionStateSnapshot.childStateNames, string, StringUtils::equalsIgnoreCase);
						if (!boolean1) {
							this.removeChildStateAt(int1);
							--int1;
						}
					}

					for (int1 = 0; int1 < actionStateSnapshot.childStateNames.length; ++int1) {
						string = actionStateSnapshot.childStateNames[int1];
						ActionState actionState2 = this.m_stateGroup.get(string);
						this.tryAddChildState(actionState2);
					}
				}
			}
		}
	}

	public ActionStateSnapshot getPlaybackStateSnapshot() {
		if (this.m_currentState == null) {
			return null;
		} else {
			ActionStateSnapshot actionStateSnapshot = new ActionStateSnapshot();
			actionStateSnapshot.stateName = this.m_currentState.name;
			actionStateSnapshot.childStateNames = new String[this.m_childStates.size()];
			for (int int1 = 0; int1 < actionStateSnapshot.childStateNames.length; ++int1) {
				actionStateSnapshot.childStateNames[int1] = ((ActionState)this.m_childStates.get(int1)).name;
			}

			return actionStateSnapshot;
		}
	}

	protected boolean setCurrentState(ActionState actionState) {
		if (actionState == this.m_currentState) {
			return false;
		} else {
			this.m_previousStateName = this.m_currentState == null ? "" : this.m_currentState.getName();
			this.m_currentState = actionState;
			for (int int1 = 0; int1 < this.m_childStates.size(); ++int1) {
				ActionState actionState2 = (ActionState)this.m_childStates.get(int1);
				if (!this.m_currentState.canHaveSubState(actionState2)) {
					this.removeChildStateAt(int1);
					--int1;
				}
			}

			this.onStatesChanged();
			return true;
		}
	}

	protected boolean tryAddChildState(ActionState actionState) {
		if (this.hasChildState(actionState)) {
			return false;
		} else {
			this.m_childStates.add(actionState);
			this.onStatesChanged();
			return true;
		}
	}

	protected void removeChildStateAt(int int1) {
		this.m_childStates.remove(int1);
		this.onStatesChanged();
	}

	private void onStatesChanged() {
		this.m_statesChanged = true;
	}

	public void logCurrentState() {
		if (this.m_owner.isAnimationRecorderActive()) {
			this.m_owner.getAnimationPlayerRecorder().logActionState(this.m_currentState, this.m_childStates);
		}
	}

	private void invokeAnyStateChangedEvents() {
		if (this.m_statesChanged) {
			this.m_statesChanged = false;
			this.occurredEvents.clear();
			for (int int1 = 0; int1 < this.onStateChanged.size(); ++int1) {
				IActionStateChanged iActionStateChanged = (IActionStateChanged)this.onStateChanged.get(int1);
				iActionStateChanged.actionStateChanged(this);
			}

			if (this.m_owner instanceof IsoZombie) {
				((IsoZombie)this.m_owner).networkAI.extraUpdate();
			}
		}
	}

	public ActionState getCurrentState() {
		return this.m_currentState;
	}

	public void setGroup(ActionGroup actionGroup) {
		String string = this.m_currentState == null ? null : this.m_currentState.name;
		this.m_stateGroup = actionGroup;
		ActionState actionState = actionGroup.getInitialState();
		if (!StringUtils.equalsIgnoreCase(string, actionState.name)) {
			this.setCurrentState(actionState);
		} else {
			this.m_currentState = actionState;
		}
	}

	public ActionGroup getGroup() {
		return this.m_stateGroup;
	}

	public void reportEvent(String string) {
		this.reportEvent(-1, string);
	}

	public void reportEvent(int int1, String string) {
		this.occurredEvents.add(string, int1);
		if (GameClient.bClient && int1 == -1 && this.m_owner instanceof IsoPlayer && ((IsoPlayer)this.m_owner).isLocalPlayer()) {
			GameClient.sendEvent((IsoPlayer)this.m_owner, string);
		}
	}

	public final boolean hasChildStates() {
		return this.childStateCount() > 0;
	}

	public final int childStateCount() {
		return this.m_childStates != null ? this.m_childStates.size() : 0;
	}

	public final void foreachChildState(Consumer consumer) {
		for (int int1 = 0; int1 < this.childStateCount(); ++int1) {
			ActionState actionState = this.getChildStateAt(int1);
			consumer.accept(actionState);
		}
	}

	public final int indexOfChildState(Predicate predicate) {
		int int1 = -1;
		for (int int2 = 0; int2 < this.childStateCount(); ++int2) {
			ActionState actionState = this.getChildStateAt(int2);
			if (predicate.test(actionState)) {
				int1 = int2;
				break;
			}
		}

		return int1;
	}

	public final ActionState getChildStateAt(int int1) {
		if (int1 >= 0 && int1 < this.childStateCount()) {
			return (ActionState)this.m_childStates.get(int1);
		} else {
			throw new IndexOutOfBoundsException(String.format("Index %d out of bounds. childCount: %d", int1, this.childStateCount()));
		}
	}

	public List getChildStates() {
		return this.m_childStates;
	}

	public String getCurrentStateName() {
		return this.m_currentState.name;
	}

	public String getPreviousStateName() {
		return this.m_previousStateName;
	}

	public boolean hasEventOccurred(String string) {
		return this.hasEventOccurred(string, -1);
	}

	public boolean hasEventOccurred(String string, int int1) {
		return this.occurredEvents.contains(string, int1);
	}

	public void clearEvent(String string) {
		this.occurredEvents.clearEvent(string);
	}

	static  {
	CharacterVariableCondition.Factory var0 = new CharacterVariableCondition.Factory();
		IActionCondition.registerFactory("isTrue", var0);
		IActionCondition.registerFactory("isFalse", var0);
		IActionCondition.registerFactory("compare", var0);
		IActionCondition.registerFactory("gtr", var0);
		IActionCondition.registerFactory("less", var0);
		IActionCondition.registerFactory("equals", var0);
		IActionCondition.registerFactory("lessEqual", var0);
		IActionCondition.registerFactory("gtrEqual", var0);
		IActionCondition.registerFactory("notEquals", var0);
		IActionCondition.registerFactory("eventOccurred", new EventOccurred.Factory());
		IActionCondition.registerFactory("eventNotOccurred", new EventNotOccurred.Factory());
		IActionCondition.registerFactory("lua", new LuaCall.Factory());
	}

	private static class s_performance {
		static final PerformanceProfileProbe update = new PerformanceProfileProbe("ActionContext.update");
		static final PerformanceProfileProbe evaluateCurrentStateTransitions = new PerformanceProfileProbe("ActionContext.evaluateCurrentStateTransitions");
		static final PerformanceProfileProbe evaluateSubStateTransitions = new PerformanceProfileProbe("ActionContext.evaluateSubStateTransitions");
	}
}
