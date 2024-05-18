package zombie.vehicles;

import fmod.fmod.FMODSoundEmitter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.JustDieState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.Bullet;
import zombie.core.physics.CarController;
import zombie.core.physics.Transform;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.utils.OnceEvery;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PassengerMap;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.TextManager;


public class BaseVehicle extends IsoMovingObject {
	public static final float RADIUS = 0.3F;
	public static final int FADE_DISTANCE = 15;
	public static final int RANDOMIZE_CONTAINER_CHANCE = 100;
	protected BaseVehicle.Passenger[] passengers = new BaseVehicle.Passenger[1];
	protected String scriptName;
	protected VehicleScript script;
	protected ArrayList parts = new ArrayList();
	public ArrayList models = new ArrayList();
	protected VehiclePart battery;
	protected int engineQuality;
	protected int engineLoudness;
	protected int enginePower;
	protected long engineCheckTime;
	protected ArrayList lights = new ArrayList();
	protected boolean createdModel = false;
	protected Vector3f lastLinearVelocity = new Vector3f();
	public IsoChunk chunk;
	protected int skinIndex = -1;
	public static boolean LEMMY_FLIP_FIX = false;
	protected CarController physics;
	protected boolean bCreated;
	protected PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
	protected PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
	public boolean polyDirty = true;
	private float polyPlusRadiusMinX = -123.0F;
	private float polyPlusRadiusMinY;
	private float polyPlusRadiusMaxX;
	private float polyPlusRadiusMaxY;
	private float maxSpeed;
	private boolean keyIsOnDoor = false;
	private boolean hotwired = false;
	private boolean hotwiredBroken = false;
	private boolean keysInIgnition = false;
	private long soundHorn = -1L;
	private long soundBackMoveSignal = -1L;
	private long soundSirenSignal = -1L;
	private HashMap choosenParts = new HashMap();
	public short VehicleID = -1;
	public boolean serverRemovedFromWorld = false;
	public int sendPacketNum = 0;
	public boolean trace = false;
	private String type = "";
	public VehicleInterpolation interpolation = null;
	public static Texture vehicleShadow = null;
	private String respawnZone;
	UpdateLimit limitPhysicSend = new UpdateLimit(100L);
	UpdateLimit limitPhysicValid = new UpdateLimit(1000L);
	public boolean waitFullUpdate;
	private float mass = 0.0F;
	private float initialMass = 0.0F;
	private float brakingForce = 0.0F;
	private float baseQuality = 0.0F;
	private float currentSteering = 0.0F;
	private boolean isBraking = false;
	private int mechanicalID = 0;
	private boolean needPartsUpdate = false;
	private boolean alarmed = false;
	private int alarmTime = -1;
	private float alarmAccumulator;
	private double sirenStartTime = 0.0;
	private boolean mechanicUIOpen = false;
	protected boolean bDoDamageOverlay = false;
	private boolean isGoodCar = false;
	private InventoryItem currentKey = null;
	private boolean doColor = true;
	public float throttle = 0.0F;
	private final BaseVehicle.VehicleImpulse impulseFromServer = new BaseVehicle.VehicleImpulse();
	private final BaseVehicle.VehicleImpulse[] impulseFromSquishedZombie = new BaseVehicle.VehicleImpulse[4];
	private final ArrayList impulseFromHitZombie = new ArrayList();
	public double engineSpeed;
	public TransmissionNumber transmissionNumber;
	public UpdateLimit transmissionChangeTime = new UpdateLimit(1000L);
	private ArrayList brekingObjectsList;
	private UpdateLimit limitUpdate;
	private byte keySpawned = 0;
	private static final Vector3f UNIT_X = new Vector3f(1.0F, 0.0F, 0.0F);
	private static final Vector3f UNIT_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	private static final Vector3f UNIT_Z = new Vector3f(0.0F, 0.0F, 1.0F);
	private static final Vector3f _UNIT_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	public boolean hasExtendOffset = true;
	public boolean hasExtendOffsetExiting = false;
	public Quaternionf savedRot = new Quaternionf();
	protected boolean loaded = false;
	private Matrix4f tempMatrix4f = new Matrix4f();
	private Matrix4f tempMatrix4fLWJGL_1 = new Matrix4f();
	public Transform jniTransform = new Transform();
	public float jniSpeed;
	public boolean jniIsCollide;
	public Vector3f jniLinearVelocity = new Vector3f();
	public Vector3f netLinearVelocity = new Vector3f();
	public static final byte authorizationOnServer = 0;
	public static final byte authorizationSimulation = 1;
	public static final byte authorizationServerSimulation = 2;
	public static final byte authorizationOwner = 3;
	public static final byte authorizationServerOwner = 4;
	public byte netPlayerAuthorization = 0;
	public int netPlayerId = 0;
	public int netPlayerTimeout = 0;
	private final int netPlayerTimeoutMax = 30;
	public int authSimulationHash = 0;
	public long authSimulationTime = 0L;
	private Quaternionf tempQuat4f = new Quaternionf();
	private Matrix4f renderTransform = new Matrix4f();
	public int frontEndDurability = 100;
	public int rearEndDurability = 100;
	public float rust = 0.0F;
	public float colorHue = 0.0F;
	public float colorSaturation = 0.0F;
	public float colorValue = 0.0F;
	public int currentFrontEndDurability = 100;
	public int currentRearEndDurability = 100;
	private Transform tempTransform = new Transform();
	private Transform tempTransform2 = new Transform();
	public float collideX = -1.0F;
	public float collideY = -1.0F;
	private static final Vector2[] testVecs1 = new Vector2[4];
	private static final Vector2[] testVecs2 = new Vector2[4];
	public Vector3f tempVector3f_1 = new Vector3f();
	public Vector3f tempVector3f_2 = new Vector3f();
	private Vector2 tempVector2 = new Vector2();
	private Transform tempTransform3 = new Transform();
	private Vector3f tempVector3f_3 = new Vector3f();
	private static final PolygonalMap2.VehiclePoly tempPoly = new PolygonalMap2.VehiclePoly();
	protected static ColorInfo inf = new ColorInfo();
	public PolygonalMap2.VehiclePoly shadowCoord = new PolygonalMap2.VehiclePoly();
	private static final boolean YURI_FORCE_FIELD = true;
	private final Vector4f tempVector4f = new Vector4f();
	public BaseVehicle.engineStateTypes engineState;
	public long engineLastUpdateStateTime;
	public BaseVehicle.WheelInfo[] wheelInfo;
	public boolean skidding;
	public long skidSound;
	public long ramSound;
	public long ramSoundTime;
	public long[] engineSound;
	public long[] new_EngineSoundId;
	public static BaseVehicle.EngineRPMData[] GenericCarEngineData = new BaseVehicle.EngineRPMData[]{new BaseVehicle.EngineRPMData("GenericVehicleEngine1", 100.0F, 0.5F, 0.3F, 1000.0F, 1.0F, 0.6F, 1300.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("GenericVehicleEngine2", 800.0F, 0.7F, 0.0F, 3000.0F, 1.1F, 1.8F, 7000.0F, 1.4F, 0.0F), new BaseVehicle.EngineRPMData("GenericVehicleEngine3", 100.0F, 0.7F, 0.0F, 140.0F, 1.0F, 1.0F, 200.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("GenericVehicleEngine4", 3000.0F, 0.7F, 0.0F, 5000.0F, 1.0F, 0.8F, 10000.0F, 1.5F, 1.2F)};
	public static BaseVehicle.EngineRPMData[] JeepEngineData = new BaseVehicle.EngineRPMData[]{new BaseVehicle.EngineRPMData("JeepVehicleEngine1", 100.0F, 0.5F, 0.3F, 1000.0F, 1.0F, 0.6F, 1300.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("JeepVehicleEngine2", 800.0F, 0.7F, 0.0F, 3000.0F, 1.1F, 1.8F, 7000.0F, 1.4F, 0.0F), new BaseVehicle.EngineRPMData("JeepVehicleEngine3", 100.0F, 0.7F, 0.0F, 140.0F, 1.0F, 1.0F, 200.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("JeepVehicleEngine4", 3000.0F, 0.7F, 0.0F, 5000.0F, 1.0F, 0.8F, 10000.0F, 1.5F, 1.2F)};
	public static BaseVehicle.EngineRPMData[] FirebirdEngineData = new BaseVehicle.EngineRPMData[]{new BaseVehicle.EngineRPMData("FirebirdVehicleEngine1", 100.0F, 0.5F, 0.3F, 1000.0F, 1.0F, 0.6F, 1300.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("FirebirdVehicleEngine2", 800.0F, 0.7F, 0.0F, 3000.0F, 1.1F, 1.8F, 7000.0F, 1.4F, 0.0F), new BaseVehicle.EngineRPMData("FirebirdVehicleEngine3", 3000.0F, 0.7F, 0.0F, 5000.0F, 1.0F, 1.0F, 9000.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("FirebirdVehicleEngine4", 100.0F, 0.7F, 0.0F, 140.0F, 1.0F, 1.0F, 200.0F, 1.2F, 0.0F)};
	public static BaseVehicle.EngineRPMData[] VanEngineData = new BaseVehicle.EngineRPMData[]{new BaseVehicle.EngineRPMData("VanVehicleEngine1", 100.0F, 0.5F, 0.7F, 3000.0F, 1.0F, 0.6F, 4300.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("VanVehicleEngine2", 2000.0F, 0.7F, 0.0F, 5000.0F, 1.1F, 1.8F, 10000.0F, 1.4F, 1.0F), new BaseVehicle.EngineRPMData("van_engine2_unused", 100.0F, 0.7F, 0.0F, 140.0F, 1.0F, 1.0F, 200.0F, 1.2F, 0.0F), new BaseVehicle.EngineRPMData("van_engine2_unused", 100.0F, 0.7F, 0.0F, 140.0F, 1.0F, 1.0F, 200.0F, 1.2F, 0.0F)};
	public int engineSoundIndex;
	private BaseSoundEmitter emitter;
	public BaseSoundEmitter hornemitter;
	public float startTime;
	public static ArrayList RPMList = new ArrayList();
	private static OnceEvery updateTimer = new OnceEvery(1.0F);
	private float brakeBetweenUpdatesSpeed;
	public boolean headlightsOn;
	public boolean stoplightsOn;
	public boolean soundHornOn;
	public boolean soundBackMoveOn;
	public LightbarLightsMode lightbarLightsMode;
	public LightbarSirenMode lightbarSirenMode;
	boolean addedToWorld;
	boolean removedFromWorld;
	private static HashMap luaFunctionMap = new HashMap();
	protected short updateFlags;
	protected long updateLockTimeout;
	public BaseVehicle.ServerVehicleState[] connectionState;
	private static final float[] vehicleParams = new float[24];

	public boolean isLocalPhysicSim() {
		if (GameServer.bServer) {
			return this.netPlayerAuthorization == 0;
		} else {
			return this.netPlayerAuthorization == 1 || this.netPlayerAuthorization == 3;
		}
	}

	public void addImpulse(Vector3f vector3f, Vector3f vector3f2) {
		if (!this.impulseFromServer.enable) {
			this.impulseFromServer.enable = true;
			this.impulseFromServer.impulse.set((Vector3fc)vector3f);
			this.impulseFromServer.rel_pos.set((Vector3fc)vector3f2);
		} else if (this.impulseFromServer.impulse.length() < vector3f.length()) {
			this.impulseFromServer.impulse.set((Vector3fc)vector3f);
			this.impulseFromServer.rel_pos.set((Vector3fc)vector3f2);
		}
	}

	public double getEngineSpeed() {
		return this.engineSpeed;
	}

	public String getTransmissionNumberLetter() {
		return this.transmissionNumber.getString();
	}

	public int getTransmissionNumber() {
		return this.transmissionNumber.getIndex();
	}

	public void setClientForce(float float1) {
		this.physics.clientForce = float1;
	}

	public BaseVehicle(IsoCell cell) {
		super(cell, false);
		this.engineState = BaseVehicle.engineStateTypes.Idle;
		this.wheelInfo = new BaseVehicle.WheelInfo[4];
		this.skidding = false;
		this.engineSound = new long[4];
		this.new_EngineSoundId = new long[4];
		this.engineSoundIndex = 0;
		this.hornemitter = null;
		this.startTime = 0.0F;
		this.brakeBetweenUpdatesSpeed = 0.0F;
		this.headlightsOn = false;
		this.stoplightsOn = false;
		this.soundHornOn = false;
		this.soundBackMoveOn = false;
		this.lightbarLightsMode = new LightbarLightsMode();
		this.lightbarSirenMode = new LightbarSirenMode();
		this.addedToWorld = false;
		this.removedFromWorld = false;
		this.updateLockTimeout = 0L;
		this.connectionState = new BaseVehicle.ServerVehicleState[512];
		this.setCollidable(false);
		this.respawnZone = new String("");
		this.scriptName = "Base.PickUpTruck";
		this.passengers[0] = new BaseVehicle.Passenger();
		this.waitFullUpdate = false;
		this.savedRot.w = 1.0F;
		for (int int1 = 0; int1 < this.wheelInfo.length; ++int1) {
			this.wheelInfo[int1] = new BaseVehicle.WheelInfo();
		}

		if (GameClient.bClient) {
			this.interpolation = new VehicleInterpolation(VehicleManager.physicsDelay);
		}

		this.setKeyId(Rand.Next(100000000));
		this.engineSpeed = 0.0;
		this.transmissionNumber = TransmissionNumber.N;
		this.rust = (float)Rand.Next(0, 2);
		if (vehicleShadow == null) {
			vehicleShadow = Texture.getSharedTexture("media/vehicleShadow.png");
		}

		this.brekingObjectsList = new ArrayList();
		this.jniIsCollide = false;
		this.limitUpdate = new UpdateLimit(333L);
	}

	public static void LoadAllVehicleTextures() {
		RenderThread.borrowContext();
		try {
			ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				LoadVehicleTextures((VehicleScript)arrayList.get(int1));
			}
		} finally {
			RenderThread.returnContext();
		}
	}

	public static void LoadVehicleTextures(VehicleScript vehicleScript) {
		if (vehicleScript.getSkinCount() > 0) {
			vehicleScript.textureDataSkins = new Texture[vehicleScript.getSkinCount()];
			for (int int1 = 0; int1 < vehicleScript.getSkinCount(); ++int1) {
				vehicleScript.textureDataSkins[int1] = Texture.getSharedTexture("media/textures/" + vehicleScript.getSkin(int1).texture + ".png");
			}
		}

		if (vehicleScript.textureMask != null) {
			boolean boolean1 = TextureID.bUseCompression;
			TextureID.bUseCompression = false;
			vehicleScript.textureDataMask = Texture.getSharedTexture("media/textures/" + vehicleScript.textureMask + ".png");
			TextureID.bUseCompression = boolean1;
		}

		vehicleScript.textureDataDamage1Overlay = Texture.getSharedTexture("media/textures/" + vehicleScript.textureDamage1Overlay + ".png");
		vehicleScript.textureDataDamage1Shell = Texture.getSharedTexture("media/textures/" + vehicleScript.textureDamage1Shell + ".png");
		vehicleScript.textureDataDamage2Overlay = Texture.getSharedTexture("media/textures/" + vehicleScript.textureDamage2Overlay + ".png");
		vehicleScript.textureDataDamage2Shell = Texture.getSharedTexture("media/textures/" + vehicleScript.textureDamage2Shell + ".png");
		vehicleScript.textureDataLights = Texture.getSharedTexture("media/textures/" + vehicleScript.textureLights + ".png");
		vehicleScript.textureDataRust = Texture.getSharedTexture("media/textures/" + vehicleScript.textureRust + ".png");
	}

	private void doVehicleColor() {
		if (!this.isDoColor()) {
			this.colorSaturation = 0.1F;
			this.colorValue = 0.9F;
		} else {
			this.colorHue = Rand.Next(0.0F, 0.0F);
			this.colorSaturation = 0.5F;
			this.colorValue = Rand.Next(0.3F, 0.6F);
			int int1 = Rand.Next(100);
			if (int1 < 20) {
				this.colorHue = Rand.Next(0.0F, 0.03F);
				this.colorSaturation = Rand.Next(0.85F, 1.0F);
				this.colorValue = Rand.Next(0.55F, 0.85F);
			} else if (int1 < 32) {
				this.colorHue = Rand.Next(0.55F, 0.61F);
				this.colorSaturation = Rand.Next(0.85F, 1.0F);
				this.colorValue = Rand.Next(0.65F, 0.75F);
			} else if (int1 < 67) {
				this.colorHue = Rand.Next(0.0F, 1.0F);
				this.colorSaturation = Rand.Next(0.0F, 0.1F);
				this.colorValue = Rand.Next(0.7F, 0.8F);
			} else if (int1 < 89) {
				this.colorHue = Rand.Next(0.0F, 1.0F);
				this.colorSaturation = Rand.Next(0.0F, 0.1F);
				this.colorValue = Rand.Next(0.1F, 0.25F);
			} else {
				this.colorHue = Rand.Next(0.0F, 1.0F);
				this.colorSaturation = Rand.Next(0.6F, 0.75F);
				this.colorValue = Rand.Next(0.3F, 0.7F);
			}

			if (this.getScript() != null) {
				if (this.getScript().getForcedHue() > -1.0F) {
					this.colorHue = this.getScript().getForcedHue();
				}

				if (this.getScript().getForcedSat() > -1.0F) {
					this.colorSaturation = this.getScript().getForcedSat();
				}

				if (this.getScript().getForcedVal() > -1.0F) {
					this.colorValue = this.getScript().getForcedVal();
				}
			}
		}
	}

	public String getObjectName() {
		return "Vehicle";
	}

	public boolean Serialize() {
		return true;
	}

	public void createPhysics() {
		if (!GameClient.bClient && this.VehicleID == -1) {
			this.VehicleID = VehicleIDMap.instance.allocateID();
			if (GameServer.bServer) {
				VehicleManager.instance.registerVehicle(this);
			} else {
				VehicleIDMap.instance.put(this.VehicleID, this);
			}
		}

		if (this.script == null) {
			this.setScript(this.scriptName);
		}

		if (this.script != null) {
			if (this.skinIndex == -1) {
				this.setSkinIndex(Rand.Next(this.getSkinCount()));
			}

			WorldSimulation.instance.create();
			this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, this.getZ(), this.getY() - WorldSimulation.instance.offsetY);
			this.physics = new CarController(this);
			if (!this.bCreated) {
				this.bCreated = true;
				byte byte1 = 30;
				if (SandboxOptions.getInstance().RecentlySurvivorVehicles.getValue() == 1) {
					byte1 = 10;
				}

				if (SandboxOptions.getInstance().RecentlySurvivorVehicles.getValue() == 3) {
					byte1 = 50;
				}

				if (Rand.Next(100) < byte1) {
					this.setGoodCar(true);
				}
			}

			this.createParts();
			this.initParts();
			if (!this.createdModel) {
				ModelManager.instance.Add(this);
				this.createdModel = true;
			}

			this.updateTransform();
			this.lights.clear();
			VehiclePart vehiclePart;
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getLight() != null) {
					this.lights.add(vehiclePart);
				}
			}

			this.setMaxSpeed(this.getScript().maxSpeed);
			this.setInitialMass(this.getScript().getMass());
			if (!this.getCell().getVehicles().contains(this) && !this.getCell().addVehicles.contains(this)) {
				this.getCell().addVehicles.add(this);
			}

			this.square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
			Iterator iterator = this.parts.iterator();
			while (iterator.hasNext()) {
				vehiclePart = (VehiclePart)iterator.next();
				if (vehiclePart.getItemContainer() != null && !vehiclePart.getItemContainer().bExplored) {
					if (Rand.Next(100) <= 100) {
						this.randomizeContainer(vehiclePart);
					}

					vehiclePart.getItemContainer().setExplored(true);
				}
			}

			if (this.engineState == BaseVehicle.engineStateTypes.Running) {
				this.engineDoRunning();
			}

			this.updateTotalMass();
			this.bDoDamageOverlay = true;
			this.updatePartStats();
			this.mechanicalID = Rand.Next(100000);
		}
	}

	public int getKeyId() {
		return this.keyId;
	}

	public boolean getKeySpawned() {
		return this.keySpawned != 0;
	}

	public void putKeyToZombie(IsoZombie zombie) {
		InventoryItem inventoryItem = this.createVehicleKey();
		zombie.getInventory().AddItem(inventoryItem);
	}

	public void putKeyToContainer(ItemContainer itemContainer, IsoGridSquare square, IsoObject object) {
		InventoryItem inventoryItem = this.createVehicleKey();
		itemContainer.AddItem(inventoryItem);
		if (GameServer.bServer) {
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				if (udpConnection.ReleventTo((float)object.square.x, (float)object.square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)20, byteBufferWriter);
					byteBufferWriter.putShort((short)2);
					byteBufferWriter.putInt((int)object.getX());
					byteBufferWriter.putInt((int)object.getY());
					byteBufferWriter.putInt((int)object.getZ());
					int int2 = square.getObjects().indexOf(object);
					byteBufferWriter.putByte((byte)int2);
					byteBufferWriter.putByte((byte)object.getContainerIndex(itemContainer));
					try {
						CompressIdenticalItems.save(byteBufferWriter.bb, inventoryItem);
					} catch (Exception exception) {
						exception.printStackTrace();
					}

					udpConnection.endPacketUnordered();
				}
			}
		}
	}

	public void putKeyToWorld(IsoGridSquare square) {
		InventoryItem inventoryItem = this.createVehicleKey();
		square.AddWorldInventoryItem(inventoryItem, 0.0F, 0.0F, 0.0F);
	}

	public void addKeyToWorld() {
		if (this.haveOneDoorUnlocked() && Rand.Next(100) < 30) {
			if (Rand.Next(5) == 0) {
				this.keyIsOnDoor = true;
				this.currentKey = this.createVehicleKey();
			} else {
				this.addKeyToGloveBox();
			}
		} else if (this.haveOneDoorUnlocked() && Rand.Next(100) < 30) {
			this.keysInIgnition = true;
			this.currentKey = this.createVehicleKey();
		} else {
			if (Rand.Next(100) < 50) {
				IsoGridSquare square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
				if (square != null) {
					this.addKeyToSquare(square);
					return;
				}
			}
		}
	}

	public void addKeyToGloveBox() {
		if (this.keySpawned == 0) {
			if (this.getPartById("GloveBox") != null) {
				VehiclePart vehiclePart = this.getPartById("GloveBox");
				InventoryItem inventoryItem = this.createVehicleKey();
				vehiclePart.container.addItem(inventoryItem);
				this.keySpawned = 1;
			}
		}
	}

	public InventoryItem createVehicleKey() {
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem("CarKey");
		inventoryItem.setKeyId(this.getKeyId());
		inventoryItem.setName(Translator.getText("IGUI_CarKey", Translator.getText("IGUI_VehicleName" + this.getScript().getName())));
		Integer[] integerArray = Color.HSBtoRGB(this.colorHue, this.colorSaturation * 0.5F, this.colorValue);
		inventoryItem.setColor(new Color(integerArray[0], integerArray[1], integerArray[2]));
		inventoryItem.setCustomColor(true);
		return inventoryItem;
	}

	public boolean addKeyToSquare(IsoGridSquare square) {
		boolean boolean1 = false;
		IsoGridSquare square2 = null;
		int int1;
		int int2;
		for (int1 = 0; int1 < 3; ++int1) {
			for (int2 = square.getX() - 10; int2 < square.getX() + 10; ++int2) {
				for (int int3 = square.getY() - 10; int3 < square.getY() + 10; ++int3) {
					square2 = IsoWorld.instance.getCell().getGridSquare(int2, int3, int1);
					if (square2 != null) {
						int int4;
						for (int4 = 0; int4 < square2.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square2.getObjects().get(int4);
							if (object.container != null && (object.container.type.equals("counter") || object.container.type.equals("officedrawers") || object.container.type.equals("shelves") || object.container.type.equals("desk"))) {
								this.putKeyToContainer(object.container, square2, object);
								boolean1 = true;
								break;
							}
						}

						for (int4 = 0; int4 < square2.getMovingObjects().size(); ++int4) {
							if (square2.getMovingObjects().get(int4) instanceof IsoZombie) {
								((IsoZombie)square2.getMovingObjects().get(int4)).addItemToSpawnAtDeath(this.createVehicleKey());
								boolean1 = true;
								break;
							}
						}
					}

					if (boolean1) {
						break;
					}
				}

				if (boolean1) {
					break;
				}
			}

			if (boolean1) {
				break;
			}
		}

		if (Rand.Next(10) < 6) {
			while (!boolean1) {
				int1 = square.getX() - 10 + Rand.Next(20);
				int2 = square.getY() - 10 + Rand.Next(20);
				square2 = IsoWorld.instance.getCell().getGridSquare((double)int1, (double)int2, (double)this.z);
				if (square2 != null && !square2.isSolid() && !square2.isSolidTrans() && !square2.HasTree()) {
					this.putKeyToWorld(square2);
					boolean1 = true;
					break;
				}
			}
		}

		return boolean1;
	}

	public void toggleLockedDoor(VehiclePart vehiclePart, IsoGameCharacter gameCharacter, boolean boolean1) {
		if (boolean1) {
			if (!this.canLockDoor(vehiclePart, gameCharacter)) {
				return;
			}

			vehiclePart.getDoor().setLocked(true);
		} else {
			if (!this.canUnlockDoor(vehiclePart, gameCharacter)) {
				return;
			}

			vehiclePart.getDoor().setLocked(false);
		}
	}

	public boolean canLockDoor(VehiclePart vehiclePart, IsoGameCharacter gameCharacter) {
		if (vehiclePart == null) {
			return false;
		} else if (gameCharacter == null) {
			return false;
		} else {
			VehicleDoor vehicleDoor = vehiclePart.getDoor();
			if (vehicleDoor == null) {
				return false;
			} else if (vehicleDoor.lockBroken) {
				return false;
			} else if (vehicleDoor.locked) {
				return false;
			} else if (this.getSeat(gameCharacter) != -1) {
				return true;
			} else if (gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) != null) {
				return true;
			} else {
				VehiclePart vehiclePart2 = vehiclePart.getChildWindow();
				if (vehiclePart2 != null && vehiclePart2.getInventoryItem() == null) {
					return true;
				} else {
					VehicleWindow vehicleWindow = vehiclePart2 == null ? null : vehiclePart2.getWindow();
					return vehicleWindow != null && (vehicleWindow.isOpen() || vehicleWindow.isDestroyed());
				}
			}
		}
	}

	public boolean canUnlockDoor(VehiclePart vehiclePart, IsoGameCharacter gameCharacter) {
		if (vehiclePart == null) {
			return false;
		} else if (gameCharacter == null) {
			return false;
		} else {
			VehicleDoor vehicleDoor = vehiclePart.getDoor();
			if (vehicleDoor == null) {
				return false;
			} else if (vehicleDoor.lockBroken) {
				return false;
			} else if (!vehicleDoor.locked) {
				return false;
			} else if (this.getSeat(gameCharacter) != -1) {
				return true;
			} else if (gameCharacter.getInventory().haveThisKeyId(this.getKeyId()) != null) {
				return true;
			} else {
				VehiclePart vehiclePart2 = vehiclePart.getChildWindow();
				if (vehiclePart2 != null && vehiclePart2.getInventoryItem() == null) {
					return true;
				} else {
					VehicleWindow vehicleWindow = vehiclePart2 == null ? null : vehiclePart2.getWindow();
					return vehicleWindow != null && (vehicleWindow.isOpen() || vehicleWindow.isDestroyed());
				}
			}
		}
	}

	public void setFullUpdateFlag() {
		this.updateFlags = (short)(this.updateFlags | 1);
	}

