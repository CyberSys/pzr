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
	public SandboxOptions.DoubleSandboxOption XpMultiplier = new SandboxOptions.DoubleSandboxOption(this, "XpMultiplier", 0.001, 1000.0, 1.0);
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
	public SandboxOptions.DoubleSandboxOption GeneratorFuelConsumption = new SandboxOptions.DoubleSandboxOption(this, "GeneratorFuelConsumption", 0.0, 100.0, 1.0);
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
	public SandboxOptions.DoubleSandboxOption ZombieAttractionMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieAttractionMultiplier", 0.0, 100.0, 1.0);
	public SandboxOptions.BooleanSandboxOption VehicleEasyUse = new SandboxOptions.BooleanSandboxOption(this, "VehicleEasyUse", false);
	public SandboxOptions.EnumSandboxOption InitialGas = new SandboxOptions.EnumSandboxOption(this, "InitialGas", 6, 3);
	public SandboxOptions.EnumSandboxOption LockedCar = new SandboxOptions.EnumSandboxOption(this, "LockedCar", 6, 4);
	public SandboxOptions.DoubleSandboxOption CarGasConsumption = new SandboxOptions.DoubleSandboxOption(this, "CarGasConsumption", 0.0, 100.0, 1.0);
	public SandboxOptions.EnumSandboxOption CarGeneralCondition = new SandboxOptions.EnumSandboxOption(this, "CarGeneralCondition", 5, 3);
	public SandboxOptions.EnumSandboxOption CarDamageOnImpact = new SandboxOptions.EnumSandboxOption(this, "CarDamageOnImpact", 5, 3);
	public SandboxOptions.EnumSandboxOption DamageToPlayerFromHitByACar = new SandboxOptions.EnumSandboxOption(this, "DamageToPlayerFromHitByACar", 5, 1);
	public SandboxOptions.BooleanSandboxOption TrafficJam = new SandboxOptions.BooleanSandboxOption(this, "TrafficJam", true);
	public SandboxOptions.EnumSandboxOption CarAlarm = (SandboxOptions.EnumSandboxOption)(new SandboxOptions.EnumSandboxOption(this, "CarAlarm", 6, 4)).setTranslation("CarAlarmFrequency");
	public SandboxOptions.BooleanSandboxOption PlayerDamageFromCrash = new SandboxOptions.BooleanSandboxOption(this, "PlayerDamageFromCrash", true);
	public SandboxOptions.DoubleSandboxOption SirenShutoffHours = new SandboxOptions.DoubleSandboxOption(this, "SirenShutoffHours", 0.0, 168.0, 0.0);
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
		this.zombieConfig.PopulationMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.PopulationMultiplier", 0.0, 4.0, 1.0);
		this.zombieConfig.PopulationStartMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.PopulationStartMultiplier", 0.0, 4.0, 1.0);
		this.zombieConfig.PopulationPeakMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.PopulationPeakMultiplier", 0.0, 4.0, 1.5);
		this.zombieConfig.PopulationPeakDay = new SandboxOptions.IntegerSandboxOption(this, "ZombieConfig.PopulationPeakDay", 1, 365, 28);
		this.zombieConfig.RespawnHours = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RespawnHours", 0.0, 8760.0, 72.0);
		this.zombieConfig.RespawnUnseenHours = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RespawnUnseenHours", 0.0, 8760.0, 16.0);
		this.zombieConfig.RespawnMultiplier = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RespawnMultiplier", 0.0, 1.0, 0.1);
		this.zombieConfig.RedistributeHours = new SandboxOptions.DoubleSandboxOption(this, "ZombieConfig.RedistributeHours", 0.0, 8760.0, 12.0);
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
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("SandboxVars");
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			((SandboxOptions.SandboxOption)this.options.get(int1)).toTable(kahluaTable);
		}
	}

	public void updateFromLua() {
		if (Core.GameMode.equals("LastStand")) {
			GameTime.instance.multiplierBias = 1.2F;
		}

		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("SandboxVars");
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			((SandboxOptions.SandboxOption)this.options.get(int1)).fromTable(kahluaTable);
		}

		switch (this.Speed) {
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

	public int randomWaterShut(int int1) {
		switch (int1) {
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

	public int randomElectricityShut(int int1) {
		switch (int1) {
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
		switch (this.EndRegen.getValue()) {
		case 1: 
			return 1.8;
		
		case 2: 
			return 1.3;
		
		case 3: 
		
		default: 
			return 1.0;
		
		case 4: 
			return 0.7;
		
		case 5: 
			return 0.4;
		
		}
	}

	public double getStatsDecreaseMultiplier() {
		switch (this.StatsDecrease.getValue()) {
		case 1: 
			return 2.0;
		
		case 2: 
			return 1.6;
		
		case 3: 
		
		default: 
			return 1.0;
		
		case 4: 
			return 0.8;
		
		case 5: 
			return 0.65;
		
		}
	}

	public int getDayLengthMinutes() {
		switch (this.DayLength.getValue()) {
		case 1: 
			return 15;
		
		case 2: 
			return 30;
		
		default: 
			return (this.DayLength.getValue() - 2) * 60;
		
		}
	}

	public int getDayLengthMinutesDefault() {
		switch (this.DayLength.getDefaultValue()) {
		case 1: 
			return 15;
		
		case 2: 
			return 30;
		
		default: 
			return (this.DayLength.getDefaultValue() - 2) * 60;
		
		}
	}

	public int getCompostHours() {
		switch (this.CompostTime.getValue()) {
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

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)83);
		byteBuffer.put((byte)65);
		byteBuffer.put((byte)78);
		byteBuffer.put((byte)68);
		byteBuffer.putInt(143);
		byteBuffer.putInt(4);
		byteBuffer.putInt(this.options.size());
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.options.get(int1);
			GameWindow.WriteStringUTF(byteBuffer, sandboxOption.asConfigOption().getName());
			GameWindow.WriteStringUTF(byteBuffer, sandboxOption.asConfigOption().getValueAsString());
		}
	}

	public void load(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.mark();
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		byte byte4 = byteBuffer.get();
		int int1;
		if (byte1 == 83 && byte2 == 65 && byte3 == 78 && byte4 == 68) {
			int1 = byteBuffer.getInt();
		} else {
			int1 = 41;
			byteBuffer.reset();
		}

		int int2;
		int int3;
		int int4;
		if (int1 >= 88) {
			int2 = 2;
			if (int1 >= 131) {
				int2 = byteBuffer.getInt();
			}

			int3 = byteBuffer.getInt();
			for (int4 = 0; int4 < int3; ++int4) {
				String string = GameWindow.ReadStringUTF(byteBuffer);
				String string2 = GameWindow.ReadStringUTF(byteBuffer);
				string = this.upgradeOptionName(string, int2);
				string2 = this.upgradeOptionValue(string, string2, int2);
				SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.optionByName.get(string);
				if (sandboxOption == null) {
					DebugLog.log("ERROR unknown SandboxOption \"" + string + "\"");
				} else {
					sandboxOption.asConfigOption().parse(string2);
				}
			}
		} else {
			this.Zombies.setValue(byteBuffer.getInt());
			this.Distribution.setValue(byteBuffer.getInt());
			int2 = byteBuffer.getInt();
			this.Speed = byteBuffer.getInt();
			this.DayLength.setValue(byteBuffer.getInt());
			if (int1 >= 66) {
				this.StartYear.setValue(byteBuffer.getInt());
			}

			this.StartMonth.setValue(byteBuffer.getInt());
			if (int1 >= 66) {
				this.StartDay.setValue(byteBuffer.getInt());
			}

			this.StartTime.setValue(byteBuffer.getInt());
			this.WaterShutModifier.setValue(byteBuffer.getInt());
			this.ElecShutModifier.setValue(byteBuffer.getInt());
			this.FoodLoot.setValue(byteBuffer.getInt());
			this.Temperature.setValue(byteBuffer.getInt());
			this.Rain.setValue(byteBuffer.getInt());
			if (int1 >= 45) {
				int3 = byteBuffer.getInt();
				this.ErosionSpeed.setValue(byteBuffer.getInt());
				this.XpMultiplier.setValue(byteBuffer.getDouble());
				if (int1 >= 89) {
					this.ZombieAttractionMultiplier.setValue(byteBuffer.getDouble());
					this.VehicleEasyUse.setValue(byteBuffer.get() == 1);
				}

				this.Farming.setValue(byteBuffer.getInt());
				this.WeaponLoot.setValue(byteBuffer.getInt());
				this.OtherLoot.setValue(byteBuffer.getInt());
				this.StatsDecrease.setValue(byteBuffer.getInt());
				this.NatureAbundance.setValue(byteBuffer.getInt());
				this.Alarm.setValue(byteBuffer.getInt());
				this.LockedHouses.setValue(byteBuffer.getInt());
				this.FoodRotSpeed.setValue(byteBuffer.getInt());
				this.FridgeFactor.setValue(byteBuffer.getInt());
				if (int1 < 67) {
					int4 = byteBuffer.getInt();
					if (int1 >= 63) {
						int int5 = byteBuffer.getInt();
					}
				}

				this.LootRespawn.setValue(byteBuffer.getInt());
				this.StarterKit.setValue(byteBuffer.get() == 1);
				if (int1 >= 86) {
					this.Nutrition.setValue(byteBuffer.get() == 1);
				}

				if (int1 >= 77) {
					this.Lore.ThumpNoChasing.setValue(byteBuffer.get() == 1);
				}
			}

			this.Lore.Speed.setValue(byteBuffer.getInt());
			this.Lore.Strength.setValue(byteBuffer.getInt());
			this.Lore.Toughness.setValue(byteBuffer.getInt());
			this.Lore.Transmission.setValue(byteBuffer.getInt());
			this.Lore.Mortality.setValue(byteBuffer.getInt());
			this.Lore.Reanimate.setValue(byteBuffer.getInt());
			this.Lore.Cognition.setValue(byteBuffer.getInt());
			this.Lore.Memory.setValue(byteBuffer.getInt());
			this.Lore.Decomp.setValue(byteBuffer.getInt());
			this.Lore.Sight.setValue(byteBuffer.getInt());
			this.Lore.Hearing.setValue(byteBuffer.getInt());
			this.Lore.Smell.setValue(byteBuffer.getInt());
			if (int1 >= 110) {
				this.Lore.ThumpOnConstruction.setValue(byteBuffer.get() == 1);
			}

			if (int1 >= 50) {
				this.TimeSinceApo.setValue(byteBuffer.getInt());
			}

			if (int1 >= 51) {
				this.PlantResilience.setValue(byteBuffer.getInt());
				this.PlantAbundance.setValue(byteBuffer.getInt());
			}

			if (int1 >= 52) {
				this.EndRegen.setValue(byteBuffer.getInt());
			}

			if (int1 >= 90) {
				this.Helicopter.setValue(byteBuffer.getInt());
				this.MetaEvent.setValue(byteBuffer.getInt());
				this.SleepingEvent.setValue(byteBuffer.getInt());
			}

			if (int1 >= 110) {
				this.GeneratorSpawning.setValue(byteBuffer.getInt());
				this.GeneratorFuelConsumption.setValue(byteBuffer.getDouble());
				this.SurvivorHouseChance.setValue(byteBuffer.getInt());
				this.AnnotatedMapChance.setValue(byteBuffer.getInt());
				this.CharacterFreePoints.setValue(byteBuffer.getInt());
			}

			if (int1 < 42) {
				this.DayLength.setValue(this.DayLength.getValue() + 1);
			}
		}
	}

	public int getFirstYear() {
		return 1993;
	}

	private static String[] parseName(String string) {
		String[] stringArray = new String[]{null, string};
		if (string.contains(".")) {
			String[] stringArray2 = string.split("\\.");
			if (stringArray2.length == 2) {
				stringArray[0] = stringArray2[0];
				stringArray[1] = stringArray2[1];
			}
		}

		return stringArray;
	}

	protected SandboxOptions addOption(SandboxOptions.SandboxOption sandboxOption) {
		this.options.add(sandboxOption);
		this.optionByName.put(sandboxOption.asConfigOption().getName(), sandboxOption);
		return this;
	}

	public int getNumOptions() {
		return this.options.size();
	}

	public SandboxOptions.SandboxOption getOptionByIndex(int int1) {
		return (SandboxOptions.SandboxOption)this.options.get(int1);
	}

	public SandboxOptions.SandboxOption getOptionByName(String string) {
		return (SandboxOptions.SandboxOption)this.optionByName.get(string);
	}

	public void set(String string, Object object) {
		if (string != null && object != null) {
			SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.optionByName.get(string);
			if (sandboxOption == null) {
				throw new IllegalArgumentException("unknown SandboxOption \"" + string + "\"");
			} else {
				sandboxOption.asConfigOption().setValueFromObject(object);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void copyValuesFrom(SandboxOptions sandboxOptions) {
		if (sandboxOptions == null) {
			throw new NullPointerException();
		} else {
			for (int int1 = 0; int1 < this.options.size(); ++int1) {
				((SandboxOptions.SandboxOption)this.options.get(int1)).asConfigOption().setValueFromObject(((SandboxOptions.SandboxOption)sandboxOptions.options.get(int1)).asConfigOption().getValueAsObject());
			}
		}
	}

	public void resetToDefault() {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			((SandboxOptions.SandboxOption)this.options.get(int1)).asConfigOption().resetToDefault();
		}
	}

	public void setDefaultsToCurrentValues() {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			((SandboxOptions.SandboxOption)this.options.get(int1)).asConfigOption().setDefaultToCurrentValue();
		}
	}

	public SandboxOptions newCopy() {
		SandboxOptions sandboxOptions = new SandboxOptions();
		sandboxOptions.copyValuesFrom(this);
		return sandboxOptions;
	}

	public static boolean isValidPresetName(String string) {
		if (string != null && !string.isEmpty()) {
			return !string.contains("/") && !string.contains("\\") && !string.contains(":") && !string.contains(";") && !string.contains("\"") && !string.contains(".");
		} else {
			return false;
		}
	}

	private boolean readTextFile(String string, boolean boolean1) {
		ConfigFile configFile = new ConfigFile();
		if (!configFile.read(string)) {
			return false;
		} else {
			int int1 = configFile.getVersion();
			HashSet hashSet = null;
			int int2;
			if (boolean1 && int1 == 1) {
				hashSet = new HashSet();
				for (int2 = 0; int2 < this.options.size(); ++int2) {
					if ("ZombieLore".equals(((SandboxOptions.SandboxOption)this.options.get(int2)).getTableName())) {
						hashSet.add(((SandboxOptions.SandboxOption)this.options.get(int2)).getShortName());
					}
				}
			}

			for (int2 = 0; int2 < configFile.getOptions().size(); ++int2) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int2);
				String string2 = configOption.getName();
				String string3 = configOption.getValueAsString();
				if (hashSet != null && hashSet.contains(string2)) {
					string2 = "ZombieLore." + string2;
				}

				if (boolean1 && int1 == 1) {
					if ("WaterShutModifier".equals(string2)) {
						string2 = "WaterShut";
					} else if ("ElecShutModifier".equals(string2)) {
						string2 = "ElecShut";
					}
				}

				string2 = this.upgradeOptionName(string2, int1);
				string3 = this.upgradeOptionValue(string2, string3, int1);
				SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.optionByName.get(string2);
				if (sandboxOption != null) {
					sandboxOption.asConfigOption().parse(string3);
				}
			}

			return true;
		}
	}

	private boolean writeTextFile(String string, int int1) {
		ConfigFile configFile = new ConfigFile();
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.options.iterator();
		while (iterator.hasNext()) {
			SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)iterator.next();
			arrayList.add(sandboxOption.asConfigOption());
		}

		return configFile.write(string, int1, arrayList);
	}

	public boolean loadServerTextFile(String string) {
		return this.readTextFile(ServerSettingsManager.instance.getNameInSettingsFolder(string + "_sandbox.ini"), false);
	}

	public boolean loadServerLuaFile(String string) {
		boolean boolean1 = this.readLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(string + "_SandboxVars.lua"));
		if (this.Lore.Speed.getValue() == 1) {
			this.Lore.Speed.setValue(2);
		}

		return boolean1;
	}

	public boolean saveServerLuaFile(String string) {
		return this.writeLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(string + "_SandboxVars.lua"), false);
	}

	public boolean loadPresetFile(String string) {
		return this.readTextFile(LuaManager.getSandboxCacheDir() + File.separator + string + ".cfg", true);
	}

	public boolean savePresetFile(String string) {
		return !isValidPresetName(string) ? false : this.writeTextFile(LuaManager.getSandboxCacheDir() + File.separator + string + ".cfg", 4);
	}

	public boolean loadGameFile(String string) {
		File file = new File("media/lua/shared/Sandbox/" + string + ".lua");
		if (!file.exists()) {
			throw new RuntimeException("media/lua/shared/Sandbox/" + string + ".lua not found");
		} else {
			try {
				LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
				Object object = LuaManager.RunLua(file.getAbsolutePath());
				if (!(object instanceof KahluaTable)) {
					throw new RuntimeException(file.getName() + " must return a SandboxVars table");
				} else {
					for (int int1 = 0; int1 < this.options.size(); ++int1) {
						((SandboxOptions.SandboxOption)this.options.get(int1)).fromTable((KahluaTable)object);
					}

					return true;
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return false;
			}
		}
	}

	public boolean saveGameFile(String string) {
		return !Core.bDebug ? false : this.writeLuaFile("media/lua/shared/Sandbox/" + string + ".lua", true);
	}

	private void saveCurrentGameBinFile() {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			Throwable throwable = null;
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				Throwable throwable2 = null;
				try {
					if (SliceY.SliceBuffer == null) {
						SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
					}

					synchronized (SliceY.SliceBuffer) {
						SliceY.SliceBuffer.rewind();
						this.save(SliceY.SliceBuffer);
						bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
					}
				} catch (Throwable throwable3) {
					throwable2 = throwable3;
					throw throwable3;
				} finally {
					if (bufferedOutputStream != null) {
						if (throwable2 != null) {
							try {
								bufferedOutputStream.close();
							} catch (Throwable throwable4) {
								throwable2.addSuppressed(throwable4);
							}
						} else {
							bufferedOutputStream.close();
						}
					}
				}
			} catch (Throwable throwable5) {
				throwable = throwable5;
				throw throwable5;
			} finally {
				if (fileOutputStream != null) {
					if (throwable != null) {
						try {
							fileOutputStream.close();
						} catch (Throwable throwable6) {
							throwable.addSuppressed(throwable6);
						}
					} else {
						fileOutputStream.close();
					}
				}
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void handleOldZombiesFile1() {
		if (!GameServer.bServer) {
			String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "zombies.ini";
			ConfigFile configFile = new ConfigFile();
			if (configFile.read(string)) {
				for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
					ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
					SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.optionByName.get("ZombieConfig." + configOption.getName());
					if (sandboxOption != null) {
						sandboxOption.asConfigOption().parse(configOption.getValueAsString());
					}
				}
			}
		}
	}

	public void handleOldZombiesFile2() {
		if (!GameServer.bServer) {
			String string = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "zombies.ini";
			File file = new File(string);
			if (file.exists()) {
				try {
					DebugLog.log("deleting " + file.getAbsolutePath());
					file.delete();
					this.saveCurrentGameBinFile();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}
	}

	public void handleOldServerZombiesFile() {
		if (GameServer.bServer) {
			if (this.loadServerZombiesFile(GameServer.ServerName)) {
				String string = ServerSettingsManager.instance.getNameInSettingsFolder(GameServer.ServerName + "_zombies.ini");
				try {
					File file = new File(string);
					DebugLog.log("deleting " + file.getAbsolutePath());
					file.delete();
					this.saveServerLuaFile(GameServer.ServerName);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}
	}

	public boolean loadServerZombiesFile(String string) {
		String string2 = ServerSettingsManager.instance.getNameInSettingsFolder(string + "_zombies.ini");
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string2)) {
			for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
				SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.optionByName.get("ZombieConfig." + configOption.getName());
				if (sandboxOption != null) {
					sandboxOption.asConfigOption().parse(configOption.getValueAsString());
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean readLuaFile(String string) {
		File file = new File(string);
		if (!file.exists()) {
			return false;
		} else {
			Object object = LuaManager.env.rawget("SandboxVars");
			KahluaTable kahluaTable = null;
			if (object instanceof KahluaTable) {
				kahluaTable = (KahluaTable)object;
			}

			boolean boolean1;
			try {
				LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
				Object object2 = LuaManager.RunLua(file.getAbsolutePath());
				Object object3 = LuaManager.env.rawget("SandboxVars");
				if (object3 instanceof KahluaTable) {
					KahluaTable kahluaTable2 = (KahluaTable)object3;
					int int1 = 0;
					Object object4 = kahluaTable2.rawget("VERSION");
					if (object4 != null) {
						if (object4 instanceof Double) {
							int1 = ((Double)object4).intValue();
						} else {
							DebugLog.log("ERROR: VERSION=\"" + object4 + "\" in " + string);
						}

						kahluaTable2.rawset("VERSION", (Object)null);
					}

					kahluaTable2 = this.upgradeLuaTable("", kahluaTable2, int1);
					for (int int2 = 0; int2 < this.options.size(); ++int2) {
						((SandboxOptions.SandboxOption)this.options.get(int2)).fromTable(kahluaTable2);
					}
				}

				boolean boolean2 = true;
				return boolean2;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				boolean1 = false;
			} finally {
				if (kahluaTable != null) {
					LuaManager.env.rawset("SandboxVars", kahluaTable);
				}
			}

			return boolean1;
		}
	}

	private boolean writeLuaFile(String string, boolean boolean1) {
		File file = new File(string);
		DebugLog.log("writing " + string);
		try {
			FileWriter fileWriter = new FileWriter(file);
			Throwable throwable = null;
			try {
				HashMap hashMap = new HashMap();
				ArrayList arrayList = new ArrayList();
				hashMap.put("", new ArrayList());
				Iterator iterator = this.options.iterator();
				while (iterator.hasNext()) {
					SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)iterator.next();
					if (sandboxOption.getTableName() == null) {
						((ArrayList)hashMap.get("")).add(sandboxOption);
					} else {
						if (hashMap.get(sandboxOption.getTableName()) == null) {
							hashMap.put(sandboxOption.getTableName(), new ArrayList());
							arrayList.add(sandboxOption.getTableName());
						}

						((ArrayList)hashMap.get(sandboxOption.getTableName())).add(sandboxOption);
					}
				}

				String string2 = System.lineSeparator();
				if (boolean1) {
					fileWriter.write("return {" + string2);
				} else {
					fileWriter.write("SandboxVars = {" + string2);
				}

				fileWriter.write("	VERSION = 4," + string2);
				Iterator iterator2 = ((ArrayList)hashMap.get("")).iterator();
				while (iterator2.hasNext()) {
					SandboxOptions.SandboxOption sandboxOption2 = (SandboxOptions.SandboxOption)iterator2.next();
					fileWriter.write("	" + sandboxOption2.asConfigOption().getName() + " = " + sandboxOption2.asConfigOption().getValueAsString() + "," + string2);
				}

				iterator2 = arrayList.iterator();
				while (iterator2.hasNext()) {
					String string3 = (String)iterator2.next();
					fileWriter.write("	" + string3 + " = {" + string2);
					Iterator iterator3 = ((ArrayList)hashMap.get(string3)).iterator();
					while (iterator3.hasNext()) {
						SandboxOptions.SandboxOption sandboxOption3 = (SandboxOptions.SandboxOption)iterator3.next();
						fileWriter.write("		" + sandboxOption3.getShortName() + " = " + sandboxOption3.asConfigOption().getValueAsString() + "," + string2);
					}

					fileWriter.write("	}," + string2);
				}

				fileWriter.write("}" + System.lineSeparator());
				return true;
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (fileWriter != null) {
					if (throwable != null) {
						try {
							fileWriter.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						fileWriter.close();
					}
				}
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return false;
		}
	}

	public void loadCurrentGameBinFile() {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
		if (file.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				synchronized (SliceY.SliceBuffer) {
					bufferedInputStream.read(SliceY.SliceBuffer.array());
					bufferedInputStream.close();
					SliceY.SliceBuffer.rewind();
					instance.load(SliceY.SliceBuffer);
					instance.toLua();
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	private String upgradeOptionName(String string, int int1) {
		return string;
	}

	private String upgradeOptionValue(String string, String string2, int int1) {
		if (int1 < 3 && "DayLength".equals(string)) {
			this.DayLength.parse(string2);
			if (this.DayLength.getValue() == 8) {
				this.DayLength.setValue(14);
			} else if (this.DayLength.getValue() == 9) {
				this.DayLength.setValue(26);
			}

			string2 = this.DayLength.getValueAsString();
		}

		if (int1 < 4 && "CarSpawnRate".equals(string)) {
			try {
				int int2 = (int)Double.parseDouble(string2);
				if (int2 > 1) {
					string2 = Integer.toString(int2 + 1);
				}
			} catch (NumberFormatException numberFormatException) {
				numberFormatException.printStackTrace();
			}
		}

		return string2;
	}

	private KahluaTable upgradeLuaTable(String string, KahluaTable kahluaTable, int int1) {
		KahluaTable kahluaTable2 = LuaManager.platform.newTable();
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			if (!(kahluaTableIterator.getKey() instanceof String)) {
				throw new IllegalStateException("expected a String key");
			}

			if (kahluaTableIterator.getValue() instanceof KahluaTable) {
				KahluaTable kahluaTable3 = this.upgradeLuaTable(string + kahluaTableIterator.getKey() + ".", (KahluaTable)kahluaTableIterator.getValue(), int1);
				kahluaTable2.rawset(kahluaTableIterator.getKey(), kahluaTable3);
			} else {
				String string2 = this.upgradeOptionName(string + kahluaTableIterator.getKey(), int1);
				String string3 = this.upgradeOptionValue(string2, kahluaTableIterator.getValue().toString(), int1);
				kahluaTable2.rawset(string2.replace(string, ""), string3);
			}
		}

		return kahluaTable2;
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

		public EnumSandboxOption(SandboxOptions sandboxOptions, String string, int int1, int int2) {
			super(string, int1, int2);
			String[] stringArray = SandboxOptions.parseName(string);
			this.tableName = stringArray[0];
			this.shortName = stringArray[1];
			sandboxOptions.addOption(this);
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

		public SandboxOptions.SandboxOption setTranslation(String string) {
			this.translation = string;
			return this;
		}

		public String getTranslatedName() {
			return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
		}

		public String getTooltip() {
			return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
		}

		public void fromTable(KahluaTable kahluaTable) {
			Object object;
			if (this.tableName != null) {
				object = kahluaTable.rawget(this.tableName);
				if (!(object instanceof KahluaTable)) {
					return;
				}

				kahluaTable = (KahluaTable)object;
			}

			object = kahluaTable.rawget(this.getShortName());
			if (object != null) {
				this.setValueFromObject(object);
			}
		}

		public void toTable(KahluaTable kahluaTable) {
			if (this.tableName != null) {
				Object object = kahluaTable.rawget(this.tableName);
				if (object instanceof KahluaTable) {
					kahluaTable = (KahluaTable)object;
				} else {
					KahluaTable kahluaTable2 = LuaManager.platform.newTable();
					kahluaTable.rawset(this.tableName, kahluaTable2);
					kahluaTable = kahluaTable2;
				}
			}

			kahluaTable.rawset(this.getShortName(), this.getValueAsObject());
		}

		public SandboxOptions.EnumSandboxOption setValueTranslation(String string) {
			this.valueTranslation = string;
			return this;
		}

		public String getValueTranslation() {
			return this.valueTranslation != null ? this.valueTranslation : (this.translation == null ? this.getShortName() : this.translation);
		}

		public String getValueTranslationByIndex(int int1) {
			if (int1 >= 1 && int1 <= this.getNumValues()) {
				return Translator.getText("Sandbox_" + this.getValueTranslation() + "_option" + int1);
			} else {
				throw new ArrayIndexOutOfBoundsException();
			}
		}
	}

	public static class IntegerSandboxOption extends IntegerConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;

		public IntegerSandboxOption(SandboxOptions sandboxOptions, String string, int int1, int int2, int int3) {
			super(string, int1, int2, int3);
			String[] stringArray = SandboxOptions.parseName(string);
			this.tableName = stringArray[0];
			this.shortName = stringArray[1];
			sandboxOptions.addOption(this);
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

		public SandboxOptions.SandboxOption setTranslation(String string) {
			this.translation = string;
			return this;
		}

		public String getTranslatedName() {
			return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
		}

		public String getTooltip() {
			if ("ZombieConfig".equals(this.tableName)) {
				String string = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_help");
				String string2 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
				if (string == null) {
					return string2;
				} else {
					return string2 == null ? string : string + "\\n" + string2;
				}
			} else {
				return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
			}
		}

		public void fromTable(KahluaTable kahluaTable) {
			Object object;
			if (this.tableName != null) {
				object = kahluaTable.rawget(this.tableName);
				if (!(object instanceof KahluaTable)) {
					return;
				}

				kahluaTable = (KahluaTable)object;
			}

			object = kahluaTable.rawget(this.getShortName());
			if (object != null) {
				this.setValueFromObject(object);
			}
		}

		public void toTable(KahluaTable kahluaTable) {
			if (this.tableName != null) {
				Object object = kahluaTable.rawget(this.tableName);
				if (object instanceof KahluaTable) {
					kahluaTable = (KahluaTable)object;
				} else {
					KahluaTable kahluaTable2 = LuaManager.platform.newTable();
					kahluaTable.rawset(this.tableName, kahluaTable2);
					kahluaTable = kahluaTable2;
				}
			}

			kahluaTable.rawset(this.getShortName(), this.getValueAsObject());
		}
	}

	public static class DoubleSandboxOption extends DoubleConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;

		public DoubleSandboxOption(SandboxOptions sandboxOptions, String string, double double1, double double2, double double3) {
			super(string, double1, double2, double3);
			String[] stringArray = SandboxOptions.parseName(string);
			this.tableName = stringArray[0];
			this.shortName = stringArray[1];
			sandboxOptions.addOption(this);
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

		public SandboxOptions.SandboxOption setTranslation(String string) {
			this.translation = string;
			return this;
		}

		public String getTranslatedName() {
			return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
		}

		public String getTooltip() {
			String string;
			if ("ZombieConfig".equals(this.tableName)) {
				string = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_help");
			} else {
				string = Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
			}

			String string2 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
			if (string == null) {
				return string2;
			} else {
				return string2 == null ? string : string + "\\n" + string2;
			}
		}

		public void fromTable(KahluaTable kahluaTable) {
			Object object;
			if (this.tableName != null) {
				object = kahluaTable.rawget(this.tableName);
				if (!(object instanceof KahluaTable)) {
					return;
				}

				kahluaTable = (KahluaTable)object;
			}

			object = kahluaTable.rawget(this.getShortName());
			if (object != null) {
				this.setValueFromObject(object);
			}
		}

		public void toTable(KahluaTable kahluaTable) {
			if (this.tableName != null) {
				Object object = kahluaTable.rawget(this.tableName);
				if (object instanceof KahluaTable) {
					kahluaTable = (KahluaTable)object;
				} else {
					KahluaTable kahluaTable2 = LuaManager.platform.newTable();
					kahluaTable.rawset(this.tableName, kahluaTable2);
					kahluaTable = kahluaTable2;
				}
			}

			kahluaTable.rawset(this.getShortName(), this.getValueAsObject());
		}
	}

	public static class BooleanSandboxOption extends BooleanConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;

		public BooleanSandboxOption(SandboxOptions sandboxOptions, String string, boolean boolean1) {
			super(string, boolean1);
			String[] stringArray = SandboxOptions.parseName(string);
			this.tableName = stringArray[0];
			this.shortName = stringArray[1];
			sandboxOptions.addOption(this);
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

		public SandboxOptions.SandboxOption setTranslation(String string) {
			this.translation = string;
			return this;
		}

		public String getTranslatedName() {
			return Translator.getText("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation));
		}

		public String getTooltip() {
			return Translator.getTextOrNull("Sandbox_" + (this.translation == null ? this.getShortName() : this.translation) + "_tooltip");
		}

		public void fromTable(KahluaTable kahluaTable) {
			Object object;
			if (this.tableName != null) {
				object = kahluaTable.rawget(this.tableName);
				if (!(object instanceof KahluaTable)) {
					return;
				}

				kahluaTable = (KahluaTable)object;
			}

			object = kahluaTable.rawget(this.getShortName());
			if (object != null) {
				this.setValueFromObject(object);
			}
		}

		public void toTable(KahluaTable kahluaTable) {
			if (this.tableName != null) {
				Object object = kahluaTable.rawget(this.tableName);
				if (object instanceof KahluaTable) {
					kahluaTable = (KahluaTable)object;
				} else {
					KahluaTable kahluaTable2 = LuaManager.platform.newTable();
					kahluaTable.rawset(this.tableName, kahluaTable2);
					kahluaTable = kahluaTable2;
				}
			}

			kahluaTable.rawset(this.getShortName(), this.getValueAsObject());
		}
	}

	public interface SandboxOption {

		ConfigOption asConfigOption();

		String getShortName();

		String getTableName();

		SandboxOptions.SandboxOption setTranslation(String string);

		String getTranslatedName();

		String getTooltip();

		void fromTable(KahluaTable kahluaTable);

		void toTable(KahluaTable kahluaTable);
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
