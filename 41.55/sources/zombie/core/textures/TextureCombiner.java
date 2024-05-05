package zombie.core.textures;

import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjglx.opengl.OpenGLException;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.PZGLUtil;
import zombie.core.utils.ImageUtils;


public final class TextureCombiner {
	public static final TextureCombiner instance = new TextureCombiner();
	public static int count = 0;
	private TextureFBO fbo;
	private final float m_coordinateSpaceMax = 256.0F;
	private final ArrayList fboPool = new ArrayList();

	public void init() throws Exception {
	}

	public void combineStart() {
		this.clear();
		count = 33984;
		GL13.glEnable(3042);
		GL13.glEnable(3553);
		GL13.glTexEnvi(8960, 8704, 7681);
	}

	public void combineEnd() {
		GL13.glActiveTexture(33984);
	}

	public void clear() {
		for (int int1 = 33985; int1 <= count; ++int1) {
			GL13.glActiveTexture(int1);
			GL13.glDisable(3553);
		}

		GL13.glActiveTexture(33984);
	}

	public void overlay(Texture texture) {
		GL13.glActiveTexture(count);
		GL13.glEnable(3553);
		GL13.glEnable(3042);
		texture.bind();
		if (count > 33984) {
			GL13.glTexEnvi(8960, 8704, 34160);
			GL13.glTexEnvi(8960, 34161, 34165);
			GL13.glTexEnvi(8960, 34176, 34168);
			GL13.glTexEnvi(8960, 34177, 5890);
			GL13.glTexEnvi(8960, 34178, 34168);
			GL13.glTexEnvi(8960, 34192, 768);
			GL13.glTexEnvi(8960, 34193, 768);
			GL13.glTexEnvi(8960, 34194, 770);
			GL13.glTexEnvi(8960, 34162, 34165);
			GL13.glTexEnvi(8960, 34184, 34168);
			GL13.glTexEnvi(8960, 34185, 5890);
			GL13.glTexEnvi(8960, 34186, 34168);
			GL13.glTexEnvi(8960, 34200, 770);
			GL13.glTexEnvi(8960, 34201, 770);
			GL13.glTexEnvi(8960, 34202, 770);
		}

		++count;
	}

	public Texture combine(Texture texture, Texture texture2) throws Exception {
		Core.getInstance().DoStartFrameStuff(texture.width, texture2.width, 1.0F, 0);
		Texture texture3 = new Texture(texture.width, texture2.height, 16);
		if (this.fbo == null) {
			this.fbo = new TextureFBO(texture3);
		} else {
			this.fbo.setTexture(texture3);
		}

		GL13.glActiveTexture(33984);
		GL13.glEnable(3553);
		GL13.glBindTexture(3553, texture.getID());
		this.fbo.startDrawing(true, true);
		GL13.glBegin(7);
		GL13.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL13.glTexCoord2f(0.0F, 0.0F);
		GL13.glVertex2d(0.0, 0.0);
		GL13.glTexCoord2f(0.0F, 1.0F);
		GL13.glVertex2d(0.0, (double)texture.height);
		GL13.glTexCoord2f(1.0F, 1.0F);
		GL13.glVertex2d((double)texture.width, (double)texture.height);
		GL13.glTexCoord2f(1.0F, 0.0F);
		GL13.glVertex2d((double)texture.width, 0.0);
		GL13.glEnd();
		GL13.glBindTexture(3553, texture2.getID());
		GL13.glBegin(7);
		GL13.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL13.glTexCoord2f(0.0F, 0.0F);
		GL13.glVertex2d(0.0, 0.0);
		GL13.glTexCoord2f(0.0F, 1.0F);
		GL13.glVertex2d(0.0, (double)texture.height);
		GL13.glTexCoord2f(1.0F, 1.0F);
		GL13.glVertex2d((double)texture.width, (double)texture.height);
		GL13.glTexCoord2f(1.0F, 0.0F);
		GL13.glVertex2d((double)texture.width, 0.0);
		GL13.glEnd();
		this.fbo.endDrawing();
		Core.getInstance().DoEndFrameStuff(texture.width, texture2.width);
		return texture3;
	}

	public static int[] flipPixels(int[] intArray, int int1, int int2) {
		int[] intArray2 = null;
		if (intArray != null) {
			intArray2 = new int[int1 * int2];
			for (int int3 = 0; int3 < int2; ++int3) {
				for (int int4 = 0; int4 < int1; ++int4) {
					intArray2[(int2 - int3 - 1) * int1 + int4] = intArray[int3 * int1 + int4];
				}
			}
		}

		return intArray2;
	}

