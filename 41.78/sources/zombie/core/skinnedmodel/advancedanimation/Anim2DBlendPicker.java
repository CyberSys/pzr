package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;


public final class Anim2DBlendPicker {
	private List m_tris;
	private List m_hull;
	private Anim2DBlendPicker.HullComparer m_hullComparer;

	public void SetPickTriangles(List list) {
		this.m_tris = list;
		this.BuildHull();
	}

	private void BuildHull() {
		HashMap hashMap = new HashMap();
		Anim2DBlendPicker.Counter counter = new Anim2DBlendPicker.Counter();
		Anim2DBlendPicker.Counter counter2;
		for (Iterator iterator = this.m_tris.iterator(); iterator.hasNext(); counter2.Increment()) {
			Anim2DBlendTriangle anim2DBlendTriangle = (Anim2DBlendTriangle)iterator.next();
			counter2 = (Anim2DBlendPicker.Counter)hashMap.putIfAbsent(new Anim2DBlendPicker.Edge(anim2DBlendTriangle.node1, anim2DBlendTriangle.node2), counter);
			if (counter2 == null) {
				counter2 = counter;
				counter = new Anim2DBlendPicker.Counter();
			}

			counter2.Increment();
			counter2 = (Anim2DBlendPicker.Counter)hashMap.putIfAbsent(new Anim2DBlendPicker.Edge(anim2DBlendTriangle.node2, anim2DBlendTriangle.node3), counter);
			if (counter2 == null) {
				counter2 = counter;
				counter = new Anim2DBlendPicker.Counter();
			}

			counter2.Increment();
			counter2 = (Anim2DBlendPicker.Counter)hashMap.putIfAbsent(new Anim2DBlendPicker.Edge(anim2DBlendTriangle.node3, anim2DBlendTriangle.node1), counter);
			if (counter2 == null) {
				counter2 = counter;
				counter = new Anim2DBlendPicker.Counter();
			}
		}

		HashSet hashSet = new HashSet();
		hashMap.forEach((hashMapx,counterx)->{
			if (counterx.count == 1) {
				hashSet.add(hashMapx.a);
				hashSet.add(hashMapx.b);
			}
		});
		ArrayList arrayList = new ArrayList(hashSet);
		float float1 = 0.0F;
		float float2 = 0.0F;
		Anim2DBlend anim2DBlend;
		for (Iterator iterator2 = arrayList.iterator(); iterator2.hasNext(); float2 += anim2DBlend.m_YPos) {
			anim2DBlend = (Anim2DBlend)iterator2.next();
			float1 += anim2DBlend.m_XPos;
		}

		float1 /= (float)arrayList.size();
		float2 /= (float)arrayList.size();
		this.m_hullComparer = new Anim2DBlendPicker.HullComparer(float1, float2);
		arrayList.sort(this.m_hullComparer);
		this.m_hull = arrayList;
	}

	static int LowerBoundIdx(List list, Object object, Comparator comparator) {
		int int1 = 0;
		int int2 = list.size();
		while (int1 != int2) {
			int int3 = (int1 + int2) / 2;
			if (comparator.compare(object, list.get(int3)) < 0) {
				int2 = int3;
			} else {
				int1 = int3 + 1;
			}
		}

		return int1;
	}

	private static float ProjectPointToLine(float float1, float float2, float float3, float float4, float float5, float float6) {
		float float7 = float1 - float3;
		float float8 = float2 - float4;
		float float9 = float5 - float3;
		float float10 = float6 - float4;
		return (float9 * float7 + float10 * float8) / (float9 * float9 + float10 * float10);
	}

