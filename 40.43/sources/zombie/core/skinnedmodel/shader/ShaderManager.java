package zombie.core.skinnedmodel.shader;

import java.util.ArrayList;


public class ShaderManager {
	public static ShaderManager instance = new ShaderManager();
	private ArrayList shaders = new ArrayList();

	public Shader getShader(String string, boolean boolean1) {
		for (int int1 = 0; int1 < this.shaders.size(); ++int1) {
			Shader shader = (Shader)this.shaders.get(int1);
			if (string.equals(shader.name) && boolean1 == shader.bStatic) {
				return shader;
			}
		}

		return null;
	}

	public Shader getOrCreateShader(String string, boolean boolean1) {
		Shader shader = this.getShader(string, boolean1);
		if (shader != null) {
			return shader;
		} else {
			shader = new Shader(string, boolean1);
			this.shaders.add(shader);
			return shader;
		}
	}
}
