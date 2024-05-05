package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class PlayerKnockedDown extends State {
   private static final PlayerKnockedDown _instance = new PlayerKnockedDown();

   public static PlayerKnockedDown instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setIgnoreMovement(true);
      ((IsoPlayer)var1).setBlockMovement(true);
      var1.setHitReaction("");
   }

   public void execute(IsoGameCharacter var1) {
      if (!GameServer.bServer && var1.isDead()) {
         if (!var1.isOnDeathDone()) {
            var1.setOnDeathDone(true);
            var1.DoDeath((HandWeapon)null, (IsoGameCharacter)null);
         }

         IsoDeadBody var2 = new IsoDeadBody(var1);
         if (GameClient.bClient) {
            DebugLog.log("DieState adding " + ((IsoPlayer)var1).username + " to PlayerToBody");
            GameClient.instance.PlayerToBody.put((IsoPlayer)var1, var2);
         }

         if (var1.shouldBecomeZombieAfterDeath()) {
            var2.reanimateLater();
         }

      } else {
         var1.setReanimateTimer(var1.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
      }
   }

   public void animEvent(IsoGameCharacter var1, AnimEvent var2) {
      if (var2.m_EventName.equalsIgnoreCase("FallOnFront")) {
         var1.setFallOnFront(Boolean.parseBoolean(var2.m_ParameterValue));
         var1.setOnFloor(var1.isFallOnFront());
      }

      if (var2.m_EventName.equalsIgnoreCase("FallOnBack")) {
         var1.setOnFloor(Boolean.parseBoolean(var2.m_ParameterValue));
      }

      if (var2.m_EventName.equalsIgnoreCase("setSitOnGround")) {
         var1.setSitOnGround(Boolean.parseBoolean(var2.m_ParameterValue));
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovement(false);
      ((IsoPlayer)var1).setBlockMovement(false);
      ((IsoPlayer)var1).setM_bKnockedDown(false);
   }
}
