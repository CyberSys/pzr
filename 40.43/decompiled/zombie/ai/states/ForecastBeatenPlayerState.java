package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;

public class ForecastBeatenPlayerState extends State {
   static ForecastBeatenPlayerState _instance = new ForecastBeatenPlayerState();

   public static ForecastBeatenPlayerState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setReanimateTimer(30.0F);
   }

   public void execute(IsoGameCharacter var1) {
      if (var1.getCurrentSquare() != null) {
         var1.setReanimateTimer(var1.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
         if (var1.getReanimateTimer() <= 0.0F) {
            var1.setReanimateTimer(0.0F);
            var1.getStateMachine().changeState(BeatenPlayerState.instance());
         }

      }
   }
}
