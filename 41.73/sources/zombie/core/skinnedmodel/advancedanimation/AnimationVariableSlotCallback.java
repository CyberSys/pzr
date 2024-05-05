package zombie.core.skinnedmodel.advancedanimation;

import zombie.debug.DebugLog;


public abstract class AnimationVariableSlotCallback extends AnimationVariableSlot {
	private final AnimationVariableSlotCallback.CallbackGet m_callbackGet;
	private final AnimationVariableSlotCallback.CallbackSet m_callbackSet;

	protected AnimationVariableSlotCallback(String string, AnimationVariableSlotCallback.CallbackGet callbackGet) {
		this(string, callbackGet, (AnimationVariableSlotCallback.CallbackSet)null);
	}

	protected AnimationVariableSlotCallback(String string, AnimationVariableSlotCallback.CallbackGet callbackGet, AnimationVariableSlotCallback.CallbackSet callbackSet) {
		super(string);
		this.m_callbackGet = callbackGet;
		this.m_callbackSet = callbackSet;
	}

	public Object getValue() {
		return this.m_callbackGet.call();
	}

	public abstract Object getDefaultValue();

	public boolean trySetValue(Object object) {
		if (this.isReadOnly()) {
			DebugLog.General.warn("Trying to set read-only variable \"%s\"", super.getKey());
			return false;
		} else {
			this.m_callbackSet.call(object);
			return true;
		}
	}

	public boolean isReadOnly() {
		return this.m_callbackSet == null;
	}

	public void clear() {
		if (!this.isReadOnly()) {
			this.trySetValue(this.getDefaultValue());
		}
	}

	public interface CallbackGet {

		Object call();
	}

	public interface CallbackSet {

		void call(Object object);
	}
}
