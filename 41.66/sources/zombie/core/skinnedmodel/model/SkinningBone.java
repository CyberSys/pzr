package zombie.core.skinnedmodel.model;

import java.util.function.Consumer;


public final class SkinningBone {
	public SkinningBone Parent;
	public String Name;
	public int Index;
	public SkinningBone[] Children;

	public void forEachDescendant(Consumer consumer) {
		forEachDescendant(this, consumer);
	}

	private static void forEachDescendant(SkinningBone skinningBone, Consumer consumer) {
		if (skinningBone.Children != null && skinningBone.Children.length != 0) {
			SkinningBone[] skinningBoneArray = skinningBone.Children;
			int int1 = skinningBoneArray.length;
			int int2;
			SkinningBone skinningBone2;
			for (int2 = 0; int2 < int1; ++int2) {
				skinningBone2 = skinningBoneArray[int2];
				consumer.accept(skinningBone2);
			}

			skinningBoneArray = skinningBone.Children;
			int1 = skinningBoneArray.length;
			for (int2 = 0; int2 < int1; ++int2) {
				skinningBone2 = skinningBoneArray[int2];
				forEachDescendant(skinningBone2, consumer);
			}
		}
	}

	public String toString() {
		String string = System.lineSeparator();
		return this.getClass().getName() + string + "{" + string + "\tName:\"" + this.Name + "\"" + string + "\tIndex:" + this.Index + string + "}";
	}
}
