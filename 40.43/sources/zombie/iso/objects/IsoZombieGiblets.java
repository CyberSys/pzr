package zombie.iso.objects;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
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
		if (Rand.Next(Rand.AdjustForFramerate(8)) == 0 && this.getZ() > (float)((int)this.getZ()) && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
			this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)((int)this.z), Rand.Next(8));
		}

		if (Core.bLastStand && Rand.Next(Rand.AdjustForFramerate(10)) == 0 && this.getZ() > (float)((int)this.getZ()) && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
			this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)((int)this.z), Rand.Next(8));
		}

		super.update();
		this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
		if (this.velX == 0.0F && this.velY == 0.0F && this.getZ() == (float)((int)this.getZ())) {
			this.setCollidable(false);
			IsoWorld.instance.CurrentCell.getRemoveList().add(this);
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		this.targetAlpha[IsoPlayer.getPlayerIndex()] = this.sprite.def.targetAlpha = this.def.targetAlpha = 1.0F - this.time / 1.0F;
		if (this.targetAlpha[IsoPlayer.getPlayerIndex()] < 0.0F) {
			this.targetAlpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		}

		if (this.targetAlpha[IsoPlayer.getPlayerIndex()] > 1.0F) {
			this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
		}

		super.render(float1, float2, float3, colorInfo, boolean1);
		if (Core.bDebug) {
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
		this.alpha[IsoPlayer.getPlayerIndex()] = 0.5F;
		this.def = IsoSpriteInstance.get(this.sprite);
		this.def.alpha = 0.4F;
		this.sprite.def.alpha = 0.4F;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		switch (gibletType) {
		case A: 
			this.sprite.LoadFramesNoDirPage("Giblet", "00", 3);
			break;
		
		case B: 
			this.sprite.LoadFramesNoDirPage("Giblet", "01", 3);
			break;
		
		case Eye: 
			this.sprite.LoadFramesNoDirPage("Eyeball", "00", 1);
		
		}
	}
	public static enum GibletType {

		A,
		B,
		Eye;
	}
}
