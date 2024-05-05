package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;


public class Cylinder extends Quadric {

	public void draw(float float1, float float2, float float3, int int1, int int2) {
		float float4;
		if (super.orientation == 100021) {
			float4 = -1.0F;
		} else {
			float4 = 1.0F;
		}

		float float5 = 6.2831855F / (float)int1;
		float float6 = (float2 - float1) / (float)int2;
		float float7 = float3 / (float)int2;
		float float8 = (float1 - float2) / float3;
		float float9;
		float float10;
		float float11;
		float float12;
		int int3;
		int int4;
		if (super.drawStyle == 100010) {
			GL11.glBegin(0);
			for (int3 = 0; int3 < int1; ++int3) {
				float10 = this.cos((float)int3 * float5);
				float11 = this.sin((float)int3 * float5);
				this.normal3f(float10 * float4, float11 * float4, float8 * float4);
				float12 = 0.0F;
				float9 = float1;
				for (int4 = 0; int4 <= int2; ++int4) {
					GL11.glVertex3f(float10 * float9, float11 * float9, float12);
					float12 += float7;
					float9 += float6;
				}
			}

			GL11.glEnd();
		} else if (super.drawStyle != 100011 && super.drawStyle != 100013) {
			if (super.drawStyle == 100012) {
				float float13 = 1.0F / (float)int1;
				float float14 = 1.0F / (float)int2;
				float float15 = 0.0F;
				float12 = 0.0F;
				float9 = float1;
				for (int4 = 0; int4 < int2; ++int4) {
					float float16 = 0.0F;
					GL11.glBegin(8);
					for (int3 = 0; int3 <= int1; ++int3) {
						if (int3 == int1) {
							float10 = this.sin(0.0F);
							float11 = this.cos(0.0F);
						} else {
							float10 = this.sin((float)int3 * float5);
							float11 = this.cos((float)int3 * float5);
						}

						if (float4 == 1.0F) {
							this.normal3f(float10 * float4, float11 * float4, float8 * float4);
							this.TXTR_COORD(float16, float15);
							GL11.glVertex3f(float10 * float9, float11 * float9, float12);
							this.normal3f(float10 * float4, float11 * float4, float8 * float4);
							this.TXTR_COORD(float16, float15 + float14);
							GL11.glVertex3f(float10 * (float9 + float6), float11 * (float9 + float6), float12 + float7);
						} else {
							this.normal3f(float10 * float4, float11 * float4, float8 * float4);
							this.TXTR_COORD(float16, float15);
							GL11.glVertex3f(float10 * float9, float11 * float9, float12);
							this.normal3f(float10 * float4, float11 * float4, float8 * float4);
							this.TXTR_COORD(float16, float15 + float14);
							GL11.glVertex3f(float10 * (float9 + float6), float11 * (float9 + float6), float12 + float7);
						}

						float16 += float13;
					}

					GL11.glEnd();
					float9 += float6;
					float15 += float14;
					float12 += float7;
				}
			}
		} else {
			if (super.drawStyle == 100011) {
				float12 = 0.0F;
				float9 = float1;
				for (int4 = 0; int4 <= int2; ++int4) {
					GL11.glBegin(2);
					for (int3 = 0; int3 < int1; ++int3) {
						float10 = this.cos((float)int3 * float5);
						float11 = this.sin((float)int3 * float5);
						this.normal3f(float10 * float4, float11 * float4, float8 * float4);
						GL11.glVertex3f(float10 * float9, float11 * float9, float12);
					}

					GL11.glEnd();
					float12 += float7;
					float9 += float6;
				}
			} else if ((double)float1 != 0.0) {
				GL11.glBegin(2);
				for (int3 = 0; int3 < int1; ++int3) {
					float10 = this.cos((float)int3 * float5);
					float11 = this.sin((float)int3 * float5);
					this.normal3f(float10 * float4, float11 * float4, float8 * float4);
					GL11.glVertex3f(float10 * float1, float11 * float1, 0.0F);
				}

				GL11.glEnd();
				GL11.glBegin(2);
				for (int3 = 0; int3 < int1; ++int3) {
					float10 = this.cos((float)int3 * float5);
					float11 = this.sin((float)int3 * float5);
					this.normal3f(float10 * float4, float11 * float4, float8 * float4);
					GL11.glVertex3f(float10 * float2, float11 * float2, float3);
				}

				GL11.glEnd();
			}

			GL11.glBegin(1);
			for (int3 = 0; int3 < int1; ++int3) {
				float10 = this.cos((float)int3 * float5);
				float11 = this.sin((float)int3 * float5);
				this.normal3f(float10 * float4, float11 * float4, float8 * float4);
				GL11.glVertex3f(float10 * float1, float11 * float1, 0.0F);
				GL11.glVertex3f(float10 * float2, float11 * float2, float3);
			}

			GL11.glEnd();
		}
	}
}
