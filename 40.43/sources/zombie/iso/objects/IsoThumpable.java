package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;


public class IsoThumpable extends IsoObject implements BarricadeAble,Thumpable {
	private KahluaTable table;
	private KahluaTable modData;
	public Boolean isDoor = false;
	public Boolean isDoorFrame = false;
	public String breakSound = "BreakObject";
	private boolean isCorner = false;
	private boolean isFloor = false;
	private boolean blockAllTheSquare = false;
	public boolean Locked = false;
	public int MaxHealth = 500;
	public int Health = 500;
	public int PushedMaxStrength = 0;
	public int PushedStrength = 0;
	IsoSprite closedSprite;
	public boolean north = false;
	public String name = "";
	private int thumpDmg = 8;
	private float crossSpeed = 1.0F;
	int gid = -1;
	public boolean open = false;
	IsoSprite openSprite;
	private boolean destroyed = false;
	private boolean canBarricade = false;
	public boolean canPassThrough = false;
	private boolean isStairs = false;
	private boolean isContainer = false;
	private boolean dismantable = false;
	private boolean canBePlastered = false;
	private boolean paintable = false;
	private boolean isThumpable = true;
	private boolean isHoppable = false;
	private int lightSourceRadius = -1;
	private int lightSourceLife = -1;
	private int lightSourceXOffset = 0;
	private int lightSourceYOffset = 0;
	private boolean lightSourceOn = false;
	private IsoLightSource lightSource = null;
	private String lightSourceFuel = null;
	private float lifeLeft = -1.0F;
	private float lifeDelta = 0.0F;
	private boolean haveFuel = false;
	private float updateAccumulator = 0.0F;
	private float lastUpdateHours = -1.0F;
	public int keyId = -1;
	private boolean lockedByKey = false;
	public boolean lockedByPadlock = false;
	private boolean canBeLockByPadlock = false;
	public int lockedByCode = 0;
	public int OldNumPlanks = 0;
	public String thumpSound = "ZombieThumpGeneric";
	public static Vector2 tempo = new Vector2();

	public KahluaTable getModData() {
		if (this.modData == null) {
			this.modData = LuaManager.platform.newTable();
		}

		return this.modData;
	}

	public void setModData(KahluaTable kahluaTable) {
		this.modData = kahluaTable;
	}

	public boolean hasModData() {
		return this.modData != null && !this.modData.isEmpty();
	}

	public boolean isCanPassThrough() {
		return this.canPassThrough;
	}

	public void setCanPassThrough(boolean boolean1) {
		this.canPassThrough = boolean1;
	}

	public boolean isBlockAllTheSquare() {
		return this.blockAllTheSquare;
	}

	public void setBlockAllTheSquare(boolean boolean1) {
		this.blockAllTheSquare = boolean1;
	}

	public void setIsDismantable(boolean boolean1) {
		this.dismantable = boolean1;
	}

	public boolean isDismantable() {
		return this.dismantable;
	}

	public float getCrossSpeed() {
		return this.crossSpeed;
	}

	public void setCrossSpeed(float float1) {
		this.crossSpeed = float1;
	}

	public void setIsFloor(boolean boolean1) {
		this.isFloor = boolean1;
	}

	public boolean isCorner() {
		return this.isCorner;
	}

	public boolean isFloor() {
		return this.isFloor;
	}

	public void setIsContainer(boolean boolean1) {
		this.isContainer = boolean1;
		if (boolean1) {
			this.container = new ItemContainer("crate", this.square, this, 6, 6);
			if (this.sprite.getProperties().Is("ContainerCapacity")) {
				this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
			}

			this.container.setExplored(true);
		}
	}

	public void setIsStairs(boolean boolean1) {
		this.isStairs = boolean1;
	}

	public boolean isStairs() {
		return this.isStairs;
	}

