package zombie.iso;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import zombie.GameTime;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.interfaces.ITexture;
import zombie.iso.weather.ClimateManager;


public final class IsoWater {
	public Shader Effect;
	private float WaterTime;
	private float WaterWindAngle;
	private float WaterWindIntensity;
	private float WaterRainIntensity;
	private Vector2f WaterParamWindINT;
	private Texture texBottom = Texture.getSharedTexture("media/textures/river_bottom.png");
	private int apiId;
	private static IsoWater instance;
	private static boolean isShaderEnable = false;
	private final IsoWater.RenderData[][] renderData = new IsoWater.RenderData[3][4];
	private final IsoWater.RenderData[][] renderDataShore = new IsoWater.RenderData[3][4];
	static final int BYTES_PER_FLOAT = 4;
	static final int FLOATS_PER_VERTEX = 7;
	static final int BYTES_PER_VERTEX = 28;
	static final int VERTICES_PER_SQUARE = 4;
	private final Vector4f shaderOffset = new Vector4f();

	public static synchronized IsoWater getInstance() {
		if (instance == null) {
			instance = new IsoWater();
		}

		return instance;
	}

	public boolean getShaderEnable() {
		return isShaderEnable;
	}

	public IsoWater() {
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
		for (int int1 = 0; int1 < this.renderData.length; ++int1) {
			for (int int2 = 0; int2 < 4; ++int2) {
				this.renderData[int1][int2] = new IsoWater.RenderData();
				this.renderDataShore[int1][int2] = new IsoWater.RenderData();
			}
		}

		this.applyWaterQuality();
		this.WaterParamWindINT = new Vector2f(0.0F);
	}

	public void applyWaterQuality() {
		if (PerformanceSettings.WaterQuality == 2) {
			isShaderEnable = false;
		}

		if (PerformanceSettings.WaterQuality == 1) {
			isShaderEnable = true;
			RenderThread.invokeOnRenderContext(()->{
				ARBShaderObjects.glUseProgramObjectARB(0);
				this.Effect = new WaterShader("water");
				ARBShaderObjects.glUseProgramObjectARB(0);
			});
		}

		if (PerformanceSettings.WaterQuality == 0) {
			isShaderEnable = true;
			RenderThread.invokeOnRenderContext(()->{
				this.Effect = new WaterShader("water_hq");
				this.Effect.Start();
				this.Effect.End();
			});
		}
	}

	public void render(ArrayList arrayList, boolean boolean1) {
		if (this.getShaderEnable()) {
			int int1 = IsoCamera.frameState.playerIndex;
			int int2 = SpriteRenderer.instance.getMainStateIndex();
			IsoWater.RenderData renderData = this.renderData[int2][int1];
			IsoWater.RenderData renderData2 = this.renderDataShore[int2][int1];
			if (boolean1) {
				if (renderData2.numSquares > 0) {
					SpriteRenderer.instance.drawWater(this.Effect, int1, this.apiId, true);
				}
			} else {
				renderData.clear();
				renderData2.clear();
				for (int int3 = 0; int3 < arrayList.size(); ++int3) {
					IsoGridSquare square = (IsoGridSquare)arrayList.get(int3);
					if (square.chunk == null || !square.chunk.bLightingNeverDone[int1]) {
						IsoWaterGeometry waterGeometry = square.getWater();
						if (waterGeometry != null) {
							if (waterGeometry.bShore) {
								renderData2.addSquare(waterGeometry);
							} else if (waterGeometry.hasWater) {
								renderData.addSquare(waterGeometry);
							}
						}
					}
				}

				if (renderData.numSquares != 0) {
					SpriteRenderer.instance.drawWater(this.Effect, int1, this.apiId, false);
				}
			}
		}
	}

