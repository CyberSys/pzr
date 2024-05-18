package zombie.ai.states;

import zombie.SoundManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;

public class WanderState extends State {
   static WanderState _instance = new WanderState();
   static Vector2 vec = new Vector2();

   public static WanderState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      this.chooseNewDirection(var1);
   }

   public void chooseNewDirection(IsoGameCharacter var1) {
      if (!(var1 instanceof IsoZombie) || !((IsoZombie)var1).isUseless()) {
         boolean var2 = false;
         int var3 = 0;
         if (var1.getCurrentSquare() == null) {
            var1.ensureOnTile();
         }

         int var4;
         do {
            ++var3;
            var1.setNextWander(300);
            var1.setPathTargetX((int)(var1.getX() + (float)Rand.Next(IsoWorld.instance.CurrentCell.getWidthInTiles()) - 75.0F));
            var1.setPathTargetY((int)(var1.getY() + (float)Rand.Next(IsoWorld.instance.CurrentCell.getHeightInTiles()) - 75.0F));
            var4 = LosUtil.lineClearCollideCount(var1, var1.getCell(), var1.getPathTargetX(), var1.getPathTargetY(), (int)var1.getZ(), (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), 1);
            vec.x = (float)var1.getPathTargetX();
            vec.y = (float)var1.getPathTargetY();
            Vector2 var10000 = vec;
            var10000.x -= var1.x;
            var10000 = vec;
            var10000.y -= var1.y;
            vec.setLength(((IsoZombie)var1).wanderSpeed);
            var1.reqMovement.x = vec.x;
            var1.reqMovement.y = vec.y;
            vec.normalize();
            var1.DirectionFromVector(vec);
         } while(var4 < 1 && var3 < 100);

      }
   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      if (!((IsoZombie)var1).bCrawling) {
         var1.setOnFloor(false);
      }

      var2.bRunning = false;
      --var2.iIgnoreDirectionChange;
      Vector2 var10000;
      float var3;
      float var4;
      float var5;
      if ((Rand.Next(100) != 0 || var2.iIgnoreDirectionChange > 0) && !var1.isCollidedThisFrame() && (!(Math.abs((float)var1.getPathTargetX() - var1.getX()) <= 1.0F) || !(Math.abs((float)var1.getPathTargetY() - var1.getY()) <= 1.0F))) {
         vec.x = var1.reqMovement.x;
         vec.y = var1.reqMovement.y;
         var3 = vec.x;
         var4 = vec.y;
         var5 = IsoUtils.DistanceManhatten(IsoCamera.CamCharacter.x, IsoCamera.CamCharacter.y, var1.x, var1.y);
         var5 /= 30.0F;
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         if (var5 < 0.0F) {
            var5 = 0.0F;
         }

         var5 = 1.0F - var5;
         var10000 = vec;
         var10000.x *= var5;
         var10000 = vec;
         var10000.y *= var5;
         var1.Move(var1.reqMovement);
         vec.normalize();
         var1.DirectionFromVector(vec);
         var1.reqMovement.x = var3;
         var1.reqMovement.y = var4;
      } else {
         this.chooseNewDirection(var1);
         vec.x = (float)var1.getPathTargetX();
         vec.y = (float)var1.getPathTargetY();
         var10000 = vec;
         var10000.x -= var1.x;
         var10000 = vec;
         var10000.y -= var1.y;
         vec.x = var1.reqMovement.x;
         vec.y = var1.reqMovement.y;
         var3 = vec.x;
         var4 = vec.y;
         var5 = IsoUtils.DistanceManhatten(IsoCamera.CamCharacter.x, IsoCamera.CamCharacter.y, var1.x, var1.y);
         if (var5 > 20.0F) {
            var5 /= 40.0F;
            if (var5 > 1.0F) {
               var5 = 1.0F;
            }

            if (var5 < 0.0F) {
               var5 = 0.0F;
            }

            var5 = 1.0F - var5;
            var5 *= 0.5F;
         } else {
            var5 = 1.0F;
         }

         vec.setLength(((IsoZombie)var1).wanderSpeed * var5);
         var1.DirectionFromVector(vec);
         var1.Move(vec);
         var1.reqMovement.x = var3;
         var1.reqMovement.y = var4;
      }

   }

   void calculate() {
      SoundManager.instance.update1();
   }
}
