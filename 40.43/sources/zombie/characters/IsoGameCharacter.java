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


public class IsoGameCharacter extends IsoMovingObject implements Talker,ChatElementOwner {
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

	public void setAnimForecasted(int int1) {
		this.isAnimForecasted = System.currentTimeMillis() + (long)int1;
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

	public void setModel(String string) {
		try {
			if (string == null) {
				ModelManager.instance.Remove(this);
			} else if (!ModelManager.instance.Contains.contains(this)) {
				ModelManager.instance.Add(this);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
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

	public boolean hasItems(String string, int int1) {
		int int2 = this.inventory.getItemCount(string);
		return int1 <= int2;
	}

	public static HashMap getSurvivorMap() {
		return SurvivorMap;
	}

	public static void setSurvivorMap(HashMap hashMap) {
		SurvivorMap = hashMap;
	}

	public static int[] getLevelUpLevels() {
		return LevelUpLevels;
	}

	public int getLevelUpLevels(int int1) {
		return LevelUpLevels.length <= int1 ? LevelUpLevels[LevelUpLevels.length - 1] : LevelUpLevels[int1];
	}

	public int getLevelMaxForXp() {
		return LevelUpLevels.length;
	}

	public int getXpForLevel(int int1) {
		return int1 < LevelUpLevels.length ? (int)((float)LevelUpLevels[int1] * this.LevelUpMultiplier) : (int)((float)(LevelUpLevels[LevelUpLevels.length - 1] + (int1 - LevelUpLevels.length + 1) * 400) * this.LevelUpMultiplier);
	}

	public static void setLevelUpLevels(int[] intArray) {
		LevelUpLevels = intArray;
	}

	public static Vector2 getTempo() {
		return tempo;
	}

	public static void setTempo(Vector2 vector2) {
		tempo = vector2;
	}

	public static ColorInfo getInf() {
		return inf;
	}

	public static void setInf(ColorInfo colorInfo) {
		inf = colorInfo;
	}

	public static OnceEvery getTestPlayerSpotInDarkness() {
		return testPlayerSpotInDarkness;
	}

	public static void setTestPlayerSpotInDarkness(OnceEvery onceEvery) {
		testPlayerSpotInDarkness = onceEvery;
	}

	public void DoDeath(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		this.DoDeath(handWeapon, gameCharacter, true);
	}

	public void DoDeath(HandWeapon handWeapon, IsoGameCharacter gameCharacter, boolean boolean1) {
		this.OnDeath();
		if (this.getHitBy() instanceof IsoPlayer && GameServer.bServer && this instanceof IsoPlayer) {
			String string = "";
			String string2 = "";
			if (SteamUtils.isSteamModeEnabled()) {
				string = " (" + ((IsoPlayer)this.getHitBy()).getSteamID() + ") ";
				string2 = " (" + ((IsoPlayer)this).getSteamID() + ") ";
			}

			LoggerManager.getLogger("pvp").write("user " + ((IsoPlayer)this.getHitBy()).username + string + " killed " + ((IsoPlayer)this).username + string2 + " " + LoggerManager.getPlayerCoords((IsoPlayer)this), "IMPORTANT");
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
			float float1 = 0.6F;
			if (this instanceof IsoZombie && ((IsoZombie)this).bCrawling || this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieDeath".equals(this.legsSprite.CurrentAnim.name)) {
				float1 = 0.3F;
			}

			if (GameServer.bServer) {
				boolean boolean2 = this.isOnFloor() && gameCharacter instanceof IsoPlayer && handWeapon != null && "BareHands".equals(handWeapon.getType());
				GameServer.sendBloodSplatter(handWeapon, this.getX(), this.getY(), this.getZ() + float1, this.getHitDir(), this.isCloseKilled(), boolean2);
			}

			if (this.bUseParts && handWeapon != null && handWeapon.getType().equals("Shotgun")) {
				this.headSprite = null;
			}

			int int1;
			int int2;
			if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
				int2 = handWeapon.getSplatNumber();
				if (int2 < 1) {
					int2 = 1;
				}

				if (Core.bLastStand) {
					int2 *= 3;
				}

				switch (SandboxOptions.instance.BloodLevel.getValue()) {
				case 2: 
					int2 /= 2;
				
				case 3: 
				
				default: 
					break;
				
				case 4: 
					int2 *= 2;
					break;
				
				case 5: 
					int2 *= 5;
				
				}

				for (int1 = 0; int1 < int2; ++int1) {
					this.splatBlood(3, 0.3F);
				}
			}

			if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
				this.splatBloodFloorBig(0.3F);
			}

			if (gameCharacter != null && gameCharacter.xp != null) {
				gameCharacter.xp.AddXP(handWeapon, 3);
			}

			if (SandboxOptions.instance.BloodLevel.getValue() > 1 && this.isOnFloor() && gameCharacter instanceof IsoPlayer && handWeapon == ((IsoPlayer)gameCharacter).bareHands) {
				for (int2 = -1; int2 <= 1; ++int2) {
					for (int1 = -1; int1 <= 1; ++int1) {
						if (int2 != 0 || int1 != 0) {
							new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, (float)int2 * Rand.Next(0.25F, 0.5F), (float)int1 * Rand.Next(0.25F, 0.5F));
						}
					}
				}

				new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
			} else if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
				tempo.x = this.getHitDir().x;
				tempo.y = this.getHitDir().y;
				if (Core.getInstance().getGameMode().equals("Tutorial")) {
					for (int2 = 0; int2 < 4; ++int2) {
						for (int1 = -2; int1 <= 2; ++int1) {
							for (int int3 = -2; int3 <= 2; ++int3) {
								if (int1 != 0 || int3 != 0) {
									new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, (float)int1 * Rand.Next(0.25F, 0.7F), (float)int3 * Rand.Next(0.25F, 0.7F));
								}
							}
						}
					}
				}

				byte byte1 = 3;
				byte byte2 = 0;
				byte byte3 = 1;
				switch (SandboxOptions.instance.BloodLevel.getValue()) {
				case 1: 
					byte3 = 0;
					break;
				
				case 2: 
					byte3 = 1;
					byte1 = 5;
					byte2 = 2;
				
				case 3: 
				
				default: 
					break;
				
				case 4: 
					byte3 = 3;
					byte1 = 2;
					break;
				
				case 5: 
					byte3 = 10;
					byte1 = 0;
				
				}

				for (int int4 = 0; int4 < byte3; ++int4) {
					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.8F, this.getHitDir().y * 1.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.9F, this.getHitDir().y * 1.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 3.5F, this.getHitDir().y * 3.5F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
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

	private void TestIfSeen(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player != null && this != player && !GameServer.bServer) {
			float float1 = this.DistTo(player);
			if (!(float1 > GameTime.getInstance().getViewDist())) {
				float float2 = (this.getCurrentSquare().lighting[int1].lightInfo().r + this.getCurrentSquare().lighting[int1].lightInfo().g + this.getCurrentSquare().lighting[int1].lightInfo().b) / 3.0F;
				if (float2 > 0.4F) {
					float2 = 1.0F;
				}

				float float3 = 1.0F - float1 / GameTime.getInstance().getViewDist();
				if (float2 == 1.0F && float3 > 0.3F) {
					float3 = 1.0F;
				}

				tempo.x = this.getX();
				tempo.y = this.getY();
				Vector2 vector2 = tempo;
				vector2.x -= player.getX();
				vector2 = tempo;
				vector2.y -= player.getY();
				Vector2 vector22 = player.getVectorFromDirection(tempo2);
				tempo.normalize();
				float float4 = vector22.dot(tempo);
				if (float4 < 0.5F) {
					float4 = 0.5F;
				}

				float2 *= float4;
				if (float2 < 0.0F) {
					float2 = 0.0F;
				}

				if (float1 <= 1.0F) {
					float3 = 1.0F;
					float2 *= 2.0F;
				}

				float2 *= float3;
				float2 *= 100.0F;
				if ((float)Rand.Next(Rand.AdjustForFramerate(100)) < float2 || !(this instanceof IsoGameCharacter)) {
					this.SpottedSinceAlphaZero[int1] = true;
					this.timeTillForgetLocation[int1] = 600;
				}
			}
		}
	}

	private void DoLand() {
		float float1 = GameServer.bServer ? 10.0F : (float)PerformanceSettings.LockFPS;
		this.fallTime = (int)((float)this.fallTime * (30.0F / float1));
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
				boolean boolean1 = Rand.Next(80) == 0;
				float float2 = (float)this.fallTime * 1.5F;
				float2 *= this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight();
				if (this.getCurrentSquare().getFloor() != null && this.getCurrentSquare().getFloor().getSprite().getName() != null && this.getCurrentSquare().getFloor().getSprite().getName().startsWith("blends_natural")) {
					float2 *= 0.8F;
					if (!boolean1) {
						boolean1 = Rand.Next(65) == 0;
					}
				}

				if (!boolean1) {
					if (this.HasTrait("Obese") || this.HasTrait("Emaciated")) {
						float2 *= 1.4F;
					}

					if (this.HasTrait("Overweight") || this.HasTrait("Very Underweight")) {
						float2 *= 1.2F;
					}

					if (this.getPerkLevel(PerkFactory.Perks.Fitness) > 4) {
						float2 *= (float)(this.getPerkLevel(PerkFactory.Perks.Fitness) - 4) * 0.1F;
					}

					if (this.fallTime > 135) {
						float2 = 1000.0F;
					}

					this.BodyDamage.ReduceGeneralHealth(float2);
					if (this.fallTime > 70) {
						int int1 = 100 - (int)((double)this.fallTime * 0.6);
						if (this.getInventory().getMaxWeight() - this.getInventory().getCapacityWeight() < 2.0F) {
							int1 = (int)((float)int1 - this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight() * 100.0F / 5.0F);
						}

						if (this.HasTrait("Obese") || this.HasTrait("Emaciated")) {
							int1 -= 20;
						}

						if (this.HasTrait("Overweight") || this.HasTrait("Very Underweight")) {
							int1 -= 10;
						}

						if (this.getPerkLevel(PerkFactory.Perks.Fitness) > 4) {
							int1 += (this.getPerkLevel(PerkFactory.Perks.Fitness) - 4) * 3;
						}

						if (Rand.Next(100) >= int1) {
							if (!SandboxOptions.instance.BoneFracture.getValue()) {
								return;
							}

							float float3 = (float)Rand.Next(50, 80);
							if (this.HasTrait("FastHealer")) {
								float3 = (float)Rand.Next(30, 50);
							} else if (this.HasTrait("SlowHealer")) {
								float3 = (float)Rand.Next(80, 150);
							}

							switch (SandboxOptions.instance.InjurySeverity.getValue()) {
							case 1: 
								float3 *= 0.5F;
								break;
							
							case 3: 
								float3 *= 1.5F;
							
							}

							this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1))).setFractureTime(float3);
						} else if (Rand.Next(100) >= int1 - 10) {
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

	public void setFollowingTarget(IsoGameCharacter gameCharacter) {
		this.FollowingTarget = gameCharacter;
	}

	public ArrayList getLocalList() {
		return this.LocalList;
	}

	public void setLocalList(ArrayList arrayList) {
		this.LocalList = arrayList;
	}

	public ArrayList getLocalNeutralList() {
		return this.LocalNeutralList;
	}

	public void setLocalNeutralList(ArrayList arrayList) {
		this.LocalNeutralList = arrayList;
	}

	public ArrayList getLocalGroupList() {
		return this.LocalGroupList;
	}

	public void setLocalGroupList(ArrayList arrayList) {
		this.LocalGroupList = arrayList;
	}

	public ArrayList getLocalRelevantEnemyList() {
		return this.LocalRelevantEnemyList;
	}

	public void setLocalRelevantEnemyList(ArrayList arrayList) {
		this.LocalRelevantEnemyList = arrayList;
	}

	public float getDangerLevels() {
		return this.dangerLevels;
	}

	public void setDangerLevels(float float1) {
		this.dangerLevels = float1;
	}

	public Stack getMeetList() {
		return this.MeetList;
	}

	public void setMeetList(Stack stack) {
		this.MeetList = stack;
	}

	public Order getOrder() {
		return this.Order;
	}

	public void setOrder(Order order) {
		this.Order = order;
	}

	public Stack getOrders() {
		return this.Orders;
	}

	public void setOrders(Stack stack) {
		this.Orders = stack;
	}

	public ArrayList getPerkList() {
		return this.PerkList;
	}

	public void setPerkList(ArrayList arrayList) {
		this.PerkList = arrayList;
	}

	public Order getPersonalNeed() {
		return this.PersonalNeed;
	}

	public void setPersonalNeed(Order order) {
		this.PersonalNeed = order;
	}

	public Stack getPersonalNeeds() {
		return this.PersonalNeeds;
	}

	public void setPersonalNeeds(Stack stack) {
		this.PersonalNeeds = stack;
	}

	public float getLeaveBodyTimedown() {
		return this.leaveBodyTimedown;
	}

	public void setLeaveBodyTimedown(float float1) {
		this.leaveBodyTimedown = float1;
	}

	public boolean isAllowConversation() {
		return this.AllowConversation;
	}

	public void setAllowConversation(boolean boolean1) {
		this.AllowConversation = boolean1;
	}

	public int getReanimPhase() {
		return this.ReanimPhase;
	}

	public void setReanimPhase(int int1) {
		this.ReanimPhase = int1;
	}

	public float getReanimateTimer() {
		return this.ReanimateTimer;
	}

	public void setReanimateTimer(float float1) {
		this.ReanimateTimer = float1;
	}

	public int getReanimAnimFrame() {
		return this.ReanimAnimFrame;
	}

	public void setReanimAnimFrame(int int1) {
		this.ReanimAnimFrame = int1;
	}

	public int getReanimAnimDelay() {
		return this.ReanimAnimDelay;
	}

	public void setReanimAnimDelay(int int1) {
		this.ReanimAnimDelay = int1;
	}

	public boolean isReanim() {
		return this.Reanim;
	}

	public void setReanim(boolean boolean1) {
		this.Reanim = boolean1;
	}

	public boolean isVisibleToNPCs() {
		return this.VisibleToNPCs;
	}

	public void setVisibleToNPCs(boolean boolean1) {
		this.VisibleToNPCs = boolean1;
	}

	public int getDieCount() {
		return this.DieCount;
	}

	public void setDieCount(int int1) {
		this.DieCount = int1;
	}

	public float getLlx() {
		return this.llx;
	}

	public void setLlx(float float1) {
		this.llx = float1;
	}

	public float getLly() {
		return this.lly;
	}

	public void setLly(float float1) {
		this.lly = float1;
	}

	public float getLlz() {
		return this.llz;
	}

	public void setLlz(float float1) {
		this.llz = float1;
	}

	public int getRemoteID() {
		return this.RemoteID;
	}

	public void setRemoteID(int int1) {
		this.RemoteID = int1;
	}

	public int getNumSurvivorsInVicinity() {
		return this.NumSurvivorsInVicinity;
	}

	public void setNumSurvivorsInVicinity(int int1) {
		this.NumSurvivorsInVicinity = int1;
	}

	public float getLevelUpMultiplier() {
		return this.LevelUpMultiplier;
	}

	public void setLevelUpMultiplier(float float1) {
		this.LevelUpMultiplier = float1;
	}

	public IsoGameCharacter.XP getXp() {
		return this.xp;
	}

	public void setXp(IsoGameCharacter.XP xP) {
		this.xp = xP;
	}

	public int getNumberOfPerksToPick() {
		return this.NumberOfPerksToPick;
	}

	public void setNumberOfPerksToPick(int int1) {
		this.NumberOfPerksToPick = int1;
	}

	public ArrayList getCanUpgradePerk() {
		return this.CanUpgradePerk;
	}

	public void setCanUpgradePerk(ArrayList arrayList) {
		this.CanUpgradePerk = arrayList;
	}

	public int getLastLocalEnemies() {
		return this.LastLocalEnemies;
	}

	public void setLastLocalEnemies(int int1) {
		this.LastLocalEnemies = int1;
	}

	public ArrayList getVeryCloseEnemyList() {
		return this.VeryCloseEnemyList;
	}

	public void setVeryCloseEnemyList(ArrayList arrayList) {
		this.VeryCloseEnemyList = arrayList;
	}

	public HashMap getLastKnownLocation() {
		return this.LastKnownLocation;
	}

	public void setLastKnownLocation(HashMap hashMap) {
		this.LastKnownLocation = hashMap;
	}

	public IsoGameCharacter getAttackedBy() {
		return this.AttackedBy;
	}

	public void setAttackedBy(IsoGameCharacter gameCharacter) {
		this.AttackedBy = gameCharacter;
	}

	public boolean isIgnoreStaggerBack() {
		return this.IgnoreStaggerBack;
	}

	public void setIgnoreStaggerBack(boolean boolean1) {
		this.IgnoreStaggerBack = boolean1;
	}

	public boolean isAttackWasSuperAttack() {
		return this.AttackWasSuperAttack;
	}

	public void setAttackWasSuperAttack(boolean boolean1) {
		this.AttackWasSuperAttack = boolean1;
	}

	public int getTimeThumping() {
		return this.TimeThumping;
	}

	public void setTimeThumping(int int1) {
		this.TimeThumping = int1;
	}

	public int getPatienceMax() {
		return this.PatienceMax;
	}

	public void setPatienceMax(int int1) {
		this.PatienceMax = int1;
	}

	public int getPatienceMin() {
		return this.PatienceMin;
	}

	public void setPatienceMin(int int1) {
		this.PatienceMin = int1;
	}

	public int getPatience() {
		return this.Patience;
	}

	public void setPatience(int int1) {
		this.Patience = int1;
	}

	public Stack getCharacterActions() {
		return this.CharacterActions;
	}

	public void setCharacterActions(Stack stack) {
		this.CharacterActions = stack;
	}

	public Vector2 getAngle() {
		return this.angle;
	}

	public void setAngle(Vector2 vector2) {
		this.angle = vector2;
	}

	public boolean isAsleep() {
		return this.Asleep;
	}

	public void setAsleep(boolean boolean1) {
		this.Asleep = boolean1;
	}

	public float getAttackDelay() {
		return this.AttackDelay;
	}

	public void setAttackDelay(float float1) {
		this.AttackDelay = float1;
	}

	public float getAttackDelayMax() {
		return this.AttackDelayMax;
	}

	public void setAttackDelayMax(float float1) {
		this.AttackDelayMax = float1;
	}

	public float getAttackDelayUse() {
		return this.AttackDelayUse;
	}

	public void setAttackDelayUse(float float1) {
		this.AttackDelayUse = float1;
	}

	public int getZombieKills() {
		return this.ZombieKills;
	}

	public void setZombieKills(int int1) {
		this.ZombieKills = int1;
	}

	public int getLastZombieKills() {
		return this.LastZombieKills;
	}

	public void setLastZombieKills(int int1) {
		this.LastZombieKills = int1;
	}

	public boolean isSuperAttack() {
		return this.superAttack;
	}

	public void setSuperAttack(boolean boolean1) {
		this.superAttack = boolean1;
	}

	public float getForceWakeUpTime() {
		return this.ForceWakeUpTime;
	}

	public void setForceWakeUpTime(float float1) {
		this.ForceWakeUpTime = float1;
	}

	public void forceAwake() {
		if (this.isAsleep()) {
			this.ForceWakeUp = true;
		}
	}

	public BodyDamage getBodyDamage() {
		return this.BodyDamage;
	}

	public void setBodyDamage(BodyDamage bodyDamage) {
		this.BodyDamage = bodyDamage;
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

	public void setCraftIngredient1(InventoryItem inventoryItem) {
		this.craftIngredient1 = inventoryItem;
	}

	public InventoryItem getCraftIngredient2() {
		return this.craftIngredient2;
	}

	public void setCraftIngredient2(InventoryItem inventoryItem) {
		this.craftIngredient2 = inventoryItem;
	}

	public InventoryItem getCraftIngredient3() {
		return this.craftIngredient3;
	}

	public void setCraftIngredient3(InventoryItem inventoryItem) {
		this.craftIngredient3 = inventoryItem;
	}

	public InventoryItem getCraftIngredient4() {
		return this.craftIngredient4;
	}

	public void setCraftIngredient4(InventoryItem inventoryItem) {
		this.craftIngredient4 = inventoryItem;
	}

	public State getDefaultState() {
		return this.defaultState;
	}

	public void setDefaultState(State state) {
		this.defaultState = state;
	}

	public SurvivorDesc getDescriptor() {
		return this.descriptor;
	}

	public void setDescriptor(SurvivorDesc survivorDesc) {
		this.descriptor = survivorDesc;
	}

	public String getFullName() {
		return this.descriptor != null ? this.descriptor.forename + " " + this.descriptor.surname : "Bob Smith";
	}

	public Stack getFamiliarBuildings() {
		return this.FamiliarBuildings;
	}

	public void setFamiliarBuildings(Stack stack) {
		this.FamiliarBuildings = stack;
	}

	public AStarPathFinderResult getFinder() {
		return this.finder;
	}

	public void setFinder(AStarPathFinderResult aStarPathFinderResult) {
		this.finder = aStarPathFinderResult;
	}

	public float getFireKillRate() {
		return this.FireKillRate;
	}

	public void setFireKillRate(float float1) {
		this.FireKillRate = float1;
	}

	public int getFireSpreadProbability() {
		return this.FireSpreadProbability;
	}

	public void setFireSpreadProbability(int int1) {
		this.FireSpreadProbability = int1;
	}

	public float getFootStepCounter() {
		return this.footStepCounter;
	}

	public void setFootStepCounter(float float1) {
		this.footStepCounter = float1;
	}

	public float getFootStepCounterMax() {
		return this.footStepCounterMax;
	}

	public void setFootStepCounterMax(float float1) {
		this.footStepCounterMax = float1;
	}

	public float getHealth() {
		return this.Health;
	}

	public void setHealth(float float1) {
		this.Health = float1;
	}

	public MasterSurvivorBehavior getMasterProper() {
		return this.masterProper;
	}

	public void setMasterProper(MasterSurvivorBehavior masterSurvivorBehavior) {
		this.masterProper = masterSurvivorBehavior;
	}

	public IsoGameCharacter getHitBy() {
		return this.hitBy;
	}

	public void setHitBy(IsoGameCharacter gameCharacter) {
		this.hitBy = gameCharacter;
	}

	public String getHurtSound() {
		return this.hurtSound;
	}

	public void setHurtSound(String string) {
		this.hurtSound = string;
	}

	public boolean isIgnoreMovementForDirection() {
		return this.IgnoreMovementForDirection;
	}

	public void setIgnoreMovementForDirection(boolean boolean1) {
		this.IgnoreMovementForDirection = boolean1;
	}

	public ItemContainer getInventory() {
		return this.inventory;
	}

	public void setInventory(ItemContainer itemContainer) {
		this.inventory = itemContainer;
		this.inventory.setExplored(true);
	}

	public IsoDirections getLastdir() {
		return this.lastdir;
	}

	public void setLastdir(IsoDirections directions) {
		this.lastdir = directions;
	}

	public boolean isPrimaryEquipped(String string) {
		return this.leftHandItem == null ? false : this.leftHandItem.getType().equals(string);
	}

	public InventoryItem getPrimaryHandItem() {
		return this.leftHandItem;
	}

	public void setPrimaryHandItem(InventoryItem inventoryItem) {
		this.setEquipParent(this.leftHandItem, inventoryItem);
		this.leftHandItem = inventoryItem;
		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.equip((IsoPlayer)this, 0, inventoryItem);
		}

		if (inventoryItem instanceof HandWeapon) {
			if (this.legsSprite != null && this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.contains("Attack_") && ((HandWeapon)inventoryItem).getSwingAnim() != null) {
				this.PlayAnim("Attack_" + ((HandWeapon)inventoryItem).getSwingAnim());
				this.def.Finished = true;
				this.def.Frame = 0.0F;
			}
		} else if (this.legsSprite != null && this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.contains("Attack_")) {
			this.PlayAnim("Idle");
		}

		LuaEventManager.triggerEvent("OnEquipPrimary", this, inventoryItem);
		this.resetModel();
	}

	protected void setEquipParent(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
		if (inventoryItem != null) {
			inventoryItem.setEquipParent((IsoGameCharacter)null);
		}

		if (inventoryItem2 != null) {
			inventoryItem2.setEquipParent(this);
		}
	}

	public InventoryItem getClothingItem_Head() {
		return this.ClothingItem_Head;
	}

	public void setClothingItem_Head(InventoryItem inventoryItem) {
		this.ClothingItem_Head = inventoryItem;
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

	public void setClothingItem_Back(InventoryItem inventoryItem) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (this.ClothingItem_Back != null) {
			cell.addToProcessItemsRemove(this.ClothingItem_Back);
		}

		this.ClothingItem_Back = inventoryItem;
		if (this.ClothingItem_Back != null && this.ClothingItem_Back.getContainer() != null) {
			this.ClothingItem_Back.getContainer().parent = this;
		}

		if (this.ClothingItem_Back != null) {
			cell.addToProcessItems(this.ClothingItem_Back);
		}
	}

	public void setClothingItem_Torso(InventoryItem inventoryItem) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (this.ClothingItem_Torso != null) {
			cell.addToProcessItemsRemove(this.ClothingItem_Torso);
		}

		this.ClothingItem_Torso = inventoryItem;
		if (this.ClothingItem_Torso != null && this.ClothingItem_Torso.getContainer() != null) {
			this.ClothingItem_Torso.getContainer().parent = this;
		}

		if (inventoryItem == null) {
			this.topSprite = null;
			this.descriptor.toppal = null;
		} else {
			this.descriptor.toppal = ((Clothing)inventoryItem).getPalette();
			if (!this.bFemale && this.descriptor.toppal.contains("Blouse")) {
				this.descriptor.toppal = this.descriptor.toppal.replace("Blouse", "Shirt");
			}

			this.descriptor.topColor.set(inventoryItem.col);
			cell.addToProcessItems(inventoryItem);
		}

		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.sendClothing((IsoPlayer)this, Item.ClothingBodyLocation.Top.ordinal(), this.ClothingItem_Torso);
		}

		this.resetModelNextFrame();
	}

	public InventoryItem getClothingItem_Hands() {
		return this.ClothingItem_Hands;
	}

	public void setClothingItem_Hands(InventoryItem inventoryItem) {
		this.ClothingItem_Hands = inventoryItem;
		if (this.ClothingItem_Hands != null && this.ClothingItem_Hands.getContainer() != null) {
			this.ClothingItem_Hands.getContainer().parent = this;
		}
	}

	public InventoryItem getClothingItem_Legs() {
		return this.ClothingItem_Legs;
	}

	public void setClothingItem_Legs(InventoryItem inventoryItem) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (this.ClothingItem_Legs != null) {
			cell.addToProcessItemsRemove(this.ClothingItem_Legs);
		}

		this.ClothingItem_Legs = inventoryItem;
		if (this.ClothingItem_Legs != null && this.ClothingItem_Legs.getContainer() != null) {
			this.ClothingItem_Legs.getContainer().parent = this;
		}

		if (inventoryItem == null) {
			this.bottomsSprite = null;
			this.descriptor.bottomspal = null;
		} else {
			this.descriptor.bottomspal = ((Clothing)inventoryItem).getPalette();
			if (!this.bFemale && this.descriptor.bottomspal.contains("Skirt")) {
				this.descriptor.bottomspal = this.descriptor.bottomspal.replace("Skirt", "Trousers");
			}

			this.descriptor.trouserColor.set(inventoryItem.col);
			cell.addToProcessItems(inventoryItem);
		}

		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.sendClothing((IsoPlayer)this, Item.ClothingBodyLocation.Bottoms.ordinal(), this.ClothingItem_Legs);
		}

