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

public class ClimbOverFenceState2 extends State {
   static ClimbOverFenceState2 _instance = new ClimbOverFenceState2();

   public static ClimbOverFenceState2 instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setPath((Path)null);
      IsoDirections var2 = (IsoDirections)var1.StateMachineParams.get(0);
      float var3 = 0.0F;
      float var4 = 0.0F;
      if (var2 == IsoDirections.N) {
         var1.y = (float)((double)var1.y - 1.182D);
         var1.ny = var1.y;
         var4 = 0.5F;
      } else if (var2 == IsoDirections.S) {
         var1.y = (float)((double)var1.y + 0.818D);
         var1.ny = var1.y;
         var4 = -0.5F;
      }

      if (var2 == IsoDirections.W) {
         var1.x = (float)((double)var1.x - 1.182D);
         var1.nx = var1.x;
         var3 = 0.5F;
      } else if (var2 == IsoDirections.E) {
         var1.x = (float)((double)var1.x + 0.818D);
         var1.nx = var1.x;
         var3 = -0.5F;
      }

      var1.PlayAnimUnlooped("Climb_WindowB");
      if (var1.hasActiveModel()) {
         var1.sprite.modelSlot.DisableBlendingFrom("Climb_WindowA");
      }

      if (var1.getPath() != null) {
         var1.setPathIndex(var1.getPathIndex() + 1);
      }

      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = var3;
         IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = var4;
         IsoCamera.update();
      }

      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare((int)var1.x, (int)var1.y, (int)var1.z);
      if (!(var1 instanceof IsoZombie) && var1.canClimbDownSheetRope(var5)) {
         var1.setbClimbing(true);
      }

   }

   public void execute(IsoGameCharacter var1) {
      IsoDirections var2 = (IsoDirections)var1.StateMachineParams.get(0);
      var1.PlayAnimUnlooped("Climb_WindowB");
      var1.getSpriteDef().AnimFrameIncrease = 0.23F;
      if (var1 instanceof IsoZombie) {
         IsoSpriteInstance var10000 = var1.getSpriteDef();
         var10000.AnimFrameIncrease *= 0.8F;
      }

      float var3 = 0.5F;
      float var4 = 0.5F;
      float var5 = var1.x - (float)((int)var1.x);
      float var6 = var1.y - (float)((int)var1.y);
      float var7;
      if (var5 != var3) {
         var7 = (var3 - var5) / 4.0F;
         var5 += var7;
         var1.x = (float)((int)var1.x) + var5;
      }

      if (var6 != var4) {
         var7 = (var4 - var6) / 4.0F;
         var6 += var7;
         var1.y = (float)((int)var1.y) + var6;
      }

      var1.nx = var1.x;
      var1.ny = var1.y;
      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         var7 = 0.0F;
         float var8 = 0.0F;
         if (var1.dir == IsoDirections.N) {
            var8 = 0.5F;
         } else if (var1.dir == IsoDirections.S) {
            var8 = -0.5F;
         } else if (var1.dir == IsoDirections.W) {
            var7 = 0.5F;
         } else if (var1.dir == IsoDirections.E) {
            var7 = -0.5F;
         }

         float var9 = var1.getSpriteDef().Frame / (float)(var1.getSprite().CurrentAnim.Frames.size() - 1);
         if (var9 > 1.0F) {
            var9 = 1.0F;
         }

         var9 = 1.0F - var9;
         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = var7 * var9;
         IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = var8 * var9;
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
               var1.getStateMachine().changeState(var1.getDefaultState());
            } else {
               var1.changeState((State)var1.StateMachineParams.get(1));
            }

            if (var1.getStateMachine().getCurrent() == instance()) {
               var1.getStateMachine().changeState(var1.getDefaultState());
            }

            var1.setCollidable(true);
            var1.StateMachineParams.clear();
         }

      }
   }

   public void exit(IsoGameCharacter var1) {
      var1.setbClimbing(false);
      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = 0.0F;
      }

      var1.setCollidable(true);
      var1.setbClimbing(false);
   }
}
