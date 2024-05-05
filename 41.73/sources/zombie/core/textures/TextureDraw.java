package zombie.core.textures;

import java.util.function.Consumer;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import zombie.IndieGL;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.iso.IsoWorld;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayUtil;


public final class TextureDraw {
	public TextureDraw.Type type;
	public int a;
	public int b;
	public float f1;
	public float[] vars;
	public int c;
	public int d;
	public int col0;
	public int col1;
	public int col2;
	public int col3;
	public float x0;
	public float x1;
	public float x2;
	public float x3;
	public float y0;
	public float y1;
	public float y2;
	public float y3;
	public float u0;
	public float u1;
	public float u2;
	public float u3;
	public float v0;
	public float v1;
	public float v2;
	public float v3;
	public Texture tex;
	public Texture tex1;
	public byte useAttribArray;
	public float tex1_u0;
	public float tex1_u1;
	public float tex1_u2;
	public float tex1_u3;
	public float tex1_v0;
	public float tex1_v1;
	public float tex1_v2;
	public float tex1_v3;
	public int tex1_col0;
	public int tex1_col1;
	public int tex1_col2;
	public int tex1_col3;
	public boolean bSingleCol;
	public boolean flipped;
	public TextureDraw.GenericDrawer drawer;

	public TextureDraw() {
		this.type = TextureDraw.Type.glDraw;
		this.a = 0;
		this.b = 0;
		this.f1 = 0.0F;
		this.c = 0;
		this.d = 0;
		this.bSingleCol = false;
		this.flipped = false;
	}

	public static void glStencilFunc(TextureDraw textureDraw, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.glStencilFunc;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
	}

	public static void glBuffer(TextureDraw textureDraw, int int1, int int2) {
		textureDraw.type = TextureDraw.Type.glBuffer;
		textureDraw.a = int1;
		textureDraw.b = int2;
	}

	public static void glStencilOp(TextureDraw textureDraw, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.glStencilOp;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
	}

	public static void glDisable(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glDisable;
		textureDraw.a = int1;
	}

	public static void glClear(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glClear;
		textureDraw.a = int1;
	}

	public static void glClearColor(TextureDraw textureDraw, int int1, int int2, int int3, int int4) {
		textureDraw.type = TextureDraw.Type.glClearColor;
		textureDraw.col0 = int1;
		textureDraw.col1 = int2;
		textureDraw.col2 = int3;
		textureDraw.col3 = int4;
	}

	public static void glEnable(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glEnable;
		textureDraw.a = int1;
	}

	public static void glAlphaFunc(TextureDraw textureDraw, int int1, float float1) {
		textureDraw.type = TextureDraw.Type.glAlphaFunc;
		textureDraw.a = int1;
		textureDraw.f1 = float1;
	}

	public static void glColorMask(TextureDraw textureDraw, int int1, int int2, int int3, int int4) {
		textureDraw.type = TextureDraw.Type.glColorMask;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
		textureDraw.x0 = (float)int4;
	}

	public static void glStencilMask(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glStencilMask;
		textureDraw.a = int1;
	}

	public static void glBlendFunc(TextureDraw textureDraw, int int1, int int2) {
		textureDraw.type = TextureDraw.Type.glBlendFunc;
		textureDraw.a = int1;
		textureDraw.b = int2;
	}

	public static void glBlendFuncSeparate(TextureDraw textureDraw, int int1, int int2, int int3, int int4) {
		textureDraw.type = TextureDraw.Type.glBlendFuncSeparate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
		textureDraw.d = int4;
	}

	public static void glBlendEquation(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glBlendEquation;
		textureDraw.a = int1;
	}

	public static void glDoEndFrame(TextureDraw textureDraw) {
		textureDraw.type = TextureDraw.Type.glDoEndFrame;
	}

	public static void glDoEndFrameFx(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glDoEndFrameFx;
		textureDraw.c = int1;
	}

	public static void glIgnoreStyles(TextureDraw textureDraw, boolean boolean1) {
		textureDraw.type = TextureDraw.Type.glIgnoreStyles;
		textureDraw.a = boolean1 ? 1 : 0;
	}

	public static void glDoStartFrame(TextureDraw textureDraw, int int1, int int2, float float1, int int3) {
		glDoStartFrame(textureDraw, int1, int2, float1, int3, false);
	}

