package zombie.core.textures;

import java.io.Serializable;


class AlphaColorIndex implements Serializable {
	byte alpha;
	byte blue;
	byte green;
	byte red;

	AlphaColorIndex(int int1, int int2, int int3, int int4) {
		this.red = (byte)int1;
		this.green = (byte)int2;
		this.blue = (byte)int3;
		this.alpha = (byte)int4;
	}
}
