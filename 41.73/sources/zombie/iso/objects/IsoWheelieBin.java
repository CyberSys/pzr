package zombie.iso.objects;

import zombie.characters.IsoPlayer;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoPushableObject;


public class IsoWheelieBin extends IsoPushableObject {
	float velx = 0.0F;
	float vely = 0.0F;

	public String getObjectName() {
		return "WheelieBin";
	}

	public IsoWheelieBin(IsoCell cell) {
		super(cell);
		this.container = new ItemContainer("wheeliebin", this.square, this);
		this.Collidable = true;
		this.solid = true;
		this.shootable = false;
		this.width = 0.3F;
		this.dir = IsoDirections.E;
		this.setAlphaAndTarget(0.0F);
		this.offsetX = -26.0F;
		this.offsetY = -248.0F;
		this.OutlineOnMouseover = true;
		this.sprite.LoadFramesPageSimple("TileObjectsExt_7", "TileObjectsExt_5", "TileObjectsExt_6", "TileObjectsExt_8");
	}

	public IsoWheelieBin(IsoCell cell, int int1, int int2, int int3) {
		super(cell, int1, int2, int3);
		this.x = (float)int1 + 0.5F;
		this.y = (float)int2 + 0.5F;
		this.z = (float)int3;
		this.nx = this.x;
		this.ny = this.y;
		this.offsetX = -26.0F;
		this.offsetY = -248.0F;
		this.weight = 6.0F;
		this.sprite.LoadFramesPageSimple("TileObjectsExt_7", "TileObjectsExt_5", "TileObjectsExt_6", "TileObjectsExt_8");
		this.square = this.getCell().getGridSquare(int1, int2, int3);
		this.current = this.getCell().getGridSquare(int1, int2, int3);
		this.container = new ItemContainer("wheeliebin", this.square, this);
		this.Collidable = true;
		this.solid = true;
		this.shootable = false;
		this.width = 0.3F;
		this.dir = IsoDirections.E;
		this.setAlphaAndTarget(0.0F);
		this.OutlineOnMouseover = true;
	}

	public void update() {
		this.velx = this.getX() - this.getLx();
		this.vely = this.getY() - this.getLy();
		float float1 = 1.0F - this.container.getContentsWeight() / 500.0F;
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		if (float1 < 0.7F) {
			float1 *= float1;
		}

		if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getDragObject() != this) {
			if (this.velx != 0.0F && this.vely == 0.0F && (this.dir == IsoDirections.E || this.dir == IsoDirections.W)) {
				this.setNx(this.getNx() + this.velx * 0.65F * float1);
			}

			if (this.vely != 0.0F && this.velx == 0.0F && (this.dir == IsoDirections.N || this.dir == IsoDirections.S)) {
				this.setNy(this.getNy() + this.vely * 0.65F * float1);
			}
		}

		super.update();
	}

	public float getWeight(float float1, float float2) {
		float float3 = this.container.getContentsWeight() / 500.0F;
		if (float3 < 0.0F) {
			float3 = 0.0F;
		}

		if (float3 > 1.0F) {
			return this.getWeight() * 8.0F;
		} else {
			float float4 = this.getWeight() * float3 + 1.5F;
			if (this.dir != IsoDirections.W && (this.dir != IsoDirections.E || float2 != 0.0F)) {
				return this.dir != IsoDirections.N && (this.dir != IsoDirections.S || float1 != 0.0F) ? float4 * 3.0F : float4 / 2.0F;
			} else {
				return float4 / 2.0F;
			}
		}
	}
}
