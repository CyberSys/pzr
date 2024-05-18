package zombie.iso;

import zombie.core.PerformanceSettings;
import zombie.network.GameServer;


public class IsoPhysicsObject extends IsoMovingObject {
	public float speedMod = 1.0F;
	public float velX = 0.0F;
	public float velY = 0.0F;
	public float velZ = 0.0F;
	public float terminalVelocity = -0.05F;

	public IsoPhysicsObject(IsoCell cell) {
		super(cell);
		this.solid = false;
		this.shootable = false;
	}

	public void collideGround() {
	}

	public void collideWall() {
	}

	public void update() {
		IsoGridSquare square = this.getCurrentSquare();
		super.update();
		if (this.isCollidedThisFrame()) {
			if (this.isCollidedN() || this.isCollidedS()) {
				this.velY = -this.velY;
				this.velY *= 0.5F;
				this.collideWall();
			}

			if (this.isCollidedE() || this.isCollidedW()) {
				this.velX = -this.velX;
				this.velX *= 0.5F;
				this.collideWall();
			}
		}

		int int1 = GameServer.bServer ? 10 : PerformanceSettings.LockFPS;
		float float1 = 30.0F / (float)int1;
		float float2 = 0.1F * this.speedMod * float1;
		float2 = 1.0F - float2;
		this.velX *= float2;
		this.velY *= float2;
		this.velZ -= 0.005F * float1;
		if (this.velZ < this.terminalVelocity) {
			this.velZ = this.terminalVelocity;
		}

		this.setNx(this.getNx() + this.velX * this.speedMod * 0.3F * float1);
		this.setNy(this.getNy() + this.velY * this.speedMod * 0.3F * float1);
		float float3 = this.getZ();
		this.setZ(this.getZ() + this.velZ * 0.4F * float1);
		if (this.getZ() < 0.0F) {
			this.setZ(0.0F);
			this.velZ = -this.velZ * 0.5F;
			this.collideGround();
		}

		if (this.getCurrentSquare() != null && (int)this.getZ() < (int)float3 && (square != null && square.TreatAsSolidFloor() || this.getCurrentSquare().TreatAsSolidFloor())) {
			this.setZ((float)((int)float3));
			this.velZ = -this.velZ * 0.5F;
			this.collideGround();
		}

		if (Math.abs(this.velX) < 1.0E-4F) {
			this.velX = 0.0F;
		}

		if (Math.abs(this.velY) < 1.0E-4F) {
			this.velY = 0.0F;
		}

		if (this.velX + this.velY == 0.0F) {
			this.sprite.Animate = false;
		}

		this.sx = this.sy = 0;
	}
}
