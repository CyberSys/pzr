package zombie.core.textures;


public interface IGLFramebufferObject {

	int GL_FRAMEBUFFER();

	int GL_RENDERBUFFER();

	int GL_COLOR_ATTACHMENT0();

	int GL_DEPTH_ATTACHMENT();

	int GL_STENCIL_ATTACHMENT();

	int GL_DEPTH_STENCIL();

	int GL_DEPTH24_STENCIL8();

	int GL_FRAMEBUFFER_COMPLETE();

	int GL_FRAMEBUFFER_UNDEFINED();

	int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT();

	int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT();

	int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS();

	int GL_FRAMEBUFFER_INCOMPLETE_FORMATS();

	int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER();

	int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER();

	int GL_FRAMEBUFFER_UNSUPPORTED();

	int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE();

	int glGenFramebuffers();

	void glBindFramebuffer(int int1, int int2);

	void glFramebufferTexture2D(int int1, int int2, int int3, int int4, int int5);

	int glGenRenderbuffers();

	void glBindRenderbuffer(int int1, int int2);

	void glRenderbufferStorage(int int1, int int2, int int3, int int4);

	void glFramebufferRenderbuffer(int int1, int int2, int int3, int int4);

	int glCheckFramebufferStatus(int int1);

	void glDeleteFramebuffers(int int1);

	void glDeleteRenderbuffers(int int1);
}
