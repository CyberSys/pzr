package zombie.iso;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.lwjglx.BufferUtils;
import zombie.core.Rand;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.interfaces.ITexture;
import zombie.iso.weather.ClimateManager;


public final class ParticlesFire extends Particles {
	int MaxParticles = 1000000;
	int MaxVortices = 4;
	int particles_data_buffer;
	ByteBuffer particule_data;
	private Texture texFireSmoke;
	private Texture texFlameFire;
	public FireShader EffectFire;
	public SmokeShader EffectSmoke;
	public Shader EffectVape;
	float windX;
	float windY;
	private static ParticlesFire instance;
	private ParticlesArray particles = new ParticlesArray();
	private ArrayList zones = new ArrayList();
	private int intensityFire = 0;
	private int intensitySmoke = 0;
	private int intensitySteam = 0;
	private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

	public static synchronized ParticlesFire getInstance() {
		if (instance == null) {
			instance = new ParticlesFire();
		}

		return instance;
	}

	public ParticlesFire() {
		this.particule_data = BufferUtils.createByteBuffer(this.MaxParticles * 4 * 4);
		this.texFireSmoke = Texture.getSharedTexture("media/textures/FireSmokes.png");
		this.texFlameFire = Texture.getSharedTexture("media/textures/FireFlame.png");
		this.zones.clear();
		float float1 = (float)((int)(IsoCamera.frameState.OffX + (float)(IsoCamera.frameState.OffscreenWidth / 2)));
		float float2 = (float)((int)(IsoCamera.frameState.OffY + (float)(IsoCamera.frameState.OffscreenHeight / 2)));
		this.zones.add(new ParticlesFire.Zone(10, float1 - 30.0F, float2 - 10.0F, float1 + 30.0F, float2 + 10.0F));
		this.zones.add(new ParticlesFire.Zone(10, float1 - 200.0F, float2, 50.0F));
		this.zones.add(new ParticlesFire.Zone(40, float1 + 200.0F, float2, 100.0F));
		this.zones.add(new ParticlesFire.Zone(60, float1 - 150.0F, float2 - 300.0F, float1 + 250.0F, float2 - 300.0F, 10.0F));
		this.zones.add(new ParticlesFire.Zone(10, float1 - 350.0F, float2 - 200.0F, float1 - 350.0F, float2 - 300.0F, 10.0F));
	}

	private void ParticlesProcess() {
		for (int int1 = 0; int1 < this.zones.size(); ++int1) {
			ParticlesFire.Zone zone = (ParticlesFire.Zone)this.zones.get(int1);
			int int2 = (int)Math.ceil((double)((float)(zone.intensity - zone.currentParticles) * 0.1F));
			int int3;
			ParticlesFire.Particle particle;
			if (zone.type == ParticlesFire.ZoneType.Rectangle) {
				for (int3 = 0; int3 < int2; ++int3) {
					particle = new ParticlesFire.Particle();
					particle.x = Rand.Next(zone.x0, zone.x1);
					particle.y = Rand.Next(zone.y0, zone.y1);
					particle.vx = Rand.Next(-3.0F, 3.0F);
					particle.vy = Rand.Next(1.0F, 5.0F);
					particle.tShift = 0.0F;
					particle.id = Rand.Next(-1000000.0F, 1000000.0F);
					particle.zone = zone;
					++zone.currentParticles;
					this.particles.addParticle(particle);
				}
			}

			float float1;
			float float2;
			if (zone.type == ParticlesFire.ZoneType.Circle) {
				for (int3 = 0; int3 < int2; ++int3) {
					particle = new ParticlesFire.Particle();
					float1 = Rand.Next(0.0F, 6.2831855F);
					float2 = Rand.Next(0.0F, zone.r);
					particle.x = (float)((double)zone.x0 + (double)float2 * Math.cos((double)float1));
					particle.y = (float)((double)zone.y0 + (double)float2 * Math.sin((double)float1));
					particle.vx = Rand.Next(-3.0F, 3.0F);
					particle.vy = Rand.Next(1.0F, 5.0F);
					particle.tShift = 0.0F;
					particle.id = Rand.Next(-1000000.0F, 1000000.0F);
					particle.zone = zone;
					++zone.currentParticles;
					this.particles.addParticle(particle);
				}
			}

			if (zone.type == ParticlesFire.ZoneType.Line) {
				for (int3 = 0; int3 < int2; ++int3) {
					particle = new ParticlesFire.Particle();
					float1 = Rand.Next(0.0F, 6.2831855F);
					float2 = Rand.Next(0.0F, zone.r);
					float float3 = Rand.Next(0.0F, 1.0F);
					particle.x = (float)((double)(zone.x0 * float3 + zone.x1 * (1.0F - float3)) + (double)float2 * Math.cos((double)float1));
					particle.y = (float)((double)(zone.y0 * float3 + zone.y1 * (1.0F - float3)) + (double)float2 * Math.sin((double)float1));
					particle.vx = Rand.Next(-3.0F, 3.0F);
					particle.vy = Rand.Next(1.0F, 5.0F);
					particle.tShift = 0.0F;
					particle.id = Rand.Next(-1000000.0F, 1000000.0F);
					particle.zone = zone;
					++zone.currentParticles;
					this.particles.addParticle(particle);
				}
			}

			if (int2 < 0) {
				for (int3 = 0; int3 < -int2; ++int3) {
					--zone.currentParticles;
					this.particles.deleteParticle(Rand.Next(0, this.particles.getCount() + 1));
				}
			}
		}
	}

