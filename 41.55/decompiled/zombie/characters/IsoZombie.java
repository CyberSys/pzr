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

   public void setVehicle4TestCollision(BaseVehicle var1) {
      this.vehicle4testCollision = var1;
   }

   public IsoZombie(IsoCell var1) {
      this(var1, (SurvivorDesc)null, -1);
   }

   public IsoZombie(IsoCell var1, SurvivorDesc var2, int var3) {
      super(var1, 0.0F, 0.0F, 0.0F);
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
      if (var2 != null) {
         this.descriptor = var2;
         this.palette = var3;
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
      HashMap var1 = this.getStateUpdateLookup();
      var1.clear();
      var1.put("attack-network", AttackNetworkState.instance());
      var1.put("attackvehicle-network", IdleState.instance());
      var1.put("fakedead-attack-network", IdleState.instance());
      var1.put("lunge-network", LungeNetworkState.instance());
      var1.put("walktoward-network", WalkTowardNetworkState.instance());
      if (this.bCrawling) {
         var1.put("attack", AttackState.instance());
         var1.put("fakedead", FakeDeadZombieState.instance());
         var1.put("fakedead-attack", FakeDeadAttackState.instance());
         var1.put("getup", ZombieGetUpFromCrawlState.instance());
         var1.put("hitreaction", ZombieHitReactionState.instance());
         var1.put("hitreaction-hit", ZombieHitReactionState.instance());
         var1.put("idle", ZombieIdleState.instance());
         var1.put("onground", ZombieOnGroundState.instance());
         var1.put("pathfind", PathFindState.instance());
         var1.put("reanimate", ZombieReanimateState.instance());
         var1.put("staggerback", StaggerBackState.instance());
         var1.put("thump", ThumpState.instance());
         var1.put("turn", CrawlingZombieTurnState.instance());
         var1.put("walktoward", WalkTowardState.instance());
      } else {
         var1.put("attack", AttackState.instance());
         var1.put("attackvehicle", AttackVehicleState.instance());
         var1.put("bumped", BumpedState.instance());
         var1.put("climbfence", ClimbOverFenceState.instance());
         var1.put("climbwindow", ClimbThroughWindowState.instance());
         var1.put("eatbody", ZombieEatBodyState.instance());
         var1.put("falldown", ZombieFallDownState.instance());
         var1.put("falling", ZombieFallingState.instance());
         var1.put("face-target", ZombieFaceTargetState.instance());
         var1.put("fakedead", FakeDeadZombieState.instance());
         var1.put("fakedead-attack", FakeDeadAttackState.instance());
         var1.put("getdown", ZombieGetDownState.instance());
         var1.put("getup", ZombieGetUpState.instance());
         var1.put("hitreaction", ZombieHitReactionState.instance());
         var1.put("hitreaction-hit", ZombieHitReactionState.instance());
         var1.put("idle", ZombieIdleState.instance());
         var1.put("lunge", LungeState.instance());
         var1.put("onground", ZombieOnGroundState.instance());
         var1.put("pathfind", PathFindState.instance());
         var1.put("sitting", ZombieSittingState.instance());
         var1.put("staggerback", StaggerBackState.instance());
         var1.put("thump", ThumpState.instance());
         var1.put("turnalerted", ZombieTurnAlerted.instance());
         var1.put("walktoward", WalkTowardState.instance());
      }

   }

   private void registerVariableCallbacks() {
      this.setVariable("bClient", () -> {
         return GameClient.bClient;
      });
      this.setVariable("bMovingNetwork", () -> {
         return IsoUtils.DistanceManhatten(this.networkAI.targetX, this.networkAI.targetY, this.x, this.y) > 0.5F || this.z != (float)this.networkAI.targetZ;
      });
      this.setVariable("hitHeadType", this::getHitHeadWhileOnFloor);
      this.setVariable("battack", () -> {
         if (this.target == null) {
            return false;
         } else {
            if (this.target instanceof IsoGameCharacter) {
               if (this.target.isOnFloor() && ((IsoGameCharacter)this.target).getCurrentState() != BumpedState.instance()) {
                  this.setTarget((IsoMovingObject)null);
                  return false;
               }

               BaseVehicle var1 = ((IsoGameCharacter)this.target).getVehicle();
               if (var1 != null) {
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
               IsoGridSquare var5 = this.getCurrentSquare();
               IsoGridSquare var2 = this.target.getCurrentSquare();
               if (var5 != null && var5.isSomethingTo(var2)) {
                  return false;
               } else if (GameClient.bClient && !this.networkAI.isLocalControl() && this.attackNetworkEvents.isEmpty()) {
                  return false;
               } else {
                  float var3 = this.bCrawling ? 1.4F : 0.72F;
                  float var4 = this.vectorToTarget.getLength();
                  return var4 <= var3;
               }
            } else {
               return !this.isUnderVehicle() && this.DistTo(this.target) < 1.3F;
            }
         }
      });
      this.setVariable("isFacingTarget", this::isFacingTarget);
      this.setVariable("targetSeenTime", this::getTargetSeenTime);
      this.setVariable("targethitreaction", () -> {
         return this.target != null ? ((IsoGameCharacter)this.target).getHitReaction() : "";
      });
      this.setVariable("battackvehicle", () -> {
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
            BaseVehicle var1 = ((IsoGameCharacter)this.target).getVehicle();
            return var1 != null && var1.isCharacterAdjacentTo(this);
         }
      });
      this.setVariable("bdead", this::isDead);
      this.setVariable("beatbodytarget", () -> {
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
      this.setVariable("bfakedead", () -> {
         return this.bFakeDead;
      });
      this.setVariable("bfalling", () -> {
         return this.z > 0.0F && this.fallTime > 2.0F;
      });
      this.setVariable("bhastarget", () -> {
         if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
            this.setTarget((IsoMovingObject)null);
         }

         return this.target != null;
      });
      this.setVariable("shouldSprint", () -> {
         if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
            this.setTarget((IsoMovingObject)null);
         }

         return this.target != null || this.soundSourceTarget != null && !(this.soundSourceTarget instanceof IsoZombie);
      });
      this.setVariable("bknockeddown", () -> {
         return this.bKnockedDown;
      });
      this.setVariable("blunge", () -> {
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
               IsoGridSquare var1 = this.getCurrentSquare();
               IsoGridSquare var2 = this.target.getCurrentSquare();
               if (var2 != null && var2.isSomethingTo(var1) && this.getThumpTarget() != null) {
                  return false;
               } else if (this.isCurrentState(ZombieTurnAlerted.instance()) && !this.isFacingTarget()) {
                  return false;
               } else {
                  float var3 = this.vectorToTarget.getLength();
                  return var3 > 3.5F && (!(var3 <= 4.0F) || !(this.target instanceof IsoGameCharacter) || ((IsoGameCharacter)this.target).getVehicle() == null) ? false : !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), this.target.x, this.target.y, (int)this.getZ(), this.target, false, true);
               }
            }
         }
      });
      this.setVariable("bpassengerexposed", () -> {
         return AttackVehicleState.instance().isPassengerExposed(this);
      });
      this.setVariable("bistargetissmallvehicle", () -> {
         return this.target != null && this.target instanceof IsoPlayer && ((IsoPlayer)this.target).getVehicle() != null ? ((IsoPlayer)this.target).getVehicle().getScript().isSmallVehicle : true;
      });
      this.setVariable("breanimate", this::isReanimate, this::setReanimate);
      this.setVariable("brunning", () -> {
         return this.bRunning;
      });
      this.setVariable("bstaggerback", () -> {
         return this.bStaggerBack;
      });
      this.setVariable("btargetvehicle", () -> {
         return this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null;
      });
      this.setVariable("bthump", () -> {
         if (this.getThumpTarget() instanceof IsoObject && !(this.getThumpTarget() instanceof BaseVehicle)) {
            IsoObject var1 = (IsoObject)this.getThumpTarget();
            if (var1 != null && this.DistToSquared(var1.getX() + 0.5F, var1.getY() + 0.5F) > 9.0F) {
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
      this.setVariable("distancetotarget", () -> {
         return this.target == null ? "" : String.valueOf(this.vectorToTarget.getLength() - this.getWidth() + this.target.getWidth());
      });
      this.setVariable("lasttargetseen", () -> {
         return this.LastTargetSeenX != -1;
      });
      this.setVariable("lungetimer", () -> {
         return this.LungeTimer;
      });
      this.setVariable("reanimatetimer", this::getReanimateTimer);
      this.setVariable("stateeventdelaytimer", this::getStateEventDelayTimer);
      this.setVariable("turndirection", () -> {
         if (this.getPath2() != null) {
            return "";
         } else {
            IsoDirections var1;
            boolean var2;
            if (this.target != null && this.vectorToTarget.getLength() != 0.0F) {
               var1 = IsoDirections.fromAngle(this.vectorToTarget);
               if (this.dir == var1) {
                  return "";
               } else {
                  var2 = CrawlingZombieTurnState.calculateDir(this, var1);
                  return var2 ? "left" : "right";
               }
            } else if (this.isCurrentState(WalkTowardState.instance())) {
               WalkTowardState.instance().calculateTargetLocation(this, tempo);
               Vector2 var10000 = tempo;
               var10000.x -= this.getX();
               var10000 = tempo;
               var10000.y -= this.getY();
               var1 = IsoDirections.fromAngle(tempo);
               if (this.dir == var1) {
                  return "";
               } else {
                  var2 = CrawlingZombieTurnState.calculateDir(this, var1);
                  return var2 ? "left" : "right";
               }
            } else {
               if (this.isCurrentState(PathFindState.instance())) {
               }

               return "";
            }
         }
      });
      this.setVariable("hitforce", this::getHitForce);
      this.setVariable("alerted", () -> {
         return this.alerted;
      });
      this.setVariable("zombiewalktype", () -> {
         return this.walkType;
      });
      this.setVariable("crawlertype", () -> {
         return this.crawlerType;
      });
      this.setVariable("bGetUpFromCrawl", this::shouldGetUpFromCrawl);
      this.setVariable("playerattackposition", this::getPlayerAttackPosition);
      this.setVariable("eatspeed", () -> {
         return this.eatSpeed;
      });
      this.setVariable("issitting", this::isSitAgainstWall);
   }

   public void actionStateChanged(ActionContext var1) {
      super.actionStateChanged(var1);
      if (this.networkAI != null && GameServer.bServer) {
         this.networkAI.extraUpdate();
      }

   }

   public ActionContext getActionContext() {
      return this.actionContext;
   }

   protected void onAnimPlayerCreated(AnimationPlayer var1) {
      super.onAnimPlayerCreated(var1);
      var1.setSharedAnimRepo(m_sharedSkeleRepo);
   }

   public String GetAnimSetName() {
      return this.bCrawling ? "zombie-crawler" : "zombie";
   }

   public void InitSpritePartsZombie() {
      SurvivorDesc var1 = this.descriptor;
      this.InitSpritePartsZombie(var1);
   }

   public void InitSpritePartsZombie(SurvivorDesc var1) {
      this.sprite.AnimMap.clear();
      this.sprite.AnimStack.clear();
      this.sprite.CurrentAnim = new IsoAnim();
      this.sprite.CurrentAnim.name = "REMOVE";
      this.legsSprite = this.sprite;
      this.legsSprite.name = var1.torso;
      this.ZombieID = Rand.Next(10000);
      this.bUseParts = true;
   }

   public void pathToCharacter(IsoGameCharacter var1) {
      if (!(this.AllowRepathDelay > 0.0F) || !this.isCurrentState(PathFindState.instance()) && !this.isCurrentState(WalkTowardState.instance()) && !this.isCurrentState(WalkTowardNetworkState.instance())) {
         super.pathToCharacter(var1);
      }
   }

   public void pathToLocationF(float var1, float var2, float var3) {
      if (!(this.AllowRepathDelay > 0.0F) || !this.isCurrentState(PathFindState.instance()) && !this.isCurrentState(WalkTowardState.instance()) && !this.isCurrentState(WalkTowardNetworkState.instance())) {
         super.pathToLocationF(var1, var2, var3);
      }
   }

   public void load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      super.load(var1, var2, var3);
      this.walkVariant = "ZombieWalk";
      this.SpriteName = "BobZ";
      if (this.palette != 1) {
         this.SpriteName = this.SpriteName + this.palette;
      }

      SurvivorDesc var4 = this.descriptor;
      this.setFemale(var4.isFemale());
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

      this.InitSpritePartsZombie(var4);
      this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
      this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
      this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
      this.setDefaultState(ZombieIdleState.instance());
      this.DoZombieStats();
      var1.getFloat();
      this.setWidth(0.3F);
      this.TimeSinceSeenFlesh = (float)var1.getInt();
      this.setAlpha(0.0F);
      this.setFakeDead(var1.getInt() == 1);
      ArrayList var5 = this.savedInventoryItems;
      byte var6 = var1.get();

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = GameWindow.ReadString(var1);
         short var9 = var1.getShort();
         if (var9 >= 0 && var9 < var5.size() && this.wornItems.getBodyLocationGroup().getLocation(var8) != null) {
            this.wornItems.setItem(var8, (InventoryItem)var5.get(var9));
         }
      }

      this.setStateMachineLocked(false);
      this.setDefaultState();
      this.getCell().getZombieList().add(this);
   }

   public void save(ByteBuffer var1, boolean var2) throws IOException {
      super.save(var1, var2);
      var1.putFloat(0.0F);
      var1.putInt((int)this.TimeSinceSeenFlesh);
      var1.putInt(this.isFakeDead() ? 1 : 0);
      if (this.wornItems.size() > 127) {
         throw new RuntimeException("too many worn items");
      } else {
         var1.put((byte)this.wornItems.size());
         this.wornItems.forEach((var2x) -> {
            GameWindow.WriteString(var1, var2x.getLocation());
            var1.putShort((short)this.savedInventoryItems.indexOf(var2x.getItem()));
         });
      }
   }

   public void collideWith(IsoObject var1) {
      if (!this.Ghost && var1 != null) {
         if (var1.rerouteCollide != null) {
            var1 = this.rerouteCollide;
         }

         State var2 = this.getCurrentState();
         boolean var3 = this.isCurrentState(PathFindState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance());
         IsoWindow var4 = (IsoWindow)Type.tryCastTo(var1, IsoWindow.class);
         if (var4 != null && var4.canClimbThrough(this) && var3) {
            if (!this.isFacingObject(var4, 0.8F)) {
               super.collideWith(var1);
               return;
            }

            if ((var2 != PathFindState.instance() || this.isOnPath(var1)) && !this.bCrawling) {
               this.climbThroughWindow(var4);
            }
         } else if (var1 instanceof IsoThumpable && ((IsoThumpable)var1).canClimbThrough(this) && var3) {
            if ((var2 != PathFindState.instance() || this.isOnPath(var1)) && !this.bCrawling) {
               this.climbThroughWindow((IsoThumpable)var1);
            }
         } else if ((!(var1 instanceof IsoDoor) || !((IsoDoor)var1).isHoppable()) && var1 != null && var1.getThumpableFor(this) != null && var3) {
            boolean var5 = (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance())) && this.getPathFindBehavior2().isGoalSound();
            if (!SandboxOptions.instance.Lore.ThumpNoChasing.getValue() && this.target == null && !var5) {
               this.setVariable("bPathfind", false);
               this.setVariable("bMoving", false);
               this.setPath2((PolygonalMap2.Path)null);
            } else {
               if (var1 instanceof IsoThumpable && !SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
                  return;
               }

               Object var6 = var1;
               if (var1 instanceof IsoWindow && ((IsoWindow)var1).getThumpableFor(this) != null && ((IsoWindow)var1).isDestroyed()) {
                  var6 = ((IsoWindow)var1).getThumpableFor(this);
               }

               this.setThumpTarget((Thumpable)var6);
               this.setPath2((PolygonalMap2.Path)null);
            }
         }

         if (!this.bCrawling && IsoWindowFrame.isWindowFrame(var1) && var3 && (var2 != PathFindState.instance() || this.isOnPath(var1))) {
            this.climbThroughWindowFrame(var1);
         }

         super.collideWith(var1);
      }
   }

   private boolean isOnPath(IsoObject var1) {
      return false;
   }

   public float Hit(HandWeapon var1, IsoGameCharacter var2, float var3, boolean var4, float var5, boolean var6) {
      if (Core.bTutorial && this.ImmortalTutorialZombie) {
         return 0.0F;
      } else {
         BodyPartType var7 = BodyPartType.FromIndex(Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Upper), BodyPartType.ToIndex(BodyPartType.Torso_Lower) + 1));
         if (Rand.NextBool(7)) {
            var7 = BodyPartType.Head;
         }

         if (var2.isCrit && Rand.NextBool(3)) {
            var7 = BodyPartType.Head;
         }

         LuaEventManager.triggerEvent("OnHitZombie", this, var2, var7, var1);
         float var8 = super.Hit(var1, var2, var3, var4, var5, var6);
         if (this.Health <= 0.0F && !this.isOnDeathDone()) {
            this.DoZombieInventory();
            LuaEventManager.triggerEvent("OnZombieDead", this);
            this.setOnDeathDone(true);
         }

         this.TimeSinceSeenFlesh = 0.0F;
         if (!this.isDead() && !this.emitter.isPlaying(this.getHurtSound())) {
            this.playHurtSound();
         }

         if (!this.isDead() && !this.isOnFloor() && !var4 && var1 != null && var1.getScriptItem().getCategories().contains("Blade") && var2 instanceof IsoPlayer && this.DistToProper(var2) <= 0.9F && (this.isCurrentState(AttackState.instance()) || this.isCurrentState(AttackNetworkState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()))) {
            this.setHitForce(0.5F);
            this.changeState(StaggerBackState.instance());
         }

         if (GameServer.bServer || GameClient.bClient && this.isDead()) {
            this.lastPlayerHit = var2.getOnlineID();
         }

         return var8;
      }
   }

   public void onMouseLeftClick() {
      if (IsoPlayer.getInstance() == null || !IsoPlayer.getInstance().isAiming()) {
         if (IsoPlayer.getInstance().IsAttackRange(this.getX(), this.getY(), this.getZ())) {
            Vector2 var1 = new Vector2(this.getX(), this.getY());
            var1.x -= IsoPlayer.getInstance().getX();
            var1.y -= IsoPlayer.getInstance().getY();
            var1.normalize();
            IsoPlayer.getInstance().DirectionFromVector(var1);
            IsoPlayer.getInstance().AttemptAttack();
         }

      }
   }

   private void renderAtlasTexture(float var1, float var2, float var3) {
      if (this.atlasTex != null) {
         if (IsoSprite.globalOffsetX == -1.0F) {
            IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
            IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
         }

         float var4 = IsoUtils.XToScreen(var1, var2, var3, 0);
         float var5 = IsoUtils.YToScreen(var1, var2, var3, 0);
         this.sx = var4;
         this.sy = var5;
         var4 = this.sx + IsoSprite.globalOffsetX;
         var5 = this.sy + IsoSprite.globalOffsetY;
         ColorInfo var6 = inf.set(1.0F, 1.0F, 1.0F, 1.0F);
         if (PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
            this.getCurrentSquare().interpolateLight(var6, var1 - (float)this.getCurrentSquare().getX(), var2 - (float)this.getCurrentSquare().getY());
         }

         this.atlasTex.render((float)((int)var4 - this.atlasTex.getWidth() / 2), (float)((int)var5 - this.atlasTex.getHeight() / 2), (float)this.atlasTex.getWidth(), (float)this.atlasTex.getHeight(), var6.r, var6.g, var6.b, var6.a, (Consumer)null);
      }
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5, boolean var6, Shader var7) {
      if (this.getCurrentState() == FakeDeadZombieState.instance()) {
         if (this.bDressInRandomOutfit) {
            ModelManager.instance.dressInRandomOutfit(this);
         }

         if (this.atlasTex == null) {
            this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
            DeadBodyAtlas.instance.render();
         }

         if (this.atlasTex != null) {
            this.renderAtlasTexture(var1, var2, var3);
         }

      } else {
         if (this.atlasTex != null) {
            this.atlasTex = null;
         }

         if (IsoCamera.CamCharacter != IsoPlayer.getInstance()) {
            this.setAlphaAndTarget(1.0F);
         }

         super.render(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public void renderlast() {
      super.renderlast();
      if (DebugOptions.instance.ZombieRenderCanCrawlUnderVehicle.getValue() && this.isCanCrawlUnderVehicle()) {
         this.renderTextureOverHead("media/ui/FavoriteStar.png");
      }

      if (DebugOptions.instance.ZombieRenderMemory.getValue()) {
         String var1;
         if (this.target == null) {
            var1 = "media/ui/Moodles/Moodle_Icon_Bored.png";
         } else if (this.BonusSpotTime == 0.0F) {
            var1 = "media/ui/Moodles/Moodle_Icon_Angry.png";
         } else {
            var1 = "media/ui/Moodles/Moodle_Icon_Zombie.png";
         }

         this.renderTextureOverHead(var1);
         int var2 = (int)IsoUtils.XToScreenExact(this.x, this.y, this.z, 0);
         int var3 = (int)IsoUtils.YToScreenExact(this.x, this.y, this.z, 0);
         int var4 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
         TextManager.instance.DrawString((double)var2, (double)(var3 += var4), "AllowRepathDelay : " + this.AllowRepathDelay);
         TextManager.instance.DrawString((double)var2, (double)(var3 += var4), "BonusSpotTime : " + this.BonusSpotTime);
         TextManager.instance.DrawString((double)var2, (double)(var3 + var4), "TimeSinceSeenFlesh : " + this.TimeSinceSeenFlesh);
      }

   }

   protected boolean renderTextureInsteadOfModel(float var1, float var2) {
      boolean var3 = this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(PathFindState.instance());
      String var4 = "zombie";
      String var5 = var3 ? "walktoward" : "idle";
      byte var6 = 4;
      int var7 = (int)(this.m_characterTextureAnimTime / this.m_characterTextureAnimDuration * (float)var6);
      float var8 = (var3 ? 0.67F : 1.0F) * ((float)var7 / (float)var6);
      Texture var9 = DeadBodyAtlas.instance.getBodyTexture(this.isFemale(), var4, var5, this.getDir(), var7, var8);
      if (var9 != null && var9.isReady()) {
         float var10 = (float)Core.TileScale;
         float var11 = this.offsetX + 1.0F * var10;
         float var12 = this.offsetY + -89.0F * var10;
         float var13 = IsoUtils.XToScreen(var1, var2, this.getZ(), 0);
         float var14 = IsoUtils.YToScreen(var1, var2, this.getZ(), 0);
         var13 = var13 - IsoCamera.getOffX() - var11;
         var14 = var14 - IsoCamera.getOffY() - var12;
         var13 -= (float)(var9.getWidthOrig() / 2);
         var14 -= (float)var9.getHeightOrig();
         var14 -= 64.0F * var10;
         int var15 = IsoCamera.frameState.playerIndex;
         var9.render(var13, var14, (float)var9.getWidth(), (float)var9.getHeight(), 0.0F, 0.0F, 0.0F, this.getAlpha(var15), (Consumer)null);
      }

      if (DebugOptions.instance.Character.Debug.Render.Angle.getValue()) {
         tempo.set(this.dir.ToVector());
         this.drawDirectionLine(tempo, 1.2F, 0.0F, 1.0F, 0.0F);
      }

      return true;
   }

   private void renderTextureOverHead(String var1) {
      float var2 = this.x;
      float var3 = this.y;
      float var4 = IsoUtils.XToScreen(var2, var3, this.getZ(), 0);
      float var5 = IsoUtils.YToScreen(var2, var3, this.getZ(), 0);
      var4 = var4 - IsoCamera.getOffX() - this.offsetX;
      var5 = var5 - IsoCamera.getOffY() - this.offsetY;
      var5 -= (float)(128 / (2 / Core.TileScale));
      Texture var6 = Texture.getSharedTexture(var1);
      float var7 = Core.getInstance().getZoom(IsoCamera.frameState.playerIndex);
      var7 = Math.max(var7, 1.0F);
      int var8 = (int)((float)var6.getWidth() * var7);
      int var9 = (int)((float)var6.getHeight() * var7);
      var6.render((float)((int)var4 - var8 / 2), (float)((int)var5 - var9), (float)var8, (float)var9);
   }

   protected void updateAlpha(int var1, float var2, float var3) {
      if (this.isFakeDead()) {
         this.setAlphaAndTarget(1.0F);
      } else {
         super.updateAlpha(var1, var2, var3);
      }
   }

   public void RespondToSound() {
      if (!this.Ghost) {
         if (!this.Deaf) {
            if (!this.isUseless()) {
               float var1;
               if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && this.getPathFindBehavior2().isGoalSound() && (int)this.z == this.getPathTargetZ() && this.bSoundSourceRepeating) {
                  var1 = this.DistToSquared((float)this.getPathTargetX(), (float)this.getPathTargetY());
                  if (var1 < 25.0F && LosUtil.lineClear(this.getCell(), (int)this.x, (int)this.y, (int)this.z, this.getPathTargetX(), this.getPathTargetY(), (int)this.z, false) != LosUtil.TestResults.Blocked) {
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

               var1 = 0.0F;
               Object var2 = null;
               WorldSoundManager.WorldSound var3 = WorldSoundManager.instance.getSoundZomb(this);
               float var4 = WorldSoundManager.instance.getSoundAttract(var3, this);
               if (var4 <= 0.0F) {
                  var3 = null;
               }

               if (var3 != null) {
                  var1 = var4;
                  var2 = var3.source;
                  this.soundAttract = var4;
                  this.soundAttractTimeout = 60.0F;
               } else if (this.soundAttractTimeout > 0.0F) {
                  this.soundAttractTimeout -= GameTime.getInstance().getMultiplier() / 1.6F;
                  if (this.soundAttractTimeout < 0.0F) {
                     this.soundAttractTimeout = 0.0F;
                  }
               }

               WorldSoundManager.ResultBiggestSound var5 = WorldSoundManager.instance.getBiggestSoundZomb((int)this.getX(), (int)this.getY(), (int)this.getZ(), true, this);
               if (var5.sound != null && (this.soundAttractTimeout == 0.0F || this.soundAttract * 2.0F < var5.attract)) {
                  var3 = var5.sound;
                  var1 = var5.attract;
                  var2 = var3.source;
               }

               if (var3 != null && var3.bRepeating && var3.z == (int)this.z) {
                  float var6 = this.DistToSquared((float)var3.x, (float)var3.y);
                  if (var6 < 25.0F && LosUtil.lineClear(this.getCell(), (int)this.x, (int)this.y, (int)this.z, var3.x, var3.y, (int)this.z, false) != LosUtil.TestResults.Blocked) {
                     var3 = null;
                  }
               }

               if (var3 != null) {
                  this.soundAttract = var1;
                  this.soundSourceTarget = var2;
                  this.soundReactDelay = (float)Rand.Next(0, 16);
                  this.delayedSound.x = var3.x;
                  this.delayedSound.y = var3.y;
                  this.delayedSound.z = var3.z;
                  this.bSoundSourceRepeating = var3.bRepeating;
               }

               if (this.delayedSound.x != -1 && this.soundReactDelay == 0.0F) {
                  int var10 = this.delayedSound.x;
                  int var7 = this.delayedSound.y;
                  int var8 = this.delayedSound.z;
                  this.delayedSound.x = -1;
                  float var9 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), (float)var10, (float)var7) / 2.5F;
                  var10 += Rand.Next((int)(-var9), (int)var9);
                  var7 += Rand.Next((int)(-var9), (int)var9);
                  if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) && (this.getPathFindBehavior2().isGoalLocation() || this.getPathFindBehavior2().isGoalSound())) {
                     if (!IsoUtils.isSimilarDirection(this, (float)var10, (float)var7, this.getPathFindBehavior2().getTargetX(), this.getPathFindBehavior2().getTargetY(), 0.5F)) {
                        this.setTurnAlertedValues(var10, var7);
                        this.pathToSound(var10, var7, var8);
                        this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
                        this.AllowRepathDelay = 120.0F;
                        this.timeSinceRespondToSound = 0.0F;
                     }

                     return;
                  }

                  if (this.timeSinceRespondToSound < 60.0F) {
                     return;
                  }

                  if (!IsoUtils.isSimilarDirection(this, (float)var10, (float)var7, this.x + this.getForwardDirection().x, this.y + this.getForwardDirection().y, 0.5F)) {
                     this.setTurnAlertedValues(var10, var7);
                  }

                  this.pathToSound(var10, var7, var8);
                  this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
                  this.AllowRepathDelay = 120.0F;
                  this.timeSinceRespondToSound = 0.0F;
               }

            }
         }
      }
   }

   public void setTurnAlertedValues(int var1, int var2) {
      Vector2 var3 = new Vector2(this.getX() - ((float)var1 + 0.5F), this.getY() - ((float)var2 + 0.5F));
      float var4 = var3.getDirectionNeg();
      if (var4 < 0.0F) {
         var4 = Math.abs(var4);
      } else {
         var4 = new Float(6.283185307179586D - (double)var4);
      }

      Double var5 = new Double(Math.toDegrees((double)var4));
      Vector2 var6 = new Vector2(IsoDirections.reverse(this.getDir()).ToVector().x, IsoDirections.reverse(this.getDir()).ToVector().y);
      var6.normalize();
      float var7 = var6.getDirectionNeg();
      if (var7 < 0.0F) {
         var7 = Math.abs(var7);
      } else {
         var7 = 6.2831855F - var7;
      }

      double var8 = Math.toDegrees((double)var7);
      if ((int)var8 == 360) {
         var8 = 0.0D;
      }

      if (var5.intValue() == 360) {
         var5 = 0.0D;
      }

      String var10 = "0";
      boolean var11 = false;
      int var12;
      if (var5 > var8) {
         var12 = (int)(var5 - var8);
         if (var12 > 350 || var12 <= 35) {
            var10 = "45R";
         }

         if (var12 > 35 && var12 <= 80) {
            var10 = "90R";
         }

         if (var12 > 80 && var12 <= 125) {
            var10 = "135R";
         }

         if (var12 > 125 && var12 <= 170) {
            var10 = "180R";
         }

         if (var12 > 170 && var12 < 215) {
            var10 = "180L";
         }

         if (var12 >= 215 && var12 < 260) {
            var10 = "135L";
         }

         if (var12 >= 260 && var12 < 305) {
            var10 = "90L";
         }

         if (var12 >= 305 && var12 < 350) {
            var10 = "45L";
         }
      } else {
         var12 = (int)(var8 - var5);
         if (var12 > 10 && var12 <= 55) {
            var10 = "45L";
         }

         if (var12 > 55 && var12 <= 100) {
            var10 = "90L";
         }

         if (var12 > 100 && var12 <= 145) {
            var10 = "135L";
         }

         if (var12 > 145 && var12 <= 190) {
            var10 = "180L";
         }

         if (var12 > 190 && var12 < 235) {
            var10 = "180R";
         }

         if (var12 >= 235 && var12 < 280) {
            var10 = "135R";
         }

         if (var12 >= 280 && var12 < 325) {
            var10 = "90R";
         }

         if (var12 >= 325 || var12 < 10) {
            var10 = "45R";
         }
      }

      this.setVariable("turnalertedvalue", var10);
      ZombieTurnAlerted.instance().setParams(this, var3.set((float)var1 + 0.5F - this.x, (float)var2 + 0.5F - this.y).getDirection());
      this.alerted = true;
      this.networkAI.extraUpdate();
   }

   public void spotted(IsoMovingObject var1, boolean var2) {
      if (!GameClient.bClient) {
         if (this.getCurrentSquare() != null) {
            if (var1.getCurrentSquare() != null) {
               if (!this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke) && !this.isUseless()) {
                  if (!(var1 instanceof IsoPlayer) || !((IsoPlayer)var1).isGhostMode()) {
                     IsoGameCharacter var3 = (IsoGameCharacter)Type.tryCastTo(var1, IsoGameCharacter.class);
                     if (var3 == null || !var3.isDead()) {
                        if (this.getCurrentSquare() == null) {
                           this.ensureOnTile();
                        }

                        if (var1.getCurrentSquare() == null) {
                           var1.ensureOnTile();
                        }

                        float var4 = 200.0F;
                        int var5 = var1 instanceof IsoPlayer && !GameServer.bServer ? ((IsoPlayer)var1).PlayerIndex : 0;
                        float var6 = (var1.getCurrentSquare().lighting[var5].lightInfo().r + var1.getCurrentSquare().lighting[var5].lightInfo().g + var1.getCurrentSquare().lighting[var5].lightInfo().b) / 3.0F;
                        float var7 = RenderSettings.getInstance().getAmbientForPlayer(var5);
                        float var8 = (this.getCurrentSquare().lighting[var5].lightInfo().r + this.getCurrentSquare().lighting[var5].lightInfo().g + this.getCurrentSquare().lighting[var5].lightInfo().b) / 3.0F;
                        var8 = var8 * var8 * var8;
                        if (var6 > 1.0F) {
                           var6 = 1.0F;
                        }

                        if (var6 < 0.0F) {
                           var6 = 0.0F;
                        }

                        if (var8 > 1.0F) {
                           var8 = 1.0F;
                        }

                        if (var8 < 0.0F) {
                           var8 = 0.0F;
                        }

                        float var9 = 1.0F - (var6 - var8);
                        if (var6 < 0.2F) {
                           var6 = 0.2F;
                        }

                        if (var7 < 0.2F) {
                           var7 = 0.2F;
                        }

                        if (var1.getCurrentSquare().getRoom() != this.getCurrentSquare().getRoom()) {
                           var4 = 50.0F;
                           if (var1.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() == null || var1.getCurrentSquare().getRoom() == null && this.getCurrentSquare().getRoom() != null) {
                              var4 = 20.0F;
                              if (!var3.isAiming() && !var3.isSneaking()) {
                                 if (var1.getMovementLastFrame().getLength() <= 0.04F && var6 < 0.4F) {
                                    var4 = 10.0F;
                                 }
                              } else if (var6 < 0.4F) {
                                 var4 = 0.0F;
                              } else {
                                 var4 = 10.0F;
                              }
                           }
                        }

                        tempo.x = var1.getX();
                        tempo.y = var1.getY();
                        Vector2 var10000 = tempo;
                        var10000.x -= this.getX();
                        var10000 = tempo;
                        var10000.y -= this.getY();
                        if (var1.getCurrentSquare().getZ() != this.current.getZ()) {
                           int var10 = Math.abs(var1.getCurrentSquare().getZ() - this.current.getZ()) * 5;
                           ++var10;
                           var4 /= (float)var10;
                        }

                        float var23 = GameTime.getInstance().getViewDist();
                        if (!(tempo.getLength() > var23)) {
                           if (GameServer.bServer) {
                              this.bIndoorZombie = false;
                           }

                           if (tempo.getLength() < var23) {
                              var23 = tempo.getLength();
                           }

                           var23 *= 1.1F;
                           if (var23 > GameTime.getInstance().getViewDistMax()) {
                              var23 = GameTime.getInstance().getViewDistMax();
                           }

                           tempo.normalize();
                           Vector2 var11 = this.getLookVector(tempo2);
                           float var12 = var11.dot(tempo);
                           if (this.DistTo(var1) > 20.0F) {
                              var4 -= 10000.0F;
                           }

                           if ((double)var23 > 0.5D) {
                              if (var12 < -0.4F) {
                                 var4 = 0.0F;
                              } else if (var12 < -0.2F) {
                                 var4 /= 8.0F;
                              } else if (var12 < -0.0F) {
                                 var4 /= 4.0F;
                              } else if (var12 < 0.2F) {
                                 var4 /= 2.0F;
                              } else if (var12 <= 0.4F) {
                                 var4 *= 2.0F;
                              } else if (var12 > 0.4F) {
                                 var4 *= 8.0F;
                              } else if (var12 > 0.6F) {
                                 var4 *= 16.0F;
                              } else if (var12 > 0.8F) {
                                 var4 *= 32.0F;
                              }
                           }

                           if (var4 > 0.0F && this.target instanceof IsoPlayer) {
                              IsoPlayer var13 = (IsoPlayer)this.target;
                              if (!GameServer.bServer && var13.RemoteID == -1 && this.current.isCanSee(var13.PlayerIndex)) {
                                 ((IsoPlayer)this.target).targetedByZombie = true;
                                 ((IsoPlayer)this.target).lastTargeted = 0.0F;
                              }
                           }

                           var4 *= var9;
                           int var24 = (int)var1.getZ() - (int)this.getZ();
                           if (var24 >= 1) {
                              var4 /= (float)(var24 * 3);
                           }

                           float var14 = PZMath.clamp(var23 / GameTime.getInstance().getViewDist(), 0.0F, 1.0F);
                           var4 *= 1.0F - var14;
                           var4 *= 1.0F - var14;
                           var4 *= 1.0F - var14;
                           float var15 = PZMath.clamp(var23 / 10.0F, 0.0F, 1.0F);
                           var4 *= 1.0F + (1.0F - var15) * 10.0F;
                           float var16 = var1.getMovementLastFrame().getLength();
                           if (var16 == 0.0F && var6 <= 0.2F) {
                              var6 = 0.0F;
                           }

                           if (var3 != null) {
                              if (var3.getTorchStrength() > 0.0F) {
                                 var4 *= 3.0F;
                              }

                              if (var16 < 0.01F) {
                                 var4 *= 0.5F;
                              } else if (var3.isSneaking()) {
                                 var4 *= 0.4F;
                              } else if (var3.isAiming()) {
                                 var4 *= 0.75F;
                              } else if (var16 < 0.06F) {
                                 var4 *= 0.8F;
                              } else if (var16 >= 0.06F) {
                                 var4 *= 2.4F;
                              }

                              if (this.eatBodyTarget != null) {
                                 var4 *= 0.6F;
                              }

                              if (var23 < 5.0F && (!var3.isRunning() && !var3.isSneaking() && !var3.isAiming() || var3.isRunning())) {
                                 var4 *= 3.0F;
                              }

                              if (this.spottedLast == var1 && this.TimeSinceSeenFlesh < 120.0F) {
                                 var4 = 1000.0F;
                              }

                              var4 *= var3.getSneakSpotMod();
                              var4 *= var7;
                              if (this.target != var1 && this.target != null) {
                                 float var17 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), var1.getX(), var1.getY());
                                 float var18 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), this.target.getX(), this.target.getY());
                                 if (var17 > var18) {
                                    return;
                                 }
                              }

                              var4 *= 0.3F;
                              if (var2) {
                                 var4 = 1000000.0F;
                              }

                              if (this.BonusSpotTime > 0.0F) {
                                 var4 = 1000000.0F;
                              }

                              var4 *= 1.2F;
                              if (SandboxOptions.instance.Lore.Sight.getValue() == 1) {
                                 var4 *= 2.5F;
                              }

                              if (SandboxOptions.instance.Lore.Sight.getValue() == 3) {
                                 var4 *= 0.45F;
                              }

                              if (this.inactive) {
                                 var4 *= 0.25F;
                              }

                              var4 *= 0.25F;
                              if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).Traits.Inconspicuous.isSet()) {
                                 var4 *= 0.5F;
                              }

                              if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).Traits.Conspicuous.isSet()) {
                                 var4 *= 2.0F;
                              }

                              var4 *= 1.6F;
                              IsoGridSquare var25 = null;
                              IsoGridSquare var26 = null;
                              float var21;
                              if (this.getCurrentSquare() != var1.getCurrentSquare() && var1 instanceof IsoPlayer && ((IsoPlayer)var1).isSneaking()) {
                                 int var19 = Math.abs(this.getCurrentSquare().getX() - var1.getCurrentSquare().getX());
                                 int var20 = Math.abs(this.getCurrentSquare().getY() - var1.getCurrentSquare().getY());
                                 if (var19 > var20) {
                                    if (this.getCurrentSquare().getX() - var1.getCurrentSquare().getX() > 0) {
                                       var25 = var1.getCurrentSquare().nav[IsoDirections.E.index()];
                                    } else {
                                       var25 = var1.getCurrentSquare();
                                       var26 = var1.getCurrentSquare().nav[IsoDirections.W.index()];
                                    }
                                 } else if (this.getCurrentSquare().getY() - var1.getCurrentSquare().getY() > 0) {
                                    var25 = var1.getCurrentSquare().nav[IsoDirections.S.index()];
                                 } else {
                                    var25 = var1.getCurrentSquare();
                                    var26 = var1.getCurrentSquare().nav[IsoDirections.N.index()];
                                 }

                                 if (var25 != null && var1 instanceof IsoGameCharacter) {
                                    var21 = ((IsoGameCharacter)var1).checkIsNearWall();
                                    if (var21 == 1.0F && var26 != null) {
                                       var21 = var26.getGridSneakModifier(true);
                                    }

                                    if (var21 > 1.0F) {
                                       float var22 = var1.DistTo(var25.x, var25.y);
                                       if (var22 > 1.0F) {
                                          var21 /= var22;
                                       }

                                       var4 /= var21;
                                    }
                                 }
                              }

                              var4 = (float)Math.floor((double)var4);
                              boolean var27 = false;
                              var4 = Math.min(var4, 400.0F);
                              var4 /= 400.0F;
                              var4 = Math.max(0.0F, var4);
                              var4 = Math.min(1.0F, var4);
                              float var28 = GameTime.instance.getMultiplier();
                              var4 = (float)(1.0D - Math.pow((double)(1.0F - var4), (double)var28));
                              var4 *= 100.0F;
                              if ((float)Rand.Next(10000) / 100.0F < var4) {
                                 var27 = true;
                              }

                              if (!var27) {
                                 if (var4 > 20.0F && var1 instanceof IsoPlayer && var23 < 15.0F) {
                                    ((IsoPlayer)var1).bCouldBeSeenThisFrame = true;
                                 }

                                 if (!((IsoPlayer)var1).isbCouldBeSeenThisFrame() && !((IsoPlayer)var1).isbSeenThisFrame() && ((IsoPlayer)var1).isSneaking() && ((IsoPlayer)var1).JustMoved && Rand.Next((int)(1100.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                    if (GameServer.bServer) {
                                       GameServer.addXp((IsoPlayer)var1, PerkFactory.Perks.Sneak, 1);
                                    } else {
                                       ((IsoPlayer)var1).getXp().AddXP(PerkFactory.Perks.Sneak, 1.0F);
                                    }
                                 }

                                 if (!((IsoPlayer)var1).isbCouldBeSeenThisFrame() && !((IsoPlayer)var1).isbSeenThisFrame() && ((IsoPlayer)var1).isSneaking() && ((IsoPlayer)var1).JustMoved && Rand.Next((int)(1100.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                    if (GameServer.bServer) {
                                       GameServer.addXp((IsoPlayer)var1, PerkFactory.Perks.Lightfoot, 1);
                                    } else {
                                       ((IsoPlayer)var1).getXp().AddXP(PerkFactory.Perks.Lightfoot, 1.0F);
                                    }
                                 }

                              } else {
                                 if (var1 instanceof IsoPlayer) {
                                    ((IsoPlayer)var1).setbSeenThisFrame(true);
                                 }

                                 if (!var2) {
                                    this.BonusSpotTime = 120.0F;
                                 }

                                 this.LastTargetSeenX = (int)var1.getX();
                                 this.LastTargetSeenY = (int)var1.getY();
                                 this.LastTargetSeenZ = (int)var1.getZ();
                                 if (this.stateMachine.getCurrent() != StaggerBackState.instance()) {
                                    if (this.target != var1) {
                                       this.targetSeenTime = 0.0F;
                                    }

                                    this.setTarget(var1);
                                    this.vectorToTarget.x = var1.getX();
                                    this.vectorToTarget.y = var1.getY();
                                    var10000 = this.vectorToTarget;
                                    var10000.x -= this.getX();
                                    var10000 = this.vectorToTarget;
                                    var10000.y -= this.getY();
                                    var21 = this.vectorToTarget.getLength();
                                    if (!var2) {
                                       this.TimeSinceSeenFlesh = 0.0F;
                                       this.targetSeenTime += GameTime.getInstance().getRealworldSecondsSinceLastUpdate();
                                    }

                                    if (this.target != this.spottedLast || this.getCurrentState() != LungeState.instance() || !(this.LungeTimer > 0.0F)) {
                                       if (this.target != this.spottedLast || this.getCurrentState() != AttackVehicleState.instance()) {
                                          if ((int)this.getZ() == (int)this.target.getZ() && (var21 <= 3.5F || this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null && var21 <= 4.0F) && this.getStateEventDelayTimer() <= 0.0F && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), var1.x, var1.y, (int)this.getZ(), var1)) {
                                             this.setTarget(var1);
                                             if (this.getCurrentState() == LungeState.instance()) {
                                                return;
                                             }
                                          }

                                          this.spottedLast = var1;
                                          if (!this.Ghost && !this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke)) {
                                             this.setTarget(var1);
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

                                                BaseVehicle var29 = ((IsoGameCharacter)this.target).getVehicle();
                                                if (Math.abs(var29.getCurrentSpeedKmHour()) > 0.1F && this.DistToSquared(var29) <= 16.0F) {
                                                   return;
                                                }

                                                this.pathToCharacter((IsoGameCharacter)this.target);
                                                this.AllowRepathDelay = 10.0F;
                                                return;
                                             }

                                             this.pathToCharacter(var3);
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

   public void Move(Vector2 var1) {
      if (!GameClient.bClient) {
         this.nx += var1.x * GameTime.instance.getMultiplier();
         this.ny += var1.y * GameTime.instance.getMultiplier();
         this.movex = var1.x;
         this.movey = var1.y;
      }
   }

   public void MoveUnmodded(Vector2 var1) {
      if (this.speedType == 1 && (this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(AttackState.instance()) || this.isCurrentState(AttackNetworkState.instance()) || this.isCurrentState(StaggerBackState.instance()) || this.isCurrentState(ZombieHitReactionState.instance())) && this.target instanceof IsoGameCharacter) {
         float var2 = this.target.nx - this.x;
         float var3 = this.target.ny - this.y;
         float var4 = (float)Math.sqrt((double)(var2 * var2 + var3 * var3));
         var4 -= this.getWidth() + this.target.getWidth() - 0.1F;
         var4 = Math.max(0.0F, var4);
         if (var1.getLength() > var4) {
            var1.setLength(var4);
         }
      }

      super.MoveUnmodded(var1);
   }

   public void DoFootstepSound(String var1) {
      ParameterCharacterMovementSpeed.MovementType var2 = ParameterCharacterMovementSpeed.MovementType.Walk;
      float var3 = 0.5F;
      byte var5 = -1;
      switch(var1.hashCode()) {
      case -940878112:
         if (var1.equals("sneak_run")) {
            var5 = 1;
         }
         break;
      case -895679974:
         if (var1.equals("sprint")) {
            var5 = 5;
         }
         break;
      case -891993841:
         if (var1.equals("strafe")) {
            var5 = 2;
         }
         break;
      case 113291:
         if (var1.equals("run")) {
            var5 = 4;
         }
         break;
      case 3641801:
         if (var1.equals("walk")) {
            var5 = 3;
         }
         break;
      case 897679380:
         if (var1.equals("sneak_walk")) {
            var5 = 0;
         }
      }

      switch(var5) {
      case 0:
         var3 = 0.25F;
         var2 = ParameterCharacterMovementSpeed.MovementType.SneakWalk;
         break;
      case 1:
         var3 = 0.25F;
         var2 = ParameterCharacterMovementSpeed.MovementType.SneakRun;
         break;
      case 2:
         var3 = 0.5F;
         var2 = ParameterCharacterMovementSpeed.MovementType.Strafe;
         break;
      case 3:
         var3 = 0.5F;
         var2 = ParameterCharacterMovementSpeed.MovementType.Walk;
         break;
      case 4:
         var3 = 0.75F;
         var2 = ParameterCharacterMovementSpeed.MovementType.Run;
         break;
      case 5:
         var3 = 1.0F;
         var2 = ParameterCharacterMovementSpeed.MovementType.Sprint;
      }

      if (!GameServer.bServer && !this.getFMODParameters().parameterList.contains(this.parameterCharacterMovementSpeed)) {
         this.getFMODParameters().add(this.parameterCharacterMovementSpeed);
         this.getFMODParameters().add(this.parameterFootstepMaterial);
         this.getFMODParameters().add(this.parameterFootstepMaterial2);
         this.getFMODParameters().add(this.parameterShoeType);
      }

      this.parameterCharacterMovementSpeed.setMovementType(var2);
      this.DoFootstepSound(var3);
   }

   public void DoFootstepSound(float var1) {
      if (!GameServer.bServer) {
         if (!(var1 <= 0.0F)) {
            if (this.getCurrentSquare() != null) {
               if (!GameClient.bClient) {
                  boolean var6 = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 15.0F);
                  if (var6) {
                     this.footstepVolume = var1;
                     ZombieFootstepManager.instance.addCharacter(this);
                  }

               } else {
                  if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
                     int var2 = (int)this.def.Frame;
                     boolean var3;
                     if (var2 >= 0 && var2 < 5) {
                        var3 = this.stepFrameLast < 0 || this.stepFrameLast > 5;
                     } else {
                        var3 = this.stepFrameLast < 5;
                     }

                     if (var3) {
                        for(int var4 = 0; var4 < IsoPlayer.numPlayers; ++var4) {
                           IsoPlayer var5 = IsoPlayer.players[var4];
                           if (var5 != null && var5.DistToSquared(this) < 225.0F) {
                              ZombieFootstepManager.instance.addCharacter(this);
                              break;
                           }
                        }
                     }

                     this.stepFrameLast = var2;
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
         IsoPlayer var1 = this.getReanimatedPlayer();
         if (var1 != null) {
            var1.setX(this.getX());
            var1.setY(this.getY());
            var1.setZ(this.getZ());
            var1.setDir(this.getDir());
            var1.setForwardDirection(this.getForwardDirection());
            AnimationPlayer var2 = this.getAnimationPlayer();
            AnimationPlayer var3 = var1.getAnimationPlayer();
            if (var2 != null && var2.isReady() && var3 != null && var3.isReady()) {
               var3.setTargetAngle(var2.getAngle());
               var3.setAngleToTarget();
            }

            var1.setCurrent(this.getCell().getGridSquare((int)var1.x, (int)var1.y, (int)var1.z));
            var1.updateLightInfo();
            if (var1.soundListener != null) {
               var1.soundListener.setPos(var1.getX(), var1.getY(), var1.getZ());
               var1.soundListener.tick();
            }

            IsoPlayer var4 = IsoPlayer.getInstance();
            IsoPlayer.setInstance(var1);
            var1.updateLOS();
            IsoPlayer.setInstance(var4);
            if (GameClient.bClient && this.networkUpdate.Check()) {
               GameClient.instance.sendPlayer(var1);
            }

            var1.dirtyRecalcGridStackTime = 2.0F;
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

   public boolean isPushedByForSeparate(IsoMovingObject var1) {
      if (var1 instanceof IsoZombie && ((IsoZombie)var1).getCurrentState() == ZombieHitReactionState.instance() && !((IsoZombie)var1).collideWhileHit) {
         return false;
      } else {
         return this.getCurrentState() == ZombieHitReactionState.instance() && !this.collideWhileHit ? false : super.isPushedByForSeparate(var1);
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

      BaseVehicle var1 = this.getNearVehicle();
      if (var1 != null && this.target == null && var1.hasLightbar() && var1.lightbarSirenMode.get() > 0) {
         VehiclePart var2 = var1.getUseablePart(this, false);
         if (var2 != null && var2.getSquare().DistTo((IsoMovingObject)this) < 0.7F) {
            this.setThumpTarget(var1);
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
            Vector2 var10;
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
                     String var7 = this.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
                     if (!this.getEmitter().isPlaying(var7)) {
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
                           var10 = this.vectorToTarget;
                           var10.x -= this.getX();
                           var10 = this.vectorToTarget;
                           var10.y -= this.getY();
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
                     IsoPlayer var6 = null;
                     float var8 = 1000000.0F;

                     for(int var9 = 0; var9 < IsoPlayer.numPlayers; ++var9) {
                        if (IsoPlayer.players[var9] != null && IsoPlayer.players[var9].DistTo(this) < var8 && !IsoPlayer.players[var9].isDead()) {
                           var8 = IsoPlayer.players[var9].DistTo(this);
                           var6 = IsoPlayer.players[var9];
                        }
                     }

                     if (var6 != null) {
                        this.AllowRepathDelay = -1.0F;
                        this.pathToCharacter(var6);
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
                           State var5 = this.stateMachine.getCurrent();
                           if (var5 != StaggerBackState.instance() && var5 != BurntToDeath.instance() && var5 != FakeDeadZombieState.instance() && var5 != ZombieFallDownState.instance() && var5 != ZombieOnGroundState.instance() && var5 != ZombieHitReactionState.instance() && var5 != ZombieGetUpState.instance()) {
                              if (GameServer.bServer && this.OnlineID == -1) {
                                 this.OnlineID = ServerMap.instance.getUniqueZombieId();
                              } else {
                                 IsoSpriteInstance var10000;
                                 if (var5 == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
                                    if (this.bCrawling) {
                                       this.PlayAnim("ZombieCrawl");
                                       this.def.AnimFrameIncrease = 0.0F;
                                    } else {
                                       this.PlayAnim("ZombieIdle");
                                       this.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
                                       var10000 = this.def;
                                       var10000.AnimFrameIncrease *= 0.5F;
                                    }
                                 } else if (var5 != AttackState.instance() && var5 != AttackVehicleState.instance() && (this.nx != this.x || this.ny != this.y)) {
                                    if (this.walkVariantUse == null || var5 != LungeState.instance() && var5 != LungeNetworkState.instance()) {
                                       this.walkVariantUse = this.walkVariant;
                                    }

                                    if (this.bCrawling) {
                                       this.walkVariantUse = "ZombieCrawl";
                                    }

                                    if (var5 != ZombieIdleState.instance() && var5 != StaggerBackState.instance() && var5 != ThumpState.instance() && var5 != FakeDeadZombieState.instance()) {
                                       if (this.bRunning) {
                                          this.PlayAnim("Run");
                                          this.def.setFrameSpeedPerFrame(0.33F);
                                       } else {
                                          this.PlayAnim(this.walkVariantUse);
                                          this.def.setFrameSpeedPerFrame(0.26F);
                                          var10000 = this.def;
                                          var10000.AnimFrameIncrease *= this.speedMod;
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
                              int var3 = this.getSandboxMemoryDuration();
                              if (this.TimeSinceSeenFlesh > (float)var3 && this.target != null) {
                                 this.setTarget((IsoMovingObject)null);
                              }

                              if (this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).ReanimatedCorpse != null) {
                                 this.setTarget((IsoMovingObject)null);
                              }

                              if (this.target != null) {
                                 this.vectorToTarget.x = this.target.getX();
                                 this.vectorToTarget.y = this.target.getY();
                                 var10 = this.vectorToTarget;
                                 var10.x -= this.getX();
                                 var10 = this.vectorToTarget;
                                 var10.y -= this.getY();
                                 this.updateZombieTripping();
                              }

                              if (IsoPlayer.getInstance() != null) {
                                 this.nextIdleSound -= GameTime.getInstance().getMultiplier() / 1.6F;
                                 if (this.nextIdleSound < 0.0F && (this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState.instance())) {
                                    this.nextIdleSound = (float)Rand.Next(300, 600);
                                    if (SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0F)) {
                                       String var4 = this.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
                                       if (!this.emitter.isPlaying(var4)) {
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

   void doHit(BaseVehicle var1) {
      if (GameServer.bServer || GameClient.bClient && !var1.isDriver(IsoPlayer.getInstance())) {
         if (this.networkAI.hitVehicle != null && var1.getId() == this.networkAI.hitVehicle.vehicleID) {
            this.networkAI.hitVehicle.process();
            this.networkAI.hitVehicle = null;
         }
      } else {
         var1.hitCharacter(this);
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
         boolean var1 = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0F);
         if (this.vocalEvent == 0L && !this.isDead() && !this.isFakeDead() && var1) {
            String var2 = this.isFemale() ? "FemaleZombieCombined" : "MaleZombieCombined";
            if (!this.getFMODParameters().parameterList.contains(this.parameterZombieState)) {
               this.parameterZombieState.update();
               this.getFMODParameters().add(this.parameterZombieState);
               this.parameterCharacterInside.update();
               this.getFMODParameters().add(this.parameterCharacterInside);
               this.parameterPlayerDistance.update();
               this.getFMODParameters().add(this.parameterPlayerDistance);
            }

            this.vocalEvent = this.getEmitter().playVocals(var2);
         }

         if (this.vocalEvent != 0L && !this.isDead() && this.isFakeDead() && this.getEmitter().isPlaying(this.vocalEvent)) {
            this.getEmitter().stopSound(this.vocalEvent);
            this.vocalEvent = 0L;
         }

      }
   }

   public void setVehicleHitLocation(BaseVehicle var1) {
      if (!this.getFMODParameters().parameterList.contains(this.parameterVehicleHitLocation)) {
         this.getFMODParameters().add(this.parameterVehicleHitLocation);
      }

      ParameterVehicleHitLocation.HitLocation var2 = ParameterVehicleHitLocation.calculateLocation(var1, this.getX(), this.getY(), this.getZ());
      this.parameterVehicleHitLocation.setLocation(var2);
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

               for(int var1 = -10; var1 < 10; ++var1) {
                  for(int var2 = -10; var2 < 10; ++var2) {
                     IsoGridSquare var3 = this.getCell().getGridSquare((double)(this.getX() + (float)var1), (double)(this.getY() + (float)var2), (double)this.getZ());
                     if (var3 != null) {
                        IsoDeadBody var4 = var3.getDeadBody();
                        if (var4 != null && !var4.isSkeleton() && !var4.isZombie() && var4.getEatingZombies().size() < 3 && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), var4.x, var4.y, (int)this.getZ(), (IsoMovingObject)null, false, true)) {
                           tempBodies.add(var4);
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
               Vector2 var5 = tempo.set(this.x - this.bodyToEat.x, this.y - this.bodyToEat.y);
               var5.setLength(0.5F);
               this.pathToLocationF(this.bodyToEat.getX() + var5.x, this.bodyToEat.getY() + var5.y, this.bodyToEat.getZ());
            }

         }
      } else {
         this.checkForCorpseTimer = 10000.0F;
         this.bodyToEat = null;
      }
   }

   private void damageSheetRope() {
      if (Rand.Next(30) == 0 && this.current != null && (this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetE) || this.current.Is(IsoFlagType.climbSheetS) || this.current.Is(IsoFlagType.climbSheetW))) {
         IsoObject var1 = this.current.getSheetRope();
         if (var1 != null) {
            var1.sheetRopeHealth -= (float)Rand.Next(5, 15);
            if (var1.sheetRopeHealth < 40.0F) {
               this.current.damageSpriteSheetRopeFromBottom((IsoPlayer)null, this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetS));
               this.current.RecalcProperties();
            }

            if (var1.sheetRopeHealth <= 0.0F) {
               this.current.removeSheetRopeFromBottom((IsoPlayer)null, this.current.Is(IsoFlagType.climbSheetN) || this.current.Is(IsoFlagType.climbSheetS));
            }
         }
      }

   }

   public void getZombieWalkTowardSpeed(float var1, float var2, Vector2 var3) {
      float var4 = 1.0F;
      var4 = var2 / 24.0F;
      if (var4 < 1.0F) {
         var4 = 1.0F;
      }

      if (var4 > 1.3F) {
         var4 = 1.3F;
      }

      var3.setLength((var1 * this.getSpeedMod() + 0.006F) * var4);
      if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
         var3.setLength(0.08F);
         this.bRunning = true;
      }

      if (var3.getLength() > var2) {
         var3.setLength(var2);
      }

   }

   public void getZombieLungeSpeed() {
      this.bRunning = false;
      if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
         this.bRunning = true;
      }

   }

   public boolean tryThump(IsoGridSquare var1) {
      if (this.Ghost) {
         return false;
      } else if (this.bCrawling) {
         return false;
      } else {
         boolean var2 = this.isCurrentState(PathFindState.instance()) || this.isCurrentState(LungeState.instance()) || this.isCurrentState(LungeNetworkState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance());
         if (!var2) {
            return false;
         } else {
            IsoGridSquare var3;
            if (var1 != null) {
               var3 = var1;
            } else {
               var3 = this.getFeelerTile(this.getFeelersize());
            }

            if (var3 != null && this.current != null) {
               IsoObject var4 = this.current.testCollideSpecialObjects(var3);
               IsoDoor var5 = (IsoDoor)Type.tryCastTo(var4, IsoDoor.class);
               IsoThumpable var6 = (IsoThumpable)Type.tryCastTo(var4, IsoThumpable.class);
               IsoWindow var7 = (IsoWindow)Type.tryCastTo(var4, IsoWindow.class);
               if (var7 != null && var7.canClimbThrough(this)) {
                  if (!this.isFacingObject(var7, 0.8F)) {
                     return false;
                  } else {
                     this.climbThroughWindow(var7);
                     return true;
                  }
               } else if (var6 != null && var6.canClimbThrough(this)) {
                  this.climbThroughWindow(var6);
                  return true;
               } else if (var6 != null && var6.getThumpableFor(this) != null || var7 != null && var7.getThumpableFor(this) != null || var5 != null && var5.getThumpableFor(this) != null) {
                  int var8 = var3.getX() - this.current.getX();
                  int var9 = var3.getY() - this.current.getY();
                  IsoDirections var10 = IsoDirections.N;
                  if (var8 < 0 && Math.abs(var8) > Math.abs(var9)) {
                     var10 = IsoDirections.S;
                  }

                  if (var8 < 0 && Math.abs(var8) <= Math.abs(var9)) {
                     var10 = IsoDirections.SW;
                  }

                  if (var8 > 0 && Math.abs(var8) > Math.abs(var9)) {
                     var10 = IsoDirections.W;
                  }

                  if (var8 > 0 && Math.abs(var8) <= Math.abs(var9)) {
                     var10 = IsoDirections.SE;
                  }

                  if (var9 < 0 && Math.abs(var8) < Math.abs(var9)) {
                     var10 = IsoDirections.N;
                  }

                  if (var9 < 0 && Math.abs(var8) >= Math.abs(var9)) {
                     var10 = IsoDirections.NW;
                  }

                  if (var9 > 0 && Math.abs(var8) < Math.abs(var9)) {
                     var10 = IsoDirections.E;
                  }

                  if (var9 > 0 && Math.abs(var8) >= Math.abs(var9)) {
                     var10 = IsoDirections.NE;
                  }

                  if (this.getDir() == var10) {
                     boolean var11 = this.getPathFindBehavior2().isGoalSound() && (this.isCurrentState(PathFindState.instance()) || this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(WalkTowardNetworkState.instance()));
                     if (SandboxOptions.instance.Lore.ThumpNoChasing.getValue() || this.target != null || var11) {
                        if (var7 != null && var7.getThumpableFor(this) != null) {
                           var4 = (IsoObject)var7.getThumpableFor(this);
                        }

                        this.setThumpTarget(var4);
                        this.setPath2((PolygonalMap2.Path)null);
                     }
                  }

                  return true;
               } else if (var4 != null && IsoWindowFrame.isWindowFrame(var4)) {
                  this.climbThroughWindowFrame(var4);
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

         InventoryItem var3;
         for(int var1 = 0; var1 < this.attachedItems.size(); ++var1) {
            AttachedItem var2 = this.attachedItems.get(var1);
            var3 = var2.getItem();
            if (!this.getInventory().contains(var3)) {
               var3.setContainer(this.getInventory());
               this.getInventory().getItems().add(var3);
            }
         }

         if (!GameClient.bClient) {
            IsoBuilding var4 = this.getCurrentBuilding();
            if (var4 != null && var4.getDef() != null && var4.getDef().getKeyId() != -1 && Rand.Next(4) == 0) {
               int var10000 = Rand.Next(5);
               String var5 = "Base.Key" + (var10000 + 1);
               var3 = this.inventory.AddItem(var5);
               var3.setKeyId(var4.getDef().getKeyId());
            }

            if (this.itemsToSpawnAtDeath != null && !this.itemsToSpawnAtDeath.isEmpty()) {
               for(int var6 = 0; var6 < this.itemsToSpawnAtDeath.size(); ++var6) {
                  this.inventory.AddItem((InventoryItem)this.itemsToSpawnAtDeath.get(var6));
               }

               this.itemsToSpawnAtDeath.clear();
            }
         }

      }
   }

   public void changeSpeed(int var1) {
      this.walkVariant = "ZombieWalk";
      this.speedType = var1;
      IsoSpriteInstance var10000;
      if (this.speedType == 3) {
         this.speedMod = 0.55F;
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         this.walkVariant = this.walkVariant + "1";
         this.def.setFrameSpeedPerFrame(0.24F);
         var10000 = this.def;
         var10000.AnimFrameIncrease *= this.speedMod;
      } else {
         this.bLunger = true;
         this.speedMod = 0.85F;
         this.walkVariant = this.walkVariant + "2";
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         this.def.setFrameSpeedPerFrame(0.24F);
         var10000 = this.def;
         var10000.AnimFrameIncrease *= this.speedMod;
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

      IsoSpriteInstance var10000;
      if (this.bCrawling) {
         this.speedMod = 0.3F;
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         var10000 = this.def;
         var10000.AnimFrameIncrease *= 0.8F;
      } else if (SandboxOptions.instance.Lore.Speed.getValue() != 3 && this.speedType != 3 && Rand.Next(3) == 0) {
         if (SandboxOptions.instance.Lore.Speed.getValue() != 3 || this.speedType != 3) {
            this.bLunger = true;
            this.speedMod = 0.85F;
            this.walkVariant = this.walkVariant + "2";
            this.speedMod += (float)Rand.Next(1500) / 10000.0F;
            this.def.setFrameSpeedPerFrame(0.24F);
            var10000 = this.def;
            var10000.AnimFrameIncrease *= this.speedMod;
         }
      } else {
         this.speedMod = 0.55F;
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         this.walkVariant = this.walkVariant + "1";
         this.def.setFrameSpeedPerFrame(0.24F);
         var10000 = this.def;
         var10000.AnimFrameIncrease *= this.speedMod;
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

   public void setWalkType(String var1) {
      this.walkType = var1;
   }

   public void DoZombieSpeeds(float var1) {
      IsoSpriteInstance var10000;
      if (this.bCrawling) {
         this.speedMod = var1;
         var10000 = this.def;
         var10000.AnimFrameIncrease *= 0.8F;
      } else if (Rand.Next(3) == 0 && SandboxOptions.instance.Lore.Speed.getValue() != 3) {
         if (SandboxOptions.instance.Lore.Speed.getValue() != 3) {
            this.bLunger = true;
            this.speedMod = var1;
            this.walkVariant = this.walkVariant + "2";
            this.def.setFrameSpeedPerFrame(0.24F);
            var10000 = this.def;
            var10000.AnimFrameIncrease *= this.speedMod;
         }
      } else {
         this.speedMod = var1;
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         this.walkVariant = this.walkVariant + "1";
         this.def.setFrameSpeedPerFrame(0.24F);
         var10000 = this.def;
         var10000.AnimFrameIncrease *= this.speedMod;
      }

   }

   public boolean isFakeDead() {
      return this.bFakeDead;
   }

   public void setFakeDead(boolean var1) {
      if (var1 && Rand.Next(2) == 0) {
         this.setCrawlerType(2);
      }

      this.bFakeDead = var1;
   }

   public boolean isForceFakeDead() {
      return this.bForceFakeDead;
   }

   public void setForceFakeDead(boolean var1) {
      this.bForceFakeDead = var1;
   }

   public void HitSilence(HandWeapon var1, IsoZombie var2, float var3, boolean var4, float var5) {
      super.HitSilence(var1, var2, var3, var4, var5);
      this.setTarget(var2);
      if (this.Health <= 0.0F && !this.isOnDeathDone()) {
         this.DoZombieInventory();
         this.setOnDeathDone(true);
      }

      this.TimeSinceSeenFlesh = 0.0F;
   }

   protected void DoDeathSilence(HandWeapon var1, IsoGameCharacter var2) {
      if (this.Health <= 0.0F && !this.isOnDeathDone()) {
         this.DoZombieInventory();
         this.setOnDeathDone(true);
      }

      super.DoDeathSilence(var1, var2);
   }

   public void Hit(BaseVehicle var1, float var2, float var3, Vector2 var4) {
      this.AttackedBy = var1.getDriver();
      this.setHitDir(var4);
      this.setHitForce(var2 * 0.15F);
      int var5 = (int)(var2 * 6.0F);
      this.setTarget(var1.getCharacter(0));
      if (!this.bStaggerBack && !this.isOnFloor() && this.getCurrentState() != ZombieGetUpState.instance() && this.getCurrentState() != ZombieOnGroundState.instance()) {
         boolean var6 = this.bStaggerBack;
         boolean var7 = this.bKnockedDown;
         boolean var8 = this.isBecomeCrawler();
         if (var3 > 0.0F) {
            this.setHitFromBehind(true);
            if (Rand.Next(100) <= var5) {
               if (Rand.Next(5) == 0) {
                  var8 = true;
               }

               var6 = true;
               var7 = true;
            } else {
               var6 = true;
            }
         } else if (var2 < 3.0F) {
            if (Rand.Next(100) <= var5) {
               if (Rand.Next(8) == 0) {
                  var8 = true;
               }

               var6 = true;
               var7 = true;
            } else {
               var6 = true;
            }
         } else if (var2 < 10.0F) {
            if (Rand.Next(8) == 0) {
               var8 = true;
            }

            var6 = true;
            var7 = true;
         } else if (!GameServer.bServer && (!GameClient.bClient || var1.isDriver(IsoPlayer.getInstance()))) {
            this.Health = -1.0F;
            this.checkDead(var1.getDriver(), true);
         }

         if (!GameServer.bServer) {
            this.bStaggerBack = var6;
            this.bKnockedDown = var7;
            this.bBecomeCrawler = var8;
         }
      } else {
         if (this.isFakeDead()) {
            this.setFakeDead(false);
         }

         this.setHitReaction("Floor");
         if (!GameServer.bServer && (!GameClient.bClient || var1.isDriver(IsoPlayer.getInstance()))) {
            this.Health -= var2 / 5.0F;
            this.checkDead(var1.getDriver(), true);
         }
      }

      if ((GameServer.bServer || GameClient.bClient) && this.isDead() && this.AttackedBy != null) {
         this.lastPlayerHit = this.AttackedBy.getOnlineID() | DeadBodyPacket.DIED_UNDER_VEHICLE;
      }

      this.addBlood(var2);
   }

   public void addBlood(float var1) {
      if (!((float)Rand.Next(10) > var1)) {
         float var2 = 0.6F;
         if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            int var3 = Rand.Next(4, 10);
            if (var3 < 1) {
               var3 = 1;
            }

            if (Core.bLastStand) {
               var3 *= 3;
            }

            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 2:
               var3 /= 2;
            case 3:
            default:
               break;
            case 4:
               var3 *= 2;
               break;
            case 5:
               var3 *= 5;
            }

            for(int var4 = 0; var4 < var3; ++var4) {
               this.splatBlood(2, 0.3F);
            }
         }

         if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            this.splatBloodFloorBig(0.3F);
         }

         if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            this.playBloodSplatterSound();
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
            tempo.x = this.getHitDir().x;
            tempo.y = this.getHitDir().y;
            byte var8 = 3;
            byte var7 = 0;
            byte var5 = 1;
            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 1:
               var5 = 0;
               break;
            case 2:
               var5 = 1;
               var8 = 5;
               var7 = 2;
            case 3:
            default:
               break;
            case 4:
               var5 = 3;
               var8 = 2;
               break;
            case 5:
               var5 = 10;
               var8 = 0;
            }

            for(int var6 = 0; var6 < var5; ++var6) {
               if (Rand.Next(this.isCloseKilled() ? 8 : var8) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var8) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 1.8F, this.getHitDir().y * 1.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var8) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 1.9F, this.getHitDir().y * 1.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var7) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var7) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + var2, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
               }
            }
         }

      }
   }

   private void processHitDirection(HandWeapon var1, IsoGameCharacter var2) {
      String var3 = var2.getVariableString("ZombieHitReaction");
      if ("Shot".equals(var3)) {
         var3 = "ShotBelly";
         var2.isCrit = Rand.Next(100) < ((IsoPlayer)var2).calculateCritChance(this);
         Vector2 var4 = var2.getForwardDirection();
         Vector2 var5 = this.getHitAngle();
         double var6 = (double)(var4.x * var5.y - var4.y * var5.x);
         double var8 = var6 >= 0.0D ? 1.0D : -1.0D;
         double var10 = (double)(var4.x * var5.x + var4.y * var5.y);
         double var12 = Math.acos(var10) * var8;
         if (var12 < 0.0D) {
            var12 += 6.283185307179586D;
         }

         String var14 = "";
         int var15;
         if (Math.toDegrees(var12) < 45.0D) {
            this.setHitFromBehind(true);
            var14 = "S";
            var15 = Rand.Next(9);
            if (var15 > 6) {
               var14 = "L";
            }

            if (var15 > 4) {
               var14 = "R";
            }
         }

         if (Math.toDegrees(var12) > 45.0D && Math.toDegrees(var12) < 90.0D) {
            this.setHitFromBehind(true);
            if (Rand.Next(4) == 0) {
               var14 = "S";
            } else {
               var14 = "R";
            }
         }

         if (Math.toDegrees(var12) > 90.0D && Math.toDegrees(var12) < 135.0D) {
            var14 = "R";
         }

         if (Math.toDegrees(var12) > 135.0D && Math.toDegrees(var12) < 180.0D) {
            if (Rand.Next(4) == 0) {
               var14 = "N";
            } else {
               var14 = "R";
            }
         }

         if (Math.toDegrees(var12) > 180.0D && Math.toDegrees(var12) < 225.0D) {
            var14 = "N";
            var15 = Rand.Next(9);
            if (var15 > 6) {
               var14 = "L";
            }

            if (var15 > 4) {
               var14 = "R";
            }
         }

         if (Math.toDegrees(var12) > 225.0D && Math.toDegrees(var12) < 270.0D) {
            if (Rand.Next(4) == 0) {
               var14 = "N";
            } else {
               var14 = "L";
            }
         }

         if (Math.toDegrees(var12) > 270.0D && Math.toDegrees(var12) < 315.0D) {
            this.setHitFromBehind(true);
            var14 = "L";
         }

         if (Math.toDegrees(var12) > 315.0D) {
            if (Rand.Next(4) == 0) {
               var14 = "S";
            } else {
               var14 = "L";
            }
         }

         if ("N".equals(var14)) {
            if (this.isHitFromBehind()) {
               var3 = "ShotBellyStep";
            } else {
               var15 = Rand.Next(2);
               switch(var15) {
               case 0:
                  var3 = "ShotBelly";
                  break;
               case 1:
                  var3 = "ShotBellyStep";
               }
            }
         }

         if ("S".equals(var14)) {
            var3 = "ShotBellyStep";
         }

         if ("L".equals(var14) || "R".equals(var14)) {
            if (this.isHitFromBehind()) {
               var15 = Rand.Next(3);
               switch(var15) {
               case 0:
                  var3 = "ShotChest";
                  break;
               case 1:
                  var3 = "ShotLeg";
                  break;
               case 2:
                  var3 = "ShotShoulderStep";
               }
            } else {
               var15 = Rand.Next(5);
               switch(var15) {
               case 0:
                  var3 = "ShotChest";
                  break;
               case 1:
                  var3 = "ShotChestStep";
                  break;
               case 2:
                  var3 = "ShotLeg";
                  break;
               case 3:
                  var3 = "ShotShoulder";
                  break;
               case 4:
                  var3 = "ShotShoulderStep";
               }
            }

            var3 = var3 + var14;
         }

         if (var2.isCrit) {
            if ("S".equals(var14)) {
               var3 = "ShotHeadFwd";
            }

            if ("N".equals(var14)) {
               var3 = "ShotHeadBwd";
            }

            if (("L".equals(var14) || "R".equals(var14)) && Rand.Next(4) == 0) {
               var3 = "ShotHeadBwd";
            }
         }

         if (var3.contains("Head")) {
            this.addBlood(BloodBodyPartType.Head, false, true, true);
         } else if (var3.contains("Chest")) {
            this.addBlood(BloodBodyPartType.Torso_Upper, !this.isCrit, this.isCrit, true);
         } else if (var3.contains("Belly")) {
            this.addBlood(BloodBodyPartType.Torso_Lower, !this.isCrit, this.isCrit, true);
         } else {
            boolean var16;
            if (var3.contains("Leg")) {
               var16 = Rand.Next(2) == 0;
               if ("L".equals(var14)) {
                  this.addBlood(var16 ? BloodBodyPartType.LowerLeg_L : BloodBodyPartType.UpperLeg_L, !this.isCrit, this.isCrit, true);
               } else {
                  this.addBlood(var16 ? BloodBodyPartType.LowerLeg_R : BloodBodyPartType.UpperLeg_R, !this.isCrit, this.isCrit, true);
               }
            } else if (var3.contains("Shoulder")) {
               var16 = Rand.Next(2) == 0;
               if ("L".equals(var14)) {
                  this.addBlood(var16 ? BloodBodyPartType.ForeArm_L : BloodBodyPartType.UpperArm_L, !this.isCrit, this.isCrit, true);
               } else {
                  this.addBlood(var16 ? BloodBodyPartType.ForeArm_R : BloodBodyPartType.UpperArm_R, !this.isCrit, this.isCrit, true);
               }
            }
         }
      } else if (var1.getCategories().contains("Blunt")) {
         this.addBlood(BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperArm_L.index(), BloodBodyPartType.Groin.index())), false, false, true);
      } else if (!var1.getCategories().contains("Unarmed")) {
         this.addBlood(BloodBodyPartType.FromIndex(Rand.Next(BloodBodyPartType.UpperArm_L.index(), BloodBodyPartType.Groin.index())), false, true, true);
      }

      if ("ShotHeadFwd".equals(var3) && Rand.Next(2) == 0) {
         var3 = "ShotHeadFwd02";
      }

      if (this.getEatBodyTarget() != null) {
         if (this.getVariableBoolean("onknees")) {
            var3 = "OnKnees";
         } else {
            var3 = "Eating";
         }
      }

      if (var3 != null && !"".equals(var3)) {
         this.setHitReaction(var3);
      } else {
         this.bStaggerBack = true;
         this.setHitReaction("");
         if ("LEFT".equals(this.getPlayerAttackPosition()) || "RIGHT".equals(this.getPlayerAttackPosition())) {
            var2.isCrit = false;
         }
      }

   }

   public void hitConsequences(HandWeapon var1, IsoGameCharacter var2, boolean var3, float var4, boolean var5) {
      if (!this.isOnlyJawStab() || this.isCloseKilled()) {
         if (this.shouldBecomeFakeDead(var2)) {
            this.setFakeDead(true);
            this.Health = 0.0F;
         }

         super.hitConsequences(var1, var2, var3, var4, var5);
         if (DebugLog.isEnabled(DebugType.Combat)) {
            DebugLog.Combat.debugln(this + " got hit for " + var4);
         }

         this.actionContext.reportEvent("wasHit");
         if (!var5) {
            this.processHitDirection(var1, var2);
         }

         this.setTarget(var2);
         if (!GameServer.bServer && !GameClient.bClient || GameClient.bClient && var2 instanceof IsoPlayer && ((IsoPlayer)var2).isLocalPlayer()) {
            this.bKnockedDown = var2.isCrit || this.isOnFloor() || this.isAlwaysKnockedDown();
         }

         this.checkClimbOverFenceHit();
         this.checkClimbThroughWindowHit();
         if (this.shouldBecomeCrawler(var2)) {
            this.setBecomeCrawler(true);
         }

      }
   }

   private void checkClimbOverFenceHit() {
      if (!this.isOnFloor()) {
         if (this.isCurrentState(ClimbOverFenceState.instance()) && this.getVariableBoolean("ClimbFenceStarted") && !this.isVariable("ClimbFenceOutcome", "fall") && !this.getVariableBoolean("ClimbFenceFlopped")) {
            HashMap var1 = (HashMap)this.StateMachineParams.get(ClimbOverFenceState.instance());
            byte var2 = 3;
            byte var3 = 4;
            int var4 = (Integer)var1.get(Integer.valueOf(var2));
            int var5 = (Integer)var1.get(Integer.valueOf(var3));
            this.climbFenceWindowHit(var4, var5);
         }
      }
   }

   private void checkClimbThroughWindowHit() {
      if (!this.isOnFloor()) {
         if (this.isCurrentState(ClimbThroughWindowState.instance()) && this.getVariableBoolean("ClimbWindowStarted") && !this.isVariable("ClimbWindowOutcome", "fall") && !this.getVariableBoolean("ClimbWindowFlopped")) {
            HashMap var1 = (HashMap)this.StateMachineParams.get(ClimbThroughWindowState.instance());
            byte var2 = 12;
            byte var3 = 13;
            int var4 = (Integer)var1.get(Integer.valueOf(var2));
            int var5 = (Integer)var1.get(Integer.valueOf(var3));
            this.climbFenceWindowHit(var4, var5);
         }
      }
   }

   private void climbFenceWindowHit(int var1, int var2) {
      if (this.getDir() == IsoDirections.W) {
         this.setX((float)var1 + 0.9F);
         this.setLx(this.getX());
      } else if (this.getDir() == IsoDirections.E) {
         this.setX((float)var1 + 0.1F);
         this.setLx(this.getX());
      } else if (this.getDir() == IsoDirections.N) {
         this.setY((float)var2 + 0.9F);
         this.setLy(this.getY());
      } else if (this.getDir() == IsoDirections.S) {
         this.setY((float)var2 + 0.1F);
         this.setLy(this.getY());
      }

      this.bStaggerBack = false;
      this.bKnockedDown = true;
      this.setOnFloor(true);
      this.setFallOnFront(true);
      this.setHitReaction("FenceWindow");
   }

   private boolean shouldBecomeFakeDead(IsoGameCharacter var1) {
      return false;
   }

   private boolean shouldBecomeCrawler(IsoGameCharacter var1) {
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
         IsoPlayer var2 = (IsoPlayer)Type.tryCastTo(var1, IsoPlayer.class);
         if (var2 != null && !var2.isAimAtFloor() && var2.bDoShove) {
            return false;
         } else {
            byte var3 = 30;
            if (var2 != null && var2.isAimAtFloor() && var2.bDoShove) {
               if (this.isHitLegsWhileOnFloor()) {
                  var3 = 7;
               } else {
                  var3 = 15;
               }
            }

            return Rand.NextBool(var3);
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

   public void setWasFakeDead(boolean var1) {
      this.bWasFakeDead = var1;
   }

   public boolean isBecomeCrawler() {
      return this.bBecomeCrawler;
   }

   public void setBecomeCrawler(boolean var1) {
      this.bBecomeCrawler = var1;
   }

   public boolean isReanimate() {
      return this.bReanimate;
   }

   public void setReanimate(boolean var1) {
      this.bReanimate = var1;
   }

   public boolean isReanimatedPlayer() {
      return this.bReanimatedPlayer;
   }

   public void setReanimatedPlayer(boolean var1) {
      this.bReanimatedPlayer = var1;
   }

   public IsoPlayer getReanimatedPlayer() {
      for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
         IsoPlayer var2 = IsoPlayer.players[var1];
         if (var2 != null && var2.ReanimatedCorpse == this) {
            return var2;
         }
      }

      return null;
   }

   public void setFemaleEtc(boolean var1) {
      this.setFemale(var1);
      if (this.getDescriptor() != null) {
         this.getDescriptor().setFemale(var1);
      }

      this.SpriteName = var1 ? "KateZ" : "BobZ";
      this.hurtSound = var1 ? "FemaleZombieHurt" : "MaleZombieHurt";
   }

   public void addRandomBloodDirtHolesEtc() {
      this.addBlood((BloodBodyPartType)null, false, true, false);
      this.addDirt((BloodBodyPartType)null, OutfitRNG.Next(5, 10), false);
      this.addRandomVisualDamages();
      this.addRandomVisualBandages();
      int var1 = Math.max(8 - (int)IsoWorld.instance.getWorldAgeDays() / 30, 0);

      int var2;
      for(var2 = 0; var2 < 5; ++var2) {
         if (OutfitRNG.NextBool(var1)) {
            this.addBlood((BloodBodyPartType)null, false, true, false);
            this.addDirt((BloodBodyPartType)null, (Integer)null, false);
         }
      }

      for(var2 = 0; var2 < 8; ++var2) {
         if (OutfitRNG.NextBool(var1)) {
            BloodBodyPartType var3 = BloodBodyPartType.FromIndex(OutfitRNG.Next(0, BloodBodyPartType.MAX.index()));
            this.addHole(var3);
            this.addBlood(var3, true, false, false);
         }
      }

   }

   public void useDescriptor(SharedDescriptors.Descriptor var1) {
      this.getHumanVisual().clear();
      this.itemVisuals.clear();
      this.m_persistentOutfitId = var1 == null ? 0 : var1.getPersistentOutfitID();
      this.m_bPersistentOutfitInit = true;
      this.sharedDesc = var1;
      if (var1 != null) {
         this.setFemaleEtc(var1.isFemale());
         this.getHumanVisual().copyFrom(var1.getHumanVisual());
         this.getWornItems().setFromItemVisuals(var1.itemVisuals);
         this.onWornItemsChanged();
      }
   }

   public SharedDescriptors.Descriptor getSharedDescriptor() {
      return this.sharedDesc;
   }

   public int getSharedDescriptorID() {
      return this.getPersistentOutfitID();
   }

   public int getScreenProperX(int var1) {
      return (int)(IsoUtils.XToScreen(this.x, this.y, this.z, 0) - IsoCamera.cameras[var1].getOffX());
   }

   public int getScreenProperY(int var1) {
      return (int)(IsoUtils.YToScreen(this.x, this.y, this.z, 0) - IsoCamera.cameras[var1].getOffY());
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

   public void getItemVisuals(ItemVisuals var1) {
      if (this.isUsingWornItems()) {
         this.getWornItems().getItemVisuals(var1);
      } else if (var1 != this.itemVisuals) {
         var1.clear();
         var1.addAll(this.itemVisuals);
      }

   }

   public boolean isUsingWornItems() {
      return this.isOnDeathDone() || this.isReanimatedPlayer() || this.wasFakeDead();
   }

   public void setAsSurvivor() {
      String var1 = "Survivalist";
      switch(Rand.Next(3)) {
      case 1:
         var1 = "Survivalist02";
         break;
      case 2:
         var1 = "Survivalist03";
      }

      this.dressInPersistentOutfit(var1);
   }

   public void dressInRandomOutfit() {
      ZombiesZoneDefinition.dressInRandomOutfit(this);
   }

   public void dressInNamedOutfit(String var1) {
      this.wornItems.clear();
      this.getHumanVisual().clear();
      this.itemVisuals.clear();
      Outfit var2 = this.isFemale() ? OutfitManager.instance.FindFemaleOutfit(var1) : OutfitManager.instance.FindMaleOutfit(var1);
      if (var2 != null) {
         if (var2.isEmpty()) {
            var2.loadItems();
            this.pendingOutfitName = var1;
         } else {
            this.getHumanVisual().dressInNamedOutfit(var1, this.itemVisuals);
            this.getHumanVisual().synchWithOutfit(this.getHumanVisual().getOutfit());
            this.onWornItemsChanged();
         }
      }
   }

   public void dressInPersistentOutfitID(int var1) {
      this.getHumanVisual().clear();
      this.itemVisuals.clear();
      this.m_persistentOutfitId = var1;
      this.m_bPersistentOutfitInit = true;
      if (var1 != 0) {
         this.bDressInRandomOutfit = false;
         PersistentOutfits.instance.dressInOutfit(this, var1);
         this.onWornItemsChanged();
      }
   }

   public void dressInClothingItem(String var1) {
      this.wornItems.clear();
      this.getHumanVisual().dressInClothingItem(var1, this.itemVisuals);
      this.onWornItemsChanged();
   }

   public void onWornItemsChanged() {
      this.parameterShoeType.setShoeType((ParameterShoeType.ShoeType)null);
   }

   public void clothingItemChanged(String var1) {
      super.clothingItemChanged(var1);
      if (!StringUtils.isNullOrWhitespace(this.pendingOutfitName)) {
         Outfit var2 = this.isFemale() ? OutfitManager.instance.FindFemaleOutfit(this.pendingOutfitName) : OutfitManager.instance.FindMaleOutfit(this.pendingOutfitName);
         if (var2 != null && !var2.isEmpty()) {
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
         IsoZombie.FloodFill var1 = floodFill;
         var1.calculate(this, this.getCurrentSquare());
         IsoGridSquare var2 = var1.choose();
         var1.reset();
         if (var2 != null) {
            this.pathToLocation(var2.getX(), var2.getY(), var2.getZ());
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isUseless() {
      return this.useless;
   }

   public void setUseless(boolean var1) {
      this.useless = var1;
   }

   public void setImmortalTutorialZombie(boolean var1) {
      this.ImmortalTutorialZombie = var1;
   }

   public boolean isTargetInCone(float var1, float var2) {
      if (this.target == null) {
         return false;
      } else {
         tempo.set(this.target.getX() - this.getX(), this.target.getY() - this.getY());
         float var3 = tempo.getLength();
         if (var3 == 0.0F) {
            return true;
         } else if (var3 > var1) {
            return false;
         } else {
            tempo.normalize();
            this.getVectorFromDirection(tempo2);
            float var4 = tempo.dot(tempo2);
            return var4 >= var2;
         }
      }
   }

   public boolean testCollideWithVehicles(BaseVehicle var1) {
      if (this.Health <= 0.0F) {
         return false;
      } else if (this.isProne()) {
         if (var1.getDriver() == null) {
            return false;
         } else {
            int var3 = var1.isEngineRunning() ? var1.testCollisionWithProneCharacter(this, true) : 0;
            if (var3 > 0) {
               if (!this.emitter.isPlaying(this.getHurtSound())) {
                  this.playHurtSound();
               }

               this.AttackedBy = var1.getDriver();
               this.doHit(var1);
               if (!GameClient.bClient) {
                  this.checkDead(var1.getDriver(), true);
               }

               super.update();
               return true;
            } else {
               return false;
            }
         }
      } else {
         if (var1.shouldCollideWithCharacters()) {
            Vector2 var2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
            if (var1.testCollisionWithCharacter(this, 0.3F, var2) != null) {
               ((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(var2);
               this.doHit(var1);
               super.update();
               return true;
            }

            ((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(var2);
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

   public void setCanCrawlUnderVehicle(boolean var1) {
      this.bCanCrawlUnderVehicle = var1;
   }

   public boolean isCanWalk() {
      return this.bCanWalk;
   }

   public void setCanWalk(boolean var1) {
      this.bCanWalk = var1;
   }

   public void initCanCrawlUnderVehicle() {
      byte var1 = 100;
      switch(SandboxOptions.instance.Lore.CrawlUnderVehicle.getValue()) {
      case 1:
         var1 = 0;
         break;
      case 2:
         var1 = 5;
         break;
      case 3:
         var1 = 10;
         break;
      case 4:
         var1 = 25;
         break;
      case 5:
         var1 = 50;
         break;
      case 6:
         var1 = 75;
         break;
      case 7:
         var1 = 100;
      }

      this.setCanCrawlUnderVehicle(Rand.Next(100) < var1);
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
               float var1 = this.getPathFindBehavior2().getTargetX();
               float var2 = this.getPathFindBehavior2().getTargetY();
               if (this.DistToSquared(var1, var2) > 0.010000001F && PolygonalMap2.instance.lineClearCollide(this.x, this.y, var1, var2, (int)this.z, (IsoMovingObject)null)) {
                  return false;
               }
            }

            return this.isCurrentState(ZombieGetDownState.instance()) ? false : PolygonalMap2.instance.canStandAt(this.x, this.y, (int)this.z, (IsoMovingObject)null, false, true);
         }
      }
   }

   public void toggleCrawling() {
      boolean var1 = this.bCanCrawlUnderVehicle;
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

      this.bCanCrawlUnderVehicle = var1;
   }

   public void knockDown(boolean var1) {
      this.bKnockedDown = true;
      this.bStaggerBack = true;
      this.setHitReaction("");
      this.playerAttackPosition = var1 ? "BEHIND" : null;
      this.setHitForce(1.0F);
      this.reportEvent("wasHit");
   }

   public void addItemToSpawnAtDeath(InventoryItem var1) {
      if (this.itemsToSpawnAtDeath == null) {
         this.itemsToSpawnAtDeath = new ArrayList();
      }

      this.itemsToSpawnAtDeath.add(var1);
   }

   public void clearItemsToSpawnAtDeath() {
      if (this.itemsToSpawnAtDeath != null) {
         this.itemsToSpawnAtDeath.clear();
      }

   }

   public IsoMovingObject getEatBodyTarget() {
      return this.eatBodyTarget;
   }

   public void setEatBodyTarget(IsoMovingObject var1, boolean var2) {
      if (var1 != this.eatBodyTarget) {
         if (var2 || var1 == null || var1.getEatingZombies().size() < 3) {
            if (this.eatBodyTarget != null) {
               this.eatBodyTarget.getEatingZombies().remove(this);
            }

            this.eatBodyTarget = var1;
            if (var1 != null) {
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

         IsoPlayer var1 = (IsoPlayer)Type.tryCastTo(this.eatBodyTarget, IsoPlayer.class);
         if (var1 != null && var1.ReanimatedCorpse != null) {
            this.setEatBodyTarget((IsoMovingObject)null, false);
         }

         if (var1 != null && var1.isAlive() && !var1.isOnFloor() && !var1.isCurrentState(PlayerHitReactionState.instance())) {
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
      boolean var1 = this.isCurrentState(WalkTowardState.instance()) || this.isCurrentState(PathFindState.instance());
      this.m_characterTextureAnimDuration = var1 ? 0.67F : 2.0F;
      this.m_characterTextureAnimTime += GameTime.getInstance().getTimeDelta();
      if (this.m_characterTextureAnimTime > this.m_characterTextureAnimDuration) {
         this.m_characterTextureAnimTime %= this.m_characterTextureAnimDuration;
      }

   }

   public Vector2 getHitAngle() {
      return this.hitAngle;
   }

   public void setHitAngle(Vector2 var1) {
      if (var1 != null) {
         this.hitAngle.set(var1);
      }
   }

   public int getCrawlerType() {
      return this.crawlerType;
   }

   public void setCrawlerType(int var1) {
      this.crawlerType = var1;
   }

   public void addRandomVisualBandages() {
      if (!"Tutorial".equals(Core.getInstance().getGameMode())) {
         for(int var1 = 0; var1 < 5; ++var1) {
            if (OutfitRNG.Next(10) == 0) {
               BodyPartType var2 = BodyPartType.getRandom();
               String var3 = var2.getBandageModel() + "_Blood";
               this.addBodyVisualFromItemType(var3);
            }
         }

      }
   }

   public void addVisualBandage(BodyPartType var1, boolean var2) {
      String var10000 = var1.getBandageModel();
      String var3 = var10000 + (var2 ? "_Blood" : "");
      this.addBodyVisualFromItemType(var3);
   }

   public void addRandomVisualDamages() {
      for(int var1 = 0; var1 < 5; ++var1) {
         if (OutfitRNG.Next(5) == 0) {
            String var2 = (String)OutfitRNG.pickRandom(ScriptManager.instance.getZedDmgMap());
            this.addBodyVisualFromItemType("Base." + var2);
         }
      }

   }

   public String getPlayerAttackPosition() {
      return this.playerAttackPosition;
   }

   public void setPlayerAttackPosition(String var1) {
      this.playerAttackPosition = var1;
   }

   public boolean isSitAgainstWall() {
      return this.sitAgainstWall;
   }

   public void setSitAgainstWall(boolean var1) {
      this.sitAgainstWall = var1;
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

   public void setSkeleton(boolean var1) {
      this.isSkeleton = var1;
      if (var1) {
         this.getHumanVisual().setHairModel("");
         this.getHumanVisual().setBeardModel("");
         ModelManager.instance.Reset(this);
      }

   }

   public int getHitTime() {
      return this.hitTime;
   }

   public void setHitTime(int var1) {
      this.hitTime = var1;
   }

   public int getThumpTimer() {
      return this.thumpTimer;
   }

   public void setThumpTimer(int var1) {
      this.thumpTimer = var1;
   }

   public IsoMovingObject getTarget() {
      return this.target;
   }

   public void setTargetSeenTime(float var1) {
      this.targetSeenTime = var1;
   }

   public float getTargetSeenTime() {
      return this.targetSeenTime;
   }

   public boolean isTargetVisible() {
      IsoPlayer var1 = (IsoPlayer)Type.tryCastTo(this.target, IsoPlayer.class);
      if (var1 != null && this.getCurrentSquare() != null) {
         return GameServer.bServer ? ServerLOS.instance.isCouldSee(var1, this.getCurrentSquare()) : this.getCurrentSquare().isCouldSee(var1.getPlayerNum());
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
      State var1 = this.getCurrentState();
      return var1 != null && var1.isAttacking(this);
   }

   public boolean isZombieAttacking(IsoMovingObject var1) {
      if (GameClient.bClient) {
         return this.legsSprite != null && this.legsSprite.CurrentAnim != null && "ZombieBite".equals(this.legsSprite.CurrentAnim.name);
      } else {
         return var1 == this.target && this.isCurrentState(AttackState.instance());
      }
   }

   public int getHitHeadWhileOnFloor() {
      return this.hitHeadWhileOnFloor;
   }

   public void setHitHeadWhileOnFloor(int var1) {
      this.hitHeadWhileOnFloor = var1;
      this.networkAI.extraUpdate();
   }

   public boolean isHitLegsWhileOnFloor() {
      return this.hitLegsWhileOnFloor;
   }

   public void setHitLegsWhileOnFloor(boolean var1) {
      this.hitLegsWhileOnFloor = var1;
   }

   public void makeInactive(boolean var1) {
      if (var1 != this.inactive) {
         if (var1) {
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
         float var1 = Vector2.dot(tempo.x, tempo.y, tempo2.x, tempo2.y);
         return (double)var1 >= 0.8D;
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
      int var1 = SandboxOptions.instance.Lore.Memory.getValue();
      short var2 = 160;
      if (this.inactive) {
         var2 = 5;
      } else if (var1 == 1) {
         var2 = 250;
      } else if (var1 == 3) {
         var2 = 100;
      } else if (var1 == 4) {
         var2 = 5;
      }

      int var3 = var2 * 5;
      return var3;
   }

   public boolean shouldDoFenceLunge() {
      if (!SandboxOptions.instance.Lore.ZombiesFenceLunge.getValue()) {
         return false;
      } else if (Rand.NextBool(3)) {
         return false;
      } else {
         IsoGameCharacter var1 = (IsoGameCharacter)Type.tryCastTo(this.target, IsoGameCharacter.class);
         if (var1 != null && (int)var1.getZ() == (int)this.getZ()) {
            if (var1.getVehicle() != null) {
               return false;
            } else {
               return (double)this.DistTo(var1) < 3.9D;
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

   public void setTarget(IsoMovingObject var1) {
      if (this.target != var1 && (!GameClient.bClient || !this.networkAI.isLocalControl())) {
         this.target = var1;
         this.networkAI.extraUpdate();
      }
   }

   public boolean isAlwaysKnockedDown() {
      return this.alwaysKnockedDown;
   }

   public void setAlwaysKnockedDown(boolean var1) {
      this.alwaysKnockedDown = var1;
   }

   public void setDressInRandomOutfit(boolean var1) {
      this.bDressInRandomOutfit = var1;
   }

   public void setBodyToEat(IsoDeadBody var1) {
      this.bodyToEat = var1;
   }

   public boolean isForceEatingAnimation() {
      return this.forceEatingAnimation;
   }

   public void setForceEatingAnimation(boolean var1) {
      this.forceEatingAnimation = var1;
   }

   public boolean isOnlyJawStab() {
      return this.onlyJawStab;
   }

   public void setOnlyJawStab(boolean var1) {
      this.onlyJawStab = var1;
   }

   public boolean isNoTeeth() {
      return this.noTeeth;
   }

   public void setNoTeeth(boolean var1) {
      this.noTeeth = var1;
   }

   public void setThumpFlag(int var1) {
      if (this.thumpFlag != var1) {
         this.thumpFlag = var1;
         this.networkAI.extraUpdate();
      }
   }

   public void checkDead(IsoGameCharacter var1, boolean var2) {
      if (this.isDead() && !this.isOnDeathDone()) {
         this.DoZombieInventory();
         LuaEventManager.triggerEvent("OnZombieDead", this);
         this.setDoDeathSound(var2);
         super.Kill(var1);
         this.setOnDeathDone(true);
      }

   }

   public static class AttackNetworkEvent {
      public short zombieId;
      public short targetId;
      public byte outcome;
      public String targetHitReaction;

      public void load(ByteBufferReader var1) {
         this.zombieId = var1.getShort();
         this.targetId = var1.getShort();
         this.outcome = var1.getByte();
         this.targetHitReaction = var1.getUTF();
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

      void calculate(Mover var1, IsoGridSquare var2) {
         this.start = var2;
         this.mover = var1;
         if (this.start.getRoom() != null) {
            this.building = this.start.getRoom().getBuilding();
         }

         boolean var3 = false;
         boolean var4 = false;
         if (this.push(this.start.getX(), this.start.getY())) {
            while((var2 = this.pop()) != null) {
               int var6 = var2.getX();

               int var5;
               for(var5 = var2.getY(); this.shouldVisit(var6, var5, var6, var5 - 1); --var5) {
               }

               var4 = false;
               var3 = false;

               while(true) {
                  this.visited.setValue(this.gridX(var6), this.gridY(var5), true);
                  IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var6, var5, this.start.getZ());
                  if (var7 != null) {
                     this.choices.add(var7);
                  }

                  if (!var3 && this.shouldVisit(var6, var5, var6 - 1, var5)) {
                     if (!this.push(var6 - 1, var5)) {
                        return;
                     }

                     var3 = true;
                  } else if (var3 && !this.shouldVisit(var6, var5, var6 - 1, var5)) {
                     var3 = false;
                  } else if (var3 && !this.shouldVisit(var6 - 1, var5, var6 - 1, var5 - 1) && !this.push(var6 - 1, var5)) {
                     return;
                  }

                  if (!var4 && this.shouldVisit(var6, var5, var6 + 1, var5)) {
                     if (!this.push(var6 + 1, var5)) {
                        return;
                     }

                     var4 = true;
                  } else if (var4 && !this.shouldVisit(var6, var5, var6 + 1, var5)) {
                     var4 = false;
                  } else if (var4 && !this.shouldVisit(var6 + 1, var5, var6 + 1, var5 - 1) && !this.push(var6 + 1, var5)) {
                     return;
                  }

                  ++var5;
                  if (!this.shouldVisit(var6, var5 - 1, var6, var5)) {
                     break;
                  }
               }
            }

         }
      }

      boolean shouldVisit(int var1, int var2, int var3, int var4) {
         if (this.gridX(var3) < 11 && this.gridX(var3) >= 0) {
            if (this.gridY(var4) < 11 && this.gridY(var4) >= 0) {
               if (this.visited.getValue(this.gridX(var3), this.gridY(var4))) {
                  return false;
               } else {
                  IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, this.start.getZ());
                  if (var5 == null) {
                     return false;
                  } else if (!var5.Has(IsoObjectType.stairsBN) && !var5.Has(IsoObjectType.stairsMN) && !var5.Has(IsoObjectType.stairsTN)) {
                     if (!var5.Has(IsoObjectType.stairsBW) && !var5.Has(IsoObjectType.stairsMW) && !var5.Has(IsoObjectType.stairsTW)) {
                        if (var5.getRoom() != null && this.building == null) {
                           return false;
                        } else if (var5.getRoom() == null && this.building != null) {
                           return false;
                        } else {
                           return !IsoWorld.instance.CurrentCell.blocked(this.mover, var3, var4, this.start.getZ(), var1, var2, this.start.getZ());
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

      boolean push(int var1, int var2) {
         IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, this.start.getZ());
         this.stack.push(var3);
         return true;
      }

      IsoGridSquare pop() {
         return this.stack.isEmpty() ? null : (IsoGridSquare)this.stack.pop();
      }

      int gridX(int var1) {
         return var1 - (this.start.getX() - 5);
      }

      int gridY(int var1) {
         return var1 - (this.start.getY() - 5);
      }

      int gridX(IsoGridSquare var1) {
         return var1.getX() - (this.start.getX() - 5);
      }

      int gridY(IsoGridSquare var1) {
         return var1.getY() - (this.start.getY() - 5);
      }

      IsoGridSquare choose() {
         if (this.choices.isEmpty()) {
            return null;
         } else {
            int var1 = Rand.Next(this.choices.size());
            return (IsoGridSquare)this.choices.get(var1);
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
      Burned(10),
      DeadCloseKilled(10),
      DeadNotCloseKilled(10),
      Hurt(10),
      Idle(15),
      Lunge(40),
      MAX(-1);

      private int radius;
      private static final IsoZombie.ZombieSound[] values = values();

      private ZombieSound(int var3) {
         this.radius = var3;
      }

      public int radius() {
         return this.radius;
      }

      public static IsoZombie.ZombieSound fromIndex(int var0) {
         return var0 >= 0 && var0 < values.length ? values[var0] : MAX;
      }

      // $FF: synthetic method
      private static IsoZombie.ZombieSound[] $values() {
         return new IsoZombie.ZombieSound[]{Burned, DeadCloseKilled, DeadNotCloseKilled, Hurt, Idle, Lunge, MAX};
      }
   }
}
