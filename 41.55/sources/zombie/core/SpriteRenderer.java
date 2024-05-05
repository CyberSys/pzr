package zombie.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import zombie.GameProfiler;
import zombie.asset.Asset;
import zombie.core.Styles.AbstractStyle;
import zombie.core.Styles.AdditiveStyle;
import zombie.core.Styles.AlphaOp;
import zombie.core.Styles.LightingStyle;
import zombie.core.Styles.Style;
import zombie.core.Styles.TransparentStyle;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.math.PZMath;
import zombie.core.opengl.GLState;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.sprite.SpriteRendererStates;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoPuddles;
import zombie.iso.PlayerCamera;
import zombie.util.list.PZArrayUtil;


public final class SpriteRenderer {
	public static final SpriteRenderer instance = new SpriteRenderer();
	static final int VERTEX_SIZE = 32;
	static final int TEXTURE0_COORD_OFFSET = 8;
	static final int TEXTURE1_COORD_OFFSET = 16;
	static final int TEXTURE2_ATTRIB_OFFSET = 24;
	static final int COLOR_OFFSET = 28;
	public static final SpriteRenderer.RingBuffer ringBuffer = new SpriteRenderer.RingBuffer();
	public static final int NUM_RENDER_STATES = 3;
	public final SpriteRendererStates m_states = new SpriteRendererStates();
	private volatile boolean m_waitingForRenderState = false;
	public static boolean GL_BLENDFUNC_ENABLED = true;

	public void create() {
		ringBuffer.create();
	}

	public void clearSprites() {
		this.m_states.getPopulating().clear();
	}

	public void glDepthMask(boolean boolean1) {
		this.m_states.getPopulatingActiveState().glDepthMask(boolean1);
	}

	public void renderflipped(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		this.m_states.getPopulatingActiveState().renderflipped(texture, float1, float2, float3, float4, float5, float6, float7, float8, consumer);
	}

	public void drawModel(ModelManager.ModelSlot modelSlot) {
		this.m_states.getPopulatingActiveState().drawModel(modelSlot);
	}

	public void drawSkyBox(Shader shader, int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().drawSkyBox(shader, int1, int2, int3);
	}

	public void drawWater(Shader shader, int int1, int int2, boolean boolean1) {
		this.m_states.getPopulatingActiveState().drawWater(shader, int1, int2, boolean1);
	}

	public void drawPuddles(Shader shader, int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().drawPuddles(shader, int1, int2, int3);
	}

	public void drawParticles(int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().drawParticles(int1, int2, int3);
	}

	public void drawGeneric(TextureDraw.GenericDrawer genericDrawer) {
		this.m_states.getPopulatingActiveState().drawGeneric(genericDrawer);
	}

	public void glDisable(int int1) {
		this.m_states.getPopulatingActiveState().glDisable(int1);
	}

	public void glEnable(int int1) {
		this.m_states.getPopulatingActiveState().glEnable(int1);
	}

	public void glStencilMask(int int1) {
		this.m_states.getPopulatingActiveState().glStencilMask(int1);
	}

	public void glClear(int int1) {
		this.m_states.getPopulatingActiveState().glClear(int1);
	}

	public void glClearColor(int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().glClearColor(int1, int2, int3, int4);
	}

	public void glStencilFunc(int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().glStencilFunc(int1, int2, int3);
	}

	public void glStencilOp(int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().glStencilOp(int1, int2, int3);
	}

	public void glColorMask(int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().glColorMask(int1, int2, int3, int4);
	}

	public void glAlphaFunc(int int1, float float1) {
		this.m_states.getPopulatingActiveState().glAlphaFunc(int1, float1);
	}

	public void glBlendFunc(int int1, int int2) {
		this.m_states.getPopulatingActiveState().glBlendFunc(int1, int2);
	}

	public void glBlendFuncSeparate(int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().glBlendFuncSeparate(int1, int2, int3, int4);
	}

	public void glBlendEquation(int int1) {
		this.m_states.getPopulatingActiveState().glBlendEquation(int1);
	}

	public void render(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, float float1, float float2, float float3, float float4, Consumer consumer) {
		this.m_states.getPopulatingActiveState().render(texture, double1, double2, double3, double4, double5, double6, double7, double8, float1, float2, float3, float4, consumer);
	}

