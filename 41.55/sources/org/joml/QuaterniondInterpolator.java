package org.joml;


public class QuaterniondInterpolator {
	private final QuaterniondInterpolator.SvdDecomposition3d svdDecomposition3d = new QuaterniondInterpolator.SvdDecomposition3d();
	private final double[] m = new double[9];
	private final Matrix3d u = new Matrix3d();
	private final Matrix3d v = new Matrix3d();

	public Quaterniond computeWeightedAverage(Quaterniond[] quaterniondArray, double[] doubleArray, int int1, Quaterniond quaterniond) {
		double double1 = 0.0;
		double double2 = 0.0;
		double double3 = 0.0;
		double double4 = 0.0;
		double double5 = 0.0;
		double double6 = 0.0;
		double double7 = 0.0;
		double double8 = 0.0;
		double double9 = 0.0;
		for (int int2 = 0; int2 < quaterniondArray.length; ++int2) {
			Quaterniond quaterniond2 = quaterniondArray[int2];
			double double10 = quaterniond2.x + quaterniond2.x;
			double double11 = quaterniond2.y + quaterniond2.y;
			double double12 = quaterniond2.z + quaterniond2.z;
			double double13 = double10 * quaterniond2.x;
			double double14 = double11 * quaterniond2.y;
			double double15 = double12 * quaterniond2.z;
			double double16 = double10 * quaterniond2.y;
			double double17 = double10 * quaterniond2.z;
			double double18 = double10 * quaterniond2.w;
			double double19 = double11 * quaterniond2.z;
			double double20 = double11 * quaterniond2.w;
			double double21 = double12 * quaterniond2.w;
			double1 += doubleArray[int2] * (1.0 - double14 - double15);
			double2 += doubleArray[int2] * (double16 + double21);
			double3 += doubleArray[int2] * (double17 - double20);
			double4 += doubleArray[int2] * (double16 - double21);
			double5 += doubleArray[int2] * (1.0 - double15 - double13);
			double6 += doubleArray[int2] * (double19 + double18);
			double7 += doubleArray[int2] * (double17 + double20);
			double8 += doubleArray[int2] * (double19 - double18);
			double9 += doubleArray[int2] * (1.0 - double14 - double13);
		}

		this.m[0] = double1;
		this.m[1] = double2;
		this.m[2] = double3;
		this.m[3] = double4;
		this.m[4] = double5;
		this.m[5] = double6;
		this.m[6] = double7;
		this.m[7] = double8;
		this.m[8] = double9;
		this.svdDecomposition3d.svd(this.m, int1, this.u, this.v);
		this.u.mul((Matrix3dc)this.v.transpose());
		return quaterniond.setFromNormalized((Matrix3dc)this.u).normalize();
	}

	private static class SvdDecomposition3d {
		private final double[] rv1 = new double[3];
		private final double[] w = new double[3];
		private final double[] v = new double[9];

		SvdDecomposition3d() {
		}

		private double SIGN(double double1, double double2) {
			return double2 >= 0.0 ? Math.abs(double1) : -Math.abs(double1);
		}

