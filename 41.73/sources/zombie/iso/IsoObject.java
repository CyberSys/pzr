package zombie.iso;

import fmod.fmod.FMODManager;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.lwjgl.opengl.ARBShaderObjects;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ThumpState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.ObjectAmbientEmitters;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoCombinationWasherDryer;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStackedWasherDryer;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.sprite.shapers.WallShaper;
import zombie.iso.sprite.shapers.WallShaperN;
import zombie.iso.sprite.shapers.WallShaperW;
import zombie.iso.sprite.shapers.WallShaperWhole;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.util.list.PZArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.world.WorldDictionary;


public class IsoObject implements Serializable,Thumpable {
	public static final byte OBF_Highlighted = 1;
	public static final byte OBF_HighlightRenderOnce = 2;
	public static final byte OBF_Blink = 4;
	public static final int MAX_WALL_SPLATS = 32;
	private static final String PropMoveWithWind = "MoveWithWind";
	public static IsoObject lastRendered = null;
	public static IsoObject lastRenderedRendered = null;
	private static final ColorInfo stCol = new ColorInfo();
	public static float rmod;
	public static float gmod;
	public static float bmod;
	public static boolean LowLightingQualityHack = false;
	private static int DefaultCondition = 0;
	private static final ColorInfo stCol2 = new ColorInfo();
	private static final ColorInfo colFxMask = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
	public byte highlightFlags;
	public int keyId;
	public BaseSoundEmitter emitter;
	public float sheetRopeHealth;
	public boolean sheetRope;
	public boolean bNeverDoneAlpha;
	public boolean bAlphaForced;
	public ArrayList AttachedAnimSprite;
	public ArrayList wallBloodSplats;
	public ItemContainer container;
	public IsoDirections dir;
	public short Damage;
	public float partialThumpDmg;
	public boolean NoPicking;
	public float offsetX;
	public float offsetY;
	public boolean OutlineOnMouseover;
	public IsoObject rerouteMask;
	public IsoSprite sprite;
	public IsoSprite overlaySprite;
	public ColorInfo overlaySpriteColor;
	public IsoGridSquare square;
	private final float[] alpha;
	private final float[] targetAlpha;
	public IsoObject rerouteCollide;
	public KahluaTable table;
	public String name;
	public float tintr;
	public float tintg;
	public float tintb;
	public String spriteName;
	public float sx;
	public float sy;
	public boolean doNotSync;
	protected ObjectRenderEffects windRenderEffects;
	protected ObjectRenderEffects objectRenderEffects;
	protected IsoObject externalWaterSource;
	protected boolean usesExternalWaterSource;
	ArrayList Children;
	String tile;
	private boolean specialTooltip;
	private ColorInfo highlightColor;
	private ArrayList secondaryContainers;
	private ColorInfo customColor;
	private float renderYOffset;
	protected byte isOutlineHighlight;
	protected byte isOutlineHlAttached;
	protected byte isOutlineHlBlink;
	protected final int[] outlineHighlightCol;
	private float outlineThickness;
	protected boolean bMovedThumpable;
	private static final Map byteToObjectMap = new HashMap();
	private static final Map hashCodeToObjectMap = new HashMap();
	private static final Map nameToObjectMap = new HashMap();
	private static IsoObject.IsoObjectFactory factoryIsoObject;
	private static IsoObject.IsoObjectFactory factoryVehicle;

	public IsoObject(IsoCell cell) {
		this();
	}

	public IsoObject() {
		this.keyId = -1;
		this.sheetRopeHealth = 100.0F;
		this.sheetRope = false;
		this.bNeverDoneAlpha = true;
		this.bAlphaForced = false;
		this.container = null;
		this.dir = IsoDirections.N;
		this.Damage = 100;
		this.partialThumpDmg = 0.0F;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.alpha = new float[4];
		this.targetAlpha = new float[4];
		this.rerouteCollide = null;
		this.table = null;
		this.name = null;
		this.tintr = 1.0F;
		this.tintg = 1.0F;
		this.tintb = 1.0F;
		this.spriteName = null;
		this.doNotSync = false;
		this.externalWaterSource = null;
		this.usesExternalWaterSource = false;
		this.specialTooltip = false;
		this.highlightColor = new ColorInfo(0.9F, 1.0F, 0.0F, 1.0F);
		this.customColor = null;
		this.renderYOffset = 0.0F;
		this.isOutlineHighlight = 0;
		this.isOutlineHlAttached = 0;
		this.isOutlineHlBlink = 0;
		this.outlineHighlightCol = new int[4];
		this.outlineThickness = 0.15F;
		this.bMovedThumpable = false;
		for (int int1 = 0; int1 < 4; ++int1) {
			this.setAlphaAndTarget(int1, 1.0F);
			this.outlineHighlightCol[int1] = -1;
		}
	}

	public IsoObject(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		this();
		this.sprite = sprite;
		this.square = square;
	}

	public IsoObject(IsoCell cell, IsoGridSquare square, String string) {
		this();
		this.sprite = IsoSpriteManager.instance.getSprite(string);
		this.square = square;
		this.tile = string;
	}

	public IsoObject(IsoGridSquare square, String string, String string2) {
		this();
		this.sprite = IsoSpriteManager.instance.getSprite(string);
		this.square = square;
		this.tile = string;
		this.spriteName = string;
		this.name = string2;
	}

	public IsoObject(IsoGridSquare square, String string, String string2, boolean boolean1) {
		this();
		if (boolean1) {
			this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.sprite.LoadFramesNoDirPageSimple(string);
		} else {
			this.sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string);
		}

