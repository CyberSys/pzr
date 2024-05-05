package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;


public class Color3f extends Tuple3f implements Serializable {
	static final long serialVersionUID = -1861792981817493659L;

	public Color3f(float float1, float float2, float float3) {
		super(float1, float2, float3);
	}

	public Color3f(float[] floatArray) {
		super(floatArray);
	}

	public Color3f(Color3f color3f) {
		super((Tuple3f)color3f);
	}

	public Color3f(Tuple3f tuple3f) {
		super(tuple3f);
	}

	public Color3f(Tuple3d tuple3d) {
		super(tuple3d);
	}

	public Color3f(Color color) {
		super((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F);
	}

	public Color3f() {
	}

	public final void set(Color color) {
		this.x = (float)color.getRed() / 255.0F;
		this.y = (float)color.getGreen() / 255.0F;
		this.z = (float)color.getBlue() / 255.0F;
	}

	public final Color get() {
		int int1 = Math.round(this.x * 255.0F);
		int int2 = Math.round(this.y * 255.0F);
		int int3 = Math.round(this.z * 255.0F);
		return new Color(int1, int2, int3);
	}
}
