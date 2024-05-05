package zombie.characters;

import zombie.iso.Vector2;


public class NetworkCharacter {
	float minMovement;
	float maxMovement;
	long deltaTime;
	public final NetworkCharacter.Transform transform = new NetworkCharacter.Transform();
	final Vector2 movement = new Vector2();
	final NetworkCharacter.Point d1 = new NetworkCharacter.Point();
	final NetworkCharacter.Point d2 = new NetworkCharacter.Point();

	public NetworkCharacter() {
		this.minMovement = 0.075F;
		this.maxMovement = 0.5F;
		this.deltaTime = 10L;
	}

	NetworkCharacter(float float1, float float2, long long1) {
		this.minMovement = float1;
		this.maxMovement = float2;
		this.deltaTime = long1;
	}

	public void updateTransform(float float1, float float2, float float3, float float4) {
		this.transform.position.x = float1;
		this.transform.position.y = float2;
		this.transform.rotation.x = float3;
		this.transform.rotation.y = float4;
	}

	public void updateInterpolationPoint(int int1, float float1, float float2, float float3, float float4) {
		if (this.d2.t == 0) {
			this.updateTransform(float1, float2, float3, float4);
		}

		this.d2.t = int1;
		this.d2.px = float1;
		this.d2.py = float2;
		this.d2.rx = float3;
		this.d2.ry = float4;
	}

	public void updatePointInternal(float float1, float float2, float float3, float float4) {
		this.d1.px = float1;
		this.d1.py = float2;
		this.d1.rx = float3;
		this.d1.ry = float4;
	}

	public void updateExtrapolationPoint(int int1, float float1, float float2, float float3, float float4) {
		if (int1 > this.d1.t) {
			this.d2.t = this.d1.t;
			this.d2.px = this.d1.px;
			this.d2.py = this.d1.py;
			this.d2.rx = this.d1.rx;
			this.d2.ry = this.d1.ry;
			this.d1.t = int1;
			this.d1.px = float1;
			this.d1.py = float2;
			this.d1.rx = float3;
			this.d1.ry = float4;
		}
	}

	void extrapolate(int int1) {
		float float1 = (float)(int1 - this.d1.t) / (float)(this.d1.t - this.d2.t);
		float float2 = this.d1.px - this.d2.px;
		float float3 = this.d1.py - this.d2.py;
		this.movement.x = float2 * float1;
		this.movement.y = float3 * float1;
		if (float2 > this.minMovement || float3 > this.minMovement || -float2 > this.minMovement || -float3 > this.minMovement) {
			this.transform.moving = true;
			this.transform.rotation.x = this.movement.x;
			this.transform.rotation.y = this.movement.y;
			this.transform.rotation.normalize();
		}

		this.transform.position.x = this.d1.px + this.movement.x;
		this.transform.position.y = this.d1.py + this.movement.y;
		this.transform.operation = NetworkCharacter.Operation.EXTRAPOLATION;
	}

	void extrapolateInternal(int int1, float float1, float float2) {
		float float3 = (float)(int1 - this.d1.t) / (float)(int1 - this.d1.t);
		float float4 = float1 - this.d1.px;
		float float5 = float2 - this.d1.py;
		this.movement.x = float4 * float3;
		this.movement.y = float5 * float3;
		if (this.movement.getLength() > this.maxMovement) {
			this.movement.setLength(this.maxMovement);
		}

		if (float4 > this.minMovement || float5 > this.minMovement || -float4 > this.minMovement || -float5 > this.minMovement) {
			this.transform.moving = true;
			this.transform.rotation.x = this.movement.x;
			this.transform.rotation.y = this.movement.y;
			this.transform.rotation.normalize();
		}

		this.transform.position.x = float1 + this.movement.x;
		this.transform.position.y = float2 + this.movement.y;
		this.transform.operation = NetworkCharacter.Operation.EXTRAPOLATION;
	}

	void interpolate(int int1) {
		float float1 = (float)(int1 - this.d1.t) / (float)(this.d2.t - this.d1.t);
		float float2 = this.d2.px - this.d1.px;
		float float3 = this.d2.py - this.d1.py;
		this.movement.x = float2 * float1;
		this.movement.y = float3 * float1;
		if (this.movement.getLength() > this.maxMovement) {
			this.movement.setLength(this.maxMovement);
		}

		if (float2 > this.minMovement || float3 > this.minMovement || -float2 > this.minMovement || -float3 > this.minMovement) {
			this.transform.moving = true;
			this.transform.rotation.x = this.movement.x;
			this.transform.rotation.y = this.movement.y;
			this.transform.rotation.normalize();
		}

		this.transform.position.x = this.d1.px + this.movement.x;
		this.transform.position.y = this.d1.py + this.movement.y;
		this.transform.operation = NetworkCharacter.Operation.INTERPOLATION;
	}

	public NetworkCharacter.Transform predict(int int1, int int2, float float1, float float2, float float3, float float4) {
		this.transform.moving = false;
		this.transform.operation = NetworkCharacter.Operation.NONE;
		this.transform.time = int2 + int1;
		this.updateExtrapolationPoint(int2, float1, float2, float3, float4);
		if (this.d1.t != 0 && this.d2.t != 0) {
			this.extrapolate(int1 + int2);
		} else {
			this.updateTransform(float1, float2, float3, float4);
		}

		return this.transform;
	}

	public NetworkCharacter.Transform reconstruct(int int1, float float1, float float2, float float3, float float4) {
		this.transform.moving = false;
		this.transform.operation = NetworkCharacter.Operation.NONE;
		if (this.d2.t != 0) {
			if ((long)int1 + this.deltaTime <= (long)this.d2.t) {
				this.updatePointInternal(float1, float2, float3, float4);
				if (this.d1.t != 0 && this.d1.t != int1) {
					this.interpolate(int1);
				}

				this.d1.t = int1;
			} else if (int1 > this.d2.t && int1 < this.d2.t + 2000) {
				this.extrapolateInternal(int1, float1, float2);
				this.updatePointInternal(float1, float2, float3, float4);
				this.d1.t = int1;
			}
		}

		return this.transform;
	}

	public void checkReset(int int1) {
		if (int1 > 2000 + this.d2.t) {
			this.reset();
		}
	}

	public void checkResetPlayer(int int1) {
		if (int1 > 2000 + this.d1.t) {
			this.reset();
		}
	}

	public void reset() {
		this.d1.t = 0;
		this.d1.px = 0.0F;
		this.d1.py = 0.0F;
		this.d1.rx = 0.0F;
		this.d1.ry = 0.0F;
		this.d2.t = 0;
		this.d2.px = 0.0F;
		this.d2.py = 0.0F;
		this.d2.rx = 0.0F;
		this.d2.ry = 0.0F;
	}

	public static class Transform {
		public Vector2 position = new Vector2();
		public Vector2 rotation = new Vector2();
		public NetworkCharacter.Operation operation;
		public boolean moving;
		public int time;

		public Transform() {
			this.operation = NetworkCharacter.Operation.NONE;
			this.moving = false;
			this.time = 0;
		}
	}

	static class Point {
		public float px = 0.0F;
		public float py = 0.0F;
		public float rx = 0.0F;
		public float ry = 0.0F;
		public int t = 0;
	}

	public static enum Operation {

		INTERPOLATION,
		EXTRAPOLATION,
		NONE;

		private static NetworkCharacter.Operation[] $values() {
			return new NetworkCharacter.Operation[]{INTERPOLATION, EXTRAPOLATION, NONE};
		}
	}
}
