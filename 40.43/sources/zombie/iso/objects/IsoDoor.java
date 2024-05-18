package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.bucket.BucketManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Key;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;


public class IsoDoor extends IsoObject implements BarricadeAble,Thumpable {
	public int Health = 500;
	private boolean lockedByKey = false;
	private boolean haveKey = false;
	public boolean Locked = false;
	public int MaxHealth = 500;
	public int PushedMaxStrength = 0;
	public int PushedStrength = 0;
	public IsoDoor.DoorType type;
	IsoSpriteInstance barricadeSprite;
	IsoSprite closedSprite;
	public boolean north;
	int gid;
	public boolean open;
	IsoSprite openSprite;
	private boolean destroyed;
	private boolean bHasCurtain;
	private boolean bCurtainInside;
	private boolean bCurtainOpen;
	public int OldNumPlanks;
	KahluaTable table;
	public static Vector2 tempo = new Vector2();
	private IsoSprite curtainN;
	private IsoSprite curtainS;
	private IsoSprite curtainW;
	private IsoSprite curtainE;
	private IsoSprite curtainNopen;
	private IsoSprite curtainSopen;
	private IsoSprite curtainWopen;
	private IsoSprite curtainEopen;
	private static final int[] DoubleDoorNorthSpriteOffset = new int[]{5, 3, 4, 4};
	private static final int[] DoubleDoorWestSpriteOffset = new int[]{4, 4, 5, 3};
	private static final int[] DoubleDoorNorthClosedXOffset = new int[]{0, 1, 2, 3};
	private static final int[] DoubleDoorNorthOpenXOffset = new int[]{0, 0, 3, 3};
	private static final int[] DoubleDoorNorthClosedYOffset = new int[]{0, 0, 0, 0};
	private static final int[] DoubleDoorNorthOpenYOffset = new int[]{0, 1, 1, 0};
	private static final int[] DoubleDoorWestClosedXOffset = new int[]{0, 0, 0, 0};
	private static final int[] DoubleDoorWestOpenXOffset = new int[]{0, 1, 1, 0};
	private static final int[] DoubleDoorWestClosedYOffset = new int[]{0, -1, -2, -3};
	private static final int[] DoubleDoorWestOpenYOffset = new int[]{0, 0, -3, -3};

	public IsoDoor(IsoCell cell) {
		super(cell);
		this.type = IsoDoor.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.OldNumPlanks = 0;
	}

