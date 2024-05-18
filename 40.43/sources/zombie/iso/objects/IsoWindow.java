package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import zombie.AmbientStreamManager;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;


public class IsoWindow extends IsoObject implements BarricadeAble,Thumpable {
	public int Health = 75;
	public int MaxHealth = 75;
	public IsoWindow.WindowType type;
	IsoSprite closedSprite;
	IsoSprite smashedSprite;
	public boolean north;
	public boolean Locked;
	public boolean PermaLocked;
	public boolean open;
	IsoSprite openSprite;
	private boolean destroyed;
	private boolean glassRemoved;
	private IsoSprite glassRemovedSprite;
	public int OldNumPlanks;

	public IsoCurtain HasCurtains() {
		IsoGridSquare square = this.getInsideSquare();
		int int1;
		if (square != null && !square.getSpecialObjects().isEmpty()) {
			for (int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
				if (square.getSpecialObjects().get(int1) instanceof IsoCurtain) {
					return (IsoCurtain)square.getSpecialObjects().get(int1);
				}
			}
		}

		square = this.square;
		if (!square.getSpecialObjects().isEmpty()) {
			for (int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
				if (square.getSpecialObjects().get(int1) instanceof IsoCurtain) {
					return (IsoCurtain)square.getSpecialObjects().get(int1);
				}
			}
		}

		return null;
	}

	public IsoGridSquare getIndoorSquare() {
		if (this.square.getRoom() != null) {
			return this.square;
		} else {
			IsoGridSquare square;
			if (this.north) {
				square = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
				if (square != null && square.getRoom() != null) {
					return square;
				}
			} else {
				square = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
				if (square != null && square.getRoom() != null) {
					return square;
				}
			}

			return null;
		}
	}

