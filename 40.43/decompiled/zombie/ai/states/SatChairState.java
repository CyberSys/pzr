package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;

public class SatChairState extends State {
   static SatChairState _instance = new SatChairState();

   public static SatChairState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setSat(true);
      IsoObject var2 = (IsoObject)var1.StateMachineParams.get(0);
      int var3 = var2.getSquare().getX();
      int var4 = var2.getSquare().getY();
      float var5 = var1.getX() - (float)((int)var1.getX());
      var1.setX((float)var3);
      var1.setX(var1.getX() + var5);
      float var6 = var1.getY() - (float)((int)var1.getY());
      var1.setY((float)var4);
      var1.setY(var1.getY() + var6);
   }

   public void execute(IsoGameCharacter var1) {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      IsoObject var5 = (IsoObject)var1.StateMachineParams.get(0);
      int var11 = var5.getSquare().getX();
      int var12 = var5.getSquare().getY();
      float var6 = 0.2F;
      float var7 = 0.1F;
      var1.PlayAnimUnlooped("SatChairIn");
      var1.getSpriteDef().AnimFrameIncrease = 0.23F;
      if (var2) {
         if ((float)var12 < var1.getY()) {
            var1.setDir(IsoDirections.S);
         } else {
            var1.setDir(IsoDirections.N);
         }
      } else if ((float)var11 < var1.getX()) {
         var1.setDir(IsoDirections.W);
      } else {
         var1.setDir(IsoDirections.E);
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
      var1.setChair(var5);
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
