package zombie.core;

import zombie.core.math.PZMath;


public final class ImmutableColor {
	public static final ImmutableColor transparent = new ImmutableColor(0.0F, 0.0F, 0.0F, 0.0F);
	public static final ImmutableColor white = new ImmutableColor(1.0F, 1.0F, 1.0F, 1.0F);
	public static final ImmutableColor yellow = new ImmutableColor(1.0F, 1.0F, 0.0F, 1.0F);
	public static final ImmutableColor red = new ImmutableColor(1.0F, 0.0F, 0.0F, 1.0F);
	public static final ImmutableColor purple = new ImmutableColor(196.0F, 0.0F, 171.0F);
	public static final ImmutableColor blue = new ImmutableColor(0.0F, 0.0F, 1.0F, 1.0F);
	public static final ImmutableColor green = new ImmutableColor(0.0F, 1.0F, 0.0F, 1.0F);
	public static final ImmutableColor black = new ImmutableColor(0.0F, 0.0F, 0.0F, 1.0F);
	public static final ImmutableColor gray = new ImmutableColor(0.5F, 0.5F, 0.5F, 1.0F);
	public static final ImmutableColor cyan = new ImmutableColor(0.0F, 1.0F, 1.0F, 1.0F);
	public static final ImmutableColor darkGray = new ImmutableColor(0.3F, 0.3F, 0.3F, 1.0F);
	public static final ImmutableColor lightGray = new ImmutableColor(0.7F, 0.7F, 0.7F, 1.0F);
	public static final ImmutableColor pink = new ImmutableColor(255, 175, 175, 255);
	public static final ImmutableColor orange = new ImmutableColor(255, 200, 0, 255);
	public static final ImmutableColor magenta = new ImmutableColor(255, 0, 255, 255);
	public static final ImmutableColor darkGreen = new ImmutableColor(22, 113, 20, 255);
	public static final ImmutableColor lightGreen = new ImmutableColor(55, 148, 53, 255);
	public final float a;
	public final float b;
	public final float g;
	public final float r;

	public ImmutableColor(ImmutableColor immutableColor) {
		if (immutableColor == null) {
			this.r = 0.0F;
			this.g = 0.0F;
			this.b = 0.0F;
			this.a = 1.0F;
		} else {
			this.r = immutableColor.r;
			this.g = immutableColor.g;
			this.b = immutableColor.b;
			this.a = immutableColor.a;
		}
	}

	public ImmutableColor(Color color) {
		if (color == null) {
			this.r = 0.0F;
			this.g = 0.0F;
			this.b = 0.0F;
			this.a = 1.0F;
		} else {
			this.r = color.r;
			this.g = color.g;
			this.b = color.b;
			this.a = color.a;
		}
	}

	public Color toMutableColor() {
		return new Color(this.r, this.g, this.b, this.a);
	}

	public ImmutableColor(float float1, float float2, float float3) {
		this.r = PZMath.clamp(float1, 0.0F, 1.0F);
		this.g = PZMath.clamp(float2, 0.0F, 1.0F);
		this.b = PZMath.clamp(float3, 0.0F, 1.0F);
		this.a = 1.0F;
	}

	public ImmutableColor(float float1, float float2, float float3, float float4) {
		this.r = Math.min(float1, 1.0F);
		this.g = Math.min(float2, 1.0F);
		this.b = Math.min(float3, 1.0F);
		this.a = Math.min(float4, 1.0F);
	}

	public ImmutableColor(Color color, Color color2, float float1) {
		float float2 = (color2.r - color.r) * float1;
		float float3 = (color2.g - color.g) * float1;
		float float4 = (color2.b - color.b) * float1;
		float float5 = (color2.a - color.a) * float1;
		this.r = color.r + float2;
		this.g = color.g + float3;
		this.b = color.b + float4;
		this.a = color.a + float5;
	}

	public ImmutableColor(int int1, int int2, int int3) {
		this.r = (float)int1 / 255.0F;
		this.g = (float)int2 / 255.0F;
		this.b = (float)int3 / 255.0F;
		this.a = 1.0F;
	}

	public ImmutableColor(int int1, int int2, int int3, int int4) {
		this.r = (float)int1 / 255.0F;
		this.g = (float)int2 / 255.0F;
		this.b = (float)int3 / 255.0F;
		this.a = (float)int4 / 255.0F;
	}

	public ImmutableColor(int int1) {
		int int2 = (int1 & 16711680) >> 16;
		int int3 = (int1 & '＀') >> 8;
		int int4 = int1 & 255;
		int int5 = (int1 & -16777216) >> 24;
		if (int5 < 0) {
			int5 += 256;
		}

		if (int5 == 0) {
			int5 = 255;
		}

		this.r = (float)int4 / 255.0F;
		this.g = (float)int3 / 255.0F;
		this.b = (float)int2 / 255.0F;
		this.a = (float)int5 / 255.0F;
	}

	public static ImmutableColor random() {
		float float1 = Rand.Next(0.0F, 1.0F);
		float float2 = Rand.Next(0.0F, 0.6F);
		float float3 = Rand.Next(0.0F, 0.9F);
		Color color = Color.HSBtoRGB(float1, float2, float3);
		return new ImmutableColor(color);
	}

