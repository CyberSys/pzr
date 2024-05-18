package zombie.iso.weather;

import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.core.Core;
import zombie.network.GameServer;

public class Temperature {
   public static boolean DO_DEFAULT_BASE = false;
   public static boolean DO_DAYLEN_MOD = true;
   public static String CELSIUS_POSTFIX = "°C";
   public static String FAHRENHEIT_POSTFIX = "°F";
   private static final float FavorableAirTempMean = 22.0F;
   private static final float FavorableAirTempMin = 16.0F;
   private static final float FavorableAirTempMax = 28.0F;
   private static final float FavorableBodyTemp = 37.0F;
   private static final float BodyMaxColdResistTemp = 8.0F;
   private static final float ClothMaxColdResistTemp = -25.0F;
   private static final float BodyMaxHeatResistTemp = 36.0F;
   private static final float ClothMaxHeatResistTemp = 36.0F;
   public static final float Hypothermia_1 = 36.5F;
   public static final float Hypothermia_2 = 33.0F;
   public static final float Hypothermia_3 = 30.0F;
   public static final float Hypothermia_4 = 25.0F;
   public static final float Hyperthermia_1 = 37.5F;
   public static final float Hyperthermia_2 = 39.0F;
   public static final float Hyperthermia_3 = 40.0F;
   public static final float Hyperthermia_4 = 41.0F;
   public static final float BodyMinTemp = 20.0F;
   public static final float BodyMaxTemp = 42.0F;
   private static String cacheTempString = "";
   private static float cacheTemp = -9000.0F;
   private static Temperature.PlayerTempVars[] playerTemperatures;

   public static String getCelsiusPostfix() {
      return CELSIUS_POSTFIX;
   }

   public static String getFahrenheitPostfix() {
      return FAHRENHEIT_POSTFIX;
   }

   public static String getTemperaturePostfix() {
      return Core.OptionTemperatureDisplayCelsius ? CELSIUS_POSTFIX : FAHRENHEIT_POSTFIX;
   }

   public static String getTemperatureString(float var0) {
      float var1 = Core.OptionTemperatureDisplayCelsius ? var0 : CelsiusToFahrenheit(var0);
      var1 = (float)Math.round(var1 * 10.0F) / 10.0F;
      if (cacheTemp != var1) {
         cacheTemp = var1;
         cacheTempString = var1 + " " + getTemperaturePostfix();
      }

      return cacheTempString;
   }

   public static float CelsiusToFahrenheit(float var0) {
      return var0 * 1.8F + 32.0F;
   }

   public static float FahrenheitToCelsius(float var0) {
      return (var0 - 32.0F) / 1.8F;
   }

   public static float WindchillCelsiusKph(float var0, float var1) {
      float var2 = 13.12F + 0.6215F * var0 - 11.37F * (float)Math.pow((double)var1, 0.1599999964237213D) + 0.3965F * var0 * (float)Math.pow((double)var1, 0.1599999964237213D);
      return var2 < var0 ? var2 : var0;
   }

   public static void reset() {
      playerTemperatures = null;
   }

