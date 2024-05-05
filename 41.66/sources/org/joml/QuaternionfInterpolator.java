package org.joml;


public class QuaternionfInterpolator {
	private final QuaternionfInterpolator.SvdDecomposition3f svdDecomposition3f = new QuaternionfInterpolator.SvdDecomposition3f();
	private final float[] m = new float[9];
	private final Matrix3f u = new Matrix3f();
	private final Matrix3f v = new Matrix3f();

	public Quaternionf computeWeightedAverage(Quaternionfc[] quaternionfcArray, float[] floatArray, int int1, Quaternionf quaternionf) {
		float float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = 0.0F;
		float float4 = 0.0F;
		float float5 = 0.0F;
		float float6 = 0.0F;
		float float7 = 0.0F;
		float float8 = 0.0F;
		float float9 = 0.0F;
		for (int int2 = 0; int2 < quaternionfcArray.length; ++int2) {
			Quaternionfc quaternionfc = quaternionfcArray[int2];
			float float10 = quaternionfc.x() + quaternionfc.x();
			float float11 = quaternionfc.y() + quaternionfc.y();
			float float12 = quaternionfc.z() + quaternionfc.z();
			float float13 = float10 * quaternionfc.x();
			float float14 = float11 * quaternionfc.y();
			float float15 = float12 * quaternionfc.z();
			float float16 = float10 * quaternionfc.y();
			float float17 = float10 * quaternionfc.z();
			float float18 = float10 * quaternionfc.w();
			float float19 = float11 * quaternionfc.z();
			float float20 = float11 * quaternionfc.w();
			float float21 = float12 * quaternionfc.w();
			float1 += floatArray[int2] * (1.0F - float14 - float15);
			float2 += floatArray[int2] * (float16 + float21);
			float3 += floatArray[int2] * (float17 - float20);
			float4 += floatArray[int2] * (float16 - float21);
			float5 += floatArray[int2] * (1.0F - float15 - float13);
			float6 += floatArray[int2] * (float19 + float18);
			float7 += floatArray[int2] * (float17 + float20);
			float8 += floatArray[int2] * (float19 - float18);
			float9 += floatArray[int2] * (1.0F - float14 - float13);
		}

		this.m[0] = float1;
		this.m[1] = float2;
		this.m[2] = float3;
		this.m[3] = float4;
		this.m[4] = float5;
		this.m[5] = float6;
		this.m[6] = float7;
		this.m[7] = float8;
		this.m[8] = float9;
		this.svdDecomposition3f.svd(this.m, int1, this.u, this.v);
		this.u.mul(this.v.transpose());
		return quaternionf.setFromNormalized((Matrix3fc)this.u).normalize();
	}

	private static class SvdDecomposition3f {
		private final float[] rv1 = new float[3];
		private final float[] w = new float[3];
		private final float[] v = new float[9];

		SvdDecomposition3f() {
		}

		private float SIGN(float float1, float float2) {
			return (double)float2 >= 0.0 ? Math.abs(float1) : -Math.abs(float1);
		}

