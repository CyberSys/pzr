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
	private double HoursSurvived = 0.0;
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
	double lastSeenZombieTime = 2.0;
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

	public void TestZombieSpotPlayer(IsoMovingObject movingObject) {
		movingObject.spotted(this, false);
		if (movingObject instanceof IsoZombie) {
			float float1 = movingObject.DistTo(this);
			if (float1 < this.closestZombie && !((IsoZombie)movingObject).isOnFloor()) {
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

	public void setGhostMode(boolean boolean1) {
		this.GhostMode = boolean1;
	}

	public static IsoPlayer getInstance() {
		return instance;
	}

	public static void setInstance(IsoPlayer player) {
		instance = player;
	}

	public Vector2 getPlayerMoveDir() {
		return this.playerMoveDir;
	}

	public void setPlayerMoveDir(Vector2 vector2) {
		this.playerMoveDir = vector2;
	}

	public boolean isIsAiming() {
		return this.isAiming;
	}

	public boolean isAiming() {
		return this.isAiming;
	}

	public void setIsAiming(boolean boolean1) {
		this.isAiming = boolean1;
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

	public static Stack getStaticTraits() {
		return StaticTraits;
	}

	public static void setStaticTraits(Stack stack) {
		StaticTraits = stack;
	}

	public static int getFollowDeadCount() {
		return FollowDeadCount;
	}

	public static void setFollowDeadCount(int int1) {
		FollowDeadCount = int1;
	}

	public IsoPlayer(IsoCell cell) {
		super(cell, 0.0F, 0.0F, 0.0F);
		this.bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
		if (Core.bDebug) {
		}

		this.GuardModeUISprite = new IsoSprite(this.getCell().SpriteManager);
		this.GuardModeUISprite.LoadFrameExplicit("TileFloorInt_0");
		for (int int1 = 0; int1 < StaticTraits.size(); ++int1) {
			this.Traits.add(StaticTraits.get(int1));
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

	public IsoPlayer(IsoCell cell, SurvivorDesc survivorDesc, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
		for (int int4 = 0; int4 < StaticTraits.size(); ++int4) {
			this.Traits.add(StaticTraits.get(int4));
		}

		StaticTraits.clear();
		this.dir = IsoDirections.W;
		this.nutrition = new Nutrition(this);
		this.descriptor = new SurvivorDesc();
		this.PathSpeed = 0.08F;
		this.bFemale = survivorDesc.isFemale();
		this.Dressup(survivorDesc);
		this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
		this.descriptor = survivorDesc;
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
				} catch (IOException ioException) {
					ioException.printStackTrace();
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

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		byteBuffer.get();
		byteBuffer.getInt();
		IsoPlayer player = instance;
		instance = this;
		try {
			super.load(byteBuffer, int1);
		} finally {
			instance = player;
		}

		this.setHoursSurvived(byteBuffer.getDouble());
		if (int1 < 142 && this.getBodyDamage().getInfectionTime() > 0.0F) {
			float float1 = (float)Math.max(GameTime.getInstance().getWorldAgeHours() - (double)this.getBodyDamage().getInfectionTime(), 0.0);
			this.getBodyDamage().setInfectionTime((float)Math.max(this.getHoursSurvived() - (double)float1, 0.0));
		}

		SurvivorDesc survivorDesc = this.descriptor;
		this.bFemale = survivorDesc.isFemale();
		this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
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
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		this.PathSpeed = 0.07F;
		this.setZombieKills(byteBuffer.getInt());
		String string;
		String string2;
		if (byteBuffer.getInt() == 1) {
			string = GameWindow.ReadString(byteBuffer);
			string2 = GameWindow.ReadString(byteBuffer);
			this.SetClothing(Item.ClothingBodyLocation.Top, string2, string);
			this.topSprite.TintMod.r = byteBuffer.getFloat();
			this.topSprite.TintMod.g = byteBuffer.getFloat();
			this.topSprite.TintMod.b = byteBuffer.getFloat();
		}

		if (byteBuffer.getInt() == 1) {
			string = GameWindow.ReadString(byteBuffer);
			string2 = GameWindow.ReadString(byteBuffer);
			this.SetClothing(Item.ClothingBodyLocation.Shoes, string2, string);
		}

		if (byteBuffer.getInt() == 1) {
			string = GameWindow.ReadString(byteBuffer);
			string2 = GameWindow.ReadString(byteBuffer);
		}

		if (byteBuffer.getInt() == 1) {
			string = GameWindow.ReadString(byteBuffer);
			string2 = GameWindow.ReadString(byteBuffer);
			this.SetClothing(Item.ClothingBodyLocation.Bottoms, string2, string);
			this.bottomsSprite.TintMod.r = byteBuffer.getFloat();
			this.bottomsSprite.TintMod.g = byteBuffer.getFloat();
			this.bottomsSprite.TintMod.b = byteBuffer.getFloat();
		}

		if (int1 >= 46) {
			ArrayList arrayList = this.savedInventoryItems;
			short short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size()) {
				this.ClothingItem_Torso = (InventoryItem)arrayList.get(short1);
			}

			short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size()) {
				this.ClothingItem_Legs = (InventoryItem)arrayList.get(short1);
			}

			short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size()) {
				this.ClothingItem_Feet = (InventoryItem)arrayList.get(short1);
			}

			short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size()) {
				this.ClothingItem_Back = (InventoryItem)arrayList.get(short1);
			}

			short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size()) {
				this.leftHandItem = (InventoryItem)arrayList.get(short1);
			}

			short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size()) {
				this.rightHandItem = (InventoryItem)arrayList.get(short1);
			}
		}

		this.setSurvivorKills(byteBuffer.getInt());
		int int2 = byteBuffer.getInt();
		this.initSpritePartsEmpty();
		if (int1 < 57) {
			this.createKeyRing();
		}

		if (int1 >= 81) {
			this.nutrition.load(byteBuffer);
		}

		if (int1 >= 99) {
			this.setAllChatMuted(byteBuffer.get() == 1);
			this.tagPrefix = GameWindow.ReadString(byteBuffer);
			this.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		}

		if (int1 >= 100 && GameClient.bClient && !ServerOptions.instance.Open.getValue() && ServerOptions.instance.SaveTransactionID.getValue()) {
			this.setTransactionID(byteBuffer.getInt());
		} else if (int1 >= 104) {
			this.setTransactionID(byteBuffer.getInt());
		}

		if (int1 >= 100) {
			this.setDisplayName(GameWindow.ReadString(byteBuffer));
		}

		if (int1 >= 103) {
			this.showTag = byteBuffer.get() == 1;
			this.factionPvp = byteBuffer.get() == 1;
		}

		if (int1 >= 91 && byteBuffer.get() == 1) {
			this.savedVehicleX = byteBuffer.getFloat();
			this.savedVehicleY = byteBuffer.getFloat();
			this.savedVehicleSeat = (short)byteBuffer.get();
			this.savedVehicleRunning = byteBuffer.get() == 1;
		}

		if (int1 >= 120) {
			int int3 = byteBuffer.getInt();
			for (int int4 = 0; int4 < int3; ++int4) {
				this.mechanicsItem.put(byteBuffer.getLong(), byteBuffer.getLong());
			}
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		IsoPlayer player = instance;
		instance = this;
		try {
			super.save(byteBuffer);
		} finally {
			instance = player;
		}

		byteBuffer.putDouble(this.getHoursSurvived());
		byteBuffer.putInt(this.getZombieKills());
		if (this.getClothingItem_Torso() != null && this.topSprite != null) {
			byteBuffer.putInt(1);
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Torso()).getPalette());
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Torso()).getSpriteName());
			byteBuffer.putFloat(this.topSprite.TintMod.r);
			byteBuffer.putFloat(this.topSprite.TintMod.g);
			byteBuffer.putFloat(this.topSprite.TintMod.b);
		} else {
			byteBuffer.putInt(0);
		}

		if (this.getClothingItem_Feet() != null) {
			byteBuffer.putInt(1);
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Feet()).getPalette());
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Feet()).getSpriteName());
		} else {
			byteBuffer.putInt(0);
		}

		if (this.getClothingItem_Hands() != null) {
			byteBuffer.putInt(1);
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Hands()).getPalette());
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Hands()).getSpriteName());
		} else {
			byteBuffer.putInt(0);
		}

		if (this.getClothingItem_Legs() != null && this.bottomsSprite != null) {
			byteBuffer.putInt(1);
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Legs()).getPalette());
			GameWindow.WriteString(byteBuffer, ((Clothing)this.getClothingItem_Legs()).getSpriteName());
			byteBuffer.putFloat(this.bottomsSprite.TintMod.r);
			byteBuffer.putFloat(this.bottomsSprite.TintMod.g);
			byteBuffer.putFloat(this.bottomsSprite.TintMod.b);
		} else {
			byteBuffer.putInt(0);
		}

		byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Torso()));
		byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Legs()));
		byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Feet()));
		byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getClothingItem_Back()));
		byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getPrimaryHandItem()));
		byteBuffer.putShort((short)this.savedInventoryItems.indexOf(this.getSecondaryHandItem()));
		byteBuffer.putInt(this.getSurvivorKills());
		byteBuffer.putInt(this.PlayerIndex);
		this.nutrition.save(byteBuffer);
		byteBuffer.put((byte)(this.isAllChatMuted() ? 1 : 0));
		GameWindow.WriteString(byteBuffer, this.tagPrefix);
		byteBuffer.putFloat(this.getTagColor().r);
		byteBuffer.putFloat(this.getTagColor().g);
		byteBuffer.putFloat(this.getTagColor().b);
		byteBuffer.putInt(this.transactionID);
		GameWindow.WriteString(byteBuffer, this.displayName);
		byteBuffer.put((byte)(this.showTag ? 1 : 0));
		byteBuffer.put((byte)(this.factionPvp ? 1 : 0));
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
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
		if (!Core.getInstance().isNoSave()) {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			Throwable throwable = null;
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				Throwable throwable2 = null;
				try {
					bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
				} catch (Throwable throwable3) {
					throwable2 = throwable3;
					throw throwable3;
				} finally {
					if (bufferedOutputStream != null) {
						if (throwable2 != null) {
							try {
								bufferedOutputStream.close();
							} catch (Throwable throwable4) {
								throwable2.addSuppressed(throwable4);
							}
						} else {
							bufferedOutputStream.close();
						}
					}
				}
			} catch (Throwable throwable5) {
				throwable = throwable5;
				throw throwable5;
			} finally {
				if (fileOutputStream != null) {
					if (throwable != null) {
						try {
							fileOutputStream.close();
						} catch (Throwable throwable6) {
							throwable.addSuppressed(throwable6);
						}
					} else {
						fileOutputStream.close();
					}
				}
			}
		}
	}

	public void save(String string) throws IOException {
		this.SaveFileName = string;
		File file = new File(string);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		SliceY.SliceBuffer.rewind();
		SliceY.SliceBuffer.putInt(143);
		GameWindow.WriteString(SliceY.SliceBuffer, this.bMultiplayer ? ServerOptions.instance.ServerPlayerID.getValue() : "");
		this.save(SliceY.SliceBuffer);
		bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
	}

	public void load(String string) throws FileNotFoundException, IOException {
		File file = new File(string);
		if (file.exists()) {
			this.SaveFileName = string;
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			synchronized (SliceY.SliceBuffer) {
				SliceY.SliceBuffer.rewind();
				bufferedInputStream.read(SliceY.SliceBuffer.array());
				int int1 = SliceY.SliceBuffer.getInt();
				if (int1 >= 69) {
					this.SaveFileIP = GameWindow.ReadStringUTF(SliceY.SliceBuffer);
					if (int1 < 71) {
						this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
					}
				} else if (GameClient.bClient) {
					this.SaveFileIP = ServerOptions.instance.ServerPlayerID.getValue();
				}

				this.load(SliceY.SliceBuffer, int1);
				fileInputStream.close();
			}
		}
	}

	public static ArrayList getAllFileNames() {
		ArrayList arrayList = new ArrayList();
		String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;
		for (int int1 = 1; int1 < 100; ++int1) {
			File file = new File(string + File.separator + "map_p" + int1 + ".bin");
			if (file.exists()) {
				arrayList.add("map_p" + int1 + ".bin");
			}
		}

		return arrayList;
	}

	public static String getUniqueFileName() {
		int int1 = 0;
		String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;
		for (int int2 = 1; int2 < 100; ++int2) {
			File file = new File(string + File.separator + "map_p" + int2 + ".bin");
			if (file.exists()) {
				int1 = int2;
			}
		}

		++int1;
		return GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p" + int1 + ".bin";
	}

	public void setVehicle4TestCollision(BaseVehicle baseVehicle) {
		this.vehicle4testCollision = baseVehicle;
	}

	public static ArrayList getAllSavedPlayers() {
		ArrayList arrayList = new ArrayList();
		IsoPlayer player = instance;
		String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;
		for (int int1 = 1; int1 < 100; ++int1) {
			File file = new File(string + File.separator + "map_p" + int1 + ".bin");
			if (file.exists()) {
				try {
					IsoPlayer player2 = new IsoPlayer(IsoWorld.instance.CurrentCell);
					try {
						player2.load(string + File.separator + "map_p" + int1 + ".bin");
					} finally {
						instance = player;
					}

					arrayList.add(player2);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		return arrayList;
	}

	public boolean isSaveFileInUse() {
		if (this.SaveFileName == null) {
			return false;
		} else {
			for (int int1 = 0; int1 < numPlayers; ++int1) {
				if (players[int1] != null && this.SaveFileName.equals(players[int1].SaveFileName)) {
					return true;
				}
			}

			return false;
		}
	}

	public void removeSaveFile() {
		try {
			File file;
			if (this == players[0]) {
				file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
				if (file.exists()) {
					file.delete();
				}
			} else if (this.SaveFileName != null) {
				file = new File(this.SaveFileName);
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean isSaveFileIPValid() {
		return isServerPlayerIDValid(this.SaveFileIP);
	}

	public static boolean isServerPlayerIDValid(String string) {
		if (GameClient.bClient) {
			String string2 = ServerOptions.instance.ServerPlayerID.getValue();
			return string2 != null && !string2.isEmpty() ? string2.equals(string) : true;
		} else {
			return true;
		}
	}

	public String getObjectName() {
		return "Player";
	}

	public void collideWith(IsoObject object) {
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
				if (vector2.set(float1, float2).getLength() > 0.0F) {
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
			this.angle.x = vector2.x;
			this.angle.y = vector2.y;
			if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
				this.dirtyRecalcGridStackTime = 10.0F;
			}
		}

		return vector2;
	}

	public IsoObject getInteract() {
		int int1 = 0;
		int int2 = 0;
		int int3 = 0;
		int int4 = 0;
		int int5 = 0;
		int int6 = 0;
		if (this.dir == IsoDirections.N) {
			--int4;
			--int6;
		}

		if (this.dir == IsoDirections.NE) {
			--int4;
			--int6;
			++int1;
			++int6;
		}

		if (this.dir == IsoDirections.E) {
			++int1;
			++int5;
		}

		if (this.dir == IsoDirections.SE) {
			++int1;
			++int5;
			++int2;
			++int6;
		}

		if (this.dir == IsoDirections.S) {
			++int2;
			++int6;
		}

		if (this.dir == IsoDirections.SW) {
			++int2;
			++int6;
			--int3;
			--int5;
		}

		if (this.dir == IsoDirections.W) {
			--int3;
			--int5;
		}

		if (this.dir == IsoDirections.NW) {
			--int3;
			--int4;
			--int5;
			--int6;
		}

		IsoGridSquare square = this.getCell().getGridSquare((int)this.getX() + int5, (int)(this.getY() + (float)int6), (int)this.getZ());
		IsoGridSquare square2 = this.getCell().getGridSquare((int)this.getX(), (int)this.getY(), (int)this.getZ());
		IsoGridSquare square3 = this.getCell().getGridSquare((int)(this.getX() + (float)int1), (int)this.getY(), (int)this.getZ());
		IsoGridSquare square4 = this.getCell().getGridSquare((int)this.getX(), (int)(this.getY() + (float)int2), (int)this.getZ());
		IsoGridSquare square5 = this.getCell().getGridSquare((int)(this.getX() - (float)int3), (int)this.getY(), (int)this.getZ());
		IsoGridSquare square6 = this.getCell().getGridSquare((int)this.getX(), (int)(this.getY() - (float)int4), (int)this.getZ());
		int int7;
		IsoObject object;
		if (square2 != null) {
			for (int7 = 0; int7 < square2.getObjects().size(); ++int7) {
				object = (IsoObject)square2.getObjects().get(int7);
				if (object.container != null) {
					return object;
				}
			}
		}

		if (square != null) {
			for (int7 = 0; int7 < square.getObjects().size(); ++int7) {
				object = (IsoObject)square.getObjects().get(int7);
				if (object.container != null) {
					return object;
				}
			}
		}

		if (int5 != 0 && int6 != 0) {
			IsoGridSquare square7 = this.getCell().getGridSquare((int)this.getX() + int5, (int)this.getY(), (int)this.getZ());
			IsoGridSquare square8 = this.getCell().getGridSquare((int)this.getX(), (int)this.getY() + int6, (int)this.getZ());
			int int8;
			IsoObject object2;
			if (square7 != null) {
				for (int8 = 0; int8 < square7.getObjects().size(); ++int8) {
					object2 = (IsoObject)square7.getObjects().get(int8);
					if (object2.container != null) {
						return object2;
					}
				}
			}

			if (square8 != null) {
				for (int8 = 0; int8 < square8.getObjects().size(); ++int8) {
					object2 = (IsoObject)square8.getObjects().get(int8);
					if (object2.container != null) {
						return object2;
					}
				}
			}
		}

		if (square2 != null && square2.getSpecialObjects().size() > 0) {
			for (int7 = 0; int7 < square2.getObjects().size(); ++int7) {
				object = (IsoObject)square2.getObjects().get(int7);
				if (object instanceof IsoDoor || object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
					return object;
				}
			}
		} else if (square3 != null && square3.getSpecialObjects().size() > 0) {
			for (int7 = 0; int7 < square3.getSpecialObjects().size(); ++int7) {
				object = (IsoObject)square3.getSpecialObjects().get(int7);
				if (object instanceof IsoDoor || object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
					return object;
				}
			}
		} else if (square4 != null && square4.getSpecialObjects().size() > 0) {
			for (int7 = 0; int7 < square4.getSpecialObjects().size(); ++int7) {
				object = (IsoObject)square4.getSpecialObjects().get(int7);
				if (object instanceof IsoDoor || object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
					return object;
				}
			}
		} else if (square5 != null && square4.getSpecialObjects().size() > 0) {
			for (int7 = 0; int7 < square5.getSpecialObjects().size(); ++int7) {
				object = (IsoObject)square5.getSpecialObjects().get(int7);
				if (object instanceof IsoDoor || object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
					return object;
				}
			}
		} else if (square6 != null && square4.getSpecialObjects().size() > 0) {
			for (int7 = 0; int7 < square6.getSpecialObjects().size(); ++int7) {
				object = (IsoObject)square6.getSpecialObjects().get(int7);
				if (object instanceof IsoDoor || object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
					return object;
				}
			}
		}

		return null;
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
		} else if (this.getMoodles().getMoodleLevel(MoodleType.Panic) >= 4 && this.HasTrait("AdrenalineJunkie")) {
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
			float float1 = 0.0F;
			if (this.leftHandItem != null && this.leftHandItem.getLightStrength() > 0.0F && (this.leftHandItem instanceof Drainable && ((Drainable)this.leftHandItem).getUsedDelta() > 0.0F || !(this.leftHandItem instanceof Drainable)) && (this.leftHandItem.canBeActivated() && this.leftHandItem.isActivated() || !this.leftHandItem.canBeActivated())) {
				float1 = this.leftHandItem.getLightStrength();
			}

			if (this.rightHandItem != null && this.rightHandItem.getLightStrength() > 0.0F && (this.rightHandItem instanceof Drainable && ((Drainable)this.rightHandItem).getUsedDelta() > 0.0F || !(this.rightHandItem instanceof Drainable)) && (this.rightHandItem.canBeActivated() && this.rightHandItem.isActivated() || !this.rightHandItem.canBeActivated())) {
				float1 = this.rightHandItem.getLightStrength();
			}

			return float1;
		}
	}

	public void pathFinished() {
		this.stateMachine.changeState(this.defaultState);
		this.path = null;
	}

	public void Scratched() {
		if (this.descriptor.Group != null && this.descriptor.Group.Members.size() > 0) {
			IsoSurvivor survivor = (IsoSurvivor)this.descriptor.Group.getRandomMemberExcept(instance);
		}
	}

	public void Bitten() {
		if (this.descriptor.Group != null && this.descriptor.Group.Members.size() > 0) {
			IsoSurvivor survivor = (IsoSurvivor)this.descriptor.Group.getRandomMemberExcept(instance);
		}
	}

	public float getRadiusKickback(HandWeapon handWeapon) {
		return 15.0F * this.getInvAimingMod();
	}

	public int getChancesToHeadshotHandWeapon() {
		int int1 = this.getPerkLevel(PerkFactory.Perks.Aiming);
		if (int1 == 1) {
			return 2;
		} else if (int1 == 2) {
			return 2;
		} else if (int1 == 3) {
			return 2;
		} else if (int1 == 4) {
			return 2;
		} else if (int1 == 5) {
			return 3;
		} else if (int1 == 6) {
			return 3;
		} else if (int1 == 7) {
			return 3;
		} else if (int1 == 8) {
			return 4;
		} else if (int1 == 9) {
			return 4;
		} else {
			return int1 == 10 ? 4 : 2;
		}
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

	public void CalculateAim() {
		if (this.JoypadBind == -1) {
			Vector2 vector2 = tempVector2.set(instance.getX(), instance.getY());
			int int1 = Mouse.getX();
			int int2 = Mouse.getY();
			vector2.x -= IsoUtils.XToIso((float)(int1 - 0), (float)int2 - 0.0F + 0.0F, instance.getZ());
			vector2.y -= IsoUtils.YToIso((float)(int1 - 0), (float)int2 - 0.0F + 0.0F, instance.getZ());
			float float1 = vector2.getLength();
			this.EffectiveAimDistance = float1;
			float float2 = float1 / 10.0F;
			if (float2 > 1.0F) {
				float2 *= 2.0F;
			}

			if (float2 < 0.05F) {
				float2 = 0.05F;
			}

			float2 *= this.getInvAimingRangeMod();
			if (this.IsUsingAimWeapon()) {
				this.DesiredAimRadius = float2 * 60.0F;
			} else if (this.IsUsingAimHandWeapon() && this.isCharging) {
				this.DesiredAimRadius = 10.0F;
			} else if (this.IsUsingAimHandWeapon() && !this.isCharging) {
				this.AimRadius = 100.0F;
			}

			if (this.IsUsingAimWeapon() && this.DesiredAimRadius < 10.0F) {
				this.DesiredAimRadius = 10.0F;
			}

			int1 = Mouse.getXA();
			int2 = Mouse.getYA();
			float float3 = IsoUtils.DistanceTo((float)int1, (float)int2, (float)lmx, (float)lmy);
			float float4 = float3 / 30.0F;
			float4 *= this.getInvAimingMod();
			this.AimRadius += float4 * 5.0F;
			if (this.AimRadius > 70.0F) {
				this.AimRadius = 70.0F;
			}

			lmx = int1;
			lmy = int2;
			float float5 = Math.abs(this.AimRadius - this.DesiredAimRadius) / 40.0F;
			float5 *= GameTime.instance.getMultiplier();
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
				if (float2 <= 0.2F) {
					float5 *= 2.0F;
				}

				if (float2 <= 0.5F) {
					float5 *= 2.0F;
				}

				float5 *= this.getAimingMod();
				if (this.getUseHandWeapon() != null) {
					float5 *= this.getUseHandWeapon().getAimingMod();
				}

				this.AimRadius -= float5;
				if (this.AimRadius < this.DesiredAimRadius) {
					this.AimRadius = this.DesiredAimRadius;
				}
			} else if (this.AimRadius > this.DesiredAimRadius) {
				this.AimRadius += float5;
				if (this.AimRadius > this.DesiredAimRadius) {
					this.AimRadius = this.DesiredAimRadius;
				}
			}
		}
	}

	public void render() {
	}

	public void doBeatenVehicle(float float1, float float2, float float3, boolean boolean1) {
		if (GameClient.bClient && this.isLocalPlayer() && boolean1) {
			this.stateMachine.changeState(ForecastBeatenPlayerState.instance());
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
					if (this.HasTrait("FastHealer")) {
						float6 = (float)((double)float6 * 0.8);
					} else if (this.HasTrait("SlowHealer")) {
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
		BaseVehicle baseVehicle;
		if (this.vehicle4testCollision != null && this.ULbeatenVehicle.Check() && SandboxOptions.instance.DamageToPlayerFromHitByACar.getValue() > 1) {
			baseVehicle = this.vehicle4testCollision;
			this.vehicle4testCollision = null;
			if (baseVehicle.isEngineRunning() && this.getVehicle() != baseVehicle) {
				float float1 = baseVehicle.jniLinearVelocity.x;
				float float2 = baseVehicle.jniLinearVelocity.z;
				if (this.isLocalPlayer()) {
					float1 = baseVehicle.netLinearVelocity.x;
					float2 = baseVehicle.netLinearVelocity.z;
				}

				float float3 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
				Vector2 vector2 = baseVehicle.testCollisionWithCharacter(this, 0.3F - 0.1F);
				if (vector2 != null && vector2.x != -1.0F) {
					vector2.x = (vector2.x - baseVehicle.x) * float3 * 1.0F + this.x;
					vector2.y = (vector2.y - baseVehicle.y) * float3 * 1.0F + this.x;
					if (this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.equals("ZombieDeath")) {
						int int1 = baseVehicle.testCollisionWithProneCharacter(this, this.angle.x, this.angle.y, false);
						if (int1 > 0) {
							this.doBeatenVehicle(Math.max(float3 * 6.0F, 5.0F), vector2.x, vector2.y, false);
						}

						this.doBeatenVehicle(0.0F, vector2.x, vector2.y, false);
					} else if (this.getCurrentState() != BeatenPlayerState.instance() && float3 > 0.1F) {
						this.doBeatenVehicle(Math.max(float3 * 2.0F, 5.0F), vector2.x, vector2.y, false);
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

				baseVehicle = null;
				if (this.getPrimaryHandItem() != null && this.getPrimaryHandItem() instanceof HandWeapon) {
					HandWeapon handWeapon = (HandWeapon)this.getPrimaryHandItem();
					if (this.lastWeapon != handWeapon) {
						String string = handWeapon.getSwingAnim();
						if (!string.equals("Bat") && !string.equals("Handgun") && !string.equals("Rifle")) {
							string = "Bat";
						}

						this.strafeRAnim = "Strafe_Aim_" + string + "_R";
						this.strafeAnim = "Strafe_Aim_" + string;
						this.walkAnim = "Walk_Aim_" + string;
						this.walkRAnim = "Walk_Aim_" + string + "_R";
						this.lastWeapon = handWeapon;
					}
				} else {
					this.strafeRAnim = "Strafe_R";
					this.strafeAnim = "Strafe";
					this.walkAnim = "Walk";
					this.walkRAnim = "Walk_R";
					this.lastWeapon = baseVehicle;
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
					GameSound gameSound = GameSounds.getSound("HeartBeat");
					boolean boolean1 = gameSound != null && gameSound.userVolume > 0.0F && this.stats.Panic > 0.0F;
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

					if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Interact")) && !GameKeyboard.wasKeyDown(Core.getInstance().getKey("Interact"))) {
						this.ContextPanic += 0.6F;
					}

					if (this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState2.instance() && this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbThroughWindowState2.instance() && this.getStateMachine().getCurrent() != OpenWindowState.instance() && this.getStateMachine().getCurrent() != SatChairState.instance() && this.getStateMachine().getCurrent() != SatChairStateOut.instance() && this.getStateMachine().getCurrent() != ClimbSheetRopeState.instance() && this.getStateMachine().getCurrent() != ClimbDownSheetRopeState.instance() && this.getStateMachine().getCurrent() != ForecastBeatenPlayerState.instance() && this.getStateMachine().getCurrent() != BeatenPlayerState.instance() && !this.isAsleep()) {
						BaseAction baseAction;
						if (!this.CharacterActions.isEmpty()) {
							baseAction = (BaseAction)this.CharacterActions.get(0);
							if (baseAction.blockMovementEtc) {
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
							baseAction = (BaseAction)this.CharacterActions.get(0);
							if (baseAction.blockMovementEtc) {
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
								int int2;
								if (Core.bDebug && GameKeyboard.isKeyDown(49) && !GameKeyboard.wasKeyDown(49)) {
									IsoPlayer player = null;
									for (int2 = 0; int2 < numPlayers; ++int2) {
										if (players[int2] != null && !players[int2].isDead()) {
											player = players[int2];
											break;
										}
									}

									if (player != null && this == player) {
										player.GhostMode = !player.GhostMode;
										player.godMod = !player.godMod;
										for (int2 = 0; int2 < numPlayers; ++int2) {
											if (players[int2] != null && players[int2] != player) {
												players[int2].GhostMode = player.GhostMode;
												players[int2].godMod = player.godMod;
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
											for (int2 = 0; int2 < this.getCell().getObjectList().size(); ++int2) {
												IsoMovingObject movingObject = (IsoMovingObject)this.getCell().getObjectList().get(int2);
												if (movingObject instanceof IsoSurvivor) {
													this.FollowCamStack.add((IsoSurvivor)movingObject);
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

							boolean boolean2 = false;
							boolean boolean3 = false;
							boolean boolean4 = false;
							this.bRunning = false;
							this.bSneaking = false;
							++this.TimeSinceRightClick;
							this.useChargeTime = this.chargeTime;
							float float4;
							float float5;
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
										if (!boolean3) {
											boolean3 = this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind);
										} else {
											boolean3 = this.isCharging && !JoypadManager.instance.isRTPressed(this.JoypadBind);
										}

										if (this.bJoypadIgnoreAimUntilCentered) {
											float4 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
											float5 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
											if (float4 == 0.0F && float5 == 0.0F) {
												this.bJoypadIgnoreAimUntilCentered = false;
											}
										}
									}

									if (this.isChargingLT && !JoypadManager.instance.isLTPressed(this.JoypadBind)) {
										boolean4 = true;
										boolean3 = false;
									}
								} else {
									if (this.bNewControls) {
										if (!boolean3) {
											boolean3 = this.isCharging && Mouse.isButtonDownUICheck(0);
										} else {
											boolean3 = this.isCharging && Mouse.isButtonDown(0);
										}
									} else if (!boolean3) {
										boolean3 = this.isCharging && !Mouse.isButtonDownUICheck(0);
									} else {
										boolean3 = this.isCharging && !Mouse.isButtonDown(0);
									}

									if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
										boolean4 = true;
										boolean3 = false;
									}
								}

								boolean boolean5;
								if (this.isCharging) {
									boolean5 = false;
								}

								if (boolean3) {
									this.TimeLeftPressed = 0;
									this.isAiming = true;
								} else {
									boolean5 = false;
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
								int int3 = 0;
								if (!this.bNewControls) {
									if (Mouse.isButtonDownUICheck(2)) {
										++this.TimeLeftPressed;
									} else {
										int3 = this.TimeLeftPressed;
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
									if (this.TimeLeftPressed == 0 && int3 != 0) {
										this.isCharging = true;
										this.isAiming = true;
										boolean3 = true;
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
							if ((double)this.playerMoveDir.x == 0.0 && (double)this.playerMoveDir.y == 0.0) {
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
								float4 = 0.0F;
								float5 = 0.0F;
								float float6 = float4;
								float float7 = float5;
								IsoDirections directions = this.dir;
								float float8;
								float float9;
								float float10;
								float float11;
								Vector2 vector22;
								Vector2 vector23;
								Vector2 vector24;
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

										float8 = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
										float9 = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
										float10 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
										float float12 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
										float11 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
										if (tempVector2.set(float12, float10).getLength() < float11) {
											float10 = 0.0F;
											float12 = 0.0F;
										}

										vector23 = tempVector2.set(float9, float8);
										if (vector23.getLength() > 1.0F) {
											vector23.setLength(1.0F);
											float9 = vector23.x;
											float8 = vector23.y;
										}

										if (Math.abs(float9) > float11) {
											vector22 = this.playerMoveDir;
											vector22.x += 0.04F * float9;
											vector22 = this.playerMoveDir;
											vector22.y -= 0.04F * float9;
											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (Math.abs(float8) > float11) {
											vector22 = this.playerMoveDir;
											vector22.y += 0.04F * float8;
											vector22 = this.playerMoveDir;
											vector22.x += 0.04F * float8;
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
											if (vector23.getLength() > 0.95F) {
												this.bRunning = true;
												this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
											} else if (vector23.getLength() < 0.5F) {
												this.bSneaking = true;
											}
										} else if (vector23.getLength() < 0.5F) {
											this.bSneaking = true;
										}

										this.playerMoveDir.setLength(0.05F * vector23.getLength() * vector23.getLength() * vector23.getLength() * vector23.getLength() * vector23.getLength() * vector23.getLength() * vector23.getLength() * vector23.getLength() * vector23.getLength());
										IsoDirections directions2;
										if (float12 == 0.0F && float10 == 0.0F) {
											if (float9 != 0.0F || float8 != 0.0F) {
												vector24 = tempVector2.set(this.playerMoveDir.x, this.playerMoveDir.y);
												if (vector24.getLength() > 0.0F) {
													vector24.normalize();
													directions2 = this.dir;
													this.DirectionFromVector(vector24);
													directions = this.dir;
													this.dir = directions2;
												}
											}
										} else {
											vector24 = tempVector2.set(float12, float10);
											directions2 = this.dir;
											vector24.normalize();
											this.DirectionFromVector(vector24);
											directions = this.dir;
											this.bSneaking = true;
											this.isAiming = true;
											this.dir = directions2;
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
											float4 = 0.0F;
											float5 = 0.0F;
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr))) {
											float4 -= 0.04F;
											directions = IsoDirections.W;
											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr))) {
											float4 += 0.04F;
											directions = IsoDirections.E;
											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr))) {
											float5 -= 0.04F;
											if (directions == IsoDirections.W) {
												directions = IsoDirections.NW;
											} else if (directions == IsoDirections.E) {
												directions = IsoDirections.NE;
											} else {
												directions = IsoDirections.N;
											}

											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr))) {
											float5 += 0.04F;
											if (directions == IsoDirections.W) {
												directions = IsoDirections.SW;
											} else if (directions == IsoDirections.E) {
												directions = IsoDirections.SE;
											} else {
												directions = IsoDirections.S;
											}

											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}
									} else if (this.PlayerIndex == 0 && this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
										if (!this.isBlockMovement() && (GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) || GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)))) {
											float4 = 0.0F;
											float5 = 0.0F;
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr))) {
											float4 -= 0.04F;
											float5 += 0.04F;
											directions = IsoDirections.SW;
											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(rightStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(leftStr))) {
											float4 += 0.04F;
											float5 -= 0.04F;
											directions = IsoDirections.NE;
											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr))) {
											UIManager.setDoMouseControls(true);
											float5 -= 0.04F;
											float4 -= 0.04F;
											if (directions == IsoDirections.SW) {
												directions = IsoDirections.W;
											} else if (directions == IsoDirections.NE) {
												directions = IsoDirections.N;
											} else {
												directions = IsoDirections.NW;
											}

											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}

										if (!this.isBlockMovement() && GameKeyboard.isKeyDown(Core.getInstance().getKey(backwardStr)) && !GameKeyboard.isKeyDown(Core.getInstance().getKey(forwardStr))) {
											float5 += 0.04F;
											float4 += 0.04F;
											if (directions == IsoDirections.SW) {
												directions = IsoDirections.S;
											} else if (directions == IsoDirections.NE) {
												directions = IsoDirections.E;
											} else {
												directions = IsoDirections.SE;
											}

											if (this.stateMachine.getCurrent() == PathFindState.instance()) {
												this.stateMachine.setCurrent(this.defaultState);
											}

											this.JustMoved = true;
											this.setBeenMovingFor(this.getBeenMovingFor() + this.BeenMovingForIncrease * GameTime.getInstance().getMultiplier());
										}
									}

									float6 = float4;
									float7 = float5;
									if (!this.isBlockMovement() && this.JustMoved) {
										if (!this.isAiming) {
											if (this.JoypadBind != -1) {
												float6 = this.playerMoveDir.x;
												float7 = this.playerMoveDir.y;
												this.angle.x = float6;
												this.angle.y = float7;
												if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
													this.dirtyRecalcGridStackTime = 2.0F;
												}
											} else {
												this.angle.x = float4;
												this.angle.y = float5;
												float6 = this.angle.x;
												float7 = this.angle.y;
												if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
													this.dirtyRecalcGridStackTime = 2.0F;
												}
											}
										}

										this.bRightClickMove = true;
										this.angle.normalize();
										UIManager.speedControls.SetCurrentGameSpeed(1);
									} else {
										if (float4 != 0.0F || float5 != 0.0F) {
											this.angle.x = float4;
											this.angle.y = float5;
											if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
												this.dirtyRecalcGridStackTime = 2.0F;
											}
										}

										float6 = float4;
										float7 = float5;
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
									float8 = this.getMoveSpeed();
									float9 = 1.0F;
									float10 = 0.0F;
									if (this.bRightClickMove) {
										if (this.JustMoved) {
											if (!this.bRunning) {
												float10 = 1.0F;
											} else {
												float10 = 1.5F;
											}
										} else if (!this.isAiming) {
											float10 = this.runAngle.getLength() / 4.0F;
										}

										if (float10 > 1.5F) {
											float10 = 1.5F;
										}
									}

									float9 *= float10;
									if (this.runAngle.getLength() == 0.0F && !this.JustMoved) {
										float9 = 0.0F;
									}

									if (float9 > 1.0F) {
										float9 *= this.getSprintMod();
									}

									float11 = this.CurrentSpeed / 0.06F * this.getGlobalMovementMod(false);
									if (float9 > 1.0F && this.HasTrait("Athletic")) {
										float9 *= 1.2F;
									}

									if (float9 > 1.0F) {
										if (this.HasTrait("Overweight")) {
											float9 *= 0.99F;
										}

										if (this.HasTrait("Obese")) {
											float9 *= 0.85F;
											if (this.getNutrition().getWeight() > 120.0F) {
												float9 *= 0.97F;
											}
										}

										if (this.HasTrait("Out of Shape")) {
											float9 *= 0.99F;
										}

										if (this.HasTrait("Unfit")) {
											float9 *= 0.8F;
										}
									}

									float float13 = this.CurrentSpeed / 0.06F;
									float float14;
									float float15;
									float float16;
									Stats stats;
									if (!(float11 > 1.0F) && !(float9 > 1.0F)) {
										if (this.CurrentSpeed > 0.0F && this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 0) {
											float15 = 0.7F;
											if (this.HasTrait("Asthmatic")) {
												float15 = 1.4F;
											}

											float16 = 1.4F;
											if (this.HasTrait("Overweight")) {
												float16 = 2.9F;
											}

											if (this.HasTrait("Athletic")) {
												float16 = 0.8F;
											}

											float16 *= 3.0F;
											float16 *= this.getPacingMod();
											float16 *= this.getHyperthermiaMod();
											float14 = 2.8F;
											switch (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
											case 1: 
												float14 = 1.5F;
												break;
											
											case 2: 
												float14 = 1.9F;
												break;
											
											case 3: 
												float14 = 2.3F;
											
											}

											stats = this.stats;
											stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)float16 * (double)(float13 * 0.5F) * (double)float15 * (double)GameTime.instance.getMultiplier() * (double)float14 * (double)this.stats.endurance / 2.0);
										}
									} else {
										if (float9 < 1.0F) {
											float13 *= 0.3F;
										}

										float15 = 1.4F;
										if (this.HasTrait("Overweight")) {
											float15 = 2.9F;
										}

										if (this.HasTrait("Athletic")) {
											float15 = 0.8F;
										}

										float15 *= 3.0F;
										float15 *= this.getPacingMod();
										float15 *= this.getHyperthermiaMod();
										float16 = 0.7F;
										if (this.HasTrait("Asthmatic")) {
											float16 = 1.4F;
										}

										if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
											stats = this.stats;
											stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)float15 * (double)(float13 * 0.5F) * (double)float16 * (double)GameTime.instance.getMultiplier() * (double)this.stats.endurance);
										} else {
											float14 = 2.8F;
											switch (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad)) {
											case 1: 
												float14 = 1.5F;
												break;
											
											case 2: 
												float14 = 1.9F;
												break;
											
											case 3: 
												float14 = 2.3F;
											
											}

											stats = this.stats;
											stats.endurance = (float)((double)stats.endurance - ZomboidGlobals.RunningEnduranceReduce * (double)float15 * (double)(float13 * 0.5F) * (double)float16 * (double)GameTime.instance.getMultiplier() * (double)float14 * (double)this.stats.endurance);
										}
									}

									if (TutorialManager.instance.ActiveControlZombies && !IsoWorld.instance.CurrentCell.IsZone("tutArea", (int)this.x, (int)this.y)) {
										TutorialManager.instance.ActiveControlZombies = false;
									}

									if (this.bSneaking && this.JustMoved) {
										float9 *= 0.7F;
									}

									if (this.bSneaking) {
										float9 *= this.getNimbleMod();
									}

									if (float9 > 0.0F) {
										if (float9 < 0.7F) {
											this.bSneaking = true;
										}

										if (float9 > 1.2F) {
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

									if (boolean4) {
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

										vector23 = null;
										HandWeapon handWeapon2;
										if (this.leftHandItem instanceof HandWeapon) {
											handWeapon2 = (HandWeapon)this.leftHandItem;
										} else {
											handWeapon2 = this.bareHands;
										}

										if (!this.isForceShove() && handWeapon2 != null && this.AttackDelay <= 0.0F && this.isAiming && !this.JustMoved && this.DoAimAnimOnAiming()) {
											this.PlayShootAnim();
										}

										if (boolean3 && !this.bBannedAttacking) {
											this.sprite.Animate = true;
											vector24 = tempVector2.set(instance.getX(), instance.getY());
											int int4 = Mouse.getX();
											int int5 = Mouse.getY();
											vector24.x -= IsoUtils.XToIso((float)int4, (float)int5 + 55.0F * this.def.getScaleY(), this.getZ());
											vector24.y -= IsoUtils.YToIso((float)int4, (float)int5 + 55.0F * this.def.getScaleY(), this.getZ());
											vector24.x = -vector24.x;
											vector24.y = -vector24.y;
											vector24.normalize();
											if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
												vector24 = this.getControllerAimDir(vector24);
											}

											if (vector24.getLength() > 0.0F) {
												this.DirectionFromVector(vector24);
												this.angle.x = vector24.x;
												this.angle.y = vector24.y;
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

										int int6 = Core.getInstance().getKey("Aim");
										boolean boolean6 = GameKeyboard.isKeyDown(int6);
										boolean boolean7 = int6 == 29 || int6 == 157;
										if (this.PlayerIndex == 0 && boolean7 && boolean6 && UIManager.isMouseOverInventory() || this.JustMoved && !this.isAiming || this.JoypadBind != -1 && !this.bJoypadMovementActive) {
											if (this.angle.getLength() > 0.0F) {
												this.DirectionFromVector(this.angle);
											}
										} else {
											Vector2 vector25 = tempVector2.set(this.getX(), this.getY());
											int int7 = Mouse.getX();
											int int8 = Mouse.getY();
											vector25.x -= IsoUtils.XToIso((float)int7, (float)int8 + 55.0F * this.def.getScaleY(), this.getZ());
											vector25.y -= IsoUtils.YToIso((float)int7, (float)int8 + 55.0F * this.def.getScaleY(), this.getZ());
											this.runAngle.x = vector25.x;
											this.runAngle.y = vector25.y;
											if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
												this.dirtyRecalcGridStackTime = 2.0F;
											}

											if (this.runAngle.getLength() < 0.3F) {
												this.runAngle.setLength(0.0F);
											} else {
												this.runAngle.setLength(this.runAngle.getLength() - 0.3F);
											}

											vector25.x = -vector25.x;
											vector25.y = -vector25.y;
											vector25.normalize();
											if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
												this.dirtyRecalcGridStackTime = 2.0F;
											}

											this.lastAngle.x = this.angle.x;
											this.lastAngle.y = this.angle.y;
											++this.angleCounter;
											if (GameWindow.ActivatedJoyPad != null && this.JoypadBind != -1) {
												vector25 = this.getControllerAimDir(vector25);
											} else {
												this.angle.x = vector25.x;
												this.angle.y = vector25.y;
												if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
													this.dirtyRecalcGridStackTime = 2.0F;
												}

												this.angleCounter = 0;
											}

											if (vector25.getLength() > 0.0F) {
												this.DirectionFromVector(vector25);
												this.angle.x = vector25.x;
												this.angle.y = vector25.y;
												if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
													this.dirtyRecalcGridStackTime = 2.0F;
												}
											}
										}

										directions = this.dir;
									}

									if (this.angle.x == 0.0F && this.angle.y == 0.0F) {
										this.angle.x = this.dir.ToVector().x;
										this.angle.y = this.dir.ToVector().y;
									}

									if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
										this.dirtyRecalcGridStackTime = 2.0F;
									}

									if (this.DragCharacter != null) {
										float9 = 0.4F;
									}

									if (this.stats.endurance < 0.0F) {
										this.stats.endurance = 0.0F;
									}

									if (this.stats.endurance > 1.0F) {
										this.stats.endurance = 1.0F;
									}

									switch (this.Moodles.getMoodleLevel(MoodleType.Endurance)) {
									case 1: 
										float9 *= 0.95F;
										break;
									
									case 2: 
										float9 *= 0.9F;
										break;
									
									case 3: 
										float9 *= 0.8F;
										break;
									
									case 4: 
										float9 *= 0.6F;
									
									}

									if (this.stats.enduranceRecharging) {
										float9 *= 0.85F;
									}

									if (float9 < 0.6F) {
										float13 = 1.0F;
										float13 *= 1.0F - this.stats.fatigue;
										float13 *= GameTime.instance.getMultiplier();
										if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
											stats = this.stats;
											stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float13);
										}
									}

									if (float9 <= 1.0F && float9 > 0.6F) {
										float13 = 1.0F;
										float13 *= 1.0F - this.stats.fatigue;
										float13 *= GameTime.instance.getMultiplier();
										if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) == 0) {
											stats = this.stats;
											stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce / 3.0 * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float13);
										}
									}

									if (this.Moodles.getMoodleLevel(MoodleType.HeavyLoad) > 0) {
										float13 = this.getInventory().getCapacityWeight();
										float15 = (float)this.getMaxWeight();
										float16 = Math.min(2.7F, float13 / float15) - 1.0F;
										float9 *= 0.65F + 0.35F * (1.0F - float16);
									}

									if (this.playerMoveDir.getLength() > 0.0F) {
										UIManager.CloseContainers();
									}

									int int9 = Math.abs(this.dir.index() - directions.index());
									if (int9 > 4) {
										int9 = 4 - (int9 - 4);
									}

									if (int9 > 2) {
									}

									if (!this.bFalling && !this.isAiming && !boolean3) {
										this.dir = directions;
									}

									if (this.current != null) {
										if (this.current.Has(IsoObjectType.stairsBN) || this.current.Has(IsoObjectType.stairsMN) || this.current.Has(IsoObjectType.stairsTN)) {
											float6 = 0.0F;
											if (!this.JustMoved && this.bRightClickMove) {
												this.angle.x = 0.0F;
												this.angle.normalize();
											}
										}

										if (this.current.Has(IsoObjectType.stairsBW) || this.current.Has(IsoObjectType.stairsMW) || this.current.Has(IsoObjectType.stairsTW)) {
											float7 = 0.0F;
											if (!this.JustMoved && this.bRightClickMove) {
												this.angle.y = 0.0F;
												this.angle.normalize();
											}
										}
									}

									if (this.isAiming && (GameWindow.ActivatedJoyPad == null || this.JoypadBind == -1)) {
										this.playerMoveDir.x = float6;
										this.playerMoveDir.y = float7;
									}

									if (!this.isAiming && this.bRightClickMove) {
										float6 = this.angle.x;
										float7 = this.angle.y;
										this.playerMoveDir.x = float6;
										this.playerMoveDir.y = float7;
									}

									float13 = 1.0F + (1.0F - (float11 > 1.0F ? 1.0F : float11));
									Vector2 vector26;
									if (this.bRightClickMove) {
										float15 = 0.0F;
										if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 1) {
											float15 = 0.1F;
										}

										if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 2) {
											float15 = 0.3F;
										}

										if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 3) {
											float15 = 0.5F;
										}

										if (this.Moodles.getMoodleLevel(MoodleType.Drunk) == 4) {
											float15 = 1.0F;
										}

										if (!this.bRunning) {
											float15 /= 2.0F;
										}

										if (Rand.Next(80) == 0) {
											this.DrunkCos2 = (float)Rand.Next(-1000, 1000) / 500.0F;
											this.DrunkCos2 *= float15;
										}

										if (this.DrunkSin < this.DrunkCos2) {
											this.DrunkSin += 0.015F;
											if (this.DrunkSin > this.DrunkCos2) {
												this.DrunkSin = this.DrunkCos2;
												this.DrunkCos2 = (float)Rand.Next(-1000, 1000) / 500.0F;
												this.DrunkCos2 *= float15;
											}
										}

										if (this.DrunkSin > this.DrunkCos2) {
											this.DrunkSin -= 0.015F;
											if (this.DrunkSin < this.DrunkCos2) {
												this.DrunkSin = this.DrunkCos2;
												this.DrunkCos2 = (float)Rand.Next(-1000, 1000) / 500.0F;
												this.DrunkCos2 *= float15;
											}
										}

										this.playerMoveDir.rotate(this.DrunkSin);
										if (float15 > 0.0F && (this.playerMoveDir.x != 0.0F || this.playerMoveDir.y != 0.0F)) {
											vector26 = tempo;
											tempo.x = this.playerMoveDir.x;
											tempo.y = this.playerMoveDir.y;
											vector26.normalize();
											IsoDirections directions3 = this.dir;
											this.DirectionFromVector(vector26);
											directions = this.dir;
											this.dir = directions3;
											float6 = vector26.x;
											float7 = vector26.y;
											if (!this.isAiming) {
												this.angle.x = float6;
												this.angle.y = float7;
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

									boolean boolean8 = this.getStateMachine().getCurrent() == SwipeStatePlayer.instance();
									if (!this.CharacterActions.isEmpty()) {
										BaseAction baseAction2 = (BaseAction)this.CharacterActions.get(0);
										if (baseAction2.overrideAnimation) {
											boolean8 = true;
										}
									}

									if (!boolean8 && !this.isForceOverrideAnim()) {
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
												} else if (float11 > 1.0F) {
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

														this.def.setFrameSpeedPerFrame(0.25F * float11);
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

														this.def.setFrameSpeedPerFrame(0.25F * float11);
													} else {
														tempo.x = this.playerMoveDir.x;
														tempo.y = this.playerMoveDir.y;
														tempo.normalize();
														float16 = tempo.dot(this.angle);
														if ((double)float16 > 0.8) {
															this.def.setFrameSpeedPerFrame(0.2F * (this.playerMoveDir.getLength() / 0.06F));
															this.PlayAnim(this.walkAnim);
														} else if ((double)float16 >= -0.8 && (double)float16 <= 0.8) {
															tempo.rotate((float)Math.toRadians(90.0));
															float16 = tempo.dot(this.angle);
															if (float16 < 0.0F) {
																this.PlayAnim(this.strafeAnim);
															} else {
																this.PlayAnim(this.strafeRAnim);
															}

															this.playerMoveDir.setLength(this.CurrentSpeed * 0.8F);
															this.def.setFrameSpeedPerFrame(0.35F * (this.CurrentSpeed / 0.06F));
														} else if (float16 < -0.8F) {
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
											float16 = this.getPathSpeed() / 0.06F * this.getGlobalMovementMod(false);
											this.def.setFrameSpeedPerFrame(0.25F * float16);
										}
									}

									if (float9 > 1.3F) {
										float16 = 1.0F;
										float14 = 180.0F;
										if ((float)this.TimeSprinting >= float14) {
											float16 = 1.0F - ((float)this.TimeSprinting - float14) / 360.0F;
										} else {
											float16 = 1.0F - (float14 - (float)this.TimeSprinting) / 360.0F;
										}

										float16 *= 0.1F;
										++float16;
										if (float16 < 0.0F) {
											float16 = 0.0F;
										}

										++this.TimeSprinting;
										this.TargetSpeed = float8 * float16 * float9 * 1.1F;
									} else {
										this.TargetSpeed = float8 * float9 * 0.9F;
										if (this.CurrentSpeed < 0.08F) {
											this.TimeSprinting = 0;
										}
									}

									float16 = this.SpeedChange;
									if (this.CurrentSpeed < 0.06F) {
										float16 *= 5.0F;
									}

									if (this.slowTimer > 0.0F) {
										this.TargetSpeed *= 1.0F - this.slowFactor;
									}

									if (this.CurrentSpeed < this.TargetSpeed) {
										this.CurrentSpeed += float16 / 3.0F;
										if (this.CurrentSpeed > this.TargetSpeed) {
											this.CurrentSpeed = this.TargetSpeed;
										}
									} else if (this.CurrentSpeed > this.TargetSpeed) {
										if (this.CurrentSpeed < 0.03F) {
											this.CurrentSpeed = this.TargetSpeed;
										} else {
											this.CurrentSpeed -= float16;
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

									float8 = this.CurrentSpeed;
									this.playerMoveDir.setLength(float8);
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
										vector26 = new Vector2(instance.getX(), instance.getY());
										vector26.x -= this.DragObject.getX();
										vector26.y -= this.DragObject.getY();
										vector26.x = -vector26.x;
										vector26.y = -vector26.y;
										vector26.normalize();
										this.DirectionFromVectorNoDiags(vector26);
										if ((this.dir == IsoDirections.W || this.dir == IsoDirections.S || this.dir == IsoDirections.N || this.dir == IsoDirections.E) && this.DragObject instanceof IsoWheelieBin) {
											this.DragObject.dir = this.dir;
										}
									}

									if (this.DragObject != null && this.DragObject instanceof IsoWheelieBin) {
										this.DragObject.dir = this.dir;
									}

									if (this.DragObject != null) {
										float16 = this.DragObject.getWeight(this.playerMoveDir.x, this.playerMoveDir.y) + this.getWeight(this.playerMoveDir.x, this.playerMoveDir.y);
										float14 = this.getWeight(this.playerMoveDir.x, this.playerMoveDir.y) / float16;
										vector22 = this.playerMoveDir;
										vector22.x *= float14;
										vector22 = this.playerMoveDir;
										vector22.y *= float14;
									}

									if (this.DragObject != null && this.playerMoveDir.getLength() != 0.0F) {
										this.DragObject.setImpulsex(this.DragObject.getImpulsex() + this.playerMoveDir.x);
										this.DragObject.setImpulsey(this.DragObject.getImpulsey() + this.playerMoveDir.y);
									}

									if (this.GhostMode) {
										vector22 = this.playerMoveDir;
										vector22.x = (float)((double)vector22.x * (4.0 + (this.bRunning ? 4.0 : 0.5)));
										vector22 = this.playerMoveDir;
										vector22.y = (float)((double)vector22.y * (4.0 + (this.bRunning ? 4.0 : 0.5)));
									}

									if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
										this.Move(this.playerMoveDir);
									}

									if (GameClient.bClient && networkUpdate.Check()) {
										GameClient.instance.sendPlayer(this);
									}

									if (this.DragCharacter != null) {
										vector26 = new Vector2(instance.getX(), instance.getY());
										vector26.x -= this.DragCharacter.getX();
										vector26.y -= this.DragCharacter.getY();
										vector26.x = -vector26.x;
										vector26.y = -vector26.y;
										vector26.normalize();
										this.DirectionFromVector(vector26);
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
							if ((int)this.getZ() == (int)baseVehicle.getZ() && (!this.isLocalPlayer() || baseVehicle.targetAlpha[this.PlayerIndex] != 0.0F) && !(this.DistToSquared((float)((int)baseVehicle.x), (float)((int)baseVehicle.y)) >= 16.0F) && PolygonalMap2.instance.intersectLineWithVehicle(this.x, this.y, this.x + this.angle.x * 4.0F, this.y + this.angle.y * 4.0F, baseVehicle, tempVector2) && !PolygonalMap2.instance.lineClearCollide(this.x, this.y, tempVector2.x, tempVector2.y, (int)this.z, baseVehicle, false, true)) {
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
				float float1 = 1.0F;
				float1 *= 1.0F - this.stats.fatigue;
				float1 *= GameTime.instance.getMultiplier();
				Stats stats = this.stats;
				stats.endurance = (float)((double)stats.endurance + ZomboidGlobals.ImobileEnduranceReduce * SandboxOptions.instance.getEnduranceRegenMultiplier() * (double)this.getRecoveryMod() * (double)float1);
			}

			if (this.vehicle != null) {
				Vector3f vector3f = this.vehicle.getForwardVector(this.tempVector3f);
				this.angle.x = vector3f.x;
				this.angle.y = vector3f.z;
				this.angle.normalize();
				if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
					this.dirtyRecalcGridStackTime = 10.0F;
				}

				this.DirectionFromVector(this.angle);
				boolean boolean1 = false;
				int int1 = this.vehicle.getSeat(this);
				VehiclePart vehiclePart = this.vehicle.getPassengerDoor(int1);
				if (vehiclePart != null) {
					VehicleWindow vehicleWindow = vehiclePart.findWindow();
					if (vehicleWindow != null && !vehicleWindow.isHittable()) {
						boolean1 = true;
					}
				}

				if (boolean1) {
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

			this.isAiming = float1 * float1 + float2 * float2 >= 0.09F;
			this.isCharging = this.isAiming && JoypadManager.instance.isRTPressed(this.JoypadBind);
			this.isChargingLT = this.isAiming && JoypadManager.instance.isLTPressed(this.JoypadBind);
		} else {
			if (Mouse.isButtonDown(1)) {
				this.TimeRightPressed += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
			} else {
				this.TimeRightPressed = 0.0F;
			}

			boolean boolean3 = this.isAimKeyDown();
			this.isAiming = boolean3 || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= TIME_RIGHT_PRESSED_SECONDS;
			if (this.isCharging) {
				this.isCharging = boolean3 || Mouse.isButtonDown(1);
			} else {
				this.isCharging = boolean3 || Mouse.isButtonDownUICheck(1) && this.TimeRightPressed >= TIME_RIGHT_PRESSED_SECONDS;
			}

			if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Melee")) && this.authorizeMeleeAction) {
				boolean2 = true;
			} else {
				boolean1 = this.isCharging && Mouse.isButtonDownUICheck(0);
				if (boolean1) {
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
			if (boolean2) {
				this.sprite.Animate = true;
				this.DoAttack(this.useChargeTime, true, (String)null);
				this.useChargeTime = 0.0F;
				this.chargeTime = 0.0F;
			} else {
				if (!this.isForceShove() && this.AttackDelay <= 0.0F && this.DoAimAnimOnAiming()) {
					this.PlayShootAnim();
				}

				if (boolean1) {
					this.sprite.Animate = true;
					this.AttemptAttack(this.useChargeTime);
					this.useChargeTime = 0.0F;
					this.chargeTime = 0.0F;
				}
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

		if (vector2.getLength() > 0.0F) {
			vector2.normalize();
			this.DirectionFromVector(vector2);
			this.angle.x = vector2.x;
			this.angle.y = vector2.y;
			if ((this.lastAngle.x != this.angle.x || this.lastAngle.y != this.angle.y) && this instanceof IsoPlayer) {
				this.dirtyRecalcGridStackTime = 10.0F;
			}
		}
	}

	private void updateTorchStrength() {
		if (this.getTorchStrength() > 0.0F || this.flickTorch) {
			InventoryItem inventoryItem = null;
			if (this.leftHandItem != null && this.leftHandItem.getLightStrength() > 0.0F && this.leftHandItem instanceof Drainable) {
				inventoryItem = this.leftHandItem;
			}

			if (inventoryItem == null && this.rightHandItem != null && this.rightHandItem.getLightStrength() > 0.0F && this.rightHandItem instanceof Drainable) {
				inventoryItem = this.rightHandItem;
			}

			if (inventoryItem == null) {
				return;
			}

			if (Rand.Next(600 - (int)(0.4 / (double)((Drainable)inventoryItem).getUsedDelta() * 100.0)) == 0) {
				this.flickTorch = true;
			}

			if (this.flickTorch) {
				if (Rand.Next(6) == 0) {
					inventoryItem.setActivated(false);
				} else {
					inventoryItem.setActivated(true);
				}

				if (Rand.Next(40) == 0) {
					this.flickTorch = false;
					inventoryItem.setActivated(true);
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

	private boolean isSafeToClimbOver(IsoDirections directions) {
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
			DebugLog.log("IsoPlayer.isSafeToClimbOver(): unhandled direction");
			return false;
		
		}
		return square != null && square.TreatAsSolidFloor() && !square.Is(IsoFlagType.water);
	}

	private boolean doContext(IsoDirections directions, boolean boolean1) {
		if (this instanceof IsoPlayer && this.isBlockMovement()) {
			return false;
		} else {
			float float1 = this.x - (float)((int)this.x);
			float float2 = this.y - (float)((int)this.y);
			if (directions == IsoDirections.NW) {
				if (float2 < float1) {
					return this.doContext(IsoDirections.N, boolean1) ? true : this.doContext(IsoDirections.W, boolean1);
				} else {
					return this.doContext(IsoDirections.W, boolean1) ? true : this.doContext(IsoDirections.N, boolean1);
				}
			} else if (directions == IsoDirections.NE) {
				float1 = 1.0F - float1;
				if (float2 < float1) {
					return this.doContext(IsoDirections.N, boolean1) ? true : this.doContext(IsoDirections.E, boolean1);
				} else {
					return this.doContext(IsoDirections.E, boolean1) ? true : this.doContext(IsoDirections.N, boolean1);
				}
			} else if (directions == IsoDirections.SE) {
				float1 = 1.0F - float1;
				float2 = 1.0F - float2;
				if (float2 < float1) {
					return this.doContext(IsoDirections.S, boolean1) ? true : this.doContext(IsoDirections.E, boolean1);
				} else {
					return this.doContext(IsoDirections.E, boolean1) ? true : this.doContext(IsoDirections.S, boolean1);
				}
			} else if (directions == IsoDirections.SW) {
				float2 = 1.0F - float2;
				if (float2 < float1) {
					return this.doContext(IsoDirections.S, boolean1) ? true : this.doContext(IsoDirections.W, boolean1);
				} else {
					return this.doContext(IsoDirections.W, boolean1) ? true : this.doContext(IsoDirections.S, boolean1);
				}
			} else {
				if (this.current != null) {
					if (directions == IsoDirections.N && this.current.Is(IsoFlagType.climbSheetN) && this.canClimbSheetRope(this.current)) {
						this.climbSheetRope();
						return true;
					}

					if (directions == IsoDirections.S && this.current.Is(IsoFlagType.climbSheetS) && this.canClimbSheetRope(this.current)) {
						this.climbSheetRope();
						return true;
					}

					if (directions == IsoDirections.W && this.current.Is(IsoFlagType.climbSheetW) && this.canClimbSheetRope(this.current)) {
						this.climbSheetRope();
						return true;
					}

					if (directions == IsoDirections.E && this.current.Is(IsoFlagType.climbSheetE) && this.canClimbSheetRope(this.current)) {
						this.climbSheetRope();
						return true;
					}
				}

				IsoGridSquare square = this.getCurrentSquare();
				if (directions == IsoDirections.S) {
					square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
				} else if (directions == IsoDirections.E) {
					square = IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
				}

				if (square == null) {
					return false;
				} else {
					boolean boolean2 = directions == IsoDirections.S || directions == IsoDirections.N;
					IsoObject object = square.getDoorOrWindow(boolean2);
					if (object == null) {
						object = square.getWindowFrame(boolean2);
					}

					if (object == null) {
						object = this.getCurrentSquare().getOpenDoor(directions);
					}

					IsoGridSquare square2;
					if (object == null) {
						if (directions == IsoDirections.N) {
							square2 = square.nav[IsoDirections.N.index()];
							if (square2 != null) {
								object = square2.getOpenDoor(IsoDirections.S);
							}
						} else if (directions == IsoDirections.S) {
							object = square.getOpenDoor(IsoDirections.N);
						} else if (directions == IsoDirections.W) {
							square2 = square.nav[IsoDirections.W.index()];
							if (square2 != null) {
								object = square2.getOpenDoor(IsoDirections.E);
							}
						} else if (directions == IsoDirections.E) {
							object = square.getOpenDoor(IsoDirections.W);
						}
					}

					square2 = this.getCurrentSquare() == null ? null : this.getCurrentSquare().nav[directions.index()];
					boolean boolean3 = IsoWindow.isTopOfSheetRopeHere(square2) && this.canClimbDownSheetRope(square2);
					if (object == null) {
						if (Keyboard.isKeyDown(42) && this.current != null && this.ticksSincePressedMovement > PerformanceSettings.LockFPS / 2) {
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

							IsoObject object4;
							if (directions == IsoDirections.E) {
								square = IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
								object4 = square != null ? square.getDoor(true) : null;
								if (object4 instanceof IsoDoor && ((IsoDoor)object4).isFacingSheet(this)) {
									((IsoDoor)object4).toggleCurtain();
									return true;
								}
							}

							if (directions == IsoDirections.S) {
								square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
								object4 = square != null ? square.getDoor(false) : null;
								if (object4 instanceof IsoDoor && ((IsoDoor)object4).isFacingSheet(this)) {
									((IsoDoor)object4).toggleCurtain();
									return true;
								}
							}
						}

						boolean boolean4 = this.isSafeToClimbOver(directions);
						if (this.z > 0.0F) {
							boolean4 = false;
							if (boolean3) {
								boolean4 = true;
							}
						}

						if (this.timePressedContext < 0.5F && !boolean4) {
							return false;
						} else if (directions == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
							this.StateMachineParams.clear();
							this.StateMachineParams.put(0, directions);
							this.changeState(ClimbOverFenceState.instance());
							return true;
						} else if (directions == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
							this.StateMachineParams.clear();
							this.StateMachineParams.put(0, directions);
							this.changeState(ClimbOverFenceState.instance());
							return true;
						} else if (directions == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z).Is(IsoFlagType.HoppableN)) {
							this.StateMachineParams.clear();
							this.StateMachineParams.put(0, directions);
							this.changeState(ClimbOverFenceState.instance());
							return true;
						} else if (directions == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z).Is(IsoFlagType.HoppableW)) {
							this.StateMachineParams.clear();
							this.StateMachineParams.put(0, directions);
							this.changeState(ClimbOverFenceState.instance());
							return true;
						} else {
							return false;
						}
					} else {
						if (object instanceof IsoDoor) {
							IsoDoor door = (IsoDoor)object;
							if (Keyboard.isKeyDown(42) && door.HasCurtains() != null && door.isFacingSheet(this) && this.ticksSincePressedMovement > PerformanceSettings.LockFPS / 2) {
								door.toggleCurtain();
							} else if (!boolean1) {
								door.ToggleDoor(this);
							}
						} else {
							IsoThumpable thumpable;
							if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor()) {
								thumpable = (IsoThumpable)object;
								if (!boolean1) {
									thumpable.ToggleDoor(this);
								}
							} else {
								IsoCurtain curtain;
								if (object instanceof IsoWindow && !object.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
									IsoWindow window = (IsoWindow)object;
									if (Keyboard.isKeyDown(42)) {
										curtain = window.HasCurtains();
										if (curtain != null && this.current != null && !curtain.getSquare().isBlockedTo(this.current)) {
											curtain.ToggleDoor(this);
										}
									} else if (this.timePressedContext >= 0.5F) {
										if (window.canClimbThrough(this)) {
											if (!boolean1 && !this.isBlockMovement()) {
												this.StateMachineParams.clear();
												this.StateMachineParams.put(0, window);
												this.changeState(ClimbThroughWindowState.instance());
											}
										} else if (!window.PermaLocked && !window.isBarricaded() && !boolean1) {
											this.openWindow(window);
										}
									} else if (window.Health > 0 && !window.isDestroyed()) {
										if (!window.open && window.getBarricadeForCharacter(this) == null) {
											if (!boolean1) {
												this.openWindow(window);
											}
										} else if (!boolean1) {
											window.ToggleWindow(this);
										}
									} else {
										if (!this.isSafeToClimbOver(directions) && !object.getSquare().haveSheetRope && !boolean3) {
											return true;
										}

										if (!boolean1 && !window.isBarricaded() && !this.isBlockMovement()) {
											this.StateMachineParams.clear();
											this.StateMachineParams.put(0, window);
											this.changeState(ClimbThroughWindowState.instance());
										} else {
											printString = "Climb through";
										}
									}
								} else if (object instanceof IsoThumpable && !object.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
									thumpable = (IsoThumpable)object;
									if (Keyboard.isKeyDown(42)) {
										curtain = thumpable.HasCurtains();
										if (curtain != null && this.current != null && !curtain.getSquare().isBlockedTo(this.current)) {
											curtain.ToggleDoor(this);
										}
									} else if (this.timePressedContext >= 0.5F) {
										if (!thumpable.isBarricaded() && !boolean1 && !this.isBlockMovement()) {
											this.StateMachineParams.clear();
											this.StateMachineParams.put(0, thumpable);
											this.changeState(ClimbThroughWindowState.instance());
										}
									} else {
										if (!this.isSafeToClimbOver(directions) && !object.getSquare().haveSheetRope && !boolean3) {
											return false;
										}

										if (!boolean1 && !thumpable.isBarricaded() && !this.isBlockMovement()) {
											this.StateMachineParams.clear();
											this.StateMachineParams.put(0, thumpable);
											this.changeState(ClimbThroughWindowState.instance());
										} else {
											printString = "Climb through";
										}
									}
								} else if (IsoWindowFrame.isWindowFrame(object) && (this.timePressedContext >= 0.5F || this.isSafeToClimbOver(directions) || boolean3) && !boolean1 && !this.isBlockMovement()) {
									this.StateMachineParams.clear();
									this.StateMachineParams.put(0, object);
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
		} else if (directions == IsoDirections.N && this.getCurrentSquare().Is(IsoFlagType.HoppableN)) {
			if (boolean1) {
				return true;
			} else {
				this.StateMachineParams.clear();
				this.StateMachineParams.put(0, directions);
				this.changeState(ClimbOverFenceState.instance());
				return true;
			}
		} else if (directions == IsoDirections.W && this.getCurrentSquare().Is(IsoFlagType.HoppableW)) {
			if (boolean1) {
				return true;
			} else {
				this.StateMachineParams.clear();
				this.StateMachineParams.put(0, directions);
				this.changeState(ClimbOverFenceState.instance());
				return true;
			}
		} else if (directions == IsoDirections.S && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z).Is(IsoFlagType.HoppableN)) {
			if (boolean1) {
				return true;
			} else {
				this.StateMachineParams.clear();
				this.StateMachineParams.put(0, directions);
				this.changeState(ClimbOverFenceState.instance());
				return true;
			}
		} else if (directions == IsoDirections.E && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z) != null && IsoWorld.instance.CurrentCell.getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z).Is(IsoFlagType.HoppableW)) {
			if (boolean1) {
				return true;
			} else {
				this.StateMachineParams.clear();
				this.StateMachineParams.put(0, directions);
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

	public boolean DoAttack(float float1) {
		return this.DoAttack(float1, false, (String)null);
	}

	public boolean DoAttack(float float1, boolean boolean1, String string) {
		this.setForceShove(boolean1);
		this.setClickSound(string);
		if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance() && !(this.getRecoilDelay() > 0.0F)) {
			if (boolean1) {
				float1 *= 2.0F;
			}

			if (float1 > 90.0F) {
				float1 = 90.0F;
			}

			float1 /= 25.0F;
			this.useChargeDelta = float1;
			if (this instanceof IsoPlayer) {
				this.FakeAttack = false;
			}

			if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() < 0.0F)) {
				if (this.AttackDelay <= 0.0F && (!this.sprite.CurrentAnim.name.contains("Attack") || this.def.Frame >= (float)(this.sprite.CurrentAnim.Frames.size() - 1)) || this.def.Frame == 0.0F) {
					Object object = this.leftHandItem;
					if (object == null || !(object instanceof HandWeapon) || boolean1) {
						object = this.bareHands;
					}

					if (object instanceof HandWeapon) {
						this.useHandWeapon = (HandWeapon)object;
						int int1 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
						if (this.useHandWeapon.isCantAttackWithLowestEndurance() && int1 == 4) {
							return false;
						}

						if (this.PlayerIndex == 0 && this.JoypadBind == -1 && UIManager.getPicked() != null) {
							this.attackTargetSquare = UIManager.getPicked().square;
							if (UIManager.getPicked().tile instanceof IsoMovingObject) {
								this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
							}
						}

						if (this.useHandWeapon.getOtherHandRequire() == null || this.rightHandItem != null && this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire())) {
							float float2 = this.useHandWeapon.getSwingTime();
							if (this.useHandWeapon.isCantAttackWithLowestEndurance() && int1 == 4) {
								return false;
							}

							if (this.useHandWeapon.isUseEndurance()) {
								switch (int1) {
								case 1: 
									float2 *= 1.1F;
									break;
								
								case 2: 
									float2 *= 1.2F;
									break;
								
								case 3: 
									float2 *= 1.3F;
									break;
								
								case 4: 
									float2 *= 1.4F;
								
								}
							}

							if (float2 < this.useHandWeapon.getMinimumSwingTime()) {
								float2 = this.useHandWeapon.getMinimumSwingTime();
							}

							float2 *= this.useHandWeapon.getSpeedMod(this);
							float2 *= 1.0F / GameTime.instance.getMultiplier();
							if (this.HasTrait("BaseballPlayer") && this.useHandWeapon.getType().contains("Baseball")) {
								float2 *= 0.8F;
							}

							this.AttackDelayMax = this.AttackDelay = float2 * GameTime.instance.getMultiplier() * 0.6F;
							this.AttackDelayUse = this.AttackDelayMax * this.useHandWeapon.getDoSwingBeforeImpact();
							if (this.AttackDelayUse == 0.0F) {
								this.AttackDelayUse = 0.2F;
							}

							this.AttackDelay = 0.0F;
							this.AttackWasSuperAttack = this.superAttack;
							if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
								this.setRecoilDelay((float)(this.useHandWeapon.getRecoilDelay() - this.getPerkLevel(PerkFactory.Perks.Aiming) * 2));
								if (boolean1) {
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
		int int1 = 0;
		this.NumSurvivorsInVicinity = 0;
		int int2 = this.getCell().getObjectList().size();
		for (int int3 = 0; int3 < int2; ++int3) {
			IsoMovingObject movingObject = (IsoMovingObject)this.getCell().getObjectList().get(int3);
			if (!(movingObject instanceof IsoPhysicsObject) && !(movingObject instanceof BaseVehicle)) {
				if (movingObject == this) {
					this.spottedList.add(movingObject);
				} else {
					float float1 = IsoUtils.DistanceManhatten(movingObject.getX(), movingObject.getY(), this.getX(), this.getY());
					if (float1 < 20.0F) {
						++int1;
					}

					if (movingObject.getCurrentSquare() != null) {
						if (this.getCurrentSquare() == null) {
							return;
						}

						boolean boolean1 = GameServer.bServer ? ServerLOS.instance.isCouldSee(this, movingObject.getCurrentSquare()) : movingObject.getCurrentSquare().isCouldSee(this.PlayerIndex);
						boolean boolean2 = GameServer.bServer ? boolean1 : movingObject.getCurrentSquare().isCanSee(this.PlayerIndex);
						if (this.isAsleep() || !boolean2 && (!(float1 < 2.5F) || !boolean1)) {
							if (movingObject != instance) {
								movingObject.targetAlpha[getPlayerIndex()] = 0.0F;
							}

							if (boolean1) {
								this.TestZombieSpotPlayer(movingObject);
							}
						} else {
							this.TestZombieSpotPlayer(movingObject);
							if (movingObject instanceof IsoGameCharacter && ((IsoGameCharacter)movingObject).SpottedSinceAlphaZero[this.PlayerIndex]) {
								if (movingObject instanceof IsoSurvivor) {
									++this.NumSurvivorsInVicinity;
								}

								if (movingObject instanceof IsoZombie) {
									this.lastSeenZombieTime = 0.0;
									if (movingObject.getZ() >= this.getZ() - 1.0F && float1 < 7.0F && !((IsoZombie)((IsoZombie)movingObject)).Ghost && !((IsoZombie)((IsoZombie)movingObject)).isFakeDead() && movingObject.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom()) {
										this.TicksSinceSeenZombie = 0;
										++this.stats.NumVisibleZombies;
									}
								}

								this.spottedList.add(movingObject);
								movingObject.targetAlpha[getPlayerIndex()] = 1.0F;
								float float2 = 4.0F;
								if (this.stats.NumVisibleZombies > 4) {
									float2 = 7.0F;
								}

								if (float1 < float2 * 2.0F && movingObject instanceof IsoZombie && (int)movingObject.getZ() == (int)this.getZ() && !this.GhostMode && !GameClient.bClient) {
									GameTime.instance.setMultiplier(1.0F);
									UIManager.getSpeedControls().SetCurrentGameSpeed(1);
								}

								if (float1 < float2 && movingObject instanceof IsoZombie && (int)movingObject.getZ() == (int)this.getZ() && !this.LastSpotted.contains(movingObject)) {
									Stats stats = this.stats;
									stats.NumVisibleZombies += 2;
								}
							} else {
								movingObject.targetAlpha[getPlayerIndex()] = 1.0F;
							}
						}

						if (this.GhostMode) {
							movingObject.alpha[getPlayerIndex()] = movingObject.targetAlpha[getPlayerIndex()] = 1.0F;
						}

						if (movingObject instanceof IsoGameCharacter && ((IsoGameCharacter)movingObject).invisible && this.accessLevel.equals("")) {
							movingObject.alpha[getPlayerIndex()] = movingObject.targetAlpha[getPlayerIndex()] = 0.0F;
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
		float float3 = (float)int1 / 20.0F;
		if (float3 > 1.0F) {
			float3 = 1.0F;
		}

		float3 *= 0.6F;
		SoundManager.instance.BlendVolume(MainScreenState.ambient, float3);
		int int4 = 0;
		for (int int5 = 0; int5 < this.spottedList.size(); ++int5) {
			if (!this.LastSpotted.contains(this.spottedList.get(int5))) {
				this.LastSpotted.add(this.spottedList.get(int5));
			}

			if (this.spottedList.get(int5) instanceof IsoZombie) {
				++int4;
			}
		}

		if (this.ClearSpottedTimer <= 0 && int4 == 0) {
			this.LastSpotted.clear();
			this.ClearSpottedTimer = 1000;
		} else {
			--this.ClearSpottedTimer;
		}
	}

	void DoHotKey(int int1, int int2) {
		if (GameKeyboard.isKeyDown(int2)) {
			if (GameKeyboard.isKeyDown(42) && GameKeyboard.isKeyDown(29)) {
				UIManager.setDoMouseControls(true);
				if (this.leftHandItem != null) {
					this.MainHot[int1] = this.leftHandItem.getType();
				} else {
					this.MainHot[int1] = null;
				}

				if (this.rightHandItem != null) {
					this.SecHot[int1] = this.rightHandItem.getType();
				} else {
					this.SecHot[int1] = null;
				}
			} else {
				this.leftHandItem = this.inventory.FindAndReturn(this.MainHot[int1]);
				this.rightHandItem = this.inventory.FindAndReturn(this.SecHot[int1]);
			}
		}
	}

	private void PressedA() {
		IsoObject object = this.getInteract();
		if (object != null) {
			if (object instanceof IsoDoor) {
				((IsoDoor)object).ToggleDoor(this);
			}

			if (object instanceof IsoThumpable) {
				((IsoThumpable)object).ToggleDoor(this);
			}

			if (object.container != null) {
				object.onMouseLeftClick(0, 0);
			}
		}

		this.DebounceA = true;
	}

	public void saveGame() {
	}

	public int getAngleCounter() {
		return this.angleCounter;
	}

	public void setAngleCounter(int int1) {
		this.angleCounter = int1;
	}

	public Vector2 getLastAngle() {
		return this.lastAngle;
	}

	public void setLastAngle(Vector2 vector2) {
		this.lastAngle = vector2;
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

	public boolean isFakeAttack() {
		return this.FakeAttack;
	}

	public void setFakeAttack(boolean boolean1) {
		this.FakeAttack = boolean1;
	}

	public IsoObject getFakeAttackTarget() {
		return this.FakeAttackTarget;
	}

	public void setFakeAttackTarget(IsoObject object) {
		this.FakeAttackTarget = object;
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

	public String[] getMainHot() {
		return this.MainHot;
	}

	public void setMainHot(String[] stringArray) {
		this.MainHot = stringArray;
	}

	public String[] getSecHot() {
		return this.SecHot;
	}

	public void setSecHot(String[] stringArray) {
		this.SecHot = stringArray;
	}

	public Stack getSpottedList() {
		return this.spottedList;
	}

	public void setSpottedList(Stack stack) {
		this.spottedList = stack;
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

	public Stack getLastPos() {
		return this.lastPos;
	}

	public void setLastPos(Stack stack) {
		this.lastPos = stack;
	}

	public boolean isbDebounceLMB() {
		return this.bDebounceLMB;
	}

	public void setbDebounceLMB(boolean boolean1) {
		this.bDebounceLMB = boolean1;
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

	public IsoSprite getGuardModeUISprite() {
		return this.GuardModeUISprite;
	}

	public void setGuardModeUISprite(IsoSprite sprite) {
		this.GuardModeUISprite = sprite;
	}

	public int getGuardModeUI() {
		return this.GuardModeUI;
	}

	public void setGuardModeUI(int int1) {
		this.GuardModeUI = int1;
	}

	public IsoSurvivor getGuardChosen() {
		return this.GuardChosen;
	}

	public void setGuardChosen(IsoSurvivor survivor) {
		this.GuardChosen = survivor;
	}

	public IsoGridSquare getGuardStand() {
		return this.GuardStand;
	}

	public void setGuardStand(IsoGridSquare square) {
		this.GuardStand = square;
	}

	public IsoGridSquare getGuardFace() {
		return this.GuardFace;
	}

	public void setGuardFace(IsoGridSquare square) {
		this.GuardFace = square;
	}

	public boolean isbSneaking() {
		return this.bSneaking;
	}

	public void setbSneaking(boolean boolean1) {
		this.bSneaking = boolean1;
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

	public int getTimeSinceLastStab() {
		return this.timeSinceLastStab;
	}

	public void setTimeSinceLastStab(int int1) {
		this.timeSinceLastStab = int1;
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
		return this.bRunning;
	}

	public void InitSpriteParts() {
		SurvivorDesc survivorDesc = this.descriptor;
		this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
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
				float float1 = JoypadManager.instance.getMovementAxisY(this.JoypadBind);
				float float2 = JoypadManager.instance.getMovementAxisX(this.JoypadBind);
				float float3 = JoypadManager.instance.getDeadZone(this.JoypadBind, 0);
				if (Math.abs(float1) > float3 || Math.abs(float2) > float3) {
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
			float float1 = JoypadManager.instance.getAimingAxisY(this.JoypadBind);
			float float2 = JoypadManager.instance.getAimingAxisX(this.JoypadBind);
			if (Math.abs(float1) > 0.1F || Math.abs(float2) > 0.1F) {
				return true;
			}
		}

		return false;
	}

	public static int getPlayerIndex() {
		return instance == null ? assumedPlayer : instance.PlayerIndex;
	}

	public void setSteamID(long long1) {
		this.steamID = long1;
	}

	public long getSteamID() {
		return this.steamID;
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
		for (int int1 = 0; int1 < numPlayers; ++int1) {
			if (players[int1] == this) {
				return true;
			}
		}

		return false;
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

	public void setNoClip(boolean boolean1) {
		this.noClip = boolean1;
	}

	public static boolean getCoopPVP() {
		return CoopPVP;
	}

	public static void setCoopPVP(boolean boolean1) {
		CoopPVP = boolean1;
	}

	public void setAuthorizeMeleeAction(boolean boolean1) {
		this.authorizeMeleeAction = boolean1;
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

	private void updateHeartSound() {
		GameSound gameSound = GameSounds.getSound("HeartBeat");
		boolean boolean1 = gameSound != null && gameSound.userVolume > 0.0F;
		if (!this.Asleep && boolean1 && GameTime.getInstance().getTrueMultiplier() == 1.0F) {
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
		ArrayList arrayList = this.vehicleContainerData.tempContainers;
		arrayList.clear();
		int int1 = (int)this.getX() - 4;
		int int2 = (int)this.getY() - 4;
		int int3 = (int)this.getX() + 4;
		int int4 = (int)this.getY() + 4;
		int int5 = int1 / 10;
		int int6 = int2 / 10;
		int int7 = (int)Math.ceil((double)(int3 / 10));
		int int8 = (int)Math.ceil((double)(int4 / 10));
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

	public void addSmallInjuries() {
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
		byteBufferWriter.putUTF(this.getTagPrefix());
		if (this.accessLevel.equals("")) {
			this.GhostMode = false;
			this.invisible = false;
			this.godMod = false;
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
		String string7 = GameWindow.ReadString(byteBuffer);
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
				string8 = string + " Changed " + string2 + " profession in " + string5;
			} else {
				string8 = "Changed your profession in " + string5;
			}
		}

		this.getDescriptor().setProfession(string5);
		if (!this.accessLevel.equals(string6)) {
			if (GameServer.bServer) {
				(new StringBuilder()).append(string).append(" Changed ").append(this.getDisplayName()).append(" access level in ").append(string6).toString();
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
				this.GhostMode = false;
				this.invisible = false;
				this.godMod = false;
			}

			string8 = "Changed access level in " + string6;
			this.accessLevel = string6;
		}

		if (!this.getDisplayName().equals(string2)) {
			if (GameServer.bServer) {
				string8 = string + " Changed display name " + this.getDisplayName() + " in " + string2;
				ServerWorldDatabase.instance.updateDisplayName(this.username, string2);
			} else {
				string8 = "Changed your display name in " + string2;
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

	public void setUsername(String string) {
		this.username = string;
	}

	public void setAccessLevel(String string) {
		this.accessLevel = string;
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
		this.tagColor = colorInfo;
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
				this.displayName = this.username;
			}
		} else if (!GameServer.bServer) {
			this.displayName = this.username;
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

	public boolean isFactionPvp() {
		return this.factionPvp;
	}

	public void setShowTag(boolean boolean1) {
		this.showTag = boolean1;
	}

	public void setFactionPvp(boolean boolean1) {
		this.factionPvp = boolean1;
	}

	public boolean isForceRun() {
		return this.forceRun;
	}

	public void setForceRun(boolean boolean1) {
		this.forceRun = boolean1;
	}

	public boolean isDeaf() {
		return this.getTraits().contains("Deaf");
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

	public boolean isWearingNightVisionGoggles() {
		return this.isWearingNightVisionGoggles;
	}

	public void setWearingNightVisionGoggles(boolean boolean1) {
		this.isWearingNightVisionGoggles = boolean1;
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

	private static class VehicleContainerData {
		ArrayList tempContainers;
		ArrayList containers;
		Stack freeContainers;

		private VehicleContainerData() {
			this.tempContainers = new ArrayList();
			this.containers = new ArrayList();
			this.freeContainers = new Stack();
		}

		VehicleContainerData(Object object) {
			this();
		}
	}

	private static class VehicleContainer {
		BaseVehicle vehicle;
		int containerIndex;

		private VehicleContainer() {
		}

		public IsoPlayer.VehicleContainer set(BaseVehicle baseVehicle, int int1) {
			this.vehicle = baseVehicle;
			this.containerIndex = int1;
			return this;
		}

		public boolean equals(Object object) {
			return object instanceof IsoPlayer.VehicleContainer && this.vehicle == ((IsoPlayer.VehicleContainer)object).vehicle && this.containerIndex == ((IsoPlayer.VehicleContainer)object).containerIndex;
		}

		VehicleContainer(Object object) {
			this();
		}
	}
}
