package zombie.core.sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import zombie.core.Color;
import zombie.core.SpriteRenderer;
import zombie.core.Styles.AbstractStyle;
import zombie.core.Styles.Style;
import zombie.core.Styles.TransparentStyle;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;


public abstract class GenericSpriteRenderState {
	public final int index;
	public TextureDraw[] sprite = new TextureDraw[2048];
	public Style[] style = new Style[2048];
	public int numSprites;
	public TextureFBO fbo;
	public boolean bRendered;
	private boolean m_isRendering;
	public final ArrayList postRender = new ArrayList();
	public AbstractStyle defaultStyle;
	public boolean bCursorVisible;
	public static final byte UVCA_NONE = -1;
	public static final byte UVCA_CIRCLE = 1;
	public static final byte UVCA_NOCIRCLE = 2;
	private byte useVertColorsArray;
	private int texture2_color0;
	private int texture2_color1;
	private int texture2_color2;
	private int texture2_color3;
	private SpriteRenderer.WallShaderTexRender wallShaderTexRender;
	private Texture texture1_cutaway;
	private int texture1_cutaway_x;
	private int texture1_cutaway_y;
	private int texture1_cutaway_w;
	private int texture1_cutaway_h;

	protected GenericSpriteRenderState(int int1) {
		this.defaultStyle = TransparentStyle.instance;
		this.bCursorVisible = true;
		this.useVertColorsArray = -1;
		this.index = int1;
		for (int int2 = 0; int2 < this.sprite.length; ++int2) {
			this.sprite[int2] = new TextureDraw();
		}
	}

	public void onRendered() {
		this.m_isRendering = false;
		this.bRendered = true;
	}

	public void onRenderAcquired() {
		this.m_isRendering = true;
	}

	public boolean isRendering() {
		return this.m_isRendering;
	}

	public void onReady() {
		this.bRendered = false;
	}

	public boolean isReady() {
		return !this.bRendered;
	}

	public boolean isRendered() {
		return this.bRendered;
	}

	public void CheckSpriteSlots() {
		if (this.numSprites == this.sprite.length) {
			TextureDraw[] textureDrawArray = this.sprite;
			this.sprite = new TextureDraw[this.numSprites * 3 / 2 + 1];
			for (int int1 = this.numSprites; int1 < this.sprite.length; ++int1) {
				this.sprite[int1] = new TextureDraw();
			}

			System.arraycopy(textureDrawArray, 0, this.sprite, 0, this.numSprites);
			Style[] styleArray = this.style;
			this.style = new Style[this.numSprites * 3 / 2 + 1];
			System.arraycopy(styleArray, 0, this.style, 0, this.numSprites);
		}
	}

	public static void clearSprites(List list) {
		for (int int1 = 0; int1 < list.size(); ++int1) {
			((TextureDraw)list.get(int1)).postRender();
		}

		list.clear();
	}

	public void clear() {
		clearSprites(this.postRender);
		this.numSprites = 0;
	}

