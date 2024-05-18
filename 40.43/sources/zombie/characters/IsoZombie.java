package zombie.characters;

import fmod.javafmod;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.PathfindManager;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.ai.ZombieGroupManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.ai.states.AttackState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverFenceState2;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbThroughWindowState2;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.IdleState;
import zombie.ai.states.JustDieState;
import zombie.ai.states.LungeState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ReanimatePlayerState;
import zombie.ai.states.ReanimateState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieStandState;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.vehicles.AttackVehicleState;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindState2;
import zombie.vehicles.PolygonalMap2;


public class IsoZombie extends IsoGameCharacter {
	public static final byte NetRemoteState_Idle = 1;
	public static final byte NetRemoteState_Walk = 2;
	public static final byte NetRemoteState_Stagger = 3;
	public static final byte NetRemoteState_Lunge = 4;
	public static final byte NetRemoteState_Bite = 5;
	public static final byte NetRemoteState_WalkToward = 6;
	public static final byte NetRemoteState_StaggerBack = 7;
	public static final byte NetRemoteState_StaggerBackDie = 8;
	public long zombieSoundInstance;
	public static float baseSpeed = 0.029F;
	static int AllowRepathDelayMax = 120;
	public static int ZombieDeaths = 0;
	public int HurtPlayerTimer;
	public int LastTargetSeenX;
	public int LastTargetSeenY;
	public int LastTargetSeenZ;
	public boolean Ghost;
	public float LungeTimer;
	public long LungeSoundTime;
	public IsoMovingObject target;
	public int iIgnoreDirectionChange;
	public float TimeSinceSeenFlesh;
	public int FollowCount;
	public float GhostLife;
	public float wanderSpeed;
	public float predXVel;
	public float predYVel;
	public int ZombieID;
	public boolean bRightie;
	private int BonusSpotTime;
	public boolean bDead;
	private boolean bFakeDead;
	private boolean bForceFakeDead;
	private boolean bReanimatedPlayer;
	public boolean bIndoorZombie;
	public int thumpFrame;
	public int thumpFlag;
	public boolean thumpSent;
	public boolean mpIdleSound;
	public static final float EAT_BODY_TIME = 3600.0F;
	public static final float LUNGE_TIME = 180.0F;
	public static final float CRAWLER_DAMAGE_DOT = 0.9F;
	public static final float CRAWLER_DAMAGE_RANGE = 1.5F;
	private boolean useless;
	public int speedType;
	public ZombieGroup group;
	public boolean inactive;
	public int strength;
	public int cognition;
	private ArrayList itemsToSpawnAtDeath;
	public String serverState;
	public IsoObject soundSourceTarget;
	public float soundAttract;
	public float soundAttractTimeout;
	private BaseVehicle vehicle4testCollision;
	public String SpriteName;
	public static final int PALETTE_COUNT = 3;
	public Vector2 vectorToTarget;
	public float AllowRepathDelay;
	IsoDirections lastDir;
	IsoDirections lastlastDir;
	public boolean KeepItReal;
	public boolean Deaf;
	public static boolean Fast = false;
	public int palette;
	public int AttackAnimTime;
	public static int AttackAnimTimeMax = 50;
	boolean GhostShow;
	public static int HighQualityZombiesDrawnThisFrame = 0;
	public float nextRallyTime;
	public boolean chasingSound;
	public IsoMovingObject spottedLast;
	OnceEvery spottedPlayer;
	public int spotSoundDelay;
	public float movex;
	public float movey;
	private int stepFrameLast;
	OnceEvery networkUpdate;
	OnceEvery networkUpdate2;
	OnceEvery networkUpdate3;
	static Vector2 move = new Vector2(0.0F, 0.0F);
	static Vector2 predTest = new Vector2();
	static HandWeapon w = null;
	public short lastRemoteUpdate;
	public short OnlineID;
	public boolean usingSoundInstance;
	float timeSinceRespondToSound;
	ArrayList doneGrids;
	ArrayList choiceGrids;
	public String walkVariantUse;
	public String walkVariant;
	public boolean bLunger;
	public boolean bRunning;
	public boolean bCrawling;
	public int MoveDelay;
	public boolean bRemote;
	private static final IsoZombie.FloodFill floodFill = new IsoZombie.FloodFill();
	private static IsoZombie ImmortalTutorialZombie;

	public String getObjectName() {
		return "Zombie";
	}

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

	public void setVehicle4TestCollision(BaseVehicle baseVehicle) {
		this.vehicle4testCollision = baseVehicle;
	}

