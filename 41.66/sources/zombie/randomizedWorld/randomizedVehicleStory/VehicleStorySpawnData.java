package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;


public final class VehicleStorySpawnData {
	public RandomizedVehicleStoryBase m_story;
	public IsoMetaGrid.Zone m_zone;
	public float m_spawnX;
	public float m_spawnY;
	public float m_direction;
	public int m_x1;
	public int m_y1;
	public int m_x2;
	public int m_y2;

	public VehicleStorySpawnData(RandomizedVehicleStoryBase randomizedVehicleStoryBase, IsoMetaGrid.Zone zone, float float1, float float2, float float3, int int1, int int2, int int3, int int4) {
		this.m_story = randomizedVehicleStoryBase;
		this.m_zone = zone;
		this.m_spawnX = float1;
		this.m_spawnY = float2;
		this.m_direction = float3;
		this.m_x1 = int1;
		this.m_y1 = int2;
		this.m_x2 = int3;
		this.m_y2 = int4;
	}

	public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk) {
		if (zone != this.m_zone) {
			return false;
		} else if (!this.m_story.isFullyStreamedIn(this.m_x1, this.m_y1, this.m_x2, this.m_y2)) {
			return false;
		} else {
			chunk.setRandomVehicleStoryToSpawnLater((VehicleStorySpawnData)null);
			return this.m_story.isValid(zone, chunk, false);
		}
	}
}