		this.tile = string;
		this.square = square;
		this.name = string2;
	}

	public boolean isFloor() {
		return this.getProperties() != null ? this.getProperties().Is(IsoFlagType.solidfloor) : false;
	}

	public IsoObject(IsoGridSquare square, String string, boolean boolean1) {
		this();
		if (boolean1) {
			this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.sprite.LoadFramesNoDirPageSimple(string);
		} else {
			this.sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string);
		}

		this.tile = string;
		this.square = square;
	}

	public IsoObject(IsoGridSquare square, String string) {
		this();
		this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
		this.sprite.LoadFramesNoDirPageSimple(string);
		this.square = square;
	}

	public static IsoObject getNew(IsoGridSquare square, String string, String string2, boolean boolean1) {
		IsoObject object = null;
		synchronized (CellLoader.isoObjectCache) {
			if (CellLoader.isoObjectCache.isEmpty()) {
				object = new IsoObject(square, string, string2, boolean1);
			} else {
				object = (IsoObject)CellLoader.isoObjectCache.pop();
				object.reset();
				object.tile = string;
			}
		}
		if (boolean1) {
			object.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			object.sprite.LoadFramesNoDirPageSimple(object.tile);
		} else {
			object.sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(object.tile);
		}

		object.square = square;
		object.name = string2;
		return object;
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

	public static IsoObject getNew() {
		synchronized (CellLoader.isoObjectCache) {
			return CellLoader.isoObjectCache.isEmpty() ? new IsoObject() : (IsoObject)CellLoader.isoObjectCache.pop();
		}
	}

	private static IsoObject.IsoObjectFactory addIsoObjectFactory(IsoObject.IsoObjectFactory objectFactory) {
		if (byteToObjectMap.containsKey(objectFactory.classID)) {
			throw new RuntimeException("Class id already exists, " + objectFactory.objectName);
		} else {
			byteToObjectMap.put(objectFactory.classID, objectFactory);
			if (hashCodeToObjectMap.containsKey(objectFactory.hashCode)) {
				throw new RuntimeException("Hashcode already exists, " + objectFactory.objectName);
			} else {
				hashCodeToObjectMap.put(objectFactory.hashCode, objectFactory);
				if (nameToObjectMap.containsKey(objectFactory.objectName)) {
					throw new RuntimeException("ObjectName already exists, " + objectFactory.objectName);
				} else {
					nameToObjectMap.put(objectFactory.objectName, objectFactory);
					return objectFactory;
				}
			}
		}
	}

	public static IsoObject.IsoObjectFactory getFactoryVehicle() {
		return factoryVehicle;
	}

	private static void initFactory() {
		factoryIsoObject = addIsoObjectFactory(new IsoObject.IsoObjectFactory(0, "IsoObject"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				IsoObject var2 = IsoObject.getNew();
				var2.sx = 0.0F;
				return var2;
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(1, "Player"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoPlayer(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(2, "Survivor"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoSurvivor(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(3, "Zombie"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoZombie(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(4, "Pushable"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoPushableObject(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(5, "WheelieBin"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoWheelieBin(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(6, "WorldInventoryItem"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoWorldInventoryObject(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(7, "Jukebox"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoJukebox(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(8, "Curtain"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoCurtain(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(9, "Radio"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoRadio(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(10, "Television"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoTelevision(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(11, "DeadBody"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoDeadBody(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(12, "Barbecue"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoBarbecue(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(13, "ClothingDryer"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoClothingDryer(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(14, "ClothingWasher"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoClothingWasher(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(15, "Fireplace"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoFireplace(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(16, "Stove"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoStove(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(17, "Door"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoDoor(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(18, "Thumpable"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoThumpable(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(19, "IsoTrap"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoTrap(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(20, "IsoBrokenGlass"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoBrokenGlass(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(21, "IsoCarBatteryCharger"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoCarBatteryCharger(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(22, "IsoGenerator"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoGenerator(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(23, "IsoCompost"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoCompost(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(24, "Mannequin"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoMannequin(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(25, "StoneFurnace"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new BSFurnace(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(26, "Window"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoWindow(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(27, "Barricade"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoBarricade(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(28, "Tree"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return IsoTree.getNew();
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(29, "LightSwitch"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoLightSwitch(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(30, "ZombieGiblets"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoZombieGiblets(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(31, "MolotovCocktail"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoMolotovCocktail(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(32, "Fire"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoFire(var1);
			}
		});
		factoryVehicle = addIsoObjectFactory(new IsoObject.IsoObjectFactory(33, "Vehicle"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new BaseVehicle(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(34, "CombinationWasherDryer"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoCombinationWasherDryer(var1);
			}
		});
		addIsoObjectFactory(new IsoObject.IsoObjectFactory(35, "StackedWasherDryer"){
			
			protected IsoObject InstantiateObject(IsoCell var1) {
				return new IsoStackedWasherDryer(var1);
			}
		});
	}

	public static byte factoryGetClassID(String string) {
		IsoObject.IsoObjectFactory objectFactory = (IsoObject.IsoObjectFactory)hashCodeToObjectMap.get(string.hashCode());
		return objectFactory != null ? objectFactory.classID : factoryIsoObject.classID;
	}

	public static IsoObject factoryFromFileInput(IsoCell cell, byte byte1) {
		IsoObject.IsoObjectFactory objectFactory = (IsoObject.IsoObjectFactory)byteToObjectMap.get(byte1);
		if (objectFactory == null || objectFactory.objectName.equals("Vehicle") && GameClient.bClient) {
			if (objectFactory == null && Core.bDebug) {
				throw new RuntimeException("Cannot get IsoObject from classID: " + byte1);
			} else {
				IsoObject object = new IsoObject(cell);
				return object;
			}
		} else {
			return objectFactory.InstantiateObject(cell);
		}
	}

	@Deprecated
	public static IsoObject factoryFromFileInput_OLD(IsoCell cell, int int1) {
		IsoObject object;
		if (int1 == "IsoObject".hashCode()) {
			object = getNew();
			object.sx = 0.0F;
			return object;
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
		} else if (int1 == "ClothingDryer".hashCode()) {
			return new IsoClothingDryer(cell);
		} else if (int1 == "ClothingWasher".hashCode()) {
			return new IsoClothingWasher(cell);
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
		} else if (int1 == "IsoBrokenGlass".hashCode()) {
			return new IsoBrokenGlass(cell);
		} else if (int1 == "IsoCarBatteryCharger".hashCode()) {
			return new IsoCarBatteryCharger(cell);
		} else if (int1 == "IsoGenerator".hashCode()) {
			return new IsoGenerator(cell);
		} else if (int1 == "IsoCompost".hashCode()) {
			return new IsoCompost(cell);
		} else if (int1 == "Mannequin".hashCode()) {
			return new IsoMannequin(cell);
		} else if (int1 == "StoneFurnace".hashCode()) {
			return new BSFurnace(cell);
		} else if (int1 == "Window".hashCode()) {
			return new IsoWindow(cell);
		} else if (int1 == "Barricade".hashCode()) {
			return new IsoBarricade(cell);
		} else if (int1 == "Tree".hashCode()) {
			return IsoTree.getNew();
		} else if (int1 == "LightSwitch".hashCode()) {
			return new IsoLightSwitch(cell);
		} else if (int1 == "ZombieGiblets".hashCode()) {
			return new IsoZombieGiblets(cell);
		} else if (int1 == "MolotovCocktail".hashCode()) {
			return new IsoMolotovCocktail(cell);
		} else if (int1 == "Fire".hashCode()) {
			return new IsoFire(cell);
		} else if (int1 == "Vehicle".hashCode() && !GameClient.bClient) {
			return new BaseVehicle(cell);
		} else {
			object = new IsoObject(cell);
			return object;
		}
	}

	@Deprecated
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
		} else if (int1 == "ClothingDryer".hashCode()) {
			return IsoClothingDryer.class;
		} else if (int1 == "ClothingWasher".hashCode()) {
			return IsoClothingWasher.class;
		} else if (int1 == "Fireplace".hashCode()) {
			return IsoFireplace.class;
		} else if (int1 == "Stove".hashCode()) {
			return IsoStove.class;
		} else if (int1 == "Mannequin".hashCode()) {
			return IsoMannequin.class;
		} else if (int1 == "Door".hashCode()) {
			return IsoDoor.class;
		} else if (int1 == "Thumpable".hashCode()) {
			return IsoThumpable.class;
		} else if (int1 == "Window".hashCode()) {
			return IsoWindow.class;
		} else if (int1 == "Barricade".hashCode()) {
			return IsoBarricade.class;
		} else if (int1 == "Tree".hashCode()) {
			return IsoTree.class;
		} else if (int1 == "LightSwitch".hashCode()) {
			return IsoLightSwitch.class;
		} else if (int1 == "ZombieGiblets".hashCode()) {
			return IsoZombieGiblets.class;
		} else if (int1 == "MolotovCocktail".hashCode()) {
			return IsoMolotovCocktail.class;
		} else {
			return int1 == "Vehicle".hashCode() ? BaseVehicle.class : IsoObject.class;
		}
	}

	@Deprecated
	static IsoObject factoryFromFileInput(IsoCell cell, DataInputStream dataInputStream) throws IOException {
		boolean boolean1 = dataInputStream.readBoolean();
		if (!boolean1) {
			return null;
		} else {
			byte byte1 = dataInputStream.readByte();
			IsoObject object = factoryFromFileInput(cell, byte1);
			return object;
		}
	}

	public static IsoObject factoryFromFileInput(IsoCell cell, ByteBuffer byteBuffer) {
		boolean boolean1 = byteBuffer.get() != 0;
		if (!boolean1) {
			return null;
		} else {
			byte byte1 = byteBuffer.get();
			IsoObject object = factoryFromFileInput(cell, byte1);
			return object;
		}
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

	public boolean Serialize() {
		return true;
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

	public IsoGridSquare getSquare() {
		return this.square;
	}

	public void setSquare(IsoGridSquare square) {
		this.square = square;
	}

	public IsoChunk getChunk() {
		IsoGridSquare square = this.getSquare();
		return square == null ? null : square.getChunk();
	}

	public void update() {
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

	public final void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.load(byteBuffer, int1, false);
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		int int2 = byteBuffer.getInt();
		int2 = IsoChunk.Fix2x(this.square, int2);
		this.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, int2);
		if (int2 == -1) {
			this.sprite = IsoSpriteManager.instance.getSprite("");
			assert this.sprite != null;
			assert this.sprite.ID == -1;
		}

		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
		if (!bitHeaderRead.equals(0)) {
			String string;
			int int3;
			if (bitHeaderRead.hasFlags(1)) {
				int int4;
				if (bitHeaderRead.hasFlags(2)) {
					int4 = 1;
				} else {
					int4 = byteBuffer.get() & 255;
				}

				if (boolean1) {
					string = GameWindow.ReadStringUTF(byteBuffer);
					DebugLog.log(string + ", read = " + int4);
				}

				for (int int5 = 0; int5 < int4; ++int5) {
					if (this.AttachedAnimSprite == null) {
						this.AttachedAnimSprite = new ArrayList();
					}

					int3 = byteBuffer.getInt();
					IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, int3);
					IsoSpriteInstance spriteInstance = null;
					if (sprite != null) {
						spriteInstance = sprite.newInstance();
					} else if (Core.bDebug) {
						DebugLog.General.warn("discarding attached sprite because it has no tile properties");
					}

					byte byte1 = byteBuffer.get();
					boolean boolean2 = false;
					boolean boolean3 = false;
					if ((byte1 & 2) != 0) {
						boolean2 = true;
					}

					if ((byte1 & 4) != 0 && spriteInstance != null) {
						spriteInstance.Flip = true;
					}

					if ((byte1 & 8) != 0 && spriteInstance != null) {
						spriteInstance.bCopyTargetAlpha = true;
					}

					if ((byte1 & 16) != 0) {
						boolean3 = true;
						if (spriteInstance != null) {
							spriteInstance.bMultiplyObjectAlpha = true;
						}
					}

					float float1;
					if (boolean2) {
						float1 = byteBuffer.getFloat();
						float float2 = byteBuffer.getFloat();
						float float3 = byteBuffer.getFloat();
						float float4 = Bits.unpackByteToFloatUnit(byteBuffer.get());
						float float5 = Bits.unpackByteToFloatUnit(byteBuffer.get());
						float float6 = Bits.unpackByteToFloatUnit(byteBuffer.get());
						if (spriteInstance != null) {
							spriteInstance.offX = float1;
							spriteInstance.offY = float2;
							spriteInstance.offZ = float3;
							spriteInstance.tintr = float4;
							spriteInstance.tintg = float5;
							spriteInstance.tintb = float6;
						}
					} else if (spriteInstance != null) {
						spriteInstance.offX = 0.0F;
						spriteInstance.offY = 0.0F;
						spriteInstance.offZ = 0.0F;
						spriteInstance.tintr = 1.0F;
						spriteInstance.tintg = 1.0F;
						spriteInstance.tintb = 1.0F;
						spriteInstance.alpha = 1.0F;
						spriteInstance.targetAlpha = 1.0F;
					}

					if (boolean3) {
						float1 = byteBuffer.getFloat();
						if (spriteInstance != null) {
							spriteInstance.alpha = float1;
						}
					}

					if (sprite != null) {
						if (sprite.name != null && sprite.name.startsWith("overlay_blood_")) {
							float1 = (float)GameTime.getInstance().getWorldAgeHours();
							IsoWallBloodSplat wallBloodSplat = new IsoWallBloodSplat(float1, sprite);
							if (this.wallBloodSplats == null) {
								this.wallBloodSplats = new ArrayList();
							}

							this.wallBloodSplats.add(wallBloodSplat);
						} else {
							this.AttachedAnimSprite.add(spriteInstance);
						}
					}
				}
			}

			if (bitHeaderRead.hasFlags(4)) {
				if (boolean1) {
					String string2 = GameWindow.ReadStringUTF(byteBuffer);
					DebugLog.log(string2);
				}

				byte byte2 = byteBuffer.get();
				if ((byte2 & 2) != 0) {
					this.name = "Grass";
				} else if ((byte2 & 4) != 0) {
					this.name = WorldDictionary.getObjectNameFromID(byteBuffer.get());
				} else if ((byte2 & 8) != 0) {
					this.name = GameWindow.ReadString(byteBuffer);
				}

				if ((byte2 & 16) != 0) {
					this.spriteName = WorldDictionary.getSpriteNameFromID(byteBuffer.getInt());
				} else if ((byte2 & 32) != 0) {
					this.spriteName = GameWindow.ReadString(byteBuffer);
				}
			}

			float float7;
			float float8;
			if (bitHeaderRead.hasFlags(8)) {
				float float9 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				float7 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				float8 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				this.customColor = new ColorInfo(float9, float7, float8, 1.0F);
			}

			this.doNotSync = bitHeaderRead.hasFlags(16);
			this.setOutlineOnMouseover(bitHeaderRead.hasFlags(32));
			if (bitHeaderRead.hasFlags(64)) {
				BitHeaderRead bitHeaderRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Short, byteBuffer);
				byte byte3;
				float float10;
				if (bitHeaderRead2.hasFlags(1)) {
					byte3 = byteBuffer.get();
					if (byte3 > 0) {
						if (this.wallBloodSplats == null) {
							this.wallBloodSplats = new ArrayList();
						}

						int3 = 0;
						if (GameClient.bClient || GameServer.bServer) {
							int3 = ServerOptions.getInstance().BloodSplatLifespanDays.getValue();
						}

						float10 = (float)GameTime.getInstance().getWorldAgeHours();
						for (int int6 = 0; int6 < byte3; ++int6) {
							IsoWallBloodSplat wallBloodSplat2 = new IsoWallBloodSplat();
							wallBloodSplat2.load(byteBuffer, int1);
							if (wallBloodSplat2.worldAge > float10) {
								wallBloodSplat2.worldAge = float10;
							}

							if (int3 <= 0 || !(float10 - wallBloodSplat2.worldAge >= (float)(int3 * 24))) {
								this.wallBloodSplats.add(wallBloodSplat2);
							}
						}
					}
				}

				if (bitHeaderRead2.hasFlags(2)) {
					if (boolean1) {
						string = GameWindow.ReadStringUTF(byteBuffer);
						DebugLog.log(string);
					}

					byte3 = byteBuffer.get();
					for (int3 = 0; int3 < byte3; ++int3) {
						try {
							ItemContainer itemContainer = new ItemContainer();
							itemContainer.ID = 0;
							itemContainer.parent = this;
							itemContainer.parent.square = this.square;
							itemContainer.SourceGrid = this.square;
							itemContainer.load(byteBuffer, int1);
							if (int3 == 0) {
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
				}

				if (bitHeaderRead2.hasFlags(4)) {
					if (this.table == null) {
						this.table = LuaManager.platform.newTable();
					}

					this.table.load(byteBuffer, int1);
				}

				this.setSpecialTooltip(bitHeaderRead2.hasFlags(8));
				if (bitHeaderRead2.hasFlags(16)) {
					this.keyId = byteBuffer.getInt();
				}

				this.usesExternalWaterSource = bitHeaderRead2.hasFlags(32);
				if (bitHeaderRead2.hasFlags(64)) {
					this.sheetRope = true;
					this.sheetRopeHealth = byteBuffer.getFloat();
				} else {
					this.sheetRope = false;
				}

				if (bitHeaderRead2.hasFlags(128)) {
					this.renderYOffset = byteBuffer.getFloat();
				}

				if (bitHeaderRead2.hasFlags(256)) {
					string = null;
					if (bitHeaderRead2.hasFlags(512)) {
						string = GameWindow.ReadString(byteBuffer);
					} else {
						string = WorldDictionary.getSpriteNameFromID(byteBuffer.getInt());
					}

					if (string != null && !string.isEmpty()) {
						this.overlaySprite = IsoSpriteManager.instance.getSprite(string);
						this.overlaySprite.name = string;
					}
				}

				if (bitHeaderRead2.hasFlags(1024)) {
					float7 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					float8 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					float10 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					float float11 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					if (this.overlaySprite != null) {
						this.setOverlaySpriteColor(float7, float8, float10, float11);
					}
				}

				this.setMovedThumpable(bitHeaderRead2.hasFlags(2048));
				bitHeaderRead2.release();
			}
		}

		bitHeaderRead.release();
		if (this.sprite == null) {
			this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			this.sprite.LoadFramesNoDirPageSimple(this.spriteName);
		}
	}

	public final void save(ByteBuffer byteBuffer) throws IOException {
		this.save(byteBuffer, false);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
		if (this.Serialize()) {
			byteBuffer.put(factoryGetClassID(this.getObjectName()));
			byteBuffer.putInt(this.sprite == null ? -1 : this.sprite.ID);
			BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
			int int1;
			if (this.AttachedAnimSprite != null) {
				bitHeaderWrite.addFlags(1);
				if (this.AttachedAnimSprite.size() == 1) {
					bitHeaderWrite.addFlags(2);
				}

				int int2 = this.AttachedAnimSprite.size() > 255 ? 255 : this.AttachedAnimSprite.size();
				if (int2 != 1) {
					byteBuffer.put((byte)int2);
				}

				if (boolean1) {
					GameWindow.WriteString(byteBuffer, "Writing attached sprites (" + int2 + ")");
				}

				for (int1 = 0; int1 < int2; ++int1) {
					IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
					byteBuffer.putInt(spriteInstance.getID());
					byte byte1 = 0;
					boolean boolean2 = false;
					if (spriteInstance.offX != 0.0F || spriteInstance.offY != 0.0F || spriteInstance.offZ != 0.0F || spriteInstance.tintr != 1.0F || spriteInstance.tintg != 1.0F || spriteInstance.tintb != 1.0F) {
						byte1 = (byte)(byte1 | 2);
						boolean2 = true;
					}

					if (spriteInstance.Flip) {
						byte1 = (byte)(byte1 | 4);
					}

					if (spriteInstance.bCopyTargetAlpha) {
						byte1 = (byte)(byte1 | 8);
					}

					if (spriteInstance.bMultiplyObjectAlpha) {
						byte1 = (byte)(byte1 | 16);
					}

					byteBuffer.put(byte1);
					if (boolean2) {
						byteBuffer.putFloat(spriteInstance.offX);
						byteBuffer.putFloat(spriteInstance.offY);
						byteBuffer.putFloat(spriteInstance.offZ);
						byteBuffer.put(Bits.packFloatUnitToByte(spriteInstance.tintr));
						byteBuffer.put(Bits.packFloatUnitToByte(spriteInstance.tintg));
						byteBuffer.put(Bits.packFloatUnitToByte(spriteInstance.tintb));
					}

					if (spriteInstance.bMultiplyObjectAlpha) {
						byteBuffer.putFloat(spriteInstance.alpha);
					}
				}
			}

			int int3;
			if (this.name != null || this.spriteName != null) {
				bitHeaderWrite.addFlags(4);
				if (boolean1) {
					GameWindow.WriteString(byteBuffer, "Writing name");
				}

				byte byte2 = 0;
				byte byte3 = -1;
				int3 = -1;
				if (this.name != null) {
					if (this.name.equals("Grass")) {
						byte2 = (byte)(byte2 | 2);
					} else {
						byte3 = WorldDictionary.getIdForObjectName(this.name);
						if (byte3 >= 0) {
							byte2 = (byte)(byte2 | 4);
						} else {
							byte2 = (byte)(byte2 | 8);
						}
					}
				}

				if (this.spriteName != null) {
					int3 = WorldDictionary.getIdForSpriteName(this.spriteName);
					if (int3 >= 0) {
						byte2 = (byte)(byte2 | 16);
					} else {
						byte2 = (byte)(byte2 | 32);
					}
				}

				byteBuffer.put(byte2);
				if (this.name != null && !this.name.equals("Grass")) {
					if (byte3 >= 0) {
						byteBuffer.put(byte3);
					} else {
						GameWindow.WriteString(byteBuffer, this.name);
					}
				}

				if (this.spriteName != null) {
					if (int3 >= 0) {
						byteBuffer.putInt(int3);
					} else {
						GameWindow.WriteString(byteBuffer, this.spriteName);
					}
				}
			}

			if (this.customColor != null) {
				bitHeaderWrite.addFlags(8);
				byteBuffer.put(Bits.packFloatUnitToByte(this.customColor.r));
				byteBuffer.put(Bits.packFloatUnitToByte(this.customColor.g));
				byteBuffer.put(Bits.packFloatUnitToByte(this.customColor.b));
			}

			if (this.doNotSync) {
				bitHeaderWrite.addFlags(16);
			}

			if (this.isOutlineOnMouseover()) {
				bitHeaderWrite.addFlags(32);
			}

			BitHeaderWrite bitHeaderWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Short, byteBuffer);
			if (this.wallBloodSplats != null) {
				bitHeaderWrite2.addFlags(1);
				int1 = Math.min(this.wallBloodSplats.size(), 32);
				int3 = this.wallBloodSplats.size() - int1;
				byteBuffer.put((byte)int1);
				for (int int4 = int3; int4 < this.wallBloodSplats.size(); ++int4) {
					((IsoWallBloodSplat)this.wallBloodSplats.get(int4)).save(byteBuffer);
				}
			}

			if (this.getContainerCount() > 0) {
				bitHeaderWrite2.addFlags(2);
				if (boolean1) {
					GameWindow.WriteString(byteBuffer, "Writing container");
				}

				byteBuffer.put((byte)this.getContainerCount());
				for (int1 = 0; int1 < this.getContainerCount(); ++int1) {
					this.getContainerByIndex(int1).save(byteBuffer);
				}
			}

			if (this.table != null && !this.table.isEmpty()) {
				bitHeaderWrite2.addFlags(4);
				this.table.save(byteBuffer);
			}

			if (this.haveSpecialTooltip()) {
				bitHeaderWrite2.addFlags(8);
			}

			if (this.getKeyId() != -1) {
				bitHeaderWrite2.addFlags(16);
				byteBuffer.putInt(this.getKeyId());
			}

			if (this.usesExternalWaterSource) {
				bitHeaderWrite2.addFlags(32);
			}

			if (this.sheetRope) {
				bitHeaderWrite2.addFlags(64);
				byteBuffer.putFloat(this.sheetRopeHealth);
			}

			if (this.renderYOffset != 0.0F) {
				bitHeaderWrite2.addFlags(128);
				byteBuffer.putFloat(this.renderYOffset);
			}

			if (this.getOverlaySprite() != null) {
				bitHeaderWrite2.addFlags(256);
				int1 = WorldDictionary.getIdForSpriteName(this.getOverlaySprite().name);
				if (int1 < 0) {
					bitHeaderWrite2.addFlags(512);
					GameWindow.WriteString(byteBuffer, this.getOverlaySprite().name);
				} else {
					byteBuffer.putInt(int1);
				}

				if (this.getOverlaySpriteColor() != null) {
					bitHeaderWrite2.addFlags(1024);
					byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().r));
					byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().g));
					byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().b));
					byteBuffer.put(Bits.packFloatUnitToByte(this.getOverlaySpriteColor().a));
				}
			}

			if (this.isMovedThumpable()) {
				bitHeaderWrite2.addFlags(2048);
			}

			if (!bitHeaderWrite2.equals(0)) {
				bitHeaderWrite.addFlags(64);
				bitHeaderWrite2.write();
			} else {
				byteBuffer.position(bitHeaderWrite2.getStartPosition());
			}

			bitHeaderWrite.write();
			bitHeaderWrite.release();
			bitHeaderWrite2.release();
		}
	}

	public void saveState(ByteBuffer byteBuffer) throws IOException {
	}

	public void loadState(ByteBuffer byteBuffer) throws IOException {
	}

	public void softReset() {
		if (this.container != null) {
			this.container.Items.clear();
			this.container.bExplored = false;
			this.setOverlaySprite((String)null, -1.0F, -1.0F, -1.0F, -1.0F, false);
		}

		if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
			for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
				IsoSprite sprite = ((IsoSpriteInstance)this.AttachedAnimSprite.get(int1)).parentSprite;
				if (sprite.name != null && sprite.name.contains("blood")) {
					this.AttachedAnimSprite.remove(int1);
					--int1;
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
			this.HitByVehicle((BaseVehicle)object, float1);
			if (this.Damage <= 0 && BrokenFences.getInstance().isBreakableObject(this)) {
				PropertyContainer propertyContainer = this.getProperties();
				IsoDirections directions;
				if (propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW)) {
					directions = object.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
				} else if (propertyContainer.Is(IsoFlagType.collideN)) {
					directions = object.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
				} else {
					directions = object.getX() >= this.getX() ? IsoDirections.W : IsoDirections.E;
				}

				BrokenFences.getInstance().destroyFence(this, directions);
			}
		}
	}

	public void Damage(float float1) {
		this.Damage = (short)((int)((double)this.Damage - (double)float1 * 0.1));
	}

	public void HitByVehicle(BaseVehicle baseVehicle, float float1) {
		short short1 = this.Damage;
		this.Damage = (short)((int)((double)this.Damage - (double)float1 * 0.1));
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)this.square.x + 0.5F, (float)this.square.y + 0.5F, (float)this.square.z);
		long long1 = baseSoundEmitter.playSound("VehicleHitObject");
		baseSoundEmitter.setParameterValue(long1, FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
		WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		if (this.getProperties().Is("HitByCar") && this.getSprite().getProperties().Val("DamagedSprite") != null && !this.getSprite().getProperties().Val("DamagedSprite").equals("") && this.Damage <= 90 && short1 > 90) {
			this.setSprite(IsoSpriteManager.instance.getSprite(this.getSprite().getProperties().Val("DamagedSprite")));
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

		if (this.Damage <= 40 && this.getProperties().Is("HitByCar") && !BrokenFences.getInstance().isBreakableObject(this)) {
			this.getSquare().transmitRemoveItemFromSquare(this);
		}
	}

	public void Collision(Vector2 vector2, IsoObject object) {
		if (object instanceof BaseVehicle) {
			if (this.getProperties().Is("CarSlowFactor")) {
				int int1 = Integer.parseInt(this.getProperties().Val("CarSlowFactor"));
				BaseVehicle baseVehicle = (BaseVehicle)object;
				baseVehicle.ApplyImpulse(this, Math.abs(baseVehicle.getFudgedMass() * baseVehicle.getCurrentSpeedKmHour() * (float)int1 / 100.0F));
			}

			if (this.getProperties().Is("HitByCar")) {
				BaseVehicle baseVehicle2 = (BaseVehicle)object;
				String string = this.getSprite().getProperties().Val("MinimumCarSpeedDmg");
				if (string == null) {
					string = "150";
				}

				if (Math.abs(baseVehicle2.getCurrentSpeedKmHour()) > (float)Integer.parseInt(string)) {
					this.HitByVehicle(baseVehicle2, Math.abs(baseVehicle2.getFudgedMass() * baseVehicle2.getCurrentSpeedKmHour()) / 300.0F);
					if (this.Damage <= 0 && BrokenFences.getInstance().isBreakableObject(this)) {
						PropertyContainer propertyContainer = this.getProperties();
						IsoDirections directions;
						if (propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW)) {
							directions = baseVehicle2.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
						} else if (propertyContainer.Is(IsoFlagType.collideN)) {
							directions = baseVehicle2.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
						} else {
							directions = baseVehicle2.getX() >= this.getX() ? IsoDirections.W : IsoDirections.E;
						}

						BrokenFences.getInstance().destroyFence(this, directions);
					}
				} else if (!this.square.getProperties().Is(IsoFlagType.collideN) && !this.square.getProperties().Is(IsoFlagType.collideW)) {
					baseVehicle2.ApplyImpulse(this, Math.abs(baseVehicle2.getFudgedMass() * baseVehicle2.getCurrentSpeedKmHour() * 10.0F / 200.0F));
					if (baseVehicle2.getCurrentSpeedKmHour() > 3.0F) {
						baseVehicle2.ApplyImpulse(this, Math.abs(baseVehicle2.getFudgedMass() * baseVehicle2.getCurrentSpeedKmHour() * 10.0F / 150.0F));
					}

					baseVehicle2.jniSpeed = 0.0F;
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

	public void setAlpha(float float1) {
		this.setAlpha(IsoPlayer.getPlayerIndex(), float1);
	}

	public void setAlpha(int int1, float float1) {
		this.alpha[int1] = PZMath.clamp(float1, 0.0F, 1.0F);
	}

	public void setAlphaToTarget(int int1) {
		this.setAlpha(int1, this.getTargetAlpha(int1));
	}

	public void setAlphaAndTarget(float float1) {
		int int1 = IsoPlayer.getPlayerIndex();
		this.setAlphaAndTarget(int1, float1);
	}

	public void setAlphaAndTarget(int int1, float float1) {
		this.setAlpha(int1, float1);
		this.setTargetAlpha(int1, float1);
	}

	public float getAlpha() {
		return this.getAlpha(IsoPlayer.getPlayerIndex());
	}

	public float getAlpha(int int1) {
		return this.alpha[int1];
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

	public void clearAttachedAnimSprite() {
		if (this.AttachedAnimSprite != null) {
			for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
				IsoSpriteInstance.add((IsoSpriteInstance)this.AttachedAnimSprite.get(int1));
			}

			this.AttachedAnimSprite.clear();
		}
	}

	public ItemContainer getContainer() {
		return this.container;
	}

	public void setContainer(ItemContainer itemContainer) {
		itemContainer.parent = this;
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
		this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
		this.sprite.LoadFramesNoDirPageSimple(string);
		this.tile = string;
		this.spriteName = string;
		this.windRenderEffects = null;
		this.checkMoveWithWind();
	}

	public void setSpriteFromName(String string) {
		this.sprite = IsoSpriteManager.instance.getSprite(string);
		this.windRenderEffects = null;
		this.checkMoveWithWind();
	}

	public float getTargetAlpha() {
		return this.getTargetAlpha(IsoPlayer.getPlayerIndex());
	}

	public void setTargetAlpha(float float1) {
		this.setTargetAlpha(IsoPlayer.getPlayerIndex(), float1);
	}

	public void setTargetAlpha(int int1, float float1) {
		this.targetAlpha[int1] = PZMath.clamp(float1, 0.0F, 1.0F);
	}

	public float getTargetAlpha(int int1) {
		return this.targetAlpha[int1];
	}

	public boolean isAlphaAndTargetZero() {
		int int1 = IsoPlayer.getPlayerIndex();
		return this.isAlphaAndTargetZero(int1);
	}

	public boolean isAlphaAndTargetZero(int int1) {
		return this.isAlphaZero(int1) && this.isTargetAlphaZero(int1);
	}

	public boolean isAlphaZero() {
		int int1 = IsoPlayer.getPlayerIndex();
		return this.isAlphaZero(int1);
	}

	public boolean isAlphaZero(int int1) {
		return this.alpha[int1] <= 0.001F;
	}

	public boolean isTargetAlphaZero(int int1) {
		return this.targetAlpha[int1] <= 0.001F;
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
				if (this.getSquare() != null) {
					IsoGridSquare square = this.getCell().getGridSquare(this.getSquare().x - 1, this.getSquare().y, this.getSquare().z);
					IsoGridSquare square2;
					if (square != null) {
						square2 = this.getCell().getGridSquare(square.x, square.y + 1, square.z);
						if (square2 != null && !square2.isExteriorCache && square2.getWall(true) != null) {
							this.windRenderEffects = null;
							return;
						}
					}

					square2 = this.getCell().getGridSquare(this.getSquare().x, this.getSquare().y - 1, this.getSquare().z);
					if (square2 != null) {
						IsoGridSquare square3 = this.getCell().getGridSquare(square2.x + 1, square2.y, square2.z);
						if (square3 != null && !square3.isExteriorCache && square3.getWall(false) != null) {
							this.windRenderEffects = null;
							return;
						}
					}
				}

				this.windRenderEffects = ObjectRenderEffects.getNextWindEffect(this.sprite.windType, boolean1);
			} else {
				if (this.windRenderEffects != null && (this.sprite == null || !this.sprite.moveWithWind)) {
					this.windRenderEffects = null;
				}
			}
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
		}

		if (this.wallBloodSplats != null) {
			this.wallBloodSplats.clear();
		}

		this.overlaySprite = null;
		this.overlaySpriteColor = null;
		this.customColor = null;
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
		this.partialThumpDmg = 0.0F;
		this.NoPicking = false;
		this.offsetX = (float)(32 * Core.TileScale);
		this.offsetY = (float)(96 * Core.TileScale);
		this.OutlineOnMouseover = false;
		this.rerouteMask = null;
		this.sprite = null;
		this.square = null;
		for (int1 = 0; int1 < 4; ++int1) {
			this.setAlphaAndTarget(int1, 1.0F);
		}

		this.bNeverDoneAlpha = true;
		this.bAlphaForced = false;
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
		this.sx = 0.0F;
		this.windRenderEffects = null;
		this.objectRenderEffects = null;
		this.sheetRope = false;
		this.sheetRopeHealth = 100.0F;
		this.bMovedThumpable = false;
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
						long1 += (long)(((InventoryItem)this.container.Items.get(int1)).getModule().hashCode() + ((InventoryItem)this.container.Items.get(int1)).getType().hashCode() + ((InventoryItem)this.container.Items.get(int1)).id);
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

	public void setName(String string) {
		this.name = string;
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
		return false;
	}

	public String getScriptName() {
		return "none";
	}

	public void AttachAnim(String string, String string2, int int1, float float1, int int2, int int3, boolean boolean1, int int4, boolean boolean2, float float2, ColorInfo colorInfo) {
		if (this.AttachedAnimSprite == null) {
			this.AttachedAnimSprite = new ArrayList(4);
		}

		IsoSprite sprite = IsoSprite.CreateSpriteUsingCache(string, string2, int1);
		sprite.TintMod.set(colorInfo);
		sprite.soffX = (short)(-int2);
		sprite.soffY = (short)(-int3);
		sprite.Animate = true;
		sprite.Loop = boolean1;
		sprite.DeleteWhenFinished = boolean2;
		sprite.PlayAnim(string2);
		IsoSpriteInstance spriteInstance = sprite.def;
		spriteInstance.AnimFrameIncrease = float1;
		spriteInstance.Frame = 0.0F;
		this.AttachedAnimSprite.add(spriteInstance);
	}

	public void AttachExistingAnim(IsoSprite sprite, int int1, int int2, boolean boolean1, int int3, boolean boolean2, float float1, ColorInfo colorInfo) {
		if (this.AttachedAnimSprite == null) {
			this.AttachedAnimSprite = new ArrayList(4);
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

	public void setOffsetX(float float1) {
		this.offsetX = float1;
	}

	public float getOffsetY() {
		return this.offsetY;
	}

	public void setOffsetY(float float1) {
		this.offsetY = float1;
	}

	public IsoObject getRerouteMaskObject() {
		return this.rerouteMask;
	}

	public boolean HasTooltip() {
		return false;
	}

	public boolean getUsesExternalWaterSource() {
		return this.usesExternalWaterSource;
	}

	public void setUsesExternalWaterSource(boolean boolean1) {
		this.usesExternalWaterSource = boolean1;
	}

	public boolean hasExternalWaterSource() {
		return this.externalWaterSource != null;
	}

	public void doFindExternalWaterSource() {
		this.externalWaterSource = FindExternalWaterSource(this.getSquare());
	}

	public static IsoObject FindExternalWaterSource(IsoGridSquare square) {
		return square == null ? null : FindExternalWaterSource(square.getX(), square.getY(), square.getZ());
	}

	public static IsoObject FindExternalWaterSource(int int1, int int2, int int3) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3 + 1);
		IsoObject object = null;
		IsoObject object2 = FindWaterSourceOnSquare(square);
		if (object2 != null) {
			if (object2.hasWater()) {
				return object2;
			}

			object = object2;
		}

		for (int int4 = -1; int4 <= 1; ++int4) {
			for (int int5 = -1; int5 <= 1; ++int5) {
				if (int5 != 0 || int4 != 0) {
					square = IsoWorld.instance.CurrentCell.getGridSquare(int1 + int5, int2 + int4, int3 + 1);
					object2 = FindWaterSourceOnSquare(square);
					if (object2 != null) {
						if (object2.hasWater()) {
							return object2;
						}

						if (object == null) {
							object = object2;
						}
					}
				}
			}
		}

		return object;
	}

	public static IsoObject FindWaterSourceOnSquare(IsoGridSquare square) {
		if (square == null) {
			return null;
		} else {
			PZArrayList pZArrayList = square.getObjects();
			for (int int1 = 0; int1 < pZArrayList.size(); ++int1) {
				IsoObject object = (IsoObject)pZArrayList.get(int1);
				if (object instanceof IsoThumpable && (object.getSprite() == null || !object.getSprite().solidfloor) && !object.getUsesExternalWaterSource() && object.getWaterMax() > 0) {
					return object;
				}
			}

			return null;
		}
	}

	public int getPipedFuelAmount() {
		if (this.sprite == null) {
			return 0;
		} else {
			double double1 = 0.0;
			if (this.hasModData() && !this.getModData().isEmpty()) {
				Object object = this.getModData().rawget("fuelAmount");
				if (object != null) {
					double1 = (Double)object;
				}
			}

			if (this.sprite.getProperties().Is("fuelAmount")) {
				if (SandboxOptions.instance.FuelStationGas.getValue() == 7) {
					return 1000;
				}

				if (double1 == 0.0 && (SandboxOptions.getInstance().AllowExteriorGenerator.getValue() && this.getSquare().haveElectricity() || IsoWorld.instance.isHydroPowerOn())) {
					float float1 = 0.8F;
					float float2 = 1.0F;
					switch (SandboxOptions.getInstance().FuelStationGas.getValue()) {
					case 1: 
						float2 = 0.0F;
						float1 = 0.0F;
						break;
					
					case 2: 
						float1 = 0.2F;
						float2 = 0.4F;
						break;
					
					case 3: 
						float1 = 0.3F;
						float2 = 0.5F;
						break;
					
					case 4: 
						float1 = 0.5F;
						float2 = 0.7F;
						break;
					
					case 5: 
						float1 = 0.7F;
						float2 = 0.8F;
						break;
					
					case 6: 
						float1 = 0.8F;
						float2 = 0.9F;
						break;
					
					case 7: 
						float2 = 1.0F;
						float1 = 1.0F;
					
					}

					double1 = (double)((int)Rand.Next((float)Integer.parseInt(this.sprite.getProperties().Val("fuelAmount")) * float1, (float)Integer.parseInt(this.sprite.getProperties().Val("fuelAmount")) * float2));
					this.getModData().rawset("fuelAmount", double1);
					this.transmitModData();
					return (int)double1;
				}
			}

			return (int)double1;
		}
	}

	public void setPipedFuelAmount(int int1) {
		int1 = Math.max(0, int1);
		int int2 = this.getPipedFuelAmount();
		if (int1 != int2) {
			if (int1 == 0 && int2 != 0) {
				int1 = -1;
			}

			this.getModData().rawset("fuelAmount", (double)int1);
			this.transmitModData();
		}
	}

	private boolean isWaterInfinite() {
		if (this.sprite == null) {
			return false;
		} else if (this.square != null && this.square.getRoom() != null) {
			if (!this.sprite.getProperties().Is(IsoFlagType.waterPiped)) {
				return false;
			} else if (GameTime.getInstance().getNightsSurvived() >= SandboxOptions.instance.getWaterShutModifier()) {
				return false;
			} else {
				return !this.hasModData() || !(this.getModData().rawget("canBeWaterPiped") instanceof Boolean) || !(Boolean)this.getModData().rawget("canBeWaterPiped");
			}
		} else {
			return false;
		}
	}

	private IsoObject checkExternalWaterSource() {
		if (!this.usesExternalWaterSource) {
			return null;
		} else {
			if (this.externalWaterSource == null || !this.externalWaterSource.hasWater()) {
				this.doFindExternalWaterSource();
			}

			return this.externalWaterSource;
		}
	}

	public int getWaterAmount() {
		if (this.sprite == null) {
			return 0;
		} else if (this.usesExternalWaterSource) {
			if (this.isWaterInfinite()) {
				return 10000;
			} else {
				IsoObject object = this.checkExternalWaterSource();
				return object == null ? 0 : object.getWaterAmount();
			}
		} else if (this.isWaterInfinite()) {
			return 10000;
		} else {
			if (this.hasModData() && !this.getModData().isEmpty()) {
				Object object2 = this.getModData().rawget("waterAmount");
				if (object2 != null) {
					if (object2 instanceof Double) {
						return (int)Math.max(0.0, (Double)object2);
					}

					if (object2 instanceof String) {
						return Math.max(0, Integer.parseInt((String)object2));
					}

					return 0;
				}
			}

			if (this.square != null && !this.square.getProperties().Is(IsoFlagType.water) && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09F) {
				return (int)(this.square.getPuddlesInGround() * 10.0F);
			} else if (!this.sprite.Properties.Is("waterAmount")) {
				return 0;
			} else {
				int int1 = Integer.parseInt(this.sprite.getProperties().Val("waterAmount"));
				return int1;
			}
		}
	}

	public void setWaterAmount(int int1) {
		if (this.usesExternalWaterSource) {
			if (!this.isWaterInfinite()) {
				IsoObject object = this.checkExternalWaterSource();
				if (object != null) {
					object.setWaterAmount(int1);
				}
			}
		} else {
			int1 = Math.max(0, int1);
			int int2 = this.getWaterAmount();
			if (int1 != int2) {
				boolean boolean1 = true;
				if (this.hasModData() && !this.getModData().isEmpty()) {
					boolean1 = this.getModData().rawget("waterAmount") == null;
				}

				if (boolean1) {
					this.getModData().rawset("waterMax", (double)int2);
				}

				this.getModData().rawset("waterAmount", (double)int1);
				if (int1 <= 0) {
					this.setTaintedWater(false);
				}

				LuaEventManager.triggerEvent("OnWaterAmountChange", this, int2);
			}
		}
	}

	public int getWaterMax() {
		if (this.sprite == null) {
			return 0;
		} else if (this.usesExternalWaterSource) {
			if (this.isWaterInfinite()) {
				return 10000;
			} else {
				IsoObject object = this.checkExternalWaterSource();
				return object != null ? object.getWaterMax() : 0;
			}
		} else if (this.isWaterInfinite()) {
			return 10000;
		} else {
			if (this.hasModData() && !this.getModData().isEmpty()) {
				Object object2 = this.getModData().rawget("waterMax");
				if (object2 != null) {
					if (object2 instanceof Double) {
						return (int)Math.max(0.0, (Double)object2);
					}

					if (object2 instanceof String) {
						return Math.max(0, Integer.parseInt((String)object2));
					}

					return 0;
				}
			}

			if (this.square != null && !this.square.getProperties().Is(IsoFlagType.water) && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09F) {
				return (int)(this.square.getPuddlesInGround() * 10.0F);
			} else if (this.sprite.Properties.Is("waterMaxAmount")) {
				return Integer.parseInt(this.sprite.getProperties().Val("waterMaxAmount"));
			} else {
				return this.sprite.Properties.Is("waterAmount") ? Integer.parseInt(this.sprite.getProperties().Val("waterAmount")) : 0;
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

			if (this.square != null && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09F) {
				return int3;
			} else {
				if (!this.usesExternalWaterSource) {
					if (this.sprite.getProperties().Is(IsoFlagType.water)) {
						return int3;
					}

					if (this.isWaterInfinite()) {
						return int3;
					}
				}

				this.setWaterAmount(int2 - int3);
				return int3;
			}
		}
	}

	public boolean hasWater() {
		if (this.square != null && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09F) {
			return true;
		} else {
			return this.getWaterAmount() > 0;
		}
	}

	public boolean isTaintedWater() {
		if (this.square != null && this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getPuddlesInGround() > 0.09F) {
			return true;
		} else {
			if (this.hasModData()) {
				Object object = this.getModData().rawget("taintedWater");
				if (object instanceof Boolean) {
					return (Boolean)object;
				}
			}

			return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.taintedWater);
		}
	}

	public void setTaintedWater(boolean boolean1) {
		this.getModData().rawset("taintedWater", boolean1);
	}

	public InventoryItem replaceItem(InventoryItem inventoryItem) {
		String string = null;
		InventoryItem inventoryItem2 = null;
		if (inventoryItem != null) {
			if (inventoryItem.hasReplaceType(this.getObjectName())) {
				string = inventoryItem.getReplaceType(this.getObjectName());
			} else if (inventoryItem.hasReplaceType("WaterSource")) {
				string = inventoryItem.getReplaceType("WaterSource");
			}
		}

		if (string != null) {
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

	@Deprecated
	public void useItemOn(InventoryItem inventoryItem) {
		String string = null;
		if (inventoryItem != null) {
			if (inventoryItem.hasReplaceType(this.getObjectName())) {
				string = inventoryItem.getReplaceType(this.getObjectName());
			} else if (inventoryItem.hasReplaceType("WaterSource")) {
				string = inventoryItem.getReplaceType("WaterSource");
				this.useWater(10);
			}
		}

		if (string != null) {
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
		}
	}

	public void RemoveAttachedAnim(int int1) {
		if (this.AttachedAnimSprite != null) {
			if (int1 >= 0 && int1 < this.AttachedAnimSprite.size()) {
				((IsoSpriteInstance)this.AttachedAnimSprite.get(int1)).Dispose();
				this.AttachedAnimSprite.remove(int1);
			}
		}
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else {
			PropertyContainer propertyContainer = this.getProperties();
			if (propertyContainer != null) {
				if (this.getType() == IsoObjectType.wall) {
					if (propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW)) {
						return vector2.set(this.getX(), this.getY());
					}

					if (propertyContainer.Is(IsoFlagType.collideN)) {
						return vector2.set(this.getX() + 0.5F, this.getY());
					}

					if (propertyContainer.Is(IsoFlagType.collideW)) {
						return vector2.set(this.getX(), this.getY() + 0.5F);
					}

					if (propertyContainer.Is(IsoFlagType.DoorWallN)) {
						return vector2.set(this.getX() + 0.5F, this.getY());
					}

					if (propertyContainer.Is(IsoFlagType.DoorWallW)) {
						return vector2.set(this.getX(), this.getY() + 0.5F);
					}
				} else {
					if (propertyContainer.Is(IsoFlagType.attachedN)) {
						return vector2.set(this.getX() + 0.5F, this.getY());
					}

					if (propertyContainer.Is(IsoFlagType.attachedS)) {
						return vector2.set(this.getX() + 0.5F, this.getY() + 1.0F);
					}

					if (propertyContainer.Is(IsoFlagType.attachedW)) {
						return vector2.set(this.getX(), this.getY() + 0.5F);
					}

					if (propertyContainer.Is(IsoFlagType.attachedE)) {
						return vector2.set(this.getX() + 1.0F, this.getY() + 0.5F);
					}
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
		this.sx = 0.0F;
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

	public float getSurfaceOffsetNoTable() {
		float float1 = 0.0F;
		int int1 = 0;
		PropertyContainer propertyContainer = this.getProperties();
		if (propertyContainer != null) {
			float1 = (float)propertyContainer.getSurface();
			int1 = propertyContainer.getItemHeight();
		}

		return float1 + this.getRenderYOffset() + (float)int1;
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

	public boolean isHoppable() {
		return this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.HoppableN) || this.sprite.getProperties().Is(IsoFlagType.HoppableW));
	}

	public boolean isNorthHoppable() {
		return this.sprite != null && this.isHoppable() && this.sprite.getProperties().Is(IsoFlagType.HoppableN);
	}

	public boolean haveSheetRope() {
		return IsoWindow.isTopOfSheetRopeHere(this.square, this.isNorthHoppable());
	}

	public int countAddSheetRope() {
		return IsoWindow.countAddSheetRope(this.square, this.isNorthHoppable());
	}

	public boolean canAddSheetRope() {
		return IsoWindow.canAddSheetRope(this.square, this.isNorthHoppable());
	}

	public boolean addSheetRope(IsoPlayer player, String string) {
		return !this.canAddSheetRope() ? false : IsoWindow.addSheetRope(player, this.square, this.isNorthHoppable(), string);
	}

	public boolean removeSheetRope(IsoPlayer player) {
		return this.haveSheetRope() ? IsoWindow.removeSheetRope(player, this.square, this.isNorthHoppable()) : false;
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (!this.isSpriteInvisible()) {
			this.prepareToRender(colorInfo);
			int int1 = IsoCamera.frameState.playerIndex;
			int int2;
			float float4;
			float float5;
			float float6;
			Texture texture;
			if (this.shouldDrawMainSprite()) {
				this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink());
				if (this.isOutlineHighlight(int1) && !this.isOutlineHlAttached(int1) && IsoObject.OutlineShader.instance.StartShader()) {
					int2 = this.outlineHighlightCol[int1];
					float4 = Color.getRedChannelFromABGR(int2);
					float5 = Color.getGreenChannelFromABGR(int2);
					float6 = Color.getBlueChannelFromABGR(int2);
					IsoObject.OutlineShader.instance.setOutlineColor(float4, float5, float6, this.isOutlineHlBlink(int1) ? Core.blinkAlpha : 1.0F);
					texture = this.sprite.getTextureForCurrentFrame(this.dir);
					if (texture != null) {
						IsoObject.OutlineShader.instance.setStepSize(this.outlineThickness, texture.getWidth(), texture.getHeight());
					}

					this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink());
					IndieGL.EndShader();
				}
			}

			this.renderAttachedAndOverlaySpritesInternal(float1, float2, float3, colorInfo, boolean1, boolean2, shader, (Consumer)null);
			if (this.isOutlineHighlight(int1) && this.isOutlineHlAttached(int1) && IsoObject.OutlineShader.instance.StartShader()) {
				int2 = this.outlineHighlightCol[int1];
				float4 = Color.getRedChannelFromABGR(int2);
				float5 = Color.getGreenChannelFromABGR(int2);
				float6 = Color.getBlueChannelFromABGR(int2);
				IsoObject.OutlineShader.instance.setOutlineColor(float4, float5, float6, this.isOutlineHlBlink(int1) ? Core.blinkAlpha : 1.0F);
				texture = this.sprite.getTextureForCurrentFrame(this.dir);
				if (texture != null) {
					IsoObject.OutlineShader.instance.setStepSize(this.outlineThickness, texture.getWidth(), texture.getHeight());
				}

				if (this.shouldDrawMainSprite()) {
					this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink());
				}

				this.renderAttachedAndOverlaySpritesInternal(float1, float2, float3, colorInfo, boolean1, boolean2, shader, (Consumer)null);
				IndieGL.EndShader();
			}

			if (!this.bAlphaForced && this.isUpdateAlphaDuringRender()) {
				this.updateAlpha(int1);
			}

			this.debugRenderItemHeight(float1, float2, float3);
			this.debugRenderSurface(float1, float2, float3);
		}
	}

	private void debugRenderItemHeight(float float1, float float2, float float3) {
		if (DebugOptions.instance.IsoSprite.ItemHeight.getValue()) {
			if (this.square != null && IsoCamera.frameState.CamCharacterSquare != null && this.square.z == IsoCamera.frameState.CamCharacterSquare.z) {
				int int1 = this.sprite.getProperties().getItemHeight();
				if (int1 > 0) {
					int int2 = 0;
					if (this.sprite != null && this.sprite.getProperties().getSurface() > 0 && this.sprite.getProperties().isSurfaceOffset()) {
						int2 = this.sprite.getProperties().getSurface();
					}

					LineDrawer.addRectYOffset(float1, float2, float3, 1.0F, 1.0F, (int)this.getRenderYOffset() + int2 + int1, 0.66F, 0.66F, 0.66F);
				}
			}
		}
	}

	private void debugRenderSurface(float float1, float float2, float float3) {
		if (DebugOptions.instance.IsoSprite.Surface.getValue()) {
			if (this.square != null && IsoCamera.frameState.CamCharacterSquare != null && this.square.z == IsoCamera.frameState.CamCharacterSquare.z) {
				int int1 = 0;
				if (this.sprite != null && this.sprite.getProperties().getSurface() > 0 && !this.sprite.getProperties().isSurfaceOffset()) {
					int1 = this.sprite.getProperties().getSurface();
				}

				if (int1 > 0) {
					LineDrawer.addRectYOffset(float1, float2, float3, 1.0F, 1.0F, (int)this.getRenderYOffset() + int1, 1.0F, 1.0F, 1.0F);
				}
			}
		}
	}

	public void renderFloorTile(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader, Consumer consumer, Consumer consumer2) {
		if (!this.isSpriteInvisible()) {
			this.prepareToRender(colorInfo);
			FloorShaper floorShaper = (FloorShaper)Type.tryCastTo(consumer, FloorShaper.class);
			FloorShaper floorShaper2 = (FloorShaper)Type.tryCastTo(consumer2, FloorShaper.class);
			if ((floorShaper != null || floorShaper2 != null) && this.isHighlighted() && this.getHighlightColor() != null) {
				ColorInfo colorInfo2 = this.getHighlightColor();
				float float4 = colorInfo2.a * (this.isBlink() ? Core.blinkAlpha : 1.0F);
				int int1 = Color.colorToABGR(colorInfo2.r, colorInfo2.g, colorInfo2.b, float4);
				if (floorShaper != null) {
					floorShaper.setTintColor(int1);
				}

				if (floorShaper2 != null) {
					floorShaper2.setTintColor(int1);
				}
			}

			if (this.shouldDrawMainSprite()) {
				IndieGL.shaderSetValue(shader, "floorLayer", 0);
				this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink(), consumer);
			}

			this.renderAttachedAndOverlaySpritesInternal(float1, float2, float3, colorInfo, boolean1, boolean2, shader, consumer2);
			if (floorShaper != null) {
				floorShaper.setTintColor(0);
			}

			if (floorShaper2 != null) {
				floorShaper2.setTintColor(0);
			}
		}
	}

	public void renderWallTile(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader, Consumer consumer) {
		if (!this.isSpriteInvisible()) {
			this.renderWallTileOnly(float1, float2, float3, colorInfo, shader, consumer);
			this.renderAttachedAndOverlaySpritesInternal(float1, float2, float3, colorInfo, boolean1, boolean2, shader, consumer);
			int int1 = IsoCamera.frameState.playerIndex;
			if (this.isOutlineHighlight(int1) && !this.isOutlineHlAttached(int1) && IsoObject.OutlineShader.instance.StartShader()) {
				int int2 = this.outlineHighlightCol[int1];
				float float4 = Color.getRedChannelFromABGR(int2);
				float float5 = Color.getGreenChannelFromABGR(int2);
				float float6 = Color.getBlueChannelFromABGR(int2);
				IsoObject.OutlineShader.instance.setOutlineColor(float4, float5, float6, this.isOutlineHlBlink(int1) ? Core.blinkAlpha : 1.0F);
				Texture texture = this.sprite.getTextureForCurrentFrame(this.dir);
				if (texture != null) {
					IsoObject.OutlineShader.instance.setStepSize(this.outlineThickness, texture.getWidth(), texture.getHeight());
				}

				this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink());
				IndieGL.EndShader();
			}
		}
	}

	public void renderWallTileOnly(float float1, float float2, float float3, ColorInfo colorInfo, Shader shader, Consumer consumer) {
		if (!this.isSpriteInvisible()) {
			this.prepareToRender(colorInfo);
			WallShaper wallShaper = (WallShaper)Type.tryCastTo(consumer, WallShaper.class);
			if (wallShaper != null && this.isHighlighted() && this.getHighlightColor() != null) {
				ColorInfo colorInfo2 = this.getHighlightColor();
				float float4 = colorInfo2.a * (this.isBlink() ? Core.blinkAlpha : 1.0F);
				int int1 = Color.colorToABGR(colorInfo2.r, colorInfo2.g, colorInfo2.b, float4);
				wallShaper.setTintColor(int1);
			}

			if (this.shouldDrawMainSprite()) {
				IndieGL.pushShader(shader);
				this.sprite.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink(), consumer);
				IndieGL.popShader(shader);
			}

			if (wallShaper != null) {
				wallShaper.setTintColor(0);
			}
		}
	}

	private boolean shouldDrawMainSprite() {
		if (this.sprite == null) {
			return false;
		} else {
			return DebugOptions.instance.Terrain.RenderTiles.RenderSprites.getValue();
		}
	}

	public void renderAttachedAndOverlaySprites(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader, Consumer consumer) {
		if (!this.isSpriteInvisible()) {
			this.renderAttachedAndOverlaySpritesInternal(float1, float2, float3, colorInfo, boolean1, boolean2, shader, consumer);
		}
	}

	private void renderAttachedAndOverlaySpritesInternal(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader, Consumer consumer) {
		if (this.isHighlighted()) {
			colorInfo = stCol;
		}

		this.renderOverlaySprites(float1, float2, float3, colorInfo);
		if (boolean1) {
			this.renderAttachedSprites(float1, float2, float3, colorInfo, boolean2, shader, consumer);
		}
	}

	private void prepareToRender(ColorInfo colorInfo) {
		stCol.set(colorInfo);
		if (this.isHighlighted()) {
			stCol.set(this.getHighlightColor());
			if (this.isBlink()) {
				stCol.a = Core.blinkAlpha;
			} else {
				stCol.a = 1.0F;
			}

			stCol.r = colorInfo.r * (1.0F - stCol.a) + this.getHighlightColor().r * stCol.a;
			stCol.g = colorInfo.g * (1.0F - stCol.a) + this.getHighlightColor().g * stCol.a;
			stCol.b = colorInfo.b * (1.0F - stCol.a) + this.getHighlightColor().b * stCol.a;
			stCol.a = colorInfo.a;
		}

		float float1;
		if (this.customColor != null) {
			float1 = this.square != null ? this.square.getDarkMulti(IsoPlayer.getPlayerIndex()) : 1.0F;
			if (this.isHighlighted()) {
				ColorInfo colorInfo2 = stCol;
				colorInfo2.r *= this.customColor.r * float1;
				colorInfo2 = stCol;
				colorInfo2.g *= this.customColor.g * float1;
				colorInfo2 = stCol;
				colorInfo2.b *= this.customColor.b * float1;
			} else {
				stCol.r = this.customColor.r * float1;
				stCol.g = this.customColor.g * float1;
				stCol.b = this.customColor.b * float1;
			}
		}

		float float2;
		float float3;
		if (this.sprite != null && this.sprite.forceAmbient) {
			float1 = rmod * this.tintr;
			float2 = gmod * this.tintg;
			float3 = bmod * this.tintb;
			if (!this.isHighlighted()) {
				stCol.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * float1;
				stCol.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * float2;
				stCol.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * float3;
			}
		}

		int int1 = IsoPlayer.getPlayerIndex();
		float2 = IsoCamera.frameState.CamCharacterX;
		float3 = IsoCamera.frameState.CamCharacterY;
		float float4 = IsoCamera.frameState.CamCharacterZ;
		if (IsoWorld.instance.CurrentCell.IsPlayerWindowPeeking(int1)) {
			IsoPlayer player = IsoPlayer.players[int1];
			IsoDirections directions = IsoDirections.fromAngle(player.getForwardDirection());
			if (directions == IsoDirections.N || directions == IsoDirections.NW) {
				float3 = (float)((double)float3 - 1.0);
			}

			if (directions == IsoDirections.W || directions == IsoDirections.NW) {
				float2 = (float)((double)float2 - 1.0);
			}
		}

		if (this == IsoCamera.CamCharacter) {
			this.setAlphaAndTarget(int1, 1.0F);
		}

		lastRenderedRendered = lastRendered;
		lastRendered = this;
		if (this.sprite != null && !(this instanceof IsoPhysicsObject) && IsoCamera.CamCharacter != null) {
			boolean boolean1 = this instanceof IsoWindow || this.sprite.getType() == IsoObjectType.doorW || this.sprite.getType() == IsoObjectType.doorN;
			if (this.sprite.getProperties().Is("GarageDoor")) {
				boolean1 = false;
			}

			if (!boolean1 && ((float)this.square.getX() > float2 || (float)this.square.getY() > float3) && (int)float4 <= this.square.getZ()) {
				boolean boolean2 = false;
				float float5 = 0.2F;
				boolean boolean3 = (this.sprite.cutW || this.sprite.getProperties().Is(IsoFlagType.doorW)) && (float)this.square.getX() > float2;
				boolean boolean4 = (this.sprite.cutN || this.sprite.getProperties().Is(IsoFlagType.doorN)) && (float)this.square.getY() > float3;
				if (boolean3 && this.square.getProperties().Is(IsoFlagType.WallSE) && (float)this.square.getY() <= float3) {
					boolean3 = false;
				}

				if (!boolean3 && !boolean4) {
					boolean boolean5 = this.getType() == IsoObjectType.WestRoofB || this.getType() == IsoObjectType.WestRoofM || this.getType() == IsoObjectType.WestRoofT;
					boolean boolean6 = boolean5 && (int)float4 == this.square.getZ() && this.square.getBuilding() == null;
					if (boolean6 && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this.square, int1)) {
						boolean2 = true;
						float5 = 0.05F;
					}
				} else {
					boolean2 = true;
				}

				if (this.sprite.getProperties().Is(IsoFlagType.halfheight)) {
					boolean2 = false;
				}

				if (boolean2) {
					if (boolean4 && this.sprite.getProperties().Is(IsoFlagType.HoppableN)) {
						float5 = 0.25F;
					}

					if (boolean3 && this.sprite.getProperties().Is(IsoFlagType.HoppableW)) {
						float5 = 0.25F;
					}

					if (this.bAlphaForced) {
						if (this.getTargetAlpha(int1) == 1.0F) {
							this.setAlphaAndTarget(int1, 0.99F);
						}
					} else {
						this.setTargetAlpha(int1, float5);
					}

					LowLightingQualityHack = true;
					this.NoPicking = this.rerouteMask == null && !(this instanceof IsoThumpable) && !IsoWindowFrame.isWindowFrame(this) && !this.sprite.getProperties().Is(IsoFlagType.doorN) && !this.sprite.getProperties().Is(IsoFlagType.doorW) && !this.sprite.getProperties().Is(IsoFlagType.HoppableN) && !this.sprite.getProperties().Is(IsoFlagType.HoppableW);
				} else {
					this.NoPicking = false;
				}
			} else {
				this.NoPicking = false;
			}
		}

		if (this == IsoCamera.CamCharacter) {
			this.setTargetAlpha(int1, 1.0F);
		}
	}

	protected float getAlphaUpdateRateDiv() {
		float float1 = 14.0F;
		return float1;
	}

	protected float getAlphaUpdateRateMul() {
		float float1 = 0.25F;
		if (this.square != null && this.square.room != null) {
			float1 *= 2.0F;
		}

		return float1;
	}

	protected boolean isUpdateAlphaEnabled() {
		return true;
	}

	protected boolean isUpdateAlphaDuringRender() {
		return true;
	}

	protected final void updateAlpha() {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					this.updateAlpha(int1);
				}
			}
		}
	}

	protected final void updateAlpha(int int1) {
		if (!GameServer.bServer) {
			float float1 = this.getAlphaUpdateRateMul();
			float float2 = this.getAlphaUpdateRateDiv();
			this.updateAlpha(int1, float1, float2);
		}
	}

	protected void updateAlpha(int int1, float float1, float float2) {
		if (this.isUpdateAlphaEnabled()) {
			if (!DebugOptions.instance.Character.Debug.UpdateAlpha.getValue()) {
				this.setAlphaToTarget(int1);
			} else {
				if (this.bNeverDoneAlpha) {
					this.setAlpha(0.0F);
					this.bNeverDoneAlpha = false;
				}

				if (DebugOptions.instance.Character.Debug.UpdateAlphaEighthSpeed.getValue()) {
					float1 /= 8.0F;
					float2 *= 8.0F;
				}

				float float3 = GameTime.getInstance().getMultiplier();
				float float4 = float3 * 0.28F;
				float float5 = this.getAlpha(int1);
				float float6 = this.targetAlpha[int1];
				if (float5 < float6) {
					float5 += float4 * float1;
					if (float5 > float6) {
						float5 = float6;
					}
				} else if (float5 > float6) {
					float5 -= float4 / float2;
					if (float5 < float6) {
						float5 = float6;
					}
				}

				this.setAlpha(int1, float5);
			}
		}
	}

	private void renderOverlaySprites(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.getOverlaySprite() != null && DebugOptions.instance.Terrain.RenderTiles.OverlaySprites.getValue()) {
			ColorInfo colorInfo2 = stCol2;
			colorInfo2.set(colorInfo);
			if (this.overlaySpriteColor != null) {
				colorInfo2.set(this.overlaySpriteColor);
			}

			if (colorInfo2.a != 1.0F && this.overlaySprite.def != null && this.overlaySprite.def.bCopyTargetAlpha) {
				int int1 = IsoPlayer.getPlayerIndex();
				float float4 = this.alpha[int1];
				float[] floatArray = this.alpha;
				floatArray[int1] *= colorInfo2.a;
				this.getOverlaySprite().render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colorInfo2, true);
				this.alpha[int1] = float4;
			} else {
				this.getOverlaySprite().render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colorInfo2, true);
			}
		}
	}

	private void renderAttachedSprites(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, Shader shader, Consumer consumer) {
		int int1;
		int int2;
		if (this.AttachedAnimSprite != null && DebugOptions.instance.Terrain.RenderTiles.AttachedAnimSprites.getValue()) {
			int1 = this.AttachedAnimSprite.size();
			for (int2 = 0; int2 < int1; ++int2) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
				if (!boolean1 || !spriteInstance.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
					float float4 = colorInfo.a;
					IndieGL.shaderSetValue(shader, "floorLayer", 1);
					colorInfo.a = spriteInstance.alpha;
					Object object = consumer;
					if (consumer == WallShaperW.instance) {
						if (spriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedN)) {
							Texture texture = spriteInstance.parentSprite.getTextureForCurrentFrame(this.dir);
							if (texture != null && texture.getWidth() < 32 * Core.TileScale) {
								continue;
							}
						}

						if (spriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedW)) {
							object = WallShaperWhole.instance;
						}
					} else if (consumer == WallShaperN.instance) {
						if (spriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedW)) {
							continue;
						}

						if (spriteInstance.parentSprite.getProperties().Is(IsoFlagType.attachedN)) {
							object = WallShaperWhole.instance;
						}
					}

					spriteInstance.parentSprite.render(spriteInstance, this, float1, float2, float3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colorInfo, true, (Consumer)object);
					colorInfo.a = float4;
					spriteInstance.update();
				}
			}
		}

		if (this.Children != null && DebugOptions.instance.Terrain.RenderTiles.AttachedChildren.getValue()) {
			int1 = this.Children.size();
			for (int2 = 0; int2 < int1; ++int2) {
				IsoObject object2 = (IsoObject)this.Children.get(int2);
				if (object2 instanceof IsoMovingObject) {
					IndieGL.shaderSetValue(shader, "floorLayer", 1);
					object2.render(((IsoMovingObject)object2).x, ((IsoMovingObject)object2).y, ((IsoMovingObject)object2).z, colorInfo, true, false, (Shader)null);
				}
			}
		}

		if (this.wallBloodSplats != null && DebugOptions.instance.Terrain.RenderTiles.AttachedWallBloodSplats.getValue()) {
			if (Core.OptionBloodDecals == 0) {
				return;
			}

			IndieGL.shaderSetValue(shader, "floorLayer", 0);
			for (int1 = 0; int1 < this.wallBloodSplats.size(); ++int1) {
				((IsoWallBloodSplat)this.wallBloodSplats.get(int1)).render(float1, float2, float3, colorInfo);
			}
		}
	}

	public boolean isSpriteInvisible() {
		return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.invisible);
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
						object.render(((IsoMovingObject)object).x, ((IsoMovingObject)object).y, ((IsoMovingObject)object).z, colFxMask, boolean1, false, (Shader)null);
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
				this.sprite.renderObjectPicker(this.sprite.def, this, this.dir);
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

	public ColorInfo getCustomColor() {
		return this.customColor;
	}

	public void setCustomColor(ColorInfo colorInfo) {
		this.customColor = colorInfo;
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
			this.load(byteBuffer, 194);
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
					IsoRegions.setPreviousFlags(this.square);
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

	protected boolean hasObjectAmbientEmitter() {
		IsoChunk chunk = this.getChunk();
		return chunk == null ? false : chunk.hasObjectAmbientEmitter(this);
	}

	protected void addObjectAmbientEmitter(ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
		IsoChunk chunk = this.getChunk();
		if (chunk != null) {
			chunk.addObjectAmbientEmitter(this, perObjectLogic);
		}
	}

	public void addToWorld() {
		ItemContainer itemContainer;
		for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
			itemContainer = this.getContainerByIndex(int1);
			itemContainer.addItemsToProcessItems();
		}

		if (!GameServer.bServer) {
			String string = null;
			itemContainer = this.getContainerByEitherType("fridge", "freezer");
			if (itemContainer != null && itemContainer.isPowered()) {
				this.addObjectAmbientEmitter((new ObjectAmbientEmitters.FridgeHumLogic()).init(this));
				string = "FridgeHum";
				IsoWorld.instance.getCell().addToProcessIsoObject(this);
			} else if (this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.waterPiped) && (float)this.getWaterAmount() > 0.0F && Rand.Next(15) == 0) {
				this.addObjectAmbientEmitter((new ObjectAmbientEmitters.WaterDripLogic()).init(this));
				string = "WaterDrip";
			} else if (this.sprite == null || this.sprite.getName() == null || !this.sprite.getName().startsWith("camping_01") || this.sprite.tileSheetIndex != 0 && this.sprite.tileSheetIndex != 3) {
				if (this instanceof IsoDoor) {
					if (((IsoDoor)this).isExterior()) {
						this.addObjectAmbientEmitter((new ObjectAmbientEmitters.DoorLogic()).init(this));
					}
				} else if (this instanceof IsoWindow) {
					if (((IsoWindow)this).isExterior()) {
						this.addObjectAmbientEmitter((new ObjectAmbientEmitters.WindowLogic()).init(this));
					}
				} else if (this instanceof IsoTree && Rand.Next(40) == 0) {
					this.addObjectAmbientEmitter((new ObjectAmbientEmitters.TreeAmbianceLogic()).init(this));
					string = "TreeAmbiance";
				}
			} else {
				this.addObjectAmbientEmitter((new ObjectAmbientEmitters.TentAmbianceLogic()).init(this));
				string = "TentAmbiance";
			}

			PropertyContainer propertyContainer = this.getProperties();
			if (propertyContainer != null && propertyContainer.Is("AmbientSound")) {
				this.addObjectAmbientEmitter((new ObjectAmbientEmitters.AmbientSoundLogic()).init(this));
				string = propertyContainer.Val("AmbientSound");
			}

			this.checkMoveWithWind();
		}
	}

	public void removeFromWorld() {
		IsoCell cell = this.getCell();
		cell.addToProcessIsoObjectRemove(this);
		cell.getStaticUpdaterObjectList().remove(this);
		for (int int1 = 0; int1 < this.getContainerCount(); ++int1) {
			ItemContainer itemContainer = this.getContainerByIndex(int1);
			itemContainer.removeItemsFromProcessItems();
		}

		if (this.emitter != null) {
			this.emitter.stopAll();
			this.emitter = null;
		}

		if (this.getChunk() != null) {
			this.getChunk().removeObjectAmbientEmitter(this);
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
					if (udpConnection.RelevantTo((float)this.square.x, (float)this.square.y)) {
						GameServer.SyncObjectChunkHashes(this.square.chunk, udpConnection);
					}
				}

				return;
			}

			for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection != null && this.square != null && udpConnection.RelevantTo((float)this.square.x, (float)this.square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.AddItemToMap.doPacket(byteBufferWriter);
					this.writeToRemoteBuffer(byteBufferWriter);
					PacketTypes.PacketType.AddItemToMap.send(udpConnection);
				}
			}
		}
	}

	public void transmitUpdatedSpriteToClients(UdpConnection udpConnection) {
		if (GameServer.bServer) {
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection2 != null && this.square != null && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) && udpConnection2.RelevantTo((float)this.square.x, (float)this.square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.UpdateItemSprite.doPacket(byteBufferWriter);
					byteBufferWriter.putInt(this.getSprite().ID);
					GameWindow.WriteStringUTF(byteBufferWriter.bb, this.spriteName);
					byteBufferWriter.putInt(this.getSquare().getX());
					byteBufferWriter.putInt(this.getSquare().getY());
					byteBufferWriter.putInt(this.getSquare().getZ());
					byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
					if (this.AttachedAnimSprite != null) {
						byteBufferWriter.putByte((byte)this.AttachedAnimSprite.size());
						for (int int2 = 0; int2 < this.AttachedAnimSprite.size(); ++int2) {
							IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
							byteBufferWriter.putInt(spriteInstance.parentSprite.ID);
						}
					} else {
						byteBufferWriter.putByte((byte)0);
					}

					PacketTypes.PacketType.UpdateItemSprite.send(udpConnection2);
				}
			}
		}
	}

	public void transmitUpdatedSpriteToClients() {
		this.transmitUpdatedSpriteToClients((UdpConnection)null);
	}

	public void transmitUpdatedSprite() {
		if (GameClient.bClient) {
			this.transmitUpdatedSpriteToServer();
		}

		if (GameServer.bServer) {
			this.transmitUpdatedSpriteToClients();
		}
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
					itemContainer.save(byteBuffer);
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
					itemContainer2.load(byteBuffer, 194);
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
					object.load(byteBuffer, 194);
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
				this.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, int1);
				if (this.sprite == null) {
					this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
					this.sprite.LoadFramesNoDirPageSimple(this.spriteName);
				}
			}
		} else if ("emptyTrash".equals(string)) {
			this.getContainer().clear();
			if (this.getOverlaySprite() != null) {
				ItemPickerJava.updateOverlaySprite(this);
			}
		}

		this.checkMoveWithWind();
	}

	public void transmitUpdatedSpriteToServer() {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.UpdateItemSprite.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(this.getSprite().ID);
			GameWindow.WriteStringUTF(byteBufferWriter.bb, this.spriteName);
			byteBufferWriter.putInt(this.getSquare().getX());
			byteBufferWriter.putInt(this.getSquare().getY());
			byteBufferWriter.putInt(this.getSquare().getZ());
			byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
			if (this.AttachedAnimSprite != null) {
				byteBufferWriter.putByte((byte)this.AttachedAnimSprite.size());
				for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
					IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
					byteBufferWriter.putInt(spriteInstance.parentSprite.ID);
				}
			} else {
				byteBufferWriter.putByte((byte)0);
			}

			PacketTypes.PacketType.UpdateItemSprite.send(GameClient.connection);
		}
	}

	public void transmitCompleteItemToServer() {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.AddItemToMap.doPacket(byteBufferWriter);
			this.writeToRemoteBuffer(byteBufferWriter);
			PacketTypes.PacketType.AddItemToMap.send(GameClient.connection);
		}
	}

	public void transmitModData() {
		if (this.square != null) {
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.ObjectModData.doPacket(byteBufferWriter);
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

				PacketTypes.PacketType.ObjectModData.send(GameClient.connection);
			} else if (GameServer.bServer) {
				GameServer.sendObjectModData(this);
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

	public void setOverlaySprite(String string) {
		this.setOverlaySprite(string, -1.0F, -1.0F, -1.0F, -1.0F, true);
	}

	public void setOverlaySprite(String string, boolean boolean1) {
		this.setOverlaySprite(string, -1.0F, -1.0F, -1.0F, -1.0F, boolean1);
	}

	public void setOverlaySpriteColor(float float1, float float2, float float3, float float4) {
		this.overlaySpriteColor = new ColorInfo(float1, float2, float3, float4);
	}

	public ColorInfo getOverlaySpriteColor() {
		return this.overlaySpriteColor;
	}

	public void setOverlaySprite(String string, float float1, float float2, float float3, float float4) {
		this.setOverlaySprite(string, float1, float2, float3, float4, true);
	}

	public boolean setOverlaySprite(String string, float float1, float float2, float float3, float float4, boolean boolean1) {
		if (StringUtils.isNullOrWhitespace(string)) {
			if (this.overlaySprite == null) {
				return false;
			}

			this.overlaySprite = null;
			string = "";
		} else {
			boolean boolean2;
			if (!(float1 > -1.0F)) {
				boolean2 = this.overlaySpriteColor == null;
			} else {
				boolean2 = this.overlaySpriteColor != null && this.overlaySpriteColor.r == float1 && this.overlaySpriteColor.g == float2 && this.overlaySpriteColor.b == float3 && this.overlaySpriteColor.a == float4;
			}

			if (this.overlaySprite != null && string.equals(this.overlaySprite.name) && boolean2) {
				return false;
			}

			this.overlaySprite = IsoSpriteManager.instance.getSprite(string);
			this.overlaySprite.name = string;
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
				PacketTypes.PacketType.UpdateOverlaySprite.doPacket(byteBufferWriter);
				GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
				byteBufferWriter.putInt(this.getSquare().getX());
				byteBufferWriter.putInt(this.getSquare().getY());
				byteBufferWriter.putInt(this.getSquare().getZ());
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				byteBufferWriter.putFloat(float4);
				byteBufferWriter.putInt(this.getSquare().getObjects().indexOf(this));
				PacketTypes.PacketType.UpdateOverlaySprite.send(GameClient.connection);
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
		this.highlightColor.set(colorInfo);
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
			if (itemContainer != null && itemContainer.isPowered()) {
				IsoWorld.instance.getCell().addToProcessIsoObject(this);
				if (this.getChunk() != null && !this.hasObjectAmbientEmitter()) {
					this.getChunk().addObjectAmbientEmitter(this, (new ObjectAmbientEmitters.FridgeHumLogic()).init(this));
				}
			}

			this.checkAmbientSound();
		}
	}

	public void checkAmbientSound() {
		PropertyContainer propertyContainer = this.getProperties();
		if (propertyContainer != null && propertyContainer.Is("AmbientSound") && this.getChunk() != null && !this.hasObjectAmbientEmitter()) {
			this.getChunk().addObjectAmbientEmitter(this, (new ObjectAmbientEmitters.AmbientSoundLogic()).init(this));
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
				if (this.sprite.getProperties().Is(IsoFlagType.container) && this.container == null) {
					this.container = new ItemContainer(this.sprite.getProperties().Val("container"), this.square, this);
					this.container.parent = this;
					this.OutlineOnMouseover = true;
					if (this.sprite.getProperties().Is("ContainerCapacity")) {
						this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
					}

					if (this.sprite.getProperties().Is("ContainerPosition")) {
						this.container.setContainerPosition(this.sprite.getProperties().Val("ContainerPosition"));
					}
				}

				if (this.getSprite().getProperties().Is("Freezer")) {
					ItemContainer itemContainer = new ItemContainer("freezer", this.square, this);
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

					if (this.sprite.getProperties().Is("FreezerPosition")) {
						itemContainer.setFreezerPosition(this.sprite.getProperties().Val("FreezerPosition"));
					}
				}
			}
		}
	}

	public boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return true;
	}

	public boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return true;
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

	public IsoObject getRenderEffectMaster() {
		return this;
	}

	public void setRenderEffect(RenderEffectType renderEffectType, boolean boolean1) {
		if (!GameServer.bServer) {
			IsoObject object = this.getRenderEffectMaster();
			if (object.objectRenderEffects == null || boolean1) {
				object.objectRenderEffects = ObjectRenderEffects.getNew(this, renderEffectType, boolean1);
			}
		}
	}

	public void removeRenderEffect(ObjectRenderEffects objectRenderEffects) {
		IsoObject object = this.getRenderEffectMaster();
		if (object.objectRenderEffects != null && object.objectRenderEffects == objectRenderEffects) {
			object.objectRenderEffects = null;
		}
	}

	public ObjectRenderEffects getObjectRenderEffectsToApply() {
		IsoObject object = this.getRenderEffectMaster();
		if (object.objectRenderEffects != null) {
			return object.objectRenderEffects;
		} else {
			return Core.getInstance().getOptionDoWindSpriteEffects() && object.windRenderEffects != null ? object.windRenderEffects : null;
		}
	}

	public void destroyFence(IsoDirections directions) {
		BrokenFences.getInstance().destroyFence(this, directions);
	}

	public void getSpriteGridObjects(ArrayList arrayList) {
		arrayList.clear();
		IsoSprite sprite = this.getSprite();
		if (sprite != null) {
			IsoSpriteGrid spriteGrid = sprite.getSpriteGrid();
			if (spriteGrid != null) {
				int int1 = spriteGrid.getSpriteGridPosX(sprite);
				int int2 = spriteGrid.getSpriteGridPosY(sprite);
				int int3 = this.getSquare().getX();
				int int4 = this.getSquare().getY();
				int int5 = this.getSquare().getZ();
				for (int int6 = int4 - int2; int6 < int4 - int2 + spriteGrid.getHeight(); ++int6) {
					for (int int7 = int3 - int1; int7 < int3 - int1 + spriteGrid.getWidth(); ++int7) {
						IsoGridSquare square = this.getCell().getGridSquare(int7, int6, int5);
						if (square != null) {
							for (int int8 = 0; int8 < square.getObjects().size(); ++int8) {
								IsoObject object = (IsoObject)square.getObjects().get(int8);
								if (object.getSprite() != null && object.getSprite().getSpriteGrid() == spriteGrid) {
									arrayList.add(object);
								}
							}
						}
					}
				}
			}
		}
	}

	public final int getOutlineHighlightCol() {
		return this.outlineHighlightCol[0];
	}

	public final void setOutlineHighlightCol(ColorInfo colorInfo) {
		if (colorInfo != null) {
			for (int int1 = 0; int1 < this.outlineHighlightCol.length; ++int1) {
				this.outlineHighlightCol[int1] = Color.colorToABGR(colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
			}
		}
	}

	public final int getOutlineHighlightCol(int int1) {
		return this.outlineHighlightCol[int1];
	}

	public final void setOutlineHighlightCol(int int1, ColorInfo colorInfo) {
		if (colorInfo != null) {
			this.outlineHighlightCol[int1] = Color.colorToABGR(colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
		}
	}

	public final void setOutlineHighlightCol(float float1, float float2, float float3, float float4) {
		for (int int1 = 0; int1 < this.outlineHighlightCol.length; ++int1) {
			this.outlineHighlightCol[int1] = Color.colorToABGR(float1, float2, float3, float4);
		}
	}

	public final void setOutlineHighlightCol(int int1, float float1, float float2, float float3, float float4) {
		this.outlineHighlightCol[int1] = Color.colorToABGR(float1, float2, float3, float4);
	}

	public final boolean isOutlineHighlight() {
		return this.isOutlineHighlight != 0;
	}

	public final boolean isOutlineHighlight(int int1) {
		return (this.isOutlineHighlight & 1 << int1) != 0;
	}

	public final void setOutlineHighlight(boolean boolean1) {
		this.isOutlineHighlight = (byte)(boolean1 ? -1 : 0);
	}

	public final void setOutlineHighlight(int int1, boolean boolean1) {
		if (boolean1) {
			this.isOutlineHighlight = (byte)(this.isOutlineHighlight | 1 << int1);
		} else {
			this.isOutlineHighlight = (byte)(this.isOutlineHighlight & ~(1 << int1));
		}
	}

	public final boolean isOutlineHlAttached() {
		return this.isOutlineHlAttached != 0;
	}

	public final boolean isOutlineHlAttached(int int1) {
		return (this.isOutlineHlAttached & 1 << int1) != 0;
	}

	public void setOutlineHlAttached(boolean boolean1) {
		this.isOutlineHlAttached = (byte)(boolean1 ? -1 : 0);
	}

	public final void setOutlineHlAttached(int int1, boolean boolean1) {
		if (boolean1) {
			this.isOutlineHlAttached = (byte)(this.isOutlineHlAttached | 1 << int1);
		} else {
			this.isOutlineHlAttached = (byte)(this.isOutlineHlAttached & ~(1 << int1));
		}
	}

	public boolean isOutlineHlBlink() {
		return this.isOutlineHlBlink != 0;
	}

	public final boolean isOutlineHlBlink(int int1) {
		return (this.isOutlineHlBlink & 1 << int1) != 0;
	}

	public void setOutlineHlBlink(boolean boolean1) {
		this.isOutlineHlBlink = (byte)(boolean1 ? -1 : 0);
	}

	public final void setOutlineHlBlink(int int1, boolean boolean1) {
		if (boolean1) {
			this.isOutlineHlBlink = (byte)(this.isOutlineHlBlink | 1 << int1);
		} else {
			this.isOutlineHlBlink = (byte)(this.isOutlineHlBlink & ~(1 << int1));
		}
	}

	public void unsetOutlineHighlight() {
		this.isOutlineHighlight = 0;
		this.isOutlineHlBlink = 0;
		this.isOutlineHlAttached = 0;
	}

	public float getOutlineThickness() {
		return this.outlineThickness;
	}

	public void setOutlineThickness(float float1) {
		this.outlineThickness = float1;
	}

	protected void addItemsFromProperties() {
		PropertyContainer propertyContainer = this.getProperties();
		if (propertyContainer != null) {
			String string = propertyContainer.Val("Material");
			String string2 = propertyContainer.Val("Material2");
			String string3 = propertyContainer.Val("Material3");
			if ("Wood".equals(string) || "Wood".equals(string2) || "Wood".equals(string3)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.UnusableWood"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
				if (Rand.NextBool(5)) {
					this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.UnusableWood"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
				}
			}

			if (("MetalBars".equals(string) || "MetalBars".equals(string2) || "MetalBars".equals(string3)) && Rand.NextBool(2)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalBar"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("MetalPlates".equals(string) || "MetalPlates".equals(string2) || "MetalPlates".equals(string3)) && Rand.NextBool(2)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.SheetMetal"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("MetalPipe".equals(string) || "MetalPipe".equals(string2) || "MetalPipe".equals(string3)) && Rand.NextBool(2)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalPipe"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("MetalWire".equals(string) || "MetalWire".equals(string2) || "MetalWire".equals(string3)) && Rand.NextBool(3)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Wire"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("Nails".equals(string) || "Nails".equals(string2) || "Nails".equals(string3)) && Rand.NextBool(2)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Nails"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}

			if (("Screws".equals(string) || "Screws".equals(string2) || "Screws".equals(string3)) && Rand.NextBool(2)) {
				this.square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Screws"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
			}
		}
	}

	public boolean isDestroyed() {
		return this.Damage <= 0;
	}

	public void Thump(IsoMovingObject movingObject) {
		IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
		if (gameCharacter != null) {
			Thumpable thumpable = this.getThumpableFor(gameCharacter);
			if (thumpable == null) {
				return;
			}

			if (thumpable != this) {
				thumpable.Thump(movingObject);
				return;
			}
		}

		boolean boolean1 = BrokenFences.getInstance().isBreakableObject(this);
		byte byte1 = 8;
		int int1;
		if (movingObject instanceof IsoZombie) {
			int int2 = movingObject.getCurrentSquare().getMovingObjects().size();
			if (movingObject.getCurrentSquare().getW() != null) {
				int2 += movingObject.getCurrentSquare().getW().getMovingObjects().size();
			}

			if (movingObject.getCurrentSquare().getE() != null) {
				int2 += movingObject.getCurrentSquare().getE().getMovingObjects().size();
			}

			if (movingObject.getCurrentSquare().getS() != null) {
				int2 += movingObject.getCurrentSquare().getS().getMovingObjects().size();
			}

			if (movingObject.getCurrentSquare().getN() != null) {
				int2 += movingObject.getCurrentSquare().getN().getMovingObjects().size();
			}

			if (int2 >= byte1) {
				int1 = 1 * ThumpState.getFastForwardDamageMultiplier();
				this.Damage = (short)(this.Damage - int1);
			} else {
				this.partialThumpDmg += (float)int2 / (float)byte1 * (float)ThumpState.getFastForwardDamageMultiplier();
				if ((int)this.partialThumpDmg > 0) {
					int1 = (int)this.partialThumpDmg;
					this.Damage = (short)(this.Damage - int1);
					this.partialThumpDmg -= (float)int1;
				}
			}

			WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		}

		if (this.Damage <= 0) {
			String string = "BreakObject";
			if (gameCharacter != null) {
				gameCharacter.getEmitter().playSound(string, this);
			}

			if (GameServer.bServer) {
				GameServer.PlayWorldSoundServer(string, false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
			}

			WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
			movingObject.setThumpTarget((Thumpable)null);
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

			ArrayList arrayList = new ArrayList();
			for (int1 = 0; int1 < this.getContainerCount(); ++int1) {
				ItemContainer itemContainer = this.getContainerByIndex(int1);
				arrayList.clear();
				arrayList.addAll(itemContainer.getItems());
				itemContainer.removeItemsFromProcessItems();
				itemContainer.removeAllItems();
				for (int int3 = 0; int3 < arrayList.size(); ++int3) {
					this.getSquare().AddWorldInventoryItem((InventoryItem)arrayList.get(int3), 0.0F, 0.0F, 0.0F);
				}
			}

			this.square.transmitRemoveItemFromSquare(this);
		}
	}

	public void setMovedThumpable(boolean boolean1) {
		this.bMovedThumpable = boolean1;
	}

	public boolean isMovedThumpable() {
		return this.bMovedThumpable;
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
	}

	public Thumpable getThumpableFor(IsoGameCharacter gameCharacter) {
		if (this.isDestroyed()) {
			return null;
		} else if (this.isMovedThumpable()) {
			return this;
		} else if (!BrokenFences.getInstance().isBreakableObject(this)) {
			return null;
		} else {
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
			return zombie != null && zombie.isCrawling() ? this : null;
		}
	}

	public boolean isExistInTheWorld() {
		return this.square.getMovingObjects().contains(this);
	}

	public float getThumpCondition() {
		return (float)PZMath.clamp(this.getDamage(), 0, 100) / 100.0F;
	}

	static  {
		initFactory();
	}

	public static class IsoObjectFactory {
		private final byte classID;
		private final String objectName;
		private final int hashCode;

		public IsoObjectFactory(byte byte1, String string) {
			this.classID = byte1;
			this.objectName = string;
			this.hashCode = string.hashCode();
		}

		protected IsoObject InstantiateObject(IsoCell cell) {
			return new IsoObject(cell);
		}

		public byte getClassID() {
			return this.classID;
		}

		public String getObjectName() {
			return this.objectName;
		}
	}

	public static class OutlineShader {
		public static final IsoObject.OutlineShader instance = new IsoObject.OutlineShader();
		private ShaderProgram shaderProgram;
		private int stepSize;
		private int outlineColor;

		public void initShader() {
			this.shaderProgram = ShaderProgram.createShaderProgram("outline", false, true);
			if (this.shaderProgram.isCompiled()) {
				this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "stepSize");
				this.outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "outlineColor");
				ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
				ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001F, 0.001F);
				ARBShaderObjects.glUseProgramObjectARB(0);
			}
		}

		public void setOutlineColor(float float1, float float2, float float3, float float4) {
			SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.outlineColor, float1, float2, float3, float4);
		}

		public void setStepSize(float float1, int int1, int int2) {
			SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.stepSize, float1 / (float)int1, float1 / (float)int2);
		}

		public boolean StartShader() {
			if (this.shaderProgram == null) {
				RenderThread.invokeOnRenderContext(this::initShader);
			}

			if (this.shaderProgram.isCompiled()) {
				IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
				return true;
			} else {
				return false;
			}
		}
	}

	public static enum VisionResult {

		NoEffect,
		Blocked,
		Unblocked;

		private static IsoObject.VisionResult[] $values() {
			return new IsoObject.VisionResult[]{NoEffect, Blocked, Unblocked};
		}
	}
}
