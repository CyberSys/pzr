package zombie.iso.objects;



public enum RenderEffectType {

	Hit_Tree_Shudder,
	Vegetation_Rustle,
	Hit_Door;

	private static RenderEffectType[] $values() {
		return new RenderEffectType[]{Hit_Tree_Shudder, Vegetation_Rustle, Hit_Door};
	}
}