	public void glDepthMask(boolean boolean1) {
		this.CheckSpriteSlots();
		TextureDraw.glDepthMask(this.sprite[this.numSprites], boolean1);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void renderflipped(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		this.render(texture, float1, float2, float3, float4, float5, float6, float7, float8, consumer);
		this.sprite[this.numSprites - 1].flipped = true;
	}

	public void drawSkyBox(Shader shader, int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.drawSkyBox(this.sprite[this.numSprites], shader, int1, int2, int3);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void drawWater(Shader shader, int int1, int int2, boolean boolean1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		shader.startMainThread(this.sprite[this.numSprites], int1);
		TextureDraw.drawWater(this.sprite[this.numSprites], shader, int1, int2, boolean1);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void drawPuddles(Shader shader, int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.drawPuddles(this.sprite[this.numSprites], shader, int1, int2, int3);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void drawParticles(int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.drawParticles(this.sprite[this.numSprites], int1, int2, int3);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void glDisable(int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDisable(this.sprite[this.numSprites], int1);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
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
		if (SpriteRenderer.GL_BLENDFUNC_ENABLED) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			TextureDraw.glAlphaFunc(this.sprite[this.numSprites], int1, float1);
			this.style[this.numSprites] = TransparentStyle.instance;
			++this.numSprites;
		}
	}

	public void glBlendFunc(int int1, int int2) {
		if (SpriteRenderer.GL_BLENDFUNC_ENABLED) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			TextureDraw.glBlendFunc(this.sprite[this.numSprites], int1, int2);
			this.style[this.numSprites] = TransparentStyle.instance;
			++this.numSprites;
		}
	}

	public void glBlendFuncSeparate(int int1, int int2, int int3, int int4) {
		if (SpriteRenderer.GL_BLENDFUNC_ENABLED) {
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

	public void render(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, float float1, float float2, float float3, float float4, Consumer consumer) {
		this.render(texture, double1, double2, double3, double4, double5, double6, double7, double8, float1, float2, float3, float4, float1, float2, float3, float4, float1, float2, float3, float4, float1, float2, float3, float4, consumer);
	}

	public void render(Texture texture, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Consumer consumer) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, (float)double1, (float)double2, (float)double3, (float)double4, (float)double5, (float)double6, (float)double7, (float)double8, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
		if (this.useVertColorsArray != -1) {
			TextureDraw textureDraw = this.sprite[this.numSprites];
			textureDraw.useAttribArray = this.useVertColorsArray;
			textureDraw.tex1_col0 = this.texture2_color0;
			textureDraw.tex1_col1 = this.texture2_color1;
			textureDraw.tex1_col2 = this.texture2_color2;
			textureDraw.tex1_col3 = this.texture2_color3;
		}

		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void setUseVertColorsArray(byte byte1, int int1, int int2, int int3, int int4) {
		this.useVertColorsArray = byte1;
		this.texture2_color0 = int1;
		this.texture2_color1 = int2;
		this.texture2_color2 = int3;
		this.texture2_color3 = int4;
	}

	public void clearUseVertColorsArray() {
		this.useVertColorsArray = -1;
	}

	public void renderdebug(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19, float float20, float float21, float float22, float float23, float float24, Consumer consumer) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, float17, float18, float19, float20, float21, float22, float23, float24, consumer);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void renderline(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		if (float1 <= float3 && float2 <= float4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, float1 + (float)int1, float2 - (float)int1, float3 + (float)int1, float4 - (float)int1, float3 - (float)int1, float4 + (float)int1, float1 - (float)int1, float2 + (float)int1, float5, float6, float7, float8);
		} else if (float1 >= float3 && float2 >= float4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, float1 + (float)int1, float2 - (float)int1, float1 - (float)int1, float2 + (float)int1, float3 - (float)int1, float4 + (float)int1, float3 + (float)int1, float4 - (float)int1, float5, float6, float7, float8);
		} else if (float1 >= float3 && float2 <= float4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, float3 - (float)int1, float4 - (float)int1, float1 - (float)int1, float2 - (float)int1, float1 + (float)int1, float2 + (float)int1, float3 + (float)int1, float4 + (float)int1, float5, float6, float7, float8);
		} else if (float1 <= float3 && float2 >= float4) {
			TextureDraw.Create(this.sprite[this.numSprites], texture, float1 - (float)int1, float2 - (float)int1, float1 + (float)int1, float2 + (float)int1, float3 + (float)int1, float4 + (float)int1, float3 - (float)int1, float4 - (float)int1, float5, float6, float7, float8);
		}

		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void renderline(Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		this.renderline(texture, (float)int1, (float)int2, (float)int3, (float)int4, float1, float2, float3, float4, 1);
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1, int int2, int int3, int int4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], texture, float1, float2, float3, float4, float5, float6, float7, float8, int1, int2, int3, int4);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		if (texture == null || texture.isReady()) {
			if (float8 != 0.0F) {
				if (this.numSprites == this.sprite.length) {
					this.CheckSpriteSlots();
				}

				this.sprite[this.numSprites].reset();
				int int1 = Color.colorToABGR(float5, float6, float7, float8);
				float float9 = float1 + float3;
				float float10 = float2 + float4;
				TextureDraw textureDraw;
				if (this.wallShaderTexRender == null) {
					textureDraw = TextureDraw.Create(this.sprite[this.numSprites], texture, float1, float2, float9, float2, float9, float10, float1, float10, int1, int1, int1, int1, consumer);
				} else {
					textureDraw = TextureDraw.Create(this.sprite[this.numSprites], texture, this.wallShaderTexRender, float1, float2, float9 - float1, float10 - float2, float5, float6, float7, float8, consumer);
				}

				if (this.useVertColorsArray != -1) {
					textureDraw.useAttribArray = this.useVertColorsArray;
					textureDraw.tex1_col0 = this.texture2_color0;
					textureDraw.tex1_col1 = this.texture2_color1;
					textureDraw.tex1_col2 = this.texture2_color2;
					textureDraw.tex1_col3 = this.texture2_color3;
				}

				if (this.texture1_cutaway != null) {
					textureDraw.tex1 = this.texture1_cutaway;
					float float11 = this.texture1_cutaway.xEnd - this.texture1_cutaway.xStart;
					float float12 = this.texture1_cutaway.yEnd - this.texture1_cutaway.yStart;
					float float13 = (float)this.texture1_cutaway_x / (float)this.texture1_cutaway.getWidth();
					float float14 = (float)(this.texture1_cutaway_x + this.texture1_cutaway_w) / (float)this.texture1_cutaway.getWidth();
					float float15 = (float)this.texture1_cutaway_y / (float)this.texture1_cutaway.getHeight();
					float float16 = (float)(this.texture1_cutaway_y + this.texture1_cutaway_h) / (float)this.texture1_cutaway.getHeight();
					textureDraw.tex1_u0 = textureDraw.tex1_u3 = this.texture1_cutaway.xStart + float13 * float11;
					textureDraw.tex1_v0 = textureDraw.tex1_v1 = this.texture1_cutaway.yStart + float15 * float12;
					textureDraw.tex1_u1 = textureDraw.tex1_u2 = this.texture1_cutaway.xStart + float14 * float11;
					textureDraw.tex1_v2 = textureDraw.tex1_v3 = this.texture1_cutaway.yStart + float16 * float12;
				}

				this.style[this.numSprites] = this.defaultStyle;
				++this.numSprites;
			}
		}
	}

