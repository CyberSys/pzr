package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;

public class DieState extends State {
   static DieState _instance = new DieState();

   public static DieState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (var1 instanceof IsoPlayer) {
         DebugLog.log("DieState enter " + ((IsoPlayer)var1).username);
      }

      var1.getStateMachine().Lock = true;
      if (var1 instanceof IsoPlayer) {
         var1.DoDeath((HandWeapon)null, (IsoGameCharacter)null);
      }

      if (var1 instanceof IsoSurvivor) {
         ((IsoSurvivor)var1).getDescriptor().bDead = true;
      }

      var1.PlayAnimUnlooped("ZombieDeath");
      var1.def.Frame = 0.0F;
      var1.def.AnimFrameIncrease = 0.25F;
      var1.setAnimated(true);
      var1.setDefaultState(this);
      if (var1 instanceof IsoSurvivor && var1.getTimeSinceZombieAttack() < 10) {
         ((IsoSurvivor)var1).ChewedByZombies();
      }

      if (GameServer.bServer && var1 instanceof IsoPlayer) {
         IsoDeadBody var2 = new IsoDeadBody(var1);
         GameServer.PlayerToBody.put((IsoPlayer)var1, var2);
         GameServer.SendDeath((IsoPlayer)var1);
         if (var1.shouldBecomeZombieAfterDeath()) {
            var2.reanimateLater();
         }
      }

   }

   public void execute(IsoGameCharacter var1) {
      if (var1.getCurrentSquare() != null) {
         if ((int)var1.def.Frame == var1.sprite.CurrentAnim.Frames.size() - 1) {
            if (var1 instanceof IsoSurvivor) {
               ((IsoSurvivor)var1).SetAllFrames((short)((int)var1.def.Frame));
            }

            if (var1 == TutorialManager.instance.wife) {
               var1.dir = IsoDirections.S;
            }

            if (GameServer.bServer && var1 instanceof IsoZombie) {
               GameServer.sendDeadZombie((IsoZombie)var1);
            }

            if (!GameServer.bServer) {
               IsoDeadBody var2 = new IsoDeadBody(var1);
               if (GameClient.bClient && var1 != IsoPlayer.instance) {
                  DebugLog.log("DieState adding " + ((IsoPlayer)var1).username + " to PlayerToBody");
                  GameClient.instance.PlayerToBody.put((IsoPlayer)var1, var2);
               }

               if (var1.shouldBecomeZombieAfterDeath()) {
                  var2.reanimateLater();
               }
            }
         }

      }
   }
}
