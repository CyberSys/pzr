package zombie.characters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.PersistentOutfits;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.ai.ZombieGroupManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.ai.states.AttackNetworkState;
import zombie.ai.states.AttackState;
import zombie.ai.states.BumpedState;
import zombie.ai.states.BurntToDeath;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.FakeDeadAttackState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.IdleState;
import zombie.ai.states.LungeNetworkState;
import zombie.ai.states.LungeState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.PlayerHitReactionState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardNetworkState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieEatBodyState;
import zombie.ai.states.ZombieFaceTargetState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieFallingState;
import zombie.ai.states.ZombieGetDownState;
import zombie.ai.states.ZombieGetUpFromCrawlState;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieHitReactionState;
import zombie.ai.states.ZombieIdleState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.ai.states.ZombieReanimateState;
import zombie.ai.states.ZombieSittingState;
import zombie.ai.states.ZombieTurnAlerted;
import zombie.audio.parameters.ParameterCharacterInside;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.audio.parameters.ParameterFootstepMaterial;
import zombie.audio.parameters.ParameterFootstepMaterial2;
import zombie.audio.parameters.ParameterPlayerDistance;
import zombie.audio.parameters.ParameterShoeType;
import zombie.audio.parameters.ParameterVehicleHitLocation;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionGroup;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferReader;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.Shader;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.sharedskele.SharedSkeleAnimationRepository;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
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
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.packets.DeadBodyPacket;
import zombie.scripting.ScriptManager;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.AttackVehicleState;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehiclePart;


public final class IsoZombie extends IsoGameCharacter implements IHumanVisual {
	public static final byte NetRemoteState_Idle = 1;
	public static final byte NetRemoteState_Walk = 2;
	public static final byte NetRemoteState_Stagger = 3;
	public static final byte NetRemoteState_Lunge = 4;
	public static final byte NetRemoteState_Bite = 5;
	public static final byte NetRemoteState_WalkToward = 6;
	public static final byte NetRemoteState_StaggerBack = 7;
	public static final byte NetRemoteState_StaggerBackDie = 8;
	public static final byte SPEED_SPRINTER = 1;
	public static final byte SPEED_FAST_SHAMBLER = 2;
	public static final byte SPEED_SHAMBLER = 3;
	public static final byte SPEED_RANDOM = 4;
	private boolean alwaysKnockedDown;
	private boolean onlyJawStab;
	private boolean forceEatingAnimation;
	private boolean noTeeth;
	private static final int AllowRepathDelayMax = 120;
	public static final boolean SPRINTER_FIXES = true;
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
	private float targetSeenTime;
	public int FollowCount;
	public int ZombieID;
	private float BonusSpotTime;
	public boolean bStaggerBack;
	public boolean bKnockedDown;
	private boolean bBecomeCrawler;
	private boolean bFakeDead;
	private boolean bForceFakeDead;
	private boolean bWasFakeDead;
	private boolean bReanimate;
	public Texture atlasTex;
	private boolean bReanimatedPlayer;
	public boolean bIndoorZombie;
	public int thumpFlag;
	public boolean thumpSent;
	public boolean mpIdleSound;
	public float nextIdleSound;
	public static final float EAT_BODY_DIST = 1.0F;
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
	private float soundReactDelay;
	private final IsoGameCharacter.Location delayedSound;
	private boolean bSoundSourceRepeating;
	public Object soundSourceTarget;
	public float soundAttract;
	public float soundAttractTimeout;
	private final Vector2 hitAngle;
	public boolean alerted;
	private String walkType;
	private float footstepVolume;
	protected SharedDescriptors.Descriptor sharedDesc;
	public boolean bDressInRandomOutfit;
	public String pendingOutfitName;
	protected final HumanVisual humanVisual;
	private int crawlerType;
	private String playerAttackPosition;
	private float eatSpeed;
	private boolean sitAgainstWall;
	private static final int CHECK_FOR_CORPSE_TIMER_MAX = 10000;
	private float checkForCorpseTimer;
	public IsoDeadBody bodyToEat;
	public IsoMovingObject eatBodyTarget;
	private int hitTime;
	private int thumpTimer;
	private boolean hitLegsWhileOnFloor;
	public boolean collideWhileHit;
	private float m_characterTextureAnimTime;
	private float m_characterTextureAnimDuration;
	public int lastPlayerHit;
	protected final ItemVisuals itemVisuals;
	private int hitHeadWhileOnFloor;
	private BaseVehicle vehicle4testCollision;
	public String SpriteName;
	public static final int PALETTE_COUNT = 3;
	public final Vector2 vectorToTarget;
	public float AllowRepathDelay;
	public boolean KeepItReal;
	public boolean Deaf;
	private boolean isSkeleton;
	private final ParameterCharacterInside parameterCharacterInside;
	private final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed;
	private final ParameterFootstepMaterial parameterFootstepMaterial;
	private final ParameterFootstepMaterial2 parameterFootstepMaterial2;
	private final ParameterPlayerDistance parameterPlayerDistance;
	private final ParameterShoeType parameterShoeType;
	private final ParameterVehicleHitLocation parameterVehicleHitLocation;
	public final ParameterZombieState parameterZombieState;
	public NetworkZombieAI networkAI;
	private static final SharedSkeleAnimationRepository m_sharedSkeleRepo = new SharedSkeleAnimationRepository();
	public int palette;
	public ArrayDeque attackNetworkEvents;
	public IsoZombie.AttackNetworkEvent currentAttackNetworkEvent;
	public int AttackAnimTime;
	public static int AttackAnimTimeMax = 50;
	public IsoMovingObject spottedLast;
	public int spotSoundDelay;
	public float movex;
	public float movey;
	private int stepFrameLast;
	private OnceEvery networkUpdate;
	public short lastRemoteUpdate;
	public short OnlineID;
	private static final ArrayList tempBodies = new ArrayList();
	float timeSinceRespondToSound;
	public String walkVariantUse;
	public String walkVariant;
	public boolean bLunger;
	public boolean bRunning;
	public boolean bCrawling;
	private boolean bCanCrawlUnderVehicle;
	private boolean bCanWalk;
	public int MoveDelay;
	public boolean bRemote;
	private static final IsoZombie.FloodFill floodFill = new IsoZombie.FloodFill();
	public boolean ImmortalTutorialZombie;

	public String getObjectName() {
		return "Zombie";
	}

	public int getOnlineID() {
		return this.OnlineID;
	}

	public void setVehicle4TestCollision(BaseVehicle baseVehicle) {
		this.vehicle4testCollision = baseVehicle;
	}

	public IsoZombie(IsoCell cell) {
		this(cell, (SurvivorDesc)null, -1);
	}

	public IsoZombie(IsoCell cell, SurvivorDesc survivorDesc, int int1) {
		super(cell, 0.0F, 0.0F, 0.0F);
		this.alwaysKnockedDown = false;
		this.onlyJawStab = false;
		this.forceEatingAnimation = false;
		this.noTeeth = false;
		this.HurtPlayerTimer = 10;
		this.LastTargetSeenX = -1;
		this.LastTargetSeenY = -1;
		this.LastTargetSeenZ = -1;
		this.Ghost = false;
		this.LungeTimer = 0.0F;
		this.LungeSoundTime = 0L;
		this.iIgnoreDirectionChange = 0;
		this.TimeSinceSeenFlesh = 100000.0F;
		this.targetSeenTime = 0.0F;
		this.FollowCount = 0;
		this.ZombieID = 0;
		this.BonusSpotTime = 0.0F;
		this.bStaggerBack = false;
		this.bKnockedDown = false;
		this.bBecomeCrawler = false;
		this.bFakeDead = false;
		this.bForceFakeDead = false;
		this.bWasFakeDead = false;
		this.bReanimate = false;
		this.atlasTex = null;
		this.bReanimatedPlayer = false;
		this.bIndoorZombie = false;
		this.thumpFlag = 0;
		this.thumpSent = false;
		this.mpIdleSound = false;
		this.nextIdleSound = 0.0F;
		this.useless = false;
		this.speedType = -1;
		this.inactive = false;
		this.strength = -1;
		this.cognition = -1;
		this.itemsToSpawnAtDeath = null;
		this.serverState = "-";
		this.soundReactDelay = 0.0F;
		this.delayedSound = new IsoGameCharacter.Location(-1, -1, -1);
		this.bSoundSourceRepeating = false;
		this.soundSourceTarget = null;
		this.soundAttract = 0.0F;
		this.soundAttractTimeout = 0.0F;
		this.hitAngle = new Vector2();
		this.alerted = false;
		this.walkType = null;
		this.footstepVolume = 1.0F;
		this.bDressInRandomOutfit = false;
		this.humanVisual = new HumanVisual(this);
		this.crawlerType = 0;
		this.playerAttackPosition = null;
		this.eatSpeed = 1.0F;
		this.sitAgainstWall = false;
		this.checkForCorpseTimer = 10000.0F;
		this.bodyToEat = null;
		this.hitTime = 0;
		this.thumpTimer = 0;
		this.hitLegsWhileOnFloor = false;
		this.collideWhileHit = true;
		this.m_characterTextureAnimTime = 0.0F;
		this.m_characterTextureAnimDuration = 1.0F;
		this.lastPlayerHit = -1;
		this.itemVisuals = new ItemVisuals();
		this.hitHeadWhileOnFloor = 0;
		this.vehicle4testCollision = null;
		this.SpriteName = "BobZ";
		this.vectorToTarget = new Vector2();
		this.AllowRepathDelay = 0.0F;
		this.KeepItReal = false;
		this.Deaf = false;
		this.isSkeleton = false;
		this.parameterCharacterInside = new ParameterCharacterInside(this);
		this.parameterCharacterMovementSpeed = new ParameterCharacterMovementSpeed(this);
		this.parameterFootstepMaterial = new ParameterFootstepMaterial(this);
		this.parameterFootstepMaterial2 = new ParameterFootstepMaterial2(this);
		this.parameterPlayerDistance = new ParameterPlayerDistance(this);
		this.parameterShoeType = new ParameterShoeType(this);
		this.parameterVehicleHitLocation = new ParameterVehicleHitLocation();
		this.parameterZombieState = new ParameterZombieState(this);
		this.networkAI = null;
		this.palette = 0;
		this.attackNetworkEvents = new ArrayDeque();
		this.currentAttackNetworkEvent = null;
		this.AttackAnimTime = 50;
		this.spottedLast = null;
		this.spotSoundDelay = 0;
		this.stepFrameLast = -1;
		this.networkUpdate = new OnceEvery(1.0F);
		this.lastRemoteUpdate = 0;
		this.OnlineID = -1;
		this.timeSinceRespondToSound = 1000000.0F;
		this.walkVariantUse = null;
		this.walkVariant = "ZombieWalk";
		this.bCanCrawlUnderVehicle = true;
		this.bCanWalk = true;
		this.MoveDelay = 0;
		this.registerVariableCallbacks();
		this.Health = 1.8F + Rand.Next(0.0F, 0.3F);
		this.weight = 0.7F;
		this.dir = IsoDirections.fromIndex(Rand.Next(8));
		this.humanVisual.randomBlood();
		if (survivorDesc != null) {
			this.descriptor = survivorDesc;
			this.palette = int1;
		} else {
			this.descriptor = SurvivorFactory.CreateSurvivor();
			this.palette = Rand.Next(3) + 1;
		}

		this.setFemale(this.descriptor.isFemale());
		this.SpriteName = this.isFemale() ? "KateZ" : "BobZ";
		if (this.palette != 1) {
			this.SpriteName = this.SpriteName + this.palette;
		}

		this.InitSpritePartsZombie();
		this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.setDefaultState(ZombieIdleState.instance());
		this.setFakeDead(false);
		this.DoZombieStats();
		this.width = 0.3F;
		this.setAlphaAndTarget(0.0F);
		this.finder.maxSearchDistance = 20;
		if (this.isFemale()) {
			this.hurtSound = "FemaleZombieHurt";
		}

		this.initializeStates();
		this.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
		this.initWornItems("Human");
		this.initAttachedItems("Human");
		this.networkAI = new NetworkZombieAI(this);
	}

	public void initializeStates() {
		HashMap hashMap = this.getStateUpdateLookup();
		hashMap.clear();
		hashMap.put("attack-network", AttackNetworkState.instance());
		hashMap.put("attackvehicle-network", IdleState.instance());
		hashMap.put("fakedead-attack-network", IdleState.instance());
		hashMap.put("lunge-network", LungeNetworkState.instance());
		hashMap.put("walktoward-network", WalkTowardNetworkState.instance());
		if (this.bCrawling) {
			hashMap.put("attack", AttackState.instance());
			hashMap.put("fakedead", FakeDeadZombieState.instance());
			hashMap.put("fakedead-attack", FakeDeadAttackState.instance());
			hashMap.put("getup", ZombieGetUpFromCrawlState.instance());
			hashMap.put("hitreaction", ZombieHitReactionState.instance());
			hashMap.put("hitreaction-hit", ZombieHitReactionState.instance());
			hashMap.put("idle", ZombieIdleState.instance());
			hashMap.put("onground", ZombieOnGroundState.instance());
			hashMap.put("pathfind", PathFindState.instance());
			hashMap.put("reanimate", ZombieReanimateState.instance());
			hashMap.put("staggerback", StaggerBackState.instance());
			hashMap.put("thump", ThumpState.instance());
			hashMap.put("turn", CrawlingZombieTurnState.instance());
			hashMap.put("walktoward", WalkTowardState.instance());
		} else {
			hashMap.put("attack", AttackState.instance());
			hashMap.put("attackvehicle", AttackVehicleState.instance());
			hashMap.put("bumped", BumpedState.instance());
			hashMap.put("climbfence", ClimbOverFenceState.instance());
			hashMap.put("climbwindow", ClimbThroughWindowState.instance());
			hashMap.put("eatbody", ZombieEatBodyState.instance());
			hashMap.put("falldown", ZombieFallDownState.instance());
			hashMap.put("falling", ZombieFallingState.instance());
			hashMap.put("face-target", ZombieFaceTargetState.instance());
			hashMap.put("fakedead", FakeDeadZombieState.instance());
			hashMap.put("fakedead-attack", FakeDeadAttackState.instance());
			hashMap.put("getdown", ZombieGetDownState.instance());
			hashMap.put("getup", ZombieGetUpState.instance());
			hashMap.put("hitreaction", ZombieHitReactionState.instance());
			hashMap.put("hitreaction-hit", ZombieHitReactionState.instance());
			hashMap.put("idle", ZombieIdleState.instance());
			hashMap.put("lunge", LungeState.instance());
			hashMap.put("onground", ZombieOnGroundState.instance());
			hashMap.put("pathfind", PathFindState.instance());
			hashMap.put("sitting", ZombieSittingState.instance());
			hashMap.put("staggerback", StaggerBackState.instance());
			hashMap.put("thump", ThumpState.instance());
			hashMap.put("turnalerted", ZombieTurnAlerted.instance());
			hashMap.put("walktoward", WalkTowardState.instance());
		}
	}