		this.resetModelNextFrame();
	}

	public InventoryItem getClothingItem_Feet() {
		return this.ClothingItem_Feet;
	}

	public void setClothingItem_Feet(InventoryItem inventoryItem) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (this.ClothingItem_Feet != null) {
			cell.addToProcessItemsRemove(this.ClothingItem_Feet);
		}

		this.ClothingItem_Feet = inventoryItem;
		if (this.ClothingItem_Feet != null && this.ClothingItem_Feet.getContainer() != null) {
			this.ClothingItem_Feet.getContainer().parent = this;
		}

		if (inventoryItem == null) {
			this.shoeSprite = null;
			this.descriptor.shoespal = null;
		} else {
			cell.addToProcessItems(this.ClothingItem_Feet);
		}

		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.sendClothing((IsoPlayer)this, Item.ClothingBodyLocation.Shoes.ordinal(), this.ClothingItem_Feet);
		}

		this.resetModelNextFrame();
	}

	public SequenceBehavior getMasterBehaviorList() {
		return this.masterBehaviorList;
	}

	public void setMasterBehaviorList(SequenceBehavior sequenceBehavior) {
		this.masterBehaviorList = sequenceBehavior;
	}

	public int getNextWander() {
		return this.NextWander;
	}

	public void setNextWander(int int1) {
		this.NextWander = int1;
	}

	public boolean isOnFire() {
		return this.OnFire;
	}

	public void setOnFire(boolean boolean1) {
		this.OnFire = boolean1;
	}

	public Path getPath() {
		return this.path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public int getPathIndex() {
		return this.pathIndex;
	}

	public void setPathIndex(int int1) {
		this.pathIndex = int1;
	}

	public float getPathSpeed() {
		return this.PathSpeed;
	}

	public void setPathSpeed(float float1) {
		if (this == IsoCamera.CamCharacter) {
		}

		this.PathSpeed = float1;
	}

	public int getPathTargetX() {
		return (int)this.getPathFindBehavior2().getTargetX();
	}

	public void setPathTargetX(int int1) {
	}

	public int getPathTargetY() {
		return (int)this.getPathFindBehavior2().getTargetY();
	}

	public void setPathTargetY(int int1) {
	}

	public int getPathTargetZ() {
		return (int)this.getPathFindBehavior2().getTargetZ();
	}

	public void setPathTargetZ(int int1) {
	}

	public SurvivorPersonality getPersonality() {
		return this.Personality;
	}

	public void setPersonality(SurvivorPersonality survivorPersonality) {
		this.Personality = survivorPersonality;
	}

	public InventoryItem getSecondaryHandItem() {
		return this.rightHandItem;
	}

	public void setSecondaryHandItem(InventoryItem inventoryItem) {
		this.setEquipParent(this.rightHandItem, inventoryItem);
		this.rightHandItem = inventoryItem;
		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.equip((IsoPlayer)this, 1, inventoryItem);
		}

		LuaEventManager.triggerEvent("OnEquipSecondary", this, inventoryItem);
	}

	public String getSayLineOld() {
		return this.sayLine;
	}

	public void setSayLine(String string) {
		this.Say(string);
	}

	public Color getSpeakColour() {
		return this.SpeakColour;
	}

	public void setSpeakColour(Color color) {
		this.SpeakColour = color;
	}

	public void setSpeakColourInfo(ColorInfo colorInfo) {
		this.SpeakColour = new Color(colorInfo.r, colorInfo.g, colorInfo.b, 1.0F);
	}

	public float getSlowFactor() {
		return this.slowFactor;
	}

	public void setSlowFactor(float float1) {
		this.slowFactor = float1;
	}

	public float getSlowTimer() {
		return this.slowTimer;
	}

	public void setSlowTimer(float float1) {
		this.slowTimer = float1;
	}

	public boolean isbUseParts() {
		return this.bUseParts;
	}

	public void setbUseParts(boolean boolean1) {
		this.bUseParts = boolean1;
	}

	public boolean isSpeaking() {
		return this.IsSpeaking();
	}

	public void setSpeaking(boolean boolean1) {
		this.Speaking = boolean1;
	}

	public float getSpeakTime() {
		return this.SpeakTime;
	}

	public void setSpeakTime(int int1) {
		this.SpeakTime = (float)int1;
	}

	public float getSpeedMod() {
		return this.speedMod;
	}

	public void setSpeedMod(float float1) {
		this.speedMod = float1;
	}

	public float getStaggerTimeMod() {
		return this.staggerTimeMod;
	}

	public void setStaggerTimeMod(float float1) {
		this.staggerTimeMod = float1;
	}

	public StateMachine getStateMachine() {
		return this.stateMachine;
	}

	public void setStateMachine(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public Moodles getMoodles() {
		return this.Moodles;
	}

	public void setMoodles(Moodles moodles) {
		this.Moodles = moodles;
	}

	public Stats getStats() {
		return this.stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public Stack getTagGroup() {
		return this.TagGroup;
	}

	public void setTagGroup(Stack stack) {
		this.TagGroup = stack;
	}

	public Stack getUsedItemsOn() {
		return this.UsedItemsOn;
	}

	public void setUsedItemsOn(Stack stack) {
		this.UsedItemsOn = stack;
	}

	public HandWeapon getUseHandWeapon() {
		return this.useHandWeapon;
	}

	public void setUseHandWeapon(HandWeapon handWeapon) {
		this.useHandWeapon = handWeapon;
	}

	public IsoSprite getTorsoSprite() {
		return this.torsoSprite;
	}

	public void setTorsoSprite(IsoSprite sprite) {
		this.torsoSprite = sprite;
	}

	public IsoSprite getLegsSprite() {
		return this.legsSprite;
	}

	public void setLegsSprite(IsoSprite sprite) {
		this.legsSprite = sprite;
	}

	public IsoSprite getHeadSprite() {
		return this.headSprite;
	}

	public void setHeadSprite(IsoSprite sprite) {
		this.headSprite = sprite;
	}

	public IsoSprite getShoeSprite() {
		return this.shoeSprite;
	}

	public void setShoeSprite(IsoSprite sprite) {
		this.shoeSprite = sprite;
	}

	public IsoSprite getTopSprite() {
		return this.topSprite;
	}

	public void setTopSprite(IsoSprite sprite) {
		this.topSprite = sprite;
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

	public void setHairSprite(IsoSprite sprite) {
		this.hairSprite = sprite;
	}

	public void setBottomsSprite(IsoSprite sprite) {
		this.bottomsSprite = sprite;
	}

	public Stack getWounds() {
		return this.wounds;
	}

	public void setWounds(Stack stack) {
		this.wounds = stack;
	}

	public IsoGridSquare getAttackTargetSquare() {
		return this.attackTargetSquare;
	}

	public void setAttackTargetSquare(IsoGridSquare square) {
		this.attackTargetSquare = square;
	}

	public float getBloodImpactX() {
		return this.BloodImpactX;
	}

	public void setBloodImpactX(float float1) {
		this.BloodImpactX = float1;
	}

	public float getBloodImpactY() {
		return this.BloodImpactY;
	}

	public void setBloodImpactY(float float1) {
		this.BloodImpactY = float1;
	}

	public float getBloodImpactZ() {
		return this.BloodImpactZ;
	}

	public void setBloodImpactZ(float float1) {
		this.BloodImpactZ = float1;
	}

	public IsoSprite getBloodSplat() {
		return this.bloodSplat;
	}

	public void setBloodSplat(IsoSprite sprite) {
		this.bloodSplat = sprite;
	}

	public boolean isbOnBed() {
		return this.bOnBed;
	}

	public void setbOnBed(boolean boolean1) {
		this.bOnBed = boolean1;
	}

	public Vector2 getMoveForwardVec() {
		return this.moveForwardVec;
	}

	public void setMoveForwardVec(Vector2 vector2) {
		this.moveForwardVec = vector2;
	}

	public boolean isPathing() {
		return this.pathing;
	}

	public void setPathing(boolean boolean1) {
		this.pathing = boolean1;
	}

	public Stack getLocalEnemyList() {
		return this.LocalEnemyList;
	}

	public void setLocalEnemyList(Stack stack) {
		this.LocalEnemyList = stack;
	}

	public Stack getEnemyList() {
		return this.EnemyList;
	}

	public void setEnemyList(Stack stack) {
		this.EnemyList = stack;
	}

	public ArrayList getTraits() {
		return this.Traits;
	}

	public void setTraits(ArrayList arrayList) {
		this.Traits = arrayList;
	}

	public Integer getMaxWeight() {
		return this.maxWeight;
	}

	public void setMaxWeight(Integer integer) {
		this.maxWeight = integer;
	}

	public int getMaxWeightBase() {
		return this.maxWeightBase;
	}

	public void setMaxWeightBase(int int1) {
		this.maxWeightBase = int1;
	}

	public float getSleepingTabletDelta() {
		return this.SleepingTabletDelta;
	}

	public void setSleepingTabletDelta(float float1) {
		this.SleepingTabletDelta = float1;
	}

	public float getBetaEffect() {
		return this.BetaEffect;
	}

	public void setBetaEffect(float float1) {
		this.BetaEffect = float1;
	}

	public float getDepressEffect() {
		return this.DepressEffect;
	}

	public void setDepressEffect(float float1) {
		this.DepressEffect = float1;
	}

	public float getSleepingTabletEffect() {
		return this.SleepingTabletEffect;
	}

	public void setSleepingTabletEffect(float float1) {
		this.SleepingTabletEffect = float1;
	}

	public float getBetaDelta() {
		return this.BetaDelta;
	}

	public void setBetaDelta(float float1) {
		this.BetaDelta = float1;
	}

	public float getDepressDelta() {
		return this.DepressDelta;
	}

	public void setDepressDelta(float float1) {
		this.DepressDelta = float1;
	}

	public float getPainEffect() {
		return this.PainEffect;
	}

	public void setPainEffect(float float1) {
		this.PainEffect = float1;
	}

	public float getPainDelta() {
		return this.PainDelta;
	}

	public void setPainDelta(float float1) {
		this.PainDelta = float1;
	}

	public boolean isbDoDefer() {
		return this.bDoDefer;
	}

	public void setbDoDefer(boolean boolean1) {
		this.bDoDefer = boolean1;
	}

	public IsoGameCharacter.Location getLastHeardSound() {
		return this.LastHeardSound;
	}

	public void setLastHeardSound(int int1, int int2, int int3) {
		this.LastHeardSound.x = int1;
		this.LastHeardSound.y = int2;
		this.LastHeardSound.z = int3;
	}

	public float getLrx() {
		return this.lrx;
	}

	public void setLrx(float float1) {
		this.lrx = float1;
	}

	public float getLry() {
		return this.lry;
	}

	public void setLry(float float1) {
		this.lry = float1;
	}

	public boolean isClimbing() {
		return this.bClimbing;
	}

	public void setbClimbing(boolean boolean1) {
		this.bClimbing = boolean1;
	}

	public boolean isLastCollidedW() {
		return this.lastCollidedW;
	}

	public void setLastCollidedW(boolean boolean1) {
		this.lastCollidedW = boolean1;
	}

	public boolean isLastCollidedN() {
		return this.lastCollidedN;
	}

	public void setLastCollidedN(boolean boolean1) {
		this.lastCollidedN = boolean1;
	}

	public int getFallTime() {
		return this.fallTime;
	}

	public void setFallTime(int int1) {
		this.fallTime = int1;
	}

	public float getLastFallSpeed() {
		return this.lastFallSpeed;
	}

	public void setLastFallSpeed(float float1) {
		this.lastFallSpeed = float1;
	}

	public boolean isbFalling() {
		return this.bFalling;
	}

	public void setbFalling(boolean boolean1) {
		this.bFalling = boolean1;
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
		int int1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
		if (int1 == 2) {
			return 0.8F;
		} else if (int1 == 3) {
			return 0.6F;
		} else if (int1 == 4) {
			return 0.4F;
		} else {
			return int1 >= 5 ? 0.4F : 1.0F;
		}
	}

	public float getWeldingSoundMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.MetalWelding);
		if (int1 == 2) {
			return 0.8F;
		} else if (int1 == 3) {
			return 0.6F;
		} else if (int1 == 4) {
			return 0.4F;
		} else {
			return int1 >= 5 ? 0.4F : 1.0F;
		}
	}

	public float getBarricadeTimeMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
		if (int1 == 1) {
			return 0.8F;
		} else if (int1 == 2) {
			return 0.7F;
		} else if (int1 == 3) {
			return 0.62F;
		} else if (int1 == 4) {
			return 0.56F;
		} else if (int1 == 5) {
			return 0.5F;
		} else if (int1 == 6) {
			return 0.42F;
		} else if (int1 == 7) {
			return 0.36F;
		} else if (int1 == 8) {
			return 0.3F;
		} else if (int1 == 9) {
			return 0.26F;
		} else {
			return int1 == 10 ? 0.2F : 0.7F;
		}
	}

	public float getMetalBarricadeStrengthMod() {
		switch (this.getPerkLevel(PerkFactory.Perks.MetalWelding)) {
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
			int int1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
			if (int1 == 2) {
				return 1.1F;
			} else if (int1 == 3) {
				return 1.14F;
			} else if (int1 == 4) {
				return 1.18F;
			} else if (int1 == 5) {
				return 1.22F;
			} else if (int1 == 6) {
				return 1.26F;
			} else if (int1 == 7) {
				return 1.3F;
			} else if (int1 == 8) {
				return 1.34F;
			} else if (int1 == 9) {
				return 1.4F;
			} else {
				return int1 == 10 ? 1.5F : 1.0F;
			}

		
		}
	}

	public float getBarricadeStrengthMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Woodwork);
		if (int1 == 2) {
			return 1.1F;
		} else if (int1 == 3) {
			return 1.14F;
		} else if (int1 == 4) {
			return 1.18F;
		} else if (int1 == 5) {
			return 1.22F;
		} else if (int1 == 6) {
			return 1.26F;
		} else if (int1 == 7) {
			return 1.3F;
		} else if (int1 == 8) {
			return 1.34F;
		} else if (int1 == 9) {
			return 1.4F;
		} else {
			return int1 == 10 ? 1.5F : 1.0F;
		}
	}

	public float getSneakSpotMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Sneak);
		if (int1 == 1) {
			return 0.9F;
		} else if (int1 == 2) {
			return 0.8F;
		} else if (int1 == 3) {
			return 0.75F;
		} else if (int1 == 4) {
			return 0.7F;
		} else if (int1 == 5) {
			return 0.65F;
		} else if (int1 == 6) {
			return 0.6F;
		} else if (int1 == 7) {
			return 0.55F;
		} else if (int1 == 8) {
			return 0.5F;
		} else if (int1 == 9) {
			return 0.45F;
		} else {
			return int1 == 10 ? 0.4F : 0.95F;
		}
	}

	public float getNimbleMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Nimble);
		if (int1 == 1) {
			return 1.1F;
		} else if (int1 == 2) {
			return 1.14F;
		} else if (int1 == 3) {
			return 1.18F;
		} else if (int1 == 4) {
			return 1.22F;
		} else if (int1 == 5) {
			return 1.26F;
		} else if (int1 == 6) {
			return 1.3F;
		} else if (int1 == 7) {
			return 1.34F;
		} else if (int1 == 8) {
			return 1.38F;
		} else if (int1 == 9) {
			return 1.42F;
		} else {
			return int1 == 10 ? 1.5F : 1.0F;
		}
	}

	public float getFatigueMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Fitness);
		if (int1 == 1) {
			return 0.95F;
		} else if (int1 == 2) {
			return 0.92F;
		} else if (int1 == 3) {
			return 0.89F;
		} else if (int1 == 4) {
			return 0.87F;
		} else if (int1 == 5) {
			return 0.85F;
		} else if (int1 == 6) {
			return 0.83F;
		} else if (int1 == 7) {
			return 0.81F;
		} else if (int1 == 8) {
			return 0.79F;
		} else if (int1 == 9) {
			return 0.77F;
		} else {
			return int1 == 10 ? 0.75F : 1.0F;
		}
	}

	public float getLightfootMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Lightfoot);
		if (int1 == 1) {
			return 0.9F;
		} else if (int1 == 2) {
			return 0.79F;
		} else if (int1 == 3) {
			return 0.71F;
		} else if (int1 == 4) {
			return 0.65F;
		} else if (int1 == 5) {
			return 0.59F;
		} else if (int1 == 6) {
			return 0.52F;
		} else if (int1 == 7) {
			return 0.45F;
		} else if (int1 == 8) {
			return 0.37F;
		} else if (int1 == 9) {
			return 0.3F;
		} else {
			return int1 == 10 ? 0.2F : 0.99F;
		}
	}

	public float getPacingMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Fitness);
		if (int1 == 1) {
			return 0.6F;
		} else if (int1 == 2) {
			return 0.5F;
		} else if (int1 == 3) {
			return 0.45F;
		} else if (int1 == 4) {
			return 0.4F;
		} else if (int1 == 5) {
			return 0.35F;
		} else if (int1 == 6) {
			return 0.3F;
		} else if (int1 == 7) {
			return 0.25F;
		} else if (int1 == 8) {
			return 0.2F;
		} else if (int1 == 9) {
			return 0.15F;
		} else {
			return int1 == 10 ? 0.1F : 0.65F;
		}
	}

	public float getHyperthermiaMod() {
		float float1 = 1.0F;
		if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) > 1) {
			float1 = 1.0F;
			if (this.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4) {
				float1 = 2.0F;
			}
		}

		return float1;
	}

	public float getHittingMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Strength);
		if (int1 == 1) {
			return 0.8F;
		} else if (int1 == 2) {
			return 0.85F;
		} else if (int1 == 3) {
			return 0.9F;
		} else if (int1 == 4) {
			return 0.95F;
		} else if (int1 == 5) {
			return 1.0F;
		} else if (int1 == 6) {
			return 1.05F;
		} else if (int1 == 7) {
			return 1.1F;
		} else if (int1 == 8) {
			return 1.15F;
		} else if (int1 == 9) {
			return 1.2F;
		} else {
			return int1 == 10 ? 1.25F : 0.75F;
		}
	}

	public float getShovingMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Strength);
		if (int1 == 1) {
			return 0.8F;
		} else if (int1 == 2) {
			return 0.85F;
		} else if (int1 == 3) {
			return 0.9F;
		} else if (int1 == 4) {
			return 0.95F;
		} else if (int1 == 5) {
			return 1.0F;
		} else if (int1 == 6) {
			return 1.05F;
		} else if (int1 == 7) {
			return 1.1F;
		} else if (int1 == 8) {
			return 1.15F;
		} else if (int1 == 9) {
			return 1.2F;
		} else {
			return int1 == 10 ? 1.25F : 0.75F;
		}
	}

	public float getRecoveryMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Fitness);
		float float1 = 0.0F;
		if (int1 == 0) {
			float1 = 0.7F;
		}

		if (int1 == 1) {
			float1 = 0.8F;
		}

		if (int1 == 2) {
			float1 = 0.9F;
		}

		if (int1 == 3) {
			float1 = 1.0F;
		}

		if (int1 == 4) {
			float1 = 1.1F;
		}

		if (int1 == 5) {
			float1 = 1.2F;
		}

		if (int1 == 6) {
			float1 = 1.3F;
		}

		if (int1 == 7) {
			float1 = 1.4F;
		}

		if (int1 == 8) {
			float1 = 1.5F;
		}

		if (int1 == 9) {
			float1 = 1.55F;
		}

		if (int1 == 10) {
			float1 = 1.6F;
		}

		if (this.HasTrait("Obese")) {
			float1 = (float)((double)float1 * 0.4);
		}

		if (this.HasTrait("Overweight")) {
			float1 = (float)((double)float1 * 0.7);
		}

		if (this.HasTrait("Very Underweight")) {
			float1 = (float)((double)float1 * 0.7);
		}

		if (this.HasTrait("Emaciated")) {
			float1 = (float)((double)float1 * 0.3);
		}

		if (this instanceof IsoPlayer) {
			if (((IsoPlayer)this).getNutrition().getLipids() < -1500.0F) {
				float1 = (float)((double)float1 * 0.2);
			} else if (((IsoPlayer)this).getNutrition().getLipids() < -1000.0F) {
				float1 = (float)((double)float1 * 0.5);
			}

			if (((IsoPlayer)this).getNutrition().getProteins() < -1500.0F) {
				float1 = (float)((double)float1 * 0.2);
			} else if (((IsoPlayer)this).getNutrition().getProteins() < -1000.0F) {
				float1 = (float)((double)float1 * 0.5);
			}
		}

		return float1;
	}

	public float getWeightMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Strength);
		if (int1 == 1) {
			return 0.9F;
		} else if (int1 == 2) {
			return 1.07F;
		} else if (int1 == 3) {
			return 1.24F;
		} else if (int1 == 4) {
			return 1.41F;
		} else if (int1 == 5) {
			return 1.58F;
		} else if (int1 == 6) {
			return 1.75F;
		} else if (int1 == 7) {
			return 1.92F;
		} else if (int1 == 8) {
			return 2.09F;
		} else if (int1 == 9) {
			return 2.26F;
		} else {
			return int1 == 10 ? 2.5F : 0.8F;
		}
	}

	public int getHitChancesMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
		if (int1 == 1) {
			return 1;
		} else if (int1 == 2) {
			return 1;
		} else if (int1 == 3) {
			return 2;
		} else if (int1 == 4) {
			return 2;
		} else if (int1 == 5) {
			return 3;
		} else if (int1 == 6) {
			return 3;
		} else if (int1 == 7) {
			return 4;
		} else if (int1 == 8) {
			return 4;
		} else if (int1 == 9) {
			return 5;
		} else {
			return int1 == 10 ? 5 : 1;
		}
	}

	public float getSprintMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Sprinting);
		if (int1 == 1) {
			return 1.1F;
		} else if (int1 == 2) {
			return 1.15F;
		} else if (int1 == 3) {
			return 1.2F;
		} else if (int1 == 4) {
			return 1.25F;
		} else if (int1 == 5) {
			return 1.3F;
		} else if (int1 == 6) {
			return 1.35F;
		} else if (int1 == 7) {
			return 1.4F;
		} else if (int1 == 8) {
			return 1.45F;
		} else if (int1 == 9) {
			return 1.5F;
		} else {
			return int1 == 10 ? 1.6F : 0.9F;
		}
	}

	public int getPerkLevel(PerkFactory.Perks perks) {
		for (int int1 = 0; int1 < this.PerkList.size(); ++int1) {
			IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int1);
			if (perkInfo.perkType == perks) {
				return perkInfo.level;
			}
		}

		return 0;
	}

	public void setPerkLevelDebug(PerkFactory.Perks perks, int int1) {
		for (int int2 = 0; int2 < this.PerkList.size(); ++int2) {
			IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int2);
			if (perkInfo.perkType == perks) {
				perkInfo.level = int1;
			}
		}
	}

	public void LoseLevel(PerkFactory.Perks perks) {
		for (int int1 = 0; int1 < this.PerkList.size(); ++int1) {
			IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int1);
			if (perkInfo.perkType == perks) {
				--perkInfo.level;
				if (perkInfo.level < 0) {
					perkInfo.level = 0;
				}

				LuaEventManager.triggerEvent("LevelPerk", this, perks, perkInfo.level, false);
				return;
			}
		}

		LuaEventManager.triggerEvent("LevelPerk", this, perks, 0, false);
	}

	public void LevelPerk(PerkFactory.Perks perks, boolean boolean1) {
		if (boolean1) {
			--this.NumberOfPerksToPick;
			if (this.NumberOfPerksToPick < 0) {
				this.NumberOfPerksToPick = 0;
			}
		}

		IsoGameCharacter.PerkInfo perkInfo;
		for (int int1 = 0; int1 < this.PerkList.size(); ++int1) {
			perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int1);
			if (perkInfo.perkType == perks) {
				++perkInfo.level;
				if (perkInfo.level > 10) {
					perkInfo.level = 10;
				}

				if (GameClient.bClient && this instanceof IsoPlayer) {
					GameClient.instance.sendSyncXp((IsoPlayer)this);
				}

				LuaEventManager.triggerEventGarbage("LevelPerk", this, perks, perkInfo.level, true);
				return;
			}
		}

		PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkMap.get(perks);
		perkInfo = new IsoGameCharacter.PerkInfo();
		perkInfo.perk = perk;
		perkInfo.perkType = perks;
		perkInfo.level = 1;
		this.PerkList.add(perkInfo);
		if (GameClient.bClient && this instanceof IsoPlayer) {
			GameClient.instance.sendSyncXp((IsoPlayer)this);
		}

		LuaEventManager.triggerEvent("LevelPerk", this, perks, perkInfo.level, true);
	}

	public void LevelPerk(PerkFactory.Perks perks) {
		this.LevelPerk(perks, true);
	}

	public void level0(PerkFactory.Perks perks) {
		for (int int1 = 0; int1 < this.PerkList.size(); ++int1) {
			IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int1);
			if (perkInfo.perkType == perks) {
				perkInfo.level = 0;
			}
		}
	}

	public void LevelUp() {
		if (GameClient.bClient && this instanceof IsoPlayer) {
			GameClient.instance.sendSyncXp((IsoPlayer)this);
		}

		++this.NumberOfPerksToPick;
	}

	public void GiveOrder(Order order, boolean boolean1) {
		if (boolean1) {
			this.Orders.clear();
		}

		if (order.character != this) {
			order.character = this;
		}

		this.Orders.push(order);
	}

	public void GivePersonalNeed(Order order) {
		if (order.character != this) {
			order.character = this;
		}

		if (order.isCritical()) {
			this.PersonalNeeds.push(order);
		} else {
			this.PersonalNeeds.insertElementAt(order, 0);
		}
	}

	public IsoGameCharacter.Location getLastKnownLocationOf(String string) {
		return this.LastKnownLocation.containsKey(string) ? (IsoGameCharacter.Location)this.LastKnownLocation.get(string) : null;
	}

	public void ReadLiterature(Literature literature) {
		Stats stats = this.stats;
		stats.stress += literature.getStressChange();
		this.getBodyDamage().JustReadSomething(literature);
		if (literature.getTeachedRecipes() != null) {
			for (int int1 = 0; int1 < literature.getTeachedRecipes().size(); ++int1) {
				if (!this.getKnownRecipes().contains(literature.getTeachedRecipes().get(int1))) {
					this.getKnownRecipes().add(literature.getTeachedRecipes().get(int1));
				}
			}
		}

		literature.Use();
	}

	public void OnDeath() {
	}

	public boolean IsArmed() {
		return this.inventory.getBestWeapon(this.descriptor) != null;
	}

	public void dripBloodFloor(float float1) {
		Integer integer = 32 + Rand.Next(8);
		this.DoFloorSplat(this.getCurrentSquare(), "BloodFloor_" + integer, Rand.Next(2) == 0, 0.0F, float1);
		this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(8));
	}

	public void splatBloodFloorBig(float float1) {
		if (this.getCurrentSquare() != null) {
			this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
		}
	}

	public void splatBloodFloor(float float1) {
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
		int int1 = this.LocalRelevantEnemyList.size();
		int1 += this.VeryCloseEnemyList.size() * 10;
		if (int1 > 20) {
			return 3;
		} else if (int1 > 10) {
			return 2;
		} else {
			return int1 > 0 ? 1 : 0;
		}
	}

	public boolean InBuildingWith(IsoGameCharacter gameCharacter) {
		if (this.getCurrentSquare() == null) {
			return false;
		} else if (gameCharacter.getCurrentSquare() == null) {
			return false;
		} else if (this.getCurrentSquare().getRoom() == null) {
			return false;
		} else if (gameCharacter.getCurrentSquare().getRoom() == null) {
			return false;
		} else {
			return gameCharacter.getCurrentSquare().getRoom().building == this.getCurrentSquare().getRoom().building;
		}
	}

	public boolean InRoomWith(IsoGameCharacter gameCharacter) {
		if (this.getCurrentSquare() == null) {
			return false;
		} else if (gameCharacter.getCurrentSquare() == null) {
			return false;
		} else if (this.getCurrentSquare().getRoom() == null) {
			return false;
		} else if (gameCharacter.getCurrentSquare().getRoom() == null) {
			return false;
		} else {
			return gameCharacter.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom();
		}
	}

	public boolean isDead() {
		return this.Health <= 0.0F || this.BodyDamage.getHealth() <= 0.0F;
	}

	public boolean isAlive() {
		return !this.isDead();
	}

	public boolean IsInBuilding(IsoBuilding building) {
		if (this.getCurrentSquare() == null) {
			return false;
		} else if (this.getCurrentSquare().getRoom() == null) {
			return false;
		} else {
			return this.getCurrentSquare().getRoom().building == building;
		}
	}

	public void Seen(Stack stack) {
		synchronized (this.LocalList) {
			this.LocalList.clear();
			this.LocalList.addAll(stack);
		}
	}

	public boolean CanSee(IsoMovingObject movingObject) {
		return LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), (int)movingObject.getX(), (int)movingObject.getY(), (int)movingObject.getZ(), false) != LosUtil.TestResults.Blocked;
	}

	public IsoGridSquare getLowDangerInVicinity(int int1, int int2) {
		float float1 = -1000000.0F;
		IsoGridSquare square = null;
		for (int int3 = 0; int3 < int1; ++int3) {
			float float2 = 0.0F;
			int int4 = Rand.Next(-int2, int2);
			int int5 = Rand.Next(-int2, int2);
			IsoGridSquare square2 = this.getCell().getGridSquare((int)this.getX() + int4, (int)this.getY() + int5, (int)this.getZ());
			if (square2 != null && square2.isFree(true)) {
				float float3 = (float)square2.getMovingObjects().size();
				if (square2.getE() != null) {
					float3 += (float)square2.getE().getMovingObjects().size();
				}

				if (square2.getS() != null) {
					float3 += (float)square2.getS().getMovingObjects().size();
				}

				if (square2.getW() != null) {
					float3 += (float)square2.getW().getMovingObjects().size();
				}

				if (square2.getN() != null) {
					float3 += (float)square2.getN().getMovingObjects().size();
				}

				float2 -= float3 * 1000.0F;
				if (float2 > float1) {
					float1 = float2;
					square = square2;
				}
			}
		}

		return square;
	}

	public void SetAnim(int int1) {
		if (!this.bUseParts) {
			this.sprite.CurrentAnim = (IsoAnim)this.sprite.AnimStack.get(int1);
		} else {
			this.legsSprite.CurrentAnim = (IsoAnim)this.legsSprite.AnimStack.get(int1);
			if (this.torsoSprite != null) {
				this.torsoSprite.CurrentAnim = (IsoAnim)this.torsoSprite.AnimStack.get(int1);
			}

			if (this.headSprite != null) {
				this.headSprite.CurrentAnim = (IsoAnim)this.headSprite.AnimStack.get(int1);
			}

			if (this.bottomsSprite != null) {
				this.bottomsSprite.CurrentAnim = (IsoAnim)this.bottomsSprite.AnimStack.get(int1);
			}

			if (this.hairSprite != null) {
				this.hairSprite.CurrentAnim = (IsoAnim)this.hairSprite.AnimStack.get(int1);
			}

			if (this.shoeSprite != null) {
				this.shoeSprite.CurrentAnim = (IsoAnim)this.shoeSprite.AnimStack.get(int1);
			}

			if (this.topSprite != null) {
				this.topSprite.CurrentAnim = (IsoAnim)this.topSprite.AnimStack.get(int1);
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

	public void Anger(int int1) {
		float float1 = 10.0F;
		if ((float)Rand.Next(100) < float1) {
			int1 *= 2;
		}

		int1 = (int)((float)int1 * (this.stats.getStress() + 1.0F));
		int1 = (int)((float)int1 * (this.BodyDamage.getUnhappynessLevel() / 100.0F + 1.0F));
		Stats stats = this.stats;
		stats.Anger += (float)int1 / 100.0F;
	}

	public boolean hasEquipped(String string) {
		if (string.contains(".")) {
			string = string.split("\\.")[1];
		}

		if (this.leftHandItem != null && this.leftHandItem.getType().equals(string)) {
			return true;
		} else {
			return this.rightHandItem != null && this.rightHandItem.getType().equals(string);
		}
	}

	public void setDir(IsoDirections directions) {
		this.dir = directions;
		this.getVectorFromDirection(this.angle);
	}

	public void SetClothing(Item.ClothingBodyLocation clothingBodyLocation, String string, String string2) {
		if (clothingBodyLocation == Item.ClothingBodyLocation.Top) {
			if (string == null) {
				this.topSprite = null;
			} else {
				this.topSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
				if (this.bFemale) {
					if (string2 != null && string2.contains("Shirt")) {
						string2 = string2.replace("Shirt", "Blouse");
					}

					string2 = "F_" + string2;
				}

				this.DoCharacterPart(string2, this.topSprite);
				if (this.ClothingItem_Torso != null) {
					this.topSprite.TintMod.r = this.descriptor.topColor.r;
					this.topSprite.TintMod.g = this.descriptor.topColor.g;
					this.topSprite.TintMod.b = this.descriptor.topColor.b;
					this.topSprite.TintMod.desaturate(0.5F);
				}
			}
		}

		if (clothingBodyLocation == Item.ClothingBodyLocation.Bottoms) {
			if (string == null) {
				this.bottomsSprite = null;
			} else {
				this.bottomsSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
				if (this.bFemale) {
					string2 = "F_" + string2;
				}

				this.DoCharacterPart(string2, this.bottomsSprite);
				if (this.ClothingItem_Legs != null) {
					this.bottomsSprite.TintMod.r = this.descriptor.trouserColor.r;
					this.bottomsSprite.TintMod.g = this.descriptor.trouserColor.g;
					this.bottomsSprite.TintMod.b = this.descriptor.trouserColor.b;
					this.bottomsSprite.TintMod.desaturate(0.5F);
				}
			}
		}

		if (clothingBodyLocation == Item.ClothingBodyLocation.Shoes) {
			if (string == null) {
				this.shoeSprite = null;
			} else {
				this.shoeSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
				if (this.bFemale) {
					string2 = "F_" + string2;
				}

				this.DoCharacterPart(string2, this.shoeSprite);
			}
		}
	}

	public void Callout() {
		String string = "";
		if (Core.getInstance().getGameMode().equals("Tutorial")) {
			string = Translator.getText("IGUI_PlayerText_CalloutTutorial");
		} else {
			switch (Rand.Next(3)) {
			case 0: 
				string = Translator.getText("IGUI_PlayerText_Callout1");
				break;
			
			case 1: 
				string = Translator.getText("IGUI_PlayerText_Callout2");
				break;
			
			case 2: 
				string = Translator.getText("IGUI_PlayerText_Callout3");
			
			}
		}

		ChatManager.getInstance().sendMessageToChat(((IsoPlayer)this).getUsername(), ChatType.say, string);
		this.callOut = true;
	}

	public void Kill(IsoGameCharacter gameCharacter) {
		this.Health = -1.0F;
		this.DoDeath((HandWeapon)null, gameCharacter);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.getVectorFromDirection(this.angle);
		if (byteBuffer.get() == 1) {
			this.descriptor = new SurvivorDesc(true);
			this.descriptor.load(byteBuffer, int1, this);
			this.bFemale = this.descriptor.isFemale();
		}

		ArrayList arrayList = this.inventory.load(byteBuffer, int1, false);
		this.savedInventoryItems.clear();
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			this.savedInventoryItems.add(arrayList.get(int2));
		}

		this.Asleep = byteBuffer.get() == 1;
		this.ForceWakeUpTime = byteBuffer.getFloat();
		int int3;
		if (!(this instanceof IsoZombie)) {
			this.NumberOfPerksToPick = byteBuffer.getInt();
			this.stats.load(byteBuffer, int1);
			this.BodyDamage.load(byteBuffer, int1);
			this.xp.load(byteBuffer, int1);
			if (int1 >= 26) {
				ArrayList arrayList2 = this.inventory.IncludingObsoleteItems;
				int3 = byteBuffer.getInt();
				if (int3 >= 0 && int3 < arrayList2.size()) {
					this.leftHandItem = (InventoryItem)arrayList2.get(int3);
				}

				int3 = byteBuffer.getInt();
				if (int3 >= 0 && int3 < arrayList2.size()) {
					this.rightHandItem = (InventoryItem)arrayList2.get(int3);
				}
			} else {
				if (byteBuffer.get() == 1) {
					this.leftHandItem = this.inventory.getBestCondition(GameWindow.ReadString(byteBuffer));
				}

				if (byteBuffer.get() == 1) {
					this.rightHandItem = this.inventory.getBestCondition(GameWindow.ReadString(byteBuffer));
				}
			}

			this.setEquipParent((InventoryItem)null, this.leftHandItem);
			this.setEquipParent((InventoryItem)null, this.rightHandItem);
		}

		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			this.SetOnFire();
		}

		if (int1 >= 40) {
			this.DepressEffect = byteBuffer.getFloat();
			this.DepressFirstTakeTime = byteBuffer.getFloat();
			this.BetaEffect = byteBuffer.getFloat();
			this.BetaDelta = byteBuffer.getFloat();
			this.PainEffect = byteBuffer.getFloat();
			this.PainDelta = byteBuffer.getFloat();
			this.SleepingTabletEffect = byteBuffer.getFloat();
			this.SleepingTabletDelta = byteBuffer.getFloat();
		} else {
			this.DepressEffect = (float)byteBuffer.getInt();
			this.DepressFirstTakeTime = (float)byteBuffer.getInt();
		}

		int3 = byteBuffer.getInt();
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			IsoGameCharacter.ReadBook readBook = new IsoGameCharacter.ReadBook();
			readBook.fullType = GameWindow.ReadString(byteBuffer);
			readBook.alreadyReadPages = byteBuffer.getInt();
			this.ReadBooks.add(readBook);
		}

		if (int1 >= 44) {
			this.reduceInfectionPower = byteBuffer.getFloat();
		}

		if (int1 >= 62) {
			int4 = byteBuffer.getInt();
			for (int int5 = 0; int5 < int4; ++int5) {
				this.knownRecipes.add(GameWindow.ReadString(byteBuffer));
			}
		}

		if (int1 >= 90) {
			this.lastHourSleeped = byteBuffer.getInt();
		}

		if (int1 >= 97) {
			this.timeSinceLastSmoke = byteBuffer.getFloat();
		}

		if (int1 >= 136) {
			this.setUnlimitedCarry(byteBuffer.get() == 1);
			this.setBuildCheat(byteBuffer.get() == 1);
			this.setHealthCheat(byteBuffer.get() == 1);
			this.setMechanicsCheat(byteBuffer.get() == 1);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		if (this.descriptor == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.descriptor.save(byteBuffer);
		}

		ArrayList arrayList = this.inventory.save(byteBuffer, false, this);
		this.savedInventoryItems.clear();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			this.savedInventoryItems.add(arrayList.get(int1));
		}

		byteBuffer.put((byte)(this.Asleep ? 1 : 0));
		byteBuffer.putFloat(this.ForceWakeUpTime);
		if (!(this instanceof IsoZombie)) {
			byteBuffer.putInt(this.NumberOfPerksToPick);
			this.stats.save(byteBuffer);
			this.BodyDamage.save(byteBuffer);
			this.xp.save(byteBuffer);
			if (this.leftHandItem != null) {
				byteBuffer.putInt(this.inventory.getItems().indexOf(this.leftHandItem));
			} else {
				byteBuffer.putInt(-1);
			}

			if (this.rightHandItem != null) {
				byteBuffer.putInt(this.inventory.getItems().indexOf(this.rightHandItem));
			} else {
				byteBuffer.putInt(-1);
			}
		}

		byteBuffer.put((byte)(this.OnFire ? 1 : 0));
		byteBuffer.putFloat(this.DepressEffect);
		byteBuffer.putFloat(this.DepressFirstTakeTime);
		byteBuffer.putFloat(this.BetaEffect);
		byteBuffer.putFloat(this.BetaDelta);
		byteBuffer.putFloat(this.PainEffect);
		byteBuffer.putFloat(this.PainDelta);
		byteBuffer.putFloat(this.SleepingTabletEffect);
		byteBuffer.putFloat(this.SleepingTabletDelta);
		byteBuffer.putInt(this.ReadBooks.size());
		Iterator iterator = this.ReadBooks.iterator();
		while (iterator.hasNext()) {
			IsoGameCharacter.ReadBook readBook = (IsoGameCharacter.ReadBook)iterator.next();
			GameWindow.WriteString(byteBuffer, readBook.fullType);
			byteBuffer.putInt(readBook.alreadyReadPages);
		}

		byteBuffer.putFloat(this.reduceInfectionPower);
		byteBuffer.putInt(this.knownRecipes.size());
		iterator = this.knownRecipes.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
		}

		byteBuffer.putInt(this.lastHourSleeped);
		byteBuffer.putFloat(this.timeSinceLastSmoke);
		byteBuffer.put((byte)(this.isUnlimitedCarry() ? 1 : 0));
		byteBuffer.put((byte)(this.isBuildCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isHealthCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isMechanicsCheat() ? 1 : 0));
	}

	public ChatElement getChatElement() {
		return this.chatElement;
	}

	public void StartAction(BaseAction baseAction) {
		this.CharacterActions.clear();
		this.CharacterActions.push(baseAction);
		if (baseAction.valid()) {
			baseAction.start();
		}
	}

	public void QueueAction(BaseAction baseAction) {
	}

	public void StopAllActionQueue() {
		if (!this.CharacterActions.isEmpty()) {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			baseAction.stop();
			this.CharacterActions.clear();
			if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
				UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
			}
		}
	}

	public void StopAllActionQueueRunning() {
		if (!this.CharacterActions.isEmpty()) {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			if (baseAction.StopOnRun) {
				baseAction.stop();
				this.CharacterActions.clear();
				if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
					UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
				}
			}
		}
	}

	public void StopAllActionQueueAiming() {
		if (this.CharacterActions.size() != 0) {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			if (baseAction.StopOnAim) {
				baseAction.stop();
				this.CharacterActions.clear();
				if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
					UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
				}
			}
		}
	}

	public void StopAllActionQueueWalking() {
		if (this.CharacterActions.size() != 0) {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			if (baseAction.StopOnWalk) {
				baseAction.stop();
				this.CharacterActions.clear();
				if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
					UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
				}
			}
		}
	}

	public IsoGameCharacter(IsoCell cell, float float1, float float2, float float3) {
		super(cell, false);
		if (this.emitter == null && !(this instanceof IsoSurvivor)) {
			this.emitter = (BaseCharacterSoundEmitter)(!Core.SoundDisabled && !GameServer.bServer ? new CharacterSoundEmitter(this) : new DummyCharacterSoundEmitter(this));
		}

		this.pvpTexture = Texture.getSharedTexture("media/ui/Skull.png");
		if (float1 != 0.0F || float2 != 0.0F || float3 != 0.0F) {
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
		this.x = float1 + 0.5F;
		this.y = float2 + 0.5F;
		this.z = float3;
		this.scriptnx = this.lx = this.nx = float1;
		this.scriptny = this.ly = this.ny = float2;
		if (cell != null) {
			this.current = this.getCell().getGridSquare((int)float1, (int)float2, (int)float3);
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

	public void SleepingTablet(float float1) {
		this.SleepingTabletEffect = 6600.0F;
		this.SleepingTabletDelta += float1;
	}

	public void BetaBlockers(float float1) {
		this.BetaEffect = 6600.0F;
		this.BetaDelta += float1;
	}

	public void BetaAntiDepress(float float1) {
		if (this.DepressEffect == 0.0F) {
			this.DepressFirstTakeTime = 10000.0F;
		}

		this.DepressEffect = 6600.0F;
		this.DepressDelta += float1;
	}

	public void PainMeds(float float1) {
		this.PainEffect = 300.0F;
		this.PainDelta += float1;
	}

	public void DoCharacterPart(String string, IsoSprite sprite) {
		sprite.LoadFrames(string, szRun, 11);
		sprite.LoadFrames(string, szRun_Weapon2, 11);
		sprite.LoadFrames(string, szIdle_Weapon2, 6);
		sprite.LoadFrames(string, szWalk, 11);
		sprite.LoadFramesReverseAltName(string, szWalk, szWalk_R, 11);
		sprite.LoadFrames(string, szStrafe_Aim_Bat, 11);
		sprite.LoadFrames(string, szStrafe_Aim_Handgun, 11);
		sprite.LoadFrames(string, szStrafe_Aim_Rifle, 11);
		sprite.LoadFrames(string, szStrafe_Aim_Stab, 11);
		sprite.LoadFrames(string, szAttack_Jaw_Stab, 30);
		sprite.LoadFrames(string, szStrafe, 11);
		sprite.LoadFrames(string, szWalk_Aim_Stab, 11);
		sprite.LoadFrames(string, szWalk_Aim_Bat, 11);
		sprite.LoadFrames(string, szWalk_Aim_Handgun, 11);
		sprite.LoadFrames(string, szWalk_Aim_Rifle, 11);
		sprite.LoadFramesReverseAltName(string, szWalk_Aim_Rifle, szWalk_Aim_Rifle_R, 11);
		sprite.LoadFramesReverseAltName(string, szWalk_Aim_Stab, szWalk_Aim_Stab_R, 11);
		sprite.LoadFramesReverseAltName(string, szStrafe, szStrafe_R, 11);
		sprite.LoadFramesReverseAltName(string, szWalk_Aim_Bat, szWalk_Aim_Bat_R, 11);
		sprite.LoadFramesReverseAltName(string, szStrafe_Aim_Rifle, szStrafe_Aim_Rifle_R, 11);
		sprite.LoadFramesReverseAltName(string, szStrafe_Aim_Bat, szStrafe_Aim_Bat_R, 11);
		sprite.LoadFramesReverseAltName(string, szWalk_Aim_Handgun, szWalk_Aim_Handgun_R, 11);
		sprite.LoadFramesReverseAltName(string, szStrafe_Aim_Handgun, szStrafe_Aim_Handgun_R, 11);
		sprite.LoadFramesReverseAltName(string, szStrafe_Aim_Stab, szStrafe_Aim_Stab_R, 11);
		sprite.LoadFrames(string, szZombieDeath, 14);
		sprite.LoadFrames(string, szAttack_Bat, 14);
		sprite.LoadFrames(string, szAttack_Sledgehammer, 14);
		sprite.LoadFrames(string, szAttack_Handgun, 6);
		sprite.LoadFrames(string, szAttack_Rifle, 5);
		sprite.LoadFrames(string, szAttack_Stab, 8);
		sprite.LoadFrames(string, szAttack_Shove, 11);
		sprite.LoadFrames(string, szAttack_Floor_Bat, 15);
		sprite.LoadFrames(string, szAttack_Floor_Handgun, 6);
		sprite.LoadFrames(string, szAttack_Floor_Rifle, 6);
		sprite.LoadFrames(string, szAttack_Floor_Stab, 16);
		sprite.LoadFrames(string, szAttack_Floor_Stamp, 16);
		sprite.LoadFrames(string, szClimb_WindowA, 10);
		sprite.LoadFrames(string, szClimb_WindowB, 10);
		sprite.LoadFrames(string, szWindowOpenIn, 5);
		sprite.LoadFrames(string, szWindowOpenStruggle, 10);
		sprite.LoadFrames(string, szWindowOpenSuccess, 10);
		sprite.LoadFrames(string, szWindowSmash, 10);
		sprite.LoadFrames(string, szSatChairIn, 9);
		sprite.LoadFrames(string, szSatChairOut, 9);
		sprite.LoadFrames(string, szSatChairIdle, 1);
		sprite.LoadFrames(string, szClimb_Rope, 11);
		sprite.LoadFramesReverseAltName(string, szClimb_Rope, szClimbDown_Rope, 11);
		sprite.LoadFrames(string, szZombieGetUp, 15);
		sprite.LoadFrames(string, szIdle, 6);
	}

	public void DoZombiePart(String string, IsoSprite sprite) {
		sprite.LoadFrames(string, "ZombieDoor", 11);
		sprite.LoadFrames(string, "ZombieBite", 20);
		sprite.LoadFrames(string, "ZombieDeath", 14);
		sprite.LoadFrames(string, "ZombieStaggerBack", 10);
		sprite.LoadFrames(string, "ZombieGetUp", 15);
		sprite.LoadFrames(string, "Climb_WindowA", 10);
		sprite.LoadFrames(string, "Climb_WindowB", 10);
		sprite.LoadFrames(string, "ZombieCrawl", 11);
		sprite.LoadFrames(string, "Zombie_CrawlLunge", 20);
		sprite.LoadFrames(string, "ZombieDeadToCrawl", 20);
		sprite.LoadFrames(string, "Zombie_CrawlTurnL", 11);
		sprite.LoadFrames(string, "Zombie_CrawlTurnR", 11);
		sprite.LoadFrames(string, "ZombieIdle", 20);
		sprite.LoadFrames(string, "Run", 11);
		sprite.LoadFrames(string, "ZombieWalk1", 11);
		sprite.LoadFrames(string, "ZombieWalk2", 11);
		sprite.LoadFrames(string, "ZombieWalk3", 11);
	}

	public void DoZombiePart(String string, String string2, IsoSprite sprite) {
		sprite.LoadFrames(string, "ZombieDoor", 11);
		sprite.LoadFrames(string, "ZombieBite", 20);
		sprite.LoadFrames(string, "ZombieDeath", 14);
		sprite.LoadFrames(string, "ZombieStaggerBack", 10);
		sprite.LoadFrames(string, "ZombieGetUp", 15);
		sprite.LoadFrames(string, "Climb_WindowA", 10);
		sprite.LoadFrames(string, "Climb_WindowB", 10);
		sprite.LoadFrames(string, "ZombieWalk1", 11);
		sprite.LoadFrames(string, "ZombieWalk2", 11);
		sprite.LoadFrames(string, "ZombieWalk3", 11);
		sprite.LoadFrames(string, "ZombieIdle", 20);
		sprite.LoadFrames(string, "ZombieCrawl", 11);
		sprite.LoadFrames(string, "Zombie_CrawlLunge", 20);
		sprite.LoadFrames(string, "ZombieDeadToCrawl", 20);
		sprite.LoadFrames(string, "Zombie_CrawlTurnL", 11);
		sprite.LoadFrames(string, "Zombie_CrawlTurnR", 11);
		sprite.LoadFrames(string, "Run", 11);
	}

	public void initSpritePartsEmpty() {
		this.InitSpriteParts(this.descriptor, this.descriptor.legs, this.descriptor.torso, this.descriptor.head, this.descriptor.top, this.descriptor.bottoms, this.descriptor.shoes, this.descriptor.skinpal, this.descriptor.toppal, this.descriptor.bottomspal, this.descriptor.shoespal, this.descriptor.hair, this.descriptor.extra);
	}

	public void InitSpriteParts(SurvivorDesc survivorDesc, String string, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11, ArrayList arrayList) {
		this.sprite.AnimMap.clear();
		this.sprite.AnimStack.clear();
		this.sprite.CurrentAnim = null;
		this.legsSprite = this.sprite;
		this.legsSprite.name = string2;
		if (DropShadow == null) {
			DropShadow = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			DropShadow.LoadFramesPageSimple("dropshadow", "dropshadow", "dropshadow", "dropshadow");
		}

		this.DoCharacterPart(string2, this.legsSprite);
		if (string8 != null && !string8.isEmpty()) {
			if (this.bFemale) {
				string8 = string8.replace("Shirt", "Blouse");
				string8 = "F_" + string8;
			}

			this.topSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.DoCharacterPart(string8, this.topSprite);
		}

		if (this.ClothingItem_Torso != null) {
			this.topSprite.TintMod.r = survivorDesc.topColor.r;
			this.topSprite.TintMod.g = survivorDesc.topColor.g;
			this.topSprite.TintMod.b = survivorDesc.topColor.b;
			this.topSprite.TintMod.desaturate(0.5F);
		}

		if (string9 != null && !string9.isEmpty()) {
			if (this.bFemale) {
				string9 = "F_" + string9;
			}

			this.bottomsSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.DoCharacterPart(string9, this.bottomsSprite);
		}

		if (this.ClothingItem_Legs != null) {
			this.bottomsSprite.TintMod.r = survivorDesc.trouserColor.r;
			this.bottomsSprite.TintMod.g = survivorDesc.trouserColor.g;
			this.bottomsSprite.TintMod.b = survivorDesc.trouserColor.b;
			this.bottomsSprite.TintMod.desaturate(0.5F);
		}

		this.hairSprite = null;
		if (!"none".equals(string11)) {
			string11 = string11.replace("Black", "White");
			string11 = string11.replace("Blonde", "White");
			string11 = string11.replace("Brown", "White");
			string11 = string11.replace("Red", "White");
			survivorDesc.hair = string11;
			this.hairSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.DoCharacterPart(string11, this.hairSprite);
			this.hairSprite.TintMod.r = survivorDesc.hairColor.r;
			this.hairSprite.TintMod.g = survivorDesc.hairColor.g;
			this.hairSprite.TintMod.b = survivorDesc.hairColor.b;
			this.hairSprite.TintMod.desaturate(0.5F);
		}

		this.extraSprites.clear();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoSprite sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			this.DoCharacterPart((String)arrayList.get(int1), sprite);
			this.extraSprites.add(sprite);
			sprite.TintMod.r = survivorDesc.hairColor.r;
			sprite.TintMod.g = survivorDesc.hairColor.g;
			sprite.TintMod.b = survivorDesc.hairColor.b;
			sprite.TintMod.desaturate(0.5F);
		}

		this.bUseParts = true;
	}

	public void InitSpritePartsZombie() {
		SurvivorDesc survivorDesc = this.descriptor;
		this.InitSpritePartsZombie(((IsoZombie)this).SpriteName, survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
	}

	public void InitSpritePartsZombie(String string, SurvivorDesc survivorDesc, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11, String string12, ArrayList arrayList) {
		this.sprite.AnimMap.clear();
		this.sprite.AnimStack.clear();
		this.sprite.CurrentAnim = null;
		this.legsSprite = this.sprite;
		this.legsSprite.name = string3;
		if (this instanceof IsoZombie) {
			((IsoZombie)this).ZombieID = Rand.Next(10000);
		}

		if (IsoSprite.HasCache(string)) {
			this.legsSprite.LoadCache(string);
		} else {
			this.DoZombiePart(string, this.descriptor.torso, this.legsSprite);
			this.legsSprite.CacheAnims(string);
		}

		this.legsSprite.TintMod.r = survivorDesc.skinColor.r;
		this.legsSprite.TintMod.g = survivorDesc.skinColor.g;
		this.legsSprite.TintMod.b = survivorDesc.skinColor.b;
		this.legsSprite.TintMod.desaturate(0.5F);
		if (this.bFemale && string9 != null) {
			if (string9 != null && string9.contains("Shirt")) {
				string9 = string9.replace("Shirt", "Blouse");
			}

			string9 = "F_" + string9;
		}

		this.topSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.topSprite.TintMod.r = survivorDesc.topColor.r;
		this.topSprite.TintMod.g = survivorDesc.topColor.g;
		this.topSprite.TintMod.b = survivorDesc.topColor.b;
		this.topSprite.TintMod.desaturate(0.6F);
		if (IsoSprite.HasCache(string9 + "z")) {
			this.topSprite.LoadCache(string9 + "z");
		} else {
			this.DoZombiePart(string9, this.topSprite);
			this.topSprite.CacheAnims(string9 + "z");
		}

		this.extraSprites.clear();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoSprite sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			if (IsoSprite.HasCache((String)arrayList.get(int1) + "z")) {
				sprite.LoadCache((String)arrayList.get(int1) + "z");
			} else {
				this.DoZombiePart((String)arrayList.get(int1), sprite);
				sprite.CacheAnims((String)arrayList.get(int1) + "z");
			}

			sprite.TintMod.r = survivorDesc.hairColor.r;
			sprite.TintMod.g = survivorDesc.hairColor.g;
			sprite.TintMod.b = survivorDesc.hairColor.b;
			sprite.TintMod.desaturate(0.6F);
			this.extraSprites.add(sprite);
		}

		this.bottomsSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		this.bottomsSprite.TintMod.r = survivorDesc.trouserColor.r;
		this.bottomsSprite.TintMod.g = survivorDesc.trouserColor.g;
		this.bottomsSprite.TintMod.b = survivorDesc.trouserColor.b;
		this.bottomsSprite.TintMod.desaturate(0.6F);
		if (this.bFemale) {
			string10 = "F_" + string10;
		}

		if (IsoSprite.HasCache(string10 + "z")) {
			this.bottomsSprite.LoadCache(string10 + "z");
		} else {
			this.DoZombiePart(string10, this.bottomsSprite);
			this.bottomsSprite.CacheAnims(string10 + "z");
		}

		this.hairSprite = null;
		if (!"none".equals(string12)) {
			this.hairSprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
			if (IsoSprite.HasCache(string12 + "z")) {
				this.hairSprite.LoadCache(string12 + "z");
			} else {
				this.DoZombiePart(string12, this.hairSprite);
				this.hairSprite.CacheAnims(string12 + "z");
			}

			this.hairSprite.TintMod.r = survivorDesc.hairColor.r;
			this.hairSprite.TintMod.g = survivorDesc.hairColor.g;
			this.hairSprite.TintMod.b = survivorDesc.hairColor.b;
			this.hairSprite.TintMod.desaturate(0.6F);
		}

		this.bUseParts = true;
	}

	public boolean HasTrait(String string) {
		return this.Traits.contains(string);
	}

	public void ApplyInBedOffset(boolean boolean1) {
		if (boolean1) {
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

	public void Dressup(SurvivorDesc survivorDesc) {
		InventoryItem inventoryItem = null;
		InventoryItem inventoryItem2 = null;
		InventoryItem inventoryItem3 = null;
		boolean boolean1 = this instanceof IsoZombie;
		if (survivorDesc.bottomspal != null && !survivorDesc.bottomspal.isEmpty()) {
			inventoryItem = this.inventory.AddItem((InventoryItem)Clothing.CreateFromSprite(survivorDesc.bottomspal.replace("_White", "")));
			inventoryItem.col = new Color(survivorDesc.trouserColor.r, survivorDesc.trouserColor.g, survivorDesc.trouserColor.b);
			if (inventoryItem instanceof Clothing && boolean1 && SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
				((Clothing)inventoryItem).setDirtyness((float)Rand.Next(0, 100));
				((Clothing)inventoryItem).setBloodLevel((float)Rand.Next(20, 100));
				if (((Clothing)inventoryItem).isBloody() && Rand.Next(0, 100) > 95) {
					((Clothing)inventoryItem).setInfected(true);
				}
			}
		}

		if (survivorDesc.toppal != null && !"".equals(survivorDesc.toppal)) {
			inventoryItem2 = this.inventory.AddItem((InventoryItem)Clothing.CreateFromSprite(survivorDesc.toppal.replace("_White", "")));
			inventoryItem2.col = new Color(survivorDesc.topColor.r, survivorDesc.topColor.g, survivorDesc.topColor.b);
			if (inventoryItem2 instanceof Clothing && boolean1 && SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
				((Clothing)inventoryItem2).setDirtyness((float)Rand.Next(0, 100));
				((Clothing)inventoryItem2).setBloodLevel((float)Rand.Next(20, 100));
				if (((Clothing)inventoryItem2).isBloody() && Rand.Next(0, 100) > 95) {
					((Clothing)inventoryItem2).setInfected(true);
				}
			}
		}

		if (survivorDesc.shoes != null && !survivorDesc.shoes.isEmpty()) {
			inventoryItem3 = this.inventory.AddItem((InventoryItem)Clothing.CreateFromSprite("Shoes"));
			inventoryItem3.col = new Color(64, 64, 64);
		}

		if (inventoryItem3 != null) {
			this.ClothingItem_Feet = inventoryItem3;
		}

		if (inventoryItem2 != null) {
			this.ClothingItem_Torso = inventoryItem2;
		}

		if (inventoryItem != null) {
			this.ClothingItem_Legs = inventoryItem;
		}
	}

	public void Dressup(InventoryItem inventoryItem, InventoryItem inventoryItem2, InventoryItem inventoryItem3) {
		if (inventoryItem3 != null) {
			this.ClothingItem_Feet = inventoryItem3;
		}

		if (inventoryItem2 != null) {
			this.ClothingItem_Torso = inventoryItem2;
		}

		if (inventoryItem != null) {
			this.ClothingItem_Legs = inventoryItem;
		}
	}

	public void PlayAnimNoReset(String string) {
		if (string != null) {
			if ("Run".equals(string)) {
				this.def.setFrameSpeedPerFrame(0.24F);
			} else if (string.contains("Walk")) {
				this.def.setFrameSpeedPerFrame(0.2F);
			} else if (string.contains("Strafe")) {
				this.def.setFrameSpeedPerFrame(0.05F);
			}

			if (!this.sprite.CurrentAnim.name.equals("Die")) {
				if (!this.bUseParts) {
					this.sprite.PlayAnimNoReset(string);
				} else {
					this.legsSprite.PlayAnimNoReset(string);
					if (this.torsoSprite != null) {
						this.torsoSprite.PlayAnimNoReset(string);
					}

					if (this.headSprite != null) {
						this.headSprite.PlayAnimNoReset(string);
					}

					if (this.bottomsSprite != null) {
						this.bottomsSprite.PlayAnimNoReset(string);
					}

					if (this.shoeSprite != null) {
						this.shoeSprite.PlayAnimNoReset(string);
					}

					if (this.topSprite != null) {
						this.topSprite.PlayAnimNoReset(string);
					}

					if (this.hairSprite != null) {
						this.hairSprite.PlayAnimNoReset(string);
					}

					for (int int1 = 0; int1 < this.extraSprites.size(); ++int1) {
						((IsoSprite)this.extraSprites.get(int1)).PlayAnimNoReset(string);
					}
				}
			}
		}
	}

	public void PlayAnim(String string) {
		if (string != null) {
			if (!"Kate".equals(this.getScriptName())) {
				this.def.Looped = true;
				this.def.Finished = false;
				if (string.contains("Run")) {
					this.def.setFrameSpeedPerFrame(0.34F);
				} else if ("Walk".equals(string)) {
					this.def.setFrameSpeedPerFrame(0.25F);
				}

				if (this.sprite != null && this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.name.equals("Die")) {
					if (!this.bUseParts) {
						this.sprite.PlayAnim(string);
					} else {
						IsoAnim anim = this.legsSprite.CurrentAnim;
						this.legsSprite.PlayAnim(string);
						if (this.torsoSprite != null) {
							this.torsoSprite.PlayAnim(string);
						}

						if (this.headSprite != null) {
							this.headSprite.PlayAnim(string);
						}

						if (this.bottomsSprite != null) {
							this.bottomsSprite.PlayAnim(string);
						}

						if (this.hairSprite != null) {
							this.hairSprite.PlayAnim(string);
						}

						if (this.shoeSprite != null) {
							this.shoeSprite.PlayAnim(string);
						}

						if (this.topSprite != null) {
							this.topSprite.PlayAnim(string);
						}

						for (int int1 = 0; int1 < this.extraSprites.size(); ++int1) {
							((IsoSprite)this.extraSprites.get(int1)).PlayAnim(string);
						}

						if (anim != this.legsSprite.CurrentAnim && (float)this.legsSprite.CurrentAnim.Frames.size() <= this.def.Frame) {
							this.def.Frame = 0.0F;
						}

						this.EnforceAnims();
					}
				}
			}
		}
	}

	public void PlayAnimWithSpeed(String string, float float1) {
		if (string != null) {
			this.PlayAnim(string);
			this.def.setFrameSpeedPerFrame(float1);
		}
	}

	public void PlayAnimUnlooped(String string) {
		if (string != null) {
			if (!this.sprite.CurrentAnim.name.equals("Die")) {
				if (!this.bUseParts) {
					this.sprite.PlayAnimUnlooped(string);
					this.def.Looped = false;
				} else {
					IsoAnim anim = this.legsSprite.CurrentAnim;
					this.def.Looped = false;
					if (anim.name.equals(string) && (this.def.Finished || this.def.Frame != 0.0F)) {
						return;
					}

					if (this instanceof IsoZombie && GameClient.bClient) {
						boolean boolean1 = false;
					}

					this.legsSprite.PlayAnimUnlooped(string);
					if (this.torsoSprite != null) {
						this.torsoSprite.PlayAnimUnlooped(string);
					}

					if (this.headSprite != null) {
						this.headSprite.PlayAnimUnlooped(string);
					}

					if (this.bottomsSprite != null) {
						this.bottomsSprite.PlayAnimUnlooped(string);
					}

					if (this.hairSprite != null) {
						this.hairSprite.PlayAnimUnlooped(string);
					}

					if (this.shoeSprite != null) {
						this.shoeSprite.PlayAnimUnlooped(string);
					}

					if (this.topSprite != null) {
						this.topSprite.PlayAnimUnlooped(string);
					}

					for (int int1 = 0; int1 < this.extraSprites.size(); ++int1) {
						((IsoSprite)this.extraSprites.get(int1)).PlayAnimUnlooped(string);
					}

					this.def.Frame = 0.0F;
					this.def.Finished = false;
				}
			}
		}
	}

	public void PlayAnimFrame(String string, int int1) {
		if (string != null) {
			if (this instanceof IsoLivingCharacter) {
				if (!this.bUseParts) {
					this.sprite.PlayAnim(string);
					this.def.Frame = (float)((short)int1);
					this.def.Finished = true;
				} else {
					this.legsSprite.PlayAnimUnlooped(string);
					this.def.Finished = true;
					if (this.torsoSprite != null) {
						this.torsoSprite.PlayAnimUnlooped(string);
					}

					if (this.torsoSprite != null) {
						this.def.Finished = true;
					}

					if (this.headSprite != null) {
						this.headSprite.PlayAnimUnlooped(string);
						this.def.Finished = true;
					}

					if (this.bottomsSprite != null) {
						this.bottomsSprite.PlayAnimUnlooped(string);
						this.def.Finished = true;
					}

					if (this.hairSprite != null) {
						this.hairSprite.PlayAnimUnlooped(string);
						this.def.Finished = true;
					}

					if (this.shoeSprite != null) {
						this.shoeSprite.PlayAnimUnlooped(string);
						this.def.Finished = true;
					}

					if (this.topSprite != null) {
						this.topSprite.PlayAnimUnlooped(string);
						this.def.Finished = true;
					}

					for (int int2 = 0; int2 < this.extraSprites.size(); ++int2) {
						((IsoSprite)this.extraSprites.get(int2)).PlayAnimUnlooped(string);
					}

					this.def.Finished = true;
				}
			}
		}
	}

	public void SetAnimFrame(float float1, boolean boolean1) {
		if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null) {
			if (float1 < 0.0F) {
				this.def.Frame = 0.0F;
			} else if (float1 >= (float)this.sprite.CurrentAnim.Frames.size()) {
				this.def.Frame = (float)(this.sprite.CurrentAnim.Frames.size() - 1);
			} else {
				this.def.Frame = float1;
			}

			this.def.Finished = boolean1;
		}
	}

	public void DirectionFromVectorNoDiags(Vector2 vector2) {
		if (!this.IgnoreMovementForDirection) {
			if (Math.abs(vector2.x) < Math.abs(vector2.y)) {
				tempo.x = vector2.x;
				tempo.y = vector2.y;
				tempo.x = 0.0F;
				tempo.normalize();
				this.dir = IsoDirections.fromAngle(tempo);
			} else {
				tempo.x = vector2.x;
				tempo.y = vector2.y;
				tempo.y = 0.0F;
				tempo.normalize();
				this.dir = IsoDirections.fromAngle(tempo);
			}
		}
	}

	public void DirectionFromVector(Vector2 vector2) {
		if (!this.IgnoreMovementForDirection) {
			this.dir = IsoDirections.fromAngle(vector2);
		}
	}

	public void DoFootstepSound(float float1) {
		float1 *= 0.8F;
		if (!(this instanceof IsoPlayer) || !((IsoPlayer)this).GhostMode) {
			if (this.getCurrentSquare() != null) {
				if (float1 != 0.0F) {
					float float2 = 0.05F;
					if (float1 < 0.06F) {
						float2 *= 0.4F;
					} else if (float1 <= 0.07F) {
						float2 *= 0.6F;
					} else {
						float2 *= 0.9F;
					}

					this.footStepCounter += GameTime.instance.getMultiplier();
					this.footStepCounterMax = (float)((int)(1.0F / float1 * 1.0F));
					if (float1 <= 0.06F) {
						this.footStepCounterMax *= 0.8F;
					}

					if (this.HasTrait("Graceful")) {
						float2 *= 0.6F;
					}

					if (this.HasTrait("Clumsy")) {
						float2 *= 1.2F;
					}

					float2 *= this.getLightfootMod();
					boolean boolean1 = false;
					if (this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk")) && this.def.NextFrame) {
						if ((int)this.def.Frame == 0) {
							boolean1 = true;
						}

						if ((int)this.def.Frame == 5) {
							boolean1 = true;
						}
					}

					if (boolean1 && float2 > 0.0F) {
						this.footStepCounter = 0.0F;
						this.emitter.playFootsteps("human_m");
					}

					this.def.NextFrame = false;
					int int1 = (int)(float1 * 80.0F * this.getLightfootMod());
					if (this.getCurrentSquare().getRoom() != null) {
						int1 = (int)((float)int1 * 0.5F);
					}

					if (this.HasTrait("Graceful")) {
						int1 = (int)((float)int1 * 0.6F);
					}

					if (this.HasTrait("Clumsy")) {
						int1 = (int)((float)int1 * 1.6F);
					}

					if (!GameClient.bClient) {
						int1 *= 3;
						if (Rand.Next(4) == 0) {
							WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int1, int1, false);
						} else if (Rand.Next(4) == 0) {
							WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), (int)((float)int1 * 0.5F), (int)((float)int1 * 0.5F), false);
						}
					}
				}
			}
		}
	}

	public boolean Eat(InventoryItem inventoryItem, float float1) {
		float1 = Math.max(0.0F, float1);
		float1 = Math.min(1.0F, float1);
		if (!(inventoryItem instanceof Food)) {
			return false;
		} else {
			Food food = (Food)inventoryItem;
			if (food.getRequireInHandOrInventory() != null) {
				InventoryItem inventoryItem2 = null;
				for (int int1 = 0; int1 < food.getRequireInHandOrInventory().size(); ++int1) {
					String string = (String)food.getRequireInHandOrInventory().get(int1);
					inventoryItem2 = this.getInventory().FindAndReturn(string);
					if (inventoryItem2 != null) {
						inventoryItem2.Use();
						break;
					}
				}
			}

			if (food.getBaseHunger() != 0.0F && food.getHungChange() != 0.0F) {
				float float2 = food.getBaseHunger() * float1;
				float float3 = float2 / food.getHungChange();
				if (float3 < 0.0F) {
					float3 = 0.0F;
				}

				if (float3 > 1.0F) {
					float3 = 1.0F;
				}

				float1 = float3;
			}

			if (food.getHungChange() * (1.0F - float1) > -0.01F) {
				float1 = 1.0F;
			}

			Stats stats = this.stats;
			stats.thirst += food.getThirstChange() * float1;
			if (this.stats.thirst < 0.0F) {
				this.stats.thirst = 0.0F;
			}

			stats = this.stats;
			stats.hunger += food.getHungerChange() * float1;
			stats = this.stats;
			stats.endurance += food.getEnduranceChange() * float1;
			stats = this.stats;
			stats.stress += food.getStressChange() * float1;
			stats = this.stats;
			stats.fatigue += food.getFatigueChange() * float1;
			if (this instanceof IsoPlayer) {
				((IsoPlayer)this).getNutrition().setCalories(((IsoPlayer)this).getNutrition().getCalories() + food.getCalories() * float1);
				((IsoPlayer)this).getNutrition().setCarbohydrates(((IsoPlayer)this).getNutrition().getCarbohydrates() + food.getCarbohydrates() * float1);
				((IsoPlayer)this).getNutrition().setProteins(((IsoPlayer)this).getNutrition().getProteins() + food.getProteins() * float1);
				((IsoPlayer)this).getNutrition().setLipids(((IsoPlayer)this).getNutrition().getLipids() + food.getLipids() * float1);
			}

			this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + food.getPainReduction() * float1);
			this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + (float)food.getFluReduction() * float1);
			if (this.BodyDamage.getFoodSicknessLevel() > 0.0F && (float)food.getReduceFoodSickness() > 0.0F) {
				this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - (float)food.getReduceFoodSickness() * float1);
				this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - (float)food.getReduceFoodSickness() * float1);
				if (this.BodyDamage.getFoodSicknessLevel() < 0.0F) {
					this.BodyDamage.setFoodSicknessLevel(0.0F);
				}

				if (this.BodyDamage.getPoisonLevel() < 0.0F) {
					this.BodyDamage.setPoisonLevel(0.0F);
				}
			}

			this.BodyDamage.JustAteFood(food, float1);
			if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
				GameClient.instance.eatFood((IsoPlayer)this, food, float1);
			}

			if (float1 == 1.0F) {
				food.setHungChange(0.0F);
				food.UseItem();
			} else {
				food.multiplyFoodValues(1.0F - float1);
				if ((double)food.getHungerChange() > -0.01) {
					food.setHungChange(0.0F);
					food.UseItem();
					return true;
				}
			}

			if (((Food)inventoryItem).getOnEat() != null) {
				LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(((Food)inventoryItem).getOnEat()), inventoryItem, this);
			}

			return true;
		}
	}

	public boolean Eat(InventoryItem inventoryItem) {
		return this.Eat(inventoryItem, 1.0F);
	}

	public boolean EatRemote(InventoryItem inventoryItem) {
		if (!(inventoryItem instanceof Food)) {
			return false;
		} else {
			Food food = (Food)inventoryItem;
			if (food.getRequireInHandOrInventory() != null) {
				InventoryItem inventoryItem2 = null;
				for (int int1 = 0; int1 < food.getRequireInHandOrInventory().size(); ++int1) {
					String string = (String)food.getRequireInHandOrInventory().get(int1);
					inventoryItem2 = this.getInventory().FindAndReturn(string);
					if (inventoryItem2 != null) {
						inventoryItem2.Use();
						break;
					}
				}
			}

			Stats stats = this.stats;
			stats.thirst += food.getThirstChange();
			if (this.stats.thirst < 0.0F) {
				this.stats.thirst = 0.0F;
			}

			stats = this.stats;
			stats.hunger += food.getHungerChange();
			stats = this.stats;
			stats.endurance += food.getEnduranceChange();
			stats = this.stats;
			stats.stress += food.getStressChange();
			stats = this.stats;
			stats.fatigue += food.getFatigueChange();
			this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + food.getPainReduction());
			this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + (float)food.getFluReduction());
			if (this.BodyDamage.getFoodSicknessLevel() > 0.0F && (float)food.getReduceFoodSickness() > 0.0F) {
				this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - (float)food.getReduceFoodSickness());
				this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - (float)food.getReduceFoodSickness());
				if (this.BodyDamage.getFoodSicknessLevel() < 0.0F) {
					this.BodyDamage.setFoodSicknessLevel(0.0F);
				}

				if (this.BodyDamage.getPoisonLevel() < 0.0F) {
					this.BodyDamage.setPoisonLevel(0.0F);
				}
			}

			this.BodyDamage.JustAteFood(food);
			return true;
		}
	}

	public void FaceNextPathNode() {
		if (this.path != null) {
			if (this.pathIndex <= this.path.getLength()) {
				boolean boolean1 = false;
				boolean boolean2 = false;
				boolean boolean3 = false;
				boolean boolean4 = false;
				float float1 = (float)this.path.getX(this.pathIndex) + 0.5F;
				float float2 = (float)this.path.getY(this.pathIndex) + 0.5F;
				if (float1 > this.getX() && Math.abs(float1 - this.getX()) >= 0.1F) {
					boolean3 = true;
				}

				if (float1 < this.getX() && Math.abs(float1 - this.getX()) >= 0.1F) {
					boolean4 = true;
				}

				if (float2 > this.getY() && Math.abs(float2 - this.getY()) >= 0.1F) {
					boolean2 = true;
				}

				if (float2 < this.getY() && Math.abs(float2 - this.getY()) >= 0.1F) {
					boolean1 = true;
				}

				if (boolean1 && boolean4) {
					this.dir = IsoDirections.NW;
				} else if (boolean1 && boolean3) {
					this.dir = IsoDirections.NE;
				} else if (boolean2 && boolean3) {
					this.dir = IsoDirections.SE;
				} else if (boolean2 && boolean4) {
					this.dir = IsoDirections.SW;
				} else if (boolean2) {
					this.dir = IsoDirections.S;
				} else if (boolean1) {
					this.dir = IsoDirections.N;
				} else if (boolean3) {
					this.dir = IsoDirections.E;
				} else if (boolean4) {
					this.dir = IsoDirections.W;
				}
			}
		}
	}

	public void FaceNextPathNode(int int1, int int2) {
		tempo.x = (float)int1 + 0.5F;
		tempo.y = (float)int2 + 0.5F;
		Vector2 vector2 = tempo;
		vector2.x -= this.getX();
		vector2 = tempo;
		vector2.y -= this.getY();
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
									float float1 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F / 2.0F;
									this.BodyDamage.ReduceGeneralHealth(float1);
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

	public InventoryItem getCraftingByIndex(int int1) {
		if (int1 == 0) {
			return this.craftIngredient1;
		} else if (int1 == 1) {
			return this.craftIngredient2;
		} else if (int1 == 2) {
			return this.craftIngredient3;
		} else {
			return int1 == 3 ? this.craftIngredient4 : null;
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

	public boolean HasItem(String string) {
		if (string == null) {
			return true;
		} else {
			return string.equals(this.getSecondaryHandType()) || string.equals(this.getPrimaryHandType()) || this.inventory.contains(string);
		}
	}

	public void changeState(State state) {
		this.stateMachine.changeState(state);
	}

	public State getCurrentState() {
		return this.stateMachine.getCurrent();
	}

	public void setLockStates(boolean boolean1) {
		this.stateMachine.Lock = boolean1;
	}

	public void Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
		this.Hit(handWeapon, gameCharacter, float1, boolean1, float2, false);
	}

	public void Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2, boolean boolean2) {
		if (gameCharacter != null && handWeapon != null) {
			if (this.isOnFloor()) {
				float1 = 1.0F;
				float2 = 2.0F;
				boolean1 = false;
			}

			if (gameCharacter.legsSprite.CurrentAnim.name != null && gameCharacter.legsSprite.CurrentAnim.name.contains("Shove")) {
				boolean1 = true;
				float2 *= 1.5F;
			}

			LuaEventManager.triggerEvent("OnWeaponHitCharacter", gameCharacter, this, handWeapon, float1);
			if (!LuaHookManager.TriggerHook("WeaponHitCharacter", gameCharacter, this, handWeapon, float1)) {
				if (this.avoidDamage) {
					this.avoidDamage = false;
				} else {
					if (this.noDamage) {
						boolean1 = true;
						this.noDamage = false;
					}

					if (this instanceof IsoSurvivor && !this.EnemyList.contains(gameCharacter)) {
						this.EnemyList.add(gameCharacter);
					}

					this.staggerTimeMod = handWeapon.getPushBackMod() * handWeapon.getKnockbackMod(gameCharacter) * gameCharacter.getShovingMod();
					if (this instanceof IsoZombie && Rand.Next(3) == 0) {
						this.emitter.playSound(this.hurtSound);
						if (GameServer.bServer) {
							GameServer.sendZombieSound(IsoZombie.ZombieSound.Hurt, (IsoZombie)this);
						}
					}

					WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 5, 1);
					float float3 = float1 * float2;
					float float4 = float3 * handWeapon.getKnockbackMod(gameCharacter) * gameCharacter.getShovingMod();
					if (float4 > 1.0F) {
						float4 = 1.0F;
					}

					this.setHitForce(float4);
					if (gameCharacter.HasTrait("Strong") && !handWeapon.isRanged()) {
						this.setHitForce(this.getHitForce() * 1.4F);
					}

					if (gameCharacter.HasTrait("Weak") && !handWeapon.isRanged()) {
						this.setHitForce(this.getHitForce() * 0.6F);
					}

					this.AttackedBy = gameCharacter;
					float float5 = IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), this.getX(), this.getY());
					float5 -= handWeapon.getMinRange();
					float5 /= handWeapon.getMaxRange(gameCharacter);
					float5 = 1.0F - float5;
					if (float5 > 1.0F) {
						float5 = 1.0F;
					}

					this.hitDir.x = this.getX();
					this.hitDir.y = this.getY();
					Vector2 vector2 = this.hitDir;
					vector2.x -= gameCharacter.getX();
					vector2 = this.hitDir;
					vector2.y -= gameCharacter.getY();
					this.getHitDir().normalize();
					vector2 = this.hitDir;
					vector2.x *= handWeapon.getPushBackMod();
					vector2 = this.hitDir;
					vector2.y *= handWeapon.getPushBackMod();
					this.hitDir.rotate(handWeapon.HitAngleMod);
					float float6 = gameCharacter.stats.endurance;
					float6 *= gameCharacter.knockbackAttackMod;
					this.hitBy = gameCharacter;
					if (float6 < 0.5F) {
						float6 *= 1.3F;
						if (float6 < 0.4F) {
							float6 = 0.4F;
						}

						this.setHitForce(this.getHitForce() * float6);
					}

					if (!handWeapon.isRangeFalloff()) {
						float5 = 1.0F;
					}

					if (!handWeapon.isShareDamage()) {
						float1 = 1.0F;
					}

					if (gameCharacter instanceof IsoPlayer) {
						this.setHitForce(this.getHitForce() * 2.0F);
					}

					Vector2 vector22 = tempVector2_1.set(this.getX(), this.getY());
					Vector2 vector23 = tempVector2_2.set(gameCharacter.getX(), gameCharacter.getY());
					vector22.x -= vector23.x;
					vector22.y -= vector23.y;
					Vector2 vector24 = this.getVectorFromDirection(tempVector2_2);
					vector22.normalize();
					float float7 = vector22.dot(vector24);
					if (float7 > -0.3F) {
						float3 *= 1.5F;
					}

					if (this instanceof IsoPlayer) {
						float3 *= 0.4F;
					} else {
						float3 *= 1.5F;
					}

					if (handWeapon.getCategories() != null && handWeapon.getCategories().contains("Unarmed") && float3 > 0.7F) {
						float3 = 0.7F;
					}

					if (handWeapon.getCategories().contains("Blade") || handWeapon.getCategories().contains("Axe")) {
						switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Axe)) {
						case 0: 
							float3 *= 0.3F;
							break;
						
						case 1: 
							float3 *= 0.4F;
							break;
						
						case 2: 
							float3 *= 0.5F;
							break;
						
						case 3: 
							float3 *= 0.6F;
							break;
						
						case 4: 
							float3 *= 0.7F;
							break;
						
						case 5: 
							float3 *= 0.8F;
							break;
						
						case 6: 
							float3 *= 0.9F;
							break;
						
						case 7: 
							float3 *= 1.0F;
							break;
						
						case 8: 
							float3 *= 1.1F;
							break;
						
						case 9: 
							float3 *= 1.2F;
							break;
						
						case 10: 
							float3 *= 1.3F;
						
						}
					}

					if (handWeapon.getCategories().contains("Blunt")) {
						switch (gameCharacter.getPerkLevel(PerkFactory.Perks.Blunt)) {
						case 0: 
							float3 *= 0.3F;
							break;
						
						case 1: 
							float3 *= 0.4F;
							break;
						
						case 2: 
							float3 *= 0.5F;
							break;
						
						case 3: 
							float3 *= 0.6F;
							break;
						
						case 4: 
							float3 *= 0.7F;
							break;
						
						case 5: 
							float3 *= 0.8F;
							break;
						
						case 6: 
							float3 *= 0.9F;
							break;
						
						case 7: 
							float3 *= 1.0F;
							break;
						
						case 8: 
							float3 *= 1.1F;
							break;
						
						case 9: 
							float3 *= 1.2F;
							break;
						
						case 10: 
							float3 *= 1.3F;
						
						}
					}

					float float8 = handWeapon.CriticalChance;
					if (handWeapon.isRanged()) {
						float8 += (float)(handWeapon.getAimingPerkCritModifier() * (gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2));
					}

					if (this.isOnFloor()) {
						float8 *= 2.0F;
					}

					if (float8 > 100.0F) {
						float8 = 100.0F;
					}

					if ((float)Rand.Next(100) < float8 && !boolean1) {
						float3 *= 10.0F;
					}

					if (!this.isOnFloor() && handWeapon.getScriptItem().Categories.contains("Axe")) {
						float3 *= 2.0F;
					}

					float float9 = 12.0F;
					if (gameCharacter instanceof IsoPlayer) {
						int int1 = ((IsoPlayer)gameCharacter).Moodles.getMoodleLevel(MoodleType.Endurance);
						if (int1 == 4) {
							float9 = 50.0F;
						} else if (int1 == 3) {
							float9 = 35.0F;
						} else if (int1 == 2) {
							float9 = 24.0F;
						} else if (int1 == 1) {
							float9 = 16.0F;
						}
					}

					if (handWeapon.getKnockdownMod() <= 0.0F) {
						handWeapon.setKnockdownMod(1.0F);
					}

					float9 /= handWeapon.getKnockdownMod();
					if (float3 < handWeapon.getMinDamage() / 4.0F) {
						float9 += 10.0F;
					}

					if (gameCharacter instanceof IsoPlayer && !handWeapon.isAimedHandWeapon()) {
						float9 = (float)((double)float9 - (double)((IsoPlayer)gameCharacter).useChargeDelta * 2.5);
					}

					if (float9 < 1.0F) {
						float9 = 1.0F;
					}

					if (float9 > 10.0F) {
						float9 = 10.0F;
					}

					if (gameCharacter instanceof IsoPlayer) {
						float9 = (float)((double)float9 - (double)((IsoPlayer)gameCharacter).Moodles.getMoodleLevel(MoodleType.Panic) * 1.3);
					}

					if (this.getCurrentState() == StaggerBackState.instance()) {
						float9 /= 3.0F;
					}

					boolean boolean3 = Rand.Next((int)float9) == 0;
					if (Core.getInstance().getGameMode().equals("Tutorial")) {
						boolean3 = true;
					}

					float7 = 0.0F;
					if (handWeapon.isTwoHandWeapon() && (gameCharacter.getPrimaryHandItem() != handWeapon || gameCharacter.getSecondaryHandItem() != handWeapon)) {
						float7 = handWeapon.getWeight() / 1.5F / 10.0F;
					}

					float float10 = (handWeapon.getWeight() * 0.28F * handWeapon.getFatigueMod(gameCharacter) * this.getFatigueMod() * handWeapon.getEnduranceMod() * 0.3F + float7) * 0.04F;
					float float11;
					if (handWeapon.isAimedFirearm()) {
						float11 = float3 * 0.7F;
					} else {
						float11 = float3 * 0.15F;
					}

					if (this.getHealth() < float3) {
						float11 = this.getHealth();
					}

					float float12 = float11 / handWeapon.getMaxDamage();
					if (float12 > 1.0F) {
						float12 = 1.0F;
					}

					if (this.isCloseKilled()) {
						float12 = 0.2F;
					}

					if (handWeapon.isUseEndurance()) {
						Stats stats = gameCharacter.getStats();
						stats.endurance -= float10 * float12;
					}

					this.hitConsequences(handWeapon, gameCharacter, boolean1, float3, boolean3);
				}
			}
		}
	}

	public void hitConsequences(HandWeapon handWeapon, IsoGameCharacter gameCharacter, boolean boolean1, float float1, boolean boolean2) {
		if (this instanceof IsoPlayer) {
			if (boolean1) {
				if (GameServer.bServer) {
					this.sendObjectChange("Shove", new Object[]{"hitDirX", this.getHitDir().getX(), "hitDirY", this.getHitDir().getY(), "force", this.getHitForce()});
					return;
				}

				gameCharacter.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
				this.setHitForce(Math.min(0.5F, this.getHitForce()));
				if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
					StaggerBackState.instance().enter(this);
				}

				this.stateMachine.changeState(StaggerBackState.instance());
				return;
			}

			this.BodyDamage.DamageFromWeapon(handWeapon);
		} else {
			if (gameCharacter instanceof IsoPlayer) {
				if (!boolean1) {
					if (handWeapon.isAimedFirearm()) {
						this.Health -= float1 * 0.7F;
					} else {
						this.Health -= float1 * 0.15F;
					}
				}
			} else if (!boolean1) {
				if (handWeapon.isAimedFirearm()) {
					this.Health -= float1 * 0.7F;
				} else {
					this.Health -= float1 * 0.15F;
				}
			}

			if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() <= 0.0F) && (!handWeapon.isAlwaysKnockdown() && !boolean2 || !(this instanceof IsoZombie))) {
				if (handWeapon.isSplatBloodOnNoDeath()) {
					this.splatBlood(3, 0.3F);
				}

				if (handWeapon.isKnockBackOnNoDeath()) {
					if (gameCharacter.xp != null) {
						gameCharacter.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
					}

					if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
						StaggerBackState.instance().enter(this);
					}

					this.stateMachine.changeState(StaggerBackState.instance());
				}
			} else {
				this.DoDeath(handWeapon, gameCharacter);
			}
		}
	}

	public void inflictWound(IsoGameCharacter.BodyLocation bodyLocation, float float1, boolean boolean1, float float2) {
		IsoGameCharacter.Wound wound = new IsoGameCharacter.Wound();
		wound.loc = bodyLocation;
		wound.bleeding = float1;
		wound.infectedZombie = false;
		if (Rand.Next(100) < (int)float2 * 100) {
			wound.infectedNormal = true;
		}

		this.wounds.add(wound);
	}

	public boolean IsAttackRange(float float1, float float2, float float3) {
		float float4 = 1.0F;
		float float5 = 0.0F;
		if (this.leftHandItem != null) {
			InventoryItem inventoryItem = this.leftHandItem;
			if (inventoryItem instanceof HandWeapon) {
				float4 = ((HandWeapon)inventoryItem).getMaxRange(this);
				float5 = ((HandWeapon)inventoryItem).getMinRange();
				float4 *= ((HandWeapon)this.leftHandItem).getRangeMod(this);
			}
		}

		if (Math.abs(float3 - this.getZ()) > 0.3F) {
			return false;
		} else {
			float float6 = IsoUtils.DistanceTo(float1, float2, this.getX(), this.getY());
			return float6 < float4 && float6 > float5;
		}
	}

	public boolean IsAttackRange(HandWeapon handWeapon, float float1, float float2, float float3) {
		float float4 = 1.0F;
		float float5 = 0.0F;
		if (handWeapon instanceof HandWeapon) {
			float4 = ((HandWeapon)handWeapon).getMaxRange(this);
			float5 = ((HandWeapon)handWeapon).getMinRange();
			float4 *= handWeapon.getRangeMod(this);
		}

		if (handWeapon != null && !handWeapon.isRanged() && Math.abs(float3 - this.getZ()) >= 0.5F) {
			return false;
		} else if (Math.abs(float3 - this.getZ()) > 3.3F) {
			return false;
		} else {
			float float6 = IsoUtils.DistanceTo(float1, float2, this.getX(), this.getY());
			return float6 < float4;
		}
	}

	public boolean IsSpeaking() {
		return this.chatElement.IsSpeaking();
	}

	public void MoveForward(float float1) {
		this.moveForwardVec.x = 0.0F;
		this.moveForwardVec.y = 0.0F;
		switch (this.dir) {
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
		this.moveForwardVec.setLength(float1);
		this.setNx(this.getNx() + this.moveForwardVec.x * GameTime.instance.getMultiplier());
		this.setNy(this.getNy() + this.moveForwardVec.y * GameTime.instance.getMultiplier());
		this.DoFootstepSound(float1);
		if (!(this instanceof IsoZombie)) {
		}
	}

	public void MoveForward(float float1, float float2, float float3, float float4) {
		if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
			this.reqMovement.x = float2;
			this.reqMovement.y = float3;
			this.reqMovement.normalize();
			float float5 = GameTime.instance.getMultiplier();
			this.setNx(this.getNx() + float2 * float1 * float5);
			this.setNy(this.getNy() + float3 * float1 * float5);
			this.DoFootstepSound(float1);
			if (!(this instanceof IsoZombie)) {
			}
		}
	}

	public void pathFinished() {
		this.setPathFindIndex(-1);
		this.stateMachine.changeState(this.defaultState);
	}

	private void pathToAux(float float1, float float2, float float3) {
		boolean boolean1 = true;
		if ((int)float3 == (int)this.getZ() && IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) <= 30.0F) {
			int int1 = (int)float1 / 10;
			int int2 = (int)float2 / 10;
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1, int2) : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)float1, (int)float2, (int)float3);
			if (chunk != null) {
				boolean1 = !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), float1, float2, (int)float3, this.getPathFindBehavior2().getTargetChar());
			}
		}

		if (boolean1 && this.current != null && this.current.HasStairs() && !this.current.isSameStaircase((int)float1, (int)float2, (int)float3)) {
			boolean1 = false;
		}

		if (boolean1) {
			this.stateMachine.changeState(WalkTowardState.instance());
		} else {
			this.stateMachine.changeState(PathFindState.instance());
		}
	}

	public void pathToCharacter(IsoGameCharacter gameCharacter) {
		this.getPathFindBehavior2().pathToCharacter(gameCharacter);
		this.pathToAux(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ());
	}

	public void pathToLocation(int int1, int int2, int int3) {
		this.getPathFindBehavior2().pathToLocation(int1, int2, int3);
		this.pathToAux((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3);
	}

	public void pathToLocationF(float float1, float float2, float float3) {
		this.getPathFindBehavior2().pathToLocationF(float1, float2, float3);
		this.pathToAux(float1, float2, float3);
	}

	public boolean CanAttack() {
		InventoryItem inventoryItem = this.leftHandItem;
		if (inventoryItem instanceof HandWeapon && inventoryItem.getSwingAnim() != null) {
			this.useHandWeapon = (HandWeapon)inventoryItem;
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
			float float1 = 12.0F;
			int int1 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
			return !this.useHandWeapon.isCantAttackWithLowestEndurance() || int1 != 4;
		}
	}

	public void PlayShootAnim() {
		if (!this.sprite.CurrentAnim.name.contains("Attack_")) {
			InventoryItem inventoryItem = this.leftHandItem;
			if (inventoryItem instanceof HandWeapon && inventoryItem.getSwingAnim() != null) {
				if (this.bUseParts) {
					this.useHandWeapon = (HandWeapon)inventoryItem;
					if (this.useHandWeapon.getCondition() <= 0) {
						return;
					}

					if (this.useHandWeapon.isCantAttackWithLowestEndurance() && this.stats.endurance < this.stats.endurancedanger) {
						return;
					}

					this.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
					this.legsSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
					this.def.Frame = 0.0F;
					this.def.Finished = true;
					this.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					if (this.torsoSprite != null) {
						this.torsoSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						this.def.Finished = true;
						this.torsoSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					}

					if (this.headSprite != null) {
						this.headSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						this.def.Finished = true;
						this.headSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					}

					for (int int1 = 0; int1 < this.extraSprites.size(); ++int1) {
						((IsoSprite)this.extraSprites.get(int1)).PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						((IsoSprite)this.extraSprites.get(int1)).CurrentAnim.FinishUnloopedOnFrame = 0;
						this.def.Finished = true;
					}

					if (this.bottomsSprite != null) {
						this.bottomsSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						this.def.Finished = true;
						this.bottomsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					}

					if (this.hairSprite != null) {
						this.hairSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						this.def.Finished = true;
						this.hairSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					}

					if (this.shoeSprite != null) {
						this.shoeSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						this.def.Finished = true;
						this.shoeSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					}

					if (this.topSprite != null) {
						this.topSprite.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
						this.def.Finished = true;
						this.topSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
					}

					this.def.Frame = 0.0F;
					this.sprite.Animate = false;
					this.def.setFrameSpeedPerFrame(1.0F / this.useHandWeapon.getSwingTime());
				} else {
					this.useHandWeapon = (HandWeapon)inventoryItem;
					if (this.useHandWeapon.getCondition() <= 0) {
						return;
					}

					if (this.useHandWeapon.isCantAttackWithLowestEndurance() && this.stats.endurance < this.stats.endurancedanger) {
						return;
					}

					this.PlayAnimUnlooped("Attack_" + inventoryItem.getSwingAnim());
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
							float float1 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F;
							this.BodyDamage.ReduceGeneralHealth(float1);
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
			String string = null;
			if (this.BodyDamage.IsSneezingCoughing() == 1) {
				string = "Ah-choo!";
			}

			if (this.BodyDamage.IsSneezingCoughing() == 2) {
				string = "Cough!";
			}

			if (this.BodyDamage.IsSneezingCoughing() == 3) {
				string = "Ah-fmmph!";
			}

			if (this.BodyDamage.IsSneezingCoughing() == 4) {
				string = "fmmmph!";
			}

			float float1 = (float)this.sx;
			float float2 = (float)this.sy;
			float1 = (float)((int)float1);
			float2 = (float)((int)float2);
			float1 -= (float)((int)IsoCamera.getOffX());
			float2 -= (float)((int)IsoCamera.getOffY());
			float2 -= 48.0F;
			if (string != null) {
				IndieGL.End();
				TextManager.instance.DrawStringCentre(UIFont.Dialogue, (double)((int)float1), (double)((int)float2), string, (double)this.SpeakColour.r, (double)this.SpeakColour.g, (double)this.SpeakColour.b, (double)this.SpeakColour.a);
			}
		}
	}

	public IsoSpriteInstance getSpriteDef() {
		if (this.def == null) {
			this.def = new IsoSpriteInstance();
		}

		return this.def;
	}

	public void drawAt(int int1, int int2) {
		if (this.def == null) {
			this.def = this.def;
		}

		if (!this.bUseParts && this.def == null) {
			this.def = this.def = IsoSpriteInstance.get(this.sprite);
		}

		if (this.sprite != null) {
			if (!this.bUseParts) {
				this.sprite.drawAt(this.def, this, int1, int2, this.dir);
			} else {
				this.EnforceAnims();
				this.legsSprite.drawAt(this.def, this, int1, int2, this.dir);
				if (this.torsoSprite != null) {
					this.torsoSprite.drawAt(this.def, this, int1, int2, this.dir);
				}

				if (this.shoeSprite != null) {
					this.shoeSprite.drawAt(this.def, this, int1, int2, this.dir);
				}

				if (this.bottomsSprite != null) {
					this.bottomsSprite.TintMod.r = this.descriptor.trouserColor.r;
					this.bottomsSprite.TintMod.g = this.descriptor.trouserColor.g;
					this.bottomsSprite.TintMod.b = this.descriptor.trouserColor.b;
					this.bottomsSprite.TintMod.desaturate(0.5F);
					this.bottomsSprite.drawAt(this.def, this, int1, int2, this.dir);
				}

				if (this.topSprite != null) {
					this.topSprite.TintMod.r = this.descriptor.topColor.r;
					this.topSprite.TintMod.g = this.descriptor.topColor.g;
					this.topSprite.TintMod.b = this.descriptor.topColor.b;
					this.topSprite.TintMod.desaturate(0.5F);
					this.topSprite.drawAt(this.def, this, int1, int2, this.dir);
				}

				if (this.headSprite != null) {
					this.headSprite.drawAt(this.def, this, int1, int2, this.dir);
				}

				if (this.hairSprite != null) {
					this.hairSprite.drawAt(this.def, this, int1, int2, this.dir);
				}

				for (int int3 = 0; int3 < this.extraSprites.size(); ++int3) {
					((IsoSprite)this.extraSprites.get(int3)).drawAt(this.def, this, int1, int2, this.dir);
				}
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		if (this.alpha[IsoPlayer.getPlayerIndex()] != 0.0F || this.targetAlpha[IsoPlayer.getPlayerIndex()] != 0.0F) {
			if (this.vehicle == null || this.vehicle.getSeat(this) == -1) {
				if (!this.bUseParts && this.def == null) {
					this.def = IsoSpriteInstance.get(this.sprite);
				}

				int int1 = PerformanceSettings.numberOf3D;
				switch (PerformanceSettings.numberOf3D) {
				case 1: 
					int1 = 1;
					break;
				
				case 2: 
					int1 = 2;
					break;
				
				case 3: 
					int1 = 3;
					break;
				
				case 4: 
					int1 = 4;
					break;
				
				case 5: 
					int1 = 5;
					break;
				
				case 6: 
					int1 = 8;
					break;
				
				case 7: 
					int1 = 10;
					break;
				
				case 8: 
					int1 = 20;
					break;
				
				case 9: 
					int1 = 20000;
				
				}

				int1 += PerformanceSettings.numberOf3DAlt;
				float float4;
				float float5;
				float float6;
				float float7;
				if (DropShadow != null && this.getCurrentSquare() != null && this.getVehicle() == null) {
					float4 = 0.5F * this.alpha[IsoPlayer.getPlayerIndex()];
					float4 *= (this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo().r + this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo().g + this.getCurrentSquare().lighting[IsoPlayer.getPlayerIndex()].lightInfo().b) / 3.0F;
					if (DropShadow.def == null) {
						DropShadow.def = IsoSpriteInstance.get(DropShadow);
					}

					IsoAnim anim = this.legsSprite != null && this.legsSprite.CurrentAnim != null ? this.legsSprite.CurrentAnim : null;
					if ("ZombieDeath".equals(anim.name) && this.def.Frame >= (float)(anim.Frames.size() / 2)) {
						float4 *= ((float)anim.Frames.size() - this.def.Frame) / (float)(anim.Frames.size() / 2);
					} else if ("ZombieGetUp".equals(anim.name) && this.def.Frame < (float)(anim.Frames.size() / 2)) {
						float4 *= 1.0F - ((float)(anim.Frames.size() / 2) - this.def.Frame) / (float)(anim.Frames.size() / 2);
					} else if (anim.name != null && anim.name.contains("Crawl")) {
						float4 = 0.0F;
					}

					DropShadow.def.alpha = 0.8F * float4;
					DropShadow.def.targetAlpha = DropShadow.def.alpha;
					float5 = this.def.getScaleX();
					float6 = this.def.getScaleY();
					DropShadow.def.setScale(float5, float6);
					float7 = 49.0F;
					float float8 = 27.0F;
					DropShadow.render((IsoObject)null, float1, float2, float3, this.dir, this.offsetX + float7 * float5 / 2.0F, this.offsetY + float8 * float6 / 2.0F - 2.0F * float6, inf);
				}

				IsoGridSquare square;
				if (this.bDoDefer && float3 - (float)((int)float3) > 0.2F) {
					square = this.getCell().getGridSquare((int)float1, (int)float2, (int)float3 + 1);
					if (square != null) {
						square.addDeferredCharacter(this);
					}
				}

				if (PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
					this.getCurrentSquare().interpolateLight(inf, float1 - (float)this.getCurrentSquare().getX(), float2 - (float)this.getCurrentSquare().getY());
					if (int1 > 0 && this.legsSprite.modelSlot == null) {
					}

					if (float3 - (float)((int)float3) > 0.2F) {
						square = this.getCell().getGridSquare((int)float1, (int)float2, (int)float3 + 1);
						if (square != null) {
							ColorInfo colorInfo2 = tempColorInfo;
							square.lighting[IsoCamera.frameState.playerIndex].lightInfo();
							square.interpolateLight(colorInfo2, float1 - (float)this.getCurrentSquare().getX(), float2 - (float)this.getCurrentSquare().getY());
							inf.interp(colorInfo2, (float3 - ((float)((int)float3) + 0.2F)) / 0.8F, inf);
						}
					}
				} else {
					inf.r = colorInfo.r;
					inf.g = colorInfo.g;
					inf.b = colorInfo.b;
					inf.a = colorInfo.a;
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
					this.checkDrawWeaponPre(float1, float2, float3, colorInfo);
				}

				lastRenderedRendered = lastRendered;
				lastRendered = this;
				if (this.sprite == null || !this.sprite.getProperties().Is(IsoFlagType.invisible)) {
					float4 = 2.0F;
					float float9 = 1.5F;
					if (IsoCamera.CamCharacter.HasTrait("ShortSighted")) {
						float4 = 1.0F;
					}

					if (IsoCamera.CamCharacter.HasTrait("EagleEyed")) {
						float4 = 3.0F;
						float9 = 2.0F;
					}

					if (this == IsoCamera.CamCharacter) {
						this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
					}

					if (alphaStep == -100.0F) {
						IsoCamera.CamCharacter.getBodyDamage().ReduceFactor();
					}

					float[] floatArray;
					int int2;
					if (this.alpha[IsoPlayer.getPlayerIndex()] < this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
						floatArray = this.alpha;
						int2 = IsoPlayer.getPlayerIndex();
						floatArray[int2] += alphaStep * float4;
						if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
							this.alpha[IsoPlayer.getPlayerIndex()] = this.targetAlpha[IsoPlayer.getPlayerIndex()];
						}
					} else if (this.alpha[IsoPlayer.getPlayerIndex()] > this.targetAlpha[IsoPlayer.getPlayerIndex()]) {
						floatArray = this.alpha;
						int2 = IsoPlayer.getPlayerIndex();
						floatArray[int2] -= alphaStep / float9 / 2.5F;
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
						float5 = (float)Core.TileScale;
						float6 = this.offsetX + (float)RENDER_OFFSET_X * float5;
						float7 = this.offsetY + (float)RENDER_OFFSET_Y * float5;
						int int3;
						if (this.sprite != null) {
							this.def.setScale(float5, float5);
							if (!this.bUseParts) {
								this.sprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
							} else {
								this.def.Flip = false;
								this.EnforceAnims();
								this.legsSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
								if (!this.hasActiveModel()) {
									if (this.torsoSprite != null) {
										this.torsoSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}

									if (this.shoeSprite != null) {
										this.shoeSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}

									if (this.bottomsSprite != null) {
										this.bottomsSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}

									if (this.topSprite != null) {
										this.topSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}

									if (this.headSprite != null) {
										this.headSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}

									if (this.hairSprite != null) {
										this.hairSprite.render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}

									for (int3 = 0; int3 < this.extraSprites.size(); ++int3) {
										((IsoSprite)this.extraSprites.get(int3)).render(this.def, this, float1, float2, float3, this.dir, float6, float7, inf);
									}
								}
							}
						}

						if (this.AttachedAnimSprite != null) {
							for (int3 = 0; int3 < this.AttachedAnimSprite.size(); ++int3) {
								IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int3);
								spriteInstance.update();
								float float10 = inf.a;
								inf.a = spriteInstance.alpha;
								spriteInstance.SetTargetAlpha(this.targetAlpha[IsoPlayer.getPlayerIndex()]);
								spriteInstance.render(this, float1, float2, float3, this.dir, float6, float7, inf);
								inf.a = float10;
							}
						}

						if (this.sprite != null && !this.legsSprite.hasActiveModel()) {
							this.checkDrawWeaponPost(float1, float2, float3, inf);
						}

						for (int3 = 0; int3 < this.inventory.Items.size(); ++int3) {
							InventoryItem inventoryItem = (InventoryItem)this.inventory.Items.get(int3);
							if (inventoryItem instanceof IUpdater) {
								((IUpdater)inventoryItem).render();
							}
						}
					}
				}
			}
		}
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (!this.bUseParts) {
			this.sprite.renderObjectPicker(this.def, this, float1, float2, float3, this.dir, this.offsetX, this.offsetY, colorInfo);
		} else {
			this.legsSprite.renderObjectPicker(this.def, this, float1, float2, float3, this.dir, this.offsetX, this.offsetY, colorInfo);
			if (this.torsoSprite != null) {
				this.torsoSprite.renderObjectPicker(this.def, this, float1, float2, float3, this.dir, this.offsetX, this.offsetY, colorInfo);
			}
		}
	}

	public boolean isMaskClicked(int int1, int int2, boolean boolean1) {
		if (this.sprite == null) {
			return false;
		} else if (!this.bUseParts) {
			return super.isMaskClicked(int1, int2, boolean1);
		} else {
			boolean boolean2 = false;
			boolean2 = this.legsSprite.isMaskClicked(this.dir, int1, int2, boolean1);
			if (this.torsoSprite != null) {
				boolean2 |= this.torsoSprite.isMaskClicked(this.dir, int1, int2, boolean1);
			}

			return boolean2;
		}
	}

	public void setHaloNote(String string) {
		this.setHaloNote(string, this.haloDispTime);
	}

	public void setHaloNote(String string, float float1) {
		this.setHaloNote(string, 0, 255, 0, float1);
	}

	public void setHaloNote(String string, int int1, int int2, int int3, float float1) {
		if (this.haloNote != null && string != null) {
			this.haloDispTime = float1;
			this.haloNote.setDefaultColors(int1, int2, int3);
			this.haloNote.ReadString(string);
			this.haloNote.setInternalTickClock(this.haloDispTime);
		}
	}

	public void DoSneezeText() {
		if (this.BodyDamage.IsSneezingCoughing() > 0) {
			String string = null;
			if (this.BodyDamage.IsSneezingCoughing() == 1) {
				string = "Ah-choo!";
			}

			if (this.BodyDamage.IsSneezingCoughing() == 2) {
				string = "Cough!";
			}

			if (this.BodyDamage.IsSneezingCoughing() == 3) {
				string = "Ah-fmmph!";
			}

			if (this.BodyDamage.IsSneezingCoughing() == 4) {
				string = "fmmmph!";
			}

			if (string != null) {
				this.Say(string);
			}
		}
	}

	public String getSayLine() {
		return this.chatElement.getSayLine();
	}

	public ChatMessage getLastChatMessage() {
		return this.lastChatMessage;
	}

	public void setLastChatMessage(ChatMessage chatMessage) {
		this.lastChatMessage = chatMessage;
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

	public void SayDebug(String string) {
		this.chatElement.SayDebug(0, string);
	}

	public void SayDebug(int int1, String string) {
		this.chatElement.SayDebug(int1, string);
	}

	public int getMaxChatLines() {
		return this.chatElement.getMaxChatLines();
	}

	public void Say(String string) {
		this.ProcessSay(string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, UIFont.Dialogue, 30.0F, false, "default", false, false, false, false, false, true);
		if (this.AllowConversation) {
			if (TutorialManager.instance.ProfanityFilter) {
				string = ProfanityFilter.getInstance().filterString(string);
			}

			ChatManager.getInstance().showInfoMessage(((IsoPlayer)this).getUsername(), string);
		}
	}

	public void SayWhisper(String string) {
		if (this.AllowConversation) {
			if (TutorialManager.instance.ProfanityFilter) {
				string = ProfanityFilter.getInstance().filterString(string);
			}

			this.ProcessSay(string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, UIFont.Dialogue, 10.0F, false, "whisper", false, false, false, false, false, true);
		}
	}

	public void SayShout(String string) {
		ChatManager.getInstance().sendMessageToChat(ChatType.shout, string);
		this.ProcessSay(string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, UIFont.Dialogue, 60.0F, false, "shout", false, false, false, false, false, true);
	}

	private void ProcessSay(String string, float float1, float float2, float float3, UIFont uIFont, float float4, boolean boolean1, String string2, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6, boolean boolean7) {
		if (this.AllowConversation) {
			if (TutorialManager.instance.ProfanityFilter) {
				string = ProfanityFilter.getInstance().filterString(string);
			}

			if (boolean1 && GameClient.bClient && this == IsoPlayer.instance) {
				boolean boolean8 = false;
				if (string2.equals("whisper")) {
					boolean8 = true;
				} else if (string2.equals("shout")) {
					boolean8 = true;
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
			String string = this.isoPlayer.getDisplayName();
			if (this != IsoPlayer.instance && this.isoPlayer.invisible && !this.invisible) {
				this.userName.ReadString("");
				return;
			}

			Faction faction = Faction.getPlayerFaction(this.isoPlayer);
			if (faction != null) {
				if (!this.isoPlayer.showTag && this.isoPlayer != IsoPlayer.instance && Faction.getPlayerFaction(IsoPlayer.instance) != faction) {
					this.isoPlayer.tagPrefix = "";
				} else {
					this.isoPlayer.tagPrefix = faction.getTag();
					if (faction.getTagColor() != null) {
						this.isoPlayer.setTagColor(faction.getTagColor());
					}
				}
			} else {
				this.isoPlayer.tagPrefix = "";
			}

			boolean boolean1 = this.isoPlayer != null && this.isoPlayer.bRemote || Core.getInstance().isShowYourUsername();
			boolean boolean2 = IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
			if (!ServerOptions.instance.DisplayUserName.getValue() && !boolean2) {
				boolean1 = false;
			}

			if (!boolean1) {
				string = "";
			}

			if (boolean1 && this.isoPlayer.tagPrefix != null && !this.isoPlayer.tagPrefix.equals("")) {
				string = "[col=" + (new Float(this.isoPlayer.getTagColor().r * 255.0F)).intValue() + "," + (new Float(this.isoPlayer.getTagColor().g * 255.0F)).intValue() + "," + (new Float(this.isoPlayer.getTagColor().b * 255.0F)).intValue() + "][" + this.isoPlayer.tagPrefix + "][/] " + string;
			}

			if (boolean1 && !this.isoPlayer.accessLevel.equals("") && this.isoPlayer.isShowAdminTag()) {
				string = (String)this.namesPrefix.get(this.isoPlayer.accessLevel) + string;
			}

			if (boolean1 && !this.isoPlayer.isSafety() && ServerOptions.instance.ShowSafety.getValue()) {
				string = string + this.namePvpSuffix;
			}

			if (this.isoPlayer.isSpeek && !this.isoPlayer.isVoiceMute) {
				string = this.voiceSuffix + string;
			}

			if (this.isoPlayer.isVoiceMute) {
				string = this.voiceMuteSuffix + string;
			}

			BaseVehicle baseVehicle = IsoCamera.CamCharacter == this.isoPlayer ? this.isoPlayer.getNearVehicle() : null;
			if (this.getVehicle() == null && baseVehicle != null && (this.isoPlayer.getInventory().haveThisKeyId(baseVehicle.getKeyId()) != null || baseVehicle.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue())) {
				Integer[] integerArray = Color.HSBtoRGB(baseVehicle.colorHue, baseVehicle.colorSaturation * 0.5F, baseVehicle.colorValue);
				string = this.nameCarKeySuffix + "," + integerArray[0] + "," + integerArray[1] + "," + integerArray[2] + "]" + string;
			}

			if (!string.equals(this.userName.getOriginal())) {
				this.userName.ReadString(string);
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
		int int1 = IsoCamera.frameState.playerIndex;
		float float1 = this.x;
		float float2 = this.y;
		if (GameClient.bClient && this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
			float1 = this.bx;
			float2 = this.by;
		}

		if (this.sx == 0 && this.def != null) {
			this.sx = (int)IsoUtils.XToScreen(float1 + this.def.offX, float2 + this.def.offY, this.z + this.def.offZ, 0);
			this.sy = (int)IsoUtils.YToScreen(float1 + this.def.offX, float2 + this.def.offY, this.z + this.def.offZ, 0);
			this.sx = (int)((float)this.sx - (this.offsetX - 8.0F));
			this.sy = (int)((float)this.sy - (this.offsetY - 60.0F));
		}

		float float3;
		float float4;
		float float5;
		if (this.hasInitTextObjects && this.isoPlayer != null || this.chatElement.getHasChatToDisplay()) {
			float3 = IsoUtils.XToScreen(float1, float2, this.getZ(), 0);
			float4 = IsoUtils.YToScreen(float1, float2, this.getZ(), 0);
			float3 = float3 - IsoCamera.getOffX() - this.offsetX;
			float4 = float4 - IsoCamera.getOffY() - this.offsetY;
			float4 -= (float)(128 / (2 / Core.TileScale));
			float3 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
			float4 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
			this.canSeeCurrent = false;
			this.drawUserName = false;
			if (this.isoPlayer != null && (this == IsoCamera.frameState.CamCharacter || this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(int1))) {
				this.canSeeCurrent = true;
				if (GameClient.bClient && this.userName != null) {
					this.drawUserName = true;
					this.updateUserName();
				}

				if (!GameClient.bClient && this.isoPlayer != null && this.isoPlayer.getVehicle() == null) {
					String string = "";
					BaseVehicle baseVehicle = this.isoPlayer.getNearVehicle();
					if (this.getVehicle() == null && baseVehicle != null && (this.isoPlayer.getInventory().haveThisKeyId(baseVehicle.getKeyId()) != null || baseVehicle.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue())) {
						Integer[] integerArray = Color.HSBtoRGB(baseVehicle.colorHue, baseVehicle.colorSaturation * 0.5F, baseVehicle.colorValue);
						string = this.nameCarKeySuffix + "," + integerArray[0] + "," + integerArray[1] + "," + integerArray[2] + "]";
					}

					if (!string.equals("")) {
						this.userName.ReadString(string);
						this.drawUserName = true;
					}
				}
			}

			if (this.isoPlayer != null && this.hasInitTextObjects && (this.playerIsSelf() || this.canSeeCurrent)) {
				if (this.canSeeCurrent && this.drawUserName) {
					float4 -= (float)this.userName.getHeight();
					this.userName.AddBatchedDraw((double)((int)float3), (double)((int)float4), true);
				}

				if (this.playerIsSelf()) {
					ActionProgressBar actionProgressBar = UIManager.getProgressBar((double)IsoPlayer.getPlayerIndex());
					if (actionProgressBar != null && actionProgressBar.isVisible()) {
						float4 -= (float)(actionProgressBar.getHeight().intValue() + 2);
					}
				}

				if (this.playerIsSelf() && this.haloNote != null && this.haloNote.getInternalClock() > 0.0F) {
					float5 = this.haloNote.getInternalClock() / this.haloDispTime;
					float4 -= (float)(this.haloNote.getHeight() + 2);
					this.haloNote.AddBatchedDraw((double)((int)float3), (double)((int)float4), false, float5);
				}
			}

			boolean boolean1 = false;
			if (IsoPlayer.instance != this && this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && this.equipedRadio.getDeviceData().getHeadphoneType() >= 0) {
				boolean1 = true;
			}

			boolean boolean2 = GameClient.bClient && IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
			if (!this.invisible || this == IsoCamera.frameState.CamCharacter || boolean2) {
				this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)float3, (int)float4, boolean1);
			}
		}

		if (Core.bDebug && DebugOptions.instance.AimConeRender.getValue()) {
			this.debugAim();
		}

		if (this.inventory != null) {
			for (int int2 = 0; int2 < this.inventory.Items.size(); ++int2) {
				InventoryItem inventoryItem = (InventoryItem)this.inventory.Items.get(int2);
				if (inventoryItem instanceof IUpdater) {
					((IUpdater)inventoryItem).renderlast();
				}
			}

			if (Core.bDebug) {
			}

			if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.pfb2 != null) {
				this.pfb2.render();
			}

			if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRadius.getValue()) {
				float3 = 0.3F;
				float4 = 1.0F;
				float5 = 1.0F;
				float float6 = 1.0F;
				double double1 = (double)this.x + (double)float3 * Math.cos(Math.toRadians(0.0));
				double double2 = (double)this.y + (double)float3 * Math.sin(Math.toRadians(0.0));
				for (int int3 = 1; int3 <= 16; ++int3) {
					double double3 = (double)this.x + (double)float3 * Math.cos(Math.toRadians((double)(int3 * 360 / 16)));
					double double4 = (double)this.y + (double)float3 * Math.sin(Math.toRadians((double)(int3 * 360 / 16)));
					LineDrawer.addLine((float)double1, (float)double2, this.z, (float)double3, (float)double4, this.z, float4, float5, float6, (String)null, true);
					double1 = double3;
					double2 = double4;
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

	private void setWeaponHitList(HandWeapon handWeapon) {
		IsoGameCharacter gameCharacter = this;
		int int1 = handWeapon.getMaxHitCount();
		HitList.clear();
		int int2 = 0;
		boolean boolean1 = false;
		int int3;
		IsoMovingObject movingObject;
		if (int2 < int1) {
			for (int3 = 0; int3 < gameCharacter.getCell().getObjectList().size(); ++int3) {
				movingObject = (IsoMovingObject)gameCharacter.getCell().getObjectList().get(int3);
				if (movingObject != gameCharacter && (!(movingObject instanceof IsoPlayer) || !((IsoPlayer)movingObject).accessLevel.equals("admin") && ServerOptions.instance.PVP.getValue() && (!ServerOptions.instance.SafetySystem.getValue() || !gameCharacter.isSafety() || !((IsoGameCharacter)movingObject).isSafety())) && (!(movingObject instanceof IsoGameCharacter) || !((IsoGameCharacter)movingObject).godMod) && (movingObject.isShootable() && !movingObject.isOnFloor() && !boolean1 || movingObject.isShootable() && movingObject.isOnFloor() && boolean1) && gameCharacter.IsAttackRange(handWeapon, movingObject.getX(), movingObject.getY(), movingObject.getZ()) && !handWeapon.isDirectional()) {
					Vector2 vector2 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
					Vector2 vector22 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
					vector22.x -= vector2.x;
					vector22.y -= vector2.y;
					Vector2 vector23 = gameCharacter.getAngle();
					gameCharacter.DirectionFromVector(vector23);
					vector22.normalize();
					float float1 = vector22.dot(vector23);
					if (float1 > 1.0F) {
						float1 = 1.0F;
					}

					if (float1 < -1.0F) {
						float1 = -1.0F;
					}

					float float2 = handWeapon.getMinAngle();
					if (handWeapon.isRanged()) {
						float2 -= handWeapon.getAimingPerkMinAngleModifier() * (float)(gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2);
					}

					if (float1 >= float2 && float1 <= handWeapon.getMaxAngle()) {
						float float3 = movingObject.DistToProper(gameCharacter);
						HitList.add(new IsoGameCharacter.HitInfo(movingObject, float1, float3));
						++int2;
					}
				}
			}

			Collections.sort(HitList, new IsoGameCharacter.HitInfoComparator(gameCharacter, handWeapon.getMaxRange(gameCharacter) * handWeapon.getRangeMod(gameCharacter)));
			if (handWeapon.isPiercingBullets()) {
				ArrayList arrayList = new ArrayList();
				double double1 = 0.0;
				for (int int4 = 0; int4 < HitList.size(); ++int4) {
					IsoMovingObject movingObject2 = ((IsoGameCharacter.HitInfo)HitList.get(int4)).object;
					double double2 = (double)(gameCharacter.getX() - movingObject2.getX());
					double double3 = (double)(-(gameCharacter.getY() - movingObject2.getY()));
					double double4 = Math.atan2(double3, double2);
					if (double4 < 0.0) {
						double4 = Math.abs(double4);
					} else {
						double4 = 6.283185307179586 - double4;
					}

					if (int4 == 0) {
						double1 = Math.toDegrees(double4);
						arrayList.add(HitList.get(int4));
					} else {
						double double5 = Math.toDegrees(double4);
						if (Math.abs(double1 - double5) < 1.0) {
							arrayList.add(HitList.get(int4));
							break;
						}
					}
				}

				HitList = arrayList;
			} else {
				while (HitList.size() > int1) {
					HitList.remove(HitList.size() - 1);
				}
			}
		}

		for (int3 = 0; int3 < HitList.size(); ++int3) {
			movingObject = ((IsoGameCharacter.HitInfo)HitList.get(int3)).object;
			int int5 = handWeapon.getHitChance();
			int5 = (int)((float)int5 + handWeapon.getAimingPerkHitChanceModifier() * (float)gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming));
			float float4 = IsoUtils.DistanceTo(movingObject.getX(), movingObject.getY(), gameCharacter.getX(), gameCharacter.getY());
			if (handWeapon.getMinRangeRanged() > 0.0F) {
				if (float4 < handWeapon.getMinRangeRanged()) {
					int5 -= 50;
				}
			} else if ((double)float4 < 1.5 && handWeapon.isRanged()) {
				int5 += 15;
			}

			if (handWeapon.isRanged() && gameCharacter.getBeenMovingFor() > (float)(handWeapon.getAimingTime() + gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) * 2)) {
				int5 = (int)((float)int5 - (gameCharacter.getBeenMovingFor() - (float)(handWeapon.getAimingTime() + gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) * 2)));
			}

			if (gameCharacter.HasTrait("Marksman")) {
				int5 += 20;
			}

			if (int5 < 10) {
				int5 = 10;
			}

			if (int5 > 100 || !handWeapon.isRanged()) {
				int5 = 100;
			}

			((IsoGameCharacter.HitInfo)HitList.get(int3)).chance = int5;
		}
	}

	private void debugAim() {
		if (this == IsoPlayer.instance) {
			IsoPlayer player = (IsoPlayer)this;
			HandWeapon handWeapon = player.getUseHandWeapon();
			if (player.IsAiming() && handWeapon != null && handWeapon.isRanged()) {
				float float1 = handWeapon.getMaxRange(player) * handWeapon.getRangeMod(player);
				Vector2 vector2 = this.getAngle();
				float float2 = this.x + vector2.x * float1;
				float float3 = this.y + vector2.y * float1;
				float float4 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
				float float5 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
				float float6 = IsoUtils.XToScreenExact(float2, float3, this.z, 0);
				float float7 = IsoUtils.YToScreenExact(float2, float3, this.z, 0);
				LineDrawer.drawLine(float4, float5, float6, float7, 1.0F, 1.0F, 1.0F, 0.5F, 1);
				float float8 = handWeapon.getMinAngle();
				float8 -= handWeapon.getAimingPerkMinAngleModifier() * (float)(this.getPerkLevel(PerkFactory.Perks.Aiming) / 2);
				Vector2 vector22 = tempVector2_1.set(vector2.y, vector2.x);
				vector22.setLengthAndDirection(vector22.getDirection() - (1.0F - float8) * 3.1415927F, 1.0F);
				float2 = this.x + vector22.x * float1;
				float3 = this.y + vector22.y * float1;
				float6 = IsoUtils.XToScreenExact(float2, float3, this.z, 0);
				float7 = IsoUtils.YToScreenExact(float2, float3, this.z, 0);
				LineDrawer.drawLine(float4, float5, float6, float7, 1.0F, 1.0F, 1.0F, 0.5F, 1);
				vector22.set(vector2.y, vector2.x);
				vector22.setLengthAndDirection(vector22.getDirection() + (1.0F - float8) * 3.1415927F, 1.0F);
				float2 = this.x + vector22.x * float1;
				float3 = this.y + vector22.y * float1;
				float6 = IsoUtils.XToScreenExact(float2, float3, this.z, 0);
				float7 = IsoUtils.YToScreenExact(float2, float3, this.z, 0);
				LineDrawer.drawLine(float4, float5, float6, float7, 1.0F, 1.0F, 1.0F, 0.5F, 1);
				this.setWeaponHitList(handWeapon);
				for (int int1 = 0; int1 < HitList.size(); ++int1) {
					IsoMovingObject movingObject = ((IsoGameCharacter.HitInfo)HitList.get(int1)).object;
					int int2 = ((IsoGameCharacter.HitInfo)HitList.get(int1)).chance;
					float float9 = 1.0F - (float)int2 / 100.0F;
					float float10 = 1.0F - float9;
					float float11 = Math.max(0.2F, 1.0F - (float)int2 / 100.0F) / 2.0F;
					float4 = IsoUtils.XToScreenExact(movingObject.x + 1.0F - float11, movingObject.y + float11, this.z, 0);
					float5 = IsoUtils.YToScreenExact(movingObject.x + 1.0F - float11, movingObject.y + float11, this.z, 0);
					float6 = IsoUtils.XToScreenExact(movingObject.x + 1.0F - float11, movingObject.y + 1.0F - float11, this.z, 0);
					float7 = IsoUtils.YToScreenExact(movingObject.x + 1.0F - float11, movingObject.y + 1.0F - float11, this.z, 0);
					float float12 = IsoUtils.XToScreenExact(movingObject.x + float11, movingObject.y + 1.0F - float11, this.z, 0);
					float float13 = IsoUtils.YToScreenExact(movingObject.x + float11, movingObject.y + 1.0F - float11, this.z, 0);
					float float14 = IsoUtils.XToScreenExact(movingObject.x + float11, movingObject.y + float11, this.z, 0);
					float float15 = IsoUtils.YToScreenExact(movingObject.x + float11, movingObject.y + float11, this.z, 0);
					SpriteRenderer.instance.renderPoly((int)float4, (int)float5, (int)float6, (int)float7, (int)float12, (int)float13, (int)float14, (int)float15, float9, float10, 0.0F, 1.0F);
				}
			}
		}
	}

	public void rendertalk(float float1) {
		if (this.Speaking) {
			float float2 = (float)this.sx;
			float float3 = (float)this.sy;
			float2 = (float)((int)float2);
			float3 = (float)((int)float3);
			float2 -= (float)((int)IsoCamera.getOffX());
			float3 -= (float)((int)IsoCamera.getOffY());
			float2 += 8.0F;
			float3 += 10.0F;
			float3 += float1;
			if (this.sayLine != null) {
				TextManager.instance.DrawStringCentre(UIFont.Dialogue, (double)((int)float2), (double)((int)float3), this.sayLine, (double)this.SpeakColour.r, (double)this.SpeakColour.g, (double)this.SpeakColour.b, (double)this.SpeakColour.a);
			}
		}
	}

	public void setCraftingByIndex(int int1, InventoryItem inventoryItem) {
		if (int1 == 0) {
			this.craftIngredient1 = inventoryItem;
		}

		if (int1 == 1) {
			this.craftIngredient2 = inventoryItem;
		}

		if (int1 == 2) {
			this.craftIngredient3 = inventoryItem;
		}

		if (int1 == 3) {
			this.craftIngredient4 = inventoryItem;
		}
	}

	public void setDefaultState() {
		this.stateMachine.changeState(this.defaultState);
	}

	public void SetOnFire() {
		if (!this.OnFire) {
			this.OnFire = true;
			float float1 = (float)Core.TileScale;
			this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, (int)(-(this.offsetX + (float)RENDER_OFFSET_X * float1)) + (8 - Rand.Next(16)), (int)(-(this.offsetY + (float)RENDER_OFFSET_Y * float1)) + (int)((float)(10 + Rand.Next(20)) * float1), true, 0, false, 0.7F, IsoFireManager.FireTintMod);
			IsoFireManager.AddBurningCharacter(this);
			int int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
			((BodyPart)this.getBodyDamage().getBodyParts().get(int1)).setBurned();
			if (float1 == 2.0F) {
				int int2 = this.AttachedAnimSprite.size() - 1;
				((IsoSpriteInstance)this.AttachedAnimSprite.get(int2)).setScale(float1, float1);
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
				IsoPlayer player = (IsoPlayer)this;
				if (player.isLocalPlayer()) {
					this.StopBurning();
				} else {
					GameClient.sendStopFire((IsoGameCharacter)player);
				}
			}

			if (this instanceof IsoZombie) {
				IsoZombie zombie = (IsoZombie)this;
				GameClient.sendStopFire((IsoGameCharacter)zombie);
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

	public void Throw(HandWeapon handWeapon) {
		if (this instanceof IsoPlayer && ((IsoPlayer)this).getJoypadBind() != -1) {
			Vector2 vector2 = tempo.set(this.angle);
			vector2.setLength(handWeapon.getMaxRange());
			this.attackTargetSquare = this.getCell().getGridSquare((double)(this.getX() + vector2.getX()), (double)(this.getY() + vector2.getY()), (double)this.getZ());
		}

		float float1 = (float)this.attackTargetSquare.getX() - this.getX();
		if (float1 > 0.0F) {
			if ((float)this.attackTargetSquare.getX() - this.getX() > handWeapon.getMaxRange()) {
				float1 = handWeapon.getMaxRange();
			}
		} else if ((float)this.attackTargetSquare.getX() - this.getX() < -handWeapon.getMaxRange()) {
			float1 = -handWeapon.getMaxRange();
		}

		float float2 = (float)this.attackTargetSquare.getY() - this.getY();
		if (float2 > 0.0F) {
			if ((float)this.attackTargetSquare.getY() - this.getY() > handWeapon.getMaxRange()) {
				float2 = handWeapon.getMaxRange();
			}
		} else if ((float)this.attackTargetSquare.getY() - this.getY() < -handWeapon.getMaxRange()) {
			float2 = -handWeapon.getMaxRange();
		}

		new IsoMolotovCocktail(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6F, float1 * 0.4F, float2 * 0.4F, handWeapon, this);
	}

	public void smashWindow(IsoWindow window) {
		if (!window.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
			this.StateMachineParams.clear();
			this.StateMachineParams.put(0, window);
			this.StateMachineParams.put(1, "WindowSmash");
			this.StateMachineParams.put(4, true);
			this.getStateMachine().changeState(OpenWindowState.instance());
		}
	}

	public void openWindow(IsoWindow window) {
		if (!window.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
			this.StateMachineParams.clear();
			this.StateMachineParams.put(0, window);
			this.getStateMachine().changeState(OpenWindowState.instance());
		}
	}

	public void climbThroughWindow(IsoWindow window) {
		if (window.canClimbThrough(this)) {
			float float1 = this.x - (float)((int)this.x);
			float float2 = this.y - (float)((int)this.y);
			byte byte1 = 0;
			byte byte2 = 0;
			if (window.getX() > this.x && !window.north) {
				byte1 = -1;
			}

			if (window.getY() > this.y && window.north) {
				byte2 = -1;
			}

			this.x = window.getX() + float1 + (float)byte1;
			this.y = window.getY() + float2 + (float)byte2;
			this.StateMachineParams.clear();
			this.StateMachineParams.put(0, window);
			this.getStateMachine().changeState(ClimbThroughWindowState.instance());
		}
	}

	public void climbThroughWindow(IsoWindow window, Integer integer) {
		if (window.canClimbThrough(this)) {
			this.StateMachineParams.clear();
			this.StateMachineParams.put(0, window);
			this.StateMachineParams.put(1, integer);
			this.getStateMachine().changeState(ClimbThroughWindowState.instance());
		}
	}

	public void climbThroughWindowFrame(IsoObject object) {
		this.StateMachineParams.clear();
		this.StateMachineParams.put(0, object);
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

	public boolean canClimbSheetRope(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			for (int int1 = square.getZ(); square != null; square = this.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() + 1)) {
				if (!IsoWindow.isSheetRopeHere(square)) {
					return false;
				}

				if (!IsoWindow.canClimbHere(square)) {
					return false;
				}

				if (square.TreatAsSolidFloor() && square.getZ() > int1) {
					return false;
				}

				if (IsoWindow.isTopOfSheetRopeHere(square)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean canClimbDownSheetRope(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			for (int int1 = square.getZ(); square != null; square = this.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() - 1)) {
				if (!IsoWindow.isSheetRopeHere(square)) {
					return false;
				}

				if (!IsoWindow.canClimbHere(square)) {
					return false;
				}

				if (square.TreatAsSolidFloor()) {
					return square.getZ() < int1;
				}
			}

			return false;
		}
	}

	public void satOnChair(IsoObject object) {
		this.StateMachineParams.clear();
		this.StateMachineParams.put(0, object);
		this.getStateMachine().changeState(SatChairState.instance());
	}

	public void climbThroughWindow(IsoThumpable thumpable) {
		if (!thumpable.isBarricaded()) {
			float float1 = this.x - (float)((int)this.x);
			float float2 = this.y - (float)((int)this.y);
			byte byte1 = 0;
			byte byte2 = 0;
			if (thumpable.getX() > this.x && !thumpable.north) {
				byte1 = -1;
			}

			if (thumpable.getY() > this.y && thumpable.north) {
				byte2 = -1;
			}

			this.x = thumpable.getX() + float1 + (float)byte1;
			this.y = thumpable.getY() + float2 + (float)byte2;
			this.StateMachineParams.clear();
			this.StateMachineParams.put(0, thumpable);
			this.getStateMachine().changeState(ClimbThroughWindowState.instance());
		}
	}

	public void climbThroughWindow(IsoThumpable thumpable, Integer integer) {
		if (!thumpable.isBarricaded()) {
			this.StateMachineParams.clear();
			this.StateMachineParams.put(0, thumpable);
			this.StateMachineParams.put(1, integer);
			this.getStateMachine().changeState(ClimbThroughWindowState.instance());
		}
	}

	public boolean isAboveTopOfStairs() {
		if (this.z != 0.0F && !((double)(this.z - (float)((int)this.z)) > 0.01) && (this.current == null || !this.current.TreatAsSolidFloor())) {
			IsoGridSquare square = this.getCell().getGridSquare((double)this.x, (double)this.y, (double)(this.z - 1.0F));
			return square != null && (square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsTW));
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

				if ((double)this.getStats().getFatigue() > 0.8) {
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

			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (!this.current.isCanSee(int1)) {
					int int2 = this.timeTillForgetLocation[int1]--;
					if (this.timeTillForgetLocation[int1] < 0) {
						this.SpottedSinceAlphaZero[int1] = false;
					}

					if (!(this instanceof IsoPlayer)) {
						this.targetAlpha[int1] = 0.0F;
					}
				} else if (testPlayerSpotInDarkness.Check()) {
					this.TestIfSeen(int1);
				}
			}

			this.llx = this.getLx();
			this.lly = this.getLy();
			this.setLx(this.getX());
			this.setLy(this.getY());
			this.setLz(this.getZ());
			float float1 = GameServer.bServer ? 10.0F : (float)PerformanceSettings.LockFPS;
			float float2 = 0.125F * (30.0F / float1);
			IsoDirections directions = IsoDirections.Max;
			boolean boolean1;
			if (this.z > 0.0F) {
				if (!(this instanceof IsoZombie) && this.isClimbing()) {
					if (this.current.Is(IsoFlagType.climbSheetW) || this.current.Is(IsoFlagType.climbSheetTopW)) {
						directions = IsoDirections.W;
					}

					if (this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetTopE)) {
						directions = IsoDirections.E;
					}

					if (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetTopN)) {
						directions = IsoDirections.N;
					}

					if (this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetTopS)) {
						directions = IsoDirections.S;
					}
				}

				if (this.bClimbing) {
					float2 = 0.0F;
				}

				if (this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance()) {
					float2 = 0.0F;
				}

				this.lastFallSpeed = float2;
				boolean1 = false;
				if (this.lastFallSpeed < 0.0F && float2 >= 0.0F) {
					float2 = this.lastFallSpeed;
					boolean1 = true;
				}

				if (!this.current.TreatAsSolidFloor()) {
					if (directions != IsoDirections.Max) {
						this.dir = directions;
					}

					this.fallTime += 6 + Rand.Next(3);
					if (directions != IsoDirections.Max) {
						this.fallTime = 0;
					}

					if (this.fallTime < 20 && this.isAboveTopOfStairs()) {
						this.fallTime = 0;
					}

					this.setZ(this.getZ() - float2);
				} else if (!(this.getZ() > (float)((int)this.getZ())) && !(float2 < 0.0F)) {
					this.DoLand();
					this.fallTime = 0;
					this.bFalling = false;
				} else {
					if (directions != IsoDirections.Max) {
						this.dir = directions;
					}

					if (!this.current.Has(IsoObjectType.stairsBN) && !this.current.Has(IsoObjectType.stairsTN) && !this.current.Has(IsoObjectType.stairsMN) && !this.current.Has(IsoObjectType.stairsBW) && !this.current.Has(IsoObjectType.stairsMW) && !this.current.Has(IsoObjectType.stairsTW)) {
						if (!this.wasOnStairs) {
							this.fallTime += 6 + Rand.Next(3);
							if (directions != IsoDirections.Max) {
								this.fallTime = 0;
							}

							this.setZ(this.getZ() - float2);
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

				if (boolean1) {
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

			int int3;
			int int4;
			if (!(this instanceof IsoZombie)) {
				Stats stats;
				if (!FrameLoader.bClient) {
					if (this.HasTrait("Agoraphobic") && this.getCurrentSquare().getRoom() == null) {
						stats = this.stats;
						stats.Panic += 0.5F * (GameTime.getInstance().getMultiplier() / 1.6F);
					}

					if (this.HasTrait("Claustophobic") && this.getCurrentSquare().getRoom() != null) {
						float float3 = 1.0F;
						int4 = this.getCurrentSquare().getRoom().def.getH() * this.getCurrentSquare().getRoom().def.getW();
						float3 = 1.0F - (float)int4 / 70.0F;
						if (float3 < 0.0F) {
							float3 = 0.0F;
						}

						float float4 = 0.6F * float3 * (GameTime.getInstance().getMultiplier() / 1.6F);
						if (float4 > 0.6F) {
							float4 = 0.6F;
						}

						stats = this.stats;
						stats.Panic += float4;
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
						stats = this.stats;
						stats.Panic -= 0.6F * (GameTime.getInstance().getMultiplier() / 1.6F);
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
						stats = this.stats;
						stats.fatigue += 0.0016666667F * this.SleepingTabletDelta * (GameTime.getInstance().getMultiplier() / 1.6F);
					} else {
						this.SleepingTabletDelta = 0.0F;
					}

					int3 = this.Moodles.getMoodleLevel(MoodleType.Panic);
					if (int3 == 2) {
						stats = this.stats;
						stats.Sanity -= 3.2E-7F;
					} else if (int3 == 3) {
						stats = this.stats;
						stats.Sanity -= 4.8000004E-7F;
					} else if (int3 == 4) {
						stats = this.stats;
						stats.Sanity -= 8.0E-7F;
					} else if (int3 == 0) {
						stats = this.stats;
						stats.Sanity += 1.0E-7F;
					}

					int4 = this.Moodles.getMoodleLevel(MoodleType.Tired);
					if (int4 == 4) {
						stats = this.stats;
						stats.Sanity -= 2.0E-6F;
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
					BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
					boolean1 = baseAction.valid();
					if (boolean1) {
						baseAction.update();
					}

					if (!boolean1 || baseAction.finished() || baseAction.forceComplete || baseAction.forceStop) {
						if (baseAction.finished() || baseAction.forceComplete) {
							baseAction.perform();
						}

						if (baseAction.finished() || baseAction.forceComplete || baseAction.forceStop || !boolean1) {
							if (baseAction.forceStop || !boolean1) {
								baseAction.stop();
							}

							this.CharacterActions.removeElement(baseAction);
							if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
								UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
							}
						}
					}
				}

				for (int3 = 0; int3 < this.EnemyList.size(); ++int3) {
					IsoGameCharacter gameCharacter = (IsoGameCharacter)this.EnemyList.get(int3);
					if (gameCharacter.Health < 0.0F) {
						this.EnemyList.remove(gameCharacter);
						--int3;
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
					for (int3 = 0; int3 < this.inventory.Items.size(); ++int3) {
						InventoryItem inventoryItem = (InventoryItem)this.inventory.Items.get(int3);
						if (inventoryItem instanceof IUpdater) {
							((IUpdater)inventoryItem).update();
						}
					}
				}

				this.LastZombieKills = this.ZombieKills;
				if (this.AttachedAnimSprite != null && this.AttachedAnimSpriteActual != null) {
					int3 = this.AttachedAnimSprite.size();
					for (int4 = 0; int4 < int3; ++int4) {
						IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int4);
						IsoSprite sprite = (IsoSprite)this.AttachedAnimSpriteActual.get(int4);
						spriteInstance.update();
						spriteInstance.Frame += spriteInstance.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
						if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
							spriteInstance.Frame = 0.0F;
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

	public void DoFloorSplat(IsoGridSquare square, String string, boolean boolean1, float float1, float float2) {
		if (square != null) {
			square.DirtySlice();
			IsoObject object = null;
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object2 = (IsoObject)square.getObjects().get(int1);
				if (object2.sprite != null && object2.sprite.getProperties().Is(IsoFlagType.solidfloor) && object == null) {
					object = object2;
				}
			}

			if (object != null && object.sprite != null && (object.sprite.getProperties().Is(IsoFlagType.vegitation) || object.sprite.getProperties().Is(IsoFlagType.solidfloor))) {
				IsoSprite sprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 0);
				if (sprite == null) {
					return;
				}

				if (object.AttachedAnimSprite.size() > 7) {
					return;
				}

				IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(sprite);
				object.AttachedAnimSprite.add(spriteInstance);
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).Flip = boolean1;
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).tintr = 0.5F + (float)Rand.Next(100) / 2000.0F;
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).tintg = 0.7F + (float)Rand.Next(300) / 1000.0F;
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).tintb = 0.7F + (float)Rand.Next(300) / 1000.0F;
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).SetAlpha(0.4F * float2 * 0.6F);
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).SetTargetAlpha(0.4F * float2 * 0.6F);
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).offZ = -float1;
				float float3 = 0.0F;
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).offX = float3;
			}
		}
	}

	void DoSplat(IsoGridSquare square, String string, boolean boolean1, IsoFlagType flagType, float float1, float float2, float float3) {
		if (square != null) {
			square.DoSplat(string, boolean1, flagType, float1, float2, float3);
		}
	}

	InventoryItem FindAndReturn(String string, InventoryItem inventoryItem) {
		if (inventoryItem == null) {
			return null;
		} else if (string == null) {
			return inventoryItem;
		} else {
			return string.equals(inventoryItem.getType()) ? inventoryItem : null;
		}
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoCamera.CamCharacter != IsoPlayer.instance && Core.bDebug) {
			IsoCamera.CamCharacter = this;
		}

		return super.onMouseLeftClick(int1, int2);
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
					Stats stats;
					if (this.stats.Tripping) {
						stats = this.stats;
						stats.TrippingRotAngle += 0.06F;
					} else {
						stats = this.stats;
						stats.TrippingRotAngle += 0.0F;
					}

					float float1 = 1.0F;
					if (this.HasTrait("HighThirst")) {
						float1 = (float)((double)float1 * 2.0);
					}

					if (this.HasTrait("LowThirst")) {
						float1 = (float)((double)float1 * 0.5);
					}

					if (this == IsoPlayer.instance && !IsoPlayer.instance.GhostMode) {
						if (this == IsoPlayer.instance && this.Asleep) {
							stats = this.stats;
							stats.thirst = (float)((double)stats.thirst + ZomboidGlobals.ThirstSleepingIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)float1);
						} else {
							stats = this.stats;
							stats.thirst = (float)((double)stats.thirst + ZomboidGlobals.ThirstIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * this.getRunningThirstReduction() * (double)GameTime.instance.getDeltaMinutesPerDay());
						}

						if (this.stats.thirst > 1.0F) {
							this.stats.thirst = 1.0F;
						}
					}

					this.autoDrink();
					float float2 = 1.0F - this.stats.hunger;
					if (this.HasTrait("HeartyAppitite")) {
						float2 *= 1.5F;
					}

					if (this.HasTrait("LightEater")) {
						float2 *= 0.75F;
					}

					float float3 = 1.0F;
					if (this.HasTrait("Cowardly")) {
						float3 = 2.0F;
					}

					if (this.HasTrait("Brave")) {
						float3 = 0.3F;
					}

					if (this.stats.Panic > 100.0F) {
						this.stats.Panic = 100.0F;
					}

					stats = this.stats;
					stats.stress = (float)((double)stats.stress + (double)WorldSoundManager.instance.getStressFromSounds((int)this.getX(), (int)this.getY(), (int)this.getZ()) * ZomboidGlobals.StressFromSoundsMultiplier);
					if (this.BodyDamage.getNumPartsBitten() > 0) {
						stats = this.stats;
						stats.stress = (float)((double)stats.stress + ZomboidGlobals.StressFromBiteOrScratch * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
					}

					if (this.BodyDamage.getNumPartsScratched() > 0) {
						stats = this.stats;
						stats.stress = (float)((double)stats.stress + ZomboidGlobals.StressFromBiteOrScratch * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
					}

					if (this.BodyDamage.IsInfected() || this.BodyDamage.IsFakeInfected()) {
						stats = this.stats;
						stats.stress = (float)((double)stats.stress + ZomboidGlobals.StressFromBiteOrScratch * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
					}

					if (this.HasTrait("Brooding")) {
						stats = this.stats;
						stats.Anger = (float)((double)stats.Anger - ZomboidGlobals.AngerDecrease * ZomboidGlobals.BroodingAngerDecreaseMultiplier * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
					} else {
						stats = this.stats;
						stats.Anger = (float)((double)stats.Anger - ZomboidGlobals.AngerDecrease * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
					}

					if (this.stats.Anger > 1.0F) {
						this.stats.Anger = 1.0F;
					}

					if (this.stats.Anger < 0.0F) {
						this.stats.Anger = 0.0F;
					}

					float float4;
					float float5;
					float float6;
					float float7;
					if (IsoPlayer.instance == this && IsoPlayer.instance.Asleep) {
						float4 = 2.0F;
						if (IsoPlayer.allPlayersAsleep()) {
							float4 *= GameTime.instance.getDeltaMinutesPerDay();
						}

						stats = this.stats;
						stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)GameTime.instance.getMultiplier() * (double)float4);
						if (this.stats.endurance > 1.0F) {
							this.stats.endurance = 1.0F;
						}

						if (this.stats.fatigue > 0.0F) {
							float5 = 1.0F;
							if (this.HasTrait("Insomniac")) {
								float5 *= 0.5F;
							}

							float7 = 1.0F;
							if ("goodBed".equals(this.getBedType())) {
								float7 = 1.1F;
							}

							if ("badBed".equals(this.getBedType())) {
								float7 = 0.9F;
							}

							float6 = 1.0F / GameTime.instance.getMinutesPerDay() / 60.0F * GameTime.instance.getMultiplier() / 2.0F;
							this.timeOfSleep += float6;
							if (this.timeOfSleep > this.delayToActuallySleep) {
								if (this.stats.fatigue <= 0.3F) {
									stats = this.stats;
									stats.fatigue -= float6 / 7.0F * 0.3F * float5 * float7;
								} else {
									stats = this.stats;
									stats.fatigue -= float6 / 5.0F * 0.7F * float5 * float7;
								}
							}

							if (this.stats.fatigue < 0.0F) {
								this.stats.fatigue = 0.0F;
							}
						}

						if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
							stats = this.stats;
							stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
						} else {
							stats = this.stats;
							stats.hunger += (float)(ZomboidGlobals.HungerIncreaseWhenWellFed * SandboxOptions.instance.getStatsDecreaseMultiplier() * ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
						}

						if (this.ForceWakeUpTime == 0.0F) {
							this.ForceWakeUpTime = 9.0F;
						}

						float5 = GameTime.getInstance().getTimeOfDay();
						float7 = GameTime.getInstance().getLastTimeOfDay();
						if (float7 > float5) {
							if (float7 < this.ForceWakeUpTime) {
								float5 += 24.0F;
							} else {
								float7 -= 24.0F;
							}
						}

						boolean boolean1 = float5 >= this.ForceWakeUpTime && float7 < this.ForceWakeUpTime;
						if (IsoPlayer.instance.getAsleepTime() > 16.0F) {
							boolean1 = true;
						}

						if (GameClient.bClient || IsoPlayer.numPlayers > 1) {
							boolean1 = boolean1 || IsoPlayer.instance.pressedAim() || IsoPlayer.instance.pressedMovement();
						}

						if (this.ForceWakeUp) {
							boolean1 = true;
						}

						if (this.Asleep && boolean1) {
							this.ForceWakeUp = false;
							SleepingEvent.instance.wakeUp(this);
							this.ForceWakeUpTime = -1.0F;
							if (GameClient.bClient) {
								GameClient.instance.sendPlayer((IsoPlayer)this);
							}

							((IsoPlayer)this).dirtyRecalcGridStackTime = 20.0F;
						}
					} else {
						stats = this.stats;
						stats.stress = (float)((double)stats.stress - ZomboidGlobals.StressReduction * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
						float4 = 1.0F - this.stats.endurance;
						if (float4 < 0.3F) {
							float4 = 0.3F;
						}

						float5 = 1.0F;
						if (this.HasTrait("NeedsLessSleep")) {
							float5 = 0.7F;
						}

						if (this.HasTrait("NeedsMoreSleep")) {
							float5 = 1.3F;
						}

						double double1 = SandboxOptions.instance.getStatsDecreaseMultiplier();
						if (double1 < 1.0) {
							double1 = 1.0;
						}

						stats = this.stats;
						stats.fatigue = (float)((double)stats.fatigue + ZomboidGlobals.FatigueIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float4 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)float5);
						if ((!(this instanceof IsoPlayer) || !((IsoPlayer)this).IsRunning()) && this.getCurrentState() != SwipeStatePlayer.instance()) {
							if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
								stats = this.stats;
								stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
							} else {
								stats = this.stats;
								stats.hunger = (float)((double)stats.hunger + (double)((float)ZomboidGlobals.HungerIncreaseWhenWellFed) * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
							}
						} else if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
							stats = this.stats;
							stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncreaseWhenExercise / 3.0 * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
						} else {
							stats = this.stats;
							stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncreaseWhenExercise * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
						}

						if (this.getCurrentSquare() == this.getLastSquare() && !this.isReading()) {
							stats = this.stats;
							stats.idleboredom += 5.0E-5F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
							stats = this.stats;
							stats.idleboredom += 0.00125F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
						}

						if (this.getCurrentSquare() != null && this.getLastSquare() != null && this.getCurrentSquare().getRoom() == this.getLastSquare().getRoom() && this.getCurrentSquare().getRoom() != null && !this.isReading()) {
							stats = this.stats;
							stats.idleboredom += 1.0E-4F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
							stats = this.stats;
							stats.idleboredom += 0.00125F * GameTime.instance.getMultiplier() * GameTime.instance.getDeltaMinutesPerDay();
						}
					}

					if (this.getLx() != this.getX() || this.getLy() != this.getY()) {
						tempo.x = this.getX() - this.getLx();
						tempo.y = this.getY() - this.getLy();
						Vector2 vector2 = tempo;
						float5 = 1.0F;
						float7 = vector2.getLength() / this.getMoveSpeed();
						if (float7 < 1.0F) {
							float5 = 0.5F;
						}

						if (float7 > 1.0F) {
							float5 = 4.0F;
						}

						float6 = vector2.getLength() * 0.0018F * float5;
						float6 /= 0.06F;
						if (float6 > 0.0F && float6 < 0.3F) {
							float6 = 0.3F;
						}

						float6 = 1.0F - float6;
						if (float6 < 0.0F) {
							float6 = 1.0F;
						}

						if (float6 > 1.0F) {
							float6 = 1.0F;
						}

						if (vector2.getLength() != 0.0F && this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 0) {
							float float8 = 1.0F;
							if (this.HasTrait("Asthmatic")) {
								float8 = 1.3F;
							}

							stats = this.stats;
							stats.endurance -= 4.0000004E-6F * this.getRecoveryMod() * GameTime.instance.getMultiplier() * float6 * float8;
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

					float4 = 1.0F - this.stats.getStress() - 0.5F;
					float4 *= 1.0E-4F;
					if (float4 > 0.0F) {
						float4 += 0.5F;
					}

					stats = this.stats;
					stats.morale += float4;
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
		return this == IsoPlayer.instance && IsoPlayer.instance.IsRunning() ? 1.2 : 1.0;
	}

	private void checkDrawWeaponPost(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.sprite != null) {
			if (this.sprite.CurrentAnim != null) {
				if (this.sprite.CurrentAnim.name != null) {
					if (this.leftHandItem instanceof HandWeapon) {
						this.useHandWeapon = (HandWeapon)this.leftHandItem;
					} else {
						this.useHandWeapon = null;
					}

					WeaponOverlayUtils.DrawWeapon(this.useHandWeapon, this, this.sprite, float1, float2, float3, colorInfo);
				}
			}
		}
	}

	public void faceDirection(IsoGameCharacter gameCharacter) {
		tempo.x = gameCharacter.x;
		tempo.y = gameCharacter.y;
		Vector2 vector2 = tempo;
		vector2.x -= this.x;
		vector2 = tempo;
		vector2.y -= this.y;
		tempo.normalize();
		this.DirectionFromVector(tempo);
	}

	public void faceLocation(float float1, float float2) {
		tempo.x = float1 + 0.5F;
		tempo.y = float2 + 0.5F;
		Vector2 vector2 = tempo;
		vector2.x -= this.getX();
		vector2 = tempo;
		vector2.y -= this.getY();
		this.DirectionFromVector(tempo);
		this.getVectorFromDirection(this.angle);
	}

	private void checkDrawWeaponPre(float float1, float float2, float float3, ColorInfo colorInfo) {
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

	public void splatBlood(int int1, float float1) {
		if (this.getCurrentSquare() != null) {
			this.getCurrentSquare().splatBlood(int1, float1);
		}
	}

	private void UpdateWounds() {
		for (int int1 = 0; int1 < this.wounds.size(); ++int1) {
			IsoGameCharacter.Wound wound = (IsoGameCharacter.Wound)this.wounds.get(int1);
			if (wound.tourniquet) {
				wound.bleeding -= 5.0E-5F;
			}

			if (wound.bandaged) {
				wound.bleeding -= 2.0E-5F;
			}
		}
	}

	public boolean isOutside() {
		return this.getCurrentSquare() == null ? false : this.getCurrentSquare().isOutside();
	}

	public boolean isFemale() {
		return this.bFemale;
	}

	public void setFemale(boolean boolean1) {
		this.bFemale = boolean1;
	}

	public void setLastHitCount(int int1) {
		this.lastHitCount = int1;
	}

	public int getLastHitCount() {
		return this.lastHitCount;
	}

	public int getSurvivorKills() {
		return this.SurvivorKills;
	}

	public void setSurvivorKills(int int1) {
		this.SurvivorKills = int1;
	}

	public int getAge() {
		return this.age;
	}

	public void setAge(int int1) {
		this.age = int1;
	}

	public void exert(float float1) {
		if (this.HasTrait("PlaysFootball")) {
			float1 *= 0.9F;
		}

		if (this.HasTrait("Jogger")) {
			float1 *= 0.9F;
		}

		Stats stats = this.stats;
		stats.endurance -= float1;
	}

	public IsoGameCharacter.PerkInfo getPerkInfo(PerkFactory.Perks perks) {
		for (int int1 = 0; int1 < this.PerkList.size(); ++int1) {
			IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int1);
			if (perkInfo.perkType == perks) {
				return perkInfo;
			}
		}

		return null;
	}

	public boolean isSat() {
		return this.isSat;
	}

	public void setSat(boolean boolean1) {
		this.isSat = boolean1;
	}

	public IsoObject getChair() {
		return this.chair;
	}

	public void setChair(IsoObject object) {
		this.chair = object;
	}

	public void HitSilence(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
		if (gameCharacter != null && handWeapon != null) {
			if (this.getStateMachine().getCurrent() != StaggerBackState.instance()) {
				if (this.isOnFloor()) {
					float1 = 1.0F;
					float2 = 2.0F;
					boolean1 = false;
					this.setReanimateTimer(this.getReanimateTimer() + 38.0F);
				}

				if (handWeapon.getName().contains("Bare Hands") || gameCharacter.legsSprite.CurrentAnim.name != null && gameCharacter.legsSprite.CurrentAnim.name.contains("Shove")) {
					boolean1 = true;
					this.noDamage = true;
				}

				this.staggerTimeMod = handWeapon.getPushBackMod() * handWeapon.getKnockbackMod(gameCharacter) * gameCharacter.getShovingMod();
				float float3 = 0.0F;
				float float4;
				float float5;
				if (gameCharacter instanceof IsoPlayer && !handWeapon.bIsAimedFirearm) {
					float4 = ((IsoPlayer)gameCharacter).useChargeDelta;
					if (float4 > 1.0F) {
						float4 = 1.0F;
					}

					if (float4 < 0.0F) {
						float4 = 0.0F;
					}

					float5 = handWeapon.getMinDamage() + (handWeapon.getMaxDamage() - handWeapon.getMinDamage()) * float4;
					float3 = float5;
				} else {
					float3 = (float)Rand.Next((int)((handWeapon.getMaxDamage() - handWeapon.getMinDamage()) * 1000.0F)) / 1000.0F + handWeapon.getMinDamage();
				}

				float3 *= float2;
				float4 = float3 * handWeapon.getKnockbackMod(gameCharacter) * gameCharacter.getShovingMod();
				if (float4 > 1.0F) {
					float4 = 1.0F;
				}

				this.setHitForce(float4);
				this.AttackedBy = gameCharacter;
				float5 = IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), this.getX(), this.getY());
				float5 -= handWeapon.getMinRange();
				float5 /= handWeapon.getMaxRange(gameCharacter);
				float5 = 1.0F - float5;
				if (float5 > 1.0F) {
					float5 = 1.0F;
				}

				this.hitDir.x = this.getX();
				this.hitDir.y = this.getY();
				Vector2 vector2 = this.hitDir;
				vector2.x -= gameCharacter.getX();
				vector2 = this.hitDir;
				vector2.y -= gameCharacter.getY();
				this.getHitDir().normalize();
				vector2 = this.hitDir;
				vector2.x *= handWeapon.getPushBackMod();
				vector2 = this.hitDir;
				vector2.y *= handWeapon.getPushBackMod();
				this.hitDir.rotate(handWeapon.HitAngleMod);
				float float6 = gameCharacter.stats.endurance;
				float6 *= gameCharacter.knockbackAttackMod;
				this.hitBy = gameCharacter;
				if (float6 < 0.5F) {
					float6 *= 1.3F;
					if (float6 < 0.4F) {
						float6 = 0.4F;
					}

					this.setHitForce(this.getHitForce() * float6);
				}

				if (!handWeapon.isRangeFalloff()) {
					float5 = 1.0F;
				}

				if (!handWeapon.isShareDamage()) {
					float1 = 1.0F;
				}

				if (gameCharacter instanceof IsoPlayer) {
					this.setHitForce(this.getHitForce() * 2.0F);
				}

				Vector2 vector22 = tempVector2_1.set(this.getX(), this.getY());
				Vector2 vector23 = tempVector2_2.set(gameCharacter.getX(), gameCharacter.getY());
				vector22.x -= vector23.x;
				vector22.y -= vector23.y;
				Vector2 vector24 = this.getVectorFromDirection(tempVector2_2);
				vector22.normalize();
				float float7 = vector22.dot(vector24);
				if (float7 > -0.3F) {
					float3 *= 1.5F;
				}

				float float8 = handWeapon.CriticalChance;
				if (this.isOnFloor()) {
					float8 *= 2.0F;
				}

				if ((float)Rand.Next(100) < float8) {
					float3 *= 10.0F;
				}

				if (!this.isOnFloor() && handWeapon.getScriptItem().Categories.contains("Axe")) {
					float3 *= 2.0F;
				}

				if (gameCharacter instanceof IsoPlayer) {
					if (!boolean1) {
						if (handWeapon.isAimedFirearm()) {
							this.Health -= float3 * 0.7F;
						} else {
							this.Health -= float3 * 0.15F;
						}
					}
				} else if (!boolean1) {
					if (handWeapon.isAimedFirearm()) {
						this.Health -= float3 * 0.7F;
					} else {
						this.Health -= float3 * 0.15F;
					}
				}

				float float9 = 12.0F;
				if (gameCharacter instanceof IsoPlayer) {
					int int1 = ((IsoPlayer)gameCharacter).Moodles.getMoodleLevel(MoodleType.Endurance);
					if (int1 == 4) {
						float9 = 50.0F;
					} else if (int1 == 3) {
						float9 = 35.0F;
					} else if (int1 == 2) {
						float9 = 24.0F;
					} else if (int1 == 1) {
						float9 = 16.0F;
					}
				}

				if (handWeapon.getKnockdownMod() <= 0.0F) {
					handWeapon.setKnockdownMod(1.0F);
				}

				float9 /= handWeapon.getKnockdownMod();
				if (gameCharacter instanceof IsoPlayer && !handWeapon.isAimedHandWeapon()) {
					float9 *= 2.0F - ((IsoPlayer)gameCharacter).useChargeDelta;
				}

				if (float9 < 1.0F) {
					float9 = 1.0F;
				}

				boolean boolean2 = Rand.Next((int)float9) == 0;
				if (this.Health <= 0.0F || (handWeapon.isAlwaysKnockdown() || boolean2) && this instanceof IsoZombie) {
					this.DoDeathSilence(handWeapon, gameCharacter);
				}
			}
		}
	}

	protected void DoDeathSilence(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (this.Health <= 0.0F) {
			if (this.bUseParts && handWeapon != null && handWeapon.getType().equals("Shotgun")) {
				this.headSprite = null;
			}

			if (handWeapon != null) {
				int int1 = handWeapon.getSplatNumber();
				if (int1 < 1) {
					int1 = 1;
				}

				if (Core.bLastStand) {
					int1 *= 3;
				}

				for (int int2 = 0; int2 < int1; ++int2) {
					this.splatBlood(3, 0.3F);
				}
			}

			this.splatBloodFloorBig(0.3F);
			if (gameCharacter != null && gameCharacter.xp != null) {
				gameCharacter.xp.AddXP(handWeapon, 3);
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

	public boolean isEquipped(InventoryItem inventoryItem) {
		return this.isEquippedClothing(inventoryItem) || this.getPrimaryHandItem() == inventoryItem || this.getSecondaryHandItem() == inventoryItem;
	}

	public boolean isEquippedClothing(InventoryItem inventoryItem) {
		return this.getClothingItem_Back() == inventoryItem || this.getClothingItem_Feet() == inventoryItem || this.getClothingItem_Hands() == inventoryItem || this.getClothingItem_Head() == inventoryItem || this.getClothingItem_Legs() == inventoryItem || this.getClothingItem_Torso() == inventoryItem;
	}

	public void faceThisObject(IsoObject object) {
		if (object != null) {
			Vector2 vector2;
			if (object instanceof BaseVehicle) {
				((BaseVehicle)object).getFacingPosition(this, tempo);
				vector2 = tempo;
				vector2.x -= this.getX();
				vector2 = tempo;
				vector2.y -= this.getY();
				this.DirectionFromVector(tempo);
				this.angle.set(tempo.x, tempo.y);
				this.angle.normalize();
			} else {
				object.getFacingPosition(tempo);
				vector2 = tempo;
				vector2.x -= this.getX();
				vector2 = tempo;
				vector2.y -= this.getY();
				this.DirectionFromVector(tempo);
				this.getVectorFromDirection(this.angle);
			}
		}
	}

	public void faceThisObjectAlt(IsoObject object) {
		if (object != null) {
			object.getFacingPositionAlt(tempo);
			Vector2 vector2 = tempo;
			vector2.x -= this.getX();
			vector2 = tempo;
			vector2.y -= this.getY();
			this.DirectionFromVector(tempo);
			this.getVectorFromDirection(this.angle);
		}
	}

	public void setAnimated(boolean boolean1) {
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

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		super.saveChange(string, kahluaTable, byteBuffer);
		if ("addItem".equals(string)) {
			if (kahluaTable != null && kahluaTable.rawget("item") instanceof InventoryItem) {
				InventoryItem inventoryItem = (InventoryItem)kahluaTable.rawget("item");
				try {
					inventoryItem.save(byteBuffer, false);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		} else if ("addItemOfType".equals(string)) {
			if (kahluaTable != null && kahluaTable.rawget("type") instanceof String) {
				GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget("type"));
				if (kahluaTable.rawget("count") instanceof Double) {
					byteBuffer.putShort(((Double)kahluaTable.rawget("count")).shortValue());
				} else {
					byteBuffer.putShort((short)1);
				}
			}
		} else if ("AddRandomDamageFromZombie".equals(string)) {
			if (kahluaTable != null && kahluaTable.rawget("zombie") instanceof Double) {
				byteBuffer.putShort(((Double)kahluaTable.rawget("zombie")).shortValue());
			}
		} else if (!"AddZombieKill".equals(string)) {
			if ("DamageFromWeapon".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("weapon") instanceof String) {
					GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget("weapon"));
				}
			} else if ("removeItem".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("item") instanceof Double) {
					byteBuffer.putInt(((Double)kahluaTable.rawget("item")).intValue());
				}
			} else if ("removeItemID".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("id") instanceof Double) {
					byteBuffer.putLong(((Double)kahluaTable.rawget("id")).longValue());
				}

				if (kahluaTable != null && kahluaTable.rawget("type") instanceof String) {
					GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget("type"));
				} else {
					GameWindow.WriteStringUTF(byteBuffer, (String)null);
				}
			} else if ("removeItemType".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("type") instanceof String) {
					GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget("type"));
					if (kahluaTable.rawget("count") instanceof Double) {
						byteBuffer.putShort(((Double)kahluaTable.rawget("count")).shortValue());
					} else {
						byteBuffer.putShort((short)1);
					}
				}
			} else if ("removeOneOf".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("type") instanceof String) {
					GameWindow.WriteStringUTF(byteBuffer, (String)kahluaTable.rawget("type"));
				}
			} else if ("reanimatedID".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("ID") instanceof Double) {
					int int1 = ((Double)kahluaTable.rawget("ID")).intValue();
					byteBuffer.putInt(int1);
				}
			} else if ("Shove".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("hitDirX") instanceof Double && kahluaTable.rawget("hitDirY") instanceof Double && kahluaTable.rawget("force") instanceof Double) {
					byteBuffer.putFloat(((Double)kahluaTable.rawget("hitDirX")).floatValue());
					byteBuffer.putFloat(((Double)kahluaTable.rawget("hitDirY")).floatValue());
					byteBuffer.putFloat(((Double)kahluaTable.rawget("force")).floatValue());
				}
			} else if ("addXp".equals(string)) {
				if (kahluaTable != null && kahluaTable.rawget("perk") instanceof Double && kahluaTable.rawget("xp") instanceof Double) {
					byteBuffer.putInt(((Double)kahluaTable.rawget("perk")).intValue());
					byteBuffer.putInt(((Double)kahluaTable.rawget("xp")).intValue());
					Object object = kahluaTable.rawget("noMultiplier");
					byteBuffer.put((byte)(Boolean.TRUE.equals(object) ? 1 : 0));
				}
			} else if (!"wakeUp".equals(string) && "mechanicActionDone".equals(string) && kahluaTable != null) {
				byteBuffer.put((byte)((Boolean)kahluaTable.rawget("success") ? 1 : 0));
				byteBuffer.putInt(((Double)kahluaTable.rawget("vehicleId")).intValue());
				GameWindow.WriteString(byteBuffer, (String)kahluaTable.rawget("partId"));
				byteBuffer.put((byte)((Boolean)kahluaTable.rawget("installing") ? 1 : 0));
				byteBuffer.putLong(((Double)kahluaTable.rawget("itemId")).longValue());
			}
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		super.loadChange(string, byteBuffer);
		String string2;
		if ("addItem".equals(string)) {
			string2 = GameWindow.ReadStringUTF(byteBuffer);
			byte byte1 = byteBuffer.get();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string2);
			if (inventoryItem != null) {
				try {
					inventoryItem.load(byteBuffer, 143, false);
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				this.getInventory().AddItem(inventoryItem);
			} else {
				DebugLog.log("IsoGameCharacter.loadChange() unknown item type \"" + string2 + "\"");
			}
		} else {
			short short1;
			int int1;
			if ("addItemOfType".equals(string)) {
				string2 = GameWindow.ReadStringUTF(byteBuffer);
				short1 = byteBuffer.getShort();
				for (int1 = 0; int1 < short1; ++int1) {
					this.getInventory().AddItem(string2);
				}
			} else if ("AddRandomDamageFromZombie".equals(string)) {
				short short2 = byteBuffer.getShort();
				IsoZombie zombie = GameClient.getZombie(short2);
				if (zombie != null && !this.isDead()) {
					this.getBodyDamage().AddRandomDamageFromZombie(zombie);
					this.getBodyDamage().Update();
					if (this.isDead()) {
						if (this.isFemale()) {
							zombie.getEmitter().playSound("FemaleBeingEatenDeath");
						} else {
							zombie.getEmitter().playSound("MaleBeingEatenDeath");
						}
					}
				}
			} else if ("AddZombieKill".equals(string)) {
				this.setZombieKills(this.getZombieKills() + 1);
			} else {
				InventoryItem inventoryItem2;
				if ("DamageFromWeapon".equals(string)) {
					string2 = GameWindow.ReadStringUTF(byteBuffer);
					inventoryItem2 = InventoryItemFactory.CreateItem(string2);
					if (inventoryItem2 instanceof HandWeapon) {
						this.getBodyDamage().DamageFromWeapon((HandWeapon)inventoryItem2);
					}
				} else if ("exitVehicle".equals(string)) {
					BaseVehicle baseVehicle = this.getVehicle();
					if (baseVehicle != null) {
						baseVehicle.exit(this);
						this.setVehicle((BaseVehicle)null);
					}
				} else if ("removeItem".equals(string)) {
					int int2 = byteBuffer.getInt();
					if (int2 >= 0 && int2 < this.getInventory().getItems().size()) {
						inventoryItem2 = (InventoryItem)this.getInventory().getItems().get(int2);
						if (this.getPrimaryHandItem() == inventoryItem2) {
							this.setPrimaryHandItem((InventoryItem)null);
						}

						if (this.getSecondaryHandItem() == inventoryItem2) {
							this.setSecondaryHandItem((InventoryItem)null);
						}

						this.getInventory().Remove(inventoryItem2);
					}
				} else {
					String string3;
					if ("removeItemID".equals(string)) {
						long long1 = byteBuffer.getLong();
						string3 = GameWindow.ReadStringUTF(byteBuffer);
						InventoryItem inventoryItem3 = this.getInventory().getItemWithID(long1);
						if (inventoryItem3 != null && inventoryItem3.getFullType().equals(string3)) {
							if (inventoryItem3 == this.getPrimaryHandItem()) {
								this.setPrimaryHandItem((InventoryItem)null);
							}

							if (inventoryItem3 == this.getSecondaryHandItem()) {
								this.setSecondaryHandItem((InventoryItem)null);
							}

							this.getInventory().Remove(inventoryItem3);
						}
					} else if ("removeItemType".equals(string)) {
						string2 = GameWindow.ReadStringUTF(byteBuffer);
						short1 = byteBuffer.getShort();
						for (int1 = 0; int1 < short1; ++int1) {
							this.getInventory().RemoveOneOf(string2);
						}
					} else if ("removeOneOf".equals(string)) {
						string2 = GameWindow.ReadStringUTF(byteBuffer);
						this.getInventory().RemoveOneOf(string2);
					} else if ("reanimatedID".equals(string)) {
						this.ReanimatedCorpseID = byteBuffer.getInt();
					} else if ("Shove".equals(string)) {
						if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer()) {
							return;
						}

						this.getHitDir().x = byteBuffer.getFloat();
						this.getHitDir().y = byteBuffer.getFloat();
						this.setHitForce(Math.min(0.5F, byteBuffer.getFloat()));
						if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
							StaggerBackState.instance().enter(this);
						}

						this.stateMachine.changeState(StaggerBackState.instance());
					} else if ("StopBurning".equals(string)) {
						this.StopBurning();
					} else {
						int int3;
						if ("addXp".equals(string)) {
							PerkFactory.Perks perks = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
							int3 = byteBuffer.getInt();
							boolean boolean1 = byteBuffer.get() == 1;
							if (boolean1) {
								this.getXp().AddXPNoMultiplier(perks, (float)int3);
							} else {
								this.getXp().AddXP(perks, (float)int3);
							}
						} else if ("wakeUp".equals(string)) {
							if (this.isAsleep()) {
								this.Asleep = false;
								this.ForceWakeUpTime = -1.0F;
								TutorialManager.instance.StealControl = false;
								if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
									UIManager.setFadeBeforeUI(((IsoPlayer)this).getPlayerNum(), true);
									UIManager.FadeIn((double)((IsoPlayer)this).getPlayerNum(), 2.0);
									ScriptManager.instance.Trigger("OnPlayerWake");
									GameClient.instance.sendPlayer((IsoPlayer)this);
								}
							}
						} else if ("mechanicActionDone".equals(string)) {
							boolean boolean2 = byteBuffer.get() == 1;
							int3 = byteBuffer.getInt();
							string3 = GameWindow.ReadString(byteBuffer);
							boolean boolean3 = byteBuffer.get() == 1;
							long long2 = byteBuffer.getLong();
							LuaEventManager.triggerEvent("OnMechanicActionDone", this, boolean2, int3, string3, long2, boolean3);
						} else if ("vehicleNoKey".equals(string)) {
							this.SayDebug(" [img=media/ui/CarKey_none.png]");
						}
					}
				}
			}
		}
	}

	public void setRemoteMoveX(float float1) {
		this.remoteMoveX = float1;
	}

	public void setRemoteMoveY(float float1) {
		this.remoteMoveY = float1;
	}

	public void setRemoteState(byte byte1) {
		this.NetRemoteState = byte1;
	}

	public int getAlreadyReadPages(String string) {
		for (int int1 = 0; int1 < this.ReadBooks.size(); ++int1) {
			IsoGameCharacter.ReadBook readBook = (IsoGameCharacter.ReadBook)this.ReadBooks.get(int1);
			if (readBook.fullType.equals(string)) {
				return readBook.alreadyReadPages;
			}
		}

		return 0;
	}

	public void setAlreadyReadPages(String string, int int1) {
		for (int int2 = 0; int2 < this.ReadBooks.size(); ++int2) {
			IsoGameCharacter.ReadBook readBook = (IsoGameCharacter.ReadBook)this.ReadBooks.get(int2);
			if (readBook.fullType.equals(string)) {
				readBook.alreadyReadPages = int1;
				return;
			}
		}

		IsoGameCharacter.ReadBook readBook2 = new IsoGameCharacter.ReadBook();
		readBook2.fullType = string;
		readBook2.alreadyReadPages = int1;
		this.ReadBooks.add(readBook2);
	}

	public void updateLightInfo() {
		if (GameServer.bServer || LightingThread.instance == null || !LightingThread.instance.newLightingMethod) {
			if (!GameServer.bServer || !(this instanceof IsoZombie)) {
				if (GameServer.bServer || this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
					synchronized (this.lightInfo) {
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
								float float1 = GameTime.getInstance().getNight();
								if (float1 > 0.8F && this.HasTrait("NightVision")) {
									float1 = 0.8F;
								}

								this.lightInfo.rmod = GameTime.getInstance().Lerp(1.0F, 0.1F, float1);
								this.lightInfo.gmod = GameTime.getInstance().Lerp(1.0F, 0.2F, float1);
								this.lightInfo.bmod = GameTime.getInstance().Lerp(1.0F, 0.45F, float1);
							}

							int int1;
							if (GameClient.bClient) {
								ArrayList arrayList = GameClient.instance.getPlayers();
								for (int int2 = 0; int2 < arrayList.size(); ++int2) {
									IsoPlayer player = (IsoPlayer)arrayList.get(int2);
									if (player.getTorchStrength() > 0.0F && (player == this || IsoUtils.DistanceManhatten(this.getX(), this.getY(), player.getX(), player.getY()) < 50.0F)) {
										this.lightInfo.torches.add(IsoGameCharacter.TorchInfo.alloc().set(player));
									}
								}
							} else {
								for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
									IsoPlayer player2 = IsoPlayer.players[int1];
									if (player2 != null && !player2.isDead() && player2.getTorchStrength() > 0.0F && (player2 == this || IsoUtils.DistanceManhatten(this.getX(), this.getY(), player2.getX(), player2.getY()) < 50.0F)) {
										this.lightInfo.torches.add(IsoGameCharacter.TorchInfo.alloc().set(player2));
									}
								}
							}

							for (int1 = 0; int1 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++int1) {
								BaseVehicle baseVehicle = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(int1);
								if (baseVehicle.hasHeadlights() && baseVehicle.getHeadlightsOn() && IsoUtils.DistanceManhatten(this.getX(), this.getY(), baseVehicle.getX(), baseVehicle.getY()) < 50.0F) {
									for (int int3 = 0; int3 < baseVehicle.getLightCount(); ++int3) {
										this.lightInfo.torches.add(IsoGameCharacter.TorchInfo.alloc().set(baseVehicle.getLightByIndex(int3)));
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
		synchronized (this.lightInfo) {
			for (int int1 = 0; int1 < this.lightInfo2.torches.size(); ++int1) {
				IsoGameCharacter.TorchInfo.release((IsoGameCharacter.TorchInfo)this.lightInfo2.torches.get(int1));
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
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		this.updateLightInfo();
	}

	public boolean isSafety() {
		return this.safety;
	}

	public void setSafety(boolean boolean1) {
		this.safety = boolean1;
	}

	public float getSafetyCooldown() {
		return this.safetyCooldown;
	}

	public void setSafetyCooldown(float float1) {
		this.safetyCooldown = Math.max(float1, 0.0F);
	}

	public float getRecoilDelay() {
		return this.RecoilDelay;
	}

	public void setRecoilDelay(float float1) {
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		this.RecoilDelay = float1;
	}

	public float getBeenMovingFor() {
		return this.BeenMovingFor;
	}

	public void setBeenMovingFor(float float1) {
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		if (float1 > 70.0F) {
			float1 = 70.0F;
		}

		this.BeenMovingFor = float1;
	}

	public boolean isForceShove() {
		return this.forceShove;
	}

	public void setForceShove(boolean boolean1) {
		this.forceShove = boolean1;
	}

	public String getClickSound() {
		return this.clickSound;
	}

	public void setClickSound(String string) {
		this.clickSound = string;
	}

	public int getMeleeCombatMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.BluntGuard);
		if (this.haveBladeWeapon()) {
			int1 = this.getPerkLevel(PerkFactory.Perks.BladeGuard);
		}

		if (int1 == 1) {
			return -2;
		} else if (int1 == 2) {
			return 0;
		} else if (int1 == 3) {
			return 1;
		} else if (int1 == 4) {
			return 2;
		} else if (int1 == 5) {
			return 3;
		} else if (int1 == 6) {
			return 4;
		} else if (int1 == 7) {
			return 5;
		} else if (int1 == 8) {
			return 5;
		} else if (int1 == 9) {
			return 6;
		} else {
			return int1 == 10 ? 7 : -5;
		}
	}

	public int getMaintenanceMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.BluntMaintenance);
		if (this.haveBladeWeapon()) {
			int1 = this.getPerkLevel(PerkFactory.Perks.BladeMaintenance);
		}

		return int1 / 2;
	}

	public boolean haveBladeWeapon() {
		if (this.getPrimaryHandItem() == null) {
			return false;
		} else {
			Item item = this.getPrimaryHandItem().getScriptItem();
			return item.getCategories().contains("Blade") || item.getCategories().contains("Axe");
		}
	}

	public void setVehicle(BaseVehicle baseVehicle) {
		this.vehicle = baseVehicle;
	}

	public BaseVehicle getVehicle() {
		return this.vehicle;
	}

	public boolean isUnderVehicle() {
		int int1 = ((int)this.x - 4) / 10;
		int int2 = ((int)this.y - 4) / 10;
		int int3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F));
		int int4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F));
		for (int int5 = int2; int5 < int4; ++int5) {
			for (int int6 = int1; int6 < int3; ++int6) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int6 * 10, int5 * 10, 0);
				if (chunk != null) {
					for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
						BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
						Vector2 vector2 = baseVehicle.testCollisionWithCharacter(this, 0.3F);
						if (vector2 != null && vector2.x != -1.0F) {
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

	public void setTemperature(float float1) {
		this.getBodyDamage().setTemperature(float1);
	}

	public float getReduceInfectionPower() {
		return this.reduceInfectionPower;
	}

	public void setReduceInfectionPower(float float1) {
		this.reduceInfectionPower = float1;
	}

	public float getInventoryWeight() {
		if (this.getInventory() == null) {
			return 0.0F;
		} else {
			float float1 = 0.0F;
			ArrayList arrayList = this.getInventory().getItems();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				InventoryItem inventoryItem = (InventoryItem)arrayList.get(int1);
				if (this.isEquipped(inventoryItem)) {
					float1 += inventoryItem.getEquippedWeight();
				} else {
					float1 += inventoryItem.getUnequippedWeight();
				}
			}

			return float1;
		}
	}

	public void dropHandItems() {
		IsoGridSquare square = this.getCurrentSquare();
		if (square != null) {
			InventoryItem inventoryItem = this.getPrimaryHandItem();
			InventoryItem inventoryItem2 = this.getSecondaryHandItem();
			if (inventoryItem != null || inventoryItem2 != null) {
				if (inventoryItem2 == inventoryItem) {
					inventoryItem2 = null;
				}

				if (inventoryItem != null) {
					this.setPrimaryHandItem((InventoryItem)null);
					this.getInventory().DoRemoveItem(inventoryItem);
					square.AddWorldInventoryItem(inventoryItem, 0.0F, 0.0F, 0.0F);
				}

				if (inventoryItem2 != null) {
					this.setSecondaryHandItem((InventoryItem)null);
					this.getInventory().DoRemoveItem(inventoryItem2);
					square.AddWorldInventoryItem(inventoryItem2, 0.0F, 0.0F, 0.0F);
				}
			}
		}
	}

	public boolean shouldBecomeZombieAfterDeath() {
		switch (SandboxOptions.instance.Lore.Transmission.getValue()) {
		case 1: 
			boolean boolean1;
			if (!this.getBodyDamage().IsFakeInfected()) {
				float float1 = this.getBodyDamage().getInfectionLevel();
				BodyDamage bodyDamage = this.BodyDamage;
				if (float1 >= 0.001F) {
					boolean1 = true;
					return boolean1;
				}
			}

			boolean1 = false;
			return boolean1;
		
		case 2: 
			return true;
		
		case 3: 
			return false;
		
		default: 
			return false;
		
		}
	}

	public void applyTraits(ArrayList arrayList) {
		if (arrayList != null) {
			HashMap hashMap = new HashMap();
			hashMap.put(PerkFactory.Perks.Fitness, 5);
			hashMap.put(PerkFactory.Perks.Strength, 5);
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				String string = (String)arrayList.get(int1);
				if (string != null && !string.isEmpty()) {
					TraitFactory.Trait trait = TraitFactory.getTrait(string);
					if (trait != null) {
						if (!this.HasTrait(string)) {
							this.getTraits().add(string);
						}

						HashMap hashMap2 = trait.getXPBoostMap();
						PerkFactory.Perks perks;
						int int2;
						if (hashMap2 != null) {
							for (Iterator iterator = hashMap2.entrySet().iterator(); iterator.hasNext(); hashMap.put(perks, int2)) {
								Entry entry = (Entry)iterator.next();
								perks = (PerkFactory.Perks)entry.getKey();
								int2 = (Integer)entry.getValue();
								if (hashMap.containsKey(perks)) {
									int2 += (Integer)hashMap.get(perks);
								}
							}
						}
					}
				}
			}

			if (this instanceof IsoPlayer) {
				((IsoPlayer)this).getNutrition().applyWeightFromTraits();
			}

			HashMap hashMap3 = this.getDescriptor().getXPBoostMap();
			Iterator iterator2;
			Entry entry2;
			PerkFactory.Perks perks2;
			int int3;
			for (iterator2 = hashMap3.entrySet().iterator(); iterator2.hasNext(); hashMap.put(perks2, int3)) {
				entry2 = (Entry)iterator2.next();
				perks2 = (PerkFactory.Perks)entry2.getKey();
				int3 = (Integer)entry2.getValue();
				if (hashMap.containsKey(perks2)) {
					int3 += (Integer)hashMap.get(perks2);
				}
			}

			iterator2 = hashMap.entrySet().iterator();
			while (iterator2.hasNext()) {
				entry2 = (Entry)iterator2.next();
				perks2 = (PerkFactory.Perks)entry2.getKey();
				int3 = (Integer)entry2.getValue();
				int3 = Math.max(0, int3);
				int3 = Math.min(10, int3);
				this.getDescriptor().getXPBoostMap().put(perks2, Math.min(3, int3));
				for (int int4 = 0; int4 < int3; ++int4) {
					this.LevelPerk(perks2);
				}

				this.getXp().setXPToLevel(perks2, this.getPerkLevel(perks2));
			}
		}
	}

	public void createKeyRing() {
		InventoryItem inventoryItem = this.getInventory().AddItem("Base.KeyRing");
		if (inventoryItem != null && inventoryItem instanceof InventoryContainer) {
			InventoryContainer inventoryContainer = (InventoryContainer)inventoryItem;
			inventoryContainer.setName(Translator.getText("IGUI_KeyRingName", this.getDescriptor().getForename(), this.getDescriptor().getSurname()));
			if (Rand.Next(100) < 40) {
				RoomDef roomDef = IsoWorld.instance.MetaGrid.getRoomAt((int)this.getX(), (int)this.getY(), (int)this.getZ());
				if (roomDef != null && roomDef.getBuilding() != null) {
					String string = "Base.Key" + (Rand.Next(5) + 1);
					InventoryItem inventoryItem2 = inventoryContainer.getInventory().AddItem(string);
					inventoryItem2.setKeyId(roomDef.getBuilding().getKeyId());
				}
			}
		}
	}

	public void autoDrink() {
		if (!GameServer.bServer) {
			if (!GameClient.bClient || !(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
				if (!LuaHookManager.TriggerHook("AutoDrink", this)) {
					if (!(this.stats.thirst <= 0.1F)) {
						InventoryItem inventoryItem = null;
						ArrayList arrayList = this.getInventory().getItems();
						for (int int1 = 0; int1 < arrayList.size(); ++int1) {
							InventoryItem inventoryItem2 = (InventoryItem)arrayList.get(int1);
							if (inventoryItem2.isWaterSource() && !inventoryItem2.isBeingFilled() && !inventoryItem2.isTaintedWater()) {
								if (!(inventoryItem2 instanceof Drainable)) {
									inventoryItem = inventoryItem2;
									break;
								}

								if (((Drainable)inventoryItem2).getUsedDelta() > 0.0F) {
									inventoryItem = inventoryItem2;
									break;
								}
							}
						}

						if (inventoryItem != null) {
							Stats stats = this.stats;
							stats.thirst -= 0.1F;
							if (GameClient.bClient) {
								GameClient.instance.drink((IsoPlayer)this, 0.1F);
							}

							inventoryItem.Use();
						}
					}
				}
			}
		}
	}

	public List getKnownRecipes() {
		return this.knownRecipes;
	}

	public boolean isRecipeKnown(Recipe recipe) {
		return !recipe.needToBeLearn() || this.getKnownRecipes().contains(recipe.getOriginalname());
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

	private boolean isZombieAttacking(IsoMovingObject movingObject) {
		if (GameClient.bClient) {
			return this instanceof IsoZombie && this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieBite".equals(this.legsSprite.CurrentAnim.name);
		} else {
			return this instanceof IsoZombie && movingObject == ((IsoZombie)this).target && this.getCurrentState() == AttackState.instance();
		}
	}

	private boolean isZombieThumping() {
		if (this instanceof IsoZombie) {
			return this.getCurrentState() == ThumpState.instance();
		} else {
			return false;
		}
	}

	public int compareMovePriority(IsoGameCharacter gameCharacter) {
		if (gameCharacter == null) {
			return 1;
		} else if (this.isZombieThumping() && !gameCharacter.isZombieThumping()) {
			return 1;
		} else if (!this.isZombieThumping() && gameCharacter.isZombieThumping()) {
			return -1;
		} else if (gameCharacter instanceof IsoPlayer) {
			return GameClient.bClient && this.isZombieAttacking(gameCharacter) ? -1 : 0;
		} else if (this.isZombieAttacking() && !gameCharacter.isZombieAttacking()) {
			return 1;
		} else if (!this.isZombieAttacking() && gameCharacter.isZombieAttacking()) {
			return -1;
		} else if (this.isMoving() && !gameCharacter.isMoving()) {
			return 1;
		} else if (!this.isMoving() && gameCharacter.isMoving()) {
			return -1;
		} else if (this.isFacingNorthWesterly() && !gameCharacter.isFacingNorthWesterly()) {
			return 1;
		} else {
			return !this.isFacingNorthWesterly() && gameCharacter.isFacingNorthWesterly() ? -1 : 0;
		}
	}

	public long playSound(String string) {
		return this.getEmitter().playSound(string);
	}

	@Deprecated
	public long playSound(String string, boolean boolean1) {
		return this.getEmitter().playSound(string, boolean1);
	}

	public boolean isKnownPoison(InventoryItem inventoryItem) {
		if (inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			if (food.getPoisonPower() <= 0) {
				return false;
			}

			if (food.getHerbalistType() != null && !food.getHerbalistType().isEmpty()) {
				return this.getKnownRecipes().contains("Herbalist");
			}

			if (food.getPoisonDetectionLevel() >= 0 && this.getPerkLevel(PerkFactory.Perks.Cooking) >= 10 - food.getPoisonDetectionLevel()) {
				return true;
			}

			if (food.getPoisonLevelForRecipe() != null) {
				return true;
			}
		}

		return false;
	}

	public int getLastHourSleeped() {
		return this.lastHourSleeped;
	}

	public void setLastHourSleeped(int int1) {
		this.lastHourSleeped = int1;
	}

	public void setTimeOfSleep(float float1) {
		this.timeOfSleep = float1;
	}

	public void setDelayToSleep(float float1) {
		this.delayToActuallySleep = float1;
	}

	public String getBedType() {
		return this.bedType;
	}

	public void setBedType(String string) {
		this.bedType = string;
	}

	public void enterVehicle(BaseVehicle baseVehicle, int int1, Vector3f vector3f) {
		if (this.vehicle != null) {
			this.vehicle.exit(this);
		}

		if (baseVehicle != null) {
			baseVehicle.enter(int1, this, vector3f);
		}
	}

	public void Hit(BaseVehicle baseVehicle, float float1, float float2, Vector2 vector2) {
		this.AttackedBy = baseVehicle.getDriver();
		this.setHitDir(vector2);
		this.setHitForce(float1 * 0.1F);
		this.Move(vector2.setLength(float1));
		if (float2 > 0.0F) {
			this.getStateMachine().changeState(StaggerBackState.instance());
		} else if (float1 < 5.0F) {
			this.getStateMachine().changeState(StaggerBackState.instance());
		} else if (float1 < 10.0F) {
			this.getStateMachine().changeState(StaggerBackDieState.instance());
		} else {
			this.Kill(baseVehicle.getCharacter(0));
		}
	}

	public PolygonalMap2.Path getPath2() {
		return this.path2;
	}

	public void setPath2(PolygonalMap2.Path path) {
		this.path2 = path;
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

	public void setAvoidDamage(boolean boolean1) {
		this.avoidDamage = boolean1;
	}

	public void setBed(IsoObject object) {
		this.bed = object;
	}

	public boolean isReading() {
		return this.isReading;
	}

	public void setReading(boolean boolean1) {
		this.isReading = boolean1;
	}

	public float getTimeSinceLastSmoke() {
		return this.timeSinceLastSmoke;
	}

	public void setTimeSinceLastSmoke(float float1) {
		this.timeSinceLastSmoke = float1;
	}

	public boolean isInvisible() {
		return this.invisible;
	}

	public void removeEquippedClothing(InventoryItem inventoryItem) {
		if (this.getClothingItem_Back() == inventoryItem) {
			this.setClothingItem_Back((InventoryItem)null);
		}

		if (this.getClothingItem_Feet() == inventoryItem) {
			this.setClothingItem_Feet((InventoryItem)null);
		}

		if (this.getClothingItem_Hands() == inventoryItem) {
			this.setClothingItem_Hands((InventoryItem)null);
		}

		if (this.getClothingItem_Head() == inventoryItem) {
			this.setClothingItem_Head((InventoryItem)null);
		}

		if (this.getClothingItem_Legs() == inventoryItem) {
			this.setClothingItem_Legs((InventoryItem)null);
		}

		if (this.getClothingItem_Torso() == inventoryItem) {
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

	public void setInvisible(boolean boolean1) {
		this.invisible = boolean1;
	}

	public void setGodMod(boolean boolean1) {
		this.godMod = boolean1;
	}

	public boolean isUnlimitedCarry() {
		return this.unlimitedCarry;
	}

	public void setUnlimitedCarry(boolean boolean1) {
		this.unlimitedCarry = boolean1;
	}

	public boolean isBuildCheat() {
		return this.buildCheat;
	}

	public void setBuildCheat(boolean boolean1) {
		this.buildCheat = boolean1;
	}

	public boolean isHealthCheat() {
		return this.healthCheat;
	}

	public void setHealthCheat(boolean boolean1) {
		this.healthCheat = boolean1;
	}

	public boolean isMechanicsCheat() {
		return this.mechanicsCheat;
	}

	public void setMechanicsCheat(boolean boolean1) {
		this.mechanicsCheat = boolean1;
	}

	public boolean isShowAdminTag() {
		return this.showAdminTag;
	}

	public void setShowAdminTag(boolean boolean1) {
		this.showAdminTag = boolean1;
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

		public IsoGameCharacter.TorchInfo set(IsoPlayer player) {
			this.x = player.getX();
			this.y = player.getY();
			this.z = player.getZ();
			this.angleX = player.getAngle().x;
			this.angleY = player.getAngle().y;
			this.dist = player.getLightDistance();
			this.strength = player.getTorchStrength();
			this.bCone = player.isTorchCone();
			this.dot = 0.96F;
			this.focusing = 0;
			return this;
		}

		public static IsoGameCharacter.TorchInfo alloc() {
			return TorchInfoPool.isEmpty() ? new IsoGameCharacter.TorchInfo() : (IsoGameCharacter.TorchInfo)TorchInfoPool.pop();
		}

		public static void release(IsoGameCharacter.TorchInfo torchInfo) {
			TorchInfoPool.push(torchInfo);
		}

		public IsoGameCharacter.TorchInfo set(VehiclePart vehiclePart) {
			BaseVehicle baseVehicle = vehiclePart.getVehicle();
			VehicleLight vehicleLight = vehiclePart.getLight();
			VehicleScript vehicleScript = baseVehicle.getScript();
			Vector3f vector3f = tempVector3f;
			vector3f.set(vehicleLight.offset.x * vehicleScript.getExtents().x / 2.0F / vehicleScript.getModelScale(), 0.0F, vehicleLight.offset.y * vehicleScript.getExtents().z / 2.0F / vehicleScript.getModelScale());
			baseVehicle.getWorldPos(vector3f, vector3f);
			this.x = vector3f.x;
			this.y = vector3f.y;
			this.z = vector3f.z;
			vector3f = baseVehicle.getForwardVector(vector3f);
			this.angleX = vector3f.x;
			this.angleY = vector3f.z;
			this.dist = vehicleLight.dist;
			this.strength = vehicleLight.intensity;
			this.bCone = true;
			this.dot = vehicleLight.dot;
			this.focusing = vehicleLight.focusing;
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

		public void initFrom(IsoGameCharacter.LightInfo lightInfo) {
			this.square = lightInfo.square;
			this.x = lightInfo.x;
			this.y = lightInfo.y;
			this.z = lightInfo.z;
			this.angleX = lightInfo.angleX;
			this.angleY = lightInfo.angleY;
			this.torches.clear();
			this.torches.addAll(lightInfo.torches);
			this.time = (long)((double)System.nanoTime() / 1000000.0);
			this.night = lightInfo.night;
			this.rmod = lightInfo.rmod;
			this.gmod = lightInfo.gmod;
			this.bmod = lightInfo.bmod;
		}
	}

	private static class ReadBook {
		String fullType;
		int alreadyReadPages;

		private ReadBook() {
		}

		ReadBook(Object object) {
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

		public HitInfoComparator(IsoMovingObject movingObject, float float1) {
			this.testPlayer = movingObject;
			this.maxRange = float1;
		}

		public int compare(IsoGameCharacter.HitInfo hitInfo, IsoGameCharacter.HitInfo hitInfo2) {
			float float1 = hitInfo.dist;
			float float2 = hitInfo2.dist;
			if (this.alternateScoringMethod) {
				float float3 = (this.maxRange - float1) / this.maxRange * 20.0F + hitInfo.dot * 20.0F;
				float float4 = (this.maxRange - float2) / this.maxRange * 20.0F + hitInfo2.dot * 20.0F;
				if (float3 > float4) {
					return -1;
				} else {
					return float4 < float3 ? 1 : 0;
				}
			} else if (float1 > float2) {
				return 1;
			} else {
				return float2 > float1 ? -1 : 0;
			}
		}
	}

	private class HitInfo {
		public IsoMovingObject object;
		public float dot;
		public float dist;
		public int chance = 0;

		public HitInfo(IsoMovingObject movingObject, float float1, float float2) {
			this.object = movingObject;
			this.dot = float1;
			this.dist = float2;
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

		public Location(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
		}

		public boolean equals(Object object) {
			if (!(object instanceof IsoGameCharacter.Location)) {
				return false;
			} else {
				IsoGameCharacter.Location location = (IsoGameCharacter.Location)object;
				return this.x == location.x && this.y == location.y && this.z == location.z;
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

		public void addXpMultiplier(PerkFactory.Perks perks, float float1, int int1, int int2) {
			IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.XPMapMultiplier.get(perks);
			if (xPMultiplier == null) {
				xPMultiplier = new IsoGameCharacter.XPMultiplier();
			}

			xPMultiplier.multiplier = float1;
			xPMultiplier.minLevel = int1;
			xPMultiplier.maxLevel = int2;
			this.XPMapMultiplier.put(perks, xPMultiplier);
		}

		public HashMap getMultiplierMap() {
			return this.XPMapMultiplier;
		}

		public float getMultiplier(PerkFactory.Perks perks) {
			IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.XPMapMultiplier.get(perks);
			return xPMultiplier == null ? 0.0F : xPMultiplier.multiplier;
		}

		public int getPerkBoost(PerkFactory.Perks perks) {
			return IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(perks) != null ? (Integer)IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(perks) : 0;
		}

		public int getLevel() {
			return this.level;
		}

		public void setLevel(int int1) {
			this.level = int1;
		}

		public float getTotalXp() {
			return this.TotalXP;
		}

		public XP(IsoGameCharacter gameCharacter) {
			this.chr = gameCharacter;
		}

		public void AddXP(PerkFactory.Perks perks, float float1) {
			this.AddXP(perks, float1, true);
		}

		public void AddXPNoMultiplier(PerkFactory.Perks perks, float float1) {
			IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.getMultiplierMap().remove(perks);
			try {
				this.AddXP(perks, float1);
			} finally {
				if (xPMultiplier != null) {
					this.getMultiplierMap().put(perks, xPMultiplier);
				}
			}
		}

		public void AddXP(PerkFactory.Perks perks, float float1, boolean boolean1) {
			this.AddXP(perks, float1, boolean1, true);
		}

		public void AddXP(PerkFactory.Perks perks, float float1, boolean boolean1, boolean boolean2) {
			this.AddXP(perks, float1, boolean1, boolean2, true, false);
		}

		public void AddXP(PerkFactory.Perks perks, float float1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
			if (!boolean4 && GameClient.bClient && this.chr instanceof IsoPlayer) {
				GameClient.instance.sendAddXpFromPlayerStatsUI((IsoPlayer)this.chr, perks, (int)float1, boolean2, false);
			}

			PerkFactory.Perk perk = null;
			for (int int1 = 0; int1 < PerkFactory.PerkList.size(); ++int1) {
				PerkFactory.Perk perk2 = (PerkFactory.Perk)PerkFactory.PerkList.get(int1);
				if (perk2.getType() == perks) {
					perk = perk2;
					break;
				}
			}

			if (perk.getType() != PerkFactory.Perks.Fitness || !(this.chr instanceof IsoPlayer) || ((IsoPlayer)this.chr).getNutrition().canAddFitnessXp()) {
				if (perk.getType() == PerkFactory.Perks.Strength && this.chr instanceof IsoPlayer) {
					if (((IsoPlayer)this.chr).getNutrition().getProteins() > 50.0F && ((IsoPlayer)this.chr).getNutrition().getProteins() < 300.0F) {
						float1 = (float)((double)float1 * 1.5);
					}

					if (((IsoPlayer)this.chr).getNutrition().getProteins() < -300.0F) {
						float1 = (float)((double)float1 * 0.7);
					}
				}

				float float2 = this.getXP(perks);
				float float3 = perk.getTotalXpForLevel(10);
				if (!(float1 >= 0.0F) || !(float2 >= float3)) {
					float float4 = 1.0F;
					float float5;
					if (boolean3) {
						boolean boolean5 = false;
						Iterator iterator = IsoGameCharacter.this.getDescriptor().getXPBoostMap().entrySet().iterator();
						label183: while (true) {
							while (true) {
								Entry entry;
								do {
									if (!iterator.hasNext()) {
										if (!boolean5 && !this.isSkillExcludedFromSpeedReduction(perk.getType())) {
											float4 = 0.25F;
										}

										if (IsoGameCharacter.this.HasTrait("FastLearner") && !this.isSkillExcludedFromSpeedIncrease(perk.getType())) {
											float4 *= 1.3F;
										}

										if (IsoGameCharacter.this.HasTrait("SlowLearner") && !this.isSkillExcludedFromSpeedReduction(perk.getType())) {
											float4 *= 0.7F;
										}

										if (IsoGameCharacter.this.HasTrait("Pacifist")) {
											if (perk.getType() != PerkFactory.Perks.Axe && perk.getType() != PerkFactory.Perks.BluntGuard && perk.getType() != PerkFactory.Perks.BluntMaintenance && perk.getType() != PerkFactory.Perks.BladeGuard && perk.getType() != PerkFactory.Perks.BladeMaintenance) {
												if (perk.getType() == PerkFactory.Perks.Blunt) {
													float4 *= 0.75F;
												} else if (perk.getType() == PerkFactory.Perks.Aiming) {
													float4 *= 0.75F;
												}
											} else {
												float4 *= 0.75F;
											}
										}

										float1 *= float4;
										float5 = this.getMultiplier(perks);
										if (float5 > 1.0F) {
											float1 *= float5;
										}

										if (!perk.isPassiv()) {
											float1 = (float)((double)float1 * SandboxOptions.instance.XpMultiplier.getValue());
										}

										break label183;
									}

									entry = (Entry)iterator.next();
								}					 while (entry.getKey() != perk.getType());

								boolean5 = true;
								if ((Integer)entry.getValue() == 0 && !this.isSkillExcludedFromSpeedReduction((PerkFactory.Perks)entry.getKey())) {
									float4 *= 0.25F;
								} else if ((Integer)entry.getValue() == 1 && entry.getKey() == PerkFactory.Perks.Sprinting) {
									float4 = (float)((double)float4 * 1.25);
								} else if ((Integer)entry.getValue() == 1) {
									float4 = (float)((double)float4 * 1.0);
								} else if ((Integer)entry.getValue() == 2 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perks)entry.getKey())) {
									float4 = (float)((double)float4 * 1.33);
								} else if ((Integer)entry.getValue() >= 3 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perks)entry.getKey())) {
									float4 = (float)((double)float4 * 1.66);
								}
							}
						}
					}

					float float6 = float2 + float1;
					if (float6 < 0.0F) {
						float6 = 0.0F;
						float1 = -float2;
					}

					if (float6 > float3) {
						float6 = float3;
						float1 = float3 - float2;
					}

					this.XPMap.put(perks, float6);
					PerkFactory.CheckForUnlockedPerks(this.chr, perk);
					float5 = perk.getTotalXpForLevel(this.chr.getPerkLevel(perk.type) + 1);
					if (!perk.isPassiv() && float2 < float5 && float6 >= float5 && this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && IsoGameCharacter.this.NumberOfPerksToPick > 0 && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
						this.chr.getEmitter().playSoundImpl("GainExperienceLevel", (IsoObject)null);
					}

					IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.getMultiplierMap().get(perk.type);
					if (xPMultiplier != null) {
						float float7 = perk.getTotalXpForLevel(xPMultiplier.minLevel - 1);
						float float8 = perk.getTotalXpForLevel(xPMultiplier.maxLevel);
						if (float2 >= float7 && float6 < float7 || float2 < float8 && float6 >= float8) {
							this.getMultiplierMap().remove(perk.type);
						}
					}

					if (!perk.isPassiv() && boolean2) {
						this.addGlobalXP(float1);
					}

					if (boolean1) {
						LuaEventManager.triggerEventGarbage("AddXP", this.chr, perks, float1);
					}
				}
			}
		}

		public void addGlobalXP(float float1) {
			this.TotalXP += float1;
			float float2 = (float)IsoGameCharacter.this.getXpForLevel(this.getLevel());
			boolean boolean1 = false;
			for (int int1 = 0; int1 < PerkFactory.PerkList.size(); ++int1) {
				PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkList.get(int1);
				int int2 = this.chr.getPerkLevel(perk.type);
				if (!perk.isPassiv() && int2 < 10 && this.getXP(perk.type) >= perk.getTotalXpForLevel(int2 + 1)) {
					boolean1 = true;
					break;
				}
			}

			for (; this.TotalXP >= float2; float2 = (float)IsoGameCharacter.this.getXpForLevel(this.getLevel())) {
				this.setLevel(this.getLevel() + 1);
				++this.chr.NumberOfPerksToPick;
				if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && boolean1 && IsoGameCharacter.this.NumberOfPerksToPick > 0 && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
					this.chr.getEmitter().playSound("GainExperienceLevel");
				}
			}
		}

		private boolean isSkillExcludedFromSpeedReduction(PerkFactory.Perks perks) {
			if (perks == PerkFactory.Perks.Sprinting) {
				return true;
			} else if (perks == PerkFactory.Perks.Fitness) {
				return true;
			} else {
				return perks == PerkFactory.Perks.Strength;
			}
		}

		private boolean isSkillExcludedFromSpeedIncrease(PerkFactory.Perks perks) {
			if (perks == PerkFactory.Perks.Fitness) {
				return true;
			} else {
				return perks == PerkFactory.Perks.Strength;
			}
		}

		public float getXP(PerkFactory.Perks perks) {
			return this.XPMap.containsKey(perks) ? (Float)this.XPMap.get(perks) : 0.0F;
		}

		public void AddXP(HandWeapon handWeapon, int int1) {
		}

		public void setTotalXP(float float1) {
			this.TotalXP = float1;
		}

		public void load(ByteBuffer byteBuffer, int int1) throws IOException {
			int int2 = byteBuffer.getInt();
			this.chr.Traits.clear();
			int int3;
			for (int3 = 0; int3 < int2; ++int3) {
				String string = GameWindow.ReadString(byteBuffer);
				if (TraitFactory.getTrait(string) != null) {
					if (!this.chr.Traits.contains(string)) {
						this.chr.Traits.add(string);
					}
				} else {
					DebugLog.log("ERROR: unknown trait \"" + string + "\"");
				}
			}

			if (int1 >= 112) {
				this.TotalXP = byteBuffer.getFloat();
			} else {
				this.TotalXP = (float)byteBuffer.getInt();
			}

			this.level = byteBuffer.getInt();
			this.lastlevel = byteBuffer.getInt();
			this.XPMap.clear();
			int3 = byteBuffer.getInt();
			int int4;
			for (int4 = 0; int4 < int3; ++int4) {
				this.XPMap.put(PerkFactory.Perks.fromIndex(byteBuffer.getInt()), (float)byteBuffer.getInt());
			}

			int4 = byteBuffer.getInt();
			this.chr.CanUpgradePerk.clear();
			int int5;
			for (int5 = 0; int5 < int4; ++int5) {
				this.chr.CanUpgradePerk.add(PerkFactory.Perks.fromIndex(byteBuffer.getInt()));
			}

			IsoGameCharacter.this.PerkList.clear();
			int5 = byteBuffer.getInt();
			int int6;
			for (int6 = 0; int6 < int5; ++int6) {
				PerkFactory.Perks perks = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
				PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkMap.get(perks);
				IsoGameCharacter.PerkInfo perkInfo = IsoGameCharacter.this.new PerkInfo();
				perkInfo.perk = perk;
				perkInfo.perkType = perks;
				perkInfo.level = byteBuffer.getInt();
				IsoGameCharacter.this.PerkList.add(perkInfo);
			}

			int6 = byteBuffer.getInt();
			for (int int7 = 0; int7 < int6; ++int7) {
				PerkFactory.Perks perks2;
				float float1;
				if (int1 >= 60) {
					perks2 = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
					float1 = byteBuffer.getFloat();
					byte byte1 = byteBuffer.get();
					byte byte2 = byteBuffer.get();
					this.addXpMultiplier(perks2, float1, byte1, byte2);
				} else {
					int int8;
					int int9;
					if (int1 < 57) {
						perks2 = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
						float1 = (float)byteBuffer.getInt();
						int8 = this.chr.getPerkLevel(perks2) * 2 + 1;
						int9 = int8 + 1;
						this.addXpMultiplier(perks2, float1, int8, int9);
					} else {
						perks2 = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
						float1 = (float)byteBuffer.getInt();
						int8 = this.chr.getPerkLevel(perks2) + 1;
						if (int8 == 2 || int8 == 4 || int8 == 6 || int8 == 8 || int8 == 10) {
							--int8;
						}

						int9 = int8 + 1;
						this.addXpMultiplier(perks2, float1, int8, int9);
					}
				}
			}

			if (this.TotalXP > (float)IsoGameCharacter.this.getXpForLevel(this.getLevel() + 1)) {
				this.setTotalXP((float)this.chr.getXpForLevel(this.getLevel()));
				this.addGlobalXP(1.0F);
			}

			this.convertXp(int1);
		}

		public void save(ByteBuffer byteBuffer) throws IOException {
			byteBuffer.putInt(this.chr.Traits.size());
			for (int int1 = 0; int1 < this.chr.Traits.size(); ++int1) {
				GameWindow.WriteString(byteBuffer, (String)this.chr.Traits.get(int1));
			}

			byteBuffer.putFloat(this.TotalXP);
			byteBuffer.putInt(this.level);
			byteBuffer.putInt(this.lastlevel);
			byteBuffer.putInt(this.XPMap.size());
			Iterator iterator = this.XPMap.entrySet().iterator();
			while (iterator != null && iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				byteBuffer.putInt(((PerkFactory.Perks)entry.getKey()).index());
				byteBuffer.putInt(((Float)entry.getValue()).intValue());
			}

			byteBuffer.putInt(this.chr.CanUpgradePerk.size());
			int int2;
			for (int2 = 0; int2 < this.chr.CanUpgradePerk.size(); ++int2) {
				int int3 = ((PerkFactory.Perks)this.chr.CanUpgradePerk.get(int2)).index();
				byteBuffer.putInt(int3);
			}

			byteBuffer.putInt(IsoGameCharacter.this.PerkList.size());
			for (int2 = 0; int2 < IsoGameCharacter.this.PerkList.size(); ++int2) {
				byteBuffer.putInt(((IsoGameCharacter.PerkInfo)IsoGameCharacter.this.PerkList.get(int2)).perkType.index());
				byteBuffer.putInt(((IsoGameCharacter.PerkInfo)IsoGameCharacter.this.PerkList.get(int2)).level);
			}

			byteBuffer.putInt(this.XPMapMultiplier.size());
			Iterator iterator2 = this.XPMapMultiplier.entrySet().iterator();
			while (iterator2 != null && iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				byteBuffer.putInt(((PerkFactory.Perks)entry2.getKey()).index());
				byteBuffer.putFloat(((IsoGameCharacter.XPMultiplier)entry2.getValue()).multiplier);
				byteBuffer.put((byte)((IsoGameCharacter.XPMultiplier)entry2.getValue()).minLevel);
				byteBuffer.put((byte)((IsoGameCharacter.XPMultiplier)entry2.getValue()).maxLevel);
			}
		}

		private void convertXp(int int1) {
			if (int1 < 57) {
				HashMap hashMap = new HashMap();
				hashMap.put(PerkFactory.Perks.BluntParent, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.BladeParent, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Blunt, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.BluntGuard, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.BluntMaintenance, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Axe, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.BladeGuard, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.BladeMaintenance, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Firearm, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Aiming, new float[]{50.0F, 150.0F, 750.0F, 2650.0F, 4150.0F});
				hashMap.put(PerkFactory.Perks.Reloading, new float[]{50.0F, 150.0F, 450.0F, 1050.0F, 1950.0F});
				hashMap.put(PerkFactory.Perks.Crafting, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Woodwork, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Cooking, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Farming, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Doctor, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Survivalist, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Fishing, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Trapping, new float[]{50.0F, 150.0F, 450.0F, 1050.0F, 1950.0F});
				hashMap.put(PerkFactory.Perks.PlantScavenging, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Passiv, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Fitness, new float[]{2000.0F, 5000.0F, 23000.0F, 81000.0F, 169000.0F});
				hashMap.put(PerkFactory.Perks.Strength, new float[]{2000.0F, 5000.0F, 23000.0F, 81000.0F, 169000.0F});
				hashMap.put(PerkFactory.Perks.Agility, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Sprinting, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Lightfoot, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Nimble, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				hashMap.put(PerkFactory.Perks.Sneak, new float[]{50.0F, 150.0F, 750.0F, 3650.0F, 5450.0F});
				for (int int2 = 0; int2 < PerkFactory.PerkList.size(); ++int2) {
					PerkFactory.Perk perk = (PerkFactory.Perk)PerkFactory.PerkList.get(int2);
					if (this.XPMap.containsKey(perk.type)) {
						float float1 = 0.0F;
						float[] floatArray = (float[])hashMap.get(perk.type);
						for (int int3 = 0; int3 < 5; ++int3) {
							floatArray[int3] *= 2.0F;
							float1 += floatArray[int3];
						}

						float float2 = (Float)this.XPMap.get(perk.type);
						float2 = Math.max(float2, 0.0F);
						float2 = Math.min(float2, float1);
						float float3 = 0.0F;
						for (int int4 = 1; int4 <= 5; ++int4) {
							if (float2 >= float3 && (float2 == float1 || float2 < float3 + floatArray[int4 - 1])) {
								float float4 = (float2 - float3) / floatArray[int4 - 1];
								int int5 = int4 * 2;
								float float5 = perk.getTotalXpForLevel(int5 - 2);
								float float6 = perk.getTotalXpForLevel(int5);
								float float7 = float5 + (float6 - float5) * float4;
								this.XPMap.put(perk.type, float7);
								DebugLog.log("XP updated: " + perk.getName() + " " + float2 + " -> " + float7);
								break;
							}

							float3 += floatArray[int4 - 1];
						}
					}

					IsoGameCharacter.PerkInfo perkInfo = IsoGameCharacter.this.getPerkInfo(perk.type);
					if (perkInfo != null) {
						if (perk.type != PerkFactory.Perks.Fitness && perk.type != PerkFactory.Perks.Strength) {
							perkInfo.level *= 2;
						} else {
							float float8 = this.getXP(perk.type);
							switch (perkInfo.level) {
							case 0: 
								if (float8 >= perk.getTotalXpForLevel(1)) {
									perkInfo.level = 1;
								}

								break;
							
							case 1: 
								if (float8 >= perk.getTotalXpForLevel(4)) {
									perkInfo.level = 4;
								} else if (float8 >= perk.getTotalXpForLevel(3)) {
									perkInfo.level = 3;
								} else {
									perkInfo.level = 2;
								}

								break;
							
							case 2: 
								perkInfo.level = 5;
								break;
							
							case 3: 
								if (float8 >= perk.getTotalXpForLevel(8)) {
									perkInfo.level = 8;
								} else if (float8 >= perk.getTotalXpForLevel(7)) {
									perkInfo.level = 7;
								} else {
									perkInfo.level = 6;
								}

								break;
							
							case 4: 
								perkInfo.level = 9;
								break;
							
							case 5: 
								perkInfo.level = 10;
							
							}
						}
					}
				}
			}
		}

		public void setXPToLevel(PerkFactory.Perks perks, int int1) {
			PerkFactory.Perk perk = null;
			for (int int2 = 0; int2 < PerkFactory.PerkList.size(); ++int2) {
				PerkFactory.Perk perk2 = (PerkFactory.Perk)PerkFactory.PerkList.get(int2);
				if (perk2.getType() == perks) {
					perk = perk2;
					break;
				}
			}

			if (perk != null) {
				this.XPMap.put(perks, perk.getTotalXpForLevel(int1));
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
