package zombie.ai.states;

import zombie.GameTime;
import zombie.SoundManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.ZombieThumpManager;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;

public class ThumpState extends State {
   static ThumpState _instance = new ThumpState();

   public static ThumpState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      ((IsoZombie)var1).thumpFrame = -1;
      var1.setIgnoreMovementForDirection(true);
   }

   public void execute(IsoGameCharacter var1) {
      Thumpable var2 = var1.getThumpTarget();
      var1.PlayAnim("ZombieDoor");
      var1.def.setFrameSpeedPerFrame(0.15F);
      if (var2 instanceof IsoObject) {
         var1.setIgnoreMovementForDirection(false);
         var1.faceThisObject((IsoObject)var2);
         var1.setIgnoreMovementForDirection(true);
      }

      boolean var3 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
      int var4 = (int)var1.def.getFrame();
      IsoZombie var5;
      if (var3 || ((IsoZombie)var1).thumpFrame < 5 && var4 >= 5) {
         var1.setTimeThumping(var1.getTimeThumping() + 1);
         if (((IsoZombie)var1).TimeSinceSeenFlesh < 5.0F) {
            var1.setTimeThumping(0);
         }

         var5 = (IsoZombie)var1;
         if (var5.target != null && var5.z == var5.target.z && var5.vectorToTarget.getLength() < 5.0F) {
            LosUtil.TestResults var6 = LosUtil.lineClear(var5.getCell(), (int)var5.x, (int)var5.y, (int)var5.z, (int)var5.target.getX(), (int)var5.target.getY(), (int)var5.target.getZ(), false);
            boolean var7 = LosUtil.lineClearCollideCount(var5, var5.getCell(), (int)var5.target.getX(), (int)var5.target.getY(), (int)var5.target.getZ(), (int)var5.x, (int)var5.y, (int)var5.z) == 0;
            if (var6 != LosUtil.TestResults.Blocked && var6 != LosUtil.TestResults.ClearThroughWindow && !var7) {
               var5.setTimeThumping(0);
               var5.AllowRepathDelay = 0.0F;
               var5.setDefaultState();
               return;
            }
         }

         int var10 = 1;
         if (var1.getCurrentSquare() != null) {
            var10 = var1.getCurrentSquare().getMovingObjects().size();
         }

         int var11 = 0;

         while(true) {
            if (var11 >= var10) {
               boolean var12 = GameServer.bServer || SoundManager.instance.isListenerInRange(var1.x, var1.y, 20.0F);
               if (var12 && !IsoPlayer.allPlayersAsleep()) {
                  if (!(var1.getThumpTarget() instanceof IsoDoor) && !(var1.getThumpTarget() instanceof IsoThumpable) && (!(var1.getThumpTarget() instanceof IsoWindow) || !var1.getThumpTarget().isDestroyed() && !((IsoWindow)var1.getThumpTarget()).IsOpen() && ((IsoWindow)var1.getThumpTarget()).getBarricadeForCharacter(var1) == null)) {
                     if (Rand.Next(3) == 0) {
                        if (!GameServer.bServer) {
                           ZombieThumpManager.instance.addCharacter((IsoZombie)var1);
                        }

                        ((IsoZombie)var1).thumpFlag = 2;
                     } else {
                        if (!GameServer.bServer) {
                           ZombieThumpManager.instance.addCharacter((IsoZombie)var1);
                        }

                        ((IsoZombie)var1).thumpFlag = 3;
                     }
                  } else {
                     String var14 = "ZombieThumpGeneric";
                     if (var1.getThumpTarget() instanceof IsoWindow && ((IsoWindow)var1.getThumpTarget()).getBarricadeForCharacter(var1) != null) {
                        IsoBarricade var16 = ((IsoWindow)var1.getThumpTarget()).getBarricadeForCharacter(var1);
                        if (var16.isMetal() || var16.isMetalBar()) {
                           var14 = "ZombieThumpMetal";
                        }
                     } else if (var1.getThumpTarget() instanceof IsoThumpable) {
                        IsoThumpable var8 = (IsoThumpable)var1.getThumpTarget();
                        var14 = var8.getThumpSound();
                        IsoBarricade var9 = var8.getBarricadeForCharacter(var1);
                        if (var9 != null && (var9.isMetal() || var9.isMetalBar())) {
                           var14 = "ZombieThumpMetal";
                        }
                     }

                     if ("ZombieThumpGeneric".equals(var14)) {
                        ((IsoZombie)var1).thumpFlag = 1;
                     } else if ("ZombieThumpMetal".equals(var14)) {
                        ((IsoZombie)var1).thumpFlag = 4;
                     } else {
                        ((IsoZombie)var1).thumpFlag = 1;
                     }

                     if (!GameServer.bServer) {
                        ZombieThumpManager.instance.addCharacter((IsoZombie)var1);
                     }
                  }
               }
               break;
            }

            if (var1.getThumpTarget() == null) {
               var1.setDefaultState();
               var1.setTimeThumping(0);
               return;
            }

            if (var1.getThumpTarget() instanceof IsoDoor && ((IsoDoor)((IsoDoor)var1.getThumpTarget())).open || var1.getThumpTarget() instanceof IsoThumpable && ((IsoThumpable)((IsoThumpable)var1.getThumpTarget())).open) {
               var1.setDefaultState();
               var1.setTimeThumping(0);
               return;
            }

            if (var1.getThumpTarget() instanceof IsoThumpable && !((IsoThumpable)((IsoThumpable)var1.getThumpTarget())).isThumpable()) {
               var1.getStateMachine().RevertToPrevious();
               var1.setTimeThumping(0);
               return;
            }

            var1.getThumpTarget().Thump(var1);
            ++var11;
         }
      }

      ((IsoZombie)var1).thumpFrame = var4;
      if (!(var2 instanceof IsoWindow) || ((IsoWindow)var2).canClimbThrough(var1)) {
         if (var2 == null || var2.isDestroyed() || var2 instanceof IsoObject && ((IsoObject)var2).getObjectIndex() == -1 || var2 instanceof IsoDoor && ((IsoDoor)var2).open || var2 instanceof IsoThumpable && ((IsoThumpable)var2).isDoor && ((IsoThumpable)var2).open || var2 instanceof IsoWindow && ((IsoWindow)var2).canClimbThrough(var1)) {
            var1.setThumpTarget((Thumpable)null);
            if (var2 instanceof IsoWindow && ((IsoWindow)var2).canClimbThrough(var1)) {
               var1.setTimeThumping(0);
               var1.StateMachineParams.put(0, var2);
               var1.changeState(ClimbThroughWindowState.instance());
               return;
            }

            var1.setTimeThumping(0);
            if (var1 instanceof IsoZombie) {
               var5 = (IsoZombie)var1;
               IsoGridSquare var17;
               IsoGridSquare var18;
               if (var2 instanceof IsoDoor && (((IsoDoor)var2).open || var2.isDestroyed())) {
                  IsoDoor var13 = (IsoDoor)var2;
                  var18 = var13.getSquare();
                  var17 = var13.getOppositeSquare();
                  if (this.lungeThroughDoor(var5, var18, var17)) {
                     return;
                  }
               }

               if (var2 instanceof IsoThumpable && ((IsoThumpable)var2).isDoor && (((IsoThumpable)var2).open || var2.isDestroyed())) {
                  IsoThumpable var15 = (IsoThumpable)var2;
                  var18 = var15.getSquare();
                  var17 = var15.getInsideSquare();
                  if (this.lungeThroughDoor(var5, var18, var17)) {
                     return;
                  }
               }

               if (var5.LastTargetSeenX != -1) {
                  var1.pathToLocation(var5.LastTargetSeenX, var5.LastTargetSeenY, var5.LastTargetSeenZ);
                  if (var1.getStateMachine().getCurrent() == WalkTowardState.instance() || var1.getStateMachine().getCurrent() == PathFindState.instance()) {
                     return;
                  }
               }
            }

            var1.setDefaultState();
         }

      }
   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }

   private IsoPlayer findPlayer(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var3; var6 <= var4; ++var6) {
         for(int var7 = var1; var7 <= var2; ++var7) {
            IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare(var7, var6, var5);
            if (var8 != null) {
               for(int var9 = 0; var9 < var8.getMovingObjects().size(); ++var9) {
                  IsoMovingObject var10 = (IsoMovingObject)var8.getMovingObjects().get(var9);
                  if (var10 instanceof IsoPlayer && !((IsoPlayer)var10).GhostMode) {
                     return (IsoPlayer)var10;
                  }
               }
            }
         }
      }

      return null;
   }

   private boolean lungeThroughDoor(IsoZombie var1, IsoGridSquare var2, IsoGridSquare var3) {
      if (var2 != null && var3 != null) {
         boolean var4 = var2.getY() > var3.getY();
         IsoGridSquare var5 = null;
         IsoPlayer var6 = null;
         if (var1.getCurrentSquare() == var2) {
            var5 = var3;
            if (var4) {
               var6 = this.findPlayer(var3.getX() - 1, var3.getX() + 1, var3.getY() - 1, var3.getY(), var3.getZ());
            } else {
               var6 = this.findPlayer(var3.getX() - 1, var3.getX(), var3.getY() - 1, var3.getY() + 1, var3.getZ());
            }
         } else if (var1.getCurrentSquare() == var3) {
            var5 = var2;
            if (var4) {
               var6 = this.findPlayer(var2.getX() - 1, var2.getX() + 1, var2.getY(), var2.getY() + 1, var2.getZ());
            } else {
               var6 = this.findPlayer(var2.getX(), var2.getX() + 1, var2.getY() - 1, var2.getY() + 1, var2.getZ());
            }
         }

         if (var6 != null && !LosUtil.lineClearCollide(var5.getX(), var5.getY(), var5.getZ(), (int)var6.getX(), (int)var6.getY(), (int)var6.getZ(), false)) {
            var1.target = var6;
            var1.vectorToTarget.x = var6.getX();
            var1.vectorToTarget.y = var6.getY();
            Vector2 var10000 = var1.vectorToTarget;
            var10000.x -= var1.getX();
            var10000 = var1.vectorToTarget;
            var10000.y -= var1.getY();
            var1.TimeSinceSeenFlesh = 0.0F;
            var1.setDefaultState();
            var1.Lunge();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static int getFastForwardDamageMultiplier() {
      GameTime var0 = GameTime.getInstance();
      if (GameServer.bServer) {
         return (int)(GameServer.bFastForward ? ServerOptions.instance.FastForwardMultiplier.getValue() / (double)var0.getDeltaMinutesPerDay() : 1.0D);
      } else if (GameClient.bClient) {
         return (int)(GameClient.bFastForward ? ServerOptions.instance.FastForwardMultiplier.getValue() / (double)var0.getDeltaMinutesPerDay() : 1.0D);
      } else {
         return IsoPlayer.allPlayersAsleep() ? (int)(200.0F * (30.0F / (float)PerformanceSettings.LockFPS) / 1.6F) : (int)var0.getTrueMultiplier();
      }
   }
}
