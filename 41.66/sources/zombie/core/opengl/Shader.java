package zombie.core.opengl;

import java.util.HashMap;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;


public class Shader implements IShaderProgramListener {
	public static HashMap ShaderMap = new HashMap();
	public String name;
	private int m_shaderMapID = 0;
	private final ShaderProgram m_shaderProgram;
	public Texture tex;
	public int width;
	public int height;

	public Shader(String string) {
		this.name = string;
		this.m_shaderProgram = ShaderProgram.createShaderProgram(string, false, false);
		this.m_shaderProgram.addCompileListener(this);
		this.m_shaderProgram.compile();
	}

	public void setTexture(Texture texture) {
		this.tex = texture;
	}

	public int getID() {
		return this.m_shaderProgram.getShaderID();
	}

	public void Start() {
		ARBShaderObjects.glUseProgramObjectARB(this.m_shaderProgram.getShaderID());
	}

	public void End() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}

	public void destroy() {
		this.m_shaderProgram.destroy();
		ShaderMap.remove(this.m_shaderMapID);
		this.m_shaderMapID = 0;
	}

	public void startMainThread(TextureDraw textureDraw, int int1) {
	}

	public void startRenderThread(TextureDraw textureDraw) {
	}

	public void postRender(TextureDraw textureDraw) {
	}

	public boolean isCompiled() {
		return this.m_shaderProgram.isCompiled();
	}

	public void callback(ShaderProgram shaderProgram) {
		ShaderMap.remove(this.m_shaderMapID);
		this.m_shaderMapID = shaderProgram.getShaderID();
		ShaderMap.put(this.m_shaderMapID, this);
		this.onCompileSuccess(shaderProgram);
	}

	protected void onCompileSuccess(ShaderProgram shaderProgram) {
	}

	public ShaderProgram getProgram() {
		return this.m_shaderProgram;
	}
}
