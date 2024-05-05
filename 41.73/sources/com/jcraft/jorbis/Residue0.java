package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


class Residue0 extends FuncResidue {
	private static int[][][] _01inverse_partword = new int[2][][];
	static int[][] _2inverse_partword = null;

	static synchronized int _01inverse(Block block, Object object, float[][] floatArrayArray, int int1, int int2) {
		Residue0.LookResidue0 lookResidue0 = (Residue0.LookResidue0)object;
		Residue0.InfoResidue0 infoResidue0 = lookResidue0.info;
		int int3 = infoResidue0.grouping;
		int int4 = lookResidue0.phrasebook.dim;
		int int5 = infoResidue0.end - infoResidue0.begin;
		int int6 = int5 / int3;
		int int7 = (int6 + int4 - 1) / int4;
		if (_01inverse_partword.length < int1) {
			_01inverse_partword = new int[int1][][];
		}

		int int8;
		for (int8 = 0; int8 < int1; ++int8) {
			if (_01inverse_partword[int8] == null || _01inverse_partword[int8].length < int7) {
				_01inverse_partword[int8] = new int[int7][];
			}
		}

		for (int int9 = 0; int9 < lookResidue0.stages; ++int9) {
			int int10 = 0;
			for (int int11 = 0; int10 < int6; ++int11) {
				int int12;
				if (int9 == 0) {
					for (int8 = 0; int8 < int1; ++int8) {
						int12 = lookResidue0.phrasebook.decode(block.opb);
						if (int12 == -1) {
							return 0;
						}

						_01inverse_partword[int8][int11] = lookResidue0.decodemap[int12];
						if (_01inverse_partword[int8][int11] == null) {
							return 0;
						}
					}
				}

				for (int int13 = 0; int13 < int4 && int10 < int6; ++int10) {
					for (int8 = 0; int8 < int1; ++int8) {
						int12 = infoResidue0.begin + int10 * int3;
						int int14 = _01inverse_partword[int8][int11][int13];
						if ((infoResidue0.secondstages[int14] & 1 << int9) != 0) {
							CodeBook codeBook = lookResidue0.fullbooks[lookResidue0.partbooks[int14][int9]];
							if (codeBook != null) {
								if (int2 == 0) {
									if (codeBook.decodevs_add(floatArrayArray[int8], int12, block.opb, int3) == -1) {
										return 0;
									}
								} else if (int2 == 1 && codeBook.decodev_add(floatArrayArray[int8], int12, block.opb, int3) == -1) {
									return 0;
								}
							}
						}
					}

					++int13;
				}
			}
		}

		return 0;
	}

	static synchronized int _2inverse(Block block, Object object, float[][] floatArrayArray, int int1) {
		Residue0.LookResidue0 lookResidue0 = (Residue0.LookResidue0)object;
		Residue0.InfoResidue0 infoResidue0 = lookResidue0.info;
		int int2 = infoResidue0.grouping;
		int int3 = lookResidue0.phrasebook.dim;
		int int4 = infoResidue0.end - infoResidue0.begin;
		int int5 = int4 / int2;
		int int6 = (int5 + int3 - 1) / int3;
		if (_2inverse_partword == null || _2inverse_partword.length < int6) {
			_2inverse_partword = new int[int6][];
		}

		for (int int7 = 0; int7 < lookResidue0.stages; ++int7) {
			int int8 = 0;
			for (int int9 = 0; int8 < int5; ++int9) {
				int int10;
				if (int7 == 0) {
					int10 = lookResidue0.phrasebook.decode(block.opb);
					if (int10 == -1) {
						return 0;
					}

					_2inverse_partword[int9] = lookResidue0.decodemap[int10];
					if (_2inverse_partword[int9] == null) {
						return 0;
					}
				}

				for (int int11 = 0; int11 < int3 && int8 < int5; ++int8) {
					int10 = infoResidue0.begin + int8 * int2;
					int int12 = _2inverse_partword[int9][int11];
					if ((infoResidue0.secondstages[int12] & 1 << int7) != 0) {
						CodeBook codeBook = lookResidue0.fullbooks[lookResidue0.partbooks[int12][int7]];
						if (codeBook != null && codeBook.decodevv_add(floatArrayArray, int10, int1, block.opb, int2) == -1) {
							return 0;
						}
					}

					++int11;
				}
			}
		}

		return 0;
	}

	void free_info(Object object) {
	}

	void free_look(Object object) {
	}

	int inverse(Block block, Object object, float[][] floatArrayArray, int[] intArray, int int1) {
		int int2 = 0;
		for (int int3 = 0; int3 < int1; ++int3) {
			if (intArray[int3] != 0) {
				floatArrayArray[int2++] = floatArrayArray[int3];
			}
		}

		if (int2 != 0) {
			return _01inverse(block, object, floatArrayArray, int2, 0);
		} else {
			return 0;
		}
	}

