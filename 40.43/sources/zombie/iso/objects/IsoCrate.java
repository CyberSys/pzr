package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.bucket.BucketManager;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.ObjectTooltip;


public class IsoCrate extends IsoObject implements Thumpable {
	public boolean Barricaded = false;
	public Integer BarricideMaxStrength = 0;
	public Integer BarricideStrength = 0;
	public Integer Health = 10000;
	public boolean Locked = false;
	public Integer MaxHealth = 10000;
	public Integer PushedMaxStrength = 0;
	public Integer PushedStrength = 0;
	public IsoCrate.DoorType type;
	IsoSprite barricadeSprite;
	IsoSprite closedSprite;
	boolean open;
	IsoSprite openSprite;
	private boolean destroyed;

	public IsoCrate(IsoCell cell) {
		super(cell);
		this.type = IsoCrate.DoorType.WeakWooden;
		this.open = false;
		this.destroyed = false;
	}

	public String getObjectName() {
		return "Crate";
	}

	public IsoCrate(IsoCell cell, IsoGridSquare square, int int1) {
		this.type = IsoCrate.DoorType.WeakWooden;
		this.open = false;
		this.destroyed = false;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.sprite.getProperties().Set((IsoFlagType)IsoFlagType.solidtrans, (String)null);
		this.sprite.LoadFramesNoDirPageSimple("TileObjects2_0");
		this.square = square;
		this.container = new ItemContainer("playerCrate", this.square, this, 6, 6);
		this.DirtySlice();
		switch (this.type) {
		case WeakWooden: 
		
		default: 
		
		}
	}

	public void Barricade(IsoGameCharacter gameCharacter) {
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.putInt(this.container.ID);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.sprite.getProperties().Set((IsoFlagType)IsoFlagType.solidtrans, (String)null);
		this.sprite.LoadFramesNoDirPageSimple("TileObjects2_0");
		this.container = new ItemContainer(byteBuffer.getInt(), "playerCrate", this.square, this, 6, 6);
		this.container.load(byteBuffer, int1, false);
		switch (this.type) {
		case WeakWooden: 
		
		default: 
		
		}
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
		byte byte1 = 60;
		String string = "";
		switch (this.type) {
		case WeakWooden: 
			if (this.Locked) {
				string = "Crate";
			} else {
				string = "Crate";
			}

			break;
		
		case StrongWooden: 
			string = "Strong Wooden Door";
		
		}
		byte byte2 = 5;
		objectTooltip.DrawText(string, 5.0, (double)byte2, 1.0, 1.0, 0.800000011920929, 1.0);
		int int1 = byte2 + 20;
		int int2 = this.Health;
		int int3 = this.MaxHealth;
		int int4 = int2;
		int int5 = int3;
		if (this.IsStrengthenedByPushedItems()) {
			int2 += this.PushedMaxStrength;
			int3 += this.PushedMaxStrength;
			int2 /= 100;
			int3 /= 100;
			if (int4 < int5 && int4 > 0) {
				++int2;
			}

			objectTooltip.DrawText("Health:", 5.0, (double)int1, 1.0, 1.0, 0.800000011920929, 1.0);
		} else {
			int2 /= 100;
			int3 /= 100;
			if (int4 < int5 && int4 > 0) {
				++int2;
			}

			objectTooltip.DrawText("Health:", 5.0, (double)int1, 1.0, 1.0, 0.800000011920929, 1.0);
		}

		if ((double)int2 > (double)int3 * 0.75) {
			objectTooltip.DrawText(int2 + "/" + int3, (double)byte1, (double)int1, 0.30000001192092896, 1.0, 0.20000000298023224, 1.0);
		} else if ((double)int2 > (double)int3 * 0.33) {
			objectTooltip.DrawText(int2 + "/" + int3, (double)byte1, (double)int1, 0.800000011920929, 1.0, 0.20000000298023224, 1.0);
		} else {
			objectTooltip.DrawText(int2 + "/" + int3, (double)byte1, (double)int1, 0.800000011920929, 0.30000001192092896, 0.20000000298023224, 1.0);
		}

		int1 += 15;
		if (this.Barricaded) {
			if (!this.IsStrengthenedByPushedItems()) {
				objectTooltip.DrawText("Barricade:", 5.0, (double)int1, 1.0, 1.0, 0.800000011920929, 1.0);
			} else {
				objectTooltip.DrawText("Barricade:", 5.0, (double)int1, 1.0, 1.0, 0.800000011920929, 1.0);
			}

			if ((double)(this.BarricideStrength / 100) > (double)(this.BarricideMaxStrength / 100) * 0.75) {
				objectTooltip.DrawText(this.BarricideStrength / 100 + "/" + this.BarricideMaxStrength / 100, (double)byte1, (double)int1, 0.30000001192092896, 1.0, 0.20000000298023224, 1.0);
			} else if ((double)(this.BarricideStrength / 100) > (double)(this.BarricideMaxStrength / 100) * 0.33) {
				objectTooltip.DrawText(this.BarricideStrength / 100 + "/" + this.BarricideMaxStrength / 100, (double)byte1, (double)int1, 0.800000011920929, 1.0, 0.20000000298023224, 1.0);
			} else {
				objectTooltip.DrawText(this.BarricideStrength / 100 + "/" + this.BarricideMaxStrength / 100, (double)byte1, (double)int1, 0.800000011920929, 0.30000001192092896, 0.20000000298023224, 1.0);
			}
		}

		int1 += 19;
		objectTooltip.setHeight((double)int1);
	}

