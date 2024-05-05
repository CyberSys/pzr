package zombie.core.skinnedmodel.advancedanimation;



public enum AnimationVariableType {

	Void,
	String,
	Float,
	Boolean;

	private static AnimationVariableType[] $values() {
		return new AnimationVariableType[]{Void, String, Float, Boolean};
	}
}
