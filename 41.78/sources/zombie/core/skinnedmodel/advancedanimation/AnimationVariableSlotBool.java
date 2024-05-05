package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;


public final class AnimationVariableSlotBool extends AnimationVariableSlot {
	private boolean m_value;

	public AnimationVariableSlotBool(String string) {
		super(string);
	}

	public String getValueString() {
		return this.m_value ? "true" : "false";
	}

	public float getValueFloat() {
		return this.m_value ? 1.0F : 0.0F;
	}

	public boolean getValueBool() {
		return this.m_value;
	}

	public void setValue(String string) {
		this.m_value = StringUtils.tryParseBoolean(string);
	}

	public void setValue(float float1) {
		this.m_value = (double)float1 != 0.0;
	}

	public void setValue(boolean boolean1) {
		this.m_value = boolean1;
	}

	public AnimationVariableType getType() {
		return AnimationVariableType.Boolean;
	}

	public boolean canConvertFrom(String string) {
		return StringUtils.isBoolean(string);
	}

	public void clear() {
		this.m_value = false;
	}
}