	public static void glDoStartFrame(TextureDraw textureDraw, int int1, int int2, float float1, int int3, boolean boolean1) {
		if (boolean1) {
			textureDraw.type = TextureDraw.Type.glDoStartFrameText;
		} else {
			textureDraw.type = TextureDraw.Type.glDoStartFrame;
		}

		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.f1 = float1;
		textureDraw.c = int3;
	}

	public static void glDoStartFrameFx(TextureDraw textureDraw, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.glDoStartFrameFx;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
	}

	public static void glTexParameteri(TextureDraw textureDraw, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.glTexParameteri;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
	}

	public static void drawModel(TextureDraw textureDraw, ModelManager.ModelSlot modelSlot) {
		textureDraw.type = TextureDraw.Type.DrawModel;
		textureDraw.a = modelSlot.ID;
		textureDraw.drawer = ModelSlotRenderData.alloc().init(modelSlot);
	}

	public static void drawSkyBox(TextureDraw textureDraw, Shader shader, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.DrawSkyBox;
		textureDraw.a = shader.getID();
		textureDraw.b = int1;
		textureDraw.c = int2;
		textureDraw.d = int3;
		textureDraw.drawer = null;
	}

	public static void drawWater(TextureDraw textureDraw, Shader shader, int int1, int int2, boolean boolean1) {
		textureDraw.type = TextureDraw.Type.DrawWater;
		textureDraw.a = shader.getID();
		textureDraw.b = int1;
		textureDraw.c = int2;
		textureDraw.d = boolean1 ? 1 : 0;
		textureDraw.drawer = null;
	}

	public static void drawPuddles(TextureDraw textureDraw, Shader shader, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.DrawPuddles;
		textureDraw.a = shader.getID();
		textureDraw.b = int1;
		textureDraw.c = int2;
		textureDraw.d = int3;
		textureDraw.drawer = null;
	}

	public static void drawParticles(TextureDraw textureDraw, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.DrawParticles;
		textureDraw.b = int1;
		textureDraw.c = int2;
		textureDraw.d = int3;
		textureDraw.drawer = null;
	}

	public static void StartShader(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.StartShader;
		textureDraw.a = int1;
	}

	public static void ShaderUpdate1i(TextureDraw textureDraw, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.ShaderUpdate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = -1;
		textureDraw.d = int3;
	}

	public static void ShaderUpdate1f(TextureDraw textureDraw, int int1, int int2, float float1) {
		textureDraw.type = TextureDraw.Type.ShaderUpdate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = 1;
		textureDraw.u0 = float1;
	}

	public static void ShaderUpdate2f(TextureDraw textureDraw, int int1, int int2, float float1, float float2) {
		textureDraw.type = TextureDraw.Type.ShaderUpdate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = 2;
		textureDraw.u0 = float1;
		textureDraw.u1 = float2;
	}

	public static void ShaderUpdate3f(TextureDraw textureDraw, int int1, int int2, float float1, float float2, float float3) {
		textureDraw.type = TextureDraw.Type.ShaderUpdate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = 3;
		textureDraw.u0 = float1;
		textureDraw.u1 = float2;
		textureDraw.u2 = float3;
	}

	public static void ShaderUpdate4f(TextureDraw textureDraw, int int1, int int2, float float1, float float2, float float3, float float4) {
		textureDraw.type = TextureDraw.Type.ShaderUpdate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = 4;
		textureDraw.u0 = float1;
		textureDraw.u1 = float2;
		textureDraw.u2 = float3;
		textureDraw.u3 = float4;
	}

