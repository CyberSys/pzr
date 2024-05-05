package zombie.core.skinnedmodel.advancedanimation;


public interface IAnimationVariableSlot {

	String getKey();

	String getValueString();

	float getValueFloat();

	boolean getValueBool();

	void setValue(String string);

	void setValue(float float1);

	void setValue(boolean boolean1);

	AnimationVariableType getType();

	boolean canConvertFrom(String string);

	void clear();

	default boolean isReadOnly() {
		return false;
	}
}
