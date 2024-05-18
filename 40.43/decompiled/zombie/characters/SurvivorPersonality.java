package zombie.characters;

import zombie.characters.personalities.Cowardly;
import zombie.characters.personalities.FriendlyArmed;
import zombie.characters.personalities.GunNut;

public class SurvivorPersonality {
   public SurvivorPersonality.Personality type;

   public int getZombieFleeAmount() {
      return 10;
   }

   public float getPlayerDistanceComfort() {
      return 5.0F;
   }

   public int getZombieIgnoreOrdersCount() {
      return 4;
   }

   public static SurvivorPersonality CreatePersonality(SurvivorPersonality.Personality var0) {
      Object var1 = null;
      if (var0 == SurvivorPersonality.Personality.GunNut) {
         var1 = new GunNut();
      }

      if (var0 == SurvivorPersonality.Personality.FriendlyArmed) {
         var1 = new FriendlyArmed();
      }

      if (var0 == SurvivorPersonality.Personality.Cowardly) {
         var1 = new Cowardly();
      }

      if (var1 != null) {
         ((SurvivorPersonality)var1).type = var0;
      }

      return (SurvivorPersonality)var1;
   }

   public void CreateBehaviours(IsoSurvivor var1) {
   }

   public int getHuntZombieRange() {
      return 5;
   }

   public static enum Personality {
      GunNut,
      Kate,
      FriendlyArmed,
      Cowardly;
   }
}
