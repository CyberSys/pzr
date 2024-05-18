package zombie.network;

import java.nio.ByteBuffer;
import java.util.Stack;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoAnim;

public class PlayerNetState {
   public long time;
   public byte id;
   public byte dir;
   public float x;
   public float y;
   public float z;
   public float mx;
   public float my;
   public byte remoteState;
   public byte anim;
   public byte frame;
   public float adv;
   public float torchDist;
   public float torchStrength;
   public boolean finished;
   public boolean looped;
   public boolean stopOnFrameOneAfterLoop;
   public boolean torchCone;
   private static Stack pool = new Stack();

   public void unpack(ByteBuffer var1) {
      this.id = var1.get();
      this.time = var1.getLong();
      this.dir = var1.get();
      this.x = var1.getFloat();
      this.y = var1.getFloat();
      this.z = var1.getFloat();
      this.mx = var1.getFloat();
      this.my = var1.getFloat();
      this.remoteState = var1.get();
      this.anim = var1.get();
      this.frame = var1.get();
      this.adv = var1.getFloat();
      this.torchDist = var1.getFloat();
      this.torchStrength = var1.getFloat();
      byte var2 = var1.get();
      this.finished = (var2 & 1) != 0;
      this.looped = (var2 & 2) != 0;
      this.stopOnFrameOneAfterLoop = (var2 & 4) != 0;
      this.torchCone = (var2 & 16) != 0;
   }

   public void update(IsoPlayer var1, float var2, float var3, float var4) {
      var1.setRemoteState(this.remoteState);
      IsoAnim var5 = (IsoAnim)var1.legsSprite.AnimStack.get(this.anim);
      var1.setDir(this.dir);
      var1.setX(var2);
      var1.setY(var3);
      var1.setZ(var4);
      if (this.remoteState != IsoPlayer.NetRemoteState_Attack) {
         var1.setRemoteMoveX(this.mx);
         var1.setRemoteMoveY(this.my);
      } else {
         var1.setRemoteMoveX(0.0F);
         var1.setRemoteMoveY(0.0F);
      }

      var1.TimeSinceLastNetData = 0;
      var1.PlayAnim(var5.name);
      var1.def.Frame = (float)this.frame;
      var1.def.Finished = this.finished;
      var1.def.Looped = this.looped;
      if (var1.legsSprite != null && var1.legsSprite.CurrentAnim != null && this.stopOnFrameOneAfterLoop) {
         var1.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
      }

      var1.def.AnimFrameIncrease = this.adv;
      var1.angle.x = var1.dir.ToVector().x;
      var1.angle.y = var1.dir.ToVector().y;
      var1.mpTorchDist = this.torchDist;
      var1.mpTorchStrength = this.torchStrength;
      var1.mpTorchCone = this.torchCone;
      var1.bx = var1.by = 0.0F;
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare((double)var2, (double)var3, (double)var4);
      if (var6 != null && !IsoWorld.instance.CurrentCell.getObjectList().contains(var1)) {
         IsoWorld.instance.CurrentCell.getObjectList().add(var1);
      }

   }

   public static PlayerNetState get() {
      return pool.isEmpty() ? new PlayerNetState() : (PlayerNetState)pool.pop();
   }

   public static void release(PlayerNetState var0) {
      pool.push(var0);
   }
}
