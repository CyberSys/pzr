package org.joml;


public class GeometryUtils {

	public static void perpendicular(float float1, float float2, float float3, Vector3f vector3f, Vector3f vector3f2) {
		float float4 = float3 * float3 + float2 * float2;
		float float5 = float3 * float3 + float1 * float1;
		float float6 = float2 * float2 + float1 * float1;
		float float7;
		if (float4 > float5 && float4 > float6) {
			vector3f.x = 0.0F;
			vector3f.y = float3;
			vector3f.z = -float2;
			float7 = float4;
		} else if (float5 > float6) {
			vector3f.x = float3;
			vector3f.y = 0.0F;
			vector3f.z = float1;
			float7 = float5;
		} else {
			vector3f.x = float2;
			vector3f.y = -float1;
			vector3f.z = 0.0F;
			float7 = float6;
		}

		float float8 = 1.0F / (float)Math.sqrt((double)float7);
		vector3f.x *= float8;
		vector3f.y *= float8;
		vector3f.z *= float8;
		vector3f2.x = float2 * vector3f.z - float3 * vector3f.y;
		vector3f2.y = float3 * vector3f.x - float1 * vector3f.z;
		vector3f2.z = float1 * vector3f.y - float2 * vector3f.x;
	}

	public static void perpendicular(Vector3fc vector3fc, Vector3f vector3f, Vector3f vector3f2) {
		perpendicular(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f, vector3f2);
	}

	public static void normal(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3f vector3f) {
		normal(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3f);
	}

	public static void normal(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, Vector3f vector3f) {
		vector3f.x = (float5 - float2) * (float9 - float3) - (float6 - float3) * (float8 - float2);
		vector3f.y = (float6 - float3) * (float7 - float1) - (float4 - float1) * (float9 - float3);
		vector3f.z = (float4 - float1) * (float8 - float2) - (float5 - float2) * (float7 - float1);
		vector3f.normalize();
	}

	public static void tangent(Vector3fc vector3fc, Vector2fc vector2fc, Vector3fc vector3fc2, Vector2fc vector2fc2, Vector3fc vector3fc3, Vector2fc vector2fc3, Vector3f vector3f) {
		float float1 = vector2fc2.y() - vector2fc.y();
		float float2 = vector2fc3.y() - vector2fc.y();
		float float3 = 1.0F / ((vector2fc2.x() - vector2fc.x()) * float2 - (vector2fc3.x() - vector2fc.x()) * float1);
		vector3f.x = float3 * (float2 * (vector3fc2.x() - vector3fc.x()) - float1 * (vector3fc3.x() - vector3fc.x()));
		vector3f.y = float3 * (float2 * (vector3fc2.y() - vector3fc.y()) - float1 * (vector3fc3.y() - vector3fc.y()));
		vector3f.z = float3 * (float2 * (vector3fc2.z() - vector3fc.z()) - float1 * (vector3fc3.z() - vector3fc.z()));
		vector3f.normalize();
	}

	public static void bitangent(Vector3fc vector3fc, Vector2fc vector2fc, Vector3fc vector3fc2, Vector2fc vector2fc2, Vector3fc vector3fc3, Vector2fc vector2fc3, Vector3f vector3f) {
		float float1 = vector2fc2.x() - vector2fc.x();
		float float2 = vector2fc3.x() - vector2fc.x();
		float float3 = 1.0F / (float1 * (vector2fc3.y() - vector2fc.y()) - float2 * (vector2fc2.y() - vector2fc.y()));
		vector3f.x = float3 * (-float2 * (vector3fc2.x() - vector3fc.x()) - float1 * (vector3fc3.x() - vector3fc.x()));
		vector3f.y = float3 * (-float2 * (vector3fc2.y() - vector3fc.y()) - float1 * (vector3fc3.y() - vector3fc.y()));
		vector3f.z = float3 * (-float2 * (vector3fc2.z() - vector3fc.z()) - float1 * (vector3fc3.z() - vector3fc.z()));
		vector3f.normalize();
	}

	public static void tangentBitangent(Vector3fc vector3fc, Vector2fc vector2fc, Vector3fc vector3fc2, Vector2fc vector2fc2, Vector3fc vector3fc3, Vector2fc vector2fc3, Vector3f vector3f, Vector3f vector3f2) {
		float float1 = vector2fc2.y() - vector2fc.y();
		float float2 = vector2fc3.y() - vector2fc.y();
		float float3 = vector2fc2.x() - vector2fc.x();
		float float4 = vector2fc3.x() - vector2fc.x();
		float float5 = 1.0F / (float3 * float2 - float4 * float1);
		vector3f.x = float5 * (float2 * (vector3fc2.x() - vector3fc.x()) - float1 * (vector3fc3.x() - vector3fc.x()));
		vector3f.y = float5 * (float2 * (vector3fc2.y() - vector3fc.y()) - float1 * (vector3fc3.y() - vector3fc.y()));
		vector3f.z = float5 * (float2 * (vector3fc2.z() - vector3fc.z()) - float1 * (vector3fc3.z() - vector3fc.z()));
		vector3f.normalize();
		vector3f2.x = float5 * (-float4 * (vector3fc2.x() - vector3fc.x()) - float3 * (vector3fc3.x() - vector3fc.x()));
		vector3f2.y = float5 * (-float4 * (vector3fc2.y() - vector3fc.y()) - float3 * (vector3fc3.y() - vector3fc.y()));
		vector3f2.z = float5 * (-float4 * (vector3fc2.z() - vector3fc.z()) - float3 * (vector3fc3.z() - vector3fc.z()));
		vector3f2.normalize();
	}
}