	private void registerVariableCallbacks() {
		this.setVariable("bClient", ()->{
			return GameClient.bClient;
		});
		this.setVariable("bMovingNetwork", ()->{
			return IsoUtils.DistanceManhatten(this.networkAI.targetX, this.networkAI.targetY, this.x, this.y) > 0.5F || this.z != (float)this.networkAI.targetZ;
		});
		this.setVariable("hitHeadType", this::getHitHeadWhileOnFloor);
		this.setVariable("battack", ()->{
			if (this.target == null) {
				return false;
			} else {
				if (this.target instanceof IsoGameCharacter) {
					if (this.target.isOnFloor() && ((IsoGameCharacter)this.target).getCurrentState() != BumpedState.instance()) {
						this.setTarget((IsoMovingObject)null);
						return false;
					}

					BaseVehicle baseVehicle = ((IsoGameCharacter)this.target).getVehicle();
					if (baseVehicle != null) {
						return false;
					}

					if (((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
						return false;
					}

					if (((IsoGameCharacter)this.target).getStateMachine().getCurrent() == ClimbOverWallState.instance()) {
						return false;
					}
				}

				if (this.bReanimate) {
					return false;
				} else if (Math.abs(this.target.z - this.z) >= 0.2F) {
					return false;
				} else if (this.target instanceof IsoPlayer && ((IsoPlayer)this.target).isGhostMode()) {
					return false;
				} else if (this.bFakeDead) {
					return !this.isUnderVehicle() && this.DistTo(this.target) < 1.3F;
				} else if (!this.bCrawling) {
					IsoGridSquare square = this.getCurrentSquare();
					IsoGridSquare square2 = this.target.getCurrentSquare();
					if (square != null && square.isSomethingTo(square2)) {
						return false;
					} else if (GameClient.bClient && !this.networkAI.isLocalControl() && this.attackNetworkEvents.isEmpty()) {
						return false;
					} else {
						float float1 = this.bCrawling ? 1.4F : 0.72F;
						float float2 = this.vectorToTarget.getLength();
						return float2 <= float1;
					}
				} else {
					return !this.isUnderVehicle() && this.DistTo(this.target) < 1.3F;
				}
			}
		});
		this.setVariable("isFacingTarget", this::isFacingTarget);
		this.setVariable("targetSeenTime", this::getTargetSeenTime);
		this.setVariable("targethitreaction", ()->{
			return this.target != null ? ((IsoGameCharacter)this.target).getHitReaction() : "";
		});
		this.setVariable("battackvehicle", ()->{
			if (this.getVariableBoolean("bPathfind")) {
				return false;
			} else if (this.isMoving()) {
				return false;
			} else if (this.target == null) {
				return false;
			} else if (Math.abs(this.target.z - this.z) >= 0.8F) {
				return false;
			} else if (this.target instanceof IsoPlayer && ((IsoPlayer)this.target).isGhostMode()) {
				return false;
			} else if (!(this.target instanceof IsoGameCharacter)) {
				return false;
			} else {
				BaseVehicle baseVehicle = ((IsoGameCharacter)this.target).getVehicle();
				return baseVehicle != null && baseVehicle.isCharacterAdjacentTo(this);
			}
		});
		this.setVariable("bdead", this::isDead);
		this.setVariable("beatbodytarget", ()->{
			if (this.isForceEatingAnimation()) {
				return true;
			} else {
				if (!GameClient.bClient) {
					this.updateEatBodyTarget();
				}

				return this.getEatBodyTarget() != null;
			}
		});
		this.setVariable("bbecomecrawler", this::isBecomeCrawler, this::setBecomeCrawler);
		this.setVariable("bfakedead", ()->{
			return this.bFakeDead;
		});
		this.setVariable("bfalling", ()->{
			return this.z > 0.0F && this.fallTime > 2.0F;
		});
		this.setVariable("bhastarget", ()->{
			if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
				this.setTarget((IsoMovingObject)null);
			}

			return this.target != null;
		});
		this.setVariable("shouldSprint", ()->{
			if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
				this.setTarget((IsoMovingObject)null);
			}

			return this.target != null || this.soundSourceTarget != null && !(this.soundSourceTarget instanceof IsoZombie);
		});
		this.setVariable("bknockeddown", ()->{
			return this.bKnockedDown;
		});
		this.setVariable("blunge", ()->{
			if (this.target == null) {
				return false;
			} else if ((int)this.getZ() != (int)this.target.getZ()) {
				return false;
			} else {
				if (this.target instanceof IsoGameCharacter) {
					if (((IsoGameCharacter)this.target).getVehicle() != null) {
						return false;
					}

					if (((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
						return false;
					}
				}

				if (this.target instanceof IsoPlayer && ((IsoPlayer)this.target).isGhostMode()) {
					this.setTarget((IsoMovingObject)null);
					return false;
				} else {
					IsoGridSquare baseVehicle = this.getCurrentSquare();
					IsoGridSquare square2 = this.target.getCurrentSquare();
					if (square2 != null && square2.isSomethingTo(baseVehicle) && this.getThumpTarget() != null) {
						return false;
					} else if (this.isCurrentState(ZombieTurnAlerted.instance()) && !this.isFacingTarget()) {
						return false;
					} else {
						float float1 = this.vectorToTarget.getLength();
						return float1 > 3.5F && (!(float1 <= 4.0F) || !(this.target instanceof IsoGameCharacter) || ((IsoGameCharacter)this.target).getVehicle() == null) ? false : !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), this.target.x, this.target.y, (int)this.getZ(), this.target, false, true);
					}
				}
			}
		});
		this.setVariable("bpassengerexposed", ()->{
			return AttackVehicleState.instance().isPassengerExposed(this);
		});
		this.setVariable("bistargetissmallvehicle", ()->{
			return this.target != null && this.target instanceof IsoPlayer && ((IsoPlayer)this.target).getVehicle() != null ? ((IsoPlayer)this.target).getVehicle().getScript().isSmallVehicle : true;
		});
		this.setVariable("breanimate", this::isReanimate, this::setReanimate);
		this.setVariable("brunning", ()->{
			return this.bRunning;
		});
		this.setVariable("bstaggerback", ()->{
			return this.bStaggerBack;
		});
		this.setVariable("btargetvehicle", ()->{
			return this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null;
		});
		this.setVariable("bthump", ()->{
			if (this.getThumpTarget() instanceof IsoObject && !(this.getThumpTarget() instanceof BaseVehicle)) {
				IsoObject baseVehicle = (IsoObject)this.getThumpTarget();
				if (baseVehicle != null && this.DistToSquared(baseVehicle.getX() + 0.5F, baseVehicle.getY() + 0.5F) > 9.0F) {
					this.setThumpTarget((Thumpable)null);
				}
			}

			if (this.getThumpTimer() > 0) {
				this.setThumpTarget((Thumpable)null);
			}

			return this.getThumpTarget() != null;
		});
		this.setVariable("bundervehicle", this::isUnderVehicle);
		this.setVariable("bBeingSteppedOn", this::isBeingSteppedOn);
		this.setVariable("distancetotarget", ()->{
			return this.target == null ? "" : String.valueOf(this.vectorToTarget.getLength() - this.getWidth() + this.target.getWidth());
		});
		this.setVariable("lasttargetseen", ()->{
			return this.LastTargetSeenX != -1;
		});
		this.setVariable("lungetimer", ()->{
			return this.LungeTimer;
		});
		this.setVariable("reanimatetimer", this::getReanimateTimer);
		this.setVariable("stateeventdelaytimer", this::getStateEventDelayTimer);
		this.setVariable("turndirection", ()->{
			if (this.getPath2() != null) {
				return "";
			} else {
				IsoDirections baseVehicle;
				boolean square2;
				if (this.target != null && this.vectorToTarget.getLength() != 0.0F) {
					baseVehicle = IsoDirections.fromAngle(this.vectorToTarget);
					if (this.dir == baseVehicle) {
						return "";
					} else {
						square2 = CrawlingZombieTurnState.calculateDir(this, baseVehicle);
						return square2 ? "left" : "right";
					}
				} else if (this.isCurrentState(WalkTowardState.instance())) {
					WalkTowardState.instance().calculateTargetLocation(this, tempo);
					Vector2 vector2 = tempo;
					vector2.x -= this.getX();
					vector2 = tempo;
					vector2.y -= this.getY();
					baseVehicle = IsoDirections.fromAngle(tempo);
					if (this.dir == baseVehicle) {
						return "";
					} else {
						square2 = CrawlingZombieTurnState.calculateDir(this, baseVehicle);
						return square2 ? "left" : "right";
					}
				} else {
					if (this.isCurrentState(PathFindState.instance())) {
					}

					return "";
				}
			}
		});
		this.setVariable("hitforce", this::getHitForce);
		this.setVariable("alerted", ()->{
			return this.alerted;
		});
		this.setVariable("zombiewalktype", ()->{
			return this.walkType;
		});
		this.setVariable("crawlertype", ()->{
			return this.crawlerType;
		});
		this.setVariable("bGetUpFromCrawl", this::shouldGetUpFromCrawl);
		this.setVariable("playerattackposition", this::getPlayerAttackPosition);
		this.setVariable("eatspeed", ()->{
			return this.eatSpeed;
		});
		this.setVariable("issitting", this::isSitAgainstWall);
	}

	public void actionStateChanged(ActionContext actionContext) {
		super.actionStateChanged(actionContext);
		if (this.networkAI != null && GameServer.bServer) {
			this.networkAI.extraUpdate();
		}
	}

	public ActionContext getActionContext() {
		return this.actionContext;
	}

	protected void onAnimPlayerCreated(AnimationPlayer animationPlayer) {
		super.onAnimPlayerCreated(animationPlayer);
		animationPlayer.setSharedAnimRepo(m_sharedSkeleRepo);
	}

	public String GetAnimSetName() {
		return this.bCrawling ? "zombie-crawler" : "zombie";
	}

	public void InitSpritePartsZombie() {
		SurvivorDesc survivorDesc = this.descriptor;
		this.InitSpritePartsZombie(survivorDesc);
	}

	public void InitSpritePartsZombie(SurvivorDesc survivorDesc) {
		this.sprite.AnimMap.clear();
		this.sprite.AnimStack.clear();
		this.sprite.CurrentAnim = new IsoAnim();
		this.sprite.CurrentAnim.name = "REMOVE";
		this.legsSprite = this.sprite;
		this.legsSprite.name = survivorDesc.torso;
		this.ZombieID = Rand.Next(10000);
		this.bUseParts = true;
	}

	public void pathToCharacter(IsoGameCharacter gameCharacter) {
		if (!(this.AllowRepathDelay > 0.0F) || !this.isCurrentState(PathFindState.instance()) && !this.isCurrentState(WalkTowardState.instance()) && !this.isCurrentState(WalkTowardNetworkState.instance())) {
			super.pathToCharacter(gameCharacter);
		}
	}

	public void pathToLocationF(float float1, float float2, float float3) {
		if (!(this.AllowRepathDelay > 0.0F) || !this.isCurrentState(PathFindState.instance()) && !this.isCurrentState(WalkTowardState.instance()) && !this.isCurrentState(WalkTowardNetworkState.instance())) {
			super.pathToLocationF(float1, float2, float3);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.walkVariant = "ZombieWalk";
		this.SpriteName = "BobZ";
		if (this.palette != 1) {
			this.SpriteName = this.SpriteName + this.palette;
		}

		SurvivorDesc survivorDesc = this.descriptor;
		this.setFemale(survivorDesc.isFemale());
		if (this.isFemale()) {
			if (this.palette == 1) {
				this.SpriteName = "KateZ";
			} else {
				this.SpriteName = "KateZ" + this.palette;
			}
		}

		if (this.isFemale()) {
			this.hurtSound = "FemaleZombieHurt";
		} else {
			this.hurtSound = "MaleZombieHurt";
		}

		this.InitSpritePartsZombie(survivorDesc);
		this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
		this.setDefaultState(ZombieIdleState.instance());
		this.DoZombieStats();
		byteBuffer.getFloat();
		this.setWidth(0.3F);
		this.TimeSinceSeenFlesh = (float)byteBuffer.getInt();
		this.setAlpha(0.0F);
		this.setFakeDead(byteBuffer.getInt() == 1);
		ArrayList arrayList = this.savedInventoryItems;
		byte byte1 = byteBuffer.get();
		for (int int2 = 0; int2 < byte1; ++int2) {
			String string = GameWindow.ReadString(byteBuffer);
			short short1 = byteBuffer.getShort();
			if (short1 >= 0 && short1 < arrayList.size() && this.wornItems.getBodyLocationGroup().getLocation(string) != null) {
				this.wornItems.setItem(string, (InventoryItem)arrayList.get(short1));
			}
		}

		this.setStateMachineLocked(false);
		this.setDefaultState();
		this.getCell().getZombieList().add(this);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putFloat(0.0F);
		byteBuffer.putInt((int)this.TimeSinceSeenFlesh);
		byteBuffer.putInt(this.isFakeDead() ? 1 : 0);
		if (this.wornItems.size() > 127) {
			throw new RuntimeException("too many worn items");
		} else {
			byteBuffer.put((byte)this.wornItems.size());
			this.wornItems.forEach((boolean1x)->{
				GameWindow.WriteString(byteBuffer, boolean1x.getLocation());
				byteBuffer.putShort((short)this.savedInventoryItems.indexOf(boolean1x.getItem()));
			});
		}
	}

	public void collideWith(IsoObject object) {
		if (!this.Ghost && object != null) {
			if (object.rerouteCollide != null) {
				object = this.rerouteCollide;
			}

			State state = this.getCurrentState();
			boolean boolean1 = this.isCurrentState(PathFindState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance());
			IsoWindow window = (IsoWindow)Type.tryCastTo(object, IsoWindow.class);
			if (window != null && window.canClimbThrough(this) && boolean1) {
				if (!this.isFacingObject(window, 0.8F)) {
					super.collideWith(object);
					return;
				}

				if ((state != PathFindState.instance() || this.isOnPath(object)) && !this.bCrawling) {
					this.climbThroughWindow(window);
				}
			} else if (object instanceof IsoThumpable && ((IsoThumpable)object).canClimbThrough(this) && boolean1) {
				if ((state != PathFindState.instance() || this.isOnPath(object)) && !this.bCrawling) {
					this.climbThroughWindow((IsoThumpable)object);
				}
			} else if ((!(object instanceof IsoDoor) || !((IsoDoor)object).isHoppable()) && object != null && object.getThumpableFor(this) != null && boolean1) {
				boolean boolean2 = (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance())) && this.getPathFindBehavior2().isGoalSound();
				if (!SandboxOptions.instance.Lore.ThumpNoChasing.getValue() && this.target == null && !boolean2) {
					this.setVariable("bPathfind", false);
					this.setVariable("bMoving", false);
					this.setPath2((PolygonalMap2.Path)null);
				} else {
					if (object instanceof IsoThumpable && !SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
						return;
					}

					Object object2 = object;
					if (object instanceof IsoWindow && ((IsoWindow)object).getThumpableFor(this) != null && ((IsoWindow)object).isDestroyed()) {
						object2 = ((IsoWindow)object).getThumpableFor(this);
					}

					this.setThumpTarget((Thumpable)object2);
					this.setPath2((PolygonalMap2.Path)null);
				}
			}

			if (!this.bCrawling && IsoWindowFrame.isWindowFrame(object) && boolean1 && (state != PathFindState.instance() || this.isOnPath(object))) {
				this.climbThroughWindowFrame(object);
			}

			super.collideWith(object);
		}
	}

