package zombie.util;

import zombie.iso.Vector3;


public class RaySphereIntersectCheck {
	static Vector3 toSphere = new Vector3();
	static Vector3 dirNormal = new Vector3();

	public static boolean intersects(Vector3 vector3, Vector3 vector32, Vector3 vector33, float float1) {
		float1 *= float1;
		dirNormal.x = vector32.x;
		dirNormal.y = vector32.y;
		dirNormal.z = vector32.z;
		dirNormal.normalize();
		toSphere.x = vector33.x - vector3.x;
		toSphere.y = vector33.y - vector3.y;
		toSphere.z = vector33.z - vector3.z;
		float float2 = toSphere.getLength();
		float2 *= float2;
		if (float2 < float1) {
			return false;
		} else {
			float float3 = toSphere.dot3d(dirNormal);
			if (float3 < 0.0F) {
				return false;
			} else {
				float float4 = float1 + float3 * float3 - toSphere.getLength();
				return (double)float4 >= 0.0;
			}
		}
	}
}
