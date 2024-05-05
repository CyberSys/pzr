package zombie.core;

import java.io.Serializable;
import zombie.core.math.PZMath;


public final class Color implements Serializable {
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

	public Color() {
	}

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
		this.r = PZMath.clamp(float1, 0.0F, 1.0F);
		this.g = PZMath.clamp(float2, 0.0F, 1.0F);
		this.b = PZMath.clamp(float3, 0.0F, 1.0F);
		this.a = PZMath.clamp(float4, 0.0F, 1.0F);
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

	@Deprecated
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

	public void setABGR(int int1) {
		abgrToColor(int1, this);
	}

	public static Color abgrToColor(int int1, Color color) {
		int int2 = int1 >> 24 & 255;
		int int3 = int1 >> 16 & 255;
		int int4 = int1 >> 8 & 255;
		int int5 = int1 & 255;
		float float1 = 0.003921569F * (float)int5;
		float float2 = 0.003921569F * (float)int4;
		float float3 = 0.003921569F * (float)int3;
		float float4 = 0.003921569F * (float)int2;
		color.r = float1;
		color.g = float2;
		color.b = float3;
		color.a = float4;
		return color;
	}

	public static int colorToABGR(Color color) {
		return colorToABGR(color.r, color.g, color.b, color.a);
	}

	public static int colorToABGR(float float1, float float2, float float3, float float4) {
		float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		float2 = PZMath.clamp(float2, 0.0F, 1.0F);
		float3 = PZMath.clamp(float3, 0.0F, 1.0F);
		float4 = PZMath.clamp(float4, 0.0F, 1.0F);
		int int1 = (int)(float1 * 255.0F);
		int int2 = (int)(float2 * 255.0F);
		int int3 = (int)(float3 * 255.0F);
		int int4 = (int)(float4 * 255.0F);
		int int5 = (int4 & 255) << 24 | (int3 & 255) << 16 | (int2 & 255) << 8 | int1 & 255;
		return int5;
	}

	public static int multiplyABGR(int int1, int int2) {
		float float1 = getRedChannelFromABGR(int1);
		float float2 = getGreenChannelFromABGR(int1);
		float float3 = getBlueChannelFromABGR(int1);
		float float4 = getAlphaChannelFromABGR(int1);
		float float5 = getRedChannelFromABGR(int2);
		float float6 = getGreenChannelFromABGR(int2);
		float float7 = getBlueChannelFromABGR(int2);
		float float8 = getAlphaChannelFromABGR(int2);
		return colorToABGR(float1 * float5, float2 * float6, float3 * float7, float4 * float8);
	}

	public static int multiplyBGR(int int1, int int2) {
		float float1 = getRedChannelFromABGR(int1);
		float float2 = getGreenChannelFromABGR(int1);
		float float3 = getBlueChannelFromABGR(int1);
		float float4 = getAlphaChannelFromABGR(int1);
		float float5 = getRedChannelFromABGR(int2);
		float float6 = getGreenChannelFromABGR(int2);
		float float7 = getBlueChannelFromABGR(int2);
		return colorToABGR(float1 * float5, float2 * float6, float3 * float7, float4);
	}

	public static int blendBGR(int int1, int int2) {
		float float1 = getRedChannelFromABGR(int1);
		float float2 = getGreenChannelFromABGR(int1);
		float float3 = getBlueChannelFromABGR(int1);
		float float4 = getAlphaChannelFromABGR(int1);
		float float5 = getRedChannelFromABGR(int2);
		float float6 = getGreenChannelFromABGR(int2);
		float float7 = getBlueChannelFromABGR(int2);
		float float8 = getAlphaChannelFromABGR(int2);
		return colorToABGR(float1 * (1.0F - float8) + float5 * float8, float2 * (1.0F - float8) + float6 * float8, float3 * (1.0F - float8) + float7 * float8, float4);
	}

	public static int blendABGR(int int1, int int2) {
		float float1 = getRedChannelFromABGR(int1);
		float float2 = getGreenChannelFromABGR(int1);
		float float3 = getBlueChannelFromABGR(int1);
		float float4 = getAlphaChannelFromABGR(int1);
		float float5 = getRedChannelFromABGR(int2);
		float float6 = getGreenChannelFromABGR(int2);
		float float7 = getBlueChannelFromABGR(int2);
		float float8 = getAlphaChannelFromABGR(int2);
		return colorToABGR(float1 * (1.0F - float8) + float5 * float8, float2 * (1.0F - float8) + float6 * float8, float3 * (1.0F - float8) + float7 * float8, float4 * (1.0F - float8) + float8 * float8);
	}

