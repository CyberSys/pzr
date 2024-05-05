package com.jcraft.jorbis;


public class DspState {
	static final float M_PI = 3.1415927F;
	static final int VI_TRANSFORMB = 1;
	static final int VI_WINDOWB = 1;
	int analysisp;
	int centerW;
	int envelope_current;
	int envelope_storage;
	int eofflag;
	long floor_bits;
	CodeBook[] fullbooks;
	long glue_bits;
	long granulepos;
	byte[] header;
	byte[] header1;
	byte[] header2;
	int lW;
	Object[] mode;
	int modebits;
	float[] multipliers;
	int nW;
	float[][] pcm;
	int pcm_current;
	int pcm_returned;
	int pcm_storage;
	long res_bits;
	long sequence;
	long time_bits;
	Object[][] transform;
	Info vi;
	int W;
	float[][][][][] window;

	public DspState() {
		this.transform = new Object[2][];
		this.window = new float[2][][][][];
		this.window[0] = new float[2][][][];
		this.window[0][0] = new float[2][][];
		this.window[0][1] = new float[2][][];
		this.window[0][0][0] = new float[2][];
		this.window[0][0][1] = new float[2][];
		this.window[0][1][0] = new float[2][];
		this.window[0][1][1] = new float[2][];
		this.window[1] = new float[2][][][];
		this.window[1][0] = new float[2][][];
		this.window[1][1] = new float[2][][];
		this.window[1][0][0] = new float[2][];
		this.window[1][0][1] = new float[2][];
		this.window[1][1][0] = new float[2][];
		this.window[1][1][1] = new float[2][];
	}

	DspState(Info info) {
		this();
		this.init(info, false);
		this.pcm_returned = this.centerW;
		this.centerW -= info.blocksizes[this.W] / 4 + info.blocksizes[this.lW] / 4;
		this.granulepos = -1L;
		this.sequence = -1L;
	}

	static float[] window(int int1, int int2, int int3, int int4) {
		float[] floatArray = new float[int2];
		switch (int1) {
		case 0: 
			int int5 = int2 / 4 - int3 / 2;
			int int6 = int2 - int2 / 4 - int4 / 2;
			int int7;
			float float1;
			for (int7 = 0; int7 < int3; ++int7) {
				float1 = (float)(((double)int7 + 0.5) / (double)int3 * 3.1415927410125732 / 2.0);
				float1 = (float)Math.sin((double)float1);
				float1 *= float1;
				float1 = (float)((double)float1 * 1.5707963705062866);
				float1 = (float)Math.sin((double)float1);
				floatArray[int7 + int5] = float1;
			}

			for (int7 = int5 + int3; int7 < int6; ++int7) {
				floatArray[int7] = 1.0F;
			}

			for (int7 = 0; int7 < int4; ++int7) {
				float1 = (float)(((double)(int4 - int7) - 0.5) / (double)int4 * 3.1415927410125732 / 2.0);
				float1 = (float)Math.sin((double)float1);
				float1 *= float1;
				float1 = (float)((double)float1 * 1.5707963705062866);
				float1 = (float)Math.sin((double)float1);
				floatArray[int7 + int6] = float1;
			}

			return floatArray;
		
		default: 
			return null;
		
		}
	}

	public void clear() {
	}

	public int synthesis_blockin(Block block) {
		int int1;
		int int2;
		if (this.centerW > this.vi.blocksizes[1] / 2 && this.pcm_returned > 8192) {
			int1 = this.centerW - this.vi.blocksizes[1] / 2;
			int1 = this.pcm_returned < int1 ? this.pcm_returned : int1;
			this.pcm_current -= int1;
			this.centerW -= int1;
			this.pcm_returned -= int1;
			if (int1 != 0) {
				for (int2 = 0; int2 < this.vi.channels; ++int2) {
					System.arraycopy(this.pcm[int2], int1, this.pcm[int2], 0, this.pcm_current);
				}
			}
		}

		this.lW = this.W;
		this.W = block.W;
		this.nW = -1;
		this.glue_bits += (long)block.glue_bits;
		this.time_bits += (long)block.time_bits;
		this.floor_bits += (long)block.floor_bits;
		this.res_bits += (long)block.res_bits;
		if (this.sequence + 1L != block.sequence) {
			this.granulepos = -1L;
		}

		this.sequence = block.sequence;
		int1 = this.vi.blocksizes[this.W];
		int2 = this.centerW + this.vi.blocksizes[this.lW] / 4 + int1 / 4;
		int int3 = int2 - int1 / 2;
		int int4 = int3 + int1;
		int int5 = 0;
		int int6 = 0;
		int int7;
		if (int4 > this.pcm_storage) {
			this.pcm_storage = int4 + this.vi.blocksizes[1];
			for (int7 = 0; int7 < this.vi.channels; ++int7) {
				float[] floatArray = new float[this.pcm_storage];
				System.arraycopy(this.pcm[int7], 0, floatArray, 0, this.pcm[int7].length);
				this.pcm[int7] = floatArray;
			}
		}

		switch (this.W) {
		case 0: 
			int5 = 0;
			int6 = this.vi.blocksizes[0] / 2;
			break;
		
		case 1: 
			int5 = this.vi.blocksizes[1] / 4 - this.vi.blocksizes[this.lW] / 4;
			int6 = int5 + this.vi.blocksizes[this.lW] / 2;
		
		}
		for (int7 = 0; int7 < this.vi.channels; ++int7) {
			int int8 = int3;
			boolean boolean1 = false;
			int int9;
			for (int9 = int5; int9 < int6; ++int9) {
				float[] floatArray2 = this.pcm[int7];
				floatArray2[int8 + int9] += block.pcm[int7][int9];
			}

			while (int9 < int1) {
				this.pcm[int7][int8 + int9] = block.pcm[int7][int9];
				++int9;
			}
		}

		if (this.granulepos == -1L) {
			this.granulepos = block.granulepos;
		} else {
			this.granulepos += (long)(int2 - this.centerW);
			if (block.granulepos != -1L && this.granulepos != block.granulepos) {
				if (this.granulepos > block.granulepos && block.eofflag != 0) {
					int2 = (int)((long)int2 - (this.granulepos - block.granulepos));
				}

				this.granulepos = block.granulepos;
			}
		}

		this.centerW = int2;
		this.pcm_current = int4;
		if (block.eofflag != 0) {
			this.eofflag = 1;
		}

		return 0;
	}

