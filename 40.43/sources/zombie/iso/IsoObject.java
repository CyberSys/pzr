package zombie.iso;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.bucket.BucketManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderSettings;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCrate;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWoodenWall;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.ObjectTooltip;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayList;
import zombie.vehicles.BaseVehicle;


public class IsoObject implements Serializable {
	public static final byte OBF_Highlighted = 1;
	public static final byte OBF_HighlightRenderOnce = 2;
	public static final byte OBF_Blink = 4;
	public byte highlightFlags;
	private boolean specialTooltip = false;
	public int keyId = -1;
	private ColorInfo highlightColor = new ColorInfo(0.9F, 1.0F, 0.0F, 1.0F);
	public BaseSoundEmitter emitter;
	public float sheetRopeHealth = 100.0F;
	public boolean sheetRope = false;
	private static int DefaultCondition = 0;
	ArrayList Children;
	public float[] alpha = new float[4];
	public static float alphaStep = 0.03F;
	public boolean bNeverDoneAlpha = true;
	public ArrayList AttachedAnimSprite;
	public ArrayList AttachedAnimSpriteActual;
	public static final int MAX_WALL_SPLATS = 32;
	public ArrayList wallBloodSplats;
	public ItemContainer container = null;
	private ArrayList secondaryContainers;
	public IsoDirections dir;
	public short Damage;
	public boolean NoPicking;
	public float offsetX;
	public float offsetY;
	public boolean OutlineOnMouseover;
	public IsoObject rerouteMask;
	public IsoSprite sprite;
	public IsoSprite overlaySprite;
	public ColorInfo overlaySpriteColor;
	public IsoGridSquare square;
	public float[] targetAlpha;
	public IsoObject rerouteCollide;
	public KahluaTable table;
	public String name;
	String tile;
	public static IsoObject lastRendered = null;
	public static IsoObject lastRenderedRendered = null;
	public float tintr;
	public float tintg;
	public float tintb;
	public static ColorInfo stCol = new ColorInfo();
	private static ColorInfo stCol2 = new ColorInfo();
	protected ObjectRenderEffects windRenderEffects;
	protected ObjectRenderEffects objectRenderEffects;
	private static final String PropMoveWithWind = "MoveWithWind";
	public String spriteName;
	public int sx;
	public int sy;
	private ColorInfo customColor;
	public boolean doNotSync;
	protected IsoObject externalWaterSource;
	protected boolean usesExternalWaterSource;
	private float renderYOffset;
	public static float rmod;
	public static float gmod;
	public static float bmod;
	private float blinkAlpha;
	private boolean blinkAlphaIncrease;
	public static boolean LowLightingQualityHack = false;
	private static ColorInfo colFxMask = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);

	public static IsoObject getNew(IsoGridSquare square, String string, String string2, boolean boolean1) {
		IsoObject object = null;
		if (CellLoader.isoObjectCache.isEmpty()) {
			object = new IsoObject(square, string, string2, boolean1);
		} else {
			object = (IsoObject)CellLoader.isoObjectCache.pop();
			object.reset();
		}

		if (boolean1) {
			object.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			object.sprite.LoadFramesNoDirPageSimple(object.tile);
		} else {
			object.sprite = (IsoSprite)square.getCell().SpriteManager.NamedMap.get(object.tile);
		}

		object.square = square;
		object.name = string2;
		return object;
	}

	public static boolean DoChecksumCheck(String string, String string2) {
		String string3 = "";
		try {
			string3 = getMD5Checksum(string);
			if (!string3.equals(string2)) {
				return false;
			}
		} catch (Exception exception) {
			string3 = "";
			try {
				string3 = getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + string);
			} catch (Exception exception2) {
				return false;
			}
		}

		return string3.equals(string2);
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putByte((byte)0);
	}

	public String getTextureName() {
		return this.sprite == null ? null : this.sprite.name;
	}

	public static boolean DoChecksumCheck() {
		if (!DoChecksumCheck("zombie/GameWindow.class", "c4a62b8857f0fb6b9c103ff6ef127a9b")) {
			return false;
		} else if (!DoChecksumCheck("zombie/GameWindow$1.class", "5d93dc446b2dc49092fe4ecb5edf5f17")) {
			return false;
		} else if (!DoChecksumCheck("zombie/GameWindow$2.class", "a3e3d2c8cf6f0efaa1bf7f6ceb572073")) {
			return false;
		} else if (!DoChecksumCheck("zombie/gameStates/MainScreenState.class", "206848ba7cb764293dd2c19780263854")) {
			return false;
		} else if (!DoChecksumCheck("zombie/FrameLoader$1.class", "0ebfcc9557cc28d53aa982a71616bf5b")) {
			return false;
		} else {
			return DoChecksumCheck("zombie/FrameLoader.class", "d5b1f7b2886a499d848c204f6a815776");
		}
	}

	public static byte[] createChecksum(String string) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(string);
		byte[] byteArray = new byte[1024];
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		int int1;
		do {
			int1 = fileInputStream.read(byteArray);
			if (int1 > 0) {
				messageDigest.update(byteArray, 0, int1);
			}
		} while (int1 != -1);

		fileInputStream.close();
		return messageDigest.digest();
	}

	public static String getMD5Checksum(String string) throws Exception {
		byte[] byteArray = createChecksum(string);
		String string2 = "";
		for (int int1 = 0; int1 < byteArray.length; ++int1) {
			string2 = string2 + Integer.toString((byteArray[int1] & 255) + 256, 16).substring(1);
		}

		return string2;
	}

	public static IsoObject getLastRendered() {
		return lastRendered;
	}

	public static void setLastRendered(IsoObject object) {
		lastRendered = object;
	}

	public static IsoObject getLastRenderedRendered() {
		return lastRenderedRendered;
	}

	public static void setLastRenderedRendered(IsoObject object) {
		lastRenderedRendered = object;
	}

	public static void setDefaultCondition(int int1) {
		DefaultCondition = int1;
	}

	public boolean Serialize() {
		return true;
	}

	public IsoObject(IsoCell cell) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		for (int int1 = 0; int1 < 4; ++int1) {
			this.alpha[int1] = 1.0F;
			this.targetAlpha[int1] = 1.0F;
		}
	}

	public KahluaTable getModData() {
		if (this.table == null) {
			this.table = LuaManager.platform.newTable();
		}

		return this.table;
	}

	public boolean hasModData() {
		return this.table != null && !this.table.isEmpty();
	}

	public static IsoObject getNew() {
		return CellLoader.isoObjectCache.isEmpty() ? new IsoObject() : (IsoObject)CellLoader.isoObjectCache.pop();
	}

	public static IsoObject factoryFromFileInput(IsoCell cell, int int1) {
		IsoObject object;
		if (int1 == "IsoObject".hashCode()) {
			if (CellLoader.isoObjectCache.isEmpty()) {
				return new IsoObject(cell);
			} else {
				object = (IsoObject)CellLoader.isoObjectCache.pop();
				object.sx = 0;
				return object;
			}
		} else if (int1 == "Player".hashCode()) {
			return new IsoPlayer(cell);
		} else if (int1 == "Survivor".hashCode()) {
			return new IsoSurvivor(cell);
		} else if (int1 == "Zombie".hashCode()) {
			return new IsoZombie(cell);
		} else if (int1 == "Pushable".hashCode()) {
			return new IsoPushableObject(cell);
		} else if (int1 == "WheelieBin".hashCode()) {
			return new IsoWheelieBin(cell);
		} else if (int1 == "WorldInventoryItem".hashCode()) {
			return new IsoWorldInventoryObject(cell);
		} else if (int1 == "Jukebox".hashCode()) {
			return new IsoJukebox(cell);
		} else if (int1 == "Curtain".hashCode()) {
			return new IsoCurtain(cell);
		} else if (int1 == "Radio".hashCode()) {
			return new IsoRadio(cell);
		} else if (int1 == "Television".hashCode()) {
			return new IsoTelevision(cell);
		} else if (int1 == "DeadBody".hashCode()) {
			return new IsoDeadBody(cell);
		} else if (int1 == "Barbecue".hashCode()) {
			return new IsoBarbecue(cell);
		} else if (int1 == "Fireplace".hashCode()) {
			return new IsoFireplace(cell);
		} else if (int1 == "Stove".hashCode()) {
			return new IsoStove(cell);
		} else if (int1 == "Door".hashCode()) {
			return new IsoDoor(cell);
		} else if (int1 == "Thumpable".hashCode()) {
			return new IsoThumpable(cell);
		} else if (int1 == "IsoTrap".hashCode()) {
			return new IsoTrap(cell);
		} else if (int1 == "IsoCarBatteryCharger".hashCode()) {
			return new IsoCarBatteryCharger(cell);
		} else if (int1 == "IsoGenerator".hashCode()) {
			return new IsoGenerator(cell);
		} else if (int1 == "IsoCompost".hashCode()) {
			return new IsoCompost(cell);
		} else if (int1 == "StoneFurnace".hashCode()) {
			return new BSFurnace(cell);
		} else if (int1 == "Window".hashCode()) {
			return new IsoWindow(cell);
		} else if (int1 == "Curtain".hashCode()) {
			return new IsoCurtain(cell);
		} else if (int1 == "Barricade".hashCode()) {
			return new IsoBarricade(cell);
		} else if (int1 == "Crate".hashCode()) {
			return new IsoCrate(cell);
		} else if (int1 == "Tree".hashCode()) {
			if (CellLoader.isoTreeCache.isEmpty()) {
				return new IsoTree(cell);
			} else {
				IsoTree tree = (IsoTree)CellLoader.isoTreeCache.pop();
				tree.sx = 0;
				return tree;
			}
		} else if (int1 == "LightSwitch".hashCode()) {
			return new IsoLightSwitch(cell);
		} else if (int1 == "ZombieGiblets".hashCode()) {
			return new IsoZombieGiblets(cell);
		} else if (int1 == "MolotovCocktail".hashCode()) {
			return new IsoMolotovCocktail(cell);
		} else if (int1 == "WoodenWall".hashCode()) {
			return new IsoWoodenWall(cell);
		} else if (int1 == "Fire".hashCode()) {
			return new IsoFire(cell);
		} else if (int1 == "Vehicle".hashCode() && !GameClient.bClient) {
			return new BaseVehicle(cell);
		} else {
			object = new IsoObject(cell);
			return object;
		}
	}

	public static Class factoryClassFromFileInput(IsoCell cell, int int1) {
		if (int1 == "IsoObject".hashCode()) {
			return IsoObject.class;
		} else if (int1 == "Player".hashCode()) {
			return IsoPlayer.class;
		} else if (int1 == "Survivor".hashCode()) {
			return IsoSurvivor.class;
		} else if (int1 == "Zombie".hashCode()) {
			return IsoZombie.class;
		} else if (int1 == "Pushable".hashCode()) {
			return IsoPushableObject.class;
		} else if (int1 == "WheelieBin".hashCode()) {
			return IsoWheelieBin.class;
		} else if (int1 == "WorldInventoryItem".hashCode()) {
			return IsoWorldInventoryObject.class;
		} else if (int1 == "Jukebox".hashCode()) {
			return IsoJukebox.class;
		} else if (int1 == "Curtain".hashCode()) {
			return IsoCurtain.class;
		} else if (int1 == "Radio".hashCode()) {
			return IsoRadio.class;
		} else if (int1 == "Television".hashCode()) {
			return IsoTelevision.class;
		} else if (int1 == "DeadBody".hashCode()) {
			return IsoDeadBody.class;
		} else if (int1 == "Barbecue".hashCode()) {
			return IsoBarbecue.class;
		} else if (int1 == "Fireplace".hashCode()) {
			return IsoFireplace.class;
		} else if (int1 == "Stove".hashCode()) {
			return IsoStove.class;
		} else if (int1 == "Door".hashCode()) {
			return IsoDoor.class;
		} else if (int1 == "Thumpable".hashCode()) {
			return IsoThumpable.class;
		} else if (int1 == "Window".hashCode()) {
			return IsoWindow.class;
		} else if (int1 == "Curtain".hashCode()) {
			return IsoCurtain.class;
		} else if (int1 == "Barricade".hashCode()) {
			return IsoBarricade.class;
		} else if (int1 == "Crate".hashCode()) {
			return IsoCrate.class;
		} else if (int1 == "Tree".hashCode()) {
			return IsoTree.class;
		} else if (int1 == "LightSwitch".hashCode()) {
			return IsoLightSwitch.class;
		} else if (int1 == "ZombieGiblets".hashCode()) {
			return IsoZombieGiblets.class;
		} else if (int1 == "MolotovCocktail".hashCode()) {
			return IsoMolotovCocktail.class;
		} else if (int1 == "WoodenWall".hashCode()) {
			return IsoWoodenWall.class;
		} else {
			return int1 == "Vehicle".hashCode() ? BaseVehicle.class : IsoObject.class;
		}
	}

	static IsoObject factoryFromFileInput(IsoCell cell, DataInputStream dataInputStream) throws IOException {
		boolean boolean1 = dataInputStream.readBoolean();
		if (!boolean1) {
			return null;
		} else {
			int int1 = dataInputStream.readInt();
			IsoObject object = factoryFromFileInput(cell, int1);
			return object;
		}
	}

	public static IsoObject factoryFromFileInput(IsoCell cell, ByteBuffer byteBuffer) throws IOException {
		boolean boolean1 = byteBuffer.get() != 0;
		if (!boolean1) {
			return null;
		} else {
			int int1 = byteBuffer.getInt();
			IsoObject object = factoryFromFileInput(cell, int1);
			return object;
		}
	}

	public IsoGridSquare getSquare() {
		return this.square;
	}

	public void update() {
		this.checkHaveElectricity();
	}

	public void renderlast() {
	}

	public void DirtySlice() {
	}

	public String getObjectName() {
		if (this.name != null) {
			return this.name;
		} else {
			return this.sprite != null && this.sprite.getParentObjectName() != null ? this.sprite.getParentObjectName() : "IsoObject";
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		int int2 = byteBuffer.getInt();
		int2 = IsoChunk.Fix2x(this.square, int2);
		this.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, int2);
		this.spriteName = GameWindow.ReadString(byteBuffer);
		if (int2 == -1) {
			this.sprite = IsoWorld.instance.spriteManager.getSprite("");
			assert this.sprite != null;
			assert this.sprite.ID == -1;
		}

		if (this.sprite == null) {
			this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.sprite.LoadFramesNoDirPageSimple(this.spriteName);
		}

		int int3 = byteBuffer.get() & 255;
		IsoWallBloodSplat wallBloodSplat;
		for (int int4 = 0; int4 < int3; ++int4) {
			if (this.AttachedAnimSprite == null) {
				this.AttachedAnimSprite = new ArrayList();
			}

			if (int3 == 10000) {
				this.AttachedAnimSprite = null;
			}

			int int5 = byteBuffer.getInt();
			IsoSprite sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, int5);
			if (sprite != null) {
				IsoSpriteInstance spriteInstance = sprite.newInstance();
				boolean boolean1 = byteBuffer.get() == 1;
				if (boolean1) {
					spriteInstance.offX = 0.0F;
					spriteInstance.offY = 0.0F;
					spriteInstance.offZ = 0.0F;
					spriteInstance.tintr = 1.0F;
					spriteInstance.tintg = 1.0F;
					spriteInstance.tintb = 1.0F;
					spriteInstance.alpha = 1.0F;
					spriteInstance.targetAlpha = 1.0F;
				} else {
					spriteInstance.offX = byteBuffer.getFloat();
					spriteInstance.offY = byteBuffer.getFloat();
					spriteInstance.offZ = byteBuffer.getFloat();
					spriteInstance.tintr = byteBuffer.getFloat();
					spriteInstance.tintg = byteBuffer.getFloat();
					spriteInstance.tintb = byteBuffer.getFloat();
				}

				spriteInstance.Flip = byteBuffer.get() != 0;
				spriteInstance.bCopyTargetAlpha = byteBuffer.get() != 0;
				if (sprite.name != null && sprite.name.startsWith("overlay_blood_")) {
					float float1 = (float)GameTime.getInstance().getWorldAgeHours();
					wallBloodSplat = new IsoWallBloodSplat(float1, sprite);
					if (this.wallBloodSplats == null) {
						this.wallBloodSplats = new ArrayList();
					}

					this.wallBloodSplats.add(wallBloodSplat);
				} else {
					if (this.AttachedAnimSpriteActual == null) {
						this.AttachedAnimSpriteActual = new ArrayList(4);
					}

					this.AttachedAnimSprite.add(spriteInstance);
					this.AttachedAnimSpriteActual.add(sprite);
				}
			} else {
				boolean boolean2 = byteBuffer.get() == 1;
				if (!boolean2) {
					byteBuffer.getFloat();
					byteBuffer.getFloat();
					byteBuffer.getFloat();
					byteBuffer.getFloat();
					byteBuffer.getFloat();
					byteBuffer.getFloat();
				}

				byteBuffer.get();
				byteBuffer.get();
			}
		}

		boolean boolean3 = byteBuffer.get() != 0;
		if (boolean3) {
			this.name = GameWindow.ReadString(byteBuffer);
		}

		byte byte1 = byteBuffer.get();
		for (int int6 = 0; int6 < byte1; ++int6) {
			try {
				ItemContainer itemContainer = new ItemContainer();
				itemContainer.ID = 0;
				itemContainer.parent = this;
				itemContainer.parent.square = this.square;
				itemContainer.SourceGrid = this.square;
				itemContainer.load(byteBuffer, int1, false);
				if (int6 == 0) {
					if (this instanceof IsoDeadBody) {
						itemContainer.Capacity = 8;
					}

					this.container = itemContainer;
				} else {
					this.addSecondaryContainer(itemContainer);
				}
			} catch (Exception exception) {
				if (this.container != null) {
					DebugLog.log("Failed to stream in container ID: " + this.container.ID);
				}

				throw new RuntimeException(exception);
			}
		}

		if (byteBuffer.get() != 0) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load(byteBuffer, int1);
		}

		this.setOutlineOnMouseover(byteBuffer.get() == 1);
		if (int1 >= 34 && byteBuffer.get() == 1) {
			String string = GameWindow.ReadString(byteBuffer);
			if (!string.isEmpty()) {
				this.overlaySprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
				this.overlaySprite.name = string;
			}

			if (int1 >= 37 && byteBuffer.get() == 1) {
				this.setOverlaySpriteColor(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
			}
		}

		if (int1 >= 38) {
			this.setSpecialTooltip(byteBuffer.get() == 1);
		}

		if (int1 >= 57) {
			this.keyId = byteBuffer.getInt();
		}

		if (int1 >= 65) {
			byte byte2 = byteBuffer.get();
			if (byte2 > 0) {
				if (this.wallBloodSplats == null) {
					this.wallBloodSplats = new ArrayList();
				}

				int int7 = 0;
				if (GameClient.bClient || GameServer.bServer) {
					int7 = ServerOptions.getInstance().BloodSplatLifespanDays.getValue();
				}

				float float2 = (float)GameTime.getInstance().getWorldAgeHours();
				for (int int8 = 0; int8 < byte2; ++int8) {
					wallBloodSplat = new IsoWallBloodSplat();
					wallBloodSplat.load(byteBuffer, int1);
					if (wallBloodSplat.worldAge > float2) {
						wallBloodSplat.worldAge = float2;
					}

					if (int7 <= 0 || !(float2 - wallBloodSplat.worldAge >= (float)(int7 * 24))) {
						this.wallBloodSplats.add(wallBloodSplat);
					}
				}
			}
		}

		if (int1 >= 75) {
			this.usesExternalWaterSource = byteBuffer.get() == 1;
			this.renderYOffset = byteBuffer.getFloat();
		}

		if (int1 >= 92 && byteBuffer.get() == 1) {
			this.sheetRope = true;
			this.sheetRopeHealth = byteBuffer.getFloat();
		}

		if (int1 >= 95 && byteBuffer.get() == 1) {
			this.customColor = new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F);
		}

		if (int1 >= 123) {
			this.doNotSync = byteBuffer.get() == 1;
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
		if (this.Serialize()) {
			byteBuffer.putInt(this.getObjectName().hashCode());
			byteBuffer.putInt(this.sprite == null ? -1 : this.sprite.ID);
			GameWindow.WriteString(byteBuffer, this.spriteName);
			if (GameWindow.DEBUG_SAVE) {
				GameWindow.WriteString(byteBuffer, "Writing attached sprites");
			}

			int int1;
			int int2;
			if (this.AttachedAnimSprite == null) {
				byteBuffer.put((byte)0);
			} else {
				int1 = this.AttachedAnimSprite.size() > 255 ? 255 : this.AttachedAnimSprite.size();
				byteBuffer.put((byte)int1);
				for (int2 = 0; int2 < int1; ++int2) {
					IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
					byteBuffer.putInt(spriteInstance.getID());
					if (spriteInstance.offX == 0.0F && spriteInstance.offY == 0.0F && spriteInstance.offZ == 0.0F && spriteInstance.tintr == 1.0F && spriteInstance.tintg == 1.0F && spriteInstance.tintb == 1.0F) {
						byteBuffer.put((byte)1);
					} else {
						byteBuffer.put((byte)0);
						byteBuffer.putFloat(spriteInstance.offX);
						byteBuffer.putFloat(spriteInstance.offY);
						byteBuffer.putFloat(spriteInstance.offZ);
						byteBuffer.putFloat(spriteInstance.tintr);
						byteBuffer.putFloat(spriteInstance.tintg);
						byteBuffer.putFloat(spriteInstance.tintb);
					}

					byteBuffer.put((byte)(spriteInstance.Flip ? 1 : 0));
					byteBuffer.put((byte)(spriteInstance.bCopyTargetAlpha ? 1 : 0));
				}
			}

			if (GameWindow.DEBUG_SAVE) {
				GameWindow.WriteString(byteBuffer, "Writing name");
			}

			if (this.name != null) {
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.name);
			} else {
				byteBuffer.put((byte)0);
			}

			if (GameWindow.DEBUG_SAVE) {
				GameWindow.WriteString(byteBuffer, "Writing container");
			}

			byteBuffer.put((byte)this.getContainerCount());
			for (int1 = 0; int1 < this.getContainerCount(); ++int1) {
				this.getContainerByIndex(int1).save(byteBuffer, false);
			}

			if (this.table != null && !this.table.isEmpty()) {
				byteBuffer.put((byte)1);
				this.table.save(byteBuffer);
			} else {
				byteBuffer.put((byte)0);
			}

			byteBuffer.put((byte)(this.isOutlineOnMouseover() ? 1 : 0));
			if (this.getOverlaySprite() != null) {
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.getOverlaySprite().name);
				if (this.getOverlaySpriteColor() != null) {
					byteBuffer.put((byte)1);
					byteBuffer.putFloat(this.getOverlaySpriteColor().r);
					byteBuffer.putFloat(this.getOverlaySpriteColor().g);
					byteBuffer.putFloat(this.getOverlaySpriteColor().b);
					byteBuffer.putFloat(this.getOverlaySpriteColor().a);
				} else {
					byteBuffer.put((byte)0);
				}
			} else {
				byteBuffer.put((byte)0);
			}

			byteBuffer.put((byte)(this.haveSpecialTooltip() ? 1 : 0));
			byteBuffer.putInt(this.getKeyId());
			if (this.wallBloodSplats == null) {
				byteBuffer.put((byte)0);
			} else {
				int1 = Math.min(this.wallBloodSplats.size(), 32);
				int2 = this.wallBloodSplats.size() - int1;
				byteBuffer.put((byte)int1);
				for (int int3 = int2; int3 < this.wallBloodSplats.size(); ++int3) {
					((IsoWallBloodSplat)this.wallBloodSplats.get(int3)).save(byteBuffer);
				}
			}

			byteBuffer.put((byte)(this.usesExternalWaterSource ? 1 : 0));
			byteBuffer.putFloat(this.renderYOffset);
			if (this.sheetRope) {
				byteBuffer.put((byte)1);
				byteBuffer.putFloat(this.sheetRopeHealth);
			} else {
				byteBuffer.put((byte)0);
			}

			if (this.customColor != null) {
				byteBuffer.put((byte)1);
				byteBuffer.putFloat(this.customColor.r);
				byteBuffer.putFloat(this.customColor.g);
				byteBuffer.putFloat(this.customColor.b);
			} else {
				byteBuffer.put((byte)0);
			}

			byteBuffer.put((byte)(this.doNotSync ? 1 : 0));
		}
	}

	public void saveState(ByteBuffer byteBuffer) {
	}

	public void loadState(ByteBuffer byteBuffer) {
	}

	public void softReset() {
		if (this.container != null) {
			this.container.Items.clear();
			this.container.bExplored = false;
			this.setOverlaySprite((String)null, -1.0F, -1.0F, -1.0F, -1.0F, false);
		}

		if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
			for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
				IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int1);
				if (sprite.name != null && sprite.name.contains("blood")) {
					this.AttachedAnimSprite.remove(int1);
					this.AttachedAnimSpriteActual.remove(int1);
				}
			}
		}
	}

	public void AttackObject(IsoGameCharacter gameCharacter) {
		this.Damage = (short)(this.Damage - 10);
		HandWeapon handWeapon = (HandWeapon)gameCharacter.getPrimaryHandItem();
		SoundManager.instance.PlaySound(handWeapon.getDoorHitSound(), false, 2.0F);
		WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
		if (this.Damage <= 0) {
			this.square.getObjects().remove(this);
			this.square.RecalcAllWithNeighbours(true);
			if (this.getType() == IsoObjectType.stairsBN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsBW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsTW) {
				this.square.RemoveAllWith(IsoFlagType.attachtostairs);
			}

			byte byte1 = 1;
			for (int int1 = 0; int1 < byte1; ++int1) {
				InventoryItem inventoryItem = this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
				inventoryItem.setUses(1);
			}
		}
	}

	public void onMouseRightClick(int int1, int int2) {
	}

	public void onMouseRightReleased() {
	}

	public void Hit(Vector2 vector2, IsoObject object, float float1) {
		if (object instanceof BaseVehicle) {
			this.Damage(float1);
		}
	}

	public void Damage(float float1) {
		short short1 = this.Damage;
		this.Damage = (short)((int)((double)this.Damage - (double)float1 * 0.1));
		SoundManager.instance.PlayWorldSound("VehicleHitObject", this.square, 1.0F, 20.0F, 1.0F, true);
		WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		if (this.getProperties().Is("HitByCar") && this.getSprite().getProperties().Val("DamagedSprite") != null && !this.getSprite().getProperties().Val("DamagedSprite").equals("") && this.Damage <= 90 && short1 > 90) {
			this.setSprite(IsoWorld.instance.CurrentCell.SpriteManager.getSprite(this.getSprite().getProperties().Val("DamagedSprite")));
			if (this.getSprite().getProperties().Is("StopCar")) {
				this.getSprite().setType(IsoObjectType.isMoveAbleObject);
			} else {
				this.getSprite().setType(IsoObjectType.MAX);
			}

			if (this instanceof IsoThumpable) {
				((IsoThumpable)this).setBlockAllTheSquare(false);
			}

			if (GameServer.bServer) {
				this.transmitUpdatedSpriteToClients();
			}

			this.getSquare().RecalcProperties();
			this.Damage = 50;
		}

		if (this.Damage <= 40 && this.getProperties().Is("HitByCar")) {
			this.getSquare().transmitRemoveItemFromSquare(this);
		}
	}

	public void Collision(Vector2 vector2, IsoObject object) {
		if (object instanceof BaseVehicle) {
			if (this.getProperties().Is("CarSlowFactor")) {
				int int1 = Integer.parseInt(this.getProperties().Val("CarSlowFactor"));
				BaseVehicle baseVehicle = (BaseVehicle)object;
				baseVehicle.ApplyImpulse(this, Math.abs(baseVehicle.getMass() * baseVehicle.getCurrentSpeedKmHour() * (float)int1 / 100.0F));
			}

			if (this.getProperties().Is("HitByCar")) {
				BaseVehicle baseVehicle2 = (BaseVehicle)object;
				String string = this.getSprite().getProperties().Val("MinimumCarSpeedDmg");
				if (string == null) {
					string = "150";
				}

				if (Math.abs(baseVehicle2.getCurrentSpeedKmHour()) > (float)Integer.parseInt(string)) {
					this.Damage(Math.abs(baseVehicle2.getMass() * baseVehicle2.getCurrentSpeedKmHour()) / 300.0F);
				} else {
					baseVehicle2.ApplyImpulse(this, Math.abs(baseVehicle2.getMass() * baseVehicle2.getCurrentSpeedKmHour() * 10.0F / 200.0F));
					baseVehicle2.jniSpeed = 0.0F;
				}

				if (baseVehicle2.getCurrentSpeedKmHour() > 3.0F) {
					baseVehicle2.ApplyImpulse(this, Math.abs(baseVehicle2.getMass() * baseVehicle2.getCurrentSpeedKmHour() * 10.0F / 150.0F));
				}
			}
		}
	}

	public void UnCollision(IsoObject object) {
	}

	public float GetVehicleSlowFactor(BaseVehicle baseVehicle) {
		if (this.getProperties().Is("CarSlowFactor")) {
			int int1 = Integer.parseInt(this.getProperties().Val("CarSlowFactor"));
			return 33.0F - (float)(10 - int1);
		} else {
			return 0.0F;
		}
	}

	public IsoObject getRerouteCollide() {
		return this.rerouteCollide;
	}

	public void setRerouteCollide(IsoObject object) {
		this.rerouteCollide = object;
	}

	public KahluaTable getTable() {
		return this.table;
	}

	public void setTable(KahluaTable kahluaTable) {
		this.table = kahluaTable;
	}

	public float getAlpha() {
		return this.alpha[IsoPlayer.getPlayerIndex()];
	}

	public void setAlpha(float float1) {
		this.alpha[IsoPlayer.getPlayerIndex()] = float1;
	}

	public float getAlphaStep() {
		return alphaStep;
	}

	public void setAlphaStep(float float1) {
		alphaStep = float1;
	}

	public ArrayList getAttachedAnimSprite() {
		return this.AttachedAnimSprite;
	}

	public void setAttachedAnimSprite(ArrayList arrayList) {
		this.AttachedAnimSprite = arrayList;
	}

	public IsoCell getCell() {
		return IsoWorld.instance.CurrentCell;
	}

	public ArrayList getChildSprites() {
		return this.AttachedAnimSprite;
	}

	public void setChildSprites(ArrayList arrayList) {
		this.AttachedAnimSprite = arrayList;
	}

	public ItemContainer getContainer() {
		return this.container;
	}

	public void setContainer(ItemContainer itemContainer) {
		this.container = itemContainer;
	}

	public IsoDirections getDir() {
		return this.dir;
	}

	public void setDir(IsoDirections directions) {
		this.dir = directions;
	}

	public void setDir(int int1) {
		this.dir = IsoDirections.fromIndex(int1);
	}

	public short getDamage() {
		return this.Damage;
	}

	public void setDamage(short short1) {
		this.Damage = short1;
	}

	public boolean isNoPicking() {
		return this.NoPicking;
	}

	public void setNoPicking(boolean boolean1) {
		this.NoPicking = boolean1;
	}

	public void setOffsetX(float float1) {
		this.offsetX = float1;
	}

	public void setOffsetY(float float1) {
		this.offsetY = float1;
	}

	public boolean isOutlineOnMouseover() {
		return this.OutlineOnMouseover;
	}

	public void setOutlineOnMouseover(boolean boolean1) {
		this.OutlineOnMouseover = boolean1;
	}

	public IsoObject getRerouteMask() {
		return this.rerouteMask;
	}

	public void setRerouteMask(IsoObject object) {
		this.rerouteMask = object;
	}

	public IsoSprite getSprite() {
		return this.sprite;
	}

	public void setSprite(IsoSprite sprite) {
		this.sprite = sprite;
		this.windRenderEffects = null;
		this.checkMoveWithWind();
	}

	public void setSprite(String string) {
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.sprite.LoadFramesNoDirPageSimple(string);
		this.tile = string;
		this.spriteName = string;
		this.windRenderEffects = null;
		this.checkMoveWithWind();
	}

	public void setSpriteFromName(String string) {
		this.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
		this.windRenderEffects = null;
		this.checkMoveWithWind();
	}

	public void setSquare(IsoGridSquare square) {
		this.square = square;
	}

	public float getTargetAlpha() {
		return this.targetAlpha[IsoPlayer.getPlayerIndex()];
	}

	public void setTargetAlpha(float float1) {
		this.targetAlpha[IsoPlayer.getPlayerIndex()] = float1;
	}

	public void setName(String string) {
		this.name = string;
	}

	public IsoObjectType getType() {
		return this.sprite == null ? IsoObjectType.MAX : this.sprite.getType();
	}

	public void setType(IsoObjectType objectType) {
		if (this.sprite != null) {
			this.sprite.setType(objectType);
		}
	}

	public void addChild(IsoObject object) {
		if (this.Children == null) {
			this.Children = new ArrayList(4);
		}

		this.Children.add(object);
	}

	public void debugPrintout() {
		System.out.println(this.getClass().toString());
		System.out.println(this.getObjectName());
	}

	protected void checkMoveWithWind() {
		this.checkMoveWithWind(this.sprite != null && this.sprite.isBush);
	}

	protected void checkMoveWithWind(boolean boolean1) {
		if (!GameServer.bServer) {
			if (this.sprite != null && this.windRenderEffects == null && this.sprite.moveWithWind) {
				this.windRenderEffects = ObjectRenderEffects.getNextWindEffect(this.sprite.windType, boolean1);
			} else {
				if (this.windRenderEffects != null && (this.sprite == null || !this.sprite.moveWithWind)) {
					this.windRenderEffects = null;
				}
			}
		}
	}

	public IsoObject() {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		for (int int1 = 0; int1 < 4; ++int1) {
			this.alpha[int1] = 1.0F;
			this.targetAlpha[int1] = 1.0F;
		}
	}

	public void reset() {
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.name = null;
		this.table = null;
		this.rerouteCollide = null;
		int int1;
		if (this.AttachedAnimSprite != null) {
			for (int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
				IsoSpriteInstance.add(spriteInstance);
			}

			this.AttachedAnimSprite.clear();
			if (this.AttachedAnimSpriteActual == null) {
				this.AttachedAnimSpriteActual = new ArrayList();
			}

			this.AttachedAnimSpriteActual.clear();
		}

		if (this.wallBloodSplats != null) {
			this.wallBloodSplats.clear();
		}

		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		if (this.container != null) {
			this.container.Items.clear();
			this.container.IncludingObsoleteItems.clear();
			this.container.setParent((IsoObject)null);
			this.container.setSourceGrid((IsoGridSquare)null);
			this.container.vehiclePart = null;
		}

		this.container = null;
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.square = null;
		for (int1 = 0; int1 < 4; ++int1) {
			this.alpha[int1] = 1.0F;
			this.targetAlpha[int1] = 1.0F;
		}

		this.bNeverDoneAlpha = true;
		this.highlightFlags = 0;
		this.tile = null;
		this.spriteName = null;
		this.specialTooltip = false;
		this.usesExternalWaterSource = false;
		this.externalWaterSource = null;
		if (this.secondaryContainers != null) {
			for (int1 = 0; int1 < this.secondaryContainers.size(); ++int1) {
				ItemContainer itemContainer = (ItemContainer)this.secondaryContainers.get(int1);
				itemContainer.Items.clear();
				itemContainer.IncludingObsoleteItems.clear();
				itemContainer.setParent((IsoObject)null);
				itemContainer.setSourceGrid((IsoGridSquare)null);
				itemContainer.vehiclePart = null;
			}

			this.secondaryContainers.clear();
		}

		this.renderYOffset = 0.0F;
		this.sx = 0;
		this.windRenderEffects = null;
		this.objectRenderEffects = null;
	}

	public IsoObject(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		this.sprite = sprite;
		this.square = square;
	}

	public IsoObject(IsoCell cell, IsoGridSquare square, String string) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		this.sprite = IsoWorld.instance.spriteManager.getSprite(string);
		this.square = square;
		this.tile = string;
	}

	public IsoObject(IsoGridSquare square, String string, String string2) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.sprite.LoadFramesNoDirPageSimple(string);
		this.square = square;
		this.tile = string;
		this.spriteName = string;
		this.name = string2;
	}

	public IsoObject(IsoGridSquare square, String string, String string2, boolean boolean1) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		if (boolean1) {
			this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.sprite.LoadFramesNoDirPageSimple(string);
		} else {
			this.sprite = (IsoSprite)square.getCell().SpriteManager.NamedMap.get(string);
		}

		this.tile = string;
		this.square = square;
		this.name = string2;
	}

	public IsoObject(IsoGridSquare square, String string, boolean boolean1) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		if (boolean1) {
			this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.sprite.LoadFramesNoDirPageSimple(string);
		} else {
			this.sprite = (IsoSprite)square.getCell().SpriteManager.NamedMap.get(string);
		}

		this.tile = string;
		this.square = square;
	}

	public IsoObject(IsoGridSquare square, String string) {
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.customColor = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.renderYOffset = 0.0F;
		this.blinkAlpha = 1.0F;
		this.blinkAlphaIncrease = false;
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.sprite.LoadFramesNoDirPageSimple(string);
		this.square = square;
	}

	public long customHashCode() {
		if (this.doNotSync) {
			return 0L;
		} else {
			try {
				long long1 = 1L;
				if (this.getObjectName() != null) {
					long1 = long1 * 3L + (long)this.getObjectName().hashCode();
				}

				if (this.name != null) {
					long1 = long1 * 2L + (long)this.name.hashCode();
				}

				if (this.container != null) {
					++long1;
					long1 += (long)this.container.Items.size();
					for (int int1 = 0; int1 < this.container.Items.size(); ++int1) {
						long1 += (long)(((InventoryItem)this.container.Items.get(int1)).getModule().hashCode() + ((InventoryItem)this.container.Items.get(int1)).getType().hashCode()) + ((InventoryItem)this.container.Items.get(int1)).id;
					}
				}

				long1 += (long)this.square.getObjects().indexOf(this);
				return long1;
			} catch (Throwable throwable) {
				DebugLog.log("ERROR: " + throwable.getMessage());
				return 0L;
			}
		}
	}

	public void SetName(String string) {
		this.name = string;
	}

	public String getName() {
		return this.name;
	}

	public String getSpriteName() {
		return this.spriteName;
	}

	public String getTile() {
		return this.tile;
	}

	public boolean isCharacter() {
		return this instanceof IsoLivingCharacter;
	}

	public boolean isZombie() {
		return this instanceof IsoZombie;
	}

	public String getScriptName() {
		return "none";
	}

	public void AttachAnim(String string, String string2, int int1, float float1, int int2, int int3, boolean boolean1, int int4, boolean boolean2, float float2, ColorInfo colorInfo) {
		if (this.AttachedAnimSprite == null) {
			this.AttachedAnimSprite = new ArrayList(4);
		}

		if (this.AttachedAnimSpriteActual == null) {
			this.AttachedAnimSpriteActual = new ArrayList(4);
		}

		IsoSprite sprite = IsoSprite.CreateSprite(this.getCell().SpriteManager);
		if (IsoSprite.HasCache(string + string2)) {
			sprite.LoadCache(string + string2);
		} else {
			sprite.LoadFramesNoDirPage(string, string2, int1);
			sprite.CacheAnims(string + string2);
		}

		sprite.TintMod.r = colorInfo.r;
		sprite.TintMod.g = colorInfo.g;
		sprite.TintMod.b = colorInfo.b;
		sprite.TintMod.a = colorInfo.a;
		Integer integer = int2;
		Integer integer2 = int3;
		sprite.soffX = (short)(-integer);
		sprite.soffY = (short)(-integer2);
		sprite.Animate = true;
		sprite.Loop = boolean1;
		sprite.DeleteWhenFinished = boolean2;
		sprite.PlayAnim(string2);
		IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(sprite);
		spriteInstance.AnimFrameIncrease = float1;
		spriteInstance.Frame = 0.0F;
		this.AttachedAnimSprite.add(spriteInstance);
		this.AttachedAnimSpriteActual.add(sprite);
	}

	public void AttachExistingAnim(IsoSprite sprite, int int1, int int2, boolean boolean1, int int3, boolean boolean2, float float1, ColorInfo colorInfo) {
		if (this.AttachedAnimSprite == null) {
			this.AttachedAnimSprite = new ArrayList(4);
		}

		if (this.AttachedAnimSpriteActual == null) {
			this.AttachedAnimSpriteActual = new ArrayList(4);
		}

		sprite.TintMod.r = colorInfo.r;
		sprite.TintMod.g = colorInfo.g;
		sprite.TintMod.b = colorInfo.b;
		sprite.TintMod.a = colorInfo.a;
		Integer integer = int1;
		Integer integer2 = int2;
		sprite.soffX = (short)(-integer);
		sprite.soffY = (short)(-integer2);
		sprite.Animate = true;
		sprite.Loop = boolean1;
		sprite.DeleteWhenFinished = boolean2;
		this.AttachedAnimSpriteActual.add(sprite);
		IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(sprite);
		this.AttachedAnimSprite.add(spriteInstance);
	}

	public void AttachExistingAnim(IsoSprite sprite, int int1, int int2, boolean boolean1, int int3, boolean boolean2, float float1) {
		this.AttachExistingAnim(sprite, int1, int2, boolean1, int3, boolean2, float1, new ColorInfo());
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
	}

	public void DoSpecialTooltip(ObjectTooltip objectTooltip, IsoGridSquare square) {
		if (this.haveSpecialTooltip()) {
			objectTooltip.setHeight(0.0);
			LuaEventManager.triggerEvent("DoSpecialTooltip", objectTooltip, square);
			if (objectTooltip.getHeight() == 0.0) {
				objectTooltip.hide();
			}
		}
	}

	public ItemContainer getItemContainer() {
		return this.container;
	}

	public float getOffsetX() {
		return this.offsetX;
	}

	public float getOffsetY() {
		return this.offsetY;
	}

	public IsoObject getRerouteMaskObject() {
		return this.rerouteMask;
	}

	public boolean HasTooltip() {
		return false;
	}

	public void setUsesExternalWaterSource(boolean boolean1) {
		this.usesExternalWaterSource = boolean1;
	}

	public boolean getUsesExternalWaterSource() {
		return this.usesExternalWaterSource;
	}

	public boolean hasExternalWaterSource() {
		return this.externalWaterSource != null;
	}

	public void doFindExternalWaterSource() {
		this.externalWaterSource = this.FindExternalWaterSource(this.getSquare(), true);
	}

	protected IsoObject FindExternalWaterSource(IsoGridSquare square, boolean boolean1) {
		if (square != null) {
			square = this.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
			if (square != null) {
				for (int int1 = 0; int1 < 8; ++int1) {
					IsoGridSquare square2 = square.getTileInDirection(IsoDirections.fromIndex(int1));
					if (square2 != null) {
						PZArrayList pZArrayList = square2.getObjects();
						for (int int2 = 0; int2 < pZArrayList.size(); ++int2) {
							IsoObject object = (IsoObject)pZArrayList.get(int2);
							if (!object.getUsesExternalWaterSource() && object.hasWater()) {
								return object;
							}
						}
					}
				}
			}
		}

		return null;
	}

	public void setWaterAmount(int int1) {
		if (this.usesExternalWaterSource) {
			if (!this.sprite.getProperties().Is(IsoFlagType.waterPiped) || GameTime.getInstance().getNightsSurvived() >= SandboxOptions.instance.getWaterShutModifier() || this.square == null || this.square.getRoom() == null) {
				if (this.externalWaterSource != null) {
					this.externalWaterSource.setWaterAmount(int1);
				}
			}
		} else {
			int1 = Math.max(0, int1);
			int int2 = this.getWaterAmount();
			if (int1 != int2) {
				this.getModData().rawset("waterAmount", (double)int1);
				if (int1 <= 0) {
					this.setTaintedWater(false);
				}

				LuaEventManager.triggerEvent("OnWaterAmountChange", this, int2);
			}
		}
	}

	public int getWaterAmount() {
		if (this.sprite == null) {
			return 0;
		} else if (this.usesExternalWaterSource) {
			if (this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier() && this.square != null && this.square.getRoom() != null) {
				return 10000;
			} else {
				if (this.externalWaterSource == null || !this.externalWaterSource.hasWater()) {
					this.doFindExternalWaterSource();
				}

				return this.externalWaterSource != null && this.externalWaterSource.hasWater() ? this.externalWaterSource.getWaterAmount() : 0;
			}
		} else if (this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier()) {
			return this.hasModData() && this.getModData().rawget("canBeWaterPiped") instanceof Boolean && (Boolean)this.getModData().rawget("canBeWaterPiped") ? 0 : 10000;
		} else {
			if (this.hasModData() && !this.getModData().isEmpty()) {
				KahluaTableIterator kahluaTableIterator = this.getModData().iterator();
				while (kahluaTableIterator.advance()) {
					if (kahluaTableIterator.getKey().toString().equals("waterAmount")) {
						Object object = kahluaTableIterator.getValue();
						if (object instanceof Double) {
							return (int)Math.max(0.0, (Double)object);
						}

						if (object instanceof String) {
							return Math.max(0, Integer.parseInt((String)object));
						}

						return 0;
					}
				}
			}

			if (!this.sprite.Properties.Is("waterAmount")) {
				return 0;
			} else {
				int int1 = Integer.parseInt(this.sprite.getProperties().Val("waterAmount"));
				return int1;
			}
		}
	}

	public int useWater(int int1) {
		if (this.sprite == null) {
			return 0;
		} else {
			int int2 = this.getWaterAmount();
			boolean boolean1 = false;
			int int3;
			if (int2 >= int1) {
				int3 = int1;
			} else {
				int3 = int2;
			}

			if (!this.usesExternalWaterSource) {
				if (this.sprite.getProperties().Is(IsoFlagType.water)) {
					return int3;
				}

				if (this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier()) {
					return int3;
				}
			}

			this.setWaterAmount(int2 - int3);
			return int3;
		}
	}

	public boolean hasWater() {
		return this.getWaterAmount() > 0;
	}

	public void setTaintedWater(boolean boolean1) {
		this.getModData().rawset("taintedWater", boolean1);
	}

	public boolean isTaintedWater() {
		if (this.hasModData()) {
			Object object = this.getModData().rawget("taintedWater");
			if (object instanceof Boolean) {
				return (Boolean)object;
			}
		}

		return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.taintedWater);
	}

	public InventoryItem replaceItem(InventoryItem inventoryItem) {
		String string = null;
		InventoryItem inventoryItem2 = null;
		if (inventoryItem != null && inventoryItem != null) {
			string = inventoryItem.getReplaceOnUseOn();
			if (string.split("-")[0].trim().contains(this.getObjectName())) {
				string = string.split("-")[1];
				if (!string.contains(".")) {
					string = inventoryItem.getModule() + "." + string;
				}
			} else if (string.split("-")[0].trim().contains("WaterSource")) {
				string = string.split("-")[1];
				if (!string.contains(".")) {
					string = inventoryItem.getModule() + "." + string;
				}
			} else {
				string = null;
			}
		}

		if (string != null && inventoryItem != null) {
			inventoryItem2 = inventoryItem.getContainer().AddItem(InventoryItemFactory.CreateItem(string));
			if (inventoryItem.getContainer().getParent() instanceof IsoGameCharacter) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)inventoryItem.getContainer().getParent();
				if (gameCharacter.getPrimaryHandItem() == inventoryItem) {
					gameCharacter.setPrimaryHandItem(inventoryItem2);
				}

				if (gameCharacter.getSecondaryHandItem() == inventoryItem) {
					gameCharacter.setSecondaryHandItem(inventoryItem2);
				}
			}

			inventoryItem.getContainer().Remove(inventoryItem);
		}

		return inventoryItem2;
	}

	public void useItemOn(InventoryItem inventoryItem) {
		String string = null;
		if (inventoryItem != null && inventoryItem != null) {
			string = inventoryItem.getReplaceOnUseOn();
			if (string.split("-")[0].trim().contains(this.getObjectName())) {
				string = string.split("-")[1];
				if (!string.contains(".")) {
					string = inventoryItem.getModule() + "." + string;
				}
			} else if (string.split("-")[0].trim().contains("WaterSource")) {
				string = string.split("-")[1];
				if (!string.contains(".")) {
					string = inventoryItem.getModule() + "." + string;
				}

				this.useWater(10);
			} else {
				string = null;
			}
		}

		if (string != null && inventoryItem != null) {
			InventoryItem inventoryItem2 = inventoryItem.getContainer().AddItem(InventoryItemFactory.CreateItem(string));
			inventoryItem.setUses(inventoryItem.getUses() - 1);
			if (inventoryItem.getUses() <= 0 && inventoryItem.getContainer() != null) {
				inventoryItem.getContainer().Items.remove(inventoryItem);
			}
		}
	}

	public float getX() {
		return (float)this.square.getX();
	}

	public float getY() {
		return (float)this.square.getY();
	}

	public float getZ() {
		return (float)this.square.getZ();
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoPlayer.instance.getBodyDamage().getOverallBodyHealth() > 0.0F) {
			float float1 = IsoPlayer.instance.DistTo(this.square.getX(), this.square.getY());
			String string = null;
			if (UIManager.getDragInventory() != null && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && UIManager.getDragInventory().getReplaceOnUseOn() != null) {
				string = UIManager.getDragInventory().getReplaceOnUseOn();
				if (string.split("-")[0].trim().contains(this.getObjectName())) {
					string = string.split("-")[1];
					if (!string.contains(".")) {
						string = UIManager.getDragInventory().getModule() + "." + string;
					}
				} else if (string.split("-")[0].trim().contains("WaterSource") && this.hasWater()) {
					string = string.split("-")[1];
					if (!string.contains(".")) {
						string = UIManager.getDragInventory().getModule() + "." + string;
					}

					this.useWater(10);
				} else {
					string = null;
				}
			}

			boolean boolean1 = false;
			int int3;
			String string2;
			IsoObject object;
			IsoGridSquare square;
			if (UIManager.getDragInventory() != null && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && UIManager.getDragInventory().getType().contains("SheetRope") && this.sprite != null && this.sprite.Properties.Is(IsoFlagType.windowN)) {
				square = this.square;
				int3 = 0;
				if (square.getProperties().Is(IsoFlagType.solidfloor)) {
					return false;
				}

				while (square != null && UIManager.getDragInventory() != null) {
					string2 = "TileRope_1";
					if (int3 > 0) {
						string2 = "TileRope_9";
					}

					object = new IsoObject(this.getCell(), square, string2);
					object.sprite.getProperties().Set(IsoFlagType.climbSheetN);
					if (int3 == 0) {
						object.sprite.getProperties().Set(IsoFlagType.climbSheetTopN);
						square.getProperties().Set(IsoFlagType.climbSheetTopN);
					}

					if (!square.getProperties().Is(IsoFlagType.climbSheetN)) {
						square.DirtySlice();
						UIManager.getDragInventory().Use();
					}

					square.getProperties().Set(IsoFlagType.climbSheetN);
					square.getObjects().add(object);
					++int3;
					if (boolean1) {
						break;
					}

					square = this.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() - 1);
					if (square != null && square.getProperties().Is(IsoFlagType.solidfloor)) {
						boolean1 = true;
					}
				}
			} else if (UIManager.getDragInventory() != null && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && UIManager.getDragInventory().getType().contains("SheetRope") && this.sprite != null && this.sprite.Properties.Is(IsoFlagType.windowN)) {
				square = this.square;
				int3 = 0;
				if (square.getProperties().Is(IsoFlagType.solidfloor)) {
					return false;
				}

				while (square != null && UIManager.getDragInventory() != null) {
					string2 = "TileRope_0";
					if (int3 > 0) {
						string2 = "TileRope_8";
					}

					object = new IsoObject(this.getCell(), square, string2);
					object.sprite.getProperties().Set(IsoFlagType.climbSheetW);
					if (int3 == 0) {
						object.sprite.getProperties().Set(IsoFlagType.climbSheetTopW);
						square.getProperties().Set(IsoFlagType.climbSheetTopW);
					}

					if (!square.getProperties().Is(IsoFlagType.climbSheetW)) {
						UIManager.getDragInventory().Use();
						square.DirtySlice();
					}

					square.getProperties().Set(IsoFlagType.climbSheetW);
					square.getObjects().add(object);
					++int3;
					if (boolean1) {
						break;
					}

					square = this.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() - 1);
					if (square != null && square.getProperties().Is(IsoFlagType.solidfloor)) {
						boolean1 = true;
					}
				}
			} else {
				if (this.rerouteMask != this && this.rerouteMask != null) {
					return this.rerouteMask.onMouseLeftClick(int1, int2);
				}

				if (UIManager.getDragInventory() != null && UIManager.getDragInventory().getType().contains("Sledgehammer") && (this.sprite.getProperties().Is(IsoFlagType.sledgesmash) || this.getType() == IsoObjectType.stairsTW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsBW || this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsBN || this.sprite.getProperties().Is(IsoFlagType.cutW) || this.sprite.getProperties().Is(IsoFlagType.cutN) || this.sprite.getProperties().Is(IsoFlagType.windowW) || this.sprite.getProperties().Is(IsoFlagType.windowN))) {
					if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) <= 2.0F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
						Vector2 vector2 = new Vector2((float)this.square.getX() + 0.5F, (float)this.square.getY() + 0.5F);
						vector2.x -= IsoPlayer.getInstance().getX();
						vector2.y -= IsoPlayer.getInstance().getY();
						vector2.normalize();
						IsoPlayer.getInstance().DirectionFromVector(vector2);
						IsoPlayer.getInstance().AttemptAttack();
						IsoPlayer.instance.setFakeAttack(true);
						IsoPlayer.instance.setFakeAttackTarget(this);
					}
				} else if (this.container != null) {
					if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.5F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
					}
				} else if (string != null && UIManager.getDragInventory() != null) {
					InventoryItem inventoryItem = UIManager.getDragInventory().getContainer().AddItem(InventoryItemFactory.CreateItem(string));
					UIManager.getDragInventory().setUses(UIManager.getDragInventory().getUses() - 1);
					if (UIManager.getDragInventory().getUses() <= 0 && UIManager.getDragInventory().getContainer() != null) {
						UIManager.getDragInventory().getContainer().Items.remove(UIManager.getDragInventory());
						UIManager.getDragInventory().getContainer().dirty = true;
						UIManager.setDragInventory((InventoryItem)null);
					}

					UIManager.setDragInventory(inventoryItem);
				}
			}

			if (this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && float1 <= 3.0F && UIManager.getDragInventory() != null) {
				int int4 = Mouse.getX();
				int3 = Mouse.getY();
				float float2 = IsoUtils.XToIso((float)(int4 - 30), (float)int3 - 356.0F - 5.0F, IsoPlayer.instance.getZ());
				float float3 = IsoUtils.YToIso((float)(int4 - 30), (float)int3 - 356.0F - 5.0F, IsoPlayer.instance.getZ());
				float2 -= (float)this.square.getX();
				float3 -= (float)this.square.getY();
				InventoryItem inventoryItem2 = null;
				if (UIManager.getDragInventory().getUses() > 1) {
					inventoryItem2 = InventoryItemFactory.CreateItem(UIManager.getDragInventory().getModule() + "." + UIManager.getDragInventory().getType());
				} else {
					inventoryItem2 = UIManager.getDragInventory();
				}

				IsoWorldInventoryObject worldInventoryObject = new IsoWorldInventoryObject(inventoryItem2, this.square, float2, float3, 0.05F);
				inventoryItem2.setWorldItem(worldInventoryObject);
				worldInventoryObject.item.setUses(1);
				this.square.getObjects().add(worldInventoryObject);
				if (UIManager.getDragInventory().getUses() > 1) {
					UIManager.getDragInventory().Use(true);
				} else {
					IsoPlayer.instance.getInventory().Remove(UIManager.getDragInventory());
					UIManager.setDragInventory((InventoryItem)null);
				}
			}
		}

		return false;
	}

	public PropertyContainer getProperties() {
		return this.sprite == null ? null : this.sprite.getProperties();
	}

	public void RemoveAttachedAnims() {
		if (this.AttachedAnimSprite != null) {
			for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
				((IsoSpriteInstance)this.AttachedAnimSprite.get(int1)).Dispose();
			}

			this.AttachedAnimSprite.clear();
			if (this.AttachedAnimSpriteActual != null) {
				this.AttachedAnimSpriteActual.clear();
			}
		}
	}

	public void RemoveAttachedAnim(int int1) {
		if (this.AttachedAnimSprite != null) {
			if (int1 >= 0 && int1 < this.AttachedAnimSprite.size()) {
				((IsoSpriteInstance)this.AttachedAnimSprite.get(int1)).Dispose();
				this.AttachedAnimSprite.remove(int1);
				if (this.AttachedAnimSpriteActual != null && int1 < this.AttachedAnimSpriteActual.size()) {
					this.AttachedAnimSpriteActual.remove(int1);
				}
			}
		}
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else {
			if (this.getType() == IsoObjectType.wall) {
				if (this.getProperties().Is(IsoFlagType.collideN) && this.getProperties().Is(IsoFlagType.collideW)) {
					return vector2.set(this.getX(), this.getY());
				}

				if (this.getProperties().Is(IsoFlagType.collideN)) {
					return vector2.set(this.getX() + 0.5F, this.getY());
				}

				if (this.getProperties().Is(IsoFlagType.collideW)) {
					return vector2.set(this.getX(), this.getY() + 0.5F);
				}
			}

			return vector2.set(this.getX() + 0.5F, this.getY() + 0.5F);
		}
	}

	public Vector2 getFacingPositionAlt(Vector2 vector2) {
		return this.getFacingPosition(vector2);
	}

	public float getRenderYOffset() {
		return this.renderYOffset;
	}

	public void setRenderYOffset(float float1) {
		this.renderYOffset = float1;
		this.sx = 0;
	}

	public boolean isTableSurface() {
		PropertyContainer propertyContainer = this.getProperties();
		return propertyContainer != null ? propertyContainer.isTable() : false;
	}

	public boolean isTableTopObject() {
		PropertyContainer propertyContainer = this.getProperties();
		return propertyContainer != null ? propertyContainer.isTableTop() : false;
	}

	public boolean getIsSurfaceNormalOffset() {
		PropertyContainer propertyContainer = this.getProperties();
		return propertyContainer != null ? propertyContainer.isSurfaceOffset() : false;
	}

	public float getSurfaceNormalOffset() {
		float float1 = 0.0F;
		PropertyContainer propertyContainer = this.getProperties();
		if (propertyContainer.isSurfaceOffset()) {
			float1 = (float)propertyContainer.getSurface();
		}

		return float1;
	}

	public float getSurfaceOffset() {
		float float1 = 0.0F;
		if (this.isTableSurface()) {
			PropertyContainer propertyContainer = this.getProperties();
			if (propertyContainer != null) {
				float1 = (float)propertyContainer.getSurface();
			}
		}

		return float1;
	}

	public boolean isStairsNorth() {
		return this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsBN;
	}

	public boolean isStairsWest() {
		return this.getType() == IsoObjectType.stairsTW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsBW;
	}

	public boolean isStairsObject() {
		return this.isStairsNorth() || this.isStairsWest();
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		this.render(float1, float2, float3, colorInfo, boolean1, false);
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2) {
		stCol.r = colorInfo.r;
		stCol.g = colorInfo.g;
		stCol.b = colorInfo.b;
		stCol.a = colorInfo.a;
		if (this.isHighlighted()) {
			stCol.r = this.getHighlightColor().r;
			stCol.g = this.getHighlightColor().g;
			stCol.b = this.getHighlightColor().b;
			stCol.a = this.getHighlightColor().a;
			if (this.isBlink()) {
				stCol.a = this.blinkAlpha;
				if (!this.blinkAlphaIncrease) {
					this.blinkAlpha -= 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
					if (this.blinkAlpha < 0.15F) {
						this.blinkAlpha = 0.15F;
						this.blinkAlphaIncrease = true;
					}
				} else {
					this.blinkAlpha += 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
					if (this.blinkAlpha > 1.0F) {
						this.blinkAlpha = 1.0F;
						this.blinkAlphaIncrease = false;
					}
				}
			} else {
				stCol.a = 1.0F;
			}
		}

		if (this.customColor != null) {
			stCol.r = this.customColor.r;
			stCol.g = this.customColor.g;
			stCol.b = this.customColor.b;
			stCol.a = this.customColor.a;
		}

		float float4;
		float float5;
		float float6;
		if (this.sprite != null && this.sprite.forceAmbient) {
			GameTime gameTime = GameTime.getInstance();
			float4 = rmod * this.tintr;
			float5 = gmod * this.tintg;
			float6 = bmod * this.tintb;
			if (!this.isHighlighted()) {
				stCol.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * float4;
				stCol.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * float5;
				stCol.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * float6;
			}
		}

		int int1 = IsoPlayer.getPlayerIndex();
		float4 = IsoCamera.frameState.CamCharacterX;
		float5 = IsoCamera.frameState.CamCharacterY;
		float6 = IsoCamera.frameState.CamCharacterZ;
		if (IsoWorld.instance.CurrentCell.IsPlayerWindowPeeking(int1)) {
			IsoPlayer player = IsoPlayer.players[int1];
			IsoDirections directions = IsoDirections.cardinalFromAngle(player.angle);
			if (directions == IsoDirections.N) {
				float5 = (float)((double)float5 - 1.0);
			} else if (directions == IsoDirections.W) {
				float4 = (float)((double)float4 - 1.0);
			}
		}

		if (this == IsoCamera.CamCharacter && !IsoPlayer.DemoMode) {
			this.targetAlpha[int1] = 1.0F;
			this.alpha[int1] = 1.0F;
		}

		lastRenderedRendered = lastRendered;
		lastRendered = this;
		float float7;
		if (this.sprite != null) {
			if (this.sprite.getProperties().Is(IsoFlagType.invisible)) {
				return;
			}

			if (!(this instanceof IsoPhysicsObject) && IsoCamera.CamCharacter != null) {
				if (!(this instanceof IsoWindow) && this.sprite.getType() != IsoObjectType.doorW && this.sprite.getType() != IsoObjectType.doorN && ((float)this.square.getX() > float4 || (float)this.square.getY() > float5) && (int)float6 <= this.square.getZ()) {
					boolean boolean3 = false;
					float7 = 0.2F;
					boolean boolean4 = (this.sprite.cutW || this.sprite.getProperties().Is(IsoFlagType.doorW)) && (float)this.square.getX() > float4;
					boolean boolean5 = (this.sprite.cutN || this.sprite.getProperties().Is(IsoFlagType.doorN)) && (float)this.square.getY() > float5;
					if (boolean4 && this.square.getProperties().Is(IsoFlagType.WallSE) && (float)this.square.getY() <= float5) {
						boolean4 = false;
					}

					if (!boolean4 && !boolean5) {
						boolean boolean6 = this.getType() == IsoObjectType.WestRoofB || this.getType() == IsoObjectType.WestRoofM || this.getType() == IsoObjectType.WestRoofT;
						boolean boolean7 = boolean6 && (int)float6 == this.square.getZ() && this.square.getBuilding() == null;
						if (boolean7 && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this.square, int1)) {
							boolean3 = true;
							float7 = 0.05F;
						}
					} else {
						boolean3 = true;
					}

					if (this.sprite.getProperties().Is(IsoFlagType.halfheight)) {
						boolean3 = false;
					}

					if (boolean3) {
						if (boolean5 && this.sprite.getProperties().Is(IsoFlagType.HoppableN)) {
							float7 = 0.25F;
						}

						if (boolean4 && this.sprite.getProperties().Is(IsoFlagType.HoppableW)) {
							float7 = 0.25F;
						}

						this.targetAlpha[int1] = float7;
						LowLightingQualityHack = true;
						if (this.rerouteMask == null && !(this instanceof IsoThumpable) && !IsoWindowFrame.isWindowFrame(this) && (UIManager.getDragInventory() == null || !UIManager.DragInventory.getType().contains("Sledgehammer") && !UIManager.DragInventory.getType().contains("Axe")) && !this.sprite.getProperties().Is(IsoFlagType.doorN) && !this.sprite.getProperties().Is(IsoFlagType.doorW)) {
							this.NoPicking = true;
						} else {
							this.NoPicking = false;
						}
					} else {
						this.NoPicking = false;
					}
				} else {
					this.NoPicking = false;
				}
			}
		}

		float float8 = 2.0F;
		float7 = 1.5F;
		if (this.square != null && this.square.room != null) {
			float8 *= 2.0F;
		}

		if (this instanceof IsoGameCharacter && !((IsoGameCharacter)this).SpottedSinceAlphaZero[int1]) {
			this.targetAlpha[int1] = 0.0F;
		}

		if (this == IsoCamera.CamCharacter) {
			this.targetAlpha[int1] = 1.0F;
		}

		if (this.bNeverDoneAlpha) {
			this.alpha[int1] = this.targetAlpha[int1];
			this.bNeverDoneAlpha = false;
		}

		float[] floatArray;
		if (this.alpha[int1] < this.targetAlpha[int1]) {
			floatArray = this.alpha;
			floatArray[int1] += alphaStep * float8;
			if (this.alpha[int1] > this.targetAlpha[int1]) {
				this.alpha[int1] = this.targetAlpha[int1];
			}
		} else if (this.alpha[int1] > this.targetAlpha[int1]) {
			floatArray = this.alpha;
			floatArray[int1] -= alphaStep / float7;
			if (this.alpha[int1] < this.targetAlpha[int1]) {
				this.alpha[int1] = this.targetAlpha[int1];
			}
		}

		if (this.alpha[int1] < 0.0F) {
			this.alpha[int1] = 0.0F;
		}

		if (this.alpha[int1] > 1.0F) {
			this.alpha[int1] = 1.0F;
		}

		if (this.sprite != null) {
			if (this.getType() == IsoObjectType.wall) {
			}

			this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink());
		}

		if (this.isHighlighted()) {
			colorInfo = stCol;
		}

		if (this.getOverlaySprite() != null) {
			ColorInfo colorInfo2 = stCol2;
			colorInfo2.r = colorInfo.r;
			colorInfo2.g = colorInfo.g;
			colorInfo2.b = colorInfo.b;
			colorInfo2.a = colorInfo.a;
			if (this.overlaySpriteColor != null) {
				colorInfo2.r *= this.overlaySpriteColor.r;
				colorInfo2.g *= this.overlaySpriteColor.g;
				colorInfo2.b *= this.overlaySpriteColor.b;
				colorInfo2.a *= this.overlaySpriteColor.a;
			}

			this.getOverlaySprite().render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colorInfo2);
		}

		if (boolean1) {
			int int2;
			int int3;
			if (this.AttachedAnimSprite != null) {
				int2 = this.AttachedAnimSprite.size();
				for (int3 = 0; int3 < int2; ++int3) {
					IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int3);
					if (!boolean2 || !spriteInstance.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
						float float9 = colorInfo.a;
						colorInfo.a = spriteInstance.alpha;
						spriteInstance.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colorInfo);
						colorInfo.a = float9;
						spriteInstance.update();
					}
				}
			}

			if (this.Children != null) {
				int2 = this.Children.size();
				for (int3 = 0; int3 < int2; ++int3) {
					IsoObject object = (IsoObject)this.Children.get(int3);
					if (object instanceof IsoMovingObject) {
						object.render(((IsoMovingObject)object).x, ((IsoMovingObject)object).y, ((IsoMovingObject)object).z, colorInfo, boolean1);
					}
				}
			}

			if (this.wallBloodSplats != null) {
				if (Core.OptionBloodDecals == 0) {
					return;
				}

				for (int2 = 0; int2 < this.wallBloodSplats.size(); ++int2) {
					((IsoWallBloodSplat)this.wallBloodSplats.get(int2)).render(float1, float2, float3, colorInfo);
				}
			}
		}
	}

	public void renderFxMask(float float1, float float2, float float3, boolean boolean1) {
		if (this.sprite != null) {
			if (this.getType() == IsoObjectType.wall) {
			}

			this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colFxMask, false);
		}

		if (this.getOverlaySprite() != null) {
			this.getOverlaySprite().render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colFxMask, false);
		}

		if (boolean1) {
			int int1;
			int int2;
			if (this.AttachedAnimSprite != null) {
				int1 = this.AttachedAnimSprite.size();
				for (int2 = 0; int2 < int1; ++int2) {
					IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
					spriteInstance.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colFxMask);
				}
			}

			if (this.Children != null) {
				int1 = this.Children.size();
				for (int2 = 0; int2 < int1; ++int2) {
					IsoObject object = (IsoObject)this.Children.get(int2);
					if (object instanceof IsoMovingObject) {
						object.render(((IsoMovingObject)object).x, ((IsoMovingObject)object).y, ((IsoMovingObject)object).z, colFxMask, boolean1);
					}
				}
			}

			if (this.wallBloodSplats != null) {
				if (Core.OptionBloodDecals == 0) {
					return;
				}

				for (int1 = 0; int1 < this.wallBloodSplats.size(); ++int1) {
					((IsoWallBloodSplat)this.wallBloodSplats.get(int1)).render(float1, float2, float3, colFxMask);
				}
			}
		}
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.sprite != null) {
			if (!this.sprite.getProperties().Is(IsoFlagType.invisible)) {
				if (UIManager.getDragInventory() == null || !"Barricade".equals(UIManager.getDragInventory().getType()) || this.sprite.Properties.Is(IsoFlagType.solidfloor)) {
					this.sprite.renderObjectPicker(this.sprite.def, this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colorInfo);
				}
			}
		}
	}

	public boolean TestPathfindCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		return false;
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		return false;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		return IsoObject.VisionResult.Unblocked;
	}

	Texture getCurrentFrameTex() {
		if (this.sprite == null) {
			return null;
		} else if (this.sprite.CurrentAnim == null) {
			return null;
		} else {
			return (float)this.sprite.CurrentAnim.Frames.size() <= this.sprite.def.Frame ? null : ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get((int)this.sprite.def.Frame)).getTexture(this.dir);
		}
	}

	public boolean isMaskClicked(int int1, int int2) {
		return this.sprite == null ? false : this.sprite.isMaskClicked(this.dir, int1, int2);
	}

	public boolean isMaskClicked(int int1, int int2, boolean boolean1) {
		if (this.sprite == null) {
			return false;
		} else {
			return this.overlaySprite != null && this.overlaySprite.isMaskClicked(this.dir, int1, int2, boolean1) ? true : this.sprite.isMaskClicked(this.dir, int1, int2, boolean1);
		}
	}

	public float getMaskClickedY(int int1, int int2, boolean boolean1) {
		return this.sprite == null ? 10000.0F : this.sprite.getMaskClickedY(this.dir, int1, int2, boolean1);
	}

	public void setCustomColor(ColorInfo colorInfo) {
		this.customColor = colorInfo;
	}

	public ColorInfo getCustomColor() {
		return this.customColor;
	}

	public void setCustomColor(float float1, float float2, float float3, float float4) {
		ColorInfo colorInfo = new ColorInfo(float1, float2, float3, float4);
		this.customColor = colorInfo;
	}

	public void loadFromRemoteBuffer(ByteBuffer byteBuffer) {
		this.loadFromRemoteBuffer(byteBuffer, true);
	}

	public void loadFromRemoteBuffer(ByteBuffer byteBuffer, boolean boolean1) {
		try {
			this.load(byteBuffer, 143);
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return;
		}

		if (this instanceof IsoWorldInventoryObject && ((IsoWorldInventoryObject)this).getItem() == null) {
			DebugLog.log("loadFromRemoteBuffer() failed due to an unknown item type");
		} else {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			boolean boolean2 = byteBuffer.get() != 0;
			boolean boolean3 = byteBuffer.get() != 0;
			IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(int1, int2, int3);
			this.square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (this.square != null) {
				if (GameServer.bServer && !(this instanceof IsoWorldInventoryObject)) {
					IsoRegion.setPreviousFlags(this.square);
				}

				if (boolean2) {
					this.square.getSpecialObjects().add(this);
				}

				if (boolean3 && this instanceof IsoWorldInventoryObject) {
					this.square.getWorldObjects().add((IsoWorldInventoryObject)this);
					this.square.chunk.recalcHashCodeObjects();
				}

				if (boolean1) {
					if (int4 != -1 && int4 >= 0 && int4 <= this.square.getObjects().size()) {
						this.square.getObjects().add(int4, this);
					} else {
						this.square.getObjects().add(this);
					}
				}

				int int5;
				for (int5 = 0; int5 < this.getContainerCount(); ++int5) {
					ItemContainer itemContainer = this.getContainerByIndex(int5);
					itemContainer.parent = this;
					itemContainer.parent.square = this.square;
					itemContainer.SourceGrid = this.square;
				}

				for (int5 = -1; int5 <= 1; ++int5) {
					for (int int6 = -1; int6 <= 1; ++int6) {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int5 + int1, int6 + int2, int3);
						if (square != null) {
							square.RecalcAllWithNeighbours(true);
						}
					}
				}
			}
		}
	}

	public void addToWorld() {
		for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
			ItemContainer itemContainer = this.getContainerByIndex(int1);
			itemContainer.addItemsToProcessItems();
		}

		if (!GameServer.bServer) {
			ItemContainer itemContainer2 = this.getContainerByEitherType("fridge", "freezer");
			if (itemContainer2 != null && itemContainer2.isPowered()) {
				this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
				this.emitter.playAmbientLoopedImpl("FridgeHum");
				if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
					IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
				}
			}

			if ((this.sprite == null || this.sprite.name == null || !this.sprite.name.startsWith("blends_natural_02")) && this.hasWater() && Rand.Next(5) == 0) {
				AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WaterDrip");
			}

			if (this instanceof IsoWindow && Rand.Next(8) == 0) {
				AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WindowWind");
			}

			if (this instanceof IsoWindow && Rand.Next(8) == 0) {
				AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WindowRattle");
			}

			if (this instanceof IsoDoor && Rand.Next(6) == 0) {
				AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WoodDoorCreaks");
			}

			if (this instanceof IsoTree && Rand.Next(80) == 0) {
				AmbientStreamManager.instance.addDaytimeAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "BirdInTree");
			}

			this.checkMoveWithWind();
		}
	}

	public void removeFromWorld() {
		IsoCell cell = this.getCell();
		cell.getProcessIsoObjects().remove(this);
		cell.getStaticUpdaterObjectList().remove(this);
		for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
			ItemContainer itemContainer = this.getContainerByIndex(int1);
			itemContainer.removeItemsFromProcessItems();
		}

		if (this.emitter != null) {
			this.emitter.stopAll();
			this.emitter = null;
		}
	}

	public void reuseGridSquare() {
	}

	public void removeFromSquare() {
		if (this.square != null) {
			this.square.getObjects().remove(this);
			this.square.getSpecialObjects().remove(this);
		}
	}

	public void transmitCustomColor() {
		if (GameClient.bClient && this.getCustomColor() != null) {
			GameClient.instance.sendCustomColor(this);
		}
	}

	public void transmitCompleteItemToClients() {
		if (GameServer.bServer) {
			if (GameServer.udpEngine == null) {
				return;
			}

			int int1;
			UdpConnection udpConnection;
			if (SystemDisabler.doWorldSyncEnable) {
				for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (udpConnection.ReleventTo((float)this.square.x, (float)this.square.y)) {
						GameServer.SyncObjectChunkHashes(this.square.chunk, udpConnection);
					}
				}

				return;
			}

			for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection != null && this.square != null && udpConnection.ReleventTo((float)this.square.x, (float)this.square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)17, byteBufferWriter);
					this.writeToRemoteBuffer(byteBufferWriter);
					udpConnection.endPacketImmediate();
				}
			}
		}
	}

	public void transmitUpdatedSpriteToClients(UdpConnection udpConnection) {
		if (GameServer.bServer) {
			this.revisionUp();
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection2 != null && this.square != null && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null) && udpConnection2.ReleventTo((float)this.square.x, (float)this.square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)76, byteBufferWriter);
					byteBufferWriter.putInt(this.getSprite().ID);
					GameWindow.WriteStringUTF(byteBufferWriter.bb, this.spriteName);
					byteBufferWriter.putInt(this.getSquare().getX());
					byteBufferWriter.putInt(this.getSquare().getY());
					byteBufferWriter.putInt(this.getSquare().getZ());
					byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
					if (this.AttachedAnimSpriteActual != null) {
						byteBufferWriter.putByte((byte)this.AttachedAnimSpriteActual.size());
						Iterator iterator = this.AttachedAnimSpriteActual.iterator();
						while (iterator.hasNext()) {
							IsoSprite sprite = (IsoSprite)iterator.next();
							byteBufferWriter.putInt(sprite.ID);
						}
					} else {
						byteBufferWriter.putByte((byte)0);
					}

					udpConnection2.endPacket();
				}
			}
		}
	}

	public void transmitUpdatedSpriteToClients() {
		this.transmitUpdatedSpriteToClients((UdpConnection)null);
	}

	public void sendObjectChange(String string) {
		if (GameServer.bServer) {
			GameServer.sendObjectChange(this, string, (KahluaTable)null);
		} else if (GameClient.bClient) {
			DebugLog.log("sendObjectChange() can only be called on the server");
		} else {
			SinglePlayerServer.sendObjectChange(this, string, (KahluaTable)null);
		}
	}

	public void sendObjectChange(String string, KahluaTable kahluaTable) {
		if (GameServer.bServer) {
			GameServer.sendObjectChange(this, string, kahluaTable);
		} else if (GameClient.bClient) {
			DebugLog.log("sendObjectChange() can only be called on the server");
		} else {
			SinglePlayerServer.sendObjectChange(this, string, kahluaTable);
		}
	}

	public void sendObjectChange(String string, Object[] objectArray) {
		if (GameServer.bServer) {
			GameServer.sendObjectChange(this, string, objectArray);
		} else if (GameClient.bClient) {
			DebugLog.log("sendObjectChange() can only be called on the server");
		} else {
			SinglePlayerServer.sendObjectChange(this, string, objectArray);
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("containers".equals(string)) {
			byteBuffer.put((byte)this.getContainerCount());
			for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
				ItemContainer itemContainer = this.getContainerByIndex(int1);
				try {
					itemContainer.save(byteBuffer, false);
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
				}
			}
		} else if ("container.customTemperature".equals(string)) {
			if (this.getContainer() != null) {
				byteBuffer.putFloat(this.getContainer().getCustomTemperature());
			} else {
				byteBuffer.putFloat(0.0F);
			}
		} else if ("name".equals(string)) {
			GameWindow.WriteStringUTF(byteBuffer, this.getName());
		} else if ("replaceWith".equals(string)) {
			if (kahluaTable != null && kahluaTable.rawget("object") instanceof IsoObject) {
				IsoObject object = (IsoObject)kahluaTable.rawget("object");
				try {
					object.save(byteBuffer);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		} else if ("usesExternalWaterSource".equals(string)) {
			boolean boolean1 = kahluaTable != null && Boolean.TRUE.equals(kahluaTable.rawget("value"));
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
		} else if ("sprite".equals(string)) {
			if (this.sprite == null) {
				byteBuffer.putInt(0);
			} else {
				byteBuffer.putInt(this.sprite.ID);
				GameWindow.WriteStringUTF(byteBuffer, this.spriteName);
			}
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		int int1;
		if ("containers".equals(string)) {
			for (int1 = 0; int1 < this.getContainerCount(); ++int1) {
				ItemContainer itemContainer = this.getContainerByIndex(int1);
				itemContainer.removeItemsFromProcessItems();
				itemContainer.removeAllItems();
			}

			this.removeAllContainers();
			byte byte1 = byteBuffer.get();
			for (int int2 = 0; int2 < byte1; ++int2) {
				ItemContainer itemContainer2 = new ItemContainer();
				itemContainer2.ID = 0;
				itemContainer2.parent = this;
				itemContainer2.SourceGrid = this.square;
				try {
					itemContainer2.load(byteBuffer, 143, false);
					if (int2 == 0) {
						if (this instanceof IsoDeadBody) {
							itemContainer2.Capacity = 8;
						}

						this.container = itemContainer2;
					} else {
						this.addSecondaryContainer(itemContainer2);
					}
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
				}
			}
		} else if ("container.customTemperature".equals(string)) {
			float float1 = byteBuffer.getFloat();
			if (this.getContainer() != null) {
				this.getContainer().setCustomTemperature(float1);
			}
		} else if ("name".equals(string)) {
			String string2 = GameWindow.ReadStringUTF(byteBuffer);
			this.setName(string2);
		} else if ("replaceWith".equals(string)) {
			try {
				int1 = this.getObjectIndex();
				if (int1 >= 0) {
					IsoObject object = factoryFromFileInput(this.getCell(), byteBuffer);
					object.load(byteBuffer, 143);
					object.setSquare(this.square);
					this.square.getObjects().set(int1, object);
					this.square.getSpecialObjects().remove(this);
					this.square.RecalcAllWithNeighbours(true);
					if (this.getContainerCount() > 0) {
						for (int int3 = 0; int3 < this.getContainerCount(); ++int3) {
							ItemContainer itemContainer3 = this.getContainerByIndex(int3);
							itemContainer3.removeItemsFromProcessItems();
						}

						LuaEventManager.triggerEvent("OnContainerUpdate");
					}
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else if ("usesExternalWaterSource".equals(string)) {
			this.usesExternalWaterSource = byteBuffer.get() == 1;
		} else if ("sprite".equals(string)) {
			int1 = byteBuffer.getInt();
			if (int1 == 0) {
				this.sprite = null;
				this.spriteName = null;
				this.tile = null;
			} else {
				this.spriteName = GameWindow.ReadString(byteBuffer);
				this.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, int1);
				if (this.sprite == null) {
					this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
					this.sprite.LoadFramesNoDirPageSimple(this.spriteName);
				}
			}
		}

		this.checkMoveWithWind();
	}

	public void transmitUpdatedSpriteToServer() {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)76, byteBufferWriter);
			byteBufferWriter.putInt(this.getSprite().ID);
			GameWindow.WriteStringUTF(byteBufferWriter.bb, this.spriteName);
			byteBufferWriter.putInt(this.getSquare().getX());
			byteBufferWriter.putInt(this.getSquare().getY());
			byteBufferWriter.putInt(this.getSquare().getZ());
			byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
			if (this.AttachedAnimSpriteActual != null) {
				byteBufferWriter.putByte((byte)this.AttachedAnimSpriteActual.size());
				Iterator iterator = this.AttachedAnimSpriteActual.iterator();
				while (iterator.hasNext()) {
					IsoSprite sprite = (IsoSprite)iterator.next();
					byteBufferWriter.putInt(sprite.ID);
				}
			} else {
				byteBufferWriter.putByte((byte)0);
			}

			GameClient.connection.endPacket();
		}
	}

	public void transmitCompleteItemToServer() {
		if (GameClient.bClient) {
			this.square.clientModify();
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)17, byteBufferWriter);
			this.writeToRemoteBuffer(byteBufferWriter);
			GameClient.connection.endPacketImmediate();
		}
	}

	public void transmitModData() {
		if (this.square != null) {
			if (GameClient.bClient) {
				this.square.clientModify();
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)58, byteBufferWriter);
				byteBufferWriter.putInt(this.getSquare().getX());
				byteBufferWriter.putInt(this.getSquare().getY());
				byteBufferWriter.putInt(this.getSquare().getZ());
				byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
				if (this.getModData().isEmpty()) {
					byteBufferWriter.putByte((byte)0);
				} else {
					byteBufferWriter.putByte((byte)1);
					try {
						this.getModData().save(byteBufferWriter.bb);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}

				GameClient.connection.endPacketImmediate();
			} else if (GameServer.bServer) {
				GameServer.sendObjectModData(this);
				this.square.revisionUp();
			}
		}
	}

	public void writeToRemoteBuffer(ByteBufferWriter byteBufferWriter) {
		try {
			this.save(byteBufferWriter.bb);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byteBufferWriter.putInt(this.getObjectIndex());
		byteBufferWriter.putBoolean(this.square.getSpecialObjects().contains(this));
		byteBufferWriter.putBoolean(this.square.getWorldObjects().contains(this));
	}

	public int getObjectIndex() {
		return this.square == null ? -1 : this.square.getObjects().indexOf(this);
	}

	public int getMovingObjectIndex() {
		return this.square == null ? -1 : this.square.getMovingObjects().indexOf(this);
	}

	public int getSpecialObjectIndex() {
		return this.square == null ? -1 : this.square.getSpecialObjects().indexOf(this);
	}

	public int getStaticMovingObjectIndex() {
		return this.square == null ? -1 : this.square.getStaticMovingObjects().indexOf(this);
	}

	public int getWorldObjectIndex() {
		return this.square == null ? -1 : this.square.getWorldObjects().indexOf(this);
	}

	public IsoSprite getOverlaySprite() {
		return this.overlaySprite;
	}

	public void setOverlaySpriteColor(float float1, float float2, float float3, float float4) {
		this.overlaySpriteColor = new ColorInfo(float1, float2, float3, float4);
	}

	public ColorInfo getOverlaySpriteColor() {
		return this.overlaySpriteColor;
	}

	public void setOverlaySprite(String string) {
		this.setOverlaySprite(string, -1.0F, -1.0F, -1.0F, -1.0F, true);
	}

	public void setOverlaySprite(String string, float float1, float float2, float float3, float float4) {
		this.setOverlaySprite(string, float1, float2, float3, float4, true);
	}

	public boolean setOverlaySprite(String string, float float1, float float2, float float3, float float4, boolean boolean1) {
		if (string != null && !string.isEmpty()) {
			boolean boolean2;
			if (!(float1 > -1.0F)) {
				boolean2 = this.overlaySpriteColor == null;
			} else {
				boolean2 = this.overlaySpriteColor != null && this.overlaySpriteColor.r == float1 && this.overlaySpriteColor.g == float2 && this.overlaySpriteColor.b == float3 && this.overlaySpriteColor.a == float4;
			}

			if (this.overlaySprite != null && string.equals(this.overlaySprite.name) && boolean2) {
				return false;
			}

			this.overlaySprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(string);
			this.overlaySprite.name = string;
		} else {
			if (this.overlaySprite == null) {
				return false;
			}

			this.overlaySprite = null;
			string = "";
		}

		if (float1 > -1.0F) {
			this.overlaySpriteColor = new ColorInfo(float1, float2, float3, float4);
		} else {
			this.overlaySpriteColor = null;
		}

		if (!boolean1) {
			return true;
		} else {
			if (GameServer.bServer) {
				GameServer.updateOverlayForClients(this, string, float1, float2, float3, float4, (UdpConnection)null);
			} else if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)90, byteBufferWriter);
				GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
				byteBufferWriter.putInt(this.getSquare().getX());
				byteBufferWriter.putInt(this.getSquare().getY());
				byteBufferWriter.putInt(this.getSquare().getZ());
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				byteBufferWriter.putFloat(float4);
				byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
				GameClient.connection.endPacketImmediate();
			}

			return true;
		}
	}

	public boolean haveSpecialTooltip() {
		return this.specialTooltip;
	}

	public void setSpecialTooltip(boolean boolean1) {
		this.specialTooltip = boolean1;
	}

	public int getKeyId() {
		return this.keyId;
	}

	public void setKeyId(int int1) {
		this.keyId = int1;
	}

	public boolean isHighlighted() {
		return (this.highlightFlags & 1) != 0;
	}

	public void setHighlighted(boolean boolean1) {
		this.setHighlighted(boolean1, true);
	}

	public void setHighlighted(boolean boolean1, boolean boolean2) {
		if (boolean1) {
			this.highlightFlags = (byte)(this.highlightFlags | 1);
		} else {
			this.highlightFlags &= -2;
		}

		if (boolean2) {
			this.highlightFlags = (byte)(this.highlightFlags | 2);
		} else {
			this.highlightFlags &= -3;
		}
	}

	public ColorInfo getHighlightColor() {
		return this.highlightColor;
	}

	public void setHighlightColor(ColorInfo colorInfo) {
		this.highlightColor = colorInfo;
	}

	public void setHighlightColor(float float1, float float2, float float3, float float4) {
		if (this.highlightColor == null) {
			this.highlightColor = new ColorInfo(float1, float2, float3, float4);
		} else {
			this.highlightColor.set(float1, float2, float3, float4);
		}
	}

	public boolean isBlink() {
		return (this.highlightFlags & 4) != 0;
	}

	public void setBlink(boolean boolean1) {
		if (boolean1) {
			this.highlightFlags = (byte)(this.highlightFlags | 4);
		} else {
			this.highlightFlags &= -5;
		}
	}

	public void checkHaveElectricity() {
		if (!GameServer.bServer) {
			ItemContainer itemContainer = this.getContainerByEitherType("fridge", "freezer");
			if (itemContainer != null) {
				if (itemContainer.isPowered()) {
					if (this.emitter == null) {
						this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
						this.emitter.playAmbientLoopedImpl("FridgeHum");
						if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
							IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
						}
					}
				} else if (this.emitter != null) {
					this.emitter.stopAll();
					this.emitter = null;
				}
			}
		}
	}

	public int getContainerCount() {
		int int1 = this.container == null ? 0 : 1;
		int int2 = this.secondaryContainers == null ? 0 : this.secondaryContainers.size();
		return int1 + int2;
	}

	public ItemContainer getContainerByIndex(int int1) {
		if (this.container != null) {
			if (int1 == 0) {
				return this.container;
			} else if (this.secondaryContainers == null) {
				return null;
			} else {
				return int1 >= 1 && int1 <= this.secondaryContainers.size() ? (ItemContainer)this.secondaryContainers.get(int1 - 1) : null;
			}
		} else if (this.secondaryContainers == null) {
			return null;
		} else {
			return int1 >= 0 && int1 < this.secondaryContainers.size() ? (ItemContainer)this.secondaryContainers.get(int1) : null;
		}
	}

	public ItemContainer getContainerByType(String string) {
		for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
			ItemContainer itemContainer = this.getContainerByIndex(int1);
			if (itemContainer.getType().equals(string)) {
				return itemContainer;
			}
		}

		return null;
	}

	public ItemContainer getContainerByEitherType(String string, String string2) {
		for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
			ItemContainer itemContainer = this.getContainerByIndex(int1);
			if (itemContainer.getType().equals(string) || itemContainer.getType().equals(string2)) {
				return itemContainer;
			}
		}

		return null;
	}

	public void addSecondaryContainer(ItemContainer itemContainer) {
		if (this.secondaryContainers == null) {
			this.secondaryContainers = new ArrayList();
		}

		this.secondaryContainers.add(itemContainer);
		itemContainer.parent = this;
	}

	public int getContainerIndex(ItemContainer itemContainer) {
		if (itemContainer == this.container) {
			return 0;
		} else if (this.secondaryContainers == null) {
			return -1;
		} else {
			for (int int1 = 0; int1 < this.secondaryContainers.size(); ++int1) {
				if (this.secondaryContainers.get(int1) == itemContainer) {
					return (this.container == null ? 0 : 1) + int1;
				}
			}

			return -1;
		}
	}

	public void removeAllContainers() {
		this.container = null;
		if (this.secondaryContainers != null) {
			this.secondaryContainers.clear();
		}
	}

	public void createContainersFromSpriteProperties() {
		if (this.sprite != null) {
			if (this.container == null) {
				if (this.sprite.getProperties().Is("container") && this.container == null) {
					this.container = new ItemContainer(this.sprite.getProperties().Val("container"), this.square, this, 1, 1);
					this.container.parent = this;
					this.OutlineOnMouseover = true;
					if (this.sprite.getProperties().Is("ContainerCapacity")) {
						this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
					}
				}

				if (this.getSprite().getProperties().Is("Freezer")) {
					ItemContainer itemContainer = new ItemContainer("freezer", this.square, this, 1, 1);
					if (this.getSprite().getProperties().Is("FreezerCapacity")) {
						itemContainer.Capacity = Integer.parseInt(this.sprite.getProperties().Val("FreezerCapacity"));
					} else {
						itemContainer.Capacity = 15;
					}

					if (this.container == null) {
						this.container = itemContainer;
						this.container.parent = this;
					} else {
						this.addSecondaryContainer(itemContainer);
					}
				}
			}
		}
	}

	public void revisionUp() {
		if (this.square != null) {
			this.square.revisionUp();
		}
	}

	public void cleanWallBlood() {
		this.square.removeBlood(false, true);
	}

	public ObjectRenderEffects getWindRenderEffects() {
		return this.windRenderEffects;
	}

	public ObjectRenderEffects getObjectRenderEffects() {
		return this.objectRenderEffects;
	}

	public void setRenderEffect(RenderEffectType renderEffectType) {
		this.setRenderEffect(renderEffectType, false);
	}

	public void setRenderEffect(RenderEffectType renderEffectType, boolean boolean1) {
		if (!GameServer.bServer) {
			if (this.objectRenderEffects == null || boolean1) {
				this.objectRenderEffects = ObjectRenderEffects.getNew(this, renderEffectType, boolean1);
			}
		}
	}

	public void removeRenderEffect(ObjectRenderEffects objectRenderEffects) {
		if (this.objectRenderEffects != null && this.objectRenderEffects == objectRenderEffects) {
			this.objectRenderEffects = null;
		}
	}

	public ObjectRenderEffects getObjectRenderEffectsToApply() {
		if (this.objectRenderEffects != null) {
			return this.objectRenderEffects;
		} else {
			return Core.getInstance().getOptionDoWindSpriteEffects() && this.windRenderEffects != null ? this.windRenderEffects : null;
		}
	}
	public static enum VisionResult {

		NoEffect,
		Blocked,
		Unblocked;
	}
}
