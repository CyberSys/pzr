package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


abstract class FuncResidue {
	public static FuncResidue[] residue_P = new FuncResidue[]{new Residue0(), new Residue1(), new Residue2()};

	abstract void free_info(Object object);

	abstract void free_look(Object object);

	abstract int inverse(Block block, Object object, float[][] floatArrayArray, int[] intArray, int int1);

	abstract Object look(DspState dspState, InfoMode infoMode, Object object);

	abstract void pack(Object object, Buffer buffer);

	abstract Object unpack(Info info, Buffer buffer);
}
