package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;


public class Disk extends Quadric {

	public void draw(float float1, float float2, int int1, int int2) {
		if (super.normals != 100002) {
			if (super.orientation == 100020) {
				GL11.glNormal3f(0.0F, 0.0F, 1.0F);
			} else {
				GL11.glNormal3f(0.0F, 0.0F, -1.0F);
			}
		}

		float float3 = 6.2831855F / (float)int1;
		float float4 = (float2 - float1) / (float)int2;
		float float5;
		float float6;
		float float7;
		float float8;
		int int3;
		float float9;
		int int4;
		switch (super.drawStyle) {
		case 100010: 
			GL11.glBegin(0);
			for (int4 = 0; int4 < int1; ++int4) {
				float6 = (float)int4 * float3;
				float7 = this.sin(float6);
				float8 = this.cos(float6);
				for (int3 = 0; int3 <= int2; ++int3) {
					float9 = float1 * (float)int3 * float4;
					GL11.glVertex2f(float9 * float7, float9 * float8);
				}
			}

			GL11.glEnd();
			break;
		
		case 100011: 
			int int5;
			for (int4 = 0; int4 <= int2; ++int4) {
				float7 = float1 + (float)int4 * float4;
				GL11.glBegin(2);
				for (int5 = 0; int5 < int1; ++int5) {
					float8 = (float)int5 * float3;
					GL11.glVertex2f(float7 * this.sin(float8), float7 * this.cos(float8));
				}

				GL11.glEnd();
			}

			for (int5 = 0; int5 < int1; ++int5) {
				float7 = (float)int5 * float3;
				float8 = this.sin(float7);
				float float10 = this.cos(float7);
				GL11.glBegin(3);
				for (int4 = 0; int4 <= int2; ++int4) {
					float9 = float1 + (float)int4 * float4;
					GL11.glVertex2f(float9 * float8, float9 * float10);
				}

				GL11.glEnd();
			}

			return;
		
		case 100012: 
			float5 = 2.0F * float2;
			float8 = float1;
			for (int3 = 0; int3 < int2; ++int3) {
				float9 = float8 + float4;
				int int6;
				float float11;
				if (super.orientation == 100020) {
					GL11.glBegin(8);
					for (int6 = 0; int6 <= int1; ++int6) {
						if (int6 == int1) {
							float11 = 0.0F;
						} else {
							float11 = (float)int6 * float3;
						}

						float6 = this.sin(float11);
						float7 = this.cos(float11);
						this.TXTR_COORD(0.5F + float6 * float9 / float5, 0.5F + float7 * float9 / float5);
						GL11.glVertex2f(float9 * float6, float9 * float7);
						this.TXTR_COORD(0.5F + float6 * float8 / float5, 0.5F + float7 * float8 / float5);
						GL11.glVertex2f(float8 * float6, float8 * float7);
					}

					GL11.glEnd();
				} else {
					GL11.glBegin(8);
					for (int6 = int1; int6 >= 0; --int6) {
						if (int6 == int1) {
							float11 = 0.0F;
						} else {
							float11 = (float)int6 * float3;
						}

						float6 = this.sin(float11);
						float7 = this.cos(float11);
						this.TXTR_COORD(0.5F - float6 * float9 / float5, 0.5F + float7 * float9 / float5);
						GL11.glVertex2f(float9 * float6, float9 * float7);
						this.TXTR_COORD(0.5F - float6 * float8 / float5, 0.5F + float7 * float8 / float5);
						GL11.glVertex2f(float8 * float6, float8 * float7);
					}

					GL11.glEnd();
				}

				float8 = float9;
			}

			return;
		
		case 100013: 
			if ((double)float1 != 0.0) {
				GL11.glBegin(2);
				for (float5 = 0.0F; (double)float5 < 6.2831854820251465; float5 += float3) {
					float6 = float1 * this.sin(float5);
					float7 = float1 * this.cos(float5);
					GL11.glVertex2f(float6, float7);
				}

				GL11.glEnd();
			}

			GL11.glBegin(2);
			for (float5 = 0.0F; float5 < 6.2831855F; float5 += float3) {
				float6 = float2 * this.sin(float5);
				float7 = float2 * this.cos(float5);
				GL11.glVertex2f(float6, float7);
			}

			GL11.glEnd();
			break;
		
		default: 
			return;
		
		}
	}
}
