package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.ui.TutorialManager;

public class JustDieState extends State {
   static JustDieState _instance = new JustDieState();
   Vector2 dirThisFrame = new Vector2();
   int AnimDelayRate = 10;

   public static JustDieState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      if (var1 instanceof IsoSurvivor) {
         ((IsoSurvivor)var1).getDescriptor().bDead = true;
      }

      if (var1 instanceof IsoZombie) {
         ++IsoZombie.ZombieDeaths;
         var1.PlayAnim("ZombieStaggerBack");
         var1.def.setFrameSpeedPerFrame(0.3F);
      } else {
         boolean var2 = false;
      }

      if (var1.getHealth() <= 0.0F) {
         var1.playDeadSound();
      }

      var1.setStateEventDelayTimer(30.0F * var1.getHitForce() * var1.getStaggerTimeMod());
      Vector2 var10000 = var1.getHitDir();
      var10000.x *= var1.getHitForce();
      var10000 = var1.getHitDir();
      var10000.y *= var1.getHitForce();
      var10000 = var1.getHitDir();
      var10000.x *= 0.08F;
      var10000 = var1.getHitDir();
      var10000.y *= 0.08F;
      if (var1.getHitDir().getLength() > 0.08F) {
         var1.getHitDir().setLength(0.08F);
      }

      var1.setIgnoreMovementForDirection(true);
      var1.getStateMachine().Lock = true;
      var1.setReanimPhase(0);
      if (var1 instanceof IsoZombie) {
         var1.setReanimateTimer((float)(Rand.Next(30) + 4));
      }

      if (Rand.Next(5) == 0) {
         var1.setReanimateTimer((float)(Rand.Next(30) + 30));
      }

      if (var1 instanceof IsoZombie) {
         var1.setReanimAnimFrame(3);
         var1.setReanimAnimDelay(this.AnimDelayRate);
      }

      if (var1.getHealth() > 0.0F) {
         var1.setReanim(true);
         var1.setDieCount(var1.getDieCount() + 1);
      }

   }

   public void execute(IsoGameCharacter var1) {
      if (!(var1 instanceof IsoZombie)) {
         boolean var2 = false;
      }

      if (var1 == TutorialManager.instance.wife) {
         var1.dir = IsoDirections.S;
      }

      var1.PlayAnimFrame("ZombieDeath", 13);
      if (GameServer.bServer && var1 instanceof IsoZombie) {
         GameServer.sendDeadZombie((IsoZombie)var1);
      }

      new IsoDeadBody(var1);
      if (var1.getAttackedBy() != null) {
         var1.getAttackedBy().setZombieKills(var1.getAttackedBy().getZombieKills() + 1);
      }

      if (GameServer.bServer && var1 instanceof IsoZombie && var1.getAttackedBy() instanceof IsoPlayer) {
         var1.getAttackedBy().sendObjectChange("AddZombieKill");
      }

   }

   public void exit(IsoGameCharacter var1) {
      var1.setIgnoreMovementForDirection(false);
   }
}
