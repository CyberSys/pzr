package zombie.creative.creativerects;


public class OpenSimplexNoise {
	private static final double STRETCH_CONSTANT_2D = -0.211324865405187;
	private static final double SQUISH_CONSTANT_2D = 0.366025403784439;
	private static final double STRETCH_CONSTANT_3D = -0.16666666666666666;
	private static final double SQUISH_CONSTANT_3D = 0.3333333333333333;
	private static final double STRETCH_CONSTANT_4D = -0.138196601125011;
	private static final double SQUISH_CONSTANT_4D = 0.309016994374947;
	private static final double NORM_CONSTANT_2D = 47.0;
	private static final double NORM_CONSTANT_3D = 103.0;
	private static final double NORM_CONSTANT_4D = 30.0;
	private static final long DEFAULT_SEED = 0L;
	private short[] perm;
	private short[] permGradIndex3D;
	private static byte[] gradients2D = new byte[]{5, 2, 2, 5, -5, 2, -2, 5, 5, -2, 2, -5, -5, -2, -2, -5};
	private static byte[] gradients3D = new byte[]{-11, 4, 4, -4, 11, 4, -4, 4, 11, 11, 4, 4, 4, 11, 4, 4, 4, 11, -11, -4, 4, -4, -11, 4, -4, -4, 11, 11, -4, 4, 4, -11, 4, 4, -4, 11, -11, 4, -4, -4, 11, -4, -4, 4, -11, 11, 4, -4, 4, 11, -4, 4, 4, -11, -11, -4, -4, -4, -11, -4, -4, -4, -11, 11, -4, -4, 4, -11, -4, 4, -4, -11};
	private static byte[] gradients4D = new byte[]{3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 1, 3, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, 1, 1, -1, 1, 3, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, 1, 1, 1, -1, 3, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, 1, 1, -1, -1, 3, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, 3, 1, 1, -1, 1, 3, 1, -1, 1, 1, 3, -1, 1, 1, 1, -3, -3, 1, 1, -1, -1, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, 3, -1, 1, -1, 1, -3, 1, -1, 1, -1, 3, -1, 1, -1, 1, -3, -3, -1, 1, -1, -1, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, 3, 1, -1, -1, 1, 3, -1, -1, 1, 1, -3, -1, 1, 1, -1, -3, -3, 1, -1, -1, -1, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, 3, -1, -1, -1, 1, -3, -1, -1, 1, -1, -3, -1, 1, -1, -1, -3, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3, -1, -1, -1, -1, -3};

	public OpenSimplexNoise() {
		this(0L);
	}

	public OpenSimplexNoise(short[] shortArray) {
		this.perm = shortArray;
		this.permGradIndex3D = new short[256];
		for (int int1 = 0; int1 < 256; ++int1) {
			this.permGradIndex3D[int1] = (short)(shortArray[int1] % (gradients3D.length / 3) * 3);
		}
	}

	public OpenSimplexNoise(long long1) {
		this.perm = new short[256];
		this.permGradIndex3D = new short[256];
		short[] shortArray = new short[256];
		for (short short1 = 0; short1 < 256; shortArray[short1] = short1++) {
		}

		long1 = long1 * 6364136223846793005L + 1442695040888963407L;
		long1 = long1 * 6364136223846793005L + 1442695040888963407L;
		long1 = long1 * 6364136223846793005L + 1442695040888963407L;
		for (int int1 = 255; int1 >= 0; --int1) {
			long1 = long1 * 6364136223846793005L + 1442695040888963407L;
			int int2 = (int)((long1 + 31L) % (long)(int1 + 1));
			if (int2 < 0) {
				int2 += int1 + 1;
			}

			this.perm[int1] = shortArray[int2];
			this.permGradIndex3D[int1] = (short)(this.perm[int1] % (gradients3D.length / 3) * 3);
			shortArray[int2] = shortArray[int1];
		}
	}

	public double eval(double double1, double double2) {
		double double3 = (double1 + double2) * -0.211324865405187;
		double double4 = double1 + double3;
		double double5 = double2 + double3;
		int int1 = fastFloor(double4);
		int int2 = fastFloor(double5);
		double double6 = (double)(int1 + int2) * 0.366025403784439;
		double double7 = (double)int1 + double6;
		double double8 = (double)int2 + double6;
		double double9 = double4 - (double)int1;
		double double10 = double5 - (double)int2;
		double double11 = double9 + double10;
		double double12 = double1 - double7;
		double double13 = double2 - double8;
		double double14 = 0.0;
		double double15 = double12 - 1.0 - 0.366025403784439;
		double double16 = double13 - 0.0 - 0.366025403784439;
		double double17 = 2.0 - double15 * double15 - double16 * double16;
		if (double17 > 0.0) {
			double17 *= double17;
			double14 += double17 * double17 * this.extrapolate(int1 + 1, int2 + 0, double15, double16);
		}

		double double18 = double12 - 0.0 - 0.366025403784439;
		double double19 = double13 - 1.0 - 0.366025403784439;
		double double20 = 2.0 - double18 * double18 - double19 * double19;
		if (double20 > 0.0) {
			double20 *= double20;
			double14 += double20 * double20 * this.extrapolate(int1 + 0, int2 + 1, double18, double19);
		}

		double double21;
		double double22;
		int int3;
		int int4;
		double double23;
		if (double11 <= 1.0) {
			double23 = 1.0 - double11;
			if (!(double23 > double9) && !(double23 > double10)) {
				int3 = int1 + 1;
				int4 = int2 + 1;
				double21 = double12 - 1.0 - 0.732050807568878;
				double22 = double13 - 1.0 - 0.732050807568878;
			} else if (double9 > double10) {
				int3 = int1 + 1;
				int4 = int2 - 1;
				double21 = double12 - 1.0;
				double22 = double13 + 1.0;
			} else {
				int3 = int1 - 1;
				int4 = int2 + 1;
				double21 = double12 + 1.0;
				double22 = double13 - 1.0;
			}
		} else {
			double23 = 2.0 - double11;
			if (!(double23 < double9) && !(double23 < double10)) {
				double21 = double12;
				double22 = double13;
				int3 = int1;
				int4 = int2;
			} else if (double9 > double10) {
				int3 = int1 + 2;
				int4 = int2 + 0;
				double21 = double12 - 2.0 - 0.732050807568878;
				double22 = double13 + 0.0 - 0.732050807568878;
			} else {
				int3 = int1 + 0;
				int4 = int2 + 2;
				double21 = double12 + 0.0 - 0.732050807568878;
				double22 = double13 - 2.0 - 0.732050807568878;
			}

			++int1;
			++int2;
			double12 = double12 - 1.0 - 0.732050807568878;
			double13 = double13 - 1.0 - 0.732050807568878;
		}

		double23 = 2.0 - double12 * double12 - double13 * double13;
		if (double23 > 0.0) {
			double23 *= double23;
			double14 += double23 * double23 * this.extrapolate(int1, int2, double12, double13);
		}

		double double24 = 2.0 - double21 * double21 - double22 * double22;
		if (double24 > 0.0) {
			double24 *= double24;
			double14 += double24 * double24 * this.extrapolate(int3, int4, double21, double22);
		}

		return double14 / 47.0;
	}

