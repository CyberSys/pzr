package zombie.core.skinnedmodel;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;


public class Matrix4 {
	private FloatBuffer matrix;
	public static Matrix4 Identity = new Matrix4();
	private FloatBuffer direct;

	public Matrix4() {
		this.matrix = FloatBuffer.allocate(16);
	}

	public Matrix4(float[] floatArray) {
		this();
		this.put(floatArray);
	}

	public Matrix4(Matrix4 matrix4) {
		this();
		this.put(matrix4);
	}

	public Matrix4 clear() {
		for (int int1 = 0; int1 < 16; ++int1) {
			this.matrix.put(int1, 0.0F);
		}

		return this;
	}

	public Matrix4 clearToIdentity() {
		return this.clear().put(0, 1.0F).put(5, 1.0F).put(10, 1.0F).put(15, 1.0F);
	}

	public Matrix4 clearToOrtho(float float1, float float2, float float3, float float4, float float5, float float6) {
		return this.clear().put(0, 2.0F / (float2 - float1)).put(5, 2.0F / (float4 - float3)).put(10, -2.0F / (float6 - float5)).put(12, -(float2 + float1) / (float2 - float1)).put(13, -(float4 + float3) / (float4 - float3)).put(14, -(float6 + float5) / (float6 - float5)).put(15, 1.0F);
	}

	public Matrix4 clearToPerspective(float float1, float float2, float float3, float float4, float float5) {
		float float6 = 1.0F / (float)Math.tan((double)(float1 / 2.0F));
		return this.clear().put(0, float6 / (float2 / float3)).put(5, float6).put(10, (float5 + float4) / (float4 - float5)).put(14, 2.0F * float5 * float4 / (float4 - float5)).put(11, -1.0F);
	}

	public float get(int int1) {
		return this.matrix.get(int1);
	}

	public Matrix4 put(int int1, float float1) {
		this.matrix.put(int1, float1);
		return this;
	}

	public Matrix4 put(int int1, Vector3 vector3, float float1) {
		this.put(int1 * 4 + 0, vector3.x());
		this.put(int1 * 4 + 1, vector3.y());
		this.put(int1 * 4 + 2, vector3.z());
		this.put(int1 * 4 + 3, float1);
		return this;
	}

	public Matrix4 put(float[] floatArray) {
		if (floatArray.length < 16) {
			throw new IllegalArgumentException("float array must have at least 16 values.");
		} else {
			this.matrix.position(0);
			this.matrix.put(floatArray, 0, 16);
			return this;
		}
	}

	public Matrix4 put(Matrix4 matrix4) {
		FloatBuffer floatBuffer = matrix4.getBuffer();
		while (floatBuffer.hasRemaining()) {
			this.matrix.put(floatBuffer.get());
		}

		return this;
	}

	public Matrix4 mult(float[] floatArray) {
		float[] floatArray2 = new float[16];
		for (int int1 = 0; int1 < 16; int1 += 4) {
			floatArray2[int1 + 0] = this.get(0) * floatArray[int1] + this.get(4) * floatArray[int1 + 1] + this.get(8) * floatArray[int1 + 2] + this.get(12) * floatArray[int1 + 3];
			floatArray2[int1 + 1] = this.get(1) * floatArray[int1] + this.get(5) * floatArray[int1 + 1] + this.get(9) * floatArray[int1 + 2] + this.get(13) * floatArray[int1 + 3];
			floatArray2[int1 + 2] = this.get(2) * floatArray[int1] + this.get(6) * floatArray[int1 + 1] + this.get(10) * floatArray[int1 + 2] + this.get(14) * floatArray[int1 + 3];
			floatArray2[int1 + 3] = this.get(3) * floatArray[int1] + this.get(7) * floatArray[int1 + 1] + this.get(11) * floatArray[int1 + 2] + this.get(15) * floatArray[int1 + 3];
		}

		this.put(floatArray2);
		return this;
	}

