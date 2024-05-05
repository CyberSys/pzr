package zombie;

import java.util.Stack;
import org.lwjgl.opengl.GL11;
import zombie.core.SpriteRenderer;
import zombie.core.math.Vector4;
import zombie.core.opengl.GLState;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;


public final class IndieGL {
	public static int nCount = 0;
	private static final GLState.CIntValue tempInt = new GLState.CIntValue();
	private static final GLState.C2IntsValue temp2Ints = new GLState.C2IntsValue();
	private static final GLState.C3IntsValue temp3Ints = new GLState.C3IntsValue();
	private static final GLState.C4IntsValue temp4Ints = new GLState.C4IntsValue();
	private static final GLState.C4BooleansValue temp4Booleans = new GLState.C4BooleansValue();
	private static final GLState.CIntFloatValue tempIntFloat = new GLState.CIntFloatValue();
	private static final Stack m_shaderStack = new Stack();

	public static void glBlendFunc(int int1, int int2) {
		if (SpriteRenderer.instance != null && SpriteRenderer.GL_BLENDFUNC_ENABLED) {
			GLState.BlendFuncSeparate.set(temp4Ints.set(int1, int2, int1, int2));
		}
	}

	public static void glBlendFuncSeparate(int int1, int int2, int int3, int int4) {
		if (SpriteRenderer.instance != null && SpriteRenderer.GL_BLENDFUNC_ENABLED) {
			GLState.BlendFuncSeparate.set(temp4Ints.set(int1, int2, int3, int4));
		}
	}

	public static void StartShader(Shader shader) {
		int int1 = IsoCamera.frameState.playerIndex;
		StartShader(shader, int1);
	}

	public static void StartShader(Shader shader, int int1) {
		if (shader != null) {
			StartShader(shader.getID(), int1);
		} else {
			EndShader();
		}
	}

	public static void StartShader(int int1) {
		int int2 = IsoCamera.frameState.playerIndex;
		StartShader(int1, int2);
	}

	public static void StartShader(int int1, int int2) {
		SpriteRenderer.instance.StartShader(int1, int2);
	}

	public static void EndShader() {
		SpriteRenderer.instance.EndShader();
	}

	public static void pushShader(Shader shader) {
		int int1 = IsoCamera.frameState.playerIndex;
		m_shaderStack.push(ShaderStackEntry.alloc(shader, int1));
		StartShader(shader, int1);
	}

	public static void popShader(Shader shader) {
		if (m_shaderStack.isEmpty()) {
			throw new RuntimeException("Push/PopShader mismatch. Cannot pop. Stack is empty.");
		} else if (((ShaderStackEntry)m_shaderStack.peek()).getShader() != shader) {
			throw new RuntimeException("Push/PopShader mismatch. The popped shader != the pushed shader.");
		} else {
			ShaderStackEntry shaderStackEntry = (ShaderStackEntry)m_shaderStack.pop();
			shaderStackEntry.release();
			if (m_shaderStack.isEmpty()) {
				EndShader();
			} else {
				ShaderStackEntry shaderStackEntry2 = (ShaderStackEntry)m_shaderStack.peek();
				StartShader(shaderStackEntry2.getShader(), shaderStackEntry2.getPlayerIndex());
			}
		}
	}

	public static void bindShader(Shader shader, Runnable runnable) {
		pushShader(shader);
		try {
			runnable.run();
		} finally {
			popShader(shader);
		}
	}

	public static void bindShader(Shader shader, Object object, Invokers.Params1.ICallback iCallback) {
		Lambda.capture(shader, object, iCallback, (shaderx,objectx,iCallbackx,var3)->{
			bindShader(objectx, shaderx.invoker(iCallbackx, var3));
		});
	}

	public static void bindShader(Shader shader, Object object, Object object2, Invokers.Params2.ICallback iCallback) {
		Lambda.capture(shader, object, object2, iCallback, (shaderx,objectx,object2x,iCallbackx,var4)->{
			bindShader(objectx, shaderx.invoker(object2x, iCallbackx, var4));
		});
	}

