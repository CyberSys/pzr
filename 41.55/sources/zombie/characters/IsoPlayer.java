package zombie.characters;

import fmod.fmod.BaseSoundListener;
import fmod.fmod.DummySoundListener;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.SoundListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.DebugFileWatcher;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.PredicatedFileWatcher;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.ai.states.BumpedState;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CloseWindowState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.FishingState;
import zombie.ai.states.FitnessState;
import zombie.ai.states.ForecastBeatenPlayerState;
import zombie.ai.states.IdleState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerActionsState;
import zombie.ai.states.PlayerAimState;
import zombie.ai.states.PlayerEmoteState;
import zombie.ai.states.PlayerExtState;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerFallingState;
import zombie.ai.states.PlayerGetUpState;
import zombie.ai.states.PlayerHitReactionPVPState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.PlayerSitOnGroundState;
import zombie.ai.states.PlayerStrafeState;
import zombie.ai.states.SmashWindowState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSound;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.audio.parameters.ParameterFootstepMaterial;
import zombie.audio.parameters.ParameterFootstepMaterial2;
import zombie.audio.parameters.ParameterLocalPlayer;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.audio.parameters.ParameterPlayerHealth;
import zombie.audio.parameters.ParameterShoeType;
import zombie.audio.parameters.ParameterVehicleHitLocation;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Fitness;
import zombie.characters.BodyDamage.Nutrition;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.Moodles.Moodles;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionGroup;
import zombie.characters.skills.PerkFactory;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.gameStates.MainScreenState;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponType;
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
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.weather.ClimateManager;
import zombie.network.BodyDamageSync;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.PassengerMap;
import zombie.network.PlayerNetHistory;
import zombie.network.ReplayManager;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclesDB2;
import zombie.world.WorldDictionary;


public final class IsoPlayer extends IsoLivingCharacter implements IHumanVisual {
	private String attackType;
	public static String DEATH_MUSIC_NAME = "PlayerDied";
	private boolean allowSprint;
	private boolean allowRun;
	public static boolean isTestAIMode = false;
	public static final byte NetRemoteState_Idle = 0;
	public static final byte NetRemoteState_Walk = 1;
	public static final byte NetRemoteState_Run = 2;
	public static final boolean NoSound = false;
	private static final float TIME_RIGHT_PRESSED_SECONDS = 0.15F;
	public static int assumedPlayer = 0;
	public static int numPlayers = 1;
	public static final int MAX = 4;
	public static final IsoPlayer[] players = new IsoPlayer[4];
	public static byte NetRemoteState_Attack = 3;
	private static IsoPlayer instance;
	private static final Object instanceLock = "IsoPlayer.instance Lock";
	private static final Vector2 testHitPosition = new Vector2();
	private static int FollowDeadCount = 240;
	private static final Stack StaticTraits = new Stack();
	private static int lmx = -1;
	private static int lmy = -1;
	private boolean ignoreAutoVault;
	public int remoteSneakLvl;
	public int remoteStrLvl;
	public int remoteFitLvl;
	public boolean canSeeAll;
	public boolean canHearAll;
	public boolean MoodleCantSprint;
	private static final Vector2 tempo = new Vector2();
	private static final Vector2 tempVector2 = new Vector2();
	private static final String forwardStr = "Forward";
	private static final String backwardStr = "Backward";
	private static final String leftStr = "Left";
	private static final String rightStr = "Right";
	private static boolean CoopPVP = false;
	private boolean ignoreContextKey;
	private boolean ignoreInputsForDirection;
	public boolean spottedByPlayer;
	private HashMap spottedPlayerTimer;
	private float extUpdateCount;
	private static final int s_randomIdleFidgetInterval = 5000;
	public boolean attackStarted;
	private static final PredicatedFileWatcher m_isoPlayerTriggerWatcher;
	private final PredicatedFileWatcher m_setClothingTriggerWatcher;
	private static Vector2 tempVector2_1;
	private static Vector2 tempVector2_2;
	protected final HumanVisual humanVisual;
	protected final ItemVisuals itemVisuals;
	public boolean targetedByZombie;
	public float lastTargeted;
	public float TimeSinceOpenDoor;
	public boolean bRemote;
	public int TimeSinceLastNetData;
	public String accessLevel;
	public String tagPrefix;
	public boolean showTag;
	public boolean factionPvp;
	public int OnlineID;
	public int OnlineChunkGridWidth;
	public final PlayerNetHistory netHistory;
	public boolean bJoypadMovementActive;
	public boolean bJoypadIgnoreAimUntilCentered;
	public boolean bJoypadIgnoreChargingRT;
	protected boolean bJoypadBDown;
	protected boolean bJoypadSprint;
	public boolean mpTorchCone;
	public float mpTorchDist;
	public float mpTorchStrength;
	public int PlayerIndex;
	public int serverPlayerIndex;
	public float useChargeDelta;
	public int JoypadBind;
	public float ContextPanic;
	public float numNearbyBuildingsRooms;
	public boolean isCharging;
	public boolean isChargingLT;
	private boolean bLookingWhileInVehicle;
	public boolean JustMoved;
	public boolean L3Pressed;
	public float maxWeightDelta;
	public float CurrentSpeed;
	public float MaxSpeed;
	public boolean bDeathFinished;
	public boolean isSpeek;
	public boolean isVoiceMute;
	public final Vector2 playerMoveDir;
	public BaseSoundListener soundListener;
	public String username;
	public boolean dirtyRecalcGridStack;
	public float dirtyRecalcGridStackTime;
	public float runningTime;
	public float timePressedContext;
	public float chargeTime;
	public float useChargeTime;
	public boolean bPressContext;
	public float closestZombie;
	public final Vector2 lastAngle;
	public String SaveFileName;
	public boolean bBannedAttacking;
	public int sqlID;
	protected int ClearSpottedTimer;
	protected float timeSinceLastStab;
	protected Stack LastSpotted;
	protected boolean bChangeCharacterDebounce;
	protected int followID;
	protected final Stack FollowCamStack;
	protected boolean bSeenThisFrame;
	protected boolean bCouldBeSeenThisFrame;
	protected float AsleepTime;
	protected final Stack spottedList;
	protected int TicksSinceSeenZombie;
	protected boolean Waiting;
	protected IsoSurvivor DragCharacter;
	protected float heartDelay;
	protected float heartDelayMax;
	protected long heartEventInstance;
	protected long worldAmbianceInstance;
	protected float DrunkOscilatorStepSin;
	protected float DrunkOscilatorRateSin;
	protected float DrunkOscilatorStepCos;
	protected float DrunkOscilatorRateCos;
	protected float DrunkOscilatorStepCos2;
	protected float DrunkOscilatorRateCos2;
	protected float DrunkSin;
	protected float DrunkCos;
	protected float DrunkCos2;
	protected float MinOscilatorRate;
	protected float MaxOscilatorRate;
	protected float DesiredSinRate;
	protected float DesiredCosRate;
	protected float OscilatorChangeRate;
	protected String Forname;
	protected String Surname;
	protected int DialogMood;
	protected int ping;
	protected IsoMovingObject DragObject;
	private double lastSeenZombieTime;
	private BaseSoundEmitter testemitter;
	private int checkSafehouse;
	private boolean attackFromBehind;
	private float TimeRightPressed;
	private long aimKeyDownMS;
	private long runKeyDownMS;
	private long sprintKeyDownMS;
	private int hypothermiaCache;
	private int hyperthermiaCache;
	private float ticksSincePressedMovement;
	private boolean flickTorch;
	private float checkNearbyRooms;
	private boolean bUseVehicle;
	private boolean bUsedVehicle;
	private float useVehicleDuration;
	private static final Vector3f tempVector3f;
	private final IsoPlayer.InputState inputState;
	private boolean isWearingNightVisionGoggles;
	private Integer transactionID;
	private float MoveSpeed;
	private int offSetXUI;
	private int offSetYUI;
	private float combatSpeed;
	private double HoursSurvived;
	private boolean bSentDeath;
	private boolean noClip;
	private boolean authorizeMeleeAction;
	private boolean authorizeShoveStomp;
	private boolean blockMovement;
	private Nutrition nutrition;
	private Fitness fitness;
	private boolean forceOverrideAnim;
	private boolean initiateAttack;
	private final ColorInfo tagColor;
	private String displayName;
	private boolean seeNonPvpZone;
	private final HashMap mechanicsItem;
	private int sleepingPillsTaken;
	private long lastPillsTaken;
	private long heavyBreathInstance;
	private String heavyBreathSoundName;
	private boolean allChatMuted;
	private boolean forceAim;
	private boolean forceRun;
	private boolean forceSprint;
	private boolean LastRT;
	private final UpdateLimit ULbeatenVehicle;
	private boolean bMultiplayer;
	private String SaveFileIP;
	private BaseVehicle vehicle4testCollision;
	private long steamID;
	private final IsoPlayer.VehicleContainerData vehicleContainerData;
	private boolean isWalking;
	private int footInjuryTimer;
	private boolean bSneakDebounce;
	private final UpdateLimit footstepTimer;
	private float m_turnDelta;
	public boolean m_bKnockedDown;
	protected boolean m_isPlayerMoving;
	private float m_walkSpeed;
	private float m_walkInjury;
	private float m_runSpeed;
	private float m_idleSpeed;
	private float m_deltaX;
	private float m_deltaY;
	private float m_windspeed;
	private float m_windForce;
	private float m_IPX;
	private float m_IPY;
	private float pressedRunTimer;
	private boolean pressedRun;
	private boolean m_meleePressed;
	private boolean m_lastAttackWasShove;
	private boolean m_isPerformingAnAction;
	private ArrayList alreadyReadBook;
	public final NetworkPlayerAI networkAI;
	public ReplayManager replay;
	public final ArrayList strikes;
	private boolean pathfindRun;
	private static final IsoPlayer.MoveVars s_moveVars;
	int atkTimer;
	private static final ArrayList s_targetsProne;
	private static final ArrayList s_targetsStanding;
	private boolean bReloadButtonDown;
	private boolean bRackButtonDown;
	private boolean bReloadKeyDown;
	private boolean bRackKeyDown;
	int shoottimer;
	private final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed;
	private final ParameterFootstepMaterial parameterFootstepMaterial;
	private final ParameterFootstepMaterial2 parameterFootstepMaterial2;
	private final ParameterLocalPlayer parameterLocalPlayer;
	private final ParameterMeleeHitSurface parameterMeleeHitSurface;
	private final ParameterPlayerHealth parameterPlayerHealth;
	private final ParameterVehicleHitLocation parameterVehicleHitLocation;
	private final ParameterShoeType parameterShoeType;

	public IsoPlayer(IsoCell cell) {
		this(cell, (SurvivorDesc)null, 0, 0, 0);
	}

	public IsoPlayer(IsoCell cell, SurvivorDesc survivorDesc, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.attackType = null;
		this.allowSprint = true;
		this.allowRun = true;
		this.ignoreAutoVault = false;
		this.remoteSneakLvl = 0;
		this.remoteStrLvl = 0;
		this.remoteFitLvl = 0;
		this.canSeeAll = false;
		this.canHearAll = false;
		this.MoodleCantSprint = false;
		this.ignoreContextKey = false;
		this.ignoreInputsForDirection = false;
		this.spottedByPlayer = false;
		this.spottedPlayerTimer = new HashMap();
		this.extUpdateCount = 0.0F;
		this.attackStarted = false;
		this.humanVisual = new HumanVisual(this);
		this.itemVisuals = new ItemVisuals();
		this.targetedByZombie = false;
		this.lastTargeted = 1.0E8F;
		this.TimeSinceLastNetData = 0;
		this.accessLevel = "";
		this.tagPrefix = "";
		this.showTag = true;
		this.factionPvp = false;
		this.OnlineID = 1;
		this.netHistory = new PlayerNetHistory();
		this.bJoypadMovementActive = true;
		this.bJoypadIgnoreChargingRT = false;
		this.bJoypadBDown = false;
		this.bJoypadSprint = false;
		this.mpTorchCone = false;
		this.mpTorchDist = 0.0F;
		this.mpTorchStrength = 0.0F;
		this.PlayerIndex = 0;
		this.serverPlayerIndex = 1;
		this.useChargeDelta = 0.0F;
		this.JoypadBind = -1;
		this.ContextPanic = 0.0F;
		this.numNearbyBuildingsRooms = 0.0F;
		this.isCharging = false;
		this.isChargingLT = false;
		this.bLookingWhileInVehicle = false;
		this.JustMoved = false;
		this.L3Pressed = false;
		this.maxWeightDelta = 1.0F;
		this.CurrentSpeed = 0.0F;
		this.MaxSpeed = 0.09F;
		this.bDeathFinished = false;
		this.playerMoveDir = new Vector2(0.0F, 0.0F);
		this.username = "Bob";
		this.dirtyRecalcGridStack = true;
		this.dirtyRecalcGridStackTime = 10.0F;
		this.runningTime = 0.0F;
		this.timePressedContext = 0.0F;
		this.chargeTime = 0.0F;
		this.useChargeTime = 0.0F;
		this.bPressContext = false;
		this.closestZombie = 1000000.0F;
		this.lastAngle = new Vector2();
		this.bBannedAttacking = false;
		this.sqlID = -1;
		this.ClearSpottedTimer = -1;
		this.timeSinceLastStab = 0.0F;
		this.LastSpotted = new Stack();
		this.bChangeCharacterDebounce = false;
		this.followID = 0;
		this.FollowCamStack = new Stack();
		this.bSeenThisFrame = false;
		this.bCouldBeSeenThisFrame = false;
		this.AsleepTime = 0.0F;
		this.spottedList = new Stack();
		this.TicksSinceSeenZombie = 9999999;
		this.Waiting = true;
		this.DragCharacter = null;
		this.heartDelay = 30.0F;
		this.heartDelayMax = 30.0F;
		this.DrunkOscilatorStepSin = 0.0F;
		this.DrunkOscilatorRateSin = 0.1F;
		this.DrunkOscilatorStepCos = 0.0F;
		this.DrunkOscilatorRateCos = 0.1F;
		this.DrunkOscilatorStepCos2 = 0.0F;
		this.DrunkOscilatorRateCos2 = 0.07F;
		this.DrunkSin = 0.0F;
		this.DrunkCos = 23784.0F;
		this.DrunkCos2 = 61616.0F;
		this.MinOscilatorRate = 0.01F;
		this.MaxOscilatorRate = 0.15F;
		this.DesiredSinRate = 0.0F;
		this.DesiredCosRate = 0.0F;
		this.OscilatorChangeRate = 0.05F;
		this.Forname = "Bob";
		this.Surname = "Smith";
		this.DialogMood = 1;
		this.ping = 0;
		this.DragObject = null;
		this.lastSeenZombieTime = 2.0;
		this.checkSafehouse = 200;
		this.attackFromBehind = false;
		this.TimeRightPressed = 0.0F;
		this.aimKeyDownMS = 0L;
		this.runKeyDownMS = 0L;
		this.sprintKeyDownMS = 0L;
		this.hypothermiaCache = -1;
		this.hyperthermiaCache = -1;
		this.ticksSincePressedMovement = 0.0F;
		this.flickTorch = false;
		this.checkNearbyRooms = 0.0F;
		this.bUseVehicle = false;
		this.inputState = new IsoPlayer.InputState();
		this.isWearingNightVisionGoggles = false;
		this.transactionID = 0;
		this.MoveSpeed = 0.06F;
		this.offSetXUI = 0;
		this.offSetYUI = 0;
		this.combatSpeed = 1.0F;
		this.HoursSurvived = 0.0;
		this.noClip = false;
		this.authorizeMeleeAction = true;
		this.authorizeShoveStomp = true;
		this.blockMovement = false;
		this.forceOverrideAnim = false;
		this.initiateAttack = false;
		this.tagColor = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
		this.displayName = null;
		this.seeNonPvpZone = false;
		this.mechanicsItem = new HashMap();
		this.sleepingPillsTaken = 0;
		this.lastPillsTaken = 0L;
		this.heavyBreathInstance = 0L;
		this.heavyBreathSoundName = null;
		this.allChatMuted = false;
		this.forceAim = false;
		this.forceRun = false;
		this.forceSprint = false;
		this.ULbeatenVehicle = new UpdateLimit(200L);
		this.vehicle4testCollision = null;
		this.vehicleContainerData = new IsoPlayer.VehicleContainerData();
		this.isWalking = false;
		this.footInjuryTimer = 0;
		this.footstepTimer = new UpdateLimit(500L);
		this.m_turnDelta = 0.0F;
		this.m_bKnockedDown = false;
		this.m_isPlayerMoving = false;
		this.m_walkSpeed = 0.0F;
		this.m_walkInjury = 0.0F;
		this.m_runSpeed = 0.0F;
		this.m_idleSpeed = 0.0F;
		this.m_deltaX = 0.0F;
		this.m_deltaY = 0.0F;
		this.m_windspeed = 0.0F;
		this.m_windForce = 0.0F;
		this.m_IPX = 0.0F;
		this.m_IPY = 0.0F;
		this.pressedRunTimer = 0.0F;
		this.pressedRun = false;
		this.m_meleePressed = false;
		this.m_lastAttackWasShove = false;
		this.m_isPerformingAnAction = false;
		this.alreadyReadBook = new ArrayList();
		this.replay = null;
		this.strikes = new ArrayList(3);
		this.pathfindRun = false;
		this.atkTimer = 0;
		this.bReloadButtonDown = false;
		this.bRackButtonDown = false;
		this.bReloadKeyDown = false;
		this.bRackKeyDown = false;
		this.shoottimer = 0;
		this.parameterCharacterMovementSpeed = new ParameterCharacterMovementSpeed(this);
		this.parameterFootstepMaterial = new ParameterFootstepMaterial(this);
		this.parameterFootstepMaterial2 = new ParameterFootstepMaterial2(this);
		this.parameterLocalPlayer = new ParameterLocalPlayer(this);
		this.parameterMeleeHitSurface = new ParameterMeleeHitSurface(this);
		this.parameterPlayerHealth = new ParameterPlayerHealth(this);
		this.parameterVehicleHitLocation = new ParameterVehicleHitLocation();
		this.parameterShoeType = new ParameterShoeType(this);
		this.registerVariableCallbacks();
		this.Traits.addAll(StaticTraits);
		StaticTraits.clear();
		this.dir = IsoDirections.W;
		this.nutrition = new Nutrition(this);
		this.fitness = new Fitness(this);
		this.initWornItems("Human");
		this.initAttachedItems("Human");
		this.clothingWetness = new ClothingWetness(this);
		if (survivorDesc != null) {
			this.descriptor = survivorDesc;
		} else {
			this.descriptor = new SurvivorDesc();
		}

		this.setFemale(this.descriptor.isFemale());
		this.Dressup(this.descriptor);
		this.getHumanVisual().copyFrom(this.descriptor.humanVisual);
		this.InitSpriteParts(this.descriptor);
		LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
		if (!GameClient.bClient && !GameServer.bServer) {
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
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		if (Core.GameMode.equals("LastStand")) {
			this.Traits.add("Strong");
		}

		if (this.Traits.Strong.isSet()) {
			this.maxWeightDelta = 1.5F;
		}

		if (this.Traits.Weak.isSet()) {
			this.maxWeightDelta = 0.75F;
		}

		if (this.Traits.Feeble.isSet()) {
			this.maxWeightDelta = 0.9F;
		}

		if (this.Traits.Stout.isSet()) {
			this.maxWeightDelta = 1.25F;
		}

		this.descriptor.temper = 5.0F;
		if (this.Traits.ShortTemper.isSet()) {
			this.descriptor.temper = 7.5F;
		} else if (this.Traits.Patient.isSet()) {
			this.descriptor.temper = 2.5F;
		}

		if (this.Traits.Injured.isSet()) {
			this.getBodyDamage().AddRandomDamage();
		}

		this.bMultiplayer = GameServer.bServer || GameClient.bClient;
		this.vehicle4testCollision = null;
		if (Core.bDebug && DebugOptions.instance.CheatPlayerStartInvisible.getValue()) {
			this.setGhostMode(true);
			this.setGodMod(true);
		}

		this.actionContext.setGroup(ActionGroup.getActionGroup("player"));
		this.initializeStates();
		DebugFileWatcher.instance.add(m_isoPlayerTriggerWatcher);
		this.m_setClothingTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_SetClothing.xml"), TriggerXmlFile.class, this::onTrigger_setClothingToXmlTriggerFile);
		this.networkAI = new NetworkPlayerAI(this);
		this.initFMODParameters();
	}

	private void registerVariableCallbacks() {
		this.setVariable("CombatSpeed", ()->{
			return this.combatSpeed;
		}, (var1)->{
			this.combatSpeed = var1;
		});
		this.setVariable("CriticalHit", ()->{
			return this.isCrit;
		}, (var1)->{
			this.isCrit = var1;
		});
		this.setVariable("TurnDelta", ()->{
			return this.m_turnDelta;
		}, (var1)->{
			this.m_turnDelta = var1;
		});
		this.setVariable("bKnockedDown", ()->{
			return this.m_bKnockedDown;
		}, (var1)->{
			this.m_bKnockedDown = var1;
		});
		this.setVariable("sneaking", this::isSneaking, this::setSneaking);
		this.setVariable("initiateAttack", ()->{
			return this.initiateAttack;
		}, (var1)->{
			this.initiateAttack = var1;
		});
		this.setVariable("isMoving", this::isPlayerMoving);
		this.setVariable("isRunning", this::isRunning, this::setRunning);
		this.setVariable("isSprinting", this::isSprinting, this::setSprinting);
		this.setVariable("run", this::isRunning, this::setRunning);
		this.setVariable("sprint", this::isSprinting, this::setSprinting);
		this.setVariable("isStrafing", this::isStrafing);
		this.setVariable("WalkSpeed", ()->{
			return this.m_walkSpeed;
		}, (var1)->{
			this.m_walkSpeed = var1;
		});
		this.setVariable("WalkInjury", ()->{
			return this.m_walkInjury;
		}, (var1)->{
			this.m_walkInjury = var1;
		});
		this.setVariable("RunSpeed", ()->{
			return this.m_runSpeed;
		}, (var1)->{
			this.m_runSpeed = var1;
		});
		this.setVariable("IdleSpeed", ()->{
			return this.m_idleSpeed;
		}, (var1)->{
			this.m_idleSpeed = var1;
		});
		this.setVariable("DeltaX", ()->{
			return this.m_deltaX;
		}, (var1)->{
			this.m_deltaX = var1;
		});
		this.setVariable("DeltaY", ()->{
			return this.m_deltaY;
		}, (var1)->{
			this.m_deltaY = var1;
		});
		this.setVariable("Windspeed", ()->{
			return this.m_windspeed;
		}, (var1)->{
			this.m_windspeed = var1;
		});
		this.setVariable("WindForce", ()->{
			return this.m_windForce;
		}, (var1)->{
			this.m_windForce = var1;
		});
		this.setVariable("IPX", ()->{
			return this.m_IPX;
		}, (var1)->{
			this.m_IPX = var1;
		});
		this.setVariable("IPY", ()->{
			return this.m_IPY;
		}, (var1)->{
			this.m_IPY = var1;
		});
		this.setVariable("attacktype", ()->{
			return this.attackType;
		});
		this.setVariable("aim", this::isAiming);
		this.setVariable("bdead", ()->{
			return (!GameClient.bClient || this.bSentDeath) && this.isDead() || GameClient.bClient && this.bRemote && this.bDead;
		});
		this.setVariable("bdoshove", ()->{
			return this.bDoShove;
		});
		this.setVariable("bfalling", ()->{
			return this.z > 0.0F && this.fallTime > 2.0F;
		});
		this.setVariable("baimatfloor", this::isAimAtFloor);
		this.setVariable("attackfrombehind", ()->{
			return this.attackFromBehind;
		});
		this.setVariable("bundervehicle", this::isUnderVehicle);
		this.setVariable("reanimatetimer", this::getReanimateTimer);
		this.setVariable("isattacking", this::isAttacking);
		this.setVariable("beensprintingfor", this::getBeenSprintingFor);
		this.setVariable("bannedAttacking", ()->{
			return this.bBannedAttacking;
		});
		this.setVariable("meleePressed", ()->{
			return this.m_meleePressed;
		});
		this.setVariable("AttackAnim", false);
		this.setVariable("BumpFall", false);
		this.setVariable("IsPerformingAnAction", this::isPerformingAnAction, this::setPerformingAnAction);
	}

	public Vector2 getDeferredMovement(Vector2 vector2) {
		super.getDeferredMovement(vector2);
		if (DebugOptions.instance.CheatPlayerInvisibleSprint.getValue() && this.isGhostMode() && (this.IsRunning() || this.isSprinting()) && !this.isCurrentState(ClimbOverFenceState.instance()) && !this.isCurrentState(ClimbThroughWindowState.instance())) {
			if (this.getPath2() == null && !this.pressedMovement(false)) {
				return vector2.set(0.0F, 0.0F);
			}

			if (this.getCurrentBuilding() != null) {
				vector2.scale(2.5F);
				return vector2;
			}

			vector2.scale(7.5F);
		}

		return vector2;
	}

	public float getTurnDelta() {
		return !DebugOptions.instance.CheatPlayerInvisibleSprint.getValue() || !this.isGhostMode() || !this.isRunning() && !this.isSprinting() ? super.getTurnDelta() : 10.0F;
	}

	public void setPerformingAnAction(boolean boolean1) {
		this.m_isPerformingAnAction = boolean1;
	}

	public boolean isPerformingAnAction() {
		return this.m_isPerformingAnAction;
	}

	public boolean isAttacking() {
		return !StringUtils.isNullOrWhitespace(this.getAttackType());
	}

	public boolean shouldBeTurning() {
		if (this.isPerformingAnAction()) {
		}

		return super.shouldBeTurning();
	}

	public static void invokeOnPlayerInstance(Runnable runnable) {
		synchronized (instanceLock) {
			if (instance != null) {
				runnable.run();
			}
		}
	}

	public static IsoPlayer getInstance() {
		return instance;
	}

	public static void setInstance(IsoPlayer player) {
		synchronized (instanceLock) {
			instance = player;
		}
	}

	public static boolean hasInstance() {
		return instance != null;
	}

	private static void onTrigger_ResetIsoPlayerModel(String string) {
		if (instance != null) {
			DebugLog.log(DebugType.General, "DebugFileWatcher Hit. Resetting player model: " + string);
			instance.resetModel();
		} else {
			DebugLog.log(DebugType.General, "DebugFileWatcher Hit. Player instance null : " + string);
		}
	}

	public static Stack getStaticTraits() {
		return StaticTraits;
	}

	public static int getFollowDeadCount() {
		return FollowDeadCount;
	}

	public static void setFollowDeadCount(int int1) {
		FollowDeadCount = int1;
	}

	public static ArrayList getAllFileNames() {
		ArrayList arrayList = new ArrayList();
		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		String string2 = string + File.separator + Core.GameSaveWorld;
		for (int int1 = 1; int1 < 100; ++int1) {
			File file = new File(string2 + File.separator + "map_p" + int1 + ".bin");
			if (file.exists()) {
				arrayList.add("map_p" + int1 + ".bin");
			}
		}

		return arrayList;
	}

	public static String getUniqueFileName() {
		int int1 = 0;
		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		String string2 = string + File.separator + Core.GameSaveWorld;
		for (int int2 = 1; int2 < 100; ++int2) {
			File file = new File(string2 + File.separator + "map_p" + int2 + ".bin");
			if (file.exists()) {
				int1 = int2;
			}
		}

		++int1;
		return ZomboidFileSystem.instance.getFileNameInCurrentSave("map_p" + int1 + ".bin");
	}

	public static ArrayList getAllSavedPlayers() {
		ArrayList arrayList;
		if (GameClient.bClient) {
			arrayList = ClientPlayerDB.getInstance().getAllNetworkPlayers();
		} else {
			arrayList = PlayerDB.getInstance().getAllLocalPlayers();
		}

		for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
			if (((IsoPlayer)arrayList.get(int1)).isDead()) {
				arrayList.remove(int1);
			}
		}

		return arrayList;
	}

	public static boolean isServerPlayerIDValid(String string) {
		if (GameClient.bClient) {
			String string2 = ServerOptions.instance.ServerPlayerID.getValue();
			return string2 != null && !string2.isEmpty() ? string2.equals(string) : true;
		} else {
			return true;
		}
	}

	public static int getPlayerIndex() {
		return instance == null ? assumedPlayer : instance.PlayerIndex;
	}

