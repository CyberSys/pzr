package zombie.core.skinnedmodel;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import zombie.core.Color;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.model.VertexPositionNormalTangentTextureSkin;
import zombie.debug.DebugLog;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;


public final class HelperFunctions {
	private static final Vector3f s_zero3 = new Vector3f(0.0F, 0.0F, 0.0F);
	private static final Quaternion s_identityQ = new Quaternion();
	private static final Stack MatrixStack = new Stack();
	private static final AtomicBoolean MatrixLock = new AtomicBoolean(false);
	private static final ObjectPool VectorPool = new ObjectPool(Vector3f::new);

	public static int ToRgba(Color color) {
		return (int)color.a << 24 | (int)color.b << 16 | (int)color.g << 8 | (int)color.r;
	}

	public static void returnMatrix(Matrix4f matrix4f) {
		while (!MatrixLock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		assert !MatrixStack.contains(matrix4f);
		MatrixStack.push(matrix4f);
		MatrixLock.set(false);
	}

	public static Matrix4f getMatrix() {
		Matrix4f matrix4f = null;
		while (!MatrixLock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		if (MatrixStack.isEmpty()) {
			matrix4f = new Matrix4f();
		} else {
			matrix4f = (Matrix4f)MatrixStack.pop();
		}

		MatrixLock.set(false);
		return matrix4f;
	}

	public static Matrix4f getMatrix(Matrix4f matrix4f) {
		Matrix4f matrix4f2 = getMatrix();
		matrix4f2.load(matrix4f);
		return matrix4f2;
	}

	public static Vector3f getVector3f() {
		while (!MatrixLock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		Vector3f vector3f = (Vector3f)VectorPool.alloc();
		MatrixLock.set(false);
		return vector3f;
	}

	public static void returnVector3f(Vector3f vector3f) {
		while (!MatrixLock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		VectorPool.release((Object)vector3f);
		MatrixLock.set(false);
	}

	public static Matrix4f CreateFromQuaternion(Quaternion quaternion) {
		Matrix4f matrix4f = getMatrix();
		CreateFromQuaternion(quaternion, matrix4f);
		return matrix4f;
	}

	public static Matrix4f CreateFromQuaternion(Quaternion quaternion, Matrix4f matrix4f) {
		matrix4f.setIdentity();
		float float1 = quaternion.lengthSquared();
		float float2;
		float float3;
		if (float1 > 0.0F && float1 < 0.99999F || float1 > 1.00001F) {
			float2 = (float)Math.sqrt((double)float1);
			float3 = 1.0F / float2;
			quaternion.scale(float3);
		}

		float2 = quaternion.x * quaternion.x;
		float3 = quaternion.x * quaternion.y;
		float float4 = quaternion.x * quaternion.z;
		float float5 = quaternion.x * quaternion.w;
		float float6 = quaternion.y * quaternion.y;
		float float7 = quaternion.y * quaternion.z;
		float float8 = quaternion.y * quaternion.w;
		float float9 = quaternion.z * quaternion.z;
		float float10 = quaternion.z * quaternion.w;
		matrix4f.m00 = 1.0F - 2.0F * (float6 + float9);
		matrix4f.m10 = 2.0F * (float3 - float10);
		matrix4f.m20 = 2.0F * (float4 + float8);
		matrix4f.m30 = 0.0F;
		matrix4f.m01 = 2.0F * (float3 + float10);
		matrix4f.m11 = 1.0F - 2.0F * (float2 + float9);
		matrix4f.m21 = 2.0F * (float7 - float5) * 1.0F;
		matrix4f.m31 = 0.0F;
		matrix4f.m02 = 2.0F * (float4 - float8);
		matrix4f.m12 = 2.0F * (float7 + float5);
		matrix4f.m22 = 1.0F - 2.0F * (float2 + float6);
		matrix4f.m32 = 0.0F;
		matrix4f.m03 = 0.0F;
		matrix4f.m13 = 0.0F;
		matrix4f.m23 = 0.0F;
		matrix4f.m33 = 1.0F;
		matrix4f.m30 = 0.0F;
		matrix4f.m31 = 0.0F;
		matrix4f.m32 = 0.0F;
		matrix4f.transpose();
		return matrix4f;
	}

	public static Matrix4f CreateFromQuaternionPositionScale(Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2, Matrix4f matrix4f) {
		Matrix4f matrix4f2 = getMatrix();
		Matrix4f matrix4f3 = getMatrix();
		Matrix4f matrix4f4 = getMatrix();
		CreateFromQuaternionPositionScale(vector3f, quaternion, vector3f2, matrix4f, matrix4f3, matrix4f4, matrix4f2);
		returnMatrix(matrix4f2);
		returnMatrix(matrix4f3);
		returnMatrix(matrix4f4);
		return matrix4f;
	}

	public static void CreateFromQuaternionPositionScale(Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2, HelperFunctions.TransformResult_QPS transformResult_QPS) {
		CreateFromQuaternionPositionScale(vector3f, quaternion, vector3f2, transformResult_QPS.result, transformResult_QPS.trans, transformResult_QPS.rot, transformResult_QPS.scl);
	}

	private static void CreateFromQuaternionPositionScale(Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2, Matrix4f matrix4f, Matrix4f matrix4f2, Matrix4f matrix4f3, Matrix4f matrix4f4) {
		matrix4f4.setIdentity();
		matrix4f4.scale(vector3f2);
		matrix4f2.setIdentity();
		matrix4f2.translate(vector3f);
		matrix4f2.transpose();
		CreateFromQuaternion(quaternion, matrix4f3);
		Matrix4f.mul(matrix4f4, matrix4f3, matrix4f3);
		Matrix4f.mul(matrix4f3, matrix4f2, matrix4f);
	}

	public static void TransformVertices(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray, List list) {
		Vector3 vector3 = new Vector3();
		Vector3 vector32 = new Vector3();
		Vector4f vector4f = new Vector4f();
		VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray2 = vertexPositionNormalTangentTextureSkinArray;
		int int1 = vertexPositionNormalTangentTextureSkinArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin = vertexPositionNormalTangentTextureSkinArray2[int2];
			vector3.reset();
			vector32.reset();
			Vector3 vector33 = vertexPositionNormalTangentTextureSkin.Position;
			Vector3 vector34 = vertexPositionNormalTangentTextureSkin.Normal;
			ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.x, (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.X), vector33, vector34, vector4f, vector3, vector32);
			ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.y, (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.Y), vector33, vector34, vector4f, vector3, vector32);
			ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.z, (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.Z), vector33, vector34, vector4f, vector3, vector32);
			ApplyBlendBone(vertexPositionNormalTangentTextureSkin.BlendWeights.w, (Matrix4f)list.get(vertexPositionNormalTangentTextureSkin.BlendIndices.W), vector33, vector34, vector4f, vector3, vector32);
			vector33.set(vector3);
			vector34.set(vector32);
		}
	}

