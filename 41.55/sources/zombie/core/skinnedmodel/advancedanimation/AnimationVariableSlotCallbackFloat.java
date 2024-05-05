package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;


public final class AnimationVariableSlotCallbackFloat extends AnimationVariableSlotCallback {
	private float m_defaultValue = 0.0F;

	public AnimationVariableSlotCallbackFloat(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
	}

	public AnimationVariableSlotCallbackFloat(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public AnimationVariableSlotCallbackFloat(String string, float float1, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
		this.m_defaultValue = float1;
	}

	public AnimationVariableSlotCallbackFloat(String string, float float1, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
		this.m_defaultValue = float1;
	}

	public Float getDefaultValue() {
		return this.m_defaultValue;
	}

	public String getValueString() {
		return ((Float)this.getValue()).toString();
	}

	public float getValueFloat() {
		return (Float)this.getValue();
	}

	public boolean getValueBool() {
		return this.getValueFloat() != 0.0F;
	}

	public void setValue(String string) {
		this.trySetValue(PZMath.tryParseFloat(string, 0.0F));
	}

	public void setValue(float float1) {
		this.trySetValue(float1);
	}

	public void setValue(boolean boolean1) {
		this.trySetValue(boolean1 ? 1.0F : 0.0F);
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
