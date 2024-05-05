package zombie.iso.objects;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ThumpState;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.characters.BaseCharacterSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugOptions;
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
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;


public class IsoDoor extends IsoObject implements BarricadeAble,Thumpable {
	public int Health = 500;
	public boolean lockedByKey = false;
	private boolean haveKey = false;
	public boolean Locked = false;
	public int MaxHealth = 500;
	public int PushedMaxStrength = 0;
	public int PushedStrength = 0;
	public IsoDoor.DoorType type;
	IsoSprite closedSprite;
	public boolean north;
	int gid;
	public boolean open;
	IsoSprite openSprite;
	private boolean destroyed;
	private boolean bHasCurtain;
	private boolean bCurtainInside;
	private boolean bCurtainOpen;
	KahluaTable table;
	public static final Vector2 tempo = new Vector2();
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
	}

	public String getObjectName() {
		return "Door";
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		this.checkKeyHighlight(float1, float2);
		if (!this.bHasCurtain) {
			super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
		} else {
			this.initCurtainSprites();
			IsoDirections directions = this.getSpriteEdge(false);
			this.prerender(float1, float2, float3, colorInfo, boolean1, boolean2, directions);
			super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
			this.postrender(float1, float2, float3, colorInfo, boolean1, boolean2, directions);
		}
	}

	public void renderWallTile(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader, Consumer consumer) {
		this.checkKeyHighlight(float1, float2);
		if (!this.bHasCurtain) {
			super.renderWallTile(float1, float2, float3, colorInfo, boolean1, boolean2, shader, consumer);
		} else {
			this.initCurtainSprites();
			IsoDirections directions = this.getSpriteEdge(false);
			this.prerender(float1, float2, float3, colorInfo, boolean1, boolean2, directions);
			super.renderWallTile(float1, float2, float3, colorInfo, boolean1, boolean2, shader, consumer);
			this.postrender(float1, float2, float3, colorInfo, boolean1, boolean2, directions);
		}
	}

	private void checkKeyHighlight(float float1, float float2) {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoGameCharacter gameCharacter = IsoCamera.frameState.CamCharacter;
		Key key = Key.highlightDoor[int1];
		if (key != null && float1 >= gameCharacter.getX() - 20.0F && float2 >= gameCharacter.getY() - 20.0F && float1 < gameCharacter.getX() + 20.0F && float2 < gameCharacter.getY() + 20.0F) {
			boolean boolean1 = this.square.isSeen(int1);
			if (!boolean1) {
				IsoGridSquare square = this.getOppositeSquare();
				boolean1 = square != null && square.isSeen(int1);
			}

			if (boolean1) {
				this.checkKeyId();
				if (this.getKeyId() == key.getKeyId()) {
					this.setHighlighted(true);
				}
			}
		}
	}

	private void prerender(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, IsoDirections directions) {
		if (Core.TileScale == 1) {
			switch (directions) {
			case N: 
				this.prerender1xN(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case S: 
				this.prerender1xS(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case W: 
				this.prerender1xW(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case E: 
				this.prerender1xE(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
			
			}
		} else {
			switch (directions) {
			case N: 
				this.prerender2xN(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case S: 
				this.prerender2xS(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case W: 
				this.prerender2xW(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case E: 
				this.prerender2xE(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
			
			}
		}
	}

	private void postrender(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, IsoDirections directions) {
		if (Core.TileScale == 1) {
			switch (directions) {
			case N: 
				this.postrender1xN(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case S: 
				this.postrender1xS(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case W: 
				this.postrender1xW(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case E: 
				this.postrender1xE(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
			
			}
		} else {
			switch (directions) {
			case N: 
				this.postrender2xN(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case S: 
				this.postrender2xS(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case W: 
				this.postrender2xW(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
				break;
			
			case E: 
				this.postrender2xE(float1, float2, float3, colorInfo, boolean1, boolean2, (Shader)null);
			
			}
		}
	}

	private void prerender1xN(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (!this.north && this.open) {
				(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX + 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -14 : -14), colorInfo, true);
			}
		} else if (this.north && !this.open) {
			(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX - 1.0F - 1.0F, this.offsetY + -15.0F, colorInfo, true);
		}
	}

	private void postrender1xN(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 10.0F - 1.0F, this.offsetY + -10.0F, colorInfo, true);
			}
		} else if (!this.north && this.open) {
			(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 4.0F, this.offsetY + (float)(this.bCurtainOpen ? -10 : -10), colorInfo, true);
		}
	}

	private void prerender1xS(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert !this.north && this.open;
		if (!this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX + (float)((this.bCurtainOpen ? -14 : -14) / 2), this.offsetY + (float)((this.bCurtainOpen ? -16 : -16) / 2), colorInfo, true);
		}
	}

	private void postrender1xS(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert !this.north && this.open;
		if (this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2 + 1.0F, float3, this.dir, this.offsetX + (float)((this.bCurtainOpen ? -28 : -28) / 2), this.offsetY + (float)((this.bCurtainOpen ? -8 : -8) / 2), colorInfo, true);
		}
	}

	private void prerender1xW(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (this.north && this.open) {
				(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -16 : -18), this.offsetY + (float)(this.bCurtainOpen ? -14 : -15), colorInfo, true);
			}

			if (!this.north && this.open) {
				(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX + 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -14 : -14), colorInfo, true);
			}
		} else {
			if (this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX - 1.0F - 1.0F, this.offsetY + -15.0F, colorInfo, true);
			}

			if (!this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -12 : -14), this.offsetY + (float)(this.bCurtainOpen ? -14 : -15), colorInfo, true);
			}
		}
	}

	private void postrender1xW(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 10.0F - 1.0F, this.offsetY + -10.0F, colorInfo, true);
			}

			if (!this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 2.0F - 1.0F, this.offsetY + -10.0F, colorInfo, true);
			}
		} else {
			if (this.north && this.open) {
				(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 9.0F, this.offsetY + -10.0F, colorInfo, true);
			}

			if (!this.north && this.open) {
				(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 4.0F, this.offsetY + (float)(this.bCurtainOpen ? -10 : -10), colorInfo, true);
			}
		}
	}

	private void prerender1xE(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert this.north && this.open;
		if (!this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX + (float)((this.bCurtainOpen ? -13 : -18) / 2), this.offsetY + (float)((this.bCurtainOpen ? -15 : -18) / 2), colorInfo, true);
		}
	}

	private void postrender1xE(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert this.north && this.open;
		if (this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1 + 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? 0 : 0), this.offsetY + (float)(this.bCurtainOpen ? 0 : 0), colorInfo, true);
		}
	}

	private void prerender2xN(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (!this.north && this.open) {
				(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX + 7.0F, this.offsetY + (float)(this.bCurtainOpen ? -28 : -28), colorInfo, true);
			}
		} else if (this.north && !this.open) {
			(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2 - 1.0F, float3, this.dir, this.offsetX - 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -30 : -30), colorInfo, true);
		}
	}

	private void postrender2xN(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 20.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo, true);
			}
		} else if (!this.north && this.open) {
			(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 8.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo, true);
		}
	}

	private void prerender2xS(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert !this.north && this.open;
		if (!this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -14 : -14), this.offsetY + (float)(this.bCurtainOpen ? -16 : -16), colorInfo, true);
		}
	}

	private void postrender2xS(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert !this.north && this.open;
		if (this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, float1, float2 + 1.0F, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -28 : -28), this.offsetY + (float)(this.bCurtainOpen ? -8 : -8), colorInfo, true);
		}
	}

	private void prerender2xW(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (this.north && this.open) {
				(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -32 : -37), this.offsetY + (float)(this.bCurtainOpen ? -28 : -31), colorInfo, true);
			}
		} else if (!this.north && !this.open) {
			(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1 - 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -22 : -26), this.offsetY + (float)(this.bCurtainOpen ? -28 : -31), colorInfo, true);
		}
	}

	private void postrender2xW(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.bCurtainInside) {
			if (!this.north && !this.open) {
				(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 5.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo, true);
			}
		} else if (this.north && this.open) {
			(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX - 19.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), colorInfo, true);
		}
	}

	private void prerender2xE(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert this.north && this.open;
		if (!this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -13 : -18), this.offsetY + (float)(this.bCurtainOpen ? -15 : -18), colorInfo, true);
		}
	}

	private void postrender2xE(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		assert this.north && this.open;
		if (this.bCurtainInside) {
			(this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, float1 + 1.0F, float2, float3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? 0 : 0), this.offsetY + (float)(this.bCurtainOpen ? 0 : 0), colorInfo, true);
		}
	}

	public IsoDirections getSpriteEdge(boolean boolean1) {
		if (this.open && !boolean1) {
			PropertyContainer propertyContainer = this.getProperties();
			if (propertyContainer != null && propertyContainer.Is("GarageDoor")) {
				return this.north ? IsoDirections.N : IsoDirections.W;
			} else if (propertyContainer != null && propertyContainer.Is(IsoFlagType.attachedE)) {
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

	public IsoDoor(IsoCell cell, IsoGridSquare square, IsoSprite sprite, boolean boolean1) {
		this.type = IsoDoor.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = sprite;
		this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (IsoSprite)sprite, 2);
		this.sprite = this.closedSprite;
		String string = sprite.getProperties().Val("GarageDoor");
		if (string != null) {
			int int1 = Integer.parseInt(string);
			if (int1 <= 3) {
				this.closedSprite = sprite;
				this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (IsoSprite)sprite, 8);
			} else {
				this.openSprite = sprite;
				this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (IsoSprite)sprite, -8);
			}
		}

		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case WeakWooden: 
			this.MaxHealth = this.Health = 500;
			break;
		
		case StrongWooden: 
			this.MaxHealth = this.Health = 800;
		
		}
		if (this.getSprite().getName() != null && this.getSprite().getName().contains("fences")) {
			this.MaxHealth = this.Health = 100;
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
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 0);
		this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 2);
		this.sprite = this.closedSprite;
		String string2 = this.closedSprite.getProperties().Val("GarageDoor");
		if (string2 != null) {
			int int1 = Integer.parseInt(string2);
			if (int1 <= 3) {
				this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 8);
			} else {
				this.openSprite = this.sprite;
				this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, -8);
			}
		}

		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case WeakWooden: 
			this.MaxHealth = this.Health = 500;
			break;
		
		case StrongWooden: 
			this.MaxHealth = this.Health = 800;
		
		}
		if (this.getSprite().getName() != null && this.getSprite().getName().contains("fences")) {
			this.MaxHealth = this.Health = 100;
		}
	}

	public IsoDoor(IsoCell cell, IsoGridSquare square, String string, boolean boolean1, KahluaTable kahluaTable) {
		this.type = IsoDoor.DoorType.WeakWooden;
		this.north = false;
		this.gid = -1;
		this.open = false;
		this.destroyed = false;
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 0);
		this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 2);
		this.table = kahluaTable;
		this.sprite = this.closedSprite;
		String string2 = this.sprite.getProperties().Val("GarageDoor");
		if (string2 != null) {
			int int1 = Integer.parseInt(string2);
			if (int1 <= 3) {
				this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 8);
			} else {
				this.openSprite = this.sprite;
				this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, -8);
			}
		}

		this.square = square;
		this.north = boolean1;
		switch (this.type) {
		case WeakWooden: 
			this.MaxHealth = this.Health = 500;
			break;
		
		case StrongWooden: 
			this.MaxHealth = this.Health = 800;
		
		}
		if (this.getSprite().getName() != null && this.getSprite().getName().contains("fences")) {
			this.MaxHealth = this.Health = 100;
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.open = byteBuffer.get() == 1;
		this.Locked = byteBuffer.get() == 1;
		this.north = byteBuffer.get() == 1;
		this.Health = byteBuffer.getInt();
		this.MaxHealth = byteBuffer.getInt();
		this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
		this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
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

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
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

	public void saveState(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.Locked ? 1 : 0));
		byteBuffer.put((byte)(this.lockedByKey ? 1 : 0));
	}

	public void loadState(ByteBuffer byteBuffer) throws IOException {
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
		return false;
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
		if (this.sprite != null && this.sprite.getProperties().Is("GarageDoor") && this.open) {
			boolean1 = true;
		}

		if (this.open) {
			boolean1 = true;
		} else if (this.bHasCurtain && !this.bCurtainOpen) {
			boolean1 = false;
		}

		boolean boolean2 = this.north;
		if (this.open) {
			boolean2 = !boolean2;
		}

		if (square2.getZ() != square.getZ()) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			if (square == this.square) {
				if (boolean2 && square2.getY() < square.getY()) {
					if (boolean1) {
						return IsoObject.VisionResult.Unblocked;
					}

					return IsoObject.VisionResult.Blocked;
				}

				if (!boolean2 && square2.getX() < square.getX()) {
					if (boolean1) {
						return IsoObject.VisionResult.Unblocked;
					}

					return IsoObject.VisionResult.Blocked;
				}
			} else {
				if (boolean2 && square2.getY() > square.getY()) {
					if (boolean1) {
						return IsoObject.VisionResult.Unblocked;
					}

					return IsoObject.VisionResult.Blocked;
				}

				if (!boolean2 && square2.getX() > square.getX()) {
					if (boolean1) {
						return IsoObject.VisionResult.Unblocked;
					}

					return IsoObject.VisionResult.Blocked;
				}
			}

			return IsoObject.VisionResult.NoEffect;
		}
	}

	public void Thump(IsoMovingObject movingObject) {
		if (!this.isDestroyed()) {
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
				if (int1 >= 2) {
					this.DirtySlice();
					this.Damage(((IsoZombie)movingObject).strength * int2);
					if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
						this.Damage(int1 * 2 * int2);
					}
				}

				if (Core.GameMode.equals("LastStand")) {
					this.Damage(1 * int2);
				}

				WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			}

			if (this.Health <= 0) {
				if (this.getSquare().getBuilding() != null) {
					this.getSquare().getBuilding().forceAwake();
				}

				this.playDoorSound(((IsoGameCharacter)movingObject).getEmitter(), "Break");
				if (GameServer.bServer) {
					GameServer.PlayWorldSoundServer("BreakDoor", false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
				}

				WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
				movingObject.setThumpTarget((Thumpable)null);
				if (destroyDoubleDoor(this)) {
					return;
				}

				if (destroyGarageDoor(this)) {
					return;
				}

				this.destroy();
			}
		}
	}

	public Thumpable getThumpableFor(IsoGameCharacter gameCharacter) {
		IsoBarricade barricade = this.getBarricadeForCharacter(gameCharacter);
		if (barricade != null) {
			return barricade;
		} else {
			barricade = this.getBarricadeOppositeCharacter(gameCharacter);
			if (barricade != null) {
				return barricade;
			} else {
				return !this.isDestroyed() && !this.IsOpen() ? this : null;
			}
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (GameClient.bClient) {
			if (player != null) {
				GameClient.instance.sendWeaponHit(player, handWeapon, this);
			}

			this.setRenderEffect(RenderEffectType.Hit_Door, true);
		} else {
			Thumpable thumpable = this.getThumpableFor(gameCharacter);
			if (thumpable != null) {
				if (thumpable instanceof IsoBarricade) {
					((IsoBarricade)thumpable).WeaponHit(gameCharacter, handWeapon);
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
							if (player != null) {
								String string = this.getSoundPrefix();
								byte byte1 = -1;
								switch (string.hashCode()) {
								case -247340139: 
									if (string.equals("GarageDoor")) {
										byte1 = 0;
									}

									break;
								
								case 945260341: 
									if (string.equals("MetalDoor")) {
										byte1 = 1;
									}

									break;
								
								case 945336402: 
									if (string.equals("MetalGate")) {
										byte1 = 2;
									}

								
								}

								switch (byte1) {
								case 0: 
								
								case 1: 
								
								case 2: 
									player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Metal);
									break;
								
								default: 
									player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Wood);
								
								}
							}

							gameCharacter.getEmitter().playSound(handWeapon.getDoorHitSound(), this);
							if (GameServer.bServer) {
								GameServer.PlayWorldSoundServer(handWeapon.getDoorHitSound(), false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
							}
						}

						WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
						if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
							this.playDoorSound(gameCharacter.getEmitter(), "Break");
							if (GameServer.bServer) {
								GameServer.PlayWorldSoundServer("BreakDoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
							}

							WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
							if (destroyDoubleDoor(this)) {
								return;
							}

							if (destroyGarageDoor(this)) {
								return;
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
		if (this.sprite != null && this.sprite.getProperties().Is("GarageDoor")) {
			this.destroyed = true;
			this.square.transmitRemoveItemFromSquare(this);
		} else {
			PropertyContainer propertyContainer = this.getProperties();
			if (propertyContainer != null) {
				String string = propertyContainer.Val("Material");
				String string2 = propertyContainer.Val("Material2");
				String string3 = propertyContainer.Val("Material3");
				int int1;
				if (StringUtils.isNullOrEmpty(string) && StringUtils.isNullOrEmpty(string2) && StringUtils.isNullOrEmpty(string3)) {
					int int2 = Rand.Next(2) + 1;
					for (int1 = 0; int1 < int2; ++int1) {
						this.square.AddWorldInventoryItem("Base.Plank", 0.0F, 0.0F, 0.0F);
					}
				} else {
					this.addItemsFromProperties();
				}

				InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.Doorknob");
				inventoryItem.setKeyId(this.checkKeyId());
				this.square.AddWorldInventoryItem(inventoryItem, 0.0F, 0.0F, 0.0F);
				int1 = Rand.Next(3);
				for (int int3 = 0; int3 < int1; ++int3) {
					this.square.AddWorldInventoryItem("Base.Hinge", 0.0F, 0.0F, 0.0F);
				}

				if (this.bHasCurtain) {
					this.square.AddWorldInventoryItem("Base.Sheet", 0.0F, 0.0F, 0.0F);
				}

				this.destroyed = true;
				this.square.transmitRemoveItemFromSquare(this);
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

	public boolean isHoppable() {
		if (this.IsOpen()) {
			return false;
		} else if (this.closedSprite == null) {
			return false;
		} else {
			PropertyContainer propertyContainer = this.closedSprite.getProperties();
			return propertyContainer.Is(IsoFlagType.HoppableN) || propertyContainer.Is(IsoFlagType.HoppableW);
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

	public void ToggleDoorActual(IsoGameCharacter gameCharacter) {
		if (Core.bDebug && DebugOptions.instance.CheatDoorUnlock.getValue()) {
			this.Locked = false;
			this.setLockedByKey(false);
		}

		if (this.isHoppable()) {
			this.Locked = false;
			this.setLockedByKey(false);
		}

		if (this.isBarricaded()) {
			if (gameCharacter != null) {
				this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
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
					this.playDoorSound(gameCharacter.getEmitter(), "Locked");
					this.setRenderEffect(RenderEffectType.Hit_Door, true);
					return;
				}

				this.playDoorSound(gameCharacter.getEmitter(), "Unlock");
				this.playDoorSound(gameCharacter.getEmitter(), "Open");
				this.Locked = false;
				this.setLockedByKey(false);
			}

			boolean boolean1 = gameCharacter instanceof IsoPlayer && !gameCharacter.getCurrentSquare().isOutside();
			if ("Tutorial".equals(Core.getInstance().getGameMode()) && this.isLockedByKey()) {
				boolean1 = false;
			}

			boolean boolean2;
			if (gameCharacter instanceof IsoPlayer && this.getSprite().getProperties().Is("GarageDoor")) {
				boolean2 = this.getSprite().getProperties().Is("InteriorSide");
				if (boolean2) {
					boolean1 = this.north ? gameCharacter.getY() >= this.getY() : gameCharacter.getX() >= this.getX();
				} else {
					boolean1 = this.north ? gameCharacter.getY() < this.getY() : gameCharacter.getX() < this.getX();
				}
			}

			if (this.Locked && !boolean1 && !this.open) {
				this.playDoorSound(gameCharacter.getEmitter(), "Locked");
				this.setRenderEffect(RenderEffectType.Hit_Door, true);
			} else if (this.getSprite().getProperties().Is("DoubleDoor")) {
				if (isDoubleDoorObstructed(this)) {
					if (gameCharacter != null) {
						this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
						gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
					}
				} else {
					boolean2 = this.open;
					toggleDoubleDoor(this, true);
					if (boolean2 != this.open) {
						this.playDoorSound(gameCharacter.getEmitter(), this.open ? "Open" : "Close");
					}
				}
			} else if (this.getSprite().getProperties().Is("GarageDoor")) {
				if (isGarageDoorObstructed(this)) {
					if (gameCharacter != null) {
						this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
						gameCharacter.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
					}
				} else {
					boolean2 = this.open;
					toggleGarageDoor(this, true);
					if (boolean2 != this.open) {
						this.playDoorSound(gameCharacter.getEmitter(), this.open ? "Open" : "Close");
					}
				}
			} else if (this.isObstructed()) {
				if (gameCharacter != null) {
					this.playDoorSound(gameCharacter.getEmitter(), "Blocked");
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
				WeatherFxMask.forceMaskUpdateAll();
				this.sprite = this.closedSprite;
				if (this.open) {
					if (gameCharacter != null) {
						this.playDoorSound(gameCharacter.getEmitter(), "Open");
					}

					this.sprite = this.openSprite;
				} else if (gameCharacter != null) {
					this.playDoorSound(gameCharacter.getEmitter(), "Close");
				}

				this.square.RecalcProperties();
				this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
				PolygonalMap2.instance.squareChanged(this.square);
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
			PrintStream printStream = System.out;
			String string = this.getClass().getSimpleName();
			printStream.println("ERROR: " + string + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
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
						label59: while (true) {
							do {
								if (!iterator.hasNext()) {
									break label59;
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
			this.square.RecalcAllWithNeighbours(true);
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				LosUtil.cachecleared[int1] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
			LuaEventManager.triggerEvent("OnContainerUpdate");
			WeatherFxMask.forceMaskUpdateAll();
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

	public boolean isBarricadeAllowed() {
		return this.getSprite() != null && !this.getSprite().getProperties().Is("DoubleDoor") && !this.getSprite().getProperties().Is("GarageDoor");
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

	public void setLocked(boolean boolean1) {
		this.Locked = boolean1;
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
		} else {
			switch (this.getSpriteEdge(false)) {
			case N: 
				return vector2.set(this.getX() + 0.5F, this.getY());
			
			case S: 
				return vector2.set(this.getX() + 0.5F, this.getY() + 1.0F);
			
			case W: 
				return vector2.set(this.getX(), this.getY() + 0.5F);
			
			case E: 
				return vector2.set(this.getX() + 1.0F, this.getY() + 0.5F);
			
			default: 
				throw new IllegalStateException();
			
			}
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
			PrintStream printStream = System.out;
			int int1 = this.square.getX();
			printStream.println("ERROR: Door not found on square " + int1 + ", " + this.square.getY() + ", " + this.square.getZ());
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
			this.curtainW = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainW.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_16");
			this.curtainW.def.setScale(0.8F, 0.8F);
			this.curtainWopen = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainWopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_20");
			this.curtainWopen.def.setScale(0.8F, 0.8F);
			this.curtainE = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainE.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_17");
			this.curtainE.def.setScale(0.8F, 0.8F);
			this.curtainEopen = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainEopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_21");
			this.curtainEopen.def.setScale(0.8F, 0.8F);
			this.curtainN = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainN.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_18");
			this.curtainN.def.setScale(0.8F, 0.8F);
			this.curtainNopen = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainNopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_22");
			this.curtainNopen.def.setScale(0.8F, 0.8F);
			this.curtainS = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.curtainS.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_19");
			this.curtainS.def.setScale(0.8F, 0.8F);
			this.curtainSopen = IsoSprite.CreateSprite(IsoSpriteManager.instance);
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
					this.square.RecalcAllWithNeighbours(true);
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
			switch (this.getSpriteEdge(false)) {
			case N: 
				boolean1 = this.north == square.getY() >= square2.getY();
				break;
			
			case S: 
				boolean1 = square.getY() > square2.getY();
				break;
			
			case W: 
				boolean1 = this.north == square.getX() < square2.getX();
				break;
			
			case E: 
				boolean1 = square.getX() > square2.getX();
				break;
			
			default: 
				throw new IllegalStateException();
			
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
			switch (this.getSpriteEdge(false)) {
			case N: 
				return square.getY() >= square2.getY() ? square2 : this.getCell().getGridSquare(square2.x, square2.y - 1, square2.z);
			
			case S: 
				return square.getY() <= square2.getY() ? square2 : this.getCell().getGridSquare(square2.x, square2.y + 1, square2.z);
			
			case W: 
				return square.getX() >= square2.getX() ? square2 : this.getCell().getGridSquare(square2.x - 1, square2.y, square2.z);
			
			case E: 
				return square.getX() <= square2.getX() ? square2 : this.getCell().getGridSquare(square2.x + 1, square2.y, square2.z);
			
			default: 
				throw new IllegalStateException();
			
			}
		} else {
			return null;
		}
	}

	public IsoGridSquare getSheetSquare() {
		if (!this.bHasCurtain) {
			return null;
		} else {
			switch (this.getSpriteEdge(false)) {
			case N: 
				if (this.open) {
					return this.bCurtainInside ? this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ()) : this.getSquare();
				}

				return this.bCurtainInside ? this.getSquare() : this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
			
			case S: 
				return this.bCurtainInside ? this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() + 1.0F), (double)this.getZ()) : this.getSquare();
			
			case W: 
				if (this.open) {
					return this.bCurtainInside ? this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ()) : this.getSquare();
				}

				return this.bCurtainInside ? this.getSquare() : this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
			
			case E: 
				return this.bCurtainInside ? this.getCell().getGridSquare((double)(this.getX() + 1.0F), (double)this.getY(), (double)this.getZ()) : this.getSquare();
			
			default: 
				throw new IllegalStateException();
			
			}
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

			IsoDirections directions2 = this.getSpriteEdge(false);
			if (directions2 == IsoDirections.E) {
				directions = this.bCurtainInside ? IsoDirections.W : IsoDirections.E;
			}

			if (directions2 == IsoDirections.S) {
				directions = this.bCurtainInside ? IsoDirections.N : IsoDirections.S;
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

			object.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, sprite.getName(), int2);
			object.getSquare().RecalcAllWithNeighbours(true);
			if (int1 != 2 && int1 != 3) {
				PolygonalMap2.instance.squareChanged(object.getSquare());
			} else {
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
				PolygonalMap2.instance.squareChanged(square);
				square = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, square.getZ());
				if (square == null) {
					return;
				}

				if (thumpable != null) {
					IsoThumpable thumpable2 = new IsoThumpable(square.getCell(), square, object.getSprite().getName(), boolean1, thumpable.getTable());
					thumpable2.setModData(thumpable.getModData());
					thumpable2.setCanBeLockByPadlock(thumpable.canBeLockByPadlock());
					thumpable2.setCanBePlastered(thumpable.canBePlastered());
					thumpable2.setIsHoppable(thumpable.isHoppable());
					thumpable2.setIsDismantable(thumpable.isDismantable());
					thumpable2.setName(thumpable.getName());
					thumpable2.setIsDoor(true);
					thumpable2.setIsThumpable(thumpable.isThumpable());
					thumpable2.setThumpDmg(thumpable.getThumpDmg());
					thumpable2.setThumpSound(thumpable.getThumpSound());
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

				PolygonalMap2.instance.squareChanged(square);
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

	public static boolean destroyDoubleDoor(IsoObject object) {
		int int1 = getDoubleDoorIndex(object);
		if (int1 == -1) {
			return false;
		} else {
			if (int1 == 1 || int1 == 4) {
				IsoObject object2 = getDoubleDoorObject(object, int1 == 1 ? 2 : 3);
				if (object2 instanceof IsoDoor) {
					((IsoDoor)object2).destroy();
				} else if (object2 instanceof IsoThumpable) {
					((IsoThumpable)object2).destroy();
				}
			}

			if (object instanceof IsoDoor) {
				((IsoDoor)object).destroy();
			} else if (object instanceof IsoThumpable) {
				((IsoThumpable)object).destroy();
			}

			LuaEventManager.triggerEvent("OnContainerUpdate");
			return true;
		}
	}

	public static int getGarageDoorIndex(IsoObject object) {
		if (object != null && object.getSquare() != null) {
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null && propertyContainer.Is("GarageDoor")) {
				int int1 = Integer.parseInt(propertyContainer.Val("GarageDoor"));
				if (int1 >= 1 && int1 <= 6) {
					IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
					IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
					if (door == null && thumpable == null) {
						return -1;
					} else {
						boolean boolean1 = door == null ? thumpable.open : door.open;
						if (boolean1) {
							return int1 >= 4 ? int1 - 3 : -1;
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

	public static IsoObject getGarageDoorPrev(IsoObject object) {
		int int1 = getGarageDoorIndex(object);
		if (int1 == -1) {
			return null;
		} else if (int1 == 1) {
			return null;
		} else {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.north : door.north;
			IsoGridSquare square = object.getSquare();
			int int2 = square.x - (boolean1 ? 1 : 0);
			int int3 = square.y + (boolean1 ? 0 : 1);
			square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, square.getZ());
			if (square == null) {
				return null;
			} else {
				ArrayList arrayList = square.getSpecialObjects();
				int int4;
				IsoObject object2;
				if (door != null) {
					for (int4 = 0; int4 < arrayList.size(); ++int4) {
						object2 = (IsoObject)arrayList.get(int4);
						if (object2 instanceof IsoDoor && ((IsoDoor)object2).north == boolean1 && getGarageDoorIndex(object2) <= int1) {
							return object2;
						}
					}
				}

				if (thumpable != null) {
					for (int4 = 0; int4 < arrayList.size(); ++int4) {
						object2 = (IsoObject)arrayList.get(int4);
						if (object2 instanceof IsoThumpable && ((IsoThumpable)object2).north == boolean1 && getGarageDoorIndex(object2) <= int1) {
							return object2;
						}
					}
				}

				return null;
			}
		}
	}

	public static IsoObject getGarageDoorNext(IsoObject object) {
		int int1 = getGarageDoorIndex(object);
		if (int1 == -1) {
			return null;
		} else if (int1 == 3) {
			return null;
		} else {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.north : door.north;
			IsoGridSquare square = object.getSquare();
			int int2 = square.x + (boolean1 ? 1 : 0);
			int int3 = square.y - (boolean1 ? 0 : 1);
			square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, square.getZ());
			if (square == null) {
				return null;
			} else {
				ArrayList arrayList = square.getSpecialObjects();
				int int4;
				IsoObject object2;
				if (door != null) {
					for (int4 = 0; int4 < arrayList.size(); ++int4) {
						object2 = (IsoObject)arrayList.get(int4);
						if (object2 instanceof IsoDoor && ((IsoDoor)object2).north == boolean1 && getGarageDoorIndex(object2) >= int1) {
							return object2;
						}
					}
				}

				if (thumpable != null) {
					for (int4 = 0; int4 < arrayList.size(); ++int4) {
						object2 = (IsoObject)arrayList.get(int4);
						if (object2 instanceof IsoThumpable && ((IsoThumpable)object2).north == boolean1 && getGarageDoorIndex(object2) >= int1) {
							return object2;
						}
					}
				}

				return null;
			}
		}
	}

	public static IsoObject getGarageDoorFirst(IsoObject object) {
		int int1 = getGarageDoorIndex(object);
		if (int1 == -1) {
			return null;
		} else if (int1 == 1) {
			return object;
		} else {
			for (IsoObject object2 = getGarageDoorPrev(object); object2 != null; object2 = getGarageDoorPrev(object2)) {
				if (getGarageDoorIndex(object2) == 1) {
					return object2;
				}
			}

			return object;
		}
	}

	private static void toggleGarageDoorObject(IsoObject object) {
		int int1 = getGarageDoorIndex(object);
		if (int1 != -1) {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.open : door.open;
			if (door != null) {
				door.open = !boolean1;
				door.setLockedByKey(false);
				door.sprite = door.open ? door.openSprite : door.closedSprite;
			}

			if (thumpable != null) {
				thumpable.open = !boolean1;
				thumpable.setLockedByKey(false);
				thumpable.sprite = thumpable.open ? thumpable.openSprite : thumpable.closedSprite;
			}

			object.getSquare().RecalcAllWithNeighbours(true);
			object.syncIsoObject(false, (byte)(boolean1 ? 0 : 1), (UdpConnection)null, (ByteBuffer)null);
			PolygonalMap2.instance.squareChanged(object.getSquare());
		}
	}

	public static void toggleGarageDoor(IsoObject object, boolean boolean1) {
		int int1 = getGarageDoorIndex(object);
		if (int1 != -1) {
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			if (boolean1 && thumpable != null) {
				thumpable.syncIsoObject(false, (byte)(thumpable.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
			}

			toggleGarageDoorObject(object);
			for (IsoObject object2 = getGarageDoorPrev(object); object2 != null; object2 = getGarageDoorPrev(object2)) {
				toggleGarageDoorObject(object2);
			}

			for (IsoObject object3 = getGarageDoorNext(object); object3 != null; object3 = getGarageDoorNext(object3)) {
				toggleGarageDoorObject(object3);
			}

			for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				LosUtil.cachecleared[int2] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}

	private static boolean isGarageDoorObstructed(IsoObject object) {
		int int1 = getGarageDoorIndex(object);
		if (int1 == -1) {
			return false;
		} else {
			IsoDoor door = object instanceof IsoDoor ? (IsoDoor)object : null;
			IsoThumpable thumpable = object instanceof IsoThumpable ? (IsoThumpable)object : null;
			boolean boolean1 = door == null ? thumpable.north : door.north;
			boolean boolean2 = door == null ? thumpable.open : door.open;
			if (!boolean2) {
				return false;
			} else {
				int int2 = object.square.x;
				int int3 = object.square.y;
				int int4 = int2;
				int int5 = int3;
				IsoObject object2;
				IsoObject object3;
				if (boolean1) {
					for (object2 = getGarageDoorPrev(object); object2 != null; object2 = getGarageDoorPrev(object2)) {
						--int2;
					}

					for (object3 = getGarageDoorNext(object); object3 != null; object3 = getGarageDoorNext(object3)) {
						++int4;
					}
				} else {
					for (object2 = getGarageDoorPrev(object); object2 != null; object2 = getGarageDoorPrev(object2)) {
						++int5;
					}

					for (object3 = getGarageDoorNext(object); object3 != null; object3 = getGarageDoorNext(object3)) {
						--int3;
					}
				}

				int int6 = (int2 - 4) / 10;
				int int7 = (int3 - 4) / 10;
				int int8 = (int)Math.ceil((double)((int4 + 4) / 10));
				int int9 = (int)Math.ceil((double)((int5 + 4) / 10));
				int int10 = object.square.z;
				for (int int11 = int7; int11 <= int9; ++int11) {
					for (int int12 = int6; int12 <= int8; ++int12) {
						IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int12, int11) : IsoWorld.instance.CurrentCell.getChunk(int12, int11);
						if (chunk != null) {
							for (int int13 = 0; int13 < chunk.vehicles.size(); ++int13) {
								BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int13);
								for (int int14 = int3; int14 <= int5; ++int14) {
									for (int int15 = int2; int15 <= int4; ++int15) {
										if (baseVehicle.isIntersectingSquare(int15, int14, int10) && baseVehicle.isIntersectingSquare(int15 - (boolean1 ? 0 : 1), int14 - (boolean1 ? 1 : 0), int10)) {
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
	}

	public static boolean destroyGarageDoor(IsoObject object) {
		int int1 = getGarageDoorIndex(object);
		if (int1 == -1) {
			return false;
		} else {
			IsoObject object2;
			for (IsoObject object3 = getGarageDoorPrev(object); object3 != null; object3 = object2) {
				object2 = getGarageDoorPrev(object3);
				if (object3 instanceof IsoDoor) {
					((IsoDoor)object3).destroy();
				} else if (object3 instanceof IsoThumpable) {
					((IsoThumpable)object3).destroy();
				}
			}

			IsoObject object4;
			for (object2 = getGarageDoorNext(object); object2 != null; object2 = object4) {
				object4 = getGarageDoorNext(object2);
				if (object2 instanceof IsoDoor) {
					((IsoDoor)object2).destroy();
				} else if (object2 instanceof IsoThumpable) {
					((IsoThumpable)object2).destroy();
				}
			}

			if (object instanceof IsoDoor) {
				((IsoDoor)object).destroy();
			} else if (object instanceof IsoThumpable) {
				((IsoThumpable)object).destroy();
			}

			LuaEventManager.triggerEvent("OnContainerUpdate");
			return true;
		}
	}

	public IsoObject getRenderEffectMaster() {
		int int1 = getDoubleDoorIndex(this);
		IsoObject object;
		if (int1 != -1) {
			object = null;
			if (int1 == 2) {
				object = getDoubleDoorObject(this, 1);
			} else if (int1 == 3) {
				object = getDoubleDoorObject(this, 4);
			}

			if (object != null) {
				return object;
			}
		} else {
			object = getGarageDoorFirst(this);
			if (object != null) {
				return object;
			}
		}

		return this;
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

	public static enum DoorType {

		WeakWooden,
		StrongWooden;

		private static IsoDoor.DoorType[] $values() {
			return new IsoDoor.DoorType[]{WeakWooden, StrongWooden};
		}
	}
}
