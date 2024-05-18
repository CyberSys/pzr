package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.network.GameClient;
import zombie.ui.TutorialManager;

public class BeatenPlayerState extends State {
   static BeatenPlayerState _instance = new BeatenPlayerState();

   public static BeatenPlayerState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.getStateMachine().Lock = true;
      var1.PlayAnimUnlooped("ZombieDeath");
      var1.def.Frame = 0.0F;
      var1.def.AnimFrameIncrease = 0.4F;
      var1.setAnimated(true);
      var1.setReanimateTimer((float)(30 + Rand.Next(120)));
      if (var1 instanceof IsoPlayer && !((IsoPlayer)var1).isLocalPlayer()) {
         ((IsoPlayer)var1).setAnimForecasted(5000);
      }

      if (GameClient.bClient && var1 instanceof IsoPlayer && ((IsoPlayer)var1).isLocalPlayer()) {
         GameClient.instance.sendPlayer((IsoPlayer)var1);
      }

      var1.setOnFloor(true);
   }

   public void execute(IsoGameCharacter var1) {
      if (var1.getCurrentSquare() != null) {
         var1.setReanimateTimer(var1.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
         if (var1.getReanimateTimer() <= 0.0F && var1.getBodyDamage().getOverallBodyHealth() > 0.0F) {
            var1.setReanimateTimer(0.0F);
            var1.getStateMachine().Lock = false;
            var1.getStateMachine().changeState(ReanimatePlayerState.instance());
         }

         if ((int)var1.def.Frame == var1.sprite.CurrentAnim.Frames.size() - 1) {
            if (var1 instanceof IsoSurvivor) {
               ((IsoSurvivor)var1).SetAllFrames((short)((int)var1.def.Frame));
            }

            if (var1 == TutorialManager.instance.wife) {
               var1.dir = IsoDirections.S;
            }
         }

      }
   }
}