	public double eval(double double1, double double2, double double3) {
		double double4 = (double1 + double2 + double3) * -0.16666666666666666;
		double double5 = double1 + double4;
		double double6 = double2 + double4;
		double double7 = double3 + double4;
		int int1 = fastFloor(double5);
		int int2 = fastFloor(double6);
		int int3 = fastFloor(double7);
		double double8 = (double)(int1 + int2 + int3) * 0.3333333333333333;
		double double9 = (double)int1 + double8;
		double double10 = (double)int2 + double8;
		double double11 = (double)int3 + double8;
		double double12 = double5 - (double)int1;
		double double13 = double6 - (double)int2;
		double double14 = double7 - (double)int3;
		double double15 = double12 + double13 + double14;
		double double16 = double1 - double9;
		double double17 = double2 - double10;
		double double18 = double3 - double11;
		double double19 = 0.0;
		double double20;
		double double21;
		double double22;
		double double23;
		double double24;
		double double25;
		int int4;
		int int5;
		int int6;
		int int7;
		int int8;
		int int9;
		byte byte1;
		double double26;
		byte byte2;
		double double27;
		double double28;
		byte byte3;
		double double29;
		double double30;
		double double31;
		double double32;
		double double33;
		double double34;
		double double35;
		double double36;
		double double37;
		double double38;
		byte byte4;
		double double39;
		if (double15 <= 1.0) {
			byte1 = 1;
			double26 = double12;
			byte2 = 2;
			double27 = double13;
			if (double12 >= double13 && double14 > double13) {
				double27 = double14;
				byte2 = 4;
			} else if (double12 < double13 && double14 > double12) {
				double26 = double14;
				byte1 = 4;
			}

			double28 = 1.0 - double15;
			if (!(double28 > double26) && !(double28 > double27)) {
				byte4 = (byte)(byte1 | byte2);
				if ((byte4 & 1) == 0) {
					int4 = int1;
					int7 = int1 - 1;
					double20 = double16 - 0.6666666666666666;
					double23 = double16 + 1.0 - 0.3333333333333333;
				} else {
					int4 = int7 = int1 + 1;
					double20 = double16 - 1.0 - 0.6666666666666666;
					double23 = double16 - 1.0 - 0.3333333333333333;
				}

				if ((byte4 & 2) == 0) {
					int5 = int2;
					int8 = int2 - 1;
					double21 = double17 - 0.6666666666666666;
					double24 = double17 + 1.0 - 0.3333333333333333;
				} else {
					int5 = int8 = int2 + 1;
					double21 = double17 - 1.0 - 0.6666666666666666;
					double24 = double17 - 1.0 - 0.3333333333333333;
				}

				if ((byte4 & 4) == 0) {
					int6 = int3;
					int9 = int3 - 1;
					double22 = double18 - 0.6666666666666666;
					double25 = double18 + 1.0 - 0.3333333333333333;
				} else {
					int6 = int9 = int3 + 1;
					double22 = double18 - 1.0 - 0.6666666666666666;
					double25 = double18 - 1.0 - 0.3333333333333333;
				}
			} else {
				byte3 = double27 > double26 ? byte2 : byte1;
				if ((byte3 & 1) == 0) {
					int4 = int1 - 1;
					int7 = int1;
					double20 = double16 + 1.0;
					double23 = double16;
				} else {
					int4 = int7 = int1 + 1;
					double20 = double23 = double16 - 1.0;
				}

				if ((byte3 & 2) == 0) {
					int8 = int2;
					int5 = int2;
					double24 = double17;
					double21 = double17;
					if ((byte3 & 1) == 0) {
						int8 = int2 - 1;
						double24 = double17 + 1.0;
					} else {
						int5 = int2 - 1;
						double21 = double17 + 1.0;
					}
				} else {
					int5 = int8 = int2 + 1;
					double21 = double24 = double17 - 1.0;
				}

				if ((byte3 & 4) == 0) {
					int6 = int3;
					int9 = int3 - 1;
					double22 = double18;
					double25 = double18 + 1.0;
				} else {
					int6 = int9 = int3 + 1;
					double22 = double25 = double18 - 1.0;
				}
			}

			double39 = 2.0 - double16 * double16 - double17 * double17 - double18 * double18;
			if (double39 > 0.0) {
				double39 *= double39;
				double19 += double39 * double39 * this.extrapolate(int1 + 0, int2 + 0, int3 + 0, double16, double17, double18);
			}

			double29 = double16 - 1.0 - 0.3333333333333333;
			double30 = double17 - 0.0 - 0.3333333333333333;
			double31 = double18 - 0.0 - 0.3333333333333333;
			double32 = 2.0 - double29 * double29 - double30 * double30 - double31 * double31;
			if (double32 > 0.0) {
				double32 *= double32;
				double19 += double32 * double32 * this.extrapolate(int1 + 1, int2 + 0, int3 + 0, double29, double30, double31);
			}

			double33 = double16 - 0.0 - 0.3333333333333333;
			double34 = double17 - 1.0 - 0.3333333333333333;
			double35 = 2.0 - double33 * double33 - double34 * double34 - double31 * double31;
			if (double35 > 0.0) {
				double35 *= double35;
				double19 += double35 * double35 * this.extrapolate(int1 + 0, int2 + 1, int3 + 0, double33, double34, double31);
			}

			double36 = double18 - 1.0 - 0.3333333333333333;
			double37 = 2.0 - double33 * double33 - double30 * double30 - double36 * double36;
			if (double37 > 0.0) {
				double37 *= double37;
				double19 += double37 * double37 * this.extrapolate(int1 + 0, int2 + 0, int3 + 1, double33, double30, double36);
			}
		} else {
			double double40;
			if (double15 >= 2.0) {
				byte1 = 6;
				double26 = double12;
				byte2 = 5;
				double27 = double13;
				if (double12 <= double13 && double14 < double13) {
					double27 = double14;
					byte2 = 3;
				} else if (double12 > double13 && double14 < double12) {
					double26 = double14;
					byte1 = 3;
				}

				double28 = 3.0 - double15;
				if (!(double28 < double26) && !(double28 < double27)) {
					byte4 = (byte)(byte1 & byte2);
					if ((byte4 & 1) != 0) {
						int4 = int1 + 1;
						int7 = int1 + 2;
						double20 = double16 - 1.0 - 0.3333333333333333;
						double23 = double16 - 2.0 - 0.6666666666666666;
					} else {
						int7 = int1;
						int4 = int1;
						double20 = double16 - 0.3333333333333333;
						double23 = double16 - 0.6666666666666666;
					}

					if ((byte4 & 2) != 0) {
						int5 = int2 + 1;
						int8 = int2 + 2;
						double21 = double17 - 1.0 - 0.3333333333333333;
						double24 = double17 - 2.0 - 0.6666666666666666;
					} else {
						int8 = int2;
						int5 = int2;
						double21 = double17 - 0.3333333333333333;
						double24 = double17 - 0.6666666666666666;
					}

					if ((byte4 & 4) != 0) {
						int6 = int3 + 1;
						int9 = int3 + 2;
						double22 = double18 - 1.0 - 0.3333333333333333;
						double25 = double18 - 2.0 - 0.6666666666666666;
					} else {
						int9 = int3;
						int6 = int3;
						double22 = double18 - 0.3333333333333333;
						double25 = double18 - 0.6666666666666666;
					}
				} else {
					byte3 = double27 < double26 ? byte2 : byte1;
					if ((byte3 & 1) != 0) {
						int4 = int1 + 2;
						int7 = int1 + 1;
						double20 = double16 - 2.0 - 1.0;
						double23 = double16 - 1.0 - 1.0;
					} else {
						int7 = int1;
						int4 = int1;
						double20 = double23 = double16 - 1.0;
					}

					if ((byte3 & 2) != 0) {
						int5 = int8 = int2 + 1;
						double21 = double24 = double17 - 1.0 - 1.0;
						if ((byte3 & 1) != 0) {
							++int8;
							--double24;
						} else {
							++int5;
							--double21;
						}
					} else {
						int8 = int2;
						int5 = int2;
						double21 = double24 = double17 - 1.0;
					}

					if ((byte3 & 4) != 0) {
						int6 = int3 + 1;
						int9 = int3 + 2;
						double22 = double18 - 1.0 - 1.0;
						double25 = double18 - 2.0 - 1.0;
					} else {
						int9 = int3;
						int6 = int3;
						double22 = double25 = double18 - 1.0;
					}
				}

				double39 = double16 - 1.0 - 0.6666666666666666;
				double29 = double17 - 1.0 - 0.6666666666666666;
				double30 = double18 - 0.0 - 0.6666666666666666;
				double31 = 2.0 - double39 * double39 - double29 * double29 - double30 * double30;
				if (double31 > 0.0) {
					double31 *= double31;
					double19 += double31 * double31 * this.extrapolate(int1 + 1, int2 + 1, int3 + 0, double39, double29, double30);
				}

				double33 = double17 - 0.0 - 0.6666666666666666;
				double34 = double18 - 1.0 - 0.6666666666666666;
				double40 = 2.0 - double39 * double39 - double33 * double33 - double34 * double34;
				if (double40 > 0.0) {
					double40 *= double40;
					double19 += double40 * double40 * this.extrapolate(int1 + 1, int2 + 0, int3 + 1, double39, double33, double34);
				}

				double35 = double16 - 0.0 - 0.6666666666666666;
				double36 = 2.0 - double35 * double35 - double29 * double29 - double34 * double34;
				if (double36 > 0.0) {
					double36 *= double36;
					double19 += double36 * double36 * this.extrapolate(int1 + 0, int2 + 1, int3 + 1, double35, double29, double34);
				}

				double16 = double16 - 1.0 - 1.0;
				double17 = double17 - 1.0 - 1.0;
				double18 = double18 - 1.0 - 1.0;
				double37 = 2.0 - double16 * double16 - double17 * double17 - double18 * double18;
				if (double37 > 0.0) {
					double37 *= double37;
					double19 += double37 * double37 * this.extrapolate(int1 + 1, int2 + 1, int3 + 1, double16, double17, double18);
				}
			} else {
				double39 = double12 + double13;
				byte byte5;
				boolean boolean1;
				if (double39 > 1.0) {
					double38 = double39 - 1.0;
					byte5 = 3;
					boolean1 = true;
				} else {
					double38 = 1.0 - double39;
					byte5 = 4;
					boolean1 = false;
				}

				double29 = double12 + double14;
				boolean boolean2;
				byte byte6;
				if (double29 > 1.0) {
					double27 = double29 - 1.0;
					byte6 = 5;
					boolean2 = true;
				} else {
					double27 = 1.0 - double29;
					byte6 = 2;
					boolean2 = false;
				}

				double30 = double13 + double14;
				if (double30 > 1.0) {
					double31 = double30 - 1.0;
					if (double38 <= double27 && double38 < double31) {
						byte5 = 6;
						boolean1 = true;
					} else if (double38 > double27 && double27 < double31) {
						byte6 = 6;
						boolean2 = true;
					}
				} else {
					double31 = 1.0 - double30;
					if (double38 <= double27 && double38 < double31) {
						byte5 = 1;
						boolean1 = false;
					} else if (double38 > double27 && double27 < double31) {
						byte6 = 1;
						boolean2 = false;
					}
				}

				if (boolean1 == boolean2) {
					byte byte7;
					if (boolean1) {
						double20 = double16 - 1.0 - 1.0;
						double21 = double17 - 1.0 - 1.0;
						double22 = double18 - 1.0 - 1.0;
						int4 = int1 + 1;
						int5 = int2 + 1;
						int6 = int3 + 1;
						byte7 = (byte)(byte5 & byte6);
						if ((byte7 & 1) != 0) {
							double23 = double16 - 2.0 - 0.6666666666666666;
							double24 = double17 - 0.6666666666666666;
							double25 = double18 - 0.6666666666666666;
							int7 = int1 + 2;
							int8 = int2;
							int9 = int3;
						} else if ((byte7 & 2) != 0) {
							double23 = double16 - 0.6666666666666666;
							double24 = double17 - 2.0 - 0.6666666666666666;
							double25 = double18 - 0.6666666666666666;
							int7 = int1;
							int8 = int2 + 2;
							int9 = int3;
						} else {
							double23 = double16 - 0.6666666666666666;
							double24 = double17 - 0.6666666666666666;
							double25 = double18 - 2.0 - 0.6666666666666666;
							int7 = int1;
							int8 = int2;
							int9 = int3 + 2;
						}
					} else {
						double20 = double16;
						double21 = double17;
						double22 = double18;
						int4 = int1;
						int5 = int2;
						int6 = int3;
						byte7 = (byte)(byte5 | byte6);
						if ((byte7 & 1) == 0) {
							double23 = double16 + 1.0 - 0.3333333333333333;
							double24 = double17 - 1.0 - 0.3333333333333333;
							double25 = double18 - 1.0 - 0.3333333333333333;
							int7 = int1 - 1;
							int8 = int2 + 1;
							int9 = int3 + 1;
						} else if ((byte7 & 2) == 0) {
							double23 = double16 - 1.0 - 0.3333333333333333;
							double24 = double17 + 1.0 - 0.3333333333333333;
							double25 = double18 - 1.0 - 0.3333333333333333;
							int7 = int1 + 1;
							int8 = int2 - 1;
							int9 = int3 + 1;
						} else {
							double23 = double16 - 1.0 - 0.3333333333333333;
							double24 = double17 - 1.0 - 0.3333333333333333;
							double25 = double18 + 1.0 - 0.3333333333333333;
							int7 = int1 + 1;
							int8 = int2 + 1;
							int9 = int3 - 1;
						}
					}
				} else {
					byte byte8;
					byte byte9;
					if (boolean1) {
						byte8 = byte5;
						byte9 = byte6;
					} else {
						byte8 = byte6;
						byte9 = byte5;
					}

					if ((byte8 & 1) == 0) {
						double20 = double16 + 1.0 - 0.3333333333333333;
						double21 = double17 - 1.0 - 0.3333333333333333;
						double22 = double18 - 1.0 - 0.3333333333333333;
						int4 = int1 - 1;
						int5 = int2 + 1;
						int6 = int3 + 1;
					} else if ((byte8 & 2) == 0) {
						double20 = double16 - 1.0 - 0.3333333333333333;
						double21 = double17 + 1.0 - 0.3333333333333333;
						double22 = double18 - 1.0 - 0.3333333333333333;
						int4 = int1 + 1;
						int5 = int2 - 1;
						int6 = int3 + 1;
					} else {
						double20 = double16 - 1.0 - 0.3333333333333333;
						double21 = double17 - 1.0 - 0.3333333333333333;
						double22 = double18 + 1.0 - 0.3333333333333333;
						int4 = int1 + 1;
						int5 = int2 + 1;
						int6 = int3 - 1;
					}

					double23 = double16 - 0.6666666666666666;
					double24 = double17 - 0.6666666666666666;
					double25 = double18 - 0.6666666666666666;
					int7 = int1;
					int8 = int2;
					int9 = int3;
					if ((byte9 & 1) != 0) {
						double23 -= 2.0;
						int7 = int1 + 2;
					} else if ((byte9 & 2) != 0) {
						double24 -= 2.0;
						int8 = int2 + 2;
					} else {
						double25 -= 2.0;
						int9 = int3 + 2;
					}
				}

				double31 = double16 - 1.0 - 0.3333333333333333;
				double32 = double17 - 0.0 - 0.3333333333333333;
				double33 = double18 - 0.0 - 0.3333333333333333;
				double34 = 2.0 - double31 * double31 - double32 * double32 - double33 * double33;
				if (double34 > 0.0) {
					double34 *= double34;
					double19 += double34 * double34 * this.extrapolate(int1 + 1, int2 + 0, int3 + 0, double31, double32, double33);
				}

				double40 = double16 - 0.0 - 0.3333333333333333;
				double35 = double17 - 1.0 - 0.3333333333333333;
				double double41 = 2.0 - double40 * double40 - double35 * double35 - double33 * double33;
				if (double41 > 0.0) {
					double41 *= double41;
					double19 += double41 * double41 * this.extrapolate(int1 + 0, int2 + 1, int3 + 0, double40, double35, double33);
				}

				double double42 = double18 - 1.0 - 0.3333333333333333;
				double double43 = 2.0 - double40 * double40 - double32 * double32 - double42 * double42;
				if (double43 > 0.0) {
					double43 *= double43;
					double19 += double43 * double43 * this.extrapolate(int1 + 0, int2 + 0, int3 + 1, double40, double32, double42);
				}

				double double44 = double16 - 1.0 - 0.6666666666666666;
				double double45 = double17 - 1.0 - 0.6666666666666666;
				double double46 = double18 - 0.0 - 0.6666666666666666;
				double double47 = 2.0 - double44 * double44 - double45 * double45 - double46 * double46;
				if (double47 > 0.0) {
					double47 *= double47;
					double19 += double47 * double47 * this.extrapolate(int1 + 1, int2 + 1, int3 + 0, double44, double45, double46);
				}

				double double48 = double17 - 0.0 - 0.6666666666666666;
				double double49 = double18 - 1.0 - 0.6666666666666666;
				double double50 = 2.0 - double44 * double44 - double48 * double48 - double49 * double49;
				if (double50 > 0.0) {
					double50 *= double50;
					double19 += double50 * double50 * this.extrapolate(int1 + 1, int2 + 0, int3 + 1, double44, double48, double49);
				}

				double double51 = double16 - 0.0 - 0.6666666666666666;
				double double52 = 2.0 - double51 * double51 - double45 * double45 - double49 * double49;
				if (double52 > 0.0) {
					double52 *= double52;
					double19 += double52 * double52 * this.extrapolate(int1 + 0, int2 + 1, int3 + 1, double51, double45, double49);
				}
			}
		}

		double38 = 2.0 - double20 * double20 - double21 * double21 - double22 * double22;
		if (double38 > 0.0) {
			double38 *= double38;
			double19 += double38 * double38 * this.extrapolate(int4, int5, int6, double20, double21, double22);
		}

		double double53 = 2.0 - double23 * double23 - double24 * double24 - double25 * double25;
		if (double53 > 0.0) {
			double53 *= double53;
			double19 += double53 * double53 * this.extrapolate(int7, int8, int9, double23, double24, double25);
		}

		return double19 / 103.0;
	}