	public void run() {
		switch (this.type) {
		case StartShader: 
			ARBShaderObjects.glUseProgramObjectARB(this.a);
			if (Shader.ShaderMap.containsKey(this.a)) {
				((Shader)Shader.ShaderMap.get(this.a)).startRenderThread(this);
			}

			if (this.a == 0) {
				SpriteRenderer.ringBuffer.checkShaderChangedTexture1();
			}

			break;
		
		case ShaderUpdate: 
			if (this.c == 1) {
				ARBShaderObjects.glUniform1fARB(this.b, this.u0);
			}

			if (this.c == 2) {
				ARBShaderObjects.glUniform2fARB(this.b, this.u0, this.u1);
			}

			if (this.c == 3) {
				ARBShaderObjects.glUniform3fARB(this.b, this.u0, this.u1, this.u2);
			}

			if (this.c == 4) {
				ARBShaderObjects.glUniform4fARB(this.b, this.u0, this.u1, this.u2, this.u3);
			}

			if (this.c == -1) {
				ARBShaderObjects.glUniform1iARB(this.b, this.d);
			}

			break;
		
		case BindActiveTexture: 
			GL13.glActiveTexture(this.a);
			if (this.b != -1) {
				GL11.glBindTexture(3553, this.b);
			}

			GL13.glActiveTexture(33984);
			break;
		
		case DrawModel: 
			if (this.drawer != null) {
				this.drawer.render();
			}

			break;
		
		case DrawSkyBox: 
			try {
				ModelManager.instance.RenderSkyBox(this, this.a, this.b, this.c, this.d);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			break;
		
		case DrawWater: 
			try {
				ModelManager.instance.RenderWater(this, this.a, this.b, this.d == 1);
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			break;
		
		case DrawPuddles: 
			try {
				ModelManager.instance.RenderPuddles(this.a, this.b, this.d);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

			break;
		
		case DrawParticles: 
			try {
				ModelManager.instance.RenderParticles(this, this.b, this.c);
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}

			break;
		
		case glClear: 
			IndieGL.glClearA(this.a);
			break;
		
		case glClearColor: 
			GL11.glClearColor((float)this.col0 / 255.0F, (float)this.col1 / 255.0F, (float)this.col2 / 255.0F, (float)this.col3 / 255.0F);
			break;
		
		case glBlendFunc: 
			IndieGL.glBlendFuncA(this.a, this.b);
			break;
		
		case glBlendFuncSeparate: 
			GL14.glBlendFuncSeparate(this.a, this.b, this.c, this.d);
			break;
		
		case glColorMask: 
			IndieGL.glColorMaskA(this.a == 1, this.b == 1, this.c == 1, this.x0 == 1.0F);
			break;
		
		case glTexParameteri: 
			IndieGL.glTexParameteriActual(this.a, this.b, this.c);
			break;
		
		case glStencilMask: 
			IndieGL.glStencilMaskA(this.a);
			break;
		
		case glDoEndFrame: 
			Core.getInstance().DoEndFrameStuff(this.a, this.b);
			break;
		
		case glDoEndFrameFx: 
			Core.getInstance().DoEndFrameStuffFx(this.a, this.b, this.c);
			break;
		
		case glDoStartFrame: 
			Core.getInstance().DoStartFrameStuff(this.a, this.b, this.f1, this.c);
			break;
		
		case glDoStartFrameText: 
			Core.getInstance().DoStartFrameStuff(this.a, this.b, this.f1, this.c, true);
			break;
		
		case glDoStartFrameFx: 
			Core.getInstance().DoStartFrameStuffSmartTextureFx(this.a, this.b, this.c);
			break;
		
		case glStencilFunc: 
			IndieGL.glStencilFuncA(this.a, this.b, this.c);
			break;
		
		case glBuffer: 
			if (Core.getInstance().supportsFBO()) {
				if (this.a == 1) {
					SpriteRenderer.instance.getRenderingState().fbo.startDrawing(false, false);
				} else if (this.a == 2) {
					UIManager.UIFBO.startDrawing(true, true);
				} else if (this.a == 3) {
					UIManager.UIFBO.endDrawing();
				} else if (this.a == 4) {
					WeatherFxMask.getFboMask().startDrawing(true, true);
				} else if (this.a == 5) {
					WeatherFxMask.getFboMask().endDrawing();
				} else if (this.a == 6) {
					WeatherFxMask.getFboParticles().startDrawing(true, true);
				} else if (this.a == 7) {
					WeatherFxMask.getFboParticles().endDrawing();
				} else {
					SpriteRenderer.instance.getRenderingState().fbo.endDrawing();
				}
			}

			break;
		
		case glStencilOp: 
			IndieGL.glStencilOpA(this.a, this.b, this.c);
			break;
		
		case glLoadIdentity: 
			GL11.glLoadIdentity();
			break;
		
		case glBind: 
			GL11.glBindTexture(3553, this.a);
			Texture.lastlastTextureID = Texture.lastTextureID;
			Texture.lastTextureID = this.a;
			break;
		
		case glViewport: 
			GL11.glViewport(this.a, this.b, this.c, this.d);
			break;
		
		case drawTerrain: 
			IsoWorld.instance.renderTerrain();
			break;
		
		case doCoreIntParam: 
			Core.getInstance().FloatParamMap.put(this.a, this.f1);
			break;
		
		case glDepthMask: 
			GL11.glDepthMask(this.a == 1);
		
		case glGenerateMipMaps: 
		
		default: 
			break;
		
		case glAlphaFunc: 
			IndieGL.glAlphaFuncA(this.a, this.f1);
			break;
		
		case glEnable: 
			IndieGL.glEnableA(this.a);
			break;
		
		case glDisable: 
			IndieGL.glDisableA(this.a);
			break;
		
		case glBlendEquation: 
			GL14.glBlendEquation(this.a);
			break;
		
		case glIgnoreStyles: 
			SpriteRenderer.RingBuffer.IGNORE_STYLES = this.a == 1;
		
		}
	}

	public static void glDepthMask(TextureDraw textureDraw, boolean boolean1) {
		textureDraw.type = TextureDraw.Type.glDepthMask;
		textureDraw.a = boolean1 ? 1 : 0;
	}

	public static void doCoreIntParam(TextureDraw textureDraw, int int1, float float1) {
		textureDraw.type = TextureDraw.Type.doCoreIntParam;
		textureDraw.a = int1;
		textureDraw.f1 = float1;
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{ " + this.type + ", a:" + this.a + ", b:" + this.b + ", f1:" + this.f1 + ", vars:" + (this.vars != null ? PZArrayUtil.arrayToString(this.vars, "{", "}", ", ") : "null") + ", c:" + this.c + ", d:" + this.d + ", col0:" + this.col0 + ", col1:" + this.col1 + ", col2:" + this.col2 + ", col3:" + this.col3 + ", x0:" + this.x0 + ", x1:" + this.x1 + ", x2:" + this.x2 + ", x3:" + this.x3 + ", x0:" + this.x0 + ", x1:" + this.x1 + ", x2:" + this.x2 + ", x3:" + this.x3 + ", y0:" + this.y0 + ", y1:" + this.y1 + ", y2:" + this.y2 + ", y3:" + this.y3 + ", u0:" + this.u0 + ", u1:" + this.u1 + ", u2:" + this.u2 + ", u3:" + this.u3 + ", v0:" + this.v0 + ", v1:" + this.v1 + ", v2:" + this.v2 + ", v3:" + this.v3 + ", tex:" + this.tex + ", tex1:" + this.tex1 + ", useAttribArray:" + this.useAttribArray + ", tex1_u0:" + this.tex1_u0 + ", tex1_u1:" + this.tex1_u1 + ", tex1_u2:" + this.tex1_u2 + ", tex1_u3:" + this.tex1_u3 + ", tex1_u0:" + this.tex1_u0 + ", tex1_u1:" + this.tex1_u1 + ", tex1_u2:" + this.tex1_u2 + ", tex1_u3:" + this.tex1_u3 + ", tex1_col0:" + this.tex1_col0 + ", tex1_col1:" + this.tex1_col1 + ", tex1_col2:" + this.tex1_col2 + ", tex1_col3:" + this.tex1_col3 + ", bSingleCol:" + this.bSingleCol + " }";
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		int int1 = Color.colorToABGR(float5, float6, float7, float8);
		Create(textureDraw, texture, float1, float2, float1 + float3, float2, float1 + float3, float2 + float4, float1, float2 + float4, int1, int1, int1, int1, consumer);
		return textureDraw;
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, SpriteRenderer.WallShaderTexRender wallShaderTexRender, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Consumer consumer) {
		int int1 = Color.colorToABGR(float5, float6, float7, float8);
		float float9 = 0.0F;
		float float10 = 0.0F;
		float float11 = 1.0F;
		float float12 = 0.0F;
		float float13 = 1.0F;
		float float14 = 1.0F;
		float float15 = 0.0F;
		float float16 = 1.0F;
		float float17;
		float float18;
		float float19;
		float float20;
		float float21;
		float float22;
		float float23;
		float float24;
		float float25;
		float float26;
		float float27;
		float float28;
		float float29;
		switch (wallShaderTexRender) {
		case LeftOnly: 
			float23 = float1;
			float17 = float1;
			float20 = float2;
			float18 = float2;
			float19 = float21 = float1 + float3 / 2.0F;
			float22 = float24 = float2 + float4;
			if (texture != null) {
				float25 = texture.getXEnd();
				float26 = texture.getXStart();
				float27 = texture.getYEnd();
				float28 = texture.getYStart();
				float29 = 0.5F * (float25 - float26);
				float9 = float26;
				float11 = float26 + float29;
				float13 = float26 + float29;
				float15 = float26;
				float10 = float28;
				float12 = float28;
				float14 = float27;
				float16 = float27;
			}

			break;
		
		case RightOnly: 
			float17 = float23 = float1 + float3 / 2.0F;
			float20 = float2;
			float18 = float2;
			float19 = float21 = float1 + float3;
			float22 = float24 = float2 + float4;
			if (texture != null) {
				float25 = texture.getXEnd();
				float26 = texture.getXStart();
				float27 = texture.getYEnd();
				float28 = texture.getYStart();
				float29 = 0.5F * (float25 - float26);
				float9 = float26 + float29;
				float11 = float25;
				float13 = float25;
				float15 = float26 + float29;
				float10 = float28;
				float12 = float28;
				float14 = float27;
				float16 = float27;
			}

			break;
		
		case All: 
		
		default: 
			float23 = float1;
			float17 = float1;
			float20 = float2;
			float18 = float2;
			float19 = float21 = float1 + float3;
			float22 = float24 = float2 + float4;
			if (texture != null) {
				float25 = texture.getXEnd();
				float26 = texture.getXStart();
				float27 = texture.getYEnd();
				float28 = texture.getYStart();
				float9 = float26;
				float11 = float25;
				float13 = float25;
				float15 = float26;
				float10 = float28;
				float12 = float28;
				float14 = float27;
				float16 = float27;
			}

		
		}
		Create(textureDraw, texture, float17, float18, float19, float20, float21, float22, float23, float24, int1, int1, int1, int1, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
		return textureDraw;
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Consumer consumer) {
		int int1 = Color.colorToABGR(float5, float6, float7, float8);
		Create(textureDraw, texture, float1, float2, float1 + float3, float2, float1 + float3, float2 + float4, float1, float2 + float4, int1, int1, int1, int1, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
		return textureDraw;
	}

	public static void Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, float float19, float float20, float float21, float float22, float float23, float float24, Consumer consumer) {
		int int1 = Color.colorToABGR(float9, float10, float11, float12);
		int int2 = Color.colorToABGR(float13, float14, float15, float16);
		int int3 = Color.colorToABGR(float17, float18, float19, float20);
		int int4 = Color.colorToABGR(float21, float22, float23, float24);
		Create(textureDraw, texture, float1, float2, float3, float4, float5, float6, float7, float8, int1, int2, int3, int4, consumer);
	}

	public static void Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		int int1 = Color.colorToABGR(float9, float10, float11, float12);
		Create(textureDraw, texture, float1, float2, float3, float4, float5, float6, float7, float8, int1, int1, int1, int1, (Consumer)null);
	}

	public static void Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1, int int2, int int3, int int4) {
		Create(textureDraw, texture, float1, float2, float3, float4, float5, float6, float7, float8, int1, int2, int3, int4, (Consumer)null);
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1, int int2, int int3, int int4, Consumer consumer) {
		float float9 = 0.0F;
		float float10 = 0.0F;
		float float11 = 1.0F;
		float float12 = 0.0F;
		float float13 = 1.0F;
		float float14 = 1.0F;
		float float15 = 0.0F;
		float float16 = 1.0F;
		if (texture != null) {
			float float17 = texture.getXEnd();
			float float18 = texture.getXStart();
			float float19 = texture.getYEnd();
			float float20 = texture.getYStart();
			float9 = float18;
			float10 = float20;
			float11 = float17;
			float12 = float20;
			float13 = float17;
			float14 = float19;
			float15 = float18;
			float16 = float19;
		}

		return Create(textureDraw, texture, float1, float2, float3, float4, float5, float6, float7, float8, int1, int2, int3, int4, float9, float10, float11, float12, float13, float14, float15, float16, consumer);
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1, int int2, int int3, int int4, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Consumer consumer) {
		textureDraw.bSingleCol = int1 == int2 && int1 == int3 && int1 == int4;
		textureDraw.tex = texture;
		textureDraw.x0 = float1;
		textureDraw.y0 = float2;
		textureDraw.x1 = float3;
		textureDraw.y1 = float4;
		textureDraw.x2 = float5;
		textureDraw.y2 = float6;
		textureDraw.x3 = float7;
		textureDraw.y3 = float8;
		textureDraw.col0 = int1;
		textureDraw.col1 = int2;
		textureDraw.col2 = int3;
		textureDraw.col3 = int4;
		textureDraw.u0 = float9;
		textureDraw.u1 = float11;
		textureDraw.u2 = float13;
		textureDraw.u3 = float15;
		textureDraw.v0 = float10;
		textureDraw.v1 = float12;
		textureDraw.v2 = float14;
		textureDraw.v3 = float16;
		if (texture != null) {
			textureDraw.flipped = texture.flip;
		}

		if (consumer != null) {
			consumer.accept(textureDraw);
			textureDraw.bSingleCol = textureDraw.col0 == textureDraw.col1 && textureDraw.col0 == textureDraw.col2 && textureDraw.col0 == textureDraw.col3;
		}

		return textureDraw;
	}

	public int getColor(int int1) {
		if (this.bSingleCol) {
			return this.col0;
		} else if (int1 == 0) {
			return this.col0;
		} else if (int1 == 1) {
			return this.col1;
		} else if (int1 == 2) {
			return this.col2;
		} else {
			return int1 == 3 ? this.col3 : this.col0;
		}
	}

	public void reset() {
		this.type = TextureDraw.Type.glDraw;
		this.flipped = false;
		this.tex = null;
		this.tex1 = null;
		this.useAttribArray = -1;
		this.col0 = -1;
		this.col1 = -1;
		this.col2 = -1;
		this.col3 = -1;
		this.bSingleCol = true;
		this.x0 = this.x1 = this.x2 = this.x3 = this.y0 = this.y1 = this.y2 = this.y3 = -1.0F;
		this.drawer = null;
	}

	public static void glLoadIdentity(TextureDraw textureDraw) {
		textureDraw.type = TextureDraw.Type.glLoadIdentity;
	}

	public static void glGenerateMipMaps(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glGenerateMipMaps;
		textureDraw.a = int1;
	}

	public static void glBind(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.glBind;
		textureDraw.a = int1;
	}

	public static void glViewport(TextureDraw textureDraw, int int1, int int2, int int3, int int4) {
		textureDraw.type = TextureDraw.Type.glViewport;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.c = int3;
		textureDraw.d = int4;
	}

	public void postRender() {
		if (this.type == TextureDraw.Type.StartShader) {
			Shader shader = (Shader)Shader.ShaderMap.get(this.a);
			if (shader != null) {
				shader.postRender(this);
			}
		}

		if (this.drawer != null) {
			this.drawer.postRender();
			this.drawer = null;
		}
	}

	public static enum Type {

		glDraw,
		glBuffer,
		glStencilFunc,
		glAlphaFunc,
		glStencilOp,
		glEnable,
		glDisable,
		glColorMask,
		glStencilMask,
		glClear,
		glBlendFunc,
		glDoStartFrame,
		glDoStartFrameText,
		glDoEndFrame,
		glTexParameteri,
		StartShader,
		glLoadIdentity,
		glGenerateMipMaps,
		glBind,
		glViewport,
		DrawModel,
		DrawSkyBox,
		DrawWater,
		DrawPuddles,
		DrawParticles,
		ShaderUpdate,
		BindActiveTexture,
		glBlendEquation,
		glDoStartFrameFx,
		glDoEndFrameFx,
		glIgnoreStyles,
		glClearColor,
		glBlendFuncSeparate,
		glDepthMask,
		doCoreIntParam,
		drawTerrain;

		private static TextureDraw.Type[] $values() {
			return new TextureDraw.Type[]{glDraw, glBuffer, glStencilFunc, glAlphaFunc, glStencilOp, glEnable, glDisable, glColorMask, glStencilMask, glClear, glBlendFunc, glDoStartFrame, glDoStartFrameText, glDoEndFrame, glTexParameteri, StartShader, glLoadIdentity, glGenerateMipMaps, glBind, glViewport, DrawModel, DrawSkyBox, DrawWater, DrawPuddles, DrawParticles, ShaderUpdate, BindActiveTexture, glBlendEquation, glDoStartFrameFx, glDoEndFrameFx, glIgnoreStyles, glClearColor, glBlendFuncSeparate, glDepthMask, doCoreIntParam, drawTerrain};
		}
	}

	public abstract static class GenericDrawer {
		public abstract void render();

		public void postRender() {
		}
	}
}