	public FloatBuffer getParametersFire() {
		this.floatBuffer.clear();
		this.floatBuffer.put(this.windX);
		this.floatBuffer.put((float)this.intensityFire);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(this.windY);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}

	public int getFireShaderID() {
		return this.EffectFire.getID();
	}

	public int getSmokeShaderID() {
		return this.EffectSmoke.getID();
	}

	public int getVapeShaderID() {
		return this.EffectVape.getID();
	}

	public ITexture getFireFlameTexture() {
		return this.texFlameFire;
	}

	public ITexture getFireSmokeTexture() {
		return this.texFireSmoke;
	}

	public void reloadShader() {
		RenderThread.invokeOnRenderContext(()->{
			this.EffectFire = new FireShader("fire");
			this.EffectSmoke = new SmokeShader("smoke");
			this.EffectVape = new Shader("vape");
		});
	}

	void createParticleBuffers() {
		this.particles_data_buffer = funcs.glGenBuffers();
		funcs.glBindBuffer(34962, this.particles_data_buffer);
		funcs.glBufferData(34962, (long)(this.MaxParticles * 4 * 4), 35044);
	}

	void destroyParticleBuffers() {
		funcs.glDeleteBuffers(this.particles_data_buffer);
	}

	void updateParticleParams() {
		float float1 = ClimateManager.getInstance().getWindAngleIntensity();
		float float2 = ClimateManager.getInstance().getWindIntensity();
		this.windX = (float)Math.sin((double)(float1 * 6.0F)) * float2;
		this.windY = (float)Math.cos((double)(float1 * 6.0F)) * float2;
		this.ParticlesProcess();
		if (this.particles.getNeedToUpdate()) {
			this.particles.defragmentParticle();
			this.particule_data.clear();
			for (int int1 = 0; int1 < this.particles.size(); ++int1) {
				ParticlesFire.Particle particle = (ParticlesFire.Particle)this.particles.get(int1);
				if (particle != null) {
					this.particule_data.putFloat(particle.x);
					this.particule_data.putFloat(particle.y);
					this.particule_data.putFloat(particle.id);
					this.particule_data.putFloat((float)int1 / (float)this.particles.size());
				}
			}

			this.particule_data.flip();
		}

		funcs.glBindBuffer(34962, this.particles_data_buffer);
		funcs.glBufferData(34962, this.particule_data, 35040);
		GL20.glEnableVertexAttribArray(1);
		funcs.glBindBuffer(34962, this.particles_data_buffer);
		GL20.glVertexAttribPointer(1, 4, 5126, false, 0, 0L);
		GL33.glVertexAttribDivisor(1, 1);
	}