	public int synthesis_init(Info info) {
		this.init(info, false);
		this.pcm_returned = this.centerW;
		this.centerW -= info.blocksizes[this.W] / 4 + info.blocksizes[this.lW] / 4;
		this.granulepos = -1L;
		this.sequence = -1L;
		return 0;
	}

	public int synthesis_pcmout(float[][][] floatArrayArrayArray, int[] intArray) {
		if (this.pcm_returned >= this.centerW) {
			return 0;
		} else {
			if (floatArrayArrayArray != null) {
				for (int int1 = 0; int1 < this.vi.channels; ++int1) {
					intArray[int1] = this.pcm_returned;
				}

				floatArrayArrayArray[0] = this.pcm;
			}

			return this.centerW - this.pcm_returned;
		}
	}

	public int synthesis_read(int int1) {
		if (int1 != 0 && this.pcm_returned + int1 > this.centerW) {
			return -1;
		} else {
			this.pcm_returned += int1;
			return 0;
		}
	}

	int init(Info info, boolean boolean1) {
		this.vi = info;
		this.modebits = Util.ilog2(info.modes);
		this.transform[0] = new Object[1];
		this.transform[1] = new Object[1];
		this.transform[0][0] = new Mdct();
		this.transform[1][0] = new Mdct();
		((Mdct)this.transform[0][0]).init(info.blocksizes[0]);
		((Mdct)this.transform[1][0]).init(info.blocksizes[1]);
		this.window[0][0][0] = new float[1][];
		this.window[0][0][1] = this.window[0][0][0];
		this.window[0][1][0] = this.window[0][0][0];
		this.window[0][1][1] = this.window[0][0][0];
		this.window[1][0][0] = new float[1][];
		this.window[1][0][1] = new float[1][];
		this.window[1][1][0] = new float[1][];
		this.window[1][1][1] = new float[1][];
		int int1;
		for (int1 = 0; int1 < 1; ++int1) {
			this.window[0][0][0][int1] = window(int1, info.blocksizes[0], info.blocksizes[0] / 2, info.blocksizes[0] / 2);
			this.window[1][0][0][int1] = window(int1, info.blocksizes[1], info.blocksizes[0] / 2, info.blocksizes[0] / 2);
			this.window[1][0][1][int1] = window(int1, info.blocksizes[1], info.blocksizes[0] / 2, info.blocksizes[1] / 2);
			this.window[1][1][0][int1] = window(int1, info.blocksizes[1], info.blocksizes[1] / 2, info.blocksizes[0] / 2);
			this.window[1][1][1][int1] = window(int1, info.blocksizes[1], info.blocksizes[1] / 2, info.blocksizes[1] / 2);
		}

		this.fullbooks = new CodeBook[info.books];
		for (int1 = 0; int1 < info.books; ++int1) {
			this.fullbooks[int1] = new CodeBook();
			this.fullbooks[int1].init_decode(info.book_param[int1]);
		}

		this.pcm_storage = 8192;
		this.pcm = new float[info.channels][];
		for (int1 = 0; int1 < info.channels; ++int1) {
			this.pcm[int1] = new float[this.pcm_storage];
		}

		this.lW = 0;
		this.W = 0;
		this.centerW = info.blocksizes[1] / 2;
		this.pcm_current = this.centerW;
		this.mode = new Object[info.modes];
		for (int1 = 0; int1 < info.modes; ++int1) {
			int int2 = info.mode_param[int1].mapping;
			int int3 = info.map_type[int2];
			this.mode[int1] = FuncMapping.mapping_P[int3].look(this, info.mode_param[int1], info.map_param[int2]);
		}

		return 0;
	}
}
