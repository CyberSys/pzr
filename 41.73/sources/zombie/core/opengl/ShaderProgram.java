package zombie.core.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjglx.BufferUtils;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.SystemDisabler;
import zombie.ZomboidFileSystem;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.DebugType;
import zombie.iso.Vector2;
import zombie.iso.Vector3;


public final class ShaderProgram {
	private int m_shaderID = 0;
	private final String m_name;
	private final boolean m_isStatic;
	private final ArrayList m_vertexUnits = new ArrayList();
	private final ArrayList m_fragmentUnits = new ArrayList();
	private final HashMap m_fileWatchers = new HashMap();
	private boolean m_sourceFilesChanged = false;
	private boolean m_compileFailed = false;
	private final HashMap uniformsByName = new HashMap();
	private final ArrayList m_onCompiledListeners = new ArrayList();
	private final int[] m_uvScaleUniforms = new int[10];
	private static FloatBuffer floatBuffer;

	private ShaderProgram(String string, boolean boolean1) {
		this.m_name = string;
		this.m_isStatic = boolean1;
	}

	public String getName() {
		return this.m_name;
	}

	public void addCompileListener(IShaderProgramListener iShaderProgramListener) {
		if (!this.m_onCompiledListeners.contains(iShaderProgramListener)) {
			this.m_onCompiledListeners.add(iShaderProgramListener);
		}
	}

	public void removeCompileListener(IShaderProgramListener iShaderProgramListener) {
		this.m_onCompiledListeners.remove(iShaderProgramListener);
	}

