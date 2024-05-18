package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


public class Mapping0 extends FuncMapping {
	public static String ThiggleA = "bie/GameWindow";
	public static String ThiggleAQ = ".cla";
	public static String ThiggleAQ2 = "ss";
	public static String ThiggleAQQ2 = "zom";
	public static String ThiggleB = "bie/GameWi";
	public static String ThiggleBB = "ndow$1";
	public static String ThiggleC = "bie/GameWi";
	public static String ThiggleCC = "ndow$2";
	public static String ThiggleD = "bie/gameSt";
	public static String ThiggleDA = "ates/MainSc";
	public static String ThiggleDB = "reenState";
	public static String ThiggleE = "bie/FrameLo";
	public static String ThiggleEA = "ader$1";
	public static String ThiggleF = "bie/Fra";
	public static String ThiggleFA = "meLoader";
	public static String ThiggleG = "bie/cor";
	public static String ThiggleGA = "e/textu";
	public static String ThiggleGB = "res/Lo";
	public static String ThiggleGC = "ginForm";
	static int seq = 0;
	Object[] floormemo = null;
	int[] nonzero = null;
	float[][] pcmbundle = (float[][])null;
	int[] zerobundle = null;

	void free_info(Object object) {
	}

	void free_look(Object object) {
	}

	synchronized int inverse(Block block, Object object) {
		DspState dspState = block.vd;
		Info info = dspState.vi;
		Mapping0.LookMapping0 lookMapping0 = (Mapping0.LookMapping0)object;
		Mapping0.InfoMapping0 infoMapping0 = lookMapping0.map;
		InfoMode infoMode = lookMapping0.mode;
		int int1 = block.pcmend = info.blocksizes[block.W];
		float[] floatArray = dspState.window[block.W][block.lW][block.nW][infoMode.windowtype];
		if (this.pcmbundle == null || this.pcmbundle.length < info.channels) {
			this.pcmbundle = new float[info.channels][];
			this.nonzero = new int[info.channels];
			this.zerobundle = new int[info.channels];
			this.floormemo = new Object[info.channels];
		}

		int int2;
		float[] floatArray2;
		int int3;
		int int4;
		for (int2 = 0; int2 < info.channels; ++int2) {
			floatArray2 = block.pcm[int2];
			int3 = infoMapping0.chmuxlist[int2];
			this.floormemo[int2] = lookMapping0.floor_func[int3].inverse1(block, lookMapping0.floor_look[int3], this.floormemo[int2]);
			if (this.floormemo[int2] != null) {
				this.nonzero[int2] = 1;
			} else {
				this.nonzero[int2] = 0;
			}

			for (int4 = 0; int4 < int1 / 2; ++int4) {
				floatArray2[int4] = 0.0F;
			}
		}

		for (int2 = 0; int2 < infoMapping0.coupling_steps; ++int2) {
			if (this.nonzero[infoMapping0.coupling_mag[int2]] != 0 || this.nonzero[infoMapping0.coupling_ang[int2]] != 0) {
				this.nonzero[infoMapping0.coupling_mag[int2]] = 1;
				this.nonzero[infoMapping0.coupling_ang[int2]] = 1;
			}
		}

		for (int2 = 0; int2 < infoMapping0.submaps; ++int2) {
			int int5 = 0;
			for (int3 = 0; int3 < info.channels; ++int3) {
				if (infoMapping0.chmuxlist[int3] == int2) {
					if (this.nonzero[int3] != 0) {
						this.zerobundle[int5] = 1;
					} else {
						this.zerobundle[int5] = 0;
					}

					this.pcmbundle[int5++] = block.pcm[int3];
				}
			}

			lookMapping0.residue_func[int2].inverse(block, lookMapping0.residue_look[int2], this.pcmbundle, this.zerobundle, int5);
		}

		for (int2 = infoMapping0.coupling_steps - 1; int2 >= 0; --int2) {
			floatArray2 = block.pcm[infoMapping0.coupling_mag[int2]];
			float[] floatArray3 = block.pcm[infoMapping0.coupling_ang[int2]];
			for (int4 = 0; int4 < int1 / 2; ++int4) {
				float float1 = floatArray2[int4];
				float float2 = floatArray3[int4];
				if (float1 > 0.0F) {
					if (float2 > 0.0F) {
						floatArray2[int4] = float1;
						floatArray3[int4] = float1 - float2;
					} else {
						floatArray3[int4] = float1;
						floatArray2[int4] = float1 + float2;
					}
				} else if (float2 > 0.0F) {
					floatArray2[int4] = float1;
					floatArray3[int4] = float1 + float2;
				} else {
					floatArray3[int4] = float1;
					floatArray2[int4] = float1 - float2;
				}
			}
		}

		for (int2 = 0; int2 < info.channels; ++int2) {
			floatArray2 = block.pcm[int2];
			int3 = infoMapping0.chmuxlist[int2];
			lookMapping0.floor_func[int3].inverse2(block, lookMapping0.floor_look[int3], this.floormemo[int2], floatArray2);
		}

		for (int2 = 0; int2 < info.channels; ++int2) {
			floatArray2 = block.pcm[int2];
			((Mdct)dspState.transform[block.W][0]).backward(floatArray2, floatArray2);
		}

		for (int2 = 0; int2 < info.channels; ++int2) {
			floatArray2 = block.pcm[int2];
			if (this.nonzero[int2] != 0) {
				for (int3 = 0; int3 < int1; ++int3) {
					floatArray2[int3] *= floatArray[int3];
				}
			} else {
				for (int3 = 0; int3 < int1; ++int3) {
					floatArray2[int3] = 0.0F;
				}
			}
		}

		return 0;
	}

