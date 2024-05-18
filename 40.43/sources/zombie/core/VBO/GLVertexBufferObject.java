package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.ARBMapBufferRange;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import zombie.core.skinnedmodel.model.VertexBufferObject;


public class GLVertexBufferObject {
	public static IGLBufferObject funcs;
	private long size;
	private final int type;
	private final int usage;
	private transient int id;
	private transient boolean mapped;
	private transient boolean cleared;
	private transient ByteBuffer buffer;

	public static void init() {
		if (GLContext.getCapabilities().OpenGL15) {
			System.out.println("OpenGL 1.5 buffer objects supported");
			funcs = new GLBufferObject15();
		} else {
			if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
				throw new RuntimeException("Neither OpenGL 1.5 nor GL_ARB_vertex_buffer_object supported");
			}

			System.out.println("GL_ARB_vertex_buffer_object supported");
			funcs = new GLBufferObjectARB();
		}

		VertexBufferObject.funcs = funcs;
	}

	public GLVertexBufferObject(long long1, int int1, int int2) {
		this.size = long1;
		this.type = int1;
		this.usage = int2;
	}

	public GLVertexBufferObject(int int1, int int2) {
		this.size = 0L;
		this.type = int1;
		this.usage = int2;
	}

	public void create() {
		this.id = funcs.glGenBuffers();
	}

	public void clear() {
		if (!this.cleared) {
			funcs.glBufferData(this.type, this.size, this.usage);
			this.cleared = true;
		}
	}

	protected void doDestroy() {
		if (this.id != 0) {
			this.unmap();
			funcs.glDeleteBuffers(this.id);
			this.id = 0;
		}
	}

	public ByteBuffer map(int int1) {
		if (!this.mapped) {
			if (this.size != (long)int1) {
				this.size = (long)int1;
				this.clear();
			}

			if (this.buffer != null && this.buffer.capacity() < int1) {
				this.buffer = null;
			}

			ByteBuffer byteBuffer = this.buffer;
			byte byte1;
			if (GLContext.getCapabilities().OpenGL30) {
				byte1 = 34;
				this.buffer = GL30.glMapBufferRange(this.type, 0L, (long)int1, byte1, this.buffer);
			} else if (GLContext.getCapabilities().GL_ARB_map_buffer_range) {
				byte1 = 34;
				this.buffer = ARBMapBufferRange.glMapBufferRange(this.type, 0L, (long)int1, byte1, this.buffer);
			} else {
				this.buffer = funcs.glMapBuffer(this.type, funcs.GL_WRITE_ONLY(), (long)int1, this.buffer);
			}

			if (this.buffer == null) {
				throw new OpenGLException("Failed to map buffer " + this);
			}

			if (this.buffer != byteBuffer && byteBuffer != null) {
			}

			this.buffer.order(ByteOrder.nativeOrder()).clear().limit(int1);
			this.mapped = true;
			this.cleared = false;
		}

		return this.buffer;
	}

	public ByteBuffer map() {
		if (!this.mapped) {
			assert this.size > 0L;
			this.clear();
			ByteBuffer byteBuffer = this.buffer;
			byte byte1;
			if (GLContext.getCapabilities().OpenGL30) {
				byte1 = 34;
				this.buffer = GL30.glMapBufferRange(this.type, 0L, this.size, byte1, this.buffer);
			} else if (GLContext.getCapabilities().GL_ARB_map_buffer_range) {
				byte1 = 34;
				this.buffer = ARBMapBufferRange.glMapBufferRange(this.type, 0L, this.size, byte1, this.buffer);
			} else {
				this.buffer = funcs.glMapBuffer(this.type, funcs.GL_WRITE_ONLY(), this.size, this.buffer);
			}

			if (this.buffer == null) {
				throw new OpenGLException("Failed to map a buffer " + this.size + " bytes long");
			}

			if (this.buffer != byteBuffer && byteBuffer != null) {
			}

			this.buffer.order(ByteOrder.nativeOrder()).clear().limit((int)this.size);
			this.mapped = true;
			this.cleared = false;
		}

		return this.buffer;
	}

	public void orphan() {
		funcs.glMapBuffer(this.type, this.usage, this.size, (ByteBuffer)null);
	}

	public boolean unmap() {
		if (this.mapped) {
			this.mapped = false;
			return funcs.glUnmapBuffer(this.type);
		} else {
			return true;
		}
	}

	public boolean isMapped() {
		return this.mapped;
	}

	public String toString() {
		return "GLVertexBufferObject[" + this.id + ", " + this.size + "]";
	}

	public void render() {
		funcs.glBindBuffer(this.type, this.id);
	}

	public int getID() {
		return this.id;
	}
}
