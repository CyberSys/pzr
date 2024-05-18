package zombie.core.textures;

import zombie.core.Color;


public class ColorInfo {
	public float a = 1.0F;
	public float b = 1.0F;
	public float g = 1.0F;
	public float r = 1.0F;

	public ColorInfo() {
		this.r = 1.0F;
		this.g = 1.0F;
		this.b = 1.0F;
		this.a = 1.0F;
	}

	public ColorInfo(float float1, float float2, float float3, float float4) {
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.a = float4;
	}

	public ColorInfo set(float float1, float float2, float float3, float float4) {
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.a = float4;
		return this;
	}

	public float getR() {
		return this.r;
	}

	public float getG() {
		return this.g;
	}

	public float getB() {
		return this.b;
	}

	public Color toColor() {
		return new Color(this.r, this.g, this.b, this.a);
	}

	public float getA() {
		return this.a;
	}

	public void desaturate(float float1) {
		float float2 = this.r * 0.3086F + this.g * 0.6094F + this.b * 0.082F;
		this.r = float2 * float1 + this.r * (1.0F - float1);
		this.g = float2 * float1 + this.g * (1.0F - float1);
		this.b = float2 * float1 + this.b * (1.0F - float1);
	}

	public void interp(ColorInfo colorInfo, float float1, ColorInfo colorInfo2) {
		float float2 = colorInfo.r - this.r;
		float float3 = colorInfo.g - this.g;
		float float4 = colorInfo.b - this.b;
		float float5 = colorInfo.a - this.a;
		float2 *= float1;
		float3 *= float1;
		float4 *= float1;
		float5 *= float1;
		colorInfo2.r = this.r + float2;
		colorInfo2.g = this.g + float3;
		colorInfo2.b = this.b + float4;
		colorInfo2.a = this.a + float5;
	}
}
