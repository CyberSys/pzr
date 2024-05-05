package zombie.iso;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjglx.BufferUtils;
import zombie.GameTime;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLBufferObject15;
import zombie.core.VBO.GLBufferObjectARB;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;


public abstract class Particles {
	private float ParticlesTime;
	public static int ParticleSystemsCount = 0;
	public static int ParticleSystemsLast = 0;
	public static final ArrayList ParticleSystems = new ArrayList();
	private int id;
	int particle_vertex_buffer;
	public static IGLBufferObject funcs = null;
	private Matrix4f projectionMatrix;
	private Matrix4f mvpMatrix;
	private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

	public static synchronized int addParticle(Particles particles) {
		if (ParticleSystems.size() == ParticleSystemsCount) {
			ParticleSystems.add(particles);
			++ParticleSystemsCount;
			return ParticleSystems.size() - 1;
		} else {
			int int1 = ParticleSystemsLast;
			if (int1 < ParticleSystems.size()) {
				if (ParticleSystems.get(int1) == null) {
					ParticleSystemsLast = int1;
					ParticleSystems.set(int1, particles);
					++ParticleSystemsCount;
				}

				return int1;
			} else {
				byte byte1 = 0;
				if (byte1 < ParticleSystemsLast) {
					if (ParticleSystems.get(byte1) == null) {
						ParticleSystemsLast = byte1;
						ParticleSystems.set(byte1, particles);
						++ParticleSystemsCount;
					}

					return byte1;
				} else {
					DebugLog.log("ERROR: addParticle has unknown error");
					return -1;
				}
			}
		}
	}

	public static synchronized void deleteParticle(int int1) {
		ParticleSystems.set(int1, (Object)null);
		--ParticleSystemsCount;
	}

	public static void init() {
		if (funcs == null) {
			if (!GL.getCapabilities().OpenGL33) {
				System.out.println("OpenGL 3.3 don\'t supported");
			}

			if (GL.getCapabilities().OpenGL15) {
				System.out.println("OpenGL 1.5 buffer objects supported");
				funcs = new GLBufferObject15();
			} else {
				if (!GL.getCapabilities().GL_ARB_vertex_buffer_object) {
					throw new RuntimeException("Neither OpenGL 1.5 nor GL_ARB_vertex_buffer_object supported");
				}

				System.out.println("GL_ARB_vertex_buffer_object supported");
				funcs = new GLBufferObjectARB();
			}
		}
	}

	public void initBuffers() {
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(48);
		byteBuffer.clear();
		byteBuffer.putFloat(-1.0F);
		byteBuffer.putFloat(-1.0F);
		byteBuffer.putFloat(0.0F);
		byteBuffer.putFloat(1.0F);
		byteBuffer.putFloat(-1.0F);
		byteBuffer.putFloat(0.0F);
		byteBuffer.putFloat(-1.0F);
		byteBuffer.putFloat(1.0F);
		byteBuffer.putFloat(0.0F);
		byteBuffer.putFloat(1.0F);
		byteBuffer.putFloat(1.0F);
		byteBuffer.putFloat(0.0F);
		byteBuffer.flip();
		this.particle_vertex_buffer = funcs.glGenBuffers();
		funcs.glBindBuffer(34962, this.particle_vertex_buffer);
		funcs.glBufferData(34962, byteBuffer, 35044);
		MemoryUtil.memFree(byteBuffer);
		this.createParticleBuffers();
	}

	public void destroy() {
		deleteParticle(this.id);
		funcs.glDeleteBuffers(this.particle_vertex_buffer);
		this.destroyParticleBuffers();
	}

	public abstract void reloadShader();

	public Particles() {
		RenderThread.invokeOnRenderContext(()->{
			init();
			this.initBuffers();
			this.projectionMatrix = new Matrix4f();
		});
		this.reloadShader();
		this.id = addParticle(this);
	}

	private static Matrix4f orthogonal(float float1, float float2, float float3, float float4, float float5, float float6) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();
		matrix4f.m00 = 2.0F / (float2 - float1);
		matrix4f.m11 = 2.0F / (float4 - float3);
		matrix4f.m22 = -2.0F / (float6 - float5);
		matrix4f.m32 = (-float6 - float5) / (float6 - float5);
		matrix4f.m30 = (-float2 - float1) / (float2 - float1);
		matrix4f.m31 = (-float4 - float3) / (float4 - float3);
		return matrix4f;
	}

	public void render() {
		int int1 = IsoCamera.frameState.playerIndex;
		this.ParticlesTime += 0.0166F * GameTime.getInstance().getMultiplier();
		this.updateMVPMatrix();
		SpriteRenderer.instance.drawParticles(int1, 0, 0);
	}

	private void updateMVPMatrix() {
		this.projectionMatrix = orthogonal(IsoCamera.frameState.OffX, IsoCamera.frameState.OffX + (float)IsoCamera.frameState.OffscreenWidth, IsoCamera.frameState.OffY + (float)IsoCamera.frameState.OffscreenHeight, IsoCamera.frameState.OffY, -1.0F, 1.0F);
		this.mvpMatrix = this.projectionMatrix;
	}

	public FloatBuffer getMVPMatrix() {
		this.floatBuffer.clear();
		this.floatBuffer.put(this.mvpMatrix.m00);
		this.floatBuffer.put(this.mvpMatrix.m10);
		this.floatBuffer.put(this.mvpMatrix.m20);
		this.floatBuffer.put(this.mvpMatrix.m30);
		this.floatBuffer.put(this.mvpMatrix.m01);
		this.floatBuffer.put(this.mvpMatrix.m11);
		this.floatBuffer.put(this.mvpMatrix.m21);
		this.floatBuffer.put(this.mvpMatrix.m31);
		this.floatBuffer.put(this.mvpMatrix.m02);
		this.floatBuffer.put(this.mvpMatrix.m12);
		this.floatBuffer.put(this.mvpMatrix.m22);
		this.floatBuffer.put(this.mvpMatrix.m32);
		this.floatBuffer.put(this.mvpMatrix.m03);
		this.floatBuffer.put(this.mvpMatrix.m13);
		this.floatBuffer.put(this.mvpMatrix.m23);
		this.floatBuffer.put(this.mvpMatrix.m33);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}

	public void getGeometry(int int1) {
		this.updateParticleParams();
		GL20.glEnableVertexAttribArray(0);
		funcs.glBindBuffer(34962, this.particle_vertex_buffer);
		GL20.glVertexAttribPointer(0, 3, 5126, false, 0, 0L);
		GL33.glVertexAttribDivisor(0, 0);
		GL31.glDrawArraysInstanced(5, 0, 4, this.getParticleCount());
	}

	public void getGeometryFire(int int1) {
		this.updateParticleParams();
		GL20.glEnableVertexAttribArray(0);
		funcs.glBindBuffer(34962, this.particle_vertex_buffer);
		GL20.glVertexAttribPointer(0, 3, 5126, false, 0, 0L);
		GL33.glVertexAttribDivisor(0, 0);
		GL31.glDrawArraysInstanced(5, 0, 4, this.getParticleCount());
	}

	public float getShaderTime() {
		return this.ParticlesTime;
	}

	abstract void createParticleBuffers();

	abstract void destroyParticleBuffers();

	abstract void updateParticleParams();

	abstract int getParticleCount();
}