	private boolean isOnPath(IsoObject object) {
		return false;
	}

	public float Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2, boolean boolean2) {
		if (Core.bTutorial && this.ImmortalTutorialZombie) {
			return 0.0F;
		} else {
			BodyPartType bodyPartType = BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Upper), BodyPartType.ToIndex(BodyPartType.Torso_Lower) + 1));
			if (Rand.NextBool(7)) {
				bodyPartType = BodyPartType.Head;
			}

			if (gameCharacter.isCrit && Rand.NextBool(3)) {
				bodyPartType = BodyPartType.Head;
			}

			LuaEventManager.triggerEvent("OnHitZombie", this, gameCharacter, bodyPartType, handWeapon);
			float float3 = super.Hit(handWeapon, gameCharacter, float1, boolean1, float2, boolean2);
			if (this.Health <= 0.0F && !this.isOnDeathDone()) {
				this.DoZombieInventory();
				LuaEventManager.triggerEvent("OnZombieDead", this);
				this.setOnDeathDone(true);
			}

			this.TimeSinceSeenFlesh = 0.0F;
			if (!this.isDead() && !this.emitter.isPlaying(this.getHurtSound())) {
				this.playHurtSound();
			}

			if (!this.isDead() && !this.isOnFloor() && !boolean1 && handWeapon != null && handWeapon.getScriptItem().getCategories().contains("Blade") && gameCharacter instanceof IsoPlayer && this.DistToProper(gameCharacter) <= 0.9F && (this.isCurrentState(AttackState.instance()) || this.isCurrentState(AttackNetworkState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()))) {
				this.setHitForce(0.5F);
				this.changeState(StaggerBackState.instance());
			}

			if (GameServer.bServer || GameClient.bClient && this.isDead()) {
				this.lastPlayerHit = gameCharacter.getOnlineID();
			}

			return float3;
		}
	}

	public void onMouseLeftClick() {
		if (IsoPlayer.getInstance() == null || !IsoPlayer.getInstance().isAiming()) {
			if (IsoPlayer.getInstance().IsAttackRange(this.getX(), this.getY(), this.getZ())) {
				Vector2 vector2 = new Vector2(this.getX(), this.getY());
				vector2.x -= IsoPlayer.getInstance().getX();
				vector2.y -= IsoPlayer.getInstance().getY();
				vector2.normalize();
				IsoPlayer.getInstance().DirectionFromVector(vector2);
				IsoPlayer.getInstance().AttemptAttack();
			}
		}
	}

	private void renderAtlasTexture(float float1, float float2, float float3) {
		if (this.atlasTex != null) {
			if (IsoSprite.globalOffsetX == -1.0F) {
				IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
				IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
			}

			float float4 = IsoUtils.XToScreen(float1, float2, float3, 0);
			float float5 = IsoUtils.YToScreen(float1, float2, float3, 0);
			this.sx = float4;
			this.sy = float5;
			float4 = this.sx + IsoSprite.globalOffsetX;
			float5 = this.sy + IsoSprite.globalOffsetY;
			ColorInfo colorInfo = inf.set(1.0F, 1.0F, 1.0F, 1.0F);
			if (PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
				this.getCurrentSquare().interpolateLight(colorInfo, float1 - (float)this.getCurrentSquare().getX(), float2 - (float)this.getCurrentSquare().getY());
			}

			this.atlasTex.render((float)((int)float4 - this.atlasTex.getWidth() / 2), (float)((int)float5 - this.atlasTex.getHeight() / 2), (float)this.atlasTex.getWidth(), (float)this.atlasTex.getHeight(), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a, (Consumer)null);
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.getCurrentState() == FakeDeadZombieState.instance()) {
			if (this.bDressInRandomOutfit) {
				ModelManager.instance.dressInRandomOutfit(this);
			}

			if (this.atlasTex == null) {
				this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
				DeadBodyAtlas.instance.render();
			}

			if (this.atlasTex != null) {
				this.renderAtlasTexture(float1, float2, float3);
			}
		} else {
			if (this.atlasTex != null) {
				this.atlasTex = null;
			}

			if (IsoCamera.CamCharacter != IsoPlayer.getInstance()) {
				this.setAlphaAndTarget(1.0F);
			}

			super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
		}
	}

	public void renderlast() {
		super.renderlast();
		if (DebugOptions.instance.ZombieRenderCanCrawlUnderVehicle.getValue() && this.isCanCrawlUnderVehicle()) {
			this.renderTextureOverHead("media/ui/FavoriteStar.png");
		}

		if (DebugOptions.instance.ZombieRenderMemory.getValue()) {
			String string;
			if (this.target == null) {
				string = "media/ui/Moodles/Moodle_Icon_Bored.png";
			} else if (this.BonusSpotTime == 0.0F) {
				string = "media/ui/Moodles/Moodle_Icon_Angry.png";
			} else {
				string = "media/ui/Moodles/Moodle_Icon_Zombie.png";
			}

			this.renderTextureOverHead(string);
			int int1 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
			int int2 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
			int int3 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
			TextManager.instance.DrawString((double)int1, (double)(int2 += int3), "AllowRepathDelay : " + this.AllowRepathDelay);
			TextManager.instance.DrawString((double)int1, (double)(int2 += int3), "BonusSpotTime : " + this.BonusSpotTime);
			TextManager.instance.DrawString((double)int1, (double)(int2 + int3), "TimeSinceSeenFlesh : " + this.TimeSinceSeenFlesh);
		}
	}

	protected boolean renderTextureInsteadOfModel(float float1, float float2) {
		boolean boolean1 = this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(PathFindState.instance());
		String string = "zombie";
		String string2 = boolean1 ? "walktoward" : "idle";
		byte byte1 = 4;
		int int1 = (int)(this.m_characterTextureAnimTime / this.m_characterTextureAnimDuration * (float)byte1);
		float float3 = (boolean1 ? 0.67F : 1.0F) * ((float)int1 / (float)byte1);
		Texture texture = DeadBodyAtlas.instance.getBodyTexture(this.isFemale(), string, string2, this.getDir(), int1, float3);
		if (texture != null && texture.isReady()) {
			float float4 = (float)Core.TileScale;
			float float5 = this.offsetX + 1.0F * float4;
			float float6 = this.offsetY + -89.0F * float4;
			float float7 = IsoUtils.XToScreen(float1, float2, this.getZ(), 0);
			float float8 = IsoUtils.YToScreen(float1, float2, this.getZ(), 0);
			float7 = float7 - IsoCamera.getOffX() - float5;
			float8 = float8 - IsoCamera.getOffY() - float6;
			float7 -= (float)(texture.getWidthOrig() / 2);
			float8 -= (float)texture.getHeightOrig();
			float8 -= 64.0F * float4;
			int int2 = IsoCamera.frameState.playerIndex;
			texture.render(float7, float8, (float)texture.getWidth(), (float)texture.getHeight(), 0.0F, 0.0F, 0.0F, this.getAlpha(int2), (Consumer)null);
		}

		if (DebugOptions.instance.Character.Debug.Render.Angle.getValue()) {
			tempo.set(this.dir.ToVector());
			this.drawDirectionLine(tempo, 1.2F, 0.0F, 1.0F, 0.0F);
		}

		return true;
	}

	private void renderTextureOverHead(String string) {
		float float1 = this.x;
		float float2 = this.y;
		float float3 = IsoUtils.XToScreen(float1, float2, this.getZ(), 0);
		float float4 = IsoUtils.YToScreen(float1, float2, this.getZ(), 0);
		float3 = float3 - IsoCamera.getOffX() - this.offsetX;
		float4 = float4 - IsoCamera.getOffY() - this.offsetY;
		float4 -= (float)(128 / (2 / Core.TileScale));
		Texture texture = Texture.getSharedTexture(string);
		float float5 = Core.getInstance().getZoom(IsoCamera.frameState.playerIndex);
		float5 = Math.max(float5, 1.0F);
		int int1 = (int)((float)texture.getWidth() * float5);
		int int2 = (int)((float)texture.getHeight() * float5);
		texture.render((float)((int)float3 - int1 / 2), (float)((int)float4 - int2), (float)int1, (float)int2);
	}

	protected void updateAlpha(int int1, float float1, float float2) {
		if (this.isFakeDead()) {
			this.setAlphaAndTarget(1.0F);
		} else {
			super.updateAlpha(int1, float1, float2);
		}
	}

	public void RespondToSound() {
		if (!this.Ghost) {
			if (!this.Deaf) {
				if (!this.isUseless()) {
					float float1;
					if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && this.getPathFindBehavior2().isGoalSound() && (int)this.z == this.getPathTargetZ() && this.bSoundSourceRepeating) {
						float1 = this.DistToSquared((float)this.getPathTargetX(), (float)this.getPathTargetY());
						if (float1 < 25.0F && LosUtil.lineClear(this.getCell(), (int)this.x, (int)this.y, (int)this.z, this.getPathTargetX(), this.getPathTargetY(), (int)this.z, false) != LosUtil.TestResults.Blocked) {
							this.setVariable("bPathfind", false);
							this.setVariable("bMoving", false);
							this.setPath2((PolygonalMap2.Path)null);
						}
					}

					if (this.soundReactDelay > 0.0F) {
						this.soundReactDelay -= GameTime.getInstance().getMultiplier() / 1.6F;
						if (this.soundReactDelay < 0.0F) {
							this.soundReactDelay = 0.0F;
						}

						if (this.soundReactDelay > 0.0F) {
							return;
						}
					}

					float1 = 0.0F;
					Object object = null;
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

					if (worldSound != null && worldSound.bRepeating && worldSound.z == (int)this.z) {
						float float3 = this.DistToSquared((float)worldSound.x, (float)worldSound.y);
						if (float3 < 25.0F && LosUtil.lineClear(this.getCell(), (int)this.x, (int)this.y, (int)this.z, worldSound.x, worldSound.y, (int)this.z, false) != LosUtil.TestResults.Blocked) {
							worldSound = null;
						}
					}

					if (worldSound != null) {
						this.soundAttract = float1;
						this.soundSourceTarget = object;
						this.soundReactDelay = (float)Rand.Next(0, 16);
						this.delayedSound.x = worldSound.x;
						this.delayedSound.y = worldSound.y;
						this.delayedSound.z = worldSound.z;
						this.bSoundSourceRepeating = worldSound.bRepeating;
					}

					if (this.delayedSound.x != -1 && this.soundReactDelay == 0.0F) {
						int int1 = this.delayedSound.x;
						int int2 = this.delayedSound.y;
						int int3 = this.delayedSound.z;
						this.delayedSound.x = -1;
						float float4 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), (float)int1, (float)int2) / 2.5F;
						int1 += Rand.Next((int)(-float4), (int)float4);
						int2 += Rand.Next((int)(-float4), (int)float4);
						if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && (this.getPathFindBehavior2().isGoalLocation() || this.getPathFindBehavior2().isGoalSound())) {
							if (!IsoUtils.isSimilarDirection(this, (float)int1, (float)int2, this.getPathFindBehavior2().getTargetX(), this.getPathFindBehavior2().getTargetY(), 0.5F)) {
								this.setTurnAlertedValues(int1, int2);
								this.pathToSound(int1, int2, int3);
								this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
								this.AllowRepathDelay = 120.0F;
								this.timeSinceRespondToSound = 0.0F;
							}

							return;
						}

						if (this.timeSinceRespondToSound < 60.0F) {
							return;
						}

						if (!IsoUtils.isSimilarDirection(this, (float)int1, (float)int2, this.x + this.getForwardDirection().x, this.y + this.getForwardDirection().y, 0.5F)) {
							this.setTurnAlertedValues(int1, int2);
						}

						this.pathToSound(int1, int2, int3);
						this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
						this.AllowRepathDelay = 120.0F;
						this.timeSinceRespondToSound = 0.0F;
					}
				}
			}
		}
	}

	public void setTurnAlertedValues(int int1, int int2) {
		Vector2 vector2 = new Vector2(this.getX() - ((float)int1 + 0.5F), this.getY() - ((float)int2 + 0.5F));
		float float1 = vector2.getDirectionNeg();
		if (float1 < 0.0F) {
			float1 = Math.abs(float1);
		} else {
			float1 = new Float(6.283185307179586 - (double)float1);
		}

		Double Double1 = new Double(Math.toDegrees((double)float1));
		Vector2 vector22 = new Vector2(IsoDirections.reverse(this.getDir()).ToVector().x, IsoDirections.reverse(this.getDir()).ToVector().y);
		vector22.normalize();
		float float2 = vector22.getDirectionNeg();
		if (float2 < 0.0F) {
			float2 = Math.abs(float2);
		} else {
			float2 = 6.2831855F - float2;
		}

		double double1 = Math.toDegrees((double)float2);
		if ((int)double1 == 360) {
			double1 = 0.0;
		}

		if (Double1.intValue() == 360) {
			Double1 = 0.0;
		}

		String string = "0";
		boolean boolean1 = false;
		int int3;
		if (Double1 > double1) {
			int3 = (int)(Double1 - double1);
			if (int3 > 350 || int3 <= 35) {
				string = "45R";
			}

			if (int3 > 35 && int3 <= 80) {
				string = "90R";
			}

			if (int3 > 80 && int3 <= 125) {
				string = "135R";
			}

			if (int3 > 125 && int3 <= 170) {
				string = "180R";
			}

			if (int3 > 170 && int3 < 215) {
				string = "180L";
			}

			if (int3 >= 215 && int3 < 260) {
				string = "135L";
			}

			if (int3 >= 260 && int3 < 305) {
				string = "90L";
			}

			if (int3 >= 305 && int3 < 350) {
				string = "45L";
			}
		} else {
			int3 = (int)(double1 - Double1);
			if (int3 > 10 && int3 <= 55) {
				string = "45L";
			}

			if (int3 > 55 && int3 <= 100) {
				string = "90L";
			}

			if (int3 > 100 && int3 <= 145) {
				string = "135L";
			}

			if (int3 > 145 && int3 <= 190) {
				string = "180L";
			}

			if (int3 > 190 && int3 < 235) {
				string = "180R";
			}

			if (int3 >= 235 && int3 < 280) {
				string = "135R";
			}

			if (int3 >= 280 && int3 < 325) {
				string = "90R";
			}

			if (int3 >= 325 || int3 < 10) {
				string = "45R";
			}
		}

		this.setVariable("turnalertedvalue", string);
		ZombieTurnAlerted.instance().setParams(this, vector2.set((float)int1 + 0.5F - this.x, (float)int2 + 0.5F - this.y).getDirection());
		this.alerted = true;
		this.networkAI.extraUpdate();
	}

	public void spotted(IsoMovingObject movingObject, boolean boolean1) {
		if (!GameClient.bClient) {
			if (this.getCurrentSquare() != null) {
				if (movingObject.getCurrentSquare() != null) {
					if (!this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke) && !this.isUseless()) {
						if (!(movingObject instanceof IsoPlayer) || !((IsoPlayer)movingObject).isGhostMode()) {
							IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
							if (gameCharacter == null || !gameCharacter.isDead()) {
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

								if (movingObject.getCurrentSquare().getRoom() != this.getCurrentSquare().getRoom()) {
									float1 = 50.0F;
									if (movingObject.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() == null || movingObject.getCurrentSquare().getRoom() == null && this.getCurrentSquare().getRoom() != null) {
										float1 = 20.0F;
										if (!gameCharacter.isAiming() && !gameCharacter.isSneaking()) {
											if (movingObject.getMovementLastFrame().getLength() <= 0.04F && float2 < 0.4F) {
												float1 = 10.0F;
											}
										} else if (float2 < 0.4F) {
											float1 = 0.0F;
										} else {
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
									Vector2 vector22 = this.getLookVector(tempo2);
									float float7 = vector22.dot(tempo);
									if (this.DistTo(movingObject) > 20.0F) {
										float1 -= 10000.0F;
									}

									if ((double)float6 > 0.5) {
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

									float float8 = PZMath.clamp(float6 / GameTime.getInstance().getViewDist(), 0.0F, 1.0F);
									float1 *= 1.0F - float8;
									float1 *= 1.0F - float8;
									float1 *= 1.0F - float8;
									float float9 = PZMath.clamp(float6 / 10.0F, 0.0F, 1.0F);
									float1 *= 1.0F + (1.0F - float9) * 10.0F;
									float float10 = movingObject.getMovementLastFrame().getLength();
									if (float10 == 0.0F && float2 <= 0.2F) {
										float2 = 0.0F;
									}

									if (gameCharacter != null) {
										if (gameCharacter.getTorchStrength() > 0.0F) {
											float1 *= 3.0F;
										}

										if (float10 < 0.01F) {
											float1 *= 0.5F;
										} else if (gameCharacter.isSneaking()) {
											float1 *= 0.4F;
										} else if (gameCharacter.isAiming()) {
											float1 *= 0.75F;
										} else if (float10 < 0.06F) {
											float1 *= 0.8F;
										} else if (float10 >= 0.06F) {
											float1 *= 2.4F;
										}

										if (this.eatBodyTarget != null) {
											float1 *= 0.6F;
										}

										if (float6 < 5.0F && (!gameCharacter.isRunning() && !gameCharacter.isSneaking() && !gameCharacter.isAiming() || gameCharacter.isRunning())) {
											float1 *= 3.0F;
										}

										if (this.spottedLast == movingObject && this.TimeSinceSeenFlesh < 120.0F) {
											float1 = 1000.0F;
										}

										float1 *= gameCharacter.getSneakSpotMod();
										float1 *= float3;
										if (this.target != movingObject && this.target != null) {
											float float11 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), movingObject.getX(), movingObject.getY());
											float float12 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), this.target.getX(), this.target.getY());
											if (float11 > float12) {
												return;
											}
										}

										float1 *= 0.3F;
										if (boolean1) {
											float1 = 1000000.0F;
										}

										if (this.BonusSpotTime > 0.0F) {
											float1 = 1000000.0F;
										}

										float1 *= 1.2F;
										if (SandboxOptions.instance.Lore.Sight.getValue() == 1) {
											float1 *= 2.5F;
										}

										if (SandboxOptions.instance.Lore.Sight.getValue() == 3) {
											float1 *= 0.45F;
										}

										if (this.inactive) {
											float1 *= 0.25F;
										}

										float1 *= 0.25F;
										if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).Traits.Inconspicuous.isSet()) {
											float1 *= 0.5F;
										}

										if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).Traits.Conspicuous.isSet()) {
											float1 *= 2.0F;
										}

										float1 *= 1.6F;
										IsoGridSquare square = null;
										IsoGridSquare square2 = null;
										float float13;
										if (this.getCurrentSquare() != movingObject.getCurrentSquare() && movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).isSneaking()) {
											int int4 = Math.abs(this.getCurrentSquare().getX() - movingObject.getCurrentSquare().getX());
											int int5 = Math.abs(this.getCurrentSquare().getY() - movingObject.getCurrentSquare().getY());
											if (int4 > int5) {
												if (this.getCurrentSquare().getX() - movingObject.getCurrentSquare().getX() > 0) {
													square = movingObject.getCurrentSquare().nav[IsoDirections.E.index()];
												} else {
													square = movingObject.getCurrentSquare();
													square2 = movingObject.getCurrentSquare().nav[IsoDirections.W.index()];
												}
											} else if (this.getCurrentSquare().getY() - movingObject.getCurrentSquare().getY() > 0) {
												square = movingObject.getCurrentSquare().nav[IsoDirections.S.index()];
											} else {
												square = movingObject.getCurrentSquare();
												square2 = movingObject.getCurrentSquare().nav[IsoDirections.N.index()];
											}

											if (square != null && movingObject instanceof IsoGameCharacter) {
												float13 = ((IsoGameCharacter)movingObject).checkIsNearWall();
												if (float13 == 1.0F && square2 != null) {
													float13 = square2.getGridSneakModifier(true);
												}

												if (float13 > 1.0F) {
													float float14 = movingObject.DistTo(square.x, square.y);
													if (float14 > 1.0F) {
														float13 /= float14;
													}

													float1 /= float13;
												}
											}
										}

										float1 = (float)Math.floor((double)float1);
										boolean boolean2 = false;
										float1 = Math.min(float1, 400.0F);
										float1 /= 400.0F;
										float1 = Math.max(0.0F, float1);
										float1 = Math.min(1.0F, float1);
										float float15 = GameTime.instance.getMultiplier();
										float1 = (float)(1.0 - Math.pow((double)(1.0F - float1), (double)float15));
										float1 *= 100.0F;
										if ((float)Rand.Next(10000) / 100.0F < float1) {
											boolean2 = true;
										}

										if (!boolean2) {
											if (float1 > 20.0F && movingObject instanceof IsoPlayer && float6 < 15.0F) {
												((IsoPlayer)movingObject).bCouldBeSeenThisFrame = true;
											}

											if (!((IsoPlayer)movingObject).isbCouldBeSeenThisFrame() && !((IsoPlayer)movingObject).isbSeenThisFrame() && ((IsoPlayer)movingObject).isSneaking() && ((IsoPlayer)movingObject).JustMoved && Rand.Next((int)(1100.0F * GameTime.instance.getInvMultiplier())) == 0) {
												if (GameServer.bServer) {
													GameServer.addXp((IsoPlayer)movingObject, PerkFactory.Perks.Sneak, 1);
												} else {
													((IsoPlayer)movingObject).getXp().AddXP(PerkFactory.Perks.Sneak, 1.0F);
												}
											}

											if (!((IsoPlayer)movingObject).isbCouldBeSeenThisFrame() && !((IsoPlayer)movingObject).isbSeenThisFrame() && ((IsoPlayer)movingObject).isSneaking() && ((IsoPlayer)movingObject).JustMoved && Rand.Next((int)(1100.0F * GameTime.instance.getInvMultiplier())) == 0) {
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
												this.BonusSpotTime = 120.0F;
											}

											this.LastTargetSeenX = (int)movingObject.getX();
											this.LastTargetSeenY = (int)movingObject.getY();
											this.LastTargetSeenZ = (int)movingObject.getZ();
											if (this.stateMachine.getCurrent() != StaggerBackState.instance()) {
												if (this.target != movingObject) {
													this.targetSeenTime = 0.0F;
												}

												this.setTarget(movingObject);
												this.vectorToTarget.x = movingObject.getX();
												this.vectorToTarget.y = movingObject.getY();
												vector2 = this.vectorToTarget;
												vector2.x -= this.getX();
												vector2 = this.vectorToTarget;
												vector2.y -= this.getY();
												float13 = this.vectorToTarget.getLength();
												if (!boolean1) {
													this.TimeSinceSeenFlesh = 0.0F;
													this.targetSeenTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
												}

												if (this.target != this.spottedLast || this.getCurrentState() != LungeState.instance() || !(this.LungeTimer > 0.0F)) {
													if (this.target != this.spottedLast || this.getCurrentState() != AttackVehicleState.instance()) {
														if ((int)this.getZ() == (int)this.target.getZ() && (float13 <= 3.5F || this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null && float13 <= 4.0F) && this.getStateEventDelayTimer() <= 0.0F && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), movingObject.x, movingObject.y, (int)this.getZ(), movingObject)) {
															this.setTarget(movingObject);
															if (this.getCurrentState() == LungeState.instance()) {
																return;
															}
														}

														this.spottedLast = movingObject;
														if (!this.Ghost && !this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke)) {
															this.setTarget(movingObject);
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

															this.pathToCharacter(gameCharacter);
															if (Rand.Next(5) == 0) {
																this.spotSoundDelay = 200;
															}

															this.AllowRepathDelay = 480.0F;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					} else {
						this.setTarget((IsoMovingObject)null);
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
		}
	}

	public void MoveUnmodded(Vector2 vector2) {
		if (this.speedType == 1 && (this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(AttackState.instance()) || this.isCurrentState(AttackNetworkState.instance()) || this.isCurrentState(StaggerBackState.instance()) || this.isCurrentState(ZombieHitReactionState.instance())) && this.target instanceof IsoGameCharacter) {
			float float1 = this.target.nx - this.x;
			float float2 = this.target.ny - this.y;
			float float3 = (float)Math.sqrt((double)(float1 * float1 + float2 * float2));
			float3 -= this.getWidth() + this.target.getWidth() - 0.1F;
			float3 = Math.max(0.0F, float3);
			if (vector2.getLength() > float3) {
				vector2.setLength(float3);
			}
		}

		super.MoveUnmodded(vector2);
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
		if (!GameServer.bServer && !this.getFMODParameters().parameterList.contains(this.parameterCharacterMovementSpeed)) {
			this.getFMODParameters().add(this.parameterCharacterMovementSpeed);
			this.getFMODParameters().add(this.parameterFootstepMaterial);
			this.getFMODParameters().add(this.parameterFootstepMaterial2);
			this.getFMODParameters().add(this.parameterShoeType);
		}

		this.parameterCharacterMovementSpeed.setMovementType(movementType);
		this.DoFootstepSound(float1);
	}

	public void DoFootstepSound(float float1) {
		if (!GameServer.bServer) {
			if (!(float1 <= 0.0F)) {
				if (this.getCurrentSquare() != null) {
					if (!GameClient.bClient) {
						boolean boolean1 = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 15.0F);
						if (boolean1) {
							this.footstepVolume = float1;
							ZombieFootstepManager.instance.addCharacter(this);
						}
					} else {
						if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
							int int1 = (int)this.def.Frame;
							boolean boolean2;
							if (int1 >= 0 && int1 < 5) {
								boolean2 = this.stepFrameLast < 0 || this.stepFrameLast > 5;
							} else {
								boolean2 = this.stepFrameLast < 5;
							}

							if (boolean2) {
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
		if (this.networkAI != null && (GameClient.bClient || GameServer.bServer)) {
			this.networkAI.update();
		}

		super.preupdate();
	}

	public void postupdate() {
		IsoZombie.s_performance.postUpdate.invokeAndMeasure(this, IsoZombie::postUpdateInternal);
	}

	private void postUpdateInternal() {
		if (this.target instanceof IsoPlayer) {
			++((IsoPlayer)this.target).getStats().NumChasingZombies;
		}

		super.postupdate();
		if (this.current == null && !GameClient.bClient) {
			this.removeFromWorld();
			this.removeFromSquare();
		}

		if (!GameServer.bServer) {
			IsoPlayer player = this.getReanimatedPlayer();
			if (player != null) {
				player.setX(this.getX());
				player.setY(this.getY());
				player.setZ(this.getZ());
				player.setDir(this.getDir());
				player.setForwardDirection(this.getForwardDirection());
				AnimationPlayer animationPlayer = this.getAnimationPlayer();
				AnimationPlayer animationPlayer2 = player.getAnimationPlayer();
				if (animationPlayer != null && animationPlayer.isReady() && animationPlayer2 != null && animationPlayer2.isReady()) {
					animationPlayer2.setTargetAngle(animationPlayer.getAngle());
					animationPlayer2.setAngleToTarget();
				}

				player.setCurrent(this.getCell().getGridSquare((int)player.x, (int)player.y, (int)player.z));
				player.updateLightInfo();
				if (player.soundListener != null) {
					player.soundListener.setPos(player.getX(), player.getY(), player.getZ());
					player.soundListener.tick();
				}

				IsoPlayer player2 = IsoPlayer.getInstance();
				IsoPlayer.setInstance(player);
				player.updateLOS();
				IsoPlayer.setInstance(player2);
				if (GameClient.bClient && this.networkUpdate.Check()) {
					GameClient.instance.sendPlayer(player);
				}

				player.dirtyRecalcGridStackTime = 2.0F;
			}
		}

		if (this.targetSeenTime > 0.0F && !this.isTargetVisible()) {
			this.targetSeenTime = 0.0F;
		}
	}

	public boolean isSolidForSeparate() {
		if (!this.isCurrentState(FakeDeadZombieState.instance()) && !this.isCurrentState(ZombieFallDownState.instance()) && !this.isCurrentState(ZombieOnGroundState.instance()) && !this.isCurrentState(ZombieGetUpState.instance()) && (!this.isCurrentState(ZombieHitReactionState.instance()) || this.speedType == 1)) {
			return this.isSitAgainstWall() ? false : super.isSolidForSeparate();
		} else {
			return false;
		}
	}

	public boolean isPushableForSeparate() {
		if (!this.isCurrentState(ThumpState.instance()) && !this.isCurrentState(AttackState.instance()) && !this.isCurrentState(AttackVehicleState.instance()) && !this.isCurrentState(ZombieEatBodyState.instance()) && !this.isCurrentState(ZombieFaceTargetState.instance())) {
			return this.isSitAgainstWall() ? false : super.isPushableForSeparate();
		} else {
			return false;
		}
	}

	public boolean isPushedByForSeparate(IsoMovingObject movingObject) {
		if (movingObject instanceof IsoZombie && ((IsoZombie)movingObject).getCurrentState() == ZombieHitReactionState.instance() && !((IsoZombie)movingObject).collideWhileHit) {
			return false;
		} else {
			return this.getCurrentState() == ZombieHitReactionState.instance() && !this.collideWhileHit ? false : super.isPushedByForSeparate(movingObject);
		}
	}

	public void update() {
		IsoZombie.s_performance.update.invokeAndMeasure(this, IsoZombie::updateInternal);
	}

	private void updateInternal() {
		if (SandboxOptions.instance.Lore.ActiveOnly.getValue() > 1) {
			if ((SandboxOptions.instance.Lore.ActiveOnly.getValue() != 2 || GameTime.instance.getHour() < 20 && GameTime.instance.getHour() > 8) && (SandboxOptions.instance.Lore.ActiveOnly.getValue() != 3 || GameTime.instance.getHour() <= 8 || GameTime.instance.getHour() >= 20)) {
				this.makeInactive(true);
			} else {
				this.makeInactive(false);
			}
		}

		this.updateVocalProperties();
		if (this.bCrawling) {
			if (this.actionContext.getGroup() != ActionGroup.getActionGroup("zombie-crawler")) {
				this.advancedAnimator.OnAnimDataChanged(false);
				this.initializeStates();
				this.actionContext.setGroup(ActionGroup.getActionGroup("zombie-crawler"));
			}
		} else if (this.actionContext.getGroup() != ActionGroup.getActionGroup("zombie")) {
			this.advancedAnimator.OnAnimDataChanged(false);
			this.initializeStates();
			this.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
		}

		if (this.getThumpTimer() > 0) {
			--this.thumpTimer;
		}

		BaseVehicle baseVehicle = this.getNearVehicle();
		if (baseVehicle != null && this.target == null && baseVehicle.hasLightbar() && baseVehicle.lightbarSirenMode.get() > 0) {
			VehiclePart vehiclePart = baseVehicle.getUseablePart(this, false);
			if (vehiclePart != null && vehiclePart.getSquare().DistTo((IsoMovingObject)this) < 0.7F) {
				this.setThumpTarget(baseVehicle);
			}
		}

		this.doDeferredMovement();
		this.updateEmitter();
		if (this.spotSoundDelay > 0) {
			--this.spotSoundDelay;
		}

		if (GameClient.bClient) {
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

		if (GameClient.bClient && this.lastRemoteUpdate < 2000 && this.lastRemoteUpdate + 1000 / PerformanceSettings.getLockFPS() > 2000) {
			DebugLog.log(DebugType.Zombie, "lastRemoteUpdate 2000+ id=" + this.OnlineID);
		}

		this.lastRemoteUpdate = (short)(this.lastRemoteUpdate + 1000 / PerformanceSettings.getLockFPS());
		if (!GameClient.bClient || this.bRemote && this.lastRemoteUpdate <= 5000) {
			this.sprite = this.legsSprite;
			if (this.sprite != null) {
				this.updateCharacterTextureAnimTime();
				Vector2 vector2;
				if (this.bRemote && GameClient.bClient) {
					if (this.thumpFlag != 0) {
						if (SoundManager.instance.isListenerInRange(this.x, this.y, 20.0F)) {
							ZombieThumpManager.instance.addCharacter(this);
						} else {
							this.setThumpFlag(0);
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

					super.update();
					if (this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance() && this.getStateMachine().getCurrent() != ZombieHitReactionState.instance() && this.getStateMachine().getCurrent() != ZombieFallDownState.instance()) {
						this.setCollidable(true);
						if (this.Health > 0.0F && this.vehicle4testCollision != null && this.testCollideWithVehicles(this.vehicle4testCollision)) {
							this.vehicle4testCollision = null;
						} else if (this.Health > 0.0F && this.vehicle4testCollision != null && this.isCollidedWithVehicle()) {
							this.doHit(this.vehicle4testCollision);
							super.update();
						} else {
							this.vehicle4testCollision = null;
							this.ensureOnTile();
							if (this.getStateMachine().getCurrent() != StaggerBackState.instance() && this.getStateMachine().getCurrent() != BurntToDeath.instance() && this.getStateMachine().getCurrent() != FakeDeadZombieState.instance() && this.getStateMachine().getCurrent() != ZombieOnGroundState.instance() && this.getStateMachine().getCurrent() != ZombieGetUpState.instance()) {
								this.setShootable(true);
								this.setSolid(true);
								if (this.target != null) {
									this.vectorToTarget.x = this.target.getX();
									this.vectorToTarget.y = this.target.getY();
									vector2 = this.vectorToTarget;
									vector2.x -= this.getX();
									vector2 = this.vectorToTarget;
									vector2.y -= this.getY();
									this.updateZombieTripping();
								}

								this.separate();
							}
						}
					}
				} else if (GameServer.bServer && this.bIndoorZombie) {
					super.update();
					if (GameServer.bServer && GameServer.doSendZombies()) {
						GameServer.sendZombie(this);
					}
				} else {
					this.BonusSpotTime = PZMath.clamp(this.BonusSpotTime - GameTime.instance.getMultiplier(), 0.0F, Float.MAX_VALUE);
					this.TimeSinceSeenFlesh = PZMath.clamp(this.TimeSinceSeenFlesh + GameTime.instance.getMultiplier(), 0.0F, Float.MAX_VALUE);
					if (this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance() && this.getStateMachine().getCurrent() != ZombieHitReactionState.instance() && this.getStateMachine().getCurrent() != ZombieFallDownState.instance()) {
						this.setCollidable(true);
						LuaEventManager.triggerEvent("OnZombieUpdate", this);
						if (Core.bLastStand && this.getStateMachine().getCurrent() != ThumpState.instance() && this.getStateMachine().getCurrent() != AttackState.instance() && this.TimeSinceSeenFlesh > 120.0F && Rand.Next(36000) == 0) {
							IsoPlayer player = null;
							float float1 = 1000000.0F;
							for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
								if (IsoPlayer.players[int1] != null && IsoPlayer.players[int1].DistTo(this) < float1 && !IsoPlayer.players[int1].isDead()) {
									float1 = IsoPlayer.players[int1].DistTo(this);
									player = IsoPlayer.players[int1];
								}
							}

							if (player != null) {
								this.AllowRepathDelay = -1.0F;
								this.pathToCharacter(player);
							}
						} else {
							if (!GameServer.bServer && !GameClient.bClient) {
								if (this.Health > 0.0F && this.vehicle4testCollision != null && this.testCollideWithVehicles(this.vehicle4testCollision)) {
									this.vehicle4testCollision = null;
									return;
								}

								if (this.Health > 0.0F && this.vehicle4testCollision != null && this.isCollidedWithVehicle()) {
									this.doHit(this.vehicle4testCollision);
									super.update();
									return;
								}
							}

							this.vehicle4testCollision = null;
							if (this.BonusSpotTime > 0.0F && this.spottedLast != null && !((IsoGameCharacter)this.spottedLast).isDead()) {
								this.spotted(this.spottedLast, true);
							}

							if (GameServer.bServer && this.getStateMachine().getCurrent() == BurntToDeath.instance()) {
								DebugLog.log(DebugType.Zombie, "Zombie is burning " + this.OnlineID);
							}

							super.update();
							if (VirtualZombieManager.instance.isReused(this)) {
								DebugLog.log(DebugType.Zombie, "Zombie added to ReusableZombies after super.update - RETURNING " + this);
							} else {
								if (GameServer.bServer && (GameServer.doSendZombies() || this.getStateMachine().getCurrent() == StaggerBackState.instance() || this.getStateMachine().getCurrent() == BurntToDeath.instance())) {
									GameServer.sendZombie(this);
								}

								if (this.getStateMachine().getCurrent() != ClimbThroughWindowState.instance() && this.getStateMachine().getCurrent() != ClimbOverFenceState.instance() && this.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance()) {
									this.ensureOnTile();
									State state = this.stateMachine.getCurrent();
									if (state != StaggerBackState.instance() && state != BurntToDeath.instance() && state != FakeDeadZombieState.instance() && state != ZombieFallDownState.instance() && state != ZombieOnGroundState.instance() && state != ZombieHitReactionState.instance() && state != ZombieGetUpState.instance()) {
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
												if (this.walkVariantUse == null || state != LungeState.instance() && state != LungeNetworkState.instance()) {
													this.walkVariantUse = this.walkVariant;
												}

												if (this.bCrawling) {
													this.walkVariantUse = "ZombieCrawl";
												}

												if (state != ZombieIdleState.instance() && state != StaggerBackState.instance() && state != ThumpState.instance() && state != FakeDeadZombieState.instance()) {
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
										this.AllowRepathDelay = PZMath.clamp(this.AllowRepathDelay - GameTime.instance.getMultiplier(), 0.0F, Float.MAX_VALUE);
										int int2 = this.getSandboxMemoryDuration();
										if (this.TimeSinceSeenFlesh > (float)int2 && this.target != null) {
											this.setTarget((IsoMovingObject)null);
										}

										if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
											this.setTarget((IsoMovingObject)null);
										}

										if (this.target != null) {
											this.vectorToTarget.x = this.target.getX();
											this.vectorToTarget.y = this.target.getY();
											vector2 = this.vectorToTarget;
											vector2.x -= this.getX();
											vector2 = this.vectorToTarget;
											vector2.y -= this.getY();
											this.updateZombieTripping();
										}

										if (IsoPlayer.getInstance() != null) {
											this.nextIdleSound -= GameTime.getInstance().getMultiplier() / 1.6F;
											if (this.nextIdleSound < 0.0F && (this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState.instance())) {
												this.nextIdleSound = (float)Rand.Next(300, 600);
												if (SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0F)) {
													String string2 = this.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
													if (!this.emitter.isPlaying(string2)) {
														ZombieVocalsManager.instance.addCharacter(this);
													}
												}
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
										}

										this.timeSinceRespondToSound += GameTime.getInstance().getMultiplier() / 1.6F;
										this.separate();
										this.updateSearchForCorpse();
										if (this.TimeSinceSeenFlesh > 2000.0F && this.timeSinceRespondToSound > 2000.0F) {
											ZombieGroupManager.instance.update(this);
										}
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
		} else {
			DebugLog.log(DebugType.Zombie, "removing stale zombie 5000 id=" + this.OnlineID);
			VirtualZombieManager.instance.removeZombieFromWorld(this);
		}
	}

	void doHit(BaseVehicle baseVehicle) {
		if (GameServer.bServer || GameClient.bClient && !baseVehicle.isDriver(IsoPlayer.getInstance())) {
			if (this.networkAI.hitVehicle != null && baseVehicle.getId() == this.networkAI.hitVehicle.vehicleID) {
				this.networkAI.hitVehicle.process();
				this.networkAI.hitVehicle = null;
			}
		} else {
			baseVehicle.hitCharacter(this);
		}
	}

	protected void calculateStats() {
	}

	private void updateZombieTripping() {
		if (this.speedType == 1 && StringUtils.isNullOrEmpty(this.getBumpType()) && this.target != null && Rand.NextBool(Rand.AdjustForFramerate(750))) {
			this.setBumpType("trippingFromSprint");
		}
	}

	private void updateVocalProperties() {
		if (!GameServer.bServer) {
			boolean boolean1 = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0F);
			if (this.vocalEvent == 0L && !this.isDead() && !this.isFakeDead() && boolean1) {
				String string = this.isFemale() ? "FemaleZombieCombined" : "MaleZombieCombined";
				if (!this.getFMODParameters().parameterList.contains(this.parameterZombieState)) {
					this.parameterZombieState.update();
					this.getFMODParameters().add(this.parameterZombieState);
					this.parameterCharacterInside.update();
					this.getFMODParameters().add(this.parameterCharacterInside);
					this.parameterPlayerDistance.update();
					this.getFMODParameters().add(this.parameterPlayerDistance);
				}

				this.vocalEvent = this.getEmitter().playVocals(string);
			}

			if (this.vocalEvent != 0L && !this.isDead() && this.isFakeDead() && this.getEmitter().isPlaying(this.vocalEvent)) {
				this.getEmitter().stopSound(this.vocalEvent);
				this.vocalEvent = 0L;
			}
		}
	}

	public void setVehicleHitLocation(BaseVehicle baseVehicle) {
		if (!this.getFMODParameters().parameterList.contains(this.parameterVehicleHitLocation)) {
			this.getFMODParameters().add(this.parameterVehicleHitLocation);
		}

		ParameterVehicleHitLocation.HitLocation hitLocation = ParameterVehicleHitLocation.calculateLocation(baseVehicle, this.getX(), this.getY(), this.getZ());
		this.parameterVehicleHitLocation.setLocation(hitLocation);
	}

	private void updateSearchForCorpse() {
		if (!this.bCrawling && this.target == null && this.eatBodyTarget == null) {
			if (this.bodyToEat != null) {
				if (this.bodyToEat.getStaticMovingObjectIndex() == -1) {
					this.bodyToEat = null;
				} else if (!this.isEatingOther(this.bodyToEat) && this.bodyToEat.getEatingZombies().size() >= 3) {
					this.bodyToEat = null;
				}
			}

			if (this.bodyToEat == null) {
				this.checkForCorpseTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
				if (this.checkForCorpseTimer <= 0.0F) {
					this.checkForCorpseTimer = 10000.0F;
					tempBodies.clear();
					for (int int1 = -10; int1 < 10; ++int1) {
						for (int int2 = -10; int2 < 10; ++int2) {
							IsoGridSquare square = this.getCell().getGridSquare((double)(this.getX() + (float)int1), (double)(this.getY() + (float)int2), (double)this.getZ());
							if (square != null) {
								IsoDeadBody deadBody = square.getDeadBody();
								if (deadBody != null && !deadBody.isSkeleton() && !deadBody.isZombie() && deadBody.getEatingZombies().size() < 3 && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), deadBody.x, deadBody.y, (int)this.getZ(), (IsoMovingObject)null, false, true)) {
									tempBodies.add(deadBody);
								}
							}
						}
					}

					if (!tempBodies.isEmpty()) {
						this.bodyToEat = (IsoDeadBody)PZArrayUtil.pickRandom((List)tempBodies);
						tempBodies.clear();
					}
				}
			}

			if (this.bodyToEat != null && this.isCurrentState(ZombieIdleState.instance())) {
				if (this.DistToSquared(this.bodyToEat) > 1.0F) {
					Vector2 vector2 = tempo.set(this.x - this.bodyToEat.x, this.y - this.bodyToEat.y);
					vector2.setLength(0.5F);
					this.pathToLocationF(this.bodyToEat.getX() + vector2.x, this.bodyToEat.getY() + vector2.y, this.bodyToEat.getZ());
				}
			}
		} else {
			this.checkForCorpseTimer = 10000.0F;
			this.bodyToEat = null;
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

	public void getZombieLungeSpeed() {
		this.bRunning = false;
		if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
			this.bRunning = true;
		}
	}

	public boolean tryThump(IsoGridSquare square) {
		if (this.Ghost) {
			return false;
		} else if (this.bCrawling) {
			return false;
		} else {
			boolean boolean1 = this.isCurrentState(PathFindState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance());
			if (!boolean1) {
				return false;
			} else {
				IsoGridSquare square2;
				if (square != null) {
					square2 = square;
				} else {
					square2 = this.getFeelerTile(this.getFeelersize());
				}

				if (square2 != null && this.current != null) {
					IsoObject object = this.current.testCollideSpecialObjects(square2);
					IsoDoor door = (IsoDoor)Type.tryCastTo(object, IsoDoor.class);
					IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
					IsoWindow window = (IsoWindow)Type.tryCastTo(object, IsoWindow.class);
					if (window != null && window.canClimbThrough(this)) {
						if (!this.isFacingObject(window, 0.8F)) {
							return false;
						} else {
							this.climbThroughWindow(window);
							return true;
						}
					} else if (thumpable != null && thumpable.canClimbThrough(this)) {
						this.climbThroughWindow(thumpable);
						return true;
					} else if (thumpable != null && thumpable.getThumpableFor(this) != null || window != null && window.getThumpableFor(this) != null || door != null && door.getThumpableFor(this) != null) {
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
							boolean boolean2 = this.getPathFindBehavior2().isGoalSound() && (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance()));
							if (SandboxOptions.instance.Lore.ThumpNoChasing.getValue() || this.target != null || boolean2) {
								if (window != null && window.getThumpableFor(this) != null) {
									object = (IsoObject)window.getThumpableFor(this);
								}

								this.setThumpTarget(object);
								this.setPath2((PolygonalMap2.Path)null);
							}
						}

						return true;
					} else if (object != null && IsoWindowFrame.isWindowFrame(object)) {
						this.climbThroughWindowFrame(object);
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
	}

	public void Wander() {
		GameServer.sendZombie(this);
		this.changeState(ZombieIdleState.instance());
	}

	public void DoZombieInventory() {
		if (!this.isReanimatedPlayer() && !this.wasFakeDead()) {
			if (GameServer.bServer && !this.m_bPersistentOutfitInit) {
				this.dressInPersistentOutfitID(this.m_persistentOutfitId);
			}

			this.getInventory().removeAllItems();
			this.getInventory().setSourceGrid(this.getCurrentSquare());
			this.wornItems.setFromItemVisuals(this.itemVisuals);
			this.wornItems.addItemsToItemContainer(this.getInventory());
			InventoryItem inventoryItem;
			for (int int1 = 0; int1 < this.attachedItems.size(); ++int1) {
				AttachedItem attachedItem = this.attachedItems.get(int1);
				inventoryItem = attachedItem.getItem();
				if (!this.getInventory().contains(inventoryItem)) {
					inventoryItem.setContainer(this.getInventory());
					this.getInventory().getItems().add(inventoryItem);
				}
			}

			if (!GameClient.bClient) {
				IsoBuilding building = this.getCurrentBuilding();
				if (building != null && building.getDef() != null && building.getDef().getKeyId() != -1 && Rand.Next(4) == 0) {
					int int2 = Rand.Next(5);
					String string = "Base.Key" + (int2 + 1);
					inventoryItem = this.inventory.AddItem(string);
					inventoryItem.setKeyId(building.getDef().getKeyId());
				}

				if (this.itemsToSpawnAtDeath != null && !this.itemsToSpawnAtDeath.isEmpty()) {
					for (int int3 = 0; int3 < this.itemsToSpawnAtDeath.size(); ++int3) {
						this.inventory.AddItem((InventoryItem)this.itemsToSpawnAtDeath.get(int3));
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
		} else {
			this.bLunger = true;
			this.speedMod = 0.85F;
			this.walkVariant = this.walkVariant + "2";
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			this.def.setFrameSpeedPerFrame(0.24F);
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= this.speedMod;
		}
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

		if (this.speedType == -1) {
			this.speedType = SandboxOptions.instance.Lore.Speed.getValue();
			if (this.speedType == 4) {
				this.speedType = Rand.Next(2);
			}

			if (this.inactive) {
				this.speedType = 3;
			}
		}

		IsoSpriteInstance spriteInstance;
		if (this.bCrawling) {
			this.speedMod = 0.3F;
			this.speedMod += (float)Rand.Next(1500) / 10000.0F;
			spriteInstance = this.def;
			spriteInstance.AnimFrameIncrease *= 0.8F;
		} else if (SandboxOptions.instance.Lore.Speed.getValue() != 3 && this.speedType != 3 && Rand.Next(3) == 0) {
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

		this.walkType = Integer.toString(Rand.Next(5) + 1);
		if (this.speedType == 1) {
			this.setTurnDelta(1.0F);
			this.walkType = "sprint" + this.walkType;
		}

		if (this.speedType == 3) {
			this.walkType = Integer.toString(Rand.Next(3) + 1);
			this.walkType = "slow" + this.walkType;
		}

		this.initCanCrawlUnderVehicle();
	}

	public void setWalkType(String string) {
		this.walkType = string;
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
	}

	public boolean isFakeDead() {
		return this.bFakeDead;
	}

	public void setFakeDead(boolean boolean1) {
		if (boolean1 && Rand.Next(2) == 0) {
			this.setCrawlerType(2);
		}

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
		this.setTarget(zombie);
		if (this.Health <= 0.0F && !this.isOnDeathDone()) {
			this.DoZombieInventory();
			this.setOnDeathDone(true);
		}

		this.TimeSinceSeenFlesh = 0.0F;
	}

	protected void DoDeathSilence(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (this.Health <= 0.0F && !this.isOnDeathDone()) {
			this.DoZombieInventory();
			this.setOnDeathDone(true);
		}

		super.DoDeathSilence(handWeapon, gameCharacter);
	}

	public void Hit(BaseVehicle baseVehicle, float float1, float float2, Vector2 vector2) {
		this.AttackedBy = baseVehicle.getDriver();
		this.setHitDir(vector2);
		this.setHitForce(float1 * 0.15F);
		int int1 = (int)(float1 * 6.0F);
		this.setTarget(baseVehicle.getCharacter(0));
		if (!this.bStaggerBack && !this.isOnFloor() && this.getCurrentState() != ZombieGetUpState.instance() && this.getCurrentState() != ZombieOnGroundState.instance()) {
			boolean boolean1 = this.bStaggerBack;
			boolean boolean2 = this.bKnockedDown;
			boolean boolean3 = this.isBecomeCrawler();
			if (float2 > 0.0F) {
				this.setHitFromBehind(true);
				if (Rand.Next(100) <= int1) {
					if (Rand.Next(5) == 0) {
						boolean3 = true;
					}

					boolean1 = true;
					boolean2 = true;
				} else {
					boolean1 = true;
				}
			} else if (float1 < 3.0F) {
				if (Rand.Next(100) <= int1) {
					if (Rand.Next(8) == 0) {
						boolean3 = true;
					}

					boolean1 = true;
					boolean2 = true;
				} else {
					boolean1 = true;
				}
			} else if (float1 < 10.0F) {
				if (Rand.Next(8) == 0) {
					boolean3 = true;
				}

				boolean1 = true;
				boolean2 = true;
			} else if (!GameServer.bServer && (!GameClient.bClient || baseVehicle.isDriver(IsoPlayer.getInstance()))) {
				this.Health = -1.0F;
				this.checkDead(baseVehicle.getDriver(), true);
			}

			if (!GameServer.bServer) {
				this.bStaggerBack = boolean1;
				this.bKnockedDown = boolean2;
				this.bBecomeCrawler = boolean3;
			}
		} else {
			if (this.isFakeDead()) {
				this.setFakeDead(false);
			}

			this.setHitReaction("Floor");
			if (!GameServer.bServer && (!GameClient.bClient || baseVehicle.isDriver(IsoPlayer.getInstance()))) {
				this.Health -= float1 / 5.0F;
				this.checkDead(baseVehicle.getDriver(), true);
			}
		}

		if ((GameServer.bServer || GameClient.bClient) && this.isDead() && this.AttackedBy != null) {
			this.lastPlayerHit = this.AttackedBy.getOnlineID() | DeadBodyPacket.DIED_UNDER_VEHICLE;
		}

		this.addBlood(float1);
	}

	public void addBlood(float float1) {
		if (!((float)Rand.Next(10) > float1)) {
			float float2 = 0.6F;
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
				this.splatBloodFloorBig(0.3F);
			}

			if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
				this.playBloodSplatterSound();
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
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
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 1.8F, this.getHitDir().y * 1.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 8 : byte1) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 1.9F, this.getHitDir().y * 1.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
					}

					if (Rand.Next(this.isCloseKilled() ? 4 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
					}

					if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + float2, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
					}
				}
			}
		}
	}

	private void processHitDirection(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		String string = gameCharacter.getVariableString("ZombieHitReaction");
		if ("Shot".equals(string)) {
			string = "ShotBelly";
			gameCharacter.isCrit = Rand.Next(100) < ((IsoPlayer)gameCharacter).calculateCritChance(this);
			Vector2 vector2 = gameCharacter.getForwardDirection();
			Vector2 vector22 = this.getHitAngle();
			double double1 = (double)(vector2.x * vector22.y - vector2.y * vector22.x);
			double double2 = double1 >= 0.0 ? 1.0 : -1.0;
			double double3 = (double)(vector2.x * vector22.x + vector2.y * vector22.y);
			double double4 = Math.acos(double3) * double2;
			if (double4 < 0.0) {
				double4 += 6.283185307179586;
			}

			String string2 = "";
			int int1;
			if (Math.toDegrees(double4) < 45.0) {
				this.setHitFromBehind(true);
				string2 = "S";
				int1 = Rand.Next(9);
				if (int1 > 6) {
					string2 = "L";
				}

				if (int1 > 4) {
					string2 = "R";
				}
			}

			if (Math.toDegrees(double4) > 45.0 && Math.toDegrees(double4) < 90.0) {
				this.setHitFromBehind(true);
				if (Rand.Next(4) == 0) {
					string2 = "S";
				} else {
					string2 = "R";
				}
			}

			if (Math.toDegrees(double4) > 90.0 && Math.toDegrees(double4) < 135.0) {
				string2 = "R";
			}

			if (Math.toDegrees(double4) > 135.0 && Math.toDegrees(double4) < 180.0) {
				if (Rand.Next(4) == 0) {
					string2 = "N";
				} else {
					string2 = "R";
				}
			}

			if (Math.toDegrees(double4) > 180.0 && Math.toDegrees(double4) < 225.0) {
				string2 = "N";
				int1 = Rand.Next(9);
				if (int1 > 6) {
					string2 = "L";
				}

				if (int1 > 4) {
					string2 = "R";
				}
			}

			if (Math.toDegrees(double4) > 225.0 && Math.toDegrees(double4) < 270.0) {
				if (Rand.Next(4) == 0) {
					string2 = "N";
				} else {
					string2 = "L";
				}
			}

			if (Math.toDegrees(double4) > 270.0 && Math.toDegrees(double4) < 315.0) {
				this.setHitFromBehind(true);
				string2 = "L";
			}

			if (Math.toDegrees(double4) > 315.0) {
				if (Rand.Next(4) == 0) {
					string2 = "S";
				} else {
					string2 = "L";
				}
			}

			if ("N".equals(string2)) {
				if (this.isHitFromBehind()) {
					string = "ShotBellyStep";
				} else {
					int1 = Rand.Next(2);
					switch (int1) {
					case 0: 
						string = "ShotBelly";
						break;
					
					case 1: 
						string = "ShotBellyStep";
					
					}
				}
			}

			if ("S".equals(string2)) {
				string = "ShotBellyStep";
			}

			if ("L".equals(string2) || "R".equals(string2)) {
				if (this.isHitFromBehind()) {
					int1 = Rand.Next(3);
					switch (int1) {
					case 0: 
						string = "ShotChest";
						break;
					
					case 1: 
						string = "ShotLeg";
						break;
					
					case 2: 
						string = "ShotShoulderStep";
					
					}
				} else {
					int1 = Rand.Next(5);
					switch (int1) {
					case 0: 
						string = "ShotChest";
						break;
					
					case 1: 
						string = "ShotChestStep";
						break;
					
					case 2: 
						string = "ShotLeg";
						break;
					
					case 3: 
						string = "ShotShoulder";
						break;
					
					case 4: 
						string = "ShotShoulderStep";
					
					}
				}

				string = string + string2;
			}

			if (gameCharacter.isCrit) {
				if ("S".equals(string2)) {
					string = "ShotHeadFwd";
				}

				if ("N".equals(string2)) {
					string = "ShotHeadBwd";
				}

				if (("L".equals(string2) || "R".equals(string2)) && Rand.Next(4) == 0) {
					string = "ShotHeadBwd";
				}
			}

			if (string.contains("Head")) {
				this.addBlood(BloodBodyPartType.Head, false, true, true);
			} else if (string.contains("Chest")) {
				this.addBlood(BloodBodyPartType.Torso_Upper, !this.isCrit, this.isCrit, true);
			} else if (string.contains("Belly")) {
				this.addBlood(BloodBodyPartType.Torso_Lower, !this.isCrit, this.isCrit, true);
			} else {
				boolean boolean1;
				if (string.contains("Leg")) {
					boolean1 = Rand.Next(2) == 0;
					if ("L".equals(string2)) {
						this.addBlood(boolean1 ? BloodBodyPartType.LowerLeg_L : BloodBodyPartType.UpperLeg_L, !this.isCrit, this.isCrit, true);
					} else {
						this.addBlood(boolean1 ? BloodBodyPartType.LowerLeg_R : BloodBodyPartType.UpperLeg_R, !this.isCrit, this.isCrit, true);
					}
				} else if (string.contains("Shoulder")) {
					boolean1 = Rand.Next(2) == 0;
					if ("L".equals(string2)) {
						this.addBlood(boolean1 ? BloodBodyPartType.ForeArm_L : BloodBodyPartType.UpperArm_L, !this.isCrit, this.isCrit, true);
					} else {
						this.addBlood(boolean1 ? BloodBodyPartType.ForeArm_R : BloodBodyPartType.UpperArm_R, !this.isCrit, this.isCrit, true);
					}
				}
			}
		} else if (handWeapon.getCategories().contains("Blunt")) {
			this.addBlood(BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperArm_L.index(), BloodBodyPartType.Groin.index())), false, false, true);
		} else if (!handWeapon.getCategories().contains("Unarmed")) {
			this.addBlood(BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperArm_L.index(), BloodBodyPartType.Groin.index())), false, true, true);
		}

		if ("ShotHeadFwd".equals(string) && Rand.Next(2) == 0) {
			string = "ShotHeadFwd02";
		}

		if (this.getEatBodyTarget() != null) {
			if (this.getVariableBoolean("onknees")) {
				string = "OnKnees";
			} else {
				string = "Eating";
			}
		}

		if (string != null && !"".equals(string)) {
			this.setHitReaction(string);
		} else {
			this.bStaggerBack = true;
			this.setHitReaction("");
			if ("LEFT".equals(this.getPlayerAttackPosition()) || "RIGHT".equals(this.getPlayerAttackPosition())) {
				gameCharacter.isCrit = false;
			}
		}
	}

	public void hitConsequences(HandWeapon handWeapon, IsoGameCharacter gameCharacter, boolean boolean1, float float1, boolean boolean2) {
		if (!this.isOnlyJawStab() || this.isCloseKilled()) {
			if (this.shouldBecomeFakeDead(gameCharacter)) {
				this.setFakeDead(true);
				this.Health = 0.0F;
			}

			super.hitConsequences(handWeapon, gameCharacter, boolean1, float1, boolean2);
			if (DebugLog.isEnabled(DebugType.Combat)) {
				DebugLog.Combat.debugln(this + " got hit for " + float1);
			}

			this.actionContext.reportEvent("wasHit");
			if (!boolean2) {
				this.processHitDirection(handWeapon, gameCharacter);
			}

			this.setTarget(gameCharacter);
			if (!GameServer.bServer && !GameClient.bClient || GameClient.bClient && gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				this.bKnockedDown = gameCharacter.isCrit || this.isOnFloor() || this.isAlwaysKnockedDown();
			}

			this.checkClimbOverFenceHit();
			this.checkClimbThroughWindowHit();
			if (this.shouldBecomeCrawler(gameCharacter)) {
				this.setBecomeCrawler(true);
			}
		}
	}

	private void checkClimbOverFenceHit() {
		if (!this.isOnFloor()) {
			if (this.isCurrentState(ClimbOverFenceState.instance()) && this.getVariableBoolean("ClimbFenceStarted") && !this.isVariable("ClimbFenceOutcome", "fall") && !this.getVariableBoolean("ClimbFenceFlopped")) {
				HashMap hashMap = (HashMap)this.StateMachineParams.get(ClimbOverFenceState.instance());
				byte byte1 = 3;
				byte byte2 = 4;
				int int1 = (Integer)hashMap.get(Integer.valueOf(byte1));
				int int2 = (Integer)hashMap.get(Integer.valueOf(byte2));
				this.climbFenceWindowHit(int1, int2);
			}
		}
	}

	private void checkClimbThroughWindowHit() {
		if (!this.isOnFloor()) {
			if (this.isCurrentState(ClimbThroughWindowState.instance()) && this.getVariableBoolean("ClimbWindowStarted") && !this.isVariable("ClimbWindowOutcome", "fall") && !this.getVariableBoolean("ClimbWindowFlopped")) {
				HashMap hashMap = (HashMap)this.StateMachineParams.get(ClimbThroughWindowState.instance());
				byte byte1 = 12;
				byte byte2 = 13;
				int int1 = (Integer)hashMap.get(Integer.valueOf(byte1));
				int int2 = (Integer)hashMap.get(Integer.valueOf(byte2));
				this.climbFenceWindowHit(int1, int2);
			}
		}
	}

	private void climbFenceWindowHit(int int1, int int2) {
		if (this.getDir() == IsoDirections.W) {
			this.setX((float)int1 + 0.9F);
			this.setLx(this.getX());
		} else if (this.getDir() == IsoDirections.E) {
			this.setX((float)int1 + 0.1F);
			this.setLx(this.getX());
		} else if (this.getDir() == IsoDirections.N) {
			this.setY((float)int2 + 0.9F);
			this.setLy(this.getY());
		} else if (this.getDir() == IsoDirections.S) {
			this.setY((float)int2 + 0.1F);
			this.setLy(this.getY());
		}

		this.bStaggerBack = false;
		this.bKnockedDown = true;
		this.setOnFloor(true);
		this.setFallOnFront(true);
		this.setHitReaction("FenceWindow");
	}

	private boolean shouldBecomeFakeDead(IsoGameCharacter gameCharacter) {
		return false;
	}

	private boolean shouldBecomeCrawler(IsoGameCharacter gameCharacter) {
		if (this.isBecomeCrawler()) {
			return true;
		} else if (this.isCrawling()) {
			return false;
		} else if (Core.bLastStand) {
			return false;
		} else if (this.isDead()) {
			return false;
		} else if (this.isCloseKilled()) {
			return false;
		} else {
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
			if (player != null && !player.isAimAtFloor() && player.bDoShove) {
				return false;
			} else {
				byte byte1 = 30;
				if (player != null && player.isAimAtFloor() && player.bDoShove) {
					if (this.isHitLegsWhileOnFloor()) {
						byte1 = 7;
					} else {
						byte1 = 15;
					}
				}

				return Rand.NextBool(byte1);
			}
		}
	}

	public void removeFromWorld() {
		this.getEmitter().stopSoundByName("BurningFlesh");
		VirtualZombieManager.instance.RemoveZombie(this);
		this.setPath2((PolygonalMap2.Path)null);
		PolygonalMap2.instance.cancelRequest(this);
		if (this.getFinder().progress != AStarPathFinder.PathFindProgress.notrunning && this.getFinder().progress != AStarPathFinder.PathFindProgress.found) {
			this.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
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

	public void resetForReuse() {
		this.bCrawling = false;
		this.initializeStates();
		this.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
		this.advancedAnimator.OnAnimDataChanged(false);
		this.setStateMachineLocked(false);
		this.setDefaultState();
		if (this.vocalEvent != 0L) {
			this.getEmitter().stopSound(this.vocalEvent);
			this.vocalEvent = 0L;
		}

		this.parameterZombieState.setState(ParameterZombieState.State.Idle);
		this.setSceneCulled(true);
		this.releaseAnimationPlayer();
		Arrays.fill(this.IsVisibleToPlayer, false);
		this.setCurrent((IsoGridSquare)null);
		this.setLast((IsoGridSquare)null);
		this.setOnFloor(false);
		this.setCanWalk(true);
		this.setFallOnFront(false);
		this.setHitTime(0);
		this.strength = -1;
		this.setImmortalTutorialZombie(false);
		this.setOnlyJawStab(false);
		this.setAlwaysKnockedDown(false);
		this.setForceEatingAnimation(false);
		this.setNoTeeth(false);
		this.cognition = -1;
		this.speedType = -1;
		this.bodyToEat = null;
		this.checkForCorpseTimer = 10000.0F;
		this.clearAttachedItems();
		this.target = null;
		this.setEatBodyTarget((IsoMovingObject)null, false);
		this.setSkeleton(false);
		this.setReanimatedPlayer(false);
		this.setBecomeCrawler(false);
		this.setWasFakeDead(false);
		this.setReanimate(false);
		this.DoZombieStats();
		this.alerted = false;
		this.BonusSpotTime = 0.0F;
		this.TimeSinceSeenFlesh = 100000.0F;
		this.soundReactDelay = 0.0F;
		this.delayedSound.x = this.delayedSound.y = this.delayedSound.z = -1;
		this.bSoundSourceRepeating = false;
		this.soundSourceTarget = null;
		this.soundAttract = 0.0F;
		this.soundAttractTimeout = 0.0F;
		if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
			this.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
			this.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
			this.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
			this.setHealth(Rand.Next(0.5F, 3.5F) + Rand.Next(0.0F, 0.3F));
		}

		this.setCollidable(true);
		this.setShootable(true);
		if (this.isOnFire()) {
			IsoFireManager.RemoveBurningCharacter(this);
			this.setOnFire(false);
		}

		if (this.AttachedAnimSprite != null) {
			this.AttachedAnimSprite.clear();
		}

		this.OnlineID = -1;
		this.bIndoorZombie = false;
		this.setVehicle4TestCollision((BaseVehicle)null);
		this.clearItemsToSpawnAtDeath();
		this.m_persistentOutfitId = 0;
		this.m_bPersistentOutfitInit = false;
		this.sharedDesc = null;
	}

	public boolean wasFakeDead() {
		return this.bWasFakeDead;
	}

	public void setWasFakeDead(boolean boolean1) {
		this.bWasFakeDead = boolean1;
	}

	public boolean isBecomeCrawler() {
		return this.bBecomeCrawler;
	}

	public void setBecomeCrawler(boolean boolean1) {
		this.bBecomeCrawler = boolean1;
	}

	public boolean isReanimate() {
		return this.bReanimate;
	}

	public void setReanimate(boolean boolean1) {
		this.bReanimate = boolean1;
	}

	public boolean isReanimatedPlayer() {
		return this.bReanimatedPlayer;
	}

	public void setReanimatedPlayer(boolean boolean1) {
		this.bReanimatedPlayer = boolean1;
	}

	public IsoPlayer getReanimatedPlayer() {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && player.ReanimatedCorpse == this) {
				return player;
			}
		}

		return null;
	}

	public void setFemaleEtc(boolean boolean1) {
		this.setFemale(boolean1);
		if (this.getDescriptor() != null) {
			this.getDescriptor().setFemale(boolean1);
		}

		this.SpriteName = boolean1 ? "KateZ" : "BobZ";
		this.hurtSound = boolean1 ? "FemaleZombieHurt" : "MaleZombieHurt";
	}

	public void addRandomBloodDirtHolesEtc() {
		this.addBlood((BloodBodyPartType)null, false, true, false);
		this.addDirt((BloodBodyPartType)null, OutfitRNG.Next(5, 10), false);
		this.addRandomVisualDamages();
		this.addRandomVisualBandages();
		int int1 = Math.max(8 - (int)IsoWorld.instance.getWorldAgeDays() / 30, 0);
		int int2;
		for (int2 = 0; int2 < 5; ++int2) {
			if (OutfitRNG.NextBool(int1)) {
				this.addBlood((BloodBodyPartType)null, false, true, false);
				this.addDirt((BloodBodyPartType)null, (Integer)null, false);
			}
		}

		for (int2 = 0; int2 < 8; ++int2) {
			if (OutfitRNG.NextBool(int1)) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
				this.addHole(bloodBodyPartType);
				this.addBlood(bloodBodyPartType, true, false, false);
			}
		}
	}

	public void useDescriptor(SharedDescriptors.Descriptor descriptor) {
		this.getHumanVisual().clear();
		this.itemVisuals.clear();
		this.m_persistentOutfitId = descriptor == null ? 0 : descriptor.getPersistentOutfitID();
		this.m_bPersistentOutfitInit = true;
		this.sharedDesc = descriptor;
		if (descriptor != null) {
			this.setFemaleEtc(descriptor.isFemale());
			this.getHumanVisual().copyFrom(descriptor.getHumanVisual());
			this.getWornItems().setFromItemVisuals(descriptor.itemVisuals);
			this.onWornItemsChanged();
		}
	}

	public SharedDescriptors.Descriptor getSharedDescriptor() {
		return this.sharedDesc;
	}

	public int getSharedDescriptorID() {
		return this.getPersistentOutfitID();
	}

	public int getScreenProperX(int int1) {
		return (int)(IsoUtils.XToScreen(this.x, this.y, this.z, 0) - IsoCamera.cameras[int1].getOffX());
	}

	public int getScreenProperY(int int1) {
		return (int)(IsoUtils.YToScreen(this.x, this.y, this.z, 0) - IsoCamera.cameras[int1].getOffY());
	}

	public BaseVisual getVisual() {
		return this.humanVisual;
	}

	public HumanVisual getHumanVisual() {
		return this.humanVisual;
	}

	public ItemVisuals getItemVisuals() {
		this.getItemVisuals(this.itemVisuals);
		return this.itemVisuals;
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		if (this.isUsingWornItems()) {
			this.getWornItems().getItemVisuals(itemVisuals);
		} else if (itemVisuals != this.itemVisuals) {
			itemVisuals.clear();
			itemVisuals.addAll(this.itemVisuals);
		}
	}

	public boolean isUsingWornItems() {
		return this.isOnDeathDone() || this.isReanimatedPlayer() || this.wasFakeDead();
	}

	public void setAsSurvivor() {
		String string = "Survivalist";
		switch (Rand.Next(3)) {
		case 1: 
			string = "Survivalist02";
			break;
		
		case 2: 
			string = "Survivalist03";
		
		}
		this.dressInPersistentOutfit(string);
	}

	public void dressInRandomOutfit() {
		ZombiesZoneDefinition.dressInRandomOutfit(this);
	}

	public void dressInNamedOutfit(String string) {
		this.wornItems.clear();
		this.getHumanVisual().clear();
		this.itemVisuals.clear();
		Outfit outfit = this.isFemale() ? OutfitManager.instance.FindFemaleOutfit(string) : OutfitManager.instance.FindMaleOutfit(string);
		if (outfit != null) {
			if (outfit.isEmpty()) {
				outfit.loadItems();
				this.pendingOutfitName = string;
			} else {
				this.getHumanVisual().dressInNamedOutfit(string, this.itemVisuals);
				this.getHumanVisual().synchWithOutfit(this.getHumanVisual().getOutfit());
				this.onWornItemsChanged();
			}
		}
	}

	public void dressInPersistentOutfitID(int int1) {
		this.getHumanVisual().clear();
		this.itemVisuals.clear();
		this.m_persistentOutfitId = int1;
		this.m_bPersistentOutfitInit = true;
		if (int1 != 0) {
			this.bDressInRandomOutfit = false;
			PersistentOutfits.instance.dressInOutfit(this, int1);
			this.onWornItemsChanged();
		}
	}

	public void dressInClothingItem(String string) {
		this.wornItems.clear();
		this.getHumanVisual().dressInClothingItem(string, this.itemVisuals);
		this.onWornItemsChanged();
	}

	public void onWornItemsChanged() {
		this.parameterShoeType.setShoeType((ParameterShoeType.ShoeType)null);
	}

	public void clothingItemChanged(String string) {
		super.clothingItemChanged(string);
		if (!StringUtils.isNullOrWhitespace(this.pendingOutfitName)) {
			Outfit outfit = this.isFemale() ? OutfitManager.instance.FindFemaleOutfit(this.pendingOutfitName) : OutfitManager.instance.FindMaleOutfit(this.pendingOutfitName);
			if (outfit != null && !outfit.isEmpty()) {
				this.dressInNamedOutfit(this.pendingOutfitName);
				this.pendingOutfitName = null;
				this.resetModelNextFrame();
			}
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
		this.ImmortalTutorialZombie = boolean1;
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
		} else if (this.isProne()) {
			if (baseVehicle.getDriver() == null) {
				return false;
			} else {
				int int1 = baseVehicle.isEngineRunning() ? baseVehicle.testCollisionWithProneCharacter(this, true) : 0;
				if (int1 > 0) {
					if (!this.emitter.isPlaying(this.getHurtSound())) {
						this.playHurtSound();
					}

					this.AttackedBy = baseVehicle.getDriver();
					this.doHit(baseVehicle);
					if (!GameClient.bClient) {
						this.checkDead(baseVehicle.getDriver(), true);
					}

					super.update();
					return true;
				} else {
					return false;
				}
			}
		} else {
			if (baseVehicle.shouldCollideWithCharacters()) {
				Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
				if (baseVehicle.testCollisionWithCharacter(this, 0.3F, vector2) != null) {
					((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
					this.doHit(baseVehicle);
					super.update();
					return true;
				}

				((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
			}

			return false;
		}
	}

	public boolean isCrawling() {
		return this.bCrawling;
	}

	public boolean isCanCrawlUnderVehicle() {
		return this.bCanCrawlUnderVehicle;
	}

	public void setCanCrawlUnderVehicle(boolean boolean1) {
		this.bCanCrawlUnderVehicle = boolean1;
	}

	public boolean isCanWalk() {
		return this.bCanWalk;
	}

	public void setCanWalk(boolean boolean1) {
		this.bCanWalk = boolean1;
	}

	public void initCanCrawlUnderVehicle() {
		byte byte1 = 100;
		switch (SandboxOptions.instance.Lore.CrawlUnderVehicle.getValue()) {
		case 1: 
			byte1 = 0;
			break;
		
		case 2: 
			byte1 = 5;
			break;
		
		case 3: 
			byte1 = 10;
			break;
		
		case 4: 
			byte1 = 25;
			break;
		
		case 5: 
			byte1 = 50;
			break;
		
		case 6: 
			byte1 = 75;
			break;
		
		case 7: 
			byte1 = 100;
		
		}
		this.setCanCrawlUnderVehicle(Rand.Next(100) < byte1);
	}

	public boolean shouldGetUpFromCrawl() {
		if (this.isCurrentState(ZombieGetUpFromCrawlState.instance())) {
			return true;
		} else if (this.isCurrentState(ZombieGetUpState.instance())) {
			return this.stateMachine.getPrevious() == ZombieGetUpFromCrawlState.instance();
		} else if (!this.isCrawling()) {
			return false;
		} else if (!this.isCanWalk()) {
			return false;
		} else {
			if (this.isBeingSteppedOn()) {
			}

			if (this.isCurrentState(PathFindState.instance())) {
				return this.stateMachine.getPrevious() == ZombieGetDownState.instance() && ZombieGetDownState.instance().isNearStartXY(this) ? false : this.getPathFindBehavior2().shouldGetUpFromCrawl();
			} else {
				if (this.isCurrentState(WalkTowardState.instance())) {
					float float1 = this.getPathFindBehavior2().getTargetX();
					float float2 = this.getPathFindBehavior2().getTargetY();
					if (this.DistToSquared(float1, float2) > 0.010000001F && PolygonalMap2.instance.lineClearCollide(this.x, this.y, float1, float2, (int)this.z, (IsoMovingObject)null)) {
						return false;
					}
				}

				return this.isCurrentState(ZombieGetDownState.instance()) ? false : PolygonalMap2.instance.canStandAt(this.x, this.y, (int)this.z, (IsoMovingObject)null, false, true);
			}
		}
	}

	public void toggleCrawling() {
		boolean boolean1 = this.bCanCrawlUnderVehicle;
		if (this.bCrawling) {
			this.bCrawling = false;
			this.bKnockedDown = false;
			this.bStaggerBack = false;
			this.setFallOnFront(false);
			this.setOnFloor(false);
			this.DoZombieStats();
		} else {
			this.bCrawling = true;
			this.setOnFloor(true);
			this.DoZombieStats();
			this.walkVariant = "ZombieWalk";
		}

		this.bCanCrawlUnderVehicle = boolean1;
	}

	public void knockDown(boolean boolean1) {
		this.bKnockedDown = true;
		this.bStaggerBack = true;
		this.setHitReaction("");
		this.playerAttackPosition = boolean1 ? "BEHIND" : null;
		this.setHitForce(1.0F);
		this.reportEvent("wasHit");
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

	public IsoMovingObject getEatBodyTarget() {
		return this.eatBodyTarget;
	}

	public void setEatBodyTarget(IsoMovingObject movingObject, boolean boolean1) {
		if (movingObject != this.eatBodyTarget) {
			if (boolean1 || movingObject == null || movingObject.getEatingZombies().size() < 3) {
				if (this.eatBodyTarget != null) {
					this.eatBodyTarget.getEatingZombies().remove(this);
				}

				this.eatBodyTarget = movingObject;
				if (movingObject != null) {
					this.eatBodyTarget.getEatingZombies().add(this);
					this.eatSpeed = Rand.Next(0.8F, 1.2F) * GameTime.getAnimSpeedFix();
				}
			}
		}
	}

	private void updateEatBodyTarget() {
		if (this.bodyToEat != null && this.isCurrentState(ZombieIdleState.instance()) && this.DistToSquared(this.bodyToEat) <= 1.0F && (int)this.getZ() == (int)this.bodyToEat.getZ()) {
			this.setEatBodyTarget(this.bodyToEat, false);
			this.bodyToEat = null;
		}

		if (this.eatBodyTarget != null) {
			if (this.eatBodyTarget instanceof IsoDeadBody && this.eatBodyTarget.getStaticMovingObjectIndex() == -1) {
				this.setEatBodyTarget((IsoMovingObject)null, false);
			}

			if (this.target != null && !this.target.isOnFloor() && this.target != this.eatBodyTarget) {
				this.setEatBodyTarget((IsoMovingObject)null, false);
			}

			IsoPlayer player = (IsoPlayer)Type.tryCastTo(this.eatBodyTarget, IsoPlayer.class);
			if (player != null && player.ReanimatedCorpse != null) {
				this.setEatBodyTarget((IsoMovingObject)null, false);
			}

			if (player != null && player.isAlive() && !player.isOnFloor() && !player.isCurrentState(PlayerHitReactionState.instance())) {
				this.setEatBodyTarget((IsoMovingObject)null, false);
			}

			if (!this.isCurrentState(ZombieEatBodyState.instance()) && this.eatBodyTarget != null && this.DistToSquared(this.eatBodyTarget) > 1.0F) {
				this.setEatBodyTarget((IsoMovingObject)null, false);
			}

			if (this.eatBodyTarget != null && this.eatBodyTarget.getSquare() != null && this.current != null && this.current.isSomethingTo(this.eatBodyTarget.getSquare())) {
				this.setEatBodyTarget((IsoMovingObject)null, false);
			}
		}
	}

	private void updateCharacterTextureAnimTime() {
		boolean boolean1 = this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(PathFindState.instance());
		this.m_characterTextureAnimDuration = boolean1 ? 0.67F : 2.0F;
		this.m_characterTextureAnimTime += GameTime.getInstance().getTimeDelta();
		if (this.m_characterTextureAnimTime > this.m_characterTextureAnimDuration) {
			this.m_characterTextureAnimTime %= this.m_characterTextureAnimDuration;
		}
	}

	public Vector2 getHitAngle() {
		return this.hitAngle;
	}

	public void setHitAngle(Vector2 vector2) {
		if (vector2 != null) {
			this.hitAngle.set(vector2);
		}
	}

	public int getCrawlerType() {
		return this.crawlerType;
	}

	public void setCrawlerType(int int1) {
		this.crawlerType = int1;
	}

	public void addRandomVisualBandages() {
		if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
			for (int int1 = 0; int1 < 5; ++int1) {
				if (OutfitRNG.Next(10) == 0) {
					BodyPartType bodyPartType = BodyPartType.getRandom();
					String string = bodyPartType.getBandageModel() + "_Blood";
					this.addBodyVisualFromItemType(string);
				}
			}
		}
	}

	public void addVisualBandage(BodyPartType bodyPartType, boolean boolean1) {
		String string = bodyPartType.getBandageModel();
		String string2 = string + (boolean1 ? "_Blood" : "");
		this.addBodyVisualFromItemType(string2);
	}

	public void addRandomVisualDamages() {
		for (int int1 = 0; int1 < 5; ++int1) {
			if (OutfitRNG.Next(5) == 0) {
				String string = (String)OutfitRNG.pickRandom(ScriptManager.instance.getZedDmgMap());
				this.addBodyVisualFromItemType("Base." + string);
			}
		}
	}

	public String getPlayerAttackPosition() {
		return this.playerAttackPosition;
	}

	public void setPlayerAttackPosition(String string) {
		this.playerAttackPosition = string;
	}

	public boolean isSitAgainstWall() {
		return this.sitAgainstWall;
	}

	public void setSitAgainstWall(boolean boolean1) {
		this.sitAgainstWall = boolean1;
		this.networkAI.extraUpdate();
	}

	public boolean isSkeleton() {
		if (Core.bDebug && DebugOptions.instance.ModelSkeleton.getValue()) {
			this.getHumanVisual().setSkinTextureIndex(2);
			return true;
		} else {
			return this.isSkeleton;
		}
	}

	public boolean isZombie() {
		return true;
	}

	public void setSkeleton(boolean boolean1) {
		this.isSkeleton = boolean1;
		if (boolean1) {
			this.getHumanVisual().setHairModel("");
			this.getHumanVisual().setBeardModel("");
			ModelManager.instance.Reset(this);
		}
	}

	public int getHitTime() {
		return this.hitTime;
	}

	public void setHitTime(int int1) {
		this.hitTime = int1;
	}

	public int getThumpTimer() {
		return this.thumpTimer;
	}

	public void setThumpTimer(int int1) {
		this.thumpTimer = int1;
	}

	public IsoMovingObject getTarget() {
		return this.target;
	}

	public void setTargetSeenTime(float float1) {
		this.targetSeenTime = float1;
	}

	public float getTargetSeenTime() {
		return this.targetSeenTime;
	}

	public boolean isTargetVisible() {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(this.target, IsoPlayer.class);
		if (player != null && this.getCurrentSquare() != null) {
			return GameServer.bServer ? ServerLOS.instance.isCouldSee(player, this.getCurrentSquare()) : this.getCurrentSquare().isCouldSee(player.getPlayerNum());
		} else {
			return false;
		}
	}

	public float getTurnDelta() {
		return this.m_turnDeltaNormal;
	}

	public boolean isAttacking() {
		return this.isZombieAttacking();
	}

	public boolean isZombieAttacking() {
		State state = this.getCurrentState();
		return state != null && state.isAttacking(this);
	}

	public boolean isZombieAttacking(IsoMovingObject movingObject) {
		if (GameClient.bClient) {
			return this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieBite".equals(this.legsSprite.CurrentAnim.name);
		} else {
			return movingObject == this.target && this.isCurrentState(AttackState.instance());
		}
	}

	public int getHitHeadWhileOnFloor() {
		return this.hitHeadWhileOnFloor;
	}

	public void setHitHeadWhileOnFloor(int int1) {
		this.hitHeadWhileOnFloor = int1;
		this.networkAI.extraUpdate();
	}

	public boolean isHitLegsWhileOnFloor() {
		return this.hitLegsWhileOnFloor;
	}

	public void setHitLegsWhileOnFloor(boolean boolean1) {
		this.hitLegsWhileOnFloor = boolean1;
	}

	public void makeInactive(boolean boolean1) {
		if (boolean1 != this.inactive) {
			if (boolean1) {
				this.walkType = Integer.toString(Rand.Next(3) + 1);
				this.walkType = "slow" + this.walkType;
				this.bRunning = false;
				this.inactive = true;
				this.speedType = 3;
			} else {
				this.speedType = -1;
				this.inactive = false;
				this.DoZombieStats();
			}
		}
	}

	public float getFootstepVolume() {
		return this.footstepVolume;
	}

	public boolean isFacingTarget() {
		if (this.target == null) {
			return false;
		} else {
			tempo.set(this.target.x - this.x, this.target.y - this.y).normalize();
			this.getLookVector(tempo2);
			float float1 = Vector2.dot(tempo.x, tempo.y, tempo2.x, tempo2.y);
			return (double)float1 >= 0.8;
		}
	}

	public boolean isTargetLocationKnown() {
		if (this.target == null) {
			return false;
		} else if (this.BonusSpotTime > 0.0F) {
			return true;
		} else {
			return this.TimeSinceSeenFlesh < 1.0F;
		}
	}

	protected int getSandboxMemoryDuration() {
		int int1 = SandboxOptions.instance.Lore.Memory.getValue();
		short short1 = 160;
		if (this.inactive) {
			short1 = 5;
		} else if (int1 == 1) {
			short1 = 250;
		} else if (int1 == 3) {
			short1 = 100;
		} else if (int1 == 4) {
			short1 = 5;
		}

		int int2 = short1 * 5;
		return int2;
	}

	public boolean shouldDoFenceLunge() {
		if (!SandboxOptions.instance.Lore.ZombiesFenceLunge.getValue()) {
			return false;
		} else if (Rand.NextBool(3)) {
			return false;
		} else {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(this.target, IsoGameCharacter.class);
			if (gameCharacter != null && (int)gameCharacter.getZ() == (int)this.getZ()) {
				if (gameCharacter.getVehicle() != null) {
					return false;
				} else {
					return (double)this.DistTo(gameCharacter) < 3.9;
				}
			} else {
				return false;
			}
		}
	}

	public boolean isProne() {
		if (!this.isOnFloor()) {
			return false;
		} else if (this.bCrawling) {
			return true;
		} else {
			return this.isCurrentState(ZombieOnGroundState.instance()) || this.isCurrentState(FakeDeadZombieState.instance());
		}
	}

	public void setTarget(IsoMovingObject movingObject) {
		if (this.target != movingObject && (!GameClient.bClient || !this.networkAI.isLocalControl())) {
			this.target = movingObject;
			this.networkAI.extraUpdate();
		}
	}

	public boolean isAlwaysKnockedDown() {
		return this.alwaysKnockedDown;
	}

	public void setAlwaysKnockedDown(boolean boolean1) {
		this.alwaysKnockedDown = boolean1;
	}

	public void setDressInRandomOutfit(boolean boolean1) {
		this.bDressInRandomOutfit = boolean1;
	}

	public void setBodyToEat(IsoDeadBody deadBody) {
		this.bodyToEat = deadBody;
	}

	public boolean isForceEatingAnimation() {
		return this.forceEatingAnimation;
	}

	public void setForceEatingAnimation(boolean boolean1) {
		this.forceEatingAnimation = boolean1;
	}

	public boolean isOnlyJawStab() {
		return this.onlyJawStab;
	}

	public void setOnlyJawStab(boolean boolean1) {
		this.onlyJawStab = boolean1;
	}

	public boolean isNoTeeth() {
		return this.noTeeth;
	}

	public void setNoTeeth(boolean boolean1) {
		this.noTeeth = boolean1;
	}

	public void setThumpFlag(int int1) {
		if (this.thumpFlag != int1) {
			this.thumpFlag = int1;
			this.networkAI.extraUpdate();
		}
	}

	public void checkDead(IsoGameCharacter gameCharacter, boolean boolean1) {
		if (this.isDead() && !this.isOnDeathDone()) {
			this.DoZombieInventory();
			LuaEventManager.triggerEvent("OnZombieDead", this);
			this.setDoDeathSound(boolean1);
			super.Kill(gameCharacter);
			this.setOnDeathDone(true);
		}
	}

	public static class AttackNetworkEvent {
		public short zombieId;
		public short targetId;
		public byte outcome;
		public String targetHitReaction;

		public void load(ByteBufferReader byteBufferReader) {
			this.zombieId = byteBufferReader.getShort();
			this.targetId = byteBufferReader.getShort();
			this.outcome = byteBufferReader.getByte();
			this.targetHitReaction = byteBufferReader.getUTF();
		}
	}

	private static class s_performance {
		static final PerformanceProfileProbe update = new PerformanceProfileProbe("IsoZombie.update");
		static final PerformanceProfileProbe postUpdate = new PerformanceProfileProbe("IsoZombie.postUpdate");
	}

	private static final class FloodFill {
		private IsoGridSquare start = null;
		private final int FLOOD_SIZE = 11;
		private final BooleanGrid visited = new BooleanGrid(11, 11);
		private final Stack stack = new Stack();
		private IsoBuilding building = null;
		private Mover mover = null;
		private final ArrayList choices = new ArrayList(121);

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
		private static IsoZombie.ZombieSound[] $values() {
			return new IsoZombie.ZombieSound[]{Burned, DeadCloseKilled, DeadNotCloseKilled, Hurt, Idle, Lunge, MAX};
		}
	}
}
