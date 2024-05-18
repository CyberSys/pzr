package org.joml.sampling;

import java.util.ArrayList;
import org.joml.Vector2f;


public class PoissonSampling {

	public static class Disk {
		private final Vector2f[] grid;
		private final float diskRadius;
		private final float diskRadiusSquared;
		private final float minDist;
		private final float minDistSquared;
		private final float cellSize;
		private final int numCells;
		private final Random rnd;
		private final ArrayList processList;

		public Disk(long long1, float float1, float float2, int int1, Callback2d callback2d) {
			this.diskRadius = float1;
			this.diskRadiusSquared = float1 * float1;
			this.minDist = float2;
			this.minDistSquared = float2 * float2;
			this.rnd = new Random(long1);
			this.cellSize = float2 / (float)Math.sqrt(2.0);
			this.numCells = (int)(float1 * 2.0F / this.cellSize) + 1;
			this.grid = new Vector2f[this.numCells * this.numCells];
			this.processList = new ArrayList();
			this.compute(int1, callback2d);
		}

		private void compute(int int1, Callback2d callback2d) {
			float float1;
			float float2;
			do {
				float1 = this.rnd.nextFloat() * 2.0F - 1.0F;
				float2 = this.rnd.nextFloat() * 2.0F - 1.0F;
			} while (float1 * float1 + float2 * float2 > 1.0F);

			Vector2f vector2f = new Vector2f(float1, float2);
			this.processList.add(vector2f);
			callback2d.onNewSample(vector2f.x, vector2f.y);
			this.insert(vector2f);
			while (!this.processList.isEmpty()) {
				int int2 = this.rnd.nextInt(this.processList.size());
				Vector2f vector2f2 = (Vector2f)this.processList.get(int2);
				boolean boolean1 = false;
				for (int int3 = 0; int3 < int1; ++int3) {
					float float3 = this.rnd.nextFloat() * 6.2831855F;
					float float4 = this.minDist * (this.rnd.nextFloat() + 1.0F);
					float1 = (float)((double)float4 * Math.sin_roquen_9((double)float3 + 1.5707963267948966));
					float2 = (float)((double)float4 * Math.sin_roquen_9((double)float3));
					float1 += vector2f2.x;
					float2 += vector2f2.y;
					if (!(float1 * float1 + float2 * float2 > this.diskRadiusSquared) && !this.searchNeighbors(float1, float2)) {
						boolean1 = true;
						callback2d.onNewSample(float1, float2);
						Vector2f vector2f3 = new Vector2f(float1, float2);
						this.processList.add(vector2f3);
						this.insert(vector2f3);
						break;
					}
				}

				if (!boolean1) {
					this.processList.remove(int2);
				}
			}
		}

		private boolean searchNeighbors(float float1, float float2) {
			int int1 = (int)((float2 + this.diskRadius) / this.cellSize);
			int int2 = (int)((float1 + this.diskRadius) / this.cellSize);
			if (this.grid[int1 * this.numCells + int2] != null) {
				return true;
			} else {
				int int3 = Math.max(0, int2 - 1);
				int int4 = Math.max(0, int1 - 1);
				int int5 = Math.min(int2 + 1, this.numCells - 1);
				int int6 = Math.min(int1 + 1, this.numCells - 1);
				for (int int7 = int4; int7 <= int6; ++int7) {
					for (int int8 = int3; int8 <= int5; ++int8) {
						Vector2f vector2f = this.grid[int7 * this.numCells + int8];
						if (vector2f != null) {
							float float3 = vector2f.x - float1;
							float float4 = vector2f.y - float2;
							if (float3 * float3 + float4 * float4 < this.minDistSquared) {
								return true;
							}
						}
					}
				}

				return false;
			}
		}

		private void insert(Vector2f vector2f) {
			int int1 = (int)((vector2f.y + this.diskRadius) / this.cellSize);
			int int2 = (int)((vector2f.x + this.diskRadius) / this.cellSize);
			this.grid[int1 * this.numCells + int2] = vector2f;
		}
	}
}
