package zombie.worldMap;

import java.util.ArrayList;


public class StrokeGeometry {
	static StrokeGeometry.Point s_firstPoint = null;
	static StrokeGeometry.Point s_lastPoint = null;
	static final double EPSILON = 1.0E-4;

	static StrokeGeometry.Point newPoint(double double1, double double2) {
		if (s_firstPoint == null) {
			return new StrokeGeometry.Point(double1, double2);
		} else {
			StrokeGeometry.Point point = s_firstPoint;
			s_firstPoint = s_firstPoint.next;
			if (s_lastPoint == point) {
				s_lastPoint = null;
			}

			point.next = null;
			return point.set(double1, double2);
		}
	}

	static void release(StrokeGeometry.Point point) {
		if (point.next == null && point != s_lastPoint) {
			point.next = s_firstPoint;
			s_firstPoint = point;
			if (s_lastPoint == null) {
				s_lastPoint = point;
			}
		}
	}

	static void release(ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			release((StrokeGeometry.Point)arrayList.get(int1));
		}
	}

	static ArrayList getStrokeGeometry(StrokeGeometry.Point[] pointArray, StrokeGeometry.Attrs attrs) {
		if (pointArray.length < 2) {
			return null;
		} else {
			String string = attrs.cap;
			String string2 = attrs.join;
			float float1 = attrs.width / 2.0F;
			float float2 = attrs.miterLimit;
			ArrayList arrayList = new ArrayList();
			ArrayList arrayList2 = new ArrayList();
			boolean boolean1 = false;
			if (pointArray.length == 2) {
				string2 = "bevel";
				createTriangles(pointArray[0], StrokeGeometry.Point.Middle(pointArray[0], pointArray[1]), pointArray[1], arrayList, float1, string2, float2);
			} else {
				int int1;
				for (int1 = 0; int1 < pointArray.length - 1; ++int1) {
					if (int1 == 0) {
						arrayList2.add(pointArray[0]);
					} else if (int1 == pointArray.length - 2) {
						arrayList2.add(pointArray[pointArray.length - 1]);
					} else {
						arrayList2.add(StrokeGeometry.Point.Middle(pointArray[int1], pointArray[int1 + 1]));
					}
				}

				for (int1 = 1; int1 < arrayList2.size(); ++int1) {
					createTriangles((StrokeGeometry.Point)arrayList2.get(int1 - 1), pointArray[int1], (StrokeGeometry.Point)arrayList2.get(int1), arrayList, float1, string2, float2);
				}
			}

			if (!boolean1) {
				StrokeGeometry.Point point;
				StrokeGeometry.Point point2;
				if (string.equals("round")) {
					point2 = (StrokeGeometry.Point)arrayList.get(0);
					point = (StrokeGeometry.Point)arrayList.get(1);
					StrokeGeometry.Point point3 = pointArray[1];
					StrokeGeometry.Point point4 = (StrokeGeometry.Point)arrayList.get(arrayList.size() - 1);
					StrokeGeometry.Point point5 = (StrokeGeometry.Point)arrayList.get(arrayList.size() - 3);
					StrokeGeometry.Point point6 = pointArray[pointArray.length - 2];
					createRoundCap(pointArray[0], point2, point, point3, arrayList);
					createRoundCap(pointArray[pointArray.length - 1], point4, point5, point6, arrayList);
				} else if (string.equals("square")) {
					point2 = (StrokeGeometry.Point)arrayList.get(arrayList.size() - 1);
					point = (StrokeGeometry.Point)arrayList.get(arrayList.size() - 3);
					createSquareCap((StrokeGeometry.Point)arrayList.get(0), (StrokeGeometry.Point)arrayList.get(1), StrokeGeometry.Point.Sub(pointArray[0], pointArray[1]).normalize().scalarMult(StrokeGeometry.Point.Sub(pointArray[0], (StrokeGeometry.Point)arrayList.get(0)).length()), arrayList);
					createSquareCap(point2, point, StrokeGeometry.Point.Sub(pointArray[pointArray.length - 1], pointArray[pointArray.length - 2]).normalize().scalarMult(StrokeGeometry.Point.Sub(point, pointArray[pointArray.length - 1]).length()), arrayList);
				}
			}

			return arrayList;
		}
	}

	static void createSquareCap(StrokeGeometry.Point point, StrokeGeometry.Point point2, StrokeGeometry.Point point3, ArrayList arrayList) {
		arrayList.add(point);
		arrayList.add(StrokeGeometry.Point.Add(point, point3));
		arrayList.add(StrokeGeometry.Point.Add(point2, point3));
		arrayList.add(point2);
		arrayList.add(StrokeGeometry.Point.Add(point2, point3));
		arrayList.add(point);
	}

	static void createRoundCap(StrokeGeometry.Point point, StrokeGeometry.Point point2, StrokeGeometry.Point point3, StrokeGeometry.Point point4, ArrayList arrayList) {
		double double1 = StrokeGeometry.Point.Sub(point, point2).length();
		double double2 = Math.atan2(point3.y - point.y, point3.x - point.x);
		double double3 = Math.atan2(point2.y - point.y, point2.x - point.x);
		double double4 = double2;
		if (double3 > double2) {
			if (double3 - double2 >= 3.141492653589793) {
				double3 -= 6.283185307179586;
			}
		} else if (double2 - double3 >= 3.141492653589793) {
			double2 -= 6.283185307179586;
		}

		double double5 = double3 - double2;
		if (Math.abs(double5) >= 3.141492653589793 && Math.abs(double5) <= 3.1416926535897933) {
			StrokeGeometry.Point point5 = StrokeGeometry.Point.Sub(point, point4);
			if (point5.x == 0.0) {
				if (point5.y > 0.0) {
					double5 = -double5;
				}
			} else if (point5.x >= -1.0E-4) {
				double5 = -double5;
			}
		}

		int int1 = (int)(Math.abs(double5 * double1) / 7.0);
		++int1;
		double double6 = double5 / (double)int1;
		for (int int2 = 0; int2 < int1; ++int2) {
			arrayList.add(newPoint(point.x, point.y));
			arrayList.add(newPoint(point.x + double1 * Math.cos(double4 + double6 * (double)int2), point.y + double1 * Math.sin(double4 + double6 * (double)int2)));
			arrayList.add(newPoint(point.x + double1 * Math.cos(double4 + double6 * (double)(1 + int2)), point.y + double1 * Math.sin(double4 + double6 * (double)(1 + int2))));
		}
	}

	static double signedArea(StrokeGeometry.Point point, StrokeGeometry.Point point2, StrokeGeometry.Point point3) {
		return (point2.x - point.x) * (point3.y - point.y) - (point3.x - point.x) * (point2.y - point.y);
	}

	static StrokeGeometry.Point lineIntersection(StrokeGeometry.Point point, StrokeGeometry.Point point2, StrokeGeometry.Point point3, StrokeGeometry.Point point4) {
		double double1 = point2.y - point.y;
		double double2 = point.x - point2.x;
		double double3 = point4.y - point3.y;
		double double4 = point3.x - point4.x;
		double double5 = double1 * double4 - double3 * double2;
		if (double5 > -1.0E-4 && double5 < 1.0E-4) {
			return null;
		} else {
			double double6 = double1 * point.x + double2 * point.y;
			double double7 = double3 * point3.x + double4 * point3.y;
			double double8 = (double4 * double6 - double2 * double7) / double5;
			double double9 = (double1 * double7 - double3 * double6) / double5;
			return newPoint(double8, double9);
		}
	}

	static void createTriangles(StrokeGeometry.Point point, StrokeGeometry.Point point2, StrokeGeometry.Point point3, ArrayList arrayList, float float1, String string, float float2) {
		StrokeGeometry.Point point4 = StrokeGeometry.Point.Sub(point2, point);
		StrokeGeometry.Point point5 = StrokeGeometry.Point.Sub(point3, point2);
		point4.perpendicular();
		point5.perpendicular();
		if (signedArea(point, point2, point3) > 0.0) {
			point4.invert();
			point5.invert();
		}

		point4.normalize();
		point5.normalize();
		point4.scalarMult((double)float1);
		point5.scalarMult((double)float1);
		StrokeGeometry.Point point6 = lineIntersection(StrokeGeometry.Point.Add(point4, point), StrokeGeometry.Point.Add(point4, point2), StrokeGeometry.Point.Add(point5, point3), StrokeGeometry.Point.Add(point5, point2));
		StrokeGeometry.Point point7 = null;
		double double1 = Double.MAX_VALUE;
		if (point6 != null) {
			point7 = StrokeGeometry.Point.Sub(point6, point2);
			double1 = point7.length();
		}

		double double2 = (double)((int)(double1 / (double)float1));
		StrokeGeometry.Point point8 = StrokeGeometry.Point.Sub(point, point2);
		double double3 = point8.length();
		StrokeGeometry.Point point9 = StrokeGeometry.Point.Sub(point2, point3);
		double double4 = point9.length();
		if (!(double1 > double3) && !(double1 > double4)) {
			arrayList.add(StrokeGeometry.Point.Add(point, point4));
			arrayList.add(StrokeGeometry.Point.Sub(point, point4));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point7));
			arrayList.add(StrokeGeometry.Point.Add(point, point4));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point7));
			arrayList.add(StrokeGeometry.Point.Add(point2, point4));
			if (string.equals("round")) {
				StrokeGeometry.Point point10 = StrokeGeometry.Point.Add(point2, point4);
				StrokeGeometry.Point point11 = StrokeGeometry.Point.Add(point2, point5);
				StrokeGeometry.Point point12 = StrokeGeometry.Point.Sub(point2, point7);
				arrayList.add(point10);
				arrayList.add(point2);
				arrayList.add(point12);
				createRoundCap(point2, point10, point11, point12, arrayList);
				arrayList.add(point2);
				arrayList.add(point11);
				arrayList.add(point12);
			} else {
				if (string.equals("bevel") || string.equals("miter") && double2 >= (double)float2) {
					arrayList.add(StrokeGeometry.Point.Add(point2, point4));
					arrayList.add(StrokeGeometry.Point.Add(point2, point5));
					arrayList.add(StrokeGeometry.Point.Sub(point2, point7));
				}

				if (string.equals("miter") && double2 < (double)float2) {
					arrayList.add(point6);
					arrayList.add(StrokeGeometry.Point.Add(point2, point4));
					arrayList.add(StrokeGeometry.Point.Add(point2, point5));
				}
			}

			arrayList.add(StrokeGeometry.Point.Add(point3, point5));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point7));
			arrayList.add(StrokeGeometry.Point.Add(point2, point5));
			arrayList.add(StrokeGeometry.Point.Add(point3, point5));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point7));
			arrayList.add(StrokeGeometry.Point.Sub(point3, point5));
		} else {
			arrayList.add(StrokeGeometry.Point.Add(point, point4));
			arrayList.add(StrokeGeometry.Point.Sub(point, point4));
			arrayList.add(StrokeGeometry.Point.Add(point2, point4));
			arrayList.add(StrokeGeometry.Point.Sub(point, point4));
			arrayList.add(StrokeGeometry.Point.Add(point2, point4));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point4));
			if (string.equals("round")) {
				createRoundCap(point2, StrokeGeometry.Point.Add(point2, point4), StrokeGeometry.Point.Add(point2, point5), point3, arrayList);
			} else if (string.equals("bevel") || string.equals("miter") && double2 >= (double)float2) {
				arrayList.add(point2);
				arrayList.add(StrokeGeometry.Point.Add(point2, point4));
				arrayList.add(StrokeGeometry.Point.Add(point2, point5));
			} else if (string.equals("miter") && double2 < (double)float2 && point6 != null) {
				arrayList.add(StrokeGeometry.Point.Add(point2, point4));
				arrayList.add(point2);
				arrayList.add(point6);
				arrayList.add(StrokeGeometry.Point.Add(point2, point5));
				arrayList.add(point2);
				arrayList.add(point6);
			}

			arrayList.add(StrokeGeometry.Point.Add(point3, point5));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point5));
			arrayList.add(StrokeGeometry.Point.Add(point2, point5));
			arrayList.add(StrokeGeometry.Point.Add(point3, point5));
			arrayList.add(StrokeGeometry.Point.Sub(point2, point5));
			arrayList.add(StrokeGeometry.Point.Sub(point3, point5));
		}
	}

	public static final class Point {
		double x;
		double y;
		StrokeGeometry.Point next;

		Point() {
			this.x = 0.0;
			this.y = 0.0;
		}

		Point(double double1, double double2) {
			this.x = double1;
			this.y = double2;
		}

		StrokeGeometry.Point set(double double1, double double2) {
			this.x = double1;
			this.y = double2;
			return this;
		}

		StrokeGeometry.Point scalarMult(double double1) {
			this.x *= double1;
			this.y *= double1;
			return this;
		}

		StrokeGeometry.Point perpendicular() {
			double double1 = this.x;
			this.x = -this.y;
			this.y = double1;
			return this;
		}

		StrokeGeometry.Point invert() {
			this.x = -this.x;
			this.y = -this.y;
			return this;
		}

		double length() {
			return Math.sqrt(this.x * this.x + this.y * this.y);
		}

		StrokeGeometry.Point normalize() {
			double double1 = this.length();
			this.x /= double1;
			this.y /= double1;
			return this;
		}

		double angle() {
			return this.y / this.x;
		}

		static double Angle(StrokeGeometry.Point point, StrokeGeometry.Point point2) {
			return Math.atan2(point2.x - point.x, point2.y - point.y);
		}

		static StrokeGeometry.Point Add(StrokeGeometry.Point point, StrokeGeometry.Point point2) {
			return StrokeGeometry.newPoint(point.x + point2.x, point.y + point2.y);
		}

		static StrokeGeometry.Point Sub(StrokeGeometry.Point point, StrokeGeometry.Point point2) {
			return StrokeGeometry.newPoint(point.x - point2.x, point.y - point2.y);
		}

		static StrokeGeometry.Point Middle(StrokeGeometry.Point point, StrokeGeometry.Point point2) {
			return Add(point, point2).scalarMult(0.5);
		}
	}

	static class Attrs {
		String cap = "butt";
		String join = "bevel";
		float width = 1.0F;
		float miterLimit = 10.0F;
	}
}
