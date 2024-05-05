package zombie.vehicles;

import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.IFMODParameterUpdater;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.ZombieFallDownState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.FMODParameter;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterVehicleBrake;
import zombie.audio.parameters.ParameterVehicleEngineCondition;
import zombie.audio.parameters.ParameterVehicleGear;
import zombie.audio.parameters.ParameterVehicleLoad;
import zombie.audio.parameters.ParameterVehicleRPM;
import zombie.audio.parameters.ParameterVehicleRoadMaterial;
import zombie.audio.parameters.ParameterVehicleSkid;
import zombie.audio.parameters.ParameterVehicleSpeed;
import zombie.audio.parameters.ParameterVehicleSteer;
import zombie.audio.parameters.ParameterVehicleTireMissing;
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
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.physics.Bullet;
import zombie.core.physics.CarController;
import zombie.core.physics.Transform;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.GameKeyboard;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
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
import zombie.iso.IsoLightSource;
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
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.weather.ClimateManager;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PassengerMap;
import zombie.network.ServerOptions;
import zombie.popman.ObjectPool;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.TextManager;
import zombie.ui.UIManager;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public final class BaseVehicle extends IsoMovingObject implements Thumpable,IFMODParameterUpdater {
	public static final float RADIUS = 0.3F;
	public static final int FADE_DISTANCE = 15;
	public static final int RANDOMIZE_CONTAINER_CHANCE = 100;
	public static final byte authorizationOnServer = 0;
	public static final byte authorizationSimulation = 1;
	public static final byte authorizationServerSimulation = 2;
	public static final byte authorizationOwner = 3;
	public static final byte authorizationServerOwner = 4;
	private static final Vector3f _UNIT_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	private static final Vector2[] testVecs1 = new Vector2[4];
	private static final Vector2[] testVecs2 = new Vector2[4];
	private static final PolygonalMap2.VehiclePoly tempPoly = new PolygonalMap2.VehiclePoly();
	public static final boolean YURI_FORCE_FIELD = false;
	public static boolean RENDER_TO_TEXTURE = false;
	public static float CENTER_OF_MASS_MAGIC = 0.7F;
	private static final float[] vehicleParams = new float[24];
	public static Texture vehicleShadow = null;
	public int justBreakConstraintTimer = 0;
	public BaseVehicle wasTowedBy = null;
	protected static final ColorInfo inf = new ColorInfo();
	private static final float[] lowRiderParam = new float[4];
	private final BaseVehicle.VehicleImpulse impulseFromServer = new BaseVehicle.VehicleImpulse();
	private final BaseVehicle.VehicleImpulse[] impulseFromSquishedZombie = new BaseVehicle.VehicleImpulse[4];
	private final ArrayList impulseFromHitZombie = new ArrayList();
	private final int netPlayerTimeoutMax = 30;
	private final Vector4f tempVector4f = new Vector4f();
	public final ArrayList models = new ArrayList();
	public IsoChunk chunk;
	public boolean polyDirty = true;
	private boolean polyGarageCheck = true;
	private float radiusReductionInGarage = 0.0F;
	public short VehicleID = -1;
	public int sqlID = -1;
	public boolean serverRemovedFromWorld = false;
	public boolean trace = false;
	public VehicleInterpolation interpolation = null;
	public boolean waitFullUpdate;
	public float throttle = 0.0F;
	public double engineSpeed;
	public TransmissionNumber transmissionNumber;
	public final UpdateLimit transmissionChangeTime = new UpdateLimit(1000L);
	public boolean hasExtendOffset = true;
	public boolean hasExtendOffsetExiting = false;
	public float savedPhysicsZ = Float.NaN;
	public final Quaternionf savedRot = new Quaternionf();
	public final Transform jniTransform = new Transform();
	public float jniSpeed;
	public boolean jniIsCollide;
	public final Vector3f jniLinearVelocity = new Vector3f();
	public final Vector3f netLinearVelocity = new Vector3f();
	public byte netPlayerAuthorization = 0;
	public int netPlayerId = 0;
	public int netPlayerTimeout = 0;
	public int authSimulationHash = 0;
	public long authSimulationTime = 0L;
	public int frontEndDurability = 100;
	public int rearEndDurability = 100;
	public float rust = 0.0F;
	public float colorHue = 0.0F;
	public float colorSaturation = 0.0F;
	public float colorValue = 0.0F;
	public int currentFrontEndDurability = 100;
	public int currentRearEndDurability = 100;
	public float collideX = -1.0F;
	public float collideY = -1.0F;
	public final PolygonalMap2.VehiclePoly shadowCoord = new PolygonalMap2.VehiclePoly();
	public BaseVehicle.engineStateTypes engineState;
	public long engineLastUpdateStateTime;
	public static final int MAX_WHEELS = 4;
	public final BaseVehicle.WheelInfo[] wheelInfo;
	public boolean skidding;
	public long skidSound;
	public long ramSound;
	public long ramSoundTime;
	private VehicleEngineRPM vehicleEngineRPM;
	public final long[] new_EngineSoundId;
	private long combinedEngineSound;
	public int engineSoundIndex;
	public BaseSoundEmitter hornemitter;
	public float startTime;
	public boolean headlightsOn;
	public boolean stoplightsOn;
	public boolean windowLightsOn;
	public boolean soundHornOn;
	public boolean soundBackMoveOn;
	public final LightbarLightsMode lightbarLightsMode;
	public final LightbarSirenMode lightbarSirenMode;
	private final IsoLightSource leftLight1;
	private final IsoLightSource leftLight2;
	private final IsoLightSource rightLight1;
	private final IsoLightSource rightLight2;
	private int leftLightIndex;
	private int rightLightIndex;
	public final BaseVehicle.ServerVehicleState[] connectionState;
	protected BaseVehicle.Passenger[] passengers;
	protected String scriptName;
	protected VehicleScript script;
	protected final ArrayList parts;
	protected VehiclePart battery;
	protected int engineQuality;
	protected int engineLoudness;
	protected int enginePower;
	protected long engineCheckTime;
	protected final ArrayList lights;
	protected boolean createdModel;
	protected final Vector3f lastLinearVelocity;
	protected int skinIndex;
	protected CarController physics;
	protected boolean bCreated;
	protected final PolygonalMap2.VehiclePoly poly;
	protected final PolygonalMap2.VehiclePoly polyPlusRadius;
	protected boolean bDoDamageOverlay;
	protected boolean loaded;
	protected short updateFlags;
	protected long updateLockTimeout;
	final UpdateLimit limitPhysicSend;
	final UpdateLimit limitPhysicValid;
	public boolean addedToWorld;
	boolean removedFromWorld;
	private float polyPlusRadiusMinX;
	private float polyPlusRadiusMinY;
	private float polyPlusRadiusMaxX;
	private float polyPlusRadiusMaxY;
	private float maxSpeed;
	private boolean keyIsOnDoor;
	private boolean hotwired;
	private boolean hotwiredBroken;
	private boolean keysInIgnition;
	private long soundHorn;
	private long soundScrapePastPlant;
	private long soundBackMoveSignal;
	public long soundSirenSignal;
	private final HashMap choosenParts;
	private String type;
	private String respawnZone;
	private float mass;
	private float initialMass;
	private float brakingForce;
	private float baseQuality;
	private float currentSteering;
	private boolean isBraking;
	private int mechanicalID;
	private boolean needPartsUpdate;
	private boolean alarmed;
	private int alarmTime;
	private float alarmAccumulator;
	private double sirenStartTime;
	private boolean mechanicUIOpen;
	private boolean isGoodCar;
	private InventoryItem currentKey;
	private boolean doColor;
	private float brekingSlowFactor;
	private final ArrayList brekingObjectsList;
	private final UpdateLimit limitUpdate;
	public byte keySpawned;
	public final Matrix4f vehicleTransform;
	public final Matrix4f renderTransform;
	private final Matrix4f tempMatrix4fLWJGL_1;
	private final Quaternionf tempQuat4f;
	private final Transform tempTransform;
	private final Transform tempTransform2;
	private final Transform tempTransform3;
	private BaseSoundEmitter emitter;
	private float brakeBetweenUpdatesSpeed;
	public long physicActiveCheck;
	private long constraintChangedTime;
	private AnimationPlayer m_animPlayer;
	public String specificDistributionId;
	private boolean bAddThumpWorldSound;
	private final SurroundVehicle m_surroundVehicle;
	private boolean regulator;
	private float regulatorSpeed;
	private static final HashMap s_PartToMaskMap = new HashMap();
	private static final Byte BYTE_ZERO = 0;
	private final HashMap bloodIntensity;
	private boolean OptionBloodDecals;
	private long createPhysicsTime;
	private BaseVehicle vehicleTowing;
	private BaseVehicle vehicleTowedBy;
	public int constraintTowing;
	private int vehicleTowingID;
	private int vehicleTowedByID;
	private String towAttachmentSelf;
	private String towAttachmentOther;
	private float towConstraintZOffset;
	private final ParameterVehicleBrake parameterVehicleBrake;
	private final ParameterVehicleEngineCondition parameterVehicleEngineCondition;
	private final ParameterVehicleGear parameterVehicleGear;
	private final ParameterVehicleLoad parameterVehicleLoad;
	private final ParameterVehicleRoadMaterial parameterVehicleRoadMaterial;
	private final ParameterVehicleRPM parameterVehicleRPM;
	private final ParameterVehicleSkid parameterVehicleSkid;
	private final ParameterVehicleSpeed parameterVehicleSpeed;
	private final ParameterVehicleSteer parameterVehicleSteer;
	private final ParameterVehicleTireMissing parameterVehicleTireMissing;
	private final FMODParameterList fmodParameters;
	public static final ThreadLocal TL_vector2_pool = ThreadLocal.withInitial(BaseVehicle.Vector2ObjectPool::new);
	public static final ThreadLocal TL_vector2f_pool = ThreadLocal.withInitial(BaseVehicle.Vector2fObjectPool::new);
	public static final ThreadLocal TL_vector3f_pool = ThreadLocal.withInitial(BaseVehicle.Vector3fObjectPool::new);
	public static final ThreadLocal TL_matrix4f_pool = ThreadLocal.withInitial(BaseVehicle.Matrix4fObjectPool::new);
	public static final ThreadLocal TL_quaternionf_pool = ThreadLocal.withInitial(BaseVehicle.QuaternionfObjectPool::new);
	public static final float PHYSICS_Z_SCALE = 0.82F;
	public static float PLUS_RADIUS = 0.15F;
	private int zombiesHits;
	private long zombieHitTimestamp;
	public static final int MASK1_FRONT = 0;
	public static final int MASK1_REAR = 4;
	public static final int MASK1_DOOR_RIGHT_FRONT = 8;
	public static final int MASK1_DOOR_RIGHT_REAR = 12;
	public static final int MASK1_DOOR_LEFT_FRONT = 1;
	public static final int MASK1_DOOR_LEFT_REAR = 5;
	public static final int MASK1_WINDOW_RIGHT_FRONT = 9;
	public static final int MASK1_WINDOW_RIGHT_REAR = 13;
	public static final int MASK1_WINDOW_LEFT_FRONT = 2;
	public static final int MASK1_WINDOW_LEFT_REAR = 6;
	public static final int MASK1_WINDOW_FRONT = 10;
	public static final int MASK1_WINDOW_REAR = 14;
	public static final int MASK1_GUARD_RIGHT_FRONT = 3;
	public static final int MASK1_GUARD_RIGHT_REAR = 7;
	public static final int MASK1_GUARD_LEFT_FRONT = 11;
	public static final int MASK1_GUARD_LEFT_REAR = 15;
	public static final int MASK2_ROOF = 0;
	public static final int MASK2_LIGHT_RIGHT_FRONT = 4;
	public static final int MASK2_LIGHT_LEFT_FRONT = 8;
	public static final int MASK2_LIGHT_RIGHT_REAR = 12;
	public static final int MASK2_LIGHT_LEFT_REAR = 1;
	public static final int MASK2_BRAKE_RIGHT = 5;
	public static final int MASK2_BRAKE_LEFT = 9;
	public static final int MASK2_LIGHTBAR_RIGHT = 13;
	public static final int MASK2_LIGHTBAR_LEFT = 2;
	public static final int MASK2_HOOD = 6;
	public static final int MASK2_BOOT = 10;
	public float forcedFriction;

	public int getSqlId() {
		return this.sqlID;
	}

	public static Vector2 allocVector2() {
		return (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
	}

	public static void releaseVector2(Vector2 vector2) {
		((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
	}

	private static Vector3f allocVector3f() {
		return (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
	}

	private static void releaseVector3f(Vector3f vector3f) {
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
	}

	public BaseVehicle(IsoCell cell) {
		super(cell, false);
		this.engineState = BaseVehicle.engineStateTypes.Idle;
		this.wheelInfo = new BaseVehicle.WheelInfo[4];
		this.skidding = false;
		this.vehicleEngineRPM = null;
		this.new_EngineSoundId = new long[8];
		this.combinedEngineSound = 0L;
		this.engineSoundIndex = 0;
		this.hornemitter = null;
		this.startTime = 0.0F;
		this.headlightsOn = false;
		this.stoplightsOn = false;
		this.windowLightsOn = false;
		this.soundHornOn = false;
		this.soundBackMoveOn = false;
		this.lightbarLightsMode = new LightbarLightsMode();
		this.lightbarSirenMode = new LightbarSirenMode();
		this.leftLight1 = new IsoLightSource(0, 0, 0, 1.0F, 0.0F, 0.0F, 8);
		this.leftLight2 = new IsoLightSource(0, 0, 0, 1.0F, 0.0F, 0.0F, 8);
		this.rightLight1 = new IsoLightSource(0, 0, 0, 0.0F, 0.0F, 1.0F, 8);
		this.rightLight2 = new IsoLightSource(0, 0, 0, 0.0F, 0.0F, 1.0F, 8);
		this.leftLightIndex = -1;
		this.rightLightIndex = -1;
		this.connectionState = new BaseVehicle.ServerVehicleState[512];
		this.passengers = new BaseVehicle.Passenger[1];
		this.parts = new ArrayList();
		this.lights = new ArrayList();
		this.createdModel = false;
		this.lastLinearVelocity = new Vector3f();
		this.skinIndex = -1;
		this.poly = new PolygonalMap2.VehiclePoly();
		this.polyPlusRadius = new PolygonalMap2.VehiclePoly();
		this.bDoDamageOverlay = false;
		this.loaded = false;
		this.updateLockTimeout = 0L;
		this.limitPhysicSend = new UpdateLimit(100L);
		this.limitPhysicValid = new UpdateLimit(1000L);
		this.addedToWorld = false;
		this.removedFromWorld = false;
		this.polyPlusRadiusMinX = -123.0F;
		this.keyIsOnDoor = false;
		this.hotwired = false;
		this.hotwiredBroken = false;
		this.keysInIgnition = false;
		this.soundHorn = -1L;
		this.soundScrapePastPlant = -1L;
		this.soundBackMoveSignal = -1L;
		this.soundSirenSignal = -1L;
		this.choosenParts = new HashMap();
		this.type = "";
		this.mass = 0.0F;
		this.initialMass = 0.0F;
		this.brakingForce = 0.0F;
		this.baseQuality = 0.0F;
		this.currentSteering = 0.0F;
		this.isBraking = false;
		this.mechanicalID = 0;
		this.needPartsUpdate = false;
		this.alarmed = false;
		this.alarmTime = -1;
		this.sirenStartTime = 0.0;
		this.mechanicUIOpen = false;
		this.isGoodCar = false;
		this.currentKey = null;
		this.doColor = true;
		this.brekingSlowFactor = 0.0F;
		this.brekingObjectsList = new ArrayList();
		this.limitUpdate = new UpdateLimit(333L);
		this.keySpawned = 0;
		this.vehicleTransform = new Matrix4f();
		this.renderTransform = new Matrix4f();
		this.tempMatrix4fLWJGL_1 = new Matrix4f();
		this.tempQuat4f = new Quaternionf();
		this.tempTransform = new Transform();
		this.tempTransform2 = new Transform();
		this.tempTransform3 = new Transform();
		this.brakeBetweenUpdatesSpeed = 0.0F;
		this.physicActiveCheck = -1L;
		this.constraintChangedTime = -1L;
		this.m_animPlayer = null;
		this.specificDistributionId = null;
		this.bAddThumpWorldSound = false;
		this.m_surroundVehicle = new SurroundVehicle(this);
		this.regulator = false;
		this.regulatorSpeed = 0.0F;
		this.bloodIntensity = new HashMap();
		this.OptionBloodDecals = false;
		this.createPhysicsTime = -1L;
		this.vehicleTowing = null;
		this.vehicleTowedBy = null;
		this.constraintTowing = -1;
		this.vehicleTowingID = -1;
		this.vehicleTowedByID = -1;
		this.towAttachmentSelf = null;
		this.towAttachmentOther = null;
		this.towConstraintZOffset = 0.0F;
		this.parameterVehicleBrake = new ParameterVehicleBrake(this);
		this.parameterVehicleEngineCondition = new ParameterVehicleEngineCondition(this);
		this.parameterVehicleGear = new ParameterVehicleGear(this);
		this.parameterVehicleLoad = new ParameterVehicleLoad(this);
		this.parameterVehicleRoadMaterial = new ParameterVehicleRoadMaterial(this);
		this.parameterVehicleRPM = new ParameterVehicleRPM(this);
		this.parameterVehicleSkid = new ParameterVehicleSkid(this);
		this.parameterVehicleSpeed = new ParameterVehicleSpeed(this);
		this.parameterVehicleSteer = new ParameterVehicleSteer(this);
		this.parameterVehicleTireMissing = new ParameterVehicleTireMissing(this);
		this.fmodParameters = new FMODParameterList();
		this.zombiesHits = 0;
		this.zombieHitTimestamp = 0L;
		this.forcedFriction = -1.0F;
		this.setCollidable(false);
		this.respawnZone = new String("");
		this.scriptName = "Base.PickUpTruck";
		this.passengers[0] = new BaseVehicle.Passenger();
		this.waitFullUpdate = false;
		this.savedRot.w = 1.0F;
		int int1;
		for (int1 = 0; int1 < this.wheelInfo.length; ++int1) {
			this.wheelInfo[int1] = new BaseVehicle.WheelInfo();
		}

		if (GameClient.bClient) {
			this.interpolation = new VehicleInterpolation(VehicleManager.physicsDelay);
		}

		this.setKeyId(Rand.Next(100000000));
		this.engineSpeed = 0.0;
		this.transmissionNumber = TransmissionNumber.N;
		this.rust = (float)Rand.Next(0, 2);
		this.jniIsCollide = false;
		for (int1 = 0; int1 < 4; ++int1) {
			lowRiderParam[int1] = 0.0F;
		}

		this.fmodParameters.add(this.parameterVehicleBrake);
		this.fmodParameters.add(this.parameterVehicleEngineCondition);
		this.fmodParameters.add(this.parameterVehicleGear);
		this.fmodParameters.add(this.parameterVehicleLoad);
		this.fmodParameters.add(this.parameterVehicleRPM);
		this.fmodParameters.add(this.parameterVehicleRoadMaterial);
		this.fmodParameters.add(this.parameterVehicleSkid);
		this.fmodParameters.add(this.parameterVehicleSpeed);
		this.fmodParameters.add(this.parameterVehicleSteer);
		this.fmodParameters.add(this.parameterVehicleTireMissing);
	}

	public static void LoadAllVehicleTextures() {
		DebugLog.General.println("BaseVehicle.LoadAllVehicleTextures...");
		ArrayList arrayList = ScriptManager.instance.getAllVehicleScripts();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			VehicleScript vehicleScript = (VehicleScript)iterator.next();
			LoadVehicleTextures(vehicleScript);
		}
	}

	public static void LoadVehicleTextures(VehicleScript vehicleScript) {
		if (SystemDisabler.doVehiclesWithoutTextures) {
			VehicleScript.Skin skin = vehicleScript.getSkin(0);
			skin.textureData = LoadVehicleTexture(skin.texture);
			skin.textureDataMask = LoadVehicleTexture("vehicles_placeholder_mask");
			skin.textureDataDamage1Overlay = LoadVehicleTexture("vehicles_placeholder_damage1overlay");
			skin.textureDataDamage1Shell = LoadVehicleTexture("vehicles_placeholder_damage1shell");
			skin.textureDataDamage2Overlay = LoadVehicleTexture("vehicles_placeholder_damage2overlay");
			skin.textureDataDamage2Shell = LoadVehicleTexture("vehicles_placeholder_damage2shell");
			skin.textureDataLights = LoadVehicleTexture("vehicles_placeholder_lights");
			skin.textureDataRust = LoadVehicleTexture("vehicles_placeholder_rust");
		} else {
			for (int int1 = 0; int1 < vehicleScript.getSkinCount(); ++int1) {
				VehicleScript.Skin skin2 = vehicleScript.getSkin(int1);
				skin2.copyMissingFrom(vehicleScript.getTextures());
				LoadVehicleTextures(skin2);
			}
		}
	}

	private static void LoadVehicleTextures(VehicleScript.Skin skin) {
		skin.textureData = LoadVehicleTexture(skin.texture);
		if (skin.textureMask != null) {
			skin.textureDataMask = LoadVehicleTexture(skin.textureMask, 0);
		}

		skin.textureDataDamage1Overlay = LoadVehicleTexture(skin.textureDamage1Overlay);
		skin.textureDataDamage1Shell = LoadVehicleTexture(skin.textureDamage1Shell);
		skin.textureDataDamage2Overlay = LoadVehicleTexture(skin.textureDamage2Overlay);
		skin.textureDataDamage2Shell = LoadVehicleTexture(skin.textureDamage2Shell);
		skin.textureDataLights = LoadVehicleTexture(skin.textureLights);
		skin.textureDataRust = LoadVehicleTexture(skin.textureRust);
		skin.textureDataShadow = LoadVehicleTexture(skin.textureShadow);
	}

	public static Texture LoadVehicleTexture(String string) {
		byte byte1 = 0;
		int int1 = byte1 | (TextureID.bUseCompression ? 4 : 0);
		return LoadVehicleTexture(string, int1);
	}

	public static Texture LoadVehicleTexture(String string, int int1) {
		return StringUtils.isNullOrWhitespace(string) ? null : Texture.getSharedTexture("media/textures/" + string + ".png", int1);
	}

	public static float getFakeSpeedModifier() {
		if (!GameClient.bClient && !GameServer.bServer) {
			return 1.0F;
		} else {
			float float1 = (float)ServerOptions.instance.SpeedLimit.getValue();
			return 120.0F / Math.min(float1, 120.0F);
		}
	}

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
			this.impulseFromServer.enable = false;
			this.impulseFromServer.release();
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
				this.colorHue = 0.15F;
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
			this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, Float.isNaN(this.savedPhysicsZ) ? this.getZ() : this.savedPhysicsZ, this.getY() - WorldSimulation.instance.offsetY);
			this.physics = new CarController(this);
			this.savedPhysicsZ = Float.NaN;
			this.createPhysicsTime = System.currentTimeMillis();
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
				ModelManager.instance.addVehicle(this);
				this.createdModel = true;
			}

			this.updateTransform();
			this.lights.clear();
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
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
			this.randomizeContainers();
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
		Color color = Color.HSBtoRGB(this.colorHue, this.colorSaturation * 0.5F, this.colorValue);
		inventoryItem.setColor(color);
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

	public void setGeneralPartCondition(float float1, float float2) {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			vehiclePart.setGeneralCondition((InventoryItem)null, float1, float2);
		}
	}

	private void createParts() {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			ArrayList arrayList = vehiclePart.getItemType();
			if (vehiclePart.bCreated && arrayList != null && !arrayList.isEmpty() && vehiclePart.getInventoryItem() == null && vehiclePart.getTable("install") == null) {
				vehiclePart.bCreated = false;
			} else if ((arrayList == null || arrayList.isEmpty()) && vehiclePart.getInventoryItem() != null) {
				vehiclePart.item = null;
			}

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

		if (this.hasLightbar() && this.getScript().rightSirenCol != null && this.getScript().leftSirenCol != null) {
			this.leftLight1.r = this.leftLight2.r = this.getScript().leftSirenCol.r;
			this.leftLight1.g = this.leftLight2.g = this.getScript().leftSirenCol.g;
			this.leftLight1.b = this.leftLight2.b = this.getScript().leftSirenCol.b;
			this.rightLight1.r = this.rightLight2.r = this.getScript().rightSirenCol.r;
			this.rightLight1.g = this.rightLight2.g = this.getScript().rightSirenCol.g;
			this.rightLight1.b = this.rightLight2.b = this.getScript().rightSirenCol.b;
		}
	}

	public CarController getController() {
		return this.physics;
	}

	public SurroundVehicle getSurroundVehicle() {
		return this.m_surroundVehicle;
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

	public Texture getShadowTexture() {
		if (this.getScript() != null) {
			VehicleScript.Skin skin = this.getScript().getTextures();
			if (this.getSkinIndex() >= 0 && this.getSkinIndex() < this.getScript().getSkinCount()) {
				skin = this.getScript().getSkin(this.getSkinIndex());
			}

			if (skin.textureDataShadow != null) {
				return skin.textureDataShadow;
			}
		}

		if (vehicleShadow == null) {
			byte byte1 = 0;
			int int1 = byte1 | (TextureID.bUseCompression ? 4 : 0);
			vehicleShadow = Texture.getSharedTexture("media/vehicleShadow.png", int1);
		}

		return vehicleShadow;
	}

	public VehicleScript getScript() {
		return this.script;
	}

	public void setScript(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
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
				this.scriptName = this.script.getFullName();
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
							ItemContainer itemContainer = new ItemContainer(part.id, (IsoGridSquare)null, this);
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

			this.m_surroundVehicle.reset();
		}
	}

	public String getScriptName() {
		return this.scriptName;
	}

	public void setScriptName(String string) {
		assert string == null || string.contains(".");
		this.scriptName = string;
	}

	public void setScript() {
		this.setScript(this.scriptName);
	}

	public void scriptReloaded() {
		this.tempTransform2.setIdentity();
		if (this.physics != null) {
			this.getWorldTransform(this.tempTransform2);
			this.tempTransform2.basis.getUnnormalizedRotation(this.savedRot);
			this.breakConstraint(false, false);
			Bullet.removeVehicle(this.VehicleID);
			this.physics = null;
		}

		if (this.createdModel) {
			ModelManager.instance.Remove(this);
			this.createdModel = false;
		}

		this.vehicleEngineRPM = null;
		int int1;
		for (int1 = 0; int1 < this.parts.size(); ++int1) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
			vehiclePart.setInventoryItem((InventoryItem)null);
			vehiclePart.bCreated = false;
		}

		this.setScript(this.scriptName);
		this.createPhysics();
		if (this.script != null) {
			for (int1 = 0; int1 < this.passengers.length; ++int1) {
				BaseVehicle.Passenger passenger = this.passengers[int1];
				if (passenger != null && passenger.character != null) {
					VehicleScript.Position position = this.getPassengerPosition(int1, "inside");
					if (position != null) {
						passenger.offset.set((Vector3fc)position.offset);
					}
				}
			}
		}

		this.polyDirty = true;
		if (this.isEngineRunning()) {
			this.engineDoShuttingDown();
			this.engineState = BaseVehicle.engineStateTypes.Idle;
		}

		if (this.addedToWorld) {
			PolygonalMap2.instance.removeVehicleFromWorld(this);
			PolygonalMap2.instance.addVehicleToWorld(this);
		}
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

				if (modelInfo.m_animPlayer != null) {
					modelInfo.m_animPlayer.reset();
					modelInfo.m_animPlayer = null;
				}

				this.models.remove(int1);
				if (this.createdModel) {
					ModelManager.instance.Remove(this);
					ModelManager.instance.addVehicle(this);
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
			modelInfo2.modelScript = ScriptManager.instance.getModelScript(model.file);
			modelInfo2.wheelIndex = vehiclePart.getWheelIndex();
			this.models.add(modelInfo2);
			if (this.createdModel) {
				ModelManager.instance.Remove(this);
				ModelManager.instance.addVehicle(this);
			}

			vehiclePart.updateFlags = (short)(vehiclePart.updateFlags | 64);
			this.updateFlags = (short)(this.updateFlags | 64);
			return modelInfo2;
		} else {
			return null;
		}
	}

	protected BaseVehicle.ModelInfo getModelInfoForPart(VehiclePart vehiclePart) {
		for (int int1 = 0; int1 < this.models.size(); ++int1) {
			BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int1);
			if (modelInfo.part == vehiclePart) {
				return modelInfo;
			}
		}

		return null;
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
		return this.getWorldPos(vector3f.x, vector3f.y, vector3f.z, vector3f2, vehicleScript);
	}

	public Vector3f getWorldPos(float float1, float float2, float float3, Vector3f vector3f, VehicleScript vehicleScript) {
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.origin.set(0.0F, 0.0F, 0.0F);
		vector3f.set(float1, float2, float3);
		transform.transform(vector3f);
		float float4 = this.jniTransform.origin.x + WorldSimulation.instance.offsetX;
		float float5 = this.jniTransform.origin.z + WorldSimulation.instance.offsetY;
		float float6 = this.jniTransform.origin.y / 2.46F;
		vector3f.set(float4 + vector3f.x, float5 + vector3f.z, float6 + vector3f.y);
		return vector3f;
	}

	public Vector3f getWorldPos(Vector3f vector3f, Vector3f vector3f2) {
		return this.getWorldPos(vector3f.x, vector3f.y, vector3f.z, vector3f2, this.getScript());
	}

	public Vector3f getWorldPos(float float1, float float2, float float3, Vector3f vector3f) {
		return this.getWorldPos(float1, float2, float3, vector3f, this.getScript());
	}

	public Vector3f getLocalPos(Vector3f vector3f, Vector3f vector3f2) {
		return this.getLocalPos(vector3f.x, vector3f.y, vector3f.z, vector3f2);
	}

	public Vector3f getLocalPos(float float1, float float2, float float3, Vector3f vector3f) {
		Transform transform = this.getWorldTransform(this.tempTransform);
		transform.inverse();
		vector3f.set(float1 - WorldSimulation.instance.offsetX, 0.0F, float2 - WorldSimulation.instance.offsetY);
		transform.transform(vector3f);
		return vector3f;
	}

	public Vector3f getPassengerLocalPos(int int1, Vector3f vector3f) {
		BaseVehicle.Passenger passenger = this.getPassenger(int1);
		return passenger == null ? null : vector3f.set((Vector3fc)this.script.getModel().getOffset()).add(passenger.offset);
	}

	public Vector3f getPassengerWorldPos(int int1, Vector3f vector3f) {
		BaseVehicle.Passenger passenger = this.getPassenger(int1);
		return passenger == null ? null : this.getPassengerPositionWorldPos(passenger.offset.x, passenger.offset.y, passenger.offset.z, vector3f);
	}

	public Vector3f getPassengerPositionWorldPos(VehicleScript.Position position, Vector3f vector3f) {
		return this.getPassengerPositionWorldPos(position.offset.x, position.offset.y, position.offset.z, vector3f);
	}

	public Vector3f getPassengerPositionWorldPos(float float1, float float2, float float3, Vector3f vector3f) {
		vector3f.set((Vector3fc)this.script.getModel().offset);
		vector3f.add(float1, float2, float3);
		this.getWorldPos(vector3f.x, vector3f.y, vector3f.z, vector3f);
		vector3f.z = (float)((int)this.getZ());
		return vector3f;
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

	public boolean isPositionOnLeftOrRight(float float1, float float2) {
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		this.getLocalPos(float1, float2, 0.0F, vector3f);
		float1 = vector3f.x;
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		Vector3f vector3f2 = this.script.getExtents();
		Vector3f vector3f3 = this.script.getCenterOfMassOffset();
		float float3 = vector3f3.x - vector3f2.x / 2.0F;
		float float4 = vector3f3.x + vector3f2.x / 2.0F;
		return float1 < float3 * 0.98F || float1 > float4 * 0.98F;
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
				this.playCharacterAnim(gameCharacter, anim, true);
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
			if (anim != null && !StringUtils.isNullOrWhitespace(anim.anim)) {
				BaseVehicle.ModelInfo modelInfo = this.getModelInfoForPart(vehiclePart);
				if (modelInfo != null) {
					AnimationPlayer animationPlayer = modelInfo.getAnimationPlayer();
					if (animationPlayer != null && animationPlayer.isReady()) {
						if (animationPlayer.getMultiTrack().getIndexOfTrack(modelInfo.m_track) != -1) {
							animationPlayer.getMultiTrack().removeTrack(modelInfo.m_track);
						}

						modelInfo.m_track = null;
						SkinningData skinningData = animationPlayer.getSkinningData();
						if (skinningData == null || skinningData.AnimationClips.containsKey(anim.anim)) {
							AnimationTrack animationTrack = animationPlayer.play(anim.anim, anim.bLoop);
							modelInfo.m_track = animationTrack;
							if (animationTrack != null) {
								animationTrack.setLayerIdx(0);
								animationTrack.BlendDelta = 1.0F;
								animationTrack.SpeedDelta = anim.rate;
								animationTrack.IsPlaying = anim.bAnimate;
								animationTrack.reverse = anim.bReverse;
								if (!modelInfo.modelScript.boneWeights.isEmpty()) {
									animationTrack.setBoneWeights(modelInfo.modelScript.boneWeights);
									animationTrack.initBoneWeights(skinningData);
								}

								if (vehiclePart.getWindow() != null) {
									animationTrack.setCurrentTimeValue(animationTrack.getDuration() * vehiclePart.getWindow().getOpenDelta());
								}
							}
						}
					}
				}
			}
		}
	}

	public void playActorAnim(VehiclePart vehiclePart, String string, IsoGameCharacter gameCharacter) {
		if (gameCharacter != null) {
			if (this.parts.contains(vehiclePart)) {
				VehicleScript.Anim anim = vehiclePart.getAnimById("Actor" + string);
				if (anim != null) {
					this.playCharacterAnim(gameCharacter, anim, !"EngineDoor".equals(vehiclePart.getId()));
				}
			}
		}
	}

	private void playCharacterAnim(IsoGameCharacter gameCharacter, VehicleScript.Anim anim, boolean boolean1) {
		gameCharacter.PlayAnimUnlooped(anim.anim);
		gameCharacter.getSpriteDef().setFrameSpeedPerFrame(anim.rate);
		gameCharacter.getLegsSprite().Animate = true;
		Vector3f vector3f = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		if (anim.angle.lengthSquared() != 0.0F) {
			Matrix4f matrix4f = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).alloc();
			matrix4f.rotationXYZ((float)Math.toRadians((double)anim.angle.x), (float)Math.toRadians((double)anim.angle.y), (float)Math.toRadians((double)anim.angle.z));
			vector3f.rotate(matrix4f.getNormalizedRotation(this.tempQuat4f));
			((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).release(matrix4f);
		}

		Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
		vector2.set(vector3f.x, vector3f.z);
		gameCharacter.DirectionFromVector(vector2);
		((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
		gameCharacter.setForwardDirection(vector3f.x, vector3f.z);
		if (gameCharacter.getAnimationPlayer() != null) {
			gameCharacter.getAnimationPlayer().setTargetAngle(gameCharacter.getForwardDirection().getDirection());
			if (boolean1) {
				gameCharacter.getAnimationPlayer().setAngleToTarget();
			}
		}

		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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
					Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
					if (position.area == null) {
						this.getPassengerPositionWorldPos(position, vector3f);
					} else {
						VehicleScript.Area area = this.script.getAreaById(position.area);
						Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
						Vector2 vector22 = this.areaPositionWorld4PlayerInteract(area, vector2);
						vector3f.x = vector22.x;
						vector3f.y = vector22.y;
						vector3f.z = 0.0F;
						((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
					}

					gameCharacter.setX(vector3f.x);
					gameCharacter.setY(vector3f.y);
					gameCharacter.setZ(0.0F);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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
				Vector3f vector3f = this.getWorldPos(anim.offset, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
				gameCharacter.setX(vector3f.x);
				gameCharacter.setY(vector3f.y);
				gameCharacter.setZ(0.0F);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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
				Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				vector3f.set(0.0F, 0.0F, 0.0F);
				this.setPassenger(int1, gameCharacter, vector3f);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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
				Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				vector3f.set(0.0F, 0.0F, 0.0F);
				this.setPassenger(int1, gameCharacter, vector3f);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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
				Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
				for (int int1 = 0; int1 < vehicleScript.getPassengerCount(); ++int1) {
					if (!this.isEnterBlocked(gameCharacter, int1) && !this.isSeatOccupied(int1)) {
						VehicleScript.Position position = this.getPassengerPosition(int1, "outside");
						float float1;
						float float2;
						float float3;
						if (position != null) {
							this.getPassengerPositionWorldPos(position, vector3f);
							float1 = vector3f.x;
							float2 = vector3f.y;
							this.getPassengerPositionWorldPos(0.0F, position.offset.y, position.offset.z, vector3f2);
							vector2.set(vector3f2.x - gameCharacter.getX(), vector3f2.y - gameCharacter.getY());
							vector2.normalize();
							float3 = vector2.dot(gameCharacter.getForwardDirection());
							if (float3 > 0.5F && IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), float1, float2) < 1.0F) {
								((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
								((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
								((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
								return int1;
							}
						}

						position = this.getPassengerPosition(int1, "outside2");
						if (position != null) {
							this.getPassengerPositionWorldPos(position, vector3f);
							float1 = vector3f.x;
							float2 = vector3f.y;
							this.getPassengerPositionWorldPos(0.0F, position.offset.y, position.offset.z, vector3f2);
							vector2.set(vector3f2.x - gameCharacter.getX(), vector3f2.y - gameCharacter.getY());
							vector2.normalize();
							float3 = vector2.dot(gameCharacter.getForwardDirection());
							if (float3 > 0.5F && IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), float1, float2) < 1.0F) {
								((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
								((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
								((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
								return int1;
							}
						}
					}
				}

				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
				((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
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
		this.hasExtendOffset = false;
		this.hasExtendOffsetExiting = false;
	}

	public VehiclePart getUseablePart(IsoGameCharacter gameCharacter) {
		return this.getUseablePart(gameCharacter, true);
	}

	public VehiclePart getUseablePart(IsoGameCharacter gameCharacter, boolean boolean1) {
		if ((int)this.getZ() != (int)gameCharacter.getZ()) {
			return null;
		} else if (gameCharacter.DistTo(this) > 6.0F) {
			return null;
		} else {
			VehicleScript vehicleScript = this.getScript();
			if (vehicleScript == null) {
				return null;
			} else {
				Vector3f vector3f = vehicleScript.getExtents();
				Vector3f vector3f2 = vehicleScript.getCenterOfMassOffset();
				float float1 = vector3f2.z - vector3f.z / 2.0F;
				float float2 = vector3f2.z + vector3f.z / 2.0F;
				Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
				Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				for (int int1 = 0; int1 < this.parts.size(); ++int1) {
					VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
					if (vehiclePart.getArea() != null && this.isInArea(vehiclePart.getArea(), gameCharacter)) {
						String string = vehiclePart.getLuaFunction("use");
						if (string != null && !string.equals("")) {
							VehicleScript.Area area = vehicleScript.getAreaById(vehiclePart.getArea());
							if (area != null) {
								Vector2 vector22 = this.areaPositionLocal(area, vector2);
								if (vector22 != null) {
									float float3 = 0.0F;
									float float4 = 0.0F;
									float float5 = 0.0F;
									if (!(vector22.y >= float2) && !(vector22.y <= float1)) {
										float5 = vector22.y;
									} else {
										float3 = vector22.x;
									}

									if (!boolean1) {
										return vehiclePart;
									}

									this.getWorldPos(float3, float4, float5, vector3f3);
									vector2.set(vector3f3.x - gameCharacter.getX(), vector3f3.y - gameCharacter.getY());
									vector2.normalize();
									float float6 = vector2.dot(gameCharacter.getForwardDirection());
									if (float6 > 0.5F && !PolygonalMap2.instance.lineClearCollide(gameCharacter.x, gameCharacter.y, vector3f3.x, vector3f3.y, (int)gameCharacter.z, this, false, true)) {
										((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
										((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
										return vehiclePart;
									}

									break;
								}
							}
						}
					}
				}

				((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
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
			Vector3f vector3f = this.script.getExtents();
			Vector3f vector3f2 = this.script.getCenterOfMassOffset();
			float float1 = vector3f2.z - vector3f.z / 2.0F;
			float float2 = vector3f2.z + vector3f.z / 2.0F;
			Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
			Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getWindow() != null && vehiclePart.getArea() != null && this.isInArea(vehiclePart.getArea(), gameCharacter)) {
					VehicleScript.Area area = this.script.getAreaById(vehiclePart.getArea());
					if (!(area.y >= float2) && !(area.y <= float1)) {
						vector3f3.set(0.0F, 0.0F, area.y);
					} else {
						vector3f3.set(area.x, 0.0F, 0.0F);
					}

					this.getWorldPos(vector3f3, vector3f3);
					vector2.set(vector3f3.x - gameCharacter.getX(), vector3f3.y - gameCharacter.getY());
					vector2.normalize();
					float float3 = vector2.dot(gameCharacter.getForwardDirection());
					if (float3 > 0.5F) {
						((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
						((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
						return vehiclePart;
					}

					break;
				}
			}

			((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
			return null;
		}
	}

	public void getFacingPosition(IsoGameCharacter gameCharacter, Vector2 vector2) {
		Vector3f vector3f = this.getLocalPos(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ(), (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		Vector3f vector3f2 = this.script.getExtents();
		Vector3f vector3f3 = this.script.getCenterOfMassOffset();
		float float1 = vector3f3.x - vector3f2.x / 2.0F;
		float float2 = vector3f3.x + vector3f2.x / 2.0F;
		float float3 = vector3f3.z - vector3f2.z / 2.0F;
		float float4 = vector3f3.z + vector3f2.z / 2.0F;
		float float5 = 0.0F;
		float float6 = 0.0F;
		if (vector3f.x <= 0.0F && vector3f.z >= float3 && vector3f.z <= float4) {
			float6 = vector3f.z;
		} else if (vector3f.x > 0.0F && vector3f.z >= float3 && vector3f.z <= float4) {
			float6 = vector3f.z;
		} else if (vector3f.z <= 0.0F && vector3f.x >= float1 && vector3f.x <= float2) {
			float5 = vector3f.x;
		} else if (vector3f.z > 0.0F && vector3f.x >= float1 && vector3f.x <= float2) {
			float5 = vector3f.x;
		}

		this.getWorldPos(float5, 0.0F, float6, vector3f);
		vector2.set(vector3f.x, vector3f.y);
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
	}

	public boolean enter(int int1, IsoGameCharacter gameCharacter, Vector3f vector3f) {
		if (!GameClient.bClient) {
			VehiclesDB2.instance.updateVehicleAndTrailer(this);
		}

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
		if (!GameClient.bClient) {
			VehiclesDB2.instance.updateVehicleAndTrailer(this);
		}

		if (gameCharacter == null) {
			return false;
		} else {
			int int1 = this.getSeat(gameCharacter);
			if (int1 == -1) {
				return false;
			} else if (this.clearPassenger(int1)) {
				this.enginePower = (int)this.getScript().getEngineForce();
				gameCharacter.setVehicle((BaseVehicle)null);
				gameCharacter.setCollidable(true);
				if (GameClient.bClient) {
					VehicleManager.instance.sendExit(this, gameCharacter);
				}

				if (this.getDriver() == null && this.soundHornOn) {
					this.onHornStop();
				}

				this.polyGarageCheck = true;
				this.polyDirty = true;
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

	public boolean showPassenger(int int1) {
		VehicleScript.Passenger passenger = this.getScriptPassenger(int1);
		return passenger == null ? false : passenger.showPassenger;
	}

	public boolean showPassenger(IsoGameCharacter gameCharacter) {
		int int1 = this.getSeat(gameCharacter);
		return this.showPassenger(int1);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		if (this.square != null) {
			float float1 = 5.0E-4F;
			this.x = PZMath.clamp(this.x, (float)this.square.x + float1, (float)(this.square.x + 1) - float1);
			this.y = PZMath.clamp(this.y, (float)this.square.y + float1, (float)(this.square.y + 1) - float1);
		}

		super.save(byteBuffer, boolean1);
		Quaternionf quaternionf = this.savedRot;
		Transform transform = this.getWorldTransform(this.tempTransform);
		byteBuffer.putFloat(transform.origin.y);
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
			this.getCurrentKey().saveWithSize(byteBuffer, false);
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.put((byte)this.bloodIntensity.size());
		Iterator iterator = this.bloodIntensity.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			GameWindow.WriteStringUTF(byteBuffer, (String)entry.getKey());
			byteBuffer.put((Byte)entry.getValue());
		}

		if (this.vehicleTowingID != -1) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.vehicleTowingID);
			GameWindow.WriteStringUTF(byteBuffer, this.towAttachmentSelf);
			GameWindow.WriteStringUTF(byteBuffer, this.towAttachmentOther);
			byteBuffer.putFloat(this.towConstraintZOffset);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (this.z < 0.0F) {
			this.z = 0.0F;
		}

		if (int1 >= 173) {
			this.savedPhysicsZ = PZMath.clamp(byteBuffer.getFloat(), 0.0F, (float)((int)this.z) + 2.4477F);
		}

		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		this.savedRot.set(float1, float2, float3, float4);
		this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, Float.isNaN(this.savedPhysicsZ) ? this.z : this.savedPhysicsZ, this.getY() - WorldSimulation.instance.offsetY);
		this.jniTransform.setRotation(this.savedRot);
		this.scriptName = GameWindow.ReadStringUTF(byteBuffer);
		this.skinIndex = byteBuffer.getInt();
		boolean boolean2 = byteBuffer.get() == 1;
		if (boolean2) {
			this.engineState = BaseVehicle.engineStateTypes.Running;
		}

		this.frontEndDurability = byteBuffer.getInt();
		this.rearEndDurability = byteBuffer.getInt();
		this.currentFrontEndDurability = byteBuffer.getInt();
		this.currentRearEndDurability = byteBuffer.getInt();
		this.engineLoudness = byteBuffer.getInt();
		this.engineQuality = byteBuffer.getInt();
		this.engineQuality = PZMath.clamp(this.engineQuality, 0, 100);
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

		if (int1 >= 122) {
			String string = GameWindow.ReadString(byteBuffer);
			this.mechanicalID = byteBuffer.getInt();
		}

		if (int1 >= 124) {
			this.alarmed = byteBuffer.get() == 1;
		}

		if (int1 >= 129) {
			this.sirenStartTime = byteBuffer.getDouble();
		}

		if (int1 >= 133 && byteBuffer.get() == 1) {
			InventoryItem inventoryItem = null;
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, int1);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (inventoryItem != null) {
				this.setCurrentKey(inventoryItem);
			}
		}

		if (int1 >= 165) {
			byte byte1 = byteBuffer.get();
			for (int int3 = 0; int3 < byte1; ++int3) {
				String string2 = GameWindow.ReadStringUTF(byteBuffer);
				byte byte2 = byteBuffer.get();
				this.bloodIntensity.put(string2, byte2);
			}
		}

		if (int1 >= 174) {
			if (byteBuffer.get() == 1) {
				this.vehicleTowingID = byteBuffer.getInt();
				this.towAttachmentSelf = GameWindow.ReadStringUTF(byteBuffer);
				this.towAttachmentOther = GameWindow.ReadStringUTF(byteBuffer);
				this.towConstraintZOffset = byteBuffer.getFloat();
			}
		} else if (int1 >= 172) {
			this.vehicleTowingID = byteBuffer.getInt();
		}

		this.loaded = true;
	}

	public void softReset() {
		this.keySpawned = 0;
		this.keyIsOnDoor = false;
		this.keysInIgnition = false;
		this.currentKey = null;
		this.engineState = BaseVehicle.engineStateTypes.Idle;
		this.randomizeContainers();
	}

	public void trySpawnKey() {
		if (!GameClient.bClient) {
			if (this.script != null && this.script.getPartById("Engine") != null) {
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

	public boolean shouldCollideWithCharacters() {
		if (this.vehicleTowedBy != null) {
			return this.vehicleTowedBy.shouldCollideWithCharacters();
		} else {
			float float1 = this.getSpeed2D();
			return this.isEngineRunning() ? float1 > 0.05F : float1 > 1.0F;
		}
	}

	public boolean shouldCollideWithObjects() {
		return this.vehicleTowedBy != null ? this.vehicleTowedBy.shouldCollideWithObjects() : this.isEngineRunning();
	}

	public void brekingObjects() {
		boolean boolean1 = this.shouldCollideWithCharacters();
		boolean boolean2 = this.shouldCollideWithObjects();
		if (boolean1 || boolean2) {
			Vector3f vector3f = this.script.getExtents();
			Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
			float float1 = Math.max(vector3f.x / 2.0F, vector3f.z / 2.0F) + 0.3F + 1.0F;
			int int1 = (int)Math.ceil((double)float1);
			int int2;
			for (int int3 = -int1; int3 < int1; ++int3) {
				for (int2 = -int1; int2 < int1; ++int2) {
					IsoGridSquare square = this.getCell().getGridSquare((double)(this.x + (float)int2), (double)(this.y + (float)int3), (double)this.z);
					if (square != null) {
						int int4;
						if (boolean2) {
							for (int4 = 0; int4 < square.getObjects().size(); ++int4) {
								IsoObject object = (IsoObject)square.getObjects().get(int4);
								if (!(object instanceof IsoWorldInventoryObject)) {
									Vector2 vector22 = null;
									if (!this.brekingObjectsList.contains(object) && object != null && object.getProperties() != null) {
										if (object.getProperties().Is("CarSlowFactor")) {
											vector22 = this.testCollisionWithObject(object, 0.3F, vector2);
										}

										if (vector22 != null) {
											this.brekingObjectsList.add(object);
											if (!GameClient.bClient) {
												object.Collision(vector22, this);
											}
										}

										if (object.getProperties().Is("HitByCar")) {
											vector22 = this.testCollisionWithObject(object, 0.3F, vector2);
										}

										if (vector22 != null && !GameClient.bClient) {
											object.Collision(vector22, this);
										}

										this.checkCollisionWithPlant(square, object, vector2);
									}
								}
							}
						}

						IsoMovingObject movingObject;
						if (boolean1) {
							for (int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
								movingObject = (IsoMovingObject)square.getMovingObjects().get(int4);
								IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
								if (zombie != null) {
									if (zombie.isProne()) {
										this.testCollisionWithProneCharacter(zombie, false);
									}

									zombie.setVehicle4TestCollision(this);
								}

								if (movingObject instanceof IsoPlayer && movingObject != this.getDriver()) {
									IsoPlayer player = (IsoPlayer)movingObject;
									player.setVehicle4TestCollision(this);
								}
							}
						}

						if (boolean2) {
							for (int4 = 0; int4 < square.getStaticMovingObjects().size(); ++int4) {
								movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int4);
								IsoDeadBody deadBody = (IsoDeadBody)Type.tryCastTo(movingObject, IsoDeadBody.class);
								if (deadBody != null) {
									this.testCollisionWithCorpse(deadBody, true);
								}
							}
						}
					}
				}
			}

			float float2 = -999.0F;
			for (int2 = 0; int2 < this.brekingObjectsList.size(); ++int2) {
				IsoObject object2 = (IsoObject)this.brekingObjectsList.get(int2);
				Vector2 vector23 = this.testCollisionWithObject(object2, 1.0F, vector2);
				if (vector23 != null && object2.getSquare().getObjects().contains(object2)) {
					if (float2 < object2.GetVehicleSlowFactor(this)) {
						float2 = object2.GetVehicleSlowFactor(this);
					}
				} else {
					this.brekingObjectsList.remove(object2);
					object2.UnCollision(this);
				}
			}

			if (float2 != -999.0F) {
				this.brekingSlowFactor = PZMath.clamp(float2, 0.0F, 34.0F);
			} else {
				this.brekingSlowFactor = 0.0F;
			}

			((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
		}
	}

	private void updateVelocityMultiplier() {
		if (this.physics != null && this.getScript() != null) {
			Vector3f vector3f = this.getLinearVelocity((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			vector3f.y = 0.0F;
			float float1 = vector3f.length();
			float float2 = 100000.0F;
			float float3 = 1.0F;
			if (this.getScript().getWheelCount() > 0) {
				if (float1 > 0.0F && float1 > 34.0F - this.brekingSlowFactor) {
					float2 = 34.0F - this.brekingSlowFactor;
					float3 = (34.0F - this.brekingSlowFactor) / float1;
				}
			} else if (this.getVehicleTowedBy() == null) {
				float2 = 0.0F;
				float3 = 0.1F;
			}

			Bullet.setVehicleVelocityMultiplier(this.VehicleID, float2, float3);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		}
	}

	private void playScrapePastPlantSound(IsoGridSquare square) {
		if (this.emitter != null && !this.emitter.isPlaying(this.soundScrapePastPlant)) {
			this.emitter.setPos((float)square.x + 0.5F, (float)square.y + 0.5F, (float)square.z);
			this.soundScrapePastPlant = this.emitter.playSoundImpl("VehicleScrapePastPlant", square);
		}
	}

	private void checkCollisionWithPlant(IsoGridSquare square, IsoObject object, Vector2 vector2) {
		IsoTree tree = (IsoTree)Type.tryCastTo(object, IsoTree.class);
		if (tree != null || object.getProperties().Is("Bush")) {
			float float1 = Math.abs(this.getCurrentSpeedKmHour());
			if (!(float1 <= 1.0F)) {
				Vector2 vector22 = this.testCollisionWithObject(object, 0.3F, vector2);
				if (vector22 != null) {
					if (tree != null && tree.getSize() == 1) {
						this.ApplyImpulse4Break(object, 0.025F);
						this.playScrapePastPlantSound(square);
					} else if (this.isPositionOnLeftOrRight(vector22.x, vector22.y)) {
						this.ApplyImpulse4Break(object, 0.025F);
						this.playScrapePastPlantSound(square);
					} else if (float1 < 10.0F) {
						this.ApplyImpulse4Break(object, 0.025F);
						this.playScrapePastPlantSound(square);
					} else {
						this.ApplyImpulse4Break(object, 0.1F);
						this.playScrapePastPlantSound(square);
					}
				}
			}
		}
	}

	public void damageObjects(float float1) {
		if (this.isEngineRunning()) {
			Vector3f vector3f = this.script.getExtents();
			Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
			float float2 = Math.max(vector3f.x / 2.0F, vector3f.z / 2.0F) + 0.3F + 1.0F;
			int int1 = (int)Math.ceil((double)float2);
			for (int int2 = -int1; int2 < int1; ++int2) {
				for (int int3 = -int1; int3 < int1; ++int3) {
					IsoGridSquare square = this.getCell().getGridSquare((double)(this.x + (float)int3), (double)(this.y + (float)int2), (double)this.z);
					if (square != null) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							Vector2 vector22 = null;
							if (object instanceof IsoTree) {
								vector22 = this.testCollisionWithObject(object, 2.0F, vector2);
								if (vector22 != null) {
									object.setRenderEffect(RenderEffectType.Hit_Tree_Shudder);
								}
							}

							if (vector22 == null && object instanceof IsoWindow) {
								vector22 = this.testCollisionWithObject(object, 1.0F, vector2);
							}

							if (vector22 == null && object.sprite != null && (object.sprite.getProperties().Is("HitByCar") || object.sprite.getProperties().Is("CarSlowFactor"))) {
								vector22 = this.testCollisionWithObject(object, 1.0F, vector2);
							}

							IsoGridSquare square2;
							if (vector22 == null) {
								square2 = this.getCell().getGridSquare((double)(this.x + (float)int3), (double)(this.y + (float)int2), 1.0);
								if (square2 != null && square2.getHasTypes().isSet(IsoObjectType.lightswitch)) {
									vector22 = this.testCollisionWithObject(object, 1.0F, vector2);
								}
							}

							if (vector22 == null) {
								square2 = this.getCell().getGridSquare((double)(this.x + (float)int3), (double)(this.y + (float)int2), 0.0);
								if (square2 != null && square2.getHasTypes().isSet(IsoObjectType.lightswitch)) {
									vector22 = this.testCollisionWithObject(object, 1.0F, vector2);
								}
							}

							if (vector22 != null) {
								object.Hit(vector22, this, float1);
							}
						}
					}
				}
			}

			((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
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

			float float1;
			if (this.getVehicleTowing() != null && this.getDriver() != null) {
				float1 = 2.5F;
				if (this.getVehicleTowing().getPartCount() == 0) {
					float1 = 12.0F;
				}
			}

			if (this.getVehicleTowedBy() != null && this.getDriver() != null) {
				float1 = 2.5F;
				if (this.getVehicleTowedBy().getPartCount() == 0) {
					float1 = 12.0F;
				}
			}

			if (this.physics != null && this.vehicleTowingID != -1 && this.vehicleTowing == null) {
				this.tryReconnectToTowedVehicle();
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			if (this.getVehicleTowedBy() != null && this.getVehicleTowedBy().getController() != null) {
				boolean1 = this.getVehicleTowedBy() != null && this.getVehicleTowedBy().getController().isEnable;
				boolean2 = this.getVehicleTowing() != null && this.getVehicleTowing().getDriver() != null;
			}

			if (this.physics != null) {
				boolean boolean3 = this.getDriver() != null || boolean1 || boolean2;
				long long1 = System.currentTimeMillis();
				if (this.constraintChangedTime != -1L) {
					if (this.constraintChangedTime + 3500L < long1) {
						this.constraintChangedTime = -1L;
						if (!boolean3 && this.physicActiveCheck < long1) {
							this.setPhysicsActive(false);
						}
					}
				} else {
					if (this.physicActiveCheck != -1L && (boolean3 || !this.physics.isEnable)) {
						this.physicActiveCheck = -1L;
					}

					if (!boolean3 && this.physics.isEnable && this.physicActiveCheck != -1L && this.physicActiveCheck < long1) {
						this.physicActiveCheck = -1L;
						this.setPhysicsActive(false);
					}
				}

				if (this.getVehicleTowedBy() != null && this.getScript().getWheelCount() > 0) {
					this.physics.updateTrailer();
				} else if (this.getDriver() == null) {
					this.physics.checkShouldBeActive();
				}

				this.doAlarm();
				BaseVehicle.VehicleImpulse vehicleImpulse = this.impulseFromServer;
				Vector3f vector3f;
				if (vehicleImpulse != null && vehicleImpulse.enable) {
					vehicleImpulse.enable = false;
					float float2 = 1.0F;
					Bullet.applyCentralForceToVehicle(this.VehicleID, vehicleImpulse.impulse.x * float2, vehicleImpulse.impulse.y * float2, vehicleImpulse.impulse.z * float2);
					vector3f = vehicleImpulse.rel_pos.cross(vehicleImpulse.impulse, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
					Bullet.applyTorqueToVehicle(this.VehicleID, vector3f.x * float2, vector3f.y * float2, vector3f.z * float2);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				}

				this.applyImpulseFromHitZombies();
				this.applyImpulseFromProneCharacters();
				if (System.currentTimeMillis() - this.engineCheckTime > 1000L && !GameClient.bClient) {
					this.engineCheckTime = System.currentTimeMillis();
					if (!GameClient.bClient) {
						if (this.engineState != BaseVehicle.engineStateTypes.Idle) {
							int int1 = (int)((double)this.engineLoudness * this.engineSpeed / 2500.0);
							double double1 = Math.min(this.getEngineSpeed(), 2000.0);
							int1 = (int)((double)int1 * (1.0 + double1 / 4000.0));
							if (Rand.Next((int)(120.0F * GameTime.instance.getInvMultiplier())) == 0) {
								WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, int1), int1 / 40, false);
							}

							if (Rand.Next((int)(35.0F * GameTime.instance.getInvMultiplier())) == 0) {
								WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, int1 / 2), int1 / 40, false);
							}

							if (Rand.Next((int)(2.0F * GameTime.instance.getInvMultiplier())) == 0) {
								WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, int1 / 4), int1 / 40, false);
							}

							WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), Math.max(8, int1 / 6), int1 / 40, false);
						}

						if (this.lightbarSirenMode.isEnable() && this.getBatteryCharge() > 0.0F) {
							WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 600, 60, false);
						}
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Running && !this.isEngineWorking()) {
						this.shutOff();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Running && this.getPartById("Engine").getCondition() < 50 && Rand.Next(this.getPartById("Engine").getCondition() * 12) == 0) {
						this.shutOff();
					}

					if (this.engineState == BaseVehicle.engineStateTypes.Starting) {
						this.updateEngineStarting();
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

				if (this.getDriver() == null && !boolean1) {
					this.getController().park();
				}

				this.setX(this.jniTransform.origin.x + WorldSimulation.instance.offsetX);
				this.setY(this.jniTransform.origin.z + WorldSimulation.instance.offsetY);
				this.setZ(0.0F);
				IsoGridSquare square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
				if (square == null) {
					float float3 = 5.0E-4F;
					int int2 = this.chunk.wx * 10;
					int int3 = this.chunk.wy * 10;
					int int4 = int2 + 10;
					int int5 = int3 + 10;
					float float4 = this.x;
					float float5 = this.y;
					this.x = Math.max(this.x, (float)int2 + float3);
					this.x = Math.min(this.x, (float)int4 - float3);
					this.y = Math.max(this.y, (float)int3 + float3);
					this.y = Math.min(this.y, (float)int5 - float3);
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
					boolean boolean4 = false;
				}

				this.updateTransform();
				if (this.jniIsCollide) {
					this.jniIsCollide = false;
					vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
					if (GameServer.bServer) {
						vector3f.set((Vector3fc)this.netLinearVelocity);
					} else {
						vector3f.set((Vector3fc)this.jniLinearVelocity);
					}

					vector3f.negate();
					vector3f.add(this.lastLinearVelocity);
					vector3f.y = 0.0F;
					float float6 = Math.abs(vector3f.length());
					if (float6 > 2.0F) {
						if (this.lastLinearVelocity.length() < 6.0F) {
							float6 /= 3.0F;
						}

						this.jniTransform.getRotation(this.tempQuat4f);
						this.tempQuat4f.invert(this.tempQuat4f);
						if (this.lastLinearVelocity.rotate(this.tempQuat4f).z < 0.0F) {
							float6 *= -1.0F;
						}

						if (Core.bDebug) {
							DebugLog.log("CRASH lastSpeed=" + this.lastLinearVelocity.length() + " speed=" + vector3f + " delta=" + float6 + " netLinearVelocity=" + this.netLinearVelocity.length());
						}

						Vector3f vector3f2 = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
						float float7 = vector3f.normalize().dot(vector3f2);
						((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
						this.crash(Math.abs(float6 * 3.0F), float7 > 0.0F);
						this.damageObjects(Math.abs(float6) * 30.0F);
					}

					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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

			int int6;
			for (int6 = 0; int6 < this.impulseFromSquishedZombie.length; ++int6) {
				BaseVehicle.VehicleImpulse vehicleImpulse2 = this.impulseFromSquishedZombie[int6];
				if (vehicleImpulse2 != null) {
					vehicleImpulse2.enable = false;
				}
			}

			this.updateSounds();
			this.brekingObjects();
			if (this.bAddThumpWorldSound) {
				this.bAddThumpWorldSound = false;
				WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, (int)this.z, 20, 20, true);
			}

			if (this.script.getLightbar().enable && this.lightbarLightsMode.isEnable() && this.getBatteryCharge() > 0.0F) {
				this.lightbarLightsMode.update();
			}

			this.updateWorldLights();
			for (int6 = 0; int6 < IsoPlayer.numPlayers; ++int6) {
				if (this.current == null || !this.current.lighting[int6].bCanSee()) {
					this.setTargetAlpha(int6, 0.0F);
				}

				IsoPlayer player = IsoPlayer.players[int6];
				if (player != null && this.DistToSquared(player) < 225.0F) {
					this.setTargetAlpha(int6, 1.0F);
				}
			}

			for (int6 = 0; int6 < this.getScript().getPassengerCount(); ++int6) {
				if (this.getCharacter(int6) != null) {
					Vector3f vector3f3 = this.getPassengerWorldPos(int6, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
					this.getCharacter(int6).setX(vector3f3.x);
					this.getCharacter(int6).setY(vector3f3.y);
					this.getCharacter(int6).setZ(vector3f3.z * 1.0F);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
				}
			}

			if (!this.needPartsUpdate() && !this.isMechanicUIOpen()) {
				this.drainBatteryUpdateHack();
			} else {
				this.updateParts();
			}

			if (this.engineState == BaseVehicle.engineStateTypes.Running || boolean1) {
				this.updateBulletStats();
			}

			if (this.bDoDamageOverlay) {
				this.bDoDamageOverlay = false;
				this.doDamageOverlay();
			}

			if (GameClient.bClient) {
				this.checkPhysicsValidWithServer();
			}

			VehiclePart vehiclePart = this.getPartById("GasTank");
			if (vehiclePart != null && vehiclePart.getContainerContentAmount() > (float)vehiclePart.getContainerCapacity()) {
				vehiclePart.setContainerContentAmount((float)vehiclePart.getContainerCapacity());
			}

			boolean boolean5 = false;
			for (int int7 = 0; int7 < this.getMaxPassengers(); ++int7) {
				BaseVehicle.Passenger passenger = this.getPassenger(int7);
				if (passenger.character != null) {
					boolean5 = true;
					break;
				}
			}

			if (boolean5) {
				this.m_surroundVehicle.update();
			}

			if (this.physics != null) {
				Bullet.setVehicleMass(this.VehicleID, this.getFudgedMass());
			}

			this.updateVelocityMultiplier();
		}
	}

	private void updateEngineStarting() {
		if (this.getBatteryCharge() <= 0.1F) {
			this.engineDoStartingFailedNoPower();
		} else {
			VehiclePart vehiclePart = this.getPartById("GasTank");
			if (vehiclePart != null && vehiclePart.getContainerContentAmount() <= 0.0F) {
				this.engineDoStartingFailed();
			} else {
				int int1 = 0;
				float float1 = ClimateManager.getInstance().getAirTemperatureForSquare(this.getSquare());
				if (this.engineQuality < 65 && float1 <= 2.0F) {
					int1 = Math.min((2 - (int)float1) * 2, 30);
				}

				if (!SandboxOptions.instance.VehicleEasyUse.getValue() && this.engineQuality < 100 && Rand.Next(this.engineQuality + 50 - int1) <= 30) {
					this.engineDoStartingFailed();
				} else {
					if (Rand.Next(this.engineQuality) != 0) {
						this.engineDoStartingSuccess();
					} else {
						this.engineDoRetryingStarting();
					}
				}
			}
		}
	}

	private void applyImpulseFromHitZombies() {
		if (!this.impulseFromHitZombie.isEmpty()) {
			Vector3f vector3f = ((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc()).set(0.0F, 0.0F, 0.0F);
			Vector3f vector3f2 = ((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc()).set(0.0F, 0.0F, 0.0F);
			Vector3f vector3f3 = ((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc()).set(0.0F, 0.0F, 0.0F);
			int int1 = this.impulseFromHitZombie.size();
			for (int int2 = 0; int2 < int1; ++int2) {
				BaseVehicle.VehicleImpulse vehicleImpulse = (BaseVehicle.VehicleImpulse)this.impulseFromHitZombie.get(int2);
				vector3f.add(vehicleImpulse.impulse);
				vector3f2.add(vehicleImpulse.rel_pos.cross(vehicleImpulse.impulse, vector3f3));
				vehicleImpulse.release();
				vehicleImpulse.enable = false;
			}

			this.impulseFromHitZombie.clear();
			float float1 = 7.0F * this.getFudgedMass();
			if (vector3f.lengthSquared() > float1 * float1) {
				vector3f.mul(float1 / vector3f.length());
			}

			float float2 = 30.0F;
			Bullet.applyCentralForceToVehicle(this.VehicleID, vector3f.x * float2, vector3f.y * float2, vector3f.z * float2);
			Bullet.applyTorqueToVehicle(this.VehicleID, vector3f2.x * float2, vector3f2.y * float2, vector3f2.z * float2);
			if (GameServer.bServer) {
			}

			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
		}
	}

	private void applyImpulseFromProneCharacters() {
		boolean boolean1 = PZArrayUtil.contains((Object[])this.impulseFromSquishedZombie, (var0)->{
    return var0 != null && var0.enable;
});
		if (boolean1) {
			Vector3f vector3f = ((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc()).set(0.0F, 0.0F, 0.0F);
			Vector3f vector3f2 = ((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc()).set(0.0F, 0.0F, 0.0F);
			Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			for (int int1 = 0; int1 < this.impulseFromSquishedZombie.length; ++int1) {
				BaseVehicle.VehicleImpulse vehicleImpulse = this.impulseFromSquishedZombie[int1];
				if (vehicleImpulse != null && vehicleImpulse.enable) {
					vector3f.add(vehicleImpulse.impulse);
					vector3f2.add(vehicleImpulse.rel_pos.cross(vehicleImpulse.impulse, vector3f3));
					vehicleImpulse.enable = false;
					vehicleImpulse.release();
				}
			}

			if (vector3f.lengthSquared() > 0.0F) {
				float float1 = this.getFudgedMass() * 0.15F;
				if (vector3f.lengthSquared() > float1 * float1) {
					vector3f.mul(float1 / vector3f.length());
				}

				float float2 = 30.0F;
				Bullet.applyCentralForceToVehicle(this.VehicleID, vector3f.x * float2, vector3f.y * float2, vector3f.z * float2);
				Bullet.applyTorqueToVehicle(this.VehicleID, vector3f2.x * float2, vector3f2.y * float2, vector3f2.z * float2);
			}

			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
		}
	}

	public float getFudgedMass() {
		if (this.getScriptName().contains("Trailer")) {
			return this.getMass();
		} else {
			BaseVehicle baseVehicle = this.getVehicleTowedBy();
			if (baseVehicle != null && baseVehicle.getDriver() != null && baseVehicle.isEngineRunning()) {
				float float1 = Math.max(250.0F, baseVehicle.getMass() / 3.7F);
				if (this.getScript().getWheelCount() == 0) {
					float1 = Math.min(float1, 200.0F);
				}

				return float1;
			} else {
				return this.getMass();
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
		Vector3f vector3f = this.getLinearVelocity((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		float float1 = Math.abs(vector3f.x);
		float float2 = Math.abs(vector3f.z);
		boolean boolean1 = vector3f.x < 0.0F && float1 > float2;
		boolean boolean2 = vector3f.x > 0.0F && float1 > float2;
		boolean boolean3 = vector3f.z < 0.0F && float2 > float1;
		boolean boolean4 = vector3f.z > 0.0F && float2 > float1;
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		return this.isInvalidChunkAround(boolean1, boolean2, boolean3, boolean4);
	}

	public boolean isInvalidChunkAhead() {
		Vector3f vector3f = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		boolean boolean1 = vector3f.x < -0.5F;
		boolean boolean2 = vector3f.z > 0.5F;
		boolean boolean3 = vector3f.x > 0.5F;
		boolean boolean4 = vector3f.z < -0.5F;
		return this.isInvalidChunkAround(boolean1, boolean3, boolean4, boolean2);
	}

	public boolean isInvalidChunkBehind() {
		Vector3f vector3f = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		boolean boolean1 = vector3f.x < -0.5F;
		boolean boolean2 = vector3f.z > 0.5F;
		boolean boolean3 = vector3f.x > 0.5F;
		boolean boolean4 = vector3f.z < -0.5F;
		return this.isInvalidChunkAround(boolean3, boolean1, boolean2, boolean4);
	}

	public boolean isInvalidChunkAround(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
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
			if (!boolean1 || !this.isNullChunk(this.chunk.wx - 1, this.chunk.wy) && !this.isNullChunk(this.chunk.wx - 2, this.chunk.wy)) {
				if (boolean4 && (this.isNullChunk(this.chunk.wx, this.chunk.wy + 1) || this.isNullChunk(this.chunk.wx, this.chunk.wy + 2))) {
					return true;
				} else if (!boolean3 || !this.isNullChunk(this.chunk.wx, this.chunk.wy - 1) && !this.isNullChunk(this.chunk.wx, this.chunk.wy - 2)) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public void postupdate() {
		this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, 0);
		int int1;
		if (this.current == null) {
			for (int1 = (int)this.z; int1 >= 0; --int1) {
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
		if (this.sprite.hasActiveModel()) {
			this.updateAnimationPlayer(this.getAnimationPlayer(), (VehiclePart)null);
			for (int1 = 0; int1 < this.models.size(); ++int1) {
				BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int1);
				this.updateAnimationPlayer(modelInfo.getAnimationPlayer(), modelInfo.part);
			}
		}
	}

	protected void updateAnimationPlayer(AnimationPlayer animationPlayer, VehiclePart vehiclePart) {
		if (animationPlayer != null && animationPlayer.isReady()) {
			AnimationMultiTrack animationMultiTrack = animationPlayer.getMultiTrack();
			float float1 = 0.016666668F;
			float1 *= 0.8F;
			float1 *= GameTime.instance.getUnmoddedMultiplier();
			animationPlayer.Update(float1);
			for (int int1 = 0; int1 < animationMultiTrack.getTrackCount(); ++int1) {
				AnimationTrack animationTrack = (AnimationTrack)animationMultiTrack.getTracks().get(int1);
				if (animationTrack.IsPlaying && animationTrack.isFinished()) {
					animationMultiTrack.removeTrackAt(int1);
					--int1;
				}
			}

			if (vehiclePart != null) {
				BaseVehicle.ModelInfo modelInfo = this.getModelInfoForPart(vehiclePart);
				if (modelInfo.m_track != null && animationMultiTrack.getIndexOfTrack(modelInfo.m_track) == -1) {
					modelInfo.m_track = null;
				}

				if (modelInfo.m_track != null) {
					VehicleWindow vehicleWindow = vehiclePart.getWindow();
					if (vehicleWindow != null) {
						AnimationTrack animationTrack2 = modelInfo.m_track;
						animationTrack2.setCurrentTimeValue(animationTrack2.getDuration() * vehicleWindow.getOpenDelta());
					}
				} else {
					VehicleDoor vehicleDoor = vehiclePart.getDoor();
					if (vehicleDoor != null) {
						this.playPartAnim(vehiclePart, vehicleDoor.isOpen() ? "Opened" : "Closed");
					}

					VehicleWindow vehicleWindow2 = vehiclePart.getWindow();
					if (vehicleWindow2 != null) {
						this.playPartAnim(vehiclePart, "ClosedToOpen");
					}
				}
			}
		}
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
			if (this.getVehicleTowedBy() != null) {
				if (this.getVehicleTowedBy().getDriver() != null) {
					this.netPlayerAuthorization = 3;
					this.netPlayerId = this.getVehicleTowedBy().getDriver().getOnlineID();
					this.netPlayerTimeout = 30;
				} else {
					this.netPlayerAuthorization = 0;
					this.netPlayerId = -1;
				}
			} else {
				this.netPlayerAuthorization = 3;
				this.netPlayerId = ((IsoPlayer)this.getDriver()).OnlineID;
				this.netPlayerTimeout = 30;
			}
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
		Quaternionf quaternionf = this.tempQuat4f;
		quaternionf.setAngleAxis(0.0F, _UNIT_Y.x, _UNIT_Y.y, _UNIT_Y.z);
		transform.setRotation(quaternionf);
		this.setWorldTransform(transform);
	}

	public void setAngles(float float1, float float2, float float3) {
		if ((int)float1 != (int)this.getAngleX() || (int)float2 != (int)this.getAngleY() || float3 != (float)((int)this.getAngleZ())) {
			this.polyDirty = true;
			float float4 = float1 * 0.017453292F;
			float float5 = float2 * 0.017453292F;
			float float6 = float3 * 0.017453292F;
			this.tempQuat4f.rotationXYZ(float4, float5, float6);
			this.tempTransform.set(this.jniTransform);
			this.tempTransform.setRotation(this.tempQuat4f);
			this.setWorldTransform(this.tempTransform);
		}
	}

	public float getAngleX() {
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		this.jniTransform.getRotation(this.tempQuat4f).getEulerAnglesXYZ(vector3f);
		float float1 = vector3f.x * 57.295776F;
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		return float1;
	}

	public float getAngleY() {
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		this.jniTransform.getRotation(this.tempQuat4f).getEulerAnglesXYZ(vector3f);
		float float1 = vector3f.y * 57.295776F;
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		return float1;
	}

	public float getAngleZ() {
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		this.jniTransform.getRotation(this.tempQuat4f).getEulerAnglesXYZ(vector3f);
		float float1 = vector3f.z * 57.295776F;
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		return float1;
	}

	public void setDebugZ(float float1) {
		this.tempTransform.set(this.jniTransform);
		this.tempTransform.origin.y = PZMath.clamp(float1, 0.0F, 1.0F) * 3.0F * 0.82F;
		this.setWorldTransform(this.tempTransform);
	}

	public void setPhysicsActive(boolean boolean1) {
		if (this.physics != null && boolean1 != this.physics.isEnable) {
			this.physics.isEnable = boolean1;
			Bullet.setVehicleActive(this.VehicleID, boolean1);
			if (boolean1) {
				this.physicActiveCheck = System.currentTimeMillis() + 3000L;
			}
		}
	}

	public float getDebugZ() {
		return this.jniTransform.origin.y / 2.46F;
	}

	public PolygonalMap2.VehiclePoly getPoly() {
		if (this.polyDirty) {
			if (this.polyGarageCheck && this.square != null) {
				if (this.square.getRoom() != null && this.square.getRoom().RoomDef != null && this.square.getRoom().RoomDef.contains("garagestorage")) {
					this.radiusReductionInGarage = -0.3F;
				} else {
					this.radiusReductionInGarage = 0.0F;
				}

				this.polyGarageCheck = false;
			}

			this.poly.init(this, 0.0F);
			this.polyPlusRadius.init(this, PLUS_RADIUS + this.radiusReductionInGarage);
			this.polyDirty = false;
			this.polyPlusRadiusMinX = -123.0F;
			this.initShadowPoly();
		}

		return this.poly;
	}

	public PolygonalMap2.VehiclePoly getPolyPlusRadius() {
		if (this.polyDirty) {
			if (this.polyGarageCheck && this.square != null) {
				if (this.square.getRoom() != null && this.square.getRoom().RoomDef != null && this.square.getRoom().RoomDef.contains("garagestorage")) {
					this.radiusReductionInGarage = -0.3F;
				} else {
					this.radiusReductionInGarage = 0.0F;
				}

				this.polyGarageCheck = false;
			}

			this.poly.init(this, 0.0F);
			this.polyPlusRadius.init(this, PLUS_RADIUS + this.radiusReductionInGarage);
			this.polyDirty = false;
			this.polyPlusRadiusMinX = -123.0F;
			this.initShadowPoly();
		}

		return this.polyPlusRadius;
	}

	private void initShadowPoly() {
		this.getWorldTransform(this.tempTransform);
		Quaternionf quaternionf = this.tempTransform.getRotation(this.tempQuat4f);
		Vector2f vector2f = this.script.getShadowExtents();
		Vector2f vector2f2 = this.script.getShadowOffset();
		float float1 = vector2f.x / 2.0F;
		float float2 = vector2f.y / 2.0F;
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		if (quaternionf.x < 0.0F) {
			this.getWorldPos(vector2f2.x - float1, 0.0F, vector2f2.y + float2, vector3f);
			this.shadowCoord.x1 = vector3f.x;
			this.shadowCoord.y1 = vector3f.y;
			this.getWorldPos(vector2f2.x + float1, 0.0F, vector2f2.y + float2, vector3f);
			this.shadowCoord.x2 = vector3f.x;
			this.shadowCoord.y2 = vector3f.y;
			this.getWorldPos(vector2f2.x + float1, 0.0F, vector2f2.y - float2, vector3f);
			this.shadowCoord.x3 = vector3f.x;
			this.shadowCoord.y3 = vector3f.y;
			this.getWorldPos(vector2f2.x - float1, 0.0F, vector2f2.y - float2, vector3f);
			this.shadowCoord.x4 = vector3f.x;
			this.shadowCoord.y4 = vector3f.y;
		} else {
			this.getWorldPos(vector2f2.x - float1, 0.0F, vector2f2.y + float2, vector3f);
			this.shadowCoord.x1 = vector3f.x;
			this.shadowCoord.y1 = vector3f.y;
			this.getWorldPos(vector2f2.x + float1, 0.0F, vector2f2.y + float2, vector3f);
			this.shadowCoord.x2 = vector3f.x;
			this.shadowCoord.y2 = vector3f.y;
			this.getWorldPos(vector2f2.x + float1, 0.0F, vector2f2.y - float2, vector3f);
			this.shadowCoord.x3 = vector3f.x;
			this.shadowCoord.y3 = vector3f.y;
			this.getWorldPos(vector2f2.x - float1, 0.0F, vector2f2.y - float2, vector3f);
			this.shadowCoord.x4 = vector3f.x;
			this.shadowCoord.y4 = vector3f.y;
		}

		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
	}

	private void initPolyPlusRadiusBounds() {
		if (this.polyPlusRadiusMinX == -123.0F) {
			PolygonalMap2.VehiclePoly vehiclePoly = this.getPolyPlusRadius();
			Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			Vector3f vector3f2 = this.getLocalPos(vehiclePoly.x1, vehiclePoly.y1, vehiclePoly.z, vector3f);
			float float1 = (float)((int)(vector3f2.x * 100.0F)) / 100.0F;
			float float2 = (float)((int)(vector3f2.z * 100.0F)) / 100.0F;
			vector3f2 = this.getLocalPos(vehiclePoly.x2, vehiclePoly.y2, vehiclePoly.z, vector3f);
			float float3 = (float)((int)(vector3f2.x * 100.0F)) / 100.0F;
			float float4 = (float)((int)(vector3f2.z * 100.0F)) / 100.0F;
			vector3f2 = this.getLocalPos(vehiclePoly.x3, vehiclePoly.y3, vehiclePoly.z, vector3f);
			float float5 = (float)((int)(vector3f2.x * 100.0F)) / 100.0F;
			float float6 = (float)((int)(vector3f2.z * 100.0F)) / 100.0F;
			vector3f2 = this.getLocalPos(vehiclePoly.x4, vehiclePoly.y4, vehiclePoly.z, vector3f);
			float float7 = (float)((int)(vector3f2.x * 100.0F)) / 100.0F;
			float float8 = (float)((int)(vector3f2.z * 100.0F)) / 100.0F;
			this.polyPlusRadiusMinX = Math.min(float1, Math.min(float3, Math.min(float5, float7)));
			this.polyPlusRadiusMaxX = Math.max(float1, Math.max(float3, Math.max(float5, float7)));
			this.polyPlusRadiusMinY = Math.min(float2, Math.min(float4, Math.min(float6, float8)));
			this.polyPlusRadiusMaxY = Math.max(float2, Math.max(float4, Math.max(float6, float8)));
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		}
	}

	public Vector3f getForwardVector(Vector3f vector3f) {
		byte byte1 = 2;
		return this.jniTransform.basis.getColumn(byte1, vector3f);
	}

	public Vector3f getUpVector(Vector3f vector3f) {
		byte byte1 = 1;
		return this.jniTransform.basis.getColumn(byte1, vector3f);
	}

	public float getUpVectorDot() {
		Vector3f vector3f = this.getUpVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		float float1 = vector3f.dot(_UNIT_Y);
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		return float1;
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

	public float getSpeed2D() {
		float float1 = GameServer.bServer ? this.netLinearVelocity.x : this.jniLinearVelocity.x;
		float float2 = GameServer.bServer ? this.netLinearVelocity.z : this.jniLinearVelocity.z;
		return (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
	}

	public boolean isAtRest() {
		if (this.physics == null) {
			return true;
		} else {
			float float1 = GameServer.bServer ? this.netLinearVelocity.y : this.jniLinearVelocity.y;
			return Math.abs(this.physics.EngineForce) < 0.01F && this.getSpeed2D() < 0.02F && Math.abs(float1) < 0.5F;
		}
	}

	protected void updateTransform() {
		if (this.sprite.modelSlot != null) {
			float float1 = this.getScript().getModelScale();
			float float2 = 1.0F;
			if (this.sprite.modelSlot != null && this.sprite.modelSlot.model.scale != 1.0F) {
				float2 = this.sprite.modelSlot.model.scale;
			}

			Transform transform = this.getWorldTransform(this.tempTransform);
			Quaternionf quaternionf = (Quaternionf)((BaseVehicle.QuaternionfObjectPool)TL_quaternionf_pool.get()).alloc();
			Quaternionf quaternionf2 = (Quaternionf)((BaseVehicle.QuaternionfObjectPool)TL_quaternionf_pool.get()).alloc();
			Matrix4f matrix4f = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).alloc();
			transform.getRotation(quaternionf);
			quaternionf.y *= -1.0F;
			quaternionf.z *= -1.0F;
			Matrix4f matrix4f2 = quaternionf.get(matrix4f);
			float float3 = 1.0F;
			if (this.sprite.modelSlot.model.m_modelScript != null) {
				float3 = this.sprite.modelSlot.model.m_modelScript.invertX ? -1.0F : 1.0F;
			}

			Vector3f vector3f = this.script.getModel().getOffset();
			Vector3f vector3f2 = this.getScript().getModel().getRotate();
			quaternionf2.rotationXYZ(vector3f2.x * 0.017453292F, vector3f2.y * 0.017453292F, vector3f2.z * 0.017453292F);
			this.renderTransform.translationRotateScale(vector3f.x * -1.0F, vector3f.y, vector3f.z, quaternionf2.x, quaternionf2.y, quaternionf2.z, quaternionf2.w, float1 * float2 * float3, float1 * float2, float1 * float2);
			matrix4f2.mul((Matrix4fc)this.renderTransform, this.renderTransform);
			this.vehicleTransform.translationRotateScale(vector3f.x * -1.0F, vector3f.y, vector3f.z, 0.0F, 0.0F, 0.0F, 1.0F, float1);
			matrix4f2.mul((Matrix4fc)this.vehicleTransform, this.vehicleTransform);
			for (int int1 = 0; int1 < this.models.size(); ++int1) {
				BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int1);
				VehicleScript.Model model = modelInfo.scriptModel;
				vector3f2 = model.getOffset();
				Vector3f vector3f3 = model.getRotate();
				float float4 = model.scale;
				float2 = 1.0F;
				float float5 = 1.0F;
				if (modelInfo.modelScript != null) {
					float2 = modelInfo.modelScript.scale;
					float5 = modelInfo.modelScript.invertX ? -1.0F : 1.0F;
				}

				quaternionf2.rotationXYZ(vector3f3.x * 0.017453292F, vector3f3.y * 0.017453292F, vector3f3.z * 0.017453292F);
				if (modelInfo.wheelIndex == -1) {
					modelInfo.renderTransform.translationRotateScale(vector3f2.x * -1.0F, vector3f2.y, vector3f2.z, quaternionf2.x, quaternionf2.y, quaternionf2.z, quaternionf2.w, float4 * float2 * float5, float4 * float2, float4 * float2);
					this.vehicleTransform.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
				} else {
					BaseVehicle.WheelInfo wheelInfo = this.wheelInfo[modelInfo.wheelIndex];
					float float6 = wheelInfo.steering;
					float float7 = wheelInfo.rotation;
					VehicleScript.Wheel wheel = this.getScript().getWheel(modelInfo.wheelIndex);
					BaseVehicle.VehicleImpulse vehicleImpulse = modelInfo.wheelIndex < this.impulseFromSquishedZombie.length ? this.impulseFromSquishedZombie[modelInfo.wheelIndex] : null;
					float float8 = vehicleImpulse != null && vehicleImpulse.enable ? 0.05F : 0.0F;
					if (wheelInfo.suspensionLength == 0.0F) {
						matrix4f.translation(wheel.offset.x / float1 * -1.0F, wheel.offset.y / float1, wheel.offset.z / float1);
					} else {
						matrix4f.translation(wheel.offset.x / float1 * -1.0F, (wheel.offset.y + this.script.getSuspensionRestLength() - wheelInfo.suspensionLength) / float1 + float8 * 0.5F, wheel.offset.z / float1);
					}

					modelInfo.renderTransform.identity();
					modelInfo.renderTransform.mul((Matrix4fc)matrix4f);
					modelInfo.renderTransform.rotateY(float6 * -1.0F);
					modelInfo.renderTransform.rotateX(float7);
					matrix4f.translationRotateScale(vector3f2.x * -1.0F, vector3f2.y, vector3f2.z, quaternionf2.x, quaternionf2.y, quaternionf2.z, quaternionf2.w, float4 * float2 * float5, float4 * float2, float4 * float2);
					modelInfo.renderTransform.mul((Matrix4fc)matrix4f);
					this.vehicleTransform.mul((Matrix4fc)modelInfo.renderTransform, modelInfo.renderTransform);
				}
			}

			((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).release(matrix4f);
			((BaseVehicle.QuaternionfObjectPool)TL_quaternionf_pool.get()).release(quaternionf);
			((BaseVehicle.QuaternionfObjectPool)TL_quaternionf_pool.get()).release(quaternionf2);
		}
	}

	public void serverUpdateSimulatorState() {
		if (Math.abs(this.physics.clientForce) > 0.01F && !this.physics.isEnable) {
			Bullet.setVehicleActive(this.VehicleID, true);
			this.physics.isEnable = true;
		}

		if (this.physics.isEnable && Math.abs(this.physics.clientForce) < 0.01F && this.isAtRest()) {
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
			if (Bullet.getOwnVehiclePhysics(this.VehicleID, floatArray) == 0 && (Math.abs(floatArray[0] - this.x) > float1 || Math.abs(floatArray[1] - this.y) > float1)) {
				VehicleManager.instance.sendReqestGetPosition(this.VehicleID);
			}
		}
	}

	public void updateControls() {
		if (this.getController() != null) {
			if (this.isOperational()) {
				IsoPlayer player = (IsoPlayer)Type.tryCastTo(this.getDriver(), IsoPlayer.class);
				if (player == null || !player.isBlockMovement()) {
					this.getController().updateControls();
				}
			}
		}
	}

	public boolean isKeyboardControlled() {
		IsoGameCharacter gameCharacter = this.getCharacter(0);
		return gameCharacter != null && gameCharacter == IsoPlayer.players[0] && this.getVehicleTowedBy() == null;
	}

	public int getJoypad() {
		IsoGameCharacter gameCharacter = this.getCharacter(0);
		return gameCharacter != null && gameCharacter instanceof IsoPlayer ? ((IsoPlayer)gameCharacter).JoypadBind : -1;
	}

	public void Damage(float float1) {
		this.crash(float1, true);
	}

	public void HitByVehicle(BaseVehicle baseVehicle, float float1) {
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
			if (float3 < 5.0F) {
				SoundManager.instance.PlayWorldSound("VehicleCrash1", this.square, 1.0F, 20.0F, 1.0F, true);
			} else if (float3 < 30.0F) {
				SoundManager.instance.PlayWorldSound("VehicleCrash2", this.square, 1.0F, 20.0F, 1.0F, true);
			} else {
				SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0F, 20.0F, 1.0F, true);
			}
		}
	}

	private void addDamageFrontHitAChr(int int1) {
		if (int1 >= 4 || !Rand.NextBool(7)) {
			VehiclePart vehiclePart = this.getPartById("EngineDoor");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 10), int1 + 3));
			}

			if (vehiclePart != null && vehiclePart.getCondition() <= 0 && Rand.NextBool(5)) {
				vehiclePart = this.getPartById("Engine");
				if (vehiclePart != null) {
					vehiclePart.damage(Rand.Next(1, 3));
				}
			}

			if (int1 > 12) {
				vehiclePart = this.getPartById("Windshield");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					vehiclePart.damage(Rand.Next(Math.max(1, int1 - 10), int1 + 3));
				}
			}

			if (Rand.Next(5) < int1) {
				if (Rand.NextBool(2)) {
					vehiclePart = this.getPartById("TireFrontLeft");
				} else {
					vehiclePart = this.getPartById("TireFrontRight");
				}

				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					vehiclePart.damage(Rand.Next(1, 3));
				}
			}

			if (Rand.Next(7) < int1) {
				this.damageHeadlight("HeadlightLeft", Rand.Next(1, 4));
			}

			if (Rand.Next(7) < int1) {
				this.damageHeadlight("HeadlightRight", Rand.Next(1, 4));
			}

			float float1 = this.getBloodIntensity("Front");
			this.setBloodIntensity("Front", float1 + 0.01F);
		}
	}

	private void addDamageRearHitAChr(int int1) {
		if (int1 >= 4 || !Rand.NextBool(7)) {
			VehiclePart vehiclePart = this.getPartById("TruckBed");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.setCondition(vehiclePart.getCondition() - Rand.Next(Math.max(1, int1 - 10), int1 + 3));
				vehiclePart.doInventoryItemStats(vehiclePart.getInventoryItem(), 0);
				this.transmitPartCondition(vehiclePart);
			}

			vehiclePart = this.getPartById("DoorRear");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 10), int1 + 3));
			}

			vehiclePart = this.getPartById("TrunkDoor");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 10), int1 + 3));
			}

			if (int1 > 12) {
				vehiclePart = this.getPartById("WindshieldRear");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					vehiclePart.damage(int1);
				}
			}

			if (Rand.Next(5) < int1) {
				if (Rand.NextBool(2)) {
					vehiclePart = this.getPartById("TireRearLeft");
				} else {
					vehiclePart = this.getPartById("TireRearRight");
				}

				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					vehiclePart.damage(Rand.Next(1, 3));
				}
			}

			if (Rand.Next(7) < int1) {
				this.damageHeadlight("HeadlightRearLeft", Rand.Next(1, 4));
			}

			if (Rand.Next(7) < int1) {
				this.damageHeadlight("HeadlightRearRight", Rand.Next(1, 4));
			}

			if (Rand.Next(6) < int1) {
				vehiclePart = this.getPartById("GasTank");
				if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
					vehiclePart.damage(Rand.Next(1, 3));
				}
			}

			float float1 = this.getBloodIntensity("Rear");
			this.setBloodIntensity("Rear", float1 + 0.01F);
		}
	}

	private void addDamageFront(int int1) {
		this.currentFrontEndDurability -= int1;
		VehiclePart vehiclePart = this.getPartById("EngineDoor");
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
		}

		if (vehiclePart != null && vehiclePart.getCondition() < 25) {
			vehiclePart = this.getPartById("Engine");
			if (vehiclePart != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 3), int1 + 3));
			}
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
			this.damageHeadlight("HeadlightLeft", int1);
		}

		if (Rand.Next(20) < int1) {
			this.damageHeadlight("HeadlightRight", int1);
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
			this.damageHeadlight("HeadlightRearLeft", int1);
		}

		if (Rand.Next(20) < int1) {
			this.damageHeadlight("HeadlightRearRight", int1);
		}

		if (Rand.Next(20) < int1) {
			vehiclePart = this.getPartById("Muffler");
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				vehiclePart.damage(Rand.Next(Math.max(1, int1 - 5), int1 + 5));
			}
		}
	}

	private void damageHeadlight(String string, int int1) {
		VehiclePart vehiclePart = this.getPartById(string);
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			vehiclePart.damage(int1);
			if (vehiclePart.getCondition() <= 0) {
				vehiclePart.setInventoryItem((InventoryItem)null);
				this.transmitPartItem(vehiclePart);
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
			Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			vector3f.set(gameCharacter.x - WorldSimulation.instance.offsetX, 0.0F, gameCharacter.y - WorldSimulation.instance.offsetY);
			transform.transform(vector3f);
			Vector3f vector3f2 = this.script.getExtents();
			Vector3f vector3f3 = this.script.getCenterOfMassOffset();
			float float1 = vector3f3.x - vector3f2.x / 2.0F;
			float float2 = vector3f3.x + vector3f2.x / 2.0F;
			float float3 = vector3f3.z - vector3f2.z / 2.0F;
			float float4 = vector3f3.z + vector3f2.z / 2.0F;
			if (vector3f.x >= float1 - 0.4F && vector3f.x < float2 + 0.4F && vector3f.z >= float3 - 0.4F && vector3f.z < float4 + 0.4F) {
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				return true;
			} else {
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				return false;
			}
		}
	}

	public Vector2 testCollisionWithCharacter(IsoGameCharacter gameCharacter, float float1, Vector2 vector2) {
		if (this.physics == null) {
			return null;
		} else {
			Vector3f vector3f = this.script.getExtents();
			Vector3f vector3f2 = this.script.getCenterOfMassOffset();
			if (this.DistToProper(gameCharacter) > Math.max(vector3f.x / 2.0F, vector3f.z / 2.0F) + float1 + 1.0F) {
				return null;
			} else {
				Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				this.getLocalPos(gameCharacter.nx, gameCharacter.ny, 0.0F, vector3f3);
				float float2 = vector3f2.x - vector3f.x / 2.0F;
				float float3 = vector3f2.x + vector3f.x / 2.0F;
				float float4 = vector3f2.z - vector3f.z / 2.0F;
				float float5 = vector3f2.z + vector3f.z / 2.0F;
				float float6;
				float float7;
				float float8;
				float float9;
				if (vector3f3.x > float2 && vector3f3.x < float3 && vector3f3.z > float4 && vector3f3.z < float5) {
					float6 = vector3f3.x - float2;
					float7 = float3 - vector3f3.x;
					float8 = vector3f3.z - float4;
					float9 = float5 - vector3f3.z;
					Vector3f vector3f4 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
					if (float6 < float7 && float6 < float8 && float6 < float9) {
						vector3f4.set(float2 - float1 - 0.015F, 0.0F, vector3f3.z);
					} else if (float7 < float6 && float7 < float8 && float7 < float9) {
						vector3f4.set(float3 + float1 + 0.015F, 0.0F, vector3f3.z);
					} else if (float8 < float6 && float8 < float7 && float8 < float9) {
						vector3f4.set(vector3f3.x, 0.0F, float4 - float1 - 0.015F);
					} else if (float9 < float6 && float9 < float7 && float9 < float8) {
						vector3f4.set(vector3f3.x, 0.0F, float5 + float1 + 0.015F);
					}

					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
					Transform transform = this.getWorldTransform(this.tempTransform);
					transform.origin.set(0.0F, 0.0F, 0.0F);
					transform.transform(vector3f4);
					vector3f4.x += this.getX();
					vector3f4.z += this.getY();
					this.collideX = vector3f4.x;
					this.collideY = vector3f4.z;
					vector2.set(vector3f4.x, vector3f4.z);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f4);
					return vector2;
				} else {
					float6 = this.clamp(vector3f3.x, float2, float3);
					float7 = this.clamp(vector3f3.z, float4, float5);
					float8 = vector3f3.x - float6;
					float9 = vector3f3.z - float7;
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
					float float10 = float8 * float8 + float9 * float9;
					if (float10 < float1 * float1) {
						if (float8 == 0.0F && float9 == 0.0F) {
							return vector2.set(-1.0F, -1.0F);
						} else {
							Vector3f vector3f5 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
							vector3f5.set(float8, 0.0F, float9);
							vector3f5.normalize();
							vector3f5.mul(float1 + 0.015F);
							vector3f5.x += float6;
							vector3f5.z += float7;
							Transform transform2 = this.getWorldTransform(this.tempTransform);
							transform2.origin.set(0.0F, 0.0F, 0.0F);
							transform2.transform(vector3f5);
							vector3f5.x += this.getX();
							vector3f5.z += this.getY();
							this.collideX = vector3f5.x;
							this.collideY = vector3f5.z;
							vector2.set(vector3f5.x, vector3f5.z);
							((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f5);
							return vector2;
						}
					} else {
						return null;
					}
				}
			}
		}
	}

	public int testCollisionWithProneCharacter(IsoGameCharacter gameCharacter, boolean boolean1) {
		Vector2 vector2 = gameCharacter.getAnimVector((Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc());
		int int1 = this.testCollisionWithProneCharacter(gameCharacter, vector2.x, vector2.y, boolean1);
		((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
		return int1;
	}

	public int testCollisionWithCorpse(IsoDeadBody deadBody, boolean boolean1) {
		float float1 = (float)Math.cos((double)deadBody.getAngle());
		float float2 = (float)Math.sin((double)deadBody.getAngle());
		int int1 = this.testCollisionWithProneCharacter(deadBody, float1, float2, boolean1);
		return int1;
	}

	public int testCollisionWithProneCharacter(IsoMovingObject movingObject, float float1, float float2, boolean boolean1) {
		if (this.physics == null) {
			return 0;
		} else if (GameServer.bServer) {
			return 0;
		} else {
			Vector3f vector3f = this.script.getExtents();
			float float3 = 0.3F;
			if (this.DistToProper(movingObject) > Math.max(vector3f.x / 2.0F, vector3f.z / 2.0F) + float3 + 1.0F) {
				return 0;
			} else if (Math.abs(this.jniSpeed) < 3.0F) {
				return 0;
			} else {
				float float4 = movingObject.x + float1 * 0.65F;
				float float5 = movingObject.y + float2 * 0.65F;
				float float6 = movingObject.x - float1 * 0.65F;
				float float7 = movingObject.y - float2 * 0.65F;
				int int1 = 0;
				Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				for (int int2 = 0; int2 < this.script.getWheelCount(); ++int2) {
					VehicleScript.Wheel wheel = this.script.getWheel(int2);
					boolean boolean2 = true;
					for (int int3 = 0; int3 < this.models.size(); ++int3) {
						BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)this.models.get(int3);
						if (modelInfo.wheelIndex == int2) {
							this.getWorldPos(wheel.offset.x, wheel.offset.y - this.wheelInfo[int2].suspensionLength, wheel.offset.z, vector3f2);
							if (vector3f2.z > this.script.getWheel(int2).radius + 0.05F) {
								boolean2 = false;
							}

							break;
						}
					}

					if (boolean2) {
						this.getWorldPos(wheel.offset.x, wheel.offset.y, wheel.offset.z, vector3f3);
						float float8 = vector3f3.x;
						float float9 = vector3f3.y;
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

						if (!(IsoUtils.DistanceToSquared(vector3f3.x, vector3f3.y, float10, float11) > wheel.radius * wheel.radius)) {
							if (boolean1 && Math.abs(this.jniSpeed) > 10.0F) {
								if (GameServer.bServer && movingObject instanceof IsoZombie) {
									((IsoZombie)movingObject).setThumpFlag(1);
								} else {
									SoundManager.instance.PlayWorldSound("VehicleRunOverBody", movingObject.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
								}

								boolean1 = false;
							}

							if (int2 < this.impulseFromSquishedZombie.length) {
								if (this.impulseFromSquishedZombie[int2] == null) {
									this.impulseFromSquishedZombie[int2] = new BaseVehicle.VehicleImpulse();
								}

								this.impulseFromSquishedZombie[int2].impulse.set(0.0F, 1.0F, 0.0F);
								float float12 = Math.max(Math.abs(this.jniSpeed), 20.0F) / 20.0F;
								this.impulseFromSquishedZombie[int2].impulse.mul(0.065F * this.getFudgedMass() * float12);
								this.impulseFromSquishedZombie[int2].rel_pos.set(vector3f3.x - this.x, 0.0F, vector3f3.y - this.y);
								this.impulseFromSquishedZombie[int2].enable = true;
								++int1;
							}
						}
					}
				}

				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
				return int1;
			}
		}
	}

	public Vector2 testCollisionWithObject(IsoObject object, float float1, Vector2 vector2) {
		if (this.physics == null) {
			return null;
		} else if (object.square == null) {
			return null;
		} else {
			float float2 = this.getObjectX(object);
			float float3 = this.getObjectY(object);
			Vector3f vector3f = this.script.getExtents();
			Vector3f vector3f2 = this.script.getCenterOfMassOffset();
			float float4 = Math.max(vector3f.x / 2.0F, vector3f.z / 2.0F) + float1 + 1.0F;
			if (this.DistToSquared(float2, float3) > float4 * float4) {
				return null;
			} else {
				Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				this.getLocalPos(float2, float3, 0.0F, vector3f3);
				float float5 = vector3f2.x - vector3f.x / 2.0F;
				float float6 = vector3f2.x + vector3f.x / 2.0F;
				float float7 = vector3f2.z - vector3f.z / 2.0F;
				float float8 = vector3f2.z + vector3f.z / 2.0F;
				float float9;
				float float10;
				float float11;
				float float12;
				if (vector3f3.x > float5 && vector3f3.x < float6 && vector3f3.z > float7 && vector3f3.z < float8) {
					float9 = vector3f3.x - float5;
					float10 = float6 - vector3f3.x;
					float11 = vector3f3.z - float7;
					float12 = float8 - vector3f3.z;
					Vector3f vector3f4 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
					if (float9 < float10 && float9 < float11 && float9 < float12) {
						vector3f4.set(float5 - float1 - 0.015F, 0.0F, vector3f3.z);
					} else if (float10 < float9 && float10 < float11 && float10 < float12) {
						vector3f4.set(float6 + float1 + 0.015F, 0.0F, vector3f3.z);
					} else if (float11 < float9 && float11 < float10 && float11 < float12) {
						vector3f4.set(vector3f3.x, 0.0F, float7 - float1 - 0.015F);
					} else if (float12 < float9 && float12 < float10 && float12 < float11) {
						vector3f4.set(vector3f3.x, 0.0F, float8 + float1 + 0.015F);
					}

					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
					Transform transform = this.getWorldTransform(this.tempTransform);
					transform.origin.set(0.0F, 0.0F, 0.0F);
					transform.transform(vector3f4);
					vector3f4.x += this.getX();
					vector3f4.z += this.getY();
					this.collideX = vector3f4.x;
					this.collideY = vector3f4.z;
					vector2.set(vector3f4.x, vector3f4.z);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f4);
					return vector2;
				} else {
					float9 = this.clamp(vector3f3.x, float5, float6);
					float10 = this.clamp(vector3f3.z, float7, float8);
					float11 = vector3f3.x - float9;
					float12 = vector3f3.z - float10;
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
					float float13 = float11 * float11 + float12 * float12;
					if (float13 < float1 * float1) {
						if (float11 == 0.0F && float12 == 0.0F) {
							return vector2.set(-1.0F, -1.0F);
						} else {
							Vector3f vector3f5 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
							vector3f5.set(float11, 0.0F, float12);
							vector3f5.normalize();
							vector3f5.mul(float1 + 0.015F);
							vector3f5.x += float9;
							vector3f5.z += float10;
							Transform transform2 = this.getWorldTransform(this.tempTransform);
							transform2.origin.set(0.0F, 0.0F, 0.0F);
							transform2.transform(vector3f5);
							vector3f5.x += this.getX();
							vector3f5.z += this.getY();
							this.collideX = vector3f5.x;
							this.collideY = vector3f5.z;
							vector2.set(vector3f5.x, vector3f5.z);
							((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f5);
							return vector2;
						}
					} else {
						return null;
					}
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

		if (vehicleScript != null && vehicleScript2 != null) {
			if (testVecs1[0] == null) {
				for (int int1 = 0; int1 < testVecs1.length; ++int1) {
					testVecs1[int1] = new Vector2();
					testVecs2[int1] = new Vector2();
				}
			}

			Vector3f vector3f = vehicleScript.getExtents();
			Vector3f vector3f2 = vehicleScript.getCenterOfMassOffset();
			Vector3f vector3f3 = vehicleScript2.getExtents();
			Vector3f vector3f4 = vehicleScript2.getCenterOfMassOffset();
			Vector3f vector3f5 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			float float1 = 0.5F;
			this.getWorldPos(vector3f2.x + vector3f.x * float1, 0.0F, vector3f2.z + vector3f.z * float1, vector3f5, vehicleScript);
			testVecs1[0].set(vector3f5.x, vector3f5.y);
			this.getWorldPos(vector3f2.x - vector3f.x * float1, 0.0F, vector3f2.z + vector3f.z * float1, vector3f5, vehicleScript);
			testVecs1[1].set(vector3f5.x, vector3f5.y);
			this.getWorldPos(vector3f2.x - vector3f.x * float1, 0.0F, vector3f2.z - vector3f.z * float1, vector3f5, vehicleScript);
			testVecs1[2].set(vector3f5.x, vector3f5.y);
			this.getWorldPos(vector3f2.x + vector3f.x * float1, 0.0F, vector3f2.z - vector3f.z * float1, vector3f5, vehicleScript);
			testVecs1[3].set(vector3f5.x, vector3f5.y);
			float1 = 0.5F;
			baseVehicle.getWorldPos(vector3f4.x + vector3f3.x * float1, 0.0F, vector3f4.z + vector3f3.z * float1, vector3f5, vehicleScript2);
			testVecs2[0].set(vector3f5.x, vector3f5.y);
			baseVehicle.getWorldPos(vector3f4.x - vector3f3.x * float1, 0.0F, vector3f4.z + vector3f3.z * float1, vector3f5, vehicleScript2);
			testVecs2[1].set(vector3f5.x, vector3f5.y);
			baseVehicle.getWorldPos(vector3f4.x - vector3f3.x * float1, 0.0F, vector3f4.z - vector3f3.z * float1, vector3f5, vehicleScript2);
			testVecs2[2].set(vector3f5.x, vector3f5.y);
			baseVehicle.getWorldPos(vector3f4.x + vector3f3.x * float1, 0.0F, vector3f4.z - vector3f3.z * float1, vector3f5, vehicleScript2);
			testVecs2[3].set(vector3f5.x, vector3f5.y);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f5);
			return QuadranglesIntersection.IsQuadranglesAreIntersected(testVecs1, testVecs2);
		} else {
			return false;
		}
	}

	protected float getObjectX(IsoObject object) {
		return object instanceof IsoMovingObject ? object.getX() : (float)object.getSquare().getX() + 0.5F;
	}

	protected float getObjectY(IsoObject object) {
		return object instanceof IsoMovingObject ? object.getY() : (float)object.getSquare().getY() + 0.5F;
	}

	public void ApplyImpulse(IsoObject object, float float1) {
		float float2 = this.getObjectX(object);
		float float3 = this.getObjectY(object);
		BaseVehicle.VehicleImpulse vehicleImpulse = BaseVehicle.VehicleImpulse.alloc();
		vehicleImpulse.impulse.set(this.x - float2, 0.0F, this.y - float3);
		vehicleImpulse.impulse.normalize();
		vehicleImpulse.impulse.mul(float1);
		vehicleImpulse.rel_pos.set(float2 - this.x, 0.0F, float3 - this.y);
		this.impulseFromHitZombie.add(vehicleImpulse);
	}

	public void ApplyImpulse4Break(IsoObject object, float float1) {
		float float2 = this.getObjectX(object);
		float float3 = this.getObjectY(object);
		BaseVehicle.VehicleImpulse vehicleImpulse = BaseVehicle.VehicleImpulse.alloc();
		this.getLinearVelocity(vehicleImpulse.impulse);
		vehicleImpulse.impulse.mul(-float1 * this.getFudgedMass());
		vehicleImpulse.rel_pos.set(float2 - this.x, 0.0F, float3 - this.y);
		this.impulseFromHitZombie.add(vehicleImpulse);
	}

	public void hitCharacter(IsoGameCharacter gameCharacter) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (gameCharacter.getCurrentState() != StaggerBackState.instance() && gameCharacter.getCurrentState() != ZombieFallDownState.instance()) {
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
				Vector3f vector3f = this.getLinearVelocity((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
				vector3f.y = 0.0F;
				float float2 = vector3f.length();
				float2 = Math.min(float2, float1);
				if (float2 < 0.05F) {
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				} else {
					Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
					vector3f2.set(this.x - gameCharacter.x, 0.0F, this.y - gameCharacter.y);
					vector3f2.normalize();
					vector3f.normalize();
					float float3 = vector3f.dot(vector3f2);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
					if (float3 < 0.0F && !GameServer.bServer) {
						this.ApplyImpulse(gameCharacter, this.getFudgedMass() * 7.0F * float2 / float1 * Math.abs(float3));
					}

					vector3f2.normalize();
					vector3f2.mul(3.0F * float2 / float1);
					Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
					float float4 = float2 + this.physics.clientForce / this.getFudgedMass();
					if (GameClient.bClient) {
						SoundManager.instance.PlayWorldSound("VehicleHitCharacter", gameCharacter.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
						gameCharacter.Hit(this, float4, float3, vector2.set(-vector3f2.x, -vector3f2.z));
						IsoGameCharacter gameCharacter2 = this.getDriver();
						if (gameCharacter2 instanceof IsoPlayer && this.isDriver(IsoPlayer.getInstance())) {
							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).networkAI.reanimateTimer = (float)(Rand.Next(60) + 30);
							}

							GameClient.sendHitVehicle(gameCharacter2, gameCharacter, this, float4, float3, vector2.x, vector2.y);
						}

						gameCharacter.clientIgnoreCollision = System.currentTimeMillis();
						((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
						((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
					} else {
						if (GameServer.bServer) {
							if (gameCharacter instanceof IsoZombie) {
								((IsoZombie)gameCharacter).setThumpFlag(1);
							}
						} else {
							if (player != null) {
								player.setVehicleHitLocation(this);
							} else if (zombie != null) {
								zombie.setVehicleHitLocation(this);
							}

							BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(gameCharacter.x, gameCharacter.y, gameCharacter.z);
							long long1 = baseSoundEmitter.playSound("VehicleHitCharacter");
							baseSoundEmitter.setParameterValue(long1, FMODManager.instance.getParameterDescription("VehicleSpeed"), this.getCurrentSpeedKmHour());
							gameCharacter.Hit(this, float4, float3, vector2.set(-vector3f2.x, -vector3f2.z));
						}

						((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
						((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
						long long2 = System.currentTimeMillis();
						long long3 = (long2 - this.zombieHitTimestamp) / 1000L;
						this.zombiesHits = Math.max(this.zombiesHits - (int)long3, 0);
						if (long2 - this.zombieHitTimestamp > 700L) {
							this.zombieHitTimestamp = long2;
							++this.zombiesHits;
							this.zombiesHits = Math.min(this.zombiesHits, 20);
						}

						if (float2 >= 5.0F || this.zombiesHits > 10) {
							float2 = this.getCurrentSpeedKmHour() / 5.0F;
							Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
							this.getLocalPos(gameCharacter.x, gameCharacter.y, gameCharacter.z, vector3f3);
							int int1;
							if (vector3f3.z > 0.0F) {
								int1 = this.caclulateDamageWithBodies(true);
								this.addDamageFrontHitAChr(int1);
							} else {
								int1 = this.caclulateDamageWithBodies(false);
								this.addDamageRearHitAChr(int1);
							}

							((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
						}
					}
				}
			}
		}
	}

	private int caclulateDamageWithBodies(boolean boolean1) {
		boolean boolean2 = this.getCurrentSpeedKmHour() > 0.0F;
		float float1 = Math.abs(this.getCurrentSpeedKmHour());
		float float2 = float1 / 160.0F;
		float2 = PZMath.clamp(float2 * float2, 0.0F, 1.0F);
		float float3 = 60.0F * float2;
		float float4 = PZMath.max(1.0F, (float)this.zombiesHits / 3.0F);
		if (!boolean1 && !boolean2) {
			float4 = 1.0F;
		}

		if (this.zombiesHits > 10 && float3 < Math.abs(this.getCurrentSpeedKmHour()) / 5.0F) {
			float3 = Math.abs(this.getCurrentSpeedKmHour()) / 5.0F;
		}

		return (int)(float4 * float3);
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
				Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
				vector3f.set((float)int1 + 0.5F - WorldSimulation.instance.offsetX, 0.0F, (float)int2 + 0.5F - WorldSimulation.instance.offsetY);
				transform.transform(vector3f);
				Vector3f vector3f2 = this.script.getExtents();
				Vector3f vector3f3 = this.script.getCenterOfMassOffset();
				float float2 = this.clamp(vector3f.x, vector3f3.x - vector3f2.x / 2.0F, vector3f3.x + vector3f2.x / 2.0F);
				float float3 = this.clamp(vector3f.z, vector3f3.z - vector3f2.z / 2.0F, vector3f3.z + vector3f2.z / 2.0F);
				float float4 = vector3f.x - float2;
				float float5 = vector3f.z - float3;
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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
			Vector3f vector3f = this.script.getExtents();
			Vector3f vector3f2 = this.script.getCenterOfMassOffset();
			Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			this.getLocalPos(float1, float2, float3, vector3f3);
			float float5 = vector3f2.x - vector3f.x / 2.0F;
			float float6 = vector3f2.x + vector3f.x / 2.0F;
			float float7 = vector3f2.z - vector3f.z / 2.0F;
			float float8 = vector3f2.z + vector3f.z / 2.0F;
			if (vector3f3.x > float5 && vector3f3.x < float6 && vector3f3.z > float7 && vector3f3.z < float8) {
				return true;
			} else {
				float float9 = this.clamp(vector3f3.x, float5, float6);
				float float10 = this.clamp(vector3f3.z, float7, float8);
				float float11 = vector3f3.x - float9;
				float float12 = vector3f3.z - float10;
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
				float float13 = float11 * float11 + float12 * float12;
				return float13 < float4 * float4;
			}
		}
	}

	public void updateLights() {
		VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
		vehicleModelInstance.textureRustA = this.rust;
		if (this.script.getWheelCount() == 0) {
			vehicleModelInstance.textureRustA = 0.0F;
		}

		vehicleModelInstance.painColor.x = this.colorHue;
		vehicleModelInstance.painColor.y = this.colorSaturation;
		vehicleModelInstance.painColor.z = this.colorValue;
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		boolean boolean5 = false;
		boolean boolean6 = false;
		boolean boolean7 = false;
		boolean boolean8 = false;
		if (this.windowLightsOn) {
			VehiclePart vehiclePart = this.getPartById("Windshield");
			boolean1 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindshieldRear");
			boolean2 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindowFrontLeft");
			boolean3 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindowMiddleLeft");
			boolean4 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindowRearLeft");
			boolean5 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindowFrontRight");
			boolean6 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindowMiddleRight");
			boolean7 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
			vehiclePart = this.getPartById("WindowRearRight");
			boolean8 = vehiclePart != null && vehiclePart.getInventoryItem() != null;
		}

		vehicleModelInstance.textureLightsEnables1[10] = boolean1 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables1[14] = boolean2 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables1[2] = boolean3 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables1[6] = boolean4 | boolean5 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables1[9] = boolean6 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables1[13] = boolean7 | boolean8 ? 1.0F : 0.0F;
		boolean boolean9 = false;
		boolean boolean10 = false;
		boolean boolean11 = false;
		boolean boolean12 = false;
		if (this.headlightsOn && this.getBatteryCharge() > 0.0F) {
			VehiclePart vehiclePart2 = this.getPartById("HeadlightLeft");
			if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
				boolean9 = true;
			}

			vehiclePart2 = this.getPartById("HeadlightRight");
			if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
				boolean10 = true;
			}

			vehiclePart2 = this.getPartById("HeadlightRearLeft");
			if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
				boolean12 = true;
			}

			vehiclePart2 = this.getPartById("HeadlightRearRight");
			if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
				boolean11 = true;
			}
		}

		vehicleModelInstance.textureLightsEnables2[4] = boolean10 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables2[8] = boolean9 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables2[12] = boolean11 ? 1.0F : 0.0F;
		vehicleModelInstance.textureLightsEnables2[1] = boolean12 ? 1.0F : 0.0F;
		boolean boolean13 = this.stoplightsOn && this.getBatteryCharge() > 0.0F;
		if (this.scriptName.contains("Trailer") && this.vehicleTowedBy != null && this.vehicleTowedBy.stoplightsOn && this.vehicleTowedBy.getBatteryCharge() > 0.0F) {
			boolean13 = true;
		}

		if (boolean13) {
			vehicleModelInstance.textureLightsEnables2[5] = 1.0F;
			vehicleModelInstance.textureLightsEnables2[9] = 1.0F;
		} else {
			vehicleModelInstance.textureLightsEnables2[5] = 0.0F;
			vehicleModelInstance.textureLightsEnables2[9] = 0.0F;
		}

		if (this.script.getLightbar().enable) {
			if (this.lightbarLightsMode.isEnable() && this.getBatteryCharge() > 0.0F) {
				switch (this.lightbarLightsMode.getLightTexIndex()) {
				case 0: 
					vehicleModelInstance.textureLightsEnables2[13] = 0.0F;
					vehicleModelInstance.textureLightsEnables2[2] = 0.0F;
					break;
				
				case 1: 
					vehicleModelInstance.textureLightsEnables2[13] = 0.0F;
					vehicleModelInstance.textureLightsEnables2[2] = 1.0F;
					break;
				
				case 2: 
					vehicleModelInstance.textureLightsEnables2[13] = 1.0F;
					vehicleModelInstance.textureLightsEnables2[2] = 0.0F;
					break;
				
				default: 
					vehicleModelInstance.textureLightsEnables2[13] = 0.0F;
					vehicleModelInstance.textureLightsEnables2[2] = 0.0F;
				
				}
			} else {
				vehicleModelInstance.textureLightsEnables2[13] = 0.0F;
				vehicleModelInstance.textureLightsEnables2[2] = 0.0F;
			}
		}

		if (DebugOptions.instance.VehicleCycleColor.getValue()) {
			float float1 = (float)(System.currentTimeMillis() % 2000L);
			float float2 = (float)(System.currentTimeMillis() % 7000L);
			float float3 = (float)(System.currentTimeMillis() % 11000L);
			vehicleModelInstance.painColor.x = float1 / 2000.0F;
			vehicleModelInstance.painColor.y = float2 / 7000.0F;
			vehicleModelInstance.painColor.z = float3 / 11000.0F;
		}

		if (DebugOptions.instance.VehicleRenderBlood0.getValue()) {
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 0.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 0.0F);
		}

		if (DebugOptions.instance.VehicleRenderBlood50.getValue()) {
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 0.5F);
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 0.5F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 1.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 1.0F);
		}

		if (DebugOptions.instance.VehicleRenderBlood100.getValue()) {
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 1.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 1.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 1.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 1.0F);
		}

		if (DebugOptions.instance.VehicleRenderDamage0.getValue()) {
			Arrays.fill(vehicleModelInstance.textureDamage1Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.textureDamage1Enables2, 0.0F);
			Arrays.fill(vehicleModelInstance.textureDamage2Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.textureDamage2Enables2, 0.0F);
		}

		if (DebugOptions.instance.VehicleRenderDamage1.getValue()) {
			Arrays.fill(vehicleModelInstance.textureDamage1Enables1, 1.0F);
			Arrays.fill(vehicleModelInstance.textureDamage1Enables2, 1.0F);
			Arrays.fill(vehicleModelInstance.textureDamage2Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.textureDamage2Enables2, 0.0F);
		}

		if (DebugOptions.instance.VehicleRenderDamage2.getValue()) {
			Arrays.fill(vehicleModelInstance.textureDamage1Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.textureDamage1Enables2, 0.0F);
			Arrays.fill(vehicleModelInstance.textureDamage2Enables1, 1.0F);
			Arrays.fill(vehicleModelInstance.textureDamage2Enables2, 1.0F);
		}

		if (DebugOptions.instance.VehicleRenderRust0.getValue()) {
			vehicleModelInstance.textureRustA = 0.0F;
		}

		if (DebugOptions.instance.VehicleRenderRust50.getValue()) {
			vehicleModelInstance.textureRustA = 0.5F;
		}

		if (DebugOptions.instance.VehicleRenderRust100.getValue()) {
			vehicleModelInstance.textureRustA = 1.0F;
		}

		vehicleModelInstance.refBody = 0.3F;
		vehicleModelInstance.refWindows = 0.4F;
		if (this.rust > 0.8F) {
			vehicleModelInstance.refBody = 0.1F;
			vehicleModelInstance.refWindows = 0.2F;
		}
	}

	private void updateWorldLights() {
		if (!this.script.getLightbar().enable) {
			this.removeWorldLights();
		} else if (this.lightbarLightsMode.isEnable() && !(this.getBatteryCharge() <= 0.0F)) {
			if (this.lightbarLightsMode.getLightTexIndex() == 0) {
				this.removeWorldLights();
			} else {
				this.leftLight1.radius = this.leftLight2.radius = this.rightLight1.radius = this.rightLight2.radius = 8;
				Vector3f vector3f;
				int int1;
				int int2;
				int int3;
				int int4;
				IsoLightSource lightSource;
				if (this.lightbarLightsMode.getLightTexIndex() == 1) {
					vector3f = this.getWorldPos(0.4F, 0.0F, 0.0F, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
					int1 = (int)vector3f.x;
					int2 = (int)vector3f.y;
					int3 = (int)(this.getZ() + 1.0F);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
					int4 = this.leftLightIndex;
					if (int4 == 1 && this.leftLight1.x == int1 && this.leftLight1.y == int2 && this.leftLight1.z == int3) {
						return;
					}

					if (int4 == 2 && this.leftLight2.x == int1 && this.leftLight2.y == int2 && this.leftLight2.z == int3) {
						return;
					}

					this.removeWorldLights();
					if (int4 == 1) {
						lightSource = this.leftLight2;
						this.leftLightIndex = 2;
					} else {
						lightSource = this.leftLight1;
						this.leftLightIndex = 1;
					}

					lightSource.life = -1;
					lightSource.x = int1;
					lightSource.y = int2;
					lightSource.z = int3;
					IsoWorld.instance.CurrentCell.addLamppost(lightSource);
				} else {
					vector3f = this.getWorldPos(-0.4F, 0.0F, 0.0F, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
					int1 = (int)vector3f.x;
					int2 = (int)vector3f.y;
					int3 = (int)(this.getZ() + 1.0F);
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
					int4 = this.rightLightIndex;
					if (int4 == 1 && this.rightLight1.x == int1 && this.rightLight1.y == int2 && this.rightLight1.z == int3) {
						return;
					}

					if (int4 == 2 && this.rightLight2.x == int1 && this.rightLight2.y == int2 && this.rightLight2.z == int3) {
						return;
					}

					this.removeWorldLights();
					if (int4 == 1) {
						lightSource = this.rightLight2;
						this.rightLightIndex = 2;
					} else {
						lightSource = this.rightLight1;
						this.rightLightIndex = 1;
					}

					lightSource.life = -1;
					lightSource.x = int1;
					lightSource.y = int2;
					lightSource.z = int3;
					IsoWorld.instance.CurrentCell.addLamppost(lightSource);
				}
			}
		} else {
			this.removeWorldLights();
		}
	}

	public void fixLightbarModelLighting(IsoLightSource lightSource, Vector3f vector3f) {
		if (lightSource != this.leftLight1 && lightSource != this.leftLight2) {
			if (lightSource == this.rightLight1 || lightSource == this.rightLight2) {
				vector3f.set(-1.0F, 0.0F, 0.0F);
			}
		} else {
			vector3f.set(1.0F, 0.0F, 0.0F);
		}
	}

	private void removeWorldLights() {
		if (this.leftLightIndex == 1) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.leftLight1);
			this.leftLightIndex = -1;
		}

		if (this.leftLightIndex == 2) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.leftLight2);
			this.leftLightIndex = -1;
		}

		if (this.rightLightIndex == 1) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.rightLight1);
			this.rightLightIndex = -1;
		}

		if (this.rightLightIndex == 2) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.rightLight2);
			this.rightLightIndex = -1;
		}
	}

	public void doDamageOverlay() {
		if (this.sprite.modelSlot != null) {
			this.doDoorDamage();
			this.doWindowDamage();
			this.doOtherBodyWorkDamage();
			this.doBloodOverlay();
		}
	}

	private void checkDamage(VehiclePart vehiclePart, int int1, boolean boolean1) {
		if (boolean1 && vehiclePart != null && vehiclePart.getId().startsWith("Window") && vehiclePart.getScriptModelById("Default") != null) {
			boolean1 = false;
		}

		VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
		try {
			vehicleModelInstance.textureDamage1Enables1[int1] = 0.0F;
			vehicleModelInstance.textureDamage2Enables1[int1] = 0.0F;
			vehicleModelInstance.textureUninstall1[int1] = 0.0F;
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				if (vehiclePart.getInventoryItem().getCondition() < 60 && vehiclePart.getInventoryItem().getCondition() >= 40) {
					vehicleModelInstance.textureDamage1Enables1[int1] = 1.0F;
				}

				if (vehiclePart.getInventoryItem().getCondition() < 40) {
					vehicleModelInstance.textureDamage2Enables1[int1] = 1.0F;
				}

				if (vehiclePart.window != null && vehiclePart.window.isOpen() && boolean1) {
					vehicleModelInstance.textureUninstall1[int1] = 1.0F;
				}
			} else if (vehiclePart != null && boolean1) {
				vehicleModelInstance.textureUninstall1[int1] = 1.0F;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void checkDamage2(VehiclePart vehiclePart, int int1, boolean boolean1) {
		VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
		try {
			vehicleModelInstance.textureDamage1Enables2[int1] = 0.0F;
			vehicleModelInstance.textureDamage2Enables2[int1] = 0.0F;
			vehicleModelInstance.textureUninstall2[int1] = 0.0F;
			if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
				if (vehiclePart.getInventoryItem().getCondition() < 60 && vehiclePart.getInventoryItem().getCondition() >= 40) {
					vehicleModelInstance.textureDamage1Enables2[int1] = 1.0F;
				}

				if (vehiclePart.getInventoryItem().getCondition() < 40) {
					vehicleModelInstance.textureDamage2Enables2[int1] = 1.0F;
				}

				if (vehiclePart.window != null && vehiclePart.window.isOpen() && boolean1) {
					vehicleModelInstance.textureUninstall2[int1] = 1.0F;
				}
			} else if (vehiclePart != null && boolean1) {
				vehicleModelInstance.textureUninstall2[int1] = 1.0F;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void checkUninstall2(VehiclePart vehiclePart, int int1) {
		VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
		try {
			vehicleModelInstance.textureUninstall2[int1] = 0.0F;
			if (vehiclePart != null && vehiclePart.getInventoryItem() == null) {
				vehicleModelInstance.textureUninstall2[int1] = 1.0F;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void doOtherBodyWorkDamage() {
		this.checkDamage(this.getPartById("EngineDoor"), 0, false);
		this.checkDamage(this.getPartById("EngineDoor"), 3, false);
		this.checkDamage(this.getPartById("EngineDoor"), 11, false);
		this.checkDamage2(this.getPartById("EngineDoor"), 6, true);
		this.checkDamage(this.getPartById("TruckBed"), 4, false);
		this.checkDamage(this.getPartById("TruckBed"), 7, false);
		this.checkDamage(this.getPartById("TruckBed"), 15, false);
		VehiclePart vehiclePart = this.getPartById("TrunkDoor");
		if (vehiclePart != null) {
			this.checkDamage2(vehiclePart, 10, true);
			if (vehiclePart.scriptPart.hasLightsRear) {
				this.checkUninstall2(vehiclePart, 12);
				this.checkUninstall2(vehiclePart, 1);
				this.checkUninstall2(vehiclePart, 5);
				this.checkUninstall2(vehiclePart, 9);
			}
		} else {
			vehiclePart = this.getPartById("DoorRear");
			if (vehiclePart != null) {
				this.checkDamage2(vehiclePart, 10, true);
				if (vehiclePart.scriptPart.hasLightsRear) {
					this.checkUninstall2(vehiclePart, 12);
					this.checkUninstall2(vehiclePart, 1);
					this.checkUninstall2(vehiclePart, 5);
					this.checkUninstall2(vehiclePart, 9);
				}
			}
		}
	}

	private void doWindowDamage() {
		this.checkDamage(this.getPartById("WindowFrontLeft"), 2, true);
		this.checkDamage(this.getPartById("WindowFrontRight"), 9, true);
		VehiclePart vehiclePart = this.getPartById("WindowRearLeft");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, 6, true);
		} else {
			vehiclePart = this.getPartById("WindowMiddleLeft");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, 6, true);
			}
		}

		vehiclePart = this.getPartById("WindowRearRight");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, 13, true);
		} else {
			vehiclePart = this.getPartById("WindowMiddleRight");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, 13, true);
			}
		}

		this.checkDamage(this.getPartById("Windshield"), 10, true);
		this.checkDamage(this.getPartById("WindshieldRear"), 14, true);
	}

	private void doDoorDamage() {
		this.checkDamage(this.getPartById("DoorFrontLeft"), 1, true);
		this.checkDamage(this.getPartById("DoorFrontRight"), 8, true);
		VehiclePart vehiclePart = this.getPartById("DoorRearLeft");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, 5, true);
		} else {
			vehiclePart = this.getPartById("DoorMiddleLeft");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, 5, true);
			}
		}

		vehiclePart = this.getPartById("DoorRearRight");
		if (vehiclePart != null) {
			this.checkDamage(vehiclePart, 12, true);
		} else {
			vehiclePart = this.getPartById("DoorMiddleRight");
			if (vehiclePart != null) {
				this.checkDamage(vehiclePart, 12, true);
			}
		}
	}

	public float getBloodIntensity(String string) {
		return (float)((Byte)this.bloodIntensity.getOrDefault(string, BYTE_ZERO) & 255) / 100.0F;
	}

	public void setBloodIntensity(String string, float float1) {
		byte byte1 = (byte)((int)(PZMath.clamp(float1, 0.0F, 1.0F) * 100.0F));
		if (!this.bloodIntensity.containsKey(string) || byte1 != (Byte)this.bloodIntensity.get(string)) {
			this.bloodIntensity.put(string, byte1);
			this.doBloodOverlay();
			this.transmitBlood();
		}
	}

	public void transmitBlood() {
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4096);
		}
	}

	public void doBloodOverlay() {
		if (this.sprite.modelSlot != null) {
			VehicleModelInstance vehicleModelInstance = (VehicleModelInstance)this.sprite.modelSlot.model;
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood1Enables2, 0.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables1, 0.0F);
			Arrays.fill(vehicleModelInstance.matrixBlood2Enables2, 0.0F);
			if (Core.getInstance().getOptionBloodDecals() != 0) {
				this.doBloodOverlayFront(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Front"));
				this.doBloodOverlayRear(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Rear"));
				this.doBloodOverlayLeft(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Left"));
				this.doBloodOverlayRight(vehicleModelInstance.matrixBlood1Enables1, vehicleModelInstance.matrixBlood1Enables2, this.getBloodIntensity("Right"));
				Iterator iterator = this.bloodIntensity.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					Integer integer = (Integer)s_PartToMaskMap.get(entry.getKey());
					if (integer != null) {
						vehicleModelInstance.matrixBlood1Enables1[integer] = (float)((Byte)entry.getValue() & 255) / 100.0F;
					}
				}

				this.doBloodOverlayAux(vehicleModelInstance.matrixBlood2Enables1, vehicleModelInstance.matrixBlood2Enables2, 1.0F);
			}
		}
	}

	private void doBloodOverlayAux(float[] floatArray, float[] floatArray2, float float1) {
		floatArray[0] = float1;
		floatArray2[6] = float1;
		floatArray2[4] = float1;
		floatArray2[8] = float1;
		floatArray[4] = float1;
		floatArray[7] = float1;
		floatArray[15] = float1;
		floatArray2[10] = float1;
		floatArray2[12] = float1;
		floatArray2[1] = float1;
		floatArray2[5] = float1;
		floatArray2[9] = float1;
		floatArray[3] = float1;
		floatArray[8] = float1;
		floatArray[12] = float1;
		floatArray[11] = float1;
		floatArray[1] = float1;
		floatArray[5] = float1;
		floatArray2[0] = float1;
		floatArray[10] = float1;
		floatArray[14] = float1;
		floatArray[9] = float1;
		floatArray[13] = float1;
		floatArray[2] = float1;
		floatArray[6] = float1;
	}

	private void doBloodOverlayFront(float[] floatArray, float[] floatArray2, float float1) {
		floatArray[0] = float1;
		floatArray2[6] = float1;
		floatArray2[4] = float1;
		floatArray2[8] = float1;
		floatArray[10] = float1;
	}

	private void doBloodOverlayRear(float[] floatArray, float[] floatArray2, float float1) {
		floatArray[4] = float1;
		floatArray2[10] = float1;
		floatArray2[12] = float1;
		floatArray2[1] = float1;
		floatArray2[5] = float1;
		floatArray2[9] = float1;
		floatArray[14] = float1;
	}

	private void doBloodOverlayLeft(float[] floatArray, float[] floatArray2, float float1) {
		floatArray[11] = float1;
		floatArray[1] = float1;
		floatArray[5] = float1;
		floatArray[15] = float1;
		floatArray[2] = float1;
		floatArray[6] = float1;
	}

	private void doBloodOverlayRight(float[] floatArray, float[] floatArray2, float float1) {
		floatArray[3] = float1;
		floatArray[8] = float1;
		floatArray[12] = float1;
		floatArray[7] = float1;
		floatArray[9] = float1;
		floatArray[13] = float1;
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.script != null) {
			if (this.physics != null) {
				this.physics.debug();
			}

			int int1 = IsoCamera.frameState.playerIndex;
			boolean boolean3 = IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getVehicle() == this;
			if (boolean3 || this.square.lighting[int1].bSeen()) {
				if (!boolean3 && !this.square.lighting[int1].bCouldSee()) {
					this.setTargetAlpha(int1, 0.0F);
				} else {
					this.setTargetAlpha(int1, 1.0F);
				}

				if (this.sprite.hasActiveModel()) {
					this.updateLights();
					boolean boolean4 = Core.getInstance().getOptionBloodDecals() != 0;
					if (this.OptionBloodDecals != boolean4) {
						this.OptionBloodDecals = boolean4;
						this.doBloodOverlay();
					}

					colorInfo.a = this.getAlpha(int1);
					inf.a = colorInfo.a;
					inf.r = colorInfo.r;
					inf.g = colorInfo.g;
					inf.b = colorInfo.b;
					this.sprite.renderVehicle(this.def, this, float1, float2, 0.0F, 0.0F, 0.0F, inf, true);
				}

				this.updateAlpha(int1);
				if (Core.bDebug && DebugOptions.instance.VehicleRenderArea.getValue()) {
					this.renderAreas();
				}

				if (Core.bDebug && DebugOptions.instance.VehicleRenderAttackPositions.getValue()) {
					this.m_surroundVehicle.render();
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

				if (DebugOptions.instance.VehicleRenderTrailerPositions.getValue()) {
					this.renderTrailerPositions();
				}

				this.renderUsableArea();
			}
		}
	}

	public void renderlast() {
		int int1 = IsoCamera.frameState.playerIndex;
		for (int int2 = 0; int2 < this.parts.size(); ++int2) {
			VehiclePart vehiclePart = (VehiclePart)this.parts.get(int2);
			if (vehiclePart.chatElement != null && vehiclePart.chatElement.getHasChatToDisplay()) {
				if (vehiclePart.getDeviceData() != null && !vehiclePart.getDeviceData().getIsTurnedOn()) {
					vehiclePart.chatElement.clear(int1);
				} else {
					float float1 = IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0);
					float float2 = IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0);
					float1 = float1 - IsoCamera.getOffX() - this.offsetX;
					float2 = float2 - IsoCamera.getOffY() - this.offsetY;
					float1 += (float)(32 * Core.TileScale);
					float2 += (float)(20 * Core.TileScale);
					float1 /= Core.getInstance().getZoom(int1);
					float2 /= Core.getInstance().getZoom(int1);
					vehiclePart.chatElement.renderBatched(int1, (int)float1, (int)float2);
				}
			}
		}
	}

	public void renderShadow() {
		if (this.physics != null) {
			if (this.script != null) {
				int int1 = IsoCamera.frameState.playerIndex;
				if (this.square.lighting[int1].bSeen()) {
					if (this.square.lighting[int1].bCouldSee()) {
						this.setTargetAlpha(int1, 1.0F);
					} else {
						this.setTargetAlpha(int1, 0.0F);
					}

					Texture texture = this.getShadowTexture();
					if (texture != null && this.getCurrentSquare() != null) {
						float float1 = 0.6F * this.getAlpha(int1);
						ColorInfo colorInfo = this.getCurrentSquare().lighting[int1].lightInfo();
						float1 *= (colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F;
						if (this.polyDirty) {
							this.getPoly();
						}

						SpriteRenderer.instance.renderPoly(texture, (float)((int)IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0)), (float)((int)IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0)), (float)((int)IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0)), (float)((int)IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0)), (float)((int)IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0)), (float)((int)IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0)), (float)((int)IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0)), (float)((int)IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0)), 1.0F, 1.0F, 1.0F, 0.8F * float1);
					}
				}
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
			Vector3f vector3f = this.getPassengerPositionWorldPos(position2, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			if (position2.area != null) {
				Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
				VehicleScript.Area area = this.script.getAreaById(position2.area);
				Vector2 vector22 = this.areaPositionWorld4PlayerInteract(area, vector2);
				if (vector22 != null) {
					vector3f.x = vector22.x;
					vector3f.y = vector22.y;
				}

				((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
			}

			vector3f.z = 0.0F;
			Vector3f vector3f2 = this.getPassengerPositionWorldPos(position, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			boolean boolean1 = PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, vector3f.x, vector3f.y, (int)this.z, this, false, false);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
			return boolean1;
		} else {
			return true;
		}
	}

	public boolean isPassengerUseDoor2(IsoGameCharacter gameCharacter, int int1) {
		VehicleScript.Position position = this.getPassengerPosition(int1, "outside2");
		if (position != null) {
			Vector3f vector3f = this.getPassengerPositionWorldPos(position, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			vector3f.sub(gameCharacter.x, gameCharacter.y, gameCharacter.z);
			float float1 = vector3f.length();
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			if (float1 < 2.0F) {
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
			Vector3f vector3f = this.getPassengerPositionWorldPos(position2, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			vector3f.z = 0.0F;
			Vector3f vector3f2 = this.getPassengerPositionWorldPos(position, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			boolean boolean1 = PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, vector3f.x, vector3f.y, (int)this.z, this, false, false);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
			return boolean1;
		} else {
			return true;
		}
	}

	private void renderExits() {
		int int1 = Core.TileScale;
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		for (int int2 = 0; int2 < this.getMaxPassengers(); ++int2) {
			VehicleScript.Position position = this.getPassengerPosition(int2, "inside");
			VehicleScript.Position position2 = this.getPassengerPosition(int2, "outside");
			if (position != null && position2 != null) {
				float float1 = 0.3F;
				this.getPassengerPositionWorldPos(position2, vector3f);
				this.getPassengerPositionWorldPos(position, vector3f2);
				int int3 = (int)Math.floor((double)(vector3f.x - float1));
				int int4 = (int)Math.floor((double)(vector3f.x + float1));
				int int5 = (int)Math.floor((double)(vector3f.y - float1));
				int int6 = (int)Math.floor((double)(vector3f.y + float1));
				for (int int7 = int5; int7 <= int6; ++int7) {
					for (int int8 = int3; int8 <= int4; ++int8) {
						int int9 = (int)IsoUtils.XToScreenExact((float)int8, (float)(int7 + 1), (float)((int)this.z), 0);
						int int10 = (int)IsoUtils.YToScreenExact((float)int8, (float)(int7 + 1), (float)((int)this.z), 0);
						SpriteRenderer.instance.renderPoly((float)int9, (float)int10, (float)(int9 + 32 * int1), (float)(int10 - 16 * int1), (float)(int9 + 64 * int1), (float)int10, (float)(int9 + 32 * int1), (float)(int10 + 16 * int1), 1.0F, 1.0F, 1.0F, 0.5F);
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

		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
	}

	private Vector2 areaPositionLocal(VehicleScript.Area area) {
		return this.areaPositionLocal(area, new Vector2());
	}

	private Vector2 areaPositionLocal(VehicleScript.Area area, Vector2 vector2) {
		Vector2 vector22 = this.areaPositionWorld(area, vector2);
		Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		this.getLocalPos(vector22.x, vector22.y, 0.0F, vector3f);
		vector22.set(vector3f.x, vector3f.z);
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		return vector22;
	}

	public Vector2 areaPositionWorld(VehicleScript.Area area) {
		return this.areaPositionWorld(area, new Vector2());
	}

	public Vector2 areaPositionWorld(VehicleScript.Area area, Vector2 vector2) {
		if (area == null) {
			return null;
		} else {
			Vector3f vector3f = this.getWorldPos(area.x, 0.0F, area.y, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			vector2.set(vector3f.x, vector3f.y);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			return vector2;
		}
	}

	public Vector2 areaPositionWorld4PlayerInteract(VehicleScript.Area area) {
		return this.areaPositionWorld4PlayerInteract(area, new Vector2());
	}

	public Vector2 areaPositionWorld4PlayerInteract(VehicleScript.Area area, Vector2 vector2) {
		Vector3f vector3f = this.script.getExtents();
		Vector3f vector3f2 = this.script.getCenterOfMassOffset();
		Vector2 vector22 = this.areaPositionWorld(area, vector2);
		Vector3f vector3f3 = this.getLocalPos(vector22.x, vector22.y, 0.0F, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		if (!(area.x > vector3f2.x + vector3f.x / 2.0F) && !(area.x < vector3f2.x - vector3f.x / 2.0F)) {
			if (area.y > 0.0F) {
				vector3f3.z -= area.h * 0.3F;
			} else {
				vector3f3.z += area.h * 0.3F;
			}
		} else if (area.x > 0.0F) {
			vector3f3.x -= area.w * 0.3F;
		} else {
			vector3f3.x += area.w * 0.3F;
		}

		this.getWorldPos(vector3f3, vector3f3);
		vector2.set(vector3f3.x, vector3f3.y);
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
		return vector2;
	}

	private void renderAreas() {
		if (this.getScript() != null) {
			Vector3f vector3f = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
			for (int int1 = 0; int1 < this.parts.size(); ++int1) {
				VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
				if (vehiclePart.getArea() != null) {
					VehicleScript.Area area = this.getScript().getAreaById(vehiclePart.getArea());
					if (area != null) {
						Vector2 vector22 = this.areaPositionWorld(area, vector2);
						if (vector22 != null) {
							boolean boolean1 = this.isInArea(area.id, IsoPlayer.getInstance());
							this.getController().drawRect(vector3f, vector22.x - WorldSimulation.instance.offsetX, vector22.y - WorldSimulation.instance.offsetY, area.w, area.h / 2.0F, boolean1 ? 0.0F : 0.65F, boolean1 ? 1.0F : 0.65F, boolean1 ? 1.0F : 0.65F);
							vector22 = this.areaPositionWorld4PlayerInteract(area, vector2);
							this.getController().drawRect(vector3f, vector22.x - WorldSimulation.instance.offsetX, vector22.y - WorldSimulation.instance.offsetY, 0.1F, 0.1F, 1.0F, 0.0F, 0.0F);
						}
					}
				}
			}

			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
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

	private void renderUsableArea() {
		if (this.getScript() != null && UIManager.VisibleAllUI) {
			VehiclePart vehiclePart = this.getUseablePart(IsoPlayer.getInstance());
			if (vehiclePart != null) {
				VehicleScript.Area area = this.getScript().getAreaById(vehiclePart.getArea());
				if (area != null) {
					Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
					Vector2 vector22 = this.areaPositionWorld(area, vector2);
					if (vector22 == null) {
						((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
					} else {
						Vector3f vector3f = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
						float float1 = 0.0F;
						float float2 = 1.0F;
						float float3 = 0.0F;
						this.getController().drawRect(vector3f, vector22.x - WorldSimulation.instance.offsetX, vector22.y - WorldSimulation.instance.offsetY, area.w, area.h / 2.0F, float1, float2, float3);
						vector3f.x *= area.h / this.script.getModelScale();
						vector3f.z *= area.h / this.script.getModelScale();
						if (vehiclePart.getDoor() != null && (vehiclePart.getId().contains("Left") || vehiclePart.getId().contains("Right"))) {
							if (vehiclePart.getId().contains("Front")) {
								this.getController().drawRect(vector3f, vector22.x - WorldSimulation.instance.offsetX + vector3f.x * area.h / 2.0F, vector22.y - WorldSimulation.instance.offsetY + vector3f.z * area.h / 2.0F, area.w, area.h / 8.0F, float1, float2, float3);
							} else if (vehiclePart.getId().contains("Rear")) {
								this.getController().drawRect(vector3f, vector22.x - WorldSimulation.instance.offsetX - vector3f.x * area.h / 2.0F, vector22.y - WorldSimulation.instance.offsetY - vector3f.z * area.h / 2.0F, area.w, area.h / 8.0F, float1, float2, float3);
							}
						}

						((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
						((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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

	private void renderTrailerPositions() {
		if (this.script != null && this.physics != null) {
			Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			Vector3f vector3f3 = this.getTowingWorldPos("trailer", vector3f2);
			if (vector3f3 != null) {
				this.physics.drawCircle(vector3f3.x, vector3f3.y, 0.3F, 1.0F, 1.0F, 1.0F, 1.0F);
			}

			Vector3f vector3f4 = this.getPlayerTrailerLocalPos("trailer", false, vector3f);
			boolean boolean1;
			if (vector3f4 != null) {
				this.getWorldPos(vector3f4, vector3f4);
				boolean1 = PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, vector3f4.x, vector3f4.y, (int)this.z, this, false, false);
				this.physics.drawCircle(vector3f4.x, vector3f4.y, 0.3F, 1.0F, 1.0F, 1.0F, 1.0F);
				if (boolean1) {
					LineDrawer.addLine(vector3f4.x, vector3f4.y, 0.0F, vector3f2.x, vector3f2.y, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
				}
			}

			vector3f4 = this.getPlayerTrailerLocalPos("trailer", true, vector3f);
			if (vector3f4 != null) {
				this.getWorldPos(vector3f4, vector3f4);
				boolean1 = PolygonalMap2.instance.lineClearCollide(vector3f2.x, vector3f2.y, vector3f4.x, vector3f4.y, (int)this.z, this, false, false);
				this.physics.drawCircle(vector3f4.x, vector3f4.y, 0.3F, 1.0F, boolean1 ? 0.0F : 1.0F, boolean1 ? 0.0F : 1.0F, 1.0F);
				if (boolean1) {
					LineDrawer.addLine(vector3f4.x, vector3f4.y, 0.0F, vector3f2.x, vector3f2.y, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
				}
			}

			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
		}
	}

	public void getWheelForwardVector(int int1, Vector3f vector3f) {
		BaseVehicle.WheelInfo wheelInfo = this.wheelInfo[int1];
		Matrix4f matrix4f = (Matrix4f)((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).alloc();
		matrix4f.rotationY(wheelInfo.steering);
		Matrix4f matrix4f2 = this.jniTransform.getMatrix((Matrix4f)((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).alloc());
		matrix4f2.setTranslation(0.0F, 0.0F, 0.0F);
		matrix4f.mul((Matrix4fc)matrix4f2, matrix4f);
		((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).release(matrix4f2);
		((BaseVehicle.Matrix4fObjectPool)TL_matrix4f_pool.get()).release(matrix4f);
		matrix4f.getColumn(2, (Vector4f)this.tempVector4f);
		vector3f.set(this.tempVector4f.x, 0.0F, this.tempVector4f.z);
	}

	public void tryStartEngine(boolean boolean1) {
		if (this.getDriver() == null || !(this.getDriver() instanceof IsoPlayer) || !((IsoPlayer)this.getDriver()).isBlockMovement()) {
			if (this.engineState == BaseVehicle.engineStateTypes.Idle) {
				if ((!Core.bDebug || !DebugOptions.instance.CheatVehicleStartWithoutKey.getValue()) && !SandboxOptions.instance.VehicleEasyUse.getValue() && !this.isKeysInIgnition() && !boolean1 && this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) == null && !this.isHotwired()) {
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
		this.transmitEngine();
	}

	public void engineDoStarting() {
		this.engineState = BaseVehicle.engineStateTypes.Starting;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.transmitEngine();
		this.setKeysInIgnition(true);
	}

	public boolean isStarting() {
		return this.engineState == BaseVehicle.engineStateTypes.Starting || this.engineState == BaseVehicle.engineStateTypes.StartingFailed || this.engineState == BaseVehicle.engineStateTypes.StartingSuccess || this.engineState == BaseVehicle.engineStateTypes.StartingFailedNoPower;
	}

	private String getEngineSound() {
		return this.getScript() != null && this.getScript().getSounds().engine != null ? this.getScript().getSounds().engine : "VehicleEngineDefault";
	}

	private String getEngineStartSound() {
		return this.getScript() != null && this.getScript().getSounds().engineStart != null ? this.getScript().getSounds().engineStart : "VehicleStarted";
	}

	private String getEngineTurnOffSound() {
		return this.getScript() != null && this.getScript().getSounds().engineTurnOff != null ? this.getScript().getSounds().engineTurnOff : "VehicleTurnedOff";
	}

	private String getIgnitionFailSound() {
		return this.getScript() != null && this.getScript().getSounds().ignitionFail != null ? this.getScript().getSounds().ignitionFail : "VehicleFailingToStart";
	}

	private String getIgnitionFailNoPowerSound() {
		return this.getScript() != null && this.getScript().getSounds().ignitionFailNoPower != null ? this.getScript().getSounds().ignitionFailNoPower : "VehicleFailingToStartNoPower";
	}

	public void engineDoRetryingStarting() {
		this.getEmitter().stopSoundByName(this.getIgnitionFailSound());
		this.getEmitter().playSoundImpl(this.getIgnitionFailSound(), (IsoObject)null);
		this.engineState = BaseVehicle.engineStateTypes.RetryingStarting;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.transmitEngine();
	}

	public void engineDoStartingSuccess() {
		this.getEmitter().stopSoundByName(this.getIgnitionFailSound());
		this.engineState = BaseVehicle.engineStateTypes.StartingSuccess;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		if (this.getEngineStartSound().equals(this.getEngineSound())) {
			if (!this.getEmitter().isPlaying(this.combinedEngineSound)) {
				this.combinedEngineSound = this.emitter.playSoundImpl(this.getEngineSound(), (IsoObject)null);
			}
		} else {
			this.getEmitter().playSoundImpl(this.getEngineStartSound(), (IsoObject)null);
		}

		this.transmitEngine();
		this.setKeysInIgnition(true);
	}

	public void engineDoStartingFailed() {
		this.getEmitter().stopSoundByName(this.getIgnitionFailSound());
		this.getEmitter().playSoundImpl(this.getIgnitionFailSound(), (IsoObject)null);
		this.stopEngineSounds();
		this.engineState = BaseVehicle.engineStateTypes.StartingFailed;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.transmitEngine();
	}

	public void engineDoStartingFailedNoPower() {
		this.getEmitter().stopSoundByName(this.getIgnitionFailNoPowerSound());
		this.getEmitter().playSoundImpl(this.getIgnitionFailNoPowerSound(), (IsoObject)null);
		this.stopEngineSounds();
		this.engineState = BaseVehicle.engineStateTypes.StartingFailedNoPower;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.transmitEngine();
	}

	public void engineDoRunning() {
		this.setNeedPartsUpdate(true);
		this.engineState = BaseVehicle.engineStateTypes.Running;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.transmitEngine();
	}

	public void engineDoStalling() {
		this.getEmitter().playSoundImpl("VehicleRunningOutOfGas", (IsoObject)null);
		this.engineState = BaseVehicle.engineStateTypes.Stalling;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.stopEngineSounds();
		this.engineSoundIndex = 0;
		this.transmitEngine();
		if (!Core.getInstance().getOptionLeaveKeyInIgnition()) {
			this.setKeysInIgnition(false);
		}
	}

	public void engineDoShuttingDown() {
		if (!this.getEngineTurnOffSound().equals(this.getEngineSound())) {
			this.getEmitter().playSoundImpl(this.getEngineTurnOffSound(), (IsoObject)null);
		}

		this.stopEngineSounds();
		this.engineSoundIndex = 0;
		this.engineState = BaseVehicle.engineStateTypes.ShutingDown;
		this.engineLastUpdateStateTime = System.currentTimeMillis();
		this.transmitEngine();
		if (!Core.getInstance().getOptionLeaveKeyInIgnition()) {
			this.setKeysInIgnition(false);
		}

		VehiclePart vehiclePart = this.getHeater();
		if (vehiclePart != null) {
			vehiclePart.getModData().rawset("active", false);
		}
	}

	public void shutOff() {
		if (this.getPartById("GasTank").getContainerContentAmount() == 0.0F) {
			this.engineDoStalling();
		} else {
			this.engineDoShuttingDown();
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
			if (!Core.SoundDisabled && !GameServer.bServer) {
				FMODSoundEmitter fMODSoundEmitter = new FMODSoundEmitter();
				fMODSoundEmitter.parameterUpdater = this;
				this.emitter = fMODSoundEmitter;
			} else {
				this.emitter = new DummySoundEmitter();
			}
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
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player2 = IsoPlayer.players[int1];
			if (player2 != null && player2.getCurrentSquare() != null) {
				float float2 = player2.getX();
				float float3 = player2.getY();
				float float4 = IsoUtils.DistanceToSquared(float2, float3, this.x, this.y);
				if (player2.Traits.HardOfHearing.isSet()) {
					float4 *= 4.5F;
				}

				if (player2.Traits.Deaf.isSet()) {
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
			if (!GameServer.bServer) {
				if (!this.getEmitter().isPlaying("VehicleAmbiance")) {
					this.emitter.playAmbientLoopedImpl("VehicleAmbiance");
				}

				float float5 = float1;
				if (float1 > 1200.0F) {
					this.stopEngineSounds();
					if (this.emitter != null && !this.emitter.isEmpty()) {
						this.emitter.setPos(this.x, this.y, this.z);
						this.emitter.tick();
					}

					return;
				}

				for (int int2 = 0; int2 < this.new_EngineSoundId.length; ++int2) {
					if (this.new_EngineSoundId[int2] != 0L) {
						this.getEmitter().setVolume(this.new_EngineSoundId[int2], 1.0F - float5 / 1200.0F);
					}
				}
			}

			this.startTime -= GameTime.instance.getMultiplier();
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
					if (this.startTime <= 0.0F && this.engineState == BaseVehicle.engineStateTypes.Running && !this.getEmitter().isPlaying(this.combinedEngineSound)) {
						this.combinedEngineSound = this.emitter.playSoundImpl(this.getEngineSound(), (IsoObject)null);
						if (this.getEngineSound().equals(this.getEngineStartSound())) {
							this.emitter.setTimelinePosition(this.combinedEngineSound, "idle");
						}
					}

					boolean boolean2 = false;
					if (!GameClient.bClient || this.isLocalPhysicSim()) {
						for (int int3 = 0; int3 < this.script.getWheelCount(); ++int3) {
							if (this.wheelInfo[int3].skidInfo < 0.15F) {
								boolean2 = true;
								break;
							}
						}
					}

					if (this.getDriver() == null) {
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
						this.getFMODParameters().update();
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

	public boolean getHeadlightsOn() {
		return this.headlightsOn;
	}

	public void setHeadlightsOn(boolean boolean1) {
		if (this.headlightsOn != boolean1) {
			this.headlightsOn = boolean1;
			if (GameServer.bServer) {
				this.updateFlags = (short)(this.updateFlags | 8);
			}
		}
	}

	public boolean getWindowLightsOn() {
		return this.windowLightsOn;
	}

	public void setWindowLightsOn(boolean boolean1) {
		this.windowLightsOn = boolean1;
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

	public boolean getStoplightsOn() {
		return this.stoplightsOn;
	}

	public void setStoplightsOn(boolean boolean1) {
		if (this.stoplightsOn != boolean1) {
			this.stoplightsOn = boolean1;
			if (GameServer.bServer) {
				this.updateFlags = (short)(this.updateFlags | 8);
			}
		}
	}

	public boolean hasHeadlights() {
		return this.getLightCount() > 0;
	}

	public void addToWorld() {
		if (this.addedToWorld) {
			DebugLog.General.error("added vehicle twice " + this + " id=" + this.VehicleID);
		} else {
			VehiclesDB2.instance.setVehicleLoaded(this);
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
				this.engineSpeed = this.getScript() == null ? 1000.0 : (double)this.getScript().getEngineIdleSpeed();
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
		this.breakConstraint(false, false);
		VehiclesDB2.instance.setVehicleUnloaded(this);
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

			this.releaseAnimationPlayers();
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

			this.m_surroundVehicle.reset();
			this.removeWorldLights();
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

		this.breakConstraint(true, false);
		this.removeFromWorld();
		this.removeFromSquare();
		if (this.chunk != null) {
			this.chunk.vehicles.remove(this);
		}

		VehiclesDB2.instance.removeVehicle(this);
	}

	public VehiclePart getBattery() {
		return this.battery;
	}

	public void setEngineFeature(int int1, int int2, int int3) {
		this.engineQuality = PZMath.clamp(int1, 0, 100);
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

	public String getZone() {
		return this.respawnZone;
	}

	public void setZone(String string) {
		this.respawnZone = string;
	}

	public boolean isInArea(String string, IsoGameCharacter gameCharacter) {
		if (string != null && this.getScript() != null) {
			VehicleScript.Area area = this.getScript().getAreaById(string);
			if (area == null) {
				return false;
			} else {
				Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).alloc();
				Vector2 vector22 = this.areaPositionLocal(area, vector2);
				if (vector22 == null) {
					((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
					return false;
				} else {
					Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
					this.getLocalPos(gameCharacter.x, gameCharacter.y, this.z, vector3f);
					float float1 = vector22.x - area.w / 2.0F;
					float float2 = vector22.y - area.h / 2.0F;
					float float3 = vector22.x + area.w / 2.0F;
					float float4 = vector22.y + area.h / 2.0F;
					((BaseVehicle.Vector2ObjectPool)TL_vector2_pool.get()).release(vector2);
					boolean boolean1 = vector3f.x >= float1 && vector3f.x < float3 && vector3f.z >= float2 && vector3f.z < float4;
					((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
					return boolean1;
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
				Vector3f vector3f = this.getLocalPos(gameCharacter.x, gameCharacter.y, this.z, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
				float float1 = Math.abs(area.x - area.w / 2.0F);
				float float2 = Math.abs(area.y - area.h / 2.0F);
				float float3 = Math.abs(area.x + area.w / 2.0F);
				float float4 = Math.abs(area.y + area.h / 2.0F);
				float float5 = Math.abs(vector3f.x + float1) + Math.abs(vector3f.z + float2);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				return float5;
			} else {
				return 999.0F;
			}
		} else {
			return 999.0F;
		}
	}

	public Vector2 getAreaCenter(String string) {
		return this.getAreaCenter(string, new Vector2());
	}

	public Vector2 getAreaCenter(String string, Vector2 vector2) {
		if (string != null && this.getScript() != null) {
			VehicleScript.Area area = this.getScript().getAreaById(string);
			return area == null ? null : this.areaPositionWorld(area, vector2);
		} else {
			return null;
		}
	}

	public boolean isInBounds(float float1, float float2) {
		return this.getPoly().containsPoint(float1, float2);
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

	private void callLuaVoid(String string, Object object, Object object2) {
		Object object3 = LuaManager.getFunctionObject(string);
		if (object3 != null) {
			LuaManager.caller.protectedCallVoid(LuaManager.thread, object3, object, object2);
		}
	}

	private void callLuaVoid(String string, Object object, Object object2, Object object3) {
		Object object4 = LuaManager.getFunctionObject(string);
		if (object4 != null) {
			LuaManager.caller.protectedCallVoid(LuaManager.thread, object4, object, object2, object3);
		}
	}

	private Boolean callLuaBoolean(String string, Object object, Object object2) {
		Object object3 = LuaManager.getFunctionObject(string);
		return object3 == null ? null : LuaManager.caller.protectedCallBoolean(LuaManager.thread, object3, object, object2);
	}

	private Boolean callLuaBoolean(String string, Object object, Object object2, Object object3) {
		Object object4 = LuaManager.getFunctionObject(string);
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

	public Vector3f chooseBestAttackPosition(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, Vector3f vector3f) {
		Vector2f vector2f = (Vector2f)((BaseVehicle.Vector2fObjectPool)TL_vector2f_pool.get()).alloc();
		Vector2f vector2f2 = gameCharacter.getVehicle().getSurroundVehicle().getPositionForZombie((IsoZombie)gameCharacter2, vector2f);
		float float1 = vector2f.x;
		float float2 = vector2f.y;
		((BaseVehicle.Vector2fObjectPool)TL_vector2f_pool.get()).release(vector2f);
		return vector2f2 != null ? vector3f.set(float1, float2, this.z) : null;
	}

	public BaseVehicle.MinMaxPosition getMinMaxPosition() {
		BaseVehicle.MinMaxPosition minMaxPosition = new BaseVehicle.MinMaxPosition();
		float float1 = this.getX();
		float float2 = this.getY();
		Vector3f vector3f = this.getScript().getExtents();
		float float3 = vector3f.x;
		float float4 = vector3f.z;
		IsoDirections directions = this.getDir();
		switch (directions) {
		case E: 
		
		case W: 
			minMaxPosition.minX = float1 - float3 / 2.0F;
			minMaxPosition.maxX = float1 + float3 / 2.0F;
			minMaxPosition.minY = float2 - float4 / 2.0F;
			minMaxPosition.maxY = float2 + float4 / 2.0F;
			break;
		
		case N: 
		
		case S: 
			minMaxPosition.minX = float1 - float4 / 2.0F;
			minMaxPosition.maxX = float1 + float4 / 2.0F;
			minMaxPosition.minY = float2 - float3 / 2.0F;
			minMaxPosition.maxY = float2 + float3 / 2.0F;
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
		String string = null;
		if (Rand.Next(100) <= int4) {
			this.setHotwired(true);
			boolean1 = true;
			string = "VehicleHotwireSuccess";
		} else if (Rand.Next(100) <= 10 - int1) {
			this.setHotwiredBroken(true);
			boolean1 = true;
			string = "VehicleHotwireFail";
		} else {
			string = "VehicleHotwireFail";
		}

		if (string != null) {
			if (GameServer.bServer) {
				LuaManager.GlobalObject.playServerSound(string, this.square);
			} else if (this.getDriver() != null) {
				this.getDriver().getEmitter().playSound(string);
			}
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
							InventoryItem inventoryItem3 = itemContainer.getItemWithID(Double1.intValue());
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

	private void randomizeContainers() {
		if (!GameClient.bClient) {
			boolean boolean1 = true;
			String string = this.getScriptName().substring(this.getScriptName().indexOf(46) + 1);
			ItemPickerJava.VehicleDistribution vehicleDistribution = (ItemPickerJava.VehicleDistribution)ItemPickerJava.VehicleDistributions.get(string + this.getSkinIndex());
			if (vehicleDistribution != null) {
				boolean1 = false;
			} else {
				vehicleDistribution = (ItemPickerJava.VehicleDistribution)ItemPickerJava.VehicleDistributions.get(string);
			}

			if (vehicleDistribution == null) {
				for (int int1 = 0; int1 < this.parts.size(); ++int1) {
					VehiclePart vehiclePart = (VehiclePart)this.parts.get(int1);
					if (vehiclePart.getItemContainer() != null) {
						DebugLog.log("VEHICLE MISSING CONT DISTRIBUTION: " + string);
						return;
					}
				}
			} else {
				ItemPickerJava.ItemPickerRoom itemPickerRoom;
				if (boolean1 && Rand.Next(100) <= 8 && !vehicleDistribution.Specific.isEmpty()) {
					itemPickerRoom = (ItemPickerJava.ItemPickerRoom)PZArrayUtil.pickRandom((List)vehicleDistribution.Specific);
				} else {
					itemPickerRoom = vehicleDistribution.Normal;
				}

				int int2;
				if (!StringUtils.isNullOrWhitespace(this.specificDistributionId)) {
					for (int2 = 0; int2 < vehicleDistribution.Specific.size(); ++int2) {
						ItemPickerJava.ItemPickerRoom itemPickerRoom2 = (ItemPickerJava.ItemPickerRoom)vehicleDistribution.Specific.get(int2);
						if (this.specificDistributionId.equals(itemPickerRoom2.specificId)) {
							itemPickerRoom = itemPickerRoom2;
							break;
						}
					}
				}

				for (int2 = 0; int2 < this.parts.size(); ++int2) {
					VehiclePart vehiclePart2 = (VehiclePart)this.parts.get(int2);
					if (vehiclePart2.getItemContainer() != null && !vehiclePart2.getItemContainer().bExplored) {
						vehiclePart2.getItemContainer().clear();
						if (Rand.Next(100) <= 100) {
							this.randomizeContainer(vehiclePart2, itemPickerRoom);
						}

						vehiclePart2.getItemContainer().setExplored(true);
					}
				}
			}
		}
	}

	private void randomizeContainer(VehiclePart vehiclePart, ItemPickerJava.ItemPickerRoom itemPickerRoom) {
		if (!GameClient.bClient) {
			if (itemPickerRoom != null) {
				if (!vehiclePart.getId().contains("Seat") && !itemPickerRoom.Containers.containsKey(vehiclePart.getId())) {
					String string = vehiclePart.getId();
					DebugLog.log("NO CONT DISTRIB FOR PART: " + string + " CAR: " + this.getScriptName().replaceFirst("Base.", ""));
				}

				ItemPickerJava.fillContainerType(itemPickerRoom, vehiclePart.getItemContainer(), "", (IsoGameCharacter)null);
				if (GameServer.bServer && !vehiclePart.getItemContainer().getItems().isEmpty()) {
				}
			}
		}
	}

	public boolean hasHorn() {
		return this.script.getSounds().hornEnable;
	}

	public boolean hasLightbar() {
		VehiclePart vehiclePart = this.getPartById("lightbar");
		return vehiclePart != null && vehiclePart.getCondition() > 0;
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

	public void setLightbarLightsMode(int int1) {
		this.lightbarLightsMode.set(int1);
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 1024);
		}
	}

	public int getLightbarSirenMode() {
		return this.lightbarSirenMode.get();
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
		if (this.physics != null) {
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
		if (this.getCurrentSquare() == null) {
			return false;
		} else {
			IsoObject object = this.getCurrentSquare().getFloor();
			if (object != null && object.getSprite() != null) {
				String string = object.getSprite().getName();
				if (string == null) {
					return false;
				} else {
					return !string.contains("carpentry_02") && !string.contains("blends_street") && !string.contains("floors_exterior_street");
				}
			} else {
				return false;
			}
		}
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

	public void transmitEngine() {
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4);
		}
	}

	public void setRust(float float1) {
		this.rust = PZMath.clamp(float1, 0.0F, 1.0F);
	}

	public float getRust() {
		return this.rust;
	}

	public void transmitRust() {
		if (GameServer.bServer) {
			this.updateFlags = (short)(this.updateFlags | 4096);
		}
	}

	public void updateBulletStats() {
		if (!this.getScriptName().contains("Burnt") && WorldSimulation.instance.created) {
			float[] floatArray = vehicleParams;
			double double1 = 2.4;
			byte byte1 = 5;
			double double2;
			float float1;
			if (this.isInForest() && this.isDoingOffroad() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0F) {
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

			Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			for (int int1 = 0; int1 < this.script.getWheelCount(); ++int1) {
				this.updateBulletStatsWheel(int1, floatArray, vector3f, float1, byte1, double1, double2);
			}

			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			if (SystemDisabler.getdoVehicleLowRider() && this.isKeyboardControlled()) {
				float float2 = 1.6F;
				float float3 = 1.0F;
				float[] floatArray2;
				if (GameKeyboard.isKeyDown(79)) {
					floatArray2 = lowRiderParam;
					floatArray2[0] += (float2 - lowRiderParam[0]) * float3;
				} else {
					floatArray2 = lowRiderParam;
					floatArray2[0] += (0.0F - lowRiderParam[0]) * 0.05F;
				}

				if (GameKeyboard.isKeyDown(80)) {
					floatArray2 = lowRiderParam;
					floatArray2[1] += (float2 - lowRiderParam[1]) * float3;
				} else {
					floatArray2 = lowRiderParam;
					floatArray2[1] += (0.0F - lowRiderParam[1]) * 0.05F;
				}

				if (GameKeyboard.isKeyDown(75)) {
					floatArray2 = lowRiderParam;
					floatArray2[2] += (float2 - lowRiderParam[2]) * float3;
				} else {
					floatArray2 = lowRiderParam;
					floatArray2[2] += (0.0F - lowRiderParam[2]) * 0.05F;
				}

				if (GameKeyboard.isKeyDown(76)) {
					floatArray2 = lowRiderParam;
					floatArray2[3] += (float2 - lowRiderParam[3]) * float3;
				} else {
					floatArray2 = lowRiderParam;
					floatArray2[3] += (0.0F - lowRiderParam[3]) * 0.05F;
				}

				floatArray[23] = lowRiderParam[0];
				floatArray[22] = lowRiderParam[1];
				floatArray[21] = lowRiderParam[2];
				floatArray[20] = lowRiderParam[3];
			}

			Bullet.setVehicleParams(this.VehicleID, floatArray);
		}
	}

	private void updateBulletStatsWheel(int int1, float[] floatArray, Vector3f vector3f, float float1, int int2, double double1, double double2) {
		int int3 = int1 * 6;
		VehicleScript.Wheel wheel = this.script.getWheel(int1);
		Vector3f vector3f2 = this.getWorldPos(wheel.offset.x, wheel.offset.y, wheel.offset.z, vector3f);
		VehiclePart vehiclePart = this.getPartById("Tire" + wheel.getId());
		VehiclePart vehiclePart2 = this.getPartById("Suspension" + wheel.getId());
		if (vehiclePart != null && vehiclePart.getInventoryItem() != null) {
			floatArray[int3 + 0] = 1.0F;
			floatArray[int3 + 1] = Math.min(vehiclePart.getContainerContentAmount() / (float)(vehiclePart.getContainerCapacity() - 10), 1.0F);
			floatArray[int3 + 2] = float1 * vehiclePart.getWheelFriction();
			if (vehiclePart2 != null && vehiclePart2.getInventoryItem() != null) {
				floatArray[int3 + 3] = vehiclePart2.getSuspensionDamping();
				floatArray[int3 + 4] = vehiclePart2.getSuspensionCompression();
			} else {
				floatArray[int3 + 3] = 0.1F;
				floatArray[int3 + 4] = 0.1F;
			}

			if (Rand.Next(int2) == 0) {
				floatArray[int3 + 5] = (float)(Math.sin(double1 * (double)vector3f2.x()) * Math.sin(double1 * (double)vector3f2.y()) * double2);
			} else {
				floatArray[int3 + 5] = 0.0F;
			}
		} else {
			floatArray[int3 + 0] = 0.0F;
			floatArray[int3 + 1] = 30.0F;
			floatArray[int3 + 2] = 0.0F;
			floatArray[int3 + 3] = 2.88F;
			floatArray[int3 + 4] = 3.83F;
			if (Rand.Next(int2) == 0) {
				floatArray[int3 + 5] = (float)(Math.sin(double1 * (double)vector3f2.x()) * Math.sin(double1 * (double)vector3f2.y()) * double2);
			} else {
				floatArray[int3 + 5] = 0.0F;
			}
		}

		if (this.forcedFriction > -1.0F) {
			floatArray[int3 + 2] = this.forcedFriction;
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

	public boolean isAnyDoorLocked() {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			VehiclePart vehiclePart = this.getPassengerDoor(int1);
			if (vehiclePart != null && vehiclePart.getDoor() != null && vehiclePart.getDoor().isLocked()) {
				return true;
			}
		}

		return false;
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
			if (gameCharacter.Traits.FastHealer.isSet()) {
				float2 = (float)((double)float2 * 0.8);
			} else if (gameCharacter.Traits.SlowHealer.isSet()) {
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
		if (gameCharacter.isCrit) {
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
			Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			this.getLocalPos(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ(), vector3f);
			boolean boolean1 = vector3f.x > 0.0F;
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
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

	public double getSirenStartTime() {
		return this.sirenStartTime;
	}

	public void setSirenStartTime(double double1) {
		this.sirenStartTime = double1;
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

		this.rust = 0.0F;
		this.transmitRust();
		this.bloodIntensity.clear();
		this.transmitBlood();
		this.doBloodOverlay();
	}

	public boolean isAnyListenerInside() {
		for (int int1 = 0; int1 < this.getMaxPassengers(); ++int1) {
			IsoGameCharacter gameCharacter = this.getCharacter(int1);
			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer() && !gameCharacter.Traits.Deaf.isSet()) {
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
		Vector3f vector3f = this.getLinearVelocity((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
		vector3f.y = 0.0F;
		Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
		vector3f2.set(this.x - object.getX(), 0.0F, this.z - object.getY());
		vector3f2.normalize();
		vector3f.mul((Vector3fc)vector3f2);
		((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
		float float2 = vector3f.length();
		float2 = Math.min(float2, float1);
		if (float2 < 0.05F) {
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
		} else {
			if (GameServer.bServer) {
				if (object instanceof IsoZombie) {
					((IsoZombie)object).setThumpFlag(1);
				}
			} else {
				SoundManager.instance.PlayWorldSound("ZombieThumpGeneric", object.square, 0.0F, 20.0F, 0.9F, true);
			}

			Vector3f vector3f3 = (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc();
			vector3f3.set(this.x - object.getX(), 0.0F, this.y - object.getY());
			vector3f3.normalize();
			vector3f.normalize();
			float float3 = vector3f.dot(vector3f3);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f3);
			this.ApplyImpulse(object, this.getFudgedMass() * 3.0F * float2 / float1 * Math.abs(float3));
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

	public void setColorHSV(float float1, float float2, float float3) {
		this.colorHue = float1;
		this.colorSaturation = float2;
		this.colorValue = float3;
	}

	public float getColorHue() {
		return this.colorHue;
	}

	public float getColorSaturation() {
		return this.colorSaturation;
	}

	public float getColorValue() {
		return this.colorValue;
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

	public AnimationPlayer getAnimationPlayer() {
		String string = this.getScript().getModel().file;
		Model model = ModelManager.instance.getLoadedModel(string);
		if (model != null && !model.bStatic) {
			if (this.m_animPlayer != null && this.m_animPlayer.getModel() != model) {
				this.m_animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.m_animPlayer);
			}

			if (this.m_animPlayer == null) {
				this.m_animPlayer = AnimationPlayer.alloc(model);
			}

			return this.m_animPlayer;
		} else {
			return null;
		}
	}

	public void releaseAnimationPlayers() {
		this.m_animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.m_animPlayer);
		PZArrayUtil.forEach((List)this.models, BaseVehicle.ModelInfo::releaseAnimationPlayer);
	}

	public void setAddThumpWorldSound(boolean boolean1) {
		this.bAddThumpWorldSound = boolean1;
	}

	public void Thump(IsoMovingObject movingObject) {
		VehiclePart vehiclePart = this.getPartById("lightbar");
		if (vehiclePart != null) {
			if (vehiclePart.getCondition() <= 0) {
				movingObject.setThumpTarget((Thumpable)null);
			}

			VehiclePart vehiclePart2 = this.getUseablePart((IsoGameCharacter)movingObject);
			if (vehiclePart2 != null) {
				vehiclePart2.setCondition(vehiclePart2.getCondition() - Rand.Next(1, 5));
			}

			vehiclePart.setCondition(vehiclePart.getCondition() - Rand.Next(1, 5));
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
	}

	public Thumpable getThumpableFor(IsoGameCharacter gameCharacter) {
		return null;
	}

	public boolean isRegulator() {
		return this.regulator;
	}

	public void setRegulator(boolean boolean1) {
		this.regulator = boolean1;
	}

	public float getRegulatorSpeed() {
		return this.regulatorSpeed;
	}

	public void setRegulatorSpeed(float float1) {
		this.regulatorSpeed = float1;
	}

	public void setVehicleTowing(BaseVehicle baseVehicle, String string, String string2, float float1) {
		this.vehicleTowing = baseVehicle;
		this.vehicleTowingID = this.vehicleTowing == null ? -1 : this.vehicleTowing.getSqlId();
		this.towAttachmentSelf = string;
		this.towAttachmentOther = string2;
		this.towConstraintZOffset = float1;
	}

	public void setVehicleTowedBy(BaseVehicle baseVehicle, String string, String string2, float float1) {
		this.vehicleTowedBy = baseVehicle;
		this.vehicleTowedByID = this.vehicleTowedBy == null ? -1 : this.vehicleTowedBy.getSqlId();
		this.towAttachmentSelf = string2;
		this.towAttachmentOther = string;
		this.towConstraintZOffset = float1;
	}

	public BaseVehicle getVehicleTowing() {
		return this.vehicleTowing;
	}

	public BaseVehicle getVehicleTowedBy() {
		return this.vehicleTowedBy;
	}

	public boolean attachmentExist(String string) {
		VehicleScript vehicleScript = this.getScript();
		if (vehicleScript == null) {
			return false;
		} else {
			ModelAttachment modelAttachment = vehicleScript.getAttachmentById(string);
			return modelAttachment != null;
		}
	}

	public Vector3f getAttachmentLocalPos(String string, Vector3f vector3f) {
		VehicleScript vehicleScript = this.getScript();
		if (vehicleScript == null) {
			return null;
		} else {
			ModelAttachment modelAttachment = vehicleScript.getAttachmentById(string);
			if (modelAttachment == null) {
				return null;
			} else {
				vector3f.set((Vector3fc)modelAttachment.getOffset());
				return vehicleScript.getModel() == null ? vector3f : vector3f.add(vehicleScript.getModel().getOffset());
			}
		}
	}

	public Vector3f getAttachmentWorldPos(String string, Vector3f vector3f) {
		vector3f = this.getAttachmentLocalPos(string, vector3f);
		return vector3f == null ? null : this.getWorldPos(vector3f, vector3f);
	}

	public Vector3f getTowingLocalPos(String string, Vector3f vector3f) {
		return this.getAttachmentLocalPos(string, vector3f);
	}

	public Vector3f getTowedByLocalPos(String string, Vector3f vector3f) {
		return this.getAttachmentLocalPos(string, vector3f);
	}

	public Vector3f getTowingWorldPos(String string, Vector3f vector3f) {
		vector3f = this.getTowingLocalPos(string, vector3f);
		return vector3f == null ? null : this.getWorldPos(vector3f, vector3f);
	}

	public Vector3f getTowedByWorldPos(String string, Vector3f vector3f) {
		vector3f = this.getTowedByLocalPos(string, vector3f);
		return vector3f == null ? null : this.getWorldPos(vector3f, vector3f);
	}

	public Vector3f getPlayerTrailerLocalPos(String string, boolean boolean1, Vector3f vector3f) {
		ModelAttachment modelAttachment = this.getScript().getAttachmentById(string);
		if (modelAttachment == null) {
			return null;
		} else {
			Vector3f vector3f2 = this.getScript().getExtents();
			Vector3f vector3f3 = this.getScript().getCenterOfMassOffset();
			float float1 = vector3f3.x + vector3f2.x / 2.0F + 0.3F + 0.05F;
			if (!boolean1) {
				float1 *= -1.0F;
			}

			return modelAttachment.getOffset().z > 0.0F ? vector3f.set(float1, 0.0F, vector3f3.z + vector3f2.z / 2.0F + 0.3F + 0.05F) : vector3f.set(float1, 0.0F, vector3f3.z - (vector3f2.z / 2.0F + 0.3F + 0.05F));
		}
	}

	public Vector3f getPlayerTrailerWorldPos(String string, boolean boolean1, Vector3f vector3f) {
		vector3f = this.getPlayerTrailerLocalPos(string, boolean1, vector3f);
		if (vector3f == null) {
			return null;
		} else {
			this.getWorldPos(vector3f, vector3f);
			vector3f.z = (float)((int)this.z);
			Vector3f vector3f2 = this.getTowingWorldPos(string, (Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
			boolean boolean2 = PolygonalMap2.instance.lineClearCollide(vector3f.x, vector3f.y, vector3f2.x, vector3f2.y, (int)this.z, this, false, false);
			((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
			return boolean2 ? null : vector3f;
		}
	}

	public void addHingeConstraint(BaseVehicle baseVehicle, String string, String string2) {
		this.breakConstraint(true, false);
		baseVehicle.breakConstraint(true, false);
		BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get();
		Vector3f vector3f = this.getTowingLocalPos(string, (Vector3f)vector3fObjectPool.alloc());
		Vector3f vector3f2 = baseVehicle.getTowedByLocalPos(string2, (Vector3f)vector3fObjectPool.alloc());
		if (vector3f != null && vector3f2 != null) {
			this.constraintTowing = Bullet.addHingeConstraint(this.VehicleID, baseVehicle.VehicleID, vector3f.x, vector3f.y, vector3f.z, vector3f2.x, vector3f2.y, vector3f2.z);
			baseVehicle.constraintTowing = this.constraintTowing;
			this.setVehicleTowing(baseVehicle, string, string2, 0.0F);
			baseVehicle.setVehicleTowedBy(this, string, string2, 0.0F);
			vector3fObjectPool.release(vector3f);
			vector3fObjectPool.release(vector3f2);
			this.constraintChanged();
			baseVehicle.constraintChanged();
		} else {
			if (vector3f != null) {
				vector3fObjectPool.release(vector3f);
			}

			if (vector3f2 != null) {
				vector3fObjectPool.release(vector3f2);
			}
		}
	}

	private void drawTowingRope() {
		BaseVehicle baseVehicle = this.getVehicleTowing();
		if (baseVehicle != null) {
			BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get();
			this.getAttachmentWorldPos("trailer", (Vector3f)vector3fObjectPool.alloc());
			Vector3f vector3f = this.getAttachmentWorldPos("trailerfront", (Vector3f)vector3fObjectPool.alloc());
			ModelAttachment modelAttachment = this.script.getAttachmentById("trailerfront");
			if (modelAttachment != null) {
				vector3f.set((Vector3fc)modelAttachment.getOffset());
			}

			Vector2 vector2 = new Vector2();
			vector2.x = baseVehicle.x;
			vector2.y = baseVehicle.y;
			vector2.x -= this.x;
			vector2.y -= this.y;
			vector2.setLength(2.0F);
			this.drawDirectionLine(vector2, vector2.getLength(), 1.0F, 0.5F, 0.5F);
		}
	}

	public void drawDirectionLine(Vector2 vector2, float float1, float float2, float float3, float float4) {
		float float5 = this.x + vector2.x * float1;
		float float6 = this.y + vector2.y * float1;
		float float7 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
		float float8 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
		float float9 = IsoUtils.XToScreenExact(float5, float6, this.z, 0);
		float float10 = IsoUtils.YToScreenExact(float5, float6, this.z, 0);
		LineDrawer.drawLine(float7, float8, float9, float10, float2, float3, float4, 0.5F, 1);
	}

	public void updateConstraint(BaseVehicle baseVehicle) {
		if (!this.getScriptName().contains("Trailer") && !baseVehicle.getScriptName().contains("Trailer")) {
			ModelAttachment modelAttachment = this.script.getAttachmentById(this.towAttachmentSelf);
			if (modelAttachment != null && modelAttachment.isUpdateConstraint()) {
				Vector3f vector3f = this.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
				Vector3f vector3f2 = this.getLinearVelocity((Vector3f)((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).alloc());
				float float1 = vector3f2.dot(vector3f);
				if (vector3f2.lengthSquared() < 0.25F) {
					float1 = 0.0F;
				}

				boolean boolean1 = float1 > 0.0F;
				boolean boolean2 = float1 < 0.0F;
				float float2 = this.towConstraintZOffset;
				float float3 = GameTime.getInstance().getMultiplier() / 0.8F;
				if (modelAttachment.getZOffset() > 0.0F) {
					if ((boolean1 && this.isBraking || this.getController().EngineForce < 0.0F) && float2 < 1.0F) {
						float2 = Math.min(1.0F, float2 + 0.015F * float3);
					} else if ((boolean2 && this.isBraking || this.getController().EngineForce > 0.0F) && float2 > 0.1F) {
						float2 = Math.max(0.1F, float2 - 0.01F * float3);
					}
				} else if (modelAttachment.getZOffset() < 0.0F) {
					if ((boolean1 && this.isBraking || this.getController().EngineForce < 0.0F) && float2 < 0.1F) {
						float2 = Math.min(0.1F, float2 + 0.015F * float3);
					} else if ((boolean2 && this.isBraking || this.getController().EngineForce > 0.0F) && float2 > -1.0F) {
						float2 = Math.max(-1.0F, float2 - 0.01F * float3);
					}
				}

				if (float2 != this.towConstraintZOffset) {
					this.addPointConstraint(baseVehicle, this.towAttachmentSelf, baseVehicle.towAttachmentSelf, float2, true);
				}

				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f);
				((BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get()).release(vector3f2);
			}
		}
	}

	public void addPointConstraint(BaseVehicle baseVehicle, String string, String string2) {
		this.addPointConstraint(baseVehicle, string, string2, (Float)null, false);
	}

	public void addPointConstraint(BaseVehicle baseVehicle, String string, String string2, Float Float1, Boolean Boolean1) {
		this.breakConstraint(true, true);
		baseVehicle.breakConstraint(true, true);
		BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get();
		Vector3f vector3f = this.getTowingLocalPos(string, (Vector3f)vector3fObjectPool.alloc());
		Vector3f vector3f2 = baseVehicle.getTowedByLocalPos(string2, (Vector3f)vector3fObjectPool.alloc());
		if (vector3f != null && vector3f2 != null) {
			ModelAttachment modelAttachment = this.script.getAttachmentById(string);
			float float1 = 0.0F;
			float float2 = 0.0F;
			if (modelAttachment != null && modelAttachment.getZOffset() != 0.0F) {
				float1 = modelAttachment.getZOffset();
			}

			if (Float1 != null) {
				float1 = Float1;
			}

			if (this.getScriptName().contains("Trailer") || baseVehicle.getScriptName().contains("Trailer")) {
				float1 = 0.0F;
			}

			this.constraintTowing = Bullet.addPointConstraint(this.VehicleID, baseVehicle.VehicleID, vector3f.x, vector3f.y, vector3f.z + float1, vector3f2.x, vector3f2.y, vector3f2.z + float2);
			baseVehicle.constraintTowing = this.constraintTowing;
			this.setVehicleTowing(baseVehicle, string, string2, float1);
			baseVehicle.setVehicleTowedBy(this, string, string2, float2);
			vector3fObjectPool.release(vector3f);
			vector3fObjectPool.release(vector3f2);
			this.constraintChanged();
			baseVehicle.constraintChanged();
			if (GameClient.bClient && !Boolean1) {
				VehicleManager.instance.sendTowing(this, baseVehicle, string, string2, Float1);
			}
		} else {
			if (vector3f != null) {
				vector3fObjectPool.release(vector3f);
			}

			if (vector3f2 != null) {
				vector3fObjectPool.release(vector3f2);
			}
		}
	}

	public void constraintChanged() {
		long long1 = System.currentTimeMillis();
		this.setPhysicsActive(true);
		this.constraintChangedTime = long1;
	}

	public void breakConstraint(boolean boolean1, boolean boolean2) {
		if (this.constraintTowing != -1) {
			Bullet.removeConstraint(this.constraintTowing);
			this.constraintTowing = -1;
			this.constraintChanged();
			if (GameClient.bClient && !boolean2) {
				VehicleManager.instance.sendDetachTowing(this.vehicleTowing, this.vehicleTowedBy);
			}

			if (this.vehicleTowing != null) {
				this.vehicleTowing.constraintChanged();
				this.vehicleTowing.vehicleTowedBy = null;
				this.vehicleTowing.constraintTowing = -1;
				if (boolean1) {
					this.vehicleTowingID = -1;
					this.vehicleTowing.vehicleTowedByID = -1;
				}

				this.vehicleTowing = null;
			}

			if (this.vehicleTowedBy != null) {
				this.vehicleTowedBy.constraintChanged();
				this.vehicleTowedBy.vehicleTowing = null;
				this.vehicleTowedBy.constraintTowing = -1;
				if (boolean1) {
					this.vehicleTowedBy.vehicleTowingID = -1;
					this.vehicleTowedByID = -1;
				}

				this.vehicleTowedBy = null;
			}
		}
	}

	public boolean canAttachTrailer(BaseVehicle baseVehicle, String string, String string2) {
		if (this != baseVehicle && this.physics != null && this.constraintTowing == -1) {
			if (baseVehicle != null && baseVehicle.physics != null && baseVehicle.constraintTowing == -1) {
				if (!GameClient.bClient) {
					float float1 = GameServer.bServer ? this.netLinearVelocity.y : this.jniLinearVelocity.y;
					if (Math.abs(float1) > 0.2F) {
						return false;
					}

					float float2 = GameServer.bServer ? baseVehicle.netLinearVelocity.y : baseVehicle.jniLinearVelocity.y;
					if (Math.abs(float2) > 0.2F) {
						return false;
					}
				}

				long long1 = System.currentTimeMillis();
				if (this.createPhysicsTime + 1000L > long1) {
					return false;
				} else if (baseVehicle.createPhysicsTime + 1000L > long1) {
					return false;
				} else {
					BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get();
					Vector3f vector3f = this.getTowingWorldPos(string, (Vector3f)vector3fObjectPool.alloc());
					Vector3f vector3f2 = baseVehicle.getTowedByWorldPos(string2, (Vector3f)vector3fObjectPool.alloc());
					if (vector3f != null && vector3f2 != null) {
						float float3 = IsoUtils.DistanceToSquared(vector3f.x, vector3f.y, vector3f.z, vector3f2.x, vector3f2.y, vector3f2.z);
						vector3fObjectPool.release(vector3f);
						vector3fObjectPool.release(vector3f2);
						ModelAttachment modelAttachment = this.script.getAttachmentById(string);
						ModelAttachment modelAttachment2 = baseVehicle.script.getAttachmentById(string2);
						if (modelAttachment != null && modelAttachment.getCanAttach() != null && !modelAttachment.getCanAttach().contains(string2)) {
							return false;
						} else if (modelAttachment2 != null && modelAttachment2.getCanAttach() != null && !modelAttachment2.getCanAttach().contains(string)) {
							return false;
						} else {
							return float3 < 2.0F;
						}
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void tryReconnectToTowedVehicle() {
		if (!GameClient.bClient) {
			if (this.vehicleTowing == null) {
				if (this.vehicleTowingID != -1) {
					BaseVehicle baseVehicle = null;
					ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();
					for (int int1 = 0; int1 < arrayList.size(); ++int1) {
						BaseVehicle baseVehicle2 = (BaseVehicle)arrayList.get(int1);
						if (baseVehicle2.getSqlId() == this.vehicleTowingID) {
							baseVehicle = baseVehicle2;
							break;
						}
					}

					if (baseVehicle != null) {
						if (this.canAttachTrailer(baseVehicle, this.towAttachmentSelf, this.towAttachmentOther)) {
							this.addPointConstraint(baseVehicle, this.towAttachmentSelf, this.towAttachmentOther, this.towConstraintZOffset, false);
						}
					}
				}
			}
		}
	}

	public void positionTrailer(BaseVehicle baseVehicle) {
		if (baseVehicle != null) {
			BaseVehicle.Vector3fObjectPool vector3fObjectPool = (BaseVehicle.Vector3fObjectPool)TL_vector3f_pool.get();
			Vector3f vector3f = this.getTowingWorldPos("trailer", (Vector3f)vector3fObjectPool.alloc());
			Vector3f vector3f2 = baseVehicle.getTowedByWorldPos("trailer", (Vector3f)vector3fObjectPool.alloc());
			if (vector3f != null && vector3f2 != null) {
				vector3f2.sub(baseVehicle.x, baseVehicle.y, baseVehicle.z);
				vector3f.sub(vector3f2);
				Transform transform = baseVehicle.getWorldTransform(this.tempTransform);
				transform.origin.set(vector3f.x - WorldSimulation.instance.offsetX, baseVehicle.jniTransform.origin.y, vector3f.y - WorldSimulation.instance.offsetY);
				baseVehicle.setWorldTransform(transform);
				baseVehicle.setX(vector3f.x);
				baseVehicle.setLx(vector3f.x);
				baseVehicle.setY(vector3f.y);
				baseVehicle.setLy(vector3f.y);
				baseVehicle.setCurrent(this.getCell().getGridSquare((double)vector3f.x, (double)vector3f.y, 0.0));
				this.addPointConstraint(baseVehicle, "trailer", "trailer");
				vector3fObjectPool.release(vector3f);
				vector3fObjectPool.release(vector3f2);
			}
		}
	}

	public String getTowAttachmentSelf() {
		return this.towAttachmentSelf;
	}

	public String getTowAttachmentOther() {
		return this.towAttachmentOther;
	}

	public VehicleEngineRPM getVehicleEngineRPM() {
		if (this.vehicleEngineRPM == null) {
			this.vehicleEngineRPM = ScriptManager.instance.getVehicleEngineRPM(this.getScript().getEngineRPMType());
			if (this.vehicleEngineRPM == null) {
				DebugLog.General.warn("unknown vehicleEngineRPM \"%s\"", this.getScript().getEngineRPMType());
				this.vehicleEngineRPM = new VehicleEngineRPM();
			}
		}

		return this.vehicleEngineRPM;
	}

	public FMODParameterList getFMODParameters() {
		return this.fmodParameters;
	}

	public void startEvent(long long1, GameSoundClip gameSoundClip, BitSet bitSet) {
		FMODParameterList fMODParameterList = this.getFMODParameters();
		ArrayList arrayList = gameSoundClip.eventDescription.parameters;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int1);
			if (!bitSet.get(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
				FMODParameter fMODParameter = fMODParameterList.get(fMOD_STUDIO_PARAMETER_DESCRIPTION);
				if (fMODParameter != null) {
					fMODParameter.startEventInstance(long1);
				}
			}
		}
	}

	public void updateEvent(long long1, GameSoundClip gameSoundClip) {
	}

	public void stopEvent(long long1, GameSoundClip gameSoundClip, BitSet bitSet) {
		FMODParameterList fMODParameterList = this.getFMODParameters();
		ArrayList arrayList = gameSoundClip.eventDescription.parameters;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			FMOD_STUDIO_PARAMETER_DESCRIPTION fMOD_STUDIO_PARAMETER_DESCRIPTION = (FMOD_STUDIO_PARAMETER_DESCRIPTION)arrayList.get(int1);
			if (!bitSet.get(fMOD_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
				FMODParameter fMODParameter = fMODParameterList.get(fMOD_STUDIO_PARAMETER_DESCRIPTION);
				if (fMODParameter != null) {
					fMODParameter.stopEventInstance(long1);
				}
			}
		}
	}

	private void stopEngineSounds() {
		if (this.emitter != null) {
			for (int int1 = 0; int1 < this.new_EngineSoundId.length; ++int1) {
				if (this.new_EngineSoundId[int1] != 0L) {
					this.getEmitter().stopSound(this.new_EngineSoundId[int1]);
					this.new_EngineSoundId[int1] = 0L;
				}
			}

			if (this.combinedEngineSound != 0L) {
				if (this.getEmitter().hasSustainPoints(this.combinedEngineSound)) {
					this.getEmitter().triggerCue(this.combinedEngineSound);
				} else {
					this.getEmitter().stopSound(this.combinedEngineSound);
				}

				this.combinedEngineSound = 0L;
			}
		}
	}

	public void debugSetStatic(boolean boolean1) {
		Bullet.setVehicleStatic(this.getId(), boolean1);
	}

	public BaseVehicle setSmashed(String string) {
		return this.setSmashed(string, false);
	}

	public BaseVehicle setSmashed(String string, boolean boolean1) {
		String string2 = null;
		Integer integer = null;
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("SmashedCarDefinitions");
		if (kahluaTableImpl != null) {
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget("cars");
			if (kahluaTableImpl2 != null) {
				KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget(this.getScriptName());
				if (kahluaTableImpl3 != null) {
					string2 = kahluaTableImpl3.rawgetStr(string.toLowerCase());
					integer = kahluaTableImpl3.rawgetInt("skin");
					if (integer == -1) {
						integer = this.getSkinIndex();
					}
				}
			}
		}

		if (string2 == null) {
			return this;
		} else {
			this.removeFromWorld();
			this.permanentlyRemove();
			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle.setScriptName(string2);
			baseVehicle.setScript();
			baseVehicle.setSkinIndex(integer);
			baseVehicle.setX(this.x);
			baseVehicle.setY(this.y);
			baseVehicle.setZ(this.z);
			baseVehicle.setDir(this.getDir());
			float float1;
			if (!boolean1) {
				for (float1 = this.dir.toAngle() + 3.1415927F + Rand.Next(-0.5F, 0.5F); (double)float1 > 6.283185307179586; float1 = (float)((double)float1 - 6.283185307179586)) {
				}

				baseVehicle.savedRot.setAngleAxis(float1, 0.0F, 1.0F, 0.0F);
			} else {
				float1 = 3.0F;
				baseVehicle.savedRot.setAngleAxis(float1, 1.0F, 0.0F, 0.0F);
			}

			baseVehicle.jniTransform.setRotation(baseVehicle.savedRot);
			if (IsoChunk.doSpawnedVehiclesInInvalidPosition(baseVehicle)) {
				baseVehicle.setSquare(this.square);
				baseVehicle.square.chunk.vehicles.add(baseVehicle);
				baseVehicle.chunk = baseVehicle.square.chunk;
				baseVehicle.addToWorld();
				VehiclesDB2.instance.addVehicle(baseVehicle);
			}

			baseVehicle.setGeneralPartCondition(0.5F, 60.0F);
			VehiclePart vehiclePart = baseVehicle.getPartById("Engine");
			if (vehiclePart != null) {
				vehiclePart.setCondition(0);
			}

			baseVehicle.engineQuality = 0;
			return baseVehicle;
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

	public static final class Vector2ObjectPool extends ObjectPool {
		int allocated = 0;

		Vector2ObjectPool() {
			super(Vector2::new);
		}

		protected Vector2 makeObject() {
			++this.allocated;
			return (Vector2)super.makeObject();
		}
	}

	public static final class Vector3fObjectPool extends ObjectPool {
		int allocated = 0;

		Vector3fObjectPool() {
			super(Vector3f::new);
		}

		protected Vector3f makeObject() {
			++this.allocated;
			return (Vector3f)super.makeObject();
		}
	}

	private static final class VehicleImpulse {
		static final ArrayDeque pool = new ArrayDeque();
		final Vector3f impulse = new Vector3f();
		final Vector3f rel_pos = new Vector3f();
		boolean enable = false;

		static BaseVehicle.VehicleImpulse alloc() {
			return pool.isEmpty() ? new BaseVehicle.VehicleImpulse() : (BaseVehicle.VehicleImpulse)pool.pop();
		}

		void release() {
			pool.push(this);
		}
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

		private static BaseVehicle.engineStateTypes[] $values() {
			return new BaseVehicle.engineStateTypes[]{Idle, Starting, RetryingStarting, StartingSuccess, StartingFailed, Running, Stalling, ShutingDown, StartingFailedNoPower};
		}
	}

	public static final class WheelInfo {
		public float steering;
		public float rotation;
		public float skidInfo;
		public float suspensionLength;
	}

	public static final class ServerVehicleState {
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
				if (Math.abs(this.x - baseVehicle.x) > 0.01F || Math.abs(this.y - baseVehicle.y) > 0.01F || Math.abs(this.z - baseVehicle.jniTransform.origin.y) > 0.01F || Math.abs(this.orient.x - baseVehicle.savedRot.x) > 0.01F || Math.abs(this.orient.y - baseVehicle.savedRot.y) > 0.01F || Math.abs(this.orient.z - baseVehicle.savedRot.z) > 0.01F || Math.abs(this.orient.w - baseVehicle.savedRot.w) > 0.01F) {
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

	public static final class Passenger {
		public IsoGameCharacter character;
		final Vector3f offset = new Vector3f();
	}

	public static final class ModelInfo {
		public VehiclePart part;
		public VehicleScript.Model scriptModel;
		public ModelScript modelScript;
		public int wheelIndex;
		public final Matrix4f renderTransform = new Matrix4f();
		public VehicleSubModelInstance modelInstance;
		public AnimationPlayer m_animPlayer;
		public AnimationTrack m_track;

		public AnimationPlayer getAnimationPlayer() {
			if (this.part != null && this.part.getParent() != null) {
				BaseVehicle.ModelInfo modelInfo = this.part.getVehicle().getModelInfoForPart(this.part.getParent());
				if (modelInfo != null) {
					return modelInfo.getAnimationPlayer();
				}
			}

			String string = this.scriptModel.file;
			Model model = ModelManager.instance.getLoadedModel(string);
			if (model != null && !model.bStatic) {
				if (this.m_animPlayer != null && this.m_animPlayer.getModel() != model) {
					this.m_animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.m_animPlayer);
				}

				if (this.m_animPlayer == null) {
					this.m_animPlayer = AnimationPlayer.alloc(model);
				}

				return this.m_animPlayer;
			} else {
				return null;
			}
		}

		public void releaseAnimationPlayer() {
			this.m_animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.m_animPlayer);
		}
	}

	public static final class Matrix4fObjectPool extends ObjectPool {
		int allocated = 0;

		Matrix4fObjectPool() {
			super(Matrix4f::new);
		}

		protected Matrix4f makeObject() {
			++this.allocated;
			return (Matrix4f)super.makeObject();
		}
	}

	public static final class QuaternionfObjectPool extends ObjectPool {
		int allocated = 0;

		QuaternionfObjectPool() {
			super(Quaternionf::new);
		}

		protected Quaternionf makeObject() {
			++this.allocated;
			return (Quaternionf)super.makeObject();
		}
	}

	public static final class Vector2fObjectPool extends ObjectPool {
		int allocated = 0;

		Vector2fObjectPool() {
			super(Vector2f::new);
		}

		protected Vector2f makeObject() {
			++this.allocated;
			return (Vector2f)super.makeObject();
		}
	}

	public static final class MinMaxPosition {
		public float minX;
		public float maxX;
		public float minY;
		public float maxY;
	}
}
