package zombie.worldMap;

import java.util.ArrayList;
import java.util.Iterator;


public final class WorldMapCell {
	public int m_x;
	public int m_y;
	public final ArrayList m_features = new ArrayList();

	public void hitTest(float float1, float float2, ArrayList arrayList) {
		float1 -= (float)(this.m_x * 300);
		float2 -= (float)(this.m_y * 300);
		for (int int1 = 0; int1 < this.m_features.size(); ++int1) {
			WorldMapFeature worldMapFeature = (WorldMapFeature)this.m_features.get(int1);
			if (worldMapFeature.containsPoint(float1, float2)) {
				arrayList.add(worldMapFeature);
			}
		}
	}

	public void dispose() {
		Iterator iterator = this.m_features.iterator();
		while (iterator.hasNext()) {
			WorldMapFeature worldMapFeature = (WorldMapFeature)iterator.next();
			worldMapFeature.dispose();
		}

		this.m_features.clear();
	}
}