	public static void ApplyBlendBone(float float1, Matrix4f matrix4f, Vector3 vector3, Vector3 vector32, Vector4f vector4f, Vector3 vector33, Vector3 vector34) {
		if (float1 > 0.0F) {
			float float2 = vector3.x();
			float float3 = vector3.y();
			float float4 = vector3.z();
			float float5 = matrix4f.m00 * float2 + matrix4f.m01 * float3 + matrix4f.m02 * float4 + matrix4f.m03;
			float float6 = matrix4f.m10 * float2 + matrix4f.m11 * float3 + matrix4f.m12 * float4 + matrix4f.m13;
			float float7 = matrix4f.m20 * float2 + matrix4f.m21 * float3 + matrix4f.m22 * float4 + matrix4f.m23;
			vector33.add(float5 * float1, float6 * float1, float7 * float1);
			float2 = vector32.x();
			float3 = vector32.y();
			float4 = vector32.z();
			float5 = matrix4f.m00 * float2 + matrix4f.m01 * float3 + matrix4f.m02 * float4;
			float6 = matrix4f.m10 * float2 + matrix4f.m11 * float3 + matrix4f.m12 * float4;
			float7 = matrix4f.m20 * float2 + matrix4f.m21 * float3 + matrix4f.m22 * float4;
			vector34.add(float5 * float1, float6 * float1, float7 * float1);
		}
	}

