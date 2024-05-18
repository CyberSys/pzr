package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;

public class SatChairStateOut extends State {
   static SatChairStateOut _instance = new SatChairStateOut();

   public static SatChairStateOut instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
   }

   public void execute(IsoGameCharacter var1) {
      boolean var2 = true;
      boolean var3 = false;
      boolean var4 = false;
      IsoObject var5 = (IsoObject)var1.StateMachineParams.get(0);
      int var11 = var5.getSquare().getX();
      int var12 = var5.getSquare().getY();
      float var6 = 0.5F;
      float var7 = 0.5F;
      var1.PlayAnimUnlooped("SatChairOut");
      var1.getSpriteDef().AnimFrameIncrease = 0.23F;
      var1.setSat(true);
      if (var2) {
         if ((float)var12 < var1.getY()) {
            var1.setDir(IsoDirections.S);
            var6 = 0.5F;
            var7 = 0.682F;
         } else {
            var1.setDir(IsoDirections.N);
            var6 = 0.5F;
            var7 = 0.682F;
         }
      } else if ((float)var11 < var1.getX()) {
         var1.setDir(IsoDirections.W);
         var7 = 0.5F;
         var6 = 0.682F;
      } else {
         var1.setDir(IsoDirections.E);
         var7 = 0.5F;
         var6 = 0.682F;
      }

      float var8 = var1.x - (float)((int)var1.x);
      float var9 = var1.y - (float)((int)var1.y);
      float var10;
      if (var8 != var6) {
         var10 = (var6 - var8) / 4.0F;
         var8 += var10;
         var1.x = (float)((int)var1.x) + var8;
      }

      if (var9 != var7) {
         var10 = (var7 - var9) / 4.0F;
         var9 += var10;
         var1.y = (float)((int)var1.y) + var9;
      }

      var1.nx = var1.x;
      var1.ny = var1.y;
      var1.setChair((IsoObject)null);
      var1.setSat(false);
      if ((int)var1.getSpriteDef().Frame == var1.getSprite().CurrentAnim.Frames.size() - 1) {
         var1.getStateMachine().changeState(var1.getDefaultState());
         if (var1.getStateMachine().getCurrent() == instance()) {
            var1.getStateMachine().changeState(var1.getDefaultState());
         }

         var1.setCollidable(true);
         var1.StateMachineParams.clear();
      }

   }
}