	public Matrix4 mult(Matrix4 matrix4) {
		float[] floatArray = new float[16];
		for (int int1 = 0; int1 < 16; int1 += 4) {
			floatArray[int1 + 0] = this.get(0) * matrix4.get(int1) + this.get(4) * matrix4.get(int1 + 1) + this.get(8) * matrix4.get(int1 + 2) + this.get(12) * matrix4.get(int1 + 3);
			floatArray[int1 + 1] = this.get(1) * matrix4.get(int1) + this.get(5) * matrix4.get(int1 + 1) + this.get(9) * matrix4.get(int1 + 2) + this.get(13) * matrix4.get(int1 + 3);
			floatArray[int1 + 2] = this.get(2) * matrix4.get(int1) + this.get(6) * matrix4.get(int1 + 1) + this.get(10) * matrix4.get(int1 + 2) + this.get(14) * matrix4.get(int1 + 3);
			floatArray[int1 + 3] = this.get(3) * matrix4.get(int1) + this.get(7) * matrix4.get(int1 + 1) + this.get(11) * matrix4.get(int1 + 2) + this.get(15) * matrix4.get(int1 + 3);
		}

		this.put(floatArray);
		return this;
	}

	public Matrix4 transpose() {
		float float1 = this.get(1);
		this.put(1, this.get(4));
		this.put(4, float1);
		float1 = this.get(2);
		this.put(2, this.get(8));
		this.put(8, float1);
		float1 = this.get(3);
		this.put(3, this.get(12));
		this.put(12, float1);
		float1 = this.get(7);
		this.put(7, this.get(13));
		this.put(13, float1);
		float1 = this.get(11);
		this.put(11, this.get(14));
		this.put(14, float1);
		float1 = this.get(6);
		this.put(6, this.get(9));
		this.put(9, float1);
		return this;
	}

	public Matrix4 translate(float float1, float float2, float float3) {
		float[] floatArray = new float[16];
		floatArray[0] = 1.0F;
		floatArray[5] = 1.0F;
		floatArray[10] = 1.0F;
		floatArray[15] = 1.0F;
		floatArray[12] = float1;
		floatArray[13] = float2;
		floatArray[14] = float3;
		return this.mult(floatArray);
	}

	public Matrix4 translate(Vector3 vector3) {
		return this.translate(vector3.x(), vector3.y(), vector3.z());
	}

	public Matrix4 scale(float float1, float float2, float float3) {
		float[] floatArray = new float[16];
		floatArray[0] = float1;
		floatArray[5] = float2;
		floatArray[10] = float3;
		floatArray[15] = 1.0F;
		return this.mult(floatArray);
	}

	public Matrix4 scale(Vector3 vector3) {
		return this.scale(vector3.x(), vector3.y(), vector3.z());
	}

	public Matrix4 rotate(float float1, float float2, float float3, float float4) {
		float float5 = (float)Math.cos((double)float1);
		float float6 = (float)Math.sin((double)float1);
		float float7 = 1.0F - float5;
		Vector3 vector3 = (new Vector3(float2, float3, float4)).normalize();
		float[] floatArray = new float[16];
		floatArray[0] = vector3.x() * vector3.x() + (1.0F - vector3.x() * vector3.x()) * float5;
		floatArray[4] = vector3.x() * vector3.y() * float7 - vector3.z() * float6;
		floatArray[8] = vector3.x() * vector3.z() * float7 + vector3.y() * float6;
		floatArray[1] = vector3.y() * vector3.x() * float7 + vector3.z() * float6;
		floatArray[5] = vector3.y() * vector3.y() + (1.0F - vector3.y() * vector3.y()) * float5;
		floatArray[9] = vector3.y() * vector3.z() * float7 - vector3.x() * float6;
		floatArray[2] = vector3.z() * vector3.x() * float7 - vector3.y() * float6;
		floatArray[6] = vector3.z() * vector3.y() * float7 + vector3.x() * float6;
		floatArray[10] = vector3.z() * vector3.z() + (1.0F - vector3.z() * vector3.z()) * float5;
		floatArray[15] = 1.0F;
		return this.mult(floatArray);
	}

	public Matrix4 rotate(float float1, Vector3 vector3) {
		return this.rotate(float1, vector3.x(), vector3.y(), vector3.z());
	}

	public FloatBuffer getBuffer() {
		if (this.direct == null) {
			this.direct = BufferUtils.createFloatBuffer(16);
		}

		this.direct.clear();
		this.direct.put((FloatBuffer)this.matrix.position(16).flip());
		this.direct.flip();
		return this.direct;
	}

	static  {
		Identity.clearToIdentity();
	}
}
