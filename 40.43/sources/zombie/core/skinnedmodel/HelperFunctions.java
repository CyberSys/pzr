package zombie.core.skinnedmodel;

import java.util.Stack;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.Color;


public class HelperFunctions {
	public static Stack MatrixStack = new Stack();

	public static int ToRgba(Color color) {
		return (int)color.a << 24 | (int)color.b << 16 | (int)color.g << 8 | (int)color.r;
	}

	public static void returnMatrix(Matrix4f matrix4f) {
		MatrixStack.push(matrix4f);
	}

	public static Matrix4f getMatrix() {
		Matrix4f matrix4f = null;
		if (MatrixStack.isEmpty()) {
			matrix4f = new Matrix4f();
		} else {
			matrix4f = (Matrix4f)MatrixStack.pop();
		}

		return matrix4f;
	}

	public static Matrix4f getMatrix(Matrix4f matrix4f) {
		Matrix4f matrix4f2 = null;
		if (MatrixStack.isEmpty()) {
			matrix4f2 = new Matrix4f();
		} else {
			matrix4f2 = (Matrix4f)MatrixStack.pop();
		}

		matrix4f2.load(matrix4f);
		return matrix4f2;
	}

	public static Matrix4f CreateFromQuaternion(Quaternion quaternion) {
		Matrix4f matrix4f = getMatrix();
		matrix4f.setIdentity();
		if (quaternion.length() > 0.0F) {
			quaternion.normalise();
		}

		float float1 = quaternion.x * quaternion.x;
		float float2 = quaternion.x * quaternion.y;
		float float3 = quaternion.x * quaternion.z;
		float float4 = quaternion.x * quaternion.w;
		float float5 = quaternion.y * quaternion.y;
		float float6 = quaternion.y * quaternion.z;
		float float7 = quaternion.y * quaternion.w;
		float float8 = quaternion.z * quaternion.z;
		float float9 = quaternion.z * quaternion.w;
		matrix4f.m00 = 1.0F - 2.0F * (float5 + float8);
		matrix4f.m10 = 2.0F * (float2 - float9);
		matrix4f.m20 = 2.0F * (float3 + float7);
		matrix4f.m30 = 0.0F;
		matrix4f.m01 = 2.0F * (float2 + float9);
		matrix4f.m11 = 1.0F - 2.0F * (float1 + float8);
		matrix4f.m21 = 2.0F * (float6 - float4) * 1.0F;
		matrix4f.m31 = 0.0F;
		matrix4f.m02 = 2.0F * (float3 - float7);
		matrix4f.m12 = 2.0F * (float6 + float4);
		matrix4f.m22 = 1.0F - 2.0F * (float1 + float5);
		matrix4f.m32 = 0.0F;
		matrix4f.m03 = 0.0F;
		matrix4f.m13 = 0.0F;
		matrix4f.m23 = 0.0F;
		matrix4f.m33 = 1.0F;
		matrix4f.m30 = 0.0F;
		matrix4f.m31 = 0.0F;
		matrix4f.m32 = 0.0F;
		matrix4f = (Matrix4f)matrix4f.transpose();
		return matrix4f;
	}

	public static Matrix4f CreateFromQuaternionPosition(Quaternion quaternion, Vector3f vector3f) {
		Matrix4f matrix4f = CreateFromQuaternion(quaternion);
		Matrix4f matrix4f2 = getMatrix();
		matrix4f2.setIdentity();
		matrix4f2.translate(vector3f);
		matrix4f2 = (Matrix4f)matrix4f2.transpose();
		Matrix4f matrix4f3 = getMatrix();
		Matrix4f.mul(matrix4f, matrix4f2, matrix4f3);
		returnMatrix(matrix4f2);
		returnMatrix(matrix4f);
		return matrix4f3;
	}
}