	private TextureCombiner.CombinerFBO getFBO(int int1, int int2) {
		for (int int3 = 0; int3 < this.fboPool.size(); ++int3) {
			TextureCombiner.CombinerFBO combinerFBO = (TextureCombiner.CombinerFBO)this.fboPool.get(int3);
			if (combinerFBO.fbo.getWidth() == int1 && combinerFBO.fbo.getHeight() == int2) {
				return combinerFBO;
			}
		}

		return null;
	}

	private Texture createTexture(int int1, int int2) {
		TextureCombiner.CombinerFBO combinerFBO = this.getFBO(int1, int2);
		Texture texture;
		if (combinerFBO == null) {
			combinerFBO = new TextureCombiner.CombinerFBO();
			texture = new Texture(int1, int2, 16);
			combinerFBO.fbo = new TextureFBO(texture);
			this.fboPool.add(combinerFBO);
		} else {
			texture = combinerFBO.textures.isEmpty() ? new Texture(int1, int2, 16) : (Texture)combinerFBO.textures.pop();
			texture.bind();
			GL11.glTexImage2D(3553, 0, 6408, texture.getWidthHW(), texture.getHeightHW(), 0, 6408, 5121, (IntBuffer)null);
			GL11.glTexParameteri(3553, 10242, 33071);
			GL11.glTexParameteri(3553, 10243, 33071);
			GL11.glTexParameteri(3553, 10240, 9729);
			GL11.glTexParameteri(3553, 10241, 9729);
			texture.dataid.setMinFilter(9729);
			Texture.lastTextureID = 0;
			GL13.glBindTexture(3553, 0);
			combinerFBO.fbo.setTexture(texture);
		}

		this.fbo = combinerFBO.fbo;
		return texture;
	}

	public void releaseTexture(Texture texture) {
		TextureCombiner.CombinerFBO combinerFBO = this.getFBO(texture.getWidth(), texture.getHeight());
		if (combinerFBO != null && combinerFBO.textures.size() < 100) {
			combinerFBO.textures.push(texture);
		} else {
			texture.destroy();
		}
	}

	public Texture combine(ArrayList arrayList) throws Exception, OpenGLException {
		PZGLUtil.checkGLErrorThrow("Enter");
		int int1 = getResultingWidth(arrayList);
		int int2 = getResultingHeight(arrayList);
		Texture texture = this.createTexture(int1, int2);
		GL13.glPushAttrib(24576);
		GL11.glDisable(3089);
		GL11.glDisable(2960);
		this.fbo.startDrawing(true, true);
		PZGLUtil.checkGLErrorThrow("FBO.startDrawing %s", this.fbo);
		Core.getInstance().DoStartFrameStuffSmartTextureFx(int1, int2, -1);
		PZGLUtil.checkGLErrorThrow("Core.DoStartFrameStuffFx w:%d, h:%d", int1, int2);
		for (int int3 = 0; int3 < arrayList.size(); ++int3) {
			TextureCombinerCommand textureCombinerCommand = (TextureCombinerCommand)arrayList.get(int3);
			if (textureCombinerCommand.shader != null) {
				textureCombinerCommand.shader.Start();
			}

			GL13.glActiveTexture(33984);
			GL11.glEnable(3553);
			Texture texture2 = textureCombinerCommand.tex == null ? Texture.getErrorTexture() : textureCombinerCommand.tex;
			texture2.bind();
			if (textureCombinerCommand.mask != null) {
				GL13.glActiveTexture(33985);
				GL13.glEnable(3553);
				int int4 = Texture.lastTextureID;
				if (textureCombinerCommand.mask.getTextureId() != null) {
					textureCombinerCommand.mask.getTextureId().setMagFilter(9728);
					textureCombinerCommand.mask.getTextureId().setMinFilter(9728);
				}

				textureCombinerCommand.mask.bind();
				Texture.lastTextureID = int4;
			} else {
				GL13.glActiveTexture(33985);
				GL13.glDisable(3553);
			}

			if (textureCombinerCommand.shader != null) {
				if (textureCombinerCommand.shaderParams != null) {
					ArrayList arrayList2 = textureCombinerCommand.shaderParams;
					for (int int5 = 0; int5 < arrayList2.size(); ++int5) {
						TextureCombinerShaderParam textureCombinerShaderParam = (TextureCombinerShaderParam)arrayList2.get(int5);
						float float1 = Rand.Next(textureCombinerShaderParam.min, textureCombinerShaderParam.max);
						textureCombinerCommand.shader.setValue(textureCombinerShaderParam.name, float1);
					}
				}

				textureCombinerCommand.shader.setValue("DIFFUSE", texture2, 0);
				if (textureCombinerCommand.mask != null) {
					textureCombinerCommand.shader.setValue("MASK", textureCombinerCommand.mask, 1);
				}
			}

			GL13.glBlendFunc(textureCombinerCommand.blendSrc, textureCombinerCommand.blendDest);
			if (textureCombinerCommand.x != -1) {
				float float2 = (float)int1 / 256.0F;
				float float3 = (float)int2 / 256.0F;
				GL13.glBegin(7);
				GL13.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL13.glTexCoord2f(0.0F, 1.0F);
				GL13.glVertex2d((double)((float)textureCombinerCommand.x * float2), (double)((float)textureCombinerCommand.y * float3));
				GL13.glTexCoord2f(0.0F, 0.0F);
				GL13.glVertex2d((double)((float)textureCombinerCommand.x * float2), (double)((float)(textureCombinerCommand.y + textureCombinerCommand.h) * float3));
				GL13.glTexCoord2f(1.0F, 0.0F);
				GL13.glVertex2d((double)((float)(textureCombinerCommand.x + textureCombinerCommand.w) * float2), (double)((float)(textureCombinerCommand.y + textureCombinerCommand.h) * float3));
				GL13.glTexCoord2f(1.0F, 1.0F);
				GL13.glVertex2d((double)((float)(textureCombinerCommand.x + textureCombinerCommand.w) * float2), (double)((float)textureCombinerCommand.y * float3));
				GL13.glEnd();
			} else {
				GL13.glBegin(7);
				GL13.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL13.glTexCoord2f(0.0F, 1.0F);
				GL13.glVertex2d(0.0, 0.0);
				GL13.glTexCoord2f(0.0F, 0.0F);
				GL13.glVertex2d(0.0, (double)int2);
				GL13.glTexCoord2f(1.0F, 0.0F);
				GL13.glVertex2d((double)int1, (double)int2);
				GL13.glTexCoord2f(1.0F, 1.0F);
				GL13.glVertex2d((double)int1, 0.0);
				GL13.glEnd();
			}

			if (textureCombinerCommand.shader != null) {
				textureCombinerCommand.shader.End();
			}

			PZGLUtil.checkGLErrorThrow("TextureCombinerCommand[%d}: %s", int3, textureCombinerCommand);
		}

		Core.getInstance().DoEndFrameStuffFx(int1, int2, -1);
		this.fbo.releaseTexture();
		this.fbo.endDrawing();
		PZGLUtil.checkGLErrorThrow("FBO.endDrawing: %s", this.fbo);
		GL13.glBlendFunc(770, 771);
		GL13.glActiveTexture(33985);
		GL13.glDisable(3553);
		if (Core.OptionModelTextureMipmaps) {
		}

		GL13.glActiveTexture(33984);
		Texture.lastTextureID = 0;
		GL13.glBindTexture(3553, 0);
		SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		GL13.glPopAttrib();
		PZGLUtil.checkGLErrorThrow("Exit.");
		return texture;
	}

