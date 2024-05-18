package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.SafeHouse;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;

public class ClimbThroughWindowState extends State {
   static ClimbThroughWindowState _instance = new ClimbThroughWindowState();
   static boolean first = true;

   public static ClimbThroughWindowState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      first = true;
      if (var1.StateMachineParams.get(0) instanceof IsoWindow) {
         IsoWindow var2 = (IsoWindow)var1.StateMachineParams.get(0);
         if (var2.isDestroyed() && !var2.isGlassRemoved() && Rand.Next(2) == 0) {
            var1.getBodyDamage().setScratchedWindow();
         }
      } else if (var1.StateMachineParams.get(0) instanceof IsoThumpable) {
         IsoThumpable var3 = (IsoThumpable)var1.StateMachineParams.get(0);
         if (var3.isDestroyed() && Rand.Next(15) == 0) {
            var1.getBodyDamage().setScratchedWindow();
         }
      }

   }

   public void execute(IsoGameCharacter var1) {
      boolean var2 = true;
      int var3 = 0;
      int var4 = 0;
      if (var1.StateMachineParams.get(0) instanceof IsoWindow) {
         IsoWindow var5 = (IsoWindow)var1.StateMachineParams.get(0);
         if (GameClient.bClient && var1 instanceof IsoPlayer && SafeHouse.isSafeHouse(var5.getOppositeSquare(), ((IsoPlayer)var1).getUsername(), true) != null && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
            var1.getStateMachine().changeState(var1.getDefaultState());
         }

         var2 = var5.north;
         var3 = var5.getSquare().getX();
         var4 = var5.getSquare().getY();
      } else if (var1.StateMachineParams.get(0) instanceof IsoThumpable) {
         IsoThumpable var12 = (IsoThumpable)var1.StateMachineParams.get(0);
         Object var6 = null;
         var2 = var12.north;
         if (GameClient.bClient && var1 instanceof IsoPlayer && SafeHouse.isSafeHouse(var12.getInsideSquare(), ((IsoPlayer)var1).getUsername(), true) != null && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
            var1.getStateMachine().changeState(var1.getDefaultState());
         }

         var3 = var12.getSquare().getX();
         var4 = var12.getSquare().getY();
      } else if (var1.StateMachineParams.get(0) instanceof IsoObject) {
         IsoObject var13 = (IsoObject)var1.StateMachineParams.get(0);
         var2 = IsoWindowFrame.isWindowFrame(var13, true);
         var3 = var13.getSquare().getX();
         var4 = var13.getSquare().getY();
      }

      float var14 = 0.5F;
      float var15 = 0.5F;
      var1.PlayAnimUnlooped("Climb_WindowA");
      var1.getSpriteDef().AnimFrameIncrease = 0.33F;
      var1.getLegsSprite().Animate = true;
      if (var1.StateMachineParams.get(1) != null && first && var1.StateMachineParams.get(1) instanceof Integer) {
         var1.getSpriteDef().Frame = (float)(Integer)var1.StateMachineParams.get(1);
         first = false;
      }

      if (var1 instanceof IsoZombie) {
         IsoSpriteInstance var10000 = var1.getSpriteDef();
         var10000.AnimFrameIncrease *= 0.8F;
      }

      var1.setCollidable(false);
      float var7 = 0.0F;
      float var8 = 0.0F;
      if (var2) {
         if ((float)var4 < var1.getY()) {
            var1.setDir(IsoDirections.N);
            var14 = (float)var3 + 0.5F;
            var15 = (float)var4 + 0.682F;
            var8 = -0.682F;
         } else {
            var1.setDir(IsoDirections.S);
            var14 = (float)var3 + 0.5F;
            var15 = (float)(var4 - 1) + 0.682F;
            var8 = 0.31800002F;
         }
      } else if ((float)var3 < var1.getX()) {
         var1.setDir(IsoDirections.W);
         var15 = (float)var4 + 0.5F;
         var14 = (float)var3 + 0.682F;
         var7 = -0.682F;
      } else {
         var1.setDir(IsoDirections.E);
         var15 = (float)var4 + 0.5F;
         var14 = (float)(var3 - 1) + 0.682F;
         var7 = 0.31800002F;
      }

      float var9;
      if (var1.x != var14) {
         var9 = (var14 - var1.x) / 4.0F;
         var1.x += var9;
      }

      if (var1.y != var15) {
         var9 = (var15 - var1.y) / 4.0F;
         var1.y += var9;
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

         IsoCamera.DeferedX[((IsoPlayer)var1).getPlayerNum()] = var7 * var9;
         IsoCamera.DeferedY[((IsoPlayer)var1).getPlayerNum()] = var8 * var9;
      }

      if (var1.getSpriteDef().Finished) {
         if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isLocalPlayer()) {
            ((IsoPlayer)var1).dirtyRecalcGridStackTime = 20.0F;
         }

         var1.x = var14;
         var1.y = var15;
         int var16 = (int)var1.x;
         int var10 = (int)var1.y;
         switch(var1.dir) {
         case N:
            --var10;
            break;
         case S:
            ++var10;
            break;
         case W:
            --var16;
            break;
         case E:
            ++var16;
         }

         IsoChunk var11 = GameServer.bServer ? ServerMap.instance.getChunk(var16 / 10, var10 / 10) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(var16, var10, (int)var1.z);
         if (var11 == null) {
            var1.setDefaultState();
            return;
         }

         var1.StateMachineParams.put(1, var1.getStateMachine().getPrevious());
         var1.getStateMachine().changeState(ClimbThroughWindowState2.instance());
      }

   }

   public void exit(IsoGameCharacter var1) {
      if (var1.StateMachineParams.get(0) instanceof IsoThumpable && var1 instanceof IsoPlayer && ((IsoThumpable)var1.StateMachineParams.get(0)).getName().equals("Barbed Fence") && Rand.Next(101) > 75) {
         var1.getBodyDamage().setScratchedWindow();
      }

      var1.setIgnoreMovementForDirection(false);
   }
}
