package zombie.core.textures;

import gnu.trove.stack.array.TIntArrayStack;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;
import zombie.interfaces.ITexture;


public final class TextureFBO {
	private static IGLFramebufferObject funcs;
	private static int lastID = 0;
	private static final TIntArrayStack stack = new TIntArrayStack();
	private int id;
	ITexture texture;
	private int depth;
	private int width;
	private int height;
	private static Boolean checked = null;

	public void swapTexture(ITexture iTexture) {
		assert lastID == this.id;
		if (iTexture != null && iTexture != this.texture) {
			if (iTexture.getWidth() == this.width && iTexture.getHeight() == this.height) {
				if (iTexture.getID() == -1) {
					iTexture.bind();
				}

				IGLFramebufferObject iGLFramebufferObject = getFuncs();
				iGLFramebufferObject.glFramebufferTexture2D(iGLFramebufferObject.GL_FRAMEBUFFER(), iGLFramebufferObject.GL_COLOR_ATTACHMENT0(), 3553, iTexture.getID(), 0);
				this.texture = iTexture;
			}
		}
	}

	public TextureFBO(ITexture iTexture) {
		this(iTexture, true);
	}

	public TextureFBO(ITexture iTexture, boolean boolean1) {
		this.id = 0;
		this.depth = 0;
		RenderThread.invokeOnRenderContext(iTexture, boolean1, this::init);
	}

	private void init(ITexture iTexture, boolean boolean1) {
		int int1 = lastID;
		boolean boolean2 = false;
		int int2;
		try {
			boolean2 = true;
			this.initInternal(iTexture, boolean1);
			boolean2 = false;
		} finally {
			if (boolean2) {
				IGLFramebufferObject iGLFramebufferObject = getFuncs();
				int2 = iGLFramebufferObject.GL_FRAMEBUFFER();
				lastID = int1;
				iGLFramebufferObject.glBindFramebuffer(int2, int1);
			}
		}

		IGLFramebufferObject iGLFramebufferObject2 = getFuncs();
		int2 = iGLFramebufferObject2.GL_FRAMEBUFFER();
		lastID = int1;
		iGLFramebufferObject2.glBindFramebuffer(int2, int1);
	}

	public static IGLFramebufferObject getFuncs() {
		if (funcs == null) {
			checkFBOSupport();
		}

		return funcs;
	}

