package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;


public class Quadric {
	protected int drawStyle = 100012;
	protected int orientation = 100020;
	protected boolean textureFlag = false;
	protected int normals = 100000;

	protected void normal3f(float float1, float float2, float float3) {
		float float4 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2 + float3 * float3));
		if (float4 > 1.0E-5F) {
			float1 /= float4;
			float2 /= float4;
			float3 /= float4;
		}

		GL11.glNormal3f(float1, float2, float3);
	}

	public void setDrawStyle(int int1) {
		this.drawStyle = int1;
	}

	public void setNormals(int int1) {
		this.normals = int1;
	}

	public void setOrientation(int int1) {
		this.orientation = int1;
	}

	public void setTextureFlag(boolean boolean1) {
		this.textureFlag = boolean1;
	}

	public int getDrawStyle() {
		return this.drawStyle;
	}

	public int getNormals() {
		return this.normals;
	}

	public int getOrientation() {
		return this.orientation;
	}

	public boolean getTextureFlag() {
		return this.textureFlag;
	}

	protected void TXTR_COORD(float float1, float float2) {
		if (this.textureFlag) {
			GL11.glTexCoord2f(float1, float2);
		}
	}

	protected float sin(float float1) {
		return (float)Math.sin((double)float1);
	}

	protected float cos(float float1) {
		return (float)Math.cos((double)float1);
	}
}