	public double eval(double double1, double double2, double double3, double double4) {
		double double5 = (double1 + double2 + double3 + double4) * -0.138196601125011;
		double double6 = double1 + double5;
		double double7 = double2 + double5;
		double double8 = double3 + double5;
		double double9 = double4 + double5;
		int int1 = fastFloor(double6);
		int int2 = fastFloor(double7);
		int int3 = fastFloor(double8);
		int int4 = fastFloor(double9);
		double double10 = (double)(int1 + int2 + int3 + int4) * 0.309016994374947;
		double double11 = (double)int1 + double10;
		double double12 = (double)int2 + double10;
		double double13 = (double)int3 + double10;
		double double14 = (double)int4 + double10;
		double double15 = double6 - (double)int1;
		double double16 = double7 - (double)int2;
		double double17 = double8 - (double)int3;
		double double18 = double9 - (double)int4;
		double double19 = double15 + double16 + double17 + double18;
		double double20 = double1 - double11;
		double double21 = double2 - double12;
		double double22 = double3 - double13;
		double double23 = double4 - double14;
		double double24 = 0.0;
		double double25;
		double double26;
		double double27;
		double double28;
		double double29;
		double double30;
		double double31;
		double double32;
		double double33;
		double double34;
		double double35;
		double double36;
		int int5;
		int int6;
		int int7;
		int int8;
		int int9;
		int int10;
		int int11;
		int int12;
		int int13;
		int int14;
		int int15;
		int int16;
		byte byte1;
		double double37;
		byte byte2;
		double double38;
		double double39;
		byte byte3;
		double double40;
		double double41;
		double double42;
		double double43;
		double double44;
		double double45;
		double double46;
		double double47;
		double double48;
		double double49;
		double double50;
		double double51;
		double double52;
		byte byte4;
		double double53;
		if (double19 <= 1.0) {
			byte1 = 1;
			double37 = double15;
			byte2 = 2;
			double38 = double16;
			if (double15 >= double16 && double17 > double16) {
				double38 = double17;
				byte2 = 4;
			} else if (double15 < double16 && double17 > double15) {
				double37 = double17;
				byte1 = 4;
			}

			if (double37 >= double38 && double18 > double38) {
				double38 = double18;
				byte2 = 8;
			} else if (double37 < double38 && double18 > double37) {
				double37 = double18;
				byte1 = 8;
			}

			double39 = 1.0 - double19;
			if (!(double39 > double37) && !(double39 > double38)) {
				byte4 = (byte)(byte1 | byte2);
				if ((byte4 & 1) == 0) {
					int13 = int1;
					int5 = int1;
					int9 = int1 - 1;
					double25 = double20 - 0.618033988749894;
					double29 = double20 + 1.0 - 0.309016994374947;
					double33 = double20 - 0.309016994374947;
				} else {
					int5 = int9 = int13 = int1 + 1;
					double25 = double20 - 1.0 - 0.618033988749894;
					double29 = double33 = double20 - 1.0 - 0.309016994374947;
				}

				if ((byte4 & 2) == 0) {
					int14 = int2;
					int10 = int2;
					int6 = int2;
					double26 = double21 - 0.618033988749894;
					double30 = double34 = double21 - 0.309016994374947;
					if ((byte4 & 1) == 1) {
						int10 = int2 - 1;
						++double30;
					} else {
						int14 = int2 - 1;
						++double34;
					}
				} else {
					int6 = int10 = int14 = int2 + 1;
					double26 = double21 - 1.0 - 0.618033988749894;
					double30 = double34 = double21 - 1.0 - 0.309016994374947;
				}

				if ((byte4 & 4) == 0) {
					int15 = int3;
					int11 = int3;
					int7 = int3;
					double27 = double22 - 0.618033988749894;
					double31 = double35 = double22 - 0.309016994374947;
					if ((byte4 & 3) == 3) {
						int11 = int3 - 1;
						++double31;
					} else {
						int15 = int3 - 1;
						++double35;
					}
				} else {
					int7 = int11 = int15 = int3 + 1;
					double27 = double22 - 1.0 - 0.618033988749894;
					double31 = double35 = double22 - 1.0 - 0.309016994374947;
				}

				if ((byte4 & 8) == 0) {
					int12 = int4;
					int8 = int4;
					int16 = int4 - 1;
					double28 = double23 - 0.618033988749894;
					double32 = double23 - 0.309016994374947;
					double36 = double23 + 1.0 - 0.309016994374947;
				} else {
					int8 = int12 = int16 = int4 + 1;
					double28 = double23 - 1.0 - 0.618033988749894;
					double32 = double36 = double23 - 1.0 - 0.309016994374947;
				}
			} else {
				byte3 = double38 > double37 ? byte2 : byte1;
				if ((byte3 & 1) == 0) {
					int5 = int1 - 1;
					int13 = int1;
					int9 = int1;
					double25 = double20 + 1.0;
					double33 = double20;
					double29 = double20;
				} else {
					int5 = int9 = int13 = int1 + 1;
					double25 = double29 = double33 = double20 - 1.0;
				}

				if ((byte3 & 2) == 0) {
					int14 = int2;
					int10 = int2;
					int6 = int2;
					double34 = double21;
					double30 = double21;
					double26 = double21;
					if ((byte3 & 1) == 1) {
						int6 = int2 - 1;
						double26 = double21 + 1.0;
					} else {
						int10 = int2 - 1;
						double30 = double21 + 1.0;
					}
				} else {
					int6 = int10 = int14 = int2 + 1;
					double26 = double30 = double34 = double21 - 1.0;
				}

				if ((byte3 & 4) == 0) {
					int15 = int3;
					int11 = int3;
					int7 = int3;
					double35 = double22;
					double31 = double22;
					double27 = double22;
					if ((byte3 & 3) != 0) {
						if ((byte3 & 3) == 3) {
							int7 = int3 - 1;
							double27 = double22 + 1.0;
						} else {
							int11 = int3 - 1;
							double31 = double22 + 1.0;
						}
					} else {
						int15 = int3 - 1;
						double35 = double22 + 1.0;
					}
				} else {
					int7 = int11 = int15 = int3 + 1;
					double27 = double31 = double35 = double22 - 1.0;
				}

				if ((byte3 & 8) == 0) {
					int12 = int4;
					int8 = int4;
					int16 = int4 - 1;
					double32 = double23;
					double28 = double23;
					double36 = double23 + 1.0;
				} else {
					int8 = int12 = int16 = int4 + 1;
					double28 = double32 = double36 = double23 - 1.0;
				}
			}

			double53 = 2.0 - double20 * double20 - double21 * double21 - double22 * double22 - double23 * double23;
			if (double53 > 0.0) {
				double53 *= double53;
				double24 += double53 * double53 * this.extrapolate(int1 + 0, int2 + 0, int3 + 0, int4 + 0, double20, double21, double22, double23);
			}

			double40 = double20 - 1.0 - 0.309016994374947;
			double41 = double21 - 0.0 - 0.309016994374947;
			double42 = double22 - 0.0 - 0.309016994374947;
			double43 = double23 - 0.0 - 0.309016994374947;
			double44 = 2.0 - double40 * double40 - double41 * double41 - double42 * double42 - double43 * double43;
			if (double44 > 0.0) {
				double44 *= double44;
				double24 += double44 * double44 * this.extrapolate(int1 + 1, int2 + 0, int3 + 0, int4 + 0, double40, double41, double42, double43);
			}

			double45 = double20 - 0.0 - 0.309016994374947;
			double46 = double21 - 1.0 - 0.309016994374947;
			double47 = 2.0 - double45 * double45 - double46 * double46 - double42 * double42 - double43 * double43;
			if (double47 > 0.0) {
				double47 *= double47;
				double24 += double47 * double47 * this.extrapolate(int1 + 0, int2 + 1, int3 + 0, int4 + 0, double45, double46, double42, double43);
			}

			double48 = double22 - 1.0 - 0.309016994374947;
			double49 = 2.0 - double45 * double45 - double41 * double41 - double48 * double48 - double43 * double43;
			if (double49 > 0.0) {
				double49 *= double49;
				double24 += double49 * double49 * this.extrapolate(int1 + 0, int2 + 0, int3 + 1, int4 + 0, double45, double41, double48, double43);
			}

			double50 = double23 - 1.0 - 0.309016994374947;
			double51 = 2.0 - double45 * double45 - double41 * double41 - double42 * double42 - double50 * double50;
			if (double51 > 0.0) {
				double51 *= double51;
				double24 += double51 * double51 * this.extrapolate(int1 + 0, int2 + 0, int3 + 0, int4 + 1, double45, double41, double42, double50);
			}
		} else {
			double double54;
			double double55;
			double double56;
			if (double19 >= 3.0) {
				byte1 = 14;
				double37 = double15;
				byte2 = 13;
				double38 = double16;
				if (double15 <= double16 && double17 < double16) {
					double38 = double17;
					byte2 = 11;
				} else if (double15 > double16 && double17 < double15) {
					double37 = double17;
					byte1 = 11;
				}

				if (double37 <= double38 && double18 < double38) {
					double38 = double18;
					byte2 = 7;
				} else if (double37 > double38 && double18 < double37) {
					double37 = double18;
					byte1 = 7;
				}

				double39 = 4.0 - double19;
				if (!(double39 < double37) && !(double39 < double38)) {
					byte4 = (byte)(byte1 & byte2);
					if ((byte4 & 1) != 0) {
						int5 = int13 = int1 + 1;
						int9 = int1 + 2;
						double25 = double20 - 1.0 - 0.618033988749894;
						double29 = double20 - 2.0 - 0.927050983124841;
						double33 = double20 - 1.0 - 0.927050983124841;
					} else {
						int13 = int1;
						int9 = int1;
						int5 = int1;
						double25 = double20 - 0.618033988749894;
						double29 = double33 = double20 - 0.927050983124841;
					}

					if ((byte4 & 2) != 0) {
						int6 = int10 = int14 = int2 + 1;
						double26 = double21 - 1.0 - 0.618033988749894;
						double30 = double34 = double21 - 1.0 - 0.927050983124841;
						if ((byte4 & 1) != 0) {
							++int14;
							--double34;
						} else {
							++int10;
							--double30;
						}
					} else {
						int14 = int2;
						int10 = int2;
						int6 = int2;
						double26 = double21 - 0.618033988749894;
						double30 = double34 = double21 - 0.927050983124841;
					}

					if ((byte4 & 4) != 0) {
						int7 = int11 = int15 = int3 + 1;
						double27 = double22 - 1.0 - 0.618033988749894;
						double31 = double35 = double22 - 1.0 - 0.927050983124841;
						if ((byte4 & 3) != 0) {
							++int15;
							--double35;
						} else {
							++int11;
							--double31;
						}
					} else {
						int15 = int3;
						int11 = int3;
						int7 = int3;
						double27 = double22 - 0.618033988749894;
						double31 = double35 = double22 - 0.927050983124841;
					}

					if ((byte4 & 8) != 0) {
						int8 = int12 = int4 + 1;
						int16 = int4 + 2;
						double28 = double23 - 1.0 - 0.618033988749894;
						double32 = double23 - 1.0 - 0.927050983124841;
						double36 = double23 - 2.0 - 0.927050983124841;
					} else {
						int16 = int4;
						int12 = int4;
						int8 = int4;
						double28 = double23 - 0.618033988749894;
						double32 = double36 = double23 - 0.927050983124841;
					}
				} else {
					byte3 = double38 < double37 ? byte2 : byte1;
					if ((byte3 & 1) != 0) {
						int5 = int1 + 2;
						int9 = int13 = int1 + 1;
						double25 = double20 - 2.0 - 1.236067977499788;
						double29 = double33 = double20 - 1.0 - 1.236067977499788;
					} else {
						int13 = int1;
						int9 = int1;
						int5 = int1;
						double25 = double29 = double33 = double20 - 1.236067977499788;
					}

					if ((byte3 & 2) != 0) {
						int6 = int10 = int14 = int2 + 1;
						double26 = double30 = double34 = double21 - 1.0 - 1.236067977499788;
						if ((byte3 & 1) != 0) {
							++int10;
							--double30;
						} else {
							++int6;
							--double26;
						}
					} else {
						int14 = int2;
						int10 = int2;
						int6 = int2;
						double26 = double30 = double34 = double21 - 1.236067977499788;
					}

					if ((byte3 & 4) != 0) {
						int7 = int11 = int15 = int3 + 1;
						double27 = double31 = double35 = double22 - 1.0 - 1.236067977499788;
						if ((byte3 & 3) != 3) {
							if ((byte3 & 3) == 0) {
								++int7;
								--double27;
							} else {
								++int11;
								--double31;
							}
						} else {
							++int15;
							--double35;
						}
					} else {
						int15 = int3;
						int11 = int3;
						int7 = int3;
						double27 = double31 = double35 = double22 - 1.236067977499788;
					}

					if ((byte3 & 8) != 0) {
						int8 = int12 = int4 + 1;
						int16 = int4 + 2;
						double28 = double32 = double23 - 1.0 - 1.236067977499788;
						double36 = double23 - 2.0 - 1.236067977499788;
					} else {
						int16 = int4;
						int12 = int4;
						int8 = int4;
						double28 = double32 = double36 = double23 - 1.236067977499788;
					}
				}

				double53 = double20 - 1.0 - 0.927050983124841;
				double40 = double21 - 1.0 - 0.927050983124841;
				double41 = double22 - 1.0 - 0.927050983124841;
				double42 = double23 - 0.927050983124841;
				double43 = 2.0 - double53 * double53 - double40 * double40 - double41 * double41 - double42 * double42;
				if (double43 > 0.0) {
					double43 *= double43;
					double24 += double43 * double43 * this.extrapolate(int1 + 1, int2 + 1, int3 + 1, int4 + 0, double53, double40, double41, double42);
				}

				double46 = double22 - 0.927050983124841;
				double54 = double23 - 1.0 - 0.927050983124841;
				double55 = 2.0 - double53 * double53 - double40 * double40 - double46 * double46 - double54 * double54;
				if (double55 > 0.0) {
					double55 *= double55;
					double24 += double55 * double55 * this.extrapolate(int1 + 1, int2 + 1, int3 + 0, int4 + 1, double53, double40, double46, double54);
				}

				double56 = double21 - 0.927050983124841;
				double double57 = 2.0 - double53 * double53 - double56 * double56 - double41 * double41 - double54 * double54;
				if (double57 > 0.0) {
					double57 *= double57;
					double24 += double57 * double57 * this.extrapolate(int1 + 1, int2 + 0, int3 + 1, int4 + 1, double53, double56, double41, double54);
				}

				double49 = double20 - 0.927050983124841;
				double50 = 2.0 - double49 * double49 - double40 * double40 - double41 * double41 - double54 * double54;
				if (double50 > 0.0) {
					double50 *= double50;
					double24 += double50 * double50 * this.extrapolate(int1 + 0, int2 + 1, int3 + 1, int4 + 1, double49, double40, double41, double54);
				}

				double20 = double20 - 1.0 - 1.236067977499788;
				double21 = double21 - 1.0 - 1.236067977499788;
				double22 = double22 - 1.0 - 1.236067977499788;
				double23 = double23 - 1.0 - 1.236067977499788;
				double51 = 2.0 - double20 * double20 - double21 * double21 - double22 * double22 - double23 * double23;
				if (double51 > 0.0) {
					double51 *= double51;
					double24 += double51 * double51 * this.extrapolate(int1 + 1, int2 + 1, int3 + 1, int4 + 1, double20, double21, double22, double23);
				}
			} else {
				byte byte5;
				boolean boolean1;
				byte byte6;
				double double58;
				double double59;
				double double60;
				double double61;
				double double62;
				double double63;
				double double64;
				double double65;
				double double66;
				double double67;
				double double68;
				double double69;
				double double70;
				double double71;
				double double72;
				double double73;
				double double74;
				double double75;
				double double76;
				double double77;
				double double78;
				double double79;
				double double80;
				double double81;
				double double82;
				double double83;
				double double84;
				double double85;
				double double86;
				double double87;
				double double88;
				double double89;
				boolean boolean2;
				byte byte7;
				byte byte8;
				byte byte9;
				byte byte10;
				if (double19 <= 2.0) {
					boolean2 = true;
					boolean1 = true;
					if (double15 + double16 > double17 + double18) {
						double52 = double15 + double16;
						byte5 = 3;
					} else {
						double52 = double17 + double18;
						byte5 = 12;
					}

					if (double15 + double17 > double16 + double18) {
						double38 = double15 + double17;
						byte7 = 5;
					} else {
						double38 = double16 + double18;
						byte7 = 10;
					}

					if (double15 + double18 > double16 + double17) {
						double53 = double15 + double18;
						if (double52 >= double38 && double53 > double38) {
							double38 = double53;
							byte7 = 9;
						} else if (double52 < double38 && double53 > double52) {
							double52 = double53;
							byte5 = 9;
						}
					} else {
						double53 = double16 + double17;
						if (double52 >= double38 && double53 > double38) {
							double38 = double53;
							byte7 = 6;
						} else if (double52 < double38 && double53 > double52) {
							double52 = double53;
							byte5 = 6;
						}
					}

					double53 = 2.0 - double19 + double15;
					if (double52 >= double38 && double53 > double38) {
						double38 = double53;
						byte7 = 1;
						boolean1 = false;
					} else if (double52 < double38 && double53 > double52) {
						double52 = double53;
						byte5 = 1;
						boolean2 = false;
					}

					double40 = 2.0 - double19 + double16;
					if (double52 >= double38 && double40 > double38) {
						double38 = double40;
						byte7 = 2;
						boolean1 = false;
					} else if (double52 < double38 && double40 > double52) {
						double52 = double40;
						byte5 = 2;
						boolean2 = false;
					}

					double41 = 2.0 - double19 + double17;
					if (double52 >= double38 && double41 > double38) {
						double38 = double41;
						byte7 = 4;
						boolean1 = false;
					} else if (double52 < double38 && double41 > double52) {
						double52 = double41;
						byte5 = 4;
						boolean2 = false;
					}

					double42 = 2.0 - double19 + double18;
					if (double52 >= double38 && double42 > double38) {
						byte7 = 8;
						boolean1 = false;
					} else if (double52 < double38 && double42 > double52) {
						byte5 = 8;
						boolean2 = false;
					}

					if (boolean2 == boolean1) {
						if (boolean2) {
							byte8 = (byte)(byte5 | byte7);
							byte6 = (byte)(byte5 & byte7);
							if ((byte8 & 1) == 0) {
								int5 = int1;
								int9 = int1 - 1;
								double25 = double20 - 0.927050983124841;
								double29 = double20 + 1.0 - 0.618033988749894;
							} else {
								int5 = int9 = int1 + 1;
								double25 = double20 - 1.0 - 0.927050983124841;
								double29 = double20 - 1.0 - 0.618033988749894;
							}

							if ((byte8 & 2) == 0) {
								int6 = int2;
								int10 = int2 - 1;
								double26 = double21 - 0.927050983124841;
								double30 = double21 + 1.0 - 0.618033988749894;
							} else {
								int6 = int10 = int2 + 1;
								double26 = double21 - 1.0 - 0.927050983124841;
								double30 = double21 - 1.0 - 0.618033988749894;
							}

							if ((byte8 & 4) == 0) {
								int7 = int3;
								int11 = int3 - 1;
								double27 = double22 - 0.927050983124841;
								double31 = double22 + 1.0 - 0.618033988749894;
							} else {
								int7 = int11 = int3 + 1;
								double27 = double22 - 1.0 - 0.927050983124841;
								double31 = double22 - 1.0 - 0.618033988749894;
							}

							if ((byte8 & 8) == 0) {
								int8 = int4;
								int12 = int4 - 1;
								double28 = double23 - 0.927050983124841;
								double32 = double23 + 1.0 - 0.618033988749894;
							} else {
								int8 = int12 = int4 + 1;
								double28 = double23 - 1.0 - 0.927050983124841;
								double32 = double23 - 1.0 - 0.618033988749894;
							}

							int13 = int1;
							int14 = int2;
							int15 = int3;
							int16 = int4;
							double33 = double20 - 0.618033988749894;
							double34 = double21 - 0.618033988749894;
							double35 = double22 - 0.618033988749894;
							double36 = double23 - 0.618033988749894;
							if ((byte6 & 1) != 0) {
								int13 = int1 + 2;
								double33 -= 2.0;
							} else if ((byte6 & 2) != 0) {
								int14 = int2 + 2;
								double34 -= 2.0;
							} else if ((byte6 & 4) != 0) {
								int15 = int3 + 2;
								double35 -= 2.0;
							} else {
								int16 = int4 + 2;
								double36 -= 2.0;
							}
						} else {
							int13 = int1;
							int14 = int2;
							int15 = int3;
							int16 = int4;
							double33 = double20;
							double34 = double21;
							double35 = double22;
							double36 = double23;
							byte8 = (byte)(byte5 | byte7);
							if ((byte8 & 1) == 0) {
								int5 = int1 - 1;
								int9 = int1;
								double25 = double20 + 1.0 - 0.309016994374947;
								double29 = double20 - 0.309016994374947;
							} else {
								int5 = int9 = int1 + 1;
								double25 = double29 = double20 - 1.0 - 0.309016994374947;
							}

							if ((byte8 & 2) == 0) {
								int10 = int2;
								int6 = int2;
								double26 = double30 = double21 - 0.309016994374947;
								if ((byte8 & 1) == 1) {
									int6 = int2 - 1;
									++double26;
								} else {
									int10 = int2 - 1;
									++double30;
								}
							} else {
								int6 = int10 = int2 + 1;
								double26 = double30 = double21 - 1.0 - 0.309016994374947;
							}

							if ((byte8 & 4) == 0) {
								int11 = int3;
								int7 = int3;
								double27 = double31 = double22 - 0.309016994374947;
								if ((byte8 & 3) == 3) {
									int7 = int3 - 1;
									++double27;
								} else {
									int11 = int3 - 1;
									++double31;
								}
							} else {
								int7 = int11 = int3 + 1;
								double27 = double31 = double22 - 1.0 - 0.309016994374947;
							}

							if ((byte8 & 8) == 0) {
								int8 = int4;
								int12 = int4 - 1;
								double28 = double23 - 0.309016994374947;
								double32 = double23 + 1.0 - 0.309016994374947;
							} else {
								int8 = int12 = int4 + 1;
								double28 = double32 = double23 - 1.0 - 0.309016994374947;
							}
						}
					} else {
						if (boolean2) {
							byte10 = byte5;
							byte9 = byte7;
						} else {
							byte10 = byte7;
							byte9 = byte5;
						}

						if ((byte10 & 1) == 0) {
							int5 = int1 - 1;
							int9 = int1;
							double25 = double20 + 1.0 - 0.309016994374947;
							double29 = double20 - 0.309016994374947;
						} else {
							int5 = int9 = int1 + 1;
							double25 = double29 = double20 - 1.0 - 0.309016994374947;
						}

						if ((byte10 & 2) == 0) {
							int10 = int2;
							int6 = int2;
							double26 = double30 = double21 - 0.309016994374947;
							if ((byte10 & 1) == 1) {
								int6 = int2 - 1;
								++double26;
							} else {
								int10 = int2 - 1;
								++double30;
							}
						} else {
							int6 = int10 = int2 + 1;
							double26 = double30 = double21 - 1.0 - 0.309016994374947;
						}

						if ((byte10 & 4) == 0) {
							int11 = int3;
							int7 = int3;
							double27 = double31 = double22 - 0.309016994374947;
							if ((byte10 & 3) == 3) {
								int7 = int3 - 1;
								++double27;
							} else {
								int11 = int3 - 1;
								++double31;
							}
						} else {
							int7 = int11 = int3 + 1;
							double27 = double31 = double22 - 1.0 - 0.309016994374947;
						}

						if ((byte10 & 8) == 0) {
							int8 = int4;
							int12 = int4 - 1;
							double28 = double23 - 0.309016994374947;
							double32 = double23 + 1.0 - 0.309016994374947;
						} else {
							int8 = int12 = int4 + 1;
							double28 = double32 = double23 - 1.0 - 0.309016994374947;
						}

						int13 = int1;
						int14 = int2;
						int15 = int3;
						int16 = int4;
						double33 = double20 - 0.618033988749894;
						double34 = double21 - 0.618033988749894;
						double35 = double22 - 0.618033988749894;
						double36 = double23 - 0.618033988749894;
						if ((byte9 & 1) != 0) {
							int13 = int1 + 2;
							double33 -= 2.0;
						} else if ((byte9 & 2) != 0) {
							int14 = int2 + 2;
							double34 -= 2.0;
						} else if ((byte9 & 4) != 0) {
							int15 = int3 + 2;
							double35 -= 2.0;
						} else {
							int16 = int4 + 2;
							double36 -= 2.0;
						}
					}

					double43 = double20 - 1.0 - 0.309016994374947;
					double44 = double21 - 0.0 - 0.309016994374947;
					double45 = double22 - 0.0 - 0.309016994374947;
					double46 = double23 - 0.0 - 0.309016994374947;
					double54 = 2.0 - double43 * double43 - double44 * double44 - double45 * double45 - double46 * double46;
					if (double54 > 0.0) {
						double54 *= double54;
						double24 += double54 * double54 * this.extrapolate(int1 + 1, int2 + 0, int3 + 0, int4 + 0, double43, double44, double45, double46);
					}

					double55 = double20 - 0.0 - 0.309016994374947;
					double47 = double21 - 1.0 - 0.309016994374947;
					double48 = 2.0 - double55 * double55 - double47 * double47 - double45 * double45 - double46 * double46;
					if (double48 > 0.0) {
						double48 *= double48;
						double24 += double48 * double48 * this.extrapolate(int1 + 0, int2 + 1, int3 + 0, int4 + 0, double55, double47, double45, double46);
					}

					double double90 = double22 - 1.0 - 0.309016994374947;
					double58 = 2.0 - double55 * double55 - double44 * double44 - double90 * double90 - double46 * double46;
					if (double58 > 0.0) {
						double58 *= double58;
						double24 += double58 * double58 * this.extrapolate(int1 + 0, int2 + 0, int3 + 1, int4 + 0, double55, double44, double90, double46);
					}

					double double91 = double23 - 1.0 - 0.309016994374947;
					double59 = 2.0 - double55 * double55 - double44 * double44 - double45 * double45 - double91 * double91;
					if (double59 > 0.0) {
						double59 *= double59;
						double24 += double59 * double59 * this.extrapolate(int1 + 0, int2 + 0, int3 + 0, int4 + 1, double55, double44, double45, double91);
					}

					double60 = double20 - 1.0 - 0.618033988749894;
					double61 = double21 - 1.0 - 0.618033988749894;
					double62 = double22 - 0.0 - 0.618033988749894;
					double63 = double23 - 0.0 - 0.618033988749894;
					double64 = 2.0 - double60 * double60 - double61 * double61 - double62 * double62 - double63 * double63;
					if (double64 > 0.0) {
						double64 *= double64;
						double24 += double64 * double64 * this.extrapolate(int1 + 1, int2 + 1, int3 + 0, int4 + 0, double60, double61, double62, double63);
					}

					double65 = double20 - 1.0 - 0.618033988749894;
					double66 = double21 - 0.0 - 0.618033988749894;
					double67 = double22 - 1.0 - 0.618033988749894;
					double68 = double23 - 0.0 - 0.618033988749894;
					double69 = 2.0 - double65 * double65 - double66 * double66 - double67 * double67 - double68 * double68;
					if (double69 > 0.0) {
						double69 *= double69;
						double24 += double69 * double69 * this.extrapolate(int1 + 1, int2 + 0, int3 + 1, int4 + 0, double65, double66, double67, double68);
					}

					double70 = double20 - 1.0 - 0.618033988749894;
					double71 = double21 - 0.0 - 0.618033988749894;
					double72 = double22 - 0.0 - 0.618033988749894;
					double73 = double23 - 1.0 - 0.618033988749894;
					double74 = 2.0 - double70 * double70 - double71 * double71 - double72 * double72 - double73 * double73;
					if (double74 > 0.0) {
						double74 *= double74;
						double24 += double74 * double74 * this.extrapolate(int1 + 1, int2 + 0, int3 + 0, int4 + 1, double70, double71, double72, double73);
					}

					double75 = double20 - 0.0 - 0.618033988749894;
					double76 = double21 - 1.0 - 0.618033988749894;
					double77 = double22 - 1.0 - 0.618033988749894;
					double78 = double23 - 0.0 - 0.618033988749894;
					double79 = 2.0 - double75 * double75 - double76 * double76 - double77 * double77 - double78 * double78;
					if (double79 > 0.0) {
						double79 *= double79;
						double24 += double79 * double79 * this.extrapolate(int1 + 0, int2 + 1, int3 + 1, int4 + 0, double75, double76, double77, double78);
					}

					double80 = double20 - 0.0 - 0.618033988749894;
					double81 = double21 - 1.0 - 0.618033988749894;
					double82 = double22 - 0.0 - 0.618033988749894;
					double83 = double23 - 1.0 - 0.618033988749894;
					double84 = 2.0 - double80 * double80 - double81 * double81 - double82 * double82 - double83 * double83;
					if (double84 > 0.0) {
						double84 *= double84;
						double24 += double84 * double84 * this.extrapolate(int1 + 0, int2 + 1, int3 + 0, int4 + 1, double80, double81, double82, double83);
					}

					double85 = double20 - 0.0 - 0.618033988749894;
					double86 = double21 - 0.0 - 0.618033988749894;
					double87 = double22 - 1.0 - 0.618033988749894;
					double88 = double23 - 1.0 - 0.618033988749894;
					double89 = 2.0 - double85 * double85 - double86 * double86 - double87 * double87 - double88 * double88;
					if (double89 > 0.0) {
						double89 *= double89;
						double24 += double89 * double89 * this.extrapolate(int1 + 0, int2 + 0, int3 + 1, int4 + 1, double85, double86, double87, double88);
					}
				} else {
					boolean2 = true;
					boolean1 = true;
					if (double15 + double16 < double17 + double18) {
						double52 = double15 + double16;
						byte5 = 12;
					} else {
						double52 = double17 + double18;
						byte5 = 3;
					}

					if (double15 + double17 < double16 + double18) {
						double38 = double15 + double17;
						byte7 = 10;
					} else {
						double38 = double16 + double18;
						byte7 = 5;
					}

					if (double15 + double18 < double16 + double17) {
						double53 = double15 + double18;
						if (double52 <= double38 && double53 < double38) {
							double38 = double53;
							byte7 = 6;
						} else if (double52 > double38 && double53 < double52) {
							double52 = double53;
							byte5 = 6;
						}
					} else {
						double53 = double16 + double17;
						if (double52 <= double38 && double53 < double38) {
							double38 = double53;
							byte7 = 9;
						} else if (double52 > double38 && double53 < double52) {
							double52 = double53;
							byte5 = 9;
						}
					}

					double53 = 3.0 - double19 + double15;
					if (double52 <= double38 && double53 < double38) {
						double38 = double53;
						byte7 = 14;
						boolean1 = false;
					} else if (double52 > double38 && double53 < double52) {
						double52 = double53;
						byte5 = 14;
						boolean2 = false;
					}

					double40 = 3.0 - double19 + double16;
					if (double52 <= double38 && double40 < double38) {
						double38 = double40;
						byte7 = 13;
						boolean1 = false;
					} else if (double52 > double38 && double40 < double52) {
						double52 = double40;
						byte5 = 13;
						boolean2 = false;
					}

					double41 = 3.0 - double19 + double17;
					if (double52 <= double38 && double41 < double38) {
						double38 = double41;
						byte7 = 11;
						boolean1 = false;
					} else if (double52 > double38 && double41 < double52) {
						double52 = double41;
						byte5 = 11;
						boolean2 = false;
					}

					double42 = 3.0 - double19 + double18;
					if (double52 <= double38 && double42 < double38) {
						byte7 = 7;
						boolean1 = false;
					} else if (double52 > double38 && double42 < double52) {
						byte5 = 7;
						boolean2 = false;
					}

					if (boolean2 == boolean1) {
						if (boolean2) {
							byte8 = (byte)(byte5 & byte7);
							byte6 = (byte)(byte5 | byte7);
							int9 = int1;
							int5 = int1;
							int10 = int2;
							int6 = int2;
							int11 = int3;
							int7 = int3;
							int12 = int4;
							int8 = int4;
							double25 = double20 - 0.309016994374947;
							double26 = double21 - 0.309016994374947;
							double27 = double22 - 0.309016994374947;
							double28 = double23 - 0.309016994374947;
							double29 = double20 - 0.618033988749894;
							double30 = double21 - 0.618033988749894;
							double31 = double22 - 0.618033988749894;
							double32 = double23 - 0.618033988749894;
							if ((byte8 & 1) != 0) {
								int5 = int1 + 1;
								--double25;
								int9 = int1 + 2;
								double29 -= 2.0;
							} else if ((byte8 & 2) != 0) {
								int6 = int2 + 1;
								--double26;
								int10 = int2 + 2;
								double30 -= 2.0;
							} else if ((byte8 & 4) != 0) {
								int7 = int3 + 1;
								--double27;
								int11 = int3 + 2;
								double31 -= 2.0;
							} else {
								int8 = int4 + 1;
								--double28;
								int12 = int4 + 2;
								double32 -= 2.0;
							}

							int13 = int1 + 1;
							int14 = int2 + 1;
							int15 = int3 + 1;
							int16 = int4 + 1;
							double33 = double20 - 1.0 - 0.618033988749894;
							double34 = double21 - 1.0 - 0.618033988749894;
							double35 = double22 - 1.0 - 0.618033988749894;
							double36 = double23 - 1.0 - 0.618033988749894;
							if ((byte6 & 1) == 0) {
								int13 -= 2;
								double33 += 2.0;
							} else if ((byte6 & 2) == 0) {
								int14 -= 2;
								double34 += 2.0;
							} else if ((byte6 & 4) == 0) {
								int15 -= 2;
								double35 += 2.0;
							} else {
								int16 -= 2;
								double36 += 2.0;
							}
						} else {
							int13 = int1 + 1;
							int14 = int2 + 1;
							int15 = int3 + 1;
							int16 = int4 + 1;
							double33 = double20 - 1.0 - 1.236067977499788;
							double34 = double21 - 1.0 - 1.236067977499788;
							double35 = double22 - 1.0 - 1.236067977499788;
							double36 = double23 - 1.0 - 1.236067977499788;
							byte8 = (byte)(byte5 & byte7);
							if ((byte8 & 1) != 0) {
								int5 = int1 + 2;
								int9 = int1 + 1;
								double25 = double20 - 2.0 - 0.927050983124841;
								double29 = double20 - 1.0 - 0.927050983124841;
							} else {
								int9 = int1;
								int5 = int1;
								double25 = double29 = double20 - 0.927050983124841;
							}

							if ((byte8 & 2) != 0) {
								int6 = int10 = int2 + 1;
								double26 = double30 = double21 - 1.0 - 0.927050983124841;
								if ((byte8 & 1) == 0) {
									++int6;
									--double26;
								} else {
									++int10;
									--double30;
								}
							} else {
								int10 = int2;
								int6 = int2;
								double26 = double30 = double21 - 0.927050983124841;
							}

							if ((byte8 & 4) != 0) {
								int7 = int11 = int3 + 1;
								double27 = double31 = double22 - 1.0 - 0.927050983124841;
								if ((byte8 & 3) == 0) {
									++int7;
									--double27;
								} else {
									++int11;
									--double31;
								}
							} else {
								int11 = int3;
								int7 = int3;
								double27 = double31 = double22 - 0.927050983124841;
							}

							if ((byte8 & 8) != 0) {
								int8 = int4 + 1;
								int12 = int4 + 2;
								double28 = double23 - 1.0 - 0.927050983124841;
								double32 = double23 - 2.0 - 0.927050983124841;
							} else {
								int12 = int4;
								int8 = int4;
								double28 = double32 = double23 - 0.927050983124841;
							}
						}
					} else {
						if (boolean2) {
							byte10 = byte5;
							byte9 = byte7;
						} else {
							byte10 = byte7;
							byte9 = byte5;
						}

						if ((byte10 & 1) != 0) {
							int5 = int1 + 2;
							int9 = int1 + 1;
							double25 = double20 - 2.0 - 0.927050983124841;
							double29 = double20 - 1.0 - 0.927050983124841;
						} else {
							int9 = int1;
							int5 = int1;
							double25 = double29 = double20 - 0.927050983124841;
						}

						if ((byte10 & 2) != 0) {
							int6 = int10 = int2 + 1;
							double26 = double30 = double21 - 1.0 - 0.927050983124841;
							if ((byte10 & 1) == 0) {
								++int6;
								--double26;
							} else {
								++int10;
								--double30;
							}
						} else {
							int10 = int2;
							int6 = int2;
							double26 = double30 = double21 - 0.927050983124841;
						}

						if ((byte10 & 4) != 0) {
							int7 = int11 = int3 + 1;
							double27 = double31 = double22 - 1.0 - 0.927050983124841;
							if ((byte10 & 3) == 0) {
								++int7;
								--double27;
							} else {
								++int11;
								--double31;
							}
						} else {
							int11 = int3;
							int7 = int3;
							double27 = double31 = double22 - 0.927050983124841;
						}

						if ((byte10 & 8) != 0) {
							int8 = int4 + 1;
							int12 = int4 + 2;
							double28 = double23 - 1.0 - 0.927050983124841;
							double32 = double23 - 2.0 - 0.927050983124841;
						} else {
							int12 = int4;
							int8 = int4;
							double28 = double32 = double23 - 0.927050983124841;
						}

						int13 = int1 + 1;
						int14 = int2 + 1;
						int15 = int3 + 1;
						int16 = int4 + 1;
						double33 = double20 - 1.0 - 0.618033988749894;
						double34 = double21 - 1.0 - 0.618033988749894;
						double35 = double22 - 1.0 - 0.618033988749894;
						double36 = double23 - 1.0 - 0.618033988749894;
						if ((byte9 & 1) == 0) {
							int13 -= 2;
							double33 += 2.0;
						} else if ((byte9 & 2) == 0) {
							int14 -= 2;
							double34 += 2.0;
						} else if ((byte9 & 4) == 0) {
							int15 -= 2;
							double35 += 2.0;
						} else {
							int16 -= 2;
							double36 += 2.0;
						}
					}

					double43 = double20 - 1.0 - 0.927050983124841;
					double44 = double21 - 1.0 - 0.927050983124841;
					double45 = double22 - 1.0 - 0.927050983124841;
					double46 = double23 - 0.927050983124841;
					double54 = 2.0 - double43 * double43 - double44 * double44 - double45 * double45 - double46 * double46;
					if (double54 > 0.0) {
						double54 *= double54;
						double24 += double54 * double54 * this.extrapolate(int1 + 1, int2 + 1, int3 + 1, int4 + 0, double43, double44, double45, double46);
					}

					double56 = double22 - 0.927050983124841;
					double double92 = double23 - 1.0 - 0.927050983124841;
					double48 = 2.0 - double43 * double43 - double44 * double44 - double56 * double56 - double92 * double92;
					if (double48 > 0.0) {
						double48 *= double48;
						double24 += double48 * double48 * this.extrapolate(int1 + 1, int2 + 1, int3 + 0, int4 + 1, double43, double44, double56, double92);
					}

					double49 = double21 - 0.927050983124841;
					double58 = 2.0 - double43 * double43 - double49 * double49 - double45 * double45 - double92 * double92;
					if (double58 > 0.0) {
						double58 *= double58;
						double24 += double58 * double58 * this.extrapolate(int1 + 1, int2 + 0, int3 + 1, int4 + 1, double43, double49, double45, double92);
					}

					double50 = double20 - 0.927050983124841;
					double59 = 2.0 - double50 * double50 - double44 * double44 - double45 * double45 - double92 * double92;
					if (double59 > 0.0) {
						double59 *= double59;
						double24 += double59 * double59 * this.extrapolate(int1 + 0, int2 + 1, int3 + 1, int4 + 1, double50, double44, double45, double92);
					}

					double60 = double20 - 1.0 - 0.618033988749894;
					double61 = double21 - 1.0 - 0.618033988749894;
					double62 = double22 - 0.0 - 0.618033988749894;
					double63 = double23 - 0.0 - 0.618033988749894;
					double64 = 2.0 - double60 * double60 - double61 * double61 - double62 * double62 - double63 * double63;
					if (double64 > 0.0) {
						double64 *= double64;
						double24 += double64 * double64 * this.extrapolate(int1 + 1, int2 + 1, int3 + 0, int4 + 0, double60, double61, double62, double63);
					}

					double65 = double20 - 1.0 - 0.618033988749894;
					double66 = double21 - 0.0 - 0.618033988749894;
					double67 = double22 - 1.0 - 0.618033988749894;
					double68 = double23 - 0.0 - 0.618033988749894;
					double69 = 2.0 - double65 * double65 - double66 * double66 - double67 * double67 - double68 * double68;
					if (double69 > 0.0) {
						double69 *= double69;
						double24 += double69 * double69 * this.extrapolate(int1 + 1, int2 + 0, int3 + 1, int4 + 0, double65, double66, double67, double68);
					}

					double70 = double20 - 1.0 - 0.618033988749894;
					double71 = double21 - 0.0 - 0.618033988749894;
					double72 = double22 - 0.0 - 0.618033988749894;
					double73 = double23 - 1.0 - 0.618033988749894;
					double74 = 2.0 - double70 * double70 - double71 * double71 - double72 * double72 - double73 * double73;
					if (double74 > 0.0) {
						double74 *= double74;
						double24 += double74 * double74 * this.extrapolate(int1 + 1, int2 + 0, int3 + 0, int4 + 1, double70, double71, double72, double73);
					}

					double75 = double20 - 0.0 - 0.618033988749894;
					double76 = double21 - 1.0 - 0.618033988749894;
					double77 = double22 - 1.0 - 0.618033988749894;
					double78 = double23 - 0.0 - 0.618033988749894;
					double79 = 2.0 - double75 * double75 - double76 * double76 - double77 * double77 - double78 * double78;
					if (double79 > 0.0) {
						double79 *= double79;
						double24 += double79 * double79 * this.extrapolate(int1 + 0, int2 + 1, int3 + 1, int4 + 0, double75, double76, double77, double78);
					}

					double80 = double20 - 0.0 - 0.618033988749894;
					double81 = double21 - 1.0 - 0.618033988749894;
					double82 = double22 - 0.0 - 0.618033988749894;
					double83 = double23 - 1.0 - 0.618033988749894;
					double84 = 2.0 - double80 * double80 - double81 * double81 - double82 * double82 - double83 * double83;
					if (double84 > 0.0) {
						double84 *= double84;
						double24 += double84 * double84 * this.extrapolate(int1 + 0, int2 + 1, int3 + 0, int4 + 1, double80, double81, double82, double83);
					}

					double85 = double20 - 0.0 - 0.618033988749894;
					double86 = double21 - 0.0 - 0.618033988749894;
					double87 = double22 - 1.0 - 0.618033988749894;
					double88 = double23 - 1.0 - 0.618033988749894;
					double89 = 2.0 - double85 * double85 - double86 * double86 - double87 * double87 - double88 * double88;
					if (double89 > 0.0) {
						double89 *= double89;
						double24 += double89 * double89 * this.extrapolate(int1 + 0, int2 + 0, int3 + 1, int4 + 1, double85, double86, double87, double88);
					}
				}
			}
		}

		double52 = 2.0 - double25 * double25 - double26 * double26 - double27 * double27 - double28 * double28;
		if (double52 > 0.0) {
			double52 *= double52;
			double24 += double52 * double52 * this.extrapolate(int5, int6, int7, int8, double25, double26, double27, double28);
		}

		double double93 = 2.0 - double29 * double29 - double30 * double30 - double31 * double31 - double32 * double32;
		if (double93 > 0.0) {
			double93 *= double93;
			double24 += double93 * double93 * this.extrapolate(int9, int10, int11, int12, double29, double30, double31, double32);
		}

		double38 = 2.0 - double33 * double33 - double34 * double34 - double35 * double35 - double36 * double36;
		if (double38 > 0.0) {
			double38 *= double38;
			double24 += double38 * double38 * this.extrapolate(int13, int14, int15, int16, double33, double34, double35, double36);
		}

		return double24 / 30.0;
	}