	public static int tintABGR(int int1, int int2) {
		float float1 = getRedChannelFromABGR(int2);
		float float2 = getGreenChannelFromABGR(int2);
		float float3 = getBlueChannelFromABGR(int2);
		float float4 = getAlphaChannelFromABGR(int2);
		float float5 = getRedChannelFromABGR(int1);
		float float6 = getGreenChannelFromABGR(int1);
		float float7 = getBlueChannelFromABGR(int1);
		float float8 = getAlphaChannelFromABGR(int1);
		return colorToABGR(float1 * float4 + float5 * (1.0F - float4), float2 * float4 + float6 * (1.0F - float4), float3 * float4 + float7 * (1.0F - float4), float8);
	}

	public static int lerpABGR(int int1, int int2, float float1) {
		float float2 = getRedChannelFromABGR(int1);
		float float3 = getGreenChannelFromABGR(int1);
		float float4 = getBlueChannelFromABGR(int1);
		float float5 = getAlphaChannelFromABGR(int1);
		float float6 = getRedChannelFromABGR(int2);
		float float7 = getGreenChannelFromABGR(int2);
		float float8 = getBlueChannelFromABGR(int2);
		float float9 = getAlphaChannelFromABGR(int2);
		return colorToABGR(float2 * (1.0F - float1) + float6 * float1, float3 * (1.0F - float1) + float7 * float1, float4 * (1.0F - float1) + float8 * float1, float5 * (1.0F - float1) + float9 * float1);
	}

	public static float getAlphaChannelFromABGR(int int1) {
		int int2 = int1 >> 24 & 255;
		float float1 = 0.003921569F * (float)int2;
		return float1;
	}

	public static float getBlueChannelFromABGR(int int1) {
		int int2 = int1 >> 16 & 255;
		float float1 = 0.003921569F * (float)int2;
		return float1;
	}

	public static float getGreenChannelFromABGR(int int1) {
		int int2 = int1 >> 8 & 255;
		float float1 = 0.003921569F * (float)int2;
		return float1;
	}

	public static float getRedChannelFromABGR(int int1) {
		int int2 = int1 & 255;
		float float1 = 0.003921569F * (float)int2;
		return float1;
	}

	public static int setAlphaChannelToABGR(int int1, float float1) {
		float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		int int2 = (int)(float1 * 255.0F);
		int int3 = (int2 & 255) << 24 | int1 & 16777215;
		return int3;
	}

	public static int setBlueChannelToABGR(int int1, float float1) {
		float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		int int2 = (int)(float1 * 255.0F);
		int int3 = (int2 & 255) << 16 | int1 & -16711681;
		return int3;
	}

	public static int setGreenChannelToABGR(int int1, float float1) {
		float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		int int2 = (int)(float1 * 255.0F);
		int int3 = (int2 & 255) << 8 | int1 & -65281;
		return int3;
	}

	public static int setRedChannelToABGR(int int1, float float1) {
		float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		int int2 = (int)(float1 * 255.0F);
		int int3 = int2 & 255 | int1 & -256;
		return int3;
	}

	public static Color random() {
		return Colors.GetRandomColor();
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

	public Color set(Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
		return this;
	}

	public Color set(float float1, float float2, float float3) {
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.a = 1.0F;
		return this;
	}

	public Color set(float float1, float float2, float float3, float float4) {
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.a = float4;
		return this;
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

	public Color scale(float float1) {
		this.r *= float1;
		this.g *= float1;
		this.b *= float1;
		this.a *= float1;
		return this;
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

	public void changeHSBValue(float float1, float float2, float float3) {
		float[] floatArray = java.awt.Color.RGBtoHSB(this.getRedByte(), this.getGreenByte(), this.getBlueByte(), (float[])null);
		int int1 = java.awt.Color.HSBtoRGB(floatArray[0] * float1, floatArray[1] * float2, floatArray[2] * float3);
		this.r = (float)(int1 >> 16 & 255) / 255.0F;
		this.g = (float)(int1 >> 8 & 255) / 255.0F;
		this.b = (float)(int1 & 255) / 255.0F;
	}

	public static Color HSBtoRGB(float float1, float float2, float float3, Color color) {
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

		return color.set((float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F);
	}

	public static Color HSBtoRGB(float float1, float float2, float float3) {
		return HSBtoRGB(float1, float2, float3, new Color());
	}
}
