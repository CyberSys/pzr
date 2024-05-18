package zombie.characters;

import fmod.fmod.BaseSoundListener;
import fmod.fmod.DummySoundListener;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.SoundListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;
import se.krka.kahlua.vm.KahluaTable;
import zombie.FrameLoader;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.BeatenPlayerState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverFenceState2;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbThroughWindowState2;
import zombie.ai.states.DieState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.ForecastBeatenPlayerState;
import zombie.ai.states.IdleState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ReanimateState;
import zombie.ai.states.SatChairState;
import zombie.ai.states.SatChairStateOut;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.GameSound;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.Collections.ZombieSortedList;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.OnceEvery;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.gameStates.MainScreenState;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoPhysicsObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.SafeHouse;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.network.BodyDamageSync;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PassengerMap;
import zombie.network.PlayerNetHistory;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.PZConsole;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;

public class IsoPlayer extends IsoLivingCharacter {
   private boolean isWearingNightVisionGoggles = false;
   public static final byte NetRemoteState_Idle = 0;
   public static final byte NetRemoteState_Walk = 1;
   public static final byte NetRemoteState_Run = 2;
   public static byte NetRemoteState_Attack = 3;
   private Integer transactionID = 0;
   public static final boolean NoSound = false;
   public static final int MAX = 4;
   private float MoveSpeed = 0.06F;
   private static String forwardStr = "Forward";
   private static String backwardStr = "Backward";
   private static String leftStr = "Left";
   private static String rightStr = "Right";
   private static boolean CoopPVP = false;
   private int offSetXUI = 0;
   private int offSetYUI = 0;
   private double HoursSurvived = 0.0D;
   public boolean bDeathFinished = false;
   private boolean bSentDeath;
   private boolean noClip = false;
   private boolean authorizeMeleeAction = true;
   private boolean blockMovement = false;
   private Nutrition nutrition;
   private boolean forceOverrideAnim = false;
   public boolean isSpeek;
   public boolean isVoiceMute;
   private ColorInfo tagColor = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
   private String displayName = null;
   private boolean seeNonPvpZone = false;
   private HashMap mechanicsItem = new HashMap();
   public ZombieSortedList zombiesToSend;
   public BaseSoundListener soundListener;
   private long heavyBreathInstance = 0L;
   private String heavyBreathSoundName = null;
   public boolean bNewControls = true;
   public String username = "Bob";
   private boolean allChatMuted = false;
   private boolean forceRun = false;
   public boolean dirtyRecalcGridStack = true;
   public float dirtyRecalcGridStackTime = 10.0F;
   public float closestZombie = 1000000.0F;
   public boolean GhostMode = false;
   public static IsoPlayer instance;
   public Vector2 playerMoveDir = new Vector2(0.0F, 0.0F);
   protected boolean isAiming = false;
   protected static Stack StaticTraits = new Stack();
   private int sleepingPillsTaken = 0;
   private long lastPillsTaken = 0L;
   protected int angleCounter = 0;
   public Vector2 lastAngle = new Vector2();
   protected int DialogMood = 1;
   protected int ping = 0;
   protected boolean FakeAttack = false;
   protected IsoObject FakeAttackTarget = null;
   protected IsoMovingObject DragObject = null;
   protected float AsleepTime = 0.0F;
   protected String[] MainHot = new String[10];
   protected String[] SecHot = new String[10];
   protected Stack spottedList = new Stack();
   protected int TicksSinceSeenZombie = 9999999;
   protected boolean Waiting = true;
   protected IsoSurvivor DragCharacter = null;
   protected Stack lastPos = new Stack();
   protected boolean bDebounceLMB = false;
   protected float heartDelay = 30.0F;
   protected float heartDelayMax = 30.0F;
   protected long heartEventInstance;
   protected float DrunkOscilatorStepSin = 0.0F;
   protected float DrunkOscilatorRateSin = 0.1F;
   protected float DrunkOscilatorStepCos = 0.0F;
   protected float DrunkOscilatorRateCos = 0.1F;
   protected float DrunkOscilatorStepCos2 = 0.0F;
   protected float DrunkOscilatorRateCos2 = 0.07F;
   protected float DrunkSin = 0.0F;
   protected float DrunkCos = 23784.0F;
   protected float DrunkCos2 = 61616.0F;
   protected float MinOscilatorRate = 0.01F;
   protected float MaxOscilatorRate = 0.15F;
   protected float DesiredSinRate = 0.0F;
   protected float DesiredCosRate = 0.0F;
   protected float OscilatorChangeRate = 0.05F;
   public float maxWeightDelta = 1.0F;
   protected String Forname = "Bob";
   protected String Surname = "Smith";
   public float TargetSpeed = 0.0F;
   public float CurrentSpeed = 0.0F;
   public float MaxSpeed = 0.09F;
   public float SpeedChange = 0.007F;
   protected IsoSprite GuardModeUISprite;
   protected int GuardModeUI = 0;
   protected IsoSurvivor GuardChosen = null;
   protected IsoGridSquare GuardStand = null;
   protected IsoGridSquare GuardFace = null;
   private boolean bMultiplayer;
   public String SaveFileName;
   private String SaveFileIP;
   private BaseVehicle vehicle4testCollision = null;
   public boolean isCharging = false;
   public boolean isChargingLT = false;
   public boolean bSneaking = false;
   protected boolean bRunning = false;
   protected boolean bWasRunning = false;
   public boolean bRightClickMove = false;
   protected boolean bChangeCharacterDebounce = false;
   protected Vector2 runAngle = new Vector2();
   protected int followID = 0;
   protected Stack FollowCamStack = new Stack();
   public static boolean DemoMode = false;
   protected static int FollowDeadCount = 240;
   protected boolean bSeenThisFrame = false;
   protected boolean bCouldBeSeenThisFrame = false;
   public int TimeSprinting = 0;
   public int TimeSinceRightClick = 0;
   public int TimeRightClicking = 1110;
   public int LastTimeRightClicking = 1110;
   public boolean JustMoved = false;
   public boolean ControllerRun = true;
   public boolean L3Pressed = false;
   public boolean bDoubleClick = false;
   public float AimRadius = 0.0F;
   public float DesiredAimRadius = 0.0F;
   static int lmx = -1;
   static int lmy = -1;
   public boolean bBannedAttacking = false;
   public float EffectiveAimDistance = 0.0F;
   private static Vector2 tempVector2 = new Vector2();
   public float timePressedContext = 0.0F;
   private static float TIME_RIGHT_PRESSED_SECONDS = 0.15F;
   float TimeRightPressed = 0.0F;
   int TimeLeftPressed = 0;
   public float chargeTime = 0.0F;
   public float useChargeTime = 0.0F;
   public boolean bPressContext = false;
   HandWeapon lastWeapon = null;
   String strafeAnim = "";
   String strafeRAnim = "";
   String walkAnim = "";
   String walkRAnim = "";
   public float numNearbyBuildingsRooms = 0.0F;
   private int checkNearbyRooms = 0;
   static OnceEvery networkUpdate = new OnceEvery(0.1F);
   static OnceEvery networkUpdate2 = new OnceEvery(0.1F);
   BaseSoundEmitter testemitter;
   long Checksum = 0L;
   float lastdist = 0.0F;
   int checkSafehouse = 200;
   private boolean bUseVehicle = false;
   private boolean bUsedVehicle;
   private float useVehicleDuration;
   private Vector3f tempVector3f = new Vector3f();
   private UpdateLimit ULbeatenVehicle = new UpdateLimit(200L);
   private boolean flickTorch = false;
   public float ContextPanic = 0.0F;
   private int ticksSincePressedMovement = 0;
   static String printString = "";
   public static IsoPlayer[] players = new IsoPlayer[4];
   public int JoypadBind = -1;
   public float useChargeDelta = 0.0F;
   protected int timeSinceLastStab = 0;
   protected Stack LastSpotted = new Stack();
   protected int ClearSpottedTimer = -1;
   public boolean DebounceA = false;
   double lastSeenZombieTime = 2.0D;
   public int PlayerIndex = 0;
   public boolean mpTorchCone = false;
   public float mpTorchDist = 0.0F;
   public float mpTorchStrength = 0.0F;
   public static int assumedPlayer = 0;
   public static int numPlayers = 1;
   public int OnlineID = 1;
   public int OnlineChunkGridWidth;
   public PlayerNetHistory netHistory = new PlayerNetHistory();
   public boolean bJoypadMovementActive = true;
   public boolean bJoypadIgnoreAimUntilCentered;
   private long steamID;
   public boolean targetedByZombie = false;
   public float lastTargeted = 1.0E8F;
   public float TimeSinceOpenDoor;
   public boolean bRemote;
   public int TimeSinceLastNetData = 0;
   public String accessLevel = "";
   public String tagPrefix = "";
   public boolean showTag = true;
   public boolean factionPvp = false;
   private IsoPlayer.VehicleContainerData vehicleContainerData = new IsoPlayer.VehicleContainerData();
   private int hypothermiaCache = -1;
   private int hyperthermiaCache = -1;

   public static byte[] createChecksum(String var0) throws Exception {
      FileInputStream var1 = new FileInputStream(var0);
      byte[] var2 = new byte[1024];
      MessageDigest var3 = MessageDigest.getInstance("MD5");

      int var4;
      do {
         var4 = var1.read(var2);
         if (var4 > 0) {
            var3.update(var2, 0, var4);
         }
      } while(var4 != -1);

      var1.close();
      return var3.digest();
   }

   public static String getMD5Checksum(String var0) throws Exception {
      byte[] var1 = createChecksum(var0);
      String var2 = "";

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2 = var2 + Integer.toString((var1[var3] & 255) + 256, 16).substring(1);
      }

