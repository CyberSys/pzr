package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.ObjectTooltip;


public class IsoWoodenWall extends IsoObject implements Thumpable {
	public int Barricaded = 0;
	public int BarricideMaxStrength = 0;
	public int BarricideStrength = 0;
	public int Health = 500;
	public boolean Locked = false;
	public int MaxHealth = 500;
	public int PushedMaxStrength = 0;
	public int PushedStrength = 0;
	public IsoWoodenWall.DoorType type;
	IsoSpriteInstance barricadeSprite;
	IsoSprite closedSprite;
	public boolean north;
	int gid;
	public boolean open;
	IsoSprite openSprite;
	private boolean destroyed;
	public boolean MetalBarricaded;
	KahluaTable table;

	public IsoWoodenWall(IsoCell cell) {
		super(cell);
		this.type = IsoWoodenWall.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.MetalBarricaded = false;
	}

	public String getObjectName() {
		return "WoodenWall";
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo) {
		super.render(float1, float2, float3, colorInfo, true);
	}

	public IsoWoodenWall(IsoCell cell, IsoGridSquare square, IsoSprite sprite, boolean boolean1) {
		this.type = IsoWoodenWall.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.MetalBarricaded = false;
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = sprite;
		this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)sprite, 2);
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case WeakWooden: 
			this.MaxHealth = this.Health = 500;
			break;
		
		case StrongWooden: 
			this.MaxHealth = this.Health = 800;
		
		}
	}

	public IsoWoodenWall(IsoCell cell, IsoGridSquare square, String string, boolean boolean1) {
		this.type = IsoWoodenWall.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.MetalBarricaded = false;
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 0);
		this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 2);
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case WeakWooden: 
			this.MaxHealth = this.Health = 500;
			break;
		
		case StrongWooden: 
			this.MaxHealth = this.Health = 800;
		
		}
	}

	public IsoWoodenWall(IsoCell cell, IsoGridSquare square, String string, boolean boolean1, KahluaTable kahluaTable) {
		this.type = IsoWoodenWall.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.MetalBarricaded = false;
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 0);
		this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 2);
		this.table = kahluaTable;
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case WeakWooden: 
			this.MaxHealth = this.Health = 500;
			break;
		
		case StrongWooden: 
			this.MaxHealth = this.Health = 800;
		
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.open = byteBuffer.get() == 1;
		this.Locked = byteBuffer.get() == 1;
		this.north = byteBuffer.get() == 1;
		this.Barricaded = byteBuffer.getInt();
		this.Health = byteBuffer.getInt();
		this.MaxHealth = byteBuffer.getInt();
		this.BarricideStrength = byteBuffer.getInt();
		if (int1 >= 49) {
			this.BarricideMaxStrength = byteBuffer.getShort();
		} else {
			this.BarricideMaxStrength = Math.max(this.BarricideStrength, this.Barricaded * 1000);
		}

		this.closedSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
		this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		if (int1 >= 87) {
			this.MetalBarricaded = byteBuffer.get() == 1;
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
		byteBuffer.put((byte)(this.north ? 1 : 0));
		byteBuffer.putInt(this.Barricaded);
		byteBuffer.putShort((short)this.BarricideMaxStrength);
		byteBuffer.putInt(this.Health);
		byteBuffer.putInt(this.MaxHealth);
		byteBuffer.putInt(this.BarricideStrength);
		byteBuffer.putInt(this.closedSprite.ID);
		byteBuffer.putInt(this.openSprite.ID);
		byteBuffer.put((byte)(this.MetalBarricaded ? 1 : 0));
	}

	public void Barricade(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			this.DirtySlice();
			this.square.InvalidateSpecialObjectPaths();
			if (this.Barricaded < 4) {
				if (this.open) {
					this.ToggleDoor(gameCharacter);
				}

				IsoGridSquare.setRecalcLightTime(-1);
				if (gameCharacter != null) {
					this.BarricideMaxStrength += (int)(1000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()) * gameCharacter.getBarricadeStrengthMod());
					this.BarricideStrength += (int)(1000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()) * gameCharacter.getBarricadeStrengthMod());
				} else {
					this.BarricideMaxStrength += (int)(1000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()));
					this.BarricideStrength += (int)(1000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()));
				}

				if (this.barricadeSprite != null && this.AttachedAnimSprite != null) {
					this.AttachedAnimSprite.remove(this.barricadeSprite);
				}

				Integer integer = 8;
				if (this.north) {
					integer = integer + 1;
				}

				integer = integer + this.Barricaded * 2;
				this.barricadeSprite = IsoSpriteInstance.get(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("TileBarricade_" + integer), 0));
				this.AttachedAnimSprite.add(this.barricadeSprite);
				++this.Barricaded;
			}
		}
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
		byte byte1 = 60;
		String string = "";
		switch (this.type) {
		case WeakWooden: 
			string = "Wooden Wall";
			break;
		
		case StrongWooden: 
			string = "Strong Wooden Wall";
		
		}
		if (this.Barricaded > 0) {
			string = "Barricaded Wall";
			if (this.IsStrengthenedByPushedItems()) {
				string = "Heavy Barricaded Wall";
			}
		} else if (this.IsStrengthenedByPushedItems()) {
			string = "Blocked Wall";
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
		if (this.Barricaded > 0) {
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
		return false;
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		return true;
	}

	public boolean TestPathfindCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		boolean boolean1 = this.north;
		if (this.Barricaded == 0) {
			return false;
		} else if (movingObject instanceof IsoSurvivor && ((IsoSurvivor)movingObject).getInventory().contains("Hammer")) {
			return false;
		} else {
			if (this.open) {
				boolean1 = !boolean1;
			}

			if (square == this.square) {
				if (boolean1 && square2.getY() < square.getY()) {
					return true;
				}

				if (!boolean1 && square2.getX() < square.getX()) {
					return true;
				}
			} else {
				if (boolean1 && square2.getY() > square.getY()) {
					return true;
				}

				if (!boolean1 && square2.getX() > square.getX()) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		boolean boolean1 = this.north;
		if (this.open) {
			boolean1 = !boolean1;
		}

		if (square == this.square) {
			if (boolean1 && square2.getY() < square.getY()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}

			if (!boolean1 && square2.getX() < square.getX()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}
		} else {
			if (boolean1 && square2.getY() > square.getY()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}

			if (!boolean1 && square2.getX() > square.getX()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}
		}

		return false;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		boolean boolean1 = this.north;
		if (this.open) {
			boolean1 = !boolean1;
		}

		if (square2.getZ() != square.getZ()) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			if (square == this.square) {
				if (boolean1 && square2.getY() < square.getY()) {
					return IsoObject.VisionResult.Blocked;
				}

				if (!boolean1 && square2.getX() < square.getX()) {
					return IsoObject.VisionResult.Blocked;
				}
			} else {
				if (boolean1 && square2.getY() > square.getY()) {
					return IsoObject.VisionResult.Blocked;
				}

				if (!boolean1 && square2.getX() > square.getX()) {
					return IsoObject.VisionResult.Blocked;
				}
			}

			return IsoObject.VisionResult.NoEffect;
		}
	}

	public void Thump(IsoMovingObject movingObject) {
		if (!this.isDestroyed()) {
			int int1;
			int int2;
			int int3;
			if (movingObject instanceof IsoZombie) {
				int1 = movingObject.getCurrentSquare().getMovingObjects().size();
				if (movingObject.getCurrentSquare().getW() != null) {
					int1 += movingObject.getCurrentSquare().getW().getMovingObjects().size();
				}

				if (movingObject.getCurrentSquare().getE() != null) {
					int1 += movingObject.getCurrentSquare().getE().getMovingObjects().size();
				}

				if (movingObject.getCurrentSquare().getS() != null) {
					int1 += movingObject.getCurrentSquare().getS().getMovingObjects().size();
				}

				if (movingObject.getCurrentSquare().getN() != null) {
					int1 += movingObject.getCurrentSquare().getN().getMovingObjects().size();
				}

				int2 = this.Barricaded > 0 ? 1 : 8;
				if (int1 >= int2 && !this.MetalBarricaded) {
					int3 = this.Barricaded;
					int int4 = ThumpState.getFastForwardDamageMultiplier();
					this.DirtySlice();
					this.Damage(1 * int4);
					if (GameServer.bServer && int3 > 0 && this.Barricaded <= 0) {
						GameServer.PlayWorldSoundServer("breakdoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
					}
				}

				WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
			}

			if (this.Health <= 0) {
				SoundManager.instance.PlayWorldSound("breakdoor", movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
				WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
				movingObject.setThumpTarget((Thumpable)null);
				this.destroyed = true;
				this.square.getObjects().remove(this);
				this.square.getSpecialObjects().remove(this);
				int1 = Rand.Next(2) + 1;
				for (int2 = 0; int2 < int1; ++int2) {
					this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
				}

				this.square.AddWorldInventoryItem("Base.Doorknob", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
				int2 = Rand.Next(3);
				for (int3 = 0; int3 < int2; ++int3) {
					this.square.AddWorldInventoryItem("Base.Hinge", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
				}
			}
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameClient.bClient) {
			if (gameCharacter instanceof IsoPlayer) {
				GameClient.instance.sendWeaponHit((IsoPlayer)gameCharacter, handWeapon, this);
			}
		} else if (!this.open) {
			if (!this.isDestroyed()) {
				int int1 = this.Barricaded;
				this.Damage(handWeapon.getDoorDamage());
				this.DirtySlice();
				if (handWeapon.getDoorHitSound() != null) {
					gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
					if (GameServer.bServer) {
						GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 0.2F, 20.0F, 1.0F, true);
					}
				}

				WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
				if (GameServer.bServer && int1 > 0 && this.Barricaded <= 0) {
					GameServer.PlayWorldSoundServer("breakdoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
				}

				if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
					gameCharacter.getEmitter().playSound("breakdoor", this);
					if (GameServer.bServer) {
						GameServer.PlayWorldSoundServer("breakdoor", false, this.square, 0.2F, 20.0F, 1.1F, true);
					}

					WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
					int1 = Rand.Next(2) + 1;
					int int2;
					for (int2 = 0; int2 < int1; ++int2) {
						this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
					}

					this.square.AddWorldInventoryItem("Base.Doorknob", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
					int2 = Rand.Next(3);
					for (int int3 = 0; int3 < int2; ++int3) {
						this.square.AddWorldInventoryItem("Base.Hinge", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
					}

					this.destroyed = true;
					this.square.getObjects().remove(this);
					this.square.getSpecialObjects().remove(this);
				}
			}
		}
	}

	public IsoGridSquare getOtherSideOfDoor(IsoGameCharacter gameCharacter) {
		if (this.north) {
			return gameCharacter.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
		} else {
			return gameCharacter.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
		}
	}

	public void ToggleDoorActual(IsoGameCharacter gameCharacter) {
		this.DirtySlice();
		this.square.InvalidateSpecialObjectPaths();
		if (gameCharacter instanceof IsoPlayer && !this.open) {
		}

		if (gameCharacter instanceof IsoSurvivor && gameCharacter.getInventory().contains("Hammer")) {
			if (this.Barricaded > 0) {
				this.Unbarricade(gameCharacter);
			}
		} else if (this.Barricaded > 0 || this.MetalBarricaded) {
			return;
		}

		if (this.Locked && gameCharacter != null && gameCharacter instanceof IsoPlayer && gameCharacter.getCurrentSquare().getRoom() == null && !this.open) {
			gameCharacter.getEmitter().playSound("DoorIsLocked", this);
			gameCharacter.Say("Gah, locked!");
			if (gameCharacter instanceof IsoSurvivor) {
				gameCharacter.getMasterBehaviorList().reset();
			}
		} else {
			if (gameCharacter instanceof IsoPlayer) {
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					LosUtil.cachecleared[int1] = true;
				}

				IsoGridSquare.setRecalcLightTime(-1);
			}

			this.open = !this.open;
			this.sprite = this.closedSprite;
			if (this.open) {
				gameCharacter.getEmitter().playSound("OpenDoor", this);
				this.sprite = this.openSprite;
			} else {
				gameCharacter.getEmitter().playSound("CloseDoor", this);
			}
		}
	}

	public void ToggleDoor(IsoGameCharacter gameCharacter) {
		this.ToggleDoorActual(gameCharacter);
	}

	public void ToggleDoorSilent() {
		if (this.Barricaded <= 0 && !this.MetalBarricaded) {
			this.square.InvalidateSpecialObjectPaths();
			LosUtil.cachecleared[IsoPlayer.getPlayerIndex()] = true;
			IsoGridSquare.setRecalcLightTime(-1);
			this.open = !this.open;
			this.sprite = this.closedSprite;
			if (this.open) {
				this.sprite = this.openSprite;
			}
		}
	}

	public void Unbarricade(IsoGameCharacter gameCharacter) {
		this.DirtySlice();
		float float1;
		if (this.MetalBarricaded && gameCharacter != null) {
			float1 = (float)this.BarricideStrength / (float)this.BarricideMaxStrength;
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.SheetMetal");
			inventoryItem.setCondition((int)((float)inventoryItem.getConditionMax() * float1));
			if (inventoryItem.getCondition() < 0) {
				inventoryItem.setCondition(0);
			}

			gameCharacter.getInventory().AddItem(inventoryItem);
		} else {
			if (this.Barricaded == 0) {
				return;
			}

			if (gameCharacter != null && this.BarricideMaxStrength > 0) {
				float1 = (float)this.BarricideStrength / (float)this.BarricideMaxStrength;
				for (int int1 = 0; int1 < this.Barricaded; ++int1) {
					InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem("Base.Plank");
					inventoryItem2.setCondition((int)((float)inventoryItem2.getConditionMax() * float1));
					if (inventoryItem2.getCondition() < 0) {
						inventoryItem2.setCondition(0);
					}

					gameCharacter.getInventory().AddItem(inventoryItem2);
				}
			}
		}

		this.square.InvalidateSpecialObjectPaths();
		if (gameCharacter != null) {
			gameCharacter.getEmitter().playSound("woodfall", this);
		}

		if (this.AttachedAnimSprite != null) {
			this.AttachedAnimSprite.clear();
		}

		this.Barricaded = 0;
		this.BarricideStrength = 0;
		this.BarricideMaxStrength = 0;
	}

	void Damage(int int1) {
		this.DirtySlice();
		if (this.Barricaded > 0) {
			this.BarricideStrength -= int1;
			if (this.BarricideStrength <= 0) {
				this.Unbarricade((IsoGameCharacter)null);
			}
		} else if (this.MetalBarricaded) {
			this.BarricideStrength -= int1;
			if (this.BarricideStrength <= this.BarricideMaxStrength / 2) {
				int int2 = 26;
				if (this.north) {
					++int2;
				}

				this.barricadeSprite = new IsoSpriteInstance(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + int2), 0));
				if (this.AttachedAnimSprite == null) {
					this.AttachedAnimSprite = new ArrayList(4);
					this.AttachedAnimSpriteActual = new ArrayList(4);
				}

				this.AttachedAnimSprite.add(this.barricadeSprite);
				this.AttachedAnimSpriteActual.add(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + int2), 0));
			}

			if (this.BarricideStrength <= 0) {
				this.Unbarricade((IsoGameCharacter)null);
			}
		} else {
			this.Health -= int1;
		}
	}

	public void MetalBarricade(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		this.DirtySlice();
		IsoGridSquare.setRecalcLightTime(-1);
		if (gameCharacter != null) {
			this.BarricideMaxStrength += (int)(5000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()) * gameCharacter.getBarricadeStrengthMod());
			this.BarricideStrength += (int)(5000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()) * gameCharacter.getBarricadeStrengthMod());
		} else {
			this.BarricideMaxStrength += (int)(5000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()));
			this.BarricideStrength += (int)(5000.0F * ((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax()));
		}

		if (this.barricadeSprite != null && this.AttachedAnimSprite != null) {
			this.AttachedAnimSprite.remove(this.barricadeSprite);
		}

		int int1 = 24;
		if (this.north) {
			++int1;
		}

		this.barricadeSprite = new IsoSpriteInstance(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + int1), 0));
		if (this.AttachedAnimSprite == null) {
			this.AttachedAnimSprite = new ArrayList(4);
			this.AttachedAnimSpriteActual = new ArrayList(4);
		}

		this.AttachedAnimSprite.add(this.barricadeSprite);
		this.AttachedAnimSpriteActual.add(IsoSprite.getSprite(IsoWorld.instance.spriteManager, (String)("constructedobjects_01_" + int1), 0));
		this.square.InvalidateSpecialObjectPaths();
		for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
			LosUtil.cachecleared[int2] = true;
		}

		IsoGridSquare.setRecalcLightTime(-1);
		GameTime.instance.lightSourceUpdate = 100.0F;
		this.MetalBarricaded = true;
		if (this.square != null) {
			this.square.RecalcProperties();
		}
	}

	public boolean isMetalBarricaded() {
		return this.MetalBarricaded;
	}
	public static enum DoorType {

		WeakWooden,
		StrongWooden;
	}
}
