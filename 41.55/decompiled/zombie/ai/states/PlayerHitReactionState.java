package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.network.GameClient;

public final class PlayerHitReactionState extends State {
   private static final PlayerHitReactionState _instance = new PlayerHitReactionState();

   public static PlayerHitReactionState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setIgnoreMovement(true);
      if (!var1.getCharacterActions().isEmpty()) {
         ((BaseAction)var1.getCharacterActions().get(0)).forceStop();
      }

      ((IsoPlayer)var1).setIsAiming(false);
      if (var1.getAttackingZombie() != null) {
         if (GameClient.bClient) {
            GameClient.sendHitReactionFromZombie((IsoPlayer)var1);
         }

      }
   }

   public void execute(IsoGameCharacter var1) {
   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovement(false);
      var1.setHitReaction("");
      var1.setAttackingZombie((IsoZombie)null);
   }

   public void animEvent(IsoGameCharacter var1, AnimEvent var2) {
      if (var1.getAttackingZombie() == null) {
         DebugLog.log("PlayerHitReactionState.animEvent (" + var2.m_EventName + ") zombie is null");
      } else {
         if (var2.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
            var1.getAttackingZombie().setHitForce(0.03F);
            var1.getAttackingZombie().setPlayerAttackPosition((String)null);
            var1.getAttackingZombie().bStaggerBack = true;
         }

         if (var2.m_EventName.equalsIgnoreCase("Defend")) {
            var1.getAttackingZombie().setHitReaction("BiteDefended");
         }

         if (var2.m_EventName.equalsIgnoreCase("DeathSound")) {
            if (var1.isPlayingDeathSound()) {
               return;
            }

            var1.setPlayingDeathSound(true);
            String var3 = "Male";
            if (var1.isFemale()) {
               var3 = "Female";
            }

            var3 = var3 + "BeingEatenDeath";
            var1.playSound(var3);
         }

         if (var2.m_EventName.equalsIgnoreCase("Death")) {
            var1.setOnFloor(true);
            var1.Kill(var1.getAttackingZombie());
         }

      }
   }
}
