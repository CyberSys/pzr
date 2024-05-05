package zombie;

import zombie.core.opengl.Shader;
import zombie.util.Pool;
import zombie.util.PooledObject;


public final class ShaderStackEntry extends PooledObject {
	private Shader m_shader;
	private int m_playerIndex;
	private static final Pool s_pool = new Pool(ShaderStackEntry::new);

	public Shader getShader() {
		return this.m_shader;
	}

	public int getPlayerIndex() {
		return this.m_playerIndex;
	}

	public static ShaderStackEntry alloc(Shader shader, int int1) {
		ShaderStackEntry shaderStackEntry = (ShaderStackEntry)s_pool.alloc();
		shaderStackEntry.m_shader = shader;
		shaderStackEntry.m_playerIndex = int1;
		return shaderStackEntry;
	}
}