	public static boolean allPlayersDead() {
		for (int int1 = 0; int1 < numPlayers; ++int1) {
			if (players[int1] != null && !players[int1].isDead()) {
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

	public static boolean allPlayersAsleep() {
		int int1 = 0;
		int int2 = 0;
		for (int int3 = 0; int3 < numPlayers; ++int3) {
			if (players[int3] != null && !players[int3].isDead()) {
				++int1;
				if (players[int3] != null && players[int3].isAsleep()) {
					++int2;
				}
			}
		}

		return int1 > 0 && int1 == int2;
	}

	public static boolean getCoopPVP() {
		return CoopPVP;
	}

	public static void setCoopPVP(boolean boolean1) {
		CoopPVP = boolean1;
	}

	public void TestZombieSpotPlayer(IsoMovingObject movingObject) {
		movingObject.spotted(this, false);
		if (movingObject instanceof IsoZombie) {
			float float1 = movingObject.DistTo(this);
			if (float1 < this.closestZombie && !movingObject.isOnFloor()) {
				this.closestZombie = float1;
			}
		}
	}

	public float getPathSpeed() {
		float float1 = this.getMoveSpeed() * 0.9F;
		switch (this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
		case 1: 
			float1 *= 0.95F;
			break;
		
		case 2: 
			float1 *= 0.9F;
			break;
		
		case 3: 
			float1 *= 0.8F;
			break;
		
		case 4: 
			float1 *= 0.6F;
		
		}
		if (this.stats.enduranceRecharging) {
			float1 *= 0.85F;
		}

		if (this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 0) {
			float float2 = this.getInventory().getCapacityWeight();
			float float3 = (float)this.getMaxWeight();
			float float4 = Math.min(2.0F, float2 / float3) - 1.0F;
			float1 *= 0.65F + 0.35F * (1.0F - float4);
		}

		return float1;
	}

	public boolean isGhostMode() {
		return this.isInvisible();
	}

	public void setGhostMode(boolean boolean1) {
		this.setInvisible(boolean1);
	}

	public boolean isSeeEveryone() {
		if (Core.bDebug && DebugOptions.instance.CheatPlayerSeeEveryone.getValue()) {
			return true;
		} else {
			return this.isGhostMode() && GameClient.bClient;
		}
	}

	public Vector2 getPlayerMoveDir() {
		return this.playerMoveDir;
	}

	public void setPlayerMoveDir(Vector2 vector2) {
		this.playerMoveDir.set(vector2);
	}

	public void MoveUnmodded(Vector2 vector2) {
		if (this.getSlowFactor() > 0.0F) {
			vector2.x *= 1.0F - this.getSlowFactor();
			vector2.y *= 1.0F - this.getSlowFactor();
		}

		super.MoveUnmodded(vector2);
	}

	public void nullifyAiming() {
		this.isCharging = false;
		this.setIsAiming(false);
	}

	public boolean isAimKeyDown() {
		if (this.PlayerIndex != 0) {
			return false;
		} else {
			int int1 = Core.getInstance().getKey("Aim");
			boolean boolean1 = GameKeyboard.isKeyDown(int1);
			if (!boolean1) {
				return false;
			} else {
				boolean boolean2 = int1 == 29 || int1 == 157;
				return !boolean2 || !UIManager.isMouseOverInventory();
			}
		}
	}

	private void initializeStates() {
		HashMap hashMap = this.getStateUpdateLookup();
		hashMap.clear();
		if (this.getVehicle() == null) {
			hashMap.put("actions", PlayerActionsState.instance());
			hashMap.put("aim", PlayerAimState.instance());
			hashMap.put("climbfence", ClimbOverFenceState.instance());
			hashMap.put("climbdownrope", ClimbDownSheetRopeState.instance());
			hashMap.put("climbrope", ClimbSheetRopeState.instance());
			hashMap.put("climbwall", ClimbOverWallState.instance());
			hashMap.put("climbwindow", ClimbThroughWindowState.instance());
			hashMap.put("emote", PlayerEmoteState.instance());
			hashMap.put("ext", PlayerExtState.instance());
			hashMap.put("sitext", PlayerExtState.instance());
			hashMap.put("falldown", PlayerFallDownState.instance());
			hashMap.put("falling", PlayerFallingState.instance());
			hashMap.put("getup", PlayerGetUpState.instance());
			hashMap.put("idle", IdleState.instance());
			hashMap.put("melee", SwipeStatePlayer.instance());
			hashMap.put("shove", SwipeStatePlayer.instance());
			hashMap.put("ranged", SwipeStatePlayer.instance());
			hashMap.put("onground", PlayerOnGroundState.instance());
			hashMap.put("knockeddown", PlayerKnockedDown.instance());
			hashMap.put("openwindow", OpenWindowState.instance());
			hashMap.put("closewindow", CloseWindowState.instance());
			hashMap.put("smashwindow", SmashWindowState.instance());
			hashMap.put("fishing", FishingState.instance());
			hashMap.put("fitness", FitnessState.instance());
			hashMap.put("hitreaction", PlayerHitReactionState.instance());
			hashMap.put("hitreactionpvp", PlayerHitReactionPVPState.instance());
			hashMap.put("hitreaction-hit", PlayerHitReactionPVPState.instance());
			hashMap.put("collide", CollideWithWallState.instance());
			hashMap.put("bumped", BumpedState.instance());
			hashMap.put("bumped-bump", BumpedState.instance());
			hashMap.put("sitonground", PlayerSitOnGroundState.instance());
			hashMap.put("strafe", PlayerStrafeState.instance());
		} else {
			hashMap.put("aim", PlayerAimState.instance());
			hashMap.put("idle", IdleState.instance());
			hashMap.put("melee", SwipeStatePlayer.instance());
			hashMap.put("shove", SwipeStatePlayer.instance());
			hashMap.put("ranged", SwipeStatePlayer.instance());
		}
	}

	public ActionContext getActionContext() {
		return this.actionContext;
	}

	protected void onAnimPlayerCreated(AnimationPlayer animationPlayer) {
		super.onAnimPlayerCreated(animationPlayer);
		animationPlayer.addBoneReparent("Bip01_L_Thigh", "Bip01");
		animationPlayer.addBoneReparent("Bip01_R_Thigh", "Bip01");
		animationPlayer.addBoneReparent("Bip01_L_Clavicle", "Bip01_Spine1");
		animationPlayer.addBoneReparent("Bip01_R_Clavicle", "Bip01_Spine1");
		animationPlayer.addBoneReparent("Bip01_Prop1", "Bip01_R_Hand");
		animationPlayer.addBoneReparent("Bip01_Prop2", "Bip01_L_Hand");
	}

	public String GetAnimSetName() {
		return this.getVehicle() == null ? "player" : "player-vehicle";
	}

	public boolean IsInMeleeAttack() {
		return this.isCurrentState(SwipeStatePlayer.instance());
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		super.load(byteBuffer, int1, boolean1);
		this.setHoursSurvived(byteBuffer.getDouble());
		SurvivorDesc survivorDesc = this.descriptor;
		this.setFemale(survivorDesc.isFemale());
		this.InitSpriteParts(survivorDesc);
		this.SpeakColour = new Color(Rand.Next(135) + 120, Rand.Next(135) + 120, Rand.Next(135) + 120, 255);
		if (GameClient.bClient) {
			if (Core.getInstance().getMpTextColor() != null) {
				this.SpeakColour = new Color(Core.getInstance().getMpTextColor().r, Core.getInstance().getMpTextColor().g, Core.getInstance().getMpTextColor().b, 1.0F);
			} else {
				Core.getInstance().setMpTextColor(new ColorInfo(this.SpeakColour.r, this.SpeakColour.g, this.SpeakColour.b, 1.0F));
				try {
					Core.getInstance().saveOptions();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		this.setZombieKills(byteBuffer.getInt());
		ArrayList arrayList = this.savedInventoryItems;
		byte byte3 = byteBuffer.get();
		short short1;
		for (int int2 = 0; int2 < byte3; ++int2) {
			String string = GameWindow.ReadString(byteBuffer);
			short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size() && this.wornItems.getBodyLocationGroup().getLocation(string) != null) {
				this.wornItems.setItem(string, (InventoryItem)arrayList.get(short1));
			}
		}

		short short2 = byteBuffer.getShort();
		if (short2 >= 0 && short2 < arrayList.size()) {
			this.leftHandItem = (InventoryItem)arrayList.get(short2);
		}

		short2 = byteBuffer.getShort();
		if (short2 >= 0 && short2 < arrayList.size()) {
			this.rightHandItem = (InventoryItem)arrayList.get(short2);
		}

		this.setVariable("Weapon", WeaponType.getWeaponType((IsoGameCharacter)this).type);
		this.setSurvivorKills(byteBuffer.getInt());
		this.initSpritePartsEmpty();
		this.nutrition.load(byteBuffer);
		this.setAllChatMuted(byteBuffer.get() == 1);
		this.tagPrefix = GameWindow.ReadString(byteBuffer);
		this.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		this.setDisplayName(GameWindow.ReadString(byteBuffer));
		this.showTag = byteBuffer.get() == 1;
		this.factionPvp = byteBuffer.get() == 1;
		if (int1 >= 176) {
			this.noClip = byteBuffer.get() == 1;
		}

		if (byteBuffer.get() == 1) {
			this.savedVehicleX = byteBuffer.getFloat();
			this.savedVehicleY = byteBuffer.getFloat();
			this.savedVehicleSeat = (short)byteBuffer.get();
			this.savedVehicleRunning = byteBuffer.get() == 1;
			this.z = 0.0F;
		}

		int int3 = byteBuffer.getInt();
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			this.mechanicsItem.put(byteBuffer.getLong(), byteBuffer.getLong());
		}

		this.fitness.load(byteBuffer, int1);
		int int5;
		if (int1 >= 184) {
			short1 = byteBuffer.getShort();
			for (int5 = 0; int5 < short1; ++int5) {
				short short3 = byteBuffer.getShort();
				String string2 = WorldDictionary.getItemTypeFromID(short3);
				if (string2 != null) {
					this.alreadyReadBook.add(string2);
				}
			}
		} else if (int1 >= 182) {
			int4 = byteBuffer.getInt();
			for (int5 = 0; int5 < int4; ++int5) {
				this.alreadyReadBook.add(GameWindow.ReadString(byteBuffer));
			}
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		IsoPlayer player = instance;
		instance = this;
		try {
			super.save(byteBuffer, boolean1);
		} finally {
			instance = player;
		}

		byteBuffer.putDouble(this.getHoursSurvived());
		byteBuffer.putInt(this.getZombieKills());
		if (this.wornItems.size() > 127) {
			throw new RuntimeException("too many worn items");
		} else {
			byteBuffer.put((byte)this.wornItems.size());
			this.wornItems.forEach((boolean1x)->{
				GameWindow.WriteString(byteBuffer, boolean1x.getLocation());
				byteBuffer.putShort((short)this.savedInventoryItems.indexOf(boolean1x.getItem()));
			});

			byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getPrimaryHandItem()));
			byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getSecondaryHandItem()));
			byteBuffer.putInt(this.getSurvivorKills());
			this.nutrition.save(byteBuffer);
			byteBuffer.put((byte)(this.isAllChatMuted() ? 1 : 0));
			GameWindow.WriteString(byteBuffer, this.tagPrefix);
			byteBuffer.putFloat(this.getTagColor().r);
			byteBuffer.putFloat(this.getTagColor().g);
			byteBuffer.putFloat(this.getTagColor().b);
			GameWindow.WriteString(byteBuffer, this.displayName);
			byteBuffer.put((byte)(this.showTag ? 1 : 0));
			byteBuffer.put((byte)(this.factionPvp ? 1 : 0));
			byteBuffer.put((byte)(this.isNoClip() ? 1 : 0));
			if (this.vehicle != null) {
				byteBuffer.put((byte)1);
				byteBuffer.putFloat(this.vehicle.x);
				byteBuffer.putFloat(this.vehicle.y);
				byteBuffer.put((byte)this.vehicle.getSeat(this));
				byteBuffer.put((byte)(this.vehicle.isEngineRunning() ? 1 : 0));
			} else {
				byteBuffer.put((byte)0);
			}

			byteBuffer.putInt(this.mechanicsItem.size());
			Iterator iterator = this.mechanicsItem.keySet().iterator();
			while (iterator.hasNext()) {
				Long Long1 = (Long)iterator.next();
				byteBuffer.putLong(Long1);
				byteBuffer.putLong((Long)this.mechanicsItem.get(Long1));
			}

			this.fitness.save(byteBuffer);
			byteBuffer.putShort((short)this.alreadyReadBook.size());
			for (int int1 = 0; int1 < this.alreadyReadBook.size(); ++int1) {
				byteBuffer.putShort(WorldDictionary.getItemRegistryID((String)this.alreadyReadBook.get(int1)));
			}
		}
	}

	public void save() throws IOException {
		synchronized (SliceY.SliceBufferLock) {
			ByteBuffer byteBuffer = SliceY.SliceBuffer;
			byteBuffer.clear();
			byteBuffer.put((byte)80);
			byteBuffer.put((byte)76);
			byteBuffer.put((byte)89);
			byteBuffer.put((byte)82);
			byteBuffer.putInt(184);
			GameWindow.WriteString(byteBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
			byteBuffer.putInt((int)(this.x / 10.0F));
			byteBuffer.putInt((int)(this.y / 10.0F));
			byteBuffer.putInt((int)this.x);
			byteBuffer.putInt((int)this.y);
			byteBuffer.putInt((int)this.z);
			this.save(byteBuffer);
			String string = ZomboidFileSystem.instance.getGameModeCacheDir();
			File file = new File(string + Core.GameSaveWorld + File.separator + "map_p.bin");
			if (!Core.getInstance().isNoSave()) {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				try {
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					try {
						bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
					} catch (Throwable throwable) {
						try {
							bufferedOutputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedOutputStream.close();
				} catch (Throwable throwable3) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileOutputStream.close();
			}

			if (this.getVehicle() != null && !GameClient.bClient) {
				VehiclesDB2.instance.updateVehicleAndTrailer(this.getVehicle());
			}
		}
	}

	public void save(String string) throws IOException {
		this.SaveFileName = string;
		synchronized (SliceY.SliceBufferLock) {
			SliceY.SliceBuffer.clear();
			SliceY.SliceBuffer.putInt(184);
			GameWindow.WriteString(SliceY.SliceBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
			this.save(SliceY.SliceBuffer);
			File file = (new File(string)).getAbsoluteFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				try {
					bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
				} catch (Throwable throwable) {
					try {
						bufferedOutputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedOutputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileOutputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileOutputStream.close();
		}
	}

	public void load(String string) throws IOException {
		File file = (new File(string)).getAbsoluteFile();
		if (file.exists()) {
			this.SaveFileName = string;
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
						SliceY.SliceBuffer.limit(int1);
						int int2 = SliceY.SliceBuffer.getInt();
						if (int2 >= 69) {
							this.SaveFileIP = GameWindow.ReadStringUTF(SliceY.SliceBuffer);
							if (int2 < 71) {
								this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
							}
						} else if (GameClient.bClient) {
							this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
						}

						this.load(SliceY.SliceBuffer, int2);
					}
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		}
	}

	public void setVehicle4TestCollision(BaseVehicle baseVehicle) {
		this.vehicle4testCollision = baseVehicle;
	}

	public boolean isSaveFileInUse() {
		for (int int1 = 0; int1 < numPlayers; ++int1) {
			IsoPlayer player = players[int1];
			if (player != null) {
				if (this.sqlID != -1 && this.sqlID == player.sqlID) {
					return true;
				}

				if (this.SaveFileName != null && this.SaveFileName.equals(player.SaveFileName)) {
					return true;
				}
			}
		}

		return false;
	}

	public void removeSaveFile() {
		try {
			if (PlayerDB.isAvailable()) {
				PlayerDB.getInstance().saveLocalPlayersForce();
			}

			if (this.isNPC() && this.SaveFileName != null) {
				File file = (new File(this.SaveFileName)).getAbsoluteFile();
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public boolean isSaveFileIPValid() {
		return isServerPlayerIDValid(this.SaveFileIP);
	}

	public String getObjectName() {
		return "Player";
	}

	public int getJoypadBind() {
		return this.JoypadBind;
	}

	public boolean isLBPressed() {
		return this.JoypadBind == -1 ? false : JoypadManager.instance.isLBPressed(this.JoypadBind);
	}

	public Vector2 getControllerAimDir(Vector2 vector2) {
		if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive) {
			float float1 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
			float float2 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
			if (this.bJoypadIgnoreAimUntilCentered) {
				if (vector2.set(float1, float2).getLengthSquared() > 0.0F) {
					return vector2.set(0.0F, 0.0F);
				}

				this.bJoypadIgnoreAimUntilCentered = false;
			}

			if (vector2.set(float1, float2).getLength() < 0.3F) {
				float2 = 0.0F;
				float1 = 0.0F;
			}

			if (float1 == 0.0F && float2 == 0.0F) {
				return vector2.set(0.0F, 0.0F);
			}

			vector2.set(float1, float2);
			vector2.normalize();
			vector2.rotate(-0.7853982F);
		}

		return vector2;
	}

	public Vector2 getMouseAimVector(Vector2 vector2) {
		int int1 = Mouse.getX();
		int int2 = Mouse.getY();
		vector2.x = IsoUtils.XToIso((float)int1, (float)int2 + 55.0F * this.def.getScaleY(), this.getZ()) - this.getX();
		vector2.y = IsoUtils.YToIso((float)int1, (float)int2 + 55.0F * this.def.getScaleY(), this.getZ()) - this.getY();
		vector2.normalize();
		return vector2;
	}

	public Vector2 getAimVector(Vector2 vector2) {
		return this.JoypadBind == -1 ? this.getMouseAimVector(vector2) : this.getControllerAimDir(vector2);
	}

	public float getGlobalMovementMod(boolean boolean1) {
		return !this.isGhostMode() && !this.isNoClip() ? super.getGlobalMovementMod(boolean1) : 1.0F;
	}

	public boolean isInTrees2(boolean boolean1) {
		return !this.isGhostMode() && !this.isNoClip() ? super.isInTrees2(boolean1) : false;
	}

	public float getMoveSpeed() {
		float float1 = 1.0F;
		for (int int1 = BodyPartType.ToIndex(BodyPartType.UpperLeg_L); int1 <= BodyPartType.ToIndex(BodyPartType.Foot_R); ++int1) {
			BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			float float2 = 1.0F;
			if (bodyPart.getFractureTime() > 20.0F) {
				float2 = 0.4F;
				if (bodyPart.getFractureTime() > 50.0F) {
					float2 = 0.3F;
				}

				if (bodyPart.getSplintFactor() > 0.0F) {
					float2 += bodyPart.getSplintFactor() / 10.0F;
				}
			}

			if (bodyPart.getFractureTime() < 20.0F && bodyPart.getSplintFactor() > 0.0F) {
				float2 = 0.8F;
			}

			if (float2 > 0.7F && bodyPart.getDeepWoundTime() > 0.0F) {
				float2 = 0.7F;
				if (bodyPart.bandaged()) {
					float2 += 0.2F;
				}
			}

			if (float2 < float1) {
				float1 = float2;
			}
		}

		if (float1 != 1.0F) {
			return this.MoveSpeed * float1;
		} else if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 4 && this.Traits.AdrenalineJunkie.isSet()) {
			float float3 = 1.0F;
			int int2 = this.getMoodles().getMoodleLevel(MoodleType.Panic) + 1;
			float3 += (float)int2 / 50.0F;
			return this.MoveSpeed * float3;
		} else {
			return this.MoveSpeed;
		}
	}

	public void setMoveSpeed(float float1) {
		this.MoveSpeed = float1;
	}

	public float getTorchStrength() {
		if (this.bRemote) {
			return this.mpTorchStrength;
		} else {
			InventoryItem inventoryItem = this.getActiveLightItem();
			return inventoryItem != null ? inventoryItem.getLightStrength() : 0.0F;
		}
	}

	public void Scratched() {
	}

	public void Bitten() {
	}

	public float getInvAimingMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
		if (int1 == 1) {
			return 0.9F;
		} else if (int1 == 2) {
			return 0.86F;
		} else if (int1 == 3) {
			return 0.82F;
		} else if (int1 == 4) {
			return 0.74F;
		} else if (int1 == 5) {
			return 0.7F;
		} else if (int1 == 6) {
			return 0.66F;
		} else if (int1 == 7) {
			return 0.62F;
		} else if (int1 == 8) {
			return 0.58F;
		} else if (int1 == 9) {
			return 0.54F;
		} else {
			return int1 == 10 ? 0.5F : 0.9F;
		}
	}

	public float getAimingMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
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
			return 1.36F;
		} else if (int1 == 9) {
			return 1.4F;
		} else {
			return int1 == 10 ? 1.5F : 1.0F;
		}
	}

	public float getReloadingMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Reloading);
		return 3.5F - (float)int1 * 0.25F;
	}

	public float getAimingRangeMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
		if (int1 == 1) {
			return 1.2F;
		} else if (int1 == 2) {
			return 1.28F;
		} else if (int1 == 3) {
			return 1.36F;
		} else if (int1 == 4) {
			return 1.42F;
		} else if (int1 == 5) {
			return 1.5F;
		} else if (int1 == 6) {
			return 1.58F;
		} else if (int1 == 7) {
			return 1.66F;
		} else if (int1 == 8) {
			return 1.72F;
		} else if (int1 == 9) {
			return 1.8F;
		} else {
			return int1 == 10 ? 2.0F : 1.1F;
		}
	}

	public boolean isPathfindRunning() {
		return this.pathfindRun;
	}

	public void setPathfindRunning(boolean boolean1) {
		this.pathfindRun = boolean1;
	}

	public boolean isBannedAttacking() {
		return this.bBannedAttacking;
	}

	public void setBannedAttacking(boolean boolean1) {
		this.bBannedAttacking = boolean1;
	}

	public float getInvAimingRangeMod() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
		if (int1 == 1) {
			return 0.8F;
		} else if (int1 == 2) {
			return 0.7F;
		} else if (int1 == 3) {
			return 0.62F;
		} else if (int1 == 4) {
			return 0.56F;
		} else if (int1 == 5) {
			return 0.45F;
		} else if (int1 == 6) {
			return 0.38F;
		} else if (int1 == 7) {
			return 0.31F;
		} else if (int1 == 8) {
			return 0.24F;
		} else if (int1 == 9) {
			return 0.17F;
		} else {
			return int1 == 10 ? 0.1F : 0.8F;
		}
	}