	public void renderRect(int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		if (float4 != 0.0F) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], (Texture)null, (float)int1, (float)int2, (float)int3, (float)int4, float1, float2, float3, float4, (Consumer)null);
			this.style[this.numSprites] = this.defaultStyle;
			++this.numSprites;
		}
	}

	public void renderPoly(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].reset();
		TextureDraw.Create(this.sprite[this.numSprites], (Texture)null, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void renderPoly(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		if (texture == null || texture.isReady()) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
			if (texture != null) {
				float float13 = texture.getXEnd();
				float float14 = texture.getXStart();
				float float15 = texture.getYEnd();
				float float16 = texture.getYStart();
				TextureDraw textureDraw = this.sprite[this.numSprites];
				textureDraw.u0 = float14;
				textureDraw.u1 = float13;
				textureDraw.u2 = float13;
				textureDraw.u3 = float14;
				textureDraw.v0 = float16;
				textureDraw.v1 = float16;
				textureDraw.v2 = float15;
				textureDraw.v3 = float15;
			}

			this.style[this.numSprites] = this.defaultStyle;
			++this.numSprites;
		}
	}

	public void renderPoly(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19, float float20) {
		if (texture == null || texture.isReady()) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12);
			if (texture != null) {
				TextureDraw textureDraw = this.sprite[this.numSprites];
				textureDraw.u0 = float13;
				textureDraw.u1 = float15;
				textureDraw.u2 = float17;
				textureDraw.u3 = float19;
				textureDraw.v0 = float14;
				textureDraw.v1 = float16;
				textureDraw.v2 = float18;
				textureDraw.v3 = float20;
			}

			this.style[this.numSprites] = this.defaultStyle;
			++this.numSprites;
		}
	}

	public void render(Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Consumer consumer) {
		if (float8 != 0.0F) {
			if (this.numSprites == this.sprite.length) {
				this.CheckSpriteSlots();
			}

			this.sprite[this.numSprites].reset();
			TextureDraw.Create(this.sprite[this.numSprites], texture, float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
			this.style[this.numSprites] = this.defaultStyle;
			++this.numSprites;
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

	public void glDoStartFrame(int int1, int int2, float float1, int int3) {
		this.glDoStartFrame(int1, int2, float1, int3, false);
	}

	public void glDoStartFrame(int int1, int int2, float float1, int int3, boolean boolean1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glDoStartFrame(this.sprite[this.numSprites], int1, int2, float1, int3, boolean1);
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

	public void doCoreIntParam(int int1, float float1) {
		this.CheckSpriteSlots();
		TextureDraw.doCoreIntParam(this.sprite[this.numSprites], int1, float1);
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

	public void setCutawayTexture(Texture texture, int int1, int int2, int int3, int int4) {
		this.texture1_cutaway = texture;
		this.texture1_cutaway_x = int1;
		this.texture1_cutaway_y = int2;
		this.texture1_cutaway_w = int3;
		this.texture1_cutaway_h = int4;
	}

	public void clearCutawayTexture() {
		this.texture1_cutaway = null;
	}

	public void setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender wallShaderTexRender) {
		this.wallShaderTexRender = wallShaderTexRender;
	}

	public void ShaderUpdate1i(int int1, int int2, int int3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.ShaderUpdate1i(this.sprite[this.numSprites], int1, int2, int3);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void ShaderUpdate1f(int int1, int int2, float float1) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.ShaderUpdate1f(this.sprite[this.numSprites], int1, int2, float1);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void ShaderUpdate2f(int int1, int int2, float float1, float float2) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.ShaderUpdate2f(this.sprite[this.numSprites], int1, int2, float1, float2);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void ShaderUpdate3f(int int1, int int2, float float1, float float2, float float3) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.ShaderUpdate3f(this.sprite[this.numSprites], int1, int2, float1, float2, float3);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void ShaderUpdate4f(int int1, int int2, float float1, float float2, float float3, float float4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.ShaderUpdate4f(this.sprite[this.numSprites], int1, int2, float1, float2, float3, float4);
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
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void glViewport(int int1, int int2, int int3, int int4) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.glViewport(this.sprite[this.numSprites], int1, int2, int3, int4);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
	}

	public void drawModel(ModelManager.ModelSlot modelSlot) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.drawModel(this.sprite[this.numSprites], modelSlot);
		assert this.sprite[this.numSprites].drawer != null;
		ArrayList arrayList = this.postRender;
		arrayList.add(this.sprite[this.numSprites]);
		this.style[this.numSprites] = this.defaultStyle;
		++this.numSprites;
		++modelSlot.renderRefCount;
	}

	public void drawGeneric(TextureDraw.GenericDrawer genericDrawer) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		this.sprite[this.numSprites].type = TextureDraw.Type.DrawModel;
		this.sprite[this.numSprites].drawer = genericDrawer;
		this.style[this.numSprites] = this.defaultStyle;
		ArrayList arrayList = this.postRender;
		arrayList.add(this.sprite[this.numSprites]);
		++this.numSprites;
	}

	public void StartShader(int int1, int int2) {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.StartShader(this.sprite[this.numSprites], int1);
		if (int1 != 0 && Shader.ShaderMap.containsKey(int1)) {
			((Shader)Shader.ShaderMap.get(int1)).startMainThread(this.sprite[this.numSprites], int2);
			ArrayList arrayList = this.postRender;
			arrayList.add(this.sprite[this.numSprites]);
		}

		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}

	public void EndShader() {
		if (this.numSprites == this.sprite.length) {
			this.CheckSpriteSlots();
		}

		TextureDraw.StartShader(this.sprite[this.numSprites], 0);
		this.style[this.numSprites] = TransparentStyle.instance;
		++this.numSprites;
	}
}
