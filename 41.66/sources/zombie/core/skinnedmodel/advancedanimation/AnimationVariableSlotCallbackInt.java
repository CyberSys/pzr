package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;


public final class AnimationVariableSlotCallbackInt extends AnimationVariableSlotCallback {
	private int m_defaultValue = 0;

	public AnimationVariableSlotCallbackInt(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
	}

	public AnimationVariableSlotCallbackInt(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public AnimationVariableSlotCallbackInt(String string, int int1, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
		this.m_defaultValue = int1;
	}

	public AnimationVariableSlotCallbackInt(String string, int int1, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
		this.m_defaultValue = int1;
	}

	public Integer getDefaultValue() {
		return this.m_defaultValue;
	}

	public String getValueString() {
		return ((Integer)this.getValue()).toString();
	}

	public float getValueFloat() {
		return (float)(Integer)this.getValue();
	}

	public boolean getValueBool() {
		return this.getValueFloat() != 0.0F;
	}

	public void setValue(String string) {
		this.trySetValue(PZMath.tryParseInt(string, 0));
	}

	public void setValue(float float1) {
		this.trySetValue((int)float1);
	}

	public void setValue(boolean boolean1) {
		this.trySetValue(boolean1 ? 1 : 0);
	}

	public AnimationVariableType getType() {
		return AnimationVariableType.Float;
	}

	public boolean canConvertFrom(String string) {
		return true;
	}

	public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet {
	}

	public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet {
	}
}
