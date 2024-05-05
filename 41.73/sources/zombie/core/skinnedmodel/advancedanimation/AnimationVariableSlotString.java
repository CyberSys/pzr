package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;
import zombie.util.StringUtils;


public final class AnimationVariableSlotString extends AnimationVariableSlot {
	private String m_value;

	public AnimationVariableSlotString(String string) {
		super(string);
	}

	public String getValueString() {
		return this.m_value;
	}

	public float getValueFloat() {
		return PZMath.tryParseFloat(this.m_value, 0.0F);
	}

	public boolean getValueBool() {
		return StringUtils.tryParseBoolean(this.m_value);
	}

	public void setValue(String string) {
		this.m_value = string;
	}

	public void setValue(float float1) {
		this.m_value = String.valueOf(float1);
	}

	public void setValue(boolean boolean1) {
		this.m_value = boolean1 ? "true" : "false";
	}

	public AnimationVariableType getType() {
		return AnimationVariableType.String;
	}

	public boolean canConvertFrom(String string) {
		return true;
	}

	public void clear() {
		this.m_value = "";
	}
}
