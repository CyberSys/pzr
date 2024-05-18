package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;

public class FakeDeadZombieState extends State {
   static FakeDeadZombieState _instance = new FakeDeadZombieState();

   public static FakeDeadZombieState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setVisibleToNPCs(false);
      var1.setCollidable(false);
      ((IsoZombie)var1).setFakeDead(true);
      var1.setOnFloor(true);
      var1.PlayAnimUnlooped("ZombieDeath");
      var1.def.Frame = (float)(var1.sprite.CurrentAnim.Frames.size() - 1);
      var1.setIgnoreMovementForDirection(true);
      var1.getStateMachine().Lock = true;
   }

   public void execute(IsoGameCharacter var1) {
      IsoZombie var2 = (IsoZombie)var1;
      if (!var2.isUnderVehicle() && var2.isFakeDead() && var2.target != null && var2.target.DistTo(var1) < 2.5F) {
         var1.setIgnoreMovementForDirection(false);
         var2.DirectionFromVector(var2.vectorToTarget);
         var1.setIgnoreMovementForDirection(true);
         var2.setFakeDead(false);
         var2.bCrawling = true;
         var1.setVisibleToNPCs(true);
         var1.setCollidable(true);
         var1.PlayAnimUnlooped("ZombieDeadToCrawl");
         var1.def.setFrameSpeedPerFrame(0.27F);
         var2.DoZombieStats();
         var1.getStateMachine().Lock = false;
         String var3 = "MaleZombieAttack";
         if (var1.isFemale()) {
            var3 = "FemaleZombieAttack";
         }

         var1.getEmitter().playSound(var3);
         if (var2.target instanceof IsoPlayer) {
            IsoPlayer var4 = (IsoPlayer)var2.target;
            Stats var10000 = var4.getStats();
            var10000.Panic += var4.getBodyDamage().getPanicIncreaseValue() * 3.0F;
         }
      }

      if (var1.getSprite().CurrentAnim.name.equals("ZombieDeadToCrawl") && (int)var1.getSpriteDef().Frame >= 16 && (int)var1.getSpriteDef().Frame <= 21) {
         if (var1.getHealth() > 0.0F && var2.isTargetInCone(1.5F, 0.9F) && var2.target instanceof IsoGameCharacter) {
            IsoGameCharacter var5 = (IsoGameCharacter)var2.target;
            if (var5.getVehicle() == null || var5.getVehicle().couldCrawlerAttackPassenger(var5)) {
               var5.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)var1);
            }
         }

         var1.getStateMachine().Lock = false;
         var1.getStateMachine().changeState(WalkTowardState.instance());
      }

   }

   public void exit(IsoGameCharacter var1) {
   }
}