		void svd(double[] doubleArray, int int1, Matrix3d matrix3d, Matrix3d matrix3d2) {
			int int2 = 0;
			int int3 = 0;
			double double1 = 0.0;
			double double2 = 0.0;
			double double3 = 0.0;
			int int4;
			int int5;
			int int6;
			double double4;
			double double5;
			double double6;
			for (int4 = 0; int4 < 3; ++int4) {
				int2 = int4 + 1;
				this.rv1[int4] = double3 * double2;
				double3 = 0.0;
				double6 = 0.0;
				double2 = 0.0;
				for (int6 = int4; int6 < 3; ++int6) {
					double3 += Math.abs(doubleArray[int6 + 3 * int4]);
				}

				if (double3 != 0.0) {
					for (int6 = int4; int6 < 3; ++int6) {
						doubleArray[int6 + 3 * int4] /= double3;
						double6 += doubleArray[int6 + 3 * int4] * doubleArray[int6 + 3 * int4];
					}

					double4 = doubleArray[int4 + 3 * int4];
					double2 = -this.SIGN(Math.sqrt(double6), double4);
					double5 = double4 * double2 - double6;
					doubleArray[int4 + 3 * int4] = double4 - double2;
					if (int4 != 2) {
						for (int5 = int2; int5 < 3; ++int5) {
							double6 = 0.0;
							for (int6 = int4; int6 < 3; ++int6) {
								double6 += doubleArray[int6 + 3 * int4] * doubleArray[int6 + 3 * int5];
							}

							double4 = double6 / double5;
							for (int6 = int4; int6 < 3; ++int6) {
								doubleArray[int6 + 3 * int5] += double4 * doubleArray[int6 + 3 * int4];
							}
						}
					}

					for (int6 = int4; int6 < 3; ++int6) {
						doubleArray[int6 + 3 * int4] *= double3;
					}
				}

				this.w[int4] = double3 * double2;
				double3 = 0.0;
				double6 = 0.0;
				double2 = 0.0;
				if (int4 < 3 && int4 != 2) {
					for (int6 = int2; int6 < 3; ++int6) {
						double3 += Math.abs(doubleArray[int4 + 3 * int6]);
					}

					if (double3 != 0.0) {
						for (int6 = int2; int6 < 3; ++int6) {
							doubleArray[int4 + 3 * int6] /= double3;
							double6 += doubleArray[int4 + 3 * int6] * doubleArray[int4 + 3 * int6];
						}

						double4 = doubleArray[int4 + 3 * int2];
						double2 = -this.SIGN(Math.sqrt(double6), double4);
						double5 = double4 * double2 - double6;
						doubleArray[int4 + 3 * int2] = double4 - double2;
						for (int6 = int2; int6 < 3; ++int6) {
							this.rv1[int6] = doubleArray[int4 + 3 * int6] / double5;
						}

						if (int4 != 2) {
							for (int5 = int2; int5 < 3; ++int5) {
								double6 = 0.0;
								for (int6 = int2; int6 < 3; ++int6) {
									double6 += doubleArray[int5 + 3 * int6] * doubleArray[int4 + 3 * int6];
								}

								for (int6 = int2; int6 < 3; ++int6) {
									doubleArray[int5 + 3 * int6] += double6 * this.rv1[int6];
								}
							}
						}

						for (int6 = int2; int6 < 3; ++int6) {
							doubleArray[int4 + 3 * int6] *= double3;
						}
					}
				}

				double1 = Math.max(double1, Math.abs(this.w[int4]) + Math.abs(this.rv1[int4]));
			}

			for (int4 = 2; int4 >= 0; int2 = int4--) {
				if (int4 < 2) {
					if (double2 != 0.0) {
						for (int5 = int2; int5 < 3; ++int5) {
							this.v[int5 + 3 * int4] = doubleArray[int4 + 3 * int5] / doubleArray[int4 + 3 * int2] / double2;
						}

						for (int5 = int2; int5 < 3; ++int5) {
							double6 = 0.0;
							for (int6 = int2; int6 < 3; ++int6) {
								double6 += doubleArray[int4 + 3 * int6] * this.v[int6 + 3 * int5];
							}

							for (int6 = int2; int6 < 3; ++int6) {
								double[] doubleArray2 = this.v;
								doubleArray2[int6 + 3 * int5] += double6 * this.v[int6 + 3 * int4];
							}
						}
					}

					for (int5 = int2; int5 < 3; ++int5) {
						this.v[int4 + 3 * int5] = this.v[int5 + 3 * int4] = 0.0;
					}
				}

				this.v[int4 + 3 * int4] = 1.0;
				double2 = this.rv1[int4];
			}

			for (int4 = 2; int4 >= 0; --int4) {
				int2 = int4 + 1;
				double2 = this.w[int4];
				if (int4 < 2) {
					for (int5 = int2; int5 < 3; ++int5) {
						doubleArray[int4 + 3 * int5] = 0.0;
					}
				}

				if (double2 == 0.0) {
					for (int5 = int4; int5 < 3; ++int5) {
						doubleArray[int5 + 3 * int4] = 0.0;
					}
				} else {
					double2 = 1.0 / double2;
					if (int4 != 2) {
						for (int5 = int2; int5 < 3; ++int5) {
							double6 = 0.0;
							for (int6 = int2; int6 < 3; ++int6) {
								double6 += doubleArray[int6 + 3 * int4] * doubleArray[int6 + 3 * int5];
							}

							double4 = double6 / doubleArray[int4 + 3 * int4] * double2;
							for (int6 = int4; int6 < 3; ++int6) {
								doubleArray[int6 + 3 * int5] += double4 * doubleArray[int6 + 3 * int4];
							}
						}
					}

					for (int5 = int4; int5 < 3; ++int5) {
						doubleArray[int5 + 3 * int4] *= double2;
					}
				}

				++doubleArray[int4 + 3 * int4];
			}

			label200: for (int6 = 2; int6 >= 0; --int6) {
				for (int int7 = 0; int7 < int1; ++int7) {
					boolean boolean1 = true;
					for (int2 = int6; int2 >= 0; --int2) {
						int3 = int2 - 1;
						if (Math.abs(this.rv1[int2]) + double1 == double1) {
							boolean1 = false;
							break;
						}

						if (Math.abs(this.w[int3]) + double1 == double1) {
							break;
						}
					}

					double double7;
					double double8;
					double double9;
					if (boolean1) {
						double7 = 0.0;
						double6 = 1.0;
						for (int4 = int2; int4 <= int6; ++int4) {
							double4 = double6 * this.rv1[int4];
							if (Math.abs(double4) + double1 != double1) {
								double2 = this.w[int4];
								double5 = PYTHAG(double4, double2);
								this.w[int4] = double5;
								double5 = 1.0 / double5;
								double7 = double2 * double5;
								double6 = -double4 * double5;
								for (int5 = 0; int5 < 3; ++int5) {
									double8 = doubleArray[int5 + 3 * int3];
									double9 = doubleArray[int5 + 3 * int4];
									doubleArray[int5 + 3 * int3] = double8 * double7 + double9 * double6;
									doubleArray[int5 + 3 * int4] = double9 * double7 - double8 * double6;
								}
							}
						}
					}

					double9 = this.w[int6];
					if (int2 == int6) {
						if (!(double9 < 0.0)) {
							break;
						}

						this.w[int6] = -double9;
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

					double double10 = this.w[int2];
					int3 = int6 - 1;
					double8 = this.w[int3];
					double2 = this.rv1[int3];
					double5 = this.rv1[int6];
					double4 = ((double8 - double9) * (double8 + double9) + (double2 - double5) * (double2 + double5)) / (2.0 * double5 * double8);
					double2 = PYTHAG(double4, 1.0);
					double4 = ((double10 - double9) * (double10 + double9) + double5 * (double8 / (double4 + this.SIGN(double2, double4)) - double5)) / double10;
					double6 = 1.0;
					double7 = 1.0;
					for (int5 = int2; int5 <= int3; ++int5) {
						int4 = int5 + 1;
						double2 = this.rv1[int4];
						double8 = this.w[int4];
						double5 = double6 * double2;
						double2 = double7 * double2;
						double9 = PYTHAG(double4, double5);
						this.rv1[int5] = double9;
						double7 = double4 / double9;
						double6 = double5 / double9;
						double4 = double10 * double7 + double2 * double6;
						double2 = double2 * double7 - double10 * double6;
						double5 = double8 * double6;
						double8 *= double7;
						int int8;
						for (int8 = 0; int8 < 3; ++int8) {
							double10 = this.v[int8 + 3 * int5];
							double9 = this.v[int8 + 3 * int4];
							this.v[int8 + 3 * int5] = double10 * double7 + double9 * double6;
							this.v[int8 + 3 * int4] = double9 * double7 - double10 * double6;
						}

						double9 = PYTHAG(double4, double5);
						this.w[int5] = double9;
						if (double9 != 0.0) {
							double9 = 1.0 / double9;
							double7 = double4 * double9;
							double6 = double5 * double9;
						}

						double4 = double7 * double2 + double6 * double8;
						double10 = double7 * double8 - double6 * double2;
						for (int8 = 0; int8 < 3; ++int8) {
							double8 = doubleArray[int8 + 3 * int5];
							double9 = doubleArray[int8 + 3 * int4];
							doubleArray[int8 + 3 * int5] = double8 * double7 + double9 * double6;
							doubleArray[int8 + 3 * int4] = double9 * double7 - double8 * double6;
						}
					}

					this.rv1[int2] = 0.0;
					this.rv1[int6] = double4;
					this.w[int6] = double10;
				}
			}
			matrix3d.set(doubleArray);
			matrix3d2.set(this.v);
		}

		private static double PYTHAG(double double1, double double2) {
			double double3 = Math.abs(double1);
			double double4 = Math.abs(double2);
			double double5;
			double double6;
			if (double3 > double4) {
				double5 = double4 / double3;
				double6 = double3 * Math.sqrt(1.0 + double5 * double5);
			} else if (double4 > 0.0) {
				double5 = double3 / double4;
				double6 = double4 * Math.sqrt(1.0 + double5 * double5);
			} else {
				double6 = 0.0;
			}

			return double6;
		}
	}
}
