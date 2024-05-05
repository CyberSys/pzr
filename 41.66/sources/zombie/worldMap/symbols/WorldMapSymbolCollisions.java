package zombie.worldMap.symbols;

import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TFloatArrayList;


public final class WorldMapSymbolCollisions {
	final TFloatArrayList m_boxes = new TFloatArrayList();
	final TByteArrayList m_collide = new TByteArrayList();

	boolean addBox(float float1, float float2, float float3, float float4, boolean boolean1) {
		int int1 = this.m_boxes.size() / 4 - 1;
		int int2 = int1 + 1;
		this.m_boxes.add(float1);
		this.m_boxes.add(float2);
		this.m_boxes.add(float1 + float3);
		this.m_boxes.add(float2 + float4);
		this.m_collide.add((byte)(boolean1 ? 1 : 0));
		if (!boolean1) {
			return false;
		} else {
			for (int int3 = 0; int3 <= int1; ++int3) {
				if (this.isCollision(int3, int2)) {
					float1 += float3 / 2.0F;
					float2 += float4 / 2.0F;
					this.m_boxes.set(int2 * 4, float1 - 3.0F - 1.0F);
					this.m_boxes.set(int2 * 4 + 1, float2 - 3.0F - 1.0F);
					this.m_boxes.set(int2 * 4 + 2, float1 + 3.0F + 1.0F);
					this.m_boxes.set(int2 * 4 + 3, float2 - 3.0F + 1.0F);
					return true;
				}
			}

			return false;
		}
	}

	boolean isCollision(int int1, int int2) {
		if (this.m_collide.getQuick(int1) != 0 && this.m_collide.getQuick(int2) != 0) {
			int1 *= 4;
			int2 *= 4;
			float float1 = this.m_boxes.get(int1);
			float float2 = this.m_boxes.get(int1 + 1);
			float float3 = this.m_boxes.get(int1 + 2);
			float float4 = this.m_boxes.get(int1 + 3);
			float float5 = this.m_boxes.get(int2);
			float float6 = this.m_boxes.get(int2 + 1);
			float float7 = this.m_boxes.get(int2 + 2);
			float float8 = this.m_boxes.get(int2 + 3);
			return float1 < float7 && float3 > float5 && float2 < float8 && float4 > float6;
		} else {
			return false;
		}
	}

	boolean isCollision(int int1) {
		for (int int2 = 0; int2 < this.m_boxes.size() / 4; ++int2) {
			if (int2 != int1 && this.isCollision(int1, int2)) {
				return true;
			}
		}

		return false;
	}
}
