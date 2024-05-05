package zombie.iso;

import java.util.ArrayList;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;


public final class IsoWaterFlow {
	private static final ArrayList points = new ArrayList();
	private static final ArrayList zones = new ArrayList();

	public static void addFlow(float float1, float float2, float float3, float float4) {
		int int1 = (360 - (int)float3 - 45) % 360;
		if (int1 < 0) {
			int1 += 360;
		}

		float3 = (float)Math.toRadians((double)int1);
		points.add(new Vector4f(float1, float2, float3, float4));
	}

	public static void addZone(float float1, float float2, float float3, float float4, float float5, float float6) {
		if (float1 > float3 || float2 > float4 || (double)float5 > 1.0) {
			DebugLog.log("ERROR IsoWaterFlow: Invalid waterzone (" + float1 + ", " + float2 + ", " + float3 + ", " + float4 + ")");
		}

		zones.add(new Matrix3f(float1, float2, float3, float4, float5, float6, 0.0F, 0.0F, 0.0F));
	}

	public static int getShore(int int1, int int2) {
		for (int int3 = 0; int3 < zones.size(); ++int3) {
			Matrix3f matrix3f = (Matrix3f)zones.get(int3);
			if (matrix3f.m00 <= (float)int1 && matrix3f.m02 >= (float)int1 && matrix3f.m01 <= (float)int2 && matrix3f.m10 >= (float)int2) {
				return (int)matrix3f.m11;
			}
		}

		return 1;
	}

	public static Vector2f getFlow(IsoGridSquare square, int int1, int int2, Vector2f vector2f) {
		float float1 = 0.0F;
		float float2 = 0.0F;
		Vector4f vector4f = null;
		float float3 = Float.MAX_VALUE;
		Vector4f vector4f2 = null;
		float float4 = Float.MAX_VALUE;
		Vector4f vector4f3 = null;
		float float5 = Float.MAX_VALUE;
		if (points.size() == 0) {
			return vector2f.set(0.0F, 0.0F);
		} else {
			int int3;
			Vector4f vector4f4;
			double double1;
			for (int3 = 0; int3 < points.size(); ++int3) {
				vector4f4 = (Vector4f)points.get(int3);
				double1 = Math.pow((double)(vector4f4.x - (float)(square.x + int1)), 2.0) + Math.pow((double)(vector4f4.y - (float)(square.y + int2)), 2.0);
				if (double1 < (double)float3) {
					float3 = (float)double1;
					vector4f = vector4f4;
				}
			}

			for (int3 = 0; int3 < points.size(); ++int3) {
				vector4f4 = (Vector4f)points.get(int3);
				double1 = Math.pow((double)(vector4f4.x - (float)(square.x + int1)), 2.0) + Math.pow((double)(vector4f4.y - (float)(square.y + int2)), 2.0);
				if (double1 < (double)float4 && vector4f4 != vector4f) {
					float4 = (float)double1;
					vector4f2 = vector4f4;
				}
			}

			float3 = Math.max((float)Math.sqrt((double)float3), 0.1F);
			float4 = Math.max((float)Math.sqrt((double)float4), 0.1F);
			float float6;
			if (float3 > float4 * 10.0F) {
				float1 = vector4f.z;
				float2 = vector4f.w;
			} else {
				for (int3 = 0; int3 < points.size(); ++int3) {
					vector4f4 = (Vector4f)points.get(int3);
					double1 = Math.pow((double)(vector4f4.x - (float)(square.x + int1)), 2.0) + Math.pow((double)(vector4f4.y - (float)(square.y + int2)), 2.0);
					if (double1 < (double)float5 && vector4f4 != vector4f && vector4f4 != vector4f2) {
						float5 = (float)double1;
						vector4f3 = vector4f4;
					}
				}

				float5 = Math.max((float)Math.sqrt((double)float5), 0.1F);
				float6 = vector4f2.z * (1.0F - float4 / (float4 + float5)) + vector4f3.z * (1.0F - float5 / (float4 + float5));
				float float7 = vector4f2.w * (1.0F - float4 / (float4 + float5)) + vector4f3.w * (1.0F - float5 / (float4 + float5));
				float float8 = float4 * (1.0F - float4 / (float4 + float5)) + float5 * (1.0F - float5 / (float4 + float5));
				float1 = vector4f.z * (1.0F - float3 / (float3 + float8)) + float6 * (1.0F - float8 / (float3 + float8));
				float2 = vector4f.w * (1.0F - float3 / (float3 + float8)) + float7 * (1.0F - float8 / (float3 + float8));
			}

			float6 = 1.0F;
			IsoCell cell = square.getCell();
			for (int int4 = -5; int4 < 5; ++int4) {
				for (int int5 = -5; int5 < 5; ++int5) {
					IsoGridSquare square2 = cell.getGridSquare(square.x + int1 + int4, square.y + int2 + int5, 0);
					if (square2 == null || !square2.getProperties().Is(IsoFlagType.water)) {
						float6 = (float)Math.min((double)float6, Math.max(0.0, Math.sqrt((double)(int4 * int4 + int5 * int5))) / 4.0);
					}
				}
			}

			float2 *= float6;
			return vector2f.set(float1, float2);
		}
	}

	public static void Reset() {
		points.clear();
		zones.clear();
	}
}
