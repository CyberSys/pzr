package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


class Floor0 extends FuncFloor {
	float[] lsp = null;

	static float fromdB(float float1) {
		return (float)Math.exp((double)float1 * 0.11512925);
	}

	static void lpc_to_curve(float[] floatArray, float[] floatArray2, float float1, Floor0.LookFloor0 lookFloor0, String string, int int1) {
		float[] floatArray3 = new float[Math.max(lookFloor0.ln * 2, lookFloor0.m * 2 + 2)];
		int int2;
		if (float1 == 0.0F) {
			for (int2 = 0; int2 < lookFloor0.n; ++int2) {
				floatArray[int2] = 0.0F;
			}
		} else {
			lookFloor0.lpclook.lpc_to_curve(floatArray3, floatArray2, float1);
			for (int2 = 0; int2 < lookFloor0.n; ++int2) {
				floatArray[int2] = floatArray3[lookFloor0.linearmap[int2]];
			}
		}
	}

	static void lsp_to_lpc(float[] floatArray, float[] floatArray2, int int1) {
		int int2 = int1 / 2;
		float[] floatArray3 = new float[int2];
		float[] floatArray4 = new float[int2];
		float[] floatArray5 = new float[int2 + 1];
		float[] floatArray6 = new float[int2 + 1];
		float[] floatArray7 = new float[int2];
		float[] floatArray8 = new float[int2];
		int int3;
		for (int3 = 0; int3 < int2; ++int3) {
			floatArray3[int3] = (float)(-2.0 * Math.cos((double)floatArray[int3 * 2]));
			floatArray4[int3] = (float)(-2.0 * Math.cos((double)floatArray[int3 * 2 + 1]));
		}

		int int4;
		for (int4 = 0; int4 < int2; ++int4) {
			floatArray5[int4] = 0.0F;
			floatArray6[int4] = 1.0F;
			floatArray7[int4] = 0.0F;
			floatArray8[int4] = 1.0F;
		}

		floatArray6[int4] = 1.0F;
		floatArray5[int4] = 1.0F;
		for (int3 = 1; int3 < int1 + 1; ++int3) {
			float float1 = 0.0F;
			float float2 = 0.0F;
			for (int4 = 0; int4 < int2; ++int4) {
				float float3 = floatArray3[int4] * floatArray6[int4] + floatArray5[int4];
				floatArray5[int4] = floatArray6[int4];
				floatArray6[int4] = float2;
				float2 += float3;
				float3 = floatArray4[int4] * floatArray8[int4] + floatArray7[int4];
				floatArray7[int4] = floatArray8[int4];
				floatArray8[int4] = float1;
				float1 += float3;
			}

			floatArray2[int3 - 1] = (float2 + floatArray6[int4] + float1 - floatArray5[int4]) / 2.0F;
			floatArray6[int4] = float2;
			floatArray5[int4] = float1;
		}
	}

	static float toBARK(float float1) {
		return (float)(13.1 * Math.atan(7.4E-4 * (double)float1) + 2.24 * Math.atan((double)(float1 * float1) * 1.85E-8) + 1.0E-4 * (double)float1);
	}

	int forward(Block block, Object object, float[] floatArray, float[] floatArray2, Object object2) {
		return 0;
	}

	void free_info(Object object) {
	}

	void free_look(Object object) {
	}

	void free_state(Object object) {
	}

