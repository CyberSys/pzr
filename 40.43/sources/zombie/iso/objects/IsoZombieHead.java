package zombie.iso.objects;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoMovingObject;
import zombie.iso.sprite.IsoSpriteInstance;


public class IsoZombieHead extends IsoMovingObject {
	public float tintb = 1.0F;
	public float tintg = 1.0F;
	public float tintr = 1.0F;
	public float time = 0.0F;

	public IsoZombieHead(IsoCell cell) {
		super(cell);
	}

	public boolean Serialize() {
		return false;
	}

	public String getObjectName() {
		return "ZombieHead";
	}

	public void update() {
		super.update();
		this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
		this.sx = this.sy = 0;
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
		if (this.targetAlpha[IsoPlayer.getPlayerIndex()] < 0.0F) {
			this.targetAlpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		}

		if (this.targetAlpha[IsoPlayer.getPlayerIndex()] > 1.0F) {
			this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
		}

		super.render(float1, float2, float3, colorInfo, boolean1);
	}

	public IsoZombieHead(IsoZombieHead.GibletType gibletType, IsoCell cell, float float1, float float2, float float3) {
		super(cell);
		this.solid = false;
		this.shootable = false;
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.nx = float1;
		this.ny = float2;
		this.alpha[IsoPlayer.getPlayerIndex()] = 0.5F;
		this.def = IsoSpriteInstance.get(this.sprite);
		this.def.alpha = 1.0F;
		this.sprite.def.alpha = 1.0F;
		this.offsetX = -26.0F;
		this.offsetY = -242.0F;
		switch (gibletType) {
		case A: 
			this.sprite.LoadFramesNoDirPageDirect("media/gibs/Giblet", "00", 3);
			break;
		
		case B: 
			this.sprite.LoadFramesNoDirPageDirect("media/gibs/Giblet", "01", 3);
		
		}
	}
	public static enum GibletType {

		A,
		B,
		Eye;
	}
}
