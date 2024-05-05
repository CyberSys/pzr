package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


abstract class FuncTime {
	public static FuncTime[] time_P = new FuncTime[]{new Time0()};

	abstract void free_info(Object object);

	abstract void free_look(Object object);

	abstract int inverse(Block block, Object object, float[] floatArray, float[] floatArray2);

	abstract Object look(DspState dspState, InfoMode infoMode, Object object);

	abstract void pack(Object object, Buffer buffer);

	abstract Object unpack(Info info, Buffer buffer);
}