	public static Vector3f getPosition(Matrix4f matrix4f, Vector3f vector3f) {
		vector3f.set(matrix4f.m03, matrix4f.m13, matrix4f.m23);
		return vector3f;
	}

	public static void setPosition(Matrix4f matrix4f, Vector3f vector3f) {
		matrix4f.m03 = vector3f.x;
		matrix4f.m13 = vector3f.y;
		matrix4f.m23 = vector3f.z;
	}

	public static Quaternion getRotation(Matrix4f matrix4f, Quaternion quaternion) {
		return Quaternion.setFromMatrix(matrix4f, quaternion);
	}

	public static void transform(Quaternion quaternion, Vector3f vector3f, Vector3f vector3f2) {
		quaternion.normalise();
		float float1 = quaternion.w;
		float float2 = quaternion.x;
		float float3 = quaternion.y;
		float float4 = quaternion.z;
		float float5 = float1 * float1;
		float float6 = float2 * float2 + float3 * float3 + float4 * float4;
		float float7 = vector3f.x;
		float float8 = vector3f.y;
		float float9 = vector3f.z;
		float float10 = float3 * float9 - float4 * float8;
		float float11 = float4 * float7 - float2 * float9;
		float float12 = float2 * float8 - float3 * float7;
		float float13 = float7 * float2 + float8 * float3 + float9 * float4;
		float float14 = (float5 - float6) * float7 + 2.0F * float1 * float10 + 2.0F * float2 * float13;
		float float15 = (float5 - float6) * float8 + 2.0F * float1 * float11 + 2.0F * float3 * float13;
		float float16 = (float5 - float6) * float9 + 2.0F * float1 * float12 + 2.0F * float4 * float13;
		vector3f2.set(float14, float15, float16);
	}

	private static Vector4f transform(Matrix4f matrix4f, Vector4f vector4f, Vector4f vector4f2) {
		float float1 = matrix4f.m00 * vector4f.x + matrix4f.m01 * vector4f.y + matrix4f.m02 * vector4f.z + matrix4f.m30 * vector4f.w;
		float float2 = matrix4f.m10 * vector4f.x + matrix4f.m11 * vector4f.y + matrix4f.m12 * vector4f.z + matrix4f.m31 * vector4f.w;
		float float3 = matrix4f.m20 * vector4f.x + matrix4f.m21 * vector4f.y + matrix4f.m22 * vector4f.z + matrix4f.m32 * vector4f.w;
		float float4 = matrix4f.m03 * vector4f.x + matrix4f.m13 * vector4f.y + matrix4f.m23 * vector4f.z + matrix4f.m33 * vector4f.w;
		vector4f2.x = float1;
		vector4f2.y = float2;
		vector4f2.z = float3;
		vector4f2.w = float4;
		return vector4f2;
	}

	public static float getRotationY(Quaternion quaternion) {
		quaternion.normalise();
		float float1 = quaternion.w;
		float float2 = quaternion.x;
		float float3 = quaternion.y;
		float float4 = quaternion.z;
		float float5 = float1 * float1;
		float float6 = float2 * float2 + float3 * float3 + float4 * float4;
		float float7 = float3 * 0.0F - float4 * 0.0F;
		float float8 = float2 * 0.0F - float3 * 1.0F;
		float float9 = 1.0F * float2 + 0.0F * float3 + 0.0F * float4;
		float float10 = (float5 - float6) * 1.0F + 2.0F * float1 * float7 + 2.0F * float2 * float9;
		float float11 = (float5 - float6) * 0.0F + 2.0F * float1 * float8 + 2.0F * float4 * float9;
		float float12 = (float)Math.atan2((double)(-float11), (double)float10);
		return PZMath.wrap(float12, -3.1415927F, 3.1415927F);
	}

