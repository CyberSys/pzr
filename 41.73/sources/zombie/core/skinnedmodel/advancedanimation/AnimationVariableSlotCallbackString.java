package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;
import zombie.util.StringUtils;


public final class AnimationVariableSlotCallbackString extends AnimationVariableSlotCallback {
	private String m_defaultValue = "";

	public AnimationVariableSlotCallbackString(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
	}

	public AnimationVariableSlotCallbackString(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public AnimationVariableSlotCallbackString(String string, String string2, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
		super(string, callbackGetStrongTyped);
		this.m_defaultValue = string2;
	}

	public AnimationVariableSlotCallbackString(String string, String string2, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
		super(string, callbackGetStrongTyped, callbackSetStrongTyped);
		this.m_defaultValue = string2;
	}

	public String getDefaultValue() {
		return this.m_defaultValue;
	}

	public String getValueString() {
		return (String)this.getValue();
	}

	public float getValueFloat() {
		return PZMath.tryParseFloat((String)this.getValue(), 0.0F);
	}

	public boolean getValueBool() {
		return StringUtils.tryParseBoolean((String)this.getValue());
	}

	public void setValue(String string) {
		this.trySetValue(string);
	}

	public void setValue(float float1) {
		this.trySetValue(String.valueOf(float1));
	}

	public void setValue(boolean boolean1) {
		this.trySetValue(boolean1 ? "true" : "false");
	}

	public AnimationVariableType getType() {
		return AnimationVariableType.String;
	}

	public boolean canConvertFrom(String string) {
		return true;
	}

	public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet {
	}

	public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet {
	}
}
