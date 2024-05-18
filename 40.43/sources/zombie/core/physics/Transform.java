package zombie.core.physics;

import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;


public class Transform {
	public final Matrix3f basis = new Matrix3f();
	public final Vector3f origin = new Vector3f();

	public Transform() {
	}

	public Transform(Matrix3f matrix3f) {
		this.basis.set((Matrix3fc)matrix3f);
	}

	public Transform(Matrix4f matrix4f) {
		this.set(matrix4f);
	}

	public Transform(Transform transform) {
		this.set(transform);
	}

	public void set(Transform transform) {
		this.basis.set((Matrix3fc)transform.basis);
		this.origin.set((Vector3fc)transform.origin);
	}

	public void set(Matrix3f matrix3f) {
		this.basis.set((Matrix3fc)matrix3f);
		this.origin.set(0.0F, 0.0F, 0.0F);
	}

	public void set(Matrix4f matrix4f) {
		matrix4f.get3x3(this.basis);
		matrix4f.getTranslation(this.origin);
	}

	public void transform(Vector3f vector3f) {
		this.basis.transform(vector3f);
		vector3f.add(this.origin);
	}

	public void setIdentity() {
		this.basis.identity();
		this.origin.set(0.0F, 0.0F, 0.0F);
	}

	public void inverse() {
		this.basis.transpose();
		this.origin.negate();
		this.basis.transform(this.origin);
	}

	public void inverse(Transform transform) {
		this.set(transform);
		this.inverse();
	}

	public Quaternionf getRotation(Quaternionf quaternionf) {
		this.basis.getUnnormalizedRotation(quaternionf);
		return quaternionf;
	}

	public void setRotation(Quaternionf quaternionf) {
		this.basis.set((Quaternionfc)quaternionf);
	}

	public Matrix4f getMatrix(Matrix4f matrix4f) {
		matrix4f.set((Matrix3fc)this.basis);
		matrix4f.setTranslation(this.origin);
		return matrix4f;
	}

	public boolean equals(Object object) {
		if (object != null && object instanceof Transform) {
			Transform transform = (Transform)object;
			return this.basis.equals(transform.basis) && this.origin.equals(transform.origin);
		} else {
			return false;
		}
	}

	public int hashCode() {
		byte byte1 = 3;
		int int1 = 41 * byte1 + this.basis.hashCode();
		int1 = 41 * int1 + this.origin.hashCode();
		return int1;
	}
}