	public static float getRotationZ(Quaternion quaternion) {
		float float1 = quaternion.w;
		float float2 = quaternion.x;
		float float3 = quaternion.y;
		float float4 = quaternion.z;
		float float5 = float1 * float1;
		float float6 = float2 * float2 + float3 * float3 + float4 * float4;
		float float7 = float4 * 1.0F;
		float float8 = 1.0F * float2;
		float float9 = (float5 - float6) * 1.0F + 2.0F * float2 * float8;
		float float10 = 2.0F * float1 * float7 + 2.0F * float3 * float8;
		float float11 = (float)Math.atan2((double)float10, (double)float9);
		return float11;
	}

	public static Vector3f ToEulerAngles(Quaternion quaternion, Vector3f vector3f) {
		double double1 = 2.0 * (double)(quaternion.w * quaternion.x + quaternion.y * quaternion.z);
		double double2 = 1.0 - 2.0 * (double)(quaternion.x * quaternion.x + quaternion.y * quaternion.y);
		vector3f.x = (float)Math.atan2(double1, double2);
		double double3 = 2.0 * (double)(quaternion.w * quaternion.y - quaternion.z * quaternion.x);
		if (Math.abs(double3) >= 1.0) {
			vector3f.y = (float)Math.copySign(1.5707963705062866, double3);
		} else {
			vector3f.y = (float)Math.asin(double3);
		}

		double double4 = 2.0 * (double)(quaternion.w * quaternion.z + quaternion.x * quaternion.y);
		double double5 = 1.0 - 2.0 * (double)(quaternion.y * quaternion.y + quaternion.z * quaternion.z);
		vector3f.z = (float)Math.atan2(double4, double5);
		return vector3f;
	}

	public static Quaternion ToQuaternion(double double1, double double2, double double3, Quaternion quaternion) {
		double double4 = Math.cos(double3 * 0.5);
		double double5 = Math.sin(double3 * 0.5);
		double double6 = Math.cos(double2 * 0.5);
		double double7 = Math.sin(double2 * 0.5);
		double double8 = Math.cos(double1 * 0.5);
		double double9 = Math.sin(double1 * 0.5);
		quaternion.w = (float)(double4 * double6 * double8 + double5 * double7 * double9);
		quaternion.x = (float)(double4 * double6 * double9 - double5 * double7 * double8);
		quaternion.y = (float)(double5 * double6 * double9 + double4 * double7 * double8);
		quaternion.z = (float)(double5 * double6 * double8 - double4 * double7 * double9);
		return quaternion;
	}

	public static Vector3f getZero3() {
		s_zero3.set(0.0F, 0.0F, 0.0F);
		return s_zero3;
	}

	public static Quaternion getIdentityQ() {
		s_identityQ.setIdentity();
		return s_identityQ;
	}

	static  {
		HelperFunctions.UnitTests.runAll();
	}

	public static class TransformResult_QPS {
		public final Matrix4f result;
		final Matrix4f trans;
		final Matrix4f rot;
		final Matrix4f scl;

		public TransformResult_QPS() {
			this.result = new Matrix4f();
			this.trans = new Matrix4f();
			this.rot = new Matrix4f();
			this.scl = new Matrix4f();
		}

		public TransformResult_QPS(Matrix4f matrix4f) {
			this.result = matrix4f;
			this.trans = new Matrix4f();
			this.rot = new Matrix4f();
			this.scl = new Matrix4f();
		}
	}

	private static final class UnitTests {
		private static final Runnable[] s_unitTests = new Runnable[0];

