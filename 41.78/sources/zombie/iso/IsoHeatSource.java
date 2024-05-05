package zombie.iso;

import zombie.characters.IsoPlayer;


public class IsoHeatSource {
	private int x;
	private int y;
	private int z;
	private int radius;
	private int temperature;

	public IsoHeatSource(int int1, int int2, int int3, int int4, int int5) {
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.radius = int4;
		this.temperature = int5;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public int getRadius() {
		return this.radius;
	}

	public void setRadius(int int1) {
		this.radius = int1;
	}

	public int getTemperature() {
		return this.temperature;
	}

	public void setTemperature(int int1) {
		this.temperature = int1;
	}

	public boolean isInBounds(int int1, int int2, int int3, int int4) {
		return this.x >= int1 && this.x < int3 && this.y >= int2 && this.y < int4;
	}

	public boolean isInBounds() {
		IsoChunkMap[] chunkMapArray = IsoWorld.instance.CurrentCell.ChunkMap;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (!chunkMapArray[int1].ignore) {
				int int2 = chunkMapArray[int1].getWorldXMinTiles();
				int int3 = chunkMapArray[int1].getWorldXMaxTiles();
				int int4 = chunkMapArray[int1].getWorldYMinTiles();
				int int5 = chunkMapArray[int1].getWorldYMaxTiles();
				if (this.isInBounds(int2, int4, int3, int5)) {
					return true;
				}
			}
		}

		return false;
	}
}
