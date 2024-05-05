package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.math.PZMath;


public final class Keyframe {
	public Quaternion Rotation;
	public Vector3f Position;
	public Vector3f Scale = new Vector3f(1.0F, 1.0F, 1.0F);
	public int Bone;
	public String BoneName;
	public float Time = -1.0F;

	public Keyframe() {
	}

	public Keyframe(Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		this.Position = new Vector3f(vector3f);
		this.Rotation = new Quaternion(quaternion);
		this.Scale = new Vector3f(vector3f2);
	}

	public void set(Keyframe keyframe) {
		if (keyframe.Position != null) {
			this.setPosition(keyframe.Position);
		}

		if (keyframe.Rotation != null) {
			this.setRotation(keyframe.Rotation);
		}

		if (keyframe.Scale != null) {
			this.setScale(keyframe.Scale);
		}

		this.Time = keyframe.Time;
		this.Bone = keyframe.Bone;
		this.BoneName = keyframe.BoneName;
	}

	public void get(Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		setIfNotNull(vector3f, this.Position, 0.0F, 0.0F, 0.0F);
		setIfNotNull(quaternion, this.Rotation);
		setIfNotNull(vector3f2, this.Scale, 1.0F, 1.0F, 1.0F);
	}

	private void setScale(Vector3f vector3f) {
		if (this.Scale == null) {
			this.Scale = new Vector3f();
		}

		this.Scale.set(vector3f);
	}

	private void setRotation(Quaternion quaternion) {
		if (this.Rotation == null) {
			this.Rotation = new Quaternion();
		}

		this.Rotation.set(quaternion);
	}

	private void setPosition(Vector3f vector3f) {
		if (this.Position == null) {
			this.Position = new Vector3f();
		}

		this.Position.set(vector3f);
	}

	public void clear() {
		this.Time = -1.0F;
		this.Position = null;
		this.Rotation = null;
	}

	public void setIdentity() {
		setIdentity(this.Position, this.Rotation, this.Scale);
	}

	public static void setIdentity(Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		setIfNotNull(vector3f, 0.0F, 0.0F, 0.0F);
		setIdentityIfNotNull(quaternion);
		setIfNotNull(vector3f2, 1.0F, 1.0F, 1.0F);
	}

	public static Keyframe lerp(Keyframe keyframe, Keyframe keyframe2, float float1, Keyframe keyframe3) {
		lerp(keyframe, keyframe2, float1, keyframe3.Position, keyframe3.Rotation, keyframe3.Scale);
		keyframe3.Bone = keyframe2.Bone;
		keyframe3.BoneName = keyframe2.BoneName;
		keyframe3.Time = float1;
		return keyframe3;
	}

	public static void setIfNotNull(Vector3f vector3f, Vector3f vector3f2, float float1, float float2, float float3) {
		if (vector3f != null) {
			if (vector3f2 != null) {
				vector3f.set(vector3f2);
			} else {
				vector3f.set(float1, float2, float3);
			}
		}
	}

	public static void setIfNotNull(Vector3f vector3f, float float1, float float2, float float3) {
		if (vector3f != null) {
			vector3f.set(float1, float2, float3);
		}
	}

	public static void setIfNotNull(Quaternion quaternion, Quaternion quaternion2) {
		if (quaternion != null) {
			if (quaternion2 != null) {
				quaternion.set(quaternion2);
			} else {
				quaternion.setIdentity();
			}
		}
	}

	public static void setIdentityIfNotNull(Quaternion quaternion) {
		if (quaternion != null) {
			quaternion.setIdentity();
		}
	}

	public static void lerp(Keyframe keyframe, Keyframe keyframe2, float float1, Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		if (keyframe2.Time == keyframe.Time) {
			keyframe2.get(vector3f, quaternion, vector3f2);
		} else {
			float float2 = (float1 - keyframe.Time) / (keyframe2.Time - keyframe.Time);
			if (vector3f != null) {
				PZMath.lerp(vector3f, keyframe.Position, keyframe2.Position, float2);
			}

			if (quaternion != null) {
				PZMath.slerp(quaternion, keyframe.Rotation, keyframe2.Rotation, float2);
			}

			if (vector3f2 != null) {
				PZMath.lerp(vector3f2, keyframe.Scale, keyframe2.Scale, float2);
			}
		}
	}
}
