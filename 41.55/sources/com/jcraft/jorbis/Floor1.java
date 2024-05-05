package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


class Floor1 extends FuncFloor {
	static final int floor1_rangedb = 140;
	static final int VIF_POSIT = 63;
	private static float[] FLOOR_fromdB_LOOKUP = new float[]{1.0649863E-7F, 1.1341951E-7F, 1.2079015E-7F, 1.2863978E-7F, 1.369995E-7F, 1.459025E-7F, 1.5538409E-7F, 1.6548181E-7F, 1.7623574E-7F, 1.8768856E-7F, 1.998856E-7F, 2.128753E-7F, 2.2670913E-7F, 2.4144197E-7F, 2.5713223E-7F, 2.7384212E-7F, 2.9163792E-7F, 3.1059022E-7F, 3.307741E-7F, 3.5226967E-7F, 3.7516213E-7F, 3.995423E-7F, 4.255068E-7F, 4.5315863E-7F, 4.8260745E-7F, 5.1397E-7F, 5.4737063E-7F, 5.829419E-7F, 6.208247E-7F, 6.611694E-7F, 7.041359E-7F, 7.4989464E-7F, 7.98627E-7F, 8.505263E-7F, 9.057983E-7F, 9.646621E-7F, 1.0273513E-6F, 1.0941144E-6F, 1.1652161E-6F, 1.2409384E-6F, 1.3215816E-6F, 1.4074654E-6F, 1.4989305E-6F, 1.5963394E-6F, 1.7000785E-6F, 1.8105592E-6F, 1.9282195E-6F, 2.053526E-6F, 2.1869757E-6F, 2.3290977E-6F, 2.4804558E-6F, 2.6416496E-6F, 2.813319E-6F, 2.9961443E-6F, 3.1908505E-6F, 3.39821E-6F, 3.619045E-6F, 3.8542307E-6F, 4.1047006E-6F, 4.371447E-6F, 4.6555283E-6F, 4.958071E-6F, 5.280274E-6F, 5.623416E-6F, 5.988857E-6F, 6.3780467E-6F, 6.7925284E-6F, 7.2339453E-6F, 7.704048E-6F, 8.2047E-6F, 8.737888E-6F, 9.305725E-6F, 9.910464E-6F, 1.0554501E-5F, 1.1240392E-5F, 1.1970856E-5F, 1.2748789E-5F, 1.3577278E-5F, 1.4459606E-5F, 1.5399271E-5F, 1.6400005E-5F, 1.7465769E-5F, 1.8600793E-5F, 1.9809577E-5F, 2.1096914E-5F, 2.2467912E-5F, 2.3928002E-5F, 2.5482977E-5F, 2.7139005E-5F, 2.890265E-5F, 3.078091E-5F, 3.2781227E-5F, 3.4911533E-5F, 3.718028E-5F, 3.9596467E-5F, 4.2169668E-5F, 4.491009E-5F, 4.7828602E-5F, 5.0936775E-5F, 5.424693E-5F, 5.7772202E-5F, 6.152657E-5F, 6.552491E-5F, 6.9783084E-5F, 7.4317984E-5F, 7.914758E-5F, 8.429104E-5F, 8.976875E-5F, 9.560242E-5F, 1.0181521E-4F, 1.0843174E-4F, 1.1547824E-4F, 1.2298267E-4F, 1.3097477E-4F, 1.3948625E-4F, 1.4855085E-4F, 1.5820454E-4F, 1.6848555E-4F, 1.7943469E-4F, 1.9109536E-4F, 2.0351382E-4F, 2.167393E-4F, 2.3082423E-4F, 2.4582449E-4F, 2.6179955E-4F, 2.7881275E-4F, 2.9693157E-4F, 3.1622787E-4F, 3.3677815E-4F, 3.5866388E-4F, 3.8197188E-4F, 4.0679457E-4F, 4.3323037E-4F, 4.613841E-4F, 4.913675E-4F, 5.2329927E-4F, 5.573062E-4F, 5.935231E-4F, 6.320936E-4F, 6.731706E-4F, 7.16917E-4F, 7.635063E-4F, 8.1312325E-4F, 8.6596457E-4F, 9.2223985E-4F, 9.821722E-4F, 0.0010459992F, 0.0011139743F, 0.0011863665F, 0.0012634633F, 0.0013455702F, 0.0014330129F, 0.0015261382F, 0.0016253153F, 0.0017309374F, 0.0018434235F, 0.0019632196F, 0.0020908006F, 0.0022266726F, 0.0023713743F, 0.0025254795F, 0.0026895993F, 0.0028643848F, 0.0030505287F, 0.003248769F, 0.0034598925F, 0.0036847359F, 0.0039241905F, 0.0041792067F, 0.004450795F, 0.004740033F, 0.005048067F, 0.0053761187F, 0.005725489F, 0.0060975635F, 0.0064938175F, 0.0069158226F, 0.0073652514F, 0.007843887F, 0.008353627F, 0.008896492F, 0.009474637F, 0.010090352F, 0.01074608F, 0.011444421F, 0.012188144F, 0.012980198F, 0.013823725F, 0.014722068F, 0.015678791F, 0.016697686F, 0.017782796F, 0.018938422F, 0.020169148F, 0.021479854F, 0.022875736F, 0.02436233F, 0.025945531F, 0.027631618F, 0.029427277F, 0.031339627F, 0.03337625F, 0.035545226F, 0.037855156F, 0.0403152F, 0.042935107F, 0.045725275F, 0.048696756F, 0.05186135F, 0.05523159F, 0.05882085F, 0.062643364F, 0.06671428F, 0.07104975F, 0.075666964F, 0.08058423F, 0.08582105F, 0.09139818F, 0.097337745F, 0.1036633F, 0.11039993F, 0.11757434F, 0.12521498F, 0.13335215F, 0.14201812F, 0.15124726F, 0.16107617F, 0.1715438F, 0.18269168F, 0.19456401F, 0.20720787F, 0.22067343F, 0.23501402F, 0.25028655F, 0.26655158F, 0.28387362F, 0.3023213F, 0.32196787F, 0.34289113F, 0.36517414F, 0.3889052F, 0.41417846F, 0.44109413F, 0.4697589F, 0.50028646F, 0.53279793F, 0.5674221F, 0.6042964F, 0.64356697F, 0.6853896F, 0.72993004F, 0.777365F, 0.8278826F, 0.88168305F, 0.9389798F, 1.0F};

