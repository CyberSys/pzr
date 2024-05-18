package zombie.core;

import java.io.Serializable;


public class Color implements Serializable {
	private static final long serialVersionUID = 1393939L;
	public static final Color transparent = new Color(0.0F, 0.0F, 0.0F, 0.0F);
	public static final Color white = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	public static final Color yellow = new Color(1.0F, 1.0F, 0.0F, 1.0F);
	public static final Color red = new Color(1.0F, 0.0F, 0.0F, 1.0F);
	public static final Color purple = new Color(196.0F, 0.0F, 171.0F);
	public static final Color blue = new Color(0.0F, 0.0F, 1.0F, 1.0F);
	public static final Color green = new Color(0.0F, 1.0F, 0.0F, 1.0F);
	public static final Color black = new Color(0.0F, 0.0F, 0.0F, 1.0F);
	public static final Color gray = new Color(0.5F, 0.5F, 0.5F, 1.0F);
	public static final Color cyan = new Color(0.0F, 1.0F, 1.0F, 1.0F);
	public static final Color darkGray = new Color(0.3F, 0.3F, 0.3F, 1.0F);
	public static final Color lightGray = new Color(0.7F, 0.7F, 0.7F, 1.0F);
	public static final Color pink = new Color(255, 175, 175, 255);
	public static final Color orange = new Color(255, 200, 0, 255);
	public static final Color magenta = new Color(255, 0, 255, 255);
	public static final Color darkGreen = new Color(22, 113, 20, 255);
	public static final Color lightGreen = new Color(55, 148, 53, 255);
	public float a = 1.0F;
	public float b;
	public float g;
	public float r;

	public Color(Color color) {
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

	public Color(float float1, float float2, float float3) {
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.a = 1.0F;
	}

	public Color(float float1, float float2, float float3, float float4) {
		this.r = Math.min(float1, 1.0F);
		this.g = Math.min(float2, 1.0F);
		this.b = Math.min(float3, 1.0F);
		this.a = Math.min(float4, 1.0F);
	}

	public Color(Color color, Color color2, float float1) {
		float float2 = (color2.r - color.r) * float1;
		float float3 = (color2.g - color.g) * float1;
		float float4 = (color2.b - color.b) * float1;
		float float5 = (color2.a - color.a) * float1;
		this.r = color.r + float2;
		this.g = color.g + float3;
		this.b = color.b + float4;
		this.a = color.a + float5;
	}

	public void setColor(Color color, Color color2, float float1) {
		float float2 = (color2.r - color.r) * float1;
		float float3 = (color2.g - color.g) * float1;
		float float4 = (color2.b - color.b) * float1;
		float float5 = (color2.a - color.a) * float1;
		this.r = color.r + float2;
		this.g = color.g + float3;
		this.b = color.b + float4;
		this.a = color.a + float5;
	}

	public Color(int int1, int int2, int int3) {
		this.r = (float)int1 / 255.0F;
		this.g = (float)int2 / 255.0F;
		this.b = (float)int3 / 255.0F;
		this.a = 1.0F;
	}

	public Color(int int1, int int2, int int3, int int4) {
		this.r = (float)int1 / 255.0F;
		this.g = (float)int2 / 255.0F;
		this.b = (float)int3 / 255.0F;
		this.a = (float)int4 / 255.0F;
	}

	public Color(int int1) {
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

	public void fromColor(int int1) {
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

	public static Color decode(String string) {
		return new Color(Integer.decode(string));
	}

	public void add(Color color) {
		this.r += color.r;
		this.g += color.g;
		this.b += color.b;
		this.a += color.a;
	}

	public Color addToCopy(Color color) {
		Color color2 = new Color(this.r, this.g, this.b, this.a);
		color2.r += color.r;
		color2.g += color.g;
		color2.b += color.b;
		color2.a += color.a;
		return color2;
	}

	public Color brighter() {
		return this.brighter(0.2F);
	}

	public Color brighter(float float1) {
		this.r = this.r += float1;
		this.g = this.g += float1;
		this.b = this.b += float1;
		return this;
	}

	public Color darker() {
		return this.darker(0.5F);
	}

	public Color darker(float float1) {
		this.r = this.r -= float1;
		this.g = this.g -= float1;
		this.b = this.b -= float1;
		return this;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Color)) {
			return false;
		} else {
			Color color = (Color)object;
			return color.r == this.r && color.g == this.g && color.b == this.b && color.a == this.a;
		}
	}

	public void set(Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}

	public int getAlpha() {
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

	public int getAlphaByte() {
		return (int)(this.a * 255.0F);
	}

	public int getBlue() {
		return (int)(this.b * 255.0F);
	}

	public int getBlueByte() {
		return (int)(this.b * 255.0F);
	}

	public int getGreen() {
		return (int)(this.g * 255.0F);
	}

	public int getGreenByte() {
		return (int)(this.g * 255.0F);
	}

	public int getRed() {
		return (int)(this.r * 255.0F);
	}

	public int getRedByte() {
		return (int)(this.r * 255.0F);
	}

	public int hashCode() {
		return (int)(this.r + this.g + this.b + this.a) * 255;
	}

	public Color multiply(Color color) {
		return new Color(this.r * color.r, this.g * color.g, this.b * color.b, this.a * color.a);
	}

	public void scale(float float1) {
		this.r *= float1;
		this.g *= float1;
		this.b *= float1;
		this.a *= float1;
	}

	public Color scaleCopy(float float1) {
		Color color = new Color(this.r, this.g, this.b, this.a);
		color.r *= float1;
		color.g *= float1;
		color.b *= float1;
		color.a *= float1;
		return color;
	}

	public String toString() {
		return "Color (" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
	}

	public void interp(Color color, float float1, Color color2) {
		float float2 = color.r - this.r;
		float float3 = color.g - this.g;
		float float4 = color.b - this.b;
		float float5 = color.a - this.a;
		float2 *= float1;
		float3 *= float1;
		float4 *= float1;
		float5 *= float1;
		color2.r = this.r + float2;
		color2.g = this.g + float3;
		color2.b = this.b + float4;
		color2.a = this.a + float5;
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
