package org.joml;


public class Interpolationd {

	public static double interpolateTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11) {
		double double12 = double5 - double8;
		double double13 = double7 - double4;
		double double14 = double1 - double7;
		double double15 = double11 - double8;
		double double16 = double10 - double7;
		double double17 = double2 - double8;
		double double18 = 1.0 / (double12 * double14 + double13 * double17);
		double double19 = (double12 * double16 + double13 * double15) * double18;
		double double20 = (double14 * double15 - double17 * double16) * double18;
		return double19 * double3 + double20 * double6 + (1.0 - double19 - double20) * double9;
	}

	public static Vector2d interpolateTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, Vector2d vector2d) {
		double double15 = double6 - double10;
		double double16 = double9 - double5;
		double double17 = double1 - double9;
		double double18 = double14 - double10;
		double double19 = double13 - double9;
		double double20 = double2 - double10;
		double double21 = 1.0 / (double15 * double17 + double16 * double20);
		double double22 = (double15 * double19 + double16 * double18) * double21;
		double double23 = (double17 * double18 - double20 * double19) * double21;
		double double24 = 1.0 - double22 - double23;
		vector2d.x = double22 * double3 + double23 * double7 + double24 * double11;
		vector2d.y = double22 * double4 + double23 * double8 + double24 * double12;
		return vector2d;
	}

	public static Vector2d dFdxLinear(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, Vector2d vector2d) {
		double double13 = double6 - double10;
		double double14 = double2 - double10;
		double double15 = double13 * (double1 - double9) + (double9 - double5) * double14;
		double double16 = double15 - double13 + double14;
		double double17 = 1.0 / double15;
		vector2d.x = double17 * (double13 * double3 - double14 * double7 + double16 * double11) - double11;
		vector2d.y = double17 * (double13 * double4 - double14 * double8 + double16 * double12) - double12;
		return vector2d;
	}

	public static Vector2d dFdyLinear(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, Vector2d vector2d) {
		double double13 = double9 - double5;
		double double14 = double1 - double9;
		double double15 = (double6 - double10) * double14 + double13 * (double2 - double10);
		double double16 = double15 - double13 - double14;
		double double17 = 1.0 / double15;
		vector2d.x = double17 * (double13 * double3 + double14 * double7 + double16 * double11) - double11;
		vector2d.y = double17 * (double13 * double4 + double14 * double8 + double16 * double12) - double12;
		return vector2d;
	}

	public static Vector3d interpolateTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, double double17, Vector3d vector3d) {
		interpolationFactorsTriangle(double1, double2, double6, double7, double11, double12, double16, double17, vector3d);
		return vector3d.set(vector3d.x * double3 + vector3d.y * double8 + vector3d.z * double13, vector3d.x * double4 + vector3d.y * double9 + vector3d.z * double14, vector3d.x * double5 + vector3d.y * double10 + vector3d.z * double15);
	}

	public static Vector3d interpolationFactorsTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector3d vector3d) {
		double double9 = double4 - double6;
		double double10 = double5 - double3;
		double double11 = double1 - double5;
		double double12 = double8 - double6;
		double double13 = double7 - double5;
		double double14 = double2 - double6;
		double double15 = 1.0 / (double9 * double11 + double10 * double14);
		vector3d.x = (double9 * double13 + double10 * double12) * double15;
		vector3d.y = (double11 * double12 - double14 * double13) * double15;
		vector3d.z = 1.0 - vector3d.x - vector3d.y;
		return vector3d;
	}
}
