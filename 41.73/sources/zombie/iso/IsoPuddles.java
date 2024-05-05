package zombie.iso;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjglx.BufferUtils;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.opengl.SharedVertexBufferObjects;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.interfaces.ITexture;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;


public final class IsoPuddles {
	public Shader Effect;
	private float PuddlesWindAngle;
	private float PuddlesWindIntensity;
	private float PuddlesTime;
	private final Vector2f PuddlesParamWindINT;
	public static boolean leakingPuddlesInTheRoom = false;
	private Texture texHM;
	private int apiId;
	private static IsoPuddles instance;
	private static boolean isShaderEnable = false;
	static final int BYTES_PER_FLOAT = 4;
	static final int FLOATS_PER_VERTEX = 7;
	static final int BYTES_PER_VERTEX = 28;
	static final int VERTICES_PER_SQUARE = 4;
	public static final SharedVertexBufferObjects VBOs = new SharedVertexBufferObjects(28);
	private final IsoPuddles.RenderData[][] renderData = new IsoPuddles.RenderData[3][4];
	private final Vector4f shaderOffset = new Vector4f();
	private final Vector4f shaderOffsetMain = new Vector4f();
	private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
	public static final int BOOL_MAX = 0;
	public static final int FLOAT_RAIN = 0;
	public static final int FLOAT_WETGROUND = 1;
	public static final int FLOAT_MUDDYPUDDLES = 2;
	public static final int FLOAT_PUDDLESSIZE = 3;
	public static final int FLOAT_RAININTENSITY = 4;
	public static final int FLOAT_MAX = 5;
	private IsoPuddles.PuddlesFloat rain;
	private IsoPuddles.PuddlesFloat wetGround;
	private IsoPuddles.PuddlesFloat muddyPuddles;
	private IsoPuddles.PuddlesFloat puddlesSize;
	private IsoPuddles.PuddlesFloat rainIntensity;
	private final IsoPuddles.PuddlesFloat[] climateFloats = new IsoPuddles.PuddlesFloat[5];

	public static synchronized IsoPuddles getInstance() {
		if (instance == null) {
			instance = new IsoPuddles();
		}

		return instance;
	}

	public boolean getShaderEnable() {
		return isShaderEnable;
	}

	public IsoPuddles() {
		if (GameServer.bServer) {
			Core.getInstance().setPerfPuddles(3);
			this.applyPuddlesQuality();
			this.PuddlesParamWindINT = new Vector2f(0.0F);
			this.setup();
		} else {
			this.texHM = Texture.getSharedTexture("media/textures/puddles_hm.png");
			RenderThread.invokeOnRenderContext(()->{
				if (GL.getCapabilities().OpenGL30) {
					this.apiId = 1;
				}

				if (GL.getCapabilities().GL_ARB_framebuffer_object) {
					this.apiId = 2;
				}

				if (GL.getCapabilities().GL_EXT_framebuffer_object) {
					this.apiId = 3;
				}
			});

			this.applyPuddlesQuality();
			this.PuddlesParamWindINT = new Vector2f(0.0F);
			for (int int1 = 0; int1 < this.renderData.length; ++int1) {
				for (int int2 = 0; int2 < 4; ++int2) {
					this.renderData[int1][int2] = new IsoPuddles.RenderData();
				}
			}

			this.setup();
		}
	}

	public void applyPuddlesQuality() {
		leakingPuddlesInTheRoom = Core.getInstance().getPerfPuddles() == 0;
		if (Core.getInstance().getPerfPuddles() == 3) {
			isShaderEnable = false;
		} else {
			isShaderEnable = true;
			if (PerformanceSettings.PuddlesQuality == 2) {
				RenderThread.invokeOnRenderContext(()->{
					this.Effect = new PuddlesShader("puddles_lq");
					this.Effect.Start();
					this.Effect.End();
				});
			}

			if (PerformanceSettings.PuddlesQuality == 1) {
				RenderThread.invokeOnRenderContext(()->{
					this.Effect = new PuddlesShader("puddles_mq");
					this.Effect.Start();
					this.Effect.End();
				});
			}

			if (PerformanceSettings.PuddlesQuality == 0) {
				RenderThread.invokeOnRenderContext(()->{
					this.Effect = new PuddlesShader("puddles_hq");
					this.Effect.Start();
					this.Effect.End();
				});
			}
		}
	}

