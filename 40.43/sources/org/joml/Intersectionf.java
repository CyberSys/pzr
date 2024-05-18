package org.joml;


public class Intersectionf {
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

	public static boolean testPlaneSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
		float float10 = (float1 * float5 + float2 * float6 + float3 * float7 + float4) / float9;
		return -float8 <= float10 && float10 <= float8;
	}

	public static boolean intersectPlaneSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector4f vector4f) {
		float float9 = 1.0F / (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
		float float10 = (float1 * float5 + float2 * float6 + float3 * float7 + float4) * float9;
		if (-float8 <= float10 && float10 <= float8) {
			vector4f.x = float5 + float10 * float1 * float9;
			vector4f.y = float6 + float10 * float2 * float9;
			vector4f.z = float7 + float10 * float3 * float9;
			vector4f.w = (float)Math.sqrt((double)(float8 * float8 - float10 * float10));
			return true;
		} else {
			return false;
		}
	}

	public static boolean testAabPlane(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		float float11;
		float float12;
		if (float7 > 0.0F) {
			float11 = float4;
			float12 = float1;
		} else {
			float11 = float1;
			float12 = float4;
		}

		float float13;
		float float14;
		if (float8 > 0.0F) {
			float13 = float5;
			float14 = float2;
		} else {
			float13 = float2;
			float14 = float5;
		}

		float float15;
		float float16;
		if (float9 > 0.0F) {
			float15 = float6;
			float16 = float3;
		} else {
			float15 = float3;
			float16 = float6;
		}

		float float17 = float10 + float7 * float12 + float8 * float14 + float9 * float16;
		float float18 = float10 + float7 * float11 + float8 * float13 + float9 * float15;
		return float17 <= 0.0F && float18 >= 0.0F;
	}

	public static boolean testAabPlane(Vector3fc vector3fc, Vector3fc vector3fc2, float float1, float float2, float float3, float float4) {
		return testAabPlane(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), float1, float2, float3, float4);
	}

	public static boolean testAabAab(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		return float4 >= float7 && float5 >= float8 && float6 >= float9 && float1 <= float10 && float2 <= float11 && float3 <= float12;
	}

	public static boolean testAabAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4) {
		return testAabAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z());
	}

	public static boolean intersectSphereSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector4f vector4f) {
		float float9 = float5 - float1;
		float float10 = float6 - float2;
		float float11 = float7 - float3;
		float float12 = float9 * float9 + float10 * float10 + float11 * float11;
		float float13 = 0.5F + (float4 - float8) / float12;
		float float14 = float4 - float13 * float13 * float12;
		if (float14 >= 0.0F) {
			vector4f.x = float1 + float13 * float9;
			vector4f.y = float2 + float13 * float10;
			vector4f.z = float3 + float13 * float11;
			vector4f.w = (float)Math.sqrt((double)float14);
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectSphereSphere(Vector3fc vector3fc, float float1, Vector3fc vector3fc2, float float2, Vector4f vector4f) {
		return intersectSphereSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), float1, vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), float2, vector4f);
	}

	public static boolean testSphereSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = float5 - float1;
		float float10 = float6 - float2;
		float float11 = float7 - float3;
		float float12 = float9 * float9 + float10 * float10 + float11 * float11;
		float float13 = 0.5F + (float4 - float8) / float12;
		float float14 = float4 - float13 * float13 * float12;
		return float14 >= 0.0F;
	}

	public static boolean testSphereSphere(Vector3fc vector3fc, float float1, Vector3fc vector3fc2, float float2) {
		return testSphereSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), float1, vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), float2);
	}

	public static float distancePointPlane(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = (float)Math.sqrt((double)(float4 * float4 + float5 * float5 + float6 * float6));
		return (float4 * float1 + float5 * float2 + float6 * float3 + float7) / float8;
	}

	public static float distancePointPlane(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		float float13 = float8 - float5;
		float float14 = float12 - float6;
		float float15 = float11 - float5;
		float float16 = float9 - float6;
		float float17 = float10 - float4;
		float float18 = float7 - float4;
		float float19 = float13 * float14 - float15 * float16;
		float float20 = float16 * float17 - float14 * float18;
		float float21 = float18 * float15 - float17 * float13;
		float float22 = -(float19 * float4 + float20 * float5 + float21 * float6);
		return distancePointPlane(float1, float2, float3, float19, float20, float21, float22);
	}

	public static float intersectRayPlane(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13) {
		float float14 = float10 * float4 + float11 * float5 + float12 * float6;
		if (float14 < float13) {
			float float15 = ((float7 - float1) * float10 + (float8 - float2) * float11 + (float9 - float3) * float12) / float14;
			if (float15 >= 0.0F) {
				return float15;
			}
		}

		return -1.0F;
	}

	public static float intersectRayPlane(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, float float1) {
		return intersectRayPlane(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), float1);
	}

	public static float intersectRayPlane(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11) {
		float float12 = float7 * float4 + float8 * float5 + float9 * float6;
		if (float12 < 0.0F) {
			float float13 = -(float7 * float1 + float8 * float2 + float9 * float3 + float10) / float12;
			if (float13 >= 0.0F) {
				return float13;
			}
		}

		return -1.0F;
	}

	public static boolean testAabSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		float float11 = float10;
		float float12;
		if (float7 < float1) {
			float12 = float7 - float1;
			float11 = float10 - float12 * float12;
		} else if (float7 > float4) {
			float12 = float7 - float4;
			float11 = float10 - float12 * float12;
		}

		if (float8 < float2) {
			float12 = float8 - float2;
			float11 -= float12 * float12;
		} else if (float8 > float5) {
			float12 = float8 - float5;
			float11 -= float12 * float12;
		}

		if (float9 < float3) {
			float12 = float9 - float3;
			float11 -= float12 * float12;
		} else if (float9 > float6) {
			float12 = float9 - float6;
			float11 -= float12 * float12;
		}

		return float11 >= 0.0F;
	}

	public static boolean testAabSphere(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1) {
		return testAabSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), float1);
	}

	public static int findClosestPointOnTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, Vector3f vector3f) {
		float float13 = float1 - float10;
		float float14 = float2 - float11;
		float float15 = float3 - float12;
		float float16 = float4 - float10;
		float float17 = float5 - float11;
		float float18 = float6 - float12;
		float float19 = float7 - float10;
		float float20 = float8 - float11;
		float float21 = float9 - float12;
		float float22 = float16 - float13;
		float float23 = float17 - float14;
		float float24 = float18 - float15;
		float float25 = float19 - float13;
		float float26 = float20 - float14;
		float float27 = float21 - float15;
		float float28 = -(float22 * float13 + float23 * float14 + float24 * float15);
		float float29 = -(float25 * float13 + float26 * float14 + float27 * float15);
		if (float28 <= 0.0F && float29 <= 0.0F) {
			vector3f.set(float1, float2, float3);
			return 0;
		} else {
			float float30 = -(float22 * float16 + float23 * float17 + float24 * float18);
			float float31 = -(float25 * float16 + float26 * float17 + float27 * float18);
			if (float30 >= 0.0F && float31 <= float30) {
				vector3f.set(float4, float5, float6);
				return 0;
			} else {
				float float32 = float28 * float31 - float30 * float29;
				float float33;
				if (float32 <= 0.0F && float28 >= 0.0F && float30 <= 0.0F) {
					float33 = float28 / (float28 - float30);
					vector3f.set(float1 + float22 * float33, float2 + float23 * float33, float3 * float24 * float33);
					return 1;
				} else {
					float33 = -(float22 * float19 + float23 * float20 + float24 * float21);
					float float34 = -(float25 * float19 + float26 * float20 + float27 * float21);
					if (float34 >= 0.0F && float33 <= float34) {
						vector3f.set(float7, float8, float9);
						return 0;
					} else {
						float float35 = float33 * float29 - float28 * float34;
						float float36;
						if (float35 <= 0.0F && float29 >= 0.0F && float34 <= 0.0F) {
							float36 = float29 / (float29 - float34);
							vector3f.set(float1 + float25 * float36, float2 + float26 * float36, float3 + float27 * float36);
							return 1;
						} else {
							float36 = float30 * float34 - float33 * float31;
							float float37;
							if (float36 <= 0.0F && float31 - float30 >= 0.0F && float33 - float34 >= 0.0F) {
								float37 = (float31 - float30) / (float31 - float30 + float33 - float34);
								vector3f.set(float4 + (float19 - float16) * float37, float5 + (float20 - float17) * float37, float6 + (float21 - float18) * float37);
								return 1;
							} else {
								float37 = 1.0F / (float36 + float35 + float32);
								float float38 = float35 * float37;
								float float39 = float32 * float37;
								vector3f.set(float1 + float22 * float38 + float25 * float39, float2 + float23 * float38 + float26 * float39, float3 + float24 * float38 + float27 * float39);
								return 2;
							}
						}
					}
				}
			}
		}
	}

	public static int findClosestPointOnTriangle(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3f vector3f) {
		return findClosestPointOnTriangle(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3f);
	}

	public static int intersectSweptSphereTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17, float float18, Vector4f vector4f) {
		float float19 = float11 - float8;
		float float20 = float12 - float9;
		float float21 = float13 - float10;
		float float22 = float14 - float8;
		float float23 = float15 - float9;
		float float24 = float16 - float10;
		float float25 = float20 * float24 - float23 * float21;
		float float26 = float21 * float22 - float24 * float19;
		float float27 = float19 * float23 - float22 * float20;
		float float28 = -(float25 * float8 + float26 * float9 + float27 * float10);
		float float29 = (float)(1.0 / Math.sqrt((double)(float25 * float25 + float26 * float26 + float27 * float27)));
		float float30 = (float25 * float1 + float26 * float2 + float27 * float3 + float28) * float29;
		float float31 = (float25 * float5 + float26 * float6 + float27 * float7) * float29;
		if (float31 < float17 && float31 > -float17) {
			return -1;
		} else {
			float float32 = (float4 - float30) / float31;
			if (float32 > float18) {
				return -1;
			} else {
				float float33 = (-float4 - float30) / float31;
				float float34 = float1 - float4 * float25 * float29 + float5 * float32;
				float float35 = float2 - float4 * float26 * float29 + float6 * float32;
				float float36 = float3 - float4 * float27 * float29 + float7 * float32;
				boolean boolean1 = testPointInTriangle(float34, float35, float36, float8, float9, float10, float11, float12, float13, float14, float15, float16);
				if (boolean1) {
					vector4f.x = float34;
					vector4f.y = float35;
					vector4f.z = float36;
					vector4f.w = float32;
					return 2;
				} else {
					byte byte1 = -1;
					float float37 = float18;
					float float38 = float5 * float5 + float6 * float6 + float7 * float7;
					float float39 = float4 * float4;
					float float40 = float1 - float8;
					float float41 = float2 - float9;
					float float42 = float3 - float10;
					float float43 = 2.0F * (float5 * float40 + float6 * float41 + float7 * float42);
					float float44 = float40 * float40 + float41 * float41 + float42 * float42 - float39;
					float float45 = computeLowestRoot(float38, float43, float44, float18);
					if (float45 < float18) {
						vector4f.x = float8;
						vector4f.y = float9;
						vector4f.z = float10;
						vector4f.w = float45;
						float37 = float45;
						byte1 = 0;
					}

					float float46 = float1 - float11;
					float float47 = float2 - float12;
					float float48 = float3 - float13;
					float float49 = float46 * float46 + float47 * float47 + float48 * float48;
					float float50 = 2.0F * (float5 * float46 + float6 * float47 + float7 * float48);
					float float51 = float49 - float39;
					float float52 = computeLowestRoot(float38, float50, float51, float37);
					if (float52 < float37) {
						vector4f.x = float11;
						vector4f.y = float12;
						vector4f.z = float13;
						vector4f.w = float52;
						float37 = float52;
						byte1 = 0;
					}

					float float53 = float1 - float14;
					float float54 = float2 - float15;
					float float55 = float3 - float16;
					float float56 = 2.0F * (float5 * float53 + float6 * float54 + float7 * float55);
					float float57 = float53 * float53 + float54 * float54 + float55 * float55 - float39;
					float float58 = computeLowestRoot(float38, float56, float57, float37);
					if (float58 < float37) {
						vector4f.x = float14;
						vector4f.y = float15;
						vector4f.z = float16;
						vector4f.w = float58;
						float37 = float58;
						byte1 = 0;
					}

					float float59 = float5 * float5 + float6 * float6 + float7 * float7;
					float float60 = float19 * float19 + float20 * float20 + float21 * float21;
					float float61 = float40 * float40 + float41 * float41 + float42 * float42;
					float float62 = float19 * float5 + float20 * float6 + float21 * float7;
					float float63 = float60 * -float59 + float62 * float62;
					float float64 = float19 * -float40 + float20 * -float41 + float21 * -float42;
					float float65 = float5 * -float40 + float6 * -float41 + float7 * -float42;
					float float66 = float60 * 2.0F * float65 - 2.0F * float62 * float64;
					float float67 = float60 * (float39 - float61) + float64 * float64;
					float float68 = computeLowestRoot(float63, float66, float67, float37);
					float float69 = (float62 * float68 - float64) / float60;
					if (float69 >= 0.0F && float69 <= 1.0F && float68 < float37) {
						vector4f.x = float8 + float69 * float19;
						vector4f.y = float9 + float69 * float20;
						vector4f.z = float10 + float69 * float21;
						vector4f.w = float68;
						float37 = float68;
						byte1 = 1;
					}

					float float70 = float22 * float22 + float23 * float23 + float24 * float24;
					float float71 = float22 * float5 + float23 * float6 + float24 * float7;
					float float72 = float70 * -float59 + float71 * float71;
					float float73 = float22 * -float40 + float23 * -float41 + float24 * -float42;
					float float74 = float70 * 2.0F * float65 - 2.0F * float71 * float73;
					float float75 = float70 * (float39 - float61) + float73 * float73;
					float float76 = computeLowestRoot(float72, float74, float75, float37);
					float float77 = (float71 * float76 - float73) / float70;
					if (float77 >= 0.0F && float77 <= 1.0F && float76 < float33) {
						vector4f.x = float8 + float77 * float22;
						vector4f.y = float9 + float77 * float23;
						vector4f.z = float10 + float77 * float24;
						vector4f.w = float76;
						float37 = float76;
						byte1 = 1;
					}

					float float78 = float14 - float11;
					float float79 = float15 - float12;
					float float80 = float16 - float13;
					float float81 = float78 * float78 + float79 * float79 + float80 * float80;
					float float82 = float78 * float5 + float79 * float6 + float80 * float7;
					float float83 = float81 * -float59 + float82 * float82;
					float float84 = float78 * -float46 + float79 * -float47 + float80 * -float48;
					float float85 = float5 * -float46 + float6 * -float47 + float7 * -float48;
					float float86 = float81 * 2.0F * float85 - 2.0F * float82 * float84;
					float float87 = float81 * (float39 - float49) + float84 * float84;
					float float88 = computeLowestRoot(float83, float86, float87, float37);
					float float89 = (float82 * float88 - float84) / float81;
					if (float89 >= 0.0F && float89 <= 1.0F && float88 < float37) {
						vector4f.x = float11 + float89 * float78;
						vector4f.y = float12 + float89 * float79;
						vector4f.z = float13 + float89 * float80;
						vector4f.w = float88;
						byte1 = 1;
					}

					return byte1;
				}
			}
		}
	}

	private static float computeLowestRoot(float float1, float float2, float float3, float float4) {
		float float5 = float2 * float2 - 4.0F * float1 * float3;
		if (float5 < 0.0F) {
			return Float.MAX_VALUE;
		} else {
			float float6 = (float)Math.sqrt((double)float5);
			float float7 = (-float2 - float6) / (2.0F * float1);
			float float8 = (-float2 + float6) / (2.0F * float1);
			if (float7 > float8) {
				float float9 = float8;
				float8 = float7;
				float7 = float9;
			}

			if (float7 > 0.0F && float7 < float4) {
				return float7;
			} else {
				return float8 > 0.0F && float8 < float4 ? float8 : Float.MAX_VALUE;
			}
		}
	}

	public static boolean testPointInTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		float float13 = float7 - float4;
		float float14 = float8 - float5;
		float float15 = float9 - float6;
		float float16 = float10 - float4;
		float float17 = float11 - float5;
		float float18 = float12 - float6;
		float float19 = float13 * float13 + float14 * float14 + float15 * float15;
		float float20 = float13 * float16 + float14 * float17 + float15 * float18;
		float float21 = float16 * float16 + float17 * float17 + float18 * float18;
		float float22 = float19 * float21 - float20 * float20;
		float float23 = float1 - float4;
		float float24 = float2 - float5;
		float float25 = float3 - float6;
		float float26 = float23 * float13 + float24 * float14 + float25 * float15;
		float float27 = float23 * float16 + float24 * float17 + float25 * float18;
		float float28 = float26 * float21 - float27 * float20;
		float float29 = float27 * float19 - float26 * float20;
		float float30 = float28 + float29 - float22;
		return (Float.floatToRawIntBits(float30) & ~(Float.floatToRawIntBits(float28) | Float.floatToRawIntBits(float29)) & Integer.MIN_VALUE) != 0;
	}

	public static boolean intersectRaySphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, Vector2f vector2f) {
		float float11 = float7 - float1;
		float float12 = float8 - float2;
		float float13 = float9 - float3;
		float float14 = float11 * float4 + float12 * float5 + float13 * float6;
		float float15 = float11 * float11 + float12 * float12 + float13 * float13 - float14 * float14;
		if (float15 > float10) {
			return false;
		} else {
			float float16 = (float)Math.sqrt((double)(float10 - float15));
			float float17 = float14 - float16;
			float float18 = float14 + float16;
			if (float17 < float18 && float18 >= 0.0F) {
				vector2f.x = float17;
				vector2f.y = float18;
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean intersectRaySphere(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1, Vector2f vector2f) {
		return intersectRaySphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), float1, vector2f);
	}

	public static boolean testRaySphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		float float11 = float7 - float1;
		float float12 = float8 - float2;
		float float13 = float9 - float3;
		float float14 = float11 * float4 + float12 * float5 + float13 * float6;
		float float15 = float11 * float11 + float12 * float12 + float13 * float13 - float14 * float14;
		if (float15 > float10) {
			return false;
		} else {
			float float16 = (float)Math.sqrt((double)(float10 - float15));
			float float17 = float14 - float16;
			float float18 = float14 + float16;
			return float17 < float18 && float18 >= 0.0F;
		}
	}

	public static boolean testRaySphere(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1) {
		return testRaySphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), float1);
	}

	public static boolean testLineSegmentSphere(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10) {
		float float11 = float4 - float1;
		float float12 = float5 - float2;
		float float13 = float6 - float3;
		float float14 = (float7 - float1) * float11 + (float8 - float2) * float12 + (float9 - float3) * float13;
		float float15 = float11 * float11 + float12 * float12 + float13 * float13;
		float float16 = float14 / float15;
		float float17;
		if (float16 < 0.0F) {
			float11 = float1 - float7;
			float12 = float2 - float8;
			float13 = float3 - float9;
		} else if (float16 > 1.0F) {
			float11 = float4 - float7;
			float12 = float5 - float8;
			float13 = float6 - float9;
		} else {
			float17 = float1 + float16 * float11;
			float float18 = float2 + float16 * float12;
			float float19 = float3 + float16 * float13;
			float11 = float17 - float7;
			float12 = float18 - float8;
			float13 = float19 - float9;
		}

		float17 = float11 * float11 + float12 * float12 + float13 * float13;
		return float17 <= float10;
	}

	public static boolean testLineSegmentSphere(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, float float1) {
		return testLineSegmentSphere(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), float1);
	}

	public static boolean intersectRayAab(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, Vector2f vector2f) {
		float float13 = 1.0F / float4;
		float float14 = 1.0F / float5;
		float float15 = 1.0F / float6;
		float float16;
		float float17;
		if (float13 >= 0.0F) {
			float16 = (float7 - float1) * float13;
			float17 = (float10 - float1) * float13;
		} else {
			float16 = (float10 - float1) * float13;
			float17 = (float7 - float1) * float13;
		}

		float float18;
		float float19;
		if (float14 >= 0.0F) {
			float18 = (float8 - float2) * float14;
			float19 = (float11 - float2) * float14;
		} else {
			float18 = (float11 - float2) * float14;
			float19 = (float8 - float2) * float14;
		}

		if (!(float16 > float19) && !(float18 > float17)) {
			float float20;
			float float21;
			if (float15 >= 0.0F) {
				float20 = (float9 - float3) * float15;
				float21 = (float12 - float3) * float15;
			} else {
				float20 = (float12 - float3) * float15;
				float21 = (float9 - float3) * float15;
			}

			if (!(float16 > float21) && !(float20 > float17)) {
				float16 = !(float18 > float16) && !Float.isNaN(float16) ? float16 : float18;
				float17 = !(float19 < float17) && !Float.isNaN(float17) ? float17 : float19;
				float16 = float20 > float16 ? float20 : float16;
				float17 = float21 < float17 ? float21 : float17;
				if (float16 < float17 && float17 >= 0.0F) {
					vector2f.x = float16;
					vector2f.y = float17;
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

	public static boolean intersectRayAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector2f vector2f) {
		return intersectRayAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector2f);
	}

	public static int intersectLineSegmentAab(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, Vector2f vector2f) {
		float float13 = float4 - float1;
		float float14 = float5 - float2;
		float float15 = float6 - float3;
		float float16 = 1.0F / float13;
		float float17 = 1.0F / float14;
		float float18 = 1.0F / float15;
		float float19;
		float float20;
		if (float16 >= 0.0F) {
			float19 = (float7 - float1) * float16;
			float20 = (float10 - float1) * float16;
		} else {
			float19 = (float10 - float1) * float16;
			float20 = (float7 - float1) * float16;
		}

		float float21;
		float float22;
		if (float17 >= 0.0F) {
			float21 = (float8 - float2) * float17;
			float22 = (float11 - float2) * float17;
		} else {
			float21 = (float11 - float2) * float17;
			float22 = (float8 - float2) * float17;
		}

		if (!(float19 > float22) && !(float21 > float20)) {
			float float23;
			float float24;
			if (float18 >= 0.0F) {
				float23 = (float9 - float3) * float18;
				float24 = (float12 - float3) * float18;
			} else {
				float23 = (float12 - float3) * float18;
				float24 = (float9 - float3) * float18;
			}

			if (!(float19 > float24) && !(float23 > float20)) {
				float19 = !(float21 > float19) && !Float.isNaN(float19) ? float19 : float21;
				float20 = !(float22 < float20) && !Float.isNaN(float20) ? float20 : float22;
				float19 = float23 > float19 ? float23 : float19;
				float20 = float24 < float20 ? float24 : float20;
				byte byte1 = -1;
				if (float19 < float20 && float19 <= 1.0F && float20 >= 0.0F) {
					if (float19 > 0.0F && float20 > 1.0F) {
						float20 = float19;
						byte1 = 1;
					} else if (float19 < 0.0F && float20 < 1.0F) {
						float19 = float20;
						byte1 = 1;
					} else if (float19 < 0.0F && float20 > 1.0F) {
						byte1 = 3;
					} else {
						byte1 = 2;
					}

					vector2f.x = float19;
					vector2f.y = float20;
				}

				return byte1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public static int intersectLineSegmentAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector2f vector2f) {
		return intersectLineSegmentAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector2f);
	}

	public static boolean testRayAab(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12) {
		float float13 = 1.0F / float4;
		float float14 = 1.0F / float5;
		float float15 = 1.0F / float6;
		float float16;
		float float17;
		if (float13 >= 0.0F) {
			float16 = (float7 - float1) * float13;
			float17 = (float10 - float1) * float13;
		} else {
			float16 = (float10 - float1) * float13;
			float17 = (float7 - float1) * float13;
		}

		float float18;
		float float19;
		if (float14 >= 0.0F) {
			float18 = (float8 - float2) * float14;
			float19 = (float11 - float2) * float14;
		} else {
			float18 = (float11 - float2) * float14;
			float19 = (float8 - float2) * float14;
		}

		if (!(float16 > float19) && !(float18 > float17)) {
			float float20;
			float float21;
			if (float15 >= 0.0F) {
				float20 = (float9 - float3) * float15;
				float21 = (float12 - float3) * float15;
			} else {
				float20 = (float12 - float3) * float15;
				float21 = (float9 - float3) * float15;
			}

			if (!(float16 > float21) && !(float20 > float17)) {
				float16 = !(float18 > float16) && !Float.isNaN(float16) ? float16 : float18;
				float17 = !(float19 < float17) && !Float.isNaN(float17) ? float17 : float19;
				float16 = float20 > float16 ? float20 : float16;
				float17 = float21 < float17 ? float21 : float17;
				return float16 < float17 && float17 >= 0.0F;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean testRayAab(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4) {
		return testRayAab(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z());
	}

	public static boolean testRayTriangleFront(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		float float17 = float10 - float7;
		float float18 = float11 - float8;
		float float19 = float12 - float9;
		float float20 = float13 - float7;
		float float21 = float14 - float8;
		float float22 = float15 - float9;
		float float23 = float5 * float22 - float6 * float21;
		float float24 = float6 * float20 - float4 * float22;
		float float25 = float4 * float21 - float5 * float20;
		float float26 = float17 * float23 + float18 * float24 + float19 * float25;
		if (float26 < float16) {
			return false;
		} else {
			float float27 = float1 - float7;
			float float28 = float2 - float8;
			float float29 = float3 - float9;
			float float30 = float27 * float23 + float28 * float24 + float29 * float25;
			if (!(float30 < 0.0F) && !(float30 > float26)) {
				float float31 = float28 * float19 - float29 * float18;
				float float32 = float29 * float17 - float27 * float19;
				float float33 = float27 * float18 - float28 * float17;
				float float34 = float4 * float31 + float5 * float32 + float6 * float33;
				if (!(float34 < 0.0F) && !(float30 + float34 > float26)) {
					float float35 = 1.0F / float26;
					float float36 = (float20 * float31 + float21 * float32 + float22 * float33) * float35;
					return float36 >= float16;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public static boolean testRayTriangleFront(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3fc vector3fc5, float float1) {
		return testRayTriangleFront(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3fc5.x(), vector3fc5.y(), vector3fc5.z(), float1);
	}

	public static boolean testRayTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		float float17 = float10 - float7;
		float float18 = float11 - float8;
		float float19 = float12 - float9;
		float float20 = float13 - float7;
		float float21 = float14 - float8;
		float float22 = float15 - float9;
		float float23 = float5 * float22 - float6 * float21;
		float float24 = float6 * float20 - float4 * float22;
		float float25 = float4 * float21 - float5 * float20;
		float float26 = float17 * float23 + float18 * float24 + float19 * float25;
		if (float26 > -float16 && float26 < float16) {
			return false;
		} else {
			float float27 = float1 - float7;
			float float28 = float2 - float8;
			float float29 = float3 - float9;
			float float30 = 1.0F / float26;
			float float31 = (float27 * float23 + float28 * float24 + float29 * float25) * float30;
			if (!(float31 < 0.0F) && !(float31 > 1.0F)) {
				float float32 = float28 * float19 - float29 * float18;
				float float33 = float29 * float17 - float27 * float19;
				float float34 = float27 * float18 - float28 * float17;
				float float35 = (float4 * float32 + float5 * float33 + float6 * float34) * float30;
				if (!(float35 < 0.0F) && !(float31 + float35 > 1.0F)) {
					float float36 = (float20 * float32 + float21 * float33 + float22 * float34) * float30;
					return float36 >= float16;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public static boolean testRayTriangle(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3fc vector3fc5, float float1) {
		return testRayTriangleFront(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3fc5.x(), vector3fc5.y(), vector3fc5.z(), float1);
	}

	public static float intersectRayTriangleFront(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		float float17 = float10 - float7;
		float float18 = float11 - float8;
		float float19 = float12 - float9;
		float float20 = float13 - float7;
		float float21 = float14 - float8;
		float float22 = float15 - float9;
		float float23 = float5 * float22 - float6 * float21;
		float float24 = float6 * float20 - float4 * float22;
		float float25 = float4 * float21 - float5 * float20;
		float float26 = float17 * float23 + float18 * float24 + float19 * float25;
		if (float26 <= float16) {
			return -1.0F;
		} else {
			float float27 = float1 - float7;
			float float28 = float2 - float8;
			float float29 = float3 - float9;
			float float30 = float27 * float23 + float28 * float24 + float29 * float25;
			if (!(float30 < 0.0F) && !(float30 > float26)) {
				float float31 = float28 * float19 - float29 * float18;
				float float32 = float29 * float17 - float27 * float19;
				float float33 = float27 * float18 - float28 * float17;
				float float34 = float4 * float31 + float5 * float32 + float6 * float33;
				if (!(float34 < 0.0F) && !(float30 + float34 > float26)) {
					float float35 = 1.0F / float26;
					float float36 = (float20 * float31 + float21 * float32 + float22 * float33) * float35;
					return float36;
				} else {
					return -1.0F;
				}
			} else {
				return -1.0F;
			}
		}
	}

	public static float intersectRayTriangleFront(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3fc vector3fc5, float float1) {
		return intersectRayTriangleFront(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3fc5.x(), vector3fc5.y(), vector3fc5.z(), float1);
	}

	public static float intersectRayTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		float float17 = float10 - float7;
		float float18 = float11 - float8;
		float float19 = float12 - float9;
		float float20 = float13 - float7;
		float float21 = float14 - float8;
		float float22 = float15 - float9;
		float float23 = float5 * float22 - float6 * float21;
		float float24 = float6 * float20 - float4 * float22;
		float float25 = float4 * float21 - float5 * float20;
		float float26 = float17 * float23 + float18 * float24 + float19 * float25;
		if (float26 > -float16 && float26 < float16) {
			return -1.0F;
		} else {
			float float27 = float1 - float7;
			float float28 = float2 - float8;
			float float29 = float3 - float9;
			float float30 = 1.0F / float26;
			float float31 = (float27 * float23 + float28 * float24 + float29 * float25) * float30;
			if (!(float31 < 0.0F) && !(float31 > 1.0F)) {
				float float32 = float28 * float19 - float29 * float18;
				float float33 = float29 * float17 - float27 * float19;
				float float34 = float27 * float18 - float28 * float17;
				float float35 = (float4 * float32 + float5 * float33 + float6 * float34) * float30;
				if (!(float35 < 0.0F) && !(float31 + float35 > 1.0F)) {
					float float36 = (float20 * float32 + float21 * float33 + float22 * float34) * float30;
					return float36;
				} else {
					return -1.0F;
				}
			} else {
				return -1.0F;
			}
		}
	}

	public static float intersectRayTriangle(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3fc vector3fc5, float float1) {
		return intersectRayTriangle(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3fc5.x(), vector3fc5.y(), vector3fc5.z(), float1);
	}

	public static boolean testLineSegmentTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16) {
		float float17 = float4 - float1;
		float float18 = float5 - float2;
		float float19 = float6 - float3;
		float float20 = intersectRayTriangle(float1, float2, float3, float17, float18, float19, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
		return float20 >= 0.0F && float20 <= 1.0F;
	}

	public static boolean testLineSegmentTriangle(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3fc vector3fc5, float float1) {
		return testLineSegmentTriangle(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3fc5.x(), vector3fc5.y(), vector3fc5.z(), float1);
	}

	public static boolean intersectLineSegmentTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, Vector3f vector3f) {
		float float17 = float4 - float1;
		float float18 = float5 - float2;
		float float19 = float6 - float3;
		float float20 = intersectRayTriangle(float1, float2, float3, float17, float18, float19, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
		if (float20 >= 0.0F && float20 <= 1.0F) {
			vector3f.x = float1 + float17 * float20;
			vector3f.y = float2 + float18 * float20;
			vector3f.z = float3 + float19 * float20;
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectLineSegmentTriangle(Vector3fc vector3fc, Vector3fc vector3fc2, Vector3fc vector3fc3, Vector3fc vector3fc4, Vector3fc vector3fc5, float float1, Vector3f vector3f) {
		return intersectLineSegmentTriangle(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3fc2.x(), vector3fc2.y(), vector3fc2.z(), vector3fc3.x(), vector3fc3.y(), vector3fc3.z(), vector3fc4.x(), vector3fc4.y(), vector3fc4.z(), vector3fc5.x(), vector3fc5.y(), vector3fc5.z(), float1, vector3f);
	}

	public static boolean intersectLineSegmentPlane(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, Vector3f vector3f) {
		float float11 = float4 - float1;
		float float12 = float5 - float2;
		float float13 = float6 - float3;
		float float14 = float7 * float11 + float8 * float12 + float9 * float13;
		float float15 = -(float7 * float1 + float8 * float2 + float9 * float3 + float10) / float14;
		if (float15 >= 0.0F && float15 <= 1.0F) {
			vector3f.x = float1 + float15 * float11;
			vector3f.y = float2 + float15 * float12;
			vector3f.z = float3 + float15 * float13;
			return true;
		} else {
			return false;
		}
	}

	public static boolean testLineCircle(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
		float float8 = (float1 * float4 + float2 * float5 + float3) / float7;
		return -float6 <= float8 && float8 <= float6;
	}

	public static boolean intersectLineCircle(float float1, float float2, float float3, float float4, float float5, float float6, Vector3f vector3f) {
		float float7 = 1.0F / (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
		float float8 = (float1 * float4 + float2 * float5 + float3) * float7;
		if (-float6 <= float8 && float8 <= float6) {
			vector3f.x = float4 + float8 * float1 * float7;
			vector3f.y = float5 + float8 * float2 * float7;
			vector3f.z = (float)Math.sqrt((double)(float6 * float6 - float8 * float8));
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectLineCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, Vector3f vector3f) {
		return intersectLineCircle(float2 - float4, float3 - float1, (float1 - float3) * float2 + (float4 - float2) * float1, float5, float6, float7, vector3f);
	}

	public static boolean testAarLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8;
		float float9;
		if (float5 > 0.0F) {
			float8 = float3;
			float9 = float1;
		} else {
			float8 = float1;
			float9 = float3;
		}

		float float10;
		float float11;
		if (float6 > 0.0F) {
			float10 = float4;
			float11 = float2;
		} else {
			float10 = float2;
			float11 = float4;
		}

		float float12 = float7 + float5 * float9 + float6 * float11;
		float float13 = float7 + float5 * float8 + float6 * float10;
		return float12 <= 0.0F && float13 >= 0.0F;
	}

	public static boolean testAarLine(Vector2fc vector2fc, Vector2fc vector2fc2, float float1, float float2, float float3) {
		return testAarLine(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), float1, float2, float3);
	}

	public static boolean testAarLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = float6 - float8;
		float float10 = float7 - float5;
		float float11 = -float10 * float6 - float9 * float5;
		return testAarLine(float1, float2, float3, float4, float9, float10, float11);
	}

	public static boolean testAarAar(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		return float3 >= float5 && float4 >= float6 && float1 <= float7 && float2 <= float8;
	}

	public static boolean testAarAar(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4) {
		return testAarAar(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y());
	}

	public static boolean intersectCircleCircle(float float1, float float2, float float3, float float4, float float5, float float6, Vector3f vector3f) {
		float float7 = float4 - float1;
		float float8 = float5 - float2;
		float float9 = float7 * float7 + float8 * float8;
		float float10 = 0.5F + (float3 - float6) / float9;
		float float11 = (float)Math.sqrt((double)(float3 - float10 * float10 * float9));
		if (float11 >= 0.0F) {
			vector3f.x = float1 + float10 * float7;
			vector3f.y = float2 + float10 * float8;
			vector3f.z = float11;
			return true;
		} else {
			return false;
		}
	}

	public static boolean intersectCircleCircle(Vector2fc vector2fc, float float1, Vector2fc vector2fc2, float float2, Vector3f vector3f) {
		return intersectCircleCircle(vector2fc.x(), vector2fc.y(), float1, vector2fc2.x(), vector2fc2.y(), float2, vector3f);
	}

	public static boolean testCircleCircle(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = (float1 - float4) * (float1 - float4) + (float2 - float5) * (float2 - float5);
		return float7 <= (float3 + float6) * (float3 + float6);
	}

	public static boolean testCircleCircle(Vector2fc vector2fc, float float1, Vector2fc vector2fc2, float float2) {
		return testCircleCircle(vector2fc.x(), vector2fc.y(), float1, vector2fc2.x(), vector2fc2.y(), float2);
	}

	public static float distancePointLine(float float1, float float2, float float3, float float4, float float5) {
		float float6 = (float)Math.sqrt((double)(float3 * float3 + float4 * float4));
		return (float3 * float1 + float4 * float2 + float5) / float6;
	}

	public static float distancePointLine(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = float5 - float3;
		float float8 = float6 - float4;
		float float9 = (float)Math.sqrt((double)(float7 * float7 + float8 * float8));
		return (float7 * (float4 - float2) - (float3 - float1) * float8) / float9;
	}

	public static float intersectRayLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float7 * float3 + float8 * float4;
		if (float10 < float9) {
			float float11 = ((float5 - float1) * float7 + (float6 - float2) * float8) / float10;
			if (float11 >= 0.0F) {
				return float11;
			}
		}

		return -1.0F;
	}

	public static float intersectRayLine(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4, float float1) {
		return intersectRayLine(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y(), float1);
	}

	public static float intersectRayLineSegment(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = float1 - float5;
		float float10 = float2 - float6;
		float float11 = float7 - float5;
		float float12 = float8 - float6;
		float float13 = 1.0F / (float12 * float3 - float11 * float4);
		float float14 = (float11 * float10 - float12 * float9) * float13;
		float float15 = (float10 * float3 - float9 * float4) * float13;
		return float14 >= 0.0F && float15 >= 0.0F && float15 <= 1.0F ? float14 : -1.0F;
	}

	public static float intersectRayLineSegment(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4) {
		return intersectRayLineSegment(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y());
	}

	public static boolean testAarCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = float7;
		float float9;
		if (float5 < float1) {
			float9 = float5 - float1;
			float8 = float7 - float9 * float9;
		} else if (float5 > float3) {
			float9 = float5 - float3;
			float8 = float7 - float9 * float9;
		}

		if (float6 < float2) {
			float9 = float6 - float2;
			float8 -= float9 * float9;
		} else if (float6 > float4) {
			float9 = float6 - float4;
			float8 -= float9 * float9;
		}

		return float8 >= 0.0F;
	}

	public static boolean testAarCircle(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, float float1) {
		return testAarCircle(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), float1);
	}

	public static int findClosestPointOnTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector2f vector2f) {
		float float9 = float1 - float7;
		float float10 = float2 - float8;
		float float11 = float3 - float7;
		float float12 = float4 - float8;
		float float13 = float5 - float7;
		float float14 = float6 - float8;
		float float15 = float11 - float9;
		float float16 = float12 - float10;
		float float17 = float13 - float9;
		float float18 = float14 - float10;
		float float19 = -(float15 * float9 + float16 * float10);
		float float20 = -(float17 * float9 + float18 * float10);
		if (float19 <= 0.0F && float20 <= 0.0F) {
			vector2f.set(float1, float2);
			return 0;
		} else {
			float float21 = -(float15 * float11 + float16 * float12);
			float float22 = -(float17 * float11 + float18 * float12);
			if (float21 >= 0.0F && float22 <= float21) {
				vector2f.set(float3, float4);
				return 0;
			} else {
				float float23 = float19 * float22 - float21 * float20;
				float float24;
				if (float23 <= 0.0F && float19 >= 0.0F && float21 <= 0.0F) {
					float24 = float19 / (float19 - float21);
					vector2f.set(float1 + float15 * float24, float2 + float16 * float24);
					return 1;
				} else {
					float24 = -(float15 * float13 + float16 * float14);
					float float25 = -(float17 * float13 + float18 * float14);
					if (float25 >= 0.0F && float24 <= float25) {
						vector2f.set(float5, float6);
						return 0;
					} else {
						float float26 = float24 * float20 - float19 * float25;
						float float27;
						if (float26 <= 0.0F && float20 >= 0.0F && float25 <= 0.0F) {
							float27 = float20 / (float20 - float25);
							vector2f.set(float1 + float17 * float27, float2 + float18 * float27);
							return 1;
						} else {
							float27 = float21 * float25 - float24 * float22;
							float float28;
							if (float27 <= 0.0F && float22 - float21 >= 0.0F && float24 - float25 >= 0.0F) {
								float28 = (float22 - float21) / (float22 - float21 + float24 - float25);
								vector2f.set(float3 + (float13 - float11) * float28, float4 + (float14 - float12) * float28);
								return 1;
							} else {
								float28 = 1.0F / (float27 + float26 + float23);
								float float29 = float26 * float28;
								float float30 = float23 * float28;
								vector2f.set(float1 + float15 * float29 + float17 * float30, float2 + float16 * float29 + float18 * float30);
								return 2;
							}
						}
					}
				}
			}
		}
	}

	public static int findClosestPointOnTriangle(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4, Vector2f vector2f) {
		return findClosestPointOnTriangle(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y(), vector2f);
	}

	public static boolean intersectRayCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, Vector2f vector2f) {
		float float8 = float5 - float1;
		float float9 = float6 - float2;
		float float10 = float8 * float3 + float9 * float4;
		float float11 = float8 * float8 + float9 * float9 - float10 * float10;
		if (float11 > float7) {
			return false;
		} else {
			float float12 = (float)Math.sqrt((double)(float7 - float11));
			float float13 = float10 - float12;
			float float14 = float10 + float12;
			if (float13 < float14 && float14 >= 0.0F) {
				vector2f.x = float13;
				vector2f.y = float14;
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean intersectRayCircle(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, float float1, Vector2f vector2f) {
		return intersectRayCircle(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), float1, vector2f);
	}

	public static boolean testRayCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = float5 - float1;
		float float9 = float6 - float2;
		float float10 = float8 * float3 + float9 * float4;
		float float11 = float8 * float8 + float9 * float9 - float10 * float10;
		if (float11 > float7) {
			return false;
		} else {
			float float12 = (float)Math.sqrt((double)(float7 - float11));
			float float13 = float10 - float12;
			float float14 = float10 + float12;
			return float13 < float14 && float14 >= 0.0F;
		}
	}

	public static boolean testRayCircle(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, float float1) {
		return testRayCircle(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), float1);
	}

	public static int intersectRayAar(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector2f vector2f) {
		float float9 = 1.0F / float3;
		float float10 = 1.0F / float4;
		float float11;
		float float12;
		if (float9 >= 0.0F) {
			float11 = (float5 - float1) * float9;
			float12 = (float7 - float1) * float9;
		} else {
			float11 = (float7 - float1) * float9;
			float12 = (float5 - float1) * float9;
		}

		float float13;
		float float14;
		if (float10 >= 0.0F) {
			float13 = (float6 - float2) * float10;
			float14 = (float8 - float2) * float10;
		} else {
			float13 = (float8 - float2) * float10;
			float14 = (float6 - float2) * float10;
		}

		if (!(float11 > float14) && !(float13 > float12)) {
			float11 = !(float13 > float11) && !Float.isNaN(float11) ? float11 : float13;
			float12 = !(float14 < float12) && !Float.isNaN(float12) ? float12 : float14;
			byte byte1 = -1;
			if (float11 < float12 && float12 >= 0.0F) {
				float float15 = float1 + float11 * float3;
				float float16 = float2 + float11 * float4;
				vector2f.x = float11;
				vector2f.y = float12;
				float float17 = Math.abs(float15 - float5);
				float float18 = Math.abs(float16 - float6);
				float float19 = Math.abs(float15 - float7);
				float float20 = Math.abs(float16 - float8);
				byte1 = 0;
				float float21 = float17;
				if (float18 < float17) {
					float21 = float18;
					byte1 = 1;
				}

				if (float19 < float21) {
					float21 = float19;
					byte1 = 2;
				}

				if (float20 < float21) {
					byte1 = 3;
				}
			}

			return byte1;
		} else {
			return -1;
		}
	}

	public static int intersectRayAar(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4, Vector2f vector2f) {
		return intersectRayAar(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y(), vector2f);
	}

	public static int intersectLineSegmentAar(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector2f vector2f) {
		float float9 = float3 - float1;
		float float10 = float4 - float2;
		float float11 = 1.0F / float9;
		float float12 = 1.0F / float10;
		float float13;
		float float14;
		if (float11 >= 0.0F) {
			float13 = (float5 - float1) * float11;
			float14 = (float7 - float1) * float11;
		} else {
			float13 = (float7 - float1) * float11;
			float14 = (float5 - float1) * float11;
		}

		float float15;
		float float16;
		if (float12 >= 0.0F) {
			float15 = (float6 - float2) * float12;
			float16 = (float8 - float2) * float12;
		} else {
			float15 = (float8 - float2) * float12;
			float16 = (float6 - float2) * float12;
		}

		if (!(float13 > float16) && !(float15 > float14)) {
			float13 = !(float15 > float13) && !Float.isNaN(float13) ? float13 : float15;
			float14 = !(float16 < float14) && !Float.isNaN(float14) ? float14 : float16;
			byte byte1 = -1;
			if (float13 < float14 && float13 <= 1.0F && float14 >= 0.0F) {
				if (float13 > 0.0F && float14 > 1.0F) {
					float14 = float13;
					byte1 = 1;
				} else if (float13 < 0.0F && float14 < 1.0F) {
					float13 = float14;
					byte1 = 1;
				} else if (float13 < 0.0F && float14 > 1.0F) {
					byte1 = 3;
				} else {
					byte1 = 2;
				}

				vector2f.x = float13;
				vector2f.y = float14;
			}

			return byte1;
		} else {
			return -1;
		}
	}

	public static int intersectLineSegmentAar(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4, Vector2f vector2f) {
		return intersectLineSegmentAar(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y(), vector2f);
	}

	public static boolean testRayAar(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = 1.0F / float3;
		float float10 = 1.0F / float4;
		float float11;
		float float12;
		if (float9 >= 0.0F) {
			float11 = (float5 - float1) * float9;
			float12 = (float7 - float1) * float9;
		} else {
			float11 = (float7 - float1) * float9;
			float12 = (float5 - float1) * float9;
		}

		float float13;
		float float14;
		if (float10 >= 0.0F) {
			float13 = (float6 - float2) * float10;
			float14 = (float8 - float2) * float10;
		} else {
			float13 = (float8 - float2) * float10;
			float14 = (float6 - float2) * float10;
		}

		if (!(float11 > float14) && !(float13 > float12)) {
			float11 = !(float13 > float11) && !Float.isNaN(float11) ? float11 : float13;
			float12 = !(float14 < float12) && !Float.isNaN(float12) ? float12 : float14;
			return float11 < float12 && float12 >= 0.0F;
		} else {
			return false;
		}
	}

	public static boolean testRayAar(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4) {
		return testRayAar(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y());
	}

	public static boolean testPointTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		boolean boolean1 = (float1 - float5) * (float4 - float6) - (float3 - float5) * (float2 - float6) < 0.0F;
		boolean boolean2 = (float1 - float7) * (float6 - float8) - (float5 - float7) * (float2 - float8) < 0.0F;
		if (boolean1 != boolean2) {
			return false;
		} else {
			boolean boolean3 = (float1 - float3) * (float8 - float4) - (float7 - float3) * (float2 - float4) < 0.0F;
			return boolean2 == boolean3;
		}
	}

	public static boolean testPointTriangle(Vector2fc vector2fc, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4) {
		return testPointTriangle(vector2fc.x(), vector2fc.y(), vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y());
	}

	public static boolean testPointAar(float float1, float float2, float float3, float float4, float float5, float float6) {
		return float1 >= float3 && float2 >= float4 && float1 <= float5 && float2 <= float6;
	}

	public static boolean testPointCircle(float float1, float float2, float float3, float float4, float float5) {
		float float6 = float1 - float3;
		float float7 = float2 - float4;
		float float8 = float6 * float6;
		float float9 = float7 * float7;
		return float8 + float9 <= float5;
	}

	public static boolean testCircleTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float float10 = float1 - float4;
		float float11 = float2 - float5;
		float float12 = float10 * float10 + float11 * float11 - float3;
		if (float12 <= 0.0F) {
			return true;
		} else {
			float float13 = float1 - float6;
			float float14 = float2 - float7;
			float float15 = float13 * float13 + float14 * float14 - float3;
			if (float15 <= 0.0F) {
				return true;
			} else {
				float float16 = float1 - float8;
				float float17 = float2 - float9;
				float float18 = float16 * float16 + float17 * float17 - float3;
				if (float18 <= 0.0F) {
					return true;
				} else {
					float float19 = float6 - float4;
					float float20 = float7 - float5;
					float float21 = float8 - float6;
					float float22 = float9 - float7;
					float float23 = float4 - float8;
					float float24 = float5 - float9;
					if (float19 * float11 - float20 * float10 >= 0.0F && float21 * float14 - float22 * float13 >= 0.0F && float23 * float17 - float24 * float16 >= 0.0F) {
						return true;
					} else {
						float float25 = float10 * float19 + float11 * float20;
						float float26;
						if (float25 >= 0.0F) {
							float26 = float19 * float19 + float20 * float20;
							if (float25 <= float26 && float12 * float26 <= float25 * float25) {
								return true;
							}
						}

						float25 = float13 * float21 + float14 * float22;
						if (float25 > 0.0F) {
							float26 = float21 * float21 + float22 * float22;
							if (float25 <= float26 && float15 * float26 <= float25 * float25) {
								return true;
							}
						}

						float25 = float16 * float23 + float17 * float24;
						if (float25 >= 0.0F) {
							float26 = float23 * float23 + float24 * float24;
							if (float25 < float26 && float18 * float26 <= float25 * float25) {
								return true;
							}
						}

						return false;
					}
				}
			}
		}
	}

	public static boolean testCircleTriangle(Vector2fc vector2fc, float float1, Vector2fc vector2fc2, Vector2fc vector2fc3, Vector2fc vector2fc4) {
		return testCircleTriangle(vector2fc.x(), vector2fc.y(), float1, vector2fc2.x(), vector2fc2.y(), vector2fc3.x(), vector2fc3.y(), vector2fc4.x(), vector2fc4.y());
	}

	public static int intersectPolygonRay(float[] floatArray, float float1, float float2, float float3, float float4, Vector2f vector2f) {
		float float5 = Float.MAX_VALUE;
		int int1 = floatArray.length >> 1;
		int int2 = -1;
		float float6 = floatArray[int1 - 1 << 1];
		float float7 = floatArray[(int1 - 1 << 1) + 1];
		for (int int3 = 0; int3 < int1; ++int3) {
			float float8 = floatArray[int3 << 1];
			float float9 = floatArray[(int3 << 1) + 1];
			float float10 = float1 - float6;
			float float11 = float2 - float7;
			float float12 = float8 - float6;
			float float13 = float9 - float7;
			float float14 = 1.0F / (float13 * float3 - float12 * float4);
			float float15 = (float12 * float11 - float13 * float10) * float14;
			if (float15 >= 0.0F && float15 < float5) {
				float float16 = (float11 * float3 - float10 * float4) * float14;
				if (float16 >= 0.0F && float16 <= 1.0F) {
					int2 = (int3 - 1 + int1) % int1;
					float5 = float15;
					vector2f.x = float1 + float15 * float3;
					vector2f.y = float2 + float15 * float4;
				}
			}

			float6 = float8;
			float7 = float9;
		}

		return int2;
	}

	public static int intersectPolygonRay(Vector2fc[] vector2fcArray, float float1, float float2, float float3, float float4, Vector2f vector2f) {
		float float5 = Float.MAX_VALUE;
		int int1 = vector2fcArray.length;
		int int2 = -1;
		float float6 = vector2fcArray[int1 - 1].x();
		float float7 = vector2fcArray[int1 - 1].y();
		for (int int3 = 0; int3 < int1; ++int3) {
			Vector2fc vector2fc = vector2fcArray[int3];
			float float8 = vector2fc.x();
			float float9 = vector2fc.y();
			float float10 = float1 - float6;
			float float11 = float2 - float7;
			float float12 = float8 - float6;
			float float13 = float9 - float7;
			float float14 = 1.0F / (float13 * float3 - float12 * float4);
			float float15 = (float12 * float11 - float13 * float10) * float14;
			if (float15 >= 0.0F && float15 < float5) {
				float float16 = (float11 * float3 - float10 * float4) * float14;
				if (float16 >= 0.0F && float16 <= 1.0F) {
					int2 = (int3 - 1 + int1) % int1;
					float5 = float15;
					vector2f.x = float1 + float15 * float3;
					vector2f.y = float2 + float15 * float4;
				}
			}

			float6 = float8;
			float7 = float9;
		}

		return int2;
	}

	public static boolean intersectLineLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, Vector2f vector2f) {
		float float9 = float1 - float3;
		float float10 = float4 - float2;
		float float11 = float10 * float1 + float9 * float2;
		float float12 = float5 - float7;
		float float13 = float8 - float6;
		float float14 = float13 * float5 + float12 * float6;
		float float15 = float10 * float12 - float13 * float9;
		if (float15 == 0.0F) {
			return false;
		} else {
			vector2f.x = (float12 * float11 - float9 * float14) / float15;
			vector2f.y = (float10 * float14 - float13 * float11) / float15;
			return true;
		}
	}
}
