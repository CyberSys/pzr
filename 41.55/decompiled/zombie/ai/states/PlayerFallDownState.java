package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;

public final class PlayerFallDownState extends State {
   private static final PlayerFallDownState _instance = new PlayerFallDownState();

   public static PlayerFallDownState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setIgnoreMovement(true);
      var1.clearVariable("bKnockedDown");
      if (var1 instanceof IsoPlayer && var1.isDead()) {
         if (!var1.isOnDeathDone()) {
            var1.setOnDeathDone(true);
            var1.DoDeath((HandWeapon)null, (IsoGameCharacter)null);
         }

         if (GameServer.bServer) {
            IsoDeadBody var2 = new IsoDeadBody(var1);
            GameServer.PlayerToBody.put((IsoPlayer)var1, var2);
            GameServer.SendDeath((IsoPlayer)var1);
            if (var1.shouldBecomeZombieAfterDeath()) {
               var2.reanimateLater();
            }
         }
      }

   }

   public void execute(IsoGameCharacter var1) {
   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovement(false);
   }
}