	public Anim2DBlendPicker.PickResults Pick(float float1, float float2) {
		Anim2DBlendPicker.PickResults pickResults = new Anim2DBlendPicker.PickResults();
		Iterator iterator = this.m_tris.iterator();
		Anim2DBlendTriangle anim2DBlendTriangle;
		float float3;
		do {
			if (!iterator.hasNext()) {
				float1 *= 1.1F;
				float2 *= 1.1F;
				Anim2DBlend anim2DBlend = new Anim2DBlend();
				anim2DBlend.m_XPos = float1;
				anim2DBlend.m_YPos = float2;
				int int1 = LowerBoundIdx(this.m_hull, anim2DBlend, this.m_hullComparer);
				if (int1 == this.m_hull.size()) {
					int1 = 0;
				}

				int int2 = int1 > 0 ? int1 - 1 : this.m_hull.size() - 1;
				Anim2DBlend anim2DBlend2 = (Anim2DBlend)this.m_hull.get(int1);
				Anim2DBlend anim2DBlend3 = (Anim2DBlend)this.m_hull.get(int2);
				float3 = ProjectPointToLine(float1, float2, anim2DBlend2.m_XPos, anim2DBlend2.m_YPos, anim2DBlend3.m_XPos, anim2DBlend3.m_YPos);
				if (float3 < 0.0F) {
					pickResults.numNodes = 1;
					pickResults.node1 = anim2DBlend2;
					pickResults.scale1 = 1.0F;
				} else if (float3 > 1.0F) {
					pickResults.numNodes = 1;
					pickResults.node1 = anim2DBlend3;
					pickResults.scale1 = 1.0F;
				} else {
					pickResults.numNodes = 2;
					pickResults.node1 = anim2DBlend2;
					pickResults.node2 = anim2DBlend3;
					pickResults.scale1 = 1.0F - float3;
					pickResults.scale2 = float3;
				}

				return pickResults;
			}

			anim2DBlendTriangle = (Anim2DBlendTriangle)iterator.next();
		} while (!anim2DBlendTriangle.Contains(float1, float2));

		pickResults.numNodes = 3;
		pickResults.node1 = anim2DBlendTriangle.node1;
		pickResults.node2 = anim2DBlendTriangle.node2;
		pickResults.node3 = anim2DBlendTriangle.node3;
		float float4 = pickResults.node1.m_XPos;
		float float5 = pickResults.node1.m_YPos;
		float float6 = pickResults.node2.m_XPos;
		float3 = pickResults.node2.m_YPos;
		float float7 = pickResults.node3.m_XPos;
		float float8 = pickResults.node3.m_YPos;
		pickResults.scale1 = ((float3 - float8) * (float1 - float7) + (float7 - float6) * (float2 - float8)) / ((float3 - float8) * (float4 - float7) + (float7 - float6) * (float5 - float8));
		pickResults.scale2 = ((float8 - float5) * (float1 - float7) + (float4 - float7) * (float2 - float8)) / ((float3 - float8) * (float4 - float7) + (float7 - float6) * (float5 - float8));
		pickResults.scale3 = 1.0F - pickResults.scale1 - pickResults.scale2;
		return pickResults;
	}