	int inverse(Block block, Object object, float[] floatArray) {
		Floor0.LookFloor0 lookFloor0 = (Floor0.LookFloor0)object;
		Floor0.InfoFloor0 infoFloor0 = lookFloor0.vi;
		int int1 = block.opb.read(infoFloor0.ampbits);
		if (int1 > 0) {
			int int2 = (1 << infoFloor0.ampbits) - 1;
			float float1 = (float)int1 / (float)int2 * (float)infoFloor0.ampdB;
			int int3 = block.opb.read(Util.ilog(infoFloor0.numbooks));
			if (int3 != -1 && int3 < infoFloor0.numbooks) {
				synchronized (this) {
					if (this.lsp != null && this.lsp.length >= lookFloor0.m) {
						for (int int4 = 0; int4 < lookFloor0.m; ++int4) {
							this.lsp[int4] = 0.0F;
						}
					} else {
						this.lsp = new float[lookFloor0.m];
					}

					CodeBook codeBook = block.vd.fullbooks[infoFloor0.books[int3]];
					float float2 = 0.0F;
					int int5;
					for (int5 = 0; int5 < lookFloor0.m; ++int5) {
						floatArray[int5] = 0.0F;
					}

					int int6;
					for (int5 = 0; int5 < lookFloor0.m; int5 += codeBook.dim) {
						if (codeBook.decodevs(this.lsp, int5, block.opb, 1, -1) == -1) {
							for (int6 = 0; int6 < lookFloor0.n; ++int6) {
								floatArray[int6] = 0.0F;
							}

							return 0;
						}
					}

					for (int5 = 0; int5 < lookFloor0.m; float2 = this.lsp[int5 - 1]) {
						for (int6 = 0; int6 < codeBook.dim; ++int5) {
							float[] floatArray2 = this.lsp;
							floatArray2[int5] += float2;
							++int6;
						}
					}

					Lsp.lsp_to_curve(floatArray, lookFloor0.linearmap, lookFloor0.n, lookFloor0.ln, this.lsp, lookFloor0.m, float1, (float)infoFloor0.ampdB);
					return 1;
				}
			}
		}

		return 0;
	}

	Object inverse1(Block block, Object object, Object object2) {
		Floor0.LookFloor0 lookFloor0 = (Floor0.LookFloor0)object;
		Floor0.InfoFloor0 infoFloor0 = lookFloor0.vi;
		float[] floatArray = null;
		if (object2 instanceof float[]) {
			floatArray = (float[])object2;
		}

		int int1 = block.opb.read(infoFloor0.ampbits);
		if (int1 > 0) {
			int int2 = (1 << infoFloor0.ampbits) - 1;
			float float1 = (float)int1 / (float)int2 * (float)infoFloor0.ampdB;
			int int3 = block.opb.read(Util.ilog(infoFloor0.numbooks));
			if (int3 != -1 && int3 < infoFloor0.numbooks) {
				CodeBook codeBook = block.vd.fullbooks[infoFloor0.books[int3]];
				float float2 = 0.0F;
				int int4;
				if (floatArray != null && floatArray.length >= lookFloor0.m + 1) {
					for (int4 = 0; int4 < floatArray.length; ++int4) {
						floatArray[int4] = 0.0F;
					}
				} else {
					floatArray = new float[lookFloor0.m + 1];
				}

				for (int4 = 0; int4 < lookFloor0.m; int4 += codeBook.dim) {
					if (codeBook.decodev_set(floatArray, int4, block.opb, codeBook.dim) == -1) {
						return null;
					}
				}

				for (int4 = 0; int4 < lookFloor0.m; float2 = floatArray[int4 - 1]) {
					for (int int5 = 0; int5 < codeBook.dim; ++int4) {
						floatArray[int4] += float2;
						++int5;
					}
				}

				floatArray[lookFloor0.m] = float1;
				return floatArray;
			}
		}

		return null;
	}

	int inverse2(Block block, Object object, Object object2, float[] floatArray) {
		Floor0.LookFloor0 lookFloor0 = (Floor0.LookFloor0)object;
		Floor0.InfoFloor0 infoFloor0 = lookFloor0.vi;
		if (object2 != null) {
			float[] floatArray2 = (float[])object2;
			float float1 = floatArray2[lookFloor0.m];
			Lsp.lsp_to_curve(floatArray, lookFloor0.linearmap, lookFloor0.n, lookFloor0.ln, floatArray2, lookFloor0.m, float1, (float)infoFloor0.ampdB);
			return 1;
		} else {
			for (int int1 = 0; int1 < lookFloor0.n; ++int1) {
				floatArray[int1] = 0.0F;
			}

			return 0;
		}
	}

