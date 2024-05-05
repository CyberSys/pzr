package zombie.core.skinnedmodel.advancedanimation;



public enum BlendType {

	Linear,
	InverseExponential,
	Type;

	private static BlendType[] $values() {
		return new BlendType[]{Linear, InverseExponential, Type};
	}
}
