package zombie.core.textures;


public final class TextureCombinerShaderParam {
	public String name;
	public float min;
	public float max;

	public TextureCombinerShaderParam(String string, float float1, float float2) {
		this.name = string;
		this.min = float1;
		this.max = float2;
	}

	public TextureCombinerShaderParam(String string, float float1) {
		this.name = string;
		this.min = float1;
		this.max = float1;
	}
}