	private void updateCursorVisibility() {
		if (this.isAiming()) {
			if (this.PlayerIndex == 0 && this.JoypadBind == -1 && !this.isDead()) {
				if (!Core.getInstance().getOptionShowCursorWhileAiming()) {
					if (Core.getInstance().getIsoCursorVisibility() != 0) {
						if (!UIManager.isForceCursorVisible()) {
							int int1 = Mouse.getXA();
							int int2 = Mouse.getYA();
							if (int1 >= IsoCamera.getScreenLeft(0) && int1 <= IsoCamera.getScreenLeft(0) + IsoCamera.getScreenWidth(0)) {
								if (int2 >= IsoCamera.getScreenTop(0) && int2 <= IsoCamera.getScreenTop(0) + IsoCamera.getScreenHeight(0)) {
									Mouse.setCursorVisible(false);
								}
							}
						}
					}
				}
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (!getInstance().checkCanSeeClient(this)) {
			this.setTargetAlpha(0.0F);
			getInstance().spottedPlayerTimer.remove(this.getRemoteID());
		} else {
			this.setTargetAlpha(1.0F);
		}

		super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
	}

	public void renderlast() {
		super.renderlast();
	}

	public void doBeatenVehicle(float float1, float float2, float float3, boolean boolean1) {
		if (GameClient.bClient && this.isLocalPlayer() && boolean1) {
			this.changeState(ForecastBeatenPlayerState.instance());
		} else {
			if (GameClient.bClient && !this.isLocalPlayer() && !boolean1) {
				GameClient.instance.sendOnBeaten(this, float1, float2, float3);
			}

			float float4 = 1.0F;
			switch (SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue()) {
			case 1: 
				float4 = 0.0F;
				break;
			
			case 2: 
				float4 = 0.5F;
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				float4 = 2.0F;
				break;
			
			case 5: 
				float4 = 5.0F;
			
			}

			float float5 = float1 * float4;
			if (float5 > 0.0F) {
				int int1 = (int)(2.0F + float5 * 0.07F);
				for (int int2 = 0; int2 < int1; ++int2) {
					int int3 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
					BodyPart bodyPart = this.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int3));
					float float6 = Math.max(Rand.Next(float5 - 15.0F, float5), 5.0F);
					if (this.Traits.FastHealer.isSet()) {
						float6 = (float)((double)float6 * 0.8);
					} else if (this.Traits.SlowHealer.isSet()) {
						float6 = (float)((double)float6 * 1.2);
					}

					switch (SandboxOptions.instance.InjurySeverity.getValue()) {
					case 1: 
						float6 *= 0.5F;
						break;
					
					case 3: 
						float6 *= 1.5F;
					
					}

					float6 = (float)((double)float6 * 0.9);
					bodyPart.AddDamage(float6);
					if (float6 > 40.0F && Rand.Next(12) == 0) {
						bodyPart.generateDeepWound();
					}

					if (float6 > 10.0F && Rand.Next(100) <= 10 && SandboxOptions.instance.BoneFracture.getValue()) {
						bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0F, float6 + 10.0F), Rand.Next(float6 + 20.0F, float6 + 30.0F)));
					}

					if (float6 > 30.0F && Rand.Next(100) <= 80 && SandboxOptions.instance.BoneFracture.getValue() && int3 == BodyPartType.ToIndex(BodyPartType.Head)) {
						bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0F, float6 + 10.0F), Rand.Next(float6 + 20.0F, float6 + 30.0F)));
					}

					if (float6 > 10.0F && Rand.Next(100) <= 60 && SandboxOptions.instance.BoneFracture.getValue() && int3 > BodyPartType.ToIndex(BodyPartType.Groin)) {
						bodyPart.setFractureTime(Rand.Next(Rand.Next(10.0F, float6 + 20.0F), Rand.Next(float6 + 30.0F, float6 + 40.0F)));
					}
				}

				this.getBodyDamage().Update();
			}

			if (this.isAlive() && this.getCurrentState() != PlayerHitReactionState.instance() && this.getCurrentState() != PlayerFallDownState.instance() && this.getCurrentState() != PlayerOnGroundState.instance() && !this.m_bKnockedDown) {
				if (float5 > 15.0F) {
					this.setM_bKnockedDown(true);
					this.setReanimateTimer((float)(20 + Rand.Next(60)));
				}

				this.setHitReaction("HitReaction");
				this.actionContext.reportEvent("washit");
				if (GameClient.bClient) {
					GameClient.sendGotHitByVehicle(this);
				}
			}
		}
	}

	public void update() {
		IsoPlayer.s_performance.update.invokeAndMeasure(this, IsoPlayer::updateInternal1);
	}

	private void updateInternal1() {
		if (this.replay != null) {
			this.replay.update();
		}

		boolean boolean1 = this.updateInternal2();
		GameClient.instance.sendPlayer2(this);
		if (boolean1) {
			this.updateLOS();
			super.update();
		}
	}

	private boolean updateInternal2() {
		if (isTestAIMode) {
			this.isNPC = true;
		}

		if (!this.attackStarted) {
			this.initiateAttack = false;
			this.setAttackType((String)null);
		}

		if (!this.isRunning() && !this.isSprinting()) {
			this.runningTime = 0.0F;
		} else {
			this.runningTime += GameTime.getInstance().getMultiplier() / 1.6F;
		}

		if (this.getLastCollideTime() > 0.0F) {
			this.setLastCollideTime(this.getLastCollideTime() - GameTime.getInstance().getMultiplier() / 1.6F);
		}

		this.updateDeathDragDown();
		this.updateGodModeKey();
		this.doDeferredMovement();
		this.updateHitByVehicle();
		this.updateEmitter();
		this.updateMechanicsItems();
		this.updateHeavyBreathing();
		this.updateTemperatureCheck();
		this.updateAimingStance();
		if (SystemDisabler.doCharacterStats) {
			this.nutrition.update();
		}

		this.fitness.update();
		this.updateSoundListener();
		if (GameClient.bClient && this.isLocalPlayer() && this.getSafetyCooldown() > 0.0F) {
			this.setSafetyCooldown(this.getSafetyCooldown() - GameTime.instance.getRealworldSecondsSinceLastUpdate());
		}

		if (!GameClient.bClient && !GameServer.bServer && this.bDeathFinished) {
			return false;
		} else {
			if (!GameClient.bClient && this.getCurrentBuildingDef() != null && !this.isInvisible()) {
				this.getCurrentBuildingDef().setHasBeenVisited(true);
			}

			if (this.checkSafehouse > 0 && GameServer.bServer) {
				--this.checkSafehouse;
				if (this.checkSafehouse == 0) {
					this.checkSafehouse = 200;
					SafeHouse safeHouse = SafeHouse.isSafeHouse(this.getCurrentSquare(), (String)null, false);
					if (safeHouse != null) {
						safeHouse.updateSafehouse(this);
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
			this.checkActionGroup();
			if (this.updateRemotePlayer()) {
				if (this.updateWhileDead()) {
					return true;
				} else {
					this.updateHeartSound();
					this.checkIsNearWall();
					this.updateExt();
					this.setCollidable(true);
					return true;
				}
			} else {
				assert !GameServer.bServer;
				assert !this.bRemote;
				assert !GameClient.bClient || this.isLocalPlayer();
				IsoCamera.CamCharacter = this;
				instance = this;
				if (this.isLocalPlayer()) {
					IsoCamera.cameras[this.PlayerIndex].update();
					if (UIManager.getMoodleUI((double)this.PlayerIndex) != null) {
						UIManager.getMoodleUI((double)this.PlayerIndex).setCharacter(this);
					}
				}

				if (this.closestZombie > 1.2F) {
					this.slowTimer = -1.0F;
					this.slowFactor = 0.0F;
				}

				this.ContextPanic -= 1.5F * GameTime.instance.getTimeDelta();
				if (this.ContextPanic < 0.0F) {
					this.ContextPanic = 0.0F;
				}

				this.lastSeenZombieTime += (double)(GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 60.0F / 60.0F);
				LuaEventManager.triggerEvent("OnPlayerUpdate", this);
				if (this.pressedMovement(false)) {
					this.ContextPanic = 0.0F;
					this.ticksSincePressedMovement = 0.0F;
				} else {
					this.ticksSincePressedMovement += GameTime.getInstance().getMultiplier() / 1.6F;
				}

				this.setVariable("pressedMovement", this.pressedMovement(true));
				if (this.updateWhileDead()) {
					return true;
				} else {
					this.updateHeartSound();
					this.updateWorldAmbiance();
					this.updateSneakKey();
					this.checkIsNearWall();
					this.updateExt();
					this.updateInteractKeyPanic();
					if (this.isAsleep()) {
						this.m_isPlayerMoving = false;
					}

					if ((this.getVehicle() == null || !this.getVehicle().isDriver(this) || !this.getVehicle().hasHorn() || Core.getInstance().getKey("Shout") != Core.getInstance().getKey("VehicleHorn")) && !this.isAsleep() && this.PlayerIndex == 0 && !this.Speaking && GameKeyboard.isKeyDown(Core.getInstance().getKey("Shout")) && !this.isNPC) {
					}

					if (!this.getIgnoreMovement() && !this.isAsleep()) {
						if (this.checkActionsBlockingMovement()) {
							if (this.getVehicle() != null && this.getVehicle().getDriver() == this && this.getVehicle().getController() != null) {
								this.getVehicle().getController().clientControls.reset();
								this.getVehicle().updatePhysics();
							}

							return true;
						} else {
							this.enterExitVehicle();
							this.checkActionGroup();
							this.checkReloading();
							if (this.checkActionsBlockingMovement()) {
								return true;
							} else if (this.getVehicle() != null) {
								this.updateWhileInVehicle();
								return true;
							} else {
								this.checkVehicleContainers();
								this.setCollidable(true);
								this.updateCursorVisibility();
								this.bSeenThisFrame = false;
								this.bCouldBeSeenThisFrame = false;
								if (IsoCamera.CamCharacter == null && GameClient.bClient) {
									IsoCamera.CamCharacter = instance;
								}

								if (this.updateUseKey()) {
									return true;
								} else {
									this.updateEnableModelsKey();
									this.updateChangeCharacterKey();
									boolean boolean1 = false;
									boolean boolean2 = false;
									this.setRunning(false);
									this.setSprinting(false);
									this.useChargeTime = this.chargeTime;
									if (!this.isBlockMovement() && !this.isNPC) {
										if (!this.isCharging && !this.isChargingLT) {
											this.chargeTime = 0.0F;
										} else {
											this.chargeTime += 1.0F * GameTime.instance.getMultiplier();
										}

										this.UpdateInputState(this.inputState);
										boolean2 = this.inputState.bMelee;
										boolean1 = this.inputState.isAttacking;
										this.setRunning(this.inputState.bRunning);
										this.setSprinting(this.inputState.bSprinting);
										if (this.isSprinting() && !this.JustMoved) {
											this.setSprinting(false);
										}

										if (this.isSprinting()) {
											this.setRunning(false);
										}

										this.setIsAiming(this.inputState.isAiming);
										this.isCharging = this.inputState.isCharging;
										this.isChargingLT = this.inputState.isChargingLT;
										this.updateMovementRates();
										if (this.isAiming()) {
											this.StopAllActionQueueAiming();
										}

										if (boolean1) {
											this.setIsAiming(true);
										}

										this.Waiting = false;
										if (this.isAiming()) {
											this.setMoving(false);
											this.setRunning(false);
											this.setSprinting(false);
										}

										++this.TicksSinceSeenZombie;
									}

									if ((double)this.playerMoveDir.x == 0.0 && (double)this.playerMoveDir.y == 0.0) {
										this.setForceRun(false);
										this.setForceSprint(false);
									}

									this.movementLastFrame.x = this.playerMoveDir.x;
									this.movementLastFrame.y = this.playerMoveDir.y;
									if (this.stateMachine.getCurrent() != StaggerBackState.instance() && this.stateMachine.getCurrent() != FakeDeadZombieState.instance() && UIManager.speedControls != null) {
										if (GameKeyboard.isKeyDown(88) && Translator.debug) {
											Translator.loadFiles();
										}

										this.JustMoved = false;
										IsoPlayer.MoveVars moveVars = s_moveVars;
										this.updateMovementFromInput(moveVars);
										if (!this.JustMoved && this.hasPath() && !this.getPathFindBehavior2().bStopping) {
											this.JustMoved = true;
										}

										float float1 = moveVars.strafeX;
										float float2 = moveVars.strafeY;
										if (this.JustMoved && !this.isNPC && !this.hasPath()) {
											if (UIManager.getSpeedControls().getCurrentGameSpeed() > 1) {
												UIManager.getSpeedControls().SetCurrentGameSpeed(1);
											}
										} else if (this.stats.endurance < this.stats.endurancedanger && Rand.Next((int)(300.0F * GameTime.instance.getInvMultiplier())) == 0) {
											this.xp.AddXP(PerkFactory.Perks.Fitness, 1.0F);
										}

										if (this.JustMoved) {
											this.setBeenMovingFor(this.getBeenMovingFor() + 1.25F * GameTime.getInstance().getMultiplier());
										} else {
											this.setBeenMovingFor(this.getBeenMovingFor() - 0.625F * GameTime.getInstance().getMultiplier());
										}

										if (this.JustMoved && this.isSprinting()) {
											this.setBeenSprintingFor(this.getBeenSprintingFor() + 1.25F * GameTime.getInstance().getMultiplier());
										} else {
											this.setBeenSprintingFor(0.0F);
										}

										float float3 = 1.0F;
										float float4 = 0.0F;
										if (this.JustMoved && !this.isNPC) {
											if (!this.isRunning() && !this.isSprinting()) {
												float4 = 1.0F;
											} else {
												float4 = 1.5F;
											}
										}

										float3 *= float4;
										if (float3 > 1.0F) {
											float3 *= this.getSprintMod();
										}

										if (float3 > 1.0F && this.Traits.Athletic.isSet()) {
											float3 *= 1.2F;
										}

										if (float3 > 1.0F) {
											if (this.Traits.Overweight.isSet()) {
												float3 *= 0.99F;
											}

											if (this.Traits.Obese.isSet()) {
												float3 *= 0.85F;
											}

											if (this.getNutrition().getWeight() > 120.0F) {
												float3 *= 0.97F;
											}

											if (this.Traits.OutOfShape.isSet()) {
												float3 *= 0.99F;
											}

											if (this.Traits.Unfit.isSet()) {
												float3 *= 0.8F;
											}
										}

										this.updateEndurance(float3);
										if (this.isAiming() && this.JustMoved) {
											float3 *= 0.7F;
										}

										if (this.isAiming()) {
											float3 *= this.getNimbleMod();
										}

										this.isWalking = false;
										if (float3 > 0.0F && !this.isNPC) {
											this.isWalking = true;
											LuaEventManager.triggerEvent("OnPlayerMove", this);
										}

										if (this.JustMoved) {
											this.sprite.Animate = true;
										}

										if (this.isNPC && this.GameCharacterAIBrain != null) {
											boolean2 = this.GameCharacterAIBrain.HumanControlVars.bMelee;
											this.bBannedAttacking = this.GameCharacterAIBrain.HumanControlVars.bBannedAttacking;
										}

										this.m_meleePressed = boolean2;
										if (boolean2) {
											if (!this.m_lastAttackWasShove) {
												this.setMeleeDelay(Math.min(this.getMeleeDelay(), 2.0F));
											}

											if (!this.bBannedAttacking && this.isAuthorizeShoveStomp() && this.CanAttack() && this.getMeleeDelay() <= 0.0F) {
												this.bDoShove = true;
												if (!this.isCharging && !this.isChargingLT) {
													this.setIsAiming(false);
												}

												this.AttemptAttack(this.useChargeTime);
												this.useChargeTime = 0.0F;
												this.chargeTime = 0.0F;
											}
										} else if (this.isAiming() && this.CanAttack()) {
											if (this.DragCharacter != null) {
												this.DragObject = null;
												this.DragCharacter.Dragging = false;
												this.DragCharacter = null;
											}

											if (boolean1 && !this.bBannedAttacking) {
												this.sprite.Animate = true;
												if (this.getRecoilDelay() <= 0.0F && this.getMeleeDelay() <= 0.0F) {
													this.AttemptAttack(this.useChargeTime);
												}

												this.useChargeTime = 0.0F;
												this.chargeTime = 0.0F;
											}
										}

										if (this.isAiming() && !this.isNPC) {
											if (this.JoypadBind != -1 && !this.bJoypadMovementActive) {
												if (this.getForwardDirection().getLengthSquared() > 0.0F) {
													this.DirectionFromVector(this.getForwardDirection());
												}
											} else {
												Vector2 vector2 = tempVector2.set(0.0F, 0.0F);
												if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
													this.getControllerAimDir(vector2);
												} else {
													this.getMouseAimVector(vector2);
												}

												if (vector2.getLengthSquared() > 0.0F) {
													this.DirectionFromVector(vector2);
													this.setForwardDirection(vector2);
												}
											}

											moveVars.NewFacing = this.dir;
										}

										if (this.getForwardDirection().x == 0.0F && this.getForwardDirection().y == 0.0F) {
											this.setForwardDirection(this.dir.ToVector());
										}

										if (this.lastAngle.x != this.getForwardDirection().x || this.lastAngle.y != this.getForwardDirection().y) {
											this.lastAngle.x = this.getForwardDirection().x;
											this.lastAngle.y = this.getForwardDirection().y;
											this.dirtyRecalcGridStackTime = 2.0F;
										}

										this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
										AnimationPlayer animationPlayer = this.getAnimationPlayer();
										if (animationPlayer != null && animationPlayer.isReady()) {
											float float5 = animationPlayer.getAngle() + animationPlayer.getTwistAngle();
											this.dir = IsoDirections.fromAngle(tempVector2.setLengthAndDirection(float5, 1.0F));
										} else if (!this.bFalling && !this.isAiming() && !boolean1) {
											this.dir = moveVars.NewFacing;
										}

										if (this.isAiming() && (GameWindow.ActivatedJoyPad == null || this.JoypadBind == -1)) {
											this.playerMoveDir.x = moveVars.moveX;
											this.playerMoveDir.y = moveVars.moveY;
										}

										if (!this.isAiming() && this.JustMoved) {
											this.playerMoveDir.x = this.getForwardDirection().x;
											this.playerMoveDir.y = this.getForwardDirection().y;
										}

										if (this.JustMoved) {
											if (this.isSprinting()) {
												this.CurrentSpeed = 1.5F;
											} else if (this.isRunning()) {
												this.CurrentSpeed = 1.0F;
											} else {
												this.CurrentSpeed = 0.5F;
											}
										} else {
											this.CurrentSpeed = 0.0F;
										}

										this.LastRT = JoypadManager.instance.isRTPressed(this.getJoypadBind());
										boolean boolean3 = this.IsInMeleeAttack();
										if (!this.CharacterActions.isEmpty()) {
											BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
											if (baseAction.overrideAnimation) {
												boolean3 = true;
											}
										}

										if (!boolean3 && !this.isForceOverrideAnim()) {
											if (this.getPath2() == null) {
												if (this.CurrentSpeed > 0.0F && (!this.bClimbing || this.lastFallSpeed > 0.0F)) {
													if (!this.isRunning() && !this.isSprinting()) {
														this.StopAllActionQueueWalking();
													} else {
														this.StopAllActionQueueRunning();
													}
												}
											} else {
												this.StopAllActionQueueWalking();
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

										this.playerMoveDir.setLength(this.CurrentSpeed);
										if (this.playerMoveDir.x != 0.0F || this.playerMoveDir.y != 0.0F) {
											this.dirtyRecalcGridStackTime = 10.0F;
										}

										if (this.getPath2() != null && this.current != this.last) {
											this.dirtyRecalcGridStackTime = 10.0F;
										}

										this.closestZombie = 1000000.0F;
										this.weight = 0.3F;
										this.separate();
										this.updateSleepingPillsTaken();
										this.updateTorchStrength();
										if (this.isNPC && this.GameCharacterAIBrain != null) {
											this.GameCharacterAIBrain.postUpdateHuman(this);
											this.initiateAttack = this.GameCharacterAIBrain.HumanControlVars.initiateAttack;
											this.setRunning(this.GameCharacterAIBrain.HumanControlVars.bRunning);
											float1 = this.GameCharacterAIBrain.HumanControlVars.strafeX;
											float2 = this.GameCharacterAIBrain.HumanControlVars.strafeY;
											this.JustMoved = this.GameCharacterAIBrain.HumanControlVars.JustMoved;
											this.updateMovementRates();
										}

										this.m_isPlayerMoving = this.JustMoved || this.getPath2() != null && !this.getPathFindBehavior2().bStopping;
										boolean boolean4 = this.isInTrees();
										float float6;
										if (boolean4) {
											float6 = "parkranger".equals(this.getDescriptor().getProfession()) ? 1.3F : 1.0F;
											float6 = "lumberjack".equals(this.getDescriptor().getProfession()) ? 1.15F : float6;
											if (this.isRunning()) {
												float6 *= 1.1F;
											}

											this.setVariable("WalkSpeedTrees", float6);
										}

										if ((boolean4 || this.m_walkSpeed < 0.4F || this.m_walkInjury > 0.5F) && this.isSprinting() && !this.isGhostMode()) {
											if ((double)this.runSpeedModifier < 1.0) {
												this.setMoodleCantSprint(true);
											}

											this.setSprinting(false);
											this.setForceSprint(false);
											if (this.isInTreesNoBush()) {
												this.setBumpType("left");
												this.setVariable("BumpDone", false);
												this.setVariable("BumpFall", true);
												this.setVariable("TripObstacleType", "tree");
												this.actionContext.reportEvent("wasBumped");
											}
										}

										this.m_deltaX = float1;
										this.m_deltaY = float2;
										this.m_windspeed = ClimateManager.getInstance().getWindSpeedMovement();
										float6 = this.getForwardDirection().getDirectionNeg();
										this.m_windForce = ClimateManager.getInstance().getWindForceMovement(this, float6);
										return true;
									} else {
										return true;
									}
								}
							}
						}
					} else {
						return true;
					}
				}
			}
		}
	}

	private void updateMovementFromInput(IsoPlayer.MoveVars moveVars) {
		moveVars.moveX = 0.0F;
		moveVars.moveY = 0.0F;
		moveVars.strafeX = 0.0F;
		moveVars.strafeY = 0.0F;
		moveVars.NewFacing = this.dir;
		if (!TutorialManager.instance.StealControl) {
			if (!this.isBlockMovement()) {
				if (!this.isNPC) {
					if (!(this.fallTime > 2.0F)) {
						if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
							this.updateMovementFromJoypad(moveVars);
						}

						if (this.PlayerIndex == 0 && this.JoypadBind == -1) {
							this.updateMovementFromKeyboardMouse(moveVars);
						}

						if (this.JustMoved) {
							this.getForwardDirection().normalize();
							UIManager.speedControls.SetCurrentGameSpeed(1);
						}
					}
				}
			}
		}
	}

	private void updateMovementFromJoypad(IsoPlayer.MoveVars moveVars) {
		this.playerMoveDir.x = 0.0F;
		this.playerMoveDir.y = 0.0F;
		this.getJoypadAimVector(tempVector2);
		float float1 = tempVector2.x;
		float float2 = tempVector2.y;
		Vector2 vector2 = this.getJoypadMoveVector(tempVector2);
		if (vector2.getLength() > 1.0F) {
			vector2.setLength(1.0F);
		}

		float float3 = vector2.x;
		float float4 = vector2.y;
		Vector2 vector22;
		if (Math.abs(float3) > 0.0F) {
			vector22 = this.playerMoveDir;
			vector22.x += 0.04F * float3;
			vector22 = this.playerMoveDir;
			vector22.y -= 0.04F * float3;
			this.JustMoved = true;
		}

		if (Math.abs(float4) > 0.0F) {
			vector22 = this.playerMoveDir;
			vector22.y += 0.04F * float4;
			vector22 = this.playerMoveDir;
			vector22.x += 0.04F * float4;
			this.JustMoved = true;
		}

		if (JoypadManager.instance.isL3Pressed(this.JoypadBind)) {
			if (!this.L3Pressed) {
				this.setSneaking(!this.isSneaking());
				this.L3Pressed = true;
			}
		} else {
			this.L3Pressed = false;
		}

		this.playerMoveDir.setLength(0.05F * (float)Math.pow((double)vector2.getLength(), 9.0));
		if (float1 == 0.0F && float2 == 0.0F) {
			if ((float3 != 0.0F || float4 != 0.0F) && this.playerMoveDir.getLengthSquared() > 0.0F) {
				vector2 = tempVector2.set(this.playerMoveDir);
				vector2.normalize();
				moveVars.NewFacing = IsoDirections.fromAngle(vector2);
			}
		} else {
			Vector2 vector23 = tempVector2.set(float1, float2);
			vector23.normalize();
			moveVars.NewFacing = IsoDirections.fromAngle(vector23);
		}

		PathFindBehavior2 pathFindBehavior2 = this.getPathFindBehavior2();
		if (this.playerMoveDir.x == 0.0F && this.playerMoveDir.y == 0.0F && this.getPath2() != null && pathFindBehavior2.isStrafing() && !pathFindBehavior2.bStopping) {
			this.playerMoveDir.set(pathFindBehavior2.getTargetX() - this.x, pathFindBehavior2.getTargetY() - this.y);
			this.playerMoveDir.normalize();
		}

		if (this.playerMoveDir.x != 0.0F || this.playerMoveDir.y != 0.0F) {
			if (this.isStrafing()) {
				tempo.set(this.playerMoveDir.x, -this.playerMoveDir.y);
				tempo.normalize();
				float float5 = this.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
				if ((double)float5 > 6.283185307179586) {
					float5 = (float)((double)float5 - 6.283185307179586);
				}

				if (float5 < 0.0F) {
					float5 = (float)((double)float5 + 6.283185307179586);
				}

				tempo.rotate(float5);
				moveVars.strafeX = tempo.x;
				moveVars.strafeY = tempo.y;
				this.m_IPX = this.playerMoveDir.x;
				this.m_IPY = this.playerMoveDir.y;
			} else {
				moveVars.moveX = this.playerMoveDir.x;
				moveVars.moveY = this.playerMoveDir.y;
				tempo.set(this.playerMoveDir);
				tempo.normalize();
				this.setForwardDirection(tempo);
			}
		}
	}

	private void updateMovementFromKeyboardMouse(IsoPlayer.MoveVars moveVars) {
		int int1 = Core.getInstance().getKey("Left");
		int int2 = Core.getInstance().getKey("Right");
		int int3 = Core.getInstance().getKey("Forward");
		int int4 = Core.getInstance().getKey("Backward");
		boolean boolean1 = GameKeyboard.isKeyDown(int1);
		boolean boolean2 = GameKeyboard.isKeyDown(int2);
		boolean boolean3 = GameKeyboard.isKeyDown(int3);
		boolean boolean4 = GameKeyboard.isKeyDown(int4);
		if (!boolean1 && !boolean2 && !boolean3 && !boolean4 || int1 != 30 && int2 != 30 && int3 != 30 && int4 != 30 || !GameKeyboard.isKeyDown(29) && !GameKeyboard.isKeyDown(157) || !UIManager.isMouseOverInventory() || !Core.getInstance().isSelectingAll()) {
			if (!this.isIgnoreInputsForDirection()) {
				if (Core.bAltMoveMethod) {
					if (boolean1 && !boolean2) {
						moveVars.moveX -= 0.04F;
						moveVars.NewFacing = IsoDirections.W;
					}

					if (boolean2 && !boolean1) {
						moveVars.moveX += 0.04F;
						moveVars.NewFacing = IsoDirections.E;
					}

					if (boolean3 && !boolean4) {
						moveVars.moveY -= 0.04F;
						if (moveVars.NewFacing == IsoDirections.W) {
							moveVars.NewFacing = IsoDirections.NW;
						} else if (moveVars.NewFacing == IsoDirections.E) {
							moveVars.NewFacing = IsoDirections.NE;
						} else {
							moveVars.NewFacing = IsoDirections.N;
						}
					}

					if (boolean4 && !boolean3) {
						moveVars.moveY += 0.04F;
						if (moveVars.NewFacing == IsoDirections.W) {
							moveVars.NewFacing = IsoDirections.SW;
						} else if (moveVars.NewFacing == IsoDirections.E) {
							moveVars.NewFacing = IsoDirections.SE;
						} else {
							moveVars.NewFacing = IsoDirections.S;
						}
					}
				} else {
					if (boolean1) {
						moveVars.moveX = -1.0F;
					} else if (boolean2) {
						moveVars.moveX = 1.0F;
					}

					if (boolean3) {
						moveVars.moveY = 1.0F;
					} else if (boolean4) {
						moveVars.moveY = -1.0F;
					}

					if (moveVars.moveX != 0.0F || moveVars.moveY != 0.0F) {
						tempo.set(moveVars.moveX, moveVars.moveY);
						tempo.normalize();
						moveVars.NewFacing = IsoDirections.fromAngle(tempo);
					}
				}
			}

			PathFindBehavior2 pathFindBehavior2 = this.getPathFindBehavior2();
			if (moveVars.moveX == 0.0F && moveVars.moveY == 0.0F && this.getPath2() != null && pathFindBehavior2.isStrafing() && !pathFindBehavior2.bStopping) {
				Vector2 vector2 = tempo.set(pathFindBehavior2.getTargetX() - this.x, pathFindBehavior2.getTargetY() - this.y);
				Vector2 vector22 = tempo2.set(-1.0F, 0.0F);
				float float1 = 1.0F;
				float float2 = vector2.dot(vector22);
				float float3 = float2 / float1;
				vector22 = tempo2.set(0.0F, -1.0F);
				float2 = vector2.dot(vector22);
				float float4 = float2 / float1;
				tempo.set(float4, float3);
				tempo.normalize();
				tempo.rotate(0.7853982F);
				moveVars.moveX = tempo.x;
				moveVars.moveY = tempo.y;
			}

			if (moveVars.moveX != 0.0F || moveVars.moveY != 0.0F) {
				if (this.stateMachine.getCurrent() == PathFindState.instance()) {
					this.setDefaultState();
				}

				this.JustMoved = true;
				this.setMoveDelta(1.0F);
				if (this.isStrafing()) {
					tempo.set(moveVars.moveX, moveVars.moveY);
					tempo.normalize();
					float float5 = this.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
					float5 = (float)((double)float5 + 0.7853981633974483);
					if ((double)float5 > 6.283185307179586) {
						float5 = (float)((double)float5 - 6.283185307179586);
					}

					if (float5 < 0.0F) {
						float5 = (float)((double)float5 + 6.283185307179586);
					}

					tempo.rotate(float5);
					moveVars.strafeX = tempo.x;
					moveVars.strafeY = tempo.y;
					this.m_IPX = moveVars.moveX;
					this.m_IPY = moveVars.moveY;
				} else {
					tempo.set(moveVars.moveX, -moveVars.moveY);
					tempo.normalize();
					tempo.rotate(-0.7853982F);
					this.setForwardDirection(tempo);
				}
			}
		}
	}

	private void updateAimingStance() {
		if (this.isVariable("LeftHandMask", "RaiseHand")) {
			this.clearVariable("LeftHandMask");
		}

		if (this.isAiming() && !this.isCurrentState(SwipeStatePlayer.instance())) {
			HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
			handWeapon = handWeapon == null ? this.bareHands : handWeapon;
			SwipeStatePlayer.instance().calcValidTargets(this, handWeapon, true, s_targetsProne, s_targetsStanding);
			SwipeStatePlayer.HitInfo hitInfo = s_targetsStanding.isEmpty() ? null : (SwipeStatePlayer.HitInfo)s_targetsStanding.get(0);
			SwipeStatePlayer.HitInfo hitInfo2 = s_targetsProne.isEmpty() ? null : (SwipeStatePlayer.HitInfo)s_targetsProne.get(0);
			if (SwipeStatePlayer.instance().isProneTargetBetter(this, hitInfo, hitInfo2)) {
				hitInfo = null;
			}

			boolean boolean1 = this.getVariableBoolean("AttackAnim") || this.getVariableBoolean("ShoveAnim") || this.getVariableBoolean("StompAnim");
			if (!boolean1) {
				this.setAimAtFloor(false);
			}

			if (hitInfo != null) {
				if (!boolean1) {
					this.setAimAtFloor(false);
				}
			} else if (hitInfo2 != null && !boolean1) {
				this.setAimAtFloor(true);
			}

			if (hitInfo != null) {
				boolean boolean2 = !this.getVariableBoolean("AttackAnim") && handWeapon.getSwingAnim() != null && handWeapon.CloseKillMove != null && hitInfo.distSq < handWeapon.getMinRange() * handWeapon.getMinRange();
				if (boolean2 && (this.getSecondaryHandItem() == null || this.getSecondaryHandItem().getItemReplacementSecondHand() == null)) {
					this.setVariable("LeftHandMask", "RaiseHand");
				}
			}

			SwipeStatePlayer.instance().hitInfoPool.release((List)s_targetsStanding);
			SwipeStatePlayer.instance().hitInfoPool.release((List)s_targetsProne);
			s_targetsStanding.clear();
			s_targetsProne.clear();
		}
	}

	protected void calculateStats() {
		if (!this.bRemote) {
			super.calculateStats();
		}
	}

	protected void updateStats_Sleeping() {
		float float1 = 2.0F;
		if (allPlayersAsleep()) {
			float1 *= GameTime.instance.getDeltaMinutesPerDay();
		}

		Stats stats = this.stats;
		stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)GameTime.instance.getMultiplier() * (double)float1);
		if (this.stats.endurance > 1.0F) {
			this.stats.endurance = 1.0F;
		}

		float float2;
		float float3;
		if (this.stats.fatigue > 0.0F) {
			float2 = 1.0F;
			if (this.Traits.Insomniac.isSet()) {
				float2 *= 0.5F;
			}

			if (this.Traits.NightOwl.isSet()) {
				float2 *= 1.4F;
			}

			float3 = 1.0F;
			if ("goodBed".equals(this.getBedType())) {
				float3 = 1.1F;
			}

			if ("badBed".equals(this.getBedType())) {
				float3 = 0.9F;
			}

			if ("floor".equals(this.getBedType())) {
				float3 = 0.6F;
			}

			float float4 = 1.0F / GameTime.instance.getMinutesPerDay() / 60.0F * GameTime.instance.getMultiplier() / 2.0F;
			this.timeOfSleep += float4;
			if (this.timeOfSleep > this.delayToActuallySleep) {
				float float5 = 1.0F;
				if (this.Traits.NeedsLessSleep.isSet()) {
					float5 *= 0.75F;
				} else if (this.Traits.NeedsMoreSleep.isSet()) {
					float5 *= 1.18F;
				}

				float float6 = 1.0F;
				if (this.stats.fatigue <= 0.3F) {
					float6 = 7.0F * float5;
					stats = this.stats;
					stats.fatigue -= float4 / float6 * 0.3F * float2 * float3;
				} else {
					float6 = 5.0F * float5;
					stats = this.stats;
					stats.fatigue -= float4 / float6 * 0.7F * float2 * float3;
				}
			}

			if (this.stats.fatigue < 0.0F) {
				this.stats.fatigue = 0.0F;
			}
		}

		if (this.Moodles.getMoodleLevel(MoodleType.FoodEaten) == 0) {
			float2 = this.getAppetiteMultiplier();
			stats = this.stats;
			stats.hunger = (float)((double)stats.hunger + ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)float2 * (double)GameTime.instance.getMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay() * this.getHungerMultiplier());
		} else {
			stats = this.stats;
			stats.hunger += (float)(ZomboidGlobals.HungerIncreaseWhenWellFed * SandboxOptions.instance.getStatsDecreaseMultiplier() * ZomboidGlobals.HungerIncreaseWhileAsleep * SandboxOptions.instance.getStatsDecreaseMultiplier() * (double)GameTime.instance.getMultiplier() * this.getHungerMultiplier() * (double)GameTime.instance.getDeltaMinutesPerDay());
		}

		if (this.ForceWakeUpTime == 0.0F) {
			this.ForceWakeUpTime = 9.0F;
		}

		float2 = GameTime.getInstance().getTimeOfDay();
		float3 = GameTime.getInstance().getLastTimeOfDay();
		if (float3 > float2) {
			if (float3 < this.ForceWakeUpTime) {
				float2 += 24.0F;
			} else {
				float3 -= 24.0F;
			}
		}

		boolean boolean1 = float2 >= this.ForceWakeUpTime && float3 < this.ForceWakeUpTime;
		if (this.getAsleepTime() > 16.0F) {
			boolean1 = true;
		}

		if (GameClient.bClient || numPlayers > 1) {
			boolean1 = boolean1 || this.pressedAim() || this.pressedMovement(false);
		}

		if (this.ForceWakeUp) {
			boolean1 = true;
		}

		if (this.Asleep && boolean1) {
			this.ForceWakeUp = false;
			SoundManager.instance.setMusicWakeState(this, "WakeNormal");
			SleepingEvent.instance.wakeUp(this);
			this.ForceWakeUpTime = -1.0F;
			if (GameClient.bClient) {
				GameClient.instance.sendPlayer(this);
			}

			this.dirtyRecalcGridStackTime = 20.0F;
		}
	}

	private void updateEndurance(float float1) {
		Stats stats;
		float float2;
		if (this.isSitOnGround()) {
			float2 = (float)ZomboidGlobals.SittingEnduranceMultiplier;
			float2 *= 1.0F - this.stats.fatigue;
			float2 *= GameTime.instance.getMultiplier();
			stats = this.stats;
			stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float2);
			this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
		} else {
			float2 = 1.0F;
			if (this.isSneaking()) {
				float2 = 1.5F;
			}

			float float3;
			float float4;
			if (!(this.CurrentSpeed > 0.0F) || !this.isRunning() && !this.isSprinting()) {
				if (this.CurrentSpeed > 0.0F && this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 2) {
					float4 = 0.7F;
					if (this.Traits.Asthmatic.isSet()) {
						float4 = 1.4F;
					}

					float float5 = 1.4F;
					if (this.Traits.Overweight.isSet()) {
						float5 = 2.9F;
					}

					if (this.Traits.Athletic.isSet()) {
						float5 = 0.8F;
					}

					float5 *= 3.0F;
					float5 *= this.getPacingMod();
					float5 *= this.getHyperthermiaMod();
					float3 = 2.8F;
					switch (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
					case 2: 
						float3 = 1.5F;
						break;
					
					case 3: 
						float3 = 1.9F;
						break;
					
					case 4: 
						float3 = 2.3F;
					
					}

					stats = this.stats;
					stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)float5 * 0.5 * (double)float4 * (double)float2 * (double)GameTime.instance.getMultiplier() * (double)float3 / 2.0);
				}
			} else {
				double double1 = ZomboidGlobals.RunningEnduranceReduce;
				if (this.isSprinting()) {
					double1 = ZomboidGlobals.SprintingEnduranceReduce;
				}

				float3 = 1.4F;
				if (this.Traits.Overweight.isSet()) {
					float3 = 2.9F;
				}

				if (this.Traits.Athletic.isSet()) {
					float3 = 0.8F;
				}

				float3 *= 2.3F;
				float3 *= this.getPacingMod();
				float3 *= this.getHyperthermiaMod();
				float float6 = 0.7F;
				if (this.Traits.Asthmatic.isSet()) {
					float6 = 1.4F;
				}

				if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
					stats = this.stats;
					stats.endurance = (float)((double)stats.endurance - double1 * (double)float3 * 0.5 * (double)float6 * (double)GameTime.instance.getMultiplier() * (double)float2);
				} else {
					float float7 = 2.8F;
					switch (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
					case 1: 
						float7 = 1.5F;
						break;
					
					case 2: 
						float7 = 1.9F;
						break;
					
					case 3: 
						float7 = 2.3F;
					
					}

					stats = this.stats;
					stats.endurance = (float)((double)stats.endurance - double1 * (double)float3 * 0.5 * (double)float6 * (double)GameTime.instance.getMultiplier() * (double)float7 * (double)float2);
				}
			}

			switch (this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
			case 1: 
				float1 *= 0.95F;
				break;
			
			case 2: 
				float1 *= 0.9F;
				break;
			
			case 3: 
				float1 *= 0.8F;
				break;
			
			case 4: 
				float1 *= 0.6F;
			
			}

			if (this.stats.enduranceRecharging) {
				float1 *= 0.85F;
			}

			if (!this.isPlayerMoving()) {
				float4 = 1.0F;
				float4 *= 1.0F - this.stats.fatigue;
				float4 *= GameTime.instance.getMultiplier();
				if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) <= 1) {
					stats = this.stats;
					stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float4);
				}
			}

			if (!this.isSprinting() && !this.isRunning() && this.CurrentSpeed > 0.0F) {
				float4 = 1.0F;
				float4 *= 1.0F - this.stats.fatigue;
				float4 *= GameTime.instance.getMultiplier();
				if (this.getMoodles().getMoodleLevel(MoodleType.Endurance) < 2) {
					if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) <= 1) {
						stats = this.stats;
						stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce / 4.0 * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float4);
					}
				} else {
					stats = this.stats;
					stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce / 7.0 * (double)float2);
				}
			}
		}
	}

	private boolean checkActionsBlockingMovement() {
		if (this.CharacterActions.isEmpty()) {
			return false;
		} else {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			return baseAction.blockMovementEtc;
		}
	}

	private void updateInteractKeyPanic() {
		if (this.PlayerIndex == 0) {
			if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("Interact"))) {
				this.ContextPanic += 0.6F;
			}
		}
	}

	private void updateSneakKey() {
		if (this.PlayerIndex != 0) {
			this.bSneakDebounce = false;
		} else {
			if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey("Crouch"))) {
				if (!this.bSneakDebounce) {
					this.setSneaking(!this.isSneaking());
					this.bSneakDebounce = true;
				}
			} else {
				this.bSneakDebounce = false;
			}
		}
	}

	private void updateChangeCharacterKey() {
		if (Core.bDebug) {
			if (this.PlayerIndex == 0 && Core.bDebug && GameKeyboard.isKeyDown(22)) {
				if (!this.bChangeCharacterDebounce) {
					this.FollowCamStack.clear();
					this.bChangeCharacterDebounce = true;
					for (int int1 = 0; int1 < this.getCell().getObjectList().size(); ++int1) {
						IsoMovingObject movingObject = (IsoMovingObject)this.getCell().getObjectList().get(int1);
						if (movingObject instanceof IsoSurvivor) {
							this.FollowCamStack.add((IsoSurvivor)movingObject);
						}
					}

					if (!this.FollowCamStack.isEmpty()) {
						if (this.followID >= this.FollowCamStack.size()) {
							this.followID = 0;
						}

						IsoCamera.SetCharacterToFollow((IsoGameCharacter)this.FollowCamStack.get(this.followID));
						++this.followID;
					}
				}
			} else {
				this.bChangeCharacterDebounce = false;
			}
		}
	}

	private void updateEnableModelsKey() {
		if (Core.bDebug) {
			if (this.PlayerIndex == 0 && GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleModelsEnabled"))) {
				ModelManager.instance.bDebugEnableModels = !ModelManager.instance.bDebugEnableModels;
			}
		}
	}

	private void updateDeathDragDown() {
		if (!this.isDead()) {
			if (this.isDeathDragDown()) {
				if (this.isGodMod()) {
					this.setDeathDragDown(false);
				} else if (!"EndDeath".equals(this.getHitReaction())) {
					for (int int1 = -1; int1 <= 1; ++int1) {
						for (int int2 = -1; int2 <= 1; ++int2) {
							IsoGridSquare square = this.getCell().getGridSquare((int)this.x + int2, (int)this.y + int1, (int)this.z);
							if (square != null) {
								for (int int3 = 0; int3 < square.getMovingObjects().size(); ++int3) {
									IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int3);
									IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
									if (zombie != null && zombie.isAlive() && !zombie.isOnFloor()) {
										this.setAttackingZombie(zombie);
										this.setHitReaction("EndDeath");
										this.setBlockMovement(true);
										return;
									}
								}
							}
						}
					}

					this.setDeathDragDown(false);
				}
			}
		}
	}

	private void updateGodModeKey() {
		if (Core.bDebug) {
			if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleGodModeInvisible"))) {
				IsoPlayer player = null;
				for (int int1 = 0; int1 < numPlayers; ++int1) {
					if (players[int1] != null && !players[int1].isDead()) {
						player = players[int1];
						break;
					}
				}

				if (this == player) {
					boolean boolean1 = !player.isGodMod();
					DebugLog.General.println("Toggle GodMode: %s", boolean1 ? "ON" : "OFF");
					player.setInvisible(boolean1);
					player.setGhostMode(boolean1);
					player.setGodMod(boolean1);
					for (int int2 = 0; int2 < numPlayers; ++int2) {
						if (players[int2] != null && players[int2] != player) {
							players[int2].setInvisible(boolean1);
							players[int2].setGhostMode(boolean1);
							players[int2].setGodMod(boolean1);
						}
					}

					if (GameClient.bClient) {
						GameClient.sendPlayerExtraInfo(player);
						if (DebugOptions.instance.MultiplayerShowTeleport.getValue() && !boolean1) {
							Iterator iterator = GameClient.IDToZombieMap.valueCollection().iterator();
							while (iterator.hasNext()) {
								IsoZombie zombie = (IsoZombie)iterator.next();
								zombie.teleportDebug = null;
								zombie.debugData.clear();
							}
						}
					}
				}
			}
		}
	}

	private void checkReloading() {
		HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
		if (handWeapon != null && handWeapon.isReloadable(this)) {
			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3;
			if (this.JoypadBind != -1 && this.bJoypadMovementActive) {
				boolean3 = JoypadManager.instance.isRBPressed(this.JoypadBind);
				if (boolean3) {
					boolean1 = !this.bReloadButtonDown;
				}

				this.bReloadButtonDown = boolean3;
				boolean3 = JoypadManager.instance.isLBPressed(this.JoypadBind);
				if (boolean3) {
					boolean2 = !this.bRackButtonDown;
				}

				this.bRackButtonDown = boolean3;
			}

			if (this.PlayerIndex == 0) {
				boolean3 = GameKeyboard.isKeyDown(Core.getInstance().getKey("ReloadWeapon"));
				if (boolean3) {
					boolean1 = !this.bReloadKeyDown;
				}

				this.bReloadKeyDown = boolean3;
				boolean3 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Rack Firearm"));
				if (boolean3) {
					boolean2 = !this.bRackKeyDown;
				}

				this.bRackKeyDown = boolean3;
			}

			if (boolean1) {
				this.setVariable("WeaponReloadType", handWeapon.getWeaponReloadType());
				LuaEventManager.triggerEvent("OnPressReloadButton", this, handWeapon);
			} else if (boolean2) {
				this.setVariable("WeaponReloadType", handWeapon.getWeaponReloadType());
				LuaEventManager.triggerEvent("OnPressRackButton", this, handWeapon);
			}
		}
	}

	public void postupdate() {
		IsoPlayer.s_performance.postUpdate.invokeAndMeasure(this, IsoPlayer::postupdateInternal);
	}

	private void postupdateInternal() {
		super.postupdate();
		this.highlightRangedTargets();
		if (this.isNPC) {
			GameTime gameTime = GameTime.getInstance();
			float float1 = 1.0F / gameTime.getMinutesPerDay() / 60.0F * gameTime.getMultiplier() / 2.0F;
			if (Core.bLastStand) {
				float1 = 1.0F / gameTime.getMinutesPerDay() / 60.0F * gameTime.getUnmoddedMultiplier() / 2.0F;
			}

			this.setHoursSurvived(this.getHoursSurvived() + (double)float1);
		}
	}

	private void highlightRangedTargets() {
		if (this.isLocalPlayer() && !this.isNPC) {
			if (this.isAiming()) {
				if (Core.getInstance().getOptionAimOutline() != 1) {
					IsoPlayer.s_performance.highlightRangedTargets.invokeAndMeasure(this, IsoPlayer::highlightRangedTargetsInternal);
				}
			}
		}
	}

	private void highlightRangedTargetsInternal() {
		HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(this.getPrimaryHandItem(), HandWeapon.class);
		if (handWeapon == null || handWeapon.getSwingAnim() == null || handWeapon.getCondition() <= 0) {
			handWeapon = this.bareHands;
		}

		if (Core.getInstance().getOptionAimOutline() != 2 || handWeapon.isRanged()) {
			boolean boolean1 = this.bDoShove;
			this.bDoShove = false;
			SwipeStatePlayer.AttackVars attackVars = SwipeStatePlayer.instance().attackVars;
			SwipeStatePlayer.instance().CalcAttackVars(this, attackVars);
			SwipeStatePlayer.instance().CalcHitList(this, false, attackVars);
			for (int int1 = 0; int1 < SwipeStatePlayer.HitList.size(); ++int1) {
				SwipeStatePlayer.HitInfo hitInfo = (SwipeStatePlayer.HitInfo)SwipeStatePlayer.HitList.get(int1);
				IsoMovingObject movingObject = hitInfo.object;
				if (movingObject instanceof IsoZombie || movingObject instanceof IsoPlayer) {
					float float1 = 1.0F - (float)hitInfo.chance / 100.0F;
					float float2 = (float)hitInfo.chance / 100.0F;
					float float3 = 0.4F;
					if ((double)float2 < 0.7) {
						float3 = 0.36F;
					}

					movingObject.bOutline[this.PlayerIndex] = true;
					if (movingObject.outlineColor[this.PlayerIndex] == null) {
						movingObject.outlineColor[this.PlayerIndex] = new ColorInfo();
					}

					movingObject.outlineColor[this.PlayerIndex].set(float1 * 0.75F, float2 * float3, 0.0F, 1.0F);
				}

				if (hitInfo.window != null) {
					hitInfo.window.setHighlightColor(0.8F, 0.1F, 0.1F, 0.5F);
					hitInfo.window.setHighlighted(true);
				}
			}

			this.bDoShove = boolean1;
		}
	}

	public boolean isSolidForSeparate() {
		return this.isGhostMode() ? false : super.isSolidForSeparate();
	}

	public boolean isPushableForSeparate() {
		if (this.isCurrentState(PlayerHitReactionState.instance())) {
			return false;
		} else {
			return this.isCurrentState(SwipeStatePlayer.instance()) ? false : super.isPushableForSeparate();
		}
	}

	public boolean isPushedByForSeparate(IsoMovingObject movingObject) {
		if (!this.isPlayerMoving() && movingObject.isZombie() && ((IsoZombie)movingObject).isAttacking()) {
			return false;
		} else {
			return !GameClient.bClient || this.isLocalPlayer() && this.JustMoved ? super.isPushedByForSeparate(movingObject) : false;
		}
	}

	private void updateExt() {
		if (!this.isSneaking()) {
			this.extUpdateCount += GameTime.getInstance().getMultiplier() / 0.8F;
			if (!this.getAdvancedAnimator().containsAnyIdleNodes() && !this.isSitOnGround()) {
				this.extUpdateCount = 0.0F;
			}

			if (!(this.extUpdateCount <= 5000.0F)) {
				this.extUpdateCount = 0.0F;
				if (this.stats.NumVisibleZombies == 0 && this.stats.NumChasingZombies == 0) {
					if (Rand.NextBool(3)) {
						if (this.getAdvancedAnimator().containsAnyIdleNodes() || this.isSitOnGround()) {
							this.onIdlePerformFidgets();
							this.reportEvent("EventDoExt");
						}
					}
				}
			}
		}
	}

	private void onIdlePerformFidgets() {
		Moodles moodles = this.getMoodles();
		BodyDamage bodyDamage = this.getBodyDamage();
		if (moodles.getMoodleLevel(MoodleType.Hypothermia) > 0 && Rand.NextBool(7)) {
			this.setVariable("Ext", "Shiver");
		} else if (moodles.getMoodleLevel(MoodleType.Hyperthermia) > 0 && Rand.NextBool(7)) {
			this.setVariable("Ext", "WipeBrow");
		} else {
			int int1;
			if (moodles.getMoodleLevel(MoodleType.Sick) > 0 && Rand.NextBool(7)) {
				if (Rand.NextBool(4)) {
					this.setVariable("Ext", "Cough");
				} else {
					int1 = Rand.Next(2);
					this.setVariable("Ext", "PainStomach" + (int1 + 1));
				}
			} else if (moodles.getMoodleLevel(MoodleType.Endurance) > 2 && Rand.NextBool(10)) {
				if (Rand.NextBool(5) && !this.isSitOnGround()) {
					this.setVariable("Ext", "BentDouble");
				} else {
					this.setVariable("Ext", "WipeBrow");
				}
			} else if (moodles.getMoodleLevel(MoodleType.Tired) > 2 && Rand.NextBool(10)) {
				if (Rand.NextBool(7)) {
					this.setVariable("Ext", "TiredStretch");
				} else if (Rand.NextBool(7)) {
					this.setVariable("Ext", "Sway");
				} else {
					this.setVariable("Ext", "Yawn");
				}
			} else if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.Head, BodyPartType.Neck) && Rand.NextBool(7)) {
				if (bodyDamage.areBodyPartsBleeding(BodyPartType.Head, BodyPartType.Neck) && Rand.NextBool(2)) {
					this.setVariable("Ext", "WipeHead");
				} else {
					int1 = Rand.Next(2);
					this.setVariable("Ext", "PainHead" + (int1 + 1));
				}
			} else if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperArm_L, BodyPartType.ForeArm_L) && Rand.NextBool(7)) {
				if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperArm_L, BodyPartType.ForeArm_L) && Rand.NextBool(2)) {
					this.setVariable("Ext", "WipeArmL");
				} else {
					this.setVariable("Ext", "PainArmL");
				}
			} else if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperArm_R, BodyPartType.ForeArm_R) && Rand.NextBool(7)) {
				if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperArm_R, BodyPartType.ForeArm_R) && Rand.NextBool(2)) {
					this.setVariable("Ext", "WipeArmR");
				} else {
					this.setVariable("Ext", "PainArmR");
				}
			} else if (bodyDamage.doesBodyPartHaveInjury(BodyPartType.Hand_L) && Rand.NextBool(7)) {
				this.setVariable("Ext", "PainHandL");
			} else if (bodyDamage.doesBodyPartHaveInjury(BodyPartType.Hand_R) && Rand.NextBool(7)) {
				this.setVariable("Ext", "PainHandR");
			} else if (!this.isSitOnGround() && bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperLeg_L, BodyPartType.LowerLeg_L) && Rand.NextBool(7)) {
				if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperLeg_L, BodyPartType.LowerLeg_L) && Rand.NextBool(2)) {
					this.setVariable("Ext", "WipeLegL");
				} else {
					this.setVariable("Ext", "PainLegL");
				}
			} else if (!this.isSitOnGround() && bodyDamage.doBodyPartsHaveInjuries(BodyPartType.UpperLeg_R, BodyPartType.LowerLeg_R) && Rand.NextBool(7)) {
				if (bodyDamage.areBodyPartsBleeding(BodyPartType.UpperLeg_R, BodyPartType.LowerLeg_R) && Rand.NextBool(2)) {
					this.setVariable("Ext", "WipeLegR");
				} else {
					this.setVariable("Ext", "PainLegR");
				}
			} else if (bodyDamage.doBodyPartsHaveInjuries(BodyPartType.Torso_Upper, BodyPartType.Torso_Lower) && Rand.NextBool(7)) {
				if (bodyDamage.areBodyPartsBleeding(BodyPartType.Torso_Upper, BodyPartType.Torso_Lower) && Rand.NextBool(2)) {
					int1 = Rand.Next(2);
					this.setVariable("Ext", "WipeTorso" + (int1 + 1));
				} else {
					this.setVariable("Ext", "PainTorso");
				}
			} else if (WeaponType.getWeaponType((IsoGameCharacter)this) != WeaponType.barehand) {
				int1 = Rand.Next(5);
				this.setVariable("Ext", (int1 + 1).makeConcatWithConstants(int1 + 1));
			} else if (Rand.NextBool(10)) {
				this.setVariable("Ext", "ChewNails");
			} else if (Rand.NextBool(10)) {
				this.setVariable("Ext", "ShiftWeight");
			} else if (Rand.NextBool(10)) {
				this.setVariable("Ext", "PullAtColar");
			} else if (Rand.NextBool(10)) {
				this.setVariable("Ext", "BridgeNose");
			} else {
				int1 = Rand.Next(5);
				this.setVariable("Ext", (int1 + 1).makeConcatWithConstants(int1 + 1));
			}
		}
	}

	private boolean updateUseKey() {
		if (GameServer.bServer) {
			return false;
		} else if (!this.isLocalPlayer()) {
			return false;
		} else if (this.PlayerIndex != 0) {
			return false;
		} else {
			this.timePressedContext += GameTime.instance.getRealworldSecondsSinceLastUpdate();
			boolean boolean1 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact"));
			if (boolean1 && this.timePressedContext < 0.5F) {
				this.bPressContext = true;
			} else {
				if (this.bPressContext && (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry || !GameKeyboard.doLuaKeyPressed)) {
					this.bPressContext = false;
				}

				if (this.bPressContext && this.doContext(this.dir, false)) {
					this.timePressedContext = 0.0F;
					this.bPressContext = false;
					return true;
				}

				if (!boolean1) {
					this.bPressContext = false;
					this.timePressedContext = 0.0F;
				}
			}

			return false;
		}
	}

	private void updateHitByVehicle() {
		if (!GameServer.bServer) {
			if (this.isLocalPlayer()) {
				if (this.vehicle4testCollision != null && this.ULbeatenVehicle.Check() && SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue() > 1) {
					BaseVehicle baseVehicle = this.vehicle4testCollision;
					this.vehicle4testCollision = null;
					if (baseVehicle.isEngineRunning() && this.getVehicle() != baseVehicle) {
						float float1 = baseVehicle.jniLinearVelocity.x;
						float float2 = baseVehicle.jniLinearVelocity.z;
						if (GameClient.bClient && this.isLocalPlayer()) {
							float1 = baseVehicle.netLinearVelocity.x;
							float2 = baseVehicle.netLinearVelocity.z;
						}

						float float3 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
						Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
						Vector2 vector22 = baseVehicle.testCollisionWithCharacter(this, 0.20000002F, vector2);
						if (vector22 != null && vector22.x != -1.0F) {
							vector22.x = (vector22.x - baseVehicle.x) * float3 * 1.0F + this.x;
							vector22.y = (vector22.y - baseVehicle.y) * float3 * 1.0F + this.x;
							if (this.isOnFloor()) {
								int int1 = baseVehicle.testCollisionWithProneCharacter(this, false);
								if (int1 > 0) {
									this.doBeatenVehicle(Math.max(float3 * 6.0F, 5.0F), vector22.x, vector22.y, false);
								}

								this.doBeatenVehicle(0.0F, vector22.x, vector22.y, false);
							} else if (this.getCurrentState() != PlayerFallDownState.instance() && float3 > 0.1F) {
								this.doBeatenVehicle(Math.max(float3 * 2.0F, 5.0F), vector22.x, vector22.y, false);
							}
						}

						((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
					}
				}
			}
		}
	}

	private void updateSoundListener() {
		if (!GameServer.bServer) {
			if (this.isLocalPlayer()) {
				if (this.soundListener == null) {
					this.soundListener = (BaseSoundListener)(Core.SoundDisabled ? new DummySoundListener(this.PlayerIndex) : new SoundListener(this.PlayerIndex));
				}

				this.soundListener.setPos(this.x, this.y, this.z);
				this.checkNearbyRooms -= GameTime.getInstance().getMultiplier() / 1.6F;
				if (this.checkNearbyRooms <= 0.0F) {
					this.checkNearbyRooms = 30.0F;
					this.numNearbyBuildingsRooms = (float)IsoWorld.instance.MetaGrid.countNearbyBuildingsRooms(this);
				}

				if (this.testemitter == null) {
					this.testemitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
					this.testemitter.setPos(this.x, this.y, this.z);
				}

				this.soundListener.tick();
				this.testemitter.tick();
			}
		}
	}

	public void updateMovementRates() {
		this.calculateWalkSpeed();
		this.m_idleSpeed = this.calculateIdleSpeed();
		this.updateFootInjuries();
	}

	public void pressedAttack() {
		this.pressedAttack((String)null);
	}

	public void pressedAttack(String string) {
		boolean boolean1 = this.isSprinting();
		this.setSprinting(false);
		this.setForceSprint(false);
		if (!this.attackStarted && !this.isCurrentState(PlayerHitReactionState.instance())) {
			if (!GameClient.bClient || !this.isCurrentState(PlayerHitReactionPVPState.instance()) || ServerOptions.instance.PVPMeleeWhileHitReaction.getValue()) {
				if (this.primaryHandModel != null && !StringUtils.isNullOrEmpty(this.primaryHandModel.maskVariableValue) && this.secondaryHandModel != null && !StringUtils.isNullOrEmpty(this.secondaryHandModel.maskVariableValue)) {
					this.bDoShove = false;
					this.setForceShove(false);
					this.initiateAttack = false;
					this.attackStarted = false;
					this.setAttackType((String)null);
				} else if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem().getItemReplacementPrimaryHand() != null && this.getSecondaryHandItem() != null && this.getSecondaryHandItem().getItemReplacementSecondHand() != null) {
					this.bDoShove = false;
					this.setForceShove(false);
					this.initiateAttack = false;
					this.attackStarted = false;
					this.setAttackType((String)null);
				} else {
					if (!this.attackStarted) {
						this.setVariable("StartedAttackWhileSprinting", boolean1);
					}

					this.initiateAttack = true;
					this.attackStarted = true;
					this.isCrit = false;
					this.attackFromBehind = false;
					WeaponType weaponType = WeaponType.getWeaponType((IsoGameCharacter)this);
					this.setAttackType((String)PZArrayUtil.pickRandom(weaponType.possibleAttack));
					this.combatSpeed = this.calculateCombatSpeed();
					SwipeStatePlayer.AttackVars attackVars = SwipeStatePlayer.instance().attackVars;
					SwipeStatePlayer.instance().CalcAttackVars(this, attackVars);
					if (attackVars.bDoShove && !this.isAuthorizeShoveStomp()) {
						this.bDoShove = false;
						this.setForceShove(false);
						this.initiateAttack = false;
						this.attackStarted = false;
						this.setAttackType((String)null);
					} else {
						this.useHandWeapon = attackVars.weapon;
						this.setAimAtFloor(attackVars.bAimAtFloor);
						this.bDoShove = attackVars.bDoShove;
						this.targetOnGround = attackVars.targetOnGround;
						if (attackVars.weapon != null && !StringUtils.isNullOrEmpty(attackVars.weapon.getFireMode())) {
							this.setVariable("FireMode", attackVars.weapon.getFireMode());
						} else {
							this.clearVariable("FireMode");
						}

						int int1;
						if (this.useHandWeapon != null && weaponType.isRanged && !this.bDoShove) {
							int1 = this.useHandWeapon.getRecoilDelay();
							Float Float1 = (float)int1 * (1.0F - (float)this.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0F);
							this.setRecoilDelay((float)Float1.intValue());
						}

						int1 = Rand.Next(0, 3);
						if (int1 == 0) {
							this.setVariable("AttackVariationX", Rand.Next(-1.0F, -0.5F));
						}

						if (int1 == 1) {
							this.setVariable("AttackVariationX", 0.0F);
						}

						if (int1 == 2) {
							this.setVariable("AttackVariationX", Rand.Next(0.5F, 1.0F));
						}

						this.setVariable("AttackVariationY", 0.0F);
						ArrayList arrayList = SwipeStatePlayer.HitList;
						SwipeStatePlayer.instance().CalcHitList(this, true, attackVars);
						if (StringUtils.isNullOrEmpty(string)) {
							IsoGameCharacter gameCharacter = null;
							if (!arrayList.isEmpty()) {
								gameCharacter = (IsoGameCharacter)Type.tryCastTo(((SwipeStatePlayer.HitInfo)arrayList.get(0)).object, IsoGameCharacter.class);
							}

							if (gameCharacter == null) {
								if (this.isAiming() && !this.m_meleePressed && this.useHandWeapon != this.bareHands) {
									this.bDoShove = false;
									this.setForceShove(false);
								}

								this.m_lastAttackWasShove = this.bDoShove;
								if (weaponType.canMiss && !this.isAimAtFloor()) {
									this.setAttackType("miss");
								}

								if (this.isAiming() && this.bDoShove) {
									this.setVariable("bShoveAiming", true);
								} else {
									this.clearVariable("bShoveAiming");
								}

								return;
							}

							this.attackFromBehind = this.isBehind(gameCharacter);
							float float1 = IsoUtils.DistanceTo(gameCharacter.x, gameCharacter.y, this.x, this.y);
							this.setVariable("TargetDist", float1);
							int int2 = this.calculateCritChance(gameCharacter);
							if (gameCharacter instanceof IsoZombie) {
								IsoZombie zombie = this.getClosestZombieToOtherZombie((IsoZombie)gameCharacter);
								if (!attackVars.bAimAtFloor && (double)float1 > 1.25 && weaponType == WeaponType.spear && (zombie == null || (double)IsoUtils.DistanceTo(gameCharacter.x, gameCharacter.y, zombie.x, zombie.y) > 1.7)) {
									this.setAttackType("overhead");
									int2 += 30;
								}
							}

							gameCharacter.setHitFromBehind(this.attackFromBehind);
							if (this.attackFromBehind) {
								if (gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).target == null) {
									int2 += 30;
								} else {
									int2 += 5;
								}
							}

							if (gameCharacter instanceof IsoPlayer && weaponType.isRanged && !this.bDoShove) {
								int2 = (int)(attackVars.weapon.getStopPower() * (1.0F + (float)this.getPerkLevel(PerkFactory.Perks.Aiming) / 15.0F));
							}

							this.isCrit = Rand.Next(100) < int2;
							if (this.attackFromBehind && attackVars.bCloseKill && gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).target == null) {
								this.isCrit = true;
							}

							if (this.isCrit && !attackVars.bCloseKill && !this.bDoShove && weaponType == WeaponType.knife) {
								this.isCrit = false;
							}

							this.setAttackWasSuperAttack(false);
							if (this.stats.NumChasingZombies > 1 && attackVars.bCloseKill && !this.bDoShove && weaponType == WeaponType.knife) {
								this.isCrit = false;
							}

							if (this.isCrit) {
								this.combatSpeed *= 1.1F;
							}

							if (DebugLog.isEnabled(DebugType.Combat)) {
								DebugLog.Combat.debugln("Hit zombie dist: " + float1 + " crit: " + this.isCrit + " (" + int2 + "%) from behind: " + this.attackFromBehind);
							}
						}

						if (!StringUtils.isNullOrEmpty(string)) {
							this.setAttackType(string);
						}

						if (this.isAiming() && this.bDoShove) {
							this.setVariable("bShoveAiming", true);
						} else {
							this.clearVariable("bShoveAiming");
						}

						if (this.useHandWeapon != null && weaponType.isRanged) {
							this.setRecoilDelay((float)(this.useHandWeapon.getRecoilDelay() - this.getPerkLevel(PerkFactory.Perks.Aiming) * 2));
						}

						this.m_lastAttackWasShove = this.bDoShove;
					}
				}
			}
		}
	}

	public int calculateCritChance(IsoGameCharacter gameCharacter) {
		if (this.bDoShove) {
			int int1 = 35;
			if (gameCharacter instanceof IsoPlayer) {
				IsoPlayer player = (IsoPlayer)gameCharacter;
				int1 = 20;
				if (GameClient.bClient && !player.isLocalPlayer()) {
					int1 = (int)((double)int1 - (double)player.remoteStrLvl * 1.5);
					if (player.getNutrition().getWeight() < 80.0F) {
						int1 = (int)((float)int1 + Math.abs((player.getNutrition().getWeight() - 80.0F) / 2.0F));
					} else {
						int1 = (int)((float)int1 - (player.getNutrition().getWeight() - 80.0F) / 2.0F);
					}
				}
			}

			int1 -= this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5;
			int1 -= this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5;
			int1 = (int)((double)int1 - (double)this.getMoodles().getMoodleLevel(MoodleType.Panic) * 1.3);
			int1 += this.getPerkLevel(PerkFactory.Perks.Strength) * 2;
			return int1;
		} else if (this.bDoShove && gameCharacter.getStateMachine().getCurrent() == StaggerBackState.instance() && gameCharacter instanceof IsoZombie) {
			return 100;
		} else if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
			HandWeapon handWeapon = (HandWeapon)this.getPrimaryHandItem();
			int int2 = (int)handWeapon.getCriticalChance();
			if (handWeapon.isAlwaysKnockdown()) {
				return 100;
			} else {
				WeaponType weaponType = WeaponType.getWeaponType((IsoGameCharacter)this);
				if (weaponType.isRanged) {
					int2 = (int)((float)int2 + (float)handWeapon.getAimingPerkCritModifier() * ((float)this.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F));
					if (this.getBeenMovingFor() > (float)(handWeapon.getAimingTime() + this.getPerkLevel(PerkFactory.Perks.Aiming) * 2)) {
						int2 = (int)((float)int2 - (this.getBeenMovingFor() - (float)(handWeapon.getAimingTime() + this.getPerkLevel(PerkFactory.Perks.Aiming) * 2)));
					}

					int2 += this.getPerkLevel(PerkFactory.Perks.Aiming) * 3;
					if (this.DistTo(gameCharacter) < 4.0F) {
						int2 = (int)((float)int2 + (3.0F - this.DistTo(gameCharacter)) * 7.0F);
					} else if (this.DistTo(gameCharacter) >= 4.0F) {
						int2 = (int)((float)int2 - (4.0F - this.DistTo(gameCharacter)) * 7.0F);
					}
				} else {
					if (handWeapon.isTwoHandWeapon() && (this.getPrimaryHandItem() != handWeapon || this.getSecondaryHandItem() != handWeapon)) {
						int2 -= int2 / 3;
					}

					if (this.chargeTime < 2.0F) {
						int2 -= int2 / 5;
					}

					int int3 = this.getPerkLevel(PerkFactory.Perks.Blunt);
					if (handWeapon.getCategories().contains("Axe")) {
						int3 = this.getPerkLevel(PerkFactory.Perks.Axe);
					}

					if (handWeapon.getCategories().contains("LongBlade")) {
						int3 = this.getPerkLevel(PerkFactory.Perks.LongBlade);
					}

					if (handWeapon.getCategories().contains("Spear")) {
						int3 = this.getPerkLevel(PerkFactory.Perks.Spear);
					}

					if (handWeapon.getCategories().contains("SmallBlade")) {
						int3 = this.getPerkLevel(PerkFactory.Perks.SmallBlade);
					}

					if (handWeapon.getCategories().contains("SmallBlunt")) {
						int3 = this.getPerkLevel(PerkFactory.Perks.SmallBlunt);
					}

					int2 += int3 * 3;
					if (gameCharacter instanceof IsoPlayer) {
						IsoPlayer player2 = (IsoPlayer)gameCharacter;
						if (GameClient.bClient && !player2.isLocalPlayer()) {
							int2 = (int)((double)int2 - (double)player2.remoteStrLvl * 1.5);
							if (player2.getNutrition().getWeight() < 80.0F) {
								int2 = (int)((float)int2 + Math.abs((player2.getNutrition().getWeight() - 80.0F) / 2.0F));
							} else {
								int2 = (int)((float)int2 - (player2.getNutrition().getWeight() - 80.0F) / 2.0F);
							}
						}
					}
				}

				int2 -= this.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5;
				int2 -= this.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 5;
				int2 = (int)((double)int2 - (double)this.getMoodles().getMoodleLevel(MoodleType.Panic) * 1.3);
				if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
					int2 -= 6;
				}

				if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
					int2 += 6;
				}

				if (int2 < 10) {
					int2 = 10;
				}

				if (int2 > 90) {
					int2 = 90;
				}

				return int2;
			}
		} else {
			return 0;
		}
	}

	private void checkJoypadIgnoreAimUntilCentered() {
		if (this.bJoypadIgnoreAimUntilCentered) {
			if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive) {
				float float1 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
				float float2 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
				if (float1 * float1 + float2 + float2 <= 0.0F) {
					this.bJoypadIgnoreAimUntilCentered = false;
				}
			}
		}
	}

	public boolean isAimControlActive() {
		if (this.isForceAim()) {
			return true;
		} else if (this.isAimKeyDown()) {
			return true;
		} else {
			return GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1 && this.bJoypadMovementActive && this.getJoypadAimVector(tempo).getLengthSquared() > 0.0F;
		}
	}

	private Vector2 getJoypadAimVector(Vector2 vector2) {
		if (this.bJoypadIgnoreAimUntilCentered) {
			return vector2.set(0.0F, 0.0F);
		} else {
			float float1 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
			float float2 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
			float float3 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
			if (float2 * float2 + float1 * float1 < float3 * float3) {
				float1 = 0.0F;
				float2 = 0.0F;
			}

			return vector2.set(float2, float1);
		}
	}

	private Vector2 getJoypadMoveVector(Vector2 vector2) {
		float float1 = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
		float float2 = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
		float float3 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
		if (float2 * float2 + float1 * float1 < float3 * float3) {
			float1 = 0.0F;
			float2 = 0.0F;
		}

		vector2.set(float2, float1);
		if (this.isIgnoreInputsForDirection()) {
			vector2.set(0.0F, 0.0F);
		}

		return vector2;
	}

	private void updateToggleToAim() {
		if (this.PlayerIndex == 0) {
			if (!Core.getInstance().isToggleToAim()) {
				this.setForceAim(false);
			} else {
				boolean boolean1 = this.isAimKeyDown();
				long long1 = System.currentTimeMillis();
				if (boolean1) {
					if (this.aimKeyDownMS == 0L) {
						this.aimKeyDownMS = long1;
					}
				} else {
					if (this.aimKeyDownMS != 0L && long1 - this.aimKeyDownMS < 500L) {
						this.toggleForceAim();
					} else if (this.isForceAim()) {
						if (this.aimKeyDownMS != 0L) {
							this.toggleForceAim();
						} else {
							int int1 = Core.getInstance().getKey("Aim");
							boolean boolean2 = int1 == 29 || int1 == 157;
							if (boolean2 && UIManager.isMouseOverInventory()) {
								this.toggleForceAim();
							}
						}
					}

					this.aimKeyDownMS = 0L;
				}
			}
		}
	}

	private void UpdateInputState(IsoPlayer.InputState inputState) {
		inputState.bMelee = false;
		if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
			if (this.bJoypadMovementActive) {
				inputState.isAttacking = this.isCharging;
				if (this.bJoypadIgnoreChargingRT) {
					inputState.isAttacking = false;
				}

				if (this.bJoypadIgnoreAimUntilCentered) {
					float float1 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
					float float2 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
					if (float1 == 0.0F && float2 == 0.0F) {
						this.bJoypadIgnoreAimUntilCentered = false;
					}
				}
			}

			if (this.isChargingLT) {
				inputState.bMelee = true;
				inputState.isAttacking = false;
			}
		} else {
			inputState.isAttacking = this.isCharging && Mouse.isButtonDownUICheck(0);
			if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
				inputState.bMelee = true;
				inputState.isAttacking = false;
			}
		}

		boolean boolean1;
		if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
			if (this.bJoypadMovementActive) {
				inputState.isCharging = JoypadManager.instance.isRTPressed(this.JoypadBind);
				inputState.isChargingLT = JoypadManager.instance.isLTPressed(this.JoypadBind);
				if (this.bJoypadIgnoreChargingRT && !inputState.isCharging) {
					this.bJoypadIgnoreChargingRT = false;
				}
			}

			inputState.isAiming = false;
			inputState.bRunning = false;
			inputState.bSprinting = false;
			Vector2 vector2 = this.getJoypadAimVector(tempVector2);
			if (vector2.x == 0.0F && vector2.y == 0.0F) {
				inputState.isCharging = false;
				Vector2 vector22 = this.getJoypadMoveVector(tempVector2);
				if (vector22.x != 0.0F || vector22.y != 0.0F) {
					if (this.isAllowRun()) {
						inputState.bRunning = JoypadManager.instance.isRTPressed(this.JoypadBind);
					}

					inputState.isAttacking = false;
					inputState.bMelee = false;
					this.bJoypadIgnoreChargingRT = true;
					inputState.isCharging = false;
					boolean1 = JoypadManager.instance.isBPressed(this.JoypadBind);
					if (inputState.bRunning && boolean1 && !this.bJoypadBDown) {
						this.bJoypadSprint = !this.bJoypadSprint;
					}

					this.bJoypadBDown = boolean1;
					inputState.bSprinting = this.bJoypadSprint;
				}
			} else {
				inputState.isAiming = true;
			}

			if (!inputState.bRunning) {
				this.bJoypadBDown = false;
				this.bJoypadSprint = false;
			}
		} else {
			inputState.isAiming = (this.isAimKeyDown() || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15F) && this.getPlayerNum() == 0 && StringUtils.isNullOrEmpty(this.getVariableString("BumpFallType"));
			if (Mouse.isButtonDown(1)) {
				this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
			} else {
				this.TimeRightPressed = 0.0F;
			}

			if (!this.isCharging) {
				inputState.isCharging = Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15F || this.isAimKeyDown();
			} else {
				inputState.isCharging = Mouse.isButtonDown(1) || this.isAimKeyDown();
			}

			int int1 = Core.getInstance().getKey("Run");
			int int2 = Core.getInstance().getKey("Sprint");
			if (this.isAllowRun()) {
				inputState.bRunning = GameKeyboard.isKeyDown(int1);
			}

			if (this.isAllowSprint()) {
				if (!Core.OptiondblTapJogToSprint) {
					if (GameKeyboard.isKeyDown(int2)) {
						inputState.bSprinting = true;
						this.pressedRunTimer = 1.0F;
					} else {
						inputState.bSprinting = false;
					}
				} else {
					if (!GameKeyboard.wasKeyDown(int1) && GameKeyboard.isKeyDown(int1) && this.pressedRunTimer < 30.0F && this.pressedRun) {
						inputState.bSprinting = true;
					}

					if (GameKeyboard.wasKeyDown(int1) && !GameKeyboard.isKeyDown(int1)) {
						inputState.bSprinting = false;
						this.pressedRun = true;
					}

					if (!inputState.bRunning) {
						inputState.bSprinting = false;
					}

					if (this.pressedRun) {
						++this.pressedRunTimer;
					}

					if (this.pressedRunTimer > 30.0F) {
						this.pressedRunTimer = 0.0F;
						this.pressedRun = false;
					}
				}
			}

			this.updateToggleToAim();
			if (inputState.bRunning || inputState.bSprinting) {
				this.setForceAim(false);
			}

			boolean boolean2;
			long long1;
			if (this.PlayerIndex == 0 && Core.getInstance().isToggleToRun()) {
				boolean1 = GameKeyboard.isKeyDown(int1);
				boolean2 = GameKeyboard.wasKeyDown(int1);
				long1 = System.currentTimeMillis();
				if (boolean1 && !boolean2) {
					this.runKeyDownMS = long1;
				} else if (!boolean1 && boolean2 && long1 - this.runKeyDownMS < 500L) {
					this.toggleForceRun();
				}
			}

			if (this.PlayerIndex == 0 && Core.getInstance().isToggleToSprint()) {
				boolean1 = GameKeyboard.isKeyDown(int2);
				boolean2 = GameKeyboard.wasKeyDown(int2);
				long1 = System.currentTimeMillis();
				if (boolean1 && !boolean2) {
					this.sprintKeyDownMS = long1;
				} else if (!boolean1 && boolean2 && long1 - this.sprintKeyDownMS < 500L) {
					this.toggleForceSprint();
				}
			}

			if (this.isForceAim()) {
				inputState.isAiming = true;
				inputState.isCharging = true;
			}

			if (this.isForceRun()) {
				inputState.bRunning = true;
			}

			if (this.isForceSprint()) {
				inputState.bSprinting = true;
			}
		}
	}

	public IsoZombie getClosestZombieToOtherZombie(IsoZombie zombie) {
		IsoZombie zombie2 = null;
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = IsoWorld.instance.CurrentCell.getObjectList();
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList2.get(int1);
			if (movingObject != zombie && movingObject instanceof IsoZombie) {
				arrayList.add((IsoZombie)movingObject);
			}
		}

		float float1 = 0.0F;
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoZombie zombie3 = (IsoZombie)arrayList.get(int2);
			float float2 = IsoUtils.DistanceTo(zombie3.x, zombie3.y, zombie.x, zombie.y);
			if (zombie2 == null || float2 < float1) {
				zombie2 = zombie3;
				float1 = float2;
			}
		}

		return zombie2;
	}

	@Deprecated
	public IsoGameCharacter getClosestZombieDist() {
		float float1 = 0.4F;
		boolean boolean1 = false;
		testHitPosition.x = this.x + this.getForwardDirection().x * float1;
		testHitPosition.y = this.y + this.getForwardDirection().y * float1;
		HandWeapon handWeapon = this.getWeapon();
		ArrayList arrayList = new ArrayList();
		for (int int1 = (int)testHitPosition.x - (int)handWeapon.getMaxRange(); int1 <= (int)testHitPosition.x + (int)handWeapon.getMaxRange(); ++int1) {
			for (int int2 = (int)testHitPosition.y - (int)handWeapon.getMaxRange(); int2 <= (int)testHitPosition.y + (int)handWeapon.getMaxRange(); ++int2) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)int1, (double)int2, (double)this.z);
				if (square != null && square.getMovingObjects().size() > 0) {
					for (int int3 = 0; int3 < square.getMovingObjects().size(); ++int3) {
						IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int3);
						if (movingObject instanceof IsoZombie) {
							Vector2 vector2 = tempVector2_1.set(this.getX(), this.getY());
							Vector2 vector22 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
							vector22.x -= vector2.x;
							vector22.y -= vector2.y;
							Vector2 vector23 = this.getForwardDirection();
							vector22.normalize();
							vector23.normalize();
							Float Float1 = vector22.dot(vector23);
							if (Float1 >= handWeapon.getMinAngle() || movingObject.isOnFloor()) {
								boolean1 = true;
							}

							if (boolean1 && ((IsoZombie)movingObject).Health > 0.0F) {
								((IsoZombie)movingObject).setHitFromBehind(this.isBehind((IsoZombie)movingObject));
								((IsoZombie)movingObject).setHitAngle(((IsoZombie)movingObject).getForwardDirection());
								((IsoZombie)movingObject).setPlayerAttackPosition(((IsoZombie)movingObject).testDotSide(this));
								float float2 = IsoUtils.DistanceTo(movingObject.x, movingObject.y, this.x, this.y);
								if (float2 < handWeapon.getMaxRange()) {
									arrayList.add((IsoZombie)movingObject);
								}
							}
						}
					}
				}
			}
		}

		if (!arrayList.isEmpty()) {
			Collections.sort(arrayList, new Comparator(){
				
				public int compare(IsoGameCharacter float1, IsoGameCharacter boolean1) {
					float handWeapon = IsoUtils.DistanceTo(float1.x, float1.y, IsoPlayer.testHitPosition.x, IsoPlayer.testHitPosition.y);
					float arrayList = IsoUtils.DistanceTo(boolean1.x, boolean1.y, IsoPlayer.testHitPosition.x, IsoPlayer.testHitPosition.y);
					if (handWeapon > arrayList) {
						return 1;
					} else {
						return arrayList > handWeapon ? -1 : 0;
					}
				}
			});

			return (IsoGameCharacter)arrayList.get(0);
		} else {
			return null;
		}
	}

	public void hitConsequences(HandWeapon handWeapon, IsoGameCharacter gameCharacter, boolean boolean1, float float1, boolean boolean2) {
		this.setM_bKnockedDown(gameCharacter.isCrit);
		if (gameCharacter instanceof IsoPlayer) {
			if (!StringUtils.isNullOrEmpty(this.getHitReaction())) {
				this.actionContext.reportEvent("washitpvpagain");
			}

			this.actionContext.reportEvent("washitpvp");
			this.setVariable("hitpvp", true);
		} else {
			this.actionContext.reportEvent("washit");
		}

		String string;
		if (boolean1) {
			if (!GameServer.bServer) {
				gameCharacter.xp.AddXP(PerkFactory.Perks.Strength, 2.0F);
				this.setHitForce(Math.min(0.5F, this.getHitForce()));
				this.setHitReaction("HitReaction");
				string = this.testDotSide(gameCharacter);
				this.setHitFromBehind("BEHIND".equals(string));
			}
		} else {
			if (!GameClient.bClient) {
				this.BodyDamage.DamageFromWeapon(handWeapon);
			}

			string = gameCharacter.getVariableString("ZombieHitReaction");
			if ("Bite".equals(string)) {
				String string2 = this.testDotSide(gameCharacter);
				boolean boolean3 = string2.equals("FRONT");
				boolean boolean4 = string2.equals("BEHIND");
				if (string2.equals("RIGHT")) {
					string = string + "LEFT";
				}

				if (string2.equals("LEFT")) {
					string = string + "RIGHT";
				}

				if (string != null && !"".equals(string)) {
					this.setHitReaction(string);
				}
			} else if (!this.m_bKnockedDown) {
				this.setHitReaction("HitReaction");
			}
		}
	}

	private HandWeapon getWeapon() {
		if (this.getPrimaryHandItem() instanceof HandWeapon) {
			return (HandWeapon)this.getPrimaryHandItem();
		} else {
			return this.getSecondaryHandItem() instanceof HandWeapon ? (HandWeapon)this.getSecondaryHandItem() : (HandWeapon)InventoryItemFactory.CreateItem("BareHands");
		}
	}

	private void updateMechanicsItems() {
		if (!GameServer.bServer && !this.mechanicsItem.isEmpty()) {
			Iterator iterator = this.mechanicsItem.keySet().iterator();
			ArrayList arrayList = new ArrayList();
			while (iterator.hasNext()) {
				Long Long1 = (Long)iterator.next();
				Long Long2 = (Long)this.mechanicsItem.get(Long1);
				if (GameTime.getInstance().getCalender().getTimeInMillis() > Long2 + 86400000L) {
					arrayList.add(Long1);
				}
			}

			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				this.mechanicsItem.remove(arrayList.get(int1));
			}
		}
	}

	private void enterExitVehicle() {
		boolean boolean1 = this.PlayerIndex == 0 && GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact"));
		if (boolean1) {
			this.bUseVehicle = true;
			this.useVehicleDuration += GameTime.instance.getRealworldSecondsSinceLastUpdate();
		}

		if (!this.bUsedVehicle && this.bUseVehicle && (!boolean1 || this.useVehicleDuration > 0.5F)) {
			this.bUsedVehicle = true;
			if (this.getVehicle() != null) {
				LuaEventManager.triggerEvent("OnUseVehicle", this, this.getVehicle(), this.useVehicleDuration > 0.5F);
			} else {
				for (int int1 = 0; int1 < this.getCell().vehicles.size(); ++int1) {
					BaseVehicle baseVehicle = (BaseVehicle)this.getCell().vehicles.get(int1);
					if (baseVehicle.getUseablePart(this) != null) {
						LuaEventManager.triggerEvent("OnUseVehicle", this, baseVehicle, this.useVehicleDuration > 0.5F);
						break;
					}
				}
			}
		}

		if (!boolean1) {
			this.bUseVehicle = false;
			this.bUsedVehicle = false;
			this.useVehicleDuration = 0.0F;
		}
	}

	private void checkActionGroup() {
		ActionGroup actionGroup = this.actionContext.getGroup();
		ActionGroup actionGroup2;
		if (this.getVehicle() == null) {
			actionGroup2 = ActionGroup.getActionGroup("player");
			if (actionGroup != actionGroup2) {
				this.advancedAnimator.OnAnimDataChanged(false);
				this.initializeStates();
				this.actionContext.setGroup(actionGroup2);
				this.clearVariable("bEnteringVehicle");
				this.clearVariable("EnterAnimationFinished");
				this.clearVariable("bExitingVehicle");
				this.clearVariable("ExitAnimationFinished");
				this.clearVariable("bSwitchingSeat");
				this.clearVariable("SwitchSeatAnimationFinished");
				this.setHitReaction("");
			}
		} else {
			actionGroup2 = ActionGroup.getActionGroup("player-vehicle");
			if (actionGroup != actionGroup2) {
				this.advancedAnimator.OnAnimDataChanged(false);
				this.initializeStates();
				this.actionContext.setGroup(actionGroup2);
			}
		}
	}

	public BaseVehicle getUseableVehicle() {
		if (this.getVehicle() != null) {
			return null;
		} else {
			int int1 = ((int)this.x - 4) / 10 - 1;
			int int2 = ((int)this.y - 4) / 10 - 1;
			int int3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F)) + 1;
			int int4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F)) + 1;
			for (int int5 = int2; int5 < int4; ++int5) {
				for (int int6 = int1; int6 < int3; ++int6) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int6 * 10, int5 * 10, 0);
					if (chunk != null) {
						for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
							BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
							if (baseVehicle.getUseablePart(this) != null || baseVehicle.getBestSeat(this) != -1) {
								return baseVehicle;
							}
						}
					}
				}
			}

			return null;
		}
	}

	public Boolean isNearVehicle() {
		if (this.getVehicle() != null) {
			return false;
		} else {
			int int1 = ((int)this.x - 4) / 10 - 1;
			int int2 = ((int)this.y - 4) / 10 - 1;
			int int3 = (int)Math.ceil((double)((this.x + 4.0F) / 10.0F)) + 1;
			int int4 = (int)Math.ceil((double)((this.y + 4.0F) / 10.0F)) + 1;
			for (int int5 = int2; int5 < int4; ++int5) {
				for (int int6 = int1; int6 < int3; ++int6) {
					IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int6 * 10, int5 * 10, 0);
					if (chunk != null) {
						for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
							BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
							if ((double)baseVehicle.DistTo(this) < 3.5) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
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
							if ((int)this.getZ() == (int)baseVehicle.getZ() && (!this.isLocalPlayer() || baseVehicle.getTargetAlpha(this.PlayerIndex) != 0.0F) && !(this.DistToSquared((float)((int)baseVehicle.x), (float)((int)baseVehicle.y)) >= 16.0F) && PolygonalMap2.instance.intersectLineWithVehicle(this.x, this.y, this.x + this.getForwardDirection().x * 4.0F, this.y + this.getForwardDirection().y * 4.0F, baseVehicle, tempVector2) && !PolygonalMap2.instance.lineClearCollide(this.x, this.y, tempVector2.x, tempVector2.y, (int)this.z, baseVehicle, false, true)) {
								return baseVehicle;
							}
						}
					}
				}
			}

			return null;
		}
	}

	private void updateWhileInVehicle() {
		this.bLookingWhileInVehicle = false;
		ActionGroup actionGroup = this.actionContext.getGroup();
		ActionGroup actionGroup2 = ActionGroup.getActionGroup("player-vehicle");
		if (actionGroup != actionGroup2) {
			this.advancedAnimator.OnAnimDataChanged(false);
			this.initializeStates();
			this.actionContext.setGroup(actionGroup2);
		}

		if (GameClient.bClient && this.getVehicle().getSeat(this) == -1) {
			DebugLog.log("forced " + this.getUsername() + " out of vehicle seat -1");
			this.setVehicle((BaseVehicle)null);
		} else {
			this.dirtyRecalcGridStackTime = 10.0F;
			if (this.getVehicle().isDriver(this)) {
				this.getVehicle().updatePhysics();
				boolean boolean1 = true;
				if (this.isAiming()) {
					WeaponType weaponType = WeaponType.getWeaponType((IsoGameCharacter)this);
					if (weaponType.equals(WeaponType.firearm)) {
						boolean1 = false;
					}
				}

				if (this.getVariableBoolean("isLoading")) {
					boolean1 = false;
				}

				if (boolean1) {
					this.getVehicle().updateControls();
				}
			} else if (GameClient.connection != null) {
				PassengerMap.updatePassenger(this);
			}

			this.fallTime = 0.0F;
			this.bSeenThisFrame = false;
			this.bCouldBeSeenThisFrame = false;
			this.closestZombie = 1000000.0F;
			this.setBeenMovingFor(this.getBeenMovingFor() - 0.625F * GameTime.getInstance().getMultiplier());
			if (!this.Asleep) {
				float float1 = (float)ZomboidGlobals.SittingEnduranceMultiplier;
				float1 *= 1.0F - this.stats.fatigue;
				float1 *= GameTime.instance.getMultiplier();
				Stats stats = this.stats;
				stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float1);
				this.stats.endurance = PZMath.clamp(this.stats.endurance, 0.0F, 1.0F);
			}

			this.updateToggleToAim();
			if (this.vehicle != null) {
				Vector3f vector3f = this.vehicle.getForwardVector(tempVector3f);
				boolean boolean2 = this.isAimControlActive();
				if (this.PlayerIndex == 0) {
					if (Mouse.isButtonDown(1)) {
						this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
					} else {
						this.TimeRightPressed = 0.0F;
					}

					boolean2 |= Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15F;
				}

				if (!boolean2 && this.isCurrentState(IdleState.instance())) {
					this.setForwardDirection(vector3f.x, vector3f.z);
					this.getForwardDirection().normalize();
				}

				if (this.lastAngle.x != this.getForwardDirection().x || this.lastAngle.y != this.getForwardDirection().y) {
					this.dirtyRecalcGridStackTime = 10.0F;
				}

				this.DirectionFromVector(this.getForwardDirection());
				AnimationPlayer animationPlayer = this.getAnimationPlayer();
				if (animationPlayer != null && animationPlayer.isReady()) {
					animationPlayer.SetForceDir(this.getForwardDirection());
					float float2 = animationPlayer.getAngle() + animationPlayer.getTwistAngle();
					this.dir = IsoDirections.fromAngle(tempVector2.setLengthAndDirection(float2, 1.0F));
				}

				boolean boolean3 = false;
				int int1 = this.vehicle.getSeat(this);
				VehiclePart vehiclePart = this.vehicle.getPassengerDoor(int1);
				if (vehiclePart != null) {
					VehicleWindow vehicleWindow = vehiclePart.findWindow();
					if (vehicleWindow != null && !vehicleWindow.isHittable()) {
						boolean3 = true;
					}
				}

				if (boolean3) {
					this.attackWhileInVehicle();
				} else if (boolean2) {
					this.bLookingWhileInVehicle = true;
					this.setAngleFromAim();
				} else {
					this.checkJoypadIgnoreAimUntilCentered();
					this.setIsAiming(false);
				}
			}

			this.updateCursorVisibility();
		}
	}

	private void attackWhileInVehicle() {
		this.setIsAiming(false);
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
			if (!this.bJoypadMovementActive) {
				return;
			}

			if (this.isChargingLT && !JoypadManager.instance.isLTPressed(this.JoypadBind)) {
				boolean2 = true;
			} else {
				boolean1 = this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind);
			}

			float float1 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
			float float2 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
			if (this.bJoypadIgnoreAimUntilCentered) {
				if (float1 == 0.0F && float2 == 0.0F) {
					this.bJoypadIgnoreAimUntilCentered = false;
				} else {
					float2 = 0.0F;
					float1 = 0.0F;
				}
			}

			this.setIsAiming(float1 * float1 + float2 * float2 >= 0.09F);
			this.isCharging = this.isAiming() && JoypadManager.instance.isRTPressed(this.JoypadBind);
			this.isChargingLT = this.isAiming() && JoypadManager.instance.isLTPressed(this.JoypadBind);
		} else {
			boolean boolean3 = this.isAimKeyDown();
			this.setIsAiming(boolean3 || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15F);
			if (this.isCharging) {
				this.isCharging = boolean3 || Mouse.isButtonDown(1);
			} else {
				this.isCharging = boolean3 || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= 0.15F;
			}

			if (this.isForceAim()) {
				this.setIsAiming(true);
				this.isCharging = true;
			}

			if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
				boolean2 = true;
			} else {
				boolean1 = this.isCharging && Mouse.isButtonDownUICheck(0);
				if (boolean1) {
					this.setIsAiming(true);
				}
			}
		}

		if (!this.isCharging && !this.isChargingLT) {
			this.chargeTime = 0.0F;
		}

		if (this.isAiming() && !this.bBannedAttacking && this.CanAttack()) {
			this.chargeTime += GameTime.instance.getMultiplier();
			this.useChargeTime = this.chargeTime;
			this.m_meleePressed = boolean2;
			this.setAngleFromAim();
			if (boolean2) {
				this.sprite.Animate = true;
				this.bDoShove = true;
				this.AttemptAttack(this.useChargeTime);
				this.useChargeTime = 0.0F;
				this.chargeTime = 0.0F;
			} else if (boolean1) {
				this.sprite.Animate = true;
				if (this.getRecoilDelay() <= 0.0F) {
					this.AttemptAttack(this.useChargeTime);
				}

				this.useChargeTime = 0.0F;
				this.chargeTime = 0.0F;
			}
		}
	}

	private void setAngleFromAim() {
		Vector2 vector2 = tempVector2;
		if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
			this.getControllerAimDir(vector2);
		} else {
			vector2.set(this.getX(), this.getY());
			int int1 = Mouse.getX();
			int int2 = Mouse.getY();
			vector2.x -= IsoUtils.XToIso((float)int1, (float)int2 + 55.0F * this.def.getScaleY(), this.getZ());
			vector2.y -= IsoUtils.YToIso((float)int1, (float)int2 + 55.0F * this.def.getScaleY(), this.getZ());
			vector2.x = -vector2.x;
			vector2.y = -vector2.y;
		}

		if (vector2.getLengthSquared() > 0.0F) {
			vector2.normalize();
			this.DirectionFromVector(vector2);
			this.setForwardDirection(vector2);
			if (this.lastAngle.x != vector2.x || this.lastAngle.y != vector2.y) {
				this.lastAngle.x = vector2.x;
				this.lastAngle.y = vector2.y;
				this.dirtyRecalcGridStackTime = 10.0F;
			}
		}
	}

	private void updateTorchStrength() {
		if (this.getTorchStrength() > 0.0F || this.flickTorch) {
			DrainableComboItem drainableComboItem = (DrainableComboItem)Type.tryCastTo(this.getActiveLightItem(), DrainableComboItem.class);
			if (drainableComboItem == null) {
				return;
			}

			if (Rand.Next(600 - (int)(0.4 / (double)drainableComboItem.getUsedDelta() * 100.0)) == 0) {
				this.flickTorch = true;
			}

			this.flickTorch = false;
			if (this.flickTorch) {
				if (Rand.Next(6) == 0) {
					drainableComboItem.setActivated(false);
				} else {
					drainableComboItem.setActivated(true);
				}

				if (Rand.Next(40) == 0) {
					this.flickTorch = false;
					drainableComboItem.setActivated(true);
				}
			}
		}
	}

	public IsoCell getCell() {
		return IsoWorld.instance.CurrentCell;
	}

	public void calculateContext() {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = this.x;
		IsoGridSquare[] gridSquareArray = new IsoGridSquare[4];
		if (this.dir == IsoDirections.N) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)(float2 - 1.0F), (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)float1, (double)(float2 - 1.0F), (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)(float2 - 1.0F), (double)float3);
		} else if (this.dir == IsoDirections.NE) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)float1, (double)(float2 - 1.0F), (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)(float2 - 1.0F), (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)float2, (double)float3);
		} else if (this.dir == IsoDirections.E) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)(float2 - 1.0F), (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)float2, (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)(float2 + 1.0F), (double)float3);
		} else if (this.dir == IsoDirections.SE) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)float2, (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)(float2 + 1.0F), (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)float1, (double)(float2 + 1.0F), (double)float3);
		} else if (this.dir == IsoDirections.S) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)(float1 + 1.0F), (double)(float2 + 1.0F), (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)float1, (double)(float2 + 1.0F), (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)(float2 + 1.0F), (double)float3);
		} else if (this.dir == IsoDirections.SW) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)float1, (double)(float2 + 1.0F), (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)(float2 + 1.0F), (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)float2, (double)float3);
		} else if (this.dir == IsoDirections.W) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)(float2 + 1.0F), (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)float2, (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)(float2 - 1.0F), (double)float3);
		} else if (this.dir == IsoDirections.NW) {
			gridSquareArray[2] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)float2, (double)float3);
			gridSquareArray[1] = this.getCell().getGridSquare((double)(float1 - 1.0F), (double)(float2 - 1.0F), (double)float3);
			gridSquareArray[3] = this.getCell().getGridSquare((double)float1, (double)(float2 - 1.0F), (double)float3);
		}

		gridSquareArray[0] = this.current;
		for (int int1 = 0; int1 < 4; ++int1) {
			IsoGridSquare square = gridSquareArray[int1];
			if (square == null) {
			}
		}
	}

	public boolean isSafeToClimbOver(IsoDirections directions) {
		IsoGridSquare square = null;
		switch (directions) {
		case N: 
			square = this.getCell().getGridSquare((double)this.x, (double)(this.y - 1.0F), (double)this.z);
			break;
		
		case S: 
			square = this.getCell().getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
			break;
		
		case W: 
			square = this.getCell().getGridSquare((double)(this.x - 1.0F), (double)this.y, (double)this.z);
			break;
		
		case E: 
			square = this.getCell().getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
			break;
		
		default: 
			return false;
		
		}
		if (square == null) {
			return false;
		} else if (square.Is(IsoFlagType.water)) {
			return false;
		} else {
			return !square.TreatAsSolidFloor() ? square.HasStairsBelow() : true;
		}
	}

	@Deprecated
	public boolean doContext(IsoDirections directions, boolean boolean1) {
		return this.doContext(directions);
	}

	public boolean doContext(IsoDirections directions) {
		if (this.isIgnoreContextKey()) {
			return false;
		} else if (this.isBlockMovement()) {
			return false;
		} else {
			for (int int1 = 0; int1 < this.getCell().vehicles.size(); ++int1) {
				BaseVehicle baseVehicle = (BaseVehicle)this.getCell().vehicles.get(int1);
				if (baseVehicle.getUseablePart(this) != null) {
					return false;
				}
			}

			float float1 = this.x - (float)((int)this.x);
			float float2 = this.y - (float)((int)this.y);
			IsoDirections directions2 = IsoDirections.Max;
			IsoDirections directions3 = IsoDirections.Max;
			if (directions == IsoDirections.NW) {
				if (float2 < float1) {
					if (this.doContextNSWE(IsoDirections.N)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.W)) {
						return true;
					}

					directions2 = IsoDirections.S;
					directions3 = IsoDirections.E;
				} else {
					if (this.doContextNSWE(IsoDirections.W)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.N)) {
						return true;
					}

					directions2 = IsoDirections.E;
					directions3 = IsoDirections.S;
				}
			} else if (directions == IsoDirections.NE) {
				float1 = 1.0F - float1;
				if (float2 < float1) {
					if (this.doContextNSWE(IsoDirections.N)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.E)) {
						return true;
					}

					directions2 = IsoDirections.S;
					directions3 = IsoDirections.W;
				} else {
					if (this.doContextNSWE(IsoDirections.E)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.N)) {
						return true;
					}

					directions2 = IsoDirections.W;
					directions3 = IsoDirections.S;
				}
			} else if (directions == IsoDirections.SE) {
				float1 = 1.0F - float1;
				float2 = 1.0F - float2;
				if (float2 < float1) {
					if (this.doContextNSWE(IsoDirections.S)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.E)) {
						return true;
					}

					directions2 = IsoDirections.N;
					directions3 = IsoDirections.W;
				} else {
					if (this.doContextNSWE(IsoDirections.E)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.S)) {
						return true;
					}

					directions2 = IsoDirections.W;
					directions3 = IsoDirections.N;
				}
			} else if (directions == IsoDirections.SW) {
				float2 = 1.0F - float2;
				if (float2 < float1) {
					if (this.doContextNSWE(IsoDirections.S)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.W)) {
						return true;
					}

					directions2 = IsoDirections.N;
					directions3 = IsoDirections.E;
				} else {
					if (this.doContextNSWE(IsoDirections.W)) {
						return true;
					}

					if (this.doContextNSWE(IsoDirections.S)) {
						return true;
					}

					directions2 = IsoDirections.E;
					directions3 = IsoDirections.N;
				}
			} else {
				if (this.doContextNSWE(directions)) {
					return true;
				}

				directions2 = directions.RotLeft(4);
			}

			IsoObject object;
			if (directions2 != IsoDirections.Max) {
				object = this.getContextDoorOrWindowOrWindowFrame(directions2);
				if (object != null) {
					this.doContextDoorOrWindowOrWindowFrame(directions2, object);
					return true;
				}
			}

			if (directions3 != IsoDirections.Max) {
				object = this.getContextDoorOrWindowOrWindowFrame(directions3);
				if (object != null) {
					this.doContextDoorOrWindowOrWindowFrame(directions3, object);
					return true;
				}
			}

			return false;
		}
	}

	private boolean doContextNSWE(IsoDirections directions) {
		assert directions == IsoDirections.N || directions == IsoDirections.S || directions == IsoDirections.W || directions == IsoDirections.E;
		if (this.current == null) {
			return false;
		} else if (directions == IsoDirections.N && this.current.Is(IsoFlagType.climbSheetN) && this.canClimbSheetRope(this.current)) {
			this.climbSheetRope();
			return true;
		} else if (directions == IsoDirections.S && this.current.Is(IsoFlagType.climbSheetS) && this.canClimbSheetRope(this.current)) {
			this.climbSheetRope();
			return true;
		} else if (directions == IsoDirections.W && this.current.Is(IsoFlagType.climbSheetW) && this.canClimbSheetRope(this.current)) {
			this.climbSheetRope();
			return true;
		} else if (directions == IsoDirections.E && this.current.Is(IsoFlagType.climbSheetE) && this.canClimbSheetRope(this.current)) {
			this.climbSheetRope();
			return true;
		} else {
			IsoGridSquare square = this.current.nav[directions.index()];
			boolean boolean1 = IsoWindow.isTopOfSheetRopeHere(square) && this.canClimbDownSheetRope(square);
			IsoObject object = this.getContextDoorOrWindowOrWindowFrame(directions);
			if (object != null) {
				this.doContextDoorOrWindowOrWindowFrame(directions, object);
				return true;
			} else {
				if (GameKeyboard.isKeyDown(42) && this.current != null && this.ticksSincePressedMovement > 15.0F) {
					IsoObject object2 = this.current.getDoor(true);
					if (object2 instanceof IsoDoor && ((IsoDoor)object2).isFacingSheet(this)) {
						((IsoDoor)object2).toggleCurtain();
						return true;
					}

					IsoObject object3 = this.current.getDoor(false);
					if (object3 instanceof IsoDoor && ((IsoDoor)object3).isFacingSheet(this)) {
						((IsoDoor)object3).toggleCurtain();
						return true;
					}

					IsoGridSquare square2;
					IsoObject object4;
					if (directions == IsoDirections.E) {
						square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
						object4 = square2 != null ? square2.getDoor(true) : null;
						if (object4 instanceof IsoDoor && ((IsoDoor)object4).isFacingSheet(this)) {
							((IsoDoor)object4).toggleCurtain();
							return true;
						}
					}

					if (directions == IsoDirections.S) {
						square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
						object4 = square2 != null ? square2.getDoor(false) : null;
						if (object4 instanceof IsoDoor && ((IsoDoor)object4).isFacingSheet(this)) {
							((IsoDoor)object4).toggleCurtain();
							return true;
						}
					}
				}

				boolean boolean2 = this.isSafeToClimbOver(directions);
				if (this.z > 0.0F && boolean1) {
					boolean2 = true;
				}

				if (this.timePressedContext < 0.5F && !boolean2) {
					return false;
				} else if (this.ignoreAutoVault) {
					return false;
				} else if (directions == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
					this.climbOverFence(directions);
					return true;
				} else if (directions == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
					this.climbOverFence(directions);
					return true;
				} else if (directions == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z).Is(IsoFlagType.HoppableN)) {
					this.climbOverFence(directions);
					return true;
				} else if (directions == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z).Is(IsoFlagType.HoppableW)) {
					this.climbOverFence(directions);
					return true;
				} else {
					return this.climbOverWall(directions);
				}
			}
		}
	}

	public IsoObject getContextDoorOrWindowOrWindowFrame(IsoDirections directions) {
		if (this.current != null && directions != null) {
			IsoGridSquare square = this.current.nav[directions.index()];
			IsoObject object = null;
			switch (directions) {
			case N: 
				object = this.current.getOpenDoor(directions);
				if (object != null) {
					return object;
				}

				object = this.current.getDoorOrWindowOrWindowFrame(directions, true);
				if (object != null) {
					return object;
				}

				object = this.current.getDoor(true);
				if (object != null) {
					return object;
				}

				if (square != null && !this.current.isBlockedTo(square)) {
					object = square.getOpenDoor(IsoDirections.S);
				}

				break;
			
			case S: 
				object = this.current.getOpenDoor(directions);
				if (object != null) {
					return object;
				}

				if (square != null) {
					boolean boolean1 = this.current.isBlockedTo(square);
					object = square.getDoorOrWindowOrWindowFrame(IsoDirections.N, boolean1);
					if (object != null) {
						return object;
					}

					object = square.getDoor(true);
				}

				break;
			
			case W: 
				object = this.current.getOpenDoor(directions);
				if (object != null) {
					return object;
				}

				object = this.current.getDoorOrWindowOrWindowFrame(directions, true);
				if (object != null) {
					return object;
				}

				object = this.current.getDoor(false);
				if (object != null) {
					return object;
				}

				if (square != null && !this.current.isBlockedTo(square)) {
					object = square.getOpenDoor(IsoDirections.E);
				}

				break;
			
			case E: 
				object = this.current.getOpenDoor(directions);
				if (object != null) {
					return object;
				}

				if (square != null) {
					boolean boolean2 = this.current.isBlockedTo(square);
					object = square.getDoorOrWindowOrWindowFrame(IsoDirections.W, boolean2);
					if (object != null) {
						return object;
					}

					object = square.getDoor(false);
				}

			
			}

			return object;
		} else {
			return null;
		}
	}

	private void doContextDoorOrWindowOrWindowFrame(IsoDirections directions, IsoObject object) {
		IsoGridSquare square = this.current.nav[directions.index()];
		boolean boolean1 = IsoWindow.isTopOfSheetRopeHere(square) && this.canClimbDownSheetRope(square);
		if (object instanceof IsoDoor) {
			IsoDoor door = (IsoDoor)object;
			if (GameKeyboard.isKeyDown(42) && door.HasCurtains() != null && door.isFacingSheet(this) && this.ticksSincePressedMovement > 15.0F) {
				door.toggleCurtain();
			} else if (this.timePressedContext >= 0.5F) {
				if (door.isHoppable() && !this.isIgnoreAutoVault()) {
					this.climbOverFence(directions);
				} else {
					door.ToggleDoor(this);
				}
			} else {
				door.ToggleDoor(this);
			}
		} else {
			IsoThumpable thumpable;
			if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor()) {
				thumpable = (IsoThumpable)object;
				if (this.timePressedContext >= 0.5F) {
					if (thumpable.isHoppable() && !this.isIgnoreAutoVault()) {
						this.climbOverFence(directions);
					} else {
						thumpable.ToggleDoor(this);
					}
				} else {
					thumpable.ToggleDoor(this);
				}
			} else {
				IsoCurtain curtain;
				if (object instanceof IsoWindow && !object.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
					IsoWindow window = (IsoWindow)object;
					if (GameKeyboard.isKeyDown(42)) {
						curtain = window.HasCurtains();
						if (curtain != null && this.current != null && !curtain.getSquare().isBlockedTo(this.current)) {
							curtain.ToggleDoor(this);
						}
					} else if (this.timePressedContext >= 0.5F) {
						if (window.canClimbThrough(this)) {
							this.climbThroughWindow(window);
						} else if (!window.PermaLocked && !window.isBarricaded() && !window.IsOpen()) {
							this.openWindow(window);
						}
					} else if (window.Health > 0 && !window.isDestroyed()) {
						IsoBarricade barricade = window.getBarricadeForCharacter(this);
						if (!window.open && barricade == null) {
							this.openWindow(window);
						} else if (barricade == null) {
							this.closeWindow(window);
						}
					} else if (window.isGlassRemoved()) {
						if (!this.isSafeToClimbOver(directions) && !object.getSquare().haveSheetRope && !boolean1) {
							return;
						}

						if (!window.isBarricaded()) {
							this.climbThroughWindow(window);
						}
					}
				} else if (object instanceof IsoThumpable && !object.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
					thumpable = (IsoThumpable)object;
					if (GameKeyboard.isKeyDown(42)) {
						curtain = thumpable.HasCurtains();
						if (curtain != null && this.current != null && !curtain.getSquare().isBlockedTo(this.current)) {
							curtain.ToggleDoor(this);
						}
					} else if (this.timePressedContext >= 0.5F) {
						if (thumpable.canClimbThrough(this)) {
							this.climbThroughWindow(thumpable);
						}
					} else {
						if (!this.isSafeToClimbOver(directions) && !object.getSquare().haveSheetRope && !boolean1) {
							return;
						}

						if (thumpable.canClimbThrough(this)) {
							this.climbThroughWindow(thumpable);
						}
					}
				} else if (IsoWindowFrame.isWindowFrame(object) && (this.timePressedContext >= 0.5F || this.isSafeToClimbOver(directions) || boolean1) && IsoWindowFrame.canClimbThrough(object, this)) {
					this.climbThroughWindowFrame(object);
				}
			}
		}
	}

	public boolean hopFence(IsoDirections directions, boolean boolean1) {
		float float1 = this.x - (float)((int)this.x);
		float float2 = this.y - (float)((int)this.y);
		if (directions == IsoDirections.NW) {
			if (float2 < float1) {
				return this.hopFence(IsoDirections.N, boolean1) ? true : this.hopFence(IsoDirections.W, boolean1);
			} else {
				return this.hopFence(IsoDirections.W, boolean1) ? true : this.hopFence(IsoDirections.N, boolean1);
			}
		} else if (directions == IsoDirections.NE) {
			float1 = 1.0F - float1;
			if (float2 < float1) {
				return this.hopFence(IsoDirections.N, boolean1) ? true : this.hopFence(IsoDirections.E, boolean1);
			} else {
				return this.hopFence(IsoDirections.E, boolean1) ? true : this.hopFence(IsoDirections.N, boolean1);
			}
		} else if (directions == IsoDirections.SE) {
			float1 = 1.0F - float1;
			float2 = 1.0F - float2;
			if (float2 < float1) {
				return this.hopFence(IsoDirections.S, boolean1) ? true : this.hopFence(IsoDirections.E, boolean1);
			} else {
				return this.hopFence(IsoDirections.E, boolean1) ? true : this.hopFence(IsoDirections.S, boolean1);
			}
		} else if (directions == IsoDirections.SW) {
			float2 = 1.0F - float2;
			if (float2 < float1) {
				return this.hopFence(IsoDirections.S, boolean1) ? true : this.hopFence(IsoDirections.W, boolean1);
			} else {
				return this.hopFence(IsoDirections.W, boolean1) ? true : this.hopFence(IsoDirections.S, boolean1);
			}
		} else if (this.current == null) {
			return false;
		} else {
			IsoGridSquare square = this.current.nav[directions.index()];
			if (square != null && !square.Is(IsoFlagType.water)) {
				if (directions == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
					if (boolean1) {
						return true;
					} else {
						this.climbOverFence(directions);
						return true;
					}
				} else if (directions == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
					if (boolean1) {
						return true;
					} else {
						this.climbOverFence(directions);
						return true;
					}
				} else if (directions == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z).Is(IsoFlagType.HoppableN)) {
					if (boolean1) {
						return true;
					} else {
						this.climbOverFence(directions);
						return true;
					}
				} else if (directions == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z).Is(IsoFlagType.HoppableW)) {
					if (boolean1) {
						return true;
					} else {
						this.climbOverFence(directions);
						return true;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	public boolean canClimbOverWall(IsoDirections directions) {
		if (this.isSafeToClimbOver(directions) && this.current != null) {
			if (this.current.haveRoof) {
				return false;
			} else if (this.current.getBuilding() != null) {
				return false;
			} else {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.current.x, this.current.y, this.current.z + 1);
				if (square != null && square.HasSlopedRoof()) {
					return false;
				} else {
					IsoGridSquare square2 = this.current.nav[directions.index()];
					if (square2.haveRoof) {
						return false;
					} else if (!square2.isSolid() && !square2.isSolidTrans()) {
						if (square2.getBuilding() != null) {
							return false;
						} else {
							IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(square2.x, square2.y, square2.z + 1);
							if (square3 != null && square3.HasSlopedRoof()) {
								return false;
							} else {
								switch (directions) {
								case N: 
									if (this.current.Is(IsoFlagType.CantClimb)) {
										return false;
									}

									if (!this.current.Has(IsoObjectType.wall)) {
										return false;
									}

									if (!this.current.Is(IsoFlagType.collideN)) {
										return false;
									}

									if (this.current.Is(IsoFlagType.HoppableN)) {
										return false;
									}

									if (square != null && square.Is(IsoFlagType.collideN)) {
										return false;
									}

									break;
								
								case S: 
									if (square2.Is(IsoFlagType.CantClimb)) {
										return false;
									}

									if (!square2.Has(IsoObjectType.wall)) {
										return false;
									}

									if (!square2.Is(IsoFlagType.collideN)) {
										return false;
									}

									if (square2.Is(IsoFlagType.HoppableN)) {
										return false;
									}

									if (square3 != null && square3.Is(IsoFlagType.collideN)) {
										return false;
									}

									break;
								
								case W: 
									if (this.current.Is(IsoFlagType.CantClimb)) {
										return false;
									}

									if (!this.current.Has(IsoObjectType.wall)) {
										return false;
									}

									if (!this.current.Is(IsoFlagType.collideW)) {
										return false;
									}

									if (this.current.Is(IsoFlagType.HoppableW)) {
										return false;
									}

									if (square != null && square.Is(IsoFlagType.collideW)) {
										return false;
									}

									break;
								
								case E: 
									if (square2.Is(IsoFlagType.CantClimb)) {
										return false;
									}

									if (!square2.Has(IsoObjectType.wall)) {
										return false;
									}

									if (!square2.Is(IsoFlagType.collideW)) {
										return false;
									}

									if (square2.Is(IsoFlagType.HoppableW)) {
										return false;
									}

									if (square3 != null && square3.Is(IsoFlagType.collideW)) {
										return false;
									}

									break;
								
								default: 
									return false;
								
								}

								return IsoWindow.canClimbThroughHelper(this, this.current, square2, directions == IsoDirections.N || directions == IsoDirections.S);
							}
						}
					} else {
						return false;
					}
				}
			}
		} else {
			return false;
		}
	}

	public boolean climbOverWall(IsoDirections directions) {
		if (!this.canClimbOverWall(directions)) {
			return false;
		} else {
			this.dropHeavyItems();
			ClimbOverWallState.instance().setParams(this, directions);
			this.actionContext.reportEvent("EventClimbWall");
			return true;
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

	public boolean DoAttack(float float1) {
		return this.DoAttack(float1, false, (String)null);
	}

	public boolean DoAttack(float float1, boolean boolean1, String string) {
		if (!this.authorizeMeleeAction) {
			return false;
		} else {
			this.setForceShove(boolean1);
			this.setClickSound(string);
			this.pressedAttack();
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
		this.stats.MusicZombiesTargeting = 0;
		this.stats.MusicZombiesVisible = 0;
		this.NumSurvivorsInVicinity = 0;
		if (this.getCurrentSquare() != null) {
			boolean boolean1 = GameServer.bServer;
			boolean boolean2 = GameClient.bClient;
			int int1 = this.PlayerIndex;
			IsoPlayer player = getInstance();
			float float1 = this.getX();
			float float2 = this.getY();
			float float3 = this.getZ();
			int int2 = 0;
			int int3 = 0;
			int int4 = this.getCell().getObjectList().size();
			for (int int5 = 0; int5 < int4; ++int5) {
				IsoMovingObject movingObject = (IsoMovingObject)this.getCell().getObjectList().get(int5);
				if (!(movingObject instanceof IsoPhysicsObject) && !(movingObject instanceof BaseVehicle)) {
					if (movingObject == this) {
						this.spottedList.add(movingObject);
					} else {
						float float4 = movingObject.getX();
						float float5 = movingObject.getY();
						float float6 = movingObject.getZ();
						float float7 = IsoUtils.DistanceTo(float4, float5, float1, float2);
						if (float7 < 20.0F) {
							++int2;
						}

						IsoGridSquare square = movingObject.getCurrentSquare();
						if (square != null) {
							IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
							IsoPlayer player2 = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
							IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
							if (player != null && movingObject != player && gameCharacter != null && gameCharacter.isInvisible() && player.accessLevel.isEmpty()) {
								gameCharacter.setAlphaAndTarget(int1, 0.0F);
							} else {
								float float8 = this.getSeeNearbyCharacterDistance();
								boolean boolean3;
								if (boolean1) {
									boolean3 = ServerLOS.instance.isCouldSee(this, square);
								} else {
									boolean3 = square.isCouldSee(int1);
								}

								boolean boolean4;
								if (boolean2 && player2 != null) {
									boolean4 = true;
								} else if (!boolean1) {
									boolean4 = square.isCanSee(int1);
								} else {
									boolean4 = boolean3;
								}

								if (!this.isAsleep() && (boolean4 || float7 < float8 && boolean3)) {
									this.TestZombieSpotPlayer(movingObject);
									if (gameCharacter != null && gameCharacter.IsVisibleToPlayer[int1]) {
										if (gameCharacter instanceof IsoSurvivor) {
											++this.NumSurvivorsInVicinity;
										}

										if (zombie != null) {
											this.lastSeenZombieTime = 0.0;
											if (float6 >= float3 - 1.0F && float7 < 7.0F && !zombie.Ghost && !zombie.isFakeDead() && square.getRoom() == this.getCurrentSquare().getRoom()) {
												this.TicksSinceSeenZombie = 0;
												++this.stats.NumVisibleZombies;
											}

											if (float7 < 3.0F) {
												++int3;
											}

											if (!zombie.isSceneCulled()) {
												++this.stats.MusicZombiesVisible;
												if (zombie.target == this) {
													++this.stats.MusicZombiesTargeting;
												}
											}
										}

										this.spottedList.add(gameCharacter);
										if (!(player2 instanceof IsoPlayer)) {
											if (player2 != null && player2 != player) {
												player2.setTargetAlpha(int1, 1.0F);
											} else {
												gameCharacter.setTargetAlpha(int1, 1.0F);
											}
										}

										float float9 = 4.0F;
										if (this.stats.NumVisibleZombies > 4) {
											float9 = 7.0F;
										}

										if (float7 < float9 && gameCharacter instanceof IsoZombie && (int)float6 == (int)float3 && !this.isGhostMode() && !boolean2) {
											GameTime.instance.setMultiplier(1.0F);
											if (!boolean1) {
												UIManager.getSpeedControls().SetCurrentGameSpeed(1);
											}
										}

										if (float7 < float9 && gameCharacter instanceof IsoZombie && (int)float6 == (int)float3 && !this.LastSpotted.contains(gameCharacter)) {
											Stats stats = this.stats;
											stats.NumVisibleZombies += 2;
										}
									} else if (player2 != null && player2 != player && boolean2) {
									}
								} else {
									if (movingObject != instance) {
										movingObject.setTargetAlpha(int1, 0.0F);
									}

									if (boolean3) {
										this.TestZombieSpotPlayer(movingObject);
									}
								}

								if (float7 < 2.0F && movingObject.getTargetAlpha(int1) == 1.0F) {
									movingObject.setAlpha(int1, 1.0F);
								}
							}
						}
					}
				}
			}

			if (this.isAlive() && int3 > 0 && this.stats.LastVeryCloseZombies == 0 && this.stats.NumVisibleZombies > 0 && this.stats.LastNumVisibleZombies == 0 && this.timeSinceLastStab >= 600.0F) {
				this.timeSinceLastStab = 0.0F;
				this.getEmitter().playSoundImpl("ZombieSurprisedPlayer", (IsoObject)null);
			}

			if (this.stats.NumVisibleZombies > 0) {
				this.timeSinceLastStab = 0.0F;
			}

			if (this.timeSinceLastStab < 600.0F) {
				this.timeSinceLastStab += GameTime.getInstance().getMultiplier() / 1.6F;
			}

			float float10 = (float)int2 / 20.0F;
			if (float10 > 1.0F) {
				float10 = 1.0F;
			}

			float10 *= 0.6F;
			SoundManager.instance.BlendVolume(MainScreenState.ambient, float10);
			int int6 = 0;
			for (int int7 = 0; int7 < this.spottedList.size(); ++int7) {
				if (!this.LastSpotted.contains(this.spottedList.get(int7))) {
					this.LastSpotted.add((IsoMovingObject)this.spottedList.get(int7));
				}

				if (this.spottedList.get(int7) instanceof IsoZombie) {
					++int6;
				}
			}

			if (this.ClearSpottedTimer <= 0 && int6 == 0) {
				this.LastSpotted.clear();
				this.ClearSpottedTimer = 1000;
			} else {
				--this.ClearSpottedTimer;
			}

			this.stats.LastNumVisibleZombies = this.stats.NumVisibleZombies;
			this.stats.LastVeryCloseZombies = int3;
		}
	}

	public float getSeeNearbyCharacterDistance() {
		return 3.5F - this.stats.getFatigue();
	}

	private boolean checkSpottedPLayerTimer(IsoPlayer player) {
		if (!player.spottedByPlayer) {
			return false;
		} else {
			if (this.spottedPlayerTimer.containsKey(player.getRemoteID())) {
				this.spottedPlayerTimer.put(player.getRemoteID(), (Integer)this.spottedPlayerTimer.get(player.getRemoteID()) + 1);
			} else {
				this.spottedPlayerTimer.put(player.getRemoteID(), 1);
			}

			if ((Integer)this.spottedPlayerTimer.get(player.getRemoteID()) > 100) {
				player.spottedByPlayer = false;
				player.doRenderShadow = false;
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean checkCanSeeClient(IsoPlayer player) {
		player.doRenderShadow = true;
		Vector2 vector2 = tempVector2_1.set(this.getX(), this.getY());
		Vector2 vector22 = tempVector2_2.set(player.getX(), player.getY());
		vector22.x -= vector2.x;
		vector22.y -= vector2.y;
		Vector2 vector23 = this.getForwardDirection();
		vector22.normalize();
		vector23.normalize();
		vector23.normalize();
		float float1 = vector22.dot(vector23);
		if (GameClient.bClient && player != this && this.isLocalPlayer()) {
			if (!this.getAccessLevel().equals("None") && this.canSeeAll) {
				player.spottedByPlayer = true;
				return true;
			} else if (ServerOptions.getInstance().HidePlayersBehindYou.getValue() && (double)float1 < -0.5) {
				return this.checkSpottedPLayerTimer(player);
			} else if (player.isGhostMode() && this.getAccessLevel().equals("None")) {
				player.doRenderShadow = false;
				player.spottedByPlayer = false;
				return false;
			} else {
				IsoGridSquare.ILighting iLighting = player.getCurrentSquare().lighting[this.getPlayerNum()];
				if (!iLighting.bCouldSee()) {
					return this.checkSpottedPLayerTimer(player);
				} else if (player.isSneaking() && !player.isSprinting()) {
					float float2 = player.getCurrentSquare().DistTo(this.getCurrentSquare());
					if (float2 > 30.0F) {
						player.spottedByPlayer = false;
					}

					if (player.spottedByPlayer) {
						return true;
					} else {
						player.doRenderShadow = true;
						float float3 = (float)(Math.pow((double)Math.max(40.0F - float2, 0.0F), 3.0) / 12000.0);
						float float4 = (float)(1.0 - (double)((float)player.remoteSneakLvl / 10.0F) * 0.9 + 0.3);
						float float5 = 1.0F;
						if ((double)float1 < 0.8) {
							float5 = 0.3F;
						}

						if ((double)float1 < 0.6) {
							float5 = 0.05F;
						}

						float float6 = (iLighting.lightInfo().getR() + iLighting.lightInfo().getG() + iLighting.lightInfo().getB()) / 3.0F;
						float float7 = (float)((1.0 - (double)((float)this.getMoodles().getMoodleLevel(MoodleType.Tired) / 5.0F)) * 0.7 + 0.3);
						float float8 = 0.1F;
						if (player.isPlayerMoving()) {
							float8 = 0.35F;
						}

						if (player.isRunning()) {
							float8 = 1.0F;
						}

						ArrayList arrayList = PolygonalMap2.instance.getPointInLine(player.getX(), player.getY(), this.getX(), this.getY(), (int)this.getZ());
						IsoGridSquare square = null;
						float float9 = 0.0F;
						float float10 = 0.0F;
						boolean boolean1 = false;
						float float11;
						for (int int1 = 0; int1 < arrayList.size(); ++int1) {
							PolygonalMap2.Point point = (PolygonalMap2.Point)arrayList.get(int1);
							square = IsoCell.getInstance().getGridSquare((double)point.x, (double)point.y, (double)this.getZ());
							float11 = square.getGridSneakModifier(false);
							if (float11 > 1.0F) {
								boolean1 = true;
								break;
							}

							for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
								IsoObject object = (IsoObject)square.getObjects().get(int2);
								if (object.getSprite().getProperties().Is(IsoFlagType.solidtrans) || object.getSprite().getProperties().Is(IsoFlagType.solid) || object.getSprite().getProperties().Is(IsoFlagType.windowW) || object.getSprite().getProperties().Is(IsoFlagType.windowN)) {
									boolean1 = true;
									break;
								}
							}

							if (boolean1) {
								break;
							}
						}

						if (boolean1) {
							float9 = square.DistTo(player.getCurrentSquare());
							float10 = square.DistTo(this.getCurrentSquare());
						}

						float float12 = float10 < 2.0F ? 5.0F : Math.min(float9, 5.0F);
						float12 = Math.max(0.0F, float12 - 1.0F);
						float12 = (float)((double)float12 / 5.0 * 0.9 + 0.1);
						float float13 = Math.max(0.1F, 1.0F - ClimateManager.getInstance().getFogIntensity());
						float11 = float5 * float3 * float6 * float4 * float7 * float8 * float12 * float13;
						if (float11 >= 1.0F) {
							player.spottedByPlayer = true;
							return true;
						} else {
							float float14 = float11 * 1.0F;
							float11 = (float)(1.0 - Math.pow((double)(1.0F - float11), (double)GameTime.getInstance().getMultiplier()));
							float11 *= 0.5F;
							boolean boolean2 = Rand.Next(0.0F, 1.0F) < float11;
							player.spottedByPlayer = boolean2;
							if (!boolean2) {
								player.doRenderShadow = false;
							}

							return boolean2;
						}
					}
				} else {
					player.spottedByPlayer = true;
					return true;
				}
			}
		} else {
			return true;
		}
	}

	public String getTimeSurvived() {
		String string = "";
		int int1 = (int)this.getHoursSurvived();
		int int2 = int1 / 24;
		int int3 = int1 % 24;
		int int4 = int2 / 30;
		int2 %= 30;
		int int5 = int4 / 12;
		int4 %= 12;
		String string2 = Translator.getText("IGUI_Gametime_day");
		String string3 = Translator.getText("IGUI_Gametime_year");
		String string4 = Translator.getText("IGUI_Gametime_hour");
		String string5 = Translator.getText("IGUI_Gametime_month");
		if (int5 != 0) {
			if (int5 > 1) {
				string3 = Translator.getText("IGUI_Gametime_years");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + int5 + " " + string3;
		}

		if (int4 != 0) {
			if (int4 > 1) {
				string5 = Translator.getText("IGUI_Gametime_months");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + int4 + " " + string5;
		}

		if (int2 != 0) {
			if (int2 > 1) {
				string2 = Translator.getText("IGUI_Gametime_days");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + int2 + " " + string2;
		}

		if (int3 != 0) {
			if (int3 > 1) {
				string4 = Translator.getText("IGUI_Gametime_hours");
			}

			if (string.length() > 0) {
				string = string + ", ";
			}

			string = string + int3 + " " + string4;
		}

		if (string.isEmpty()) {
			int int6 = (int)(this.HoursSurvived * 60.0);
			string = int6 + " " + Translator.getText("IGUI_Gametime_minutes");
		}

		return string;
	}

	public boolean IsUsingAimWeapon() {
		if (this.leftHandItem == null) {
			return false;
		} else if (!(this.leftHandItem instanceof HandWeapon)) {
			return false;
		} else if (!this.isAiming()) {
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
		} else if (!this.isAiming()) {
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

	public void setSleepingPillsTaken(int int1) {
		this.sleepingPillsTaken = int1;
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
		float float1 = 0.0F;
		if (this.getClothingItem_Feet() != null) {
			float1 += ((Clothing)this.getClothingItem_Feet()).getTemperature();
		}

		if (this.getClothingItem_Hands() != null) {
			float1 += ((Clothing)this.getClothingItem_Hands()).getTemperature();
		}

		if (this.getClothingItem_Head() != null) {
			float1 += ((Clothing)this.getClothingItem_Head()).getTemperature();
		}

		if (this.getClothingItem_Legs() != null) {
			float1 += ((Clothing)this.getClothingItem_Legs()).getTemperature();
		}

		if (this.getClothingItem_Torso() != null) {
			float1 += ((Clothing)this.getClothingItem_Torso()).getTemperature();
		}

		return float1;
	}

	public float getPlayerClothingInsulation() {
		float float1 = 0.0F;
		if (this.getClothingItem_Feet() != null) {
			float1 += ((Clothing)this.getClothingItem_Feet()).getInsulation() * 0.1F;
		}

		if (this.getClothingItem_Hands() != null) {
			float1 += ((Clothing)this.getClothingItem_Hands()).getInsulation() * 0.0F;
		}

		if (this.getClothingItem_Head() != null) {
			float1 += ((Clothing)this.getClothingItem_Head()).getInsulation() * 0.0F;
		}

		if (this.getClothingItem_Legs() != null) {
			float1 += ((Clothing)this.getClothingItem_Legs()).getInsulation() * 0.3F;
		}

		if (this.getClothingItem_Torso() != null) {
			float1 += ((Clothing)this.getClothingItem_Torso()).getInsulation() * 0.6F;
		}

		return float1;
	}

	public InventoryItem getActiveLightItem() {
		if (this.rightHandItem != null && this.rightHandItem.isEmittingLight()) {
			return this.rightHandItem;
		} else if (this.leftHandItem != null && this.leftHandItem.isEmittingLight()) {
			return this.leftHandItem;
		} else {
			AttachedItems attachedItems = this.getAttachedItems();
			for (int int1 = 0; int1 < attachedItems.size(); ++int1) {
				InventoryItem inventoryItem = attachedItems.getItemByIndex(int1);
				if (inventoryItem.isEmittingLight()) {
					return inventoryItem;
				}
			}

			return null;
		}
	}

	public boolean isTorchCone() {
		if (this.bRemote) {
			return this.mpTorchCone;
		} else {
			InventoryItem inventoryItem = this.getActiveLightItem();
			return inventoryItem != null && inventoryItem.isTorchCone();
		}
	}

	public float getTorchDot() {
		if (this.bRemote) {
		}

		InventoryItem inventoryItem = this.getActiveLightItem();
		return inventoryItem != null ? inventoryItem.getTorchDot() : 0.0F;
	}

	public float getLightDistance() {
		if (this.bRemote) {
			return this.mpTorchDist;
		} else {
			InventoryItem inventoryItem = this.getActiveLightItem();
			return inventoryItem != null ? (float)inventoryItem.getLightDistance() : 0.0F;
		}
	}

	public boolean pressedMovement(boolean boolean1) {
		if (this.isNPC) {
			return false;
		} else if (this.bRemote) {
			return this.networkAI.pressedMovement;
		} else {
			this.setVariable("pressedRunButton", GameKeyboard.isKeyDown(Core.getInstance().getKey("Run")));
			if (!boolean1 && (this.isBlockMovement() || this.isIgnoreInputsForDirection())) {
				return false;
			} else if (this.PlayerIndex == 0 && (GameKeyboard.isKeyDown(Core.getInstance().getKey("Left")) || GameKeyboard.isKeyDown(Core.getInstance().getKey("Right")) || GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward")) || GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward")))) {
				if (this.isLocalPlayer()) {
					this.networkAI.pressedMovement = true;
				}

				return true;
			} else {
				if (this.JoypadBind != -1) {
					float float1 = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
					float float2 = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
					float float3 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
					if (Math.abs(float1) > float3 || Math.abs(float2) > float3) {
						if (this.isLocalPlayer()) {
							this.networkAI.pressedMovement = true;
						}

						return true;
					}
				}

				return false;
			}
		}
	}

	public boolean pressedCancelAction() {
		if (this.isNPC) {
			return false;
		} else if (this.PlayerIndex == 0 && GameKeyboard.isKeyDown(Core.getInstance().getKey("CancelAction"))) {
			return true;
		} else {
			return this.JoypadBind != -1 ? JoypadManager.instance.isBButtonStartPress(this.JoypadBind) : false;
		}
	}

	public boolean pressedAim() {
		if (this.isNPC) {
			return false;
		} else {
			if (this.PlayerIndex == 0) {
				if (this.isAimKeyDown()) {
					return true;
				}

				if (Mouse.isButtonDownUICheck(1)) {
					return true;
				}
			}

			if (this.JoypadBind != -1) {
				float float1 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
				float float2 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
				if (Math.abs(float1) > 0.1F || Math.abs(float2) > 0.1F) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isDoingActionThatCanBeCancelled() {
		if (this.isDead()) {
			return false;
		} else if (!this.getCharacterActions().isEmpty()) {
			return true;
		} else {
			State state = this.getCurrentState();
			if (state != null && state.isDoingActionThatCanBeCancelled()) {
				return true;
			} else {
				for (int int1 = 0; int1 < this.stateMachine.getSubStateCount(); ++int1) {
					state = this.stateMachine.getSubStateAt(int1);
					if (state != null && state.isDoingActionThatCanBeCancelled()) {
						return true;
					}
				}

				return false;
			}
		}
	}

	public long getSteamID() {
		return this.steamID;
	}

	public void setSteamID(long long1) {
		this.steamID = long1;
	}

	public boolean isTargetedByZombie() {
		return this.targetedByZombie;
	}

	public boolean isMaskClicked(int int1, int int2, boolean boolean1) {
		return this.sprite == null ? false : this.sprite.isMaskClicked(this.dir, int1, int2, boolean1);
	}

	public int getOffSetXUI() {
		return this.offSetXUI;
	}

	public void setOffSetXUI(int int1) {
		this.offSetXUI = int1;
	}

	public int getOffSetYUI() {
		return this.offSetYUI;
	}

	public void setOffSetYUI(int int1) {
		this.offSetYUI = int1;
	}

	public String getUsername() {
		return this.getUsername(false);
	}

	public String getUsername(Boolean Boolean1) {
		String string = this.username;
		if (Boolean1 && GameClient.bClient && ServerOptions.instance.ShowFirstAndLastName.getValue() && "None".equals(this.getAccessLevel())) {
			String string2 = this.getDescriptor().getForename();
			string = string2 + " " + this.getDescriptor().getSurname();
			string = string + " (" + this.username + ")";
		}

		return string;
	}

	public void setUsername(String string) {
		this.username = string;
	}

	public void updateUsername() {
		if (!GameClient.bClient && !GameServer.bServer) {
			String string = this.getDescriptor().getForename();
			this.username = string + this.getDescriptor().getSurname();
		}
	}

	public int getOnlineID() {
		return this.OnlineID;
	}

	public boolean isLocalPlayer() {
		if (GameServer.bServer) {
			return false;
		} else {
			for (int int1 = 0; int1 < numPlayers; ++int1) {
				if (players[int1] == this) {
					return true;
				}
			}

			return false;
		}
	}

	public static void setLocalPlayer(int int1, IsoPlayer player) {
		players[int1] = player;
	}

	public boolean isOnlyPlayerAsleep() {
		if (!this.isAsleep()) {
			return false;
		} else {
			for (int int1 = 0; int1 < numPlayers; ++int1) {
				if (players[int1] != null && !players[int1].isDead() && players[int1] != this && players[int1].isAsleep()) {
					return false;
				}
			}

			return true;
		}
	}

	public void OnDeath() {
		super.OnDeath();
		this.advancedAnimator.SetState("death");
		if (!GameServer.bServer) {
			this.StopAllActionQueue();
			if (!GameClient.bClient) {
				this.dropHandItems();
			}

			if (allPlayersDead()) {
				SoundManager.instance.playMusic(DEATH_MUSIC_NAME);
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

	public void setNoClip(boolean boolean1) {
		this.noClip = boolean1;
	}

	public void setAuthorizeMeleeAction(boolean boolean1) {
		this.authorizeMeleeAction = boolean1;
	}

	public boolean isAuthorizeMeleeAction() {
		return this.authorizeMeleeAction;
	}

	public void setAuthorizeShoveStomp(boolean boolean1) {
		this.authorizeShoveStomp = boolean1;
	}

	public boolean isAuthorizeShoveStomp() {
		return this.authorizeShoveStomp;
	}

	public boolean isBlockMovement() {
		return this.blockMovement;
	}

	public void setBlockMovement(boolean boolean1) {
		this.blockMovement = boolean1;
	}

	public void startReceivingBodyDamageUpdates(IsoPlayer player) {
		if (GameClient.bClient && player != null && player != this && this.isLocalPlayer() && !player.isLocalPlayer()) {
			player.resetBodyDamageRemote();
			BodyDamageSync.instance.startReceivingUpdates(player.getOnlineID());
		}
	}

	public void stopReceivingBodyDamageUpdates(IsoPlayer player) {
		if (GameClient.bClient && player != null && player != this && !player.isLocalPlayer()) {
			BodyDamageSync.instance.stopReceivingUpdates(player.getOnlineID());
		}
	}

	public Nutrition getNutrition() {
		return this.nutrition;
	}

	public Fitness getFitness() {
		return this.fitness;
	}

	private boolean updateRemotePlayer() {
		if (this.bRemote || GameClient.bClient && this.NetRemoteState == NetRemoteState_Attack && !this.def.Finished) {
			if (GameServer.bServer) {
				ServerLOS.instance.doServerZombieLOS(this);
				ServerLOS.instance.updateLOS(this);
				if (this.isDead()) {
					return true;
				}

				this.removeFromSquare();
				this.setX(this.realx);
				this.setY(this.realy);
				this.setZ((float)this.realz);
				this.setLx(this.realx);
				this.setLy(this.realy);
				this.setLz((float)this.realz);
				this.ensureOnTile();
				this.doServerFootstepSound();
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

			if (GameClient.bClient) {
				this.updateMovementRates();
				float float1 = this.networkAI.targetX;
				float float2 = this.networkAI.targetY;
				float float3 = (float)this.networkAI.targetZ;
				int int1 = this.networkAI.targetT;
				PathFindBehavior2 pathFindBehavior2 = this.getPathFindBehavior2();
				if (!this.networkAI.events.isEmpty()) {
					NetworkPlayerAI.Event event = (NetworkPlayerAI.Event)this.networkAI.events.peek();
					if (this.networkAI.events.contains(event) && event.process(this)) {
						DebugLog.log(DebugType.Multiplayer, "Event processed: " + event.getDescription());
						this.JustMoved = this.m_isPlayerMoving = this.networkAI.moving = false;
						this.networkAI.events.remove(event);
						this.networkAI.lastEvent = event;
						if (this.networkAI.usePathFind) {
							pathFindBehavior2.reset();
							this.setPath2((PolygonalMap2.Path)null);
							this.networkAI.usePathFind = false;
						}

						return true;
					}

					if (!event.isMovableEvent()) {
						tempo.set(event.x - this.x, event.y - this.y);
						float1 = event.x;
						float2 = event.y;
						float3 = event.z;
						int1 = (int)((float)(GameTime.getServerTime() / 1000000L) + tempo.getLength() / 0.00122F);
					}

					if (event.isTimeout()) {
						DebugLog.log(DebugType.Multiplayer, "Event timeout: " + event.getDescription());
						this.JustMoved = this.m_isPlayerMoving = this.networkAI.moving = false;
						this.networkAI.events.remove(event);
						this.networkAI.lastEvent = event;
						if (this.networkAI.usePathFind) {
							pathFindBehavior2.reset();
							this.setPath2((PolygonalMap2.Path)null);
							this.networkAI.usePathFind = false;
						}

						return true;
					}
				} else if (!this.networkAI.moving && this.strike == null && this.strikes.isEmpty() && IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) < 0.7F) {
					this.DirectionFromVector(this.networkAI.direction);
					this.setForwardDirection(this.networkAI.direction);
					this.JustMoved = this.m_isPlayerMoving = false;
					this.networkAI.forcePathFinder = false;
					if (this.networkAI.usePathFind) {
						pathFindBehavior2.reset();
						this.setPath2((PolygonalMap2.Path)null);
						this.networkAI.usePathFind = false;
					}

					this.m_deltaX = 0.0F;
					this.m_deltaY = 0.0F;
					return true;
				}

				if (this.strike != null) {
					float1 = this.strike.zom.x;
					float2 = this.strike.zom.y;
					float3 = this.strike.zom.z;
					int1 = (int)(Math.sqrt((double)this.getDistanceSq(this.strike.zom)) / 0.0012199999764561653);
					long long1 = this.strike.lifeTime - System.currentTimeMillis();
					if (long1 < 0L || long1 > 3000L) {
						this.strike = null;
					}
				} else if (!this.strikes.isEmpty()) {
					NetworkStrikeAI.Strike strike = (NetworkStrikeAI.Strike)this.strikes.get(0);
					long long2 = strike.lifeTime - System.currentTimeMillis();
					if (long2 >= 0L && long2 <= 3000L) {
						if (NetworkStrikeAI.canStrike(strike)) {
							NetworkStrikeAI.executeStrike(strike);
							NetworkStrikeAI.deleteStrike(strike);
						} else {
							if (strike.zom.strike == null) {
								strike.zom.strike = strike;
								DebugLog.log("updateRemotePlayer setStrikeToZombie objType:" + strike.objType);
							}

							float1 = strike.zom.x;
							float2 = strike.zom.y;
							float3 = strike.zom.z;
							int1 = (int)((double)(GameTime.getServerTime() / 1000000L) + Math.sqrt((double)this.getDistanceSq(strike.zom)) / 0.0012199999764561653);
						}
					} else {
						NetworkStrikeAI.deleteStrike(strike);
					}
				}

				if (!this.networkAI.forcePathFinder && this.isCollidedThisFrame() && IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) > 3.0F) {
					this.networkAI.forcePathFinder = true;
				}

				if (this.networkAI.forcePathFinder && !PolygonalMap2.instance.lineClearCollide(this.x, this.y, tempVector2.x, tempVector2.y, (int)this.z, this.vehicle, false, true) && IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) < 2.0F) {
					this.networkAI.forcePathFinder = false;
				}

				float float4;
				if (!this.networkAI.needToMovingUsingPathFinder && !this.networkAI.forcePathFinder) {
					if (this.networkAI.usePathFind) {
						pathFindBehavior2.reset();
						this.setPath2((PolygonalMap2.Path)null);
						this.networkAI.usePathFind = false;
					}

					pathFindBehavior2.walkingOnTheSpot.reset(this.x, this.y);
					this.getDeferredMovement(tempVector2_2);
					float4 = pathFindBehavior2.calcSpeedMul(float1, float2, int1, tempVector2_2.getLength());
					NetworkPlayerAI networkPlayerAI = this.networkAI;
					float float5 = float4 * NetworkPlayerAI.controlQuality + 1.0F;
					networkPlayerAI = this.networkAI;
					float4 = float5 - NetworkPlayerAI.controlQuality;
					pathFindBehavior2.moveToPoint(float1, float2, float4);
					if (this.JustMoved) {
						this.JustMoved = this.m_isPlayerMoving = IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) > 0.2F || (int)this.z != (int)float3;
					} else {
						this.JustMoved = this.m_isPlayerMoving = IsoUtils.DistanceManhatten(float1, float2, this.x, this.y) > 1.0F || (int)this.z != (int)float3;
					}

					this.m_deltaX = 0.0F;
					this.m_deltaY = 0.0F;
				} else {
					if (!this.networkAI.usePathFind) {
						pathFindBehavior2.pathToLocationF(float1, float2, float3);
						pathFindBehavior2.walkingOnTheSpot.reset(this.x, this.y);
						pathFindBehavior2.setTargetT(int1);
						this.networkAI.usePathFind = true;
					}

					PathFindBehavior2.BehaviorResult behaviorResult = pathFindBehavior2.update();
					if (behaviorResult == PathFindBehavior2.BehaviorResult.Failed) {
						this.setPathFindIndex(-1);
						if (this.networkAI.forcePathFinder) {
							this.networkAI.forcePathFinder = false;
						} else if (NetworkTeleport.teleport(this, NetworkTeleport.Type.teleportation, float1, float2, (byte)((int)float3), 1.0F)) {
							DebugLog.General.warn(String.format("Player %d teleport from (%.2f, %.2f, %.2f) to (%.2f, %.2f %.2f)", this.getOnlineID(), this.x, this.y, this.z, float1, float2, float3));
						}
					} else if (behaviorResult == PathFindBehavior2.BehaviorResult.Succeeded) {
						int int2 = (int)pathFindBehavior2.getTargetX();
						int int3 = (int)pathFindBehavior2.getTargetY();
						IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int2 / 10, int3 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int2, int3, 0);
						if (chunk == null) {
							this.JustMoved = this.m_isPlayerMoving = true;
						} else {
							this.JustMoved = this.m_isPlayerMoving = true;
						}
					}

					this.m_deltaX = 0.0F;
					this.m_deltaY = 0.0F;
				}

				if (!this.m_isPlayerMoving || this.isAiming()) {
					if (this.strikes.isEmpty()) {
						this.DirectionFromVector(this.networkAI.direction);
						this.setForwardDirection(this.networkAI.direction);
					}

					tempo.set(float1 - this.nx, -(float2 - this.ny));
					tempo.normalize();
					float4 = this.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
					if ((double)float4 > 6.283185307179586) {
						float4 = (float)((double)float4 - 6.283185307179586);
					}

					if (float4 < 0.0F) {
						float4 = (float)((double)float4 + 6.283185307179586);
					}

					tempo.rotate(float4);
					tempo.setLength(Math.min(IsoUtils.DistanceTo(float1, float2, this.x, this.y), 1.0F));
					this.m_deltaX = tempo.x;
					this.m_deltaY = tempo.y;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean updateWhileDead() {
		if (GameServer.bServer) {
			return false;
		} else if (!this.isLocalPlayer()) {
			return false;
		} else if (!this.isDead()) {
			return false;
		} else {
			this.setVariable("bPathfind", false);
			this.setMoving(false);
			this.m_isPlayerMoving = false;
			if (this.getVehicle() != null) {
				this.getVehicle().exit(this);
			}

			if (this.heartEventInstance != 0L) {
				this.getEmitter().stopSound(this.heartEventInstance);
				this.heartEventInstance = 0L;
			}

			if (GameClient.bClient && !this.bRemote && !this.bSentDeath) {
				this.dropHandItems();
				GameClient.instance.sendDeath(this);
				ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(this);
				this.bSentDeath = true;
			}

			return true;
		}
	}

	private void initFMODParameters() {
		FMODParameterList fMODParameterList = this.getFMODParameters();
		fMODParameterList.add(this.parameterCharacterMovementSpeed);
		fMODParameterList.add(this.parameterFootstepMaterial);
		fMODParameterList.add(this.parameterFootstepMaterial2);
		fMODParameterList.add(this.parameterLocalPlayer);
		fMODParameterList.add(this.parameterMeleeHitSurface);
		fMODParameterList.add(this.parameterPlayerHealth);
		fMODParameterList.add(this.parameterShoeType);
		fMODParameterList.add(this.parameterVehicleHitLocation);
	}

	public ParameterCharacterMovementSpeed getParameterCharacterMovementSpeed() {
		return this.parameterCharacterMovementSpeed;
	}

	public void setMeleeHitSurface(ParameterMeleeHitSurface.Material material) {
		this.parameterMeleeHitSurface.setMaterial(material);
	}

	public void setMeleeHitSurface(String string) {
		try {
			this.parameterMeleeHitSurface.setMaterial(ParameterMeleeHitSurface.Material.valueOf(string));
		} catch (IllegalArgumentException illegalArgumentException) {
			this.parameterMeleeHitSurface.setMaterial(ParameterMeleeHitSurface.Material.Default);
		}
	}

	public void setVehicleHitLocation(BaseVehicle baseVehicle) {
		ParameterVehicleHitLocation.HitLocation hitLocation = ParameterVehicleHitLocation.calculateLocation(baseVehicle, this.getX(), this.getY(), this.getZ());
		this.parameterVehicleHitLocation.setLocation(hitLocation);
	}

	private void updateHeartSound() {
		if (!GameServer.bServer) {
			if (this.isLocalPlayer()) {
				GameSound gameSound = GameSounds.getSound("HeartBeat");
				boolean boolean1 = gameSound != null && gameSound.getUserVolume() > 0.0F && this.stats.Panic > 0.0F;
				if (!this.Asleep && boolean1 && GameTime.getInstance().getTrueMultiplier() == 1.0F) {
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
			}
		}
	}

	private void updateWorldAmbiance() {
		if (!GameServer.bServer) {
			if (this.isLocalPlayer()) {
				if (this.getPlayerNum() == 0 && (this.worldAmbianceInstance == 0L || !this.getEmitter().isPlaying(this.worldAmbianceInstance))) {
					this.worldAmbianceInstance = this.getEmitter().playSoundImpl("WorldAmbiance", (IsoObject)null);
					this.getEmitter().setVolume(this.worldAmbianceInstance, 1.0F);
				}
			}
		}
	}

	public void DoFootstepSound(String string) {
		ParameterCharacterMovementSpeed.MovementType movementType = ParameterCharacterMovementSpeed.MovementType.Walk;
		float float1 = 0.5F;
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
			float1 = 0.25F;
			movementType = ParameterCharacterMovementSpeed.MovementType.SneakWalk;
			break;
		
		case 1: 
			float1 = 0.25F;
			movementType = ParameterCharacterMovementSpeed.MovementType.SneakRun;
			break;
		
		case 2: 
			float1 = 0.5F;
			movementType = ParameterCharacterMovementSpeed.MovementType.Strafe;
			break;
		
		case 3: 
			float1 = 0.5F;
			movementType = ParameterCharacterMovementSpeed.MovementType.Walk;
			break;
		
		case 4: 
			float1 = 0.75F;
			movementType = ParameterCharacterMovementSpeed.MovementType.Run;
			break;
		
		case 5: 
			float1 = 1.0F;
			movementType = ParameterCharacterMovementSpeed.MovementType.Sprint;
		
		}
		this.parameterCharacterMovementSpeed.setMovementType(movementType);
		super.DoFootstepSound(float1);
	}

	private void updateHeavyBreathing() {
	}

	private void doServerFootstepSound() {
		if (!this.isInvisible()) {
			if (this.networkAI.moving) {
				if (this.footstepTimer.Check()) {
					byte byte1 = this.networkAI.footstepSoundRadius;
					WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), byte1, byte1, false);
				}
			}
		}
	}

	private void checkVehicleContainers() {
		ArrayList arrayList = this.vehicleContainerData.tempContainers;
		arrayList.clear();
		int int1 = (int)this.getX() - 4;
		int int2 = (int)this.getY() - 4;
		int int3 = (int)this.getX() + 4;
		int int4 = (int)this.getY() + 4;
		int int5 = int1 / 10;
		int int6 = int2 / 10;
		int int7 = (int)Math.ceil((double)((float)int3 / 10.0F));
		int int8 = (int)Math.ceil((double)((float)int4 / 10.0F));
		int int9;
		for (int9 = int6; int9 < int8; ++int9) {
			for (int int10 = int5; int10 < int7; ++int10) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int10, int9) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int10 * 10, int9 * 10, 0);
				if (chunk != null) {
					for (int int11 = 0; int11 < chunk.vehicles.size(); ++int11) {
						BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int11);
						VehicleScript vehicleScript = baseVehicle.getScript();
						if (vehicleScript != null) {
							for (int int12 = 0; int12 < vehicleScript.getPartCount(); ++int12) {
								VehicleScript.Part part = vehicleScript.getPart(int12);
								if (part.container != null && part.area != null && baseVehicle.isInArea(part.area, this)) {
									IsoPlayer.VehicleContainer vehicleContainer = this.vehicleContainerData.freeContainers.isEmpty() ? new IsoPlayer.VehicleContainer() : (IsoPlayer.VehicleContainer)this.vehicleContainerData.freeContainers.pop();
									arrayList.add(vehicleContainer.set(baseVehicle, int12));
								}
							}
						}
					}
				}
			}
		}

		if (arrayList.size() != this.vehicleContainerData.containers.size()) {
			this.vehicleContainerData.freeContainers.addAll(this.vehicleContainerData.containers);
			this.vehicleContainerData.containers.clear();
			this.vehicleContainerData.containers.addAll(arrayList);
			LuaEventManager.triggerEvent("OnContainerUpdate");
		} else {
			for (int9 = 0; int9 < arrayList.size(); ++int9) {
				IsoPlayer.VehicleContainer vehicleContainer2 = (IsoPlayer.VehicleContainer)arrayList.get(int9);
				IsoPlayer.VehicleContainer vehicleContainer3 = (IsoPlayer.VehicleContainer)this.vehicleContainerData.containers.get(int9);
				if (!vehicleContainer2.equals(vehicleContainer3)) {
					this.vehicleContainerData.freeContainers.addAll(this.vehicleContainerData.containers);
					this.vehicleContainerData.containers.clear();
					this.vehicleContainerData.containers.addAll(arrayList);
					LuaEventManager.triggerEvent("OnContainerUpdate");
					break;
				}
			}
		}
	}

	public void setJoypadIgnoreAimUntilCentered(boolean boolean1) {
		this.bJoypadIgnoreAimUntilCentered = boolean1;
	}

	public boolean canSeePlayerStats() {
		return this.accessLevel != "";
	}

	public ByteBufferWriter createPlayerStats(ByteBufferWriter byteBufferWriter, String string) {
		PacketTypes.doPacket((short)123, byteBufferWriter);
		byteBufferWriter.putInt(this.getOnlineID());
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(this.getDisplayName());
		byteBufferWriter.putUTF(this.getDescriptor().getForename());
		byteBufferWriter.putUTF(this.getDescriptor().getSurname());
		byteBufferWriter.putUTF(this.getDescriptor().getProfession());
		byteBufferWriter.putUTF(this.accessLevel);
		if (!StringUtils.isNullOrEmpty(this.getTagPrefix())) {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putUTF(this.getTagPrefix());
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		if (this.accessLevel.equals("")) {
			this.setGhostMode(false);
			this.setInvisible(false);
			this.setGodMod(false);
		}

		byteBufferWriter.putBoolean(this.isAllChatMuted());
		byteBufferWriter.putFloat(this.getTagColor().r);
		byteBufferWriter.putFloat(this.getTagColor().g);
		byteBufferWriter.putFloat(this.getTagColor().b);
		byteBufferWriter.putByte((byte)(this.showTag ? 1 : 0));
		byteBufferWriter.putByte((byte)(this.factionPvp ? 1 : 0));
		return byteBufferWriter;
	}

	public String setPlayerStats(ByteBuffer byteBuffer, String string) {
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		String string4 = GameWindow.ReadString(byteBuffer);
		String string5 = GameWindow.ReadString(byteBuffer);
		String string6 = GameWindow.ReadString(byteBuffer);
		String string7 = "";
		if (byteBuffer.get() == 1) {
			string7 = GameWindow.ReadString(byteBuffer);
		}

		boolean boolean1 = byteBuffer.get() == 1;
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		String string8 = "";
		this.setTagColor(new ColorInfo(float1, float2, float3, 1.0F));
		this.setTagPrefix(string7);
		this.showTag = byteBuffer.get() == 1;
		this.factionPvp = byteBuffer.get() == 1;
		if (!string3.equals(this.getDescriptor().getForename())) {
			if (GameServer.bServer) {
				string8 = string + " Changed " + string2 + " forname in " + string3;
			} else {
				string8 = "Changed your forname in " + string3;
			}
		}

		this.getDescriptor().setForename(string3);
		if (!string4.equals(this.getDescriptor().getSurname())) {
			if (GameServer.bServer) {
				string8 = string + " Changed " + string2 + " surname in " + string4;
			} else {
				string8 = "Changed your surname in " + string4;
			}
		}

		this.getDescriptor().setSurname(string4);
		if (!string5.equals(this.getDescriptor().getProfession())) {
			if (GameServer.bServer) {
				string8 = string + " Changed " + string2 + " profession to " + string5;
			} else {
				string8 = "Changed your profession in " + string5;
			}
		}

		this.getDescriptor().setProfession(string5);
		if (!this.accessLevel.equals(string6)) {
			if (GameServer.bServer) {
				string.makeConcatWithConstants(string, this.getDisplayName(), string6);
				try {
					ServerWorldDatabase.instance.setAccessLevel(this.username, string6);
				} catch (SQLException sQLException) {
					sQLException.printStackTrace();
				}
			} else if (GameClient.bClient && GameClient.username.equals(this.username)) {
				GameClient.accessLevel = string6;
				GameClient.connection.accessLevel = string6;
			}

			if (string6.equals("")) {
				this.setGhostMode(false);
				this.setInvisible(false);
				this.setGodMod(false);
			}

			string8 = "Changed access level to " + string6;
			this.accessLevel = string6;
		}

		if (!this.getDisplayName().equals(string2)) {
			if (GameServer.bServer) {
				string8 = string + " Changed display name \"" + this.getDisplayName() + "\" to \"" + string2 + "\"";
				ServerWorldDatabase.instance.updateDisplayName(this.username, string2);
			} else {
				string8 = "Changed your display name to " + string2;
			}

			this.setDisplayName(string2);
		}

		if (boolean1 != this.isAllChatMuted()) {
			if (boolean1) {
				if (GameServer.bServer) {
					string8 = string + " Banned " + string2 + " from using /all chat";
				} else {
					string8 = "Banned you from using /all chat";
				}
			} else if (GameServer.bServer) {
				string8 = string + " Allowed " + string2 + " to use /all chat";
			} else {
				string8 = "Now allowed you to use /all chat";
			}
		}

		this.setAllChatMuted(boolean1);
		if (GameServer.bServer && !"".equals(string8)) {
			LoggerManager.getLogger("admin").write(string8);
		}

		if (GameClient.bClient) {
			LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
		}

		return string8;
	}

	public boolean isAllChatMuted() {
		return this.allChatMuted;
	}

	public void setAllChatMuted(boolean boolean1) {
		this.allChatMuted = boolean1;
	}

	public String getAccessLevel() {
		String string = this.accessLevel;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -2004703995: 
			if (string.equals("moderator")) {
				byte1 = 1;
			}

			break;
		
		case 3302: 
			if (string.equals("gm")) {
				byte1 = 3;
			}

			break;
		
		case 92668751: 
			if (string.equals("admin")) {
				byte1 = 0;
			}

			break;
		
		case 348607190: 
			if (string.equals("observer")) {
				byte1 = 4;
			}

			break;
		
		case 530022739: 
			if (string.equals("overseer")) {
				byte1 = 2;
			}

		
		}
		switch (byte1) {
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

	public void setAccessLevel(String string) {
		this.accessLevel = string;
	}

	public void addMechanicsItem(String string, VehiclePart vehiclePart, Long Long1) {
		byte byte1 = 1;
		byte byte2 = 1;
		if (this.mechanicsItem.get(Long.parseLong(string)) == null) {
			if (vehiclePart.getTable("uninstall") != null && vehiclePart.getTable("uninstall").rawget("skills") != null) {
				String[] stringArray = ((String)vehiclePart.getTable("uninstall").rawget("skills")).split(";");
				String[] stringArray2 = stringArray;
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string2 = stringArray2[int2];
					if (string2.contains("Mechanics")) {
						int int3 = Integer.parseInt(string2.split(":")[1]);
						if (int3 >= 6) {
							byte1 = 3;
							byte2 = 7;
						} else if (int3 >= 4) {
							byte1 = 3;
							byte2 = 5;
						} else if (int3 >= 2) {
							byte1 = 2;
							byte2 = 4;
						} else if (Rand.Next(3) == 0) {
							byte1 = 2;
							byte2 = 2;
						}
					}
				}
			}

			this.getXp().AddXP(PerkFactory.Perks.Mechanics, (float)Rand.Next(byte1, byte2));
		}

		this.mechanicsItem.put(Long.parseLong(string), Long1);
	}

	public void setPosition(float float1, float float2, float float3) {
		this.setX(float1);
		this.setY(float2);
		this.setZ(float3);
	}

	private void updateTemperatureCheck() {
		int int1 = this.Moodles.getMoodleLevel(MoodleType.Hypothermia);
		if (this.hypothermiaCache == -1 || this.hypothermiaCache != int1) {
			if (int1 >= 3 && int1 > this.hypothermiaCache && this.isAsleep() && !this.ForceWakeUp) {
				this.forceAwake();
			}

			this.hypothermiaCache = int1;
		}

		int int2 = this.Moodles.getMoodleLevel(MoodleType.Hyperthermia);
		if (this.hyperthermiaCache == -1 || this.hyperthermiaCache != int2) {
			if (int2 >= 3 && int2 > this.hyperthermiaCache && this.isAsleep() && !this.ForceWakeUp) {
				this.forceAwake();
			}

			this.hyperthermiaCache = int2;
		}
	}

	public float getZombieRelevenceScore(IsoZombie zombie) {
		if (zombie.getCurrentSquare() == null) {
			return -10000.0F;
		} else {
			float float1 = 0.0F;
			if (zombie.getCurrentSquare().getCanSee(this.PlayerIndex)) {
				float1 += 100.0F;
			} else if (zombie.getCurrentSquare().isCouldSee(this.PlayerIndex)) {
				float1 += 10.0F;
			}

			if (zombie.getCurrentSquare().getRoom() != null && this.current.getRoom() == null) {
				float1 -= 20.0F;
			}

			if (zombie.getCurrentSquare().getRoom() == null && this.current.getRoom() != null) {
				float1 -= 20.0F;
			}

			if (zombie.getCurrentSquare().getRoom() != this.current.getRoom()) {
				float1 -= 20.0F;
			}

			float float2 = zombie.DistTo(this);
			float1 -= float2;
			if (float2 < 20.0F) {
				float1 += 300.0F;
			}

			if (float2 < 15.0F) {
				float1 += 300.0F;
			}

			if (float2 < 10.0F) {
				float1 += 1000.0F;
			}

			if (zombie.getTargetAlpha() < 1.0F && float1 > 0.0F) {
				float1 *= zombie.getTargetAlpha();
			}

			return float1;
		}
	}

	public BaseVisual getVisual() {
		return this.humanVisual;
	}

	public HumanVisual getHumanVisual() {
		return this.humanVisual;
	}

	public ItemVisuals getItemVisuals() {
		return this.itemVisuals;
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		if (!this.bRemote) {
			this.getWornItems().getItemVisuals(itemVisuals);
		} else {
			itemVisuals.clear();
			itemVisuals.addAll(this.itemVisuals);
		}
	}

	public void dressInNamedOutfit(String string) {
		this.getHumanVisual().dressInNamedOutfit(string, this.itemVisuals);
		this.onClothingOutfitPreviewChanged();
	}

	public void dressInClothingItem(String string) {
		this.getHumanVisual().dressInClothingItem(string, this.itemVisuals);
		this.onClothingOutfitPreviewChanged();
	}

	private void onClothingOutfitPreviewChanged() {
		if (this.isLocalPlayer()) {
			this.getInventory().clear();
			this.wornItems.setFromItemVisuals(this.itemVisuals);
			this.wornItems.addItemsToItemContainer(this.getInventory());
			this.itemVisuals.clear();
			this.resetModel();
			this.onWornItemsChanged();
		}
	}

	public void onWornItemsChanged() {
		this.parameterShoeType.setShoeType((ParameterShoeType.ShoeType)null);
	}

	public void actionStateChanged(ActionContext actionContext) {
		super.actionStateChanged(actionContext);
	}

	public Vector2 getLastAngle() {
		return this.lastAngle;
	}

	public void setLastAngle(Vector2 vector2) {
		this.lastAngle.set(vector2);
	}

	public int getDialogMood() {
		return this.DialogMood;
	}

	public void setDialogMood(int int1) {
		this.DialogMood = int1;
	}

	public int getPing() {
		return this.ping;
	}

	public void setPing(int int1) {
		this.ping = int1;
	}

	public IsoMovingObject getDragObject() {
		return this.DragObject;
	}

	public void setDragObject(IsoMovingObject movingObject) {
		this.DragObject = movingObject;
	}

	public float getAsleepTime() {
		return this.AsleepTime;
	}

	public void setAsleepTime(float float1) {
		this.AsleepTime = float1;
	}

	public Stack getSpottedList() {
		return this.spottedList;
	}

	public int getTicksSinceSeenZombie() {
		return this.TicksSinceSeenZombie;
	}

	public void setTicksSinceSeenZombie(int int1) {
		this.TicksSinceSeenZombie = int1;
	}

	public boolean isWaiting() {
		return this.Waiting;
	}

	public void setWaiting(boolean boolean1) {
		this.Waiting = boolean1;
	}

	public IsoSurvivor getDragCharacter() {
		return this.DragCharacter;
	}

	public void setDragCharacter(IsoSurvivor survivor) {
		this.DragCharacter = survivor;
	}

	public float getHeartDelay() {
		return this.heartDelay;
	}

	public void setHeartDelay(float float1) {
		this.heartDelay = float1;
	}

	public float getHeartDelayMax() {
		return this.heartDelayMax;
	}

	public void setHeartDelayMax(int int1) {
		this.heartDelayMax = (float)int1;
	}

	public double getHoursSurvived() {
		return this.HoursSurvived;
	}

	public void setHoursSurvived(double double1) {
		this.HoursSurvived = double1;
	}

	public float getDrunkOscilatorStepSin() {
		return this.DrunkOscilatorStepSin;
	}

	public void setDrunkOscilatorStepSin(float float1) {
		this.DrunkOscilatorStepSin = float1;
	}

	public float getDrunkOscilatorRateSin() {
		return this.DrunkOscilatorRateSin;
	}

	public void setDrunkOscilatorRateSin(float float1) {
		this.DrunkOscilatorRateSin = float1;
	}

	public float getDrunkOscilatorStepCos() {
		return this.DrunkOscilatorStepCos;
	}

	public void setDrunkOscilatorStepCos(float float1) {
		this.DrunkOscilatorStepCos = float1;
	}

	public float getDrunkOscilatorRateCos() {
		return this.DrunkOscilatorRateCos;
	}

	public void setDrunkOscilatorRateCos(float float1) {
		this.DrunkOscilatorRateCos = float1;
	}

	public float getDrunkOscilatorStepCos2() {
		return this.DrunkOscilatorStepCos2;
	}

	public void setDrunkOscilatorStepCos2(float float1) {
		this.DrunkOscilatorStepCos2 = float1;
	}

	public float getDrunkOscilatorRateCos2() {
		return this.DrunkOscilatorRateCos2;
	}

	public void setDrunkOscilatorRateCos2(float float1) {
		this.DrunkOscilatorRateCos2 = float1;
	}

	public float getDrunkSin() {
		return this.DrunkSin;
	}

	public void setDrunkSin(float float1) {
		this.DrunkSin = float1;
	}

	public float getDrunkCos() {
		return this.DrunkCos;
	}

	public void setDrunkCos(float float1) {
		this.DrunkCos = float1;
	}

	public float getDrunkCos2() {
		return this.DrunkCos2;
	}

	public void setDrunkCos2(float float1) {
		this.DrunkCos2 = float1;
	}

	public float getMinOscilatorRate() {
		return this.MinOscilatorRate;
	}

	public void setMinOscilatorRate(float float1) {
		this.MinOscilatorRate = float1;
	}

	public float getMaxOscilatorRate() {
		return this.MaxOscilatorRate;
	}

	public void setMaxOscilatorRate(float float1) {
		this.MaxOscilatorRate = float1;
	}

	public float getDesiredSinRate() {
		return this.DesiredSinRate;
	}

	public void setDesiredSinRate(float float1) {
		this.DesiredSinRate = float1;
	}

	public float getDesiredCosRate() {
		return this.DesiredCosRate;
	}

	public void setDesiredCosRate(float float1) {
		this.DesiredCosRate = float1;
	}

	public float getOscilatorChangeRate() {
		return this.OscilatorChangeRate;
	}

	public void setOscilatorChangeRate(float float1) {
		this.OscilatorChangeRate = float1;
	}

	public float getMaxWeightDelta() {
		return this.maxWeightDelta;
	}

	public void setMaxWeightDelta(float float1) {
		this.maxWeightDelta = float1;
	}

	public String getForname() {
		return this.Forname;
	}

	public void setForname(String string) {
		this.Forname = string;
	}

	public String getSurname() {
		return this.Surname;
	}

	public void setSurname(String string) {
		this.Surname = string;
	}

	public boolean isbChangeCharacterDebounce() {
		return this.bChangeCharacterDebounce;
	}

	public void setbChangeCharacterDebounce(boolean boolean1) {
		this.bChangeCharacterDebounce = boolean1;
	}

	public int getFollowID() {
		return this.followID;
	}

	public void setFollowID(int int1) {
		this.followID = int1;
	}

	public boolean isbSeenThisFrame() {
		return this.bSeenThisFrame;
	}

	public void setbSeenThisFrame(boolean boolean1) {
		this.bSeenThisFrame = boolean1;
	}

	public boolean isbCouldBeSeenThisFrame() {
		return this.bCouldBeSeenThisFrame;
	}

	public void setbCouldBeSeenThisFrame(boolean boolean1) {
		this.bCouldBeSeenThisFrame = boolean1;
	}

	public float getTimeSinceLastStab() {
		return this.timeSinceLastStab;
	}

	public void setTimeSinceLastStab(float float1) {
		this.timeSinceLastStab = float1;
	}

	public Stack getLastSpotted() {
		return this.LastSpotted;
	}

	public void setLastSpotted(Stack stack) {
		this.LastSpotted = stack;
	}

	public int getClearSpottedTimer() {
		return this.ClearSpottedTimer;
	}

	public void setClearSpottedTimer(int int1) {
		this.ClearSpottedTimer = int1;
	}

	public boolean IsRunning() {
		return this.isRunning();
	}

	public void InitSpriteParts() {
	}

	public boolean IsAiming() {
		return this.isAiming();
	}

	public String getTagPrefix() {
		return this.tagPrefix;
	}

	public void setTagPrefix(String string) {
		this.tagPrefix = string;
	}

	public ColorInfo getTagColor() {
		return this.tagColor;
	}

	public void setTagColor(ColorInfo colorInfo) {
		this.tagColor.set(colorInfo);
	}

	public Integer getTransactionID() {
		return this.transactionID;
	}

	public void setTransactionID(Integer integer) {
		this.transactionID = integer;
	}

	public String getDisplayName() {
		if (GameClient.bClient) {
			if (this.displayName == null || this.displayName.equals("")) {
				this.displayName = this.getUsername();
			}
		} else if (!GameServer.bServer) {
			this.displayName = this.getUsername();
		}

		return this.displayName;
	}

	public void setDisplayName(String string) {
		this.displayName = string;
	}

	public boolean isSeeNonPvpZone() {
		return this.seeNonPvpZone;
	}

	public void setSeeNonPvpZone(boolean boolean1) {
		this.seeNonPvpZone = boolean1;
	}

	public boolean isShowTag() {
		return this.showTag;
	}

	public void setShowTag(boolean boolean1) {
		this.showTag = boolean1;
	}

	public boolean isFactionPvp() {
		return this.factionPvp;
	}

	public void setFactionPvp(boolean boolean1) {
		this.factionPvp = boolean1;
	}

	public boolean isForceAim() {
		return this.forceAim;
	}

	public void setForceAim(boolean boolean1) {
		this.forceAim = boolean1;
	}

	public boolean toggleForceAim() {
		this.forceAim = !this.forceAim;
		return this.forceAim;
	}

	public boolean isForceSprint() {
		return this.forceSprint;
	}

	public void setForceSprint(boolean boolean1) {
		this.forceSprint = boolean1;
	}

	public boolean toggleForceSprint() {
		this.forceSprint = !this.forceSprint;
		return this.forceSprint;
	}

	public boolean isForceRun() {
		return this.forceRun;
	}

	public void setForceRun(boolean boolean1) {
		this.forceRun = boolean1;
	}

	public boolean toggleForceRun() {
		this.forceRun = !this.forceRun;
		return this.forceRun;
	}

	public boolean isDeaf() {
		return this.Traits.Deaf.isSet();
	}

	public boolean isForceOverrideAnim() {
		return this.forceOverrideAnim;
	}

	public void setForceOverrideAnim(boolean boolean1) {
		this.forceOverrideAnim = boolean1;
	}

	public Long getMechanicsItem(String string) {
		return (Long)this.mechanicsItem.get(Long.parseLong(string));
	}

	public boolean isWearingNightVisionGoggles() {
		return this.isWearingNightVisionGoggles;
	}

	public void setWearingNightVisionGoggles(boolean boolean1) {
		this.isWearingNightVisionGoggles = boolean1;
	}

	public void OnAnimEvent(AnimLayer animLayer, AnimEvent animEvent) {
		super.OnAnimEvent(animLayer, animEvent);
		if (!this.CharacterActions.isEmpty()) {
			BaseAction baseAction = (BaseAction)this.CharacterActions.get(0);
			baseAction.OnAnimEvent(animEvent);
		}
	}

	public void onCullStateChanged(ModelManager modelManager, boolean boolean1) {
		super.onCullStateChanged(modelManager, boolean1);
		if (!boolean1) {
			DebugFileWatcher.instance.add(this.m_setClothingTriggerWatcher);
		} else {
			DebugFileWatcher.instance.remove(this.m_setClothingTriggerWatcher);
		}
	}

	public boolean isTimedActionInstant() {
		return (GameClient.bClient || GameServer.bServer) && "None".equals(this.getAccessLevel()) ? false : super.isTimedActionInstant();
	}

	public boolean isSkeleton() {
		return false;
	}

	public void addWorldSoundUnlessInvisible(int int1, int int2, boolean boolean1) {
		if (!this.isGhostMode()) {
			super.addWorldSoundUnlessInvisible(int1, int2, boolean1);
		}
	}

	private void updateFootInjuries() {
		InventoryItem inventoryItem = this.getWornItems().getItem("Shoes");
		if (inventoryItem == null || inventoryItem.getCondition() <= 0) {
			if (this.getCurrentSquare() != null) {
				if (this.getCurrentSquare().getBrokenGlass() != null) {
					BodyPartType bodyPartType = BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Foot_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1));
					BodyPart bodyPart = this.getBodyDamage().getBodyPart(bodyPartType);
					bodyPart.generateDeepShardWound();
				}

				byte byte1 = 0;
				boolean boolean1 = false;
				if (this.getCurrentSquare().getZone() != null && (this.getCurrentSquare().getZone().getType().equals("Forest") || this.getCurrentSquare().getZone().getType().equals("DeepForest"))) {
					boolean1 = true;
				}

				IsoObject object = this.getCurrentSquare().getFloor();
				if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
					String string = object.getSprite().getName();
					if (string.contains("blends_natural_01") && boolean1) {
						byte1 = 2;
					} else if (!string.contains("blends_natural_01") && this.getCurrentSquare().getBuilding() == null) {
						byte1 = 1;
					}
				}

				if (byte1 != 0) {
					if (this.isWalking && !this.isRunning() && !this.isSprinting()) {
						this.footInjuryTimer += byte1;
					} else if (this.isRunning() && !this.isSprinting()) {
						this.footInjuryTimer += byte1 + 2;
					} else {
						if (!this.isSprinting()) {
							if (this.footInjuryTimer > 0 && Rand.Next(3) == 0) {
								--this.footInjuryTimer;
							}

							return;
						}

						this.footInjuryTimer += byte1 + 5;
					}

					if (Rand.Next(Rand.AdjustForFramerate(8500 - this.footInjuryTimer)) <= 0) {
						this.footInjuryTimer = 0;
						BodyPartType bodyPartType2 = BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Foot_L), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1));
						BodyPart bodyPart2 = this.getBodyDamage().getBodyPart(bodyPartType2);
						if (bodyPart2.getScratchTime() > 30.0F) {
							if (!bodyPart2.isCut()) {
								bodyPart2.setCut(true);
								bodyPart2.setCutTime(Rand.Next(1.0F, 3.0F));
							} else {
								bodyPart2.setCutTime(bodyPart2.getCutTime() + Rand.Next(1.0F, 3.0F));
							}
						} else {
							if (!bodyPart2.scratched()) {
								bodyPart2.setScratched(true, true);
								bodyPart2.setScratchTime(Rand.Next(1.0F, 3.0F));
							} else {
								bodyPart2.setScratchTime(bodyPart2.getScratchTime() + Rand.Next(1.0F, 3.0F));
							}

							if (bodyPart2.getScratchTime() > 20.0F && bodyPart2.getBleedingTime() == 0.0F) {
								bodyPart2.setBleedingTime(Rand.Next(3.0F, 10.0F));
							}
						}
					}
				}
			}
		}
	}

	public int getMoodleLevel(MoodleType moodleType) {
		return this.getMoodles().getMoodleLevel(moodleType);
	}

	public boolean isAttackStarted() {
		return this.attackStarted;
	}

	public boolean isBehaviourMoving() {
		return this.hasPath() || super.isBehaviourMoving();
	}

	public boolean isPlayerMoving() {
		return this.m_isPlayerMoving;
	}

	public float getTimedActionTimeModifier() {
		return this.getBodyDamage().getThermoregulator() != null ? this.getBodyDamage().getThermoregulator().getTimedActionTimeModifier() : 1.0F;
	}

	public boolean isLookingWhileInVehicle() {
		return this.getVehicle() != null && this.bLookingWhileInVehicle;
	}

	public void setInitiateAttack(boolean boolean1) {
		this.initiateAttack = boolean1;
	}

	public boolean isIgnoreInputsForDirection() {
		return this.ignoreInputsForDirection;
	}

	public void setIgnoreInputsForDirection(boolean boolean1) {
		this.ignoreInputsForDirection = boolean1;
	}

	public boolean isIgnoreContextKey() {
		return this.ignoreContextKey;
	}

	public void setIgnoreContextKey(boolean boolean1) {
		this.ignoreContextKey = boolean1;
	}

	public boolean isIgnoreAutoVault() {
		return this.ignoreAutoVault;
	}

	public void setIgnoreAutoVault(boolean boolean1) {
		this.ignoreAutoVault = boolean1;
	}

	public boolean isAllowSprint() {
		return this.allowSprint;
	}

	public void setAllowSprint(boolean boolean1) {
		this.allowSprint = boolean1;
	}

	public boolean isAllowRun() {
		return this.allowRun;
	}

	public void setAllowRun(boolean boolean1) {
		this.allowRun = boolean1;
	}

	public String getAttackType() {
		return this.attackType;
	}

	public void setAttackType(String string) {
		this.attackType = string;
	}

	public void clearNetworkEvents() {
		this.networkAI.events.clear();
		this.clearVariable("PerformingAction");
		this.clearVariable("IsPerformingAnAction");
		this.overridePrimaryHandModel = null;
		this.overrideSecondaryHandModel = null;
		this.resetModelNextFrame();
	}

	public void setM_bKnockedDown(boolean boolean1) {
		this.m_bKnockedDown = boolean1;
	}

	public boolean isCanSeeAll() {
		return this.canSeeAll;
	}

	public void setCanSeeAll(boolean boolean1) {
		this.canSeeAll = boolean1;
	}

	public boolean isCanHearAll() {
		return this.canHearAll;
	}

	public void setCanHearAll(boolean boolean1) {
		this.canHearAll = boolean1;
	}

	public ArrayList getAlreadyReadBook() {
		return this.alreadyReadBook;
	}

	public void setMoodleCantSprint(boolean boolean1) {
		this.MoodleCantSprint = boolean1;
	}

	static  {
		m_isoPlayerTriggerWatcher = new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_ResetIsoPlayerModel.xml"), IsoPlayer::onTrigger_ResetIsoPlayerModel);
		tempVector2_1 = new Vector2();
		tempVector2_2 = new Vector2();
		tempVector3f = new Vector3f();
		s_moveVars = new IsoPlayer.MoveVars();
		s_targetsProne = new ArrayList();
		s_targetsStanding = new ArrayList();
	}

	private static class InputState {
		public boolean bMelee;
		public boolean isAttacking;
		public boolean bRunning;
		public boolean bSprinting;
		boolean isAiming;
		boolean isCharging;
		boolean isChargingLT;
	}

	private static class VehicleContainerData {
		ArrayList tempContainers = new ArrayList();
		ArrayList containers = new ArrayList();
		Stack freeContainers = new Stack();
	}

	private static class s_performance {
		static final PerformanceProfileProbe postUpdate = new PerformanceProfileProbe("IsoPlayer.postUpdate");
		static final PerformanceProfileProbe highlightRangedTargets = new PerformanceProfileProbe("IsoPlayer.highlightRangedTargets");
		static final PerformanceProfileProbe update = new PerformanceProfileProbe("IsoPlayer.update");
	}

	static final class MoveVars {
		float moveX;
		float moveY;
		float strafeX;
		float strafeY;
		IsoDirections NewFacing;
	}

	private static class VehicleContainer {
		BaseVehicle vehicle;
		int containerIndex;

		public IsoPlayer.VehicleContainer set(BaseVehicle baseVehicle, int int1) {
			this.vehicle = baseVehicle;
			this.containerIndex = int1;
			return this;
		}

		public boolean equals(Object object) {
			return object instanceof IsoPlayer.VehicleContainer && this.vehicle == ((IsoPlayer.VehicleContainer)object).vehicle && this.containerIndex == ((IsoPlayer.VehicleContainer)object).containerIndex;
		}
	}
}