	private void initParts() {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			String string = vehiclePart.getLuaFunction("init");
			if (string != null) {
				this.callLuaVoid(string, this, vehiclePart);
			}
		}
	}

	private void createParts() {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			if (!vehiclePart.bCreated) {
				vehiclePart.bCreated = true;
				String string = vehiclePart.getLuaFunction("create");
				if (string == null) {
					vehiclePart.setRandomCondition((InventoryItem)null);
				} else {
					this.callLuaVoid(string, this, vehiclePart);
					if (vehiclePart.getCondition() == -1) {
						vehiclePart.setRandomCondition((InventoryItem)null);
					}
				}
			}
		}
	}

	public CarController getController() {
		return this.physics;
	}

	public int getSkinCount() {
		return this.script.getSkinCount();
	}

	public int getSkinIndex() {
		return this.skinIndex;
	}

	public void setSkinIndex(int int1) {
		if (int1 >= 0 && int1 <= this.getSkinCount()) {
			this.skinIndex = int1;
		}
	}

	public VehicleScript getScript() {
		return this.script;
	}

	public void setScriptName(String string) {
		this.scriptName = string;
	}

	public String getScriptName() {
		return this.scriptName;
	}

	public void setScript() {
		this.setScript(this.scriptName);
	}

	public void setScript(String string) {
		if (string != null && !string.isEmpty()) {
			this.scriptName = string;
			boolean boolean1 = this.script != null;
			this.script = ScriptManager.instance.getVehicle(this.scriptName);
			ArrayList arrayList;
			int int1;
			if (this.script == null) {
				ArrayList arrayList2 = ScriptManager.instance.getAllVehicleScripts();
				if (!arrayList2.isEmpty()) {
					arrayList = new ArrayList();
					for (int1 = 0; int1 < arrayList2.size(); ++int1) {
						VehicleScript vehicleScript = (VehicleScript)arrayList2.get(int1);
						if (vehicleScript.getWheelCount() == 0) {
							arrayList.add(vehicleScript);
							arrayList2.remove(int1--);
						}
					}

					boolean boolean2 = this.loaded && this.parts.isEmpty() || this.scriptName.contains("Burnt");
					if (boolean2 && !arrayList.isEmpty()) {
						this.script = (VehicleScript)arrayList.get(Rand.Next(arrayList.size()));
					} else if (!arrayList2.isEmpty()) {
						this.script = (VehicleScript)arrayList2.get(Rand.Next(arrayList2.size()));
					}

					if (this.script != null) {
						this.scriptName = this.script.getFullName();
					}
				}
			}

			this.battery = null;
			this.models.clear();
			if (this.script != null) {
				BaseVehicle.Passenger[] passengerArray = this.passengers;
				this.passengers = new BaseVehicle.Passenger[this.script.getPassengerCount()];
				for (int int2 = 0; int2 < this.passengers.length; ++int2) {
					if (int2 < passengerArray.length) {
						this.passengers[int2] = passengerArray[int2];
					} else {
						this.passengers[int2] = new BaseVehicle.Passenger();
					}
				}

				arrayList = new ArrayList();
				arrayList.addAll(this.parts);
				this.parts.clear();
				for (int1 = 0; int1 < this.script.getPartCount(); ++int1) {
					VehicleScript.Part part = this.script.getPart(int1);
					VehiclePart vehiclePart = null;
					for (int int3 = 0; int3 < arrayList.size(); ++int3) {
						VehiclePart vehiclePart2 = (VehiclePart)arrayList.get(int3);
						if (vehiclePart2.getScriptPart() != null && part.id.equals(vehiclePart2.getScriptPart().id)) {
							vehiclePart = vehiclePart2;
							break;
						}

						if (vehiclePart2.partId != null && part.id.equals(vehiclePart2.partId)) {
							vehiclePart = vehiclePart2;
							break;
						}
					}

					if (vehiclePart == null) {
						vehiclePart = new VehiclePart(this);
					}

					vehiclePart.setScriptPart(part);
					vehiclePart.category = part.category;
					vehiclePart.specificItem = part.specificItem;
					if (part.container != null && part.container.contentType == null) {
						if (vehiclePart.getItemContainer() == null) {
							ItemContainer itemContainer = new ItemContainer(part.id, (IsoGridSquare)null, this, 1, 1);
							vehiclePart.setItemContainer(itemContainer);
							itemContainer.ID = 0;
						}

						vehiclePart.getItemContainer().Capacity = part.container.capacity;
					} else {
						vehiclePart.setItemContainer((ItemContainer)null);
					}

					if (part.door == null) {
						vehiclePart.door = null;
					} else if (vehiclePart.door == null) {
						vehiclePart.door = new VehicleDoor(vehiclePart);
						vehiclePart.door.init(part.door);
					}

					if (part.window == null) {
						vehiclePart.window = null;
					} else if (vehiclePart.window == null) {
						vehiclePart.window = new VehicleWindow(vehiclePart);
						vehiclePart.window.init(part.window);
					} else {
						vehiclePart.window.openable = part.window.openable;
					}

					vehiclePart.parent = null;
					if (vehiclePart.children != null) {
						vehiclePart.children.clear();
					}

					this.parts.add(vehiclePart);
					if ("Battery".equals(vehiclePart.getId())) {
						this.battery = vehiclePart;
					}
				}

				VehiclePart vehiclePart3;
				for (int1 = 0; int1 < this.script.getPartCount(); ++int1) {
					vehiclePart3 = (VehiclePart)this.parts.get(int1);
					VehicleScript.Part part2 = vehiclePart3.getScriptPart();
					if (part2.parent != null) {
						vehiclePart3.parent = this.getPartById(part2.parent);
						if (vehiclePart3.parent != null) {
							vehiclePart3.parent.addChild(vehiclePart3);
						}
					}
				}

				if (!boolean1 && !this.loaded) {
					this.frontEndDurability = this.rearEndDurability = 99999;
				}

				this.frontEndDurability = Math.min(this.frontEndDurability, this.script.getFrontEndHealth());
				this.rearEndDurability = Math.min(this.rearEndDurability, this.script.getRearEndHealth());
				this.currentFrontEndDurability = this.frontEndDurability;
				this.currentRearEndDurability = this.rearEndDurability;
				for (int1 = 0; int1 < this.script.getPartCount(); ++int1) {
					vehiclePart3 = (VehiclePart)this.parts.get(int1);
					vehiclePart3.setInventoryItem(vehiclePart3.item);
				}
			}

			if (!this.loaded || this.colorHue == 0.0F && this.colorSaturation == 0.0F && this.colorValue == 0.0F) {
				this.doVehicleColor();
			}
		}
	}

	public void scriptReloaded() {
		this.tempTransform2.setIdentity();
		if (this.physics != null) {
			this.getWorldTransform(this.tempTransform2);
			this.tempTransform2.basis.getUnnormalizedRotation(this.savedRot);
			Bullet.removeVehicle(this.VehicleID);
			this.physics = null;
		}

		if (this.createdModel) {
			ModelManager.instance.Remove(this);
			this.createdModel = false;
		}

		this.setScript(this.scriptName);
		this.createPhysics();
	}

	public String getSkin() {
		if (this.script != null && this.script.getSkinCount() != 0) {
			if (this.skinIndex < 0 || this.skinIndex >= this.script.getSkinCount()) {
				this.skinIndex = Rand.Next(this.script.getSkinCount());
			}

			return this.script.getSkin(this.skinIndex).texture;
		} else {
			return "BOGUS";
		}
	}

	protected BaseVehicle.ModelInfo setModelVisible(VehiclePart vehiclePart, VehicleScript.Model model, boolean boolean1) {
		for (int int1 = 0; int1 < this.models.size(); ++int1) {
			BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int1);
			if (modelInfo.part == vehiclePart && modelInfo.scriptModel == model) {
				if (boolean1) {
					return modelInfo;
				}

				this.models.remove(int1);
				if (this.createdModel) {
					ModelManager.instance.Remove(this);
					ModelManager.instance.Add(this);
				}

				vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 64);
				this.updateFlags = (short)(this.updateFlags | 64);
				return null;
			}
		}

		if (boolean1) {
			BaseVehicle.ModelInfo modelInfo2 = new BaseVehicle.ModelInfo();
			modelInfo2.part = vehiclePart;
			modelInfo2.scriptModel = model;
			modelInfo2.wheelIndex = vehiclePart.getWheelIndex();
			this.models.add(modelInfo2);
			if (this.createdModel) {
				ModelManager.instance.Remove(this);
				ModelManager.instance.Add(this);
			}

			vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 64);
			this.updateFlags = (short)(this.updateFlags | 64);
			return modelInfo2;
		} else {
			return null;
		}
	}

	protected VehicleScript.Passenger getScriptPassenger(int int1) {
		if (this.getScript() == null) {
			return null;
		} else {
			return int1 >= 0 && int1 < this.getScript().getPassengerCount() ? this.getScript().getPassenger(int1) : null;
		}
	}

	public int getMaxPassengers() {
		return this.passengers.length;
	}

	public boolean setPassenger(int int1, IsoGameCharacter gameCharacter, Vector3f vector3f) {
		if (int1 >= 0 && int1 < this.passengers.length) {
			if (int1 == 0) {
				this.setNeedPartsUpdate(true);
			}

			this.passengers[int1].character = gameCharacter;
			this.passengers[int1].offset.set((Vector3fc)vector3f);
			return true;
		} else {
			return false;
		}
	}

	public boolean clearPassenger(int int1) {
		if (int1 >= 0 && int1 < this.passengers.length) {
			this.passengers[int1].character = null;
			this.passengers[int1].offset.set(0.0F, 0.0F, 0.0F);
			return true;
		} else {
			return false;
		}
	}

	public BaseVehicle.Passenger getPassenger(int int1) {
		return int1 >= 0 && int1 < this.passengers.length ? this.passengers[int1] : null;
	}

	public IsoGameCharacter getCharacter(int int1) {
		BaseVehicle.Passenger passenger = this.getPassenger(int1);
		return passenger != null ? passenger.character : null;
	}

	public int getSeat(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			if (this.getCharacter(int1) == gameCharacter) {
				return int1;
			}
		}

		return -1;
	}

	public boolean isDriver(IsoGameCharacter gameCharacter) {
		return this.getSeat(gameCharacter) == 0;
	}

	public Vector3f getWorldPos(Vector3f vector3f, Vector3f vector3f2, VehicleScript vehicleScript) {
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.origin.set(0.0F, 0.0F, 0.0F);
		transform.basis.scale(vehicleScript.getModelScale());
		vector3f2.set((Vector3fc)vector3f);
		transform.transform(vector3f2);
		float float1 = this.jniTransform.origin.x + WorldSimulation.instance.offsetX;
		float float2 = this.jniTransform.origin.z + WorldSimulation.instance.offsetY;
		float float3 = this.jniTransform.origin.y;
		vector3f2.set(float1 + vector3f2.x, float2 + vector3f2.z, float3 + vector3f2.y);
		return vector3f2;
	}

	public Vector3f getWorldPos(Vector3f vector3f, Vector3f vector3f2) {
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.origin.set(0.0F, 0.0F, 0.0F);
		transform.basis.scale(this.getScript().getModelScale());
		vector3f2.set((Vector3fc)vector3f);
		transform.transform(vector3f2);
		float float1 = this.jniTransform.origin.x + WorldSimulation.instance.offsetX;
		float float2 = this.jniTransform.origin.z + WorldSimulation.instance.offsetY;
		float float3 = this.jniTransform.origin.y;
		vector3f2.set(float1 + vector3f2.x, float2 + vector3f2.z, float3 + vector3f2.y);
		return vector3f2;
	}

	public Vector3f getLocalPos(Vector3f vector3f, Vector3f vector3f2) {
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.basis.scale(1.0F / this.getScript().getModelScale());
		transform.inverse();
		vector3f2.set(vector3f.x - WorldSimulation.instance.offsetX, 0.0F, vector3f.y - WorldSimulation.instance.offsetY);
		transform.transform(vector3f2);
		return vector3f2;
	}

	public Vector3f getLocalPosUnscaled(Vector3f vector3f, Vector3f vector3f2) {
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.inverse();
		vector3f2.set(vector3f.x - WorldSimulation.instance.offsetX, 0.0F, vector3f.y - WorldSimulation.instance.offsetY);
		transform.transform(vector3f2);
		return vector3f2;
	}

	public Vector3f getPassengerWorldPos(int int1, Vector3f vector3f) {
		BaseVehicle.Passenger passenger = this.getPassenger(int1);
		return passenger == null ? null : this.getWorldPos(passenger.offset, vector3f);
	}

	public VehicleScript.Anim getPassengerAnim(int int1, String string) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		if (passenger == null) {
			return null;
		} else {
			for (int int2 = 0; int2 < passenger.anims.size(); ++int2) {
				VehicleScript.Anim anim = (VehicleScript.Anim)passenger.anims.get(int2);
				if (string.equals(anim.id)) {
					return anim;
				}
			}

			return null;
		}
	}

	public VehicleScript.Position getPassengerPosition(int int1, String string) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? null : passenger.getPositionById(string);
	}

	public VehiclePart getPassengerDoor(int int1) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? null : this.getPartById(passenger.door);
	}

	public VehiclePart getPassengerDoor2(int int1) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? null : this.getPartById(passenger.door2);
	}

	public boolean haveOneDoorUnlocked() {
		for (int int1 = 0; int1 < this.getPartCount(); ++int1) {
			VehiclePart vehiclePart = this.getPartByIndex(int1);
			if (vehiclePart.getDoor() != null && (vehiclePart.getId().contains("Left") || vehiclePart.getId().contains("Right")) && (!vehiclePart.getDoor().isLocked() || vehiclePart.getDoor().isOpen())) {
				return true;
			}
		}

		return false;
	}

	public String getPassengerArea(int int1) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? null : passenger.area;
	}

	public void playPassengerAnim(int int1, String string) {
		IsoGameCharacter gameCharacter = this.getCharacter(int1);
		this.playPassengerAnim(int1, string, gameCharacter);
	}

	public void playPassengerAnim(int int1, String string, IsoGameCharacter gameCharacter) {
		if (gameCharacter != null) {
			VehicleScript.Anim anim = this.getPassengerAnim(int1, string);
			if (anim != null) {
				this.playCharacterAnim(gameCharacter, anim);
			}
		}
	}

	public void playPassengerSound(int int1, String string) {
		VehicleScript.Anim anim = this.getPassengerAnim(int1, string);
		if (anim != null && anim.sound != null) {
			this.playSound(anim.sound);
		}
	}

	public void playPartAnim(VehiclePart vehiclePart, String string) {
		if (this.parts.contains(vehiclePart)) {
			VehicleScript.Anim anim = vehiclePart.getAnimById(string);
			if (anim != null) {
				;
			}
		}
	}

	public void playActorAnim(VehiclePart vehiclePart, String string, IsoGameCharacter gameCharacter) {
		if (gameCharacter != null) {
			if (this.parts.contains(vehiclePart)) {
				VehicleScript.Anim anim = vehiclePart.getAnimById("Actor" + string);
				if (anim != null) {
					this.playCharacterAnim(gameCharacter, anim);
				}
			}
		}
	}

	private void playCharacterAnim(IsoGameCharacter gameCharacter, VehicleScript.Anim anim) {
		gameCharacter.PlayAnimUnlooped(anim.anim);
		gameCharacter.getSpriteDef().setFrameSpeedPerFrame(anim.rate);
		gameCharacter.getLegsSprite().Animate = true;
		Vector3f vector3f = this.getForwardVector(this.tempVector3f_1);
		if (anim.angle.lengthSquared() != 0.0F) {
			Matrix4f matrix4f = this.tempMatrix4fLWJGL_1;
			matrix4f.rotationXYZ((float)Math.toRadians((double)anim.angle.x), (float)Math.toRadians((double)anim.angle.y), (float)Math.toRadians((double)anim.angle.z));
			vector3f.rotate(matrix4f.getNormalizedRotation(this.tempQuat4f));
		}

		this.tempVector2.set(vector3f.x, vector3f.z);
		gameCharacter.DirectionFromVector(this.tempVector2);
		gameCharacter.angle.x = vector3f.x;
		gameCharacter.angle.y = vector3f.z;
	}

	public void playPartSound(VehiclePart vehiclePart, String string) {
		if (this.parts.contains(vehiclePart)) {
			VehicleScript.Anim anim = vehiclePart.getAnimById(string);
			if (anim != null && anim.sound != null) {
				this.playSound(anim.sound);
			}
		}
	}

	public void setCharacterPosition(IsoGameCharacter gameCharacter, int int1, String string) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		if (passenger != null) {
			VehicleScript.Position position = passenger.getPositionById(string);
			if (position != null) {
				if (this.getCharacter(int1) == gameCharacter) {
					this.passengers[int1].offset.set((Vector3fc)position.offset);
				} else {
					Vector3f vector3f = this.tempVector3f_1;
					if (position.area == null) {
						vector3f = this.getWorldPos(position.offset, this.tempVector3f_1);
					} else {
						VehicleScript.Area area = this.script.getAreaById(position.area);
						Vector2 vector2 = this.areaPositionWorld(area);
						vector3f.x = vector2.x;
						vector3f.y = vector2.y;
						vector3f.z = 0.0F;
					}

					gameCharacter.setX(vector3f.x);
					gameCharacter.setY(vector3f.y);
					gameCharacter.setZ(0.0F);
				}

				if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
					((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 10.0F;
				}
			}
		}
	}

	public void transmitCharacterPosition(int int1, String string) {
		if (GameClient.bClient) {
			VehicleManager.instance.sendPassengerPosition(this, int1, string);
		}
	}

	public void setCharacterPositionToAnim(IsoGameCharacter gameCharacter, int int1, String string) {
		VehicleScript.Anim anim = this.getPassengerAnim(int1, string);
		if (anim != null) {
			if (this.getCharacter(int1) == gameCharacter) {
				this.passengers[int1].offset.set((Vector3fc)anim.offset);
			} else {
				Vector3f vector3f = this.getWorldPos(anim.offset, this.tempVector3f_1);
				gameCharacter.setX(vector3f.x);
				gameCharacter.setY(vector3f.y);
				gameCharacter.setZ(0.0F);
			}
		}
	}

	public int getPassengerSwitchSeatCount(int int1) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? -1 : passenger.switchSeats.size();
	}

	public VehicleScript.Passenger.SwitchSeat getPassengerSwitchSeat(int int1, int int2) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		if (passenger == null) {
			return null;
		} else {
			return int2 >= 0 && int2 < passenger.switchSeats.size() ? (VehicleScript.Passenger.SwitchSeat)passenger.switchSeats.get(int2) : null;
		}
	}

	private VehicleScript.Passenger.SwitchSeat getSwitchSeat(int int1, int int2) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		if (passenger == null) {
			return null;
		} else {
			for (int int3 = 0; int3 < passenger.switchSeats.size(); ++int3) {
				VehicleScript.Passenger.SwitchSeat switchSeat = (VehicleScript.Passenger.SwitchSeat)passenger.switchSeats.get(int3);
				if (switchSeat.seat == int2 && this.getPartForSeatContainer(int2) != null && this.getPartForSeatContainer(int2).getInventoryItem() != null) {
					return switchSeat;
				}
			}

			return null;
		}
	}

	public String getSwitchSeatAnimName(int int1, int int2) {
		VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(int1, int2);
		return switchSeat == null ? null : switchSeat.anim;
	}

	public float getSwitchSeatAnimRate(int int1, int int2) {
		VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(int1, int2);
		return switchSeat == null ? 0.0F : switchSeat.rate;
	}

	public String getSwitchSeatSound(int int1, int int2) {
		VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(int1, int2);
		return switchSeat == null ? null : switchSeat.sound;
	}

	public boolean canSwitchSeat(int int1, int int2) {
		VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(int1, int2);
		return switchSeat != null;
	}

	public void switchSeat(IsoGameCharacter gameCharacter, int int1) {
		int int2 = this.getSeat(gameCharacter);
		if (int2 != -1) {
			this.clearPassenger(int2);
			VehicleScript.Position position = this.getPassengerPosition(int1, "inside");
			if (position == null) {
				this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
				this.setPassenger(int1, gameCharacter, this.tempVector3f_1);
			} else {
				this.setPassenger(int1, gameCharacter, position.offset);
			}

			VehicleManager.instance.sendSwichSeat(this, int1, gameCharacter);
		}
	}

	public void switchSeatRSync(IsoGameCharacter gameCharacter, int int1) {
		int int2 = this.getSeat(gameCharacter);
		if (int2 != -1) {
			this.clearPassenger(int2);
			VehicleScript.Position position = this.getPassengerPosition(int1, "inside");
			if (position == null) {
				this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
				this.setPassenger(int1, gameCharacter, this.tempVector3f_1);
			} else {
				this.setPassenger(int1, gameCharacter, position.offset);
			}
		}
	}

	public void playSwitchSeatAnim(int int1, int int2) {
		IsoGameCharacter gameCharacter = this.getCharacter(int1);
		if (gameCharacter != null) {
			VehicleScript.Passenger.SwitchSeat switchSeat = this.getSwitchSeat(int1, int2);
			if (switchSeat != null) {
				gameCharacter.PlayAnimUnlooped(switchSeat.anim);
				gameCharacter.getSpriteDef().setFrameSpeedPerFrame(switchSeat.rate);
				gameCharacter.getLegsSprite().Animate = true;
			}
		}
	}

	public boolean isSeatOccupied(int int1) {
		VehiclePart vehiclePart = this.getPartForSeatContainer(int1);
		if (vehiclePart != null && vehiclePart.getItemContainer() != null && !vehiclePart.getItemContainer().getItems().isEmpty()) {
			return true;
		} else {
			return this.getCharacter(int1) != null;
		}
	}

	public boolean isSeatInstalled(int int1) {
		VehiclePart vehiclePart = this.getPartForSeatContainer(int1);
		return vehiclePart != null && vehiclePart.getInventoryItem() != null;
	}

	public int getBestSeat(IsoGameCharacter gameCharacter) {
		if ((int)this.getZ() != (int)gameCharacter.getZ()) {
			return -1;
		} else if (gameCharacter.DistTo(this) > 5.0F) {
			return -1;
		} else {
			VehicleScript vehicleScript = this.getScript();
			if (vehicleScript == null) {
				return -1;
			} else {
				for (int int1 = 0; int1 < vehicleScript.getPassengerCount(); ++int1) {
					if (!this.isEnterBlocked(gameCharacter, int1) && !this.isSeatOccupied(int1)) {
						VehicleScript.Position position = this.getPassengerPosition(int1, "outside");
						Vector3f vector3f;
						float float1;
						float float2;
						Vector3f vector3f2;
						Vector2 vector2;
						float float3;
						if (position != null) {
							vector3f = this.getWorldPos(position.offset, this.tempVector3f_1);
							float1 = vector3f.x;
							float2 = vector3f.y;
							vector3f2 = this.tempVector3f_1;
							vector3f2.set(0.0F, position.offset.y, position.offset.z);
							this.getWorldPos(vector3f2, vector3f2);
							vector2 = this.tempVector2;
							vector2.set(vector3f2.x - gameCharacter.getX(), vector3f2.y - gameCharacter.getY());
							vector2.normalize();
							float3 = vector2.dot(gameCharacter.getAngle());
							if (float3 > 0.5F && IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), float1, float2) < 1.0F) {
								return int1;
							}
						}

						position = this.getPassengerPosition(int1, "outside2");
						if (position != null) {
							vector3f = this.getWorldPos(position.offset, this.tempVector3f_1);
							float1 = vector3f.x;
							float2 = vector3f.y;
							vector3f2 = this.tempVector3f_1;
							vector3f2.set(0.0F, position.offset.y, position.offset.z);
							this.getWorldPos(vector3f2, vector3f2);
							vector2 = this.tempVector2;
							vector2.set(vector3f2.x - gameCharacter.getX(), vector3f2.y - gameCharacter.getY());
							vector2.normalize();
							float3 = vector2.dot(gameCharacter.getAngle());
							if (float3 > 0.5F && IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), float1, float2) < 1.0F) {
								return int1;
							}
						}
					}
				}

				return -1;
			}
		}
	}

	public void updateHasExtendOffsetForExit(IsoGameCharacter gameCharacter) {
		this.hasExtendOffsetExiting = true;
		this.updateHasExtendOffset(gameCharacter);
		this.getPoly();
	}

	public void updateHasExtendOffsetForExitEnd(IsoGameCharacter gameCharacter) {
		this.hasExtendOffsetExiting = false;
		this.updateHasExtendOffset(gameCharacter);
		this.getPoly();
	}

	public void updateHasExtendOffset(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getVehicle() == this && !this.hasExtendOffsetExiting) {
			if (!this.hasExtendOffset) {
				this.polyDirty = true;
			}

			this.hasExtendOffset = true;
		} else if ((int)this.getZ() != (int)gameCharacter.getZ()) {
			if (!this.hasExtendOffset) {
				this.polyDirty = true;
			}

			this.hasExtendOffset = true;
		} else {
			Vector3f vector3f = this.tempVector3f_1;
			vector3f.set(gameCharacter.x, gameCharacter.y, this.z);
			this.getLocalPos(vector3f, vector3f);
			float float1 = this.script.getModelScale();
			float float2 = 3.0F;
			float float3 = -(this.script.getExtents().x / 2.0F + float2) / float1;
			float float4 = -float3;
			float float5 = -(this.script.getExtents().z / 2.0F + float2) / float1;
			float float6 = -float5;
			if (vector3f.x >= float3 && vector3f.x < float4 && vector3f.z >= float5 && vector3f.z < float6) {
				if (!this.hasExtendOffset) {
					return;
				}

				byte byte1 = 5;
				int int1 = ((int)this.x - byte1) / 10;
				int int2 = ((int)this.y - byte1) / 10;
				int int3 = (int)Math.ceil((double)((this.x + (float)byte1) / 10.0F));
				int int4 = (int)Math.ceil((double)((this.y + (float)byte1) / 10.0F));
				for (int int5 = int2; int5 <= int4; ++int5) {
					for (int int6 = int1; int6 <= int3; ++int6) {
						IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int6 * 10, int5 * 10, 0);
						if (chunk != null) {
							for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
								BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
								if (this != baseVehicle && this.DistTo(baseVehicle) < (float)byte1) {
									if (this.hasExtendOffset) {
										this.polyDirty = true;
									}

									this.hasExtendOffset = false;
									return;
								}
							}
						}
					}
				}
			}

			if (!this.hasExtendOffset) {
				this.polyDirty = true;
			}

			this.hasExtendOffset = true;
		}
	}

	public VehiclePart getUseablePart(IsoGameCharacter gameCharacter) {
		if ((int)this.getZ() != (int)gameCharacter.getZ()) {
			return null;
		} else if (gameCharacter.DistTo(this) > 6.0F) {
			return null;
		} else {
			VehicleScript vehicleScript = this.getScript();
			if (vehicleScript == null) {
				return null;
			} else {
				for (int int1 = 0; int1 < this.parts.size(); ++int1) {
					VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
					if (vehiclePart.getArea() != null && this.isInArea(vehiclePart.getArea(), gameCharacter)) {
						String string = vehiclePart.getLuaFunction("use");
						if (string != null && !string.equals("")) {
							VehicleScript.Area area = vehicleScript.getAreaById(vehiclePart.getArea());
							if (area != null) {
								Vector2 vector2 = this.areaPositionLocal(area);
								if (vector2 != null) {
									Vector3f vector3f = this.tempVector3f_1;
									float float1 = vehicleScript.getExtents().z / vehicleScript.getModelScale();
									if (!(vector2.y >= float1 / 2.0F) && !(vector2.y <= -float1 / 2.0F)) {
										vector3f.set(0.0F, 0.0F, vector2.y);
									} else {
										vector3f.set(vector2.x, 0.0F, 0.0F);
									}

									this.getWorldPos(vector3f, vector3f);
									Vector2 vector22 = this.tempVector2;
									vector22.set(vector3f.x - gameCharacter.getX(), vector3f.y - gameCharacter.getY());
									vector22.normalize();
									float float2 = vector22.dot(gameCharacter.getAngle());
									if (float2 > 0.5F && !PolygonalMap2.instance.lineClearCollide(gameCharacter.x, gameCharacter.y, vector3f.x, vector3f.y, (int)gameCharacter.z, this, false, true)) {
										return vehiclePart;
									}

									break;
								}
							}
						}
					}
				}

				return null;
			}
		}
	}

	public VehiclePart getClosestWindow(IsoGameCharacter gameCharacter) {
		if ((int)this.getZ() != (int)gameCharacter.getZ()) {
			return null;
		} else if (gameCharacter.DistTo(this) > 5.0F) {
			return null;
		} else {
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getWindow() != null && vehiclePart.getArea() != null && this.isInArea(vehiclePart.getArea(), gameCharacter)) {
					VehicleScript.Area area = this.script.getAreaById(vehiclePart.getArea());
					Vector3f vector3f = this.tempVector3f_1;
					float float1 = this.script.getExtents().x / this.script.getModelScale();
					float float2 = this.script.getExtents().z / this.script.getModelScale();
					if (!(area.y >= float2 / 2.0F) && !(area.y <= -float2 / 2.0F)) {
						vector3f.set(0.0F, 0.0F, area.y);
					} else {
						vector3f.set(area.x, 0.0F, 0.0F);
					}

					this.getWorldPos(vector3f, vector3f);
					Vector2 vector2 = this.tempVector2;
					vector2.set(vector3f.x - gameCharacter.getX(), vector3f.y - gameCharacter.getY());
					vector2.normalize();
					float float3 = vector2.dot(gameCharacter.getAngle());
					if (float3 > 0.5F) {
						return vehiclePart;
					}

					break;
				}
			}

			return null;
		}
	}

	public void getFacingPosition(IsoGameCharacter gameCharacter, Vector2 vector2) {
		Vector3f vector3f = this.tempVector3f_1.set(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ());
		this.getLocalPos(vector3f, vector3f);
		float float1 = this.script.getModelScale();
		float float2 = -this.script.getExtents().x / float1 / 2.0F;
		float float3 = this.script.getExtents().x / float1 / 2.0F;
		float float4 = -this.script.getExtents().z / float1 / 2.0F;
		float float5 = this.script.getExtents().z / float1 / 2.0F;
		float float6 = 0.0F;
		float float7 = 0.0F;
		if (vector3f.x <= 0.0F && vector3f.z >= float4 && vector3f.z <= float5) {
			float7 = vector3f.z;
		} else if (vector3f.x > 0.0F && vector3f.z >= float4 && vector3f.z <= float5) {
			float7 = vector3f.z;
		} else if (vector3f.z <= 0.0F && vector3f.x >= float2 && vector3f.x <= float3) {
			float6 = vector3f.x;
		} else if (vector3f.z > 0.0F && vector3f.x >= float2 && vector3f.x <= float3) {
			float6 = vector3f.x;
		}

		vector3f.set(float6, 0.0F, float7);
		this.getWorldPos(vector3f, vector3f);
		vector2.set(vector3f.x, vector3f.y);
	}

	public boolean enter(int int1, IsoGameCharacter gameCharacter, Vector3f vector3f) {
		if (gameCharacter == null) {
			return false;
		} else if (gameCharacter.getVehicle() != null && !gameCharacter.getVehicle().exit(gameCharacter)) {
			return false;
		} else if (this.setPassenger(int1, gameCharacter, vector3f)) {
			gameCharacter.setVehicle(this);
			gameCharacter.setCollidable(false);
			if (GameClient.bClient) {
				VehicleManager.instance.sendEnter(this, int1, gameCharacter);
			}

			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				((IsoPlayer)gameCharacter).dirtyRecalcGridStackTime = 10.0F;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean enter(int int1, IsoGameCharacter gameCharacter) {
		if (this.getPartForSeatContainer(int1) != null && this.getPartForSeatContainer(int1).getInventoryItem() != null) {
			VehicleScript.Position position = this.getPassengerPosition(int1, "outside");
			return position != null ? this.enter(int1, gameCharacter, position.offset) : false;
		} else {
			return false;
		}
	}

	public boolean enterRSync(int int1, IsoGameCharacter gameCharacter, BaseVehicle baseVehicle) {
		if (gameCharacter == null) {
			return false;
		} else {
			VehicleScript.Position position = this.getPassengerPosition(int1, "inside");
			if (position != null) {
				if (this.setPassenger(int1, gameCharacter, position.offset)) {
					gameCharacter.setVehicle(baseVehicle);
					gameCharacter.setCollidable(false);
					if (GameClient.bClient) {
						LuaEventManager.triggerEvent("OnContainerUpdate");
					}

					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public boolean exit(IsoGameCharacter gameCharacter) {
		if (gameCharacter == null) {
			return false;
		} else {
			int int1 = this.getSeat(gameCharacter);
			if (int1 == -1) {
				return false;
			} else if (this.clearPassenger(int1)) {
				gameCharacter.setVehicle((BaseVehicle)null);
				gameCharacter.setCollidable(true);
				if (GameClient.bClient) {
					VehicleManager.instance.sendExit(this, gameCharacter);
				}

				if (this.getDriver() == null && this.soundHornOn) {
					this.onHornStop();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public boolean exitRSync(IsoGameCharacter gameCharacter) {
		if (gameCharacter == null) {
			return false;
		} else {
			int int1 = this.getSeat(gameCharacter);
			if (int1 == -1) {
				return false;
			} else if (this.clearPassenger(int1)) {
				gameCharacter.setVehicle((BaseVehicle)null);
				gameCharacter.setCollidable(true);
				if (GameClient.bClient) {
					LuaEventManager.triggerEvent("OnContainerUpdate");
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public boolean hasRoof(int int1) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? false : passenger.hasRoof;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		float float1 = 5.0E-4F;
		if (this.getX() < (float)this.square.x + float1) {
			this.setX((float)this.square.x + float1);
		} else if (this.getX() > (float)(this.square.x + 1) - float1) {
			this.setX((float)(this.square.x + 1) - float1);
		}

		if (this.getY() < (float)this.square.y + float1) {
			this.setY((float)this.square.y + float1);
		} else if (this.getY() > (float)(this.square.y + 1) - float1) {
			this.setY((float)(this.square.y + 1) - float1);
		}

		super.save(byteBuffer);
		Quaternionf quaternionf = this.savedRot;
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.getRotation(quaternionf);
		byteBuffer.putFloat(quaternionf.x);
		byteBuffer.putFloat(quaternionf.y);
		byteBuffer.putFloat(quaternionf.z);
		byteBuffer.putFloat(quaternionf.w);
		GameWindow.WriteStringUTF(byteBuffer, this.scriptName);
		byteBuffer.putInt(this.skinIndex);
		byteBuffer.put((byte)(this.isEngineRunning() ? 1 : 0));
		byteBuffer.putInt(this.frontEndDurability);
		byteBuffer.putInt(this.rearEndDurability);
		byteBuffer.putInt(this.currentFrontEndDurability);
		byteBuffer.putInt(this.currentRearEndDurability);
		byteBuffer.putInt(this.engineLoudness);
		byteBuffer.putInt(this.engineQuality);
		byteBuffer.putInt(this.keyId);
		byteBuffer.put(this.keySpawned);
		byteBuffer.put((byte)(this.headlightsOn ? 1 : 0));
		byteBuffer.put((byte)(this.bCreated ? 1 : 0));
		byteBuffer.put((byte)(this.soundHornOn ? 1 : 0));
		byteBuffer.put((byte)(this.soundBackMoveOn ? 1 : 0));
		byteBuffer.put((byte)this.lightbarLightsMode.get());
		byteBuffer.put((byte)this.lightbarSirenMode.get());
		byteBuffer.putShort((short)this.parts.size());
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			vehiclePart.save(byteBuffer);
		}

		byteBuffer.put((byte)(this.keyIsOnDoor ? 1 : 0));
		byteBuffer.put((byte)(this.hotwired ? 1 : 0));
		byteBuffer.put((byte)(this.hotwiredBroken ? 1 : 0));
		byteBuffer.put((byte)(this.keysInIgnition ? 1 : 0));
		byteBuffer.putFloat(this.rust);
		byteBuffer.putFloat(this.colorHue);
		byteBuffer.putFloat(this.colorSaturation);
		byteBuffer.putFloat(this.colorValue);
		byteBuffer.putInt(this.enginePower);
		byteBuffer.putShort(this.VehicleID);
		GameWindow.WriteString((ByteBuffer)byteBuffer, (String)null);
		byteBuffer.putInt(this.mechanicalID);
		byteBuffer.put((byte)(this.alarmed ? 1 : 0));
		byteBuffer.putDouble(this.sirenStartTime);
		if (this.getCurrentKey() != null) {
			byteBuffer.put((byte)1);
			this.getCurrentKey().save(byteBuffer, false);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		if (this.z < 0.0F) {
			this.z = 0.0F;
		}

		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		this.savedRot.set(float1, float2, float3, float4);
		this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, this.getZ(), this.getY() - WorldSimulation.instance.offsetY);
		this.jniTransform.setRotation(this.savedRot);
		this.scriptName = GameWindow.ReadStringUTF(byteBuffer);
		this.skinIndex = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			this.engineState = BaseVehicle.engineStateTypes.Running;
		}

		this.frontEndDurability = byteBuffer.getInt();
		this.rearEndDurability = byteBuffer.getInt();
		this.currentFrontEndDurability = byteBuffer.getInt();
		this.currentRearEndDurability = byteBuffer.getInt();
		this.engineLoudness = byteBuffer.getInt();
		this.engineQuality = byteBuffer.getInt();
		this.keyId = byteBuffer.getInt();
		this.keySpawned = byteBuffer.get();
		this.headlightsOn = byteBuffer.get() == 1;
		this.bCreated = byteBuffer.get() == 1;
		this.soundHornOn = byteBuffer.get() == 1;
		this.soundBackMoveOn = byteBuffer.get() == 1;
		this.lightbarLightsMode.set(byteBuffer.get());
		this.lightbarSirenMode.set(byteBuffer.get());
		short short1 = byteBuffer.getShort();
		for (int int2 = 0; int2 < short1; ++int2) {
			VehiclePart vehiclePart = new VehiclePart(this);
			vehiclePart.load(byteBuffer, int1);
			this.parts.add(vehiclePart);
		}

		if (int1 >= 112) {
			this.keyIsOnDoor = byteBuffer.get() == 1;
			this.hotwired = byteBuffer.get() == 1;
			this.hotwiredBroken = byteBuffer.get() == 1;
			this.keysInIgnition = byteBuffer.get() == 1;
		}

		if (int1 >= 116) {
			this.rust = byteBuffer.getFloat();
			this.colorHue = byteBuffer.getFloat();
			this.colorSaturation = byteBuffer.getFloat();
			this.colorValue = byteBuffer.getFloat();
		}

		if (int1 >= 117) {
			this.enginePower = byteBuffer.getInt();
		}

		if (int1 >= 120) {
			byteBuffer.getShort();
		}

		String string;
		if (int1 >= 122) {
			string = GameWindow.ReadString(byteBuffer);
			this.mechanicalID = byteBuffer.getInt();
		}

		if (int1 >= 124) {
			this.alarmed = byteBuffer.get() == 1;
		}

		if (int1 >= 129) {
			this.sirenStartTime = byteBuffer.getDouble();
		}

		if (int1 >= 133 && byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
			byteBuffer.get();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
			inventoryItem.load(byteBuffer, int1, false);
			this.setCurrentKey(inventoryItem);
		}

		this.loaded = true;
	}

	public void softReset() {
		this.keySpawned = 0;
		this.keyIsOnDoor = false;
		this.keysInIgnition = false;
		this.currentKey = null;
		this.engineState = BaseVehicle.engineStateTypes.Idle;
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			if (vehiclePart.getItemContainer() != null) {
				vehiclePart.getItemContainer().clear();
				if (Rand.Next(100) <= 100) {
					this.randomizeContainer(vehiclePart);
				}

				vehiclePart.getItemContainer().setExplored(true);
			}
		}
	}

	public void trySpawnKey() {
		if (!GameClient.bClient) {
			if (this.script != null && this.script.getWheelCount() != 0) {
				if (this.keySpawned != 1) {
					if (SandboxOptions.getInstance().VehicleEasyUse.getValue()) {
						this.addKeyToGloveBox();
					} else {
						VehicleType vehicleType = VehicleType.getTypeFromName(this.getVehicleType());
						int int1 = vehicleType == null ? 70 : vehicleType.getChanceToSpawnKey();
						if (Rand.Next(100) <= int1) {
							this.addKeyToWorld();
						}

						this.keySpawned = 1;
					}
				}
			}
		}
	}

	public void brekingObjects() {
		if (this.isEngineRunning()) {
			float float1 = Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + 0.3F + 1.0F;
			int int1 = (int)Math.ceil((double)float1);
			int int2;
			for (int int3 = -int1; int3 < int1; ++int3) {
				for (int2 = -int1; int2 < int1; ++int2) {
					IsoGridSquare square = this.getCell().getGridSquare((double)(this.x + (float)int2), (double)(this.y + (float)int3), (double)this.z);
					if (square != null) {
						int int4;
						Vector2 vector2;
						for (int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (!(object instanceof IsoWorldInventoryObject)) {
								vector2 = null;
								if (!this.brekingObjectsList.contains(object) && object != null && object.getProperties() != null) {
									if (object.getProperties().Is("CarSlowFactor")) {
										vector2 = this.testCollisionWithObject(object, 0.3F);
									}

									if (vector2 != null) {
										this.brekingObjectsList.add(object);
										if (!GameClient.bClient) {
											object.Collision(vector2, this);
										}
									}

									if (object.getProperties().Is("HitByCar")) {
										vector2 = this.testCollisionWithObject(object, 0.3F);
									}

									if (vector2 != null && !GameClient.bClient) {
										object.Collision(vector2, this);
									}
								}
							}
						}

						IsoMovingObject movingObject;
						for (int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
							movingObject = (IsoMovingObject)square.getMovingObjects().get(int4);
							if (movingObject instanceof IsoZombie) {
								IsoZombie zombie = (IsoZombie)movingObject;
								if (zombie.isOnFloor() && (zombie.bCrawling || zombie.legsSprite.CurrentAnim != null && zombie.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
									this.testCollisionWithProneCharacter(movingObject, zombie.angle.x, zombie.angle.y, false);
								}

								zombie.setVehicle4TestCollision(this);
							}

							if (GameClient.bClient && movingObject instanceof IsoPlayer && movingObject != this.getDriver()) {
								IsoPlayer player = (IsoPlayer)movingObject;
								player.setVehicle4TestCollision(this);
							}
						}

						for (int4 = 0; int4 < square.getStaticMovingObjects().size(); ++int4) {
							movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int4);
							if (movingObject instanceof IsoDeadBody) {
								vector2 = movingObject.dir.ToVector();
								this.testCollisionWithProneCharacter(movingObject, vector2.x, vector2.y, true);
							}
						}
					}
				}
			}

			float float2 = -999.0F;
			for (int2 = 0; int2 < this.brekingObjectsList.size(); ++int2) {
				IsoObject object2 = (IsoObject)this.brekingObjectsList.get(int2);
				Vector2 vector22 = this.testCollisionWithObject(object2, 1.0F);
				if (vector22 != null && object2.getSquare().getObjects().contains(object2)) {
					if (float2 < object2.GetVehicleSlowFactor(this)) {
						float2 = object2.GetVehicleSlowFactor(this);
					}
				} else {
					this.brekingObjectsList.remove(object2);
					object2.UnCollision(this);
				}
			}

			if (float2 != -999.0F) {
				Bullet.brekingVehicle(this.VehicleID, float2);
			} else {
				Bullet.brekingVehicle(this.VehicleID, 0.0F);
			}
		}
	}

	public void damageObjects(float float1) {
		if (this.isEngineRunning()) {
			float float2 = Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + 0.3F + 1.0F;
			int int1 = (int)Math.ceil((double)float2);
			for (int int2 = -int1; int2 < int1; ++int2) {
				for (int int3 = -int1; int3 < int1; ++int3) {
					IsoGridSquare square = this.getCell().getGridSquare((double)(this.x + (float)int3), (double)(this.y + (float)int2), (double)this.z);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							Vector2 vector2 = null;
							if (object instanceof IsoTree) {
								vector2 = this.testCollisionWithObject(object, 2.0F);
								if (vector2 != null) {
									object.setRenderEffect(RenderEffectType.Hit_Tree_Shudder);
								}
							}

							if (vector2 == null && object instanceof IsoWindow) {
								vector2 = this.testCollisionWithObject(object, 1.0F);
							}

							if (vector2 == null && object.sprite != null && (object.sprite.getProperties().Is("HitByCar") || object.sprite.getProperties().Is("CarSlowFactor"))) {
								vector2 = this.testCollisionWithObject(object, 1.0F);
							}

							IsoGridSquare square2;
							if (vector2 == null) {
								square2 = this.getCell().getGridSquare((double)(this.x + (float)int3), (double)(this.y + (float)int2), 1.0);
								if (square2 != null && square.getProperties().getFlags() != null && square2.getHasTypes().isSet(IsoObjectType.lightswitch)) {
									vector2 = this.testCollisionWithObject(object, 1.0F);
								}
							}

							if (vector2 == null) {
								square2 = this.getCell().getGridSquare((double)(this.x + (float)int3), (double)(this.y + (float)int2), 0.0);
								if (square2 != null && square.getProperties().getFlags() != null && square2.getHasTypes().isSet(IsoObjectType.lightswitch)) {
									vector2 = this.testCollisionWithObject(object, 1.0F);
								}
							}

							if (vector2 != null) {
								object.Hit(vector2, this, float1);
							}
						}
					}
				}
			}
		}
	}

	public void update() {
		if (this.removedFromWorld) {
			DebugLog.log("vehicle update() removedFromWorld=true id=" + this.VehicleID);
		} else if (!this.getCell().vehicles.contains(this)) {
			DebugLog.log("vehicle update() not in cell.vehicles list x,y=" + this.x + "," + this.y + " " + this);
			this.getCell().getRemoveList().add(this);
		} else {
			if (this.chunk == null) {
				DebugLog.log("vehicle update() chunk=null x,y=" + this.x + "," + this.y + " id=" + this.VehicleID);
			} else if (!this.chunk.vehicles.contains(this)) {
				DebugLog.log("vehicle update() not in chunk.vehicles list x,y=" + this.x + "," + this.y + " id=" + this.VehicleID);
				if (GameClient.bClient) {
					VehicleManager.instance.sendReqestGetPosition(this.VehicleID);
				}
			} else if (!GameServer.bServer && this.chunk.refs.isEmpty()) {
				DebugLog.log("vehicle update() chunk was unloaded id=" + this.VehicleID);
				this.removeFromWorld();
				return;
			}

			super.update();
			if (GameClient.bClient && (this.netPlayerAuthorization == 1 || this.netPlayerAuthorization == 3) && GameClient.connection != null) {
				this.updatePhysicsNetwork();
			}

			if (this.physics != null) {
				this.doAlarm();
				BaseVehicle.VehicleImpulse vehicleImpulse = this.impulseFromServer;
				Vector3f vector3f;
				if (vehicleImpulse != null && vehicleImpulse.enable) {
					vehicleImpulse.enable = false;
					float float1 = 1.0F;
					Bullet.applyCentralForceToVehicle(this.VehicleID, vehicleImpulse.impulse.x * float1, vehicleImpulse.impulse.y * float1, vehicleImpulse.impulse.z * float1);
					vector3f = vehicleImpulse.rel_pos.cross(vehicleImpulse.impulse, this.tempVector3f_1);
					Bullet.applyTorqueToVehicle(this.VehicleID, vector3f.x * float1, vector3f.y * float1, vector3f.z * float1);
				}

				int int1;
				int int2;
				float float2;
				if (!this.impulseFromHitZombie.isEmpty()) {
					Vector3f vector3f2 = this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
					vector3f = this.tempVector3f_2.set(0.0F, 0.0F, 0.0F);
					int1 = this.impulseFromHitZombie.size();
					for (int2 = 0; int2 < int1; ++int2) {
						vehicleImpulse = (BaseVehicle.VehicleImpulse)this.impulseFromHitZombie.get(int2);
						vector3f2.add(vehicleImpulse.impulse);
						vector3f.add(vehicleImpulse.rel_pos.cross(vehicleImpulse.impulse, this.tempVector3f_3));
						vehicleImpulse.release();
					}

					float2 = 7.0F * this.getMass();
					if (vector3f2.lengthSquared() > float2 * float2) {
						vector3f2.mul(float2 / vector3f2.length());
					}

					this.impulseFromHitZombie.clear();
					float float3 = 30.0F;
					Bullet.applyCentralForceToVehicle(this.VehicleID, vector3f2.x * float3, vector3f2.y * float3, vector3f2.z * float3);
					Bullet.applyTorqueToVehicle(this.VehicleID, vector3f.x * float3, vector3f.y * float3, vector3f.z * float3);
					if (GameServer.bServer) {
					}
				}

				int int3;
				float float4;
				for (int3 = 0; int3 < this.impulseFromSquishedZombie.length; ++int3) {
					vehicleImpulse = this.impulseFromSquishedZombie[int3];
					if (vehicleImpulse != null && vehicleImpulse.enable) {
						float4 = 30.0F;
						Bullet.applyCentralForceToVehicle(this.VehicleID, vehicleImpulse.impulse.x * float4, vehicleImpulse.impulse.y * float4, vehicleImpulse.impulse.z * float4);
						Vector3f vector3f3 = vehicleImpulse.rel_pos.cross(vehicleImpulse.impulse, this.tempVector3f_1);
						Bullet.applyTorqueToVehicle(this.VehicleID, vector3f3.x * float4, vector3f3.y * float4, vector3f3.z * float4);
					}
				}

				if (System.currentTimeMillis() - this.engineCheckTime > 1000L && !GameClient.bClient) {
					this.engineCheckTime = System.currentTimeMillis();
					if (!GameClient.bClient) {
						if (this.engineState != BaseVehicle.engineStateTypes.Idle) {
							int3 = (int)((double)this.engineLoudness * this.engineSpeed / 2500.0);
							double double1 = Math.min(this.getEngineSpeed(), 2000.0);
							int3 = (int)((double)int3 * (1.0 + double1 / 4000.0));
							if (Rand.Next((int)(120.0F * GameTime.instance.getInvMultiplier())) == 0) {
								WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int3, int3 / 40, false);
							}

							if (Rand.Next((int)(35.0F * GameTime.instance.getInvMultiplier())) == 0) {
								WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int3 / 2, int3 / 40, false);
							}

							if (Rand.Next((int)(2.0F * GameTime.instance.getInvMultiplier())) == 0) {
								WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int3 / 4, int3 / 40, false);
							}

							WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int3 / 6, int3 / 40, false);
						}

						if (this.lightbarSirenMode.isEnable() && this.getBatteryCharge() > 0.0F) {
							WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 600, 60, false);
						}
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Running && !this.isEngineWorking()) {
						this.shutOff();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Running && this.getPartById("Engine").getCondition() < 50 && Rand.Next(this.getPartById("Engine").getCondition() * 12) == 0) {
						this.shutOff();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Starting) {
						VehiclePart vehiclePart = this.getPartById("GasTank");
						if (this.getBatteryCharge() <= 0.1F) {
							this.engineDoStartingFailedNoPower();
						} else if ((vehiclePart == null || !(vehiclePart.getContainerContentAmount() <= 0.0F)) && !(this.getBatteryCharge() <= 0.0F)) {
							int int4 = 0;
							if (this.engineQuality < 65 && ClimateManager.getInstance().getAirTemperatureForSquare(this.getSquare()) <= 2.0F) {
								int4 = Math.min(Math.abs((int)ClimateManager.getInstance().getAirTemperatureForSquare(this.getSquare()) - 2) * 2, 30);
							}

							if (!SandboxOptions.instance.VehicleEasyUse.getValue() && this.engineQuality < 100 && Rand.Next(100 - this.engineQuality + int4 + 50) <= 30) {
								this.engineDoStartingFailed();
							} else if (Rand.Next(this.engineQuality) != 0) {
								this.engineDoStartingSuccess();
							} else {
								this.engineDoRetryingStarting();
							}
						} else {
							this.engineDoStartingFailed();
						}
					}

					if (this.engineState == BaseVehicle.engineStateTypes.RetryingStarting && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
						this.engineDoStarting();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.StartingSuccess && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
						this.engineDoRunning();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.StartingFailed && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
						this.engineDoIdle();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.StartingFailedNoPower && System.currentTimeMillis() - this.engineLastUpdateStateTime > 500L) {
						this.engineDoIdle();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Stalling && System.currentTimeMillis() - this.engineLastUpdateStateTime > 3000L) {
						this.engineDoIdle();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.ShutingDown && System.currentTimeMillis() - this.engineLastUpdateStateTime > 2000L) {
						this.engineDoIdle();
					}
				}

				if (this.getCharacter(0) == null) {
					this.getController().park();
				}

				this.setX(this.jniTransform.origin.x + WorldSimulation.instance.offsetX);
				this.setY(this.jniTransform.origin.z + WorldSimulation.instance.offsetY);
				this.setZ(this.jniTransform.origin.y);
				IsoGridSquare square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
				if (square == null) {
					float4 = 5.0E-4F;
					int1 = this.chunk.wx * 10;
					int2 = this.chunk.wy * 10;
					int int5 = int1 + 10;
					int int6 = int2 + 10;
					float float5 = this.x;
					float float6 = this.y;
					this.x = Math.max(this.x, (float)int1 + float4);
					this.x = Math.min(this.x, (float)int5 - float4);
					this.y = Math.max(this.y, (float)int2 + float4);
					this.y = Math.min(this.y, (float)int6 - float4);
					this.z = 0.2F;
					Transform transform = this.tempTransform;
					Transform transform2 = this.tempTransform2;
					this.getWorldTransform(transform);
					transform2.basis.set((Matrix3fc)transform.basis);
					transform2.origin.set(this.x - WorldSimulation.instance.offsetX, this.z, this.y - WorldSimulation.instance.offsetY);
					this.setWorldTransform(transform2);
					this.current = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
				}

				if (this.current != null && this.current.chunk != null) {
					if (this.current.getChunk() != this.chunk) {
						assert this.chunk.vehicles.contains(this);
						this.chunk.vehicles.remove(this);
						this.chunk = this.current.getChunk();
						if (!GameServer.bServer && this.chunk.refs.isEmpty()) {
							DebugLog.log("BaseVehicle.update() added to unloaded chunk id=" + this.VehicleID);
						}

						assert !this.chunk.vehicles.contains(this);
						this.chunk.vehicles.add(this);
						IsoChunk.addFromCheckedVehicles(this);
					}
				} else {
					boolean boolean1 = false;
				}

				this.updateTransform();
				if (this.jniIsCollide) {
					this.jniIsCollide = false;
					vector3f = this.tempVector3f_1;
					if (GameServer.bServer) {
						vector3f.set((Vector3fc)this.netLinearVelocity);
					} else {
						vector3f.set((Vector3fc)this.jniLinearVelocity);
					}

					vector3f.negate();
					vector3f.add(this.lastLinearVelocity);
					float float7 = Math.abs(vector3f.length());
					if (float7 > 2.0F) {
						if (this.lastLinearVelocity.length() < 6.0F) {
							float7 /= 3.0F;
						}

						this.jniTransform.getRotation(this.tempQuat4f);
						this.tempQuat4f.invert(this.tempQuat4f);
						if (this.lastLinearVelocity.rotate(this.tempQuat4f).z < 0.0F) {
							float7 *= -1.0F;
						}

						if (Core.bDebug) {
							DebugLog.log("CRASH lastSpeed=" + this.lastLinearVelocity.length() + " speed=" + vector3f + " delta=" + float7 + " netLinearVelocity=" + this.netLinearVelocity.length());
						}

						float2 = vector3f.normalize().dot(this.getForwardVector(this.tempVector3f_2));
						this.crash(Math.abs(float7 * 3.0F), float2 > 0.0F);
						this.damageObjects(Math.abs(float7) * 30.0F);
					}
				}

				if (GameServer.bServer) {
					this.lastLinearVelocity.set((Vector3fc)this.netLinearVelocity);
				} else {
					this.lastLinearVelocity.set((Vector3fc)this.jniLinearVelocity);
				}
			}

			if (this.soundHornOn && this.hornemitter != null) {
				this.hornemitter.setPos(this.getX(), this.getY(), this.getZ());
			}

			int int7;
			for (int7 = 0; int7 < this.impulseFromSquishedZombie.length; ++int7) {
				BaseVehicle.VehicleImpulse vehicleImpulse2 = this.impulseFromSquishedZombie[int7];
				if (vehicleImpulse2 != null) {
					vehicleImpulse2.enable = false;
				}
			}

			this.updateSounds();
			this.brekingObjects();
			if (this.sprite != null && this.sprite.hasActiveModel()) {
				this.sprite.modelSlot.UpdateLights();
			}

			for (int7 = 0; int7 < IsoPlayer.numPlayers; ++int7) {
				if (this.current == null || !this.current.lighting[int7].bCanSee()) {
					this.targetAlpha[int7] = 0.0F;
				}

				IsoPlayer player = IsoPlayer.players[int7];
				if (player != null && this.DistToSquared(player) < 225.0F) {
					this.targetAlpha[int7] = 1.0F;
				}
			}

			for (int7 = 0; int7 < this.getScript().getPassengerCount(); ++int7) {
				if (this.getCharacter(int7) != null) {
					this.getPassengerWorldPos(int7, this.tempVector3f_1);
					this.getCharacter(int7).setX(this.tempVector3f_1.x);
					this.getCharacter(int7).setY(this.tempVector3f_1.y);
					this.getCharacter(int7).setZ(this.tempVector3f_1.z * 0.0F);
				}
			}

			if (!this.needPartsUpdate() && !this.isMechanicUIOpen()) {
				this.drainBatteryUpdateHack();
			} else {
				this.updateParts();
			}

			if (this.engineState == BaseVehicle.engineStateTypes.Running) {
				this.updateBulletStats();
			}

			if (this.bDoDamageOverlay) {
				this.bDoDamageOverlay = false;
				this.doDamageOverlay();
			}

			if (GameClient.bClient) {
				this.checkPhysicsValidWithServer();
			}
		}
	}

	private boolean isNullChunk(int int1, int int2) {
		if (!IsoWorld.instance.getMetaGrid().isValidChunk(int1, int2)) {
			return false;
		} else if (GameClient.bClient && !ClientServerMap.isChunkLoaded(int1, int2)) {
			return true;
		} else if (GameClient.bClient && !PassengerMap.isChunkLoaded(this, int1, int2)) {
			return true;
		} else {
			return this.getCell().getChunk(int1, int2) == null;
		}
	}

	public boolean isInvalidChunkAround() {
		Vector3f vector3f = this.getLinearVelocity(this.tempVector3f_1);
		float float1 = Math.abs(vector3f.x);
		float float2 = Math.abs(vector3f.z);
		boolean boolean1 = vector3f.x < 0.0F && float1 > float2;
		boolean boolean2 = vector3f.x > 0.0F && float1 > float2;
		boolean boolean3 = vector3f.z < 0.0F && float2 > float1;
		boolean boolean4 = vector3f.z > 0.0F && float2 > float1;
		if (IsoChunkMap.ChunkGridWidth <= 7) {
			if (IsoChunkMap.ChunkGridWidth <= 4) {
				return false;
			} else if (boolean2 && this.isNullChunk(this.chunk.wx + 1, this.chunk.wy)) {
				return true;
			} else if (boolean1 && this.isNullChunk(this.chunk.wx - 1, this.chunk.wy)) {
				return true;
			} else if (boolean4 && this.isNullChunk(this.chunk.wx, this.chunk.wy + 1)) {
				return true;
			} else if (boolean3 && this.isNullChunk(this.chunk.wx, this.chunk.wy - 1)) {
				return true;
			} else {
				return false;
			}
		} else if (!boolean2 || !this.isNullChunk(this.chunk.wx + 1, this.chunk.wy) && !this.isNullChunk(this.chunk.wx + 2, this.chunk.wy)) {
			if (boolean1 && (this.isNullChunk(this.chunk.wx - 1, this.chunk.wy) || this.isNullChunk(this.chunk.wx - 2, this.chunk.wy))) {
				return true;
			} else if (boolean4 && (this.isNullChunk(this.chunk.wx, this.chunk.wy + 1) || this.isNullChunk(this.chunk.wx, this.chunk.wy + 2))) {
				return true;
			} else if (!boolean3 || !this.isNullChunk(this.chunk.wx, this.chunk.wy - 1) && !this.isNullChunk(this.chunk.wx, this.chunk.wy - 2)) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public void postupdate() {
		this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, 0);
		if (this.current == null) {
			for (int int1 = (int)this.z; int1 >= 0; --int1) {
				this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, int1);
				if (this.current != null) {
					break;
				}
			}
		}

		if (this.movingSq != null) {
			this.movingSq.getMovingObjects().remove(this);
			this.movingSq = null;
		}

		if (this.current != null && !this.current.getMovingObjects().contains(this)) {
			this.current.getMovingObjects().add(this);
			this.movingSq = this.current;
		}

		this.square = this.current;
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		super.saveChange(string, kahluaTable, byteBuffer);
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		super.loadChange(string, byteBuffer);
	}

	public void authorizationClientForecast(boolean boolean1) {
		if (boolean1 && this.getDriver() == null) {
			this.netPlayerAuthorization = 1;
		}
	}

	public void authorizationServerUpdate() {
		if (this.netPlayerAuthorization == 1 && this.netPlayerTimeout-- < 1) {
			this.netPlayerAuthorization = 0;
			this.netPlayerId = -1;
			this.netPlayerTimeout = 0;
		}
	}

	public void authorizationServerCollide(int int1, boolean boolean1) {
		if (this.netPlayerAuthorization != 3) {
			if (boolean1) {
				this.netPlayerAuthorization = 1;
				this.netPlayerId = int1;
				this.netPlayerTimeout = 30;
			} else {
				this.netPlayerAuthorization = 0;
				this.netPlayerId = -1;
				this.netPlayerTimeout = 0;
			}
		}
	}

	public void authorizationServerOnSeat() {
		if (this.getDriver() != null) {
			this.netPlayerAuthorization = 3;
			this.netPlayerId = ((IsoPlayer)this.getDriver()).OnlineID;
			this.netPlayerTimeout = 30;
		} else {
			this.netPlayerAuthorization = 0;
			this.netPlayerId = -1;
		}
	}

	public boolean authorizationServerOnOwnerData(UdpConnection udpConnection) {
		boolean boolean1 = false;
		if (this.netPlayerAuthorization == 0) {
			return false;
		} else {
			for (int int1 = 0; int1 < udpConnection.players.length; ++int1) {
				if (udpConnection.players[int1] != null && udpConnection.players[int1].OnlineID == this.netPlayerId) {
					boolean1 = true;
					break;
				}
			}

			if (this.getDriver() != null) {
				this.netPlayerTimeout = 30;
			}

			return boolean1;
		}
	}

	public void netPlayerServerSendAuthorisation(ByteBuffer byteBuffer) {
		byteBuffer.put(this.netPlayerAuthorization);
		byteBuffer.putInt(this.netPlayerId);
	}

	public void netPlayerFromServerUpdate(byte byte1, int int1) {
		if (IsoPlayer.getPlayerIndex() >= 0 && IsoPlayer.players[IsoPlayer.getPlayerIndex()] != null && (byte1 != this.netPlayerAuthorization || this.netPlayerId != int1)) {
			if (byte1 == 3) {
				if (int1 == IsoPlayer.players[IsoPlayer.getPlayerIndex()].OnlineID) {
					this.netPlayerAuthorization = 3;
					this.netPlayerId = int1;
					Bullet.setVehicleStatic(this.VehicleID, false);
				} else if (this.netPlayerAuthorization != 4) {
					this.netPlayerAuthorization = 4;
					this.netPlayerId = int1;
					Bullet.setVehicleStatic(this.VehicleID, true);
				}
			} else if (byte1 == 1) {
				if (int1 == IsoPlayer.players[IsoPlayer.getPlayerIndex()].OnlineID) {
					this.netPlayerAuthorization = 1;
					this.netPlayerId = int1;
					Bullet.setVehicleStatic(this.VehicleID, false);
				} else if (this.netPlayerAuthorization != 2) {
					this.netPlayerAuthorization = 2;
					this.netPlayerId = int1;
					Bullet.setVehicleStatic(this.VehicleID, true);
				}
			} else {
				this.netPlayerAuthorization = 0;
				this.netPlayerId = -1;
				Bullet.setVehicleStatic(this.VehicleID, false);
			}
		}
	}

	public Transform getWorldTransform(Transform transform) {
		transform.set(this.jniTransform);
		return transform;
	}

	public void setWorldTransform(Transform transform) {
		this.jniTransform.set(transform);
		Quaternionf quaternionf = this.tempQuat4f;
		transform.getRotation(quaternionf);
		Bullet.teleportVehicle(this.VehicleID, transform.origin.x + WorldSimulation.instance.offsetX, transform.origin.z + WorldSimulation.instance.offsetY, transform.origin.y, quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w);
	}

	public void flipUpright() {
		Transform transform = this.tempTransform;
		transform.set(this.jniTransform);
		Quaternionf quaternionf = new Quaternionf();
		quaternionf.setAngleAxis(0.0F, _UNIT_Y.x, _UNIT_Y.y, _UNIT_Y.z);
		transform.setRotation(quaternionf);
		this.setWorldTransform(transform);
	}

	public PolygonalMap2.VehiclePoly getPoly() {
		if (this.polyDirty) {
			this.poly.init(this, 0.0F);
			this.polyPlusRadius.init(this, 0.3F);
			this.polyDirty = false;
			this.polyPlusRadiusMinX = -123.0F;
		}

		return this.poly;
	}

	public PolygonalMap2.VehiclePoly getPolyPlusRadius() {
		if (this.polyDirty) {
			this.poly.init(this, 0.0F);
			this.polyPlusRadius.init(this, 0.3F);
			this.polyDirty = false;
			this.polyPlusRadiusMinX = -123.0F;
		}

		return this.polyPlusRadius;
	}

	private void initPolyPlusRadiusBounds() {
		if (this.polyPlusRadiusMinX == -123.0F) {
			PolygonalMap2.VehiclePoly vehiclePoly = this.getPolyPlusRadius();
			Vector3f vector3f = this.tempVector3f_2;
			Vector3f vector3f2 = this.tempVector3f_3;
			Vector3f vector3f3 = this.getLocalPos(vector3f.set(vehiclePoly.x1, vehiclePoly.y1, vehiclePoly.z), vector3f2);
			float float1 = (float)((int)(vector3f3.x * 100.0F)) / 100.0F;
			float float2 = (float)((int)(vector3f3.z * 100.0F)) / 100.0F;
			vector3f3 = this.getLocalPos(vector3f.set(vehiclePoly.x2, vehiclePoly.y2, vehiclePoly.z), vector3f2);
			float float3 = (float)((int)(vector3f3.x * 100.0F)) / 100.0F;
			float float4 = (float)((int)(vector3f3.z * 100.0F)) / 100.0F;
			vector3f3 = this.getLocalPos(vector3f.set(vehiclePoly.x3, vehiclePoly.y3, vehiclePoly.z), vector3f2);
			float float5 = (float)((int)(vector3f3.x * 100.0F)) / 100.0F;
			float float6 = (float)((int)(vector3f3.z * 100.0F)) / 100.0F;
			vector3f3 = this.getLocalPos(vector3f.set(vehiclePoly.x4, vehiclePoly.y4, vehiclePoly.z), vector3f2);
			float float7 = (float)((int)(vector3f3.x * 100.0F)) / 100.0F;
			float float8 = (float)((int)(vector3f3.z * 100.0F)) / 100.0F;
			this.polyPlusRadiusMinX = Math.min(float1, Math.min(float3, Math.min(float5, float7)));
			this.polyPlusRadiusMaxX = Math.max(float1, Math.max(float3, Math.max(float5, float7)));
			this.polyPlusRadiusMinY = Math.min(float2, Math.min(float4, Math.min(float6, float8)));
			this.polyPlusRadiusMaxY = Math.max(float2, Math.max(float4, Math.max(float6, float8)));
		}
	}

	public Vector3f getForwardVector(Vector3f vector3f) {
		byte byte1 = 2;
		return this.jniTransform.basis.getColumn(byte1, vector3f);
	}

	public float getCurrentSpeedKmHour() {
		return this.jniSpeed;
	}

	public Vector3f getLinearVelocity(Vector3f vector3f) {
		if (GameServer.bServer) {
			vector3f.set((Vector3fc)this.netLinearVelocity);
			return vector3f;
		} else {
			vector3f.set((Vector3fc)this.jniLinearVelocity);
			return vector3f;
		}
	}

	private void updateTransform() {
		if (this.sprite.modelSlot != null) {
			Vector3f vector3f = this.getForwardVector(this.tempVector3f_1);
			float float1 = (float)(-Math.atan2((double)vector3f.z, (double)vector3f.x));
			float float2 = this.getScript().getModelScale();
			Transform transform = this.getWorldTransform(this.tempTransform);
			transform.getRotation(this.tempQuat4f);
			float float3 = this.getScript().getModel().offset.y() * (1.0F - Math.abs(this.tempQuat4f.x()));
			LEMMY_FLIP_FIX = false;
			transform.origin.x = transform.origin.y = transform.origin.z = 0.0F;
			Vector3f vector3f2 = transform.origin;
			vector3f2.y += float3;
			transform.getMatrix(this.renderTransform);
			if (!LEMMY_FLIP_FIX) {
				this.renderTransform.transpose();
			}

			for (int int1 = 0; int1 < this.models.size(); ++int1) {
				BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int1);
				if (modelInfo.wheelIndex == -1) {
					VehicleScript.Model model = modelInfo.scriptModel;
					modelInfo.renderOrigin.set(model.offset.x + this.script.getCenterOfMassOffset().x(), model.offset.y + this.script.getCenterOfMassOffset().y(), model.offset.z + this.script.getCenterOfMassOffset().z());
					Matrix4f matrix4f = this.tempMatrix4fLWJGL_1;
					this.tempMatrix4f.translation(modelInfo.renderOrigin).transpose();
					matrix4f.mulGeneric(this.tempMatrix4f, matrix4f);
					modelInfo.renderTransform.scaling(float2);
					matrix4f.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
					modelInfo.renderTransform.mulGeneric(this.renderTransform, modelInfo.renderTransform);
				} else {
					BaseVehicle.WheelInfo wheelInfo = this.wheelInfo[modelInfo.wheelIndex];
					float float4 = -wheelInfo.steering;
					if (LEMMY_FLIP_FIX) {
						float4 *= -1.0F;
					}

					float float5 = -wheelInfo.rotation;
					VehicleScript.Wheel wheel = this.getScript().getWheel(modelInfo.wheelIndex);
					BaseVehicle.VehicleImpulse vehicleImpulse = modelInfo.wheelIndex < this.impulseFromSquishedZombie.length ? this.impulseFromSquishedZombie[modelInfo.wheelIndex] : null;
					float float6 = vehicleImpulse != null && vehicleImpulse.enable ? 0.05F : 0.0F;
					if (wheelInfo.suspensionLength == 0.0F) {
						modelInfo.renderOrigin.set(wheel.offset.x, wheel.offset.y - this.script.getSuspensionRestLength() / 3.0F - this.script.getCenterOfMassOffset().y() + float3 / float2, wheel.offset.z);
					} else {
						modelInfo.renderOrigin.set(wheel.offset.x, wheel.offset.y - wheelInfo.suspensionLength / 2.0F - this.script.getCenterOfMassOffset().y() + (float3 + float6) / float2, wheel.offset.z);
					}

					this.setModelTransform(modelInfo, float1, float4, float5, float2);
					modelInfo.renderTransform.mulGeneric(this.renderTransform, modelInfo.renderTransform);
				}
			}

			this.renderTransform.scale(float2);
		}
	}

	public void serverUpdateSimulatorState() {
		if (Math.abs(this.physics.clientForce) > 0.01F && !this.physics.isEnable) {
			Bullet.setVehicleActive(this.VehicleID, true);
			this.physics.isEnable = true;
		}

		if (this.physics.isEnable && Math.abs(this.physics.clientForce) < 0.01F && this.jniSpeed < 0.01F) {
			Bullet.setVehicleActive(this.VehicleID, false);
			this.physics.isEnable = false;
		}
	}

	public void updatePhysics() {
		this.physics.update();
	}

	public void updatePhysicsNetwork() {
		if (this.limitPhysicSend.Check()) {
			VehicleManager.instance.sendPhysic(this);
		}
	}

	public void checkPhysicsValidWithServer() {
		if (this.limitPhysicValid.Check()) {
			float[] floatArray = vehicleParams;
			float float1 = 0.05F;
			if (Bullet.getOwnVehiclePhysics(this.VehicleID, floatArray) == 0 && (Math.abs(floatArray[0] - this.x) > float1 || Math.abs(floatArray[1] - this.y) > float1 || Math.abs(floatArray[2] - this.z) > float1)) {
				VehicleManager.instance.sendReqestGetPosition(this.VehicleID);
			}
		}
	}

	public void updateControls() {
		if (this.isOperational()) {
			if (this.getDriver() == null || !(this.getDriver() instanceof IsoPlayer) || !((IsoPlayer)this.getDriver()).isBlockMovement()) {
				if (this.getController() != null) {
					this.getController().updateControls();
				}
			}
		}
	}

	public boolean isKeyboardControlled() {
		IsoGameCharacter gameCharacter = this.getCharacter(0);
		return gameCharacter != null && gameCharacter == IsoPlayer.players[0];
	}

	public int getJoypad() {
		IsoGameCharacter gameCharacter = this.getCharacter(0);
		return gameCharacter != null && gameCharacter instanceof IsoPlayer ? ((IsoPlayer)gameCharacter).JoypadBind : -1;
	}

	public void Damage(float float1) {
		this.crash(float1, true);
	}

	public void crash(float float1, boolean boolean1) {
		if (GameClient.bClient) {
			SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0F, 20.0F, 1.0F, true);
			GameClient.instance.sendClientCommandV((IsoPlayer)null, "vehicle", "crash", "vehicle", this.getId(), "amount", float1, "front", boolean1);
		} else {
			float float2 = 1.3F;
			float float3 = float1;
			switch (SandboxOptions.instance.CarDamageOnImpact.getValue()) {
			case 1: 
				float2 = 1.9F;
				break;
			
			case 2: 
				float2 = 1.6F;
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				float2 = 1.1F;
				break;
			
			case 5: 
				float2 = 0.9F;
			
			}

			float1 = Math.abs(float1) / float2;
			if (boolean1) {
				this.addDamageFront((int)float1);
			} else {
				this.addDamageRear((int)Math.abs(float1 / float2));
			}

			this.damagePlayers(Math.abs(float3));
			SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0F, 20.0F, 1.0F, true);
		}
	}

	private void addDamageFrontHitAChr(int int1) {
		VehiclePart vehiclePart = this.getPartById("EngineDoor");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		if (int1 > 12) {
			vehiclePart = this.getPartById("Windshield");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}
	}

	private void addDamageRearHitAChr(int int1) {
		VehiclePart vehiclePart = this.getPartById("TruckBed");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.setCondition(vehiclePart.getCondition() - Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			vehiclePart.doInventoryItemStats(vehiclePart.getInventoryItem(), 0);
			this.transmitPartCondition(vehiclePart);
		}

		vehiclePart = this.getPartById("DoorRear");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		vehiclePart = this.getPartById("TrunkDoor");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		if (int1 > 12) {
			vehiclePart = this.getPartById("WindshieldRear");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(int1);
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightRearLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightRearRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}
	}

	private void addDamageFront(int int1) {
		this.currentFrontEndDurability -= int1;
		VehiclePart vehiclePart = this.getPartById("EngineDoor");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		vehiclePart = this.getPartById("Windshield");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		if (Rand.Next(4) == 0) {
			vehiclePart = this.getPartById("DoorFrontLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}

			vehiclePart = this.getPartById("WindowFrontLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}

		if (Rand.Next(4) == 0) {
			vehiclePart = this.getPartById("DoorFrontRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}

			vehiclePart = this.getPartById("WindowFrontRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}

		if (Rand.Next(45) < int1) {
			vehiclePart = this.getPartById("Muffler");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 30), int1 - 20));
			}
		}
	}

	private void addDamageRear(int int1) {
		this.currentRearEndDurability -= int1;
		VehiclePart vehiclePart = this.getPartById("TruckBed");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.setCondition(vehiclePart.getCondition() - Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			vehiclePart.doInventoryItemStats(vehiclePart.getInventoryItem(), 0);
			this.transmitPartCondition(vehiclePart);
		}

		vehiclePart = this.getPartById("DoorRear");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		vehiclePart = this.getPartById("TrunkDoor");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		vehiclePart = this.getPartById("WindshieldRear");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(int1);
		}

		if (Rand.Next(4) == 0) {
			vehiclePart = this.getPartById("DoorRearLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}

			vehiclePart = this.getPartById("WindowRearLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}

		if (Rand.Next(4) == 0) {
			vehiclePart = this.getPartById("DoorRearRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}

			vehiclePart = this.getPartById("WindowRearRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightRearLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("HeadlightRearRight");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
			}
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("Muffler");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}
	}

	private float clamp(float float1, float float2, float float3) {
		if (float1 < float2) {
			float1 = float2;
		}

		if (float1 > float3) {
			float1 = float3;
		}

		return float1;
	}

	public boolean isCharacterAdjacentTo(IsoGameCharacter gameCharacter) {
		if ((int)gameCharacter.z != (int)this.z) {
			return false;
		} else {
			Transform transform = this.getWorldTransform(this.tempTransform);
			transform.inverse();
			Vector3f vector3f = this.tempVector3f_1;
			vector3f.set(gameCharacter.x - WorldSimulation.instance.offsetX, 0.0F, gameCharacter.y - WorldSimulation.instance.offsetY);
			transform.transform(vector3f);
			float float1 = -this.script.getExtents().x / 2.0F;
			float float2 = this.script.getExtents().x / 2.0F;
			float float3 = -this.script.getExtents().z / 2.0F;
			float float4 = this.script.getExtents().z / 2.0F;
			this.initPolyPlusRadiusBounds();
			float1 = this.polyPlusRadiusMinX * this.script.getModelScale() + 0.3F;
			float2 = this.polyPlusRadiusMaxX * this.script.getModelScale() - 0.3F;
			float3 = this.polyPlusRadiusMinY * this.script.getModelScale() + 0.3F;
			float4 = this.polyPlusRadiusMaxY * this.script.getModelScale() - 0.3F;
			return vector3f.x >= float1 - 0.4F && vector3f.x < float2 + 0.4F && vector3f.z >= float3 - 0.4F && vector3f.z < float4 + 0.4F;
		}
	}

	public Vector2 testCollisionWithCharacter(IsoGameCharacter gameCharacter, float float1) {
		if (this.physics == null) {
			return null;
		} else if (this.DistToProper(gameCharacter) > Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + float1 + 1.0F) {
			return null;
		} else {
			Vector3f vector3f = this.tempVector3f_1;
			vector3f.set(gameCharacter.nx, gameCharacter.ny, 0.0F);
			this.getLocalPosUnscaled(vector3f, vector3f);
			float float2 = -this.script.getExtents().x / 2.0F;
			float float3 = this.script.getExtents().x / 2.0F;
			float float4 = -this.script.getExtents().z / 2.0F;
			float float5 = this.script.getExtents().z / 2.0F;
			float float6;
			float float7;
			float float8;
			float float9;
			Vector3f vector3f2;
			if (vector3f.x > float2 && vector3f.x < float3 && vector3f.z > float4 && vector3f.z < float5) {
				float6 = vector3f.x - float2;
				float7 = float3 - vector3f.x;
				float8 = vector3f.z - float4;
				float9 = float5 - vector3f.z;
				if (float6 < float7 && float6 < float8 && float6 < float9) {
					this.tempVector3f_1.set(float2 - float1 - 0.015F, 0.0F, vector3f.z);
				} else if (float7 < float6 && float7 < float8 && float7 < float9) {
					this.tempVector3f_1.set(float3 + float1 + 0.015F, 0.0F, vector3f.z);
				} else if (float8 < float6 && float8 < float7 && float8 < float9) {
					this.tempVector3f_1.set(vector3f.x, 0.0F, float4 - float1 - 0.015F);
				} else if (float9 < float6 && float9 < float7 && float9 < float8) {
					this.tempVector3f_1.set(vector3f.x, 0.0F, float5 + float1 + 0.015F);
				}

				Transform transform = this.getWorldTransform(this.tempTransform);
				transform.origin.set(0.0F, 0.0F, 0.0F);
				transform.transform(this.tempVector3f_1);
				vector3f2 = this.tempVector3f_1;
				vector3f2.x += this.getX();
				vector3f2 = this.tempVector3f_1;
				vector3f2.z += this.getY();
				this.collideX = this.tempVector3f_1.x;
				this.collideY = this.tempVector3f_1.z;
				return this.tempVector2.set(this.tempVector3f_1.x, this.tempVector3f_1.z);
			} else {
				float6 = this.clamp(vector3f.x, float2, float3);
				float7 = this.clamp(vector3f.z, float4, float5);
				float8 = vector3f.x - float6;
				float9 = vector3f.z - float7;
				float float10 = float8 * float8 + float9 * float9;
				if (float10 < float1 * float1) {
					if (float8 == 0.0F && float9 == 0.0F) {
						return this.tempVector2.set(-1.0F, -1.0F);
					} else {
						this.tempVector3f_1.set(float8, 0.0F, float9);
						this.tempVector3f_1.normalize();
						this.tempVector3f_1.mul(float1 + 0.015F);
						vector3f2 = this.tempVector3f_1;
						vector3f2.x += float6;
						vector3f2 = this.tempVector3f_1;
						vector3f2.z += float7;
						Transform transform2 = this.getWorldTransform(this.tempTransform);
						transform2.origin.set(0.0F, 0.0F, 0.0F);
						transform2.transform(this.tempVector3f_1);
						vector3f2 = this.tempVector3f_1;
						vector3f2.x += this.getX();
						vector3f2 = this.tempVector3f_1;
						vector3f2.z += this.getY();
						this.collideX = this.tempVector3f_1.x;
						this.collideY = this.tempVector3f_1.z;
						return this.tempVector2.set(this.tempVector3f_1.x, this.tempVector3f_1.z);
					}
				} else {
					return null;
				}
			}
		}
	}

	public int testCollisionWithProneCharacter(IsoMovingObject movingObject, float float1, float float2, boolean boolean1) {
		if (this.physics == null) {
			return 0;
		} else {
			float float3 = 0.3F;
			if (this.DistToProper(movingObject) > Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + float3 + 1.0F) {
				return 0;
			} else {
				float float4 = movingObject.x + float1 * 0.5F;
				float float5 = movingObject.y + float2 * 0.5F;
				float float6 = movingObject.x - float1 * 0.65F;
				float float7 = movingObject.y - float2 * 0.65F;
				int int1 = 0;
				for (int int2 = 0; int2 < this.script.getWheelCount(); ++int2) {
					VehicleScript.Wheel wheel = this.script.getWheel(int2);
					boolean boolean2 = true;
					for (int int3 = 0; int3 < this.models.size(); ++int3) {
						BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int3);
						if (modelInfo.wheelIndex == int2) {
							this.tempVector3f_1.set((Vector3fc)wheel.offset);
							Vector3f vector3f = this.tempVector3f_1;
							vector3f.y -= this.wheelInfo[int2].suspensionLength / this.script.getModelScale();
							this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1);
							if (this.tempVector3f_1.z > this.script.getWheel(int2).radius + 0.05F) {
								boolean2 = false;
							}

							break;
						}
					}

					if (boolean2) {
						Vector3f vector3f2 = this.tempVector3f_1.set(wheel.offset.x, wheel.offset.y, wheel.offset.z);
						this.getWorldPos(vector3f2, vector3f2);
						float float8 = vector3f2.x;
						float float9 = vector3f2.y;
						double double1 = (double)((float8 - float6) * (float4 - float6) + (float9 - float7) * (float5 - float7)) / (Math.pow((double)(float4 - float6), 2.0) + Math.pow((double)(float5 - float7), 2.0));
						float float10;
						float float11;
						if (double1 <= 0.0) {
							float10 = float6;
							float11 = float7;
						} else if (double1 >= 1.0) {
							float10 = float4;
							float11 = float5;
						} else {
							float10 = float6 + (float4 - float6) * (float)double1;
							float11 = float7 + (float5 - float7) * (float)double1;
						}

						if (!(IsoUtils.DistanceToSquared(vector3f2.x, vector3f2.y, float10, float11) > wheel.radius * wheel.radius)) {
							if (boolean1 && this.jniSpeed > 10.0F) {
								if (GameServer.bServer && movingObject instanceof IsoZombie) {
									((IsoZombie)movingObject).thumpFlag = 1;
								} else {
									SoundManager.instance.PlayWorldSound("VehicleRunOverBody", movingObject.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
								}
							}

							if (int2 < this.impulseFromSquishedZombie.length) {
								if (this.impulseFromSquishedZombie[int2] == null) {
									this.impulseFromSquishedZombie[int2] = new BaseVehicle.VehicleImpulse();
								}

								this.impulseFromSquishedZombie[int2].impulse.set(0.0F, 1.0F, 0.0F);
								float float12 = Math.max(this.jniSpeed, 20.0F) / 20.0F;
								this.impulseFromSquishedZombie[int2].impulse.mul(0.065F * this.getMass() * float12);
								this.impulseFromSquishedZombie[int2].rel_pos.set(vector3f2.x - this.x, 0.0F, vector3f2.y - this.y);
								this.impulseFromSquishedZombie[int2].enable = true;
								++int1;
							}
						}
					}
				}

				return int1;
			}
		}
	}

	public Vector2 testCollisionWithObject(IsoObject object, float float1) {
		if (this.physics == null) {
			return null;
		} else if (object.square == null) {
			return null;
		} else if (this.DistToProper(object) > Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + float1 + 1.0F) {
			return null;
		} else {
			Vector3f vector3f = this.tempVector3f_1;
			vector3f.set(object.getX(), object.getY(), 0.0F);
			this.getLocalPosUnscaled(vector3f, vector3f);
			float float2 = -this.script.getExtents().x / 2.0F;
			float float3 = this.script.getExtents().x / 2.0F;
			float float4 = -this.script.getExtents().z / 2.0F;
			float float5 = this.script.getExtents().z / 2.0F;
			float float6;
			float float7;
			float float8;
			float float9;
			Vector3f vector3f2;
			if (vector3f.x > float2 && vector3f.x < float3 && vector3f.z > float4 && vector3f.z < float5) {
				float6 = vector3f.x - float2;
				float7 = float3 - vector3f.x;
				float8 = vector3f.z - float4;
				float9 = float5 - vector3f.z;
				if (float6 < float7 && float6 < float8 && float6 < float9) {
					this.tempVector3f_1.set(float2 - float1 - 0.015F, 0.0F, vector3f.z);
				} else if (float7 < float6 && float7 < float8 && float7 < float9) {
					this.tempVector3f_1.set(float3 + float1 + 0.015F, 0.0F, vector3f.z);
				} else if (float8 < float6 && float8 < float7 && float8 < float9) {
					this.tempVector3f_1.set(vector3f.x, 0.0F, float4 - float1 - 0.015F);
				} else if (float9 < float6 && float9 < float7 && float9 < float8) {
					this.tempVector3f_1.set(vector3f.x, 0.0F, float5 + float1 + 0.015F);
				}

				Transform transform = this.getWorldTransform(this.tempTransform);
				transform.origin.set(0.0F, 0.0F, 0.0F);
				transform.transform(this.tempVector3f_1);
				vector3f2 = this.tempVector3f_1;
				vector3f2.x += this.getX();
				vector3f2 = this.tempVector3f_1;
				vector3f2.z += this.getY();
				this.collideX = this.tempVector3f_1.x;
				this.collideY = this.tempVector3f_1.z;
				return this.tempVector2.set(this.tempVector3f_1.x, this.tempVector3f_1.z);
			} else {
				float6 = this.clamp(vector3f.x, float2, float3);
				float7 = this.clamp(vector3f.z, float4, float5);
				float8 = vector3f.x - float6;
				float9 = vector3f.z - float7;
				float float10 = float8 * float8 + float9 * float9;
				if (float10 < float1 * float1) {
					if (float8 == 0.0F && float9 == 0.0F) {
						return this.tempVector2.set(-1.0F, -1.0F);
					} else {
						this.tempVector3f_1.set(float8, 0.0F, float9);
						this.tempVector3f_1.normalize();
						this.tempVector3f_1.mul(float1 + 0.015F);
						vector3f2 = this.tempVector3f_1;
						vector3f2.x += float6;
						vector3f2 = this.tempVector3f_1;
						vector3f2.z += float7;
						Transform transform2 = this.getWorldTransform(this.tempTransform);
						transform2.origin.set(0.0F, 0.0F, 0.0F);
						transform2.transform(this.tempVector3f_1);
						vector3f2 = this.tempVector3f_1;
						vector3f2.x += this.getX();
						vector3f2 = this.tempVector3f_1;
						vector3f2.z += this.getY();
						this.collideX = this.tempVector3f_1.x;
						this.collideY = this.tempVector3f_1.z;
						return this.tempVector2.set(this.tempVector3f_1.x, this.tempVector3f_1.z);
					}
				} else {
					return null;
				}
			}
		}
	}

	public boolean testCollisionWithVehicle(BaseVehicle baseVehicle) {
		VehicleScript vehicleScript = this.script;
		if (vehicleScript == null) {
			vehicleScript = ScriptManager.instance.getVehicle(this.scriptName);
		}

		VehicleScript vehicleScript2 = baseVehicle.script;
		if (vehicleScript2 == null) {
			vehicleScript2 = ScriptManager.instance.getVehicle(baseVehicle.scriptName);
		}

		if (testVecs1[0] == null) {
			for (int int1 = 0; int1 < testVecs1.length; ++int1) {
				testVecs1[int1] = new Vector2();
				testVecs2[int1] = new Vector2();
			}
		}

		float float1 = 1.0F / vehicleScript.getModelScale() / 2.0F;
		this.tempVector3f_1.set(vehicleScript.getExtents().x * float1, 0.0F, vehicleScript.getExtents().z * float1);
		this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript);
		testVecs1[0].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		this.tempVector3f_1.set(-vehicleScript.getExtents().x * float1, 0.0F, vehicleScript.getExtents().z * float1);
		this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript);
		testVecs1[1].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		this.tempVector3f_1.set(-vehicleScript.getExtents().x * float1, 0.0F, -vehicleScript.getExtents().z * float1);
		this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript);
		testVecs1[2].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		this.tempVector3f_1.set(vehicleScript.getExtents().x * float1, 0.0F, -vehicleScript.getExtents().z * float1);
		this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript);
		testVecs1[3].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		float1 = 1.0F / vehicleScript2.getModelScale() / 2.0F;
		this.tempVector3f_1.set(vehicleScript2.getExtents().x * float1, 0.0F, vehicleScript2.getExtents().z * float1);
		baseVehicle.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript2);
		testVecs2[0].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		this.tempVector3f_1.set(-vehicleScript2.getExtents().x * float1, 0.0F, vehicleScript2.getExtents().z * float1);
		baseVehicle.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript2);
		testVecs2[1].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		this.tempVector3f_1.set(-vehicleScript2.getExtents().x * float1, 0.0F, -vehicleScript2.getExtents().z * float1);
		baseVehicle.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript2);
		testVecs2[2].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		this.tempVector3f_1.set(vehicleScript2.getExtents().x * float1, 0.0F, -vehicleScript2.getExtents().z * float1);
		baseVehicle.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, vehicleScript2);
		testVecs2[3].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
		return QuadranglesIntersection.IsQuadranglesAreIntersected(testVecs1, testVecs2);
	}

	public void ApplyImpulse(IsoObject object, float float1) {
		BaseVehicle.VehicleImpulse vehicleImpulse = BaseVehicle.VehicleImpulse.alloc();
		vehicleImpulse.impulse.set(this.x - object.getX(), 0.0F, this.y - object.getY());
		vehicleImpulse.impulse.normalize();
		vehicleImpulse.impulse.mul(float1);
		vehicleImpulse.rel_pos.set(object.getX() - this.x, 0.0F, object.getY() - this.y);
		this.impulseFromHitZombie.add(vehicleImpulse);
	}

	public void hitCharacter(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getStateMachine().getCurrent() != StaggerBackState.instance() && gameCharacter.getStateMachine().getCurrent() != StaggerBackDieState.instance() && gameCharacter.getStateMachine().getCurrent() != JustDieState.instance()) {
			if (GameClient.bClient) {
				if (gameCharacter.clientIgnoreCollision + 400L > System.currentTimeMillis()) {
					return;
				}

				if (gameCharacter.legsSprite.CurrentAnim != null && (gameCharacter.legsSprite.CurrentAnim.name.equals("ZombieStaggerBack") || gameCharacter.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
					return;
				}
			}

			if (!(Math.abs(gameCharacter.x - this.x) < 0.01F) && !(Math.abs(gameCharacter.y - this.y) < 0.01F)) {
				float float1 = 15.0F;
				Vector3f vector3f = this.getLinearVelocity(this.tempVector3f_1);
				vector3f.y = 0.0F;
				float float2 = vector3f.length();
				float2 = Math.min(float2, float1);
				if (!(float2 < 0.05F)) {
					if (GameServer.bServer) {
						if (gameCharacter instanceof IsoZombie) {
							((IsoZombie)gameCharacter).thumpFlag = 1;
						}
					} else if (!GameClient.bClient) {
						SoundManager.instance.PlayWorldSound("VehicleHitCharacter", gameCharacter.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
					}

					Vector3f vector3f2 = this.tempVector3f_2;
					vector3f2.set(this.x - gameCharacter.x, 0.0F, this.y - gameCharacter.y);
					vector3f2.normalize();
					vector3f.normalize();
					float float3 = vector3f.dot(vector3f2);
					vector3f.mul(float2);
					if (float3 < 0.0F && !GameServer.bServer) {
						this.ApplyImpulse(gameCharacter, this.getMass() * 7.0F * float2 / float1 * Math.abs(float3));
					}

					if (GameClient.bClient) {
						gameCharacter.clientIgnoreCollision = System.currentTimeMillis();
					} else {
						vector3f2.normalize();
						vector3f2.mul(3.0F * float2 / float1);
						gameCharacter.Hit(this, float2 + this.physics.clientForce / this.getMass(), float3, this.tempVector2.set(-vector3f2.x, -vector3f2.z));
						if (this.getCurrentSpeedKmHour() >= 30.0F) {
							float2 = this.getCurrentSpeedKmHour() / 5.0F;
							if (float2 > 0.0F) {
								this.addDamageFrontHitAChr((int)float2);
							} else {
								this.addDamageRearHitAChr(Math.abs((int)float2));
							}
						}
					}
				}
			}
		}
	}

	public boolean blocked(int int1, int int2, int int3) {
		if (!this.removedFromWorld && this.current != null && this.getController() != null) {
			if (this.getController() == null) {
				return false;
			} else if (int3 != (int)this.getZ()) {
				return false;
			} else if (IsoUtils.DistanceTo2D((float)int1 + 0.5F, (float)int2 + 0.5F, this.x, this.y) > 5.0F) {
				return false;
			} else {
				float float1 = 0.3F;
				Transform transform = this.tempTransform3;
				this.getWorldTransform(transform);
				transform.inverse();
				Vector3f vector3f = this.tempVector3f_3;
				vector3f.set((float)int1 + 0.5F - WorldSimulation.instance.offsetX, 0.0F, (float)int2 + 0.5F - WorldSimulation.instance.offsetY);
				transform.transform(vector3f);
				float float2 = this.clamp(vector3f.x, -this.script.getExtents().x / 2.0F, this.script.getExtents().x / 2.0F);
				float float3 = this.clamp(vector3f.z, -this.script.getExtents().z / 2.0F, this.script.getExtents().z / 2.0F);
				float float4 = vector3f.x - float2;
				float float5 = vector3f.z - float3;
				float float6 = float4 * float4 + float5 * float5;
				return float6 < float1 * float1;
			}
		} else {
			return false;
		}
	}

	public boolean isIntersectingSquare(int int1, int int2, int int3) {
		if (int3 != (int)this.getZ()) {
			return false;
		} else if (!this.removedFromWorld && this.current != null && this.getController() != null) {
			tempPoly.x1 = tempPoly.x4 = (float)int1;
			tempPoly.y1 = tempPoly.y2 = (float)int2;
			tempPoly.x2 = tempPoly.x3 = (float)(int1 + 1);
			tempPoly.y3 = tempPoly.y4 = (float)(int2 + 1);
			return PolyPolyIntersect.intersects(tempPoly, this.getPoly());
		} else {
			return false;
		}
	}

	public boolean isIntersectingSquareWithShadow(int int1, int int2, int int3) {
		if (int3 != (int)this.getZ()) {
			return false;
		} else if (!this.removedFromWorld && this.current != null && this.getController() != null) {
			tempPoly.x1 = tempPoly.x4 = (float)int1;
			tempPoly.y1 = tempPoly.y2 = (float)int2;
			tempPoly.x2 = tempPoly.x3 = (float)(int1 + 1);
			tempPoly.y3 = tempPoly.y4 = (float)(int2 + 1);
			return PolyPolyIntersect.intersects(tempPoly, this.shadowCoord);
		} else {
			return false;
		}
	}

	public boolean circleIntersects(float float1, float float2, float float3, float float4) {
		if (this.getController() == null) {
			return false;
		} else if ((int)float3 != (int)this.getZ()) {
			return false;
		} else if (IsoUtils.DistanceTo2D(float1, float2, this.x, this.y) > 5.0F) {
			return false;
		} else {
			Vector3f vector3f = this.tempVector3f_3;
			vector3f.set(float1, float2, float3);
			this.getLocalPosUnscaled(vector3f, vector3f);
			float float5 = -this.script.getExtents().x / 2.0F;
			float float6 = this.script.getExtents().x / 2.0F;
			float float7 = -this.script.getExtents().z / 2.0F;
			float float8 = this.script.getExtents().z / 2.0F;
			if (vector3f.x > float5 && vector3f.x < float6 && vector3f.z > float7 && vector3f.z < float8) {
				return true;
			} else {
				float float9 = this.clamp(vector3f.x, float5, float6);
				float float10 = this.clamp(vector3f.z, float7, float8);
				float float11 = vector3f.x - float9;
				float float12 = vector3f.z - float10;
				float float13 = float11 * float11 + float12 * float12;
				return float13 < float4 * float4;
			}
		}
	}

	public void updateLights() {
		this.sprite.modelSlot.model.textureRustA = this.rust;
		if (this.script.getWheelCount() == 0) {
			this.sprite.modelSlot.model.textureRustA = 0.0F;
		}

		this.sprite.modelSlot.model.painColor.x = this.colorHue;
		this.sprite.modelSlot.model.painColor.y = this.colorSaturation;
		this.sprite.modelSlot.model.painColor.z = this.colorValue;
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		VehiclePart vehiclePart = this.getPartById("HeadlightLeft");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			boolean1 = true;
		}

		vehiclePart = this.getPartById("HeadlightRight");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			boolean2 = true;
		}

		vehiclePart = this.getPartById("HeadlightRearLeft");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			boolean4 = true;
		}

		vehiclePart = this.getPartById("HeadlightRearRight");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			boolean3 = true;
		}

		if (this.headlightsOn && this.getBatteryCharge() > 0.0F) {
			if (boolean2) {
				this.sprite.modelSlot.model.textureLightsEnables2.m10(1.0F);
			} else {
				this.sprite.modelSlot.model.textureLightsEnables2.m10(0.0F);
			}

			if (boolean1) {
				this.sprite.modelSlot.model.textureLightsEnables2.m20(1.0F);
			} else {
				this.sprite.modelSlot.model.textureLightsEnables2.m20(0.0F);
			}

			if (boolean3) {
				this.sprite.modelSlot.model.textureLightsEnables2.m30(1.0F);
			} else {
				this.sprite.modelSlot.model.textureLightsEnables2.m30(0.0F);
			}

			if (boolean4) {
				this.sprite.modelSlot.model.textureLightsEnables2.m01(1.0F);
			} else {
				this.sprite.modelSlot.model.textureLightsEnables2.m01(0.0F);
			}
		} else {
			this.sprite.modelSlot.model.textureLightsEnables2.m10(0.0F);
			this.sprite.modelSlot.model.textureLightsEnables2.m20(0.0F);
			this.sprite.modelSlot.model.textureLightsEnables2.m30(0.0F);
			this.sprite.modelSlot.model.textureLightsEnables2.m01(0.0F);
		}

		if (this.stoplightsOn && this.getBatteryCharge() > 0.0F) {
			this.sprite.modelSlot.model.textureLightsEnables2.m11(1.0F);
			this.sprite.modelSlot.model.textureLightsEnables2.m21(1.0F);
		} else {
			this.sprite.modelSlot.model.textureLightsEnables2.m11(0.0F);
			this.sprite.modelSlot.model.textureLightsEnables2.m21(0.0F);
		}

		if (this.script.getLightbar().enable) {
			if (this.lightbarLightsMode.isEnable() && this.getBatteryCharge() > 0.0F) {
				this.lightbarLightsMode.update();
				switch (this.lightbarLightsMode.getLightTexIndex()) {
				case 0: 
					this.sprite.modelSlot.model.textureLightsEnables2.m31(0.0F);
					this.sprite.modelSlot.model.textureLightsEnables2.m02(0.0F);
					break;
				
				case 1: 
					this.sprite.modelSlot.model.textureLightsEnables2.m31(0.0F);
					this.sprite.modelSlot.model.textureLightsEnables2.m02(1.0F);
					break;
				
				case 2: 
					this.sprite.modelSlot.model.textureLightsEnables2.m31(1.0F);
					this.sprite.modelSlot.model.textureLightsEnables2.m02(0.0F);
					break;
				
				default: 
					this.sprite.modelSlot.model.textureLightsEnables2.m31(0.0F);
					this.sprite.modelSlot.model.textureLightsEnables2.m02(0.0F);
				
				}
			} else {
				this.sprite.modelSlot.model.textureLightsEnables2.m31(0.0F);
				this.sprite.modelSlot.model.textureLightsEnables2.m02(0.0F);
			}
		}

		this.sprite.modelSlot.model.refBody = 0.3F;
		this.sprite.modelSlot.model.refWindows = 0.4F;
		if (this.rust > 0.8F) {
			this.sprite.modelSlot.model.refBody = 0.1F;
			this.sprite.modelSlot.model.refWindows = 0.2F;
		}
	}

	public void doDamageOverlay() {
		if (this.sprite.modelSlot != null) {
			this.doDoorDamage();
			this.doWindowDamage();
			this.doOtherBodyWorkDamage();
		}
	}

	private void checkDamage(VehiclePart vehiclePart, String string, boolean boolean1) {
		try {
			Method method = this.sprite.modelSlot.model.textureDamage1Enables1.getClass().getMethod(string, Float.TYPE);
			method.invoke(this.sprite.modelSlot.model.textureDamage1Enables1, 0.0F);
			Method method2 = this.sprite.modelSlot.model.textureDamage2Enables1.getClass().getMethod(string, Float.TYPE);
			method2.invoke(this.sprite.modelSlot.model.textureDamage2Enables1, 0.0F);
			Method method3 = this.sprite.modelSlot.model.textureUninstall1.getClass().getMethod(string, Float.TYPE);
			method3.invoke(this.sprite.modelSlot.model.textureUninstall1, 0.0F);
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				if (vehiclePart.getInventoryItem().getCondition() < 60 && vehiclePart.getInventoryItem().getCondition() >= 40) {
					method.invoke(this.sprite.modelSlot.model.textureDamage1Enables1, 1.0F);
				}

				if (vehiclePart.getInventoryItem().getCondition() < 40) {
					method2.invoke(this.sprite.modelSlot.model.textureDamage2Enables1, 1.0F);
				}

				if (vehiclePart.window != null && vehiclePart.window.isOpen() && boolean1) {
					method3.invoke(this.sprite.modelSlot.model.textureUninstall1, 1.0F);
				}
			} else if (vehiclePart != null && boolean1) {
				method3.invoke(this.sprite.modelSlot.model.textureUninstall1, 1.0F);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void checkDamage2(VehiclePart vehiclePart, String string, boolean boolean1) {
		try {
			Method method = this.sprite.modelSlot.model.textureDamage1Enables2.getClass().getMethod(string, Float.TYPE);
			method.invoke(this.sprite.modelSlot.model.textureDamage1Enables2, 0.0F);
			Method method2 = this.sprite.modelSlot.model.textureDamage2Enables2.getClass().getMethod(string, Float.TYPE);
			method2.invoke(this.sprite.modelSlot.model.textureDamage2Enables2, 0.0F);
			Method method3 = this.sprite.modelSlot.model.textureUninstall2.getClass().getMethod(string, Float.TYPE);
			method3.invoke(this.sprite.modelSlot.model.textureUninstall2, 0.0F);
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				if (vehiclePart.getInventoryItem().getCondition() < 60 && vehiclePart.getInventoryItem().getCondition() >= 40) {
					method.invoke(this.sprite.modelSlot.model.textureDamage1Enables2, 1.0F);
				}

				if (vehiclePart.getInventoryItem().getCondition() < 40) {
					method2.invoke(this.sprite.modelSlot.model.textureDamage2Enables2, 1.0F);
				}

				if (vehiclePart.window != null && vehiclePart.window.isOpen() && boolean1) {
					method3.invoke(this.sprite.modelSlot.model.textureUninstall2, 1.0F);
				}
			} else if (vehiclePart != null && boolean1) {
				method3.invoke(this.sprite.modelSlot.model.textureUninstall2, 1.0F);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void checkUninstall2(VehiclePart vehiclePart, String string) {
		try {
			Method method = this.sprite.modelSlot.model.textureUninstall2.getClass().getMethod(string, Float.TYPE);
			method.invoke(this.sprite.modelSlot.model.textureUninstall2, 0.0F);
			if (vehiclePart != null && vehiclePart.getInventoryItem() == null) {
				method.invoke(this.sprite.modelSlot.model.textureUninstall2, 1.0F);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void doOtherBodyWorkDamage() {
		this.checkDamage(this.getPartById("EngineDoor"), "m00", false);
		this.checkDamage(this.getPartById("EngineDoor"), "m03", false);
		this.checkDamage(this.getPartById("EngineDoor"), "m23", false);
		this.checkDamage2(this.getPartById("EngineDoor"), "m12", true);
		this.checkDamage(this.getPartById("TruckBed"), "m10", false);
		this.checkDamage(this.getPartById("TruckBed"), "m13", false);
		this.checkDamage(this.getPartById("TruckBed"), "m33", false);
		VehiclePart vehiclePart = this.getPartById("TrunkDoor");
		if (vehiclePart != null) {
			this.checkDamage2(vehiclePart, "m22", true);
			if (vehiclePart.scriptPart.hasLightsRear) {
				this.checkUninstall2(vehiclePart, "m30");
				this.checkUninstall2(vehiclePart, "m01");
				this.checkUninstall2(vehiclePart, "m11");
				this.checkUninstall2(vehiclePart, "m21");
			}
		} else {
			vehiclePart = this.getPartById("DoorRear");
			if (vehiclePart != null) {
				this.checkDamage2(vehiclePart, "m22", true);
				if (vehiclePart.scriptPart.hasLightsRear) {
					this.checkUninstall2(vehiclePart, "m30");
					this.checkUninstall2(vehiclePart, "m01");
					this.checkUninstall2(vehiclePart, "m11");
					this.checkUninstall2(vehiclePart, "m21");
				}
			}
		}
	}

	private void doWindowDamage() {
		this.checkDamage(this.getPartById("WindowFrontLeft"), "m02", true);
		this.checkDamage(this.getPartById("WindowFrontRight"), "m21", true);
		VehiclePart vehiclePart = this.getPartById("WindowRearLeft");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, "m12", true);
		} else {
			vehiclePart = this.getPartById("WindowMiddleLeft");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, "m12", true);
			}
		}

		vehiclePart = this.getPartById("WindowRearRight");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, "m31", true);
		} else {
			vehiclePart = this.getPartById("WindowMiddleRight");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, "m31", true);
			}
		}

		this.checkDamage(this.getPartById("Windshield"), "m22", true);
		this.checkDamage(this.getPartById("WindshieldRear"), "m32", true);
	}

	private void doDoorDamage() {
		this.checkDamage(this.getPartById("DoorFrontLeft"), "m01", true);
		this.checkDamage(this.getPartById("DoorFrontRight"), "m20", true);
		VehiclePart vehiclePart = this.getPartById("DoorRearLeft");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, "m11", true);
		} else {
			vehiclePart = this.getPartById("DoorMiddleLeft");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, "m11", true);
			}
		}

		vehiclePart = this.getPartById("DoorRearRight");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, "m30", true);
		} else {
			vehiclePart = this.getPartById("DoorMiddleRight");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, "m30", true);
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		if (this.script != null) {
			if (this.physics != null) {
				this.physics.debug();
			}

			int int1 = IsoCamera.frameState.playerIndex;
			if (this.square.lighting[int1].bSeen()) {
				if (this.square.lighting[int1].bCanSee()) {
					this.targetAlpha[int1] = 1.0F;
				} else {
					this.targetAlpha[int1] = 0.0F;
					if (IsoCamera.frameState.CamCharacter != null && IsoCamera.frameState.CamCharacterRoom == null && this.DistToSquared(IsoCamera.frameState.CamCharacter) < 225.0F) {
						this.targetAlpha[int1] = 1.0F;
					}
				}

				this.renderShadow();
				if (this.sprite.hasActiveModel()) {
					int int2 = SpriteRenderer.instance.state.index;
					if (this.sprite.modelSlot.model.xfrm[int2] == null) {
						this.sprite.modelSlot.model.xfrm[int2] = new Matrix4f();
						this.sprite.modelSlot.model.origin[int2] = new Vector3f();
						this.sprite.modelSlot.model.worldPos[int2] = new Vector3f();
					}

					this.sprite.modelSlot.model.xfrm[int2].set((Matrix4fc)this.renderTransform);
					this.sprite.modelSlot.model.worldPos[int2].set(this.getX(), this.getY(), this.getZ());
					this.sprite.modelSlot.model.alpha = this.alpha[IsoPlayer.getPlayerIndex()];
					for (int int3 = 0; int3 < this.models.size(); ++int3) {
						BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int3);
						if (((ModelInstance)this.sprite.modelSlot.sub.get(int3)).xfrm[int2] == null) {
							((ModelInstance)this.sprite.modelSlot.sub.get(int3)).xfrm[int2] = new Matrix4f();
							((ModelInstance)this.sprite.modelSlot.sub.get(int3)).origin[int2] = new Vector3f();
							((ModelInstance)this.sprite.modelSlot.sub.get(int3)).worldPos[int2] = new Vector3f();
						}

						((ModelInstance)this.sprite.modelSlot.sub.get(int3)).xfrm[int2].set((Matrix4fc)modelInfo.renderTransform);
						((ModelInstance)this.sprite.modelSlot.sub.get(int3)).origin[int2].set((Vector3fc)modelInfo.renderOrigin);
						((ModelInstance)this.sprite.modelSlot.sub.get(int3)).worldPos[int2].set(this.getX(), this.getY(), this.getZ());
						((ModelInstance)this.sprite.modelSlot.sub.get(int3)).alpha = this.alpha[IsoPlayer.getPlayerIndex()];
					}

					this.updateLights();
					Transform transform = this.tempTransform;
					this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
					this.tempVector3f_1.set(-this.getScript().getCenterOfMassOffset().x, -this.getScript().getCenterOfMassOffset().y, -this.getScript().getCenterOfMassOffset().z);
					Vector3f vector3f = this.tempVector3f_1;
					this.getWorldTransform(transform);
					transform.transform(vector3f);
					vector3f.set(vector3f.x + WorldSimulation.instance.offsetX, vector3f.z + WorldSimulation.instance.offsetY, vector3f.y);
					float1 = vector3f.x;
					float2 = vector3f.y;
					float3 = vector3f.z;
					float float4 = IsoUtils.XToScreen(float1 + this.sprite.def.offX, float2 + this.sprite.def.offY, Math.max(float3, 0.0F) + this.sprite.def.offZ, 0);
					float float5 = IsoUtils.YToScreen(float1 + this.sprite.def.offX, float2 + this.sprite.def.offY, Math.max(float3, 0.0F) + this.sprite.def.offZ, 0);
					if (float3 < 0.0F) {
						float5 -= float3 * 96.0F * (float)Core.TileScale;
					}

					float4 = (float)((int)float4 + IsoSprite.globalOffsetX);
					float5 = (float)((int)float5 + IsoSprite.globalOffsetY);
					int int4 = ModelManager.instance.bitmap.getTexture().getWidth() * Core.TileScale;
					int int5 = -ModelManager.instance.bitmap.getTexture().getHeight() * Core.TileScale;
					if (LEMMY_FLIP_FIX) {
						int5 *= -1;
					}

					float float6 = float4 - (float)(int4 / 2);
					float5 += (float)(36 * Core.TileScale);
					if (LEMMY_FLIP_FIX) {
						float5 -= (float)ModelManager.instance.bitmap.getTexture().getHeight();
					}

					colorInfo.a = this.alpha[IsoPlayer.getPlayerIndex()];
					inf.a = colorInfo.a;
					inf.r = colorInfo.r;
					inf.g = colorInfo.g;
					inf.b = colorInfo.b;
					this.sprite.renderVehicle(this.def, this, float1, float2, float3, this.dir, 0.0F, 0.0F, inf, true);
				}

				float float7 = 2.0F;
				float float8 = 1.5F;
				int int6;
				float[] floatArray;
				if (this.alpha[IsoPlayer.getPlayerIndex()] < this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
					floatArray = this.alpha;
					int6 = IsoPlayer.getPlayerIndex();
					floatArray[int6] += alphaStep * float7;
					if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
						this.alpha[IsoPlayer.getPlayerIndex()] = this.targetAlpha[IsoPlayer.getPlayerIndex()];
					}
				} else if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
					floatArray = this.alpha;
					int6 = IsoPlayer.getPlayerIndex();
					floatArray[int6] -= alphaStep / float8;
					if (this.alpha[IsoPlayer.getPlayerIndex()] < this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
						this.alpha[IsoPlayer.getPlayerIndex()] = this.targetAlpha[IsoPlayer.getPlayerIndex()];
					}
				}

				if (this.alpha[IsoPlayer.getPlayerIndex()] < 0.0F) {
					this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
				}

				if (this.alpha[IsoPlayer.getPlayerIndex()] > 1.0F) {
					this.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
				}

				if (Core.bDebug && DebugOptions.instance.VehicleRenderArea.getValue()) {
					this.renderAreas();
				}

				if (Core.bDebug && DebugOptions.instance.VehicleRenderAttackPositions.getValue()) {
					this.renderAttackPositions();
				}

				if (Core.bDebug && DebugOptions.instance.VehicleRenderExit.getValue()) {
					this.renderExits();
				}

				if (Core.bDebug && DebugOptions.instance.VehicleRenderIntersectedSquares.getValue()) {
					this.renderIntersectedSquares();
				}

				if (Core.bDebug && DebugOptions.instance.VehicleRenderAuthorizations.getValue()) {
					this.renderAuthorizations();
				}

				this.renderUsableArea();
				if (this.limitUpdate.Check()) {
					for (int int7 = 0; int7 < IsoPlayer.numPlayers; ++int7) {
						IsoPlayer player = IsoPlayer.players[int7];
						if (player != null && !player.isDead()) {
							this.updateHasExtendOffset(player);
							if (!this.hasExtendOffset) {
								break;
							}
						}
					}
				}
			}
		}
	}

	public void renderlast() {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			if (vehiclePart.chatElement != null && vehiclePart.chatElement.getHasChatToDisplay()) {
				float float1 = IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0);
				float float2 = IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0);
				float1 = float1 - IsoCamera.getOffX() - this.offsetX;
				float2 = float2 - IsoCamera.getOffY() - this.offsetY;
				float1 += (float)(32 * Core.TileScale);
				float2 += (float)(20 * Core.TileScale);
				float1 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
				float2 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
				vehiclePart.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)float1, (int)float2);
			}
		}
	}

	private void renderShadow() {
		if (this.physics != null) {
			IsoSprite sprite = IsoGameCharacter.DropShadow;
			if (sprite != null && this.getCurrentSquare() != null) {
				float float1 = 0.6F * this.alpha[IsoPlayer.getPlayerIndex()];
				ColorInfo colorInfo = this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo();
				float1 *= (colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F;
				PolygonalMap2.VehiclePoly vehiclePoly = this.getPoly();
				SpriteRenderer.instance.renderPoly(vehicleShadow, (int)IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), (int)IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), (int)IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), (int)IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), 1.0F, 1.0F, 1.0F, 0.8F * float1);
			}
		}
	}

	public boolean isEnterBlocked(IsoGameCharacter gameCharacter, int int1) {
		return this.isExitBlocked(int1);
	}

	public boolean isExitBlocked(int int1) {
		VehicleScript.Position position = this.getPassengerPosition(int1, "inside");
		VehicleScript.Position position2 = this.getPassengerPosition(int1, "outside");
		if (position != null && position2 != null) {
			Vector3f vector3f = this.getWorldPos(position2.offset, this.tempVector3f_1);
			if (position2.area != null) {
				VehicleScript.Area area = this.script.getAreaById(position2.area);
				Vector2 vector2 = this.areaPositionWorld(area);
				if (vector2 != null) {
					vector3f.x = vector2.x;
					vector3f.y = vector2.y;
				}
			}

			vector3f.z = 0.0F;
			Vector3f vector3f2 = this.getWorldPos(position.offset, this.tempVector3f_2);
			return PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, vector3f.x, vector3f.y, (int)this.z, this, false, false);
		} else {
			return true;
		}
	}

	public boolean isPassengerUseDoor2(IsoGameCharacter gameCharacter, int int1) {
		VehicleScript.Position position = this.getPassengerPosition(int1, "outside2");
		if (position != null) {
			Vector3f vector3f = this.getWorldPos(position.offset, new Vector3f());
			vector3f.sub(gameCharacter.x, gameCharacter.y, gameCharacter.z);
			if (vector3f.length() < 2.0F) {
				return true;
			}
		}

		return false;
	}

	public boolean isEnterBlocked2(IsoGameCharacter gameCharacter, int int1) {
		return this.isExitBlocked2(int1);
	}

	public boolean isExitBlocked2(int int1) {
		VehicleScript.Position position = this.getPassengerPosition(int1, "inside");
		VehicleScript.Position position2 = this.getPassengerPosition(int1, "outside2");
		if (position != null && position2 != null) {
			Vector3f vector3f = this.getWorldPos(position2.offset, this.tempVector3f_1);
			vector3f.z = 0.0F;
			Vector3f vector3f2 = this.getWorldPos(position.offset, this.tempVector3f_2);
			return PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, vector3f.x, vector3f.y, (int)this.z, this, false, false);
		} else {
			return true;
		}
	}

	private void renderExits() {
		int int1 = Core.TileScale;
		for (int int2 = 0; int2 < this.getMaxPassengers(); ++int2) {
			VehicleScript.Position position = this.getPassengerPosition(int2, "inside");
			VehicleScript.Position position2 = this.getPassengerPosition(int2, "outside");
			if (position != null && position2 != null) {
				float float1 = 0.3F;
				Vector3f vector3f = this.getWorldPos(position2.offset, this.tempVector3f_1);
				Vector3f vector3f2 = this.getWorldPos(position.offset, this.tempVector3f_2);
				int int3 = (int)Math.floor((double)(vector3f.x - float1));
				int int4 = (int)Math.floor((double)(vector3f.x + float1));
				int int5 = (int)Math.floor((double)(vector3f.y - float1));
				int int6 = (int)Math.floor((double)(vector3f.y + float1));
				for (int int7 = int5; int7 <= int6; ++int7) {
					for (int int8 = int3; int8 <= int4; ++int8) {
						int int9 = (int)IsoUtils.XToScreenExact((float)int8, (float)(int7 + 1), (float)((int)this.z), 0);
						int int10 = (int)IsoUtils.YToScreenExact((float)int8, (float)(int7 + 1), (float)((int)this.z), 0);
						SpriteRenderer.instance.renderPoly(int9, int10, int9 + 32 * int1, int10 - 16 * int1, int9 + 64 * int1, int10, int9 + 32 * int1, int10 + 16 * int1, 1.0F, 1.0F, 1.0F, 0.5F);
					}
				}

				float float2 = 1.0F;
				float float3 = 1.0F;
				float float4 = 1.0F;
				if (this.isExitBlocked(int2)) {
					float4 = 0.0F;
					float3 = 0.0F;
				}

				this.getController().drawCircle(vector3f2.x, vector3f2.y, float1, 0.0F, 0.0F, 1.0F, 1.0F);
				this.getController().drawCircle(vector3f.x, vector3f.y, float1, float2, float3, float4, 1.0F);
			}
		}
	}

	private Vector2 areaPositionLocal(VehicleScript.Area area) {
		Vector2 vector2 = this.areaPositionWorld(area);
		Vector3f vector3f = this.tempVector3f_2;
		vector3f.set(vector2.x, vector2.y, 0.0F);
		this.getLocalPos(vector3f, vector3f);
		vector2.set(vector3f.x, vector3f.z);
		return vector2;
	}

	public Vector2 areaPositionWorld(VehicleScript.Area area) {
		if (area == null) {
			return null;
		} else {
			Vector3f vector3f = this.tempVector3f_2;
			Vector2[] vector2Array = new Vector2[4];
			float float1 = 0.05F;
			vector3f.set(area.x + area.w / 2.0F - float1, 0.0F, area.y + area.h / 2.0F - float1);
			this.getWorldPos(vector3f, vector3f);
			vector2Array[0] = new Vector2(vector3f.x, vector3f.y);
			vector3f.set(area.x - area.w / 2.0F + float1, 0.0F, area.y + area.h / 2.0F - float1);
			this.getWorldPos(vector3f, vector3f);
			vector2Array[1] = new Vector2(vector3f.x, vector3f.y);
			vector3f.set(area.x - area.w / 2.0F + float1, 0.0F, area.y - area.h / 2.0F + float1);
			this.getWorldPos(vector3f, vector3f);
			vector2Array[2] = new Vector2(vector3f.x, vector3f.y);
			vector3f.set(area.x + area.w / 2.0F - float1, 0.0F, area.y - area.h / 2.0F + float1);
			this.getWorldPos(vector3f, vector3f);
			vector2Array[3] = new Vector2(vector3f.x, vector3f.y);
			boolean boolean1 = QuadranglesIntersection.IsQuadranglesAreIntersected(vector2Array, this.poly.borders);
			if (boolean1) {
				if (area.x > this.width / 2.0F || area.x < -this.width / 2.0F) {
					vector3f.set(area.x, 0.0F, area.y);
					this.getWorldPos(vector3f, vector3f);
					return new Vector2(vector3f.x - this.script.getExtentsOffset().x, vector3f.y - this.script.getExtentsOffset().y);
				}

				vector3f.set(area.x, 0.0F, area.y);
				this.getWorldPos(vector3f, vector3f);
				vector2Array[0] = new Vector2(vector3f.x, vector3f.y);
				vector3f.set(area.x, 0.0F, area.y + area.h / 2.0F);
				this.getWorldPos(vector3f, vector3f);
				vector2Array[1] = new Vector2(vector3f.x, vector3f.y);
				vector3f.set(area.x, 0.0F, area.y);
				this.getWorldPos(vector3f, vector3f);
				vector2Array[2] = new Vector2(vector3f.x - this.script.getExtentsOffset().x, vector3f.y - this.script.getExtentsOffset().y);
				vector3f.set(area.x + area.w / 2.0F, 0.0F, area.y);
				this.getWorldPos(vector3f, vector3f);
				vector2Array[3] = new Vector2(vector3f.x - this.script.getExtentsOffset().x, vector3f.y - this.script.getExtentsOffset().y);
				Vector2 vector2 = PolygonalMap2.VehiclePoly.lineIntersection(vector2Array[0], vector2Array[1], vector2Array[2], vector2Array[3]);
				if (vector2 != null) {
					return vector2;
				}
			}

			vector3f.set(area.x, 0.0F, area.y);
			this.getWorldPos(vector3f, vector3f);
			return new Vector2(vector3f.x, vector3f.y);
		}
	}

	private void renderAreas() {
		if (this.getScript() != null) {
			float float1 = this.getScript().getModelScale();
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getArea() != null) {
					VehicleScript.Area area = this.getScript().getAreaById(vehiclePart.getArea());
					if (area != null) {
						Vector2 vector2 = this.areaPositionWorld(area);
						if (vector2 != null) {
							boolean boolean1 = this.isInArea(area.id, IsoPlayer.instance);
							Vector3f vector3f = this.getForwardVector(this.tempVector3f_1);
							this.getController().drawRect(vector3f, vector2.x - WorldSimulation.instance.offsetX, vector2.y - WorldSimulation.instance.offsetY, area.w * float1, area.h / 2.0F * float1, boolean1 ? 0.0F : 0.65F, boolean1 ? 1.0F : 0.65F, boolean1 ? 1.0F : 0.65F);
						}
					}
				}
			}

			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), 1.0F, 0.5F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), 1.0F, 0.5F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), 1.0F, 0.5F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), 1.0F, 0.5F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), 0.5F, 1.0F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), 0.5F, 1.0F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), 0.5F, 1.0F, 0.5F, 1.0F, 0);
			LineDrawer.drawLine(IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), 0.5F, 1.0F, 0.5F, 1.0F, 0);
		}
	}

	private void renderAuthorizations() {
		float float1 = 0.3F;
		float float2 = 0.3F;
		float float3 = 0.3F;
		if (this.netPlayerAuthorization == 0) {
			float1 = 1.0F;
		}

		if (this.netPlayerAuthorization == 1) {
			float3 = 1.0F;
		}

		if (this.netPlayerAuthorization == 3) {
			float2 = 1.0F;
		}

		if (this.netPlayerAuthorization == 4) {
			float2 = 1.0F;
			float1 = 1.0F;
		}

		if (this.netPlayerAuthorization == 2) {
			float3 = 1.0F;
			float1 = 1.0F;
		}

		LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), float1, float2, float3, 1.0F, 0);
		LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), float1, float2, float3, 1.0F, 0);
		LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), float1, float2, float3, 1.0F, 0);
		LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), float1, float2, float3, 1.0F, 0);
		TextManager.instance.DrawString((double)IsoUtils.XToScreenExact(this.x, this.y, 0.0F, 0), (double)IsoUtils.YToScreenExact(this.x, this.y, 0.0F, 0), "Authorizations VID:" + this.VehicleID + "\n auth:" + this.netPlayerAuthorization + "\n authPID:" + this.netPlayerId + "\n authTimeout:" + this.netPlayerTimeout);
	}

	private void renderAttackPositions() {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			IsoGameCharacter gameCharacter = this.getCharacter(int1);
			if (gameCharacter != null) {
				ArrayList arrayList = new ArrayList();
				float float1 = this.script.getExtents().x / this.script.getModelScale();
				float float2 = this.script.getExtents().z / this.script.getModelScale();
				float float3 = 0.3F / this.script.getModelScale();
				float float4 = -float1 / 2.0F - float3;
				float float5 = -float2 / 2.0F - float3;
				float float6 = float1 / 2.0F + float3;
				float float7 = float2 / 2.0F + float3;
				this.initPolyPlusRadiusBounds();
				float float8 = this.polyPlusRadiusMinX;
				float float9 = this.polyPlusRadiusMaxX;
				float float10 = this.polyPlusRadiusMinY;
				float float11 = this.polyPlusRadiusMaxY;
				this.getAdjacentPositions(gameCharacter, float8, -float2 / 2.0F, float8, float2 / 2.0F, arrayList);
				this.getAdjacentPositions(gameCharacter, float9, -float2 / 2.0F, float9, float2 / 2.0F, arrayList);
				this.getAdjacentPositions(gameCharacter, float4, float10, float6, float10, arrayList);
				this.getAdjacentPositions(gameCharacter, float4, float11, float6, float11, arrayList);
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					Vector3f vector3f = (Vector3f)arrayList.get(int2);
					float10 = 1.0F;
					float11 = 1.0F;
					float float12 = 1.0F;
					if (!this.isAttackPositionFree(vector3f.x, vector3f.y, (IsoGameCharacter)null)) {
						float12 = 0.0F;
						float11 = 0.0F;
					}

					this.physics.drawCircle(vector3f.x, vector3f.y, 0.3F, float10, float11, float12, 1.0F);
				}

				return;
			}
		}
	}

	private void renderUsableArea() {
		if (this.getScript() != null) {
			VehiclePart vehiclePart = this.getUseablePart(IsoPlayer.instance);
			if (vehiclePart != null) {
				VehicleScript.Area area = this.getScript().getAreaById(vehiclePart.getArea());
				if (area != null) {
					Vector2 vector2 = this.areaPositionWorld(area);
					if (vector2 != null) {
						Vector3f vector3f = this.getForwardVector(this.tempVector3f_1);
						float float1 = this.getScript().getModelScale();
						this.getController().drawRect(vector3f, vector2.x - WorldSimulation.instance.offsetX, vector2.y - WorldSimulation.instance.offsetY, area.w * float1, area.h / 2.0F * float1, 0.0F, 1.0F, 0.0F);
					}
				}
			}
		}
	}

	private void renderIntersectedSquares() {
		PolygonalMap2.VehiclePoly vehiclePoly = this.getPoly();
		float float1 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
		float float2 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
		float float3 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
		float float4 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
		for (int int1 = (int)float2; int1 < (int)Math.ceil((double)float4); ++int1) {
			for (int int2 = (int)float1; int2 < (int)Math.ceil((double)float3); ++int2) {
				if (this.isIntersectingSquare(int2, int1, (int)this.z)) {
					LineDrawer.addLine((float)int2, (float)int1, (float)((int)this.z), (float)(int2 + 1), (float)(int1 + 1), (float)((int)this.z), 1.0F, 1.0F, 1.0F, (String)null, false);
				}
			}
		}
	}

	private void setModelTransform(BaseVehicle.ModelInfo modelInfo, float float1, float float2, float float3, float float4) {
		modelInfo.renderTransform.scaling(float4);
		if (modelInfo.renderOrigin.length() > 0.0F) {
			Matrix4f matrix4f = this.tempMatrix4fLWJGL_1;
			matrix4f.rotationX(float3).rotateY(float2);
			this.tempMatrix4f.translation(modelInfo.renderOrigin).transpose();
			matrix4f.mulGeneric(this.tempMatrix4f, matrix4f);
			matrix4f.mulGeneric(modelInfo.renderTransform, modelInfo.renderTransform);
			Vector3f vector3f = modelInfo.scriptModel.offset;
			if (vector3f.x != 0.0F || vector3f.y != 0.0F || vector3f.z != 0.0F) {
				matrix4f.translation(vector3f).transpose();
				matrix4f.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
			}

			matrix4f.scaling(modelInfo.scriptModel.scale);
			matrix4f.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
		}
	}

	public void getWheelForwardVector(int int1, Vector3f vector3f) {
		BaseVehicle.WheelInfo wheelInfo = this.wheelInfo[int1];
		Matrix4f matrix4f = this.tempMatrix4fLWJGL_1;
		matrix4f.rotationY(wheelInfo.steering);
		matrix4f.mulGeneric(this.jniTransform.getMatrix(this.tempMatrix4f).setTranslation(0.0F, 0.0F, 0.0F), matrix4f);
		matrix4f.getColumn(2, this.tempVector4f);
		vector3f.set(this.tempVector4f.x, 0.0F, this.tempVector4f.z);
	}

	public void tryStartEngine(boolean boolean1) {
		if (this.getDriver() == null || !(this.getDriver() instanceof IsoPlayer) || !((IsoPlayer)this.getDriver()).isBlockMovement()) {
			if (this.engineState == BaseVehicle.engineStateTypes.Idle) {
				if (!Core.bDebug && !SandboxOptions.instance.VehicleEasyUse.getValue() && !this.isKeysInIgnition() && !boolean1 && this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) == null && !this.isHotwired()) {
					if (GameServer.bServer) {
						this.getDriver().sendObjectChange("vehicleNoKey");
					} else {
						this.getDriver().SayDebug(" [img=media/ui/CarKey_none.png]");
					}
				} else {
					this.engineDoStarting();
				}
			}
		}
	}

	public void tryStartEngine() {
		this.tryStartEngine(false);
	}

	public void engineDoIdle() {
		this.engineState = BaseVehicle.engineStateTypes.Idle;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}
	}

	public void engineDoStarting() {
		this.engineState = BaseVehicle.engineStateTypes.Starting;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}

		this.setKeysInIgnition(true);
	}

	public boolean isStarting() {
		return this.engineState == BaseVehicle.engineStateTypes.Starting || this.engineState == BaseVehicle.engineStateTypes.StartingFailed || this.engineState == BaseVehicle.engineStateTypes.StartingSuccess || this.engineState == BaseVehicle.engineStateTypes.StartingFailedNoPower;
	}

	public void engineDoRetryingStarting() {
		this.getEmitter().stopSoundByName("VehicleFailingToStart");
		this.getEmitter().playSoundImpl("VehicleFailingToStart", (IsoObject)null);
		this.engineState = BaseVehicle.engineStateTypes.RetryingStarting;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}
	}

	public void engineDoStartingSuccess() {
		this.getEmitter().stopSoundByName("VehicleFailingToStart");
		this.getEmitter().playSoundImpl("VehicleStarted", (IsoObject)null);
		this.engineState = BaseVehicle.engineStateTypes.StartingSuccess;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}

		this.setKeysInIgnition(true);
	}

	public void engineDoStartingFailed() {
		this.getEmitter().stopSoundByName("VehicleFailingToStart");
		this.getEmitter().playSoundImpl("VehicleFailingToStart", (IsoObject)null);
		int int1;
		for (int1 = 0; int1 < this.engineSound.length; ++int1) {
			if (this.engineSound[int1] != 0L) {
				this.emitter.stopSound(this.engineSound[int1]);
				this.engineSound[int1] = 0L;
			}
		}

		for (int1 = 0; int1 < this.new_EngineSoundId.length; ++int1) {
			if (this.new_EngineSoundId[int1] != 0L) {
				this.emitter.stopSound(this.new_EngineSoundId[int1]);
				this.new_EngineSoundId[int1] = 0L;
			}
		}

		this.engineState = BaseVehicle.engineStateTypes.StartingFailed;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}
	}

	public void engineDoStartingFailedNoPower() {
		this.getEmitter().stopSoundByName("vehicle_key");
		this.getEmitter().playSoundImpl("vehicle_key", (IsoObject)null);
		int int1;
		for (int1 = 0; int1 < this.engineSound.length; ++int1) {
			if (this.engineSound[int1] != 0L) {
				this.emitter.stopSound(this.engineSound[int1]);
				this.engineSound[int1] = 0L;
			}
		}

		for (int1 = 0; int1 < this.new_EngineSoundId.length; ++int1) {
			if (this.new_EngineSoundId[int1] != 0L) {
				this.emitter.stopSound(this.new_EngineSoundId[int1]);
				this.new_EngineSoundId[int1] = 0L;
			}
		}

		this.engineState = BaseVehicle.engineStateTypes.StartingFailedNoPower;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}
	}

	public void engineDoRunning() {
		this.setNeedPartsUpdate(true);
		this.engineState = BaseVehicle.engineStateTypes.Running;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}
	}

	public void engineDoStalling() {
		this.getEmitter().playSoundImpl("VehicleRunningOutOfGas", (IsoObject)null);
		this.engineState = BaseVehicle.engineStateTypes.Stalling;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		int int1;
		for (int1 = 0; int1 < this.engineSound.length; ++int1) {
			if (this.engineSound[int1] != 0L) {
				this.emitter.stopSound(this.engineSound[int1]);
				this.engineSound[int1] = 0L;
			}
		}

		for (int1 = 0; int1 < this.new_EngineSoundId.length; ++int1) {
			if (this.new_EngineSoundId[int1] != 0L) {
				this.emitter.stopSound(this.new_EngineSoundId[int1]);
				this.new_EngineSoundId[int1] = 0L;
			}
		}

		this.engineSoundIndex = 0;
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}

		this.setKeysInIgnition(false);
	}

	public void engineDoShutingDown() {
		this.getEmitter().playSoundImpl("VehicleTurnedOff", (IsoObject)null);
		int int1;
		for (int1 = 0; int1 < this.engineSound.length; ++int1) {
			if (this.engineSound[int1] != 0L) {
				this.emitter.stopSound(this.engineSound[int1]);
				this.engineSound[int1] = 0L;
			}
		}

		for (int1 = 0; int1 < this.new_EngineSoundId.length; ++int1) {
			if (this.new_EngineSoundId[int1] != 0L) {
				this.emitter.stopSound(this.new_EngineSoundId[int1]);
				this.new_EngineSoundId[int1] = 0L;
			}
		}

		this.engineSoundIndex = 0;
		this.engineState = BaseVehicle.engineStateTypes.ShutingDown;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}

		this.setKeysInIgnition(false);
		VehiclePart vehiclePart = this.getHeater();
		if (vehiclePart != null) {
			vehiclePart.getModData().rawset("active", false);
		}
	}

	public void shutOff() {
		if (this.getPartById("GasTank").getContainerContentAmount() == 0.0F) {
			this.engineDoStalling();
		} else {
			this.engineDoShutingDown();
		}
	}

	public void resumeRunningAfterLoad() {
		if (GameClient.bClient) {
			IsoGameCharacter gameCharacter = this.getDriver();
			if (gameCharacter != null) {
				Boolean Boolean1 = this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) != null ? Boolean.TRUE : Boolean.FALSE;
				GameClient.instance.sendClientCommandV((IsoPlayer)this.getDriver(), "vehicle", "startEngine", "haveKey", Boolean1);
			}
		} else if (this.isEngineWorking()) {
			this.getEmitter();
			this.engineDoStartingSuccess();
		}
	}

	public boolean isEngineStarted() {
		return this.engineState == BaseVehicle.engineStateTypes.Starting || this.engineState == BaseVehicle.engineStateTypes.StartingFailed || this.engineState == BaseVehicle.engineStateTypes.StartingSuccess || this.engineState == BaseVehicle.engineStateTypes.RetryingStarting;
	}

	public boolean isEngineRunning() {
		return this.engineState == BaseVehicle.engineStateTypes.Running;
	}

	public boolean isEngineWorking() {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			String string = vehiclePart.getLuaFunction("checkEngine");
			if (string != null && !Boolean.TRUE.equals(this.callLuaBoolean(string, this, vehiclePart))) {
				return false;
			}
		}

		return true;
	}

	public boolean isOperational() {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			String string = vehiclePart.getLuaFunction("checkOperate");
			if (string != null && !Boolean.TRUE.equals(this.callLuaBoolean(string, this, vehiclePart))) {
				return false;
			}
		}

		return true;
	}

	public boolean isDriveable() {
		if (!this.isEngineWorking()) {
			return false;
		} else {
			return this.isOperational();
		}
	}

	public BaseSoundEmitter getEmitter() {
		if (this.emitter == null) {
			this.emitter = (BaseSoundEmitter)(!Core.SoundDisabled && !GameServer.bServer ? new FMODSoundEmitter() : new DummySoundEmitter());
		}

		return this.emitter;
	}

	public long playSoundImpl(String string, IsoObject object) {
		return this.getEmitter().playSoundImpl(string, object);
	}

	public int stopSound(long long1) {
		return this.getEmitter().stopSound(long1);
	}

	public void playSound(String string) {
		this.getEmitter().playSound(string);
	}

	public void updateSounds() {
		if (!GameServer.bServer) {
			if (this.getBatteryCharge() > 0.0F) {
				if (this.lightbarSirenMode.isEnable() && this.soundSirenSignal == -1L) {
					this.setLightbarSirenMode(this.lightbarSirenMode.get());
				}
			} else if (this.soundSirenSignal != -1L) {
				this.getEmitter().stopSound(this.soundSirenSignal);
				this.soundSirenSignal = -1L;
			}
		}

		IsoPlayer player = null;
		float float1 = Float.MAX_VALUE;
		int int1;
		float float2;
		float float3;
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && player2.getCurrentSquare() != null) {
				float2 = player2.getX();
				float3 = player2.getY();
				float float4 = IsoUtils.DistanceToSquared(float2, float3, this.x, this.y);
				if (player2.HasTrait("HardOfHearing")) {
					float4 *= 4.5F;
				}

				if (player2.HasTrait("Deaf")) {
					float4 = Float.MAX_VALUE;
				}

				if (float4 < float1) {
					player = player2;
					float1 = float4;
				}
			}
		}

		if (player == null) {
			if (this.emitter != null) {
				this.emitter.setPos(this.x, this.y, this.z);
				if (!this.emitter.isEmpty()) {
					this.emitter.tick();
				}
			}
		} else {
			int int2;
			if (!GameServer.bServer) {
				float float5 = float1;
				if (float1 > 1200.0F) {
					for (int2 = 0; int2 < this.engineSound.length; ++int2) {
						if (this.engineSound[int2] != 0L) {
							this.getEmitter().stopSound(this.engineSound[int2]);
							this.engineSound[int2] = 0L;
						}
					}

					for (int2 = 0; int2 < this.new_EngineSoundId.length; ++int2) {
						if (this.new_EngineSoundId[int2] != 0L) {
							this.getEmitter().stopSound(this.new_EngineSoundId[int2]);
							this.new_EngineSoundId[int2] = 0L;
						}
					}

					if (this.emitter != null && !this.emitter.isEmpty()) {
						this.emitter.setPos(this.x, this.y, this.z);
						this.emitter.tick();
					}

					return;
				}

				for (int2 = 0; int2 < this.new_EngineSoundId.length; ++int2) {
					if (this.new_EngineSoundId[int2] != 0L) {
						this.getEmitter().setVolume(this.new_EngineSoundId[int2], 1.0F - float5 / 1200.0F);
					}
				}
			}

			this.startTime -= GameTime.instance.getMultiplier();
			if (RPMList.size() == 0) {
				for (int1 = 0; int1 < 37; ++int1) {
					int2 = Math.max(int1, 4);
					String string = "" + int2;
					if (int1 < 10) {
						string = "0" + int2;
					}

					RPMList.add("car/" + string);
				}
			}

			if (this.getController() != null) {
				if (!GameServer.bServer) {
					if (this.emitter == null) {
						if (this.engineState != BaseVehicle.engineStateTypes.Running) {
							return;
						}

						this.getEmitter();
					}

					boolean boolean1 = this.isAnyListenerInside();
					float float6 = Math.abs(this.getCurrentSpeedKmHour());
					if (this.startTime <= 0.0F && this.engineState == BaseVehicle.engineStateTypes.Running) {
						float2 = 1.0F - float1 / 1200.0F;
						float2 = this.clamp(float2, 0.0F, 1.0F);
						float2 *= float2;
						float3 = 0.5F + this.throttle * 0.5F;
						BaseVehicle.EngineRPMData[] engineRPMDataArray = null;
						if ("jeep".equals(this.getScript().getEngineRPMType())) {
							engineRPMDataArray = JeepEngineData;
						} else if ("van".equals(this.getScript().getEngineRPMType())) {
							engineRPMDataArray = VanEngineData;
						} else if ("firebird".equals(this.getScript().getEngineRPMType())) {
							engineRPMDataArray = FirebirdEngineData;
						}

						if (this.getDriver() == null) {
							if (this.engineSpeed > 1000.0) {
								this.engineSpeed -= (double)(Rand.Next(10, 30) * 2);
							} else {
								this.engineSpeed = 1000.0;
							}
						}

						float float7 = (float)this.engineSpeed;
						float float8 = (float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F;
						for (int int3 = 0; int3 < 4; ++int3) {
							if (engineRPMDataArray[int3].SoundName != null && !engineRPMDataArray[int3].SoundName.contains("unused")) {
								if (this.new_EngineSoundId[int3] == 0L) {
									this.new_EngineSoundId[int3] = this.emitter.playSoundLoopedImpl(engineRPMDataArray[int3].SoundName);
								}

								this.emitter.set3D(this.new_EngineSoundId[int3], !boolean1);
								if (float7 >= engineRPMDataArray[int3].RPM_Min && float7 < engineRPMDataArray[int3].RPM_Max && float8 > 0.0F) {
									float float9 = 1.0F;
									float float10 = 1.0F;
									float float11;
									if (float7 < engineRPMDataArray[int3].RPM_Mid) {
										float11 = (float7 - engineRPMDataArray[int3].RPM_Min) / (engineRPMDataArray[int3].RPM_Mid - engineRPMDataArray[int3].RPM_Min);
										float9 = engineRPMDataArray[int3].RPM_Min_Pitch + float11 * (engineRPMDataArray[int3].RPM_Mid_Pitch - engineRPMDataArray[int3].RPM_Min_Pitch);
										float10 = engineRPMDataArray[int3].RPM_Min_Volume + float11 * (engineRPMDataArray[int3].RPM_Mid_Volume - engineRPMDataArray[int3].RPM_Min_Volume);
									} else {
										float11 = (float7 - engineRPMDataArray[int3].RPM_Mid) / (engineRPMDataArray[int3].RPM_Max - engineRPMDataArray[int3].RPM_Mid);
										float9 = engineRPMDataArray[int3].RPM_Mid_Pitch + float11 * (engineRPMDataArray[int3].RPM_Max_Pitch - engineRPMDataArray[int3].RPM_Mid_Pitch);
										float10 = engineRPMDataArray[int3].RPM_Mid_Volume + float11 * (engineRPMDataArray[int3].RPM_Max_Volume - engineRPMDataArray[int3].RPM_Mid_Volume);
									}

									float9 *= float9;
									this.emitter.setPitch(this.new_EngineSoundId[int3], float9);
									this.emitter.setVolume(this.new_EngineSoundId[int3], float10 * float2 * float3);
								} else if (this.new_EngineSoundId[int3] != 0L) {
									this.emitter.setVolume(this.new_EngineSoundId[int3], 0.0F);
								}
							}
						}
					}

					boolean boolean2 = false;
					if (!GameClient.bClient || this.isLocalPhysicSim()) {
						for (int int4 = 0; int4 < this.script.getWheelCount(); ++int4) {
							if (this.wheelInfo[int4].skidInfo < 0.15F) {
								boolean2 = true;
								break;
							}
						}
					}

					if (this.getCharacter(0) == null) {
						boolean2 = false;
					}

					if (boolean2 != this.skidding) {
						if (boolean2) {
							this.skidSound = this.getEmitter().playSoundImpl("VehicleSkid", (IsoObject)null);
						} else if (this.skidSound != 0L) {
							this.emitter.stopSound(this.skidSound);
							this.skidSound = 0L;
						}

						this.skidding = boolean2;
					}

					if (this.soundBackMoveSignal != -1L && this.emitter != null) {
						this.emitter.set3D(this.soundBackMoveSignal, !boolean1);
					}

					if (this.soundHorn != -1L && this.emitter != null) {
						this.emitter.set3D(this.soundHorn, !boolean1);
					}

					if (this.soundSirenSignal != -1L && this.emitter != null) {
						this.emitter.set3D(this.soundSirenSignal, !boolean1);
					}

					if (this.emitter != null && (this.engineState != BaseVehicle.engineStateTypes.Idle || !this.emitter.isEmpty())) {
						this.emitter.setPos(this.x, this.y, this.z);
						this.emitter.tick();
					}
				}
			}
		}
	}

	private boolean updatePart(VehiclePart vehiclePart) {
		vehiclePart.updateSignalDevice();
		VehicleLight vehicleLight = vehiclePart.getLight();
		if (vehicleLight != null && vehiclePart.getId().contains("Headlight")) {
			vehiclePart.setLightActive(this.getHeadlightsOn() && vehiclePart.getInventoryItem() != null && this.getBatteryCharge() > 0.0F);
		}

		String string = vehiclePart.getLuaFunction("update");
		if (string == null) {
			return false;
		} else {
			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			if (vehiclePart.getLastUpdated() < 0.0F) {
				vehiclePart.setLastUpdated(float1);
			} else if (vehiclePart.getLastUpdated() > float1) {
				vehiclePart.setLastUpdated(float1);
			}

			float float2 = float1 - vehiclePart.getLastUpdated();
			if ((int)(float2 * 60.0F) > 0) {
				vehiclePart.setLastUpdated(float1);
				this.callLuaVoid(string, this, vehiclePart, (double)(float2 * 60.0F));
				return true;
			} else {
				return false;
			}
		}
	}

	public void updateParts() {
		if (!GameClient.bClient) {
			boolean boolean1 = false;
			for (int int1 = 0; int1 < this.getPartCount(); ++int1) {
				VehiclePart vehiclePart = this.getPartByIndex(int1);
				if (this.updatePart(vehiclePart) && !boolean1) {
					boolean1 = true;
				}

				if (int1 == this.getPartCount() - 1 && boolean1) {
					this.brakeBetweenUpdatesSpeed = 0.0F;
				}
			}
		} else {
			for (int int2 = 0; int2 < this.getPartCount(); ++int2) {
				VehiclePart vehiclePart2 = this.getPartByIndex(int2);
				vehiclePart2.updateSignalDevice();
			}
		}
	}

	public void drainBatteryUpdateHack() {
		boolean boolean1 = this.isEngineRunning();
		if (!boolean1) {
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getDeviceData() != null && vehiclePart.getDeviceData().getIsTurnedOn()) {
					this.updatePart(vehiclePart);
				} else if (vehiclePart.getLight() != null && vehiclePart.getLight().getActive()) {
					this.updatePart(vehiclePart);
				}
			}

			if (this.hasLightbar() && (this.lightbarLightsMode.isEnable() || this.lightbarSirenMode.isEnable()) && this.getBattery() != null) {
				this.updatePart(this.getBattery());
			}
		}
	}

	public void setHeadlightsOn(boolean boolean1) {
		if (this.headlightsOn != boolean1) {
			this.headlightsOn = boolean1;
			if (GameServer.bServer) {
				this.updateFlags = (short)(this.updateFlags | 8);
			}
		}
	}

	public boolean getHeadlightsOn() {
		return this.headlightsOn;
	}

	public boolean getHeadlightCanEmmitLight() {
		if (this.getBatteryCharge() <= 0.0F) {
			return false;
		} else {
			VehiclePart vehiclePart = this.getPartById("HeadlightLeft");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				return true;
			} else {
				vehiclePart = this.getPartById("HeadlightRight");
				return vehiclePart != null && vehiclePart.getInventoryItem() != null;
			}
		}
	}

	public void setStoplightsOn(boolean boolean1) {
		if (this.stoplightsOn != boolean1) {
			this.stoplightsOn = boolean1;
			if (GameServer.bServer) {
				this.updateFlags = (short)(this.updateFlags | 8);
			}
		}
	}

	public boolean getStoplightsOn() {
		return this.stoplightsOn;
	}

	public boolean hasHeadlights() {
		return this.getLightCount() > 0;
	}

	public void addToWorld() {
		if (this.addedToWorld) {
			DebugLog.log("ERROR: added vehicle twice " + this + " id=" + this.VehicleID);
		} else {
			this.addedToWorld = true;
			this.removedFromWorld = false;
			super.addToWorld();
			this.createPhysics();
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getItemContainer() != null) {
					vehiclePart.getItemContainer().addItemsToProcessItems();
				}

				if (vehiclePart.getDeviceData() != null && !GameServer.bServer) {
					ZomboidRadio.getInstance().RegisterDevice(vehiclePart);
				}
			}

			if (this.lightbarSirenMode.isEnable()) {
				this.setLightbarSirenMode(this.lightbarSirenMode.get());
				if (this.sirenStartTime <= 0.0) {
					this.sirenStartTime = GameTime.instance.getWorldAgeHours();
				}
			}

			if (this.chunk != null && this.chunk.jobType != IsoChunk.JobType.SoftReset) {
				PolygonalMap2.instance.addVehicleToWorld(this);
			}

			if (this.engineState != BaseVehicle.engineStateTypes.Idle) {
				this.engineSpeed = 1000.0;
			}

			if (this.chunk != null && this.chunk.jobType != IsoChunk.JobType.SoftReset) {
				this.trySpawnKey();
			}

			if (this.emitter != null) {
				SoundManager.instance.registerEmitter(this.emitter);
			}
		}
	}

	public void removeFromWorld() {
		int int1;
		for (int1 = 0; int1 < this.passengers.length; ++int1) {
			if (this.getPassenger(int1).character != null) {
				for (int int2 = 0; int2 < 4; ++int2) {
					if (this.getPassenger(int1).character == IsoPlayer.players[int2]) {
						return;
					}
				}
			}
		}

		IsoChunk.removeFromCheckedVehicles(this);
		if (this.trace) {
			DebugLog.log("BaseVehicle.removeFromWorld() " + this + " id=" + this.VehicleID);
		}

		if (!this.removedFromWorld) {
			if (!this.addedToWorld) {
				DebugLog.log("ERROR: removing vehicle but addedToWorld=false " + this + " id=" + this.VehicleID);
			}

			this.removedFromWorld = true;
			this.addedToWorld = false;
			for (int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getItemContainer() != null) {
					vehiclePart.getItemContainer().removeItemsFromProcessItems();
				}

				if (vehiclePart.getDeviceData() != null && !GameServer.bServer) {
					ZomboidRadio.getInstance().UnRegisterDevice(vehiclePart);
				}
			}

			if (this.emitter != null) {
				this.emitter.stopAll();
				SoundManager.instance.unregisterEmitter(this.emitter);
				this.emitter = null;
			}

			if (this.hornemitter != null && this.soundHorn != -1L) {
				this.hornemitter.stopAll();
				this.soundHorn = -1L;
			}

			if (this.createdModel) {
				ModelManager.instance.Remove(this);
				this.createdModel = false;
			}

			if (this.getController() != null) {
				Bullet.removeVehicle(this.VehicleID);
				this.physics = null;
			}

			if (!GameServer.bServer && !GameClient.bClient) {
				if (this.VehicleID != -1) {
					VehicleIDMap.instance.remove(this.VehicleID);
				}
			} else {
				VehicleManager.instance.removeFromWorld(this);
			}

			IsoWorld.instance.CurrentCell.addVehicles.remove(this);
			IsoWorld.instance.CurrentCell.vehicles.remove(this);
			PolygonalMap2.instance.removeVehicleFromWorld(this);
			if (GameClient.bClient) {
				this.chunk.vehicles.remove(this);
			}

			super.removeFromWorld();
		}
	}

	public void permanentlyRemove() {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			IsoGameCharacter gameCharacter = this.getCharacter(int1);
			if (gameCharacter != null) {
				if (GameServer.bServer) {
					gameCharacter.sendObjectChange("exitVehicle");
				}

				this.exit(gameCharacter);
			}
		}

		this.removeFromWorld();
		this.removeFromSquare();
		if (this.chunk != null) {
			this.chunk.vehicles.remove(this);
		}
	}

	public VehiclePart getBattery() {
		return this.battery;
	}

	public void setEngineFeature(int int1, int int2, int int3) {
		this.engineQuality = int1;
		this.engineLoudness = (int)((float)int2 / 2.7F);
		this.enginePower = int3;
	}

	public int getEngineQuality() {
		return this.engineQuality;
	}

	public int getEngineLoudness() {
		return this.engineLoudness;
	}

	public int getEnginePower() {
		return this.enginePower;
	}

	public float getBatteryCharge() {
		VehiclePart vehiclePart = this.getBattery();
		return vehiclePart != null && vehiclePart.getInventoryItem() instanceof DrainableComboItem ? ((DrainableComboItem)vehiclePart.getInventoryItem()).getUsedDelta() : 0.0F;
	}

	public int getPartCount() {
		return this.parts.size();
	}

	public VehiclePart getPartByIndex(int int1) {
		return int1 >= 0 && int1 < this.parts.size() ? (VehiclePart)this.parts.get(int1) : null;
	}

	public VehiclePart getPartById(String string) {
		if (string == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				VehicleScript.Part part = vehiclePart.getScriptPart();
				if (part != null && string.equals(part.id)) {
					return vehiclePart;
				}
			}

			return null;
		}
	}

	public int getNumberOfPartsWithContainers() {
		if (this.getScript() == null) {
			return 0;
		} else {
			int int1 = 0;
			for (int int2 = 0; int2 < this.getScript().getPartCount(); ++int2) {
				if (this.getScript().getPart(int2).container != null) {
					++int1;
				}
			}

			return int1;
		}
	}

	public VehiclePart getPartForSeatContainer(int int1) {
		if (this.getScript() != null && int1 >= 0 && int1 < this.getMaxPassengers()) {
			for (int int2 = 0; int2 < this.getPartCount(); ++int2) {
				VehiclePart vehiclePart = this.getPartByIndex(int2);
				if (vehiclePart.getContainerSeatNumber() == int1) {
					return vehiclePart;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public void transmitPartCondition(VehiclePart vehiclePart) {
		if (GameServer.bServer) {
			if (this.parts.contains(vehiclePart)) {
				vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 2048);
				this.updateFlags = (short)(this.updateFlags | 2048);
			}
		}
	}

	public void transmitPartItem(VehiclePart vehiclePart) {
		if (GameServer.bServer) {
			if (this.parts.contains(vehiclePart)) {
				vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 128);
				this.updateFlags = (short)(this.updateFlags | 128);
			}
		}
	}

	public void transmitPartModData(VehiclePart vehiclePart) {
		if (GameServer.bServer) {
			if (this.parts.contains(vehiclePart)) {
				vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 16);
				this.updateFlags = (short)(this.updateFlags | 16);
			}
		}
	}

	public void transmitPartUsedDelta(VehiclePart vehiclePart) {
		if (GameServer.bServer) {
			if (this.parts.contains(vehiclePart)) {
				if (vehiclePart.getInventoryItem() instanceof DrainableComboItem) {
					vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 32);
					this.updateFlags = (short)(this.updateFlags | 32);
				}
			}
		}
	}

	public void transmitPartDoor(VehiclePart vehiclePart) {
		if (GameServer.bServer) {
			if (this.parts.contains(vehiclePart)) {
				if (vehiclePart.getDoor() != null) {
					vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 512);
					this.updateFlags = (short)(this.updateFlags | 512);
				}
			}
		}
	}

	public void transmitPartWindow(VehiclePart vehiclePart) {
		if (GameServer.bServer) {
			if (this.parts.contains(vehiclePart)) {
				if (vehiclePart.getWindow() != null) {
					vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 256);
					this.updateFlags = (short)(this.updateFlags | 256);
				}
			}
		}
	}

	public int getLightCount() {
		return this.lights.size();
	}

	public VehiclePart getLightByIndex(int int1) {
		return int1 >= 0 && int1 < this.lights.size() ? (VehiclePart)this.lights.get(int1) : null;
	}

	public void setZone(String string) {
		this.respawnZone = string;
	}

	public String getZone() {
		return this.respawnZone;
	}

	public boolean isInArea(String string, IsoGameCharacter gameCharacter) {
		if (string != null && this.getScript() != null) {
			VehicleScript.Area area = this.getScript().getAreaById(string);
			if (area == null) {
				return false;
			} else {
				Vector2 vector2 = this.areaPositionLocal(area);
				if (vector2 == null) {
					return false;
				} else {
					Vector3f vector3f = this.tempVector3f_1;
					vector3f.set(gameCharacter.x, gameCharacter.y, this.z);
					this.getLocalPos(vector3f, vector3f);
					float float1 = vector2.x - area.w / 2.0F;
					float float2 = vector2.y - area.h / 2.0F;
					float float3 = vector2.x + area.w / 2.0F;
					float float4 = vector2.y + area.h / 2.0F;
					return vector3f.x >= float1 && vector3f.x < float3 && vector3f.z >= float2 && vector3f.z < float4;
				}
			}
		} else {
			return false;
		}
	}

	public float getAreaDist(String string, IsoGameCharacter gameCharacter) {
		if (string != null && this.getScript() != null) {
			VehicleScript.Area area = this.getScript().getAreaById(string);
			if (area != null) {
				Vector3f vector3f = this.tempVector3f_1;
				vector3f.set(gameCharacter.x, gameCharacter.y, this.z);
				this.getLocalPos(vector3f, vector3f);
				float float1 = Math.abs(area.x - area.w / 2.0F);
				float float2 = Math.abs(area.y - area.h / 2.0F);
				float float3 = Math.abs(area.x + area.w / 2.0F);
				float float4 = Math.abs(area.y + area.h / 2.0F);
				return Math.abs(vector3f.x + float1) + Math.abs(vector3f.z + float2);
			} else {
				return 999.0F;
			}
		} else {
			return 999.0F;
		}
	}

	public Vector2 getAreaCenter(String string) {
		if (string != null && this.getScript() != null) {
			VehicleScript.Area area = this.getScript().getAreaById(string);
			if (area == null) {
				return null;
			} else {
				Vector2 vector2 = this.areaPositionWorld(area);
				return vector2 == null ? null : new Vector2(vector2.x, vector2.y);
			}
		} else {
			return null;
		}
	}

	public boolean isInBounds(float float1, float float2) {
		if (this.getScript() == null) {
			return false;
		} else {
			Vector3f vector3f = this.tempVector3f_1;
			vector3f.set(float1, float2, this.z);
			this.getLocalPos(vector3f, vector3f);
			float float3 = this.getScript().getModelScale();
			float float4 = -this.getScript().getExtents().x / float3 / 2.0F;
			float float5 = -this.getScript().getExtents().z / float3 / 2.0F;
			float float6 = this.getScript().getExtents().x / float3 / 2.0F;
			float float7 = this.getScript().getExtents().z / float3 / 2.0F;
			return vector3f.x >= float4 && vector3f.x < float6 && vector3f.z >= float5 && vector3f.z < float7;
		}
	}

	public boolean canAccessContainer(int int1, IsoGameCharacter gameCharacter) {
		VehiclePart vehiclePart = this.getPartByIndex(int1);
		if (vehiclePart == null) {
			return false;
		} else {
			VehicleScript.Part part = vehiclePart.getScriptPart();
			if (part == null) {
				return false;
			} else if (part.container == null) {
				return false;
			} else if (vehiclePart.getItemType() != null && vehiclePart.getInventoryItem() == null && part.container.capacity == 0) {
				return false;
			} else {
				return part.container.luaTest != null && !part.container.luaTest.isEmpty() ? Boolean.TRUE.equals(this.callLuaBoolean(part.container.luaTest, this, vehiclePart, gameCharacter)) : true;
			}
		}
	}

	public boolean canInstallPart(IsoGameCharacter gameCharacter, VehiclePart vehiclePart) {
		if (!this.parts.contains(vehiclePart)) {
			return false;
		} else {
			KahluaTable kahluaTable = vehiclePart.getTable("install");
			return kahluaTable != null && kahluaTable.rawget("test") instanceof String ? Boolean.TRUE.equals(this.callLuaBoolean((String)kahluaTable.rawget("test"), this, vehiclePart, gameCharacter)) : false;
		}
	}

	public boolean canUninstallPart(IsoGameCharacter gameCharacter, VehiclePart vehiclePart) {
		if (!this.parts.contains(vehiclePart)) {
			return false;
		} else {
			KahluaTable kahluaTable = vehiclePart.getTable("uninstall");
			return kahluaTable != null && kahluaTable.rawget("test") instanceof String ? Boolean.TRUE.equals(this.callLuaBoolean((String)kahluaTable.rawget("test"), this, vehiclePart, gameCharacter)) : false;
		}
	}

	public static void resetLuaFunctions() {
		luaFunctionMap.clear();
	}

	private Object getLuaFunctionObject(String string) {
		Object object = luaFunctionMap.get(string);
		if (object == null) {
			KahluaTable kahluaTable = LuaManager.env;
			if (!string.contains(".")) {
				object = kahluaTable.rawget(string);
			} else {
				String[] stringArray = string.split("\\.");
				for (int int1 = 0; int1 < stringArray.length - 1; ++int1) {
					Object object2 = kahluaTable.rawget(stringArray[int1]);
					if (!(object2 instanceof KahluaTable)) {
						DebugLog.log("ERROR: no such function \"" + string + "\"");
						return null;
					}

					kahluaTable = (KahluaTable)object2;
				}

				object = kahluaTable.rawget(stringArray[stringArray.length - 1]);
			}

			if (!(object instanceof JavaFunction) && !(object instanceof LuaClosure)) {
				DebugLog.log("ERROR: no such function \"" + string + "\"");
			} else {
				luaFunctionMap.put(string, object);
			}
		}

		return object;
	}

	private void callLuaVoid(String string, Object object, Object object2) {
		Object object3 = this.getLuaFunctionObject(string);
		if (object3 != null) {
			LuaManager.caller.protectedCallVoid(LuaManager.thread, object3, object, object2);
		}
	}

	private void callLuaVoid(String string, Object object, Object object2, Object object3) {
		Object object4 = this.getLuaFunctionObject(string);
		if (object4 != null) {
			LuaManager.caller.protectedCallVoid(LuaManager.thread, object4, object, object2, object3);
		}
	}

	private Boolean callLuaBoolean(String string, Object object, Object object2) {
		Object object3 = this.getLuaFunctionObject(string);
		return object3 == null ? null : LuaManager.caller.protectedCallBoolean(LuaManager.thread, object3, object, object2);
	}

	private Boolean callLuaBoolean(String string, Object object, Object object2, Object object3) {
		Object object4 = this.getLuaFunctionObject(string);
		return object4 == null ? null : LuaManager.caller.protectedCallBoolean(LuaManager.thread, object4, object, object2, object3);
	}

	public short getId() {
		return this.VehicleID;
	}

	public void setTireInflation(int int1, float float1) {
	}

	public void setTireRemoved(int int1, boolean boolean1) {
		Bullet.setTireRemoved(this.VehicleID, int1, boolean1);
	}

	private void getAdjacentPositions(IsoGameCharacter gameCharacter, float float1, float float2, float float3, float float4, ArrayList arrayList) {
		BaseVehicle.Passenger passenger = this.getPassenger(this.getSeat(gameCharacter));
		if (passenger != null) {
			float float5 = 0.3F / this.script.getModelScale();
			float float6;
			float float7;
			float float8;
			Vector3f vector3f;
			if (float1 == float3) {
				float6 = float1;
				float7 = passenger.offset.z;
				for (float8 = float7; float8 >= float2 + float5; float8 -= float5 * 2.0F) {
					vector3f = new Vector3f(float6, 0.0F, float8);
					this.getWorldPos(vector3f, vector3f);
					arrayList.add(vector3f);
				}

				for (float8 = float7 + float5 * 2.0F; float8 < float4 - float5; float8 += float5 * 2.0F) {
					vector3f = new Vector3f(float6, 0.0F, float8);
					this.getWorldPos(vector3f, vector3f);
					arrayList.add(vector3f);
				}
			} else {
				float6 = 0.0F;
				float7 = float2;
				for (float8 = float6; float8 >= float1 + float5; float8 -= float5 * 2.0F) {
					vector3f = new Vector3f(float8, 0.0F, float7);
					this.getWorldPos(vector3f, vector3f);
					arrayList.add(vector3f);
				}

				for (float8 = float6 + float5 * 2.0F; float8 < float3 - float5; float8 += float5 * 2.0F) {
					vector3f = new Vector3f(float8, 0.0F, float7);
					this.getWorldPos(vector3f, vector3f);
					arrayList.add(vector3f);
				}
			}
		}
	}

	private boolean testAdjacentPositions(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, float float1, float float2, float float3, float float4) {
		BaseVehicle.Passenger passenger = this.getPassenger(this.getSeat(gameCharacter));
		if (passenger == null) {
			return false;
		} else {
			float float5 = 0.3F / this.script.getModelScale();
			float float6;
			float float7;
			float float8;
			Vector3f vector3f;
			if (float1 == float3) {
				float6 = float1;
				float7 = passenger.offset.z;
				for (float8 = float7; float8 >= float2 + float5; float8 -= float5 * 2.0F) {
					vector3f = this.tempVector3f_1.set(float6, 0.0F, float8);
					this.getWorldPos(vector3f, vector3f);
					vector3f.z = (float)((int)vector3f.z);
					if (this.isAttackPositionFree(vector3f.x, vector3f.y, gameCharacter2)) {
						return true;
					}
				}

				for (float8 = float7 + float5 * 2.0F; float8 < float4 - float5; float8 += float5 * 2.0F) {
					vector3f = this.tempVector3f_1.set(float6, 0.0F, float8);
					this.getWorldPos(vector3f, vector3f);
					vector3f.z = (float)((int)vector3f.z);
					if (this.isAttackPositionFree(vector3f.x, vector3f.y, gameCharacter2)) {
						return true;
					}
				}
			} else {
				float6 = 0.0F;
				float7 = float2;
				for (float8 = float6; float8 >= float1 + float5; float8 -= float5 * 2.0F) {
					vector3f = this.tempVector3f_1.set(float8, 0.0F, float7);
					this.getWorldPos(vector3f, vector3f);
					vector3f.z = (float)((int)vector3f.z);
					if (this.isAttackPositionFree(vector3f.x, vector3f.y, gameCharacter2)) {
						return true;
					}
				}

				for (float8 = float6 + float5 * 2.0F; float8 < float3 - float5; float8 += float5 * 2.0F) {
					vector3f = this.tempVector3f_1.set(float8, 0.0F, float7);
					this.getWorldPos(vector3f, vector3f);
					vector3f.z = (float)((int)vector3f.z);
					if (this.isAttackPositionFree(vector3f.x, vector3f.y, gameCharacter2)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private boolean isAttackPositionFree(float float1, float float2, IsoGameCharacter gameCharacter) {
		float float3 = 0.1F;
		for (int int1 = -1; int1 <= 1; ++int1) {
			for (int int2 = -1; int2 <= 1; ++int2) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1 + int2, (int)float2 + int1, (int)this.getZ());
				if (square != null) {
					for (int int3 = 0; int3 < square.getMovingObjects().size(); ++int3) {
						IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int3);
						if (movingObject != gameCharacter && movingObject instanceof IsoZombie && ((IsoZombie)movingObject).getCurrentState() == AttackVehicleState.instance() && movingObject.DistToSquared(float1, float2) < float3) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private boolean chooseBestAttackPositionAlongEdge(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, float float1, float float2, float float3, float float4) {
		return this.testAdjacentPositions(gameCharacter, gameCharacter2, float1, float2, float3, float4);
	}

	public Vector3f chooseBestAttackPosition(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2) {
		int int1 = this.getSeat(gameCharacter);
		if (int1 == -1) {
			return null;
		} else {
			float float1 = this.script.getExtents().x / this.script.getModelScale();
			float float2 = this.script.getExtents().z / this.script.getModelScale();
			float float3 = 0.3F / this.script.getModelScale();
			float float4 = -float1 / 2.0F - float3;
			float float5 = -float2 / 2.0F - float3;
			float float6 = float1 / 2.0F + float3;
			float float7 = float2 / 2.0F + float3;
			this.initPolyPlusRadiusBounds();
			float4 = this.polyPlusRadiusMinX;
			float6 = this.polyPlusRadiusMaxX;
			float5 = this.polyPlusRadiusMinY;
			float7 = this.polyPlusRadiusMaxY;
			BaseVehicle.Passenger passenger = this.getPassenger(int1);
			VehicleScript.Position position = this.getPassengerPosition(int1, "outside");
			if (position != null && position.getOffset().z() < float5 && this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, float4, float5, float6, float5)) {
				return this.tempVector3f_1;
			} else {
				if (passenger.offset.x > 0.0F) {
					if (this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, float6, -float2 / 2.0F, float6, float2 / 2.0F)) {
						return this.tempVector3f_1;
					}

					if (this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, float4, -float2 / 2.0F, float4, float2 / 2.0F)) {
						return this.tempVector3f_1;
					}
				} else {
					if (this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, float4, -float2 / 2.0F, float4, float2 / 2.0F)) {
						return this.tempVector3f_1;
					}

					if (this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, float6, -float2 / 2.0F, float6, float2 / 2.0F)) {
						return this.tempVector3f_1;
					}
				}

				if (this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, -float1 / 2.0F - float3, float7, float1 / 2.0F + float3, float7)) {
					return this.tempVector3f_1;
				} else {
					return this.chooseBestAttackPositionAlongEdge(gameCharacter, gameCharacter2, -float1 / 2.0F - float3, float5, float1 / 2.0F + float3, float5) ? this.tempVector3f_1 : null;
				}
			}
		}
	}

	public BaseVehicle.MinMaxPosition getMinMaxPosition() {
		BaseVehicle.MinMaxPosition minMaxPosition = new BaseVehicle.MinMaxPosition();
		float float1 = this.getScript().getModelScale();
		float float2 = this.getX();
		float float3 = this.getY();
		float float4 = this.getScript().getExtents().x / float1;
		float float5 = this.getScript().getExtents().z / float1;
		IsoDirections directions = this.getDir();
		switch (directions) {
		case E: 
		
		case W: 
			minMaxPosition.minX = float2 - float4 / 2.0F;
			minMaxPosition.maxX = float2 + float4 / 2.0F;
			minMaxPosition.minY = float3 - float5 / 2.0F;
			minMaxPosition.maxY = float3 + float5 / 2.0F;
			break;
		
		case N: 
		
		case S: 
			minMaxPosition.minX = float2 - float5 / 2.0F;
			minMaxPosition.maxX = float2 + float5 / 2.0F;
			minMaxPosition.minY = float3 - float4 / 2.0F;
			minMaxPosition.maxY = float3 + float4 / 2.0F;
			break;
		
		default: 
			return null;
		
		}
		return minMaxPosition;
	}

	public String getVehicleType() {
		return this.type;
	}

	public void setVehicleType(String string) {
		this.type = string;
	}

	public float getMaxSpeed() {
		return this.maxSpeed;
	}

	public void setMaxSpeed(float float1) {
		this.maxSpeed = float1;
	}

	public void lockServerUpdate(long long1) {
		this.updateLockTimeout = System.currentTimeMillis() + long1;
	}

	public void changeTransmission(TransmissionNumber transmissionNumber) {
		this.transmissionNumber = transmissionNumber;
	}

	public void tryHotwire(int int1) {
		int int2 = Math.max(100 - this.getEngineQuality(), 5);
		int2 = Math.min(int2, 50);
		int int3 = int1 * 4;
		int int4 = int2 + int3;
		boolean boolean1 = false;
		if (Rand.Next(100) <= int4) {
			this.setHotwired(true);
			boolean1 = true;
		} else if (Rand.Next(100) <= 10 - int1) {
			this.setHotwiredBroken(true);
			boolean1 = true;
		} else if (GameServer.bServer) {
			LuaManager.GlobalObject.playServerSound("VehicleHotwireFail", this.square);
		} else if (this.getDriver() != null) {
			this.getDriver().getEmitter().playSound("VehicleHotwireFail");
		}

		if (boolean1 && GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4096);
		}
	}

	public void cheatHotwire(boolean boolean1, boolean boolean2) {
		if (boolean1 != this.hotwired || boolean2 != this.hotwiredBroken) {
			this.hotwired = boolean1;
			this.hotwiredBroken = boolean2;
			if (GameServer.bServer) {
				this.updateFlags = (short)(this.updateFlags | 4096);
			}
		}
	}

	public boolean isKeyIsOnDoor() {
		return this.keyIsOnDoor;
	}

	public void setKeyIsOnDoor(boolean boolean1) {
		this.keyIsOnDoor = boolean1;
	}

	public boolean isHotwired() {
		return this.hotwired;
	}

	public void setHotwired(boolean boolean1) {
		this.hotwired = boolean1;
	}

	public boolean isHotwiredBroken() {
		return this.hotwiredBroken;
	}

	public void setHotwiredBroken(boolean boolean1) {
		this.hotwiredBroken = boolean1;
	}

	public IsoGameCharacter getDriver() {
		BaseVehicle.Passenger passenger = this.getPassenger(0);
		return passenger == null ? null : passenger.character;
	}

	public boolean isKeysInIgnition() {
		return this.keysInIgnition;
	}

	public void setKeysInIgnition(boolean boolean1) {
		IsoGameCharacter gameCharacter = this.getDriver();
		if (gameCharacter != null) {
			this.setAlarmed(false);
			if (!GameClient.bClient || gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				if (!this.isHotwired()) {
					InventoryItem inventoryItem;
					if (!GameServer.bServer && boolean1 && !this.keysInIgnition) {
						inventoryItem = this.getDriver().getInventory().haveThisKeyId(this.getKeyId());
						if (inventoryItem != null) {
							this.setCurrentKey(inventoryItem);
							InventoryItem inventoryItem2 = inventoryItem.getContainer().getContainingItem();
							if (inventoryItem2 instanceof InventoryContainer && "KeyRing".equals(inventoryItem2.getType())) {
								inventoryItem.getModData().rawset("keyRing", (double)inventoryItem2.getID());
							} else if (inventoryItem.hasModData()) {
								inventoryItem.getModData().rawset("keyRing", (Object)null);
							}

							inventoryItem.getContainer().DoRemoveItem(inventoryItem);
							this.keysInIgnition = boolean1;
							if (GameClient.bClient) {
								GameClient.instance.sendClientCommandV((IsoPlayer)this.getDriver(), "vehicle", "putKeyInIgnition", "key", inventoryItem);
							}
						}
					}

					if (!boolean1 && this.keysInIgnition && !GameServer.bServer) {
						if (this.currentKey == null) {
							this.currentKey = this.createVehicleKey();
						}

						inventoryItem = this.getCurrentKey();
						ItemContainer itemContainer = this.getDriver().getInventory();
						if (inventoryItem.hasModData() && inventoryItem.getModData().rawget("keyRing") instanceof Double) {
							Double Double1 = (Double)inventoryItem.getModData().rawget("keyRing");
							InventoryItem inventoryItem3 = itemContainer.getItemWithID(Double1.longValue());
							if (inventoryItem3 instanceof InventoryContainer && "KeyRing".equals(inventoryItem3.getType())) {
								itemContainer = ((InventoryContainer)inventoryItem3).getInventory();
							}

							inventoryItem.getModData().rawset("keyRing", (Object)null);
						}

						itemContainer.addItem(inventoryItem);
						this.setCurrentKey((InventoryItem)null);
						this.keysInIgnition = boolean1;
						if (GameClient.bClient) {
							GameClient.instance.sendClientCommand((IsoPlayer)this.getDriver(), "vehicle", "removeKeyFromIgnition", (KahluaTable)null);
						}
					}
				}
			}
		}
	}

	public void putKeyInIgnition(InventoryItem inventoryItem) {
		if (GameServer.bServer) {
			if (inventoryItem instanceof Key) {
				if (!this.keysInIgnition) {
					this.keysInIgnition = true;
					this.keyIsOnDoor = false;
					this.currentKey = inventoryItem;
					this.updateFlags = (short)(this.updateFlags | 4096);
				}
			}
		}
	}

	public void removeKeyFromIgnition() {
		if (GameServer.bServer) {
			if (this.keysInIgnition) {
				this.keysInIgnition = false;
				this.currentKey = null;
				this.updateFlags = (short)(this.updateFlags | 4096);
			}
		}
	}

	public void putKeyOnDoor(InventoryItem inventoryItem) {
		if (GameServer.bServer) {
			if (inventoryItem instanceof Key) {
				if (!this.keyIsOnDoor) {
					this.keyIsOnDoor = true;
					this.keysInIgnition = false;
					this.currentKey = inventoryItem;
					this.updateFlags = (short)(this.updateFlags | 4096);
				}
			}
		}
	}

	public void removeKeyFromDoor() {
		if (GameServer.bServer) {
			if (this.keyIsOnDoor) {
				this.keyIsOnDoor = false;
				this.currentKey = null;
				this.updateFlags = (short)(this.updateFlags | 4096);
			}
		}
	}

	public void syncKeyInIgnition(boolean boolean1, boolean boolean2, InventoryItem inventoryItem) {
		if (GameClient.bClient) {
			if (!(this.getDriver() instanceof IsoPlayer) || !((IsoPlayer)this.getDriver()).isLocalPlayer()) {
				this.keysInIgnition = boolean1;
				this.keyIsOnDoor = boolean2;
				this.currentKey = inventoryItem;
			}
		}
	}

	private void randomizeContainer(VehiclePart vehiclePart) {
		if (!GameClient.bClient) {
			KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ItemPicker");
			KahluaTable kahluaTable2 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("VehicleDistributions")).rawget(1);
			boolean boolean1 = true;
			KahluaTable kahluaTable3 = null;
			if (kahluaTable2.rawget(this.getScriptName().replaceFirst("Base.", "") + this.getSkinIndex()) != null) {
				boolean1 = false;
				kahluaTable2 = (KahluaTable)kahluaTable2.rawget(this.getScriptName().replaceFirst("Base.", "") + this.getSkinIndex());
			} else {
				kahluaTable2 = (KahluaTable)kahluaTable2.rawget(this.getScriptName().replaceFirst("Base.", ""));
			}

			if (kahluaTable2 != null) {
				if (boolean1 && Rand.Next(100) <= 8 && kahluaTable2.rawget("Specific") != null) {
					kahluaTable2 = (KahluaTable)kahluaTable2.rawget("Specific");
					int int1 = Rand.Next(1, kahluaTable2.size() + 1);
					kahluaTable3 = (KahluaTable)kahluaTable2.rawget(int1);
				} else {
					kahluaTable3 = (KahluaTable)kahluaTable2.rawget("Normal");
				}

				if (kahluaTable3 != null) {
					if (!vehiclePart.getId().contains("Seat") && kahluaTable3.rawget(vehiclePart.getId()) == null) {
						DebugLog.log("NO CONT DISTRIB FOR PART: " + vehiclePart.getId() + " CAR: " + this.getScriptName().replaceFirst("Base.", ""));
					}

					LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("fillContainerType"), kahluaTable3, vehiclePart.getItemContainer(), "", null);
					if (GameServer.bServer && !vehiclePart.getItemContainer().getItems().isEmpty()) {
					}
				}
			} else {
				DebugLog.log("VEHICLE MISSING CONT DISTRIBUTION: " + this.getScriptName().replaceFirst("Base.", ""));
			}
		}
	}

	public boolean hasHorn() {
		return this.script.getSounds().hornEnable;
	}

	public boolean hasLightbar() {
		return this.script.getLightbar().enable;
	}

	public void onHornStart() {
		this.soundHornOn = true;
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
			if (this.script.getSounds().hornEnable) {
				WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 150, 150, false);
			}
		} else {
			if (this.soundHorn != -1L) {
				this.hornemitter.stopSound(this.soundHorn);
			}

			if (this.script.getSounds().hornEnable) {
				this.hornemitter = IsoWorld.instance.getFreeEmitter(this.getX(), this.getY(), (float)((int)this.getZ()));
				this.soundHorn = this.hornemitter.playSoundLoopedImpl(this.script.getSounds().horn);
				this.hornemitter.set3D(this.soundHorn, !this.isAnyListenerInside());
				this.hornemitter.setVolume(this.soundHorn, 1.0F);
				this.hornemitter.setPitch(this.soundHorn, 1.0F);
				if (!GameClient.bClient) {
					WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 150, 150, false);
				}
			}
		}
	}

	public void onHornStop() {
		this.soundHornOn = false;
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
		} else {
			if (this.script.getSounds().hornEnable && this.soundHorn != -1L) {
				this.hornemitter.stopSound(this.soundHorn);
				this.soundHorn = -1L;
			}
		}
	}

	public boolean hasBackSignal() {
		return this.script != null && this.script.getSounds().backSignalEnable;
	}

	public boolean isBackSignalEmitting() {
		return this.soundBackMoveSignal != -1L;
	}

	public void onBackMoveSignalStart() {
		this.soundBackMoveOn = true;
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
		} else {
			if (this.soundBackMoveSignal != -1L) {
				this.emitter.stopSound(this.soundBackMoveSignal);
			}

			if (this.script.getSounds().backSignalEnable) {
				this.soundBackMoveSignal = this.emitter.playSoundLoopedImpl(this.script.getSounds().backSignal);
				this.emitter.set3D(this.soundBackMoveSignal, !this.isAnyListenerInside());
			}
		}
	}

	public void onBackMoveSignalStop() {
		this.soundBackMoveOn = false;
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
		} else {
			if (this.script.getSounds().backSignalEnable && this.soundBackMoveSignal != -1L) {
				this.emitter.stopSound(this.soundBackMoveSignal);
				this.soundBackMoveSignal = -1L;
			}
		}
	}

	public int getLightbarLightsMode() {
		return this.lightbarLightsMode.get();
	}

	public int getLightbarSirenMode() {
		return this.lightbarSirenMode.get();
	}

	public void setLightbarLightsMode(int int1) {
		this.lightbarLightsMode.set(int1);
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
		}
	}

	public void setLightbarSirenMode(int int1) {
		if (this.soundSirenSignal != -1L) {
			this.getEmitter().stopSound(this.soundSirenSignal);
			this.soundSirenSignal = -1L;
		}

		this.lightbarSirenMode.set(int1);
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
		} else {
			if (this.lightbarSirenMode.isEnable() && this.getBatteryCharge() > 0.0F) {
				this.soundSirenSignal = this.getEmitter().playSoundLoopedImpl(this.lightbarSirenMode.getSoundName(this.script.getLightbar()));
				this.getEmitter().set3D(this.soundSirenSignal, !this.isAnyListenerInside());
			}
		}
	}

	public HashMap getChoosenParts() {
		return this.choosenParts;
	}

	public void setChoosenParts(HashMap hashMap) {
		this.choosenParts = hashMap;
	}

	public float getMass() {
		return this.mass;
	}

	public void setMass(float float1) {
		this.mass = float1;
	}

	public float getInitialMass() {
		return this.initialMass;
	}

	public void setInitialMass(float float1) {
		this.initialMass = float1;
	}

	public void updateTotalMass() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			if (vehiclePart.getItemContainer() != null) {
				float1 += vehiclePart.getItemContainer().getCapacityWeight();
			}

			if (vehiclePart.getInventoryItem() != null) {
				float1 += vehiclePart.getInventoryItem().getWeight();
			}
		}

		this.setMass((float)Math.round(this.getInitialMass() + float1));
		if (WorldSimulation.instance.created) {
			Bullet.setVehicleMass(this.VehicleID, this.getMass());
		}
	}

	public float getBrakingForce() {
		return this.brakingForce;
	}

	public void setBrakingForce(float float1) {
		this.brakingForce = float1;
	}

	public float getBaseQuality() {
		return this.baseQuality;
	}

	public void setBaseQuality(float float1) {
		this.baseQuality = float1;
	}

	public float getCurrentSteering() {
		return this.currentSteering;
	}

	public void setCurrentSteering(float float1) {
		this.currentSteering = float1;
	}

	public boolean isDoingOffroad() {
		return this.getCurrentSquare() != null && this.getCurrentSquare().getFloor() != null && !this.getCurrentSquare().getFloor().getSprite().getName().contains("carpentry_02") && !this.getCurrentSquare().getFloor().getSprite().getName().contains("blends_street") && !this.getCurrentSquare().getFloor().getSprite().getName().contains("floors_exterior_street");
	}

	public boolean isBraking() {
		return this.isBraking;
	}

	public void setBraking(boolean boolean1) {
		this.isBraking = boolean1;
		if (boolean1 && this.brakeBetweenUpdatesSpeed == 0.0F) {
			this.brakeBetweenUpdatesSpeed = Math.abs(this.getCurrentSpeedKmHour());
		}
	}

	public void updatePartStats() {
		this.setBrakingForce(0.0F);
		this.engineLoudness = (int)((double)this.getScript().getEngineLoudness() * SandboxOptions.instance.ZombieAttractionMultiplier.getValue() / 2.0);
		boolean boolean1 = false;
		for (int int1 = 0; int1 < this.getPartCount(); ++int1) {
			VehiclePart vehiclePart = this.getPartByIndex(int1);
			if (vehiclePart.getInventoryItem() != null) {
				float float1;
				if (vehiclePart.getInventoryItem().getBrakeForce() > 0.0F) {
					float1 = VehiclePart.getNumberByCondition(vehiclePart.getInventoryItem().getBrakeForce(), (float)vehiclePart.getInventoryItem().getCondition(), 5.0F);
					float1 += float1 / 50.0F * (float)vehiclePart.getMechanicSkillInstaller();
					this.setBrakingForce(this.getBrakingForce() + float1);
				}

				if (vehiclePart.getInventoryItem().getWheelFriction() > 0.0F) {
					vehiclePart.setWheelFriction(0.0F);
					float1 = VehiclePart.getNumberByCondition(vehiclePart.getInventoryItem().getWheelFriction(), (float)vehiclePart.getInventoryItem().getCondition(), 0.2F);
					float1 += 0.1F * (float)vehiclePart.getMechanicSkillInstaller();
					float1 = Math.min(2.3F, float1);
					vehiclePart.setWheelFriction(float1);
				}

				if (vehiclePart.getInventoryItem().getSuspensionCompression() > 0.0F) {
					vehiclePart.setSuspensionCompression(VehiclePart.getNumberByCondition(vehiclePart.getInventoryItem().getSuspensionCompression(), (float)vehiclePart.getInventoryItem().getCondition(), 0.6F));
					vehiclePart.setSuspensionDamping(VehiclePart.getNumberByCondition(vehiclePart.getInventoryItem().getSuspensionDamping(), (float)vehiclePart.getInventoryItem().getCondition(), 0.6F));
				}

				if (vehiclePart.getInventoryItem().getEngineLoudness() > 0.0F) {
					vehiclePart.setEngineLoudness(VehiclePart.getNumberByCondition(vehiclePart.getInventoryItem().getEngineLoudness(), (float)vehiclePart.getInventoryItem().getCondition(), 10.0F));
					this.engineLoudness = (int)((float)this.engineLoudness * (1.0F + (100.0F - vehiclePart.getEngineLoudness()) / 100.0F));
					boolean1 = true;
				}
			}
		}

		if (!boolean1) {
			this.engineLoudness *= 2;
		}
	}

	public void setRust(float float1) {
		this.rust = float1;
	}

	public void updateBulletStats() {
		if (!this.getScriptName().contains("Burnt") && WorldSimulation.instance.created) {
			float[] floatArray = vehicleParams;
			double double1 = 2.4;
			byte byte1 = 5;
			if (WorldSimulation.instance.created) {
				double double2;
				float float1;
				if (this.isInForest() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0F) {
					double2 = (double)Rand.Next(0.08F, 0.18F);
					float1 = 0.7F;
					byte1 = 3;
				} else if (this.isDoingOffroad() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0F) {
					double2 = (double)Rand.Next(0.05F, 0.15F);
					float1 = 0.7F;
				} else {
					if (Math.abs(this.getCurrentSpeedKmHour()) > 1.0F && Rand.Next(100) < 10) {
						double2 = (double)Rand.Next(0.05F, 0.15F);
					} else {
						double2 = 0.0;
					}

					float1 = 1.0F;
				}

				if (RainManager.isRaining()) {
					float1 -= 0.3F;
				}

				VehicleScript.Wheel wheel = this.script.getWheel(0);
				Vector3f vector3f = this.tempVector3f_1.set(wheel.offset.x, wheel.offset.y, wheel.offset.z);
				this.getWorldPos(vector3f, vector3f);
				VehiclePart vehiclePart = this.getPartById("TireFrontLeft");
				VehiclePart vehiclePart2 = this.getPartById("SuspensionFrontLeft");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					floatArray[0] = 1.0F;
					floatArray[4] = Math.min(vehiclePart.getContainerContentAmount() / (float)(vehiclePart.getContainerCapacity() - 10), 1.0F);
					floatArray[8] = float1 * vehiclePart.getWheelFriction();
					if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
						floatArray[12] = vehiclePart2.getSuspensionDamping();
						floatArray[16] = vehiclePart2.getSuspensionCompression();
					} else {
						floatArray[12] = 0.1F;
						floatArray[16] = 0.1F;
					}

					if (Rand.Next(byte1) == 0) {
						floatArray[20] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[20] = 0.0F;
					}
				} else {
					floatArray[0] = 0.0F;
					floatArray[4] = 30.0F;
					floatArray[8] = 0.0F;
					floatArray[12] = 2.88F;
					floatArray[16] = 3.83F;
					if (Rand.Next(byte1) == 0) {
						floatArray[20] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[20] = 0.0F;
					}
				}

				wheel = this.script.getWheel(1);
				vector3f = this.tempVector3f_1.set(wheel.offset.x, wheel.offset.y, wheel.offset.z);
				this.getWorldPos(vector3f, vector3f);
				vehiclePart = this.getPartById("TireFrontRight");
				vehiclePart2 = this.getPartById("SuspensionFrontRight");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					floatArray[1] = 1.0F;
					floatArray[5] = Math.min(vehiclePart.getContainerContentAmount() / (float)(vehiclePart.getContainerCapacity() - 10), 1.0F);
					floatArray[9] = float1 * vehiclePart.getWheelFriction();
					if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
						floatArray[13] = vehiclePart2.getSuspensionDamping();
						floatArray[17] = vehiclePart2.getSuspensionCompression();
					} else {
						floatArray[13] = 0.1F;
						floatArray[17] = 0.1F;
					}

					if (Rand.Next(byte1) == 0) {
						floatArray[21] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[21] = 0.0F;
					}
				} else {
					floatArray[1] = 0.0F;
					floatArray[5] = 30.0F;
					floatArray[9] = 0.0F;
					floatArray[13] = 2.88F;
					floatArray[17] = 3.83F;
					if (Rand.Next(byte1) == 0) {
						floatArray[21] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[21] = 0.0F;
					}
				}

				wheel = this.script.getWheel(2);
				vector3f = this.tempVector3f_1.set(wheel.offset.x, wheel.offset.y, wheel.offset.z);
				this.getWorldPos(vector3f, vector3f);
				vehiclePart = this.getPartById("TireRearLeft");
				vehiclePart2 = this.getPartById("SuspensionRearLeft");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					floatArray[2] = 1.0F;
					floatArray[6] = Math.min(vehiclePart.getContainerContentAmount() / (float)(vehiclePart.getContainerCapacity() - 10), 1.0F);
					floatArray[10] = float1 * vehiclePart.getWheelFriction();
					if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
						floatArray[14] = vehiclePart2.getSuspensionDamping();
						floatArray[18] = vehiclePart2.getSuspensionCompression();
					} else {
						floatArray[14] = 0.1F;
						floatArray[18] = 0.1F;
					}

					if (Rand.Next(byte1) == 0) {
						floatArray[22] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[22] = 0.0F;
					}
				} else {
					floatArray[2] = 0.0F;
					floatArray[6] = 30.0F;
					floatArray[10] = 0.0F;
					floatArray[14] = 2.88F;
					floatArray[18] = 3.83F;
					if (Rand.Next(byte1) == 0) {
						floatArray[22] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[22] = 0.0F;
					}
				}

				wheel = this.script.getWheel(3);
				vector3f = this.tempVector3f_1.set(wheel.offset.x, wheel.offset.y, wheel.offset.z);
				this.getWorldPos(vector3f, vector3f);
				vehiclePart = this.getPartById("TireRearRight");
				vehiclePart2 = this.getPartById("SuspensionRearRight");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					floatArray[3] = 1.0F;
					floatArray[7] = Math.min(vehiclePart.getContainerContentAmount() / (float)(vehiclePart.getContainerCapacity() - 10), 1.0F);
					floatArray[11] = float1 * vehiclePart.getWheelFriction();
					if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
						floatArray[15] = vehiclePart2.getSuspensionDamping();
						floatArray[19] = vehiclePart2.getSuspensionCompression();
					} else {
						floatArray[15] = 0.1F;
						floatArray[19] = 0.1F;
					}

					if (Rand.Next(byte1) == 0) {
						floatArray[23] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[23] = 0.0F;
					}
				} else {
					floatArray[3] = 0.0F;
					floatArray[7] = 30.0F;
					floatArray[11] = 0.0F;
					floatArray[15] = 2.88F;
					floatArray[19] = 3.83F;
					if (Rand.Next(byte1) == 0) {
						floatArray[23] = (float)(Math.sin(double1 * (double)vector3f.x()) * Math.sin(double1 * (double)vector3f.y()) * double2);
					} else {
						floatArray[23] = 0.0F;
					}
				}

				Bullet.setVehicleParams(this.VehicleID, floatArray);
			}
		}
	}

	public void setActiveInBullet(boolean boolean1) {
		if (boolean1 || !this.isEngineRunning()) {
			;
		}
	}

	public boolean areAllDoorsLocked() {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			VehiclePart vehiclePart = this.getPassengerDoor(int1);
			if (vehiclePart != null && vehiclePart.getDoor() != null && !vehiclePart.getDoor().isLocked()) {
				return false;
			}
		}

		return true;
	}

	public float getRemainingFuelPercentage() {
		VehiclePart vehiclePart = this.getPartById("GasTank");
		return vehiclePart == null ? 0.0F : vehiclePart.getContainerContentAmount() / (float)vehiclePart.getContainerCapacity() * 100.0F;
	}

	public int getMechanicalID() {
		return this.mechanicalID;
	}

	public void setMechanicalID(int int1) {
		this.mechanicalID = int1;
	}

	public boolean needPartsUpdate() {
		return this.needPartsUpdate;
	}

	public void setNeedPartsUpdate(boolean boolean1) {
		this.needPartsUpdate = boolean1;
	}

	public VehiclePart getHeater() {
		return this.getPartById("Heater");
	}

	public int windowsOpen() {
		int int1 = 0;
		for (int int2 = 0; int2 < this.getPartCount(); ++int2) {
			VehiclePart vehiclePart = this.getPartByIndex(int2);
			if (vehiclePart.window != null && vehiclePart.window.open) {
				++int1;
			}
		}

		return int1;
	}

	public boolean isAlarmed() {
		return this.alarmed;
	}

	public void setAlarmed(boolean boolean1) {
		this.alarmed = boolean1;
	}

	public void triggerAlarm() {
		if (this.alarmed) {
			this.alarmed = false;
			this.alarmTime = Rand.Next(1500, 3000);
			this.alarmAccumulator = 0.0F;
		}
	}

	private void doAlarm() {
		if (this.alarmTime > 0) {
			if (this.getBatteryCharge() <= 0.0F) {
				if (this.soundHornOn) {
					this.onHornStop();
				}

				this.alarmTime = -1;
				return;
			}

			this.alarmAccumulator += GameTime.instance.getMultiplier() / 1.6F;
			if (this.alarmAccumulator >= (float)this.alarmTime) {
				this.onHornStop();
				this.setHeadlightsOn(false);
				this.alarmTime = -1;
				return;
			}

			int int1 = (int)this.alarmAccumulator / 20;
			if (!this.soundHornOn && int1 % 2 == 0) {
				this.onHornStart();
				this.setHeadlightsOn(true);
			}

			if (this.soundHornOn && int1 % 2 == 1) {
				this.onHornStop();
				this.setHeadlightsOn(false);
			}
		}
	}

	public boolean isMechanicUIOpen() {
		return this.mechanicUIOpen;
	}

	public void setMechanicUIOpen(boolean boolean1) {
		this.mechanicUIOpen = boolean1;
	}

	public void damagePlayers(float float1) {
		if (SandboxOptions.instance.PlayerDamageFromCrash.getValue()) {
			if (!GameClient.bClient) {
				for (int int1 = 0; int1 < this.passengers.length; ++int1) {
					if (this.getPassenger(int1).character != null) {
						IsoGameCharacter gameCharacter = this.getPassenger(int1).character;
						if (GameServer.bServer && gameCharacter instanceof IsoPlayer) {
							GameServer.sendPlayerDamagedByCarCrash((IsoPlayer)gameCharacter, float1);
						} else {
							this.addRandomDamageFromCrash(gameCharacter, float1);
						}
					}
				}
			}
		}
	}

	public void addRandomDamageFromCrash(IsoGameCharacter gameCharacter, float float1) {
		int int1 = 1;
		if (float1 > 40.0F) {
			int1 = Rand.Next(1, 3);
		}

		if (float1 > 70.0F) {
			int1 = Rand.Next(2, 4);
		}

		int int2 = 0;
		int int3;
		for (int3 = 0; int3 < gameCharacter.getVehicle().getPartCount(); ++int3) {
			VehiclePart vehiclePart = gameCharacter.getVehicle().getPartByIndex(int3);
			if (vehiclePart.window != null && vehiclePart.getCondition() < 15) {
				++int2;
			}
		}

		for (int3 = 0; int3 < int1; ++int3) {
			int int4 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
			BodyPart bodyPart = gameCharacter.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int4));
			float float2 = Math.max(Rand.Next(float1 - 15.0F, float1), 5.0F);
			if (gameCharacter.HasTrait("FastHealer")) {
				float2 = (float)((double)float2 * 0.8);
			} else if (gameCharacter.HasTrait("SlowHealer")) {
				float2 = (float)((double)float2 * 1.2);
			}

			switch (SandboxOptions.instance.InjurySeverity.getValue()) {
			case 1: 
				float2 *= 0.5F;
				break;
			
			case 3: 
				float2 *= 1.5F;
			
			}

			float2 *= this.getScript().getPlayerDamageProtection();
			float2 = (float)((double)float2 * 0.9);
			bodyPart.AddDamage(float2);
			if (float2 > 40.0F && Rand.Next(12) == 0) {
				bodyPart.generateDeepWound();
			} else if (float2 > 50.0F && Rand.Next(10) == 0 && SandboxOptions.instance.BoneFracture.getValue()) {
				if (bodyPart.getType() != BodyPartType.Neck && bodyPart.getType() != BodyPartType.Groin) {
					bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0F, float2 + 10.0F), Rand.Next(float2 + 20.0F, float2 + 30.0F)));
				} else {
					bodyPart.generateDeepWound();
				}
			}

			if (float2 > 30.0F && Rand.Next(12 - int2) == 0) {
				bodyPart = gameCharacter.getBodyDamage().setScratchedWindow();
				if (Rand.Next(5) == 0) {
					bodyPart.generateDeepWound();
					bodyPart.setHaveGlass(true);
				}
			}
		}
	}

	public void hitVehicle(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		float float1 = 1.0F;
		if (handWeapon == null) {
			handWeapon = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
		}

		float1 = (float)handWeapon.getDoorDamage();
		if ((float)Rand.Next(100) < handWeapon.CriticalChance) {
			float1 *= 10.0F;
		}

		VehiclePart vehiclePart = this.getNearestBodyworkPart(gameCharacter);
		if (vehiclePart != null) {
			VehicleWindow vehicleWindow = vehiclePart.getWindow();
			for (int int1 = 0; int1 < vehiclePart.getChildCount(); ++int1) {
				VehiclePart vehiclePart2 = vehiclePart.getChild(int1);
				if (vehiclePart2.getWindow() != null) {
					vehicleWindow = vehiclePart2.getWindow();
					break;
				}
			}

			if (vehicleWindow != null && vehicleWindow.getHealth() > 0) {
				vehicleWindow.damage((int)float1);
				this.transmitPartWindow(vehiclePart);
				if (vehicleWindow.getHealth() == 0) {
					VehicleManager.sendSoundFromServer(this, (byte)1);
				}
			} else {
				vehiclePart.setCondition(vehiclePart.getCondition() - (int)float1);
				this.transmitPartItem(vehiclePart);
			}

			vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 2048);
			this.updateFlags = (short)(this.updateFlags | 2048);
		} else {
			Vector3f vector3f = this.tempVector3f_1.set(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ());
			this.getLocalPos(vector3f, vector3f);
			boolean boolean1 = vector3f.x > 0.0F;
			if (boolean1) {
				this.addDamageFront((int)float1);
			} else {
				this.addDamageRear((int)float1);
			}

			this.updateFlags = (short)(this.updateFlags | 2048);
		}
	}

	public boolean isTrunkLocked() {
		VehiclePart vehiclePart = this.getPartById("TrunkDoor");
		if (vehiclePart == null) {
			vehiclePart = this.getPartById("DoorRear");
		}

		return vehiclePart != null && vehiclePart.getDoor() != null && vehiclePart.getInventoryItem() != null ? vehiclePart.getDoor().isLocked() : false;
	}

	public void setTrunkLocked(boolean boolean1) {
		VehiclePart vehiclePart = this.getPartById("TrunkDoor");
		if (vehiclePart == null) {
			vehiclePart = this.getPartById("DoorRear");
		}

		if (vehiclePart != null && vehiclePart.getDoor() != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.getDoor().setLocked(boolean1);
			if (GameServer.bServer) {
				this.transmitPartDoor(vehiclePart);
			}
		}
	}

	public VehiclePart getNearestBodyworkPart(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < this.getPartCount(); ++int1) {
			VehiclePart vehiclePart = this.getPartByIndex(int1);
			if (("door".equals(vehiclePart.getCategory()) || "bodywork".equals(vehiclePart.getCategory())) && this.isInArea(vehiclePart.getArea(), gameCharacter) && vehiclePart.getCondition() > 0) {
				return vehiclePart;
			}
		}

		return null;
	}

	public void setSirenStartTime(double double1) {
		this.sirenStartTime = double1;
	}

	public double getSirenStartTime() {
		return this.sirenStartTime;
	}

	public boolean sirenShutoffTimeExpired() {
		double double1 = SandboxOptions.instance.SirenShutoffHours.getValue();
		if (double1 <= 0.0) {
			return false;
		} else {
			double double2 = GameTime.instance.getWorldAgeHours();
			if (this.sirenStartTime > double2) {
				this.sirenStartTime = double2;
			}

			return this.sirenStartTime + double1 < double2;
		}
	}

	public void repair() {
		for (int int1 = 0; int1 < this.getPartCount(); ++int1) {
			VehiclePart vehiclePart = this.getPartByIndex(int1);
			vehiclePart.repair();
		}
	}

	public static float getFakeSpeedModifier() {
		if (!GameClient.bClient && !GameServer.bServer) {
			return 1.0F;
		} else {
			float float1 = (float)ServerOptions.instance.SpeedLimit.getValue();
			return 120.0F / Math.min(float1, 120.0F);
		}
	}

	public boolean isAnyListenerInside() {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			IsoGameCharacter gameCharacter = this.getCharacter(int1);
			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer() && !gameCharacter.HasTrait("Deaf")) {
				return true;
			}
		}

		return false;
	}

	public boolean couldCrawlerAttackPassenger(IsoGameCharacter gameCharacter) {
		int int1 = this.getSeat(gameCharacter);
		return int1 == -1 ? false : false;
	}

	public boolean isGoodCar() {
		return this.isGoodCar;
	}

	public void setGoodCar(boolean boolean1) {
		this.isGoodCar = boolean1;
	}

	public InventoryItem getCurrentKey() {
		return this.currentKey;
	}

	public void setCurrentKey(InventoryItem inventoryItem) {
		this.currentKey = inventoryItem;
	}

	public boolean isInForest() {
		return this.getSquare() != null && this.getSquare().getZone() != null && ("Forest".equals(this.getSquare().getZone().getType()) || "DeepForest".equals(this.getSquare().getZone().getType()) || "FarmLand".equals(this.getSquare().getZone().getType()));
	}

	public float getOffroadEfficiency() {
		return this.isInForest() ? this.script.getOffroadEfficiency() * 1.5F : this.script.getOffroadEfficiency() * 2.0F;
	}

	public void doChrHitImpulse(IsoObject object) {
		float float1 = 22.0F;
		Vector3f vector3f = this.getLinearVelocity(this.tempVector3f_1);
		vector3f.y = 0.0F;
		Vector3f vector3f2 = this.tempVector3f_2.set(this.x - object.getX(), 0.0F, this.z - object.getY());
		vector3f2.normalize();
		vector3f.mul((Vector3fc)vector3f2);
		float float2 = vector3f.length();
		float2 = Math.min(float2, float1);
		if (!(float2 < 0.05F)) {
			if (GameServer.bServer) {
				if (object instanceof IsoZombie) {
					((IsoZombie)object).thumpFlag = 1;
				}
			} else {
				SoundManager.instance.PlayWorldSound("ZombieThumpGeneric", object.square, 0.0F, 20.0F, 0.9F, true);
			}

			Vector3f vector3f3 = this.tempVector3f_2;
			vector3f3.set(this.x - object.getX(), 0.0F, this.y - object.getY());
			vector3f3.normalize();
			vector3f.normalize();
			float float3 = vector3f.dot(vector3f3);
			vector3f.mul(float2);
			this.ApplyImpulse(object, this.getMass() * 3.0F * float2 / float1 * Math.abs(float3));
		}
	}

	public boolean isDoColor() {
		return this.doColor;
	}

	public void setDoColor(boolean boolean1) {
		this.doColor = boolean1;
	}

	public float getBrakeSpeedBetweenUpdate() {
		return this.brakeBetweenUpdatesSpeed;
	}

	public IsoGridSquare getSquare() {
		return this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
	}

	public void setColor(float float1, float float2, float float3) {
		this.colorValue = float1;
		this.colorSaturation = float2;
		this.colorHue = float3;
	}

	public boolean isRemovedFromWorld() {
		return this.removedFromWorld;
	}

	public float getInsideTemperature() {
		VehiclePart vehiclePart = this.getPartById("PassengerCompartment");
		float float1 = 0.0F;
		if (vehiclePart != null && vehiclePart.getModData() != null) {
			if (vehiclePart.getModData().rawget("temperature") != null) {
				float1 += ((Double)vehiclePart.getModData().rawget("temperature")).floatValue();
			}

			if (vehiclePart.getModData().rawget("windowtemperature") != null) {
				float1 += ((Double)vehiclePart.getModData().rawget("windowtemperature")).floatValue();
			}
		}

		return float1;
	}

	public static class ServerVehicleState {
		private static final float delta = 0.01F;
		public float x = -1.0F;
		public float y;
		public float z;
		public Quaternionf orient = new Quaternionf();
		public short flags = 0;
		public byte netPlayerAuthorization = 0;
		public int netPlayerId = 0;

		public void setAuthorization(BaseVehicle baseVehicle) {
			this.netPlayerAuthorization = baseVehicle.netPlayerAuthorization;
			this.netPlayerId = baseVehicle.netPlayerId;
		}

		public boolean shouldSend(BaseVehicle baseVehicle) {
			if (baseVehicle.getController() == null) {
				return false;
			} else if (baseVehicle.updateLockTimeout > System.currentTimeMillis()) {
				return false;
			} else {
				this.flags = 0;
				if (Math.abs(this.x - baseVehicle.x) > 0.01F || Math.abs(this.y - baseVehicle.y) > 0.01F || Math.abs(this.z - baseVehicle.z) > 0.01F || Math.abs(this.orient.x - baseVehicle.savedRot.x) > 0.01F || Math.abs(this.orient.y - baseVehicle.savedRot.y) > 0.01F || Math.abs(this.orient.z - baseVehicle.savedRot.z) > 0.01F || Math.abs(this.orient.w - baseVehicle.savedRot.w) > 0.01F) {
					this.flags = (short)(this.flags | 2);
				}

				if (this.netPlayerAuthorization != baseVehicle.netPlayerAuthorization || this.netPlayerId != baseVehicle.netPlayerId) {
					this.flags = (short)(this.flags | 16384);
				}

				this.flags |= baseVehicle.updateFlags;
				return this.flags != 0;
			}
		}
	}

	protected static class UpdateFlags {
		public static final short Full = 1;
		public static final short PositionOrientation = 2;
		public static final short Engine = 4;
		public static final short Lights = 8;
		public static final short PartModData = 16;
		public static final short PartUsedDelta = 32;
		public static final short PartModels = 64;
		public static final short PartItem = 128;
		public static final short PartWindow = 256;
		public static final short PartDoor = 512;
		public static final short Sounds = 1024;
		public static final short PartCondition = 2048;
		public static final short UpdateCarProperties = 4096;
		public static final short EngineSound = 8192;
		public static final short Authorization = 16384;
		public static final short AllPartFlags = 19440;
	}

	public class Passenger {
		public IsoGameCharacter character;
		Vector3f offset = new Vector3f();
	}

	public class MinMaxPosition {
		public float minX;
		public float maxX;
		public float minY;
		public float maxY;
	}

	private static class EngineRPMData {
		public String SoundName;
		public float RPM_Min;
		public float RPM_Min_Pitch;
		public float RPM_Min_Volume;
		public float RPM_Mid;
		public float RPM_Mid_Pitch;
		public float RPM_Mid_Volume;
		public float RPM_Max;
		public float RPM_Max_Pitch;
		public float RPM_Max_Volume;

		public EngineRPMData(String string, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9) {
			this.SoundName = string;
			this.RPM_Min = float1;
			this.RPM_Min_Pitch = float2;
			this.RPM_Min_Volume = float3;
			this.RPM_Mid = float4;
			this.RPM_Mid_Pitch = float5;
			this.RPM_Mid_Volume = float6;
			this.RPM_Max = float7;
			this.RPM_Max_Pitch = float8;
			this.RPM_Max_Volume = float9;
		}
	}

	public static class WheelInfo {
		public float steering;
		public float rotation;
		public float skidInfo;
		public float suspensionLength;
	}
	public static enum engineStateTypes {

		Idle,
		Starting,
		RetryingStarting,
		StartingSuccess,
		StartingFailed,
		Running,
		Stalling,
		ShutingDown,
		StartingFailedNoPower,
		Values;
	}

	public static class ModelInfo {
		public VehiclePart part;
		public VehicleScript.Model scriptModel;
		public int wheelIndex;
		public Matrix4f renderTransform = new Matrix4f();
		public Vector3f renderOrigin = new Vector3f();
	}

	private static final class VehicleImpulse {
		final Vector3f impulse;
		final Vector3f rel_pos;
		boolean enable;
		static final ArrayDeque pool = new ArrayDeque();

		private VehicleImpulse() {
			this.impulse = new Vector3f();
			this.rel_pos = new Vector3f();
			this.enable = false;
		}

		static BaseVehicle.VehicleImpulse alloc() {
			return pool.isEmpty() ? new BaseVehicle.VehicleImpulse() : (BaseVehicle.VehicleImpulse)pool.pop();
		}

		void release() {
			pool.push(this);
		}

		VehicleImpulse(Object object) {
			this();
		}
	}
}
