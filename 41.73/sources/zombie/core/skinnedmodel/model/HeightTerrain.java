package zombie.core.skinnedmodel.model;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.textures.Texture;
import zombie.creative.creativerects.OpenSimplexNoise;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;


public final class HeightTerrain {
	private final ByteBuffer buffer;
	public VertexBufferObject vb;
	public static float isoAngle = 62.65607F;
	public static float scale = 0.047085002F;
	OpenSimplexNoise noise = new OpenSimplexNoise((long)Rand.Next(10000000));
	static float[] lightAmbient = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] lightDiffuse = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] lightPosition = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] specular = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] shininess = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] emission = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] ambient = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static float[] diffuse = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	static ByteBuffer temp = ByteBuffer.allocateDirect(16);

	public HeightTerrain(int int1, int int2) {
		ArrayList arrayList = new ArrayList();
		int int3 = int1 * int2;
		int int4 = int1;
		int int5 = int2;
		ArrayList arrayList2 = new ArrayList();
		Vector2 vector2 = new Vector2(2.0F, 0.0F);
		boolean boolean1 = false;
		int int6;
		int int7;
		float float1;
		VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin;
		for (int6 = 0; int6 < int4; ++int6) {
			for (int7 = 0; int7 < int5; ++int7) {
				float1 = (float)this.calc((float)int6, (float)int7);
				float1 *= 1.0F;
				++float1;
				vertexPositionNormalTangentTextureSkin = null;
				vertexPositionNormalTangentTextureSkin = new VertexPositionNormalTangentTextureSkin();
				vertexPositionNormalTangentTextureSkin.Position = new Vector3();
				vertexPositionNormalTangentTextureSkin.Position.set((float)(-int6), float1 * 30.0F, (float)(-int7));
				vertexPositionNormalTangentTextureSkin.Normal = new Vector3();
				vertexPositionNormalTangentTextureSkin.Normal.set(0.0F, 1.0F, 0.0F);
				vertexPositionNormalTangentTextureSkin.Normal.normalize();
				vertexPositionNormalTangentTextureSkin.TextureCoordinates = new Vector2();
				vertexPositionNormalTangentTextureSkin.TextureCoordinates = new Vector2((float)int6 / (float)(int4 - 1) * 16.0F, (float)int7 / (float)(int5 - 1) * 16.0F);
				arrayList.add(vertexPositionNormalTangentTextureSkin);
			}
		}

		int int8 = 0;
		for (int6 = 0; int6 < int4; ++int6) {
			for (int7 = 0; int7 < int5; ++int7) {
				float1 = (float)this.calc((float)int6, (float)int7);
				float1 *= 1.0F;
				++float1;
				float1 = Math.max(0.0F, float1);
				float1 = Math.min(1.0F, float1);
				vertexPositionNormalTangentTextureSkin = null;
				vertexPositionNormalTangentTextureSkin = (VertexPositionNormalTangentTextureSkin)arrayList.get(int8);
				Vector3 vector3 = new Vector3();
				Vector3 vector32 = new Vector3();
				float float2 = (float)this.calc((float)(int6 + 1), (float)int7);
				float2 *= 1.0F;
				++float2;
				float float3 = (float)this.calc((float)(int6 - 1), (float)int7);
				float3 *= 1.0F;
				++float3;
				float float4 = (float)this.calc((float)int6, (float)(int7 + 1));
				float4 *= 1.0F;
				++float4;
				float float5 = (float)this.calc((float)int6, (float)(int7 - 1));
				float5 *= 1.0F;
				++float5;
				float float6 = float2 * 700.0F;
				float float7 = float3 * 700.0F;
				float float8 = float4 * 700.0F;
				float float9 = float5 * 700.0F;
				vector3.set(vector2.x, vector2.y, float6 - float7);
				vector32.set(vector2.y, vector2.x, float8 - float9);
				vector3.normalize();
				vector32.normalize();
				Vector3 vector33 = vector3.cross(vector32);
				vertexPositionNormalTangentTextureSkin.Normal.x(vector33.x());
				vertexPositionNormalTangentTextureSkin.Normal.y(vector33.z());
				vertexPositionNormalTangentTextureSkin.Normal.z(vector33.y());
				vertexPositionNormalTangentTextureSkin.Normal.normalize();
				PrintStream printStream = System.out;
				float float10 = vertexPositionNormalTangentTextureSkin.Normal.x();
				printStream.println(float10 + " , " + vertexPositionNormalTangentTextureSkin.Normal.y() + ", " + vertexPositionNormalTangentTextureSkin.Normal.z());
				vertexPositionNormalTangentTextureSkin.Normal.normalize();
				++int8;
			}
		}

		int8 = 0;
		for (int6 = 0; int6 < int5 - 1; ++int6) {
			if ((int6 & 1) == 0) {
				for (int7 = 0; int7 < int4; ++int7) {
					arrayList2.add(int7 + (int6 + 1) * int4);
					arrayList2.add(int7 + int6 * int4);
					++int8;
					++int8;
				}
			} else {
				for (int7 = int4 - 1; int7 > 0; --int7) {
					arrayList2.add(int7 - 1 + int6 * int4);
					arrayList2.add(int7 + (int6 + 1) * int4);
					++int8;
					++int8;
				}
			}
		}

		if ((int4 & 1) > 0 && int5 > 2) {
			arrayList2.add((int5 - 1) * int4);
			++int8;
		}

		this.vb = new VertexBufferObject();
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(arrayList.size() * 36);
		for (int7 = 0; int7 < arrayList.size(); ++int7) {
			VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin2 = (VertexPositionNormalTangentTextureSkin)arrayList.get(int7);
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.x());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.y());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Position.z());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.x());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.y());
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.Normal.z());
			byte byte1 = -1;
			byteBuffer.putInt(byte1);
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.TextureCoordinates.x);
			byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin2.TextureCoordinates.y);
		}

		byteBuffer.flip();
		int[] intArray = new int[arrayList2.size()];
		for (int int9 = 0; int9 < arrayList2.size(); ++int9) {
			Integer integer = (Integer)arrayList2.get(arrayList2.size() - 1 - int9);
			intArray[int9] = integer;
		}

		this.vb._handle = this.vb.LoadSoftwareVBO(byteBuffer, this.vb._handle, intArray);
		this.buffer = byteBuffer;
	}

	double calcTerrain(float float1, float float2) {
		float1 *= 10.0F;
		float2 *= 10.0F;
		double double1 = this.noise.eval((double)(float1 / 900.0F), (double)(float2 / 600.0F), 0.0);
		double1 += this.noise.eval((double)(float1 / 600.0F), (double)(float2 / 600.0F), 0.0) / 4.0;
		double1 += (this.noise.eval((double)(float1 / 300.0F), (double)(float2 / 300.0F), 0.0) + 1.0) / 8.0;
		double1 += (this.noise.eval((double)(float1 / 150.0F), (double)(float2 / 150.0F), 0.0) + 1.0) / 16.0;
		double1 += (this.noise.eval((double)(float1 / 75.0F), (double)(float2 / 75.0F), 0.0) + 1.0) / 32.0;
		double double2 = (this.noise.eval((double)float1, (double)float2, 0.0) + 1.0) / 2.0;
		double double3 = double2 * ((this.noise.eval((double)float1, (double)float2, 0.0) + 1.0) / 2.0);
		return double1;
	}

	double calc(float float1, float float2) {
		return this.calcTerrain(float1, float2);
	}

	public void pushView(int int1, int int2, int int3) {
		GL11.glDepthMask(false);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		float float1 = 0.6F;
		byte byte1 = 0;
		byte byte2 = 0;
		int int4 = byte1 + IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex());
		int int5 = byte2 + IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex());
		double double1 = (double)IsoUtils.XToIso((float)byte1, (float)byte2, 0.0F);
		double double2 = (double)IsoUtils.YToIso(0.0F, 0.0F, 0.0F);
		double double3 = (double)IsoUtils.XToIso((float)Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()), 0.0F, 0.0F);
		double double4 = (double)IsoUtils.YToIso((float)int4, (float)byte2, 0.0F);
		double double5 = (double)IsoUtils.XToIso((float)int4, (float)int5, 0.0F);
		double double6 = (double)IsoUtils.YToIso((float)Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()), (float)Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()), 6.0F);
		double double7 = (double)IsoUtils.XToIso(-128.0F, (float)Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()), 6.0F);
		double double8 = (double)IsoUtils.YToIso((float)byte1, (float)int5, 0.0F);
		double double9 = double5 - double1;
		double9 = double8 - double4;
		double double10 = (double)((float)Math.abs(Core.getInstance().getOffscreenWidth(0)) / 1920.0F);
		double double11 = (double)((float)Math.abs(Core.getInstance().getOffscreenHeight(0)) / 1080.0F);
		GL11.glLoadIdentity();
		GL11.glOrtho(-double10 / 2.0, double10 / 2.0, -double11 / 2.0, double11 / 2.0, -10.0, 10.0);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glScaled((double)scale, (double)scale, (double)scale);
		GL11.glRotatef(isoAngle, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslated((double)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2), 0.0, (double)(IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2));
		GL11.glDepthRange(-100.0, 100.0);
	}

	public void popView() {
		GL11.glEnable(3008);
		GL11.glDepthFunc(519);
		GL11.glDepthMask(false);
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
	}

	public void render() {
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glDisable(2884);
		GL11.glEnable(2929);
		GL11.glDepthFunc(519);
		GL11.glColorMask(true, true, true, true);
		GL11.glAlphaFunc(519, 0.0F);
		GL11.glDepthFunc(519);
		GL11.glDepthRange(-10.0, 10.0);
		GL11.glEnable(2903);
		GL11.glEnable(2896);
		GL11.glEnable(16384);
		GL11.glEnable(16385);
		GL11.glEnable(2929);
		GL11.glDisable(3008);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(3008);
		GL11.glAlphaFunc(519, 0.0F);
		GL11.glDisable(3089);
		this.doLighting();
		GL11.glDisable(2929);
		GL11.glEnable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glCullFace(1029);
		this.pushView(IsoPlayer.getInstance().getCurrentSquare().getChunk().wx / 30 * 300, IsoPlayer.getInstance().getCurrentSquare().getChunk().wy / 30 * 300, 0);
		Texture.getSharedTexture("media/textures/grass.png").bind();
		this.vb.DrawStrip((Shader)null);
		this.popView();
		GL11.glEnable(3042);
		GL11.glDisable(3008);
		GL11.glDisable(2929);
		GL11.glEnable(6144);
		if (PerformanceSettings.ModelLighting) {
			GL11.glDisable(2903);
			GL11.glDisable(2896);
			GL11.glDisable(16384);
			GL11.glDisable(16385);
		}

		GL11.glDepthRange(0.0, 100.0);
		SpriteRenderer.ringBuffer.restoreVBOs = true;
		GL11.glEnable(2929);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
		GL11.glEnable(3553);
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
	}

	private void doLighting() {
		temp.order(ByteOrder.nativeOrder());
		temp.clear();
		GL11.glColorMaterial(1032, 5634);
		GL11.glDisable(2903);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2896);
		GL11.glEnable(16384);
		GL11.glDisable(16385);
		lightAmbient[0] = 0.7F;
		lightAmbient[1] = 0.7F;
		lightAmbient[2] = 0.7F;
		lightAmbient[3] = 0.5F;
		lightDiffuse[0] = 0.5F;
		lightDiffuse[1] = 0.5F;
		lightDiffuse[2] = 0.5F;
		lightDiffuse[3] = 1.0F;
		Vector3 vector3 = new Vector3(1.0F, 1.0F, 1.0F);
		vector3.normalize();
		lightPosition[0] = -vector3.x();
		lightPosition[1] = vector3.y();
		lightPosition[2] = -vector3.z();
		lightPosition[3] = 0.0F;
		GL11.glLightfv(16384, 4608, temp.asFloatBuffer().put(lightAmbient).flip());
		GL11.glLightfv(16384, 4609, temp.asFloatBuffer().put(lightDiffuse).flip());
		GL11.glLightfv(16384, 4611, temp.asFloatBuffer().put(lightPosition).flip());
		GL11.glLightf(16384, 4615, 0.0F);
		GL11.glLightf(16384, 4616, 0.0F);
		GL11.glLightf(16384, 4617, 0.0F);
		specular[0] = 0.0F;
		specular[1] = 0.0F;
		specular[2] = 0.0F;
		specular[3] = 0.0F;
		GL11.glMaterialfv(1032, 4610, temp.asFloatBuffer().put(specular).flip());
		GL11.glMaterialfv(1032, 5633, temp.asFloatBuffer().put(specular).flip());
		GL11.glMaterialfv(1032, 5632, temp.asFloatBuffer().put(specular).flip());
		ambient[0] = 0.6F;
		ambient[1] = 0.6F;
		ambient[2] = 0.6F;
		ambient[3] = 1.0F;
		diffuse[0] = 0.6F;
		diffuse[1] = 0.6F;
		diffuse[2] = 0.6F;
		diffuse[3] = 0.6F;
		GL11.glMaterialfv(1032, 4608, temp.asFloatBuffer().put(ambient).flip());
		GL11.glMaterialfv(1032, 4609, temp.asFloatBuffer().put(diffuse).flip());
	}
}
