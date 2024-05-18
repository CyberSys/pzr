package zombie;

import org.lwjgl.opengl.GL11;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;


public class IndieGL {
	public static int nCount = 0;
	static int glBlendFuncA = -12345;
	static int glBlendFuncB = -12345;
	static int glStencilFuncA = -12345;
	static int glStencilFuncB = -12345;
	static int glStencilFuncC = -12345;
	static int glStencilOpA = -12345;
	static int glStencilOpB = -12345;
	static int glStencilOpC = -12345;
	private static boolean glColorMaskB0 = true;
	private static boolean glColorMaskB1 = true;
	private static boolean glColorMaskB2 = true;
	private static boolean glColorMaskB3 = true;
	static int glAlphaFuncAA = -1;
	static float glAlphaFuncBB = -1.0F;
	private static boolean ALPHA_TEST = false;
	private static boolean STENCIL_TEST = false;

	public static void glBlendFunc(int int1, int int2) {
		if (SpriteRenderer.instance != null) {
			SpriteRenderer.instance.glBlendFunc(int1, int2);
		}
	}

	public static void StartShader(int int1, int int2) {
		SpriteRenderer.instance.StartShader(int1, int2);
	}

	public static void glBlendFuncA(int int1, int int2) {
		glBlendFuncA = int1;
		glBlendFuncB = int2;
		GL11.glBlendFunc(int1, int2);
	}

	public static void glEnable(int int1) {
		SpriteRenderer.instance.glEnable(int1);
	}

	public static void glDoStartFrame(int int1, int int2, int int3) {
		glDoStartFrame(int1, int2, int3, false);
	}

	public static void glDoStartFrame(int int1, int int2, int int3, boolean boolean1) {
		SpriteRenderer.instance.glDoStartFrame(int1, int2, int3, boolean1);
	}

	public static void glDoEndFrame() {
		SpriteRenderer.instance.glDoEndFrame();
	}

	public static void glColorMask(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
		if (boolean1 != glColorMaskB0 || boolean2 != glColorMaskB1 || boolean3 != glColorMaskB2 || boolean4 != glColorMaskB3) {
			glColorMaskB0 = boolean1;
			glColorMaskB1 = boolean2;
			glColorMaskB2 = boolean3;
			glColorMaskB3 = boolean4;
			SpriteRenderer.instance.glColorMask(boolean1 ? 1 : 0, boolean2 ? 1 : 0, boolean3 ? 1 : 0, boolean4 ? 1 : 0);
		}
	}

	public static void glColorMaskA(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
		GL11.glColorMask(boolean1, boolean1, boolean4, boolean4);
	}

	public static void glEnableA(int int1) {
		GL11.glEnable(int1);
	}

	public static void glAlphaFunc(int int1, float float1) {
		if (int1 != glAlphaFuncAA || float1 != glAlphaFuncBB) {
			SpriteRenderer.instance.glAlphaFunc(int1, float1);
			glAlphaFuncAA = int1;
			glAlphaFuncBB = float1;
		}
	}

	public static void glAlphaFuncA(int int1, float float1) {
		GL11.glAlphaFunc(int1, float1);
	}

	public static void glStencilFunc(int int1, int int2, int int3) {
		if (int1 != glStencilFuncA || int2 != glStencilFuncB || int3 != glStencilFuncC) {
			SpriteRenderer.instance.glStencilFunc(int1, int2, int3);
			glStencilFuncA = int1;
			glStencilFuncB = int2;
			glStencilFuncC = int3;
		}
	}

	public static void glStencilFuncA(int int1, int int2, int int3) {
		GL11.glStencilFunc(int1, int2, int3);
	}

	public static void glStencilOp(int int1, int int2, int int3) {
		if (int1 != glStencilOpA || int2 != glStencilOpB || int3 != glStencilOpC) {
			SpriteRenderer.instance.glStencilOp(int1, int2, int3);
			glStencilOpA = int1;
			glStencilOpB = int2;
			glStencilOpC = int3;
		}
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
		SpriteRenderer.instance.glStencilMask(int1);
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

	public static void End() {
	}

	public static void Begin() {
	}

	public static void BeginLine() {
	}

	public static void glLoadIdentity() {
		SpriteRenderer.instance.glLoadIdentity();
	}

	public static void glGenerateMipMaps(int int1) {
		SpriteRenderer.instance.glGenerateMipMaps(int1);
	}

	public static void glBind(Texture texture) {
		SpriteRenderer.instance.glBind(texture.getID());
	}

	public static void enableAlphaTest() {
		if (!ALPHA_TEST) {
			ALPHA_TEST = true;
			SpriteRenderer.instance.glEnable(3008);
		}
	}

	public static void disableAlphaTest() {
		if (ALPHA_TEST) {
			ALPHA_TEST = false;
			SpriteRenderer.instance.glDisable(3008);
		}
	}

	public static void enableStencilTest() {
		if (!STENCIL_TEST) {
			STENCIL_TEST = true;
			SpriteRenderer.instance.glEnable(2960);
		}
	}

	public static void disableStencilTest() {
		if (STENCIL_TEST) {
			STENCIL_TEST = false;
			SpriteRenderer.instance.glDisable(2960);
		}
	}
}