	private static void render_line(int int1, int int2, int int3, int int4, float[] floatArray) {
		int int5 = int4 - int3;
		int int6 = int2 - int1;
		int int7 = Math.abs(int5);
		int int8 = int5 / int6;
		int int9 = int5 < 0 ? int8 - 1 : int8 + 1;
		int int10 = int1;
		int int11 = int3;
		int int12 = 0;
		int7 -= Math.abs(int8 * int6);
		floatArray[int1] *= FLOOR_fromdB_LOOKUP[int3];
		while (true) {
			++int10;
			if (int10 >= int2) {
				return;
			}

			int12 += int7;
			if (int12 >= int6) {
				int12 -= int6;
				int11 += int9;
			} else {
				int11 += int8;
			}

			floatArray[int10] *= FLOOR_fromdB_LOOKUP[int11];
		}
	}

	private static int render_point(int int1, int int2, int int3, int int4, int int5) {
		int3 &= 32767;
		int4 &= 32767;
		int int6 = int4 - int3;
		int int7 = int2 - int1;
		int int8 = Math.abs(int6);
		int int9 = int8 * (int5 - int1);
		int int10 = int9 / int7;
		return int6 < 0 ? int3 - int10 : int3 + int10;
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

	Object inverse1(Block block, Object object, Object object2) {
		Floor1.LookFloor1 lookFloor1 = (Floor1.LookFloor1)object;
		Floor1.InfoFloor1 infoFloor1 = lookFloor1.vi;
		CodeBook[] codeBookArray = block.vd.fullbooks;
		if (block.opb.read(1) == 1) {
			int[] intArray = null;
			if (object2 instanceof int[]) {
				intArray = (int[])object2;
			}

			int int1;
			if (intArray != null && intArray.length >= lookFloor1.posts) {
				for (int1 = 0; int1 < intArray.length; ++int1) {
					intArray[int1] = 0;
				}
			} else {
				intArray = new int[lookFloor1.posts];
			}

			intArray[0] = block.opb.read(Util.ilog(lookFloor1.quant_q - 1));
			intArray[1] = block.opb.read(Util.ilog(lookFloor1.quant_q - 1));
			int1 = 0;
			int int2;
			int int3;
			int int4;
			int int5;
			for (int2 = 2; int1 < infoFloor1.partitions; ++int1) {
				int3 = infoFloor1.partitionclass[int1];
				int int6 = infoFloor1.class_dim[int3];
				int4 = infoFloor1.class_subs[int3];
				int5 = 1 << int4;
				int int7 = 0;
				if (int4 != 0) {
					int7 = codeBookArray[infoFloor1.class_book[int3]].decode(block.opb);
					if (int7 == -1) {
						return null;
					}
				}

				for (int int8 = 0; int8 < int6; ++int8) {
					int int9 = infoFloor1.class_subbook[int3][int7 & int5 - 1];
					int7 >>>= int4;
					if (int9 >= 0) {
						if ((intArray[int2 + int8] = codeBookArray[int9].decode(block.opb)) == -1) {
							return null;
						}
					} else {
						intArray[int2 + int8] = 0;
					}
				}

				int2 += int6;
			}

			for (int1 = 2; int1 < lookFloor1.posts; ++int1) {
				int2 = render_point(infoFloor1.postlist[lookFloor1.loneighbor[int1 - 2]], infoFloor1.postlist[lookFloor1.hineighbor[int1 - 2]], intArray[lookFloor1.loneighbor[int1 - 2]], intArray[lookFloor1.hineighbor[int1 - 2]], infoFloor1.postlist[int1]);
				int3 = lookFloor1.quant_q - int2;
				int4 = (int3 < int2 ? int3 : int2) << 1;
				int5 = intArray[int1];
				if (int5 != 0) {
					if (int5 >= int4) {
						if (int3 > int2) {
							int5 -= int2;
						} else {
							int5 = -1 - (int5 - int3);
						}
					} else if ((int5 & 1) != 0) {
						int5 = -(int5 + 1 >>> 1);
					} else {
						int5 >>= 1;
					}

					intArray[int1] = int5 + int2;
					int int10 = lookFloor1.loneighbor[int1 - 2];
					intArray[int10] &= 32767;
					int10 = lookFloor1.hineighbor[int1 - 2];
					intArray[int10] &= 32767;
				} else {
					intArray[int1] = int2 | '耀';
				}
			}

			return intArray;
		} else {
			return null;
		}
	}

	int inverse2(Block block, Object object, Object object2, float[] floatArray) {
		Floor1.LookFloor1 lookFloor1 = (Floor1.LookFloor1)object;
		Floor1.InfoFloor1 infoFloor1 = lookFloor1.vi;
		int int1 = block.vd.vi.blocksizes[block.mode] / 2;
		if (object2 != null) {
			int[] intArray = (int[])object2;
			int int2 = 0;
			int int3 = 0;
			int int4 = intArray[0] * infoFloor1.mult;
			int int5;
			for (int5 = 1; int5 < lookFloor1.posts; ++int5) {
				int int6 = lookFloor1.forward_index[int5];
				int int7 = intArray[int6] & 32767;
				if (int7 == intArray[int6]) {
					int7 *= infoFloor1.mult;
					int2 = infoFloor1.postlist[int6];
					render_line(int3, int2, int4, int7, floatArray);
					int3 = int2;
					int4 = int7;
				}
			}

			for (int5 = int2; int5 < int1; ++int5) {
				floatArray[int5] *= floatArray[int5 - 1];
			}

			return 1;
		} else {
			for (int int8 = 0; int8 < int1; ++int8) {
				floatArray[int8] = 0.0F;
			}

			return 0;
		}
	}

	Object look(DspState dspState, InfoMode infoMode, Object object) {
		int int1 = 0;
		int[] intArray = new int[65];
		Floor1.InfoFloor1 infoFloor1 = (Floor1.InfoFloor1)object;
		Floor1.LookFloor1 lookFloor1 = new Floor1.LookFloor1();
		lookFloor1.vi = infoFloor1;
		lookFloor1.n = infoFloor1.postlist[1];
		int int2;
		for (int2 = 0; int2 < infoFloor1.partitions; ++int2) {
			int1 += infoFloor1.class_dim[infoFloor1.partitionclass[int2]];
		}

		int1 += 2;
		lookFloor1.posts = int1;
		for (int2 = 0; int2 < int1; intArray[int2] = int2++) {
		}

		int int3;
		int int4;
		for (int3 = 0; int3 < int1 - 1; ++int3) {
			for (int4 = int3; int4 < int1; ++int4) {
				if (infoFloor1.postlist[intArray[int3]] > infoFloor1.postlist[intArray[int4]]) {
					int2 = intArray[int4];
					intArray[int4] = intArray[int3];
					intArray[int3] = int2;
				}
			}
		}

		for (int3 = 0; int3 < int1; ++int3) {
			lookFloor1.forward_index[int3] = intArray[int3];
		}

		for (int3 = 0; int3 < int1; lookFloor1.reverse_index[lookFloor1.forward_index[int3]] = int3++) {
		}

		for (int3 = 0; int3 < int1; ++int3) {
			lookFloor1.sorted_index[int3] = infoFloor1.postlist[lookFloor1.forward_index[int3]];
		}

		switch (infoFloor1.mult) {
		case 1: 
			lookFloor1.quant_q = 256;
			break;
		
		case 2: 
			lookFloor1.quant_q = 128;
			break;
		
		case 3: 
			lookFloor1.quant_q = 86;
			break;
		
		case 4: 
			lookFloor1.quant_q = 64;
			break;
		
		default: 
			lookFloor1.quant_q = -1;
		
		}
		for (int3 = 0; int3 < int1 - 2; ++int3) {
			int4 = 0;
			int int5 = 1;
			int int6 = 0;
			int int7 = lookFloor1.n;
			int int8 = infoFloor1.postlist[int3 + 2];
			for (int int9 = 0; int9 < int3 + 2; ++int9) {
				int int10 = infoFloor1.postlist[int9];
				if (int10 > int6 && int10 < int8) {
					int4 = int9;
					int6 = int10;
				}

				if (int10 < int7 && int10 > int8) {
					int5 = int9;
					int7 = int10;
				}
			}

			lookFloor1.loneighbor[int3] = int4;
			lookFloor1.hineighbor[int3] = int5;
		}

		return lookFloor1;
	}

	void pack(Object object, Buffer buffer) {
		Floor1.InfoFloor1 infoFloor1 = (Floor1.InfoFloor1)object;
		int int1 = 0;
		int int2 = infoFloor1.postlist[1];
		int int3 = -1;
		buffer.write(infoFloor1.partitions, 5);
		int int4;
		for (int4 = 0; int4 < infoFloor1.partitions; ++int4) {
			buffer.write(infoFloor1.partitionclass[int4], 4);
			if (int3 < infoFloor1.partitionclass[int4]) {
				int3 = infoFloor1.partitionclass[int4];
			}
		}

		int int5;
		for (int4 = 0; int4 < int3 + 1; ++int4) {
			buffer.write(infoFloor1.class_dim[int4] - 1, 3);
			buffer.write(infoFloor1.class_subs[int4], 2);
			if (infoFloor1.class_subs[int4] != 0) {
				buffer.write(infoFloor1.class_book[int4], 8);
			}

			for (int5 = 0; int5 < 1 << infoFloor1.class_subs[int4]; ++int5) {
				buffer.write(infoFloor1.class_subbook[int4][int5] + 1, 8);
			}
		}

		buffer.write(infoFloor1.mult - 1, 2);
		buffer.write(Util.ilog2(int2), 4);
		int int6 = Util.ilog2(int2);
		int4 = 0;
		for (int5 = 0; int4 < infoFloor1.partitions; ++int4) {
			for (int1 += infoFloor1.class_dim[infoFloor1.partitionclass[int4]]; int5 < int1; ++int5) {
				buffer.write(infoFloor1.postlist[int5 + 2], int6);
			}
		}
	}

	Object unpack(Info info, Buffer buffer) {
		int int1 = 0;
		int int2 = -1;
		Floor1.InfoFloor1 infoFloor1 = new Floor1.InfoFloor1();
		infoFloor1.partitions = buffer.read(5);
		int int3;
		for (int3 = 0; int3 < infoFloor1.partitions; ++int3) {
			infoFloor1.partitionclass[int3] = buffer.read(4);
			if (int2 < infoFloor1.partitionclass[int3]) {
				int2 = infoFloor1.partitionclass[int3];
			}
		}

		int int4;
		for (int3 = 0; int3 < int2 + 1; ++int3) {
			infoFloor1.class_dim[int3] = buffer.read(3) + 1;
			infoFloor1.class_subs[int3] = buffer.read(2);
			if (infoFloor1.class_subs[int3] < 0) {
				infoFloor1.free();
				return null;
			}

			if (infoFloor1.class_subs[int3] != 0) {
				infoFloor1.class_book[int3] = buffer.read(8);
			}

			if (infoFloor1.class_book[int3] < 0 || infoFloor1.class_book[int3] >= info.books) {
				infoFloor1.free();
				return null;
			}

			for (int4 = 0; int4 < 1 << infoFloor1.class_subs[int3]; ++int4) {
				infoFloor1.class_subbook[int3][int4] = buffer.read(8) - 1;
				if (infoFloor1.class_subbook[int3][int4] < -1 || infoFloor1.class_subbook[int3][int4] >= info.books) {
					infoFloor1.free();
					return null;
				}
			}
		}

		infoFloor1.mult = buffer.read(2) + 1;
		int int5 = buffer.read(4);
		int3 = 0;
		for (int4 = 0; int3 < infoFloor1.partitions; ++int3) {
			for (int1 += infoFloor1.class_dim[infoFloor1.partitionclass[int3]]; int4 < int1; ++int4) {
				int int6 = infoFloor1.postlist[int4 + 2] = buffer.read(int5);
				if (int6 < 0 || int6 >= 1 << int5) {
					infoFloor1.free();
					return null;
				}
			}
		}

		infoFloor1.postlist[0] = 0;
		infoFloor1.postlist[1] = 1 << int5;
		return infoFloor1;
	}

	class LookFloor1 {
		static final int VIF_POSIT = 63;
		int[] forward_index = new int[65];
		int frames;
		int[] hineighbor = new int[63];
		int[] loneighbor = new int[63];
		int n;
		int phrasebits;
		int postbits;
		int posts;
		int quant_q;
		int[] reverse_index = new int[65];
		int[] sorted_index = new int[65];
		Floor1.InfoFloor1 vi;

		void free() {
			this.sorted_index = null;
			this.forward_index = null;
			this.reverse_index = null;
			this.hineighbor = null;
			this.loneighbor = null;
		}
	}

	class InfoFloor1 {
		static final int VIF_POSIT = 63;
		static final int VIF_CLASS = 16;
		static final int VIF_PARTS = 31;
		int[] class_book = new int[16];
		int[] class_dim = new int[16];
		int[][] class_subbook = new int[16][];
		int[] class_subs = new int[16];
		float maxerr;
		float maxover;
		float maxunder;
		int mult;
		int n;
		int[] partitionclass = new int[31];
		int partitions;
		int[] postlist = new int[65];
		float twofitatten;
		int twofitminsize;
		int twofitminused;
		int twofitweight;
		int unusedmin_n;
		int unusedminsize;

		InfoFloor1() {
			for (int int1 = 0; int1 < this.class_subbook.length; ++int1) {
				this.class_subbook[int1] = new int[8];
			}
		}

		Object copy_info() {
			Floor1.InfoFloor1 infoFloor1 = this;
			Floor1.InfoFloor1 infoFloor12 = Floor1.this.new InfoFloor1();
			infoFloor12.partitions = this.partitions;
			System.arraycopy(this.partitionclass, 0, infoFloor12.partitionclass, 0, 31);
			System.arraycopy(this.class_dim, 0, infoFloor12.class_dim, 0, 16);
			System.arraycopy(this.class_subs, 0, infoFloor12.class_subs, 0, 16);
			System.arraycopy(this.class_book, 0, infoFloor12.class_book, 0, 16);
			for (int int1 = 0; int1 < 16; ++int1) {
				System.arraycopy(infoFloor1.class_subbook[int1], 0, infoFloor12.class_subbook[int1], 0, 8);
			}

			infoFloor12.mult = infoFloor1.mult;
			System.arraycopy(infoFloor1.postlist, 0, infoFloor12.postlist, 0, 65);
			infoFloor12.maxover = infoFloor1.maxover;
			infoFloor12.maxunder = infoFloor1.maxunder;
			infoFloor12.maxerr = infoFloor1.maxerr;
			infoFloor12.twofitminsize = infoFloor1.twofitminsize;
			infoFloor12.twofitminused = infoFloor1.twofitminused;
			infoFloor12.twofitweight = infoFloor1.twofitweight;
			infoFloor12.twofitatten = infoFloor1.twofitatten;
			infoFloor12.unusedminsize = infoFloor1.unusedminsize;
			infoFloor12.unusedmin_n = infoFloor1.unusedmin_n;
			infoFloor12.n = infoFloor1.n;
			return infoFloor12;
		}

		void free() {
			this.partitionclass = null;
			this.class_dim = null;
			this.class_subs = null;
			this.class_book = null;
			this.class_subbook = null;
			this.postlist = null;
		}
	}

	class EchstateFloor1 {
		long codes;
		int[] codewords;
		float[] curve;
		long frameno;
	}

	class Lsfit_acc {
		long an;
		long edgey0;
		long edgey1;
		long n;
		long un;
		long x0;
		long x1;
		long x2a;
		long xa;
		long xya;
		long y2a;
		long ya;
	}
}
