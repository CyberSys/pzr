package zombie.iso;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.popman.ObjectPool;


public final class IsoPuddlesCompute {
	private static final float Pi = 3.1415F;
	private static float puddlesDirNE;
	private static float puddlesDirNW;
	private static float puddlesDirAll;
	private static float puddlesDirNone;
	private static float puddlesSize;
	private static boolean hd_quality = true;
	private static final Vector2f add = new Vector2f(1.0F, 0.0F);
	private static final Vector3f add_xyy = new Vector3f(1.0F, 0.0F, 0.0F);
	private static final Vector3f add_xxy = new Vector3f(1.0F, 1.0F, 0.0F);
	private static final Vector3f add_xxx = new Vector3f(1.0F, 1.0F, 1.0F);
	private static final Vector3f add_xyx = new Vector3f(1.0F, 0.0F, 1.0F);
	private static final Vector3f add_yxy = new Vector3f(0.0F, 1.0F, 0.0F);
	private static final Vector3f add_yyx = new Vector3f(0.0F, 0.0F, 1.0F);
	private static final Vector3f add_yxx = new Vector3f(0.0F, 1.0F, 1.0F);
	private static final Vector3f HashVector31 = new Vector3f(17.1F, 31.7F, 32.6F);
	private static final Vector3f HashVector32 = new Vector3f(29.5F, 13.3F, 42.6F);
	private static final ObjectPool pool_vector3f = new ObjectPool(Vector3f::new);
	private static final ArrayList allocated_vector3f = new ArrayList();
	private static final Vector2f temp_vector2f = new Vector2f();

	private static Vector3f allocVector3f(float float1, float float2, float float3) {
		Vector3f vector3f = ((Vector3f)pool_vector3f.alloc()).set(float1, float2, float3);
		allocated_vector3f.add(vector3f);
		return vector3f;
	}

	private static Vector3f allocVector3f(Vector3f vector3f) {
		return allocVector3f(vector3f.x, vector3f.y, vector3f.z);
	}

	private static Vector3f floor(Vector3f vector3f) {
		return allocVector3f((float)Math.floor((double)vector3f.x), (float)Math.floor((double)vector3f.y), (float)Math.floor((double)vector3f.z));
	}

	private static Vector3f fract(Vector3f vector3f) {
		return allocVector3f(fract(vector3f.x), fract(vector3f.y), fract(vector3f.z));
	}

	private static float fract(float float1) {
		return (float)((double)float1 - Math.floor((double)float1));
	}

	private static float mix(float float1, float float2, float float3) {
		return float1 * (1.0F - float3) + float2 * float3;
	}

	private static float FuncHash(Vector3f vector3f) {
		Vector3f vector3f2 = allocVector3f(vector3f.dot(HashVector31), vector3f.dot(HashVector32), 0.0F);
		return fract((float)(Math.sin((double)vector3f2.x * 2.1 + 1.1) + Math.sin((double)vector3f2.y * 2.5 + 1.5)));
	}

	private static float FuncNoise(Vector3f vector3f) {
		Vector3f vector3f2 = floor(vector3f);
		Vector3f vector3f3 = fract(vector3f);
		Vector3f vector3f4 = allocVector3f(vector3f3.x * vector3f3.x * (4.5F - 3.5F * vector3f3.x), vector3f3.y * vector3f3.y * (4.5F - 3.5F * vector3f3.y), vector3f3.z * vector3f3.z * (4.5F - 3.5F * vector3f3.z));
		float float1 = mix(FuncHash(vector3f2), FuncHash(allocVector3f(vector3f2).add(add_xyy)), vector3f4.x);
		float float2 = mix(FuncHash(allocVector3f(vector3f2).add(add_yxy)), FuncHash(allocVector3f(vector3f2).add(add_xxy)), vector3f4.x);
		float float3 = mix(FuncHash(allocVector3f(vector3f2).add(add_yyx)), FuncHash(allocVector3f(vector3f2).add(add_xyx)), vector3f4.x);
		float float4 = mix(FuncHash(allocVector3f(vector3f2).add(add_yxx)), FuncHash(allocVector3f(vector3f2).add(add_xxx)), vector3f4.x);
		float float5 = mix(float1, float2, vector3f4.y);
		float float6 = mix(float3, float4, vector3f4.y);
		return mix(float5, float6, vector3f4.z);
	}

