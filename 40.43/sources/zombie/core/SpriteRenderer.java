package zombie.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import zombie.characters.IsoPlayer;
import zombie.core.Styles.AdditiveStyle;
import zombie.core.Styles.AlphaOp;
import zombie.core.Styles.FloatList;
import zombie.core.Styles.GeometryData;
import zombie.core.Styles.LightingStyle;
import zombie.core.Styles.ShortList;
import zombie.core.Styles.Style;
import zombie.core.Styles.TransparentStyle;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;


public class SpriteRenderer {
	public static SpriteRenderer instance;
	static final int VERTEX_SIZE = 20;
	static final int TEXTURE0_COORD_OFFSET = 8;
	static final int COLOR_OFFSET = 16;
	public static SpriteRenderer.RingBuffer ringBuffer;
	public volatile TextureDraw[] sprite;
	public volatile TextureDraw[] drawsprite;
	public volatile Style[] style;
	public volatile Style[] drawstyle;
	public volatile int numSprites = 0;
	public volatile int drawNumSprites = 0;
	public boolean bDoAdditive = false;
	public static boolean GL_BLENDFUNC_ENABLED = true;
	public boolean DoingRender = false;
	public static final int NUM_RENDER_STATES = 3;
	public SpriteRenderer.RenderState state = null;
	public SpriteRenderer.RenderState[] states = new SpriteRenderer.RenderState[3];
	private static int discardedFrameCount = 0;

	public SpriteRenderer() {
		for (int int1 = 0; int1 < this.states.length; ++int1) {
			this.states[int1] = new SpriteRenderer.RenderState(int1);
		}

		this.state = this.states[0];
		this.style = this.state.style;
		this.sprite = this.state.sprite;
	}

	public void CheckSpriteSlots() {
		if (this.numSprites == this.sprite.length) {
			TextureDraw[] textureDrawArray = this.sprite;
			this.state.sprite = this.sprite = new TextureDraw[this.numSprites * 2];
			for (int int1 = this.numSprites; int1 < this.sprite.length; ++int1) {
				this.sprite[int1] = new TextureDraw();
			}

			System.arraycopy(textureDrawArray, 0, this.sprite, 0, this.numSprites);
			textureDrawArray = null;
			Style[] styleArray = this.style;
			this.state.style = this.style = new Style[this.numSprites * 2];
			System.arraycopy(styleArray, 0, this.style, 0, this.numSprites);
			styleArray = null;
		}
	}

	public void create() {
		if (ringBuffer == null) {
			ringBuffer = new SpriteRenderer.RingBuffer();
			ringBuffer.create();
		}
	}

	public void clearSprites() {
		for (int int1 = 0; int1 < this.state.drawnModels.size(); ++int1) {
			ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.state.drawnModels.get(int1);
			ModelManager.instance.DoneRendering(modelSlot.ID);
		}

		this.state.drawnModels.clear();
	}

