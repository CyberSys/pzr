package zombie.vehicles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector3f;
import org.lwjgl.util.vector.Vector2f;
import zombie.characters.IsoPlayer;
import zombie.core.physics.CarController;
import zombie.core.physics.WorldSimulation;
import zombie.debug.LineDrawer;


public class CircleLineIntersect {

	public static CircleLineIntersect.Collideresult checkforcecirclescollidetime(List list, ArrayList arrayList, double[] doubleArray, boolean[] booleanArray, boolean boolean1) {
		CircleLineIntersect.PointVector[] pointVectorArray = new CircleLineIntersect.PointVector[list.size()];
		double[] doubleArray2 = new double[list.size()];
		CircleLineIntersect.Collideclassindex[] collideclassindexArray = new CircleLineIntersect.Collideclassindex[list.size()];
		double[] doubleArray3 = new double[list.size()];
		for (int int1 = list.size() - 1; int1 >= 0; --int1) {
			doubleArray2[int1] = -1.0;
			collideclassindexArray[int1] = new CircleLineIntersect.Collideclassindex();
			pointVectorArray[int1] = (CircleLineIntersect.PointVector)list.get(int1);
			doubleArray3[int1] = 1.0;
		}

		CircleLineIntersect.ForceCircle forceCircle;
		int int2;
		double double1;
		for (int int3 = Math.min(list.size(), doubleArray.length) - 1; int3 >= 0; --int3) {
			if (boolean1 || booleanArray[int3]) {
				forceCircle = (CircleLineIntersect.ForceCircle)list.get(int3);
				for (int2 = arrayList.size() - 1; int2 >= 0; --int2) {
					CircleLineIntersect.StaticLine staticLine = (CircleLineIntersect.StaticLine)arrayList.get(int2);
					CircleLineIntersect.Point point = CircleLineIntersect.VectorMath.closestpointonline(staticLine.getX1(), staticLine.getY1(), staticLine.getX2(), staticLine.getY2(), forceCircle.getX(), forceCircle.getY());
					double double2 = CircleLineIntersect.Point.distanceSq(point.getX(), point.getY(), forceCircle.getX(), forceCircle.getY());
					double double3;
					double double4;
					double double5;
					double double6;
					if (double2 < forceCircle.getRadiusSq()) {
						double3 = 0.0;
						double1 = 0.0;
						if (double2 == 0.0) {
							CircleLineIntersect.Point point2 = CircleLineIntersect.Point.midpoint(staticLine.getP1(), staticLine.getP2());
							double double7 = staticLine.getP1().distance(staticLine.getP2());
							double double8 = forceCircle.distanceSq(point2);
							if (double8 < Math.pow(forceCircle.getRadius() + double7 / 2.0, 2.0)) {
								if (double8 != 0.0) {
									double double9 = forceCircle.distance(point2);
									double double10 = (forceCircle.getX() - point2.getX()) / double9;
									double double11 = (forceCircle.getY() - point2.getY()) / double9;
									double3 = point2.getX() + (forceCircle.getRadius() + double7 / 2.0) * double10;
									double1 = point2.getY() + (forceCircle.getRadius() + double7 / 2.0) * double11;
								} else {
									double3 = forceCircle.getX();
									double1 = forceCircle.getY();
								}

								if (doubleArray2[int3] == -1.0) {
									pointVectorArray[int3] = new CircleLineIntersect.PointVector(double3, double1);
								} else {
									pointVectorArray[int3].setPoint(double3, double1);
								}

								if (doubleArray2[int3] == 0.0) {
									collideclassindexArray[int3].addCollided(staticLine, int2, forceCircle.getVector());
								} else {
									collideclassindexArray[int3].setCollided(staticLine, int2, forceCircle.getVector());
								}

								doubleArray2[int3] = 0.0;
								continue;
							}

							if (double8 == Math.pow(forceCircle.getRadius() + double7 / 2.0, 2.0) && forceCircle.getLength() == 0.0) {
								continue;
							}
						} else {
							if (Math.min(staticLine.getX1(), staticLine.getX2()) <= point.getX() && point.getX() <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= point.getY() && point.getY() <= Math.max(staticLine.getY1(), staticLine.getY2())) {
								double6 = Math.sqrt(double2);
								double4 = (forceCircle.getX() - point.getX()) / double6;
								double5 = (forceCircle.getY() - point.getY()) / double6;
								double3 = point.getX() + forceCircle.getRadius() * double4;
								double1 = point.getY() + forceCircle.getRadius() * double5;
								if (doubleArray2[int3] == -1.0) {
									pointVectorArray[int3] = new CircleLineIntersect.PointVector(double3, double1);
								} else {
									pointVectorArray[int3].setPoint(double3, double1);
								}

								if (doubleArray2[int3] == 0.0) {
									collideclassindexArray[int3].addCollided(staticLine, int2, forceCircle.getVector());
								} else {
									collideclassindexArray[int3].setCollided(staticLine, int2, forceCircle.getVector());
								}

								doubleArray2[int3] = 0.0;
								continue;
							}

							if (CircleLineIntersect.Point.distanceSq(forceCircle.getX(), forceCircle.getY(), staticLine.getX1(), staticLine.getY1()) < forceCircle.getRadiusSq()) {
								double6 = CircleLineIntersect.Point.distance(forceCircle.getX(), forceCircle.getY(), staticLine.getX1(), staticLine.getY1());
								double4 = (forceCircle.getX() - staticLine.getX1()) / double6;
								double5 = (forceCircle.getY() - staticLine.getY1()) / double6;
								double3 = staticLine.getX1() + forceCircle.getRadius() * double4;
								double1 = staticLine.getY1() + forceCircle.getRadius() * double5;
								if (doubleArray2[int3] == -1.0) {
									pointVectorArray[int3] = new CircleLineIntersect.PointVector(double3, double1);
								} else {
									pointVectorArray[int3].setPoint(double3, double1);
								}

								if (doubleArray2[int3] == 0.0) {
									collideclassindexArray[int3].addCollided(staticLine, int2, forceCircle.getVector());
								} else {
									collideclassindexArray[int3].setCollided(staticLine, int2, forceCircle.getVector());
								}

								doubleArray2[int3] = 0.0;
								continue;
							}

							if (CircleLineIntersect.Point.distanceSq(forceCircle.getX(), forceCircle.getY(), staticLine.getX2(), staticLine.getY2()) < forceCircle.getRadiusSq()) {
								double6 = CircleLineIntersect.Point.distance(forceCircle.getX(), forceCircle.getY(), staticLine.getX2(), staticLine.getY2());
								double4 = (forceCircle.getX() - staticLine.getX2()) / double6;
								double5 = (forceCircle.getY() - staticLine.getY2()) / double6;
								double3 = staticLine.getX2() + forceCircle.getRadius() * double4;
								double1 = staticLine.getY2() + forceCircle.getRadius() * double5;
								if (doubleArray2[int3] == -1.0) {
									pointVectorArray[int3] = new CircleLineIntersect.PointVector(double3, double1);
								} else {
									pointVectorArray[int3].setPoint(double3, double1);
								}

								if (doubleArray2[int3] == 0.0) {
									collideclassindexArray[int3].addCollided(staticLine, int2, forceCircle.getVector());
								} else {
									collideclassindexArray[int3].setCollided(staticLine, int2, forceCircle.getVector());
								}

								doubleArray2[int3] = 0.0;
								continue;
							}
						}
					}

					double3 = staticLine.getY2() - staticLine.getY1();
					double1 = staticLine.getX1() - staticLine.getX2();
					double6 = (staticLine.getY2() - staticLine.getY1()) * staticLine.getX1() + (staticLine.getX1() - staticLine.getX2()) * staticLine.getY1();
					double4 = forceCircle.getvy();
					double5 = -forceCircle.getvx();
					double double12 = forceCircle.getvy() * forceCircle.getX() + -forceCircle.getvx() * forceCircle.getY();
					double double13 = double3 * double5 - double4 * double1;
					double double14 = 0.0;
					double double15 = 0.0;
					if (double13 != 0.0) {
						double14 = (double5 * double6 - double1 * double12) / double13;
						double15 = (double3 * double12 - double4 * double6) / double13;
					}

					CircleLineIntersect.Point point3 = CircleLineIntersect.VectorMath.closestpointonline(staticLine.getX1(), staticLine.getY1(), staticLine.getX2(), staticLine.getY2(), forceCircle.getX2(), forceCircle.getY2());
					CircleLineIntersect.Point point4 = CircleLineIntersect.VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX1(), staticLine.getY1());
					CircleLineIntersect.Point point5 = CircleLineIntersect.VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX2(), staticLine.getY2());
					if (CircleLineIntersect.Point.distanceSq(point3.getX(), point3.getY(), forceCircle.getX2(), forceCircle.getY2()) < forceCircle.getRadiusSq() && Math.min(staticLine.getX1(), staticLine.getX2()) <= point3.getX() && point3.getX() <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= point3.getY() && point3.getY() <= Math.max(staticLine.getY1(), staticLine.getY2()) || CircleLineIntersect.Point.distanceSq(point4.getX(), point4.getY(), staticLine.getX1(), staticLine.getY1()) < forceCircle.getRadiusSq() && Math.min(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) <= point4.getX() && point4.getX() <= Math.max(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) && Math.min(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) <= point4.getY() && point4.getY() <= Math.max(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) || CircleLineIntersect.Point.distanceSq(point5.getX(), point5.getY(), staticLine.getX2(), staticLine.getY2()) < forceCircle.getRadiusSq() && Math.min(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) <= point5.getX() && point5.getX() <= Math.max(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) && Math.min(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) <= point5.getY() && point5.getY() <= Math.max(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) || Math.min(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) <= double14 && double14 <= Math.max(forceCircle.getX(), forceCircle.getX() + forceCircle.getvx()) && Math.min(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) <= double15 && double15 <= Math.max(forceCircle.getY(), forceCircle.getY() + forceCircle.getvy()) && Math.min(staticLine.getX1(), staticLine.getX2()) <= double14 && double14 <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= double15 && double15 <= Math.max(staticLine.getY1(), staticLine.getY2()) || CircleLineIntersect.Point.distanceSq(staticLine.getX1(), staticLine.getY1(), forceCircle.getX2(), forceCircle.getY2()) <= forceCircle.getRadiusSq() || CircleLineIntersect.Point.distanceSq(staticLine.getX2(), staticLine.getY2(), forceCircle.getX2(), forceCircle.getY2()) <= forceCircle.getRadiusSq()) {
						double double16 = -double1;
						double double17 = double16 * forceCircle.getX() + double3 * forceCircle.getY();
						double double18 = double3 * double3 - double16 * double1;
						double double19 = 0.0;
						double double20 = 0.0;
						if (double18 != 0.0) {
							double19 = (double3 * double6 - double1 * double17) / double18;
							double20 = (double3 * double17 - double16 * double6) / double18;
							double double21 = CircleLineIntersect.Point.distance(double14, double15, forceCircle.getX(), forceCircle.getY()) * forceCircle.getRadius() / CircleLineIntersect.Point.distance(double19, double20, forceCircle.getX(), forceCircle.getY());
							double14 += -double21 * forceCircle.getnormvx();
							double15 += -double21 * forceCircle.getnormvy();
							double double22 = double16 * double14 + double3 * double15;
							double double23 = (double3 * double6 - double1 * double22) / double18;
							double double24 = (double3 * double22 - double16 * double6) / double18;
							double double25;
							CircleLineIntersect.Point point6;
							if (Math.min(staticLine.getX1(), staticLine.getX2()) <= double23 && double23 <= Math.max(staticLine.getX1(), staticLine.getX2()) && Math.min(staticLine.getY1(), staticLine.getY2()) <= double24 && double24 <= Math.max(staticLine.getY1(), staticLine.getY2())) {
								double25 = double19;
								double double26 = double20;
								double19 += double14 - double23;
								double20 += double15 - double24;
								double double27 = Math.pow(double14 - forceCircle.getX(), 2.0) + Math.pow(double15 - forceCircle.getY(), 2.0);
								if (double27 <= doubleArray2[int3] || doubleArray2[int3] < 0.0) {
									CircleLineIntersect.RectVector rectVector = null;
									if (!collideclassindexArray[int3].collided() || doubleArray2[int3] != double27) {
										for (int int4 = 0; int4 < collideclassindexArray[int3].size(); ++int4) {
											if (collideclassindexArray[int3].collided() && ((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int4)).getCollideobj() instanceof CircleLineIntersect.ForceCircle && doubleArray2[int3] > double27) {
												pointVectorArray[((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int4)).getCollidewith()] = new CircleLineIntersect.PointVector((CircleLineIntersect.PointVector)list.get(((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int4)).getCollidewith()));
												doubleArray2[((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int4)).getCollidewith()] = -1.0;
											}
										}
									}

									if (CircleLineIntersect.Point.distanceSq(double19, double20, forceCircle.getX(), forceCircle.getY()) < 1.0E-8) {
										point6 = CircleLineIntersect.VectorMath.closestpointonline(staticLine.getX1() + (double14 - double25), staticLine.getY1() + (double15 - double26), staticLine.getX2() + (double14 - double25), staticLine.getY2() + (double15 - double26), forceCircle.getX2(), forceCircle.getY2());
										rectVector = new CircleLineIntersect.RectVector(point6.getX() + (point6.getX() - forceCircle.getX2()) - forceCircle.getX(), point6.getY() + (point6.getY() - forceCircle.getY2()) - forceCircle.getY());
										rectVector = (CircleLineIntersect.RectVector)rectVector.getUnitVector();
										rectVector = new CircleLineIntersect.RectVector(rectVector.getvx() * forceCircle.getLength(), rectVector.getvy() * forceCircle.getLength());
									} else {
										rectVector = new CircleLineIntersect.RectVector(forceCircle.getX() - 2.0 * (double19 - double14) - double14, forceCircle.getY() - 2.0 * (double20 - double15) - double15);
										rectVector = (CircleLineIntersect.RectVector)rectVector.getUnitVector();
										rectVector = new CircleLineIntersect.RectVector(rectVector.getvx() * forceCircle.getLength(), rectVector.getvy() * forceCircle.getLength());
									}

									rectVector = (CircleLineIntersect.RectVector)rectVector.getUnitVector();
									rectVector = new CircleLineIntersect.RectVector(rectVector.getvx() * forceCircle.getLength(), rectVector.getvy() * forceCircle.getLength());
									if (doubleArray2[int3] == -1.0) {
										pointVectorArray[int3] = new CircleLineIntersect.PointVector(double14, double15);
									} else {
										pointVectorArray[int3].setPoint(double14, double15);
									}

									if (doubleArray2[int3] == double27) {
										collideclassindexArray[int3].addCollided(staticLine, int2, rectVector);
									} else {
										collideclassindexArray[int3].setCollided(staticLine, int2, rectVector);
									}

									doubleArray2[int3] = double27;
								}
							} else {
								double25 = forceCircle.getRadius() * forceCircle.getRadius();
								CircleLineIntersect.Point point7 = CircleLineIntersect.VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX1(), staticLine.getY1());
								double double28 = CircleLineIntersect.Point.distanceSq(point7.getX(), point7.getY(), staticLine.getX1(), staticLine.getY1());
								double double29 = CircleLineIntersect.Point.distanceSq(point7.getX(), point7.getY(), forceCircle.getX(), forceCircle.getY());
								point6 = CircleLineIntersect.VectorMath.closestpointonline(forceCircle.getX(), forceCircle.getY(), forceCircle.getX2(), forceCircle.getY2(), staticLine.getX2(), staticLine.getY2());
								double double30 = CircleLineIntersect.Point.distanceSq(point6.getX(), point6.getY(), staticLine.getX2(), staticLine.getY2());
								double double31 = CircleLineIntersect.Point.distanceSq(point6.getX(), point6.getY(), forceCircle.getX(), forceCircle.getY());
								double double32 = 0.0;
								if (double29 < double31 && double28 <= double30) {
									double32 = Math.sqrt(Math.abs(double25 - double28));
									double14 = point7.getX() - double32 * forceCircle.getnormvx();
									double15 = point7.getY() - double32 * forceCircle.getnormvy();
									double19 = staticLine.getX1();
									double20 = staticLine.getY1();
								} else if (double29 > double31 && double28 >= double30) {
									double32 = Math.sqrt(Math.abs(double25 - double30));
									double14 = point6.getX() - double32 * forceCircle.getnormvx();
									double15 = point6.getY() - double32 * forceCircle.getnormvy();
									double19 = staticLine.getX2();
									double20 = staticLine.getY2();
								} else if (double28 < double30) {
									if (!(double29 < double31) && !(CircleLineIntersect.Point.distanceSq(double23, double24, staticLine.getX1(), staticLine.getY1()) <= double25)) {
										double32 = Math.sqrt(Math.abs(double25 - double30));
										double14 = point6.getX() - double32 * forceCircle.getnormvx();
										double15 = point6.getY() - double32 * forceCircle.getnormvy();
										double19 = staticLine.getX2();
										double20 = staticLine.getY2();
									} else {
										double32 = Math.sqrt(Math.abs(double25 - double28));
										double14 = point7.getX() - double32 * forceCircle.getnormvx();
										double15 = point7.getY() - double32 * forceCircle.getnormvy();
										double19 = staticLine.getX1();
										double20 = staticLine.getY1();
									}
								} else if (double28 > double30) {
									if (!(double31 < double29) && !(CircleLineIntersect.Point.distanceSq(double23, double24, staticLine.getX2(), staticLine.getY2()) <= double25)) {
										double32 = Math.sqrt(Math.abs(double25 - double28));
										double14 = point7.getX() - double32 * forceCircle.getnormvx();
										double15 = point7.getY() - double32 * forceCircle.getnormvy();
										double19 = staticLine.getX1();
										double20 = staticLine.getY1();
									} else {
										double32 = Math.sqrt(Math.abs(double25 - double30));
										double14 = point6.getX() - double32 * forceCircle.getnormvx();
										double15 = point6.getY() - double32 * forceCircle.getnormvy();
										double19 = staticLine.getX2();
										double20 = staticLine.getY2();
									}
								} else if ((!(Math.min(forceCircle.getX(), forceCircle.getX2()) <= point6.getX()) || !(point6.getX() <= Math.max(forceCircle.getX(), forceCircle.getX2())) || !(Math.min(forceCircle.getY(), forceCircle.getY2()) <= point6.getY()) || !(point6.getY() <= Math.max(forceCircle.getY(), forceCircle.getY2()))) && !(CircleLineIntersect.Point.distanceSq(point6.getX(), point6.getY(), forceCircle.getX2(), forceCircle.getY2()) <= forceCircle.getRadiusSq())) {
									double32 = Math.sqrt(Math.abs(double25 - double28));
									double14 = point7.getX() - double32 * forceCircle.getnormvx();
									double15 = point7.getY() - double32 * forceCircle.getnormvy();
									double19 = staticLine.getX1();
									double20 = staticLine.getY1();
								} else if ((!(Math.min(forceCircle.getX(), forceCircle.getX2()) <= point7.getX()) || !(point7.getX() <= Math.max(forceCircle.getX(), forceCircle.getX2())) || !(Math.min(forceCircle.getY(), forceCircle.getY2()) <= point7.getY()) || !(point7.getY() <= Math.max(forceCircle.getY(), forceCircle.getY2()))) && !(CircleLineIntersect.Point.distanceSq(point6.getX(), point6.getY(), forceCircle.getX2(), forceCircle.getY2()) <= forceCircle.getRadiusSq())) {
									double32 = Math.sqrt(Math.abs(double25 - double30));
									double14 = point6.getX() - double32 * forceCircle.getnormvx();
									double15 = point6.getY() - double32 * forceCircle.getnormvy();
									double19 = staticLine.getX2();
									double20 = staticLine.getY2();
								} else if (double29 < double31) {
									double32 = Math.sqrt(Math.abs(double25 - double28));
									double14 = point7.getX() - double32 * forceCircle.getnormvx();
									double15 = point7.getY() - double32 * forceCircle.getnormvy();
									double19 = staticLine.getX1();
									double20 = staticLine.getY1();
								} else {
									double32 = Math.sqrt(Math.abs(double25 - double30));
									double14 = point6.getX() - double32 * forceCircle.getnormvx();
									double15 = point6.getY() - double32 * forceCircle.getnormvy();
									double19 = staticLine.getX2();
									double20 = staticLine.getY2();
								}

								double double33 = Math.pow(double14 - forceCircle.getX(), 2.0) + Math.pow(double15 - forceCircle.getY(), 2.0);
								if (double33 <= doubleArray2[int3] || doubleArray2[int3] < 0.0) {
									CircleLineIntersect.RectVector rectVector2 = null;
									if (!collideclassindexArray[int3].collided() || doubleArray2[int3] != double33) {
										for (int int5 = 0; int5 < collideclassindexArray[int3].size(); ++int5) {
											if (collideclassindexArray[int3].collided() && ((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int5)).getCollideobj() instanceof CircleLineIntersect.ForceCircle && doubleArray2[int3] > double33) {
												pointVectorArray[((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int5)).getCollidewith()] = new CircleLineIntersect.PointVector((CircleLineIntersect.PointVector)list.get(((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int5)).getCollidewith()));
												doubleArray2[((CircleLineIntersect.Collider)collideclassindexArray[int3].getColliders().get(int5)).getCollidewith()] = -1.0;
											}
										}
									}

									rectVector2 = new CircleLineIntersect.RectVector(double14 - (double19 - double14) - double14, double15 - (double20 - double15) - double15);
									rectVector2 = (CircleLineIntersect.RectVector)rectVector2.getUnitVector();
									rectVector2 = new CircleLineIntersect.RectVector(rectVector2.getvx() * forceCircle.getLength(), rectVector2.getvy() * forceCircle.getLength());
									if (doubleArray2[int3] == -1.0) {
										pointVectorArray[int3] = new CircleLineIntersect.PointVector(double14, double15);
									} else {
										pointVectorArray[int3].setPoint(double14, double15);
									}

									if (doubleArray2[int3] == double33) {
										collideclassindexArray[int3].addCollided(staticLine, int2, rectVector2);
									} else {
										collideclassindexArray[int3].setCollided(staticLine, int2, rectVector2);
									}

									doubleArray2[int3] = double33;
								}
							}
						}
					}
				}
			}
		}

		ArrayList arrayList2 = new ArrayList((int)Math.ceil((double)(list.size() / 10)));
		for (int2 = 0; int2 < pointVectorArray.length; ++int2) {
			if (collideclassindexArray[int2].collided()) {
				forceCircle = (CircleLineIntersect.ForceCircle)list.get(int2);
				if (forceCircle.isFrozen()) {
					pointVectorArray[int2].setRect(0.0, 0.0);
				} else {
					double double34 = 0.0;
					double double35 = 0.0;
					boolean boolean2 = false;
					double1 = 0.0;
					for (int int6 = 0; int6 < collideclassindexArray[int2].size(); ++int6) {
						Object object = ((CircleLineIntersect.Collider)collideclassindexArray[int2].getColliders().get(int6)).getCollideobj();
						double1 += ((CircleLineIntersect.ForceCircle)list.get(int2)).getRestitution(((CircleLineIntersect.Collider)collideclassindexArray[int2].getColliders().get(int6)).getCollideobj());
						if (object instanceof CircleLineIntersect.StaticLine && ((CircleLineIntersect.Collider)collideclassindexArray[int2].getColliders().get(int6)).getCollideforce() != null) {
							double34 += ((CircleLineIntersect.Collider)collideclassindexArray[int2].getColliders().get(int6)).getCollideforce().getvx();
							double35 += ((CircleLineIntersect.Collider)collideclassindexArray[int2].getColliders().get(int6)).getCollideforce().getvy();
						}
					}

					double1 /= (double)collideclassindexArray[int2].getColliders().size();
					if (doubleArray2[int2] == -1.0) {
						pointVectorArray[int2] = new CircleLineIntersect.PointVector(pointVectorArray[int2].getX(), pointVectorArray[int2].getY());
					}

					pointVectorArray[int2].setRect(double34 * double1, double35 * double1);
					arrayList2.add(int2);
					if (doubleArray3[int2] == 1.0 && ((CircleLineIntersect.ForceCircle)list.get(int2)).getLength() != 0.0 && !boolean2) {
						if (doubleArray2[int2] == 0.0) {
							doubleArray3[int2] = 0.0;
						} else if (doubleArray2[int2] > 0.0) {
							doubleArray3[int2] = Math.sqrt(doubleArray2[int2]) / ((CircleLineIntersect.ForceCircle)list.get(int2)).getLength();
						} else {
							doubleArray3[int2] = ((CircleLineIntersect.ForceCircle)list.get(int2)).distance(pointVectorArray[int2]) / ((CircleLineIntersect.ForceCircle)list.get(int2)).getLength();
						}
					}

					doubleArray[int2] += doubleArray3[int2] * (1.0 - doubleArray[int2]);
					if (!pointVectorArray[int2].equals(list.get(int2))) {
						booleanArray[int2] = true;
					}
				}
			}
		}

		return new CircleLineIntersect.Collideresult(pointVectorArray, collideclassindexArray, arrayList2, doubleArray, doubleArray3, booleanArray);
	}

	public static CircleLineIntersect.Collideresult checkforcecirclescollide(List list, ArrayList arrayList, double[] doubleArray, boolean[] booleanArray, boolean boolean1) {
		CircleLineIntersect.Collideresult collideresult = checkforcecirclescollidetime(list, arrayList, doubleArray, booleanArray, boolean1);
		new ArrayList();
		for (int int1 = collideresult.resultants.length - 1; int1 >= 0; --int1) {
			if (collideresult.collideinto[int1].collided()) {
				((CircleLineIntersect.ForceCircle)list.get(int1)).setPointVector(collideresult.resultants[int1]);
			}
		}

		return collideresult;
	}

	public static CircleLineIntersect.Collideresult checkforcecirclescollide(List list, ArrayList arrayList) {
		double[] doubleArray = new double[list.size()];
		boolean[] booleanArray = new boolean[list.size()];
		for (int int1 = list.size() - 1; int1 >= 0; --int1) {
			doubleArray[int1] = 1.0;
		}

		return checkforcecirclescollide(list, arrayList, doubleArray, booleanArray, true);
	}

	public static boolean TEST(Vector3f vector3f, float float1, float float2, float float3, float float4, CarController carController) {
		Vector3f vector3f2 = new Vector3f();
		vector3f.cross(new Vector3f(0.0F, 1.0F, 0.0F), vector3f2);
		vector3f.x *= float4;
		vector3f.z *= float4;
		vector3f2.x *= float3;
		vector3f2.z *= float3;
		float float5 = float1 + vector3f.x;
		float float6 = float2 + vector3f.z;
		float float7 = float1 - vector3f.x;
		float float8 = float2 - vector3f.z;
		float float9 = float5 - vector3f2.x / 2.0F;
		float float10 = float5 + vector3f2.x / 2.0F;
		float float11 = float7 - vector3f2.x / 2.0F;
		float float12 = float7 + vector3f2.x / 2.0F;
		float float13 = float8 - vector3f2.z / 2.0F;
		float float14 = float8 + vector3f2.z / 2.0F;
		float float15 = float6 - vector3f2.z / 2.0F;
		float float16 = float6 + vector3f2.z / 2.0F;
		float9 += WorldSimulation.instance.offsetX;
		float15 += WorldSimulation.instance.offsetY;
		float10 += WorldSimulation.instance.offsetX;
		float16 += WorldSimulation.instance.offsetY;
		float11 += WorldSimulation.instance.offsetX;
		float13 += WorldSimulation.instance.offsetY;
		float12 += WorldSimulation.instance.offsetX;
		float14 += WorldSimulation.instance.offsetY;
		ArrayList arrayList = new ArrayList();
		CircleLineIntersect.StaticLine staticLine;
		arrayList.add(staticLine = new CircleLineIntersect.StaticLine((double)float9, (double)float15, (double)float10, (double)float16));
		CircleLineIntersect.StaticLine staticLine2;
		arrayList.add(staticLine2 = new CircleLineIntersect.StaticLine((double)float10, (double)float16, (double)float12, (double)float14));
		CircleLineIntersect.StaticLine staticLine3;
		arrayList.add(staticLine3 = new CircleLineIntersect.StaticLine((double)float12, (double)float14, (double)float11, (double)float13));
		CircleLineIntersect.StaticLine staticLine4;
		arrayList.add(staticLine4 = new CircleLineIntersect.StaticLine((double)float11, (double)float13, (double)float9, (double)float15));
		IsoPlayer player = IsoPlayer.getInstance();
		ArrayList arrayList2 = new ArrayList();
		boolean boolean1 = true;
		CircleLineIntersect.ForceCircle forceCircle = new CircleLineIntersect.ForceCircle((double)player.x, (double)player.y, (double)(player.nx - player.x), (double)(player.ny - player.y), 0.295);
		if (carController != null) {
			carController.drawCircle((float)forceCircle.getX2(), (float)forceCircle.getY2(), 0.3F);
		}

		arrayList2.add(forceCircle);
		CircleLineIntersect.Collideresult collideresult = checkforcecirclescollide(arrayList2, arrayList);
		if (carController != null) {
			carController.drawCircle((float)forceCircle.getX(), (float)forceCircle.getY(), (float)forceCircle.getRadius());
		}

		if (collideresult.collidelist.isEmpty()) {
			return false;
		} else {
			int int1 = collideresult.collideinto.length;
			Vector2f vector2f = new Vector2f(player.nx - player.x, player.ny - player.y);
			if (vector2f.length() > 0.0F) {
				vector2f.normalise();
			}

			for (int int2 = 0; int2 < collideresult.collideinto.length; ++int2) {
				CircleLineIntersect.StaticLine staticLine5 = (CircleLineIntersect.StaticLine)((CircleLineIntersect.Collider)collideresult.collideinto[int2].getColliders().get(0)).getCollideobj();
				CircleLineIntersect.Point point;
				double double1;
				if (staticLine5 == staticLine4 || staticLine5 == staticLine2) {
					LineDrawer.addLine(float5 + WorldSimulation.instance.offsetX, float6 + WorldSimulation.instance.offsetY, 0.0F, float7 + WorldSimulation.instance.offsetX, float8 + WorldSimulation.instance.offsetY, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
					point = CircleLineIntersect.VectorMath.closestpointonline((double)(float5 + WorldSimulation.instance.offsetX), (double)(float6 + WorldSimulation.instance.offsetY), (double)(float7 + WorldSimulation.instance.offsetX), (double)(float8 + WorldSimulation.instance.offsetY), forceCircle.getX(), forceCircle.getY());
					vector3f.set((float)(point.x - (double)player.x), (float)(point.y - (double)player.y), 0.0F);
					vector3f.normalize();
					double1 = CircleLineIntersect.VectorMath.dotproduct((double)vector2f.x, (double)vector2f.y, (double)vector3f.x, (double)vector3f.y);
					if (double1 < 0.0) {
						--int1;
					}
				}

				if (staticLine5 == staticLine || staticLine5 == staticLine3) {
					LineDrawer.addLine(float1 - vector3f2.x / 2.0F + WorldSimulation.instance.offsetX, float2 - vector3f2.z / 2.0F + WorldSimulation.instance.offsetY, 0.0F, float1 + vector3f2.x / 2.0F + WorldSimulation.instance.offsetX, float2 + vector3f2.z / 2.0F + WorldSimulation.instance.offsetY, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
					point = CircleLineIntersect.VectorMath.closestpointonline((double)(float1 - vector3f2.x / 2.0F + WorldSimulation.instance.offsetX), (double)(float2 - vector3f2.z / 2.0F + WorldSimulation.instance.offsetY), (double)(float1 + vector3f2.x / 2.0F + WorldSimulation.instance.offsetX), (double)(float2 + vector3f2.z / 2.0F + WorldSimulation.instance.offsetY), forceCircle.getX(), forceCircle.getY());
					vector3f.set((float)(point.x - (double)player.x), (float)(point.y - (double)player.y), 0.0F);
					vector3f.normalize();
					double1 = CircleLineIntersect.VectorMath.dotproduct((double)vector2f.x, (double)vector2f.y, (double)vector3f.x, (double)vector3f.y);
					if (double1 < 0.0) {
						--int1;
					}
				}
			}

			if (int1 == 0) {
				return false;
			} else {
				vector3f.set((float)forceCircle.getX(), (float)forceCircle.getY(), 0.0F);
				return true;
			}
		}
	}

	static class VectorMath {

		public static final CircleLineIntersect.Vector add(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return new CircleLineIntersect.RectVector(vector.getvx() + vector2.getvx(), vector.getvy() + vector2.getvy());
		}

		public static final CircleLineIntersect.Vector subtract(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return new CircleLineIntersect.RectVector(vector.getvx() - vector2.getvx(), vector.getvy() - vector2.getvy());
		}

		public static final double length(double double1, double double2) {
			return CircleLineIntersect.Point.distance(0.0, 0.0, double1, double2);
		}

		public static final double dotproduct(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return dotproduct(vector.getvx(), vector.getvy(), vector2.getvx(), vector2.getvy());
		}

		public static final double dotproduct(double double1, double double2, double double3, double double4) {
			return double1 * double3 + double2 * double4;
		}

		public static final double cosproj(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return dotproduct(vector, vector2) / (vector.getLength() * vector2.getLength());
		}

		public static final double cosproj(double double1, double double2, double double3, double double4) {
			return dotproduct(double1, double2, double3, double4) / (length(double1, double2) * length(double3, double4));
		}

		public static final double anglebetween(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return Math.acos(cosproj(vector, vector2));
		}

		public static final double anglebetween(double double1, double double2, double double3, double double4) {
			return Math.acos(cosproj(double1, double2, double3, double4));
		}

		public static final double crossproduct(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return crossproduct(vector.getvx(), vector.getvy(), vector2.getvx(), vector2.getvy());
		}

		public static final double crossproduct(double double1, double double2, double double3, double double4) {
			return double1 * double4 - double2 * double3;
		}

		public static final double sinproj(CircleLineIntersect.Vector vector, CircleLineIntersect.Vector vector2) {
			return crossproduct(vector, vector2) / (vector.getLength() * vector2.getLength());
		}

		public static final double sinproj(double double1, double double2, double double3, double double4) {
			return crossproduct(double1, double2, double3, double4) / (length(double1, double2) * length(double3, double4));
		}

		public static final boolean equaldirection(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {
			if (double1 - double3 == 0.0 && double2 - double4 == 0.0) {
				return true;
			} else {
				double double9 = ((double1 - double3) * (double1 - double5) + (double2 - double4) * (double2 - double6)) / (Math.abs(double7) * Math.abs(double8));
				return double9 > 0.995 && double9 <= 1.0;
			}
		}

		public static final boolean equaldirection(double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8, double double9) {
			if (double1 - double3 == 0.0 && double2 - double4 == 0.0) {
				return true;
			} else {
				double double10 = ((double1 - double3) * (double1 - double5) + (double2 - double4) * (double2 - double6)) / (Math.abs(double7) * Math.abs(double8));
				return double10 > double9 && double10 <= 1.0;
			}
		}

		public static final boolean equaldirection(double double1, double double2, double double3, double double4, double double5, double double6, double double7) {
			if (double1 == 0.0 && double2 == 0.0) {
				return true;
			} else {
				double double8 = (double1 * double3 + double2 * double4) / (Math.abs(double5) * Math.abs(double6));
				return double8 > double7 && double8 <= 1.0;
			}
		}

		public static final boolean equaldirection(double double1, double double2, double double3) {
			if (double1 > 6.283185307179586) {
				double1 -= 6.283185307179586;
			} else if (double1 < 0.0) {
				double1 += 6.283185307179586;
			}

			if (double2 > 6.283185307179586) {
				double2 -= 6.283185307179586;
			} else if (double2 < 0.0) {
				double2 += 6.283185307179586;
			}

			return Math.abs(double1 - double2) < double3;
		}

		public static final double linepointdistance(double double1, double double2, double double3, double double4, double double5, double double6) {
			CircleLineIntersect.Point point = closestpointonline(double1, double2, double3, double4, double5, double6);
			return CircleLineIntersect.Point.distance(double5, double6, point.getX(), point.getY());
		}

		public static final double linepointdistancesq(double double1, double double2, double double3, double double4, double double5, double double6) {
			double double7 = double4 - double2;
			double double8 = double1 - double3;
			double double9 = (double4 - double2) * double1 + (double1 - double3) * double2;
			double double10 = -double8 * double5 + double7 * double6;
			double double11 = double7 * double7 - -double8 * double8;
			double double12 = 0.0;
			double double13 = 0.0;
			if (double11 != 0.0) {
				double12 = (double7 * double9 - double8 * double10) / double11;
				double13 = (double7 * double10 - -double8 * double9) / double11;
			}

			return Math.abs((double12 - double5) * (double12 - double5) + (double13 - double6) * (double13 - double6));
		}

		public static final CircleLineIntersect.Point closestpointonline(CircleLineIntersect.StaticLine staticLine, CircleLineIntersect.Point point) {
			return closestpointonline(staticLine.getX(), staticLine.getY(), staticLine.getX2(), staticLine.getY2(), point.getX(), point.getY());
		}

		public static final CircleLineIntersect.Point closestpointonline(double double1, double double2, double double3, double double4, double double5, double double6) {
			double double7 = double4 - double2;
			double double8 = double1 - double3;
			double double9 = (double4 - double2) * double1 + (double1 - double3) * double2;
			double double10 = -double8 * double5 + double7 * double6;
			double double11 = double7 * double7 - -double8 * double8;
			double double12 = 0.0;
			double double13 = 0.0;
			if (double11 != 0.0) {
				double12 = (double7 * double9 - double8 * double10) / double11;
				double13 = (double7 * double10 - -double8 * double9) / double11;
			} else {
				double12 = double5;
				double13 = double6;
			}

			return new CircleLineIntersect.Point(double12, double13);
		}

		public static final CircleLineIntersect.Vector getVector(CircleLineIntersect.Point point, CircleLineIntersect.Point point2) {
			return new CircleLineIntersect.RectVector(point.getX() - point2.getX(), point.getY() - point2.getY());
		}

		public static final CircleLineIntersect.Vector rotate(CircleLineIntersect.Vector vector, double double1) {
			return new CircleLineIntersect.RectVector(vector.getvx() * Math.cos(double1) - vector.getvy() * Math.sin(double1), vector.getvx() * Math.sin(double1) + vector.getvy() * Math.cos(double1));
		}
	}

	static class Collideresult {
		protected CircleLineIntersect.PointVector[] resultants;
		protected ArrayList collidelist;
		protected CircleLineIntersect.Collideclassindex[] collideinto;
		protected double[] timepassed;
		protected double[] collidetime;
		protected boolean[] modified;

		public Collideresult(CircleLineIntersect.PointVector[] pointVectorArray, CircleLineIntersect.Collideclassindex[] collideclassindexArray, ArrayList arrayList, double[] doubleArray, double[] doubleArray2, boolean[] booleanArray) {
			this.resultants = pointVectorArray;
			this.collideinto = collideclassindexArray;
			this.collidelist = arrayList;
			this.timepassed = doubleArray;
			this.collidetime = doubleArray2;
			this.modified = booleanArray;
		}

		public String toString() {
			return this.collidelist.toString();
		}
	}

	static class Collideclassindex {
		private ArrayList colliders = new ArrayList(1);
		private int numforcecircles;

		public Collideclassindex() {
			this.numforcecircles = 0;
		}

		public Collideclassindex(Object object, int int1, CircleLineIntersect.Vector vector) {
			this.colliders.add(new CircleLineIntersect.Collider(object, int1, vector));
		}

		public boolean collided() {
			return this.size() > 0;
		}

		public void reset() {
			this.colliders.trimToSize();
			this.colliders.clear();
			this.numforcecircles = 0;
		}

		public void setCollided(Object object, int int1, CircleLineIntersect.Vector vector) {
			if (this.size() > 0) {
				this.reset();
			}

			if (object instanceof CircleLineIntersect.ForceCircle && !((CircleLineIntersect.ForceCircle)object).isFrozen()) {
				++this.numforcecircles;
			}

			this.colliders.add(new CircleLineIntersect.Collider(object, int1, vector));
		}

		public void addCollided(Object object, int int1, CircleLineIntersect.Vector vector) {
			if (object instanceof CircleLineIntersect.ForceCircle && !((CircleLineIntersect.ForceCircle)object).isFrozen()) {
				++this.numforcecircles;
			}

			this.colliders.add(new CircleLineIntersect.Collider(object, int1, vector));
		}

		public ArrayList getColliders() {
			return this.colliders;
		}

		public int getNumforcecircles() {
			return this.numforcecircles;
		}

		public CircleLineIntersect.Collider contains(Object object) {
			Iterator iterator = this.colliders.iterator();
			CircleLineIntersect.Collider collider;
			do {
				if (!iterator.hasNext()) {
					return null;
				}

				collider = (CircleLineIntersect.Collider)iterator.next();
			} while (!collider.getCollideobj().equals(object));

			return collider;
		}

		public int size() {
			return this.colliders.size();
		}

		public String toString() {
			String string = "";
			CircleLineIntersect.Collider collider;
			for (Iterator iterator = this.colliders.iterator(); iterator.hasNext(); string = string + collider.toString() + "\n") {
				collider = (CircleLineIntersect.Collider)iterator.next();
			}

			return string;
		}
	}

	static class Collider {
		private Object collideobj;
		private Integer collideindex;
		private CircleLineIntersect.Vector collideforce;

		public Collider(CircleLineIntersect.Vector vector, Integer integer) {
			this.collideobj = vector;
			this.collideindex = integer;
			this.collideforce = vector;
		}

		public Collider(Object object, Integer integer, CircleLineIntersect.Vector vector) {
			this.collideobj = object;
			this.collideindex = integer;
			this.collideforce = vector;
		}

		public Object getCollideobj() {
			return this.collideobj;
		}

		public Integer getCollidewith() {
			return this.collideindex;
		}

		public CircleLineIntersect.Vector getCollideforce() {
			return this.collideforce;
		}

		public void setCollideforce(CircleLineIntersect.Vector vector) {
			this.collideforce = vector;
		}

		public String toString() {
			return this.collideobj.getClass().getSimpleName() + " @ " + this.collideindex + " hit with " + this.collideforce.toString();
		}
	}

	static class StaticLine extends CircleLineIntersect.Point {
		double x2;
		double y2;

		public StaticLine(double double1, double double2, double double3, double double4) {
			super(double1, double2);
			this.x2 = double3;
			this.y2 = double4;
		}

		public CircleLineIntersect.Point getP1() {
			return new CircleLineIntersect.Point(this.getX1(), this.getY1());
		}

		public CircleLineIntersect.Point getP2() {
			return new CircleLineIntersect.Point(this.getX2(), this.getY2());
		}

		public double getX1() {
			return this.x;
		}

		public double getX2() {
			return this.x2;
		}

		public double getY1() {
			return this.y;
		}

		public double getY2() {
			return this.y2;
		}
	}

	static class ForceCircle extends CircleLineIntersect.Force {
		protected double radius;
		protected double radiussq;

		public ForceCircle(double double1, double double2, double double3, double double4, double double5) {
			super(double1, double2, double3, double4);
			this.radius = double5;
			this.radiussq = double5 * double5;
		}

		double getRadius() {
			return this.radius;
		}

		double getRadiusSq() {
			return this.radiussq;
		}
	}

	static class Force extends CircleLineIntersect.PointVector {
		protected double length;
		protected double mass;

		public Force(double double1, double double2, double double3, double double4) {
			super(double1, double2, double3, double4);
			this.length = CircleLineIntersect.VectorMath.length(double3, double4);
		}

		public double getLength() {
			return this.length;
		}

		public double getnormvx() {
			return this.length > 0.0 ? this.vx / this.length : 0.0;
		}

		public double getnormvy() {
			return this.length > 0.0 ? this.vy / this.length : 0.0;
		}

		public double getRestitution(Object object) {
			return 1.0;
		}

		public void setPointVector(CircleLineIntersect.PointVector pointVector) {
			this.x = pointVector.getX();
			this.y = pointVector.getY();
			if (!this.isFrozen() && (this.vx != pointVector.getvx() || this.vy != pointVector.getvy())) {
				this.vx = pointVector.getvx();
				this.vy = pointVector.getvy();
				this.length = CircleLineIntersect.VectorMath.length(this.vx, this.vy);
			}
		}

		boolean isFrozen() {
			return false;
		}
	}

	static class RectVector implements CircleLineIntersect.Vector {
		private double vx;
		private double vy;

		public RectVector(double double1, double double2) {
			this.vx = double1;
			this.vy = double2;
		}

		public RectVector(CircleLineIntersect.Vector vector) {
			this.setVector(vector);
		}

		public double getLength() {
			return Math.sqrt(Math.abs(this.getvx() * this.getvx() + this.getvy() * this.getvy()));
		}

		public CircleLineIntersect.Vector getUnitVector() {
			double double1 = this.getLength();
			return new CircleLineIntersect.RectVector(this.getvx() / double1, this.getvy() / double1);
		}

		public double getvx() {
			return this.vx;
		}

		public double getvy() {
			return this.vy;
		}

		public void setVector(CircleLineIntersect.Vector vector) {
			this.vx = vector.getvx();
			this.vy = vector.getvy();
		}
	}

	static class PointVector extends CircleLineIntersect.Point implements CircleLineIntersect.Vector {
		protected double vx;
		protected double vy;

		public PointVector(double double1, double double2) {
			this(double1, double2, 0.0, 0.0);
		}

		public PointVector(double double1, double double2, double double3, double double4) {
			super(double1, double2);
			this.vx = 0.0;
			this.vy = 0.0;
			this.vx = double3;
			this.vy = double4;
		}

		public PointVector(CircleLineIntersect.PointVector pointVector) {
			this(pointVector.getX(), pointVector.getY(), pointVector.getvx(), pointVector.getvy());
		}

		public double getLength() {
			return CircleLineIntersect.VectorMath.length(this.vx, this.vy);
		}

		public CircleLineIntersect.Vector getVector() {
			return new CircleLineIntersect.RectVector(this.vx, this.vy);
		}

		public double getvx() {
			return this.vx;
		}

		public double getvy() {
			return this.vy;
		}

		public double getX1() {
			return this.x;
		}

		public double getX2() {
			return this.x + this.vx;
		}

		public double getY1() {
			return this.y;
		}

		public double getY2() {
			return this.y + this.vy;
		}

		public void setRect(double double1, double double2) {
			this.vx = double1;
			this.vy = double2;
		}
	}

	interface Vector {

		double getvx();

		double getvy();

		double getLength();
	}

	static class Point {
		double x;
		double y;

		public static final CircleLineIntersect.Point midpoint(double double1, double double2, double double3, double double4) {
			return new CircleLineIntersect.Point((double1 + double3) / 2.0, (double2 + double4) / 2.0);
		}

		public static final CircleLineIntersect.Point midpoint(CircleLineIntersect.Point point, CircleLineIntersect.Point point2) {
			return midpoint(point.getX(), point.getY(), point2.getX(), point2.getY());
		}

		public Point(double double1, double double2) {
			if (Double.isNaN(double1) || Double.isInfinite(double1)) {
				double1 = 0.0;
			}

			if (Double.isNaN(double2) || Double.isInfinite(double2)) {
				double2 = 0.0;
			}

			this.x = double1;
			this.y = double2;
		}

		public double getX() {
			return this.x;
		}

		public double getY() {
			return this.y;
		}

		public void setPoint(double double1, double double2) {
			this.x = double1;
			this.y = double2;
		}

		public static double distanceSq(double double1, double double2, double double3, double double4) {
			double1 -= double3;
			double2 -= double4;
			return double1 * double1 + double2 * double2;
		}

		public static double distance(double double1, double double2, double double3, double double4) {
			double1 -= double3;
			double2 -= double4;
			return Math.sqrt(double1 * double1 + double2 * double2);
		}

		public double distanceSq(double double1, double double2) {
			double1 -= this.getX();
			double2 -= this.getY();
			return double1 * double1 + double2 * double2;
		}

		public double distanceSq(CircleLineIntersect.Point point) {
			double double1 = point.getX() - this.getX();
			double double2 = point.getY() - this.getY();
			return double1 * double1 + double2 * double2;
		}

		public double distance(CircleLineIntersect.Point point) {
			double double1 = point.getX() - this.getX();
			double double2 = point.getY() - this.getY();
			return Math.sqrt(double1 * double1 + double2 * double2);
		}
	}
}
