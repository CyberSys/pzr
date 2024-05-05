package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


abstract class FuncFloor {
	public static FuncFloor[] floor_P = new FuncFloor[]{new Floor0(), new Floor1()};

	abstract int forward(Block block, Object object, float[] floatArray, float[] floatArray2, Object object2);

	abstract void free_info(Object object);

	abstract void free_look(Object object);

	abstract void free_state(Object object);

	abstract Object inverse1(Block block, Object object, Object object2);

	abstract int inverse2(Block block, Object object, Object object2, float[] floatArray);

	abstract Object look(DspState dspState, InfoMode infoMode, Object object);

	abstract void pack(Object object, Buffer buffer);

	abstract Object unpack(Info info, Buffer buffer);
}
