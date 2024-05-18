package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class ClimbOverFenceState extends State {
   static ClimbOverFenceState _instance = new ClimbOverFenceState();

   public static ClimbOverFenceState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
   }

   public void execute(IsoGameCharacter var1) {
      IsoDirections var2 = (IsoDirections)var1.StateMachineParams.get(0);
      float var3 = 0.5F;
      float var4 = 0.5F;
      var1.PlayAnimUnlooped("Climb_WindowA");
      var1.getSpriteDef().AnimFrameIncrease = 0.33F;
      var1.setAnimated(true);
      if (var1 instanceof IsoZombie) {
         IsoSpriteInstance var10000 = var1.getSpriteDef();
         var10000.AnimFrameIncrease *= 0.8F;
      }

      var1.setCollidable(false);
      float var5 = 0.0F;
      float var6 = 0.0F;
      if (var2 == IsoDirections.N) {
         var1.setDir(IsoDirections.N);
         var3 = 0.5F;
         var4 = 0.682F;
         var6 = -0.682F;
      } else if (var2 == IsoDirections.S) {
         var1.setDir(IsoDirections.S);
         var3 = 0.5F;
         var4 = 0.682F;
         var6 = 0.31800002F;
      } else if (var2 == IsoDirections.W) {
         var1.setDir(IsoDirections.W);
         var4 = 0.5F;
         var3 = 0.682F;
         var5 = -0.682F;
      } else if (var2 == IsoDirections.E) {
         var1.setDir(IsoDirections.E);
         var4 = 0.5F;
         var3 = 0.682F;
         var5 = 0.31800002F;
      }

      float var7 = var1.x - (float)((int)var1.x);
      float var8 = var1.y - (float)((int)var1.y);
      float var9;
      if (var7 != var3) {
         var9 = (var3 - var7) / 4.0F;
         var7 += var9;
         var1.x = (float)((int)var1.x) + var7;
      }

      if (var8 != var4) {
         var9 = (var4 - var8) / 4.0F;
         var8 += var9;
         var1.y = (float)((int)var1.y) + var8;
      }

      var1.nx = var1.x;
      var1.ny = var1.y;
      var1.reqMovement.set(0.0F, 0.0F);
      var1.setIgnoreMovementForDirection(true);
      if (var1 == IsoCamera.CamCharacter && var1 instanceof IsoPlayer) {
         var9 = var1.getSpriteDef().Frame / (float)(var1.getSprite().CurrentAnim.Frames.size() - 1);
         if (var9 > 1.0F) {
            var9 = 1.0F;
         }

         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = var5 * var9;
         IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = var6 * var9;
      }

      if (var1.getSpriteDef().Finished) {
         int var12 = (int)var1.x;
         int var10 = (int)var1.y;
         switch(var2) {
         case N:
            --var10;
            break;
         case S:
            ++var10;
            break;
         case W:
            --var12;
            break;
         case E:
            ++var12;
         }

         IsoChunk var11 = GameServer.bServer ? ServerMap.instance.getChunk(var12 / 10, var10 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var12, var10, (int)var1.z);
         if (var11 == null) {
            var1.setDefaultState();
            return;
         }

         if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isLocalPlayer()) {
            ((IsoPlayer)var1).dirtyRecalcGridStackTime = 20.0F;
         }

         var1.StateMachineParams.put(1, var1.getStateMachine().getPrevious());
         var1.getStateMachine().changeState(ClimbOverFenceState2.instance());
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }
}
