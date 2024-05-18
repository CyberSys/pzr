package zombie.characters.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import zombie.characters.IsoGameCharacter;
import zombie.core.Translator;

public class PerkFactory {
   public static boolean newMode = true;
   public static HashMap PerkMap = new HashMap();
   public static ArrayList PerkList = new ArrayList();
   static float PerkXPReqMultiplier = 1.5F;

   public static String getPerkName(PerkFactory.Perks var0) {
      for(int var1 = 0; var1 < PerkList.size(); ++var1) {
         PerkFactory.Perk var2 = (PerkFactory.Perk)PerkList.get(var1);
         if (var2.getType() == var0) {
            return var2.getName();
         }
      }

      return var0.toString();
   }

   public static PerkFactory.Perks getPerkFromName(String var0) {
      Iterator var1 = PerkMap.entrySet().iterator();

      Entry var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (Entry)var1.next();
      } while(!((PerkFactory.Perk)var2.getValue()).name.equals(var0));

      return (PerkFactory.Perks)var2.getKey();
   }

   public static PerkFactory.Perk getPerk(PerkFactory.Perks var0) {
      Iterator var1 = PerkMap.entrySet().iterator();

      Entry var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (Entry)var1.next();
      } while(var2.getKey() != var0);

      return (PerkFactory.Perk)var2.getValue();
   }

   public static PerkFactory.Perk AddPerk(PerkFactory.Perks var0, String var1, String var2, String var3, String var4, String var5, String var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16) {
      PerkFactory.Perk var17 = new PerkFactory.Perk(var1, var2, var3, var4, var5, var6);
      var17.type = var0;
      var17.xp1 = (int)((float)var7 * PerkXPReqMultiplier);
      var17.xp2 = (int)((float)var8 * PerkXPReqMultiplier);
      var17.xp3 = (int)((float)var9 * PerkXPReqMultiplier);
      var17.xp4 = (int)((float)var10 * PerkXPReqMultiplier);
      var17.xp5 = (int)((float)var11 * PerkXPReqMultiplier);
      var17.xp6 = (int)((float)var12 * PerkXPReqMultiplier);
      var17.xp7 = (int)((float)var13 * PerkXPReqMultiplier);
      var17.xp8 = (int)((float)var14 * PerkXPReqMultiplier);
      var17.xp9 = (int)((float)var15 * PerkXPReqMultiplier);
      var17.xp10 = (int)((float)var16 * PerkXPReqMultiplier);
      PerkMap.put(var0, var17);
      PerkList.add(var17);
      return var17;
   }

   public static PerkFactory.Perk AddPerk(PerkFactory.Perks var0, String var1, String var2, String var3, String var4, String var5, String var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, boolean var17) {
      PerkFactory.Perk var18 = new PerkFactory.Perk(var1, var2, var3, var4, var5, var6);
      var18.passiv = var17;
      var18.type = var0;
      var18.xp1 = (int)((float)var7 * PerkXPReqMultiplier);
      var18.xp2 = (int)((float)var8 * PerkXPReqMultiplier);
      var18.xp3 = (int)((float)var9 * PerkXPReqMultiplier);
      var18.xp4 = (int)((float)var10 * PerkXPReqMultiplier);
      var18.xp5 = (int)((float)var11 * PerkXPReqMultiplier);
      var18.xp6 = (int)((float)var12 * PerkXPReqMultiplier);
      var18.xp7 = (int)((float)var13 * PerkXPReqMultiplier);
      var18.xp8 = (int)((float)var14 * PerkXPReqMultiplier);
      var18.xp9 = (int)((float)var15 * PerkXPReqMultiplier);
      var18.xp10 = (int)((float)var16 * PerkXPReqMultiplier);
      PerkMap.put(var0, var18);
      PerkList.add(var18);
      return var18;
   }

   public static PerkFactory.Perk AddPerk(PerkFactory.Perks var0, String var1, String var2, String var3, String var4, String var5, String var6, PerkFactory.Perks var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17) {
      PerkFactory.Perk var18 = new PerkFactory.Perk(var1, var2, var3, var4, var5, var6, var7);
      var18.type = var0;
      var18.xp1 = (int)((float)var8 * PerkXPReqMultiplier);
      var18.xp2 = (int)((float)var9 * PerkXPReqMultiplier);
      var18.xp3 = (int)((float)var10 * PerkXPReqMultiplier);
      var18.xp4 = (int)((float)var11 * PerkXPReqMultiplier);
      var18.xp5 = (int)((float)var12 * PerkXPReqMultiplier);
      var18.xp6 = (int)((float)var13 * PerkXPReqMultiplier);
      var18.xp7 = (int)((float)var14 * PerkXPReqMultiplier);
      var18.xp8 = (int)((float)var15 * PerkXPReqMultiplier);
      var18.xp9 = (int)((float)var16 * PerkXPReqMultiplier);
      var18.xp10 = (int)((float)var17 * PerkXPReqMultiplier);
      PerkMap.put(var0, var18);
      PerkList.add(var18);
      return var18;
   }

   public static PerkFactory.Perk AddPerk(PerkFactory.Perks var0, String var1, String var2, String var3, String var4, String var5, String var6, PerkFactory.Perks var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17, boolean var18) {
      PerkFactory.Perk var19 = new PerkFactory.Perk(var1, var2, var3, var4, var5, var6, var7);
      var19.passiv = var18;
      var19.type = var0;
      var19.xp1 = (int)((float)var8 * PerkXPReqMultiplier);
      var19.xp2 = (int)((float)var9 * PerkXPReqMultiplier);
      var19.xp3 = (int)((float)var10 * PerkXPReqMultiplier);
      var19.xp4 = (int)((float)var11 * PerkXPReqMultiplier);
      var19.xp5 = (int)((float)var12 * PerkXPReqMultiplier);
      var19.xp6 = (int)((float)var13 * PerkXPReqMultiplier);
      var19.xp7 = (int)((float)var14 * PerkXPReqMultiplier);
      var19.xp8 = (int)((float)var15 * PerkXPReqMultiplier);
      var19.xp9 = (int)((float)var16 * PerkXPReqMultiplier);
      var19.xp10 = (int)((float)var17 * PerkXPReqMultiplier);
      PerkMap.put(var0, var19);
      PerkList.add(var19);
      return var19;
   }

   public static void init() {
      AddPerk(PerkFactory.Perks.BluntParent, Translator.getText("IGUI_perks_Blunt"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.BladeParent, Translator.getText("IGUI_perks_Blade"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Blunt, Translator.getText("IGUI_perks_Accuracy"), "", "", "", "", "", PerkFactory.Perks.BluntParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.BluntGuard, Translator.getText("IGUI_perks_Guard"), "", "", "", "", "", PerkFactory.Perks.BluntParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.BluntMaintenance, Translator.getText("IGUI_perks_Maintenance"), "", "", "", "", "", PerkFactory.Perks.BluntParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Axe, Translator.getText("IGUI_perks_Accuracy"), "", "", "", "", "", PerkFactory.Perks.BladeParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.BladeGuard, Translator.getText("IGUI_perks_Guard"), "", "", "", "", "", PerkFactory.Perks.BladeParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.BladeMaintenance, Translator.getText("IGUI_perks_Maintenance"), "", "", "", "", "", PerkFactory.Perks.BladeParent, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Firearm, Translator.getText("IGUI_perks_Firearm"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Aiming, Translator.getText("IGUI_perks_Aiming"), "", "", "", "", "", PerkFactory.Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Reloading, Translator.getText("IGUI_perks_Reloading"), "", "", "", "", "", PerkFactory.Perks.Firearm, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Crafting, Translator.getText("IGUI_perks_Crafting"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Woodwork, Translator.getText("IGUI_perks_Carpentry"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Cooking, Translator.getText("IGUI_perks_Cooking"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Farming, Translator.getText("IGUI_perks_Farming"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Doctor, Translator.getText("IGUI_perks_Doctor"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Electricity, Translator.getText("IGUI_perks_Electricity"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.MetalWelding, Translator.getText("IGUI_perks_MetalWelding"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Mechanics, Translator.getText("IGUI_perks_Mechanics"), "", "", "", "", "", PerkFactory.Perks.Crafting, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Survivalist, Translator.getText("IGUI_perks_Survivalist"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Fishing, Translator.getText("IGUI_perks_Fishing"), "", "", "", "", "", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Trapping, Translator.getText("IGUI_perks_Trapping"), "", "", "", "", "", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.PlantScavenging, Translator.getText("IGUI_perks_Foraging"), "", "", "", "", "", PerkFactory.Perks.Survivalist, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Passiv, Translator.getText("IGUI_perks_Passive"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Fitness, Translator.getText("IGUI_perks_Fitness"), "", "", "", "", "", PerkFactory.Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
      AddPerk(PerkFactory.Perks.Strength, Translator.getText("IGUI_perks_Strength"), "", "", "", "", "", PerkFactory.Perks.Passiv, 1000, 2000, 4000, 6000, 12000, 20000, 40000, 60000, 80000, 100000, true);
      AddPerk(PerkFactory.Perks.Agility, Translator.getText("IGUI_perks_Agility"), "", "", "", "", "", 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Sprinting, Translator.getText("IGUI_perks_Sprinting"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Lightfoot, Translator.getText("IGUI_perks_Lightfooted"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Nimble, Translator.getText("IGUI_perks_Nimble"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
      AddPerk(PerkFactory.Perks.Sneak, Translator.getText("IGUI_perks_Sneaking"), "", "", "", "", "", PerkFactory.Perks.Agility, 50, 100, 200, 500, 1000, 2000, 3000, 4000, 5000, 6000);
   }

   public static void CheckForUnlockedPerks(IsoGameCharacter var0) {
      for(int var1 = 0; var1 < PerkList.size(); ++var1) {
         PerkFactory.Perk var2 = (PerkFactory.Perk)PerkList.get(var1);
         int var3 = var0.getPerkLevel(var2.type);
         if (var3 != 10) {
            int var4 = (int)var2.getTotalXpForLevel(var3 + 1);
            if ((float)var4 <= var0.getXp().getXP(var2.type) && !var0.getCanUpgradePerk().contains(var2.type)) {
               if (var2.passiv) {
                  var0.LevelPerk(var2.type, false);
               } else {
                  var0.getCanUpgradePerk().add(var2.type);
               }
            }
         }
      }

   }

   public static void CheckForUnlockedPerks(IsoGameCharacter var0, PerkFactory.Perk var1) {
      int var2 = var0.getPerkLevel(var1.type);
      if (var2 != 10) {
         int var3 = (int)var1.getTotalXpForLevel(var2 + 1);
         if ((float)var3 <= var0.getXp().getXP(var1.type) && !var0.getCanUpgradePerk().contains(var1.type)) {
            if (var1.passiv) {
               var0.LevelPerk(var1.type, false);
               CheckForUnlockedPerks(var0, var1);
            } else {
               var0.getCanUpgradePerk().add(var1.type);
            }
         }

      }
   }

   public static class Perk {
      public String name;
      public String level1;
      public String level2;
      public String level3;
      public String level4;
      public String level5;
      public boolean passiv = false;
      public int xp1;
      public int xp2;
      public int xp3;
      public int xp4;
      public int xp5;
      public int xp6;
      public int xp7;
      public int xp8;
      public int xp9;
      public int xp10;
      public PerkFactory.Perks parent;
      public PerkFactory.Perks type;

      public Perk(String var1, String var2, String var3, String var4, String var5, String var6) {
         this.parent = PerkFactory.Perks.None;
         this.type = PerkFactory.Perks.None;
         this.name = var1;
         this.level1 = var2;
         this.level2 = var3;
         this.level3 = var4;
         this.level4 = var5;
         this.level5 = var6;
      }

      public Perk(String var1, String var2, String var3, String var4, String var5, String var6, PerkFactory.Perks var7) {
         this.parent = PerkFactory.Perks.None;
         this.type = PerkFactory.Perks.None;
         this.name = var1;
         this.level1 = var2;
         this.level2 = var3;
         this.level3 = var4;
         this.level4 = var5;
         this.level5 = var6;
         this.parent = var7;
      }

      public boolean isPassiv() {
         return this.passiv;
      }

      public PerkFactory.Perks getParent() {
         return this.parent;
      }

      public String getName() {
         return this.name;
      }

      public PerkFactory.Perks getType() {
         return this.type;
      }

      public int getXp1() {
         return this.xp1;
      }

      public int getXp2() {
         return this.xp2;
      }

      public int getXp3() {
         return this.xp3;
      }

      public int getXp4() {
         return this.xp4;
      }

      public int getXp5() {
         return this.xp5;
      }

      public int getXp6() {
         return this.xp6;
      }

      public int getXp7() {
         return this.xp7;
      }

      public int getXp8() {
         return this.xp8;
      }

      public int getXp9() {
         return this.xp9;
      }

      public int getXp10() {
         return this.xp10;
      }

      public float getXpForLevel(int var1) {
         if (var1 == 1) {
            return (float)this.xp1;
         } else if (var1 == 2) {
            return (float)this.xp2;
         } else if (var1 == 3) {
            return (float)this.xp3;
         } else if (var1 == 4) {
            return (float)this.xp4;
         } else if (var1 == 5) {
            return (float)this.xp5;
         } else if (var1 == 6) {
            return (float)this.xp6;
         } else if (var1 == 7) {
            return (float)this.xp7;
         } else if (var1 == 8) {
            return (float)this.xp8;
         } else if (var1 == 9) {
            return (float)this.xp9;
         } else {
            return var1 == 10 ? (float)this.xp10 : -1.0F;
         }
      }

      public float getTotalXpForLevel(int var1) {
         int var2 = 0;

         for(int var3 = 1; var3 <= var1; ++var3) {
            float var4 = this.getXpForLevel(var3);
            if (var4 != -1.0F) {
               var2 = (int)((float)var2 + var4);
            }
         }

         return (float)var2;
      }
   }

   public static enum Perks {
      None(0),
      Agility(1),
      Cooking(2),
      Melee(3),
      Crafting(4),
      Fitness(5),
      Strength(6),
      Blunt(7),
      Axe(8),
      Sprinting(9),
      Lightfoot(10),
      Nimble(11),
      Sneak(12),
      Woodwork(13),
      Aiming(14),
      Reloading(15),
      Farming(16),
      Survivalist(17),
      Fishing(18),
      Trapping(19),
      Passiv(20),
      Firearm(21),
      PlantScavenging(22),
      BluntParent(23),
      BladeParent(24),
      BluntGuard(25),
      BladeGuard(26),
      BluntMaintenance(27),
      BladeMaintenance(28),
      Doctor(29),
      Electricity(30),
      Blacksmith(31),
      MetalWelding(32),
      Melting(33),
      Mechanics(34),
      MAX(35);

      private int index;

      private Perks(int var3) {
         this.index = var3;
      }

      public int index() {
         return this.index;
      }

      public static int getMaxIndex() {
         return MAX.index();
      }

      public static PerkFactory.Perks fromIndex(int var0) {
         return ((PerkFactory.Perks[])PerkFactory.Perks.class.getEnumConstants())[var0];
      }

      public static PerkFactory.Perks FromString(String var0) {
         try {
            return valueOf(var0);
         } catch (Exception var2) {
            return MAX;
         }
      }
   }
}