	int getParticleCount() {
		return this.particles.getCount();
	}

	public class Zone {
		ParticlesFire.ZoneType type;
		int intensity;
		int currentParticles;
		float x0;
		float y0;
		float x1;
		float y1;
		float r;
		float fireIntensity;
		float smokeIntensity;
		float sparksIntensity;
		float vortices;
		float vorticeSpeed;
		float area;
		float temperature;
		float centerX;
		float centerY;
		float centerRp2;
		float currentVorticesCount;

		Zone(int int1, float float1, float float2, float float3) {
			this.type = ParticlesFire.ZoneType.Circle;
			this.intensity = int1;
			this.currentParticles = 0;
			this.x0 = float1;
			this.y0 = float2;
			this.r = float3;
			this.area = (float)(3.141592653589793 * (double)float3 * (double)float3);
			this.vortices = (float)this.intensity * 0.3F;
			this.vorticeSpeed = 0.5F;
			this.temperature = 2000.0F;
			this.centerX = float1;
			this.centerY = float2;
			this.centerRp2 = float3 * float3;
		}

		Zone(int int1, float float1, float float2, float float3, float float4) {
			this.type = ParticlesFire.ZoneType.Rectangle;
			this.intensity = int1;
			this.currentParticles = 0;
			if (float1 < float3) {
				this.x0 = float1;
				this.x1 = float3;
			} else {
				this.x1 = float1;
				this.x0 = float3;
			}

			if (float2 < float4) {
				this.y0 = float2;
				this.y1 = float4;
			} else {
				this.y1 = float2;
				this.y0 = float4;
			}

			this.area = (this.x1 - this.x0) * (this.y1 - this.y0);
			this.vortices = (float)this.intensity * 0.3F;
			this.vorticeSpeed = 0.5F;
			this.temperature = 2000.0F;
			this.centerX = (this.x0 + this.x1) * 0.5F;
			this.centerY = (this.y0 + this.y1) * 0.5F;
			this.centerRp2 = (this.x1 - this.x0) * (this.x1 - this.x0);
		}

		Zone(int int1, float float1, float float2, float float3, float float4, float float5) {
			this.type = ParticlesFire.ZoneType.Line;
			this.intensity = int1;
			this.currentParticles = 0;
			if (float1 < float3) {
				this.x0 = float1;
				this.x1 = float3;
				this.y0 = float2;
				this.y1 = float4;
			} else {
				this.x1 = float1;
				this.x0 = float3;
				this.y1 = float2;
				this.y0 = float4;
			}

			this.r = float5;
			this.area = (float)((double)this.r * Math.sqrt(Math.pow((double)(float1 - float3), 2.0) + Math.pow((double)(float2 - float4), 2.0)));
			this.vortices = (float)this.intensity * 0.3F;
			this.vorticeSpeed = 0.5F;
			this.temperature = 2000.0F;
			this.centerX = (this.x0 + this.x1) * 0.5F;
			this.centerY = (this.y0 + this.y1) * 0.5F;
			this.centerRp2 = (this.x1 - this.x0 + float5) * (this.x1 - this.x0 + float5) * 100.0F;
		}
	}

	static enum ZoneType {

		Rectangle,
		Circle,
		Line;

		private static ParticlesFire.ZoneType[] $values() {
			return new ParticlesFire.ZoneType[]{Rectangle, Circle, Line};
		}
	}

	public class Particle {
		float id;
		float x;
		float y;
		float tShift;
		float vx;
		float vy;
		ParticlesFire.Zone zone;
	}

	public class Vortice {
		float x;
		float y;
		float z;
		float size;
		float vx;
		float vy;
		float speed;
		int life;
		int lifeTime;
		ParticlesFire.Zone zone;
	}
}
