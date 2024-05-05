package zombie.worldMap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.math.PZMath;
import zombie.vehicles.Clipper;


public final class WorldMapGeometry {
	public WorldMapGeometry.Type m_type;
	public final ArrayList m_points = new ArrayList();
	public int m_minX;
	public int m_minY;
	public int m_maxX;
	public int m_maxY;
	public float[] m_triangles = null;
	public ArrayList m_trianglesPerZoom = null;
	public int m_vboIndex1 = -1;
	public int m_vboIndex2 = -1;
	public int m_vboIndex3 = -1;
	public int m_vboIndex4 = -1;
	private static Clipper s_clipper = null;
	private static ByteBuffer s_vertices = null;

	public void calculateBounds() {
		this.m_minX = this.m_minY = Integer.MAX_VALUE;
		this.m_maxX = this.m_maxY = Integer.MIN_VALUE;
		for (int int1 = 0; int1 < this.m_points.size(); ++int1) {
			WorldMapPoints worldMapPoints = (WorldMapPoints)this.m_points.get(int1);
			worldMapPoints.calculateBounds();
			this.m_minX = PZMath.min(this.m_minX, worldMapPoints.m_minX);
			this.m_minY = PZMath.min(this.m_minY, worldMapPoints.m_minY);
			this.m_maxX = PZMath.max(this.m_maxX, worldMapPoints.m_maxX);
			this.m_maxY = PZMath.max(this.m_maxY, worldMapPoints.m_maxY);
		}
	}

	public boolean containsPoint(float float1, float float2) {
		if (this.m_type == WorldMapGeometry.Type.Polygon && !this.m_points.isEmpty()) {
			return this.isPointInPolygon_WindingNumber(float1, float2, 0) != WorldMapGeometry.PolygonHit.Outside;
		} else {
			return false;
		}
	}

	public void triangulate(double[] doubleArray) {
		if (s_clipper == null) {
			s_clipper = new Clipper();
		}

		s_clipper.clear();
		WorldMapPoints worldMapPoints = (WorldMapPoints)this.m_points.get(0);
		if (s_vertices == null || s_vertices.capacity() < worldMapPoints.size() * 50 * 4) {
			s_vertices = ByteBuffer.allocateDirect(worldMapPoints.size() * 50 * 4);
		}

		s_vertices.clear();
		int int1;
		if (worldMapPoints.isClockwise()) {
			for (int1 = worldMapPoints.numPoints() - 1; int1 >= 0; --int1) {
				s_vertices.putFloat((float)worldMapPoints.getX(int1));
				s_vertices.putFloat((float)worldMapPoints.getY(int1));
			}
		} else {
			for (int1 = 0; int1 < worldMapPoints.numPoints(); ++int1) {
				s_vertices.putFloat((float)worldMapPoints.getX(int1));
				s_vertices.putFloat((float)worldMapPoints.getY(int1));
			}
		}

		s_clipper.addPath(worldMapPoints.numPoints(), s_vertices, false);
		int int2;
		for (int1 = 1; int1 < this.m_points.size(); ++int1) {
			s_vertices.clear();
			WorldMapPoints worldMapPoints2 = (WorldMapPoints)this.m_points.get(int1);
			if (worldMapPoints2.isClockwise()) {
				for (int2 = worldMapPoints2.numPoints() - 1; int2 >= 0; --int2) {
					s_vertices.putFloat((float)worldMapPoints2.getX(int2));
					s_vertices.putFloat((float)worldMapPoints2.getY(int2));
				}
			} else {
				for (int2 = 0; int2 < worldMapPoints2.numPoints(); ++int2) {
					s_vertices.putFloat((float)worldMapPoints2.getX(int2));
					s_vertices.putFloat((float)worldMapPoints2.getY(int2));
				}
			}

			s_clipper.addPath(worldMapPoints2.numPoints(), s_vertices, true);
		}

		if (this.m_minX < 0 || this.m_minY < 0 || this.m_maxX > 300 || this.m_maxY > 300) {
			short short1 = 900;
			float float1 = (float)(-short1);
			float float2 = (float)(-short1);
			float float3 = (float)(300 + short1);
			float float4 = (float)(-short1);
			float float5 = (float)(300 + short1);
			float float6 = (float)(300 + short1);
			float float7 = (float)(-short1);
			float float8 = (float)(300 + short1);
			float float9 = (float)(-short1);
			float float10 = 0.0F;
			float float11 = 0.0F;
			float float12 = 0.0F;
			float float13 = 0.0F;
			float float14 = 300.0F;
			float float15 = 300.0F;
			float float16 = 300.0F;
			float float17 = 300.0F;
			float float18 = 0.0F;
			float float19 = (float)(-short1);
			float float20 = 0.0F;
			s_vertices.clear();
			s_vertices.putFloat(float1).putFloat(float2);
			s_vertices.putFloat(float3).putFloat(float4);
			s_vertices.putFloat(float5).putFloat(float6);
			s_vertices.putFloat(float7).putFloat(float8);
			s_vertices.putFloat(float9).putFloat(float10);
			s_vertices.putFloat(float11).putFloat(float12);
			s_vertices.putFloat(float13).putFloat(float14);
			s_vertices.putFloat(float15).putFloat(float16);
			s_vertices.putFloat(float17).putFloat(float18);
			s_vertices.putFloat(float19).putFloat(float20);
			s_clipper.addPath(10, s_vertices, true);
		}

		int1 = s_clipper.generatePolygons(0.0);
		if (int1 > 0) {
			s_vertices.clear();
			int int3 = s_clipper.triangulate(0, s_vertices);
			this.m_triangles = new float[int3 * 2];
			for (int2 = 0; int2 < int3; ++int2) {
				this.m_triangles[int2 * 2] = s_vertices.getFloat();
				this.m_triangles[int2 * 2 + 1] = s_vertices.getFloat();
			}

			if (doubleArray != null) {
				for (int2 = 0; int2 < doubleArray.length; ++int2) {
					double double1 = doubleArray[int2] - (int2 == 0 ? 0.0 : doubleArray[int2 - 1]);
					int1 = s_clipper.generatePolygons(double1);
					if (int1 > 0) {
						s_vertices.clear();
						int3 = s_clipper.triangulate(0, s_vertices);
						WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = new WorldMapGeometry.TrianglesPerZoom();
						trianglesPerZoom.m_triangles = new float[int3 * 2];
						trianglesPerZoom.m_delta = doubleArray[int2];
						for (int int4 = 0; int4 < int3; ++int4) {
							trianglesPerZoom.m_triangles[int4 * 2] = s_vertices.getFloat();
							trianglesPerZoom.m_triangles[int4 * 2 + 1] = s_vertices.getFloat();
						}

						if (this.m_trianglesPerZoom == null) {
							this.m_trianglesPerZoom = new ArrayList();
						}

						this.m_trianglesPerZoom.add(trianglesPerZoom);
					}
				}
			}
		}
	}