	private static float PerlinNoise(Vector3f vector3f) {
		if (hd_quality) {
			vector3f.mul(0.5F);
			float float1 = 0.5F * FuncNoise(vector3f);
			vector3f.mul(3.0F);
			float1 = (float)((double)float1 + 0.25 * (double)FuncNoise(vector3f));
			vector3f.mul(3.0F);
			float1 = (float)((double)float1 + 0.125 * (double)FuncNoise(vector3f));
			float1 = (float)((double)float1 * Math.min(1.0, 2.0 * (double)FuncNoise(allocVector3f(vector3f).mul(0.02F)) * Math.min(1.0, 1.0 * (double)FuncNoise(allocVector3f(vector3f).mul(0.1F)))));
			return float1;
		} else {
			return FuncNoise(vector3f) * 0.5F;
		}
	}

	private static float getPuddles(Vector2f vector2f) {
		float float1 = puddlesDirNE;
		float float2 = puddlesDirNW;
		float float3 = puddlesDirAll;
		vector2f.mul(10.0F);
		float float4 = 1.02F * puddlesSize;
		float4 = (float)((double)float4 + (double)float1 * Math.sin(((double)vector2f.x * 1.0 + (double)vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * Math.cos(((double)vector2f.x * 1.0 + (double)vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * 2.0);
		float4 = (float)((double)float4 + (double)float2 * Math.sin(((double)vector2f.x * 1.0 - (double)vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * Math.cos(((double)vector2f.x * 1.0 - (double)vector2f.y * 2.0) * 3.1414999961853027 * 1.0) * 2.0);
		float4 = (float)((double)float4 + (double)float3 * 0.3);
		float float5 = PerlinNoise(allocVector3f(vector2f.x * 1.0F, 0.0F, vector2f.y * 2.0F));
		float float6 = Math.min(0.7F, float4 * float5);
		float5 = Math.min(0.7F, PerlinNoise(allocVector3f(vector2f.x * 0.7F, 1.0F, vector2f.y * 0.7F)));
		return float6 + float5;
	}

	public static float computePuddle(IsoGridSquare square) {
		pool_vector3f.release((List)allocated_vector3f);
		allocated_vector3f.clear();
		hd_quality = PerformanceSettings.PuddlesQuality == 0;
		if (!Core.getInstance().getUseShaders()) {
			return -0.1F;
		} else if (Core.getInstance().getPerfPuddlesOnLoad() != 3 && Core.getInstance().getPerfPuddles() != 3) {
			if (Core.getInstance().getPerfPuddles() > 0 && square.z > 0) {
				return -0.1F;
			} else {
				IsoPuddles puddles = IsoPuddles.getInstance();
				puddlesSize = puddles.getPuddlesSize();
				if (puddlesSize <= 0.0F) {
					return -0.1F;
				} else {
					Vector4f vector4f = puddles.getShaderOffsetMain();
					vector4f.x -= 90000.0F;
					vector4f.y -= 640000.0F;
					int int1 = (int)IsoCamera.frameState.OffX;
					int int2 = (int)IsoCamera.frameState.OffY;
					float float1 = IsoUtils.XToScreen((float)square.x + 0.5F - (float)square.z * 3.0F, (float)square.y + 0.5F - (float)square.z * 3.0F, 0.0F, 0) - (float)int1;
					float float2 = IsoUtils.YToScreen((float)square.x + 0.5F - (float)square.z * 3.0F, (float)square.y + 0.5F - (float)square.z * 3.0F, 0.0F, 0) - (float)int2;
					float1 /= (float)IsoCamera.frameState.OffscreenWidth;
					float2 /= (float)IsoCamera.frameState.OffscreenHeight;
					if (Core.getInstance().getPerfPuddles() <= 1) {
						square.getPuddles().recalcIfNeeded();
						puddlesDirNE = (square.getPuddles().pdne[0] + square.getPuddles().pdne[2]) * 0.5F;
						puddlesDirNW = (square.getPuddles().pdnw[0] + square.getPuddles().pdnw[2]) * 0.5F;
						puddlesDirAll = (square.getPuddles().pda[0] + square.getPuddles().pda[2]) * 0.5F;
						puddlesDirNone = (square.getPuddles().pnon[0] + square.getPuddles().pnon[2]) * 0.5F;
					} else {
						puddlesDirNE = 0.0F;
						puddlesDirNW = 0.0F;
						puddlesDirAll = 1.0F;
						puddlesDirNone = 0.0F;
					}

					Vector2f vector2f = temp_vector2f.set((float1 * vector4f.z + vector4f.x) * 8.0E-4F + (float)square.z * 7.0F, (float2 * vector4f.w + vector4f.y) * 8.0E-4F + (float)square.z * 7.0F);
					float float3 = (float)Math.pow((double)getPuddles(vector2f), 2.0);
					float float4 = (float)Math.min(Math.pow((double)float3, 0.3), 1.0) + float3;
					return float4 * puddlesSize - 0.34F;
				}
			}
		} else {
			return -0.1F;
		}
	}
}
