package zombie.iso.objects;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.AmbientStreamManager;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ThumpState;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.SafeHouse;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;


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
		IsoGridSquare square = this.getOppositeSquare();
		if (square != null) {
			IsoCurtain curtain = square.getCurtain(this.getNorth() ? IsoObjectType.curtainS : IsoObjectType.curtainE);
			if (curtain != null) {
				return curtain;
			}
		}

		return this.getSquare().getCurtain(this.getNorth() ? IsoObjectType.curtainN : IsoObjectType.curtainW);
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
		if (this.square == null) {
			return null;
		} else {
			return this.north ? this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
		}
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
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (GameClient.bClient) {
			if (player != null) {
				GameClient.instance.sendWeaponHit(player, handWeapon, this);
			}
		} else {
			Thumpable thumpable = this.getThumpableFor(gameCharacter);
			if (thumpable != null) {
				if (thumpable instanceof IsoBarricade) {
					((IsoBarricade)thumpable).WeaponHit(gameCharacter, handWeapon);
				} else if (handWeapon == ((IsoLivingCharacter)gameCharacter).bareHands) {
					if (player != null) {
						player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Glass);
						player.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
					}
				} else {
					if (handWeapon != null) {
						this.Damage((float)(handWeapon.getDoorDamage() * 5), gameCharacter);
					} else {
						this.Damage(100.0F, gameCharacter);
					}

					this.DirtySlice();
					if (handWeapon != null && handWeapon.getDoorHitSound() != null) {
						if (player != null) {
							player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Glass);
						}

						gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
						if (GameServer.bServer) {
							GameServer.PlayWorldSoundServer(gameCharacter, handWeapon.getDoorHitSound(), false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
						}
					}

					WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
					if (!this.isDestroyed() && this.Health <= 0) {
						this.smashWindow();
						this.addBrokenGlass(gameCharacter);
					}
				}
			}
		}
	}

	public void smashWindow(boolean boolean1, boolean boolean2) {
		if (!this.destroyed) {
			if (GameClient.bClient && !boolean1) {
				GameClient.instance.smashWindow(this, 1);
			}

			if (!boolean1) {
				if (GameServer.bServer) {
					GameServer.PlayWorldSoundServer("SmashWindow", false, this.square, 0.2F, 20.0F, 1.1F, true);
				} else {
					SoundManager.instance.PlayWorldSound("SmashWindow", this.square, 0.2F, 20.0F, 1.0F, true);
				}

				WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
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
		}
	}

	public void smashWindow(boolean boolean1) {
		this.smashWindow(boolean1, true);
	}

	public void smashWindow() {
		this.smashWindow(false, true);
	}

	public void addBrokenGlass(IsoMovingObject movingObject) {
		if (movingObject != null) {
			if (this.getSquare() != null) {
				if (this.getNorth()) {
					this.addBrokenGlass(movingObject.getY() >= (float)this.getSquare().getY());
				} else {
					this.addBrokenGlass(movingObject.getX() >= (float)this.getSquare().getX());
				}
			}
		}
	}

	public void addBrokenGlass(boolean boolean1) {
		IsoGridSquare square = boolean1 ? this.getOppositeSquare() : this.getSquare();
		if (square != null) {
			square.addBrokenGlass();
		}
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

		this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, sprite, int1);
		this.smashedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, sprite, int2);
		if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
			int int3 = Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset"));
			this.glassRemovedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.closedSprite, int3);
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
		return false;
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
		if (movingObject instanceof IsoGameCharacter) {
			Thumpable thumpable = this.getThumpableFor((IsoGameCharacter)movingObject);
			if (thumpable == null) {
				return;
			}

			if (thumpable != this) {
				thumpable.Thump(movingObject);
				return;
			}
		}

		if (movingObject instanceof IsoZombie) {
			if (((IsoZombie)movingObject).cognition == 1 && !this.canClimbThrough((IsoZombie)movingObject) && !this.isInvincible() && (!this.Locked || movingObject.getCurrentSquare() != null && !movingObject.getCurrentSquare().Is(IsoFlagType.exterior))) {
				this.ToggleWindow((IsoGameCharacter)movingObject);
				if (this.canClimbThrough((IsoZombie)movingObject)) {
					return;
				}
			}

			int int1 = ThumpState.getFastForwardDamageMultiplier();
			this.DirtySlice();
			this.Damage((float)(((IsoZombie)movingObject).strength * int1), movingObject);
			WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		}

		if (!this.isDestroyed() && this.Health <= 0) {
			if (this.getSquare().getBuilding() != null) {
				this.getSquare().getBuilding().forceAwake();
			}

			if (GameServer.bServer) {
				GameServer.smashWindow(this, 1);
				GameServer.PlayWorldSoundServer((IsoGameCharacter)movingObject, "SmashWindow", false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
			}

			((IsoGameCharacter)movingObject).getEmitter().playSound("SmashWindow", this);
			WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
			movingObject.setThumpTarget((Thumpable)null);
			this.destroyed = true;
			this.sprite = this.smashedSprite;
			this.square.InvalidateSpecialObjectPaths();
			this.addBrokenGlass(movingObject);
			if (movingObject instanceof IsoZombie && this.getThumpableFor((IsoZombie)movingObject) != null) {
				movingObject.setThumpTarget(this.getThumpableFor((IsoZombie)movingObject));
			}
		}
	}

	public Thumpable getThumpableFor(IsoGameCharacter gameCharacter) {
		IsoBarricade barricade = this.getBarricadeForCharacter(gameCharacter);
		if (barricade != null) {
			return barricade;
		} else if (!this.isDestroyed() && !this.IsOpen()) {
			return this;
		} else {
			barricade = this.getBarricadeOppositeCharacter(gameCharacter);
			return barricade != null ? barricade : null;
		}
	}

	public float getThumpCondition() {
		return (float)PZMath.clamp(this.Health, 0, this.MaxHealth) / (float)this.MaxHealth;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
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
				this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (byteBuffer.get() == 1) {
				this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (byteBuffer.get() == 1) {
				this.smashedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (byteBuffer.get() == 1) {
				this.glassRemovedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}
		} else {
			if (byteBuffer.getInt() == 1) {
				this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (byteBuffer.getInt() == 1) {
				this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (byteBuffer.getInt() == 1) {
				this.smashedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (this.closedSprite != null) {
				if (this.destroyed && this.closedSprite.getProperties().Is("SmashedTileOffset")) {
					int2 = Integer.parseInt(this.closedSprite.getProperties().Val("SmashedTileOffset"));
					this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.closedSprite, -int2);
				}

				if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
					int2 = Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset"));
					this.glassRemovedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.closedSprite, int2);
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

	public void addToWorld() {
		super.addToWorld();
		this.getCell().addToWindowList(this);
	}

	public void removeFromWorld() {
		super.removeFromWorld();
		this.getCell().removeFromWindowList(this);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
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

	public void saveState(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
	}

	public void loadState(ByteBuffer byteBuffer) throws IOException {
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1 != this.Locked) {
			this.Locked = boolean1;
		}
	}

	public void openCloseCurtain(IsoGameCharacter gameCharacter) {
		if (gameCharacter == IsoPlayer.getInstance()) {
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
		IsoObjectType objectType;
		IsoGridSquare square2;
		if (this.north) {
			square2 = this.square;
			objectType = IsoObjectType.curtainN;
			if (gameCharacter != null) {
				if (gameCharacter.getY() < this.getY()) {
					square2 = this.getCell().getGridSquare(square2.getX(), square2.getY() - 1, square2.getZ());
					objectType = IsoObjectType.curtainS;
				}
			} else if (square2.getRoom() == null) {
				square2 = this.getCell().getGridSquare(square2.getX(), square2.getY() - 1, square2.getZ());
				objectType = IsoObjectType.curtainS;
			}

			square = square2;
		} else {
			square2 = this.square;
			objectType = IsoObjectType.curtainW;
			if (gameCharacter != null) {
				if (gameCharacter.getX() < this.getX()) {
					square2 = this.getCell().getGridSquare(square2.getX() - 1, square2.getY(), square2.getZ());
					objectType = IsoObjectType.curtainE;
				}
			} else if (square2.getRoom() == null) {
				square2 = this.getCell().getGridSquare(square2.getX() - 1, square2.getY(), square2.getZ());
				objectType = IsoObjectType.curtainE;
			}

			square = square2;
		}

		if (square.getCurtain(objectType) == null) {
			if (square != null) {
				int int1 = 16;
				if (objectType == IsoObjectType.curtainE) {
					++int1;
				}

				if (objectType == IsoObjectType.curtainS) {
					int1 += 3;
				}

				if (objectType == IsoObjectType.curtainN) {
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
						if (!(gameCharacter instanceof IsoZombie) || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue()) {
							this.handleAlarm();
						}

						this.sprite = this.openSprite;
					}

					this.square.RecalcProperties();
					this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
					PolygonalMap2.instance.squareChanged(this.square);
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
			PrintStream printStream = System.out;
			String string = this.getClass().getSimpleName();
			printStream.println("ERROR: " + string + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
			} else if (GameServer.bServer && !boolean1) {
				Iterator iterator = GameServer.udpEngine.connections.iterator();
				while (iterator.hasNext()) {
					UdpConnection udpConnection2 = (UdpConnection)iterator.next();
					ByteBufferWriter byteBufferWriter2 = udpConnection2.startPacket();
					PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
					this.syncIsoObjectSend(byteBufferWriter2);
					PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
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
							PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter3);
							this.syncIsoObjectSend(byteBufferWriter3);
							PacketTypes.PacketType.SyncIsoObject.send(udpConnection3);
						}
					}
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
		if (!this.isInvincible() && !"Tutorial".equals(Core.GameMode)) {
			this.DirtySlice();
			this.Health = (int)((float)this.Health - float1);
			if (this.Health < 0) {
				this.Health = 0;
			}

			if (!this.isDestroyed() && this.Health == 0) {
				this.smashWindow(false, !boolean1 || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue());
				if (this.getSquare().getBuilding() != null) {
					this.getSquare().getBuilding().forceAwake();
				}
			}
		}
	}

	public void Damage(float float1, IsoMovingObject movingObject) {
		if (!this.isInvincible() && !"Tutorial".equals(Core.GameMode)) {
			this.Health = (int)((float)this.Health - float1);
			if (this.Health < 0) {
				this.Health = 0;
			}

			if (!this.isDestroyed() && this.Health == 0) {
				boolean boolean1 = !(movingObject instanceof IsoZombie) || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue();
				this.smashWindow(false, boolean1);
				this.addBrokenGlass(movingObject);
			}
		}
	}

	public boolean isLocked() {
		return this.Locked;
	}

	public boolean isSmashed() {
		return this.destroyed;
	}

	public boolean isInvincible() {
		if (this.square != null && this.square.Is(IsoFlagType.makeWindowInvincible)) {
			int int1 = this.getObjectIndex();
			if (int1 != -1) {
				IsoObject[] objectArray = (IsoObject[])this.square.getObjects().getElements();
				int int2 = this.square.getObjects().size();
				for (int int3 = 0; int3 < int2; ++int3) {
					if (int3 != int1) {
						IsoObject object = objectArray[int3];
						PropertyContainer propertyContainer = object.getProperties();
						if (propertyContainer != null && propertyContainer.Is(this.getNorth() ? IsoFlagType.cutN : IsoFlagType.cutW) && propertyContainer.Is(IsoFlagType.makeWindowInvincible)) {
							return true;
						}
					}
				}
			}

			return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.makeWindowInvincible);
		} else {
			return false;
		}
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

	public boolean isBarricadeAllowed() {
		return true;
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

	public static boolean canClimbThroughHelper(IsoGameCharacter gameCharacter, IsoGridSquare square, IsoGridSquare square2, boolean boolean1) {
		IsoGridSquare square3 = square;
		float float1 = 0.5F;
		float float2 = 0.5F;
		if (boolean1) {
			if (gameCharacter.getY() >= (float)square.getY()) {
				square3 = square2;
				float2 = 0.7F;
			} else {
				float2 = 0.3F;
			}
		} else if (gameCharacter.getX() >= (float)square.getX()) {
			square3 = square2;
			float1 = 0.7F;
		} else {
			float1 = 0.3F;
		}

		if (square3 == null) {
			return false;
		} else if (square3.isSolid()) {
			return false;
		} else if (square3.Is(IsoFlagType.water)) {
			return false;
		} else if (!gameCharacter.canClimbDownSheetRope(square3) && !square3.HasStairsBelow() && !PolygonalMap2.instance.canStandAt((float)square3.x + float1, (float)square3.y + float2, square3.z, (BaseVehicle)null, 19)) {
			return !square3.TreatAsSolidFloor();
		} else {
			return !GameClient.bClient || !(gameCharacter instanceof IsoPlayer) || SafeHouse.isSafeHouse(square3, ((IsoPlayer)gameCharacter).getUsername(), true) == null || ServerOptions.instance.SafehouseAllowTrepass.getValue();
		}
	}

	public boolean canClimbThrough(IsoGameCharacter gameCharacter) {
		if (this.square != null && !this.isInvincible()) {
			if (this.isBarricaded()) {
				return false;
			} else if (gameCharacter != null && !canClimbThroughHelper(gameCharacter, this.getSquare(), this.getOppositeSquare(), this.north)) {
				return false;
			} else {
				IsoGameCharacter gameCharacter2 = this.getFirstCharacterClosing();
				if (gameCharacter2 != null && gameCharacter2.isVariable("CloseWindowOutcome", "success")) {
					return false;
				} else {
					return this.Health > 0 && !this.destroyed ? this.open : true;
				}
			}
		} else {
			return false;
		}
	}

	public IsoGameCharacter getFirstCharacterClimbingThrough() {
		IsoGameCharacter gameCharacter = this.getFirstCharacterClimbingThrough(this.getSquare());
		return gameCharacter != null ? gameCharacter : this.getFirstCharacterClimbingThrough(this.getOppositeSquare());
	}

	public IsoGameCharacter getFirstCharacterClimbingThrough(IsoGridSquare square) {
		if (square == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < square.getMovingObjects().size(); ++int1) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo((IsoMovingObject)square.getMovingObjects().get(int1), IsoGameCharacter.class);
				if (gameCharacter != null && gameCharacter.isClimbingThroughWindow(this)) {
					return gameCharacter;
				}
			}

			return null;
		}
	}

	public IsoGameCharacter getFirstCharacterClosing() {
		IsoGameCharacter gameCharacter = this.getFirstCharacterClosing(this.getSquare());
		return gameCharacter != null ? gameCharacter : this.getFirstCharacterClosing(this.getOppositeSquare());
	}

	public IsoGameCharacter getFirstCharacterClosing(IsoGridSquare square) {
		if (square == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < square.getMovingObjects().size(); ++int1) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo((IsoMovingObject)square.getMovingObjects().get(int1), IsoGameCharacter.class);
				if (gameCharacter != null && gameCharacter.isClosingWindow(this)) {
					return gameCharacter;
				}
			}

			return null;
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

			if (this.getObjectIndex() != -1) {
				PolygonalMap2.instance.squareChanged(this.square);
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

	public IsoBarricade addBarricadesDebug(int int1, boolean boolean1) {
		IsoGridSquare square = this.square.getRoom() == null ? this.square : this.getOppositeSquare();
		boolean boolean2 = square != this.square;
		IsoBarricade barricade = IsoBarricade.AddBarricadeToObject(this, boolean2);
		if (barricade != null) {
			for (int int2 = 0; int2 < int1; ++int2) {
				if (boolean1) {
					barricade.addMetalBar((IsoGameCharacter)null, (InventoryItem)null);
				} else {
					barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
				}
			}
		}

		return barricade;
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

		private static IsoWindow.WindowType[] $values() {
			return new IsoWindow.WindowType[]{SinglePane, DoublePane};
		}
	}
}