	public void render(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Consumer consumer) {
		this.m_states.getPopulatingActiveState().render(texture, double1, double2, double3, double4, double5, double6, double7, double8, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
	}

	public void renderdebug(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19, float float20, float float21, float float22, float float23, float float24, Consumer consumer) {
		this.m_states.getPopulatingActiveState().renderdebug(texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, float17, float18, float19, float20, float21, float22, float23, float24, consumer);
	}

	public void renderline(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, int int5) {
		this.m_states.getPopulatingActiveState().renderline(texture, (float)int1, (float)int2, (float)int3, (float)int4, float1, float2, float3, float4, int5);
	}

	public void renderline(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		this.m_states.getPopulatingActiveState().renderline(texture, int1, int2, int3, int4, float1, float2, float3, float4);
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().render(texture, float1, float2, float3, float4, float5, float6, float7, float8, int1, int2, int3, int4);
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		float float9 = PZMath.floor(float1);
		float float10 = PZMath.floor(float2);
		float float11 = PZMath.ceil(float1 + float3);
		float float12 = PZMath.ceil(float2 + float4);
		this.m_states.getPopulatingActiveState().render(texture, float9, float10, float11 - float9, float12 - float10, float5, float6, float7, float8, consumer);
	}

	public void renderi(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, Consumer consumer) {
		this.m_states.getPopulatingActiveState().render(texture, (float)int1, (float)int2, (float)int3, (float)int4, float1, float2, float3, float4, consumer);
	}

	public void renderClamped(Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4, Consumer consumer) {
		int int9 = PZMath.clamp(int1, int5, int5 + int7);
		int int10 = PZMath.clamp(int2, int6, int6 + int8);
		int int11 = PZMath.clamp(int1 + int3, int5, int5 + int7);
		int int12 = PZMath.clamp(int2 + int4, int6, int6 + int8);
		if (int9 != int11 && int10 != int12) {
			int int13 = int9 - int1;
			int int14 = int1 + int3 - int11;
			int int15 = int10 - int2;
			int int16 = int2 + int4 - int12;
			if (int13 == 0 && int14 == 0 && int15 == 0 && int16 == 0) {
				this.m_states.getPopulatingActiveState().render(texture, (float)int1, (float)int2, (float)int3, (float)int4, float1, float2, float3, float4, consumer);
			} else {
				float float5 = 0.0F;
				float float6 = 0.0F;
				float float7 = 1.0F;
				float float8 = 0.0F;
				float float9 = 1.0F;
				float float10 = 1.0F;
				float float11 = 0.0F;
				float float12 = 1.0F;
				if (texture != null) {
					float5 = (float)int13 / (float)int3;
					float6 = (float)int15 / (float)int4;
					float7 = (float)(int3 - int14) / (float)int3;
					float8 = (float)int15 / (float)int4;
					float9 = (float)(int3 - int14) / (float)int3;
					float10 = (float)(int4 - int16) / (float)int4;
					float11 = (float)int13 / (float)int3;
					float12 = (float)(int4 - int16) / (float)int4;
				}

				int3 = int11 - int9;
				int4 = int12 - int10;
				this.m_states.getPopulatingActiveState().render(texture, (float)int9, (float)int10, (float)int3, (float)int4, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, consumer);
			}
		}
	}

	public void renderRect(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		this.m_states.getPopulatingActiveState().renderRect(int1, int2, int3, int4, float1, float2, float3, float4);
	}

	public void renderPoly(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		this.m_states.getPopulatingActiveState().renderPoly(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
	}

	public void renderPoly(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		this.m_states.getPopulatingActiveState().renderPoly(texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
	}

	public void renderPoly(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19, float float20) {
		this.m_states.getPopulatingActiveState().renderPoly(texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, float17, float18, float19, float20);
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		this.m_states.getPopulatingActiveState().render(texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, (Consumer)null);
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Consumer consumer) {
		this.m_states.getPopulatingActiveState().render(texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
	}

	private static void buildDrawBuffer(TextureDraw[] textureDrawArray, Style[] styleArray, int int1, SpriteRenderer.RingBuffer ringBuffer) {
		for (int int2 = 0; int2 < int1; ++int2) {
			TextureDraw textureDraw = textureDrawArray[int2];
			Style style = styleArray[int2];
			TextureDraw textureDraw2 = null;
			if (int2 > 0) {
				textureDraw2 = textureDrawArray[int2 - 1];
			}

			ringBuffer.add(textureDraw, textureDraw2, style);
		}
	}

	public void prePopulating() {
		this.m_states.getPopulating().prePopulating();
	}

	public void postRender() {
		SpriteRenderState spriteRenderState = this.m_states.getRendering();
		if (spriteRenderState.numSprites == 0 && spriteRenderState.stateUI.numSprites == 0) {
			spriteRenderState.onRendered();
		} else {
			TextureFBO.reset();
			IsoPuddles.VBOs.startFrame();
			GameProfiler.getInstance().invokeAndMeasure("buildStateUIDrawBuffer(UI)", this, spriteRenderState, SpriteRenderer::buildStateUIDrawBuffer);
			GameProfiler.getInstance().invokeAndMeasure("buildStateDrawBuffer", this, spriteRenderState, SpriteRenderer::buildStateDrawBuffer);
			spriteRenderState.onRendered();
			Core.getInstance().setLastRenderedFBO(spriteRenderState.fbo);
			this.notifyRenderStateQueue();
		}
	}

	protected void buildStateDrawBuffer(SpriteRenderState spriteRenderState) {
		ringBuffer.begin();
		buildDrawBuffer(spriteRenderState.sprite, spriteRenderState.style, spriteRenderState.numSprites, ringBuffer);
		GameProfiler.getInstance().invokeAndMeasure("ringBuffer.render", ()->{
			ringBuffer.render();
		});
	}

	protected void buildStateUIDrawBuffer(SpriteRenderState spriteRenderState) {
		if (spriteRenderState.stateUI.numSprites > 0) {
			ringBuffer.begin();
			spriteRenderState.stateUI.bActive = true;
			buildDrawBuffer(spriteRenderState.stateUI.sprite, spriteRenderState.stateUI.style, spriteRenderState.stateUI.numSprites, ringBuffer);
			ringBuffer.render();
		}

		spriteRenderState.stateUI.bActive = false;
	}

	public void notifyRenderStateQueue() {
		synchronized (this.m_states) {
			this.m_states.notifyAll();
		}
	}

	public void glBuffer(int int1, int int2) {
		this.m_states.getPopulatingActiveState().glBuffer(int1, int2);
	}

	public void glDoStartFrame(int int1, int int2, float float1, int int3) {
		this.m_states.getPopulatingActiveState().glDoStartFrame(int1, int2, float1, int3);
	}

	public void glDoStartFrame(int int1, int int2, float float1, int int3, boolean boolean1) {
		this.m_states.getPopulatingActiveState().glDoStartFrame(int1, int2, float1, int3, boolean1);
	}

	public void glDoStartFrameFx(int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().glDoStartFrameFx(int1, int2, int3);
	}

	public void glIgnoreStyles(boolean boolean1) {
		this.m_states.getPopulatingActiveState().glIgnoreStyles(boolean1);
	}

	public void glDoEndFrame() {
		this.m_states.getPopulatingActiveState().glDoEndFrame();
	}

	public void glDoEndFrameFx(int int1) {
		this.m_states.getPopulatingActiveState().glDoEndFrameFx(int1);
	}

	public void doCoreIntParam(int int1, float float1) {
		this.m_states.getPopulatingActiveState().doCoreIntParam(int1, float1);
	}

	public void glTexParameteri(int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().glTexParameteri(int1, int2, int3);
	}

	public void StartShader(int int1, int int2) {
		this.m_states.getPopulatingActiveState().StartShader(int1, int2);
	}

	public void EndShader() {
		this.m_states.getPopulatingActiveState().EndShader();
	}

	public void setCutawayTexture(Texture texture, int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().setCutawayTexture(texture, int1, int2, int3, int4);
	}

	public void clearCutawayTexture() {
		this.m_states.getPopulatingActiveState().clearCutawayTexture();
	}

	public void setUseVertColorsArray(byte byte1, int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().setUseVertColorsArray(byte1, int1, int2, int3, int4);
	}

	public void clearUseVertColorsArray() {
		this.m_states.getPopulatingActiveState().clearUseVertColorsArray();
	}

	public void setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender wallShaderTexRender) {
		this.m_states.getPopulatingActiveState().setExtraWallShaderParams(wallShaderTexRender);
	}

	public void ShaderUpdate1i(int int1, int int2, int int3) {
		this.m_states.getPopulatingActiveState().ShaderUpdate1i(int1, int2, int3);
	}

	public void ShaderUpdate1f(int int1, int int2, float float1) {
		this.m_states.getPopulatingActiveState().ShaderUpdate1f(int1, int2, float1);
	}

	public void ShaderUpdate2f(int int1, int int2, float float1, float float2) {
		this.m_states.getPopulatingActiveState().ShaderUpdate2f(int1, int2, float1, float2);
	}

	public void ShaderUpdate3f(int int1, int int2, float float1, float float2, float float3) {
		this.m_states.getPopulatingActiveState().ShaderUpdate3f(int1, int2, float1, float2, float3);
	}

	public void ShaderUpdate4f(int int1, int int2, float float1, float float2, float float3, float float4) {
		this.m_states.getPopulatingActiveState().ShaderUpdate4f(int1, int2, float1, float2, float3, float4);
	}

	public void glLoadIdentity() {
		this.m_states.getPopulatingActiveState().glLoadIdentity();
	}

	public void glGenerateMipMaps(int int1) {
		this.m_states.getPopulatingActiveState().glGenerateMipMaps(int1);
	}

	public void glBind(int int1) {
		this.m_states.getPopulatingActiveState().glBind(int1);
	}

	public void glViewport(int int1, int int2, int int3, int int4) {
		this.m_states.getPopulatingActiveState().glViewport(int1, int2, int3, int4);
	}

	public void startOffscreenUI() {
		this.m_states.getPopulating().stateUI.bActive = true;
		this.m_states.getPopulating().stateUI.defaultStyle = TransparentStyle.instance;
		GLState.startFrame();
	}

	public void stopOffscreenUI() {
		this.m_states.getPopulating().stateUI.bActive = false;
	}

	public void pushFrameDown() {
		synchronized (this.m_states) {
			this.waitForReadySlotToOpen();
			this.m_states.movePopulatingToReady();
			this.notifyRenderStateQueue();
		}
	}

	public SpriteRenderState acquireStateForRendering(BooleanSupplier booleanSupplier) {
		synchronized (this.m_states) {
			if (!this.waitForReadyState(booleanSupplier)) {
				return null;
			} else {
				this.m_states.moveReadyToRendering();
				this.notifyRenderStateQueue();
				return this.m_states.getRendering();
			}
		}
	}

	private boolean waitForReadyState(BooleanSupplier booleanSupplier) {
		boolean boolean1;
		try {
			SpriteRenderer.s_performance.waitForReadyState.start();
			boolean1 = this.waitForReadyStateInternal(booleanSupplier);
		} finally {
			SpriteRenderer.s_performance.waitForReadyState.end();
		}

		return boolean1;
	}

	private boolean waitForReadyStateInternal(BooleanSupplier booleanSupplier) {
		if (RenderThread.isRunning() && this.m_states.getReady() == null) {
			if (!RenderThread.isWaitForRenderState() && !this.isWaitingForRenderState()) {
				return false;
			} else {
				while (this.m_states.getReady() == null) {
					try {
						if (!booleanSupplier.getAsBoolean()) {
							return false;
						}

						this.m_states.wait();
					} catch (InterruptedException interruptedException) {
					}
				}

				return true;
			}
		} else {
			return true;
		}
	}

	private void waitForReadySlotToOpen() {
		try {
			SpriteRenderer.s_performance.waitForReadySlotToOpen.start();
			this.waitForReadySlotToOpenInternal();
		} finally {
			SpriteRenderer.s_performance.waitForReadySlotToOpen.end();
		}
	}

	private void waitForReadySlotToOpenInternal() {
		if (this.m_states.getReady() != null && RenderThread.isRunning()) {
			this.m_waitingForRenderState = true;
			while (this.m_states.getReady() != null) {
				try {
					this.m_states.wait();
				} catch (InterruptedException interruptedException) {
				}
			}

			this.m_waitingForRenderState = false;
		}
	}

	public int getMainStateIndex() {
		return this.m_states.getPopulatingActiveState().index;
	}

	public int getRenderStateIndex() {
		return this.m_states.getRenderingActiveState().index;
	}

	public boolean getDoAdditive() {
		return this.m_states.getPopulatingActiveState().defaultStyle == AdditiveStyle.instance;
	}

	public void setDefaultStyle(AbstractStyle abstractStyle) {
		this.m_states.getPopulatingActiveState().defaultStyle = abstractStyle;
	}

	public void setDoAdditive(boolean boolean1) {
		this.m_states.getPopulatingActiveState().defaultStyle = (AbstractStyle)(boolean1 ? AdditiveStyle.instance : TransparentStyle.instance);
	}

	public void initFromIsoCamera(int int1) {
		this.m_states.getPopulating().playerCamera[int1].initFromIsoCamera(int1);
	}

	public void setRenderingPlayerIndex(int int1) {
		this.m_states.getRendering().playerIndex = int1;
	}

	public int getRenderingPlayerIndex() {
		return this.m_states.getRendering().playerIndex;
	}

	public PlayerCamera getRenderingPlayerCamera(int int1) {
		return this.m_states.getRendering().playerCamera[int1];
	}

	public SpriteRenderState getRenderingState() {
		return this.m_states.getRendering();
	}

	public SpriteRenderState getPopulatingState() {
		return this.m_states.getPopulating();
	}

	public boolean isMaxZoomLevel() {
		return this.getPlayerZoomLevel() >= this.getPlayerMaxZoom();
	}

	public boolean isMinZoomLevel() {
		return this.getPlayerZoomLevel() <= this.getPlayerMinZoom();
	}

	public float getPlayerZoomLevel() {
		SpriteRenderState spriteRenderState = this.m_states.getRendering();
		int int1 = spriteRenderState.playerIndex;
		return spriteRenderState.zoomLevel[int1];
	}

	public float getPlayerMaxZoom() {
		SpriteRenderState spriteRenderState = this.m_states.getRendering();
		return spriteRenderState.maxZoomLevel;
	}

	public float getPlayerMinZoom() {
		SpriteRenderState spriteRenderState = this.m_states.getRendering();
		return spriteRenderState.minZoomLevel;
	}

	public boolean isWaitingForRenderState() {
		return this.m_waitingForRenderState;
	}

	public static final class RingBuffer {
		GLVertexBufferObject[] vbo;
		GLVertexBufferObject[] ibo;
		long bufferSize;
		long bufferSizeInVertices;
		long indexBufferSize;
		int numBuffers;
		int sequence = -1;
		int mark = -1;
		FloatBuffer currentVertices;
		ShortBuffer currentIndices;
		FloatBuffer[] vertices;
		ByteBuffer[] verticesBytes;
		ShortBuffer[] indices;
		ByteBuffer[] indicesBytes;
		Texture lastRenderedTexture0;
		Texture currentTexture0;
		Texture lastRenderedTexture1;
		Texture currentTexture1;
		boolean shaderChangedTexture1 = false;
		byte lastUseAttribArray;
		byte currentUseAttribArray;
		Style lastRenderedStyle;
		Style currentStyle;
		SpriteRenderer.RingBuffer.StateRun[] stateRun;
		public boolean restoreVBOs;
		public boolean restoreBoundTextures;
		int vertexCursor;
		int indexCursor;
		int numRuns;
		SpriteRenderer.RingBuffer.StateRun currentRun;
		public static boolean IGNORE_STYLES = false;

		RingBuffer() {
		}

		void create() {
			GL11.glEnableClientState(32884);
			GL11.glEnableClientState(32886);
			GL11.glEnableClientState(32888);
			this.bufferSize = 65536L;
			this.numBuffers = 128;
			this.bufferSizeInVertices = this.bufferSize / 32L;
			this.indexBufferSize = this.bufferSizeInVertices * 3L;
			this.vertices = new FloatBuffer[this.numBuffers];
			this.verticesBytes = new ByteBuffer[this.numBuffers];
			this.indices = new ShortBuffer[this.numBuffers];
			this.indicesBytes = new ByteBuffer[this.numBuffers];
			this.stateRun = new SpriteRenderer.RingBuffer.StateRun[5000];
			int int1;
			for (int1 = 0; int1 < 5000; ++int1) {
				this.stateRun[int1] = new SpriteRenderer.RingBuffer.StateRun();
			}

			this.vbo = new GLVertexBufferObject[this.numBuffers];
			this.ibo = new GLVertexBufferObject[this.numBuffers];
			for (int1 = 0; int1 < this.numBuffers; ++int1) {
				this.vbo[int1] = new GLVertexBufferObject(this.bufferSize, GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW());
				this.vbo[int1].create();
				this.ibo[int1] = new GLVertexBufferObject(this.indexBufferSize, GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), GLVertexBufferObject.funcs.GL_STREAM_DRAW());
				this.ibo[int1].create();
			}
		}

		void add(TextureDraw textureDraw, TextureDraw textureDraw2, Style style) {
			if (style != null) {
				if ((long)(this.vertexCursor + 4) > this.bufferSizeInVertices || (long)(this.indexCursor + 6) > this.indexBufferSize) {
					this.render();
					this.next();
				}

				if (this.prepareCurrentRun(textureDraw, textureDraw2, style)) {
					FloatBuffer floatBuffer = this.currentVertices;
					AlphaOp alphaOp = style.getAlphaOp();
					floatBuffer.put(textureDraw.x0);
					floatBuffer.put(textureDraw.y0);
					if (textureDraw.tex == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						if (textureDraw.flipped) {
							floatBuffer.put(textureDraw.u1);
						} else {
							floatBuffer.put(textureDraw.u0);
						}

						floatBuffer.put(textureDraw.v0);
					}

					if (textureDraw.tex1 == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						floatBuffer.put(textureDraw.tex1_u0);
						floatBuffer.put(textureDraw.tex1_v0);
					}

					floatBuffer.put(Float.intBitsToFloat(textureDraw.useAttribArray != -1 ? textureDraw.tex1_col0 : 0));
					int int1 = textureDraw.getColor(0);
					alphaOp.op(int1, 255, floatBuffer);
					floatBuffer.put(textureDraw.x1);
					floatBuffer.put(textureDraw.y1);
					if (textureDraw.tex == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						if (textureDraw.flipped) {
							floatBuffer.put(textureDraw.u0);
						} else {
							floatBuffer.put(textureDraw.u1);
						}

						floatBuffer.put(textureDraw.v1);
					}

					if (textureDraw.tex1 == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						floatBuffer.put(textureDraw.tex1_u1);
						floatBuffer.put(textureDraw.tex1_v1);
					}

					floatBuffer.put(Float.intBitsToFloat(textureDraw.useAttribArray != -1 ? textureDraw.tex1_col1 : 0));
					int1 = textureDraw.getColor(1);
					alphaOp.op(int1, 255, floatBuffer);
					floatBuffer.put(textureDraw.x2);
					floatBuffer.put(textureDraw.y2);
					if (textureDraw.tex == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						if (textureDraw.flipped) {
							floatBuffer.put(textureDraw.u3);
						} else {
							floatBuffer.put(textureDraw.u2);
						}

						floatBuffer.put(textureDraw.v2);
					}

					if (textureDraw.tex1 == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						floatBuffer.put(textureDraw.tex1_u2);
						floatBuffer.put(textureDraw.tex1_v2);
					}

					floatBuffer.put(Float.intBitsToFloat(textureDraw.useAttribArray != -1 ? textureDraw.tex1_col2 : 0));
					int1 = textureDraw.getColor(2);
					alphaOp.op(int1, 255, floatBuffer);
					floatBuffer.put(textureDraw.x3);
					floatBuffer.put(textureDraw.y3);
					if (textureDraw.tex == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						if (textureDraw.flipped) {
							floatBuffer.put(textureDraw.u2);
						} else {
							floatBuffer.put(textureDraw.u3);
						}

						floatBuffer.put(textureDraw.v3);
					}

					if (textureDraw.tex1 == null) {
						floatBuffer.put(0.0F);
						floatBuffer.put(0.0F);
					} else {
						floatBuffer.put(textureDraw.tex1_u3);
						floatBuffer.put(textureDraw.tex1_v3);
					}

					floatBuffer.put(Float.intBitsToFloat(textureDraw.useAttribArray != -1 ? textureDraw.tex1_col3 : 0));
					int1 = textureDraw.getColor(3);
					alphaOp.op(int1, 255, floatBuffer);
					this.currentIndices.put((short)this.vertexCursor);
					this.currentIndices.put((short)(this.vertexCursor + 1));
					this.currentIndices.put((short)(this.vertexCursor + 2));
					this.currentIndices.put((short)this.vertexCursor);
					this.currentIndices.put((short)(this.vertexCursor + 2));
					this.currentIndices.put((short)(this.vertexCursor + 3));
					this.indexCursor += 6;
					this.vertexCursor += 4;
					SpriteRenderer.RingBuffer.StateRun stateRun = this.currentRun;
					stateRun.endIndex += 6;
					stateRun = this.currentRun;
					stateRun.length += 4;
				}
			}
		}

		private boolean prepareCurrentRun(TextureDraw textureDraw, TextureDraw textureDraw2, Style style) {
			Texture texture = textureDraw.tex;
			Texture texture2 = textureDraw.tex1;
			byte byte1 = textureDraw.useAttribArray;
			if (this.isStateChanged(textureDraw, textureDraw2, style, texture, texture2, byte1)) {
				this.currentRun = this.stateRun[this.numRuns];
				this.currentRun.start = this.vertexCursor;
				this.currentRun.length = 0;
				this.currentRun.style = style;
				this.currentRun.texture0 = texture;
				this.currentRun.texture1 = texture2;
				this.currentRun.useAttribArray = byte1;
				this.currentRun.indices = this.currentIndices;
				this.currentRun.startIndex = this.indexCursor;
				this.currentRun.endIndex = this.indexCursor;
				++this.numRuns;
				if (this.numRuns == this.stateRun.length) {
					this.growStateRuns();
				}

				this.currentStyle = style;
				this.currentTexture0 = texture;
				this.currentTexture1 = texture2;
				this.currentUseAttribArray = byte1;
			}

			if (textureDraw.type != TextureDraw.Type.glDraw) {
				this.currentRun.ops.add(textureDraw);
				return false;
			} else {
				return true;
			}
		}

		private boolean isStateChanged(TextureDraw textureDraw, TextureDraw textureDraw2, Style style, Texture texture, Texture texture2, byte byte1) {
			if (this.currentRun == null) {
				return true;
			} else if (textureDraw.type == TextureDraw.Type.DrawModel) {
				return true;
			} else if (byte1 != this.currentUseAttribArray) {
				return true;
			} else if (texture != this.currentTexture0) {
				return true;
			} else if (texture2 != this.currentTexture1) {
				return true;
			} else {
				if (textureDraw2 != null) {
					if (textureDraw2.type == TextureDraw.Type.DrawModel) {
						return true;
					}

					if (textureDraw.type == TextureDraw.Type.glDraw && textureDraw2.type != TextureDraw.Type.glDraw) {
						return true;
					}

					if (textureDraw.type != TextureDraw.Type.glDraw && textureDraw2.type == TextureDraw.Type.glDraw) {
						return true;
					}
				}

				if (style != this.currentStyle) {
					if (this.currentStyle == null) {
						return true;
					}

					if (style.getStyleID() != this.currentStyle.getStyleID()) {
						return true;
					}
				}

				return false;
			}
		}

		private void next() {
			++this.sequence;
			if (this.sequence == this.numBuffers) {
				this.sequence = 0;
			}

			if (this.sequence == this.mark) {
				DebugLog.General.error("Buffer overrun.");
			}

			this.vbo[this.sequence].bind();
			ByteBuffer byteBuffer = this.vbo[this.sequence].map();
			if (this.vertices[this.sequence] == null || this.verticesBytes[this.sequence] != byteBuffer) {
				this.verticesBytes[this.sequence] = byteBuffer;
				this.vertices[this.sequence] = byteBuffer.asFloatBuffer();
			}

			this.ibo[this.sequence].bind();
			ByteBuffer byteBuffer2 = this.ibo[this.sequence].map();
			if (this.indices[this.sequence] == null || this.indicesBytes[this.sequence] != byteBuffer2) {
				this.indicesBytes[this.sequence] = byteBuffer2;
				this.indices[this.sequence] = byteBuffer2.asShortBuffer();
			}

			this.currentVertices = this.vertices[this.sequence];
			this.currentVertices.clear();
			this.currentIndices = this.indices[this.sequence];
			this.currentIndices.clear();
			this.vertexCursor = 0;
			this.indexCursor = 0;
			this.numRuns = 0;
			this.currentRun = null;
		}

		void begin() {
			this.currentStyle = null;
			this.currentTexture0 = null;
			this.currentTexture1 = null;
			this.currentUseAttribArray = -1;
			this.next();
			this.mark = this.sequence;
		}

		void render() {
			this.vbo[this.sequence].unmap();
			this.ibo[this.sequence].unmap();
			this.restoreVBOs = true;
			for (int int1 = 0; int1 < this.numRuns; ++int1) {
				this.stateRun[int1].render();
			}
		}

		void growStateRuns() {
			SpriteRenderer.RingBuffer.StateRun[] stateRunArray = new SpriteRenderer.RingBuffer.StateRun[(int)((float)this.stateRun.length * 1.5F)];
			System.arraycopy(this.stateRun, 0, stateRunArray, 0, this.stateRun.length);
			for (int int1 = this.numRuns; int1 < stateRunArray.length; ++int1) {
				stateRunArray[int1] = new SpriteRenderer.RingBuffer.StateRun();
			}

			this.stateRun = stateRunArray;
		}

		public void shaderChangedTexture1() {
			this.shaderChangedTexture1 = true;
		}

		public void checkShaderChangedTexture1() {
			if (this.shaderChangedTexture1) {
				this.shaderChangedTexture1 = false;
				this.lastRenderedTexture1 = null;
				GL13.glActiveTexture(33985);
				GL13.glClientActiveTexture(33985);
				GL11.glDisable(3553);
				GL13.glActiveTexture(33984);
				GL13.glClientActiveTexture(33984);
			}
		}

		public void debugBoundTexture(Texture texture, int int1) {
			if (GL11.glGetInteger(34016) == int1) {
				int int2 = GL11.glGetInteger(32873);
				String string = null;
				Iterator iterator;
				Asset asset;
				Texture texture2;
				if (texture == null && int2 != 0) {
					iterator = TextureAssetManager.instance.getAssetTable().values().iterator();
					while (iterator.hasNext()) {
						asset = (Asset)iterator.next();
						texture2 = (Texture)asset;
						if (texture2.getID() == int2) {
							string = texture2.getPath().getPath();
							break;
						}
					}

					DebugLog.General.error("SpriteRenderer.lastBoundTexture0=null doesn\'t match OpenGL texture id=" + int2 + " " + string);
				} else if (texture != null && texture.getID() != -1 && int2 != texture.getID()) {
					iterator = TextureAssetManager.instance.getAssetTable().values().iterator();
					while (iterator.hasNext()) {
						asset = (Asset)iterator.next();
						texture2 = (Texture)asset;
						if (texture2.getID() == int2) {
							string = texture2.getName();
							break;
						}
					}

					DebugLog.General.error("SpriteRenderer.lastBoundTexture0 id=" + texture.getID() + " doesn\'t match OpenGL texture id=" + int2 + " " + string);
				}
			}
		}

		private class StateRun {
			Texture texture0;
			Texture texture1;
			byte useAttribArray = -1;
			Style style;
			int start;
			int length;
			ShortBuffer indices;
			int startIndex;
			int endIndex;
			final ArrayList ops = new ArrayList();

			public String toString() {
				String string = System.lineSeparator();
				String string2 = this.getClass().getSimpleName();
				return string2 + "{ " + string + "  ops:" + PZArrayUtil.arrayToString((Iterable)this.ops, "{", "}", ", ") + string + "  texture0:" + this.texture0 + string + "  texture1:" + this.texture1 + string + "  useAttribArray:" + this.useAttribArray + string + "  style:" + this.style + string + "  start:" + this.start + string + "  length:" + this.length + string + "  indices:" + this.indices + string + "  startIndex:" + this.startIndex + string + "  endIndex:" + this.endIndex + string + "}";
			}

			void render() {
				if (this.style != null) {
					int int1 = this.ops.size();
					int int2;
					if (int1 > 0) {
						for (int2 = 0; int2 < int1; ++int2) {
							((TextureDraw)this.ops.get(int2)).run();
						}

						this.ops.clear();
					} else {
						if (this.style != RingBuffer.this.lastRenderedStyle) {
							if (RingBuffer.this.lastRenderedStyle != null && (!SpriteRenderer.RingBuffer.IGNORE_STYLES || RingBuffer.this.lastRenderedStyle != AdditiveStyle.instance && RingBuffer.this.lastRenderedStyle != TransparentStyle.instance && RingBuffer.this.lastRenderedStyle != LightingStyle.instance)) {
								RingBuffer.this.lastRenderedStyle.resetState();
							}

							if (this.style != null && (!SpriteRenderer.RingBuffer.IGNORE_STYLES || this.style != AdditiveStyle.instance && this.style != TransparentStyle.instance && this.style != LightingStyle.instance)) {
								this.style.setupState();
							}

							RingBuffer.this.lastRenderedStyle = this.style;
						}

						if (RingBuffer.this.lastRenderedTexture0 != null && RingBuffer.this.lastRenderedTexture0.getID() != Texture.lastTextureID) {
							RingBuffer.this.restoreBoundTextures = true;
						}

						if (RingBuffer.this.restoreBoundTextures) {
							Texture.lastTextureID = 0;
							GL11.glBindTexture(3553, 0);
							if (this.texture0 == null) {
								GL11.glDisable(3553);
							}

							RingBuffer.this.lastRenderedTexture0 = null;
							RingBuffer.this.lastRenderedTexture1 = null;
							RingBuffer.this.restoreBoundTextures = false;
						}

						if (this.texture0 != RingBuffer.this.lastRenderedTexture0) {
							if (this.texture0 != null) {
								if (RingBuffer.this.lastRenderedTexture0 == null) {
									GL11.glEnable(3553);
								}

								this.texture0.bind();
							} else {
								GL11.glDisable(3553);
								Texture.lastTextureID = 0;
								GL11.glBindTexture(3553, 0);
							}

							RingBuffer.this.lastRenderedTexture0 = this.texture0;
						}

						if (DebugOptions.instance.Checks.BoundTextures.getValue()) {
							RingBuffer.this.debugBoundTexture(RingBuffer.this.lastRenderedTexture0, 33984);
						}

						if (this.texture1 != RingBuffer.this.lastRenderedTexture1) {
							GL13.glActiveTexture(33985);
							GL13.glClientActiveTexture(33985);
							if (this.texture1 != null) {
								GL11.glBindTexture(3553, this.texture1.getID());
							} else {
								GL11.glDisable(3553);
							}

							GL13.glActiveTexture(33984);
							GL13.glClientActiveTexture(33984);
							RingBuffer.this.lastRenderedTexture1 = this.texture1;
						}

						if (this.useAttribArray != RingBuffer.this.lastUseAttribArray) {
							if (this.useAttribArray != -1) {
								if (this.useAttribArray == 1) {
									int2 = IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor;
									if (int2 != -1) {
										GL20.glEnableVertexAttribArray(int2);
									}
								}

								if (this.useAttribArray == 2) {
									int2 = IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor;
									if (int2 != -1) {
										GL20.glEnableVertexAttribArray(int2);
									}
								}
							} else {
								if (RingBuffer.this.lastUseAttribArray == 1) {
									int2 = IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor;
									if (int2 != -1) {
										GL20.glDisableVertexAttribArray(int2);
									}
								}

								if (RingBuffer.this.lastUseAttribArray == 2) {
									int2 = IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor;
									if (int2 != -1) {
										GL20.glDisableVertexAttribArray(int2);
									}
								}
							}

							RingBuffer.this.lastUseAttribArray = this.useAttribArray;
						}

						if (this.length != 0) {
							if (this.length == -1) {
								RingBuffer.this.restoreVBOs = true;
							} else {
								if (RingBuffer.this.restoreVBOs) {
									RingBuffer.this.restoreVBOs = false;
									RingBuffer.this.vbo[RingBuffer.this.sequence].bind();
									RingBuffer.this.ibo[RingBuffer.this.sequence].bind();
									GL11.glVertexPointer(2, 5126, 32, 0L);
									GL11.glTexCoordPointer(2, 5126, 32, 8L);
									GL11.glColorPointer(4, 5121, 32, 28L);
									GL13.glActiveTexture(33985);
									GL13.glClientActiveTexture(33985);
									GL11.glTexCoordPointer(2, 5126, 32, 16L);
									GL11.glEnableClientState(32888);
									int2 = IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor;
									if (int2 != -1) {
										GL20.glVertexAttribPointer(int2, 4, 5121, true, 32, 24L);
									}

									int2 = IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor;
									if (int2 != -1) {
										GL20.glVertexAttribPointer(int2, 4, 5121, true, 32, 24L);
									}

									GL13.glActiveTexture(33984);
									GL13.glClientActiveTexture(33984);
								}

								assert GL11.glGetInteger(34964) == RingBuffer.this.vbo[RingBuffer.this.sequence].getID();
								if (this.useAttribArray == 1) {
									RingBuffer.this.vbo[RingBuffer.this.sequence].enableVertexAttribArray(IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor);
									assert GL20.glGetVertexAttribi(IsoGridSquare.CircleStencilShader.instance.a_wallShadeColor, 34975) != 0;
								} else if (this.useAttribArray == 2) {
									RingBuffer.this.vbo[RingBuffer.this.sequence].enableVertexAttribArray(IsoGridSquare.NoCircleStencilShader.instance.a_wallShadeColor);
								} else {
									RingBuffer.this.vbo[RingBuffer.this.sequence].disableVertexAttribArray();
								}

								if (this.style.getRenderSprite()) {
									GL12.glDrawRangeElements(4, this.start, this.start + this.length, this.endIndex - this.startIndex, 5123, (long)this.startIndex * 2L);
								} else {
									this.style.render(this.start, this.startIndex);
								}
							}
						}
					}
				}
			}
		}
	}

	public static enum WallShaderTexRender {

		All,
		LeftOnly,
		RightOnly;

		private static SpriteRenderer.WallShaderTexRender[] $values() {
			return new SpriteRenderer.WallShaderTexRender[]{All, LeftOnly, RightOnly};
		}
	}

	private static class s_performance {
		private static final PerformanceProfileProbe waitForReadyState = new PerformanceProfileProbe("waitForReadyState");
		private static final PerformanceProfileProbe waitForReadySlotToOpen = new PerformanceProfileProbe("waitForReadySlotToOpen");
	}
}
