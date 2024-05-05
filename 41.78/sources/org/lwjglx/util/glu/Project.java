package org.lwjglx.util.glu;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;


public class Project extends Util {
	private static final float[] IDENTITY_MATRIX = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
	private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer finalMatrix = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
	private static final float[] in = new float[4];
	private static final float[] out = new float[4];
	private static final float[] forward = new float[3];
	private static final float[] side = new float[3];
	private static final float[] up = new float[3];

	private static void __gluMakeIdentityf(FloatBuffer floatBuffer) {
		int int1 = floatBuffer.position();
		floatBuffer.put(IDENTITY_MATRIX);
		floatBuffer.position(int1);
	}

	private static void __gluMultMatrixVecf(FloatBuffer floatBuffer, float[] floatArray, float[] floatArray2) {
		for (int int1 = 0; int1 < 4; ++int1) {
			floatArray2[int1] = floatArray[0] * floatBuffer.get(floatBuffer.position() + 0 + int1) + floatArray[1] * floatBuffer.get(floatBuffer.position() + 4 + int1) + floatArray[2] * floatBuffer.get(floatBuffer.position() + 8 + int1) + floatArray[3] * floatBuffer.get(floatBuffer.position() + 12 + int1);
		}
	}

	private static boolean __gluInvertMatrixf(FloatBuffer floatBuffer, FloatBuffer floatBuffer2) {
		FloatBuffer floatBuffer3 = tempMatrix;
		int int1;
		for (int1 = 0; int1 < 16; ++int1) {
			floatBuffer3.put(int1, floatBuffer.get(int1 + floatBuffer.position()));
		}

		__gluMakeIdentityf(floatBuffer2);
		for (int1 = 0; int1 < 4; ++int1) {
			int int2 = int1;
			int int3;
			for (int3 = int1 + 1; int3 < 4; ++int3) {
				if (Math.abs(floatBuffer3.get(int3 * 4 + int1)) > Math.abs(floatBuffer3.get(int1 * 4 + int1))) {
					int2 = int3;
				}
			}

			int int4;
			float float1;
			if (int2 != int1) {
				for (int4 = 0; int4 < 4; ++int4) {
					float1 = floatBuffer3.get(int1 * 4 + int4);
					floatBuffer3.put(int1 * 4 + int4, floatBuffer3.get(int2 * 4 + int4));
					floatBuffer3.put(int2 * 4 + int4, float1);
					float1 = floatBuffer2.get(int1 * 4 + int4);
					floatBuffer2.put(int1 * 4 + int4, floatBuffer2.get(int2 * 4 + int4));
					floatBuffer2.put(int2 * 4 + int4, float1);
				}
			}

			if (floatBuffer3.get(int1 * 4 + int1) == 0.0F) {
				return false;
			}

			float1 = floatBuffer3.get(int1 * 4 + int1);
			for (int4 = 0; int4 < 4; ++int4) {
				floatBuffer3.put(int1 * 4 + int4, floatBuffer3.get(int1 * 4 + int4) / float1);
				floatBuffer2.put(int1 * 4 + int4, floatBuffer2.get(int1 * 4 + int4) / float1);
			}

			for (int3 = 0; int3 < 4; ++int3) {
				if (int3 != int1) {
					float1 = floatBuffer3.get(int3 * 4 + int1);
					for (int4 = 0; int4 < 4; ++int4) {
						floatBuffer3.put(int3 * 4 + int4, floatBuffer3.get(int3 * 4 + int4) - floatBuffer3.get(int1 * 4 + int4) * float1);
						floatBuffer2.put(int3 * 4 + int4, floatBuffer2.get(int3 * 4 + int4) - floatBuffer2.get(int1 * 4 + int4) * float1);
					}
				}
			}
		}

		return true;
	}

	private static void __gluMultMatricesf(FloatBuffer floatBuffer, FloatBuffer floatBuffer2, FloatBuffer floatBuffer3) {
		for (int int1 = 0; int1 < 4; ++int1) {
			for (int int2 = 0; int2 < 4; ++int2) {
				floatBuffer3.put(floatBuffer3.position() + int1 * 4 + int2, floatBuffer.get(floatBuffer.position() + int1 * 4 + 0) * floatBuffer2.get(floatBuffer2.position() + 0 + int2) + floatBuffer.get(floatBuffer.position() + int1 * 4 + 1) * floatBuffer2.get(floatBuffer2.position() + 4 + int2) + floatBuffer.get(floatBuffer.position() + int1 * 4 + 2) * floatBuffer2.get(floatBuffer2.position() + 8 + int2) + floatBuffer.get(floatBuffer.position() + int1 * 4 + 3) * floatBuffer2.get(floatBuffer2.position() + 12 + int2));
			}
		}
	}

	public static void gluPerspective(float float1, float float2, float float3, float float4) {
		float float5 = float1 / 2.0F * 3.1415927F / 180.0F;
		float float6 = float4 - float3;
		float float7 = (float)Math.sin((double)float5);
		if (float6 != 0.0F && float7 != 0.0F && float2 != 0.0F) {
			float float8 = (float)Math.cos((double)float5) / float7;
			__gluMakeIdentityf(matrix);
			matrix.put(0, float8 / float2);
			matrix.put(5, float8);
			matrix.put(10, -(float4 + float3) / float6);
			matrix.put(11, -1.0F);
			matrix.put(14, -2.0F * float3 * float4 / float6);
			matrix.put(15, 0.0F);
			GL11.glMultMatrixf(matrix);
		}
	}

