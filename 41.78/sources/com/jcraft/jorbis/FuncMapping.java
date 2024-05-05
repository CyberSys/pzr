package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


abstract class FuncMapping {
	public static FuncMapping[] mapping_P = new FuncMapping[]{new Mapping0()};

	abstract void free_info(Object object);

	abstract void free_look(Object object);

	abstract int inverse(Block block, Object object);

	abstract Object look(DspState dspState, InfoMode infoMode, Object object);

	abstract void pack(Info info, Object object, Buffer buffer);

	abstract Object unpack(Info info, Buffer buffer);
}
