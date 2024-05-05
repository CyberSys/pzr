package zombie.worldMap;

import org.joml.Vector2d;


public final class MapProjection {
	public static final double EARTH_RADIUS_METERS = 6378137.0;
	public static final double EARTH_HALF_CIRCUMFERENCE_METERS = 2.0037508342789244E7;
	public static final double EARTH_CIRCUMFERENCE_METERS = 4.007501668557849E7;
	public static final double MAX_LATITUDE_DEGREES = 85.05112878;
	private static final double LOG_2 = Math.log(2.0);

	static MapProjection.ProjectedMeters lngLatToProjectedMeters(MapProjection.LngLat lngLat) {
		MapProjection.ProjectedMeters projectedMeters = new MapProjection.ProjectedMeters();
		projectedMeters.x = lngLat.longitude * 2.0037508342789244E7 / 180.0;
		projectedMeters.y = Math.log(Math.tan(0.7853981633974483 + lngLat.latitude * 3.141592653589793 / 360.0)) * 6378137.0;
		return projectedMeters;
	}

	static double metersPerTileAtZoom(int int1) {
		return 4.007501668557849E7 / (double)(1 << int1);
	}

	static double metersPerPixelAtZoom(double double1, double double2) {
		return 4.007501668557849E7 / (exp2(double1) * double2);
	}

	static double zoomAtMetersPerPixel(double double1, double double2) {
		return log2(4.007501668557849E7 / (double1 * double2));
	}

	static MapProjection.BoundingBox mapLngLatBounds() {
		return new MapProjection.BoundingBox(new Vector2d(-180.0, -85.05112878), new Vector2d(180.0, 85.05112878));
	}

	static MapProjection.BoundingBox mapProjectedMetersBounds() {
		MapProjection.BoundingBox boundingBox = mapLngLatBounds();
		return new MapProjection.BoundingBox(lngLatToProjectedMeters(new MapProjection.LngLat(boundingBox.min.x, boundingBox.min.y)), lngLatToProjectedMeters(new MapProjection.LngLat(boundingBox.max.x, boundingBox.max.y)));
	}

	public static double exp2(double double1) {
		return Math.pow(2.0, double1);
	}

	public static double log2(double double1) {
		return Math.log(double1) / LOG_2;
	}

	public static final class ProjectedMeters extends Vector2d {
	}

	public static final class LngLat {
		double longitude = 0.0;
		double latitude = 0.0;

		public LngLat(double double1, double double2) {
			this.longitude = double1;
			this.latitude = double2;
		}
	}

	public static final class BoundingBox {
		Vector2d min;
		Vector2d max;

		public BoundingBox(Vector2d vector2d, Vector2d vector2d2) {
			this.min = vector2d;
			this.max = vector2d2;
		}
	}
}