	void render(float float1, float float2) {
		short short1 = 200;
		int int1 = Core.getInstance().getScreenWidth() - short1 - 100;
		int int2 = Core.getInstance().getScreenHeight() - short1 - 100;
		SpriteRenderer.instance.renderi((Texture)null, int1 - 20, int2 - 20, short1 + 40, short1 + 40, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
		for (int int3 = 0; int3 < this.m_tris.size(); ++int3) {
			Anim2DBlendTriangle anim2DBlendTriangle = (Anim2DBlendTriangle)this.m_tris.get(int3);
			SpriteRenderer.instance.renderline((Texture)null, (int)((float)(int1 + short1 / 2) + anim2DBlendTriangle.node1.m_XPos * (float)short1 / 2.0F), (int)((float)(int2 + short1 / 2) - anim2DBlendTriangle.node1.m_YPos * (float)short1 / 2.0F), (int)((float)(int1 + short1 / 2) + anim2DBlendTriangle.node2.m_XPos * (float)short1 / 2.0F), (int)((float)(int2 + short1 / 2) - anim2DBlendTriangle.node2.m_YPos * (float)short1 / 2.0F), 0.5F, 0.5F, 0.5F, 1.0F);
			SpriteRenderer.instance.renderline((Texture)null, (int)((float)(int1 + short1 / 2) + anim2DBlendTriangle.node2.m_XPos * (float)short1 / 2.0F), (int)((float)(int2 + short1 / 2) - anim2DBlendTriangle.node2.m_YPos * (float)short1 / 2.0F), (int)((float)(int1 + short1 / 2) + anim2DBlendTriangle.node3.m_XPos * (float)short1 / 2.0F), (int)((float)(int2 + short1 / 2) - anim2DBlendTriangle.node3.m_YPos * (float)short1 / 2.0F), 0.5F, 0.5F, 0.5F, 1.0F);
			SpriteRenderer.instance.renderline((Texture)null, (int)((float)(int1 + short1 / 2) + anim2DBlendTriangle.node3.m_XPos * (float)short1 / 2.0F), (int)((float)(int2 + short1 / 2) - anim2DBlendTriangle.node3.m_YPos * (float)short1 / 2.0F), (int)((float)(int1 + short1 / 2) + anim2DBlendTriangle.node1.m_XPos * (float)short1 / 2.0F), (int)((float)(int2 + short1 / 2) - anim2DBlendTriangle.node1.m_YPos * (float)short1 / 2.0F), 0.5F, 0.5F, 0.5F, 1.0F);
		}

		float float3 = 8.0F;
		Anim2DBlendPicker.PickResults pickResults = this.Pick(float1, float2);
		if (pickResults.node1 != null) {
			SpriteRenderer.instance.render((Texture)null, (float)(int1 + short1 / 2) + pickResults.node1.m_XPos * (float)short1 / 2.0F - float3 / 2.0F, (float)(int2 + short1 / 2) - pickResults.node1.m_YPos * (float)short1 / 2.0F - float3 / 2.0F, float3, float3, 0.0F, 1.0F, 0.0F, 1.0F, (Consumer)null);
		}

		if (pickResults.node2 != null) {
			SpriteRenderer.instance.render((Texture)null, (float)(int1 + short1 / 2) + pickResults.node2.m_XPos * (float)short1 / 2.0F - float3 / 2.0F, (float)(int2 + short1 / 2) - pickResults.node2.m_YPos * (float)short1 / 2.0F - float3 / 2.0F, float3, float3, 0.0F, 1.0F, 0.0F, 1.0F, (Consumer)null);
		}

		if (pickResults.node3 != null) {
			SpriteRenderer.instance.render((Texture)null, (float)(int1 + short1 / 2) + pickResults.node3.m_XPos * (float)short1 / 2.0F - float3 / 2.0F, (float)(int2 + short1 / 2) - pickResults.node3.m_YPos * (float)short1 / 2.0F - float3 / 2.0F, float3, float3, 0.0F, 1.0F, 0.0F, 1.0F, (Consumer)null);
		}

		float3 = 4.0F;
		SpriteRenderer.instance.render((Texture)null, (float)(int1 + short1 / 2) + float1 * (float)short1 / 2.0F - float3 / 2.0F, (float)(int2 + short1 / 2) - float2 * (float)short1 / 2.0F - float3 / 2.0F, float3, float3, 0.0F, 0.0F, 1.0F, 1.0F, (Consumer)null);
	}

	static class Counter {
		public int count = 0;

		public int Increment() {
			return ++this.count;
		}
	}

	static class Edge {
		public Anim2DBlend a;
		public Anim2DBlend b;

		public Edge(Anim2DBlend anim2DBlend, Anim2DBlend anim2DBlend2) {
			boolean boolean1;
			if (anim2DBlend.m_XPos != anim2DBlend2.m_XPos) {
				boolean1 = anim2DBlend.m_XPos > anim2DBlend2.m_XPos;
			} else {
				boolean1 = anim2DBlend.m_YPos > anim2DBlend2.m_YPos;
			}

			if (boolean1) {
				this.a = anim2DBlend2;
				this.b = anim2DBlend;
			} else {
				this.a = anim2DBlend;
				this.b = anim2DBlend2;
			}
		}

		public int hashCode() {
			int int1 = this.a.hashCode();
			int int2 = this.b.hashCode();
			return (int1 << 5) + int1 ^ int2;
		}

		public boolean equals(Object object) {
			if (!(object instanceof Anim2DBlendPicker.Edge)) {
				return false;
			} else {
				return this.a == ((Anim2DBlendPicker.Edge)object).a && this.b == ((Anim2DBlendPicker.Edge)object).b;
			}
		}
	}

	static class HullComparer implements Comparator {
		private int centerX;
		private int centerY;

		public HullComparer(float float1, float float2) {
			this.centerX = (int)(float1 * 1000.0F);
			this.centerY = (int)(float2 * 1000.0F);
		}

		public boolean isLessThan(Anim2DBlend anim2DBlend, Anim2DBlend anim2DBlend2) {
			int int1 = (int)(anim2DBlend.m_XPos * 1000.0F);
			int int2 = (int)(anim2DBlend.m_YPos * 1000.0F);
			int int3 = (int)(anim2DBlend2.m_XPos * 1000.0F);
			int int4 = (int)(anim2DBlend2.m_YPos * 1000.0F);
			int int5 = int1 - this.centerX;
			int int6 = int2 - this.centerY;
			int int7 = int3 - this.centerX;
			int int8 = int4 - this.centerY;
			if (int6 == 0 && int5 > 0) {
				return true;
			} else if (int8 == 0 && int7 > 0) {
				return false;
			} else if (int6 > 0 && int8 < 0) {
				return true;
			} else if (int6 < 0 && int8 > 0) {
				return false;
			} else {
				int int9 = int5 * int8 - int6 * int7;
				return int9 > 0;
			}
		}

		public int compare(Anim2DBlend anim2DBlend, Anim2DBlend anim2DBlend2) {
			if (this.isLessThan(anim2DBlend, anim2DBlend2)) {
				return -1;
			} else {
				return this.isLessThan(anim2DBlend2, anim2DBlend) ? 1 : 0;
			}
		}
	}

	public static class PickResults {
		public int numNodes;
		public Anim2DBlend node1;
		public Anim2DBlend node2;
		public Anim2DBlend node3;
		public float scale1;
		public float scale2;
		public float scale3;
	}
}
