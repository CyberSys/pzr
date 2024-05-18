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

   public void setVehicle4TestCollision(BaseVehicle var1) {
      this.vehicle4testCollision = var1;
   }

   public static String getMD5Checksum(String var0) throws Exception {
      byte[] var1 = createChecksum(var0);
      String var2 = "";

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2 = var2 + Integer.toString((var1[var3] & 255) + 256, 16).substring(1);
      }

      return var2;
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

   public IsoZombie(IsoCell var1) {
      this(var1, (SurvivorDesc)null, -1);
   }

   public IsoZombie(IsoCell var1, SurvivorDesc var2, int var3) {
      super(var1, 0.0F, 0.0F, 0.0F);
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
      int var4 = Rand.Next(10) + 1;
      if (var2 != null) {
         this.descriptor = var2;
         this.palette = var3;
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

   /** @deprecated */
   @Deprecated
   public IsoZombie(IsoCell var1, SurvivorDesc var2) {
      super(var1, 1.0F, 0.0F, 0.0F);
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
      int var3 = Rand.Next(10) + 1;
      int var4 = Rand.Next(3) + 1;
      this.palette = var4;
      if (var4 != 1) {
         this.SpriteName = this.SpriteName + var4;
      }

      this.bFemale = var2.isFemale();
      if (this.bFemale) {
         if (var4 == 1) {
            this.SpriteName = "KateZ";
         } else {
            this.SpriteName = "KateZ" + var4;
         }
      }

      this.InitSpritePartsZombie(this.SpriteName, var2, var2.legs, var2.torso, var2.head, var2.top, var2.bottoms, var2.shoes, var2.skinpal, var2.toppal, var2.bottomspal, var2.shoespal, var2.hair, var2.extra);
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

   public void pathToCharacter(IsoGameCharacter var1) {
      if (!(this.AllowRepathDelay > 0.0F) || this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance()) {
         super.pathToCharacter(var1);
      }
   }

   public void pathToLocation(int var1, int var2, int var3) {
      super.pathToLocation(var1, var2, var3);
   }

   public void pathToLocationF(float var1, float var2, float var3) {
      if (!(this.AllowRepathDelay > 0.0F) || this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance()) {
         super.pathToLocationF(var1, var2, var3);
      }
   }

   /** @deprecated */
   @Deprecated
   public IsoZombie(IsoCell var1, int var2) {
      super(var1, 0.0F, 0.0F, 0.0F);
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
      this.palette = var2;
      this.dir = IsoDirections.fromIndex(Rand.Next(8));
      this.weight = 0.7F;
      String var4 = "Zombie_palette";
      if (var2 == 10) {
         var4 = var4 + "10";
      } else {
         var4 = var4 + "0" + Integer.toString(var2);
      }

      int var5 = Rand.Next(3) + 1;
      if (var5 != 1) {
         this.SpriteName = this.SpriteName + var5;
      }

      if (this.bFemale) {
         if (var5 == 1) {
            this.SpriteName = "KateZ";
         } else {
            this.SpriteName = "KateZ" + var5;
         }
      }

      this.palette = var5;
      SurvivorDesc var6 = SurvivorFactory.CreateSurvivor();
      this.InitSpritePartsZombie(this.SpriteName, var6, var6.legs, var6.torso, var6.head, var6.top, var6.bottoms, var6.shoes, var6.skinpal, var6.toppal, var6.bottomspal, var6.shoespal, var6.hair, var6.extra);
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

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.palette = var1.getInt();
      int var3 = this.palette;
      String var4 = "Zombie_palette";
      if (var3 == 10) {
         var4 = var4 + "10";
      } else {
         (new StringBuilder()).append(var4).append("0").append(Integer.toString(var3)).toString();
      }

      this.walkVariant = "ZombieWalk";
      this.SpriteName = "BobZ";
      if (this.palette != 1) {
         this.SpriteName = this.SpriteName + this.palette;
      }

      SurvivorDesc var5 = this.descriptor;
      this.bFemale = var5.isFemale();
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

      this.InitSpritePartsZombie(this.SpriteName, var5, var5.legs, var5.torso, var5.head, var5.top, var5.bottoms, var5.shoes, var5.skinpal, var5.toppal, var5.bottomspal, var5.shoespal, var5.hair, var5.extra);
      this.sprite.def.tintr = 0.95F + (float)Rand.Next(5) / 100.0F;
      this.sprite.def.tintg = 0.95F + (float)Rand.Next(5) / 100.0F;
      this.sprite.def.tintb = 0.95F + (float)Rand.Next(5) / 100.0F;
      this.defaultState = ZombieStandState.instance();
      this.DoZombieStats();
      this.PathSpeed = var1.getFloat();
      this.setWidth(0.3F);
      this.TimeSinceSeenFlesh = (float)var1.getInt();
      this.alpha[IsoPlayer.getPlayerIndex()] = 0.0F;
      this.setFakeDead(var1.getInt() == 1);
      this.stateMachine.Lock = false;
      this.stateMachine.changeState(this.defaultState);
      this.getCell().getZombieList().add(this);
   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.putInt(this.palette);
      var1.putFloat(this.PathSpeed);
      var1.putInt((int)this.TimeSinceSeenFlesh);
      if (this.bCrawling) {
         var1.putInt(1);
      } else {
         var1.putInt(this.isFakeDead() ? 1 : 0);
      }

   }

   public boolean AttemptAttack() {
      if (this.stateMachine.getCurrent() != AttackState.instance() && this.getCurrentState() != AttackVehicleState.instance()) {
         if (!this.Ghost) {
            if (GameServer.bServer && this instanceof IsoZombie) {
               GameServer.sendZombie(this);
            }

            if (this.target != null && this.target instanceof IsoGameCharacter) {
               IsoGameCharacter var1 = (IsoGameCharacter)this.target;
               BaseVehicle var2 = var1.getVehicle();
               if (var2 != null) {
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

   public void collideWith(IsoObject var1) {
      if (!this.Ghost) {
         if (var1.rerouteCollide != null) {
            var1 = this.rerouteCollide;
         }

         if (var1 instanceof IsoWindow && ((IsoWindow)var1).canClimbThrough(this) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
            if ((this.stateMachine.getCurrent() != PathFindState.instance() || this.isOnPath((IsoWindow)var1)) && !this.bCrawling) {
               this.StateMachineParams.clear();
               this.StateMachineParams.put(0, var1);
               this.getStateMachine().changeState(ClimbThroughWindowState.instance());
            }
         } else if (var1 instanceof IsoThumpable && ((IsoThumpable)var1).isWindow() && !((IsoThumpable)var1).isBarricaded() && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
            if ((this.stateMachine.getCurrent() != PathFindState.instance() || this.isOnPath(var1)) && !this.bCrawling) {
               this.StateMachineParams.clear();
               this.StateMachineParams.put(0, var1);
               this.getStateMachine().changeState(ClimbThroughWindowState.instance());
            }
         } else if (var1 instanceof Thumpable && (!(var1 instanceof IsoThumpable) || ((IsoThumpable)var1).isThumpable()) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost && !this.bCrawling) {
            if (!SandboxOptions.instance.Lore.ThumpNoChasing.getValue() && this.target == null && !this.chasingSound) {
               this.stateMachine.changeState(ZombieStandState.instance());
            } else {
               if (var1 instanceof IsoThumpable && !SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
                  return;
               }

               this.setThumpTarget((Thumpable)var1);
               this.path = null;
               this.stateMachine.changeState(ThumpState.instance());
            }
         }

         State var2 = this.getCurrentState();
         if (!this.bCrawling && IsoWindowFrame.isWindowFrame(var1) && (var2 == PathFindState.instance() || var2 == LungeState.instance() || var2 == WalkTowardState.instance()) && (var2 != PathFindState.instance() || this.isOnPath(var1))) {
            this.StateMachineParams.clear();
            this.StateMachineParams.put(0, var1);
            this.getStateMachine().changeState(ClimbThroughWindowState.instance());
         }

         super.collideWith(var1);
      }
   }

   private boolean isOnPath(IsoObject var1) {
      if (this.path != null) {
         for(int var2 = this.pathIndex; var2 < this.path.getLength(); ++var2) {
            Path.Step var3 = this.path.getStep(var2);
            IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare(var3.x, var3.y, var3.z);
            if (var4 != null && var4.getObjects().contains(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public void Hit(HandWeapon var1, IsoGameCharacter var2, float var3, boolean var4, float var5) {
      if (!Core.bTutorial || this != ImmortalTutorialZombie) {
         super.Hit(var1, var2, var3, var4, var5);
         if (!(var2 instanceof IsoZombie)) {
            this.target = var2;
         }

         if (this.Health <= 0.0F && !this.bDead) {
            this.DoZombieInventory();
            LuaEventManager.triggerEvent("OnZombieDead", this);
            this.bDead = true;
         }

         this.TimeSinceSeenFlesh = 0.0F;
         if (!this.bDead && !this.isOnFloor() && !var4 && var1 != null && var1.getScriptItem().getCategories().contains("Blade") && var2 instanceof IsoPlayer && this.DistToProper(var2) <= 0.9F && (this.getCurrentState() == AttackState.instance() || this.getCurrentState() == LungeState.instance())) {
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
                                          BaseVehicle var1 = ((IsoGameCharacter)this.target).getVehicle();
                                          if (var1 != null) {
                                             if (var1.isCharacterAdjacentTo(this) && this.getCurrentState() != PathFindState.instance() && this.getCurrentState() != WalkTowardState.instance()) {
                                                this.AttemptAttack();
                                             }

                                             return;
                                          }
                                       }

                                       if (System.currentTimeMillis() - this.LungeSoundTime > 5000L) {
                                          String var2 = "MaleZombieAttack";
                                          if (this.bFemale) {
                                             var2 = "FemaleZombieAttack";
                                          }

                                          this.getEmitter().playVocals(var2);
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
            Vector2 var1 = new Vector2(this.getX(), this.getY());
            var1.x -= IsoPlayer.instance.getX();
            var1.y -= IsoPlayer.instance.getY();
            var1.normalize();
            IsoPlayer.instance.DirectionFromVector(var1);
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

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      if (IsoCamera.CamCharacter != IsoPlayer.instance) {
         this.targetAlpha[IsoPlayer.getPlayerIndex()] = 1.0F;
         this.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
      }

      super.render(var1, var2, var3, var4, var5);
   }

   public void RespondToSound() {
      if (!this.Ghost) {
         if (!this.Deaf) {
            if (!this.isUseless()) {
               float var1 = 0.0F;
               IsoObject var2 = null;
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

               if (var3 != null) {
                  float var6 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), (float)var3.x, (float)var3.y) / 4.0F;
                  if ((this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance() || this.getCurrentState() == PathFindState2.instance() || this.getCurrentState() == ZombieStandState.instance()) && this.getPathFindBehavior2().isGoal2Location() && !IsoUtils.isSimilarDirection(this, (float)var3.x, (float)var3.y, this.getPathFindBehavior2().getTargetX(), this.getPathFindBehavior2().getTargetY(), 0.5F)) {
                     this.pathToLocation(var3.x + Rand.Next((int)(-var6), (int)var6), var3.y + Rand.Next((int)(-var6), (int)var6), var3.z);
                     this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
                     this.chasingSound = true;
                     this.AllowRepathDelay = (float)AllowRepathDelayMax;
                     this.timeSinceRespondToSound = 0.0F;
                     this.soundAttract = var1;
                     this.soundSourceTarget = var2;
                     return;
                  }

                  if (this.timeSinceRespondToSound < 60.0F || this.getPathFindBehavior2().isGoal2Location()) {
                     return;
                  }

                  this.pathToLocation(var3.x + Rand.Next((int)(-var6), (int)var6), var3.y + Rand.Next((int)(-var6), (int)var6), var3.z);
                  if (this.getCurrentState() == PathFindState.instance() || this.getCurrentState() == WalkTowardState.instance()) {
                     this.setLastHeardSound(this.getPathTargetX(), this.getPathTargetY(), this.getPathTargetZ());
                     this.chasingSound = true;
                  }

                  this.AllowRepathDelay = (float)AllowRepathDelayMax;
                  this.timeSinceRespondToSound = 0.0F;
                  this.soundAttract = var1;
                  this.soundSourceTarget = var2;
               }

            }
         }
      }
   }

   public void spotted(IsoMovingObject var1, boolean var2) {
      if (!GameClient.bClient) {
         if (this.getCurrentSquare() != null) {
            if (var1.getCurrentSquare() != null) {
               if (!this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke) && !this.isUseless()) {
                  if (!(var1 instanceof IsoPlayer) || !((IsoPlayer)var1).GhostMode) {
                     if (!(var1 instanceof IsoGameCharacter) || !((IsoGameCharacter)var1).isDead()) {
                        if (this.getCurrentSquare() == null) {
                           this.ensureOnTile();
                        }

                        if (var1.getCurrentSquare() == null) {
                           var1.ensureOnTile();
                        }

                        float var3 = 200.0F;
                        int var4 = var1 instanceof IsoPlayer && !GameServer.bServer ? ((IsoPlayer)var1).PlayerIndex : 0;
                        float var5 = (var1.getCurrentSquare().lighting[var4].lightInfo().r + var1.getCurrentSquare().lighting[var4].lightInfo().g + var1.getCurrentSquare().lighting[var4].lightInfo().b) / 3.0F;
                        float var6 = RenderSettings.getInstance().getAmbientForPlayer(var4);
                        float var7 = (this.getCurrentSquare().lighting[var4].lightInfo().r + this.getCurrentSquare().lighting[var4].lightInfo().g + this.getCurrentSquare().lighting[var4].lightInfo().b) / 3.0F;
                        var7 = var7 * var7 * var7;
                        if (var5 > 1.0F) {
                           var5 = 1.0F;
                        }

                        if (var5 < 0.0F) {
                           var5 = 0.0F;
                        }

                        if (var7 > 1.0F) {
                           var7 = 1.0F;
                        }

                        if (var7 < 0.0F) {
                           var7 = 0.0F;
                        }

                        float var8 = 1.0F - (var5 - var7);
                        if (var5 < 0.2F) {
                           var5 = 0.2F;
                        }

                        if (var6 < 0.2F) {
                           var6 = 0.2F;
                        }

                        if (var1 instanceof IsoPlayer) {
                           boolean var9 = false;
                        }

                        if (var1.getCurrentSquare().getRoom() != this.getCurrentSquare().getRoom()) {
                           var3 = 50.0F;
                           if (var1.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() == null || var1.getCurrentSquare().getRoom() == null && this.getCurrentSquare().getRoom() != null) {
                              var3 = 20.0F;
                              if (((IsoGameCharacter)var1).IsSneaking()) {
                                 if (var5 < 0.4F) {
                                    var3 = 0.0F;
                                 } else {
                                    var3 = 10.0F;
                                 }
                              } else if (var1.getMovementLastFrame().getLength() <= 0.04F && var5 < 0.4F) {
                                 var3 = 10.0F;
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
                           int var19 = Math.abs(var1.getCurrentSquare().getZ() - this.current.getZ()) * 5;
                           ++var19;
                           var3 /= (float)var19;
                        }

                        float var20 = GameTime.getInstance().getViewDist();
                        if (!(tempo.getLength() > var20)) {
                           if (GameServer.bServer) {
                              this.bIndoorZombie = false;
                           }

                           if (tempo.getLength() < var20) {
                              var20 = tempo.getLength();
                           }

                           var20 *= 1.1F;
                           if (var20 > GameTime.getInstance().getViewDistMax()) {
                              var20 = GameTime.getInstance().getViewDistMax();
                           }

                           tempo.normalize();
                           Vector2 var10 = this.getVectorFromDirection(tempo2);
                           float var11 = var10.dot(tempo);
                           if (var20 > 1.0F) {
                              if (var11 < -0.4F) {
                                 var3 = 0.0F;
                              } else if (var11 < -0.2F) {
                                 var3 /= 8.0F;
                              } else if (var11 < -0.0F) {
                                 var3 /= 4.0F;
                              } else if (var11 < 0.2F) {
                                 var3 /= 2.0F;
                              } else if (var11 <= 0.4F) {
                                 var3 *= 2.0F;
                              } else if (var11 > 0.4F) {
                                 var3 *= 8.0F;
                              } else if (var11 > 0.6F) {
                                 var3 *= 16.0F;
                              } else if (var11 > 0.8F) {
                                 var3 *= 32.0F;
                              }
                           }

                           if (var3 > 0.0F && this.target instanceof IsoPlayer) {
                              IsoPlayer var12 = (IsoPlayer)this.target;
                              if (!GameServer.bServer && var12.RemoteID == -1 && this.current.isCanSee(var12.PlayerIndex)) {
                                 ((IsoPlayer)this.target).targetedByZombie = true;
                                 ((IsoPlayer)this.target).lastTargeted = 0.0F;
                              }
                           }

                           var3 *= var8;
                           int var21 = (int)var1.getZ() - (int)this.getZ();
                           if (var21 >= 1) {
                              var3 /= (float)(var21 * 3);
                           }

                           var3 *= 1.0F - var20 / GameTime.getInstance().getViewDist();
                           var3 *= 1.0F - var20 / GameTime.getInstance().getViewDist();
                           var3 *= 1.0F - var20 / GameTime.getInstance().getViewDist();
                           float var13 = var1.getMovementLastFrame().getLength();
                           if (var13 == 0.0F && var5 <= 0.2F) {
                              var5 = 0.0F;
                           }

                           if (((IsoGameCharacter)var1).IsSneaking() && (!(var1 instanceof IsoPlayer) || ((IsoPlayer)var1).getTorchStrength() == 0.0F)) {
                              var3 *= 0.5F;
                           }

                           if (var13 < 0.01F) {
                              var3 *= 0.5F;
                           } else if (((IsoGameCharacter)var1).IsSneaking()) {
                              var3 *= 0.6F;
                           } else if (var13 < 0.06F) {
                              var3 *= 0.8F;
                           } else if (var13 >= 0.06F) {
                              var3 *= 2.4F;
                           }

                           if (var20 < 5.0F) {
                              var3 *= 3.0F;
                           }

                           if (this.spottedLast == var1 && this.TimeSinceSeenFlesh < 60.0F) {
                              var3 = 1000.0F;
                           }

                           var3 *= ((IsoGameCharacter)var1).getSneakSpotMod();
                           var3 *= var6;
                           float var15;
                           if (this.target != var1 && this.target != null) {
                              float var14 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), var1.getX(), var1.getY());
                              var15 = IsoUtils.DistanceManhatten(this.getX(), this.getY(), this.target.getX(), this.target.getY());
                              if (var14 > var15) {
                                 return;
                              }
                           }

                           var3 *= 0.3F;
                           if (var2) {
                              var3 = 1000000.0F;
                           }

                           if (this.BonusSpotTime > 0) {
                              var3 = 1000000.0F;
                           }

                           var3 *= 1.2F;
                           if (SandboxOptions.instance.Lore.Sight.getValue() == 1) {
                              var3 *= 3.0F;
                           }

                           if (SandboxOptions.instance.Lore.Sight.getValue() == 3 || this.inactive) {
                              var3 *= 0.25F;
                           }

                           var3 *= 0.25F;
                           if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).HasTrait("Inconspicuous")) {
                              var3 *= 0.5F;
                           }

                           if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).HasTrait("Conspicuous")) {
                              var3 *= 2.0F;
                           }

                           IsoGridSquare var22 = var1.getCurrentSquare();
                           var15 = 0.5F;
                           float var16;
                           if (var22 != null) {
                              if (var22.Is(IsoFlagType.collideN)) {
                                 var16 = 0.5F;
                                 if (!var22.Is(IsoFlagType.HoppableN)) {
                                    var16 = 0.3F;
                                 }

                                 var15 *= var16;
                              }

                              if (var22.Is(IsoFlagType.collideW)) {
                                 var16 = 0.5F;
                                 if (!var22.Is(IsoFlagType.HoppableW)) {
                                    var16 = 0.3F;
                                 }

                                 var15 *= var16;
                              }

                              IsoGridSquare var24 = IsoWorld.instance.CurrentCell.getGridSquare(var22.getX(), var22.getY() + 1, var22.getZ());
                              IsoGridSquare var17 = IsoWorld.instance.CurrentCell.getGridSquare(var22.getX() + 1, var22.getY(), var22.getZ());
                              float var18;
                              if (var24 != null && (var24.Is(IsoFlagType.collideN) || var24.Is(IsoFlagType.solid))) {
                                 var18 = 0.5F;
                                 if (!var24.Is(IsoFlagType.HoppableN)) {
                                    var18 = 0.3F;
                                 }

                                 var15 *= var18;
                              }

                              if (var17 != null && (var17.Is(IsoFlagType.collideW) || var17.Is(IsoFlagType.solid))) {
                                 var18 = 0.5F;
                                 if (!var17.Is(IsoFlagType.HoppableW)) {
                                    var18 = 0.3F;
                                 }

                                 var15 *= var18;
                              }
                           }

                           if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).bSneaking) {
                              var3 *= var15;
                           } else {
                              if (var15 < 0.5F) {
                                 var15 = 0.5F;
                              }

                              var3 *= var15;
                           }

                           var3 *= GameTime.instance.getMultiplier();
                           var3 = (float)Math.floor((double)var3);
                           if ((float)Rand.Next(400) >= var3) {
                              if (var3 > 20.0F && var1 instanceof IsoPlayer && var20 < 15.0F) {
                                 ((IsoPlayer)var1).bCouldBeSeenThisFrame = true;
                              }

                              if (!((IsoPlayer)var1).isbCouldBeSeenThisFrame() && !((IsoPlayer)var1).isbSeenThisFrame() && ((IsoPlayer)var1).isbSneaking() && ((IsoPlayer)var1).JustMoved && Rand.Next((int)(700.0F * GameTime.instance.getInvMultiplier())) == 0) {
                                 if (GameServer.bServer) {
                                    GameServer.addXp((IsoPlayer)var1, PerkFactory.Perks.Sneak, 1);
                                 } else {
                                    ((IsoPlayer)var1).getXp().AddXP(PerkFactory.Perks.Sneak, 1.0F);
                                 }
                              }

                              if (!((IsoPlayer)var1).isbCouldBeSeenThisFrame() && !((IsoPlayer)var1).isbSeenThisFrame() && ((IsoPlayer)var1).isbSneaking() && ((IsoPlayer)var1).JustMoved && Rand.Next((int)(700.0F * GameTime.instance.getInvMultiplier())) == 0) {
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
                                 this.BonusSpotTime = 120;
                              }

                              this.LastTargetSeenX = (int)var1.getX();
                              this.LastTargetSeenY = (int)var1.getY();
                              this.LastTargetSeenZ = (int)var1.getZ();
                              if (this.stateMachine.getCurrent() != StaggerBackState.instance()) {
                                 this.target = var1;
                                 this.vectorToTarget.x = var1.getX();
                                 this.vectorToTarget.y = var1.getY();
                                 var10000 = this.vectorToTarget;
                                 var10000.x -= this.getX();
                                 var10000 = this.vectorToTarget;
                                 var10000.y -= this.getY();
                                 var16 = this.vectorToTarget.getLength();
                                 if (!var2) {
                                    this.TimeSinceSeenFlesh = 0.0F;
                                 }

                                 if (Rand.Next(400) == 0) {
                                 }

                                 if (this.target != this.spottedLast || this.getCurrentState() != LungeState.instance() || !(this.LungeTimer > 0.0F)) {
                                    if (this.target != this.spottedLast || this.getCurrentState() != AttackVehicleState.instance()) {
                                       if ((int)this.getZ() == (int)this.target.getZ() && (var16 <= 3.5F || this.target instanceof IsoGameCharacter && ((IsoGameCharacter)this.target).getVehicle() != null && var16 <= 4.0F) && this.getStateEventDelayTimer() <= 0.0F && !PolygonalMap2.instance.lineClearCollide(this.getX(), this.getY(), var1.x, var1.y, (int)this.getZ(), var1)) {
                                          this.target = var1;
                                          this.Lunge();
                                          if (this.getCurrentState() == LungeState.instance()) {
                                             return;
                                          }
                                       }

                                       this.spottedLast = var1;
                                       if (!this.Ghost && !this.getCurrentSquare().getProperties().Is(IsoFlagType.smoke)) {
                                          this.target = var1;
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

                                             BaseVehicle var23 = ((IsoGameCharacter)this.target).getVehicle();
                                             if (Math.abs(var23.getCurrentSpeedKmHour()) > 0.1F && this.DistToSquared(var23) <= 16.0F) {
                                                return;
                                             }

                                             this.pathToCharacter((IsoGameCharacter)this.target);
                                             this.AllowRepathDelay = 10.0F;
                                             return;
                                          }

                                          this.pathToCharacter((IsoGameCharacter)var1);
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

   public void Move(Vector2 var1) {
      if (!GameClient.bClient) {
         this.nx += var1.x * GameTime.instance.getMultiplier();
         this.ny += var1.y * GameTime.instance.getMultiplier();
         this.movex = var1.x;
         this.movey = var1.y;
         this.reqMovement.x = var1.x;
         this.reqMovement.y = var1.y;
      }
   }

   public void DoFootstepSound(float var1) {
      if (!GameServer.bServer) {
         if (var1 != 0.0F) {
            if (this.getCurrentSquare() != null) {
               boolean var3;
               if (GameClient.bClient) {
                  if (this.def != null && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
                     int var6 = (int)this.def.Frame;
                     if (var6 >= 0 && var6 < 5) {
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

                     this.stepFrameLast = var6;
                  } else {
                     this.stepFrameLast = -1;
                  }

               } else {
                  if (this.def != null && this.def.NextFrame && this.sprite != null && this.sprite.CurrentAnim != null && (this.sprite.CurrentAnim.name.contains("Run") || this.sprite.CurrentAnim.name.contains("Walk"))) {
                     boolean var2 = SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 15.0F);
                     if (var2) {
                        var3 = false;
                        if ((int)this.def.Frame == 0) {
                           var3 = true;
                        }

                        if ((int)this.def.Frame == 5) {
                           var3 = true;
                        }

                        if (var3) {
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
         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            IsoPlayer var2 = IsoPlayer.players[var1];
            if (var2 != null && var2.ReanimatedCorpse == this) {
               var2.setX(GameClient.bClient ? this.bx : this.getX());
               var2.setY(GameClient.bClient ? this.by : this.getY());
               var2.setZ(this.getZ());
               var2.setDir(this.getDir());
               var2.angle.set(this.getAngle());
               var2.setCurrent(this.getCell().getGridSquare((int)var2.x, (int)var2.y, (int)var2.z));
               var2.updateLightInfo();
               if (var2.soundListener != null) {
                  var2.soundListener.setPos(var2.getX(), var2.getY(), var2.getZ());
                  var2.soundListener.tick();
               }

               IsoPlayer var3 = IsoPlayer.instance;
               IsoPlayer.instance = var2;
               var2.updateLOS();
               IsoPlayer.instance = var3;
               if (GameClient.bClient && this.networkUpdate.Check()) {
                  GameClient.instance.sendPlayer(var2);
               }

               var2.dirtyRecalcGridStackTime = 2.0F;
               break;
            }
         }
      }

   }

   public void update() {
      if (this.zombieSoundInstance != -1L) {
         if (this.target instanceof IsoPlayer) {
            float var1 = (float)(Rand.Next(40) + 60) / 100.0F;
            javafmod.FMOD_Studio_SetParameter(this.zombieSoundInstance, "Aggitation", var1);
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
         boolean var6 = false;
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
            float var3;
            float var4;
            float var12;
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
                     String var9 = this.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
                     if (!this.getEmitter().isPlaying(var9)) {
                        ZombieVocalsManager.instance.addCharacter(this);
                     }
                  }

                  this.mpIdleSound = false;
               }

               if (this.vehicle4testCollision != null) {
                  BaseVehicle var10 = this.vehicle4testCollision;
                  this.vehicle4testCollision = null;
                  if (var10.isEngineRunning() && var10.getDriver() instanceof IsoPlayer && ((IsoPlayer)var10.getDriver()).isLocalPlayer()) {
                     var12 = var10.jniLinearVelocity.x;
                     var3 = var10.jniLinearVelocity.z;
                     var4 = (float)Math.sqrt((double)(var12 * var12 + var3 * var3));
                     if (this.isOnFloor() && (this.bCrawling || this.legsSprite.CurrentAnim != null && this.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
                        int var13 = var10.testCollisionWithProneCharacter(this, this.angle.x, this.angle.y, false);
                        if (var13 > 0) {
                           super.update();
                           return;
                        }
                     } else if (var4 > 0.05F && var10.testCollisionWithCharacter(this, 0.3F) != null) {
                        var10.hitCharacter(this);
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
                  IsoPlayer var8 = null;
                  var12 = 1000000.0F;

                  for(int var11 = 0; var11 < IsoPlayer.numPlayers; ++var11) {
                     if (IsoPlayer.players[var11] != null && IsoPlayer.players[var11].DistTo(this) < var12 && !IsoPlayer.players[var11].isDead()) {
                        var12 = IsoPlayer.players[var11].DistTo(this);
                        var8 = IsoPlayer.players[var11];
                     }
                  }

                  if (var8 != null) {
                     this.AllowRepathDelay = -1.0F;
                     this.pathToCharacter(var8);
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
                        State var7 = this.stateMachine.getCurrent();
                        if (var7 != StaggerBackState.instance() && var7 != BurntToDeath.instance() && var7 != JustDieState.instance() && var7 != StaggerBackDieState.instance() && var7 != FakeDeadZombieState.instance() && var7 != ReanimateState.instance() && var7 != ReanimatePlayerState.instance()) {
                           if (GameServer.bServer && this.OnlineID == -1) {
                              this.OnlineID = ServerMap.instance.getUniqueZombieId();
                           } else {
                              IsoSpriteInstance var10000;
                              if (var7 == PathFindState.instance() && this.finder.progress == AStarPathFinder.PathFindProgress.notyetfound) {
                                 if (this.bCrawling) {
                                    this.PlayAnim("ZombieCrawl");
                                    this.def.AnimFrameIncrease = 0.0F;
                                 } else {
                                    this.PlayAnim("ZombieIdle");
                                    this.def.AnimFrameIncrease = 0.08F + (float)Rand.Next(1000) / 8000.0F;
                                    var10000 = this.def;
                                    var10000.AnimFrameIncrease *= 0.5F;
                                 }
                              } else if (var7 != AttackState.instance() && var7 != AttackVehicleState.instance() && (this.nx != this.x || this.ny != this.y)) {
                                 if (this.walkVariantUse == null || var7 != LungeState.instance()) {
                                    this.walkVariantUse = this.walkVariant;
                                 }

                                 if (this.bCrawling) {
                                    this.walkVariantUse = "ZombieCrawl";
                                 }

                                 if (var7 != ZombieStandState.instance() && var7 != StaggerBackDieState.instance() && var7 != StaggerBackState.instance() && var7 != ThumpState.instance() && var7 != FakeDeadZombieState.instance()) {
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
                           this.AllowRepathDelay -= GameTime.instance.getMultiplier();
                           this.TimeSinceSeenFlesh += GameTime.instance.getMultiplier();
                           short var2 = 160;
                           if (SandboxOptions.instance.Lore.Memory.getValue() == 1) {
                              var2 = 250;
                           }

                           if (SandboxOptions.instance.Lore.Memory.getValue() == 3) {
                              var2 = 100;
                           }

                           if (SandboxOptions.instance.Lore.Memory.getValue() == 4 || this.inactive) {
                              var2 = 5;
                           }

                           if (this.TimeSinceSeenFlesh > (float)var2 && this.target != null) {
                              this.target = null;
                           }

                           if (this.target != null) {
                              this.vectorToTarget.x = this.target.getX();
                              this.vectorToTarget.y = this.target.getY();
                              Vector2 var14 = this.vectorToTarget;
                              var14.x -= this.getX();
                              var14 = this.vectorToTarget;
                              var14.y -= this.getY();
                           }

                           move.x = this.getNx() - this.getLx();
                           move.y = this.getNy() - this.getLy();
                           var3 = move.getLength();
                           var4 = 1.0F - var3 / 0.08F;
                           if (var3 > 0.0F) {
                           }

                           if (IsoPlayer.instance != null && (IsoPlayer.instance.Waiting && Rand.Next(Rand.AdjustForFramerate(1000)) == 0 || !IsoPlayer.instance.Waiting && Rand.Next(Rand.AdjustForFramerate(360)) == 0 && (this.stateMachine.getCurrent() == WalkTowardState.instance() || this.stateMachine.getCurrent() == PathFindState.instance())) && SoundManager.instance.isListenerInRange(this.getX(), this.getY(), 20.0F)) {
                              String var5 = this.bFemale ? "FemaleZombieIdle" : "MaleZombieIdle";
                              if (!this.emitter.isPlaying(var5)) {
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

   public void getZombieLungeSpeed(Vector2 var1) {
      float var2 = this.LungeTimer / 180.0F;
      float var3 = this.getPathSpeed() + 0.03F * var2;
      var1.normalize();
      var1.setLength(var3 * this.getSpeedMod());
      this.bRunning = false;
      if (GameServer.bServer) {
         var2 = 1.0F;
      }

      if (SandboxOptions.instance.Lore.Speed.getValue() == 1 && !this.inactive || this.speedType == 1) {
         var1.setLength(0.08F + 0.01F * (1.0F - var2));
         this.bRunning = true;
      }

   }

   public void getZombieLungeSpeed(float var1, float var2, Vector2 var3) {
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

   public boolean tryThump(IsoGridSquare var1) {
      IsoGridSquare var2 = null;
      if (var1 != null) {
         var2 = var1;
      } else {
         var2 = this.getFeelerTile(this.getFeelersize());
      }

      if (var2 != null && this.current != null) {
         IsoObject var3 = this.current.testCollideSpecialObjects(var2);
         if (var3 instanceof Thumpable && !this.bCrawling) {
            if (var3 instanceof IsoWindow && ((IsoWindow)var3).canClimbThrough(this) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
               this.StateMachineParams.put(0, var3);
               this.stateMachine.changeState(ClimbThroughWindowState.instance());
               return true;
            }

            if (var3 instanceof IsoThumpable && ((IsoThumpable)var3).isWindow() && !((IsoThumpable)var3).isBarricaded() && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
               this.StateMachineParams.put(0, var3);
               this.stateMachine.changeState(ClimbThroughWindowState.instance());
               return true;
            }

            if ((var3 instanceof IsoThumpable && ((IsoThumpable)var3).isThumpable() || var3 instanceof IsoWindow && !((IsoWindow)var3).isDestroyed() || var3 instanceof IsoDoor && !((IsoDoor)var3).isDestroyed() && !((IsoDoor)var3).open) && (this.stateMachine.getCurrent() == PathFindState.instance() || this.stateMachine.getCurrent() == LungeState.instance() || this.stateMachine.getCurrent() == WalkTowardState.instance()) && !this.Ghost) {
               int var5 = var2.getX() - this.current.getX();
               int var6 = var2.getY() - this.current.getY();
               IsoDirections var7 = IsoDirections.N;
               if (var5 < 0 && Math.abs(var5) > Math.abs(var6)) {
                  var7 = IsoDirections.S;
               }

               if (var5 < 0 && Math.abs(var5) <= Math.abs(var6)) {
                  var7 = IsoDirections.SW;
               }

               if (var5 > 0 && Math.abs(var5) > Math.abs(var6)) {
                  var7 = IsoDirections.W;
               }

               if (var5 > 0 && Math.abs(var5) <= Math.abs(var6)) {
                  var7 = IsoDirections.SE;
               }

               if (var6 < 0 && Math.abs(var5) < Math.abs(var6)) {
                  var7 = IsoDirections.N;
               }

               if (var6 < 0 && Math.abs(var5) >= Math.abs(var6)) {
                  var7 = IsoDirections.NW;
               }

               if (var6 > 0 && Math.abs(var5) < Math.abs(var6)) {
                  var7 = IsoDirections.E;
               }

               if (var6 > 0 && Math.abs(var5) >= Math.abs(var6)) {
                  var7 = IsoDirections.NE;
               }

               if (this.getDir() == var7) {
                  if (!SandboxOptions.instance.Lore.ThumpNoChasing.getValue() && this.target == null && !this.chasingSound) {
                     this.stateMachine.changeState(ZombieStandState.instance());
                  } else {
                     this.setThumpTarget((Thumpable)var3);
                     this.path = null;
                     this.stateMachine.changeState(ThumpState.instance());
                  }
               }

               return true;
            }
         }

         State var4 = this.getCurrentState();
         if (var3 != null && !this.bCrawling && IsoWindowFrame.isWindowFrame(var3) && (var4 == PathFindState.instance() || var4 == LungeState.instance() || var4 == WalkTowardState.instance())) {
            this.StateMachineParams.clear();
            this.StateMachineParams.put(0, var3);
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

   public Path FindPath(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.getCurrentSquare() == null) {
         return null;
      } else {
         Path var7 = new Path();
         int var8 = 20;
         int var9 = var1;
         int var10 = var2;
         int var11 = var3;
         IsoDirections var12 = IsoDirections.Max;
         this.doneGrids.clear();
         this.doneGrids.add(this.getCurrentSquare());

         IsoGridSquare var14;
         while(var8 > 0) {
            this.choiceGrids.clear();
            if (var9 == 88 && var10 == 23) {
               boolean var13 = false;
            }

            float var25 = 1.0E7F;
            var14 = null;
            boolean var15 = false;
            boolean var16 = false;
            boolean var17 = false;

            int var18;
            int var19;
            for(var18 = -1; var18 <= 1; ++var18) {
               for(var19 = -1; var19 <= 1; ++var19) {
                  for(int var20 = -1; var20 <= 1; ++var20) {
                     if ((var18 != 0 || var19 != 0 || var20 != 0) && !this.getCell().blocked(this, var9 + var18, var10 + var19, var11 + var20, var9, var10, var11)) {
                        IsoGridSquare var21 = this.getCell().getGridSquare(var9 + var18, var10 + var19, var11 + var20);
                        if (var21 != null && !this.doneGrids.contains(var21)) {
                           this.choiceGrids.add(var21);
                        }
                     }
                  }
               }
            }

            var18 = var9;
            var19 = var10;
            if (var12 == IsoDirections.N || var12 == IsoDirections.NE || var12 == IsoDirections.NW) {
               var19 = var10 - 1;
            }

            if (var12 == IsoDirections.S || var12 == IsoDirections.SE || var12 == IsoDirections.SW) {
               ++var19;
            }

            if (var12 == IsoDirections.E || var12 == IsoDirections.NE || var12 == IsoDirections.SE) {
               var18 = var9 + 1;
            }

            if (var12 == IsoDirections.W || var12 == IsoDirections.NW || var12 == IsoDirections.SW) {
               --var18;
            }

            float var27 = IsoUtils.DistanceManhattenSquare((float)var18, (float)var19, (float)var4, (float)var5);
            if (var12 != IsoDirections.Max) {
               var14 = this.getCell().getGridSquare(var18, var19, var11);
            }

            ArrayList var28 = new ArrayList();
            if (var14 == null) {
               var27 = 1000000.0F;
            } else {
               var28.add(var14);
            }

            for(int var22 = 0; var22 < this.choiceGrids.size(); ++var22) {
               IsoGridSquare var23 = (IsoGridSquare)this.choiceGrids.get(var22);
               float var24 = IsoUtils.DistanceManhatten((float)var23.getX(), (float)var23.getY(), (float)var4, (float)var5, (float)var23.getZ(), (float)var6);
               if (var24 < var27 && var23 != var14) {
                  var28.add(var23);
               }
            }

            if (!var28.isEmpty()) {
               var14 = (IsoGridSquare)var28.get(Rand.Next(var28.size()));
            }

            var28.clear();
            --var8;
            if (var14 == null) {
               break;
            }

            this.doneGrids.add(var14);
            if (var14.getX() > var9) {
               if (var14.getY() < var10) {
                  var12 = IsoDirections.NE;
               } else if (var14.getY() < var10) {
                  var12 = IsoDirections.SE;
               } else {
                  var12 = IsoDirections.E;
               }
            } else if (var14.getX() < var9) {
               if (var14.getY() < var10) {
                  var12 = IsoDirections.NW;
               } else if (var14.getY() < var10) {
                  var12 = IsoDirections.SW;
               } else {
                  var12 = IsoDirections.W;
               }
            } else if (var14.getY() < var10) {
               var12 = IsoDirections.N;
            } else if (var14.getY() < var10) {
               var12 = IsoDirections.S;
            }

            var9 = var14.getX();
            var10 = var14.getY();
            var11 = var14.getZ();
            if (var9 == var4 && var10 == var5 && var11 == var6) {
               break;
            }
         }

         for(int var26 = 0; var26 < this.doneGrids.size(); ++var26) {
            var14 = (IsoGridSquare)this.doneGrids.get(var26);
            if (var14 != null) {
               var7.appendStep(var14.getX(), var14.getY(), var14.getZ());
            }
         }

         return var7;
      }
   }

   public void updateFrameSpeed() {
      move.x = this.getNx() - this.getLx();
      move.y = this.getNy() - this.getLy();
      float var1 = 1.0F - move.getLength() / 0.08F;
   }

   public void DoZombieInventory() {
      if (!this.isReanimatedPlayer()) {
         this.getInventory().removeAllItems();
         this.getInventory().setSourceGrid(this.getCurrentSquare());
         this.Dressup(this.descriptor);
         if (!GameClient.bClient) {
            IsoBuilding var1 = this.getCurrentBuilding();
            if (var1 != null && var1.getDef() != null && var1.getDef().getKeyId() != -1 && Rand.Next(4) == 0) {
               String var2 = "Base.Key" + (Rand.Next(5) + 1);
               InventoryItem var3 = this.inventory.AddItem(var2);
               var3.setKeyId(var1.getDef().getKeyId());
            }

            if (this.itemsToSpawnAtDeath != null && !this.itemsToSpawnAtDeath.isEmpty()) {
               for(int var4 = 0; var4 < this.itemsToSpawnAtDeath.size(); ++var4) {
                  this.inventory.addItem((InventoryItem)this.itemsToSpawnAtDeath.get(var4));
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
      } else if (this.speedType != 3) {
         this.bLunger = true;
         this.speedMod = 0.85F;
         this.walkVariant = this.walkVariant + "2";
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         this.def.setFrameSpeedPerFrame(0.24F);
         var10000 = this.def;
         var10000.AnimFrameIncrease *= this.speedMod;
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

      IsoSpriteInstance var10000;
      if (this.bCrawling) {
         this.speedMod = 0.3F;
         this.speedMod += (float)Rand.Next(1500) / 10000.0F;
         var10000 = this.def;
         var10000.AnimFrameIncrease *= 0.8F;
      } else if (Rand.Next(3) == 0 && SandboxOptions.instance.Lore.Speed.getValue() != 3 && this.speedType != 3) {
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

      if (IsoWorld.instance.getGlobalTemperature() < 13.0F) {
      }

      this.PathSpeed = baseSpeed * this.speedMod;
      this.wanderSpeed = this.PathSpeed;
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

      if (IsoWorld.instance.getGlobalTemperature() < 13.0F) {
      }

      this.PathSpeed = baseSpeed * this.speedMod;
      this.wanderSpeed = this.PathSpeed;
   }

   public boolean isFakeDead() {
      return this.bFakeDead;
   }

   public void setFakeDead(boolean var1) {
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
      this.target = var2;
      if (this.Health <= 0.0F && !this.bDead) {
         this.DoZombieInventory();
         this.bDead = true;
      }

      this.TimeSinceSeenFlesh = 0.0F;
   }

   protected void DoDeathSilence(HandWeapon var1, IsoGameCharacter var2) {
      if (this.Health <= 0.0F && !this.bDead) {
         this.DoZombieInventory();
         this.bDead = true;
      }

      super.DoDeathSilence(var1, var2);
   }

   public void Hit(BaseVehicle var1, float var2, float var3, Vector2 var4) {
      this.AttackedBy = var1.getDriver();
      this.setHitDir(var4);
      this.setHitForce(var2 * 0.15F);
      int var5 = (new Float(var2 * 6.0F)).intValue();
      this.target = var1.getCharacter(0);
      if (var3 > 0.0F) {
         if (Rand.Next(100) <= var5) {
            if (Rand.Next(8) == 0) {
               this.setFakeDead(true);
            }

            this.getStateMachine().changeState(StaggerBackDieState.instance());
         } else {
            this.getStateMachine().changeState(StaggerBackState.instance());
         }
      } else if (var2 < 3.0F) {
         if (Rand.Next(100) <= var5) {
            if (Rand.Next(8) == 0) {
               this.setFakeDead(true);
            }

            this.getStateMachine().changeState(StaggerBackDieState.instance());
         } else {
            this.getStateMachine().changeState(StaggerBackState.instance());
         }
      } else if (var2 < 10.0F) {
         if (Rand.Next(8) == 0) {
            this.setFakeDead(true);
         }

         this.getStateMachine().changeState(StaggerBackDieState.instance());
      } else {
         this.DoZombieInventory();
         this.Kill(var1.getCharacter(0));
      }

      if (!((float)Rand.Next(10) > var2)) {
         float var6 = 0.6F;
         if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            int var7 = Rand.Next(4, 10);
            if (var7 < 1) {
               var7 = 1;
            }

            if (Core.bLastStand) {
               var7 *= 3;
            }

            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 2:
               var7 /= 2;
            case 3:
            default:
               break;
            case 4:
               var7 *= 2;
               break;
            case 5:
               var7 *= 5;
            }

            for(int var8 = 0; var8 < var7; ++var8) {
               this.splatBlood(3, 0.3F);
            }
         }

         if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            this.splatBloodFloorBig(0.3F);
         }

         if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
            tempo.x = this.getHitDir().x;
            tempo.y = this.getHitDir().y;
            byte var12 = 3;
            byte var11 = 0;
            byte var9 = 1;
            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 1:
               var9 = 0;
               break;
            case 2:
               var9 = 1;
               var12 = 5;
               var11 = 2;
            case 3:
            default:
               break;
            case 4:
               var9 = 3;
               var12 = 2;
               break;
            case 5:
               var9 = 10;
               var12 = 0;
            }

            for(int var10 = 0; var10 < var9; ++var10) {
               if (Rand.Next(this.isCloseKilled() ? 8 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 1.5F, this.getHitDir().y * 1.5F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 1.8F, this.getHitDir().y * 1.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 8 : var12) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 1.9F, this.getHitDir().y * 1.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var11) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 3.9F, this.getHitDir().y * 3.9F);
               }

               if (Rand.Next(this.isCloseKilled() ? 4 : var11) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 3.8F, this.getHitDir().y * 3.8F);
               }

               if (Rand.Next(this.isCloseKilled() ? 9 : 6) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, this.getCell(), this.getX(), this.getY(), this.getZ() + var6, this.getHitDir().x * 0.8F, this.getHitDir().y * 0.8F);
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

   public void setReanimatedPlayer(boolean var1) {
      this.bReanimatedPlayer = var1;
   }

   public void useDescriptor(SurvivorDesc var1, int var2) {
      if (var1 != null && var1 != this.descriptor) {
         this.descriptor = var1;
         this.palette = var2;
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
      ImmortalTutorialZombie = var1 ? this : null;
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
      } else {
         float var3;
         if (!this.isOnFloor() || !this.bCrawling && (this.legsSprite.CurrentAnim == null || !this.legsSprite.CurrentAnim.name.equals("ZombieDeath"))) {
            float var5 = var1.jniLinearVelocity.x;
            var3 = var1.jniLinearVelocity.z;
            if (GameServer.bServer) {
               var5 = var1.netLinearVelocity.x;
               var3 = var1.netLinearVelocity.z;
            }

            float var4 = (float)Math.sqrt((double)(var5 * var5 + var3 * var3));
            if (var1.isEngineRunning() && var4 > 0.05F && var1.testCollisionWithCharacter(this, 0.3F) != null) {
               var1.hitCharacter(this);
               super.update();
               return true;
            } else {
               return false;
            }
         } else {
            int var2 = var1.isEngineRunning() ? var1.testCollisionWithProneCharacter(this, this.angle.x, this.angle.y, true) : 0;
            if (var2 > 0) {
               if (!this.emitter.isPlaying(this.hurtSound)) {
                  this.emitter.playSound(this.hurtSound);
               }

               this.AttackedBy = var1.getDriver();
               var3 = Math.min(GameTime.getInstance().getMultiplier() / 1.6F, 30.0F / (float)PerformanceSettings.LockFPS * 2.0F);
               this.hitConsequences(IsoPlayer.instance.bareHands, var1.getDriver(), false, 0.25F * (float)var2 * var3, true);
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

      // $FF: synthetic method
      FloodFill(Object var1) {
         this();
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
   }
}
