package zombie.iso.objects;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;


public class IsoZombieGiblets extends IsoPhysicsObject {
	public float tintb = 1.0F;
	public float tintg = 1.0F;
	public float tintr = 1.0F;
	public float time = 0.0F;
	boolean invis = false;

	public IsoZombieGiblets(IsoCell cell) {
		super(cell);
	}

	public boolean Serialize() {
		return false;
	}

	public String getObjectName() {
		return "ZombieGiblets";
	}

	public void update() {
		if (Rand.Next(Rand.AdjustForFramerate(12)) == 0 && this.getZ() > (float)((int)this.getZ()) && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
			this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)((int)this.z), Rand.Next(8));
		}

		if (Core.bLastStand && Rand.Next(Rand.AdjustForFramerate(15)) == 0 && this.getZ() > (float)((int)this.getZ()) && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
			this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)((int)this.z), Rand.Next(8));
		}

		super.update();
		this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
		if (this.velX == 0.0F && this.velY == 0.0F && this.getZ() == (float)((int)this.getZ())) {
			this.setCollidable(false);
			IsoWorld.instance.CurrentCell.getRemoveList().add(this);
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (!this.invis) {
			float float4 = colorInfo.r;
			float float5 = colorInfo.g;
			float float6 = colorInfo.b;
			colorInfo.r = 0.5F;
			colorInfo.g = 0.5F;
			colorInfo.b = 0.5F;
			this.setTargetAlpha(this.sprite.def.targetAlpha = this.def.targetAlpha = 1.0F - this.time / 1.0F);
			super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
			if (Core.bDebug) {
			}

			colorInfo.r = float4;
			colorInfo.g = float5;
			colorInfo.b = float6;
		}
	}

	public IsoZombieGiblets(IsoZombieGiblets.GibletType gibletType, IsoCell cell, float float1, float float2, float float3, float float4, float float5) {
		super(cell);
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
		this.setAlpha(0.2F);
		this.def = IsoSpriteInstance.get(this.sprite);
		this.def.alpha = 0.2F;
		this.sprite.def.alpha = 0.4F;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		if (Rand.Next(3) != 0) {
			this.def.alpha = 0.0F;
			this.sprite.def.alpha = 0.0F;
			this.invis = true;
		}

		switch (gibletType) {
		case A: 
			this.sprite.setFromCache("Giblet", "00", 3);
			break;
		
		case B: 
			this.sprite.setFromCache("Giblet", "01", 3);
			break;
		
		case Eye: 
			this.sprite.setFromCache("Eyeball", "00", 1);
		
		}
	}

	public static enum GibletType {

		A,
		B,
		Eye;

		private static IsoZombieGiblets.GibletType[] $values() {
			return new IsoZombieGiblets.GibletType[]{A, B, Eye};
		}
	}
}
