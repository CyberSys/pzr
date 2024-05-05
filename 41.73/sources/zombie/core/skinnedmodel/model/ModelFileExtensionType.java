package zombie.core.skinnedmodel.model;



public enum ModelFileExtensionType {

	None,
	X,
	Fbx,
	Txt;

	private static ModelFileExtensionType[] $values() {
		return new ModelFileExtensionType[]{None, X, Fbx, Txt};
	}
}
