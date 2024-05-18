package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;

public class CrawlingZombieTurnState extends State {
   static CrawlingZombieTurnState _instance = new CrawlingZombieTurnState();

   public static CrawlingZombieTurnState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
   }

   public void execute(IsoGameCharacter var1) {
      IsoDirections var2 = (IsoDirections)var1.StateMachineParams.get(0);
      boolean var3 = this.calculateDir(var1, var2);
      if (var3) {
         var1.PlayAnimUnlooped("Zombie_CrawlTurnL");
      } else {
         var1.PlayAnimUnlooped("Zombie_CrawlTurnR");
      }

      var1.getSpriteDef().AnimFrameIncrease = 0.11F;
      if ((int)var1.getSpriteDef().Frame == var1.getSprite().CurrentAnim.Frames.size() - 1) {
         if (var3) {
            var1.dir = IsoDirections.fromIndex(var1.dir.index() + 1);
         } else {
            var1.dir = IsoDirections.fromIndex(var1.dir.index() - 1);
         }

         var1.getVectorFromDirection(var1.angle);
         if (var1.dir == var2) {
            var1.getSpriteDef().Frame = 0.0F;
            var1.getSpriteDef().Finished = false;
            if (var1.legsSprite.modelSlot != null) {
               var1.legsSprite.modelSlot.ResetToFrameOne();
            }

            var1.getStateMachine().Lock = false;
            var1.getStateMachine().changeState(var1.getStateMachine().getPrevious());
         } else {
            var1.getSpriteDef().Frame = 0.0F;
            var1.getSpriteDef().Finished = false;
            if (var1.legsSprite.modelSlot != null) {
               var1.legsSprite.modelSlot.ResetToFrameOne();
            }
         }
      }

   }

   private boolean calculateDir(IsoGameCharacter var1, IsoDirections var2) {
      if (var2.index() > var1.dir.index()) {
         return var2.index() - var1.dir.index() <= 4;
      } else {
         return var2.index() - var1.dir.index() < -4;
      }
   }
}
