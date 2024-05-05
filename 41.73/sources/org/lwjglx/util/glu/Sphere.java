package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;


public class Sphere extends Quadric {

	public void draw(float float1, int int1, int int2) {
		boolean boolean1 = super.normals != 100002;
		float float2;
		if (super.orientation == 100021) {
			float2 = -1.0F;
		} else {
			float2 = 1.0F;
		}

		float float3 = 3.1415927F / (float)int2;
		float float4 = 6.2831855F / (float)int1;
		float float5;
		float float6;
		float float7;
		float float8;
		float float9;
		int int3;
		int int4;
		if (super.drawStyle == 100012) {
			if (!super.textureFlag) {
				GL11.glBegin(6);
				GL11.glNormal3f(0.0F, 0.0F, 1.0F);
				GL11.glVertex3f(0.0F, 0.0F, float2 * float1);
				for (int4 = 0; int4 <= int1; ++int4) {
					float6 = int4 == int1 ? 0.0F : (float)int4 * float4;
					float7 = -this.sin(float6) * this.sin(float3);
					float8 = this.cos(float6) * this.sin(float3);
					float9 = float2 * this.cos(float3);
					if (boolean1) {
						GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
					}

					GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
				}

				GL11.glEnd();
			}

			float float10 = 1.0F / (float)int1;
			float float11 = 1.0F / (float)int2;
			float float12 = 1.0F;
			byte byte1;
			int int5;
			if (super.textureFlag) {
				byte1 = 0;
				int5 = int2;
			} else {
				byte1 = 1;
				int5 = int2 - 1;
			}

			float float13;
			for (int3 = byte1; int3 < int5; ++int3) {
				float5 = (float)int3 * float3;
				GL11.glBegin(8);
				float13 = 0.0F;
				for (int4 = 0; int4 <= int1; ++int4) {
					float6 = int4 == int1 ? 0.0F : (float)int4 * float4;
					float7 = -this.sin(float6) * this.sin(float5);
					float8 = this.cos(float6) * this.sin(float5);
					float9 = float2 * this.cos(float5);
					if (boolean1) {
						GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
					}

					this.TXTR_COORD(float13, float12);
					GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
					float7 = -this.sin(float6) * this.sin(float5 + float3);
					float8 = this.cos(float6) * this.sin(float5 + float3);
					float9 = float2 * this.cos(float5 + float3);
					if (boolean1) {
						GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
					}

					this.TXTR_COORD(float13, float12 - float11);
					float13 += float10;
					GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
				}

				GL11.glEnd();
				float12 -= float11;
			}

			if (!super.textureFlag) {
				GL11.glBegin(6);
				GL11.glNormal3f(0.0F, 0.0F, -1.0F);
				GL11.glVertex3f(0.0F, 0.0F, -float1 * float2);
				float5 = 3.1415927F - float3;
				float13 = 1.0F;
				for (int4 = int1; int4 >= 0; --int4) {
					float6 = int4 == int1 ? 0.0F : (float)int4 * float4;
					float7 = -this.sin(float6) * this.sin(float5);
					float8 = this.cos(float6) * this.sin(float5);
					float9 = float2 * this.cos(float5);
					if (boolean1) {
						GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
					}

					float13 -= float10;
					GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
				}

				GL11.glEnd();
			}
		} else if (super.drawStyle != 100011 && super.drawStyle != 100013) {
			if (super.drawStyle == 100010) {
				GL11.glBegin(0);
				if (boolean1) {
					GL11.glNormal3f(0.0F, 0.0F, float2);
				}

				GL11.glVertex3f(0.0F, 0.0F, float1);
				if (boolean1) {
					GL11.glNormal3f(0.0F, 0.0F, -float2);
				}

				GL11.glVertex3f(0.0F, 0.0F, -float1);
				for (int3 = 1; int3 < int2 - 1; ++int3) {
					float5 = (float)int3 * float3;
					for (int4 = 0; int4 < int1; ++int4) {
						float6 = (float)int4 * float4;
						float7 = this.cos(float6) * this.sin(float5);
						float8 = this.sin(float6) * this.sin(float5);
						float9 = this.cos(float5);
						if (boolean1) {
							GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
						}

						GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
					}
				}

				GL11.glEnd();
			}
		} else {
			for (int3 = 1; int3 < int2; ++int3) {
				float5 = (float)int3 * float3;
				GL11.glBegin(2);
				for (int4 = 0; int4 < int1; ++int4) {
					float6 = (float)int4 * float4;
					float7 = this.cos(float6) * this.sin(float5);
					float8 = this.sin(float6) * this.sin(float5);
					float9 = this.cos(float5);
					if (boolean1) {
						GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
					}

					GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
				}

				GL11.glEnd();
			}

			for (int4 = 0; int4 < int1; ++int4) {
				float6 = (float)int4 * float4;
				GL11.glBegin(3);
				for (int3 = 0; int3 <= int2; ++int3) {
					float5 = (float)int3 * float3;
					float7 = this.cos(float6) * this.sin(float5);
					float8 = this.sin(float6) * this.sin(float5);
					float9 = this.cos(float5);
					if (boolean1) {
						GL11.glNormal3f(float7 * float2, float8 * float2, float9 * float2);
					}

					GL11.glVertex3f(float7 * float1, float8 * float1, float9 * float1);
				}

				GL11.glEnd();
			}
		}
	}
}