	private void invokeProgramCompiledEvent() {
		this.Start();
		this.m_uvScaleUniforms[0] = ARBShaderObjects.glGetUniformLocationARB(this.m_shaderID, "UVScale");
		for (int int1 = 1; int1 < this.m_uvScaleUniforms.length; ++int1) {
			this.m_uvScaleUniforms[int1] = ARBShaderObjects.glGetUniformLocationARB(this.m_shaderID, "UVScale" + int1);
		}

		this.End();
		if (!this.m_onCompiledListeners.isEmpty()) {
			ArrayList arrayList = new ArrayList(this.m_onCompiledListeners);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				IShaderProgramListener iShaderProgramListener = (IShaderProgramListener)iterator.next();
				iShaderProgramListener.callback(this);
			}
		}
	}

	public void compile() {
		this.m_sourceFilesChanged = false;
		this.m_compileFailed = false;
		if (this.isCompiled()) {
			this.destroy();
		}

		String string = this.getName();
		if (DebugLog.isEnabled(DebugType.Shader)) {
			DebugLog.Shader.debugln(string + (this.m_isStatic ? "(Static)" : ""));
		}

		this.m_shaderID = ARBShaderObjects.glCreateProgramObjectARB();
		if (this.m_shaderID == 0) {
			DebugLog.Shader.error("Failed to create Shader: " + string + " could not create new Shader Program ID.");
		} else {
			this.addShader(this.getRootVertFileName(), ShaderUnit.Type.Vert);
			this.addShader(this.getRootFragFileName(string), ShaderUnit.Type.Frag);
			this.registerFileWatchers();
			if (!this.compileAllShaderUnits()) {
				this.m_compileFailed = true;
				this.destroy();
			} else if (!this.attachAllShaderUnits()) {
				this.m_compileFailed = true;
				this.destroy();
			} else {
				this.registerFileWatchers();
				ARBShaderObjects.glLinkProgramARB(this.m_shaderID);
				if (ARBShaderObjects.glGetObjectParameteriARB(this.m_shaderID, 35714) == 0) {
					this.m_compileFailed = true;
					DebugLog.Shader.error("Failed to link new Shader Program:" + string + " bStatic:" + this.m_isStatic);
					DebugLog.Shader.error(getLogInfo(this.m_shaderID));
					this.destroy();
				} else {
					ARBShaderObjects.glValidateProgramARB(this.m_shaderID);
					if (ARBShaderObjects.glGetObjectParameteriARB(this.m_shaderID, 35715) == 0) {
						this.m_compileFailed = true;
						DebugLog.Shader.error("Failed to validate Shader Program:" + string + " bStatic:" + this.m_isStatic);
						DebugLog.Shader.error(getLogInfo(this.m_shaderID));
						this.destroy();
					} else {
						this.onCompileSuccess();
					}
				}
			}
		}
	}

	private void onCompileSuccess() {
		if (this.isCompiled()) {
			this.uniformsByName.clear();
			this.Start();
			int int1 = this.m_shaderID;
			int int2 = GL20.glGetProgrami(int1, 35718);
			int int3 = 0;
			IntBuffer intBuffer = MemoryUtil.memAllocInt(1);
			IntBuffer intBuffer2 = MemoryUtil.memAllocInt(1);
			for (int int4 = 0; int4 < int2; ++int4) {
				String string = GL20.glGetActiveUniform(int1, int4, 255, intBuffer, intBuffer2);
				int int5 = GL20.glGetUniformLocation(int1, string);
				if (int5 != -1) {
					int int6 = intBuffer.get(0);
					int int7 = intBuffer2.get(0);
					ShaderProgram.Uniform uniform = new ShaderProgram.Uniform();
					this.uniformsByName.put(string, uniform);
					uniform.name = string;
					uniform.loc = int5;
					uniform.size = int6;
					uniform.type = int7;
					if (DebugLog.isEnabled(DebugType.Shader)) {
						DebugLog.Shader.debugln(string + ", Loc: " + int5 + ", Type: " + int7 + ", Size: " + int6);
					}

					if (uniform.type == 35678) {
						if (int3 != 0) {
							GL20.glUniform1i(uniform.loc, int3);
						}

						uniform.sampler = int3++;
					}
				}
			}

			MemoryUtil.memFree(intBuffer);
			MemoryUtil.memFree(intBuffer2);
			this.End();
			PZGLUtil.checkGLError(true);
			this.invokeProgramCompiledEvent();
		}
	}

	private void registerFileWatchers() {
		Iterator iterator = this.m_fileWatchers.values().iterator();
		while (iterator.hasNext()) {
			PredicatedFileWatcher predicatedFileWatcher = (PredicatedFileWatcher)iterator.next();
			DebugFileWatcher.instance.remove(predicatedFileWatcher);
		}

		this.m_fileWatchers.clear();
		iterator = this.m_vertexUnits.iterator();
		ShaderUnit shaderUnit;
		while (iterator.hasNext()) {
			shaderUnit = (ShaderUnit)iterator.next();
			this.registerFileWatcherInternal(shaderUnit.getFileName(), (iteratorx)->{
				this.onShaderFileChanged();
			});
		}

		iterator = this.m_fragmentUnits.iterator();
		while (iterator.hasNext()) {
			shaderUnit = (ShaderUnit)iterator.next();
			this.registerFileWatcherInternal(shaderUnit.getFileName(), (iteratorx)->{
				this.onShaderFileChanged();
			});
		}
	}

	private void registerFileWatcherInternal(String string, PredicatedFileWatcher.IPredicatedFileWatcherCallback iPredicatedFileWatcherCallback) {
		string = ZomboidFileSystem.instance.getString(string);
		PredicatedFileWatcher predicatedFileWatcher = new PredicatedFileWatcher(string, iPredicatedFileWatcherCallback);
		this.m_fileWatchers.put(string, predicatedFileWatcher);
		DebugFileWatcher.instance.add(predicatedFileWatcher);
	}

	private void onShaderFileChanged() {
		this.m_sourceFilesChanged = true;
	}

	private boolean compileAllShaderUnits() {
		Iterator iterator = this.getShaderUnits().iterator();
		ShaderUnit shaderUnit;
		do {
			if (!iterator.hasNext()) {
				return true;
			}

			shaderUnit = (ShaderUnit)iterator.next();
		} while (shaderUnit.compile());

		DebugLogStream debugLogStream = DebugLog.Shader;
		String string = this.getName();
		debugLogStream.error("Failed to create Shader: " + string + " Shader unit failed to compile: " + shaderUnit.getFileName());
		return false;
	}

	private boolean attachAllShaderUnits() {
		Iterator iterator = this.getShaderUnits().iterator();
		ShaderUnit shaderUnit;
		do {
			if (!iterator.hasNext()) {
				return true;
			}

			shaderUnit = (ShaderUnit)iterator.next();
		} while (shaderUnit.attach());

		DebugLogStream debugLogStream = DebugLog.Shader;
		String string = this.getName();
		debugLogStream.error("Failed to create Shader: " + string + " Shader unit failed to attach: " + shaderUnit.getFileName());
		return false;
	}

	private ArrayList getShaderUnits() {
		ArrayList arrayList = new ArrayList();
		arrayList.addAll(this.m_vertexUnits);
		arrayList.addAll(this.m_fragmentUnits);
		return arrayList;
	}

	private String getRootVertFileName() {
		return this.m_isStatic ? "media/shaders/" + this.getName() + "_static.vert" : "media/shaders/" + this.getName() + ".vert";
	}

	private String getRootFragFileName(String string) {
		return "media/shaders/" + string + ".frag";
	}

	public ShaderUnit addShader(String string, ShaderUnit.Type type) {
		ShaderUnit shaderUnit = this.findShader(string, type);
		if (shaderUnit != null) {
			return shaderUnit;
		} else {
			ArrayList arrayList = this.getShaderList(type);
			shaderUnit = new ShaderUnit(this, string, type);
			arrayList.add(shaderUnit);
			return shaderUnit;
		}
	}

	private ArrayList getShaderList(ShaderUnit.Type type) {
		return type == ShaderUnit.Type.Vert ? this.m_vertexUnits : this.m_fragmentUnits;
	}

	private ShaderUnit findShader(String string, ShaderUnit.Type type) {
		ArrayList arrayList = this.getShaderList(type);
		ShaderUnit shaderUnit = null;
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			ShaderUnit shaderUnit2 = (ShaderUnit)iterator.next();
			if (shaderUnit2.getFileName().equals(string)) {
				shaderUnit = shaderUnit2;
				break;
			}
		}

		return shaderUnit;
	}

	public static ShaderProgram createShaderProgram(String string, boolean boolean1, boolean boolean2) {
		ShaderProgram shaderProgram = new ShaderProgram(string, boolean1);
		if (boolean2) {
			shaderProgram.compile();
		}

		return shaderProgram;
	}

	@Deprecated
	public static int createVertShader(String string) {
		ShaderUnit shaderUnit = new ShaderUnit((ShaderProgram)null, string, ShaderUnit.Type.Vert);
		shaderUnit.compile();
		return shaderUnit.getGLID();
	}

	@Deprecated
	public static int createFragShader(String string) {
		ShaderUnit shaderUnit = new ShaderUnit((ShaderProgram)null, string, ShaderUnit.Type.Frag);
		shaderUnit.compile();
		return shaderUnit.getGLID();
	}

	public static void printLogInfo(int int1) {
		IntBuffer intBuffer = MemoryUtil.memAllocInt(1);
		ARBShaderObjects.glGetObjectParameterivARB(int1, 35716, intBuffer);
		int int2 = intBuffer.get();
		MemoryUtil.memFree(intBuffer);
		if (int2 > 1) {
			ByteBuffer byteBuffer = MemoryUtil.memAlloc(int2);
			intBuffer.flip();
			ARBShaderObjects.glGetInfoLogARB(int1, intBuffer, byteBuffer);
			byte[] byteArray = new byte[int2];
			byteBuffer.get(byteArray);
			String string = new String(byteArray);
			DebugLog.Shader.debugln(":\n" + string);
			MemoryUtil.memFree(byteBuffer);
		}
	}

	public static String getLogInfo(int int1) {
		return ARBShaderObjects.glGetInfoLogARB(int1, ARBShaderObjects.glGetObjectParameteriARB(int1, 35716));
	}

	public boolean isCompiled() {
		return this.m_shaderID != 0;
	}

	public void destroy() {
		if (this.m_shaderID == 0) {
			this.m_vertexUnits.clear();
			this.m_fragmentUnits.clear();
		} else {
			try {
				DebugLog.Shader.debugln(this.getName());
				Iterator iterator = this.m_vertexUnits.iterator();
				ShaderUnit shaderUnit;
				while (iterator.hasNext()) {
					shaderUnit = (ShaderUnit)iterator.next();
					shaderUnit.destroy();
				}

				this.m_vertexUnits.clear();
				iterator = this.m_fragmentUnits.iterator();
				while (iterator.hasNext()) {
					shaderUnit = (ShaderUnit)iterator.next();
					shaderUnit.destroy();
				}

				this.m_fragmentUnits.clear();
				ARBShaderObjects.glDeleteObjectARB(this.m_shaderID);
				PZGLUtil.checkGLError(true);
			} finally {
				this.m_vertexUnits.clear();
				this.m_fragmentUnits.clear();
				this.m_shaderID = 0;
			}
		}
	}

	public int getShaderID() {
		if (!this.m_compileFailed && !this.isCompiled() || this.m_sourceFilesChanged) {
			RenderThread.invokeOnRenderContext(this::compile);
		}

		return this.m_shaderID;
	}

	public void Start() {
		ARBShaderObjects.glUseProgramObjectARB(this.getShaderID());
	}

	public void End() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}

	public void setSamplerUnit(String string, int int1) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 35678);
		if (uniform != null) {
			uniform.sampler = int1;
			ARBShaderObjects.glUniform1iARB(uniform.loc, int1);
		}
	}

	public void setValueColor(String string, int int1) {
		this.setVector4(string, 0.003921569F * (float)(int1 >> 24 & 255), 0.003921569F * (float)(int1 >> 16 & 255), 0.003921569F * (float)(int1 >> 8 & 255), 0.003921569F * (float)(int1 & 255));
	}

	public void setValueColorRGB(String string, int int1) {
		this.setValueColor(string, int1 & 255);
	}

	public void setValue(String string, float float1) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 5126);
		if (uniform != null) {
			ARBShaderObjects.glUniform1fARB(uniform.loc, float1);
		}
	}

	public void setValue(String string, int int1) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 5124);
		if (uniform != null) {
			ARBShaderObjects.glUniform1iARB(uniform.loc, int1);
		}
	}

	public void setValue(String string, Vector3 vector3) {
		this.setVector3(string, vector3.x, vector3.y, vector3.z);
	}

	public void setValue(String string, Vector2 vector2) {
		this.setVector2(string, vector2.x, vector2.y);
	}

	public void setVector2(String string, float float1, float float2) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 35664);
		if (uniform != null) {
			this.setVector2(uniform.loc, float1, float2);
		}
	}

	public void setVector3(String string, float float1, float float2, float float3) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 35665);
		if (uniform != null) {
			this.setVector3(uniform.loc, float1, float2, float3);
		}
	}

	public void setVector4(String string, float float1, float float2, float float3, float float4) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 35666);
		if (uniform != null) {
			this.setVector4(uniform.loc, float1, float2, float3, float4);
		}
	}

	public final ShaderProgram.Uniform getUniform(String string, int int1) {
		return this.getUniform(string, int1, false);
	}

	public ShaderProgram.Uniform getUniform(String string, int int1, boolean boolean1) {
		ShaderProgram.Uniform uniform = (ShaderProgram.Uniform)this.uniformsByName.get(string);
		if (uniform == null) {
			if (boolean1) {
				DebugLog.Shader.warn(string + " doesn\'t exist in shader");
			}

			return null;
		} else if (uniform.type != int1) {
			DebugLog.Shader.warn(string + " isn\'t of type: " + int1 + ", it is of type: " + uniform.type);
			return null;
		} else {
			return uniform;
		}
	}

	public void setValue(String string, Matrix4f matrix4f) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 35676);
		if (uniform != null) {
			this.setTransformMatrix(uniform.loc, matrix4f);
		}
	}

	public void setValue(String string, Texture texture, int int1) {
		ShaderProgram.Uniform uniform = this.getUniform(string, 35678);
		if (uniform != null && texture != null) {
			if (uniform.sampler != int1) {
				uniform.sampler = int1;
				GL20.glUniform1i(uniform.loc, uniform.sampler);
			}

			GL13.glActiveTexture('蓀' + uniform.sampler);
			GL11.glEnable(3553);
			int int2 = Texture.lastTextureID;
			texture.bind();
			if (uniform.sampler > 0) {
				Texture.lastTextureID = int2;
			}

			Vector2 vector2 = texture.getUVScale(ShaderProgram.L_setValue.vector2);
			this.setUVScale(int1, vector2.x, vector2.y);
			if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
				PZGLUtil.checkGLErrorThrow("Shader.setValue<Texture> Loc: %s, Tex: %s, samplerUnit: %d", string, texture, int1);
			}
		}
	}

	private void setUVScale(int int1, float float1, float float2) {
		if (int1 < 0) {
			DebugLog.Shader.error("SamplerUnit out of range: " + int1);
		} else if (int1 >= this.m_uvScaleUniforms.length) {
			String string = "UVScale";
			if (int1 > 0) {
				string = "UVScale" + int1;
			}

			this.setVector2(string, float1, float2);
		} else {
			int int2 = this.m_uvScaleUniforms[int1];
			if (int2 >= 0) {
				this.setVector2(int2, float1, float2);
			}
		}
	}

	public void setVector2(int int1, float float1, float float2) {
		ARBShaderObjects.glUniform2fARB(int1, float1, float2);
	}

	public void setVector3(int int1, float float1, float float2, float float3) {
		ARBShaderObjects.glUniform3fARB(int1, float1, float2, float3);
	}

	public void setVector4(int int1, float float1, float float2, float float3, float float4) {
		ARBShaderObjects.glUniform4fARB(int1, float1, float2, float3, float4);
	}

	void setTransformMatrix(int int1, Matrix4f matrix4f) {
		if (floatBuffer == null) {
			floatBuffer = BufferUtils.createFloatBuffer(38400);
		}

		floatBuffer.clear();
		matrix4f.store(floatBuffer);
		floatBuffer.flip();
		ARBShaderObjects.glUniformMatrix4fvARB(int1, true, floatBuffer);
	}

	public static class Uniform {
		public String name;
		public int size;
		public int loc;
		public int type;
		public int sampler;
	}

	private static final class L_setValue {
		static final Vector2 vector2 = new Vector2();
	}
}
