package zombie.characters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.AmbientStreamManager;
import zombie.FrameLoader;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.ai.State;
import zombie.ai.StateMachine;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathFinderResult;
import zombie.ai.astar.Path;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.ai.states.AttackState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.IdleState;
import zombie.ai.states.JustDieState;
import zombie.ai.states.LungeState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.SatChairState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.SequenceBehavior;
import zombie.behaviors.survivor.MasterSurvivorBehavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.chat.ChatElementOwner;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.bucket.BucketManager;
import zombie.core.logger.LoggerManager;
import zombie.core.physics.Transform;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.interfaces.IUpdater;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Literature;
import zombie.inventory.types.Radio;
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
import zombie.iso.LightingThread;
import zombie.iso.LosUtil;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatServer;
import zombie.network.chat.ChatType;
import zombie.profanity.ProfanityFilter;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.ActionProgressBar;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.AttackVehicleState;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;

public class IsoGameCharacter extends IsoMovingObject implements Talker, ChatElementOwner {
   public BaseCharacterSoundEmitter emitter;
   protected static HashMap SurvivorMap = new HashMap();
   public byte NetRemoteState = 0;
   public boolean godMod = false;
   public boolean invisible = false;
   public boolean avoidDamage = false;
   public boolean callOut = false;
   private int age = 25;
   public static IsoSprite DropShadow;
   private int lastHitCount = 0;
   private boolean isSat = false;
   private IsoObject chair = null;
   private boolean safety = true;
   private float safetyCooldown = 0.0F;
   private float RecoilDelay = 0.0F;
   protected float RecoilDelayDecrease = 0.625F;
   private float BeenMovingFor = 0.0F;
   protected float BeenMovingForIncrease = 1.25F;
   protected float BeenMovingForDecrease = 0.625F;
   UIElement ui = new UIElement();
   private boolean forceShove = false;
   private String clickSound = null;
   private static Vector2 tempVector2_1 = new Vector2();
   private static Vector2 tempVector2_2 = new Vector2();
   private float reduceInfectionPower = 0.0F;
   public IsoGameCharacter ReanimatedCorpse;
   public int ReanimatedCorpseID = -1;
   private List knownRecipes = new ArrayList();
   public static int RENDER_OFFSET_X = 1;
   public static int RENDER_OFFSET_Y = -89;
   public HashMap StateMachineParams = new HashMap();
   private int lastHourSleeped = 0;
   private float timeOfSleep = 0.0F;
   private float delayToActuallySleep = 0.0F;
   private String bedType = "averageBed";
   private IsoObject bed = null;
   private boolean isReading = false;
   private float timeSinceLastSmoke = 0.0F;
   private boolean wasOnStairs = false;
   public long clientIgnoreCollision = 0L;
   private ChatMessage lastChatMessage;
   private boolean unlimitedCarry = false;
   private boolean buildCheat = false;
   private boolean healthCheat = false;
   private boolean mechanicsCheat = false;
   private boolean showAdminTag = true;
   private long isAnimForecasted = 0L;
   protected IsoGameCharacter FollowingTarget = null;
   protected ArrayList LocalList = new ArrayList();
   protected ArrayList LocalNeutralList = new ArrayList();
   protected ArrayList LocalGroupList = new ArrayList();
   protected ArrayList LocalRelevantEnemyList = new ArrayList();
   protected float dangerLevels = 0.0F;
   public boolean upKillCount = true;
   protected Stack MeetList = new Stack();
   protected Order Order = null;
   protected Stack Orders = new Stack();
   protected InventoryItem ClothingItem_Back;
   public ArrayList PerkList = new ArrayList();
   protected Order PersonalNeed = null;
   protected Stack PersonalNeeds = new Stack();
   protected float leaveBodyTimedown = 0.0F;
   protected boolean AllowConversation = true;
   protected int ReanimPhase;
   protected float ReanimateTimer;
   protected int ReanimAnimFrame;
   protected int ReanimAnimDelay;
   protected boolean Reanim = false;
   protected boolean VisibleToNPCs = true;
   protected int DieCount = 0;
   protected float llx = 0.0F;
   protected float lly = 0.0F;
   protected float llz = 0.0F;
   protected int RemoteID = -1;
   protected int NumSurvivorsInVicinity = 0;
   protected float LevelUpMultiplier = 2.5F;
   protected static int[] LevelUpLevels = new int[]{25, 75, 150, 225, 300, 400, 500, 600, 700, 800, 900, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 3200, 3400, 3600, 3800, 4000, 4400, 4800, 5200, 5600, 6000};
   protected IsoGameCharacter.XP xp = null;
   protected int NumberOfPerksToPick = 0;
   protected ArrayList CanUpgradePerk = new ArrayList();
   protected int LastLocalEnemies = 0;
   protected ArrayList VeryCloseEnemyList = new ArrayList();
   protected HashMap LastKnownLocation = new HashMap();
   protected IsoGameCharacter AttackedBy = null;
   protected boolean IgnoreStaggerBack = false;
   protected boolean AttackWasSuperAttack = false;
   protected int TimeThumping = 0;
   protected int PatienceMax = 150;
   protected int PatienceMin = 20;
   protected int Patience = 20;
   protected Stack CharacterActions = new Stack();
   public Vector2 angle = new Vector2();
   public boolean Asleep = false;
   protected float AttackDelay = 0.0F;
   protected float AttackDelayMax = 0.0F;
   public float AttackDelayUse = 0.0F;
   public float AttackDelayLast = 0.0F;
   protected int ZombieKills = 0;
   protected int SurvivorKills = 0;
   protected int LastZombieKills = 0;
   protected boolean superAttack = false;
   protected float ForceWakeUpTime = -1.0F;
   protected boolean ForceWakeUp;
   protected BodyDamage BodyDamage = null;
   protected BodyDamage BodyDamageRemote = null;
   protected InventoryItem craftIngredient1;
   protected InventoryItem craftIngredient2;
   protected InventoryItem craftIngredient3;
   protected InventoryItem craftIngredient4;
   protected State defaultState;
   protected SurvivorDesc descriptor;
   protected Stack FamiliarBuildings = new Stack();
   protected AStarPathFinderResult finder = new AStarPathFinderResult();
   protected float FireKillRate = 0.0038F;
   protected int FireSpreadProbability = 6;
   protected float footStepCounter = 0.0F;
   protected float footStepCounterMax = 10.0F;
   protected float Health = 1.0F;
   protected MasterSurvivorBehavior masterProper = null;
   protected IsoGameCharacter hitBy;
   protected String hurtSound = "MaleZombieHurt";
   public boolean IgnoreMovementForDirection = false;
   protected ItemContainer inventory = new ItemContainer();
   protected final ArrayList savedInventoryItems = new ArrayList();
   protected IsoDirections lastdir;
   protected InventoryItem leftHandItem;
   protected InventoryItem ClothingItem_Head;
   protected InventoryItem ClothingItem_Torso;
   protected InventoryItem ClothingItem_Hands;
   protected InventoryItem ClothingItem_Legs;
   protected InventoryItem ClothingItem_Feet;
   protected DecisionPath decisionPath = new DecisionPath();
   protected SequenceBehavior masterBehaviorList = new SequenceBehavior();
   protected int NextWander = 200;
   protected boolean OnFire = false;
   protected Path path;
   protected int pathIndex = 0;
   protected float PathSpeed = 0.03F;
   protected SurvivorPersonality Personality = null;
   protected InventoryItem rightHandItem;
   protected String sayLine;
   protected Color SpeakColour = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   protected float slowFactor = 0.0F;
   protected float slowTimer = 0.0F;
   protected boolean bUseParts = false;
   protected boolean Speaking = false;
   protected float SpeakTime = 0.0F;
   public float speedMod = 1.0F;
   protected float staggerTimeMod = 1.0F;
   protected StateMachine stateMachine;
   protected Moodles Moodles;
   protected Stats stats = new Stats();
   protected Stack TagGroup = new Stack();
   protected Stack UsedItemsOn = new Stack();
   protected HandWeapon useHandWeapon = null;
   public IsoSprite torsoSprite;
   public IsoSprite legsSprite;
   public IsoSprite headSprite;
   public IsoSprite shoeSprite;
   public IsoSprite topSprite;
   public IsoSprite hairSprite;
   public ArrayList extraSprites = new ArrayList(0);
   public IsoSprite bottomsSprite;
   protected Stack wounds = new Stack();
   protected IsoGridSquare attackTargetSquare;
   protected float BloodImpactX = 0.0F;
   protected float BloodImpactY = 0.0F;
   protected float BloodImpactZ = 0.0F;
   protected IsoSprite bloodSplat;
   protected boolean bOnBed = false;
   protected Vector2 moveForwardVec = new Vector2();
   protected boolean pathing = false;
   protected ChatElement chatElement;
   protected Stack LocalEnemyList = new Stack();
   protected Stack EnemyList = new Stack();
   protected ArrayList Traits = new ArrayList(1);
   private Texture pvpTexture;
   protected Integer maxWeight = 8;
   protected int maxWeightBase = 8;
   protected float SleepingTabletDelta = 1.0F;
   protected float BetaEffect = 0.0F;
   protected float DepressEffect = 0.0F;
   protected float SleepingTabletEffect = 0.0F;
   protected float BetaDelta = 0.0F;
   protected float DepressDelta = 0.0F;
   protected float DepressFirstTakeTime = -1.0F;
   protected float PainEffect = 0.0F;
   protected float PainDelta = 0.0F;
   static String szRun = "Run";
   static String szRun_Weapon2 = "Run_Weapon2";
   static String szIdle_Weapon2 = "Idle_Weapon2";
   static String szWalk = "Walk";
   static String szWalk_R = "Walk_R";
   static String szStrafe_Aim_Bat = "Strafe_Aim_Bat";
   static String szStrafe_Aim_Handgun = "Strafe_Aim_Handgun";
   static String szStrafe_Aim_Rifle = "Strafe_Aim_Rifle";
   static String szStrafe_Aim_Stab = "Strafe_Aim_Stab";
   static String szAttack_Jaw_Stab = "Attack_Jaw_Stab";
   static String szStrafe = "Strafe";
   static String szStrafe_R = "Strafe_R";
   static String szWalk_Aim_Stab = "Walk_Aim_Stab";
   static String szWalk_Aim_Bat = "Walk_Aim_Bat";
   static String szWalk_Aim_Handgun = "Walk_Aim_Handgun";
   static String szWalk_Aim_Rifle = "Walk_Aim_Rifle";
   static String szWalk_Aim_Rifle_R = "Walk_Aim_Rifle_R";
   static String szWalk_Aim_Stab_R = "Walk_Aim_Stab_R";
   static String szStrafe_Aim_Rifle_R = "Strafe_Aim_Rifle_R";
   static String szStrafe_Aim_Bat_R = "Strafe_Aim_Bat_R";
   static String szWalk_Aim_Bat_R = "Walk_Aim_Bat_R";
   static String szWalk_Aim_Handgun_R = "Walk_Aim_Handgun_R";
   static String szStrafe_Aim_Handgun_R = "Strafe_Aim_Handgun_R";
   static String szStrafe_Aim_Stab_R = "Strafe_Aim_Stab_R";
   static String szZombieDeath = "ZombieDeath";
   static String szAttack_Bat = "Attack_Bat";
   static String szAttack_Sledgehammer = "Attack_Sledgehammer";
   static String szAttack_Handgun = "Attack_Handgun";
   static String szAttack_Rifle = "Attack_Rifle";
   static String szAttack_Stab = "Attack_Stab";
   static String szAttack_Shove = "Attack_Shove";
   static String szAttack_Floor_Bat = "Attack_Floor_Bat";
   static String szAttack_Floor_Rifle = "Attack_Floor_Rifle";
   static String szAttack_Floor_Handgun = "Attack_Floor_Handgun";
   static String szAttack_Floor_Stab = "Attack_Floor_Stab";
   static String szAttack_Floor_Stamp = "Attack_Floor_Stamp";
   static String szClimb_WindowA = "Climb_WindowA";
   static String szClimb_WindowB = "Climb_WindowB";
   static String szWindowOpenIn = "WindowOpenIn";
   static String szWindowOpenStruggle = "WindowOpenStruggle";
   static String szWindowOpenSuccess = "WindowOpenSuccess";
   static String szWindowSmash = "WindowSmash";
   static String szSatChairIn = "SatChairIn";
   static String szSatChairOut = "SatChairOut";
   static String szSatChairIdle = "SatChairIdle";
   static String szClimb_Rope = "Climb_Rope";
   static String szClimbDown_Rope = "ClimbDown_Rope";
   static String szZombieGetUp = "ZombieGetUp";
   static String szIdle = "Idle";
   public boolean bFemale = true;
   public float knockbackAttackMod = 1.0F;
   static String[] footsteps = new String[]{"foottile", "foottile", "foottile", "foottile", "foottileecho", "foottileecho", "foottileecho", "foottileecho"};
   protected static Vector2 tempo = new Vector2();
   static Vector2 tempo2 = new Vector2();
   public boolean[] SpottedSinceAlphaZero = new boolean[4];
   protected static ColorInfo inf = new ColorInfo();
   protected static final ColorInfo tempColorInfo = new ColorInfo();
   protected boolean bDoDefer = true;
   protected float haloDispTime = 128.0F;
   private int sleepSpeechCnt = 0;
   private static String sleepText = null;
   protected TextDrawObject userName;
   protected TextDrawObject haloNote;
   protected HashMap namesPrefix = new HashMap();
   protected String namePvpSuffix = " [img=media/ui/Skull.png]";
   protected String nameCarKeySuffix = " [img=media/ui/CarKey.png";
   protected String voiceSuffix = "[img=media/ui/voiceon.png] ";
   protected String voiceMuteSuffix = "[img=media/ui/voicemuted.png] ";
   protected IsoPlayer isoPlayer = null;
   protected boolean hasInitTextObjects = false;
   protected int lineDisplayTime = 314;
   protected boolean hasChatHistory = false;
   protected boolean canSeeCurrent = false;
   protected boolean drawUserName = false;
   private Radio equipedRadio;
   private InventoryItem leftHandCache;
   private InventoryItem rightHandCache;
   private static ArrayList HitList = new ArrayList();
   protected IsoGameCharacter.Location LastHeardSound = new IsoGameCharacter.Location(-1, -1, -1);
   protected float lrx = 0.0F;
   protected float lry = 0.0F;
   protected boolean bClimbing = false;
   protected boolean lastCollidedW = false;
   protected boolean lastCollidedN = false;
   protected static OnceEvery testPlayerSpotInDarkness = new OnceEvery(0.15F, true);
   protected int[] timeTillForgetLocation = new int[4];
   protected int fallTime = 0;
   protected float lastFallSpeed = 0.0F;
   protected boolean bFalling = false;
   float remoteMoveX;
   float remoteMoveY;
   private ArrayList ReadBooks = new ArrayList();
   private IsoGameCharacter.LightInfo lightInfo = new IsoGameCharacter.LightInfo();
   private IsoGameCharacter.LightInfo lightInfo2 = new IsoGameCharacter.LightInfo();
   protected BaseVehicle vehicle = null;
   public float savedVehicleX;
   public float savedVehicleY;
   public short savedVehicleSeat = -1;
   public boolean savedVehicleRunning;
   private PolygonalMap2.Path path2;
   private PathFindBehavior2 pfb2 = new PathFindBehavior2(this);

   public BaseCharacterSoundEmitter getEmitter() {
      return this.emitter;
   }

   public void updateEmitter() {
      if (IsoWorld.instance.emitterUpdate || this.emitter.hasSoundsToStart()) {
         this.emitter.set(this.x, this.y, this.z);
         this.emitter.tick();
      }
   }

   public String getTalkerType() {
      return this.chatElement.getTalkerType();
   }

   public void setAnimForecasted(int var1) {
      this.isAnimForecasted = System.currentTimeMillis() + (long)var1;
   }

   public boolean isAnimForecasted() {
      return System.currentTimeMillis() < this.isAnimForecasted;
   }

   public void resetModel() {
      ModelManager.instance.Reset(this);
   }

   private void resetModelNextFrame() {
      ModelManager.instance.ResetNextFrame(this);
   }

   public void setModel(String var1) {
      try {
         if (var1 == null) {
            ModelManager.instance.Remove(this);
         } else if (!ModelManager.instance.Contains.contains(this)) {
            ModelManager.instance.Add(this);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
         ModelManager.instance.Remove(this);
         this.legsSprite.modelSlot = null;
      }

   }

   public ModelInstance getModel() {
      return this.legsSprite != null && this.legsSprite.modelSlot != null ? this.legsSprite.modelSlot.model : null;
   }

   public boolean hasActiveModel() {
      return this.legsSprite != null && this.legsSprite.hasActiveModel();
   }

   public boolean hasItems(String var1, int var2) {
      int var3 = this.inventory.getItemCount(var1);
      return var2 <= var3;
   }

   public static HashMap getSurvivorMap() {
      return SurvivorMap;
   }

   public static void setSurvivorMap(HashMap var0) {
      SurvivorMap = var0;
   }

   public static int[] getLevelUpLevels() {
      return LevelUpLevels;
   }

   public int getLevelUpLevels(int var1) {
      return LevelUpLevels.length <= var1 ? LevelUpLevels[LevelUpLevels.length - 1] : LevelUpLevels[var1];
   }

   public int getLevelMaxForXp() {
      return LevelUpLevels.length;
   }

   public int getXpForLevel(int var1) {
      return var1 < LevelUpLevels.length ? (int)((float)LevelUpLevels[var1] * this.LevelUpMultiplier) : (int)((float)(LevelUpLevels[LevelUpLevels.length - 1] + (var1 - LevelUpLevels.length + 1) * 400) * this.LevelUpMultiplier);
   }

   public static void setLevelUpLevels(int[] var0) {
      LevelUpLevels = var0;
   }

   public static Vector2 getTempo() {
      return tempo;
   }

   public static void setTempo(Vector2 var0) {
      tempo = var0;
   }

   public static ColorInfo getInf() {
      return inf;
   }

   public static void setInf(ColorInfo var0) {
      inf = var0;
   }

   public static OnceEvery getTestPlayerSpotInDarkness() {
      return testPlayerSpotInDarkness;
   }

   public static void setTestPlayerSpotInDarkness(OnceEvery var0) {
      testPlayerSpotInDarkness = var0;
   }

   public void DoDeath(HandWeapon var1, IsoGameCharacter var2) {
      this.DoDeath(var1, var2, true);
   }

   public void DoDeath(HandWeapon var1, IsoGameCharacter var2, boolean var3) {
      this.OnDeath();
      if (this.getHitBy() instanceof IsoPlayer && GameServer.bServer && this instanceof IsoPlayer) {
         String var4 = "";
         String var5 = "";
         if (SteamUtils.isSteamModeEnabled()) {
            var4 = " (" + ((IsoPlayer)this.getHitBy()).getSteamID() + ") ";
            var5 = " (" + ((IsoPlayer)this).getSteamID() + ") ";
         }

         LoggerManager.getLogger("pvp").write("user " + ((IsoPlayer)this.getHitBy()).username + var4 + " killed " + ((IsoPlayer)this).username + var5 + " " + LoggerManager.getPlayerCoords((IsoPlayer)this), "IMPORTANT");
         if (ServerOptions.instance.AnnounceDeath.getValue()) {
            ChatServer.getInstance().sendMessageToServerChat(((IsoPlayer)this.getHitBy()).username + " killed " + ((IsoPlayer)this).username + ".");
         }

         ChatServer.getInstance().sendMessageToAdminChat("user " + ((IsoPlayer)this.getHitBy()).username + " killed " + ((IsoPlayer)this).username);
      } else {
         if (GameServer.bServer && this instanceof IsoPlayer) {
            LoggerManager.getLogger("user").write("user " + ((IsoPlayer)this).username + " died at " + LoggerManager.getPlayerCoords((IsoPlayer)this) + " (non pvp)");
         }

         if (ServerOptions.instance.AnnounceDeath.getValue() && this instanceof IsoPlayer && GameServer.bServer) {
            ChatServer.getInstance().sendMessageToServerChat(((IsoPlayer)this).username + " is dead.");
         }
      }

      if (this.Health <= 0.0F) {
         float var9 = 0.6F;
         if (this instanceof IsoZombie && ((IsoZombie)this).bCrawling || this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieDeath".equals(this.legsSprite.CurrentAnim.name)) {
            var9 = 0.3F;
         }

         if (GameServer.bServer) {
            boolean var10 = this.isOnFloor() && var2 instanceof IsoPlayer && var1 != null && "BareHands".equals(var1.getType());
            GameServer.sendBloodSplatter(var1, this.getX(), this.getY(), this.getZ() + var9, this.getHitDir(), this.isCloseKilled(), var10);
         }

         if (this.bUseParts && var1 != null && var1.getType().equals("Shotgun")) {
            this.headSprite = null;
         }

         int var6;
         int var11;
         if (var1 != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
            var11 = var1.getSplatNumber();
            if (var11 < 1) {
               var11 = 1;
            }

            if (Core.bLastStand) {
               var11 *= 3;
            }

            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 2:
               var11 /= 2;
            case 3:
            default:
               break;
            case 4:
               var11 *= 2;
               break;
            case 5:
               var11 *= 5;
            }

            for(var6 = 0; var6 < var11; ++var6) {
               this.splatBlood(3, 0.3F);
            }
         }

         if (var1 != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
            this.splatBloodFloorBig(0.3F);
         }

         if (var2 != null && var2.xp != null) {
            var2.xp.AddXP(var1, 3);
         }

         if (SandboxOptions.instance.BloodLevel.getValue() > 1 && this.isOnFloor() && var2 instanceof IsoPlayer && var1 == ((IsoPlayer)var2).bareHands) {
            for(var11 = -1; var11 <= 1; ++var11) {
               for(var6 = -1; var6 <= 1; ++var6) {
                  if (var11 != 0 || var6 != 0) {
                     new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, (float)var11 * Rand.Next(0.25F, 0.5F), (float)var6 * Rand.Next(0.25F, 0.5F));
                  }
               }
            }

            new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
         } else if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
            tempo.x = this.getHitDir().x;
            tempo.y = this.getHitDir().y;
            if (Core.getInstance().getGameMode().equals("Tutorial")) {
               for(var11 = 0; var11 < 4; ++var11) {
                  for(var6 = -2; var6 <= 2; ++var6) {
                     for(int var7 = -2; var7 <= 2; ++var7) {
                        if (var6 != 0 || var7 != 0) {
                           new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, (float)var6 * Rand.Next(0.25F, 0.7F), (float)var7 * Rand.Next(0.25F, 0.7F));
                        }
                     }
                  }
               }
            }

