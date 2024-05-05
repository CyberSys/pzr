package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;


public class PartialDisk extends Quadric {
	private static final int CACHE_SIZE = 240;

	public void draw(float float1, float float2, int int1, int int2, float float3, float float4) {
		float[] floatArray = new float[240];
		float[] floatArray2 = new float[240];
		float float5 = 0.0F;
		float float6 = 0.0F;
		if (int1 >= 240) {
			int1 = 239;
		}

		if (int1 >= 2 && int2 >= 1 && !(float2 <= 0.0F) && !(float1 < 0.0F) && !(float1 > float2)) {
			if (float4 < -360.0F) {
				float4 = 360.0F;
			}

			if (float4 > 360.0F) {
				float4 = 360.0F;
			}

			if (float4 < 0.0F) {
				float3 += float4;
				float4 = -float4;
			}

			int int3;
			if (float4 == 360.0F) {
				int3 = int1;
			} else {
				int3 = int1 + 1;
			}

			float float7 = float2 - float1;
			float float8 = float3 / 180.0F * 3.1415927F;
			int int4;
			for (int4 = 0; int4 <= int1; ++int4) {
				float float9 = float8 + 3.1415927F * float4 / 180.0F * (float)int4 / (float)int1;
				floatArray[int4] = this.sin(float9);
				floatArray2[int4] = this.cos(float9);
			}

			if (float4 == 360.0F) {
				floatArray[int1] = floatArray[0];
				floatArray2[int1] = floatArray2[0];
			}

			switch (super.normals) {
			case 100000: 
			
			case 100001: 
				if (super.orientation == 100020) {
					GL11.glNormal3f(0.0F, 0.0F, 1.0F);
				} else {
					GL11.glNormal3f(0.0F, 0.0F, -1.0F);
				}

			
			case 100002: 
			
			}

			int int5;
			float float10;
			float float11;
			float float12;
			switch (super.drawStyle) {
			case 100010: 
				GL11.glBegin(0);
				for (int4 = 0; int4 < int3; ++int4) {
					float10 = floatArray[int4];
					float11 = floatArray2[int4];
					for (int5 = 0; int5 <= int2; ++int5) {
						float12 = float2 - float7 * ((float)int5 / (float)int2);
						if (super.textureFlag) {
							float5 = float12 / float2 / 2.0F;
							GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
						}

						GL11.glVertex3f(float12 * float10, float12 * float11, 0.0F);
					}
				}

				GL11.glEnd();
				break;
			
			case 100011: 
				if (float1 == float2) {
					GL11.glBegin(3);
					for (int4 = 0; int4 <= int1; ++int4) {
						if (super.textureFlag) {
							GL11.glTexCoord2f(floatArray[int4] / 2.0F + 0.5F, floatArray2[int4] / 2.0F + 0.5F);
						}

						GL11.glVertex3f(float1 * floatArray[int4], float1 * floatArray2[int4], 0.0F);
					}

					GL11.glEnd();
					break;
				} else {
					for (int5 = 0; int5 <= int2; ++int5) {
						float12 = float2 - float7 * ((float)int5 / (float)int2);
						if (super.textureFlag) {
							float5 = float12 / float2 / 2.0F;
						}

						GL11.glBegin(3);
						for (int4 = 0; int4 <= int1; ++int4) {
							if (super.textureFlag) {
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * floatArray[int4], float12 * floatArray2[int4], 0.0F);
						}

						GL11.glEnd();
					}

					for (int4 = 0; int4 < int3; ++int4) {
						float10 = floatArray[int4];
						float11 = floatArray2[int4];
						GL11.glBegin(3);
						for (int5 = 0; int5 <= int2; ++int5) {
							float12 = float2 - float7 * ((float)int5 / (float)int2);
							if (super.textureFlag) {
								float5 = float12 / float2 / 2.0F;
							}

							if (super.textureFlag) {
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * float10, float12 * float11, 0.0F);
						}

						GL11.glEnd();
					}

					return;
				}

			
			case 100012: 
				int int6;
				if (float1 != 0.0F) {
					int6 = int2;
				} else {
					int6 = int2 - 1;
					GL11.glBegin(6);
					if (super.textureFlag) {
						GL11.glTexCoord2f(0.5F, 0.5F);
					}

					GL11.glVertex3f(0.0F, 0.0F, 0.0F);
					float12 = float2 - float7 * ((float)(int2 - 1) / (float)int2);
					if (super.textureFlag) {
						float5 = float12 / float2 / 2.0F;
					}

					if (super.orientation == 100020) {
						for (int4 = int1; int4 >= 0; --int4) {
							if (super.textureFlag) {
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * floatArray[int4], float12 * floatArray2[int4], 0.0F);
						}
					} else {
						for (int4 = 0; int4 <= int1; ++int4) {
							if (super.textureFlag) {
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * floatArray[int4], float12 * floatArray2[int4], 0.0F);
						}
					}

					GL11.glEnd();
				}

				for (int5 = 0; int5 < int6; ++int5) {
					float12 = float2 - float7 * ((float)int5 / (float)int2);
					float float13 = float2 - float7 * ((float)(int5 + 1) / (float)int2);
					if (super.textureFlag) {
						float5 = float12 / float2 / 2.0F;
						float6 = float13 / float2 / 2.0F;
					}

					GL11.glBegin(8);
					for (int4 = 0; int4 <= int1; ++int4) {
						if (super.orientation == 100020) {
							if (super.textureFlag) {
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * floatArray[int4], float12 * floatArray2[int4], 0.0F);
							if (super.textureFlag) {
								GL11.glTexCoord2f(float6 * floatArray[int4] + 0.5F, float6 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float13 * floatArray[int4], float13 * floatArray2[int4], 0.0F);
						} else {
							if (super.textureFlag) {
								GL11.glTexCoord2f(float6 * floatArray[int4] + 0.5F, float6 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float13 * floatArray[int4], float13 * floatArray2[int4], 0.0F);
							if (super.textureFlag) {
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * floatArray[int4], float12 * floatArray2[int4], 0.0F);
						}
					}

					GL11.glEnd();
				}

				return;
			
			case 100013: 
				if (float4 < 360.0F) {
					for (int4 = 0; int4 <= int1; int4 += int1) {
						float10 = floatArray[int4];
						float11 = floatArray2[int4];
						GL11.glBegin(3);
						for (int5 = 0; int5 <= int2; ++int5) {
							float12 = float2 - float7 * ((float)int5 / (float)int2);
							if (super.textureFlag) {
								float5 = float12 / float2 / 2.0F;
								GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
							}

							GL11.glVertex3f(float12 * float10, float12 * float11, 0.0F);
						}

						GL11.glEnd();
					}
				}

				for (int5 = 0; int5 <= int2; int5 += int2) {
					float12 = float2 - float7 * ((float)int5 / (float)int2);
					if (super.textureFlag) {
						float5 = float12 / float2 / 2.0F;
					}

					GL11.glBegin(3);
					for (int4 = 0; int4 <= int1; ++int4) {
						if (super.textureFlag) {
							GL11.glTexCoord2f(float5 * floatArray[int4] + 0.5F, float5 * floatArray2[int4] + 0.5F);
						}

						GL11.glVertex3f(float12 * floatArray[int4], float12 * floatArray2[int4], 0.0F);
					}

					GL11.glEnd();
					if (float1 == float2) {
						break;
					}
				}

			
			}
		} else {
			System.err.println("PartialDisk: GLU_INVALID_VALUE");
		}
	}
}
