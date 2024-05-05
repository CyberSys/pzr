package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;


public final class AnimationVariableSlotCallbackBool extends AnimationVariableSlotCallback {
	private boolean m_defaultValue = false;

	public AnimationVariableSlotCallbackBool(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
	}

	public AnimationVariableSlotCallbackBool(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public AnimationVariableSlotCallbackBool(String string, boolean boolean1, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
		this.m_defaultValue = boolean1;
	}

	public AnimationVariableSlotCallbackBool(String string, boolean boolean1, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
		this.m_defaultValue = boolean1;
	}

	public Boolean getDefaultValue() {
		return this.m_defaultValue;
	}

	public String getValueString() {
		return (Boolean)this.getValue() ? "true" : "false";
	}

	public float getValueFloat() {
		return (Boolean)this.getValue() ? 1.0F : 0.0F;
	}

	public boolean getValueBool() {
		return (Boolean)this.getValue();
	}

	public void setValue(String string) {
		this.trySetValue(StringUtils.tryParseBoolean(string));
	}

	public void setValue(float float1) {
		this.trySetValue((double)float1 != 0.0);
	}

	public void setValue(boolean boolean1) {
		this.trySetValue(boolean1);
	}

	public AnimationVariableType getType() {
		return AnimationVariableType.Boolean;
	}

	public boolean canConvertFrom(String string) {
		return StringUtils.tryParseBoolean(string);
	}

	public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet {
	}

	public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet {
	}
}