		private static void runAll() {
			PZArrayUtil.forEach((Object[])s_unitTests, Runnable::run);
		}

		private static final class transformQuaternion {

			public static void run() {
				DebugLog.UnitTests.println("UnitTest_transformQuaternion");
				DebugLog.UnitTests.println("roll, pitch, yaw, out.x, out.y, out.z, cout.x, cout.y, cout.z, result");
				Quaternion quaternion = new Quaternion();
				new Vector3f(0.0F, 0.0F, 0.0F);
				new Vector3f(1.0F, 1.0F, 1.0F);
				Vector3f vector3f = new Vector3f();
				Vector3f vector3f2 = new Vector3f();
				Matrix4f matrix4f = new Matrix4f();
				Vector4f vector4f = new Vector4f();
				Vector4f vector4f2 = new Vector4f();
				Vector3f vector3f3 = new Vector3f(1.0F, 0.0F, 0.0F);
				Vector3f vector3f4 = new Vector3f(0.0F, 1.0F, 0.0F);
				Vector3f vector3f5 = new Vector3f(0.0F, 0.0F, 1.0F);
				runTest(0.0F, 0.0F, 90.0F, quaternion, vector3f, vector3f2, matrix4f, vector4f, vector4f2, vector3f3, vector3f4, vector3f5);
				runTest(0.0F, 0.0F, 5.0F, quaternion, vector3f, vector3f2, matrix4f, vector4f, vector4f2, vector3f3, vector3f4, vector3f5);
				for (int int1 = 0; int1 < 10; ++int1) {
					float float1 = PZMath.wrap((float)int1 / 10.0F * 360.0F, -180.0F, 180.0F);
					for (int int2 = 0; int2 < 10; ++int2) {
						float float2 = PZMath.wrap((float)int2 / 10.0F * 360.0F, -180.0F, 180.0F);
						for (int int3 = 0; int3 < 10; ++int3) {
							float float3 = PZMath.wrap((float)int3 / 10.0F * 360.0F, -180.0F, 180.0F);
							runTest(float1, float2, float3, quaternion, vector3f, vector3f2, matrix4f, vector4f, vector4f2, vector3f3, vector3f4, vector3f5);
						}
					}
				}

				DebugLog.UnitTests.println("UnitTest_transformQuaternion. Complete");
			}

			public static void runTest(float float1, float float2, float float3, Quaternion quaternion, Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f, Vector4f vector4f, Vector4f vector4f2, Vector3f vector3f3, Vector3f vector3f4, Vector3f vector3f5) {
				Vector3f vector3f6 = new Vector3f(15.0F, 0.0F, 0.0F);
				matrix4f.setIdentity();
				matrix4f.translate(vector3f6);
				matrix4f.rotate(float1 * 0.017453292F, vector3f3);
				matrix4f.rotate(float2 * 0.017453292F, vector3f4);
				matrix4f.rotate(float3 * 0.017453292F, vector3f5);
				HelperFunctions.getRotation(matrix4f, quaternion);
				vector3f.set(1.0F, 0.0F, 0.0F);
				vector4f.set(vector3f.x, vector3f.y, vector3f.z, 1.0F);
				HelperFunctions.transform(matrix4f, vector4f, vector4f2);
				HelperFunctions.transform(quaternion, vector3f, vector3f2);
				vector3f2.x += vector3f6.x;
				vector3f2.y += vector3f6.y;
				vector3f2.z += vector3f6.z;
				boolean boolean1 = PZMath.equal(vector3f2.x, vector4f2.x, 0.01F) && PZMath.equal(vector3f2.y, vector4f2.y, 0.01F) && PZMath.equal(vector3f2.z, vector4f2.z, 0.01F);
				DebugLog.UnitTests.printUnitTest("%f,%f,%f,%f,%f,%f,%f,%f,%f", boolean1, float1, float2, float3, vector3f2.x, vector3f2.y, vector3f2.z, vector4f2.x, vector4f2.y, vector4f2.z);
			}
		}

