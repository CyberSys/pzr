package org.joml;


public class Intersectiond {
	public static final int POINT_ON_TRIANGLE_VERTEX = 0;
	public static final int POINT_ON_TRIANGLE_EDGE = 1;
	public static final int POINT_ON_TRIANGLE_FACE = 2;
	public static final int AAR_SIDE_MINX = 0;
	public static final int AAR_SIDE_MINY = 1;
	public static final int AAR_SIDE_MAXX = 2;
	public static final int AAR_SIDE_MAXY = 3;
	public static final int OUTSIDE = -1;
	public static final int ONE_INTERSECTION = 1;
	public static final int TWO_INTERSECTION = 2;
	public static final int INSIDE = 3;

	public static boolean testPlaneSphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double10 = (double1 * double5 + double2 * double6 + double3 * double7 + double4) / double9;
		return -double8 <= double10 && double10 <= double8;
	}

	public static boolean intersectPlaneSphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector4d vector4d) {
		double double9 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2 + double3 * double3);
		double double10 = (double1 * double5 + double2 * double6 + double3 * double7 + double4) * double9;
		if (-double8 <= double10 && double10 <= double8) {
			vector4d.x = double5 + double10 * double1 * double9;
			vector4d.y = double6 + double10 * double2 * double9;
			vector4d.z = double7 + double10 * double3 * double9;
			vector4d.w = Math.sqrt(double8 * double8 - double10 * double10);
			return true;
		} else {
			return false;
		}
	}

	public static boolean testAabPlane(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
		double double11;
		double double12;
		if (double7 > 0.0) {
			double11 = double4;
			double12 = double1;
		} else {
			double11 = double1;
			double12 = double4;
		}

		double double13;
		double double14;
		if (double8 > 0.0) {
			double13 = double5;
			double14 = double2;
		} else {
			double13 = double2;
			double14 = double5;
		}

		double double15;
		double double16;
		if (double9 > 0.0) {
			double15 = double6;
			double16 = double3;
		} else {
			double15 = double3;
			double16 = double6;
		}

		double double17 = double10 + double7 * double12 + double8 * double14 + double9 * double16;
		double double18 = double10 + double7 * double11 + double8 * double13 + double9 * double15;
		return double17 <= 0.0 && double18 >= 0.0;
	}

	public static boolean testAabPlane(Vector3dc vector3dc, Vector3dc vector3dc2, double double1, double double2, double double3, double double4) {
		return testAabPlane(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), double1, double2, double3, double4);
	}

	public static boolean testAabAab(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		return double4 >= double7 && double5 >= double8 && double6 >= double9 && double1 <= double10 && double2 <= double11 && double3 <= double12;
	}

	public static boolean testAabAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4) {
		return testAabAab(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z());
	}

	public static boolean intersectSphereSphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector4d vector4d) {
		double double9 = double5 - double1;
		double double10 = double6 - double2;
		double double11 = double7 - double3;
		double double12 = double9 * double9 + double10 * double10 + double11 * double11;
		double double13 = 0.5 + (double4 - double8) / double12;
		double double14 = double4 - double13 * double13 * double12;
		if (double14 >= 0.0) {
			vector4d.x = double1 + double13 * double9;
			vector4d.y = double2 + double13 * double10;
			vector4d.z = double3 + double13 * double11;
			vector4d.w = Math.sqrt(double14);
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectSphereSphere(Vector3dc vector3dc, double double1, Vector3dc vector3dc2, double double2, Vector4d vector4d) {
		return intersectSphereSphere(vector3dc.x(), vector3dc.y(), vector3dc.z(), double1, vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), double2, vector4d);
	}

	public static boolean testSphereSphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = double5 - double1;
		double double10 = double6 - double2;
		double double11 = double7 - double3;
		double double12 = double9 * double9 + double10 * double10 + double11 * double11;
		double double13 = 0.5 + (double4 - double8) / double12;
		double double14 = double4 - double13 * double13 * double12;
		return double14 >= 0.0;
	}

	public static boolean testSphereSphere(Vector3dc vector3dc, double double1, Vector3dc vector3dc2, double double2) {
		return testSphereSphere(vector3dc.x(), vector3dc.y(), vector3dc.z(), double1, vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), double2);
	}

	public static double distancePointPlane(double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
		double double8 = Math.sqrt(double4 * double4 + double5 * double5 + double6 * double6);
		return (double4 * double1 + double5 * double2 + double6 * double3 + double7) / double8;
	}

	public static double distancePointPlane(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		double double13 = double8 - double5;
		double double14 = double12 - double6;
		double double15 = double11 - double5;
		double double16 = double9 - double6;
		double double17 = double10 - double4;
		double double18 = double7 - double4;
		double double19 = double13 * double14 - double15 * double16;
		double double20 = double16 * double17 - double14 * double18;
		double double21 = double18 * double15 - double17 * double13;
		double double22 = -(double19 * double4 + double20 * double5 + double21 * double6);
		return distancePointPlane(double1, double2, double3, double19, double20, double21, double22);
	}

	public static double intersectRayPlane(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13) {
		double double14 = double10 * double4 + double11 * double5 + double12 * double6;
		if (double14 < double13) {
			double double15 = ((double7 - double1) * double10 + (double8 - double2) * double11 + (double9 - double3) * double12) / double14;
			if (double15 >= 0.0) {
				return double15;
			}
		}

		return -1.0;
	}

	public static double intersectRayPlane(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, double double1) {
		return intersectRayPlane(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), double1);
	}

	public static double intersectRayPlane(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11) {
		double double12 = double7 * double4 + double8 * double5 + double9 * double6;
		if (double12 < 0.0) {
			double double13 = -(double7 * double1 + double8 * double2 + double9 * double3 + double10) / double12;
			if (double13 >= 0.0) {
				return double13;
			}
		}

		return -1.0;
	}

	public static boolean testAabSphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
		double double11 = double10;
		double double12;
		if (double7 < double1) {
			double12 = double7 - double1;
			double11 = double10 - double12 * double12;
		} else if (double7 > double4) {
			double12 = double7 - double4;
			double11 = double10 - double12 * double12;
		}

		if (double8 < double2) {
			double12 = double8 - double2;
			double11 -= double12 * double12;
		} else if (double8 > double5) {
			double12 = double8 - double5;
			double11 -= double12 * double12;
		}

		if (double9 < double3) {
			double12 = double9 - double3;
			double11 -= double12 * double12;
		} else if (double9 > double6) {
			double12 = double9 - double6;
			double11 -= double12 * double12;
		}

		return double11 >= 0.0;
	}

	public static boolean testAabSphere(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1) {
		return testAabSphere(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), double1);
	}

	public static int findClosestPointOnTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, Vector3d vector3d) {
		double double13 = double1 - double10;
		double double14 = double2 - double11;
		double double15 = double3 - double12;
		double double16 = double4 - double10;
		double double17 = double5 - double11;
		double double18 = double6 - double12;
		double double19 = double7 - double10;
		double double20 = double8 - double11;
		double double21 = double9 - double12;
		double double22 = double16 - double13;
		double double23 = double17 - double14;
		double double24 = double18 - double15;
		double double25 = double19 - double13;
		double double26 = double20 - double14;
		double double27 = double21 - double15;
		double double28 = -(double22 * double13 + double23 * double14 + double24 * double15);
		double double29 = -(double25 * double13 + double26 * double14 + double27 * double15);
		if (double28 <= 0.0 && double29 <= 0.0) {
			vector3d.set(double1, double2, double3);
			return 0;
		} else {
			double double30 = -(double22 * double16 + double23 * double17 + double24 * double18);
			double double31 = -(double25 * double16 + double26 * double17 + double27 * double18);
			if (double30 >= 0.0 && double31 <= double30) {
				vector3d.set(double4, double5, double6);
				return 0;
			} else {
				double double32 = double28 * double31 - double30 * double29;
				double double33;
				if (double32 <= 0.0 && double28 >= 0.0 && double30 <= 0.0) {
					double33 = double28 / (double28 - double30);
					vector3d.set(double1 + double22 * double33, double2 + double23 * double33, double3 * double24 * double33);
					return 1;
				} else {
					double33 = -(double22 * double19 + double23 * double20 + double24 * double21);
					double double34 = -(double25 * double19 + double26 * double20 + double27 * double21);
					if (double34 >= 0.0 && double33 <= double34) {
						vector3d.set(double7, double8, double9);
						return 0;
					} else {
						double double35 = double33 * double29 - double28 * double34;
						double double36;
						if (double35 <= 0.0 && double29 >= 0.0 && double34 <= 0.0) {
							double36 = double29 / (double29 - double34);
							vector3d.set(double1 + double25 * double36, double2 + double26 * double36, double3 + double27 * double36);
							return 1;
						} else {
							double36 = double30 * double34 - double33 * double31;
							double double37;
							if (double36 <= 0.0 && double31 - double30 >= 0.0 && double33 - double34 >= 0.0) {
								double37 = (double31 - double30) / (double31 - double30 + double33 - double34);
								vector3d.set(double4 + (double19 - double16) * double37, double5 + (double20 - double17) * double37, double6 + (double21 - double18) * double37);
								return 1;
							} else {
								double37 = 1.0 / (double36 + double35 + double32);
								double double38 = double35 * double37;
								double double39 = double32 * double37;
								vector3d.set(double1 + double22 * double38 + double25 * double39, double2 + double23 * double38 + double26 * double39, double3 + double24 * double38 + double27 * double39);
								return 2;
							}
						}
					}
				}
			}
		}
	}

	public static int findClosestPointOnTriangle(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3d vector3d) {
		return findClosestPointOnTriangle(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3d);
	}

	public static int intersectSweptSphereTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, double double17, double double18, Vector4d vector4d) {
		double double19 = double11 - double8;
		double double20 = double12 - double9;
		double double21 = double13 - double10;
		double double22 = double14 - double8;
		double double23 = double15 - double9;
		double double24 = double16 - double10;
		double double25 = double20 * double24 - double23 * double21;
		double double26 = double21 * double22 - double24 * double19;
		double double27 = double19 * double23 - double22 * double20;
		double double28 = -(double25 * double8 + double26 * double9 + double27 * double10);
		double double29 = 1.0 / Math.sqrt(double25 * double25 + double26 * double26 + double27 * double27);
		double double30 = (double25 * double1 + double26 * double2 + double27 * double3 + double28) * double29;
		double double31 = (double25 * double5 + double26 * double6 + double27 * double7) * double29;
		if (double31 < double17 && double31 > -double17) {
			return -1;
		} else {
			double double32 = (double4 - double30) / double31;
			if (double32 > double18) {
				return -1;
			} else {
				double double33 = (-double4 - double30) / double31;
				double double34 = double1 - double4 * double25 * double29 + double5 * double32;
				double double35 = double2 - double4 * double26 * double29 + double6 * double32;
				double double36 = double3 - double4 * double27 * double29 + double7 * double32;
				boolean boolean1 = testPointInTriangle(double34, double35, double36, double8, double9, double10, double11, double12, double13, double14, double15, double16);
				if (boolean1) {
					vector4d.x = double34;
					vector4d.y = double35;
					vector4d.z = double36;
					vector4d.w = double32;
					return 2;
				} else {
					byte byte1 = -1;
					double double37 = double18;
					double double38 = double5 * double5 + double6 * double6 + double7 * double7;
					double double39 = double4 * double4;
					double double40 = double1 - double8;
					double double41 = double2 - double9;
					double double42 = double3 - double10;
					double double43 = 2.0 * (double5 * double40 + double6 * double41 + double7 * double42);
					double double44 = double40 * double40 + double41 * double41 + double42 * double42 - double39;
					double double45 = computeLowestRoot(double38, double43, double44, double18);
					if (double45 < double18) {
						vector4d.x = double8;
						vector4d.y = double9;
						vector4d.z = double10;
						vector4d.w = double45;
						double37 = double45;
						byte1 = 0;
					}

					double double46 = double1 - double11;
					double double47 = double2 - double12;
					double double48 = double3 - double13;
					double double49 = double46 * double46 + double47 * double47 + double48 * double48;
					double double50 = 2.0 * (double5 * double46 + double6 * double47 + double7 * double48);
					double double51 = double49 - double39;
					double double52 = computeLowestRoot(double38, double50, double51, double37);
					if (double52 < double37) {
						vector4d.x = double11;
						vector4d.y = double12;
						vector4d.z = double13;
						vector4d.w = double52;
						double37 = double52;
						byte1 = 0;
					}

					double double53 = double1 - double14;
					double double54 = double2 - double15;
					double double55 = double3 - double16;
					double double56 = 2.0 * (double5 * double53 + double6 * double54 + double7 * double55);
					double double57 = double53 * double53 + double54 * double54 + double55 * double55 - double39;
					double double58 = computeLowestRoot(double38, double56, double57, double37);
					if (double58 < double37) {
						vector4d.x = double14;
						vector4d.y = double15;
						vector4d.z = double16;
						vector4d.w = double58;
						double37 = double58;
						byte1 = 0;
					}

					double double59 = double5 * double5 + double6 * double6 + double7 * double7;
					double double60 = double19 * double19 + double20 * double20 + double21 * double21;
					double double61 = double40 * double40 + double41 * double41 + double42 * double42;
					double double62 = double19 * double5 + double20 * double6 + double21 * double7;
					double double63 = double60 * -double59 + double62 * double62;
					double double64 = double19 * -double40 + double20 * -double41 + double21 * -double42;
					double double65 = double5 * -double40 + double6 * -double41 + double7 * -double42;
					double double66 = double60 * 2.0 * double65 - 2.0 * double62 * double64;
					double double67 = double60 * (double39 - double61) + double64 * double64;
					double double68 = computeLowestRoot(double63, double66, double67, double37);
					double double69 = (double62 * double68 - double64) / double60;
					if (double69 >= 0.0 && double69 <= 1.0 && double68 < double37) {
						vector4d.x = double8 + double69 * double19;
						vector4d.y = double9 + double69 * double20;
						vector4d.z = double10 + double69 * double21;
						vector4d.w = double68;
						double37 = double68;
						byte1 = 1;
					}

					double double70 = double22 * double22 + double23 * double23 + double24 * double24;
					double double71 = double22 * double5 + double23 * double6 + double24 * double7;
					double double72 = double70 * -double59 + double71 * double71;
					double double73 = double22 * -double40 + double23 * -double41 + double24 * -double42;
					double double74 = double70 * 2.0 * double65 - 2.0 * double71 * double73;
					double double75 = double70 * (double39 - double61) + double73 * double73;
					double double76 = computeLowestRoot(double72, double74, double75, double37);
					double double77 = (double71 * double76 - double73) / double70;
					if (double77 >= 0.0 && double77 <= 1.0 && double76 < double33) {
						vector4d.x = double8 + double77 * double22;
						vector4d.y = double9 + double77 * double23;
						vector4d.z = double10 + double77 * double24;
						vector4d.w = double76;
						double37 = double76;
						byte1 = 1;
					}

					double double78 = double14 - double11;
					double double79 = double15 - double12;
					double double80 = double16 - double13;
					double double81 = double78 * double78 + double79 * double79 + double80 * double80;
					double double82 = double78 * double5 + double79 * double6 + double80 * double7;
					double double83 = double81 * -double59 + double82 * double82;
					double double84 = double78 * -double46 + double79 * -double47 + double80 * -double48;
					double double85 = double5 * -double46 + double6 * -double47 + double7 * -double48;
					double double86 = double81 * 2.0 * double85 - 2.0 * double82 * double84;
					double double87 = double81 * (double39 - double49) + double84 * double84;
					double double88 = computeLowestRoot(double83, double86, double87, double37);
					double double89 = (double82 * double88 - double84) / double81;
					if (double89 >= 0.0 && double89 <= 1.0 && double88 < double37) {
						vector4d.x = double11 + double89 * double78;
						vector4d.y = double12 + double89 * double79;
						vector4d.z = double13 + double89 * double80;
						vector4d.w = double88;
						byte1 = 1;
					}

					return byte1;
				}
			}
		}
	}

	private static double computeLowestRoot(double double1, double double2, double double3, double double4) {
		double double5 = double2 * double2 - 4.0 * double1 * double3;
		if (double5 < 0.0) {
			return Double.MAX_VALUE;
		} else {
			double double6 = Math.sqrt(double5);
			double double7 = (-double2 - double6) / (2.0 * double1);
			double double8 = (-double2 + double6) / (2.0 * double1);
			if (double7 > double8) {
				double double9 = double8;
				double8 = double7;
				double7 = double9;
			}

			if (double7 > 0.0 && double7 < double4) {
				return double7;
			} else {
				return double8 > 0.0 && double8 < double4 ? double8 : Double.MAX_VALUE;
			}
		}
	}

	public static boolean testPointInTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		double double13 = double7 - double4;
		double double14 = double8 - double5;
		double double15 = double9 - double6;
		double double16 = double10 - double4;
		double double17 = double11 - double5;
		double double18 = double12 - double6;
		double double19 = double13 * double13 + double14 * double14 + double15 * double15;
		double double20 = double13 * double16 + double14 * double17 + double15 * double18;
		double double21 = double16 * double16 + double17 * double17 + double18 * double18;
		double double22 = double19 * double21 - double20 * double20;
		double double23 = double1 - double4;
		double double24 = double2 - double5;
		double double25 = double3 - double6;
		double double26 = double23 * double13 + double24 * double14 + double25 * double15;
		double double27 = double23 * double16 + double24 * double17 + double25 * double18;
		double double28 = double26 * double21 - double27 * double20;
		double double29 = double27 * double19 - double26 * double20;
		double double30 = double28 + double29 - double22;
		return (Double.doubleToRawLongBits(double30) & ~(Double.doubleToRawLongBits(double28) | Double.doubleToRawLongBits(double29)) & Long.MIN_VALUE) != 0L;
	}

	public static boolean intersectRaySphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, Vector2d vector2d) {
		double double11 = double7 - double1;
		double double12 = double8 - double2;
		double double13 = double9 - double3;
		double double14 = double11 * double4 + double12 * double5 + double13 * double6;
		double double15 = double11 * double11 + double12 * double12 + double13 * double13 - double14 * double14;
		if (double15 > double10) {
			return false;
		} else {
			double double16 = Math.sqrt(double10 - double15);
			double double17 = double14 - double16;
			double double18 = double14 + double16;
			if (double17 < double18 && double18 >= 0.0) {
				vector2d.x = double17;
				vector2d.y = double18;
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean intersectRaySphere(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1, Vector2d vector2d) {
		return intersectRaySphere(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), double1, vector2d);
	}

	public static boolean testRaySphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
		double double11 = double7 - double1;
		double double12 = double8 - double2;
		double double13 = double9 - double3;
		double double14 = double11 * double4 + double12 * double5 + double13 * double6;
		double double15 = double11 * double11 + double12 * double12 + double13 * double13 - double14 * double14;
		if (double15 > double10) {
			return false;
		} else {
			double double16 = Math.sqrt(double10 - double15);
			double double17 = double14 - double16;
			double double18 = double14 + double16;
			return double17 < double18 && double18 >= 0.0;
		}
	}

	public static boolean testRaySphere(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1) {
		return testRaySphere(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), double1);
	}

	public static boolean testLineSegmentSphere(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10) {
		double double11 = double4 - double1;
		double double12 = double5 - double2;
		double double13 = double6 - double3;
		double double14 = (double7 - double1) * double11 + (double8 - double2) * double12 + (double9 - double3) * double13;
		double double15 = double11 * double11 + double12 * double12 + double13 * double13;
		double double16 = double14 / double15;
		double double17;
		if (double16 < 0.0) {
			double11 = double1 - double7;
			double12 = double2 - double8;
			double13 = double3 - double9;
		} else if (double16 > 1.0) {
			double11 = double4 - double7;
			double12 = double5 - double8;
			double13 = double6 - double9;
		} else {
			double17 = double1 + double16 * double11;
			double double18 = double2 + double16 * double12;
			double double19 = double3 + double16 * double13;
			double11 = double17 - double7;
			double12 = double18 - double8;
			double13 = double19 - double9;
		}

		double17 = double11 * double11 + double12 * double12 + double13 * double13;
		return double17 <= double10;
	}

	public static boolean testLineSegmentSphere(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, double double1) {
		return testLineSegmentSphere(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), double1);
	}

	public static boolean intersectRayAab(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, Vector2d vector2d) {
		double double13 = 1.0 / double4;
		double double14 = 1.0 / double5;
		double double15 = 1.0 / double6;
		double double16;
		double double17;
		if (double13 >= 0.0) {
			double17 = (double7 - double1) * double13;
			double16 = (double10 - double1) * double13;
		} else {
			double17 = (double10 - double1) * double13;
			double16 = (double7 - double1) * double13;
		}

		double double18;
		double double19;
		if (double14 >= 0.0) {
			double18 = (double8 - double2) * double14;
			double19 = (double11 - double2) * double14;
		} else {
			double18 = (double11 - double2) * double14;
			double19 = (double8 - double2) * double14;
		}

		if (!(double17 > double19) && !(double18 > double16)) {
			double double20;
			double double21;
			if (double15 >= 0.0) {
				double20 = (double9 - double3) * double15;
				double21 = (double12 - double3) * double15;
			} else {
				double20 = (double12 - double3) * double15;
				double21 = (double9 - double3) * double15;
			}

			if (!(double17 > double21) && !(double20 > double16)) {
				double17 = !(double18 > double17) && !Double.isNaN(double17) ? double17 : double18;
				double16 = !(double19 < double16) && !Double.isNaN(double16) ? double16 : double19;
				double17 = double20 > double17 ? double20 : double17;
				double16 = double21 < double16 ? double21 : double16;
				if (double17 < double16 && double16 >= 0.0) {
					vector2d.x = double17;
					vector2d.y = double16;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean intersectRayAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector2d vector2d) {
		return intersectRayAab(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector2d);
	}

	public static int intersectLineSegmentAab(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, Vector2d vector2d) {
		double double13 = double4 - double1;
		double double14 = double5 - double2;
		double double15 = double6 - double3;
		double double16 = 1.0 / double13;
		double double17 = 1.0 / double14;
		double double18 = 1.0 / double15;
		double double19;
		double double20;
		if (double16 >= 0.0) {
			double19 = (double7 - double1) * double16;
			double20 = (double10 - double1) * double16;
		} else {
			double19 = (double10 - double1) * double16;
			double20 = (double7 - double1) * double16;
		}

		double double21;
		double double22;
		if (double17 >= 0.0) {
			double21 = (double8 - double2) * double17;
			double22 = (double11 - double2) * double17;
		} else {
			double21 = (double11 - double2) * double17;
			double22 = (double8 - double2) * double17;
		}

		if (!(double19 > double22) && !(double21 > double20)) {
			double double23;
			double double24;
			if (double18 >= 0.0) {
				double23 = (double9 - double3) * double18;
				double24 = (double12 - double3) * double18;
			} else {
				double23 = (double12 - double3) * double18;
				double24 = (double9 - double3) * double18;
			}

			if (!(double19 > double24) && !(double23 > double20)) {
				double19 = !(double21 > double19) && !Double.isNaN(double19) ? double19 : double21;
				double20 = !(double22 < double20) && !Double.isNaN(double20) ? double20 : double22;
				double19 = double23 > double19 ? double23 : double19;
				double20 = double24 < double20 ? double24 : double20;
				byte byte1 = -1;
				if (double19 < double20 && double19 <= 1.0 && double20 >= 0.0) {
					if (double19 > 0.0 && double20 > 1.0) {
						double20 = double19;
						byte1 = 1;
					} else if (double19 < 0.0 && double20 < 1.0) {
						double19 = double20;
						byte1 = 1;
					} else if (double19 < 0.0 && double20 > 1.0) {
						byte1 = 3;
					} else {
						byte1 = 2;
					}

					vector2d.x = double19;
					vector2d.y = double20;
				}

				return byte1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public static int intersectLineSegmentAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector2d vector2d) {
		return intersectLineSegmentAab(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector2d);
	}

	public static boolean testRayAab(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12) {
		double double13 = 1.0 / double4;
		double double14 = 1.0 / double5;
		double double15 = 1.0 / double6;
		double double16;
		double double17;
		if (double13 >= 0.0) {
			double17 = (double7 - double1) * double13;
			double16 = (double10 - double1) * double13;
		} else {
			double17 = (double10 - double1) * double13;
			double16 = (double7 - double1) * double13;
		}

		double double18;
		double double19;
		if (double14 >= 0.0) {
			double18 = (double8 - double2) * double14;
			double19 = (double11 - double2) * double14;
		} else {
			double18 = (double11 - double2) * double14;
			double19 = (double8 - double2) * double14;
		}

		if (!(double17 > double19) && !(double18 > double16)) {
			double double20;
			double double21;
			if (double15 >= 0.0) {
				double20 = (double9 - double3) * double15;
				double21 = (double12 - double3) * double15;
			} else {
				double20 = (double12 - double3) * double15;
				double21 = (double9 - double3) * double15;
			}

			if (!(double17 > double21) && !(double20 > double16)) {
				double17 = !(double18 > double17) && !Double.isNaN(double17) ? double17 : double18;
				double16 = !(double19 < double16) && !Double.isNaN(double16) ? double16 : double19;
				double17 = double20 > double17 ? double20 : double17;
				double16 = double21 < double16 ? double21 : double16;
				return double17 < double16 && double16 >= 0.0;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean testRayAab(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4) {
		return testRayAab(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z());
	}

	public static boolean testRayTriangleFront(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		double double17 = double10 - double7;
		double double18 = double11 - double8;
		double double19 = double12 - double9;
		double double20 = double13 - double7;
		double double21 = double14 - double8;
		double double22 = double15 - double9;
		double double23 = double5 * double22 - double6 * double21;
		double double24 = double6 * double20 - double4 * double22;
		double double25 = double4 * double21 - double5 * double20;
		double double26 = double17 * double23 + double18 * double24 + double19 * double25;
		if (double26 < double16) {
			return false;
		} else {
			double double27 = double1 - double7;
			double double28 = double2 - double8;
			double double29 = double3 - double9;
			double double30 = double27 * double23 + double28 * double24 + double29 * double25;
			if (!(double30 < 0.0) && !(double30 > double26)) {
				double double31 = double28 * double19 - double29 * double18;
				double double32 = double29 * double17 - double27 * double19;
				double double33 = double27 * double18 - double28 * double17;
				double double34 = double4 * double31 + double5 * double32 + double6 * double33;
				if (!(double34 < 0.0) && !(double30 + double34 > double26)) {
					double double35 = 1.0 / double26;
					double double36 = (double20 * double31 + double21 * double32 + double22 * double33) * double35;
					return double36 >= double16;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public static boolean testRayTriangleFront(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3dc vector3dc5, double double1) {
		return testRayTriangleFront(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3dc5.x(), vector3dc5.y(), vector3dc5.z(), double1);
	}

	public static boolean testRayTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		double double17 = double10 - double7;
		double double18 = double11 - double8;
		double double19 = double12 - double9;
		double double20 = double13 - double7;
		double double21 = double14 - double8;
		double double22 = double15 - double9;
		double double23 = double5 * double22 - double6 * double21;
		double double24 = double6 * double20 - double4 * double22;
		double double25 = double4 * double21 - double5 * double20;
		double double26 = double17 * double23 + double18 * double24 + double19 * double25;
		if (double26 > -double16 && double26 < double16) {
			return false;
		} else {
			double double27 = double1 - double7;
			double double28 = double2 - double8;
			double double29 = double3 - double9;
			double double30 = 1.0 / double26;
			double double31 = (double27 * double23 + double28 * double24 + double29 * double25) * double30;
			if (!(double31 < 0.0) && !(double31 > 1.0)) {
				double double32 = double28 * double19 - double29 * double18;
				double double33 = double29 * double17 - double27 * double19;
				double double34 = double27 * double18 - double28 * double17;
				double double35 = (double4 * double32 + double5 * double33 + double6 * double34) * double30;
				if (!(double35 < 0.0) && !(double31 + double35 > 1.0)) {
					double double36 = (double20 * double32 + double21 * double33 + double22 * double34) * double30;
					return double36 >= double16;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public static boolean testRayTriangle(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3dc vector3dc5, double double1) {
		return testRayTriangleFront(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3dc5.x(), vector3dc5.y(), vector3dc5.z(), double1);
	}

	public static double intersectRayTriangleFront(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		double double17 = double10 - double7;
		double double18 = double11 - double8;
		double double19 = double12 - double9;
		double double20 = double13 - double7;
		double double21 = double14 - double8;
		double double22 = double15 - double9;
		double double23 = double5 * double22 - double6 * double21;
		double double24 = double6 * double20 - double4 * double22;
		double double25 = double4 * double21 - double5 * double20;
		double double26 = double17 * double23 + double18 * double24 + double19 * double25;
		if (double26 <= double16) {
			return -1.0;
		} else {
			double double27 = double1 - double7;
			double double28 = double2 - double8;
			double double29 = double3 - double9;
			double double30 = double27 * double23 + double28 * double24 + double29 * double25;
			if (!(double30 < 0.0) && !(double30 > double26)) {
				double double31 = double28 * double19 - double29 * double18;
				double double32 = double29 * double17 - double27 * double19;
				double double33 = double27 * double18 - double28 * double17;
				double double34 = double4 * double31 + double5 * double32 + double6 * double33;
				if (!(double34 < 0.0) && !(double30 + double34 > double26)) {
					double double35 = 1.0 / double26;
					double double36 = (double20 * double31 + double21 * double32 + double22 * double33) * double35;
					return double36;
				} else {
					return -1.0;
				}
			} else {
				return -1.0;
			}
		}
	}

	public static double intersectRayTriangleFront(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3dc vector3dc5, double double1) {
		return intersectRayTriangleFront(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3dc5.x(), vector3dc5.y(), vector3dc5.z(), double1);
	}

	public static double intersectRayTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		double double17 = double10 - double7;
		double double18 = double11 - double8;
		double double19 = double12 - double9;
		double double20 = double13 - double7;
		double double21 = double14 - double8;
		double double22 = double15 - double9;
		double double23 = double5 * double22 - double6 * double21;
		double double24 = double6 * double20 - double4 * double22;
		double double25 = double4 * double21 - double5 * double20;
		double double26 = double17 * double23 + double18 * double24 + double19 * double25;
		if (double26 > -double16 && double26 < double16) {
			return -1.0;
		} else {
			double double27 = double1 - double7;
			double double28 = double2 - double8;
			double double29 = double3 - double9;
			double double30 = 1.0 / double26;
			double double31 = (double27 * double23 + double28 * double24 + double29 * double25) * double30;
			if (!(double31 < 0.0) && !(double31 > 1.0)) {
				double double32 = double28 * double19 - double29 * double18;
				double double33 = double29 * double17 - double27 * double19;
				double double34 = double27 * double18 - double28 * double17;
				double double35 = (double4 * double32 + double5 * double33 + double6 * double34) * double30;
				if (!(double35 < 0.0) && !(double31 + double35 > 1.0)) {
					double double36 = (double20 * double32 + double21 * double33 + double22 * double34) * double30;
					return double36;
				} else {
					return -1.0;
				}
			} else {
				return -1.0;
			}
		}
	}

	public static double intersectRayTriangle(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3dc vector3dc5, double double1) {
		return intersectRayTriangle(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3dc5.x(), vector3dc5.y(), vector3dc5.z(), double1);
	}

	public static boolean testLineSegmentTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16) {
		double double17 = double4 - double1;
		double double18 = double5 - double2;
		double double19 = double6 - double3;
		double double20 = intersectRayTriangle(double1, double2, double3, double17, double18, double19, double7, double8, double9, double10, double11, double12, double13, double14, double15, double16);
		return double20 >= 0.0 && double20 <= 1.0;
	}

	public static boolean testLineSegmentTriangle(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3dc vector3dc5, double double1) {
		return testLineSegmentTriangle(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3dc5.x(), vector3dc5.y(), vector3dc5.z(), double1);
	}

	public static boolean intersectLineSegmentTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, double double11, double double12, double double13, double double14, double double15, double double16, Vector3d vector3d) {
		double double17 = double4 - double1;
		double double18 = double5 - double2;
		double double19 = double6 - double3;
		double double20 = intersectRayTriangle(double1, double2, double3, double17, double18, double19, double7, double8, double9, double10, double11, double12, double13, double14, double15, double16);
		if (double20 >= 0.0 && double20 <= 1.0) {
			vector3d.x = double1 + double17 * double20;
			vector3d.y = double2 + double18 * double20;
			vector3d.z = double3 + double19 * double20;
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectLineSegmentTriangle(Vector3dc vector3dc, Vector3dc vector3dc2, Vector3dc vector3dc3, Vector3dc vector3dc4, Vector3dc vector3dc5, double double1, Vector3d vector3d) {
		return intersectLineSegmentTriangle(vector3dc.x(), vector3dc.y(), vector3dc.z(), vector3dc2.x(), vector3dc2.y(), vector3dc2.z(), vector3dc3.x(), vector3dc3.y(), vector3dc3.z(), vector3dc4.x(), vector3dc4.y(), vector3dc4.z(), vector3dc5.x(), vector3dc5.y(), vector3dc5.z(), double1, vector3d);
	}

	public static boolean intersectLineSegmentPlane(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9, double double10, Vector3d vector3d) {
		double double11 = double4 - double1;
		double double12 = double5 - double2;
		double double13 = double6 - double3;
		double double14 = double7 * double11 + double8 * double12 + double9 * double13;
		double double15 = -(double7 * double1 + double8 * double2 + double9 * double3 + double10) / double14;
		if (double15 >= 0.0 && double15 <= 1.0) {
			vector3d.x = double1 + double15 * double11;
			vector3d.y = double2 + double15 * double12;
			vector3d.z = double3 + double15 * double13;
			return true;
		} else {
			return false;
		}
	}

	public static boolean testLineCircle(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = Math.sqrt(double1 * double1 + double2 * double2);
		double double8 = (double1 * double4 + double2 * double5 + double3) / double7;
		return -double6 <= double8 && double8 <= double6;
	}

	public static boolean intersectLineCircle(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d) {
		double double7 = 1.0 / Math.sqrt(double1 * double1 + double2 * double2);
		double double8 = (double1 * double4 + double2 * double5 + double3) * double7;
		if (-double6 <= double8 && double8 <= double6) {
			vector3d.x = double4 + double8 * double1 * double7;
			vector3d.y = double5 + double8 * double2 * double7;
			vector3d.z = Math.sqrt(double6 * double6 - double8 * double8);
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectLineCircle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, Vector3d vector3d) {
		return intersectLineCircle(double2 - double4, double3 - double1, (double1 - double3) * double2 + (double4 - double2) * double1, double5, double6, double7, vector3d);
	}

	public static boolean testAarLine(double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
		double double8;
		double double9;
		if (double5 > 0.0) {
			double8 = double3;
			double9 = double1;
		} else {
			double8 = double1;
			double9 = double3;
		}

		double double10;
		double double11;
		if (double6 > 0.0) {
			double10 = double4;
			double11 = double2;
		} else {
			double10 = double2;
			double11 = double4;
		}

		double double12 = double7 + double5 * double9 + double6 * double11;
		double double13 = double7 + double5 * double8 + double6 * double10;
		return double12 <= 0.0 && double13 >= 0.0;
	}

	public static boolean testAarLine(Vector2dc vector2dc, Vector2dc vector2dc2, double double1, double double2, double double3) {
		return testAarLine(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), double1, double2, double3);
	}

	public static boolean testAarLine(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = double6 - double8;
		double double10 = double7 - double5;
		double double11 = -double10 * double6 - double9 * double5;
		return testAarLine(double1, double2, double3, double4, double9, double10, double11);
	}

	public static boolean testAarAar(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		return double3 >= double5 && double4 >= double6 && double1 <= double7 && double2 <= double8;
	}

	public static boolean testAarAar(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4) {
		return testAarAar(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y());
	}

	public static boolean intersectCircleCircle(double double1, double double2, double double3, double double4, double double5, double double6, Vector3d vector3d) {
		double double7 = double4 - double1;
		double double8 = double5 - double2;
		double double9 = double7 * double7 + double8 * double8;
		double double10 = 0.5 + (double3 - double6) / double9;
		double double11 = Math.sqrt(double3 - double10 * double10 * double9);
		if (double11 >= 0.0) {
			vector3d.x = double1 + double10 * double7;
			vector3d.y = double2 + double10 * double8;
			vector3d.z = double11;
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectCircleCircle(Vector2dc vector2dc, double double1, Vector2dc vector2dc2, double double2, Vector3d vector3d) {
		return intersectCircleCircle(vector2dc.x(), vector2dc.y(), double1, vector2dc2.x(), vector2dc2.y(), double2, vector3d);
	}

	public static boolean testCircleCircle(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = (double1 - double4) * (double1 - double4) + (double2 - double5) * (double2 - double5);
		return double7 <= (double3 + double6) * (double3 + double6);
	}

	public static boolean testCircleCircle(Vector2dc vector2dc, double double1, Vector2dc vector2dc2, double double2) {
		return testCircleCircle(vector2dc.x(), vector2dc.y(), double1, vector2dc2.x(), vector2dc2.y(), double2);
	}

	public static double distancePointLine(double double1, double double2, double double3, double double4, double double5) {
		double double6 = Math.sqrt(double3 * double3 + double4 * double4);
		return (double3 * double1 + double4 * double2 + double5) / double6;
	}

	public static double distancePointLine(double double1, double double2, double double3, double double4, double double5, double double6) {
		double double7 = double5 - double3;
		double double8 = double6 - double4;
		double double9 = Math.sqrt(double7 * double7 + double8 * double8);
		return (double7 * (double4 - double2) - (double3 - double1) * double8) / double9;
	}

	public static double intersectRayLine(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double7 * double3 + double8 * double4;
		if (double10 < double9) {
			double double11 = ((double5 - double1) * double7 + (double6 - double2) * double8) / double10;
			if (double11 >= 0.0) {
				return double11;
			}
		}

		return -1.0;
	}

	public static double intersectRayLine(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4, double double1) {
		return intersectRayLine(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y(), double1);
	}

	public static double intersectRayLineSegment(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = double1 - double5;
		double double10 = double2 - double6;
		double double11 = double7 - double5;
		double double12 = double8 - double6;
		double double13 = 1.0 / (double12 * double3 - double11 * double4);
		double double14 = (double11 * double10 - double12 * double9) * double13;
		double double15 = (double10 * double3 - double9 * double4) * double13;
		return double14 >= 0.0 && double15 >= 0.0 && double15 <= 1.0 ? double14 : -1.0;
	}

	public static double intersectRayLineSegment(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4) {
		return intersectRayLineSegment(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y());
	}

	public static boolean testAarCircle(double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
		double double8 = double7;
		double double9;
		if (double5 < double1) {
			double9 = double5 - double1;
			double8 = double7 - double9 * double9;
		} else if (double5 > double3) {
			double9 = double5 - double3;
			double8 = double7 - double9 * double9;
		}

		if (double6 < double2) {
			double9 = double6 - double2;
			double8 -= double9 * double9;
		} else if (double6 > double4) {
			double9 = double6 - double4;
			double8 -= double9 * double9;
		}

		return double8 >= 0.0;
	}

	public static boolean testAarCircle(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, double double1) {
		return testAarCircle(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), double1);
	}

	public static int findClosestPointOnTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector2d vector2d) {
		double double9 = double1 - double7;
		double double10 = double2 - double8;
		double double11 = double3 - double7;
		double double12 = double4 - double8;
		double double13 = double5 - double7;
		double double14 = double6 - double8;
		double double15 = double11 - double9;
		double double16 = double12 - double10;
		double double17 = double13 - double9;
		double double18 = double14 - double10;
		double double19 = -(double15 * double9 + double16 * double10);
		double double20 = -(double17 * double9 + double18 * double10);
		if (double19 <= 0.0 && double20 <= 0.0) {
			vector2d.set(double1, double2);
			return 0;
		} else {
			double double21 = -(double15 * double11 + double16 * double12);
			double double22 = -(double17 * double11 + double18 * double12);
			if (double21 >= 0.0 && double22 <= double21) {
				vector2d.set(double3, double4);
				return 0;
			} else {
				double double23 = double19 * double22 - double21 * double20;
				double double24;
				if (double23 <= 0.0 && double19 >= 0.0 && double21 <= 0.0) {
					double24 = double19 / (double19 - double21);
					vector2d.set(double1 + double15 * double24, double2 + double16 * double24);
					return 1;
				} else {
					double24 = -(double15 * double13 + double16 * double14);
					double double25 = -(double17 * double13 + double18 * double14);
					if (double25 >= 0.0 && double24 <= double25) {
						vector2d.set(double5, double6);
						return 0;
					} else {
						double double26 = double24 * double20 - double19 * double25;
						double double27;
						if (double26 <= 0.0 && double20 >= 0.0 && double25 <= 0.0) {
							double27 = double20 / (double20 - double25);
							vector2d.set(double1 + double17 * double27, double2 + double18 * double27);
							return 1;
						} else {
							double27 = double21 * double25 - double24 * double22;
							double double28;
							if (double27 <= 0.0 && double22 - double21 >= 0.0 && double24 - double25 >= 0.0) {
								double28 = (double22 - double21) / (double22 - double21 + double24 - double25);
								vector2d.set(double3 + (double13 - double11) * double28, double4 + (double14 - double12) * double28);
								return 1;
							} else {
								double28 = 1.0 / (double27 + double26 + double23);
								double double29 = double26 * double28;
								double double30 = double23 * double28;
								vector2d.set(double1 + double15 * double29 + double17 * double30, double2 + double16 * double29 + double18 * double30);
								return 2;
							}
						}
					}
				}
			}
		}
	}

	public static int findClosestPointOnTriangle(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4, Vector2d vector2d) {
		return findClosestPointOnTriangle(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y(), vector2d);
	}

	public static boolean intersectRayCircle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, Vector2d vector2d) {
		double double8 = double5 - double1;
		double double9 = double6 - double2;
		double double10 = double8 * double3 + double9 * double4;
		double double11 = double8 * double8 + double9 * double9 - double10 * double10;
		if (double11 > double7) {
			return false;
		} else {
			double double12 = Math.sqrt(double7 - double11);
			double double13 = double10 - double12;
			double double14 = double10 + double12;
			if (double13 < double14 && double14 >= 0.0) {
				vector2d.x = double13;
				vector2d.y = double14;
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean intersectRayCircle(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, double double1, Vector2d vector2d) {
		return intersectRayCircle(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), double1, vector2d);
	}

	public static boolean testRayCircle(double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
		double double8 = double5 - double1;
		double double9 = double6 - double2;
		double double10 = double8 * double3 + double9 * double4;
		double double11 = double8 * double8 + double9 * double9 - double10 * double10;
		if (double11 > double7) {
			return false;
		} else {
			double double12 = Math.sqrt(double7 - double11);
			double double13 = double10 - double12;
			double double14 = double10 + double12;
			return double13 < double14 && double14 >= 0.0;
		}
	}

	public static boolean testRayCircle(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, double double1) {
		return testRayCircle(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), double1);
	}

	public static int intersectRayAar(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector2d vector2d) {
		double double9 = 1.0 / double3;
		double double10 = 1.0 / double4;
		double double11;
		double double12;
		if (double9 >= 0.0) {
			double11 = (double5 - double1) * double9;
			double12 = (double7 - double1) * double9;
		} else {
			double11 = (double7 - double1) * double9;
			double12 = (double5 - double1) * double9;
		}

		double double13;
		double double14;
		if (double10 >= 0.0) {
			double13 = (double6 - double2) * double10;
			double14 = (double8 - double2) * double10;
		} else {
			double13 = (double8 - double2) * double10;
			double14 = (double6 - double2) * double10;
		}

		if (!(double11 > double14) && !(double13 > double12)) {
			double11 = !(double13 > double11) && !Double.isNaN(double11) ? double11 : double13;
			double12 = !(double14 < double12) && !Double.isNaN(double12) ? double12 : double14;
			byte byte1 = -1;
			if (double11 < double12 && double12 >= 0.0) {
				double double15 = double1 + double11 * double3;
				double double16 = double2 + double11 * double4;
				vector2d.x = double11;
				vector2d.y = double12;
				double double17 = Math.abs(double15 - double5);
				double double18 = Math.abs(double16 - double6);
				double double19 = Math.abs(double15 - double7);
				double double20 = Math.abs(double16 - double8);
				byte1 = 0;
				double double21 = double17;
				if (double18 < double17) {
					double21 = double18;
					byte1 = 1;
				}

				if (double19 < double21) {
					double21 = double19;
					byte1 = 2;
				}

				if (double20 < double21) {
					byte1 = 3;
				}
			}

			return byte1;
		} else {
			return -1;
		}
	}

	public static int intersectRayAar(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4, Vector2d vector2d) {
		return intersectRayAar(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y(), vector2d);
	}

	public static int intersectLineSegmentAar(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector2d vector2d) {
		double double9 = double3 - double1;
		double double10 = double4 - double2;
		double double11 = 1.0 / double9;
		double double12 = 1.0 / double10;
		double double13;
		double double14;
		if (double11 >= 0.0) {
			double13 = (double5 - double1) * double11;
			double14 = (double7 - double1) * double11;
		} else {
			double13 = (double7 - double1) * double11;
			double14 = (double5 - double1) * double11;
		}

		double double15;
		double double16;
		if (double12 >= 0.0) {
			double15 = (double6 - double2) * double12;
			double16 = (double8 - double2) * double12;
		} else {
			double15 = (double8 - double2) * double12;
			double16 = (double6 - double2) * double12;
		}

		if (!(double13 > double16) && !(double15 > double14)) {
			double13 = !(double15 > double13) && !Double.isNaN(double13) ? double13 : double15;
			double14 = !(double16 < double14) && !Double.isNaN(double14) ? double14 : double16;
			byte byte1 = -1;
			if (double13 < double14 && double13 <= 1.0 && double14 >= 0.0) {
				if (double13 > 0.0 && double14 > 1.0) {
					double14 = double13;
					byte1 = 1;
				} else if (double13 < 0.0 && double14 < 1.0) {
					double13 = double14;
					byte1 = 1;
				} else if (double13 < 0.0 && double14 > 1.0) {
					byte1 = 3;
				} else {
					byte1 = 2;
				}

				vector2d.x = double13;
				vector2d.y = double14;
			}

			return byte1;
		} else {
			return -1;
		}
	}

	public static int intersectLineSegmentAar(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4, Vector2d vector2d) {
		return intersectLineSegmentAar(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y(), vector2d);
	}

	public static boolean testRayAar(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		double double9 = 1.0 / double3;
		double double10 = 1.0 / double4;
		double double11;
		double double12;
		if (double9 >= 0.0) {
			double11 = (double5 - double1) * double9;
			double12 = (double7 - double1) * double9;
		} else {
			double11 = (double7 - double1) * double9;
			double12 = (double5 - double1) * double9;
		}

		double double13;
		double double14;
		if (double10 >= 0.0) {
			double13 = (double6 - double2) * double10;
			double14 = (double8 - double2) * double10;
		} else {
			double13 = (double8 - double2) * double10;
			double14 = (double6 - double2) * double10;
		}

		if (!(double11 > double14) && !(double13 > double12)) {
			double11 = !(double13 > double11) && !Double.isNaN(double11) ? double11 : double13;
			double12 = !(double14 < double12) && !Double.isNaN(double12) ? double12 : double14;
			return double11 < double12 && double12 >= 0.0;
		} else {
			return false;
		}
	}

	public static boolean testRayAar(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4) {
		return testRayAar(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y());
	}

	public static boolean testPointTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
		boolean boolean1 = (double1 - double5) * (double4 - double6) - (double3 - double5) * (double2 - double6) < 0.0;
		boolean boolean2 = (double1 - double7) * (double6 - double8) - (double5 - double7) * (double2 - double8) < 0.0;
		if (boolean1 != boolean2) {
			return false;
		} else {
			boolean boolean3 = (double1 - double3) * (double8 - double4) - (double7 - double3) * (double2 - double4) < 0.0;
			return boolean2 == boolean3;
		}
	}

	public static boolean testPointTriangle(Vector2dc vector2dc, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4) {
		return testPointTriangle(vector2dc.x(), vector2dc.y(), vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y());
	}

	public static boolean testPointAar(double double1, double double2, double double3, double double4, double double5, double double6) {
		return double1 >= double3 && double2 >= double4 && double1 <= double5 && double2 <= double6;
	}

	public static boolean testPointCircle(double double1, double double2, double double3, double double4, double double5) {
		double double6 = double1 - double3;
		double double7 = double2 - double4;
		double double8 = double6 * double6;
		double double9 = double7 * double7;
		return double8 + double9 <= double5;
	}

	public static boolean testCircleTriangle(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
		double double10 = double1 - double4;
		double double11 = double2 - double5;
		double double12 = double10 * double10 + double11 * double11 - double3;
		if (double12 <= 0.0) {
			return true;
		} else {
			double double13 = double1 - double6;
			double double14 = double2 - double7;
			double double15 = double13 * double13 + double14 * double14 - double3;
			if (double15 <= 0.0) {
				return true;
			} else {
				double double16 = double1 - double8;
				double double17 = double2 - double9;
				double double18 = double16 * double16 + double17 * double17 - double3;
				if (double18 <= 0.0) {
					return true;
				} else {
					double double19 = double6 - double4;
					double double20 = double7 - double5;
					double double21 = double8 - double6;
					double double22 = double9 - double7;
					double double23 = double4 - double8;
					double double24 = double5 - double9;
					if (double19 * double11 - double20 * double10 >= 0.0 && double21 * double14 - double22 * double13 >= 0.0 && double23 * double17 - double24 * double16 >= 0.0) {
						return true;
					} else {
						double double25 = double10 * double19 + double11 * double20;
						double double26;
						if (double25 >= 0.0) {
							double26 = double19 * double19 + double20 * double20;
							if (double25 <= double26 && double12 * double26 <= double25 * double25) {
								return true;
							}
						}

						double25 = double13 * double21 + double14 * double22;
						if (double25 > 0.0) {
							double26 = double21 * double21 + double22 * double22;
							if (double25 <= double26 && double15 * double26 <= double25 * double25) {
								return true;
							}
						}

						double25 = double16 * double23 + double17 * double24;
						if (double25 >= 0.0) {
							double26 = double23 * double23 + double24 * double24;
							if (double25 < double26 && double18 * double26 <= double25 * double25) {
								return true;
							}
						}

						return false;
					}
				}
			}
		}
	}

	public static boolean testCircleTriangle(Vector2dc vector2dc, double double1, Vector2dc vector2dc2, Vector2dc vector2dc3, Vector2dc vector2dc4) {
		return testCircleTriangle(vector2dc.x(), vector2dc.y(), double1, vector2dc2.x(), vector2dc2.y(), vector2dc3.x(), vector2dc3.y(), vector2dc4.x(), vector2dc4.y());
	}

	public static int intersectPolygonRay(double[] doubleArray, double double1, double double2, double double3, double double4, Vector2d vector2d) {
		double double5 = Double.MAX_VALUE;
		int int1 = doubleArray.length >> 1;
		int int2 = -1;
		double double6 = doubleArray[int1 - 1 << 1];
		double double7 = doubleArray[(int1 - 1 << 1) + 1];
		for (int int3 = 0; int3 < int1; ++int3) {
			double double8 = doubleArray[int3 << 1];
			double double9 = doubleArray[(int3 << 1) + 1];
			double double10 = double1 - double6;
			double double11 = double2 - double7;
			double double12 = double8 - double6;
			double double13 = double9 - double7;
			double double14 = 1.0 / (double13 * double3 - double12 * double4);
			double double15 = (double12 * double11 - double13 * double10) * double14;
			if (double15 >= 0.0 && double15 < double5) {
				double double16 = (double11 * double3 - double10 * double4) * double14;
				if (double16 >= 0.0 && double16 <= 1.0) {
					int2 = (int3 - 1 + int1) % int1;
					double5 = double15;
					vector2d.x = double1 + double15 * double3;
					vector2d.y = double2 + double15 * double4;
				}
			}

			double6 = double8;
			double7 = double9;
		}

		return int2;
	}

	public static int intersectPolygonRay(Vector2dc[] vector2dcArray, double double1, double double2, double double3, double double4, Vector2d vector2d) {
		double double5 = Double.MAX_VALUE;
		int int1 = vector2dcArray.length;
		int int2 = -1;
		double double6 = vector2dcArray[int1 - 1].x();
		double double7 = vector2dcArray[int1 - 1].y();
		for (int int3 = 0; int3 < int1; ++int3) {
			Vector2dc vector2dc = vector2dcArray[int3];
			double double8 = vector2dc.x();
			double double9 = vector2dc.y();
			double double10 = double1 - double6;
			double double11 = double2 - double7;
			double double12 = double8 - double6;
			double double13 = double9 - double7;
			double double14 = 1.0 / (double13 * double3 - double12 * double4);
			double double15 = (double12 * double11 - double13 * double10) * double14;
			if (double15 >= 0.0 && double15 < double5) {
				double double16 = (double11 * double3 - double10 * double4) * double14;
				if (double16 >= 0.0 && double16 <= 1.0) {
					int2 = (int3 - 1 + int1) % int1;
					double5 = double15;
					vector2d.x = double1 + double15 * double3;
					vector2d.y = double2 + double15 * double4;
				}
			}

			double6 = double8;
			double7 = double9;
		}

		return int2;
	}

	public static boolean intersectLineLine(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, Vector2d vector2d) {
		double double9 = double1 - double3;
		double double10 = double4 - double2;
		double double11 = double10 * double1 + double9 * double2;
		double double12 = double5 - double7;
		double double13 = double8 - double6;
		double double14 = double13 * double5 + double12 * double6;
		double double15 = double10 * double12 - double13 * double9;
		if (double15 == 0.0) {
			return false;
		} else {
			vector2d.x = (double12 * double11 - double9 * double14) / double15;
			vector2d.y = (double10 * double14 - double13 * double11) / double15;
			return true;
		}
	}
}