	public static ImmutableColor decode(String string) {
		return new ImmutableColor(Integer.decode(string));
	}

	public ImmutableColor add(ImmutableColor immutableColor) {
		return new ImmutableColor(this.r + immutableColor.r, this.g + immutableColor.g, this.b + immutableColor.b, this.a + immutableColor.a);
	}

	public ImmutableColor brighter() {
		return this.brighter(0.2F);
	}

	public ImmutableColor brighter(float float1) {
		return new ImmutableColor(this.r + float1, this.g + float1, this.b + float1);
	}

	public ImmutableColor darker() {
		return this.darker(0.5F);
	}

	public ImmutableColor darker(float float1) {
		return new ImmutableColor(this.r - float1, this.g - float1, this.b - float1);
	}

	public boolean equals(Object object) {
		if (!(object instanceof ImmutableColor)) {
			return false;
		} else {
			ImmutableColor immutableColor = (ImmutableColor)object;
			return immutableColor.r == this.r && immutableColor.g == this.g && immutableColor.b == this.b && immutableColor.a == this.a;
		}
	}

	public int getAlphaInt() {
		return (int)(this.a * 255.0F);
	}

	public float getAlphaFloat() {
		return this.a;
	}

	public float getRedFloat() {
		return this.r;
	}

	public float getGreenFloat() {
		return this.g;
	}

	public float getBlueFloat() {
		return this.b;
	}

	public byte getAlphaByte() {
		return (byte)((int)(this.a * 255.0F) & 255);
	}

	public int getBlueInt() {
		return (int)(this.b * 255.0F);
	}

	public byte getBlueByte() {
		return (byte)((int)(this.b * 255.0F) & 255);
	}

	public int getGreenInt() {
		return (int)(this.g * 255.0F);
	}

	public byte getGreenByte() {
		return (byte)((int)(this.g * 255.0F) & 255);
	}

	public int getRedInt() {
		return (int)(this.r * 255.0F);
	}

	public byte getRedByte() {
		return (byte)((int)(this.r * 255.0F) & 255);
	}

	public int hashCode() {
		return (int)(this.r + this.g + this.b + this.a) * 255;
	}

	public ImmutableColor multiply(Color color) {
		return new ImmutableColor(this.r * color.r, this.g * color.g, this.b * color.b, this.a * color.a);
	}

	public ImmutableColor scale(float float1) {
		return new ImmutableColor(this.r * float1, this.g * float1, this.b * float1, this.a * float1);
	}

	public String toString() {
		return "ImmutableColor (" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
	}

	public ImmutableColor interp(ImmutableColor immutableColor, float float1) {
		float float2 = immutableColor.r - this.r;
		float float3 = immutableColor.g - this.g;
		float float4 = immutableColor.b - this.b;
		float float5 = immutableColor.a - this.a;
		float2 *= float1;
		float3 *= float1;
		float4 *= float1;
		float5 *= float1;
		return new ImmutableColor(this.r + float2, this.g + float3, this.b + float4, this.a + float5);
	}

	public static Integer[] HSBtoRGB(float float1, float float2, float float3) {
		int int1 = 0;
		int int2 = 0;
		int int3 = 0;
		if (float2 == 0.0F) {
			int1 = int2 = int3 = (int)(float3 * 255.0F + 0.5F);
		} else {
			float float4 = (float1 - (float)Math.floor((double)float1)) * 6.0F;
			float float5 = float4 - (float)Math.floor((double)float4);
			float float6 = float3 * (1.0F - float2);
			float float7 = float3 * (1.0F - float2 * float5);
			float float8 = float3 * (1.0F - float2 * (1.0F - float5));
			switch ((int)float4) {
			case 0: 
				int1 = (int)(float3 * 255.0F + 0.5F);
				int2 = (int)(float8 * 255.0F + 0.5F);
				int3 = (int)(float6 * 255.0F + 0.5F);
				break;
			
			case 1: 
				int1 = (int)(float7 * 255.0F + 0.5F);
				int2 = (int)(float3 * 255.0F + 0.5F);
				int3 = (int)(float6 * 255.0F + 0.5F);
				break;
			
			case 2: 
				int1 = (int)(float6 * 255.0F + 0.5F);
				int2 = (int)(float3 * 255.0F + 0.5F);
				int3 = (int)(float8 * 255.0F + 0.5F);
				break;
			
			case 3: 
				int1 = (int)(float6 * 255.0F + 0.5F);
				int2 = (int)(float7 * 255.0F + 0.5F);
				int3 = (int)(float3 * 255.0F + 0.5F);
				break;
			
			case 4: 
				int1 = (int)(float8 * 255.0F + 0.5F);
				int2 = (int)(float6 * 255.0F + 0.5F);
				int3 = (int)(float3 * 255.0F + 0.5F);
				break;
			
			case 5: 
				int1 = (int)(float3 * 255.0F + 0.5F);
				int2 = (int)(float6 * 255.0F + 0.5F);
				int3 = (int)(float7 * 255.0F + 0.5F);
			
			}
		}

		Integer[] integerArray = new Integer[]{int1, int2, int3};
		return integerArray;
	}
}
