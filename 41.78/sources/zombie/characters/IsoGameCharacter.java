package zombie.characters;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import fmod.fmod.IFMODParameterUpdater;
import gnu.trove.map.hash.THashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.AmbientStreamManager;
import zombie.DebugFileWatcher;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.PersistentOutfits;
import zombie.PredicatedFileWatcher;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.ai.GameCharacterAIBrain;
import zombie.ai.MapKnowledge;
import zombie.ai.State;
import zombie.ai.StateMachine;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.AStarPathFinderResult;
import zombie.ai.sadisticAIDirector.SleepingEventData;
import zombie.ai.states.AttackNetworkState;
import zombie.ai.states.AttackState;
import zombie.ai.states.BumpedState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.IdleState;
import zombie.ai.states.LungeNetworkState;
import zombie.ai.states.LungeState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieFallingState;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.FMODParameter;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSoundClip;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartLast;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Metabolics;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.CharacterTimedActions.LuaTimedActionNew;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionState;
import zombie.characters.action.ActionStateSnapshot;
import zombie.characters.action.IActionStateChanged;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitCollection;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.chat.ChatElementOwner;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableHandle;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackBool;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackFloat;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackInt;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSlotCallbackString;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.AnimationVariableType;
import zombie.core.skinnedmodel.advancedanimation.IAnimEventCallback;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableMap;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceTextureCreator;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.IClothingItemListener;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.debug.LogSeverity;
import zombie.gameStates.IngameState;
import zombie.input.Mouse;
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
import zombie.inventory.types.WeaponType;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoRoofFixer;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.objects.IsoBall;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFallingClothing;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.PacketValidator;
import zombie.network.ServerGUI;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.network.Userlog;
import zombie.network.chat.ChatServer;
import zombie.network.chat.ChatType;
import zombie.network.packets.hit.AttackVars;
import zombie.network.packets.hit.HitInfo;
import zombie.popman.ObjectPool;
import zombie.profanity.ProfanityFilter;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.ActionProgressBar;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;


public abstract class IsoGameCharacter extends IsoMovingObject implements Talker,ChatElementOwner,IAnimatable,IAnimationVariableMap,IClothingItemListener,IActionStateChanged,IAnimEventCallback,IFMODParameterUpdater,ILuaVariableSource,ILuaGameCharacter {
	private boolean ignoreAimingInput = false;
	public boolean doRenderShadow = true;
	private boolean doDeathSound = true;
	private boolean canShout = true;
	public boolean doDirtBloodEtc = true;
	private static int IID = 0;
	public static final int RENDER_OFFSET_X = 1;
	public static final int RENDER_OFFSET_Y = -89;
	public static final float s_maxPossibleTwist = 70.0F;
	private static final HashMap SurvivorMap = new HashMap();
	private static final int[] LevelUpLevels = new int[]{25, 75, 150, 225, 300, 400, 500, 600, 700, 800, 900, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 3200, 3400, 3600, 3800, 4000, 4400, 4800, 5200, 5600, 6000};
	protected static final Vector2 tempo = new Vector2();
	protected static final ColorInfo inf = new ColorInfo();
	public long vocalEvent;
	public long removedFromWorldMS = 0L;
	private boolean bAutoWalk = false;
	private final Vector2 autoWalkDirection = new Vector2();
	private boolean bSneaking = false;
	protected static final Vector2 tempo2 = new Vector2();
	private static final Vector2 tempVector2_1 = new Vector2();
	private static final Vector2 tempVector2_2 = new Vector2();
	private static String sleepText = null;
	protected final ArrayList savedInventoryItems = new ArrayList();
	private final String instancename;
	protected GameCharacterAIBrain GameCharacterAIBrain;
	public final ArrayList amputations = new ArrayList();
	public ModelInstance hair;
	public ModelInstance beard;
	public ModelInstance primaryHandModel;
	public ModelInstance secondaryHandModel;
	public final ActionContext actionContext = new ActionContext(this);
	public final BaseCharacterSoundEmitter emitter;
	private final FMODParameterList fmodParameters = new FMODParameterList();
	private final AnimationVariableSource m_GameVariables = new AnimationVariableSource();
	private AnimationVariableSource m_PlaybackGameVariables = null;
	private boolean bRunning = false;
	private boolean bSprinting = false;
	private boolean m_godMod = false;
	private boolean m_invisible = false;
	private boolean m_avoidDamage = false;
	public boolean callOut = false;
	public IsoGameCharacter ReanimatedCorpse;
	public int ReanimatedCorpseID = -1;
	private AnimationPlayer m_animPlayer = null;
	public final AdvancedAnimator advancedAnimator;
	public final HashMap StateMachineParams = new HashMap();
	public long clientIgnoreCollision = 0L;
	private boolean isCrit = false;
	private boolean bKnockedDown = false;
	public int bumpNbr = 0;
	public boolean upKillCount = true;
	private final ArrayList PerkList = new ArrayList();
	private final Vector2 m_forwardDirection = new Vector2();
	public boolean Asleep = false;
	public boolean blockTurning = false;
	public float speedMod = 1.0F;
	public IsoSprite legsSprite;
	private boolean bFemale = true;
	public float knockbackAttackMod = 1.0F;
	public final boolean[] IsVisibleToPlayer = new boolean[4];
	public float savedVehicleX;
	public float savedVehicleY;
	public short savedVehicleSeat = -1;
	public boolean savedVehicleRunning;
	private static final float RecoilDelayDecrease = 0.625F;
	protected static final float BeenMovingForIncrease = 1.25F;
	protected static final float BeenMovingForDecrease = 0.625F;
	private IsoGameCharacter FollowingTarget = null;
	private final ArrayList LocalList = new ArrayList();
	private final ArrayList LocalNeutralList = new ArrayList();
	private final ArrayList LocalGroupList = new ArrayList();
	private final ArrayList LocalRelevantEnemyList = new ArrayList();
	private float dangerLevels = 0.0F;
	private static final Vector2 tempVector2 = new Vector2();
	private float leaveBodyTimedown = 0.0F;
	protected boolean AllowConversation = true;
	private float ReanimateTimer;
	private int ReanimAnimFrame;
	private int ReanimAnimDelay;
	private boolean Reanim = false;
	private boolean VisibleToNPCs = true;
	private int DieCount = 0;
	private float llx = 0.0F;
	private float lly = 0.0F;
	private float llz = 0.0F;
	protected int RemoteID = -1;
	protected int NumSurvivorsInVicinity = 0;
	private float LevelUpMultiplier = 2.5F;
	protected IsoGameCharacter.XP xp = null;
	private int LastLocalEnemies = 0;
	private final ArrayList VeryCloseEnemyList = new ArrayList();
	private final HashMap LastKnownLocation = new HashMap();
	protected IsoGameCharacter AttackedBy = null;
	protected boolean IgnoreStaggerBack = false;
	protected boolean AttackWasSuperAttack = false;
	private int TimeThumping = 0;
	private int PatienceMax = 150;
	private int PatienceMin = 20;
	private int Patience = 20;
	protected final Stack CharacterActions = new Stack();
	private int ZombieKills = 0;
	private int SurvivorKills = 0;
	private int LastZombieKills = 0;
	protected boolean superAttack = false;
	protected float ForceWakeUpTime = -1.0F;
	private float fullSpeedMod = 1.0F;
	protected float runSpeedModifier = 1.0F;
	private float walkSpeedModifier = 1.0F;
	private float combatSpeedModifier = 1.0F;
	private boolean bRangedWeaponEmpty = false;
	public ArrayList bagsWorn;
	protected boolean ForceWakeUp;
	protected final BodyDamage BodyDamage;
	private BodyDamage BodyDamageRemote = null;
	private State defaultState;
	protected WornItems wornItems = null;
	protected AttachedItems attachedItems = null;
	protected ClothingWetness clothingWetness = null;
	protected SurvivorDesc descriptor;
	private final Stack FamiliarBuildings = new Stack();
	protected final AStarPathFinderResult finder = new AStarPathFinderResult();
	private float FireKillRate = 0.0038F;
	private int FireSpreadProbability = 6;
	protected float Health = 1.0F;
	protected boolean bDead = false;
	protected boolean bKill = false;
	protected boolean bPlayingDeathSound = false;
	private boolean bDeathDragDown = false;
	protected String hurtSound = "MaleZombieHurt";
	protected ItemContainer inventory = new ItemContainer();
	protected InventoryItem leftHandItem;
	private int NextWander = 200;
	private boolean OnFire = false;
	private int pathIndex = 0;
	protected InventoryItem rightHandItem;
	protected Color SpeakColour = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	protected float slowFactor = 0.0F;
	protected float slowTimer = 0.0F;
	protected boolean bUseParts = false;
	protected boolean Speaking = false;
	private float SpeakTime = 0.0F;
	private float staggerTimeMod = 1.0F;
	protected final StateMachine stateMachine;
	protected final Moodles Moodles;
	protected final Stats stats = new Stats();
	private final Stack UsedItemsOn = new Stack();
	protected HandWeapon useHandWeapon = null;
	protected IsoGridSquare attackTargetSquare;
	private float BloodImpactX = 0.0F;
	private float BloodImpactY = 0.0F;
	private float BloodImpactZ = 0.0F;
	private IsoSprite bloodSplat;
	private boolean bOnBed = false;
	private final Vector2 moveForwardVec = new Vector2();
	protected boolean pathing = false;
	protected ChatElement chatElement;
	private final Stack LocalEnemyList = new Stack();
	protected final Stack EnemyList = new Stack();
	public final IsoGameCharacter.CharacterTraits Traits = new IsoGameCharacter.CharacterTraits();
	private int maxWeight = 8;
	private int maxWeightBase = 8;
	private float SleepingTabletDelta = 1.0F;
	private float BetaEffect = 0.0F;
	private float DepressEffect = 0.0F;
	private float SleepingTabletEffect = 0.0F;
	private float BetaDelta = 0.0F;
	private float DepressDelta = 0.0F;
	private float DepressFirstTakeTime = -1.0F;
	private float PainEffect = 0.0F;
	private float PainDelta = 0.0F;
	private boolean bDoDefer = true;
	private float haloDispTime = 128.0F;
	protected TextDrawObject userName;
	private TextDrawObject haloNote;
	private final HashMap namesPrefix = new HashMap();
	private static final String namePvpSuffix = " [img=media/ui/Skull.png]";
	private static final String nameCarKeySuffix = " [img=media/ui/CarKey.png";
	private static final String voiceSuffix = "[img=media/ui/voiceon.png] ";
	private static final String voiceMuteSuffix = "[img=media/ui/voicemuted.png] ";
	protected IsoPlayer isoPlayer = null;
	private boolean hasInitTextObjects = false;
	private boolean canSeeCurrent = false;
	private boolean drawUserName = false;
	private final IsoGameCharacter.Location LastHeardSound = new IsoGameCharacter.Location(-1, -1, -1);
	private float lrx = 0.0F;
	private float lry = 0.0F;
	protected boolean bClimbing = false;
	private boolean lastCollidedW = false;
	private boolean lastCollidedN = false;
	protected float fallTime = 0.0F;
	protected float lastFallSpeed = 0.0F;
	protected boolean bFalling = false;
	protected BaseVehicle vehicle = null;
	boolean isNPC = false;
	private long lastBump = 0L;
	private IsoGameCharacter bumpedChr = null;
	private boolean m_isCulled = true;
	private int age = 25;
	private int lastHitCount = 0;
	private Safety safety = new Safety(this);
	private float meleeDelay = 0.0F;
	private float RecoilDelay = 0.0F;
	private float BeenMovingFor = 0.0F;
	private float BeenSprintingFor = 0.0F;
	private boolean forceShove = false;
	private String clickSound = null;
	private float reduceInfectionPower = 0.0F;
	private final List knownRecipes = new ArrayList();
	private final HashSet knownMediaLines = new HashSet();
	private int lastHourSleeped = 0;
	protected float timeOfSleep = 0.0F;
	protected float delayToActuallySleep = 0.0F;
	private String bedType = "averageBed";
	private IsoObject bed = null;
	private boolean isReading = false;
	private float timeSinceLastSmoke = 0.0F;
	private boolean wasOnStairs = false;
	private ChatMessage lastChatMessage;
	private String lastSpokenLine;
	private boolean unlimitedEndurance = false;
	private boolean unlimitedCarry = false;
	private boolean buildCheat = false;
	private boolean farmingCheat = false;
	private boolean healthCheat = false;
	private boolean mechanicsCheat = false;
	private boolean movablesCheat = false;
	private boolean timedActionInstantCheat = false;
	private boolean showAdminTag = true;
	private long isAnimForecasted = 0L;
	private boolean fallOnFront = false;
	private boolean hitFromBehind = false;
	private String hitReaction = "";
	private String bumpType = "";
	private boolean m_isBumpDone = false;
	private boolean m_bumpFall = false;
	private boolean m_bumpStaggered = false;
	private String m_bumpFallType = "";
	private int sleepSpeechCnt = 0;
	private Radio equipedRadio;
	private InventoryItem leftHandCache;
	private InventoryItem rightHandCache;
	private final ArrayList ReadBooks = new ArrayList();
	private final IsoGameCharacter.LightInfo lightInfo = new IsoGameCharacter.LightInfo();
	private final IsoGameCharacter.LightInfo lightInfo2 = new IsoGameCharacter.LightInfo();
	private PolygonalMap2.Path path2;
	private final MapKnowledge mapKnowledge = new MapKnowledge();
	public final AttackVars attackVars = new AttackVars();
	public final ArrayList hitList = new ArrayList();
	private final PathFindBehavior2 pfb2 = new PathFindBehavior2(this);
	private final InventoryItem[] cacheEquiped = new InventoryItem[2];
	private boolean bAimAtFloor = false;
	protected int m_persistentOutfitId = 0;
	protected boolean m_bPersistentOutfitInit = false;
	private boolean bUpdateModelTextures = false;
	private ModelInstanceTextureCreator textureCreator = null;
	public boolean bUpdateEquippedTextures = false;
	private final ArrayList readyModelData = new ArrayList();
	private boolean sitOnGround = false;
	private boolean ignoreMovement = false;
	private boolean hideWeaponModel = false;
	private boolean isAiming = false;
	private float beardGrowTiming = -1.0F;
	private float hairGrowTiming = -1.0F;
	private float m_moveDelta = 1.0F;
	protected float m_turnDeltaNormal = 1.0F;
	protected float m_turnDeltaRunning = 0.8F;
	protected float m_turnDeltaSprinting = 0.75F;
	private float m_maxTwist = 15.0F;
	private boolean m_isMoving = false;
	private boolean m_isTurning = false;
	private boolean m_isTurningAround = false;
	private boolean m_isTurning90 = false;
	public long lastAutomaticShoot = 0L;
	public int shootInARow = 0;
	private boolean invincible = false;
	private float lungeFallTimer = 0.0F;
	private SleepingEventData m_sleepingEventData;
	private final int HAIR_GROW_TIME = 20;
	private final int BEARD_GROW_TIME = 5;
	public float realx = 0.0F;
	public float realy = 0.0F;
	public byte realz = 0;
	public NetworkVariables.ZombieState realState;
	public IsoDirections realdir;
	public String overridePrimaryHandModel;
	public String overrideSecondaryHandModel;
	public boolean forceNullOverride;
	protected final UpdateLimit ulBeatenVehicle;
	private float m_momentumScalar;
	private final HashMap m_stateUpdateLookup;
	private boolean attackAnim;
	private NetworkTeleport teleport;
	@Deprecated
	public ArrayList invRadioFreq;
	private final PredicatedFileWatcher m_animStateTriggerWatcher;
	private final AnimationPlayerRecorder m_animationRecorder;
	private final String m_UID;
	private boolean m_bDebugVariablesRegistered;
	private float effectiveEdibleBuffTimer;
	private float m_shadowFM;
	private float m_shadowBM;
	private long shadowTick;
	private static final ItemVisuals tempItemVisuals = new ItemVisuals();
	private static final ArrayList movingStatic = new ArrayList();
	private long m_muzzleFlash;
	private static final IsoGameCharacter.Bandages s_bandages = new IsoGameCharacter.Bandages();
	private static final Vector3 tempVector = new Vector3();
	private static final Vector3 tempVectorBonePos = new Vector3();
	public final NetworkCharacter networkCharacter;

	public IsoGameCharacter(IsoCell cell, float float1, float float2, float float3) {
		super(cell, false);
		this.realState = NetworkVariables.ZombieState.Idle;
		this.realdir = IsoDirections.fromIndex(0);
		this.overridePrimaryHandModel = null;
		this.overrideSecondaryHandModel = null;
		this.forceNullOverride = false;
		this.ulBeatenVehicle = new UpdateLimit(200L);
		this.m_momentumScalar = 0.0F;
		this.m_stateUpdateLookup = new HashMap();
		this.attackAnim = false;
		this.teleport = null;
		this.invRadioFreq = new ArrayList();
		this.m_bDebugVariablesRegistered = false;
		this.effectiveEdibleBuffTimer = 0.0F;
		this.m_shadowFM = 0.0F;
		this.m_shadowBM = 0.0F;
		this.shadowTick = -1L;
		this.m_muzzleFlash = -1L;
		this.networkCharacter = new NetworkCharacter();
		this.m_UID = String.format("%s-%s", this.getClass().getSimpleName(), UUID.randomUUID().toString());
		this.registerVariableCallbacks();
		this.instancename = "Character" + IID;
		++IID;
		if (!(this instanceof IsoSurvivor)) {
			this.emitter = (BaseCharacterSoundEmitter)(!Core.SoundDisabled && !GameServer.bServer ? new CharacterSoundEmitter(this) : new DummyCharacterSoundEmitter(this));
		} else {
			this.emitter = null;
		}

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

		if (this instanceof IsoPlayer) {
			this.BodyDamage = new BodyDamage(this);
			this.Moodles = new Moodles(this);
			this.xp = new IsoGameCharacter.XP(this);
		} else {
			this.BodyDamage = null;
			this.Moodles = null;
			this.xp = null;
		}

		this.Patience = Rand.Next(this.PatienceMin, this.PatienceMax);
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
		this.setDefaultState(IdleState.instance());
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

		this.m_animationRecorder = new AnimationPlayerRecorder(this);
		this.advancedAnimator = new AdvancedAnimator();
		this.advancedAnimator.init(this);
		this.advancedAnimator.animCallbackHandlers.add(this);
		this.advancedAnimator.SetAnimSet(AnimationSet.GetAnimationSet(this.GetAnimSetName(), false));
		this.advancedAnimator.setRecorder(this.m_animationRecorder);
		this.actionContext.onStateChanged.add(this);
		this.m_animStateTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetAnimState.xml"), AnimStateTriggerXmlFile.class, this::onTrigger_setAnimStateToTriggerFile);
	}

	private void registerVariableCallbacks() {
		this.setVariable("hitreaction", this::getHitReaction, this::setHitReaction);
		this.setVariable("collidetype", this::getCollideType, this::setCollideType);
		this.setVariable("footInjuryType", this::getFootInjuryType);
		this.setVariable("bumptype", this::getBumpType, this::setBumpType);
		this.setVariable("sitonground", this::isSitOnGround, this::setSitOnGround);
		this.setVariable("canclimbdownrope", this::canClimbDownSheetRopeInCurrentSquare);
		this.setVariable("frombehind", this::isHitFromBehind, this::setHitFromBehind);
		this.setVariable("fallonfront", this::isFallOnFront, this::setFallOnFront);
		this.setVariable("hashitreaction", this::hasHitReaction);
		this.setVariable("intrees", this::isInTreesNoBush);
		this.setVariable("bumped", this::isBumped);
		this.setVariable("BumpDone", false, this::isBumpDone, this::setBumpDone);
		this.setVariable("BumpFall", false, this::isBumpFall, this::setBumpFall);
		this.setVariable("BumpFallType", "", this::getBumpFallType, this::setBumpFallType);
		this.setVariable("BumpStaggered", false, this::isBumpStaggered, this::setBumpStaggered);
		this.setVariable("bonfloor", this::isOnFloor, this::setOnFloor);
		this.setVariable("rangedweaponempty", this::isRangedWeaponEmpty, this::setRangedWeaponEmpty);
		this.setVariable("footInjury", this::hasFootInjury);
		this.setVariable("ChopTreeSpeed", 1.0F, this::getChopTreeSpeed);
		this.setVariable("MoveDelta", 1.0F, this::getMoveDelta, this::setMoveDelta);
		this.setVariable("TurnDelta", 1.0F, this::getTurnDelta, this::setTurnDelta);
		this.setVariable("angle", this::getDirectionAngle, this::setDirectionAngle);
		this.setVariable("animAngle", this::getAnimAngle);
		this.setVariable("twist", this::getTwist);
		this.setVariable("targetTwist", this::getTargetTwist);
		this.setVariable("maxTwist", this.m_maxTwist, this::getMaxTwist, this::setMaxTwist);
		this.setVariable("shoulderTwist", this::getShoulderTwist);
		this.setVariable("excessTwist", this::getExcessTwist);
		this.setVariable("angleStepDelta", this::getAnimAngleStepDelta);
		this.setVariable("angleTwistDelta", this::getAnimAngleTwistDelta);
		this.setVariable("isTurning", false, this::isTurning, this::setTurning);
		this.setVariable("isTurning90", false, this::isTurning90, this::setTurning90);
		this.setVariable("isTurningAround", false, this::isTurningAround, this::setTurningAround);
		this.setVariable("bMoving", false, this::isMoving, this::setMoving);
		this.setVariable("beenMovingFor", this::getBeenMovingFor);
		this.setVariable("previousState", this::getPreviousActionContextStateName);
		this.setVariable("momentumScalar", this::getMomentumScalar, this::setMomentumScalar);
		this.setVariable("hasTimedActions", this::hasTimedActions);
		if (DebugOptions.instance.Character.Debug.RegisterDebugVariables.getValue()) {
			this.registerDebugGameVariables();
		}

		this.setVariable("CriticalHit", this::isCriticalHit, this::setCriticalHit);
		this.setVariable("bKnockedDown", this::isKnockedDown, this::setKnockedDown);
		this.setVariable("bdead", this::isDead);
	}

	public void updateRecoilVar() {
		this.setVariable("recoilVarY", 0.0F);
		this.setVariable("recoilVarX", 0.0F + (float)this.getPerkLevel(PerkFactory.Perks.Aiming) / 10.0F);
	}

	private void registerDebugGameVariables() {
		for (int int1 = 0; int1 < 2; ++int1) {
			for (int int2 = 0; int2 < 9; ++int2) {
				this.dbgRegisterAnimTrackVariable(int1, int2);
			}
		}

		this.setVariable("dbg.anm.dx", ()->{
			return this.getDeferredMovement(tempo).x / GameTime.instance.getMultiplier();
		});
		this.setVariable("dbg.anm.dy", ()->{
			return this.getDeferredMovement(tempo).y / GameTime.instance.getMultiplier();
		});
		this.setVariable("dbg.anm.da", ()->{
			return this.getDeferredAngleDelta() / GameTime.instance.getMultiplier();
		});
		this.setVariable("dbg.anm.daw", this::getDeferredRotationWeight);
		this.setVariable("dbg.forward", ()->{
			float float1 = this.getForwardDirection().x;
			return float1 + "; " + this.getForwardDirection().y;
		});
		this.setVariable("dbg.anm.blend.fbx_x", ()->{
			return DebugOptions.instance.Animation.BlendUseFbx.getValue() ? 1.0F : 0.0F;
		});
		this.m_bDebugVariablesRegistered = true;
	}

	private void dbgRegisterAnimTrackVariable(int int1, int int2) {
		this.setVariable(String.format("dbg.anm.track%d%d", int1, int2), ()->{
			return this.dbgGetAnimTrackName(int1, int2);
		});
		this.setVariable(String.format("dbg.anm.t.track%d%d", int1, int2), ()->{
			return this.dbgGetAnimTrackTime(int1, int2);
		});
		this.setVariable(String.format("dbg.anm.w.track%d%d", int1, int2), ()->{
			return this.dbgGetAnimTrackWeight(int1, int2);
		});
	}

	public float getMomentumScalar() {
		return this.m_momentumScalar;
	}

	public void setMomentumScalar(float float1) {
		this.m_momentumScalar = float1;
	}

	public Vector2 getDeferredMovement(Vector2 vector2) {
		if (this.m_animPlayer == null) {
			vector2.set(0.0F, 0.0F);
			return vector2;
		} else {
			this.m_animPlayer.getDeferredMovement(vector2);
			return vector2;
		}
	}

	public float getDeferredAngleDelta() {
		return this.m_animPlayer == null ? 0.0F : this.m_animPlayer.getDeferredAngleDelta() * 57.295776F;
	}

	public float getDeferredRotationWeight() {
		return this.m_animPlayer == null ? 0.0F : this.m_animPlayer.getDeferredRotationWeight();
	}

	public boolean isStrafing() {
		return this.getPath2() != null && this.pfb2.isStrafing() ? true : this.isAiming();
	}

	public AnimationTrack dbgGetAnimTrack(int int1, int int2) {
		if (this.m_animPlayer == null) {
			return null;
		} else {
			AnimationPlayer animationPlayer = this.m_animPlayer;
			AnimationMultiTrack animationMultiTrack = animationPlayer.getMultiTrack();
			List list = animationMultiTrack.getTracks();
			AnimationTrack animationTrack = null;
			int int3 = 0;
			int int4 = 0;
			for (int int5 = list.size(); int3 < int5; ++int3) {
				AnimationTrack animationTrack2 = (AnimationTrack)list.get(int3);
				int int6 = animationTrack2.getLayerIdx();
				if (int6 == int1) {
					if (int4 == int2) {
						animationTrack = animationTrack2;
						break;
					}

					++int4;
				}
			}

			return animationTrack;
		}
	}

	public String dbgGetAnimTrackName(int int1, int int2) {
		AnimationTrack animationTrack = this.dbgGetAnimTrack(int1, int2);
		return animationTrack != null ? animationTrack.name : "";
	}

	public float dbgGetAnimTrackTime(int int1, int int2) {
		AnimationTrack animationTrack = this.dbgGetAnimTrack(int1, int2);
		return animationTrack != null ? animationTrack.getCurrentTime() : 0.0F;
	}

	public float dbgGetAnimTrackWeight(int int1, int int2) {
		AnimationTrack animationTrack = this.dbgGetAnimTrack(int1, int2);
		return animationTrack != null ? animationTrack.BlendDelta : 0.0F;
	}

	public float getTwist() {
		return this.m_animPlayer != null ? 57.295776F * this.m_animPlayer.getTwistAngle() : 0.0F;
	}

	public float getShoulderTwist() {
		return this.m_animPlayer != null ? 57.295776F * this.m_animPlayer.getShoulderTwistAngle() : 0.0F;
	}

	public float getMaxTwist() {
		return this.m_maxTwist;
	}

	public void setMaxTwist(float float1) {
		this.m_maxTwist = float1;
	}

	public float getExcessTwist() {
		return this.m_animPlayer != null ? 57.295776F * this.m_animPlayer.getExcessTwistAngle() : 0.0F;
	}

	public float getAbsoluteExcessTwist() {
		return Math.abs(this.getExcessTwist());
	}

	public float getAnimAngleTwistDelta() {
		return this.m_animPlayer != null ? this.m_animPlayer.angleTwistDelta : 0.0F;
	}

	public float getAnimAngleStepDelta() {
		return this.m_animPlayer != null ? this.m_animPlayer.angleStepDelta : 0.0F;
	}

	public float getTargetTwist() {
		return this.m_animPlayer != null ? 57.295776F * this.m_animPlayer.getTargetTwistAngle() : 0.0F;
	}

	public boolean isRangedWeaponEmpty() {
		return this.bRangedWeaponEmpty;
	}

	public void setRangedWeaponEmpty(boolean boolean1) {
		this.bRangedWeaponEmpty = boolean1;
	}

	public boolean hasFootInjury() {
		return !StringUtils.isNullOrWhitespace(this.getFootInjuryType());
	}

	public boolean isInTrees2(boolean boolean1) {
		if (this.isCurrentState(BumpedState.instance())) {
			return false;
		} else {
			IsoGridSquare square = this.getCurrentSquare();
			if (square == null) {
				return false;
			} else {
				if (square.Has(IsoObjectType.tree)) {
					IsoTree tree = square.getTree();
					if (tree == null || boolean1 && tree.getSize() > 2 || !boolean1) {
						return true;
					}
				}

				String string = square.getProperties().Val("Movement");
				if (!"HedgeLow".equalsIgnoreCase(string) && !"HedgeHigh".equalsIgnoreCase(string)) {
					return !boolean1 && square.getProperties().Is("Bush");
				} else {
					return true;
				}
			}
		}
	}

	public boolean isInTreesNoBush() {
		return this.isInTrees2(true);
	}

	public boolean isInTrees() {
		return this.isInTrees2(false);
	}

	public static HashMap getSurvivorMap() {
		return SurvivorMap;
	}

	public static int[] getLevelUpLevels() {
		return LevelUpLevels;
	}

	public static Vector2 getTempo() {
		return tempo;
	}

	public static ColorInfo getInf() {
		return inf;
	}

	public GameCharacterAIBrain getBrain() {
		return this.GameCharacterAIBrain;
	}

	public boolean getIsNPC() {
		return this.isNPC;
	}

	public void setIsNPC(boolean boolean1) {
		this.isNPC = boolean1;
	}

	public BaseCharacterSoundEmitter getEmitter() {
		return this.emitter;
	}

	public void updateEmitter() {
		this.getFMODParameters().update();
		if (IsoWorld.instance.emitterUpdate || this.emitter.hasSoundsToStart()) {
			if (this.isZombie() && this.isProne()) {
				SwipeStatePlayer.getBoneWorldPos(this, "Bip01_Head", tempVectorBonePos);
				this.emitter.set(tempVectorBonePos.x, tempVectorBonePos.y, this.z);
				this.emitter.tick();
			} else {
				this.emitter.set(this.x, this.y, this.z);
				this.emitter.tick();
			}
		}
	}

	protected void doDeferredMovement() {
		if (GameClient.bClient && this.getHitReactionNetworkAI() != null) {
			if (this.getHitReactionNetworkAI().isStarted()) {
				this.getHitReactionNetworkAI().move();
				return;
			}

			if (this.isDead() && this.getHitReactionNetworkAI().isDoSkipMovement()) {
				return;
			}
		}

		AnimationPlayer animationPlayer = this.getAnimationPlayer();
		if (animationPlayer != null) {
			if (this.getPath2() != null && !this.isCurrentState(ClimbOverFenceState.instance()) && !this.isCurrentState(ClimbThroughWindowState.instance())) {
				if (this.isCurrentState(WalkTowardState.instance())) {
					DebugLog.General.warn("WalkTowardState but path2 != null");
					this.setPath2((PolygonalMap2.Path)null);
				}
			} else {
				if (GameClient.bClient) {
					if (this instanceof IsoZombie && ((IsoZombie)this).isRemoteZombie()) {
						if (this.getCurrentState() != ClimbOverFenceState.instance() && this.getCurrentState() != ClimbThroughWindowState.instance() && this.getCurrentState() != ClimbOverWallState.instance() && this.getCurrentState() != StaggerBackState.instance() && this.getCurrentState() != ZombieHitReactionState.instance() && this.getCurrentState() != ZombieFallDownState.instance() && this.getCurrentState() != ZombieFallingState.instance() && this.getCurrentState() != ZombieOnGroundState.instance() && this.getCurrentState() != AttackNetworkState.instance()) {
							return;
						}
					} else if (this instanceof IsoPlayer && !((IsoPlayer)this).isLocalPlayer() && !this.isCurrentState(CollideWithWallState.instance()) && !this.isCurrentState(PlayerGetUpState.instance()) && !this.isCurrentState(BumpedState.instance())) {
						return;
					}
				}

				Vector2 vector2 = tempo;
				this.getDeferredMovement(vector2);
				if (GameClient.bClient && this instanceof IsoZombie && this.isCurrentState(StaggerBackState.instance())) {
					float float1 = vector2.getLength();
					vector2.set(this.getHitDir());
					vector2.setLength(float1);
				}

				this.MoveUnmodded(vector2);
			}
		}
	}

	public ActionContext getActionContext() {
		return null;
	}

	public String getPreviousActionContextStateName() {
		ActionContext actionContext = this.getActionContext();
		return actionContext == null ? "" : actionContext.getPreviousStateName();
	}

	public String getCurrentActionContextStateName() {
		ActionContext actionContext = this.getActionContext();
		return actionContext == null ? "" : actionContext.getCurrentStateName();
	}

	public boolean hasAnimationPlayer() {
		return this.m_animPlayer != null;
	}

	public AnimationPlayer getAnimationPlayer() {
		Model model = ModelManager.instance.getBodyModel(this);
		boolean boolean1 = false;
		if (this.m_animPlayer != null && this.m_animPlayer.getModel() != model) {
			boolean1 = this.m_animPlayer.getMultiTrack().getTrackCount() > 0;
			this.m_animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.m_animPlayer);
		}

		if (this.m_animPlayer == null) {
			this.m_animPlayer = AnimationPlayer.alloc(model);
			this.onAnimPlayerCreated(this.m_animPlayer);
			if (boolean1) {
				this.getAdvancedAnimator().OnAnimDataChanged(false);
			}
		}

		return this.m_animPlayer;
	}

	public void releaseAnimationPlayer() {
		this.m_animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.m_animPlayer);
	}

	protected void onAnimPlayerCreated(AnimationPlayer animationPlayer) {
		animationPlayer.setRecorder(this.m_animationRecorder);
		animationPlayer.setTwistBones("Bip01_Pelvis", "Bip01_Spine", "Bip01_Spine1", "Bip01_Neck", "Bip01_Head");
		animationPlayer.setCounterRotationBone("Bip01");
	}

	protected void updateAnimationRecorderState() {
		if (this.m_animPlayer != null) {
			if (IsoWorld.isAnimRecorderDiscardTriggered()) {
				this.m_animPlayer.discardRecording();
			}

			boolean boolean1 = IsoWorld.isAnimRecorderActive();
			boolean boolean2 = boolean1 && !this.isSceneCulled();
			if (boolean2) {
				this.getAnimationPlayerRecorder().logCharacterPos();
			}

			this.m_animPlayer.setRecording(boolean2);
		}
	}

	public AdvancedAnimator getAdvancedAnimator() {
		return this.advancedAnimator;
	}

	public ModelInstance getModelInstance() {
		if (this.legsSprite == null) {
			return null;
		} else {
			return this.legsSprite.modelSlot == null ? null : this.legsSprite.modelSlot.model;
		}
	}

	public String getCurrentStateName() {
		return this.stateMachine.getCurrent() == null ? null : this.stateMachine.getCurrent().getName();
	}

	public String getPreviousStateName() {
		return this.stateMachine.getPrevious() == null ? null : this.stateMachine.getPrevious().getName();
	}

	public String getAnimationDebug() {
		if (this.advancedAnimator != null) {
			String string = this.instancename;
			return string + "\n" + this.advancedAnimator.GetDebug();
		} else {
			return this.instancename + "\n - No Animator";
		}
	}

	public String getTalkerType() {
		return this.chatElement.getTalkerType();
	}

	public boolean isAnimForecasted() {
		return System.currentTimeMillis() < this.isAnimForecasted;
	}

	public void setAnimForecasted(int int1) {
		this.isAnimForecasted = System.currentTimeMillis() + (long)int1;
	}

	public void resetModel() {
		ModelManager.instance.Reset(this);
	}

	public void resetModelNextFrame() {
		ModelManager.instance.ResetNextFrame(this);
	}

	protected void onTrigger_setClothingToXmlTriggerFile(TriggerXmlFile triggerXmlFile) {
		OutfitManager.Reload();
		String string;
		if (!StringUtils.isNullOrWhitespace(triggerXmlFile.outfitName)) {
			string = triggerXmlFile.outfitName;
			DebugLog.Clothing.debugln("Desired outfit name: " + string);
			Outfit outfit;
			if (triggerXmlFile.isMale) {
				outfit = OutfitManager.instance.FindMaleOutfit(string);
			} else {
				outfit = OutfitManager.instance.FindFemaleOutfit(string);
			}

			if (outfit == null) {
				DebugLog.Clothing.error("Could not find outfit: " + string);
				return;
			}

			if (this.bFemale == triggerXmlFile.isMale && this instanceof IHumanVisual) {
				((IHumanVisual)this).getHumanVisual().clear();
			}

			this.bFemale = !triggerXmlFile.isMale;
			if (this.descriptor != null) {
				this.descriptor.setFemale(this.bFemale);
			}

			this.dressInNamedOutfit(outfit.m_Name);
			this.advancedAnimator.OnAnimDataChanged(false);
			if (this instanceof IsoPlayer) {
				LuaEventManager.triggerEvent("OnClothingUpdated", this);
			}
		} else if (!StringUtils.isNullOrWhitespace(triggerXmlFile.clothingItemGUID)) {
			string = "game";
			this.dressInClothingItem(string + "-" + triggerXmlFile.clothingItemGUID);
			if (this instanceof IsoPlayer) {
				LuaEventManager.triggerEvent("OnClothingUpdated", this);
			}
		}

		ModelManager.instance.Reset(this);
	}

	protected void onTrigger_setAnimStateToTriggerFile(AnimStateTriggerXmlFile animStateTriggerXmlFile) {
		String string = this.GetAnimSetName();
		if (!StringUtils.equalsIgnoreCase(string, animStateTriggerXmlFile.animSet)) {
			this.setVariable("dbgForceAnim", false);
			this.restoreAnimatorStateToActionContext();
		} else {
			DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.setValue(animStateTriggerXmlFile.forceAnim);
			if (this.advancedAnimator.containsState(animStateTriggerXmlFile.stateName)) {
				this.setVariable("dbgForceAnim", animStateTriggerXmlFile.forceAnim);
				this.setVariable("dbgForceAnimStateName", animStateTriggerXmlFile.stateName);
				this.setVariable("dbgForceAnimNodeName", animStateTriggerXmlFile.nodeName);
				this.setVariable("dbgForceAnimScalars", animStateTriggerXmlFile.setScalarValues);
				this.setVariable("dbgForceScalar", animStateTriggerXmlFile.scalarValue);
				this.setVariable("dbgForceScalar2", animStateTriggerXmlFile.scalarValue2);
				this.advancedAnimator.SetState(animStateTriggerXmlFile.stateName);
			} else {
				DebugLog.Animation.error("State not found: " + animStateTriggerXmlFile.stateName);
				this.restoreAnimatorStateToActionContext();
			}
		}
	}

	private void restoreAnimatorStateToActionContext() {
		if (this.actionContext.getCurrentState() != null) {
			this.advancedAnimator.SetState(this.actionContext.getCurrentStateName(), PZArrayUtil.listConvert(this.actionContext.getChildStates(), (var0)->{
				return var0.name;
			}));
		}
	}

	public void clothingItemChanged(String string) {
		if (this.wornItems != null) {
			for (int int1 = 0; int1 < this.wornItems.size(); ++int1) {
				InventoryItem inventoryItem = this.wornItems.getItemByIndex(int1);
				ClothingItem clothingItem = inventoryItem.getClothingItem();
				if (clothingItem != null && clothingItem.isReady() && clothingItem.m_GUID.equals(string)) {
					ClothingItemReference clothingItemReference = new ClothingItemReference();
					clothingItemReference.itemGUID = string;
					clothingItemReference.randomize();
					inventoryItem.getVisual().synchWithOutfit(clothingItemReference);
					inventoryItem.synchWithVisual();
					this.resetModelNextFrame();
				}
			}
		}
	}

	public void reloadOutfit() {
		ModelManager.instance.Reset(this);
	}

	public boolean isSceneCulled() {
		return this.m_isCulled;
	}

	public void setSceneCulled(boolean boolean1) {
		if (this.isSceneCulled() != boolean1) {
			try {
				if (boolean1) {
					ModelManager.instance.Remove(this);
				} else {
					ModelManager.instance.Add(this);
				}
			} catch (Exception exception) {
				System.err.println("Error in IsoGameCharacter.setSceneCulled(" + boolean1 + "):");
				ExceptionLogger.logException(exception);
				ModelManager.instance.Remove(this);
				this.legsSprite.modelSlot = null;
			}
		}
	}

	public void onCullStateChanged(ModelManager modelManager, boolean boolean1) {
		this.m_isCulled = boolean1;
		if (!boolean1) {
			this.restoreAnimatorStateToActionContext();
			DebugFileWatcher.instance.add(this.m_animStateTriggerWatcher);
			OutfitManager.instance.addClothingItemListener(this);
		} else {
			DebugFileWatcher.instance.remove(this.m_animStateTriggerWatcher);
			OutfitManager.instance.removeClothingItemListener(this);
		}
	}

	public void dressInRandomOutfit() {
		if (DebugLog.isEnabled(DebugType.Clothing)) {
			DebugLog.Clothing.println("IsoGameCharacter.dressInRandomOutfit>");
		}

		Outfit outfit = OutfitManager.instance.GetRandomOutfit(this.isFemale());
		if (outfit != null) {
			this.dressInNamedOutfit(outfit.m_Name);
		}
	}

	public void dressInNamedOutfit(String string) {
	}

	public void dressInPersistentOutfit(String string) {
		int int1 = PersistentOutfits.instance.pickOutfit(string, this.isFemale());
		this.dressInPersistentOutfitID(int1);
	}

	public void dressInPersistentOutfitID(int int1) {
	}

	public String getOutfitName() {
		if (this instanceof IHumanVisual) {
			HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
			Outfit outfit = humanVisual.getOutfit();
			return outfit == null ? null : outfit.m_Name;
		} else {
			return null;
		}
	}

	public void dressInClothingItem(String string) {
	}

	public Outfit getRandomDefaultOutfit() {
		IsoGridSquare square = this.getCurrentSquare();
		IsoRoom room = square == null ? null : square.getRoom();
		String string = room == null ? null : room.getName();
		return ZombiesZoneDefinition.getRandomDefaultOutfit(this.isFemale(), string);
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

	public int getLevelUpLevels(int int1) {
		return LevelUpLevels.length <= int1 ? LevelUpLevels[LevelUpLevels.length - 1] : LevelUpLevels[int1];
	}

	public int getLevelMaxForXp() {
		return LevelUpLevels.length;
	}

	public int getXpForLevel(int int1) {
		return int1 < LevelUpLevels.length ? (int)((float)LevelUpLevels[int1] * this.LevelUpMultiplier) : (int)((float)(LevelUpLevels[LevelUpLevels.length - 1] + (int1 - LevelUpLevels.length + 1) * 400) * this.LevelUpMultiplier);
	}

	public void DoDeath(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		this.DoDeath(handWeapon, gameCharacter, true);
	}

	public void DoDeath(HandWeapon handWeapon, IsoGameCharacter gameCharacter, boolean boolean1) {
		this.OnDeath();
		if (this.getAttackedBy() instanceof IsoPlayer && GameServer.bServer && this instanceof IsoPlayer) {
			String string = "";
			String string2 = "";
			if (SteamUtils.isSteamModeEnabled()) {
				string = " (" + ((IsoPlayer)this.getAttackedBy()).getSteamID() + ") ";
				string2 = " (" + ((IsoPlayer)this).getSteamID() + ") ";
			}

			LoggerManager.getLogger("pvp").write("user " + ((IsoPlayer)this.getAttackedBy()).username + string + " killed " + ((IsoPlayer)this).username + string2 + " " + LoggerManager.getPlayerCoords((IsoPlayer)this), "IMPORTANT");
			if (ServerOptions.instance.AnnounceDeath.getValue()) {
				ChatServer.getInstance().sendMessageToServerChat(((IsoPlayer)this.getAttackedBy()).username + " killed " + ((IsoPlayer)this).username + ".");
			}

			ChatServer.getInstance().sendMessageToAdminChat("user " + ((IsoPlayer)this.getAttackedBy()).username + " killed " + ((IsoPlayer)this).username);
		} else {
			if (GameServer.bServer && this instanceof IsoPlayer) {
				ZLogger zLogger = LoggerManager.getLogger("user");
				String string3 = ((IsoPlayer)this).username;
				zLogger.write("user " + string3 + " died at " + LoggerManager.getPlayerCoords((IsoPlayer)this) + " (non pvp)");
			}

			if (ServerOptions.instance.AnnounceDeath.getValue() && this instanceof IsoPlayer && GameServer.bServer) {
				ChatServer.getInstance().sendMessageToServerChat(((IsoPlayer)this).username + " is dead.");
			}
		}

		if (this.isDead()) {
			float float1 = 0.5F;
			if (this.isZombie() && (((IsoZombie)this).bCrawling || this.getCurrentState() == ZombieOnGroundState.instance())) {
				float1 = 0.2F;
			}

			if (GameServer.bServer && boolean1) {
				boolean boolean2 = this.isOnFloor() && gameCharacter instanceof IsoPlayer && handWeapon != null && "BareHands".equals(handWeapon.getType());
				GameServer.sendBloodSplatter(handWeapon, this.getX(), this.getY(), this.getZ() + float1, this.getHitDir(), this.isCloseKilled(), boolean2);
			}

			int int1;
			int int2;
			if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1 && boolean1) {
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

			if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1 && boolean1) {
				this.splatBloodFloorBig();
			}

			if (gameCharacter != null && gameCharacter.xp != null) {
				gameCharacter.xp.AddXP(handWeapon, 3);
			}

			if (SandboxOptions.instance.BloodLevel.getValue() > 1 && this.isOnFloor() && gameCharacter instanceof IsoPlayer && handWeapon == ((IsoPlayer)gameCharacter).bareHands && boolean1) {
				this.playBloodSplatterSound();
				for (int2 = -1; int2 <= 1; ++int2) {
					for (int1 = -1; int1 <= 1; ++int1) {
						if (int2 != 0 || int1 != 0) {
							new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, (float)int2 * Rand.Next(0.25F, 0.5F), (float)int1 * Rand.Next(0.25F, 0.5F));
						}
					}
				}

				new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
			} else if (SandboxOptions.instance.BloodLevel.getValue() > 1 && boolean1) {
				this.playBloodSplatterSound();
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float1, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
				tempo.x = this.getHitDir().x;
				tempo.y = this.getHitDir().y;
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

				for (int int3 = 0; int3 < byte3; ++int3) {
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

		if (this.isDoDeathSound()) {
			this.playDeadSound();
		}

		this.setDoDeathSound(false);
	}

	private boolean TestIfSeen(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player != null && this != player && !GameServer.bServer) {
			float float1 = this.DistToProper(player);
			if (float1 > GameTime.getInstance().getViewDist()) {
				return false;
			} else {
				boolean boolean1 = this.current.isCanSee(int1);
				if (!boolean1 && this.current.isCouldSee(int1)) {
					boolean1 = float1 < player.getSeeNearbyCharacterDistance();
				}

				if (!boolean1) {
					return false;
				} else {
					ColorInfo colorInfo = this.getCurrentSquare().lighting[int1].lightInfo();
					float float2 = (colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F;
					if (float2 > 0.6F) {
						float2 = 1.0F;
					}

					float float3 = 1.0F - float1 / GameTime.getInstance().getViewDist();
					if (float2 == 1.0F && float3 > 0.3F) {
						float3 = 1.0F;
					}

					float float4 = player.getDotWithForwardDirection(this.getX(), this.getY());
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
					return float2 > 0.025F;
				}
			}
		} else {
			return false;
		}
	}

	private void DoLand() {
		if (!(this.fallTime < 20.0F) && !this.isClimbing()) {
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

			if (this.isZombie()) {
				if (this.fallTime > 50.0F) {
					this.hitDir.x = this.hitDir.y = 0.0F;
					if (!((IsoZombie)this).bCrawling && (Rand.Next(100) < 80 || this.fallTime > 80.0F)) {
						this.setVariable("bHardFall", true);
					}

					this.playHurtSound();
					float float1 = (float)Rand.Next(150) / 1000.0F;
					this.Health -= float1 * this.fallTime / 50.0F;
					this.setAttackedBy((IsoGameCharacter)null);
				}
			} else {
				boolean boolean1 = Rand.Next(80) == 0;
				float float2 = this.fallTime;
				float2 *= Math.min(1.8F, this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight());
				if (this.getCurrentSquare().getFloor() != null && this.getCurrentSquare().getFloor().getSprite().getName() != null && this.getCurrentSquare().getFloor().getSprite().getName().startsWith("blends_natural")) {
					float2 *= 0.8F;
					if (!boolean1) {
						boolean1 = Rand.Next(65) == 0;
					}
				}

				if (!boolean1) {
					if (this.Traits.Obese.isSet() || this.Traits.Emaciated.isSet()) {
						float2 *= 1.4F;
					}

					if (this.Traits.Overweight.isSet() || this.Traits.VeryUnderweight.isSet()) {
						float2 *= 1.2F;
					}

					float2 *= Math.max(0.1F, 1.0F - (float)this.getPerkLevel(PerkFactory.Perks.Fitness) * 0.1F);
					if (this.fallTime > 135.0F) {
						float2 = 1000.0F;
					}

					this.BodyDamage.ReduceGeneralHealth(float2);
					LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "FALLDOWN", float2);
					if (this.fallTime > 70.0F) {
						int int1 = 100 - (int)((double)this.fallTime * 0.6);
						if (this.getInventory().getMaxWeight() - this.getInventory().getCapacityWeight() < 2.0F) {
							int1 = (int)((float)int1 - this.getInventory().getCapacityWeight() / this.getInventory().getMaxWeight() * 100.0F / 5.0F);
						}

						if (this.Traits.Obese.isSet() || this.Traits.Emaciated.isSet()) {
							int1 -= 20;
						}

						if (this.Traits.Overweight.isSet() || this.Traits.VeryUnderweight.isSet()) {
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
							if (this.Traits.FastHealer.isSet()) {
								float3 = (float)Rand.Next(30, 50);
							} else if (this.Traits.SlowHealer.isSet()) {
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

	public ArrayList getLocalNeutralList() {
		return this.LocalNeutralList;
	}

	public ArrayList getLocalGroupList() {
		return this.LocalGroupList;
	}

	public ArrayList getLocalRelevantEnemyList() {
		return this.LocalRelevantEnemyList;
	}

	public float getDangerLevels() {
		return this.dangerLevels;
	}

	public void setDangerLevels(float float1) {
		this.dangerLevels = float1;
	}

	public ArrayList getPerkList() {
		return this.PerkList;
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

	@Deprecated
	public void setXp(IsoGameCharacter.XP xP) {
		this.xp = xP;
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

	public HashMap getLastKnownLocation() {
		return this.LastKnownLocation;
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

	public boolean hasTimedActions() {
		return !this.CharacterActions.isEmpty() || this.getVariableBoolean("IsPerformingAnAction");
	}

	public Vector2 getForwardDirection() {
		return this.m_forwardDirection;
	}

	public void setForwardDirection(Vector2 vector2) {
		if (vector2 != null) {
			this.setForwardDirection(vector2.x, vector2.y);
		}
	}

	public void setForwardDirection(float float1, float float2) {
		this.m_forwardDirection.x = float1;
		this.m_forwardDirection.y = float2;
	}

	public void zeroForwardDirectionX() {
		this.setForwardDirection(0.0F, 1.0F);
	}

	public void zeroForwardDirectionY() {
		this.setForwardDirection(1.0F, 0.0F);
	}

	public float getDirectionAngle() {
		return 57.295776F * this.getForwardDirection().getDirection();
	}

	public void setDirectionAngle(float float1) {
		float float2 = 0.017453292F * float1;
		Vector2 vector2 = this.getForwardDirection();
		vector2.setDirection(float2);
	}

	public float getAnimAngle() {
		return this.m_animPlayer != null && this.m_animPlayer.isReady() && !this.m_animPlayer.isBoneTransformsNeedFirstFrame() ? 57.295776F * this.m_animPlayer.getAngle() : this.getDirectionAngle();
	}

	public float getAnimAngleRadians() {
		return this.m_animPlayer != null && this.m_animPlayer.isReady() && !this.m_animPlayer.isBoneTransformsNeedFirstFrame() ? this.m_animPlayer.getAngle() : this.m_forwardDirection.getDirection();
	}

	public Vector2 getAnimVector(Vector2 vector2) {
		return vector2.setLengthAndDirection(this.getAnimAngleRadians(), 1.0F);
	}

	public float getLookAngleRadians() {
		return this.m_animPlayer != null && this.m_animPlayer.isReady() ? this.m_animPlayer.getAngle() + this.m_animPlayer.getTwistAngle() : this.getForwardDirection().getDirection();
	}

	public Vector2 getLookVector(Vector2 vector2) {
		return vector2.setLengthAndDirection(this.getLookAngleRadians(), 1.0F);
	}

	public float getDotWithForwardDirection(Vector3 vector3) {
		return this.getDotWithForwardDirection(vector3.x, vector3.y);
	}

	public float getDotWithForwardDirection(float float1, float float2) {
		Vector2 vector2 = IsoGameCharacter.L_getDotWithForwardDirection.v1.set(float1 - this.getX(), float2 - this.getY());
		vector2.normalize();
		Vector2 vector22 = this.getLookVector(IsoGameCharacter.L_getDotWithForwardDirection.v2);
		vector22.normalize();
		return vector2.dot(vector22);
	}

	public boolean isAsleep() {
		return this.Asleep;
	}

	public void setAsleep(boolean boolean1) {
		this.Asleep = boolean1;
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

	public BodyDamage getBodyDamageRemote() {
		if (this.BodyDamageRemote == null) {
			this.BodyDamageRemote = new BodyDamage((IsoGameCharacter)null);
		}

		return this.BodyDamageRemote;
	}

	public void resetBodyDamageRemote() {
		this.BodyDamageRemote = null;
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

	public BaseVisual getVisual() {
		throw new RuntimeException("subclasses must implement this");
	}

	public ItemVisuals getItemVisuals() {
		throw new RuntimeException("subclasses must implement this");
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		this.getWornItems().getItemVisuals(itemVisuals);
	}

	public boolean isUsingWornItems() {
		return this.wornItems != null;
	}

	public Stack getFamiliarBuildings() {
		return this.FamiliarBuildings;
	}

	public AStarPathFinderResult getFinder() {
		return this.finder;
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

	public float getHealth() {
		return this.Health;
	}

	public void setHealth(float float1) {
		this.Health = float1;
	}

	public boolean isOnDeathDone() {
		return this.bDead;
	}

	public void setOnDeathDone(boolean boolean1) {
		this.bDead = boolean1;
	}

	public boolean isOnKillDone() {
		return this.bKill;
	}

	public void setOnKillDone(boolean boolean1) {
		this.bKill = boolean1;
	}

	public boolean isDeathDragDown() {
		return this.bDeathDragDown;
	}

	public void setDeathDragDown(boolean boolean1) {
		this.bDeathDragDown = boolean1;
	}

	public boolean isPlayingDeathSound() {
		return this.bPlayingDeathSound;
	}

	public void setPlayingDeathSound(boolean boolean1) {
		this.bPlayingDeathSound = boolean1;
	}

	public String getHurtSound() {
		return this.hurtSound;
	}

	public void setHurtSound(String string) {
		this.hurtSound = string;
	}

	@Deprecated
	public boolean isIgnoreMovementForDirection() {
		return false;
	}

	public ItemContainer getInventory() {
		return this.inventory;
	}

	public void setInventory(ItemContainer itemContainer) {
		itemContainer.parent = this;
		this.inventory = itemContainer;
		this.inventory.setExplored(true);
	}

	public boolean isPrimaryEquipped(String string) {
		if (this.leftHandItem == null) {
			return false;
		} else {
			return this.leftHandItem.getFullType().equals(string) || this.leftHandItem.getType().equals(string);
		}
	}

	public InventoryItem getPrimaryHandItem() {
		return this.leftHandItem;
	}

	public void setPrimaryHandItem(InventoryItem inventoryItem) {
		this.setEquipParent(this.leftHandItem, inventoryItem);
		this.leftHandItem = inventoryItem;
		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.equip((IsoPlayer)this, 0);
		}

		LuaEventManager.triggerEvent("OnEquipPrimary", this, inventoryItem);
		this.resetEquippedHandsModels();
		this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
		if (inventoryItem != null && inventoryItem instanceof HandWeapon && !StringUtils.isNullOrEmpty(((HandWeapon)inventoryItem).getFireMode())) {
			this.setVariable("FireMode", ((HandWeapon)inventoryItem).getFireMode());
		} else {
			this.clearVariable("FireMode");
		}
	}

	protected void setEquipParent(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
		if (inventoryItem != null) {
			inventoryItem.setEquipParent((IsoGameCharacter)null);
		}

		if (inventoryItem2 != null) {
			inventoryItem2.setEquipParent(this);
		}
	}

	public void initWornItems(String string) {
		BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup(string);
		this.wornItems = new WornItems(bodyLocationGroup);
	}

	public WornItems getWornItems() {
		return this.wornItems;
	}

	public void setWornItems(WornItems wornItems) {
		this.wornItems = new WornItems(wornItems);
	}

	public InventoryItem getWornItem(String string) {
		return this.wornItems.getItem(string);
	}

	public void setWornItem(String string, InventoryItem inventoryItem) {
		this.setWornItem(string, inventoryItem, true);
	}

	public void setWornItem(String string, InventoryItem inventoryItem, boolean boolean1) {
		InventoryItem inventoryItem2 = this.wornItems.getItem(string);
		if (inventoryItem != inventoryItem2) {
			IsoCell cell = IsoWorld.instance.CurrentCell;
			if (inventoryItem2 != null && cell != null) {
				cell.addToProcessItemsRemove(inventoryItem2);
			}

			this.wornItems.setItem(string, inventoryItem);
			if (inventoryItem != null && cell != null) {
				if (inventoryItem.getContainer() != null) {
					inventoryItem.getContainer().parent = this;
				}

				cell.addToProcessItems(inventoryItem);
			}

			if (boolean1 && inventoryItem2 != null && this instanceof IsoPlayer && !this.getInventory().hasRoomFor(this, inventoryItem2)) {
				IsoGridSquare square = this.getCurrentSquare();
				square = this.getSolidFloorAt(square.x, square.y, square.z);
				if (square != null) {
					float float1 = Rand.Next(0.1F, 0.9F);
					float float2 = Rand.Next(0.1F, 0.9F);
					float float3 = square.getApparentZ(float1, float2) - (float)square.getZ();
					square.AddWorldInventoryItem(inventoryItem2, float1, float2, float3);
					this.getInventory().Remove(inventoryItem2);
				}
			}

			this.resetModelNextFrame();
			if (this.clothingWetness != null) {
				this.clothingWetness.changed = true;
			}

			if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
				GameClient.instance.sendClothing((IsoPlayer)this, string, inventoryItem);
			}

			this.onWornItemsChanged();
		}
	}

	public void removeWornItem(InventoryItem inventoryItem) {
		this.removeWornItem(inventoryItem, true);
	}

	public void removeWornItem(InventoryItem inventoryItem, boolean boolean1) {
		String string = this.wornItems.getLocation(inventoryItem);
		if (string != null) {
			this.setWornItem(string, (InventoryItem)null, boolean1);
		}
	}

	public void clearWornItems() {
		if (this.wornItems != null) {
			this.wornItems.clear();
			if (this.clothingWetness != null) {
				this.clothingWetness.changed = true;
			}

			this.onWornItemsChanged();
		}
	}

	public BodyLocationGroup getBodyLocationGroup() {
		return this.wornItems == null ? null : this.wornItems.getBodyLocationGroup();
	}

	public void onWornItemsChanged() {
	}

	public void initAttachedItems(String string) {
		AttachedLocationGroup attachedLocationGroup = AttachedLocations.getGroup(string);
		this.attachedItems = new AttachedItems(attachedLocationGroup);
	}

	public AttachedItems getAttachedItems() {
		return this.attachedItems;
	}

	public void setAttachedItems(AttachedItems attachedItems) {
		this.attachedItems = new AttachedItems(attachedItems);
	}

	public InventoryItem getAttachedItem(String string) {
		return this.attachedItems.getItem(string);
	}

	public void setAttachedItem(String string, InventoryItem inventoryItem) {
		InventoryItem inventoryItem2 = this.attachedItems.getItem(string);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		if (inventoryItem2 != null && cell != null) {
			cell.addToProcessItemsRemove(inventoryItem2);
		}

		this.attachedItems.setItem(string, inventoryItem);
		if (inventoryItem != null && cell != null) {
			InventoryContainer inventoryContainer = (InventoryContainer)Type.tryCastTo(inventoryItem, InventoryContainer.class);
			if (inventoryContainer != null && inventoryContainer.getInventory() != null) {
				inventoryContainer.getInventory().parent = this;
			}

			cell.addToProcessItems(inventoryItem);
		}

		this.resetEquippedHandsModels();
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
		if (GameClient.bClient && player != null && player.isLocalPlayer()) {
			GameClient.instance.sendAttachedItem(player, string, inventoryItem);
		}

		if (!GameServer.bServer && player != null && player.isLocalPlayer()) {
			LuaEventManager.triggerEvent("OnClothingUpdated", this);
		}
	}

	public void removeAttachedItem(InventoryItem inventoryItem) {
		String string = this.attachedItems.getLocation(inventoryItem);
		if (string != null) {
			this.setAttachedItem(string, (InventoryItem)null);
		}
	}

	public void clearAttachedItems() {
		if (this.attachedItems != null) {
			this.attachedItems.clear();
		}
	}

	public AttachedLocationGroup getAttachedLocationGroup() {
		return this.attachedItems == null ? null : this.attachedItems.getGroup();
	}

	public ClothingWetness getClothingWetness() {
		return this.clothingWetness;
	}

	public InventoryItem getClothingItem_Head() {
		return this.getWornItem("Hat");
	}

	public void setClothingItem_Head(InventoryItem inventoryItem) {
		this.setWornItem("Hat", inventoryItem);
	}

	public InventoryItem getClothingItem_Torso() {
		return this.getWornItem("Tshirt");
	}

	public void setClothingItem_Torso(InventoryItem inventoryItem) {
		this.setWornItem("Tshirt", inventoryItem);
	}

	public InventoryItem getClothingItem_Back() {
		return this.getWornItem("Back");
	}

	public void setClothingItem_Back(InventoryItem inventoryItem) {
		this.setWornItem("Back", inventoryItem);
	}

	public InventoryItem getClothingItem_Hands() {
		return this.getWornItem("Hands");
	}

	public void setClothingItem_Hands(InventoryItem inventoryItem) {
		this.setWornItem("Hands", inventoryItem);
	}

	public InventoryItem getClothingItem_Legs() {
		return this.getWornItem("Pants");
	}

	public void setClothingItem_Legs(InventoryItem inventoryItem) {
		this.setWornItem("Pants", inventoryItem);
	}

	public InventoryItem getClothingItem_Feet() {
		return this.getWornItem("Shoes");
	}

	public void setClothingItem_Feet(InventoryItem inventoryItem) {
		this.setWornItem("Shoes", inventoryItem);
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
		if (GameServer.bServer) {
			if (boolean1) {
				IsoFireManager.addCharacterOnFire(this);
			} else {
				IsoFireManager.deleteCharacterOnFire(this);
			}
		}
	}

	public void removeFromWorld() {
		if (GameServer.bServer) {
			IsoFireManager.deleteCharacterOnFire(this);
		}

		super.removeFromWorld();
	}

	public int getPathIndex() {
		return this.pathIndex;
	}

	public void setPathIndex(int int1) {
		this.pathIndex = int1;
	}

	public int getPathTargetX() {
		return (int)this.getPathFindBehavior2().getTargetX();
	}

	public int getPathTargetY() {
		return (int)this.getPathFindBehavior2().getTargetY();
	}

	public int getPathTargetZ() {
		return (int)this.getPathFindBehavior2().getTargetZ();
	}

	public InventoryItem getSecondaryHandItem() {
		return this.rightHandItem;
	}

	public void setSecondaryHandItem(InventoryItem inventoryItem) {
		this.setEquipParent(this.rightHandItem, inventoryItem);
		this.rightHandItem = inventoryItem;
		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			GameClient.instance.equip((IsoPlayer)this, 1);
		}

		LuaEventManager.triggerEvent("OnEquipSecondary", this, inventoryItem);
		this.resetEquippedHandsModels();
		this.setVariable("Weapon", WeaponType.getWeaponType(this).type);
	}

	public boolean isHandItem(InventoryItem inventoryItem) {
		return this.isPrimaryHandItem(inventoryItem) || this.isSecondaryHandItem(inventoryItem);
	}

	public boolean isPrimaryHandItem(InventoryItem inventoryItem) {
		return inventoryItem != null && this.getPrimaryHandItem() == inventoryItem;
	}

	public boolean isSecondaryHandItem(InventoryItem inventoryItem) {
		return inventoryItem != null && this.getSecondaryHandItem() == inventoryItem;
	}

	public boolean isItemInBothHands(InventoryItem inventoryItem) {
		return this.isPrimaryHandItem(inventoryItem) && this.isSecondaryHandItem(inventoryItem);
	}

	public boolean removeFromHands(InventoryItem inventoryItem) {
		boolean boolean1 = true;
		if (this.isPrimaryHandItem(inventoryItem)) {
			this.setPrimaryHandItem((InventoryItem)null);
		}

		if (this.isSecondaryHandItem(inventoryItem)) {
			this.setSecondaryHandItem((InventoryItem)null);
		}

		return boolean1;
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

	public Moodles getMoodles() {
		return this.Moodles;
	}

	public Stats getStats() {
		return this.stats;
	}

	public Stack getUsedItemsOn() {
		return this.UsedItemsOn;
	}

	public HandWeapon getUseHandWeapon() {
		return this.useHandWeapon;
	}

	public void setUseHandWeapon(HandWeapon handWeapon) {
		this.useHandWeapon = handWeapon;
	}

	public IsoSprite getLegsSprite() {
		return this.legsSprite;
	}

	public void setLegsSprite(IsoSprite sprite) {
		this.legsSprite = sprite;
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
		this.moveForwardVec.set(vector2);
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

	public Stack getEnemyList() {
		return this.EnemyList;
	}

	public TraitCollection getTraits() {
		return this.getCharacterTraits();
	}

	public IsoGameCharacter.CharacterTraits getCharacterTraits() {
		return this.Traits;
	}

	public int getMaxWeight() {
		return this.maxWeight;
	}

	public void setMaxWeight(int int1) {
		this.maxWeight = int1;
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

	public float getFallTime() {
		return this.fallTime;
	}

	public void setFallTime(float float1) {
		this.fallTime = float1;
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

	public void OnAnimEvent(AnimLayer animLayer, AnimEvent animEvent) {
		if (animEvent.m_EventName != null) {
			if (animEvent.m_EventName.equalsIgnoreCase("SetVariable") && animEvent.m_SetVariable1 != null) {
				this.setVariable(animEvent.m_SetVariable1, animEvent.m_SetVariable2);
			}

			if (animEvent.m_EventName.equalsIgnoreCase("ClearVariable")) {
				this.clearVariable(animEvent.m_ParameterValue);
			}

			if (animEvent.m_EventName.equalsIgnoreCase("PlaySound")) {
				this.getEmitter().playSoundImpl(animEvent.m_ParameterValue, this);
			}

			if (animEvent.m_EventName.equalsIgnoreCase("Footstep")) {
				this.DoFootstepSound(animEvent.m_ParameterValue);
			}

			if (animEvent.m_EventName.equalsIgnoreCase("DamageWhileInTrees")) {
				this.damageWhileInTrees();
			}

			int int1 = animLayer.getDepth();
			this.actionContext.reportEvent(int1, animEvent.m_EventName);
			this.stateMachine.stateAnimEvent(int1, animEvent);
		}
	}

	private void damageWhileInTrees() {
		if (!this.isZombie() && !"Tutorial".equals(Core.GameMode)) {
			int int1 = 50;
			int int2 = Rand.Next(0, BodyPartType.ToIndex(BodyPartType.MAX));
			if (this.isRunning()) {
				int1 = 30;
			}

			if (this.Traits.Outdoorsman.isSet()) {
				int1 += 50;
			}

			int1 += (int)this.getBodyPartClothingDefense(int2, false, false);
			if (Rand.NextBool(int1)) {
				this.addHole(BloodBodyPartType.FromIndex(int2));
				int1 = 6;
				if (this.Traits.ThickSkinned.isSet()) {
					int1 += 7;
				}

				if (this.Traits.ThinSkinned.isSet()) {
					int1 -= 3;
				}

				if (Rand.NextBool(int1) && (int)this.getBodyPartClothingDefense(int2, false, false) < 100) {
					BodyPart bodyPart = (BodyPart)this.getBodyDamage().getBodyParts().get(int2);
					if (Rand.NextBool(int1 + 10)) {
						bodyPart.setCut(true, true);
					} else {
						bodyPart.setScratched(true, true);
					}
				}
			}
		}
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
		float float1 = 0.95F;
		if (int1 == 1) {
			float1 = 0.9F;
		}

		if (int1 == 2) {
			float1 = 0.8F;
		}

		if (int1 == 3) {
			float1 = 0.75F;
		}

		if (int1 == 4) {
			float1 = 0.7F;
		}

		if (int1 == 5) {
			float1 = 0.65F;
		}

		if (int1 == 6) {
			float1 = 0.6F;
		}

		if (int1 == 7) {
			float1 = 0.55F;
		}

		if (int1 == 8) {
			float1 = 0.5F;
		}

		if (int1 == 9) {
			float1 = 0.45F;
		}

		if (int1 == 10) {
			float1 = 0.4F;
		}

		float1 *= 1.2F;
		return float1;
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
			return 0.8F;
		} else if (int1 == 2) {
			return 0.75F;
		} else if (int1 == 3) {
			return 0.7F;
		} else if (int1 == 4) {
			return 0.65F;
		} else if (int1 == 5) {
			return 0.6F;
		} else if (int1 == 6) {
			return 0.57F;
		} else if (int1 == 7) {
			return 0.53F;
		} else if (int1 == 8) {
			return 0.49F;
		} else if (int1 == 9) {
			return 0.46F;
		} else {
			return int1 == 10 ? 0.43F : 0.9F;
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

		if (this.Traits.Obese.isSet()) {
			float1 = (float)((double)float1 * 0.4);
		}

		if (this.Traits.Overweight.isSet()) {
			float1 = (float)((double)float1 * 0.7);
		}

		if (this.Traits.VeryUnderweight.isSet()) {
			float1 = (float)((double)float1 * 0.7);
		}

		if (this.Traits.Emaciated.isSet()) {
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

	public int getPerkLevel(PerkFactory.Perk perk) {
		IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
		return perkInfo != null ? perkInfo.level : 0;
	}

	public void setPerkLevelDebug(PerkFactory.Perk perk, int int1) {
		IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
		if (perkInfo != null) {
			perkInfo.level = int1;
		}

		if (GameClient.bClient && this instanceof IsoPlayer) {
			GameClient.sendPerks((IsoPlayer)this);
		}
	}

	public void LoseLevel(PerkFactory.Perk perk) {
		IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
		if (perkInfo != null) {
			--perkInfo.level;
			if (perkInfo.level < 0) {
				perkInfo.level = 0;
			}

			LuaEventManager.triggerEvent("LevelPerk", this, perk, perkInfo.level, false);
			if (perk == PerkFactory.Perks.Sneak && GameClient.bClient && this instanceof IsoPlayer) {
				GameClient.sendPerks((IsoPlayer)this);
			}
		} else {
			LuaEventManager.triggerEvent("LevelPerk", this, perk, 0, false);
		}
	}

	public void LevelPerk(PerkFactory.Perk perk, boolean boolean1) {
		Objects.requireNonNull(perk, "perk is null");
		if (perk == PerkFactory.Perks.MAX) {
			throw new IllegalArgumentException("perk == Perks.MAX");
		} else {
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
			IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
			if (perkInfo != null) {
				++perkInfo.level;
				if (player != null && !"Tutorial".equals(Core.GameMode) && this.getHoursSurvived() > 0.016666666666666666) {
					HaloTextHelper.addTextWithArrow(player, "+1 " + perk.getName(), true, HaloTextHelper.getColorGreen());
				}

				if (perkInfo.level > 10) {
					perkInfo.level = 10;
				}

				if (GameClient.bClient && player != null) {
					GameClient.instance.sendSyncXp(player);
				}

				LuaEventManager.triggerEventGarbage("LevelPerk", this, perk, perkInfo.level, true);
				if (GameClient.bClient && player != null) {
					GameClient.sendPerks(player);
				}
			} else {
				perkInfo = new IsoGameCharacter.PerkInfo();
				perkInfo.perk = perk;
				perkInfo.level = 1;
				this.PerkList.add(perkInfo);
				if (player != null && !"Tutorial".equals(Core.GameMode) && this.getHoursSurvived() > 0.016666666666666666) {
					HaloTextHelper.addTextWithArrow(player, "+1 " + perk.getName(), true, HaloTextHelper.getColorGreen());
				}

				if (GameClient.bClient && this instanceof IsoPlayer) {
					GameClient.instance.sendSyncXp(player);
				}

				LuaEventManager.triggerEvent("LevelPerk", this, perk, perkInfo.level, true);
			}
		}
	}

	public void LevelPerk(PerkFactory.Perk perk) {
		this.LevelPerk(perk, true);
	}

	public void level0(PerkFactory.Perk perk) {
		IsoGameCharacter.PerkInfo perkInfo = this.getPerkInfo(perk);
		if (perkInfo != null) {
			perkInfo.level = 0;
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
					this.getKnownRecipes().add((String)literature.getTeachedRecipes().get(int1));
				}
			}
		}

		literature.Use();
	}

	public void OnDeath() {
		LuaEventManager.triggerEvent("OnCharacterDeath", this);
	}

	public void splatBloodFloorBig() {
		if (this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
			this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, this.z, Rand.Next(20));
		}
	}

	public void splatBloodFloor() {
		if (this.getCurrentSquare() != null) {
			if (this.getCurrentSquare().getChunk() != null) {
				if (this.isDead() && Rand.Next(10) == 0) {
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

	public boolean isDead() {
		return this.Health <= 0.0F || this.BodyDamage != null && this.BodyDamage.getHealth() <= 0.0F;
	}

	public boolean isAlive() {
		return !this.isDead();
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

	public boolean hasEquippedTag(String string) {
		if (this.leftHandItem != null && this.leftHandItem.hasTag(string)) {
			return true;
		} else {
			return this.rightHandItem != null && this.rightHandItem.hasTag(string);
		}
	}

	public void setDir(IsoDirections directions) {
		this.dir = directions;
		this.getVectorFromDirection(this.m_forwardDirection);
	}

	public void Callout(boolean boolean1) {
		if (this.isCanShout()) {
			this.Callout();
			if (boolean1) {
				this.playEmote("shout");
			}
		}
	}

	public void Callout() {
		String string = "";
		byte byte1 = 30;
		if (Core.getInstance().getGameMode().equals("Tutorial")) {
			string = Translator.getText("IGUI_PlayerText_CalloutTutorial");
		} else if (this.isSneaking()) {
			byte1 = 6;
			switch (Rand.Next(3)) {
			case 0: 
				string = Translator.getText("IGUI_PlayerText_Callout1Sneak");
				break;
			
			case 1: 
				string = Translator.getText("IGUI_PlayerText_Callout2Sneak");
				break;
			
			case 2: 
				string = Translator.getText("IGUI_PlayerText_Callout3Sneak");
			
			}
		} else {
			switch (Rand.Next(3)) {
			case 0: 
				string = Translator.getText("IGUI_PlayerText_Callout1New");
				break;
			
			case 1: 
				string = Translator.getText("IGUI_PlayerText_Callout2New");
				break;
			
			case 2: 
				string = Translator.getText("IGUI_PlayerText_Callout3New");
			
			}
		}

		WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, (int)this.z, byte1, byte1);
		this.SayShout(string);
		this.callOut = true;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.getVectorFromDirection(this.m_forwardDirection);
		if (byteBuffer.get() == 1) {
			this.descriptor = new SurvivorDesc(true);
			this.descriptor.load(byteBuffer, int1, this);
			this.bFemale = this.descriptor.isFemale();
		}

		this.getVisual().load(byteBuffer, int1);
		ArrayList arrayList = this.inventory.load(byteBuffer, int1);
		this.savedInventoryItems.clear();
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			this.savedInventoryItems.add((InventoryItem)arrayList.get(int2));
		}

		this.Asleep = byteBuffer.get() == 1;
		this.ForceWakeUpTime = byteBuffer.getFloat();
		int int3;
		if (!this.isZombie()) {
			this.stats.load(byteBuffer, int1);
			this.BodyDamage.load(byteBuffer, int1);
			this.xp.load(byteBuffer, int1);
			ArrayList arrayList2 = this.inventory.IncludingObsoleteItems;
			int3 = byteBuffer.getInt();
			if (int3 >= 0 && int3 < arrayList2.size()) {
				this.leftHandItem = (InventoryItem)arrayList2.get(int3);
			}

			int3 = byteBuffer.getInt();
			if (int3 >= 0 && int3 < arrayList2.size()) {
				this.rightHandItem = (InventoryItem)arrayList2.get(int3);
			}

			this.setEquipParent((InventoryItem)null, this.leftHandItem);
			this.setEquipParent((InventoryItem)null, this.rightHandItem);
		}

		boolean boolean2 = byteBuffer.get() == 1;
		if (boolean2) {
			this.SetOnFire();
		}

		this.DepressEffect = byteBuffer.getFloat();
		this.DepressFirstTakeTime = byteBuffer.getFloat();
		this.BetaEffect = byteBuffer.getFloat();
		this.BetaDelta = byteBuffer.getFloat();
		this.PainEffect = byteBuffer.getFloat();
		this.PainDelta = byteBuffer.getFloat();
		this.SleepingTabletEffect = byteBuffer.getFloat();
		this.SleepingTabletDelta = byteBuffer.getFloat();
		int3 = byteBuffer.getInt();
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			IsoGameCharacter.ReadBook readBook = new IsoGameCharacter.ReadBook();
			readBook.fullType = GameWindow.ReadString(byteBuffer);
			readBook.alreadyReadPages = byteBuffer.getInt();
			this.ReadBooks.add(readBook);
		}

		this.reduceInfectionPower = byteBuffer.getFloat();
		int4 = byteBuffer.getInt();
		for (int int5 = 0; int5 < int4; ++int5) {
			this.knownRecipes.add(GameWindow.ReadString(byteBuffer));
		}

		this.lastHourSleeped = byteBuffer.getInt();
		this.timeSinceLastSmoke = byteBuffer.getFloat();
		this.beardGrowTiming = byteBuffer.getFloat();
		this.hairGrowTiming = byteBuffer.getFloat();
		this.setUnlimitedCarry(byteBuffer.get() == 1);
		this.setBuildCheat(byteBuffer.get() == 1);
		this.setHealthCheat(byteBuffer.get() == 1);
		this.setMechanicsCheat(byteBuffer.get() == 1);
		if (int1 >= 176) {
			this.setMovablesCheat(byteBuffer.get() == 1);
			this.setFarmingCheat(byteBuffer.get() == 1);
			this.setTimedActionInstantCheat(byteBuffer.get() == 1);
			this.setUnlimitedEndurance(byteBuffer.get() == 1);
		}

		if (int1 >= 161) {
			this.setSneaking(byteBuffer.get() == 1);
			this.setDeathDragDown(byteBuffer.get() == 1);
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		if (this.descriptor == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.descriptor.save(byteBuffer);
		}

		this.getVisual().save(byteBuffer);
		ArrayList arrayList = this.inventory.save(byteBuffer, this);
		this.savedInventoryItems.clear();
		int int1;
		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			this.savedInventoryItems.add((InventoryItem)arrayList.get(int1));
		}

		byteBuffer.put((byte)(this.Asleep ? 1 : 0));
		byteBuffer.putFloat(this.ForceWakeUpTime);
		if (!this.isZombie()) {
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
		for (int1 = 0; int1 < this.ReadBooks.size(); ++int1) {
			IsoGameCharacter.ReadBook readBook = (IsoGameCharacter.ReadBook)this.ReadBooks.get(int1);
			GameWindow.WriteString(byteBuffer, readBook.fullType);
			byteBuffer.putInt(readBook.alreadyReadPages);
		}

		byteBuffer.putFloat(this.reduceInfectionPower);
		byteBuffer.putInt(this.knownRecipes.size());
		for (int1 = 0; int1 < this.knownRecipes.size(); ++int1) {
			String string = (String)this.knownRecipes.get(int1);
			GameWindow.WriteString(byteBuffer, string);
		}

		byteBuffer.putInt(this.lastHourSleeped);
		byteBuffer.putFloat(this.timeSinceLastSmoke);
		byteBuffer.putFloat(this.beardGrowTiming);
		byteBuffer.putFloat(this.hairGrowTiming);
		byteBuffer.put((byte)(this.isUnlimitedCarry() ? 1 : 0));
		byteBuffer.put((byte)(this.isBuildCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isHealthCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isMechanicsCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isMovablesCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isFarmingCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isTimedActionInstantCheat() ? 1 : 0));
		byteBuffer.put((byte)(this.isUnlimitedEndurance() ? 1 : 0));
		byteBuffer.put((byte)(this.isSneaking() ? 1 : 0));
		byteBuffer.put((byte)(this.isDeathDragDown() ? 1 : 0));
	}

	public ChatElement getChatElement() {
		return this.chatElement;
	}

	public void StartAction(BaseAction baseAction) {
		this.CharacterActions.clear();
		this.CharacterActions.push(baseAction);
		if (baseAction.valid()) {
			baseAction.waitToStart();
		}
	}

	public void QueueAction(BaseAction baseAction) {
	}

	public void StopAllActionQueue() {
		if (!this.CharacterActions.isEmpty()) {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			if (baseAction.bStarted) {
				baseAction.stop();
			}

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
				if (baseAction.bStarted) {
					baseAction.stop();
				}

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
				if (baseAction.bStarted) {
					baseAction.stop();
				}

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
				if (baseAction.bStarted) {
					baseAction.stop();
				}

				this.CharacterActions.clear();
				if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
					UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
				}
			}
		}
	}

	public String GetAnimSetName() {
		return "Base";
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
		this.PainEffect = 5400.0F;
		this.PainDelta += float1;
	}

	public void initSpritePartsEmpty() {
		this.InitSpriteParts(this.descriptor);
	}

	public void InitSpriteParts(SurvivorDesc survivorDesc) {
		this.sprite.AnimMap.clear();
		this.sprite.AnimStack.clear();
		this.sprite.CurrentAnim = null;
		this.legsSprite = this.sprite;
		this.legsSprite.name = survivorDesc.torso;
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
		if (!this.isZombie()) {
			if (this.wornItems != null) {
				ItemVisuals itemVisuals = new ItemVisuals();
				survivorDesc.getItemVisuals(itemVisuals);
				this.wornItems.setFromItemVisuals(itemVisuals);
				this.wornItems.addItemsToItemContainer(this.inventory);
				survivorDesc.wornItems.clear();
				this.onWornItemsChanged();
			}
		}
	}

	public void PlayAnim(String string) {
	}

	public void PlayAnimWithSpeed(String string, float float1) {
	}

	public void PlayAnimUnlooped(String string) {
	}

	public void DirectionFromVector(Vector2 vector2) {
		this.dir = IsoDirections.fromAngle(vector2);
	}

	public void DoFootstepSound(String string) {
		float float1 = 1.0F;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -940878112: 
			if (string.equals("sneak_run")) {
				byte1 = 1;
			}

			break;
		
		case -895679974: 
			if (string.equals("sprint")) {
				byte1 = 5;
			}

			break;
		
		case -891993841: 
			if (string.equals("strafe")) {
				byte1 = 2;
			}

			break;
		
		case 113291: 
			if (string.equals("run")) {
				byte1 = 4;
			}

			break;
		
		case 3641801: 
			if (string.equals("walk")) {
				byte1 = 3;
			}

			break;
		
		case 897679380: 
			if (string.equals("sneak_walk")) {
				byte1 = 0;
			}

		
		}
		switch (byte1) {
		case 0: 
			float1 = 0.2F;
			break;
		
		case 1: 
			float1 = 0.5F;
			break;
		
		case 2: 
			float1 = this.bSneaking ? 0.2F : 0.3F;
			break;
		
		case 3: 
			float1 = 0.5F;
			break;
		
		case 4: 
			float1 = 1.3F;
			break;
		
		case 5: 
			float1 = 1.8F;
		
		}
		this.DoFootstepSound(float1);
	}

	public void DoFootstepSound(float float1) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
		if (GameClient.bClient && player != null && player.networkAI != null) {
			player.networkAI.footstepSoundRadius = 0;
		}

		if (player == null || !player.isGhostMode() || DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
			if (this.getCurrentSquare() != null) {
				if (!(float1 <= 0.0F)) {
					float float2 = float1;
					float1 *= 1.4F;
					if (this.Traits.Graceful.isSet()) {
						float1 *= 0.6F;
					}

					if (this.Traits.Clumsy.isSet()) {
						float1 *= 1.2F;
					}

					if (this.getWornItem("Shoes") == null) {
						float1 *= 0.5F;
					}

					float1 *= this.getLightfootMod();
					float1 *= 2.0F - this.getNimbleMod();
					if (this.bSneaking) {
						float1 *= this.getSneakSpotMod();
					}

					if (float1 > 0.0F) {
						this.emitter.playFootsteps("HumanFootstepsCombined", float2);
						if (player != null && player.isGhostMode()) {
							return;
						}

						int int1 = (int)Math.ceil((double)(float1 * 10.0F));
						if (this.bSneaking) {
							int1 = Math.max(1, int1);
						}

						if (this.getCurrentSquare().getRoom() != null) {
							int1 = (int)((float)int1 * 0.5F);
						}

						int int2 = 2;
						if (this.bSneaking) {
							int2 = Math.min(12, 4 + this.getPerkLevel(PerkFactory.Perks.Lightfoot));
						}

						if (GameClient.bClient && player != null && player.networkAI != null) {
							player.networkAI.footstepSoundRadius = (byte)int1;
						}

						if (Rand.Next(int2) == 0) {
							WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int1, int1, false, 0.0F, 1.0F, false, false, false);
						}
					}
				}
			}
		}
	}

	public boolean Eat(InventoryItem inventoryItem, float float1) {
		Food food = (Food)Type.tryCastTo(inventoryItem, Food.class);
		if (food == null) {
			return false;
		} else {
			float1 = PZMath.clamp(float1, 0.0F, 1.0F);
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

			float float2 = float1;
			float float3;
			if (food.getBaseHunger() != 0.0F && food.getHungChange() != 0.0F) {
				float float4 = food.getBaseHunger() * float1;
				float3 = float4 / food.getHungChange();
				float3 = PZMath.clamp(float3, 0.0F, 1.0F);
				float1 = float3;
			}

			if (food.getHungChange() < 0.0F && food.getHungChange() * (1.0F - float1) > -0.01F) {
				float1 = 1.0F;
			}

			if (food.getHungChange() == 0.0F && food.getThirstChange() < 0.0F && food.getThirstChange() * (1.0F - float1) > -0.01F) {
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
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
			if (player != null) {
				Nutrition nutrition = player.getNutrition();
				nutrition.setCalories(nutrition.getCalories() + food.getCalories() * float1);
				nutrition.setCarbohydrates(nutrition.getCarbohydrates() + food.getCarbohydrates() * float1);
				nutrition.setProteins(nutrition.getProteins() + food.getProteins() * float1);
				nutrition.setLipids(nutrition.getLipids() + food.getLipids() * float1);
			}

			this.BodyDamage.setPainReduction(this.BodyDamage.getPainReduction() + food.getPainReduction() * float1);
			this.BodyDamage.setColdReduction(this.BodyDamage.getColdReduction() + (float)food.getFluReduction() * float1);
			float float5;
			if (this.BodyDamage.getFoodSicknessLevel() > 0.0F && (float)food.getReduceFoodSickness() > 0.0F && this.effectiveEdibleBuffTimer <= 0.0F) {
				float3 = this.BodyDamage.getFoodSicknessLevel();
				this.BodyDamage.setFoodSicknessLevel(this.BodyDamage.getFoodSicknessLevel() - (float)food.getReduceFoodSickness() * float1);
				if (this.BodyDamage.getFoodSicknessLevel() < 0.0F) {
					this.BodyDamage.setFoodSicknessLevel(0.0F);
				}

				float5 = this.BodyDamage.getPoisonLevel();
				this.BodyDamage.setPoisonLevel(this.BodyDamage.getPoisonLevel() - (float)food.getReduceFoodSickness() * float1);
				if (this.BodyDamage.getPoisonLevel() < 0.0F) {
					this.BodyDamage.setPoisonLevel(0.0F);
				}

				if (this.Traits.IronGut.isSet()) {
					this.effectiveEdibleBuffTimer = Rand.Next(80.0F, 150.0F);
				} else if (this.Traits.WeakStomach.isSet()) {
					this.effectiveEdibleBuffTimer = Rand.Next(120.0F, 230.0F);
				} else {
					this.effectiveEdibleBuffTimer = Rand.Next(200.0F, 280.0F);
				}
			}

			this.BodyDamage.JustAteFood(food, float1);
			if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
				GameClient.instance.eatFood((IsoPlayer)this, food, float1);
			}

			if (food.getOnEat() != null) {
				Object object = LuaManager.getFunctionObject(food.getOnEat());
				if (object != null) {
					LuaManager.caller.pcallvoid(LuaManager.thread, object, inventoryItem, this, BoxedStaticValues.toDouble((double)float1));
				}
			}

			if (float1 == 1.0F) {
				food.setHungChange(0.0F);
				food.UseItem();
			} else {
				float3 = food.getHungChange();
				float5 = food.getThirstChange();
				food.multiplyFoodValues(1.0F - float1);
				if (float3 < 0.0F && (double)food.getHungerChange() > -0.00999) {
				}

				if (float3 == 0.0F && float5 < 0.0F && food.getThirstChange() > -0.01F) {
					food.setHungChange(0.0F);
					food.UseItem();
					return true;
				}

				float float6 = 0.0F;
				if (food.isCustomWeight()) {
					String string2 = food.getReplaceOnUseFullType();
					Item item = string2 == null ? null : ScriptManager.instance.getItem(string2);
					if (item != null) {
						float6 = item.getActualWeight();
					}

					food.setWeight(food.getWeight() - float6 - float2 * (food.getWeight() - float6) + float6);
				}
			}

			return true;
		}
	}

	public boolean Eat(InventoryItem inventoryItem) {
		return this.Eat(inventoryItem, 1.0F);
	}

	public void FireCheck() {
		if (!this.OnFire) {
			if (!GameServer.bServer || !(this instanceof IsoPlayer)) {
				if (!GameClient.bClient || !this.isZombie() || !(this instanceof IsoZombie) || !((IsoZombie)this).isRemoteZombie()) {
					if (this.isZombie() && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
						DebugLog.log(DebugType.Zombie, "FireCheck running on REUSABLE ZOMBIE - IGNORED " + this);
					} else if (this.getVehicle() == null) {
						if (this.square != null && !GameServer.bServer && (!GameClient.bClient || this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer() || this instanceof IsoZombie && !((IsoZombie)this).isRemoteZombie()) && this.square.getProperties().Is(IsoFlagType.burning)) {
							if ((!(this instanceof IsoPlayer) || Rand.Next(Rand.AdjustForFramerate(70)) != 0) && !this.isZombie()) {
								if (!(this instanceof IsoPlayer)) {
									this.Health -= this.FireKillRate * GameTime.instance.getMultiplier() / 2.0F;
									this.setAttackedBy((IsoGameCharacter)null);
								} else {
									float float1 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F / 2.0F;
									this.BodyDamage.ReduceGeneralHealth(float1);
									LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "FIRE", float1);
									this.BodyDamage.OnFire(true);
									this.forceAwake();
								}

								if (this.isDead()) {
									IsoFireManager.RemoveBurningCharacter(this);
									if (this.isZombie()) {
										LuaEventManager.triggerEvent("OnZombieDead", this);
										if (GameClient.bClient) {
											this.setAttackedBy(IsoWorld.instance.CurrentCell.getFakeZombieForHit());
										}
									}
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

	public String getPrimaryHandType() {
		return this.leftHandItem == null ? null : this.leftHandItem.getType();
	}

	public float getGlobalMovementMod(boolean boolean1) {
		return this.getCurrentState() != ClimbOverFenceState.instance() && this.getCurrentState() != ClimbThroughWindowState.instance() && this.getCurrentState() != ClimbOverWallState.instance() ? super.getGlobalMovementMod(boolean1) : 1.0F;
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
		this.stateMachine.changeState(state, (Iterable)null);
	}

	public State getCurrentState() {
		return this.stateMachine.getCurrent();
	}

	public boolean isCurrentState(State state) {
		if (this.stateMachine.isSubstate(state)) {
			return true;
		} else {
			return this.stateMachine.getCurrent() == state;
		}
	}

	public HashMap getStateMachineParams(State state) {
		return (HashMap)this.StateMachineParams.computeIfAbsent(state, (var0)->{
			return new HashMap();
		});
	}

	public void setStateMachineLocked(boolean boolean1) {
		this.stateMachine.setLocked(boolean1);
	}

	public float Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
		return this.Hit(handWeapon, gameCharacter, float1, boolean1, float2, false);
	}

	public float Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2, boolean boolean2) {
		if (gameCharacter != null && handWeapon != null) {
			if (!boolean1 && this.isZombie()) {
				IsoZombie zombie = (IsoZombie)this;
				zombie.setHitTime(zombie.getHitTime() + 1);
				if (zombie.getHitTime() >= 4 && !boolean2) {
					float1 = (float)((double)float1 * (double)(zombie.getHitTime() - 2) * 1.5);
				}
			}

			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).bDoShove && !((IsoPlayer)gameCharacter).isAimAtFloor()) {
				boolean1 = true;
				float2 *= 1.5F;
			}

			LuaEventManager.triggerEvent("OnWeaponHitCharacter", gameCharacter, this, handWeapon, float1);
			LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "WEAPONHIT", float1);
			if (LuaHookManager.TriggerHook("WeaponHitCharacter", gameCharacter, this, handWeapon, float1)) {
				return 0.0F;
			} else if (this.m_avoidDamage) {
				this.m_avoidDamage = false;
				return 0.0F;
			} else {
				if (this.noDamage) {
					boolean1 = true;
					this.noDamage = false;
				}

				if (this instanceof IsoSurvivor && !this.EnemyList.contains(gameCharacter)) {
					this.EnemyList.add(gameCharacter);
				}

				this.staggerTimeMod = handWeapon.getPushBackMod() * handWeapon.getKnockbackMod(gameCharacter) * gameCharacter.getShovingMod();
				if (this.isZombie() && Rand.Next(3) == 0 && GameServer.bServer) {
				}

				gameCharacter.addWorldSoundUnlessInvisible(5, 1, false);
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
				this.setAttackedBy(gameCharacter);
				float float3 = float1;
				if (!boolean2) {
					float3 = this.processHitDamage(handWeapon, gameCharacter, float1, boolean1, float2);
				}

				float float4 = 0.0F;
				if (handWeapon.isTwoHandWeapon() && (gameCharacter.getPrimaryHandItem() != handWeapon || gameCharacter.getSecondaryHandItem() != handWeapon)) {
					float4 = handWeapon.getWeight() / 1.5F / 10.0F;
				}

				float float5 = (handWeapon.getWeight() * 0.28F * handWeapon.getFatigueMod(gameCharacter) * this.getFatigueMod() * handWeapon.getEnduranceMod() * 0.3F + float4) * 0.04F;
				if (gameCharacter instanceof IsoPlayer && gameCharacter.isAimAtFloor() && ((IsoPlayer)gameCharacter).bDoShove) {
					float5 *= 2.0F;
				}

				float float6;
				if (handWeapon.isAimedFirearm()) {
					float6 = float3 * 0.7F;
				} else {
					float6 = float3 * 0.15F;
				}

				if (this.getHealth() < float3) {
					float6 = this.getHealth();
				}

				float float7 = float6 / handWeapon.getMaxDamage();
				if (float7 > 1.0F) {
					float7 = 1.0F;
				}

				if (this.isCloseKilled()) {
					float7 = 0.2F;
				}

				if (handWeapon.isUseEndurance()) {
					if (float3 <= 0.0F) {
						float7 = 1.0F;
					}

					Stats stats = gameCharacter.getStats();
					stats.endurance -= float5 * float7;
				}

				this.hitConsequences(handWeapon, gameCharacter, boolean1, float3, boolean2);
				return float3;
			}
		} else {
			return 0.0F;
		}
	}

	public float processHitDamage(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
		float float3 = float1 * float2;
		float float4 = float3;
		if (boolean1) {
			float4 = float3 / 2.7F;
		}

		float float5 = float4 * gameCharacter.getShovingMod();
		if (float5 > 1.0F) {
			float5 = 1.0F;
		}

		this.setHitForce(float5);
		if (gameCharacter.Traits.Strong.isSet() && !handWeapon.isRanged()) {
			this.setHitForce(this.getHitForce() * 1.4F);
		}

		if (gameCharacter.Traits.Weak.isSet() && !handWeapon.isRanged()) {
			this.setHitForce(this.getHitForce() * 0.6F);
		}

		float float6 = IsoUtils.DistanceTo(gameCharacter.getX(), gameCharacter.getY(), this.getX(), this.getY());
		float6 -= handWeapon.getMinRange();
		float6 /= handWeapon.getMaxRange(gameCharacter);
		float6 = 1.0F - float6;
		if (float6 > 1.0F) {
			float6 = 1.0F;
		}

		float float7 = gameCharacter.stats.endurance;
		float7 *= gameCharacter.knockbackAttackMod;
		if (float7 < 0.5F) {
			float7 *= 1.3F;
			if (float7 < 0.4F) {
				float7 = 0.4F;
			}

			this.setHitForce(this.getHitForce() * float7);
		}

		if (!handWeapon.isRangeFalloff()) {
			float6 = 1.0F;
		}

		if (!handWeapon.isShareDamage()) {
			float1 = 1.0F;
		}

		if (gameCharacter instanceof IsoPlayer && !boolean1) {
			this.setHitForce(this.getHitForce() * 2.0F);
		}

		if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).bDoShove) {
			Vector2 vector2 = tempVector2_1.set(this.getX(), this.getY());
			Vector2 vector22 = tempVector2_2.set(gameCharacter.getX(), gameCharacter.getY());
			vector2.x -= vector22.x;
			vector2.y -= vector22.y;
			Vector2 vector23 = this.getVectorFromDirection(tempVector2_2);
			vector2.normalize();
			float float8 = vector2.dot(vector23);
			if (float8 > -0.3F) {
				float3 *= 1.5F;
			}
		}

		if (this instanceof IsoPlayer) {
			float3 *= 0.4F;
		} else {
			float3 *= 1.5F;
		}

		int int1 = gameCharacter.getWeaponLevel();
		switch (int1) {
		case -1: 
			float3 *= 0.3F;
			break;
		
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
		if (gameCharacter instanceof IsoPlayer && gameCharacter.isAimAtFloor() && !boolean1 && !((IsoPlayer)gameCharacter).bDoShove) {
			float3 *= Math.max(5.0F, handWeapon.getCritDmgMultiplier());
		}

		if (gameCharacter.isCriticalHit() && !boolean1) {
			float3 *= Math.max(2.0F, handWeapon.getCritDmgMultiplier());
		}

		if (handWeapon.isTwoHandWeapon() && !gameCharacter.isItemInBothHands(handWeapon)) {
			float3 *= 0.5F;
		}

		return float3;
	}

	public void hitConsequences(HandWeapon handWeapon, IsoGameCharacter gameCharacter, boolean boolean1, float float1, boolean boolean2) {
		if (!boolean1) {
			if (handWeapon.isAimedFirearm()) {
				this.Health -= float1 * 0.7F;
			} else {
				this.Health -= float1 * 0.15F;
			}
		}

		if (this.isDead()) {
			if (!this.isOnKillDone() && this.shouldDoInventory()) {
				this.Kill(gameCharacter);
			}

			if (this instanceof IsoZombie && ((IsoZombie)this).upKillCount) {
				gameCharacter.setZombieKills(gameCharacter.getZombieKills() + 1);
			}
		} else {
			if (handWeapon.isSplatBloodOnNoDeath()) {
				this.splatBlood(2, 0.2F);
			}

			if (handWeapon.isKnockBackOnNoDeath() && gameCharacter.xp != null) {
				gameCharacter.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
			}
		}
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

	public boolean IsAttackRange(HandWeapon handWeapon, IsoMovingObject movingObject, Vector3 vector3, boolean boolean1) {
		if (handWeapon == null) {
			return false;
		} else {
			float float1 = Math.abs(movingObject.getZ() - this.getZ());
			if (!handWeapon.isRanged() && float1 >= 0.5F) {
				return false;
			} else if (float1 > 3.3F) {
				return false;
			} else {
				float float2 = handWeapon.getMaxRange(this);
				float2 *= handWeapon.getRangeMod(this);
				float float3 = IsoUtils.DistanceToSquared(this.x, this.y, vector3.x, vector3.y);
				if (boolean1) {
					IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
					if (zombie != null && float3 < 4.0F && zombie.target == this && (zombie.isCurrentState(LungeState.instance()) || zombie.isCurrentState(LungeNetworkState.instance()))) {
						++float2;
					}
				}

				return float3 < float2 * float2;
			}
		}
	}

	public boolean IsSpeaking() {
		return this.chatElement.IsSpeaking();
	}

	public void MoveForward(float float1, float float2, float float3, float float4) {
		if (!this.isCurrentState(SwipeStatePlayer.instance())) {
			float float5 = GameTime.instance.getMultiplier();
			this.setNx(this.getNx() + float2 * float1 * float5);
			this.setNy(this.getNy() + float3 * float1 * float5);
			this.DoFootstepSound(float1);
			if (!this.isZombie()) {
			}
		}
	}

	private void pathToAux(float float1, float float2, float float3) {
		boolean boolean1 = true;
		if ((int)float3 == (int)this.getZ() && IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) <= 30.0F) {
			int int1 = (int)float1 / 10;
			int int2 = (int)float2 / 10;
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1, int2) : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)float1, (int)float2, (int)float3);
			if (chunk != null) {
				byte byte1 = 1;
				int int3 = byte1 | 2;
				if (!this.isZombie()) {
					int3 |= 4;
				}

				boolean1 = !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), float1, float2, (int)float3, this.getPathFindBehavior2().getTargetChar(), int3);
			}
		}

		if (boolean1 && this.current != null && this.current.HasStairs() && !this.current.isSameStaircase((int)float1, (int)float2, (int)float3)) {
			boolean1 = false;
		}

		if (boolean1) {
			this.setVariable("bPathfind", false);
			this.setMoving(true);
		} else {
			this.setVariable("bPathfind", true);
			this.setMoving(false);
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

	public void pathToSound(int int1, int int2, int int3) {
		this.getPathFindBehavior2().pathToSound(int1, int2, int3);
		this.pathToAux((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3);
	}

	public boolean CanAttack() {
		if (!this.isAttackAnim() && !this.getVariableBoolean("IsRacking") && !this.getVariableBoolean("IsUnloading") && StringUtils.isNullOrEmpty(this.getVariableString("RackWeapon"))) {
			if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer() && (this.isCurrentState(PlayerHitReactionState.instance()) || this.isCurrentState(PlayerHitReactionPVPState.instance()))) {
				return false;
			} else if (this.isSitOnGround()) {
				return false;
			} else {
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
		} else {
			return false;
		}
	}

	public void ReduceHealthWhenBurning() {
		if (this.OnFire) {
			if (this.isGodMod()) {
				this.StopBurning();
			} else if (!GameClient.bClient || !this.isZombie() || !(this instanceof IsoZombie) || !((IsoZombie)this).isRemoteZombie()) {
				if (!GameClient.bClient || !(this instanceof IsoPlayer) || !((IsoPlayer)this).bRemote) {
					if (this.isAlive()) {
						if (!(this instanceof IsoPlayer)) {
							if (this.isZombie()) {
								this.Health -= this.FireKillRate / 20.0F * GameTime.instance.getMultiplier();
								this.setAttackedBy((IsoGameCharacter)null);
							} else {
								this.Health -= this.FireKillRate * GameTime.instance.getMultiplier();
							}
						} else {
							float float1 = this.FireKillRate * GameTime.instance.getMultiplier() * GameTime.instance.getMinutesPerDay() / 1.6F;
							this.BodyDamage.ReduceGeneralHealth(float1);
							LuaEventManager.triggerEvent("OnPlayerGetDamage", this, "FIRE", float1);
							this.BodyDamage.OnFire(true);
						}

						if (this.isDead()) {
							IsoFireManager.RemoveBurningCharacter(this);
							if (this.isZombie()) {
								LuaEventManager.triggerEvent("OnZombieDead", this);
								if (GameClient.bClient) {
									this.setAttackedBy(IsoWorld.instance.CurrentCell.getFakeZombieForHit());
								}
							}
						}
					}

					if (this instanceof IsoPlayer && Rand.Next(Rand.AdjustForFramerate(((IsoPlayer)this).IsRunning() ? 150 : 400)) == 0) {
						this.StopBurning();
					}
				}
			}
		}
	}

	public void DrawSneezeText() {
		if (this.BodyDamage.IsSneezingCoughing() > 0) {
			String string = null;
			if (this.BodyDamage.IsSneezingCoughing() == 1) {
				string = Translator.getText("IGUI_PlayerText_Sneeze");
			}

			if (this.BodyDamage.IsSneezingCoughing() == 2) {
				string = Translator.getText("IGUI_PlayerText_Cough");
			}

			if (this.BodyDamage.IsSneezingCoughing() == 3) {
				string = Translator.getText("IGUI_PlayerText_SneezeMuffled");
			}

			if (this.BodyDamage.IsSneezingCoughing() == 4) {
				string = Translator.getText("IGUI_PlayerText_CoughMuffled");
			}

			float float1 = this.sx;
			float float2 = this.sy;
			float1 = (float)((int)float1);
			float2 = (float)((int)float2);
			float1 -= (float)((int)IsoCamera.getOffX());
			float2 -= (float)((int)IsoCamera.getOffY());
			float2 -= 48.0F;
			if (string != null) {
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

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (!this.isAlphaAndTargetZero()) {
			if (!this.isSeatedInVehicle() || this.getVehicle().showPassenger(this)) {
				if (!this.isSpriteInvisible()) {
					if (!this.isAlphaZero()) {
						if (!this.bUseParts && this.def == null) {
							this.def = new IsoSpriteInstance(this.sprite);
						}

						SpriteRenderer.instance.glDepthMask(true);
						IsoGridSquare square;
						if (this.bDoDefer && float3 - (float)((int)float3) > 0.2F) {
							square = this.getCell().getGridSquare((int)float1, (int)float2, (int)float3 + 1);
							if (square != null) {
								square.addDeferredCharacter(this);
							}
						}

						square = this.getCurrentSquare();
						if (PerformanceSettings.LightingFrameSkip < 3 && square != null) {
							square.interpolateLight(inf, float1 - (float)square.getX(), float2 - (float)square.getY());
						} else {
							inf.r = colorInfo.r;
							inf.g = colorInfo.g;
							inf.b = colorInfo.b;
							inf.a = colorInfo.a;
						}

						if (Core.bDebug && DebugOptions.instance.PathfindRenderWaiting.getValue() && this.hasActiveModel()) {
							if (this.getCurrentState() == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
								this.legsSprite.modelSlot.model.tintR = 1.0F;
								this.legsSprite.modelSlot.model.tintG = 0.0F;
								this.legsSprite.modelSlot.model.tintB = 0.0F;
							} else {
								this.legsSprite.modelSlot.model.tintR = 1.0F;
								this.legsSprite.modelSlot.model.tintG = 1.0F;
								this.legsSprite.modelSlot.model.tintB = 1.0F;
							}
						}

						if (this.dir == IsoDirections.Max) {
							this.dir = IsoDirections.N;
						}

						if (this.sprite != null && !this.legsSprite.hasActiveModel()) {
							this.checkDrawWeaponPre(float1, float2, float3, colorInfo);
						}

						lastRenderedRendered = lastRendered;
						lastRendered = this;
						this.checkUpdateModelTextures();
						float float4 = (float)Core.TileScale;
						float float5 = this.offsetX + 1.0F * float4;
						float float6 = this.offsetY + -89.0F * float4;
						if (this.sprite != null) {
							this.def.setScale(float4, float4);
							if (!this.bUseParts) {
								this.sprite.render(this.def, this, float1, float2, float3, this.dir, float5, float6, inf, true);
							} else if (this.legsSprite.hasActiveModel()) {
								this.legsSprite.renderActiveModel();
							} else if (!this.renderTextureInsteadOfModel(float1, float2)) {
								this.def.Flip = false;
								inf.r = 1.0F;
								inf.g = 1.0F;
								inf.b = 1.0F;
								inf.a = this.def.alpha * 0.4F;
								this.legsSprite.renderCurrentAnim(this.def, this, float1, float2, float3, this.dir, float5, float6, inf, false, (Consumer)null);
							}
						}

						int int1;
						if (this.AttachedAnimSprite != null) {
							for (int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
								IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int1);
								spriteInstance.update();
								float float7 = inf.a;
								inf.a = spriteInstance.alpha;
								spriteInstance.SetTargetAlpha(this.getTargetAlpha());
								spriteInstance.render(this, float1, float2, float3, this.dir, float5, float6, inf);
								inf.a = float7;
							}
						}

						for (int1 = 0; int1 < this.inventory.Items.size(); ++int1) {
							InventoryItem inventoryItem = (InventoryItem)this.inventory.Items.get(int1);
							if (inventoryItem instanceof IUpdater) {
								((IUpdater)inventoryItem).render();
							}
						}
					}
				}
			}
		}
	}

	public void renderServerGUI() {
		if (this instanceof IsoPlayer) {
			this.setSceneCulled(false);
		}

		if (this.bUpdateModelTextures && this.hasActiveModel()) {
			this.bUpdateModelTextures = false;
			this.textureCreator = ModelInstanceTextureCreator.alloc();
			this.textureCreator.init(this);
		}

		float float1 = (float)Core.TileScale;
		float float2 = this.offsetX + 1.0F * float1;
		float float3 = this.offsetY + -89.0F * float1;
		if (this.sprite != null) {
			this.def.setScale(float1, float1);
			inf.r = 1.0F;
			inf.g = 1.0F;
			inf.b = 1.0F;
			inf.a = this.def.alpha * 0.4F;
			if (!this.isbUseParts()) {
				this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, float2, float3, inf, true);
			} else {
				this.def.Flip = false;
				this.legsSprite.render(this.def, this, this.x, this.y, this.z, this.dir, float2, float3, inf, true);
			}
		}

		if (Core.bDebug && this.hasActiveModel()) {
			if (this instanceof IsoZombie) {
				int int1 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
				int int2 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
				TextManager.instance.DrawString((double)int1, (double)int2, "ID: " + this.getOnlineID());
				TextManager.instance.DrawString((double)int1, (double)(int2 + 10), "State: " + this.getCurrentStateName());
				TextManager.instance.DrawString((double)int1, (double)(int2 + 20), "Health: " + this.getHealth());
			}

			Vector2 vector2 = tempo;
			this.getDeferredMovement(vector2);
			this.drawDirectionLine(vector2, 1000.0F * vector2.getLength() / GameTime.instance.getMultiplier() * 2.0F, 1.0F, 0.5F, 0.5F);
		}
	}

	protected float getAlphaUpdateRateMul() {
		float float1 = super.getAlphaUpdateRateMul();
		if (IsoCamera.CamCharacter.Traits.ShortSighted.isSet()) {
			float1 /= 2.0F;
		}

		if (IsoCamera.CamCharacter.Traits.EagleEyed.isSet()) {
			float1 *= 1.5F;
		}

		return float1;
	}

	protected boolean isUpdateAlphaEnabled() {
		return !this.isTeleporting();
	}

	protected boolean isUpdateAlphaDuringRender() {
		return false;
	}

	public boolean isSeatedInVehicle() {
		return this.vehicle != null && this.vehicle.getSeat(this) != -1;
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (!this.bUseParts) {
			this.sprite.renderObjectPicker(this.def, this, this.dir);
		} else {
			this.legsSprite.renderObjectPicker(this.def, this, this.dir);
		}
	}

	static Vector2 closestpointonline(double double1, double double2, double double3, double double4, double double5, double double6, Vector2 vector2) {
		double double7 = double4 - double2;
		double double8 = double1 - double3;
		double double9 = (double4 - double2) * double1 + (double1 - double3) * double2;
		double double10 = -double8 * double5 + double7 * double6;
		double double11 = double7 * double7 - -double8 * double8;
		double double12;
		double double13;
		if (double11 != 0.0) {
			double12 = (double7 * double9 - double8 * double10) / double11;
			double13 = (double7 * double10 - -double8 * double9) / double11;
		} else {
			double12 = double5;
			double13 = double6;
		}

		return vector2.set((float)double12, (float)double13);
	}

	public void renderShadow(float float1, float float2, float float3) {
		if (this.doRenderShadow) {
			if (!this.isAlphaAndTargetZero()) {
				if (!this.isSeatedInVehicle()) {
					IsoGridSquare square = this.getCurrentSquare();
					if (square != null) {
						int int1 = IsoCamera.frameState.playerIndex;
						Vector3f vector3f = IsoGameCharacter.L_renderShadow.forward;
						Vector2 vector2 = this.getAnimVector(tempo2);
						vector3f.set(vector2.x, vector2.y, 0.0F);
						float float4 = 0.45F;
						float float5 = 1.4F;
						float float6 = 1.125F;
						float float7 = this.getAlpha(int1);
						if (this.hasActiveModel() && this.hasAnimationPlayer() && this.getAnimationPlayer().isReady()) {
							AnimationPlayer animationPlayer = this.getAnimationPlayer();
							Vector3 vector3 = IsoGameCharacter.L_renderShadow.v1;
							Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_Head", -1), vector3);
							float float8 = vector3.x;
							float float9 = vector3.y;
							Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_L_Foot", -1), vector3);
							float float10 = vector3.x;
							float float11 = vector3.y;
							Model.BoneToWorldCoords(this, animationPlayer.getSkinningBoneIndex("Bip01_R_Foot", -1), vector3);
							float float12 = vector3.x;
							float float13 = vector3.y;
							Vector3f vector3f2 = IsoGameCharacter.L_renderShadow.v3;
							float float14 = 0.0F;
							float float15 = 0.0F;
							Vector2 vector22 = closestpointonline((double)float1, (double)float2, (double)(float1 + vector3f.x), (double)(float2 + vector3f.y), (double)float8, (double)float9, tempo);
							float float16 = vector22.x;
							float float17 = vector22.y;
							float float18 = vector22.set(float16 - float1, float17 - float2).getLength();
							if (float18 > 0.001F) {
								vector3f2.set(float16 - float1, float17 - float2, 0.0F).normalize();
								if (vector3f.dot(vector3f2) > 0.0F) {
									float14 = Math.max(float14, float18);
								} else {
									float15 = Math.max(float15, float18);
								}
							}

							vector22 = closestpointonline((double)float1, (double)float2, (double)(float1 + vector3f.x), (double)(float2 + vector3f.y), (double)float10, (double)float11, tempo);
							float16 = vector22.x;
							float17 = vector22.y;
							float18 = vector22.set(float16 - float1, float17 - float2).getLength();
							if (float18 > 0.001F) {
								vector3f2.set(float16 - float1, float17 - float2, 0.0F).normalize();
								if (vector3f.dot(vector3f2) > 0.0F) {
									float14 = Math.max(float14, float18);
								} else {
									float15 = Math.max(float15, float18);
								}
							}

							vector22 = closestpointonline((double)float1, (double)float2, (double)(float1 + vector3f.x), (double)(float2 + vector3f.y), (double)float12, (double)float13, tempo);
							float16 = vector22.x;
							float17 = vector22.y;
							float18 = vector22.set(float16 - float1, float17 - float2).getLength();
							if (float18 > 0.001F) {
								vector3f2.set(float16 - float1, float17 - float2, 0.0F).normalize();
								if (vector3f.dot(vector3f2) > 0.0F) {
									float14 = Math.max(float14, float18);
								} else {
									float15 = Math.max(float15, float18);
								}
							}

							float5 = (float14 + 0.35F) * 1.35F;
							float6 = (float15 + 0.35F) * 1.35F;
							float float19 = 0.1F * (GameTime.getInstance().getMultiplier() / 1.6F);
							float19 = PZMath.clamp(float19, 0.0F, 1.0F);
							if (this.shadowTick != IngameState.instance.numberTicks - 1L) {
								this.m_shadowFM = float5;
								this.m_shadowBM = float6;
							}

							this.shadowTick = IngameState.instance.numberTicks;
							this.m_shadowFM = PZMath.lerp(this.m_shadowFM, float5, float19);
							float5 = this.m_shadowFM;
							this.m_shadowBM = PZMath.lerp(this.m_shadowBM, float6, float19);
							float6 = this.m_shadowBM;
						} else if (this.isZombie() && this.isCurrentState(FakeDeadZombieState.instance())) {
							float7 = 1.0F;
						} else if (this.isSceneCulled()) {
							return;
						}

						ColorInfo colorInfo = square.lighting[int1].lightInfo();
						IsoDeadBody.renderShadow(float1, float2, float3, vector3f, float4, float5, float6, colorInfo, float7);
					}
				}
			}
		}
	}

	public void checkUpdateModelTextures() {
		if (this.bUpdateModelTextures && this.hasActiveModel()) {
			this.bUpdateModelTextures = false;
			this.textureCreator = ModelInstanceTextureCreator.alloc();
			this.textureCreator.init(this);
		}

		if (this.bUpdateEquippedTextures && this.hasActiveModel()) {
			this.bUpdateEquippedTextures = false;
			if (this.primaryHandModel != null && this.primaryHandModel.getTextureInitializer() != null) {
				this.primaryHandModel.getTextureInitializer().setDirty();
			}

			if (this.secondaryHandModel != null && this.secondaryHandModel.getTextureInitializer() != null) {
				this.secondaryHandModel.getTextureInitializer().setDirty();
			}
		}
	}

	public boolean isMaskClicked(int int1, int int2, boolean boolean1) {
		if (this.sprite == null) {
			return false;
		} else {
			return !this.bUseParts ? super.isMaskClicked(int1, int2, boolean1) : this.legsSprite.isMaskClicked(this.dir, int1, int2, boolean1);
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

	public float getHaloTimerCount() {
		return this.haloNote != null ? this.haloNote.getInternalClock() : 0.0F;
	}

	public void DoSneezeText() {
		if (this.BodyDamage != null) {
			if (this.BodyDamage.IsSneezingCoughing() > 0) {
				String string = null;
				int int1 = 0;
				if (this.BodyDamage.IsSneezingCoughing() == 1) {
					string = Translator.getText("IGUI_PlayerText_Sneeze");
					int1 = Rand.Next(2) + 1;
					this.setVariable("Ext", "Sneeze" + int1);
				}

				if (this.BodyDamage.IsSneezingCoughing() == 2) {
					string = Translator.getText("IGUI_PlayerText_Cough");
					this.setVariable("Ext", "Cough");
				}

				if (this.BodyDamage.IsSneezingCoughing() == 3) {
					string = Translator.getText("IGUI_PlayerText_SneezeMuffled");
					int1 = Rand.Next(2) + 1;
					this.setVariable("Ext", "Sneeze" + int1);
				}

				if (this.BodyDamage.IsSneezingCoughing() == 4) {
					string = Translator.getText("IGUI_PlayerText_CoughMuffled");
					this.setVariable("Ext", "Cough");
				}

				if (string != null) {
					this.Say(string);
					this.reportEvent("EventDoExt");
					if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
						GameClient.sendSneezingCoughing(this.getOnlineID(), this.BodyDamage.IsSneezingCoughing(), (byte)int1);
					}
				}
			}
		}
	}

	public String getSayLine() {
		return this.chatElement.getSayLine();
	}

	public void setSayLine(String string) {
		this.Say(string);
	}

	public ChatMessage getLastChatMessage() {
		return this.lastChatMessage;
	}

	public void setLastChatMessage(ChatMessage chatMessage) {
		this.lastChatMessage = chatMessage;
	}

	public String getLastSpokenLine() {
		return this.lastSpokenLine;
	}

	public void setLastSpokenLine(String string) {
		this.lastSpokenLine = string;
	}

	protected void doSleepSpeech() {
		++this.sleepSpeechCnt;
		if ((float)this.sleepSpeechCnt > (float)(250 * PerformanceSettings.getLockFPS()) / 30.0F) {
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
		if (!this.isZombie()) {
			this.ProcessSay(string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 30.0F, 0, "default");
		}
	}

	public void Say(String string, float float1, float float2, float float3, UIFont uIFont, float float4, String string2) {
		this.ProcessSay(string, float1, float2, float3, float4, 0, string2);
	}

	public void SayWhisper(String string) {
		this.ProcessSay(string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 10.0F, 0, "whisper");
	}

	public void SayShout(String string) {
		this.ProcessSay(string, this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 60.0F, 0, "shout");
	}

	public void SayRadio(String string, float float1, float float2, float float3, UIFont uIFont, float float4, int int1, String string2) {
		this.ProcessSay(string, float1, float2, float3, float4, int1, string2);
	}

	private void ProcessSay(String string, float float1, float float2, float float3, float float4, int int1, String string2) {
		if (this.AllowConversation) {
			if (TutorialManager.instance.ProfanityFilter) {
				string = ProfanityFilter.getInstance().filterString(string);
			}

			if (string2.equals("default")) {
				ChatManager.getInstance().showInfoMessage(((IsoPlayer)this).getUsername(), string);
				this.lastSpokenLine = string;
			} else if (string2.equals("whisper")) {
				this.lastSpokenLine = string;
			} else if (string2.equals("shout")) {
				ChatManager.getInstance().sendMessageToChat(((IsoPlayer)this).getUsername(), ChatType.shout, string);
				this.lastSpokenLine = string;
			} else if (string2.equals("radio")) {
				UIFont uIFont = UIFont.Medium;
				boolean boolean1 = true;
				boolean boolean2 = true;
				boolean boolean3 = true;
				boolean boolean4 = false;
				boolean boolean5 = false;
				boolean boolean6 = true;
				this.chatElement.addChatLine(string, float1, float2, float3, uIFont, float4, string2, boolean1, boolean2, boolean3, boolean4, boolean5, boolean6);
				if (ZomboidRadio.isStaticSound(string)) {
					ChatManager.getInstance().showStaticRadioSound(string);
				} else {
					ChatManager.getInstance().showRadioMessage(string, int1);
				}
			}
		}
	}

	public void addLineChatElement(String string) {
		this.addLineChatElement(string, 1.0F, 1.0F, 1.0F);
	}

	public void addLineChatElement(String string, float float1, float float2, float float3) {
		this.addLineChatElement(string, float1, float2, float3, UIFont.Dialogue, 30.0F, "default");
	}

	public void addLineChatElement(String string, float float1, float float2, float float3, UIFont uIFont, float float4, String string2) {
		this.addLineChatElement(string, float1, float2, float3, uIFont, float4, string2, false, false, false, false, false, true);
	}

	public void addLineChatElement(String string, float float1, float float2, float float3, UIFont uIFont, float float4, String string2, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6) {
		this.chatElement.addChatLine(string, float1, float2, float3, uIFont, float4, string2, boolean1, boolean2, boolean3, boolean4, boolean5, boolean6);
	}

	protected boolean playerIsSelf() {
		return IsoPlayer.getInstance() == this;
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
			if (IsoPlayer.getInstance() != null) {
				System.out.println("FirstNAME:" + IsoPlayer.getInstance().username);
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
				this.haloNote.setDrawBackground(true);
				this.haloNote.setAllowImages(true);
				this.haloNote.setAllowAnyImage(true);
				this.haloNote.setOutlineColors(0.0F, 0.0F, 0.0F, 0.33F);
			}
		}
	}

	protected void updateUserName() {
		if (this.userName != null && this.isoPlayer != null) {
			String string = this.isoPlayer.getUsername(true);
			if (this != IsoPlayer.getInstance() && this.isInvisible() && IsoPlayer.getInstance() != null && IsoPlayer.getInstance().accessLevel.equals("") && (!Core.bDebug || !DebugOptions.instance.CheatPlayerSeeEveryone.getValue())) {
				this.userName.ReadString("");
				return;
			}

			Faction faction = Faction.getPlayerFaction(this.isoPlayer);
			if (faction != null) {
				if (!this.isoPlayer.showTag && this.isoPlayer != IsoPlayer.getInstance() && Faction.getPlayerFaction(IsoPlayer.getInstance()) != faction) {
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
			boolean boolean2;
			if (IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("")) {
				boolean2 = true;
			} else {
				boolean2 = false;
			}

			boolean boolean3 = IsoCamera.CamCharacter instanceof IsoPlayer && ((IsoPlayer)IsoCamera.CamCharacter).canSeeAll;
			if (!ServerOptions.instance.DisplayUserName.getValue() && !ServerOptions.instance.ShowFirstAndLastName.getValue() && !boolean3) {
				boolean1 = false;
			}

			if (!boolean1) {
				string = "";
			}

			if (boolean1 && this.isoPlayer.tagPrefix != null && !this.isoPlayer.tagPrefix.equals("")) {
				string = "[col=" + (new Float(this.isoPlayer.getTagColor().r * 255.0F)).intValue() + "," + (new Float(this.isoPlayer.getTagColor().g * 255.0F)).intValue() + "," + (new Float(this.isoPlayer.getTagColor().b * 255.0F)).intValue() + "][" + this.isoPlayer.tagPrefix + "][/] " + string;
			}

			if (boolean1 && !this.isoPlayer.accessLevel.equals("") && this.isoPlayer.isShowAdminTag()) {
				String string2 = (String)this.namesPrefix.get(this.isoPlayer.accessLevel);
				string = string2 + string;
			}

			if (boolean1 && !this.isoPlayer.getSafety().isEnabled() && ServerOptions.instance.ShowSafety.getValue() && NonPvpZone.getNonPvpZone(PZMath.fastfloor(this.isoPlayer.x), PZMath.fastfloor(this.isoPlayer.y)) == null) {
				string = string + " [img=media/ui/Skull.png]";
			}

			if (this.isoPlayer.isSpeek && !this.isoPlayer.isVoiceMute) {
				string = "[img=media/ui/voiceon.png] " + string;
			}

			if (this.isoPlayer.isVoiceMute) {
				string = "[img=media/ui/voicemuted.png] " + string;
			}

			BaseVehicle baseVehicle = IsoCamera.CamCharacter == this.isoPlayer ? this.isoPlayer.getNearVehicle() : null;
			if (this.getVehicle() == null && baseVehicle != null && (this.isoPlayer.getInventory().haveThisKeyId(baseVehicle.getKeyId()) != null || baseVehicle.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue())) {
				Color color = Color.HSBtoRGB(baseVehicle.colorHue, baseVehicle.colorSaturation * 0.5F, baseVehicle.colorValue);
				int int1 = color.getRedByte();
				string = " [img=media/ui/CarKey.png," + int1 + "," + color.getGreenByte() + "," + color.getBlueByte() + "]" + string;
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

			this.Speaking = false;
			this.drawUserName = false;
			this.canSeeCurrent = false;
			if (this.haloNote != null && this.haloNote.getInternalClock() > 0.0F) {
				this.haloNote.updateInternalTickClock();
			}

			this.legsSprite.PlayAnim("ZombieWalk1");
			this.chatElement.update();
			this.Speaking = this.chatElement.IsSpeaking();
			if (!this.Speaking || this.isDead()) {
				this.Speaking = false;
				this.callOut = false;
			}
		}
	}

	public void renderlast() {
		super.renderlast();
		int int1 = IsoCamera.frameState.playerIndex;
		float float1 = this.x;
		float float2 = this.y;
		if (this.sx == 0.0F && this.def != null) {
			this.sx = IsoUtils.XToScreen(float1 + this.def.offX, float2 + this.def.offY, this.z + this.def.offZ, 0);
			this.sy = IsoUtils.YToScreen(float1 + this.def.offX, float2 + this.def.offY, this.z + this.def.offZ, 0);
			this.sx -= this.offsetX - 8.0F;
			this.sy -= this.offsetY - 60.0F;
		}

		float float3;
		float float4;
		float float5;
		float float6;
		Color color;
		if (this.hasInitTextObjects && this.isoPlayer != null || this.chatElement.getHasChatToDisplay()) {
			float3 = IsoUtils.XToScreen(float1, float2, this.getZ(), 0);
			float4 = IsoUtils.YToScreen(float1, float2, this.getZ(), 0);
			float3 = float3 - IsoCamera.getOffX() - this.offsetX;
			float4 = float4 - IsoCamera.getOffY() - this.offsetY;
			float4 -= (float)(128 / (2 / Core.TileScale));
			float5 = Core.getInstance().getZoom(int1);
			float3 /= float5;
			float4 /= float5;
			this.canSeeCurrent = true;
			this.drawUserName = false;
			if (this.isoPlayer != null && (this == IsoCamera.frameState.CamCharacter || this.getCurrentSquare() != null && this.getCurrentSquare().getCanSee(int1)) || IsoPlayer.getInstance().isCanSeeAll()) {
				if (this == IsoPlayer.getInstance()) {
					this.canSeeCurrent = true;
				}

				if (GameClient.bClient && this.userName != null && this.doRenderShadow) {
					this.drawUserName = false;
					if (ServerOptions.getInstance().MouseOverToSeeDisplayName.getValue() && this != IsoPlayer.getInstance() && !IsoPlayer.getInstance().isCanSeeAll()) {
						IsoObjectPicker.ClickObject clickObject = IsoObjectPicker.Instance.ContextPick(Mouse.getXA(), Mouse.getYA());
						if (clickObject != null && clickObject.tile != null) {
							for (int int2 = clickObject.tile.square.getX() - 1; int2 < clickObject.tile.square.getX() + 2; ++int2) {
								for (int int3 = clickObject.tile.square.getY() - 1; int3 < clickObject.tile.square.getY() + 2; ++int3) {
									IsoGridSquare square = IsoCell.getInstance().getGridSquare(int2, int3, clickObject.tile.square.getZ());
									if (square != null) {
										for (int int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
											IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int4);
											if (movingObject instanceof IsoPlayer && this == movingObject) {
												this.drawUserName = true;
												break;
											}
										}

										if (this.drawUserName) {
											break;
										}
									}

									if (this.drawUserName) {
										break;
									}
								}
							}
						}
					} else {
						this.drawUserName = true;
					}

					if (this.drawUserName) {
						this.updateUserName();
					}
				}

				if (!GameClient.bClient && this.isoPlayer != null && this.isoPlayer.getVehicle() == null) {
					String string = "";
					BaseVehicle baseVehicle = this.isoPlayer.getNearVehicle();
					if (this.getVehicle() == null && baseVehicle != null && baseVehicle.getPartById("Engine") != null && (this.isoPlayer.getInventory().haveThisKeyId(baseVehicle.getKeyId()) != null || baseVehicle.isHotwired() || SandboxOptions.getInstance().VehicleEasyUse.getValue()) && UIManager.VisibleAllUI) {
						color = Color.HSBtoRGB(baseVehicle.colorHue, baseVehicle.colorSaturation * 0.5F, baseVehicle.colorValue, IsoGameCharacter.L_renderLast.color);
						int int5 = color.getRedByte();
						string = " [img=media/ui/CarKey.png," + int5 + "," + color.getGreenByte() + "," + color.getBlueByte() + "]";
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
					ActionProgressBar actionProgressBar = UIManager.getProgressBar((double)int1);
					if (actionProgressBar != null && actionProgressBar.isVisible()) {
						float4 -= (float)(actionProgressBar.getHeight().intValue() + 2);
					}
				}

				if (this.playerIsSelf() && this.haloNote != null && this.haloNote.getInternalClock() > 0.0F) {
					float6 = this.haloNote.getInternalClock() / (this.haloDispTime / 4.0F);
					float6 = PZMath.min(float6, 1.0F);
					float4 -= (float)(this.haloNote.getHeight() + 2);
					this.haloNote.AddBatchedDraw((double)((int)float3), (double)((int)float4), true, float6);
				}
			}

			boolean boolean1 = false;
			if (IsoPlayer.getInstance() != this && this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && this.equipedRadio.getDeviceData().getHeadphoneType() >= 0) {
				boolean1 = true;
			}

			if (this.equipedRadio != null && this.equipedRadio.getDeviceData() != null && !this.equipedRadio.getDeviceData().getIsTurnedOn()) {
				boolean1 = true;
			}

			boolean boolean2 = GameClient.bClient && IsoCamera.CamCharacter instanceof IsoPlayer && !((IsoPlayer)IsoCamera.CamCharacter).accessLevel.equals("");
			if (!this.m_invisible || this == IsoCamera.frameState.CamCharacter || boolean2) {
				this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)float3, (int)float4, boolean1);
			}
		}

		Vector2 vector2;
		AnimationPlayer animationPlayer;
		if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.Angle.getValue() && this.hasActiveModel()) {
			vector2 = tempo;
			animationPlayer = this.getAnimationPlayer();
			vector2.set(this.dir.ToVector());
			this.drawDirectionLine(vector2, 2.4F, 0.0F, 1.0F, 0.0F);
			vector2.setLengthAndDirection(this.getLookAngleRadians(), 1.0F);
			this.drawDirectionLine(vector2, 2.0F, 1.0F, 1.0F, 1.0F);
			vector2.setLengthAndDirection(this.getAnimAngleRadians(), 1.0F);
			this.drawDirectionLine(vector2, 2.0F, 1.0F, 1.0F, 0.0F);
			float5 = this.getForwardDirection().getDirection();
			vector2.setLengthAndDirection(float5, 1.0F);
			this.drawDirectionLine(vector2, 2.0F, 0.0F, 0.0F, 1.0F);
		}

		if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.DeferredMovement.getValue() && this.hasActiveModel()) {
			vector2 = tempo;
			animationPlayer = this.getAnimationPlayer();
			this.getDeferredMovement(vector2);
			this.drawDirectionLine(vector2, 1000.0F * vector2.getLength() / GameTime.instance.getMultiplier() * 2.0F, 1.0F, 0.5F, 0.5F);
		}

		if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.DeferredAngles.getValue() && this.hasActiveModel()) {
			vector2 = tempo;
			animationPlayer = this.getAnimationPlayer();
			this.getDeferredMovement(vector2);
			this.drawDirectionLine(vector2, 1000.0F * vector2.getLength() / GameTime.instance.getMultiplier() * 2.0F, 1.0F, 0.5F, 0.5F);
		}

		if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.AimCone.getValue()) {
			this.debugAim();
		}

		if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.TestDotSide.getValue()) {
			this.debugTestDotSide();
		}

		if (Core.bDebug && DebugOptions.instance.Character.Debug.Render.Vision.getValue()) {
			this.debugVision();
		}

		if (Core.bDebug) {
			IsoZombie zombie;
			Color color2;
			if (DebugOptions.instance.MultiplayerShowZombieMultiplier.getValue() && this instanceof IsoZombie) {
				zombie = (IsoZombie)this;
				byte byte1 = zombie.canHaveMultipleHits();
				if (byte1 == 0) {
					color2 = Colors.Green;
				} else if (byte1 == 1) {
					color2 = Colors.Yellow;
				} else {
					color2 = Colors.Red;
				}

				LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.45F, 4, color2.r, color2.g, color2.b, 0.5F);
				TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)IsoUtils.XToScreenExact(this.x + 0.4F, this.y + 0.4F, this.z, 0), (double)IsoUtils.YToScreenExact(this.x + 0.4F, this.y - 1.4F, this.z, 0), String.valueOf(zombie.OnlineID), (double)color2.r, (double)color2.g, (double)color2.b, (double)color2.a);
			}

			if (DebugOptions.instance.MultiplayerShowZombieOwner.getValue() && this instanceof IsoZombie) {
				zombie = (IsoZombie)this;
				if (zombie.isDead()) {
					color2 = Colors.Yellow;
				} else if (zombie.isRemoteZombie()) {
					color2 = Colors.OrangeRed;
				} else {
					color2 = Colors.Chartreuse;
				}

				LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.45F, 4, color2.r, color2.g, color2.b, 0.5F);
				TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)IsoUtils.XToScreenExact(this.x + 0.4F, this.y + 0.4F, this.z, 0), (double)IsoUtils.YToScreenExact(this.x + 0.4F, this.y - 1.4F, this.z, 0), String.valueOf(zombie.OnlineID), (double)color2.r, (double)color2.g, (double)color2.b, (double)color2.a);
			}

			if (DebugOptions.instance.MultiplayerShowZombiePrediction.getValue() && this instanceof IsoZombie) {
				zombie = (IsoZombie)this;
				LineDrawer.DrawIsoTransform(this.realx, this.realy, this.z, this.realdir.ToVector().x, this.realdir.ToVector().y, 0.35F, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.35F, 1);
				if (zombie.networkAI.DebugInterfaceActive) {
					LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.4F, 4, 1.0F, 0.1F, 0.1F, 0.35F);
				} else if (!zombie.isRemoteZombie()) {
					LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3F, 3, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35F);
				} else {
					LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3F, 5, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35F);
				}

				LineDrawer.DrawIsoTransform(zombie.networkAI.targetX, zombie.networkAI.targetY, this.z, 1.0F, 0.0F, 0.4F, 16, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.35F, 1);
				LineDrawer.DrawIsoLine(this.x, this.y, this.z, zombie.networkAI.targetX, zombie.networkAI.targetY, this.z, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.35F, 1);
				if (IsoUtils.DistanceToSquared(this.x, this.y, this.realx, this.realy) > 4.5F) {
					LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Magenta.r, Colors.Magenta.g, Colors.Magenta.b, 0.35F, 1);
				} else {
					LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.35F, 1);
				}
			}

			if (DebugOptions.instance.MultiplayerShowZombieDesync.getValue() && this instanceof IsoZombie) {
				zombie = (IsoZombie)this;
				float4 = IsoUtils.DistanceTo(this.getX(), this.getY(), this.realx, this.realy);
				if (zombie.isRemoteZombie() && float4 > 1.0F) {
					LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.9F, 1);
					LineDrawer.DrawIsoTransform(this.realx, this.realy, this.z, this.realdir.ToVector().x, this.realdir.ToVector().y, 0.35F, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.9F, 1);
					LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.4F, 4, 1.0F, 1.0F, 1.0F, 0.9F);
					float5 = IsoUtils.DistanceTo(this.realx, this.realy, zombie.networkAI.targetX, zombie.networkAI.targetY);
					float6 = IsoUtils.DistanceTo(this.x, this.y, zombie.networkAI.targetX, zombie.networkAI.targetY) / float5;
					float float7 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
					float float8 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
					TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)float7, (double)float8, String.format("dist:%f scale1:%f", float4, float6), (double)Colors.NavajoWhite.r, (double)Colors.NavajoWhite.g, (double)Colors.NavajoWhite.b, 0.8999999761581421);
				}
			}

			if (DebugOptions.instance.MultiplayerShowHit.getValue() && this.getHitReactionNetworkAI() != null && this.getHitReactionNetworkAI().isSetup()) {
				LineDrawer.DrawIsoLine(this.x, this.y, this.z, this.x + this.getHitDir().getX(), this.y + this.getHitDir().getY(), this.z, Colors.BlueViolet.r, Colors.BlueViolet.g, Colors.BlueViolet.b, 0.8F, 1);
				LineDrawer.DrawIsoLine(this.getHitReactionNetworkAI().startPosition.x, this.getHitReactionNetworkAI().startPosition.y, this.z, this.getHitReactionNetworkAI().finalPosition.x, this.getHitReactionNetworkAI().finalPosition.y, this.z, Colors.Salmon.r, Colors.Salmon.g, Colors.Salmon.b, 0.8F, 1);
				float float9 = Colors.Salmon.r - 0.2F;
				float float10 = Colors.Salmon.g + 0.2F;
				LineDrawer.DrawIsoTransform(this.getHitReactionNetworkAI().startPosition.x, this.getHitReactionNetworkAI().startPosition.y, this.z, this.getHitReactionNetworkAI().startDirection.x, this.getHitReactionNetworkAI().startDirection.y, 0.4F, 16, float9, float10, Colors.Salmon.b, 0.8F, 1);
				float10 = Colors.Salmon.g - 0.2F;
				LineDrawer.DrawIsoTransform(this.getHitReactionNetworkAI().finalPosition.x, this.getHitReactionNetworkAI().finalPosition.y, this.z, this.getHitReactionNetworkAI().finalDirection.x, this.getHitReactionNetworkAI().finalDirection.y, 0.4F, 16, Colors.Salmon.r, float10, Colors.Salmon.b, 0.8F, 1);
			}

			if (DebugOptions.instance.MultiplayerShowPlayerPrediction.getValue() && this instanceof IsoPlayer) {
				if (this.isoPlayer != null && this.isoPlayer.networkAI != null && this.isoPlayer.networkAI.footstepSoundRadius != 0) {
					LineDrawer.DrawIsoCircle(this.x, this.y, this.z, (float)this.isoPlayer.networkAI.footstepSoundRadius, 32, Colors.Violet.r, Colors.Violet.g, Colors.Violet.b, 0.5F);
				}

				if (this.isoPlayer != null && this.isoPlayer.bRemote) {
					LineDrawer.DrawIsoCircle(this.x, this.y, this.z, 0.3F, 16, Colors.OrangeRed.r, Colors.OrangeRed.g, Colors.OrangeRed.b, 0.5F);
					tempo.set(this.realdir.ToVector());
					LineDrawer.DrawIsoTransform(this.realx, this.realy, this.z, tempo.x, tempo.y, 0.35F, 16, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.5F, 1);
					LineDrawer.DrawIsoLine(this.realx, this.realy, this.z, this.x, this.y, this.z, Colors.Blue.r, Colors.Blue.g, Colors.Blue.b, 0.5F, 1);
					tempo.set(((IsoPlayer)this).networkAI.targetX, ((IsoPlayer)this).networkAI.targetY);
					LineDrawer.DrawIsoTransform(tempo.x, tempo.y, this.z, 1.0F, 0.0F, 0.4F, 16, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.5F, 1);
					LineDrawer.DrawIsoLine(this.x, this.y, this.z, tempo.x, tempo.y, this.z, Colors.LimeGreen.r, Colors.LimeGreen.g, Colors.LimeGreen.b, 0.5F, 1);
				}
			}

			if (DebugOptions.instance.MultiplayerShowTeleport.getValue() && this.getNetworkCharacterAI() != null) {
				NetworkTeleport.NetworkTeleportDebug networkTeleportDebug = this.getNetworkCharacterAI().getTeleportDebug();
				if (networkTeleportDebug != null) {
					LineDrawer.DrawIsoLine(networkTeleportDebug.lx, networkTeleportDebug.ly, networkTeleportDebug.lz, networkTeleportDebug.nx, networkTeleportDebug.ny, networkTeleportDebug.nz, Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.7F, 3);
					LineDrawer.DrawIsoCircle(networkTeleportDebug.nx, networkTeleportDebug.ny, networkTeleportDebug.nz, 0.2F, 16, Colors.NavajoWhite.r, Colors.NavajoWhite.g, Colors.NavajoWhite.b, 0.7F);
					float4 = IsoUtils.XToScreenExact(networkTeleportDebug.lx, networkTeleportDebug.ly, networkTeleportDebug.lz, 0);
					float5 = IsoUtils.YToScreenExact(networkTeleportDebug.lx, networkTeleportDebug.ly, networkTeleportDebug.lz, 0);
					TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)float4, (double)float5, String.format("%s id=%d", this instanceof IsoPlayer ? ((IsoPlayer)this).getUsername() : this.getClass().getSimpleName(), networkTeleportDebug.id), (double)Colors.NavajoWhite.r, (double)Colors.NavajoWhite.g, (double)Colors.NavajoWhite.b, 0.699999988079071);
					TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)float4, (double)(float5 + 10.0F), networkTeleportDebug.type.name(), (double)Colors.NavajoWhite.r, (double)Colors.NavajoWhite.g, (double)Colors.NavajoWhite.b, 0.699999988079071);
				}
			} else if (this.getNetworkCharacterAI() != null) {
				this.getNetworkCharacterAI().clearTeleportDebug();
			}

			if (DebugOptions.instance.MultiplayerShowZombieStatus.getValue() && this instanceof IsoZombie || DebugOptions.instance.MultiplayerShowPlayerStatus.getValue() && this instanceof IsoPlayer && !((IsoPlayer)this).isGodMod()) {
				TextManager textManager = TextManager.instance;
				Objects.requireNonNull(textManager);
				TextManager.StringDrawer stringDrawer = textManager::DrawString;
				if (this instanceof IsoPlayer && this.isLocal()) {
					textManager = TextManager.instance;
					Objects.requireNonNull(textManager);
					stringDrawer = textManager::DrawStringRight;
				}

				float4 = IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
				float5 = IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
				float6 = 10.0F;
				Color color3 = Colors.GreenYellow;
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("%d %s : %.03f / %.03f", this.getOnlineID(), this.isFemale() ? "F" : "M", this.getHealth(), this instanceof IsoZombie ? 0.0F : this.getBodyDamage().getOverallBodyHealth()), (double)color3.r, (double)color3.g, (double)color3.b, (double)color3.a);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("x=%09.3f ", this.x) + String.format("y=%09.3f ", this.y) + String.format("z=%d", (byte)((int)this.z)), (double)color3.r, (double)color3.g, (double)color3.b, (double)color3.a);
				if (this instanceof IsoPlayer) {
					IsoPlayer player = (IsoPlayer)this;
					color = Colors.NavajoWhite;
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 18.0F)), String.format("IdleSpeed: %s , targetDist: %s ", player.getVariableString("IdleSpeed"), player.getVariableString("targetDist")), (double)color.r, (double)color.g, (double)color.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("WalkInjury: %s , WalkSpeed: %s", player.getVariableString("WalkInjury"), player.getVariableString("WalkSpeed")), (double)color.r, (double)color.g, (double)color.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("DeltaX: %s , DeltaY: %s", player.getVariableString("DeltaX"), player.getVariableString("DeltaY")), (double)color.r, (double)color.g, (double)color.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("AttackVariationX: %s , AttackVariationY: %s", player.getVariableString("AttackVariationX"), player.getVariableString("AttackVariationY")), (double)color.r, (double)color.g, (double)color.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("autoShootVarX: %s , autoShootVarY: %s", player.getVariableString("autoShootVarX"), player.getVariableString("autoShootVarY")), (double)color.r, (double)color.g, (double)color.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("recoilVarX: %s , recoilVarY: %s", player.getVariableString("recoilVarX"), player.getVariableString("recoilVarY")), (double)color.r, (double)color.g, (double)color.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("ShoveAimX: %s , ShoveAimY: %s", player.getVariableString("ShoveAimX"), player.getVariableString("ShoveAimY")), (double)color.r, (double)color.g, (double)color.b, 1.0);
				}

				color3 = Colors.Yellow;
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 18.0F)), String.format("isHitFromBehind=%b/%b", this.isHitFromBehind(), this.getVariableBoolean("frombehind")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("bKnockedDown=%b/%b", this.isKnockedDown(), this.getVariableBoolean("bknockeddown")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("isFallOnFront=%b/%b", this.isFallOnFront(), this.getVariableBoolean("fallonfront")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("isOnFloor=%b/%b", this.isOnFloor(), this.getVariableBoolean("bonfloor")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("isDead=%b/%b", this.isDead(), this.getVariableBoolean("bdead")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				if (this instanceof IsoZombie) {
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("bThump=%b", this.getVariableString("bThump")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("ThumpType=%s", this.getVariableString("ThumpType")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("onknees=%b", this.getVariableBoolean("onknees")), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				} else {
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("isBumped=%b/%s", this.isBumped(), this.getBumpType()), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				}

				color3 = Colors.OrangeRed;
				if (this.getReanimateTimer() <= 0.0F) {
					color3 = Colors.LimeGreen;
				} else if (this.isBeingSteppedOn()) {
					color3 = Colors.Blue;
				}

				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 18.0F)), "Reanimate: " + this.getReanimateTimer(), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				if (this.advancedAnimator.getRootLayer() != null) {
					color3 = Colors.Pink;
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 18.0F)), "Animation set: " + this.advancedAnimator.animSet.m_Name, (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), "Animation state: " + this.advancedAnimator.getCurrentStateName(), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), "Animation node: " + this.advancedAnimator.getRootLayer().getDebugNodeName(), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				}

				color3 = Colors.LightBlue;
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("Previous state: %s ( %s )", this.getPreviousStateName(), this.getPreviousActionContextStateName()), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("Current state: %s ( %s )", this.getCurrentStateName(), this.getCurrentActionContextStateName()), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("Child state: %s", this.getActionContext() != null && this.getActionContext().getChildStates() != null && this.getActionContext().getChildStates().size() > 0 && this.getActionContext().getChildStateAt(0) != null ? this.getActionContext().getChildStateAt(0).getName() : "\"\""), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
				if (this.CharacterActions != null) {
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("Character actions: %d", this.CharacterActions.size()), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					Iterator iterator = this.CharacterActions.iterator();
					while (iterator.hasNext()) {
						BaseAction baseAction = (BaseAction)iterator.next();
						if (baseAction instanceof LuaTimedActionNew) {
							stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("Action: %s", ((LuaTimedActionNew)baseAction).getMetaType()), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
						}
					}
				}

				if (this instanceof IsoZombie) {
					color3 = Colors.GreenYellow;
					IsoZombie zombie2 = (IsoZombie)this;
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 18.0F)), "Prediction: " + this.getNetworkCharacterAI().predictionType, (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), String.format("Real state: %s", zombie2.realState), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					if (zombie2.target instanceof IsoPlayer) {
						stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), "Target: " + ((IsoPlayer)zombie2.target).username + "  =" + zombie2.vectorToTarget.getLength(), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					} else {
						stringDrawer.draw(UIFont.DebugConsole, (double)float4, (double)(float5 + (float6 += 11.0F)), "Target: " + zombie2.target + "  =" + zombie2.vectorToTarget.getLength(), (double)color3.r, (double)color3.g, (double)color3.b, 1.0);
					}
				}
			}
		}

		if (this.inventory != null) {
			int int6;
			for (int6 = 0; int6 < this.inventory.Items.size(); ++int6) {
				InventoryItem inventoryItem = (InventoryItem)this.inventory.Items.get(int6);
				if (inventoryItem instanceof IUpdater) {
					((IUpdater)inventoryItem).renderlast();
				}
			}

			if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.pfb2 != null) {
				this.pfb2.render();
			}

			if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderRadius.getValue()) {
				float3 = 0.3F;
				float4 = 1.0F;
				float5 = 1.0F;
				float6 = 1.0F;
				if (!this.isCollidable()) {
					float6 = 0.0F;
				}

				if ((int)this.z != (int)IsoCamera.frameState.CamCharacterZ) {
					float6 = 0.5F;
					float5 = 0.5F;
					float4 = 0.5F;
				}

				LineDrawer.DrawIsoCircle(this.x, this.y, this.z, float3, 16, float4, float5, float6, 1.0F);
			}

			if (DebugOptions.instance.Animation.Debug.getValue() && this.hasActiveModel()) {
				int6 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
				int int7 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
				TextManager.instance.DrawString((double)int6, (double)int7, this.getAnimationDebug());
			}

			if (this.getIsNPC() && this.GameCharacterAIBrain != null) {
				this.GameCharacterAIBrain.renderlast();
			}
		}
	}

	protected boolean renderTextureInsteadOfModel(float float1, float float2) {
		return false;
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

	public void drawDebugTextBelow(String string) {
		int int1 = TextManager.instance.MeasureStringX(UIFont.Small, string) + 32;
		int int2 = TextManager.instance.getFontHeight(UIFont.Small);
		int int3 = (int)Math.ceil((double)int2 * 1.25);
		float float1 = IsoUtils.XToScreenExact(this.getX() + 0.25F, this.getY() + 0.25F, this.getZ(), 0);
		float float2 = IsoUtils.YToScreenExact(this.getX() + 0.25F, this.getY() + 0.25F, this.getZ(), 0);
		SpriteRenderer.instance.renderi((Texture)null, (int)(float1 - (float)(int1 / 2)), (int)(float2 - (float)((int3 - int2) / 2)), int1, int3, 0.0F, 0.0F, 0.0F, 0.5F, (Consumer)null);
		TextManager.instance.DrawStringCentre(UIFont.Small, (double)float1, (double)float2, string, 1.0, 1.0, 1.0, 1.0);
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

	private void debugAim() {
		if (this == IsoPlayer.getInstance()) {
			IsoPlayer player = (IsoPlayer)this;
			if (player.IsAiming()) {
				HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
				if (handWeapon == null) {
					handWeapon = player.bareHands;
				}

				float float1 = handWeapon.getMaxRange(player) * handWeapon.getRangeMod(player);
				float float2 = this.getLookAngleRadians();
				LineDrawer.drawDirectionLine(this.x, this.y, this.z, float1, float2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
				float float3 = handWeapon.getMinAngle();
				float3 -= handWeapon.getAimingPerkMinAngleModifier() * ((float)this.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F);
				LineDrawer.drawDotLines(this.x, this.y, this.z, float1, float2, float3, 1.0F, 1.0F, 1.0F, 0.5F, 1);
				float float4 = handWeapon.getMinRange();
				LineDrawer.drawArc(this.x, this.y, this.z, float4, float2, float3, 6, 1.0F, 1.0F, 1.0F, 0.5F);
				if (float4 != float1) {
					LineDrawer.drawArc(this.x, this.y, this.z, float1, float2, float3, 6, 1.0F, 1.0F, 1.0F, 0.5F);
				}

				float float5 = PZMath.min(float1 + 1.0F, 2.0F);
				LineDrawer.drawArc(this.x, this.y, this.z, float5, float2, float3, 6, 0.75F, 0.75F, 0.75F, 0.5F);
				float float6 = Core.getInstance().getIgnoreProneZombieRange();
				if (float6 > 0.0F) {
					LineDrawer.drawArc(this.x, this.y, this.z, float6, float2, 0.0F, 12, 0.0F, 0.0F, 1.0F, 0.25F);
					LineDrawer.drawDotLines(this.x, this.y, this.z, float6, float2, 0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1);
				}

				AttackVars attackVars = new AttackVars();
				ArrayList arrayList = new ArrayList();
				SwipeStatePlayer.instance().CalcAttackVars((IsoLivingCharacter)this, attackVars);
				SwipeStatePlayer.instance().CalcHitList(this, false, attackVars, arrayList);
				HitInfo hitInfo;
				if (attackVars.targetOnGround.getMovingObject() != null) {
					hitInfo = (HitInfo)attackVars.targetsProne.get(0);
					LineDrawer.DrawIsoCircle(hitInfo.x, hitInfo.y, hitInfo.z, 0.1F, 8, 1.0F, 1.0F, 0.0F, 1.0F);
				} else if (attackVars.targetsStanding.size() > 0) {
					hitInfo = (HitInfo)attackVars.targetsStanding.get(0);
					LineDrawer.DrawIsoCircle(hitInfo.x, hitInfo.y, hitInfo.z, 0.1F, 8, 1.0F, 1.0F, 0.0F, 1.0F);
				}

				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					HitInfo hitInfo2 = (HitInfo)arrayList.get(int1);
					IsoMovingObject movingObject = hitInfo2.getObject();
					if (movingObject != null) {
						int int2 = hitInfo2.chance;
						float float7 = 1.0F - (float)int2 / 100.0F;
						float float8 = 1.0F - float7;
						float float9 = Math.max(0.2F, (float)int2 / 100.0F) / 2.0F;
						float float10 = IsoUtils.XToScreenExact(movingObject.x - float9, movingObject.y + float9, movingObject.z, 0);
						float float11 = IsoUtils.YToScreenExact(movingObject.x - float9, movingObject.y + float9, movingObject.z, 0);
						float float12 = IsoUtils.XToScreenExact(movingObject.x - float9, movingObject.y - float9, movingObject.z, 0);
						float float13 = IsoUtils.YToScreenExact(movingObject.x - float9, movingObject.y - float9, movingObject.z, 0);
						float float14 = IsoUtils.XToScreenExact(movingObject.x + float9, movingObject.y - float9, movingObject.z, 0);
						float float15 = IsoUtils.YToScreenExact(movingObject.x + float9, movingObject.y - float9, movingObject.z, 0);
						float float16 = IsoUtils.XToScreenExact(movingObject.x + float9, movingObject.y + float9, movingObject.z, 0);
						float float17 = IsoUtils.YToScreenExact(movingObject.x + float9, movingObject.y + float9, movingObject.z, 0);
						SpriteRenderer.instance.renderPoly(float10, float11, float12, float13, float14, float15, float16, float17, float7, float8, 0.0F, 0.5F);
						UIFont uIFont = UIFont.DebugConsole;
						TextManager.instance.DrawStringCentre(uIFont, (double)float16, (double)float17, String.valueOf(hitInfo2.dot), 1.0, 1.0, 1.0, 1.0);
						TextManager.instance.DrawStringCentre(uIFont, (double)float16, (double)(float17 + (float)TextManager.instance.getFontHeight(uIFont)), hitInfo2.chance + "%", 1.0, 1.0, 1.0, 1.0);
						float7 = 1.0F;
						float8 = 1.0F;
						float float18 = 1.0F;
						float float19 = PZMath.sqrt(hitInfo2.distSq);
						if (float19 < handWeapon.getMinRange()) {
							float18 = 0.0F;
							float7 = 0.0F;
						}

						TextManager.instance.DrawStringCentre(uIFont, (double)float16, (double)(float17 + (float)(TextManager.instance.getFontHeight(uIFont) * 2)), "DIST: " + float19, (double)float7, (double)float8, (double)float18, 1.0);
					}

					if (hitInfo2.window.getObject() != null) {
						hitInfo2.window.getObject().setHighlighted(true);
					}
				}
			}
		}
	}

	private void debugTestDotSide() {
		if (this == IsoPlayer.getInstance()) {
			float float1 = this.getLookAngleRadians();
			float float2 = 2.0F;
			float float3 = 0.7F;
			LineDrawer.drawDotLines(this.x, this.y, this.z, float2, float1, float3, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			float3 = -0.5F;
			LineDrawer.drawDotLines(this.x, this.y, this.z, float2, float1, float3, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			LineDrawer.drawArc(this.x, this.y, this.z, float2, float1, -1.0F, 16, 1.0F, 1.0F, 1.0F, 0.5F);
			ArrayList arrayList = this.getCell().getZombieList();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int1);
				if (this.DistToSquared(movingObject) < float2 * float2) {
					LineDrawer.DrawIsoCircle(movingObject.x, movingObject.y, movingObject.z, 0.3F, 1.0F, 1.0F, 1.0F, 1.0F);
					float float4 = 0.2F;
					float float5 = IsoUtils.XToScreenExact(movingObject.x + float4, movingObject.y + float4, movingObject.z, 0);
					float float6 = IsoUtils.YToScreenExact(movingObject.x + float4, movingObject.y + float4, movingObject.z, 0);
					UIFont uIFont = UIFont.DebugConsole;
					int int2 = TextManager.instance.getFontHeight(uIFont);
					TextManager.instance.DrawStringCentre(uIFont, (double)float5, (double)(float6 + (float)int2), "SIDE: " + this.testDotSide(movingObject), 1.0, 1.0, 1.0, 1.0);
					Vector2 vector2 = this.getLookVector(tempo2);
					Vector2 vector22 = tempo.set(movingObject.x - this.x, movingObject.y - this.y);
					vector22.normalize();
					float float7 = PZMath.wrap(vector22.getDirection() - vector2.getDirection(), 0.0F, 6.2831855F);
					TextManager.instance.DrawStringCentre(uIFont, (double)float5, (double)(float6 + (float)(int2 * 2)), "ANGLE (0-360): " + PZMath.radToDeg(float7), 1.0, 1.0, 1.0, 1.0);
					float7 = (float)Math.acos((double)this.getDotWithForwardDirection(movingObject.x, movingObject.y));
					TextManager.instance.DrawStringCentre(uIFont, (double)float5, (double)(float6 + (float)(int2 * 3)), "ANGLE (0-180): " + PZMath.radToDeg(float7), 1.0, 1.0, 1.0, 1.0);
				}
			}
		}
	}

	private void debugVision() {
		if (this == IsoPlayer.getInstance()) {
			float float1 = LightingJNI.calculateVisionCone(this);
			LineDrawer.drawDotLines(this.x, this.y, this.z, GameTime.getInstance().getViewDist(), this.getLookAngleRadians(), -float1, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			LineDrawer.drawArc(this.x, this.y, this.z, GameTime.getInstance().getViewDist(), this.getLookAngleRadians(), -float1, 16, 1.0F, 1.0F, 1.0F, 0.5F);
			float float2 = 3.5F - this.stats.getFatigue();
			LineDrawer.drawArc(this.x, this.y, this.z, float2, this.getLookAngleRadians(), -1.0F, 32, 1.0F, 1.0F, 1.0F, 0.5F);
		}
	}

	public void setDefaultState() {
		this.stateMachine.changeState(this.defaultState, (Iterable)null);
	}

	public void SetOnFire() {
		if (!this.OnFire) {
			this.setOnFire(true);
			float float1 = (float)Core.TileScale;
			this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, (int)(-(this.offsetX + 1.0F * float1)) + (8 - Rand.Next(16)), (int)(-(this.offsetY + -89.0F * float1)) + (int)((float)(10 + Rand.Next(20)) * float1), true, 0, false, 0.7F, IsoFireManager.FireTintMod);
			IsoFireManager.AddBurningCharacter(this);
			int int1 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
			if (this instanceof IsoPlayer) {
				((BodyPart)this.getBodyDamage().getBodyParts().get(int1)).setBurned();
			}

			if (float1 == 2.0F) {
				int int2 = this.AttachedAnimSprite.size() - 1;
				((IsoSpriteInstance)this.AttachedAnimSprite.get(int2)).setScale(float1, float1);
			}

			if (!this.getEmitter().isPlaying("BurningFlesh")) {
				this.getEmitter().playSoundImpl("BurningFlesh", this);
			}
		}
	}

	public void StopBurning() {
		if (this.OnFire) {
			IsoFireManager.RemoveBurningCharacter(this);
			this.setOnFire(false);
			if (this.AttachedAnimSprite != null) {
				this.AttachedAnimSprite.clear();
			}

			this.getEmitter().stopOrTriggerSoundByName("BurningFlesh");
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

			if (this.isZombie()) {
				IsoZombie zombie = (IsoZombie)this;
				GameClient.sendStopFire((IsoGameCharacter)zombie);
			}
		}
	}

	public void SpreadFireMP() {
		if (this.OnFire && GameServer.bServer && SandboxOptions.instance.FireSpread.getValue()) {
			IsoGridSquare square = ServerMap.instance.getGridSquare((int)this.x, (int)this.y, (int)this.z);
			if (square != null && !square.getProperties().Is(IsoFlagType.burning) && Rand.Next(Rand.AdjustForFramerate(3000)) < this.FireSpreadProbability) {
				IsoFireManager.StartFire(this.getCell(), square, false, 80);
			}
		}
	}

	public void SpreadFire() {
		if (this.OnFire && !GameServer.bServer && !GameClient.bClient && SandboxOptions.instance.FireSpread.getValue()) {
			if (this.square != null && !this.square.getProperties().Is(IsoFlagType.burning) && Rand.Next(Rand.AdjustForFramerate(3000)) < this.FireSpreadProbability) {
				IsoFireManager.StartFire(this.getCell(), this.square, false, 80);
			}
		}
	}

	public void Throw(HandWeapon handWeapon) {
		if (this instanceof IsoPlayer && ((IsoPlayer)this).getJoypadBind() != -1) {
			Vector2 vector2 = tempo.set(this.m_forwardDirection);
			vector2.setLength(handWeapon.getMaxRange());
			this.attackTargetSquare = this.getCell().getGridSquare((double)(this.getX() + vector2.getX()), (double)(this.getY() + vector2.getY()), (double)this.getZ());
			if (this.attackTargetSquare == null) {
				this.attackTargetSquare = this.getCell().getGridSquare((double)(this.getX() + vector2.getX()), (double)(this.getY() + vector2.getY()), 0.0);
			}
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

		if (handWeapon.getPhysicsObject().equals("Ball")) {
			new IsoBall(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6F, float1 * 0.4F, float2 * 0.4F, handWeapon, this);
		} else {
			new IsoMolotovCocktail(this.getCell(), this.getX(), this.getY(), this.getZ() + 0.6F, float1 * 0.4F, float2 * 0.4F, handWeapon, this);
		}

		if (this instanceof IsoPlayer) {
			((IsoPlayer)this).setAttackAnimThrowTimer(0L);
		}
	}

	public void serverRemoveItemFromZombie(String string) {
		if (GameServer.bServer) {
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(this, IsoZombie.class);
			this.getItemVisuals(tempItemVisuals);
			for (int int1 = 0; int1 < tempItemVisuals.size(); ++int1) {
				ItemVisual itemVisual = (ItemVisual)tempItemVisuals.get(int1);
				Item item = itemVisual.getScriptItem();
				if (item != null && item.name.equals(string)) {
					tempItemVisuals.remove(int1--);
					zombie.itemVisuals.clear();
					zombie.itemVisuals.addAll(tempItemVisuals);
				}
			}
		}
	}

	public boolean helmetFall(boolean boolean1) {
		return this.helmetFall(boolean1, (String)null);
	}

	public boolean helmetFall(boolean boolean1, String string) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
		boolean boolean2 = false;
		InventoryItem inventoryItem = null;
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(this, IsoZombie.class);
		int int1;
		IsoFallingClothing fallingClothing;
		if (zombie != null && !zombie.isUsingWornItems()) {
			this.getItemVisuals(tempItemVisuals);
			for (int1 = 0; int1 < tempItemVisuals.size(); ++int1) {
				ItemVisual itemVisual = (ItemVisual)tempItemVisuals.get(int1);
				Item item = itemVisual.getScriptItem();
				if (item != null && item.getType() == Item.Type.Clothing && item.getChanceToFall() > 0) {
					int int2 = item.getChanceToFall();
					if (boolean1) {
						int2 += 40;
					}

					if (item.name.equals(string)) {
						int2 = 100;
					}

					if (Rand.Next(100) > int2) {
						InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(item.getFullName());
						if (inventoryItem2 != null) {
							if (inventoryItem2.getVisual() != null) {
								inventoryItem2.getVisual().copyFrom(itemVisual);
								inventoryItem2.synchWithVisual();
							}

							fallingClothing = new IsoFallingClothing(this.getCell(), this.getX(), this.getY(), PZMath.min(this.getZ() + 0.4F, (float)((int)this.getZ()) + 0.95F), 0.2F, 0.2F, inventoryItem2);
							if (!StringUtils.isNullOrEmpty(string)) {
								fallingClothing.addWorldItem = false;
							}

							tempItemVisuals.remove(int1--);
							zombie.itemVisuals.clear();
							zombie.itemVisuals.addAll(tempItemVisuals);
							this.resetModelNextFrame();
							this.onWornItemsChanged();
							boolean2 = true;
							inventoryItem = inventoryItem2;
						}
					}
				}
			}
		} else if (this.getWornItems() != null && !this.getWornItems().isEmpty()) {
			for (int1 = 0; int1 < this.getWornItems().size(); ++int1) {
				WornItem wornItem = this.getWornItems().get(int1);
				InventoryItem inventoryItem3 = wornItem.getItem();
				String string2 = wornItem.getLocation();
				if (inventoryItem3 instanceof Clothing) {
					int int3 = ((Clothing)inventoryItem3).getChanceToFall();
					if (boolean1) {
						int3 += 40;
					}

					if (inventoryItem3.getType().equals(string)) {
						int3 = 100;
					}

					if (((Clothing)inventoryItem3).getChanceToFall() > 0 && Rand.Next(100) <= int3) {
						fallingClothing = new IsoFallingClothing(this.getCell(), this.getX(), this.getY(), PZMath.min(this.getZ() + 0.4F, (float)((int)this.getZ()) + 0.95F), Rand.Next(-0.2F, 0.2F), Rand.Next(-0.2F, 0.2F), inventoryItem3);
						if (!StringUtils.isNullOrEmpty(string)) {
							fallingClothing.addWorldItem = false;
						}

						this.getInventory().Remove(inventoryItem3);
						this.getWornItems().remove(inventoryItem3);
						inventoryItem = inventoryItem3;
						this.resetModelNextFrame();
						this.onWornItemsChanged();
						boolean2 = true;
						if (GameClient.bClient && player != null && player.isLocalPlayer() && StringUtils.isNullOrEmpty(string)) {
							GameClient.instance.sendClothing(player, string2, (InventoryItem)null);
						}
					}
				}
			}
		}

		if (boolean2 && GameClient.bClient && StringUtils.isNullOrEmpty(string) && IsoPlayer.getInstance().isLocalPlayer()) {
			GameClient.sendZombieHelmetFall(IsoPlayer.getInstance(), this, inventoryItem);
		}

		if (boolean2 && player != null && player.isLocalPlayer()) {
			LuaEventManager.triggerEvent("OnClothingUpdated", this);
		}

		if (boolean2 && this.isZombie()) {
			PersistentOutfits.instance.setFallenHat(this, true);
		}

		return boolean2;
	}

	public void smashCarWindow(VehiclePart vehiclePart) {
		HashMap hashMap = this.getStateMachineParams(SmashWindowState.instance());
		hashMap.clear();
		hashMap.put(0, vehiclePart.getWindow());
		hashMap.put(1, vehiclePart.getVehicle());
		hashMap.put(2, vehiclePart);
		this.actionContext.reportEvent("EventSmashWindow");
	}

	public void smashWindow(IsoWindow window) {
		if (!window.isInvincible()) {
			HashMap hashMap = this.getStateMachineParams(SmashWindowState.instance());
			hashMap.clear();
			hashMap.put(0, window);
			this.actionContext.reportEvent("EventSmashWindow");
		}
	}

	public void openWindow(IsoWindow window) {
		if (!window.isInvincible()) {
			OpenWindowState.instance().setParams(this, window);
			this.actionContext.reportEvent("EventOpenWindow");
		}
	}

	public void closeWindow(IsoWindow window) {
		if (!window.isInvincible()) {
			HashMap hashMap = this.getStateMachineParams(CloseWindowState.instance());
			hashMap.clear();
			hashMap.put(0, window);
			this.actionContext.reportEvent("EventCloseWindow");
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
			ClimbThroughWindowState.instance().setParams(this, window);
			this.actionContext.reportEvent("EventClimbWindow");
		}
	}

	public void climbThroughWindow(IsoWindow window, Integer integer) {
		if (window.canClimbThrough(this)) {
			ClimbThroughWindowState.instance().setParams(this, window);
			this.actionContext.reportEvent("EventClimbWindow");
		}
	}

	public boolean isClosingWindow(IsoWindow window) {
		if (window == null) {
			return false;
		} else if (!this.isCurrentState(CloseWindowState.instance())) {
			return false;
		} else {
			return CloseWindowState.instance().getWindow(this) == window;
		}
	}

	public boolean isClimbingThroughWindow(IsoWindow window) {
		if (window == null) {
			return false;
		} else if (!this.isCurrentState(ClimbThroughWindowState.instance())) {
			return false;
		} else if (!this.getVariableBoolean("BlockWindow")) {
			return false;
		} else {
			return ClimbThroughWindowState.instance().getWindow(this) == window;
		}
	}

	public void climbThroughWindowFrame(IsoObject object) {
		if (IsoWindowFrame.canClimbThrough(object, this)) {
			ClimbThroughWindowState.instance().setParams(this, object);
			this.actionContext.reportEvent("EventClimbWindow");
		}
	}

	public void climbSheetRope() {
		if (this.canClimbSheetRope(this.current)) {
			HashMap hashMap = this.getStateMachineParams(ClimbSheetRopeState.instance());
			hashMap.clear();
			this.actionContext.reportEvent("EventClimbRope");
		}
	}

	public void climbDownSheetRope() {
		if (this.canClimbDownSheetRope(this.current)) {
			this.dropHeavyItems();
			HashMap hashMap = this.getStateMachineParams(ClimbDownSheetRopeState.instance());
			hashMap.clear();
			this.actionContext.reportEvent("EventClimbDownRope");
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

	public boolean canClimbDownSheetRopeInCurrentSquare() {
		return this.canClimbDownSheetRope(this.current);
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

	public void climbThroughWindow(IsoThumpable thumpable) {
		if (thumpable.canClimbThrough(this)) {
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
			ClimbThroughWindowState.instance().setParams(this, thumpable);
			this.actionContext.reportEvent("EventClimbWindow");
		}
	}

	public void climbThroughWindow(IsoThumpable thumpable, Integer integer) {
		if (thumpable.canClimbThrough(this)) {
			ClimbThroughWindowState.instance().setParams(this, thumpable);
			this.actionContext.reportEvent("EventClimbWindow");
		}
	}

	public void climbOverFence(IsoDirections directions) {
		if (this.current != null) {
			IsoGridSquare square = this.current.nav[directions.index()];
			if (IsoWindow.canClimbThroughHelper(this, this.current, square, directions == IsoDirections.N || directions == IsoDirections.S)) {
				ClimbOverFenceState.instance().setParams(this, directions);
				this.actionContext.reportEvent("EventClimbFence");
			}
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

	public void preupdate() {
		super.preupdate();
		if (!this.m_bDebugVariablesRegistered && DebugOptions.instance.Character.Debug.RegisterDebugVariables.getValue()) {
			this.registerDebugGameVariables();
		}

		this.updateAnimationRecorderState();
		if (this.isAnimationRecorderActive()) {
			int int1 = IsoWorld.instance.getFrameNo();
			this.m_animationRecorder.beginLine(int1);
		}

		if (GameServer.bServer) {
			this.getXp().update();
		}
	}

	public void setTeleport(NetworkTeleport networkTeleport) {
		this.teleport = networkTeleport;
	}

	public NetworkTeleport getTeleport() {
		return this.teleport;
	}

	public boolean isTeleporting() {
		return this.teleport != null;
	}

	public void update() {
		IsoGameCharacter.s_performance.update.invokeAndMeasure(this, IsoGameCharacter::updateInternal);
	}

	private void updateInternal() {
		if (this.current != null) {
			if (this.teleport != null) {
				this.teleport.process(IsoPlayer.getPlayerIndex());
			}

			this.updateAlpha();
			if (this.isNPC) {
				if (this.GameCharacterAIBrain == null) {
					this.GameCharacterAIBrain = new GameCharacterAIBrain(this);
				}

				this.GameCharacterAIBrain.update();
			}

			if (this.sprite != null) {
				this.legsSprite = this.sprite;
			}

			if (!this.isDead() || this.current != null && this.current.getMovingObjects().contains(this)) {
				if (!GameClient.bClient && !this.m_invisible && this.getCurrentSquare().getTrapPositionX() > -1 && this.getCurrentSquare().getTrapPositionY() > -1 && this.getCurrentSquare().getTrapPositionZ() > -1) {
					this.getCurrentSquare().explodeTrap();
				}

				if (this.getBodyDamage() != null && this.getCurrentBuilding() != null && this.getCurrentBuilding().isToxic()) {
					float float1 = GameTime.getInstance().getMultiplier() / 1.6F;
					if (this.getStats().getFatigue() < 1.0F) {
						this.getStats().setFatigue(this.getStats().getFatigue() + 1.0E-4F * float1);
					}

					if ((double)this.getStats().getFatigue() > 0.8) {
						this.getBodyDamage().getBodyPart(BodyPartType.Head).ReduceHealth(0.1F * float1);
					}

					this.getBodyDamage().getBodyPart(BodyPartType.Torso_Upper).ReduceHealth(0.1F * float1);
				}

				if (this.lungeFallTimer > 0.0F) {
					this.lungeFallTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
				}

				if (this.getMeleeDelay() > 0.0F) {
					this.setMeleeDelay(this.getMeleeDelay() - 0.625F * GameTime.getInstance().getMultiplier());
				}

				if (this.getRecoilDelay() > 0.0F) {
					this.setRecoilDelay(this.getRecoilDelay() - 0.625F * GameTime.getInstance().getMultiplier());
				}

				this.sx = 0.0F;
				this.sy = 0.0F;
				if (this.current.getRoom() != null && this.current.getRoom().building.def.bAlarmed && (!this.isZombie() || Core.bTutorial) && !GameClient.bClient) {
					boolean boolean1 = false;
					if (this instanceof IsoPlayer && (((IsoPlayer)this).isInvisible() || ((IsoPlayer)this).isGhostMode())) {
						boolean1 = true;
					}

					if (!boolean1) {
						AmbientStreamManager.instance.doAlarm(this.current.getRoom().def);
					}
				}

				this.updateSeenVisibility();
				this.llx = this.getLx();
				this.lly = this.getLy();
				this.setLx(this.getX());
				this.setLy(this.getY());
				this.setLz(this.getZ());
				this.updateBeardAndHair();
				this.updateFalling();
				if (this.descriptor != null) {
					this.descriptor.Instance = this;
				}

				int int1;
				int int2;
				if (!this.isZombie()) {
					Stats stats;
					if (this.Traits.Agoraphobic.isSet() && !this.getCurrentSquare().isInARoom()) {
						stats = this.stats;
						stats.Panic += 0.5F * (GameTime.getInstance().getMultiplier() / 1.6F);
					}

					if (this.Traits.Claustophobic.isSet() && this.getCurrentSquare().isInARoom()) {
						int1 = this.getCurrentSquare().getRoomSize();
						if (int1 > 0) {
							float float2 = 1.0F;
							float2 = 1.0F - (float)int1 / 70.0F;
							if (float2 < 0.0F) {
								float2 = 0.0F;
							}

							float float3 = 0.6F * float2 * (GameTime.getInstance().getMultiplier() / 1.6F);
							if (float3 > 0.6F) {
								float3 = 0.6F;
							}

							stats = this.stats;
							stats.Panic += float3;
						}
					}

					if (this.Moodles != null) {
						this.Moodles.Update();
					}

					if (this.Asleep) {
						this.BetaEffect = 0.0F;
						this.SleepingTabletEffect = 0.0F;
						this.StopAllActionQueue();
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

					int1 = this.Moodles.getMoodleLevel(MoodleType.Panic);
					if (int1 == 2) {
						stats = this.stats;
						stats.Sanity -= 3.2E-7F;
					} else if (int1 == 3) {
						stats = this.stats;
						stats.Sanity -= 4.8000004E-7F;
					} else if (int1 == 4) {
						stats = this.stats;
						stats.Sanity -= 8.0E-7F;
					} else if (int1 == 0) {
						stats = this.stats;
						stats.Sanity += 1.0E-7F;
					}

					int2 = this.Moodles.getMoodleLevel(MoodleType.Tired);
					if (int2 == 4) {
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

				if (!this.CharacterActions.isEmpty()) {
					BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
					boolean boolean2 = baseAction.valid();
					if (boolean2 && !baseAction.bStarted) {
						baseAction.waitToStart();
					} else if (boolean2 && !baseAction.finished() && !baseAction.forceComplete && !baseAction.forceStop) {
						baseAction.update();
					}

					if (!boolean2 || baseAction.finished() || baseAction.forceComplete || baseAction.forceStop) {
						if (baseAction.finished() || baseAction.forceComplete) {
							baseAction.perform();
							boolean2 = true;
						}

						if (baseAction.finished() && !baseAction.loopAction || baseAction.forceComplete || baseAction.forceStop || !boolean2) {
							if (baseAction.bStarted && (baseAction.forceStop || !boolean2)) {
								baseAction.stop();
							}

							this.CharacterActions.removeElement(baseAction);
							if (this == IsoPlayer.players[0] || this == IsoPlayer.players[1] || this == IsoPlayer.players[2] || this == IsoPlayer.players[3]) {
								UIManager.getProgressBar((double)((IsoPlayer)this).getPlayerNum()).setValue(0.0F);
							}
						}
					}

					for (int int3 = 0; int3 < this.EnemyList.size(); ++int3) {
						IsoGameCharacter gameCharacter = (IsoGameCharacter)this.EnemyList.get(int3);
						if (gameCharacter.isDead()) {
							this.EnemyList.remove(gameCharacter);
							--int3;
						}
					}
				}

				if (SystemDisabler.doCharacterStats && this.BodyDamage != null) {
					this.BodyDamage.Update();
					this.updateBandages();
				}

				if (this == IsoPlayer.getInstance()) {
					if (this.leftHandItem != null && this.leftHandItem.getUses() <= 0) {
						this.leftHandItem = null;
					}

					if (this.rightHandItem != null && this.rightHandItem.getUses() <= 0) {
						this.rightHandItem = null;
					}
				}

				if (SystemDisabler.doCharacterStats) {
					this.calculateStats();
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

				this.stateMachine.update();
				if (this.isZombie() && VirtualZombieManager.instance.isReused((IsoZombie)this)) {
					DebugLog.log(DebugType.Zombie, "Zombie added to ReusableZombies after stateMachine.update - RETURNING " + this);
				} else {
					if (this instanceof IsoPlayer) {
						this.ensureOnTile();
					}

					if ((this instanceof IsoPlayer || this instanceof IsoSurvivor) && this.RemoteID == -1 && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
						RainManager.SetPlayerLocation(((IsoPlayer)this).getPlayerNum(), this.getCurrentSquare());
					}

					this.FireCheck();
					this.SpreadFire();
					this.ReduceHealthWhenBurning();
					this.updateTextObjects();
					if (this.stateMachine.getCurrent() == StaggerBackState.instance()) {
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

					if (!this.isZombie()) {
						this.recursiveItemUpdater(this.inventory);
					}

					this.LastZombieKills = this.ZombieKills;
					if (this.AttachedAnimSprite != null) {
						int1 = this.AttachedAnimSprite.size();
						for (int2 = 0; int2 < int1; ++int2) {
							IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
							IsoSprite sprite = spriteInstance.parentSprite;
							spriteInstance.update();
							spriteInstance.Frame += spriteInstance.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
							if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
								spriteInstance.Frame = 0.0F;
							}
						}
					}

					if (this.isGodMod()) {
						this.getStats().setFatigue(0.0F);
						this.getStats().setEndurance(1.0F);
						this.getBodyDamage().setTemperature(37.0F);
						this.getStats().setHunger(0.0F);
					}

					this.updateMovementMomentum();
					if (this.effectiveEdibleBuffTimer > 0.0F) {
						this.effectiveEdibleBuffTimer -= GameTime.getInstance().getMultiplier() * 0.015F;
						if (this.effectiveEdibleBuffTimer < 0.0F) {
							this.effectiveEdibleBuffTimer = 0.0F;
						}
					}

					if (!GameServer.bServer || GameClient.bClient) {
						this.updateDirt();
					}
				}
			}
		}
	}

	private void updateSeenVisibility() {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			this.updateSeenVisibility(int1);
		}
	}

	private void updateSeenVisibility(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		if (player != null) {
			this.IsVisibleToPlayer[int1] = this.TestIfSeen(int1);
			if (!this.IsVisibleToPlayer[int1]) {
				if (!(this instanceof IsoPlayer)) {
					if (!player.isSeeEveryone()) {
						this.setTargetAlpha(int1, 0.0F);
					}
				}
			}
		}
	}

	private void recursiveItemUpdater(ItemContainer itemContainer) {
		for (int int1 = 0; int1 < itemContainer.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)itemContainer.Items.get(int1);
			if (inventoryItem instanceof InventoryContainer) {
				this.recursiveItemUpdater((InventoryContainer)inventoryItem);
			}

			if (inventoryItem instanceof IUpdater) {
				inventoryItem.update();
			}
		}
	}

	private void recursiveItemUpdater(InventoryContainer inventoryContainer) {
		for (int int1 = 0; int1 < inventoryContainer.getInventory().getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)inventoryContainer.getInventory().getItems().get(int1);
			if (inventoryItem instanceof InventoryContainer) {
				this.recursiveItemUpdater((InventoryContainer)inventoryItem);
			}

			if (inventoryItem instanceof IUpdater) {
				inventoryItem.update();
			}
		}
	}

	private void updateDirt() {
		if (!this.isZombie() && this.getBodyDamage() != null) {
			int int1 = 0;
			if (this.isRunning() && Rand.NextBool(Rand.AdjustForFramerate(3500))) {
				int1 = 1;
			}

			if (this.isSprinting() && Rand.NextBool(Rand.AdjustForFramerate(2500))) {
				int1 += Rand.Next(1, 3);
			}

			if (this.getBodyDamage().getTemperature() > 37.0F && Rand.NextBool(Rand.AdjustForFramerate(5000))) {
				++int1;
			}

			if (this.getBodyDamage().getTemperature() > 38.0F && Rand.NextBool(Rand.AdjustForFramerate(3000))) {
				++int1;
			}

			float float1 = this.square == null ? 0.0F : this.square.getPuddlesInGround();
			if (this.isMoving() && float1 > 0.09F && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
				++int1;
			}

			if (int1 > 0) {
				this.addDirt((BloodBodyPartType)null, int1, true);
			}

			IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
			if (player != null && player.isPlayerMoving() || player == null && this.isMoving()) {
				int1 = 0;
				if (float1 > 0.09F && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
					++int1;
				}

				if (this.isInTrees() && Rand.NextBool(Rand.AdjustForFramerate(1500))) {
					++int1;
				}

				if (int1 > 0) {
					this.addDirt((BloodBodyPartType)null, int1, false);
				}
			}
		}
	}

	protected void updateMovementMomentum() {
		float float1 = GameTime.instance.getTimeDelta();
		float float2;
		float float3;
		float float4;
		if (this.isPlayerMoving() && !this.isAiming()) {
			float2 = this.m_momentumScalar * 0.55F;
			if (float2 >= 0.55F) {
				this.m_momentumScalar = 1.0F;
				return;
			}

			float3 = float2 + float1;
			float4 = float3 / 0.55F;
			this.m_momentumScalar = PZMath.clamp(float4, 0.0F, 1.0F);
		} else {
			float2 = (1.0F - this.m_momentumScalar) * 0.25F;
			if (float2 >= 0.25F) {
				this.m_momentumScalar = 0.0F;
				return;
			}

			float3 = float2 + float1;
			float4 = float3 / 0.25F;
			float float5 = PZMath.clamp(float4, 0.0F, 1.0F);
			this.m_momentumScalar = 1.0F - float5;
		}
	}

	public double getHoursSurvived() {
		return GameTime.instance.getWorldAgeHours();
	}

	private void updateBeardAndHair() {
		if (!this.isZombie()) {
			if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
				float float1 = (float)this.getHoursSurvived();
				if (this.beardGrowTiming < 0.0F || this.beardGrowTiming > float1) {
					this.beardGrowTiming = float1;
				}

				if (this.hairGrowTiming < 0.0F || this.hairGrowTiming > float1) {
					this.hairGrowTiming = float1;
				}

				boolean boolean1 = !GameClient.bClient && !GameServer.bServer || ServerOptions.instance.SleepAllowed.getValue() && ServerOptions.instance.SleepNeeded.getValue();
				boolean boolean2 = false;
				int int1;
				ArrayList arrayList;
				int int2;
				if ((this.isAsleep() || !boolean1) && float1 - this.beardGrowTiming > 120.0F) {
					this.beardGrowTiming = float1;
					BeardStyle beardStyle = BeardStyles.instance.FindStyle(((HumanVisual)this.getVisual()).getBeardModel());
					int1 = 1;
					if (beardStyle != null) {
						int1 = beardStyle.level;
					}

					arrayList = BeardStyles.instance.getAllStyles();
					for (int2 = 0; int2 < arrayList.size(); ++int2) {
						if (((BeardStyle)arrayList.get(int2)).growReference && ((BeardStyle)arrayList.get(int2)).level == int1 + 1) {
							((HumanVisual)this.getVisual()).setBeardModel(((BeardStyle)arrayList.get(int2)).name);
							boolean2 = true;
							break;
						}
					}
				}

				if ((this.isAsleep() || !boolean1) && float1 - this.hairGrowTiming > 480.0F) {
					this.hairGrowTiming = float1;
					HairStyle hairStyle = HairStyles.instance.FindMaleStyle(((HumanVisual)this.getVisual()).getHairModel());
					if (this.isFemale()) {
						hairStyle = HairStyles.instance.FindFemaleStyle(((HumanVisual)this.getVisual()).getHairModel());
					}

					int1 = 1;
					if (hairStyle != null) {
						int1 = hairStyle.level;
					}

					arrayList = HairStyles.instance.m_MaleStyles;
					if (this.isFemale()) {
						arrayList = HairStyles.instance.m_FemaleStyles;
					}

					for (int2 = 0; int2 < arrayList.size(); ++int2) {
						HairStyle hairStyle2 = (HairStyle)arrayList.get(int2);
						if (hairStyle2.growReference && hairStyle2.level == int1 + 1) {
							((HumanVisual)this.getVisual()).setHairModel(hairStyle2.name);
							((HumanVisual)this.getVisual()).setNonAttachedHair((String)null);
							boolean2 = true;
							break;
						}
					}
				}

				if (boolean2) {
					this.resetModelNextFrame();
					LuaEventManager.triggerEvent("OnClothingUpdated", this);
					if (GameClient.bClient) {
						GameClient.instance.sendVisual((IsoPlayer)this);
					}
				}
			}
		}
	}

	private void updateFalling() {
		if (this instanceof IsoPlayer && !this.isClimbing()) {
			IsoRoofFixer.FixRoofsAt(this.current);
		}

		if (this.isSeatedInVehicle()) {
			this.fallTime = 0.0F;
			this.lastFallSpeed = 0.0F;
			this.bFalling = false;
			this.wasOnStairs = false;
		} else {
			if (this.z > 0.0F) {
				IsoDirections directions = IsoDirections.Max;
				if (!this.isZombie() && this.isClimbing()) {
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

				float float1 = 0.125F * (GameTime.getInstance().getMultiplier() / 1.6F);
				if (this.bClimbing) {
					float1 = 0.0F;
				}

				if (this.getCurrentState() == ClimbOverFenceState.instance() || this.getCurrentState() == ClimbThroughWindowState.instance()) {
					this.fallTime = 0.0F;
					float1 = 0.0F;
				}

				this.lastFallSpeed = float1;
				float float2;
				float float3;
				if (!this.current.TreatAsSolidFloor()) {
					if (directions != IsoDirections.Max) {
						this.dir = directions;
					}

					float2 = 6.0F * (GameTime.getInstance().getMultiplier() / 1.6F);
					float3 = this.getHeightAboveFloor();
					if (float1 > float3) {
						float2 *= float3 / float1;
					}

					this.fallTime += float2;
					if (directions != IsoDirections.Max) {
						this.fallTime = 0.0F;
					}

					if (this.fallTime < 20.0F && float3 < 0.2F) {
						this.fallTime = 0.0F;
					}

					this.setZ(this.getZ() - float1);
				} else if (!(this.getZ() > (float)((int)this.getZ())) && !(float1 < 0.0F)) {
					this.DoLand();
					this.fallTime = 0.0F;
					this.bFalling = false;
				} else {
					if (directions != IsoDirections.Max) {
						this.dir = directions;
					}

					if (!this.current.HasStairs()) {
						if (!this.wasOnStairs) {
							float2 = 6.0F * (GameTime.getInstance().getMultiplier() / 1.6F);
							float3 = this.getHeightAboveFloor();
							if (float1 > float3) {
								float2 *= float3 / float1;
							}

							this.fallTime += float2;
							if (directions != IsoDirections.Max) {
								this.fallTime = 0.0F;
							}

							this.setZ(this.getZ() - float1);
							if (this.z < (float)((int)this.llz)) {
								this.z = (float)((int)this.llz);
								this.DoLand();
								this.fallTime = 0.0F;
								this.bFalling = false;
							}
						} else {
							this.wasOnStairs = false;
						}
					} else {
						this.fallTime = 0.0F;
						this.bFalling = false;
						this.wasOnStairs = true;
					}
				}
			} else {
				this.DoLand();
				this.fallTime = 0.0F;
				this.bFalling = false;
			}

			this.llz = this.lz;
		}
	}

	private float getHeightAboveFloor() {
		if (this.current == null) {
			return 1.0F;
		} else {
			if (this.current.HasStairs()) {
				float float1 = this.current.getApparentZ(this.x - (float)((int)this.x), this.y - (float)((int)this.y));
				if (this.getZ() >= float1) {
					return this.getZ() - float1;
				}
			}

			if (this.current.TreatAsSolidFloor()) {
				return this.getZ() - (float)((int)this.getZ());
			} else if (this.current.z == 0) {
				return this.getZ();
			} else {
				IsoGridSquare square = this.getCell().getGridSquare(this.current.x, this.current.y, this.current.z - 1);
				if (square != null && square.HasStairs()) {
					float float2 = square.getApparentZ(this.x - (float)((int)this.x), this.y - (float)((int)this.y));
					return this.getZ() - float2;
				} else {
					return 1.0F;
				}
			}
		}
	}

	protected void updateMovementRates() {
	}

	protected float calculateIdleSpeed() {
		float float1 = 0.01F;
		float1 = (float)((double)float1 + (double)this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 2.5 / 10.0);
		float1 *= GameTime.getAnimSpeedFix();
		return float1;
	}

	public float calculateBaseSpeed() {
		float float1 = 0.8F;
		float float2 = 1.0F;
		if (this.getMoodles() != null) {
			float1 -= (float)this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 0.15F;
			float1 -= (float)this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 0.15F;
		}

		int int1;
		if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 3 && this.Traits.AdrenalineJunkie.isSet()) {
			int1 = this.getMoodles().getMoodleLevel(MoodleType.Panic) + 1;
			float1 += (float)int1 / 20.0F;
		}

		for (int1 = BodyPartType.ToIndex(BodyPartType.Torso_Upper); int1 < BodyPartType.ToIndex(BodyPartType.Neck) + 1; ++int1) {
			BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			if (bodyPart.HasInjury()) {
				float1 -= 0.1F;
			}

			if (bodyPart.bandaged()) {
				float1 += 0.05F;
			}
		}

		BodyPart bodyPart2 = this.getBodyDamage().getBodyPart(BodyPartType.UpperLeg_L);
		if (bodyPart2.getAdditionalPain(true) > 20.0F) {
			float1 -= (bodyPart2.getAdditionalPain(true) - 20.0F) / 100.0F;
		}

		for (int int2 = 0; int2 < this.bagsWorn.size(); ++int2) {
			InventoryContainer inventoryContainer = (InventoryContainer)this.bagsWorn.get(int2);
			float2 += this.calcRunSpeedModByBag(inventoryContainer);
		}

		if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof InventoryContainer) {
			float2 += this.calcRunSpeedModByBag((InventoryContainer)this.getPrimaryHandItem());
		}

		if (this.getSecondaryHandItem() != null && this.getSecondaryHandItem() instanceof InventoryContainer) {
			float2 += this.calcRunSpeedModByBag((InventoryContainer)this.getSecondaryHandItem());
		}

		this.fullSpeedMod = this.runSpeedModifier + (float2 - 1.0F);
		return float1 * (1.0F - Math.abs(1.0F - this.fullSpeedMod) / 2.0F);
	}

	private float calcRunSpeedModByClothing() {
		float float1 = 0.0F;
		int int1 = 0;
		for (int int2 = 0; int2 < this.wornItems.size(); ++int2) {
			InventoryItem inventoryItem = this.wornItems.getItemByIndex(int2);
			if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getRunSpeedModifier() != 1.0F) {
				float1 += ((Clothing)inventoryItem).getRunSpeedModifier();
				++int1;
			}
		}

		if (float1 == 0.0F && int1 == 0) {
			float1 = 1.0F;
			int1 = 1;
		}

		if (this.getWornItem("Shoes") == null) {
			float1 *= 0.8F;
		}

		return float1 / (float)int1;
	}

	private float calcRunSpeedModByBag(InventoryContainer inventoryContainer) {
		float float1 = inventoryContainer.getScriptItem().runSpeedModifier - 1.0F;
		float float2 = inventoryContainer.getContentsWeight() / (float)inventoryContainer.getEffectiveCapacity(this);
		float1 *= 1.0F + float2 / 2.0F;
		return float1;
	}

	protected float calculateCombatSpeed() {
		boolean boolean1 = true;
		float float1 = 1.0F;
		HandWeapon handWeapon = null;
		if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
			handWeapon = (HandWeapon)this.getPrimaryHandItem();
			float1 *= ((HandWeapon)this.getPrimaryHandItem()).getBaseSpeed();
		}

		WeaponType weaponType = WeaponType.getWeaponType(this);
		if (handWeapon != null && handWeapon.isTwoHandWeapon() && this.getSecondaryHandItem() != handWeapon) {
			float1 *= 0.77F;
		}

		if (handWeapon != null && this.Traits.Axeman.isSet() && handWeapon.getCategories().contains("Axe")) {
			float1 *= this.getChopTreeSpeed();
			boolean1 = false;
		}

		float1 -= (float)this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 0.07F;
		float1 -= (float)this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 0.07F;
		float1 += (float)this.getWeaponLevel() * 0.03F;
		float1 += (float)this.getPerkLevel(PerkFactory.Perks.Fitness) * 0.02F;
		if (this.getSecondaryHandItem() != null && this.getSecondaryHandItem() instanceof InventoryContainer) {
			float1 *= 0.95F;
		}

		float1 *= Rand.Next(1.1F, 1.2F);
		float1 *= this.combatSpeedModifier;
		float1 *= this.getArmsInjurySpeedModifier();
		if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
			float1 *= this.getBodyDamage().getThermoregulator().getCombatModifier();
		}

		float1 = Math.min(1.6F, float1);
		float1 = Math.max(0.8F, float1);
		if (handWeapon != null && handWeapon.isTwoHandWeapon() && weaponType.type.equalsIgnoreCase("heavy")) {
			float1 *= 1.2F;
		}

		return float1 * (boolean1 ? GameTime.getAnimSpeedFix() : 1.0F);
	}

	private float getArmsInjurySpeedModifier() {
		float float1 = 1.0F;
		float float2 = 0.0F;
		BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Hand_R);
		float2 = this.calculateInjurySpeed(bodyPart, true);
		if (float2 > 0.0F) {
			float1 -= float2;
		}

		bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.ForeArm_R);
		float2 = this.calculateInjurySpeed(bodyPart, true);
		if (float2 > 0.0F) {
			float1 -= float2;
		}

		bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.UpperArm_R);
		float2 = this.calculateInjurySpeed(bodyPart, true);
		if (float2 > 0.0F) {
			float1 -= float2;
		}

		return float1;
	}

	private float getFootInjurySpeedModifier() {
		float float1 = 0.0F;
		boolean boolean1 = true;
		float float2 = 0.0F;
		float float3 = 0.0F;
		for (int int1 = BodyPartType.ToIndex(BodyPartType.Groin); int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
			float1 = this.calculateInjurySpeed(this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)), false);
			if (boolean1) {
				float2 += float1;
			} else {
				float3 += float1;
			}

			boolean1 = !boolean1;
		}

		if (float2 > float3) {
			return -(float2 + float3);
		} else {
			return float2 + float3;
		}
	}

	private float calculateInjurySpeed(BodyPart bodyPart, boolean boolean1) {
		float float1 = bodyPart.getScratchSpeedModifier();
		float float2 = bodyPart.getCutSpeedModifier();
		float float3 = bodyPart.getBurnSpeedModifier();
		float float4 = bodyPart.getDeepWoundSpeedModifier();
		float float5 = 0.0F;
		if ((bodyPart.getType() == BodyPartType.Foot_L || bodyPart.getType() == BodyPartType.Foot_R) && (bodyPart.getBurnTime() > 5.0F || bodyPart.getBiteTime() > 0.0F || bodyPart.deepWounded() || bodyPart.isSplint() || bodyPart.getFractureTime() > 0.0F || bodyPart.haveGlass())) {
			float5 = 1.0F;
			if (bodyPart.bandaged()) {
				float5 = 0.7F;
			}

			if (bodyPart.getFractureTime() > 0.0F) {
				float5 = this.calcFractureInjurySpeed(bodyPart);
			}
		}

		if (bodyPart.haveBullet()) {
			return 1.0F;
		} else {
			if (bodyPart.getScratchTime() > 2.0F || bodyPart.getCutTime() > 5.0F || bodyPart.getBurnTime() > 0.0F || bodyPart.getDeepWoundTime() > 0.0F || bodyPart.isSplint() || bodyPart.getFractureTime() > 0.0F || bodyPart.getBiteTime() > 0.0F) {
				float5 += bodyPart.getScratchTime() / float1 + bodyPart.getCutTime() / float2 + bodyPart.getBurnTime() / float3 + bodyPart.getDeepWoundTime() / float4;
				float5 += bodyPart.getBiteTime() / 20.0F;
				if (bodyPart.bandaged()) {
					float5 /= 2.0F;
				}

				if (bodyPart.getFractureTime() > 0.0F) {
					float5 = this.calcFractureInjurySpeed(bodyPart);
				}
			}

			if (boolean1 && bodyPart.getPain() > 20.0F) {
				float5 += bodyPart.getPain() / 10.0F;
			}

			return float5;
		}
	}

	private float calcFractureInjurySpeed(BodyPart bodyPart) {
		float float1 = 0.4F;
		if (bodyPart.getFractureTime() > 10.0F) {
			float1 = 0.7F;
		}

		if (bodyPart.getFractureTime() > 20.0F) {
			float1 = 1.0F;
		}

		if (bodyPart.getSplintFactor() > 0.0F) {
			float1 -= 0.2F;
			float1 -= Math.min(bodyPart.getSplintFactor() / 10.0F, 0.8F);
		}

		return Math.max(0.0F, float1);
	}

	protected void calculateWalkSpeed() {
		if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
			float float1 = 0.0F;
			float float2 = this.getFootInjurySpeedModifier();
			this.setVariable("WalkInjury", float2);
			float1 = this.calculateBaseSpeed();
			if (!this.bRunning && !this.bSprinting) {
				float1 *= this.walkSpeedModifier;
			} else {
				float1 -= 0.15F;
				float1 *= this.fullSpeedMod;
				float1 += (float)this.getPerkLevel(PerkFactory.Perks.Sprinting) / 20.0F;
				float1 = (float)((double)float1 - Math.abs((double)float2 / 1.5));
				if ("Tutorial".equals(Core.GameMode)) {
					float1 = Math.max(1.0F, float1);
				}
			}

			if (this.getSlowFactor() > 0.0F) {
				float1 *= 0.05F;
			}

			float1 = Math.min(1.0F, float1);
			if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
				float1 *= this.getBodyDamage().getThermoregulator().getMovementModifier();
			}

			if (this.isAiming()) {
				float float3 = Math.min(0.9F + (float)this.getPerkLevel(PerkFactory.Perks.Nimble) / 10.0F, 1.5F);
				float float4 = Math.min(float1 * 2.5F, 1.0F);
				float3 *= float4;
				float3 = Math.max(float3, 0.6F);
				this.setVariable("StrafeSpeed", float3 * GameTime.getAnimSpeedFix());
			}

			if (this.isInTreesNoBush()) {
				IsoGridSquare square = this.getCurrentSquare();
				if (square != null && square.Has(IsoObjectType.tree)) {
					IsoTree tree = square.getTree();
					if (tree != null) {
						float1 *= tree.getSlowFactor(this);
					}
				}
			}

			this.setVariable("WalkSpeed", float1 * GameTime.getAnimSpeedFix());
		}
	}

	public void updateSpeedModifiers() {
		this.runSpeedModifier = 1.0F;
		this.walkSpeedModifier = 1.0F;
		this.combatSpeedModifier = 1.0F;
		this.bagsWorn = new ArrayList();
		for (int int1 = 0; int1 < this.getWornItems().size(); ++int1) {
			InventoryItem inventoryItem = this.getWornItems().getItemByIndex(int1);
			if (inventoryItem instanceof Clothing) {
				Clothing clothing = (Clothing)inventoryItem;
				this.combatSpeedModifier += clothing.getCombatSpeedModifier() - 1.0F;
			}

			if (inventoryItem instanceof InventoryContainer) {
				InventoryContainer inventoryContainer = (InventoryContainer)inventoryItem;
				this.combatSpeedModifier += inventoryContainer.getScriptItem().combatSpeedModifier - 1.0F;
				this.bagsWorn.add(inventoryContainer);
			}
		}

		InventoryItem inventoryItem2 = this.getWornItems().getItem("Shoes");
		if (inventoryItem2 == null || inventoryItem2.getCondition() == 0) {
			this.runSpeedModifier *= 0.85F;
			this.walkSpeedModifier *= 0.85F;
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
				IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 0);
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
				((IsoSpriteInstance)object.AttachedAnimSprite.get(object.AttachedAnimSprite.size() - 1)).offX = 0.0F;
			}
		}
	}

	void DoSplat(IsoGridSquare square, String string, boolean boolean1, IsoFlagType flagType, float float1, float float2, float float3) {
		if (square != null) {
			square.DoSplat(string, boolean1, flagType, float1, float2, float3);
		}
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoCamera.CamCharacter != IsoPlayer.getInstance() && Core.bDebug) {
			IsoCamera.CamCharacter = this;
		}

		return super.onMouseLeftClick(int1, int2);
	}

	protected void calculateStats() {
		if (GameServer.bServer) {
			this.stats.fatigue = 0.0F;
		} else if (GameClient.bClient && (!ServerOptions.instance.SleepAllowed.getValue() || !ServerOptions.instance.SleepNeeded.getValue())) {
			this.stats.fatigue = 0.0F;
		}

		if (!LuaHookManager.TriggerHook("CalculateStats", this)) {
			this.updateEndurance();
			this.updateTripping();
			this.updateThirst();
			this.updateStress();
			this.updateStats_WakeState();
			this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
			this.stats.hunger = PZMath.clamp(this.stats.hunger, 0.0F, 1.0F);
			this.stats.stress = PZMath.clamp(this.stats.stress, 0.0F, 1.0F);
			this.stats.fatigue = PZMath.clamp(this.stats.fatigue, 0.0F, 1.0F);
			this.updateMorale();
			this.updateFitness();
		}
	}

	protected void updateStats_WakeState() {
		if (IsoPlayer.getInstance() == this && this.Asleep) {
			this.updateStats_Sleeping();
		} else {
			this.updateStats_Awake();
		}
	}

	protected void updateStats_Sleeping() {
	}

	protected void updateStats_Awake() {
		Stats stats = this.stats;
		stats.stress = (float)((double)stats.stress - ZomboidGlobals.StressReduction * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
		float float1 = 1.0F - this.stats.endurance;
		if (float1 < 0.3F) {
			float1 = 0.3F;
		}

		float float2 = 1.0F;
		if (this.Traits.NeedsLessSleep.isSet()) {
			float2 = 0.7F;
		}

		if (this.Traits.NeedsMoreSleep.isSet()) {
			float2 = 1.3F;
		}

		double double1 = SandboxOptions.instance.getStatsDecreaseMultiplier();
		if (double1 < 1.0) {
			double1 = 1.0;
		}

		stats = this.stats;
		stats.fatigue = (float)((double)stats.fatigue + ZomboidGlobals.FatigueIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float1 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)float2 * this.getFatiqueMultiplier());
		float float3 = this.getAppetiteMultiplier();
		if ((!(this instanceof IsoPlayer) || !((IsoPlayer)this).IsRunning() || !this.isPlayerMoving()) && !this.isCurrentState(SwipeStatePlayer.instance())) {
			if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
				stats = this.stats;
				stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float3 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
			} else {
				stats = this.stats;
				stats.hunger = (float)((double)stats.hunger + (double)((float)ZomboidGlobals.HungerIncreaseWhenWellFed) * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
			}
		} else if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
			stats = this.stats;
			stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncreaseWhenExercise / 3.0 * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float3 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
		} else {
			stats = this.stats;
			stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncreaseWhenExercise * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float3 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
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

	private void updateMorale() {
		float float1 = 1.0F - this.stats.getStress() - 0.5F;
		float1 *= 1.0E-4F;
		if (float1 > 0.0F) {
			float1 += 0.5F;
		}

		Stats stats = this.stats;
		stats.morale += float1;
		this.stats.morale = PZMath.clamp(this.stats.morale, 0.0F, 1.0F);
	}

	private void updateFitness() {
		this.stats.fitness = (float)this.getPerkLevel(PerkFactory.Perks.Fitness) / 5.0F - 1.0F;
		if (this.stats.fitness > 1.0F) {
			this.stats.fitness = 1.0F;
		}

		if (this.stats.fitness < -1.0F) {
			this.stats.fitness = -1.0F;
		}
	}

	private void updateTripping() {
		Stats stats;
		if (this.stats.Tripping) {
			stats = this.stats;
			stats.TrippingRotAngle += 0.06F;
		} else {
			stats = this.stats;
			stats.TrippingRotAngle += 0.0F;
		}
	}

	protected float getAppetiteMultiplier() {
		float float1 = 1.0F - this.stats.hunger;
		if (this.Traits.HeartyAppitite.isSet()) {
			float1 *= 1.5F;
		}

		if (this.Traits.LightEater.isSet()) {
			float1 *= 0.75F;
		}

		return float1;
	}

	private void updateStress() {
		float float1 = 1.0F;
		if (this.Traits.Cowardly.isSet()) {
			float1 = 2.0F;
		}

		if (this.Traits.Brave.isSet()) {
			float1 = 0.3F;
		}

		if (this.stats.Panic > 100.0F) {
			this.stats.Panic = 100.0F;
		}

		Stats stats = this.stats;
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

		if (this.Traits.Hemophobic.isSet()) {
			stats = this.stats;
			stats.stress = (float)((double)stats.stress + (double)this.getTotalBlood() * ZomboidGlobals.StressFromHemophobic * (double)(GameTime.instance.getMultiplier() / 0.8F) * (double)GameTime.instance.getDeltaMinutesPerDay());
		}

		if (this.Traits.Brooding.isSet()) {
			stats = this.stats;
			stats.Anger = (float)((double)stats.Anger - ZomboidGlobals.AngerDecrease * ZomboidGlobals.BroodingAngerDecreaseMultiplier * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
		} else {
			stats = this.stats;
			stats.Anger = (float)((double)stats.Anger - ZomboidGlobals.AngerDecrease * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
		}

		this.stats.Anger = PZMath.clamp(this.stats.Anger, 0.0F, 1.0F);
	}

	private void updateEndurance() {
		this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
		this.stats.endurancelast = this.stats.endurance;
		if (this.isUnlimitedEndurance()) {
			this.stats.endurance = 1.0F;
		}
	}

	private void updateThirst() {
		float float1 = 1.0F;
		if (this.Traits.HighThirst.isSet()) {
			float1 = (float)((double)float1 * 2.0);
		}

		if (this.Traits.LowThirst.isSet()) {
			float1 = (float)((double)float1 * 0.5);
		}

		if (IsoPlayer.getInstance() == this && !IsoPlayer.getInstance().isGhostMode()) {
			Stats stats;
			if (this.Asleep) {
				stats = this.stats;
				stats.thirst = (float)((double)stats.thirst + ZomboidGlobals.ThirstSleepingIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)float1);
			} else {
				stats = this.stats;
				stats.thirst = (float)((double)stats.thirst + ZomboidGlobals.ThirstIncrease * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * this.getRunningThirstReduction() * (double)GameTime.instance.getDeltaMinutesPerDay() * (double)float1 * this.getThirstMultiplier());
			}

			if (this.stats.thirst > 1.0F) {
				this.stats.thirst = 1.0F;
			}
		}

		this.autoDrink();
	}

	private double getRunningThirstReduction() {
		return this == IsoPlayer.getInstance() && IsoPlayer.getInstance().IsRunning() ? 1.2 : 1.0;
	}

	public void faceLocation(float float1, float float2) {
		tempo.x = float1 + 0.5F;
		tempo.y = float2 + 0.5F;
		Vector2 vector2 = tempo;
		vector2.x -= this.getX();
		vector2 = tempo;
		vector2.y -= this.getY();
		this.DirectionFromVector(tempo);
		this.getVectorFromDirection(this.m_forwardDirection);
		AnimationPlayer animationPlayer = this.getAnimationPlayer();
		if (animationPlayer != null && animationPlayer.isReady()) {
			animationPlayer.UpdateDir(this);
		}
	}

	public void faceLocationF(float float1, float float2) {
		tempo.x = float1;
		tempo.y = float2;
		Vector2 vector2 = tempo;
		vector2.x -= this.getX();
		vector2 = tempo;
		vector2.y -= this.getY();
		if (tempo.getLengthSquared() != 0.0F) {
			this.DirectionFromVector(tempo);
			tempo.normalize();
			this.m_forwardDirection.set(tempo.x, tempo.y);
			AnimationPlayer animationPlayer = this.getAnimationPlayer();
			if (animationPlayer != null && animationPlayer.isReady()) {
				animationPlayer.UpdateDir(this);
			}
		}
	}

	public boolean isFacingLocation(float float1, float float2, float float3) {
		Vector2 vector2 = BaseVehicle.allocVector2().set(float1 - this.getX(), float2 - this.getY());
		vector2.normalize();
		Vector2 vector22 = this.getLookVector(BaseVehicle.allocVector2());
		float float4 = vector2.dot(vector22);
		BaseVehicle.releaseVector2(vector2);
		BaseVehicle.releaseVector2(vector22);
		return float4 >= float3;
	}

	public boolean isFacingObject(IsoObject object, float float1) {
		Vector2 vector2 = BaseVehicle.allocVector2();
		object.getFacingPosition(vector2);
		boolean boolean1 = this.isFacingLocation(vector2.x, vector2.y, float1);
		BaseVehicle.releaseVector2(vector2);
		return boolean1;
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

	public boolean isOutside() {
		return this.getCurrentSquare() == null ? false : this.getCurrentSquare().isOutside();
	}

	public boolean isFemale() {
		return this.bFemale;
	}

	public void setFemale(boolean boolean1) {
		this.bFemale = boolean1;
	}

	public boolean isZombie() {
		return false;
	}

	public int getLastHitCount() {
		return this.lastHitCount;
	}

	public void setLastHitCount(int int1) {
		this.lastHitCount = int1;
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
		if (this.Traits.PlaysFootball.isSet()) {
			float1 *= 0.9F;
		}

		if (this.Traits.Jogger.isSet()) {
			float1 *= 0.9F;
		}

		Stats stats = this.stats;
		stats.endurance -= float1;
	}

	public IsoGameCharacter.PerkInfo getPerkInfo(PerkFactory.Perk perk) {
		for (int int1 = 0; int1 < this.PerkList.size(); ++int1) {
			IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)this.PerkList.get(int1);
			if (perkInfo.perk == perk) {
				return perkInfo;
			}
		}

		return null;
	}

	public boolean isEquipped(InventoryItem inventoryItem) {
		return this.isEquippedClothing(inventoryItem) || this.isHandItem(inventoryItem);
	}

	public boolean isEquippedClothing(InventoryItem inventoryItem) {
		return this.wornItems.contains(inventoryItem);
	}

	public boolean isAttachedItem(InventoryItem inventoryItem) {
		return this.getAttachedItems().contains(inventoryItem);
	}

	public void faceThisObject(IsoObject object) {
		if (object != null) {
			Vector2 vector2 = tempo;
			BaseVehicle baseVehicle = (BaseVehicle)Type.tryCastTo(object, BaseVehicle.class);
			BarricadeAble barricadeAble = (BarricadeAble)Type.tryCastTo(object, BarricadeAble.class);
			if (baseVehicle != null) {
				baseVehicle.getFacingPosition(this, vector2);
				vector2.x -= this.getX();
				vector2.y -= this.getY();
				this.DirectionFromVector(vector2);
				vector2.normalize();
				this.m_forwardDirection.set(vector2.x, vector2.y);
			} else if (barricadeAble != null && this.current == barricadeAble.getSquare()) {
				this.dir = barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W;
				this.getVectorFromDirection(this.m_forwardDirection);
			} else if (barricadeAble != null && this.current == barricadeAble.getOppositeSquare()) {
				this.dir = barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E;
				this.getVectorFromDirection(this.m_forwardDirection);
			} else {
				object.getFacingPosition(vector2);
				vector2.x -= this.getX();
				vector2.y -= this.getY();
				this.DirectionFromVector(vector2);
				this.getVectorFromDirection(this.m_forwardDirection);
			}

			AnimationPlayer animationPlayer = this.getAnimationPlayer();
			if (animationPlayer != null && animationPlayer.isReady()) {
				animationPlayer.UpdateDir(this);
			}
		}
	}

	public void facePosition(int int1, int int2) {
		tempo.x = (float)int1;
		tempo.y = (float)int2;
		Vector2 vector2 = tempo;
		vector2.x -= this.getX();
		vector2 = tempo;
		vector2.y -= this.getY();
		this.DirectionFromVector(tempo);
		this.getVectorFromDirection(this.m_forwardDirection);
		AnimationPlayer animationPlayer = this.getAnimationPlayer();
		if (animationPlayer != null && animationPlayer.isReady()) {
			animationPlayer.UpdateDir(this);
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
			this.getVectorFromDirection(this.m_forwardDirection);
			AnimationPlayer animationPlayer = this.getAnimationPlayer();
			if (animationPlayer != null && animationPlayer.isReady()) {
				animationPlayer.UpdateDir(this);
			}
		}
	}

	public void setAnimated(boolean boolean1) {
		this.legsSprite.Animate = true;
	}

	public void playHurtSound() {
		this.getEmitter().playVocals(this.getHurtSound());
	}

	public void playDeadSound() {
		if (this.isCloseKilled()) {
			this.getEmitter().playSoundImpl("HeadStab", this);
		} else {
			this.getEmitter().playSoundImpl("HeadSmash", this);
		}

		if (this.isZombie()) {
			((IsoZombie)this).parameterZombieState.setState(ParameterZombieState.State.Death);
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		super.saveChange(string, kahluaTable, byteBuffer);
		if ("addItem".equals(string)) {
			if (kahluaTable != null && kahluaTable.rawget("item") instanceof InventoryItem) {
				InventoryItem inventoryItem = (InventoryItem)kahluaTable.rawget("item");
				try {
					inventoryItem.saveWithSize(byteBuffer, false);
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
					byteBuffer.putInt(((Double)kahluaTable.rawget("id")).intValue());
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
		if ("addItem".equals(string)) {
			try {
				InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
				if (inventoryItem != null) {
					this.getInventory().AddItem(inventoryItem);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			short short1;
			int int1;
			String string2;
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
					this.getBodyDamage().AddRandomDamageFromZombie(zombie, (String)null);
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
				} else {
					int int2;
					if ("removeItem".equals(string)) {
						int2 = byteBuffer.getInt();
						if (int2 >= 0 && int2 < this.getInventory().getItems().size()) {
							inventoryItem2 = (InventoryItem)this.getInventory().getItems().get(int2);
							this.removeFromHands(inventoryItem2);
							this.getInventory().Remove(inventoryItem2);
						}
					} else if ("removeItemID".equals(string)) {
						int2 = byteBuffer.getInt();
						String string3 = GameWindow.ReadStringUTF(byteBuffer);
						InventoryItem inventoryItem3 = this.getInventory().getItemWithID(int2);
						if (inventoryItem3 != null && inventoryItem3.getFullType().equals(string3)) {
							this.removeFromHands(inventoryItem3);
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
					} else if (!"Shove".equals(string)) {
						if ("StopBurning".equals(string)) {
							this.StopBurning();
						} else {
							int int3;
							if ("addXp".equals(string)) {
								PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
								int3 = byteBuffer.getInt();
								boolean boolean1 = byteBuffer.get() == 1;
								if (boolean1) {
									this.getXp().AddXPNoMultiplier(perk, (float)int3);
								} else {
									this.getXp().AddXP(perk, (float)int3);
								}
							} else if ("wakeUp".equals(string)) {
								if (this.isAsleep()) {
									this.Asleep = false;
									this.ForceWakeUpTime = -1.0F;
									TutorialManager.instance.StealControl = false;
									if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
										UIManager.setFadeBeforeUI(((IsoPlayer)this).getPlayerNum(), true);
										UIManager.FadeIn((double)((IsoPlayer)this).getPlayerNum(), 2.0);
										GameClient.instance.sendPlayer((IsoPlayer)this);
									}
								}
							} else if ("mechanicActionDone".equals(string)) {
								boolean boolean2 = byteBuffer.get() == 1;
								int3 = byteBuffer.getInt();
								String string4 = GameWindow.ReadString(byteBuffer);
								boolean boolean3 = byteBuffer.get() == 1;
								long long1 = byteBuffer.getLong();
								LuaEventManager.triggerEvent("OnMechanicActionDone", this, boolean2, int3, string4, long1, boolean3);
							} else if ("vehicleNoKey".equals(string)) {
								this.SayDebug(" [img=media/ui/CarKey_none.png]");
							}
						}
					}
				}
			}
		}
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
		if (GameServer.bServer) {
			if (!this.isZombie()) {
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
					this.lightInfo.angleX = this.getForwardDirection().getX();
					this.lightInfo.angleY = this.getForwardDirection().getY();
					this.lightInfo.torches.clear();
					this.lightInfo.night = GameTime.getInstance().getNight();
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
		IsoGameCharacter.s_performance.postUpdate.invokeAndMeasure(this, IsoGameCharacter::postUpdateInternal);
	}

	private void postUpdateInternal() {
		super.postupdate();
		AnimationPlayer animationPlayer = this.getAnimationPlayer();
		animationPlayer.UpdateDir(this);
		boolean boolean1 = this.shouldBeTurning();
		this.setTurning(boolean1);
		boolean boolean2 = this.shouldBeTurning90();
		this.setTurning90(boolean2);
		boolean boolean3 = this.shouldBeTurningAround();
		this.setTurningAround(boolean3);
		this.actionContext.update();
		if (this.getCurrentSquare() != null) {
			this.advancedAnimator.update();
		}

		this.actionContext.clearEvent("ActiveAnimFinished");
		this.actionContext.clearEvent("ActiveAnimFinishing");
		this.actionContext.clearEvent("ActiveAnimLooped");
		animationPlayer = this.getAnimationPlayer();
		if (animationPlayer != null) {
			MoveDeltaModifiers moveDeltaModifiers = IsoGameCharacter.L_postUpdate.moveDeltas;
			moveDeltaModifiers.moveDelta = this.getMoveDelta();
			moveDeltaModifiers.turnDelta = this.getTurnDelta();
			boolean2 = this.hasPath();
			boolean3 = this instanceof IsoPlayer;
			if (boolean3 && boolean2 && this.isRunning()) {
				moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 2.0F);
			}

			State state = this.getCurrentState();
			if (state != null) {
				state.getDeltaModifiers(this, moveDeltaModifiers);
			}

			if (moveDeltaModifiers.twistDelta == -1.0F) {
				moveDeltaModifiers.twistDelta = moveDeltaModifiers.turnDelta * 1.8F;
			}

			if (!this.isTurning()) {
				moveDeltaModifiers.turnDelta = 0.0F;
			}

			float float1 = Math.max(1.0F - moveDeltaModifiers.moveDelta / 2.0F, 0.0F);
			animationPlayer.angleStepDelta = float1 * moveDeltaModifiers.turnDelta;
			animationPlayer.angleTwistDelta = float1 * moveDeltaModifiers.twistDelta;
			animationPlayer.setMaxTwistAngle(0.017453292F * this.getMaxTwist());
		}

		if (this.hasActiveModel()) {
			try {
				ModelManager.ModelSlot modelSlot = this.legsSprite.modelSlot;
				modelSlot.Update();
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		} else {
			animationPlayer = this.getAnimationPlayer();
			animationPlayer.bUpdateBones = false;
			boolean1 = PerformanceSettings.InterpolateAnims;
			PerformanceSettings.InterpolateAnims = false;
			try {
				animationPlayer.UpdateDir(this);
				animationPlayer.Update();
			} catch (Throwable throwable2) {
				ExceptionLogger.logException(throwable2);
			} finally {
				animationPlayer.bUpdateBones = true;
				PerformanceSettings.InterpolateAnims = boolean1;
			}
		}

		this.updateLightInfo();
		if (this.isAnimationRecorderActive()) {
			this.m_animationRecorder.logVariables(this);
			this.m_animationRecorder.endLine();
		}
	}

	public boolean shouldBeTurning() {
		float float1 = this.getTargetTwist();
		float float2 = PZMath.abs(float1);
		boolean boolean1 = float2 > 1.0F;
		if (this.isZombie() && this.getCurrentState() == ZombieFallDownState.instance()) {
			return false;
		} else if (this.blockTurning) {
			return false;
		} else if (this.isBehaviourMoving()) {
			return boolean1;
		} else if (this.isPlayerMoving()) {
			return boolean1;
		} else if (this.isAttacking()) {
			return !this.bAimAtFloor;
		} else {
			float float3 = this.getAbsoluteExcessTwist();
			if (float3 > 1.0F) {
				return true;
			} else {
				return this.isTurning() ? boolean1 : false;
			}
		}
	}

	public boolean shouldBeTurning90() {
		if (!this.isTurning()) {
			return false;
		} else if (this.isTurning90()) {
			return true;
		} else {
			float float1 = this.getTargetTwist();
			float float2 = Math.abs(float1);
			return float2 > 65.0F;
		}
	}

	public boolean shouldBeTurningAround() {
		if (!this.isTurning()) {
			return false;
		} else if (this.isTurningAround()) {
			return true;
		} else {
			float float1 = this.getTargetTwist();
			float float2 = Math.abs(float1);
			return float2 > 110.0F;
		}
	}

	private boolean isTurning() {
		return this.m_isTurning;
	}

	private void setTurning(boolean boolean1) {
		this.m_isTurning = boolean1;
	}

	private boolean isTurningAround() {
		return this.m_isTurningAround;
	}

	private void setTurningAround(boolean boolean1) {
		this.m_isTurningAround = boolean1;
	}

	private boolean isTurning90() {
		return this.m_isTurning90;
	}

	private void setTurning90(boolean boolean1) {
		this.m_isTurning90 = boolean1;
	}

	public boolean hasPath() {
		return this.getPath2() != null;
	}

	public boolean isAnimationRecorderActive() {
		return this.m_animationRecorder != null && this.m_animationRecorder.isRecording();
	}

	public AnimationPlayerRecorder getAnimationPlayerRecorder() {
		return this.m_animationRecorder;
	}

	public float getMeleeDelay() {
		return this.meleeDelay;
	}

	public void setMeleeDelay(float float1) {
		this.meleeDelay = Math.max(float1, 0.0F);
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
		int int1 = this.getWeaponLevel();
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

	public int getWeaponLevel() {
		WeaponType weaponType = WeaponType.getWeaponType(this);
		int int1 = -1;
		if (weaponType != null && weaponType != WeaponType.barehand) {
			if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Axe")) {
				int1 = this.getPerkLevel(PerkFactory.Perks.Axe);
			}

			if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Spear")) {
				int1 += this.getPerkLevel(PerkFactory.Perks.Spear);
			}

			if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("SmallBlade")) {
				int1 += this.getPerkLevel(PerkFactory.Perks.SmallBlade);
			}

			if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("LongBlade")) {
				int1 += this.getPerkLevel(PerkFactory.Perks.LongBlade);
			}

			if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("Blunt")) {
				int1 += this.getPerkLevel(PerkFactory.Perks.Blunt);
			}

			if (((HandWeapon)this.getPrimaryHandItem()).getCategories().contains("SmallBlunt")) {
				int1 += this.getPerkLevel(PerkFactory.Perks.SmallBlunt);
			}
		}

		return int1 == -1 ? 0 : int1;
	}

	public int getMaintenanceMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Maintenance);
		int1 += this.getWeaponLevel() / 2;
		return int1 / 2;
	}

	public BaseVehicle getVehicle() {
		return this.vehicle;
	}

	public void setVehicle(BaseVehicle baseVehicle) {
		this.vehicle = baseVehicle;
	}

	public boolean isUnderVehicle() {
		int int1 = ((int)this.x - 4) / 10;
		int int2 = ((int)this.y - 4) / 10;
		int int3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F));
		int int4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F));
		Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
		for (int int5 = int2; int5 < int4; ++int5) {
			for (int int6 = int1; int6 < int3; ++int6) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int6 * 10, int5 * 10, 0);
				if (chunk != null) {
					for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
						BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
						Vector2 vector22 = baseVehicle.testCollisionWithCharacter(this, 0.3F, vector2);
						if (vector22 != null && vector22.x != -1.0F) {
							((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
							return true;
						}
					}
				}
			}
		}

		((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
		return false;
	}

	public boolean isProne() {
		return this.isOnFloor();
	}

	public boolean isBeingSteppedOn() {
		if (!this.isOnFloor()) {
			return false;
		} else {
			for (int int1 = -1; int1 <= 1; ++int1) {
				for (int int2 = -1; int2 <= 1; ++int2) {
					IsoGridSquare square = this.getCell().getGridSquare((int)this.x + int2, (int)this.y + int1, (int)this.z);
					if (square != null) {
						ArrayList arrayList = square.getMovingObjects();
						for (int int3 = 0; int3 < arrayList.size(); ++int3) {
							IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int3);
							if (movingObject != this) {
								IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
								if (gameCharacter != null && gameCharacter.getVehicle() == null && !movingObject.isOnFloor() && ZombieOnGroundState.isCharacterStandingOnOther(gameCharacter, this)) {
									return true;
								}
							}
						}
					}
				}
			}

			return false;
		}
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
				if (inventoryItem.getAttachedSlot() > -1 && !this.isEquipped(inventoryItem)) {
					float1 += inventoryItem.getHotbarEquippedWeight();
				} else if (this.isEquipped(inventoryItem)) {
					float1 += inventoryItem.getEquippedWeight();
				} else {
					float1 += inventoryItem.getUnequippedWeight();
				}
			}

			return float1;
		}
	}

	public void dropHandItems() {
		if (!"Tutorial".equals(Core.GameMode)) {
			if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
				this.dropHeavyItems();
				IsoGridSquare square = this.getCurrentSquare();
				if (square != null) {
					InventoryItem inventoryItem = this.getPrimaryHandItem();
					InventoryItem inventoryItem2 = this.getSecondaryHandItem();
					if (inventoryItem != null || inventoryItem2 != null) {
						square = this.getSolidFloorAt(square.x, square.y, square.z);
						if (square != null) {
							float float1 = Rand.Next(0.1F, 0.9F);
							float float2 = Rand.Next(0.1F, 0.9F);
							float float3 = square.getApparentZ(float1, float2) - (float)square.getZ();
							boolean boolean1 = false;
							if (inventoryItem2 == inventoryItem) {
								boolean1 = true;
							}

							if (inventoryItem != null) {
								this.setPrimaryHandItem((InventoryItem)null);
								this.getInventory().DoRemoveItem(inventoryItem);
								square.AddWorldInventoryItem(inventoryItem, float1, float2, float3);
								LuaEventManager.triggerEvent("OnContainerUpdate");
								LuaEventManager.triggerEvent("onItemFall", inventoryItem);
							}

							if (inventoryItem2 != null) {
								this.setSecondaryHandItem((InventoryItem)null);
								if (!boolean1) {
									this.getInventory().DoRemoveItem(inventoryItem2);
									square.AddWorldInventoryItem(inventoryItem2, float1, float2, float3);
									LuaEventManager.triggerEvent("OnContainerUpdate");
									LuaEventManager.triggerEvent("onItemFall", inventoryItem2);
								}
							}

							this.resetEquippedHandsModels();
						}
					}
				}
			}
		}
	}

	public boolean shouldBecomeZombieAfterDeath() {
		float float1;
		BodyDamage bodyDamage;
		boolean boolean1;
		switch (SandboxOptions.instance.Lore.Transmission.getValue()) {
		case 1: 
			if (!this.getBodyDamage().IsFakeInfected()) {
				float1 = this.getBodyDamage().getInfectionLevel();
				bodyDamage = this.BodyDamage;
				if (float1 >= 0.001F) {
					boolean1 = true;
					return boolean1;
				}
			}

			boolean1 = false;
			return boolean1;
		
		case 2: 
			if (!this.getBodyDamage().IsFakeInfected()) {
				float1 = this.getBodyDamage().getInfectionLevel();
				bodyDamage = this.BodyDamage;
				if (float1 >= 0.001F) {
					boolean1 = true;
					return boolean1;
				}
			}

			boolean1 = false;
			return boolean1;
		
		case 3: 
			return true;
		
		case 4: 
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
						PerkFactory.Perk perk;
						int int2;
						if (hashMap2 != null) {
							for (Iterator iterator = hashMap2.entrySet().iterator(); iterator.hasNext(); hashMap.put(perk, int2)) {
								Entry entry = (Entry)iterator.next();
								perk = (PerkFactory.Perk)entry.getKey();
								int2 = (Integer)entry.getValue();
								if (hashMap.containsKey(perk)) {
									int2 += (Integer)hashMap.get(perk);
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
			PerkFactory.Perk perk2;
			int int3;
			for (iterator2 = hashMap3.entrySet().iterator(); iterator2.hasNext(); hashMap.put(perk2, int3)) {
				entry2 = (Entry)iterator2.next();
				perk2 = (PerkFactory.Perk)entry2.getKey();
				int3 = (Integer)entry2.getValue();
				if (hashMap.containsKey(perk2)) {
					int3 += (Integer)hashMap.get(perk2);
				}
			}

			iterator2 = hashMap.entrySet().iterator();
			while (iterator2.hasNext()) {
				entry2 = (Entry)iterator2.next();
				perk2 = (PerkFactory.Perk)entry2.getKey();
				int3 = (Integer)entry2.getValue();
				int3 = Math.max(0, int3);
				int3 = Math.min(10, int3);
				this.getDescriptor().getXPBoostMap().put(perk2, Math.min(3, int3));
				for (int int4 = 0; int4 < int3; ++int4) {
					this.LevelPerk(perk2);
				}

				this.getXp().setXPToLevel(perk2, this.getPerkLevel(perk2));
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
					int int1 = Rand.Next(5);
					String string = "Base.Key" + (int1 + 1);
					InventoryItem inventoryItem2 = inventoryContainer.getInventory().AddItem(string);
					inventoryItem2.setKeyId(roomDef.getBuilding().getKeyId());
				}
			}
		}
	}

	public void autoDrink() {
		if (!GameServer.bServer) {
			if (!GameClient.bClient || ((IsoPlayer)this).isLocalPlayer()) {
				if (Core.getInstance().getOptionAutoDrink()) {
					if (!LuaHookManager.TriggerHook("AutoDrink", this)) {
						if (!(this.stats.thirst <= 0.1F)) {
							InventoryItem inventoryItem = this.getWaterSource(this.getInventory().getItems());
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
	}

	public InventoryItem getWaterSource(ArrayList arrayList) {
		InventoryItem inventoryItem = null;
		new ArrayList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)arrayList.get(int1);
			if (inventoryItem2.isWaterSource() && !inventoryItem2.isBeingFilled() && !inventoryItem2.isTaintedWater()) {
				if (inventoryItem2 instanceof Drainable) {
					if (((Drainable)inventoryItem2).getUsedDelta() > 0.0F) {
						inventoryItem = inventoryItem2;
						break;
					}
				} else if (!(inventoryItem2 instanceof InventoryContainer)) {
					inventoryItem = inventoryItem2;
					break;
				}
			}
		}

		return inventoryItem;
	}

	public List getKnownRecipes() {
		return this.knownRecipes;
	}

	public boolean isRecipeKnown(Recipe recipe) {
		if (DebugOptions.instance.CheatRecipeKnowAll.getValue()) {
			return true;
		} else {
			return !recipe.needToBeLearn() || this.getKnownRecipes().contains(recipe.getOriginalname());
		}
	}

	public boolean isRecipeKnown(String string) {
		Recipe recipe = ScriptManager.instance.getRecipe(string);
		if (recipe == null) {
			return DebugOptions.instance.CheatRecipeKnowAll.getValue() ? true : this.getKnownRecipes().contains(string);
		} else {
			return this.isRecipeKnown(recipe);
		}
	}

	public boolean learnRecipe(String string) {
		if (!this.isRecipeKnown(string)) {
			this.getKnownRecipes().add(string);
			return true;
		} else {
			return false;
		}
	}

	public void addKnownMediaLine(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			this.knownMediaLines.add(string.trim());
		}
	}

	public void removeKnownMediaLine(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			this.knownMediaLines.remove(string.trim());
		}
	}

	public void clearKnownMediaLines() {
		this.knownMediaLines.clear();
	}

	public boolean isKnownMediaLine(String string) {
		return StringUtils.isNullOrWhitespace(string) ? false : this.knownMediaLines.contains(string.trim());
	}

	protected void saveKnownMediaLines(ByteBuffer byteBuffer) {
		byteBuffer.putShort((short)this.knownMediaLines.size());
		Iterator iterator = this.knownMediaLines.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			GameWindow.WriteStringUTF(byteBuffer, string);
		}
	}

	protected void loadKnownMediaLines(ByteBuffer byteBuffer, int int1) {
		this.knownMediaLines.clear();
		short short1 = byteBuffer.getShort();
		for (int int2 = 0; int2 < short1; ++int2) {
			String string = GameWindow.ReadStringUTF(byteBuffer);
			this.knownMediaLines.add(string);
		}
	}

	public boolean isMoving() {
		return this instanceof IsoPlayer && !((IsoPlayer)this).isAttackAnimThrowTimeOut() ? false : this.m_isMoving;
	}

	public boolean isBehaviourMoving() {
		State state = this.getCurrentState();
		return state != null && state.isMoving(this);
	}

	public boolean isPlayerMoving() {
		return false;
	}

	public void setMoving(boolean boolean1) {
		this.m_isMoving = boolean1;
		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).bRemote) {
			((IsoPlayer)this).m_isPlayerMoving = boolean1;
			((IsoPlayer)this).setJustMoved(boolean1);
		}
	}

	private boolean isFacingNorthWesterly() {
		return this.dir == IsoDirections.W || this.dir == IsoDirections.NW || this.dir == IsoDirections.N || this.dir == IsoDirections.NE;
	}

	public boolean isAttacking() {
		return false;
	}

	public boolean isZombieAttacking() {
		return false;
	}

	public boolean isZombieAttacking(IsoMovingObject movingObject) {
		return false;
	}

	private boolean isZombieThumping() {
		if (this.isZombie()) {
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
		} else if (this.isBehaviourMoving() && !gameCharacter.isBehaviourMoving()) {
			return 1;
		} else if (!this.isBehaviourMoving() && gameCharacter.isBehaviourMoving()) {
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

	public long playSoundLocal(String string) {
		return this.getEmitter().playSoundImpl(string, (IsoObject)null);
	}

	public void stopOrTriggerSound(long long1) {
		this.getEmitter().stopOrTriggerSound(long1);
	}

	public void addWorldSoundUnlessInvisible(int int1, int int2, boolean boolean1) {
		if (!this.isInvisible()) {
			WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), int1, int2, boolean1);
		}
	}

	public boolean isKnownPoison(InventoryItem inventoryItem) {
		if (inventoryItem.hasTag("NoDetect")) {
			return false;
		} else if (inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			if (food.getPoisonPower() <= 0) {
				return false;
			} else if (food.getHerbalistType() != null && !food.getHerbalistType().isEmpty()) {
				return this.isRecipeKnown("Herbalist");
			} else if (food.getPoisonDetectionLevel() >= 0 && this.getPerkLevel(PerkFactory.Perks.Cooking) >= 10 - food.getPoisonDetectionLevel()) {
				return true;
			} else {
				return food.getPoisonLevelForRecipe() != null;
			}
		} else {
			return false;
		}
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

	public float Hit(BaseVehicle baseVehicle, float float1, boolean boolean1, float float2, float float3) {
		this.setHitFromBehind(boolean1);
		if (GameClient.bClient) {
			this.setAttackedBy((IsoGameCharacter)GameClient.IDToPlayerMap.get(baseVehicle.getNetPlayerId()));
		} else if (GameServer.bServer) {
			this.setAttackedBy((IsoGameCharacter)GameServer.IDToPlayerMap.get(baseVehicle.getNetPlayerId()));
		} else {
			this.setAttackedBy(baseVehicle.getDriver());
		}

		this.getHitDir().set(float2, float3);
		if (!this.isKnockedDown()) {
			this.setHitForce(Math.max(0.5F, float1 * 0.15F));
		} else {
			this.setHitForce(Math.min(2.5F, float1 * 0.15F));
		}

		if (GameClient.bClient) {
			HitReactionNetworkAI.CalcHitReactionVehicle(this, baseVehicle);
		}

		DebugLog.Damage.noise("Vehicle id=%d hit %s id=%d: speed=%f force=%f hitDir=%s", baseVehicle.getId(), this.getClass().getSimpleName(), this.getOnlineID(), float1, this.getHitForce(), this.getHitDir());
		return this.getHealth();
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

	public MapKnowledge getMapKnowledge() {
		return this.mapKnowledge;
	}

	public IsoObject getBed() {
		return this.isAsleep() ? this.bed : null;
	}

	public void setBed(IsoObject object) {
		this.bed = object;
	}

	public boolean avoidDamage() {
		return this.m_avoidDamage;
	}

	public void setAvoidDamage(boolean boolean1) {
		this.m_avoidDamage = boolean1;
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
		this.timeSinceLastSmoke = PZMath.clamp(float1, 0.0F, 10.0F);
	}

	public boolean isInvisible() {
		return this.m_invisible;
	}

	public void setInvisible(boolean boolean1) {
		this.m_invisible = boolean1;
	}

	public boolean isDriving() {
		return this.getVehicle() != null && this.getVehicle().getDriver() == this && !this.getVehicle().isStopped();
	}

	public boolean isInARoom() {
		return this.square != null && this.square.isInARoom();
	}

	public boolean isGodMod() {
		return this.m_godMod;
	}

	public void setGodMod(boolean boolean1) {
		if (!this.isDead()) {
			this.m_godMod = boolean1;
			if (this instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this).isLocalPlayer()) {
				this.updateMovementRates();
				GameClient.sendPlayerInjuries((IsoPlayer)this);
				GameClient.sendPlayerDamage((IsoPlayer)this);
			}
		}
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

	public boolean isFarmingCheat() {
		return this.farmingCheat;
	}

	public void setFarmingCheat(boolean boolean1) {
		this.farmingCheat = boolean1;
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

	public boolean isMovablesCheat() {
		return this.movablesCheat;
	}

	public void setMovablesCheat(boolean boolean1) {
		this.movablesCheat = boolean1;
	}

	public boolean isTimedActionInstantCheat() {
		return this.timedActionInstantCheat;
	}

	public void setTimedActionInstantCheat(boolean boolean1) {
		this.timedActionInstantCheat = boolean1;
	}

	public boolean isTimedActionInstant() {
		return Core.bDebug && DebugOptions.instance.CheatTimedActionInstant.getValue() ? true : this.isTimedActionInstantCheat();
	}

	public boolean isShowAdminTag() {
		return this.showAdminTag;
	}

	public void setShowAdminTag(boolean boolean1) {
		this.showAdminTag = boolean1;
	}

	public IAnimationVariableSlot getVariable(AnimationVariableHandle animationVariableHandle) {
		return this.getGameVariablesInternal().getVariable(animationVariableHandle);
	}

	public IAnimationVariableSlot getVariable(String string) {
		return this.getGameVariablesInternal().getVariable(string);
	}

	public IAnimationVariableSlot getOrCreateVariable(String string) {
		return this.getGameVariablesInternal().getOrCreateVariable(string);
	}

	public void setVariable(IAnimationVariableSlot iAnimationVariableSlot) {
		this.getGameVariablesInternal().setVariable(iAnimationVariableSlot);
	}

	public void setVariable(String string, String string2) {
		this.getGameVariablesInternal().setVariable(string, string2);
	}

	public void setVariable(String string, boolean boolean1) {
		this.getGameVariablesInternal().setVariable(string, boolean1);
	}

	public void setVariable(String string, float float1) {
		this.getGameVariablesInternal().setVariable(string, float1);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped);
	}

	protected void setVariable(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public void setVariable(String string, boolean boolean1, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, boolean1, callbackGetStrongTyped);
	}

	public void setVariable(String string, boolean boolean1, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, boolean1, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public void setVariable(String string, String string2, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, string2, callbackGetStrongTyped);
	}

	public void setVariable(String string, String string2, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, string2, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public void setVariable(String string, float float1, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, float1, callbackGetStrongTyped);
	}

	public void setVariable(String string, float float1, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, float1, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public void setVariable(String string, int int1, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, int1, callbackGetStrongTyped);
	}

	public void setVariable(String string, int int1, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.getGameVariablesInternal().setVariable(string, int1, callbackGetStrongTyped, callbackSetStrongTyped);
	}

	public void clearVariable(String string) {
		this.getGameVariablesInternal().clearVariable(string);
	}

	public void clearVariables() {
		this.getGameVariablesInternal().clearVariables();
	}

	public String getVariableString(String string) {
		return this.getGameVariablesInternal().getVariableString(string);
	}

	private String getFootInjuryType() {
		if (!(this instanceof IsoPlayer)) {
			return "";
		} else {
			BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Foot_L);
			BodyPart bodyPart2 = this.getBodyDamage().getBodyPart(BodyPartType.Foot_R);
			if (!this.bRunning) {
				if (bodyPart.haveBullet() || bodyPart.getBurnTime() > 5.0F || bodyPart.bitten() || bodyPart.deepWounded() || bodyPart.isSplint() || bodyPart.getFractureTime() > 0.0F || bodyPart.haveGlass()) {
					return "leftheavy";
				}

				if (bodyPart2.haveBullet() || bodyPart2.getBurnTime() > 5.0F || bodyPart2.bitten() || bodyPart2.deepWounded() || bodyPart2.isSplint() || bodyPart2.getFractureTime() > 0.0F || bodyPart2.haveGlass()) {
					return "rightheavy";
				}
			}

			if (!(bodyPart.getScratchTime() > 5.0F) && !(bodyPart.getCutTime() > 7.0F) && !(bodyPart.getBurnTime() > 0.0F)) {
				if (!(bodyPart2.getScratchTime() > 5.0F) && !(bodyPart2.getCutTime() > 7.0F) && !(bodyPart2.getBurnTime() > 0.0F)) {
					return "";
				} else {
					return "rightlight";
				}
			} else {
				return "leftlight";
			}
		}
	}

	public float getVariableFloat(String string, float float1) {
		return this.getGameVariablesInternal().getVariableFloat(string, float1);
	}

	public boolean getVariableBoolean(String string) {
		return this.getGameVariablesInternal().getVariableBoolean(string);
	}

	public boolean getVariableBoolean(String string, boolean boolean1) {
		return this.getGameVariablesInternal().getVariableBoolean(this.name, boolean1);
	}

	public boolean isVariable(String string, String string2) {
		return this.getGameVariablesInternal().isVariable(string, string2);
	}

	public boolean containsVariable(String string) {
		return this.getGameVariablesInternal().containsVariable(string);
	}

	public Iterable getGameVariables() {
		return this.getGameVariablesInternal().getGameVariables();
	}

	private AnimationVariableSource getGameVariablesInternal() {
		return this.m_PlaybackGameVariables != null ? this.m_PlaybackGameVariables : this.m_GameVariables;
	}

	public AnimationVariableSource startPlaybackGameVariables() {
		if (this.m_PlaybackGameVariables != null) {
			DebugLog.General.error("Error! PlaybackGameVariables is already active.");
			return this.m_PlaybackGameVariables;
		} else {
			AnimationVariableSource animationVariableSource = new AnimationVariableSource();
			Iterator iterator = this.getGameVariables().iterator();
			while (iterator.hasNext()) {
				IAnimationVariableSlot iAnimationVariableSlot = (IAnimationVariableSlot)iterator.next();
				AnimationVariableType animationVariableType = iAnimationVariableSlot.getType();
				switch (animationVariableType) {
				case String: 
					animationVariableSource.setVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueString());
					break;
				
				case Float: 
					animationVariableSource.setVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueFloat());
					break;
				
				case Boolean: 
					animationVariableSource.setVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueBool());
				
				case Void: 
					break;
				
				default: 
					DebugLog.General.error("Error! Variable type not handled: %s", animationVariableType.toString());
				
				}
			}

			this.m_PlaybackGameVariables = animationVariableSource;
			return this.m_PlaybackGameVariables;
		}
	}

	public void endPlaybackGameVariables(AnimationVariableSource animationVariableSource) {
		if (this.m_PlaybackGameVariables != animationVariableSource) {
			DebugLog.General.error("Error! Playback GameVariables do not match.");
		}

		this.m_PlaybackGameVariables = null;
	}

	public void playbackSetCurrentStateSnapshot(ActionStateSnapshot actionStateSnapshot) {
		if (this.actionContext != null) {
			this.actionContext.setPlaybackStateSnapshot(actionStateSnapshot);
		}
	}

	public ActionStateSnapshot playbackRecordCurrentStateSnapshot() {
		return this.actionContext == null ? null : this.actionContext.getPlaybackStateSnapshot();
	}

	public String GetVariable(String string) {
		return this.getVariableString(string);
	}

	public void SetVariable(String string, String string2) {
		this.setVariable(string, string2);
	}

	public void ClearVariable(String string) {
		this.clearVariable(string);
	}

	public void actionStateChanged(ActionContext actionContext) {
		ArrayList arrayList = IsoGameCharacter.L_actionStateChanged.stateNames;
		PZArrayUtil.listConvert(actionContext.getChildStates(), arrayList, (var0)->{
			return var0.name;
		});
		this.advancedAnimator.SetState(actionContext.getCurrentStateName(), arrayList);
		try {
			++this.stateMachine.activeStateChanged;
			State state = (State)this.m_stateUpdateLookup.get(actionContext.getCurrentStateName().toLowerCase());
			if (state == null) {
				state = this.defaultState;
			}

			ArrayList arrayList2 = IsoGameCharacter.L_actionStateChanged.states;
			PZArrayUtil.listConvert(actionContext.getChildStates(), arrayList2, this.m_stateUpdateLookup, (var0,actionContextx)->{
				return (State)actionContextx.get(var0.name.toLowerCase());
			});

			this.stateMachine.changeState(state, arrayList2);
		} finally {
			--this.stateMachine.activeStateChanged;
		}
	}

	public boolean isFallOnFront() {
		return this.fallOnFront;
	}

	public void setFallOnFront(boolean boolean1) {
		this.fallOnFront = boolean1;
	}

	public boolean isHitFromBehind() {
		return this.hitFromBehind;
	}

	public void setHitFromBehind(boolean boolean1) {
		this.hitFromBehind = boolean1;
	}

	public void reportEvent(String string) {
		this.actionContext.reportEvent(string);
	}

	public void StartTimedActionAnim(String string) {
		this.StartTimedActionAnim(string, (String)null);
	}

	public void StartTimedActionAnim(String string, String string2) {
		this.reportEvent(string);
		if (string2 != null) {
			this.setVariable("TimedActionType", string2);
		}

		this.resetModelNextFrame();
	}

	public void StopTimedActionAnim() {
		this.clearVariable("TimedActionType");
		this.reportEvent("Event_TA_Exit");
		this.resetModelNextFrame();
	}

	public boolean hasHitReaction() {
		return !StringUtils.isNullOrEmpty(this.getHitReaction());
	}

	public String getHitReaction() {
		return this.hitReaction;
	}

	public void setHitReaction(String string) {
		this.hitReaction = string;
	}

	public void CacheEquipped() {
		this.cacheEquiped[0] = this.getPrimaryHandItem();
		this.cacheEquiped[1] = this.getSecondaryHandItem();
	}

	public InventoryItem GetPrimaryEquippedCache() {
		return this.cacheEquiped[0] != null && this.inventory.contains(this.cacheEquiped[0]) ? this.cacheEquiped[0] : null;
	}

	public InventoryItem GetSecondaryEquippedCache() {
		return this.cacheEquiped[1] != null && this.inventory.contains(this.cacheEquiped[1]) ? this.cacheEquiped[1] : null;
	}

	public void ClearEquippedCache() {
		this.cacheEquiped[0] = null;
		this.cacheEquiped[1] = null;
	}

	public boolean isBehind(IsoGameCharacter gameCharacter) {
		Vector2 vector2 = tempVector2_1.set(this.getX(), this.getY());
		Vector2 vector22 = tempVector2_2.set(gameCharacter.getX(), gameCharacter.getY());
		vector22.x -= vector2.x;
		vector22.y -= vector2.y;
		Vector2 vector23 = gameCharacter.getForwardDirection();
		vector22.normalize();
		vector23.normalize();
		float float1 = vector22.dot(vector23);
		return (double)float1 > 0.6;
	}

	public void resetEquippedHandsModels() {
		if (!GameServer.bServer || ServerGUI.isCreated()) {
			if (this.hasActiveModel()) {
				ModelManager.instance.ResetEquippedNextFrame(this);
			}
		}
	}

	public AnimatorDebugMonitor getDebugMonitor() {
		return this.advancedAnimator.getDebugMonitor();
	}

	public void setDebugMonitor(AnimatorDebugMonitor animatorDebugMonitor) {
		this.advancedAnimator.setDebugMonitor(animatorDebugMonitor);
	}

	public boolean isAimAtFloor() {
		return this.bAimAtFloor;
	}

	public void setAimAtFloor(boolean boolean1) {
		this.bAimAtFloor = boolean1;
	}

	public String testDotSide(IsoMovingObject movingObject) {
		Vector2 vector2 = this.getLookVector(IsoGameCharacter.l_testDotSide.v1);
		Vector2 vector22 = IsoGameCharacter.l_testDotSide.v2.set(this.getX(), this.getY());
		Vector2 vector23 = IsoGameCharacter.l_testDotSide.v3.set(movingObject.x - vector22.x, movingObject.y - vector22.y);
		vector23.normalize();
		float float1 = Vector2.dot(vector23.x, vector23.y, vector2.x, vector2.y);
		if ((double)float1 > 0.7) {
			return "FRONT";
		} else if (float1 < 0.0F && (double)float1 < -0.5) {
			return "BEHIND";
		} else {
			float float2 = movingObject.x;
			float float3 = movingObject.y;
			float float4 = vector22.x;
			float float5 = vector22.y;
			float float6 = vector22.x + vector2.x;
			float float7 = vector22.y + vector2.y;
			float float8 = (float2 - float4) * (float7 - float5) - (float3 - float5) * (float6 - float4);
			return float8 > 0.0F ? "RIGHT" : "LEFT";
		}
	}

	public void addBasicPatch(BloodBodyPartType bloodBodyPartType) {
		if (this instanceof IHumanVisual) {
			if (bloodBodyPartType == null) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(0, BloodBodyPartType.MAX.index()));
			}

			HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
			this.getItemVisuals(tempItemVisuals);
			BloodClothingType.addBasicPatch(bloodBodyPartType, humanVisual, tempItemVisuals);
			this.bUpdateModelTextures = true;
			this.bUpdateEquippedTextures = true;
			if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
				LuaEventManager.triggerEvent("OnClothingUpdated", this);
			}
		}
	}

	public boolean addHole(BloodBodyPartType bloodBodyPartType) {
		return this.addHole(bloodBodyPartType, false);
	}

	public boolean addHole(BloodBodyPartType bloodBodyPartType, boolean boolean1) {
		if (!(this instanceof IHumanVisual)) {
			return false;
		} else {
			if (bloodBodyPartType == null) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
			}

			HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
			this.getItemVisuals(tempItemVisuals);
			boolean boolean2 = BloodClothingType.addHole(bloodBodyPartType, humanVisual, tempItemVisuals, boolean1);
			this.bUpdateModelTextures = true;
			if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
				LuaEventManager.triggerEvent("OnClothingUpdated", this);
				if (GameClient.bClient) {
					GameClient.instance.sendClothing((IsoPlayer)this, "", (InventoryItem)null);
				}
			}

			return boolean2;
		}
	}

	public void addDirt(BloodBodyPartType bloodBodyPartType, Integer integer, boolean boolean1) {
		HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
		if (integer == null) {
			integer = OutfitRNG.Next(5, 10);
		}

		boolean boolean2 = false;
		if (bloodBodyPartType == null) {
			boolean2 = true;
		}

		this.getItemVisuals(tempItemVisuals);
		for (int int1 = 0; int1 < integer; ++int1) {
			if (boolean2) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
			}

			BloodClothingType.addDirt(bloodBodyPartType, humanVisual, tempItemVisuals, boolean1);
		}

		this.bUpdateModelTextures = true;
		if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			LuaEventManager.triggerEvent("OnClothingUpdated", this);
		}
	}

	public void addBlood(BloodBodyPartType bloodBodyPartType, boolean boolean1, boolean boolean2, boolean boolean3) {
		HumanVisual humanVisual = ((IHumanVisual)this).getHumanVisual();
		int int1 = 1;
		boolean boolean4 = false;
		if (bloodBodyPartType == null) {
			boolean4 = true;
		}

		if (this.getPrimaryHandItem() instanceof HandWeapon) {
			int1 = ((HandWeapon)this.getPrimaryHandItem()).getSplatNumber();
			if (OutfitRNG.Next(15) < this.getWeaponLevel()) {
				--int1;
			}
		}

		if (boolean2) {
			int1 = 20;
		}

		if (boolean1) {
			int1 = 5;
		}

		if (this.isZombie()) {
			int1 += 8;
		}

		this.getItemVisuals(tempItemVisuals);
		for (int int2 = 0; int2 < int1; ++int2) {
			if (boolean4) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
				if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
					HandWeapon handWeapon = (HandWeapon)this.getPrimaryHandItem();
					if (handWeapon.getBloodLevel() < 1.0F) {
						float float1 = handWeapon.getBloodLevel() + 0.02F;
						handWeapon.setBloodLevel(float1);
						this.bUpdateEquippedTextures = true;
					}
				}
			}

			BloodClothingType.addBlood(bloodBodyPartType, humanVisual, tempItemVisuals, boolean3);
		}

		this.bUpdateModelTextures = true;
		if (!GameServer.bServer && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			LuaEventManager.triggerEvent("OnClothingUpdated", this);
		}
	}

	public float getBodyPartClothingDefense(Integer integer, boolean boolean1, boolean boolean2) {
		float float1 = 0.0F;
		this.getItemVisuals(tempItemVisuals);
		for (int int1 = tempItemVisuals.size() - 1; int1 >= 0; --int1) {
			ItemVisual itemVisual = (ItemVisual)tempItemVisuals.get(int1);
			Item item = itemVisual.getScriptItem();
			if (item != null) {
				ArrayList arrayList = item.getBloodClothingType();
				if (arrayList != null) {
					ArrayList arrayList2 = BloodClothingType.getCoveredParts(arrayList);
					if (arrayList2 != null) {
						InventoryItem inventoryItem = itemVisual.getInventoryItem();
						if (inventoryItem == null) {
							inventoryItem = InventoryItemFactory.CreateItem(itemVisual.getItemType());
							if (inventoryItem == null) {
								continue;
							}
						}

						for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
							if (inventoryItem instanceof Clothing && ((BloodBodyPartType)arrayList2.get(int2)).index() == integer && itemVisual.getHole((BloodBodyPartType)arrayList2.get(int2)) == 0.0F) {
								Clothing clothing = (Clothing)inventoryItem;
								float1 += clothing.getDefForPart((BloodBodyPartType)arrayList2.get(int2), boolean1, boolean2);
								break;
							}
						}
					}
				}
			}
		}

		float1 = Math.min(100.0F, float1);
		return float1;
	}

	public boolean isBumped() {
		return !StringUtils.isNullOrWhitespace(this.getBumpType());
	}

	public boolean isBumpDone() {
		return this.m_isBumpDone;
	}

	public void setBumpDone(boolean boolean1) {
		this.m_isBumpDone = boolean1;
	}

	public boolean isBumpFall() {
		return this.m_bumpFall;
	}

	public void setBumpFall(boolean boolean1) {
		this.m_bumpFall = boolean1;
	}

	public boolean isBumpStaggered() {
		return this.m_bumpStaggered;
	}

	public void setBumpStaggered(boolean boolean1) {
		this.m_bumpStaggered = boolean1;
	}

	public String getBumpType() {
		return this.bumpType;
	}

	public void setBumpType(String string) {
		if (StringUtils.equalsIgnoreCase(this.bumpType, string)) {
			this.bumpType = string;
		} else {
			boolean boolean1 = this.isBumped();
			this.bumpType = string;
			boolean boolean2 = this.isBumped();
			if (boolean2 != boolean1) {
				this.setBumpStaggered(boolean2);
			}
		}
	}

	public String getBumpFallType() {
		return this.m_bumpFallType;
	}

	public void setBumpFallType(String string) {
		this.m_bumpFallType = string;
	}

	public IsoGameCharacter getBumpedChr() {
		return this.bumpedChr;
	}

	public void setBumpedChr(IsoGameCharacter gameCharacter) {
		this.bumpedChr = gameCharacter;
	}

	public long getLastBump() {
		return this.lastBump;
	}

	public void setLastBump(long long1) {
		this.lastBump = long1;
	}

	public boolean isSitOnGround() {
		return this.sitOnGround;
	}

	public void setSitOnGround(boolean boolean1) {
		this.sitOnGround = boolean1;
	}

	public String getUID() {
		return this.m_UID;
	}

	protected HashMap getStateUpdateLookup() {
		return this.m_stateUpdateLookup;
	}

	public boolean isRunning() {
		return this.getMoodles() != null && this.getMoodles().getMoodleLevel(MoodleType.Endurance) >= 3 ? false : this.bRunning;
	}

	public void setRunning(boolean boolean1) {
		this.bRunning = boolean1;
	}

	public boolean isSprinting() {
		return this.bSprinting && !this.canSprint() ? false : this.bSprinting;
	}

	public void setSprinting(boolean boolean1) {
		this.bSprinting = boolean1;
	}

	public boolean canSprint() {
		if (this instanceof IsoPlayer && !((IsoPlayer)this).isAllowSprint()) {
			return false;
		} else if ("Tutorial".equals(Core.GameMode)) {
			return true;
		} else {
			InventoryItem inventoryItem = this.getPrimaryHandItem();
			if (inventoryItem != null && inventoryItem.isEquippedNoSprint()) {
				return false;
			} else {
				inventoryItem = this.getSecondaryHandItem();
				if (inventoryItem != null && inventoryItem.isEquippedNoSprint()) {
					return false;
				} else {
					return this.getMoodles() == null || this.getMoodles().getMoodleLevel(MoodleType.Endurance) < 2;
				}
			}
		}
	}

	public void postUpdateModelTextures() {
		this.bUpdateModelTextures = true;
	}

	public ModelInstanceTextureCreator getTextureCreator() {
		return this.textureCreator;
	}

	public void setTextureCreator(ModelInstanceTextureCreator modelInstanceTextureCreator) {
		this.textureCreator = modelInstanceTextureCreator;
	}

	public void postUpdateEquippedTextures() {
		this.bUpdateEquippedTextures = true;
	}

	public ArrayList getReadyModelData() {
		return this.readyModelData;
	}

	public boolean getIgnoreMovement() {
		return this.ignoreMovement;
	}

	public void setIgnoreMovement(boolean boolean1) {
		if (this instanceof IsoPlayer && boolean1) {
			((IsoPlayer)this).networkAI.needToUpdate();
		}

		this.ignoreMovement = boolean1;
	}

	public boolean isAutoWalk() {
		return this.bAutoWalk;
	}

	public void setAutoWalk(boolean boolean1) {
		this.bAutoWalk = boolean1;
	}

	public void setAutoWalkDirection(Vector2 vector2) {
		this.autoWalkDirection.set(vector2);
	}

	public Vector2 getAutoWalkDirection() {
		return this.autoWalkDirection;
	}

	public boolean isSneaking() {
		return this.getVariableFloat("WalkInjury", 0.0F) > 0.5F ? false : this.bSneaking;
	}

	public void setSneaking(boolean boolean1) {
		this.bSneaking = boolean1;
	}

	public GameCharacterAIBrain getGameCharacterAIBrain() {
		return this.GameCharacterAIBrain;
	}

	public float getMoveDelta() {
		return this.m_moveDelta;
	}

	public void setMoveDelta(float float1) {
		this.m_moveDelta = float1;
	}

	public float getTurnDelta() {
		if (this.isSprinting()) {
			return this.m_turnDeltaSprinting;
		} else {
			return this.isRunning() ? this.m_turnDeltaRunning : this.m_turnDeltaNormal;
		}
	}

	public void setTurnDelta(float float1) {
		this.m_turnDeltaNormal = float1;
	}

	public float getChopTreeSpeed() {
		return (this.Traits.Axeman.isSet() ? 1.25F : 1.0F) * GameTime.getAnimSpeedFix();
	}

	public boolean testDefense(IsoZombie zombie) {
		if (this.testDotSide(zombie).equals("FRONT") && !zombie.bCrawling && this.getSurroundingAttackingZombies() <= 3) {
			int int1 = 0;
			if ("KnifeDeath".equals(this.getVariableString("ZombieHitReaction"))) {
				int1 += 30;
			}

			int1 += this.getWeaponLevel() * 3;
			int1 += this.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
			int1 += this.getPerkLevel(PerkFactory.Perks.Strength) * 2;
			int1 -= this.getSurroundingAttackingZombies() * 5;
			int1 -= this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 2;
			int1 -= this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 2;
			int1 -= this.getMoodles().getMoodleLevel(MoodleType.Tired) * 3;
			if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
				int1 -= 7;
			}

			if (SandboxOptions.instance.Lore.Strength.getValue() == 3) {
				int1 += 7;
			}

			if (Rand.Next(100) < int1) {
				this.setAttackedBy(zombie);
				this.setHitReaction(zombie.getVariableString("PlayerHitReaction") + "Defended");
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int getSurroundingAttackingZombies() {
		movingStatic.clear();
		IsoGridSquare square = this.getCurrentSquare();
		if (square == null) {
			return 0;
		} else {
			movingStatic.addAll(square.getMovingObjects());
			if (square.n != null) {
				movingStatic.addAll(square.n.getMovingObjects());
			}

			if (square.s != null) {
				movingStatic.addAll(square.s.getMovingObjects());
			}

			if (square.e != null) {
				movingStatic.addAll(square.e.getMovingObjects());
			}

			if (square.w != null) {
				movingStatic.addAll(square.w.getMovingObjects());
			}

			if (square.nw != null) {
				movingStatic.addAll(square.nw.getMovingObjects());
			}

			if (square.sw != null) {
				movingStatic.addAll(square.sw.getMovingObjects());
			}

			if (square.se != null) {
				movingStatic.addAll(square.se.getMovingObjects());
			}

			if (square.ne != null) {
				movingStatic.addAll(square.ne.getMovingObjects());
			}

			int int1 = 0;
			for (int int2 = 0; int2 < movingStatic.size(); ++int2) {
				IsoZombie zombie = (IsoZombie)Type.tryCastTo((IsoMovingObject)movingStatic.get(int2), IsoZombie.class);
				if (zombie != null && zombie.target == this && !(this.DistToSquared(zombie) >= 0.80999994F) && (zombie.isCurrentState(AttackState.instance()) || zombie.isCurrentState(AttackNetworkState.instance()) || zombie.isCurrentState(LungeState.instance()) || zombie.isCurrentState(LungeNetworkState.instance()))) {
					++int1;
				}
			}

			return int1;
		}
	}

	public float checkIsNearWall() {
		if (this.bSneaking && this.getCurrentSquare() != null) {
			IsoGridSquare square = this.getCurrentSquare().nav[IsoDirections.N.index()];
			IsoGridSquare square2 = this.getCurrentSquare().nav[IsoDirections.S.index()];
			IsoGridSquare square3 = this.getCurrentSquare().nav[IsoDirections.E.index()];
			IsoGridSquare square4 = this.getCurrentSquare().nav[IsoDirections.W.index()];
			float float1 = 0.0F;
			float float2 = 0.0F;
			if (square != null) {
				float1 = square.getGridSneakModifier(true);
				if (float1 > 1.0F) {
					this.setVariable("nearWallCrouching", true);
					return float1;
				}
			}

			if (square2 != null) {
				float1 = square2.getGridSneakModifier(false);
				float2 = square2.getGridSneakModifier(true);
				if (float1 > 1.0F || float2 > 1.0F) {
					this.setVariable("nearWallCrouching", true);
					return float1 > 1.0F ? float1 : float2;
				}
			}

			if (square3 != null) {
				float1 = square3.getGridSneakModifier(false);
				float2 = square3.getGridSneakModifier(true);
				if (float1 > 1.0F || float2 > 1.0F) {
					this.setVariable("nearWallCrouching", true);
					return float1 > 1.0F ? float1 : float2;
				}
			}

			if (square4 != null) {
				float1 = square4.getGridSneakModifier(false);
				float2 = square4.getGridSneakModifier(true);
				if (float1 > 1.0F || float2 > 1.0F) {
					this.setVariable("nearWallCrouching", true);
					return float1 > 1.0F ? float1 : float2;
				}
			}

			float1 = this.getCurrentSquare().getGridSneakModifier(false);
			if (float1 > 1.0F) {
				this.setVariable("nearWallCrouching", true);
				return float1;
			} else if (this instanceof IsoPlayer && ((IsoPlayer)this).isNearVehicle()) {
				this.setVariable("nearWallCrouching", true);
				return 6.0F;
			} else {
				this.setVariable("nearWallCrouching", false);
				return 0.0F;
			}
		} else {
			this.setVariable("nearWallCrouching", false);
			return 0.0F;
		}
	}

	public float getBeenSprintingFor() {
		return this.BeenSprintingFor;
	}

	public void setBeenSprintingFor(float float1) {
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		if (float1 > 100.0F) {
			float1 = 100.0F;
		}

		this.BeenSprintingFor = float1;
	}

	public boolean isHideWeaponModel() {
		return this.hideWeaponModel;
	}

	public void setHideWeaponModel(boolean boolean1) {
		if (this.hideWeaponModel != boolean1) {
			this.hideWeaponModel = boolean1;
			this.resetEquippedHandsModels();
		}
	}

	public void setIsAiming(boolean boolean1) {
		if (this.ignoreAimingInput) {
			boolean1 = false;
		}

		if (this instanceof IsoPlayer && !((IsoPlayer)this).isAttackAnimThrowTimeOut() || this.isAttackAnim() || this.getVariableBoolean("ShoveAnim")) {
			boolean1 = true;
		}

		this.isAiming = boolean1;
	}

	public boolean isAiming() {
		if (GameClient.bClient && this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer() && DebugOptions.instance.MultiplayerAttackPlayer.getValue()) {
			return false;
		} else {
			return this.isNPC ? this.NPCGetAiming() : this.isAiming;
		}
	}

	public void resetBeardGrowingTime() {
		this.beardGrowTiming = (float)this.getHoursSurvived();
		if (GameClient.bClient && this instanceof IsoPlayer) {
			GameClient.instance.sendVisual((IsoPlayer)this);
		}
	}

	public void resetHairGrowingTime() {
		this.hairGrowTiming = (float)this.getHoursSurvived();
		if (GameClient.bClient && this instanceof IsoPlayer) {
			GameClient.instance.sendVisual((IsoPlayer)this);
		}
	}

	public void fallenOnKnees() {
		if (!(this instanceof IsoPlayer) || ((IsoPlayer)this).isLocalPlayer()) {
			if (!this.isInvincible()) {
				this.helmetFall(false);
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.Hand_L.index(), BloodBodyPartType.Torso_Upper.index()));
				if (Rand.NextBool(2)) {
					bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperLeg_L.index(), BloodBodyPartType.Back.index()));
				}

				for (int int1 = 0; int1 < 4; ++int1) {
					BloodBodyPartType bloodBodyPartType2 = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.Hand_L.index(), BloodBodyPartType.Torso_Upper.index()));
					if (Rand.NextBool(2)) {
						bloodBodyPartType2 = BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperLeg_L.index(), BloodBodyPartType.Back.index()));
					}

					this.addDirt(bloodBodyPartType2, Rand.Next(2, 6), false);
				}

				if (Rand.NextBool(2)) {
					if (Rand.NextBool(4)) {
						this.dropHandItems();
					}

					this.addHole(bloodBodyPartType);
					this.addBlood(bloodBodyPartType, true, false, false);
					BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(bloodBodyPartType.index()));
					if (bodyPart.scratched()) {
						bodyPart.generateDeepWound();
					} else {
						bodyPart.setScratched(true, true);
					}
				}
			}
		}
	}

	public void addVisualDamage(String string) {
		this.addBodyVisualFromItemType("Base." + string);
	}

	protected ItemVisual addBodyVisualFromItemType(String string) {
		Item item = ScriptManager.instance.getItem(string);
		return item != null && !StringUtils.isNullOrWhitespace(item.getClothingItem()) ? this.addBodyVisualFromClothingItemName(item.getClothingItem()) : null;
	}

	protected ItemVisual addBodyVisualFromClothingItemName(String string) {
		IHumanVisual iHumanVisual = (IHumanVisual)Type.tryCastTo(this, IHumanVisual.class);
		if (iHumanVisual == null) {
			return null;
		} else {
			String string2 = ScriptManager.instance.getItemTypeForClothingItem(string);
			if (string2 == null) {
				return null;
			} else {
				Item item = ScriptManager.instance.getItem(string2);
				if (item == null) {
					return null;
				} else {
					ClothingItem clothingItem = item.getClothingItemAsset();
					if (clothingItem == null) {
						return null;
					} else {
						ClothingItemReference clothingItemReference = new ClothingItemReference();
						clothingItemReference.itemGUID = clothingItem.m_GUID;
						clothingItemReference.randomize();
						ItemVisual itemVisual = new ItemVisual();
						itemVisual.setItemType(string2);
						itemVisual.synchWithOutfit(clothingItemReference);
						if (!this.isDuplicateBodyVisual(itemVisual)) {
							ItemVisuals itemVisuals = iHumanVisual.getHumanVisual().getBodyVisuals();
							itemVisuals.add(itemVisual);
							return itemVisual;
						} else {
							return null;
						}
					}
				}
			}
		}
	}

	protected boolean isDuplicateBodyVisual(ItemVisual itemVisual) {
		IHumanVisual iHumanVisual = (IHumanVisual)Type.tryCastTo(this, IHumanVisual.class);
		if (iHumanVisual == null) {
			return false;
		} else {
			ItemVisuals itemVisuals = iHumanVisual.getHumanVisual().getBodyVisuals();
			for (int int1 = 0; int1 < itemVisuals.size(); ++int1) {
				ItemVisual itemVisual2 = (ItemVisual)itemVisuals.get(int1);
				if (itemVisual.getClothingItemName().equals(itemVisual2.getClothingItemName()) && itemVisual.getTextureChoice() == itemVisual2.getTextureChoice() && itemVisual.getBaseTexture() == itemVisual2.getBaseTexture()) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isCriticalHit() {
		return this.isCrit;
	}

	public void setCriticalHit(boolean boolean1) {
		this.isCrit = boolean1;
	}

	public float getRunSpeedModifier() {
		return this.runSpeedModifier;
	}

	public void startMuzzleFlash() {
		float float1 = GameTime.getInstance().getNight() * 0.8F;
		float1 = Math.max(float1, 0.2F);
		IsoLightSource lightSource = new IsoLightSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), 0.8F * float1, 0.8F * float1, 0.6F * float1, 18, 6);
		IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
		this.m_muzzleFlash = System.currentTimeMillis();
	}

	public boolean isMuzzleFlash() {
		if (Core.bDebug && DebugOptions.instance.ModelRenderMuzzleflash.getValue()) {
			return true;
		} else {
			return this.m_muzzleFlash > System.currentTimeMillis() - 50L;
		}
	}

	public boolean isNPC() {
		return this.isNPC;
	}

	public void setNPC(boolean boolean1) {
		if (boolean1 && this.GameCharacterAIBrain == null) {
			this.GameCharacterAIBrain = new GameCharacterAIBrain(this);
		}

		this.isNPC = boolean1;
	}

	public void NPCSetRunning(boolean boolean1) {
		this.GameCharacterAIBrain.HumanControlVars.bRunning = boolean1;
	}

	public boolean NPCGetRunning() {
		return this.GameCharacterAIBrain.HumanControlVars.bRunning;
	}

	public void NPCSetJustMoved(boolean boolean1) {
		this.GameCharacterAIBrain.HumanControlVars.JustMoved = boolean1;
	}

	public void NPCSetAiming(boolean boolean1) {
		this.GameCharacterAIBrain.HumanControlVars.bAiming = boolean1;
	}

	public boolean NPCGetAiming() {
		return this.GameCharacterAIBrain.HumanControlVars.bAiming;
	}

	public void NPCSetAttack(boolean boolean1) {
		this.GameCharacterAIBrain.HumanControlVars.initiateAttack = boolean1;
	}

	public void NPCSetMelee(boolean boolean1) {
		this.GameCharacterAIBrain.HumanControlVars.bMelee = boolean1;
	}

	public void setMetabolicTarget(Metabolics metabolics) {
		if (metabolics != null) {
			this.setMetabolicTarget(metabolics.getMet());
		}
	}

	public void setMetabolicTarget(float float1) {
		if (this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null) {
			this.getBodyDamage().getThermoregulator().setMetabolicTarget(float1);
		}
	}

	public double getThirstMultiplier() {
		return this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null ? this.getBodyDamage().getThermoregulator().getFluidsMultiplier() : 1.0;
	}

	public double getHungerMultiplier() {
		return 1.0;
	}

	public double getFatiqueMultiplier() {
		return this.getBodyDamage() != null && this.getBodyDamage().getThermoregulator() != null ? this.getBodyDamage().getThermoregulator().getFatigueMultiplier() : 1.0;
	}

	public float getTimedActionTimeModifier() {
		return 1.0F;
	}

	public boolean addHoleFromZombieAttacks(BloodBodyPartType bloodBodyPartType, boolean boolean1) {
		this.getItemVisuals(tempItemVisuals);
		ItemVisual itemVisual = null;
		for (int int1 = tempItemVisuals.size() - 1; int1 >= 0; --int1) {
			ItemVisual itemVisual2 = (ItemVisual)tempItemVisuals.get(int1);
			Item item = itemVisual2.getScriptItem();
			if (item != null) {
				ArrayList arrayList = item.getBloodClothingType();
				if (arrayList != null) {
					ArrayList arrayList2 = BloodClothingType.getCoveredParts(arrayList);
					for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
						BloodBodyPartType bloodBodyPartType2 = (BloodBodyPartType)arrayList2.get(int2);
						if (bloodBodyPartType == bloodBodyPartType2) {
							itemVisual = itemVisual2;
							break;
						}
					}

					if (itemVisual != null) {
						break;
					}
				}
			}
		}

		float float1 = 0.0F;
		boolean boolean2 = false;
		if (itemVisual != null && itemVisual.getInventoryItem() != null && itemVisual.getInventoryItem() instanceof Clothing) {
			Clothing clothing = (Clothing)itemVisual.getInventoryItem();
			clothing.getPatchType(bloodBodyPartType);
			float1 = Math.max(30.0F, 100.0F - clothing.getDefForPart(bloodBodyPartType, !boolean1, false) / 1.5F);
		}

		if ((float)Rand.Next(100) < float1) {
			boolean boolean3 = this.addHole(bloodBodyPartType);
			if (boolean3) {
				this.getEmitter().playSoundImpl("ZombieRipClothing", (IsoObject)null);
			}

			boolean2 = true;
		}

		return boolean2;
	}

	protected void updateBandages() {
		s_bandages.update(this);
	}

	public float getTotalBlood() {
		float float1 = 0.0F;
		if (this.getWornItems() == null) {
			return float1;
		} else {
			for (int int1 = 0; int1 < this.getWornItems().size(); ++int1) {
				InventoryItem inventoryItem = this.getWornItems().get(int1).getItem();
				if (inventoryItem instanceof Clothing) {
					float1 += ((Clothing)inventoryItem).getBloodlevel();
				}
			}

			float1 += ((HumanVisual)this.getVisual()).getTotalBlood();
			return float1;
		}
	}

	public void attackFromWindowsLunge(IsoZombie zombie) {
		if (!(this.lungeFallTimer > 0.0F) && (int)this.getZ() == (int)zombie.getZ() && !zombie.isDead() && this.getCurrentSquare() != null && !this.getCurrentSquare().isDoorBlockedTo(zombie.getCurrentSquare()) && !this.getCurrentSquare().isWallTo(zombie.getCurrentSquare()) && !this.getCurrentSquare().isWindowTo(zombie.getCurrentSquare())) {
			if (this.getVehicle() == null) {
				boolean boolean1 = this.DoSwingCollisionBoneCheck(zombie, zombie.getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Hand", -1), 1.0F);
				if (boolean1) {
					zombie.playSound("ZombieCrawlLungeHit");
					this.lungeFallTimer = 200.0F;
					this.setIsAiming(false);
					boolean boolean2 = false;
					byte byte1 = 30;
					int int1 = byte1 + this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 3;
					int1 += this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5;
					int1 -= this.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
					BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
					if (bodyPart.getAdditionalPain(true) > 20.0F) {
						int1 = (int)((float)int1 + (bodyPart.getAdditionalPain(true) - 20.0F) / 10.0F);
					}

					if (this.Traits.Clumsy.isSet()) {
						int1 += 10;
					}

					if (this.Traits.Graceful.isSet()) {
						int1 -= 10;
					}

					if (this.Traits.VeryUnderweight.isSet()) {
						int1 += 20;
					}

					if (this.Traits.Underweight.isSet()) {
						int1 += 10;
					}

					if (this.Traits.Obese.isSet()) {
						int1 -= 10;
					}

					if (this.Traits.Overweight.isSet()) {
						int1 -= 5;
					}

					int1 = Math.max(5, int1);
					this.clearVariable("BumpFallType");
					this.setBumpType("stagger");
					if (Rand.Next(100) < int1) {
						boolean2 = true;
					}

					this.setBumpDone(false);
					this.setBumpFall(boolean2);
					if (zombie.isBehind(this)) {
						this.setBumpFallType("pushedBehind");
					} else {
						this.setBumpFallType("pushedFront");
					}

					this.actionContext.reportEvent("wasBumped");
				}
			}
		}
	}

	public boolean DoSwingCollisionBoneCheck(IsoGameCharacter gameCharacter, int int1, float float1) {
		Model.BoneToWorldCoords(gameCharacter, int1, tempVectorBonePos);
		float float2 = IsoUtils.DistanceToSquared(tempVectorBonePos.x, tempVectorBonePos.y, this.x, this.y);
		return float2 < float1 * float1;
	}

	public boolean isInvincible() {
		return this.invincible;
	}

	public void setInvincible(boolean boolean1) {
		this.invincible = boolean1;
	}

	public BaseVehicle getNearVehicle() {
		if (this.getVehicle() != null) {
			return null;
		} else {
			int int1 = ((int)this.x - 4) / 10 - 1;
			int int2 = ((int)this.y - 4) / 10 - 1;
			int int3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F)) + 1;
			int int4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F)) + 1;
			for (int int5 = int2; int5 < int4; ++int5) {
				for (int int6 = int1; int6 < int3; ++int6) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunk(int6, int5);
					if (chunk != null) {
						for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
							BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
							if (baseVehicle.getScript() != null && (int)this.getZ() == (int)baseVehicle.getZ() && (!(this instanceof IsoPlayer) || !((IsoPlayer)this).isLocalPlayer() || baseVehicle.getTargetAlpha(((IsoPlayer)this).PlayerIndex) != 0.0F) && !(this.DistToSquared((float)((int)baseVehicle.x), (float)((int)baseVehicle.y)) >= 16.0F)) {
								return baseVehicle;
							}
						}
					}
				}
			}

			return null;
		}
	}

	private IsoGridSquare getSolidFloorAt(int int1, int int2, int int3) {
		while (int3 >= 0) {
			IsoGridSquare square = this.getCell().getGridSquare(int1, int2, int3);
			if (square != null && square.TreatAsSolidFloor()) {
				return square;
			}

			--int3;
		}

		return null;
	}

	public void dropHeavyItems() {
		IsoGridSquare square = this.getCurrentSquare();
		if (square != null) {
			InventoryItem inventoryItem = this.getPrimaryHandItem();
			InventoryItem inventoryItem2 = this.getSecondaryHandItem();
			if (inventoryItem != null || inventoryItem2 != null) {
				square = this.getSolidFloorAt(square.x, square.y, square.z);
				if (square != null) {
					boolean boolean1 = inventoryItem == inventoryItem2;
					float float1;
					float float2;
					float float3;
					if (this.isHeavyItem(inventoryItem)) {
						float1 = Rand.Next(0.1F, 0.9F);
						float2 = Rand.Next(0.1F, 0.9F);
						float3 = square.getApparentZ(float1, float2) - (float)square.getZ();
						this.setPrimaryHandItem((InventoryItem)null);
						this.getInventory().DoRemoveItem(inventoryItem);
						square.AddWorldInventoryItem(inventoryItem, float1, float2, float3);
						LuaEventManager.triggerEvent("OnContainerUpdate");
						LuaEventManager.triggerEvent("onItemFall", inventoryItem);
					}

					if (this.isHeavyItem(inventoryItem2)) {
						this.setSecondaryHandItem((InventoryItem)null);
						if (!boolean1) {
							float1 = Rand.Next(0.1F, 0.9F);
							float2 = Rand.Next(0.1F, 0.9F);
							float3 = square.getApparentZ(float1, float2) - (float)square.getZ();
							this.getInventory().DoRemoveItem(inventoryItem2);
							square.AddWorldInventoryItem(inventoryItem2, float1, float2, float3);
							LuaEventManager.triggerEvent("OnContainerUpdate");
							LuaEventManager.triggerEvent("onItemFall", inventoryItem2);
						}
					}
				}
			}
		}
	}

	public boolean isHeavyItem(InventoryItem inventoryItem) {
		if (inventoryItem == null) {
			return false;
		} else if (inventoryItem instanceof InventoryContainer) {
			return true;
		} else if (inventoryItem.hasTag("HeavyItem")) {
			return true;
		} else {
			return !inventoryItem.getType().equals("CorpseMale") && !inventoryItem.getType().equals("CorpseFemale") ? inventoryItem.getType().equals("Generator") : true;
		}
	}

	public boolean isCanShout() {
		return this.canShout;
	}

	public void setCanShout(boolean boolean1) {
		this.canShout = boolean1;
	}

	public boolean isUnlimitedEndurance() {
		return this.unlimitedEndurance;
	}

	public void setUnlimitedEndurance(boolean boolean1) {
		this.unlimitedEndurance = boolean1;
	}

	private void addActiveLightItem(InventoryItem inventoryItem, ArrayList arrayList) {
		if (inventoryItem != null && inventoryItem.isEmittingLight() && !arrayList.contains(inventoryItem)) {
			arrayList.add(inventoryItem);
		}
	}

	public ArrayList getActiveLightItems(ArrayList arrayList) {
		this.addActiveLightItem(this.getSecondaryHandItem(), arrayList);
		this.addActiveLightItem(this.getPrimaryHandItem(), arrayList);
		AttachedItems attachedItems = this.getAttachedItems();
		for (int int1 = 0; int1 < attachedItems.size(); ++int1) {
			InventoryItem inventoryItem = attachedItems.getItemByIndex(int1);
			this.addActiveLightItem(inventoryItem, arrayList);
		}

		return arrayList;
	}

	public SleepingEventData getOrCreateSleepingEventData() {
		if (this.m_sleepingEventData == null) {
			this.m_sleepingEventData = new SleepingEventData();
		}

		return this.m_sleepingEventData;
	}

	public void playEmote(String string) {
		this.setVariable("emote", string);
		this.actionContext.reportEvent("EventEmote");
	}

	public String getAnimationStateName() {
		return this.advancedAnimator.getCurrentStateName();
	}

	public String getActionStateName() {
		return this.actionContext.getCurrentStateName();
	}

	public boolean shouldWaitToStartTimedAction() {
		if (this.isSitOnGround()) {
			AdvancedAnimator advancedAnimator = this.getAdvancedAnimator();
			if (advancedAnimator.getRootLayer() == null) {
				return false;
			} else if (advancedAnimator.animSet != null && advancedAnimator.animSet.containsState("sitonground")) {
				AnimState animState = advancedAnimator.animSet.GetState("sitonground");
				if (!PZArrayUtil.contains(animState.m_Nodes, (var0)->{
					return "sit_action".equalsIgnoreCase(var0.m_Name);
				})) {
					return false;
				} else {
					LiveAnimNode liveAnimNode = (LiveAnimNode)PZArrayUtil.find(advancedAnimator.getRootLayer().getLiveAnimNodes(), (var0)->{
						return var0.isActive() && "sit_action".equalsIgnoreCase(var0.getName());
					});

					return liveAnimNode == null || !liveAnimNode.isMainAnimActive();
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void setPersistentOutfitID(int int1) {
		this.setPersistentOutfitID(int1, false);
	}

	public void setPersistentOutfitID(int int1, boolean boolean1) {
		this.m_persistentOutfitId = int1;
		this.m_bPersistentOutfitInit = boolean1;
	}

	public int getPersistentOutfitID() {
		return this.m_persistentOutfitId;
	}

	public boolean isPersistentOutfitInit() {
		return this.m_bPersistentOutfitInit;
	}

	public boolean isDoingActionThatCanBeCancelled() {
		return false;
	}

	public boolean isDoDeathSound() {
		return this.doDeathSound;
	}

	public void setDoDeathSound(boolean boolean1) {
		this.doDeathSound = boolean1;
	}

	public void updateEquippedRadioFreq() {
		this.invRadioFreq.clear();
		int int1;
		for (int1 = 0; int1 < this.getInventory().getItems().size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.getInventory().getItems().get(int1);
			if (inventoryItem instanceof Radio) {
				Radio radio = (Radio)inventoryItem;
				if (radio.getDeviceData() != null && radio.getDeviceData().getIsTurnedOn() && !radio.getDeviceData().getMicIsMuted() && !this.invRadioFreq.contains(radio.getDeviceData().getChannel())) {
					this.invRadioFreq.add(radio.getDeviceData().getChannel());
				}
			}
		}

		for (int1 = 0; int1 < this.invRadioFreq.size(); ++int1) {
			System.out.println(this.invRadioFreq.get(int1));
		}

		if (this instanceof IsoPlayer && GameClient.bClient) {
			GameClient.sendEquippedRadioFreq((IsoPlayer)this);
		}
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

	public void playBloodSplatterSound() {
		if (this.getEmitter().isPlaying("BloodSplatter")) {
		}

		this.getEmitter().playSoundImpl("BloodSplatter", this);
	}

	public void setIgnoreAimingInput(boolean boolean1) {
		this.ignoreAimingInput = boolean1;
	}

	public void addBlood(float float1) {
		if (!((float)Rand.Next(10) > float1)) {
			if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				int int1 = Rand.Next(4, 10);
				if (int1 < 1) {
					int1 = 1;
				}

				if (Core.bLastStand) {
					int1 *= 3;
				}

				switch (SandboxOptions.instance.BloodLevel.getValue()) {
				case 2: 
					int1 /= 2;
				
				case 3: 
				
				default: 
					break;
				
				case 4: 
					int1 *= 2;
					break;
				
				case 5: 
					int1 *= 5;
				
				}

				for (int int2 = 0; int2 < int1; ++int2) {
					this.splatBlood(2, 0.3F);
				}
			}

			if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				this.splatBloodFloorBig();
				this.playBloodSplatterSound();
			}
		}
	}

	public boolean isKnockedDown() {
		return this.bKnockedDown;
	}

	public void setKnockedDown(boolean boolean1) {
		this.bKnockedDown = boolean1;
	}

	public void writeInventory(ByteBuffer byteBuffer) {
		String string = this.isFemale() ? "inventoryfemale" : "inventorymale";
		GameWindow.WriteString(byteBuffer, string);
		if (this.getInventory() != null) {
			byteBuffer.put((byte)1);
			try {
				ArrayList arrayList = this.getInventory().save(byteBuffer);
				WornItems wornItems = this.getWornItems();
				int int1;
				if (wornItems == null) {
					byte byte1 = 0;
					byteBuffer.put((byte)byte1);
				} else {
					int int2 = wornItems.size();
					if (int2 > 127) {
						DebugLog.Multiplayer.warn("Too many worn items");
						int2 = 127;
					}

					byteBuffer.put((byte)int2);
					for (int1 = 0; int1 < int2; ++int1) {
						WornItem wornItem = wornItems.get(int1);
						GameWindow.WriteString(byteBuffer, wornItem.getLocation());
						byteBuffer.putShort((short)arrayList.indexOf(wornItem.getItem()));
					}
				}

				AttachedItems attachedItems = this.getAttachedItems();
				if (attachedItems == null) {
					boolean boolean1 = false;
					byteBuffer.put((byte)0);
				} else {
					int1 = attachedItems.size();
					if (int1 > 127) {
						DebugLog.Multiplayer.warn("Too many attached items");
						int1 = 127;
					}

					byteBuffer.put((byte)int1);
					for (int int3 = 0; int3 < int1; ++int3) {
						AttachedItem attachedItem = attachedItems.get(int3);
						GameWindow.WriteString(byteBuffer, attachedItem.getLocation());
						byteBuffer.putShort((short)arrayList.indexOf(attachedItem.getItem()));
					}
				}
			} catch (IOException ioException) {
				DebugLog.Multiplayer.printException(ioException, "WriteInventory error for character " + this.getOnlineID(), LogSeverity.Error);
			}
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public String readInventory(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			try {
				ArrayList arrayList = this.getInventory().load(byteBuffer, IsoWorld.getWorldVersion());
				byte byte1 = byteBuffer.get();
				for (int int1 = 0; int1 < byte1; ++int1) {
					String string2 = GameWindow.ReadStringUTF(byteBuffer);
					short short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size() && this.getBodyLocationGroup().getLocation(string2) != null) {
						this.getWornItems().setItem(string2, (InventoryItem)arrayList.get(short1));
					}
				}

				byte byte2 = byteBuffer.get();
				for (int int2 = 0; int2 < byte2; ++int2) {
					String string3 = GameWindow.ReadStringUTF(byteBuffer);
					short short2 = byteBuffer.getShort();
					if (short2 >= 0 && short2 < arrayList.size() && this.getAttachedLocationGroup().getLocation(string3) != null) {
						this.getAttachedItems().setItem(string3, (InventoryItem)arrayList.get(short2));
					}
				}
			} catch (IOException ioException) {
				DebugLog.Multiplayer.printException(ioException, "ReadInventory error for character " + this.getOnlineID(), LogSeverity.Error);
			}
		}

		return string;
	}

	public void Kill(IsoGameCharacter gameCharacter) {
		DebugLog.Death.trace("id=%d", this.getOnlineID());
		this.setAttackedBy(gameCharacter);
		this.setHealth(0.0F);
		this.setOnKillDone(true);
	}

	public boolean shouldDoInventory() {
		return true;
	}

	public void die() {
		if (!this.isOnDeathDone()) {
			if (GameClient.bClient) {
				if (this.shouldDoInventory()) {
					this.becomeCorpse();
				} else {
					this.getNetworkCharacterAI().processDeadBody();
				}
			} else {
				this.becomeCorpse();
			}
		}
	}

	public void becomeCorpse() {
		DebugLog.Death.trace("id=%d", this.getOnlineID());
		this.Kill(this.getAttackedBy());
		this.setOnDeathDone(true);
	}

	public boolean shouldBecomeCorpse() {
		if (GameClient.bClient || GameServer.bServer) {
			if (this.getHitReactionNetworkAI().isSetup() || this.getHitReactionNetworkAI().isStarted()) {
				return false;
			}

			if (GameServer.bServer) {
				return this.getNetworkCharacterAI().isSetDeadBody();
			}

			if (GameClient.bClient) {
				return this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(PlayerOnGroundState.instance());
			}
		}

		return true;
	}

	public HitReactionNetworkAI getHitReactionNetworkAI() {
		return null;
	}

	public NetworkCharacterAI getNetworkCharacterAI() {
		return null;
	}

	public boolean isLocal() {
		return !GameClient.bClient && !GameServer.bServer;
	}

	public boolean isVehicleCollisionActive(BaseVehicle baseVehicle) {
		if (!GameClient.bClient) {
			return false;
		} else if (!this.isAlive()) {
			return false;
		} else if (baseVehicle == null) {
			return false;
		} else if (!baseVehicle.shouldCollideWithCharacters()) {
			return false;
		} else if (baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
			return false;
		} else if (baseVehicle.isEngineRunning() || baseVehicle.getVehicleTowing() != null && baseVehicle.getVehicleTowing().isEngineRunning() || baseVehicle.getVehicleTowedBy() != null && baseVehicle.getVehicleTowedBy().isEngineRunning()) {
			if (baseVehicle.getDriver() == null && (baseVehicle.getVehicleTowing() == null || baseVehicle.getVehicleTowing().getDriver() == null) && (baseVehicle.getVehicleTowedBy() == null || baseVehicle.getVehicleTowedBy().getDriver() == null)) {
				return false;
			} else if (!(Math.abs(baseVehicle.x - this.x) < 0.01F) && !(Math.abs(baseVehicle.y - this.y) < 0.01F)) {
				return (!this.isKnockedDown() || this.isOnFloor()) && (this.getHitReactionNetworkAI() == null || !this.getHitReactionNetworkAI().isStarted());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void doHitByVehicle(BaseVehicle baseVehicle, BaseVehicle.HitVars hitVars) {
		if (GameClient.bClient) {
			IsoPlayer player = (IsoPlayer)GameClient.IDToPlayerMap.get(baseVehicle.getNetPlayerId());
			if (player != null) {
				if (player.isLocal()) {
					SoundManager.instance.PlayWorldSound("VehicleHitCharacter", this.getCurrentSquare(), 0.0F, 20.0F, 0.9F, true);
					float float1 = this.Hit(baseVehicle, hitVars.hitSpeed, hitVars.isTargetHitFromBehind, -hitVars.targetImpulse.x, -hitVars.targetImpulse.z);
					GameClient.sendHitVehicle(player, this, baseVehicle, float1, hitVars.isTargetHitFromBehind, hitVars.vehicleDamage, hitVars.hitSpeed, hitVars.isVehicleHitFromFront);
				} else {
					this.getNetworkCharacterAI().resetVehicleHitTimeout();
				}
			}
		} else if (!GameServer.bServer) {
			BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.x, this.y, this.z);
			long long1 = baseSoundEmitter.playSound("VehicleHitCharacter");
			baseSoundEmitter.setParameterValue(long1, FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
			this.Hit(baseVehicle, hitVars.hitSpeed, hitVars.isTargetHitFromBehind, -hitVars.targetImpulse.x, -hitVars.targetImpulse.z);
		}
	}

	public boolean isSkipResolveCollision() {
		return this instanceof IsoZombie && (this.isCurrentState(ZombieHitReactionState.instance()) || this.isCurrentState(ZombieFallDownState.instance()) || this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(StaggerBackState.instance())) || this instanceof IsoPlayer && !this.isLocal() && (this.isCurrentState(PlayerFallDownState.instance()) || this.isCurrentState(BumpedState.instance()) || this.isCurrentState(PlayerKnockedDown.instance()) || this.isCurrentState(PlayerHitReactionState.instance()) || this.isCurrentState(PlayerHitReactionPVPState.instance()) || this.isCurrentState(PlayerOnGroundState.instance()));
	}

	public boolean isAttackAnim() {
		return this.attackAnim;
	}

	public void setAttackAnim(boolean boolean1) {
		this.attackAnim = boolean1;
	}

	public Float getNextAnimationTranslationLength() {
		if (this.getActionContext() != null && this.getAnimationPlayer() != null && this.getAdvancedAnimator() != null) {
			ActionState actionState = this.getActionContext().getNextState();
			if (actionState != null && !StringUtils.isNullOrEmpty(actionState.getName())) {
				ArrayList arrayList = new ArrayList();
				this.getAdvancedAnimator().animSet.GetState(actionState.getName()).getAnimNodes(this, arrayList);
				Iterator iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					AnimNode animNode = (AnimNode)iterator.next();
					if (!StringUtils.isNullOrEmpty(animNode.m_AnimName)) {
						AnimationClip animationClip = (AnimationClip)this.getAnimationPlayer().getSkinningData().AnimationClips.get(animNode.m_AnimName);
						if (animationClip != null) {
							return animationClip.getTranslationLength(animNode.m_deferredBoneAxis);
						}
					}
				}
			}
		}

		return null;
	}

	public Float calcHitDir(IsoGameCharacter gameCharacter, HandWeapon handWeapon, Vector2 vector2) {
		Float Float1 = this.getNextAnimationTranslationLength();
		vector2.set(this.getX() - gameCharacter.getX(), this.getY() - gameCharacter.getY()).normalize();
		if (Float1 == null) {
			vector2.setLength(this.getHitForce() * 0.1F);
			vector2.scale(handWeapon.getPushBackMod());
			vector2.rotate(handWeapon.HitAngleMod);
		} else {
			vector2.scale(Float1);
		}

		return null;
	}

	public void calcHitDir(Vector2 vector2) {
		vector2.set(this.getHitDir());
		vector2.setLength(this.getHitForce());
	}

	public Safety getSafety() {
		return this.safety;
	}

	public void setSafety(Safety safety) {
		this.safety.copyFrom(safety);
	}

	public void burnCorpse(IsoDeadBody deadBody) {
		if (GameClient.bClient) {
			GameClient.sendBurnCorpse(this.getOnlineID(), deadBody.getObjectID());
		} else {
			IsoFireManager.StartFire(deadBody.getCell(), deadBody.getSquare(), true, 100, 700);
		}
	}

	public class XP {
		public int level = 0;
		public int lastlevel = 0;
		public float TotalXP = 0.0F;
		public HashMap XPMap = new HashMap();
		private float lastXPSumm = 0.0F;
		private long lastXPTime = System.currentTimeMillis();
		private float lastXPGrowthRate = 0.0F;
		public static final float MaxXPGrowthRate = 1000.0F;
		public HashMap XPMapMultiplier = new HashMap();
		IsoGameCharacter chr = null;

		public XP(IsoGameCharacter gameCharacter) {
			this.chr = gameCharacter;
		}

		public void update() {
			if (GameServer.bServer && this.chr instanceof IsoPlayer) {
				if (System.currentTimeMillis() - this.lastXPTime > 60000L) {
					this.lastXPTime = System.currentTimeMillis();
					float float1 = 0.0F;
					Float Float1;
					for (Iterator iterator = this.XPMap.values().iterator(); iterator.hasNext(); float1 += Float1) {
						Float1 = (Float)iterator.next();
					}

					this.lastXPGrowthRate = float1 - this.lastXPSumm;
					this.lastXPSumm = float1;
					if ((double)this.lastXPGrowthRate > 1000.0 * SandboxOptions.instance.XpMultiplier.getValue() * ServerOptions.instance.AntiCheatProtectionType9ThresholdMultiplier.getValue()) {
						UdpConnection udpConnection = GameServer.getConnectionFromPlayer((IsoPlayer)this.chr);
						if (ServerOptions.instance.AntiCheatProtectionType9.getValue() && PacketValidator.checkUser(udpConnection)) {
							PacketValidator.doKickUser(udpConnection, this.getClass().getSimpleName(), "Type9", (String)null);
						} else if ((double)this.lastXPGrowthRate > 1000.0 * SandboxOptions.instance.XpMultiplier.getValue() * ServerOptions.instance.AntiCheatProtectionType9ThresholdMultiplier.getValue() / 2.0) {
							PacketValidator.doLogUser(udpConnection, Userlog.UserlogType.SuspiciousActivity, this.getClass().getSimpleName(), "Type9");
						}
					}
				}
			}
		}

		public void addXpMultiplier(PerkFactory.Perk perk, float float1, int int1, int int2) {
			IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.XPMapMultiplier.get(perk);
			if (xPMultiplier == null) {
				xPMultiplier = new IsoGameCharacter.XPMultiplier();
			}

			xPMultiplier.multiplier = float1;
			xPMultiplier.minLevel = int1;
			xPMultiplier.maxLevel = int2;
			this.XPMapMultiplier.put(perk, xPMultiplier);
		}

		public HashMap getMultiplierMap() {
			return this.XPMapMultiplier;
		}

		public float getMultiplier(PerkFactory.Perk perk) {
			IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.XPMapMultiplier.get(perk);
			return xPMultiplier == null ? 0.0F : xPMultiplier.multiplier;
		}

		public int getPerkBoost(PerkFactory.Perk perk) {
			return IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(perk) != null ? (Integer)IsoGameCharacter.this.getDescriptor().getXPBoostMap().get(perk) : 0;
		}

		public void setPerkBoost(PerkFactory.Perk perk, int int1) {
			if (perk != null && perk != PerkFactory.Perks.None && perk != PerkFactory.Perks.MAX) {
				int1 = PZMath.clamp(int1, 0, 10);
				if (int1 == 0) {
					IsoGameCharacter.this.getDescriptor().getXPBoostMap().remove(perk);
				} else {
					IsoGameCharacter.this.getDescriptor().getXPBoostMap().put(perk, int1);
				}
			}
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

		public void AddXP(PerkFactory.Perk perk, float float1) {
			if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer()) {
				this.AddXP(perk, float1, true, true, false);
			}
		}

		public void AddXPNoMultiplier(PerkFactory.Perk perk, float float1) {
			IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.getMultiplierMap().remove(perk);
			try {
				this.AddXP(perk, float1);
			} finally {
				if (xPMultiplier != null) {
					this.getMultiplierMap().put(perk, xPMultiplier);
				}
			}
		}

		public void AddXP(PerkFactory.Perk perk, float float1, boolean boolean1, boolean boolean2, boolean boolean3) {
			if (!boolean3 && GameClient.bClient && this.chr instanceof IsoPlayer) {
				GameClient.instance.sendAddXp((IsoPlayer)this.chr, perk, (int)float1);
			}

			PerkFactory.Perk perk2 = null;
			for (int int1 = 0; int1 < PerkFactory.PerkList.size(); ++int1) {
				PerkFactory.Perk perk3 = (PerkFactory.Perk)PerkFactory.PerkList.get(int1);
				if (perk3.getType() == perk) {
					perk2 = perk3;
					break;
				}
			}

			if (perk2.getType() != PerkFactory.Perks.Fitness || !(this.chr instanceof IsoPlayer) || ((IsoPlayer)this.chr).getNutrition().canAddFitnessXp()) {
				if (perk2.getType() == PerkFactory.Perks.Strength && this.chr instanceof IsoPlayer) {
					if (((IsoPlayer)this.chr).getNutrition().getProteins() > 50.0F && ((IsoPlayer)this.chr).getNutrition().getProteins() < 300.0F) {
						float1 = (float)((double)float1 * 1.5);
					}

					if (((IsoPlayer)this.chr).getNutrition().getProteins() < -300.0F) {
						float1 = (float)((double)float1 * 0.7);
					}
				}

				float float2 = this.getXP(perk);
				float float3 = perk2.getTotalXpForLevel(10);
				if (!(float1 >= 0.0F) || !(float2 >= float3)) {
					float float4 = 1.0F;
					float float5;
					if (boolean2) {
						boolean boolean4 = false;
						Iterator iterator = IsoGameCharacter.this.getDescriptor().getXPBoostMap().entrySet().iterator();
						label191: while (true) {
							while (true) {
								Entry entry;
								do {
									if (!iterator.hasNext()) {
										if (!boolean4 && !this.isSkillExcludedFromSpeedReduction(perk2.getType())) {
											float4 = 0.25F;
										}

										if (IsoGameCharacter.this.Traits.FastLearner.isSet() && !this.isSkillExcludedFromSpeedIncrease(perk2.getType())) {
											float4 *= 1.3F;
										}

										if (IsoGameCharacter.this.Traits.SlowLearner.isSet() && !this.isSkillExcludedFromSpeedReduction(perk2.getType())) {
											float4 *= 0.7F;
										}

										if (IsoGameCharacter.this.Traits.Pacifist.isSet()) {
											if (perk2.getType() != PerkFactory.Perks.SmallBlade && perk2.getType() != PerkFactory.Perks.LongBlade && perk2.getType() != PerkFactory.Perks.SmallBlunt && perk2.getType() != PerkFactory.Perks.Spear && perk2.getType() != PerkFactory.Perks.Maintenance && perk2.getType() != PerkFactory.Perks.Blunt && perk2.getType() != PerkFactory.Perks.Axe) {
												if (perk2.getType() == PerkFactory.Perks.Aiming) {
													float4 *= 0.75F;
												}
											} else {
												float4 *= 0.75F;
											}
										}

										float1 *= float4;
										float5 = this.getMultiplier(perk);
										if (float5 > 1.0F) {
											float1 *= float5;
										}

										if (!perk2.isPassiv()) {
											float1 = (float)((double)float1 * SandboxOptions.instance.XpMultiplier.getValue());
										} else if (perk2.isPassiv() && SandboxOptions.instance.XpMultiplierAffectsPassive.getValue()) {
											float1 = (float)((double)float1 * SandboxOptions.instance.XpMultiplier.getValue());
										}

										break label191;
									}

									entry = (Entry)iterator.next();
								}					 while (entry.getKey() != perk2.getType());

								boolean4 = true;
								if ((Integer)entry.getValue() == 0 && !this.isSkillExcludedFromSpeedReduction((PerkFactory.Perk)entry.getKey())) {
									float4 *= 0.25F;
								} else if ((Integer)entry.getValue() == 1 && entry.getKey() == PerkFactory.Perks.Sprinting) {
									float4 = (float)((double)float4 * 1.25);
								} else if ((Integer)entry.getValue() == 1) {
									float4 = (float)((double)float4 * 1.0);
								} else if ((Integer)entry.getValue() == 2 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perk)entry.getKey())) {
									float4 = (float)((double)float4 * 1.33);
								} else if ((Integer)entry.getValue() >= 3 && !this.isSkillExcludedFromSpeedIncrease((PerkFactory.Perk)entry.getKey())) {
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

					this.XPMap.put(perk, float6);
					for (float5 = perk2.getTotalXpForLevel(this.chr.getPerkLevel(perk2) + 1); float2 < float5 && float6 >= float5; float5 = perk2.getTotalXpForLevel(this.chr.getPerkLevel(perk2) + 1)) {
						IsoGameCharacter.this.LevelPerk(perk);
						if (this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && !this.chr.getEmitter().isPlaying("GainExperienceLevel")) {
							this.chr.getEmitter().playSoundImpl("GainExperienceLevel", (IsoObject)null);
						}

						if (this.chr.getPerkLevel(perk2) >= 10) {
							break;
						}
					}

					IsoGameCharacter.XPMultiplier xPMultiplier = (IsoGameCharacter.XPMultiplier)this.getMultiplierMap().get(perk2);
					if (xPMultiplier != null) {
						float float7 = perk2.getTotalXpForLevel(xPMultiplier.minLevel - 1);
						float float8 = perk2.getTotalXpForLevel(xPMultiplier.maxLevel);
						if (float2 >= float7 && float6 < float7 || float2 < float8 && float6 >= float8) {
							this.getMultiplierMap().remove(perk2);
						}
					}

					if (boolean1) {
						LuaEventManager.triggerEventGarbage("AddXP", this.chr, perk, float1);
					}
				}
			}
		}

		private boolean isSkillExcludedFromSpeedReduction(PerkFactory.Perk perk) {
			if (perk == PerkFactory.Perks.Sprinting) {
				return true;
			} else if (perk == PerkFactory.Perks.Fitness) {
				return true;
			} else {
				return perk == PerkFactory.Perks.Strength;
			}
		}

		private boolean isSkillExcludedFromSpeedIncrease(PerkFactory.Perk perk) {
			if (perk == PerkFactory.Perks.Fitness) {
				return true;
			} else {
				return perk == PerkFactory.Perks.Strength;
			}
		}

		public float getXP(PerkFactory.Perk perk) {
			return this.XPMap.containsKey(perk) ? (Float)this.XPMap.get(perk) : 0.0F;
		}

		@Deprecated
		public void AddXP(HandWeapon handWeapon, int int1) {
		}

		public void setTotalXP(float float1) {
			this.TotalXP = float1;
		}

		private void savePerk(ByteBuffer byteBuffer, PerkFactory.Perk perk) throws IOException {
			GameWindow.WriteStringUTF(byteBuffer, perk == null ? "" : perk.getId());
		}

		private PerkFactory.Perk loadPerk(ByteBuffer byteBuffer, int int1) throws IOException {
			PerkFactory.Perk perk;
			if (int1 >= 152) {
				String string = GameWindow.ReadStringUTF(byteBuffer);
				perk = PerkFactory.Perks.FromString(string);
				return perk == PerkFactory.Perks.MAX ? null : perk;
			} else {
				int int2 = byteBuffer.getInt();
				if (int2 >= 0 && int2 < PerkFactory.Perks.MAX.index()) {
					perk = PerkFactory.Perks.fromIndex(int2);
					return perk == PerkFactory.Perks.MAX ? null : perk;
				} else {
					return null;
				}
			}
		}

		public void recalcSumm() {
			float float1 = 0.0F;
			Float Float1;
			for (Iterator iterator = this.XPMap.values().iterator(); iterator.hasNext(); float1 += Float1) {
				Float1 = (Float)iterator.next();
			}

			this.lastXPSumm = float1;
			this.lastXPTime = System.currentTimeMillis();
			this.lastXPGrowthRate = 0.0F;
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
					DebugLog.General.error("unknown trait \"" + string + "\"");
				}
			}

			this.TotalXP = byteBuffer.getFloat();
			this.level = byteBuffer.getInt();
			this.lastlevel = byteBuffer.getInt();
			this.XPMap.clear();
			int3 = byteBuffer.getInt();
			int int4;
			for (int4 = 0; int4 < int3; ++int4) {
				PerkFactory.Perk perk = this.loadPerk(byteBuffer, int1);
				float float1 = byteBuffer.getFloat();
				if (perk != null) {
					this.XPMap.put(perk, float1);
				}
			}

			int int5;
			if (int1 < 162) {
				int4 = byteBuffer.getInt();
				for (int5 = 0; int5 < int4; ++int5) {
					this.loadPerk(byteBuffer, int1);
				}
			}

			IsoGameCharacter.this.PerkList.clear();
			int4 = byteBuffer.getInt();
			for (int5 = 0; int5 < int4; ++int5) {
				PerkFactory.Perk perk2 = this.loadPerk(byteBuffer, int1);
				int int6 = byteBuffer.getInt();
				if (perk2 != null) {
					IsoGameCharacter.PerkInfo perkInfo = IsoGameCharacter.this.new PerkInfo();
					perkInfo.perk = perk2;
					perkInfo.level = int6;
					IsoGameCharacter.this.PerkList.add(perkInfo);
				}
			}

			int5 = byteBuffer.getInt();
			for (int int7 = 0; int7 < int5; ++int7) {
				PerkFactory.Perk perk3 = this.loadPerk(byteBuffer, int1);
				float float2 = byteBuffer.getFloat();
				byte byte1 = byteBuffer.get();
				byte byte2 = byteBuffer.get();
				if (perk3 != null) {
					this.addXpMultiplier(perk3, float2, byte1, byte2);
				}
			}

			if (this.TotalXP > (float)IsoGameCharacter.this.getXpForLevel(this.getLevel() + 1)) {
				this.setTotalXP((float)this.chr.getXpForLevel(this.getLevel()));
			}

			this.recalcSumm();
		}

		public void save(ByteBuffer byteBuffer) throws IOException {
			byteBuffer.putInt(this.chr.Traits.size());
			for (int int1 = 0; int1 < this.chr.Traits.size(); ++int1) {
				GameWindow.WriteString(byteBuffer, this.chr.Traits.get(int1));
			}

			byteBuffer.putFloat(this.TotalXP);
			byteBuffer.putInt(this.level);
			byteBuffer.putInt(this.lastlevel);
			byteBuffer.putInt(this.XPMap.size());
			Iterator iterator = this.XPMap.entrySet().iterator();
			while (iterator != null && iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				this.savePerk(byteBuffer, (PerkFactory.Perk)entry.getKey());
				byteBuffer.putFloat((Float)entry.getValue());
			}

			byteBuffer.putInt(IsoGameCharacter.this.PerkList.size());
			for (int int2 = 0; int2 < IsoGameCharacter.this.PerkList.size(); ++int2) {
				IsoGameCharacter.PerkInfo perkInfo = (IsoGameCharacter.PerkInfo)IsoGameCharacter.this.PerkList.get(int2);
				this.savePerk(byteBuffer, perkInfo.perk);
				byteBuffer.putInt(perkInfo.level);
			}

			byteBuffer.putInt(this.XPMapMultiplier.size());
			Iterator iterator2 = this.XPMapMultiplier.entrySet().iterator();
			while (iterator2 != null && iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				this.savePerk(byteBuffer, (PerkFactory.Perk)entry2.getKey());
				byteBuffer.putFloat(((IsoGameCharacter.XPMultiplier)entry2.getValue()).multiplier);
				byteBuffer.put((byte)((IsoGameCharacter.XPMultiplier)entry2.getValue()).minLevel);
				byteBuffer.put((byte)((IsoGameCharacter.XPMultiplier)entry2.getValue()).maxLevel);
			}
		}

		public void setXPToLevel(PerkFactory.Perk perk, int int1) {
			PerkFactory.Perk perk2 = null;
			for (int int2 = 0; int2 < PerkFactory.PerkList.size(); ++int2) {
				PerkFactory.Perk perk3 = (PerkFactory.Perk)PerkFactory.PerkList.get(int2);
				if (perk3.getType() == perk) {
					perk2 = perk3;
					break;
				}
			}

			if (perk2 != null) {
				this.XPMap.put(perk, perk2.getTotalXpForLevel(int1));
			}
		}
	}

	public class CharacterTraits extends TraitCollection {
		public final TraitCollection.TraitSlot Obese = this.getTraitSlot("Obese");
		public final TraitCollection.TraitSlot Athletic = this.getTraitSlot("Athletic");
		public final TraitCollection.TraitSlot Overweight = this.getTraitSlot("Overweight");
		public final TraitCollection.TraitSlot Unfit = this.getTraitSlot("Unfit");
		public final TraitCollection.TraitSlot Emaciated = this.getTraitSlot("Emaciated");
		public final TraitCollection.TraitSlot Graceful = this.getTraitSlot("Graceful");
		public final TraitCollection.TraitSlot Clumsy = this.getTraitSlot("Clumsy");
		public final TraitCollection.TraitSlot Strong = this.getTraitSlot("Strong");
		public final TraitCollection.TraitSlot Weak = this.getTraitSlot("Weak");
		public final TraitCollection.TraitSlot VeryUnderweight = this.getTraitSlot("Very Underweight");
		public final TraitCollection.TraitSlot Underweight = this.getTraitSlot("Underweight");
		public final TraitCollection.TraitSlot FastHealer = this.getTraitSlot("FastHealer");
		public final TraitCollection.TraitSlot SlowHealer = this.getTraitSlot("SlowHealer");
		public final TraitCollection.TraitSlot ShortSighted = this.getTraitSlot("ShortSighted");
		public final TraitCollection.TraitSlot EagleEyed = this.getTraitSlot("EagleEyed");
		public final TraitCollection.TraitSlot Agoraphobic = this.getTraitSlot("Agoraphobic");
		public final TraitCollection.TraitSlot Claustophobic = this.getTraitSlot("Claustophobic");
		public final TraitCollection.TraitSlot AdrenalineJunkie = this.getTraitSlot("AdrenalineJunkie");
		public final TraitCollection.TraitSlot OutOfShape = this.getTraitSlot("Out of Shape");
		public final TraitCollection.TraitSlot HighThirst = this.getTraitSlot("HighThirst");
		public final TraitCollection.TraitSlot LowThirst = this.getTraitSlot("LowThirst");
		public final TraitCollection.TraitSlot HeartyAppitite = this.getTraitSlot("HeartyAppitite");
		public final TraitCollection.TraitSlot LightEater = this.getTraitSlot("LightEater");
		public final TraitCollection.TraitSlot Cowardly = this.getTraitSlot("Cowardly");
		public final TraitCollection.TraitSlot Brave = this.getTraitSlot("Brave");
		public final TraitCollection.TraitSlot Brooding = this.getTraitSlot("Brooding");
		public final TraitCollection.TraitSlot Insomniac = this.getTraitSlot("Insomniac");
		public final TraitCollection.TraitSlot NeedsLessSleep = this.getTraitSlot("NeedsLessSleep");
		public final TraitCollection.TraitSlot NeedsMoreSleep = this.getTraitSlot("NeedsMoreSleep");
		public final TraitCollection.TraitSlot Asthmatic = this.getTraitSlot("Asthmatic");
		public final TraitCollection.TraitSlot PlaysFootball = this.getTraitSlot("PlaysFootball");
		public final TraitCollection.TraitSlot Jogger = this.getTraitSlot("Jogger");
		public final TraitCollection.TraitSlot NightVision = this.getTraitSlot("NightVision");
		public final TraitCollection.TraitSlot FastLearner = this.getTraitSlot("FastLearner");
		public final TraitCollection.TraitSlot SlowLearner = this.getTraitSlot("SlowLearner");
		public final TraitCollection.TraitSlot Pacifist = this.getTraitSlot("Pacifist");
		public final TraitCollection.TraitSlot Feeble = this.getTraitSlot("Feeble");
		public final TraitCollection.TraitSlot Stout = this.getTraitSlot("Stout");
		public final TraitCollection.TraitSlot ShortTemper = this.getTraitSlot("ShortTemper");
		public final TraitCollection.TraitSlot Patient = this.getTraitSlot("Patient");
		public final TraitCollection.TraitSlot Injured = this.getTraitSlot("Injured");
		public final TraitCollection.TraitSlot Inconspicuous = this.getTraitSlot("Inconspicuous");
		public final TraitCollection.TraitSlot Conspicuous = this.getTraitSlot("Conspicuous");
		public final TraitCollection.TraitSlot Desensitized = this.getTraitSlot("Desensitized");
		public final TraitCollection.TraitSlot NightOwl = this.getTraitSlot("NightOwl");
		public final TraitCollection.TraitSlot Hemophobic = this.getTraitSlot("Hemophobic");
		public final TraitCollection.TraitSlot Burglar = this.getTraitSlot("Burglar");
		public final TraitCollection.TraitSlot KeenHearing = this.getTraitSlot("KeenHearing");
		public final TraitCollection.TraitSlot Deaf = this.getTraitSlot("Deaf");
		public final TraitCollection.TraitSlot HardOfHearing = this.getTraitSlot("HardOfHearing");
		public final TraitCollection.TraitSlot ThinSkinned = this.getTraitSlot("ThinSkinned");
		public final TraitCollection.TraitSlot ThickSkinned = this.getTraitSlot("ThickSkinned");
		public final TraitCollection.TraitSlot Marksman = this.getTraitSlot("Marksman");
		public final TraitCollection.TraitSlot Outdoorsman = this.getTraitSlot("Outdoorsman");
		public final TraitCollection.TraitSlot Lucky = this.getTraitSlot("Lucky");
		public final TraitCollection.TraitSlot Unlucky = this.getTraitSlot("Unlucky");
		public final TraitCollection.TraitSlot Nutritionist = this.getTraitSlot("Nutritionist");
		public final TraitCollection.TraitSlot Nutritionist2 = this.getTraitSlot("Nutritionist2");
		public final TraitCollection.TraitSlot Organized = this.getTraitSlot("Organized");
		public final TraitCollection.TraitSlot Disorganized = this.getTraitSlot("Disorganized");
		public final TraitCollection.TraitSlot Axeman = this.getTraitSlot("Axeman");
		public final TraitCollection.TraitSlot IronGut = this.getTraitSlot("IronGut");
		public final TraitCollection.TraitSlot WeakStomach = this.getTraitSlot("WeakStomach");
		public final TraitCollection.TraitSlot HeavyDrinker = this.getTraitSlot("HeavyDrinker");
		public final TraitCollection.TraitSlot LightDrinker = this.getTraitSlot("LightDrinker");
		public final TraitCollection.TraitSlot Resilient = this.getTraitSlot("Resilient");
		public final TraitCollection.TraitSlot ProneToIllness = this.getTraitSlot("ProneToIllness");
		public final TraitCollection.TraitSlot SpeedDemon = this.getTraitSlot("SpeedDemon");
		public final TraitCollection.TraitSlot SundayDriver = this.getTraitSlot("SundayDriver");
		public final TraitCollection.TraitSlot Smoker = this.getTraitSlot("Smoker");
		public final TraitCollection.TraitSlot Hypercondriac = this.getTraitSlot("Hypercondriac");
		public final TraitCollection.TraitSlot Illiterate = this.getTraitSlot("Illiterate");

		public boolean isIlliterate() {
			return this.Illiterate.isSet();
		}
	}

	public static class Location {
		public int x;
		public int y;
		public int z;

		public Location() {
		}

		public Location(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
		}

		public IsoGameCharacter.Location set(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
			return this;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getZ() {
			return this.z;
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

	private static final class L_getDotWithForwardDirection {
		static final Vector2 v1 = new Vector2();
		static final Vector2 v2 = new Vector2();
	}

	public class PerkInfo {
		public int level = 0;
		public PerkFactory.Perk perk;

		public int getLevel() {
			return this.level;
		}
	}

	private static class ReadBook {
		String fullType;
		int alreadyReadPages;
	}

	private static final class L_renderShadow {
		static final Vector3f forward = new Vector3f();
		static final Vector3 v1 = new Vector3();
		static final Vector3f v3 = new Vector3f();
	}

	private static final class L_renderLast {
		static final Color color = new Color();
	}

	private static class s_performance {
		static final PerformanceProfileProbe postUpdate = new PerformanceProfileProbe("IsoGameCharacter.postUpdate");
		public static PerformanceProfileProbe update = new PerformanceProfileProbe("IsoGameCharacter.update");
	}

	public static class TorchInfo {
		private static final ObjectPool TorchInfoPool = new ObjectPool(IsoGameCharacter.TorchInfo::new);
		private static final Vector3f tempVector3f = new Vector3f();
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

		public static IsoGameCharacter.TorchInfo alloc() {
			return (IsoGameCharacter.TorchInfo)TorchInfoPool.alloc();
		}

		public static void release(IsoGameCharacter.TorchInfo torchInfo) {
			TorchInfoPool.release((Object)torchInfo);
		}

		public IsoGameCharacter.TorchInfo set(IsoPlayer player, InventoryItem inventoryItem) {
			this.x = player.getX();
			this.y = player.getY();
			this.z = player.getZ();
			Vector2 vector2 = player.getLookVector(IsoGameCharacter.tempVector2);
			this.angleX = vector2.x;
			this.angleY = vector2.y;
			this.dist = (float)inventoryItem.getLightDistance();
			this.strength = inventoryItem.getLightStrength();
			this.bCone = inventoryItem.isTorchCone();
			this.dot = inventoryItem.getTorchDot();
			this.focusing = 0;
			return this;
		}

		public IsoGameCharacter.TorchInfo set(VehiclePart vehiclePart) {
			BaseVehicle baseVehicle = vehiclePart.getVehicle();
			VehicleLight vehicleLight = vehiclePart.getLight();
			VehicleScript vehicleScript = baseVehicle.getScript();
			Vector3f vector3f = tempVector3f;
			vector3f.set(vehicleLight.offset.x * vehicleScript.getExtents().x / 2.0F, 0.0F, vehicleLight.offset.y * vehicleScript.getExtents().z / 2.0F);
			baseVehicle.getWorldPos(vector3f, vector3f);
			this.x = vector3f.x;
			this.y = vector3f.y;
			this.z = vector3f.z;
			vector3f = baseVehicle.getForwardVector(vector3f);
			this.angleX = vector3f.x;
			this.angleY = vector3f.z;
			this.dist = vehiclePart.getLightDistance();
			this.strength = vehiclePart.getLightIntensity();
			this.bCone = true;
			this.dot = vehicleLight.dot;
			this.focusing = (int)vehiclePart.getLightFocusing();
			return this;
		}
	}

	private static class L_postUpdate {
		static final MoveDeltaModifiers moveDeltas = new MoveDeltaModifiers();
	}

	private static final class L_actionStateChanged {
		static final ArrayList stateNames = new ArrayList();
		static final ArrayList states = new ArrayList();
	}

	protected static final class l_testDotSide {
		static final Vector2 v1 = new Vector2();
		static final Vector2 v2 = new Vector2();
		static final Vector2 v3 = new Vector2();
	}

	private static final class Bandages {
		final HashMap bandageTypeMap = new HashMap();
		final THashMap itemMap = new THashMap();

		String getBloodBandageType(String string) {
			String string2 = (String)this.bandageTypeMap.get(string);
			if (string2 == null) {
				this.bandageTypeMap.put(string, string2 = string + "_Blood");
			}

			return string2;
		}

		void update(IsoGameCharacter gameCharacter) {
			if (!GameServer.bServer) {
				BodyDamage bodyDamage = gameCharacter.getBodyDamage();
				WornItems wornItems = gameCharacter.getWornItems();
				if (bodyDamage != null && wornItems != null) {
					assert !(gameCharacter instanceof IsoZombie);
					this.itemMap.clear();
					int int1;
					for (int1 = 0; int1 < wornItems.size(); ++int1) {
						InventoryItem inventoryItem = wornItems.getItemByIndex(int1);
						if (inventoryItem != null) {
							this.itemMap.put(inventoryItem.getFullType(), inventoryItem);
						}
					}

					for (int1 = 0; int1 < BodyPartType.ToIndex(BodyPartType.MAX); ++int1) {
						BodyPart bodyPart = bodyDamage.getBodyPart(BodyPartType.FromIndex(int1));
						BodyPartLast bodyPartLast = bodyDamage.getBodyPartsLastState(BodyPartType.FromIndex(int1));
						String string = bodyPart.getType().getBandageModel();
						if (!StringUtils.isNullOrWhitespace(string)) {
							String string2 = this.getBloodBandageType(string);
							if (bodyPart.bandaged() != bodyPartLast.bandaged()) {
								if (bodyPart.bandaged()) {
									if (bodyPart.isBandageDirty()) {
										this.removeBandageModel(gameCharacter, string);
										this.addBandageModel(gameCharacter, string2);
									} else {
										this.removeBandageModel(gameCharacter, string2);
										this.addBandageModel(gameCharacter, string);
									}
								} else {
									this.removeBandageModel(gameCharacter, string);
									this.removeBandageModel(gameCharacter, string2);
								}
							}

							String string3;
							if (bodyPart.bitten() != bodyPartLast.bitten()) {
								if (bodyPart.bitten()) {
									string3 = bodyPart.getType().getBiteWoundModel(gameCharacter.isFemale());
									if (StringUtils.isNullOrWhitespace(string3)) {
										continue;
									}

									this.addBandageModel(gameCharacter, string3);
								} else {
									this.removeBandageModel(gameCharacter, bodyPart.getType().getBiteWoundModel(gameCharacter.isFemale()));
								}
							}

							if (bodyPart.scratched() != bodyPartLast.scratched()) {
								if (bodyPart.scratched()) {
									string3 = bodyPart.getType().getScratchWoundModel(gameCharacter.isFemale());
									if (StringUtils.isNullOrWhitespace(string3)) {
										continue;
									}

									this.addBandageModel(gameCharacter, string3);
								} else {
									this.removeBandageModel(gameCharacter, bodyPart.getType().getScratchWoundModel(gameCharacter.isFemale()));
								}
							}

							if (bodyPart.isCut() != bodyPartLast.isCut()) {
								if (bodyPart.isCut()) {
									string3 = bodyPart.getType().getCutWoundModel(gameCharacter.isFemale());
									if (!StringUtils.isNullOrWhitespace(string3)) {
										this.addBandageModel(gameCharacter, string3);
									}
								} else {
									this.removeBandageModel(gameCharacter, bodyPart.getType().getCutWoundModel(gameCharacter.isFemale()));
								}
							}
						}
					}
				}
			}
		}

		protected void addBandageModel(IsoGameCharacter gameCharacter, String string) {
			if (!this.itemMap.containsKey(string)) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
				if (inventoryItem instanceof Clothing) {
					Clothing clothing = (Clothing)inventoryItem;
					gameCharacter.getInventory().addItem(clothing);
					gameCharacter.setWornItem(clothing.getBodyLocation(), clothing);
					gameCharacter.resetModelNextFrame();
				}
			}
		}

		protected void removeBandageModel(IsoGameCharacter gameCharacter, String string) {
			InventoryItem inventoryItem = (InventoryItem)this.itemMap.get(string);
			if (inventoryItem != null) {
				gameCharacter.getWornItems().remove(inventoryItem);
				gameCharacter.getInventory().Remove(inventoryItem);
				gameCharacter.resetModelNextFrame();
				gameCharacter.onWornItemsChanged();
				if (GameClient.bClient && gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
					GameClient.instance.sendClothing((IsoPlayer)gameCharacter, inventoryItem.getBodyLocation(), inventoryItem);
				}
			}
		}
	}

	public static class XPMultiplier {
		public float multiplier;
		public int minLevel;
		public int maxLevel;
	}

	public static enum BodyLocation {

		Head,
		Leg,
		Arm,
		Chest,
		Stomach,
		Foot,
		Hand;

		private static IsoGameCharacter.BodyLocation[] $values() {
			return new IsoGameCharacter.BodyLocation[]{Head, Leg, Arm, Chest, Stomach, Foot, Hand};
		}
	}
}