	public boolean HasTooltip() {
		return true;
	}

	public boolean isDestroyed() {
		return this.destroyed;
	}

	public boolean IsOpen() {
		return this.open;
	}

	public boolean IsStrengthenedByPushedItems() {
		IsoGridSquare square = this.square;
		IsoGridSquare square2 = null;
		square2 = square.getCell().getGridSquare(square.getX() - 1, square.getY(), square.getZ());
		return square2.getProperties().Is(IsoFlagType.pushable) || square.getProperties().Is(IsoFlagType.pushable);
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		return super.onMouseLeftClick(int1, int2);
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		if (square != this.square) {
			if (movingObject != null) {
				movingObject.collideWith(this);
			}

			return true;
		} else {
			return false;
		}
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		return IsoObject.VisionResult.Blocked;
	}

	public void Thump(IsoMovingObject movingObject) {
		if (!this.isDestroyed()) {
			if (movingObject instanceof IsoZombie) {
				if (movingObject.getCurrentSquare().getMovingObjects().size() >= 4) {
					this.Damage(1);
				}

				WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
			}

			if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
				((IsoGameCharacter)movingObject).getEmitter().playSound("breakdoor", this);
				WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
				movingObject.setThumpTarget((Thumpable)null);
				this.destroyed = true;
				this.square.getObjects().remove(this);
				this.square.getSpecialObjects().remove(this);
				byte byte1 = 1;
				for (int int1 = 0; int1 < byte1; ++int1) {
					this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
				}
			}
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (!this.isDestroyed()) {
			this.Damage(handWeapon.getDoorDamage());
			if (handWeapon.getDoorHitSound() != null) {
				gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound());
			}

			WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
			if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
				gameCharacter.getEmitter().playSound("breakdoor", this);
				WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
				this.destroyed = true;
				this.square.getObjects().remove(this);
				this.square.getSpecialObjects().remove(this);
			}
		}
	}

	public void ToggleDoor(IsoGameCharacter gameCharacter) {
	}

	public void ToggleDoorSilent() {
	}

	public void Unbarricade() {
	}

	void Damage(int int1) {
		if (this.Barricaded) {
			this.BarricideStrength = this.BarricideStrength - int1;
			if (this.BarricideStrength <= 0) {
				this.Unbarricade();
			}
		} else {
			this.Health = this.Health - int1;
		}
	}
	public static enum DoorType {

		WeakWooden,
		StrongWooden;
	}
}