	public static void bindShader(Shader shader, Object object, Object object2, Object object3, Invokers.Params3.ICallback iCallback) {
		Lambda.capture(shader, object, object2, object3, iCallback, (shaderx,objectx,object2x,object3x,iCallbackx,var5)->{
			bindShader(objectx, shaderx.invoker(object2x, object3x, iCallbackx, var5));
		});
	}

	public static void bindShader(Shader shader, Object object, Object object2, Object object3, Object object4, Invokers.Params4.ICallback iCallback) {
		Lambda.capture(shader, object, object2, object3, object4, iCallback, (shaderx,objectx,object2x,object3x,object4x,iCallbackx,var6)->{
			bindShader(objectx, shaderx.invoker(object2x, object3x, object4x, iCallbackx, var6));
		});
	}

	private static ShaderProgram.Uniform getShaderUniform(Shader shader, String string, int int1) {
		if (shader == null) {
			return null;
		} else {
			ShaderProgram shaderProgram = shader.getProgram();
			if (shaderProgram == null) {
				return null;
			} else {
				ShaderProgram.Uniform uniform = shaderProgram.getUniform(string, int1, false);
				return uniform;
			}
		}
	}

	public static void shaderSetSamplerUnit(Shader shader, String string, int int1) {
		ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35678);
		if (uniform != null) {
			uniform.sampler = int1;
			ShaderUpdate1i(shader.getID(), uniform.loc, int1);
		}
	}

	public static void shaderSetValue(Shader shader, String string, float float1) {
		ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 5126);
		if (uniform != null) {
			ShaderUpdate1f(shader.getID(), uniform.loc, float1);
		}
	}

	public static void shaderSetValue(Shader shader, String string, int int1) {
		ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 5124);
		if (uniform != null) {
			ShaderUpdate1i(shader.getID(), uniform.loc, int1);
		}
	}

	public static void shaderSetValue(Shader shader, String string, Vector2 vector2) {
		shaderSetVector2(shader, string, vector2.x, vector2.y);
	}

	public static void shaderSetValue(Shader shader, String string, Vector3 vector3) {
		shaderSetVector3(shader, string, vector3.x, vector3.y, vector3.z);
	}

	public static void shaderSetValue(Shader shader, String string, Vector4 vector4) {
		shaderSetVector4(shader, string, vector4.x, vector4.y, vector4.z, vector4.w);
	}

	public static void shaderSetVector2(Shader shader, String string, float float1, float float2) {
		ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35664);
		if (uniform != null) {
			ShaderUpdate2f(shader.getID(), uniform.loc, float1, float2);
		}
	}

	public static void shaderSetVector3(Shader shader, String string, float float1, float float2, float float3) {
		ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35665);
		if (uniform != null) {
			ShaderUpdate3f(shader.getID(), uniform.loc, float1, float2, float3);
		}
	}

	public static void shaderSetVector4(Shader shader, String string, float float1, float float2, float float3, float float4) {
		ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35666);
		if (uniform != null) {
			ShaderUpdate4f(shader.getID(), uniform.loc, float1, float2, float3, float4);
		}
	}

	public static void ShaderUpdate1i(int int1, int int2, int int3) {
		SpriteRenderer.instance.ShaderUpdate1i(int1, int2, int3);
	}

	public static void ShaderUpdate1f(int int1, int int2, float float1) {
		SpriteRenderer.instance.ShaderUpdate1f(int1, int2, float1);
	}

	public static void ShaderUpdate2f(int int1, int int2, float float1, float float2) {
		SpriteRenderer.instance.ShaderUpdate2f(int1, int2, float1, float2);
	}

	public static void ShaderUpdate3f(int int1, int int2, float float1, float float2, float float3) {
		SpriteRenderer.instance.ShaderUpdate3f(int1, int2, float1, float2, float3);
	}

	public static void ShaderUpdate4f(int int1, int int2, float float1, float float2, float float3, float float4) {
		SpriteRenderer.instance.ShaderUpdate4f(int1, int2, float1, float2, float3, float4);
	}

	public static void glBlendFuncA(int int1, int int2) {
		GL11.glBlendFunc(int1, int2);
	}

	public static void glEnable(int int1) {
		SpriteRenderer.instance.glEnable(int1);
	}

	public static void glDoStartFrame(int int1, int int2, float float1, int int3) {
		glDoStartFrame(int1, int2, float1, int3, false);
	}

	public static void glDoStartFrame(int int1, int int2, float float1, int int3, boolean boolean1) {
		SpriteRenderer.instance.glDoStartFrame(int1, int2, float1, int3, boolean1);
	}

	public static void glDoEndFrame() {
		SpriteRenderer.instance.glDoEndFrame();
	}

	public static void glColorMask(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
		GLState.ColorMask.set(temp4Booleans.set(boolean1, boolean2, boolean3, boolean4));
	}

	public static void glColorMaskA(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
		GL11.glColorMask(boolean1, boolean1, boolean4, boolean4);
	}

	public static void glEnableA(int int1) {
		GL11.glEnable(int1);
	}

	public static void glAlphaFunc(int int1, float float1) {
		GLState.AlphaFunc.set(tempIntFloat.set(int1, float1));
	}

	public static void glAlphaFuncA(int int1, float float1) {
		GL11.glAlphaFunc(int1, float1);
	}

	public static void glStencilFunc(int int1, int int2, int int3) {
		GLState.StencilFunc.set(temp3Ints.set(int1, int2, int3));
	}

	public static void glStencilFuncA(int int1, int int2, int int3) {
		GL11.glStencilFunc(int1, int2, int3);
	}

	public static void glStencilOp(int int1, int int2, int int3) {
		GLState.StencilOp.set(temp3Ints.set(int1, int2, int3));
	}

	public static void glStencilOpA(int int1, int int2, int int3) {
		GL11.glStencilOp(int1, int2, int3);
	}

	public static void glTexParameteri(int int1, int int2, int int3) {
		SpriteRenderer.instance.glTexParameteri(int1, int2, int3);
	}

	public static void glTexParameteriActual(int int1, int int2, int int3) {
		GL11.glTexParameteri(int1, int2, int3);
	}

	public static void glStencilMask(int int1) {
		GLState.StencilMask.set(tempInt.set(int1));
	}

	public static void glStencilMaskA(int int1) {
		GL11.glStencilMask(int1);
	}

	public static void glDisable(int int1) {
		SpriteRenderer.instance.glDisable(int1);
	}

	public static void glClear(int int1) {
		SpriteRenderer.instance.glClear(int1);
	}

	public static void glClearA(int int1) {
		GL11.glClear(int1);
	}

	public static void glDisableA(int int1) {
		GL11.glDisable(int1);
	}

	public static void glLoadIdentity() {
		SpriteRenderer.instance.glLoadIdentity();
	}

	public static void glBind(Texture texture) {
		SpriteRenderer.instance.glBind(texture.getID());
	}

	public static void enableAlphaTest() {
		GLState.AlphaTest.set(GLState.CBooleanValue.TRUE);
	}

	public static void disableAlphaTest() {
		GLState.AlphaTest.set(GLState.CBooleanValue.FALSE);
	}

	public static void enableStencilTest() {
		GLState.StencilTest.set(GLState.CBooleanValue.TRUE);
	}

	public static void disableStencilTest() {
		GLState.StencilTest.set(GLState.CBooleanValue.FALSE);
	}

	public static boolean isMaxZoomLevel() {
		return SpriteRenderer.instance.isMaxZoomLevel();
	}

	public static boolean isMinZoomLevel() {
		return SpriteRenderer.instance.isMinZoomLevel();
	}
}
