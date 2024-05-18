package org.joml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PolygonsIntersection {
	private static final PolygonsIntersection.ByStartComparator byStartComparator = new PolygonsIntersection.ByStartComparator();
	private static final PolygonsIntersection.ByEndComparator byEndComparator = new PolygonsIntersection.ByEndComparator();
	protected final float[] verticesXY;
	private float minX;
	private float minY;
	private float maxX;
	private float maxY;
	private float centerX;
	private float centerY;
	private float radiusSquared;
	private PolygonsIntersection.IntervalTreeNode tree;

	public PolygonsIntersection(float[] floatArray, int[] intArray, int int1) {
		this.verticesXY = floatArray;
		this.preprocess(int1, intArray);
	}

	private PolygonsIntersection.IntervalTreeNode buildNode(List list, float float1) {
		ArrayList arrayList = null;
		ArrayList arrayList2 = null;
		ArrayList arrayList3 = null;
		ArrayList arrayList4 = null;
		float float2 = 1.0E38F;
		float float3 = -1.0E38F;
		float float4 = 1.0E38F;
		float float5 = -1.0E38F;
		float float6 = 1.0E38F;
		float float7 = -1.0E38F;
		for (int int1 = 0; int1 < list.size(); ++int1) {
			PolygonsIntersection.Interval interval = (PolygonsIntersection.Interval)list.get(int1);
			if (interval.start < float1 && interval.end < float1) {
				if (arrayList == null) {
					arrayList = new ArrayList();
				}

				arrayList.add(interval);
				float2 = float2 < interval.start ? float2 : interval.start;
				float3 = float3 > interval.end ? float3 : interval.end;
			} else if (interval.start > float1 && interval.end > float1) {
				if (arrayList2 == null) {
					arrayList2 = new ArrayList();
				}

				arrayList2.add(interval);
				float4 = float4 < interval.start ? float4 : interval.start;
				float5 = float5 > interval.end ? float5 : interval.end;
			} else {
				if (arrayList3 == null || arrayList4 == null) {
					arrayList3 = new ArrayList();
					arrayList4 = new ArrayList();
				}

				float6 = interval.start < float6 ? interval.start : float6;
				float7 = interval.end > float7 ? interval.end : float7;
				arrayList3.add(interval);
				arrayList4.add(interval);
			}
		}

		if (arrayList3 != null) {
			Collections.sort(arrayList3, byStartComparator);
			Collections.sort(arrayList4, byEndComparator);
		}

		PolygonsIntersection.IntervalTreeNode intervalTreeNode = new PolygonsIntersection.IntervalTreeNode();
		intervalTreeNode.byBeginning = arrayList3;
		intervalTreeNode.byEnding = arrayList4;
		intervalTreeNode.center = float1;
		if (arrayList != null) {
			intervalTreeNode.left = this.buildNode(arrayList, (float2 + float3) / 2.0F);
			intervalTreeNode.left.childrenMinMax = float3;
		}

		if (arrayList2 != null) {
			intervalTreeNode.right = this.buildNode(arrayList2, (float4 + float5) / 2.0F);
			intervalTreeNode.right.childrenMinMax = float4;
		}

		return intervalTreeNode;
	}

	private void preprocess(int int1, int[] intArray) {
		int int2 = 0;
		this.minX = this.minY = 1.0E38F;
		this.maxX = this.maxY = -1.0E38F;
		ArrayList arrayList = new ArrayList(int1);
		int int3 = 0;
		int int4 = 0;
		int int5;
		float float1;
		float float2;
		PolygonsIntersection.Interval interval;
		float float3;
		for (int5 = 1; int5 < int1; int2 = int5++) {
			if (intArray != null && intArray.length > int4 && intArray[int4] == int5) {
				float1 = this.verticesXY[2 * (int5 - 1) + 1];
				float2 = this.verticesXY[2 * int3 + 1];
				PolygonsIntersection.Interval interval2 = new PolygonsIntersection.Interval();
				interval2.start = float1 < float2 ? float1 : float2;
				interval2.end = float2 > float1 ? float2 : float1;
				interval2.i = int5 - 1;
				interval2.j = int3;
				interval2.polyIndex = int4;
				arrayList.add(interval2);
				int3 = int5;
				++int4;
				++int5;
				int2 = int5 - 1;
			}

			float1 = this.verticesXY[2 * int5 + 1];
			float2 = this.verticesXY[2 * int5 + 0];
			float3 = this.verticesXY[2 * int2 + 1];
			this.minX = float2 < this.minX ? float2 : this.minX;
			this.minY = float1 < this.minY ? float1 : this.minY;
			this.maxX = float2 > this.maxX ? float2 : this.maxX;
			this.maxY = float1 > this.maxY ? float1 : this.maxY;
			interval = new PolygonsIntersection.Interval();
			interval.start = float1 < float3 ? float1 : float3;
			interval.end = float3 > float1 ? float3 : float1;
			interval.i = int5;
			interval.j = int2;
			interval.polyIndex = int4;
			arrayList.add(interval);
		}

		float1 = this.verticesXY[2 * (int5 - 1) + 1];
		float2 = this.verticesXY[2 * (int5 - 1) + 0];
		float3 = this.verticesXY[2 * int3 + 1];
		this.minX = float2 < this.minX ? float2 : this.minX;
		this.minY = float1 < this.minY ? float1 : this.minY;
		this.maxX = float2 > this.maxX ? float2 : this.maxX;
		this.maxY = float1 > this.maxY ? float1 : this.maxY;
		interval = new PolygonsIntersection.Interval();
		interval.start = float1 < float3 ? float1 : float3;
		interval.end = float3 > float1 ? float3 : float1;
		interval.i = int5 - 1;
		interval.j = int3;
		interval.polyIndex = int4;
		arrayList.add(interval);
		this.centerX = (this.maxX + this.minX) * 0.5F;
		this.centerY = (this.maxY + this.minY) * 0.5F;
		float float4 = this.maxX - this.centerX;
		float float5 = this.maxY - this.centerY;
		this.radiusSquared = float4 * float4 + float5 * float5;
		this.tree = this.buildNode(arrayList, this.centerY);
	}

	public boolean testPoint(float float1, float float2) {
		return this.testPoint(float1, float2, (BitSet)null);
	}

	public boolean testPoint(float float1, float float2, BitSet bitSet) {
		float float3 = float1 - this.centerX;
		float float4 = float2 - this.centerY;
		if (bitSet != null) {
			bitSet.clear();
		}

		if (float3 * float3 + float4 * float4 > this.radiusSquared) {
			return false;
		} else if (!(this.maxX < float1) && !(this.maxY < float2) && !(this.minX > float1) && !(this.minY > float2)) {
			boolean boolean1 = this.tree.traverse(this.verticesXY, float1, float2, false, bitSet);
			return boolean1;
		} else {
			return false;
		}
	}

	static class IntervalTreeNode {
		float center;
		float childrenMinMax;
		PolygonsIntersection.IntervalTreeNode left;
		PolygonsIntersection.IntervalTreeNode right;
		List byBeginning;
		List byEnding;

		static boolean computeEvenOdd(float[] floatArray, PolygonsIntersection.Interval interval, float float1, float float2, boolean boolean1, BitSet bitSet) {
			boolean boolean2 = boolean1;
			int int1 = interval.i;
			int int2 = interval.j;
			float float3 = floatArray[2 * int1 + 1];
			float float4 = floatArray[2 * int2 + 1];
			float float5 = floatArray[2 * int1 + 0];
			float float6 = floatArray[2 * int2 + 0];
			if ((float3 < float2 && float4 >= float2 || float4 < float2 && float3 >= float2) && (float5 <= float1 || float6 <= float1)) {
				float float7 = float5 + (float2 - float3) / (float4 - float3) * (float6 - float5) - float1;
				boolean2 = boolean1 ^ float7 < 0.0F;
				if (boolean2 != boolean1 && bitSet != null) {
					bitSet.flip(interval.polyIndex);
				}
			}

			return boolean2;
		}

		boolean traverse(float[] floatArray, float float1, float float2, boolean boolean1, BitSet bitSet) {
			boolean boolean2 = boolean1;
			int int1;
			int int2;
			PolygonsIntersection.Interval interval;
			if (float2 == this.center && this.byBeginning != null) {
				int1 = this.byBeginning.size();
				for (int2 = 0; int2 < int1; ++int2) {
					interval = (PolygonsIntersection.Interval)this.byBeginning.get(int2);
					boolean2 = computeEvenOdd(floatArray, interval, float1, float2, boolean2, bitSet);
				}
			} else if (float2 < this.center) {
				if (this.left != null && this.left.childrenMinMax >= float2) {
					boolean2 = this.left.traverse(floatArray, float1, float2, boolean1, bitSet);
				}

				if (this.byBeginning != null) {
					int1 = this.byBeginning.size();
					for (int2 = 0; int2 < int1; ++int2) {
						interval = (PolygonsIntersection.Interval)this.byBeginning.get(int2);
						if (interval.start > float2) {
							break;
						}

						boolean2 = computeEvenOdd(floatArray, interval, float1, float2, boolean2, bitSet);
					}
				}
			} else if (float2 > this.center) {
				if (this.right != null && this.right.childrenMinMax <= float2) {
					boolean2 = this.right.traverse(floatArray, float1, float2, boolean1, bitSet);
				}

				if (this.byEnding != null) {
					int1 = this.byEnding.size();
					for (int2 = 0; int2 < int1; ++int2) {
						interval = (PolygonsIntersection.Interval)this.byEnding.get(int2);
						if (interval.end < float2) {
							break;
						}

						boolean2 = computeEvenOdd(floatArray, interval, float1, float2, boolean2, bitSet);
					}
				}
			}

			return boolean2;
		}
	}

	static class Interval {
		float start;
		float end;
		int i;
		int j;
		int polyIndex;
	}

	static class ByEndComparator implements Comparator {

		public int compare(Object object, Object object2) {
			PolygonsIntersection.Interval interval = (PolygonsIntersection.Interval)object;
			PolygonsIntersection.Interval interval2 = (PolygonsIntersection.Interval)object2;
			return Float.compare(interval2.end, interval.end);
		}
	}

	static class ByStartComparator implements Comparator {

		public int compare(Object object, Object object2) {
			PolygonsIntersection.Interval interval = (PolygonsIntersection.Interval)object;
			PolygonsIntersection.Interval interval2 = (PolygonsIntersection.Interval)object2;
			return Float.compare(interval.start, interval2.start);
		}
	}
}
