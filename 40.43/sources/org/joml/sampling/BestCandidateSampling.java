package org.joml.sampling;

import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class BestCandidateSampling {

	public static class Quad {
		private final Random rnd;
		private final BestCandidateSampling.QuadTree qtree;

		public Quad(long long1, int int1, int int2, Callback2d callback2d) {
			this.rnd = new Random(long1);
			this.qtree = new BestCandidateSampling.QuadTree(-1.0F, -1.0F, 2.0F);
			this.generate(int1, int2, callback2d);
		}

		private void generate(int int1, int int2, Callback2d callback2d) {
			for (int int3 = 0; int3 < int1; ++int3) {
				float float1 = 0.0F;
				float float2 = 0.0F;
				float float3 = 0.0F;
				for (int int4 = 0; int4 < int2; ++int4) {
					float float4 = this.rnd.nextFloat() * 2.0F - 1.0F;
					float float5 = this.rnd.nextFloat() * 2.0F - 1.0F;
					float float6 = this.qtree.nearest(float4, float5, Float.POSITIVE_INFINITY);
					if (float6 > float3) {
						float3 = float6;
						float1 = float4;
						float2 = float5;
					}
				}

				callback2d.onNewSample(float1, float2);
				this.qtree.insert(new Vector2f(float1, float2));
			}
		}
	}

	public static class Disk {
		private final Random rnd;
		private final BestCandidateSampling.QuadTree qtree;

		public Disk(long long1, int int1, int int2, Callback2d callback2d) {
			this.rnd = new Random(long1);
			this.qtree = new BestCandidateSampling.QuadTree(-1.0F, -1.0F, 2.0F);
			this.generate(int1, int2, callback2d);
		}

		private void generate(int int1, int int2, Callback2d callback2d) {
			for (int int3 = 0; int3 < int1; ++int3) {
				float float1 = 0.0F;
				float float2 = 0.0F;
				float float3 = 0.0F;
				for (int int4 = 0; int4 < int2; ++int4) {
					float float4;
					float float5;
					do {
						float4 = this.rnd.nextFloat() * 2.0F - 1.0F;
						float5 = this.rnd.nextFloat() * 2.0F - 1.0F;
					}		 while (float4 * float4 + float5 * float5 > 1.0F);

					float float6 = this.qtree.nearest(float4, float5, Float.POSITIVE_INFINITY);
					if (float6 > float3) {
						float3 = float6;
						float1 = float4;
						float2 = float5;
					}
				}

				callback2d.onNewSample(float1, float2);
				this.qtree.insert(new Vector2f(float1, float2));
			}
		}
	}

	private static class QuadTree {
		private static final int MAX_OBJECTS_PER_NODE = 32;
		private static final int PXNY = 0;
		private static final int NXNY = 1;
		private static final int NXPY = 2;
		private static final int PXPY = 3;
		private float minX;
		private float minY;
		private float hs;
		private ArrayList objects;
		private BestCandidateSampling.QuadTree[] children;

		QuadTree(float float1, float float2, float float3) {
			this.minX = float1;
			this.minY = float2;
			this.hs = float3 * 0.5F;
		}

		private void split() {
			this.children = new BestCandidateSampling.QuadTree[4];
			this.children[1] = new BestCandidateSampling.QuadTree(this.minX, this.minY, this.hs);
			this.children[0] = new BestCandidateSampling.QuadTree(this.minX + this.hs, this.minY, this.hs);
			this.children[2] = new BestCandidateSampling.QuadTree(this.minX, this.minY + this.hs, this.hs);
			this.children[3] = new BestCandidateSampling.QuadTree(this.minX + this.hs, this.minY + this.hs, this.hs);
		}

		private void insertIntoChild(Vector2f vector2f) {
			float float1 = this.minX + this.hs;
			float float2 = this.minY + this.hs;
			if (vector2f.x >= float1) {
				if (vector2f.y >= float2) {
					this.children[3].insert(vector2f);
				} else {
					this.children[0].insert(vector2f);
				}
			} else if (vector2f.y >= float2) {
				this.children[2].insert(vector2f);
			} else {
				this.children[1].insert(vector2f);
			}
		}

		void insert(Vector2f vector2f) {
			if (this.children != null) {
				this.insertIntoChild(vector2f);
			} else {
				if (this.objects != null && this.objects.size() == 32) {
					this.split();
					for (int int1 = 0; int1 < this.objects.size(); ++int1) {
						this.insertIntoChild((Vector2f)this.objects.get(int1));
					}

					this.objects = null;
					this.insertIntoChild(vector2f);
				} else {
					if (this.objects == null) {
						this.objects = new ArrayList(32);
					}

					this.objects.add(vector2f);
				}
			}
		}

		private int quadrant(float float1, float float2) {
			if (float1 < this.minX + this.hs) {
				return float2 < this.minY + this.hs ? 1 : 2;
			} else {
				return float2 < this.minY + this.hs ? 0 : 3;
			}
		}

		float nearest(float float1, float float2, float float3) {
			float float4 = float3;
			if (!(float1 < this.minX - float3) && !(float1 > this.minX + this.hs * 2.0F + float3) && !(float2 < this.minY - float3) && !(float2 > this.minY + this.hs * 2.0F + float3)) {
				int int1;
				if (this.children != null) {
					int int2 = this.quadrant(float1, float2);
					for (int1 = 0; int1 < 4; ++int1) {
						float float5 = this.children[int2].nearest(float1, float2, float4);
						float4 = Math.min(float5, float4);
						int2 = int2 + 1 & 3;
					}

					return float4;
				} else {
					float float6 = float3 * float3;
					for (int1 = 0; this.objects != null && int1 < this.objects.size(); ++int1) {
						Vector2f vector2f = (Vector2f)this.objects.get(int1);
						float float7 = vector2f.distanceSquared(float1, float2);
						if (float7 < float6) {
							float6 = float7;
						}
					}

					return (float)Math.sqrt((double)float6);
				}
			} else {
				return float3;
			}
		}
	}

	public static class Sphere {
		private final Random rnd;
		private final BestCandidateSampling.Sphere.Node otree;

		public Sphere(long long1, int int1, int int2, Callback3d callback3d) {
			this.rnd = new Random(long1);
			this.otree = new BestCandidateSampling.Sphere.Node();
			this.compute(int1, int2, callback3d);
		}

		private void compute(int int1, int int2, Callback3d callback3d) {
			for (int int3 = 0; int3 < int1; ++int3) {
				float float1 = 0.0F;
				float float2 = 0.0F;
				float float3 = 0.0F;
				float float4 = 0.0F;
				for (int int4 = 0; int4 < int2; ++int4) {
					float float5;
					float float6;
					do {
						float5 = this.rnd.nextFloat() * 2.0F - 1.0F;
						float6 = this.rnd.nextFloat() * 2.0F - 1.0F;
					}		 while (float5 * float5 + float6 * float6 > 1.0F);

					float float7 = (float)Math.sqrt(1.0 - (double)(float5 * float5) - (double)(float6 * float6));
					float float8 = 2.0F * float5 * float7;
					float float9 = 2.0F * float6 * float7;
					float float10 = 1.0F - 2.0F * (float5 * float5 + float6 * float6);
					float float11 = this.otree.nearest(float8, float9, float10, Float.POSITIVE_INFINITY);
					if (float11 > float4) {
						float4 = float11;
						float1 = float8;
						float2 = float9;
						float3 = float10;
					}
				}

				callback3d.onNewSample(float1, float2, float3);
				this.otree.insert(new Vector3f(float1, float2, float3));
			}
		}

		private static final class Node {
			private static final int MAX_OBJECTS_PER_NODE = 32;
			private float v0x;
			private float v0y;
			private float v0z;
			private float v1x;
			private float v1y;
			private float v1z;
			private float v2x;
			private float v2y;
			private float v2z;
			private float cx;
			private float cy;
			private float cz;
			private float arc;
			private ArrayList objects;
			private BestCandidateSampling.Sphere.Node[] children;

			Node() {
				this.children = new BestCandidateSampling.Sphere.Node[8];
				float float1 = 1.0F;
				this.arc = 6.2831855F;
				this.children[0] = new BestCandidateSampling.Sphere.Node(-float1, 0.0F, 0.0F, 0.0F, 0.0F, float1, 0.0F, float1, 0.0F);
				this.children[1] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, float1, float1, 0.0F, 0.0F, 0.0F, float1, 0.0F);
				this.children[2] = new BestCandidateSampling.Sphere.Node(float1, 0.0F, 0.0F, 0.0F, 0.0F, -float1, 0.0F, float1, 0.0F);
				this.children[3] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, -float1, -float1, 0.0F, 0.0F, 0.0F, float1, 0.0F);
				this.children[4] = new BestCandidateSampling.Sphere.Node(-float1, 0.0F, 0.0F, 0.0F, -float1, 0.0F, 0.0F, 0.0F, float1);
				this.children[5] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, float1, 0.0F, -float1, 0.0F, float1, 0.0F, 0.0F);
				this.children[6] = new BestCandidateSampling.Sphere.Node(float1, 0.0F, 0.0F, 0.0F, -float1, 0.0F, 0.0F, 0.0F, -float1);
				this.children[7] = new BestCandidateSampling.Sphere.Node(0.0F, 0.0F, -float1, 0.0F, -float1, 0.0F, -float1, 0.0F, 0.0F);
			}

			private Node(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
				this.v0x = float1;
				this.v0y = float2;
				this.v0z = float3;
				this.v1x = float4;
				this.v1y = float5;
				this.v1z = float6;
				this.v2x = float7;
				this.v2y = float8;
				this.v2z = float9;
				this.cx = (this.v0x + this.v1x + this.v2x) / 3.0F;
				this.cy = (this.v0y + this.v1y + this.v2y) / 3.0F;
				this.cz = (this.v0z + this.v1z + this.v2z) / 3.0F;
				float float10 = 1.0F / (float)Math.sqrt((double)(this.cx * this.cx + this.cy * this.cy + this.cz * this.cz));
				this.cx *= float10;
				this.cy *= float10;
				this.cz *= float10;
				float float11 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v0x, this.v0y, this.v0z);
				float float12 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v1x, this.v1y, this.v1z);
				float float13 = this.greatCircleDist(this.cx, this.cy, this.cz, this.v2x, this.v2y, this.v2z);
				float float14 = Math.max(Math.max(float11, float12), float13);
				float14 *= 1.7F;
				this.arc = float14;
			}

			private void split() {
				float float1 = this.v1x + this.v2x;
				float float2 = this.v1y + this.v2y;
				float float3 = this.v1z + this.v2z;
				float float4 = 1.0F / (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
				float1 *= float4;
				float2 *= float4;
				float3 *= float4;
				float float5 = this.v0x + this.v2x;
				float float6 = this.v0y + this.v2y;
				float float7 = this.v0z + this.v2z;
				float float8 = 1.0F / (float)Math.sqrt((double)(float5 * float5 + float6 * float6 + float7 * float7));
				float5 *= float8;
				float6 *= float8;
				float7 *= float8;
				float float9 = this.v0x + this.v1x;
				float float10 = this.v0y + this.v1y;
				float float11 = this.v0z + this.v1z;
				float float12 = 1.0F / (float)Math.sqrt((double)(float9 * float9 + float10 * float10 + float11 * float11));
				float9 *= float12;
				float10 *= float12;
				float11 *= float12;
				this.children = new BestCandidateSampling.Sphere.Node[4];
				this.children[0] = new BestCandidateSampling.Sphere.Node(this.v0x, this.v0y, this.v0z, float9, float10, float11, float5, float6, float7);
				this.children[1] = new BestCandidateSampling.Sphere.Node(this.v1x, this.v1y, this.v1z, float1, float2, float3, float9, float10, float11);
				this.children[2] = new BestCandidateSampling.Sphere.Node(this.v2x, this.v2y, this.v2z, float5, float6, float7, float1, float2, float3);
				this.children[3] = new BestCandidateSampling.Sphere.Node(float1, float2, float3, float5, float6, float7, float9, float10, float11);
			}

			private void insertIntoChild(Vector3f vector3f) {
				for (int int1 = 0; int1 < this.children.length; ++int1) {
					BestCandidateSampling.Sphere.Node node = this.children[int1];
					if (isPointOnSphericalTriangle(vector3f.x, vector3f.y, vector3f.z, node.v0x, node.v0y, node.v0z, node.v1x, node.v1y, node.v1z, node.v2x, node.v2y, node.v2z, 1.0E-6F)) {
						node.insert(vector3f);
						return;
					}
				}
			}

			void insert(Vector3f vector3f) {
				if (this.children != null) {
					this.insertIntoChild(vector3f);
				} else {
					if (this.objects != null && this.objects.size() == 32) {
						this.split();
						for (int int1 = 0; int1 < 32; ++int1) {
							this.insertIntoChild((Vector3f)this.objects.get(int1));
						}

						this.objects = null;
						this.insertIntoChild(vector3f);
					} else {
						if (this.objects == null) {
							this.objects = new ArrayList(32);
						}

						this.objects.add(vector3f);
					}
				}
			}

			private static boolean isPointOnSphericalTriangle(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13) {
				float float14 = float7 - float4;
				float float15 = float8 - float5;
				float float16 = float9 - float6;
				float float17 = float10 - float4;
				float float18 = float11 - float5;
				float float19 = float12 - float6;
				float float20 = float2 * float19 - float3 * float18;
				float float21 = float3 * float17 - float1 * float19;
				float float22 = float1 * float18 - float2 * float17;
				float float23 = float14 * float20 + float15 * float21 + float16 * float22;
				if (float23 > -float13 && float23 < float13) {
					return false;
				} else {
					float float24 = -float4;
					float float25 = -float5;
					float float26 = -float6;
					float float27 = 1.0F / float23;
					float float28 = (float24 * float20 + float25 * float21 + float26 * float22) * float27;
					if (!(float28 < 0.0F) && !(float28 > 1.0F)) {
						float float29 = float25 * float16 - float26 * float15;
						float float30 = float26 * float14 - float24 * float16;
						float float31 = float24 * float15 - float25 * float14;
						float float32 = (float1 * float29 + float2 * float30 + float3 * float31) * float27;
						if (!(float32 < 0.0F) && !(float28 + float32 > 1.0F)) {
							float float33 = (float17 * float29 + float18 * float30 + float19 * float31) * float27;
							return float33 >= float13;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			}

			private int child(float float1, float float2, float float3) {
				for (int int1 = 0; int1 < this.children.length; ++int1) {
					BestCandidateSampling.Sphere.Node node = this.children[int1];
					if (isPointOnSphericalTriangle(float1, float2, float3, node.v0x, node.v0y, node.v0z, node.v1x, node.v1y, node.v1z, node.v2x, node.v2y, node.v2z, 1.0E-5F)) {
						return int1;
					}
				}

				return 0;
			}

			private float greatCircleDist(float float1, float float2, float float3, float float4, float float5, float float6) {
				float float7 = float1 * float4 + float2 * float5 + float3 * float6;
				return (float)(-1.5707963267948966 * (double)float7 + 1.5707963267948966);
			}

			float nearest(float float1, float float2, float float3, float float4) {
				float float5 = this.greatCircleDist(float1, float2, float3, this.cx, this.cy, this.cz);
				if (float5 - this.arc > float4) {
					return float4;
				} else {
					float float6 = float4;
					int int1;
					if (this.children != null) {
						int1 = this.children.length;
						int int2 = int1 - 1;
						int int3 = this.child(float1, float2, float3);
						for (int int4 = 0; int4 < int1; ++int4) {
							float float7 = this.children[int3].nearest(float1, float2, float3, float6);
							float6 = Math.min(float7, float6);
							int3 = int3 + 1 & int2;
						}

						return float6;
					} else {
						for (int1 = 0; this.objects != null && int1 < this.objects.size(); ++int1) {
							Vector3f vector3f = (Vector3f)this.objects.get(int1);
							float float8 = this.greatCircleDist(vector3f.x, vector3f.y, vector3f.z, float1, float2, float3);
							if (float8 < float6) {
								float6 = float8;
							}
						}

						return float6;
					}
				}
			}
		}
	}
}