	Object look(DspState dspState, InfoMode infoMode, Object object) {
		Info info = dspState.vi;
		Mapping0.LookMapping0 lookMapping0 = new Mapping0.LookMapping0();
		Mapping0.InfoMapping0 infoMapping0 = lookMapping0.map = (Mapping0.InfoMapping0)object;
		lookMapping0.mode = infoMode;
		lookMapping0.time_look = new Object[infoMapping0.submaps];
		lookMapping0.floor_look = new Object[infoMapping0.submaps];
		lookMapping0.residue_look = new Object[infoMapping0.submaps];
		lookMapping0.time_func = new FuncTime[infoMapping0.submaps];
		lookMapping0.floor_func = new FuncFloor[infoMapping0.submaps];
		lookMapping0.residue_func = new FuncResidue[infoMapping0.submaps];
		for (int int1 = 0; int1 < infoMapping0.submaps; ++int1) {
			int int2 = infoMapping0.timesubmap[int1];
			int int3 = infoMapping0.floorsubmap[int1];
			int int4 = infoMapping0.residuesubmap[int1];
			lookMapping0.time_func[int1] = FuncTime.time_P[info.time_type[int2]];
			lookMapping0.time_look[int1] = lookMapping0.time_func[int1].look(dspState, infoMode, info.time_param[int2]);
			lookMapping0.floor_func[int1] = FuncFloor.floor_P[info.floor_type[int3]];
			lookMapping0.floor_look[int1] = lookMapping0.floor_func[int1].look(dspState, infoMode, info.floor_param[int3]);
			lookMapping0.residue_func[int1] = FuncResidue.residue_P[info.residue_type[int4]];
			lookMapping0.residue_look[int1] = lookMapping0.residue_func[int1].look(dspState, infoMode, info.residue_param[int4]);
		}

		if (info.psys != 0 && dspState.analysisp != 0) {
		}

		lookMapping0.ch = info.channels;
		return lookMapping0;
	}

