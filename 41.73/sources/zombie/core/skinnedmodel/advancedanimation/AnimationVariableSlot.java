package zombie.core.skinnedmodel.advancedanimation;


public abstract class AnimationVariableSlot implements IAnimationVariableSlot {
	private final String m_key;

	protected AnimationVariableSlot(String string) {
		this.m_key = string.toLowerCase().trim();
	}

	public String getKey() {
		return this.m_key;
	}
}
