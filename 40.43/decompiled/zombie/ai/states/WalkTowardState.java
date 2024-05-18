package zombie.ai.states;

import org.joml.Vector3f;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;

public class WalkTowardState extends State {
   static WalkTowardState _instance = new WalkTowardState();
   Vector2 temp = new Vector2();
   int turnTimer = 0;
   float previousX;
   float previousY = 0.0F;

   public static WalkTowardState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      this.previousX = 0.0F;
      this.previousY = 0.0F;
      this.turnTimer = 0;
      if (var1 instanceof IsoZombie && ((IsoZombie)var1).isUseless()) {
         var1.getStateMachine().changeState(ZombieStandState.instance());
      }

   }

   public void execute(IsoGameCharacter var1) {
      if (var1 instanceof IsoZombie) {
         if (SandboxOptions.instance.Lore.ActiveOnly.getValue() > 1) {
            if ((SandboxOptions.instance.Lore.ActiveOnly.getValue() != 2 || GameTime.instance.getHour() < 20 && GameTime.instance.getHour() > 8) && (SandboxOptions.instance.Lore.ActiveOnly.getValue() != 3 || GameTime.instance.getHour() <= 8 || GameTime.instance.getHour() >= 20)) {
               ((IsoZombie)var1).walkVariant = "ZombieWalk1";
               ((IsoZombie)var1).bRunning = false;
               ((IsoZombie)var1).speedType = 3;
               ((IsoZombie)var1).speedMod = 0.1F;
               ((IsoZombie)var1).def.setFrameSpeedPerFrame(0.24F);
               IsoSpriteInstance var10000 = ((IsoZombie)var1).def;
               var10000.AnimFrameIncrease *= ((IsoZombie)var1).speedMod;
               ((IsoZombie)var1).inactive = true;
            } else if (((IsoZombie)var1).speedMod == 0.1F) {
               ((IsoZombie)var1).inactive = false;
               if (SandboxOptions.instance.Lore.Speed.getValue() != 4) {
                  ((IsoZombie)var1).changeSpeed(SandboxOptions.instance.Lore.Speed.getValue());
               } else {
                  ((IsoZombie)var1).changeSpeed(Rand.Next(1, 4));
               }
            }
         }

         if (!((IsoZombie)var1).bCrawling) {
            var1.setOnFloor(false);
         }

         IsoZombie var2 = (IsoZombie)var1;
         if (this.previousX == var1.getX() && this.previousY == var1.getY()) {
         }

         this.previousX = var1.getX();
         this.previousY = var1.getY();
         var2.DoNetworkDirty();
         if (var2.target != null) {
            var2.getPathFindBehavior2().pathToCharacter((IsoGameCharacter)var2.target);
            if (var2.target instanceof IsoGameCharacter && ((IsoGameCharacter)var2.target).getVehicle() != null && var2.DistToSquared(var2.target) < 16.0F) {
               Vector3f var3 = ((IsoGameCharacter)var2.target).getVehicle().chooseBestAttackPosition((IsoGameCharacter)var2.target, var2);
               if (var3 != null && (Math.abs(var1.x - var2.getPathFindBehavior2().getTargetX()) > 0.1F || Math.abs(var1.y - var2.getPathFindBehavior2().getTargetY()) > 0.1F)) {
                  var2.changeState(PathFindState.instance());
                  return;
               }
            }
         }

         if (var1.getPathTargetX() == (int)var1.getX() && var1.getPathTargetY() == (int)var1.getY() && var1 instanceof IsoZombie) {
            if (((IsoZombie)var1).target == null) {
               var2.getStateMachine().changeState(ZombieStandState.instance());
               return;
            }

            if ((int)((IsoZombie)var1).target.getZ() != (int)var1.getZ()) {
               var2.changeState(ZombieStandState.instance());
               return;
            }
         }

         if (var1.isCollidedThisFrame() || var1.isCollidedWithVehicle()) {
            var2.AllowRepathDelay = 0.0F;
            var2.pathToLocation(var1.getPathTargetX(), var1.getPathTargetY(), var1.getPathTargetZ());
            if (var2.getCurrentState() == instance()) {
               var2.changeState(PathFindState.instance());
            }

            return;
         }

         float var10 = IsoZombie.baseSpeed;
         Vector2 var11;
         if (var1 instanceof IsoZombie) {
            var2.setIgnoreMovementForDirection(false);
            float var4 = (float)((var1.getID() + var2.ZombieID) % 20) / 10.0F - 1.0F;
            float var5 = (float)((var2.getID() + var2.ZombieID) % 20) / 10.0F - 1.0F;
            this.temp.x = var2.getPathFindBehavior2().getTargetX();
            this.temp.y = var2.getPathFindBehavior2().getTargetY();
            var11 = this.temp;
            var11.x -= var2.getX();
            var11 = this.temp;
            var11.y -= var2.getY();
            float var6 = this.temp.getLength();
            float var7 = var6 / 2.0F;
            if (var7 > 4.0F) {
               var7 = 4.0F;
            }

            if (!GameServer.bServer) {
               var11 = this.temp;
               var11.x += var2.getX();
               var11 = this.temp;
               var11.y += var2.getY();
               var11 = this.temp;
               var11.x += var4 * var7;
               var11 = this.temp;
               var11.y += var5 * var7;
               var11 = this.temp;
               var11.x -= var2.getX();
               var11 = this.temp;
               var11.y -= var2.getY();
               var2.bRunning = false;
            }

            var2.bRunning = false;
            var2.reqMovement.normalize();
            if (!(Math.abs(this.temp.x - var2.reqMovement.x) > 1.0E-4F) && Math.abs(this.temp.y - var2.reqMovement.y) > 1.0E-4F) {
            }

            var2.getZombieWalkTowardSpeed(var10, var6, this.temp);
            var2.Move(this.temp);
            var2.updateFrameSpeed();
            this.temp.normalize();
            if (!var2.bCrawling) {
               var2.setDir(IsoDirections.fromAngle(this.temp));
            } else {
               ++this.turnTimer;
               if (this.turnTimer > 3) {
                  IsoDirections var8 = var1.dir;
                  IsoDirections var9 = IsoDirections.fromAngle(this.temp);
                  if (var8 != var9 && var1.getStateMachine().getCurrent() != CrawlingZombieTurnState.instance()) {
                     var1.StateMachineParams.clear();
                     var1.StateMachineParams.put(0, var9);
                     var1.getStateMachine().Lock = false;
                     var1.getStateMachine().changeState(CrawlingZombieTurnState.instance());
                  }

                  this.turnTimer = 0;
               }
            }
         } else {
            this.temp.x = (float)var1.getPathTargetX() + 0.5F;
            this.temp.y = (float)var1.getPathTargetY() + 0.5F;
            var11 = this.temp;
            var11.x -= var1.getX();
            var11 = this.temp;
            var11.y -= var1.getY();
            this.temp.setLength(var1.getPathSpeed());
            var1.Move(this.temp);
            var1.angle.x = this.temp.x;
            var1.angle.y = this.temp.y;
            var1.angle.normalize();
         }
      }

   }
}