            byte var14 = 3;
            byte var12 = 0;
            byte var13 = 1;
            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 1:
               var13 = 0;
               break;
            case 2:
               var13 = 1;
               var14 = 5;
               var12 = 2;
            case 3:
            default:
               break;
            case 4:
               var13 = 3;
               var14 = 2;
               break;
            case 5:
               var13 = 10;
               var14 = 0;
            }

            for(int var8 = 0; var8 < var13; ++var8) {
               if (Rand.Next(this.isCloseKilled() ? 8 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 1.8F, this.getHitDir().y * 1.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 1.9F, this.getHitDir().y * 1.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 3.5F, this.getHitDir().y * 3.5F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + var9, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
               }
            }
         }
      }

      if (this.def != null && this.legsSprite != null && (this.legsSprite.CurrentAnim == null || !this.isOnFloor() || this.legsSprite.CurrentAnim != null && ("ZombieGetUp".equals(this.legsSprite.CurrentAnim.name) || "ZombieDeadToCrawl".equals(this.legsSprite.CurrentAnim.name) && this.def.Frame > 7.0F))) {
         this.stateMachine.changeState(StaggerBackDieState.instance());
      } else if (this.Health <= 0.0F && this.stateMachine.getCurrent() != StaggerBackDieState.instance()) {
         this.stateMachine.Lock = false;
         this.stateMachine.changeState(JustDieState.instance());
      } else if (this.Health <= 0.0F) {
         this.playDeadSound();
      }

      this.stateMachine.Lock = true;
   }

   private void TestIfSeen(int var1) {
      IsoPlayer var2 = IsoPlayer.players[var1];
      if (var2 != null && this != var2 && !GameServer.bServer) {
         float var3 = this.DistTo(var2);
         if (!(var3 > GameTime.getInstance().getViewDist())) {
            float var4 = (this.getCurrentSquare().lighting[var1].lightInfo().r + this.getCurrentSquare().lighting[var1].lightInfo().g + this.getCurrentSquare().lighting[var1].lightInfo().b) / 3.0F;
            if (var4 > 0.4F) {
               var4 = 1.0F;
            }

            float var5 = 1.0F - var3 / GameTime.getInstance().getViewDist();
            if (var4 == 1.0F && var5 > 0.3F) {
               var5 = 1.0F;
            }

            tempo.x = this.getX();
            tempo.y = this.getY();
            Vector2 var10000 = tempo;
            var10000.x -= var2.getX();
            var10000 = tempo;
            var10000.y -= var2.getY();
            Vector2 var6 = var2.getVectorFromDirection(tempo2);
            tempo.normalize();
            float var7 = var6.dot(tempo);
            if (var7 < 0.5F) {
               var7 = 0.5F;
            }

            var4 *= var7;
            if (var4 < 0.0F) {
               var4 = 0.0F;
            }

            if (var3 <= 1.0F) {
               var5 = 1.0F;
               var4 *= 2.0F;
            }

            var4 *= var5;
            var4 *= 100.0F;
            if ((float)Rand.Next(Rand.AdjustForFramerate(100)) < var4 || !(this instanceof IsoGameCharacter)) {
               this.SpottedSinceAlphaZero[var1] = true;
               this.timeTillForgetLocation[var1] = 600;
            }

         }
      }
   }

   private void DoLand() {
      float var1 = GameServer.bServer ? 10.0F : (float)PerformanceSettings.LockFPS;
      this.fallTime = (int)((float)this.fallTime * (30.0F / var1));
      if (this.fallTime >= 20 && !this.isClimbing()) {
         if (this instanceof IsoPlayer) {
            if (GameServer.bServer) {
               return;
            }

            if (GameClient.bClient && ((IsoPlayer)this).bRemote) {
               return;
            }

            if (((IsoPlayer)this).isGhostMode()) {
               return;
            }
         }

         if (this instanceof IsoZombie) {
            if (this.fallTime > 50) {
               this.hitDir.x = this.hitDir.y = 0.0F;
               if (!((IsoZombie)this).bCrawling) {
                  this.changeState(StaggerBackDieState.instance());
                  this.PlayAnimUnlooped("ZombieDeath");
               }

               this.getEmitter().playVocals(this.hurtSound);
               this.Health -= 0.075F * (float)this.fallTime / 50.0F;
            }

         } else {
            boolean var2 = Rand.Next(80) == 0;
            float var3 = (float)this.fallTime * 1.5F;
            var3 *= this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight();
            if (this.getCurrentSquare().getFloor() != null && this.getCurrentSquare().getFloor().getSprite().getName() != null && this.getCurrentSquare().getFloor().getSprite().getName().startsWith("blends_natural")) {
               var3 *= 0.8F;
               if (!var2) {
                  var2 = Rand.Next(65) == 0;
               }
            }

            if (!var2) {
               if (this.HasTrait("Obese") || this.HasTrait("Emaciated")) {
                  var3 *= 1.4F;
               }

               if (this.HasTrait("Overweight") || this.HasTrait("Very Underweight")) {
                  var3 *= 1.2F;
               }

               if (this.getPerkLevel(PerkFactory.Perks.Fitness) > 4) {
                  var3 *= (float)(this.getPerkLevel(PerkFactory.Perks.Fitness) - 4) * 0.1F;
               }

               if (this.fallTime > 135) {
                  var3 = 1000.0F;
               }

               this.BodyDamage.ReduceGeneralHealth(var3);
               if (this.fallTime > 70) {
                  int var4 = 100 - (int)((double)this.fallTime * 0.6D);
                  if (this.getInventory().getMaxWeight() - this.getInventory().getCapacityWeight() < 2.0F) {
                     var4 = (int)((float)var4 - this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight() * 100.0F / 5.0F);
                  }

                  if (this.HasTrait("Obese") || this.HasTrait("Emaciated")) {
                     var4 -= 20;
                  }

                  if (this.HasTrait("Overweight") || this.HasTrait("Very Underweight")) {
                     var4 -= 10;
                  }

                  if (this.getPerkLevel(PerkFactory.Perks.Fitness) > 4) {
                     var4 += (this.getPerkLevel(PerkFactory.Perks.Fitness) - 4) * 3;
                  }

                  if (Rand.Next(100) >= var4) {
                     if (!SandboxOptions.instance.BoneFracture.getValue()) {
                        return;
                     }

                     float var5 = (float)Rand.Next(50, 80);
                     if (this.HasTrait("FastHealer")) {
                        var5 = (float)Rand.Next(30, 50);
                     } else if (this.HasTrait("SlowHealer")) {
                        var5 = (float)Rand.Next(80, 150);
                     }

                     switch(SandboxOptions.instance.InjurySeverity.getValue()) {
                     case 1:
                        var5 *= 0.5F;
                        break;
                     case 3:
                        var5 *= 1.5F;
                     }

                     this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1))).setFractureTime(var5);
                  } else if (Rand.Next(100) >= var4 - 10) {
                     this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1))).generateDeepWound();
                  }
               }

            }
         }
      }
   }

   public IsoGameCharacter getFollowingTarget() {
      return this.FollowingTarget;
   }

   public void setFollowingTarget(IsoGameCharacter var1) {
      this.FollowingTarget = var1;
   }

   public ArrayList getLocalList() {
      return this.LocalList;
   }

   public void setLocalList(ArrayList var1) {
      this.LocalList = var1;
   }

   public ArrayList getLocalNeutralList() {
      return this.LocalNeutralList;
   }

   public void setLocalNeutralList(ArrayList var1) {
      this.LocalNeutralList = var1;
   }

   public ArrayList getLocalGroupList() {
      return this.LocalGroupList;
   }

   public void setLocalGroupList(ArrayList var1) {
      this.LocalGroupList = var1;
   }

   public ArrayList getLocalRelevantEnemyList() {
      return this.LocalRelevantEnemyList;
   }

   public void setLocalRelevantEnemyList(ArrayList var1) {
      this.LocalRelevantEnemyList = var1;
   }

   public float getDangerLevels() {
      return this.dangerLevels;
   }

   public void setDangerLevels(float var1) {
      this.dangerLevels = var1;
   }

   public Stack getMeetList() {
      return this.MeetList;
   }

   public void setMeetList(Stack var1) {
      this.MeetList = var1;
   }

   public Order getOrder() {
      return this.Order;
   }

   public void setOrder(Order var1) {
      this.Order = var1;
   }

   public Stack getOrders() {
      return this.Orders;
   }

   public void setOrders(Stack var1) {
      this.Orders = var1;
   }

   public ArrayList getPerkList() {
      return this.PerkList;
   }

   public void setPerkList(ArrayList var1) {
      this.PerkList = var1;
   }

   public Order getPersonalNeed() {
      return this.PersonalNeed;
   }

   public void setPersonalNeed(Order var1) {
      this.PersonalNeed = var1;
   }

   public Stack getPersonalNeeds() {
      return this.PersonalNeeds;
   }

   public void setPersonalNeeds(Stack var1) {
      this.PersonalNeeds = var1;
   }

   public float getLeaveBodyTimedown() {
      return this.leaveBodyTimedown;
   }

   public void setLeaveBodyTimedown(float var1) {
      this.leaveBodyTimedown = var1;
   }

   public boolean isAllowConversation() {
      return this.AllowConversation;
   }

   public void setAllowConversation(boolean var1) {
      this.AllowConversation = var1;
   }

   public int getReanimPhase() {
      return this.ReanimPhase;
   }

   public void setReanimPhase(int var1) {
      this.ReanimPhase = var1;
   }

   public float getReanimateTimer() {
      return this.ReanimateTimer;
   }

   public void setReanimateTimer(float var1) {
      this.ReanimateTimer = var1;
   }

   public int getReanimAnimFrame() {
      return this.ReanimAnimFrame;
   }

   public void setReanimAnimFrame(int var1) {
      this.ReanimAnimFrame = var1;
   }

   public int getReanimAnimDelay() {
      return this.ReanimAnimDelay;
   }

   public void setReanimAnimDelay(int var1) {
      this.ReanimAnimDelay = var1;
   }

   public boolean isReanim() {
      return this.Reanim;
   }

   public void setReanim(boolean var1) {
      this.Reanim = var1;
   }

   public boolean isVisibleToNPCs() {
      return this.VisibleToNPCs;
   }

   public void setVisibleToNPCs(boolean var1) {
      this.VisibleToNPCs = var1;
   }

   public int getDieCount() {
      return this.DieCount;
   }

   public void setDieCount(int var1) {
      this.DieCount = var1;
   }

   public float getLlx() {
      return this.llx;
   }

   public void setLlx(float var1) {
      this.llx = var1;
   }

   public float getLly() {
      return this.lly;
   }

   public void setLly(float var1) {
      this.lly = var1;
   }

   public float getLlz() {
      return this.llz;
   }

   public void setLlz(float var1) {
      this.llz = var1;
   }

   public int getRemoteID() {
      return this.RemoteID;
   }

   public void setRemoteID(int var1) {
      this.RemoteID = var1;
   }

   public int getNumSurvivorsInVicinity() {
      return this.NumSurvivorsInVicinity;
   }

   public void setNumSurvivorsInVicinity(int var1) {
      this.NumSurvivorsInVicinity = var1;
   }

   public float getLevelUpMultiplier() {
      return this.LevelUpMultiplier;
   }

   public void setLevelUpMultiplier(float var1) {
      this.LevelUpMultiplier = var1;
   }

   public IsoGameCharacter.XP getXp() {
      return this.xp;
   }

   public void setXp(IsoGameCharacter.XP var1) {
      this.xp = var1;
   }

   public int getNumberOfPerksToPick() {
      return this.NumberOfPerksToPick;
   }

   public void setNumberOfPerksToPick(int var1) {
      this.NumberOfPerksToPick = var1;
   }

   public ArrayList getCanUpgradePerk() {
      return this.CanUpgradePerk;
   }

   public void setCanUpgradePerk(ArrayList var1) {
      this.CanUpgradePerk = var1;
   }

   public int getLastLocalEnemies() {
      return this.LastLocalEnemies;
   }

   public void setLastLocalEnemies(int var1) {
      this.LastLocalEnemies = var1;
   }

   public ArrayList getVeryCloseEnemyList() {
      return this.VeryCloseEnemyList;
   }

   public void setVeryCloseEnemyList(ArrayList var1) {
      this.VeryCloseEnemyList = var1;
   }

   public HashMap getLastKnownLocation() {
      return this.LastKnownLocation;
   }

   public void setLastKnownLocation(HashMap var1) {
      this.LastKnownLocation = var1;
   }

   public IsoGameCharacter getAttackedBy() {
      return this.AttackedBy;
   }

   public void setAttackedBy(IsoGameCharacter var1) {
      this.AttackedBy = var1;
   }

   public boolean isIgnoreStaggerBack() {
      return this.IgnoreStaggerBack;
   }

   public void setIgnoreStaggerBack(boolean var1) {
      this.IgnoreStaggerBack = var1;
   }

   public boolean isAttackWasSuperAttack() {
      return this.AttackWasSuperAttack;
   }

   public void setAttackWasSuperAttack(boolean var1) {
      this.AttackWasSuperAttack = var1;
   }

   public int getTimeThumping() {
      return this.TimeThumping;
   }

   public void setTimeThumping(int var1) {
      this.TimeThumping = var1;
   }

   public int getPatienceMax() {
      return this.PatienceMax;
   }

   public void setPatienceMax(int var1) {
      this.PatienceMax = var1;
   }

   public int getPatienceMin() {
      return this.PatienceMin;
   }

   public void setPatienceMin(int var1) {
      this.PatienceMin = var1;
   }

   public int getPatience() {
      return this.Patience;
   }

   public void setPatience(int var1) {
      this.Patience = var1;
   }

   public Stack getCharacterActions() {
      return this.CharacterActions;
   }

   public void setCharacterActions(Stack var1) {
      this.CharacterActions = var1;
   }

   public Vector2 getAngle() {
      return this.angle;
   }

   public void setAngle(Vector2 var1) {
      this.angle = var1;
   }

   public boolean isAsleep() {
      return this.Asleep;
   }

   public void setAsleep(boolean var1) {
      this.Asleep = var1;
   }

   public float getAttackDelay() {
      return this.AttackDelay;
   }

   public void setAttackDelay(float var1) {
      this.AttackDelay = var1;
   }

   public float getAttackDelayMax() {
      return this.AttackDelayMax;
   }

   public void setAttackDelayMax(float var1) {
      this.AttackDelayMax = var1;
   }

   public float getAttackDelayUse() {
      return this.AttackDelayUse;
   }

   public void setAttackDelayUse(float var1) {
      this.AttackDelayUse = var1;
   }

   public int getZombieKills() {
      return this.ZombieKills;
   }

   public void setZombieKills(int var1) {
      this.ZombieKills = var1;
   }

   public int getLastZombieKills() {
      return this.LastZombieKills;
   }

   public void setLastZombieKills(int var1) {
      this.LastZombieKills = var1;
   }

   public boolean isSuperAttack() {
      return this.superAttack;
   }

   public void setSuperAttack(boolean var1) {
      this.superAttack = var1;
   }

   public float getForceWakeUpTime() {
      return this.ForceWakeUpTime;
   }

   public void setForceWakeUpTime(float var1) {
      this.ForceWakeUpTime = var1;
   }

   public void forceAwake() {
      if (this.isAsleep()) {
         this.ForceWakeUp = true;
      }

   }

   public BodyDamage getBodyDamage() {
      return this.BodyDamage;
   }

   public void setBodyDamage(BodyDamage var1) {
      this.BodyDamage = var1;
   }

   public BodyDamage getBodyDamageRemote() {
      if (this.BodyDamageRemote == null) {
         this.BodyDamageRemote = new BodyDamage((IsoGameCharacter)null);
      }

      return this.BodyDamageRemote;
   }

   public void resetBodyDamageRemote() {
      this.BodyDamageRemote = null;
   }

   public InventoryItem getCraftIngredient1() {
      return this.craftIngredient1;
   }

   public void setCraftIngredient1(InventoryItem var1) {
      this.craftIngredient1 = var1;
   }

   public InventoryItem getCraftIngredient2() {
      return this.craftIngredient2;
   }

   public void setCraftIngredient2(InventoryItem var1) {
      this.craftIngredient2 = var1;
   }

   public InventoryItem getCraftIngredient3() {
      return this.craftIngredient3;
   }

   public void setCraftIngredient3(InventoryItem var1) {
      this.craftIngredient3 = var1;
   }

   public InventoryItem getCraftIngredient4() {
      return this.craftIngredient4;
   }

   public void setCraftIngredient4(InventoryItem var1) {
      this.craftIngredient4 = var1;
   }

   public State getDefaultState() {
      return this.defaultState;
   }

   public void setDefaultState(State var1) {
      this.defaultState = var1;
   }

   public SurvivorDesc getDescriptor() {
      return this.descriptor;
   }

   public void setDescriptor(SurvivorDesc var1) {
      this.descriptor = var1;
   }

   public String getFullName() {
      return this.descriptor != null ? this.descriptor.forename + " " + this.descriptor.surname : "Bob Smith";
   }

   public Stack getFamiliarBuildings() {
      return this.FamiliarBuildings;
   }

   public void setFamiliarBuildings(Stack var1) {
      this.FamiliarBuildings = var1;
   }

   public AStarPathFinderResult getFinder() {
      return this.finder;
   }

   public void setFinder(AStarPathFinderResult var1) {
      this.finder = var1;
   }

   public float getFireKillRate() {
      return this.FireKillRate;
   }

   public void setFireKillRate(float var1) {
      this.FireKillRate = var1;
   }

   public int getFireSpreadProbability() {
      return this.FireSpreadProbability;
   }

   public void setFireSpreadProbability(int var1) {
      this.FireSpreadProbability = var1;
   }

   public float getFootStepCounter() {
      return this.footStepCounter;
   }

   public void setFootStepCounter(float var1) {
      this.footStepCounter = var1;
   }

   public float getFootStepCounterMax() {
      return this.footStepCounterMax;
   }

   public void setFootStepCounterMax(float var1) {
      this.footStepCounterMax = var1;
   }

   public float getHealth() {
      return this.Health;
   }

   public void setHealth(float var1) {
      this.Health = var1;
   }

   public MasterSurvivorBehavior getMasterProper() {
      return this.masterProper;
   }

   public void setMasterProper(MasterSurvivorBehavior var1) {
      this.masterProper = var1;
   }

   public IsoGameCharacter getHitBy() {
      return this.hitBy;
   }

   public void setHitBy(IsoGameCharacter var1) {
      this.hitBy = var1;
   }

   public String getHurtSound() {
      return this.hurtSound;
   }

   public void setHurtSound(String var1) {
      this.hurtSound = var1;
   }

   public boolean isIgnoreMovementForDirection() {
      return this.IgnoreMovementForDirection;
   }

   public void setIgnoreMovementForDirection(boolean var1) {
      this.IgnoreMovementForDirection = var1;
   }

   public ItemContainer getInventory() {
      return this.inventory;
   }

   public void setInventory(ItemContainer var1) {
      this.inventory = var1;
      this.inventory.setExplored(true);
   }

   public IsoDirections getLastdir() {
      return this.lastdir;
   }

   public void setLastdir(IsoDirections var1) {
      this.lastdir = var1;
   }

   public boolean isPrimaryEquipped(String var1) {
      return this.leftHandItem == null ? false : this.leftHandItem.getType().equals(var1);
   }

   public InventoryItem getPrimaryHandItem() {
      return this.leftHandItem;
   }

   public void setPrimaryHandItem(InventoryItem var1) {
      this.setEquipParent(this.leftHandItem, var1);
      this.leftHandItem = var1;
      if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
         GameClient.instance.equip((IsoPlayer)this, 0, var1);
      }

      if (var1 instanceof HandWeapon) {
         if (this.legsSprite != null && this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.contains("Attack_") && ((HandWeapon)var1).getSwingAnim() != null) {
            this.PlayAnim("Attack_" + ((HandWeapon)var1).getSwingAnim());
            this.def.Finished = true;
            this.def.Frame = 0.0F;
         }
      } else if (this.legsSprite != null && this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.contains("Attack_")) {
         this.PlayAnim("Idle");
      }

      LuaEventManager.triggerEvent("OnEquipPrimary", this, var1);
      this.resetModel();
   }

   protected void setEquipParent(InventoryItem var1, InventoryItem var2) {
      if (var1 != null) {
         var1.setEquipParent((IsoGameCharacter)null);
      }

      if (var2 != null) {
         var2.setEquipParent(this);
      }

   }

   public InventoryItem getClothingItem_Head() {
      return this.ClothingItem_Head;
   }

   public void setClothingItem_Head(InventoryItem var1) {
      this.ClothingItem_Head = var1;
      if (this.ClothingItem_Head != null && this.ClothingItem_Head.getContainer() != null) {
         this.ClothingItem_Head.getContainer().parent = this;
      }

   }

   public InventoryItem getClothingItem_Torso() {
      return this.ClothingItem_Torso;
   }

   public InventoryItem getClothingItem_Back() {
      return this.ClothingItem_Back;
   }

   public void setClothingItem_Back(InventoryItem var1) {
      IsoCell var2 = IsoWorld.instance.CurrentCell;
      if (this.ClothingItem_Back != null) {
         var2.addToProcessItemsRemove(this.ClothingItem_Back);
      }

      this.ClothingItem_Back = var1;
      if (this.ClothingItem_Back != null && this.ClothingItem_Back.getContainer() != null) {
         this.ClothingItem_Back.getContainer().parent = this;
      }

      if (this.ClothingItem_Back != null) {
         var2.addToProcessItems(this.ClothingItem_Back);
      }

   }

   public void setClothingItem_Torso(InventoryItem var1) {
      IsoCell var2 = IsoWorld.instance.CurrentCell;
      if (this.ClothingItem_Torso != null) {
         var2.addToProcessItemsRemove(this.ClothingItem_Torso);
      }

      this.ClothingItem_Torso = var1;
      if (this.ClothingItem_Torso != null && this.ClothingItem_Torso.getContainer() != null) {
         this.ClothingItem_Torso.getContainer().parent = this;
      }

      if (var1 == null) {
         this.topSprite = null;
         this.descriptor.toppal = null;
      } else {
         this.descriptor.toppal = ((Clothing)var1).getPalette();
         if (!this.bFemale && this.descriptor.toppal.contains("Blouse")) {
            this.descriptor.toppal = this.descriptor.toppal.replace("Blouse", "Shirt");
         }

         this.descriptor.topColor.set(var1.col);
         var2.addToProcessItems(var1);
      }

      if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
         GameClient.instance.sendClothing((IsoPlayer)this, Item.ClothingBodyLocation.Top.ordinal(), this.ClothingItem_Torso);
      }

      this.resetModelNextFrame();
   }

   public InventoryItem getClothingItem_Hands() {
      return this.ClothingItem_Hands;
   }

   public void setClothingItem_Hands(InventoryItem var1) {
      this.ClothingItem_Hands = var1;
      if (this.ClothingItem_Hands != null && this.ClothingItem_Hands.getContainer() != null) {
         this.ClothingItem_Hands.getContainer().parent = this;
      }

   }

   public InventoryItem getClothingItem_Legs() {
      return this.ClothingItem_Legs;
   }

   public void setClothingItem_Legs(InventoryItem var1) {
      IsoCell var2 = IsoWorld.instance.CurrentCell;
      if (this.ClothingItem_Legs != null) {
         var2.addToProcessItemsRemove(this.ClothingItem_Legs);
      }

      this.ClothingItem_Legs = var1;
      if (this.ClothingItem_Legs != null && this.ClothingItem_Legs.getContainer() != null) {
         this.ClothingItem_Legs.getContainer().parent = this;
      }

      if (var1 == null) {
         this.bottomsSprite = null;
         this.descriptor.bottomspal = null;
      } else {
         this.descriptor.bottomspal = ((Clothing)var1).getPalette();
         if (!this.bFemale && this.descriptor.bottomspal.contains("Skirt")) {
            this.descriptor.bottomspal = this.descriptor.bottomspal.replace("Skirt", "Trousers");
         }

         this.descriptor.trouserColor.set(var1.col);
         var2.addToProcessItems(var1);
      }

      if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
         GameClient.instance.sendClothing((IsoPlayer)this, Item.ClothingBodyLocation.Bottoms.ordinal(), this.ClothingItem_Legs);
      }

      this.resetModelNextFrame();
   }

   public InventoryItem getClothingItem_Feet() {
      return this.ClothingItem_Feet;
   }

   public void setClothingItem_Feet(InventoryItem var1) {
      IsoCell var2 = IsoWorld.instance.CurrentCell;
      if (this.ClothingItem_Feet != null) {
         var2.addToProcessItemsRemove(this.ClothingItem_Feet);
      }

      this.ClothingItem_Feet = var1;
      if (this.ClothingItem_Feet != null && this.ClothingItem_Feet.getContainer() != null) {
         this.ClothingItem_Feet.getContainer().parent = this;
      }

      if (var1 == null) {
         this.shoeSprite = null;
         this.descriptor.shoespal = null;
      } else {
         var2.addToProcessItems(this.ClothingItem_Feet);
      }

      if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
         GameClient.instance.sendClothing((IsoPlayer)this, Item.ClothingBodyLocation.Shoes.ordinal(), this.ClothingItem_Feet);
      }

      this.resetModelNextFrame();
   }

   public SequenceBehavior getMasterBehaviorList() {
      return this.masterBehaviorList;
   }

   public void setMasterBehaviorList(SequenceBehavior var1) {
      this.masterBehaviorList = var1;
   }

   public int getNextWander() {
      return this.NextWander;
   }

   public void setNextWander(int var1) {
      this.NextWander = var1;
   }

   public boolean isOnFire() {
      return this.OnFire;
   }

   public void setOnFire(boolean var1) {
      this.OnFire = var1;
   }

   public Path getPath() {
      return this.path;
   }

   public void setPath(Path var1) {
      this.path = var1;
   }

   public int getPathIndex() {
      return this.pathIndex;
   }

   public void setPathIndex(int var1) {
      this.pathIndex = var1;
   }

   public float getPathSpeed() {
      return this.PathSpeed;
   }

   public void setPathSpeed(float var1) {
      if (this == IsoCamera.CamCharacter) {
      }

      this.PathSpeed = var1;
   }

   public int getPathTargetX() {
      return (int)this.getPathFindBehavior2().getTargetX();
   }

   public void setPathTargetX(int var1) {
   }

   public int getPathTargetY() {
      return (int)this.getPathFindBehavior2().getTargetY();
   }

   public void setPathTargetY(int var1) {
   }

   public int getPathTargetZ() {
      return (int)this.getPathFindBehavior2().getTargetZ();
   }

   public void setPathTargetZ(int var1) {
   }

   public SurvivorPersonality getPersonality() {
      return this.Personality;
   }

   public void setPersonality(SurvivorPersonality var1) {
      this.Personality = var1;
   }

   public InventoryItem getSecondaryHandItem() {
      return this.rightHandItem;
   }

   public void setSecondaryHandItem(InventoryItem var1) {
      this.setEquipParent(this.rightHandItem, var1);
      this.rightHandItem = var1;
      if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
         GameClient.instance.equip((IsoPlayer)this, 1, var1);
      }

      LuaEventManager.triggerEvent("OnEquipSecondary", this, var1);
   }

   public String getSayLineOld() {
      return this.sayLine;
   }

   public void setSayLine(String var1) {
      this.Say(var1);
   }

   public Color getSpeakColour() {
      return this.SpeakColour;
   }

   public void setSpeakColour(Color var1) {
      this.SpeakColour = var1;
   }

   public void setSpeakColourInfo(ColorInfo var1) {
      this.SpeakColour = new Color(var1.r, var1.g, var1.b, 1.0F);
   }

   public float getSlowFactor() {
      return this.slowFactor;
   }

   public void setSlowFactor(float var1) {
      this.slowFactor = var1;
   }

   public float getSlowTimer() {
      return this.slowTimer;
   }

   public void setSlowTimer(float var1) {
      this.slowTimer = var1;
   }

   public boolean isbUseParts() {
      return this.bUseParts;
   }

   public void setbUseParts(boolean var1) {
      this.bUseParts = var1;
   }

   public boolean isSpeaking() {
      return this.IsSpeaking();
   }

   public void setSpeaking(boolean var1) {
      this.Speaking = var1;
   }

   public float getSpeakTime() {
      return this.SpeakTime;
   }

   public void setSpeakTime(int var1) {
      this.SpeakTime = (float)var1;
   }

   public float getSpeedMod() {
      return this.speedMod;
   }

   public void setSpeedMod(float var1) {
      this.speedMod = var1;
   }

   public float getStaggerTimeMod() {
      return this.staggerTimeMod;
   }

   public void setStaggerTimeMod(float var1) {
      this.staggerTimeMod = var1;
   }

   public StateMachine getStateMachine() {
      return this.stateMachine;
   }

   public void setStateMachine(StateMachine var1) {
      this.stateMachine = var1;
   }

   public Moodles getMoodles() {
      return this.Moodles;
   }

   public void setMoodles(Moodles var1) {
      this.Moodles = var1;
   }

   public Stats getStats() {
      return this.stats;
   }

   public void setStats(Stats var1) {
      this.stats = var1;
   }

   public Stack getTagGroup() {
      return this.TagGroup;
   }

   public void setTagGroup(Stack var1) {
      this.TagGroup = var1;
   }

   public Stack getUsedItemsOn() {
      return this.UsedItemsOn;
   }

   public void setUsedItemsOn(Stack var1) {
      this.UsedItemsOn = var1;
   }

   public HandWeapon getUseHandWeapon() {
      return this.useHandWeapon;
   }

   public void setUseHandWeapon(HandWeapon var1) {
      this.useHandWeapon = var1;
   }

   public IsoSprite getTorsoSprite() {
      return this.torsoSprite;
   }

   public void setTorsoSprite(IsoSprite var1) {
      this.torsoSprite = var1;
   }

   public IsoSprite getLegsSprite() {
      return this.legsSprite;
   }

   public void setLegsSprite(IsoSprite var1) {
      this.legsSprite = var1;
   }

   public IsoSprite getHeadSprite() {
      return this.headSprite;
   }

   public void setHeadSprite(IsoSprite var1) {
      this.headSprite = var1;
   }

   public IsoSprite getShoeSprite() {
      return this.shoeSprite;
   }

   public void setShoeSprite(IsoSprite var1) {
      this.shoeSprite = var1;
   }

   public IsoSprite getTopSprite() {
      return this.topSprite;
   }

   public void setTopSprite(IsoSprite var1) {
      this.topSprite = var1;
   }

   public IsoSprite getBottomsSprite() {
      return this.bottomsSprite;
   }

   public ArrayList getExtraSprites() {
      return this.extraSprites;
   }

   public IsoSprite getHairSprite() {
      return this.hairSprite;
   }

   public void setHairSprite(IsoSprite var1) {
      this.hairSprite = var1;
   }

   public void setBottomsSprite(IsoSprite var1) {
      this.bottomsSprite = var1;
   }

   public Stack getWounds() {
      return this.wounds;
   }

   public void setWounds(Stack var1) {
      this.wounds = var1;
   }

   public IsoGridSquare getAttackTargetSquare() {
      return this.attackTargetSquare;
   }

   public void setAttackTargetSquare(IsoGridSquare var1) {
      this.attackTargetSquare = var1;
   }

   public float getBloodImpactX() {
      return this.BloodImpactX;
   }

   public void setBloodImpactX(float var1) {
      this.BloodImpactX = var1;
   }

   public float getBloodImpactY() {
      return this.BloodImpactY;
   }

   public void setBloodImpactY(float var1) {
      this.BloodImpactY = var1;
   }

   public float getBloodImpactZ() {
      return this.BloodImpactZ;
   }

   public void setBloodImpactZ(float var1) {
      this.BloodImpactZ = var1;
   }

   public IsoSprite getBloodSplat() {
      return this.bloodSplat;
   }

   public void setBloodSplat(IsoSprite var1) {
      this.bloodSplat = var1;
   }

   public boolean isbOnBed() {
      return this.bOnBed;
   }

   public void setbOnBed(boolean var1) {
      this.bOnBed = var1;
   }

   public Vector2 getMoveForwardVec() {
      return this.moveForwardVec;
   }

   public void setMoveForwardVec(Vector2 var1) {
      this.moveForwardVec = var1;
   }

   public boolean isPathing() {
      return this.pathing;
   }

   public void setPathing(boolean var1) {
      this.pathing = var1;
   }

   public Stack getLocalEnemyList() {
      return this.LocalEnemyList;
   }

   public void setLocalEnemyList(Stack var1) {
      this.LocalEnemyList = var1;
   }

   public Stack getEnemyList() {
      return this.EnemyList;
   }

   public void setEnemyList(Stack var1) {
      this.EnemyList = var1;
   }

   public ArrayList getTraits() {
      return this.Traits;
   }

   public void setTraits(ArrayList var1) {
      this.Traits = var1;
   }

   public Integer getMaxWeight() {
      return this.maxWeight;
   }

   public void setMaxWeight(Integer var1) {
      this.maxWeight = var1;
   }

   public int getMaxWeightBase() {
      return this.maxWeightBase;
   }

   public void setMaxWeightBase(int var1) {
      this.maxWeightBase = var1;
   }

   public float getSleepingTabletDelta() {
      return this.SleepingTabletDelta;
   }

   public void setSleepingTabletDelta(float var1) {
      this.SleepingTabletDelta = var1;
   }

   public float getBetaEffect() {
      return this.BetaEffect;
   }

   public void setBetaEffect(float var1) {
      this.BetaEffect = var1;
   }

   public float getDepressEffect() {
      return this.DepressEffect;
   }

   public void setDepressEffect(float var1) {
      this.DepressEffect = var1;
   }

   public float getSleepingTabletEffect() {
      return this.SleepingTabletEffect;
   }

   public void setSleepingTabletEffect(float var1) {
      this.SleepingTabletEffect = var1;
   }

   public float getBetaDelta() {
      return this.BetaDelta;
   }

   public void setBetaDelta(float var1) {
      this.BetaDelta = var1;
   }

   public float getDepressDelta() {
      return this.DepressDelta;
   }

   public void setDepressDelta(float var1) {
      this.DepressDelta = var1;
   }

   public float getPainEffect() {
      return this.PainEffect;
   }

   public void setPainEffect(float var1) {
      this.PainEffect = var1;
   }

   public float getPainDelta() {
      return this.PainDelta;
   }

   public void setPainDelta(float var1) {
      this.PainDelta = var1;
   }

   public boolean isbDoDefer() {
      return this.bDoDefer;
   }

   public void setbDoDefer(boolean var1) {
      this.bDoDefer = var1;
   }

   public IsoGameCharacter.Location getLastHeardSound() {
      return this.LastHeardSound;
   }

   public void setLastHeardSound(int var1, int var2, int var3) {
      this.LastHeardSound.x = var1;
      this.LastHeardSound.y = var2;
      this.LastHeardSound.z = var3;
   }

   public float getLrx() {
      return this.lrx;
   }

   public void setLrx(float var1) {
      this.lrx = var1;
   }

   public float getLry() {
      return this.lry;
   }

   public void setLry(float var1) {
      this.lry = var1;
   }

   public boolean isClimbing() {
      return this.bClimbing;
   }

   public void setbClimbing(boolean var1) {
      this.bClimbing = var1;
   }

   public boolean isLastCollidedW() {
      return this.lastCollidedW;
   }

   public void setLastCollidedW(boolean var1) {
      this.lastCollidedW = var1;
   }

   public boolean isLastCollidedN() {
      return this.lastCollidedN;
   }

   public void setLastCollidedN(boolean var1) {
      this.lastCollidedN = var1;
   }

   public int getFallTime() {
      return this.fallTime;
   }

   public void setFallTime(int var1) {
      this.fallTime = var1;
   }

   public float getLastFallSpeed() {
      return this.lastFallSpeed;
   }

   public void setLastFallSpeed(float var1) {
      this.lastFallSpeed = var1;
   }

   public boolean isbFalling() {
      return this.bFalling;
   }

   public void setbFalling(boolean var1) {
      this.bFalling = var1;
   }

   public IsoBuilding getCurrentBuilding() {
      if (this.current == null) {
         return null;
      } else {
         return this.current.getRoom() == null ? null : this.current.getRoom().building;
      }
   }

   public BuildingDef getCurrentBuildingDef() {
      if (this.current == null) {
         return null;
      } else if (this.current.getRoom() == null) {
         return null;
      } else {
         return this.current.getRoom().building != null ? this.current.getRoom().building.def : null;
      }
   }

   public RoomDef getCurrentRoomDef() {
      if (this.current == null) {
         return null;
      } else {
         return this.current.getRoom() != null ? this.current.getRoom().def : null;
      }
   }

   public float getTorchStrength() {
      return 0.0F;
   }

   public boolean IsSneaking() {
      return this.getMovementLastFrame().getLength() < 0.04F;
   }

   public float getHammerSoundMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
      if (var1 == 2) {
         return 0.8F;
      } else if (var1 == 3) {
         return 0.6F;
      } else if (var1 == 4) {
         return 0.4F;
      } else {
         return var1 >= 5 ? 0.4F : 1.0F;
      }
   }

   public float getWeldingSoundMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.MetalWelding);
      if (var1 == 2) {
         return 0.8F;
      } else if (var1 == 3) {
         return 0.6F;
      } else if (var1 == 4) {
         return 0.4F;
      } else {
         return var1 >= 5 ? 0.4F : 1.0F;
      }
   }

   public float getBarricadeTimeMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
      if (var1 == 1) {
         return 0.8F;
      } else if (var1 == 2) {
         return 0.7F;
      } else if (var1 == 3) {
         return 0.62F;
      } else if (var1 == 4) {
         return 0.56F;
      } else if (var1 == 5) {
         return 0.5F;
      } else if (var1 == 6) {
         return 0.42F;
      } else if (var1 == 7) {
         return 0.36F;
      } else if (var1 == 8) {
         return 0.3F;
      } else if (var1 == 9) {
         return 0.26F;
      } else {
         return var1 == 10 ? 0.2F : 0.7F;
      }
   }

   public float getMetalBarricadeStrengthMod() {
      switch(this.getPerkLevel(PerkFactory.Perks.MetalWelding)) {
      case 2:
         return 1.1F;
      case 3:
         return 1.14F;
      case 4:
         return 1.18F;
      case 5:
         return 1.22F;
      case 6:
         return 1.16F;
      case 7:
         return 1.3F;
      case 8:
         return 1.34F;
      case 9:
         return 1.4F;
      case 10:
         return 1.5F;
      default:
         int var1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
         if (var1 == 2) {
            return 1.1F;
         } else if (var1 == 3) {
            return 1.14F;
         } else if (var1 == 4) {
            return 1.18F;
         } else if (var1 == 5) {
            return 1.22F;
         } else if (var1 == 6) {
            return 1.26F;
         } else if (var1 == 7) {
            return 1.3F;
         } else if (var1 == 8) {
            return 1.34F;
         } else if (var1 == 9) {
            return 1.4F;
         } else {
            return var1 == 10 ? 1.5F : 1.0F;
         }
      }
   }

   public float getBarricadeStrengthMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
      if (var1 == 2) {
         return 1.1F;
      } else if (var1 == 3) {
         return 1.14F;
      } else if (var1 == 4) {
         return 1.18F;
      } else if (var1 == 5) {
         return 1.22F;
      } else if (var1 == 6) {
         return 1.26F;
      } else if (var1 == 7) {
         return 1.3F;
      } else if (var1 == 8) {
         return 1.34F;
      } else if (var1 == 9) {
         return 1.4F;
      } else {
         return var1 == 10 ? 1.5F : 1.0F;
      }
   }

   public float getSneakSpotMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Sneak);
      if (var1 == 1) {
         return 0.9F;
      } else if (var1 == 2) {
         return 0.8F;
      } else if (var1 == 3) {
         return 0.75F;
      } else if (var1 == 4) {
         return 0.7F;
      } else if (var1 == 5) {
         return 0.65F;
      } else if (var1 == 6) {
         return 0.6F;
      } else if (var1 == 7) {
         return 0.55F;
      } else if (var1 == 8) {
         return 0.5F;
      } else if (var1 == 9) {
         return 0.45F;
      } else {
         return var1 == 10 ? 0.4F : 0.95F;
      }
   }

   public float getNimbleMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Nimble);
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
         return 1.38F;
      } else if (var1 == 9) {
         return 1.42F;
      } else {
         return var1 == 10 ? 1.5F : 1.0F;
      }
   }

   public float getFatigueMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Fitness);
      if (var1 == 1) {
         return 0.95F;
      } else if (var1 == 2) {
         return 0.92F;
      } else if (var1 == 3) {
         return 0.89F;
      } else if (var1 == 4) {
         return 0.87F;
      } else if (var1 == 5) {
         return 0.85F;
      } else if (var1 == 6) {
         return 0.83F;
      } else if (var1 == 7) {
         return 0.81F;
      } else if (var1 == 8) {
         return 0.79F;
      } else if (var1 == 9) {
         return 0.77F;
      } else {
         return var1 == 10 ? 0.75F : 1.0F;
      }
   }

   public float getLightfootMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Lightfoot);
      if (var1 == 1) {
         return 0.9F;
      } else if (var1 == 2) {
         return 0.79F;
      } else if (var1 == 3) {
         return 0.71F;
      } else if (var1 == 4) {
         return 0.65F;
      } else if (var1 == 5) {
         return 0.59F;
      } else if (var1 == 6) {
         return 0.52F;
      } else if (var1 == 7) {
         return 0.45F;
      } else if (var1 == 8) {
         return 0.37F;
      } else if (var1 == 9) {
         return 0.3F;
      } else {
         return var1 == 10 ? 0.2F : 0.99F;
      }
   }

   public float getPacingMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Fitness);
      if (var1 == 1) {
         return 0.6F;
      } else if (var1 == 2) {
         return 0.5F;
      } else if (var1 == 3) {
         return 0.45F;
      } else if (var1 == 4) {
         return 0.4F;
      } else if (var1 == 5) {
         return 0.35F;
      } else if (var1 == 6) {
         return 0.3F;
      } else if (var1 == 7) {
         return 0.25F;
      } else if (var1 == 8) {
         return 0.2F;
      } else if (var1 == 9) {
         return 0.15F;
      } else {
         return var1 == 10 ? 0.1F : 0.65F;
      }
   }

   public float getHyperthermiaMod() {
      float var1 = 1.0F;
      if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) > 1) {
         var1 = 1.0F;
         if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4) {
            var1 = 2.0F;
         }
      }

      return var1;
   }

   public float getHittingMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Strength);
      if (var1 == 1) {
         return 0.8F;
      } else if (var1 == 2) {
         return 0.85F;
      } else if (var1 == 3) {
         return 0.9F;
      } else if (var1 == 4) {
         return 0.95F;
      } else if (var1 == 5) {
         return 1.0F;
      } else if (var1 == 6) {
         return 1.05F;
      } else if (var1 == 7) {
         return 1.1F;
      } else if (var1 == 8) {
         return 1.15F;
      } else if (var1 == 9) {
         return 1.2F;
      } else {
         return var1 == 10 ? 1.25F : 0.75F;
      }
   }

   public float getShovingMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Strength);
      if (var1 == 1) {
         return 0.8F;
      } else if (var1 == 2) {
         return 0.85F;
      } else if (var1 == 3) {
         return 0.9F;
      } else if (var1 == 4) {
         return 0.95F;
      } else if (var1 == 5) {
         return 1.0F;
      } else if (var1 == 6) {
         return 1.05F;
      } else if (var1 == 7) {
         return 1.1F;
      } else if (var1 == 8) {
         return 1.15F;
      } else if (var1 == 9) {
         return 1.2F;
      } else {
         return var1 == 10 ? 1.25F : 0.75F;
      }
   }

   public float getRecoveryMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Fitness);
      float var2 = 0.0F;
      if (var1 == 0) {
         var2 = 0.7F;
      }

      if (var1 == 1) {
         var2 = 0.8F;
      }

      if (var1 == 2) {
         var2 = 0.9F;
      }

      if (var1 == 3) {
         var2 = 1.0F;
      }

      if (var1 == 4) {
         var2 = 1.1F;
      }

      if (var1 == 5) {
         var2 = 1.2F;
      }

      if (var1 == 6) {
         var2 = 1.3F;
      }

      if (var1 == 7) {
         var2 = 1.4F;
      }

      if (var1 == 8) {
         var2 = 1.5F;
      }

      if (var1 == 9) {
         var2 = 1.55F;
      }

      if (var1 == 10) {
         var2 = 1.6F;
      }

      if (this.HasTrait("Obese")) {
         var2 = (float)((double)var2 * 0.4D);
      }

      if (this.HasTrait("Overweight")) {
         var2 = (float)((double)var2 * 0.7D);
      }

      if (this.HasTrait("Very Underweight")) {
         var2 = (float)((double)var2 * 0.7D);
      }

      if (this.HasTrait("Emaciated")) {
         var2 = (float)((double)var2 * 0.3D);
      }

      if (this instanceof IsoPlayer) {
         if (((IsoPlayer)this).getNutrition().getLipids() < -1500.0F) {
            var2 = (float)((double)var2 * 0.2D);
         } else if (((IsoPlayer)this).getNutrition().getLipids() < -1000.0F) {
            var2 = (float)((double)var2 * 0.5D);
         }

         if (((IsoPlayer)this).getNutrition().getProteins() < -1500.0F) {
            var2 = (float)((double)var2 * 0.2D);
         } else if (((IsoPlayer)this).getNutrition().getProteins() < -1000.0F) {
            var2 = (float)((double)var2 * 0.5D);
         }
      }

      return var2;
   }

   public float getWeightMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Strength);
      if (var1 == 1) {
         return 0.9F;
      } else if (var1 == 2) {
         return 1.07F;
      } else if (var1 == 3) {
         return 1.24F;
      } else if (var1 == 4) {
         return 1.41F;
      } else if (var1 == 5) {
         return 1.58F;
      } else if (var1 == 6) {
         return 1.75F;
      } else if (var1 == 7) {
         return 1.92F;
      } else if (var1 == 8) {
         return 2.09F;
      } else if (var1 == 9) {
         return 2.26F;
      } else {
         return var1 == 10 ? 2.5F : 0.8F;
      }
   }

   public int getHitChancesMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
      if (var1 == 1) {
         return 1;
      } else if (var1 == 2) {
         return 1;
      } else if (var1 == 3) {
         return 2;
      } else if (var1 == 4) {
         return 2;
      } else if (var1 == 5) {
         return 3;
      } else if (var1 == 6) {
         return 3;
      } else if (var1 == 7) {
         return 4;
      } else if (var1 == 8) {
         return 4;
      } else if (var1 == 9) {
         return 5;
      } else {
         return var1 == 10 ? 5 : 1;
      }
   }

   public float getSprintMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.Sprinting);
      if (var1 == 1) {
         return 1.1F;
      } else if (var1 == 2) {
         return 1.15F;
      } else if (var1 == 3) {
         return 1.2F;
      } else if (var1 == 4) {
         return 1.25F;
      } else if (var1 == 5) {
         return 1.3F;
      } else if (var1 == 6) {
         return 1.35F;
      } else if (var1 == 7) {
         return 1.4F;
      } else if (var1 == 8) {
         return 1.45F;
      } else if (var1 == 9) {
         return 1.5F;
      } else {
         return var1 == 10 ? 1.6F : 0.9F;
      }
   }

   public int getPerkLevel(PerkFactory.Perks var1) {
      for(int var2 = 0; var2 < this.PerkList.size(); ++var2) {
         IsoGameCharacter.PerkInfo var3 = (IsoGameCharacter.PerkInfo)this.PerkList.get(var2);
         if (var3.perkType == var1) {
            return var3.level;
         }
      }

      return 0;
   }

   public void setPerkLevelDebug(PerkFactory.Perks var1, int var2) {
      for(int var3 = 0; var3 < this.PerkList.size(); ++var3) {
         IsoGameCharacter.PerkInfo var4 = (IsoGameCharacter.PerkInfo)this.PerkList.get(var3);
         if (var4.perkType == var1) {
            var4.level = var2;
         }
      }

   }

   public void LoseLevel(PerkFactory.Perks var1) {
      for(int var2 = 0; var2 < this.PerkList.size(); ++var2) {
         IsoGameCharacter.PerkInfo var3 = (IsoGameCharacter.PerkInfo)this.PerkList.get(var2);
         if (var3.perkType == var1) {
            --var3.level;
            if (var3.level < 0) {
               var3.level = 0;
            }

            LuaEventManager.triggerEvent("LevelPerk", this, var1, var3.level, false);
            return;
         }
      }

      LuaEventManager.triggerEvent("LevelPerk", this, var1, 0, false);
   }

   public void LevelPerk(PerkFactory.Perks var1, boolean var2) {
      if (var2) {
         --this.NumberOfPerksToPick;
         if (this.NumberOfPerksToPick < 0) {
            this.NumberOfPerksToPick = 0;
         }
      }

      IsoGameCharacter.PerkInfo var4;
      for(int var3 = 0; var3 < this.PerkList.size(); ++var3) {
         var4 = (IsoGameCharacter.PerkInfo)this.PerkList.get(var3);
         if (var4.perkType == var1) {
            ++var4.level;
            if (var4.level > 10) {
               var4.level = 10;
            }

            if (GameClient.bClient && this instanceof IsoPlayer) {
               GameClient.instance.sendSyncXp((IsoPlayer)this);
            }

            LuaEventManager.triggerEventGarbage("LevelPerk", this, var1, var4.level, true);
            return;
         }
      }

      PerkFactory.Perk var5 = (PerkFactory.Perk)PerkFactory.PerkMap.get(var1);
      var4 = new IsoGameCharacter.PerkInfo();
      var4.perk = var5;
      var4.perkType = var1;
      var4.level = 1;
      this.PerkList.add(var4);
      if (GameClient.bClient && this instanceof IsoPlayer) {
         GameClient.instance.sendSyncXp((IsoPlayer)this);
      }

      LuaEventManager.triggerEvent("LevelPerk", this, var1, var4.level, true);
   }

   public void LevelPerk(PerkFactory.Perks var1) {
      this.LevelPerk(var1, true);
   }

   public void level0(PerkFactory.Perks var1) {
      for(int var2 = 0; var2 < this.PerkList.size(); ++var2) {
         IsoGameCharacter.PerkInfo var3 = (IsoGameCharacter.PerkInfo)this.PerkList.get(var2);
         if (var3.perkType == var1) {
            var3.level = 0;
         }
      }

   }

   public void LevelUp() {
      if (GameClient.bClient && this instanceof IsoPlayer) {
         GameClient.instance.sendSyncXp((IsoPlayer)this);
      }

      ++this.NumberOfPerksToPick;
   }

   public void GiveOrder(Order var1, boolean var2) {
      if (var2) {
         this.Orders.clear();
      }

      if (var1.character != this) {
         var1.character = this;
      }

      this.Orders.push(var1);
   }

   public void GivePersonalNeed(Order var1) {
      if (var1.character != this) {
         var1.character = this;
      }

      if (var1.isCritical()) {
         this.PersonalNeeds.push(var1);
      } else {
         this.PersonalNeeds.insertElementAt(var1, 0);
      }

   }

   public IsoGameCharacter.Location getLastKnownLocationOf(String var1) {
      return this.LastKnownLocation.containsKey(var1) ? (IsoGameCharacter.Location)this.LastKnownLocation.get(var1) : null;
   }

   public void ReadLiterature(Literature var1) {
      Stats var10000 = this.stats;
      var10000.stress += var1.getStressChange();
      this.getBodyDamage().JustReadSomething(var1);
      if (var1.getTeachedRecipes() != null) {
         for(int var2 = 0; var2 < var1.getTeachedRecipes().size(); ++var2) {
            if (!this.getKnownRecipes().contains(var1.getTeachedRecipes().get(var2))) {
               this.getKnownRecipes().add(var1.getTeachedRecipes().get(var2));
            }
         }
      }

      var1.Use();
   }

   public void OnDeath() {
   }

   public boolean IsArmed() {
      return this.inventory.getBestWeapon(this.descriptor) != null;
   }

   public void dripBloodFloor(float var1) {
      Integer var2 = 32 + Rand.Next(8);
      this.DoFloorSplat(this.getCurrentSquare(), "BloodFloor_" + var2, Rand.Next(2) == 0, 0.0F, var1);
      this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(8));
   }

   public void splatBloodFloorBig(float var1) {
      if (this.getCurrentSquare() != null) {
         this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
      }

   }

   public void splatBloodFloor(float var1) {
      if (this.getCurrentSquare() != null) {
         if (this.getCurrentSquare().getChunk() != null) {
            if (this.Health <= 0.0F && Rand.Next(10) == 0) {
               this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
            }

            if (Rand.Next(14) == 0) {
               this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(8));
            }

            if (Rand.Next(50) == 0) {
               this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
            }

         }
      }
   }

   public void Scratched() {
   }

   public void Bitten() {
   }

   public int getThreatLevel() {
      int var1 = this.LocalRelevantEnemyList.size();
      var1 += this.VeryCloseEnemyList.size() * 10;
      if (var1 > 20) {
         return 3;
      } else if (var1 > 10) {
         return 2;
      } else {
         return var1 > 0 ? 1 : 0;
      }
   }

   public boolean InBuildingWith(IsoGameCharacter var1) {
      if (this.getCurrentSquare() == null) {
         return false;
      } else if (var1.getCurrentSquare() == null) {
         return false;
      } else if (this.getCurrentSquare().getRoom() == null) {
         return false;
      } else if (var1.getCurrentSquare().getRoom() == null) {
         return false;
      } else {
         return var1.getCurrentSquare().getRoom().building == this.getCurrentSquare().getRoom().building;
      }
   }

   public boolean InRoomWith(IsoGameCharacter var1) {
      if (this.getCurrentSquare() == null) {
         return false;
      } else if (var1.getCurrentSquare() == null) {
         return false;
      } else if (this.getCurrentSquare().getRoom() == null) {
         return false;
      } else if (var1.getCurrentSquare().getRoom() == null) {
         return false;
      } else {
         return var1.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom();
      }
   }

   public boolean isDead() {
      return this.Health <= 0.0F || this.BodyDamage.getHealth() <= 0.0F;
   }

   public boolean isAlive() {
      return !this.isDead();
   }

   public boolean IsInBuilding(IsoBuilding var1) {
      if (this.getCurrentSquare() == null) {
         return false;
      } else if (this.getCurrentSquare().getRoom() == null) {
         return false;
      } else {
         return this.getCurrentSquare().getRoom().building == var1;
      }
   }

   public void Seen(Stack var1) {
      synchronized(this.LocalList) {
         this.LocalList.clear();
         this.LocalList.addAll(var1);
      }
   }

   public boolean CanSee(IsoMovingObject var1) {
      return LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), false) != LosUtil.TestResults.Blocked;
   }

   public IsoGridSquare getLowDangerInVicinity(int var1, int var2) {
      float var3 = -1000000.0F;
      IsoGridSquare var4 = null;

      for(int var5 = 0; var5 < var1; ++var5) {
         float var6 = 0.0F;
         int var7 = Rand.Next(-var2, var2);
         int var8 = Rand.Next(-var2, var2);
         IsoGridSquare var9 = this.getCell().getGridSquare((int)this.getX() + var7, (int)this.getY() + var8, (int)this.getZ());
         if (var9 != null && var9.isFree(true)) {
            float var10 = (float)var9.getMovingObjects().size();
            if (var9.getE() != null) {
               var10 += (float)var9.getE().getMovingObjects().size();
            }

            if (var9.getS() != null) {
               var10 += (float)var9.getS().getMovingObjects().size();
            }

            if (var9.getW() != null) {
               var10 += (float)var9.getW().getMovingObjects().size();
            }

            if (var9.getN() != null) {
               var10 += (float)var9.getN().getMovingObjects().size();
            }

            var6 -= var10 * 1000.0F;
            if (var6 > var3) {
               var3 = var6;
               var4 = var9;
            }
         }
      }

      return var4;
   }

   public void SetAnim(int var1) {
      if (!this.bUseParts) {
         this.sprite.CurrentAnim = (IsoAnim)this.sprite.AnimStack.get(var1);
      } else {
         this.legsSprite.CurrentAnim = (IsoAnim)this.legsSprite.AnimStack.get(var1);
         if (this.torsoSprite != null) {
            this.torsoSprite.CurrentAnim = (IsoAnim)this.torsoSprite.AnimStack.get(var1);
         }

         if (this.headSprite != null) {
            this.headSprite.CurrentAnim = (IsoAnim)this.headSprite.AnimStack.get(var1);
         }

         if (this.bottomsSprite != null) {
            this.bottomsSprite.CurrentAnim = (IsoAnim)this.bottomsSprite.AnimStack.get(var1);
         }

         if (this.hairSprite != null) {
            this.hairSprite.CurrentAnim = (IsoAnim)this.hairSprite.AnimStack.get(var1);
         }

         if (this.shoeSprite != null) {
            this.shoeSprite.CurrentAnim = (IsoAnim)this.shoeSprite.AnimStack.get(var1);
         }

         if (this.topSprite != null) {
            this.topSprite.CurrentAnim = (IsoAnim)this.topSprite.AnimStack.get(var1);
         }

         this.EnforceAnims();
      }

   }

   private void EnforceAnims() {
      if (this.headSprite != null) {
      }

      if (this.bottomsSprite != null) {
      }

      if (this.shoeSprite != null) {
      }

      if (this.topSprite != null) {
      }

   }

   public void Anger(int var1) {
      float var2 = 10.0F;
      if ((float)Rand.Next(100) < var2) {
         var1 *= 2;
      }

      var1 = (int)((float)var1 * (this.stats.getStress() + 1.0F));
      var1 = (int)((float)var1 * (this.BodyDamage.getUnhappynessLevel() / 100.0F + 1.0F));
      Stats var10000 = this.stats;
      var10000.Anger += (float)var1 / 100.0F;
   }

   public boolean hasEquipped(String var1) {
      if (var1.contains(".")) {
         var1 = var1.split("\\.")[1];
      }

      if (this.leftHandItem != null && this.leftHandItem.getType().equals(var1)) {
         return true;
      } else {
         return this.rightHandItem != null && this.rightHandItem.getType().equals(var1);
      }
   }

   public void setDir(IsoDirections var1) {
      this.dir = var1;
      this.getVectorFromDirection(this.angle);
   }

   public void SetClothing(Item.ClothingBodyLocation var1, String var2, String var3) {
      if (var1 == Item.ClothingBodyLocation.Top) {
         if (var2 == null) {
            this.topSprite = null;
         } else {
            this.topSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
            if (this.bFemale) {
               if (var3 != null && var3.contains("Shirt")) {
                  var3 = var3.replace("Shirt", "Blouse");
               }

               var3 = "F_" + var3;
            }

            this.DoCharacterPart(var3, this.topSprite);
            if (this.ClothingItem_Torso != null) {
               this.topSprite.TintMod.r = this.descriptor.topColor.r;
               this.topSprite.TintMod.g = this.descriptor.topColor.g;
               this.topSprite.TintMod.b = this.descriptor.topColor.b;
               this.topSprite.TintMod.desaturate(0.5F);
            }
         }
      }

      if (var1 == Item.ClothingBodyLocation.Bottoms) {
         if (var2 == null) {
            this.bottomsSprite = null;
         } else {
            this.bottomsSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
            if (this.bFemale) {
               var3 = "F_" + var3;
            }

            this.DoCharacterPart(var3, this.bottomsSprite);
            if (this.ClothingItem_Legs != null) {
               this.bottomsSprite.TintMod.r = this.descriptor.trouserColor.r;
               this.bottomsSprite.TintMod.g = this.descriptor.trouserColor.g;
               this.bottomsSprite.TintMod.b = this.descriptor.trouserColor.b;
               this.bottomsSprite.TintMod.desaturate(0.5F);
            }
         }
      }

      if (var1 == Item.ClothingBodyLocation.Shoes) {
         if (var2 == null) {
            this.shoeSprite = null;
         } else {
            this.shoeSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
            if (this.bFemale) {
               var3 = "F_" + var3;
            }

            this.DoCharacterPart(var3, this.shoeSprite);
         }
      }

   }

   public void Callout() {
      String var1 = "";
      if (Core.getInstance().getGameMode().equals("Tutorial")) {
         var1 = Translator.getText("IGUI_PlayerText_CalloutTutorial");
      } else {
         switch(Rand.Next(3)) {
         case 0:
            var1 = Translator.getText("IGUI_PlayerText_Callout1");
            break;
         case 1:
            var1 = Translator.getText("IGUI_PlayerText_Callout2");
            break;
         case 2:
            var1 = Translator.getText("IGUI_PlayerText_Callout3");
         }
      }

      ChatManager.getInstance().sendMessageToChat(((IsoPlayer)this).getUsername(), ChatType.say, var1);
      this.callOut = true;
   }

   public void Kill(IsoGameCharacter var1) {
      this.Health = -1.0F;
      this.DoDeath((HandWeapon)null, var1);
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.getVectorFromDirection(this.angle);
      if (var1.get() == 1) {
         this.descriptor = new SurvivorDesc(true);
         this.descriptor.load(var1, var2, this);
         this.bFemale = this.descriptor.isFemale();
      }

      ArrayList var3 = this.inventory.load(var1, var2, false);
      this.savedInventoryItems.clear();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         this.savedInventoryItems.add(var3.get(var4));
      }

      this.Asleep = var1.get() == 1;
      this.ForceWakeUpTime = var1.getFloat();
      int var5;
      if (!(this instanceof IsoZombie)) {
         this.NumberOfPerksToPick = var1.getInt();
         this.stats.load(var1, var2);
         this.BodyDamage.load(var1, var2);
         this.xp.load(var1, var2);
         if (var2 >= 26) {
            ArrayList var8 = this.inventory.IncludingObsoleteItems;
            var5 = var1.getInt();
            if (var5 >= 0 && var5 < var8.size()) {
               this.leftHandItem = (InventoryItem)var8.get(var5);
            }

            var5 = var1.getInt();
            if (var5 >= 0 && var5 < var8.size()) {
               this.rightHandItem = (InventoryItem)var8.get(var5);
            }
         } else {
            if (var1.get() == 1) {
               this.leftHandItem = this.inventory.getBestCondition(GameWindow.ReadString(var1));
            }

            if (var1.get() == 1) {
               this.rightHandItem = this.inventory.getBestCondition(GameWindow.ReadString(var1));
            }
         }

         this.setEquipParent((InventoryItem)null, this.leftHandItem);
         this.setEquipParent((InventoryItem)null, this.rightHandItem);
      }

      boolean var9 = var1.get() == 1;
      if (var9) {
         this.SetOnFire();
      }

      if (var2 >= 40) {
         this.DepressEffect = var1.getFloat();
         this.DepressFirstTakeTime = var1.getFloat();
         this.BetaEffect = var1.getFloat();
         this.BetaDelta = var1.getFloat();
         this.PainEffect = var1.getFloat();
         this.PainDelta = var1.getFloat();
         this.SleepingTabletEffect = var1.getFloat();
         this.SleepingTabletDelta = var1.getFloat();
      } else {
         this.DepressEffect = (float)var1.getInt();
         this.DepressFirstTakeTime = (float)var1.getInt();
      }

      var5 = var1.getInt();

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         IsoGameCharacter.ReadBook var7 = new IsoGameCharacter.ReadBook();
         var7.fullType = GameWindow.ReadString(var1);
         var7.alreadyReadPages = var1.getInt();
         this.ReadBooks.add(var7);
      }

      if (var2 >= 44) {
         this.reduceInfectionPower = var1.getFloat();
      }

      if (var2 >= 62) {
         var6 = var1.getInt();

         for(int var10 = 0; var10 < var6; ++var10) {
            this.knownRecipes.add(GameWindow.ReadString(var1));
         }
      }

      if (var2 >= 90) {
         this.lastHourSleeped = var1.getInt();
      }

      if (var2 >= 97) {
         this.timeSinceLastSmoke = var1.getFloat();
      }

      if (var2 >= 136) {
         this.setUnlimitedCarry(var1.get() == 1);
         this.setBuildCheat(var1.get() == 1);
         this.setHealthCheat(var1.get() == 1);
         this.setMechanicsCheat(var1.get() == 1);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      if (this.descriptor == null) {
         var1.put((byte)0);
      } else {
         var1.put((byte)1);
         this.descriptor.save(var1);
      }

      ArrayList var2 = this.inventory.save(var1, false, this);
      this.savedInventoryItems.clear();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.savedInventoryItems.add(var2.get(var3));
      }

      var1.put((byte)(this.Asleep ? 1 : 0));
      var1.putFloat(this.ForceWakeUpTime);
      if (!(this instanceof IsoZombie)) {
         var1.putInt(this.NumberOfPerksToPick);
         this.stats.save(var1);
         this.BodyDamage.save(var1);
         this.xp.save(var1);
         if (this.leftHandItem != null) {
            var1.putInt(this.inventory.getItems().indexOf(this.leftHandItem));
         } else {
            var1.putInt(-1);
         }

         if (this.rightHandItem != null) {
            var1.putInt(this.inventory.getItems().indexOf(this.rightHandItem));
         } else {
            var1.putInt(-1);
         }
      }

      var1.put((byte)(this.OnFire ? 1 : 0));
      var1.putFloat(this.DepressEffect);
      var1.putFloat(this.DepressFirstTakeTime);
      var1.putFloat(this.BetaEffect);
      var1.putFloat(this.BetaDelta);
      var1.putFloat(this.PainEffect);
      var1.putFloat(this.PainDelta);
      var1.putFloat(this.SleepingTabletEffect);
      var1.putFloat(this.SleepingTabletDelta);
      var1.putInt(this.ReadBooks.size());
      Iterator var5 = this.ReadBooks.iterator();

      while(var5.hasNext()) {
         IsoGameCharacter.ReadBook var4 = (IsoGameCharacter.ReadBook)var5.next();
         GameWindow.WriteString(var1, var4.fullType);
         var1.putInt(var4.alreadyReadPages);
      }

      var1.putFloat(this.reduceInfectionPower);
      var1.putInt(this.knownRecipes.size());
      var5 = this.knownRecipes.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         GameWindow.WriteString(var1, var6);
      }

      var1.putInt(this.lastHourSleeped);
      var1.putFloat(this.timeSinceLastSmoke);
      var1.put((byte)(this.isUnlimitedCarry() ? 1 : 0));
      var1.put((byte)(this.isBuildCheat() ? 1 : 0));
      var1.put((byte)(this.isHealthCheat() ? 1 : 0));
      var1.put((byte)(this.isMechanicsCheat() ? 1 : 0));
   }

   public ChatElement getChatElement() {
      return this.chatElement;
   }

   public void StartAction(BaseAction var1) {
      this.CharacterActions.clear();
      this.CharacterActions.push(var1);
      if (var1.valid()) {
         var1.start();
      }

   }

   public void QueueAction(BaseAction var1) {
   }

   public void StopAllActionQueue() {
      if (!this.CharacterActions.isEmpty()) {
         BaseAction var1 = (BaseAction)this.CharacterActions.get(0);
         var1.stop();
         this.CharacterActions.clear();
         if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
            UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
         }

      }
   }

   public void StopAllActionQueueRunning() {
      if (!this.CharacterActions.isEmpty()) {
         BaseAction var1 = (BaseAction)this.CharacterActions.get(0);
         if (var1.StopOnRun) {
            var1.stop();
            this.CharacterActions.clear();
            if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
               UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
            }

         }
      }
   }

   public void StopAllActionQueueAiming() {
      if (this.CharacterActions.size() != 0) {
         BaseAction var1 = (BaseAction)this.CharacterActions.get(0);
         if (var1.StopOnAim) {
            var1.stop();
            this.CharacterActions.clear();
            if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
               UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
            }

         }
      }
   }

   public void StopAllActionQueueWalking() {
      if (this.CharacterActions.size() != 0) {
         BaseAction var1 = (BaseAction)this.CharacterActions.get(0);
         if (var1.StopOnWalk) {
            var1.stop();
            this.CharacterActions.clear();
            if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
               UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
            }

         }
      }
   }

   public IsoGameCharacter(IsoCell var1, float var2, float var3, float var4) {
      super(var1, false);
      if (this.emitter == null && !(this instanceof IsoSurvivor)) {
         this.emitter = (BaseCharacterSoundEmitter)(!Core.SoundDisabled && !GameServer.bServer ? new CharacterSoundEmitter(this) : new DummyCharacterSoundEmitter(this));
      }

      this.pvpTexture = Texture.getSharedTexture("media/ui/Skull.png");
      if (var2 != 0.0F || var3 != 0.0F || var4 != 0.0F) {
         if (this.getCell().isSafeToAdd()) {
            this.getCell().getObjectList().add(this);
         } else {
            this.getCell().getAddList().add(this);
         }
      }

      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this.sprite);
      }

      if (!(this instanceof IsoZombie)) {
         this.Moodles = new Moodles(this);
         this.xp = new IsoGameCharacter.XP(this);
      }

      this.Patience = Rand.Next(this.PatienceMin, this.PatienceMax);
      this.BodyDamage = new BodyDamage(this);
      this.x = var2 + 0.5F;
      this.y = var3 + 0.5F;
      this.z = var4;
      this.scriptnx = this.lx = this.nx = var2;
      this.scriptny = this.ly = this.ny = var3;
      if (var1 != null) {
         this.current = this.getCell().getGridSquare((int)var2, (int)var3, (int)var4);
      }

      this.offsetY = 0.0F;
      this.offsetX = 0.0F;
      this.stateMachine = new StateMachine(this);
      this.defaultState = IdleState.instance();
      this.inventory.parent = this;
      this.inventory.setExplored(true);
      this.chatElement = new ChatElement(this, 1, "character");
      if (GameClient.bClient || GameServer.bServer) {
         this.namesPrefix.put("admin", "[col=255,0,0]Admin[/] ");
         this.namesPrefix.put("moderator", "[col=0,128,47]Moderator[/] ");
         this.namesPrefix.put("overseer", "[col=26,26,191]Overseer[/] ");
         this.namesPrefix.put("gm", "[col=213,123,23]GM[/] ");
         this.namesPrefix.put("observer", "[col=128,128,128]Observer[/] ");
      }

   }

   public void SleepingTablet(float var1) {
      this.SleepingTabletEffect = 6600.0F;
      this.SleepingTabletDelta += var1;
   }

   public void BetaBlockers(float var1) {
      this.BetaEffect = 6600.0F;
      this.BetaDelta += var1;
   }

   public void BetaAntiDepress(float var1) {
      if (this.DepressEffect == 0.0F) {
         this.DepressFirstTakeTime = 10000.0F;
      }

      this.DepressEffect = 6600.0F;
      this.DepressDelta += var1;
   }

   public void PainMeds(float var1) {
      this.PainEffect = 300.0F;
      this.PainDelta += var1;
   }

   public void DoCharacterPart(String var1, IsoSprite var2) {
      var2.LoadFrames(var1, szRun, 11);
      var2.LoadFrames(var1, szRun_Weapon2, 11);
      var2.LoadFrames(var1, szIdle_Weapon2, 6);
      var2.LoadFrames(var1, szWalk, 11);
      var2.LoadFramesReverseAltName(var1, szWalk, szWalk_R, 11);
      var2.LoadFrames(var1, szStrafe_Aim_Bat, 11);
      var2.LoadFrames(var1, szStrafe_Aim_Handgun, 11);
      var2.LoadFrames(var1, szStrafe_Aim_Rifle, 11);
      var2.LoadFrames(var1, szStrafe_Aim_Stab, 11);
      var2.LoadFrames(var1, szAttack_Jaw_Stab, 30);
      var2.LoadFrames(var1, szStrafe, 11);
      var2.LoadFrames(var1, szWalk_Aim_Stab, 11);
      var2.LoadFrames(var1, szWalk_Aim_Bat, 11);
      var2.LoadFrames(var1, szWalk_Aim_Handgun, 11);
      var2.LoadFrames(var1, szWalk_Aim_Rifle, 11);
      var2.LoadFramesReverseAltName(var1, szWalk_Aim_Rifle, szWalk_Aim_Rifle_R, 11);
      var2.LoadFramesReverseAltName(var1, szWalk_Aim_Stab, szWalk_Aim_Stab_R, 11);
      var2.LoadFramesReverseAltName(var1, szStrafe, szStrafe_R, 11);
      var2.LoadFramesReverseAltName(var1, szWalk_Aim_Bat, szWalk_Aim_Bat_R, 11);
      var2.LoadFramesReverseAltName(var1, szStrafe_Aim_Rifle, szStrafe_Aim_Rifle_R, 11);
      var2.LoadFramesReverseAltName(var1, szStrafe_Aim_Bat, szStrafe_Aim_Bat_R, 11);
      var2.LoadFramesReverseAltName(var1, szWalk_Aim_Handgun, szWalk_Aim_Handgun_R, 11);
      var2.LoadFramesReverseAltName(var1, szStrafe_Aim_Handgun, szStrafe_Aim_Handgun_R, 11);
      var2.LoadFramesReverseAltName(var1, szStrafe_Aim_Stab, szStrafe_Aim_Stab_R, 11);
      var2.LoadFrames(var1, szZombieDeath, 14);
      var2.LoadFrames(var1, szAttack_Bat, 14);
      var2.LoadFrames(var1, szAttack_Sledgehammer, 14);
      var2.LoadFrames(var1, szAttack_Handgun, 6);
      var2.LoadFrames(var1, szAttack_Rifle, 5);
      var2.LoadFrames(var1, szAttack_Stab, 8);
      var2.LoadFrames(var1, szAttack_Shove, 11);
      var2.LoadFrames(var1, szAttack_Floor_Bat, 15);
      var2.LoadFrames(var1, szAttack_Floor_Handgun, 6);
      var2.LoadFrames(var1, szAttack_Floor_Rifle, 6);
      var2.LoadFrames(var1, szAttack_Floor_Stab, 16);
      var2.LoadFrames(var1, szAttack_Floor_Stamp, 16);
      var2.LoadFrames(var1, szClimb_WindowA, 10);
      var2.LoadFrames(var1, szClimb_WindowB, 10);
      var2.LoadFrames(var1, szWindowOpenIn, 5);
      var2.LoadFrames(var1, szWindowOpenStruggle, 10);
      var2.LoadFrames(var1, szWindowOpenSuccess, 10);
      var2.LoadFrames(var1, szWindowSmash, 10);
      var2.LoadFrames(var1, szSatChairIn, 9);
      var2.LoadFrames(var1, szSatChairOut, 9);
      var2.LoadFrames(var1, szSatChairIdle, 1);
      var2.LoadFrames(var1, szClimb_Rope, 11);
      var2.LoadFramesReverseAltName(var1, szClimb_Rope, szClimbDown_Rope, 11);
      var2.LoadFrames(var1, szZombieGetUp, 15);
      var2.LoadFrames(var1, szIdle, 6);
   }

   public void DoZombiePart(String var1, IsoSprite var2) {
      var2.LoadFrames(var1, "ZombieDoor", 11);
      var2.LoadFrames(var1, "ZombieBite", 20);
      var2.LoadFrames(var1, "ZombieDeath", 14);
      var2.LoadFrames(var1, "ZombieStaggerBack", 10);
      var2.LoadFrames(var1, "ZombieGetUp", 15);
      var2.LoadFrames(var1, "Climb_WindowA", 10);
      var2.LoadFrames(var1, "Climb_WindowB", 10);
      var2.LoadFrames(var1, "ZombieCrawl", 11);
      var2.LoadFrames(var1, "Zombie_CrawlLunge", 20);
      var2.LoadFrames(var1, "ZombieDeadToCrawl", 20);
      var2.LoadFrames(var1, "Zombie_CrawlTurnL", 11);
      var2.LoadFrames(var1, "Zombie_CrawlTurnR", 11);
      var2.LoadFrames(var1, "ZombieIdle", 20);
      var2.LoadFrames(var1, "Run", 11);
      var2.LoadFrames(var1, "ZombieWalk1", 11);
      var2.LoadFrames(var1, "ZombieWalk2", 11);
      var2.LoadFrames(var1, "ZombieWalk3", 11);
   }

   public void DoZombiePart(String var1, String var2, IsoSprite var3) {
      var3.LoadFrames(var1, "ZombieDoor", 11);
      var3.LoadFrames(var1, "ZombieBite", 20);
      var3.LoadFrames(var1, "ZombieDeath", 14);
      var3.LoadFrames(var1, "ZombieStaggerBack", 10);
      var3.LoadFrames(var1, "ZombieGetUp", 15);
      var3.LoadFrames(var1, "Climb_WindowA", 10);
      var3.LoadFrames(var1, "Climb_WindowB", 10);
      var3.LoadFrames(var1, "ZombieWalk1", 11);
      var3.LoadFrames(var1, "ZombieWalk2", 11);
      var3.LoadFrames(var1, "ZombieWalk3", 11);
      var3.LoadFrames(var1, "ZombieIdle", 20);
      var3.LoadFrames(var1, "ZombieCrawl", 11);
      var3.LoadFrames(var1, "Zombie_CrawlLunge", 20);
      var3.LoadFrames(var1, "ZombieDeadToCrawl", 20);
      var3.LoadFrames(var1, "Zombie_CrawlTurnL", 11);
      var3.LoadFrames(var1, "Zombie_CrawlTurnR", 11);
      var3.LoadFrames(var1, "Run", 11);
   }

   public void initSpritePartsEmpty() {
      this.InitSpriteParts(this.descriptor, this.descriptor.legs, this.descriptor.torso, this.descriptor.head, this.descriptor.top, this.descriptor.bottoms, this.descriptor.shoes, this.descriptor.skinpal, this.descriptor.toppal, this.descriptor.bottomspal, this.descriptor.shoespal, this.descriptor.hair, this.descriptor.extra);
   }

   public void InitSpriteParts(SurvivorDesc var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12, ArrayList var13) {
      this.sprite.AnimMap.clear();
      this.sprite.AnimStack.clear();
      this.sprite.CurrentAnim = null;
      this.legsSprite = this.sprite;
      this.legsSprite.name = var3;
      if (DropShadow == null) {
         DropShadow = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         DropShadow.LoadFramesPageSimple("dropshadow", "dropshadow", "dropshadow", "dropshadow");
      }

      this.DoCharacterPart(var3, this.legsSprite);
      if (var9 != null && !var9.isEmpty()) {
         if (this.bFemale) {
            var9 = var9.replace("Shirt", "Blouse");
            var9 = "F_" + var9;
         }

         this.topSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.DoCharacterPart(var9, this.topSprite);
      }

      if (this.ClothingItem_Torso != null) {
         this.topSprite.TintMod.r = var1.topColor.r;
         this.topSprite.TintMod.g = var1.topColor.g;
         this.topSprite.TintMod.b = var1.topColor.b;
         this.topSprite.TintMod.desaturate(0.5F);
      }

      if (var10 != null && !var10.isEmpty()) {
         if (this.bFemale) {
            var10 = "F_" + var10;
         }

         this.bottomsSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.DoCharacterPart(var10, this.bottomsSprite);
      }

      if (this.ClothingItem_Legs != null) {
         this.bottomsSprite.TintMod.r = var1.trouserColor.r;
         this.bottomsSprite.TintMod.g = var1.trouserColor.g;
         this.bottomsSprite.TintMod.b = var1.trouserColor.b;
         this.bottomsSprite.TintMod.desaturate(0.5F);
      }

      this.hairSprite = null;
      if (!"none".equals(var12)) {
         var12 = var12.replace("Black", "White");
         var12 = var12.replace("Blonde", "White");
         var12 = var12.replace("Brown", "White");
         var12 = var12.replace("Red", "White");
         var1.hair = var12;
         this.hairSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.DoCharacterPart(var12, this.hairSprite);
         this.hairSprite.TintMod.r = var1.hairColor.r;
         this.hairSprite.TintMod.g = var1.hairColor.g;
         this.hairSprite.TintMod.b = var1.hairColor.b;
         this.hairSprite.TintMod.desaturate(0.5F);
      }

      this.extraSprites.clear();

      for(int var14 = 0; var14 < var13.size(); ++var14) {
         IsoSprite var15 = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.DoCharacterPart((String)var13.get(var14), var15);
         this.extraSprites.add(var15);
         var15.TintMod.r = var1.hairColor.r;
         var15.TintMod.g = var1.hairColor.g;
         var15.TintMod.b = var1.hairColor.b;
         var15.TintMod.desaturate(0.5F);
      }

      this.bUseParts = true;
   }

   public void InitSpritePartsZombie() {
      SurvivorDesc var1 = this.descriptor;
      this.InitSpritePartsZombie(((IsoZombie)this).SpriteName, var1, var1.legs, var1.torso, var1.head, var1.top, var1.bottoms, var1.shoes, var1.skinpal, var1.toppal, var1.bottomspal, var1.shoespal, var1.hair, var1.extra);
   }

   public void InitSpritePartsZombie(String var1, SurvivorDesc var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12, String var13, ArrayList var14) {
      this.sprite.AnimMap.clear();
      this.sprite.AnimStack.clear();
      this.sprite.CurrentAnim = null;
      this.legsSprite = this.sprite;
      this.legsSprite.name = var4;
      if (this instanceof IsoZombie) {
         ((IsoZombie)this).ZombieID = Rand.Next(10000);
      }

      if (IsoSprite.HasCache(var1)) {
         this.legsSprite.LoadCache(var1);
      } else {
         this.DoZombiePart(var1, this.descriptor.torso, this.legsSprite);
         this.legsSprite.CacheAnims(var1);
      }

      this.legsSprite.TintMod.r = var2.skinColor.r;
      this.legsSprite.TintMod.g = var2.skinColor.g;
      this.legsSprite.TintMod.b = var2.skinColor.b;
      this.legsSprite.TintMod.desaturate(0.5F);
      if (this.bFemale && var10 != null) {
         if (var10 != null && var10.contains("Shirt")) {
            var10 = var10.replace("Shirt", "Blouse");
         }

         var10 = "F_" + var10;
      }

      this.topSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.topSprite.TintMod.r = var2.topColor.r;
      this.topSprite.TintMod.g = var2.topColor.g;
      this.topSprite.TintMod.b = var2.topColor.b;
      this.topSprite.TintMod.desaturate(0.6F);
      if (IsoSprite.HasCache(var10 + "z")) {
         this.topSprite.LoadCache(var10 + "z");
      } else {
         this.DoZombiePart(var10, this.topSprite);
         this.topSprite.CacheAnims(var10 + "z");
      }

      this.extraSprites.clear();

      for(int var15 = 0; var15 < var14.size(); ++var15) {
         IsoSprite var16 = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         if (IsoSprite.HasCache((String)var14.get(var15) + "z")) {
            var16.LoadCache((String)var14.get(var15) + "z");
         } else {
            this.DoZombiePart((String)var14.get(var15), var16);
            var16.CacheAnims((String)var14.get(var15) + "z");
         }

         var16.TintMod.r = var2.hairColor.r;
         var16.TintMod.g = var2.hairColor.g;
         var16.TintMod.b = var2.hairColor.b;
         var16.TintMod.desaturate(0.6F);
         this.extraSprites.add(var16);
      }

      this.bottomsSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.bottomsSprite.TintMod.r = var2.trouserColor.r;
      this.bottomsSprite.TintMod.g = var2.trouserColor.g;
      this.bottomsSprite.TintMod.b = var2.trouserColor.b;
      this.bottomsSprite.TintMod.desaturate(0.6F);
      if (this.bFemale) {
         var11 = "F_" + var11;
      }

      if (IsoSprite.HasCache(var11 + "z")) {
         this.bottomsSprite.LoadCache(var11 + "z");
      } else {
         this.DoZombiePart(var11, this.bottomsSprite);
         this.bottomsSprite.CacheAnims(var11 + "z");
      }

      this.hairSprite = null;
      if (!"none".equals(var13)) {
         this.hairSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         if (IsoSprite.HasCache(var13 + "z")) {
            this.hairSprite.LoadCache(var13 + "z");
         } else {
            this.DoZombiePart(var13, this.hairSprite);
            this.hairSprite.CacheAnims(var13 + "z");
         }

         this.hairSprite.TintMod.r = var2.hairColor.r;
         this.hairSprite.TintMod.g = var2.hairColor.g;
         this.hairSprite.TintMod.b = var2.hairColor.b;
         this.hairSprite.TintMod.desaturate(0.6F);
      }

      this.bUseParts = true;
   }

   public boolean HasTrait(String var1) {
      return this.Traits.contains(var1);
   }

   public void ApplyInBedOffset(boolean var1) {
      if (var1) {
         if (!this.bOnBed) {
            this.offsetX -= 20.0F;
            this.offsetY += 21.0F;
            this.bOnBed = true;
         }
      } else if (this.bOnBed) {
         this.offsetX += 20.0F;
         this.offsetY -= 21.0F;
         this.bOnBed = false;
      }

   }

   public void Dressup(SurvivorDesc var1) {
      InventoryItem var2 = null;
      InventoryItem var3 = null;
      InventoryItem var4 = null;
      boolean var5 = this instanceof IsoZombie;
      if (var1.bottomspal != null && !var1.bottomspal.isEmpty()) {
         var2 = this.inventory.AddItem((InventoryItem)Clothing.CreateFromSprite(var1.bottomspal.replace("_White", "")));
         var2.col = new Color(var1.trouserColor.r, var1.trouserColor.g, var1.trouserColor.b);
         if (var2 instanceof Clothing && var5 && SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
            ((Clothing)var2).setDirtyness((float)Rand.Next(0, 100));
            ((Clothing)var2).setBloodLevel((float)Rand.Next(20, 100));
            if (((Clothing)var2).isBloody() && Rand.Next(0, 100) > 95) {
               ((Clothing)var2).setInfected(true);
            }
         }
      }

      if (var1.toppal != null && !"".equals(var1.toppal)) {
         var3 = this.inventory.AddItem((InventoryItem)Clothing.CreateFromSprite(var1.toppal.replace("_White", "")));
         var3.col = new Color(var1.topColor.r, var1.topColor.g, var1.topColor.b);
         if (var3 instanceof Clothing && var5 && SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
            ((Clothing)var3).setDirtyness((float)Rand.Next(0, 100));
            ((Clothing)var3).setBloodLevel((float)Rand.Next(20, 100));
            if (((Clothing)var3).isBloody() && Rand.Next(0, 100) > 95) {
               ((Clothing)var3).setInfected(true);
            }
         }
      }

      if (var1.shoes != null && !var1.shoes.isEmpty()) {
         var4 = this.inventory.AddItem((InventoryItem)Clothing.CreateFromSprite("Shoes"));
         var4.col = new Color(64, 64, 64);
      }

      if (var4 != null) {
         this.ClothingItem_Feet = var4;
      }

      if (var3 != null) {
         this.ClothingItem_Torso = var3;
      }

      if (var2 != null) {
         this.ClothingItem_Legs = var2;
      }

   }

   public void Dressup(InventoryItem var1, InventoryItem var2, InventoryItem var3) {
      if (var3 != null) {
         this.ClothingItem_Feet = var3;
      }

      if (var2 != null) {
         this.ClothingItem_Torso = var2;
      }

      if (var1 != null) {
         this.ClothingItem_Legs = var1;
      }

   }

   public void PlayAnimNoReset(String var1) {
      if (var1 != null) {
         if ("Run".equals(var1)) {
            this.def.setFrameSpeedPerFrame(0.24F);
         } else if (var1.contains("Walk")) {
            this.def.setFrameSpeedPerFrame(0.2F);
         } else if (var1.contains("Strafe")) {
            this.def.setFrameSpeedPerFrame(0.05F);
         }

         if (!this.sprite.CurrentAnim.name.equals("Die")) {
            if (!this.bUseParts) {
               this.sprite.PlayAnimNoReset(var1);
            } else {
               this.legsSprite.PlayAnimNoReset(var1);
               if (this.torsoSprite != null) {
                  this.torsoSprite.PlayAnimNoReset(var1);
               }

               if (this.headSprite != null) {
                  this.headSprite.PlayAnimNoReset(var1);
               }

               if (this.bottomsSprite != null) {
                  this.bottomsSprite.PlayAnimNoReset(var1);
               }

               if (this.shoeSprite != null) {
                  this.shoeSprite.PlayAnimNoReset(var1);
               }

               if (this.topSprite != null) {
                  this.topSprite.PlayAnimNoReset(var1);
               }

               if (this.hairSprite != null) {
                  this.hairSprite.PlayAnimNoReset(var1);
               }

               for(int var2 = 0; var2 < this.extraSprites.size(); ++var2) {
                  ((IsoSprite)this.extraSprites.get(var2)).PlayAnimNoReset(var1);
               }
            }

         }
      }
   }

   public void PlayAnim(String var1) {
      if (var1 != null) {
         if (!"Kate".equals(this.getScriptName())) {
            this.def.Looped = true;
            this.def.Finished = false;
            if (var1.contains("Run")) {
               this.def.setFrameSpeedPerFrame(0.34F);
            } else if ("Walk".equals(var1)) {
               this.def.setFrameSpeedPerFrame(0.25F);
            }

            if (this.sprite != null && this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.name.equals("Die")) {
               if (!this.bUseParts) {
                  this.sprite.PlayAnim(var1);
               } else {
                  IsoAnim var2 = this.legsSprite.CurrentAnim;
                  this.legsSprite.PlayAnim(var1);
                  if (this.torsoSprite != null) {
                     this.torsoSprite.PlayAnim(var1);
                  }

                  if (this.headSprite != null) {
                     this.headSprite.PlayAnim(var1);
                  }

                  if (this.bottomsSprite != null) {
                     this.bottomsSprite.PlayAnim(var1);
                  }

                  if (this.hairSprite != null) {
                     this.hairSprite.PlayAnim(var1);
                  }

                  if (this.shoeSprite != null) {
                     this.shoeSprite.PlayAnim(var1);
                  }

                  if (this.topSprite != null) {
                     this.topSprite.PlayAnim(var1);
                  }

                  for(int var3 = 0; var3 < this.extraSprites.size(); ++var3) {
                     ((IsoSprite)this.extraSprites.get(var3)).PlayAnim(var1);
                  }

                  if (var2 != this.legsSprite.CurrentAnim && (float)this.legsSprite.CurrentAnim.Frames.size() <= this.def.Frame) {
                     this.def.Frame = 0.0F;
                  }

                  this.EnforceAnims();
               }

            }
         }
      }
   }

   public void PlayAnimWithSpeed(String var1, float var2) {
      if (var1 != null) {
         this.PlayAnim(var1);
         this.def.setFrameSpeedPerFrame(var2);
      }
   }

   public void PlayAnimUnlooped(String var1) {
      if (var1 != null) {
         if (!this.sprite.CurrentAnim.name.equals("Die")) {
            if (!this.bUseParts) {
               this.sprite.PlayAnimUnlooped(var1);
               this.def.Looped = false;
            } else {
               IsoAnim var2 = this.legsSprite.CurrentAnim;
               this.def.Looped = false;
               if (var2.name.equals(var1) && (this.def.Finished || this.def.Frame != 0.0F)) {
                  return;
               }

               if (this instanceof IsoZombie && GameClient.bClient) {
                  boolean var3 = false;
               }

               this.legsSprite.PlayAnimUnlooped(var1);
               if (this.torsoSprite != null) {
                  this.torsoSprite.PlayAnimUnlooped(var1);
               }

               if (this.headSprite != null) {
                  this.headSprite.PlayAnimUnlooped(var1);
               }

               if (this.bottomsSprite != null) {
                  this.bottomsSprite.PlayAnimUnlooped(var1);
               }

               if (this.hairSprite != null) {
                  this.hairSprite.PlayAnimUnlooped(var1);
               }

               if (this.shoeSprite != null) {
                  this.shoeSprite.PlayAnimUnlooped(var1);
               }

               if (this.topSprite != null) {
                  this.topSprite.PlayAnimUnlooped(var1);
               }

               for(int var4 = 0; var4 < this.extraSprites.size(); ++var4) {
                  ((IsoSprite)this.extraSprites.get(var4)).PlayAnimUnlooped(var1);
               }

               this.def.Frame = 0.0F;
               this.def.Finished = false;
            }

         }
      }
   }

   public void PlayAnimFrame(String var1, int var2) {
      if (var1 != null) {
         if (this instanceof IsoLivingCharacter) {
            if (!this.bUseParts) {
               this.sprite.PlayAnim(var1);
               this.def.Frame = (float)((short)var2);
               this.def.Finished = true;
            } else {
               this.legsSprite.PlayAnimUnlooped(var1);
               this.def.Finished = true;
               if (this.torsoSprite != null) {
                  this.torsoSprite.PlayAnimUnlooped(var1);
               }

               if (this.torsoSprite != null) {
                  this.def.Finished = true;
               }

               if (this.headSprite != null) {
                  this.headSprite.PlayAnimUnlooped(var1);
                  this.def.Finished = true;
               }

               if (this.bottomsSprite != null) {
                  this.bottomsSprite.PlayAnimUnlooped(var1);
                  this.def.Finished = true;
               }

               if (this.hairSprite != null) {
                  this.hairSprite.PlayAnimUnlooped(var1);
                  this.def.Finished = true;
               }

               if (this.shoeSprite != null) {
                  this.shoeSprite.PlayAnimUnlooped(var1);
                  this.def.Finished = true;
               }

               if (this.topSprite != null) {
                  this.topSprite.PlayAnimUnlooped(var1);
                  this.def.Finished = true;
               }

               for(int var3 = 0; var3 < this.extraSprites.size(); ++var3) {
                  ((IsoSprite)this.extraSprites.get(var3)).PlayAnimUnlooped(var1);
               }

               this.def.Finished = true;
            }
         }

      }
   }

   public void SetAnimFrame(float var1, boolean var2) {
      if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null) {
         if (var1 < 0.0F) {
            this.def.Frame = 0.0F;
         } else if (var1 >= (float)this.sprite.CurrentAnim.Frames.size()) {
            this.def.Frame = (float)(this.sprite.CurrentAnim.Frames.size() - 1);
         } else {
            this.def.Frame = var1;
         }

         this.def.Finished = var2;
      }
   }

   public void DirectionFromVectorNoDiags(Vector2 var1) {
      if (!this.IgnoreMovementForDirection) {
         if (Math.abs(var1.x) < Math.abs(var1.y)) {
            tempo.x = var1.x;
            tempo.y = var1.y;
            tempo.x = 0.0F;
            tempo.normalize();
            this.dir = IsoDirections.fromAngle(tempo);
         } else {
            tempo.x = var1.x;
            tempo.y = var1.y;
            tempo.y = 0.0F;
            tempo.normalize();
            this.dir = IsoDirections.fromAngle(tempo);
         }

      }
   }

   public void DirectionFromVector(Vector2 var1) {
      if (!this.IgnoreMovementForDirection) {
         this.dir = IsoDirections.fromAngle(var1);
      }
   }

   public void DoFootstepSound(float var1) {
      var1 *= 0.8F;
      if (!(this instanceof IsoPlayer) || !((IsoPlayer)this).GhostMode) {
         if (this.getCurrentSquare() != null) {
            if (var1 != 0.0F) {
               float var2 = 0.05F;
               if (var1 < 0.06F) {
                  var2 *= 0.4F;
               } else if (var1 <= 0.07F) {
                  var2 *= 0.6F;
               } else {
                  var2 *= 0.9F;
               }

               this.footStepCounter += GameTime.instance.getMultiplier();
               this.footStepCounterMax = (float)((int)(1.0F / var1 * 1.0F));
               if (var1 <= 0.06F) {
                  this.footStepCounterMax *= 0.8F;
               }

               if (this.HasTrait("Graceful")) {
                  var2 *= 0.6F;
               }

               if (this.HasTrait("Clumsy")) {
                  var2 *= 1.2F;
               }

               var2 *= this.getLightfootMod();
               boolean var3 = false;
               if (this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk")) && this.def.NextFrame) {
                  if ((int)this.def.Frame == 0) {
                     var3 = true;
                  }

                  if ((int)this.def.Frame == 5) {
                     var3 = true;
                  }
               }

               if (var3 && var2 > 0.0F) {
                  this.footStepCounter = 0.0F;
                  this.emitter.playFootsteps("human_m");
               }

               this.def.NextFrame = false;
               int var4 = (int)(var1 * 80.0F * this.getLightfootMod());
               if (this.getCurrentSquare().getRoom() != null) {
                  var4 = (int)((float)var4 * 0.5F);
               }

               if (this.HasTrait("Graceful")) {
                  var4 = (int)((float)var4 * 0.6F);
               }

               if (this.HasTrait("Clumsy")) {
                  var4 = (int)((float)var4 * 1.6F);
               }

               if (!GameClient.bClient) {
                  var4 *= 3;
                  if (Rand.Next(4) == 0) {
                     WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), var4, var4, false);
                  } else if (Rand.Next(4) == 0) {
                     WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), (int)((float)var4 * 0.5F), (int)((float)var4 * 0.5F), false);
                  }

               }
            }
         }
      }
   }

   public boolean Eat(InventoryItem var1, float var2) {
      var2 = Math.max(0.0F, var2);
      var2 = Math.min(1.0F, var2);
      if (!(var1 instanceof Food)) {
         return false;
      } else {
         Food var3 = (Food)var1;
         if (var3.getRequireInHandOrInventory() != null) {
            InventoryItem var4 = null;

            for(int var5 = 0; var5 < var3.getRequireInHandOrInventory().size(); ++var5) {
               String var6 = (String)var3.getRequireInHandOrInventory().get(var5);
               var4 = this.getInventory().FindAndReturn(var6);
               if (var4 != null) {
                  var4.Use();
                  break;
               }
            }
         }

         if (var3.getBaseHunger() != 0.0F && var3.getHungChange() != 0.0F) {
            float var7 = var3.getBaseHunger() * var2;
            float var8 = var7 / var3.getHungChange();
            if (var8 < 0.0F) {
               var8 = 0.0F;
            }

            if (var8 > 1.0F) {
               var8 = 1.0F;
            }

            var2 = var8;
         }

         if (var3.getHungChange() * (1.0F - var2) > -0.01F) {
            var2 = 1.0F;
         }

         Stats var10000 = this.stats;
         var10000.thirst += var3.getThirstChange() * var2;
         if (this.stats.thirst < 0.0F) {
            this.stats.thirst = 0.0F;
         }

         var10000 = this.stats;
         var10000.hunger += var3.getHungerChange() * var2;
         var10000 = this.stats;
         var10000.endurance += var3.getEnduranceChange() * var2;
         var10000 = this.stats;
         var10000.stress += var3.getStressChange() * var2;
         var10000 = this.stats;
         var10000.fatigue += var3.getFatigueChange() * var2;
         if (this instanceof IsoPlayer) {
            ((IsoPlayer)this).getNutrition().setCalories(((IsoPlayer)this).getNutrition().getCalories() + var3.getCalories() * var2);
            ((IsoPlayer)this).getNutrition().setCarbohydrates(((IsoPlayer)this).getNutrition().getCarbohydrates() + var3.getCarbohydrates() * var2);
            ((IsoPlayer)this).getNutrition().setProteins(((IsoPlayer)this).getNutrition().getProteins() + var3.getProteins() * var2);
            ((IsoPlayer)this).getNutrition().setLipids(((IsoPlayer)this).getNutrition().getLipids() + var3.getLipids() * var2);
         }

         this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + var3.getPainReduction() * var2);
         this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + (float)var3.getFluReduction() * var2);
         if (this.BodyDamage.getFoodSicknessLevel() > 0.0F && (float)var3.getReduceFoodSickness() > 0.0F) {
            this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - (float)var3.getReduceFoodSickness() * var2);
            this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - (float)var3.getReduceFoodSickness() * var2);
            if (this.BodyDamage.getFoodSicknessLevel() < 0.0F) {
               this.BodyDamage.setFoodSicknessLevel(0.0F);
            }

            if (this.BodyDamage.getPoisonLevel() < 0.0F) {
               this.BodyDamage.setPoisonLevel(0.0F);
            }
         }

         this.BodyDamage.JustAteFood(var3, var2);
         if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
            GameClient.instance.eatFood((IsoPlayer)this, var3, var2);
         }

         if (var2 == 1.0F) {
            var3.setHungChange(0.0F);
            var3.UseItem();
         } else {
            var3.multiplyFoodValues(1.0F - var2);
            if ((double)var3.getHungerChange() > -0.01D) {
               var3.setHungChange(0.0F);
               var3.UseItem();
               return true;
            }
         }

         if (((Food)var1).getOnEat() != null) {
            LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(((Food)var1).getOnEat()), var1, this);
         }

         return true;
      }
   }

   public boolean Eat(InventoryItem var1) {
      return this.Eat(var1, 1.0F);
   }

   public boolean EatRemote(InventoryItem var1) {
      if (!(var1 instanceof Food)) {
         return false;
      } else {
         Food var2 = (Food)var1;
         if (var2.getRequireInHandOrInventory() != null) {
            InventoryItem var3 = null;

            for(int var4 = 0; var4 < var2.getRequireInHandOrInventory().size(); ++var4) {
               String var5 = (String)var2.getRequireInHandOrInventory().get(var4);
               var3 = this.getInventory().FindAndReturn(var5);
               if (var3 != null) {
                  var3.Use();
                  break;
               }
            }
         }

         Stats var10000 = this.stats;
         var10000.thirst += var2.getThirstChange();
         if (this.stats.thirst < 0.0F) {
            this.stats.thirst = 0.0F;
         }

         var10000 = this.stats;
         var10000.hunger += var2.getHungerChange();
         var10000 = this.stats;
         var10000.endurance += var2.getEnduranceChange();
         var10000 = this.stats;
         var10000.stress += var2.getStressChange();
         var10000 = this.stats;
         var10000.fatigue += var2.getFatigueChange();
         this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + var2.getPainReduction());
         this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + (float)var2.getFluReduction());
         if (this.BodyDamage.getFoodSicknessLevel() > 0.0F && (float)var2.getReduceFoodSickness() > 0.0F) {
            this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - (float)var2.getReduceFoodSickness());
            this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - (float)var2.getReduceFoodSickness());
            if (this.BodyDamage.getFoodSicknessLevel() < 0.0F) {
               this.BodyDamage.setFoodSicknessLevel(0.0F);
            }

            if (this.BodyDamage.getPoisonLevel() < 0.0F) {
               this.BodyDamage.setPoisonLevel(0.0F);
            }
         }

         this.BodyDamage.JustAteFood(var2);
         return true;
      }
   }

   public void FaceNextPathNode() {
      if (this.path != null) {
         if (this.pathIndex <= this.path.getLength()) {
            boolean var1 = false;
            boolean var2 = false;
            boolean var3 = false;
            boolean var4 = false;
            float var5 = (float)this.path.getX(this.pathIndex) + 0.5F;
            float var6 = (float)this.path.getY(this.pathIndex) + 0.5F;
            if (var5 > this.getX() && Math.abs(var5 - this.getX()) >= 0.1F) {
               var3 = true;
            }

            if (var5 < this.getX() && Math.abs(var5 - this.getX()) >= 0.1F) {
               var4 = true;
            }

            if (var6 > this.getY() && Math.abs(var6 - this.getY()) >= 0.1F) {
               var2 = true;
            }

            if (var6 < this.getY() && Math.abs(var6 - this.getY()) >= 0.1F) {
               var1 = true;
            }

            if (var1 && var4) {
               this.dir = IsoDirections.NW;
            } else if (var1 && var3) {
               this.dir = IsoDirections.NE;
            } else if (var2 && var3) {
               this.dir = IsoDirections.SE;
            } else if (var2 && var4) {
               this.dir = IsoDirections.SW;
            } else if (var2) {
               this.dir = IsoDirections.S;
            } else if (var1) {
               this.dir = IsoDirections.N;
            } else if (var3) {
               this.dir = IsoDirections.E;
            } else if (var4) {
               this.dir = IsoDirections.W;
            }
         }

      }
   }

   public void FaceNextPathNode(int var1, int var2) {
      tempo.x = (float)var1 + 0.5F;
      tempo.y = (float)var2 + 0.5F;
      Vector2 var10000 = tempo;
      var10000.x -= this.getX();
      var10000 = tempo;
      var10000.y -= this.getY();
      this.DirectionFromVector(tempo);
      this.getVectorFromDirection(this.angle);
   }

   public void FireCheck() {
      if (!this.OnFire) {
         if (!GameServer.bServer || !(this instanceof IsoPlayer)) {
            if (!GameClient.bClient || !(this instanceof IsoZombie)) {
               if (this instanceof IsoZombie && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
                  DebugLog.log(DebugType.Zombie, "FireCheck running on REUSABLE ZOMBIE - IGNORED " + this);
               } else {
                  if (this.square != null && this.square.getProperties().Is(IsoFlagType.burning)) {
                     if ((!(this instanceof IsoPlayer) || Rand.Next(Rand.AdjustForFramerate(70)) != 0) && !(this instanceof IsoZombie)) {
                        if (!(this instanceof IsoPlayer)) {
                           this.Health -= this.FireKillRate * GameTime.instance.getMultiplier() / 2.0F;
                        } else {
                           float var1 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F / 2.0F;
                           this.BodyDamage.ReduceGeneralHealth(var1);
                           this.BodyDamage.OnFire(true);
                           this.forceAwake();
                        }

                        if (this.Health <= 0.0F) {
                           IsoFireManager.RemoveBurningCharacter(this);
                           this.stateMachine.changeState(BurntToDeath.instance());
                           this.stateMachine.Lock = true;
                        }
                     } else {
                        this.SetOnFire();
                     }
                  }

               }
            }
         }
      }
   }

   public InventoryItem getCraftingByIndex(int var1) {
      if (var1 == 0) {
         return this.craftIngredient1;
      } else if (var1 == 1) {
         return this.craftIngredient2;
      } else if (var1 == 2) {
         return this.craftIngredient3;
      } else {
         return var1 == 3 ? this.craftIngredient4 : null;
      }
   }

   public String getPrimaryHandType() {
      return this.leftHandItem == null ? null : this.leftHandItem.getType();
   }

   public float getMoveSpeed() {
      tempo2.x = this.getX() - this.getLx();
      tempo2.y = this.getY() - this.getLy();
      return tempo2.getLength();
   }

   public String getSecondaryHandType() {
      return this.rightHandItem == null ? null : this.rightHandItem.getType();
   }

   public boolean HasItem(String var1) {
      if (var1 == null) {
         return true;
      } else {
         return var1.equals(this.getSecondaryHandType()) || var1.equals(this.getPrimaryHandType()) || this.inventory.contains(var1);
      }
   }

   public void changeState(State var1) {
      this.stateMachine.changeState(var1);
   }

   public State getCurrentState() {
      return this.stateMachine.getCurrent();
   }

   public void setLockStates(boolean var1) {
      this.stateMachine.Lock = var1;
   }

   public void Hit(HandWeapon var1, IsoGameCharacter var2, float var3, boolean var4, float var5) {
      this.Hit(var1, var2, var3, var4, var5, false);
   }

   public void Hit(HandWeapon var1, IsoGameCharacter var2, float var3, boolean var4, float var5, boolean var6) {
      if (var2 != null && var1 != null) {
         if (this.isOnFloor()) {
            var3 = 1.0F;
            var5 = 2.0F;
            var4 = false;
         }

         if (var2.legsSprite.CurrentAnim.name != null && var2.legsSprite.CurrentAnim.name.contains("Shove")) {
            var4 = true;
            var5 *= 1.5F;
         }

         LuaEventManager.triggerEvent("OnWeaponHitCharacter", var2, this, var1, var3);
         if (!LuaHookManager.TriggerHook("WeaponHitCharacter", var2, this, var1, var3)) {
            if (this.avoidDamage) {
               this.avoidDamage = false;
            } else {
               if (this.noDamage) {
                  var4 = true;
                  this.noDamage = false;
               }

               if (this instanceof IsoSurvivor && !this.EnemyList.contains(var2)) {
                  this.EnemyList.add(var2);
               }

               this.staggerTimeMod = var1.getPushBackMod() * var1.getKnockbackMod(var2) * var2.getShovingMod();
               if (this instanceof IsoZombie && Rand.Next(3) == 0) {
                  this.emitter.playSound(this.hurtSound);
                  if (GameServer.bServer) {
                     GameServer.sendZombieSound(IsoZombie.ZombieSound.Hurt, (IsoZombie)this);
                  }
               }

               WorldSoundManager.instance.addSound(var2, (int)var2.x, (int)var2.y, (int)var2.z, 5, 1);
               float var7 = var3 * var5;
               float var8 = var7 * var1.getKnockbackMod(var2) * var2.getShovingMod();
               if (var8 > 1.0F) {
                  var8 = 1.0F;
               }

               this.setHitForce(var8);
               if (var2.HasTrait("Strong") && !var1.isRanged()) {
                  this.setHitForce(this.getHitForce() * 1.4F);
               }

               if (var2.HasTrait("Weak") && !var1.isRanged()) {
                  this.setHitForce(this.getHitForce() * 0.6F);
               }

               this.AttackedBy = var2;
               float var9 = IsoUtils.DistanceTo(var2.getX(), var2.getY(), this.getX(), this.getY());
               var9 -= var1.getMinRange();
               var9 /= var1.getMaxRange(var2);
               var9 = 1.0F - var9;
               if (var9 > 1.0F) {
                  var9 = 1.0F;
               }

               this.hitDir.x = this.getX();
               this.hitDir.y = this.getY();
               Vector2 var10000 = this.hitDir;
               var10000.x -= var2.getX();
               var10000 = this.hitDir;
               var10000.y -= var2.getY();
               this.getHitDir().normalize();
               var10000 = this.hitDir;
               var10000.x *= var1.getPushBackMod();
               var10000 = this.hitDir;
               var10000.y *= var1.getPushBackMod();
               this.hitDir.rotate(var1.HitAngleMod);
               float var10 = var2.stats.endurance;
               var10 *= var2.knockbackAttackMod;
               this.hitBy = var2;
               if (var10 < 0.5F) {
                  var10 *= 1.3F;
                  if (var10 < 0.4F) {
                     var10 = 0.4F;
                  }

                  this.setHitForce(this.getHitForce() * var10);
               }

               if (!var1.isRangeFalloff()) {
                  var9 = 1.0F;
               }

               if (!var1.isShareDamage()) {
                  var3 = 1.0F;
               }

               if (var2 instanceof IsoPlayer) {
                  this.setHitForce(this.getHitForce() * 2.0F);
               }

               Vector2 var11 = tempVector2_1.set(this.getX(), this.getY());
               Vector2 var12 = tempVector2_2.set(var2.getX(), var2.getY());
               var11.x -= var12.x;
               var11.y -= var12.y;
               Vector2 var13 = this.getVectorFromDirection(tempVector2_2);
               var11.normalize();
               float var14 = var11.dot(var13);
               if (var14 > -0.3F) {
                  var7 *= 1.5F;
               }

               if (this instanceof IsoPlayer) {
                  var7 *= 0.4F;
               } else {
                  var7 *= 1.5F;
               }

               if (var1.getCategories() != null && var1.getCategories().contains("Unarmed") && var7 > 0.7F) {
                  var7 = 0.7F;
               }

               if (var1.getCategories().contains("Blade") || var1.getCategories().contains("Axe")) {
                  switch(var2.getPerkLevel(PerkFactory.Perks.Axe)) {
                  case 0:
                     var7 *= 0.3F;
                     break;
                  case 1:
                     var7 *= 0.4F;
                     break;
                  case 2:
                     var7 *= 0.5F;
                     break;
                  case 3:
                     var7 *= 0.6F;
                     break;
                  case 4:
                     var7 *= 0.7F;
                     break;
                  case 5:
                     var7 *= 0.8F;
                     break;
                  case 6:
                     var7 *= 0.9F;
                     break;
                  case 7:
                     var7 *= 1.0F;
                     break;
                  case 8:
                     var7 *= 1.1F;
                     break;
                  case 9:
                     var7 *= 1.2F;
                     break;
                  case 10:
                     var7 *= 1.3F;
                  }
               }

               if (var1.getCategories().contains("Blunt")) {
                  switch(var2.getPerkLevel(PerkFactory.Perks.Blunt)) {
                  case 0:
                     var7 *= 0.3F;
                     break;
                  case 1:
                     var7 *= 0.4F;
                     break;
                  case 2:
                     var7 *= 0.5F;
                     break;
                  case 3:
                     var7 *= 0.6F;
                     break;
                  case 4:
                     var7 *= 0.7F;
                     break;
                  case 5:
                     var7 *= 0.8F;
                     break;
                  case 6:
                     var7 *= 0.9F;
                     break;
                  case 7:
                     var7 *= 1.0F;
                     break;
                  case 8:
                     var7 *= 1.1F;
                     break;
                  case 9:
                     var7 *= 1.2F;
                     break;
                  case 10:
                     var7 *= 1.3F;
                  }
               }

               float var18 = var1.CriticalChance;
               if (var1.isRanged()) {
                  var18 += (float)(var1.getAimingPerkCritModifier() * (var2.getPerkLevel(PerkFactory.Perks.Aiming) / 2));
               }

               if (this.isOnFloor()) {
                  var18 *= 2.0F;
               }

               if (var18 > 100.0F) {
                  var18 = 100.0F;
               }

               if ((float)Rand.Next(100) < var18 && !var4) {
                  var7 *= 10.0F;
               }

               if (!this.isOnFloor() && var1.getScriptItem().Categories.contains("Axe")) {
                  var7 *= 2.0F;
               }

               float var19 = 12.0F;
               if (var2 instanceof IsoPlayer) {
                  int var20 = ((IsoPlayer)var2).Moodles.getMoodleLevel(MoodleType.Endurance);
                  if (var20 == 4) {
                     var19 = 50.0F;
                  } else if (var20 == 3) {
                     var19 = 35.0F;
                  } else if (var20 == 2) {
                     var19 = 24.0F;
                  } else if (var20 == 1) {
                     var19 = 16.0F;
                  }
               }

               if (var1.getKnockdownMod() <= 0.0F) {
                  var1.setKnockdownMod(1.0F);
               }

               var19 /= var1.getKnockdownMod();
               if (var7 < var1.getMinDamage() / 4.0F) {
                  var19 += 10.0F;
               }

               if (var2 instanceof IsoPlayer && !var1.isAimedHandWeapon()) {
                  var19 = (float)((double)var19 - (double)((IsoPlayer)var2).useChargeDelta * 2.5D);
               }

               if (var19 < 1.0F) {
                  var19 = 1.0F;
               }

               if (var19 > 10.0F) {
                  var19 = 10.0F;
               }

               if (var2 instanceof IsoPlayer) {
                  var19 = (float)((double)var19 - (double)((IsoPlayer)var2).Moodles.getMoodleLevel(MoodleType.Panic) * 1.3D);
               }

               if (this.getCurrentState() == StaggerBackState.instance()) {
                  var19 /= 3.0F;
               }

               boolean var21 = Rand.Next((int)var19) == 0;
               if (Core.getInstance().getGameMode().equals("Tutorial")) {
                  var21 = true;
               }

               var14 = 0.0F;
               if (var1.isTwoHandWeapon() && (var2.getPrimaryHandItem() != var1 || var2.getSecondaryHandItem() != var1)) {
                  var14 = var1.getWeight() / 1.5F / 10.0F;
               }

               float var15 = (var1.getWeight() * 0.28F * var1.getFatigueMod(var2) * this.getFatigueMod() * var1.getEnduranceMod() * 0.3F + var14) * 0.04F;
               float var16;
               if (var1.isAimedFirearm()) {
                  var16 = var7 * 0.7F;
               } else {
                  var16 = var7 * 0.15F;
               }

               if (this.getHealth() < var7) {
                  var16 = this.getHealth();
               }

               float var17 = var16 / var1.getMaxDamage();
               if (var17 > 1.0F) {
                  var17 = 1.0F;
               }

               if (this.isCloseKilled()) {
                  var17 = 0.2F;
               }

               if (var1.isUseEndurance()) {
                  Stats var22 = var2.getStats();
                  var22.endurance -= var15 * var17;
               }

               this.hitConsequences(var1, var2, var4, var7, var21);
            }
         }
      }
   }

   public void hitConsequences(HandWeapon var1, IsoGameCharacter var2, boolean var3, float var4, boolean var5) {
      if (this instanceof IsoPlayer) {
         if (var3) {
            if (GameServer.bServer) {
               this.sendObjectChange("Shove", new Object[]{"hitDirX", this.getHitDir().getX(), "hitDirY", this.getHitDir().getY(), "force", this.getHitForce()});
               return;
            }

            var2.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
            this.setHitForce(Math.min(0.5F, this.getHitForce()));
            if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
               StaggerBackState.instance().enter(this);
            }

            this.stateMachine.changeState(StaggerBackState.instance());
            return;
         }

         this.BodyDamage.DamageFromWeapon(var1);
      } else {
         if (var2 instanceof IsoPlayer) {
            if (!var3) {
               if (var1.isAimedFirearm()) {
                  this.Health -= var4 * 0.7F;
               } else {
                  this.Health -= var4 * 0.15F;
               }
            }
         } else if (!var3) {
            if (var1.isAimedFirearm()) {
               this.Health -= var4 * 0.7F;
            } else {
               this.Health -= var4 * 0.15F;
            }
         }

         if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() <= 0.0F) && (!var1.isAlwaysKnockdown() && !var5 || !(this instanceof IsoZombie))) {
            if (var1.isSplatBloodOnNoDeath()) {
               this.splatBlood(3, 0.3F);
            }

            if (var1.isKnockBackOnNoDeath()) {
               if (var2.xp != null) {
                  var2.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
               }

               if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
                  StaggerBackState.instance().enter(this);
               }

               this.stateMachine.changeState(StaggerBackState.instance());
            }
         } else {
            this.DoDeath(var1, var2);
         }
      }

   }

   public void inflictWound(IsoGameCharacter.BodyLocation var1, float var2, boolean var3, float var4) {
      IsoGameCharacter.Wound var5 = new IsoGameCharacter.Wound();
      var5.loc = var1;
      var5.bleeding = var2;
      var5.infectedZombie = false;
      if (Rand.Next(100) < (int)var4 * 100) {
         var5.infectedNormal = true;
      }

      this.wounds.add(var5);
   }

   public boolean IsAttackRange(float var1, float var2, float var3) {
      float var4 = 1.0F;
      float var5 = 0.0F;
      if (this.leftHandItem != null) {
         InventoryItem var6 = this.leftHandItem;
         if (var6 instanceof HandWeapon) {
            var4 = ((HandWeapon)var6).getMaxRange(this);
            var5 = ((HandWeapon)var6).getMinRange();
            var4 *= ((HandWeapon)this.leftHandItem).getRangeMod(this);
         }
      }

      if (Math.abs(var3 - this.getZ()) > 0.3F) {
         return false;
      } else {
         float var7 = IsoUtils.DistanceTo(var1, var2, this.getX(), this.getY());
         return var7 < var4 && var7 > var5;
      }
   }

   public boolean IsAttackRange(HandWeapon var1, float var2, float var3, float var4) {
      float var5 = 1.0F;
      float var6 = 0.0F;
      if (var1 instanceof HandWeapon) {
         var5 = ((HandWeapon)var1).getMaxRange(this);
         var6 = ((HandWeapon)var1).getMinRange();
         var5 *= var1.getRangeMod(this);
      }

      if (var1 != null && !var1.isRanged() && Math.abs(var4 - this.getZ()) >= 0.5F) {
         return false;
      } else if (Math.abs(var4 - this.getZ()) > 3.3F) {
         return false;
      } else {
         float var7 = IsoUtils.DistanceTo(var2, var3, this.getX(), this.getY());
         return var7 < var5;
      }
   }

   public boolean IsSpeaking() {
      return this.chatElement.IsSpeaking();
   }

   public void MoveForward(float var1) {
      this.moveForwardVec.x = 0.0F;
      this.moveForwardVec.y = 0.0F;
      switch(this.dir) {
      case S:
         this.moveForwardVec.x = 0.0F;
         this.moveForwardVec.y = 1.0F;
         break;
      case N:
         this.moveForwardVec.x = 0.0F;
         this.moveForwardVec.y = -1.0F;
         break;
      case E:
         this.moveForwardVec.x = 1.0F;
         this.moveForwardVec.y = 0.0F;
         break;
      case W:
         this.moveForwardVec.x = -1.0F;
         this.moveForwardVec.y = 0.0F;
         break;
      case NW:
         this.moveForwardVec.x = -1.0F;
         this.moveForwardVec.y = -1.0F;
         break;
      case NE:
         this.moveForwardVec.x = 1.0F;
         this.moveForwardVec.y = -1.0F;
         break;
      case SW:
         this.moveForwardVec.x = -1.0F;
         this.moveForwardVec.y = 1.0F;
         break;
      case SE:
         this.moveForwardVec.x = 1.0F;
         this.moveForwardVec.y = 1.0F;
      }

      this.moveForwardVec.setLength(var1);
      this.setNx(this.getNx() + this.moveForwardVec.x * GameTime.instance.getMultiplier());
      this.setNy(this.getNy() + this.moveForwardVec.y * GameTime.instance.getMultiplier());
      this.DoFootstepSound(var1);
      if (!(this instanceof IsoZombie)) {
      }

   }

   public void MoveForward(float var1, float var2, float var3, float var4) {
      if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
         this.reqMovement.x = var2;
         this.reqMovement.y = var3;
         this.reqMovement.normalize();
         float var5 = GameTime.instance.getMultiplier();
         this.setNx(this.getNx() + var2 * var1 * var5);
         this.setNy(this.getNy() + var3 * var1 * var5);
         this.DoFootstepSound(var1);
         if (!(this instanceof IsoZombie)) {
         }

      }
   }

   public void pathFinished() {
      this.setPathFindIndex(-1);
      this.stateMachine.changeState(this.defaultState);
   }

   private void pathToAux(float var1, float var2, float var3) {
      boolean var4 = true;
      if ((int)var3 == (int)this.getZ() && IsoUtils.DistanceManhatten(var1, var2, this.x, this.y) <= 30.0F) {
         int var5 = (int)var1 / 10;
         int var6 = (int)var2 / 10;
         IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var5, var6) : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)var1, (int)var2, (int)var3);
         if (var7 != null) {
            var4 = !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), var1, var2, (int)var3, this.getPathFindBehavior2().getTargetChar());
         }
      }

      if (var4 && this.current != null && this.current.HasStairs() && !this.current.isSameStaircase((int)var1, (int)var2, (int)var3)) {
         var4 = false;
      }

      if (var4) {
         this.stateMachine.changeState(WalkTowardState.instance());
      } else {
         this.stateMachine.changeState(PathFindState.instance());
      }

   }

   public void pathToCharacter(IsoGameCharacter var1) {
      this.getPathFindBehavior2().pathToCharacter(var1);
      this.pathToAux(var1.getX(), var1.getY(), var1.getZ());
   }

   public void pathToLocation(int var1, int var2, int var3) {
      this.getPathFindBehavior2().pathToLocation(var1, var2, var3);
      this.pathToAux((float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3);
   }

   public void pathToLocationF(float var1, float var2, float var3) {
      this.getPathFindBehavior2().pathToLocationF(var1, var2, var3);
      this.pathToAux(var1, var2, var3);
   }

   public boolean CanAttack() {
      InventoryItem var1 = this.leftHandItem;
      if (var1 instanceof HandWeapon && var1.getSwingAnim() != null) {
         this.useHandWeapon = (HandWeapon)var1;
      }

      if (this.useHandWeapon == null) {
         return true;
      } else if (this.useHandWeapon.getCondition() <= 0) {
         this.useHandWeapon = null;
         if (this.rightHandItem == this.leftHandItem) {
            this.setSecondaryHandItem((InventoryItem)null);
         }

         this.setPrimaryHandItem((InventoryItem)null);
         if (this.getInventory() != null) {
            this.getInventory().setDrawDirty(true);
         }

         return false;
      } else {
         float var2 = 12.0F;
         int var3 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
         return !this.useHandWeapon.isCantAttackWithLowestEndurance() || var3 != 4;
      }
   }

   public void PlayShootAnim() {
      if (!this.sprite.CurrentAnim.name.contains("Attack_")) {
         InventoryItem var1 = this.leftHandItem;
         if (var1 instanceof HandWeapon && var1.getSwingAnim() != null) {
            if (this.bUseParts) {
               this.useHandWeapon = (HandWeapon)var1;
               if (this.useHandWeapon.getCondition() <= 0) {
                  return;
               }

               if (this.useHandWeapon.isCantAttackWithLowestEndurance() && this.stats.endurance < this.stats.endurancedanger) {
                  return;
               }

               this.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
               this.legsSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
               this.def.Frame = 0.0F;
               this.def.Finished = true;
               this.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               if (this.torsoSprite != null) {
                  this.torsoSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  this.def.Finished = true;
                  this.torsoSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               }

               if (this.headSprite != null) {
                  this.headSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  this.def.Finished = true;
                  this.headSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               }

               for(int var2 = 0; var2 < this.extraSprites.size(); ++var2) {
                  ((IsoSprite)this.extraSprites.get(var2)).PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  ((IsoSprite)this.extraSprites.get(var2)).CurrentAnim.FinishUnloopedOnFrame = 0;
                  this.def.Finished = true;
               }

               if (this.bottomsSprite != null) {
                  this.bottomsSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  this.def.Finished = true;
                  this.bottomsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               }

               if (this.hairSprite != null) {
                  this.hairSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  this.def.Finished = true;
                  this.hairSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               }

               if (this.shoeSprite != null) {
                  this.shoeSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  this.def.Finished = true;
                  this.shoeSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               }

               if (this.topSprite != null) {
                  this.topSprite.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
                  this.def.Finished = true;
                  this.topSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
               }

               this.def.Frame = 0.0F;
               this.sprite.Animate = false;
               this.def.setFrameSpeedPerFrame(1.0F / this.useHandWeapon.getSwingTime());
            } else {
               this.useHandWeapon = (HandWeapon)var1;
               if (this.useHandWeapon.getCondition() <= 0) {
                  return;
               }

               if (this.useHandWeapon.isCantAttackWithLowestEndurance() && this.stats.endurance < this.stats.endurancedanger) {
                  return;
               }

               this.PlayAnimUnlooped("Attack_" + var1.getSwingAnim());
               this.def.Frame = 0.0F;
               this.sprite.Animate = false;
               this.def.setFrameSpeedPerFrame(1.0F / this.useHandWeapon.getSwingTime());
            }
         }
      }

   }

   public void ReduceHealthWhenBurning() {
      if (this.OnFire) {
         if (this.godMod) {
            this.StopBurning();
         } else if (!GameClient.bClient || !(this instanceof IsoZombie)) {
            if (!GameClient.bClient || !(this instanceof IsoPlayer) || !((IsoPlayer)this).bRemote) {
               if (this.Health > 0.0F) {
                  if (!(this instanceof IsoPlayer)) {
                     if (this instanceof IsoZombie) {
                        this.Health -= this.FireKillRate / 20.0F * GameTime.instance.getMultiplier();
                     } else {
                        this.Health -= this.FireKillRate * GameTime.instance.getMultiplier();
                     }
                  } else {
                     float var1 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F;
                     this.BodyDamage.ReduceGeneralHealth(var1);
                     this.BodyDamage.OnFire(true);
                  }

                  if (this.Health <= 0.0F) {
                     IsoFireManager.RemoveBurningCharacter(this);
                     if (this instanceof IsoZombie) {
                        LuaEventManager.triggerEvent("OnZombieDead", this);
                     }

                     this.stateMachine.changeState(BurntToDeath.instance());
                     this.stateMachine.Lock = true;
                  }
               }

               if (this instanceof IsoPlayer && Rand.Next(Rand.AdjustForFramerate(((IsoPlayer)this).IsRunning() ? 150 : 400)) == 0) {
                  IsoFireManager.RemoveBurningCharacter(this);
                  this.setOnFire(false);
                  if (this.AttachedAnimSprite != null) {
                     this.AttachedAnimSprite.clear();
                  }

                  if (this.AttachedAnimSpriteActual != null) {
                     this.AttachedAnimSpriteActual.clear();
                  }
               }

            }
         }
      }
   }

   public void DrawSneezeText() {
      if (this.BodyDamage.IsSneezingCoughing() > 0) {
         String var1 = null;
         if (this.BodyDamage.IsSneezingCoughing() == 1) {
            var1 = "Ah-choo!";
         }

         if (this.BodyDamage.IsSneezingCoughing() == 2) {
            var1 = "Cough!";
         }

         if (this.BodyDamage.IsSneezingCoughing() == 3) {
            var1 = "Ah-fmmph!";
         }

         if (this.BodyDamage.IsSneezingCoughing() == 4) {
            var1 = "fmmmph!";
         }

         float var2 = (float)this.sx;
         float var3 = (float)this.sy;
         var2 = (float)((int)var2);
         var3 = (float)((int)var3);
         var2 -= (float)((int)IsoCamera.getOffX());
         var3 -= (float)((int)IsoCamera.getOffY());
         var3 -= 48.0F;
         if (var1 != null) {
            IndieGL.End();
            TextManager.instance.DrawStringCentre(UIFont.Dialogue, (double)((int)var2), (double)((int)var3), var1, (double)this.SpeakColour.r, (double)this.SpeakColour.g, (double)this.SpeakColour.b, (double)this.SpeakColour.a);
         }
      }

   }

   public IsoSpriteInstance getSpriteDef() {
      if (this.def == null) {
         this.def = new IsoSpriteInstance();
      }

      return this.def;
   }

   public void drawAt(int var1, int var2) {
      if (this.def == null) {
         this.def = this.def;
      }

      if (!this.bUseParts && this.def == null) {
         this.def = this.def = IsoSpriteInstance.get(this.sprite);
      }

      if (this.sprite != null) {
         if (!this.bUseParts) {
            this.sprite.drawAt(this.def, this, var1, var2, this.dir);
         } else {
            this.EnforceAnims();
            this.legsSprite.drawAt(this.def, this, var1, var2, this.dir);
            if (this.torsoSprite != null) {
               this.torsoSprite.drawAt(this.def, this, var1, var2, this.dir);
            }

            if (this.shoeSprite != null) {
               this.shoeSprite.drawAt(this.def, this, var1, var2, this.dir);
            }

            if (this.bottomsSprite != null) {
               this.bottomsSprite.TintMod.r = this.descriptor.trouserColor.r;
               this.bottomsSprite.TintMod.g = this.descriptor.trouserColor.g;
               this.bottomsSprite.TintMod.b = this.descriptor.trouserColor.b;
               this.bottomsSprite.TintMod.desaturate(0.5F);
               this.bottomsSprite.drawAt(this.def, this, var1, var2, this.dir);
            }

            if (this.topSprite != null) {
               this.topSprite.TintMod.r = this.descriptor.topColor.r;
               this.topSprite.TintMod.g = this.descriptor.topColor.g;
               this.topSprite.TintMod.b = this.descriptor.topColor.b;
               this.topSprite.TintMod.desaturate(0.5F);
               this.topSprite.drawAt(this.def, this, var1, var2, this.dir);
            }

            if (this.headSprite != null) {
               this.headSprite.drawAt(this.def, this, var1, var2, this.dir);
            }

            if (this.hairSprite != null) {
               this.hairSprite.drawAt(this.def, this, var1, var2, this.dir);
            }

            for(int var3 = 0; var3 < this.extraSprites.size(); ++var3) {
               ((IsoSprite)this.extraSprites.get(var3)).drawAt(this.def, this, var1, var2, this.dir);
            }
         }
      }

   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      if (this.alpha[IsoPlayer.getPlayerIndex()] != 0.0F || this.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) {
         if (this.vehicle == null || this.vehicle.getSeat(this) == -1) {
            if (!this.bUseParts && this.def == null) {
               this.def = IsoSpriteInstance.get(this.sprite);
            }

            int var6 = PerformanceSettings.numberOf3D;
            switch(PerformanceSettings.numberOf3D) {
            case 1:
               var6 = 1;
               break;
            case 2:
               var6 = 2;
               break;
            case 3:
               var6 = 3;
               break;
            case 4:
               var6 = 4;
               break;
            case 5:
               var6 = 5;
               break;
            case 6:
               var6 = 8;
               break;
            case 7:
               var6 = 10;
               break;
            case 8:
               var6 = 20;
               break;
            case 9:
               var6 = 20000;
            }

            var6 += PerformanceSettings.numberOf3DAlt;
            float var7;
            float var9;
            float var10;
            float var11;
            if (DropShadow != null && this.getCurrentSquare() != null && this.getVehicle() == null) {
               var7 = 0.5F * this.alpha[IsoPlayer.getPlayerIndex()];
               var7 *= (this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo().r + this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo().g + this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo().b) / 3.0F;
               if (DropShadow.def == null) {
                  DropShadow.def = IsoSpriteInstance.get(DropShadow);
               }

               IsoAnim var8 = this.legsSprite != null && this.legsSprite.CurrentAnim != null ? this.legsSprite.CurrentAnim : null;
               if ("ZombieDeath".equals(var8.name) && this.def.Frame >= (float)(var8.Frames.size() / 2)) {
                  var7 *= ((float)var8.Frames.size() - this.def.Frame) / (float)(var8.Frames.size() / 2);
               } else if ("ZombieGetUp".equals(var8.name) && this.def.Frame < (float)(var8.Frames.size() / 2)) {
                  var7 *= 1.0F - ((float)(var8.Frames.size() / 2) - this.def.Frame) / (float)(var8.Frames.size() / 2);
               } else if (var8.name != null && var8.name.contains("Crawl")) {
                  var7 = 0.0F;
               }

               DropShadow.def.alpha = 0.8F * var7;
               DropShadow.def.targetAlpha = DropShadow.def.alpha;
               var9 = this.def.getScaleX();
               var10 = this.def.getScaleY();
               DropShadow.def.setScale(var9, var10);
               var11 = 49.0F;
               float var12 = 27.0F;
               DropShadow.render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX + var11 * var9 / 2.0F, this.offsetY + var12 * var10 / 2.0F - 2.0F * var10, inf);
            }

            IsoGridSquare var17;
            if (this.bDoDefer && var3 - (float)((int)var3) > 0.2F) {
               var17 = this.getCell().getGridSquare((int)var1, (int)var2, (int)var3 + 1);
               if (var17 != null) {
                  var17.addDeferredCharacter(this);
               }
            }

            if (PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
               this.getCurrentSquare().interpolateLight(inf, var1 - (float)this.getCurrentSquare().getX(), var2 - (float)this.getCurrentSquare().getY());
               if (var6 > 0 && this.legsSprite.modelSlot == null) {
               }

               if (var3 - (float)((int)var3) > 0.2F) {
                  var17 = this.getCell().getGridSquare((int)var1, (int)var2, (int)var3 + 1);
                  if (var17 != null) {
                     ColorInfo var15 = tempColorInfo;
                     var17.lighting[IsoCamera.frameState.playerIndex].lightInfo();
                     var17.interpolateLight(var15, var1 - (float)this.getCurrentSquare().getX(), var2 - (float)this.getCurrentSquare().getY());
                     inf.interp(var15, (var3 - ((float)((int)var3) + 0.2F)) / 0.8F, inf);
                  }
               }
            } else {
               inf.r = var4.r;
               inf.g = var4.g;
               inf.b = var4.b;
               inf.a = var4.a;
            }

            if (Core.bDebug && this.getCurrentState() == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
               inf.r = 1.0F;
               inf.g = inf.b = 0.0F;
            }

            if (this instanceof IsoPlayer) {
            }

            if (this.dir == IsoDirections.Max) {
               this.dir = IsoDirections.N;
            }

            if (this.sprite != null && !this.legsSprite.hasActiveModel()) {
               this.checkDrawWeaponPre(var1, var2, var3, var4);
            }

            lastRenderedRendered = lastRendered;
            lastRendered = this;
            if (this.sprite == null || !this.sprite.getProperties().Is(IsoFlagType.invisible)) {
               var7 = 2.0F;
               float var16 = 1.5F;
               if (IsoCamera.CamCharacter.HasTrait("ShortSighted")) {
                  var7 = 1.0F;
               }

               if (IsoCamera.CamCharacter.HasTrait("EagleEyed")) {
                  var7 = 3.0F;
                  var16 = 2.0F;
               }

               if (this == IsoCamera.CamCharacter) {
                  this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
               }

               if (alphaStep == -100.0F) {
                  IsoCamera.CamCharacter.getBodyDamage().ReduceFactor();
               }

               float[] var10000;
               int var10001;
               if (this.alpha[IsoPlayer.getPlayerIndex()] < this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
                  var10000 = this.alpha;
                  var10001 = IsoPlayer.getPlayerIndex();
                  var10000[var10001] += alphaStep * var7;
                  if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
                     this.alpha[IsoPlayer.getPlayerIndex()] = this.targetAlpha[IsoPlayer.getPlayerIndex()];
                  }
               } else if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
                  var10000 = this.alpha;
                  var10001 = IsoPlayer.getPlayerIndex();
                  var10000[var10001] -= alphaStep / var16 / 2.5F;
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

               if (this instanceof IsoZombie && ((IsoZombie)this).isFakeDead()) {
                  this.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
               }

               if (this.alpha[IsoPlayer.getPlayerIndex()] != 0.0F) {
                  var9 = (float)Core.TileScale;
                  var10 = this.offsetX + (float)RENDER_OFFSET_X * var9;
                  var11 = this.offsetY + (float)RENDER_OFFSET_Y * var9;
                  int var18;
                  if (this.sprite != null) {
                     this.def.setScale(var9, var9);
                     if (!this.bUseParts) {
                        this.sprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                     } else {
                        this.def.Flip = false;
                        this.EnforceAnims();
                        this.legsSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                        if (!this.hasActiveModel()) {
                           if (this.torsoSprite != null) {
                              this.torsoSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }

                           if (this.shoeSprite != null) {
                              this.shoeSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }

                           if (this.bottomsSprite != null) {
                              this.bottomsSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }

                           if (this.topSprite != null) {
                              this.topSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }

                           if (this.headSprite != null) {
                              this.headSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }

                           if (this.hairSprite != null) {
                              this.hairSprite.render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }

                           for(var18 = 0; var18 < this.extraSprites.size(); ++var18) {
                              ((IsoSprite)this.extraSprites.get(var18)).render(this.def, this, var1, var2, var3, this.dir, var10, var11, inf);
                           }
                        }
                     }
                  }

                  if (this.AttachedAnimSprite != null) {
                     for(var18 = 0; var18 < this.AttachedAnimSprite.size(); ++var18) {
                        IsoSpriteInstance var13 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var18);
                        var13.update();
                        float var14 = inf.a;
                        inf.a = var13.alpha;
                        var13.SetTargetAlpha(this.targetAlpha[IsoPlayer.getPlayerIndex()]);
                        var13.render(this, var1, var2, var3, this.dir, var10, var11, inf);
                        inf.a = var14;
                     }
                  }

                  if (this.sprite != null && !this.legsSprite.hasActiveModel()) {
                     this.checkDrawWeaponPost(var1, var2, var3, inf);
                  }

                  for(var18 = 0; var18 < this.inventory.Items.size(); ++var18) {
                     InventoryItem var19 = (InventoryItem)this.inventory.Items.get(var18);
                     if (var19 instanceof IUpdater) {
                        ((IUpdater)var19).render();
                     }
                  }

               }
            }
         }
      }
   }

   public void renderObjectPicker(float var1, float var2, float var3, ColorInfo var4) {
      if (!this.bUseParts) {
         this.sprite.renderObjectPicker(this.def, this, var1, var2, var3, this.dir, this.offsetX, this.offsetY, var4);
      } else {
         this.legsSprite.renderObjectPicker(this.def, this, var1, var2, var3, this.dir, this.offsetX, this.offsetY, var4);
         if (this.torsoSprite != null) {
            this.torsoSprite.renderObjectPicker(this.def, this, var1, var2, var3, this.dir, this.offsetX, this.offsetY, var4);
         }
      }

   }

   public boolean isMaskClicked(int var1, int var2, boolean var3) {
      if (this.sprite == null) {
         return false;
      } else if (!this.bUseParts) {
         return super.isMaskClicked(var1, var2, var3);
      } else {
         boolean var4 = false;
         var4 = this.legsSprite.isMaskClicked(this.dir, var1, var2, var3);
         if (this.torsoSprite != null) {
            var4 |= this.torsoSprite.isMaskClicked(this.dir, var1, var2, var3);
         }

         return var4;
      }
   }

   public void setHaloNote(String var1) {
      this.setHaloNote(var1, this.haloDispTime);
   }

   public void setHaloNote(String var1, float var2) {
      this.setHaloNote(var1, 0, 255, 0, var2);
   }

   public void setHaloNote(String var1, int var2, int var3, int var4, float var5) {
      if (this.haloNote != null && var1 != null) {
         this.haloDispTime = var5;
         this.haloNote.setDefaultColors(var2, var3, var4);
         this.haloNote.ReadString(var1);
         this.haloNote.setInternalTickClock(this.haloDispTime);
      }

   }

   public void DoSneezeText() {
      if (this.BodyDamage.IsSneezingCoughing() > 0) {
         String var1 = null;
         if (this.BodyDamage.IsSneezingCoughing() == 1) {
            var1 = "Ah-choo!";
         }

         if (this.BodyDamage.IsSneezingCoughing() == 2) {
            var1 = "Cough!";
         }

         if (this.BodyDamage.IsSneezingCoughing() == 3) {
            var1 = "Ah-fmmph!";
         }

         if (this.BodyDamage.IsSneezingCoughing() == 4) {
            var1 = "fmmmph!";
         }

         if (var1 != null) {
            this.Say(var1);
         }
      }

   }

   public String getSayLine() {
      return this.chatElement.getSayLine();
   }

   public ChatMessage getLastChatMessage() {
      return this.lastChatMessage;
   }

   public void setLastChatMessage(ChatMessage var1) {
      this.lastChatMessage = var1;
   }

   protected void doSleepSpeech() {
      ++this.sleepSpeechCnt;
      if ((float)this.sleepSpeechCnt > (float)(250 * PerformanceSettings.LockFPS) / 30.0F) {
         this.sleepSpeechCnt = 0;
         if (sleepText == null) {
            sleepText = "ZzzZZZzzzz";
            ChatElement.addNoLogText(sleepText);
         }

         this.SayWhisper(sleepText);
      }

   }

   public void SayDebug(String var1) {
      this.chatElement.SayDebug(0, var1);
   }

   public void SayDebug(int var1, String var2) {
      this.chatElement.SayDebug(var1, var2);
   }

   public int getMaxChatLines() {
      return this.chatElement.getMaxChatLines();
   }

   public void Say(String var1) {
      this.ProcessSay(var1, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, UIFont.Dialogue, 30.0F, false, "default", false, false, false, false, false, true);
      if (this.AllowConversation) {
         if (TutorialManager.instance.ProfanityFilter) {
            var1 = ProfanityFilter.getInstance().filterString(var1);
         }

         ChatManager.getInstance().showInfoMessage(((IsoPlayer)this).getUsername(), var1);
      }
   }

   public void SayWhisper(String var1) {
      if (this.AllowConversation) {
         if (TutorialManager.instance.ProfanityFilter) {
            var1 = ProfanityFilter.getInstance().filterString(var1);
         }

         this.ProcessSay(var1, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, UIFont.Dialogue, 10.0F, false, "whisper", false, false, false, false, false, true);
      }
   }

   public void SayShout(String var1) {
      ChatManager.getInstance().sendMessageToChat(ChatType.shout, var1);
      this.ProcessSay(var1, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, UIFont.Dialogue, 60.0F, false, "shout", false, false, false, false, false, true);
   }

   private void ProcessSay(String var1, float var2, float var3, float var4, UIFont var5, float var6, boolean var7, String var8, boolean var9, boolean var10, boolean var11, boolean var12, boolean var13, boolean var14) {
      if (this.AllowConversation) {
         if (TutorialManager.instance.ProfanityFilter) {
            var1 = ProfanityFilter.getInstance().filterString(var1);
         }

         if (var7 && GameClient.bClient && this == IsoPlayer.instance) {
            boolean var15 = false;
            if (var8.equals("whisper")) {
               var15 = true;
            } else if (var8.equals("shout")) {
               var15 = true;
            }
         }

      }
   }

   protected boolean playerIsSelf() {
      return IsoPlayer.instance == this;
   }

   public int getUserNameHeight() {
      if (!GameClient.bClient) {
         return 0;
      } else {
         return this.userName != null ? this.userName.getHeight() : 0;
      }
   }

   protected void initTextObjects() {
      this.hasInitTextObjects = true;
      if (this instanceof IsoPlayer) {
         this.chatElement.setMaxChatLines(5);
         if (IsoPlayer.instance != null) {
            System.out.println("FirstNAME:" + IsoPlayer.instance.username);
         }

         this.isoPlayer = (IsoPlayer)this;
         if (this.isoPlayer.username != null) {
            this.userName = new TextDrawObject();
            this.userName.setAllowAnyImage(true);
            this.userName.setDefaultFont(UIFont.Small);
            this.userName.setDefaultColors(255, 255, 255, 255);
            this.updateUserName();
         }

         if (this.haloNote == null) {
            this.haloNote = new TextDrawObject();
            this.haloNote.setDefaultFont(UIFont.Small);
            this.haloNote.setDefaultColors(0, 255, 0);
         }
      }

   }

   protected void updateUserName() {
      if (this.userName != null && this.isoPlayer != null) {
         String var1 = this.isoPlayer.getDisplayName();
         if (this != IsoPlayer.instance && this.isoPlayer.invisible && !this.invisible) {
            this.userName.ReadString("");
            return;
         }

         Faction var2 = Faction.getPlayerFaction(this.isoPlayer);
         if (var2 != null) {
            if (!this.isoPlayer.showTag && this.isoPlayer != IsoPlayer.instance && Faction.getPlayerFaction(IsoPlayer.instance) != var2) {
               this.isoPlayer.tagPrefix = "";
            } else {
               this.isoPlayer.tagPrefix = var2.getTag();
               if (var2.getTagColor() != null) {
                  this.isoPlayer.setTagColor(var2.getTagColor());
               }
            }
         } else {
            this.isoPlayer.tagPrefix = "";
         }

         boolean var3 = this.isoPlayer != null && this.isoPlayer.bRemote || Core.getInstance().isShowYourUsername();
         boolean var4 = IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
         if (!ServerOptions.instance.DisplayUserName.getValue() && !var4) {
            var3 = false;
         }

         if (!var3) {
            var1 = "";
         }

         if (var3 && this.isoPlayer.tagPrefix != null && !this.isoPlayer.tagPrefix.equals("")) {
            var1 = "[col=" + (new Float(this.isoPlayer.getTagColor().r * 255.0F)).intValue() + "," + (new Float(this.isoPlayer.getTagColor().g * 255.0F)).intValue() + "," + (new Float(this.isoPlayer.getTagColor().b * 255.0F)).intValue() + "][" + this.isoPlayer.tagPrefix + "][/] " + var1;
         }

         if (var3 && !this.isoPlayer.accessLevel.equals("") && this.isoPlayer.isShowAdminTag()) {
            var1 = (String)this.namesPrefix.get(this.isoPlayer.accessLevel) + var1;
         }

         if (var3 && !this.isoPlayer.isSafety() && ServerOptions.instance.ShowSafety.getValue()) {
            var1 = var1 + this.namePvpSuffix;
         }

         if (this.isoPlayer.isSpeek && !this.isoPlayer.isVoiceMute) {
            var1 = this.voiceSuffix + var1;
         }

         if (this.isoPlayer.isVoiceMute) {
            var1 = this.voiceMuteSuffix + var1;
         }

         BaseVehicle var5 = IsoCamera.CamCharacter == this.isoPlayer ? this.isoPlayer.getNearVehicle() : null;
         if (this.getVehicle() == null && var5 != null && (this.isoPlayer.getInventory().haveThisKeyId(var5.getKeyId()) != null || var5.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue())) {
            Integer[] var6 = Color.HSBtoRGB(var5.colorHue, var5.colorSaturation * 0.5F, var5.colorValue);
            var1 = this.nameCarKeySuffix + "," + var6[0] + "," + var6[1] + "," + var6[2] + "]" + var1;
         }

         if (!var1.equals(this.userName.getOriginal())) {
            this.userName.ReadString(var1);
         }
      }

   }

   public void updateTextObjects() {
      if (!GameServer.bServer) {
         if (!this.hasInitTextObjects) {
            this.initTextObjects();
         }

         if (!this.Speaking) {
            this.DoSneezeText();
            if (this.isAsleep() && this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(0)) {
               this.doSleepSpeech();
            }
         }

         if (this.isoPlayer != null) {
            this.radioEquipedCheck();
         }

         this.sayLine = null;
         this.Speaking = false;
         this.drawUserName = false;
         this.canSeeCurrent = false;
         if (this.haloNote != null && this.haloNote.getInternalClock() > 0.0F && !IsoPlayer.DemoMode) {
            this.haloNote.updateInternalTickClock();
         }

         this.chatElement.update();
         this.sayLine = this.chatElement.getSayLine();
         this.Speaking = this.chatElement.IsSpeaking();
         if (!this.Speaking || this.Health <= 0.0F || this.BodyDamage.getHealth() <= 0.0F) {
            this.Speaking = false;
            this.callOut = false;
         }

      }
   }

   public void renderlast() {
      int var1 = IsoCamera.frameState.playerIndex;
      float var2 = this.x;
      float var3 = this.y;
      if (GameClient.bClient && this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
         var2 = this.bx;
         var3 = this.by;
      }

      if (this.sx == 0 && this.def != null) {
         this.sx = (int)IsoUtils.XToScreen(var2 + this.def.offX, var3 + this.def.offY, this.z + this.def.offZ, 0);
         this.sy = (int)IsoUtils.YToScreen(var2 + this.def.offX, var3 + this.def.offY, this.z + this.def.offZ, 0);
         this.sx = (int)((float)this.sx - (this.offsetX - 8.0F));
         this.sy = (int)((float)this.sy - (this.offsetY - 60.0F));
      }

      float var4;
      float var5;
      float var20;
      if (this.hasInitTextObjects && this.isoPlayer != null || this.chatElement.getHasChatToDisplay()) {
         var4 = IsoUtils.XToScreen(var2, var3, this.getZ(), 0);
         var5 = IsoUtils.YToScreen(var2, var3, this.getZ(), 0);
         var4 = var4 - IsoCamera.getOffX() - this.offsetX;
         var5 = var5 - IsoCamera.getOffY() - this.offsetY;
         var5 -= (float)(128 / (2 / Core.TileScale));
         var4 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
         var5 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
         this.canSeeCurrent = false;
         this.drawUserName = false;
         if (this.isoPlayer != null && (this == IsoCamera.frameState.CamCharacter || this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(var1))) {
            this.canSeeCurrent = true;
            if (GameClient.bClient && this.userName != null) {
               this.drawUserName = true;
               this.updateUserName();
            }

            if (!GameClient.bClient && this.isoPlayer != null && this.isoPlayer.getVehicle() == null) {
               String var6 = "";
               BaseVehicle var7 = this.isoPlayer.getNearVehicle();
               if (this.getVehicle() == null && var7 != null && (this.isoPlayer.getInventory().haveThisKeyId(var7.getKeyId()) != null || var7.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue())) {
                  Integer[] var8 = Color.HSBtoRGB(var7.colorHue, var7.colorSaturation * 0.5F, var7.colorValue);
                  var6 = this.nameCarKeySuffix + "," + var8[0] + "," + var8[1] + "," + var8[2] + "]";
               }

               if (!var6.equals("")) {
                  this.userName.ReadString(var6);
                  this.drawUserName = true;
               }
            }
         }

         if (this.isoPlayer != null && this.hasInitTextObjects && (this.playerIsSelf() || this.canSeeCurrent)) {
            if (this.canSeeCurrent && this.drawUserName) {
               var5 -= (float)this.userName.getHeight();
               this.userName.AddBatchedDraw((double)((int)var4), (double)((int)var5), true);
            }

            if (this.playerIsSelf()) {
               ActionProgressBar var18 = UIManager.getProgressBar((double)IsoPlayer.getPlayerIndex());
               if (var18 != null && var18.isVisible()) {
                  var5 -= (float)(var18.getHeight().intValue() + 2);
               }
            }

            if (this.playerIsSelf() && this.haloNote != null && this.haloNote.getInternalClock() > 0.0F) {
               var20 = this.haloNote.getInternalClock() / this.haloDispTime;
               var5 -= (float)(this.haloNote.getHeight() + 2);
               this.haloNote.AddBatchedDraw((double)((int)var4), (double)((int)var5), false, var20);
            }
         }

         boolean var21 = false;
         if (IsoPlayer.instance != this && this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && this.equipedRadio.getDeviceData().getHeadphoneType() >= 0) {
            var21 = true;
         }

         boolean var19 = GameClient.bClient && IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
         if (!this.invisible || this == IsoCamera.frameState.CamCharacter || var19) {
            this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)var4, (int)var5, var21);
         }
      }

      if (Core.bDebug && DebugOptions.instance.AimConeRender.getValue()) {
         this.debugAim();
      }

      if (this.inventory != null) {
         for(int var17 = 0; var17 < this.inventory.Items.size(); ++var17) {
            InventoryItem var23 = (InventoryItem)this.inventory.Items.get(var17);
            if (var23 instanceof IUpdater) {
               ((IUpdater)var23).renderlast();
            }
         }

         if (Core.bDebug) {
         }

         if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.pfb2 != null) {
            this.pfb2.render();
         }

         if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRadius.getValue()) {
            var4 = 0.3F;
            var5 = 1.0F;
            var20 = 1.0F;
            float var22 = 1.0F;
            double var24 = (double)this.x + (double)var4 * Math.cos(Math.toRadians(0.0D));
            double var10 = (double)this.y + (double)var4 * Math.sin(Math.toRadians(0.0D));

            for(int var12 = 1; var12 <= 16; ++var12) {
               double var13 = (double)this.x + (double)var4 * Math.cos(Math.toRadians((double)(var12 * 360 / 16)));
               double var15 = (double)this.y + (double)var4 * Math.sin(Math.toRadians((double)(var12 * 360 / 16)));
               LineDrawer.addLine((float)var24, (float)var10, this.z, (float)var13, (float)var15, this.z, var5, var20, var22, (String)null, true);
               var24 = var13;
               var10 = var15;
            }
         }

      }
   }

   public Radio getEquipedRadio() {
      return this.equipedRadio;
   }

   private void radioEquipedCheck() {
      if (this.leftHandItem != this.leftHandCache) {
         this.leftHandCache = this.leftHandItem;
         if (this.leftHandItem != null && (this.equipedRadio == null || this.equipedRadio != this.rightHandItem) && this.leftHandItem instanceof Radio) {
            this.equipedRadio = (Radio)this.leftHandItem;
         } else if (this.equipedRadio != null && this.equipedRadio != this.rightHandItem) {
            if (this.equipedRadio.getDeviceData() != null) {
               this.equipedRadio.getDeviceData().cleanSoundsAndEmitter();
            }

            this.equipedRadio = null;
         }
      }

      if (this.rightHandItem != this.rightHandCache) {
         this.rightHandCache = this.rightHandItem;
         if (this.rightHandItem != null && this.rightHandItem instanceof Radio) {
            this.equipedRadio = (Radio)this.rightHandItem;
         } else if (this.equipedRadio != null && this.equipedRadio != this.leftHandItem) {
            if (this.equipedRadio.getDeviceData() != null) {
               this.equipedRadio.getDeviceData().cleanSoundsAndEmitter();
            }

            this.equipedRadio = null;
         }
      }

   }

   private void setWeaponHitList(HandWeapon var1) {
      IsoGameCharacter var2 = this;
      int var3 = var1.getMaxHitCount();
      HitList.clear();
      int var4 = 0;
      boolean var5 = false;
      int var6;
      IsoMovingObject var7;
      if (var4 < var3) {
         for(var6 = 0; var6 < var2.getCell().getObjectList().size(); ++var6) {
            var7 = (IsoMovingObject)var2.getCell().getObjectList().get(var6);
            if (var7 != var2 && (!(var7 instanceof IsoPlayer) || !((IsoPlayer)var7).accessLevel.equals("admin") && ServerOptions.instance.PVP.getValue() && (!ServerOptions.instance.SafetySystem.getValue() || !var2.isSafety() || !((IsoGameCharacter)var7).isSafety())) && (!(var7 instanceof IsoGameCharacter) || !((IsoGameCharacter)var7).godMod) && (var7.isShootable() && !var7.isOnFloor() && !var5 || var7.isShootable() && var7.isOnFloor() && var5) && var2.IsAttackRange(var1, var7.getX(), var7.getY(), var7.getZ()) && !var1.isDirectional()) {
               Vector2 var8 = tempVector2_1.set(var2.getX(), var2.getY());
               Vector2 var9 = tempVector2_2.set(var7.getX(), var7.getY());
               var9.x -= var8.x;
               var9.y -= var8.y;
               Vector2 var10 = var2.getAngle();
               var2.DirectionFromVector(var10);
               var9.normalize();
               float var11 = var9.dot(var10);
               if (var11 > 1.0F) {
                  var11 = 1.0F;
               }

               if (var11 < -1.0F) {
                  var11 = -1.0F;
               }

               float var12 = var1.getMinAngle();
               if (var1.isRanged()) {
                  var12 -= var1.getAimingPerkMinAngleModifier() * (float)(var2.getPerkLevel(PerkFactory.Perks.Aiming) / 2);
               }

               if (var11 >= var12 && var11 <= var1.getMaxAngle()) {
                  float var13 = var7.DistToProper(var2);
                  HitList.add(new IsoGameCharacter.HitInfo(var7, var11, var13));
                  ++var4;
               }
            }
         }

         Collections.sort(HitList, new IsoGameCharacter.HitInfoComparator(var2, var1.getMaxRange(var2) * var1.getRangeMod(var2)));
         if (var1.isPiercingBullets()) {
            ArrayList var19 = new ArrayList();
            double var20 = 0.0D;

            for(int var22 = 0; var22 < HitList.size(); ++var22) {
               IsoMovingObject var24 = ((IsoGameCharacter.HitInfo)HitList.get(var22)).object;
               double var25 = (double)(var2.getX() - var24.getX());
               double var26 = (double)(-(var2.getY() - var24.getY()));
               double var15 = Math.atan2(var26, var25);
               if (var15 < 0.0D) {
                  var15 = Math.abs(var15);
               } else {
                  var15 = 6.283185307179586D - var15;
               }

               if (var22 == 0) {
                  var20 = Math.toDegrees(var15);
                  var19.add(HitList.get(var22));
               } else {
                  double var17 = Math.toDegrees(var15);
                  if (Math.abs(var20 - var17) < 1.0D) {
                     var19.add(HitList.get(var22));
                     break;
                  }
               }
            }

            HitList = var19;
         } else {
            while(HitList.size() > var3) {
               HitList.remove(HitList.size() - 1);
            }
         }
      }

      for(var6 = 0; var6 < HitList.size(); ++var6) {
         var7 = ((IsoGameCharacter.HitInfo)HitList.get(var6)).object;
         int var21 = var1.getHitChance();
         var21 = (int)((float)var21 + var1.getAimingPerkHitChanceModifier() * (float)var2.getPerkLevel(PerkFactory.Perks.Aiming));
         float var23 = IsoUtils.DistanceTo(var7.getX(), var7.getY(), var2.getX(), var2.getY());
         if (var1.getMinRangeRanged() > 0.0F) {
            if (var23 < var1.getMinRangeRanged()) {
               var21 -= 50;
            }
         } else if ((double)var23 < 1.5D && var1.isRanged()) {
            var21 += 15;
         }

         if (var1.isRanged() && var2.getBeenMovingFor() > (float)(var1.getAimingTime() + var2.getPerkLevel(PerkFactory.Perks.Aiming) * 2)) {
            var21 = (int)((float)var21 - (var2.getBeenMovingFor() - (float)(var1.getAimingTime() + var2.getPerkLevel(PerkFactory.Perks.Aiming) * 2)));
         }

         if (var2.HasTrait("Marksman")) {
            var21 += 20;
         }

         if (var21 < 10) {
            var21 = 10;
         }

         if (var21 > 100 || !var1.isRanged()) {
            var21 = 100;
         }

         ((IsoGameCharacter.HitInfo)HitList.get(var6)).chance = var21;
      }

   }

   private void debugAim() {
      if (this == IsoPlayer.instance) {
         IsoPlayer var1 = (IsoPlayer)this;
         HandWeapon var2 = var1.getUseHandWeapon();
         if (var1.IsAiming() && var2 != null && var2.isRanged()) {
            float var3 = var2.getMaxRange(var1) * var2.getRangeMod(var1);
            Vector2 var4 = this.getAngle();
            float var5 = this.x + var4.x * var3;
            float var6 = this.y + var4.y * var3;
            float var7 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
            float var8 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
            float var9 = IsoUtils.XToScreenExact(var5, var6, this.z, 0);
            float var10 = IsoUtils.YToScreenExact(var5, var6, this.z, 0);
            LineDrawer.drawLine(var7, var8, var9, var10, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            float var11 = var2.getMinAngle();
            var11 -= var2.getAimingPerkMinAngleModifier() * (float)(this.getPerkLevel(PerkFactory.Perks.Aiming) / 2);
            Vector2 var12 = tempVector2_1.set(var4.y, var4.x);
            var12.setLengthAndDirection(var12.getDirection() - (1.0F - var11) * 3.1415927F, 1.0F);
            var5 = this.x + var12.x * var3;
            var6 = this.y + var12.y * var3;
            var9 = IsoUtils.XToScreenExact(var5, var6, this.z, 0);
            var10 = IsoUtils.YToScreenExact(var5, var6, this.z, 0);
            LineDrawer.drawLine(var7, var8, var9, var10, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            var12.set(var4.y, var4.x);
            var12.setLengthAndDirection(var12.getDirection() + (1.0F - var11) * 3.1415927F, 1.0F);
            var5 = this.x + var12.x * var3;
            var6 = this.y + var12.y * var3;
            var9 = IsoUtils.XToScreenExact(var5, var6, this.z, 0);
            var10 = IsoUtils.YToScreenExact(var5, var6, this.z, 0);
            LineDrawer.drawLine(var7, var8, var9, var10, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            this.setWeaponHitList(var2);

            for(int var13 = 0; var13 < HitList.size(); ++var13) {
               IsoMovingObject var14 = ((IsoGameCharacter.HitInfo)HitList.get(var13)).object;
               int var15 = ((IsoGameCharacter.HitInfo)HitList.get(var13)).chance;
               float var16 = 1.0F - (float)var15 / 100.0F;
               float var17 = 1.0F - var16;
               float var18 = Math.max(0.2F, 1.0F - (float)var15 / 100.0F) / 2.0F;
               var7 = IsoUtils.XToScreenExact(var14.x + 1.0F - var18, var14.y + var18, this.z, 0);
               var8 = IsoUtils.YToScreenExact(var14.x + 1.0F - var18, var14.y + var18, this.z, 0);
               var9 = IsoUtils.XToScreenExact(var14.x + 1.0F - var18, var14.y + 1.0F - var18, this.z, 0);
               var10 = IsoUtils.YToScreenExact(var14.x + 1.0F - var18, var14.y + 1.0F - var18, this.z, 0);
               float var19 = IsoUtils.XToScreenExact(var14.x + var18, var14.y + 1.0F - var18, this.z, 0);
               float var20 = IsoUtils.YToScreenExact(var14.x + var18, var14.y + 1.0F - var18, this.z, 0);
               float var21 = IsoUtils.XToScreenExact(var14.x + var18, var14.y + var18, this.z, 0);
               float var22 = IsoUtils.YToScreenExact(var14.x + var18, var14.y + var18, this.z, 0);
               SpriteRenderer.instance.renderPoly((int)var7, (int)var8, (int)var9, (int)var10, (int)var19, (int)var20, (int)var21, (int)var22, var16, var17, 0.0F, 1.0F);
            }
         }
      }

   }

   public void rendertalk(float var1) {
      if (this.Speaking) {
         float var2 = (float)this.sx;
         float var3 = (float)this.sy;
         var2 = (float)((int)var2);
         var3 = (float)((int)var3);
         var2 -= (float)((int)IsoCamera.getOffX());
         var3 -= (float)((int)IsoCamera.getOffY());
         var2 += 8.0F;
         var3 += 10.0F;
         var3 += var1;
         if (this.sayLine != null) {
            TextManager.instance.DrawStringCentre(UIFont.Dialogue, (double)((int)var2), (double)((int)var3), this.sayLine, (double)this.SpeakColour.r, (double)this.SpeakColour.g, (double)this.SpeakColour.b, (double)this.SpeakColour.a);
         }
      }

   }

   public void setCraftingByIndex(int var1, InventoryItem var2) {
      if (var1 == 0) {
         this.craftIngredient1 = var2;
      }

      if (var1 == 1) {
         this.craftIngredient2 = var2;
      }

      if (var1 == 2) {
         this.craftIngredient3 = var2;
      }

      if (var1 == 3) {
         this.craftIngredient4 = var2;
      }

   }

   public void setDefaultState() {
      this.stateMachine.changeState(this.defaultState);
   }

   public void SetOnFire() {
      if (!this.OnFire) {
         this.OnFire = true;
         float var1 = (float)Core.TileScale;
         this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, (int)(-(this.offsetX + (float)RENDER_OFFSET_X * var1)) + (8 - Rand.Next(16)), (int)(-(this.offsetY + (float)RENDER_OFFSET_Y * var1)) + (int)((float)(10 + Rand.Next(20)) * var1), true, 0, false, 0.7F, IsoFireManager.FireTintMod);
         IsoFireManager.AddBurningCharacter(this);
         int var2 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
         ((BodyPart)this.getBodyDamage().getBodyParts().get(var2)).setBurned();
         if (var1 == 2.0F) {
            int var3 = this.AttachedAnimSprite.size() - 1;
            ((IsoSpriteInstance)this.AttachedAnimSprite.get(var3)).setScale(var1, var1);
         }

      }
   }

   public void StopBurning() {
      if (this.OnFire) {
         IsoFireManager.RemoveBurningCharacter(this);
         this.OnFire = false;
         if (this.AttachedAnimSprite != null) {
            this.AttachedAnimSprite.clear();
         }

         if (this.AttachedAnimSpriteActual != null) {
            this.AttachedAnimSpriteActual.clear();
         }

      }
   }

   public void sendStopBurning() {
      if (GameClient.bClient) {
         if (this instanceof IsoPlayer) {
            IsoPlayer var1 = (IsoPlayer)this;
            if (var1.isLocalPlayer()) {
               this.StopBurning();
            } else {
               GameClient.sendStopFire((IsoGameCharacter)var1);
            }
         }

         if (this instanceof IsoZombie) {
            IsoZombie var2 = (IsoZombie)this;
            GameClient.sendStopFire((IsoGameCharacter)var2);
         }
      }

   }

   public void SpreadFire() {
      if (this.OnFire) {
         if (!GameServer.bServer || !(this instanceof IsoPlayer)) {
            if (!GameClient.bClient || !(this instanceof IsoZombie)) {
               if (!GameClient.bClient || !(this instanceof IsoPlayer) || !((IsoPlayer)this).bRemote) {
                  if (SandboxOptions.instance.FireSpread.getValue()) {
                     if (this.square != null && !this.square.getProperties().Is(IsoFlagType.burning) && Rand.Next(Rand.AdjustForFramerate(3000)) < this.FireSpreadProbability) {
                        IsoFireManager.StartFire(this.getCell(), this.square, false, 80);
                     }

                  }
               }
            }
         }
      }
   }

   public void Throw(HandWeapon var1) {
      if (this instanceof IsoPlayer && ((IsoPlayer)this).getJoypadBind() != -1) {
         Vector2 var2 = tempo.set(this.angle);
         var2.setLength(var1.getMaxRange());
         this.attackTargetSquare = this.getCell().getGridSquare((double)(this.getX() + var2.getX()), (double)(this.getY() + var2.getY()), (double)this.getZ());
      }

      float var5 = (float)this.attackTargetSquare.getX() - this.getX();
      if (var5 > 0.0F) {
         if ((float)this.attackTargetSquare.getX() - this.getX() > var1.getMaxRange()) {
            var5 = var1.getMaxRange();
         }
      } else if ((float)this.attackTargetSquare.getX() - this.getX() < -var1.getMaxRange()) {
         var5 = -var1.getMaxRange();
      }

      float var3 = (float)this.attackTargetSquare.getY() - this.getY();
      if (var3 > 0.0F) {
         if ((float)this.attackTargetSquare.getY() - this.getY() > var1.getMaxRange()) {
            var3 = var1.getMaxRange();
         }
      } else if ((float)this.attackTargetSquare.getY() - this.getY() < -var1.getMaxRange()) {
         var3 = -var1.getMaxRange();
      }

      new IsoMolotovCocktail(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6F, var5 * 0.4F, var3 * 0.4F, var1, this);
   }

   public void smashWindow(IsoWindow var1) {
      if (!var1.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
         this.StateMachineParams.clear();
         this.StateMachineParams.put(0, var1);
         this.StateMachineParams.put(1, "WindowSmash");
         this.StateMachineParams.put(4, true);
         this.getStateMachine().changeState(OpenWindowState.instance());
      }

   }

   public void openWindow(IsoWindow var1) {
      if (!var1.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
         this.StateMachineParams.clear();
         this.StateMachineParams.put(0, var1);
         this.getStateMachine().changeState(OpenWindowState.instance());
      }

   }

   public void climbThroughWindow(IsoWindow var1) {
      if (var1.canClimbThrough(this)) {
         float var2 = this.x - (float)((int)this.x);
         float var3 = this.y - (float)((int)this.y);
         byte var4 = 0;
         byte var5 = 0;
         if (var1.getX() > this.x && !var1.north) {
            var4 = -1;
         }

         if (var1.getY() > this.y && var1.north) {
            var5 = -1;
         }

         this.x = var1.getX() + var2 + (float)var4;
         this.y = var1.getY() + var3 + (float)var5;
         this.StateMachineParams.clear();
         this.StateMachineParams.put(0, var1);
         this.getStateMachine().changeState(ClimbThroughWindowState.instance());
      }

   }

   public void climbThroughWindow(IsoWindow var1, Integer var2) {
      if (var1.canClimbThrough(this)) {
         this.StateMachineParams.clear();
         this.StateMachineParams.put(0, var1);
         this.StateMachineParams.put(1, var2);
         this.getStateMachine().changeState(ClimbThroughWindowState.instance());
      }

   }

   public void climbThroughWindowFrame(IsoObject var1) {
      this.StateMachineParams.clear();
      this.StateMachineParams.put(0, var1);
      this.getStateMachine().changeState(ClimbThroughWindowState.instance());
   }

   public void climbSheetRope() {
      if (this.canClimbSheetRope(this.current)) {
         this.StateMachineParams.clear();
         this.getStateMachine().changeState(ClimbSheetRopeState.instance());
      }
   }

   public void climbDownSheetRope() {
      if (this.canClimbDownSheetRope(this.current)) {
         this.StateMachineParams.clear();
         this.getStateMachine().changeState(ClimbDownSheetRopeState.instance());
      }
   }

   public boolean canClimbSheetRope(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else {
         for(int var2 = var1.getZ(); var1 != null; var1 = this.getCell().getGridSquare(var1.getX(), var1.getY(), var1.getZ() + 1)) {
            if (!IsoWindow.isSheetRopeHere(var1)) {
               return false;
            }

            if (!IsoWindow.canClimbHere(var1)) {
               return false;
            }

            if (var1.TreatAsSolidFloor() && var1.getZ() > var2) {
               return false;
            }

            if (IsoWindow.isTopOfSheetRopeHere(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean canClimbDownSheetRope(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else {
         for(int var2 = var1.getZ(); var1 != null; var1 = this.getCell().getGridSquare(var1.getX(), var1.getY(), var1.getZ() - 1)) {
            if (!IsoWindow.isSheetRopeHere(var1)) {
               return false;
            }

            if (!IsoWindow.canClimbHere(var1)) {
               return false;
            }

            if (var1.TreatAsSolidFloor()) {
               return var1.getZ() < var2;
            }
         }

         return false;
      }
   }

   public void satOnChair(IsoObject var1) {
      this.StateMachineParams.clear();
      this.StateMachineParams.put(0, var1);
      this.getStateMachine().changeState(SatChairState.instance());
   }

   public void climbThroughWindow(IsoThumpable var1) {
      if (!var1.isBarricaded()) {
         float var2 = this.x - (float)((int)this.x);
         float var3 = this.y - (float)((int)this.y);
         byte var4 = 0;
         byte var5 = 0;
         if (var1.getX() > this.x && !var1.north) {
            var4 = -1;
         }

         if (var1.getY() > this.y && var1.north) {
            var5 = -1;
         }

         this.x = var1.getX() + var2 + (float)var4;
         this.y = var1.getY() + var3 + (float)var5;
         this.StateMachineParams.clear();
         this.StateMachineParams.put(0, var1);
         this.getStateMachine().changeState(ClimbThroughWindowState.instance());
      }

   }

   public void climbThroughWindow(IsoThumpable var1, Integer var2) {
      if (!var1.isBarricaded()) {
         this.StateMachineParams.clear();
         this.StateMachineParams.put(0, var1);
         this.StateMachineParams.put(1, var2);
         this.getStateMachine().changeState(ClimbThroughWindowState.instance());
      }

   }

   public boolean isAboveTopOfStairs() {
      if (this.z != 0.0F && !((double)(this.z - (float)((int)this.z)) > 0.01D) && (this.current == null || !this.current.TreatAsSolidFloor())) {
         IsoGridSquare var1 = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)(this.z - 1.0F));
         return var1 != null && (var1.Has(IsoObjectType.stairsTN) || var1.Has(IsoObjectType.stairsTW));
      } else {
         return false;
      }
   }

   public void update() {
      if (this.current != null) {
         if (this.sprite != null) {
            this.legsSprite = this.sprite;
         }

         if (!GameClient.bClient && !this.invisible && this.getCurrentSquare().getTrapPositionX() > -1 && this.getCurrentSquare().getTrapPositionY() > -1 && this.getCurrentSquare().getTrapPositionZ() > -1) {
            this.getCurrentSquare().explodeTrap();
         }

         if (this.getCurrentBuilding() != null && this.getCurrentBuilding().isToxic()) {
            if (this.getStats().getFatigue() < 1.0F) {
               this.getStats().setFatigue(this.getStats().getFatigue() + 1.0E-4F * (GameTime.getInstance().getMultiplier() / 1.6F));
            }

            if ((double)this.getStats().getFatigue() > 0.8D) {
               this.getBodyDamage().getBodyPart(BodyPartType.Head).ReduceHealth(0.1F);
            }

            this.getBodyDamage().getBodyPart(BodyPartType.Torso_Upper).ReduceHealth(0.1F);
         }

         if (this.RecoilDelay > 0.0F) {
            this.setRecoilDelay(this.getRecoilDelay() - this.RecoilDelayDecrease * GameTime.getInstance().getMultiplier());
         }

         this.sx = 0;
         this.sy = 0;
         if (this.current.getRoom() != null && this.current.getRoom().building.def.bAlarmed && !(this instanceof IsoZombie) && !this.current.getRoom().building.isAllExplored() && !GameClient.bClient) {
            AmbientStreamManager.instance.doAlarm(this.current.getRoom().def);
         }

         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            if (!this.current.isCanSee(var1)) {
               int var10002 = this.timeTillForgetLocation[var1]--;
               if (this.timeTillForgetLocation[var1] < 0) {
                  this.SpottedSinceAlphaZero[var1] = false;
               }

               if (!(this instanceof IsoPlayer)) {
                  this.targetAlpha[var1] = 0.0F;
               }
            } else if (testPlayerSpotInDarkness.Check()) {
               this.TestIfSeen(var1);
            }
         }

         this.llx = this.getLx();
         this.lly = this.getLy();
         this.setLx(this.getX());
         this.setLy(this.getY());
         this.setLz(this.getZ());
         float var8 = GameServer.bServer ? 10.0F : (float)PerformanceSettings.LockFPS;
         float var2 = 0.125F * (30.0F / var8);
         IsoDirections var3 = IsoDirections.Max;
         boolean var5;
         if (this.z > 0.0F) {
            if (!(this instanceof IsoZombie) && this.isClimbing()) {
               if (this.current.Is(IsoFlagType.climbSheetW) || this.current.Is(IsoFlagType.climbSheetTopW)) {
                  var3 = IsoDirections.W;
               }

               if (this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetTopE)) {
                  var3 = IsoDirections.E;
               }

               if (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetTopN)) {
                  var3 = IsoDirections.N;
               }

               if (this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetTopS)) {
                  var3 = IsoDirections.S;
               }
            }

            if (this.bClimbing) {
               var2 = 0.0F;
            }

            if (this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance()) {
               var2 = 0.0F;
            }

            this.lastFallSpeed = var2;
            var5 = false;
            if (this.lastFallSpeed < 0.0F && var2 >= 0.0F) {
               var2 = this.lastFallSpeed;
               var5 = true;
            }

            if (!this.current.TreatAsSolidFloor()) {
               if (var3 != IsoDirections.Max) {
                  this.dir = var3;
               }

               this.fallTime += 6 + Rand.Next(3);
               if (var3 != IsoDirections.Max) {
                  this.fallTime = 0;
               }

               if (this.fallTime < 20 && this.isAboveTopOfStairs()) {
                  this.fallTime = 0;
               }

               this.setZ(this.getZ() - var2);
            } else if (!(this.getZ() > (float)((int)this.getZ())) && !(var2 < 0.0F)) {
               this.DoLand();
               this.fallTime = 0;
               this.bFalling = false;
            } else {
               if (var3 != IsoDirections.Max) {
                  this.dir = var3;
               }

               if (!this.current.Has(IsoObjectType.stairsBN) && !this.current.Has(IsoObjectType.stairsTN) && !this.current.Has(IsoObjectType.stairsMN) && !this.current.Has(IsoObjectType.stairsBW) && !this.current.Has(IsoObjectType.stairsMW) && !this.current.Has(IsoObjectType.stairsTW)) {
                  if (!this.wasOnStairs) {
                     this.fallTime += 6 + Rand.Next(3);
                     if (var3 != IsoDirections.Max) {
                        this.fallTime = 0;
                     }

                     this.setZ(this.getZ() - var2);
                     if (this.z < (float)((int)this.llz)) {
                        this.z = (float)((int)this.llz);
                        this.DoLand();
                        this.fallTime = 0;
                        this.bFalling = false;
                     }
                  } else {
                     this.wasOnStairs = false;
                  }
               } else {
                  this.fallTime = 0;
                  this.bFalling = false;
                  this.wasOnStairs = true;
               }
            }

            if (var5) {
               ;
            }
         } else {
            this.DoLand();
            this.fallTime = 0;
            this.bFalling = false;
         }

         this.llz = this.lz;
         if (!FrameLoader.bClient) {
            if (!this.Orders.isEmpty()) {
               this.Order = (Order)this.Orders.peek();
            } else {
               this.Order = null;
            }

            if (!this.PersonalNeeds.isEmpty()) {
               this.PersonalNeed = (Order)this.PersonalNeeds.peek();
            } else {
               this.PersonalNeed = null;
            }
         }

         if (this.descriptor != null) {
            this.descriptor.Instance = this;
         }

         if (this.getCurrentSquare() != null && this.getCurrentSquare().getRoom() != null) {
         }

         if (this.bUseParts) {
            if (this.topSprite != null) {
               this.topSprite.def.Frame = this.def.Frame;
            }

            if (this.bottomsSprite != null) {
               this.bottomsSprite.def.Frame = this.def.Frame;
            }

            if (this.shoeSprite != null) {
               this.shoeSprite.def.Frame = this.def.Frame;
            }
         }

         int var9;
         int var11;
         if (!(this instanceof IsoZombie)) {
            Stats var10000;
            if (!FrameLoader.bClient) {
               if (this.HasTrait("Agoraphobic") && this.getCurrentSquare().getRoom() == null) {
                  var10000 = this.stats;
                  var10000.Panic += 0.5F * (GameTime.getInstance().getMultiplier() / 1.6F);
               }

               if (this.HasTrait("Claustophobic") && this.getCurrentSquare().getRoom() != null) {
                  float var4 = 1.0F;
                  var11 = this.getCurrentSquare().getRoom().def.getH() * this.getCurrentSquare().getRoom().def.getW();
                  var4 = 1.0F - (float)var11 / 70.0F;
                  if (var4 < 0.0F) {
                     var4 = 0.0F;
                  }

                  float var6 = 0.6F * var4 * (GameTime.getInstance().getMultiplier() / 1.6F);
                  if (var6 > 0.6F) {
                     var6 = 0.6F;
                  }

                  var10000 = this.stats;
                  var10000.Panic += var6;
               }
            }

            if (this.Moodles != null) {
               this.Moodles.Update();
            }

            if (!FrameLoader.bClient) {
               if (this.Asleep) {
                  this.BetaEffect = 0.0F;
                  this.SleepingTabletEffect = 0.0F;
               }

               if (this.BetaEffect > 0.0F) {
                  this.BetaEffect -= GameTime.getInstance().getMultiplier() / 1.6F;
                  var10000 = this.stats;
                  var10000.Panic -= 0.6F * (GameTime.getInstance().getMultiplier() / 1.6F);
                  if (this.stats.Panic < 0.0F) {
                     this.stats.Panic = 0.0F;
                  }
               } else {
                  this.BetaDelta = 0.0F;
               }

               if (this.DepressFirstTakeTime > 0.0F || this.DepressEffect > 0.0F) {
                  this.DepressFirstTakeTime -= GameTime.getInstance().getMultiplier() / 1.6F;
                  if (this.DepressFirstTakeTime < 0.0F) {
                     this.DepressFirstTakeTime = -1.0F;
                     this.DepressEffect -= GameTime.getInstance().getMultiplier() / 1.6F;
                     this.getBodyDamage().setUnhappynessLevel(this.getBodyDamage().getUnhappynessLevel() - 0.03F * (GameTime.getInstance().getMultiplier() / 1.6F));
                     if (this.getBodyDamage().getUnhappynessLevel() < 0.0F) {
                        this.getBodyDamage().setUnhappynessLevel(0.0F);
                     }
                  }
               }

               if (this.DepressEffect < 0.0F) {
                  this.DepressEffect = 0.0F;
               }

               if (this.SleepingTabletEffect > 0.0F) {
                  this.SleepingTabletEffect -= GameTime.getInstance().getMultiplier() / 1.6F;
                  var10000 = this.stats;
                  var10000.fatigue += 0.0016666667F * this.SleepingTabletDelta * (GameTime.getInstance().getMultiplier() / 1.6F);
               } else {
                  this.SleepingTabletDelta = 0.0F;
               }

               var9 = this.Moodles.getMoodleLevel(MoodleType.Panic);
               if (var9 == 2) {
                  var10000 = this.stats;
                  var10000.Sanity -= 3.2E-7F;
               } else if (var9 == 3) {
                  var10000 = this.stats;
                  var10000.Sanity -= 4.8000004E-7F;
               } else if (var9 == 4) {
                  var10000 = this.stats;
                  var10000.Sanity -= 8.0E-7F;
               } else if (var9 == 0) {
                  var10000 = this.stats;
                  var10000.Sanity += 1.0E-7F;
               }

               var11 = this.Moodles.getMoodleLevel(MoodleType.Tired);
               if (var11 == 4) {
                  var10000 = this.stats;
                  var10000.Sanity -= 2.0E-6F;
               }

               if (this.stats.Sanity < 0.0F) {
                  this.stats.Sanity = 0.0F;
               }

               if (this.stats.Sanity > 1.0F) {
                  this.stats.Sanity = 1.0F;
               }
            }
         }

         if (!FrameLoader.bClient) {
            if (!this.CharacterActions.isEmpty()) {
               BaseAction var10 = (BaseAction)this.CharacterActions.get(0);
               var5 = var10.valid();
               if (var5) {
                  var10.update();
               }

               if (!var5 || var10.finished() || var10.forceComplete || var10.forceStop) {
                  if (var10.finished() || var10.forceComplete) {
                     var10.perform();
                  }

                  if (var10.finished() || var10.forceComplete || var10.forceStop || !var5) {
                     if (var10.forceStop || !var5) {
                        var10.stop();
                     }

                     this.CharacterActions.removeElement(var10);
                     if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
                        UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
                     }
                  }
               }
            }

            for(var9 = 0; var9 < this.EnemyList.size(); ++var9) {
               IsoGameCharacter var12 = (IsoGameCharacter)this.EnemyList.get(var9);
               if (var12.Health < 0.0F) {
                  this.EnemyList.remove(var12);
                  --var9;
               }
            }
         }

         if (SystemDisabler.doCharacterStats) {
            this.BodyDamage.Update();
         }

         if (!FrameLoader.bClient) {
            if (this == IsoPlayer.instance) {
               if (this.craftIngredient1 != null && this.craftIngredient1.getUses() <= 0) {
                  this.craftIngredient1 = null;
               }

               if (this.craftIngredient2 != null && this.craftIngredient2.getUses() <= 0) {
                  this.craftIngredient2 = null;
               }

               if (this.craftIngredient3 != null && this.craftIngredient3.getUses() <= 0) {
                  this.craftIngredient3 = null;
               }

               if (this.craftIngredient4 != null && this.craftIngredient4.getUses() <= 0) {
                  this.craftIngredient4 = null;
               }

               if (this.leftHandItem != null && this.leftHandItem.getUses() <= 0) {
                  this.leftHandItem = null;
               }

               if (this.rightHandItem != null && this.rightHandItem.getUses() <= 0) {
                  this.rightHandItem = null;
               }
            }

            this.lastdir = this.dir;
            if (SystemDisabler.doCharacterStats) {
               this.calculateStats();
            }

            this.UpdateWounds();
         }

         this.moveForwardVec.x = 0.0F;
         this.moveForwardVec.y = 0.0F;
         if (!this.Asleep || !(this instanceof IsoPlayer)) {
            this.setLx(this.getX());
            this.setLy(this.getY());
            this.setLz(this.getZ());
            this.square = this.getCurrentSquare();
            if (this.sprite != null) {
               if (!this.bUseParts) {
                  this.sprite.update(this.def);
               } else {
                  this.legsSprite.update(this.def);
               }
            }

            this.setStateEventDelayTimer(this.getStateEventDelayTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
         }

         if (!(this instanceof IsoZombie) || !GameClient.bClient) {
            this.stateMachine.update();
         }

         if (this instanceof IsoZombie && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
            DebugLog.log(DebugType.Zombie, "Zombie added to ReusableZombies after stateMachine.update - RETURNING " + this);
         } else {
            if (this instanceof IsoPlayer) {
               this.ensureOnTile();
            }

            if (!FrameLoader.bClient) {
               if ((this instanceof IsoPlayer || this instanceof IsoSurvivor) && this.RemoteID == -1) {
                  if (this.Health > 0.0F && this.BodyDamage.getHealth() > 0.0F && !this.Asleep && this.getAllowBehaviours()) {
                     if (!this.decisionPath.DecisionPath.isEmpty()) {
                        this.decisionPath.DecisionPath.clear();
                     }

                     this.masterBehaviorList.process(this.decisionPath, this);
                  }

                  if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                     RainManager.SetPlayerLocation(((IsoPlayer)this).getPlayerNum(), this.getCurrentSquare());
                  }
               }

               this.FireCheck();
               this.SpreadFire();
               this.ReduceHealthWhenBurning();
            }

            this.updateTextObjects();
            if (this.stateMachine.getCurrent() == StaggerBackDieState.instance()) {
               if (this.getStateEventDelayTimer() > 20.0F) {
                  this.BloodImpactX = this.getX();
                  this.BloodImpactY = this.getY();
                  this.BloodImpactZ = this.getZ();
               }
            } else {
               this.BloodImpactX = this.getX();
               this.BloodImpactY = this.getY();
               this.BloodImpactZ = this.getZ();
            }

            if (!FrameLoader.bClient && !(this instanceof IsoZombie)) {
               for(var9 = 0; var9 < this.inventory.Items.size(); ++var9) {
                  InventoryItem var14 = (InventoryItem)this.inventory.Items.get(var9);
                  if (var14 instanceof IUpdater) {
                     ((IUpdater)var14).update();
                  }
               }
            }

            this.LastZombieKills = this.ZombieKills;
            if (this.AttachedAnimSprite != null && this.AttachedAnimSpriteActual != null) {
               var9 = this.AttachedAnimSprite.size();

               for(var11 = 0; var11 < var9; ++var11) {
                  IsoSpriteInstance var13 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var11);
                  IsoSprite var7 = (IsoSprite)this.AttachedAnimSpriteActual.get(var11);
                  var13.update();
                  var13.Frame += var13.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
                  if ((int)var13.Frame >= var7.CurrentAnim.Frames.size() && var7.Loop && var13.Looped) {
                     var13.Frame = 0.0F;
                  }
               }
            }

            if (this.godMod) {
               this.getStats().setFatigue(0.0F);
               this.getStats().setEndurance(1.0F);
               this.getBodyDamage().setTemperature(37.0F);
               this.getStats().setHunger(0.0F);
            }

         }
      }
   }

   public void DoFloorSplat(IsoGridSquare var1, String var2, boolean var3, float var4, float var5) {
      if (var1 != null) {
         var1.DirtySlice();
         IsoObject var6 = null;

         for(int var7 = 0; var7 < var1.getObjects().size(); ++var7) {
            IsoObject var8 = (IsoObject)var1.getObjects().get(var7);
            if (var8.sprite != null && var8.sprite.getProperties().Is(IsoFlagType.solidfloor) && var6 == null) {
               var6 = var8;
            }
         }

         if (var6 != null && var6.sprite != null && (var6.sprite.getProperties().Is(IsoFlagType.vegitation) || var6.sprite.getProperties().Is(IsoFlagType.solidfloor))) {
            IsoSprite var10 = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var2, 0);
            if (var10 == null) {
               return;
            }

            if (var6.AttachedAnimSprite.size() > 7) {
               return;
            }

            IsoSpriteInstance var11 = IsoSpriteInstance.get(var10);
            var6.AttachedAnimSprite.add(var11);
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).Flip = var3;
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).tintr = 0.5F + (float)Rand.Next(100) / 2000.0F;
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).tintg = 0.7F + (float)Rand.Next(300) / 1000.0F;
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).tintb = 0.7F + (float)Rand.Next(300) / 1000.0F;
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).SetAlpha(0.4F * var5 * 0.6F);
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).SetTargetAlpha(0.4F * var5 * 0.6F);
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).offZ = -var4;
            float var9 = 0.0F;
            ((IsoSpriteInstance)var6.AttachedAnimSprite.get(var6.AttachedAnimSprite.size() - 1)).offX = var9;
         }

      }
   }

   void DoSplat(IsoGridSquare var1, String var2, boolean var3, IsoFlagType var4, float var5, float var6, float var7) {
      if (var1 != null) {
         var1.DoSplat(var2, var3, var4, var5, var6, var7);
      }
   }

   InventoryItem FindAndReturn(String var1, InventoryItem var2) {
      if (var2 == null) {
         return null;
      } else if (var1 == null) {
         return var2;
      } else {
         return var1.equals(var2.getType()) ? var2 : null;
      }
   }

   public boolean onMouseLeftClick(int var1, int var2) {
      if (IsoCamera.CamCharacter != IsoPlayer.instance && Core.bDebug) {
         IsoCamera.CamCharacter = this;
      }

      return super.onMouseLeftClick(var1, var2);
   }

   private void calculateStats() {
      if (!(this instanceof IsoZombie)) {
         if (!(this instanceof IsoPlayer) || !((IsoPlayer)this).bRemote) {
            if (GameServer.bServer) {
               this.stats.fatigue = 0.0F;
            } else if (GameClient.bClient && (!ServerOptions.instance.SleepAllowed.getValue() || !ServerOptions.instance.SleepNeeded.getValue())) {
               this.stats.fatigue = 0.0F;
            }

            if (!LuaHookManager.TriggerHook("CalculateStats", this)) {
               if (this.stats.endurance <= 0.0F) {
                  this.stats.endurance = 0.0F;
               }

               if (this.stats.endurance > 1.0F) {
                  this.stats.endurance = 1.0F;
               }

               this.stats.endurancelast = this.stats.endurance;
               Stats var10000;
               if (this.stats.Tripping) {
                  var10000 = this.stats;
                  var10000.TrippingRotAngle += 0.06F;
               } else {
                  var10000 = this.stats;
                  var10000.TrippingRotAngle += 0.0F;
               }

               float var1 = 1.0F;
               if (this.HasTrait("HighThirst")) {
                  var1 = (float)((double)var1 * 2.0D);
               }

               if (this.HasTrait("LowThirst")) {
                  var1 = (float)((double)var1 * 0.5D);
               }

               if (this == IsoPlayer.instance && !IsoPlayer.instance.GhostMode) {
                  if (this == IsoPlayer.instance && this.Asleep) {
                     var10000 = this.stats;
                     var10000.thirst = (float)((double)var10000.thirst + ZomboidGlobals.ThirstSleepingIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)var1);
                  } else {
                     var10000 = this.stats;
                     var10000.thirst = (float)((double)var10000.thirst + ZomboidGlobals.ThirstIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * this.getRunningThirstReduction() * (double)GameTime.instance.getDeltaMinutesPerDay());
                  }

                  if (this.stats.thirst > 1.0F) {
                     this.stats.thirst = 1.0F;
                  }
               }

               this.autoDrink();
               float var2 = 1.0F - this.stats.hunger;
               if (this.HasTrait("HeartyAppitite")) {
                  var2 *= 1.5F;
               }

               if (this.HasTrait("LightEater")) {
                  var2 *= 0.75F;
               }

               float var3 = 1.0F;
               if (this.HasTrait("Cowardly")) {
                  var3 = 2.0F;
               }

               if (this.HasTrait("Brave")) {
                  var3 = 0.3F;
               }

               if (this.stats.Panic > 100.0F) {
                  this.stats.Panic = 100.0F;
               }

               var10000 = this.stats;
               var10000.stress = (float)((double)var10000.stress + (double)WorldSoundManager.instance.getStressFromSounds((int)this.getX(), (int)this.getY(), (int)this.getZ()) * ZomboidGlobals.StressFromSoundsMultiplier);
               if (this.BodyDamage.getNumPartsBitten() > 0) {
                  var10000 = this.stats;
                  var10000.stress = (float)((double)var10000.stress + ZomboidGlobals.StressFromBiteOrScratch * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
               }

               if (this.BodyDamage.getNumPartsScratched() > 0) {
                  var10000 = this.stats;
                  var10000.stress = (float)((double)var10000.stress + ZomboidGlobals.StressFromBiteOrScratch * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
               }

               if (this.BodyDamage.IsInfected() || this.BodyDamage.IsFakeInfected()) {
                  var10000 = this.stats;
                  var10000.stress = (float)((double)var10000.stress + ZomboidGlobals.StressFromBiteOrScratch * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
               }

               if (this.HasTrait("Brooding")) {
                  var10000 = this.stats;
                  var10000.Anger = (float)((double)var10000.Anger - ZomboidGlobals.AngerDecrease * ZomboidGlobals.BroodingAngerDecreaseMultiplier * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
               } else {
                  var10000 = this.stats;
                  var10000.Anger = (float)((double)var10000.Anger - ZomboidGlobals.AngerDecrease * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
               }

               if (this.stats.Anger > 1.0F) {
                  this.stats.Anger = 1.0F;
               }

               if (this.stats.Anger < 0.0F) {
                  this.stats.Anger = 0.0F;
               }

               float var4;
               float var5;
               float var7;
               float var10;
               if (IsoPlayer.instance == this && IsoPlayer.instance.Asleep) {
                  var4 = 2.0F;
                  if (IsoPlayer.allPlayersAsleep()) {
                     var4 *= GameTime.instance.getDeltaMinutesPerDay();
                  }

                  var10000 = this.stats;
                  var10000.endurance = (float)((double)var10000.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)GameTime.instance.getMultiplier() * (double)var4);
                  if (this.stats.endurance > 1.0F) {
                     this.stats.endurance = 1.0F;
                  }

                  if (this.stats.fatigue > 0.0F) {
                     var5 = 1.0F;
                     if (this.HasTrait("Insomniac")) {
                        var5 *= 0.5F;
                     }

                     var10 = 1.0F;
                     if ("goodBed".equals(this.getBedType())) {
                        var10 = 1.1F;
                     }

                     if ("badBed".equals(this.getBedType())) {
                        var10 = 0.9F;
                     }

                     var7 = 1.0F / GameTime.instance.getMinutesPerDay() / 60.0F * GameTime.instance.getMultiplier() / 2.0F;
                     this.timeOfSleep += var7;
                     if (this.timeOfSleep > this.delayToActuallySleep) {
                        if (this.stats.fatigue <= 0.3F) {
                           var10000 = this.stats;
                           var10000.fatigue -= var7 / 7.0F * 0.3F * var5 * var10;
                        } else {
                           var10000 = this.stats;
                           var10000.fatigue -= var7 / 5.0F * 0.7F * var5 * var10;
                        }
                     }

                     if (this.stats.fatigue < 0.0F) {
                        this.stats.fatigue = 0.0F;
                     }
                  }

                  if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
                     var10000 = this.stats;
                     var10000.hunger = (float)((double)var10000.hunger + ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)var2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                  } else {
                     var10000 = this.stats;
                     var10000.hunger += (float)(ZomboidGlobals.HungerIncreaseWhenWellFed * SandboxOptions.instance.getStatsDecreaseMultiplier() * ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                  }

                  if (this.ForceWakeUpTime == 0.0F) {
                     this.ForceWakeUpTime = 9.0F;
                  }

                  var5 = GameTime.getInstance().getTimeOfDay();
                  var10 = GameTime.getInstance().getLastTimeOfDay();
                  if (var10 > var5) {
                     if (var10 < this.ForceWakeUpTime) {
                        var5 += 24.0F;
                     } else {
                        var10 -= 24.0F;
                     }
                  }

                  boolean var11 = var5 >= this.ForceWakeUpTime && var10 < this.ForceWakeUpTime;
                  if (IsoPlayer.instance.getAsleepTime() > 16.0F) {
                     var11 = true;
                  }

                  if (GameClient.bClient || IsoPlayer.numPlayers > 1) {
                     var11 = var11 || IsoPlayer.instance.pressedAim() || IsoPlayer.instance.pressedMovement();
                  }

                  if (this.ForceWakeUp) {
                     var11 = true;
                  }

                  if (this.Asleep && var11) {
                     this.ForceWakeUp = false;
                     SleepingEvent.instance.wakeUp(this);
                     this.ForceWakeUpTime = -1.0F;
                     if (GameClient.bClient) {
                        GameClient.instance.sendPlayer((IsoPlayer)this);
                     }

                     ((IsoPlayer)this).dirtyRecalcGridStackTime = 20.0F;
                  }
               } else {
                  var10000 = this.stats;
                  var10000.stress = (float)((double)var10000.stress - ZomboidGlobals.StressReduction * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                  var4 = 1.0F - this.stats.endurance;
                  if (var4 < 0.3F) {
                     var4 = 0.3F;
                  }

                  var5 = 1.0F;
                  if (this.HasTrait("NeedsLessSleep")) {
                     var5 = 0.7F;
                  }

                  if (this.HasTrait("NeedsMoreSleep")) {
                     var5 = 1.3F;
                  }

                  double var6 = SandboxOptions.instance.getStatsDecreaseMultiplier();
                  if (var6 < 1.0D) {
                     var6 = 1.0D;
                  }

                  var10000 = this.stats;
                  var10000.fatigue = (float)((double)var10000.fatigue + ZomboidGlobals.FatigueIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)var4 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)var5);
                  if ((!(this instanceof IsoPlayer) || !((IsoPlayer)this).IsRunning()) && this.getCurrentState() != SwipeStatePlayer.instance()) {
                     if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
                        var10000 = this.stats;
                        var10000.hunger = (float)((double)var10000.hunger + ZomboidGlobals.HungerIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)var2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                     } else {
                        var10000 = this.stats;
                        var10000.hunger = (float)((double)var10000.hunger + (double)((float)ZomboidGlobals.HungerIncreaseWhenWellFed) * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                     }
                  } else if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
                     var10000 = this.stats;
                     var10000.hunger = (float)((double)var10000.hunger + ZomboidGlobals.HungerIncreaseWhenExercise / 3.0D * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)var2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                  } else {
                     var10000 = this.stats;
                     var10000.hunger = (float)((double)var10000.hunger + ZomboidGlobals.HungerIncreaseWhenExercise * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)var2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
                  }

                  if (this.getCurrentSquare() == this.getLastSquare() && !this.isReading()) {
                     var10000 = this.stats;
                     var10000.idleboredom += 5.0E-5F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
                     var10000 = this.stats;
                     var10000.idleboredom += 0.00125F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
                  }

                  if (this.getCurrentSquare() != null && this.getLastSquare() != null && this.getCurrentSquare().getRoom() == this.getLastSquare().getRoom() && this.getCurrentSquare().getRoom() != null && !this.isReading()) {
                     var10000 = this.stats;
                     var10000.idleboredom += 1.0E-4F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
                     var10000 = this.stats;
                     var10000.idleboredom += 0.00125F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
                  }
               }

               if (this.getLx() != this.getX() || this.getLy() != this.getY()) {
                  tempo.x = this.getX() - this.getLx();
                  tempo.y = this.getY() - this.getLy();
                  Vector2 var9 = tempo;
                  var5 = 1.0F;
                  var10 = var9.getLength() / this.getMoveSpeed();
                  if (var10 < 1.0F) {
                     var5 = 0.5F;
                  }

                  if (var10 > 1.0F) {
                     var5 = 4.0F;
                  }

                  var7 = var9.getLength() * 0.0018F * var5;
                  var7 /= 0.06F;
                  if (var7 > 0.0F && var7 < 0.3F) {
                     var7 = 0.3F;
                  }

                  var7 = 1.0F - var7;
                  if (var7 < 0.0F) {
                     var7 = 1.0F;
                  }

                  if (var7 > 1.0F) {
                     var7 = 1.0F;
                  }

                  if (var9.getLength() != 0.0F && this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 0) {
                     float var8 = 1.0F;
                     if (this.HasTrait("Asthmatic")) {
                        var8 = 1.3F;
                     }

                     var10000 = this.stats;
                     var10000.endurance -= 4.0000004E-6F * this.getRecoveryMod() * GameTime.instance.getMultiplier() * var7 * var8;
                  }
               }

               if (this.stats.endurance < 0.0F) {
                  this.stats.endurance = 0.0F;
               }

               if (this.stats.endurance > 1.0F) {
                  this.stats.endurance = 1.0F;
               }

               if (this.stats.hunger > 1.0F) {
                  this.stats.hunger = 1.0F;
               }

               if (this.stats.hunger < 0.0F) {
                  this.stats.hunger = 0.0F;
               }

               if (this.stats.stress > 1.0F) {
                  this.stats.stress = 1.0F;
               }

               if (this.stats.stress < 0.0F) {
                  this.stats.stress = 0.0F;
               }

               if (this.stats.fatigue > 1.0F) {
                  this.stats.fatigue = 1.0F;
               }

               if (this.stats.fatigue < 0.0F) {
                  this.stats.fatigue = 0.0F;
               }

               var4 = 1.0F - this.stats.getStress() - 0.5F;
               var4 *= 1.0E-4F;
               if (var4 > 0.0F) {
                  var4 += 0.5F;
               }

               var10000 = this.stats;
               var10000.morale += var4;
               if (this.stats.morale > 1.0F) {
                  this.stats.morale = 1.0F;
               }

               if (this.stats.morale < 0.0F) {
                  this.stats.morale = 0.0F;
               }

               if (this.stats.endurance < 0.0F) {
                  this.stats.endurance = 0.0F;
               }

               this.stats.fitness = (float)this.getPerkLevel(PerkFactory.Perks.Fitness) / 5.0F - 1.0F;
               if (this.stats.fitness > 1.0F) {
                  this.stats.fitness = 1.0F;
               }

               if (this.stats.fitness < -1.0F) {
                  this.stats.fitness = -1.0F;
               }

            }
         }
      }
   }

   private double getRunningThirstReduction() {
      return this == IsoPlayer.instance && IsoPlayer.instance.IsRunning() ? 1.2D : 1.0D;
   }

   private void checkDrawWeaponPost(float var1, float var2, float var3, ColorInfo var4) {
      if (this.sprite != null) {
         if (this.sprite.CurrentAnim != null) {
            if (this.sprite.CurrentAnim.name != null) {
               if (this.leftHandItem instanceof HandWeapon) {
                  this.useHandWeapon = (HandWeapon)this.leftHandItem;
               } else {
                  this.useHandWeapon = null;
               }

               WeaponOverlayUtils.DrawWeapon(this.useHandWeapon, this, this.sprite, var1, var2, var3, var4);
            }
         }
      }
   }

   public void faceDirection(IsoGameCharacter var1) {
      tempo.x = var1.x;
      tempo.y = var1.y;
      Vector2 var10000 = tempo;
      var10000.x -= this.x;
      var10000 = tempo;
      var10000.y -= this.y;
      tempo.normalize();
      this.DirectionFromVector(tempo);
   }

   public void faceLocation(float var1, float var2) {
      tempo.x = var1 + 0.5F;
      tempo.y = var2 + 0.5F;
      Vector2 var10000 = tempo;
      var10000.x -= this.getX();
      var10000 = tempo;
      var10000.y -= this.getY();
      this.DirectionFromVector(tempo);
      this.getVectorFromDirection(this.angle);
   }

   private void checkDrawWeaponPre(float var1, float var2, float var3, ColorInfo var4) {
      if (this.sprite != null) {
         if (this.sprite.CurrentAnim != null) {
            if (this.sprite.CurrentAnim.name != null) {
               if (this.dir != IsoDirections.S && this.dir != IsoDirections.SE && this.dir != IsoDirections.E && this.dir != IsoDirections.NE && this.dir != IsoDirections.SW) {
                  if (this.sprite.CurrentAnim.name.contains("Attack_")) {
                     ;
                  }
               }
            }
         }
      }
   }

   public void splatBlood(int var1, float var2) {
      if (this.getCurrentSquare() != null) {
         this.getCurrentSquare().splatBlood(var1, var2);
      }
   }

   private void UpdateWounds() {
      for(int var1 = 0; var1 < this.wounds.size(); ++var1) {
         IsoGameCharacter.Wound var2 = (IsoGameCharacter.Wound)this.wounds.get(var1);
         if (var2.tourniquet) {
            var2.bleeding -= 5.0E-5F;
         }

         if (var2.bandaged) {
            var2.bleeding -= 2.0E-5F;
         }
      }

   }

   public boolean isOutside() {
      return this.getCurrentSquare() == null ? false : this.getCurrentSquare().isOutside();
   }

   public boolean isFemale() {
      return this.bFemale;
   }

   public void setFemale(boolean var1) {
      this.bFemale = var1;
   }

   public void setLastHitCount(int var1) {
      this.lastHitCount = var1;
   }

   public int getLastHitCount() {
      return this.lastHitCount;
   }

   public int getSurvivorKills() {
      return this.SurvivorKills;
   }

   public void setSurvivorKills(int var1) {
      this.SurvivorKills = var1;
   }

   public int getAge() {
      return this.age;
   }

   public void setAge(int var1) {
      this.age = var1;
   }

   public void exert(float var1) {
      if (this.HasTrait("PlaysFootball")) {
         var1 *= 0.9F;
      }

      if (this.HasTrait("Jogger")) {
         var1 *= 0.9F;
      }

      Stats var10000 = this.stats;
      var10000.endurance -= var1;
   }

   public IsoGameCharacter.PerkInfo getPerkInfo(PerkFactory.Perks var1) {
      for(int var2 = 0; var2 < this.PerkList.size(); ++var2) {
         IsoGameCharacter.PerkInfo var3 = (IsoGameCharacter.PerkInfo)this.PerkList.get(var2);
         if (var3.perkType == var1) {
            return var3;
         }
      }

      return null;
   }

   public boolean isSat() {
      return this.isSat;
   }

   public void setSat(boolean var1) {
      this.isSat = var1;
   }

   public IsoObject getChair() {
      return this.chair;
   }

   public void setChair(IsoObject var1) {
      this.chair = var1;
   }

   public void HitSilence(HandWeapon var1, IsoGameCharacter var2, float var3, boolean var4, float var5) {
      if (var2 != null && var1 != null) {
         if (this.getStateMachine().getCurrent() != StaggerBackState.instance()) {
            if (this.isOnFloor()) {
               var3 = 1.0F;
               var5 = 2.0F;
               var4 = false;
               this.setReanimateTimer(this.getReanimateTimer() + 38.0F);
            }

            if (var1.getName().contains("Bare Hands") || var2.legsSprite.CurrentAnim.name != null && var2.legsSprite.CurrentAnim.name.contains("Shove")) {
               var4 = true;
               this.noDamage = true;
            }

            this.staggerTimeMod = var1.getPushBackMod() * var1.getKnockbackMod(var2) * var2.getShovingMod();
            float var6 = 0.0F;
            float var7;
            float var8;
            if (var2 instanceof IsoPlayer && !var1.bIsAimedFirearm) {
               var7 = ((IsoPlayer)var2).useChargeDelta;
               if (var7 > 1.0F) {
                  var7 = 1.0F;
               }

               if (var7 < 0.0F) {
                  var7 = 0.0F;
               }

               var8 = var1.getMinDamage() + (var1.getMaxDamage() - var1.getMinDamage()) * var7;
               var6 = var8;
            } else {
               var6 = (float)Rand.Next((int)((var1.getMaxDamage() - var1.getMinDamage()) * 1000.0F)) / 1000.0F + var1.getMinDamage();
            }

            var6 *= var5;
            var7 = var6 * var1.getKnockbackMod(var2) * var2.getShovingMod();
            if (var7 > 1.0F) {
               var7 = 1.0F;
            }

            this.setHitForce(var7);
            this.AttackedBy = var2;
            var8 = IsoUtils.DistanceTo(var2.getX(), var2.getY(), this.getX(), this.getY());
            var8 -= var1.getMinRange();
            var8 /= var1.getMaxRange(var2);
            var8 = 1.0F - var8;
            if (var8 > 1.0F) {
               var8 = 1.0F;
            }

            this.hitDir.x = this.getX();
            this.hitDir.y = this.getY();
            Vector2 var10000 = this.hitDir;
            var10000.x -= var2.getX();
            var10000 = this.hitDir;
            var10000.y -= var2.getY();
            this.getHitDir().normalize();
            var10000 = this.hitDir;
            var10000.x *= var1.getPushBackMod();
            var10000 = this.hitDir;
            var10000.y *= var1.getPushBackMod();
            this.hitDir.rotate(var1.HitAngleMod);
            float var9 = var2.stats.endurance;
            var9 *= var2.knockbackAttackMod;
            this.hitBy = var2;
            if (var9 < 0.5F) {
               var9 *= 1.3F;
               if (var9 < 0.4F) {
                  var9 = 0.4F;
               }

               this.setHitForce(this.getHitForce() * var9);
            }

            if (!var1.isRangeFalloff()) {
               var8 = 1.0F;
            }

            if (!var1.isShareDamage()) {
               var3 = 1.0F;
            }

            if (var2 instanceof IsoPlayer) {
               this.setHitForce(this.getHitForce() * 2.0F);
            }

            Vector2 var10 = tempVector2_1.set(this.getX(), this.getY());
            Vector2 var11 = tempVector2_2.set(var2.getX(), var2.getY());
            var10.x -= var11.x;
            var10.y -= var11.y;
            Vector2 var12 = this.getVectorFromDirection(tempVector2_2);
            var10.normalize();
            float var13 = var10.dot(var12);
            if (var13 > -0.3F) {
               var6 *= 1.5F;
            }

            float var14 = var1.CriticalChance;
            if (this.isOnFloor()) {
               var14 *= 2.0F;
            }

            if ((float)Rand.Next(100) < var14) {
               var6 *= 10.0F;
            }

            if (!this.isOnFloor() && var1.getScriptItem().Categories.contains("Axe")) {
               var6 *= 2.0F;
            }

            if (var2 instanceof IsoPlayer) {
               if (!var4) {
                  if (var1.isAimedFirearm()) {
                     this.Health -= var6 * 0.7F;
                  } else {
                     this.Health -= var6 * 0.15F;
                  }
               }
            } else if (!var4) {
               if (var1.isAimedFirearm()) {
                  this.Health -= var6 * 0.7F;
               } else {
                  this.Health -= var6 * 0.15F;
               }
            }

            float var15 = 12.0F;
            if (var2 instanceof IsoPlayer) {
               int var16 = ((IsoPlayer)var2).Moodles.getMoodleLevel(MoodleType.Endurance);
               if (var16 == 4) {
                  var15 = 50.0F;
               } else if (var16 == 3) {
                  var15 = 35.0F;
               } else if (var16 == 2) {
                  var15 = 24.0F;
               } else if (var16 == 1) {
                  var15 = 16.0F;
               }
            }

            if (var1.getKnockdownMod() <= 0.0F) {
               var1.setKnockdownMod(1.0F);
            }

            var15 /= var1.getKnockdownMod();
            if (var2 instanceof IsoPlayer && !var1.isAimedHandWeapon()) {
               var15 *= 2.0F - ((IsoPlayer)var2).useChargeDelta;
            }

            if (var15 < 1.0F) {
               var15 = 1.0F;
            }

            boolean var17 = Rand.Next((int)var15) == 0;
            if (this.Health <= 0.0F || (var1.isAlwaysKnockdown() || var17) && this instanceof IsoZombie) {
               this.DoDeathSilence(var1, var2);
            }

         }
      }
   }

   protected void DoDeathSilence(HandWeapon var1, IsoGameCharacter var2) {
      if (this.Health <= 0.0F) {
         if (this.bUseParts && var1 != null && var1.getType().equals("Shotgun")) {
            this.headSprite = null;
         }

         if (var1 != null) {
            int var3 = var1.getSplatNumber();
            if (var3 < 1) {
               var3 = 1;
            }

            if (Core.bLastStand) {
               var3 *= 3;
            }

            for(int var4 = 0; var4 < var3; ++var4) {
               this.splatBlood(3, 0.3F);
            }
         }

         this.splatBloodFloorBig(0.3F);
         if (var2 != null && var2.xp != null) {
            var2.xp.AddXP(var1, 3);
         }

         tempo.x = this.getHitDir().x;
         tempo.y = this.getHitDir().y;
      }

      if (this.Health <= 0.0F && this.getCurrentSquare() != null) {
         if (GameServer.bServer && this instanceof IsoZombie) {
            GameServer.sendDeadZombie((IsoZombie)this);
         }

         new IsoDeadBody(this);
      }

      this.stateMachine.Lock = true;
   }

   public boolean isEquipped(InventoryItem var1) {
      return this.isEquippedClothing(var1) || this.getPrimaryHandItem() == var1 || this.getSecondaryHandItem() == var1;
   }

   public boolean isEquippedClothing(InventoryItem var1) {
      return this.getClothingItem_Back() == var1 || this.getClothingItem_Feet() == var1 || this.getClothingItem_Hands() == var1 || this.getClothingItem_Head() == var1 || this.getClothingItem_Legs() == var1 || this.getClothingItem_Torso() == var1;
   }

   public void faceThisObject(IsoObject var1) {
      if (var1 != null) {
         Vector2 var10000;
         if (var1 instanceof BaseVehicle) {
            ((BaseVehicle)var1).getFacingPosition(this, tempo);
            var10000 = tempo;
            var10000.x -= this.getX();
            var10000 = tempo;
            var10000.y -= this.getY();
            this.DirectionFromVector(tempo);
            this.angle.set(tempo.x, tempo.y);
            this.angle.normalize();
         } else {
            var1.getFacingPosition(tempo);
            var10000 = tempo;
            var10000.x -= this.getX();
            var10000 = tempo;
            var10000.y -= this.getY();
            this.DirectionFromVector(tempo);
            this.getVectorFromDirection(this.angle);
         }
      }
   }

   public void faceThisObjectAlt(IsoObject var1) {
      if (var1 != null) {
         var1.getFacingPositionAlt(tempo);
         Vector2 var10000 = tempo;
         var10000.x -= this.getX();
         var10000 = tempo;
         var10000.y -= this.getY();
         this.DirectionFromVector(tempo);
         this.getVectorFromDirection(this.angle);
      }
   }

   public void setAnimated(boolean var1) {
      this.legsSprite.Animate = true;
   }

   public void playDeadSound() {
      if (GameServer.bServer && this instanceof IsoZombie) {
         GameServer.sendZombieSound(this.isCloseKilled() ? IsoZombie.ZombieSound.DeadCloseKilled : IsoZombie.ZombieSound.DeadNotCloseKilled, (IsoZombie)this);
      }

      if (this.isCloseKilled()) {
         this.getEmitter().playSound("HeadStab");
      } else {
         this.getEmitter().playSound("HeadSmash");
      }

      if (this instanceof IsoZombie) {
         if (this.isFemale()) {
            this.getEmitter().playSound("FemaleZombieDeath");
         } else {
            this.getEmitter().playSound("MaleZombieDeath");
         }
      }

   }

   public void saveChange(String var1, KahluaTable var2, ByteBuffer var3) {
      super.saveChange(var1, var2, var3);
      if ("addItem".equals(var1)) {
         if (var2 != null && var2.rawget("item") instanceof InventoryItem) {
            InventoryItem var4 = (InventoryItem)var2.rawget("item");

            try {
               var4.save(var3, false);
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }
      } else if ("addItemOfType".equals(var1)) {
         if (var2 != null && var2.rawget("type") instanceof String) {
            GameWindow.WriteStringUTF(var3, (String)var2.rawget("type"));
            if (var2.rawget("count") instanceof Double) {
               var3.putShort(((Double)var2.rawget("count")).shortValue());
            } else {
               var3.putShort((short)1);
            }
         }
      } else if ("AddRandomDamageFromZombie".equals(var1)) {
         if (var2 != null && var2.rawget("zombie") instanceof Double) {
            var3.putShort(((Double)var2.rawget("zombie")).shortValue());
         }
      } else if (!"AddZombieKill".equals(var1)) {
         if ("DamageFromWeapon".equals(var1)) {
            if (var2 != null && var2.rawget("weapon") instanceof String) {
               GameWindow.WriteStringUTF(var3, (String)var2.rawget("weapon"));
            }
         } else if ("removeItem".equals(var1)) {
            if (var2 != null && var2.rawget("item") instanceof Double) {
               var3.putInt(((Double)var2.rawget("item")).intValue());
            }
         } else if ("removeItemID".equals(var1)) {
            if (var2 != null && var2.rawget("id") instanceof Double) {
               var3.putLong(((Double)var2.rawget("id")).longValue());
            }

            if (var2 != null && var2.rawget("type") instanceof String) {
               GameWindow.WriteStringUTF(var3, (String)var2.rawget("type"));
            } else {
               GameWindow.WriteStringUTF(var3, (String)null);
            }
         } else if ("removeItemType".equals(var1)) {
            if (var2 != null && var2.rawget("type") instanceof String) {
               GameWindow.WriteStringUTF(var3, (String)var2.rawget("type"));
               if (var2.rawget("count") instanceof Double) {
                  var3.putShort(((Double)var2.rawget("count")).shortValue());
               } else {
                  var3.putShort((short)1);
               }
            }
         } else if ("removeOneOf".equals(var1)) {
            if (var2 != null && var2.rawget("type") instanceof String) {
               GameWindow.WriteStringUTF(var3, (String)var2.rawget("type"));
            }
         } else if ("reanimatedID".equals(var1)) {
            if (var2 != null && var2.rawget("ID") instanceof Double) {
               int var7 = ((Double)var2.rawget("ID")).intValue();
               var3.putInt(var7);
            }
         } else if ("Shove".equals(var1)) {
            if (var2 != null && var2.rawget("hitDirX") instanceof Double && var2.rawget("hitDirY") instanceof Double && var2.rawget("force") instanceof Double) {
               var3.putFloat(((Double)var2.rawget("hitDirX")).floatValue());
               var3.putFloat(((Double)var2.rawget("hitDirY")).floatValue());
               var3.putFloat(((Double)var2.rawget("force")).floatValue());
            }
         } else if ("addXp".equals(var1)) {
            if (var2 != null && var2.rawget("perk") instanceof Double && var2.rawget("xp") instanceof Double) {
               var3.putInt(((Double)var2.rawget("perk")).intValue());
               var3.putInt(((Double)var2.rawget("xp")).intValue());
               Object var8 = var2.rawget("noMultiplier");
               var3.put((byte)(Boolean.TRUE.equals(var8) ? 1 : 0));
            }
         } else if (!"wakeUp".equals(var1) && "mechanicActionDone".equals(var1) && var2 != null) {
            var3.put((byte)((Boolean)var2.rawget("success") ? 1 : 0));
            var3.putInt(((Double)var2.rawget("vehicleId")).intValue());
            GameWindow.WriteString(var3, (String)var2.rawget("partId"));
            var3.put((byte)((Boolean)var2.rawget("installing") ? 1 : 0));
            var3.putLong(((Double)var2.rawget("itemId")).longValue());
         }
      }

   }

   public void loadChange(String var1, ByteBuffer var2) {
      super.loadChange(var1, var2);
      String var3;
      if ("addItem".equals(var1)) {
         var3 = GameWindow.ReadStringUTF(var2);
         byte var4 = var2.get();
         InventoryItem var5 = InventoryItemFactory.CreateItem(var3);
         if (var5 != null) {
            try {
               var5.load(var2, 143, false);
            } catch (Exception var9) {
               var9.printStackTrace();
            }

            this.getInventory().AddItem(var5);
         } else {
            DebugLog.log("IsoGameCharacter.loadChange() unknown item type \"" + var3 + "\"");
         }
      } else {
         short var11;
         int var17;
         if ("addItemOfType".equals(var1)) {
            var3 = GameWindow.ReadStringUTF(var2);
            var11 = var2.getShort();

            for(var17 = 0; var17 < var11; ++var17) {
               this.getInventory().AddItem(var3);
            }
         } else if ("AddRandomDamageFromZombie".equals(var1)) {
            short var10 = var2.getShort();
            IsoZombie var13 = GameClient.getZombie(var10);
            if (var13 != null && !this.isDead()) {
               this.getBodyDamage().AddRandomDamageFromZombie(var13);
               this.getBodyDamage().Update();
               if (this.isDead()) {
                  if (this.isFemale()) {
                     var13.getEmitter().playSound("FemaleBeingEatenDeath");
                  } else {
                     var13.getEmitter().playSound("MaleBeingEatenDeath");
                  }
               }
            }
         } else if ("AddZombieKill".equals(var1)) {
            this.setZombieKills(this.getZombieKills() + 1);
         } else {
            InventoryItem var15;
            if ("DamageFromWeapon".equals(var1)) {
               var3 = GameWindow.ReadStringUTF(var2);
               var15 = InventoryItemFactory.CreateItem(var3);
               if (var15 instanceof HandWeapon) {
                  this.getBodyDamage().DamageFromWeapon((HandWeapon)var15);
               }
            } else if ("exitVehicle".equals(var1)) {
               BaseVehicle var12 = this.getVehicle();
               if (var12 != null) {
                  var12.exit(this);
                  this.setVehicle((BaseVehicle)null);
               }
            } else if ("removeItem".equals(var1)) {
               int var14 = var2.getInt();
               if (var14 >= 0 && var14 < this.getInventory().getItems().size()) {
                  var15 = (InventoryItem)this.getInventory().getItems().get(var14);
                  if (this.getPrimaryHandItem() == var15) {
                     this.setPrimaryHandItem((InventoryItem)null);
                  }

                  if (this.getSecondaryHandItem() == var15) {
                     this.setSecondaryHandItem((InventoryItem)null);
                  }

                  this.getInventory().Remove(var15);
               }
            } else {
               String var18;
               if ("removeItemID".equals(var1)) {
                  long var16 = var2.getLong();
                  var18 = GameWindow.ReadStringUTF(var2);
                  InventoryItem var6 = this.getInventory().getItemWithID(var16);
                  if (var6 != null && var6.getFullType().equals(var18)) {
                     if (var6 == this.getPrimaryHandItem()) {
                        this.setPrimaryHandItem((InventoryItem)null);
                     }

                     if (var6 == this.getSecondaryHandItem()) {
                        this.setSecondaryHandItem((InventoryItem)null);
                     }

                     this.getInventory().Remove(var6);
                  }
               } else if ("removeItemType".equals(var1)) {
                  var3 = GameWindow.ReadStringUTF(var2);
                  var11 = var2.getShort();

                  for(var17 = 0; var17 < var11; ++var17) {
                     this.getInventory().RemoveOneOf(var3);
                  }
               } else if ("removeOneOf".equals(var1)) {
                  var3 = GameWindow.ReadStringUTF(var2);
                  this.getInventory().RemoveOneOf(var3);
               } else if ("reanimatedID".equals(var1)) {
                  this.ReanimatedCorpseID = var2.getInt();
               } else if ("Shove".equals(var1)) {
                  if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
                     return;
                  }

                  this.getHitDir().x = var2.getFloat();
                  this.getHitDir().y = var2.getFloat();
                  this.setHitForce(Math.min(0.5F, var2.getFloat()));
                  if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
                     StaggerBackState.instance().enter(this);
                  }

                  this.stateMachine.changeState(StaggerBackState.instance());
               } else if ("StopBurning".equals(var1)) {
                  this.StopBurning();
               } else {
                  int var20;
                  if ("addXp".equals(var1)) {
                     PerkFactory.Perks var19 = PerkFactory.Perks.fromIndex(var2.getInt());
                     var20 = var2.getInt();
                     boolean var23 = var2.get() == 1;
                     if (var23) {
                        this.getXp().AddXPNoMultiplier(var19, (float)var20);
                     } else {
                        this.getXp().AddXP(var19, (float)var20);
                     }
                  } else if ("wakeUp".equals(var1)) {
                     if (this.isAsleep()) {
                        this.Asleep = false;
                        this.ForceWakeUpTime = -1.0F;
                        TutorialManager.instance.StealControl = false;
                        if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
                           UIManager.setFadeBeforeUI(((IsoPlayer)this).getPlayerNum(), true);
                           UIManager.FadeIn((double)((IsoPlayer)this).getPlayerNum(), 2.0D);
                           ScriptManager.instance.Trigger("OnPlayerWake");
                           GameClient.instance.sendPlayer((IsoPlayer)this);
                        }
                     }
                  } else if ("mechanicActionDone".equals(var1)) {
                     boolean var21 = var2.get() == 1;
                     var20 = var2.getInt();
                     var18 = GameWindow.ReadString(var2);
                     boolean var22 = var2.get() == 1;
                     long var7 = var2.getLong();
                     LuaEventManager.triggerEvent("OnMechanicActionDone", this, var21, var20, var18, var7, var22);
                  } else if ("vehicleNoKey".equals(var1)) {
                     this.SayDebug(" [img=media/ui/CarKey_none.png]");
                  }
               }
            }
         }
      }

   }

   public void setRemoteMoveX(float var1) {
      this.remoteMoveX = var1;
   }

   public void setRemoteMoveY(float var1) {
      this.remoteMoveY = var1;
   }

   public void setRemoteState(byte var1) {
      this.NetRemoteState = var1;
   }

   public int getAlreadyReadPages(String var1) {
      for(int var2 = 0; var2 < this.ReadBooks.size(); ++var2) {
         IsoGameCharacter.ReadBook var3 = (IsoGameCharacter.ReadBook)this.ReadBooks.get(var2);
         if (var3.fullType.equals(var1)) {
            return var3.alreadyReadPages;
         }
      }

      return 0;
   }

   public void setAlreadyReadPages(String var1, int var2) {
      for(int var3 = 0; var3 < this.ReadBooks.size(); ++var3) {
         IsoGameCharacter.ReadBook var4 = (IsoGameCharacter.ReadBook)this.ReadBooks.get(var3);
         if (var4.fullType.equals(var1)) {
            var4.alreadyReadPages = var2;
            return;
         }
      }

      IsoGameCharacter.ReadBook var5 = new IsoGameCharacter.ReadBook();
      var5.fullType = var1;
      var5.alreadyReadPages = var2;
      this.ReadBooks.add(var5);
   }

   public void updateLightInfo() {
      if (GameServer.bServer || LightingThread.instance == null || !LightingThread.instance.newLightingMethod) {
         if (!GameServer.bServer || !(this instanceof IsoZombie)) {
            if (GameServer.bServer || this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
               synchronized(this.lightInfo) {
                  this.lightInfo.square = this.movingSq;
                  if (this.lightInfo.square == null) {
                     this.lightInfo.square = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
                  }

                  if (this.ReanimatedCorpse != null) {
                     this.lightInfo.square = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
                  }

                  this.lightInfo.x = this.getX();
                  this.lightInfo.y = this.getY();
                  this.lightInfo.z = this.getZ();
                  this.lightInfo.angleX = this.getAngle().getX();
                  this.lightInfo.angleY = this.getAngle().getY();
                  this.lightInfo.torches.clear();
                  this.lightInfo.night = GameTime.getInstance().getNight();
                  if (!GameServer.bServer) {
                     if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                        this.lightInfo.rmod = GameTime.getInstance().Lerp(1.0F, 0.7F, GameTime.getInstance().getNight());
                        this.lightInfo.gmod = GameTime.getInstance().Lerp(1.0F, 0.7F, GameTime.getInstance().getNight());
                        this.lightInfo.bmod = GameTime.getInstance().Lerp(1.0F, 0.7F, GameTime.getInstance().getNight());
                     } else {
                        float var2 = GameTime.getInstance().getNight();
                        if (var2 > 0.8F && this.HasTrait("NightVision")) {
                           var2 = 0.8F;
                        }

                        this.lightInfo.rmod = GameTime.getInstance().Lerp(1.0F, 0.1F, var2);
                        this.lightInfo.gmod = GameTime.getInstance().Lerp(1.0F, 0.2F, var2);
                        this.lightInfo.bmod = GameTime.getInstance().Lerp(1.0F, 0.45F, var2);
                     }

                     int var8;
                     if (GameClient.bClient) {
                        ArrayList var7 = GameClient.instance.getPlayers();

                        for(int var3 = 0; var3 < var7.size(); ++var3) {
                           IsoPlayer var4 = (IsoPlayer)var7.get(var3);
                           if (var4.getTorchStrength() > 0.0F && (var4 == this || IsoUtils.DistanceManhatten(this.getX(), this.getY(), var4.getX(), var4.getY()) < 50.0F)) {
                              this.lightInfo.torches.add(IsoGameCharacter.TorchInfo.alloc().set(var4));
                           }
                        }
                     } else {
                        for(var8 = 0; var8 < IsoPlayer.numPlayers; ++var8) {
                           IsoPlayer var9 = IsoPlayer.players[var8];
                           if (var9 != null && !var9.isDead() && var9.getTorchStrength() > 0.0F && (var9 == this || IsoUtils.DistanceManhatten(this.getX(), this.getY(), var9.getX(), var9.getY()) < 50.0F)) {
                              this.lightInfo.torches.add(IsoGameCharacter.TorchInfo.alloc().set(var9));
                           }
                        }
                     }

                     for(var8 = 0; var8 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++var8) {
                        BaseVehicle var10 = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(var8);
                        if (var10.hasHeadlights() && var10.getHeadlightsOn() && IsoUtils.DistanceManhatten(this.getX(), this.getY(), var10.getX(), var10.getY()) < 50.0F) {
                           for(int var11 = 0; var11 < var10.getLightCount(); ++var11) {
                              this.lightInfo.torches.add(IsoGameCharacter.TorchInfo.alloc().set(var10.getLightByIndex(var11)));
                           }
                        }
                     }

                  }
               }
            }
         }
      }
   }

   public IsoGameCharacter.LightInfo initLightInfo2() {
      synchronized(this.lightInfo) {
         for(int var2 = 0; var2 < this.lightInfo2.torches.size(); ++var2) {
            IsoGameCharacter.TorchInfo.release((IsoGameCharacter.TorchInfo)this.lightInfo2.torches.get(var2));
         }

         this.lightInfo2.initFrom(this.lightInfo);
         return this.lightInfo2;
      }
   }

   public IsoGameCharacter.LightInfo getLightInfo2() {
      return this.lightInfo2;
   }

   public void postupdate() {
      super.postupdate();
      if (this.hasActiveModel() && this.def != null && this.legsSprite.CurrentAnim != null && !this.legsSprite.CurrentAnim.Frames.isEmpty()) {
         try {
            this.legsSprite.modelSlot.Play(this.legsSprite.CurrentAnim.name, this.def.Looped, this.def.Finished, this);
            this.legsSprite.modelSlot.Update();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      this.updateLightInfo();
   }

   public boolean isSafety() {
      return this.safety;
   }

   public void setSafety(boolean var1) {
      this.safety = var1;
   }

   public float getSafetyCooldown() {
      return this.safetyCooldown;
   }

   public void setSafetyCooldown(float var1) {
      this.safetyCooldown = Math.max(var1, 0.0F);
   }

   public float getRecoilDelay() {
      return this.RecoilDelay;
   }

   public void setRecoilDelay(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.RecoilDelay = var1;
   }

   public float getBeenMovingFor() {
      return this.BeenMovingFor;
   }

   public void setBeenMovingFor(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      if (var1 > 70.0F) {
         var1 = 70.0F;
      }

      this.BeenMovingFor = var1;
   }

   public boolean isForceShove() {
      return this.forceShove;
   }

   public void setForceShove(boolean var1) {
      this.forceShove = var1;
   }

   public String getClickSound() {
      return this.clickSound;
   }

   public void setClickSound(String var1) {
      this.clickSound = var1;
   }

   public int getMeleeCombatMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.BluntGuard);
      if (this.haveBladeWeapon()) {
         var1 = this.getPerkLevel(PerkFactory.Perks.BladeGuard);
      }

      if (var1 == 1) {
         return -2;
      } else if (var1 == 2) {
         return 0;
      } else if (var1 == 3) {
         return 1;
      } else if (var1 == 4) {
         return 2;
      } else if (var1 == 5) {
         return 3;
      } else if (var1 == 6) {
         return 4;
      } else if (var1 == 7) {
         return 5;
      } else if (var1 == 8) {
         return 5;
      } else if (var1 == 9) {
         return 6;
      } else {
         return var1 == 10 ? 7 : -5;
      }
   }

   public int getMaintenanceMod() {
      int var1 = this.getPerkLevel(PerkFactory.Perks.BluntMaintenance);
      if (this.haveBladeWeapon()) {
         var1 = this.getPerkLevel(PerkFactory.Perks.BladeMaintenance);
      }

      return var1 / 2;
   }

   public boolean haveBladeWeapon() {
      if (this.getPrimaryHandItem() == null) {
         return false;
      } else {
         Item var1 = this.getPrimaryHandItem().getScriptItem();
         return var1.getCategories().contains("Blade") || var1.getCategories().contains("Axe");
      }
   }

   public void setVehicle(BaseVehicle var1) {
      this.vehicle = var1;
   }

   public BaseVehicle getVehicle() {
      return this.vehicle;
   }

   public boolean isUnderVehicle() {
      int var1 = ((int)this.x - 4) / 10;
      int var2 = ((int)this.y - 4) / 10;
      int var3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F));
      int var4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F));

      for(int var5 = var2; var5 < var4; ++var5) {
         for(int var6 = var1; var6 < var3; ++var6) {
            IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var6, var5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var6 * 10, var5 * 10, 0);
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.vehicles.size(); ++var8) {
                  BaseVehicle var9 = (BaseVehicle)var7.vehicles.get(var8);
                  Vector2 var10 = var9.testCollisionWithCharacter(this, 0.3F);
                  if (var10 != null && var10.x != -1.0F) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public float getTemperature() {
      return this.getBodyDamage().getTemperature();
   }

   public void setTemperature(float var1) {
      this.getBodyDamage().setTemperature(var1);
   }

   public float getReduceInfectionPower() {
      return this.reduceInfectionPower;
   }

   public void setReduceInfectionPower(float var1) {
      this.reduceInfectionPower = var1;
   }

   public float getInventoryWeight() {
      if (this.getInventory() == null) {
         return 0.0F;
      } else {
         float var1 = 0.0F;
         ArrayList var2 = this.getInventory().getItems();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            InventoryItem var4 = (InventoryItem)var2.get(var3);
            if (this.isEquipped(var4)) {
               var1 += var4.getEquippedWeight();
            } else {
               var1 += var4.getUnequippedWeight();
            }
         }

         return var1;
      }
   }

   public void dropHandItems() {
      IsoGridSquare var1 = this.getCurrentSquare();
      if (var1 != null) {
         InventoryItem var2 = this.getPrimaryHandItem();
         InventoryItem var3 = this.getSecondaryHandItem();
         if (var2 != null || var3 != null) {
            if (var3 == var2) {
               var3 = null;
            }

            if (var2 != null) {
               this.setPrimaryHandItem((InventoryItem)null);
               this.getInventory().DoRemoveItem(var2);
               var1.AddWorldInventoryItem(var2, 0.0F, 0.0F, 0.0F);
            }

            if (var3 != null) {
               this.setSecondaryHandItem((InventoryItem)null);
               this.getInventory().DoRemoveItem(var3);
               var1.AddWorldInventoryItem(var3, 0.0F, 0.0F, 0.0F);
            }

         }
      }
   }

   public boolean shouldBecomeZombieAfterDeath() {
      switch(SandboxOptions.instance.Lore.Transmission.getValue()) {
      case 1:
         boolean var1;
         if (!this.getBodyDamage().IsFakeInfected()) {
            float var10000 = this.getBodyDamage().getInfectionLevel();
            BodyDamage var10001 = this.BodyDamage;
            if (var10000 >= 0.001F) {
               var1 = true;
               return var1;
            }
         }

         var1 = false;
         return var1;
      case 2:
         return true;
      case 3:
         return false;
      default:
         return false;
      }
   }

   public void applyTraits(ArrayList var1) {
      if (var1 != null) {
         HashMap var2 = new HashMap();
         var2.put(PerkFactory.Perks.Fitness, 5);
         var2.put(PerkFactory.Perks.Strength, 5);

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            String var4 = (String)var1.get(var3);
            if (var4 != null && !var4.isEmpty()) {
               TraitFactory.Trait var5 = TraitFactory.getTrait(var4);
               if (var5 != null) {
                  if (!this.HasTrait(var4)) {
                     this.getTraits().add(var4);
                  }

                  HashMap var6 = var5.getXPBoostMap();
                  PerkFactory.Perks var9;
                  int var10;
                  if (var6 != null) {
                     for(Iterator var7 = var6.entrySet().iterator(); var7.hasNext(); var2.put(var9, var10)) {
                        Entry var8 = (Entry)var7.next();
                        var9 = (PerkFactory.Perks)var8.getKey();
                        var10 = (Integer)var8.getValue();
                        if (var2.containsKey(var9)) {
                           var10 += (Integer)var2.get(var9);
                        }
                     }
                  }
               }
            }
         }

         if (this instanceof IsoPlayer) {
            ((IsoPlayer)this).getNutrition().applyWeightFromTraits();
         }

         HashMap var11 = this.getDescriptor().getXPBoostMap();

         Iterator var12;
         Entry var13;
         PerkFactory.Perks var14;
         int var15;
         for(var12 = var11.entrySet().iterator(); var12.hasNext(); var2.put(var14, var15)) {
            var13 = (Entry)var12.next();
            var14 = (PerkFactory.Perks)var13.getKey();
            var15 = (Integer)var13.getValue();
            if (var2.containsKey(var14)) {
               var15 += (Integer)var2.get(var14);
            }
         }

         var12 = var2.entrySet().iterator();

         while(var12.hasNext()) {
            var13 = (Entry)var12.next();
            var14 = (PerkFactory.Perks)var13.getKey();
            var15 = (Integer)var13.getValue();
            var15 = Math.max(0, var15);
            var15 = Math.min(10, var15);
            this.getDescriptor().getXPBoostMap().put(var14, Math.min(3, var15));

            for(int var16 = 0; var16 < var15; ++var16) {
               this.LevelPerk(var14);
            }

            this.getXp().setXPToLevel(var14, this.getPerkLevel(var14));
         }

      }
   }

   public void createKeyRing() {
      InventoryItem var1 = this.getInventory().AddItem("Base.KeyRing");
      if (var1 != null && var1 instanceof InventoryContainer) {
         InventoryContainer var2 = (InventoryContainer)var1;
         var2.setName(Translator.getText("IGUI_KeyRingName", this.getDescriptor().getForename(), this.getDescriptor().getSurname()));
         if (Rand.Next(100) < 40) {
            RoomDef var3 = IsoWorld.instance.MetaGrid.getRoomAt((int)this.getX(), (int)this.getY(), (int)this.getZ());
            if (var3 != null && var3.getBuilding() != null) {
               String var4 = "Base.Key" + (Rand.Next(5) + 1);
               InventoryItem var5 = var2.getInventory().AddItem(var4);
               var5.setKeyId(var3.getBuilding().getKeyId());
            }
         }

      }
   }

   public void autoDrink() {
      if (!GameServer.bServer) {
         if (!GameClient.bClient || !(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
            if (!LuaHookManager.TriggerHook("AutoDrink", this)) {
               if (!(this.stats.thirst <= 0.1F)) {
                  InventoryItem var1 = null;
                  ArrayList var2 = this.getInventory().getItems();

                  for(int var3 = 0; var3 < var2.size(); ++var3) {
                     InventoryItem var4 = (InventoryItem)var2.get(var3);
                     if (var4.isWaterSource() && !var4.isBeingFilled() && !var4.isTaintedWater()) {
                        if (!(var4 instanceof Drainable)) {
                           var1 = var4;
                           break;
                        }

                        if (((Drainable)var4).getUsedDelta() > 0.0F) {
                           var1 = var4;
                           break;
                        }
                     }
                  }

                  if (var1 != null) {
                     Stats var10000 = this.stats;
                     var10000.thirst -= 0.1F;
                     if (GameClient.bClient) {
                        GameClient.instance.drink((IsoPlayer)this, 0.1F);
                     }

                     var1.Use();
                  }

               }
            }
         }
      }
   }

   public List getKnownRecipes() {
      return this.knownRecipes;
   }

   public boolean isRecipeKnown(Recipe var1) {
      return !var1.needToBeLearn() || this.getKnownRecipes().contains(var1.getOriginalname());
   }

   private boolean isMoving() {
      if (this instanceof IsoPlayer && this.getPath2() != null) {
         return true;
      } else {
         return this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == LungeState.instance();
      }
   }

   private boolean isFacingNorthWesterly() {
      return this.dir == IsoDirections.W || this.dir == IsoDirections.NW || this.dir == IsoDirections.N || this.dir == IsoDirections.NE;
   }

   private boolean isZombieAttacking() {
      if (!(this instanceof IsoZombie)) {
         return false;
      } else {
         return this.getCurrentState() == AttackState.instance() || this.getCurrentState() == AttackVehicleState.instance();
      }
   }

   private boolean isZombieAttacking(IsoMovingObject var1) {
      if (GameClient.bClient) {
         return this instanceof IsoZombie && this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieBite".equals(this.legsSprite.CurrentAnim.name);
      } else {
         return this instanceof IsoZombie && var1 == ((IsoZombie)this).target && this.getCurrentState() == AttackState.instance();
      }
   }

   private boolean isZombieThumping() {
      if (this instanceof IsoZombie) {
         return this.getCurrentState() == ThumpState.instance();
      } else {
         return false;
      }
   }

   public int compareMovePriority(IsoGameCharacter var1) {
      if (var1 == null) {
         return 1;
      } else if (this.isZombieThumping() && !var1.isZombieThumping()) {
         return 1;
      } else if (!this.isZombieThumping() && var1.isZombieThumping()) {
         return -1;
      } else if (var1 instanceof IsoPlayer) {
         return GameClient.bClient && this.isZombieAttacking(var1) ? -1 : 0;
      } else if (this.isZombieAttacking() && !var1.isZombieAttacking()) {
         return 1;
      } else if (!this.isZombieAttacking() && var1.isZombieAttacking()) {
         return -1;
      } else if (this.isMoving() && !var1.isMoving()) {
         return 1;
      } else if (!this.isMoving() && var1.isMoving()) {
         return -1;
      } else if (this.isFacingNorthWesterly() && !var1.isFacingNorthWesterly()) {
         return 1;
      } else {
         return !this.isFacingNorthWesterly() && var1.isFacingNorthWesterly() ? -1 : 0;
      }
   }

   public long playSound(String var1) {
      return this.getEmitter().playSound(var1);
   }

   /** @deprecated */
   @Deprecated
   public long playSound(String var1, boolean var2) {
      return this.getEmitter().playSound(var1, var2);
   }

   public boolean isKnownPoison(InventoryItem var1) {
      if (var1 instanceof Food) {
         Food var2 = (Food)var1;
         if (var2.getPoisonPower() <= 0) {
            return false;
         }

         if (var2.getHerbalistType() != null && !var2.getHerbalistType().isEmpty()) {
            return this.getKnownRecipes().contains("Herbalist");
         }

         if (var2.getPoisonDetectionLevel() >= 0 && this.getPerkLevel(PerkFactory.Perks.Cooking) >= 10 - var2.getPoisonDetectionLevel()) {
            return true;
         }

         if (var2.getPoisonLevelForRecipe() != null) {
            return true;
         }
      }

      return false;
   }

   public int getLastHourSleeped() {
      return this.lastHourSleeped;
   }

   public void setLastHourSleeped(int var1) {
      this.lastHourSleeped = var1;
   }

   public void setTimeOfSleep(float var1) {
      this.timeOfSleep = var1;
   }

   public void setDelayToSleep(float var1) {
      this.delayToActuallySleep = var1;
   }

   public String getBedType() {
      return this.bedType;
   }

   public void setBedType(String var1) {
      this.bedType = var1;
   }

   public void enterVehicle(BaseVehicle var1, int var2, Vector3f var3) {
      if (this.vehicle != null) {
         this.vehicle.exit(this);
      }

      if (var1 != null) {
         var1.enter(var2, this, var3);
      }

   }

   public void Hit(BaseVehicle var1, float var2, float var3, Vector2 var4) {
      this.AttackedBy = var1.getDriver();
      this.setHitDir(var4);
      this.setHitForce(var2 * 0.1F);
      this.Move(var4.setLength(var2));
      if (var3 > 0.0F) {
         this.getStateMachine().changeState(StaggerBackState.instance());
      } else if (var2 < 5.0F) {
         this.getStateMachine().changeState(StaggerBackState.instance());
      } else if (var2 < 10.0F) {
         this.getStateMachine().changeState(StaggerBackDieState.instance());
      } else {
         this.Kill(var1.getCharacter(0));
      }

   }

   public PolygonalMap2.Path getPath2() {
      return this.path2;
   }

   public void setPath2(PolygonalMap2.Path var1) {
      this.path2 = var1;
   }

   public PathFindBehavior2 getPathFindBehavior2() {
      return this.pfb2;
   }

   public IsoObject getBed() {
      return this.isAsleep() ? this.bed : null;
   }

   public boolean avoidDamage() {
      return this.avoidDamage;
   }

   public void setAvoidDamage(boolean var1) {
      this.avoidDamage = var1;
   }

   public void setBed(IsoObject var1) {
      this.bed = var1;
   }

   public boolean isReading() {
      return this.isReading;
   }

   public void setReading(boolean var1) {
      this.isReading = var1;
   }

   public float getTimeSinceLastSmoke() {
      return this.timeSinceLastSmoke;
   }

   public void setTimeSinceLastSmoke(float var1) {
      this.timeSinceLastSmoke = var1;
   }

   public boolean isInvisible() {
      return this.invisible;
   }

   public void removeEquippedClothing(InventoryItem var1) {
      if (this.getClothingItem_Back() == var1) {
         this.setClothingItem_Back((InventoryItem)null);
      }

      if (this.getClothingItem_Feet() == var1) {
         this.setClothingItem_Feet((InventoryItem)null);
      }

      if (this.getClothingItem_Hands() == var1) {
         this.setClothingItem_Hands((InventoryItem)null);
      }

      if (this.getClothingItem_Head() == var1) {
         this.setClothingItem_Head((InventoryItem)null);
      }

      if (this.getClothingItem_Legs() == var1) {
         this.setClothingItem_Legs((InventoryItem)null);
      }

      if (this.getClothingItem_Torso() == var1) {
         this.setClothingItem_Torso((InventoryItem)null);
      }

   }

   public boolean isDriving() {
      return this.getVehicle() != null && this.getVehicle().getDriver() == this && Math.abs(this.getVehicle().getCurrentSpeedKmHour()) > 1.0F;
   }

   public boolean isInARoom() {
      return this.square != null ? this.square.isInARoom() : false;
   }

   public boolean isGodMod() {
      return this.godMod;
   }

   public void setInvisible(boolean var1) {
      this.invisible = var1;
   }

   public void setGodMod(boolean var1) {
      this.godMod = var1;
   }

   public boolean isUnlimitedCarry() {
      return this.unlimitedCarry;
   }

   public void setUnlimitedCarry(boolean var1) {
      this.unlimitedCarry = var1;
   }

   public boolean isBuildCheat() {
      return this.buildCheat;
   }

   public void setBuildCheat(boolean var1) {
      this.buildCheat = var1;
   }

   public boolean isHealthCheat() {
      return this.healthCheat;
   }

   public void setHealthCheat(boolean var1) {
      this.healthCheat = var1;
   }

   public boolean isMechanicsCheat() {
      return this.mechanicsCheat;
   }

   public void setMechanicsCheat(boolean var1) {
      this.mechanicsCheat = var1;
   }

   public boolean isShowAdminTag() {
      return this.showAdminTag;
   }

   public void setShowAdminTag(boolean var1) {
      this.showAdminTag = var1;
   }

   public static class TorchInfo {
      public int id;
      public float x;
      public float y;
      public float z;
      public float angleX;
      public float angleY;
      public float dist;
      public float strength;
      public boolean bCone;
      public float dot;
      public int focusing;
      private static Stack TorchInfoPool = new Stack();
      private static Transform tempTransform = new Transform();
      private static Vector3f tempVector3f = new Vector3f();

      public IsoGameCharacter.TorchInfo set(IsoPlayer var1) {
         this.x = var1.getX();
         this.y = var1.getY();
         this.z = var1.getZ();
         this.angleX = var1.getAngle().x;
         this.angleY = var1.getAngle().y;
         this.dist = var1.getLightDistance();
         this.strength = var1.getTorchStrength();
         this.bCone = var1.isTorchCone();
         this.dot = 0.96F;
         this.focusing = 0;
         return this;
      }

      public static IsoGameCharacter.TorchInfo alloc() {
         return TorchInfoPool.isEmpty() ? new IsoGameCharacter.TorchInfo() : (IsoGameCharacter.TorchInfo)TorchInfoPool.pop();
      }

      public static void release(IsoGameCharacter.TorchInfo var0) {
         TorchInfoPool.push(var0);
      }

      public IsoGameCharacter.TorchInfo set(VehiclePart var1) {
         BaseVehicle var2 = var1.getVehicle();
         VehicleLight var3 = var1.getLight();
         VehicleScript var4 = var2.getScript();
         Vector3f var5 = tempVector3f;
         var5.set(var3.offset.x * var4.getExtents().x / 2.0F / var4.getModelScale(), 0.0F, var3.offset.y * var4.getExtents().z / 2.0F / var4.getModelScale());
         var2.getWorldPos(var5, var5);
         this.x = var5.x;
         this.y = var5.y;
         this.z = var5.z;
         var5 = var2.getForwardVector(var5);
         this.angleX = var5.x;
         this.angleY = var5.z;
         this.dist = var3.dist;
         this.strength = var3.intensity;
         this.bCone = true;
         this.dot = var3.dot;
         this.focusing = var3.focusing;
         return this;
      }
   }

   public static class LightInfo {
      public IsoGridSquare square;
      public float x;
      public float y;
      public float z;
      public float angleX;
      public float angleY;
      public ArrayList torches = new ArrayList();
      public long time;
      public float night;
      public float rmod;
      public float gmod;
      public float bmod;

      public void initFrom(IsoGameCharacter.LightInfo var1) {
         this.square = var1.square;
         this.x = var1.x;
         this.y = var1.y;
         this.z = var1.z;
         this.angleX = var1.angleX;
         this.angleY = var1.angleY;
         this.torches.clear();
         this.torches.addAll(var1.torches);
         this.time = (long)((double)System.nanoTime() / 1000000.0D);
         this.night = var1.night;
         this.rmod = var1.rmod;
         this.gmod = var1.gmod;
         this.bmod = var1.bmod;
      }
   }

   private static class ReadBook {
      String fullType;
      int alreadyReadPages;

      private ReadBook() {
      }

      // $FF: synthetic method
      ReadBook(Object var1) {
         this();
      }
   }

   public class Wound {
      public boolean bandaged = false;
      public float bleeding = 0.0F;
      public float infectAmount = 0.0F;
      public boolean infectedNormal = false;
      public boolean infectedZombie = false;
      public IsoGameCharacter.BodyLocation loc;
      public boolean tourniquet = false;
   }

   private static class HitInfoComparator implements Comparator {
      public IsoMovingObject testPlayer;
      public float maxRange;
      public boolean alternateScoringMethod = false;

      public HitInfoComparator(IsoMovingObject var1, float var2) {
         this.testPlayer = var1;
         this.maxRange = var2;
      }

      public int compare(IsoGameCharacter.HitInfo var1, IsoGameCharacter.HitInfo var2) {
         float var3 = var1.dist;
         float var4 = var2.dist;
         if (this.alternateScoringMethod) {
            float var5 = (this.maxRange - var3) / this.maxRange * 20.0F + var1.dot * 20.0F;
            float var6 = (this.maxRange - var4) / this.maxRange * 20.0F + var2.dot * 20.0F;
            if (var5 > var6) {
               return -1;
            } else {
               return var6 < var5 ? 1 : 0;
            }
         } else if (var3 > var4) {
            return 1;
         } else {
            return var4 > var3 ? -1 : 0;
         }
      }
   }

   private class HitInfo {
      public IsoMovingObject object;
      public float dot;
      public float dist;
      public int chance = 0;

      public HitInfo(IsoMovingObject var2, float var3, float var4) {
         this.object = var2;
         this.dot = var3;
         this.dist = var4;
      }
   }

   public static enum BodyLocation {
      Head,
      Leg,
      Arm,
      Chest,
      Stomach,
      Foot,
      Hand;
   }

   public static class Location {
      public int x;
      public int y;
      public int z;

      public Location(int var1, int var2, int var3) {
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof IsoGameCharacter.Location)) {
            return false;
         } else {
            IsoGameCharacter.Location var2 = (IsoGameCharacter.Location)var1;
            return this.x == var2.x && this.y == var2.y && this.z == var2.z;
         }
      }
   }

   public class XP {
      public int level = 0;
      public int lastlevel = 0;
      public float TotalXP = 0.0F;
      public HashMap XPMap = new HashMap();
      public HashMap XPMapMultiplier = new HashMap();
      IsoGameCharacter chr = null;

      public void addXpMultiplier(PerkFactory.Perks var1, float var2, int var3, int var4) {
         IsoGameCharacter.XPMultiplier var5 = (IsoGameCharacter.XPMultiplier)this.XPMapMultiplier.get(var1);
         if (var5 == null) {
            var5 = new IsoGameCharacter.XPMultiplier();
         }

         var5.multiplier = var2;
         var5.minLevel = var3;
         var5.maxLevel = var4;
         this.XPMapMultiplier.put(var1, var5);
      }

      public HashMap getMultiplierMap() {
         return this.XPMapMultiplier;
      }

      public float getMultiplier(PerkFactory.Perks var1) {
         IsoGameCharacter.XPMultiplier var2 = (IsoGameCharacter.XPMultiplier)this.XPMapMultiplier.get(var1);
         return var2 == null ? 0.0F : var2.multiplier;
      }

      public int getPerkBoost(PerkFactory.Perks var1) {
         return IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(var1) != null ? (Integer)IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(var1) : 0;
      }

      public int getLevel() {
         return this.level;
      }

      public void setLevel(int var1) {
         this.level = var1;
      }

      public float getTotalXp() {
         return this.TotalXP;
      }

      public XP(IsoGameCharacter var2) {
         this.chr = var2;
      }

      public void AddXP(PerkFactory.Perks var1, float var2) {
         this.AddXP(var1, var2, true);
      }

      public void AddXPNoMultiplier(PerkFactory.Perks var1, float var2) {
         IsoGameCharacter.XPMultiplier var3 = (IsoGameCharacter.XPMultiplier)this.getMultiplierMap().remove(var1);

         try {
            this.AddXP(var1, var2);
         } finally {
            if (var3 != null) {
               this.getMultiplierMap().put(var1, var3);
            }

         }

      }

      public void AddXP(PerkFactory.Perks var1, float var2, boolean var3) {
         this.AddXP(var1, var2, var3, true);
      }

      public void AddXP(PerkFactory.Perks var1, float var2, boolean var3, boolean var4) {
         this.AddXP(var1, var2, var3, var4, true, false);
      }

      public void AddXP(PerkFactory.Perks var1, float var2, boolean var3, boolean var4, boolean var5, boolean var6) {
         if (!var6 && GameClient.bClient && this.chr instanceof IsoPlayer) {
            GameClient.instance.sendAddXpFromPlayerStatsUI((IsoPlayer)this.chr, var1, (int)var2, var4, false);
         }

         PerkFactory.Perk var7 = null;

         for(int var8 = 0; var8 < PerkFactory.PerkList.size(); ++var8) {
            PerkFactory.Perk var9 = (PerkFactory.Perk)PerkFactory.PerkList.get(var8);
            if (var9.getType() == var1) {
               var7 = var9;
               break;
            }
         }

         if (var7.getType() != PerkFactory.Perks.Fitness || !(this.chr instanceof IsoPlayer) || ((IsoPlayer)this.chr).getNutrition().canAddFitnessXp()) {
            if (var7.getType() == PerkFactory.Perks.Strength && this.chr instanceof IsoPlayer) {
               if (((IsoPlayer)this.chr).getNutrition().getProteins() > 50.0F && ((IsoPlayer)this.chr).getNutrition().getProteins() < 300.0F) {
                  var2 = (float)((double)var2 * 1.5D);
               }

               if (((IsoPlayer)this.chr).getNutrition().getProteins() < -300.0F) {
                  var2 = (float)((double)var2 * 0.7D);
               }
            }

            float var16 = this.getXP(var1);
            float var17 = var7.getTotalXpForLevel(10);
            if (!(var2 >= 0.0F) || !(var16 >= var17)) {
               float var10 = 1.0F;
               float var19;
               if (var5) {
                  boolean var11 = false;
                  Iterator var12 = IsoGameCharacter.this.getDescriptor().getXPBoostMap().entrySet().iterator();

                  label183:
                  while(true) {
                     while(true) {
                        Entry var13;
                        do {
                           if (!var12.hasNext()) {
                              if (!var11 && !this.isSkillExcludedFromSpeedReduction(var7.getType())) {
                                 var10 = 0.25F;
                              }

                              if (IsoGameCharacter.this.HasTrait("FastLearner") && !this.isSkillExcludedFromSpeedIncrease(var7.getType())) {
                                 var10 *= 1.3F;
                              }

                              if (IsoGameCharacter.this.HasTrait("SlowLearner") && !this.isSkillExcludedFromSpeedReduction(var7.getType())) {
                                 var10 *= 0.7F;
                              }

                              if (IsoGameCharacter.this.HasTrait("Pacifist")) {
                                 if (var7.getType() != PerkFactory.Perks.Axe && var7.getType() != PerkFactory.Perks.BluntGuard && var7.getType() != PerkFactory.Perks.BluntMaintenance && var7.getType() != PerkFactory.Perks.BladeGuard && var7.getType() != PerkFactory.Perks.BladeMaintenance) {
                                    if (var7.getType() == PerkFactory.Perks.Blunt) {
                                       var10 *= 0.75F;
                                    } else if (var7.getType() == PerkFactory.Perks.Aiming) {
                                       var10 *= 0.75F;
                                    }
                                 } else {
                                    var10 *= 0.75F;
                                 }
                              }

                              var2 *= var10;
                              var19 = this.getMultiplier(var1);
                              if (var19 > 1.0F) {
                                 var2 *= var19;
                              }

                              if (!var7.isPassiv()) {
                                 var2 = (float)((double)var2 * SandboxOptions.instance.XpMultiplier.getValue());
                              }
                              break label183;
                           }

                           var13 = (Entry)var12.next();
                        } while(var13.getKey() != var7.getType());

                        var11 = true;
                        if ((Integer)var13.getValue() == 0 && !this.isSkillExcludedFromSpeedReduction((PerkFactory.Perks)var13.getKey())) {
                           var10 *= 0.25F;
                        } else if ((Integer)var13.getValue() == 1 && var13.getKey() == PerkFactory.Perks.Sprinting) {
                           var10 = (float)((double)var10 * 1.25D);
                        } else if ((Integer)var13.getValue() == 1) {
                           var10 = (float)((double)var10 * 1.0D);
                        } else if ((Integer)var13.getValue() == 2 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perks)var13.getKey())) {
                           var10 = (float)((double)var10 * 1.33D);
                        } else if ((Integer)var13.getValue() >= 3 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perks)var13.getKey())) {
                           var10 = (float)((double)var10 * 1.66D);
                        }
                     }
                  }
               }

               float var18 = var16 + var2;
               if (var18 < 0.0F) {
                  var18 = 0.0F;
                  var2 = -var16;
               }

               if (var18 > var17) {
                  var18 = var17;
                  var2 = var17 - var16;
               }

               this.XPMap.put(var1, var18);
               PerkFactory.CheckForUnlockedPerks(this.chr, var7);
               var19 = var7.getTotalXpForLevel(this.chr.getPerkLevel(var7.type) + 1);
               if (!var7.isPassiv() && var16 < var19 && var18 >= var19 && this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && IsoGameCharacter.this.NumberOfPerksToPick > 0 && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
                  this.chr.getEmitter().playSoundImpl("GainExperienceLevel", (IsoObject)null);
               }

               IsoGameCharacter.XPMultiplier var20 = (IsoGameCharacter.XPMultiplier)this.getMultiplierMap().get(var7.type);
               if (var20 != null) {
                  float var14 = var7.getTotalXpForLevel(var20.minLevel - 1);
                  float var15 = var7.getTotalXpForLevel(var20.maxLevel);
                  if (var16 >= var14 && var18 < var14 || var16 < var15 && var18 >= var15) {
                     this.getMultiplierMap().remove(var7.type);
                  }
               }

               if (!var7.isPassiv() && var4) {
                  this.addGlobalXP(var2);
               }

               if (var3) {
                  LuaEventManager.triggerEventGarbage("AddXP", this.chr, var1, var2);
               }

            }
         }
      }

      public void addGlobalXP(float var1) {
         this.TotalXP += var1;
         float var2 = (float)IsoGameCharacter.this.getXpForLevel(this.getLevel());
         boolean var3 = false;

         for(int var4 = 0; var4 < PerkFactory.PerkList.size(); ++var4) {
            PerkFactory.Perk var5 = (PerkFactory.Perk)PerkFactory.PerkList.get(var4);
            int var6 = this.chr.getPerkLevel(var5.type);
            if (!var5.isPassiv() && var6 < 10 && this.getXP(var5.type) >= var5.getTotalXpForLevel(var6 + 1)) {
               var3 = true;
               break;
            }
         }

         for(; this.TotalXP >= var2; var2 = (float)IsoGameCharacter.this.getXpForLevel(this.getLevel())) {
            this.setLevel(this.getLevel() + 1);
            ++this.chr.NumberOfPerksToPick;
            if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && var3 && IsoGameCharacter.this.NumberOfPerksToPick > 0 && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
               this.chr.getEmitter().playSound("GainExperienceLevel");
            }
         }

      }

      private boolean isSkillExcludedFromSpeedReduction(PerkFactory.Perks var1) {
         if (var1 == PerkFactory.Perks.Sprinting) {
            return true;
         } else if (var1 == PerkFactory.Perks.Fitness) {
            return true;
         } else {
            return var1 == PerkFactory.Perks.Strength;
         }
      }

      private boolean isSkillExcludedFromSpeedIncrease(PerkFactory.Perks var1) {
         if (var1 == PerkFactory.Perks.Fitness) {
            return true;
         } else {
            return var1 == PerkFactory.Perks.Strength;
         }
      }

      public float getXP(PerkFactory.Perks var1) {
         return this.XPMap.containsKey(var1) ? (Float)this.XPMap.get(var1) : 0.0F;
      }

      public void AddXP(HandWeapon var1, int var2) {
      }

      public void setTotalXP(float var1) {
         this.TotalXP = var1;
      }

      public void load(ByteBuffer var1, int var2) throws IOException {
         int var3 = var1.getInt();
         this.chr.Traits.clear();

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            String var5 = GameWindow.ReadString(var1);
            if (TraitFactory.getTrait(var5) != null) {
               if (!this.chr.Traits.contains(var5)) {
                  this.chr.Traits.add(var5);
               }
            } else {
               DebugLog.log("ERROR: unknown trait \"" + var5 + "\"");
            }
         }

         if (var2 >= 112) {
            this.TotalXP = var1.getFloat();
         } else {
            this.TotalXP = (float)var1.getInt();
         }

         this.level = var1.getInt();
         this.lastlevel = var1.getInt();
         this.XPMap.clear();
         var4 = var1.getInt();

         int var13;
         for(var13 = 0; var13 < var4; ++var13) {
            this.XPMap.put(PerkFactory.Perks.fromIndex(var1.getInt()), (float)var1.getInt());
         }

         var13 = var1.getInt();
         this.chr.CanUpgradePerk.clear();

         int var6;
         for(var6 = 0; var6 < var13; ++var6) {
            this.chr.CanUpgradePerk.add(PerkFactory.Perks.fromIndex(var1.getInt()));
         }

         IsoGameCharacter.this.PerkList.clear();
         var6 = var1.getInt();

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            PerkFactory.Perks var8 = PerkFactory.Perks.fromIndex(var1.getInt());
            PerkFactory.Perk var9 = (PerkFactory.Perk)PerkFactory.PerkMap.get(var8);
            IsoGameCharacter.PerkInfo var10 = IsoGameCharacter.this.new PerkInfo();
            var10.perk = var9;
            var10.perkType = var8;
            var10.level = var1.getInt();
            IsoGameCharacter.this.PerkList.add(var10);
         }

         var7 = var1.getInt();

         for(int var14 = 0; var14 < var7; ++var14) {
            PerkFactory.Perks var15;
            float var16;
            if (var2 >= 60) {
               var15 = PerkFactory.Perks.fromIndex(var1.getInt());
               var16 = var1.getFloat();
               byte var17 = var1.get();
               byte var18 = var1.get();
               this.addXpMultiplier(var15, var16, var17, var18);
            } else {
               int var11;
               int var12;
               if (var2 < 57) {
                  var15 = PerkFactory.Perks.fromIndex(var1.getInt());
                  var16 = (float)var1.getInt();
                  var11 = this.chr.getPerkLevel(var15) * 2 + 1;
                  var12 = var11 + 1;
                  this.addXpMultiplier(var15, var16, var11, var12);
               } else {
                  var15 = PerkFactory.Perks.fromIndex(var1.getInt());
                  var16 = (float)var1.getInt();
                  var11 = this.chr.getPerkLevel(var15) + 1;
                  if (var11 == 2 || var11 == 4 || var11 == 6 || var11 == 8 || var11 == 10) {
                     --var11;
                  }

                  var12 = var11 + 1;
                  this.addXpMultiplier(var15, var16, var11, var12);
               }
            }
         }

         if (this.TotalXP > (float)IsoGameCharacter.this.getXpForLevel(this.getLevel() + 1)) {
            this.setTotalXP((float)this.chr.getXpForLevel(this.getLevel()));
            this.addGlobalXP(1.0F);
         }

         this.convertXp(var2);
      }

      public void save(ByteBuffer var1) throws IOException {
         var1.putInt(this.chr.Traits.size());

         for(int var2 = 0; var2 < this.chr.Traits.size(); ++var2) {
            GameWindow.WriteString(var1, (String)this.chr.Traits.get(var2));
         }

         var1.putFloat(this.TotalXP);
         var1.putInt(this.level);
         var1.putInt(this.lastlevel);
         var1.putInt(this.XPMap.size());
         Iterator var5 = this.XPMap.entrySet().iterator();

         while(var5 != null && var5.hasNext()) {
            Entry var3 = (Entry)var5.next();
            var1.putInt(((PerkFactory.Perks)var3.getKey()).index());
            var1.putInt(((Float)var3.getValue()).intValue());
         }

         var1.putInt(this.chr.CanUpgradePerk.size());

         int var6;
         for(var6 = 0; var6 < this.chr.CanUpgradePerk.size(); ++var6) {
            int var4 = ((PerkFactory.Perks)this.chr.CanUpgradePerk.get(var6)).index();
            var1.putInt(var4);
         }

         var1.putInt(IsoGameCharacter.this.PerkList.size());

         for(var6 = 0; var6 < IsoGameCharacter.this.PerkList.size(); ++var6) {
            var1.putInt(((IsoGameCharacter.PerkInfo)IsoGameCharacter.this.PerkList.get(var6)).perkType.index());
            var1.putInt(((IsoGameCharacter.PerkInfo)IsoGameCharacter.this.PerkList.get(var6)).level);
         }

         var1.putInt(this.XPMapMultiplier.size());
         Iterator var8 = this.XPMapMultiplier.entrySet().iterator();

         while(var8 != null && var8.hasNext()) {
            Entry var7 = (Entry)var8.next();
            var1.putInt(((PerkFactory.Perks)var7.getKey()).index());
            var1.putFloat(((IsoGameCharacter.XPMultiplier)var7.getValue()).multiplier);
            var1.put((byte)((IsoGameCharacter.XPMultiplier)var7.getValue()).minLevel);
            var1.put((byte)((IsoGameCharacter.XPMultiplier)var7.getValue()).maxLevel);
         }

      }

      private void convertXp(int var1) {
         if (var1 < 57) {
            HashMap var2 = new HashMap();
            var2.put(PerkFactory.Perks.BluntParent, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.BladeParent, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Blunt, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.BluntGuard, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.BluntMaintenance, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Axe, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.BladeGuard, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.BladeMaintenance, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Firearm, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Aiming, new float[]{50.0F, 150.0F, 750.0F, 2650.0F, 4150.0F});
            var2.put(PerkFactory.Perks.Reloading, new float[]{50.0F, 150.0F, 450.0F, 1050.0F, 1950.0F});
            var2.put(PerkFactory.Perks.Crafting, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Woodwork, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Cooking, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Farming, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Doctor, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Survivalist, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Fishing, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Trapping, new float[]{50.0F, 150.0F, 450.0F, 1050.0F, 1950.0F});
            var2.put(PerkFactory.Perks.PlantScavenging, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Passiv, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Fitness, new float[]{2000.0F, 5000.0F, 23000.0F, 81000.0F, 169000.0F});
            var2.put(PerkFactory.Perks.Strength, new float[]{2000.0F, 5000.0F, 23000.0F, 81000.0F, 169000.0F});
            var2.put(PerkFactory.Perks.Agility, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Sprinting, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Lightfoot, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Nimble, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
            var2.put(PerkFactory.Perks.Sneak, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});

            for(int var4 = 0; var4 < PerkFactory.PerkList.size(); ++var4) {
               PerkFactory.Perk var5 = (PerkFactory.Perk)PerkFactory.PerkList.get(var4);
               if (this.XPMap.containsKey(var5.type)) {
                  float var6 = 0.0F;
                  float[] var7 = (float[])var2.get(var5.type);

                  for(int var8 = 0; var8 < 5; ++var8) {
                     var7[var8] *= 2.0F;
                     var6 += var7[var8];
                  }

                  float var18 = (Float)this.XPMap.get(var5.type);
                  var18 = Math.max(var18, 0.0F);
                  var18 = Math.min(var18, var6);
                  float var9 = 0.0F;

                  for(int var10 = 1; var10 <= 5; ++var10) {
                     if (var18 >= var9 && (var18 == var6 || var18 < var9 + var7[var10 - 1])) {
                        float var11 = (var18 - var9) / var7[var10 - 1];
                        int var12 = var10 * 2;
                        float var13 = var5.getTotalXpForLevel(var12 - 2);
                        float var14 = var5.getTotalXpForLevel(var12);
                        float var15 = var13 + (var14 - var13) * var11;
                        this.XPMap.put(var5.type, var15);
                        DebugLog.log("XP updated: " + var5.getName() + " " + var18 + " -> " + var15);
                        break;
                     }

                     var9 += var7[var10 - 1];
                  }
               }

               IsoGameCharacter.PerkInfo var16 = IsoGameCharacter.this.getPerkInfo(var5.type);
               if (var16 != null) {
                  if (var5.type != PerkFactory.Perks.Fitness && var5.type != PerkFactory.Perks.Strength) {
                     var16.level *= 2;
                  } else {
                     float var17 = this.getXP(var5.type);
                     switch(var16.level) {
                     case 0:
                        if (var17 >= var5.getTotalXpForLevel(1)) {
                           var16.level = 1;
                        }
                        break;
                     case 1:
                        if (var17 >= var5.getTotalXpForLevel(4)) {
                           var16.level = 4;
                        } else if (var17 >= var5.getTotalXpForLevel(3)) {
                           var16.level = 3;
                        } else {
                           var16.level = 2;
                        }
                        break;
                     case 2:
                        var16.level = 5;
                        break;
                     case 3:
                        if (var17 >= var5.getTotalXpForLevel(8)) {
                           var16.level = 8;
                        } else if (var17 >= var5.getTotalXpForLevel(7)) {
                           var16.level = 7;
                        } else {
                           var16.level = 6;
                        }
                        break;
                     case 4:
                        var16.level = 9;
                        break;
                     case 5:
                        var16.level = 10;
                     }
                  }
               }
            }
         }

      }

      public void setXPToLevel(PerkFactory.Perks var1, int var2) {
         PerkFactory.Perk var3 = null;

         for(int var4 = 0; var4 < PerkFactory.PerkList.size(); ++var4) {
            PerkFactory.Perk var5 = (PerkFactory.Perk)PerkFactory.PerkList.get(var4);
            if (var5.getType() == var1) {
               var3 = var5;
               break;
            }
         }

         if (var3 != null) {
            this.XPMap.put(var1, var3.getTotalXpForLevel(var2));
         }

      }
   }

   public static class XPMultiplier {
      public float multiplier;
      public int minLevel;
      public int maxLevel;
   }

   public class PerkInfo {
      public int level = 0;
      public PerkFactory.Perk perk;
      public PerkFactory.Perks perkType;

      public int getLevel() {
         return this.level;
      }
   }
}