	private void initInternal(ITexture iTexture, boolean boolean1) {
		IGLFramebufferObject iGLFramebufferObject = getFuncs();
		try {
			PZGLUtil.checkGLErrorThrow("Enter.");
			this.texture = iTexture;
			this.width = this.texture.getWidth();
			this.height = this.texture.getHeight();
			if (!checkFBOSupport()) {
				throw new RuntimeException("Could not create FBO. FBO\'s not supported.");
			} else if (this.texture == null) {
				throw new NullPointerException("Could not create FBO. Texture is null.");
			} else {
				this.texture.bind();
				PZGLUtil.checkGLErrorThrow("Binding texture. %s", this.texture);
				GL11.glTexImage2D(3553, 0, 6408, this.texture.getWidthHW(), this.texture.getHeightHW(), 0, 6408, 5121, (IntBuffer)null);
				PZGLUtil.checkGLErrorThrow("glTexImage2D(width: %d, height: %d)", this.texture.getWidthHW(), this.texture.getHeightHW());
				GL11.glTexParameteri(3553, 10242, 33071);
				GL11.glTexParameteri(3553, 10243, 33071);
				GL11.glTexParameteri(3553, 10240, 9729);
				GL11.glTexParameteri(3553, 10241, 9729);
				Texture.lastTextureID = 0;
				GL11.glBindTexture(3553, 0);
				this.id = iGLFramebufferObject.glGenFramebuffers();
				PZGLUtil.checkGLErrorThrow("glGenFrameBuffers");
				iGLFramebufferObject.glBindFramebuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), this.id);
				PZGLUtil.checkGLErrorThrow("glBindFramebuffer(%d)", this.id);
				iGLFramebufferObject.glFramebufferTexture2D(iGLFramebufferObject.GL_FRAMEBUFFER(), iGLFramebufferObject.GL_COLOR_ATTACHMENT0(), 3553, this.texture.getID(), 0);
				PZGLUtil.checkGLErrorThrow("glFramebufferTexture2D texture: %s", this.texture);
				this.depth = iGLFramebufferObject.glGenRenderbuffers();
				PZGLUtil.checkGLErrorThrow("glGenRenderbuffers");
				iGLFramebufferObject.glBindRenderbuffer(iGLFramebufferObject.GL_RENDERBUFFER(), this.depth);
				PZGLUtil.checkGLErrorThrow("glBindRenderbuffer depth: %d", this.depth);
				if (boolean1) {
					iGLFramebufferObject.glRenderbufferStorage(iGLFramebufferObject.GL_RENDERBUFFER(), iGLFramebufferObject.GL_DEPTH24_STENCIL8(), this.texture.getWidthHW(), this.texture.getHeightHW());
					PZGLUtil.checkGLErrorThrow("glRenderbufferStorage(width: %d, height: %d)", this.texture.getWidthHW(), this.texture.getHeightHW());
					iGLFramebufferObject.glBindRenderbuffer(iGLFramebufferObject.GL_RENDERBUFFER(), 0);
					iGLFramebufferObject.glFramebufferRenderbuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), iGLFramebufferObject.GL_DEPTH_ATTACHMENT(), iGLFramebufferObject.GL_RENDERBUFFER(), this.depth);
					PZGLUtil.checkGLErrorThrow("glFramebufferRenderbuffer(depth: %d)", this.depth);
					iGLFramebufferObject.glFramebufferRenderbuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), iGLFramebufferObject.GL_STENCIL_ATTACHMENT(), iGLFramebufferObject.GL_RENDERBUFFER(), this.depth);
					PZGLUtil.checkGLErrorThrow("glFramebufferRenderbuffer(stencil: %d)", this.depth);
				} else {
					iGLFramebufferObject.glRenderbufferStorage(iGLFramebufferObject.GL_RENDERBUFFER(), 6402, this.texture.getWidthHW(), this.texture.getHeightHW());
					PZGLUtil.checkGLErrorThrow("glRenderbufferStorage(width: %d, height: %d)", this.texture.getWidthHW(), this.texture.getHeightHW());
					iGLFramebufferObject.glBindRenderbuffer(iGLFramebufferObject.GL_RENDERBUFFER(), 0);
					iGLFramebufferObject.glFramebufferRenderbuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), iGLFramebufferObject.GL_DEPTH_ATTACHMENT(), iGLFramebufferObject.GL_RENDERBUFFER(), this.depth);
					PZGLUtil.checkGLErrorThrow("glFramebufferRenderbuffer(depth: %d)", this.depth);
				}

				int int1 = iGLFramebufferObject.glCheckFramebufferStatus(iGLFramebufferObject.GL_FRAMEBUFFER());
				if (int1 != iGLFramebufferObject.GL_FRAMEBUFFER_COMPLETE()) {
					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_UNDEFINED()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_UNDEFINED");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_FORMATS");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_UNSUPPORTED()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_UNSUPPORTED");
					}

					if (int1 == iGLFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE()) {
						DebugLog.General.error("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
					}

					throw new RuntimeException("Could not create FBO!");
				}
			}
		} catch (Exception exception) {
			iGLFramebufferObject.glDeleteFramebuffers(this.id);
			iGLFramebufferObject.glDeleteRenderbuffers(this.depth);
			this.id = 0;
			this.depth = 0;
			this.texture = null;
			throw exception;
		}
	}

	public static boolean checkFBOSupport() {
		if (checked != null) {
			return checked;
		} else if (GL.getCapabilities().OpenGL30) {
			DebugLog.General.debugln("OpenGL 3.0 framebuffer objects supported");
			funcs = new GLFramebufferObject30();
			return checked = Boolean.TRUE;
		} else if (GL.getCapabilities().GL_ARB_framebuffer_object) {
			DebugLog.General.debugln("GL_ARB_framebuffer_object supported");
			funcs = new GLFramebufferObjectARB();
			return checked = Boolean.TRUE;
		} else if (GL.getCapabilities().GL_EXT_framebuffer_object) {
			DebugLog.General.debugln("GL_EXT_framebuffer_object supported");
			if (!GL.getCapabilities().GL_EXT_packed_depth_stencil) {
				DebugLog.General.debugln("GL_EXT_packed_depth_stencil not supported");
			}

			funcs = new GLFramebufferObjectEXT();
			return checked = Boolean.TRUE;
		} else {
			DebugLog.General.debugln("None of OpenGL 3.0, GL_ARB_framebuffer_object or GL_EXT_framebuffer_object are supported, zoom disabled");
			return checked = Boolean.TRUE;
		}
	}

	public void destroy() {
		if (this.id != 0 && this.depth != 0) {
			if (lastID == this.id) {
				lastID = 0;
			}

			RenderThread.invokeOnRenderContext(()->{
				if (this.texture != null) {
					this.texture.destroy();
					this.texture = null;
				}

				IGLFramebufferObject iGLFramebufferObject = getFuncs();
				iGLFramebufferObject.glDeleteFramebuffers(this.id);
				iGLFramebufferObject.glDeleteRenderbuffers(this.depth);
				this.id = 0;
				this.depth = 0;
			});
		}
	}

	public void destroyLeaveTexture() {
		if (this.id != 0 && this.depth != 0) {
			RenderThread.invokeOnRenderContext(()->{
				this.texture = null;
				IGLFramebufferObject iGLFramebufferObject = getFuncs();
				iGLFramebufferObject.glDeleteFramebuffers(this.id);
				iGLFramebufferObject.glDeleteRenderbuffers(this.depth);
				this.id = 0;
				this.depth = 0;
			});
		}
	}

	public void releaseTexture() {
		IGLFramebufferObject iGLFramebufferObject = getFuncs();
		iGLFramebufferObject.glFramebufferTexture2D(iGLFramebufferObject.GL_FRAMEBUFFER(), iGLFramebufferObject.GL_COLOR_ATTACHMENT0(), 3553, 0, 0);
		this.texture = null;
	}

	public void endDrawing() {
		if (stack.size() != 0) {
			lastID = stack.pop();
		} else {
			lastID = 0;
		}

		IGLFramebufferObject iGLFramebufferObject = getFuncs();
		iGLFramebufferObject.glBindFramebuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), lastID);
	}

	public ITexture getTexture() {
		return this.texture;
	}

	public int getBufferId() {
		return this.id;
	}

	public boolean isDestroyed() {
		return this.texture == null || this.id == 0 || this.depth == 0;
	}

	public void startDrawing() {
		this.startDrawing(false, false);
	}

	public void startDrawing(boolean boolean1, boolean boolean2) {
		stack.push(lastID);
		lastID = this.id;
		IGLFramebufferObject iGLFramebufferObject = getFuncs();
		iGLFramebufferObject.glBindFramebuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), this.id);
		if (this.texture != null) {
			if (boolean1) {
				GL11.glClearColor(0.0F, 0.0F, 0.0F, boolean2 ? 0.0F : 1.0F);
				GL11.glClear(16640);
				if (boolean2) {
					GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
				}
			}
		}
	}

	public void setTexture(Texture texture) {
		int int1 = lastID;
		IGLFramebufferObject iGLFramebufferObject = getFuncs();
		iGLFramebufferObject.glBindFramebuffer(iGLFramebufferObject.GL_FRAMEBUFFER(), lastID = this.id);
		this.swapTexture(texture);
		int int2 = iGLFramebufferObject.GL_FRAMEBUFFER();
		lastID = int1;
		iGLFramebufferObject.glBindFramebuffer(int2, int1);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public static int getCurrentID() {
		return lastID;
	}

	public static void reset() {
		stack.clear();
		if (lastID != 0) {
			IGLFramebufferObject iGLFramebufferObject = getFuncs();
			int int1 = iGLFramebufferObject.GL_FRAMEBUFFER();
			lastID = 0;
			iGLFramebufferObject.glBindFramebuffer(int1, 0);
		}
	}
}