	public Vector4f getShaderOffset() {
		int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int1);
		return this.shaderOffset.set(playerCamera.getOffX() - (float)IsoCamera.getOffscreenLeft(int1) * playerCamera.zoom, playerCamera.getOffY() + (float)IsoCamera.getOffscreenTop(int1) * playerCamera.zoom, (float)playerCamera.OffscreenWidth, (float)playerCamera.OffscreenHeight);
	}

	public Vector4f getShaderOffsetMain() {
		int int1 = IsoCamera.frameState.playerIndex;
		PlayerCamera playerCamera = IsoCamera.cameras[int1];
		return this.shaderOffsetMain.set(playerCamera.getOffX() - (float)IsoCamera.getOffscreenLeft(int1) * playerCamera.zoom, playerCamera.getOffY() + (float)IsoCamera.getOffscreenTop(int1) * playerCamera.zoom, (float)IsoCamera.getOffscreenWidth(int1), (float)IsoCamera.getOffscreenHeight(int1));
	}

	public void render(ArrayList arrayList, int int1) {
		if (DebugOptions.instance.Weather.WaterPuddles.getValue()) {
			int int2 = SpriteRenderer.instance.getMainStateIndex();
			int int3 = IsoCamera.frameState.playerIndex;
			IsoPuddles.RenderData renderData = this.renderData[int2][int3];
			if (int1 == 0) {
				renderData.clear();
			}

			if (!arrayList.isEmpty()) {
				if (this.getShaderEnable()) {
					if (Core.getInstance().getUseShaders()) {
						if (Core.getInstance().getPerfPuddles() != 3) {
							if (int1 <= 0 || Core.getInstance().getPerfPuddles() <= 0) {
								if ((double)this.wetGround.getFinalValue() != 0.0 || (double)this.puddlesSize.getFinalValue() != 0.0) {
									for (int int4 = 0; int4 < arrayList.size(); ++int4) {
										IsoPuddlesGeometry puddlesGeometry = ((IsoGridSquare)arrayList.get(int4)).getPuddles();
										if (puddlesGeometry != null && puddlesGeometry.shouldRender()) {
											puddlesGeometry.updateLighting(int3);
											renderData.addSquare(int1, puddlesGeometry);
										}
									}

									if (renderData.squaresPerLevel[int1] > 0) {
										SpriteRenderer.instance.drawPuddles(this.Effect, int3, this.apiId, int1);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void puddlesProjection() {
		int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int1);
		GL11.glOrtho((double)playerCamera.getOffX(), (double)(playerCamera.getOffX() + (float)playerCamera.OffscreenWidth), (double)(playerCamera.getOffY() + (float)playerCamera.OffscreenHeight), (double)playerCamera.getOffY(), -1.0, 1.0);
	}

	public void puddlesGeometry(int int1) {
		int int2 = SpriteRenderer.instance.getRenderStateIndex();
		int int3 = SpriteRenderer.instance.getRenderingPlayerIndex();
		IsoPuddles.RenderData renderData = this.renderData[int2][int3];
		int int4 = 0;
		int int5;
		for (int5 = 0; int5 < int1; ++int5) {
			int4 += renderData.squaresPerLevel[int5];
		}

		int int6;
		for (int5 = renderData.squaresPerLevel[int1]; int5 > 0; int5 -= int6) {
			int6 = this.renderSome(int4, int5);
			int4 += int6;
		}

		SpriteRenderer.ringBuffer.restoreVBOs = true;
	}

	private int renderSome(int int1, int int2) {
		VBOs.next();
		FloatBuffer floatBuffer = VBOs.vertices;
		ShortBuffer shortBuffer = VBOs.indices;
		GL13.glActiveTexture(33985);
		GL13.glClientActiveTexture(33985);
		GL11.glTexCoordPointer(2, 5126, 28, 8L);
		GL11.glEnableClientState(32888);
		GL13.glActiveTexture(33984);
		GL13.glClientActiveTexture(33984);
		GL11.glTexCoordPointer(2, 5126, 28, 0L);
		GL11.glColorPointer(4, 5121, 28, 24L);
		GL11.glVertexPointer(2, 5126, 28, 16L);
		int int3 = SpriteRenderer.instance.getRenderStateIndex();
		int int4 = SpriteRenderer.instance.getRenderingPlayerIndex();
		IsoPuddles.RenderData renderData = this.renderData[int3][int4];
		int int5 = Math.min(int2 * 4, VBOs.bufferSizeVertices);
		floatBuffer.put(renderData.data, int1 * 4 * 7, int5 * 7);
		int int6 = 0;
		int int7 = 0;
		for (int int8 = 0; int8 < int5 / 4; ++int8) {
			shortBuffer.put((short)int6);
			shortBuffer.put((short)(int6 + 1));
			shortBuffer.put((short)(int6 + 2));
			shortBuffer.put((short)int6);
			shortBuffer.put((short)(int6 + 2));
			shortBuffer.put((short)(int6 + 3));
			int6 += 4;
			int7 += 6;
		}

		VBOs.unmap();
		byte byte1 = 0;
		byte byte2 = 0;
		GL12.glDrawRangeElements(4, byte1, byte1 + int6, int7 - byte2, 5123, (long)(byte2 * 2));
		return int5 / 4;
	}

	public void update(ClimateManager climateManager) {
		this.PuddlesWindAngle = climateManager.getCorrectedWindAngleIntensity();
		this.PuddlesWindIntensity = climateManager.getWindIntensity();
		this.rain.setFinalValue(climateManager.getRainIntensity());
		float float1 = GameTime.getInstance().getMultiplier() / 1.6F;
		float float2 = 2.0E-5F * float1 * climateManager.getTemperature();
		float float3 = 2.0E-5F * float1;
		float float4 = 2.0E-4F * float1;
		float float5 = this.rain.getFinalValue();
		float5 = float5 * float5 * 0.05F * float1;
		this.rainIntensity.setFinalValue(this.rain.getFinalValue() * 2.0F);
		this.wetGround.addFinalValue(float5);
		this.muddyPuddles.addFinalValue(float5 * 2.0F);
		this.puddlesSize.addFinalValueForMax(float5 * 0.01F, 0.7F);
		if ((double)float5 == 0.0) {
			this.wetGround.addFinalValue(-float2);
			this.muddyPuddles.addFinalValue(-float4);
		}

		if ((double)this.wetGround.getFinalValue() == 0.0) {
			this.puddlesSize.addFinalValue(-float3);
		}

		this.PuddlesTime += 0.0166F * GameTime.getInstance().getMultiplier();
		this.PuddlesParamWindINT.add((float)Math.sin((double)(this.PuddlesWindAngle * 6.0F)) * this.PuddlesWindIntensity * 0.05F, (float)Math.cos((double)(this.PuddlesWindAngle * 6.0F)) * this.PuddlesWindIntensity * 0.05F);
	}

	public float getShaderTime() {
		return this.PuddlesTime;
	}

	public float getPuddlesSize() {
		return this.puddlesSize.getFinalValue();
	}

	public ITexture getHMTexture() {
		return this.texHM;
	}

	public FloatBuffer getPuddlesParams(int int1) {
		this.floatBuffer.clear();
		this.floatBuffer.put(this.PuddlesParamWindINT.x);
		this.floatBuffer.put(this.muddyPuddles.getFinalValue());
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(this.PuddlesParamWindINT.y);
		this.floatBuffer.put(this.wetGround.getFinalValue());
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(this.PuddlesWindIntensity * 1.0F);
		this.floatBuffer.put(this.puddlesSize.getFinalValue());
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put((float)int1);
		this.floatBuffer.put(this.rainIntensity.getFinalValue());
		this.floatBuffer.put(0.0F);
		this.floatBuffer.put(0.0F);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}

	public float getRainIntensity() {
		return this.rainIntensity.getFinalValue();
	}

	public int getFloatMax() {
		return 5;
	}

	public int getBoolMax() {
		return 0;
	}

	public IsoPuddles.PuddlesFloat getPuddlesFloat(int int1) {
		if (int1 >= 0 && int1 < 5) {
			return this.climateFloats[int1];
		} else {
			DebugLog.log("ERROR: Climate: cannot get float override id.");
			return null;
		}
	}

	private IsoPuddles.PuddlesFloat initClimateFloat(int int1, String string) {
		if (int1 >= 0 && int1 < 5) {
			return this.climateFloats[int1].init(int1, string);
		} else {
			DebugLog.log("ERROR: Climate: cannot get float override id.");
			return null;
		}
	}

	private void setup() {
		for (int int1 = 0; int1 < this.climateFloats.length; ++int1) {
			this.climateFloats[int1] = new IsoPuddles.PuddlesFloat();
		}

		this.rain = this.initClimateFloat(0, "INPUT: RAIN");
		this.wetGround = this.initClimateFloat(1, "Wet Ground");
		this.muddyPuddles = this.initClimateFloat(2, "Muddy Puddles");
		this.puddlesSize = this.initClimateFloat(3, "Puddles Size");
		this.rainIntensity = this.initClimateFloat(4, "Rain Intensity");
	}

	private static final class RenderData {
		final int[] squaresPerLevel = new int[8];
		int numSquares;
		int capacity = 512;
		float[] data;

		RenderData() {
		}

		void clear() {
			this.numSquares = 0;
			Arrays.fill(this.squaresPerLevel, 0);
		}

		void addSquare(int int1, IsoPuddlesGeometry puddlesGeometry) {
			byte byte1 = 4;
			if (this.data == null) {
				this.data = new float[this.capacity * byte1 * 7];
			}

			if (this.numSquares + 1 > this.capacity) {
				this.capacity += 128;
				this.data = Arrays.copyOf(this.data, this.capacity * byte1 * 7);
			}

			int int2 = this.numSquares * byte1 * 7;
			for (int int3 = 0; int3 < 4; ++int3) {
				this.data[int2++] = puddlesGeometry.pdne[int3];
				this.data[int2++] = puddlesGeometry.pdnw[int3];
				this.data[int2++] = puddlesGeometry.pda[int3];
				this.data[int2++] = puddlesGeometry.pnon[int3];
				this.data[int2++] = puddlesGeometry.x[int3];
				this.data[int2++] = puddlesGeometry.y[int3];
				this.data[int2++] = Float.intBitsToFloat(puddlesGeometry.color[int3]);
			}

			++this.numSquares;
			int int4 = this.squaresPerLevel[int1]++;
		}
	}

	public static class PuddlesFloat {
		protected float finalValue;
		private boolean isAdminOverride = false;
		private float adminValue;
		private float min = 0.0F;
		private float max = 1.0F;
		private float delta = 0.01F;
		private int ID;
		private String name;

		public IsoPuddles.PuddlesFloat init(int int1, String string) {
			this.ID = int1;
			this.name = string;
			return this;
		}

		public int getID() {
			return this.ID;
		}

		public String getName() {
			return this.name;
		}

		public float getMin() {
			return this.min;
		}

		public float getMax() {
			return this.max;
		}

		public void setEnableAdmin(boolean boolean1) {
			this.isAdminOverride = boolean1;
		}

		public boolean isEnableAdmin() {
			return this.isAdminOverride;
		}

		public void setAdminValue(float float1) {
			this.adminValue = Math.max(this.min, Math.min(this.max, float1));
		}

		public float getAdminValue() {
			return this.adminValue;
		}

		public void setFinalValue(float float1) {
			this.finalValue = Math.max(this.min, Math.min(this.max, float1));
		}

		public void addFinalValue(float float1) {
			this.finalValue = Math.max(this.min, Math.min(this.max, this.finalValue + float1));
		}

		public void addFinalValueForMax(float float1, float float2) {
			this.finalValue = Math.max(this.min, Math.min(float2, this.finalValue + float1));
		}

		public float getFinalValue() {
			return this.isAdminOverride ? this.adminValue : this.finalValue;
		}

		public void interpolateFinalValue(float float1) {
			if (Math.abs(this.finalValue - float1) < this.delta) {
				this.finalValue = float1;
			} else if (float1 > this.finalValue) {
				this.finalValue += this.delta;
			} else {
				this.finalValue -= this.delta;
			}
		}

		private void calculate() {
			if (this.isAdminOverride) {
				this.finalValue = this.adminValue;
			}
		}
	}
}