	WorldMapGeometry.TrianglesPerZoom findTriangles(double double1) {
		if (this.m_trianglesPerZoom == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < this.m_trianglesPerZoom.size(); ++int1) {
				WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = (WorldMapGeometry.TrianglesPerZoom)this.m_trianglesPerZoom.get(int1);
				if (trianglesPerZoom.m_delta == double1) {
					return trianglesPerZoom;
				}
			}

			return null;
		}
	}

	public void dispose() {
		this.m_points.clear();
		this.m_triangles = null;
	}

	float isLeft(float float1, float float2, float float3, float float4, float float5, float float6) {
		return (float3 - float1) * (float6 - float2) - (float5 - float1) * (float4 - float2);
	}

	WorldMapGeometry.PolygonHit isPointInPolygon_WindingNumber(float float1, float float2, int int1) {
		int int2 = 0;
		WorldMapPoints worldMapPoints = (WorldMapPoints)this.m_points.get(0);
		for (int int3 = 0; int3 < worldMapPoints.numPoints(); ++int3) {
			int int4 = worldMapPoints.getX(int3);
			int int5 = worldMapPoints.getY(int3);
			int int6 = worldMapPoints.getX((int3 + 1) % worldMapPoints.numPoints());
			int int7 = worldMapPoints.getY((int3 + 1) % worldMapPoints.numPoints());
			if ((float)int5 <= float2) {
				if ((float)int7 > float2 && this.isLeft((float)int4, (float)int5, (float)int6, (float)int7, float1, float2) > 0.0F) {
					++int2;
				}
			} else if ((float)int7 <= float2 && this.isLeft((float)int4, (float)int5, (float)int6, (float)int7, float1, float2) < 0.0F) {
				--int2;
			}
		}

		return int2 == 0 ? WorldMapGeometry.PolygonHit.Outside : WorldMapGeometry.PolygonHit.Inside;
	}

	public static enum Type {

		LineString,
		Point,
		Polygon;

		private static WorldMapGeometry.Type[] $values() {
			return new WorldMapGeometry.Type[]{LineString, Point, Polygon};
		}
	}
	private static enum PolygonHit {

		OnEdge,
		Inside,
		Outside;

		private static WorldMapGeometry.PolygonHit[] $values() {
			return new WorldMapGeometry.PolygonHit[]{OnEdge, Inside, Outside};
		}
	}

	public static final class TrianglesPerZoom {
		public float[] m_triangles;
		double m_delta;
	}
}
