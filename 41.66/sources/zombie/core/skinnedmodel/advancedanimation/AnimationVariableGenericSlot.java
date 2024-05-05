package zombie.core.skinnedmodel.advancedanimation;

import zombie.debug.DebugLog;


public final class AnimationVariableGenericSlot extends AnimationVariableSlot {
	private AnimationVariableType m_type;
	private IAnimationVariableSlot m_valueSlot;

	public AnimationVariableGenericSlot(String string) {
		super(string);
		this.m_type = AnimationVariableType.Void;
	}

	public String getValueString() {
		return this.m_valueSlot != null ? this.m_valueSlot.getValueString() : null;
	}

	public float getValueFloat() {
		return this.m_valueSlot != null ? this.m_valueSlot.getValueFloat() : 0.0F;
	}

	public boolean getValueBool() {
		return this.m_valueSlot != null && this.m_valueSlot.getValueBool();
	}

	public void setValue(String string) {
		if (this.m_valueSlot == null || !this.m_valueSlot.canConvertFrom(string)) {
			this.m_valueSlot = new AnimationVariableSlotString(this.getKey());
			this.setType(this.m_valueSlot.getType());
		}

		this.m_valueSlot.setValue(string);
	}

	public void setValue(float float1) {
		if (this.m_valueSlot == null || this.m_type != AnimationVariableType.Float) {
			this.m_valueSlot = new AnimationVariableSlotFloat(this.getKey());
			this.setType(this.m_valueSlot.getType());
		}

		this.m_valueSlot.setValue(float1);
	}

	public void setValue(boolean boolean1) {
		if (this.m_valueSlot == null || this.m_type != AnimationVariableType.Boolean) {
			this.m_valueSlot = new AnimationVariableSlotBool(this.getKey());
			this.setType(this.m_valueSlot.getType());
		}

		this.m_valueSlot.setValue(boolean1);
	}

	public AnimationVariableType getType() {
		return this.m_type;
	}

	private void setType(AnimationVariableType animationVariableType) {
		if (this.m_type != animationVariableType) {
			if (this.m_type != AnimationVariableType.Void) {
				DebugLog.General.printf("Variable %s converting from %s to %s\n", this.getKey(), this.m_type, animationVariableType);
			}

			this.m_type = animationVariableType;
		}
	}

	public boolean canConvertFrom(String string) {
		return true;
	}

	public void clear() {
		this.m_type = AnimationVariableType.Void;
		this.m_valueSlot = null;
	}
}