	private double extrapolate(int int1, int int2, double double1, double double2) {
		int int3 = this.perm[this.perm[int1 & 255] + int2 & 255] & 14;
		return (double)gradients2D[int3] * double1 + (double)gradients2D[int3 + 1] * double2;
	}

	private double extrapolate(int int1, int int2, int int3, double double1, double double2, double double3) {
		short short1 = this.permGradIndex3D[this.perm[this.perm[int1 & 255] + int2 & 255] + int3 & 255];
		return (double)gradients3D[short1] * double1 + (double)gradients3D[short1 + 1] * double2 + (double)gradients3D[short1 + 2] * double3;
	}

	private double extrapolate(int int1, int int2, int int3, int int4, double double1, double double2, double double3, double double4) {
		int int5 = this.perm[this.perm[this.perm[this.perm[int1 & 255] + int2 & 255] + int3 & 255] + int4 & 255] & 252;
		return (double)gradients4D[int5] * double1 + (double)gradients4D[int5 + 1] * double2 + (double)gradients4D[int5 + 2] * double3 + (double)gradients4D[int5 + 3] * double4;
	}

	private static int fastFloor(double double1) {
		int int1 = (int)double1;
		return double1 < (double)int1 ? int1 - 1 : int1;
	}

	public double evalOct(float float1, float float2, int int1) {
		boolean boolean1 = true;
		double double1 = this.eval((double)float1, (double)float2, (double)int1);
		for (int int2 = 2; int2 <= 64; ++int2) {
			double1 += this.eval((double)(float1 * (float)int2 * float1), (double)(float2 * (float)int2 * float2), (double)(int1 * int2 * int1));
		}

		return double1;
	}
}