   public static float getFractionForRealTimeRatePerMin(float var0) {
      if (DO_DEFAULT_BASE) {
         return var0 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutesDefault());
      } else if (!DO_DAYLEN_MOD) {
         return var0 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutes());
      } else {
         float var1 = (float)SandboxOptions.instance.getDayLengthMinutes() / (float)SandboxOptions.instance.getDayLengthMinutesDefault();
         if (var1 < 1.0F) {
            var1 = 0.5F + 0.5F * var1;
         } else if (var1 > 1.0F) {
            var1 = 1.0F + var1 / 16.0F;
         }

         return var0 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutes()) * var1;
      }
   }

   public static Temperature.PlayerTempVars getPlayerTemperatureVars(IsoPlayer var0) {
      if (!GameServer.bServer && var0 != null) {
         for(int var1 = 0; var1 < IsoPlayer.players.length; ++var1) {
            IsoPlayer var2 = IsoPlayer.players[var1];
            if (var2 != null && !var2.isDead() && var2 == var0 && playerTemperatures != null && var1 < playerTemperatures.length) {
               return playerTemperatures[var1];
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static void updateTemperatureForAllPlayers() {
      if (!GameServer.bServer) {
         int var0;
         if (playerTemperatures == null || playerTemperatures.length != IsoPlayer.players.length) {
            playerTemperatures = new Temperature.PlayerTempVars[IsoPlayer.players.length];

            for(var0 = 0; var0 < playerTemperatures.length; ++var0) {
               playerTemperatures[var0] = new Temperature.PlayerTempVars();
            }
         }

         for(var0 = 0; var0 < IsoPlayer.players.length; ++var0) {
            IsoPlayer var1 = IsoPlayer.players[var0];
            if (var1 != null && !var1.isDead()) {
               updatePlayerTemperature(var0, var1);
            }
         }

      }
   }

   public static void updatePlayerTemperature(int var0, IsoPlayer var1) {
      if (!GameServer.bServer) {
         Temperature.PlayerTempVars var2 = playerTemperatures[var0];
         ClimateManager var3 = ClimateManager.getInstance();
         var2.isInVehicle = var1.getVehicle() != null;
         var2.isInside = false;
         var2.windSpeed = var3.getWindspeedKph();
         if (var1.getSquare() != null) {
            var2.isInside = var1.getSquare().isInARoom();
         }

         var2.airTemperature = var3.getAirTemperatureForCharacter(var1, true);
         var2.windChillAmount = 0.0F;
         if (var2.airTemperature < var3.getTemperature()) {
            var2.windChillAmount = var3.getTemperature() - var2.airTemperature;
         }

         float var4 = var1.getTemperature();
         var2.clothing = ClimateManager.clamp01(var1.getPlayerClothingInsulation());
         var2.clothing = (var2.clothing * var2.clothing + var2.clothing) / 2.0F;
         Stats var5 = var1.getStats();
         BodyDamage var6 = var1.getBodyDamage();
         var2.hunger = var5.getHunger();
         var2.thirst = var5.getThirst();
         var2.tired = var5.getFatigue();
         var2.excercise = ClimateManager.clamp01(1.0F - var5.getEndurance());
         var2.fitness = ClimateManager.clamp01((var5.getFitness() + 1.0F) / 2.0F);
         var2.drunkenness = var5.getDrunkenness();
         var2.sickness = var5.getSickness();
         var2.wetness = ClimateManager.clamp01(var6.getWetness() / 100.0F);
         var2.hasACold = ClimateManager.clamp01(var6.getColdStrength() / 100.0F);
         var2.excercise = var2.excercise * var2.excercise;
         var2.weight = var1.getNutrition().getWeight();
         float var7 = ClimateManager.clamp01((var2.weight / 75.0F - 0.5F) * 0.666F);
         var2.undercooled = ClimateManager.clamp01(1.0F - (var4 - 20.0F) / 17.0F);
         float var8 = 0.8F;
         var8 += 0.4F * var2.fitness * var2.fitness;
         var8 += 0.4F * var7 * var7;
         var8 += var1.HasTrait("Outdoorsman") ? 0.2F : 0.0F;
         var8 -= Math.max(0.2F * var2.hasACold, 0.25F * var2.sickness);
         var8 -= 0.25F * var2.drunkenness;
         var2.bodyColdResist = 0.5F + var8;
         var2.bodyColdResist = var2.bodyColdResist - 0.75F * var2.tired;
         var2.bodyColdResist = var2.bodyColdResist - 0.75F * var2.hunger;
         var2.bodyColdResist = (float)((double)var2.bodyColdResist + (double)(0.25F * var2.excercise) + 0.6D * (double)var2.excercise * (double)var2.fitness);
         var2.bodyColdResist = var2.bodyColdResist - 1.2F * var2.wetness * var2.wetness;
         var2.bodyColdResist = ClimateManager.clamp(0.0F, 2.0F, var2.bodyColdResist);
         float var9 = ClimateManager.clamp01(var2.bodyColdResist - 0.5F);
         var2.clothColdResist = 0.2F * var2.clothing;
         var2.clothColdResist = var2.clothColdResist + 0.8F * var2.clothing * (1.0F - (1.0F - var9) * (1.0F - var9));
         float var10 = var9 * 8.0F;
         float var11 = var2.clothColdResist * 41.0F;
         var2.debugColdBodyBonus = 0.0F;
         var2.debugColdClothBonus = 0.0F;
         if (var2.bodyColdResist > 1.5F) {
            var2.debugColdBodyBonus = 5.0F * ((var2.bodyColdResist - 1.5F) / 0.5F);
            var10 += var2.debugColdBodyBonus;
            var2.debugColdClothBonus = 7.0F * ((var2.bodyColdResist - 1.5F) / 0.5F);
            var11 += var2.debugColdClothBonus;
         } else if (var2.bodyColdResist < 0.5F) {
            var2.debugColdBodyBonus = -5.0F * (1.0F - var2.bodyColdResist / 0.5F);
            var10 += var2.debugColdBodyBonus;
            var2.debugColdClothBonus = -7.0F * (1.0F - var2.bodyColdResist / 0.5F);
            var11 += var2.debugColdClothBonus;
         }

         var2.coldResist = Math.max(var10, var11);
         if (var2.coldResist < 0.0F) {
            var2.coldResist = 0.0F;
         }

         var2.coldStrength = 0.0F;
         var2.coldChange = 0.0F;
         if (var2.airTemperature < 16.0F) {
            var2.coldStrength = 16.0F - var2.airTemperature;
         }

         var2.coldChange = var2.coldResist - var2.coldStrength;
         var2.overheated = ClimateManager.clamp01((var4 - 37.0F) / 5.0F);
         float var12 = 0.95F;
         var12 += 0.3F * var2.fitness * var2.fitness;
         var12 += var1.HasTrait("Outdoorsman") ? 0.2F : 0.0F;
         var12 -= 0.45F * var7 * var7;
         var12 -= Math.max(0.2F * var2.hasACold, 0.25F * var2.sickness);
         var2.bodyHeatResist = 0.5F + var12;
         var2.bodyHeatResist = var2.bodyHeatResist - 1.2F * var2.thirst * var2.thirst;
         var2.bodyHeatResist = var2.bodyHeatResist - 0.2F * var2.tired;
         var2.bodyHeatResist = var2.bodyHeatResist - 0.5F * var2.excercise;
         var2.bodyHeatResist = var2.bodyHeatResist + 1.25F * var2.wetness;
         var2.bodyHeatResist = ClimateManager.clamp(0.0F, 2.0F, var2.bodyHeatResist);
         var9 = ClimateManager.clamp01(var2.bodyHeatResist - 0.5F);
         float var13 = 1.0F - var2.clothing;
         var2.clothHeatResist = var13 * 0.65F + 0.35F * var13 * var9;
         var10 = var9 * 8.0F;
         var11 = var2.clothHeatResist * 8.0F;
         var2.debugHeatBodyBonus = 0.0F;
         var2.debugHeatClothBonus = 0.0F;
         if (var2.bodyHeatResist > 1.5F) {
            var2.debugHeatBodyBonus = 3.0F * ((var2.bodyHeatResist - 1.5F) / 0.5F);
            var10 += var2.debugHeatBodyBonus;
            var2.debugHeatClothBonus = 2.0F * ((var2.bodyHeatResist - 1.5F) / 0.5F);
            var11 += var2.debugHeatClothBonus;
         } else if (var2.bodyHeatResist < 0.5F) {
            var2.debugHeatBodyBonus = -2.0F * (1.0F - var2.bodyHeatResist / 0.5F);
            var10 += var2.debugHeatBodyBonus;
            var2.debugHeatClothBonus = -1.0F * (1.0F - var2.bodyHeatResist / 0.5F);
            var11 += var2.debugHeatClothBonus;
         }

         var2.heatResist = Math.min(var10, var11);
         if (var2.heatResist < 0.0F) {
            var2.heatResist = 0.0F;
         }

         var2.heatStrength = 0.0F;
         var2.heatChange = 0.0F;
         if (var2.airTemperature > 28.0F) {
            var2.heatStrength = var2.airTemperature - 28.0F;
         }

         var2.heatChange = var2.heatStrength - var2.heatResist;
         var2.tickChangePm = 0.0F;
         var2.tickChange = 0.0F;
         var2.changeSpeed = -1.0F;
         if (var2.coldChange < 0.0F) {
            var2.calcTickChange(0.0F, -1.0F, var2.coldChange / -25.0F);
            var4 += var2.tickChange;
         } else if (var2.coldChange > 0.0F && var4 <= 37.0F) {
            var2.calcTickChange(2.0F, 8.0F, var2.coldChange / 50.0F);
            var4 += var2.tickChange;
            if (var4 > 37.0F) {
               var4 = 37.1F;
            }
         }

         if (var2.heatChange > 0.0F) {
            var2.calcTickChange(0.0F, 0.25F, var2.heatChange / 7.0F);
            var4 += var2.tickChange;
         } else if (var2.heatChange < 0.0F && var4 >= 37.0F) {
            var2.calcTickChange(-1.0F, -3.0F, var2.heatChange / -3.0F);
            var4 += var2.tickChange;
            if (var4 < 37.0F) {
               var4 = 36.9F;
            }
         }

         var4 = ClimateManager.clamp(20.0F, 42.0F, var4);
         var1.setTemperature(var4);
         var2.damageState = var1.getBodyDamage().ColdDamageStage;
         float var14;
         if (var4 <= 25.0F && var2.airTemperature < 0.0F) {
            var14 = ClimateManager.clamp01(var2.airTemperature / -40.0F);
            var2.damageState = ClimateManager.clamp01(var2.damageState + getFractionForRealTimeRatePerMin(0.025F + 0.125F * var14));
         } else if (var2.damageState > 0.0F) {
            var14 = 0.0F;
            if (var2.tickChangePm > 0.0F) {
               var14 = var2.changeSpeed;
            } else if (var4 >= 36.5F) {
               var14 = 1.0F;
            } else if (var4 >= 33.0F) {
               var14 = 0.75F;
            } else if (var4 >= 30.0F) {
               var14 = 0.5F;
            } else if (var4 >= 25.0F) {
               var14 = 0.25F;
            }

            if (var14 > 0.0F) {
               var2.damageState = var2.damageState - getFractionForRealTimeRatePerMin(0.2F + 0.3F * var14);
               var2.damageState = ClimateManager.clamp01(var2.damageState);
            }
         }

         var1.getBodyDamage().ColdDamageStage = var2.damageState;
      }
   }

   public static class PlayerTempVars {
      private boolean isInVehicle = false;
      private boolean isInside = false;
      private float airTemperature = 0.0F;
      private float windSpeed = 0.0F;
      private float hunger;
      private float thirst;
      private float tired;
      private float excercise;
      private float weight;
      private float fitness;
      private float drunkenness;
      private float sickness;
      private float hasACold;
      private float wetness;
      private float clothing;
      private float bodyColdResist;
      private float clothColdResist;
      private float bodyHeatResist;
      private float clothHeatResist;
      private float coldResist;
      private float heatResist;
      private float coldStrength;
      private float heatStrength;
      private float coldChange;
      private float heatChange;
      private float debugColdBodyBonus;
      private float debugHeatBodyBonus;
      private float debugColdClothBonus;
      private float debugHeatClothBonus;
      private float tickChangePm;
      private float tickChange;
      private float changeSpeed = 0.0F;
      private float damageState = 0.0F;
      private float undercooled = 0.0F;
      private float overheated = 0.0F;
      private float windChillAmount = 0.0F;

      public boolean IsInVehicle() {
         return this.isInVehicle;
      }

      public boolean IsInside() {
         return this.isInside;
      }

      public float getWindSpeed() {
         return this.windSpeed;
      }

      public float getAirTemperature() {
         return this.airTemperature;
      }

      public float getHunger() {
         return this.hunger;
      }

      public float getThirst() {
         return this.thirst;
      }

      public float getTired() {
         return this.tired;
      }

      public float getExcercise() {
         return this.excercise;
      }

      public float getWeight() {
         return this.weight;
      }

      public float getFitness() {
         return this.fitness;
      }

      public float getDrunkenness() {
         return this.drunkenness;
      }

      public float getSickness() {
         return this.sickness;
      }

      public float getHasACold() {
         return this.hasACold;
      }

      public float getWetness() {
         return this.wetness;
      }

      public float getClothing() {
         return this.clothing;
      }

      public float getBodyColdResist() {
         return this.bodyColdResist;
      }

      public float getClothColdResist() {
         return this.clothColdResist;
      }

      public float getBodyHeatResist() {
         return this.bodyHeatResist;
      }

      public float getClothHeatResist() {
         return this.clothHeatResist;
      }

      public float getColdResist() {
         return this.coldResist;
      }

      public float getHeatResist() {
         return this.heatResist;
      }

      public float getColdStrength() {
         return this.coldStrength;
      }

      public float getHeatStrength() {
         return this.heatStrength;
      }

      public float getColdChange() {
         return this.coldChange;
      }

      public float getHeatChange() {
         return this.heatChange;
      }

      public float getTickChangePm() {
         return this.tickChangePm;
      }

      public float getTickChange() {
         return this.tickChange;
      }

      public float getChangeSpeed() {
         return this.changeSpeed;
      }

      public float getDamageState() {
         return this.damageState;
      }

      public float getDebugColdBodyBonus() {
         return this.debugColdBodyBonus;
      }

      public float getDebugHeatBodyBonus() {
         return this.debugHeatBodyBonus;
      }

      public float getDebugColdClothBonus() {
         return this.debugColdClothBonus;
      }

      public float getDebugHeatClothBonus() {
         return this.debugHeatClothBonus;
      }

      public float getUndercooled() {
         return this.undercooled;
      }

      public float getOverheated() {
         return this.overheated;
      }

      public float getWindChillAmount() {
         return this.windChillAmount;
      }

      private void calcTickChange(float var1, float var2, float var3) {
         this.changeSpeed = ClimateManager.clamp01(var3);
         this.tickChangePm = ClimateManager.lerp(this.changeSpeed, var1, var2);
         if (this.tickChangePm != 0.0F) {
            this.tickChange = Temperature.getFractionForRealTimeRatePerMin(this.tickChangePm);
         }

      }

      private void calcTickChangeOLD(float var1, float var2, float var3) {
         var1 = Math.min(var1, var2);
         var2 = Math.max(var1, var2);
         this.tickChangePm = ClimateManager.clamp(var1, var2, var3);
         this.changeSpeed = (this.tickChangePm - var1) / (var2 - var1);
         if (var1 < 0.0F) {
            this.changeSpeed = 1.0F - this.changeSpeed;
         }

         if (this.tickChangePm != 0.0F) {
            this.tickChange = Temperature.getFractionForRealTimeRatePerMin(this.tickChangePm);
         }

      }
   }
}