	public void PushIso() {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.PushIso(this.sprite[this.numSprites]);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void PopIso() {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.PopIso(this.sprite[this.numSprites]);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void renderflipped(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		this.render(texture, int1, int2, int3, int4, float1, float2, float3, float4);
		this.sprite[this.numSprites - 1].flipped = true;
	}

	public void renderflipped(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		this.render(texture, float1, float2, float3, float4, float5, float6, float7, float8);
		this.sprite[this.numSprites - 1].flipped = true;
	}

	public void renderflipped(Texture texture, int int1, int int2, int int3, int int4, int int5) {
		this.render(texture, int1, int2, int3, int4, int5);
		this.sprite[this.numSprites - 1].flipped = true;
	}

	public void renderflipped(Texture texture, float float1, float float2, int int1, int int2, int int3) {
		this.render(texture, float1, float2, (float)int1, (float)int2, int3);
		this.sprite[this.numSprites - 1].flipped = true;
	}

	public void drawModel(ModelManager.ModelSlot modelSlot) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		if (!this.state.drawnModels.contains(modelSlot)) {
			assert modelSlot.renderRefCount < this.states.length;
			this.state.drawnModels.add(modelSlot);
			++modelSlot.renderRefCount;
		}

		TextureDraw.drawModel(this.sprite[this.numSprites], modelSlot);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void drawSkyBox(Shader shader, int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.drawSkyBox(this.sprite[this.numSprites], shader, int1, int2, int3);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void toBodyAtlas(DeadBodyAtlas.RenderJob renderJob) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.toBodyAtlas(this.sprite[this.numSprites], renderJob);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void drawGeneric(TextureDraw.GenericDrawer genericDrawer) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].type = TextureDraw.Type.DrawModel;
		this.sprite[this.numSprites].drawer = genericDrawer;
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void glDisable(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDisable(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void doAdditive(boolean boolean1) {
		this.bDoAdditive = false;
	}

	public void glEnable(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glEnable(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glStencilMask(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glStencilMask(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glClear(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glClear(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glClearColor(int int1, int int2, int int3, int int4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glClearColor(this.sprite[this.numSprites], int1, int2, int3, int4);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glStencilFunc(int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glStencilFunc(this.sprite[this.numSprites], int1, int2, int3);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glStencilOp(int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glStencilOp(this.sprite[this.numSprites], int1, int2, int3);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glColorMask(int int1, int int2, int int3, int int4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glColorMask(this.sprite[this.numSprites], int1, int2, int3, int4);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glAlphaFunc(int int1, float float1) {
		if (GL_BLENDFUNC_ENABLED) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			TextureDraw.glAlphaFunc(this.sprite[this.numSprites], int1, float1);
			this.style[this.numSprites] = TransparentStyle.instance;
			++this.numSprites;
		}
	}

	public void glBlendFunc(int int1, int int2) {
		if (GL_BLENDFUNC_ENABLED) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			TextureDraw.glBlendFunc(this.sprite[this.numSprites], int1, int2);
			this.style[this.numSprites] = TransparentStyle.instance;
			++this.numSprites;
		}
	}

	public void glBlendFuncSeparate(int int1, int int2, int int3, int int4) {
		if (GL_BLENDFUNC_ENABLED) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			TextureDraw.glBlendFuncSeparate(this.sprite[this.numSprites], int1, int2, int3, int4);
			this.style[this.numSprites] = TransparentStyle.instance;
			++this.numSprites;
		}
	}

	public void glBlendEquation(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glBlendEquation(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void render(Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, int5, int6, int7, int8, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void render(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, float float1, float float2, float float3, float float4) {
		this.render(texture, double1, double2, double3, double4, double5, double6, double7, double8, float1, float2, float3, float4, float1, float2, float3, float4, float1, float2, float3, float4, float1, float2, float3, float4);
	}

	public void render(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, (int)double1, (int)double2, (int)double3, (int)double4, (int)double5, (int)double6, (int)double7, (int)double8, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void renderdebug(Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, int5, int6, int7, int8, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void render(Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10, int int11, int int12) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, int5, int6, int7, int8, int9, int10, int11, int12);
		this.style[this.numSprites] = LightingStyle.instance;
		++this.numSprites;
	}

	public void renderline(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, int int5) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		if (int1 <= int3 && int2 <= int4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, int1 + int5, int2 - int5, int3 + int5, int4 - int5, int3 - int5, int4 + int5, int1 - int5, int2 + int5, float1, float2, float3, float4);
		} else if (int1 >= int3 && int2 >= int4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, int1 + int5, int2 - int5, int1 - int5, int2 + int5, int3 - int5, int4 + int5, int3 + int5, int4 - int5, float1, float2, float3, float4);
		} else if (int1 >= int3 && int2 <= int4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, int3 - int5, int4 - int5, int1 - int5, int2 - int5, int1 + int5, int2 + int5, int3 + int5, int4 + int5, float1, float2, float3, float4);
		} else if (int1 <= int3 && int2 >= int4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, int1 - int5, int2 - int5, int1 + int5, int2 + int5, int3 + int5, int4 + int5, int3 - int5, int4 - int5, float1, float2, float3, float4);
		}

		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void renderline(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		this.renderline(texture, int1, int2, int3, int4, float1, float2, float3, float4, 1);
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1, int int2, int int3, int int4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, (int)float1, (int)float2, (int)float3, (int)float4, (int)float5, (int)float6, (int)float7, (int)float8, int1, int2, int3, int4);
		this.style[this.numSprites] = LightingStyle.instance;
		++this.numSprites;
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		if (float8 != 0.0F) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], texture, (int)float1, (int)float2, (int)float3, (int)float4, float5, float6, float7, float8);
			this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
			++this.numSprites;
		}
	}

	public void render(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, float1, float2, float3, float4);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void renderRect(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		if (float4 != 0.0F) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], (Texture)null, int1, int2, int3, int4, float1, float2, float3, float4);
			this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
			++this.numSprites;
		}
	}

	public void renderPoly(int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], (Texture)null, int1, int2, int3, int4, int5, int6, int7, int8, float1, float2, float3, float4);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void renderPoly(Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, int5, int6, int7, int8, float1, float2, float3, float4);
		if (texture != null) {
			float float5 = texture.getXEnd();
			float float6 = texture.getXStart();
			float float7 = texture.getYEnd();
			float float8 = texture.getYStart();
			TextureDraw textureDraw = this.sprite[this.numSprites];
			textureDraw.u[0] = float6;
			textureDraw.u[1] = float5;
			textureDraw.u[2] = float5;
			textureDraw.u[3] = float6;
			textureDraw.v[0] = float8;
			textureDraw.v[1] = float8;
			textureDraw.v[2] = float7;
			textureDraw.v[3] = float7;
		}

		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void render(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		if (float4 != 0.0F) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
			this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
			++this.numSprites;
		}
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		if (float8 != 0.0F) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], texture, (int)float1, (int)float2, (int)float3, (int)float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
			this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
			++this.numSprites;
		}
	}

	public void render(Texture texture, int int1, int int2, int int3, int int4, int int5) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, int1, int2, int3, int4, int5);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, (int)float1, (int)float2, (int)float3, (int)float4, int1);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	private void build() {
		for (int int1 = 0; int1 < this.drawNumSprites; ++int1) {
			TextureDraw textureDraw = this.drawsprite[int1];
			Style style = this.drawstyle[int1];
			TextureDraw textureDraw2 = null;
			if (int1 > 0) {
				textureDraw2 = this.drawsprite[int1 - 1];
			}

			ringBuffer.add(textureDraw, textureDraw2, style, 1.0F);
		}
	}

	public void preRender() {
		this.clearSprites();
		this.numSprites = 0;
		this.state.numSprites = 0;
		this.state.fbo = Core.getInstance().getOffscreenBuffer();
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null) {
				this.state.offscreenWidth[int1] = Core.getInstance().getOffscreenWidth(int1);
				this.state.offscreenHeight[int1] = Core.getInstance().getOffscreenHeight(int1);
				this.state.camOffX[int1] = IsoCamera.RightClickX[int1] + (float)IsoCamera.PLAYER_OFFSET_X;
				this.state.camOffY[int1] = IsoCamera.RightClickY[int1] + (float)IsoCamera.PLAYER_OFFSET_Y;
				float float1 = player.x + IsoCamera.DeferedX[int1];
				float float2 = player.y + IsoCamera.DeferedY[int1];
				float[] floatArray = this.state.camOffX;
				floatArray[int1] += IsoUtils.XToScreen(float1 - (float)((int)float1), float2 - (float)((int)float2), 0.0F, 0);
				floatArray = this.state.camOffY;
				floatArray[int1] += IsoUtils.YToScreen(float1 - (float)((int)float1), float2 - (float)((int)float2), 0.0F, 0);
				this.state.drawOffsetX[int1] = (int)float1;
				this.state.drawOffsetY[int1] = (int)float2;
			}
		}
	}

	public void postRender() {
		if (!GLContext.getCapabilities().OpenGL21 || !GLContext.getCapabilities().GL_ARB_fragment_shader) {
			PerformanceSettings.numberOf3D = 0;
			PerformanceSettings.numberOf3DAlt = 0;
			PerformanceSettings.support3D = false;
		}

		this.drawsprite = this.states[2].sprite;
		this.drawstyle = this.states[2].style;
		this.drawNumSprites = this.states[2].numSprites;
		if (this.drawNumSprites != 0) {
			this.DoingRender = true;
			ringBuffer.begin();
			this.build();
			this.DoingRender = false;
			ringBuffer.finish();
		}
	}

	public void glBuffer(int int1, int int2) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glBuffer(this.sprite[this.numSprites], int1, int2);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glDoStartFrame(int int1, int int2, int int3) {
		this.glDoStartFrame(int1, int2, int3, false);
	}

	public void glDoStartFrame(int int1, int int2, int int3, boolean boolean1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDoStartFrame(this.sprite[this.numSprites], int1, int2, int3, boolean1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glDoStartFrameFx(int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDoStartFrameFx(this.sprite[this.numSprites], int1, int2, int3);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glIgnoreStyles(boolean boolean1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glIgnoreStyles(this.sprite[this.numSprites], boolean1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glDoEndFrame() {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDoEndFrame(this.sprite[this.numSprites]);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glDoEndFrameFx(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDoEndFrameFx(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glTexParameteri(int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glTexParameteri(this.sprite[this.numSprites], int1, int2, int3);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void StartShader(int int1, int int2) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.StartShader(this.sprite[this.numSprites], int1);
		if (int1 != 0 && Shader.ShaderMap.containsKey(int1)) {
			((Shader)Shader.ShaderMap.get(int1)).startMainThread(this.sprite[this.numSprites], int2);
		}

		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void ShaderUpdate(int int1, int int2, float float1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.ShaderUpdate(this.sprite[this.numSprites], int1, int2, float1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glLoadIdentity() {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glLoadIdentity(this.sprite[this.numSprites]);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glGenerateMipMaps(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glGenerateMipMaps(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void glBind(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glBind(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void glViewport(int int1, int int2, int int3, int int4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glViewport(this.sprite[this.numSprites], int1, int2, int3, int4);
		this.style[this.numSprites] = (Style)(this.bDoAdditive ? AdditiveStyle.instance : TransparentStyle.instance);
		++this.numSprites;
	}

	public void pushFrameDown() {
		String string = null;
		synchronized (this.states) {
			this.state.numSprites = this.numSprites;
			this.states[0].bRendered = false;
			this.states[0].time = System.nanoTime();
			SpriteRenderer.RenderState renderState = this.states[0];
			this.states[0] = this.states[1];
			this.states[1] = renderState;
			if (Core.bDebug && !this.states[0].bRendered && discardedFrameCount < 100) {
				++discardedFrameCount;
				float float1 = (float)(System.nanoTime() - this.states[0].time) / 1000000.0F;
				string = "discarding frame (" + discardedFrameCount + ") age " + float1 + " numSprites " + this.states[0].numSprites;
			}

			this.state = this.states[0];
			this.numSprites = this.state.numSprites;
			this.style = this.state.style;
			this.sprite = this.state.sprite;
		}
		if (string != null) {
			DebugLog.log(string);
		}
	}

	public class RenderState {
		public int index;
		public TextureDraw[] sprite = new TextureDraw[20000];
		public Style[] style = new Style[20000];
		public int numSprites = 0;
		public TextureFBO fbo = null;
		public boolean bRendered;
		public long time;
		public final ArrayList drawnModels = new ArrayList();
		public int playerIndex;
		public int[] offscreenWidth = new int[4];
		public int[] offscreenHeight = new int[4];
		public float[] camOffX = new float[4];
		public float[] camOffY = new float[4];
		public int[] drawOffsetX = new int[4];
		public int[] drawOffsetY = new int[4];

		public RenderState(int int1) {
			this.index = int1;
			for (int int2 = 0; int2 < this.sprite.length; ++int2) {
				this.sprite[int2] = new TextureDraw();
			}
		}
	}

	public static class RingBuffer {
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
		Style lastRenderedStyle;
		Style currentStyle;
		SpriteRenderer.RingBuffer.StateRun[] stateRun;
		public boolean restoreVBOs;
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
			this.bufferSizeInVertices = this.bufferSize / 20L;
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

		void add(TextureDraw textureDraw, TextureDraw textureDraw2, Style style, float float1) {
			if (style != null) {
				if ((long)(this.vertexCursor + 4) > this.bufferSizeInVertices || (long)(this.indexCursor + 6) > this.indexBufferSize) {
					this.render();
					this.next();
				}

				Texture texture = textureDraw.tex;
				if ((textureDraw == null || textureDraw.type != TextureDraw.Type.DrawModel && textureDraw.type != TextureDraw.Type.ToBodyAtlas) && (textureDraw2 == null || textureDraw2.type != TextureDraw.Type.DrawModel && textureDraw2.type != TextureDraw.Type.ToBodyAtlas) && (textureDraw.type != TextureDraw.Type.glDraw || textureDraw2 == null || textureDraw2.type == TextureDraw.Type.glDraw) && (textureDraw.type == TextureDraw.Type.glDraw || textureDraw2 == null || textureDraw2.type != TextureDraw.Type.glDraw) && this.currentRun != null && (style == this.currentStyle || this.currentStyle != null && style.getStyleID() == this.currentStyle.getStyleID()) && texture == this.currentTexture0) {
					if (textureDraw.type != TextureDraw.Type.glDraw) {
						this.currentRun.ops.add(textureDraw);
						return;
					}
				} else {
					this.currentRun = this.stateRun[this.numRuns];
					this.currentRun.start = this.vertexCursor;
					this.currentRun.length = 0;
					this.currentRun.style = style;
					this.currentRun.texture0 = texture;
					this.currentRun.indices = this.currentIndices;
					this.currentRun.startIndex = this.indexCursor;
					this.currentRun.endIndex = this.indexCursor;
					++this.numRuns;
					if (this.numRuns == this.stateRun.length) {
						this.growStateRuns();
					}

					this.currentStyle = style;
					this.currentTexture0 = texture;
					if (textureDraw.type != TextureDraw.Type.glDraw) {
						this.currentRun.ops.add(textureDraw);
						return;
					}
				}

				FloatBuffer floatBuffer = this.currentVertices;
				AlphaOp alphaOp;
				if (style == null) {
					alphaOp = AlphaOp.KEEP;
				} else {
					alphaOp = style.getAlphaOp();
				}

				floatBuffer.put((float)textureDraw.x[0]);
				floatBuffer.put((float)textureDraw.y[0]);
				if (textureDraw.tex == null) {
					floatBuffer.put(0.0F);
					floatBuffer.put(0.0F);
				} else {
					if (textureDraw.flipped) {
						floatBuffer.put(textureDraw.u[1]);
					} else {
						floatBuffer.put(textureDraw.u[0]);
					}

					floatBuffer.put(textureDraw.v[0]);
				}

				int int1 = textureDraw.getColor(0);
				alphaOp.op(int1, 255, floatBuffer);
				floatBuffer.put((float)textureDraw.x[1]);
				floatBuffer.put((float)textureDraw.y[1]);
				if (textureDraw.tex == null) {
					floatBuffer.put(0.0F);
					floatBuffer.put(0.0F);
				} else {
					if (textureDraw.flipped) {
						floatBuffer.put(textureDraw.u[0]);
					} else {
						floatBuffer.put(textureDraw.u[1]);
					}

					floatBuffer.put(textureDraw.v[1]);
				}

				int1 = textureDraw.getColor(1);
				alphaOp.op(int1, 255, floatBuffer);
				floatBuffer.put((float)textureDraw.x[2]);
				floatBuffer.put((float)textureDraw.y[2]);
				if (textureDraw.tex == null) {
					floatBuffer.put(0.0F);
					floatBuffer.put(0.0F);
				} else {
					if (textureDraw.flipped) {
						floatBuffer.put(textureDraw.u[3]);
					} else {
						floatBuffer.put(textureDraw.u[2]);
					}

					floatBuffer.put(textureDraw.v[2]);
				}

				int1 = textureDraw.getColor(2);
				alphaOp.op(int1, 255, floatBuffer);
				floatBuffer.put((float)textureDraw.x[3]);
				floatBuffer.put((float)textureDraw.y[3]);
				if (textureDraw.tex == null) {
					floatBuffer.put(0.0F);
					floatBuffer.put(0.0F);
				} else {
					if (textureDraw.flipped) {
						floatBuffer.put(textureDraw.u[2]);
					} else {
						floatBuffer.put(textureDraw.u[3]);
					}

					floatBuffer.put(textureDraw.v[3]);
				}

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

		void add(Style style) {
			GeometryData geometryData = style.build();
			if (geometryData == null) {
				this.currentRun = this.stateRun[this.numRuns];
				this.currentRun.start = this.vertexCursor;
				this.currentRun.length = -1;
				this.currentRun.style = style;
				this.currentRun.texture0 = null;
				this.currentRun.startIndex = 0;
				++this.numRuns;
				if (this.numRuns == this.stateRun.length) {
					this.growStateRuns();
				}

				this.currentStyle = null;
				this.currentTexture0 = null;
			} else {
				FloatList floatList = geometryData.getVertexData();
				ShortList shortList = geometryData.getIndexData();
				int int1 = floatList.size() / 5;
				if ((long)(this.vertexCursor + int1) > this.bufferSizeInVertices) {
					this.render();
					this.next();
				}

				this.currentRun = this.stateRun[this.numRuns];
				this.currentRun.start = this.vertexCursor;
				this.currentRun.length = int1;
				this.currentRun.style = style;
				this.currentRun.texture0 = null;
				this.currentRun.startIndex = this.indexCursor;
				++this.numRuns;
				if (this.numRuns == this.stateRun.length) {
					this.growStateRuns();
				}

				this.currentVertices.position(this.vertexCursor * 20 >> 2);
				this.currentVertices.put(floatList.array(), 0, floatList.size());
				this.currentIndices.position(this.indexCursor);
				short[] shortArray = shortList.array();
				int int2 = shortList.size();
				for (int int3 = 0; int3 < int2; ++int3) {
					shortArray[int3] = (short)(shortArray[int3] + this.vertexCursor);
				}

				this.currentIndices.put(shortArray, 0, int2);
				this.vertexCursor += int1;
				this.indexCursor += int2;
				this.currentStyle = null;
				this.currentTexture0 = null;
			}
		}

		public void rebind() {
			this.vbo[this.sequence].render();
			this.ibo[this.sequence].render();
		}

		private void next() {
			++this.sequence;
			if (this.sequence == this.numBuffers) {
				this.sequence = 0;
			}

			if (this.sequence == this.mark) {
				DebugLog.log("Buffer overrun");
			}

			this.vbo[this.sequence].render();
			ByteBuffer byteBuffer = this.vbo[this.sequence].map();
			if (this.vertices[this.sequence] == null || this.verticesBytes[this.sequence] != byteBuffer) {
				this.verticesBytes[this.sequence] = byteBuffer;
				this.vertices[this.sequence] = byteBuffer.asFloatBuffer();
			}

			this.ibo[this.sequence].render();
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

		void finish() {
			this.render();
		}

		void growStateRuns() {
			SpriteRenderer.RingBuffer.StateRun[] stateRunArray = new SpriteRenderer.RingBuffer.StateRun[(int)((float)this.stateRun.length * 1.5F)];
			System.arraycopy(this.stateRun, 0, stateRunArray, 0, this.stateRun.length);
			for (int int1 = this.numRuns; int1 < stateRunArray.length; ++int1) {
				stateRunArray[int1] = new SpriteRenderer.RingBuffer.StateRun();
			}

			this.stateRun = stateRunArray;
		}

		private class StateRun {
			Texture texture0;
			Style style;
			int start;
			int length;
			ShortBuffer indices;
			int startIndex;
			int endIndex;
			ArrayList ops;

			private StateRun() {
				this.ops = new ArrayList();
			}

			void render() {
				if (this.style != null) {
					int int1 = this.ops.size();
					if (int1 > 0) {
						for (int int2 = 0; int2 < int1; ++int2) {
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

						if (this.texture0 != RingBuffer.this.lastRenderedTexture0) {
							if (this.texture0 != null) {
								if (RingBuffer.this.lastRenderedTexture0 == null) {
									GL11.glEnable(3553);
								}

								this.texture0.bind();
							} else if (RingBuffer.this.lastRenderedTexture0 != null) {
								GL11.glDisable(3553);
							}

							RingBuffer.this.lastRenderedTexture0 = this.texture0;
						}

						if (this.length != 0) {
							if (this.length == -1) {
								RingBuffer.this.restoreVBOs = true;
							} else {
								if (RingBuffer.this.restoreVBOs) {
									RingBuffer.this.restoreVBOs = false;
									GL11.glVertexPointer(2, 5126, 20, 0L);
									GL11.glTexCoordPointer(2, 5126, 20, 8L);
									GL11.glColorPointer(4, 5121, 20, 16L);
								}

								if (this.style.getRenderSprite()) {
									GL12.glDrawRangeElements(4, this.start, this.start + this.length, this.endIndex - this.startIndex, 5123, (long)(this.startIndex * 2));
								} else {
									this.style.render(this.start, this.startIndex);
								}
							}
						}
					}
				}
			}

			StateRun(Object object) {
				this();
			}
		}
	}
}