	Object look(DspState dspState, InfoMode infoMode, Object object) {
		Residue0.InfoResidue0 infoResidue0 = (Residue0.InfoResidue0)object;
		Residue0.LookResidue0 lookResidue0 = new Residue0.LookResidue0();
		int int1 = 0;
		int int2 = 0;
		lookResidue0.info = infoResidue0;
		lookResidue0.map = infoMode.mapping;
		lookResidue0.parts = infoResidue0.partitions;
		lookResidue0.fullbooks = dspState.fullbooks;
		lookResidue0.phrasebook = dspState.fullbooks[infoResidue0.groupbook];
		int int3 = lookResidue0.phrasebook.dim;
		lookResidue0.partbooks = new int[lookResidue0.parts][];
		int int4;
		int int5;
		int int6;
		int int7;
		for (int4 = 0; int4 < lookResidue0.parts; ++int4) {
			int5 = infoResidue0.secondstages[int4];
			int6 = Util.ilog(int5);
			if (int6 != 0) {
				if (int6 > int2) {
					int2 = int6;
				}

				lookResidue0.partbooks[int4] = new int[int6];
				for (int7 = 0; int7 < int6; ++int7) {
					if ((int5 & 1 << int7) != 0) {
						lookResidue0.partbooks[int4][int7] = infoResidue0.booklist[int1++];
					}
				}
			}
		}

		lookResidue0.partvals = (int)Math.rint(Math.pow((double)lookResidue0.parts, (double)int3));
		lookResidue0.stages = int2;
		lookResidue0.decodemap = new int[lookResidue0.partvals][];
		for (int4 = 0; int4 < lookResidue0.partvals; ++int4) {
			int5 = int4;
			int6 = lookResidue0.partvals / lookResidue0.parts;
			lookResidue0.decodemap[int4] = new int[int3];
			for (int7 = 0; int7 < int3; ++int7) {
				int int8 = int5 / int6;
				int5 -= int8 * int6;
				int6 /= lookResidue0.parts;
				lookResidue0.decodemap[int4][int7] = int8;
			}
		}

		return lookResidue0;
	}

	void pack(Object object, Buffer buffer) {
		Residue0.InfoResidue0 infoResidue0 = (Residue0.InfoResidue0)object;
		int int1 = 0;
		buffer.write(infoResidue0.begin, 24);
		buffer.write(infoResidue0.end, 24);
		buffer.write(infoResidue0.grouping - 1, 24);
		buffer.write(infoResidue0.partitions - 1, 6);
		buffer.write(infoResidue0.groupbook, 8);
		int int2;
		for (int2 = 0; int2 < infoResidue0.partitions; ++int2) {
			int int3 = infoResidue0.secondstages[int2];
			if (Util.ilog(int3) > 3) {
				buffer.write(int3, 3);
				buffer.write(1, 1);
				buffer.write(int3 >>> 3, 5);
			} else {
				buffer.write(int3, 4);
			}

			int1 += Util.icount(int3);
		}

		for (int2 = 0; int2 < int1; ++int2) {
			buffer.write(infoResidue0.booklist[int2], 8);
		}
	}

	Object unpack(Info info, Buffer buffer) {
		int int1 = 0;
		Residue0.InfoResidue0 infoResidue0 = new Residue0.InfoResidue0();
		infoResidue0.begin = buffer.read(24);
		infoResidue0.end = buffer.read(24);
		infoResidue0.grouping = buffer.read(24) + 1;
		infoResidue0.partitions = buffer.read(6) + 1;
		infoResidue0.groupbook = buffer.read(8);
		int int2;
		for (int2 = 0; int2 < infoResidue0.partitions; ++int2) {
			int int3 = buffer.read(3);
			if (buffer.read(1) != 0) {
				int3 |= buffer.read(5) << 3;
			}

			infoResidue0.secondstages[int2] = int3;
			int1 += Util.icount(int3);
		}

		for (int2 = 0; int2 < int1; ++int2) {
			infoResidue0.booklist[int2] = buffer.read(8);
		}

		if (infoResidue0.groupbook >= info.books) {
			this.free_info(infoResidue0);
			return null;
		} else {
			for (int2 = 0; int2 < int1; ++int2) {
				if (infoResidue0.booklist[int2] >= info.books) {
					this.free_info(infoResidue0);
					return null;
				}
			}

			return infoResidue0;
		}
	}

	class LookResidue0 {
		int[][] decodemap;
		int frames;
		CodeBook[] fullbooks;
		Residue0.InfoResidue0 info;
		int map;
		int[][] partbooks;
		int parts;
		int partvals;
		int phrasebits;
		CodeBook phrasebook;
		int postbits;
		int stages;
	}

	class InfoResidue0 {
		float[] ampmax = new float[64];
		int begin;
		int[] blimit = new int[64];
		int[] booklist = new int[256];
		int end;
		float[] entmax = new float[64];
		int groupbook;
		int grouping;
		int partitions;
		int[] secondstages = new int[64];
		int[] subgrp = new int[64];
	}
}
