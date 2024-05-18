package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;


public class Color3b extends Tuple3b implements Serializable {
	static final long serialVersionUID = 6632576088353444794L;

	public Color3b(byte byte1, byte byte2, byte byte3) {
		super(byte1, byte2, byte3);
	}

	public Color3b(byte[] byteArray) {
		super(byteArray);
	}

	public Color3b(Color3b color3b) {
		super((Tuple3b)color3b);
	}

	public Color3b(Tuple3b tuple3b) {
		super(tuple3b);
	}

	public Color3b(Color color) {
		super((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
	}

	public Color3b() {
	}

	public final void set(Color color) {
		this.x = (byte)color.getRed();
		this.y = (byte)color.getGreen();
		this.z = (byte)color.getBlue();
	}

	public final Color get() {
		int int1 = this.x & 255;
		int int2 = this.y & 255;
		int int3 = this.z & 255;
		return new Color(int1, int2, int3);
	}
}
