package zombie.worldMap.markers;

import java.util.ArrayList;
import zombie.util.Pool;
import zombie.worldMap.UIWorldMap;


public final class WorldMapMarkers {
	private static final Pool s_gridSquareMarkerPool = new Pool(WorldMapGridSquareMarker::new);
	private final ArrayList m_markers = new ArrayList();

	public WorldMapGridSquareMarker addGridSquareMarker(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		WorldMapGridSquareMarker worldMapGridSquareMarker = ((WorldMapGridSquareMarker)s_gridSquareMarkerPool.alloc()).init(int1, int2, int3, float1, float2, float3, float4);
		this.m_markers.add(worldMapGridSquareMarker);
		return worldMapGridSquareMarker;
	}

	public void removeMarker(WorldMapMarker worldMapMarker) {
		if (this.m_markers.contains(worldMapMarker)) {
			this.m_markers.remove(worldMapMarker);
			worldMapMarker.release();
		}
	}

	public void clear() {
		for (int int1 = 0; int1 < this.m_markers.size(); ++int1) {
			((WorldMapMarker)this.m_markers.get(int1)).release();
		}

		this.m_markers.clear();
	}

	public void render(UIWorldMap uIWorldMap) {
		for (int int1 = 0; int1 < this.m_markers.size(); ++int1) {
			((WorldMapMarker)this.m_markers.get(int1)).render(uIWorldMap);
		}
	}
}
