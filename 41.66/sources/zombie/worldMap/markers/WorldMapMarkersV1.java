package zombie.worldMap.markers;

import java.util.ArrayList;
import java.util.Objects;
import zombie.Lua.LuaManager;
import zombie.worldMap.UIWorldMap;


public class WorldMapMarkersV1 {
	private final UIWorldMap m_ui;
	private final ArrayList m_markers = new ArrayList();

	public WorldMapMarkersV1(UIWorldMap uIWorldMap) {
		Objects.requireNonNull(uIWorldMap);
		this.m_ui = uIWorldMap;
	}

	public WorldMapMarkersV1.WorldMapGridSquareMarkerV1 addGridSquareMarker(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		WorldMapGridSquareMarker worldMapGridSquareMarker = this.m_ui.getAPIv1().getMarkers().addGridSquareMarker(int1, int2, int3, float1, float2, float3, float4);
		WorldMapMarkersV1.WorldMapGridSquareMarkerV1 worldMapGridSquareMarkerV1 = new WorldMapMarkersV1.WorldMapGridSquareMarkerV1(worldMapGridSquareMarker);
		this.m_markers.add(worldMapGridSquareMarkerV1);
		return worldMapGridSquareMarkerV1;
	}

	public void removeMarker(WorldMapMarkersV1.WorldMapMarkerV1 worldMapMarkerV1) {
		if (this.m_markers.remove(worldMapMarkerV1)) {
			this.m_ui.getAPIv1().getMarkers().removeMarker(worldMapMarkerV1.m_marker);
		}
	}

	public void clear() {
		this.m_ui.getAPIv1().getMarkers().clear();
		this.m_markers.clear();
	}

	public static void setExposed(LuaManager.Exposer exposer) {
		exposer.setExposed(WorldMapMarkersV1.class);
		exposer.setExposed(WorldMapMarkersV1.WorldMapMarkerV1.class);
		exposer.setExposed(WorldMapMarkersV1.WorldMapGridSquareMarkerV1.class);
	}

	public static final class WorldMapGridSquareMarkerV1 extends WorldMapMarkersV1.WorldMapMarkerV1 {
		final WorldMapGridSquareMarker m_gridSquareMarker;

		WorldMapGridSquareMarkerV1(WorldMapGridSquareMarker worldMapGridSquareMarker) {
			super(worldMapGridSquareMarker);
			this.m_gridSquareMarker = worldMapGridSquareMarker;
		}

		public void setBlink(boolean boolean1) {
			this.m_gridSquareMarker.setBlink(boolean1);
		}

		public void setMinScreenRadius(int int1) {
			this.m_gridSquareMarker.setMinScreenRadius(int1);
		}
	}

	public static class WorldMapMarkerV1 {
		final WorldMapMarker m_marker;

		WorldMapMarkerV1(WorldMapMarker worldMapMarker) {
			this.m_marker = worldMapMarker;
		}
	}
}