	Object look(DspState dspState, InfoMode infoMode, Object object) {
		Info info = dspState.vi;
		Floor0.InfoFloor0 infoFloor0 = (Floor0.InfoFloor0)object;
		Floor0.LookFloor0 lookFloor0 = new Floor0.LookFloor0();
		lookFloor0.m = infoFloor0.order;
		lookFloor0.n = info.blocksizes[infoMode.blockflag] / 2;
		lookFloor0.ln = infoFloor0.barkmap;
		lookFloor0.vi = infoFloor0;
		lookFloor0.lpclook.init(lookFloor0.ln, lookFloor0.m);
		float float1 = (float)lookFloor0.ln / toBARK((float)((double)infoFloor0.rate / 2.0));
		lookFloor0.linearmap = new int[lookFloor0.n];
		for (int int1 = 0; int1 < lookFloor0.n; ++int1) {
			int int2 = (int)Math.floor((double)(toBARK((float)((double)infoFloor0.rate / 2.0 / (double)lookFloor0.n * (double)int1)) * float1));
			if (int2 >= lookFloor0.ln) {
				int2 = lookFloor0.ln;
			}

			lookFloor0.linearmap[int1] = int2;
		}

		return lookFloor0;
	}

	void pack(Object object, Buffer buffer) {
		Floor0.InfoFloor0 infoFloor0 = (Floor0.InfoFloor0)object;
		buffer.write(infoFloor0.order, 8);
		buffer.write(infoFloor0.rate, 16);
		buffer.write(infoFloor0.barkmap, 16);
		buffer.write(infoFloor0.ampbits, 6);
		buffer.write(infoFloor0.ampdB, 8);
		buffer.write(infoFloor0.numbooks - 1, 4);
		for (int int1 = 0; int1 < infoFloor0.numbooks; ++int1) {
			buffer.write(infoFloor0.books[int1], 8);
		}
	}

	Object state(Object object) {
		Floor0.EchstateFloor0 echstateFloor0 = new Floor0.EchstateFloor0();
		Floor0.InfoFloor0 infoFloor0 = (Floor0.InfoFloor0)object;
		echstateFloor0.codewords = new int[infoFloor0.order];
		echstateFloor0.curve = new float[infoFloor0.barkmap];
		echstateFloor0.frameno = -1L;
		return echstateFloor0;
	}

	Object unpack(Info info, Buffer buffer) {
		Floor0.InfoFloor0 infoFloor0 = new Floor0.InfoFloor0();
		infoFloor0.order = buffer.read(8);
		infoFloor0.rate = buffer.read(16);
		infoFloor0.barkmap = buffer.read(16);
		infoFloor0.ampbits = buffer.read(6);
		infoFloor0.ampdB = buffer.read(8);
		infoFloor0.numbooks = buffer.read(4) + 1;
		if (infoFloor0.order >= 1 && infoFloor0.rate >= 1 && infoFloor0.barkmap >= 1 && infoFloor0.numbooks >= 1) {
			for (int int1 = 0; int1 < infoFloor0.numbooks; ++int1) {
				infoFloor0.books[int1] = buffer.read(8);
				if (infoFloor0.books[int1] < 0 || infoFloor0.books[int1] >= info.books) {
					return null;
				}
			}

			return infoFloor0;
		} else {
			return null;
		}
	}

	class LookFloor0 {
		int[] linearmap;
		int ln;
		Lpc lpclook = new Lpc();
		int m;
		int n;
		Floor0.InfoFloor0 vi;
	}

	class InfoFloor0 {
		int ampbits;
		int ampdB;
		int barkmap;
		int[] books = new int[16];
		int numbooks;
		int order;
		int rate;
	}

	class EchstateFloor0 {
		long codes;
		int[] codewords;
		float[] curve;
		long frameno;
	}
}