	public String getObjectName() {
		return "Door";
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2) {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoGameCharacter gameCharacter = IsoCamera.frameState.CamCharacter;
		Key key = Key.highlightDoor[int1];
		if (key != null && float1 >= gameCharacter.getX() - 20.0F && float2 >= gameCharacter.getY() - 20.0F && float1 < gameCharacter.getX() + 20.0F && float2 < gameCharacter.getY() + 20.0F) {
			boolean boolean3 = this.square.isSeen(int1);
			if (!boolean3) {
				IsoGridSquare square = this.getOppositeSquare();
				boolean3 = square != null && square.isSeen(int1);
			}

			if (boolean3) {
				this.checkKeyId();
				if (this.getKeyId() == key.getKeyId()) {
					this.setHighlighted(true);
				}
			}
		}

		if (Core.TileScale == 1) {
			if (this.bHasCurtain && this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && this.open) {
					(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -16 : -18), this.offsetY + (float)(this.bCurtainOpen ? -14 : -15), colorInfo);
				}

				if (!this.north && this.open) {
					(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX + 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -14 : -14), colorInfo);
				}
			}

			if (this.bHasCurtain && !this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX - 1.0F - 1.0F, this.offsetY + -15.0F, colorInfo);
				}

				if (!this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -12 : -14), this.offsetY + (float)(this.bCurtainOpen ? -14 : -15), colorInfo);
				}
			}

			super.render(float1, float2, float3, colorInfo, boolean1, boolean2);
			if (this.bHasCurtain && this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 10.0F - 1.0F, this.offsetY + -10.0F, colorInfo);
				}

				if (!this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 2.0F - 1.0F, this.offsetY + -10.0F, colorInfo);
				}
			}

			if (this.bHasCurtain && !this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && this.open) {
					(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 9.0F, this.offsetY + -10.0F, colorInfo);
				}

				if (!this.north && this.open) {
					(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 4.0F, this.offsetY + (float)(this.bCurtainOpen ? -10 : -10), colorInfo);
				}
			}
		} else {
			if (this.bHasCurtain && this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && this.open) {
					(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -32 : -37), this.offsetY + (float)(this.bCurtainOpen ? -28 : -31), colorInfo);
				}

				if (!this.north && this.open) {
					(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX + 7.0F, this.offsetY + (float)(this.bCurtainOpen ? -28 : -28), colorInfo);
				}
			}

			if (this.bHasCurtain && !this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX - 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -30 : -30), colorInfo);
				}

				if (!this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -22 : -26), this.offsetY + (float)(this.bCurtainOpen ? -28 : -31), colorInfo);
				}
			}

			super.render(float1, float2, float3, colorInfo, boolean1, boolean2);
			if (this.bHasCurtain && this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 20.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo);
				}

				if (!this.north && !this.open) {
					(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 5.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo);
				}
			}

			if (this.bHasCurtain && !this.bCurtainInside) {
				this.initCurtainSprites();
				if (this.north && this.open) {
					(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 19.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo);
				}

				if (!this.north && this.open) {
					(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 8.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo);
				}
			}
		}
	}

	public IsoDoor(IsoCell cell, IsoGridSquare square, IsoSprite sprite, boolean boolean1) {
		this.type = IsoDoor.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.OldNumPlanks = 0;
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
			if (this.Locked && Rand.Next(3) == 0) {
				this.lockedByKey = true;
			}
		}

		if (this.getProperties().Is("forceLocked")) {
			this.Locked = true;
			this.lockedByKey = true;
		}
	}

	public IsoDoor(IsoCell cell, IsoGridSquare square, String string, boolean boolean1) {
		this.type = IsoDoor.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.OldNumPlanks = 0;
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

	public IsoDoor(IsoCell cell, IsoGridSquare square, String string, boolean boolean1, KahluaTable kahluaTable) {
		this.type = IsoDoor.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.OldNumPlanks = 0;
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
		this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		if (int1 >= 57) {
			this.keyId = byteBuffer.getInt();
			this.lockedByKey = byteBuffer.get() == 1;
		}

		if (int1 >= 80) {
			byte byte1 = byteBuffer.get();
			if ((byte1 & 1) != 0) {
				this.bHasCurtain = true;
				this.bCurtainOpen = (byte1 & 2) != 0;
				this.bCurtainInside = (byte1 & 4) != 0;
			}
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
		byteBuffer.putInt(this.closedSprite.ID);
		byteBuffer.putInt(this.openSprite.ID);
		byteBuffer.putInt(this.getKeyId());
		byteBuffer.put((byte)(this.isLockedByKey() ? 1 : 0));
		byte byte1 = 0;
		if (this.bHasCurtain) {
			byte1 = (byte)(byte1 | 1);
			if (this.bCurtainOpen) {
				byte1 = (byte)(byte1 | 2);
			}

			if (this.bCurtainInside) {
				byte1 = (byte)(byte1 | 4);
			}
		}

		byteBuffer.put(byte1);
	}

	public void saveState(ByteBuffer byteBuffer) {
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
		byteBuffer.put((byte)(this.lockedByKey ? 1 : 0));
	}

	public void loadState(ByteBuffer byteBuffer) {
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		boolean boolean3 = byteBuffer.get() == 1;
		if (boolean1 != this.open) {
			this.open = boolean1;
			this.sprite = boolean1 ? this.openSprite : this.closedSprite;
		}

		if (boolean2 != this.Locked) {
			this.Locked = boolean2;
		}

		if (boolean3 != this.lockedByKey) {
			this.lockedByKey = boolean3;
		}
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
		this.DirtySlice();
		IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
		if (!(IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.0F) || this.square.getZ() != square.getZ() || this.getOppositeSquare() != square && this.square.isSomethingTo(square)) {
			return false;
		} else if (Keyboard.isKeyDown(42)) {
			if (this.bHasCurtain && square == this.getSheetSquare()) {
				this.toggleCurtain();
			}

			return true;
		} else {
			this.ToggleDoor(IsoPlayer.getInstance());
			return true;
		}
	}

	public boolean TestPathfindCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		boolean boolean1 = this.north;
		if (!this.isBarricaded()) {
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
			return false;
		} else {
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
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		boolean boolean1 = this.sprite != null && this.sprite.getProperties().Is("doorTrans");
		boolean boolean2 = this.north;
		if (this.open) {
			boolean2 = !boolean2;
		}

		if (square2.getZ() != square.getZ()) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			if (square == this.square) {
				if (boolean2 && square2.getY() < square.getY()) {
					if (!boolean1 || this.bHasCurtain && !this.bCurtainOpen) {
						return IsoObject.VisionResult.Blocked;
					}

					return IsoObject.VisionResult.Unblocked;
				}

				if (!boolean2 && square2.getX() < square.getX()) {
					if (!boolean1 || this.bHasCurtain && !this.bCurtainOpen) {
						return IsoObject.VisionResult.Blocked;
					}

					return IsoObject.VisionResult.Unblocked;
				}
			} else {
				if (boolean2 && square2.getY() > square.getY()) {
					if (!boolean1 || this.bHasCurtain && !this.bCurtainOpen) {
						return IsoObject.VisionResult.Blocked;
					}

					return IsoObject.VisionResult.Unblocked;
				}

				if (!boolean2 && square2.getX() > square.getX()) {
					if (!boolean1 || this.bHasCurtain && !this.bCurtainOpen) {
						return IsoObject.VisionResult.Blocked;
					}

					return IsoObject.VisionResult.Unblocked;
				}
			}

			return IsoObject.VisionResult.NoEffect;
		}
	}

	public void Thump(IsoMovingObject movingObject) {
		if (!this.isDestroyed()) {
			if (movingObject instanceof IsoZombie) {
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

				if (((IsoZombie)movingObject).cognition == 1 && !this.open && (!this.Locked || movingObject.getCurrentSquare() != null && !movingObject.getCurrentSquare().Is(IsoFlagType.exterior))) {
					this.ToggleDoor((IsoGameCharacter)movingObject);
					if (this.open) {
						return;
					}
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

				int int2 = ThumpState.getFastForwardDamageMultiplier();
				int int3 = ((IsoZombie)movingObject).strength;
				if (int1 >= int3) {
					this.DirtySlice();
					this.Damage(1 * int2);
					if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
						this.Damage(2 * int2);
					}
				}

				if (Core.GameMode.equals("LastStand")) {
					this.Damage(1 * int2);
				}

				WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			}

			if (this.Health <= 0) {
				((IsoGameCharacter)((IsoGameCharacter)movingObject)).getEmitter().playSound("BreakDoor");
				if (GameServer.bServer) {
					GameServer.PlayWorldSoundServer("BreakDoor", false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
				}

				WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
				movingObject.setThumpTarget((Thumpable)null);
				int int4 = getDoubleDoorIndex(this);
				if (int4 == 1 || int4 == 4) {
					IsoObject object = getDoubleDoorObject(this, int4 == 1 ? 2 : 3);
					if (object != null) {
						((IsoDoor)object).destroy();
					}
				}

				this.destroy();
			}
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameClient.bClient) {
			if (gameCharacter instanceof IsoPlayer) {
				GameClient.instance.sendWeaponHit((IsoPlayer)gameCharacter, handWeapon, this);
			}

			this.setRenderEffect(RenderEffectType.Hit_Door, true);
		} else {
			IsoBarricade barricade = this.getBarricadeForCharacter(gameCharacter);
			if (barricade != null) {
				barricade.WeaponHit(gameCharacter, handWeapon);
			} else {
				barricade = this.getBarricadeOppositeCharacter(gameCharacter);
				if (barricade != null) {
					barricade.WeaponHit(gameCharacter, handWeapon);
				} else if (!this.open) {
					if (!this.isDestroyed()) {
						int int1 = gameCharacter.getPerkLevel(PerkFactory.Perks.Strength);
						float float1 = 1.0F;
						if (int1 == 0) {
							float1 = 0.5F;
						} else if (int1 == 1) {
							float1 = 0.63F;
						} else if (int1 == 2) {
							float1 = 0.76F;
						} else if (int1 == 3) {
							float1 = 0.89F;
						} else if (int1 == 4) {
							float1 = 1.02F;
						}

						if (int1 == 6) {
							float1 = 1.15F;
						} else if (int1 == 7) {
							float1 = 1.27F;
						} else if (int1 == 8) {
							float1 = 1.3F;
						} else if (int1 == 9) {
							float1 = 1.45F;
						} else if (int1 == 10) {
							float1 = 1.7F;
						}

						this.Damage((int)((float)handWeapon.getDoorDamage() * 2.0F * float1));
						this.setRenderEffect(RenderEffectType.Hit_Door, true);
						if (Rand.Next(10) == 0) {
							this.Damage((int)((float)handWeapon.getDoorDamage() * 6.0F * float1));
						}

						float float2 = GameTime.getInstance().getMultiplier() / 1.6F;
						switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) {
						case 0: 
							gameCharacter.exert(0.01F * float2);
							break;
						
						case 1: 
							gameCharacter.exert(0.007F * float2);
							break;
						
						case 2: 
							gameCharacter.exert(0.0065F * float2);
							break;
						
						case 3: 
							gameCharacter.exert(0.006F * float2);
							break;
						
						case 4: 
							gameCharacter.exert(0.005F * float2);
							break;
						
						case 5: 
							gameCharacter.exert(0.004F * float2);
							break;
						
						case 6: 
							gameCharacter.exert(0.0035F * float2);
							break;
						
						case 7: 
							gameCharacter.exert(0.003F * float2);
							break;
						
						case 8: 
							gameCharacter.exert(0.0025F * float2);
							break;
						
						case 9: 
							gameCharacter.exert(0.002F * float2);
						
						}

						this.DirtySlice();
						if (handWeapon.getDoorHitSound() != null) {
							gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
							if (GameServer.bServer) {
								GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
							}
						}

						WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
						if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
							gameCharacter.getEmitter().playSound("BreakDoor", this);
							if (GameServer.bServer) {
								GameServer.PlayWorldSoundServer("BreakDoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
							}

							WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
							int1 = getDoubleDoorIndex(this);
							if (int1 == 1 || int1 == 4) {
								IsoObject object = getDoubleDoorObject(this, int1 == 1 ? 2 : 3);
								if (object != null) {
									((IsoDoor)object).destroy();
								}
							}

							this.destroy();
							LuaEventManager.triggerEvent("OnContainerUpdate");
						}
					}
				}
			}
		}
	}

	public void destroy() {
		int int1 = Rand.Next(2) + 1;
		for (int int2 = 0; int2 < int1; ++int2) {
			this.square.AddWorldInventoryItem("Base.Plank", 0.0F, 0.0F, 0.0F);
		}

		InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.Doorknob");
		inventoryItem.setKeyId(this.checkKeyId());
		this.square.AddWorldInventoryItem(inventoryItem, 0.0F, 0.0F, 0.0F);
		int int3 = Rand.Next(3);
		for (int int4 = 0; int4 < int3; ++int4) {
			this.square.AddWorldInventoryItem("Base.Hinge", 0.0F, 0.0F, 0.0F);
		}

		if (this.bHasCurtain) {
			this.square.AddWorldInventoryItem("Base.Sheet", 0.0F, 0.0F, 0.0F);
		}

		this.destroyed = true;
		this.square.transmitRemoveItemFromSquare(this);
	}

	public IsoGridSquare getOtherSideOfDoor(IsoGameCharacter gameCharacter) {
		if (this.north) {
			return gameCharacter.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
		} else {
			return gameCharacter.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
		}
	}

	public boolean isExteriorDoor(IsoGameCharacter gameCharacter) {
		IsoGridSquare square = this.getSquare();
		IsoGridSquare square2 = this.getOppositeSquare();
		if (square2 == null) {
			return false;
		} else if (square.Is(IsoFlagType.exterior) && square2.getBuilding() != null && square2.getBuilding().getDef() != null) {
			return true;
		} else {
			return square.getBuilding() != null && square.getBuilding().getDef() != null && square2.Is(IsoFlagType.exterior);
		}
	}

	public void ToggleDoorActual(IsoGameCharacter gameCharacter) {
		if (this.isBarricaded()) {
			if (gameCharacter != null) {
				gameCharacter.getEmitter().playSound("DoorIsBlocked", this);
				gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0F);
			}
		} else {
			this.checkKeyId();
			if (this.Locked && !this.lockedByKey && this.getKeyId() != -1) {
				this.lockedByKey = true;
			}

			if (!this.open && gameCharacter instanceof IsoPlayer) {
				((IsoPlayer)gameCharacter).TimeSinceOpenDoor = 0.0F;
			}

			this.DirtySlice();
			IsoGridSquare.RecalcLightTime = -1;
			GameTime.instance.lightSourceUpdate = 100.0F;
			this.square.InvalidateSpecialObjectPaths();
			if (this.isLockedByKey() && gameCharacter != null && gameCharacter instanceof IsoPlayer && (gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) || this.getProperties().Is("forceLocked")) && !this.open) {
				if (gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) == null) {
					gameCharacter.getEmitter().playSound("DoorIsLocked", this);
					if (gameCharacter instanceof IsoSurvivor) {
						gameCharacter.getMasterBehaviorList().reset();
					}

					this.setRenderEffect(RenderEffectType.Hit_Door, true);
					return;
				}

				gameCharacter.getEmitter().playSound("UnlockDoor", this);
				gameCharacter.getEmitter().playSound("OpenDoor", this);
				this.Locked = false;
				this.setLockedByKey(false);
			}

			if (this.Locked && gameCharacter != null && gameCharacter instanceof IsoPlayer && gameCharacter.getCurrentSquare().Is(IsoFlagType.exterior) && !this.open) {
				gameCharacter.getEmitter().playSound("DoorIsLocked", this);
				if (gameCharacter instanceof IsoSurvivor) {
					gameCharacter.getMasterBehaviorList().reset();
				}

				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			} else if (this.getSprite().getProperties().Is("DoubleDoor")) {
				if (isDoubleDoorObstructed(this)) {
					if (gameCharacter != null) {
						gameCharacter.getEmitter().playSound("DoorIsBlocked", this);
						gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
					}
				} else {
					boolean boolean1 = this.open;
					toggleDoubleDoor(this, true);
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
				this.Locked = false;
				this.setLockedByKey(false);
				if (gameCharacter instanceof IsoPlayer) {
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						LosUtil.cachecleared[int1] = true;
					}

					IsoGridSquare.setRecalcLightTime(-1);
				}

				this.open = !this.open;
				this.sprite = this.closedSprite;
				if (this.open) {
					gameCharacter.getEmitter().playSound("OpenDoor");
					this.sprite = this.openSprite;
				} else if (gameCharacter != null) {
					gameCharacter.getEmitter().playSound("CloseDoor");
				}

				this.square.RecalcProperties();
				this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
				LuaEventManager.triggerEvent("OnContainerUpdate");
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
		if (this.open) {
			byteBufferWriter.putByte((byte)1);
		} else if (this.lockedByKey) {
			byteBufferWriter.putByte((byte)3);
		} else {
			byteBufferWriter.putByte((byte)4);
		}
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
						label60: while (true) {
							do {
								if (!iterator.hasNext()) {
									this.square.revisionUp();
									break label60;
								}

								udpConnection2 = (UdpConnection)iterator.next();
							}					 while ((udpConnection == null || udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) && udpConnection != null);

							byteBufferWriter2 = udpConnection2.startPacket();
							PacketTypes.doPacket((short)12, byteBufferWriter2);
							this.syncIsoObjectSend(byteBufferWriter2);
							udpConnection2.endPacketImmediate();
						}
					}
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

	void Damage(int int1) {
		this.DirtySlice();
		this.Health -= int1;
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

	public boolean isLocked() {
		return this.Locked;
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

	public Vector2 getFacingPositionAlt(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else if (this.open) {
			return this.north ? vector2.set(this.getX(), this.getY() + 0.5F) : vector2.set(this.getX() + 0.5F, this.getY());
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

	public int getKeyId() {
		return this.keyId;
	}

	public void syncDoorKey() {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.doPacket((short)106, byteBufferWriter);
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		if (byte1 == -1) {
			System.out.println("ERROR: Door not found on square " + this.square.getX() + ", " + this.square.getY() + ", " + this.square.getZ());
			GameClient.connection.cancelPacket();
		} else {
			byteBufferWriter.putByte(byte1);
			byteBufferWriter.putInt(this.getKeyId());
			GameClient.connection.endPacketImmediate();
		}
	}

	public void setKeyId(int int1) {
		if (this.keyId != int1 && GameClient.bClient) {
			this.keyId = int1;
			this.syncDoorKey();
		} else {
			this.keyId = int1;
		}
	}

	public boolean isLockedByKey() {
		return this.lockedByKey;
	}

	public void setLockedByKey(boolean boolean1) {
		boolean boolean2 = boolean1 != this.lockedByKey;
		this.lockedByKey = boolean1;
		this.Locked = boolean1;
		if (!GameServer.bServer && boolean2) {
			if (boolean1) {
				this.syncIsoObject(false, (byte)3, (UdpConnection)null, (ByteBuffer)null);
			} else {
				this.syncIsoObject(false, (byte)4, (UdpConnection)null, (ByteBuffer)null);
			}
		}
	}

	public boolean haveKey() {
		return this.haveKey;
	}

	public void setHaveKey(boolean boolean1) {
		this.haveKey = boolean1;
		if (!GameServer.bServer) {
			if (boolean1) {
				this.syncIsoObject(false, (byte)-1, (UdpConnection)null, (ByteBuffer)null);
			} else {
				this.syncIsoObject(false, (byte)-2, (UdpConnection)null, (ByteBuffer)null);
			}
		}
	}

	public IsoGridSquare getOppositeSquare() {
		return this.getNorth() ? this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ()) : this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
	}

	public int checkKeyId() {
		if (this.getKeyId() != -1) {
			return this.getKeyId();
		} else {
			IsoGridSquare square = this.getSquare();
			IsoGridSquare square2 = this.getOppositeSquare();
			if (square != null && square2 != null) {
				BuildingDef buildingDef = square.getBuilding() == null ? null : square.getBuilding().getDef();
				BuildingDef buildingDef2 = square2.getBuilding() == null ? null : square2.getBuilding().getDef();
				if (buildingDef == null && buildingDef2 != null) {
					this.setKeyId(buildingDef2.getKeyId());
				} else if (buildingDef != null && buildingDef2 == null) {
					this.setKeyId(buildingDef.getKeyId());
				} else if (this.getProperties().Is("forceLocked") && buildingDef != null) {
					this.setKeyId(buildingDef.getKeyId());
				}

				if (this.Locked && !this.lockedByKey && this.getKeyId() != -1) {
					this.lockedByKey = true;
				}

				return this.getKeyId();
			} else {
				return -1;
			}
		}
	}

	public void setHealth(int int1) {
		this.Health = int1;
	}

	private void initCurtainSprites() {
		if (this.curtainN == null) {
			this.curtainW = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainW.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_16");
			this.curtainW.def.setScale(0.8F, 0.8F);
			this.curtainWopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainWopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_20");
			this.curtainWopen.def.setScale(0.8F, 0.8F);
			this.curtainE = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainE.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_17");
			this.curtainE.def.setScale(0.8F, 0.8F);
			this.curtainEopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainEopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_21");
			this.curtainEopen.def.setScale(0.8F, 0.8F);
			this.curtainN = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainN.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_18");
			this.curtainN.def.setScale(0.8F, 0.8F);
			this.curtainNopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainNopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_22");
			this.curtainNopen.def.setScale(0.8F, 0.8F);
			this.curtainS = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainS.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_19");
			this.curtainS.def.setScale(0.8F, 0.8F);
			this.curtainSopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.curtainSopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_23");
			this.curtainSopen.def.setScale(0.8F, 0.8F);
		}
	}

	public IsoDoor HasCurtains() {
		return this.bHasCurtain ? this : null;
	}

	public boolean isCurtainOpen() {
		return this.bHasCurtain && this.bCurtainOpen;
	}

	public void setCurtainOpen(boolean boolean1) {
		if (this.bHasCurtain) {
			this.bCurtainOpen = boolean1;
			if (!GameServer.bServer) {
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					LosUtil.cachecleared[int1] = true;
				}

				GameTime.instance.lightSourceUpdate = 100.0F;
				IsoGridSquare.setRecalcLightTime(-1);
				if (this.square != null) {
					this.square.RecalcProperties();
				}
			}
		}
	}

	public void transmitSetCurtainOpen(boolean boolean1) {
		if (this.bHasCurtain) {
			if (GameServer.bServer) {
				this.sendObjectChange("setCurtainOpen", new Object[]{"open", boolean1});
			}

			if (GameClient.bClient) {
				GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "openCloseCurtain", "x", this.getX(), "y", this.getY(), "z", this.getZ(), "index", this.getObjectIndex(), "open", !this.bCurtainOpen);
			}
		}
	}

	public void toggleCurtain() {
		if (this.bHasCurtain) {
			if (GameClient.bClient) {
				this.transmitSetCurtainOpen(!this.isCurtainOpen());
			} else {
				this.setCurtainOpen(!this.isCurtainOpen());
				if (GameServer.bServer) {
					this.transmitSetCurtainOpen(this.isCurtainOpen());
				}
			}
		}
	}

	public void addSheet(IsoGameCharacter gameCharacter) {
		if (!this.bHasCurtain && gameCharacter != null && gameCharacter.getCurrentSquare() != null) {
			IsoGridSquare square = gameCharacter.getCurrentSquare();
			IsoGridSquare square2 = this.getSquare();
			boolean boolean1;
			if (this.open) {
				if (this.north) {
					boolean1 = square.getX() < square2.getX();
				} else {
					boolean1 = square.getY() < square2.getY();
				}
			} else if (this.north) {
				boolean1 = square.getY() >= square2.getY();
			} else {
				boolean1 = square.getX() >= square2.getX();
			}

			this.addSheet(boolean1, gameCharacter);
		}
	}

	public void addSheet(boolean boolean1, IsoGameCharacter gameCharacter) {
		if (!this.bHasCurtain) {
			this.bHasCurtain = true;
			this.bCurtainInside = boolean1;
			this.bCurtainOpen = true;
			if (GameServer.bServer) {
				this.sendObjectChange("addSheet", new Object[]{"inside", boolean1});
				if (gameCharacter != null) {
					gameCharacter.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
				}
			} else if (gameCharacter != null) {
				gameCharacter.getInventory().RemoveOneOf("Sheet");
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					LosUtil.cachecleared[int1] = true;
				}

				GameTime.instance.lightSourceUpdate = 100.0F;
				IsoGridSquare.setRecalcLightTime(-1);
				if (this.square != null) {
					this.square.RecalcProperties();
				}
			}
		}
	}

	public void removeSheet(IsoGameCharacter gameCharacter) {
		if (this.bHasCurtain) {
			this.bHasCurtain = false;
			if (GameServer.bServer) {
				this.sendObjectChange("removeSheet");
				if (gameCharacter != null) {
					gameCharacter.sendObjectChange("addItemOfType", new Object[]{"type", "Base.Sheet"});
				}
			} else if (gameCharacter != null) {
				gameCharacter.getInventory().AddItem("Base.Sheet");
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					LosUtil.cachecleared[int1] = true;
				}

				GameTime.instance.lightSourceUpdate = 100.0F;
				IsoGridSquare.setRecalcLightTime(-1);
				if (this.square != null) {
					this.square.RecalcProperties();
				}
			}
		}
	}

	public IsoGridSquare getAddSheetSquare(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.getCurrentSquare() != null) {
			IsoGridSquare square = gameCharacter.getCurrentSquare();
			IsoGridSquare square2 = this.getSquare();
			if (this.open) {
				if (this.north) {
					return square.getX() < square2.getX() ? this.getCell().getGridSquare(square2.x - 1, square2.y, square2.z) : square2;
				} else {
					return square.getY() < square2.getY() ? this.getCell().getGridSquare(square2.x, square2.y - 1, square2.z) : square2;
				}
			} else if (this.north) {
				return square.getY() >= square2.getY() ? square2 : this.getOppositeSquare();
			} else {
				return square.getX() >= square2.getX() ? square2 : this.getOppositeSquare();
			}
		} else {
			return null;
		}
	}

	public IsoGridSquare getSheetSquare() {
		if (!this.bHasCurtain) {
			return null;
		} else if (this.bCurtainInside) {
			if (this.open) {
				return this.north ? this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ()) : this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
			} else {
				return this.getSquare();
			}
		} else if (this.open) {
			return this.north ? this.getSquare() : this.getCell().getGridSquare((double)this.getX(), (double)this.getY(), (double)this.getZ());
		} else {
			return this.getOppositeSquare();
		}
	}

	public int getHealth() {
		return this.Health;
	}

	public int getMaxHealth() {
		return this.MaxHealth;
	}

	public boolean isFacingSheet(IsoGameCharacter gameCharacter) {
		if (this.bHasCurtain && gameCharacter != null && gameCharacter.getCurrentSquare() == this.getSheetSquare()) {
			IsoDirections directions;
			if (this.bCurtainInside) {
				if (this.open) {
					if (this.north) {
						directions = IsoDirections.E;
					} else {
						directions = IsoDirections.S;
					}
				} else if (this.north) {
					directions = IsoDirections.N;
				} else {
					directions = IsoDirections.W;
				}
			} else if (this.open) {
				if (this.north) {
					directions = IsoDirections.W;
				} else {
					directions = IsoDirections.N;
				}
			} else if (this.north) {
				directions = IsoDirections.S;
			} else {
				directions = IsoDirections.E;
			}

			return gameCharacter.getDir() == directions || gameCharacter.getDir() == IsoDirections.RotLeft(directions) || gameCharacter.getDir() == IsoDirections.RotRight(directions);
		} else {
			return false;
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("addSheet".equals(string)) {
			if (kahluaTable != null && kahluaTable.rawget("inside") instanceof Boolean) {
				byteBuffer.put((byte)((Boolean)kahluaTable.rawget("inside") ? 1 : 0));
			}
		} else if (!"removeSheet".equals(string)) {
			if ("setCurtainOpen".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("open") instanceof Boolean) {
					byteBuffer.put((byte)((Boolean)kahluaTable.rawget("open") ? 1 : 0));
				}
			} else {
				super.saveChange(string, kahluaTable, byteBuffer);
			}
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("addSheet".equals(string)) {
			this.addSheet(byteBuffer.get() == 1, (IsoGameCharacter)null);
		} else if ("removeSheet".equals(string)) {
			this.removeSheet((IsoGameCharacter)null);
		} else if ("setCurtainOpen".equals(string)) {
			this.setCurtainOpen(byteBuffer.get() == 1);
		} else {
			super.loadChange(string, byteBuffer);
		}
	}

	public void addRandomBarricades() {
		IsoGridSquare square = this.square.getRoom() == null ? this.square : this.getOppositeSquare();
		if (square != null && square.getRoom() == null) {
			boolean boolean1 = square != this.square;
			IsoBarricade barricade = IsoBarricade.AddBarricadeToObject(this, boolean1);
			if (barricade != null) {
				int int1 = Rand.Next(1, 4);
				for (int int2 = 0; int2 < int1; ++int2) {
					barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
				}
			}
		}
	}

	public boolean isObstructed() {
		return isDoorObstructed(this);
	}

	public static boolean isDoorObstructed(IsoObject object) {
		IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
		IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
		if (door == null && thumpable == null) {
			return false;
		} else {
			IsoGridSquare square = object.getSquare();
			if (square == null) {
				return false;
			} else if (!square.isSolid() && !square.isSolidTrans() && !square.Has(IsoObjectType.tree)) {
				int int1 = (square.x - 1) / 10;
				int int2 = (square.y - 1) / 10;
				int int3 = (int)Math.ceil((double)(((float)square.x + 1.0F) / 10.0F));
				int int4 = (int)Math.ceil((double)(((float)square.y + 1.0F) / 10.0F));
				for (int int5 = int2; int5 <= int4; ++int5) {
					for (int int6 = int1; int6 <= int3; ++int6) {
						IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunk(int6, int5);
						if (chunk != null) {
							for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
								BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
								if (baseVehicle.isIntersectingSquareWithShadow(square.x, square.y, square.z)) {
									return true;
								}
							}
						}
					}
				}

				return false;
			} else {
				return true;
			}
		}
	}

	public static void toggleDoubleDoor(IsoObject object, boolean boolean1) {
		int int1 = getDoubleDoorIndex(object);
		if (int1 != -1) {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean2;
			if (door == null) {
				boolean2 = thumpable.north;
			} else {
				boolean2 = door.north;
			}

			if (door == null) {
				boolean2 = thumpable.open;
			} else {
				boolean2 = door.open;
			}

			if (boolean1 && thumpable != null) {
				thumpable.syncIsoObject(false, (byte)(thumpable.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
			}

			IsoObject object2 = getDoubleDoorObject(object, 1);
			IsoObject object3 = getDoubleDoorObject(object, 2);
			IsoObject object4 = getDoubleDoorObject(object, 3);
			IsoObject object5 = getDoubleDoorObject(object, 4);
			if (object2 != null) {
				toggleDoubleDoorObject(object2);
			}

			if (object3 != null) {
				toggleDoubleDoorObject(object3);
			}

			if (object4 != null) {
				toggleDoubleDoorObject(object4);
			}

			if (object5 != null) {
				toggleDoubleDoorObject(object5);
			}

			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}

	private static void toggleDoubleDoorObject(IsoObject object) {
		int int1 = getDoubleDoorIndex(object);
		if (int1 != -1) {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.north : door.north;
			boolean boolean2 = door == null ? thumpable.open : door.open;
			if (door != null) {
				door.open = !boolean2;
				door.setLockedByKey(false);
			}

			if (thumpable != null) {
				thumpable.open = !boolean2;
				thumpable.setLockedByKey(false);
			}

			IsoSprite sprite = object.getSprite();
			int int2 = boolean1 ? DoubleDoorNorthSpriteOffset[int1 - 1] : DoubleDoorWestSpriteOffset[int1 - 1];
			if (boolean2) {
				int2 *= -1;
			}

			object.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, sprite.getName(), int2);
			object.getSquare().RecalcAllWithNeighbours(true);
			if (int1 == 2 || int1 == 3) {
				IsoGridSquare square = object.getSquare();
				int[] intArray;
				int[] intArray2;
				int[] intArray3;
				int[] intArray4;
				if (boolean1) {
					if (boolean2) {
						intArray = DoubleDoorNorthOpenXOffset;
						intArray2 = DoubleDoorNorthOpenYOffset;
						intArray3 = DoubleDoorNorthClosedXOffset;
						intArray4 = DoubleDoorNorthClosedYOffset;
					} else {
						intArray = DoubleDoorNorthClosedXOffset;
						intArray2 = DoubleDoorNorthClosedYOffset;
						intArray3 = DoubleDoorNorthOpenXOffset;
						intArray4 = DoubleDoorNorthOpenYOffset;
					}
				} else if (boolean2) {
					intArray = DoubleDoorWestOpenXOffset;
					intArray2 = DoubleDoorWestOpenYOffset;
					intArray3 = DoubleDoorWestClosedXOffset;
					intArray4 = DoubleDoorWestClosedYOffset;
				} else {
					intArray = DoubleDoorWestClosedXOffset;
					intArray2 = DoubleDoorWestClosedYOffset;
					intArray3 = DoubleDoorWestOpenXOffset;
					intArray4 = DoubleDoorWestOpenYOffset;
				}

				int int3 = square.getX() - intArray[int1 - 1];
				int int4 = square.getY() - intArray2[int1 - 1];
				int int5 = int3 + intArray3[int1 - 1];
				int int6 = int4 + intArray4[int1 - 1];
				square.RemoveTileObject(object);
				square = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, square.getZ());
				if (square == null) {
					return;
				}

				if (thumpable != null) {
					IsoThumpable thumpable2 = new IsoThumpable(square.getCell(), square, object.getSprite().getName(), boolean1, thumpable.getTable());
					thumpable2.setModData(thumpable.getModData());
					thumpable2.setIsDoor(true);
					thumpable2.open = !boolean2;
					square.AddSpecialObject(thumpable2);
				} else {
					IsoDoor door2 = new IsoDoor(square.getCell(), square, object.getSprite().getName(), boolean1);
					door2.open = !boolean2;
					square.getObjects().add(door2);
					square.getSpecialObjects().add(door2);
					square.RecalcProperties();
				}

				if (!GameClient.bClient) {
					square.restackSheetRope();
				}
			}
		}
	}

	public static int getDoubleDoorIndex(IsoObject object) {
		if (object != null && object.getSquare() != null) {
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null && propertyContainer.Is("DoubleDoor")) {
				int int1 = Integer.parseInt(propertyContainer.Val("DoubleDoor"));
				if (int1 >= 1 && int1 <= 8) {
					IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
					IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
					if (door == null && thumpable == null) {
						return -1;
					} else {
						boolean boolean1 = door == null ? thumpable.open : door.open;
						if (boolean1) {
							return int1 >= 5 ? int1 - 4 : -1;
						} else {
							return int1;
						}
					}
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public static IsoObject getDoubleDoorObject(IsoObject object, int int1) {
		int int2 = getDoubleDoorIndex(object);
		if (int2 == -1) {
			return null;
		} else {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.north : door.north;
			boolean boolean2 = door == null ? thumpable.open : door.open;
			IsoGridSquare square = object.getSquare();
			int[] intArray;
			int[] intArray2;
			if (boolean1) {
				if (boolean2) {
					intArray = DoubleDoorNorthOpenXOffset;
					intArray2 = DoubleDoorNorthOpenYOffset;
				} else {
					intArray = DoubleDoorNorthClosedXOffset;
					intArray2 = DoubleDoorNorthClosedYOffset;
				}
			} else if (boolean2) {
				intArray = DoubleDoorWestOpenXOffset;
				intArray2 = DoubleDoorWestOpenYOffset;
			} else {
				intArray = DoubleDoorWestClosedXOffset;
				intArray2 = DoubleDoorWestClosedYOffset;
			}

			int int3 = square.getX() - intArray[int2 - 1];
			int int4 = square.getY() - intArray2[int2 - 1];
			int int5 = int3 + intArray[int1 - 1];
			int int6 = int4 + intArray2[int1 - 1];
			square = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, square.getZ());
			if (square == null) {
				return null;
			} else {
				ArrayList arrayList = square.getSpecialObjects();
				int int7;
				IsoObject object2;
				if (door != null) {
					for (int7 = 0; int7 < arrayList.size(); ++int7) {
						object2 = (IsoObject)arrayList.get(int7);
						if (object2 instanceof IsoDoor && ((IsoDoor)object2).north == boolean1 && getDoubleDoorIndex(object2) == int1) {
							return object2;
						}
					}
				}

				if (thumpable != null) {
					for (int7 = 0; int7 < arrayList.size(); ++int7) {
						object2 = (IsoObject)arrayList.get(int7);
						if (object2 instanceof IsoThumpable && ((IsoThumpable)object2).north == boolean1 && getDoubleDoorIndex(object2) == int1) {
							return object2;
						}
					}
				}

				return null;
			}
		}
	}

	public static boolean isDoubleDoorObstructed(IsoObject object) {
		int int1 = getDoubleDoorIndex(object);
		if (int1 == -1) {
			return false;
		} else {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.north : door.north;
			boolean boolean2 = door == null ? thumpable.open : door.open;
			IsoGridSquare square = object.getSquare();
			int[] intArray;
			int[] intArray2;
			if (boolean1) {
				if (boolean2) {
					intArray = DoubleDoorNorthOpenXOffset;
					intArray2 = DoubleDoorNorthOpenYOffset;
				} else {
					intArray = DoubleDoorNorthClosedXOffset;
					intArray2 = DoubleDoorNorthClosedYOffset;
				}
			} else if (boolean2) {
				intArray = DoubleDoorWestOpenXOffset;
				intArray2 = DoubleDoorWestOpenYOffset;
			} else {
				intArray = DoubleDoorWestClosedXOffset;
				intArray2 = DoubleDoorWestClosedYOffset;
			}

			int int2 = square.getX() - intArray[int1 - 1];
			int int3 = square.getY() - intArray2[int1 - 1];
			int int4 = int2;
			int int5 = int3 + (boolean1 ? 0 : -3);
			int int6 = int2 + (boolean1 ? 4 : 2);
			int int7 = int5 + (boolean1 ? 2 : 4);
			int int8 = square.getZ();
			int int9;
			int int10;
			for (int9 = int5; int9 < int7; ++int9) {
				for (int10 = int4; int10 < int6; ++int10) {
					IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int10, int9, int8);
					if (square2 != null && (square2.isSolid() || square2.isSolidTrans() || square2.Has(IsoObjectType.tree))) {
						return true;
					}
				}
			}

			int9 = (int4 - 4) / 10;
			int10 = (int5 - 4) / 10;
			int int11 = (int)Math.ceil((double)((int6 + 4) / 10));
			int int12 = (int)Math.ceil((double)((int7 + 4) / 10));
			for (int int13 = int10; int13 <= int12; ++int13) {
				for (int int14 = int9; int14 <= int11; ++int14) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int14, int13) : IsoWorld.instance.CurrentCell.getChunk(int14, int13);
					if (chunk != null) {
						for (int int15 = 0; int15 < chunk.vehicles.size(); ++int15) {
							BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int15);
							for (int int16 = int5; int16 < int7; ++int16) {
								for (int int17 = int4; int17 < int6; ++int17) {
									if (baseVehicle.isIntersectingSquare(int17, int16, int8)) {
										return true;
									}
								}
							}
						}
					}
				}
			}

			return false;
		}
	}
	public static enum DoorType {

		WeakWooden,
		StrongWooden;
	}
}
