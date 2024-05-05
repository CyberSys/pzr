package zombie.worldMap;

import java.util.ArrayList;
import java.util.Iterator;


public final class WorldMapFeature {
	public final WorldMapCell m_cell;
	public final ArrayList m_geometries = new ArrayList();
	public WorldMapProperties m_properties = null;

	WorldMapFeature(WorldMapCell worldMapCell) {
		this.m_cell = worldMapCell;
	}

	public boolean hasLineString() {
		for (int int1 = 0; int1 < this.m_geometries.size(); ++int1) {
			if (((WorldMapGeometry)this.m_geometries.get(int1)).m_type == WorldMapGeometry.Type.LineString) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPoint() {
		for (int int1 = 0; int1 < this.m_geometries.size(); ++int1) {
			if (((WorldMapGeometry)this.m_geometries.get(int1)).m_type == WorldMapGeometry.Type.Point) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPolygon() {
		for (int int1 = 0; int1 < this.m_geometries.size(); ++int1) {
			if (((WorldMapGeometry)this.m_geometries.get(int1)).m_type == WorldMapGeometry.Type.Polygon) {
				return true;
			}
		}

		return false;
	}

	public boolean containsPoint(float float1, float float2) {
		for (int int1 = 0; int1 < this.m_geometries.size(); ++int1) {
			WorldMapGeometry worldMapGeometry = (WorldMapGeometry)this.m_geometries.get(int1);
			if (worldMapGeometry.containsPoint(float1, float2)) {
				return true;
			}
		}

		return false;
	}

	public void dispose() {
		Iterator iterator = this.m_geometries.iterator();
		while (iterator.hasNext()) {
			WorldMapGeometry worldMapGeometry = (WorldMapGeometry)iterator.next();
			worldMapGeometry.dispose();
		}

		this.m_properties.clear();
	}
}
