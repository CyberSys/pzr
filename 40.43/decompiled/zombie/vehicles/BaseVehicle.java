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
   private double sirenStartTime = 0.0D;
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

   public void addImpulse(Vector3f var1, Vector3f var2) {
      if (!this.impulseFromServer.enable) {
         this.impulseFromServer.enable = true;
         this.impulseFromServer.impulse.set((Vector3fc)var1);
         this.impulseFromServer.rel_pos.set((Vector3fc)var2);
      } else if (this.impulseFromServer.impulse.length() < var1.length()) {
         this.impulseFromServer.impulse.set((Vector3fc)var1);
         this.impulseFromServer.rel_pos.set((Vector3fc)var2);
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

   public void setClientForce(float var1) {
      this.physics.clientForce = var1;
   }

   public BaseVehicle(IsoCell var1) {
      super(var1, false);
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

      for(int var2 = 0; var2 < this.wheelInfo.length; ++var2) {
         this.wheelInfo[var2] = new BaseVehicle.WheelInfo();
      }

      if (GameClient.bClient) {
         this.interpolation = new VehicleInterpolation(VehicleManager.physicsDelay);
      }

      this.setKeyId(Rand.Next(100000000));
      this.engineSpeed = 0.0D;
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
         ArrayList var0 = ScriptManager.instance.getAllVehicleScripts();

         for(int var1 = 0; var1 < var0.size(); ++var1) {
            LoadVehicleTextures((VehicleScript)var0.get(var1));
         }
      } finally {
         RenderThread.returnContext();
      }

   }

   public static void LoadVehicleTextures(VehicleScript var0) {
      if (var0.getSkinCount() > 0) {
         var0.textureDataSkins = new Texture[var0.getSkinCount()];

         for(int var1 = 0; var1 < var0.getSkinCount(); ++var1) {
            var0.textureDataSkins[var1] = Texture.getSharedTexture("media/textures/" + var0.getSkin(var1).texture + ".png");
         }
      }

      if (var0.textureMask != null) {
         boolean var2 = TextureID.bUseCompression;
         TextureID.bUseCompression = false;
         var0.textureDataMask = Texture.getSharedTexture("media/textures/" + var0.textureMask + ".png");
         TextureID.bUseCompression = var2;
      }

      var0.textureDataDamage1Overlay = Texture.getSharedTexture("media/textures/" + var0.textureDamage1Overlay + ".png");
      var0.textureDataDamage1Shell = Texture.getSharedTexture("media/textures/" + var0.textureDamage1Shell + ".png");
      var0.textureDataDamage2Overlay = Texture.getSharedTexture("media/textures/" + var0.textureDamage2Overlay + ".png");
      var0.textureDataDamage2Shell = Texture.getSharedTexture("media/textures/" + var0.textureDamage2Shell + ".png");
      var0.textureDataLights = Texture.getSharedTexture("media/textures/" + var0.textureLights + ".png");
      var0.textureDataRust = Texture.getSharedTexture("media/textures/" + var0.textureRust + ".png");
   }

   private void doVehicleColor() {
      if (!this.isDoColor()) {
         this.colorSaturation = 0.1F;
         this.colorValue = 0.9F;
      } else {
         this.colorHue = Rand.Next(0.0F, 0.0F);
         this.colorSaturation = 0.5F;
         this.colorValue = Rand.Next(0.3F, 0.6F);
         int var1 = Rand.Next(100);
         if (var1 < 20) {
            this.colorHue = Rand.Next(0.0F, 0.03F);
            this.colorSaturation = Rand.Next(0.85F, 1.0F);
            this.colorValue = Rand.Next(0.55F, 0.85F);
         } else if (var1 < 32) {
            this.colorHue = Rand.Next(0.55F, 0.61F);
            this.colorSaturation = Rand.Next(0.85F, 1.0F);
            this.colorValue = Rand.Next(0.65F, 0.75F);
         } else if (var1 < 67) {
            this.colorHue = Rand.Next(0.0F, 1.0F);
            this.colorSaturation = Rand.Next(0.0F, 0.1F);
            this.colorValue = Rand.Next(0.7F, 0.8F);
         } else if (var1 < 89) {
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
            byte var1 = 30;
            if (SandboxOptions.getInstance().RecentlySurvivorVehicles.getValue() == 1) {
               var1 = 10;
            }

            if (SandboxOptions.getInstance().RecentlySurvivorVehicles.getValue() == 3) {
               var1 = 50;
            }

            if (Rand.Next(100) < var1) {
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

         VehiclePart var2;
         for(int var3 = 0; var3 < this.parts.size(); ++var3) {
            var2 = (VehiclePart)this.parts.get(var3);
            if (var2.getLight() != null) {
               this.lights.add(var2);
            }
         }

         this.setMaxSpeed(this.getScript().maxSpeed);
         this.setInitialMass(this.getScript().getMass());
         if (!this.getCell().getVehicles().contains(this) && !this.getCell().addVehicles.contains(this)) {
            this.getCell().addVehicles.add(this);
         }

         this.square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
         Iterator var4 = this.parts.iterator();

         while(var4.hasNext()) {
            var2 = (VehiclePart)var4.next();
            if (var2.getItemContainer() != null && !var2.getItemContainer().bExplored) {
               if (Rand.Next(100) <= 100) {
                  this.randomizeContainer(var2);
               }

               var2.getItemContainer().setExplored(true);
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

   public void putKeyToZombie(IsoZombie var1) {
      InventoryItem var2 = this.createVehicleKey();
      var1.getInventory().AddItem(var2);
   }

   public void putKeyToContainer(ItemContainer var1, IsoGridSquare var2, IsoObject var3) {
      InventoryItem var4 = this.createVehicleKey();
      var1.AddItem(var4);
      if (GameServer.bServer) {
         for(int var5 = 0; var5 < GameServer.udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)GameServer.udpEngine.connections.get(var5);
            if (var6.ReleventTo((float)var3.square.x, (float)var3.square.y)) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)20, var7);
               var7.putShort((short)2);
               var7.putInt((int)var3.getX());
               var7.putInt((int)var3.getY());
               var7.putInt((int)var3.getZ());
               int var8 = var2.getObjects().indexOf(var3);
               var7.putByte((byte)var8);
               var7.putByte((byte)var3.getContainerIndex(var1));

               try {
                  CompressIdenticalItems.save(var7.bb, var4);
               } catch (Exception var10) {
                  var10.printStackTrace();
               }

               var6.endPacketUnordered();
            }
         }
      }

   }

   public void putKeyToWorld(IsoGridSquare var1) {
      InventoryItem var2 = this.createVehicleKey();
      var1.AddWorldInventoryItem(var2, 0.0F, 0.0F, 0.0F);
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
            IsoGridSquare var1 = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
            if (var1 != null) {
               this.addKeyToSquare(var1);
               return;
            }
         }

      }
   }

   public void addKeyToGloveBox() {
      if (this.keySpawned == 0) {
         if (this.getPartById("GloveBox") != null) {
            VehiclePart var1 = this.getPartById("GloveBox");
            InventoryItem var2 = this.createVehicleKey();
            var1.container.addItem(var2);
            this.keySpawned = 1;
         }

      }
   }

   public InventoryItem createVehicleKey() {
      InventoryItem var1 = InventoryItemFactory.CreateItem("CarKey");
      var1.setKeyId(this.getKeyId());
      var1.setName(Translator.getText("IGUI_CarKey", Translator.getText("IGUI_VehicleName" + this.getScript().getName())));
      Integer[] var2 = Color.HSBtoRGB(this.colorHue, this.colorSaturation * 0.5F, this.colorValue);
      var1.setColor(new Color(var2[0], var2[1], var2[2]));
      var1.setCustomColor(true);
      return var1;
   }

   public boolean addKeyToSquare(IsoGridSquare var1) {
      boolean var2 = false;
      IsoGridSquare var3 = null;

      int var4;
      int var5;
      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = var1.getX() - 10; var5 < var1.getX() + 10; ++var5) {
            for(int var6 = var1.getY() - 10; var6 < var1.getY() + 10; ++var6) {
               var3 = IsoWorld.instance.getCell().getGridSquare(var5, var6, var4);
               if (var3 != null) {
                  int var7;
                  for(var7 = 0; var7 < var3.getObjects().size(); ++var7) {
                     IsoObject var8 = (IsoObject)var3.getObjects().get(var7);
                     if (var8.container != null && (var8.container.type.equals("counter") || var8.container.type.equals("officedrawers") || var8.container.type.equals("shelves") || var8.container.type.equals("desk"))) {
                        this.putKeyToContainer(var8.container, var3, var8);
                        var2 = true;
                        break;
                     }
                  }

                  for(var7 = 0; var7 < var3.getMovingObjects().size(); ++var7) {
                     if (var3.getMovingObjects().get(var7) instanceof IsoZombie) {
                        ((IsoZombie)var3.getMovingObjects().get(var7)).addItemToSpawnAtDeath(this.createVehicleKey());
                        var2 = true;
                        break;
                     }
                  }
               }

               if (var2) {
                  break;
               }
            }

            if (var2) {
               break;
            }
         }

         if (var2) {
            break;
         }
      }

      if (Rand.Next(10) < 6) {
         while(!var2) {
            var4 = var1.getX() - 10 + Rand.Next(20);
            var5 = var1.getY() - 10 + Rand.Next(20);
            var3 = IsoWorld.instance.getCell().getGridSquare((double)var4, (double)var5, (double)this.z);
            if (var3 != null && !var3.isSolid() && !var3.isSolidTrans() && !var3.HasTree()) {
               this.putKeyToWorld(var3);
               var2 = true;
               break;
            }
         }
      }

      return var2;
   }

   public void toggleLockedDoor(VehiclePart var1, IsoGameCharacter var2, boolean var3) {
      if (var3) {
         if (!this.canLockDoor(var1, var2)) {
            return;
         }

         var1.getDoor().setLocked(true);
      } else {
         if (!this.canUnlockDoor(var1, var2)) {
            return;
         }

         var1.getDoor().setLocked(false);
      }

   }

   public boolean canLockDoor(VehiclePart var1, IsoGameCharacter var2) {
      if (var1 == null) {
         return false;
      } else if (var2 == null) {
         return false;
      } else {
         VehicleDoor var3 = var1.getDoor();
         if (var3 == null) {
            return false;
         } else if (var3.lockBroken) {
            return false;
         } else if (var3.locked) {
            return false;
         } else if (this.getSeat(var2) != -1) {
            return true;
         } else if (var2.getInventory().haveThisKeyId(this.getKeyId()) != null) {
            return true;
         } else {
            VehiclePart var4 = var1.getChildWindow();
            if (var4 != null && var4.getInventoryItem() == null) {
               return true;
            } else {
               VehicleWindow var5 = var4 == null ? null : var4.getWindow();
               return var5 != null && (var5.isOpen() || var5.isDestroyed());
            }
         }
      }
   }

   public boolean canUnlockDoor(VehiclePart var1, IsoGameCharacter var2) {
      if (var1 == null) {
         return false;
      } else if (var2 == null) {
         return false;
      } else {
         VehicleDoor var3 = var1.getDoor();
         if (var3 == null) {
            return false;
         } else if (var3.lockBroken) {
            return false;
         } else if (!var3.locked) {
            return false;
         } else if (this.getSeat(var2) != -1) {
            return true;
         } else if (var2.getInventory().haveThisKeyId(this.getKeyId()) != null) {
            return true;
         } else {
            VehiclePart var4 = var1.getChildWindow();
            if (var4 != null && var4.getInventoryItem() == null) {
               return true;
            } else {
               VehicleWindow var5 = var4 == null ? null : var4.getWindow();
               return var5 != null && (var5.isOpen() || var5.isDestroyed());
            }
         }
      }
   }

   public void setFullUpdateFlag() {
      this.updateFlags = (short)(this.updateFlags | 1);
   }

   private void initParts() {
      for(int var1 = 0; var1 < this.parts.size(); ++var1) {
         VehiclePart var2 = (VehiclePart)this.parts.get(var1);
         String var3 = var2.getLuaFunction("init");
         if (var3 != null) {
            this.callLuaVoid(var3, this, var2);
         }
      }

   }

   private void createParts() {
      for(int var1 = 0; var1 < this.parts.size(); ++var1) {
         VehiclePart var2 = (VehiclePart)this.parts.get(var1);
         if (!var2.bCreated) {
            var2.bCreated = true;
            String var3 = var2.getLuaFunction("create");
            if (var3 == null) {
               var2.setRandomCondition((InventoryItem)null);
            } else {
               this.callLuaVoid(var3, this, var2);
               if (var2.getCondition() == -1) {
                  var2.setRandomCondition((InventoryItem)null);
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

   public void setSkinIndex(int var1) {
      if (var1 >= 0 && var1 <= this.getSkinCount()) {
         this.skinIndex = var1;
      }
   }

   public VehicleScript getScript() {
      return this.script;
   }

   public void setScriptName(String var1) {
      this.scriptName = var1;
   }

   public String getScriptName() {
      return this.scriptName;
   }

   public void setScript() {
      this.setScript(this.scriptName);
   }

   public void setScript(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.scriptName = var1;
         boolean var2 = this.script != null;
         this.script = ScriptManager.instance.getVehicle(this.scriptName);
         ArrayList var4;
         int var5;
         if (this.script == null) {
            ArrayList var3 = ScriptManager.instance.getAllVehicleScripts();
            if (!var3.isEmpty()) {
               var4 = new ArrayList();

               for(var5 = 0; var5 < var3.size(); ++var5) {
                  VehicleScript var6 = (VehicleScript)var3.get(var5);
                  if (var6.getWheelCount() == 0) {
                     var4.add(var6);
                     var3.remove(var5--);
                  }
               }

               boolean var12 = this.loaded && this.parts.isEmpty() || this.scriptName.contains("Burnt");
               if (var12 && !var4.isEmpty()) {
                  this.script = (VehicleScript)var4.get(Rand.Next(var4.size()));
               } else if (!var3.isEmpty()) {
                  this.script = (VehicleScript)var3.get(Rand.Next(var3.size()));
               }

               if (this.script != null) {
                  this.scriptName = this.script.getFullName();
               }
            }
         }

         this.battery = null;
         this.models.clear();
         if (this.script != null) {
            BaseVehicle.Passenger[] var10 = this.passengers;
            this.passengers = new BaseVehicle.Passenger[this.script.getPassengerCount()];

            for(int var11 = 0; var11 < this.passengers.length; ++var11) {
               if (var11 < var10.length) {
                  this.passengers[var11] = var10[var11];
               } else {
                  this.passengers[var11] = new BaseVehicle.Passenger();
               }
            }

            var4 = new ArrayList();
            var4.addAll(this.parts);
            this.parts.clear();

            for(var5 = 0; var5 < this.script.getPartCount(); ++var5) {
               VehicleScript.Part var13 = this.script.getPart(var5);
               VehiclePart var7 = null;

               for(int var8 = 0; var8 < var4.size(); ++var8) {
                  VehiclePart var9 = (VehiclePart)var4.get(var8);
                  if (var9.getScriptPart() != null && var13.id.equals(var9.getScriptPart().id)) {
                     var7 = var9;
                     break;
                  }

                  if (var9.partId != null && var13.id.equals(var9.partId)) {
                     var7 = var9;
                     break;
                  }
               }

               if (var7 == null) {
                  var7 = new VehiclePart(this);
               }

               var7.setScriptPart(var13);
               var7.category = var13.category;
               var7.specificItem = var13.specificItem;
               if (var13.container != null && var13.container.contentType == null) {
                  if (var7.getItemContainer() == null) {
                     ItemContainer var15 = new ItemContainer(var13.id, (IsoGridSquare)null, this, 1, 1);
                     var7.setItemContainer(var15);
                     var15.ID = 0;
                  }

                  var7.getItemContainer().Capacity = var13.container.capacity;
               } else {
                  var7.setItemContainer((ItemContainer)null);
               }

               if (var13.door == null) {
                  var7.door = null;
               } else if (var7.door == null) {
                  var7.door = new VehicleDoor(var7);
                  var7.door.init(var13.door);
               }

               if (var13.window == null) {
                  var7.window = null;
               } else if (var7.window == null) {
                  var7.window = new VehicleWindow(var7);
                  var7.window.init(var13.window);
               } else {
                  var7.window.openable = var13.window.openable;
               }

               var7.parent = null;
               if (var7.children != null) {
                  var7.children.clear();
               }

               this.parts.add(var7);
               if ("Battery".equals(var7.getId())) {
                  this.battery = var7;
               }
            }

            VehiclePart var14;
            for(var5 = 0; var5 < this.script.getPartCount(); ++var5) {
               var14 = (VehiclePart)this.parts.get(var5);
               VehicleScript.Part var16 = var14.getScriptPart();
               if (var16.parent != null) {
                  var14.parent = this.getPartById(var16.parent);
                  if (var14.parent != null) {
                     var14.parent.addChild(var14);
                  }
               }
            }

            if (!var2 && !this.loaded) {
               this.frontEndDurability = this.rearEndDurability = 99999;
            }

            this.frontEndDurability = Math.min(this.frontEndDurability, this.script.getFrontEndHealth());
            this.rearEndDurability = Math.min(this.rearEndDurability, this.script.getRearEndHealth());
            this.currentFrontEndDurability = this.frontEndDurability;
            this.currentRearEndDurability = this.rearEndDurability;

            for(var5 = 0; var5 < this.script.getPartCount(); ++var5) {
               var14 = (VehiclePart)this.parts.get(var5);
               var14.setInventoryItem(var14.item);
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

   protected BaseVehicle.ModelInfo setModelVisible(VehiclePart var1, VehicleScript.Model var2, boolean var3) {
      for(int var4 = 0; var4 < this.models.size(); ++var4) {
         BaseVehicle.ModelInfo var5 = (BaseVehicle.ModelInfo)this.models.get(var4);
         if (var5.part == var1 && var5.scriptModel == var2) {
            if (var3) {
               return var5;
            }

            this.models.remove(var4);
            if (this.createdModel) {
               ModelManager.instance.Remove(this);
               ModelManager.instance.Add(this);
            }

            var1.updateFlags = (short)(var1.updateFlags | 64);
            this.updateFlags = (short)(this.updateFlags | 64);
            return null;
         }
      }

      if (var3) {
         BaseVehicle.ModelInfo var6 = new BaseVehicle.ModelInfo();
         var6.part = var1;
         var6.scriptModel = var2;
         var6.wheelIndex = var1.getWheelIndex();
         this.models.add(var6);
         if (this.createdModel) {
            ModelManager.instance.Remove(this);
            ModelManager.instance.Add(this);
         }

         var1.updateFlags = (short)(var1.updateFlags | 64);
         this.updateFlags = (short)(this.updateFlags | 64);
         return var6;
      } else {
         return null;
      }
   }

   protected VehicleScript.Passenger getScriptPassenger(int var1) {
      if (this.getScript() == null) {
         return null;
      } else {
         return var1 >= 0 && var1 < this.getScript().getPassengerCount() ? this.getScript().getPassenger(var1) : null;
      }
   }

   public int getMaxPassengers() {
      return this.passengers.length;
   }

   public boolean setPassenger(int var1, IsoGameCharacter var2, Vector3f var3) {
      if (var1 >= 0 && var1 < this.passengers.length) {
         if (var1 == 0) {
            this.setNeedPartsUpdate(true);
         }

         this.passengers[var1].character = var2;
         this.passengers[var1].offset.set((Vector3fc)var3);
         return true;
      } else {
         return false;
      }
   }

   public boolean clearPassenger(int var1) {
      if (var1 >= 0 && var1 < this.passengers.length) {
         this.passengers[var1].character = null;
         this.passengers[var1].offset.set(0.0F, 0.0F, 0.0F);
         return true;
      } else {
         return false;
      }
   }

   public BaseVehicle.Passenger getPassenger(int var1) {
      return var1 >= 0 && var1 < this.passengers.length ? this.passengers[var1] : null;
   }

   public IsoGameCharacter getCharacter(int var1) {
      BaseVehicle.Passenger var2 = this.getPassenger(var1);
      return var2 != null ? var2.character : null;
   }

   public int getSeat(IsoGameCharacter var1) {
      for(int var2 = 0; var2 < this.getMaxPassengers(); ++var2) {
         if (this.getCharacter(var2) == var1) {
            return var2;
         }
      }

      return -1;
   }

   public boolean isDriver(IsoGameCharacter var1) {
      return this.getSeat(var1) == 0;
   }

   public Vector3f getWorldPos(Vector3f var1, Vector3f var2, VehicleScript var3) {
      Transform var4 = this.getWorldTransform(this.tempTransform);
      var4.origin.set(0.0F, 0.0F, 0.0F);
      var4.basis.scale(var3.getModelScale());
      var2.set((Vector3fc)var1);
      var4.transform(var2);
      float var5 = this.jniTransform.origin.x + WorldSimulation.instance.offsetX;
      float var6 = this.jniTransform.origin.z + WorldSimulation.instance.offsetY;
      float var7 = this.jniTransform.origin.y;
      var2.set(var5 + var2.x, var6 + var2.z, var7 + var2.y);
      return var2;
   }

   public Vector3f getWorldPos(Vector3f var1, Vector3f var2) {
      Transform var3 = this.getWorldTransform(this.tempTransform);
      var3.origin.set(0.0F, 0.0F, 0.0F);
      var3.basis.scale(this.getScript().getModelScale());
      var2.set((Vector3fc)var1);
      var3.transform(var2);
      float var4 = this.jniTransform.origin.x + WorldSimulation.instance.offsetX;
      float var5 = this.jniTransform.origin.z + WorldSimulation.instance.offsetY;
      float var6 = this.jniTransform.origin.y;
      var2.set(var4 + var2.x, var5 + var2.z, var6 + var2.y);
      return var2;
   }

   public Vector3f getLocalPos(Vector3f var1, Vector3f var2) {
      Transform var3 = this.getWorldTransform(this.tempTransform);
      var3.basis.scale(1.0F / this.getScript().getModelScale());
      var3.inverse();
      var2.set(var1.x - WorldSimulation.instance.offsetX, 0.0F, var1.y - WorldSimulation.instance.offsetY);
      var3.transform(var2);
      return var2;
   }

   public Vector3f getLocalPosUnscaled(Vector3f var1, Vector3f var2) {
      Transform var3 = this.getWorldTransform(this.tempTransform);
      var3.inverse();
      var2.set(var1.x - WorldSimulation.instance.offsetX, 0.0F, var1.y - WorldSimulation.instance.offsetY);
      var3.transform(var2);
      return var2;
   }

   public Vector3f getPassengerWorldPos(int var1, Vector3f var2) {
      BaseVehicle.Passenger var3 = this.getPassenger(var1);
      return var3 == null ? null : this.getWorldPos(var3.offset, var2);
   }

   public VehicleScript.Anim getPassengerAnim(int var1, String var2) {
      VehicleScript.Passenger var3 = this.getScriptPassenger(var1);
      if (var3 == null) {
         return null;
      } else {
         for(int var4 = 0; var4 < var3.anims.size(); ++var4) {
            VehicleScript.Anim var5 = (VehicleScript.Anim)var3.anims.get(var4);
            if (var2.equals(var5.id)) {
               return var5;
            }
         }

         return null;
      }
   }

   public VehicleScript.Position getPassengerPosition(int var1, String var2) {
      VehicleScript.Passenger var3 = this.getScriptPassenger(var1);
      return var3 == null ? null : var3.getPositionById(var2);
   }

   public VehiclePart getPassengerDoor(int var1) {
      VehicleScript.Passenger var2 = this.getScriptPassenger(var1);
      return var2 == null ? null : this.getPartById(var2.door);
   }

   public VehiclePart getPassengerDoor2(int var1) {
      VehicleScript.Passenger var2 = this.getScriptPassenger(var1);
      return var2 == null ? null : this.getPartById(var2.door2);
   }

   public boolean haveOneDoorUnlocked() {
      for(int var1 = 0; var1 < this.getPartCount(); ++var1) {
         VehiclePart var2 = this.getPartByIndex(var1);
         if (var2.getDoor() != null && (var2.getId().contains("Left") || var2.getId().contains("Right")) && (!var2.getDoor().isLocked() || var2.getDoor().isOpen())) {
            return true;
         }
      }

      return false;
   }

   public String getPassengerArea(int var1) {
      VehicleScript.Passenger var2 = this.getScriptPassenger(var1);
      return var2 == null ? null : var2.area;
   }

   public void playPassengerAnim(int var1, String var2) {
      IsoGameCharacter var3 = this.getCharacter(var1);
      this.playPassengerAnim(var1, var2, var3);
   }

   public void playPassengerAnim(int var1, String var2, IsoGameCharacter var3) {
      if (var3 != null) {
         VehicleScript.Anim var4 = this.getPassengerAnim(var1, var2);
         if (var4 != null) {
            this.playCharacterAnim(var3, var4);
         }
      }
   }

   public void playPassengerSound(int var1, String var2) {
      VehicleScript.Anim var3 = this.getPassengerAnim(var1, var2);
      if (var3 != null && var3.sound != null) {
         this.playSound(var3.sound);
      }
   }

   public void playPartAnim(VehiclePart var1, String var2) {
      if (this.parts.contains(var1)) {
         VehicleScript.Anim var3 = var1.getAnimById(var2);
         if (var3 != null) {
            ;
         }
      }
   }

   public void playActorAnim(VehiclePart var1, String var2, IsoGameCharacter var3) {
      if (var3 != null) {
         if (this.parts.contains(var1)) {
            VehicleScript.Anim var4 = var1.getAnimById("Actor" + var2);
            if (var4 != null) {
               this.playCharacterAnim(var3, var4);
            }
         }
      }
   }

   private void playCharacterAnim(IsoGameCharacter var1, VehicleScript.Anim var2) {
      var1.PlayAnimUnlooped(var2.anim);
      var1.getSpriteDef().setFrameSpeedPerFrame(var2.rate);
      var1.getLegsSprite().Animate = true;
      Vector3f var3 = this.getForwardVector(this.tempVector3f_1);
      if (var2.angle.lengthSquared() != 0.0F) {
         Matrix4f var4 = this.tempMatrix4fLWJGL_1;
         var4.rotationXYZ((float)Math.toRadians((double)var2.angle.x), (float)Math.toRadians((double)var2.angle.y), (float)Math.toRadians((double)var2.angle.z));
         var3.rotate(var4.getNormalizedRotation(this.tempQuat4f));
      }

      this.tempVector2.set(var3.x, var3.z);
      var1.DirectionFromVector(this.tempVector2);
      var1.angle.x = var3.x;
      var1.angle.y = var3.z;
   }

   public void playPartSound(VehiclePart var1, String var2) {
      if (this.parts.contains(var1)) {
         VehicleScript.Anim var3 = var1.getAnimById(var2);
         if (var3 != null && var3.sound != null) {
            this.playSound(var3.sound);
         }
      }
   }

   public void setCharacterPosition(IsoGameCharacter var1, int var2, String var3) {
      VehicleScript.Passenger var4 = this.getScriptPassenger(var2);
      if (var4 != null) {
         VehicleScript.Position var5 = var4.getPositionById(var3);
         if (var5 != null) {
            if (this.getCharacter(var2) == var1) {
               this.passengers[var2].offset.set((Vector3fc)var5.offset);
            } else {
               Vector3f var6 = this.tempVector3f_1;
               if (var5.area == null) {
                  var6 = this.getWorldPos(var5.offset, this.tempVector3f_1);
               } else {
                  VehicleScript.Area var7 = this.script.getAreaById(var5.area);
                  Vector2 var8 = this.areaPositionWorld(var7);
                  var6.x = var8.x;
                  var6.y = var8.y;
                  var6.z = 0.0F;
               }

               var1.setX(var6.x);
               var1.setY(var6.y);
               var1.setZ(0.0F);
            }

            if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isLocalPlayer()) {
               ((IsoPlayer)var1).dirtyRecalcGridStackTime = 10.0F;
            }

         }
      }
   }

   public void transmitCharacterPosition(int var1, String var2) {
      if (GameClient.bClient) {
         VehicleManager.instance.sendPassengerPosition(this, var1, var2);
      }

   }

   public void setCharacterPositionToAnim(IsoGameCharacter var1, int var2, String var3) {
      VehicleScript.Anim var4 = this.getPassengerAnim(var2, var3);
      if (var4 != null) {
         if (this.getCharacter(var2) == var1) {
            this.passengers[var2].offset.set((Vector3fc)var4.offset);
         } else {
            Vector3f var5 = this.getWorldPos(var4.offset, this.tempVector3f_1);
            var1.setX(var5.x);
            var1.setY(var5.y);
            var1.setZ(0.0F);
         }

      }
   }

   public int getPassengerSwitchSeatCount(int var1) {
      VehicleScript.Passenger var2 = this.getScriptPassenger(var1);
      return var2 == null ? -1 : var2.switchSeats.size();
   }

   public VehicleScript.Passenger.SwitchSeat getPassengerSwitchSeat(int var1, int var2) {
      VehicleScript.Passenger var3 = this.getScriptPassenger(var1);
      if (var3 == null) {
         return null;
      } else {
         return var2 >= 0 && var2 < var3.switchSeats.size() ? (VehicleScript.Passenger.SwitchSeat)var3.switchSeats.get(var2) : null;
      }
   }

   private VehicleScript.Passenger.SwitchSeat getSwitchSeat(int var1, int var2) {
      VehicleScript.Passenger var3 = this.getScriptPassenger(var1);
      if (var3 == null) {
         return null;
      } else {
         for(int var4 = 0; var4 < var3.switchSeats.size(); ++var4) {
            VehicleScript.Passenger.SwitchSeat var5 = (VehicleScript.Passenger.SwitchSeat)var3.switchSeats.get(var4);
            if (var5.seat == var2 && this.getPartForSeatContainer(var2) != null && this.getPartForSeatContainer(var2).getInventoryItem() != null) {
               return var5;
            }
         }

         return null;
      }
   }

   public String getSwitchSeatAnimName(int var1, int var2) {
      VehicleScript.Passenger.SwitchSeat var3 = this.getSwitchSeat(var1, var2);
      return var3 == null ? null : var3.anim;
   }

   public float getSwitchSeatAnimRate(int var1, int var2) {
      VehicleScript.Passenger.SwitchSeat var3 = this.getSwitchSeat(var1, var2);
      return var3 == null ? 0.0F : var3.rate;
   }

   public String getSwitchSeatSound(int var1, int var2) {
      VehicleScript.Passenger.SwitchSeat var3 = this.getSwitchSeat(var1, var2);
      return var3 == null ? null : var3.sound;
   }

   public boolean canSwitchSeat(int var1, int var2) {
      VehicleScript.Passenger.SwitchSeat var3 = this.getSwitchSeat(var1, var2);
      return var3 != null;
   }

   public void switchSeat(IsoGameCharacter var1, int var2) {
      int var3 = this.getSeat(var1);
      if (var3 != -1) {
         this.clearPassenger(var3);
         VehicleScript.Position var4 = this.getPassengerPosition(var2, "inside");
         if (var4 == null) {
            this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
            this.setPassenger(var2, var1, this.tempVector3f_1);
         } else {
            this.setPassenger(var2, var1, var4.offset);
         }

         VehicleManager.instance.sendSwichSeat(this, var2, var1);
      }
   }

   public void switchSeatRSync(IsoGameCharacter var1, int var2) {
      int var3 = this.getSeat(var1);
      if (var3 != -1) {
         this.clearPassenger(var3);
         VehicleScript.Position var4 = this.getPassengerPosition(var2, "inside");
         if (var4 == null) {
            this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
            this.setPassenger(var2, var1, this.tempVector3f_1);
         } else {
            this.setPassenger(var2, var1, var4.offset);
         }

      }
   }

   public void playSwitchSeatAnim(int var1, int var2) {
      IsoGameCharacter var3 = this.getCharacter(var1);
      if (var3 != null) {
         VehicleScript.Passenger.SwitchSeat var4 = this.getSwitchSeat(var1, var2);
         if (var4 != null) {
            var3.PlayAnimUnlooped(var4.anim);
            var3.getSpriteDef().setFrameSpeedPerFrame(var4.rate);
            var3.getLegsSprite().Animate = true;
         }
      }
   }

   public boolean isSeatOccupied(int var1) {
      VehiclePart var2 = this.getPartForSeatContainer(var1);
      if (var2 != null && var2.getItemContainer() != null && !var2.getItemContainer().getItems().isEmpty()) {
         return true;
      } else {
         return this.getCharacter(var1) != null;
      }
   }

   public boolean isSeatInstalled(int var1) {
      VehiclePart var2 = this.getPartForSeatContainer(var1);
      return var2 != null && var2.getInventoryItem() != null;
   }

   public int getBestSeat(IsoGameCharacter var1) {
      if ((int)this.getZ() != (int)var1.getZ()) {
         return -1;
      } else if (var1.DistTo(this) > 5.0F) {
         return -1;
      } else {
         VehicleScript var2 = this.getScript();
         if (var2 == null) {
            return -1;
         } else {
            for(int var3 = 0; var3 < var2.getPassengerCount(); ++var3) {
               if (!this.isEnterBlocked(var1, var3) && !this.isSeatOccupied(var3)) {
                  VehicleScript.Position var4 = this.getPassengerPosition(var3, "outside");
                  Vector3f var5;
                  float var6;
                  float var7;
                  Vector3f var8;
                  Vector2 var9;
                  float var10;
                  if (var4 != null) {
                     var5 = this.getWorldPos(var4.offset, this.tempVector3f_1);
                     var6 = var5.x;
                     var7 = var5.y;
                     var8 = this.tempVector3f_1;
                     var8.set(0.0F, var4.offset.y, var4.offset.z);
                     this.getWorldPos(var8, var8);
                     var9 = this.tempVector2;
                     var9.set(var8.x - var1.getX(), var8.y - var1.getY());
                     var9.normalize();
                     var10 = var9.dot(var1.getAngle());
                     if (var10 > 0.5F && IsoUtils.DistanceTo(var1.getX(), var1.getY(), var6, var7) < 1.0F) {
                        return var3;
                     }
                  }

                  var4 = this.getPassengerPosition(var3, "outside2");
                  if (var4 != null) {
                     var5 = this.getWorldPos(var4.offset, this.tempVector3f_1);
                     var6 = var5.x;
                     var7 = var5.y;
                     var8 = this.tempVector3f_1;
                     var8.set(0.0F, var4.offset.y, var4.offset.z);
                     this.getWorldPos(var8, var8);
                     var9 = this.tempVector2;
                     var9.set(var8.x - var1.getX(), var8.y - var1.getY());
                     var9.normalize();
                     var10 = var9.dot(var1.getAngle());
                     if (var10 > 0.5F && IsoUtils.DistanceTo(var1.getX(), var1.getY(), var6, var7) < 1.0F) {
                        return var3;
                     }
                  }
               }
            }

            return -1;
         }
      }
   }

   public void updateHasExtendOffsetForExit(IsoGameCharacter var1) {
      this.hasExtendOffsetExiting = true;
      this.updateHasExtendOffset(var1);
      this.getPoly();
   }

   public void updateHasExtendOffsetForExitEnd(IsoGameCharacter var1) {
      this.hasExtendOffsetExiting = false;
      this.updateHasExtendOffset(var1);
      this.getPoly();
   }

   public void updateHasExtendOffset(IsoGameCharacter var1) {
      if (var1.getVehicle() == this && !this.hasExtendOffsetExiting) {
         if (!this.hasExtendOffset) {
            this.polyDirty = true;
         }

         this.hasExtendOffset = true;
      } else if ((int)this.getZ() != (int)var1.getZ()) {
         if (!this.hasExtendOffset) {
            this.polyDirty = true;
         }

         this.hasExtendOffset = true;
      } else {
         Vector3f var2 = this.tempVector3f_1;
         var2.set(var1.x, var1.y, this.z);
         this.getLocalPos(var2, var2);
         float var3 = this.script.getModelScale();
         float var4 = 3.0F;
         float var5 = -(this.script.getExtents().x / 2.0F + var4) / var3;
         float var6 = -var5;
         float var7 = -(this.script.getExtents().z / 2.0F + var4) / var3;
         float var8 = -var7;
         if (var2.x >= var5 && var2.x < var6 && var2.z >= var7 && var2.z < var8) {
            if (!this.hasExtendOffset) {
               return;
            }

            byte var9 = 5;
            int var10 = ((int)this.x - var9) / 10;
            int var11 = ((int)this.y - var9) / 10;
            int var12 = (int)Math.ceil((double)((this.x + (float)var9) / 10.0F));
            int var13 = (int)Math.ceil((double)((this.y + (float)var9) / 10.0F));

            for(int var14 = var11; var14 <= var13; ++var14) {
               for(int var15 = var10; var15 <= var12; ++var15) {
                  IsoChunk var16 = GameServer.bServer ? ServerMap.instance.getChunk(var15, var14) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var15 * 10, var14 * 10, 0);
                  if (var16 != null) {
                     for(int var17 = 0; var17 < var16.vehicles.size(); ++var17) {
                        BaseVehicle var18 = (BaseVehicle)var16.vehicles.get(var17);
                        if (this != var18 && this.DistTo(var18) < (float)var9) {
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

   public VehiclePart getUseablePart(IsoGameCharacter var1) {
      if ((int)this.getZ() != (int)var1.getZ()) {
         return null;
      } else if (var1.DistTo(this) > 6.0F) {
         return null;
      } else {
         VehicleScript var2 = this.getScript();
         if (var2 == null) {
            return null;
         } else {
            for(int var3 = 0; var3 < this.parts.size(); ++var3) {
               VehiclePart var4 = (VehiclePart)this.parts.get(var3);
               if (var4.getArea() != null && this.isInArea(var4.getArea(), var1)) {
                  String var5 = var4.getLuaFunction("use");
                  if (var5 != null && !var5.equals("")) {
                     VehicleScript.Area var6 = var2.getAreaById(var4.getArea());
                     if (var6 != null) {
                        Vector2 var7 = this.areaPositionLocal(var6);
                        if (var7 != null) {
                           Vector3f var8 = this.tempVector3f_1;
                           float var9 = var2.getExtents().z / var2.getModelScale();
                           if (!(var7.y >= var9 / 2.0F) && !(var7.y <= -var9 / 2.0F)) {
                              var8.set(0.0F, 0.0F, var7.y);
                           } else {
                              var8.set(var7.x, 0.0F, 0.0F);
                           }

                           this.getWorldPos(var8, var8);
                           Vector2 var10 = this.tempVector2;
                           var10.set(var8.x - var1.getX(), var8.y - var1.getY());
                           var10.normalize();
                           float var11 = var10.dot(var1.getAngle());
                           if (var11 > 0.5F && !PolygonalMap2.instance.lineClearCollide(var1.x, var1.y, var8.x, var8.y, (int)var1.z, this, false, true)) {
                              return var4;
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

   public VehiclePart getClosestWindow(IsoGameCharacter var1) {
      if ((int)this.getZ() != (int)var1.getZ()) {
         return null;
      } else if (var1.DistTo(this) > 5.0F) {
         return null;
      } else {
         for(int var2 = 0; var2 < this.parts.size(); ++var2) {
            VehiclePart var3 = (VehiclePart)this.parts.get(var2);
            if (var3.getWindow() != null && var3.getArea() != null && this.isInArea(var3.getArea(), var1)) {
               VehicleScript.Area var4 = this.script.getAreaById(var3.getArea());
               Vector3f var5 = this.tempVector3f_1;
               float var6 = this.script.getExtents().x / this.script.getModelScale();
               float var7 = this.script.getExtents().z / this.script.getModelScale();
               if (!(var4.y >= var7 / 2.0F) && !(var4.y <= -var7 / 2.0F)) {
                  var5.set(0.0F, 0.0F, var4.y);
               } else {
                  var5.set(var4.x, 0.0F, 0.0F);
               }

               this.getWorldPos(var5, var5);
               Vector2 var8 = this.tempVector2;
               var8.set(var5.x - var1.getX(), var5.y - var1.getY());
               var8.normalize();
               float var9 = var8.dot(var1.getAngle());
               if (var9 > 0.5F) {
                  return var3;
               }
               break;
            }
         }

         return null;
      }
   }

   public void getFacingPosition(IsoGameCharacter var1, Vector2 var2) {
      Vector3f var3 = this.tempVector3f_1.set(var1.getX(), var1.getY(), var1.getZ());
      this.getLocalPos(var3, var3);
      float var4 = this.script.getModelScale();
      float var5 = -this.script.getExtents().x / var4 / 2.0F;
      float var6 = this.script.getExtents().x / var4 / 2.0F;
      float var7 = -this.script.getExtents().z / var4 / 2.0F;
      float var8 = this.script.getExtents().z / var4 / 2.0F;
      float var9 = 0.0F;
      float var10 = 0.0F;
      if (var3.x <= 0.0F && var3.z >= var7 && var3.z <= var8) {
         var10 = var3.z;
      } else if (var3.x > 0.0F && var3.z >= var7 && var3.z <= var8) {
         var10 = var3.z;
      } else if (var3.z <= 0.0F && var3.x >= var5 && var3.x <= var6) {
         var9 = var3.x;
      } else if (var3.z > 0.0F && var3.x >= var5 && var3.x <= var6) {
         var9 = var3.x;
      }

      var3.set(var9, 0.0F, var10);
      this.getWorldPos(var3, var3);
      var2.set(var3.x, var3.y);
   }

   public boolean enter(int var1, IsoGameCharacter var2, Vector3f var3) {
      if (var2 == null) {
         return false;
      } else if (var2.getVehicle() != null && !var2.getVehicle().exit(var2)) {
         return false;
      } else if (this.setPassenger(var1, var2, var3)) {
         var2.setVehicle(this);
         var2.setCollidable(false);
         if (GameClient.bClient) {
            VehicleManager.instance.sendEnter(this, var1, var2);
         }

         if (var2 instanceof IsoPlayer && ((IsoPlayer)var2).isLocalPlayer()) {
            ((IsoPlayer)var2).dirtyRecalcGridStackTime = 10.0F;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean enter(int var1, IsoGameCharacter var2) {
      if (this.getPartForSeatContainer(var1) != null && this.getPartForSeatContainer(var1).getInventoryItem() != null) {
         VehicleScript.Position var3 = this.getPassengerPosition(var1, "outside");
         return var3 != null ? this.enter(var1, var2, var3.offset) : false;
      } else {
         return false;
      }
   }

   public boolean enterRSync(int var1, IsoGameCharacter var2, BaseVehicle var3) {
      if (var2 == null) {
         return false;
      } else {
         VehicleScript.Position var4 = this.getPassengerPosition(var1, "inside");
         if (var4 != null) {
            if (this.setPassenger(var1, var2, var4.offset)) {
               var2.setVehicle(var3);
               var2.setCollidable(false);
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

   public boolean exit(IsoGameCharacter var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.getSeat(var1);
         if (var2 == -1) {
            return false;
         } else if (this.clearPassenger(var2)) {
            var1.setVehicle((BaseVehicle)null);
            var1.setCollidable(true);
            if (GameClient.bClient) {
               VehicleManager.instance.sendExit(this, var1);
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

   public boolean exitRSync(IsoGameCharacter var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.getSeat(var1);
         if (var2 == -1) {
            return false;
         } else if (this.clearPassenger(var2)) {
            var1.setVehicle((BaseVehicle)null);
            var1.setCollidable(true);
            if (GameClient.bClient) {
               LuaEventManager.triggerEvent("OnContainerUpdate");
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean hasRoof(int var1) {
      VehicleScript.Passenger var2 = this.getScriptPassenger(var1);
      return var2 == null ? false : var2.hasRoof;
   }

   public void save(ByteBuffer var1) throws IOException {
      float var2 = 5.0E-4F;
      if (this.getX() < (float)this.square.x + var2) {
         this.setX((float)this.square.x + var2);
      } else if (this.getX() > (float)(this.square.x + 1) - var2) {
         this.setX((float)(this.square.x + 1) - var2);
      }

      if (this.getY() < (float)this.square.y + var2) {
         this.setY((float)this.square.y + var2);
      } else if (this.getY() > (float)(this.square.y + 1) - var2) {
         this.setY((float)(this.square.y + 1) - var2);
      }

      super.save(var1);
      Quaternionf var3 = this.savedRot;
      Transform var4 = this.getWorldTransform(this.tempTransform);
      var4.getRotation(var3);
      var1.putFloat(var3.x);
      var1.putFloat(var3.y);
      var1.putFloat(var3.z);
      var1.putFloat(var3.w);
      GameWindow.WriteStringUTF(var1, this.scriptName);
      var1.putInt(this.skinIndex);
      var1.put((byte)(this.isEngineRunning() ? 1 : 0));
      var1.putInt(this.frontEndDurability);
      var1.putInt(this.rearEndDurability);
      var1.putInt(this.currentFrontEndDurability);
      var1.putInt(this.currentRearEndDurability);
      var1.putInt(this.engineLoudness);
      var1.putInt(this.engineQuality);
      var1.putInt(this.keyId);
      var1.put(this.keySpawned);
      var1.put((byte)(this.headlightsOn ? 1 : 0));
      var1.put((byte)(this.bCreated ? 1 : 0));
      var1.put((byte)(this.soundHornOn ? 1 : 0));
      var1.put((byte)(this.soundBackMoveOn ? 1 : 0));
      var1.put((byte)this.lightbarLightsMode.get());
      var1.put((byte)this.lightbarSirenMode.get());
      var1.putShort((short)this.parts.size());

      for(int var5 = 0; var5 < this.parts.size(); ++var5) {
         VehiclePart var6 = (VehiclePart)this.parts.get(var5);
         var6.save(var1);
      }

      var1.put((byte)(this.keyIsOnDoor ? 1 : 0));
      var1.put((byte)(this.hotwired ? 1 : 0));
      var1.put((byte)(this.hotwiredBroken ? 1 : 0));
      var1.put((byte)(this.keysInIgnition ? 1 : 0));
      var1.putFloat(this.rust);
      var1.putFloat(this.colorHue);
      var1.putFloat(this.colorSaturation);
      var1.putFloat(this.colorValue);
      var1.putInt(this.enginePower);
      var1.putShort(this.VehicleID);
      GameWindow.WriteString((ByteBuffer)var1, (String)null);
      var1.putInt(this.mechanicalID);
      var1.put((byte)(this.alarmed ? 1 : 0));
      var1.putDouble(this.sirenStartTime);
      if (this.getCurrentKey() != null) {
         var1.put((byte)1);
         this.getCurrentKey().save(var1, false);
      } else {
         var1.put((byte)0);
      }

   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      if (this.z < 0.0F) {
         this.z = 0.0F;
      }

      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      float var5 = var1.getFloat();
      float var6 = var1.getFloat();
      this.savedRot.set(var3, var4, var5, var6);
      this.jniTransform.origin.set(this.getX() - WorldSimulation.instance.offsetX, this.getZ(), this.getY() - WorldSimulation.instance.offsetY);
      this.jniTransform.setRotation(this.savedRot);
      this.scriptName = GameWindow.ReadStringUTF(var1);
      this.skinIndex = var1.getInt();
      boolean var7 = var1.get() == 1;
      if (var7) {
         this.engineState = BaseVehicle.engineStateTypes.Running;
      }

      this.frontEndDurability = var1.getInt();
      this.rearEndDurability = var1.getInt();
      this.currentFrontEndDurability = var1.getInt();
      this.currentRearEndDurability = var1.getInt();
      this.engineLoudness = var1.getInt();
      this.engineQuality = var1.getInt();
      this.keyId = var1.getInt();
      this.keySpawned = var1.get();
      this.headlightsOn = var1.get() == 1;
      this.bCreated = var1.get() == 1;
      this.soundHornOn = var1.get() == 1;
      this.soundBackMoveOn = var1.get() == 1;
      this.lightbarLightsMode.set(var1.get());
      this.lightbarSirenMode.set(var1.get());
      short var8 = var1.getShort();

      for(int var9 = 0; var9 < var8; ++var9) {
         VehiclePart var10 = new VehiclePart(this);
         var10.load(var1, var2);
         this.parts.add(var10);
      }

      if (var2 >= 112) {
         this.keyIsOnDoor = var1.get() == 1;
         this.hotwired = var1.get() == 1;
         this.hotwiredBroken = var1.get() == 1;
         this.keysInIgnition = var1.get() == 1;
      }

      if (var2 >= 116) {
         this.rust = var1.getFloat();
         this.colorHue = var1.getFloat();
         this.colorSaturation = var1.getFloat();
         this.colorValue = var1.getFloat();
      }

      if (var2 >= 117) {
         this.enginePower = var1.getInt();
      }

      if (var2 >= 120) {
         var1.getShort();
      }

      String var11;
      if (var2 >= 122) {
         var11 = GameWindow.ReadString(var1);
         this.mechanicalID = var1.getInt();
      }

      if (var2 >= 124) {
         this.alarmed = var1.get() == 1;
      }

      if (var2 >= 129) {
         this.sirenStartTime = var1.getDouble();
      }

      if (var2 >= 133 && var1.get() == 1) {
         var11 = GameWindow.ReadString(var1);
         var1.get();
         InventoryItem var12 = InventoryItemFactory.CreateItem(var11);
         var12.load(var1, var2, false);
         this.setCurrentKey(var12);
      }

      this.loaded = true;
   }

   public void softReset() {
      this.keySpawned = 0;
      this.keyIsOnDoor = false;
      this.keysInIgnition = false;
      this.currentKey = null;
      this.engineState = BaseVehicle.engineStateTypes.Idle;

      for(int var1 = 0; var1 < this.parts.size(); ++var1) {
         VehiclePart var2 = (VehiclePart)this.parts.get(var1);
         if (var2.getItemContainer() != null) {
            var2.getItemContainer().clear();
            if (Rand.Next(100) <= 100) {
               this.randomizeContainer(var2);
            }

            var2.getItemContainer().setExplored(true);
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
                  VehicleType var1 = VehicleType.getTypeFromName(this.getVehicleType());
                  int var2 = var1 == null ? 70 : var1.getChanceToSpawnKey();
                  if (Rand.Next(100) <= var2) {
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
         float var1 = Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + 0.3F + 1.0F;
         int var2 = (int)Math.ceil((double)var1);

         int var4;
         for(int var3 = -var2; var3 < var2; ++var3) {
            for(var4 = -var2; var4 < var2; ++var4) {
               IsoGridSquare var5 = this.getCell().getGridSquare((double)(this.x + (float)var4), (double)(this.y + (float)var3), (double)this.z);
               if (var5 != null) {
                  int var6;
                  Vector2 var8;
                  for(var6 = 0; var6 < var5.getObjects().size(); ++var6) {
                     IsoObject var7 = (IsoObject)var5.getObjects().get(var6);
                     if (!(var7 instanceof IsoWorldInventoryObject)) {
                        var8 = null;
                        if (!this.brekingObjectsList.contains(var7) && var7 != null && var7.getProperties() != null) {
                           if (var7.getProperties().Is("CarSlowFactor")) {
                              var8 = this.testCollisionWithObject(var7, 0.3F);
                           }

                           if (var8 != null) {
                              this.brekingObjectsList.add(var7);
                              if (!GameClient.bClient) {
                                 var7.Collision(var8, this);
                              }
                           }

                           if (var7.getProperties().Is("HitByCar")) {
                              var8 = this.testCollisionWithObject(var7, 0.3F);
                           }

                           if (var8 != null && !GameClient.bClient) {
                              var7.Collision(var8, this);
                           }
                        }
                     }
                  }

                  IsoMovingObject var12;
                  for(var6 = 0; var6 < var5.getMovingObjects().size(); ++var6) {
                     var12 = (IsoMovingObject)var5.getMovingObjects().get(var6);
                     if (var12 instanceof IsoZombie) {
                        IsoZombie var14 = (IsoZombie)var12;
                        if (var14.isOnFloor() && (var14.bCrawling || var14.legsSprite.CurrentAnim != null && var14.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
                           this.testCollisionWithProneCharacter(var12, var14.angle.x, var14.angle.y, false);
                        }

                        var14.setVehicle4TestCollision(this);
                     }

                     if (GameClient.bClient && var12 instanceof IsoPlayer && var12 != this.getDriver()) {
                        IsoPlayer var15 = (IsoPlayer)var12;
                        var15.setVehicle4TestCollision(this);
                     }
                  }

                  for(var6 = 0; var6 < var5.getStaticMovingObjects().size(); ++var6) {
                     var12 = (IsoMovingObject)var5.getStaticMovingObjects().get(var6);
                     if (var12 instanceof IsoDeadBody) {
                        var8 = var12.dir.ToVector();
                        this.testCollisionWithProneCharacter(var12, var8.x, var8.y, true);
                     }
                  }
               }
            }
         }

         float var10 = -999.0F;

         for(var4 = 0; var4 < this.brekingObjectsList.size(); ++var4) {
            IsoObject var11 = (IsoObject)this.brekingObjectsList.get(var4);
            Vector2 var13 = this.testCollisionWithObject(var11, 1.0F);
            if (var13 != null && var11.getSquare().getObjects().contains(var11)) {
               if (var10 < var11.GetVehicleSlowFactor(this)) {
                  var10 = var11.GetVehicleSlowFactor(this);
               }
            } else {
               this.brekingObjectsList.remove(var11);
               var11.UnCollision(this);
            }
         }

         if (var10 != -999.0F) {
            Bullet.brekingVehicle(this.VehicleID, var10);
         } else {
            Bullet.brekingVehicle(this.VehicleID, 0.0F);
         }

      }
   }

   public void damageObjects(float var1) {
      if (this.isEngineRunning()) {
         float var2 = Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + 0.3F + 1.0F;
         int var3 = (int)Math.ceil((double)var2);

         for(int var4 = -var3; var4 < var3; ++var4) {
            for(int var5 = -var3; var5 < var3; ++var5) {
               IsoGridSquare var6 = this.getCell().getGridSquare((double)(this.x + (float)var5), (double)(this.y + (float)var4), (double)this.z);
               if (var6 != null) {
                  for(int var7 = 0; var7 < var6.getObjects().size(); ++var7) {
                     IsoObject var8 = (IsoObject)var6.getObjects().get(var7);
                     Vector2 var9 = null;
                     if (var8 instanceof IsoTree) {
                        var9 = this.testCollisionWithObject(var8, 2.0F);
                        if (var9 != null) {
                           var8.setRenderEffect(RenderEffectType.Hit_Tree_Shudder);
                        }
                     }

                     if (var9 == null && var8 instanceof IsoWindow) {
                        var9 = this.testCollisionWithObject(var8, 1.0F);
                     }

                     if (var9 == null && var8.sprite != null && (var8.sprite.getProperties().Is("HitByCar") || var8.sprite.getProperties().Is("CarSlowFactor"))) {
                        var9 = this.testCollisionWithObject(var8, 1.0F);
                     }

                     IsoGridSquare var10;
                     if (var9 == null) {
                        var10 = this.getCell().getGridSquare((double)(this.x + (float)var5), (double)(this.y + (float)var4), 1.0D);
                        if (var10 != null && var6.getProperties().getFlags() != null && var10.getHasTypes().isSet(IsoObjectType.lightswitch)) {
                           var9 = this.testCollisionWithObject(var8, 1.0F);
                        }
                     }

                     if (var9 == null) {
                        var10 = this.getCell().getGridSquare((double)(this.x + (float)var5), (double)(this.y + (float)var4), 0.0D);
                        if (var10 != null && var6.getProperties().getFlags() != null && var10.getHasTypes().isSet(IsoObjectType.lightswitch)) {
                           var9 = this.testCollisionWithObject(var8, 1.0F);
                        }
                     }

                     if (var9 != null) {
                        var8.Hit(var9, this, var1);
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
            BaseVehicle.VehicleImpulse var1 = this.impulseFromServer;
            Vector3f var3;
            if (var1 != null && var1.enable) {
               var1.enable = false;
               float var2 = 1.0F;
               Bullet.applyCentralForceToVehicle(this.VehicleID, var1.impulse.x * var2, var1.impulse.y * var2, var1.impulse.z * var2);
               var3 = var1.rel_pos.cross(var1.impulse, this.tempVector3f_1);
               Bullet.applyTorqueToVehicle(this.VehicleID, var3.x * var2, var3.y * var2, var3.z * var2);
            }

            int var4;
            int var5;
            float var24;
            if (!this.impulseFromHitZombie.isEmpty()) {
               Vector3f var13 = this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
               var3 = this.tempVector3f_2.set(0.0F, 0.0F, 0.0F);
               var4 = this.impulseFromHitZombie.size();

               for(var5 = 0; var5 < var4; ++var5) {
                  var1 = (BaseVehicle.VehicleImpulse)this.impulseFromHitZombie.get(var5);
                  var13.add(var1.impulse);
                  var3.add(var1.rel_pos.cross(var1.impulse, this.tempVector3f_3));
                  var1.release();
               }

               var24 = 7.0F * this.getMass();
               if (var13.lengthSquared() > var24 * var24) {
                  var13.mul(var24 / var13.length());
               }

               this.impulseFromHitZombie.clear();
               float var6 = 30.0F;
               Bullet.applyCentralForceToVehicle(this.VehicleID, var13.x * var6, var13.y * var6, var13.z * var6);
               Bullet.applyTorqueToVehicle(this.VehicleID, var3.x * var6, var3.y * var6, var3.z * var6);
               if (GameServer.bServer) {
               }
            }

            int var14;
            float var15;
            for(var14 = 0; var14 < this.impulseFromSquishedZombie.length; ++var14) {
               var1 = this.impulseFromSquishedZombie[var14];
               if (var1 != null && var1.enable) {
                  var15 = 30.0F;
                  Bullet.applyCentralForceToVehicle(this.VehicleID, var1.impulse.x * var15, var1.impulse.y * var15, var1.impulse.z * var15);
                  Vector3f var18 = var1.rel_pos.cross(var1.impulse, this.tempVector3f_1);
                  Bullet.applyTorqueToVehicle(this.VehicleID, var18.x * var15, var18.y * var15, var18.z * var15);
               }
            }

            if (System.currentTimeMillis() - this.engineCheckTime > 1000L && !GameClient.bClient) {
               this.engineCheckTime = System.currentTimeMillis();
               if (!GameClient.bClient) {
                  if (this.engineState != BaseVehicle.engineStateTypes.Idle) {
                     var14 = (int)((double)this.engineLoudness * this.engineSpeed / 2500.0D);
                     double var16 = Math.min(this.getEngineSpeed(), 2000.0D);
                     var14 = (int)((double)var14 * (1.0D + var16 / 4000.0D));
                     if (Rand.Next((int)(120.0F * GameTime.instance.getInvMultiplier())) == 0) {
                        WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), var14, var14 / 40, false);
                     }

                     if (Rand.Next((int)(35.0F * GameTime.instance.getInvMultiplier())) == 0) {
                        WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), var14 / 2, var14 / 40, false);
                     }

                     if (Rand.Next((int)(2.0F * GameTime.instance.getInvMultiplier())) == 0) {
                        WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), var14 / 4, var14 / 40, false);
                     }

                     WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), var14 / 6, var14 / 40, false);
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
                  VehiclePart var17 = this.getPartById("GasTank");
                  if (this.getBatteryCharge() <= 0.1F) {
                     this.engineDoStartingFailedNoPower();
                  } else if ((var17 == null || !(var17.getContainerContentAmount() <= 0.0F)) && !(this.getBatteryCharge() <= 0.0F)) {
                     int var20 = 0;
                     if (this.engineQuality < 65 && ClimateManager.getInstance().getAirTemperatureForSquare(this.getSquare()) <= 2.0F) {
                        var20 = Math.min(Math.abs((int)ClimateManager.getInstance().getAirTemperatureForSquare(this.getSquare()) - 2) * 2, 30);
                     }

                     if (!SandboxOptions.instance.VehicleEasyUse.getValue() && this.engineQuality < 100 && Rand.Next(100 - this.engineQuality + var20 + 50) <= 30) {
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
            IsoGridSquare var19 = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
            if (var19 == null) {
               var15 = 5.0E-4F;
               var4 = this.chunk.wx * 10;
               var5 = this.chunk.wy * 10;
               int var26 = var4 + 10;
               int var7 = var5 + 10;
               float var8 = this.x;
               float var9 = this.y;
               this.x = Math.max(this.x, (float)var4 + var15);
               this.x = Math.min(this.x, (float)var26 - var15);
               this.y = Math.max(this.y, (float)var5 + var15);
               this.y = Math.min(this.y, (float)var7 - var15);
               this.z = 0.2F;
               Transform var10 = this.tempTransform;
               Transform var11 = this.tempTransform2;
               this.getWorldTransform(var10);
               var11.basis.set((Matrix3fc)var10.basis);
               var11.origin.set(this.x - WorldSimulation.instance.offsetX, this.z, this.y - WorldSimulation.instance.offsetY);
               this.setWorldTransform(var11);
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
               boolean var25 = false;
            }

            this.updateTransform();
            if (this.jniIsCollide) {
               this.jniIsCollide = false;
               var3 = this.tempVector3f_1;
               if (GameServer.bServer) {
                  var3.set((Vector3fc)this.netLinearVelocity);
               } else {
                  var3.set((Vector3fc)this.jniLinearVelocity);
               }

               var3.negate();
               var3.add(this.lastLinearVelocity);
               float var23 = Math.abs(var3.length());
               if (var23 > 2.0F) {
                  if (this.lastLinearVelocity.length() < 6.0F) {
                     var23 /= 3.0F;
                  }

                  this.jniTransform.getRotation(this.tempQuat4f);
                  this.tempQuat4f.invert(this.tempQuat4f);
                  if (this.lastLinearVelocity.rotate(this.tempQuat4f).z < 0.0F) {
                     var23 *= -1.0F;
                  }

                  if (Core.bDebug) {
                     DebugLog.log("CRASH lastSpeed=" + this.lastLinearVelocity.length() + " speed=" + var3 + " delta=" + var23 + " netLinearVelocity=" + this.netLinearVelocity.length());
                  }

                  var24 = var3.normalize().dot(this.getForwardVector(this.tempVector3f_2));
                  this.crash(Math.abs(var23 * 3.0F), var24 > 0.0F);
                  this.damageObjects(Math.abs(var23) * 30.0F);
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

         int var12;
         for(var12 = 0; var12 < this.impulseFromSquishedZombie.length; ++var12) {
            BaseVehicle.VehicleImpulse var21 = this.impulseFromSquishedZombie[var12];
            if (var21 != null) {
               var21.enable = false;
            }
         }

         this.updateSounds();
         this.brekingObjects();
         if (this.sprite != null && this.sprite.hasActiveModel()) {
            this.sprite.modelSlot.UpdateLights();
         }

         for(var12 = 0; var12 < IsoPlayer.numPlayers; ++var12) {
            if (this.current == null || !this.current.lighting[var12].bCanSee()) {
               this.targetAlpha[var12] = 0.0F;
            }

            IsoPlayer var22 = IsoPlayer.players[var12];
            if (var22 != null && this.DistToSquared(var22) < 225.0F) {
               this.targetAlpha[var12] = 1.0F;
            }
         }

         for(var12 = 0; var12 < this.getScript().getPassengerCount(); ++var12) {
            if (this.getCharacter(var12) != null) {
               this.getPassengerWorldPos(var12, this.tempVector3f_1);
               this.getCharacter(var12).setX(this.tempVector3f_1.x);
               this.getCharacter(var12).setY(this.tempVector3f_1.y);
               this.getCharacter(var12).setZ(this.tempVector3f_1.z * 0.0F);
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

   private boolean isNullChunk(int var1, int var2) {
      if (!IsoWorld.instance.getMetaGrid().isValidChunk(var1, var2)) {
         return false;
      } else if (GameClient.bClient && !ClientServerMap.isChunkLoaded(var1, var2)) {
         return true;
      } else if (GameClient.bClient && !PassengerMap.isChunkLoaded(this, var1, var2)) {
         return true;
      } else {
         return this.getCell().getChunk(var1, var2) == null;
      }
   }

   public boolean isInvalidChunkAround() {
      Vector3f var1 = this.getLinearVelocity(this.tempVector3f_1);
      float var2 = Math.abs(var1.x);
      float var3 = Math.abs(var1.z);
      boolean var4 = var1.x < 0.0F && var2 > var3;
      boolean var5 = var1.x > 0.0F && var2 > var3;
      boolean var6 = var1.z < 0.0F && var3 > var2;
      boolean var7 = var1.z > 0.0F && var3 > var2;
      if (IsoChunkMap.ChunkGridWidth <= 7) {
         if (IsoChunkMap.ChunkGridWidth <= 4) {
            return false;
         } else if (var5 && this.isNullChunk(this.chunk.wx + 1, this.chunk.wy)) {
            return true;
         } else if (var4 && this.isNullChunk(this.chunk.wx - 1, this.chunk.wy)) {
            return true;
         } else if (var7 && this.isNullChunk(this.chunk.wx, this.chunk.wy + 1)) {
            return true;
         } else if (var6 && this.isNullChunk(this.chunk.wx, this.chunk.wy - 1)) {
            return true;
         } else {
            return false;
         }
      } else if (!var5 || !this.isNullChunk(this.chunk.wx + 1, this.chunk.wy) && !this.isNullChunk(this.chunk.wx + 2, this.chunk.wy)) {
         if (var4 && (this.isNullChunk(this.chunk.wx - 1, this.chunk.wy) || this.isNullChunk(this.chunk.wx - 2, this.chunk.wy))) {
            return true;
         } else if (var7 && (this.isNullChunk(this.chunk.wx, this.chunk.wy + 1) || this.isNullChunk(this.chunk.wx, this.chunk.wy + 2))) {
            return true;
         } else if (!var6 || !this.isNullChunk(this.chunk.wx, this.chunk.wy - 1) && !this.isNullChunk(this.chunk.wx, this.chunk.wy - 2)) {
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
         for(int var1 = (int)this.z; var1 >= 0; --var1) {
            this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, var1);
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

   public void saveChange(String var1, KahluaTable var2, ByteBuffer var3) {
      super.saveChange(var1, var2, var3);
   }

   public void loadChange(String var1, ByteBuffer var2) {
      super.loadChange(var1, var2);
   }

   public void authorizationClientForecast(boolean var1) {
      if (var1 && this.getDriver() == null) {
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

   public void authorizationServerCollide(int var1, boolean var2) {
      if (this.netPlayerAuthorization != 3) {
         if (var2) {
            this.netPlayerAuthorization = 1;
            this.netPlayerId = var1;
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

   public boolean authorizationServerOnOwnerData(UdpConnection var1) {
      boolean var2 = false;
      if (this.netPlayerAuthorization == 0) {
         return false;
      } else {
         for(int var3 = 0; var3 < var1.players.length; ++var3) {
            if (var1.players[var3] != null && var1.players[var3].OnlineID == this.netPlayerId) {
               var2 = true;
               break;
            }
         }

         if (this.getDriver() != null) {
            this.netPlayerTimeout = 30;
         }

         return var2;
      }
   }

   public void netPlayerServerSendAuthorisation(ByteBuffer var1) {
      var1.put(this.netPlayerAuthorization);
      var1.putInt(this.netPlayerId);
   }

   public void netPlayerFromServerUpdate(byte var1, int var2) {
      if (IsoPlayer.getPlayerIndex() >= 0 && IsoPlayer.players[IsoPlayer.getPlayerIndex()] != null && (var1 != this.netPlayerAuthorization || this.netPlayerId != var2)) {
         if (var1 == 3) {
            if (var2 == IsoPlayer.players[IsoPlayer.getPlayerIndex()].OnlineID) {
               this.netPlayerAuthorization = 3;
               this.netPlayerId = var2;
               Bullet.setVehicleStatic(this.VehicleID, false);
            } else if (this.netPlayerAuthorization != 4) {
               this.netPlayerAuthorization = 4;
               this.netPlayerId = var2;
               Bullet.setVehicleStatic(this.VehicleID, true);
            }
         } else if (var1 == 1) {
            if (var2 == IsoPlayer.players[IsoPlayer.getPlayerIndex()].OnlineID) {
               this.netPlayerAuthorization = 1;
               this.netPlayerId = var2;
               Bullet.setVehicleStatic(this.VehicleID, false);
            } else if (this.netPlayerAuthorization != 2) {
               this.netPlayerAuthorization = 2;
               this.netPlayerId = var2;
               Bullet.setVehicleStatic(this.VehicleID, true);
            }
         } else {
            this.netPlayerAuthorization = 0;
            this.netPlayerId = -1;
            Bullet.setVehicleStatic(this.VehicleID, false);
         }
      }
   }

   public Transform getWorldTransform(Transform var1) {
      var1.set(this.jniTransform);
      return var1;
   }

   public void setWorldTransform(Transform var1) {
      this.jniTransform.set(var1);
      Quaternionf var2 = this.tempQuat4f;
      var1.getRotation(var2);
      Bullet.teleportVehicle(this.VehicleID, var1.origin.x + WorldSimulation.instance.offsetX, var1.origin.z + WorldSimulation.instance.offsetY, var1.origin.y, var2.x, var2.y, var2.z, var2.w);
   }

   public void flipUpright() {
      Transform var1 = this.tempTransform;
      var1.set(this.jniTransform);
      Quaternionf var2 = new Quaternionf();
      var2.setAngleAxis(0.0F, _UNIT_Y.x, _UNIT_Y.y, _UNIT_Y.z);
      var1.setRotation(var2);
      this.setWorldTransform(var1);
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
         PolygonalMap2.VehiclePoly var1 = this.getPolyPlusRadius();
         Vector3f var10 = this.tempVector3f_2;
         Vector3f var11 = this.tempVector3f_3;
         Vector3f var12 = this.getLocalPos(var10.set(var1.x1, var1.y1, var1.z), var11);
         float var2 = (float)((int)(var12.x * 100.0F)) / 100.0F;
         float var3 = (float)((int)(var12.z * 100.0F)) / 100.0F;
         var12 = this.getLocalPos(var10.set(var1.x2, var1.y2, var1.z), var11);
         float var4 = (float)((int)(var12.x * 100.0F)) / 100.0F;
         float var5 = (float)((int)(var12.z * 100.0F)) / 100.0F;
         var12 = this.getLocalPos(var10.set(var1.x3, var1.y3, var1.z), var11);
         float var6 = (float)((int)(var12.x * 100.0F)) / 100.0F;
         float var7 = (float)((int)(var12.z * 100.0F)) / 100.0F;
         var12 = this.getLocalPos(var10.set(var1.x4, var1.y4, var1.z), var11);
         float var8 = (float)((int)(var12.x * 100.0F)) / 100.0F;
         float var9 = (float)((int)(var12.z * 100.0F)) / 100.0F;
         this.polyPlusRadiusMinX = Math.min(var2, Math.min(var4, Math.min(var6, var8)));
         this.polyPlusRadiusMaxX = Math.max(var2, Math.max(var4, Math.max(var6, var8)));
         this.polyPlusRadiusMinY = Math.min(var3, Math.min(var5, Math.min(var7, var9)));
         this.polyPlusRadiusMaxY = Math.max(var3, Math.max(var5, Math.max(var7, var9)));
      }
   }

   public Vector3f getForwardVector(Vector3f var1) {
      byte var2 = 2;
      return this.jniTransform.basis.getColumn(var2, var1);
   }

   public float getCurrentSpeedKmHour() {
      return this.jniSpeed;
   }

   public Vector3f getLinearVelocity(Vector3f var1) {
      if (GameServer.bServer) {
         var1.set((Vector3fc)this.netLinearVelocity);
         return var1;
      } else {
         var1.set((Vector3fc)this.jniLinearVelocity);
         return var1;
      }
   }

   private void updateTransform() {
      if (this.sprite.modelSlot != null) {
         Vector3f var1 = this.getForwardVector(this.tempVector3f_1);
         float var2 = (float)(-Math.atan2((double)var1.z, (double)var1.x));
         float var3 = this.getScript().getModelScale();
         Transform var4 = this.getWorldTransform(this.tempTransform);
         var4.getRotation(this.tempQuat4f);
         float var5 = this.getScript().getModel().offset.y() * (1.0F - Math.abs(this.tempQuat4f.x()));
         LEMMY_FLIP_FIX = false;
         var4.origin.x = var4.origin.y = var4.origin.z = 0.0F;
         Vector3f var10000 = var4.origin;
         var10000.y += var5;
         var4.getMatrix(this.renderTransform);
         if (!LEMMY_FLIP_FIX) {
            this.renderTransform.transpose();
         }

         for(int var6 = 0; var6 < this.models.size(); ++var6) {
            BaseVehicle.ModelInfo var7 = (BaseVehicle.ModelInfo)this.models.get(var6);
            if (var7.wheelIndex == -1) {
               VehicleScript.Model var14 = var7.scriptModel;
               var7.renderOrigin.set(var14.offset.x + this.script.getCenterOfMassOffset().x(), var14.offset.y + this.script.getCenterOfMassOffset().y(), var14.offset.z + this.script.getCenterOfMassOffset().z());
               Matrix4f var15 = this.tempMatrix4fLWJGL_1;
               this.tempMatrix4f.translation(var7.renderOrigin).transpose();
               var15.mulGeneric(this.tempMatrix4f, var15);
               var7.renderTransform.scaling(var3);
               var15.mul((Matrix4fc)var7.renderTransform, var7.renderTransform);
               var7.renderTransform.mulGeneric(this.renderTransform, var7.renderTransform);
            } else {
               BaseVehicle.WheelInfo var8 = this.wheelInfo[var7.wheelIndex];
               float var9 = -var8.steering;
               if (LEMMY_FLIP_FIX) {
                  var9 *= -1.0F;
               }

               float var10 = -var8.rotation;
               VehicleScript.Wheel var11 = this.getScript().getWheel(var7.wheelIndex);
               BaseVehicle.VehicleImpulse var12 = var7.wheelIndex < this.impulseFromSquishedZombie.length ? this.impulseFromSquishedZombie[var7.wheelIndex] : null;
               float var13 = var12 != null && var12.enable ? 0.05F : 0.0F;
               if (var8.suspensionLength == 0.0F) {
                  var7.renderOrigin.set(var11.offset.x, var11.offset.y - this.script.getSuspensionRestLength() / 3.0F - this.script.getCenterOfMassOffset().y() + var5 / var3, var11.offset.z);
               } else {
                  var7.renderOrigin.set(var11.offset.x, var11.offset.y - var8.suspensionLength / 2.0F - this.script.getCenterOfMassOffset().y() + (var5 + var13) / var3, var11.offset.z);
               }

               this.setModelTransform(var7, var2, var9, var10, var3);
               var7.renderTransform.mulGeneric(this.renderTransform, var7.renderTransform);
            }
         }

         this.renderTransform.scale(var3);
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
         float[] var1 = vehicleParams;
         float var2 = 0.05F;
         if (Bullet.getOwnVehiclePhysics(this.VehicleID, var1) == 0 && (Math.abs(var1[0] - this.x) > var2 || Math.abs(var1[1] - this.y) > var2 || Math.abs(var1[2] - this.z) > var2)) {
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
      IsoGameCharacter var1 = this.getCharacter(0);
      return var1 != null && var1 == IsoPlayer.players[0];
   }

   public int getJoypad() {
      IsoGameCharacter var1 = this.getCharacter(0);
      return var1 != null && var1 instanceof IsoPlayer ? ((IsoPlayer)var1).JoypadBind : -1;
   }

   public void Damage(float var1) {
      this.crash(var1, true);
   }

   public void crash(float var1, boolean var2) {
      if (GameClient.bClient) {
         SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0F, 20.0F, 1.0F, true);
         GameClient.instance.sendClientCommandV((IsoPlayer)null, "vehicle", "crash", "vehicle", this.getId(), "amount", var1, "front", var2);
      } else {
         float var3 = 1.3F;
         float var4 = var1;
         switch(SandboxOptions.instance.CarDamageOnImpact.getValue()) {
         case 1:
            var3 = 1.9F;
            break;
         case 2:
            var3 = 1.6F;
         case 3:
         default:
            break;
         case 4:
            var3 = 1.1F;
            break;
         case 5:
            var3 = 0.9F;
         }

         var1 = Math.abs(var1) / var3;
         if (var2) {
            this.addDamageFront((int)var1);
         } else {
            this.addDamageRear((int)Math.abs(var1 / var3));
         }

         this.damagePlayers(Math.abs(var4));
         SoundManager.instance.PlayWorldSound("VehicleCrash", this.square, 1.0F, 20.0F, 1.0F, true);
      }
   }

   private void addDamageFrontHitAChr(int var1) {
      VehiclePart var2 = this.getPartById("EngineDoor");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      if (var1 > 12) {
         var2 = this.getPartById("Windshield");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

   }

   private void addDamageRearHitAChr(int var1) {
      VehiclePart var2 = this.getPartById("TruckBed");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.setCondition(var2.getCondition() - Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         var2.doInventoryItemStats(var2.getInventoryItem(), 0);
         this.transmitPartCondition(var2);
      }

      var2 = this.getPartById("DoorRear");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      var2 = this.getPartById("TrunkDoor");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      if (var1 > 12) {
         var2 = this.getPartById("WindshieldRear");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(var1);
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightRearLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightRearRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

   }

   private void addDamageFront(int var1) {
      this.currentFrontEndDurability -= var1;
      VehiclePart var2 = this.getPartById("EngineDoor");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      var2 = this.getPartById("Windshield");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      if (Rand.Next(4) == 0) {
         var2 = this.getPartById("DoorFrontLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }

         var2 = this.getPartById("WindowFrontLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }
      }

      if (Rand.Next(4) == 0) {
         var2 = this.getPartById("DoorFrontRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }

         var2 = this.getPartById("WindowFrontRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

      if (Rand.Next(45) < var1) {
         var2 = this.getPartById("Muffler");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 30), var1 - 20));
         }
      }

   }

   private void addDamageRear(int var1) {
      this.currentRearEndDurability -= var1;
      VehiclePart var2 = this.getPartById("TruckBed");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.setCondition(var2.getCondition() - Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         var2.doInventoryItemStats(var2.getInventoryItem(), 0);
         this.transmitPartCondition(var2);
      }

      var2 = this.getPartById("DoorRear");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      var2 = this.getPartById("TrunkDoor");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
      }

      var2 = this.getPartById("WindshieldRear");
      if (var2 != null && var2.getInventoryItem() != null) {
         var2.damage(var1);
      }

      if (Rand.Next(4) == 0) {
         var2 = this.getPartById("DoorRearLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }

         var2 = this.getPartById("WindowRearLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }
      }

      if (Rand.Next(4) == 0) {
         var2 = this.getPartById("DoorRearRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }

         var2 = this.getPartById("WindowRearRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightRearLeft");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("HeadlightRearRight");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.setInventoryItem((InventoryItem)null);
            this.transmitPartItem(var2);
         }
      }

      if (Rand.Next(20) < var1) {
         var2 = this.getPartById("Muffler");
         if (var2 != null && var2.getInventoryItem() != null) {
            var2.damage(Rand.Next(Math.max(1, var1 - 5), var1 + 5));
         }
      }

   }

   private float clamp(float var1, float var2, float var3) {
      if (var1 < var2) {
         var1 = var2;
      }

      if (var1 > var3) {
         var1 = var3;
      }

      return var1;
   }

   public boolean isCharacterAdjacentTo(IsoGameCharacter var1) {
      if ((int)var1.z != (int)this.z) {
         return false;
      } else {
         Transform var2 = this.getWorldTransform(this.tempTransform);
         var2.inverse();
         Vector3f var3 = this.tempVector3f_1;
         var3.set(var1.x - WorldSimulation.instance.offsetX, 0.0F, var1.y - WorldSimulation.instance.offsetY);
         var2.transform(var3);
         float var4 = -this.script.getExtents().x / 2.0F;
         float var5 = this.script.getExtents().x / 2.0F;
         float var6 = -this.script.getExtents().z / 2.0F;
         float var7 = this.script.getExtents().z / 2.0F;
         this.initPolyPlusRadiusBounds();
         var4 = this.polyPlusRadiusMinX * this.script.getModelScale() + 0.3F;
         var5 = this.polyPlusRadiusMaxX * this.script.getModelScale() - 0.3F;
         var6 = this.polyPlusRadiusMinY * this.script.getModelScale() + 0.3F;
         var7 = this.polyPlusRadiusMaxY * this.script.getModelScale() - 0.3F;
         return var3.x >= var4 - 0.4F && var3.x < var5 + 0.4F && var3.z >= var6 - 0.4F && var3.z < var7 + 0.4F;
      }
   }

   public Vector2 testCollisionWithCharacter(IsoGameCharacter var1, float var2) {
      if (this.physics == null) {
         return null;
      } else if (this.DistToProper(var1) > Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + var2 + 1.0F) {
         return null;
      } else {
         Vector3f var3 = this.tempVector3f_1;
         var3.set(var1.nx, var1.ny, 0.0F);
         this.getLocalPosUnscaled(var3, var3);
         float var4 = -this.script.getExtents().x / 2.0F;
         float var5 = this.script.getExtents().x / 2.0F;
         float var6 = -this.script.getExtents().z / 2.0F;
         float var7 = this.script.getExtents().z / 2.0F;
         float var8;
         float var9;
         float var10;
         float var11;
         Vector3f var10000;
         if (var3.x > var4 && var3.x < var5 && var3.z > var6 && var3.z < var7) {
            var8 = var3.x - var4;
            var9 = var5 - var3.x;
            var10 = var3.z - var6;
            var11 = var7 - var3.z;
            if (var8 < var9 && var8 < var10 && var8 < var11) {
               this.tempVector3f_1.set(var4 - var2 - 0.015F, 0.0F, var3.z);
            } else if (var9 < var8 && var9 < var10 && var9 < var11) {
               this.tempVector3f_1.set(var5 + var2 + 0.015F, 0.0F, var3.z);
            } else if (var10 < var8 && var10 < var9 && var10 < var11) {
               this.tempVector3f_1.set(var3.x, 0.0F, var6 - var2 - 0.015F);
            } else if (var11 < var8 && var11 < var9 && var11 < var10) {
               this.tempVector3f_1.set(var3.x, 0.0F, var7 + var2 + 0.015F);
            }

            Transform var14 = this.getWorldTransform(this.tempTransform);
            var14.origin.set(0.0F, 0.0F, 0.0F);
            var14.transform(this.tempVector3f_1);
            var10000 = this.tempVector3f_1;
            var10000.x += this.getX();
            var10000 = this.tempVector3f_1;
            var10000.z += this.getY();
            this.collideX = this.tempVector3f_1.x;
            this.collideY = this.tempVector3f_1.z;
            return this.tempVector2.set(this.tempVector3f_1.x, this.tempVector3f_1.z);
         } else {
            var8 = this.clamp(var3.x, var4, var5);
            var9 = this.clamp(var3.z, var6, var7);
            var10 = var3.x - var8;
            var11 = var3.z - var9;
            float var12 = var10 * var10 + var11 * var11;
            if (var12 < var2 * var2) {
               if (var10 == 0.0F && var11 == 0.0F) {
                  return this.tempVector2.set(-1.0F, -1.0F);
               } else {
                  this.tempVector3f_1.set(var10, 0.0F, var11);
                  this.tempVector3f_1.normalize();
                  this.tempVector3f_1.mul(var2 + 0.015F);
                  var10000 = this.tempVector3f_1;
                  var10000.x += var8;
                  var10000 = this.tempVector3f_1;
                  var10000.z += var9;
                  Transform var13 = this.getWorldTransform(this.tempTransform);
                  var13.origin.set(0.0F, 0.0F, 0.0F);
                  var13.transform(this.tempVector3f_1);
                  var10000 = this.tempVector3f_1;
                  var10000.x += this.getX();
                  var10000 = this.tempVector3f_1;
                  var10000.z += this.getY();
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

   public int testCollisionWithProneCharacter(IsoMovingObject var1, float var2, float var3, boolean var4) {
      if (this.physics == null) {
         return 0;
      } else {
         float var5 = 0.3F;
         if (this.DistToProper(var1) > Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + var5 + 1.0F) {
            return 0;
         } else {
            float var6 = var1.x + var2 * 0.5F;
            float var7 = var1.y + var3 * 0.5F;
            float var8 = var1.x - var2 * 0.65F;
            float var9 = var1.y - var3 * 0.65F;
            int var10 = 0;

            for(int var11 = 0; var11 < this.script.getWheelCount(); ++var11) {
               VehicleScript.Wheel var12 = this.script.getWheel(var11);
               boolean var13 = true;

               for(int var14 = 0; var14 < this.models.size(); ++var14) {
                  BaseVehicle.ModelInfo var15 = (BaseVehicle.ModelInfo)this.models.get(var14);
                  if (var15.wheelIndex == var11) {
                     this.tempVector3f_1.set((Vector3fc)var12.offset);
                     Vector3f var10000 = this.tempVector3f_1;
                     var10000.y -= this.wheelInfo[var11].suspensionLength / this.script.getModelScale();
                     this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1);
                     if (this.tempVector3f_1.z > this.script.getWheel(var11).radius + 0.05F) {
                        var13 = false;
                     }
                     break;
                  }
               }

               if (var13) {
                  Vector3f var26 = this.tempVector3f_1.set(var12.offset.x, var12.offset.y, var12.offset.z);
                  this.getWorldPos(var26, var26);
                  float var19 = var26.x;
                  float var20 = var26.y;
                  double var21 = (double)((var19 - var8) * (var6 - var8) + (var20 - var9) * (var7 - var9)) / (Math.pow((double)(var6 - var8), 2.0D) + Math.pow((double)(var7 - var9), 2.0D));
                  float var23;
                  float var24;
                  if (var21 <= 0.0D) {
                     var23 = var8;
                     var24 = var9;
                  } else if (var21 >= 1.0D) {
                     var23 = var6;
                     var24 = var7;
                  } else {
                     var23 = var8 + (var6 - var8) * (float)var21;
                     var24 = var9 + (var7 - var9) * (float)var21;
                  }

                  if (!(IsoUtils.DistanceToSquared(var26.x, var26.y, var23, var24) > var12.radius * var12.radius)) {
                     if (var4 && this.jniSpeed > 10.0F) {
                        if (GameServer.bServer && var1 instanceof IsoZombie) {
                           ((IsoZombie)var1).thumpFlag = 1;
                        } else {
                           SoundManager.instance.PlayWorldSound("VehicleRunOverBody", var1.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
                        }
                     }

                     if (var11 < this.impulseFromSquishedZombie.length) {
                        if (this.impulseFromSquishedZombie[var11] == null) {
                           this.impulseFromSquishedZombie[var11] = new BaseVehicle.VehicleImpulse();
                        }

                        this.impulseFromSquishedZombie[var11].impulse.set(0.0F, 1.0F, 0.0F);
                        float var25 = Math.max(this.jniSpeed, 20.0F) / 20.0F;
                        this.impulseFromSquishedZombie[var11].impulse.mul(0.065F * this.getMass() * var25);
                        this.impulseFromSquishedZombie[var11].rel_pos.set(var26.x - this.x, 0.0F, var26.y - this.y);
                        this.impulseFromSquishedZombie[var11].enable = true;
                        ++var10;
                     }
                  }
               }
            }

            return var10;
         }
      }
   }

   public Vector2 testCollisionWithObject(IsoObject var1, float var2) {
      if (this.physics == null) {
         return null;
      } else if (var1.square == null) {
         return null;
      } else if (this.DistToProper(var1) > Math.max(this.script.getExtents().x / 2.0F, this.script.getExtents().z / 2.0F) + var2 + 1.0F) {
         return null;
      } else {
         Vector3f var3 = this.tempVector3f_1;
         var3.set(var1.getX(), var1.getY(), 0.0F);
         this.getLocalPosUnscaled(var3, var3);
         float var4 = -this.script.getExtents().x / 2.0F;
         float var5 = this.script.getExtents().x / 2.0F;
         float var6 = -this.script.getExtents().z / 2.0F;
         float var7 = this.script.getExtents().z / 2.0F;
         float var8;
         float var9;
         float var10;
         float var11;
         Vector3f var10000;
         if (var3.x > var4 && var3.x < var5 && var3.z > var6 && var3.z < var7) {
            var8 = var3.x - var4;
            var9 = var5 - var3.x;
            var10 = var3.z - var6;
            var11 = var7 - var3.z;
            if (var8 < var9 && var8 < var10 && var8 < var11) {
               this.tempVector3f_1.set(var4 - var2 - 0.015F, 0.0F, var3.z);
            } else if (var9 < var8 && var9 < var10 && var9 < var11) {
               this.tempVector3f_1.set(var5 + var2 + 0.015F, 0.0F, var3.z);
            } else if (var10 < var8 && var10 < var9 && var10 < var11) {
               this.tempVector3f_1.set(var3.x, 0.0F, var6 - var2 - 0.015F);
            } else if (var11 < var8 && var11 < var9 && var11 < var10) {
               this.tempVector3f_1.set(var3.x, 0.0F, var7 + var2 + 0.015F);
            }

            Transform var14 = this.getWorldTransform(this.tempTransform);
            var14.origin.set(0.0F, 0.0F, 0.0F);
            var14.transform(this.tempVector3f_1);
            var10000 = this.tempVector3f_1;
            var10000.x += this.getX();
            var10000 = this.tempVector3f_1;
            var10000.z += this.getY();
            this.collideX = this.tempVector3f_1.x;
            this.collideY = this.tempVector3f_1.z;
            return this.tempVector2.set(this.tempVector3f_1.x, this.tempVector3f_1.z);
         } else {
            var8 = this.clamp(var3.x, var4, var5);
            var9 = this.clamp(var3.z, var6, var7);
            var10 = var3.x - var8;
            var11 = var3.z - var9;
            float var12 = var10 * var10 + var11 * var11;
            if (var12 < var2 * var2) {
               if (var10 == 0.0F && var11 == 0.0F) {
                  return this.tempVector2.set(-1.0F, -1.0F);
               } else {
                  this.tempVector3f_1.set(var10, 0.0F, var11);
                  this.tempVector3f_1.normalize();
                  this.tempVector3f_1.mul(var2 + 0.015F);
                  var10000 = this.tempVector3f_1;
                  var10000.x += var8;
                  var10000 = this.tempVector3f_1;
                  var10000.z += var9;
                  Transform var13 = this.getWorldTransform(this.tempTransform);
                  var13.origin.set(0.0F, 0.0F, 0.0F);
                  var13.transform(this.tempVector3f_1);
                  var10000 = this.tempVector3f_1;
                  var10000.x += this.getX();
                  var10000 = this.tempVector3f_1;
                  var10000.z += this.getY();
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

   public boolean testCollisionWithVehicle(BaseVehicle var1) {
      VehicleScript var2 = this.script;
      if (var2 == null) {
         var2 = ScriptManager.instance.getVehicle(this.scriptName);
      }

      VehicleScript var3 = var1.script;
      if (var3 == null) {
         var3 = ScriptManager.instance.getVehicle(var1.scriptName);
      }

      if (testVecs1[0] == null) {
         for(int var4 = 0; var4 < testVecs1.length; ++var4) {
            testVecs1[var4] = new Vector2();
            testVecs2[var4] = new Vector2();
         }
      }

      float var5 = 1.0F / var2.getModelScale() / 2.0F;
      this.tempVector3f_1.set(var2.getExtents().x * var5, 0.0F, var2.getExtents().z * var5);
      this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var2);
      testVecs1[0].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      this.tempVector3f_1.set(-var2.getExtents().x * var5, 0.0F, var2.getExtents().z * var5);
      this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var2);
      testVecs1[1].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      this.tempVector3f_1.set(-var2.getExtents().x * var5, 0.0F, -var2.getExtents().z * var5);
      this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var2);
      testVecs1[2].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      this.tempVector3f_1.set(var2.getExtents().x * var5, 0.0F, -var2.getExtents().z * var5);
      this.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var2);
      testVecs1[3].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      var5 = 1.0F / var3.getModelScale() / 2.0F;
      this.tempVector3f_1.set(var3.getExtents().x * var5, 0.0F, var3.getExtents().z * var5);
      var1.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var3);
      testVecs2[0].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      this.tempVector3f_1.set(-var3.getExtents().x * var5, 0.0F, var3.getExtents().z * var5);
      var1.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var3);
      testVecs2[1].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      this.tempVector3f_1.set(-var3.getExtents().x * var5, 0.0F, -var3.getExtents().z * var5);
      var1.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var3);
      testVecs2[2].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      this.tempVector3f_1.set(var3.getExtents().x * var5, 0.0F, -var3.getExtents().z * var5);
      var1.getWorldPos(this.tempVector3f_1, this.tempVector3f_1, var3);
      testVecs2[3].set(this.tempVector3f_1.x, this.tempVector3f_1.y);
      return QuadranglesIntersection.IsQuadranglesAreIntersected(testVecs1, testVecs2);
   }

   public void ApplyImpulse(IsoObject var1, float var2) {
      BaseVehicle.VehicleImpulse var3 = BaseVehicle.VehicleImpulse.alloc();
      var3.impulse.set(this.x - var1.getX(), 0.0F, this.y - var1.getY());
      var3.impulse.normalize();
      var3.impulse.mul(var2);
      var3.rel_pos.set(var1.getX() - this.x, 0.0F, var1.getY() - this.y);
      this.impulseFromHitZombie.add(var3);
   }

   public void hitCharacter(IsoGameCharacter var1) {
      if (var1.getStateMachine().getCurrent() != StaggerBackState.instance() && var1.getStateMachine().getCurrent() != StaggerBackDieState.instance() && var1.getStateMachine().getCurrent() != JustDieState.instance()) {
         if (GameClient.bClient) {
            if (var1.clientIgnoreCollision + 400L > System.currentTimeMillis()) {
               return;
            }

            if (var1.legsSprite.CurrentAnim != null && (var1.legsSprite.CurrentAnim.name.equals("ZombieStaggerBack") || var1.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
               return;
            }
         }

         if (!(Math.abs(var1.x - this.x) < 0.01F) && !(Math.abs(var1.y - this.y) < 0.01F)) {
            float var2 = 15.0F;
            Vector3f var3 = this.getLinearVelocity(this.tempVector3f_1);
            var3.y = 0.0F;
            float var4 = var3.length();
            var4 = Math.min(var4, var2);
            if (!(var4 < 0.05F)) {
               if (GameServer.bServer) {
                  if (var1 instanceof IsoZombie) {
                     ((IsoZombie)var1).thumpFlag = 1;
                  }
               } else if (!GameClient.bClient) {
                  SoundManager.instance.PlayWorldSound("VehicleHitCharacter", var1.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
               }

               Vector3f var5 = this.tempVector3f_2;
               var5.set(this.x - var1.x, 0.0F, this.y - var1.y);
               var5.normalize();
               var3.normalize();
               float var6 = var3.dot(var5);
               var3.mul(var4);
               if (var6 < 0.0F && !GameServer.bServer) {
                  this.ApplyImpulse(var1, this.getMass() * 7.0F * var4 / var2 * Math.abs(var6));
               }

               if (GameClient.bClient) {
                  var1.clientIgnoreCollision = System.currentTimeMillis();
               } else {
                  var5.normalize();
                  var5.mul(3.0F * var4 / var2);
                  var1.Hit(this, var4 + this.physics.clientForce / this.getMass(), var6, this.tempVector2.set(-var5.x, -var5.z));
                  if (this.getCurrentSpeedKmHour() >= 30.0F) {
                     var4 = this.getCurrentSpeedKmHour() / 5.0F;
                     if (var4 > 0.0F) {
                        this.addDamageFrontHitAChr((int)var4);
                     } else {
                        this.addDamageRearHitAChr(Math.abs((int)var4));
                     }
                  }

               }
            }
         }
      }
   }

   public boolean blocked(int var1, int var2, int var3) {
      if (!this.removedFromWorld && this.current != null && this.getController() != null) {
         if (this.getController() == null) {
            return false;
         } else if (var3 != (int)this.getZ()) {
            return false;
         } else if (IsoUtils.DistanceTo2D((float)var1 + 0.5F, (float)var2 + 0.5F, this.x, this.y) > 5.0F) {
            return false;
         } else {
            float var4 = 0.3F;
            Transform var5 = this.tempTransform3;
            this.getWorldTransform(var5);
            var5.inverse();
            Vector3f var6 = this.tempVector3f_3;
            var6.set((float)var1 + 0.5F - WorldSimulation.instance.offsetX, 0.0F, (float)var2 + 0.5F - WorldSimulation.instance.offsetY);
            var5.transform(var6);
            float var7 = this.clamp(var6.x, -this.script.getExtents().x / 2.0F, this.script.getExtents().x / 2.0F);
            float var8 = this.clamp(var6.z, -this.script.getExtents().z / 2.0F, this.script.getExtents().z / 2.0F);
            float var9 = var6.x - var7;
            float var10 = var6.z - var8;
            float var11 = var9 * var9 + var10 * var10;
            return var11 < var4 * var4;
         }
      } else {
         return false;
      }
   }

   public boolean isIntersectingSquare(int var1, int var2, int var3) {
      if (var3 != (int)this.getZ()) {
         return false;
      } else if (!this.removedFromWorld && this.current != null && this.getController() != null) {
         tempPoly.x1 = tempPoly.x4 = (float)var1;
         tempPoly.y1 = tempPoly.y2 = (float)var2;
         tempPoly.x2 = tempPoly.x3 = (float)(var1 + 1);
         tempPoly.y3 = tempPoly.y4 = (float)(var2 + 1);
         return PolyPolyIntersect.intersects(tempPoly, this.getPoly());
      } else {
         return false;
      }
   }

   public boolean isIntersectingSquareWithShadow(int var1, int var2, int var3) {
      if (var3 != (int)this.getZ()) {
         return false;
      } else if (!this.removedFromWorld && this.current != null && this.getController() != null) {
         tempPoly.x1 = tempPoly.x4 = (float)var1;
         tempPoly.y1 = tempPoly.y2 = (float)var2;
         tempPoly.x2 = tempPoly.x3 = (float)(var1 + 1);
         tempPoly.y3 = tempPoly.y4 = (float)(var2 + 1);
         return PolyPolyIntersect.intersects(tempPoly, this.shadowCoord);
      } else {
         return false;
      }
   }

   public boolean circleIntersects(float var1, float var2, float var3, float var4) {
      if (this.getController() == null) {
         return false;
      } else if ((int)var3 != (int)this.getZ()) {
         return false;
      } else if (IsoUtils.DistanceTo2D(var1, var2, this.x, this.y) > 5.0F) {
         return false;
      } else {
         Vector3f var5 = this.tempVector3f_3;
         var5.set(var1, var2, var3);
         this.getLocalPosUnscaled(var5, var5);
         float var6 = -this.script.getExtents().x / 2.0F;
         float var7 = this.script.getExtents().x / 2.0F;
         float var8 = -this.script.getExtents().z / 2.0F;
         float var9 = this.script.getExtents().z / 2.0F;
         if (var5.x > var6 && var5.x < var7 && var5.z > var8 && var5.z < var9) {
            return true;
         } else {
            float var10 = this.clamp(var5.x, var6, var7);
            float var11 = this.clamp(var5.z, var8, var9);
            float var12 = var5.x - var10;
            float var13 = var5.z - var11;
            float var14 = var12 * var12 + var13 * var13;
            return var14 < var4 * var4;
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
      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      VehiclePart var5 = this.getPartById("HeadlightLeft");
      if (var5 != null && var5.getInventoryItem() != null) {
         var1 = true;
      }

      var5 = this.getPartById("HeadlightRight");
      if (var5 != null && var5.getInventoryItem() != null) {
         var2 = true;
      }

      var5 = this.getPartById("HeadlightRearLeft");
      if (var5 != null && var5.getInventoryItem() != null) {
         var4 = true;
      }

      var5 = this.getPartById("HeadlightRearRight");
      if (var5 != null && var5.getInventoryItem() != null) {
         var3 = true;
      }

      if (this.headlightsOn && this.getBatteryCharge() > 0.0F) {
         if (var2) {
            this.sprite.modelSlot.model.textureLightsEnables2.m10(1.0F);
         } else {
            this.sprite.modelSlot.model.textureLightsEnables2.m10(0.0F);
         }

         if (var1) {
            this.sprite.modelSlot.model.textureLightsEnables2.m20(1.0F);
         } else {
            this.sprite.modelSlot.model.textureLightsEnables2.m20(0.0F);
         }

         if (var3) {
            this.sprite.modelSlot.model.textureLightsEnables2.m30(1.0F);
         } else {
            this.sprite.modelSlot.model.textureLightsEnables2.m30(0.0F);
         }

         if (var4) {
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
            switch(this.lightbarLightsMode.getLightTexIndex()) {
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

   private void checkDamage(VehiclePart var1, String var2, boolean var3) {
      try {
         Method var4 = this.sprite.modelSlot.model.textureDamage1Enables1.getClass().getMethod(var2, Float.TYPE);
         var4.invoke(this.sprite.modelSlot.model.textureDamage1Enables1, 0.0F);
         Method var5 = this.sprite.modelSlot.model.textureDamage2Enables1.getClass().getMethod(var2, Float.TYPE);
         var5.invoke(this.sprite.modelSlot.model.textureDamage2Enables1, 0.0F);
         Method var6 = this.sprite.modelSlot.model.textureUninstall1.getClass().getMethod(var2, Float.TYPE);
         var6.invoke(this.sprite.modelSlot.model.textureUninstall1, 0.0F);
         if (var1 != null && var1.getInventoryItem() != null) {
            if (var1.getInventoryItem().getCondition() < 60 && var1.getInventoryItem().getCondition() >= 40) {
               var4.invoke(this.sprite.modelSlot.model.textureDamage1Enables1, 1.0F);
            }

            if (var1.getInventoryItem().getCondition() < 40) {
               var5.invoke(this.sprite.modelSlot.model.textureDamage2Enables1, 1.0F);
            }

            if (var1.window != null && var1.window.isOpen() && var3) {
               var6.invoke(this.sprite.modelSlot.model.textureUninstall1, 1.0F);
            }
         } else if (var1 != null && var3) {
            var6.invoke(this.sprite.modelSlot.model.textureUninstall1, 1.0F);
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   private void checkDamage2(VehiclePart var1, String var2, boolean var3) {
      try {
         Method var4 = this.sprite.modelSlot.model.textureDamage1Enables2.getClass().getMethod(var2, Float.TYPE);
         var4.invoke(this.sprite.modelSlot.model.textureDamage1Enables2, 0.0F);
         Method var5 = this.sprite.modelSlot.model.textureDamage2Enables2.getClass().getMethod(var2, Float.TYPE);
         var5.invoke(this.sprite.modelSlot.model.textureDamage2Enables2, 0.0F);
         Method var6 = this.sprite.modelSlot.model.textureUninstall2.getClass().getMethod(var2, Float.TYPE);
         var6.invoke(this.sprite.modelSlot.model.textureUninstall2, 0.0F);
         if (var1 != null && var1.getInventoryItem() != null) {
            if (var1.getInventoryItem().getCondition() < 60 && var1.getInventoryItem().getCondition() >= 40) {
               var4.invoke(this.sprite.modelSlot.model.textureDamage1Enables2, 1.0F);
            }

            if (var1.getInventoryItem().getCondition() < 40) {
               var5.invoke(this.sprite.modelSlot.model.textureDamage2Enables2, 1.0F);
            }

            if (var1.window != null && var1.window.isOpen() && var3) {
               var6.invoke(this.sprite.modelSlot.model.textureUninstall2, 1.0F);
            }
         } else if (var1 != null && var3) {
            var6.invoke(this.sprite.modelSlot.model.textureUninstall2, 1.0F);
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   private void checkUninstall2(VehiclePart var1, String var2) {
      try {
         Method var3 = this.sprite.modelSlot.model.textureUninstall2.getClass().getMethod(var2, Float.TYPE);
         var3.invoke(this.sprite.modelSlot.model.textureUninstall2, 0.0F);
         if (var1 != null && var1.getInventoryItem() == null) {
            var3.invoke(this.sprite.modelSlot.model.textureUninstall2, 1.0F);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
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
      VehiclePart var1 = this.getPartById("TrunkDoor");
      if (var1 != null) {
         this.checkDamage2(var1, "m22", true);
         if (var1.scriptPart.hasLightsRear) {
            this.checkUninstall2(var1, "m30");
            this.checkUninstall2(var1, "m01");
            this.checkUninstall2(var1, "m11");
            this.checkUninstall2(var1, "m21");
         }
      } else {
         var1 = this.getPartById("DoorRear");
         if (var1 != null) {
            this.checkDamage2(var1, "m22", true);
            if (var1.scriptPart.hasLightsRear) {
               this.checkUninstall2(var1, "m30");
               this.checkUninstall2(var1, "m01");
               this.checkUninstall2(var1, "m11");
               this.checkUninstall2(var1, "m21");
            }
         }
      }

   }

   private void doWindowDamage() {
      this.checkDamage(this.getPartById("WindowFrontLeft"), "m02", true);
      this.checkDamage(this.getPartById("WindowFrontRight"), "m21", true);
      VehiclePart var1 = this.getPartById("WindowRearLeft");
      if (var1 != null) {
         this.checkDamage(var1, "m12", true);
      } else {
         var1 = this.getPartById("WindowMiddleLeft");
         if (var1 != null) {
            this.checkDamage(var1, "m12", true);
         }
      }

      var1 = this.getPartById("WindowRearRight");
      if (var1 != null) {
         this.checkDamage(var1, "m31", true);
      } else {
         var1 = this.getPartById("WindowMiddleRight");
         if (var1 != null) {
            this.checkDamage(var1, "m31", true);
         }
      }

      this.checkDamage(this.getPartById("Windshield"), "m22", true);
      this.checkDamage(this.getPartById("WindshieldRear"), "m32", true);
   }

   private void doDoorDamage() {
      this.checkDamage(this.getPartById("DoorFrontLeft"), "m01", true);
      this.checkDamage(this.getPartById("DoorFrontRight"), "m20", true);
      VehiclePart var1 = this.getPartById("DoorRearLeft");
      if (var1 != null) {
         this.checkDamage(var1, "m11", true);
      } else {
         var1 = this.getPartById("DoorMiddleLeft");
         if (var1 != null) {
            this.checkDamage(var1, "m11", true);
         }
      }

      var1 = this.getPartById("DoorRearRight");
      if (var1 != null) {
         this.checkDamage(var1, "m30", true);
      } else {
         var1 = this.getPartById("DoorMiddleRight");
         if (var1 != null) {
            this.checkDamage(var1, "m30", true);
         }
      }

   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      if (this.script != null) {
         if (this.physics != null) {
            this.physics.debug();
         }

         int var6 = IsoCamera.frameState.playerIndex;
         if (this.square.lighting[var6].bSeen()) {
            if (this.square.lighting[var6].bCanSee()) {
               this.targetAlpha[var6] = 1.0F;
            } else {
               this.targetAlpha[var6] = 0.0F;
               if (IsoCamera.frameState.CamCharacter != null && IsoCamera.frameState.CamCharacterRoom == null && this.DistToSquared(IsoCamera.frameState.CamCharacter) < 225.0F) {
                  this.targetAlpha[var6] = 1.0F;
               }
            }

            this.renderShadow();
            if (this.sprite.hasActiveModel()) {
               int var7 = SpriteRenderer.instance.state.index;
               if (this.sprite.modelSlot.model.xfrm[var7] == null) {
                  this.sprite.modelSlot.model.xfrm[var7] = new Matrix4f();
                  this.sprite.modelSlot.model.origin[var7] = new Vector3f();
                  this.sprite.modelSlot.model.worldPos[var7] = new Vector3f();
               }

               this.sprite.modelSlot.model.xfrm[var7].set((Matrix4fc)this.renderTransform);
               this.sprite.modelSlot.model.worldPos[var7].set(this.getX(), this.getY(), this.getZ());
               this.sprite.modelSlot.model.alpha = this.alpha[IsoPlayer.getPlayerIndex()];

               for(int var8 = 0; var8 < this.models.size(); ++var8) {
                  BaseVehicle.ModelInfo var9 = (BaseVehicle.ModelInfo)this.models.get(var8);
                  if (((ModelInstance)this.sprite.modelSlot.sub.get(var8)).xfrm[var7] == null) {
                     ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).xfrm[var7] = new Matrix4f();
                     ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).origin[var7] = new Vector3f();
                     ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).worldPos[var7] = new Vector3f();
                  }

                  ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).xfrm[var7].set((Matrix4fc)var9.renderTransform);
                  ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).origin[var7].set((Vector3fc)var9.renderOrigin);
                  ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).worldPos[var7].set(this.getX(), this.getY(), this.getZ());
                  ((ModelInstance)this.sprite.modelSlot.sub.get(var8)).alpha = this.alpha[IsoPlayer.getPlayerIndex()];
               }

               this.updateLights();
               Transform var15 = this.tempTransform;
               this.tempVector3f_1.set(0.0F, 0.0F, 0.0F);
               this.tempVector3f_1.set(-this.getScript().getCenterOfMassOffset().x, -this.getScript().getCenterOfMassOffset().y, -this.getScript().getCenterOfMassOffset().z);
               Vector3f var17 = this.tempVector3f_1;
               this.getWorldTransform(var15);
               var15.transform(var17);
               var17.set(var17.x + WorldSimulation.instance.offsetX, var17.z + WorldSimulation.instance.offsetY, var17.y);
               var1 = var17.x;
               var2 = var17.y;
               var3 = var17.z;
               float var10 = IsoUtils.XToScreen(var1 + this.sprite.def.offX, var2 + this.sprite.def.offY, Math.max(var3, 0.0F) + this.sprite.def.offZ, 0);
               float var11 = IsoUtils.YToScreen(var1 + this.sprite.def.offX, var2 + this.sprite.def.offY, Math.max(var3, 0.0F) + this.sprite.def.offZ, 0);
               if (var3 < 0.0F) {
                  var11 -= var3 * 96.0F * (float)Core.TileScale;
               }

               var10 = (float)((int)var10 + IsoSprite.globalOffsetX);
               var11 = (float)((int)var11 + IsoSprite.globalOffsetY);
               int var12 = ModelManager.instance.bitmap.getTexture().getWidth() * Core.TileScale;
               int var13 = -ModelManager.instance.bitmap.getTexture().getHeight() * Core.TileScale;
               if (LEMMY_FLIP_FIX) {
                  var13 *= -1;
               }

               float var10000 = var10 - (float)(var12 / 2);
               var11 += (float)(36 * Core.TileScale);
               if (LEMMY_FLIP_FIX) {
                  var11 -= (float)ModelManager.instance.bitmap.getTexture().getHeight();
               }

               var4.a = this.alpha[IsoPlayer.getPlayerIndex()];
               inf.a = var4.a;
               inf.r = var4.r;
               inf.g = var4.g;
               inf.b = var4.b;
               this.sprite.renderVehicle(this.def, this, var1, var2, var3, this.dir, 0.0F, 0.0F, inf, true);
            }

            float var14 = 2.0F;
            float var16 = 1.5F;
            int var10001;
            float[] var20;
            if (this.alpha[IsoPlayer.getPlayerIndex()] < this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
               var20 = this.alpha;
               var10001 = IsoPlayer.getPlayerIndex();
               var20[var10001] += alphaStep * var14;
               if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
                  this.alpha[IsoPlayer.getPlayerIndex()] = this.targetAlpha[IsoPlayer.getPlayerIndex()];
               }
            } else if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
               var20 = this.alpha;
               var10001 = IsoPlayer.getPlayerIndex();
               var20[var10001] -= alphaStep / var16;
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
               for(int var18 = 0; var18 < IsoPlayer.numPlayers; ++var18) {
                  IsoPlayer var19 = IsoPlayer.players[var18];
                  if (var19 != null && !var19.isDead()) {
                     this.updateHasExtendOffset(var19);
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
      for(int var1 = 0; var1 < this.parts.size(); ++var1) {
         VehiclePart var2 = (VehiclePart)this.parts.get(var1);
         if (var2.chatElement != null && var2.chatElement.getHasChatToDisplay()) {
            float var3 = IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0);
            float var4 = IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0);
            var3 = var3 - IsoCamera.getOffX() - this.offsetX;
            var4 = var4 - IsoCamera.getOffY() - this.offsetY;
            var3 += (float)(32 * Core.TileScale);
            var4 += (float)(20 * Core.TileScale);
            var3 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
            var4 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
            var2.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)var3, (int)var4);
         }
      }

   }

   private void renderShadow() {
      if (this.physics != null) {
         IsoSprite var1 = IsoGameCharacter.DropShadow;
         if (var1 != null && this.getCurrentSquare() != null) {
            float var2 = 0.6F * this.alpha[IsoPlayer.getPlayerIndex()];
            ColorInfo var3 = this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo();
            var2 *= (var3.r + var3.g + var3.b) / 3.0F;
            PolygonalMap2.VehiclePoly var4 = this.getPoly();
            SpriteRenderer.instance.renderPoly(vehicleShadow, (int)IsoUtils.XToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x1, this.shadowCoord.y1, 0.0F, 0), (int)IsoUtils.XToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x2, this.shadowCoord.y2, 0.0F, 0), (int)IsoUtils.XToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x3, this.shadowCoord.y3, 0.0F, 0), (int)IsoUtils.XToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), (int)IsoUtils.YToScreenExact(this.shadowCoord.x4, this.shadowCoord.y4, 0.0F, 0), 1.0F, 1.0F, 1.0F, 0.8F * var2);
         }

      }
   }

   public boolean isEnterBlocked(IsoGameCharacter var1, int var2) {
      return this.isExitBlocked(var2);
   }

   public boolean isExitBlocked(int var1) {
      VehicleScript.Position var2 = this.getPassengerPosition(var1, "inside");
      VehicleScript.Position var3 = this.getPassengerPosition(var1, "outside");
      if (var2 != null && var3 != null) {
         Vector3f var4 = this.getWorldPos(var3.offset, this.tempVector3f_1);
         if (var3.area != null) {
            VehicleScript.Area var5 = this.script.getAreaById(var3.area);
            Vector2 var6 = this.areaPositionWorld(var5);
            if (var6 != null) {
               var4.x = var6.x;
               var4.y = var6.y;
            }
         }

         var4.z = 0.0F;
         Vector3f var7 = this.getWorldPos(var2.offset, this.tempVector3f_2);
         return PolygonalMap2.instance.lineClearCollide(var7.x, var7.y, var4.x, var4.y, (int)this.z, this, false, false);
      } else {
         return true;
      }
   }

   public boolean isPassengerUseDoor2(IsoGameCharacter var1, int var2) {
      VehicleScript.Position var3 = this.getPassengerPosition(var2, "outside2");
      if (var3 != null) {
         Vector3f var4 = this.getWorldPos(var3.offset, new Vector3f());
         var4.sub(var1.x, var1.y, var1.z);
         if (var4.length() < 2.0F) {
            return true;
         }
      }

      return false;
   }

   public boolean isEnterBlocked2(IsoGameCharacter var1, int var2) {
      return this.isExitBlocked2(var2);
   }

   public boolean isExitBlocked2(int var1) {
      VehicleScript.Position var2 = this.getPassengerPosition(var1, "inside");
      VehicleScript.Position var3 = this.getPassengerPosition(var1, "outside2");
      if (var2 != null && var3 != null) {
         Vector3f var4 = this.getWorldPos(var3.offset, this.tempVector3f_1);
         var4.z = 0.0F;
         Vector3f var5 = this.getWorldPos(var2.offset, this.tempVector3f_2);
         return PolygonalMap2.instance.lineClearCollide(var5.x, var5.y, var4.x, var4.y, (int)this.z, this, false, false);
      } else {
         return true;
      }
   }

   private void renderExits() {
      int var1 = Core.TileScale;

      for(int var2 = 0; var2 < this.getMaxPassengers(); ++var2) {
         VehicleScript.Position var3 = this.getPassengerPosition(var2, "inside");
         VehicleScript.Position var4 = this.getPassengerPosition(var2, "outside");
         if (var3 != null && var4 != null) {
            float var5 = 0.3F;
            Vector3f var6 = this.getWorldPos(var4.offset, this.tempVector3f_1);
            Vector3f var7 = this.getWorldPos(var3.offset, this.tempVector3f_2);
            int var8 = (int)Math.floor((double)(var6.x - var5));
            int var9 = (int)Math.floor((double)(var6.x + var5));
            int var10 = (int)Math.floor((double)(var6.y - var5));
            int var11 = (int)Math.floor((double)(var6.y + var5));

            for(int var12 = var10; var12 <= var11; ++var12) {
               for(int var13 = var8; var13 <= var9; ++var13) {
                  int var14 = (int)IsoUtils.XToScreenExact((float)var13, (float)(var12 + 1), (float)((int)this.z), 0);
                  int var15 = (int)IsoUtils.YToScreenExact((float)var13, (float)(var12 + 1), (float)((int)this.z), 0);
                  SpriteRenderer.instance.renderPoly(var14, var15, var14 + 32 * var1, var15 - 16 * var1, var14 + 64 * var1, var15, var14 + 32 * var1, var15 + 16 * var1, 1.0F, 1.0F, 1.0F, 0.5F);
               }
            }

            float var16 = 1.0F;
            float var17 = 1.0F;
            float var18 = 1.0F;
            if (this.isExitBlocked(var2)) {
               var18 = 0.0F;
               var17 = 0.0F;
            }

            this.getController().drawCircle(var7.x, var7.y, var5, 0.0F, 0.0F, 1.0F, 1.0F);
            this.getController().drawCircle(var6.x, var6.y, var5, var16, var17, var18, 1.0F);
         }
      }

   }

   private Vector2 areaPositionLocal(VehicleScript.Area var1) {
      Vector2 var2 = this.areaPositionWorld(var1);
      Vector3f var3 = this.tempVector3f_2;
      var3.set(var2.x, var2.y, 0.0F);
      this.getLocalPos(var3, var3);
      var2.set(var3.x, var3.z);
      return var2;
   }

   public Vector2 areaPositionWorld(VehicleScript.Area var1) {
      if (var1 == null) {
         return null;
      } else {
         Vector3f var2 = this.tempVector3f_2;
         Vector2[] var3 = new Vector2[4];
         float var4 = 0.05F;
         var2.set(var1.x + var1.w / 2.0F - var4, 0.0F, var1.y + var1.h / 2.0F - var4);
         this.getWorldPos(var2, var2);
         var3[0] = new Vector2(var2.x, var2.y);
         var2.set(var1.x - var1.w / 2.0F + var4, 0.0F, var1.y + var1.h / 2.0F - var4);
         this.getWorldPos(var2, var2);
         var3[1] = new Vector2(var2.x, var2.y);
         var2.set(var1.x - var1.w / 2.0F + var4, 0.0F, var1.y - var1.h / 2.0F + var4);
         this.getWorldPos(var2, var2);
         var3[2] = new Vector2(var2.x, var2.y);
         var2.set(var1.x + var1.w / 2.0F - var4, 0.0F, var1.y - var1.h / 2.0F + var4);
         this.getWorldPos(var2, var2);
         var3[3] = new Vector2(var2.x, var2.y);
         boolean var5 = QuadranglesIntersection.IsQuadranglesAreIntersected(var3, this.poly.borders);
         if (var5) {
            if (var1.x > this.width / 2.0F || var1.x < -this.width / 2.0F) {
               var2.set(var1.x, 0.0F, var1.y);
               this.getWorldPos(var2, var2);
               return new Vector2(var2.x - this.script.getExtentsOffset().x, var2.y - this.script.getExtentsOffset().y);
            }

            var2.set(var1.x, 0.0F, var1.y);
            this.getWorldPos(var2, var2);
            var3[0] = new Vector2(var2.x, var2.y);
            var2.set(var1.x, 0.0F, var1.y + var1.h / 2.0F);
            this.getWorldPos(var2, var2);
            var3[1] = new Vector2(var2.x, var2.y);
            var2.set(var1.x, 0.0F, var1.y);
            this.getWorldPos(var2, var2);
            var3[2] = new Vector2(var2.x - this.script.getExtentsOffset().x, var2.y - this.script.getExtentsOffset().y);
            var2.set(var1.x + var1.w / 2.0F, 0.0F, var1.y);
            this.getWorldPos(var2, var2);
            var3[3] = new Vector2(var2.x - this.script.getExtentsOffset().x, var2.y - this.script.getExtentsOffset().y);
            Vector2 var6 = PolygonalMap2.VehiclePoly.lineIntersection(var3[0], var3[1], var3[2], var3[3]);
            if (var6 != null) {
               return var6;
            }
         }

         var2.set(var1.x, 0.0F, var1.y);
         this.getWorldPos(var2, var2);
         return new Vector2(var2.x, var2.y);
      }
   }

   private void renderAreas() {
      if (this.getScript() != null) {
         float var1 = this.getScript().getModelScale();

         for(int var2 = 0; var2 < this.parts.size(); ++var2) {
            VehiclePart var3 = (VehiclePart)this.parts.get(var2);
            if (var3.getArea() != null) {
               VehicleScript.Area var4 = this.getScript().getAreaById(var3.getArea());
               if (var4 != null) {
                  Vector2 var5 = this.areaPositionWorld(var4);
                  if (var5 != null) {
                     boolean var6 = this.isInArea(var4.id, IsoPlayer.instance);
                     Vector3f var7 = this.getForwardVector(this.tempVector3f_1);
                     this.getController().drawRect(var7, var5.x - WorldSimulation.instance.offsetX, var5.y - WorldSimulation.instance.offsetY, var4.w * var1, var4.h / 2.0F * var1, var6 ? 0.0F : 0.65F, var6 ? 1.0F : 0.65F, var6 ? 1.0F : 0.65F);
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
      float var1 = 0.3F;
      float var2 = 0.3F;
      float var3 = 0.3F;
      if (this.netPlayerAuthorization == 0) {
         var1 = 1.0F;
      }

      if (this.netPlayerAuthorization == 1) {
         var3 = 1.0F;
      }

      if (this.netPlayerAuthorization == 3) {
         var2 = 1.0F;
      }

      if (this.netPlayerAuthorization == 4) {
         var2 = 1.0F;
         var1 = 1.0F;
      }

      if (this.netPlayerAuthorization == 2) {
         var3 = 1.0F;
         var1 = 1.0F;
      }

      LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), var1, var2, var3, 1.0F, 0);
      LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x2, this.poly.y2, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), var1, var2, var3, 1.0F, 0);
      LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x3, this.poly.y3, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), var1, var2, var3, 1.0F, 0);
      LineDrawer.drawLine(IsoUtils.XToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x4, this.poly.y4, 0.0F, 0), IsoUtils.XToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), IsoUtils.YToScreenExact(this.poly.x1, this.poly.y1, 0.0F, 0), var1, var2, var3, 1.0F, 0);
      TextManager.instance.DrawString((double)IsoUtils.XToScreenExact(this.x, this.y, 0.0F, 0), (double)IsoUtils.YToScreenExact(this.x, this.y, 0.0F, 0), "Authorizations VID:" + this.VehicleID + "\n auth:" + this.netPlayerAuthorization + "\n authPID:" + this.netPlayerId + "\n authTimeout:" + this.netPlayerTimeout);
   }

   private void renderAttackPositions() {
      for(int var1 = 0; var1 < this.getMaxPassengers(); ++var1) {
         IsoGameCharacter var2 = this.getCharacter(var1);
         if (var2 != null) {
            ArrayList var3 = new ArrayList();
            float var4 = this.script.getExtents().x / this.script.getModelScale();
            float var5 = this.script.getExtents().z / this.script.getModelScale();
            float var6 = 0.3F / this.script.getModelScale();
            float var7 = -var4 / 2.0F - var6;
            float var8 = -var5 / 2.0F - var6;
            float var9 = var4 / 2.0F + var6;
            float var10 = var5 / 2.0F + var6;
            this.initPolyPlusRadiusBounds();
            float var11 = this.polyPlusRadiusMinX;
            float var12 = this.polyPlusRadiusMaxX;
            float var13 = this.polyPlusRadiusMinY;
            float var14 = this.polyPlusRadiusMaxY;
            this.getAdjacentPositions(var2, var11, -var5 / 2.0F, var11, var5 / 2.0F, var3);
            this.getAdjacentPositions(var2, var12, -var5 / 2.0F, var12, var5 / 2.0F, var3);
            this.getAdjacentPositions(var2, var7, var13, var9, var13, var3);
            this.getAdjacentPositions(var2, var7, var14, var9, var14, var3);

            for(int var16 = 0; var16 < var3.size(); ++var16) {
               Vector3f var17 = (Vector3f)var3.get(var16);
               var13 = 1.0F;
               var14 = 1.0F;
               float var15 = 1.0F;
               if (!this.isAttackPositionFree(var17.x, var17.y, (IsoGameCharacter)null)) {
                  var15 = 0.0F;
                  var14 = 0.0F;
               }

               this.physics.drawCircle(var17.x, var17.y, 0.3F, var13, var14, var15, 1.0F);
            }

            return;
         }
      }

   }

   private void renderUsableArea() {
      if (this.getScript() != null) {
         VehiclePart var1 = this.getUseablePart(IsoPlayer.instance);
         if (var1 != null) {
            VehicleScript.Area var2 = this.getScript().getAreaById(var1.getArea());
            if (var2 != null) {
               Vector2 var3 = this.areaPositionWorld(var2);
               if (var3 != null) {
                  Vector3f var4 = this.getForwardVector(this.tempVector3f_1);
                  float var5 = this.getScript().getModelScale();
                  this.getController().drawRect(var4, var3.x - WorldSimulation.instance.offsetX, var3.y - WorldSimulation.instance.offsetY, var2.w * var5, var2.h / 2.0F * var5, 0.0F, 1.0F, 0.0F);
               }
            }
         }
      }
   }

   private void renderIntersectedSquares() {
      PolygonalMap2.VehiclePoly var1 = this.getPoly();
      float var2 = Math.min(var1.x1, Math.min(var1.x2, Math.min(var1.x3, var1.x4)));
      float var3 = Math.min(var1.y1, Math.min(var1.y2, Math.min(var1.y3, var1.y4)));
      float var4 = Math.max(var1.x1, Math.max(var1.x2, Math.max(var1.x3, var1.x4)));
      float var5 = Math.max(var1.y1, Math.max(var1.y2, Math.max(var1.y3, var1.y4)));

      for(int var6 = (int)var3; var6 < (int)Math.ceil((double)var5); ++var6) {
         for(int var7 = (int)var2; var7 < (int)Math.ceil((double)var4); ++var7) {
            if (this.isIntersectingSquare(var7, var6, (int)this.z)) {
               LineDrawer.addLine((float)var7, (float)var6, (float)((int)this.z), (float)(var7 + 1), (float)(var6 + 1), (float)((int)this.z), 1.0F, 1.0F, 1.0F, (String)null, false);
            }
         }
      }

   }

   private void setModelTransform(BaseVehicle.ModelInfo var1, float var2, float var3, float var4, float var5) {
      var1.renderTransform.scaling(var5);
      if (var1.renderOrigin.length() > 0.0F) {
         Matrix4f var6 = this.tempMatrix4fLWJGL_1;
         var6.rotationX(var4).rotateY(var3);
         this.tempMatrix4f.translation(var1.renderOrigin).transpose();
         var6.mulGeneric(this.tempMatrix4f, var6);
         var6.mulGeneric(var1.renderTransform, var1.renderTransform);
         Vector3f var7 = var1.scriptModel.offset;
         if (var7.x != 0.0F || var7.y != 0.0F || var7.z != 0.0F) {
            var6.translation(var7).transpose();
            var6.mul((Matrix4fc)var1.renderTransform, var1.renderTransform);
         }

         var6.scaling(var1.scriptModel.scale);
         var6.mul((Matrix4fc)var1.renderTransform, var1.renderTransform);
      }

   }

   public void getWheelForwardVector(int var1, Vector3f var2) {
      BaseVehicle.WheelInfo var3 = this.wheelInfo[var1];
      Matrix4f var4 = this.tempMatrix4fLWJGL_1;
      var4.rotationY(var3.steering);
      var4.mulGeneric(this.jniTransform.getMatrix(this.tempMatrix4f).setTranslation(0.0F, 0.0F, 0.0F), var4);
      var4.getColumn(2, this.tempVector4f);
      var2.set(this.tempVector4f.x, 0.0F, this.tempVector4f.z);
   }

   public void tryStartEngine(boolean var1) {
      if (this.getDriver() == null || !(this.getDriver() instanceof IsoPlayer) || !((IsoPlayer)this.getDriver()).isBlockMovement()) {
         if (this.engineState == BaseVehicle.engineStateTypes.Idle) {
            if (!Core.bDebug && !SandboxOptions.instance.VehicleEasyUse.getValue() && !this.isKeysInIgnition() && !var1 && this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) == null && !this.isHotwired()) {
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

      int var1;
      for(var1 = 0; var1 < this.engineSound.length; ++var1) {
         if (this.engineSound[var1] != 0L) {
            this.emitter.stopSound(this.engineSound[var1]);
            this.engineSound[var1] = 0L;
         }
      }

      for(var1 = 0; var1 < this.new_EngineSoundId.length; ++var1) {
         if (this.new_EngineSoundId[var1] != 0L) {
            this.emitter.stopSound(this.new_EngineSoundId[var1]);
            this.new_EngineSoundId[var1] = 0L;
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

      int var1;
      for(var1 = 0; var1 < this.engineSound.length; ++var1) {
         if (this.engineSound[var1] != 0L) {
            this.emitter.stopSound(this.engineSound[var1]);
            this.engineSound[var1] = 0L;
         }
      }

      for(var1 = 0; var1 < this.new_EngineSoundId.length; ++var1) {
         if (this.new_EngineSoundId[var1] != 0L) {
            this.emitter.stopSound(this.new_EngineSoundId[var1]);
            this.new_EngineSoundId[var1] = 0L;
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

      int var1;
      for(var1 = 0; var1 < this.engineSound.length; ++var1) {
         if (this.engineSound[var1] != 0L) {
            this.emitter.stopSound(this.engineSound[var1]);
            this.engineSound[var1] = 0L;
         }
      }

      for(var1 = 0; var1 < this.new_EngineSoundId.length; ++var1) {
         if (this.new_EngineSoundId[var1] != 0L) {
            this.emitter.stopSound(this.new_EngineSoundId[var1]);
            this.new_EngineSoundId[var1] = 0L;
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

      int var1;
      for(var1 = 0; var1 < this.engineSound.length; ++var1) {
         if (this.engineSound[var1] != 0L) {
            this.emitter.stopSound(this.engineSound[var1]);
            this.engineSound[var1] = 0L;
         }
      }

      for(var1 = 0; var1 < this.new_EngineSoundId.length; ++var1) {
         if (this.new_EngineSoundId[var1] != 0L) {
            this.emitter.stopSound(this.new_EngineSoundId[var1]);
            this.new_EngineSoundId[var1] = 0L;
         }
      }

      this.engineSoundIndex = 0;
      this.engineState = BaseVehicle.engineStateTypes.ShutingDown;
      this.engineLastUpdateStateTime = System.currentTimeMillis();
      if (GameServer.bServer) {
         this.updateFlags = (short)(this.updateFlags | 4);
      }

      this.setKeysInIgnition(false);
      VehiclePart var2 = this.getHeater();
      if (var2 != null) {
         var2.getModData().rawset("active", false);
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
         IsoGameCharacter var1 = this.getDriver();
         if (var1 != null) {
            Boolean var2 = this.getDriver().getInventory().haveThisKeyId(this.getKeyId()) != null ? Boolean.TRUE : Boolean.FALSE;
            GameClient.instance.sendClientCommandV((IsoPlayer)this.getDriver(), "vehicle", "startEngine", "haveKey", var2);
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
      for(int var1 = 0; var1 < this.parts.size(); ++var1) {
         VehiclePart var2 = (VehiclePart)this.parts.get(var1);
         String var3 = var2.getLuaFunction("checkEngine");
         if (var3 != null && !Boolean.TRUE.equals(this.callLuaBoolean(var3, this, var2))) {
            return false;
         }
      }

      return true;
   }

   public boolean isOperational() {
      for(int var1 = 0; var1 < this.parts.size(); ++var1) {
         VehiclePart var2 = (VehiclePart)this.parts.get(var1);
         String var3 = var2.getLuaFunction("checkOperate");
         if (var3 != null && !Boolean.TRUE.equals(this.callLuaBoolean(var3, this, var2))) {
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

   public long playSoundImpl(String var1, IsoObject var2) {
      return this.getEmitter().playSoundImpl(var1, var2);
   }

   public int stopSound(long var1) {
      return this.getEmitter().stopSound(var1);
   }

   public void playSound(String var1) {
      this.getEmitter().playSound(var1);
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

      IsoPlayer var1 = null;
      float var2 = Float.MAX_VALUE;

      int var3;
      float var5;
      float var6;
      for(var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
         IsoPlayer var4 = IsoPlayer.players[var3];
         if (var4 != null && var4.getCurrentSquare() != null) {
            var5 = var4.getX();
            var6 = var4.getY();
            float var7 = IsoUtils.DistanceToSquared(var5, var6, this.x, this.y);
            if (var4.HasTrait("HardOfHearing")) {
               var7 *= 4.5F;
            }

            if (var4.HasTrait("Deaf")) {
               var7 = Float.MAX_VALUE;
            }

            if (var7 < var2) {
               var1 = var4;
               var2 = var7;
            }
         }
      }

      if (var1 == null) {
         if (this.emitter != null) {
            this.emitter.setPos(this.x, this.y, this.z);
            if (!this.emitter.isEmpty()) {
               this.emitter.tick();
            }
         }

      } else {
         int var15;
         if (!GameServer.bServer) {
            float var14 = var2;
            if (var2 > 1200.0F) {
               for(var15 = 0; var15 < this.engineSound.length; ++var15) {
                  if (this.engineSound[var15] != 0L) {
                     this.getEmitter().stopSound(this.engineSound[var15]);
                     this.engineSound[var15] = 0L;
                  }
               }

               for(var15 = 0; var15 < this.new_EngineSoundId.length; ++var15) {
                  if (this.new_EngineSoundId[var15] != 0L) {
                     this.getEmitter().stopSound(this.new_EngineSoundId[var15]);
                     this.new_EngineSoundId[var15] = 0L;
                  }
               }

               if (this.emitter != null && !this.emitter.isEmpty()) {
                  this.emitter.setPos(this.x, this.y, this.z);
                  this.emitter.tick();
               }

               return;
            }

            for(var15 = 0; var15 < this.new_EngineSoundId.length; ++var15) {
               if (this.new_EngineSoundId[var15] != 0L) {
                  this.getEmitter().setVolume(this.new_EngineSoundId[var15], 1.0F - var14 / 1200.0F);
               }
            }
         }

         this.startTime -= GameTime.instance.getMultiplier();
         if (RPMList.size() == 0) {
            for(var3 = 0; var3 < 37; ++var3) {
               var15 = Math.max(var3, 4);
               String var17 = "" + var15;
               if (var3 < 10) {
                  var17 = "0" + var15;
               }

               RPMList.add("car/" + var17);
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

               boolean var16 = this.isAnyListenerInside();
               float var18 = Math.abs(this.getCurrentSpeedKmHour());
               if (this.startTime <= 0.0F && this.engineState == BaseVehicle.engineStateTypes.Running) {
                  var5 = 1.0F - var2 / 1200.0F;
                  var5 = this.clamp(var5, 0.0F, 1.0F);
                  var5 *= var5;
                  var6 = 0.5F + this.throttle * 0.5F;
                  BaseVehicle.EngineRPMData[] var21 = null;
                  if ("jeep".equals(this.getScript().getEngineRPMType())) {
                     var21 = JeepEngineData;
                  } else if ("van".equals(this.getScript().getEngineRPMType())) {
                     var21 = VanEngineData;
                  } else if ("firebird".equals(this.getScript().getEngineRPMType())) {
                     var21 = FirebirdEngineData;
                  }

                  if (this.getDriver() == null) {
                     if (this.engineSpeed > 1000.0D) {
                        this.engineSpeed -= (double)(Rand.Next(10, 30) * 2);
                     } else {
                        this.engineSpeed = 1000.0D;
                     }
                  }

                  float var8 = (float)this.engineSpeed;
                  float var9 = (float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F;

                  for(int var10 = 0; var10 < 4; ++var10) {
                     if (var21[var10].SoundName != null && !var21[var10].SoundName.contains("unused")) {
                        if (this.new_EngineSoundId[var10] == 0L) {
                           this.new_EngineSoundId[var10] = this.emitter.playSoundLoopedImpl(var21[var10].SoundName);
                        }

                        this.emitter.set3D(this.new_EngineSoundId[var10], !var16);
                        if (var8 >= var21[var10].RPM_Min && var8 < var21[var10].RPM_Max && var9 > 0.0F) {
                           float var11 = 1.0F;
                           float var12 = 1.0F;
                           float var13;
                           if (var8 < var21[var10].RPM_Mid) {
                              var13 = (var8 - var21[var10].RPM_Min) / (var21[var10].RPM_Mid - var21[var10].RPM_Min);
                              var11 = var21[var10].RPM_Min_Pitch + var13 * (var21[var10].RPM_Mid_Pitch - var21[var10].RPM_Min_Pitch);
                              var12 = var21[var10].RPM_Min_Volume + var13 * (var21[var10].RPM_Mid_Volume - var21[var10].RPM_Min_Volume);
                           } else {
                              var13 = (var8 - var21[var10].RPM_Mid) / (var21[var10].RPM_Max - var21[var10].RPM_Mid);
                              var11 = var21[var10].RPM_Mid_Pitch + var13 * (var21[var10].RPM_Max_Pitch - var21[var10].RPM_Mid_Pitch);
                              var12 = var21[var10].RPM_Mid_Volume + var13 * (var21[var10].RPM_Max_Volume - var21[var10].RPM_Mid_Volume);
                           }

                           var11 *= var11;
                           this.emitter.setPitch(this.new_EngineSoundId[var10], var11);
                           this.emitter.setVolume(this.new_EngineSoundId[var10], var12 * var5 * var6);
                        } else if (this.new_EngineSoundId[var10] != 0L) {
                           this.emitter.setVolume(this.new_EngineSoundId[var10], 0.0F);
                        }
                     }
                  }
               }

               boolean var20 = false;
               if (!GameClient.bClient || this.isLocalPhysicSim()) {
                  for(int var19 = 0; var19 < this.script.getWheelCount(); ++var19) {
                     if (this.wheelInfo[var19].skidInfo < 0.15F) {
                        var20 = true;
                        break;
                     }
                  }
               }

               if (this.getCharacter(0) == null) {
                  var20 = false;
               }

               if (var20 != this.skidding) {
                  if (var20) {
                     this.skidSound = this.getEmitter().playSoundImpl("VehicleSkid", (IsoObject)null);
                  } else if (this.skidSound != 0L) {
                     this.emitter.stopSound(this.skidSound);
                     this.skidSound = 0L;
                  }

                  this.skidding = var20;
               }

               if (this.soundBackMoveSignal != -1L && this.emitter != null) {
                  this.emitter.set3D(this.soundBackMoveSignal, !var16);
               }

               if (this.soundHorn != -1L && this.emitter != null) {
                  this.emitter.set3D(this.soundHorn, !var16);
               }

               if (this.soundSirenSignal != -1L && this.emitter != null) {
                  this.emitter.set3D(this.soundSirenSignal, !var16);
               }

               if (this.emitter != null && (this.engineState != BaseVehicle.engineStateTypes.Idle || !this.emitter.isEmpty())) {
                  this.emitter.setPos(this.x, this.y, this.z);
                  this.emitter.tick();
               }

            }
         }
      }
   }

   private boolean updatePart(VehiclePart var1) {
      var1.updateSignalDevice();
      VehicleLight var2 = var1.getLight();
      if (var2 != null && var1.getId().contains("Headlight")) {
         var1.setLightActive(this.getHeadlightsOn() && var1.getInventoryItem() != null && this.getBatteryCharge() > 0.0F);
      }

      String var3 = var1.getLuaFunction("update");
      if (var3 == null) {
         return false;
      } else {
         float var4 = (float)GameTime.getInstance().getWorldAgeHours();
         if (var1.getLastUpdated() < 0.0F) {
            var1.setLastUpdated(var4);
         } else if (var1.getLastUpdated() > var4) {
            var1.setLastUpdated(var4);
         }

         float var5 = var4 - var1.getLastUpdated();
         if ((int)(var5 * 60.0F) > 0) {
            var1.setLastUpdated(var4);
            this.callLuaVoid(var3, this, var1, (double)(var5 * 60.0F));
            return true;
         } else {
            return false;
         }
      }
   }

   public void updateParts() {
      if (!GameClient.bClient) {
         boolean var4 = false;

         for(int var5 = 0; var5 < this.getPartCount(); ++var5) {
            VehiclePart var3 = this.getPartByIndex(var5);
            if (this.updatePart(var3) && !var4) {
               var4 = true;
            }

            if (var5 == this.getPartCount() - 1 && var4) {
               this.brakeBetweenUpdatesSpeed = 0.0F;
            }
         }

      } else {
         for(int var1 = 0; var1 < this.getPartCount(); ++var1) {
            VehiclePart var2 = this.getPartByIndex(var1);
            var2.updateSignalDevice();
         }

      }
   }

   public void drainBatteryUpdateHack() {
      boolean var1 = this.isEngineRunning();
      if (!var1) {
         for(int var2 = 0; var2 < this.parts.size(); ++var2) {
            VehiclePart var3 = (VehiclePart)this.parts.get(var2);
            if (var3.getDeviceData() != null && var3.getDeviceData().getIsTurnedOn()) {
               this.updatePart(var3);
            } else if (var3.getLight() != null && var3.getLight().getActive()) {
               this.updatePart(var3);
            }
         }

         if (this.hasLightbar() && (this.lightbarLightsMode.isEnable() || this.lightbarSirenMode.isEnable()) && this.getBattery() != null) {
            this.updatePart(this.getBattery());
         }

      }
   }

   public void setHeadlightsOn(boolean var1) {
      if (this.headlightsOn != var1) {
         this.headlightsOn = var1;
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
         VehiclePart var1 = this.getPartById("HeadlightLeft");
         if (var1 != null && var1.getInventoryItem() != null) {
            return true;
         } else {
            var1 = this.getPartById("HeadlightRight");
            return var1 != null && var1.getInventoryItem() != null;
         }
      }
   }

   public void setStoplightsOn(boolean var1) {
      if (this.stoplightsOn != var1) {
         this.stoplightsOn = var1;
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

         for(int var1 = 0; var1 < this.parts.size(); ++var1) {
            VehiclePart var2 = (VehiclePart)this.parts.get(var1);
            if (var2.getItemContainer() != null) {
               var2.getItemContainer().addItemsToProcessItems();
            }

            if (var2.getDeviceData() != null && !GameServer.bServer) {
               ZomboidRadio.getInstance().RegisterDevice(var2);
            }
         }

         if (this.lightbarSirenMode.isEnable()) {
            this.setLightbarSirenMode(this.lightbarSirenMode.get());
            if (this.sirenStartTime <= 0.0D) {
               this.sirenStartTime = GameTime.instance.getWorldAgeHours();
            }
         }

         if (this.chunk != null && this.chunk.jobType != IsoChunk.JobType.SoftReset) {
            PolygonalMap2.instance.addVehicleToWorld(this);
         }

         if (this.engineState != BaseVehicle.engineStateTypes.Idle) {
            this.engineSpeed = 1000.0D;
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
      int var1;
      for(var1 = 0; var1 < this.passengers.length; ++var1) {
         if (this.getPassenger(var1).character != null) {
            for(int var2 = 0; var2 < 4; ++var2) {
               if (this.getPassenger(var1).character == IsoPlayer.players[var2]) {
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

         for(var1 = 0; var1 < this.parts.size(); ++var1) {
            VehiclePart var3 = (VehiclePart)this.parts.get(var1);
            if (var3.getItemContainer() != null) {
               var3.getItemContainer().removeItemsFromProcessItems();
            }

            if (var3.getDeviceData() != null && !GameServer.bServer) {
               ZomboidRadio.getInstance().UnRegisterDevice(var3);
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
      for(int var1 = 0; var1 < this.getMaxPassengers(); ++var1) {
         IsoGameCharacter var2 = this.getCharacter(var1);
         if (var2 != null) {
            if (GameServer.bServer) {
               var2.sendObjectChange("exitVehicle");
            }

            this.exit(var2);
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

   public void setEngineFeature(int var1, int var2, int var3) {
      this.engineQuality = var1;
      this.engineLoudness = (int)((float)var2 / 2.7F);
      this.enginePower = var3;
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
      VehiclePart var1 = this.getBattery();
      return var1 != null && var1.getInventoryItem() instanceof DrainableComboItem ? ((DrainableComboItem)var1.getInventoryItem()).getUsedDelta() : 0.0F;
   }

   public int getPartCount() {
      return this.parts.size();
   }

   public VehiclePart getPartByIndex(int var1) {
      return var1 >= 0 && var1 < this.parts.size() ? (VehiclePart)this.parts.get(var1) : null;
   }

   public VehiclePart getPartById(String var1) {
      if (var1 == null) {
         return null;
      } else {
         for(int var2 = 0; var2 < this.parts.size(); ++var2) {
            VehiclePart var3 = (VehiclePart)this.parts.get(var2);
            VehicleScript.Part var4 = var3.getScriptPart();
            if (var4 != null && var1.equals(var4.id)) {
               return var3;
            }
         }

         return null;
      }
   }

   public int getNumberOfPartsWithContainers() {
      if (this.getScript() == null) {
         return 0;
      } else {
         int var1 = 0;

         for(int var2 = 0; var2 < this.getScript().getPartCount(); ++var2) {
            if (this.getScript().getPart(var2).container != null) {
               ++var1;
            }
         }

         return var1;
      }
   }

   public VehiclePart getPartForSeatContainer(int var1) {
      if (this.getScript() != null && var1 >= 0 && var1 < this.getMaxPassengers()) {
         for(int var2 = 0; var2 < this.getPartCount(); ++var2) {
            VehiclePart var3 = this.getPartByIndex(var2);
            if (var3.getContainerSeatNumber() == var1) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void transmitPartCondition(VehiclePart var1) {
      if (GameServer.bServer) {
         if (this.parts.contains(var1)) {
            var1.updateFlags = (short)(var1.updateFlags | 2048);
            this.updateFlags = (short)(this.updateFlags | 2048);
         }
      }
   }

   public void transmitPartItem(VehiclePart var1) {
      if (GameServer.bServer) {
         if (this.parts.contains(var1)) {
            var1.updateFlags = (short)(var1.updateFlags | 128);
            this.updateFlags = (short)(this.updateFlags | 128);
         }
      }
   }

   public void transmitPartModData(VehiclePart var1) {
      if (GameServer.bServer) {
         if (this.parts.contains(var1)) {
            var1.updateFlags = (short)(var1.updateFlags | 16);
            this.updateFlags = (short)(this.updateFlags | 16);
         }
      }
   }

   public void transmitPartUsedDelta(VehiclePart var1) {
      if (GameServer.bServer) {
         if (this.parts.contains(var1)) {
            if (var1.getInventoryItem() instanceof DrainableComboItem) {
               var1.updateFlags = (short)(var1.updateFlags | 32);
               this.updateFlags = (short)(this.updateFlags | 32);
            }
         }
      }
   }

   public void transmitPartDoor(VehiclePart var1) {
      if (GameServer.bServer) {
         if (this.parts.contains(var1)) {
            if (var1.getDoor() != null) {
               var1.updateFlags = (short)(var1.updateFlags | 512);
               this.updateFlags = (short)(this.updateFlags | 512);
            }
         }
      }
   }

   public void transmitPartWindow(VehiclePart var1) {
      if (GameServer.bServer) {
         if (this.parts.contains(var1)) {
            if (var1.getWindow() != null) {
               var1.updateFlags = (short)(var1.updateFlags | 256);
               this.updateFlags = (short)(this.updateFlags | 256);
            }
         }
      }
   }

   public int getLightCount() {
      return this.lights.size();
   }

   public VehiclePart getLightByIndex(int var1) {
      return var1 >= 0 && var1 < this.lights.size() ? (VehiclePart)this.lights.get(var1) : null;
   }

   public void setZone(String var1) {
      this.respawnZone = var1;
   }

   public String getZone() {
      return this.respawnZone;
   }

   public boolean isInArea(String var1, IsoGameCharacter var2) {
      if (var1 != null && this.getScript() != null) {
         VehicleScript.Area var3 = this.getScript().getAreaById(var1);
         if (var3 == null) {
            return false;
         } else {
            Vector2 var4 = this.areaPositionLocal(var3);
            if (var4 == null) {
               return false;
            } else {
               Vector3f var5 = this.tempVector3f_1;
               var5.set(var2.x, var2.y, this.z);
               this.getLocalPos(var5, var5);
               float var6 = var4.x - var3.w / 2.0F;
               float var7 = var4.y - var3.h / 2.0F;
               float var8 = var4.x + var3.w / 2.0F;
               float var9 = var4.y + var3.h / 2.0F;
               return var5.x >= var6 && var5.x < var8 && var5.z >= var7 && var5.z < var9;
            }
         }
      } else {
         return false;
      }
   }

   public float getAreaDist(String var1, IsoGameCharacter var2) {
      if (var1 != null && this.getScript() != null) {
         VehicleScript.Area var3 = this.getScript().getAreaById(var1);
         if (var3 != null) {
            Vector3f var4 = this.tempVector3f_1;
            var4.set(var2.x, var2.y, this.z);
            this.getLocalPos(var4, var4);
            float var5 = Math.abs(var3.x - var3.w / 2.0F);
            float var6 = Math.abs(var3.y - var3.h / 2.0F);
            float var7 = Math.abs(var3.x + var3.w / 2.0F);
            float var8 = Math.abs(var3.y + var3.h / 2.0F);
            return Math.abs(var4.x + var5) + Math.abs(var4.z + var6);
         } else {
            return 999.0F;
         }
      } else {
         return 999.0F;
      }
   }

   public Vector2 getAreaCenter(String var1) {
      if (var1 != null && this.getScript() != null) {
         VehicleScript.Area var2 = this.getScript().getAreaById(var1);
         if (var2 == null) {
            return null;
         } else {
            Vector2 var3 = this.areaPositionWorld(var2);
            return var3 == null ? null : new Vector2(var3.x, var3.y);
         }
      } else {
         return null;
      }
   }

   public boolean isInBounds(float var1, float var2) {
      if (this.getScript() == null) {
         return false;
      } else {
         Vector3f var3 = this.tempVector3f_1;
         var3.set(var1, var2, this.z);
         this.getLocalPos(var3, var3);
         float var4 = this.getScript().getModelScale();
         float var5 = -this.getScript().getExtents().x / var4 / 2.0F;
         float var6 = -this.getScript().getExtents().z / var4 / 2.0F;
         float var7 = this.getScript().getExtents().x / var4 / 2.0F;
         float var8 = this.getScript().getExtents().z / var4 / 2.0F;
         return var3.x >= var5 && var3.x < var7 && var3.z >= var6 && var3.z < var8;
      }
   }

   public boolean canAccessContainer(int var1, IsoGameCharacter var2) {
      VehiclePart var3 = this.getPartByIndex(var1);
      if (var3 == null) {
         return false;
      } else {
         VehicleScript.Part var4 = var3.getScriptPart();
         if (var4 == null) {
            return false;
         } else if (var4.container == null) {
            return false;
         } else if (var3.getItemType() != null && var3.getInventoryItem() == null && var4.container.capacity == 0) {
            return false;
         } else {
            return var4.container.luaTest != null && !var4.container.luaTest.isEmpty() ? Boolean.TRUE.equals(this.callLuaBoolean(var4.container.luaTest, this, var3, var2)) : true;
         }
      }
   }

   public boolean canInstallPart(IsoGameCharacter var1, VehiclePart var2) {
      if (!this.parts.contains(var2)) {
         return false;
      } else {
         KahluaTable var3 = var2.getTable("install");
         return var3 != null && var3.rawget("test") instanceof String ? Boolean.TRUE.equals(this.callLuaBoolean((String)var3.rawget("test"), this, var2, var1)) : false;
      }
   }

   public boolean canUninstallPart(IsoGameCharacter var1, VehiclePart var2) {
      if (!this.parts.contains(var2)) {
         return false;
      } else {
         KahluaTable var3 = var2.getTable("uninstall");
         return var3 != null && var3.rawget("test") instanceof String ? Boolean.TRUE.equals(this.callLuaBoolean((String)var3.rawget("test"), this, var2, var1)) : false;
      }
   }

   public static void resetLuaFunctions() {
      luaFunctionMap.clear();
   }

   private Object getLuaFunctionObject(String var1) {
      Object var2 = luaFunctionMap.get(var1);
      if (var2 == null) {
         KahluaTable var3 = LuaManager.env;
         if (!var1.contains(".")) {
            var2 = var3.rawget(var1);
         } else {
            String[] var4 = var1.split("\\.");

            for(int var5 = 0; var5 < var4.length - 1; ++var5) {
               Object var6 = var3.rawget(var4[var5]);
               if (!(var6 instanceof KahluaTable)) {
                  DebugLog.log("ERROR: no such function \"" + var1 + "\"");
                  return null;
               }

               var3 = (KahluaTable)var6;
            }

            var2 = var3.rawget(var4[var4.length - 1]);
         }

         if (!(var2 instanceof JavaFunction) && !(var2 instanceof LuaClosure)) {
            DebugLog.log("ERROR: no such function \"" + var1 + "\"");
         } else {
            luaFunctionMap.put(var1, var2);
         }
      }

      return var2;
   }

   private void callLuaVoid(String var1, Object var2, Object var3) {
      Object var4 = this.getLuaFunctionObject(var1);
      if (var4 != null) {
         LuaManager.caller.protectedCallVoid(LuaManager.thread, var4, var2, var3);
      }
   }

   private void callLuaVoid(String var1, Object var2, Object var3, Object var4) {
      Object var5 = this.getLuaFunctionObject(var1);
      if (var5 != null) {
         LuaManager.caller.protectedCallVoid(LuaManager.thread, var5, var2, var3, var4);
      }
   }

   private Boolean callLuaBoolean(String var1, Object var2, Object var3) {
      Object var4 = this.getLuaFunctionObject(var1);
      return var4 == null ? null : LuaManager.caller.protectedCallBoolean(LuaManager.thread, var4, var2, var3);
   }

   private Boolean callLuaBoolean(String var1, Object var2, Object var3, Object var4) {
      Object var5 = this.getLuaFunctionObject(var1);
      return var5 == null ? null : LuaManager.caller.protectedCallBoolean(LuaManager.thread, var5, var2, var3, var4);
   }

   public short getId() {
      return this.VehicleID;
   }

   public void setTireInflation(int var1, float var2) {
   }

   public void setTireRemoved(int var1, boolean var2) {
      Bullet.setTireRemoved(this.VehicleID, var1, var2);
   }

   private void getAdjacentPositions(IsoGameCharacter var1, float var2, float var3, float var4, float var5, ArrayList var6) {
      BaseVehicle.Passenger var7 = this.getPassenger(this.getSeat(var1));
      if (var7 != null) {
         float var8 = 0.3F / this.script.getModelScale();
         float var9;
         float var10;
         float var11;
         Vector3f var12;
         if (var2 == var4) {
            var9 = var2;
            var10 = var7.offset.z;

            for(var11 = var10; var11 >= var3 + var8; var11 -= var8 * 2.0F) {
               var12 = new Vector3f(var9, 0.0F, var11);
               this.getWorldPos(var12, var12);
               var6.add(var12);
            }

            for(var11 = var10 + var8 * 2.0F; var11 < var5 - var8; var11 += var8 * 2.0F) {
               var12 = new Vector3f(var9, 0.0F, var11);
               this.getWorldPos(var12, var12);
               var6.add(var12);
            }
         } else {
            var9 = 0.0F;
            var10 = var3;

            for(var11 = var9; var11 >= var2 + var8; var11 -= var8 * 2.0F) {
               var12 = new Vector3f(var11, 0.0F, var10);
               this.getWorldPos(var12, var12);
               var6.add(var12);
            }

            for(var11 = var9 + var8 * 2.0F; var11 < var4 - var8; var11 += var8 * 2.0F) {
               var12 = new Vector3f(var11, 0.0F, var10);
               this.getWorldPos(var12, var12);
               var6.add(var12);
            }
         }

      }
   }

   private boolean testAdjacentPositions(IsoGameCharacter var1, IsoGameCharacter var2, float var3, float var4, float var5, float var6) {
      BaseVehicle.Passenger var7 = this.getPassenger(this.getSeat(var1));
      if (var7 == null) {
         return false;
      } else {
         float var8 = 0.3F / this.script.getModelScale();
         float var9;
         float var10;
         float var11;
         Vector3f var12;
         if (var3 == var5) {
            var9 = var3;
            var10 = var7.offset.z;

            for(var11 = var10; var11 >= var4 + var8; var11 -= var8 * 2.0F) {
               var12 = this.tempVector3f_1.set(var9, 0.0F, var11);
               this.getWorldPos(var12, var12);
               var12.z = (float)((int)var12.z);
               if (this.isAttackPositionFree(var12.x, var12.y, var2)) {
                  return true;
               }
            }

            for(var11 = var10 + var8 * 2.0F; var11 < var6 - var8; var11 += var8 * 2.0F) {
               var12 = this.tempVector3f_1.set(var9, 0.0F, var11);
               this.getWorldPos(var12, var12);
               var12.z = (float)((int)var12.z);
               if (this.isAttackPositionFree(var12.x, var12.y, var2)) {
                  return true;
               }
            }
         } else {
            var9 = 0.0F;
            var10 = var4;

            for(var11 = var9; var11 >= var3 + var8; var11 -= var8 * 2.0F) {
               var12 = this.tempVector3f_1.set(var11, 0.0F, var10);
               this.getWorldPos(var12, var12);
               var12.z = (float)((int)var12.z);
               if (this.isAttackPositionFree(var12.x, var12.y, var2)) {
                  return true;
               }
            }

            for(var11 = var9 + var8 * 2.0F; var11 < var5 - var8; var11 += var8 * 2.0F) {
               var12 = this.tempVector3f_1.set(var11, 0.0F, var10);
               this.getWorldPos(var12, var12);
               var12.z = (float)((int)var12.z);
               if (this.isAttackPositionFree(var12.x, var12.y, var2)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean isAttackPositionFree(float var1, float var2, IsoGameCharacter var3) {
      float var4 = 0.1F;

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare((int)var1 + var6, (int)var2 + var5, (int)this.getZ());
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.getMovingObjects().size(); ++var8) {
                  IsoMovingObject var9 = (IsoMovingObject)var7.getMovingObjects().get(var8);
                  if (var9 != var3 && var9 instanceof IsoZombie && ((IsoZombie)var9).getCurrentState() == AttackVehicleState.instance() && var9.DistToSquared(var1, var2) < var4) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   private boolean chooseBestAttackPositionAlongEdge(IsoGameCharacter var1, IsoGameCharacter var2, float var3, float var4, float var5, float var6) {
      return this.testAdjacentPositions(var1, var2, var3, var4, var5, var6);
   }

   public Vector3f chooseBestAttackPosition(IsoGameCharacter var1, IsoGameCharacter var2) {
      int var3 = this.getSeat(var1);
      if (var3 == -1) {
         return null;
      } else {
         float var4 = this.script.getExtents().x / this.script.getModelScale();
         float var5 = this.script.getExtents().z / this.script.getModelScale();
         float var6 = 0.3F / this.script.getModelScale();
         float var7 = -var4 / 2.0F - var6;
         float var8 = -var5 / 2.0F - var6;
         float var9 = var4 / 2.0F + var6;
         float var10 = var5 / 2.0F + var6;
         this.initPolyPlusRadiusBounds();
         var7 = this.polyPlusRadiusMinX;
         var9 = this.polyPlusRadiusMaxX;
         var8 = this.polyPlusRadiusMinY;
         var10 = this.polyPlusRadiusMaxY;
         BaseVehicle.Passenger var11 = this.getPassenger(var3);
         VehicleScript.Position var12 = this.getPassengerPosition(var3, "outside");
         if (var12 != null && var12.getOffset().z() < var8 && this.chooseBestAttackPositionAlongEdge(var1, var2, var7, var8, var9, var8)) {
            return this.tempVector3f_1;
         } else {
            if (var11.offset.x > 0.0F) {
               if (this.chooseBestAttackPositionAlongEdge(var1, var2, var9, -var5 / 2.0F, var9, var5 / 2.0F)) {
                  return this.tempVector3f_1;
               }

               if (this.chooseBestAttackPositionAlongEdge(var1, var2, var7, -var5 / 2.0F, var7, var5 / 2.0F)) {
                  return this.tempVector3f_1;
               }
            } else {
               if (this.chooseBestAttackPositionAlongEdge(var1, var2, var7, -var5 / 2.0F, var7, var5 / 2.0F)) {
                  return this.tempVector3f_1;
               }

               if (this.chooseBestAttackPositionAlongEdge(var1, var2, var9, -var5 / 2.0F, var9, var5 / 2.0F)) {
                  return this.tempVector3f_1;
               }
            }

            if (this.chooseBestAttackPositionAlongEdge(var1, var2, -var4 / 2.0F - var6, var10, var4 / 2.0F + var6, var10)) {
               return this.tempVector3f_1;
            } else {
               return this.chooseBestAttackPositionAlongEdge(var1, var2, -var4 / 2.0F - var6, var8, var4 / 2.0F + var6, var8) ? this.tempVector3f_1 : null;
            }
         }
      }
   }

   public BaseVehicle.MinMaxPosition getMinMaxPosition() {
      BaseVehicle.MinMaxPosition var1 = new BaseVehicle.MinMaxPosition();
      float var2 = this.getScript().getModelScale();
      float var3 = this.getX();
      float var4 = this.getY();
      float var5 = this.getScript().getExtents().x / var2;
      float var6 = this.getScript().getExtents().z / var2;
      IsoDirections var7 = this.getDir();
      switch(var7) {
      case E:
      case W:
         var1.minX = var3 - var5 / 2.0F;
         var1.maxX = var3 + var5 / 2.0F;
         var1.minY = var4 - var6 / 2.0F;
         var1.maxY = var4 + var6 / 2.0F;
         break;
      case N:
      case S:
         var1.minX = var3 - var6 / 2.0F;
         var1.maxX = var3 + var6 / 2.0F;
         var1.minY = var4 - var5 / 2.0F;
         var1.maxY = var4 + var5 / 2.0F;
         break;
      default:
         return null;
      }

      return var1;
   }

   public String getVehicleType() {
      return this.type;
   }

   public void setVehicleType(String var1) {
      this.type = var1;
   }

   public float getMaxSpeed() {
      return this.maxSpeed;
   }

   public void setMaxSpeed(float var1) {
      this.maxSpeed = var1;
   }

   public void lockServerUpdate(long var1) {
      this.updateLockTimeout = System.currentTimeMillis() + var1;
   }

   public void changeTransmission(TransmissionNumber var1) {
      this.transmissionNumber = var1;
   }

   public void tryHotwire(int var1) {
      int var2 = Math.max(100 - this.getEngineQuality(), 5);
      var2 = Math.min(var2, 50);
      int var3 = var1 * 4;
      int var4 = var2 + var3;
      boolean var5 = false;
      if (Rand.Next(100) <= var4) {
         this.setHotwired(true);
         var5 = true;
      } else if (Rand.Next(100) <= 10 - var1) {
         this.setHotwiredBroken(true);
         var5 = true;
      } else if (GameServer.bServer) {
         LuaManager.GlobalObject.playServerSound("VehicleHotwireFail", this.square);
      } else if (this.getDriver() != null) {
         this.getDriver().getEmitter().playSound("VehicleHotwireFail");
      }

      if (var5 && GameServer.bServer) {
         this.updateFlags = (short)(this.updateFlags | 4096);
      }

   }

   public void cheatHotwire(boolean var1, boolean var2) {
      if (var1 != this.hotwired || var2 != this.hotwiredBroken) {
         this.hotwired = var1;
         this.hotwiredBroken = var2;
         if (GameServer.bServer) {
            this.updateFlags = (short)(this.updateFlags | 4096);
         }
      }

   }

   public boolean isKeyIsOnDoor() {
      return this.keyIsOnDoor;
   }

   public void setKeyIsOnDoor(boolean var1) {
      this.keyIsOnDoor = var1;
   }

   public boolean isHotwired() {
      return this.hotwired;
   }

   public void setHotwired(boolean var1) {
      this.hotwired = var1;
   }

   public boolean isHotwiredBroken() {
      return this.hotwiredBroken;
   }

   public void setHotwiredBroken(boolean var1) {
      this.hotwiredBroken = var1;
   }

   public IsoGameCharacter getDriver() {
      BaseVehicle.Passenger var1 = this.getPassenger(0);
      return var1 == null ? null : var1.character;
   }

   public boolean isKeysInIgnition() {
      return this.keysInIgnition;
   }

   public void setKeysInIgnition(boolean var1) {
      IsoGameCharacter var2 = this.getDriver();
      if (var2 != null) {
         this.setAlarmed(false);
         if (!GameClient.bClient || var2 instanceof IsoPlayer && ((IsoPlayer)var2).isLocalPlayer()) {
            if (!this.isHotwired()) {
               InventoryItem var3;
               if (!GameServer.bServer && var1 && !this.keysInIgnition) {
                  var3 = this.getDriver().getInventory().haveThisKeyId(this.getKeyId());
                  if (var3 != null) {
                     this.setCurrentKey(var3);
                     InventoryItem var4 = var3.getContainer().getContainingItem();
                     if (var4 instanceof InventoryContainer && "KeyRing".equals(var4.getType())) {
                        var3.getModData().rawset("keyRing", (double)var4.getID());
                     } else if (var3.hasModData()) {
                        var3.getModData().rawset("keyRing", (Object)null);
                     }

                     var3.getContainer().DoRemoveItem(var3);
                     this.keysInIgnition = var1;
                     if (GameClient.bClient) {
                        GameClient.instance.sendClientCommandV((IsoPlayer)this.getDriver(), "vehicle", "putKeyInIgnition", "key", var3);
                     }
                  }
               }

               if (!var1 && this.keysInIgnition && !GameServer.bServer) {
                  if (this.currentKey == null) {
                     this.currentKey = this.createVehicleKey();
                  }

                  var3 = this.getCurrentKey();
                  ItemContainer var7 = this.getDriver().getInventory();
                  if (var3.hasModData() && var3.getModData().rawget("keyRing") instanceof Double) {
                     Double var5 = (Double)var3.getModData().rawget("keyRing");
                     InventoryItem var6 = var7.getItemWithID(var5.longValue());
                     if (var6 instanceof InventoryContainer && "KeyRing".equals(var6.getType())) {
                        var7 = ((InventoryContainer)var6).getInventory();
                     }

                     var3.getModData().rawset("keyRing", (Object)null);
                  }

                  var7.addItem(var3);
                  this.setCurrentKey((InventoryItem)null);
                  this.keysInIgnition = var1;
                  if (GameClient.bClient) {
                     GameClient.instance.sendClientCommand((IsoPlayer)this.getDriver(), "vehicle", "removeKeyFromIgnition", (KahluaTable)null);
                  }
               }
            }

         }
      }
   }

   public void putKeyInIgnition(InventoryItem var1) {
      if (GameServer.bServer) {
         if (var1 instanceof Key) {
            if (!this.keysInIgnition) {
               this.keysInIgnition = true;
               this.keyIsOnDoor = false;
               this.currentKey = var1;
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

   public void putKeyOnDoor(InventoryItem var1) {
      if (GameServer.bServer) {
         if (var1 instanceof Key) {
            if (!this.keyIsOnDoor) {
               this.keyIsOnDoor = true;
               this.keysInIgnition = false;
               this.currentKey = var1;
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

   public void syncKeyInIgnition(boolean var1, boolean var2, InventoryItem var3) {
      if (GameClient.bClient) {
         if (!(this.getDriver() instanceof IsoPlayer) || !((IsoPlayer)this.getDriver()).isLocalPlayer()) {
            this.keysInIgnition = var1;
            this.keyIsOnDoor = var2;
            this.currentKey = var3;
         }
      }
   }

   private void randomizeContainer(VehiclePart var1) {
      if (!GameClient.bClient) {
         KahluaTable var2 = (KahluaTable)LuaManager.env.rawget("ItemPicker");
         KahluaTable var3 = (KahluaTable)((KahluaTable)LuaManager.env.rawget("VehicleDistributions")).rawget(1);
         boolean var4 = true;
         KahluaTable var5 = null;
         if (var3.rawget(this.getScriptName().replaceFirst("Base.", "") + this.getSkinIndex()) != null) {
            var4 = false;
            var3 = (KahluaTable)var3.rawget(this.getScriptName().replaceFirst("Base.", "") + this.getSkinIndex());
         } else {
            var3 = (KahluaTable)var3.rawget(this.getScriptName().replaceFirst("Base.", ""));
         }

         if (var3 != null) {
            if (var4 && Rand.Next(100) <= 8 && var3.rawget("Specific") != null) {
               var3 = (KahluaTable)var3.rawget("Specific");
               int var6 = Rand.Next(1, var3.size() + 1);
               var5 = (KahluaTable)var3.rawget(var6);
            } else {
               var5 = (KahluaTable)var3.rawget("Normal");
            }

            if (var5 != null) {
               if (!var1.getId().contains("Seat") && var5.rawget(var1.getId()) == null) {
                  DebugLog.log("NO CONT DISTRIB FOR PART: " + var1.getId() + " CAR: " + this.getScriptName().replaceFirst("Base.", ""));
               }

               LuaManager.caller.pcall(LuaManager.thread, var2.rawget("fillContainerType"), var5, var1.getItemContainer(), "", null);
               if (GameServer.bServer && !var1.getItemContainer().getItems().isEmpty()) {
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

   public void setLightbarLightsMode(int var1) {
      this.lightbarLightsMode.set(var1);
      if (GameServer.bServer) {
         this.updateFlags = (short)(this.updateFlags | 1024);
      }
   }

   public void setLightbarSirenMode(int var1) {
      if (this.soundSirenSignal != -1L) {
         this.getEmitter().stopSound(this.soundSirenSignal);
         this.soundSirenSignal = -1L;
      }

      this.lightbarSirenMode.set(var1);
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

   public void setChoosenParts(HashMap var1) {
      this.choosenParts = var1;
   }

   public float getMass() {
      return this.mass;
   }

   public void setMass(float var1) {
      this.mass = var1;
   }

   public float getInitialMass() {
      return this.initialMass;
   }

   public void setInitialMass(float var1) {
      this.initialMass = var1;
   }

   public void updateTotalMass() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 < this.parts.size(); ++var2) {
         VehiclePart var3 = (VehiclePart)this.parts.get(var2);
         if (var3.getItemContainer() != null) {
            var1 += var3.getItemContainer().getCapacityWeight();
         }

         if (var3.getInventoryItem() != null) {
            var1 += var3.getInventoryItem().getWeight();
         }
      }

      this.setMass((float)Math.round(this.getInitialMass() + var1));
      if (WorldSimulation.instance.created) {
         Bullet.setVehicleMass(this.VehicleID, this.getMass());
      }

   }

   public float getBrakingForce() {
      return this.brakingForce;
   }

   public void setBrakingForce(float var1) {
      this.brakingForce = var1;
   }

   public float getBaseQuality() {
      return this.baseQuality;
   }

   public void setBaseQuality(float var1) {
      this.baseQuality = var1;
   }

   public float getCurrentSteering() {
      return this.currentSteering;
   }

   public void setCurrentSteering(float var1) {
      this.currentSteering = var1;
   }

   public boolean isDoingOffroad() {
      return this.getCurrentSquare() != null && this.getCurrentSquare().getFloor() != null && !this.getCurrentSquare().getFloor().getSprite().getName().contains("carpentry_02") && !this.getCurrentSquare().getFloor().getSprite().getName().contains("blends_street") && !this.getCurrentSquare().getFloor().getSprite().getName().contains("floors_exterior_street");
   }

   public boolean isBraking() {
      return this.isBraking;
   }

   public void setBraking(boolean var1) {
      this.isBraking = var1;
      if (var1 && this.brakeBetweenUpdatesSpeed == 0.0F) {
         this.brakeBetweenUpdatesSpeed = Math.abs(this.getCurrentSpeedKmHour());
      }

   }

   public void updatePartStats() {
      this.setBrakingForce(0.0F);
      this.engineLoudness = (int)((double)this.getScript().getEngineLoudness() * SandboxOptions.instance.ZombieAttractionMultiplier.getValue() / 2.0D);
      boolean var1 = false;

      for(int var2 = 0; var2 < this.getPartCount(); ++var2) {
         VehiclePart var3 = this.getPartByIndex(var2);
         if (var3.getInventoryItem() != null) {
            float var4;
            if (var3.getInventoryItem().getBrakeForce() > 0.0F) {
               var4 = VehiclePart.getNumberByCondition(var3.getInventoryItem().getBrakeForce(), (float)var3.getInventoryItem().getCondition(), 5.0F);
               var4 += var4 / 50.0F * (float)var3.getMechanicSkillInstaller();
               this.setBrakingForce(this.getBrakingForce() + var4);
            }

            if (var3.getInventoryItem().getWheelFriction() > 0.0F) {
               var3.setWheelFriction(0.0F);
               var4 = VehiclePart.getNumberByCondition(var3.getInventoryItem().getWheelFriction(), (float)var3.getInventoryItem().getCondition(), 0.2F);
               var4 += 0.1F * (float)var3.getMechanicSkillInstaller();
               var4 = Math.min(2.3F, var4);
               var3.setWheelFriction(var4);
            }

            if (var3.getInventoryItem().getSuspensionCompression() > 0.0F) {
               var3.setSuspensionCompression(VehiclePart.getNumberByCondition(var3.getInventoryItem().getSuspensionCompression(), (float)var3.getInventoryItem().getCondition(), 0.6F));
               var3.setSuspensionDamping(VehiclePart.getNumberByCondition(var3.getInventoryItem().getSuspensionDamping(), (float)var3.getInventoryItem().getCondition(), 0.6F));
            }

            if (var3.getInventoryItem().getEngineLoudness() > 0.0F) {
               var3.setEngineLoudness(VehiclePart.getNumberByCondition(var3.getInventoryItem().getEngineLoudness(), (float)var3.getInventoryItem().getCondition(), 10.0F));
               this.engineLoudness = (int)((float)this.engineLoudness * (1.0F + (100.0F - var3.getEngineLoudness()) / 100.0F));
               var1 = true;
            }
         }
      }

      if (!var1) {
         this.engineLoudness *= 2;
      }

   }

   public void setRust(float var1) {
      this.rust = var1;
   }

   public void updateBulletStats() {
      if (!this.getScriptName().contains("Burnt") && WorldSimulation.instance.created) {
         float[] var1 = vehicleParams;
         double var4 = 2.4D;
         byte var6 = 5;
         if (WorldSimulation.instance.created) {
            double var2;
            float var7;
            if (this.isInForest() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0F) {
               var2 = (double)Rand.Next(0.08F, 0.18F);
               var7 = 0.7F;
               var6 = 3;
            } else if (this.isDoingOffroad() && Math.abs(this.getCurrentSpeedKmHour()) > 1.0F) {
               var2 = (double)Rand.Next(0.05F, 0.15F);
               var7 = 0.7F;
            } else {
               if (Math.abs(this.getCurrentSpeedKmHour()) > 1.0F && Rand.Next(100) < 10) {
                  var2 = (double)Rand.Next(0.05F, 0.15F);
               } else {
                  var2 = 0.0D;
               }

               var7 = 1.0F;
            }

            if (RainManager.isRaining()) {
               var7 -= 0.3F;
            }

            VehicleScript.Wheel var8 = this.script.getWheel(0);
            Vector3f var9 = this.tempVector3f_1.set(var8.offset.x, var8.offset.y, var8.offset.z);
            this.getWorldPos(var9, var9);
            VehiclePart var10 = this.getPartById("TireFrontLeft");
            VehiclePart var11 = this.getPartById("SuspensionFrontLeft");
            if (var10 != null && var10.getInventoryItem() != null) {
               var1[0] = 1.0F;
               var1[4] = Math.min(var10.getContainerContentAmount() / (float)(var10.getContainerCapacity() - 10), 1.0F);
               var1[8] = var7 * var10.getWheelFriction();
               if (var11 != null && var11.getInventoryItem() != null) {
                  var1[12] = var11.getSuspensionDamping();
                  var1[16] = var11.getSuspensionCompression();
               } else {
                  var1[12] = 0.1F;
                  var1[16] = 0.1F;
               }

               if (Rand.Next(var6) == 0) {
                  var1[20] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[20] = 0.0F;
               }
            } else {
               var1[0] = 0.0F;
               var1[4] = 30.0F;
               var1[8] = 0.0F;
               var1[12] = 2.88F;
               var1[16] = 3.83F;
               if (Rand.Next(var6) == 0) {
                  var1[20] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[20] = 0.0F;
               }
            }

            var8 = this.script.getWheel(1);
            var9 = this.tempVector3f_1.set(var8.offset.x, var8.offset.y, var8.offset.z);
            this.getWorldPos(var9, var9);
            var10 = this.getPartById("TireFrontRight");
            var11 = this.getPartById("SuspensionFrontRight");
            if (var10 != null && var10.getInventoryItem() != null) {
               var1[1] = 1.0F;
               var1[5] = Math.min(var10.getContainerContentAmount() / (float)(var10.getContainerCapacity() - 10), 1.0F);
               var1[9] = var7 * var10.getWheelFriction();
               if (var11 != null && var11.getInventoryItem() != null) {
                  var1[13] = var11.getSuspensionDamping();
                  var1[17] = var11.getSuspensionCompression();
               } else {
                  var1[13] = 0.1F;
                  var1[17] = 0.1F;
               }

               if (Rand.Next(var6) == 0) {
                  var1[21] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[21] = 0.0F;
               }
            } else {
               var1[1] = 0.0F;
               var1[5] = 30.0F;
               var1[9] = 0.0F;
               var1[13] = 2.88F;
               var1[17] = 3.83F;
               if (Rand.Next(var6) == 0) {
                  var1[21] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[21] = 0.0F;
               }
            }

            var8 = this.script.getWheel(2);
            var9 = this.tempVector3f_1.set(var8.offset.x, var8.offset.y, var8.offset.z);
            this.getWorldPos(var9, var9);
            var10 = this.getPartById("TireRearLeft");
            var11 = this.getPartById("SuspensionRearLeft");
            if (var10 != null && var10.getInventoryItem() != null) {
               var1[2] = 1.0F;
               var1[6] = Math.min(var10.getContainerContentAmount() / (float)(var10.getContainerCapacity() - 10), 1.0F);
               var1[10] = var7 * var10.getWheelFriction();
               if (var11 != null && var11.getInventoryItem() != null) {
                  var1[14] = var11.getSuspensionDamping();
                  var1[18] = var11.getSuspensionCompression();
               } else {
                  var1[14] = 0.1F;
                  var1[18] = 0.1F;
               }

               if (Rand.Next(var6) == 0) {
                  var1[22] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[22] = 0.0F;
               }
            } else {
               var1[2] = 0.0F;
               var1[6] = 30.0F;
               var1[10] = 0.0F;
               var1[14] = 2.88F;
               var1[18] = 3.83F;
               if (Rand.Next(var6) == 0) {
                  var1[22] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[22] = 0.0F;
               }
            }

            var8 = this.script.getWheel(3);
            var9 = this.tempVector3f_1.set(var8.offset.x, var8.offset.y, var8.offset.z);
            this.getWorldPos(var9, var9);
            var10 = this.getPartById("TireRearRight");
            var11 = this.getPartById("SuspensionRearRight");
            if (var10 != null && var10.getInventoryItem() != null) {
               var1[3] = 1.0F;
               var1[7] = Math.min(var10.getContainerContentAmount() / (float)(var10.getContainerCapacity() - 10), 1.0F);
               var1[11] = var7 * var10.getWheelFriction();
               if (var11 != null && var11.getInventoryItem() != null) {
                  var1[15] = var11.getSuspensionDamping();
                  var1[19] = var11.getSuspensionCompression();
               } else {
                  var1[15] = 0.1F;
                  var1[19] = 0.1F;
               }

               if (Rand.Next(var6) == 0) {
                  var1[23] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[23] = 0.0F;
               }
            } else {
               var1[3] = 0.0F;
               var1[7] = 30.0F;
               var1[11] = 0.0F;
               var1[15] = 2.88F;
               var1[19] = 3.83F;
               if (Rand.Next(var6) == 0) {
                  var1[23] = (float)(Math.sin(var4 * (double)var9.x()) * Math.sin(var4 * (double)var9.y()) * var2);
               } else {
                  var1[23] = 0.0F;
               }
            }

            Bullet.setVehicleParams(this.VehicleID, var1);
         }
      }
   }

   public void setActiveInBullet(boolean var1) {
      if (var1 || !this.isEngineRunning()) {
         ;
      }
   }

   public boolean areAllDoorsLocked() {
      for(int var1 = 0; var1 < this.getMaxPassengers(); ++var1) {
         VehiclePart var2 = this.getPassengerDoor(var1);
         if (var2 != null && var2.getDoor() != null && !var2.getDoor().isLocked()) {
            return false;
         }
      }

      return true;
   }

   public float getRemainingFuelPercentage() {
      VehiclePart var1 = this.getPartById("GasTank");
      return var1 == null ? 0.0F : var1.getContainerContentAmount() / (float)var1.getContainerCapacity() * 100.0F;
   }

   public int getMechanicalID() {
      return this.mechanicalID;
   }

   public void setMechanicalID(int var1) {
      this.mechanicalID = var1;
   }

   public boolean needPartsUpdate() {
      return this.needPartsUpdate;
   }

   public void setNeedPartsUpdate(boolean var1) {
      this.needPartsUpdate = var1;
   }

   public VehiclePart getHeater() {
      return this.getPartById("Heater");
   }

   public int windowsOpen() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.getPartCount(); ++var2) {
         VehiclePart var3 = this.getPartByIndex(var2);
         if (var3.window != null && var3.window.open) {
            ++var1;
         }
      }

      return var1;
   }

   public boolean isAlarmed() {
      return this.alarmed;
   }

   public void setAlarmed(boolean var1) {
      this.alarmed = var1;
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

         int var1 = (int)this.alarmAccumulator / 20;
         if (!this.soundHornOn && var1 % 2 == 0) {
            this.onHornStart();
            this.setHeadlightsOn(true);
         }

         if (this.soundHornOn && var1 % 2 == 1) {
            this.onHornStop();
            this.setHeadlightsOn(false);
         }
      }

   }

   public boolean isMechanicUIOpen() {
      return this.mechanicUIOpen;
   }

   public void setMechanicUIOpen(boolean var1) {
      this.mechanicUIOpen = var1;
   }

   public void damagePlayers(float var1) {
      if (SandboxOptions.instance.PlayerDamageFromCrash.getValue()) {
         if (!GameClient.bClient) {
            for(int var2 = 0; var2 < this.passengers.length; ++var2) {
               if (this.getPassenger(var2).character != null) {
                  IsoGameCharacter var3 = this.getPassenger(var2).character;
                  if (GameServer.bServer && var3 instanceof IsoPlayer) {
                     GameServer.sendPlayerDamagedByCarCrash((IsoPlayer)var3, var1);
                  } else {
                     this.addRandomDamageFromCrash(var3, var1);
                  }
               }
            }

         }
      }
   }

   public void addRandomDamageFromCrash(IsoGameCharacter var1, float var2) {
      int var3 = 1;
      if (var2 > 40.0F) {
         var3 = Rand.Next(1, 3);
      }

      if (var2 > 70.0F) {
         var3 = Rand.Next(2, 4);
      }

      int var4 = 0;

      int var5;
      for(var5 = 0; var5 < var1.getVehicle().getPartCount(); ++var5) {
         VehiclePart var6 = var1.getVehicle().getPartByIndex(var5);
         if (var6.window != null && var6.getCondition() < 15) {
            ++var4;
         }
      }

      for(var5 = 0; var5 < var3; ++var5) {
         int var9 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
         BodyPart var7 = var1.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var9));
         float var8 = Math.max(Rand.Next(var2 - 15.0F, var2), 5.0F);
         if (var1.HasTrait("FastHealer")) {
            var8 = (float)((double)var8 * 0.8D);
         } else if (var1.HasTrait("SlowHealer")) {
            var8 = (float)((double)var8 * 1.2D);
         }

         switch(SandboxOptions.instance.InjurySeverity.getValue()) {
         case 1:
            var8 *= 0.5F;
            break;
         case 3:
            var8 *= 1.5F;
         }

         var8 *= this.getScript().getPlayerDamageProtection();
         var8 = (float)((double)var8 * 0.9D);
         var7.AddDamage(var8);
         if (var8 > 40.0F && Rand.Next(12) == 0) {
            var7.generateDeepWound();
         } else if (var8 > 50.0F && Rand.Next(10) == 0 && SandboxOptions.instance.BoneFracture.getValue()) {
            if (var7.getType() != BodyPartType.Neck && var7.getType() != BodyPartType.Groin) {
               var7.setFractureTime(Rand.Next(Rand.Next(10.0F, var8 + 10.0F), Rand.Next(var8 + 20.0F, var8 + 30.0F)));
            } else {
               var7.generateDeepWound();
            }
         }

         if (var8 > 30.0F && Rand.Next(12 - var4) == 0) {
            var7 = var1.getBodyDamage().setScratchedWindow();
            if (Rand.Next(5) == 0) {
               var7.generateDeepWound();
               var7.setHaveGlass(true);
            }
         }
      }

   }

   public void hitVehicle(IsoGameCharacter var1, HandWeapon var2) {
      float var3 = 1.0F;
      if (var2 == null) {
         var2 = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
      }

      var3 = (float)var2.getDoorDamage();
      if ((float)Rand.Next(100) < var2.CriticalChance) {
         var3 *= 10.0F;
      }

      VehiclePart var4 = this.getNearestBodyworkPart(var1);
      if (var4 != null) {
         VehicleWindow var5 = var4.getWindow();

         for(int var6 = 0; var6 < var4.getChildCount(); ++var6) {
            VehiclePart var7 = var4.getChild(var6);
            if (var7.getWindow() != null) {
               var5 = var7.getWindow();
               break;
            }
         }

         if (var5 != null && var5.getHealth() > 0) {
            var5.damage((int)var3);
            this.transmitPartWindow(var4);
            if (var5.getHealth() == 0) {
               VehicleManager.sendSoundFromServer(this, (byte)1);
            }
         } else {
            var4.setCondition(var4.getCondition() - (int)var3);
            this.transmitPartItem(var4);
         }

         var4.updateFlags = (short)(var4.updateFlags | 2048);
         this.updateFlags = (short)(this.updateFlags | 2048);
      } else {
         Vector3f var8 = this.tempVector3f_1.set(var1.getX(), var1.getY(), var1.getZ());
         this.getLocalPos(var8, var8);
         boolean var9 = var8.x > 0.0F;
         if (var9) {
            this.addDamageFront((int)var3);
         } else {
            this.addDamageRear((int)var3);
         }

         this.updateFlags = (short)(this.updateFlags | 2048);
      }

   }

   public boolean isTrunkLocked() {
      VehiclePart var1 = this.getPartById("TrunkDoor");
      if (var1 == null) {
         var1 = this.getPartById("DoorRear");
      }

      return var1 != null && var1.getDoor() != null && var1.getInventoryItem() != null ? var1.getDoor().isLocked() : false;
   }

   public void setTrunkLocked(boolean var1) {
      VehiclePart var2 = this.getPartById("TrunkDoor");
      if (var2 == null) {
         var2 = this.getPartById("DoorRear");
      }

      if (var2 != null && var2.getDoor() != null && var2.getInventoryItem() != null) {
         var2.getDoor().setLocked(var1);
         if (GameServer.bServer) {
            this.transmitPartDoor(var2);
         }
      }

   }

   public VehiclePart getNearestBodyworkPart(IsoGameCharacter var1) {
      for(int var2 = 0; var2 < this.getPartCount(); ++var2) {
         VehiclePart var3 = this.getPartByIndex(var2);
         if (("door".equals(var3.getCategory()) || "bodywork".equals(var3.getCategory())) && this.isInArea(var3.getArea(), var1) && var3.getCondition() > 0) {
            return var3;
         }
      }

      return null;
   }

   public void setSirenStartTime(double var1) {
      this.sirenStartTime = var1;
   }

   public double getSirenStartTime() {
      return this.sirenStartTime;
   }

   public boolean sirenShutoffTimeExpired() {
      double var1 = SandboxOptions.instance.SirenShutoffHours.getValue();
      if (var1 <= 0.0D) {
         return false;
      } else {
         double var3 = GameTime.instance.getWorldAgeHours();
         if (this.sirenStartTime > var3) {
            this.sirenStartTime = var3;
         }

         return this.sirenStartTime + var1 < var3;
      }
   }

   public void repair() {
      for(int var1 = 0; var1 < this.getPartCount(); ++var1) {
         VehiclePart var2 = this.getPartByIndex(var1);
         var2.repair();
      }

   }

   public static float getFakeSpeedModifier() {
      if (!GameClient.bClient && !GameServer.bServer) {
         return 1.0F;
      } else {
         float var0 = (float)ServerOptions.instance.SpeedLimit.getValue();
         return 120.0F / Math.min(var0, 120.0F);
      }
   }

   public boolean isAnyListenerInside() {
      for(int var1 = 0; var1 < this.getMaxPassengers(); ++var1) {
         IsoGameCharacter var2 = this.getCharacter(var1);
         if (var2 instanceof IsoPlayer && ((IsoPlayer)var2).isLocalPlayer() && !var2.HasTrait("Deaf")) {
            return true;
         }
      }

      return false;
   }

   public boolean couldCrawlerAttackPassenger(IsoGameCharacter var1) {
      int var2 = this.getSeat(var1);
      return var2 == -1 ? false : false;
   }

   public boolean isGoodCar() {
      return this.isGoodCar;
   }

   public void setGoodCar(boolean var1) {
      this.isGoodCar = var1;
   }

   public InventoryItem getCurrentKey() {
      return this.currentKey;
   }

   public void setCurrentKey(InventoryItem var1) {
      this.currentKey = var1;
   }

   public boolean isInForest() {
      return this.getSquare() != null && this.getSquare().getZone() != null && ("Forest".equals(this.getSquare().getZone().getType()) || "DeepForest".equals(this.getSquare().getZone().getType()) || "FarmLand".equals(this.getSquare().getZone().getType()));
   }

   public float getOffroadEfficiency() {
      return this.isInForest() ? this.script.getOffroadEfficiency() * 1.5F : this.script.getOffroadEfficiency() * 2.0F;
   }

   public void doChrHitImpulse(IsoObject var1) {
      float var2 = 22.0F;
      Vector3f var3 = this.getLinearVelocity(this.tempVector3f_1);
      var3.y = 0.0F;
      Vector3f var4 = this.tempVector3f_2.set(this.x - var1.getX(), 0.0F, this.z - var1.getY());
      var4.normalize();
      var3.mul((Vector3fc)var4);
      float var5 = var3.length();
      var5 = Math.min(var5, var2);
      if (!(var5 < 0.05F)) {
         if (GameServer.bServer) {
            if (var1 instanceof IsoZombie) {
               ((IsoZombie)var1).thumpFlag = 1;
            }
         } else {
            SoundManager.instance.PlayWorldSound("ZombieThumpGeneric", var1.square, 0.0F, 20.0F, 0.9F, true);
         }

         Vector3f var6 = this.tempVector3f_2;
         var6.set(this.x - var1.getX(), 0.0F, this.y - var1.getY());
         var6.normalize();
         var3.normalize();
         float var7 = var3.dot(var6);
         var3.mul(var5);
         this.ApplyImpulse(var1, this.getMass() * 3.0F * var5 / var2 * Math.abs(var7));
      }
   }

   public boolean isDoColor() {
      return this.doColor;
   }

   public void setDoColor(boolean var1) {
      this.doColor = var1;
   }

   public float getBrakeSpeedBetweenUpdate() {
      return this.brakeBetweenUpdatesSpeed;
   }

   public IsoGridSquare getSquare() {
      return this.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
   }

   public void setColor(float var1, float var2, float var3) {
      this.colorValue = var1;
      this.colorSaturation = var2;
      this.colorHue = var3;
   }

   public boolean isRemovedFromWorld() {
      return this.removedFromWorld;
   }

   public float getInsideTemperature() {
      VehiclePart var1 = this.getPartById("PassengerCompartment");
      float var2 = 0.0F;
      if (var1 != null && var1.getModData() != null) {
         if (var1.getModData().rawget("temperature") != null) {
            var2 += ((Double)var1.getModData().rawget("temperature")).floatValue();
         }

         if (var1.getModData().rawget("windowtemperature") != null) {
            var2 += ((Double)var1.getModData().rawget("windowtemperature")).floatValue();
         }
      }

      return var2;
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

      public void setAuthorization(BaseVehicle var1) {
         this.netPlayerAuthorization = var1.netPlayerAuthorization;
         this.netPlayerId = var1.netPlayerId;
      }

      public boolean shouldSend(BaseVehicle var1) {
         if (var1.getController() == null) {
            return false;
         } else if (var1.updateLockTimeout > System.currentTimeMillis()) {
            return false;
         } else {
            this.flags = 0;
            if (Math.abs(this.x - var1.x) > 0.01F || Math.abs(this.y - var1.y) > 0.01F || Math.abs(this.z - var1.z) > 0.01F || Math.abs(this.orient.x - var1.savedRot.x) > 0.01F || Math.abs(this.orient.y - var1.savedRot.y) > 0.01F || Math.abs(this.orient.z - var1.savedRot.z) > 0.01F || Math.abs(this.orient.w - var1.savedRot.w) > 0.01F) {
               this.flags = (short)(this.flags | 2);
            }

            if (this.netPlayerAuthorization != var1.netPlayerAuthorization || this.netPlayerId != var1.netPlayerId) {
               this.flags = (short)(this.flags | 16384);
            }

            this.flags |= var1.updateFlags;
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

      public EngineRPMData(String var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
         this.SoundName = var1;
         this.RPM_Min = var2;
         this.RPM_Min_Pitch = var3;
         this.RPM_Min_Volume = var4;
         this.RPM_Mid = var5;
         this.RPM_Mid_Pitch = var6;
         this.RPM_Mid_Volume = var7;
         this.RPM_Max = var8;
         this.RPM_Max_Pitch = var9;
         this.RPM_Max_Volume = var10;
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
      StartingFailedNoPower;

      public static final BaseVehicle.engineStateTypes[] Values = values();
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

      // $FF: synthetic method
      VehicleImpulse(Object var1) {
         this();
      }
   }
}