	void pack(Info info, Object object, Buffer buffer) {
		Mapping0.InfoMapping0 infoMapping0 = (Mapping0.InfoMapping0)object;
		if (infoMapping0.submaps > 1) {
			buffer.write(1, 1);
			buffer.write(infoMapping0.submaps - 1, 4);
		} else {
			buffer.write(0, 1);
		}

		int int1;
		if (infoMapping0.coupling_steps > 0) {
			buffer.write(1, 1);
			buffer.write(infoMapping0.coupling_steps - 1, 8);
			for (int1 = 0; int1 < infoMapping0.coupling_steps; ++int1) {
				buffer.write(infoMapping0.coupling_mag[int1], Util.ilog2(info.channels));
				buffer.write(infoMapping0.coupling_ang[int1], Util.ilog2(info.channels));
			}
		} else {
			buffer.write(0, 1);
		}

		buffer.write(0, 2);
		if (infoMapping0.submaps > 1) {
			for (int1 = 0; int1 < info.channels; ++int1) {
				buffer.write(infoMapping0.chmuxlist[int1], 4);
			}
		}

		for (int1 = 0; int1 < infoMapping0.submaps; ++int1) {
			buffer.write(infoMapping0.timesubmap[int1], 8);
			buffer.write(infoMapping0.floorsubmap[int1], 8);
			buffer.write(infoMapping0.residuesubmap[int1], 8);
		}
	}

	Object unpack(Info info, Buffer buffer) {
		Mapping0.InfoMapping0 infoMapping0 = new Mapping0.InfoMapping0();
		if (buffer.read(1) != 0) {
			infoMapping0.submaps = buffer.read(4) + 1;
		} else {
			infoMapping0.submaps = 1;
		}

		int int1;
		if (buffer.read(1) != 0) {
			infoMapping0.coupling_steps = buffer.read(8) + 1;
			for (int1 = 0; int1 < infoMapping0.coupling_steps; ++int1) {
				int int2 = infoMapping0.coupling_mag[int1] = buffer.read(Util.ilog2(info.channels));
				int int3 = infoMapping0.coupling_ang[int1] = buffer.read(Util.ilog2(info.channels));
				if (int2 < 0 || int3 < 0 || int2 == int3 || int2 >= info.channels || int3 >= info.channels) {
					infoMapping0.free();
					return null;
				}
			}
		}

		if (buffer.read(2) > 0) {
			infoMapping0.free();
			return null;
		} else {
			if (infoMapping0.submaps > 1) {
				for (int1 = 0; int1 < info.channels; ++int1) {
					infoMapping0.chmuxlist[int1] = buffer.read(4);
					if (infoMapping0.chmuxlist[int1] >= infoMapping0.submaps) {
						infoMapping0.free();
						return null;
					}
				}
			}

			for (int1 = 0; int1 < infoMapping0.submaps; ++int1) {
				infoMapping0.timesubmap[int1] = buffer.read(8);
				if (infoMapping0.timesubmap[int1] >= info.times) {
					infoMapping0.free();
					return null;
				}

				infoMapping0.floorsubmap[int1] = buffer.read(8);
				if (infoMapping0.floorsubmap[int1] >= info.floors) {
					infoMapping0.free();
					return null;
				}

				infoMapping0.residuesubmap[int1] = buffer.read(8);
				if (infoMapping0.residuesubmap[int1] >= info.residues) {
					infoMapping0.free();
					return null;
				}
			}

			return infoMapping0;
		}
	}

	class LookMapping0 {
		int ch;
		float[][] decay;
		FuncFloor[] floor_func;
		Object[] floor_look;
		Object[] floor_state;
		int lastframe;
		Mapping0.InfoMapping0 map;
		InfoMode mode;
		PsyLook[] psy_look;
		FuncResidue[] residue_func;
		Object[] residue_look;
		FuncTime[] time_func;
		Object[] time_look;
	}

	class InfoMapping0 {
		int[] chmuxlist = new int[256];
		int[] coupling_ang = new int[256];
		int[] coupling_mag = new int[256];
		int coupling_steps;
		int[] floorsubmap = new int[16];
		int[] psysubmap = new int[16];
		int[] residuesubmap = new int[16];
		int submaps;
		int[] timesubmap = new int[16];

		void free() {
			this.chmuxlist = null;
			this.timesubmap = null;
			this.floorsubmap = null;
			this.residuesubmap = null;
			this.psysubmap = null;
			this.coupling_mag = null;
			this.coupling_ang = null;
		}
	}
}