	public boolean isWindow() {
		return this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.WindowN) || this.sprite.getProperties().Is(IsoFlagType.WindowW));
	}

	public String getObjectName() {
		return "Thumpable";
	}

	public IsoThumpable(IsoCell cell) {
		super(cell);
	}

	public void setCorner(boolean boolean1) {
		this.isCorner = boolean1;
	}

	public void setCanBarricade(boolean boolean1) {
		this.canBarricade = boolean1;
	}

	public boolean getCanBarricade() {
		return this.canBarricade;
	}

	public void setHealth(int int1) {
		this.Health = int1;
	}

	public int getHealth() {
		return this.Health;
	}

	public void setMaxHealth(int int1) {
		this.MaxHealth = int1;
	}

	public int getMaxHealth() {
		return this.MaxHealth;
	}

	public void setThumpDmg(Integer integer) {
		this.thumpDmg = integer;
	}

	public void setBreakSound(String string) {
		this.breakSound = string;
	}

	public boolean isDoor() {
		return this.isDoor;
	}

	public boolean getNorth() {
		return this.north;
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else if (!this.isDoor && !this.isDoorFrame && !this.isWindow() && !this.isHoppable && (this.getProperties() == null || !this.getProperties().Is(IsoFlagType.collideN) && !this.getProperties().Is(IsoFlagType.collideW))) {
			return vector2.set(this.getX() + 0.5F, this.getY() + 0.5F);
		} else {
			return this.north ? vector2.set(this.getX() + 0.5F, this.getY()) : vector2.set(this.getX(), this.getY() + 0.5F);
		}
	}

	public boolean isDoorFrame() {
		return this.isDoorFrame;
	}

	public void setIsDoor(boolean boolean1) {
		this.isDoor = boolean1;
	}

	public void setIsDoorFrame(boolean boolean1) {
		this.isDoorFrame = boolean1;
	}

	public void setSprite(String string) {
		this.closedSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
		this.sprite = this.closedSprite;
	}

	public void setSpriteFromName(String string) {
		this.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
	}

	public void setClosedSprite(IsoSprite sprite) {
		this.closedSprite = sprite;
		this.sprite = this.closedSprite;
	}

	public void setOpenSprite(IsoSprite sprite) {
		this.openSprite = sprite;
	}

	public IsoThumpable(IsoCell cell, IsoGridSquare square, String string, String string2, boolean boolean1, KahluaTable kahluaTable) {
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.openSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string2);
		this.closedSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
		this.table = kahluaTable;
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
	}

	public IsoThumpable(IsoCell cell, IsoGridSquare square, String string, boolean boolean1, KahluaTable kahluaTable) {
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
		this.table = kahluaTable;
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.open = byteBuffer.get() == 1;
		this.Locked = byteBuffer.get() == 1;
		this.north = byteBuffer.get() == 1;
		if (int1 >= 87) {
			this.Health = byteBuffer.getInt();
			this.MaxHealth = byteBuffer.getInt();
		} else {
			int int2 = byteBuffer.getInt();
			this.Health = byteBuffer.getInt();
			this.MaxHealth = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			if (int1 >= 49) {
				short short1 = byteBuffer.getShort();
			} else {
				Math.max(int3, int2 * 1000);
			}

			this.OldNumPlanks = int2;
		}

		this.closedSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		if (byteBuffer.getInt() == 1) {
			this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
		}

		this.thumpDmg = byteBuffer.getInt();
		this.name = GameWindow.ReadString(byteBuffer);
		this.isDoor = byteBuffer.get() == 1;
		this.isDoorFrame = byteBuffer.get() == 1;
		this.isCorner = byteBuffer.get() == 1;
		this.isStairs = byteBuffer.get() == 1;
		this.isContainer = byteBuffer.get() == 1;
		this.isFloor = byteBuffer.get() == 1;
		this.canBarricade = byteBuffer.get() == 1;
		this.canPassThrough = byteBuffer.get() == 1;
		this.dismantable = byteBuffer.get() == 1;
		this.canBePlastered = byteBuffer.get() == 1;
		this.paintable = byteBuffer.get() == 1;
		this.crossSpeed = byteBuffer.getFloat();
		if (byteBuffer.get() != 0) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load(byteBuffer, int1);
		}

		if (byteBuffer.get() != 0) {
			if (this.modData == null) {
				this.modData = LuaManager.platform.newTable();
			}

			this.modData.load(byteBuffer, int1);
		}

		this.blockAllTheSquare = byteBuffer.get() == 1;
		this.isThumpable = byteBuffer.get() == 1;
		this.isHoppable = byteBuffer.get() == 1;
		if (int1 >= 26) {
			this.setLightSourceLife(byteBuffer.getInt());
			this.setLightSourceRadius(byteBuffer.getInt());
			this.setLightSourceXOffset(byteBuffer.getInt());
			this.setLightSourceYOffset(byteBuffer.getInt());
			this.setLightSourceFuel(GameWindow.ReadString(byteBuffer));
			this.setLifeDelta(byteBuffer.getFloat());
			this.setLifeLeft(byteBuffer.getFloat());
			this.setLightSourceOn(byteBuffer.get() == 1);
		}

		if (int1 >= 28) {
			this.haveFuel = byteBuffer.get() == 1;
		} else if (this.getLifeDelta() > 0.0F && this.getLifeLeft() > 0.0F) {
			this.setHaveFuel(true);
		}

		if (this.getLightSourceFuel() != null) {
			boolean boolean1 = this.isLightSourceOn();
			this.createLightSource(this.getLightSourceRadius(), this.getLightSourceXOffset(), this.getLightSourceYOffset(), 0, this.getLightSourceLife(), this.getLightSourceFuel(), (InventoryItem)null, (IsoGameCharacter)null);
			if (this.lightSource != null) {
				this.getLightSource().setActive(boolean1);
			}

			this.setLightSourceOn(boolean1);
		}

		if (int1 >= 57) {
			this.keyId = byteBuffer.getInt();
			this.lockedByKey = byteBuffer.get() == 1;
			this.lockedByPadlock = byteBuffer.get() == 1;
			this.canBeLockByPadlock = byteBuffer.get() == 1;
			this.lockedByCode = byteBuffer.getInt();
		}

		if (int1 >= 91) {
			this.thumpSound = GameWindow.ReadString(byteBuffer);
			if ("thumpa2".equals(this.thumpSound)) {
				this.thumpSound = "ZombieThumpGeneric";
			}

			if ("metalthump".equals(this.thumpSound)) {
				this.thumpSound = "ZombieThumpMetal";
			}
		}

		if (int1 >= 132) {
			this.lastUpdateHours = byteBuffer.getFloat();
		}

		if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequestLoad(this.square);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
		byteBuffer.put((byte)(this.north ? 1 : 0));
		byteBuffer.putInt(this.Health);
		byteBuffer.putInt(this.MaxHealth);
		byteBuffer.putInt(this.closedSprite != null ? this.closedSprite.ID : 0);
		if (this.openSprite == null) {
			byteBuffer.putInt(0);
		} else {
			byteBuffer.putInt(1);
			byteBuffer.putInt(this.openSprite.ID);
		}

		byteBuffer.putInt(this.thumpDmg);
		GameWindow.WriteString(byteBuffer, this.name);
		byteBuffer.put((byte)(this.isDoor ? 1 : 0));
		byteBuffer.put((byte)(this.isDoorFrame ? 1 : 0));
		byteBuffer.put((byte)(this.isCorner ? 1 : 0));
		byteBuffer.put((byte)(this.isStairs ? 1 : 0));
		byteBuffer.put((byte)(this.isContainer ? 1 : 0));
		byteBuffer.put((byte)(this.isFloor ? 1 : 0));
		byteBuffer.put((byte)(this.canBarricade ? 1 : 0));
		byteBuffer.put((byte)(this.canPassThrough ? 1 : 0));
		byteBuffer.put((byte)(this.dismantable ? 1 : 0));
		byteBuffer.put((byte)(this.canBePlastered ? 1 : 0));
		byteBuffer.put((byte)(this.paintable ? 1 : 0));
		byteBuffer.putFloat(this.crossSpeed);
		if (this.table != null && !this.table.isEmpty()) {
			byteBuffer.put((byte)1);
			this.table.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.modData != null && !this.modData.isEmpty()) {
			byteBuffer.put((byte)1);
			this.modData.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.put((byte)(this.blockAllTheSquare ? 1 : 0));
		byteBuffer.put((byte)(this.isThumpable ? 1 : 0));
		byteBuffer.put((byte)(this.isHoppable ? 1 : 0));
		byteBuffer.putInt(this.getLightSourceLife());
		byteBuffer.putInt(this.getLightSourceRadius());
		byteBuffer.putInt(this.getLightSourceXOffset());
		byteBuffer.putInt(this.getLightSourceYOffset());
		GameWindow.WriteString(byteBuffer, this.getLightSourceFuel() != null ? this.getLightSourceFuel() : "");
		byteBuffer.putFloat(this.getLifeDelta());
		byteBuffer.putFloat(this.getLifeLeft());
		byteBuffer.put((byte)(this.isLightSourceOn() ? 1 : 0));
		byteBuffer.put((byte)(this.haveFuel ? 1 : 0));
		byteBuffer.putInt(this.getKeyId());
		byteBuffer.put((byte)(this.isLockedByKey() ? 1 : 0));
		byteBuffer.put((byte)(this.isLockedByPadlock() ? 1 : 0));
		byteBuffer.put((byte)(this.canBeLockByPadlock() ? 1 : 0));
		byteBuffer.putInt(this.getLockedByCode());
		GameWindow.WriteString(byteBuffer, this.thumpSound);
		byteBuffer.putFloat(this.lastUpdateHours);
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
		if (this.isDoor) {
			IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
			if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.0F && this.square.getZ() == square.getZ() && (this.getOppositeSquare() == square || !this.square.isSomethingTo(square))) {
				this.ToggleDoor(IsoPlayer.getInstance());
			}
		}

		return true;
	}

	public boolean TestPathfindCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		boolean boolean1 = this.north;
		if (movingObject instanceof IsoSurvivor && ((IsoSurvivor)movingObject).getInventory().contains("Hammer")) {
			return false;
		} else if (this.open) {
			return false;
		} else {
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
		if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).isNoClip()) {
			return false;
		} else {
			boolean boolean1 = this.north;
			if (this.open) {
				return false;
			} else if (this.blockAllTheSquare) {
				if (square != this.square) {
					if (movingObject != null) {
						movingObject.collideWith(this);
					}

					return true;
				} else {
					return false;
				}
			} else {
				if (square == this.square) {
					if (boolean1 && square2.getY() < square.getY()) {
						if (movingObject != null) {
							movingObject.collideWith(this);
						}

						if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
							return true;
						}
					}

					if (!boolean1 && square2.getX() < square.getX()) {
						if (movingObject != null) {
							movingObject.collideWith(this);
						}

						if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
							return true;
						}
					}
				} else {
					if (boolean1 && square2.getY() > square.getY()) {
						if (movingObject != null) {
							movingObject.collideWith(this);
						}

						if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
							return true;
						}
					}

					if (!boolean1 && square2.getX() > square.getX()) {
						if (movingObject != null) {
							movingObject.collideWith(this);
						}

						if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
							return true;
						}
					}
				}

				if (this.isCorner) {
					if (square2.getY() < square.getY() && square2.getX() < square.getX()) {
						if (movingObject != null) {
							movingObject.collideWith(this);
						}

						if (!this.canPassThrough) {
							return true;
						}
					}

					if (square2.getY() > square.getY() && square2.getX() > square.getX()) {
						if (movingObject != null) {
							movingObject.collideWith(this);
						}

						if (!this.canPassThrough) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		if (this.canPassThrough) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			boolean boolean1 = this.north;
			if (this.open) {
				boolean1 = !boolean1;
			}

			if (square2.getZ() != square.getZ()) {
				return IsoObject.VisionResult.NoEffect;
			} else {
				if (square == this.square) {
					if (boolean1 && square2.getY() < square.getY()) {
						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}

					if (!boolean1 && square2.getX() < square.getX()) {
						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}
				} else {
					if (boolean1 && square2.getY() > square.getY()) {
						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}

					if (!boolean1 && square2.getX() > square.getX()) {
						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}
				}

				return IsoObject.VisionResult.NoEffect;
			}
		}
	}

	public void Thump(IsoMovingObject movingObject) {
		if (SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
			if (this.isThumpable()) {
				if (!this.isDestroyed()) {
					int int1;
					if (movingObject instanceof IsoZombie) {
						if (this.isDoor() || this.isWindow()) {
							IsoBarricade barricade = this.getBarricadeForCharacter((IsoZombie)movingObject);
							if (barricade != null) {
								barricade.Thump(movingObject);
								return;
							}

							barricade = this.getBarricadeOppositeCharacter((IsoZombie)movingObject);
							if (barricade != null) {
								barricade.Thump(movingObject);
								return;
							}
						}

						if (((IsoZombie)movingObject).cognition == 1 && this.isDoor() && !this.IsOpen()) {
							this.ToggleDoor((IsoGameCharacter)movingObject);
							return;
						}

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

						int int2 = this.thumpDmg;
						if (int1 >= int2) {
							this.Damage(1 * ThumpState.getFastForwardDamageMultiplier());
						}

						WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
						if (this.isDoor()) {
							this.setRenderEffect(RenderEffectType.Hit_Door, true);
						}
					}

					if (this.Health <= 0) {
						int1 = IsoDoor.getDoubleDoorIndex(this);
						if (int1 == 1 || int1 == 4) {
							IsoObject object = IsoDoor.getDoubleDoorObject(this, int1 == 1 ? 2 : 3);
							if (object != null) {
								((IsoThumpable)object).destroy();
							}
						}

						((IsoGameCharacter)movingObject).getEmitter().playSound(this.breakSound, this);
						if (GameServer.bServer) {
							GameServer.PlayWorldSoundServer(this.breakSound, false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
						}

						WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
						movingObject.setThumpTarget((Thumpable)null);
						this.destroy();
					}
				}
			}
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameClient.bClient) {
			if (gameCharacter instanceof IsoPlayer) {
				GameClient.instance.sendWeaponHit((IsoPlayer)gameCharacter, handWeapon, this);
			}

			if (this.isDoor()) {
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			}
		} else if (!this.open || !this.isDoor) {
			if (!this.isDestroyed()) {
				if (this.isDoor() || this.isWindow()) {
					IsoBarricade barricade = this.getBarricadeForCharacter(gameCharacter);
					if (barricade != null) {
						barricade.WeaponHit(gameCharacter, handWeapon);
						return;
					}

					barricade = this.getBarricadeOppositeCharacter(gameCharacter);
					if (barricade != null) {
						barricade.WeaponHit(gameCharacter, handWeapon);
						return;
					}
				}

				this.Damage(handWeapon.getDoorDamage());
				if (handWeapon.getDoorHitSound() != null) {
					gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
					if (GameServer.bServer) {
						GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 0.2F, 20.0F, 1.0F, false);
					}
				}

				WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
				if (this.isDoor()) {
					this.setRenderEffect(RenderEffectType.Hit_Door, true);
				}

				if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
					gameCharacter.getEmitter().playSound(this.breakSound, this);
					WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
					int int1 = IsoDoor.getDoubleDoorIndex(this);
					if (int1 == 1 || int1 == 4) {
						IsoObject object = IsoDoor.getDoubleDoorObject(this, int1 == 1 ? 2 : 3);
						if (object != null) {
							((IsoThumpable)object).destroy();
						}
					}

					if (GameClient.bClient) {
						GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "OnDestroyIsoThumpable", "x", (int)this.getX(), "y", (int)this.getY(), "z", (int)this.getZ(), "index", this.getObjectIndex());
					}

					LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, (Object)null);
					this.destroyed = true;
					if (this.getObjectIndex() != -1) {
						this.square.transmitRemoveItemFromSquare(this);
					}
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
		if (this.isBarricaded()) {
			if (gameCharacter != null) {
				gameCharacter.getEmitter().playSound("DoorIsBlocked", this);
				gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0F);
			}
		} else if (this.isLockedByKey() && gameCharacter != null && gameCharacter instanceof IsoPlayer && gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null) {
			gameCharacter.getEmitter().playSound("DoorIsLocked", this);
		} else {
			if (this.isLockedByKey()) {
				gameCharacter.getEmitter().playSound("UnlockDoor", this);
			}

			this.DirtySlice();
			this.square.InvalidateSpecialObjectPaths();
			if (this.Locked && gameCharacter != null && gameCharacter instanceof IsoPlayer && gameCharacter.getCurrentSquare().getRoom() == null && !this.open) {
				gameCharacter.getEmitter().playSound("DoorIsLocked", this);
				if (gameCharacter instanceof IsoSurvivor) {
					gameCharacter.getMasterBehaviorList().reset();
				}
			} else {
				if (gameCharacter instanceof IsoPlayer) {
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						LosUtil.cachecleared[int1] = true;
					}

					IsoGridSquare.setRecalcLightTime(-1);
					GameTime.instance.lightSourceUpdate = 100.0F;
				}

				if (this.getSprite().getProperties().Is("DoubleDoor")) {
					if (IsoDoor.isDoubleDoorObstructed(this)) {
						if (gameCharacter != null) {
							gameCharacter.getEmitter().playSound("DoorIsBlocked", this);
							gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
						}
					} else {
						boolean boolean1 = this.open;
						IsoDoor.toggleDoubleDoor(this, true);
						if (boolean1 != this.open) {
							gameCharacter.getEmitter().playSound(this.open ? "OpenDoor" : "CloseDoor", this);
						}
					}
				} else if (this.isObstructed()) {
					if (gameCharacter != null) {
						gameCharacter.getEmitter().playSound("DoorIsBlocked", this);
						gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
					}
				} else {
					this.sprite = this.closedSprite;
					this.open = !this.open;
					this.setLockedByKey(false);
					if (this.open) {
						gameCharacter.getEmitter().playSound("OpenDoor", this);
						this.sprite = this.openSprite;
					} else {
						gameCharacter.getEmitter().playSound("CloseDoor", this);
					}

					this.square.RecalcProperties();
					this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
					LuaEventManager.triggerEvent("OnContainerUpdate");
				}
			}
		}
	}

	public void ToggleDoor(IsoGameCharacter gameCharacter) {
		this.ToggleDoorActual(gameCharacter);
	}

	public void ToggleDoorSilent() {
		if (!this.isBarricaded()) {
			this.square.InvalidateSpecialObjectPaths();
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				LosUtil.cachecleared[int1] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			this.open = !this.open;
			this.sprite = this.closedSprite;
			if (this.open) {
				this.sprite = this.openSprite;
			}
		}
	}

	public boolean isObstructed() {
		return IsoDoor.isDoorObstructed(this);
	}

	public boolean haveSheetRope() {
		return IsoWindow.isTopOfSheetRopeHere(this.square, this.north);
	}

	public int countAddSheetRope() {
		return !this.isHoppable() && !this.isWindow() ? 0 : IsoWindow.countAddSheetRope(this.square, this.north);
	}

	public boolean canAddSheetRope() {
		return !this.isHoppable() && !this.isWindow() ? false : IsoWindow.canAddSheetRope(this.square, this.north);
	}

	public boolean addSheetRope(IsoPlayer player, String string) {
		return !this.canAddSheetRope() ? false : IsoWindow.addSheetRope(player, this.square, this.north, string);
	}

	public boolean removeSheetRope(IsoPlayer player) {
		return this.haveSheetRope() ? IsoWindow.removeSheetRope(player, this.square, this.north) : false;
	}

	public void createLightSource(int int1, int int2, int int3, int int4, int int5, String string, InventoryItem inventoryItem, IsoGameCharacter gameCharacter) {
		this.setLightSourceXOffset(int2);
		this.setLightSourceYOffset(int3);
		this.setLightSourceRadius(int1);
		this.setLightSourceFuel(string);
		if (inventoryItem != null) {
			if (!(inventoryItem instanceof DrainableComboItem)) {
				this.setLifeLeft(1.0F);
				this.setHaveFuel(true);
			} else {
				this.setLifeLeft(((DrainableComboItem)inventoryItem).getUsedDelta());
				this.setLifeDelta(((DrainableComboItem)inventoryItem).getUseDelta());
				this.setHaveFuel(!"Base.Torch".equals(inventoryItem.getFullType()) || ((DrainableComboItem)inventoryItem).getUsedDelta() > 0.0F);
			}

			if (gameCharacter.getPrimaryHandItem() == inventoryItem) {
				gameCharacter.setPrimaryHandItem((InventoryItem)null);
			}

			if (gameCharacter.getSecondaryHandItem() == inventoryItem) {
				gameCharacter.setSecondaryHandItem((InventoryItem)null);
			}

			gameCharacter.getInventory().Remove(inventoryItem);
		}

		this.setLightSourceOn(this.haveFuel);
		if (this.lightSource != null) {
			this.lightSource.setActive(this.isLightSourceOn());
		}
	}

	public InventoryItem insertNewFuel(InventoryItem inventoryItem, IsoGameCharacter gameCharacter) {
		if (inventoryItem != null) {
			InventoryItem inventoryItem2 = this.removeCurrentFuel(gameCharacter);
			if (gameCharacter != null) {
				if (gameCharacter.getPrimaryHandItem() == inventoryItem) {
					gameCharacter.setPrimaryHandItem((InventoryItem)null);
				}

				if (gameCharacter.getSecondaryHandItem() == inventoryItem) {
					gameCharacter.setSecondaryHandItem((InventoryItem)null);
				}

				gameCharacter.getInventory().Remove(inventoryItem);
			}

			if (inventoryItem instanceof DrainableComboItem) {
				this.setLifeLeft(((DrainableComboItem)inventoryItem).getUsedDelta());
				this.setLifeDelta(((DrainableComboItem)inventoryItem).getUseDelta());
			} else {
				this.setLifeLeft(1.0F);
			}

			this.setHaveFuel(true);
			this.toggleLightSource(true);
			return inventoryItem2;
		} else {
			return null;
		}
	}

	public InventoryItem removeCurrentFuel(IsoGameCharacter gameCharacter) {
		if (this.haveFuel()) {
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(this.getLightSourceFuel());
			if (inventoryItem instanceof DrainableComboItem) {
				((DrainableComboItem)inventoryItem).setUsedDelta(this.getLifeLeft());
			}

			if (gameCharacter != null) {
				gameCharacter.getInventory().AddItem(inventoryItem);
			}

			this.setLifeLeft(0.0F);
			this.setLifeDelta(-1.0F);
			this.toggleLightSource(false);
			this.setHaveFuel(false);
			return inventoryItem;
		} else {
			return null;
		}
	}

	private int calcLightSourceX() {
		int int1 = (int)this.getX();
		int int2 = (int)this.getY();
		if (this.lightSourceXOffset != 0) {
			for (int int3 = 1; int3 <= Math.abs(this.lightSourceXOffset); ++int3) {
				int int4 = this.lightSourceXOffset > 0 ? 1 : -1;
				LosUtil.TestResults testResults = LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), int1 + int4, int2, (int)this.getZ(), false);
				if (testResults == LosUtil.TestResults.Blocked || testResults == LosUtil.TestResults.ClearThroughWindow) {
					break;
				}

				int1 += int4;
			}
		}

		return int1;
	}

	private int calcLightSourceY() {
		int int1 = (int)this.getX();
		int int2 = (int)this.getY();
		if (this.lightSourceYOffset != 0) {
			for (int int3 = 1; int3 <= Math.abs(this.lightSourceYOffset); ++int3) {
				int int4 = this.lightSourceYOffset > 0 ? 1 : -1;
				LosUtil.TestResults testResults = LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), int1, int2 + int4, (int)this.getZ(), false);
				if (testResults == LosUtil.TestResults.Blocked || testResults == LosUtil.TestResults.ClearThroughWindow) {
					break;
				}

				int2 += int4;
			}
		}

		return int2;
	}

	public void update() {
		if (this.getObjectIndex() != -1) {
			int int1;
			if (!GameServer.bServer) {
				if (this.lightSource != null && !this.lightSource.isInBounds()) {
					this.lightSource = null;
				}

				byte byte1;
				int int2;
				int int3;
				if (this.lightSourceFuel != null && !this.lightSourceFuel.isEmpty() && this.lightSource == null && this.square != null) {
					byte1 = 0;
					int2 = this.calcLightSourceX();
					int3 = this.calcLightSourceY();
					if (IsoWorld.instance.CurrentCell.isInChunkMap(int2, int3)) {
						int1 = this.getLightSourceLife();
						this.setLightSource(new IsoLightSource(int2, int3, (int)this.getZ() + byte1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, int1 > 0 ? int1 : -1));
						this.lightSource.setActive(this.isLightSourceOn());
						IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
					}
				}

				if (this.lightSource != null && this.lightSource.isActive()) {
					byte1 = 0;
					int2 = this.calcLightSourceX();
					int3 = this.calcLightSourceY();
					if (int2 != this.lightSource.x || int3 != this.lightSource.y) {
						this.getCell().removeLamppost(this.lightSource);
						int1 = this.getLightSourceLife();
						this.setLightSource(new IsoLightSource(int2, int3, (int)this.getZ() + byte1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, int1 > 0 ? int1 : -1));
						this.lightSource.setActive(this.isLightSourceOn());
						IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
					}
				}
			}

			if (this.getLifeLeft() > -1.0F) {
				float float1 = (float)GameTime.getInstance().getWorldAgeHours();
				if (this.lastUpdateHours == -1.0F) {
					this.lastUpdateHours = float1;
				} else if (this.lastUpdateHours > float1) {
					this.lastUpdateHours = float1;
				}

				float float2 = float1 - this.lastUpdateHours;
				this.lastUpdateHours = float1;
				if (this.isLightSourceOn()) {
					this.updateAccumulator += float2;
					int1 = (int)Math.floor((double)(this.updateAccumulator / 0.004166667F));
					if (int1 > 0) {
						this.updateAccumulator -= 0.004166667F * (float)int1;
						this.setLifeLeft(this.getLifeLeft() - this.getLifeDelta() * (float)int1);
						if (this.getLifeLeft() <= 0.0F) {
							this.setLifeLeft(0.0F);
							this.toggleLightSource(false);
						}
					}
				} else {
					this.updateAccumulator = 0.0F;
				}
			}

			this.checkHaveElectricity();
		}
	}

	void Damage(int int1) {
		if (this.isThumpable()) {
			this.DirtySlice();
			this.Health -= int1;
		}
	}

	public void destroy() {
		if (!this.destroyed) {
			if (this.getObjectIndex() != -1) {
				if (GameClient.bClient) {
					GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "OnDestroyIsoThumpable", "x", this.square.getX(), "y", this.square.getY(), "z", this.square.getZ(), "index", this.getObjectIndex());
				}

				LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, (Object)null);
				this.Health = 0;
				this.destroyed = true;
				if (this.getObjectIndex() != -1) {
					this.square.transmitRemoveItemFromSquare(this);
				}
			}
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

	public IsoBarricade getBarricadeForCharacter(IsoGameCharacter gameCharacter) {
		return IsoBarricade.GetBarricadeForCharacter(this, gameCharacter);
	}

	public IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter gameCharacter) {
		return IsoBarricade.GetBarricadeOppositeCharacter(this, gameCharacter);
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getName() {
		return this.name;
	}

	public void setIsDoor(Boolean Boolean1) {
		this.isDoor = Boolean1;
	}

	public KahluaTable getTable() {
		return this.table;
	}

	public void setTable(KahluaTable kahluaTable) {
		this.table = kahluaTable;
	}

	public boolean canBePlastered() {
		return this.canBePlastered;
	}

	public void setCanBePlastered(boolean boolean1) {
		this.canBePlastered = boolean1;
	}

	public boolean isPaintable() {
		return this.paintable;
	}

	public void setPaintable(boolean boolean1) {
		this.paintable = boolean1;
	}

	public boolean isLocked() {
		return this.Locked;
	}

	public void setIsLocked(boolean boolean1) {
		this.Locked = boolean1;
	}

	public boolean isThumpable() {
		return this.isBarricaded() ? true : this.isThumpable;
	}

	public void setIsThumpable(boolean boolean1) {
		this.isThumpable = boolean1;
	}

	public void setIsHoppable(boolean boolean1) {
		this.setHoppable(boolean1);
	}

	public IsoSprite getOpenSprite() {
		return this.openSprite;
	}

	public boolean isHoppable() {
		return this.isHoppable;
	}

	public void setHoppable(boolean boolean1) {
		this.isHoppable = boolean1;
	}

	public int getLightSourceRadius() {
		return this.lightSourceRadius;
	}

	public void setLightSourceRadius(int int1) {
		this.lightSourceRadius = int1;
	}

	public int getLightSourceXOffset() {
		return this.lightSourceXOffset;
	}

	public void setLightSourceXOffset(int int1) {
		this.lightSourceXOffset = int1;
	}

	public int getLightSourceYOffset() {
		return this.lightSourceYOffset;
	}

	public void setLightSourceYOffset(int int1) {
		this.lightSourceYOffset = int1;
	}

	public int getLightSourceLife() {
		return this.lightSourceLife;
	}

	public void setLightSourceLife(int int1) {
		this.lightSourceLife = int1;
	}

	public boolean isLightSourceOn() {
		return this.lightSourceOn;
	}

	public void setLightSourceOn(boolean boolean1) {
		this.lightSourceOn = boolean1;
	}

	public IsoLightSource getLightSource() {
		return this.lightSource;
	}

	public void setLightSource(IsoLightSource lightSource) {
		this.lightSource = lightSource;
	}

	public void toggleLightSource(boolean boolean1) {
		this.setLightSourceOn(boolean1);
		if (this.lightSource != null) {
			this.getLightSource().setActive(boolean1);
			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
		}
	}

	public String getLightSourceFuel() {
		return this.lightSourceFuel;
	}

	public void setLightSourceFuel(String string) {
		if (string != null && string.isEmpty()) {
			string = null;
		}

		this.lightSourceFuel = string;
	}

	public float getLifeLeft() {
		return this.lifeLeft;
	}

	public void setLifeLeft(float float1) {
		this.lifeLeft = float1;
	}

	public float getLifeDelta() {
		return this.lifeDelta;
	}

	public void setLifeDelta(float float1) {
		this.lifeDelta = float1;
	}

	public boolean haveFuel() {
		return this.haveFuel;
	}

	public void setHaveFuel(boolean boolean1) {
		this.haveFuel = boolean1;
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		if (this.getSprite() != null && this.getSprite().getProperties().Is("DoubleDoor")) {
			byteBufferWriter.putByte((byte)5);
		} else {
			if (this.open) {
				byteBufferWriter.putByte((byte)1);
			} else if (this.lockedByKey) {
				byteBufferWriter.putByte((byte)3);
			} else {
				byteBufferWriter.putByte((byte)4);
			}
		}
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		if (this.square == null) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
		} else if (this.getObjectIndex() == -1) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else if (this.isDoor()) {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
				this.square.clientModify();
			} else {
				Iterator iterator;
				UdpConnection udpConnection2;
				ByteBufferWriter byteBufferWriter2;
				if (GameServer.bServer && !boolean1) {
					iterator = GameServer.udpEngine.connections.iterator();
					while (iterator.hasNext()) {
						udpConnection2 = (UdpConnection)iterator.next();
						byteBufferWriter2 = udpConnection2.startPacket();
						PacketTypes.doPacket((short)12, byteBufferWriter2);
						this.syncIsoObjectSend(byteBufferWriter2);
						udpConnection2.endPacketImmediate();
					}
				} else if (boolean1) {
					if (byte1 == 1) {
						this.open = true;
						this.sprite = this.openSprite;
						this.Locked = false;
					} else if (byte1 == 0) {
						this.open = false;
						this.sprite = this.closedSprite;
					} else if (byte1 == 3) {
						this.lockedByKey = true;
						this.open = false;
						this.sprite = this.closedSprite;
					} else if (byte1 == 4) {
						this.lockedByKey = false;
						this.open = false;
						this.sprite = this.closedSprite;
					}

					if (GameServer.bServer) {
						iterator = GameServer.udpEngine.connections.iterator();
						while (iterator.hasNext()) {
							udpConnection2 = (UdpConnection)iterator.next();
							if (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
								byteBufferWriter2 = udpConnection2.startPacket();
								PacketTypes.doPacket((short)12, byteBufferWriter2);
								this.syncIsoObjectSend(byteBufferWriter2);
								udpConnection2.endPacketImmediate();
							}
						}

						this.square.revisionUp();
					}

					if (byte1 == 5) {
						IsoDoor.toggleDoubleDoor(this, false);
						if (GameServer.bServer) {
							ServerMap.instance.physicsCheck(this.square.getX(), this.square.getY());
						}
					}

					this.square.InvalidateSpecialObjectPaths();
					this.square.RecalcProperties();
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						LosUtil.cachecleared[int1] = true;
					}

					IsoGridSquare.setRecalcLightTime(-1);
					GameTime.instance.lightSourceUpdate = 100.0F;
					LuaEventManager.triggerEvent("OnContainerUpdate");
				}
			}
		}
	}

	public void addToWorld() {
		super.addToWorld();
		if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
			IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
		}
	}

	public void removeFromWorld() {
		if (this.lightSource != null) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.lightSource);
		}

		super.removeFromWorld();
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		super.saveChange(string, kahluaTable, byteBuffer);
		if ("lightSource".equals(string)) {
			byteBuffer.put((byte)(this.lightSourceOn ? 1 : 0));
			byteBuffer.put((byte)(this.haveFuel ? 1 : 0));
			byteBuffer.putFloat(this.lifeLeft);
			byteBuffer.putFloat(this.lifeDelta);
		} else if ("paintable".equals(string)) {
			byteBuffer.put((byte)(this.isPaintable() ? 1 : 0));
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		super.loadChange(string, byteBuffer);
		if ("lightSource".equals(string)) {
			boolean boolean1 = byteBuffer.get() == 1;
			this.haveFuel = byteBuffer.get() == 1;
			this.lifeLeft = byteBuffer.getFloat();
			this.lifeDelta = byteBuffer.getFloat();
			if (boolean1 != this.lightSourceOn) {
				this.toggleLightSource(boolean1);
			}
		} else if ("paintable".equals(string)) {
			this.setPaintable(byteBuffer.get() == 1);
		}
	}

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

	public IsoGridSquare getInsideSquare() {
		return this.north ? this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
	}

	public IsoGridSquare getOppositeSquare() {
		return this.getInsideSquare();
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

	public void addSheet(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = this.getIndoorSquare();
		IsoDirections directions = IsoDirections.N;
		if (this.north) {
			if (square != this.square) {
				directions = IsoDirections.S;
			}
		} else {
			directions = IsoDirections.W;
			if (square != this.square) {
				directions = IsoDirections.E;
			}
		}

		if (gameCharacter != null) {
			if (this.north) {
				if (gameCharacter.getY() < this.getY()) {
					square = this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
					directions = IsoDirections.S;
				} else {
					square = this.getSquare();
					directions = IsoDirections.N;
				}
			} else if (gameCharacter.getX() < this.getX()) {
				square = this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
				directions = IsoDirections.E;
			} else {
				square = this.getSquare();
				directions = IsoDirections.W;
			}
		}

		if (square != null) {
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
				if (GameServer.bServer) {
					curtain.transmitCompleteItemToClients();
					gameCharacter.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
				} else {
					gameCharacter.getInventory().RemoveOneOf("Sheet");
				}
			}
		}
	}

	public IsoGridSquare getIndoorSquare() {
		if (this.square.getRoom() != null) {
			return this.square;
		} else {
			IsoGridSquare square;
			if (this.north) {
				square = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
			} else {
				square = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
			}

			if (square != null && square.getFloor() != null) {
				if (square.getRoom() != null) {
					return square;
				} else if (this.square.getFloor() == null) {
					return square;
				} else {
					String string = square.getFloor().getSprite().getName();
					return string != null && string.startsWith("carpentry_02_") ? square : this.square;
				}
			} else {
				return this.square;
			}
		}
	}

	public int getKeyId() {
		return this.keyId;
	}

	public void setKeyId(int int1, boolean boolean1) {
		if (boolean1 && this.keyId != int1 && GameClient.bClient) {
			this.keyId = int1;
			this.syncIsoThumpable();
		} else {
			this.keyId = int1;
		}
	}

	public void setKeyId(int int1) {
		this.setKeyId(int1, true);
	}

	public boolean isLockedByKey() {
		return this.lockedByKey;
	}

	public void setLockedByKey(boolean boolean1) {
		boolean boolean2 = boolean1 != this.lockedByKey;
		this.lockedByKey = boolean1;
		if (!GameServer.bServer && boolean2) {
			if (boolean1) {
				this.syncIsoObject(false, (byte)3, (UdpConnection)null, (ByteBuffer)null);
			} else {
				this.syncIsoObject(false, (byte)4, (UdpConnection)null, (ByteBuffer)null);
			}
		}
	}

	public boolean isLockedByPadlock() {
		return this.lockedByPadlock;
	}

	public void syncIsoThumpable() {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.doPacket((short)105, byteBufferWriter);
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		if (byte1 == -1) {
			System.out.println("ERROR: Thumpable door not found on square " + this.square.getX() + ", " + this.square.getY() + ", " + this.square.getZ());
			GameClient.connection.cancelPacket();
		} else {
			byteBufferWriter.putByte(byte1);
			byteBufferWriter.putInt(this.getLockedByCode());
			byteBufferWriter.putByte((byte)(this.lockedByPadlock ? 1 : 0));
			byteBufferWriter.putInt(this.getKeyId());
			GameClient.connection.endPacketImmediate();
		}
	}

	public void setLockedByPadlock(boolean boolean1) {
		if (this.lockedByPadlock != boolean1 && GameClient.bClient) {
			this.lockedByPadlock = boolean1;
			this.syncIsoThumpable();
		} else {
			this.lockedByPadlock = boolean1;
		}
	}

	public boolean canBeLockByPadlock() {
		return this.canBeLockByPadlock;
	}

	public void setCanBeLockByPadlock(boolean boolean1) {
		this.canBeLockByPadlock = boolean1;
	}

	public int getLockedByCode() {
		return this.lockedByCode;
	}

	public void setLockedByCode(int int1) {
		if (this.lockedByCode != int1 && GameClient.bClient) {
			this.lockedByCode = int1;
			this.syncIsoThumpable();
		} else {
			this.lockedByCode = int1;
		}
	}

	public boolean isLockedToCharacter(IsoGameCharacter gameCharacter) {
		if (GameClient.bClient && gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).accessLevel.equals("")) {
			return false;
		} else if (this.getLockedByCode() > 0) {
			return true;
		} else {
			return this.isLockedByPadlock() && (gameCharacter.getInventory() == null || gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null);
		}
	}

	public String getThumpSound() {
		return this.thumpSound;
	}

	public void setThumpSound(String string) {
		this.thumpSound = string;
	}
}