	public static int getResultingHeight(ArrayList arrayList) {
		if (arrayList.isEmpty()) {
			return 32;
		} else {
			TextureCombinerCommand textureCombinerCommand = findDominantCommand(arrayList, Comparator.comparingInt((arrayListx)->{
				return arrayListx.tex.height;
			}));

			if (textureCombinerCommand == null) {
				return 32;
			} else {
				Texture texture = textureCombinerCommand.tex;
				return ImageUtils.getNextPowerOfTwoHW(texture.height);
			}
		}
	}

	public static int getResultingWidth(ArrayList arrayList) {
		if (arrayList.isEmpty()) {
			return 32;
		} else {
			TextureCombinerCommand textureCombinerCommand = findDominantCommand(arrayList, Comparator.comparingInt((arrayListx)->{
				return arrayListx.tex.width;
			}));

			if (textureCombinerCommand == null) {
				return 32;
			} else {
				Texture texture = textureCombinerCommand.tex;
				return ImageUtils.getNextPowerOfTwoHW(texture.width);
			}
		}
	}

	private static TextureCombinerCommand findDominantCommand(ArrayList arrayList, Comparator comparator) {
		TextureCombinerCommand textureCombinerCommand = null;
		int int1 = arrayList.size();
		for (int int2 = 0; int2 < int1; ++int2) {
			TextureCombinerCommand textureCombinerCommand2 = (TextureCombinerCommand)arrayList.get(int2);
			if (textureCombinerCommand2.tex != null && (textureCombinerCommand == null || comparator.compare(textureCombinerCommand2, textureCombinerCommand) > 0)) {
				textureCombinerCommand = textureCombinerCommand2;
			}
		}

		return textureCombinerCommand;
	}

	private void createMipMaps(Texture texture) {
		if (GL.getCapabilities().OpenGL30) {
			GL13.glActiveTexture(33984);
			texture.bind();
			GL30.glGenerateMipmap(3553);
			short short1 = 9987;
			GL11.glTexParameteri(3553, 10241, short1);
			texture.dataid.setMinFilter(short1);
		}
	}

	private static final class CombinerFBO {
		TextureFBO fbo;
		final ArrayDeque textures = new ArrayDeque();
	}
}
