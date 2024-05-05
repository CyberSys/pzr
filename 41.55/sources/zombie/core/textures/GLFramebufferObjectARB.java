package zombie.core.textures;

import org.lwjgl.opengl.ARBFramebufferObject;


public final class GLFramebufferObjectARB implements IGLFramebufferObject {

	public int GL_FRAMEBUFFER() {
		return 36160;
	}

	public int GL_RENDERBUFFER() {
		return 36161;
	}

	public int GL_COLOR_ATTACHMENT0() {
		return 36064;
	}

	public int GL_DEPTH_ATTACHMENT() {
		return 36096;
	}

	public int GL_STENCIL_ATTACHMENT() {
		return 36128;
	}

	public int GL_DEPTH_STENCIL() {
		return 34041;
	}

	public int GL_DEPTH24_STENCIL8() {
		return 35056;
	}

	public int GL_FRAMEBUFFER_COMPLETE() {
		return 36053;
	}

	public int GL_FRAMEBUFFER_UNDEFINED() {
		return 33305;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT() {
		return 36054;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT() {
		return 36055;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS() {
		return 0;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_FORMATS() {
		return 0;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER() {
		return 36059;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER() {
		return 36060;
	}

	public int GL_FRAMEBUFFER_UNSUPPORTED() {
		return 36061;
	}

	public int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE() {
		return 36182;
	}

	public int glGenFramebuffers() {
		return ARBFramebufferObject.glGenFramebuffers();
	}

	public void glBindFramebuffer(int int1, int int2) {
		ARBFramebufferObject.glBindFramebuffer(int1, int2);
	}

	public void glFramebufferTexture2D(int int1, int int2, int int3, int int4, int int5) {
		ARBFramebufferObject.glFramebufferTexture2D(int1, int2, int3, int4, int5);
	}

	public int glGenRenderbuffers() {
		return ARBFramebufferObject.glGenRenderbuffers();
	}

	public void glBindRenderbuffer(int int1, int int2) {
		ARBFramebufferObject.glBindRenderbuffer(int1, int2);
	}

	public void glRenderbufferStorage(int int1, int int2, int int3, int int4) {
		ARBFramebufferObject.glRenderbufferStorage(int1, int2, int3, int4);
	}

	public void glFramebufferRenderbuffer(int int1, int int2, int int3, int int4) {
		ARBFramebufferObject.glFramebufferRenderbuffer(int1, int2, int3, int4);
	}

	public int glCheckFramebufferStatus(int int1) {
		return ARBFramebufferObject.glCheckFramebufferStatus(int1);
	}

	public void glDeleteFramebuffers(int int1) {
		ARBFramebufferObject.glDeleteFramebuffers(int1);
	}

	public void glDeleteRenderbuffers(int int1) {
		ARBFramebufferObject.glDeleteRenderbuffers(int1);
	}
}
