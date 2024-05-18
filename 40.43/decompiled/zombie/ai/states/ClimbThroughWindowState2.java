package zombie.ai.states;

import zombie.ai.State;
import zombie.ai.astar.Path;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;

public class ClimbThroughWindowState2 extends State {
   static ClimbThroughWindowState2 _instance = new ClimbThroughWindowState2();

   public static ClimbThroughWindowState2 instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setPath((Path)null);
      float var2 = 0.0F;
      float var3 = 0.0F;
      if (var1.dir == IsoDirections.N) {
         var1.y = (float)((double)var1.y - 1.182D);
         var3 = 0.5F;
      } else if (var1.dir == IsoDirections.S) {
         var1.y = (float)((double)var1.y + 0.818D);
         var3 = -0.5F;
      }

      if (var1.dir == IsoDirections.W) {
         var1.x = (float)((double)var1.x - 1.182D);
         var2 = 0.5F;
      } else if (var1.dir == IsoDirections.E) {
         var1.x = (float)((double)var1.x + 0.818D);
         var2 = -0.5F;
      }

      var1.nx = var1.x;
      var1.ny = var1.y;
      IsoWorld.instance.CurrentCell.getOrCreateGridSquare((double)var1.nx, (double)var1.ny, (double)var1.getZ());
      var1.PlayAnimUnlooped("Climb_WindowB");
      if (var1.hasActiveModel()) {
         var1.sprite.modelSlot.DisableBlendingFrom("Climb_WindowA");
      }

      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = var2;
         IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = var3;
         IsoCamera.update();
      }

      IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare((int)var1.x, (int)var1.y, (int)var1.z);
      if (!(var1 instanceof IsoZombie) && var1.canClimbDownSheetRope(var4)) {
         var1.setbClimbing(true);
      }

   }

   public void execute(IsoGameCharacter var1) {
      var1.PlayAnimUnlooped("Climb_WindowB");
      var1.getSpriteDef().AnimFrameIncrease = 0.23F;
      if (var1 instanceof IsoZombie) {
         IsoSpriteInstance var10000 = var1.getSpriteDef();
         var10000.AnimFrameIncrease *= 0.8F;
      }

      float var2 = 0.5F;
      float var3 = 0.5F;
      float var4 = var1.x - (float)((int)var1.x);
      float var5 = var1.y - (float)((int)var1.y);
      float var6;
      if (var4 != var2) {
         var6 = (var2 - var4) / 4.0F;
         var4 += var6;
         var1.x = (float)((int)var1.x) + var4;
      }

      if (var5 != var3) {
         var6 = (var3 - var5) / 4.0F;
         var5 += var6;
         var1.y = (float)((int)var1.y) + var5;
      }

      var1.nx = var1.x;
      var1.ny = var1.y;
      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         var6 = 0.0F;
         float var7 = 0.0F;
         if (var1.dir == IsoDirections.N) {
            var7 = 0.5F;
         } else if (var1.dir == IsoDirections.S) {
            var7 = -0.5F;
         } else if (var1.dir == IsoDirections.W) {
            var6 = 0.5F;
         } else if (var1.dir == IsoDirections.E) {
            var6 = -0.5F;
         }

         float var8 = var1.getSpriteDef().Frame / (float)(var1.getSprite().CurrentAnim.Frames.size() - 1);
         if (var8 > 1.0F) {
            var8 = 1.0F;
         }

         var8 = 1.0F - var8;
         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = var6 * var8;
         IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = var7 * var8;
      }

      if (!(var1 instanceof IsoZombie) && (int)var1.getSpriteDef().Frame >= 6 && var1.canClimbDownSheetRope(var1.getCurrentSquare())) {
         var1.climbDownSheetRope();
         if (var1.getStateMachine().getCurrent() == instance()) {
            var1.getStateMachine().changeState(var1.getDefaultState());
         }

      } else {
         if (var1.getSpriteDef().Finished) {
            if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
               IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = 0.0F;
            }

            if (var1.StateMachineParams.get(1) != PathFindState.instance() && var1.StateMachineParams.get(1) != WalkTowardState.instance()) {
               if (!(var1 instanceof IsoZombie) || !((IsoZombie)var1).WanderFromWindow()) {
                  var1.getStateMachine().changeState(var1.getDefaultState());
               }
            } else {
               var1.changeState((State)var1.StateMachineParams.get(1));
            }

            if (var1.getStateMachine().getCurrent() == instance()) {
               var1.getStateMachine().changeState(var1.getDefaultState());
            }

            var1.setbClimbing(false);
            var1.setCollidable(true);
            var1.StateMachineParams.clear();
         }

      }
   }

   public void exit(IsoGameCharacter var1) {
      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = 0.0F;
      }

      var1.setCollidable(true);
      var1.setbClimbing(false);
   }
}
