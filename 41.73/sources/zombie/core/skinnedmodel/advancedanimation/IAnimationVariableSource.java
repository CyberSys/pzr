package zombie.core.skinnedmodel.advancedanimation;


public interface IAnimationVariableSource {

	IAnimationVariableSlot getVariable(AnimationVariableHandle animationVariableHandle);

	IAnimationVariableSlot getVariable(String string);

	String getVariableString(String string);

	float getVariableFloat(String string, float float1);

	boolean getVariableBoolean(String string);

	boolean getVariableBoolean(String string, boolean boolean1);

	Iterable getGameVariables();

	boolean isVariable(String string, String string2);

	boolean containsVariable(String string);
}
