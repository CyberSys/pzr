package zombie.vehicles;

import org.joml.Vector2f;


public final class PolyPolyIntersect {
	private static Vector2f tempVector2f_1 = new Vector2f();
	private static Vector2f tempVector2f_2 = new Vector2f();
	private static Vector2f tempVector2f_3 = new Vector2f();

	public static boolean intersects(PolygonalMap2.VehiclePoly vehiclePoly, PolygonalMap2.VehiclePoly vehiclePoly2) {
		for (int int1 = 0; int1 < 2; ++int1) {
			PolygonalMap2.VehiclePoly vehiclePoly3 = int1 == 0 ? vehiclePoly : vehiclePoly2;
			for (int int2 = 0; int2 < 4; ++int2) {
				int int3 = (int2 + 1) % 4;
				Vector2f vector2f = getPoint(vehiclePoly3, int2, tempVector2f_1);
				Vector2f vector2f2 = getPoint(vehiclePoly3, int3, tempVector2f_2);
				Vector2f vector2f3 = tempVector2f_3.set(vector2f2.y - vector2f.y, vector2f.x - vector2f2.x);
				double double1 = Double.MAX_VALUE;
				double double2 = Double.NEGATIVE_INFINITY;
				double double3;
				for (int int4 = 0; int4 < 4; ++int4) {
					Vector2f vector2f4 = getPoint(vehiclePoly, int4, tempVector2f_1);
					double3 = (double)(vector2f3.x * vector2f4.x + vector2f3.y * vector2f4.y);
					if (double3 < double1) {
						double1 = double3;
					}

					if (double3 > double2) {
						double2 = double3;
					}
				}

				double double4 = Double.MAX_VALUE;
				double3 = Double.NEGATIVE_INFINITY;
				for (int int5 = 0; int5 < 4; ++int5) {
					Vector2f vector2f5 = getPoint(vehiclePoly2, int5, tempVector2f_1);
					double double5 = (double)(vector2f3.x * vector2f5.x + vector2f3.y * vector2f5.y);
					if (double5 < double4) {
						double4 = double5;
					}

					if (double5 > double3) {
						double3 = double5;
					}
				}

				if (double2 < double4 || double3 < double1) {
					return false;
				}
			}
		}

		return true;
	}

	private static Vector2f getPoint(PolygonalMap2.VehiclePoly vehiclePoly, int int1, Vector2f vector2f) {
		if (int1 == 0) {
			return vector2f.set(vehiclePoly.x1, vehiclePoly.y1);
		} else if (int1 == 1) {
			return vector2f.set(vehiclePoly.x2, vehiclePoly.y2);
		} else if (int1 == 2) {
			return vector2f.set(vehiclePoly.x3, vehiclePoly.y3);
		} else {
			return int1 == 3 ? vector2f.set(vehiclePoly.x4, vehiclePoly.y4) : null;
		}
	}
}
