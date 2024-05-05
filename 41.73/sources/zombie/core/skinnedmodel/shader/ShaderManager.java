package zombie.core.skinnedmodel.shader;

import java.util.ArrayList;


public final class ShaderManager {
	public static final ShaderManager instance = new ShaderManager();
	private final ArrayList shaders = new ArrayList();

	private Shader getShader(String string, boolean boolean1) {
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
			for (int int1 = 0; int1 < this.shaders.size(); ++int1) {
				Shader shader2 = (Shader)this.shaders.get(int1);
				if (shader2.name.equalsIgnoreCase(string) && !shader2.name.equals(string)) {
					throw new IllegalArgumentException("shader filenames are case-sensitive");
				}
			}

			shader = new Shader(string, boolean1);
			this.shaders.add(shader);
			return shader;
		}
	}
}