	public IsoGridSquare getAddSheetSquare(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.getCurrentSquare() != null) {
			IsoGridSquare square = gameCharacter.getCurrentSquare();
			IsoGridSquare square2 = this.getSquare();
			if (this.north) {
				return square.getY() < square2.getY() ? this.getCell().getGridSquare(square2.x, square2.y - 1, square2.z) : square2;
			} else {
				return square.getX() < square2.getX() ? this.getCell().getGridSquare(square2.x - 1, square2.y, square2.z) : square2;
			}
		} else {
			return null;
		}
	}

	public void AttackObject(IsoGameCharacter gameCharacter) {
		super.AttackObject(gameCharacter);
		IsoObject object = this.square.getWall(this.north);
		if (object != null) {
			object.AttackObject(gameCharacter);
		}
	}

	public IsoGridSquare getInsideSquare() {
		IsoGridSquare square = this.square;
		return this.north ? this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
	}

	public IsoGridSquare getOppositeSquare() {
		return this.getInsideSquare();
	}

	public IsoWindow(IsoCell cell) {
		super(cell);
		this.type = IsoWindow.WindowType.SinglePane;
		this.north = false;
		this.Locked = false;
		this.PermaLocked = false;
		this.open = false;
		this.destroyed = false;
		this.glassRemoved = false;
	}

	public String getObjectName() {
		return "Window";
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameClient.bClient) {
			if (gameCharacter instanceof IsoPlayer) {
				GameClient.instance.sendWeaponHit((IsoPlayer)gameCharacter, handWeapon, this);
			}
		} else {
			IsoBarricade barricade = this.getBarricadeForCharacter(gameCharacter);
			if (barricade != null) {
				barricade.WeaponHit(gameCharacter, handWeapon);
			} else if (!this.isDestroyed() && !this.open) {
				if (handWeapon != ((IsoPlayer)gameCharacter).bareHands) {
					if (handWeapon != null) {
						this.Damage((float)(handWeapon.getDoorDamage() * 5));
					} else {
						this.Damage(100.0F);
					}

					this.DirtySlice();
					WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
					if (!this.isDestroyed() && this.Health <= 0) {
						this.smashWindow();
					}
				}
			} else {
				barricade = this.getBarricadeOppositeCharacter(gameCharacter);
				if (barricade != null) {
					barricade.WeaponHit(gameCharacter, handWeapon);
				}
			}
		}
	}

	public void smashWindow(boolean boolean1, boolean boolean2) {
		if (!this.destroyed) {
			if (GameClient.bClient && !boolean1) {
				GameClient.instance.smashWindow(this, 1);
				this.square.clientModify();
			}

			if (!boolean1) {
				if (GameServer.bServer) {
					GameServer.PlayWorldSoundServer("SmashWindow", false, this.square, 0.2F, 20.0F, 1.1F, true);
				} else {
					SoundManager.instance.PlayWorldSound("SmashWindow", this.square, 0.2F, 20.0F, 1.0F, true);
				}

				WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
			}

			this.destroyed = true;
			this.sprite = this.smashedSprite;
			if (boolean2) {
				this.handleAlarm();
			}

			if (GameServer.bServer && !boolean1) {
				GameServer.smashWindow(this, 1);
			}

			this.square.InvalidateSpecialObjectPaths();
			if (GameServer.bServer && !boolean1) {
				this.square.revisionUp();
			}
		}
	}

	public void smashWindow(boolean boolean1) {
		this.smashWindow(boolean1, true);
	}

	public void smashWindow() {
		this.smashWindow(false, true);
	}

	private void handleAlarm() {
		if (!GameClient.bClient) {
			IsoGridSquare square = this.getIndoorSquare();
			if (square != null) {
				IsoRoom room = square.getRoom();
				RoomDef roomDef = room.def;
				if (roomDef.building.bAlarmed && !GameClient.bClient) {
					AmbientStreamManager.instance.doAlarm(roomDef);
				}
			}
		}
	}

	public IsoWindow(IsoCell cell, IsoGridSquare square, IsoSprite sprite, boolean boolean1) {
		this.type = IsoWindow.WindowType.SinglePane;
		this.north = false;
		this.Locked = false;
		this.PermaLocked = false;
		this.open = false;
		this.destroyed = false;
		this.glassRemoved = false;
		sprite.getProperties().UnSet(IsoFlagType.cutN);
		sprite.getProperties().UnSet(IsoFlagType.cutW);
		int int1 = 0;
		if (sprite.getProperties().Is("OpenTileOffset")) {
			int1 = Integer.parseInt(sprite.getProperties().Val("OpenTileOffset"));
		}

		int int2 = 0;
		this.PermaLocked = sprite.getProperties().Is("WindowLocked");
		if (sprite.getProperties().Is("SmashedTileOffset")) {
			int2 = Integer.parseInt(sprite.getProperties().Val("SmashedTileOffset"));
		}

		this.closedSprite = sprite;
		if (boolean1) {
			this.closedSprite.getProperties().Set(IsoFlagType.cutN);
			this.closedSprite.getProperties().Set(IsoFlagType.windowN);
		} else {
			this.closedSprite.getProperties().Set(IsoFlagType.cutW);
			this.closedSprite.getProperties().Set(IsoFlagType.windowW);
		}

		this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, sprite, int1);
		this.smashedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, sprite, int2);
		if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
			int int3 = Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset"));
			this.glassRemovedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.closedSprite, int3);
		} else {
			this.glassRemovedSprite = this.smashedSprite;
		}

		if (this.smashedSprite != this.closedSprite && this.smashedSprite != null) {
			this.smashedSprite.AddProperties(this.closedSprite);
			this.smashedSprite.setType(this.closedSprite.getType());
		}

		if (this.openSprite != this.closedSprite && this.openSprite != null) {
			this.openSprite.AddProperties(this.closedSprite);
			this.openSprite.setType(this.closedSprite.getType());
		}

		if (this.glassRemovedSprite != this.closedSprite && this.glassRemovedSprite != null) {
			this.glassRemovedSprite.AddProperties(this.closedSprite);
			this.glassRemovedSprite.setType(this.closedSprite.getType());
		}

		this.sprite = this.closedSprite;
		IsoObject object = square.getWall(boolean1);
		if (object != null) {
			object.rerouteCollide = this;
			object.rerouteMask = this;
		}

		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case SinglePane: 
			this.MaxHealth = this.Health = 50;
			break;
		
		case DoublePane: 
			this.MaxHealth = this.Health = 150;
		
		}
		byte byte1 = 69;
		if (SandboxOptions.instance.LockedHouses.getValue() == 1) {
			byte1 = -1;
		} else if (SandboxOptions.instance.LockedHouses.getValue() == 2) {
			byte1 = 5;
		} else if (SandboxOptions.instance.LockedHouses.getValue() == 3) {
			byte1 = 10;
		} else if (SandboxOptions.instance.LockedHouses.getValue() == 4) {
			byte1 = 50;
		} else if (SandboxOptions.instance.LockedHouses.getValue() == 5) {
			byte1 = 60;
		} else if (SandboxOptions.instance.LockedHouses.getValue() == 6) {
			byte1 = 70;
		}

		if (byte1 > -1) {
			this.Locked = Rand.Next(100) < byte1;
		}
	}

	public boolean isDestroyed() {
		return this.destroyed;
	}

	public boolean IsOpen() {
		return this.open;
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (super.onMouseLeftClick(int1, int2)) {
			return true;
		} else {
			float float1 = IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY());
			if (Keyboard.isKeyDown(42)) {
				IsoCurtain curtain = this.HasCurtains();
				if (curtain != null && float1 < 2.0F && this.getZ() == IsoPlayer.getInstance().getZ() && !curtain.square.isBlockedTo(IsoPlayer.getInstance().getCurrentSquare())) {
					curtain.ToggleDoorSilent();
					return true;
				} else {
					return false;
				}
			} else if (float1 <= 1.0F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
				if (this.getBarricadeForCharacter(IsoPlayer.getInstance()) != null) {
					return true;
				} else {
					IsoPlayer.instance.StateMachineParams.clear();
					if (IsoPlayer.instance.timePressedContext >= 0.5F) {
						if (!this.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
							if ((this.open || this.Health <= 0) && !this.isBarricaded()) {
								IsoPlayer.instance.StateMachineParams.put(0, this);
								IsoPlayer.instance.changeState(ClimbThroughWindowState.instance());
							} else {
								IsoPlayer.instance.StateMachineParams.put(0, this);
								IsoPlayer.instance.getStateMachine().changeState(OpenWindowState.instance());
							}
						}
					} else if (!this.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
						if (this.Health > 0) {
							if (!this.open) {
								IsoPlayer.instance.StateMachineParams.put(0, this);
								IsoPlayer.instance.getStateMachine().changeState(OpenWindowState.instance());
							} else {
								this.ToggleWindow(IsoPlayer.instance);
							}
						} else if (!this.isBarricaded()) {
							IsoPlayer.instance.StateMachineParams.put(0, this);
							IsoPlayer.instance.changeState(ClimbThroughWindowState.instance());
						}
					}

					return true;
				}
			} else {
				return false;
			}
		}
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		if (square == this.square) {
			if (this.north && square2.getY() < square.getY()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}

			if (!this.north && square2.getX() < square.getX()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}
		} else {
			if (this.north && square2.getY() > square.getY()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}

			if (!this.north && square2.getX() > square.getX()) {
				if (movingObject != null) {
					movingObject.collideWith(this);
				}

				return true;
			}
		}

		return false;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		if (square2.getZ() != square.getZ()) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			if (square == this.square) {
				if (this.north && square2.getY() < square.getY()) {
					return IsoObject.VisionResult.Unblocked;
				}

				if (!this.north && square2.getX() < square.getX()) {
					return IsoObject.VisionResult.Unblocked;
				}
			} else {
				if (this.north && square2.getY() > square.getY()) {
					return IsoObject.VisionResult.Unblocked;
				}

				if (!this.north && square2.getX() > square.getX()) {
					return IsoObject.VisionResult.Unblocked;
				}
			}

			return IsoObject.VisionResult.NoEffect;
		}
	}

	public void Thump(IsoMovingObject movingObject) {
		if (movingObject instanceof IsoZombie) {
			IsoBarricade barricade = this.getBarricadeForCharacter((IsoZombie)movingObject);
			if (barricade != null) {
				barricade.Thump((IsoZombie)movingObject);
				return;
			}

			if (this.isDestroyed() || this.open) {
				barricade = this.getBarricadeOppositeCharacter((IsoZombie)movingObject);
				if (barricade != null) {
					barricade.Thump((IsoZombie)movingObject);
					return;
				}

				return;
			}

			if (((IsoZombie)movingObject).cognition == 1 && !this.canClimbThrough((IsoZombie)movingObject) && (this.sprite == null || !this.sprite.getProperties().Is(IsoFlagType.makeWindowInvincible)) && (!this.Locked || movingObject.getCurrentSquare() != null && !movingObject.getCurrentSquare().Is(IsoFlagType.exterior))) {
				this.ToggleWindow((IsoGameCharacter)movingObject);
				if (this.canClimbThrough((IsoZombie)movingObject)) {
					return;
				}
			}

			int int1 = ThumpState.getFastForwardDamageMultiplier();
			this.DirtySlice();
			this.Damage((float)(((IsoZombie)movingObject).strength * int1), true);
			WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		}

		if (!this.isDestroyed() && this.Health <= 0) {
			if (GameServer.bServer) {
				GameServer.smashWindow(this, 1);
				GameServer.PlayWorldSoundServer("SmashWindow", false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
			}

			((IsoGameCharacter)movingObject).getEmitter().playSound("SmashWindow", this);
			WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
			movingObject.setThumpTarget((Thumpable)null);
			this.destroyed = true;
			this.sprite = this.smashedSprite;
			this.square.InvalidateSpecialObjectPaths();
			if (GameServer.bServer) {
				this.square.revisionUp();
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.open = byteBuffer.get() == 1;
		this.north = byteBuffer.get() == 1;
		int int2;
		if (int1 >= 87) {
			this.Health = byteBuffer.getInt();
		} else {
			int2 = byteBuffer.getInt();
			this.Health = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			if (int1 >= 49) {
				short short1 = byteBuffer.getShort();
			} else {
				Math.max(int3, int2 * 1000);
			}

			this.OldNumPlanks = int2;
		}

		this.Locked = byteBuffer.get() == 1;
		this.PermaLocked = byteBuffer.get() == 1;
		this.destroyed = byteBuffer.get() == 1;
		if (int1 >= 64) {
			this.glassRemoved = byteBuffer.get() == 1;
			if (byteBuffer.get() == 1) {
				this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}

			if (byteBuffer.get() == 1) {
				this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}

			if (byteBuffer.get() == 1) {
				this.smashedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}

			if (byteBuffer.get() == 1) {
				this.glassRemovedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}
		} else {
			if (byteBuffer.getInt() == 1) {
				this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}

			if (byteBuffer.getInt() == 1) {
				this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}

			if (byteBuffer.getInt() == 1) {
				this.smashedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, byteBuffer.getInt());
			}

			if (this.closedSprite != null) {
				if (this.destroyed && this.closedSprite.getProperties().Is("SmashedTileOffset")) {
					int2 = Integer.parseInt(this.closedSprite.getProperties().Val("SmashedTileOffset"));
					this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.closedSprite, -int2);
				}

				if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
					int2 = Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset"));
					this.glassRemovedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.closedSprite, int2);
				}
			}

			if (this.glassRemovedSprite == null) {
				this.glassRemovedSprite = this.smashedSprite != null ? this.smashedSprite : this.closedSprite;
			}
		}

		this.MaxHealth = byteBuffer.getInt();
		if (this.closedSprite != null) {
			if (this.north) {
				this.closedSprite.getProperties().Set(IsoFlagType.cutN);
				this.closedSprite.getProperties().Set(IsoFlagType.windowN);
			} else {
				this.closedSprite.getProperties().Set(IsoFlagType.cutW);
				this.closedSprite.getProperties().Set(IsoFlagType.windowW);
			}

			if (this.smashedSprite != this.closedSprite && this.smashedSprite != null) {
				this.smashedSprite.AddProperties(this.closedSprite);
				this.smashedSprite.setType(this.closedSprite.getType());
			}

			if (this.openSprite != this.closedSprite && this.openSprite != null) {
				this.openSprite.AddProperties(this.closedSprite);
				this.openSprite.setType(this.closedSprite.getType());
			}

			if (this.glassRemovedSprite != this.closedSprite && this.glassRemovedSprite != null) {
				this.glassRemovedSprite.AddProperties(this.closedSprite);
				this.glassRemovedSprite.setType(this.closedSprite.getType());
			}
		}

		if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequestLoad(this.square);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.north ? 1 : 0));
		byteBuffer.putInt(this.Health);
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
		byteBuffer.put((byte)(this.PermaLocked ? 1 : 0));
		byteBuffer.put((byte)(this.destroyed ? 1 : 0));
		byteBuffer.put((byte)(this.glassRemoved ? 1 : 0));
		if (this.openSprite != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.openSprite.ID);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.closedSprite != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.closedSprite.ID);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.smashedSprite != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.smashedSprite.ID);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.glassRemovedSprite != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.glassRemovedSprite.ID);
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.putInt(this.MaxHealth);
	}

	public void saveState(ByteBuffer byteBuffer) {
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
	}

	public void loadState(ByteBuffer byteBuffer) {
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1 != this.Locked) {
			this.Locked = boolean1;
		}
	}

	public void openCloseCurtain(IsoGameCharacter gameCharacter) {
		if (gameCharacter == IsoPlayer.instance) {
			IsoGridSquare square = null;
			Object object = null;
			IsoDirections directions = IsoDirections.N;
			IsoGridSquare square2;
			if (this.north) {
				square2 = this.square;
				directions = IsoDirections.N;
				if (square2.getRoom() == null) {
					square2 = this.getCell().getGridSquare(square2.getX(), square2.getY() - 1, square2.getZ());
					directions = IsoDirections.S;
				}

				object = square;
				square = square2;
			} else {
				square2 = this.square;
				directions = IsoDirections.W;
				if (square2.getRoom() == null) {
					square2 = this.getCell().getGridSquare(square2.getX() - 1, square2.getY(), square2.getZ());
					directions = IsoDirections.E;
				}

				object = square;
				square = square2;
			}

			int int1;
			if (square != null) {
				for (int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
					if (square.getSpecialObjects().get(int1) instanceof IsoCurtain) {
						((IsoCurtain)square.getSpecialObjects().get(int1)).ToggleDoorSilent();
						return;
					}
				}
			}

			if (object != null) {
				for (int1 = 0; int1 < ((IsoGridSquare)object).getSpecialObjects().size(); ++int1) {
					if (((IsoGridSquare)object).getSpecialObjects().get(int1) instanceof IsoCurtain) {
						((IsoCurtain)((IsoGridSquare)object).getSpecialObjects().get(int1)).ToggleDoorSilent();
						return;
					}
				}
			}
		}
	}

	public void removeSheet(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = null;
		IsoDirections directions = IsoDirections.N;
		IsoGridSquare square2;
		if (this.north) {
			square2 = this.square;
			directions = IsoDirections.N;
			if (square2.getRoom() == null) {
				square2 = this.getCell().getGridSquare(square2.getX(), square2.getY() - 1, square2.getZ());
				directions = IsoDirections.S;
			}

			square = square2;
		} else {
			square2 = this.square;
			directions = IsoDirections.W;
			if (square2.getRoom() == null) {
				square2 = this.getCell().getGridSquare(square2.getX() - 1, square2.getY(), square2.getZ());
				directions = IsoDirections.E;
			}

			square = square2;
		}

		for (int int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
			IsoObject object = (IsoObject)square.getSpecialObjects().get(int1);
			if (object instanceof IsoCurtain) {
				square.transmitRemoveItemFromSquare(object);
				if (gameCharacter != null) {
					if (GameServer.bServer) {
						gameCharacter.sendObjectChange("addItemOfType", new Object[]{"type", object.getName()});
					} else {
						gameCharacter.getInventory().AddItem(object.getName());
					}
				}

				break;
			}
		}
	}

	public void addSheet(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = null;
		IsoDirections directions = IsoDirections.N;
		IsoGridSquare square2;
		if (this.north) {
			square2 = this.square;
			directions = IsoDirections.N;
			if (gameCharacter != null) {
				if (gameCharacter.getY() < this.getY()) {
					square2 = this.getCell().getGridSquare(square2.getX(), square2.getY() - 1, square2.getZ());
					directions = IsoDirections.S;
				}
			} else if (square2.getRoom() == null) {
				square2 = this.getCell().getGridSquare(square2.getX(), square2.getY() - 1, square2.getZ());
				directions = IsoDirections.S;
			}

			square = square2;
		} else {
			square2 = this.square;
			directions = IsoDirections.W;
			if (gameCharacter != null) {
				if (gameCharacter.getX() < this.getX()) {
					square2 = this.getCell().getGridSquare(square2.getX() - 1, square2.getY(), square2.getZ());
					directions = IsoDirections.E;
				}
			} else if (square2.getRoom() == null) {
				square2 = this.getCell().getGridSquare(square2.getX() - 1, square2.getY(), square2.getZ());
				directions = IsoDirections.E;
			}

			square = square2;
		}

		boolean boolean1 = true;
		int int1;
		for (int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
			if (square.getSpecialObjects().get(int1) instanceof IsoCurtain) {
				boolean1 = false;
			}
		}

		if (square != null && boolean1) {
			int1 = 16;
			if (directions == IsoDirections.E) {
				++int1;
			}

			if (directions == IsoDirections.S) {
				int1 += 3;
			}

			if (directions == IsoDirections.N) {
				int1 += 2;
			}

			int1 += 4;
			IsoCurtain curtain = new IsoCurtain(this.getCell(), square, "fixtures_windows_curtains_01_" + int1, this.north);
			square.AddSpecialTileObject(curtain);
			if (!curtain.open) {
				curtain.ToggleDoorSilent();
			}

			if (GameServer.bServer) {
				curtain.transmitCompleteItemToClients();
				if (gameCharacter != null) {
					gameCharacter.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
				}
			} else if (gameCharacter != null) {
				gameCharacter.getInventory().RemoveOneOf("Sheet");
			}
		}
	}

	public void ToggleWindow(IsoGameCharacter gameCharacter) {
		this.DirtySlice();
		IsoGridSquare.setRecalcLightTime(-1);
		if (!this.PermaLocked) {
			if (!this.destroyed) {
				if (gameCharacter == null || this.getBarricadeForCharacter(gameCharacter) == null) {
					this.Locked = false;
					this.open = !this.open;
					this.sprite = this.closedSprite;
					this.square.InvalidateSpecialObjectPaths();
					if (this.open) {
						this.handleAlarm();
						this.sprite = this.openSprite;
					} else {
						gameCharacter.getEmitter().playSound("CloseWindow");
					}

					this.square.RecalcProperties();
					this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
					LuaEventManager.triggerEvent("OnContainerUpdate");
				}
			}
		}
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)(this.open ? 1 : 0));
		byteBufferWriter.putByte((byte)(this.destroyed ? 1 : 0));
		byteBufferWriter.putByte((byte)(this.Locked ? 1 : 0));
		byteBufferWriter.putByte((byte)(this.PermaLocked ? 1 : 0));
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		if (this.square == null) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
		} else if (this.getObjectIndex() == -1) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
				this.square.clientModify();
			} else if (GameServer.bServer && !boolean1) {
				Iterator iterator = GameServer.udpEngine.connections.iterator();
				while (iterator.hasNext()) {
					UdpConnection udpConnection2 = (UdpConnection)iterator.next();
					ByteBufferWriter byteBufferWriter2 = udpConnection2.startPacket();
					PacketTypes.doPacket((short)12, byteBufferWriter2);
					this.syncIsoObjectSend(byteBufferWriter2);
					udpConnection2.endPacketImmediate();
				}
			} else if (boolean1) {
				boolean boolean2 = byteBuffer.get() == 1;
				boolean boolean3 = byteBuffer.get() == 1;
				boolean boolean4 = byteBuffer.get() == 1;
				if (byte1 == 1) {
					this.open = true;
					this.sprite = this.openSprite;
				} else if (byte1 == 0) {
					this.open = false;
					this.sprite = this.closedSprite;
				}

				if (boolean2) {
					this.destroyed = true;
					this.sprite = this.smashedSprite;
				}

				this.Locked = boolean3;
				this.PermaLocked = boolean4;
				if (GameServer.bServer) {
					Iterator iterator2 = GameServer.udpEngine.connections.iterator();
					while (iterator2.hasNext()) {
						UdpConnection udpConnection3 = (UdpConnection)iterator2.next();
						if (udpConnection != null && udpConnection3.getConnectedGUID() != udpConnection.getConnectedGUID()) {
							ByteBufferWriter byteBufferWriter3 = udpConnection3.startPacket();
							PacketTypes.doPacket((short)12, byteBufferWriter3);
							this.syncIsoObjectSend(byteBufferWriter3);
							udpConnection3.endPacketImmediate();
						}
					}

					this.square.revisionUp();
				}

				this.square.RecalcProperties();
				LuaEventManager.triggerEvent("OnContainerUpdate");
			}
		}
	}

	public static boolean isTopOfSheetRopeHere(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			return square.Is(IsoFlagType.climbSheetTopN) || square.Is(IsoFlagType.climbSheetTopS) || square.Is(IsoFlagType.climbSheetTopW) || square.Is(IsoFlagType.climbSheetTopE);
		}
	}

	public static boolean isTopOfSheetRopeHere(IsoGridSquare square, boolean boolean1) {
		if (square == null) {
			return false;
		} else {
			if (boolean1) {
				if (square.Is(IsoFlagType.climbSheetTopN)) {
					return true;
				}

				if (square.nav[IsoDirections.N.index()] != null && square.nav[IsoDirections.N.index()].Is(IsoFlagType.climbSheetTopS)) {
					return true;
				}
			} else {
				if (square.Is(IsoFlagType.climbSheetTopW)) {
					return true;
				}

				if (square.nav[IsoDirections.W.index()] != null && square.nav[IsoDirections.W.index()].Is(IsoFlagType.climbSheetTopE)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean haveSheetRope() {
		return isTopOfSheetRopeHere(this.square, this.north);
	}

	public static boolean isSheetRopeHere(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			return square.Is(IsoFlagType.climbSheetTopW) || square.Is(IsoFlagType.climbSheetTopN) || square.Is(IsoFlagType.climbSheetTopE) || square.Is(IsoFlagType.climbSheetTopS) || square.Is(IsoFlagType.climbSheetW) || square.Is(IsoFlagType.climbSheetN) || square.Is(IsoFlagType.climbSheetE) || square.Is(IsoFlagType.climbSheetS);
		}
	}

	public static boolean canClimbHere(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else if (square.getProperties().Is(IsoFlagType.solid)) {
			return false;
		} else if (!square.Has(IsoObjectType.stairsBN) && !square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsTN)) {
			return !square.Has(IsoObjectType.stairsBW) && !square.Has(IsoObjectType.stairsMW) && !square.Has(IsoObjectType.stairsTW);
		} else {
			return false;
		}
	}

	public static int countAddSheetRope(IsoGridSquare square, boolean boolean1) {
		if (isTopOfSheetRopeHere(square, boolean1)) {
			return 0;
		} else {
			IsoCell cell = IsoWorld.instance.CurrentCell;
			if (square.TreatAsSolidFloor()) {
				IsoGridSquare square2;
				if (boolean1) {
					square2 = cell.getOrCreateGridSquare((double)square.getX(), (double)(square.getY() - 1), (double)square.getZ());
					if (square2 == null || square2.TreatAsSolidFloor() || isSheetRopeHere(square2) || !canClimbHere(square2)) {
						return 0;
					}

					square = square2;
				} else {
					square2 = cell.getOrCreateGridSquare((double)(square.getX() - 1), (double)square.getY(), (double)square.getZ());
					if (square2 == null || square2.TreatAsSolidFloor() || isSheetRopeHere(square2) || !canClimbHere(square2)) {
						return 0;
					}

					square = square2;
				}
			}

			for (int int1 = 1; square != null; ++int1) {
				if (!canClimbHere(square)) {
					return 0;
				}

				if (square.TreatAsSolidFloor()) {
					return int1;
				}

				if (square.getZ() == 0) {
					return int1;
				}

				square = cell.getOrCreateGridSquare((double)square.getX(), (double)square.getY(), (double)(square.getZ() - 1));
			}

			return 0;
		}
	}

	public int countAddSheetRope() {
		return countAddSheetRope(this.square, this.north);
	}

	public static boolean canAddSheetRope(IsoGridSquare square, boolean boolean1) {
		return countAddSheetRope(square, boolean1) != 0;
	}

	public boolean canAddSheetRope() {
		return !this.canClimbThrough((IsoGameCharacter)null) ? false : canAddSheetRope(this.square, this.north);
	}

	public boolean addSheetRope(IsoPlayer player, String string) {
		return !this.canAddSheetRope() ? false : addSheetRope(player, this.square, this.north, string);
	}

	public static boolean addSheetRope(IsoPlayer player, IsoGridSquare square, boolean boolean1, String string) {
		boolean boolean2 = false;
		int int1 = 0;
		byte byte1 = 0;
		if (boolean1) {
			byte1 = 1;
		}

		boolean boolean3 = false;
		boolean boolean4 = false;
		IsoGridSquare square2 = null;
		IsoGridSquare square3 = null;
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (square.TreatAsSolidFloor()) {
			if (!boolean1) {
				square2 = cell.getGridSquare(square.getX() - 1, square.getY(), square.getZ());
				if (square2 != null) {
					boolean4 = true;
					byte1 = 3;
				}
			} else {
				square3 = cell.getGridSquare(square.getX(), square.getY() - 1, square.getZ());
				if (square3 != null) {
					boolean3 = true;
					byte1 = 4;
				}
			}
		}

		if (square.getProperties().Is(IsoFlagType.solidfloor)) {
		}

		while (square != null && (GameServer.bServer || player.getInventory().contains(string))) {
			String string2 = "crafted_01_" + byte1;
			if (int1 > 0) {
				if (boolean4) {
					string2 = "crafted_01_10";
				} else if (boolean3) {
					string2 = "crafted_01_13";
				} else {
					string2 = "crafted_01_" + (byte1 + 8);
				}
			}

			IsoObject object = new IsoObject(cell, square, string2);
			object.setName(string);
			object.sheetRope = true;
			square.getObjects().add(object);
			object.transmitCompleteItemToClients();
			square.haveSheetRope = true;
			if (boolean3 && int1 == 0) {
				square = square3;
				object = new IsoObject(cell, square3, "crafted_01_5");
				object.setName(string);
				object.sheetRope = true;
				square3.getObjects().add(object);
				object.transmitCompleteItemToClients();
			}

			if (boolean4 && int1 == 0) {
				square = square2;
				object = new IsoObject(cell, square2, "crafted_01_2");
				object.setName(string);
				object.sheetRope = true;
				square2.getObjects().add(object);
				object.transmitCompleteItemToClients();
			}

			square.RecalcProperties();
			square.getProperties().UnSet(IsoFlagType.solidtrans);
			if (GameServer.bServer) {
				if (int1 == 0) {
					player.sendObjectChange("removeOneOf", new Object[]{"type", "Nails"});
				}

				player.sendObjectChange("removeOneOf", new Object[]{"type", string});
			} else {
				if (int1 == 0) {
					player.getInventory().RemoveOneOf("Nails");
				}

				player.getInventory().RemoveOneOf(string);
			}

			++int1;
			if (boolean2) {
				break;
			}

			square = cell.getOrCreateGridSquare((double)square.getX(), (double)square.getY(), (double)(square.getZ() - 1));
			if (square != null && square.TreatAsSolidFloor()) {
				boolean2 = true;
			}
		}

		return true;
	}

	public boolean removeSheetRope(IsoPlayer player) {
		return !this.haveSheetRope() ? false : removeSheetRope(player, this.square, this.north);
	}

	public static boolean removeSheetRope(IsoPlayer player, IsoGridSquare square, boolean boolean1) {
		if (square == null) {
			return false;
		} else {
			IsoGridSquare square2 = square;
			square.haveSheetRope = false;
			IsoFlagType flagType;
			IsoFlagType flagType2;
			String string;
			int int1;
			IsoObject object;
			if (boolean1) {
				if (square.Is(IsoFlagType.climbSheetTopN)) {
					flagType = IsoFlagType.climbSheetTopN;
					flagType2 = IsoFlagType.climbSheetN;
				} else {
					if (square.nav[IsoDirections.N.index()] == null || !square.nav[IsoDirections.N.index()].Is(IsoFlagType.climbSheetTopS)) {
						return false;
					}

					flagType = IsoFlagType.climbSheetTopS;
					flagType2 = IsoFlagType.climbSheetS;
					string = "crafted_01_4";
					for (int1 = 0; int1 < square2.getObjects().size(); ++int1) {
						object = (IsoObject)square2.getObjects().get(int1);
						if (object.sprite != null && object.sprite.getName() != null && object.sprite.getName().equals(string)) {
							square2.transmitRemoveItemFromSquare(object);
							break;
						}
					}

					square2 = square.nav[IsoDirections.N.index()];
				}
			} else if (square.Is(IsoFlagType.climbSheetTopW)) {
				flagType = IsoFlagType.climbSheetTopW;
				flagType2 = IsoFlagType.climbSheetW;
			} else {
				if (square.nav[IsoDirections.W.index()] == null || !square.nav[IsoDirections.W.index()].Is(IsoFlagType.climbSheetTopE)) {
					return false;
				}

				flagType = IsoFlagType.climbSheetTopE;
				flagType2 = IsoFlagType.climbSheetE;
				string = "crafted_01_3";
				for (int1 = 0; int1 < square2.getObjects().size(); ++int1) {
					object = (IsoObject)square2.getObjects().get(int1);
					if (object.sprite != null && object.sprite.getName() != null && object.sprite.getName().equals(string)) {
						square2.transmitRemoveItemFromSquare(object);
						break;
					}
				}

				square2 = square.nav[IsoDirections.W.index()];
			}

			while (square2 != null) {
				boolean boolean2 = false;
				for (int int2 = 0; int2 < square2.getObjects().size(); ++int2) {
					IsoObject object2 = (IsoObject)square2.getObjects().get(int2);
					if (object2.getProperties() != null && (object2.getProperties().Is(flagType) || object2.getProperties().Is(flagType2))) {
						square2.transmitRemoveItemFromSquare(object2);
						if (GameServer.bServer) {
							if (player != null) {
								player.sendObjectChange("addItemOfType", new Object[]{"type", object2.getName()});
							}
						} else if (player != null) {
							player.getInventory().AddItem(object2.getName());
						}

						boolean2 = true;
						break;
					}
				}

				if (!boolean2 || square2.getZ() == 0) {
					break;
				}

				square2 = square2.getCell().getGridSquare(square2.getX(), square2.getY(), square2.getZ() - 1);
			}

			return true;
		}
	}

	public void Damage(float float1) {
		this.Damage(float1, false);
	}

	public void Damage(float float1, boolean boolean1) {
		if (!this.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
			this.DirtySlice();
			this.Health = (int)((float)this.Health - float1);
			if (this.Health < 0) {
				this.Health = 0;
			}

			if (!this.isDestroyed() && this.Health == 0) {
				this.smashWindow(false, !boolean1 || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue());
			}
		}
	}

	public boolean isLocked() {
		return this.Locked;
	}

	public boolean isSmashed() {
		return this.destroyed;
	}

	public IsoBarricade getBarricadeOnSameSquare() {
		return IsoBarricade.GetBarricadeOnSquare(this.square, this.north ? IsoDirections.N : IsoDirections.W);
	}

	public IsoBarricade getBarricadeOnOppositeSquare() {
		return IsoBarricade.GetBarricadeOnSquare(this.getOppositeSquare(), this.north ? IsoDirections.S : IsoDirections.E);
	}

	public boolean isBarricaded() {
		IsoBarricade barricade = this.getBarricadeOnSameSquare();
		if (barricade == null) {
			barricade = this.getBarricadeOnOppositeSquare();
		}

		return barricade != null;
	}

	public IsoBarricade getBarricadeForCharacter(IsoGameCharacter gameCharacter) {
		return IsoBarricade.GetBarricadeForCharacter(this, gameCharacter);
	}

	public IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter gameCharacter) {
		return IsoBarricade.GetBarricadeOppositeCharacter(this, gameCharacter);
	}

	public boolean getNorth() {
		return this.north;
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else {
			return this.north ? vector2.set(this.getX() + 0.5F, this.getY()) : vector2.set(this.getX(), this.getY() + 0.5F);
		}
	}

	public void setIsLocked(boolean boolean1) {
		this.Locked = boolean1;
	}

	public IsoSprite getOpenSprite() {
		return this.openSprite;
	}

	public void setOpenSprite(IsoSprite sprite) {
		this.openSprite = sprite;
	}

	public void setSmashed(boolean boolean1) {
		if (boolean1) {
			this.destroyed = true;
			this.sprite = this.smashedSprite;
		} else {
			this.destroyed = false;
			this.sprite = this.open ? this.openSprite : this.closedSprite;
			this.Health = this.MaxHealth;
		}

		this.glassRemoved = false;
	}

	public IsoSprite getSmashedSprite() {
		return this.smashedSprite;
	}

	public void setSmashedSprite(IsoSprite sprite) {
		this.smashedSprite = sprite;
	}

	public void setPermaLocked(Boolean Boolean1) {
		this.PermaLocked = Boolean1;
	}

	public boolean isPermaLocked() {
		return this.PermaLocked;
	}

	public boolean canClimbThrough(IsoGameCharacter gameCharacter) {
		if (this.square != null && !this.square.Is(IsoFlagType.makeWindowInvincible)) {
			if (this.isBarricaded()) {
				return false;
			} else {
				return this.Health > 0 && !this.destroyed ? this.open : true;
			}
		} else {
			return false;
		}
	}

	public boolean isGlassRemoved() {
		return this.glassRemoved;
	}

	public void setGlassRemoved(boolean boolean1) {
		if (this.destroyed) {
			if (boolean1) {
				this.sprite = this.glassRemovedSprite;
				this.glassRemoved = true;
			} else {
				this.sprite = this.smashedSprite;
				this.glassRemoved = false;
			}
		}
	}

	public void removeBrokenGlass() {
		if (GameClient.bClient) {
			GameClient.instance.smashWindow(this, 2);
		} else {
			this.setGlassRemoved(true);
		}
	}

	public void addRandomBarricades() {
		IsoGridSquare square = this.square.getRoom() == null ? this.square : this.getOppositeSquare();
		if (this.getZ() == 0.0F && square != null && square.getRoom() == null) {
			boolean boolean1 = square != this.square;
			IsoBarricade barricade = IsoBarricade.AddBarricadeToObject(this, boolean1);
			if (barricade != null) {
				int int1 = Rand.Next(1, 4);
				for (int int2 = 0; int2 < int1; ++int2) {
					barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
				}

				if (GameServer.bServer) {
					barricade.transmitCompleteItemToClients();
				}
			}
		} else {
			this.addSheet((IsoGameCharacter)null);
			this.HasCurtains().ToggleDoor((IsoGameCharacter)null);
		}
	}
	public static enum WindowType {

		SinglePane,
		DoublePane;
	}
}
