package zombie.core.skinnedmodel.advancedanimation;


public interface IAnimationVariableMap extends IAnimationVariableSource {

	IAnimationVariableSlot getOrCreateVariable(String string);

	void setVariable(IAnimationVariableSlot iAnimationVariableSlot);

	void setVariable(String string, String string2);

	void setVariable(String string, boolean boolean1);

	void setVariable(String string, float float1);

	void clearVariable(String string);

	void clearVariables();
}