	public void waterProjection() {
		int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int1);
		GL11.glOrtho((double)playerCamera.getOffX(), (double)(playerCamera.getOffX() + (float)playerCamera.OffscreenWidth), (double)(playerCamera.getOffY() + (float)playerCamera.OffscreenHeight), (double)playerCamera.getOffY(), -1.0, 1.0);
	}

	public void waterGeometry(boolean boolean1) {
		long long1 = System.nanoTime();
		int int1 = SpriteRenderer.instance.getRenderStateIndex();
		int int2 = SpriteRenderer.instance.getRenderingPlayerIndex();
		IsoWater.RenderData renderData = boolean1 ? this.renderDataShore[int1][int2] : this.renderData[int1][int2];
		int int3 = 0;
		int int4;
		for (int int5 = renderData.numSquares; int5 > 0; int5 -= int4) {
			int4 = this.renderSome(int3, int5, boolean1);
			int3 += int4;
		}

		long long2 = System.nanoTime();
		SpriteRenderer.ringBuffer.restoreVBOs = true;
	}

	private int renderSome(int int1, int int2, boolean boolean1) {
		IsoPuddles.VBOs.next();
		FloatBuffer floatBuffer = IsoPuddles.VBOs.vertices;
		ShortBuffer shortBuffer = IsoPuddles.VBOs.indices;
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
		IsoWater.RenderData renderData = boolean1 ? this.renderDataShore[int3][int4] : this.renderData[int3][int4];
		int int5 = Math.min(int2 * 4, IsoPuddles.VBOs.bufferSizeVertices);
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

		IsoPuddles.VBOs.unmap();
		byte byte1 = 0;
		byte byte2 = 0;
		GL12.glDrawRangeElements(4, byte1, byte1 + int6, int7 - byte2, 5123, (long)(byte2 * 2));
		return int5 / 4;
	}

	public ITexture getTextureBottom() {
		return this.texBottom;
	}

	public float getShaderTime() {
		return this.WaterTime;
	}

	public float getRainIntensity() {
		return this.WaterRainIntensity;
	}

	public void update(ClimateManager climateManager) {
		this.WaterWindAngle = climateManager.getCorrectedWindAngleIntensity();
		this.WaterWindIntensity = climateManager.getWindIntensity() * 5.0F;
		this.WaterRainIntensity = climateManager.getRainIntensity();
		this.WaterTime += 0.0166F * GameTime.getInstance().getMultiplier();
		this.WaterParamWindINT.add((float)Math.sin((double)(this.WaterWindAngle * 6.0F)) * this.WaterWindIntensity * 0.05F, (float)Math.cos((double)(this.WaterWindAngle * 6.0F)) * this.WaterWindIntensity * 0.15F);
	}

	public float getWaterWindX() {
		return this.WaterParamWindINT.x;
	}

	public float getWaterWindY() {
		return this.WaterParamWindINT.y;
	}

	public float getWaterWindSpeed() {
		return this.WaterWindIntensity * 2.0F;
	}

	public Vector4f getShaderOffset() {
		int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int1);
		return this.shaderOffset.set(playerCamera.getOffX() - (float)IsoCamera.getOffscreenLeft(int1) * playerCamera.zoom, playerCamera.getOffY() + (float)IsoCamera.getOffscreenTop(int1) * playerCamera.zoom, (float)playerCamera.OffscreenWidth, (float)playerCamera.OffscreenHeight);
	}

	public void FBOStart() {
		int int1 = IsoCamera.frameState.playerIndex;
	}

	public void FBOEnd() {
		int int1 = IsoCamera.frameState.playerIndex;
	}

	private static final class RenderData {
		int numSquares;
		int capacity = 512;
		float[] data;

		void clear() {
			this.numSquares = 0;
		}

		void addSquare(IsoWaterGeometry waterGeometry) {
			int int1 = IsoCamera.frameState.playerIndex;
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
				this.data[int2++] = waterGeometry.depth[int3];
				this.data[int2++] = waterGeometry.flow[int3];
				this.data[int2++] = waterGeometry.speed[int3];
				this.data[int2++] = waterGeometry.IsExternal;
				this.data[int2++] = waterGeometry.x[int3];
				this.data[int2++] = waterGeometry.y[int3];
				if (waterGeometry.square != null) {
					int int4 = waterGeometry.square.getVertLight((4 - int3) % 4, int1);
					this.data[int2++] = Float.intBitsToFloat(int4);
				} else {
					++int2;
				}
			}

			++this.numSquares;
		}
	}
}