		private static final class getRotationMatrix {

			public static void run() {
				DebugLog.UnitTests.println("UnitTest_getRotationMatrix");
				DebugLog.UnitTests.println("q.x, q.y, q.z, q.w, q_out.x, q_out.y, q_out.z, q_out.w");
				Quaternion quaternion = new Quaternion();
				Vector4f vector4f = new Vector4f();
				Matrix4f matrix4f = new Matrix4f();
				Quaternion quaternion2 = new Quaternion();
				Quaternion quaternion3 = new Quaternion();
				for (int int1 = 0; int1 < 360; int1 += 10) {
					float float1 = PZMath.wrap((float)int1, -180.0F, 180.0F);
					vector4f.set(1.0F, 0.0F, 0.0F, float1 * 0.017453292F);
					quaternion.setFromAxisAngle(vector4f);
					HelperFunctions.CreateFromQuaternion(quaternion, matrix4f);
					HelperFunctions.getRotation(matrix4f, quaternion2);
					quaternion3.set(-quaternion2.x, -quaternion2.y, -quaternion2.z, -quaternion2.w);
					boolean boolean1 = PZMath.equal(quaternion.x, quaternion2.x, 0.01F) && PZMath.equal(quaternion.y, quaternion2.y, 0.01F) && PZMath.equal(quaternion.z, quaternion2.z, 0.01F) && PZMath.equal(quaternion.w, quaternion2.w, 0.01F) || PZMath.equal(quaternion.x, quaternion3.x, 0.01F) && PZMath.equal(quaternion.y, quaternion3.y, 0.01F) && PZMath.equal(quaternion.z, quaternion3.z, 0.01F) && PZMath.equal(quaternion.w, quaternion3.w, 0.01F);
					DebugLog.UnitTests.printUnitTest("%f,%f,%f,%f, %f,%f,%f,%f", boolean1, quaternion.x, quaternion.y, quaternion.z, quaternion.w, quaternion2.x, quaternion2.y, quaternion2.z, quaternion2.w);
				}

				DebugLog.UnitTests.println("UnitTest_getRotationMatrix. Complete");
			}
		}

		private static final class getRotationY {

			public static void run() {
				DebugLog.UnitTests.println("UnitTest_getRotationY");
				DebugLog.UnitTests.println("in, out, result");
				Quaternion quaternion = new Quaternion();
				for (int int1 = 0; int1 < 360; ++int1) {
					float float1 = PZMath.wrap((float)int1, -180.0F, 180.0F);
					quaternion.setFromAxisAngle(new Vector4f(0.0F, 1.0F, 0.0F, float1 * 0.017453292F));
					float float2 = HelperFunctions.getRotationY(quaternion) * 57.295776F;
					boolean boolean1 = PZMath.equal(float1, float2, 0.001F);
					DebugLog.UnitTests.printUnitTest("%f,%f", boolean1, float1, float2);
				}

				DebugLog.UnitTests.println("UnitTest_getRotationY. Complete");
			}
		}

		private static final class getRotationZ {

			public static void run() {
				DebugLog.UnitTests.println("UnitTest_getRotationZ");
				DebugLog.UnitTests.println("in, out, result");
				Quaternion quaternion = new Quaternion();
				for (int int1 = 0; int1 < 360; ++int1) {
					float float1 = PZMath.wrap((float)int1, -180.0F, 180.0F);
					quaternion.setFromAxisAngle(new Vector4f(0.0F, 0.0F, 1.0F, float1 * 0.017453292F));
					float float2 = HelperFunctions.getRotationZ(quaternion) * 57.295776F;
					boolean boolean1 = PZMath.equal(float1, float2, 0.001F);
					DebugLog.UnitTests.printUnitTest("%f,%f", boolean1, float1, float2);
				}

				DebugLog.UnitTests.println("UnitTest_getRotationZ. Complete");
			}
		}
	}
}
