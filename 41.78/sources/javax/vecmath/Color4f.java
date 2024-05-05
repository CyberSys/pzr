package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;


public class Color4f extends Tuple4f implements Serializable {
	static final long serialVersionUID = 8577680141580006740L;

	public Color4f(float float1, float float2, float float3, float float4) {
		super(float1, float2, float3, float4);
	}

	public Color4f(float[] floatArray) {
		super(floatArray);
	}

	public Color4f(Color4f color4f) {
		super((Tuple4f)color4f);
	}

	public Color4f(Tuple4f tuple4f) {
		super(tuple4f);
	}

	public Color4f(Tuple4d tuple4d) {
		super(tuple4d);
	}

	public Color4f(Color color) {
		super((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
	}

	public Color4f() {
	}

	public final void set(Color color) {
		this.x = (float)color.getRed() / 255.0F;
		this.y = (float)color.getGreen() / 255.0F;
		this.z = (float)color.getBlue() / 255.0F;
		this.w = (float)color.getAlpha() / 255.0F;
	}

	public final Color get() {
		int int1 = Math.round(this.x * 255.0F);
		int int2 = Math.round(this.y * 255.0F);
		int int3 = Math.round(this.z * 255.0F);
		int int4 = Math.round(this.w * 255.0F);
		return new Color(int1, int2, int3, int4);
	}
}
