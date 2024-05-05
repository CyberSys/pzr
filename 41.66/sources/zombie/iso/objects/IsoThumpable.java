package zombie.iso.objects;

import java.io.IOException;
import java.io.PrintStream;
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
import zombie.characters.BaseCharacterSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.BrokenFences;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.world.WorldDictionary;


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
	private int thumpDmg = 8;
	private float crossSpeed = 1.0F;
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
	public static final Vector2 tempo = new Vector2();

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
			this.container = new ItemContainer("crate", this.square, this);
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

	public int getThumpDmg() {
		return this.thumpDmg;
	}

	public void setBreakSound(String string) {
		this.breakSound = string;
	}

	public String getBreakSound() {
		return this.breakSound;
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
		this.closedSprite = IsoSpriteManager.instance.getSprite(string);
		this.sprite = this.closedSprite;
	}

	public void setSpriteFromName(String string) {
		this.sprite = IsoSpriteManager.instance.getSprite(string);
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
		this.openSprite = IsoSpriteManager.instance.getSprite(string2);
		this.closedSprite = IsoSpriteManager.instance.getSprite(string);
		this.table = kahluaTable;
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
	}

	public IsoThumpable(IsoCell cell, IsoGridSquare square, String string, boolean boolean1, KahluaTable kahluaTable) {
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoSpriteManager.instance.getSprite(string);
		this.table = kahluaTable;
		this.sprite = this.closedSprite;
		this.square = square;
		this.north = boolean1;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Long, byteBuffer);
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		if (!bitHeaderRead.equals(0)) {
			this.open = bitHeaderRead.hasFlags(1);
			this.Locked = bitHeaderRead.hasFlags(2);
			this.north = bitHeaderRead.hasFlags(4);
			if (bitHeaderRead.hasFlags(8)) {
				this.MaxHealth = byteBuffer.getInt();
			}

			if (bitHeaderRead.hasFlags(16)) {
				this.Health = byteBuffer.getInt();
			} else {
				this.Health = this.MaxHealth;
			}

			if (bitHeaderRead.hasFlags(32)) {
				this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(64)) {
				this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(128)) {
				this.thumpDmg = byteBuffer.getInt();
			}

			this.isDoor = bitHeaderRead.hasFlags(512);
			this.isDoorFrame = bitHeaderRead.hasFlags(1024);
			this.isCorner = bitHeaderRead.hasFlags(2048);
			this.isStairs = bitHeaderRead.hasFlags(4096);
			this.isContainer = bitHeaderRead.hasFlags(8192);
			this.isFloor = bitHeaderRead.hasFlags(16384);
			this.canBarricade = bitHeaderRead.hasFlags(32768);
			this.canPassThrough = bitHeaderRead.hasFlags(65536);
			this.dismantable = bitHeaderRead.hasFlags(131072);
			this.canBePlastered = bitHeaderRead.hasFlags(262144);
			this.paintable = bitHeaderRead.hasFlags(524288);
			if (bitHeaderRead.hasFlags(1048576)) {
				this.crossSpeed = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(2097152)) {
				if (this.table == null) {
					this.table = LuaManager.platform.newTable();
				}

				this.table.load(byteBuffer, int1);
			}

			if (bitHeaderRead.hasFlags(4194304)) {
				if (this.modData == null) {
					this.modData = LuaManager.platform.newTable();
				}

				this.modData.load(byteBuffer, int1);
			}

			this.blockAllTheSquare = bitHeaderRead.hasFlags(8388608);
			this.isThumpable = bitHeaderRead.hasFlags(16777216);
			this.isHoppable = bitHeaderRead.hasFlags(33554432);
			if (bitHeaderRead.hasFlags(67108864)) {
				this.setLightSourceLife(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(134217728)) {
				this.setLightSourceRadius(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(268435456)) {
				this.setLightSourceXOffset(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(536870912)) {
				this.setLightSourceYOffset(byteBuffer.getInt());
			}

			if (bitHeaderRead.hasFlags(1073741824)) {
				this.setLightSourceFuel(WorldDictionary.getItemTypeFromID(byteBuffer.getShort()));
			}

			if (bitHeaderRead.hasFlags(2147483648L)) {
				this.setLifeDelta(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(4294967296L)) {
				this.setLifeLeft(byteBuffer.getFloat());
			}

			if (bitHeaderRead.hasFlags(8589934592L)) {
				this.keyId = byteBuffer.getInt();
			}

			this.lockedByKey = bitHeaderRead.hasFlags(17179869184L);
			this.lockedByPadlock = bitHeaderRead.hasFlags(34359738368L);
			this.canBeLockByPadlock = bitHeaderRead.hasFlags(68719476736L);
			if (bitHeaderRead.hasFlags(137438953472L)) {
				this.lockedByCode = byteBuffer.getInt();
			}

			if (bitHeaderRead.hasFlags(274877906944L)) {
				this.thumpSound = GameWindow.ReadString(byteBuffer);
				if ("thumpa2".equals(this.thumpSound)) {
					this.thumpSound = "ZombieThumpGeneric";
				}

				if ("metalthump".equals(this.thumpSound)) {
					this.thumpSound = "ZombieThumpMetal";
				}
			}

			if (bitHeaderRead.hasFlags(549755813888L)) {
				this.lastUpdateHours = byteBuffer.getFloat();
			}

			if (int1 >= 183) {
				if (bitHeaderRead.hasFlags(1099511627776L)) {
					this.haveFuel = true;
				}

				if (bitHeaderRead.hasFlags(2199023255552L)) {
					this.lightSourceOn = true;
				}
			}
		}

		bitHeaderRead.release();
		if (this.getLightSourceFuel() != null) {
			boolean boolean2 = this.isLightSourceOn();
			this.createLightSource(this.getLightSourceRadius(), this.getLightSourceXOffset(), this.getLightSourceYOffset(), 0, this.getLightSourceLife(), this.getLightSourceFuel(), (InventoryItem)null, (IsoGameCharacter)null);
			if (this.lightSource != null) {
				this.getLightSource().setActive(boolean2);
			}

			this.setLightSourceOn(boolean2);
		}

		if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequestLoad(this.square);
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Long, byteBuffer);
		if (this.open) {
			bitHeaderWrite.addFlags(1);
		}

		if (this.Locked) {
			bitHeaderWrite.addFlags(2);
		}

		if (this.north) {
			bitHeaderWrite.addFlags(4);
		}

		if (this.MaxHealth != 500) {
			bitHeaderWrite.addFlags(8);
			byteBuffer.putInt(this.MaxHealth);
		}

		if (this.Health != this.MaxHealth) {
			bitHeaderWrite.addFlags(16);
			byteBuffer.putInt(this.Health);
		}

		if (this.closedSprite != null) {
			bitHeaderWrite.addFlags(32);
			byteBuffer.putInt(this.closedSprite.ID);
		}

		if (this.openSprite != null) {
			bitHeaderWrite.addFlags(64);
			byteBuffer.putInt(this.openSprite.ID);
		}

		if (this.thumpDmg != 8) {
			bitHeaderWrite.addFlags(128);
			byteBuffer.putInt(this.thumpDmg);
		}

		if (this.isDoor) {
			bitHeaderWrite.addFlags(512);
		}

		if (this.isDoorFrame) {
			bitHeaderWrite.addFlags(1024);
		}

		if (this.isCorner) {
			bitHeaderWrite.addFlags(2048);
		}

		if (this.isStairs) {
			bitHeaderWrite.addFlags(4096);
		}

		if (this.isContainer) {
			bitHeaderWrite.addFlags(8192);
		}

		if (this.isFloor) {
			bitHeaderWrite.addFlags(16384);
		}

		if (this.canBarricade) {
			bitHeaderWrite.addFlags(32768);
		}

		if (this.canPassThrough) {
			bitHeaderWrite.addFlags(65536);
		}

		if (this.dismantable) {
			bitHeaderWrite.addFlags(131072);
		}

		if (this.canBePlastered) {
			bitHeaderWrite.addFlags(262144);
		}

		if (this.paintable) {
			bitHeaderWrite.addFlags(524288);
		}

		if (this.crossSpeed != 1.0F) {
			bitHeaderWrite.addFlags(1048576);
			byteBuffer.putFloat(this.crossSpeed);
		}

		if (this.table != null && !this.table.isEmpty()) {
			bitHeaderWrite.addFlags(2097152);
			this.table.save(byteBuffer);
		}

		if (this.modData != null && !this.modData.isEmpty()) {
			bitHeaderWrite.addFlags(4194304);
			this.modData.save(byteBuffer);
		}

		if (this.blockAllTheSquare) {
			bitHeaderWrite.addFlags(8388608);
		}

		if (this.isThumpable) {
			bitHeaderWrite.addFlags(16777216);
		}

		if (this.isHoppable) {
			bitHeaderWrite.addFlags(33554432);
		}

		if (this.getLightSourceLife() != -1) {
			bitHeaderWrite.addFlags(67108864);
			byteBuffer.putInt(this.getLightSourceLife());
		}

		if (this.getLightSourceRadius() != -1) {
			bitHeaderWrite.addFlags(134217728);
			byteBuffer.putInt(this.getLightSourceRadius());
		}

		if (this.getLightSourceXOffset() != 0) {
			bitHeaderWrite.addFlags(268435456);
			byteBuffer.putInt(this.getLightSourceXOffset());
		}

		if (this.getLightSourceYOffset() != 0) {
			bitHeaderWrite.addFlags(536870912);
			byteBuffer.putInt(this.getLightSourceYOffset());
		}

		if (this.getLightSourceFuel() != null) {
			bitHeaderWrite.addFlags(1073741824);
			byteBuffer.putShort(WorldDictionary.getItemRegistryID(this.getLightSourceFuel()));
		}

		if (this.getLifeDelta() != 0.0F) {
			bitHeaderWrite.addFlags(2147483648L);
			byteBuffer.putFloat(this.getLifeDelta());
		}

		if (this.getLifeLeft() != -1.0F) {
			bitHeaderWrite.addFlags(4294967296L);
			byteBuffer.putFloat(this.getLifeLeft());
		}

		if (this.keyId != -1) {
			bitHeaderWrite.addFlags(8589934592L);
			byteBuffer.putInt(this.keyId);
		}

		if (this.isLockedByKey()) {
			bitHeaderWrite.addFlags(17179869184L);
		}

		if (this.isLockedByPadlock()) {
			bitHeaderWrite.addFlags(34359738368L);
		}

		if (this.canBeLockByPadlock()) {
			bitHeaderWrite.addFlags(68719476736L);
		}

		if (this.getLockedByCode() != 0) {
			bitHeaderWrite.addFlags(137438953472L);
			byteBuffer.putInt(this.getLockedByCode());
		}

		if (!this.thumpSound.equals("ZombieThumbGeneric")) {
			bitHeaderWrite.addFlags(274877906944L);
			GameWindow.WriteString(byteBuffer, this.thumpSound);
		}

		if (this.lastUpdateHours != -1.0F) {
			bitHeaderWrite.addFlags(549755813888L);
			byteBuffer.putFloat(this.lastUpdateHours);
		}

		if (this.haveFuel) {
			bitHeaderWrite.addFlags(1099511627776L);
		}

		if (this.lightSourceOn) {
			bitHeaderWrite.addFlags(2199023255552L);
		}

		bitHeaderWrite.write();
		bitHeaderWrite.release();
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
		return false;
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
				boolean boolean2 = this.sprite != null && this.sprite.getProperties().Is("doorTrans");
				if (square == this.square) {
					if (boolean1 && square2.getY() < square.getY()) {
						if (boolean2) {
							return IsoObject.VisionResult.Unblocked;
						}

						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}

					if (!boolean1 && square2.getX() < square.getX()) {
						if (boolean2) {
							return IsoObject.VisionResult.Unblocked;
						}

						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}
				} else {
					if (boolean1 && square2.getY() > square.getY()) {
						if (boolean2) {
							return IsoObject.VisionResult.Unblocked;
						}

						if (this.isWindow()) {
							return IsoObject.VisionResult.Unblocked;
						}

						return IsoObject.VisionResult.Blocked;
					}

					if (!boolean1 && square2.getX() > square.getX()) {
						if (boolean2) {
							return IsoObject.VisionResult.Unblocked;
						}

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

			boolean boolean1 = BrokenFences.getInstance().isBreakableObject(this);
			if (movingObject instanceof IsoZombie) {
				if (((IsoZombie)movingObject).cognition == 1 && this.isDoor() && !this.IsOpen() && !this.isLocked()) {
					this.ToggleDoor((IsoGameCharacter)movingObject);
					return;
				}

				int int1 = movingObject.getCurrentSquare().getMovingObjects().size();
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
				int int3;
				if (int1 >= int2) {
					int3 = 1 * ThumpState.getFastForwardDamageMultiplier();
					this.Health -= int3;
				} else {
					this.partialThumpDmg += (float)int1 / (float)int2 * (float)ThumpState.getFastForwardDamageMultiplier();
					if ((int)this.partialThumpDmg > 0) {
						int3 = (int)this.partialThumpDmg;
						this.Health -= int3;
						this.partialThumpDmg -= (float)int3;
					}
				}

				WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
				if (this.isDoor()) {
					this.setRenderEffect(RenderEffectType.Hit_Door, true);
				}
			}

			if (this.Health <= 0) {
				((IsoGameCharacter)movingObject).getEmitter().playSound(this.breakSound, this);
				if (GameServer.bServer) {
					GameServer.PlayWorldSoundServer((IsoGameCharacter)movingObject, this.breakSound, false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
				}

				WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
				movingObject.setThumpTarget((Thumpable)null);
				if (IsoDoor.destroyDoubleDoor(this)) {
					return;
				}

				if (IsoDoor.destroyGarageDoor(this)) {
					return;
				}

				if (boolean1) {
					PropertyContainer propertyContainer = this.getProperties();
					IsoDirections directions;
					if (propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW)) {
						directions = movingObject.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
					} else if (propertyContainer.Is(IsoFlagType.collideN)) {
						directions = movingObject.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
					} else {
						directions = movingObject.getX() >= this.getX() ? IsoDirections.W : IsoDirections.E;
					}

					BrokenFences.getInstance().destroyFence(this, directions);
					return;
				}

				this.destroy();
			}
		}
	}

	public Thumpable getThumpableFor(IsoGameCharacter gameCharacter) {
		if (this.isDoor() || this.isWindow()) {
			IsoBarricade barricade = this.getBarricadeForCharacter(gameCharacter);
			if (barricade != null) {
				return barricade;
			}

			barricade = this.getBarricadeOppositeCharacter(gameCharacter);
			if (barricade != null) {
				return barricade;
			}
		}

		boolean boolean1 = this.isThumpable;
		boolean boolean2 = gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).isCrawling();
		if (!boolean1 && boolean2 && BrokenFences.getInstance().isBreakableObject(this)) {
			boolean1 = true;
		}

		if (!boolean1 && boolean2 && this.isHoppable()) {
			boolean1 = true;
		}

		if (boolean1 && !this.isDestroyed()) {
			if ((!this.isDoor() || !this.IsOpen()) && !this.isWindow()) {
				return !boolean2 && this.isHoppable() ? null : this;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public float getThumpCondition() {
		return (float)PZMath.clamp(this.Health, 0, this.MaxHealth) / (float)this.MaxHealth;
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (GameClient.bClient) {
			if (player != null) {
				GameClient.instance.sendWeaponHit(player, handWeapon, this);
			}

			if (this.isDoor()) {
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			}
		} else {
			Thumpable thumpable = this.getThumpableFor(gameCharacter);
			if (thumpable != null) {
				if (thumpable instanceof IsoBarricade) {
					((IsoBarricade)thumpable).WeaponHit(gameCharacter, handWeapon);
				} else {
					this.Damage(handWeapon.getDoorDamage());
					if (handWeapon.getDoorHitSound() != null) {
						if (player != null) {
							player.setMeleeHitSurface(this.getSoundPrefix());
						}

						gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
						if (GameServer.bServer) {
							GameServer.PlayWorldSoundServer(gameCharacter, handWeapon.getDoorHitSound(), false, this.getSquare(), 0.2F, 20.0F, 1.0F, false);
						}
					}

					WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
					if (this.isDoor()) {
						this.setRenderEffect(RenderEffectType.Hit_Door, true);
					}

					if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
						gameCharacter.getEmitter().playSound(this.breakSound, this);
						WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
						if (GameClient.bClient) {
							GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "OnDestroyIsoThumpable", "x", (int)this.getX(), "y", (int)this.getY(), "z", (int)this.getZ(), "index", this.getObjectIndex());
						}

						LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, (Object)null);
						if (IsoDoor.destroyDoubleDoor(this)) {
							return;
						}

						if (IsoDoor.destroyGarageDoor(this)) {
							return;
						}

						this.destroyed = true;
						if (this.getObjectIndex() != -1) {
							this.square.transmitRemoveItemFromSquare(this);
						}
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
				this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
				gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0F);
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			}
		} else if (this.isLockedByKey() && gameCharacter instanceof IsoPlayer && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) && gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null) {
			this.playDoorSound(gameCharacter.getEmitter(), "Locked");
			this.setRenderEffect(RenderEffectType.Hit_Door, true);
		} else {
			if (this.isLockedByKey() && gameCharacter instanceof IsoPlayer && gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) != null) {
				this.playDoorSound(gameCharacter.getEmitter(), "Unlock");
				this.setIsLocked(false);
				this.setLockedByKey(false);
			}

			this.DirtySlice();
			this.square.InvalidateSpecialObjectPaths();
			if (this.Locked && gameCharacter instanceof IsoPlayer && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) && !this.open) {
				this.playDoorSound(gameCharacter.getEmitter(), "Locked");
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			} else {
				if (gameCharacter instanceof IsoPlayer) {
				}

				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					LosUtil.cachecleared[int1] = true;
				}

				IsoGridSquare.setRecalcLightTime(-1);
				GameTime.instance.lightSourceUpdate = 100.0F;
				if (this.getSprite().getProperties().Is("DoubleDoor")) {
					if (IsoDoor.isDoubleDoorObstructed(this)) {
						if (gameCharacter != null) {
							this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
							gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
						}
					} else {
						boolean boolean1 = this.open;
						IsoDoor.toggleDoubleDoor(this, true);
						if (boolean1 != this.open) {
							this.playDoorSound(gameCharacter.getEmitter(), this.open ? "Open" : "Close");
						}
					}
				} else if (this.isObstructed()) {
					if (gameCharacter != null) {
						this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
						gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
					}
				} else {
					this.sprite = this.closedSprite;
					this.open = !this.open;
					this.setLockedByKey(false);
					if (this.open) {
						this.playDoorSound(gameCharacter.getEmitter(), "Open");
						this.sprite = this.openSprite;
					} else {
						this.playDoorSound(gameCharacter.getEmitter(), "Close");
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

			gameCharacter.removeFromHands(inventoryItem);
			IsoWorldInventoryObject worldInventoryObject = inventoryItem.getWorldItem();
			if (worldInventoryObject != null) {
				if (worldInventoryObject.getSquare() != null) {
					worldInventoryObject.getSquare().transmitRemoveItemFromSquare(worldInventoryObject);
					LuaEventManager.triggerEvent("OnContainerUpdate");
				}
			} else if (inventoryItem.getContainer() != null) {
				inventoryItem.getContainer().Remove(inventoryItem);
			}
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
				gameCharacter.removeFromHands(inventoryItem);
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
					int1 = this.calcLightSourceY();
					if (IsoWorld.instance.CurrentCell.isInChunkMap(int2, int1)) {
						int3 = this.getLightSourceLife();
						this.setLightSource(new IsoLightSource(int2, int1, (int)this.getZ() + byte1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, int3 > 0 ? int3 : -1));
						this.lightSource.setActive(this.isLightSourceOn());
						IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
					}
				}

				if (this.lightSource != null && this.lightSource.isActive()) {
					byte1 = 0;
					int2 = this.calcLightSourceX();
					int1 = this.calcLightSourceY();
					if (int2 != this.lightSource.x || int1 != this.lightSource.y) {
						this.getCell().removeLamppost(this.lightSource);
						int3 = this.getLightSourceLife();
						this.setLightSource(new IsoLightSource(int2, int1, (int)this.getZ() + byte1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, int3 > 0 ? int3 : -1));
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

	public boolean isBarricadeAllowed() {
		return this.canBarricade;
	}

	public IsoBarricade getBarricadeForCharacter(IsoGameCharacter gameCharacter) {
		return IsoBarricade.GetBarricadeForCharacter(this, gameCharacter);
	}

	public IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter gameCharacter) {
		return IsoBarricade.GetBarricadeOppositeCharacter(this, gameCharacter);
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
		if (this.isDoor() && !this.IsOpen() && this.closedSprite != null) {
			PropertyContainer propertyContainer = this.closedSprite.getProperties();
			return propertyContainer.Is(IsoFlagType.HoppableN) || propertyContainer.Is(IsoFlagType.HoppableW);
		} else {
			return this.sprite == null || !this.sprite.getProperties().Is(IsoFlagType.HoppableN) && !this.sprite.getProperties().Is(IsoFlagType.HoppableW) ? this.isHoppable : true;
		}
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
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putBoolean(this.open);
		byteBufferWriter.putBoolean(this.Locked);
		byteBufferWriter.putBoolean(this.lockedByKey);
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		if (this.square == null) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
		} else if (this.getObjectIndex() == -1) {
			PrintStream printStream = System.out;
			String string = this.getClass().getSimpleName();
			printStream.println("ERROR: " + string + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else if (this.isDoor()) {
			boolean boolean2 = byteBuffer != null && byteBuffer.get() == 1;
			boolean boolean3 = byteBuffer != null && byteBuffer.get() == 1;
			boolean boolean4 = byteBuffer != null && byteBuffer.get() == 1;
			short short1 = -1;
			if ((GameServer.bServer || GameClient.bClient) && byteBuffer != null) {
				short1 = byteBuffer.getShort();
			}

			if (GameClient.bClient && !boolean1) {
				short1 = IsoPlayer.getInstance().getOnlineID();
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				byteBufferWriter.putShort(short1);
				PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
			} else {
				UdpConnection udpConnection2;
				ByteBufferWriter byteBufferWriter2;
				Iterator iterator;
				if (GameServer.bServer && !boolean1) {
					iterator = GameServer.udpEngine.connections.iterator();
					while (iterator.hasNext()) {
						udpConnection2 = (UdpConnection)iterator.next();
						byteBufferWriter2 = udpConnection2.startPacket();
						PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
						this.syncIsoObjectSend(byteBufferWriter2);
						byteBufferWriter2.putShort(short1);
						PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
					}
				} else if (boolean1) {
					if (GameClient.bClient && short1 != -1) {
						IsoPlayer player = (IsoPlayer)GameClient.IDToPlayerMap.get(short1);
						if (player != null) {
							player.networkAI.setNoCollision(1000L);
						}
					}

					if (IsoDoor.getDoubleDoorIndex(this) != -1) {
						if (boolean2 != this.open) {
							IsoDoor.toggleDoubleDoor(this, false);
						}
					} else if (boolean2) {
						this.open = true;
						this.sprite = this.openSprite;
					} else {
						this.open = false;
						this.sprite = this.closedSprite;
					}

					this.Locked = boolean3;
					this.lockedByKey = boolean4;
					if (GameServer.bServer) {
						iterator = GameServer.udpEngine.connections.iterator();
						while (iterator.hasNext()) {
							udpConnection2 = (UdpConnection)iterator.next();
							if (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
								byteBufferWriter2 = udpConnection2.startPacket();
								PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
								this.syncIsoObjectSend(byteBufferWriter2);
								byteBufferWriter2.putShort(short1);
								PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
							}
						}
					}

					this.square.InvalidateSpecialObjectPaths();
					this.square.RecalcProperties();
					this.square.RecalcAllWithNeighbours(true);
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
		this.getCell().addToProcessIsoObject(this);
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
		IsoGridSquare square = this.getOppositeSquare();
		if (square != null) {
			IsoCurtain curtain = square.getCurtain(this.getNorth() ? IsoObjectType.curtainS : IsoObjectType.curtainE);
			if (curtain != null) {
				return curtain;
			}
		}

		return this.getSquare().getCurtain(this.getNorth() ? IsoObjectType.curtainN : IsoObjectType.curtainW);
	}

	public IsoGridSquare getInsideSquare() {
		return this.north ? this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
	}

	public IsoGridSquare getOppositeSquare() {
		return this.getInsideSquare();
	}

	public boolean isAdjacentToSquare(IsoGridSquare square) {
		IsoGridSquare square2 = this.getSquare();
		if (square2 != null && square != null) {
			int int1 = square2.x - square.x;
			int int2 = square2.y - square.y;
			int int3 = square2.x;
			int int4 = square2.x;
			int int5 = square2.y;
			int int6 = square2.y;
			IsoGridSquare square3 = square2;
			switch (this.getSpriteEdge(false)) {
			case N: 
				--int3;
				++int4;
				--int5;
				if (int2 == 1) {
					square3 = square2.getAdjacentSquare(IsoDirections.N);
				}

				break;
			
			case S: 
				--int3;
				++int4;
				++int6;
				if (int2 == -1) {
					square3 = square2.getAdjacentSquare(IsoDirections.S);
				}

				break;
			
			case W: 
				--int5;
				++int6;
				--int3;
				if (int1 == 1) {
					square3 = square2.getAdjacentSquare(IsoDirections.W);
				}

				break;
			
			case E: 
				--int5;
				++int6;
				++int4;
				if (int1 == -1) {
					square3 = square2.getAdjacentSquare(IsoDirections.E);
				}

				break;
			
			default: 
				return false;
			
			}

			if (square.x >= int3 && square.x <= int4 && square.y >= int5 && square.y <= int6) {
				return !square3.isSomethingTo(square);
			} else {
				return false;
			}
		} else {
			return false;
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

	public void addSheet(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = this.getIndoorSquare();
		IsoObjectType objectType;
		if (this.north) {
			objectType = IsoObjectType.curtainN;
			if (square != this.square) {
				objectType = IsoObjectType.curtainS;
			}
		} else {
			objectType = IsoObjectType.curtainW;
			if (square != this.square) {
				objectType = IsoObjectType.curtainE;
			}
		}

		if (gameCharacter != null) {
			if (this.north) {
				if (gameCharacter.getY() < this.getY()) {
					square = this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
					objectType = IsoObjectType.curtainS;
				} else {
					square = this.getSquare();
					objectType = IsoObjectType.curtainN;
				}
			} else if (gameCharacter.getX() < this.getX()) {
				square = this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
				objectType = IsoObjectType.curtainE;
			} else {
				square = this.getSquare();
				objectType = IsoObjectType.curtainW;
			}
		}

		if (square != null) {
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
					if (GameServer.bServer) {
						curtain.transmitCompleteItemToClients();
						gameCharacter.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
					} else {
						gameCharacter.getInventory().RemoveOneOf("Sheet");
					}
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
		this.setIsLocked(boolean1);
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
		PacketTypes.PacketType.SyncThumpable.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		if (byte1 == -1) {
			PrintStream printStream = System.out;
			int int1 = this.square.getX();
			printStream.println("ERROR: Thumpable door not found on square " + int1 + ", " + this.square.getY() + ", " + this.square.getZ());
			GameClient.connection.cancelPacket();
		} else {
			byteBufferWriter.putByte(byte1);
			byteBufferWriter.putInt(this.getLockedByCode());
			byteBufferWriter.putByte((byte)(this.lockedByPadlock ? 1 : 0));
			byteBufferWriter.putInt(this.getKeyId());
			PacketTypes.PacketType.SyncThumpable.send(GameClient.connection);
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

	public boolean canClimbOver(IsoGameCharacter gameCharacter) {
		if (this.square == null) {
			return false;
		} else if (!this.isHoppable()) {
			return false;
		} else {
			return gameCharacter == null || IsoWindow.canClimbThroughHelper(gameCharacter, this.getSquare(), this.getOppositeSquare(), this.north);
		}
	}

	public boolean canClimbThrough(IsoGameCharacter gameCharacter) {
		if (this.square == null) {
			return false;
		} else if (!this.isWindow()) {
			return false;
		} else if (this.isBarricaded()) {
			return false;
		} else {
			return gameCharacter == null || IsoWindow.canClimbThroughHelper(gameCharacter, this.getSquare(), this.getOppositeSquare(), this.north);
		}
	}

	public String getThumpSound() {
		return this.thumpSound;
	}

	public void setThumpSound(String string) {
		this.thumpSound = string;
	}

	public IsoObject getRenderEffectMaster() {
		int int1 = IsoDoor.getDoubleDoorIndex(this);
		IsoObject object;
		if (int1 != -1) {
			object = null;
			if (int1 == 2) {
				object = IsoDoor.getDoubleDoorObject(this, 1);
			} else if (int1 == 3) {
				object = IsoDoor.getDoubleDoorObject(this, 4);
			}

			if (object != null) {
				return object;
			}
		} else {
			object = IsoDoor.getGarageDoorFirst(this);
			if (object != null) {
				return object;
			}
		}

		return this;
	}

	public IsoDirections getSpriteEdge(boolean boolean1) {
		if (!this.isDoor() && !this.isWindow()) {
			return null;
		} else if (this.open && !boolean1) {
			PropertyContainer propertyContainer = this.getProperties();
			if (propertyContainer != null && propertyContainer.Is(IsoFlagType.attachedE)) {
				return IsoDirections.E;
			} else if (propertyContainer != null && propertyContainer.Is(IsoFlagType.attachedS)) {
				return IsoDirections.S;
			} else {
				return this.north ? IsoDirections.W : IsoDirections.N;
			}
		} else {
			return this.north ? IsoDirections.N : IsoDirections.W;
		}
	}

	private String getSoundPrefix() {
		if (this.closedSprite == null) {
			return "WoodDoor";
		} else {
			PropertyContainer propertyContainer = this.closedSprite.getProperties();
			return propertyContainer.Is("DoorSound") ? propertyContainer.Val("DoorSound") : "WoodDoor";
		}
	}

	private void playDoorSound(BaseCharacterSoundEmitter baseCharacterSoundEmitter, String string) {
		baseCharacterSoundEmitter.playSound(this.getSoundPrefix() + string, this);
	}
}
