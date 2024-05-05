package zombie.worldMap;

import gnu.trove.list.array.TIntArrayList;
import zombie.core.math.PZMath;


public final class WorldMapPoints extends TIntArrayList {
	int m_minX;
	int m_minY;
	int m_maxX;
	int m_maxY;

	public int numPoints() {
		return this.size() / 2;
	}

	public int getX(int int1) {
		return this.get(int1 * 2);
	}

	public int getY(int int1) {
		return this.get(int1 * 2 + 1);
	}

	public void calculateBounds() {
		this.m_minX = this.m_minY = Integer.MAX_VALUE;
		this.m_maxX = this.m_maxY = Integer.MIN_VALUE;
		for (int int1 = 0; int1 < this.numPoints(); ++int1) {
			int int2 = this.getX(int1);
			int int3 = this.getY(int1);
			this.m_minX = PZMath.min(this.m_minX, int2);
			this.m_minY = PZMath.min(this.m_minY, int3);
			this.m_maxX = PZMath.max(this.m_maxX, int2);
			this.m_maxY = PZMath.max(this.m_maxY, int3);
		}
	}

	public boolean isClockwise() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.numPoints(); ++int1) {
			int int2 = this.getX(int1);
			int int3 = this.getY(int1);
			int int4 = this.getX((int1 + 1) % this.numPoints());
			int int5 = this.getY((int1 + 1) % this.numPoints());
			float1 += (float)((int4 - int2) * (int5 + int3));
		}

		return (double)float1 > 0.0;
	}
}