	public static String getMD5Checksum(String string) throws Exception {
		byte[] byteArray = createChecksum(string);
		String string2 = "";
		for (int int1 = 0; int1 < byteArray.length; ++int1) {
			string2 = string2 + Integer.toString((byteArray[int1] & 255) + 256, 16).substring(1);
		}

		return string2;
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

	public IsoZombie(IsoCell cell) {
		this(cell, (SurvivorDesc)null, -1);
	}

	public IsoZombie(IsoCell cell, SurvivorDesc survivorDesc, int int1) {
		super(cell, 0.0F, 0.0F, 0.0F);
		this.zombieSoundInstance = -1L;
		this.HurtPlayerTimer = 10;
		this.LastTargetSeenX = -1;
		this.LastTargetSeenY = -1;
		this.LastTargetSeenZ = -1;
		this.Ghost = false;
		this.LungeTimer = 0.0F;
		this.LungeSoundTime = 0L;
		this.iIgnoreDirectionChange = 0;
		this.TimeSinceSeenFlesh = 100000.0F;
		this.FollowCount = 0;
		this.GhostLife = 0.0F;
		this.wanderSpeed = 0.018F;
		this.predXVel = 0.0F;
		this.predYVel = 0.0F;
		this.ZombieID = 0;
		this.bRightie = false;
		this.BonusSpotTime = 0;
		this.bDead = false;
		this.bFakeDead = false;
		this.bForceFakeDead = false;
		this.bReanimatedPlayer = false;
		this.bIndoorZombie = false;
		this.thumpFrame = -1;
		this.thumpFlag = 0;
		this.thumpSent = false;
		this.mpIdleSound = false;
		this.useless = false;
		this.speedType = -1;
		this.inactive = false;
		this.strength = -1;
		this.cognition = -1;
		this.itemsToSpawnAtDeath = null;
		this.serverState = new String("-");
		this.soundSourceTarget = null;
		this.soundAttract = 0.0F;
		this.soundAttractTimeout = 0.0F;
		this.vehicle4testCollision = null;
		this.SpriteName = "BobZ";
		this.vectorToTarget = new Vector2();
		this.AllowRepathDelay = 0.0F;
		this.KeepItReal = false;
		this.Deaf = false;
		this.palette = 0;
		this.AttackAnimTime = 50;
		this.GhostShow = false;
		this.chasingSound = false;
		this.spottedLast = null;
		this.spottedPlayer = new OnceEvery(0.7F, true);
		this.spotSoundDelay = 0;
		this.stepFrameLast = -1;
		this.networkUpdate = new OnceEvery(1.0F);
		this.networkUpdate2 = new OnceEvery(0.5F);
		this.networkUpdate3 = new OnceEvery(1.0F);
		this.lastRemoteUpdate = 0;
		this.OnlineID = -1;
		this.timeSinceRespondToSound = 1000000.0F;
		this.doneGrids = new ArrayList();
		this.choiceGrids = new ArrayList();
		this.walkVariantUse = null;
		this.walkVariant = "ZombieWalk";
		this.MoveDelay = 0;
		this.Health = 1.8F + Rand.Next(0.0F, 0.3F);
		this.weight = 0.7F;
		this.dir = IsoDirections.fromIndex(Rand.Next(8));
		int int2 = Rand.Next(10) + 1;
		if (survivorDesc != null) {
			this.descriptor = survivorDesc;
			this.palette = int1;
		} else {
			this.descriptor = SurvivorFactory.CreateSurvivor();
			this.palette = Rand.Next(3) + 1;
		}

		this.bFemale = this.descriptor.isFemale();
		this.SpriteName = this.bFemale ? "KateZ" : "BobZ";
		if (this.palette != 1) {
			this.SpriteName = this.SpriteName + this.palette;
		}

		this.InitSpritePartsZombie();
		this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.defaultState = ZombieStandState.instance();
		this.setFakeDead(false);
		this.stateMachine.changeState(this.defaultState);
		this.DoZombieStats();
		this.width = 0.3F;
		this.targetAlpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		this.finder.maxSearchDistance = 20;
		this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		if (this.bFemale) {
			this.hurtSound = "FemaleZombieHurt";
		}
	}

	@Deprecated
	public IsoZombie(IsoCell cell, SurvivorDesc survivorDesc) {
		super(cell, 1.0F, 0.0F, 0.0F);
		this.zombieSoundInstance = -1L;
		this.HurtPlayerTimer = 10;
		this.LastTargetSeenX = -1;
		this.LastTargetSeenY = -1;
		this.LastTargetSeenZ = -1;
		this.Ghost = false;
		this.LungeTimer = 0.0F;
		this.LungeSoundTime = 0L;
		this.iIgnoreDirectionChange = 0;
		this.TimeSinceSeenFlesh = 100000.0F;
		this.FollowCount = 0;
		this.GhostLife = 0.0F;
		this.wanderSpeed = 0.018F;
		this.predXVel = 0.0F;
		this.predYVel = 0.0F;
		this.ZombieID = 0;
		this.bRightie = false;
		this.BonusSpotTime = 0;
		this.bDead = false;
		this.bFakeDead = false;
		this.bForceFakeDead = false;
		this.bReanimatedPlayer = false;
		this.bIndoorZombie = false;
		this.thumpFrame = -1;
		this.thumpFlag = 0;
		this.thumpSent = false;
		this.mpIdleSound = false;
		this.useless = false;
		this.speedType = -1;
		this.inactive = false;
		this.strength = -1;
		this.cognition = -1;
		this.itemsToSpawnAtDeath = null;
		this.serverState = new String("-");
		this.soundSourceTarget = null;
		this.soundAttract = 0.0F;
		this.soundAttractTimeout = 0.0F;
		this.vehicle4testCollision = null;
		this.SpriteName = "BobZ";
		this.vectorToTarget = new Vector2();
		this.AllowRepathDelay = 0.0F;
		this.KeepItReal = false;
		this.Deaf = false;
		this.palette = 0;
		this.AttackAnimTime = 50;
		this.GhostShow = false;
		this.chasingSound = false;
		this.spottedLast = null;
		this.spottedPlayer = new OnceEvery(0.7F, true);
		this.spotSoundDelay = 0;
		this.stepFrameLast = -1;
		this.networkUpdate = new OnceEvery(1.0F);
		this.networkUpdate2 = new OnceEvery(0.5F);
		this.networkUpdate3 = new OnceEvery(1.0F);
		this.lastRemoteUpdate = 0;
		this.OnlineID = -1;
		this.timeSinceRespondToSound = 1000000.0F;
		this.doneGrids = new ArrayList();
		this.choiceGrids = new ArrayList();
		this.walkVariantUse = null;
		this.walkVariant = "ZombieWalk";
		this.MoveDelay = 0;
		this.Health = 1.8F + Rand.Next(0.0F, 0.3F);
		this.bCrawling = Rand.Next(40) == 0;
		this.weight = 0.7F;
		this.dir = IsoDirections.fromIndex(Rand.Next(8));
		int int1 = Rand.Next(10) + 1;
		int int2 = Rand.Next(3) + 1;
		this.palette = int2;
		if (int2 != 1) {
			this.SpriteName = this.SpriteName + int2;
		}

		this.bFemale = survivorDesc.isFemale();
		if (this.bFemale) {
			if (int2 == 1) {
				this.SpriteName = "KateZ";
			} else {
				this.SpriteName = "KateZ" + int2;
			}
		}

		this.InitSpritePartsZombie(this.SpriteName, survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
		this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.defaultState = ZombieStandState.instance();
		this.stateMachine.changeState(this.defaultState);
		this.DoZombieStats();
		this.width = 0.3F;
		this.targetAlpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		this.finder.maxSearchDistance = 20;
		this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		if (this.bFemale) {
			this.hurtSound = "FemaleZombieHurt";
		}

		this.vehicle4testCollision = null;
	}

	public void pathToCharacter(IsoGameCharacter gameCharacter) {
		if (!(this.AllowRepathDelay > 0.0F) || this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance()) {
			super.pathToCharacter(gameCharacter);
		}
	}

	public void pathToLocation(int int1, int int2, int int3) {
		super.pathToLocation(int1, int2, int3);
	}

	public void pathToLocationF(float float1, float float2, float float3) {
		if (!(this.AllowRepathDelay > 0.0F) || this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance()) {
			super.pathToLocationF(float1, float2, float3);
		}
	}

	@Deprecated
	public IsoZombie(IsoCell cell, int int1) {
		super(cell, 0.0F, 0.0F, 0.0F);
		this.zombieSoundInstance = -1L;
		this.HurtPlayerTimer = 10;
		this.LastTargetSeenX = -1;
		this.LastTargetSeenY = -1;
		this.LastTargetSeenZ = -1;
		this.Ghost = false;
		this.LungeTimer = 0.0F;
		this.LungeSoundTime = 0L;
		this.iIgnoreDirectionChange = 0;
		this.TimeSinceSeenFlesh = 100000.0F;
		this.FollowCount = 0;
		this.GhostLife = 0.0F;
		this.wanderSpeed = 0.018F;
		this.predXVel = 0.0F;
		this.predYVel = 0.0F;
		this.ZombieID = 0;
		this.bRightie = false;
		this.BonusSpotTime = 0;
		this.bDead = false;
		this.bFakeDead = false;
		this.bForceFakeDead = false;
		this.bReanimatedPlayer = false;
		this.bIndoorZombie = false;
		this.thumpFrame = -1;
		this.thumpFlag = 0;
		this.thumpSent = false;
		this.mpIdleSound = false;
		this.useless = false;
		this.speedType = -1;
		this.inactive = false;
		this.strength = -1;
		this.cognition = -1;
		this.itemsToSpawnAtDeath = null;
		this.serverState = new String("-");
		this.soundSourceTarget = null;
		this.soundAttract = 0.0F;
		this.soundAttractTimeout = 0.0F;
		this.vehicle4testCollision = null;
		this.SpriteName = "BobZ";
		this.vectorToTarget = new Vector2();
		this.AllowRepathDelay = 0.0F;
		this.KeepItReal = false;
		this.Deaf = false;
		this.palette = 0;
		this.AttackAnimTime = 50;
		this.GhostShow = false;
		this.chasingSound = false;
		this.spottedLast = null;
		this.spottedPlayer = new OnceEvery(0.7F, true);
		this.spotSoundDelay = 0;
		this.stepFrameLast = -1;
		this.networkUpdate = new OnceEvery(1.0F);
		this.networkUpdate2 = new OnceEvery(0.5F);
		this.networkUpdate3 = new OnceEvery(1.0F);
		this.lastRemoteUpdate = 0;
		this.OnlineID = -1;
		this.timeSinceRespondToSound = 1000000.0F;
		this.doneGrids = new ArrayList();
		this.choiceGrids = new ArrayList();
		this.walkVariantUse = null;
		this.walkVariant = "ZombieWalk";
		this.MoveDelay = 0;
		this.bCrawling = Rand.Next(40) == 0;
		this.Health = 1.5F + Rand.Next(0.0F, 0.3F);
		this.palette = int1;
		this.dir = IsoDirections.fromIndex(Rand.Next(8));
		this.weight = 0.7F;
		String string = "Zombie_palette";
		if (int1 == 10) {
			string = string + "10";
		} else {
			string = string + "0" + Integer.toString(int1);
		}

		int int2 = Rand.Next(3) + 1;
		if (int2 != 1) {
			this.SpriteName = this.SpriteName + int2;
		}

		if (this.bFemale) {
			if (int2 == 1) {
				this.SpriteName = "KateZ";
			} else {
				this.SpriteName = "KateZ" + int2;
			}
		}

		this.palette = int2;
		SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
		this.InitSpritePartsZombie(this.SpriteName, survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
		this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.defaultState = ZombieStandState.instance();
		this.stateMachine.changeState(this.defaultState);
		this.DoZombieStats();
		this.width = 0.3F;
		this.finder.maxSearchDistance = 20;
		this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		if (this.bFemale) {
			this.hurtSound = "FemaleZombieHurt";
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.palette = byteBuffer.getInt();
		int int2 = this.palette;
		String string = "Zombie_palette";
		if (int2 == 10) {
			string = string + "10";
		} else {
			(new StringBuilder()).append(string).append("0").append(Integer.toString(int2)).toString();
		}

		this.walkVariant = "ZombieWalk";
		this.SpriteName = "BobZ";
		if (this.palette != 1) {
			this.SpriteName = this.SpriteName + this.palette;
		}

		SurvivorDesc survivorDesc = this.descriptor;
		this.bFemale = survivorDesc.isFemale();
		if (this.bFemale) {
			if (this.palette == 1) {
				this.SpriteName = "KateZ";
			} else {
				this.SpriteName = "KateZ" + this.palette;
			}
		}

		if (this.bFemale) {
			this.hurtSound = "FemaleZombieHurt";
		} else {
			this.hurtSound = "MaleZombieHurt";
		}

		this.InitSpritePartsZombie(this.SpriteName, survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
		this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.defaultState = ZombieStandState.instance();
		this.DoZombieStats();
		this.PathSpeed = byteBuffer.getFloat();
		this.setWidth(0.3F);
		this.TimeSinceSeenFlesh = (float)byteBuffer.getInt();
		this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
		this.setFakeDead(byteBuffer.getInt() == 1);
		this.stateMachine.Lock = false;
		this.stateMachine.changeState(this.defaultState);
		this.getCell().getZombieList().add(this);
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.putInt(this.palette);
		byteBuffer.putFloat(this.PathSpeed);
		byteBuffer.putInt((int)this.TimeSinceSeenFlesh);
		if (this.bCrawling) {
			byteBuffer.putInt(1);
		} else {
			byteBuffer.putInt(this.isFakeDead() ? 1 : 0);
		}
	}

	public boolean AttemptAttack() {
		if (this.stateMachine.getCurrent() != AttackState.instance() && this.getCurrentState() != AttackVehicleState.instance()) {
			if (!this.Ghost) {
				if (GameServer.bServer && this instanceof IsoZombie) {
					GameServer.sendZombie(this);
				}

				if (this.target != null && this.target instanceof IsoGameCharacter) {
					IsoGameCharacter gameCharacter = (IsoGameCharacter)this.target;
					BaseVehicle baseVehicle = gameCharacter.getVehicle();
					if (baseVehicle != null) {
						if (this.bCrawling) {
							this.changeState(ZombieStandState.instance());
						} else {
							this.changeState(AttackVehicleState.instance());
						}

						return true;
					}
				}

				this.stateMachine.changeState(AttackState.instance());
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void collideWith(IsoObject object) {
		if (!this.Ghost) {
			if (object.rerouteCollide != null) {
				object = this.rerouteCollide;
			}

			if (object instanceof IsoWindow && ((IsoWindow)object).canClimbThrough(this) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
				if ((this.stateMachine.getCurrent() != PathFindState.instance() || this.isOnPath((IsoWindow)object)) && !this.bCrawling) {
					this.StateMachineParams.clear();
					this.StateMachineParams.put(0, object);
					this.getStateMachine().changeState(ClimbThroughWindowState.instance());
				}
			} else if (object instanceof IsoThumpable && ((IsoThumpable)object).isWindow() && !((IsoThumpable)object).isBarricaded() && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
				if ((this.stateMachine.getCurrent() != PathFindState.instance() || this.isOnPath(object)) && !this.bCrawling) {
					this.StateMachineParams.clear();
					this.StateMachineParams.put(0, object);
					this.getStateMachine().changeState(ClimbThroughWindowState.instance());
				}
			} else if (object instanceof Thumpable && (!(object instanceof IsoThumpable) || ((IsoThumpable)object).isThumpable()) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost && !this.bCrawling) {
				if (!SandboxOptions.instance.Lore.ThumpNoChasing.getValue() && this.target == null && !this.chasingSound) {
					this.stateMachine.changeState(ZombieStandState.instance());
				} else {
					if (object instanceof IsoThumpable && !SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
						return;
					}

					this.setThumpTarget((Thumpable)object);
					this.path = null;
					this.stateMachine.changeState(ThumpState.instance());
				}
			}

			State state = this.getCurrentState();
			if (!this.bCrawling && IsoWindowFrame.isWindowFrame(object) && (state == PathFindState.instance() || state == LungeState.instance() || state == WalkTowardState.instance()) && (state != PathFindState.instance() || this.isOnPath(object))) {
				this.StateMachineParams.clear();
				this.StateMachineParams.put(0, object);
				this.getStateMachine().changeState(ClimbThroughWindowState.instance());
			}

			super.collideWith(object);
		}
	}

	private boolean isOnPath(IsoObject object) {
		if (this.path != null) {
			for (int int1 = this.pathIndex; int1 < this.path.getLength(); ++int1) {
				Path.Step step = this.path.getStep(int1);
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(step.x, step.y, step.z);
				if (square != null && square.getObjects().contains(object)) {
					return true;
				}
			}
		}

		return false;
	}

	public void Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
		if (!Core.bTutorial || this != ImmortalTutorialZombie) {
			super.Hit(handWeapon, gameCharacter, float1, boolean1, float2);
			if (!(gameCharacter instanceof IsoZombie)) {
				this.target = gameCharacter;
			}

			if (this.Health <= 0.0F && !this.bDead) {
				this.DoZombieInventory();
				LuaEventManager.triggerEvent("OnZombieDead", this);
				this.bDead = true;
			}

			this.TimeSinceSeenFlesh = 0.0F;
			if (!this.bDead && !this.isOnFloor() && !boolean1 && handWeapon != null && handWeapon.getScriptItem().getCategories().contains("Blade") && gameCharacter instanceof IsoPlayer && this.DistToProper(gameCharacter) <= 0.9F && (this.getCurrentState() == AttackState.instance() || this.getCurrentState() == LungeState.instance())) {
				this.setHitForce(0.5F);
				this.stateMachine.changeState(StaggerBackState.instance());
			}
		}
	}

	public void Lunge() {
		if (this.stateMachine.getCurrent() != ThumpState.instance()) {
			if (this.stateMachine.getCurrent() != ClimbThroughWindowState.instance()) {
				if (this.stateMachine.getCurrent() != ClimbThroughWindowState2.instance()) {
					if (this.stateMachine.getCurrent() != ClimbOverFenceState.instance()) {
						if (this.stateMachine.getCurrent() != ClimbOverFenceState2.instance()) {
							if (this.stateMachine.getCurrent() != AttackState.instance()) {
								if (this.stateMachine.getCurrent() == AttackVehicleState.instance()) {
									this.setStateEventDelayTimer(180.0F);
								} else if (this.stateMachine.getCurrent() != StaggerBackDieState.instance()) {
									if (this.stateMachine.getCurrent() != StaggerBackState.instance()) {
										if (this.stateMachine.getCurrent() != LungeState.instance()) {
											if (this.stateMachine.getCurrent() != CrawlingZombieTurnState.instance()) {
												if (this.stateMachine.getCurrent() != FakeDeadZombieState.instance()) {
													if (this.target instanceof IsoGameCharacter) {
														BaseVehicle baseVehicle = ((IsoGameCharacter)this.target).getVehicle();
														if (baseVehicle != null) {
															if (baseVehicle.isCharacterAdjacentTo(this) && this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance()) {
																this.AttemptAttack();
															}

															return;
														}
													}

													if (System.currentTimeMillis() - this.LungeSoundTime > 5000L) {
														String string = "MaleZombieAttack";
														if (this.bFemale) {
															string = "FemaleZombieAttack";
														}

														this.getEmitter().playVocals(string);
														if (GameServer.bServer) {
															GameServer.sendZombieSound(IsoZombie.ZombieSound.Lunge, this);
														}

														this.LungeSoundTime = System.currentTimeMillis();
													}

													this.stateMachine.changeState(LungeState.instance());
													this.LungeTimer = 180.0F;
													if (GameServer.bServer && this instanceof IsoZombie) {
														GameServer.sendZombie(this);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void onMouseLeftClick() {
		if (IsoPlayer.instance == null || !IsoPlayer.instance.isAiming) {
			if (IsoPlayer.instance.IsAttackRange(this.getX(), this.getY(), this.getZ())) {
				Vector2 vector2 = new Vector2(this.getX(), this.getY());
				vector2.x -= IsoPlayer.instance.getX();
				vector2.y -= IsoPlayer.instance.getY();
				vector2.normalize();
				IsoPlayer.instance.DirectionFromVector(vector2);
				IsoPlayer.instance.AttemptAttack();
			}
		}
	}

	public void pathFinished() {
		this.AllowRepathDelay = 0.0F;
		if (this.finder.progress == AStarPathFinder.PathFindProgress.failed) {
			AllowRepathDelayMax = 300;
		} else {
			AllowRepathDelayMax = 30;
		}

		if (!this.isFakeDead()) {
			this.stateMachine.changeState(ZombieStandState.instance());
		}

		GameServer.sendZombie(this);
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		if (IsoCamera.CamCharacter != IsoPlayer.instance) {
			this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
			this.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
		}

		super.render(float1, float2, float3, colorInfo, boolean1);
	}

	public void RespondToSound() {
		if (!this.Ghost) {
			if (!this.Deaf) {
				if (!this.isUseless()) {
					float float1 = 0.0F;
					IsoObject object = null;
					WorldSoundManager.WorldSound worldSound = WorldSoundManager.instance.getSoundZomb(this);
					float float2 = WorldSoundManager.instance.getSoundAttract(worldSound, this);
					if (float2 <= 0.0F) {
						worldSound = null;
					}

					if (worldSound != null) {
						float1 = float2;
						object = worldSound.source;
						this.soundAttract = float2;
						this.soundAttractTimeout = 60.0F;
					} else if (this.soundAttractTimeout > 0.0F) {
						this.soundAttractTimeout -= GameTime.getInstance().getMultiplier() / 1.6F;
						if (this.soundAttractTimeout < 0.0F) {
							this.soundAttractTimeout = 0.0F;
						}
					}

					WorldSoundManager.ResultBiggestSound resultBiggestSound = WorldSoundManager.instance.getBiggestSoundZomb((int)this.getX(), (int)this.getY(), (int)this.getZ(), true, this);
					if (resultBiggestSound.sound != null && (this.soundAttractTimeout == 0.0F || this.soundAttract * 2.0F < resultBiggestSound.attract)) {
						worldSound = resultBiggestSound.sound;
						float1 = resultBiggestSound.attract;
						object = worldSound.source;
					}

					if (worldSound != null) {
						float float3 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), (float)worldSound.x, (float)worldSound.y) / 4.0F;
						if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState2.instance() || this.getCurrentState() == ZombieStandState.instance()) && this.getPathFindBehavior2().isGoal2Location() && !IsoUtils.isSimilarDirection(this, (float)worldSound.x, (float)worldSound.y, this.getPathFindBehavior2().getTargetX(), this.getPathFindBehavior2().getTargetY(), 0.5F)) {
							this.pathToLocation(worldSound.x + Rand.Next((int)(-float3), (int)float3), worldSound.y + Rand.Next((int)(-float3), (int)float3), worldSound.z);
							this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
							this.chasingSound = true;
							this.AllowRepathDelay = (float)AllowRepathDelayMax;
							this.timeSinceRespondToSound = 0.0F;
							this.soundAttract = float1;
							this.soundSourceTarget = object;
							return;
						}

						if (this.timeSinceRespondToSound < 60.0F || this.getPathFindBehavior2().isGoal2Location()) {
							return;
						}

						this.pathToLocation(worldSound.x + Rand.Next((int)(-float3), (int)float3), worldSound.y + Rand.Next((int)(-float3), (int)float3), worldSound.z);
						if (this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) {
							this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
							this.chasingSound = true;
						}

						this.AllowRepathDelay = (float)AllowRepathDelayMax;
						this.timeSinceRespondToSound = 0.0F;
						this.soundAttract = float1;
						this.soundSourceTarget = object;
					}
				}
			}
		}
	}

	public void spotted(IsoMovingObject movingObject, boolean boolean1) {
		if (!GameClient.bClient) {
			if (this.getCurrentSquare() != null) {
				if (movingObject.getCurrentSquare() != null) {
					if (!this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke) && !this.isUseless()) {
						if (!(movingObject instanceof IsoPlayer) || !((IsoPlayer)movingObject).GhostMode) {
							if (!(movingObject instanceof IsoGameCharacter) || !((IsoGameCharacter)movingObject).isDead()) {
								if (this.getCurrentSquare() == null) {
									this.ensureOnTile();
								}

								if (movingObject.getCurrentSquare() == null) {
									movingObject.ensureOnTile();
								}

								float float1 = 200.0F;
								int int1 = movingObject instanceof IsoPlayer && !GameServer.bServer ? ((IsoPlayer)movingObject).PlayerIndex : 0;
								float float2 = (movingObject.getCurrentSquare().lighting[int1].lightInfo().r + movingObject.getCurrentSquare().lighting[int1].lightInfo().g + movingObject.getCurrentSquare().lighting[int1].lightInfo().b) / 3.0F;
								float float3 = RenderSettings.getInstance().getAmbientForPlayer(int1);
								float float4 = (this.getCurrentSquare().lighting[int1].lightInfo().r + this.getCurrentSquare().lighting[int1].lightInfo().g + this.getCurrentSquare().lighting[int1].lightInfo().b) / 3.0F;
								float4 = float4 * float4 * float4;
								if (float2 > 1.0F) {
									float2 = 1.0F;
								}

								if (float2 < 0.0F) {
									float2 = 0.0F;
								}

								if (float4 > 1.0F) {
									float4 = 1.0F;
								}

								if (float4 < 0.0F) {
									float4 = 0.0F;
								}

								float float5 = 1.0F - (float2 - float4);
								if (float2 < 0.2F) {
									float2 = 0.2F;
								}

								if (float3 < 0.2F) {
									float3 = 0.2F;
								}

								if (movingObject instanceof IsoPlayer) {
									boolean boolean2 = false;
								}

								if (movingObject.getCurrentSquare().getRoom() != this.getCurrentSquare().getRoom()) {
									float1 = 50.0F;
									if (movingObject.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() == null || movingObject.getCurrentSquare().getRoom() == null && this.getCurrentSquare().getRoom() != null) {
										float1 = 20.0F;
										if (((IsoGameCharacter)movingObject).IsSneaking()) {
											if (float2 < 0.4F) {
												float1 = 0.0F;
											} else {
												float1 = 10.0F;
											}
										} else if (movingObject.getMovementLastFrame().getLength() <= 0.04F && float2 < 0.4F) {
											float1 = 10.0F;
										}
									}
								}

								tempo.x = movingObject.getX();
								tempo.y = movingObject.getY();
								Vector2 vector2 = tempo;
								vector2.x -= this.getX();
								vector2 = tempo;
								vector2.y -= this.getY();
								if (movingObject.getCurrentSquare().getZ() != this.current.getZ()) {
									int int2 = Math.abs(movingObject.getCurrentSquare().getZ() - this.current.getZ()) * 5;
									++int2;
									float1 /= (float)int2;
								}

								float float6 = GameTime.getInstance().getViewDist();
								if (!(tempo.getLength() > float6)) {
									if (GameServer.bServer) {
										this.bIndoorZombie = false;
									}

									if (tempo.getLength() < float6) {
										float6 = tempo.getLength();
									}

									float6 *= 1.1F;
									if (float6 > GameTime.getInstance().getViewDistMax()) {
										float6 = GameTime.getInstance().getViewDistMax();
									}

									tempo.normalize();
									Vector2 vector22 = this.getVectorFromDirection(tempo2);
									float float7 = vector22.dot(tempo);
									if (float6 > 1.0F) {
										if (float7 < -0.4F) {
											float1 = 0.0F;
										} else if (float7 < -0.2F) {
											float1 /= 8.0F;
										} else if (float7 < -0.0F) {
											float1 /= 4.0F;
										} else if (float7 < 0.2F) {
											float1 /= 2.0F;
										} else if (float7 <= 0.4F) {
											float1 *= 2.0F;
										} else if (float7 > 0.4F) {
											float1 *= 8.0F;
										} else if (float7 > 0.6F) {
											float1 *= 16.0F;
										} else if (float7 > 0.8F) {
											float1 *= 32.0F;
										}
									}

									if (float1 > 0.0F && this.target instanceof IsoPlayer) {
										IsoPlayer player = (IsoPlayer)this.target;
										if (!GameServer.bServer && player.RemoteID == -1 && this.current.isCanSee(player.PlayerIndex)) {
											((IsoPlayer)this.target).targetedByZombie = true;
											((IsoPlayer)this.target).lastTargeted = 0.0F;
										}
									}

									float1 *= float5;
									int int3 = (int)movingObject.getZ() - (int)this.getZ();
									if (int3 >= 1) {
										float1 /= (float)(int3 * 3);
									}

									float1 *= 1.0F - float6 / GameTime.getInstance().getViewDist();
									float1 *= 1.0F - float6 / GameTime.getInstance().getViewDist();
									float1 *= 1.0F - float6 / GameTime.getInstance().getViewDist();
									float float8 = movingObject.getMovementLastFrame().getLength();
									if (float8 == 0.0F && float2 <= 0.2F) {
										float2 = 0.0F;
									}

									if (((IsoGameCharacter)movingObject).IsSneaking() && (!(movingObject instanceof IsoPlayer) || ((IsoPlayer)movingObject).getTorchStrength() == 0.0F)) {
										float1 *= 0.5F;
									}

									if (float8 < 0.01F) {
										float1 *= 0.5F;
									} else if (((IsoGameCharacter)movingObject).IsSneaking()) {
										float1 *= 0.6F;
									} else if (float8 < 0.06F) {
										float1 *= 0.8F;
									} else if (float8 >= 0.06F) {
										float1 *= 2.4F;
									}

									if (float6 < 5.0F) {
										float1 *= 3.0F;
									}

									if (this.spottedLast == movingObject && this.TimeSinceSeenFlesh < 60.0F) {
										float1 = 1000.0F;
									}

									float1 *= ((IsoGameCharacter)movingObject).getSneakSpotMod();
									float1 *= float3;
									float float9;
									if (this.target != movingObject && this.target != null) {
										float float10 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), movingObject.getX(), movingObject.getY());
										float9 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), this.target.getX(), this.target.getY());
										if (float10 > float9) {
											return;
										}
									}

									float1 *= 0.3F;
									if (boolean1) {
										float1 = 1000000.0F;
									}

									if (this.BonusSpotTime > 0) {
										float1 = 1000000.0F;
									}

									float1 *= 1.2F;
									if (SandboxOptions.instance.Lore.Sight.getValue() == 1) {
										float1 *= 3.0F;
									}

									if (SandboxOptions.instance.Lore.Sight.getValue() == 3 || this.inactive) {
										float1 *= 0.25F;
									}

									float1 *= 0.25F;
									if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).HasTrait("Inconspicuous")) {
										float1 *= 0.5F;
									}

									if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).HasTrait("Conspicuous")) {
										float1 *= 2.0F;
									}

									IsoGridSquare square = movingObject.getCurrentSquare();
									float9 = 0.5F;
									float float11;
									if (square != null) {
										if (square.Is(IsoFlagType.collideN)) {
											float11 = 0.5F;
											if (!square.Is(IsoFlagType.HoppableN)) {
												float11 = 0.3F;
											}

											float9 *= float11;
										}

										if (square.Is(IsoFlagType.collideW)) {
											float11 = 0.5F;
											if (!square.Is(IsoFlagType.HoppableW)) {
												float11 = 0.3F;
											}

											float9 *= float11;
										}

										IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(square.getX(), square.getY() + 1, square.getZ());
										IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(square.getX() + 1, square.getY(), square.getZ());
										float float12;
										if (square2 != null && (square2.Is(IsoFlagType.collideN) || square2.Is(IsoFlagType.solid))) {
											float12 = 0.5F;
											if (!square2.Is(IsoFlagType.HoppableN)) {
												float12 = 0.3F;
											}

											float9 *= float12;
										}

										if (square3 != null && (square3.Is(IsoFlagType.collideW) || square3.Is(IsoFlagType.solid))) {
											float12 = 0.5F;
											if (!square3.Is(IsoFlagType.HoppableW)) {
												float12 = 0.3F;
											}

											float9 *= float12;
										}
									}

									if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).bSneaking) {
										float1 *= float9;
									} else {
										if (float9 < 0.5F) {
											float9 = 0.5F;
										}

										float1 *= float9;
									}

									float1 *= GameTime.instance.getMultiplier();
									float1 = (float)Math.floor((double)float1);
									if ((float)Rand.Next(400) >= float1) {
										if (float1 > 20.0F && movingObject instanceof IsoPlayer && float6 < 15.0F) {
											((IsoPlayer)movingObject).bCouldBeSeenThisFrame = true;
										}

										if (!((IsoPlayer)movingObject).isbCouldBeSeenThisFrame() && !((IsoPlayer)movingObject).isbSeenThisFrame() && ((IsoPlayer)movingObject).isbSneaking() && ((IsoPlayer)movingObject).JustMoved && Rand.Next((int)(700.0F * GameTime.instance.getInvMultiplier())) == 0) {
											if (GameServer.bServer) {
												GameServer.addXp((IsoPlayer)movingObject, PerkFactory.Perks.Sneak, 1);
											} else {
												((IsoPlayer)movingObject).getXp().AddXP(PerkFactory.Perks.Sneak, 1.0F);
											}
										}

										if (!((IsoPlayer)movingObject).isbCouldBeSeenThisFrame() && !((IsoPlayer)movingObject).isbSeenThisFrame() && ((IsoPlayer)movingObject).isbSneaking() && ((IsoPlayer)movingObject).JustMoved && Rand.Next((int)(700.0F * GameTime.instance.getInvMultiplier())) == 0) {
											if (GameServer.bServer) {
												GameServer.addXp((IsoPlayer)movingObject, PerkFactory.Perks.Lightfoot, 1);
											} else {
												((IsoPlayer)movingObject).getXp().AddXP(PerkFactory.Perks.Lightfoot, 1.0F);
											}
										}
									} else {
										if (movingObject instanceof IsoPlayer) {
											((IsoPlayer)movingObject).setbSeenThisFrame(true);
										}

										if (!boolean1) {
											this.BonusSpotTime = 120;
										}

										this.LastTargetSeenX = (int)movingObject.getX();
										this.LastTargetSeenY = (int)movingObject.getY();
										this.LastTargetSeenZ = (int)movingObject.getZ();
										if (this.stateMachine.getCurrent() != StaggerBackState.instance()) {
											this.target = movingObject;
											this.vectorToTarget.x = movingObject.getX();
											this.vectorToTarget.y = movingObject.getY();
											vector2 = this.vectorToTarget;
											vector2.x -= this.getX();
											vector2 = this.vectorToTarget;
											vector2.y -= this.getY();
											float11 = this.vectorToTarget.getLength();
											if (!boolean1) {
												this.TimeSinceSeenFlesh = 0.0F;
											}

											if (Rand.Next(400) == 0) {
											}

											if (this.target != this.spottedLast || this.getCurrentState() != LungeState.instance() || !(this.LungeTimer > 0.0F)) {
												if (this.target != this.spottedLast || this.getCurrentState() != AttackVehicleState.instance()) {
													if ((int)this.getZ() == (int)this.target.getZ() && (float11 <= 3.5F || this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null && float11 <= 4.0F) && this.getStateEventDelayTimer() <= 0.0F && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), movingObject.x, movingObject.y, (int)this.getZ(), movingObject)) {
														this.target = movingObject;
														this.Lunge();
														if (this.getCurrentState() == LungeState.instance()) {
															return;
														}
													}

													this.spottedLast = movingObject;
													if (!this.Ghost && !this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke)) {
														this.target = movingObject;
														if (this.AllowRepathDelay > 0.0F) {
															return;
														}

														if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null) {
															if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && this.getPathFindBehavior2().getTargetChar() == this.target) {
																return;
															}

															if (this.getCurrentState() == AttackVehicleState.instance()) {
																return;
															}

															BaseVehicle baseVehicle = ((IsoGameCharacter)this.target).getVehicle();
															if (Math.abs(baseVehicle.getCurrentSpeedKmHour()) > 0.1F && this.DistToSquared(baseVehicle) <= 16.0F) {
																return;
															}

															this.pathToCharacter((IsoGameCharacter)this.target);
															this.AllowRepathDelay = 10.0F;
															return;
														}

														this.pathToCharacter((IsoGameCharacter)movingObject);
														if (Rand.Next(5) == 0) {
															this.spotSoundDelay = 200;
														}

														this.AllowRepathDelay = (float)(AllowRepathDelayMax * 4);
													}
												}
											}
										}
									}
								}
							}
						}
					} else {
						this.target = null;
						this.spottedLast = null;
					}
				}
			}
		}
	}