	public static void gluLookAt(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
		float[] floatArray = forward;
		float[] floatArray2 = side;
		float[] floatArray3 = up;
		floatArray[0] = float4 - float1;
		floatArray[1] = float5 - float2;
		floatArray[2] = float6 - float3;
		floatArray3[0] = float7;
		floatArray3[1] = float8;
		floatArray3[2] = float9;
		normalize(floatArray);
		cross(floatArray, floatArray3, floatArray2);
		normalize(floatArray2);
		cross(floatArray2, floatArray, floatArray3);
		__gluMakeIdentityf(matrix);
		matrix.put(0, floatArray2[0]);
		matrix.put(4, floatArray2[1]);
		matrix.put(8, floatArray2[2]);
		matrix.put(1, floatArray3[0]);
		matrix.put(5, floatArray3[1]);
		matrix.put(9, floatArray3[2]);
		matrix.put(2, -floatArray[0]);
		matrix.put(6, -floatArray[1]);
		matrix.put(10, -floatArray[2]);
		GL11.glMultMatrixf(matrix);
		GL11.glTranslatef(-float1, -float2, -float3);
	}

	public static boolean gluProject(float float1, float float2, float float3, FloatBuffer floatBuffer, FloatBuffer floatBuffer2, IntBuffer intBuffer, FloatBuffer floatBuffer3) {
		float[] floatArray = in;
		float[] floatArray2 = out;
		floatArray[0] = float1;
		floatArray[1] = float2;
		floatArray[2] = float3;
		floatArray[3] = 1.0F;
		__gluMultMatrixVecf(floatBuffer, floatArray, floatArray2);
		__gluMultMatrixVecf(floatBuffer2, floatArray2, floatArray);
		if ((double)floatArray[3] == 0.0) {
			return false;
		} else {
			floatArray[3] = 1.0F / floatArray[3] * 0.5F;
			floatArray[0] = floatArray[0] * floatArray[3] + 0.5F;
			floatArray[1] = floatArray[1] * floatArray[3] + 0.5F;
			floatArray[2] = floatArray[2] * floatArray[3] + 0.5F;
			floatBuffer3.put(0, floatArray[0] * (float)intBuffer.get(intBuffer.position() + 2) + (float)intBuffer.get(intBuffer.position() + 0));
			floatBuffer3.put(1, floatArray[1] * (float)intBuffer.get(intBuffer.position() + 3) + (float)intBuffer.get(intBuffer.position() + 1));
			floatBuffer3.put(2, floatArray[2]);
			return true;
		}
	}

	public static boolean gluUnProject(float float1, float float2, float float3, FloatBuffer floatBuffer, FloatBuffer floatBuffer2, IntBuffer intBuffer, FloatBuffer floatBuffer3) {
		float[] floatArray = in;
		float[] floatArray2 = out;
		__gluMultMatricesf(floatBuffer, floatBuffer2, finalMatrix);
		if (!__gluInvertMatrixf(finalMatrix, finalMatrix)) {
			return false;
		} else {
			floatArray[0] = float1;
			floatArray[1] = float2;
			floatArray[2] = float3;
			floatArray[3] = 1.0F;
			floatArray[0] = (floatArray[0] - (float)intBuffer.get(intBuffer.position() + 0)) / (float)intBuffer.get(intBuffer.position() + 2);
			floatArray[1] = (floatArray[1] - (float)intBuffer.get(intBuffer.position() + 1)) / (float)intBuffer.get(intBuffer.position() + 3);
			floatArray[0] = floatArray[0] * 2.0F - 1.0F;
			floatArray[1] = floatArray[1] * 2.0F - 1.0F;
			floatArray[2] = floatArray[2] * 2.0F - 1.0F;
			__gluMultMatrixVecf(finalMatrix, floatArray, floatArray2);
			if ((double)floatArray2[3] == 0.0) {
				return false;
			} else {
				floatArray2[3] = 1.0F / floatArray2[3];
				floatBuffer3.put(floatBuffer3.position() + 0, floatArray2[0] * floatArray2[3]);
				floatBuffer3.put(floatBuffer3.position() + 1, floatArray2[1] * floatArray2[3]);
				floatBuffer3.put(floatBuffer3.position() + 2, floatArray2[2] * floatArray2[3]);
				return true;
			}
		}
	}

	public static void gluPickMatrix(float float1, float float2, float float3, float float4, IntBuffer intBuffer) {
		if (!(float3 <= 0.0F) && !(float4 <= 0.0F)) {
			GL11.glTranslatef(((float)intBuffer.get(intBuffer.position() + 2) - 2.0F * (float1 - (float)intBuffer.get(intBuffer.position() + 0))) / float3, ((float)intBuffer.get(intBuffer.position() + 3) - 2.0F * (float2 - (float)intBuffer.get(intBuffer.position() + 1))) / float4, 0.0F);
			GL11.glScalef((float)intBuffer.get(intBuffer.position() + 2) / float3, (float)intBuffer.get(intBuffer.position() + 3) / float4, 1.0F);
		}
	}
}
