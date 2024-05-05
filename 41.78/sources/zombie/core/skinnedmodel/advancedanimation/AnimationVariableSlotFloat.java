package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;


public final class AnimationVariableSlotFloat extends AnimationVariableSlot {
	private float m_value = 0.0F;

	public AnimationVariableSlotFloat(String string) {
		super(string);
	}

	public String getValueString() {
		return String.valueOf(this.m_value);
	}

	public float getValueFloat() {
		return this.m_value;
	}

	public boolean getValueBool() {
		return this.m_value != 0.0F;
	}

	public void setValue(String string) {
		this.m_value = PZMath.tryParseFloat(string, 0.0F);
	}

	public void setValue(float float1) {
		this.m_value = float1;
	}

	public void setValue(boolean boolean1) {
		this.m_value = boolean1 ? 1.0F : 0.0F;
	}

	public AnimationVariableType getType() {
		return AnimationVariableType.Float;
	}

	public boolean canConvertFrom(String string) {
		return PZMath.canParseFloat(string);
	}

	public void clear() {
		this.m_value = 0.0F;
	}
}