		void svd(float[] floatArray, int int1, Matrix3f matrix3f, Matrix3f matrix3f2) {
			int int2 = 0;
			int int3 = 0;
			float float1 = 0.0F;
			float float2 = 0.0F;
			float float3 = 0.0F;
			int int4;
			int int5;
			int int6;
			float float4;
			float float5;
			float float6;
			for (int4 = 0; int4 < 3; ++int4) {
				int2 = int4 + 1;
				this.rv1[int4] = float3 * float2;
				float3 = 0.0F;
				float6 = 0.0F;
				float2 = 0.0F;
				for (int6 = int4; int6 < 3; ++int6) {
					float3 += Math.abs(floatArray[int6 + 3 * int4]);
				}

				if (float3 != 0.0F) {
					for (int6 = int4; int6 < 3; ++int6) {
						floatArray[int6 + 3 * int4] /= float3;
						float6 += floatArray[int6 + 3 * int4] * floatArray[int6 + 3 * int4];
					}

					float4 = floatArray[int4 + 3 * int4];
					float2 = -this.SIGN(Math.sqrt(float6), float4);
					float5 = float4 * float2 - float6;
					floatArray[int4 + 3 * int4] = float4 - float2;
					if (int4 != 2) {
						for (int5 = int2; int5 < 3; ++int5) {
							float6 = 0.0F;
							for (int6 = int4; int6 < 3; ++int6) {
								float6 += floatArray[int6 + 3 * int4] * floatArray[int6 + 3 * int5];
							}

							float4 = float6 / float5;
							for (int6 = int4; int6 < 3; ++int6) {
								floatArray[int6 + 3 * int5] += float4 * floatArray[int6 + 3 * int4];
							}
						}
					}

					for (int6 = int4; int6 < 3; ++int6) {
						floatArray[int6 + 3 * int4] *= float3;
					}
				}

				this.w[int4] = float3 * float2;
				float3 = 0.0F;
				float6 = 0.0F;
				float2 = 0.0F;
				if (int4 < 3 && int4 != 2) {
					for (int6 = int2; int6 < 3; ++int6) {
						float3 += Math.abs(floatArray[int4 + 3 * int6]);
					}

					if (float3 != 0.0F) {
						for (int6 = int2; int6 < 3; ++int6) {
							floatArray[int4 + 3 * int6] /= float3;
							float6 += floatArray[int4 + 3 * int6] * floatArray[int4 + 3 * int6];
						}

						float4 = floatArray[int4 + 3 * int2];
						float2 = -this.SIGN(Math.sqrt(float6), float4);
						float5 = float4 * float2 - float6;
						floatArray[int4 + 3 * int2] = float4 - float2;
						for (int6 = int2; int6 < 3; ++int6) {
							this.rv1[int6] = floatArray[int4 + 3 * int6] / float5;
						}

						if (int4 != 2) {
							for (int5 = int2; int5 < 3; ++int5) {
								float6 = 0.0F;
								for (int6 = int2; int6 < 3; ++int6) {
									float6 += floatArray[int5 + 3 * int6] * floatArray[int4 + 3 * int6];
								}

								for (int6 = int2; int6 < 3; ++int6) {
									floatArray[int5 + 3 * int6] += float6 * this.rv1[int6];
								}
							}
						}

						for (int6 = int2; int6 < 3; ++int6) {
							floatArray[int4 + 3 * int6] *= float3;
						}
					}
				}

				float1 = Math.max(float1, Math.abs(this.w[int4]) + Math.abs(this.rv1[int4]));
			}

			for (int4 = 2; int4 >= 0; int2 = int4--) {
				if (int4 < 2) {
					if (float2 != 0.0F) {
						for (int5 = int2; int5 < 3; ++int5) {
							this.v[int5 + 3 * int4] = floatArray[int4 + 3 * int5] / floatArray[int4 + 3 * int2] / float2;
						}

						for (int5 = int2; int5 < 3; ++int5) {
							float6 = 0.0F;
							for (int6 = int2; int6 < 3; ++int6) {
								float6 += floatArray[int4 + 3 * int6] * this.v[int6 + 3 * int5];
							}

							for (int6 = int2; int6 < 3; ++int6) {
								float[] floatArray2 = this.v;
								floatArray2[int6 + 3 * int5] += float6 * this.v[int6 + 3 * int4];
							}
						}
					}

					for (int5 = int2; int5 < 3; ++int5) {
						this.v[int4 + 3 * int5] = this.v[int5 + 3 * int4] = 0.0F;
					}
				}

				this.v[int4 + 3 * int4] = 1.0F;
				float2 = this.rv1[int4];
			}

			for (int4 = 2; int4 >= 0; --int4) {
				int2 = int4 + 1;
				float2 = this.w[int4];
				if (int4 < 2) {
					for (int5 = int2; int5 < 3; ++int5) {
						floatArray[int4 + 3 * int5] = 0.0F;
					}
				}

				if (float2 == 0.0F) {
					for (int5 = int4; int5 < 3; ++int5) {
						floatArray[int5 + 3 * int4] = 0.0F;
					}
				} else {
					float2 = 1.0F / float2;
					if (int4 != 2) {
						for (int5 = int2; int5 < 3; ++int5) {
							float6 = 0.0F;
							for (int6 = int2; int6 < 3; ++int6) {
								float6 += floatArray[int6 + 3 * int4] * floatArray[int6 + 3 * int5];
							}

							float4 = float6 / floatArray[int4 + 3 * int4] * float2;
							for (int6 = int4; int6 < 3; ++int6) {
								floatArray[int6 + 3 * int5] += float4 * floatArray[int6 + 3 * int4];
							}
						}
					}

					for (int5 = int4; int5 < 3; ++int5) {
						floatArray[int5 + 3 * int4] *= float2;
					}
				}

				++floatArray[int4 + 3 * int4];
			}

			label200: for (int6 = 2; int6 >= 0; --int6) {
				for (int int7 = 0; int7 < int1; ++int7) {
					boolean boolean1 = true;
					for (int2 = int6; int2 >= 0; --int2) {
						int3 = int2 - 1;
						if (Math.abs(this.rv1[int2]) + float1 == float1) {
							boolean1 = false;
							break;
						}

						if (Math.abs(this.w[int3]) + float1 == float1) {
							break;
						}
					}

					float float7;
					float float8;
					float float9;
					if (boolean1) {
						float7 = 0.0F;
						float6 = 1.0F;
						for (int4 = int2; int4 <= int6; ++int4) {
							float4 = float6 * this.rv1[int4];
							if (Math.abs(float4) + float1 != float1) {
								float2 = this.w[int4];
								float5 = PYTHAG(float4, float2);
								this.w[int4] = float5;
								float5 = 1.0F / float5;
								float7 = float2 * float5;
								float6 = -float4 * float5;
								for (int5 = 0; int5 < 3; ++int5) {
									float8 = floatArray[int5 + 3 * int3];
									float9 = floatArray[int5 + 3 * int4];
									floatArray[int5 + 3 * int3] = float8 * float7 + float9 * float6;
									floatArray[int5 + 3 * int4] = float9 * float7 - float8 * float6;
								}
							}
						}
					}

					float9 = this.w[int6];
					if (int2 == int6) {
						if (!(float9 < 0.0F)) {
							break;
						}

						this.w[int6] = -float9;
						int5 = 0;
						while (true) {
							if (int5 >= 3) {
								continue label200;
							}

							this.v[int5 + 3 * int6] = -this.v[int5 + 3 * int6];
							++int5;
						}
					}

					if (int7 == int1 - 1) {
						throw new RuntimeException("No convergence after " + int1 + " iterations");
					}

					float float10 = this.w[int2];
					int3 = int6 - 1;
					float8 = this.w[int3];
					float2 = this.rv1[int3];
					float5 = this.rv1[int6];
					float4 = ((float8 - float9) * (float8 + float9) + (float2 - float5) * (float2 + float5)) / (2.0F * float5 * float8);
					float2 = PYTHAG(float4, 1.0F);
					float4 = ((float10 - float9) * (float10 + float9) + float5 * (float8 / (float4 + this.SIGN(float2, float4)) - float5)) / float10;
					float6 = 1.0F;
					float7 = 1.0F;
					for (int5 = int2; int5 <= int3; ++int5) {
						int4 = int5 + 1;
						float2 = this.rv1[int4];
						float8 = this.w[int4];
						float5 = float6 * float2;
						float2 = float7 * float2;
						float9 = PYTHAG(float4, float5);
						this.rv1[int5] = float9;
						float7 = float4 / float9;
						float6 = float5 / float9;
						float4 = float10 * float7 + float2 * float6;
						float2 = float2 * float7 - float10 * float6;
						float5 = float8 * float6;
						float8 *= float7;
						int int8;
						for (int8 = 0; int8 < 3; ++int8) {
							float10 = this.v[int8 + 3 * int5];
							float9 = this.v[int8 + 3 * int4];
							this.v[int8 + 3 * int5] = float10 * float7 + float9 * float6;
							this.v[int8 + 3 * int4] = float9 * float7 - float10 * float6;
						}

						float9 = PYTHAG(float4, float5);
						this.w[int5] = float9;
						if (float9 != 0.0F) {
							float9 = 1.0F / float9;
							float7 = float4 * float9;
							float6 = float5 * float9;
						}

						float4 = float7 * float2 + float6 * float8;
						float10 = float7 * float8 - float6 * float2;
						for (int8 = 0; int8 < 3; ++int8) {
							float8 = floatArray[int8 + 3 * int5];
							float9 = floatArray[int8 + 3 * int4];
							floatArray[int8 + 3 * int5] = float8 * float7 + float9 * float6;
							floatArray[int8 + 3 * int4] = float9 * float7 - float8 * float6;
						}
					}

					this.rv1[int2] = 0.0F;
					this.rv1[int6] = float4;
					this.w[int6] = float10;
				}
			}
			matrix3f.set(floatArray);
			matrix3f2.set(this.v);
		}

		private static float PYTHAG(float float1, float float2) {
			float float3 = Math.abs(float1);
			float float4 = Math.abs(float2);
			float float5;
			float float6;
			if (float3 > float4) {
				float5 = float4 / float3;
				float6 = float3 * (float)Math.sqrt(1.0 + (double)(float5 * float5));
			} else if (float4 > 0.0F) {
				float5 = float3 / float4;
				float6 = float4 * (float)Math.sqrt(1.0 + (double)(float5 * float5));
			} else {
				float6 = 0.0F;
			}

			return float6;
		}
	}
}