	public void Move(Vector2 vector2) {
		if (!GameClient.bClient) {
			this.nx += vector2.x * GameTime.instance.getMultiplier();
			this.ny += vector2.y * GameTime.instance.getMultiplier();
			this.movex = vector2.x;
			this.movey = vector2.y;
			this.reqMovement.x = vector2.x;
			this.reqMovement.y = vector2.y;
		}
	}

	public void DoFootstepSound(float float1) {
		if (!GameServer.bServer) {
			if (float1 != 0.0F) {
				if (this.getCurrentSquare() != null) {
					boolean boolean1;
					if (GameClient.bClient) {
						if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
							int int1 = (int)this.def.Frame;
							if (int1 >= 0 && int1 < 5) {
								boolean1 = this.stepFrameLast < 0 || this.stepFrameLast > 5;
							} else {
								boolean1 = this.stepFrameLast < 5;
							}

							if (boolean1) {
								for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
									IsoPlayer player = IsoPlayer.players[int2];
									if (player != null && player.DistToSquared(this) < 225.0F) {
										ZombieFootstepManager.instance.addCharacter(this);
										break;
									}
								}
							}

							this.stepFrameLast = int1;
						} else {
							this.stepFrameLast = -1;
						}
					} else {
						if (this.def != null && this.def.NextFrame && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
							boolean boolean2 = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 15.0F);
							if (boolean2) {
								boolean1 = false;
								if ((int)this.def.Frame == 0) {
									boolean1 = true;
								}

								if ((int)this.def.Frame == 5) {
									boolean1 = true;
								}

								if (boolean1) {
									ZombieFootstepManager.instance.addCharacter(this);
								}
							}
						}

						this.def.NextFrame = false;
					}
				}
			}
		}
	}

	public void preupdate() {
		if (GameServer.bServer && this.thumpSent) {
			this.thumpFlag = 0;
			this.thumpSent = false;
			this.mpIdleSound = false;
		}

		this.FollowCount = 0;
		super.preupdate();
	}

	public void postupdate() {
		if (this.target instanceof IsoPlayer) {
			++((IsoPlayer)this.target).getStats().NumChasingZombies;
		}

		super.postupdate();
		if (this.current == null && !GameClient.bClient) {
			this.removeFromWorld();
			this.removeFromSquare();
		}

		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.ReanimatedCorpse == this) {
					player.setX(GameClient.bClient ? this.bx : this.getX());
					player.setY(GameClient.bClient ? this.by : this.getY());
					player.setZ(this.getZ());
					player.setDir(this.getDir());
					player.angle.set(this.getAngle());
					player.setCurrent(this.getCell().getGridSquare((int)player.x, (int)player.y, (int)player.z));
					player.updateLightInfo();
					if (player.soundListener != null) {
						player.soundListener.setPos(player.getX(), player.getY(), player.getZ());
						player.soundListener.tick();
					}

					IsoPlayer player2 = IsoPlayer.instance;
					IsoPlayer.instance = player;
					player.updateLOS();
					IsoPlayer.instance = player2;
					if (GameClient.bClient && this.networkUpdate.Check()) {
						GameClient.instance.sendPlayer(player);
					}

					player.dirtyRecalcGridStackTime = 2.0F;
					break;
				}
			}
		}
	}

	public void update() {
		if (this.zombieSoundInstance != -1L) {
			if (this.target instanceof IsoPlayer) {
				float float1 = (float)(Rand.Next(40) + 60) / 100.0F;
				javafmod.FMOD_Studio_SetParameter(this.zombieSoundInstance, "Aggitation", float1);
			} else {
				javafmod.FMOD_Studio_SetParameter(this.zombieSoundInstance, "Aggitation", 0.0F);
			}

			javafmod.FMOD_Studio_EventInstance3D(this.zombieSoundInstance, this.x - IsoPlayer.instance.x, this.y - IsoPlayer.instance.y, 0.0F);
		}

		this.updateEmitter();
		if (this.spotSoundDelay > 0) {
			--this.spotSoundDelay;
			if (this.spotSoundDelay == 0) {
			}
		}

		if (GameClient.bClient) {
			GameClient.instance.RecentlyDied.clear();
			if (this.lastRemoteUpdate > 800 && (this.legsSprite.CurrentAnim.name.equals("ZombieDeath") || this.legsSprite.CurrentAnim.name.equals("ZombieStaggerBack") || this.legsSprite.CurrentAnim.name.equals("ZombieGetUp"))) {
				DebugLog.log(DebugType.Zombie, "removing stale zombie 800 id=" + this.OnlineID);
				VirtualZombieManager.instance.removeZombieFromWorld(this);
				return;
			}

			if (GameClient.bFastForward) {
				VirtualZombieManager.instance.removeZombieFromWorld(this);
				return;
			}
		}

		if (this.legsSprite.CurrentAnim.name.contains("Stagger")) {
			boolean boolean1 = false;
		}

		if (GameClient.bClient && this.lastRemoteUpdate < 2000 && this.lastRemoteUpdate + 1000 / PerformanceSettings.LockFPS > 2000) {
			DebugLog.log(DebugType.Zombie, "lastRemoteUpdate 2000+ id=" + this.OnlineID);
		}

		this.lastRemoteUpdate = (short)(this.lastRemoteUpdate + 1000 / PerformanceSettings.LockFPS);
		if (GameClient.bClient && (!this.bRemote || this.lastRemoteUpdate > 5000)) {
			DebugLog.log(DebugType.Zombie, "removing stale zombie 5000 id=" + this.OnlineID);
			VirtualZombieManager.instance.removeZombieFromWorld(this);
		} else {
			this.DoFootstepSound(0.04F);
			this.sprite = this.legsSprite;
			if (this.sprite != null) {
				float float2;
				float float3;
				float float4;
				if (this.bRemote && GameClient.bClient) {
					this.Collidable = true;
					this.shootable = true;
					this.stateMachine.setCurrent(IdleState.instance());
					if (this.thumpFlag != 0) {
						if (SoundManager.instance.isListenerInRange(this.x, this.y, 20.0F)) {
							ZombieThumpManager.instance.addCharacter(this);
						} else {
							this.thumpFlag = 0;
						}
					}

					if (this.mpIdleSound) {
						if (SoundManager.instance.isListenerInRange(this.x, this.y, 20.0F)) {
							String string = this.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
							if (!this.getEmitter().isPlaying(string)) {
								ZombieVocalsManager.instance.addCharacter(this);
							}
						}

						this.mpIdleSound = false;
					}

					if (this.vehicle4testCollision != null) {
						BaseVehicle baseVehicle = this.vehicle4testCollision;
						this.vehicle4testCollision = null;
						if (baseVehicle.isEngineRunning() && baseVehicle.getDriver() instanceof IsoPlayer && ((IsoPlayer)baseVehicle.getDriver()).isLocalPlayer()) {
							float4 = baseVehicle.jniLinearVelocity.x;
							float2 = baseVehicle.jniLinearVelocity.z;
							float3 = (float)Math.sqrt((double)(float4 * float4 + float2 * float2));
							if (this.isOnFloor() && (this.bCrawling || this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
								int int1 = baseVehicle.testCollisionWithProneCharacter(this, this.angle.x, this.angle.y, false);
								if (int1 > 0) {
									super.update();
									return;
								}
							} else if (float3 > 0.05F && baseVehicle.testCollisionWithCharacter(this, 0.3F) != null) {
								baseVehicle.hitCharacter(this);
								super.update();
								return;
							}
						}
					}

					super.update();
					this.seperate();
				} else if (GameServer.bServer && this.bIndoorZombie) {
					super.update();
					if (GameServer.bServer && GameServer.doSendZombies()) {
						GameServer.sendZombie(this);
					}
				} else if (this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbThroughWindowState2.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState2.instance() && this.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance()) {
					this.setCollidable(true);
					LuaEventManager.triggerEvent("OnZombieUpdate", this);
					if (Core.bLastStand && this.getStateMachine().getCurrent() != ThumpState.instance() && this.getStateMachine().getCurrent() != AttackState.instance() && this.TimeSinceSeenFlesh > 120.0F && Rand.Next(36000) == 0) {
						IsoPlayer player = null;
						float4 = 1000000.0F;
						for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
							if (IsoPlayer.players[int2] != null && IsoPlayer.players[int2].DistTo(this) < float4 && !IsoPlayer.players[int2].isDead()) {
								float4 = IsoPlayer.players[int2].DistTo(this);
								player = IsoPlayer.players[int2];
							}
						}

						if (player != null) {
							this.AllowRepathDelay = -1.0F;
							this.pathToCharacter(player);
						}
					} else if (this.Health > 0.0F && this.vehicle4testCollision != null && this.testCollideWithVehicles(this.vehicle4testCollision)) {
						this.vehicle4testCollision = null;
					} else if (this.Health > 0.0F && this.vehicle4testCollision != null && this.isCollidedWithVehicle()) {
						this.vehicle4testCollision.hitCharacter(this);
						super.update();
					} else {
						this.vehicle4testCollision = null;
						this.BonusSpotTime = (int)((float)this.BonusSpotTime - GameTime.instance.getMultiplier());
						if (this.BonusSpotTime > 0 && this.spottedLast != null && !((IsoGameCharacter)this.spottedLast).isDead()) {
							this.spotted(this.spottedLast, true);
						}

						if (GameServer.bServer && this.getStateMachine().getCurrent() == BurntToDeath.instance()) {
							DebugLog.log(DebugType.Zombie, "Zombie is burning " + this.OnlineID);
						}

						super.update();
						if (VirtualZombieManager.instance.isReused(this)) {
							DebugLog.log(DebugType.Zombie, "Zombie added to ReusableZombies after super.update - RETURNING " + this);
						} else {
							if (GameServer.bServer && (GameServer.doSendZombies() || this.getStateMachine().getCurrent() == StaggerBackDieState.instance() || this.getStateMachine().getCurrent() == StaggerBackState.instance() || this.getStateMachine().getCurrent() == JustDieState.instance() || this.getStateMachine().getCurrent() == BurntToDeath.instance())) {
								GameServer.sendZombie(this);
							}

							if (this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbThroughWindowState2.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState2.instance() && this.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance()) {
								this.ensureOnTile();
								State state = this.stateMachine.getCurrent();
								if (state != StaggerBackState.instance() && state != BurntToDeath.instance() && state != JustDieState.instance() && state != StaggerBackDieState.instance() && state != FakeDeadZombieState.instance() && state != ReanimateState.instance() && state != ReanimatePlayerState.instance()) {
									if (GameServer.bServer && this.OnlineID == -1) {
										this.OnlineID = ServerMap.instance.getUniqueZombieId();
									} else {
										IsoSpriteInstance spriteInstance;
										if (state == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
											if (this.bCrawling) {
												this.PlayAnim("ZombieCrawl");
												this.def.AnimFrameIncrease = 0.0F;
											} else {
												this.PlayAnim("ZombieIdle");
												this.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
												spriteInstance = this.def;
												spriteInstance.AnimFrameIncrease *= 0.5F;
											}
										} else if (state != AttackState.instance() && state != AttackVehicleState.instance() && (this.nx != this.x || this.ny != this.y)) {
											if (this.walkVariantUse == null || state != LungeState.instance()) {
												this.walkVariantUse = this.walkVariant;
											}

											if (this.bCrawling) {
												this.walkVariantUse = "ZombieCrawl";
											}

											if (state != ZombieStandState.instance() && state != StaggerBackDieState.instance() && state != StaggerBackState.instance() && state != ThumpState.instance() && state != FakeDeadZombieState.instance()) {
												if (this.bRunning) {
													this.PlayAnim("Run");
													this.def.setFrameSpeedPerFrame(0.33F);
												} else {
													this.PlayAnim(this.walkVariantUse);
													this.def.setFrameSpeedPerFrame(0.26F);
													spriteInstance = this.def;
													spriteInstance.AnimFrameIncrease *= this.speedMod;
												}

												this.setShootable(true);
											}
										}
									}

									this.shootable = true;
									this.solid = true;
									this.tryThump((IsoGridSquare)null);
									this.damageSheetRope();
									this.AllowRepathDelay -= GameTime.instance.getMultiplier();
									this.TimeSinceSeenFlesh += GameTime.instance.getMultiplier();
									short short1 = 160;
									if (SandboxOptions.instance.Lore.Memory.getValue() == 1) {
										short1 = 250;
									}

									if (SandboxOptions.instance.Lore.Memory.getValue() == 3) {
										short1 = 100;
									}

									if (SandboxOptions.instance.Lore.Memory.getValue() == 4 || this.inactive) {
										short1 = 5;
									}

									if (this.TimeSinceSeenFlesh > (float)short1 && this.target != null) {
										this.target = null;
									}

									if (this.target != null) {
										this.vectorToTarget.x = this.target.getX();
										this.vectorToTarget.y = this.target.getY();
										Vector2 vector2 = this.vectorToTarget;
										vector2.x -= this.getX();
										vector2 = this.vectorToTarget;
										vector2.y -= this.getY();
									}

									move.x = this.getNx() - this.getLx();
									move.y = this.getNy() - this.getLy();
									float2 = move.getLength();
									float3 = 1.0F - float2 / 0.08F;
									if (float2 > 0.0F) {
									}

									if (IsoPlayer.instance != null && (IsoPlayer.instance.Waiting && Rand.Next(Rand.AdjustForFramerate(1000)) == 0 || !IsoPlayer.instance.Waiting && Rand.Next(Rand.AdjustForFramerate(360)) == 0 && (this.stateMachine.getCurrent() == WalkTowardState.instance() || this.stateMachine.getCurrent() == PathFindState.instance())) && SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0F)) {
										String string2 = this.bFemale ? "FemaleZombieIdle" : "MaleZombieIdle";
										if (!this.emitter.isPlaying(string2)) {
											ZombieVocalsManager.instance.addCharacter(this);
										}
									}

									if (GameServer.bServer && (this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState.instance()) && Rand.Next(Rand.AdjustForFramerate(360)) == 0) {
										this.mpIdleSound = true;
									}

									if (this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance() && this.getCurrentState() != ClimbThroughWindowState.instance()) {
										this.setLastHeardSound(-1, -1, -1);
									}

									if (this.TimeSinceSeenFlesh > 240.0F && this.timeSinceRespondToSound > 5.0F) {
										this.RespondToSound();
										if (this.timeSinceRespondToSound > 60.0F) {
											ZombieGroupManager.instance.update(this);
										}
									}

									this.timeSinceRespondToSound += GameTime.getInstance().getMultiplier() / 1.6F;
									this.seperate();
									ZombieGroupManager.instance.update(this);
								}
							}
						}
					}
				} else {
					super.update();
					if (GameServer.bServer && GameServer.doSendZombies()) {
						GameServer.sendZombie(this);
					}
				}
			}
		}
	}

	private void damageSheetRope() {
		if (Rand.Next(30) == 0 && this.current != null && (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetW))) {
			IsoObject object = this.current.getSheetRope();
			if (object != null) {
				object.sheetRopeHealth -= (float)Rand.Next(5, 15);
				if (object.sheetRopeHealth < 40.0F) {
					this.current.damageSpriteSheetRopeFromBottom((IsoPlayer)null, this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetS));
					this.current.RecalcProperties();
				}

				if (object.sheetRopeHealth <= 0.0F) {
					this.current.removeSheetRopeFromBottom((IsoPlayer)null, this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetS));
				}
			}
		}
	}

	public void getZombieWalkTowardSpeed(float float1, float float2, Vector2 vector2) {
		float float3 = 1.0F;
		float3 = float2 / 24.0F;
		if (float3 < 1.0F) {
			float3 = 1.0F;
		}

		if (float3 > 1.3F) {
			float3 = 1.3F;
		}

		vector2.setLength((float1 * this.getSpeedMod() + 0.006F) * float3);
		if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
			vector2.setLength(0.08F);
			this.bRunning = true;
		}

		if (vector2.getLength() > float2) {
			vector2.setLength(float2);
		}
	}

	public void getZombieLungeSpeed(Vector2 vector2) {
		float float1 = this.LungeTimer / 180.0F;
		float float2 = this.getPathSpeed() + 0.03F * float1;
		vector2.normalize();
		vector2.setLength(float2 * this.getSpeedMod());
		this.bRunning = false;
		if (GameServer.bServer) {
			float1 = 1.0F;
		}

		if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
			vector2.setLength(0.08F + 0.01F * (1.0F - float1));
			this.bRunning = true;
		}
	}

	public void getZombieLungeSpeed(float float1, float float2, Vector2 vector2) {
		float float3 = 1.0F;
		float3 = float2 / 24.0F;
		if (float3 < 1.0F) {
			float3 = 1.0F;
		}

		if (float3 > 1.3F) {
			float3 = 1.3F;
		}

		vector2.setLength((float1 * this.getSpeedMod() + 0.006F) * float3);
		if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
			vector2.setLength(0.08F);
			this.bRunning = true;
		}

		if (vector2.getLength() > float2) {
			vector2.setLength(float2);
		}
	}

	public boolean tryThump(IsoGridSquare square) {
		IsoGridSquare square2 = null;
		if (square != null) {
			square2 = square;
		} else {
			square2 = this.getFeelerTile(this.getFeelersize());
		}

		if (square2 != null && this.current != null) {
			IsoObject object = this.current.testCollideSpecialObjects(square2);
			if (object instanceof Thumpable && !this.bCrawling) {
				if (object instanceof IsoWindow && ((IsoWindow)object).canClimbThrough(this) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
					this.StateMachineParams.put(0, object);
					this.stateMachine.changeState(ClimbThroughWindowState.instance());
					return true;
				}

				if (object instanceof IsoThumpable && ((IsoThumpable)object).isWindow() && !((IsoThumpable)object).isBarricaded() && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
					this.StateMachineParams.put(0, object);
					this.stateMachine.changeState(ClimbThroughWindowState.instance());
					return true;
				}

				if ((object instanceof IsoThumpable && ((IsoThumpable)object).isThumpable() || object instanceof IsoWindow && !((IsoWindow)object).isDestroyed() || object instanceof IsoDoor && !((IsoDoor)object).isDestroyed() && !((IsoDoor)object).open) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
					int int1 = square2.getX() - this.current.getX();
					int int2 = square2.getY() - this.current.getY();
					IsoDirections directions = IsoDirections.N;
					if (int1 < 0 && Math.abs(int1) > Math.abs(int2)) {
						directions = IsoDirections.S;
					}

					if (int1 < 0 && Math.abs(int1) <= Math.abs(int2)) {
						directions = IsoDirections.SW;
					}

					if (int1 > 0 && Math.abs(int1) > Math.abs(int2)) {
						directions = IsoDirections.W;
					}

					if (int1 > 0 && Math.abs(int1) <= Math.abs(int2)) {
						directions = IsoDirections.SE;
					}

					if (int2 < 0 && Math.abs(int1) < Math.abs(int2)) {
						directions = IsoDirections.N;
					}

					if (int2 < 0 && Math.abs(int1) >= Math.abs(int2)) {
						directions = IsoDirections.NW;
					}

					if (int2 > 0 && Math.abs(int1) < Math.abs(int2)) {
						directions = IsoDirections.E;
					}

					if (int2 > 0 && Math.abs(int1) >= Math.abs(int2)) {
						directions = IsoDirections.NE;
					}

					if (this.getDir() == directions) {
						if (!SandboxOptions.instance.Lore.ThumpNoChasing.getValue() && this.target == null && !this.chasingSound) {
							this.stateMachine.changeState(ZombieStandState.instance());
						} else {
							this.setThumpTarget((Thumpable)object);
							this.path = null;
							this.stateMachine.changeState(ThumpState.instance());
						}
					}

					return true;
				}
			}

			State state = this.getCurrentState();
			if (object != null && !this.bCrawling && IsoWindowFrame.isWindowFrame(object) && (state == PathFindState.instance() || state == LungeState.instance() || state == WalkTowardState.instance())) {
				this.StateMachineParams.clear();
				this.StateMachineParams.put(0, object);
				this.getStateMachine().changeState(ClimbThroughWindowState.instance());
				return true;
			}
		}

		return false;
	}

	public void Wander() {
		if (this instanceof IsoZombie) {
			GameServer.sendZombie(this);
		}

		this.stateMachine.changeState(ZombieStandState.instance());
	}

	public Path FindPath(int int1, int int2, int int3, int int4, int int5, int int6) {
		if (this.getCurrentSquare() == null) {
			return null;
		} else {
			Path path = new Path();
			int int7 = 20;
			int int8 = int1;
			int int9 = int2;
			int int10 = int3;
			IsoDirections directions = IsoDirections.Max;
			this.doneGrids.clear();
			this.doneGrids.add(this.getCurrentSquare());
			IsoGridSquare square;
			while (int7 > 0) {
				this.choiceGrids.clear();
				if (int8 == 88 && int9 == 23) {
					boolean boolean1 = false;
				}

				float float1 = 1.0E7F;
				square = null;
				boolean boolean2 = false;
				boolean boolean3 = false;
				boolean boolean4 = false;
				int int11;
				int int12;
				for (int11 = -1; int11 <= 1; ++int11) {
					for (int12 = -1; int12 <= 1; ++int12) {
						for (int int13 = -1; int13 <= 1; ++int13) {
							if ((int11 != 0 || int12 != 0 || int13 != 0) && !this.getCell().blocked(this, int8 + int11, int9 + int12, int10 + int13, int8, int9, int10)) {
								IsoGridSquare square2 = this.getCell().getGridSquare(int8 + int11, int9 + int12, int10 + int13);
								if (square2 != null && !this.doneGrids.contains(square2)) {
									this.choiceGrids.add(square2);
								}
							}
						}
					}
				}

				int11 = int8;
				int12 = int9;
				if (directions == IsoDirections.N || directions == IsoDirections.NE || directions == IsoDirections.NW) {
					int12 = int9 - 1;
				}

				if (directions == IsoDirections.S || directions == IsoDirections.SE || directions == IsoDirections.SW) {
					++int12;
				}

				if (directions == IsoDirections.E || directions == IsoDirections.NE || directions == IsoDirections.SE) {
					int11 = int8 + 1;
				}

				if (directions == IsoDirections.W || directions == IsoDirections.NW || directions == IsoDirections.SW) {
					--int11;
				}

				float float2 = IsoUtils.DistanceManhattenSquare((float)int11, (float)int12, (float)int4, (float)int5);
				if (directions != IsoDirections.Max) {
					square = this.getCell().getGridSquare(int11, int12, int10);
				}

				ArrayList arrayList = new ArrayList();
				if (square == null) {
					float2 = 1000000.0F;
				} else {
					arrayList.add(square);
				}

				for (int int14 = 0; int14 < this.choiceGrids.size(); ++int14) {
					IsoGridSquare square3 = (IsoGridSquare)this.choiceGrids.get(int14);
					float float3 = IsoUtils.DistanceManhatten((float)square3.getX(), (float)square3.getY(), (float)int4, (float)int5, (float)square3.getZ(), (float)int6);
					if (float3 < float2 && square3 != square) {
						arrayList.add(square3);
					}
				}

				if (!arrayList.isEmpty()) {
					square = (IsoGridSquare)arrayList.get(Rand.Next(arrayList.size()));
				}

				arrayList.clear();
				--int7;
				if (square == null) {
					break;
				}

				this.doneGrids.add(square);
				if (square.getX() > int8) {
					if (square.getY() < int9) {
						directions = IsoDirections.NE;
					} else if (square.getY() < int9) {
						directions = IsoDirections.SE;
					} else {
						directions = IsoDirections.E;
					}
				} else if (square.getX() < int8) {
					if (square.getY() < int9) {
						directions = IsoDirections.NW;
					} else if (square.getY() < int9) {
						directions = IsoDirections.SW;
					} else {
						directions = IsoDirections.W;
					}
				} else if (square.getY() < int9) {
					directions = IsoDirections.N;
				} else if (square.getY() < int9) {
					directions = IsoDirections.S;
				}

				int8 = square.getX();
				int9 = square.getY();
				int10 = square.getZ();
				if (int8 == int4 && int9 == int5 && int10 == int6) {
					break;
				}
			}

			for (int int15 = 0; int15 < this.doneGrids.size(); ++int15) {
				square = (IsoGridSquare)this.doneGrids.get(int15);
				if (square != null) {
					path.appendStep(square.getX(), square.getY(), square.getZ());
				}
			}

			return path;
		}
	}

	public void updateFrameSpeed() {
		move.x = this.getNx() - this.getLx();
		move.y = this.getNy() - this.getLy();
		float float1 = 1.0F - move.getLength() / 0.08F;
	}

	public void DoZombieInventory() {
		if (!this.isReanimatedPlayer()) {
			this.getInventory().removeAllItems();
			this.getInventory().setSourceGrid(this.getCurrentSquare());
			this.Dressup(this.descriptor);
			if (!GameClient.bClient) {
				IsoBuilding building = this.getCurrentBuilding();
				if (building != null && building.getDef() != null && building.getDef().getKeyId() != -1 && Rand.Next(4) == 0) {
					String string = "Base.Key" + (Rand.Next(5) + 1);
					InventoryItem inventoryItem = this.inventory.AddItem(string);
					inventoryItem.setKeyId(building.getDef().getKeyId());
				}

				if (this.itemsToSpawnAtDeath != null && !this.itemsToSpawnAtDeath.isEmpty()) {
					for (int int1 = 0; int1 < this.itemsToSpawnAtDeath.size(); ++int1) {
						this.inventory.addItem((InventoryItem)this.itemsToSpawnAtDeath.get(int1));
					}

					this.itemsToSpawnAtDeath.clear();
				}
			}
		}
	}

	public void changeSpeed(int int1) {
		this.walkVariant = "ZombieWalk";
		this.speedType = int1;
		IsoSpriteInstance spriteInstance;
		if (this.speedType == 3) {
			this.speedMod = 0.55F;
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			this.walkVariant = this.walkVariant + "1";
			this.def.setFrameSpeedPerFrame(0.24F);
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= this.speedMod;
		} else if (this.speedType != 3) {
			this.bLunger = true;
			this.speedMod = 0.85F;
			this.walkVariant = this.walkVariant + "2";
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			this.def.setFrameSpeedPerFrame(0.24F);
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= this.speedMod;
		}

		if (IsoWorld.instance.getGlobalTemperature() < 13.0F) {
		}

		this.PathSpeed = baseSpeed * this.speedMod;
		this.wanderSpeed = this.PathSpeed;
	}

	public void DoZombieStats() {
		if (SandboxOptions.instance.Lore.Cognition.getValue() == 1) {
			this.cognition = 1;
		}

		if (SandboxOptions.instance.Lore.Cognition.getValue() == 4) {
			this.cognition = Rand.Next(0, 2);
		}

		if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 1) {
			this.strength = 5;
		}

		if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 2) {
			this.strength = 3;
		}

		if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 3) {
			this.strength = 1;
		}

		if (this.strength == -1 && SandboxOptions.instance.Lore.Strength.getValue() == 4) {
			this.strength = Rand.Next(1, 5);
		}

		if (this.speedType == -1 && SandboxOptions.instance.Lore.Speed.getValue() == 4) {
			this.speedType = Rand.Next(2);
		}

		IsoSpriteInstance spriteInstance;
		if (this.bCrawling) {
			this.speedMod = 0.3F;
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= 0.8F;
		} else if (Rand.Next(3) == 0 && SandboxOptions.instance.Lore.Speed.getValue() != 3 && this.speedType != 3) {
			if (SandboxOptions.instance.Lore.Speed.getValue() != 3 || this.speedType != 3) {
				this.bLunger = true;
				this.speedMod = 0.85F;
				this.walkVariant = this.walkVariant + "2";
				this.speedMod += (float)Rand.Next(1500) / 10000.0F;
				this.def.setFrameSpeedPerFrame(0.24F);
				spriteInstance = this.def;
				spriteInstance.AnimFrameIncrease *= this.speedMod;
			}
		} else {
			this.speedMod = 0.55F;
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			this.walkVariant = this.walkVariant + "1";
			this.def.setFrameSpeedPerFrame(0.24F);
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= this.speedMod;
		}

		if (IsoWorld.instance.getGlobalTemperature() < 13.0F) {
		}

		this.PathSpeed = baseSpeed * this.speedMod;
		this.wanderSpeed = this.PathSpeed;
	}

	public void DoZombieSpeeds(float float1) {
		IsoSpriteInstance spriteInstance;
		if (this.bCrawling) {
			this.speedMod = float1;
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= 0.8F;
		} else if (Rand.Next(3) == 0 && SandboxOptions.instance.Lore.Speed.getValue() != 3) {
			if (SandboxOptions.instance.Lore.Speed.getValue() != 3) {
				this.bLunger = true;
				this.speedMod = float1;
				this.walkVariant = this.walkVariant + "2";
				this.def.setFrameSpeedPerFrame(0.24F);
				spriteInstance = this.def;
				spriteInstance.AnimFrameIncrease *= this.speedMod;
			}
		} else {
			this.speedMod = float1;
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			this.walkVariant = this.walkVariant + "1";
			this.def.setFrameSpeedPerFrame(0.24F);
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= this.speedMod;
		}

		if (IsoWorld.instance.getGlobalTemperature() < 13.0F) {
		}

		this.PathSpeed = baseSpeed * this.speedMod;
		this.wanderSpeed = this.PathSpeed;
	}

	public boolean isFakeDead() {
		return this.bFakeDead;
	}

	public void setFakeDead(boolean boolean1) {
		this.bFakeDead = boolean1;
	}

	public boolean isForceFakeDead() {
		return this.bForceFakeDead;
	}

	public void setForceFakeDead(boolean boolean1) {
		this.bForceFakeDead = boolean1;
	}

	public void HitSilence(HandWeapon handWeapon, IsoZombie zombie, float float1, boolean boolean1, float float2) {
		super.HitSilence(handWeapon, zombie, float1, boolean1, float2);
		this.target = zombie;
		if (this.Health <= 0.0F && !this.bDead) {
			this.DoZombieInventory();
			this.bDead = true;
		}

		this.TimeSinceSeenFlesh = 0.0F;
	}

	protected void DoDeathSilence(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (this.Health <= 0.0F && !this.bDead) {
			this.DoZombieInventory();
			this.bDead = true;
		}

		super.DoDeathSilence(handWeapon, gameCharacter);
	}

	public void Hit(BaseVehicle baseVehicle, float float1, float float2, Vector2 vector2) {
		this.AttackedBy = baseVehicle.getDriver();
		this.setHitDir(vector2);
		this.setHitForce(float1 * 0.15F);
		int int1 = (new Float(float1 * 6.0F)).intValue();
		this.target = baseVehicle.getCharacter(0);
		if (float2 > 0.0F) {
			if (Rand.Next(100) <= int1) {
				if (Rand.Next(8) == 0) {
					this.setFakeDead(true);
				}

				this.getStateMachine().changeState(StaggerBackDieState.instance());
			} else {
				this.getStateMachine().changeState(StaggerBackState.instance());
			}
		} else if (float1 < 3.0F) {
			if (Rand.Next(100) <= int1) {
				if (Rand.Next(8) == 0) {
					this.setFakeDead(true);
				}

				this.getStateMachine().changeState(StaggerBackDieState.instance());
			} else {
				this.getStateMachine().changeState(StaggerBackState.instance());
			}
		} else if (float1 < 10.0F) {
			if (Rand.Next(8) == 0) {
				this.setFakeDead(true);
			}

			this.getStateMachine().changeState(StaggerBackDieState.instance());
		} else {
			this.DoZombieInventory();
			this.Kill(baseVehicle.getCharacter(0));
		}

		if (!((float)Rand.Next(10) > float1)) {
			float float3 = 0.6F;
			if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				int int2 = Rand.Next(4, 10);
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

				for (int int3 = 0; int3 < int2; ++int3) {
					this.splatBlood(3, 0.3F);
				}
			}

			if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				this.splatBloodFloorBig(0.3F);
			}

			if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
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

				for (int int4 = 0; int4 < byte3; ++int4) {
					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 1.8F, this.getHitDir().y * 1.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 1.9F, this.getHitDir().y * 1.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + float3, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
					}
				}
			}
		}
	}

	public void DoNetworkDirty() {
	}

	public void removeFromWorld() {
		VirtualZombieManager.instance.RemoveZombie(this);
		this.setPath2((PolygonalMap2.Path)null);
		PolygonalMap2.instance.cancelRequest(this);
		if (this.getFinder().progress != AStarPathFinder.PathFindProgress.notrunning && this.getFinder().progress != AStarPathFinder.PathFindProgress.found) {
			this.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
			PathfindManager.instance.abortJob(this);
		}

		if (this.group != null) {
			this.group.remove(this);
			this.group = null;
		}

		if (GameServer.bServer && this.OnlineID != -1) {
			ServerMap.instance.ZombieMap.remove(this.OnlineID);
			this.OnlineID = -1;
		}

		if (GameClient.bClient) {
			GameClient.instance.removeZombieFromCache(this);
		}

		this.getCell().getZombieList().remove(this);
		super.removeFromWorld();
	}

	public boolean isReanimatedPlayer() {
		return this.bReanimatedPlayer;
	}

	public void setReanimatedPlayer(boolean boolean1) {
		this.bReanimatedPlayer = boolean1;
	}

	public void useDescriptor(SurvivorDesc survivorDesc, int int1) {
		if (survivorDesc != null && survivorDesc != this.descriptor) {
			this.descriptor = survivorDesc;
			this.palette = int1;
			this.bFemale = this.descriptor.isFemale();
			this.SpriteName = this.descriptor.isFemale() ? "KateZ" : "BobZ";
			if (this.palette != 1) {
				this.SpriteName = this.SpriteName + this.palette;
			}

			this.InitSpritePartsZombie();
			this.hurtSound = this.bFemale ? "FemaleZombieHurt" : "MaleZombieHurt";
		}
	}

	public boolean WanderFromWindow() {
		if (this.getCurrentSquare() == null) {
			return false;
		} else {
			IsoZombie.FloodFill floodFill = floodFill;
			floodFill.calculate(this, this.getCurrentSquare());
			IsoGridSquare square = floodFill.choose();
			floodFill.reset();
			if (square != null) {
				this.pathToLocation(square.getX(), square.getY(), square.getZ());
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean isUseless() {
		return this.useless;
	}

	public void setUseless(boolean boolean1) {
		this.useless = boolean1;
	}

	public void setImmortalTutorialZombie(boolean boolean1) {
		ImmortalTutorialZombie = boolean1 ? this : null;
	}

	public boolean isTargetInCone(float float1, float float2) {
		if (this.target == null) {
			return false;
		} else {
			tempo.set(this.target.getX() - this.getX(), this.target.getY() - this.getY());
			float float3 = tempo.getLength();
			if (float3 == 0.0F) {
				return true;
			} else if (float3 > float1) {
				return false;
			} else {
				tempo.normalize();
				this.getVectorFromDirection(tempo2);
				float float4 = tempo.dot(tempo2);
				return float4 >= float2;
			}
		}
	}

	public boolean testCollideWithVehicles(BaseVehicle baseVehicle) {
		if (this.Health <= 0.0F) {
			return false;
		} else {
			float float1;
			if (!this.isOnFloor() || !this.bCrawling && (this.legsSprite.CurrentAnim == null || !this.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
				float float2 = baseVehicle.jniLinearVelocity.x;
				float1 = baseVehicle.jniLinearVelocity.z;
				if (GameServer.bServer) {
					float2 = baseVehicle.netLinearVelocity.x;
					float1 = baseVehicle.netLinearVelocity.z;
				}

				float float3 = (float)Math.sqrt((double)(float2 * float2 + float1 * float1));
				if (baseVehicle.isEngineRunning() && float3 > 0.05F && baseVehicle.testCollisionWithCharacter(this, 0.3F) != null) {
					baseVehicle.hitCharacter(this);
					super.update();
					return true;
				} else {
					return false;
				}
			} else {
				int int1 = baseVehicle.isEngineRunning() ? baseVehicle.testCollisionWithProneCharacter(this, this.angle.x, this.angle.y, true) : 0;
				if (int1 > 0) {
					if (!this.emitter.isPlaying(this.hurtSound)) {
						this.emitter.playSound(this.hurtSound);
					}

					this.AttackedBy = baseVehicle.getDriver();
					float1 = Math.min(GameTime.getInstance().getMultiplier() / 1.6F, 30.0F / (float)PerformanceSettings.LockFPS * 2.0F);
					this.hitConsequences(IsoPlayer.instance.bareHands, baseVehicle.getDriver(), false, 0.25F * (float)int1 * float1, true);
					if (this.Health <= 0.0F && !this.bDead) {
						this.DoZombieInventory();
						LuaEventManager.triggerEvent("OnZombieDead", this);
						this.bDead = true;
					}

					super.update();
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void toggleCrawling() {
		if (this.bCrawling) {
			this.bCrawling = false;
			this.setOnFloor(false);
			this.DoZombieStats();
		} else {
			this.bCrawling = true;
			this.setOnFloor(true);
			this.DoZombieStats();
			this.walkVariant = "ZombieWalk";
		}
	}

	public void addItemToSpawnAtDeath(InventoryItem inventoryItem) {
		if (this.itemsToSpawnAtDeath == null) {
			this.itemsToSpawnAtDeath = new ArrayList();
		}

		this.itemsToSpawnAtDeath.add(inventoryItem);
	}

	public void clearItemsToSpawnAtDeath() {
		if (this.itemsToSpawnAtDeath != null) {
			this.itemsToSpawnAtDeath.clear();
		}
	}

	private static final class FloodFill {
		private IsoGridSquare start;
		private final int FLOOD_SIZE;
		private final BooleanGrid visited;
		private final Stack stack;
		private IsoBuilding building;
		private Mover mover;
		private final ArrayList choices;

		private FloodFill() {
			this.start = null;
			this.FLOOD_SIZE = 11;
			this.visited = new BooleanGrid(11, 11);
			this.stack = new Stack();
			this.building = null;
			this.mover = null;
			this.choices = new ArrayList(121);
		}

		void calculate(Mover mover, IsoGridSquare square) {
			this.start = square;
			this.mover = mover;
			if (this.start.getRoom() != null) {
				this.building = this.start.getRoom().getBuilding();
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			if (this.push(this.start.getX(), this.start.getY())) {
				while ((square = this.pop()) != null) {
					int int1 = square.getX();
					int int2;
					for (int2 = square.getY(); this.shouldVisit(int1, int2, int1, int2 - 1); --int2) {
					}

					boolean2 = false;
					boolean1 = false;
					while (true) {
						this.visited.setValue(this.gridX(int1), this.gridY(int2), true);
						IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, this.start.getZ());
						if (square2 != null) {
							this.choices.add(square2);
						}

						if (!boolean1 && this.shouldVisit(int1, int2, int1 - 1, int2)) {
							if (!this.push(int1 - 1, int2)) {
								return;
							}

							boolean1 = true;
						} else if (boolean1 && !this.shouldVisit(int1, int2, int1 - 1, int2)) {
							boolean1 = false;
						} else if (boolean1 && !this.shouldVisit(int1 - 1, int2, int1 - 1, int2 - 1) && !this.push(int1 - 1, int2)) {
							return;
						}

						if (!boolean2 && this.shouldVisit(int1, int2, int1 + 1, int2)) {
							if (!this.push(int1 + 1, int2)) {
								return;
							}

							boolean2 = true;
						} else if (boolean2 && !this.shouldVisit(int1, int2, int1 + 1, int2)) {
							boolean2 = false;
						} else if (boolean2 && !this.shouldVisit(int1 + 1, int2, int1 + 1, int2 - 1) && !this.push(int1 + 1, int2)) {
							return;
						}

						++int2;
						if (!this.shouldVisit(int1, int2 - 1, int1, int2)) {
							break;
						}
					}
				}
			}
		}

		boolean shouldVisit(int int1, int int2, int int3, int int4) {
			if (this.gridX(int3) < 11 && this.gridX(int3) >= 0) {
				if (this.gridY(int4) < 11 && this.gridY(int4) >= 0) {
					if (this.visited.getValue(this.gridX(int3), this.gridY(int4))) {
						return false;
					} else {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, this.start.getZ());
						if (square == null) {
							return false;
						} else if (!square.Has(IsoObjectType.stairsBN) && !square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsTN)) {
							if (!square.Has(IsoObjectType.stairsBW) && !square.Has(IsoObjectType.stairsMW) && !square.Has(IsoObjectType.stairsTW)) {
								if (square.getRoom() != null && this.building == null) {
									return false;
								} else if (square.getRoom() == null && this.building != null) {
									return false;
								} else {
									return !IsoWorld.instance.CurrentCell.blocked(this.mover, int3, int4, this.start.getZ(), int1, int2, this.start.getZ());
								}
							} else {
								return false;
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

		boolean push(int int1, int int2) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, this.start.getZ());
			this.stack.push(square);
			return true;
		}

		IsoGridSquare pop() {
			return this.stack.isEmpty() ? null : (IsoGridSquare)this.stack.pop();
		}

		int gridX(int int1) {
			return int1 - (this.start.getX() - 5);
		}

		int gridY(int int1) {
			return int1 - (this.start.getY() - 5);
		}

		int gridX(IsoGridSquare square) {
			return square.getX() - (this.start.getX() - 5);
		}

		int gridY(IsoGridSquare square) {
			return square.getY() - (this.start.getY() - 5);
		}

		IsoGridSquare choose() {
			if (this.choices.isEmpty()) {
				return null;
			} else {
				int int1 = Rand.Next(this.choices.size());
				return (IsoGridSquare)this.choices.get(int1);
			}
		}

		void reset() {
			this.building = null;
			this.choices.clear();
			this.stack.clear();
			this.visited.clear();
		}

		FloodFill(Object object) {
			this();
		}
	}

	public static enum ZombieSound {

		Burned,
		DeadCloseKilled,
		DeadNotCloseKilled,
		Hurt,
		Idle,
		Lunge,
		MAX,
		radius,
		values;

		private ZombieSound(int int1) {
			this.radius = int1;
		}
		public int radius() {
			return this.radius;
		}
		public static IsoZombie.ZombieSound fromIndex(int int1) {
			return int1 >= 0 && int1 < values.length ? values[int1] : MAX;
		}
	}
}
