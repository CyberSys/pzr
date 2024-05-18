package zombie.core.textures;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import zombie.IndieGL;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.ui.UIManager;


public class TextureDraw {
	public TextureDraw.Type type;
	public int a;
	public int b;
	public float f1;
	public float[] vars;
	public int c;
	public int d;
	public int[] col;
	public short[] x;
	public short[] y;
	public float[] u;
	public float[] v;
	public Texture tex;
	public boolean bSingleCol;
	public DeadBodyAtlas.RenderJob job;
	public boolean flipped;
	public TextureDraw.GenericDrawer drawer;

	public TextureDraw() {
		this.type = TextureDraw.Type.glDraw;
		this.a = 0;
		this.b = 0;
		this.f1 = 0.0F;
		this.c = 0;
		this.d = 0;
		this.col = new int[4];
		this.x = new short[4];
		this.y = new short[4];
		this.u = new float[4];
		this.v = new float[4];
		this.bSingleCol = false;
		this.flipped = false;
	}

	public static void Create(TextureDraw textureDraw, Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		textureDraw.bSingleCol = false;
		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		if (float2 > 1.0F) {
			float2 = 1.0F;
		}

		if (float3 > 1.0F) {
			float3 = 1.0F;
		}

		if (float4 > 1.0F) {
			float4 = 1.0F;
		}

		if (float5 > 1.0F) {
			float5 = 1.0F;
		}

		if (float6 > 1.0F) {
			float6 = 1.0F;
		}

		if (float7 > 1.0F) {
			float7 = 1.0F;
		}

		if (float8 > 1.0F) {
			float8 = 1.0F;
		}

		if (float9 > 1.0F) {
			float9 = 1.0F;
		}

		if (float10 > 1.0F) {
			float10 = 1.0F;
		}

		if (float11 > 1.0F) {
			float11 = 1.0F;
		}

		if (float12 > 1.0F) {
			float12 = 1.0F;
		}

		if (float13 > 1.0F) {
			float13 = 1.0F;
		}

		if (float14 > 1.0F) {
			float14 = 1.0F;
		}

		if (float15 > 1.0F) {
			float15 = 1.0F;
		}

		if (float16 > 1.0F) {
			float16 = 1.0F;
		}

		textureDraw.tex = texture;
		textureDraw.x[0] = (short)int1;
		textureDraw.y[0] = (short)int2;
		textureDraw.x[1] = (short)int3;
		textureDraw.y[1] = (short)int4;
		textureDraw.x[2] = (short)int5;
		textureDraw.y[2] = (short)int6;
		textureDraw.x[3] = (short)int7;
		textureDraw.y[3] = (short)int8;
		if (texture != null) {
			float float17 = texture.getXEnd();
			float float18 = texture.getXStart();
			float float19 = texture.getYEnd();
			float float20 = texture.getYStart();
			textureDraw.u[0] = float18;
			textureDraw.u[1] = float17;
			textureDraw.u[2] = float17;
			textureDraw.u[3] = float18;
			textureDraw.v[0] = float20;
			textureDraw.v[1] = float20;
			textureDraw.v[2] = float19;
			textureDraw.v[3] = float19;
		}

		textureDraw.col[0] = (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | (int)(float4 * 255.0F) << 24;
		textureDraw.col[1] = (int)(float5 * 255.0F) << 0 | (int)(float6 * 255.0F) << 8 | (int)(float7 * 255.0F) << 16 | (int)(float8 * 255.0F) << 24;
		textureDraw.col[2] = (int)(float9 * 255.0F) << 0 | (int)(float10 * 255.0F) << 8 | (int)(float11 * 255.0F) << 16 | (int)(float12 * 255.0F) << 24;
		textureDraw.col[3] = (int)(float13 * 255.0F) << 0 | (int)(float14 * 255.0F) << 8 | (int)(float15 * 255.0F) << 16 | (int)(float16 * 255.0F) << 24;
	}

	public static void Create(TextureDraw textureDraw, Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, float float1, float float2, float float3, float float4) {
		textureDraw.bSingleCol = false;
		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		if (float2 > 1.0F) {
			float2 = 1.0F;
		}

		if (float3 > 1.0F) {
			float3 = 1.0F;
		}

		if (float4 > 1.0F) {
			float4 = 1.0F;
		}

		textureDraw.tex = texture;
		textureDraw.x[0] = (short)int1;
		textureDraw.y[0] = (short)int2;
		textureDraw.x[1] = (short)int3;
		textureDraw.y[1] = (short)int4;
		textureDraw.x[2] = (short)int5;
		textureDraw.y[2] = (short)int6;
		textureDraw.x[3] = (short)int7;
		textureDraw.y[3] = (short)int8;
		textureDraw.col[3] = textureDraw.col[2] = textureDraw.col[1] = textureDraw.col[0] = (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | (int)(float4 * 255.0F) << 24;
	}

	public static void Create(TextureDraw textureDraw, Texture texture, int int1, int int2, int int3, int int4, int int5, int int6, int int7, int int8, int int9, int int10, int int11, int int12) {
		textureDraw.bSingleCol = false;
		textureDraw.tex = texture;
		textureDraw.x[0] = (short)int1;
		textureDraw.y[0] = (short)int2;
		textureDraw.x[1] = (short)int3;
		textureDraw.y[1] = (short)int4;
		textureDraw.x[2] = (short)int5;
		textureDraw.y[2] = (short)int6;
		textureDraw.x[3] = (short)int7;
		textureDraw.y[3] = (short)int8;
		textureDraw.col[0] = int9;
		textureDraw.col[1] = int10;
		textureDraw.col[2] = int11;
		textureDraw.col[3] = int12;
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
		textureDraw.col[0] = int1;
		textureDraw.col[1] = int2;
		textureDraw.col[2] = int3;
		textureDraw.col[3] = int4;
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
		textureDraw.x[0] = (short)int4;
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

	public static void glDoStartFrame(TextureDraw textureDraw, int int1, int int2, int int3) {
		glDoStartFrame(textureDraw, int1, int2, int3, false);
	}

	public static void glDoStartFrame(TextureDraw textureDraw, int int1, int int2, int int3, boolean boolean1) {
		if (boolean1) {
			textureDraw.type = TextureDraw.Type.glDoStartFrameText;
		} else {
			textureDraw.type = TextureDraw.Type.glDoStartFrame;
		}

		textureDraw.a = int1;
		textureDraw.b = int2;
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
		textureDraw.drawer = null;
	}

	public static void drawSkyBox(TextureDraw textureDraw, Shader shader, int int1, int int2, int int3) {
		textureDraw.type = TextureDraw.Type.DrawSkyBox;
		textureDraw.a = shader.ShaderID;
		textureDraw.b = int1;
		textureDraw.c = int2;
		textureDraw.d = int3;
		textureDraw.drawer = null;
	}

	public static void toBodyAtlas(TextureDraw textureDraw, DeadBodyAtlas.RenderJob renderJob) {
		textureDraw.type = TextureDraw.Type.ToBodyAtlas;
		textureDraw.job = renderJob;
	}

	public static void StartShader(TextureDraw textureDraw, int int1) {
		textureDraw.type = TextureDraw.Type.StartShader;
		textureDraw.a = int1;
	}

	public static void ShaderUpdate(TextureDraw textureDraw, int int1, int int2, float float1) {
		textureDraw.type = TextureDraw.Type.ShaderUpdate;
		textureDraw.a = int1;
		textureDraw.b = int2;
		textureDraw.f1 = float1;
	}

	public void run() {
		switch (this.type) {
		case StartShader: 
			ARBShaderObjects.glUseProgramObjectARB(this.a);
			if (Shader.ShaderMap.containsKey(this.a)) {
				((Shader)Shader.ShaderMap.get(this.a)).updateParams(this);
			}

			break;
		
		case ShaderUpdate: 
			ARBShaderObjects.glUniform1fARB(this.b, this.f1);
			break;
		
		case DrawModel: 
			if (this.drawer != null) {
				this.drawer.render();
			} else {
				try {
					ModelManager.instance.DoRender(this.a);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			break;
		
		case DrawSkyBox: 
			try {
				ModelManager.instance.RenderSkyBox(this, this.a, this.b, this.c, this.d);
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			break;
		
		case ToBodyAtlas: 
			try {
				DeadBodyAtlas.instance.toBodyAtlas(this.job);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

			break;
		
		case glClear: 
			IndieGL.glClearA(this.a);
			break;
		
		case glClearColor: 
			GL11.glClearColor((float)this.col[0] / 255.0F, (float)this.col[1] / 255.0F, (float)this.col[2] / 255.0F, (float)this.col[3] / 255.0F);
			break;
		
		case glBlendFunc: 
			IndieGL.glBlendFuncA(this.a, this.b);
			break;
		
		case glBlendFuncSeparate: 
			GL14.glBlendFuncSeparate(this.a, this.b, this.c, this.d);
			break;
		
		case glColorMask: 
			IndieGL.glColorMaskA(this.a == 1, this.b == 1, this.c == 1, this.x[0] == 1);
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
			Core.getInstance().DoStartFrameStuff(this.a, this.b, this.c);
			break;
		
		case glDoStartFrameText: 
			Core.getInstance().DoStartFrameStuff(this.a, this.b, this.c, true);
			break;
		
		case glDoStartFrameFx: 
			Core.getInstance().DoStartFrameStuffFx(this.a, this.b, this.c);
			break;
		
		case PopIso: 
			Core.getInstance().DoPopIsoStuff();
			break;
		
		case PushIso: 
			Core.getInstance().DoPushIsoStuff();
			break;
		
		case glStencilFunc: 
			IndieGL.glStencilFuncA(this.a, this.b, this.c);
			break;
		
		case glBuffer: 
			if (Core.getInstance().supportsFBO()) {
				if (this.a == 1) {
					SpriteRenderer.instance.states[2].fbo.startDrawing(false, false);
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
					SpriteRenderer.instance.states[2].fbo.endDrawing();
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
			GL11.glViewport(0, 0, this.a, this.b);
			break;
		
		case glGenerateMipMaps: 
			Core.getInstance().OffscreenBuffer.updateMipMaps();
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

	public static void PopIso(TextureDraw textureDraw) {
		textureDraw.type = TextureDraw.Type.PopIso;
	}

	public static void PushIso(TextureDraw textureDraw) {
		textureDraw.type = TextureDraw.Type.PushIso;
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4) {
		textureDraw.bSingleCol = true;
		textureDraw.tex = texture;
		textureDraw.x[0] = textureDraw.x[3] = (short)int1;
		textureDraw.y[0] = textureDraw.y[1] = (short)int2;
		textureDraw.x[1] = textureDraw.x[2] = (short)(int1 + int3);
		textureDraw.y[2] = textureDraw.y[3] = (short)(int2 + int4);
		textureDraw.col[0] = textureDraw.col[1] = textureDraw.col[2] = textureDraw.col[3] = (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | (int)(float4 * 255.0F) << 24;
		if (texture != null) {
			float float5 = texture.getXEnd();
			float float6 = texture.getXStart();
			float float7 = texture.getYEnd();
			float float8 = texture.getYStart();
			textureDraw.u[0] = float6;
			textureDraw.u[1] = float5;
			textureDraw.u[2] = float5;
			textureDraw.u[3] = float6;
			textureDraw.v[0] = float8;
			textureDraw.v[1] = float8;
			textureDraw.v[2] = float7;
			textureDraw.v[3] = float7;
		}

		return textureDraw;
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, int int1, int int2, int int3, int int4, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		textureDraw.bSingleCol = true;
		textureDraw.tex = texture;
		textureDraw.x[0] = textureDraw.x[3] = (short)int1;
		textureDraw.y[0] = textureDraw.y[1] = (short)int2;
		textureDraw.x[1] = textureDraw.x[2] = (short)(int1 + int3);
		textureDraw.y[2] = textureDraw.y[3] = (short)(int2 + int4);
		if (texture != null) {
			textureDraw.flipped = texture.flip;
		}

		textureDraw.col[0] = textureDraw.col[1] = textureDraw.col[2] = textureDraw.col[3] = (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | (int)(float4 * 255.0F) << 24;
		if (texture != null) {
			textureDraw.u[0] = float5;
			textureDraw.u[1] = float7;
			textureDraw.u[2] = float9;
			textureDraw.u[3] = float11;
			textureDraw.v[0] = float6;
			textureDraw.v[1] = float8;
			textureDraw.v[2] = float10;
			textureDraw.v[3] = float12;
		}

		return textureDraw;
	}

	public static TextureDraw Create(TextureDraw textureDraw, Texture texture, int int1, int int2, int int3, int int4, int int5) {
		textureDraw.bSingleCol = true;
		textureDraw.tex = texture;
		textureDraw.x[0] = textureDraw.x[3] = (short)int1;
		textureDraw.y[0] = textureDraw.y[1] = (short)int2;
		textureDraw.x[1] = textureDraw.x[2] = (short)(int1 + int3);
		textureDraw.y[2] = textureDraw.y[3] = (short)(int2 + int4);
		if (texture != null) {
			textureDraw.flipped = texture.flip;
		}

		textureDraw.col[0] = int5;
		textureDraw.col[1] = int5;
		textureDraw.col[2] = int5;
		textureDraw.col[3] = int5;
		if (texture != null) {
			textureDraw.u[0] = texture.getXStart();
			textureDraw.u[1] = texture.getXEnd();
			textureDraw.u[2] = texture.getXEnd();
			textureDraw.u[3] = texture.getXStart();
			textureDraw.v[0] = texture.getYStart();
			textureDraw.v[1] = texture.getYStart();
			textureDraw.v[2] = texture.getYEnd();
			textureDraw.v[3] = texture.getYEnd();
		}

		return textureDraw;
	}

	public int getColor(int int1) {
		if (this.bSingleCol) {
			return this.col[0];
		} else if (int1 == 0) {
			return this.col[0];
		} else if (int1 == 1) {
			return this.col[1];
		} else if (int1 == 2) {
			return this.col[2];
		} else {
			return int1 == 3 ? this.col[3] : this.col[0];
		}
	}

	public void reset() {
		this.type = TextureDraw.Type.glDraw;
		this.flipped = false;
		this.tex = null;
		this.col[0] = -1;
		this.col[1] = -1;
		this.col[2] = -1;
		this.col[3] = -1;
		this.bSingleCol = true;
		this.x[0] = this.x[1] = this.x[2] = this.x[3] = this.y[0] = this.y[1] = this.y[2] = this.y[3] = -1;
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
		textureDraw.a = int3;
		textureDraw.b = int4;
	}

	public static class GenericDrawer {

		public void render() {
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
		PushIso,
		PopIso,
		ToBodyAtlas,
		ShaderUpdate,
		glBlendEquation,
		glDoStartFrameFx,
		glDoEndFrameFx,
		glIgnoreStyles,
		glClearColor,
		glBlendFuncSeparate;
	}
}
