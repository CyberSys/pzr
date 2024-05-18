package zombie.iso.objects;

import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.network.GameClient;


public class IsoMolotovCocktail extends IsoPhysicsObject {
	private HandWeapon weapon = null;
	private IsoGameCharacter character = null;
	private int timer = 0;
	private int explodeTimer = 0;

	public String getObjectName() {
		return "MolotovCocktail";
	}

	public IsoMolotovCocktail(IsoCell cell) {
		super(cell);
	}

	public IsoMolotovCocktail(IsoCell cell, float float1, float float2, float float3, float float4, float float5, HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		super(cell);
		this.weapon = handWeapon;
		this.character = gameCharacter;
		this.explodeTimer = handWeapon.getTriggerExplosionTimer();
		this.velX = float4;
		this.velY = float5;
		float float6 = (float)Rand.Next(4000) / 10000.0F;
		float float7 = (float)Rand.Next(4000) / 10000.0F;
		float6 -= 0.2F;
		float7 -= 0.2F;
		this.velX += float6;
		this.velY += float7;
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.nx = float1;
		this.ny = float2;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.terminalVelocity = -0.02F;
		Texture texture = this.sprite.LoadFrameExplicit(handWeapon.getTex().getName());
		if (texture != null) {
			this.sprite.Animate = false;
			int int1 = Core.TileScale;
			this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * int1), (float)(16 * int1));
		}

		this.speedMod = 0.6F;
	}

	public void collideCharacter() {
		if (this.explodeTimer == 0) {
			this.Explode();
		}
	}

	public void collideGround() {
		if (this.explodeTimer == 0) {
			this.Explode();
		}
	}

	public void collideWall() {
		if (this.explodeTimer == 0) {
			this.Explode();
		}
	}

	public void update() {
		super.update();
		if (this.isCollidedThisFrame() && this.explodeTimer == 0) {
			this.Explode();
		}

		if (this.explodeTimer > 0) {
			++this.timer;
			if (this.timer >= this.explodeTimer) {
				this.Explode();
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		super.render(float1, float2, float3, colorInfo, boolean1);
		if (Core.bDebug) {
		}
	}

	void Explode() {
		this.setDestroyed(true);
		this.getCurrentSquare().getMovingObjects().remove(this);
		this.getCell().Remove(this);
		if (this.weapon.getExplosionTimer() <= 0 && this.weapon.getSensorRange() <= 0 && this.weapon.getRemoteControlID() == -1) {
			if (this.weapon.getExplosionRange() > 0) {
				this.square.drawCircleExplosion(this.weapon.getExplosionRange(), this.weapon, this.weapon.getSensorRange() > 0);
			}

			if (this.weapon.getFireRange() > 0) {
				this.square.drawCircleExplosion(this.weapon.getFireRange(), this.weapon, this.weapon.getSensorRange() > 0);
			}

			if (this.weapon.getSmokeRange() > 0) {
				this.square.drawCircleExplosion(this.weapon.getSmokeRange(), this.weapon, this.weapon.getSensorRange() > 0);
			}

			if (this.weapon.getNoiseRange() > 0) {
				this.square.drawCircleExplosion(0, (HandWeapon)this.weapon, this.weapon.getSensorRange() > 0);
			}
		} else {
			IsoTrap trap = new IsoTrap(this.weapon, this.getCurrentSquare().getCell(), this.getCurrentSquare());
			this.getCurrentSquare().AddTileObject(trap);
			if (GameClient.bClient) {
				this.square.syncIsoTrap(this.weapon, this.weapon.getSensorRange() > 0, false);
			}
		}
	}

	void Trigger() {
	}
}