      return var2;
   }

   public void TestZombieSpotPlayer(IsoMovingObject var1) {
      var1.spotted(this, false);
      if (var1 instanceof IsoZombie) {
         float var2 = var1.DistTo(this);
         if (var2 < this.closestZombie && !((IsoZombie)var1).isOnFloor()) {
            this.closestZombie = var2;
         }
      }

   }

   public float getPathSpeed() {
      float var1 = this.getMoveSpeed() * 0.9F;
      switch(this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
      case 1:
         var1 *= 0.95F;
         break;
      case 2:
         var1 *= 0.9F;
         break;
      case 3:
         var1 *= 0.8F;
         break;
      case 4:
         var1 *= 0.6F;
      }

      if (this.stats.enduranceRecharging) {
         var1 *= 0.85F;
      }

      if (this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 0) {
         float var2 = this.getInventory().getCapacityWeight();
         float var3 = (float)this.getMaxWeight();
         float var4 = Math.min(2.0F, var2 / var3) - 1.0F;
         var1 *= 0.65F + 0.35F * (1.0F - var4);
      }

      return var1;
   }

   public static boolean DoChecksumCheck(String var0, String var1) {
      String var2 = "";

      try {
         var2 = getMD5Checksum(var0);
         if (!var2.equals(var1)) {
            return false;
         }
      } catch (Exception var6) {
         var2 = "";

         try {
            var2 = getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + var0);
         } catch (Exception var5) {
            return false;
         }
      }

      return var2.equals(var1);
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

   public boolean isGhostMode() {
      return this.GhostMode;
   }

   public void setGhostMode(boolean var1) {
      this.GhostMode = var1;
   }

   public static IsoPlayer getInstance() {
      return instance;
   }

   public static void setInstance(IsoPlayer var0) {
      instance = var0;
   }

   public Vector2 getPlayerMoveDir() {
      return this.playerMoveDir;
   }

   public void setPlayerMoveDir(Vector2 var1) {
      this.playerMoveDir = var1;
   }

   public boolean isIsAiming() {
      return this.isAiming;
   }

   public boolean isAiming() {
      return this.isAiming;
   }

   public void setIsAiming(boolean var1) {
      this.isAiming = var1;
   }

   public void nullifyAiming() {
      this.isCharging = false;
      this.TimeLeftPressed = 0;
      this.PlayAnim("Idle");
      this.isAiming = false;
   }

   public boolean isAimKeyDown() {
      if (this.PlayerIndex != 0) {
         return false;
      } else {
         int var1 = Core.getInstance().getKey("Aim");
         boolean var2 = GameKeyboard.isKeyDown(var1);
         if (!var2) {
            return false;
         } else {
            boolean var3 = var1 == 29 || var1 == 157;
            return !var3 || !UIManager.isMouseOverInventory();
         }
      }
   }

   public static Stack getStaticTraits() {
      return StaticTraits;
   }

   public static void setStaticTraits(Stack var0) {
      StaticTraits = var0;
   }

   public static int getFollowDeadCount() {
      return FollowDeadCount;
   }

   public static void setFollowDeadCount(int var0) {
      FollowDeadCount = var0;
   }

   public IsoPlayer(IsoCell var1) {
      super(var1, 0.0F, 0.0F, 0.0F);
      this.bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
      if (Core.bDebug) {
      }

      this.GuardModeUISprite = new IsoSprite(this.getCell().SpriteManager);
      this.GuardModeUISprite.LoadFrameExplicit("TileFloorInt_0");

      for(int var2 = 0; var2 < StaticTraits.size(); ++var2) {
         this.Traits.add(StaticTraits.get(var2));
      }

      StaticTraits.clear();
      this.dir = IsoDirections.W;
      this.descriptor = new SurvivorDesc();
      this.PathSpeed = 0.08F;
      this.descriptor.Instance = this;
      if (!GameClient.bClient && !GameServer.bServer) {
         instance = this;
      }

      this.SpeakColour = new Color(Rand.Next(135) + 120, Rand.Next(135) + 120, Rand.Next(135) + 120, 255);
      if (this.HasTrait("Strong")) {
         this.maxWeightDelta = 1.5F;
      }

      if (this.HasTrait("Weak")) {
         this.maxWeightDelta = 0.75F;
      }

      if (this.HasTrait("Feeble")) {
         this.maxWeightDelta = 0.9F;
      }

      if (this.HasTrait("Stout")) {
         this.maxWeightDelta = 1.25F;
      }

      if (this.HasTrait("Injured")) {
         this.getBodyDamage().AddRandomDamage();
      }

      if (FrameLoader.bClient) {
      }

      this.descriptor.temper = 5.0F;
      if (this.HasTrait("ShortTemper")) {
         this.descriptor.temper = 7.5F;
      } else if (this.HasTrait("Patient")) {
         this.descriptor.temper = 2.5F;
      }

      this.nutrition = new Nutrition(this);
      this.bMultiplayer = GameServer.bServer || GameClient.bClient;
      this.zombiesToSend = new ZombieSortedList(this, 20);
      this.vehicle4testCollision = null;
      if (Core.bDebug) {
         this.GhostMode = true;
         this.godMod = true;
      }

   }

   public IsoPlayer(IsoCell var1, SurvivorDesc var2, int var3, int var4, int var5) {
      super(var1, (float)var3, (float)var4, (float)var5);
      this.bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");

      for(int var6 = 0; var6 < StaticTraits.size(); ++var6) {
         this.Traits.add(StaticTraits.get(var6));
      }

      StaticTraits.clear();
      this.dir = IsoDirections.W;
      this.nutrition = new Nutrition(this);
      this.descriptor = new SurvivorDesc();
      this.PathSpeed = 0.08F;
      this.bFemale = var2.isFemale();
      this.Dressup(var2);
      this.InitSpriteParts(var2, var2.legs, var2.torso, var2.head, var2.top, var2.bottoms, var2.shoes, var2.skinpal, var2.toppal, var2.bottomspal, var2.shoespal, var2.hair, var2.extra);
      this.descriptor = var2;
      LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
      if (!GameClient.bClient && !GameServer.bServer) {
         instance = this;
      }

      this.descriptor.Instance = this;
      this.SpeakColour = new Color(Rand.Next(135) + 120, Rand.Next(135) + 120, Rand.Next(135) + 120, 255);
      if (GameClient.bClient) {
         if (Core.getInstance().getMpTextColor() != null) {
            this.SpeakColour = new Color(Core.getInstance().getMpTextColor().r, Core.getInstance().getMpTextColor().g, Core.getInstance().getMpTextColor().b, 1.0F);
         } else {
            Core.getInstance().setMpTextColor(new ColorInfo(this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 1.0F));

            try {
               Core.getInstance().saveOptions();
            } catch (IOException var7) {
               var7.printStackTrace();
            }
         }
      }

      if (Core.GameMode.equals("LastStand")) {
         this.Traits.add("Strong");
      }

      if (this.HasTrait("Strong")) {
         this.maxWeightDelta = 1.5F;
      }

      if (this.HasTrait("Weak")) {
         this.maxWeightDelta = 0.75F;
      }

      if (this.HasTrait("Feeble")) {
         this.maxWeightDelta = 0.9F;
      }

      if (this.HasTrait("Stout")) {
         this.maxWeightDelta = 1.25F;
      }

      this.descriptor.temper = 5.0F;
      if (this.HasTrait("ShortTemper")) {
         this.descriptor.temper = 7.5F;
      } else if (this.HasTrait("Patient")) {
         this.descriptor.temper = 2.5F;
      }

      Core.getInstance().refreshOffscreen();
      if (this.HasTrait("Injured")) {
         this.getBodyDamage().AddRandomDamage();
      }

      this.bMultiplayer = GameServer.bServer || GameClient.bClient;
      this.zombiesToSend = new ZombieSortedList(this, 20);
      this.vehicle4testCollision = null;
      if (Core.bDebug) {
         this.GhostMode = true;
         this.godMod = true;
      }

   }

   public boolean IsSneaking() {
      return this.bSneaking;
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      var1.get();
      var1.getInt();
      IsoPlayer var3 = instance;
      instance = this;

      try {
         super.load(var1, var2);
      } finally {
         instance = var3;
      }

      this.setHoursSurvived(var1.getDouble());
      if (var2 < 142 && this.getBodyDamage().getInfectionTime() > 0.0F) {
         float var4 = (float)Math.max(GameTime.getInstance().getWorldAgeHours() - (double)this.getBodyDamage().getInfectionTime(), 0.0D);
         this.getBodyDamage().setInfectionTime((float)Math.max(this.getHoursSurvived() - (double)var4, 0.0D));
      }

      SurvivorDesc var12 = this.descriptor;
      this.bFemale = var12.isFemale();
      this.InitSpriteParts(var12, var12.legs, var12.torso, var12.head, var12.top, var12.bottoms, var12.shoes, var12.skinpal, var12.toppal, var12.bottomspal, var12.shoespal, var12.hair, var12.extra);
      if (!GameClient.bClient && !GameServer.bServer) {
         instance = this;
      }

      this.SpeakColour = new Color(Rand.Next(135) + 120, Rand.Next(135) + 120, Rand.Next(135) + 120, 255);
      if (GameClient.bClient) {
         if (Core.getInstance().getMpTextColor() != null) {
            this.SpeakColour = new Color(Core.getInstance().getMpTextColor().r, Core.getInstance().getMpTextColor().g, Core.getInstance().getMpTextColor().b, 1.0F);
         } else {
            Core.getInstance().setMpTextColor(new ColorInfo(this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 1.0F));

            try {
               Core.getInstance().saveOptions();
            } catch (IOException var10) {
               var10.printStackTrace();
            }
         }
      }

      this.PathSpeed = 0.07F;
      this.setZombieKills(var1.getInt());
      String var5;
      String var6;
      if (var1.getInt() == 1) {
         var5 = GameWindow.ReadString(var1);
         var6 = GameWindow.ReadString(var1);
         this.SetClothing(Item.ClothingBodyLocation.Top, var6, var5);
         this.topSprite.TintMod.r = var1.getFloat();
         this.topSprite.TintMod.g = var1.getFloat();
         this.topSprite.TintMod.b = var1.getFloat();
      }

      if (var1.getInt() == 1) {
         var5 = GameWindow.ReadString(var1);
         var6 = GameWindow.ReadString(var1);
         this.SetClothing(Item.ClothingBodyLocation.Shoes, var6, var5);
      }

      if (var1.getInt() == 1) {
         var5 = GameWindow.ReadString(var1);
         var6 = GameWindow.ReadString(var1);
      }

      if (var1.getInt() == 1) {
         var5 = GameWindow.ReadString(var1);
         var6 = GameWindow.ReadString(var1);
         this.SetClothing(Item.ClothingBodyLocation.Bottoms, var6, var5);
         this.bottomsSprite.TintMod.r = var1.getFloat();
         this.bottomsSprite.TintMod.g = var1.getFloat();
         this.bottomsSprite.TintMod.b = var1.getFloat();
      }

      if (var2 >= 46) {
         ArrayList var13 = this.savedInventoryItems;
         short var15 = var1.getShort();
         if (var15 >= 0 && var15 < var13.size()) {
            this.ClothingItem_Torso = (InventoryItem)var13.get(var15);
         }

         var15 = var1.getShort();
         if (var15 >= 0 && var15 < var13.size()) {
            this.ClothingItem_Legs = (InventoryItem)var13.get(var15);
         }

         var15 = var1.getShort();
         if (var15 >= 0 && var15 < var13.size()) {
            this.ClothingItem_Feet = (InventoryItem)var13.get(var15);
         }

         var15 = var1.getShort();
         if (var15 >= 0 && var15 < var13.size()) {
            this.ClothingItem_Back = (InventoryItem)var13.get(var15);
         }

         var15 = var1.getShort();
         if (var15 >= 0 && var15 < var13.size()) {
            this.leftHandItem = (InventoryItem)var13.get(var15);
         }

         var15 = var1.getShort();
         if (var15 >= 0 && var15 < var13.size()) {
            this.rightHandItem = (InventoryItem)var13.get(var15);
         }
      }

      this.setSurvivorKills(var1.getInt());
      int var14 = var1.getInt();
      this.initSpritePartsEmpty();
      if (var2 < 57) {
         this.createKeyRing();
      }

      if (var2 >= 81) {
         this.nutrition.load(var1);
      }

      if (var2 >= 99) {
         this.setAllChatMuted(var1.get() == 1);
         this.tagPrefix = GameWindow.ReadString(var1);
         this.setTagColor(new ColorInfo(var1.getFloat(), var1.getFloat(), var1.getFloat(), 1.0F));
      }

      if (var2 >= 100 && GameClient.bClient && !ServerOptions.instance.Open.getValue() && ServerOptions.instance.SaveTransactionID.getValue()) {
         this.setTransactionID(var1.getInt());
      } else if (var2 >= 104) {
         this.setTransactionID(var1.getInt());
      }

      if (var2 >= 100) {
         this.setDisplayName(GameWindow.ReadString(var1));
      }

      if (var2 >= 103) {
         this.showTag = var1.get() == 1;
         this.factionPvp = var1.get() == 1;
      }

      if (var2 >= 91 && var1.get() == 1) {
         this.savedVehicleX = var1.getFloat();
         this.savedVehicleY = var1.getFloat();
         this.savedVehicleSeat = (short)var1.get();
         this.savedVehicleRunning = var1.get() == 1;
      }

      if (var2 >= 120) {
         int var16 = var1.getInt();

         for(int var7 = 0; var7 < var16; ++var7) {
            this.mechanicsItem.put(var1.getLong(), var1.getLong());
         }
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      IsoPlayer var2 = instance;
      instance = this;

      try {
         super.save(var1);
      } finally {
         instance = var2;
      }

      var1.putDouble(this.getHoursSurvived());
      var1.putInt(this.getZombieKills());
      if (this.getClothingItem_Torso() != null && this.topSprite != null) {
         var1.putInt(1);
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Torso()).getPalette());
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Torso()).getSpriteName());
         var1.putFloat(this.topSprite.TintMod.r);
         var1.putFloat(this.topSprite.TintMod.g);
         var1.putFloat(this.topSprite.TintMod.b);
      } else {
         var1.putInt(0);
      }

      if (this.getClothingItem_Feet() != null) {
         var1.putInt(1);
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Feet()).getPalette());
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Feet()).getSpriteName());
      } else {
         var1.putInt(0);
      }

      if (this.getClothingItem_Hands() != null) {
         var1.putInt(1);
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Hands()).getPalette());
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Hands()).getSpriteName());
      } else {
         var1.putInt(0);
      }

      if (this.getClothingItem_Legs() != null && this.bottomsSprite != null) {
         var1.putInt(1);
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Legs()).getPalette());
         GameWindow.WriteString(var1, ((Clothing)this.getClothingItem_Legs()).getSpriteName());
         var1.putFloat(this.bottomsSprite.TintMod.r);
         var1.putFloat(this.bottomsSprite.TintMod.g);
         var1.putFloat(this.bottomsSprite.TintMod.b);
      } else {
         var1.putInt(0);
      }

      var1.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Torso()));
      var1.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Legs()));
      var1.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Feet()));
      var1.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Back()));
      var1.putShort((short)this.savedInventoryItems.indexOf(this.getPrimaryHandItem()));
      var1.putShort((short)this.savedInventoryItems.indexOf(this.getSecondaryHandItem()));
      var1.putInt(this.getSurvivorKills());
      var1.putInt(this.PlayerIndex);
      this.nutrition.save(var1);
      var1.put((byte)(this.isAllChatMuted() ? 1 : 0));
      GameWindow.WriteString(var1, this.tagPrefix);
      var1.putFloat(this.getTagColor().r);
      var1.putFloat(this.getTagColor().g);
      var1.putFloat(this.getTagColor().b);
      var1.putInt(this.transactionID);
      GameWindow.WriteString(var1, this.displayName);
      var1.put((byte)(this.showTag ? 1 : 0));
      var1.put((byte)(this.factionPvp ? 1 : 0));
      if (this.vehicle != null) {
         var1.put((byte)1);
         var1.putFloat(this.vehicle.x);
         var1.putFloat(this.vehicle.y);
         var1.put((byte)this.vehicle.getSeat(this));
         var1.put((byte)(this.vehicle.isEngineRunning() ? 1 : 0));
      } else {
         var1.put((byte)0);
      }

      var1.putInt(this.mechanicsItem.size());
      Iterator var3 = this.mechanicsItem.keySet().iterator();

      while(var3.hasNext()) {
         Long var4 = (Long)var3.next();
         var1.putLong(var4);
         var1.putLong((Long)this.mechanicsItem.get(var4));
      }

   }

   public void save() throws IOException {
      SliceY.SliceBuffer.rewind();
      SliceY.SliceBuffer.put((byte)80);
      SliceY.SliceBuffer.put((byte)76);
      SliceY.SliceBuffer.put((byte)89);
      SliceY.SliceBuffer.put((byte)82);
      SliceY.SliceBuffer.putInt(143);
      GameWindow.WriteString(SliceY.SliceBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
      SliceY.SliceBuffer.putInt((int)(this.x / 10.0F));
      SliceY.SliceBuffer.putInt((int)(this.y / 10.0F));
      SliceY.SliceBuffer.putInt((int)this.x);
      SliceY.SliceBuffer.putInt((int)this.y);
      SliceY.SliceBuffer.putInt((int)this.z);
      this.save(SliceY.SliceBuffer);
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
      if (!Core.getInstance().isNoSave()) {
         FileOutputStream var2 = new FileOutputStream(var1);
         Throwable var3 = null;

         try {
            BufferedOutputStream var4 = new BufferedOutputStream(var2);
            Throwable var5 = null;

            try {
               var4.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
            } catch (Throwable var28) {
               var5 = var28;
               throw var28;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var27) {
                        var5.addSuppressed(var27);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (Throwable var30) {
            var3 = var30;
            throw var30;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var26) {
                     var3.addSuppressed(var26);
                  }
               } else {
                  var2.close();
               }
            }

         }
      }

   }

   public void save(String var1) throws IOException {
      this.SaveFileName = var1;
      File var2 = new File(var1);
      FileOutputStream var3 = new FileOutputStream(var2);
      BufferedOutputStream var4 = new BufferedOutputStream(var3);
      SliceY.SliceBuffer.rewind();
      SliceY.SliceBuffer.putInt(143);
      GameWindow.WriteString(SliceY.SliceBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
      this.save(SliceY.SliceBuffer);
      var4.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
      var4.flush();
      var4.close();
   }

   public void load(String var1) throws FileNotFoundException, IOException {
      File var2 = new File(var1);
      if (var2.exists()) {
         this.SaveFileName = var1;
         FileInputStream var3 = new FileInputStream(var2);
         BufferedInputStream var4 = new BufferedInputStream(var3);
         synchronized(SliceY.SliceBuffer) {
            SliceY.SliceBuffer.rewind();
            var4.read(SliceY.SliceBuffer.array());
            int var6 = SliceY.SliceBuffer.getInt();
            if (var6 >= 69) {
               this.SaveFileIP = GameWindow.ReadStringUTF(SliceY.SliceBuffer);
               if (var6 < 71) {
                  this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
               }
            } else if (GameClient.bClient) {
               this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
            }

            this.load(SliceY.SliceBuffer, var6);
            var3.close();
         }
      }
   }

   public static ArrayList getAllFileNames() {
      ArrayList var0 = new ArrayList();
      String var1 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;

      for(int var2 = 1; var2 < 100; ++var2) {
         File var3 = new File(var1 + File.separator + "map_p" + var2 + ".bin");
         if (var3.exists()) {
            var0.add("map_p" + var2 + ".bin");
         }
      }

      return var0;
   }

   public static String getUniqueFileName() {
      int var0 = 0;
      String var1 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;

      for(int var2 = 1; var2 < 100; ++var2) {
         File var3 = new File(var1 + File.separator + "map_p" + var2 + ".bin");
         if (var3.exists()) {
            var0 = var2;
         }
      }

      ++var0;
      return GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p" + var0 + ".bin";
   }

   public void setVehicle4TestCollision(BaseVehicle var1) {
      this.vehicle4testCollision = var1;
   }

   public static ArrayList getAllSavedPlayers() {
      ArrayList var0 = new ArrayList();
      IsoPlayer var1 = instance;
      String var2 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;

      for(int var3 = 1; var3 < 100; ++var3) {
         File var4 = new File(var2 + File.separator + "map_p" + var3 + ".bin");
         if (var4.exists()) {
            try {
               IsoPlayer var5 = new IsoPlayer(IsoWorld.instance.CurrentCell);

               try {
                  var5.load(var2 + File.separator + "map_p" + var3 + ".bin");
               } finally {
                  instance = var1;
               }

               var0.add(var5);
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      }

      return var0;
   }

   public boolean isSaveFileInUse() {
      if (this.SaveFileName == null) {
         return false;
      } else {
         for(int var1 = 0; var1 < numPlayers; ++var1) {
            if (players[var1] != null && this.SaveFileName.equals(players[var1].SaveFileName)) {
               return true;
            }
         }

         return false;
      }
   }

   public void removeSaveFile() {
      try {
         File var1;
         if (this == players[0]) {
            var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
            if (var1.exists()) {
               var1.delete();
            }
         } else if (this.SaveFileName != null) {
            var1 = new File(this.SaveFileName);
            if (var1.exists()) {
               var1.delete();
            }
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public boolean isSaveFileIPValid() {
      return isServerPlayerIDValid(this.SaveFileIP);
   }

   public static boolean isServerPlayerIDValid(String var0) {
      if (GameClient.bClient) {
         String var1 = ServerOptions.instance.ServerPlayerID.getValue();
         return var1 != null && !var1.isEmpty() ? var1.equals(var0) : true;
      } else {
         return true;
      }
   }

   public String getObjectName() {
      return "Player";
   }

   public void collideWith(IsoObject var1) {
   }

   public int getJoypadBind() {
      return this.JoypadBind;
   }

   public boolean isLBPressed() {
      return this.JoypadBind == -1 ? false : JoypadManager.instance.isLBPressed(this.JoypadBind);
   }

   public Vector2 getControllerAimDir(Vector2 var1) {
      if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive) {
         float var2 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
         float var3 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
         if (this.bJoypadIgnoreAimUntilCentered) {
            if (var1.set(var2, var3).getLength() > 0.0F) {
               return var1.set(0.0F, 0.0F);
            }

            this.bJoypadIgnoreAimUntilCentered = false;
         }

         if (var1.set(var2, var3).getLength() < 0.3F) {
            var3 = 0.0F;
            var2 = 0.0F;
         }

         if (var2 == 0.0F && var3 == 0.0F) {
            return var1.set(0.0F, 0.0F);
         }

         var1.set(var2, var3);
         var1.normalize();
         var1.rotate(-0.7853982F);
         this.angle.x = var1.x;
         this.angle.y = var1.y;
         if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
            this.dirtyRecalcGridStackTime = 10.0F;
         }
      }

      return var1;
   }

   public IsoObject getInteract() {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      if (this.dir == IsoDirections.N) {
         --var4;
         --var6;
      }

      if (this.dir == IsoDirections.NE) {
         --var4;
         --var6;
         ++var1;
         ++var6;
      }

      if (this.dir == IsoDirections.E) {
         ++var1;
         ++var5;
      }

      if (this.dir == IsoDirections.SE) {
         ++var1;
         ++var5;
         ++var2;
         ++var6;
      }

      if (this.dir == IsoDirections.S) {
         ++var2;
         ++var6;
      }

      if (this.dir == IsoDirections.SW) {
         ++var2;
         ++var6;
         --var3;
         --var5;
      }

      if (this.dir == IsoDirections.W) {
         --var3;
         --var5;
      }

      if (this.dir == IsoDirections.NW) {
         --var3;
         --var4;
         --var5;
         --var6;
      }

      IsoGridSquare var7 = this.getCell().getGridSquare((int)this.getX() + var5, (int)(this.getY() + (float)var6), (int)this.getZ());
      IsoGridSquare var8 = this.getCell().getGridSquare((int)this.getX(), (int)this.getY(), (int)this.getZ());
      IsoGridSquare var9 = this.getCell().getGridSquare((int)(this.getX() + (float)var1), (int)this.getY(), (int)this.getZ());
      IsoGridSquare var10 = this.getCell().getGridSquare((int)this.getX(), (int)(this.getY() + (float)var2), (int)this.getZ());
      IsoGridSquare var11 = this.getCell().getGridSquare((int)(this.getX() - (float)var3), (int)this.getY(), (int)this.getZ());
      IsoGridSquare var12 = this.getCell().getGridSquare((int)this.getX(), (int)(this.getY() - (float)var4), (int)this.getZ());
      int var13;
      IsoObject var14;
      if (var8 != null) {
         for(var13 = 0; var13 < var8.getObjects().size(); ++var13) {
            var14 = (IsoObject)var8.getObjects().get(var13);
            if (var14.container != null) {
               return var14;
            }
         }
      }

      if (var7 != null) {
         for(var13 = 0; var13 < var7.getObjects().size(); ++var13) {
            var14 = (IsoObject)var7.getObjects().get(var13);
            if (var14.container != null) {
               return var14;
            }
         }
      }

      if (var5 != 0 && var6 != 0) {
         IsoGridSquare var17 = this.getCell().getGridSquare((int)this.getX() + var5, (int)this.getY(), (int)this.getZ());
         IsoGridSquare var18 = this.getCell().getGridSquare((int)this.getX(), (int)this.getY() + var6, (int)this.getZ());
         int var15;
         IsoObject var16;
         if (var17 != null) {
            for(var15 = 0; var15 < var17.getObjects().size(); ++var15) {
               var16 = (IsoObject)var17.getObjects().get(var15);
               if (var16.container != null) {
                  return var16;
               }
            }
         }

         if (var18 != null) {
            for(var15 = 0; var15 < var18.getObjects().size(); ++var15) {
               var16 = (IsoObject)var18.getObjects().get(var15);
               if (var16.container != null) {
                  return var16;
               }
            }
         }
      }

      if (var8 != null && var8.getSpecialObjects().size() > 0) {
         for(var13 = 0; var13 < var8.getObjects().size(); ++var13) {
            var14 = (IsoObject)var8.getObjects().get(var13);
            if (var14 instanceof IsoDoor || var14 instanceof IsoThumpable && ((IsoThumpable)var14).isDoor) {
               return var14;
            }
         }
      } else if (var9 != null && var9.getSpecialObjects().size() > 0) {
         for(var13 = 0; var13 < var9.getSpecialObjects().size(); ++var13) {
            var14 = (IsoObject)var9.getSpecialObjects().get(var13);
            if (var14 instanceof IsoDoor || var14 instanceof IsoThumpable && ((IsoThumpable)var14).isDoor) {
               return var14;
            }
         }
      } else if (var10 != null && var10.getSpecialObjects().size() > 0) {
         for(var13 = 0; var13 < var10.getSpecialObjects().size(); ++var13) {
            var14 = (IsoObject)var10.getSpecialObjects().get(var13);
            if (var14 instanceof IsoDoor || var14 instanceof IsoThumpable && ((IsoThumpable)var14).isDoor) {
               return var14;
            }
         }
      } else if (var11 != null && var10.getSpecialObjects().size() > 0) {
         for(var13 = 0; var13 < var11.getSpecialObjects().size(); ++var13) {
            var14 = (IsoObject)var11.getSpecialObjects().get(var13);
            if (var14 instanceof IsoDoor || var14 instanceof IsoThumpable && ((IsoThumpable)var14).isDoor) {
               return var14;
            }
         }
      } else if (var12 != null && var10.getSpecialObjects().size() > 0) {
         for(var13 = 0; var13 < var12.getSpecialObjects().size(); ++var13) {
            var14 = (IsoObject)var12.getSpecialObjects().get(var13);
            if (var14 instanceof IsoDoor || var14 instanceof IsoThumpable && ((IsoThumpable)var14).isDoor) {
               return var14;
            }
         }
      }

      return null;
   }

   public float getMoveSpeed() {
      float var1 = 1.0F;

      for(int var2 = BodyPartType.ToIndex(BodyPartType.UpperLeg_L); var2 <= BodyPartType.ToIndex(BodyPartType.Foot_R); ++var2) {
         BodyPart var3 = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var2));
         float var4 = 1.0F;
         if (var3.getFractureTime() > 20.0F) {
            var4 = 0.4F;
            if (var3.getFractureTime() > 50.0F) {
               var4 = 0.3F;
            }

            if (var3.getSplintFactor() > 0.0F) {
               var4 += var3.getSplintFactor() / 10.0F;
            }
         }

         if (var3.getFractureTime() < 20.0F && var3.getSplintFactor() > 0.0F) {
            var4 = 0.8F;
         }

         if (var4 > 0.7F && var3.getDeepWoundTime() > 0.0F) {
            var4 = 0.7F;
            if (var3.bandaged()) {
               var4 += 0.2F;
            }
         }

         if (var4 < var1) {
            var1 = var4;
         }
      }

      if (var1 != 1.0F) {
         return this.MoveSpeed * var1;
      } else if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 4 && this.HasTrait("AdrenalineJunkie")) {
         float var5 = 1.0F;
         int var6 = this.getMoodles().getMoodleLevel(MoodleType.Panic) + 1;
         var5 += (float)var6 / 50.0F;
         return this.MoveSpeed * var5;
      } else {
         return this.MoveSpeed;
      }
   }

   public void setMoveSpeed(float var1) {
      this.MoveSpeed = var1;
   }

   public float getTorchStrength() {
      if (this.bRemote) {
         return this.mpTorchStrength;
      } else {
         float var1 = 0.0F;
         if (this.leftHandItem != null && this.leftHandItem.getLightStrength() > 0.0F && (this.leftHandItem instanceof Drainable && ((Drainable)this.leftHandItem).getUsedDelta() > 0.0F || !(this.leftHandItem instanceof Drainable)) && (this.leftHandItem.canBeActivated() && this.leftHandItem.isActivated() || !this.leftHandItem.canBeActivated())) {
            var1 = this.leftHandItem.getLightStrength();
         }

         if (this.rightHandItem != null && this.rightHandItem.getLightStrength() > 0.0F && (this.rightHandItem instanceof Drainable && ((Drainable)this.rightHandItem).getUsedDelta() > 0.0F || !(this.rightHandItem instanceof Drainable)) && (this.rightHandItem.canBeActivated() && this.rightHandItem.isActivated() || !this.rightHandItem.canBeActivated())) {
            var1 = this.rightHandItem.getLightStrength();
         }

         return var1;
      }
   }

   public void pathFinished() {
      this.stateMachine.changeState(this.defaultState);
      this.path = null;
   }

   public void Scratched() {
      if (this.descriptor.Group != null && this.descriptor.Group.Members.size() > 0) {
         IsoSurvivor var1 = (IsoSurvivor)this.descriptor.Group.getRandomMemberExcept(instance);
      }

   }

   public void Bitten() {
      if (this.descriptor.Group != null && this.descriptor.Group.Members.size() > 0) {
         IsoSurvivor var1 = (IsoSurvivor)this.descriptor.Group.getRandomMemberExcept(instance);
      }

   }

   public float getRadiusKickback(HandWeapon var1) {
      return 15.0F * this.getInvAimingMod();
   }

   public int getChancesToHeadshotHandWeapon() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
      if (var1 == 1) {
         return 2;
      } else if (var1 == 2) {
         return 2;
      } else if (var1 == 3) {
         return 2;
      } else if (var1 == 4) {
         return 2;
      } else if (var1 == 5) {
         return 3;
      } else if (var1 == 6) {
         return 3;
      } else if (var1 == 7) {
         return 3;
      } else if (var1 == 8) {
         return 4;
      } else if (var1 == 9) {
         return 4;
      } else {
         return var1 == 10 ? 4 : 2;
      }
   }

   public float getInvAimingMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
      if (var1 == 1) {
         return 0.9F;
      } else if (var1 == 2) {
         return 0.86F;
      } else if (var1 == 3) {
         return 0.82F;
      } else if (var1 == 4) {
         return 0.74F;
      } else if (var1 == 5) {
         return 0.7F;
      } else if (var1 == 6) {
         return 0.66F;
      } else if (var1 == 7) {
         return 0.62F;
      } else if (var1 == 8) {
         return 0.58F;
      } else if (var1 == 9) {
         return 0.54F;
      } else {
         return var1 == 10 ? 0.5F : 0.9F;
      }
   }

   public float getAimingMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
      if (var1 == 1) {
         return 1.1F;
      } else if (var1 == 2) {
         return 1.14F;
      } else if (var1 == 3) {
         return 1.18F;
      } else if (var1 == 4) {
         return 1.22F;
      } else if (var1 == 5) {
         return 1.26F;
      } else if (var1 == 6) {
         return 1.3F;
      } else if (var1 == 7) {
         return 1.34F;
      } else if (var1 == 8) {
         return 1.36F;
      } else if (var1 == 9) {
         return 1.4F;
      } else {
         return var1 == 10 ? 1.5F : 1.0F;
      }
   }

   public float getReloadingMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Reloading);
      return 3.5F - (float)var1 * 0.25F;
   }

   public float getAimingRangeMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
      if (var1 == 1) {
         return 1.2F;
      } else if (var1 == 2) {
         return 1.28F;
      } else if (var1 == 3) {
         return 1.36F;
      } else if (var1 == 4) {
         return 1.42F;
      } else if (var1 == 5) {
         return 1.5F;
      } else if (var1 == 6) {
         return 1.58F;
      } else if (var1 == 7) {
         return 1.66F;
      } else if (var1 == 8) {
         return 1.72F;
      } else if (var1 == 9) {
         return 1.8F;
      } else {
         return var1 == 10 ? 2.0F : 1.1F;
      }
   }

   public boolean isBannedAttacking() {
      return this.bBannedAttacking;
   }

   public void setBannedAttacking(boolean var1) {
      this.bBannedAttacking = var1;
   }

   public float getInvAimingRangeMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
      if (var1 == 1) {
         return 0.8F;
      } else if (var1 == 2) {
         return 0.7F;
      } else if (var1 == 3) {
         return 0.62F;
      } else if (var1 == 4) {
         return 0.56F;
      } else if (var1 == 5) {
         return 0.45F;
      } else if (var1 == 6) {
         return 0.38F;
      } else if (var1 == 7) {
         return 0.31F;
      } else if (var1 == 8) {
         return 0.24F;
      } else if (var1 == 9) {
         return 0.17F;
      } else {
         return var1 == 10 ? 0.1F : 0.8F;
      }
   }

   public void CalculateAim() {
      if (this.JoypadBind == -1) {
         Vector2 var1 = tempVector2.set(instance.getX(), instance.getY());
         int var2 = Mouse.getX();
         int var3 = Mouse.getY();
         var1.x -= IsoUtils.XToIso((float)(var2 - 0), (float)var3 - 0.0F + 0.0F, instance.getZ());
         var1.y -= IsoUtils.YToIso((float)(var2 - 0), (float)var3 - 0.0F + 0.0F, instance.getZ());
         float var4 = var1.getLength();
         this.EffectiveAimDistance = var4;
         float var5 = var4 / 10.0F;
         if (var5 > 1.0F) {
            var5 *= 2.0F;
         }

         if (var5 < 0.05F) {
            var5 = 0.05F;
         }

         var5 *= this.getInvAimingRangeMod();
         if (this.IsUsingAimWeapon()) {
            this.DesiredAimRadius = var5 * 60.0F;
         } else if (this.IsUsingAimHandWeapon() && this.isCharging) {
            this.DesiredAimRadius = 10.0F;
         } else if (this.IsUsingAimHandWeapon() && !this.isCharging) {
            this.AimRadius = 100.0F;
         }

         if (this.IsUsingAimWeapon() && this.DesiredAimRadius < 10.0F) {
            this.DesiredAimRadius = 10.0F;
         }

         var2 = Mouse.getXA();
         var3 = Mouse.getYA();
         float var6 = IsoUtils.DistanceTo((float)var2, (float)var3, (float)lmx, (float)lmy);
         float var7 = var6 / 30.0F;
         var7 *= this.getInvAimingMod();
         this.AimRadius += var7 * 5.0F;
         if (this.AimRadius > 70.0F) {
            this.AimRadius = 70.0F;
         }

         lmx = var2;
         lmy = var3;
         float var8 = Math.abs(this.AimRadius - this.DesiredAimRadius) / 40.0F;
         var8 *= GameTime.instance.getMultiplier();
         if ((this.getUseHandWeapon() == null || !this.getUseHandWeapon().isAimedHandWeapon()) && this.getUseHandWeapon() != null && !this.getUseHandWeapon().isAimed()) {
            if (this.isCharging) {
               this.DesiredAimRadius += this.chargeTime * 0.05F;
            } else {
               this.DesiredAimRadius = 10.0F;
            }

            if (this.isCharging) {
               this.AimRadius += this.chargeTime * 0.05F;
            } else {
               this.AimRadius = 10.0F;
            }
         }

         if (this.getUseHandWeapon() != null) {
            this.DesiredAimRadius *= this.getUseHandWeapon().getAimingMod();
         }

         if (this.AimRadius > this.DesiredAimRadius) {
            if (var5 <= 0.2F) {
               var8 *= 2.0F;
            }

            if (var5 <= 0.5F) {
               var8 *= 2.0F;
            }

            var8 *= this.getAimingMod();
            if (this.getUseHandWeapon() != null) {
               var8 *= this.getUseHandWeapon().getAimingMod();
            }

            this.AimRadius -= var8;
            if (this.AimRadius < this.DesiredAimRadius) {
               this.AimRadius = this.DesiredAimRadius;
            }
         } else if (this.AimRadius > this.DesiredAimRadius) {
            this.AimRadius += var8;
            if (this.AimRadius > this.DesiredAimRadius) {
               this.AimRadius = this.DesiredAimRadius;
            }
         }

      }
   }

   public void render() {
   }

   public void doBeatenVehicle(float var1, float var2, float var3, boolean var4) {
      if (GameClient.bClient && this.isLocalPlayer() && var4) {
         this.stateMachine.changeState(ForecastBeatenPlayerState.instance());
      } else {
         if (GameClient.bClient && !this.isLocalPlayer() && !var4) {
            GameClient.instance.sendOnBeaten(this, var1, var2, var3);
         }

         float var5 = 1.0F;
         switch(SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue()) {
         case 1:
            var5 = 0.0F;
            break;
         case 2:
            var5 = 0.5F;
         case 3:
         default:
            break;
         case 4:
            var5 = 2.0F;
            break;
         case 5:
            var5 = 5.0F;
         }

         float var6 = var1 * var5;
         if (var6 > 0.0F) {
            int var7 = (int)(2.0F + var6 * 0.07F);

            for(int var8 = 0; var8 < var7; ++var8) {
               int var9 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
               BodyPart var10 = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var9));
               float var11 = Math.max(Rand.Next(var6 - 15.0F, var6), 5.0F);
               if (this.HasTrait("FastHealer")) {
                  var11 = (float)((double)var11 * 0.8D);
               } else if (this.HasTrait("SlowHealer")) {
                  var11 = (float)((double)var11 * 1.2D);
               }

               switch(SandboxOptions.instance.InjurySeverity.getValue()) {
               case 1:
                  var11 *= 0.5F;
                  break;
               case 3:
                  var11 *= 1.5F;
               }

               var11 = (float)((double)var11 * 0.9D);
               var10.AddDamage(var11);
               if (var11 > 40.0F && Rand.Next(12) == 0) {
                  var10.generateDeepWound();
               }

               if (var11 > 10.0F && Rand.Next(100) <= 10 && SandboxOptions.instance.BoneFracture.getValue()) {
                  var10.setFractureTime(Rand.Next(Rand.Next(10.0F, var11 + 10.0F), Rand.Next(var11 + 20.0F, var11 + 30.0F)));
               }

               if (var11 > 30.0F && Rand.Next(100) <= 80 && SandboxOptions.instance.BoneFracture.getValue() && var9 == BodyPartType.ToIndex(BodyPartType.Head)) {
                  var10.setFractureTime(Rand.Next(Rand.Next(10.0F, var11 + 10.0F), Rand.Next(var11 + 20.0F, var11 + 30.0F)));
               }

               if (var11 > 10.0F && Rand.Next(100) <= 60 && SandboxOptions.instance.BoneFracture.getValue() && var9 > BodyPartType.ToIndex(BodyPartType.Groin)) {
                  var10.setFractureTime(Rand.Next(Rand.Next(10.0F, var11 + 20.0F), Rand.Next(var11 + 30.0F, var11 + 40.0F)));
               }
            }

            this.getBodyDamage().Update();
         }

         if (this.getBodyDamage().getOverallBodyHealth() > 0.0F) {
            if (this.stateMachine.getCurrent() != BeatenPlayerState.instance()) {
               this.stateMachine.changeState(BeatenPlayerState.instance());
            } else {
               this.setReanimateTimer((float)(60 + Rand.Next(120)));
            }
         }

      }
   }

   public void update() {
      BaseVehicle var1;
      if (this.vehicle4testCollision != null && this.ULbeatenVehicle.Check() && SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue() > 1) {
         var1 = this.vehicle4testCollision;
         this.vehicle4testCollision = null;
         if (var1.isEngineRunning() && this.getVehicle() != var1) {
            float var2 = var1.jniLinearVelocity.x;
            float var3 = var1.jniLinearVelocity.z;
            if (this.isLocalPlayer()) {
               var2 = var1.netLinearVelocity.x;
               var3 = var1.netLinearVelocity.z;
            }

            float var4 = (float)Math.sqrt((double)(var2 * var2 + var3 * var3));
            Vector2 var5 = var1.testCollisionWithCharacter(this, 0.3F - 0.1F);
            if (var5 != null && var5.x != -1.0F) {
               var5.x = (var5.x - var1.x) * var4 * 1.0F + this.x;
               var5.y = (var5.y - var1.y) * var4 * 1.0F + this.x;
               if (this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.equals("ZombieDeath")) {
                  int var6 = var1.testCollisionWithProneCharacter(this, this.angle.x, this.angle.y, false);
                  if (var6 > 0) {
                     this.doBeatenVehicle(Math.max(var4 * 6.0F, 5.0F), var5.x, var5.y, false);
                  }

                  this.doBeatenVehicle(0.0F, var5.x, var5.y, false);
               } else if (this.getCurrentState() != BeatenPlayerState.instance() && var4 > 0.1F) {
                  this.doBeatenVehicle(Math.max(var4 * 2.0F, 5.0F), var5.x, var5.y, false);
               }
            }
         }
      }

      this.updateEmitter();
      this.updateMechanicsItems();
      this.updateHeavyBreathing();
      this.updateTemperatureCheck();
      if (SystemDisabler.doCharacterStats) {
         this.nutrition.update();
      }

      if (this.isLocalPlayer()) {
         if (this.soundListener == null) {
            this.soundListener = (BaseSoundListener)(Core.SoundDisabled ? new DummySoundListener(this.PlayerIndex) : new SoundListener(this.PlayerIndex));
         }

         this.soundListener.setPos(this.x, this.y, this.z);
         if (--this.checkNearbyRooms <= 0) {
            this.checkNearbyRooms = PerformanceSettings.LockFPS;
            this.numNearbyBuildingsRooms = (float)IsoWorld.instance.MetaGrid.countNearbyBuildingsRooms(this);
         }

         if (this.testemitter == null) {
            this.testemitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
            this.testemitter.setPos(this.x, this.y, this.z);
         }

         this.soundListener.tick();
         this.testemitter.tick();
         if (GameClient.bClient && this.getSafetyCooldown() > 0.0F) {
            this.setSafetyCooldown(this.getSafetyCooldown() - GameTime.instance.getRealworldSecondsSinceLastUpdate());
         }
      }

      if (GameClient.bClient || GameServer.bServer || !this.bDeathFinished) {
         if (!GameClient.bClient && this.getCurrentBuildingDef() != null) {
            this.getCurrentBuildingDef().setHasBeenVisited(true);
         }

         if (this.checkSafehouse > 0 && GameServer.bServer) {
            --this.checkSafehouse;
            if (this.checkSafehouse == 0) {
               this.checkSafehouse = 200;
               SafeHouse var24 = SafeHouse.isSafeHouse(this.getCurrentSquare(), (String)null, false);
               if (var24 != null) {
                  var24.updateSafehouse(this);
               }
            }
         }

         if (this.bRemote && this.TimeSinceLastNetData > 600) {
            IsoWorld.instance.CurrentCell.getObjectList().remove(this);
            if (this.movingSq != null) {
               this.movingSq.getMovingObjects().remove(this);
            }
         }

         this.TimeSinceLastNetData = (int)((float)this.TimeSinceLastNetData + GameTime.instance.getMultiplier());
         this.TimeSinceOpenDoor += GameTime.instance.getMultiplier();
         this.lastTargeted += GameTime.instance.getMultiplier();
         this.targetedByZombie = false;
         Core.getInstance();
         if (!this.bRemote && (!GameClient.bClient || this.NetRemoteState != NetRemoteState_Attack || this.def.Finished)) {
            if (this.isLocalPlayer()) {
               IsoCamera.CamCharacter = this;
               instance = this;
            }

            IsoCamera.update();
            if (this.isLocalPlayer() && UIManager.getMoodleUI((double)this.PlayerIndex) != null) {
               UIManager.getMoodleUI((double)this.PlayerIndex).setCharacter(this);
            }

            if (this.closestZombie > 1.2F) {
               this.slowTimer = -1.0F;
               this.slowFactor = 0.0F;
            }

            this.ContextPanic -= 0.025F * GameTime.instance.getMultiplier();
            if (this.ContextPanic < 0.0F) {
               this.ContextPanic = 0.0F;
            }

            var1 = null;
            if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
               HandWeapon var25 = (HandWeapon)this.getPrimaryHandItem();
               if (this.lastWeapon != var25) {
                  String var26 = var25.getSwingAnim();
                  if (!var26.equals("Bat") && !var26.equals("Handgun") && !var26.equals("Rifle")) {
                     var26 = "Bat";
                  }

                  this.strafeRAnim = "Strafe_Aim_" + var26 + "_R";
                  this.strafeAnim = "Strafe_Aim_" + var26;
                  this.walkAnim = "Walk_Aim_" + var26;
                  this.walkRAnim = "Walk_Aim_" + var26 + "_R";
                  this.lastWeapon = var25;
               }
            } else {
               this.strafeRAnim = "Strafe_R";
               this.strafeAnim = "Strafe";
               this.walkAnim = "Walk";
               this.walkRAnim = "Walk_R";
               this.lastWeapon = var1;
            }

            this.lastSeenZombieTime += (double)(GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 60.0F / 60.0F);
            LuaEventManager.triggerEvent("OnPlayerUpdate", this);
            if (instance.pressedMovement()) {
               instance.ContextPanic = 0.0F;
               this.ticksSincePressedMovement = 0;
            } else {
               ++this.ticksSincePressedMovement;
            }

            if (this.isDead()) {
               if (this.heartEventInstance != 0L) {
                  this.getEmitter().stopSound(this.heartEventInstance);
                  this.heartEventInstance = 0L;
               }

               if (GameClient.bClient) {
                  if (!this.bRemote && !this.bSentDeath) {
                     GameClient.instance.sendDeath(this);
                     this.bSentDeath = true;
                  }
               } else {
                  this.stateMachine.Lock = false;
                  this.stateMachine.changeState(DieState.instance());
                  this.stateMachine.Lock = true;
               }

               super.update();
            } else {
               GameSound var27 = GameSounds.getSound("HeartBeat");
               boolean var28 = var27 != null && var27.userVolume > 0.0F && this.stats.Panic > 0.0F;
               if (!this.Asleep && var28 && GameTime.getInstance().getTrueMultiplier() == 1.0F) {
                  this.heartDelay -= GameTime.getInstance().getMultiplier() / 1.6F;
                  if (this.heartEventInstance == 0L || !this.getEmitter().isPlaying(this.heartEventInstance)) {
                     this.heartEventInstance = this.getEmitter().playSoundImpl("HeartBeat", (IsoObject)null);
                     this.getEmitter().setVolume(this.heartEventInstance, 0.0F);
                  }

                  if (this.heartDelay <= 0.0F) {
                     this.heartDelayMax = (float)((int)((1.0F - this.stats.Panic / 100.0F * 0.7F) * 25.0F) * 2);
                     this.heartDelay = this.heartDelayMax;
                     if (this.heartEventInstance != 0L) {
                        this.getEmitter().setVolume(this.heartEventInstance, this.stats.Panic / 100.0F);
                     }
                  }
               } else if (this.heartEventInstance != 0L) {
                  this.getEmitter().setVolume(this.heartEventInstance, 0.0F);
               }

               if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact")) && !GameKeyboard.wasKeyDown(Core.getInstance().getKey("Interact"))) {
                  this.ContextPanic += 0.6F;
               }

               if (this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState2.instance() && this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbThroughWindowState2.instance() && this.getStateMachine().getCurrent() != OpenWindowState.instance() && this.getStateMachine().getCurrent() != SatChairState.instance() && this.getStateMachine().getCurrent() != SatChairStateOut.instance() && this.getStateMachine().getCurrent() != ClimbSheetRopeState.instance() && this.getStateMachine().getCurrent() != ClimbDownSheetRopeState.instance() && this.getStateMachine().getCurrent() != ForecastBeatenPlayerState.instance() && this.getStateMachine().getCurrent() != BeatenPlayerState.instance() && !this.isAsleep()) {
                  BaseAction var29;
                  if (!this.CharacterActions.isEmpty()) {
                     var29 = (BaseAction)this.CharacterActions.get(0);
                     if (var29.blockMovementEtc) {
                        this.updateLOS();
                        super.update();
                        if (GameClient.bClient && networkUpdate.Check()) {
                           GameClient.instance.sendPlayer(this);
                        }

                        return;
                     }
                  }

                  this.enterExitVehicle();
                  if (!this.CharacterActions.isEmpty()) {
                     var29 = (BaseAction)this.CharacterActions.get(0);
                     if (var29.blockMovementEtc) {
                        this.updateLOS();
                        super.update();
                        if (GameClient.bClient && networkUpdate.Check()) {
                           GameClient.instance.sendPlayer(this);
                        }

                        return;
                     }
                  }

                  if (this.getVehicle() != null) {
                     this.updateWhileInVehicle();
                  } else {
                     this.checkVehicleContainers();
                     this.setCollidable(true);
                     this.CalculateAim();
                     if (!PerkFactory.newMode && this.bCouldBeSeenThisFrame && !this.isbSeenThisFrame() && this.bSneaking) {
                        this.xp.AddXP(PerkFactory.Perks.Sneak, 1.0F);
                        this.xp.AddXP(PerkFactory.Perks.Lightfoot, 1.0F);
                     }

                     this.bSeenThisFrame = false;
                     this.bCouldBeSeenThisFrame = false;
                     if (IsoCamera.CamCharacter == null && GameClient.bClient) {
                        IsoCamera.CamCharacter = instance;
                     }

                     this.timePressedContext += GameTime.instance.getRealworldSecondsSinceLastUpdate();
                     if (this.PlayerIndex == 0) {
                        if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact")) && this.timePressedContext < 0.5F) {
                           this.bPressContext = true;
                        } else {
                           if (this.bPressContext && (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry || !GameKeyboard.doLuaKeyPressed)) {
                              this.bPressContext = false;
                           }

                           if (this.bPressContext) {
                              if (this.doContext(this.dir, false)) {
                                 super.update();
                                 this.timePressedContext = 0.0F;
                                 this.bPressContext = false;
                                 return;
                              }

                              this.timePressedContext = 0.0F;
                           }

                           this.bPressContext = false;
                           this.timePressedContext = 0.0F;
                        }
                     }

                     if (!GameServer.bServer && !this.isBlockMovement()) {
                        int var31;
                        if (Core.bDebug && GameKeyboard.isKeyDown(49) && !GameKeyboard.wasKeyDown(49)) {
                           IsoPlayer var30 = null;

                           for(var31 = 0; var31 < numPlayers; ++var31) {
                              if (players[var31] != null && !players[var31].isDead()) {
                                 var30 = players[var31];
                                 break;
                              }
                           }

                           if (var30 != null && this == var30) {
                              var30.GhostMode = !var30.GhostMode;
                              var30.godMod = !var30.godMod;

                              for(var31 = 0; var31 < numPlayers; ++var31) {
                                 if (players[var31] != null && players[var31] != var30) {
                                    players[var31].GhostMode = var30.GhostMode;
                                    players[var31].godMod = var30.godMod;
                                 }
                              }
                           }
                        }

                        if (this.PlayerIndex == 0 && Core.bDebug && GameKeyboard.isKeyDown(61) && !GameKeyboard.wasKeyDown(61)) {
                           ModelManager.instance.bDebugEnableModels = !ModelManager.instance.bDebugEnableModels;
                        }

                        if (this.PlayerIndex == 0) {
                           if (Core.bDebug && GameKeyboard.isKeyDown(22)) {
                              if (!this.bChangeCharacterDebounce) {
                                 this.FollowCamStack.clear();
                                 this.bChangeCharacterDebounce = true;

                                 for(var31 = 0; var31 < this.getCell().getObjectList().size(); ++var31) {
                                    IsoMovingObject var32 = (IsoMovingObject)this.getCell().getObjectList().get(var31);
                                    if (var32 instanceof IsoSurvivor) {
                                       this.FollowCamStack.add((IsoSurvivor)var32);
                                    }
                                 }

                                 if (!this.FollowCamStack.isEmpty()) {
                                    if (this.followID >= this.FollowCamStack.size()) {
                                       this.followID = 0;
                                    }

                                    IsoCamera.SetCharacterToFollow((IsoGameCharacter)this.FollowCamStack.get(this.followID));
                                    if (UIManager.getSidebar() != null) {
                                       UIManager.sidebar.InventoryFlow.Container = IsoCamera.CamCharacter.inventory;
                                       UIManager.sidebar.MainHand.chr = IsoCamera.CamCharacter;
                                       UIManager.sidebar.SecondHand.chr = IsoCamera.CamCharacter;
                                    }

                                    ++this.followID;
                                 }
                              }
                           } else {
                              this.bChangeCharacterDebounce = false;
                           }
                        }
                     }

                     boolean var33 = false;
                     boolean var35 = false;
                     boolean var34 = false;
                     this.bRunning = false;
                     this.bSneaking = false;
                     ++this.TimeSinceRightClick;
                     this.useChargeTime = this.chargeTime;
                     float var7;
                     float var8;
                     if (!GameServer.bServer && !this.isBlockMovement()) {
                        if (this.sprite.CurrentAnim.name.contains("Attack")) {
                           this.TimeLeftPressed = 16;
                        }

                        if (this.bNewControls) {
                           this.isAiming = (this.isAimKeyDown() || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= TIME_RIGHT_PRESSED_SECONDS) && this.getPlayerNum() == 0;
                        } else {
                           this.isAiming = (GameKeyboard.isKeyDown(Core.getInstance().getKey("Aim")) || this.TimeLeftPressed >= 16) && this.getPlayerNum() == 0;
                        }

                        if (this.isAiming) {
                           this.StopAllActionQueueAiming();
                        }

                        if (this.isCharging || this.isChargingLT) {
                           this.chargeTime += 1.0F * GameTime.instance.getMultiplier();
                        }

                        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
                           if (this.bJoypadMovementActive) {
                              if (!var35) {
                                 var35 = this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind);
                              } else {
                                 var35 = this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind);
                              }

                              if (this.bJoypadIgnoreAimUntilCentered) {
                                 var7 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
                                 var8 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
                                 if (var7 == 0.0F && var8 == 0.0F) {
                                    this.bJoypadIgnoreAimUntilCentered = false;
                                 }
                              }
                           }

                           if (this.isChargingLT && !JoypadManager.instance.isLTPressed(this.JoypadBind)) {
                              var34 = true;
                              var35 = false;
                           }
                        } else {
                           if (this.bNewControls) {
                              if (!var35) {
                                 var35 = this.isCharging && Mouse.isButtonDownUICheck(0);
                              } else {
                                 var35 = this.isCharging && Mouse.isButtonDown(0);
                              }
                           } else if (!var35) {
                              var35 = this.isCharging && !Mouse.isButtonDownUICheck(0);
                           } else {
                              var35 = this.isCharging && !Mouse.isButtonDown(0);
                           }

                           if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
                              var34 = true;
                              var35 = false;
                           }
                        }

                        boolean var36;
                        if (this.isCharging) {
                           var36 = false;
                        }

                        if (var35) {
                           this.TimeLeftPressed = 0;
                           this.isAiming = true;
                        } else {
                           var36 = false;
                        }

                        if (!this.isCharging && !this.isChargingLT) {
                           this.chargeTime = 0.0F;
                        }

                        if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
                           if (this.bJoypadMovementActive) {
                              this.isCharging = JoypadManager.instance.isRTPressed(this.JoypadBind);
                              this.isChargingLT = JoypadManager.instance.isLTPressed(this.JoypadBind);
                           }
                        } else {
                           if (Mouse.isButtonDown(1)) {
                              this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
                           } else {
                              this.TimeRightPressed = 0.0F;
                           }

                           if (this.bNewControls) {
                              if (!this.isCharging) {
                                 this.isCharging = Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= TIME_RIGHT_PRESSED_SECONDS || this.isAimKeyDown();
                              } else {
                                 this.isCharging = Mouse.isButtonDown(1) || this.isAimKeyDown();
                              }
                           } else if (!this.isCharging) {
                              this.isCharging = Mouse.isButtonDownUICheck(0) && (this.TimeLeftPressed >= 13 || GameKeyboard.isKeyDown(Core.getInstance().getKey("Aim")));
                           } else {
                              this.isCharging = Mouse.isButtonDown(0) && (this.TimeLeftPressed >= 13 || GameKeyboard.isKeyDown(Core.getInstance().getKey("Aim")));
                           }

                           this.bRunning = !PZConsole.instance.isVisible() && GameKeyboard.isKeyDown(Core.getInstance().getKey("Run")) && !this.bSneaking;
                           if (this.isForceRun()) {
                              this.bRunning = true;
                           }
                        }

                        this.Waiting = false;
                        int var37 = 0;
                        if (!this.bNewControls) {
                           if (Mouse.isButtonDownUICheck(2)) {
                              ++this.TimeLeftPressed;
                           } else {
                              var37 = this.TimeLeftPressed;
                              this.TimeLeftPressed = 0;
                           }
                        }

                        this.bWasRunning = this.bRightClickMove;
                        if (this.bNewControls) {
                           this.bRightClickMove = false;
                        } else {
                           this.bRightClickMove = Mouse.isRightDown() && this.stateMachine.getCurrent() != SwipeStatePlayer.instance();
                        }

                        if (this.bRightClickMove) {
                           this.isAiming = false;
                        }

                        if (this.bRightClickMove) {
                           this.isCharging = false;
                           this.chargeTime = 0.0F;
                           if (this.TimeLeftPressed == 0 && var37 != 0) {
                              this.isCharging = true;
                              this.isAiming = true;
                              var35 = true;
                              this.chargeTime = 0.5F;
                              this.bRightClickMove = false;
                              this.bWasRunning = false;
                              this.JustMoved = false;
                              this.setBeenMovingFor(this.getBeenMovingFor() - this.BeenMovingForDecrease * GameTime.getInstance().getMultiplier());
                           }

                           if (this.TimeLeftPressed >= 8) {
                              this.TimeLeftPressed = 8;
                           }
                        } else if (this.bWasRunning) {
                        }

                        if (this.bRightClickMove) {
                           this.bDoubleClick = true;
                        }

                        if (this.bRightClickMove && !this.bWasRunning && this.TimeSinceRightClick < 30) {
                           this.TimeRightClicking = 0;
                           if (this.LastTimeRightClicking < 30) {
                              this.bDoubleClick = true;
                           }
                        }

                        if (this.bRightClickMove) {
                        }

                        if (!this.bRightClickMove) {
                           if (this.bWasRunning && this.TimeRightClicking > 0) {
                              this.LastTimeRightClicking = this.TimeRightClicking;
                           }

                           this.TimeRightClicking = 0;
                           this.TimeSinceRightClick = 0;
                           this.bDoubleClick = false;
                        } else {
                           this.TimeSinceRightClick = 0;
                        }

                        if (this.bDoubleClick) {
                           this.bRunning = true;
                        }

                        if (this.bRunning) {
                           this.bDoubleClick = true;
                        }

                        ++this.TimeSinceRightClick;
                        this.bSneaking = this.isAiming;
                        if (this.bSneaking) {
                           this.bRunning = false;
                        }

                        ++this.TicksSinceSeenZombie;
                     }

                     super.update();
                     if ((double)this.playerMoveDir.x == 0.0D && (double)this.playerMoveDir.y == 0.0D) {
                        this.setForceRun(false);
                     }

                     this.movementLastFrame.x = this.playerMoveDir.x;
                     this.movementLastFrame.y = this.playerMoveDir.y;
                     if (this.sprite != null) {
                        if (this.sprite.CurrentAnim == null) {
                           return;
                        }

                        if (this.sprite.CurrentAnim.name == null) {
                           return;
                        }

                        if (this.sprite.CurrentAnim.name.equals("Die")) {
                           return;
                        }

                        if (this.sprite.CurrentAnim.name.equals("ZombieDeath")) {
                           return;
                        }
                     }

                     if (!GameServer.bServer) {
                     }

                     if (this.stateMachine.getCurrent() != StaggerBackState.instance() && this.stateMachine.getCurrent() != StaggerBackDieState.instance() && this.stateMachine.getCurrent() != DieState.instance() && this.stateMachine.getCurrent() != FakeDeadZombieState.instance() && this.stateMachine.getCurrent() != ReanimateState.instance() && UIManager.speedControls != null) {
                        this.JustMoved = false;
                        this.setBeenMovingFor(this.getBeenMovingFor() - this.BeenMovingForDecrease * GameTime.getInstance().getMultiplier());
                        var7 = 0.0F;
                        var8 = 0.0F;
                        float var9 = var7;
                        float var10 = var8;
                        IsoDirections var11 = this.dir;
                        float var12;
                        float var13;
                        float var14;
                        float var16;
                        Vector2 var10000;
                        Vector2 var17;
                        Vector2 var18;
                        if (!GameServer.bServer && !this.isBlockMovement() && !TutorialManager.instance.StealControl) {
                           if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
                              this.playerMoveDir.x = 0.0F;
                              this.playerMoveDir.y = 0.0F;
                              if (this.bJoypadMovementActive) {
                                 if (JoypadManager.instance.isRTPressed(this.JoypadBind)) {
                                    this.isCharging = true;
                                 }

                                 if (!JoypadManager.instance.isAPressed(this.JoypadBind)) {
                                    this.DebounceA = false;
                                 }
                              }

                              var12 = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
                              var13 = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
                              var14 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
                              float var15 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
                              var16 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
                              if (tempVector2.set(var15, var14).getLength() < var16) {
                                 var14 = 0.0F;
                                 var15 = 0.0F;
                              }

                              var17 = tempVector2.set(var13, var12);
                              if (var17.getLength() > 1.0F) {
                                 var17.setLength(1.0F);
                                 var13 = var17.x;
                                 var12 = var17.y;
                              }

                              if (Math.abs(var13) > var16) {
                                 var10000 = this.playerMoveDir;
                                 var10000.x += 0.04F * var13;
                                 var10000 = this.playerMoveDir;
                                 var10000.y -= 0.04F * var13;
                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (Math.abs(var12) > var16) {
                                 var10000 = this.playerMoveDir;
                                 var10000.y += 0.04F * var12;
                                 var10000 = this.playerMoveDir;
                                 var10000.x += 0.04F * var12;
                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (JoypadManager.instance.isL3Pressed(this.JoypadBind)) {
                                 if (!this.L3Pressed) {
                                    this.ControllerRun = !this.ControllerRun;
                                    this.L3Pressed = true;
                                 }
                              } else {
                                 this.L3Pressed = false;
                              }

                              if (this.ControllerRun) {
                                 if (var17.getLength() > 0.95F) {
                                    this.bRunning = true;
                                    this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                                 } else if (var17.getLength() < 0.5F) {
                                    this.bSneaking = true;
                                 }
                              } else if (var17.getLength() < 0.5F) {
                                 this.bSneaking = true;
                              }

                              this.playerMoveDir.setLength(0.05F * var17.getLength() * var17.getLength() * var17.getLength() * var17.getLength() * var17.getLength() * var17.getLength() * var17.getLength() * var17.getLength() * var17.getLength());
                              IsoDirections var19;
                              if (var15 == 0.0F && var14 == 0.0F) {
                                 if (var13 != 0.0F || var12 != 0.0F) {
                                    var18 = tempVector2.set(this.playerMoveDir.x, this.playerMoveDir.y);
                                    if (var18.getLength() > 0.0F) {
                                       var18.normalize();
                                       var19 = this.dir;
                                       this.DirectionFromVector(var18);
                                       var11 = this.dir;
                                       this.dir = var19;
                                    }
                                 }
                              } else {
                                 var18 = tempVector2.set(var15, var14);
                                 var19 = this.dir;
                                 var18.normalize();
                                 this.DirectionFromVector(var18);
                                 var11 = this.dir;
                                 this.bSneaking = true;
                                 this.isAiming = true;
                                 this.dir = var19;
                                 this.bRunning = false;
                              }
                           }

                           if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                              this.dirtyRecalcGridStackTime = 10.0F;
                           }

                           this.lastAngle.x = this.angle.x;
                           this.lastAngle.y = this.angle.y;
                           if (!this.isBlockMovement() && this.PlayerIndex == 0 && !this.Speaking && GameKeyboard.isKeyDown(Core.getInstance().getKey("Shout"))) {
                              this.Callout();
                           }

                           if (GameKeyboard.isKeyDown(88) && Translator.debug) {
                              Translator.loadFiles();
                           }

                           if (this.PlayerIndex == 0 && Core.bAltMoveMethod && this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                              if (!this.isBlockMovement() && (GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)))) {
                                 var7 = 0.0F;
                                 var8 = 0.0F;
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr))) {
                                 var7 -= 0.04F;
                                 var11 = IsoDirections.W;
                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr))) {
                                 var7 += 0.04F;
                                 var11 = IsoDirections.E;
                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr))) {
                                 var8 -= 0.04F;
                                 if (var11 == IsoDirections.W) {
                                    var11 = IsoDirections.NW;
                                 } else if (var11 == IsoDirections.E) {
                                    var11 = IsoDirections.NE;
                                 } else {
                                    var11 = IsoDirections.N;
                                 }

                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr))) {
                                 var8 += 0.04F;
                                 if (var11 == IsoDirections.W) {
                                    var11 = IsoDirections.SW;
                                 } else if (var11 == IsoDirections.E) {
                                    var11 = IsoDirections.SE;
                                 } else {
                                    var11 = IsoDirections.S;
                                 }

                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }
                           } else if (this.PlayerIndex == 0 && this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                              if (!this.isBlockMovement() && (GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)))) {
                                 var7 = 0.0F;
                                 var8 = 0.0F;
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr))) {
                                 var7 -= 0.04F;
                                 var8 += 0.04F;
                                 var11 = IsoDirections.SW;
                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr))) {
                                 var7 += 0.04F;
                                 var8 -= 0.04F;
                                 var11 = IsoDirections.NE;
                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr))) {
                                 UIManager.setDoMouseControls(true);
                                 var8 -= 0.04F;
                                 var7 -= 0.04F;
                                 if (var11 == IsoDirections.SW) {
                                    var11 = IsoDirections.W;
                                 } else if (var11 == IsoDirections.NE) {
                                    var11 = IsoDirections.N;
                                 } else {
                                    var11 = IsoDirections.NW;
                                 }

                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }

                              if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr))) {
                                 var8 += 0.04F;
                                 var7 += 0.04F;
                                 if (var11 == IsoDirections.SW) {
                                    var11 = IsoDirections.S;
                                 } else if (var11 == IsoDirections.NE) {
                                    var11 = IsoDirections.E;
                                 } else {
                                    var11 = IsoDirections.SE;
                                 }

                                 if (this.stateMachine.getCurrent() == PathFindState.instance()) {
                                    this.stateMachine.setCurrent(this.defaultState);
                                 }

                                 this.JustMoved = true;
                                 this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
                              }
                           }

                           var9 = var7;
                           var10 = var8;
                           if (!this.isBlockMovement() && this.JustMoved) {
                              if (!this.isAiming) {
                                 if (this.JoypadBind != -1) {
                                    var9 = this.playerMoveDir.x;
                                    var10 = this.playerMoveDir.y;
                                    this.angle.x = var9;
                                    this.angle.y = var10;
                                    if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                       this.dirtyRecalcGridStackTime = 2.0F;
                                    }
                                 } else {
                                    this.angle.x = var7;
                                    this.angle.y = var8;
                                    var9 = this.angle.x;
                                    var10 = this.angle.y;
                                    if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                       this.dirtyRecalcGridStackTime = 2.0F;
                                    }
                                 }
                              }

                              this.bRightClickMove = true;
                              this.angle.normalize();
                              UIManager.speedControls.SetCurrentGameSpeed(1);
                           } else {
                              if (var7 != 0.0F || var8 != 0.0F) {
                                 this.angle.x = var7;
                                 this.angle.y = var8;
                                 if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                    this.dirtyRecalcGridStackTime = 2.0F;
                                 }
                              }

                              var9 = var7;
                              var10 = var8;
                           }

                           if (this.bRightClickMove) {
                              if (UIManager.getSpeedControls().getCurrentGameSpeed() > 1) {
                                 UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                              }
                           } else if (this.stats.endurance < this.stats.endurancedanger && Rand.Next((int)(300.0F * GameTime.instance.getInvMultiplier())) == 0) {
                              this.xp.AddXP(PerkFactory.Perks.Fitness, 1.0F);
                           }
                        }

                        if (!GameServer.bServer) {
                           var12 = this.getMoveSpeed();
                           var13 = 1.0F;
                           var14 = 0.0F;
                           if (this.bRightClickMove) {
                              if (this.JustMoved) {
                                 if (!this.bRunning) {
                                    var14 = 1.0F;
                                 } else {
                                    var14 = 1.5F;
                                 }
                              } else if (!this.isAiming) {
                                 var14 = this.runAngle.getLength() / 4.0F;
                              }

                              if (var14 > 1.5F) {
                                 var14 = 1.5F;
                              }
                           }

                           var13 *= var14;
                           if (this.runAngle.getLength() == 0.0F && !this.JustMoved) {
                              var13 = 0.0F;
                           }

                           if (var13 > 1.0F) {
                              var13 *= this.getSprintMod();
                           }

                           var16 = this.CurrentSpeed / 0.06F * this.getGlobalMovementMod(false);
                           if (var13 > 1.0F && this.HasTrait("Athletic")) {
                              var13 *= 1.2F;
                           }

                           if (var13 > 1.0F) {
                              if (this.HasTrait("Overweight")) {
                                 var13 *= 0.99F;
                              }

                              if (this.HasTrait("Obese")) {
                                 var13 *= 0.85F;
                                 if (this.getNutrition().getWeight() > 120.0F) {
                                    var13 *= 0.97F;
                                 }
                              }

                              if (this.HasTrait("Out of Shape")) {
                                 var13 *= 0.99F;
                              }

                              if (this.HasTrait("Unfit")) {
                                 var13 *= 0.8F;
                              }
                           }

                           float var38 = this.CurrentSpeed / 0.06F;
                           float var20;
                           float var39;
                           float var41;
                           Stats var52;
                           if (!(var16 > 1.0F) && !(var13 > 1.0F)) {
                              if (this.CurrentSpeed > 0.0F && this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 0) {
                                 var39 = 0.7F;
                                 if (this.HasTrait("Asthmatic")) {
                                    var39 = 1.4F;
                                 }

                                 var41 = 1.4F;
                                 if (this.HasTrait("Overweight")) {
                                    var41 = 2.9F;
                                 }

                                 if (this.HasTrait("Athletic")) {
                                    var41 = 0.8F;
                                 }

                                 var41 *= 3.0F;
                                 var41 *= this.getPacingMod();
                                 var41 *= this.getHyperthermiaMod();
                                 var20 = 2.8F;
                                 switch(this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
                                 case 1:
                                    var20 = 1.5F;
                                    break;
                                 case 2:
                                    var20 = 1.9F;
                                    break;
                                 case 3:
                                    var20 = 2.3F;
                                 }

                                 var52 = this.stats;
                                 var52.endurance = (float)((double)var52.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)var41 * (double)(var38 * 0.5F) * (double)var39 * (double)GameTime.instance.getMultiplier() * (double)var20 * (double)this.stats.endurance / 2.0D);
                              }
                           } else {
                              if (var13 < 1.0F) {
                                 var38 *= 0.3F;
                              }

                              var39 = 1.4F;
                              if (this.HasTrait("Overweight")) {
                                 var39 = 2.9F;
                              }

                              if (this.HasTrait("Athletic")) {
                                 var39 = 0.8F;
                              }

                              var39 *= 3.0F;
                              var39 *= this.getPacingMod();
                              var39 *= this.getHyperthermiaMod();
                              var41 = 0.7F;
                              if (this.HasTrait("Asthmatic")) {
                                 var41 = 1.4F;
                              }

                              if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
                                 var52 = this.stats;
                                 var52.endurance = (float)((double)var52.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)var39 * (double)(var38 * 0.5F) * (double)var41 * (double)GameTime.instance.getMultiplier() * (double)this.stats.endurance);
                              } else {
                                 var20 = 2.8F;
                                 switch(this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
                                 case 1:
                                    var20 = 1.5F;
                                    break;
                                 case 2:
                                    var20 = 1.9F;
                                    break;
                                 case 3:
                                    var20 = 2.3F;
                                 }

                                 var52 = this.stats;
                                 var52.endurance = (float)((double)var52.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)var39 * (double)(var38 * 0.5F) * (double)var41 * (double)GameTime.instance.getMultiplier() * (double)var20 * (double)this.stats.endurance);
                              }
                           }

                           if (TutorialManager.instance.ActiveControlZombies && !IsoWorld.instance.CurrentCell.IsZone("tutArea", (int)this.x, (int)this.y)) {
                              TutorialManager.instance.ActiveControlZombies = false;
                           }

                           if (this.bSneaking && this.JustMoved) {
                              var13 *= 0.7F;
                           }

                           if (this.bSneaking) {
                              var13 *= this.getNimbleMod();
                           }

                           if (var13 > 0.0F) {
                              if (var13 < 0.7F) {
                                 this.bSneaking = true;
                              }

                              if (var13 > 1.2F) {
                                 this.bRunning = true;
                              }

                              LuaEventManager.triggerEvent("OnPlayerMove");
                              if (!PerkFactory.newMode) {
                                 if (this.bSneaking && Rand.Next((int)(80.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                    this.xp.AddXP(PerkFactory.Perks.Nimble, 1.0F);
                                 }

                                 if (this.getInventoryWeight() > (float)this.maxWeight * 0.5F && Rand.Next((int)(80.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                    this.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
                                 }

                                 if (this.bRunning && this.stats.endurance > this.stats.endurancewarn) {
                                    if (this.getInventoryWeight() > (float)this.maxWeight * 0.5F && Rand.Next((int)(80.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                       this.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
                                    }

                                    if (Rand.Next((int)(80.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                       this.xp.AddXP(PerkFactory.Perks.Fitness, 1.0F);
                                    }

                                    if (Rand.Next((int)(80.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                       this.xp.AddXP(PerkFactory.Perks.Sprinting, 1.0F);
                                    }
                                 }
                              }
                           }

                           if (this.JustMoved || this.bRightClickMove) {
                              this.sprite.Animate = true;
                           }

                           if (var34) {
                              if (!this.bBannedAttacking && this.CanAttack()) {
                                 this.sprite.Animate = true;
                                 this.DoAttack(this.useChargeTime, true, (String)null);
                                 this.useChargeTime = 0.0F;
                                 this.chargeTime = 0.0F;
                                 if (!this.isCharging) {
                                    this.isAiming = false;
                                 }
                              }
                           } else if ((this.bRightClickMove && !this.JustMoved || this.isAiming) && this.CanAttack()) {
                              if (this.DragCharacter != null) {
                                 this.DragObject = null;
                                 this.DragCharacter.Dragging = false;
                                 this.DragCharacter = null;
                              }

                              var17 = null;
                              HandWeapon var40;
                              if (this.leftHandItem instanceof HandWeapon) {
                                 var40 = (HandWeapon)this.leftHandItem;
                              } else {
                                 var40 = this.bareHands;
                              }

                              if (!this.isForceShove() && var40 != null && this.AttackDelay <= 0.0F && this.isAiming && !this.JustMoved && this.DoAimAnimOnAiming()) {
                                 this.PlayShootAnim();
                              }

                              if (var35 && !this.bBannedAttacking) {
                                 this.sprite.Animate = true;
                                 var18 = tempVector2.set(instance.getX(), instance.getY());
                                 int var44 = Mouse.getX();
                                 int var45 = Mouse.getY();
                                 var18.x -= IsoUtils.XToIso((float)var44, (float)var45 + 55.0F * this.def.getScaleY(), this.getZ());
                                 var18.y -= IsoUtils.YToIso((float)var44, (float)var45 + 55.0F * this.def.getScaleY(), this.getZ());
                                 var18.x = -var18.x;
                                 var18.y = -var18.y;
                                 var18.normalize();
                                 if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
                                    var18 = this.getControllerAimDir(var18);
                                 }

                                 if (var18.getLength() > 0.0F) {
                                    this.DirectionFromVector(var18);
                                    this.angle.x = var18.x;
                                    this.angle.y = var18.y;
                                    if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                       this.dirtyRecalcGridStackTime = 2.0F;
                                    }
                                 }

                                 this.AttemptAttack(this.useChargeTime);
                                 this.useChargeTime = 0.0F;
                                 this.chargeTime = 0.0F;
                                 this.bDebounceLMB = true;
                              } else {
                                 this.bDebounceLMB = false;
                              }

                              int var43 = Core.getInstance().getKey("Aim");
                              boolean var46 = GameKeyboard.isKeyDown(var43);
                              boolean var47 = var43 == 29 || var43 == 157;
                              if (this.PlayerIndex == 0 && var47 && var46 && UIManager.isMouseOverInventory() || this.JustMoved && !this.isAiming || this.JoypadBind != -1 && !this.bJoypadMovementActive) {
                                 if (this.angle.getLength() > 0.0F) {
                                    this.DirectionFromVector(this.angle);
                                 }
                              } else {
                                 Vector2 var21 = tempVector2.set(this.getX(), this.getY());
                                 int var22 = Mouse.getX();
                                 int var23 = Mouse.getY();
                                 var21.x -= IsoUtils.XToIso((float)var22, (float)var23 + 55.0F * this.def.getScaleY(), this.getZ());
                                 var21.y -= IsoUtils.YToIso((float)var22, (float)var23 + 55.0F * this.def.getScaleY(), this.getZ());
                                 this.runAngle.x = var21.x;
                                 this.runAngle.y = var21.y;
                                 if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                    this.dirtyRecalcGridStackTime = 2.0F;
                                 }

                                 if (this.runAngle.getLength() < 0.3F) {
                                    this.runAngle.setLength(0.0F);
                                 } else {
                                    this.runAngle.setLength(this.runAngle.getLength() - 0.3F);
                                 }

                                 var21.x = -var21.x;
                                 var21.y = -var21.y;
                                 var21.normalize();
                                 if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                    this.dirtyRecalcGridStackTime = 2.0F;
                                 }

                                 this.lastAngle.x = this.angle.x;
                                 this.lastAngle.y = this.angle.y;
                                 ++this.angleCounter;
                                 if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
                                    var21 = this.getControllerAimDir(var21);
                                 } else {
                                    this.angle.x = var21.x;
                                    this.angle.y = var21.y;
                                    if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                       this.dirtyRecalcGridStackTime = 2.0F;
                                    }

                                    this.angleCounter = 0;
                                 }

                                 if (var21.getLength() > 0.0F) {
                                    this.DirectionFromVector(var21);
                                    this.angle.x = var21.x;
                                    this.angle.y = var21.y;
                                    if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                       this.dirtyRecalcGridStackTime = 2.0F;
                                    }
                                 }
                              }

                              var11 = this.dir;
                           }

                           if (this.angle.x == 0.0F && this.angle.y == 0.0F) {
                              this.angle.x = this.dir.ToVector().x;
                              this.angle.y = this.dir.ToVector().y;
                           }

                           if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                              this.dirtyRecalcGridStackTime = 2.0F;
                           }

                           if (this.DragCharacter != null) {
                              var13 = 0.4F;
                           }

                           if (this.stats.endurance < 0.0F) {
                              this.stats.endurance = 0.0F;
                           }

                           if (this.stats.endurance > 1.0F) {
                              this.stats.endurance = 1.0F;
                           }

                           switch(this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
                           case 1:
                              var13 *= 0.95F;
                              break;
                           case 2:
                              var13 *= 0.9F;
                              break;
                           case 3:
                              var13 *= 0.8F;
                              break;
                           case 4:
                              var13 *= 0.6F;
                           }

                           if (this.stats.enduranceRecharging) {
                              var13 *= 0.85F;
                           }

                           if (var13 < 0.6F) {
                              var38 = 1.0F;
                              var38 *= 1.0F - this.stats.fatigue;
                              var38 *= GameTime.instance.getMultiplier();
                              if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
                                 var52 = this.stats;
                                 var52.endurance = (float)((double)var52.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)var38);
                              }
                           }

                           if (var13 <= 1.0F && var13 > 0.6F) {
                              var38 = 1.0F;
                              var38 *= 1.0F - this.stats.fatigue;
                              var38 *= GameTime.instance.getMultiplier();
                              if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
                                 var52 = this.stats;
                                 var52.endurance = (float)((double)var52.endurance + ZomboidGlobals.ImobileEnduranceReduce / 3.0D * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)var38);
                              }
                           }

                           if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 0) {
                              var38 = this.getInventory().getCapacityWeight();
                              var39 = (float)this.getMaxWeight();
                              var41 = Math.min(2.7F, var38 / var39) - 1.0F;
                              var13 *= 0.65F + 0.35F * (1.0F - var41);
                           }

                           if (this.playerMoveDir.getLength() > 0.0F) {
                              UIManager.CloseContainers();
                           }

                           int var42 = Math.abs(this.dir.index() - var11.index());
                           if (var42 > 4) {
                              var42 = 4 - (var42 - 4);
                           }

                           if (var42 > 2) {
                           }

                           if (!this.bFalling && !this.isAiming && !var35) {
                              this.dir = var11;
                           }

                           if (this.current != null) {
                              if (this.current.Has(IsoObjectType.stairsBN) || this.current.Has(IsoObjectType.stairsMN) || this.current.Has(IsoObjectType.stairsTN)) {
                                 var9 = 0.0F;
                                 if (!this.JustMoved && this.bRightClickMove) {
                                    this.angle.x = 0.0F;
                                    this.angle.normalize();
                                 }
                              }

                              if (this.current.Has(IsoObjectType.stairsBW) || this.current.Has(IsoObjectType.stairsMW) || this.current.Has(IsoObjectType.stairsTW)) {
                                 var10 = 0.0F;
                                 if (!this.JustMoved && this.bRightClickMove) {
                                    this.angle.y = 0.0F;
                                    this.angle.normalize();
                                 }
                              }
                           }

                           if (this.isAiming && (GameWindow.ActivatedJoyPad == null || this.JoypadBind == -1)) {
                              this.playerMoveDir.x = var9;
                              this.playerMoveDir.y = var10;
                           }

                           if (!this.isAiming && this.bRightClickMove) {
                              var9 = this.angle.x;
                              var10 = this.angle.y;
                              this.playerMoveDir.x = var9;
                              this.playerMoveDir.y = var10;
                           }

                           var38 = 1.0F + (1.0F - (var16 > 1.0F ? 1.0F : var16));
                           Vector2 var49;
                           if (this.bRightClickMove) {
                              var39 = 0.0F;
                              if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 1) {
                                 var39 = 0.1F;
                              }

                              if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 2) {
                                 var39 = 0.3F;
                              }

                              if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 3) {
                                 var39 = 0.5F;
                              }

                              if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 4) {
                                 var39 = 1.0F;
                              }

                              if (!this.bRunning) {
                                 var39 /= 2.0F;
                              }

                              if (Rand.Next(80) == 0) {
                                 this.DrunkCos2 = (float)Rand.Next(-1000, 1000) / 500.0F;
                                 this.DrunkCos2 *= var39;
                              }

                              if (this.DrunkSin < this.DrunkCos2) {
                                 this.DrunkSin += 0.015F;
                                 if (this.DrunkSin > this.DrunkCos2) {
                                    this.DrunkSin = this.DrunkCos2;
                                    this.DrunkCos2 = (float)Rand.Next(-1000, 1000) / 500.0F;
                                    this.DrunkCos2 *= var39;
                                 }
                              }

                              if (this.DrunkSin > this.DrunkCos2) {
                                 this.DrunkSin -= 0.015F;
                                 if (this.DrunkSin < this.DrunkCos2) {
                                    this.DrunkSin = this.DrunkCos2;
                                    this.DrunkCos2 = (float)Rand.Next(-1000, 1000) / 500.0F;
                                    this.DrunkCos2 *= var39;
                                 }
                              }

                              this.playerMoveDir.rotate(this.DrunkSin);
                              if (var39 > 0.0F && (this.playerMoveDir.x != 0.0F || this.playerMoveDir.y != 0.0F)) {
                                 var49 = tempo;
                                 tempo.x = this.playerMoveDir.x;
                                 tempo.y = this.playerMoveDir.y;
                                 var49.normalize();
                                 IsoDirections var48 = this.dir;
                                 this.DirectionFromVector(var49);
                                 var11 = this.dir;
                                 this.dir = var48;
                                 var9 = var49.x;
                                 var10 = var49.y;
                                 if (!this.isAiming) {
                                    this.angle.x = var9;
                                    this.angle.y = var10;
                                    if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
                                       this.dirtyRecalcGridStackTime = 2.0F;
                                    }
                                 }
                              }
                           } else {
                              this.DrunkCos2 = 0.0F;
                              if (this.DrunkSin < this.DrunkCos2) {
                                 this.DrunkSin += 0.015F;
                              }

                              if (this.DrunkSin > this.DrunkCos2) {
                                 this.DrunkSin -= 0.015F;
                              }
                           }

                           boolean var51 = this.getStateMachine().getCurrent() == SwipeStatePlayer.instance();
                           if (!this.CharacterActions.isEmpty()) {
                              BaseAction var50 = (BaseAction)this.CharacterActions.get(0);
                              if (var50.overrideAnimation) {
                                 var51 = true;
                              }
                           }

                           if (!var51 && !this.isForceOverrideAnim()) {
                              if (this.path == null && this.getPath2() == null) {
                                 if (this.CurrentSpeed > 0.0F) {
                                    if (this.bClimbing && !(this.lastFallSpeed > 0.0F)) {
                                       if (!this.sprite.CurrentAnim.name.contains("Attack_")) {
                                          this.def.setFrameSpeedPerFrame(0.1F);
                                          if (!GameClient.bClient) {
                                             this.NetRemoteState = 0;
                                          }

                                          if (this.leftHandItem instanceof HandWeapon) {
                                             this.PlayAnim(((HandWeapon)this.leftHandItem).IdleAnim);
                                          } else {
                                             this.PlayAnim("Idle");
                                          }
                                       }
                                    } else if (var16 > 1.0F) {
                                       this.StopAllActionQueueRunning();
                                       this.AimRadius += 10.0F;
                                       if (this.leftHandItem instanceof HandWeapon) {
                                          this.PlayAnim(((HandWeapon)this.leftHandItem).RunAnim);
                                          if (!GameClient.bClient) {
                                             this.NetRemoteState = 2;
                                          }
                                       } else {
                                          this.PlayAnim("Run");
                                          if (!GameClient.bClient) {
                                             this.NetRemoteState = 2;
                                          }

                                          this.def.setFrameSpeedPerFrame(0.25F * var16);
                                       }
                                    } else {
                                       if (this.isSat()) {
                                          this.StateMachineParams.clear();
                                          this.StateMachineParams.put(0, this.getChair());
                                          this.getStateMachine().changeState(SatChairStateOut.instance());
                                          return;
                                       }

                                       this.AimRadius += 0.8F;
                                       this.StopAllActionQueueWalking();
                                       this.def.setFrameSpeedPerFrame(0.3F);
                                       if (!this.isAiming) {
                                          this.PlayAnim("Walk");
                                          if (!GameClient.bClient) {
                                             this.NetRemoteState = 1;
                                          }

                                          this.def.setFrameSpeedPerFrame(0.25F * var16);
                                       } else {
                                          tempo.x = this.playerMoveDir.x;
                                          tempo.y = this.playerMoveDir.y;
                                          tempo.normalize();
                                          var41 = tempo.dot(this.angle);
                                          if ((double)var41 > 0.8D) {
                                             this.def.setFrameSpeedPerFrame(0.2F * (this.playerMoveDir.getLength() / 0.06F));
                                             this.PlayAnim(this.walkAnim);
                                          } else if ((double)var41 >= -0.8D && (double)var41 <= 0.8D) {
                                             tempo.rotate((float)Math.toRadians(90.0D));
                                             var41 = tempo.dot(this.angle);
                                             if (var41 < 0.0F) {
                                                this.PlayAnim(this.strafeAnim);
                                             } else {
                                                this.PlayAnim(this.strafeRAnim);
                                             }

                                             this.playerMoveDir.setLength(this.CurrentSpeed * 0.8F);
                                             this.def.setFrameSpeedPerFrame(0.35F * (this.CurrentSpeed / 0.06F));
                                          } else if (var41 < -0.8F) {
                                             this.def.setFrameSpeedPerFrame(0.2F * (this.CurrentSpeed / 0.06F));
                                             this.PlayAnim(this.walkRAnim);
                                             this.playerMoveDir.setLength(this.CurrentSpeed * 0.5F);
                                          }
                                       }
                                    }
                                 } else if (!this.sprite.CurrentAnim.name.contains("Attack_")) {
                                    this.def.setFrameSpeedPerFrame(0.1F);
                                    if (this.leftHandItem instanceof HandWeapon) {
                                       if (this.isAiming) {
                                          this.PlayAnim("Attack_" + ((HandWeapon)this.leftHandItem).getSwingAnim());
                                          this.def.Finished = true;
                                          this.def.Frame = 0.0F;
                                       } else {
                                          this.PlayAnim(((HandWeapon)this.leftHandItem).IdleAnim);
                                       }

                                       if (!GameClient.bClient) {
                                          this.NetRemoteState = 0;
                                       }
                                    } else if (this.isSat()) {
                                       this.PlayAnim("SatChairIdle");
                                    } else {
                                       if (!GameClient.bClient) {
                                          this.NetRemoteState = 0;
                                       }

                                       this.PlayAnim("Idle");
                                    }
                                 }
                              } else {
                                 this.AimRadius += 0.8F;
                                 this.StopAllActionQueueWalking();
                                 this.PlayAnim("Walk");
                                 var41 = this.getPathSpeed() / 0.06F * this.getGlobalMovementMod(false);
                                 this.def.setFrameSpeedPerFrame(0.25F * var41);
                              }
                           }

                           if (var13 > 1.3F) {
                              var41 = 1.0F;
                              var20 = 180.0F;
                              if ((float)this.TimeSprinting >= var20) {
                                 var41 = 1.0F - ((float)this.TimeSprinting - var20) / 360.0F;
                              } else {
                                 var41 = 1.0F - (var20 - (float)this.TimeSprinting) / 360.0F;
                              }

                              var41 *= 0.1F;
                              ++var41;
                              if (var41 < 0.0F) {
                                 var41 = 0.0F;
                              }

                              ++this.TimeSprinting;
                              this.TargetSpeed = var12 * var41 * var13 * 1.1F;
                           } else {
                              this.TargetSpeed = var12 * var13 * 0.9F;
                              if (this.CurrentSpeed < 0.08F) {
                                 this.TimeSprinting = 0;
                              }
                           }

                           var41 = this.SpeedChange;
                           if (this.CurrentSpeed < 0.06F) {
                              var41 *= 5.0F;
                           }

                           if (this.slowTimer > 0.0F) {
                              this.TargetSpeed *= 1.0F - this.slowFactor;
                           }

                           if (this.CurrentSpeed < this.TargetSpeed) {
                              this.CurrentSpeed += var41 / 3.0F;
                              if (this.CurrentSpeed > this.TargetSpeed) {
                                 this.CurrentSpeed = this.TargetSpeed;
                              }
                           } else if (this.CurrentSpeed > this.TargetSpeed) {
                              if (this.CurrentSpeed < 0.03F) {
                                 this.CurrentSpeed = this.TargetSpeed;
                              } else {
                                 this.CurrentSpeed -= var41;
                                 if (this.CurrentSpeed < this.TargetSpeed) {
                                    this.CurrentSpeed = this.TargetSpeed;
                                 }
                              }
                           }

                           if (this.slowTimer > 0.0F) {
                              this.slowTimer -= GameTime.instance.getRealworldSecondsSinceLastUpdate();
                              this.CurrentSpeed *= 1.0F - this.slowFactor;
                              this.slowFactor -= GameTime.instance.getMultiplier() / 100.0F;
                              if (this.slowFactor < 0.0F) {
                                 this.slowFactor = 0.0F;
                              }
                           } else {
                              this.slowFactor = 0.0F;
                           }

                           var12 = this.CurrentSpeed;
                           this.playerMoveDir.setLength(var12);
                           if (this.playerMoveDir.x != 0.0F || this.playerMoveDir.y != 0.0F) {
                              this.dirtyRecalcGridStackTime = 10.0F;
                              ScriptManager.instance.Trigger("OnPlayerMoved");
                           }

                           if (this.getPath2() != null && this.current != this.last) {
                              this.dirtyRecalcGridStackTime = 10.0F;
                           }

                           if (!this.GhostMode) {
                              if (this.bSneaking) {
                                 this.DoFootstepSound(this.CurrentSpeed * (2.0F - this.getNimbleMod()));
                              } else {
                                 this.DoFootstepSound(this.CurrentSpeed);
                              }
                           }

                           if (this.DragObject != null) {
                              var49 = new Vector2(instance.getX(), instance.getY());
                              var49.x -= this.DragObject.getX();
                              var49.y -= this.DragObject.getY();
                              var49.x = -var49.x;
                              var49.y = -var49.y;
                              var49.normalize();
                              this.DirectionFromVectorNoDiags(var49);
                              if ((this.dir == IsoDirections.W || this.dir == IsoDirections.S || this.dir == IsoDirections.N || this.dir == IsoDirections.E) && this.DragObject instanceof IsoWheelieBin) {
                                 this.DragObject.dir = this.dir;
                              }
                           }

                           if (this.DragObject != null && this.DragObject instanceof IsoWheelieBin) {
                              this.DragObject.dir = this.dir;
                           }

                           if (this.DragObject != null) {
                              var41 = this.DragObject.getWeight(this.playerMoveDir.x, this.playerMoveDir.y) + this.getWeight(this.playerMoveDir.x, this.playerMoveDir.y);
                              var20 = this.getWeight(this.playerMoveDir.x, this.playerMoveDir.y) / var41;
                              var10000 = this.playerMoveDir;
                              var10000.x *= var20;
                              var10000 = this.playerMoveDir;
                              var10000.y *= var20;
                           }

                           if (this.DragObject != null && this.playerMoveDir.getLength() != 0.0F) {
                              this.DragObject.setImpulsex(this.DragObject.getImpulsex() + this.playerMoveDir.x);
                              this.DragObject.setImpulsey(this.DragObject.getImpulsey() + this.playerMoveDir.y);
                           }

                           if (this.GhostMode) {
                              var10000 = this.playerMoveDir;
                              var10000.x = (float)((double)var10000.x * (4.0D + (this.bRunning ? 4.0D : 0.5D)));
                              var10000 = this.playerMoveDir;
                              var10000.y = (float)((double)var10000.y * (4.0D + (this.bRunning ? 4.0D : 0.5D)));
                           }

                           if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                              this.Move(this.playerMoveDir);
                           }

                           if (GameClient.bClient && networkUpdate.Check()) {
                              GameClient.instance.sendPlayer(this);
                           }

                           if (this.DragCharacter != null) {
                              var49 = new Vector2(instance.getX(), instance.getY());
                              var49.x -= this.DragCharacter.getX();
                              var49.y -= this.DragCharacter.getY();
                              var49.x = -var49.x;
                              var49.y = -var49.y;
                              var49.normalize();
                              this.DirectionFromVector(var49);
                              if (this.dir == IsoDirections.W || this.dir == IsoDirections.S || this.dir == IsoDirections.N || this.dir == IsoDirections.E) {
                                 this.DragCharacter.dir = this.dir;
                              }
                           }

                           this.closestZombie = 1000000.0F;
                           this.updateLOS();
                        }

                        this.weight = 0.3F;
                        this.seperate();
                        if (this.getCurrentSquare() != null && this.getCurrentSquare().getRoom() != null) {
                        }

                        this.updateSleepingPillsTaken();
                        this.updateTorchStrength();
                     } else {
                        if (GameClient.bClient && networkUpdate2.Check()) {
                           GameClient.instance.sendPlayer(this);
                        }

                     }
                  }
               } else {
                  this.updateLOS();
                  super.update();
                  if (GameClient.bClient && networkUpdate.Check()) {
                     GameClient.instance.sendPlayer(this);
                  }

               }
            }
         } else {
            if (GameServer.bServer) {
               ServerLOS.instance.doServerZombieLOS(this);
               ServerLOS.instance.updateLOS(this);
               if (this.Health <= 0.0F || this.BodyDamage.getHealth() <= 0.0F) {
                  super.update();
                  return;
               }

               tempo.x = this.x - this.lx;
               tempo.y = this.y - this.ly;
               if (this.bSneaking) {
                  this.DoFootstepSound(this.CurrentSpeed * (2.0F - this.getNimbleMod()));
               } else {
                  this.DoFootstepSound(this.CurrentSpeed);
               }

               if (this.slowTimer > 0.0F) {
                  this.slowTimer -= GameTime.instance.getRealworldSecondsSinceLastUpdate();
                  this.slowFactor -= GameTime.instance.getMultiplier() / 100.0F;
                  if (this.slowFactor < 0.0F) {
                     this.slowFactor = 0.0F;
                  }
               } else {
                  this.slowFactor = 0.0F;
               }
            }

            this.setBlendSpeed(0.08F);
            if (this.remoteMoveX != 0.0F || this.remoteMoveY != 0.0F) {
               this.playerMoveDir.x = this.remoteMoveX;
               this.playerMoveDir.y = this.remoteMoveY;
               tempo.x = this.remoteMoveX;
               tempo.y = this.remoteMoveY;
               this.Move(tempo);
               this.setBlendSpeed(tempo.getLength() * 2.0F * 0.9F);
            }

            if (GameClient.bClient) {
               this.netHistory.interpolate(this);
            }

            if (!GameServer.bServer && this.bRemote) {
               this.stateMachine.setCurrent(IdleState.instance());
            }

            super.update();
         }
      }
   }

   private void updateMechanicsItems() {
      if (!GameServer.bServer && !this.mechanicsItem.isEmpty()) {
         Iterator var1 = this.mechanicsItem.keySet().iterator();
         ArrayList var2 = new ArrayList();

         while(var1.hasNext()) {
            Long var3 = (Long)var1.next();
            Long var4 = (Long)this.mechanicsItem.get(var3);
            if (GameTime.getInstance().getCalender().getTimeInMillis() > var4 + 86400000L) {
               var2.add(var3);
            }
         }

         for(int var5 = 0; var5 < var2.size(); ++var5) {
            this.mechanicsItem.remove(var2.get(var5));
         }

      }
   }

   private void enterExitVehicle() {
      boolean var1 = this.PlayerIndex == 0 && GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact"));
      if (var1) {
         this.bUseVehicle = true;
         this.useVehicleDuration += GameTime.instance.getRealworldSecondsSinceLastUpdate();
      }

      if (!this.bUsedVehicle && this.bUseVehicle && (!var1 || this.useVehicleDuration > 0.5F)) {
         this.bUsedVehicle = true;
         if (this.getVehicle() != null) {
            LuaEventManager.triggerEvent("OnUseVehicle", this, this.getVehicle(), this.useVehicleDuration > 0.5F);
         } else {
            for(int var2 = 0; var2 < this.getCell().vehicles.size(); ++var2) {
               BaseVehicle var3 = (BaseVehicle)this.getCell().vehicles.get(var2);
               if (var3.getUseablePart(this) != null) {
                  LuaEventManager.triggerEvent("OnUseVehicle", this, var3, this.useVehicleDuration > 0.5F);
                  break;
               }
            }
         }
      }

      if (!var1) {
         this.bUseVehicle = false;
         this.bUsedVehicle = false;
         this.useVehicleDuration = 0.0F;
      }

   }

   public BaseVehicle getUseableVehicle() {
      if (this.getVehicle() != null) {
         return null;
      } else {
         int var1 = ((int)this.x - 4) / 10 - 1;
         int var2 = ((int)this.y - 4) / 10 - 1;
         int var3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F)) + 1;
         int var4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F)) + 1;

         for(int var5 = var2; var5 < var4; ++var5) {
            for(int var6 = var1; var6 < var3; ++var6) {
               IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var6, var5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var6 * 10, var5 * 10, 0);
               if (var7 != null) {
                  for(int var8 = 0; var8 < var7.vehicles.size(); ++var8) {
                     BaseVehicle var9 = (BaseVehicle)var7.vehicles.get(var8);
                     if (var9.getUseablePart(this) != null || var9.getBestSeat(this) != -1) {
                        return var9;
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   public BaseVehicle getNearVehicle() {
      if (this.getVehicle() != null) {
         return null;
      } else {
         int var1 = ((int)this.x - 4) / 10 - 1;
         int var2 = ((int)this.y - 4) / 10 - 1;
         int var3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F)) + 1;
         int var4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F)) + 1;

         for(int var5 = var2; var5 < var4; ++var5) {
            for(int var6 = var1; var6 < var3; ++var6) {
               IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var6, var5) : IsoWorld.instance.CurrentCell.getChunk(var6, var5);
               if (var7 != null) {
                  for(int var8 = 0; var8 < var7.vehicles.size(); ++var8) {
                     BaseVehicle var9 = (BaseVehicle)var7.vehicles.get(var8);
                     if ((int)this.getZ() == (int)var9.getZ() && (!this.isLocalPlayer() || var9.targetAlpha[this.PlayerIndex] != 0.0F) && !(this.DistToSquared((float)((int)var9.x), (float)((int)var9.y)) >= 16.0F) && PolygonalMap2.instance.intersectLineWithVehicle(this.x, this.y, this.x + this.angle.x * 4.0F, this.y + this.angle.y * 4.0F, var9, tempVector2) && !PolygonalMap2.instance.lineClearCollide(this.x, this.y, tempVector2.x, tempVector2.y, (int)this.z, var9, false, true)) {
                        return var9;
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   private void updateWhileInVehicle() {
      if (GameClient.bClient && this.getVehicle().getSeat(this) == -1) {
         DebugLog.log("forced " + this.getUsername() + " out of vehicle seat -1");
         this.setVehicle((BaseVehicle)null);
      } else {
         this.dirtyRecalcGridStackTime = 10.0F;
         if (this.getVehicle().isDriver(this)) {
            this.getVehicle().updatePhysics();
            if (!this.isAiming) {
               this.getVehicle().updateControls();
            }
         } else if (GameClient.connection != null) {
            PassengerMap.updatePassenger(this);
         }

         this.fallTime = 0;
         super.update();
         this.bSeenThisFrame = false;
         this.bCouldBeSeenThisFrame = false;
         this.closestZombie = 1000000.0F;
         this.updateLOS();
         if (!this.Asleep) {
            float var1 = 1.0F;
            var1 *= 1.0F - this.stats.fatigue;
            var1 *= GameTime.instance.getMultiplier();
            Stats var10000 = this.stats;
            var10000.endurance = (float)((double)var10000.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)var1);
         }

         if (this.vehicle != null) {
            Vector3f var6 = this.vehicle.getForwardVector(this.tempVector3f);
            this.angle.x = var6.x;
            this.angle.y = var6.z;
            this.angle.normalize();
            if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
               this.dirtyRecalcGridStackTime = 10.0F;
            }

            this.DirectionFromVector(this.angle);
            boolean var2 = false;
            int var3 = this.vehicle.getSeat(this);
            VehiclePart var4 = this.vehicle.getPassengerDoor(var3);
            if (var4 != null) {
               VehicleWindow var5 = var4.findWindow();
               if (var5 != null && !var5.isHittable()) {
                  var2 = true;
               }
            }

            if (var2) {
               this.attackWhileInVehicle();
            } else {
               this.isAiming = false;
            }
         }

         if (GameClient.bClient && networkUpdate.Check()) {
            GameClient.instance.sendPlayer(this);
         }

      }
   }

   private void attackWhileInVehicle() {
      this.isAiming = false;
      boolean var1 = false;
      boolean var2 = false;
      if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
         if (!this.bJoypadMovementActive) {
            return;
         }

         if (this.isChargingLT && !JoypadManager.instance.isLTPressed(this.JoypadBind)) {
            var2 = true;
         } else {
            var1 = this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind);
         }

         float var5 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
         float var4 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
         if (this.bJoypadIgnoreAimUntilCentered) {
            if (var5 == 0.0F && var4 == 0.0F) {
               this.bJoypadIgnoreAimUntilCentered = false;
            } else {
               var4 = 0.0F;
               var5 = 0.0F;
            }
         }

         this.isAiming = var5 * var5 + var4 * var4 >= 0.09F;
         this.isCharging = this.isAiming && JoypadManager.instance.isRTPressed(this.JoypadBind);
         this.isChargingLT = this.isAiming && JoypadManager.instance.isLTPressed(this.JoypadBind);
      } else {
         if (Mouse.isButtonDown(1)) {
            this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
         } else {
            this.TimeRightPressed = 0.0F;
         }

         boolean var3 = this.isAimKeyDown();
         this.isAiming = var3 || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= TIME_RIGHT_PRESSED_SECONDS;
         if (this.isCharging) {
            this.isCharging = var3 || Mouse.isButtonDown(1);
         } else {
            this.isCharging = var3 || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= TIME_RIGHT_PRESSED_SECONDS;
         }

         if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
            var2 = true;
         } else {
            var1 = this.isCharging && Mouse.isButtonDownUICheck(0);
            if (var1) {
               this.isAiming = true;
            }
         }
      }

      if (!this.isCharging && !this.isChargingLT) {
         this.chargeTime = 0.0F;
      }

      if (this.isAiming && !this.bBannedAttacking && this.CanAttack()) {
         this.chargeTime += GameTime.instance.getMultiplier();
         this.useChargeTime = this.chargeTime;
         this.setAngleFromAim();
         if (var2) {
            this.sprite.Animate = true;
            this.DoAttack(this.useChargeTime, true, (String)null);
            this.useChargeTime = 0.0F;
            this.chargeTime = 0.0F;
         } else {
            if (!this.isForceShove() && this.AttackDelay <= 0.0F && this.DoAimAnimOnAiming()) {
               this.PlayShootAnim();
            }

            if (var1) {
               this.sprite.Animate = true;
               this.AttemptAttack(this.useChargeTime);
               this.useChargeTime = 0.0F;
               this.chargeTime = 0.0F;
            }
         }

      }
   }

   private void setAngleFromAim() {
      Vector2 var1 = tempVector2;
      if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
         this.getControllerAimDir(var1);
      } else {
         var1.set(this.getX(), this.getY());
         int var2 = Mouse.getX();
         int var3 = Mouse.getY();
         var1.x -= IsoUtils.XToIso((float)var2, (float)var3 + 55.0F * this.def.getScaleY(), this.getZ());
         var1.y -= IsoUtils.YToIso((float)var2, (float)var3 + 55.0F * this.def.getScaleY(), this.getZ());
         var1.x = -var1.x;
         var1.y = -var1.y;
      }

      if (var1.getLength() > 0.0F) {
         var1.normalize();
         this.DirectionFromVector(var1);
         this.angle.x = var1.x;
         this.angle.y = var1.y;
         if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
            this.dirtyRecalcGridStackTime = 10.0F;
         }
      }

   }

   private void updateTorchStrength() {
      if (this.getTorchStrength() > 0.0F || this.flickTorch) {
         InventoryItem var1 = null;
         if (this.leftHandItem != null && this.leftHandItem.getLightStrength() > 0.0F && this.leftHandItem instanceof Drainable) {
            var1 = this.leftHandItem;
         }

         if (var1 == null && this.rightHandItem != null && this.rightHandItem.getLightStrength() > 0.0F && this.rightHandItem instanceof Drainable) {
            var1 = this.rightHandItem;
         }

         if (var1 == null) {
            return;
         }

         if (Rand.Next(600 - (int)(0.4D / (double)((Drainable)var1).getUsedDelta() * 100.0D)) == 0) {
            this.flickTorch = true;
         }

         if (this.flickTorch) {
            if (Rand.Next(6) == 0) {
               var1.setActivated(false);
            } else {
               var1.setActivated(true);
            }

            if (Rand.Next(40) == 0) {
               this.flickTorch = false;
               var1.setActivated(true);
            }
         }
      }

   }

   public IsoCell getCell() {
      return IsoWorld.instance.CurrentCell;
   }

   public void calculateContext() {
      float var1 = this.x;
      float var2 = this.y;
      float var3 = this.x;
      IsoGridSquare[] var4 = new IsoGridSquare[4];
      if (this.dir == IsoDirections.N) {
         var4[2] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)(var2 - 1.0F), (double)var3);
         var4[1] = this.getCell().getGridSquare((double)var1, (double)(var2 - 1.0F), (double)var3);
         var4[3] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)(var2 - 1.0F), (double)var3);
      } else if (this.dir == IsoDirections.NE) {
         var4[2] = this.getCell().getGridSquare((double)var1, (double)(var2 - 1.0F), (double)var3);
         var4[1] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)(var2 - 1.0F), (double)var3);
         var4[3] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)var2, (double)var3);
      } else if (this.dir == IsoDirections.E) {
         var4[2] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)(var2 - 1.0F), (double)var3);
         var4[1] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)var2, (double)var3);
         var4[3] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)(var2 + 1.0F), (double)var3);
      } else if (this.dir == IsoDirections.SE) {
         var4[2] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)var2, (double)var3);
         var4[1] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)(var2 + 1.0F), (double)var3);
         var4[3] = this.getCell().getGridSquare((double)var1, (double)(var2 + 1.0F), (double)var3);
      } else if (this.dir == IsoDirections.S) {
         var4[2] = this.getCell().getGridSquare((double)(var1 + 1.0F), (double)(var2 + 1.0F), (double)var3);
         var4[1] = this.getCell().getGridSquare((double)var1, (double)(var2 + 1.0F), (double)var3);
         var4[3] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)(var2 + 1.0F), (double)var3);
      } else if (this.dir == IsoDirections.SW) {
         var4[2] = this.getCell().getGridSquare((double)var1, (double)(var2 + 1.0F), (double)var3);
         var4[1] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)(var2 + 1.0F), (double)var3);
         var4[3] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)var2, (double)var3);
      } else if (this.dir == IsoDirections.W) {
         var4[2] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)(var2 + 1.0F), (double)var3);
         var4[1] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)var2, (double)var3);
         var4[3] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)(var2 - 1.0F), (double)var3);
      } else if (this.dir == IsoDirections.NW) {
         var4[2] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)var2, (double)var3);
         var4[1] = this.getCell().getGridSquare((double)(var1 - 1.0F), (double)(var2 - 1.0F), (double)var3);
         var4[3] = this.getCell().getGridSquare((double)var1, (double)(var2 - 1.0F), (double)var3);
      }

      var4[0] = this.current;

      for(int var5 = 0; var5 < 4; ++var5) {
         IsoGridSquare var6 = var4[var5];
         if (var6 == null) {
         }
      }

   }

   private boolean isSafeToClimbOver(IsoDirections var1) {
      IsoGridSquare var2 = null;
      switch(var1) {
      case N:
         var2 = this.getCell().getGridSquare((double)this.x, (double)(this.y - 1.0F), (double)this.z);
         break;
      case S:
         var2 = this.getCell().getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
         break;
      case W:
         var2 = this.getCell().getGridSquare((double)(this.x - 1.0F), (double)this.y, (double)this.z);
         break;
      case E:
         var2 = this.getCell().getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
         break;
      default:
         DebugLog.log("IsoPlayer.isSafeToClimbOver(): unhandled direction");
         return false;
      }

      return var2 != null && var2.TreatAsSolidFloor() && !var2.Is(IsoFlagType.water);
   }

   private boolean doContext(IsoDirections var1, boolean var2) {
      if (this instanceof IsoPlayer && this.isBlockMovement()) {
         return false;
      } else {
         float var4 = this.x - (float)((int)this.x);
         float var5 = this.y - (float)((int)this.y);
         if (var1 == IsoDirections.NW) {
            if (var5 < var4) {
               return this.doContext(IsoDirections.N, var2) ? true : this.doContext(IsoDirections.W, var2);
            } else {
               return this.doContext(IsoDirections.W, var2) ? true : this.doContext(IsoDirections.N, var2);
            }
         } else if (var1 == IsoDirections.NE) {
            var4 = 1.0F - var4;
            if (var5 < var4) {
               return this.doContext(IsoDirections.N, var2) ? true : this.doContext(IsoDirections.E, var2);
            } else {
               return this.doContext(IsoDirections.E, var2) ? true : this.doContext(IsoDirections.N, var2);
            }
         } else if (var1 == IsoDirections.SE) {
            var4 = 1.0F - var4;
            var5 = 1.0F - var5;
            if (var5 < var4) {
               return this.doContext(IsoDirections.S, var2) ? true : this.doContext(IsoDirections.E, var2);
            } else {
               return this.doContext(IsoDirections.E, var2) ? true : this.doContext(IsoDirections.S, var2);
            }
         } else if (var1 == IsoDirections.SW) {
            var5 = 1.0F - var5;
            if (var5 < var4) {
               return this.doContext(IsoDirections.S, var2) ? true : this.doContext(IsoDirections.W, var2);
            } else {
               return this.doContext(IsoDirections.W, var2) ? true : this.doContext(IsoDirections.S, var2);
            }
         } else {
            if (this.current != null) {
               if (var1 == IsoDirections.N && this.current.Is(IsoFlagType.climbSheetN) && this.canClimbSheetRope(this.current)) {
                  this.climbSheetRope();
                  return true;
               }

               if (var1 == IsoDirections.S && this.current.Is(IsoFlagType.climbSheetS) && this.canClimbSheetRope(this.current)) {
                  this.climbSheetRope();
                  return true;
               }

               if (var1 == IsoDirections.W && this.current.Is(IsoFlagType.climbSheetW) && this.canClimbSheetRope(this.current)) {
                  this.climbSheetRope();
                  return true;
               }

               if (var1 == IsoDirections.E && this.current.Is(IsoFlagType.climbSheetE) && this.canClimbSheetRope(this.current)) {
                  this.climbSheetRope();
                  return true;
               }
            }

            IsoGridSquare var6 = this.getCurrentSquare();
            if (var1 == IsoDirections.S) {
               var6 = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
            } else if (var1 == IsoDirections.E) {
               var6 = IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
            }

            if (var6 == null) {
               return false;
            } else {
               boolean var7 = var1 == IsoDirections.S || var1 == IsoDirections.N;
               IsoObject var8 = var6.getDoorOrWindow(var7);
               if (var8 == null) {
                  var8 = var6.getWindowFrame(var7);
               }

               if (var8 == null) {
                  var8 = this.getCurrentSquare().getOpenDoor(var1);
               }

               IsoGridSquare var9;
               if (var8 == null) {
                  if (var1 == IsoDirections.N) {
                     var9 = var6.nav[IsoDirections.N.index()];
                     if (var9 != null) {
                        var8 = var9.getOpenDoor(IsoDirections.S);
                     }
                  } else if (var1 == IsoDirections.S) {
                     var8 = var6.getOpenDoor(IsoDirections.N);
                  } else if (var1 == IsoDirections.W) {
                     var9 = var6.nav[IsoDirections.W.index()];
                     if (var9 != null) {
                        var8 = var9.getOpenDoor(IsoDirections.E);
                     }
                  } else if (var1 == IsoDirections.E) {
                     var8 = var6.getOpenDoor(IsoDirections.W);
                  }
               }

               var9 = this.getCurrentSquare() == null ? null : this.getCurrentSquare().nav[var1.index()];
               boolean var10 = IsoWindow.isTopOfSheetRopeHere(var9) && this.canClimbDownSheetRope(var9);
               if (var8 == null) {
                  if (Keyboard.isKeyDown(42) && this.current != null && this.ticksSincePressedMovement > PerformanceSettings.LockFPS / 2) {
                     IsoObject var16 = this.current.getDoor(true);
                     if (var16 instanceof IsoDoor && ((IsoDoor)var16).isFacingSheet(this)) {
                        ((IsoDoor)var16).toggleCurtain();
                        return true;
                     }

                     IsoObject var18 = this.current.getDoor(false);
                     if (var18 instanceof IsoDoor && ((IsoDoor)var18).isFacingSheet(this)) {
                        ((IsoDoor)var18).toggleCurtain();
                        return true;
                     }

                     IsoObject var13;
                     if (var1 == IsoDirections.E) {
                        var6 = IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
                        var13 = var6 != null ? var6.getDoor(true) : null;
                        if (var13 instanceof IsoDoor && ((IsoDoor)var13).isFacingSheet(this)) {
                           ((IsoDoor)var13).toggleCurtain();
                           return true;
                        }
                     }

                     if (var1 == IsoDirections.S) {
                        var6 = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
                        var13 = var6 != null ? var6.getDoor(false) : null;
                        if (var13 instanceof IsoDoor && ((IsoDoor)var13).isFacingSheet(this)) {
                           ((IsoDoor)var13).toggleCurtain();
                           return true;
                        }
                     }
                  }

                  boolean var17 = this.isSafeToClimbOver(var1);
                  if (this.z > 0.0F) {
                     var17 = false;
                     if (var10) {
                        var17 = true;
                     }
                  }

                  if (this.timePressedContext < 0.5F && !var17) {
                     return false;
                  } else if (var1 == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
                     this.StateMachineParams.clear();
                     this.StateMachineParams.put(0, var1);
                     this.changeState(ClimbOverFenceState.instance());
                     return true;
                  } else if (var1 == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
                     this.StateMachineParams.clear();
                     this.StateMachineParams.put(0, var1);
                     this.changeState(ClimbOverFenceState.instance());
                     return true;
                  } else if (var1 == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z).Is(IsoFlagType.HoppableN)) {
                     this.StateMachineParams.clear();
                     this.StateMachineParams.put(0, var1);
                     this.changeState(ClimbOverFenceState.instance());
                     return true;
                  } else if (var1 == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z).Is(IsoFlagType.HoppableW)) {
                     this.StateMachineParams.clear();
                     this.StateMachineParams.put(0, var1);
                     this.changeState(ClimbOverFenceState.instance());
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  if (var8 instanceof IsoDoor) {
                     IsoDoor var11 = (IsoDoor)var8;
                     if (Keyboard.isKeyDown(42) && var11.HasCurtains() != null && var11.isFacingSheet(this) && this.ticksSincePressedMovement > PerformanceSettings.LockFPS / 2) {
                        var11.toggleCurtain();
                     } else if (!var2) {
                        var11.ToggleDoor(this);
                     }
                  } else {
                     IsoThumpable var14;
                     if (var8 instanceof IsoThumpable && ((IsoThumpable)var8).isDoor()) {
                        var14 = (IsoThumpable)var8;
                        if (!var2) {
                           var14.ToggleDoor(this);
                        }
                     } else {
                        IsoCurtain var12;
                        if (var8 instanceof IsoWindow && !var8.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
                           IsoWindow var15 = (IsoWindow)var8;
                           if (Keyboard.isKeyDown(42)) {
                              var12 = var15.HasCurtains();
                              if (var12 != null && this.current != null && !var12.getSquare().isBlockedTo(this.current)) {
                                 var12.ToggleDoor(this);
                              }
                           } else if (this.timePressedContext >= 0.5F) {
                              if (var15.canClimbThrough(this)) {
                                 if (!var2 && !this.isBlockMovement()) {
                                    this.StateMachineParams.clear();
                                    this.StateMachineParams.put(0, var15);
                                    this.changeState(ClimbThroughWindowState.instance());
                                 }
                              } else if (!var15.PermaLocked && !var15.isBarricaded() && !var2) {
                                 this.openWindow(var15);
                              }
                           } else if (var15.Health > 0 && !var15.isDestroyed()) {
                              if (!var15.open && var15.getBarricadeForCharacter(this) == null) {
                                 if (!var2) {
                                    this.openWindow(var15);
                                 }
                              } else if (!var2) {
                                 var15.ToggleWindow(this);
                              }
                           } else {
                              if (!this.isSafeToClimbOver(var1) && !var8.getSquare().haveSheetRope && !var10) {
                                 return true;
                              }

                              if (!var2 && !var15.isBarricaded() && !this.isBlockMovement()) {
                                 this.StateMachineParams.clear();
                                 this.StateMachineParams.put(0, var15);
                                 this.changeState(ClimbThroughWindowState.instance());
                              } else {
                                 printString = "Climb through";
                              }
                           }
                        } else if (var8 instanceof IsoThumpable && !var8.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
                           var14 = (IsoThumpable)var8;
                           if (Keyboard.isKeyDown(42)) {
                              var12 = var14.HasCurtains();
                              if (var12 != null && this.current != null && !var12.getSquare().isBlockedTo(this.current)) {
                                 var12.ToggleDoor(this);
                              }
                           } else if (this.timePressedContext >= 0.5F) {
                              if (!var14.isBarricaded() && !var2 && !this.isBlockMovement()) {
                                 this.StateMachineParams.clear();
                                 this.StateMachineParams.put(0, var14);
                                 this.changeState(ClimbThroughWindowState.instance());
                              }
                           } else {
                              if (!this.isSafeToClimbOver(var1) && !var8.getSquare().haveSheetRope && !var10) {
                                 return false;
                              }

                              if (!var2 && !var14.isBarricaded() && !this.isBlockMovement()) {
                                 this.StateMachineParams.clear();
                                 this.StateMachineParams.put(0, var14);
                                 this.changeState(ClimbThroughWindowState.instance());
                              } else {
                                 printString = "Climb through";
                              }
                           }
                        } else if (IsoWindowFrame.isWindowFrame(var8) && (this.timePressedContext >= 0.5F || this.isSafeToClimbOver(var1) || var10) && !var2 && !this.isBlockMovement()) {
                           this.StateMachineParams.clear();
                           this.StateMachineParams.put(0, var8);
                           this.changeState(ClimbThroughWindowState.instance());
                        }
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   public boolean hopFence(IsoDirections var1, boolean var2) {
      float var4 = this.x - (float)((int)this.x);
      float var5 = this.y - (float)((int)this.y);
      if (var1 == IsoDirections.NW) {
         if (var5 < var4) {
            return this.hopFence(IsoDirections.N, var2) ? true : this.hopFence(IsoDirections.W, var2);
         } else {
            return this.hopFence(IsoDirections.W, var2) ? true : this.hopFence(IsoDirections.N, var2);
         }
      } else if (var1 == IsoDirections.NE) {
         var4 = 1.0F - var4;
         if (var5 < var4) {
            return this.hopFence(IsoDirections.N, var2) ? true : this.hopFence(IsoDirections.E, var2);
         } else {
            return this.hopFence(IsoDirections.E, var2) ? true : this.hopFence(IsoDirections.N, var2);
         }
      } else if (var1 == IsoDirections.SE) {
         var4 = 1.0F - var4;
         var5 = 1.0F - var5;
         if (var5 < var4) {
            return this.hopFence(IsoDirections.S, var2) ? true : this.hopFence(IsoDirections.E, var2);
         } else {
            return this.hopFence(IsoDirections.E, var2) ? true : this.hopFence(IsoDirections.S, var2);
         }
      } else if (var1 == IsoDirections.SW) {
         var5 = 1.0F - var5;
         if (var5 < var4) {
            return this.hopFence(IsoDirections.S, var2) ? true : this.hopFence(IsoDirections.W, var2);
         } else {
            return this.hopFence(IsoDirections.W, var2) ? true : this.hopFence(IsoDirections.S, var2);
         }
      } else if (var1 == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
         if (var2) {
            return true;
         } else {
            this.StateMachineParams.clear();
            this.StateMachineParams.put(0, var1);
            this.changeState(ClimbOverFenceState.instance());
            return true;
         }
      } else if (var1 == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
         if (var2) {
            return true;
         } else {
            this.StateMachineParams.clear();
            this.StateMachineParams.put(0, var1);
            this.changeState(ClimbOverFenceState.instance());
            return true;
         }
      } else if (var1 == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z).Is(IsoFlagType.HoppableN)) {
         if (var2) {
            return true;
         } else {
            this.StateMachineParams.clear();
            this.StateMachineParams.put(0, var1);
            this.changeState(ClimbOverFenceState.instance());
            return true;
         }
      } else if (var1 == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z).Is(IsoFlagType.HoppableW)) {
         if (var2) {
            return true;
         } else {
            this.StateMachineParams.clear();
            this.StateMachineParams.put(0, var1);
            this.changeState(ClimbOverFenceState.instance());
            return true;
         }
      } else {
         return false;
      }
   }

   private void updateSleepingPillsTaken() {
      if (this.getSleepingPillsTaken() > 0 && this.lastPillsTaken > 0L && GameTime.instance.Calender.getTimeInMillis() - this.lastPillsTaken > 7200000L) {
         this.setSleepingPillsTaken(this.getSleepingPillsTaken() - 1);
      }

   }

   public boolean AttemptAttack() {
      return this.DoAttack(this.useChargeTime);
   }

   public boolean DoAttack(float var1) {
      return this.DoAttack(var1, false, (String)null);
   }

   public boolean DoAttack(float var1, boolean var2, String var3) {
      this.setForceShove(var2);
      this.setClickSound(var3);
      if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance() && !(this.getRecoilDelay() > 0.0F)) {
         if (var2) {
            var1 *= 2.0F;
         }

         if (var1 > 90.0F) {
            var1 = 90.0F;
         }

         var1 /= 25.0F;
         this.useChargeDelta = var1;
         if (this instanceof IsoPlayer) {
            this.FakeAttack = false;
         }

         if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() < 0.0F)) {
            if (this.AttackDelay <= 0.0F && (!this.sprite.CurrentAnim.name.contains("Attack") || this.def.Frame >= (float)(this.sprite.CurrentAnim.Frames.size() - 1)) || this.def.Frame == 0.0F) {
               Object var4 = this.leftHandItem;
               if (var4 == null || !(var4 instanceof HandWeapon) || var2) {
                  var4 = this.bareHands;
               }

               if (var4 instanceof HandWeapon) {
                  this.useHandWeapon = (HandWeapon)var4;
                  int var5 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
                  if (this.useHandWeapon.isCantAttackWithLowestEndurance() && var5 == 4) {
                     return false;
                  }

                  if (this.PlayerIndex == 0 && this.JoypadBind == -1 && UIManager.getPicked() != null) {
                     this.attackTargetSquare = UIManager.getPicked().square;
                     if (UIManager.getPicked().tile instanceof IsoMovingObject) {
                        this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
                     }
                  }

                  if (this.useHandWeapon.getOtherHandRequire() == null || this.rightHandItem != null && this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire())) {
                     float var6 = this.useHandWeapon.getSwingTime();
                     if (this.useHandWeapon.isCantAttackWithLowestEndurance() && var5 == 4) {
                        return false;
                     }

                     if (this.useHandWeapon.isUseEndurance()) {
                        switch(var5) {
                        case 1:
                           var6 *= 1.1F;
                           break;
                        case 2:
                           var6 *= 1.2F;
                           break;
                        case 3:
                           var6 *= 1.3F;
                           break;
                        case 4:
                           var6 *= 1.4F;
                        }
                     }

                     if (var6 < this.useHandWeapon.getMinimumSwingTime()) {
                        var6 = this.useHandWeapon.getMinimumSwingTime();
                     }

                     var6 *= this.useHandWeapon.getSpeedMod(this);
                     var6 *= 1.0F / GameTime.instance.getMultiplier();
                     if (this.HasTrait("BaseballPlayer") && this.useHandWeapon.getType().contains("Baseball")) {
                        var6 *= 0.8F;
                     }

                     this.AttackDelayMax = this.AttackDelay = var6 * GameTime.instance.getMultiplier() * 0.6F;
                     this.AttackDelayUse = this.AttackDelayMax * this.useHandWeapon.getDoSwingBeforeImpact();
                     if (this.AttackDelayUse == 0.0F) {
                        this.AttackDelayUse = 0.2F;
                     }

                     this.AttackDelay = 0.0F;
                     this.AttackWasSuperAttack = this.superAttack;
                     if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                        this.setRecoilDelay((float)(this.useHandWeapon.getRecoilDelay() - this.getPerkLevel(PerkFactory.Perks.Aiming) * 2));
                        if (var2) {
                           this.setRecoilDelay(10.0F);
                        }

                        this.stateMachine.changeState(SwipeStatePlayer.instance());
                     }

                     if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
                        this.leftHandItem.Use();
                     }

                     if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
                        this.rightHandItem.Use();
                     }

                     return true;
                  }

                  return false;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int getPlayerNum() {
      return this.PlayerIndex;
   }

   public void updateLOS() {
      this.spottedList.clear();
      this.stats.NumVisibleZombies = 0;
      this.stats.LastNumChasingZombies = this.stats.NumChasingZombies;
      this.stats.NumChasingZombies = 0;
      int var1 = 0;
      this.NumSurvivorsInVicinity = 0;
      int var2 = this.getCell().getObjectList().size();

      for(int var3 = 0; var3 < var2; ++var3) {
         IsoMovingObject var4 = (IsoMovingObject)this.getCell().getObjectList().get(var3);
         if (!(var4 instanceof IsoPhysicsObject) && !(var4 instanceof BaseVehicle)) {
            if (var4 == this) {
               this.spottedList.add(var4);
            } else {
               float var5 = IsoUtils.DistanceManhatten(var4.getX(), var4.getY(), this.getX(), this.getY());
               if (var5 < 20.0F) {
                  ++var1;
               }

               if (var4.getCurrentSquare() != null) {
                  if (this.getCurrentSquare() == null) {
                     return;
                  }

                  boolean var6 = GameServer.bServer ? ServerLOS.instance.isCouldSee(this, var4.getCurrentSquare()) : var4.getCurrentSquare().isCouldSee(this.PlayerIndex);
                  boolean var7 = GameServer.bServer ? var6 : var4.getCurrentSquare().isCanSee(this.PlayerIndex);
                  if (this.isAsleep() || !var7 && (!(var5 < 2.5F) || !var6)) {
                     if (var4 != instance) {
                        var4.targetAlpha[getPlayerIndex()] = 0.0F;
                     }

                     if (var6) {
                        this.TestZombieSpotPlayer(var4);
                     }
                  } else {
                     this.TestZombieSpotPlayer(var4);
                     if (var4 instanceof IsoGameCharacter && ((IsoGameCharacter)var4).SpottedSinceAlphaZero[this.PlayerIndex]) {
                        if (var4 instanceof IsoSurvivor) {
                           ++this.NumSurvivorsInVicinity;
                        }

                        if (var4 instanceof IsoZombie) {
                           this.lastSeenZombieTime = 0.0D;
                           if (var4.getZ() >= this.getZ() - 1.0F && var5 < 7.0F && !((IsoZombie)((IsoZombie)var4)).Ghost && !((IsoZombie)((IsoZombie)var4)).isFakeDead() && var4.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom()) {
                              this.TicksSinceSeenZombie = 0;
                              ++this.stats.NumVisibleZombies;
                           }
                        }

                        this.spottedList.add(var4);
                        var4.targetAlpha[getPlayerIndex()] = 1.0F;
                        float var8 = 4.0F;
                        if (this.stats.NumVisibleZombies > 4) {
                           var8 = 7.0F;
                        }

                        if (var5 < var8 * 2.0F && var4 instanceof IsoZombie && (int)var4.getZ() == (int)this.getZ() && !this.GhostMode && !GameClient.bClient) {
                           GameTime.instance.setMultiplier(1.0F);
                           UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                        }

                        if (var5 < var8 && var4 instanceof IsoZombie && (int)var4.getZ() == (int)this.getZ() && !this.LastSpotted.contains(var4)) {
                           Stats var10000 = this.stats;
                           var10000.NumVisibleZombies += 2;
                        }
                     } else {
                        var4.targetAlpha[getPlayerIndex()] = 1.0F;
                     }
                  }

                  if (this.GhostMode) {
                     var4.alpha[getPlayerIndex()] = var4.targetAlpha[getPlayerIndex()] = 1.0F;
                  }

                  if (var4 instanceof IsoGameCharacter && ((IsoGameCharacter)var4).invisible && this.accessLevel.equals("")) {
                     var4.alpha[getPlayerIndex()] = var4.targetAlpha[getPlayerIndex()] = 0.0F;
                  }
               }
            }
         }
      }

      if (!this.isDead() && this.stats.NumVisibleZombies > 0 && this.timeSinceLastStab <= 0) {
         this.timeSinceLastStab = 1800;
         this.getEmitter().playSoundImpl("ZombieSurprisedPlayer", (IsoObject)null);
      }

      --this.timeSinceLastStab;
      float var9 = (float)var1 / 20.0F;
      if (var9 > 1.0F) {
         var9 = 1.0F;
      }

      var9 *= 0.6F;
      SoundManager.instance.BlendVolume(MainScreenState.ambient, var9);
      int var10 = 0;

      for(int var11 = 0; var11 < this.spottedList.size(); ++var11) {
         if (!this.LastSpotted.contains(this.spottedList.get(var11))) {
            this.LastSpotted.add(this.spottedList.get(var11));
         }

         if (this.spottedList.get(var11) instanceof IsoZombie) {
            ++var10;
         }
      }

      if (this.ClearSpottedTimer <= 0 && var10 == 0) {
         this.LastSpotted.clear();
         this.ClearSpottedTimer = 1000;
      } else {
         --this.ClearSpottedTimer;
      }

   }

   void DoHotKey(int var1, int var2) {
      if (GameKeyboard.isKeyDown(var2)) {
         if (GameKeyboard.isKeyDown(42) && GameKeyboard.isKeyDown(29)) {
            UIManager.setDoMouseControls(true);
            if (this.leftHandItem != null) {
               this.MainHot[var1] = this.leftHandItem.getType();
            } else {
               this.MainHot[var1] = null;
            }

            if (this.rightHandItem != null) {
               this.SecHot[var1] = this.rightHandItem.getType();
            } else {
               this.SecHot[var1] = null;
            }
         } else {
            this.leftHandItem = this.inventory.FindAndReturn(this.MainHot[var1]);
            this.rightHandItem = this.inventory.FindAndReturn(this.SecHot[var1]);
         }
      }

   }

   private void PressedA() {
      IsoObject var1 = this.getInteract();
      if (var1 != null) {
         if (var1 instanceof IsoDoor) {
            ((IsoDoor)var1).ToggleDoor(this);
         }

         if (var1 instanceof IsoThumpable) {
            ((IsoThumpable)var1).ToggleDoor(this);
         }

         if (var1.container != null) {
            var1.onMouseLeftClick(0, 0);
         }
      }

      this.DebounceA = true;
   }

   public void saveGame() {
   }

   public int getAngleCounter() {
      return this.angleCounter;
   }

   public void setAngleCounter(int var1) {
      this.angleCounter = var1;
   }

   public Vector2 getLastAngle() {
      return this.lastAngle;
   }

   public void setLastAngle(Vector2 var1) {
      this.lastAngle = var1;
   }

   public int getDialogMood() {
      return this.DialogMood;
   }

   public void setDialogMood(int var1) {
      this.DialogMood = var1;
   }

   public int getPing() {
      return this.ping;
   }

   public void setPing(int var1) {
      this.ping = var1;
   }

   public boolean isFakeAttack() {
      return this.FakeAttack;
   }

   public void setFakeAttack(boolean var1) {
      this.FakeAttack = var1;
   }

   public IsoObject getFakeAttackTarget() {
      return this.FakeAttackTarget;
   }

   public void setFakeAttackTarget(IsoObject var1) {
      this.FakeAttackTarget = var1;
   }

   public IsoMovingObject getDragObject() {
      return this.DragObject;
   }

   public void setDragObject(IsoMovingObject var1) {
      this.DragObject = var1;
   }

   public float getAsleepTime() {
      return this.AsleepTime;
   }

   public void setAsleepTime(float var1) {
      this.AsleepTime = var1;
   }

   public String[] getMainHot() {
      return this.MainHot;
   }

   public void setMainHot(String[] var1) {
      this.MainHot = var1;
   }

   public String[] getSecHot() {
      return this.SecHot;
   }

   public void setSecHot(String[] var1) {
      this.SecHot = var1;
   }

   public Stack getSpottedList() {
      return this.spottedList;
   }

   public void setSpottedList(Stack var1) {
      this.spottedList = var1;
   }

   public int getTicksSinceSeenZombie() {
      return this.TicksSinceSeenZombie;
   }

   public void setTicksSinceSeenZombie(int var1) {
      this.TicksSinceSeenZombie = var1;
   }

   public boolean isWaiting() {
      return this.Waiting;
   }

   public void setWaiting(boolean var1) {
      this.Waiting = var1;
   }

   public IsoSurvivor getDragCharacter() {
      return this.DragCharacter;
   }

   public void setDragCharacter(IsoSurvivor var1) {
      this.DragCharacter = var1;
   }

   public Stack getLastPos() {
      return this.lastPos;
   }

   public void setLastPos(Stack var1) {
      this.lastPos = var1;
   }

   public boolean isbDebounceLMB() {
      return this.bDebounceLMB;
   }

   public void setbDebounceLMB(boolean var1) {
      this.bDebounceLMB = var1;
   }

   public float getHeartDelay() {
      return this.heartDelay;
   }

   public void setHeartDelay(float var1) {
      this.heartDelay = var1;
   }

   public float getHeartDelayMax() {
      return this.heartDelayMax;
   }

   public void setHeartDelayMax(int var1) {
      this.heartDelayMax = (float)var1;
   }

   public double getHoursSurvived() {
      return this.HoursSurvived;
   }

   public void setHoursSurvived(double var1) {
      this.HoursSurvived = var1;
   }

   public String getTimeSurvived() {
      String var1 = "";
      int var2 = (int)this.getHoursSurvived();
      int var4 = var2 / 24;
      int var3 = var2 % 24;
      int var5 = var4 / 30;
      var4 %= 30;
      int var6 = var5 / 12;
      var5 %= 12;
      String var7 = Translator.getText("IGUI_Gametime_day");
      String var8 = Translator.getText("IGUI_Gametime_year");
      String var9 = Translator.getText("IGUI_Gametime_hour");
      String var10 = Translator.getText("IGUI_Gametime_month");
      if (var6 != 0) {
         if (var6 > 1) {
            var8 = Translator.getText("IGUI_Gametime_years");
         }

         if (var1.length() > 0) {
            var1 = var1 + ", ";
         }

         var1 = var1 + var6 + " " + var8;
      }

      if (var5 != 0) {
         if (var5 > 1) {
            var10 = Translator.getText("IGUI_Gametime_months");
         }

         if (var1.length() > 0) {
            var1 = var1 + ", ";
         }

         var1 = var1 + var5 + " " + var10;
      }

      if (var4 != 0) {
         if (var4 > 1) {
            var7 = Translator.getText("IGUI_Gametime_days");
         }

         if (var1.length() > 0) {
            var1 = var1 + ", ";
         }

         var1 = var1 + var4 + " " + var7;
      }

      if (var3 != 0) {
         if (var3 > 1) {
            var9 = Translator.getText("IGUI_Gametime_hours");
         }

         if (var1.length() > 0) {
            var1 = var1 + ", ";
         }

         var1 = var1 + var3 + " " + var9;
      }

      if (var1.isEmpty()) {
         int var11 = (int)(this.HoursSurvived * 60.0D);
         var1 = var11 + " " + Translator.getText("IGUI_Gametime_minutes");
      }

      return var1;
   }

   public float getDrunkOscilatorStepSin() {
      return this.DrunkOscilatorStepSin;
   }

   public void setDrunkOscilatorStepSin(float var1) {
      this.DrunkOscilatorStepSin = var1;
   }

   public float getDrunkOscilatorRateSin() {
      return this.DrunkOscilatorRateSin;
   }

   public void setDrunkOscilatorRateSin(float var1) {
      this.DrunkOscilatorRateSin = var1;
   }

   public float getDrunkOscilatorStepCos() {
      return this.DrunkOscilatorStepCos;
   }

   public void setDrunkOscilatorStepCos(float var1) {
      this.DrunkOscilatorStepCos = var1;
   }

   public float getDrunkOscilatorRateCos() {
      return this.DrunkOscilatorRateCos;
   }

   public void setDrunkOscilatorRateCos(float var1) {
      this.DrunkOscilatorRateCos = var1;
   }

   public float getDrunkOscilatorStepCos2() {
      return this.DrunkOscilatorStepCos2;
   }

   public void setDrunkOscilatorStepCos2(float var1) {
      this.DrunkOscilatorStepCos2 = var1;
   }

   public float getDrunkOscilatorRateCos2() {
      return this.DrunkOscilatorRateCos2;
   }

   public void setDrunkOscilatorRateCos2(float var1) {
      this.DrunkOscilatorRateCos2 = var1;
   }

   public float getDrunkSin() {
      return this.DrunkSin;
   }

   public void setDrunkSin(float var1) {
      this.DrunkSin = var1;
   }

   public float getDrunkCos() {
      return this.DrunkCos;
   }

   public void setDrunkCos(float var1) {
      this.DrunkCos = var1;
   }

   public float getDrunkCos2() {
      return this.DrunkCos2;
   }

   public void setDrunkCos2(float var1) {
      this.DrunkCos2 = var1;
   }

   public float getMinOscilatorRate() {
      return this.MinOscilatorRate;
   }

   public void setMinOscilatorRate(float var1) {
      this.MinOscilatorRate = var1;
   }

   public float getMaxOscilatorRate() {
      return this.MaxOscilatorRate;
   }

   public void setMaxOscilatorRate(float var1) {
      this.MaxOscilatorRate = var1;
   }

   public float getDesiredSinRate() {
      return this.DesiredSinRate;
   }

   public void setDesiredSinRate(float var1) {
      this.DesiredSinRate = var1;
   }

   public float getDesiredCosRate() {
      return this.DesiredCosRate;
   }

   public void setDesiredCosRate(float var1) {
      this.DesiredCosRate = var1;
   }

   public float getOscilatorChangeRate() {
      return this.OscilatorChangeRate;
   }

   public void setOscilatorChangeRate(float var1) {
      this.OscilatorChangeRate = var1;
   }

   public float getMaxWeightDelta() {
      return this.maxWeightDelta;
   }

   public void setMaxWeightDelta(float var1) {
      this.maxWeightDelta = var1;
   }

   public String getForname() {
      return this.Forname;
   }

   public void setForname(String var1) {
      this.Forname = var1;
   }

   public String getSurname() {
      return this.Surname;
   }

   public void setSurname(String var1) {
      this.Surname = var1;
   }

   public IsoSprite getGuardModeUISprite() {
      return this.GuardModeUISprite;
   }

   public void setGuardModeUISprite(IsoSprite var1) {
      this.GuardModeUISprite = var1;
   }

   public int getGuardModeUI() {
      return this.GuardModeUI;
   }

   public void setGuardModeUI(int var1) {
      this.GuardModeUI = var1;
   }

   public IsoSurvivor getGuardChosen() {
      return this.GuardChosen;
   }

   public void setGuardChosen(IsoSurvivor var1) {
      this.GuardChosen = var1;
   }

   public IsoGridSquare getGuardStand() {
      return this.GuardStand;
   }

   public void setGuardStand(IsoGridSquare var1) {
      this.GuardStand = var1;
   }

   public IsoGridSquare getGuardFace() {
      return this.GuardFace;
   }

   public void setGuardFace(IsoGridSquare var1) {
      this.GuardFace = var1;
   }

   public boolean isbSneaking() {
      return this.bSneaking;
   }

   public void setbSneaking(boolean var1) {
      this.bSneaking = var1;
   }

   public boolean isbChangeCharacterDebounce() {
      return this.bChangeCharacterDebounce;
   }

   public void setbChangeCharacterDebounce(boolean var1) {
      this.bChangeCharacterDebounce = var1;
   }

   public int getFollowID() {
      return this.followID;
   }

   public void setFollowID(int var1) {
      this.followID = var1;
   }

   public boolean isbSeenThisFrame() {
      return this.bSeenThisFrame;
   }

   public void setbSeenThisFrame(boolean var1) {
      this.bSeenThisFrame = var1;
   }

   public boolean isbCouldBeSeenThisFrame() {
      return this.bCouldBeSeenThisFrame;
   }

   public void setbCouldBeSeenThisFrame(boolean var1) {
      this.bCouldBeSeenThisFrame = var1;
   }

   public int getTimeSinceLastStab() {
      return this.timeSinceLastStab;
   }

   public void setTimeSinceLastStab(int var1) {
      this.timeSinceLastStab = var1;
   }

   public Stack getLastSpotted() {
      return this.LastSpotted;
   }

   public void setLastSpotted(Stack var1) {
      this.LastSpotted = var1;
   }

   public int getClearSpottedTimer() {
      return this.ClearSpottedTimer;
   }

   public void setClearSpottedTimer(int var1) {
      this.ClearSpottedTimer = var1;
   }

   public boolean IsRunning() {
      return this.bRunning;
   }

   public void InitSpriteParts() {
      SurvivorDesc var1 = this.descriptor;
      this.InitSpriteParts(var1, var1.legs, var1.torso, var1.head, var1.top, var1.bottoms, var1.shoes, var1.skinpal, var1.toppal, var1.bottomspal, var1.shoespal, var1.hair, var1.extra);
   }

   public boolean IsAiming() {
      return this.isAiming;
   }

   public boolean IsUsingAimWeapon() {
      if (this.leftHandItem == null) {
         return false;
      } else if (!(this.leftHandItem instanceof HandWeapon)) {
         return false;
      } else if (!this.isAiming) {
         return false;
      } else {
         return ((HandWeapon)this.leftHandItem).bIsAimedFirearm;
      }
   }

   private boolean IsUsingAimHandWeapon() {
      if (this.leftHandItem == null) {
         return false;
      } else if (!(this.leftHandItem instanceof HandWeapon)) {
         return false;
      } else if (!this.isAiming) {
         return false;
      } else {
         return ((HandWeapon)this.leftHandItem).bIsAimedHandWeapon;
      }
   }

   private boolean DoAimAnimOnAiming() {
      return this.IsUsingAimWeapon();
   }

   public int getSleepingPillsTaken() {
      return this.sleepingPillsTaken;
   }

   public void setSleepingPillsTaken(int var1) {
      this.sleepingPillsTaken = var1;
      if (this.getStats().Drunkenness > 10.0F) {
         ++this.sleepingPillsTaken;
      }

      this.lastPillsTaken = GameTime.instance.Calender.getTimeInMillis();
   }

   public boolean isOutside() {
      return this.getCurrentSquare() != null && this.getCurrentSquare().getRoom() == null && !this.isInARoom();
   }

   public double getLastSeenZomboidTime() {
      return this.lastSeenZombieTime;
   }

   public float getPlayerClothingTemperature() {
      float var1 = 0.0F;
      if (this.getClothingItem_Feet() != null) {
         var1 += ((Clothing)this.getClothingItem_Feet()).getTemperature();
      }

      if (this.getClothingItem_Hands() != null) {
         var1 += ((Clothing)this.getClothingItem_Hands()).getTemperature();
      }

      if (this.getClothingItem_Head() != null) {
         var1 += ((Clothing)this.getClothingItem_Head()).getTemperature();
      }

      if (this.getClothingItem_Legs() != null) {
         var1 += ((Clothing)this.getClothingItem_Legs()).getTemperature();
      }

      if (this.getClothingItem_Torso() != null) {
         var1 += ((Clothing)this.getClothingItem_Torso()).getTemperature();
      }

      return var1;
   }

   public float getPlayerClothingInsulation() {
      float var1 = 0.0F;
      if (this.getClothingItem_Feet() != null) {
         var1 += ((Clothing)this.getClothingItem_Feet()).getInsulation() * 0.1F;
      }

      if (this.getClothingItem_Hands() != null) {
         var1 += ((Clothing)this.getClothingItem_Hands()).getInsulation() * 0.0F;
      }

      if (this.getClothingItem_Head() != null) {
         var1 += ((Clothing)this.getClothingItem_Head()).getInsulation() * 0.0F;
      }

      if (this.getClothingItem_Legs() != null) {
         var1 += ((Clothing)this.getClothingItem_Legs()).getInsulation() * 0.3F;
      }

      if (this.getClothingItem_Torso() != null) {
         var1 += ((Clothing)this.getClothingItem_Torso()).getInsulation() * 0.6F;
      }

      return var1;
   }

   public boolean isTorchCone() {
      if (this.bRemote) {
         return this.mpTorchCone;
      } else if (this.leftHandItem != null && this.leftHandItem.isTorchCone()) {
         return true;
      } else {
         return this.rightHandItem != null && this.rightHandItem.isTorchCone();
      }
   }

   public float getLightDistance() {
      if (this.bRemote) {
         return this.mpTorchDist;
      } else if (this.leftHandItem != null && this.leftHandItem.getLightDistance() > 0) {
         return (float)this.leftHandItem.getLightDistance();
      } else {
         return this.rightHandItem != null && this.rightHandItem.getLightDistance() > 0 ? (float)this.rightHandItem.getLightDistance() : 0.0F;
      }
   }

   public boolean pressedMovement() {
      if (this.isBlockMovement()) {
         return false;
      } else if (this.PlayerIndex != 0 || !GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr))) {
         if (this.JoypadBind != -1) {
            float var1 = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
            float var2 = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
            float var3 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
            if (Math.abs(var1) > var3 || Math.abs(var2) > var3) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean pressedCancelAction() {
      if (this.PlayerIndex == 0 && GameKeyboard.isKeyDown(Core.getInstance().getKey("CancelAction"))) {
         return true;
      } else {
         return this.JoypadBind != -1 ? JoypadManager.instance.isBPressed(this.JoypadBind) : false;
      }
   }

   public boolean pressedAim() {
      if (this.PlayerIndex == 0) {
         if (this.isAimKeyDown()) {
            return true;
         }

         if (Mouse.isButtonDownUICheck(1)) {
            return true;
         }
      }

      if (this.JoypadBind != -1) {
         float var1 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
         float var2 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
         if (Math.abs(var1) > 0.1F || Math.abs(var2) > 0.1F) {
            return true;
         }
      }

      return false;
   }

   public static int getPlayerIndex() {
      return instance == null ? assumedPlayer : instance.PlayerIndex;
   }

   public void setSteamID(long var1) {
      this.steamID = var1;
   }

   public long getSteamID() {
      return this.steamID;
   }

   public static boolean allPlayersDead() {
      for(int var0 = 0; var0 < numPlayers; ++var0) {
         if (players[var0] != null && !players[var0].isDead()) {
            return false;
         }
      }

      if (IsoWorld.instance != null && !IsoWorld.instance.AddCoopPlayers.isEmpty()) {
         return false;
      } else {
         return true;
      }
   }

   public static ArrayList getPlayers() {
      return new ArrayList(Arrays.asList(players));
   }

   public boolean isTargetedByZombie() {
      return this.targetedByZombie;
   }

   public boolean isMaskClicked(int var1, int var2, boolean var3) {
      return this.sprite == null ? false : this.sprite.isMaskClicked(this.dir, var1, var2, var3);
   }

   public int getOffSetXUI() {
      return this.offSetXUI;
   }

   public void setOffSetXUI(int var1) {
      this.offSetXUI = var1;
   }

   public int getOffSetYUI() {
      return this.offSetYUI;
   }

   public void setOffSetYUI(int var1) {
      this.offSetYUI = var1;
   }

   public String getUsername() {
      return this.username;
   }

   public void updateUsername() {
      if (!GameClient.bClient && !GameServer.bServer) {
         this.username = this.getDescriptor().getForename() + this.getDescriptor().getSurname();
      }
   }

   public int getOnlineID() {
      return this.OnlineID;
   }

   public boolean isLocalPlayer() {
      for(int var1 = 0; var1 < numPlayers; ++var1) {
         if (players[var1] == this) {
            return true;
         }
      }

      return false;
   }

   public boolean isOnlyPlayerAsleep() {
      if (!this.isAsleep()) {
         return false;
      } else {
         for(int var1 = 0; var1 < numPlayers; ++var1) {
            if (players[var1] != null && !players[var1].isDead() && players[var1] != this && players[var1].isAsleep()) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean allPlayersAsleep() {
      int var0 = 0;
      int var1 = 0;

      for(int var2 = 0; var2 < numPlayers; ++var2) {
         if (players[var2] != null && !players[var2].isDead()) {
            ++var0;
            if (players[var2] != null && players[var2].isAsleep()) {
               ++var1;
            }
         }
      }

      return var0 > 0 && var0 == var1;
   }

   public void OnDeath() {
      super.OnDeath();
      if (!GameServer.bServer) {
         this.StopAllActionQueue();
         if (!GameClient.bClient || this.isLocalPlayer()) {
            this.dropHandItems();
         }

         if (this.isLocalPlayer()) {
            SoundManager.instance.PlaySound("PlayerDied", false, 0.8F);
         }

         if (allPlayersDead()) {
            SoundManager.instance.playMusic("OldMusic_tunedeath");
         }

         if (this.isLocalPlayer()) {
            LuaEventManager.triggerEvent("OnPlayerDeath", this);
         }

         if (this.isLocalPlayer() && this.getVehicle() != null) {
            this.getVehicle().exit(this);
         }

         this.removeSaveFile();
         if (this.shouldBecomeZombieAfterDeath()) {
            this.forceAwake();
         }

         this.getMoodles().Update();
         this.getCell().setDrag((KahluaTable)null, this.getPlayerNum());
      }
   }

   public boolean isNoClip() {
      return this.noClip;
   }

   public void setNoClip(boolean var1) {
      this.noClip = var1;
   }

   public static boolean getCoopPVP() {
      return CoopPVP;
   }

   public static void setCoopPVP(boolean var0) {
      CoopPVP = var0;
   }

   public void setAuthorizeMeleeAction(boolean var1) {
      this.authorizeMeleeAction = var1;
   }

   public boolean isBlockMovement() {
      if (!Core.getInstance().isSelectingAll()) {
         return this.blockMovement;
      } else {
         if (!GameKeyboard.isKeyDown(30) || !GameKeyboard.isKeyDown(29) && !GameKeyboard.isKeyDown(157)) {
            Core.getInstance().setIsSelectingAll(false);
         }

         return true;
      }
   }

   public void setBlockMovement(boolean var1) {
      this.blockMovement = var1;
   }

   public void startReceivingBodyDamageUpdates(IsoPlayer var1) {
      if (GameClient.bClient && var1 != null && var1 != this && this.isLocalPlayer() && !var1.isLocalPlayer()) {
         var1.resetBodyDamageRemote();
         BodyDamageSync.instance.startReceivingUpdates(var1.getOnlineID());
      }

   }

   public void stopReceivingBodyDamageUpdates(IsoPlayer var1) {
      if (GameClient.bClient && var1 != null && var1 != this && !var1.isLocalPlayer()) {
         BodyDamageSync.instance.stopReceivingUpdates(var1.getOnlineID());
      }

   }

   public Nutrition getNutrition() {
      return this.nutrition;
   }

   private void updateHeartSound() {
      GameSound var1 = GameSounds.getSound("HeartBeat");
      boolean var2 = var1 != null && var1.userVolume > 0.0F;
      if (!this.Asleep && var2 && GameTime.getInstance().getTrueMultiplier() == 1.0F) {
         this.heartDelay -= GameTime.getInstance().getMultiplier() / 1.6F;
         if (this.heartDelay <= 0.0F) {
            this.heartDelayMax = (float)((int)((1.0F - this.stats.Panic / 100.0F * 0.7F) * 25.0F) * 2);
            this.heartDelay = this.heartDelayMax;
            if (this.heartEventInstance == 0L) {
               this.heartEventInstance = this.getEmitter().playSoundImpl("heart", (IsoObject)null);
            }

            if (this.heartEventInstance != 0L) {
               this.getEmitter().setVolume(this.heartEventInstance, this.stats.Panic / 100.0F);
            }
         }
      }

   }

   private void updateHeavyBreathing() {
   }

   private void checkVehicleContainers() {
      ArrayList var1 = this.vehicleContainerData.tempContainers;
      var1.clear();
      int var2 = (int)this.getX() - 4;
      int var3 = (int)this.getY() - 4;
      int var4 = (int)this.getX() + 4;
      int var5 = (int)this.getY() + 4;
      int var6 = var2 / 10;
      int var7 = var3 / 10;
      int var8 = (int)Math.ceil((double)(var4 / 10));
      int var9 = (int)Math.ceil((double)(var5 / 10));

      int var10;
      for(var10 = var7; var10 < var9; ++var10) {
         for(int var11 = var6; var11 < var8; ++var11) {
            IsoChunk var12 = GameServer.bServer ? ServerMap.instance.getChunk(var11, var10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var11 * 10, var10 * 10, 0);
            if (var12 != null) {
               for(int var13 = 0; var13 < var12.vehicles.size(); ++var13) {
                  BaseVehicle var14 = (BaseVehicle)var12.vehicles.get(var13);
                  VehicleScript var15 = var14.getScript();
                  if (var15 != null) {
                     for(int var16 = 0; var16 < var15.getPartCount(); ++var16) {
                        VehicleScript.Part var17 = var15.getPart(var16);
                        if (var17.container != null && var17.area != null && var14.isInArea(var17.area, this)) {
                           IsoPlayer.VehicleContainer var18 = this.vehicleContainerData.freeContainers.isEmpty() ? new IsoPlayer.VehicleContainer() : (IsoPlayer.VehicleContainer)this.vehicleContainerData.freeContainers.pop();
                           var1.add(var18.set(var14, var16));
                        }
                     }
                  }
               }
            }
         }
      }

      if (var1.size() != this.vehicleContainerData.containers.size()) {
         this.vehicleContainerData.freeContainers.addAll(this.vehicleContainerData.containers);
         this.vehicleContainerData.containers.clear();
         this.vehicleContainerData.containers.addAll(var1);
         LuaEventManager.triggerEvent("OnContainerUpdate");
      } else {
         for(var10 = 0; var10 < var1.size(); ++var10) {
            IsoPlayer.VehicleContainer var19 = (IsoPlayer.VehicleContainer)var1.get(var10);
            IsoPlayer.VehicleContainer var20 = (IsoPlayer.VehicleContainer)this.vehicleContainerData.containers.get(var10);
            if (!var19.equals(var20)) {
               this.vehicleContainerData.freeContainers.addAll(this.vehicleContainerData.containers);
               this.vehicleContainerData.containers.clear();
               this.vehicleContainerData.containers.addAll(var1);
               LuaEventManager.triggerEvent("OnContainerUpdate");
               break;
            }
         }
      }

   }

   public void setJoypadIgnoreAimUntilCentered(boolean var1) {
      this.bJoypadIgnoreAimUntilCentered = var1;
   }

   public void addSmallInjuries() {
   }

   public boolean canSeePlayerStats() {
      return this.accessLevel != "";
   }

   public ByteBufferWriter createPlayerStats(ByteBufferWriter var1, String var2) {
      PacketTypes.doPacket((short)123, var1);
      var1.putInt(this.getOnlineID());
      var1.putUTF(var2);
      var1.putUTF(this.getDisplayName());
      var1.putUTF(this.getDescriptor().getForename());
      var1.putUTF(this.getDescriptor().getSurname());
      var1.putUTF(this.getDescriptor().getProfession());
      var1.putUTF(this.accessLevel);
      var1.putUTF(this.getTagPrefix());
      if (this.accessLevel.equals("")) {
         this.GhostMode = false;
         this.invisible = false;
         this.godMod = false;
      }

      var1.putBoolean(this.isAllChatMuted());
      var1.putFloat(this.getTagColor().r);
      var1.putFloat(this.getTagColor().g);
      var1.putFloat(this.getTagColor().b);
      var1.putByte((byte)(this.showTag ? 1 : 0));
      var1.putByte((byte)(this.factionPvp ? 1 : 0));
      return var1;
   }

   public String setPlayerStats(ByteBuffer var1, String var2) {
      String var3 = GameWindow.ReadString(var1);
      String var4 = GameWindow.ReadString(var1);
      String var5 = GameWindow.ReadString(var1);
      String var6 = GameWindow.ReadString(var1);
      String var7 = GameWindow.ReadString(var1);
      String var8 = GameWindow.ReadString(var1);
      boolean var9 = var1.get() == 1;
      float var10 = var1.getFloat();
      float var11 = var1.getFloat();
      float var12 = var1.getFloat();
      String var13 = "";
      this.setTagColor(new ColorInfo(var10, var11, var12, 1.0F));
      this.setTagPrefix(var8);
      this.showTag = var1.get() == 1;
      this.factionPvp = var1.get() == 1;
      if (!var4.equals(this.getDescriptor().getForename())) {
         if (GameServer.bServer) {
            var13 = var2 + " Changed " + var3 + " forname in " + var4;
         } else {
            var13 = "Changed your forname in " + var4;
         }
      }

      this.getDescriptor().setForename(var4);
      if (!var5.equals(this.getDescriptor().getSurname())) {
         if (GameServer.bServer) {
            var13 = var2 + " Changed " + var3 + " surname in " + var5;
         } else {
            var13 = "Changed your surname in " + var5;
         }
      }

      this.getDescriptor().setSurname(var5);
      if (!var6.equals(this.getDescriptor().getProfession())) {
         if (GameServer.bServer) {
            var13 = var2 + " Changed " + var3 + " profession in " + var6;
         } else {
            var13 = "Changed your profession in " + var6;
         }
      }

      this.getDescriptor().setProfession(var6);
      if (!this.accessLevel.equals(var7)) {
         if (GameServer.bServer) {
            (new StringBuilder()).append(var2).append(" Changed ").append(this.getDisplayName()).append(" access level in ").append(var7).toString();

            try {
               ServerWorldDatabase.instance.setAccessLevel(this.username, var7);
            } catch (SQLException var15) {
               var15.printStackTrace();
            }
         } else if (GameClient.bClient && GameClient.username.equals(this.username)) {
            GameClient.accessLevel = var7;
            GameClient.connection.accessLevel = var7;
         }

         if (var7.equals("")) {
            this.GhostMode = false;
            this.invisible = false;
            this.godMod = false;
         }

         var13 = "Changed access level in " + var7;
         this.accessLevel = var7;
      }

      if (!this.getDisplayName().equals(var3)) {
         if (GameServer.bServer) {
            var13 = var2 + " Changed display name " + this.getDisplayName() + " in " + var3;
            ServerWorldDatabase.instance.updateDisplayName(this.username, var3);
         } else {
            var13 = "Changed your display name in " + var3;
         }

         this.setDisplayName(var3);
      }

      if (var9 != this.isAllChatMuted()) {
         if (var9) {
            if (GameServer.bServer) {
               var13 = var2 + " Banned " + var3 + " from using /all chat";
            } else {
               var13 = "Banned you from using /all chat";
            }
         } else if (GameServer.bServer) {
            var13 = var2 + " Allowed " + var3 + " to use /all chat";
         } else {
            var13 = "Now allowed you to use /all chat";
         }
      }

      this.setAllChatMuted(var9);
      if (GameServer.bServer && !"".equals(var13)) {
         LoggerManager.getLogger("admin").write(var13);
      }

      if (GameClient.bClient) {
         LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
      }

      return var13;
   }

   public boolean isAllChatMuted() {
      return this.allChatMuted;
   }

   public void setAllChatMuted(boolean var1) {
      this.allChatMuted = var1;
   }

   public String getAccessLevel() {
      String var1 = this.accessLevel;
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -2004703995:
         if (var1.equals("moderator")) {
            var2 = 1;
         }
         break;
      case 3302:
         if (var1.equals("gm")) {
            var2 = 3;
         }
         break;
      case 92668751:
         if (var1.equals("admin")) {
            var2 = 0;
         }
         break;
      case 348607190:
         if (var1.equals("observer")) {
            var2 = 4;
         }
         break;
      case 530022739:
         if (var1.equals("overseer")) {
            var2 = 2;
         }
      }

      switch(var2) {
      case 0:
         return "Admin";
      case 1:
         return "Moderator";
      case 2:
         return "Overseer";
      case 3:
         return "GM";
      case 4:
         return "Observer";
      default:
         return "None";
      }
   }

   public void setUsername(String var1) {
      this.username = var1;
   }

   public void setAccessLevel(String var1) {
      this.accessLevel = var1;
   }

   public String getTagPrefix() {
      return this.tagPrefix;
   }

   public void setTagPrefix(String var1) {
      this.tagPrefix = var1;
   }

   public ColorInfo getTagColor() {
      return this.tagColor;
   }

   public void setTagColor(ColorInfo var1) {
      this.tagColor = var1;
   }

   public Integer getTransactionID() {
      return this.transactionID;
   }

   public void setTransactionID(Integer var1) {
      this.transactionID = var1;
   }

   public String getDisplayName() {
      if (GameClient.bClient) {
         if (this.displayName == null || this.displayName.equals("")) {
            this.displayName = this.username;
         }
      } else if (!GameServer.bServer) {
         this.displayName = this.username;
      }

      return this.displayName;
   }

   public void setDisplayName(String var1) {
      this.displayName = var1;
   }

   public boolean isSeeNonPvpZone() {
      return this.seeNonPvpZone;
   }

   public void setSeeNonPvpZone(boolean var1) {
      this.seeNonPvpZone = var1;
   }

   public boolean isShowTag() {
      return this.showTag;
   }

   public boolean isFactionPvp() {
      return this.factionPvp;
   }

   public void setShowTag(boolean var1) {
      this.showTag = var1;
   }

   public void setFactionPvp(boolean var1) {
      this.factionPvp = var1;
   }

   public boolean isForceRun() {
      return this.forceRun;
   }

   public void setForceRun(boolean var1) {
      this.forceRun = var1;
   }

   public boolean isDeaf() {
      return this.getTraits().contains("Deaf");
   }

   public boolean isForceOverrideAnim() {
      return this.forceOverrideAnim;
   }

   public void setForceOverrideAnim(boolean var1) {
      this.forceOverrideAnim = var1;
   }

   public Long getMechanicsItem(String var1) {
      return (Long)this.mechanicsItem.get(Long.parseLong(var1));
   }

   public void addMechanicsItem(String var1, VehiclePart var2, Long var3) {
      byte var4 = 1;
      byte var5 = 1;
      if (this.mechanicsItem.get(Long.parseLong(var1)) == null) {
         if (var2.getTable("uninstall") != null && var2.getTable("uninstall").rawget("skills") != null) {
            String[] var6 = ((String)var2.getTable("uninstall").rawget("skills")).split(";");
            String[] var7 = var6;
            int var8 = var6.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String var10 = var7[var9];
               if (var10.contains("Mechanics")) {
                  int var11 = Integer.parseInt(var10.split(":")[1]);
                  if (var11 >= 6) {
                     var4 = 3;
                     var5 = 7;
                  } else if (var11 >= 4) {
                     var4 = 3;
                     var5 = 5;
                  } else if (var11 >= 2) {
                     var4 = 2;
                     var5 = 4;
                  } else if (Rand.Next(3) == 0) {
                     var4 = 2;
                     var5 = 2;
                  }
               }
            }
         }

         this.getXp().AddXP(PerkFactory.Perks.Mechanics, (float)Rand.Next(var4, var5));
      }

      this.mechanicsItem.put(Long.parseLong(var1), var3);
   }

   public void setPosition(float var1, float var2, float var3) {
      this.setX(var1);
      this.setY(var2);
      this.setZ(var3);
   }

   public boolean isWearingNightVisionGoggles() {
      return this.isWearingNightVisionGoggles;
   }

   public void setWearingNightVisionGoggles(boolean var1) {
      this.isWearingNightVisionGoggles = var1;
   }

   private void updateTemperatureCheck() {
      int var1 = this.Moodles.getMoodleLevel(MoodleType.Hypothermia);
      if (this.hypothermiaCache == -1 || this.hypothermiaCache != var1) {
         if (var1 >= 3 && var1 > this.hypothermiaCache && this.isAsleep() && !this.ForceWakeUp) {
            this.forceAwake();
         }

         this.hypothermiaCache = var1;
      }

      int var2 = this.Moodles.getMoodleLevel(MoodleType.Hyperthermia);
      if (this.hyperthermiaCache == -1 || this.hyperthermiaCache != var2) {
         if (var2 >= 3 && var2 > this.hyperthermiaCache && this.isAsleep() && !this.ForceWakeUp) {
            this.forceAwake();
         }

         this.hyperthermiaCache = var2;
      }

   }

   private static class VehicleContainerData {
      ArrayList tempContainers;
      ArrayList containers;
      Stack freeContainers;

      private VehicleContainerData() {
         this.tempContainers = new ArrayList();
         this.containers = new ArrayList();
         this.freeContainers = new Stack();
      }

      // $FF: synthetic method
      VehicleContainerData(Object var1) {
         this();
      }
   }

   private static class VehicleContainer {
      BaseVehicle vehicle;
      int containerIndex;

      private VehicleContainer() {
      }

      public IsoPlayer.VehicleContainer set(BaseVehicle var1, int var2) {
         this.vehicle = var1;
         this.containerIndex = var2;
         return this;
      }

      public boolean equals(Object var1) {
         return var1 instanceof IsoPlayer.VehicleContainer && this.vehicle == ((IsoPlayer.VehicleContainer)var1).vehicle && this.containerIndex == ((IsoPlayer.VehicleContainer)var1).containerIndex;
      }

      // $FF: synthetic method
      VehicleContainer(Object var1) {
         this();
      }
   }
}
