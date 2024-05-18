package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.EnumConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerSettingsManager;

public class SandboxOptions {
   public static SandboxOptions instance = new SandboxOptions();
   public int Speed = 3;
   public SandboxOptions.EnumSandboxOption Zombies = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "Zombies", 5, 3)).setTranslation("ZombieCount");
   public SandboxOptions.EnumSandboxOption Distribution = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "Distribution", 2, 1)).setTranslation("ZombieDistribution");
   public SandboxOptions.EnumSandboxOption DayLength = new SandboxOptions.EnumSandboxOption(this, "DayLength", 26, 2);
   public SandboxOptions.EnumSandboxOption StartYear = new SandboxOptions.EnumSandboxOption(this, "StartYear", 100, 1);
   public SandboxOptions.EnumSandboxOption StartMonth = new SandboxOptions.EnumSandboxOption(this, "StartMonth", 12, 7);
   public SandboxOptions.EnumSandboxOption StartDay = new SandboxOptions.EnumSandboxOption(this, "StartDay", 31, 23);
   public SandboxOptions.EnumSandboxOption StartTime = new SandboxOptions.EnumSandboxOption(this, "StartTime", 9, 2);
   public SandboxOptions.EnumSandboxOption WaterShut = (new SandboxOptions.EnumSandboxOption(this, "WaterShut", 8, 2)).setValueTranslation("Shutoff");
   public SandboxOptions.EnumSandboxOption ElecShut = (new SandboxOptions.EnumSandboxOption(this, "ElecShut", 8, 2)).setValueTranslation("Shutoff");
   public SandboxOptions.IntegerSandboxOption WaterShutModifier = (SandboxOptions.IntegerSandboxOption)(new SandboxOptions.IntegerSandboxOption(this, "WaterShutModifier", -1, Integer.MAX_VALUE, 14)).setTranslation("WaterShut");
   public SandboxOptions.IntegerSandboxOption ElecShutModifier = (SandboxOptions.IntegerSandboxOption)(new SandboxOptions.IntegerSandboxOption(this, "ElecShutModifier", -1, Integer.MAX_VALUE, 14)).setTranslation("ElecShut");
   public SandboxOptions.EnumSandboxOption FoodLoot = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "FoodLoot", 5, 2)).setValueTranslation("Rarity").setTranslation("LootFood");
   public SandboxOptions.EnumSandboxOption WeaponLoot = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "WeaponLoot", 5, 2)).setValueTranslation("Rarity").setTranslation("LootWeapon");
   public SandboxOptions.EnumSandboxOption OtherLoot = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "OtherLoot", 5, 2)).setValueTranslation("Rarity").setTranslation("LootOther");
   public SandboxOptions.EnumSandboxOption Temperature = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "Temperature", 5, 3)).setTranslation("WorldTemperature");
   public SandboxOptions.EnumSandboxOption Rain = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "Rain", 5, 3)).setTranslation("RainAmount");
   public SandboxOptions.EnumSandboxOption ErosionSpeed = new SandboxOptions.EnumSandboxOption(this, "ErosionSpeed", 5, 3);
   public SandboxOptions.IntegerSandboxOption ErosionDays = new SandboxOptions.IntegerSandboxOption(this, "ErosionDays", -1, 36500, 0);
   public SandboxOptions.DoubleSandboxOption XpMultiplier = new SandboxOptions.DoubleSandboxOption(this, "XpMultiplier", 0.001D, 1000.0D, 1.0D);
   public SandboxOptions.EnumSandboxOption Farming = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "Farming", 5, 3)).setTranslation("FarmingSpeed");
   public SandboxOptions.EnumSandboxOption CompostTime = new SandboxOptions.EnumSandboxOption(this, "CompostTime", 8, 2);
   public SandboxOptions.EnumSandboxOption StatsDecrease = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "StatsDecrease", 5, 3)).setTranslation("StatDecrease");
   public SandboxOptions.EnumSandboxOption NatureAbundance = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "NatureAbundance", 5, 3)).setTranslation("NatureAmount");
   public SandboxOptions.EnumSandboxOption Alarm = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "Alarm", 6, 4)).setTranslation("HouseAlarmFrequency");
   public SandboxOptions.EnumSandboxOption LockedHouses = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "LockedHouses", 6, 4)).setTranslation("LockedHouseFrequency");
   public SandboxOptions.BooleanSandboxOption StarterKit = new SandboxOptions.BooleanSandboxOption(this, "StarterKit", false);
   public SandboxOptions.BooleanSandboxOption Nutrition = new SandboxOptions.BooleanSandboxOption(this, "Nutrition", false);
   public SandboxOptions.EnumSandboxOption FoodRotSpeed = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "FoodRotSpeed", 5, 3)).setTranslation("FoodSpoil");
   public SandboxOptions.EnumSandboxOption FridgeFactor = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "FridgeFactor", 5, 3)).setTranslation("FridgeEffect");
   public SandboxOptions.EnumSandboxOption LootRespawn = (new SandboxOptions.EnumSandboxOption(this, "LootRespawn", 5, 1)).setValueTranslation("Respawn");
   public SandboxOptions.IntegerSandboxOption SeenHoursPreventLootRespawn = new SandboxOptions.IntegerSandboxOption(this, "SeenHoursPreventLootRespawn", 0, Integer.MAX_VALUE, 0);
   public SandboxOptions.EnumSandboxOption TimeSinceApo = new SandboxOptions.EnumSandboxOption(this, "TimeSinceApo", 13, 1);
   public SandboxOptions.EnumSandboxOption PlantResilience = new SandboxOptions.EnumSandboxOption(this, "PlantResilience", 5, 3);
   public SandboxOptions.EnumSandboxOption PlantAbundance = (new SandboxOptions.EnumSandboxOption(this, "PlantAbundance", 5, 3)).setValueTranslation("NatureAmount");
   public SandboxOptions.EnumSandboxOption EndRegen = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "EndRegen", 5, 3)).setTranslation("EnduranceRegen");
   public SandboxOptions.EnumSandboxOption Helicopter = (new SandboxOptions.EnumSandboxOption(this, "Helicopter", 4, 2)).setValueTranslation("HelicopterFreq");
   public SandboxOptions.EnumSandboxOption MetaEvent = (new SandboxOptions.EnumSandboxOption(this, "MetaEvent", 3, 2)).setValueTranslation("MetaEventFreq");
   public SandboxOptions.EnumSandboxOption SleepingEvent = (new SandboxOptions.EnumSandboxOption(this, "SleepingEvent", 3, 2)).setValueTranslation("MetaEventFreq");
   public SandboxOptions.DoubleSandboxOption GeneratorFuelConsumption = new SandboxOptions.DoubleSandboxOption(this, "GeneratorFuelConsumption", 0.0D, 100.0D, 1.0D);
   public SandboxOptions.EnumSandboxOption GeneratorSpawning = new SandboxOptions.EnumSandboxOption(this, "GeneratorSpawning", 5, 3);
   public SandboxOptions.EnumSandboxOption SurvivorHouseChance = new SandboxOptions.EnumSandboxOption(this, "SurvivorHouseChance", 6, 3);
   public SandboxOptions.EnumSandboxOption AnnotatedMapChance = new SandboxOptions.EnumSandboxOption(this, "AnnotatedMapChance", 6, 4);
   public SandboxOptions.IntegerSandboxOption CharacterFreePoints = new SandboxOptions.IntegerSandboxOption(this, "CharacterFreePoints", -100, 100, 0);
   public SandboxOptions.EnumSandboxOption ConstructionBonusPoints = new SandboxOptions.EnumSandboxOption(this, "ConstructionBonusPoints", 5, 3);
   public SandboxOptions.EnumSandboxOption NightDarkness = new SandboxOptions.EnumSandboxOption(this, "NightDarkness", 4, 3);
   public SandboxOptions.BooleanSandboxOption BoneFracture = new SandboxOptions.BooleanSandboxOption(this, "BoneFracture", true);
   public SandboxOptions.EnumSandboxOption InjurySeverity = new SandboxOptions.EnumSandboxOption(this, "InjurySeverity", 3, 2);
   public SandboxOptions.IntegerSandboxOption HoursForCorpseRemoval = new SandboxOptions.IntegerSandboxOption(this, "HoursForCorpseRemoval", -1, Integer.MAX_VALUE, -1);
   public SandboxOptions.EnumSandboxOption DecayingCorpseHealthImpact = new SandboxOptions.EnumSandboxOption(this, "DecayingCorpseHealthImpact", 4, 3);
   public SandboxOptions.EnumSandboxOption BloodLevel = new SandboxOptions.EnumSandboxOption(this, "BloodLevel", 5, 3);
   public SandboxOptions.EnumSandboxOption ClothingDegradation = new SandboxOptions.EnumSandboxOption(this, "ClothingDegradation", 4, 3);
   public SandboxOptions.BooleanSandboxOption FireSpread = new SandboxOptions.BooleanSandboxOption(this, "FireSpread", true);
   public SandboxOptions.IntegerSandboxOption DaysForRottenFoodRemoval = new SandboxOptions.IntegerSandboxOption(this, "DaysForRottenFoodRemoval", -1, Integer.MAX_VALUE, -1);
   public SandboxOptions.BooleanSandboxOption AllowExteriorGenerator = new SandboxOptions.BooleanSandboxOption(this, "AllowExteriorGenerator", true);
   public SandboxOptions.EnumSandboxOption MaxFogIntensity = new SandboxOptions.EnumSandboxOption(this, "MaxFogIntensity", 3, 1);
   public SandboxOptions.EnumSandboxOption MaxRainFxIntensity = new SandboxOptions.EnumSandboxOption(this, "MaxRainFxIntensity", 3, 1);
   public SandboxOptions.BooleanSandboxOption EnableSnowOnGround = new SandboxOptions.BooleanSandboxOption(this, "EnableSnowOnGround", true);
   public SandboxOptions.BooleanSandboxOption EnableVehicles = new SandboxOptions.BooleanSandboxOption(this, "EnableVehicles", true);
   public SandboxOptions.EnumSandboxOption CarSpawnRate = new SandboxOptions.EnumSandboxOption(this, "CarSpawnRate", 5, 4);
   public SandboxOptions.DoubleSandboxOption ZombieAttractionMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieAttractionMultiplier", 0.0D, 100.0D, 1.0D);
   public SandboxOptions.BooleanSandboxOption VehicleEasyUse = new SandboxOptions.BooleanSandboxOption(this, "VehicleEasyUse", false);
   public SandboxOptions.EnumSandboxOption InitialGas = new SandboxOptions.EnumSandboxOption(this, "InitialGas", 6, 3);
   public SandboxOptions.EnumSandboxOption LockedCar = new SandboxOptions.EnumSandboxOption(this, "LockedCar", 6, 4);
   public SandboxOptions.DoubleSandboxOption CarGasConsumption = new SandboxOptions.DoubleSandboxOption(this, "CarGasConsumption", 0.0D, 100.0D, 1.0D);
   public SandboxOptions.EnumSandboxOption CarGeneralCondition = new SandboxOptions.EnumSandboxOption(this, "CarGeneralCondition", 5, 3);
   public SandboxOptions.EnumSandboxOption CarDamageOnImpact = new SandboxOptions.EnumSandboxOption(this, "CarDamageOnImpact", 5, 3);
   public SandboxOptions.EnumSandboxOption DamageToPlayerFromHitByACar = new SandboxOptions.EnumSandboxOption(this, "DamageToPlayerFromHitByACar", 5, 1);
   public SandboxOptions.BooleanSandboxOption TrafficJam = new SandboxOptions.BooleanSandboxOption(this, "TrafficJam", true);
   public SandboxOptions.EnumSandboxOption CarAlarm = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "CarAlarm", 6, 4)).setTranslation("CarAlarmFrequency");
   public SandboxOptions.BooleanSandboxOption PlayerDamageFromCrash = new SandboxOptions.BooleanSandboxOption(this, "PlayerDamageFromCrash", true);
   public SandboxOptions.DoubleSandboxOption SirenShutoffHours = new SandboxOptions.DoubleSandboxOption(this, "SirenShutoffHours", 0.0D, 168.0D, 0.0D);
   public SandboxOptions.EnumSandboxOption ChanceHasGas = new SandboxOptions.EnumSandboxOption(this, "ChanceHasGas", 3, 2);
   public SandboxOptions.EnumSandboxOption RecentlySurvivorVehicles = new SandboxOptions.EnumSandboxOption(this, "RecentlySurvivorVehicles", 3, 2);
   public final SandboxOptions.ZombieLore Lore = new SandboxOptions.ZombieLore();
   public final SandboxOptions.ZombieConfig zombieConfig = new SandboxOptions.ZombieConfig();
   protected ArrayList options = new ArrayList();
   protected HashMap optionByName = new HashMap();
   public final int FIRST_YEAR = 1993;
   private final int SANDBOX_VERSION = 4;

   public SandboxOptions() {
      this.Lore.Speed = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Speed", 4, 2)).setTranslation("ZSpeed");
      this.Lore.Strength = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Strength", 4, 2)).setTranslation("ZStrength");
      this.Lore.Toughness = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Toughness", 4, 2)).setTranslation("ZToughness");
      this.Lore.Transmission = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Transmission", 3, 1)).setTranslation("ZTransmission");
      this.Lore.Mortality = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Mortality", 7, 5)).setTranslation("ZInfectionMortality");
      this.Lore.Reanimate = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Reanimate", 6, 3)).setTranslation("ZReanimateTime");
      this.Lore.Cognition = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Cognition", 4, 3)).setTranslation("ZCognition");
      this.Lore.Memory = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Memory", 4, 2)).setTranslation("ZMemory");
      this.Lore.Decomp = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Decomp", 4, 1)).setTranslation("ZDecomposition");
      this.Lore.Sight = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Sight", 3, 2)).setTranslation("ZSight");
      this.Lore.Hearing = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Hearing", 3, 2)).setTranslation("ZHearing");
      this.Lore.Smell = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.Smell", 3, 2)).setTranslation("ZSmell");
      this.Lore.ThumpNoChasing = new SandboxOptions.BooleanSandboxOption(this, "ZombieLore.ThumpNoChasing", true);
      this.Lore.ThumpOnConstruction = new SandboxOptions.BooleanSandboxOption(this, "ZombieLore.ThumpOnConstruction", true);
      this.Lore.ActiveOnly = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "ZombieLore.ActiveOnly", 3, 1)).setTranslation("ActiveOnly");
      this.Lore.TriggerHouseAlarm = new SandboxOptions.BooleanSandboxOption(this, "ZombieLore.TriggerHouseAlarm", false);
      this.zombieConfig.PopulationMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.PopulationMultiplier", 0.0D, 4.0D, 1.0D);
      this.zombieConfig.PopulationStartMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.PopulationStartMultiplier", 0.0D, 4.0D, 1.0D);
      this.zombieConfig.PopulationPeakMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.PopulationPeakMultiplier", 0.0D, 4.0D, 1.5D);
      this.zombieConfig.PopulationPeakDay = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.PopulationPeakDay", 1, 365, 28);
      this.zombieConfig.RespawnHours = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RespawnHours", 0.0D, 8760.0D, 72.0D);
      this.zombieConfig.RespawnUnseenHours = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RespawnUnseenHours", 0.0D, 8760.0D, 16.0D);
      this.zombieConfig.RespawnMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RespawnMultiplier", 0.0D, 1.0D, 0.1D);
      this.zombieConfig.RedistributeHours = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RedistributeHours", 0.0D, 8760.0D, 12.0D);
      this.zombieConfig.FollowSoundDistance = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.FollowSoundDistance", 10, 1000, 100);
      this.zombieConfig.RallyGroupSize = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.RallyGroupSize", 0, 1000, 20);
      this.zombieConfig.RallyTravelDistance = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.RallyTravelDistance", 5, 50, 20);
      this.zombieConfig.RallyGroupSeparation = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.RallyGroupSeparation", 5, 25, 15);
      this.zombieConfig.RallyGroupRadius = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.RallyGroupRadius", 1, 10, 3);
      this.loadGameFile("Survival");
      this.setDefaultsToCurrentValues();
   }

   public static SandboxOptions getInstance() {
      return instance;
   }

   public void toLua() {
      KahluaTable var1 = (KahluaTable)LuaManager.env.rawget("SandboxVars");

      for(int var2 = 0; var2 < this.options.size(); ++var2) {
         ((SandboxOptions.SandboxOption)this.options.get(var2)).toTable(var1);
      }

   }

   public void updateFromLua() {
      if (Core.GameMode.equals("LastStand")) {
         GameTime.instance.multiplierBias = 1.2F;
      }

      KahluaTable var1 = (KahluaTable)LuaManager.env.rawget("SandboxVars");

      for(int var2 = 0; var2 < this.options.size(); ++var2) {
         ((SandboxOptions.SandboxOption)this.options.get(var2)).fromTable(var1);
      }

      switch(this.Speed) {
      case 1:
         GameTime.instance.multiplierBias = 0.8F;
         break;
      case 2:
         GameTime.instance.multiplierBias = 0.9F;
         break;
      case 3:
         GameTime.instance.multiplierBias = 1.0F;
         break;
      case 4:
         GameTime.instance.multiplierBias = 1.1F;
         break;
      case 5:
         GameTime.instance.multiplierBias = 1.2F;
      }

      if (this.Zombies.getValue() == 1) {
         VirtualZombieManager.instance.MaxRealZombies = 400;
      }

      if (this.Zombies.getValue() == 2) {
         VirtualZombieManager.instance.MaxRealZombies = 300;
      }

      if (this.Zombies.getValue() == 3) {
         VirtualZombieManager.instance.MaxRealZombies = 200;
      }

      if (this.Zombies.getValue() == 4) {
         VirtualZombieManager.instance.MaxRealZombies = 100;
      }

      if (this.Zombies.getValue() == 5) {
         VirtualZombieManager.instance.MaxRealZombies = 0;
      }

      this.applySettings();
   }

   public int randomWaterShut(int var1) {
      switch(var1) {
      case 2:
         return Rand.Next(0, 30);
      case 3:
         return Rand.Next(0, 60);
      case 4:
         return Rand.Next(0, 180);
      case 5:
         return Rand.Next(0, 360);
      case 6:
         return Rand.Next(0, 1800);
      case 7:
         return Rand.Next(60, 180);
      case 8:
         return Rand.Next(180, 360);
      default:
         return -1;
      }
   }

   public int randomElectricityShut(int var1) {
      switch(var1) {
      case 2:
         return Rand.Next(14, 30);
      case 3:
         return Rand.Next(14, 60);
      case 4:
         return Rand.Next(14, 180);
      case 5:
         return Rand.Next(14, 360);
      case 6:
         return Rand.Next(14, 1800);
      case 7:
         return Rand.Next(60, 180);
      case 8:
         return Rand.Next(180, 360);
      default:
         return -1;
      }
   }

   public int getTemperatureModifier() {
      return this.Temperature.getValue();
   }

   public int getRainModifier() {
      return this.Rain.getValue();
   }

   public int getErosionSpeed() {
      return this.ErosionSpeed.getValue();
   }

   public int getFoodLootModifier() {
      return this.FoodLoot.getValue();
   }

   public int getWeaponLootModifier() {
      return this.WeaponLoot.getValue();
   }

   public int getOtherLootModifier() {
      return this.OtherLoot.getValue();
   }

   public int getWaterShutModifier() {
      return this.WaterShutModifier.getValue();
   }

   public int getElecShutModifier() {
      return this.ElecShutModifier.getValue();
   }

   public int getTimeSinceApo() {
      return this.TimeSinceApo.getValue();
   }

   public double getEnduranceRegenMultiplier() {
      switch(this.EndRegen.getValue()) {
      case 1:
         return 1.8D;
      case 2:
         return 1.3D;
      case 3:
      default:
         return 1.0D;
      case 4:
         return 0.7D;
      case 5:
         return 0.4D;
      }
   }

   public double getStatsDecreaseMultiplier() {
      switch(this.StatsDecrease.getValue()) {
      case 1:
         return 2.0D;
      case 2:
         return 1.6D;
      case 3:
      default:
         return 1.0D;
      case 4:
         return 0.8D;
      case 5:
         return 0.65D;
      }
   }

   public int getDayLengthMinutes() {
      switch(this.DayLength.getValue()) {
      case 1:
         return 15;
      case 2:
         return 30;
      default:
         return (this.DayLength.getValue() - 2) * 60;
      }
   }

   public int getDayLengthMinutesDefault() {
      switch(this.DayLength.getDefaultValue()) {
      case 1:
         return 15;
      case 2:
         return 30;
      default:
         return (this.DayLength.getDefaultValue() - 2) * 60;
      }
   }

   public int getCompostHours() {
      switch(this.CompostTime.getValue()) {
      case 1:
         return 168;
      case 2:
         return 336;
      case 3:
         return 504;
      case 4:
         return 672;
      case 5:
         return 1008;
      case 6:
         return 1344;
      case 7:
         return 1680;
      case 8:
         return 2016;
      default:
         return 336;
      }
   }

   public void applySettings() {
      GameTime.instance.setStartYear(this.getFirstYear() + this.StartYear.getValue() - 1);
      GameTime.instance.setStartMonth(this.StartMonth.getValue() - 1);
      GameTime.instance.setStartDay(this.StartDay.getValue() - 1);
      GameTime.instance.setMinutesPerDay((float)this.getDayLengthMinutes());
      if (this.StartTime.getValue() == 1) {
         GameTime.instance.setStartTimeOfDay(7.0F);
      } else if (this.StartTime.getValue() == 2) {
         GameTime.instance.setStartTimeOfDay(9.0F);
      } else if (this.StartTime.getValue() == 3) {
         GameTime.instance.setStartTimeOfDay(12.0F);
      } else if (this.StartTime.getValue() == 4) {
         GameTime.instance.setStartTimeOfDay(14.0F);
      } else if (this.StartTime.getValue() == 5) {
         GameTime.instance.setStartTimeOfDay(17.0F);
      } else if (this.StartTime.getValue() == 6) {
         GameTime.instance.setStartTimeOfDay(21.0F);
      } else if (this.StartTime.getValue() == 7) {
         GameTime.instance.setStartTimeOfDay(0.0F);
      } else if (this.StartTime.getValue() == 8) {
         GameTime.instance.setStartTimeOfDay(2.0F);
      } else if (this.StartTime.getValue() == 9) {
         GameTime.instance.TimeOfDay = 5.0F;
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      var1.put((byte)83);
      var1.put((byte)65);
      var1.put((byte)78);
      var1.put((byte)68);
      var1.putInt(143);
      var1.putInt(4);
      var1.putInt(this.options.size());

      for(int var2 = 0; var2 < this.options.size(); ++var2) {
         SandboxOptions.SandboxOption var3 = (SandboxOptions.SandboxOption)this.options.get(var2);
         GameWindow.WriteStringUTF(var1, var3.asConfigOption().getName());
         GameWindow.WriteStringUTF(var1, var3.asConfigOption().getValueAsString());
      }

   }

   public void load(ByteBuffer var1) throws IOException {
      var1.mark();
      byte var3 = var1.get();
      byte var4 = var1.get();
      byte var5 = var1.get();
      byte var6 = var1.get();
      int var2;
      if (var3 == 83 && var4 == 65 && var5 == 78 && var6 == 68) {
         var2 = var1.getInt();
      } else {
         var2 = 41;
         var1.reset();
      }

      int var7;
      int var8;
      int var9;
      if (var2 >= 88) {
         var7 = 2;
         if (var2 >= 131) {
            var7 = var1.getInt();
         }

         var8 = var1.getInt();

         for(var9 = 0; var9 < var8; ++var9) {
            String var13 = GameWindow.ReadStringUTF(var1);
            String var11 = GameWindow.ReadStringUTF(var1);
            var13 = this.upgradeOptionName(var13, var7);
            var11 = this.upgradeOptionValue(var13, var11, var7);
            SandboxOptions.SandboxOption var12 = (SandboxOptions.SandboxOption)this.optionByName.get(var13);
            if (var12 == null) {
               DebugLog.log("ERROR unknown SandboxOption \"" + var13 + "\"");
            } else {
               var12.asConfigOption().parse(var11);
            }
         }

      } else {
         this.Zombies.setValue(var1.getInt());
         this.Distribution.setValue(var1.getInt());
         var7 = var1.getInt();
         this.Speed = var1.getInt();
         this.DayLength.setValue(var1.getInt());
         if (var2 >= 66) {
            this.StartYear.setValue(var1.getInt());
         }

         this.StartMonth.setValue(var1.getInt());
         if (var2 >= 66) {
            this.StartDay.setValue(var1.getInt());
         }

         this.StartTime.setValue(var1.getInt());
         this.WaterShutModifier.setValue(var1.getInt());
         this.ElecShutModifier.setValue(var1.getInt());
         this.FoodLoot.setValue(var1.getInt());
         this.Temperature.setValue(var1.getInt());
         this.Rain.setValue(var1.getInt());
         if (var2 >= 45) {
            var8 = var1.getInt();
            this.ErosionSpeed.setValue(var1.getInt());
            this.XpMultiplier.setValue(var1.getDouble());
            if (var2 >= 89) {
               this.ZombieAttractionMultiplier.setValue(var1.getDouble());
               this.VehicleEasyUse.setValue(var1.get() == 1);
            }

            this.Farming.setValue(var1.getInt());
            this.WeaponLoot.setValue(var1.getInt());
            this.OtherLoot.setValue(var1.getInt());
            this.StatsDecrease.setValue(var1.getInt());
            this.NatureAbundance.setValue(var1.getInt());
            this.Alarm.setValue(var1.getInt());
            this.LockedHouses.setValue(var1.getInt());
            this.FoodRotSpeed.setValue(var1.getInt());
            this.FridgeFactor.setValue(var1.getInt());
            if (var2 < 67) {
               var9 = var1.getInt();
               if (var2 >= 63) {
                  int var10 = var1.getInt();
               }
            }

            this.LootRespawn.setValue(var1.getInt());
            this.StarterKit.setValue(var1.get() == 1);
            if (var2 >= 86) {
               this.Nutrition.setValue(var1.get() == 1);
            }

            if (var2 >= 77) {
               this.Lore.ThumpNoChasing.setValue(var1.get() == 1);
            }
         }

         this.Lore.Speed.setValue(var1.getInt());
         this.Lore.Strength.setValue(var1.getInt());
         this.Lore.Toughness.setValue(var1.getInt());
         this.Lore.Transmission.setValue(var1.getInt());
         this.Lore.Mortality.setValue(var1.getInt());
         this.Lore.Reanimate.setValue(var1.getInt());
         this.Lore.Cognition.setValue(var1.getInt());
         this.Lore.Memory.setValue(var1.getInt());
         this.Lore.Decomp.setValue(var1.getInt());
         this.Lore.Sight.setValue(var1.getInt());
         this.Lore.Hearing.setValue(var1.getInt());
         this.Lore.Smell.setValue(var1.getInt());
         if (var2 >= 110) {
            this.Lore.ThumpOnConstruction.setValue(var1.get() == 1);
         }

         if (var2 >= 50) {
            this.TimeSinceApo.setValue(var1.getInt());
         }

         if (var2 >= 51) {
            this.PlantResilience.setValue(var1.getInt());
            this.PlantAbundance.setValue(var1.getInt());
         }

         if (var2 >= 52) {
            this.EndRegen.setValue(var1.getInt());
         }

         if (var2 >= 90) {
            this.Helicopter.setValue(var1.getInt());
            this.MetaEvent.setValue(var1.getInt());
            this.SleepingEvent.setValue(var1.getInt());
         }

         if (var2 >= 110) {
            this.GeneratorSpawning.setValue(var1.getInt());
            this.GeneratorFuelConsumption.setValue(var1.getDouble());
            this.SurvivorHouseChance.setValue(var1.getInt());
            this.AnnotatedMapChance.setValue(var1.getInt());
            this.CharacterFreePoints.setValue(var1.getInt());
         }

         if (var2 < 42) {
            this.DayLength.setValue(this.DayLength.getValue() + 1);
         }

      }
   }

   public int getFirstYear() {
      return 1993;
   }

   private static String[] parseName(String var0) {
      String[] var1 = new String[]{null, var0};
      if (var0.contains(".")) {
         String[] var2 = var0.split("\\.");
         if (var2.length == 2) {
            var1[0] = var2[0];
            var1[1] = var2[1];
         }
      }

      return var1;
   }

   protected SandboxOptions addOption(SandboxOptions.SandboxOption var1) {
      this.options.add(var1);
      this.optionByName.put(var1.asConfigOption().getName(), var1);
      return this;
   }

   public int getNumOptions() {
      return this.options.size();
   }

   public SandboxOptions.SandboxOption getOptionByIndex(int var1) {
      return (SandboxOptions.SandboxOption)this.options.get(var1);
   }

   public SandboxOptions.SandboxOption getOptionByName(String var1) {
      return (SandboxOptions.SandboxOption)this.optionByName.get(var1);
   }

   public void set(String var1, Object var2) {
      if (var1 != null && var2 != null) {
         SandboxOptions.SandboxOption var3 = (SandboxOptions.SandboxOption)this.optionByName.get(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("unknown SandboxOption \"" + var1 + "\"");
         } else {
            var3.asConfigOption().setValueFromObject(var2);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void copyValuesFrom(SandboxOptions var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         for(int var2 = 0; var2 < this.options.size(); ++var2) {
            ((SandboxOptions.SandboxOption)this.options.get(var2)).asConfigOption().setValueFromObject(((SandboxOptions.SandboxOption)var1.options.get(var2)).asConfigOption().getValueAsObject());
         }

      }
   }

   public void resetToDefault() {
      for(int var1 = 0; var1 < this.options.size(); ++var1) {
         ((SandboxOptions.SandboxOption)this.options.get(var1)).asConfigOption().resetToDefault();
      }

   }

   public void setDefaultsToCurrentValues() {
      for(int var1 = 0; var1 < this.options.size(); ++var1) {
         ((SandboxOptions.SandboxOption)this.options.get(var1)).asConfigOption().setDefaultToCurrentValue();
      }

   }

   public SandboxOptions newCopy() {
      SandboxOptions var1 = new SandboxOptions();
      var1.copyValuesFrom(this);
      return var1;
   }

   public static boolean isValidPresetName(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         return !var0.contains("/") && !var0.contains("\\") && !var0.contains(":") && !var0.contains(";") && !var0.contains("\"") && !var0.contains(".");
      } else {
         return false;
      }
   }

   private boolean readTextFile(String var1, boolean var2) {
      ConfigFile var3 = new ConfigFile();
      if (!var3.read(var1)) {
         return false;
      } else {
         int var4 = var3.getVersion();
         HashSet var5 = null;
         int var6;
         if (var2 && var4 == 1) {
            var5 = new HashSet();

            for(var6 = 0; var6 < this.options.size(); ++var6) {
               if ("ZombieLore".equals(((SandboxOptions.SandboxOption)this.options.get(var6)).getTableName())) {
                  var5.add(((SandboxOptions.SandboxOption)this.options.get(var6)).getShortName());
               }
            }
         }

         for(var6 = 0; var6 < var3.getOptions().size(); ++var6) {
            ConfigOption var7 = (ConfigOption)var3.getOptions().get(var6);
            String var8 = var7.getName();
            String var9 = var7.getValueAsString();
            if (var5 != null && var5.contains(var8)) {
               var8 = "ZombieLore." + var8;
            }

            if (var2 && var4 == 1) {
               if ("WaterShutModifier".equals(var8)) {
                  var8 = "WaterShut";
               } else if ("ElecShutModifier".equals(var8)) {
                  var8 = "ElecShut";
               }
            }

            var8 = this.upgradeOptionName(var8, var4);
            var9 = this.upgradeOptionValue(var8, var9, var4);
            SandboxOptions.SandboxOption var10 = (SandboxOptions.SandboxOption)this.optionByName.get(var8);
            if (var10 != null) {
               var10.asConfigOption().parse(var9);
            }
         }

         return true;
      }
   }

   private boolean writeTextFile(String var1, int var2) {
      ConfigFile var3 = new ConfigFile();
      ArrayList var4 = new ArrayList();
      Iterator var5 = this.options.iterator();

      while(var5.hasNext()) {
         SandboxOptions.SandboxOption var6 = (SandboxOptions.SandboxOption)var5.next();
         var4.add(var6.asConfigOption());
      }

      return var3.write(var1, var2, var4);
   }

   public boolean loadServerTextFile(String var1) {
      return this.readTextFile(ServerSettingsManager.instance.getNameInSettingsFolder(var1 + "_sandbox.ini"), false);
   }

   public boolean loadServerLuaFile(String var1) {
      boolean var2 = this.readLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(var1 + "_SandboxVars.lua"));
      if (this.Lore.Speed.getValue() == 1) {
         this.Lore.Speed.setValue(2);
      }

      return var2;
   }

   public boolean saveServerLuaFile(String var1) {
      return this.writeLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(var1 + "_SandboxVars.lua"), false);
   }

   public boolean loadPresetFile(String var1) {
      return this.readTextFile(LuaManager.getSandboxCacheDir() + File.separator + var1 + ".cfg", true);
   }

   public boolean savePresetFile(String var1) {
      return !isValidPresetName(var1) ? false : this.writeTextFile(LuaManager.getSandboxCacheDir() + File.separator + var1 + ".cfg", 4);
   }

   public boolean loadGameFile(String var1) {
      File var2 = new File("media/lua/shared/Sandbox/" + var1 + ".lua");
      if (!var2.exists()) {
         throw new RuntimeException("media/lua/shared/Sandbox/" + var1 + ".lua not found");
      } else {
         try {
            LuaManager.loaded.remove(var2.getAbsolutePath().replace("\\", "/"));
            Object var3 = LuaManager.RunLua(var2.getAbsolutePath());
            if (!(var3 instanceof KahluaTable)) {
               throw new RuntimeException(var2.getName() + " must return a SandboxVars table");
            } else {
               for(int var4 = 0; var4 < this.options.size(); ++var4) {
                  ((SandboxOptions.SandboxOption)this.options.get(var4)).fromTable((KahluaTable)var3);
               }

               return true;
            }
         } catch (Exception var5) {
            ExceptionLogger.logException(var5);
            return false;
         }
      }
   }

   public boolean saveGameFile(String var1) {
      return !Core.bDebug ? false : this.writeLuaFile("media/lua/shared/Sandbox/" + var1 + ".lua", true);
   }

   private void saveCurrentGameBinFile() {
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");

      try {
         FileOutputStream var2 = new FileOutputStream(var1);
         Throwable var3 = null;

         try {
            BufferedOutputStream var4 = new BufferedOutputStream(var2);
            Throwable var5 = null;

            try {
               if (SliceY.SliceBuffer == null) {
                  SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
               }

               synchronized(SliceY.SliceBuffer) {
                  SliceY.SliceBuffer.rewind();
                  this.save(SliceY.SliceBuffer);
                  var4.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
               }
            } catch (Throwable var34) {
               var5 = var34;
               throw var34;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var32) {
                        var5.addSuppressed(var32);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (Throwable var36) {
            var3 = var36;
            throw var36;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var31) {
                     var3.addSuppressed(var31);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (Exception var38) {
         ExceptionLogger.logException(var38);
      }

   }

   public void handleOldZombiesFile1() {
      if (!GameServer.bServer) {
         String var1 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "zombies.ini";
         ConfigFile var2 = new ConfigFile();
         if (var2.read(var1)) {
            for(int var3 = 0; var3 < var2.getOptions().size(); ++var3) {
               ConfigOption var4 = (ConfigOption)var2.getOptions().get(var3);
               SandboxOptions.SandboxOption var5 = (SandboxOptions.SandboxOption)this.optionByName.get("ZombieConfig." + var4.getName());
               if (var5 != null) {
                  var5.asConfigOption().parse(var4.getValueAsString());
               }
            }
         }

      }
   }

   public void handleOldZombiesFile2() {
      if (!GameServer.bServer) {
         String var1 = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "zombies.ini";
         File var2 = new File(var1);
         if (var2.exists()) {
            try {
               DebugLog.log("deleting " + var2.getAbsolutePath());
               var2.delete();
               this.saveCurrentGameBinFile();
            } catch (Exception var4) {
               ExceptionLogger.logException(var4);
            }

         }
      }
   }

   public void handleOldServerZombiesFile() {
      if (GameServer.bServer) {
         if (this.loadServerZombiesFile(GameServer.ServerName)) {
            String var1 = ServerSettingsManager.instance.getNameInSettingsFolder(GameServer.ServerName + "_zombies.ini");

            try {
               File var2 = new File(var1);
               DebugLog.log("deleting " + var2.getAbsolutePath());
               var2.delete();
               this.saveServerLuaFile(GameServer.ServerName);
            } catch (Exception var3) {
               ExceptionLogger.logException(var3);
            }
         }

      }
   }

   public boolean loadServerZombiesFile(String var1) {
      String var2 = ServerSettingsManager.instance.getNameInSettingsFolder(var1 + "_zombies.ini");
      ConfigFile var3 = new ConfigFile();
      if (var3.read(var2)) {
         for(int var4 = 0; var4 < var3.getOptions().size(); ++var4) {
            ConfigOption var5 = (ConfigOption)var3.getOptions().get(var4);
            SandboxOptions.SandboxOption var6 = (SandboxOptions.SandboxOption)this.optionByName.get("ZombieConfig." + var5.getName());
            if (var6 != null) {
               var6.asConfigOption().parse(var5.getValueAsString());
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean readLuaFile(String var1) {
      File var2 = new File(var1);
      if (!var2.exists()) {
         return false;
      } else {
         Object var3 = LuaManager.env.rawget("SandboxVars");
         KahluaTable var4 = null;
         if (var3 instanceof KahluaTable) {
            var4 = (KahluaTable)var3;
         }

         boolean var6;
         try {
            LuaManager.loaded.remove(var2.getAbsolutePath().replace("\\", "/"));
            Object var5 = LuaManager.RunLua(var2.getAbsolutePath());
            Object var16 = LuaManager.env.rawget("SandboxVars");
            if (var16 instanceof KahluaTable) {
               KahluaTable var7 = (KahluaTable)var16;
               int var8 = 0;
               Object var9 = var7.rawget("VERSION");
               if (var9 != null) {
                  if (var9 instanceof Double) {
                     var8 = ((Double)var9).intValue();
                  } else {
                     DebugLog.log("ERROR: VERSION=\"" + var9 + "\" in " + var1);
                  }

                  var7.rawset("VERSION", (Object)null);
               }

               var7 = this.upgradeLuaTable("", var7, var8);

               for(int var10 = 0; var10 < this.options.size(); ++var10) {
                  ((SandboxOptions.SandboxOption)this.options.get(var10)).fromTable(var7);
               }
            }

            boolean var17 = true;
            return var17;
         } catch (Exception var14) {
            ExceptionLogger.logException(var14);
            var6 = false;
         } finally {
            if (var4 != null) {
               LuaManager.env.rawset("SandboxVars", var4);
            }

         }

         return var6;
      }
   }

   private boolean writeLuaFile(String var1, boolean var2) {
      File var3 = new File(var1);
      DebugLog.log("writing " + var1);

      try {
         FileWriter var4 = new FileWriter(var3);
         Throwable var5 = null;

         try {
            HashMap var6 = new HashMap();
            ArrayList var7 = new ArrayList();
            var6.put("", new ArrayList());
            Iterator var8 = this.options.iterator();

            while(var8.hasNext()) {
               SandboxOptions.SandboxOption var9 = (SandboxOptions.SandboxOption)var8.next();
               if (var9.getTableName() == null) {
                  ((ArrayList)var6.get("")).add(var9);
               } else {
                  if (var6.get(var9.getTableName()) == null) {
                     var6.put(var9.getTableName(), new ArrayList());
                     var7.add(var9.getTableName());
                  }

                  ((ArrayList)var6.get(var9.getTableName())).add(var9);
               }
            }

            String var24 = System.lineSeparator();
            if (var2) {
               var4.write("return {" + var24);
            } else {
               var4.write("SandboxVars = {" + var24);
            }

            var4.write("    VERSION = 4," + var24);
            Iterator var25 = ((ArrayList)var6.get("")).iterator();

            while(var25.hasNext()) {
               SandboxOptions.SandboxOption var10 = (SandboxOptions.SandboxOption)var25.next();
               var4.write("    " + var10.asConfigOption().getName() + " = " + var10.asConfigOption().getValueAsString() + "," + var24);
            }

            var25 = var7.iterator();

            while(var25.hasNext()) {
               String var26 = (String)var25.next();
               var4.write("    " + var26 + " = {" + var24);
               Iterator var11 = ((ArrayList)var6.get(var26)).iterator();

               while(var11.hasNext()) {
                  SandboxOptions.SandboxOption var12 = (SandboxOptions.SandboxOption)var11.next();
                  var4.write("        " + var12.getShortName() + " = " + var12.asConfigOption().getValueAsString() + "," + var24);
               }

               var4.write("    }," + var24);
            }

            var4.write("}" + System.lineSeparator());
            return true;
         } catch (Throwable var21) {
            var5 = var21;
            throw var21;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var20) {
                     var5.addSuppressed(var20);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Exception var23) {
         ExceptionLogger.logException(var23);
         return false;
      }
   }

   public void loadCurrentGameBinFile() {
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
      if (var1.exists()) {
         try {
            FileInputStream var2 = new FileInputStream(var1);
            BufferedInputStream var3 = new BufferedInputStream(var2);
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            synchronized(SliceY.SliceBuffer) {
               var3.read(SliceY.SliceBuffer.array());
               var3.close();
               SliceY.SliceBuffer.rewind();
               instance.load(SliceY.SliceBuffer);
               instance.toLua();
            }
         } catch (Exception var7) {
            ExceptionLogger.logException(var7);
         }
      }

   }

   private String upgradeOptionName(String var1, int var2) {
      return var1;
   }

   private String upgradeOptionValue(String var1, String var2, int var3) {
      if (var3 < 3 && "DayLength".equals(var1)) {
         this.DayLength.parse(var2);
         if (this.DayLength.getValue() == 8) {
            this.DayLength.setValue(14);
         } else if (this.DayLength.getValue() == 9) {
            this.DayLength.setValue(26);
         }

         var2 = this.DayLength.getValueAsString();
      }

      if (var3 < 4 && "CarSpawnRate".equals(var1)) {
         try {
            int var4 = (int)Double.parseDouble(var2);
            if (var4 > 1) {
               var2 = Integer.toString(var4 + 1);
            }
         } catch (NumberFormatException var5) {
            var5.printStackTrace();
         }
      }

      return var2;
   }

   private KahluaTable upgradeLuaTable(String var1, KahluaTable var2, int var3) {
      KahluaTable var4 = LuaManager.platform.newTable();
      KahluaTableIterator var5 = var2.iterator();

      while(var5.advance()) {
         if (!(var5.getKey() instanceof String)) {
            throw new IllegalStateException("expected a String key");
         }

         if (var5.getValue() instanceof KahluaTable) {
            KahluaTable var6 = this.upgradeLuaTable(var1 + var5.getKey() + ".", (KahluaTable)var5.getValue(), var3);
            var4.rawset(var5.getKey(), var6);
         } else {
            String var8 = this.upgradeOptionName(var1 + var5.getKey(), var3);
            String var7 = this.upgradeOptionValue(var8, var5.getValue().toString(), var3);
            var4.rawset(var8.replace(var1, ""), var7);
         }
      }

      return var4;
   }

   public void sendToServer() {
      if (GameClient.bClient) {
         GameClient.instance.sendSandboxOptionsToServer(this);
      }

   }

   public static class EnumSandboxOption extends EnumConfigOption implements SandboxOptions.SandboxOption {
      protected String translation;
      protected String tableName;
      protected String shortName;
      protected String valueTranslation;

      public EnumSandboxOption(SandboxOptions var1, String var2, int var3, int var4) {
         super(var2, var3, var4);
         String[] var5 = SandboxOptions.parseName(var2);
         this.tableName = var5[0];
         this.shortName = var5[1];
         var1.addOption(this);
      }

      public ConfigOption asConfigOption() {
         return this;
      }

      public String getShortName() {
         return this.shortName;
      }

      public String getTableName() {
         return this.tableName;
      }

      public SandboxOptions.SandboxOption setTranslation(String var1) {
         this.translation = var1;
         return this;
      }

      public String getTranslatedName() {
         return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
      }

      public String getTooltip() {
         return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
      }

      public void fromTable(KahluaTable var1) {
         Object var2;
         if (this.tableName != null) {
            var2 = var1.rawget(this.tableName);
            if (!(var2 instanceof KahluaTable)) {
               return;
            }

            var1 = (KahluaTable)var2;
         }

         var2 = var1.rawget(this.getShortName());
         if (var2 != null) {
            this.setValueFromObject(var2);
         }

      }

      public void toTable(KahluaTable var1) {
         if (this.tableName != null) {
            Object var2 = var1.rawget(this.tableName);
            if (var2 instanceof KahluaTable) {
               var1 = (KahluaTable)var2;
            } else {
               KahluaTable var3 = LuaManager.platform.newTable();
               var1.rawset(this.tableName, var3);
               var1 = var3;
            }
         }

         var1.rawset(this.getShortName(), this.getValueAsObject());
      }

      public SandboxOptions.EnumSandboxOption setValueTranslation(String var1) {
         this.valueTranslation = var1;
         return this;
      }

      public String getValueTranslation() {
         return this.valueTranslation != null ? this.valueTranslation : (this.translation == null ? this.getShortName() : this.translation);
      }

      public String getValueTranslationByIndex(int var1) {
         if (var1 >= 1 && var1 <= this.getNumValues()) {
            return Translator.getText("Sandbox_" + this.getValueTranslation() + "_option" + var1);
         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      }
   }

   public static class IntegerSandboxOption extends IntegerConfigOption implements SandboxOptions.SandboxOption {
      protected String translation;
      protected String tableName;
      protected String shortName;

      public IntegerSandboxOption(SandboxOptions var1, String var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
         String[] var6 = SandboxOptions.parseName(var2);
         this.tableName = var6[0];
         this.shortName = var6[1];
         var1.addOption(this);
      }

      public ConfigOption asConfigOption() {
         return this;
      }

      public String getShortName() {
         return this.shortName;
      }

      public String getTableName() {
         return this.tableName;
      }

      public SandboxOptions.SandboxOption setTranslation(String var1) {
         this.translation = var1;
         return this;
      }

      public String getTranslatedName() {
         return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
      }

      public String getTooltip() {
         if ("ZombieConfig".equals(this.tableName)) {
            String var1 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_help");
            String var2 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
            if (var1 == null) {
               return var2;
            } else {
               return var2 == null ? var1 : var1 + "\\n" + var2;
            }
         } else {
            return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
         }
      }

      public void fromTable(KahluaTable var1) {
         Object var2;
         if (this.tableName != null) {
            var2 = var1.rawget(this.tableName);
            if (!(var2 instanceof KahluaTable)) {
               return;
            }

            var1 = (KahluaTable)var2;
         }

         var2 = var1.rawget(this.getShortName());
         if (var2 != null) {
            this.setValueFromObject(var2);
         }

      }

      public void toTable(KahluaTable var1) {
         if (this.tableName != null) {
            Object var2 = var1.rawget(this.tableName);
            if (var2 instanceof KahluaTable) {
               var1 = (KahluaTable)var2;
            } else {
               KahluaTable var3 = LuaManager.platform.newTable();
               var1.rawset(this.tableName, var3);
               var1 = var3;
            }
         }

         var1.rawset(this.getShortName(), this.getValueAsObject());
      }
   }

   public static class DoubleSandboxOption extends DoubleConfigOption implements SandboxOptions.SandboxOption {
      protected String translation;
      protected String tableName;
      protected String shortName;

      public DoubleSandboxOption(SandboxOptions var1, String var2, double var3, double var5, double var7) {
         super(var2, var3, var5, var7);
         String[] var9 = SandboxOptions.parseName(var2);
         this.tableName = var9[0];
         this.shortName = var9[1];
         var1.addOption(this);
      }

      public ConfigOption asConfigOption() {
         return this;
      }

      public String getShortName() {
         return this.shortName;
      }

      public String getTableName() {
         return this.tableName;
      }

      public SandboxOptions.SandboxOption setTranslation(String var1) {
         this.translation = var1;
         return this;
      }

      public String getTranslatedName() {
         return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
      }

      public String getTooltip() {
         String var1;
         if ("ZombieConfig".equals(this.tableName)) {
            var1 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_help");
         } else {
            var1 = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
         }

         String var2 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
         if (var1 == null) {
            return var2;
         } else {
            return var2 == null ? var1 : var1 + "\\n" + var2;
         }
      }

      public void fromTable(KahluaTable var1) {
         Object var2;
         if (this.tableName != null) {
            var2 = var1.rawget(this.tableName);
            if (!(var2 instanceof KahluaTable)) {
               return;
            }

            var1 = (KahluaTable)var2;
         }

         var2 = var1.rawget(this.getShortName());
         if (var2 != null) {
            this.setValueFromObject(var2);
         }

      }

      public void toTable(KahluaTable var1) {
         if (this.tableName != null) {
            Object var2 = var1.rawget(this.tableName);
            if (var2 instanceof KahluaTable) {
               var1 = (KahluaTable)var2;
            } else {
               KahluaTable var3 = LuaManager.platform.newTable();
               var1.rawset(this.tableName, var3);
               var1 = var3;
            }
         }

         var1.rawset(this.getShortName(), this.getValueAsObject());
      }
   }

   public static class BooleanSandboxOption extends BooleanConfigOption implements SandboxOptions.SandboxOption {
      protected String translation;
      protected String tableName;
      protected String shortName;

      public BooleanSandboxOption(SandboxOptions var1, String var2, boolean var3) {
         super(var2, var3);
         String[] var4 = SandboxOptions.parseName(var2);
         this.tableName = var4[0];
         this.shortName = var4[1];
         var1.addOption(this);
      }

      public ConfigOption asConfigOption() {
         return this;
      }

      public String getShortName() {
         return this.shortName;
      }

      public String getTableName() {
         return this.tableName;
      }

      public SandboxOptions.SandboxOption setTranslation(String var1) {
         this.translation = var1;
         return this;
      }

      public String getTranslatedName() {
         return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
      }

      public String getTooltip() {
         return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
      }

      public void fromTable(KahluaTable var1) {
         Object var2;
         if (this.tableName != null) {
            var2 = var1.rawget(this.tableName);
            if (!(var2 instanceof KahluaTable)) {
               return;
            }

            var1 = (KahluaTable)var2;
         }

         var2 = var1.rawget(this.getShortName());
         if (var2 != null) {
            this.setValueFromObject(var2);
         }

      }

      public void toTable(KahluaTable var1) {
         if (this.tableName != null) {
            Object var2 = var1.rawget(this.tableName);
            if (var2 instanceof KahluaTable) {
               var1 = (KahluaTable)var2;
            } else {
               KahluaTable var3 = LuaManager.platform.newTable();
               var1.rawset(this.tableName, var3);
               var1 = var3;
            }
         }

         var1.rawset(this.getShortName(), this.getValueAsObject());
      }
   }

   public interface SandboxOption {
      ConfigOption asConfigOption();

      String getShortName();

      String getTableName();

      SandboxOptions.SandboxOption setTranslation(String var1);

      String getTranslatedName();

      String getTooltip();

      void fromTable(KahluaTable var1);

      void toTable(KahluaTable var1);
   }

   public static class ZombieConfig {
      public SandboxOptions.DoubleSandboxOption PopulationMultiplier;
      public SandboxOptions.DoubleSandboxOption PopulationStartMultiplier;
      public SandboxOptions.DoubleSandboxOption PopulationPeakMultiplier;
      public SandboxOptions.IntegerSandboxOption PopulationPeakDay;
      public SandboxOptions.DoubleSandboxOption RespawnHours;
      public SandboxOptions.DoubleSandboxOption RespawnUnseenHours;
      public SandboxOptions.DoubleSandboxOption RespawnMultiplier;
      public SandboxOptions.DoubleSandboxOption RedistributeHours;
      public SandboxOptions.IntegerSandboxOption FollowSoundDistance;
      public SandboxOptions.IntegerSandboxOption RallyGroupSize;
      public SandboxOptions.IntegerSandboxOption RallyTravelDistance;
      public SandboxOptions.IntegerSandboxOption RallyGroupSeparation;
      public SandboxOptions.IntegerSandboxOption RallyGroupRadius;
   }

   public static class ZombieLore {
      public SandboxOptions.EnumSandboxOption Speed;
      public SandboxOptions.EnumSandboxOption Strength;
      public SandboxOptions.EnumSandboxOption Toughness;
      public SandboxOptions.EnumSandboxOption Transmission;
      public SandboxOptions.EnumSandboxOption Mortality;
      public SandboxOptions.EnumSandboxOption Reanimate;
      public SandboxOptions.EnumSandboxOption Cognition;
      public SandboxOptions.EnumSandboxOption Memory;
      public SandboxOptions.EnumSandboxOption Decomp;
      public SandboxOptions.EnumSandboxOption Sight;
      public SandboxOptions.EnumSandboxOption Hearing;
      public SandboxOptions.EnumSandboxOption Smell;
      public SandboxOptions.BooleanSandboxOption ThumpNoChasing;
      public SandboxOptions.BooleanSandboxOption ThumpOnConstruction;
      public SandboxOptions.EnumSandboxOption ActiveOnly;
      public SandboxOptions.BooleanSandboxOption TriggerHouseAlarm;
   }
}
