package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;


public class Color4b extends Tuple4b implements Serializable {
	static final long serialVersionUID = -105080578052502155L;

	public Color4b(byte byte1, byte byte2, byte byte3, byte byte4) {
		super(byte1, byte2, byte3, byte4);
	}

	public Color4b(byte[] byteArray) {
		super(byteArray);
	}

	public Color4b(Color4b color4b) {
		super((Tuple4b)color4b);
	}

	public Color4b(Tuple4b tuple4b) {
		super(tuple4b);
	}

	public Color4b(Color color) {
		super((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)color.getAlpha());
	}

	public Color4b() {
	}

	public final void set(Color color) {
		this.x = (byte)color.getRed();
		this.y = (byte)color.getGreen();
		this.z = (byte)color.getBlue();
		this.w = (byte)color.getAlpha();
	}

	public final Color get() {
		int int1 = this.x & 255;
		int int2 = this.y & 255;
		int int3 = this.z & 255;
		int int4 = this.w & 255;
		return new Color(int1, int2, int3, int4);
	}
}
