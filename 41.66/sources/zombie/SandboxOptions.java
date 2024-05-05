package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import zombie.config.StringConfigOption;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerSettingsManager;
import zombie.sandbox.CustomBooleanSandboxOption;
import zombie.sandbox.CustomDoubleSandboxOption;
import zombie.sandbox.CustomEnumSandboxOption;
import zombie.sandbox.CustomIntegerSandboxOption;
import zombie.sandbox.CustomSandboxOption;
import zombie.sandbox.CustomSandboxOptions;
import zombie.sandbox.CustomStringSandboxOption;
import zombie.util.Type;


public final class SandboxOptions {
	public static final SandboxOptions instance = new SandboxOptions();
	public int Speed = 3;
	public final SandboxOptions.EnumSandboxOption Zombies = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Zombies", 6, 4).setTranslation("ZombieCount");
	public final SandboxOptions.EnumSandboxOption Distribution = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Distribution", 2, 1).setTranslation("ZombieDistribution");
	public final SandboxOptions.EnumSandboxOption DayLength = this.newEnumOption("DayLength", 26, 2);
	public final SandboxOptions.EnumSandboxOption StartYear = this.newEnumOption("StartYear", 100, 1);
	public final SandboxOptions.EnumSandboxOption StartMonth = this.newEnumOption("StartMonth", 12, 7);
	public final SandboxOptions.EnumSandboxOption StartDay = this.newEnumOption("StartDay", 31, 23);
	public final SandboxOptions.EnumSandboxOption StartTime = this.newEnumOption("StartTime", 9, 2);
	public final SandboxOptions.EnumSandboxOption WaterShut = this.newEnumOption("WaterShut", 8, 2).setValueTranslation("Shutoff");
	public final SandboxOptions.EnumSandboxOption ElecShut = this.newEnumOption("ElecShut", 8, 2).setValueTranslation("Shutoff");
	public final SandboxOptions.IntegerSandboxOption WaterShutModifier = (SandboxOptions.IntegerSandboxOption)this.newIntegerOption("WaterShutModifier", -1, Integer.MAX_VALUE, 14).setTranslation("WaterShut");
	public final SandboxOptions.IntegerSandboxOption ElecShutModifier = (SandboxOptions.IntegerSandboxOption)this.newIntegerOption("ElecShutModifier", -1, Integer.MAX_VALUE, 14).setTranslation("ElecShut");
	public final SandboxOptions.EnumSandboxOption FoodLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("FoodLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootFood");
	public final SandboxOptions.EnumSandboxOption LiteratureLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("LiteratureLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootLiterature");
	public final SandboxOptions.EnumSandboxOption MedicalLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("MedicalLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootMedical");
	public final SandboxOptions.EnumSandboxOption SurvivalGearsLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("SurvivalGearsLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootSurvivalGears");
	public final SandboxOptions.EnumSandboxOption CannedFoodLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("CannedFoodLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootCannedFood");
	public final SandboxOptions.EnumSandboxOption WeaponLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("WeaponLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootWeapon");
	public final SandboxOptions.EnumSandboxOption RangedWeaponLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("RangedWeaponLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootRangedWeapon");
	public final SandboxOptions.EnumSandboxOption AmmoLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("AmmoLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootAmmo");
	public final SandboxOptions.EnumSandboxOption MechanicsLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("MechanicsLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootMechanics");
	public final SandboxOptions.EnumSandboxOption OtherLoot = (SandboxOptions.EnumSandboxOption)this.newEnumOption("OtherLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootOther");
	public final SandboxOptions.EnumSandboxOption Temperature = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Temperature", 5, 3).setTranslation("WorldTemperature");
	public final SandboxOptions.EnumSandboxOption Rain = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Rain", 5, 3).setTranslation("RainAmount");
	public final SandboxOptions.EnumSandboxOption ErosionSpeed = this.newEnumOption("ErosionSpeed", 5, 3);
	public final SandboxOptions.IntegerSandboxOption ErosionDays = this.newIntegerOption("ErosionDays", -1, 36500, 0);
	public final SandboxOptions.DoubleSandboxOption XpMultiplier = this.newDoubleOption("XpMultiplier", 0.001, 1000.0, 1.0);
	public final SandboxOptions.EnumSandboxOption Farming = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Farming", 5, 3).setTranslation("FarmingSpeed");
	public final SandboxOptions.EnumSandboxOption CompostTime = this.newEnumOption("CompostTime", 8, 2);
	public final SandboxOptions.EnumSandboxOption StatsDecrease = (SandboxOptions.EnumSandboxOption)this.newEnumOption("StatsDecrease", 5, 3).setTranslation("StatDecrease");
	public final SandboxOptions.EnumSandboxOption NatureAbundance = (SandboxOptions.EnumSandboxOption)this.newEnumOption("NatureAbundance", 5, 3).setTranslation("NatureAmount");
	public final SandboxOptions.EnumSandboxOption Alarm = (SandboxOptions.EnumSandboxOption)this.newEnumOption("Alarm", 6, 4).setTranslation("HouseAlarmFrequency");
	public final SandboxOptions.EnumSandboxOption LockedHouses = (SandboxOptions.EnumSandboxOption)this.newEnumOption("LockedHouses", 6, 4).setTranslation("LockedHouseFrequency");
	public final SandboxOptions.BooleanSandboxOption StarterKit = this.newBooleanOption("StarterKit", false);
	public final SandboxOptions.BooleanSandboxOption Nutrition = this.newBooleanOption("Nutrition", false);
	public final SandboxOptions.EnumSandboxOption FoodRotSpeed = (SandboxOptions.EnumSandboxOption)this.newEnumOption("FoodRotSpeed", 5, 3).setTranslation("FoodSpoil");
	public final SandboxOptions.EnumSandboxOption FridgeFactor = (SandboxOptions.EnumSandboxOption)this.newEnumOption("FridgeFactor", 5, 3).setTranslation("FridgeEffect");
	public final SandboxOptions.EnumSandboxOption LootRespawn = this.newEnumOption("LootRespawn", 5, 1).setValueTranslation("Respawn");
	public final SandboxOptions.IntegerSandboxOption SeenHoursPreventLootRespawn = this.newIntegerOption("SeenHoursPreventLootRespawn", 0, Integer.MAX_VALUE, 0);
	public final SandboxOptions.StringSandboxOption WorldItemRemovalList = this.newStringOption("WorldItemRemovalList", "Base.Vest,Base.Shirt,Base.Blouse,Base.Skirt,Base.Shoes,Base.Hat,Base.Glasses", -1);
	public final SandboxOptions.DoubleSandboxOption HoursForWorldItemRemoval = this.newDoubleOption("HoursForWorldItemRemoval", 0.0, 2.147483647E9, 24.0);
	public final SandboxOptions.BooleanSandboxOption ItemRemovalListBlacklistToggle = this.newBooleanOption("ItemRemovalListBlacklistToggle", false);
	public final SandboxOptions.EnumSandboxOption TimeSinceApo = this.newEnumOption("TimeSinceApo", 13, 1);
	public final SandboxOptions.EnumSandboxOption PlantResilience = this.newEnumOption("PlantResilience", 5, 3);
	public final SandboxOptions.EnumSandboxOption PlantAbundance = this.newEnumOption("PlantAbundance", 5, 3).setValueTranslation("NatureAmount");
	public final SandboxOptions.EnumSandboxOption EndRegen = (SandboxOptions.EnumSandboxOption)this.newEnumOption("EndRegen", 5, 3).setTranslation("EnduranceRegen");
	public final SandboxOptions.EnumSandboxOption Helicopter = this.newEnumOption("Helicopter", 4, 2).setValueTranslation("HelicopterFreq");
	public final SandboxOptions.EnumSandboxOption MetaEvent = this.newEnumOption("MetaEvent", 3, 2).setValueTranslation("MetaEventFreq");
	public final SandboxOptions.EnumSandboxOption SleepingEvent = this.newEnumOption("SleepingEvent", 3, 1).setValueTranslation("MetaEventFreq");
	public final SandboxOptions.DoubleSandboxOption GeneratorFuelConsumption = this.newDoubleOption("GeneratorFuelConsumption", 0.0, 100.0, 1.0);
	public final SandboxOptions.EnumSandboxOption GeneratorSpawning = this.newEnumOption("GeneratorSpawning", 5, 3);
	public final SandboxOptions.EnumSandboxOption SurvivorHouseChance = this.newEnumOption("SurvivorHouseChance", 6, 3);
	public final SandboxOptions.EnumSandboxOption AnnotatedMapChance = this.newEnumOption("AnnotatedMapChance", 6, 4);
	public final SandboxOptions.IntegerSandboxOption CharacterFreePoints = this.newIntegerOption("CharacterFreePoints", -100, 100, 0);
	public final SandboxOptions.EnumSandboxOption ConstructionBonusPoints = this.newEnumOption("ConstructionBonusPoints", 5, 3);
	public final SandboxOptions.EnumSandboxOption NightDarkness = this.newEnumOption("NightDarkness", 4, 3);
	public final SandboxOptions.EnumSandboxOption NightLength = this.newEnumOption("NightLength", 5, 3);
	public final SandboxOptions.BooleanSandboxOption BoneFracture = this.newBooleanOption("BoneFracture", true);
	public final SandboxOptions.EnumSandboxOption InjurySeverity = this.newEnumOption("InjurySeverity", 3, 2);
	public final SandboxOptions.DoubleSandboxOption HoursForCorpseRemoval = this.newDoubleOption("HoursForCorpseRemoval", -1.0, 2.147483647E9, -1.0);
	public final SandboxOptions.EnumSandboxOption DecayingCorpseHealthImpact = this.newEnumOption("DecayingCorpseHealthImpact", 4, 3);
	public final SandboxOptions.EnumSandboxOption BloodLevel = this.newEnumOption("BloodLevel", 5, 3);
	public final SandboxOptions.EnumSandboxOption ClothingDegradation = this.newEnumOption("ClothingDegradation", 4, 3);
	public final SandboxOptions.BooleanSandboxOption FireSpread = this.newBooleanOption("FireSpread", true);
	public final SandboxOptions.IntegerSandboxOption DaysForRottenFoodRemoval = this.newIntegerOption("DaysForRottenFoodRemoval", -1, Integer.MAX_VALUE, -1);
	public final SandboxOptions.BooleanSandboxOption AllowExteriorGenerator = this.newBooleanOption("AllowExteriorGenerator", true);
	public final SandboxOptions.EnumSandboxOption MaxFogIntensity = this.newEnumOption("MaxFogIntensity", 3, 1);
	public final SandboxOptions.EnumSandboxOption MaxRainFxIntensity = this.newEnumOption("MaxRainFxIntensity", 3, 1);
	public final SandboxOptions.BooleanSandboxOption EnableSnowOnGround = this.newBooleanOption("EnableSnowOnGround", true);
	public final SandboxOptions.BooleanSandboxOption AttackBlockMovements = this.newBooleanOption("AttackBlockMovements", true);
	public final SandboxOptions.EnumSandboxOption VehicleStoryChance = this.newEnumOption("VehicleStoryChance", 6, 3).setValueTranslation("SurvivorHouseChance");
	public final SandboxOptions.EnumSandboxOption ZoneStoryChance = this.newEnumOption("ZoneStoryChance", 6, 3).setValueTranslation("SurvivorHouseChance");
	public final SandboxOptions.BooleanSandboxOption AllClothesUnlocked = this.newBooleanOption("AllClothesUnlocked", false);
	public final SandboxOptions.BooleanSandboxOption EnableVehicles = this.newBooleanOption("EnableVehicles", true);
	public final SandboxOptions.EnumSandboxOption CarSpawnRate = this.newEnumOption("CarSpawnRate", 5, 4);
	public final SandboxOptions.DoubleSandboxOption ZombieAttractionMultiplier = this.newDoubleOption("ZombieAttractionMultiplier", 0.0, 100.0, 1.0);
	public final SandboxOptions.BooleanSandboxOption VehicleEasyUse = this.newBooleanOption("VehicleEasyUse", false);
	public final SandboxOptions.EnumSandboxOption InitialGas = this.newEnumOption("InitialGas", 6, 3);
	public final SandboxOptions.EnumSandboxOption FuelStationGas = this.newEnumOption("FuelStationGas", 8, 4);
	public final SandboxOptions.EnumSandboxOption LockedCar = this.newEnumOption("LockedCar", 6, 4);
	public final SandboxOptions.DoubleSandboxOption CarGasConsumption = this.newDoubleOption("CarGasConsumption", 0.0, 100.0, 1.0);
	public final SandboxOptions.EnumSandboxOption CarGeneralCondition = this.newEnumOption("CarGeneralCondition", 5, 3);
	public final SandboxOptions.EnumSandboxOption CarDamageOnImpact = this.newEnumOption("CarDamageOnImpact", 5, 3);
	public final SandboxOptions.EnumSandboxOption DamageToPlayerFromHitByACar = this.newEnumOption("DamageToPlayerFromHitByACar", 5, 1);
	public final SandboxOptions.BooleanSandboxOption TrafficJam = this.newBooleanOption("TrafficJam", true);
	public final SandboxOptions.EnumSandboxOption CarAlarm = (SandboxOptions.EnumSandboxOption)this.newEnumOption("CarAlarm", 6, 4).setTranslation("CarAlarmFrequency");
	public final SandboxOptions.BooleanSandboxOption PlayerDamageFromCrash = this.newBooleanOption("PlayerDamageFromCrash", true);
	public final SandboxOptions.DoubleSandboxOption SirenShutoffHours = this.newDoubleOption("SirenShutoffHours", 0.0, 168.0, 0.0);
	public final SandboxOptions.EnumSandboxOption ChanceHasGas = this.newEnumOption("ChanceHasGas", 3, 2);
	public final SandboxOptions.EnumSandboxOption RecentlySurvivorVehicles = this.newEnumOption("RecentlySurvivorVehicles", 3, 2);
	public final SandboxOptions.BooleanSandboxOption MultiHitZombies = this.newBooleanOption("MultiHitZombies", false);
	public final SandboxOptions.EnumSandboxOption RearVulnerability = this.newEnumOption("RearVulnerability", 3, 3);
	protected final ArrayList options = new ArrayList();
	protected final HashMap optionByName = new HashMap();
	public final SandboxOptions.Map Map = new SandboxOptions.Map();
	public final SandboxOptions.ZombieLore Lore = new SandboxOptions.ZombieLore();
	public final SandboxOptions.ZombieConfig zombieConfig = new SandboxOptions.ZombieConfig();
	public final int FIRST_YEAR = 1993;
	private final int SANDBOX_VERSION = 4;
	private final ArrayList m_customOptions = new ArrayList();

	public SandboxOptions() {
		CustomSandboxOptions.instance.initInstance(this);
		this.loadGameFile("Apocalypse");
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
			VirtualZombieManager.instance.MaxRealZombies = 350;
		}

		if (this.Zombies.getValue() == 3) {
			VirtualZombieManager.instance.MaxRealZombies = 300;
		}

		if (this.Zombies.getValue() == 4) {
			VirtualZombieManager.instance.MaxRealZombies = 200;
		}

		if (this.Zombies.getValue() == 5) {
			VirtualZombieManager.instance.MaxRealZombies = 100;
		}

		if (this.Zombies.getValue() == 6) {
			VirtualZombieManager.instance.MaxRealZombies = 0;
		}

		VirtualZombieManager.instance.MaxRealZombies = 1;
		this.applySettings();
	}

	public void initSandboxVars() {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("SandboxVars");
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)this.options.get(int1);
			sandboxOption.fromTable(kahluaTable);
			sandboxOption.toTable(kahluaTable);
		}
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
			GameTime.instance.setStartTimeOfDay(5.0F);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)83);
		byteBuffer.put((byte)65);
		byteBuffer.put((byte)78);
		byteBuffer.put((byte)68);
		byteBuffer.putInt(186);
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

		if (int1 >= 88) {
			int int2 = 2;
			if (int1 >= 131) {
				int2 = byteBuffer.getInt();
			}

			int int3 = byteBuffer.getInt();
			for (int int4 = 0; int4 < int3; ++int4) {
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

			if (int1 < 157) {
				instance.CannedFoodLoot.setValue(instance.FoodLoot.getValue());
				instance.AmmoLoot.setValue(instance.WeaponLoot.getValue());
				instance.RangedWeaponLoot.setValue(instance.WeaponLoot.getValue());
				instance.MedicalLoot.setValue(instance.OtherLoot.getValue());
				instance.LiteratureLoot.setValue(instance.OtherLoot.getValue());
				instance.SurvivalGearsLoot.setValue(instance.OtherLoot.getValue());
				instance.MechanicsLoot.setValue(instance.OtherLoot.getValue());
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

	private SandboxOptions.BooleanSandboxOption newBooleanOption(String string, boolean boolean1) {
		return new SandboxOptions.BooleanSandboxOption(this, string, boolean1);
	}

	private SandboxOptions.DoubleSandboxOption newDoubleOption(String string, double double1, double double2, double double3) {
		return new SandboxOptions.DoubleSandboxOption(this, string, double1, double2, double3);
	}

	private SandboxOptions.EnumSandboxOption newEnumOption(String string, int int1, int int2) {
		return new SandboxOptions.EnumSandboxOption(this, string, int1, int2);
	}

	private SandboxOptions.IntegerSandboxOption newIntegerOption(String string, int int1, int int2, int int3) {
		return new SandboxOptions.IntegerSandboxOption(this, string, int1, int2, int3);
	}

	private SandboxOptions.StringSandboxOption newStringOption(String string, String string2, int int1) {
		return new SandboxOptions.StringSandboxOption(this, string, string2, int1);
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
		File file = ZomboidFileSystem.instance.getMediaFile("lua/shared/Sandbox/" + string + ".lua");
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
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						this.save(SliceY.SliceBuffer);
						bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
					}
				} catch (Throwable throwable) {
					try {
						bufferedOutputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedOutputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileOutputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileOutputStream.close();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void handleOldZombiesFile1() {
		if (!GameServer.bServer) {
			String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("zombies.ini");
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
			String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("zombies.ini");
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
		File file = (new File(string)).getAbsoluteFile();
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
		File file = (new File(string)).getAbsoluteFile();
		DebugLog.log("writing " + string);
		try {
			FileWriter fileWriter = new FileWriter(file);
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
				String string3;
				String string4;
				while (iterator2.hasNext()) {
					SandboxOptions.SandboxOption sandboxOption2 = (SandboxOptions.SandboxOption)iterator2.next();
					if (!boolean1) {
						String string5 = sandboxOption2.asConfigOption().getTooltip();
						if (string5 != null) {
							string5 = string5.replace("\\n", " ");
							string5 = string5.replaceAll("\n", System.lineSeparator() + "	-- ");
							fileWriter.write("	-- " + string5 + string2);
						}

						if (sandboxOption2 instanceof SandboxOptions.EnumSandboxOption) {
							for (int int1 = 1; int1 < ((SandboxOptions.EnumSandboxOption)sandboxOption2).getNumValues(); ++int1) {
								try {
									string3 = ((SandboxOptions.EnumSandboxOption)sandboxOption2).getValueTranslationByIndexOrNull(int1);
									if (string3 != null) {
										fileWriter.write("	-- " + int1 + " = " + string3 + System.lineSeparator());
									}
								} catch (Exception exception) {
									ExceptionLogger.logException(exception);
								}
							}
						}
					}

					string4 = sandboxOption2.asConfigOption().getName();
					fileWriter.write("	" + string4 + " = " + sandboxOption2.asConfigOption().getValueAsLuaString() + "," + string2);
				}

				iterator2 = arrayList.iterator();
				while (iterator2.hasNext()) {
					String string6 = (String)iterator2.next();
					fileWriter.write("	" + string6 + " = {" + string2);
					Iterator iterator3 = ((ArrayList)hashMap.get(string6)).iterator();
					while (iterator3.hasNext()) {
						SandboxOptions.SandboxOption sandboxOption3 = (SandboxOptions.SandboxOption)iterator3.next();
						if (!boolean1) {
							string3 = sandboxOption3.asConfigOption().getTooltip();
							if (string3 != null) {
								string3 = string3.replace("\\n", " ");
								string3 = string3.replaceAll("\n", System.lineSeparator() + "		-- ");
								fileWriter.write("		-- " + string3 + string2);
							}

							if (sandboxOption3 instanceof SandboxOptions.EnumSandboxOption) {
								for (int int2 = 1; int2 < ((SandboxOptions.EnumSandboxOption)sandboxOption3).getNumValues(); ++int2) {
									try {
										String string7 = ((SandboxOptions.EnumSandboxOption)sandboxOption3).getValueTranslationByIndexOrNull(int2);
										if (string7 != null) {
											fileWriter.write("		-- " + int2 + " = " + string7 + System.lineSeparator());
										}
									} catch (Exception exception2) {
										ExceptionLogger.logException(exception2);
									}
								}
							}
						}

						string4 = sandboxOption3.getShortName();
						fileWriter.write("		" + string4 + " = " + sandboxOption3.asConfigOption().getValueAsLuaString() + "," + string2);
					}

					fileWriter.write("	}," + string2);
				}

				fileWriter.write("}" + System.lineSeparator());
			} catch (Throwable throwable) {
				try {
					fileWriter.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			fileWriter.close();
			return true;
		} catch (Exception exception3) {
			ExceptionLogger.logException(exception3);
			return false;
		}
	}

	public void load() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
						SliceY.SliceBuffer.limit(int1);
						this.load(SliceY.SliceBuffer);
						this.handleOldZombiesFile1();
						this.applySettings();
						this.toLua();
					}
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
			return;
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		this.resetToDefault();
		this.updateFromLua();
	}

	public void loadCurrentGameBinFile() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						int int1 = bufferedInputStream.read(SliceY.SliceBuffer.array());
						SliceY.SliceBuffer.limit(int1);
						this.load(SliceY.SliceBuffer);
					}

					this.toLua();
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
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

	public void newCustomOption(CustomSandboxOption customSandboxOption) {
		CustomBooleanSandboxOption customBooleanSandboxOption = (CustomBooleanSandboxOption)Type.tryCastTo(customSandboxOption, CustomBooleanSandboxOption.class);
		if (customBooleanSandboxOption != null) {
			this.addCustomOption(new SandboxOptions.BooleanSandboxOption(this, customBooleanSandboxOption.m_id, customBooleanSandboxOption.defaultValue), customSandboxOption);
		} else {
			CustomDoubleSandboxOption customDoubleSandboxOption = (CustomDoubleSandboxOption)Type.tryCastTo(customSandboxOption, CustomDoubleSandboxOption.class);
			if (customDoubleSandboxOption != null) {
				this.addCustomOption(new SandboxOptions.DoubleSandboxOption(this, customDoubleSandboxOption.m_id, customDoubleSandboxOption.min, customDoubleSandboxOption.max, customDoubleSandboxOption.defaultValue), customSandboxOption);
			} else {
				CustomEnumSandboxOption customEnumSandboxOption = (CustomEnumSandboxOption)Type.tryCastTo(customSandboxOption, CustomEnumSandboxOption.class);
				if (customEnumSandboxOption != null) {
					SandboxOptions.EnumSandboxOption enumSandboxOption = new SandboxOptions.EnumSandboxOption(this, customEnumSandboxOption.m_id, customEnumSandboxOption.numValues, customEnumSandboxOption.defaultValue);
					if (customEnumSandboxOption.m_valueTranslation != null) {
						enumSandboxOption.setValueTranslation(customEnumSandboxOption.m_valueTranslation);
					}

					this.addCustomOption(enumSandboxOption, customSandboxOption);
				} else {
					CustomIntegerSandboxOption customIntegerSandboxOption = (CustomIntegerSandboxOption)Type.tryCastTo(customSandboxOption, CustomIntegerSandboxOption.class);
					if (customIntegerSandboxOption != null) {
						this.addCustomOption(new SandboxOptions.IntegerSandboxOption(this, customIntegerSandboxOption.m_id, customIntegerSandboxOption.min, customIntegerSandboxOption.max, customIntegerSandboxOption.defaultValue), customSandboxOption);
					} else {
						CustomStringSandboxOption customStringSandboxOption = (CustomStringSandboxOption)Type.tryCastTo(customSandboxOption, CustomStringSandboxOption.class);
						if (customStringSandboxOption != null) {
							this.addCustomOption(new SandboxOptions.StringSandboxOption(this, customStringSandboxOption.m_id, customStringSandboxOption.defaultValue, -1), customSandboxOption);
						} else {
							throw new IllegalArgumentException("unhandled CustomSandboxOption " + customSandboxOption);
						}
					}
				}
			}
		}
	}

	private void addCustomOption(SandboxOptions.SandboxOption sandboxOption, CustomSandboxOption customSandboxOption) {
		sandboxOption.setCustom();
		if (customSandboxOption.m_page != null) {
			sandboxOption.setPageName(customSandboxOption.m_page);
		}

		if (customSandboxOption.m_translation != null) {
			sandboxOption.setTranslation(customSandboxOption.m_translation);
		}

		this.m_customOptions.add(sandboxOption);
	}

	private void removeCustomOptions() {
		this.options.removeAll(this.m_customOptions);
		Iterator iterator = this.m_customOptions.iterator();
		while (iterator.hasNext()) {
			SandboxOptions.SandboxOption sandboxOption = (SandboxOptions.SandboxOption)iterator.next();
			this.optionByName.remove(sandboxOption.asConfigOption().getName());
		}

		this.m_customOptions.clear();
	}

	public static void Reset() {
		instance.removeCustomOptions();
	}

	public boolean getAllClothesUnlocked() {
		return this.AllClothesUnlocked.getValue();
	}

	public final class Map {
		public final SandboxOptions.BooleanSandboxOption AllowMiniMap = SandboxOptions.this.newBooleanOption("Map.AllowMiniMap", false);
		public final SandboxOptions.BooleanSandboxOption AllowWorldMap = SandboxOptions.this.newBooleanOption("Map.AllowWorldMap", true);
		public final SandboxOptions.BooleanSandboxOption MapAllKnown = SandboxOptions.this.newBooleanOption("Map.MapAllKnown", false);

		Map() {
		}
	}

	public final class ZombieLore {
		public final SandboxOptions.EnumSandboxOption Speed = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Speed", 4, 2).setTranslation("ZSpeed");
		public final SandboxOptions.EnumSandboxOption Strength = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Strength", 4, 2).setTranslation("ZStrength");
		public final SandboxOptions.EnumSandboxOption Toughness = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Toughness", 4, 2).setTranslation("ZToughness");
		public final SandboxOptions.EnumSandboxOption Transmission = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Transmission", 4, 1).setTranslation("ZTransmission");
		public final SandboxOptions.EnumSandboxOption Mortality = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Mortality", 7, 5).setTranslation("ZInfectionMortality");
		public final SandboxOptions.EnumSandboxOption Reanimate = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Reanimate", 6, 3).setTranslation("ZReanimateTime");
		public final SandboxOptions.EnumSandboxOption Cognition = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Cognition", 4, 3).setTranslation("ZCognition");
		public final SandboxOptions.EnumSandboxOption CrawlUnderVehicle = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.CrawlUnderVehicle", 7, 5).setTranslation("ZCrawlUnderVehicle");
		public final SandboxOptions.EnumSandboxOption Memory = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Memory", 4, 2).setTranslation("ZMemory");
		public final SandboxOptions.EnumSandboxOption Decomp = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Decomp", 4, 1).setTranslation("ZDecomposition");
		public final SandboxOptions.EnumSandboxOption Sight = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Sight", 3, 2).setTranslation("ZSight");
		public final SandboxOptions.EnumSandboxOption Hearing = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Hearing", 3, 2).setTranslation("ZHearing");
		public final SandboxOptions.BooleanSandboxOption ThumpNoChasing = SandboxOptions.this.newBooleanOption("ZombieLore.ThumpNoChasing", false);
		public final SandboxOptions.BooleanSandboxOption ThumpOnConstruction = SandboxOptions.this.newBooleanOption("ZombieLore.ThumpOnConstruction", true);
		public final SandboxOptions.EnumSandboxOption ActiveOnly = (SandboxOptions.EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.ActiveOnly", 3, 1).setTranslation("ActiveOnly");
		public final SandboxOptions.BooleanSandboxOption TriggerHouseAlarm = SandboxOptions.this.newBooleanOption("ZombieLore.TriggerHouseAlarm", false);
		public final SandboxOptions.BooleanSandboxOption ZombiesDragDown = SandboxOptions.this.newBooleanOption("ZombieLore.ZombiesDragDown", true);
		public final SandboxOptions.BooleanSandboxOption ZombiesFenceLunge = SandboxOptions.this.newBooleanOption("ZombieLore.ZombiesFenceLunge", true);

		private ZombieLore() {
		}
	}

	public final class ZombieConfig {
		public final SandboxOptions.DoubleSandboxOption PopulationMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.PopulationMultiplier", 0.0, 4.0, 1.0);
		public final SandboxOptions.DoubleSandboxOption PopulationStartMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.PopulationStartMultiplier", 0.0, 4.0, 1.0);
		public final SandboxOptions.DoubleSandboxOption PopulationPeakMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.PopulationPeakMultiplier", 0.0, 4.0, 1.5);
		public final SandboxOptions.IntegerSandboxOption PopulationPeakDay = SandboxOptions.this.newIntegerOption("ZombieConfig.PopulationPeakDay", 1, 365, 28);
		public final SandboxOptions.DoubleSandboxOption RespawnHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnHours", 0.0, 8760.0, 72.0);
		public final SandboxOptions.DoubleSandboxOption RespawnUnseenHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnUnseenHours", 0.0, 8760.0, 16.0);
		public final SandboxOptions.DoubleSandboxOption RespawnMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnMultiplier", 0.0, 1.0, 0.1);
		public final SandboxOptions.DoubleSandboxOption RedistributeHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RedistributeHours", 0.0, 8760.0, 12.0);
		public final SandboxOptions.IntegerSandboxOption FollowSoundDistance = SandboxOptions.this.newIntegerOption("ZombieConfig.FollowSoundDistance", 10, 1000, 100);
		public final SandboxOptions.IntegerSandboxOption RallyGroupSize = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupSize", 0, 1000, 20);
		public final SandboxOptions.IntegerSandboxOption RallyTravelDistance = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyTravelDistance", 5, 50, 20);
		public final SandboxOptions.IntegerSandboxOption RallyGroupSeparation = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupSeparation", 5, 25, 15);
		public final SandboxOptions.IntegerSandboxOption RallyGroupRadius = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupRadius", 1, 10, 3);

		private ZombieConfig() {
		}
	}

	public static class EnumSandboxOption extends EnumConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;
		protected boolean bCustom;
		protected String pageName;
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
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getText("Sandbox_" + string);
		}

		public String getTooltip() {
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getTextOrNull("Sandbox_" + string + "_tooltip");
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

		public void setCustom() {
			this.bCustom = true;
		}

		public boolean isCustom() {
			return this.bCustom;
		}

		public SandboxOptions.SandboxOption setPageName(String string) {
			this.pageName = string;
			return this;
		}

		public String getPageName() {
			return this.pageName;
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
				String string = this.getValueTranslation();
				return Translator.getText("Sandbox_" + string + "_option" + int1);
			} else {
				throw new ArrayIndexOutOfBoundsException();
			}
		}

		public String getValueTranslationByIndexOrNull(int int1) {
			if (int1 >= 1 && int1 <= this.getNumValues()) {
				String string = this.getValueTranslation();
				return Translator.getTextOrNull("Sandbox_" + string + "_option" + int1);
			} else {
				throw new ArrayIndexOutOfBoundsException();
			}
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

		void setCustom();

		boolean isCustom();

		SandboxOptions.SandboxOption setPageName(String string);

		String getPageName();
	}

	public static class IntegerSandboxOption extends IntegerConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;
		protected boolean bCustom;
		protected String pageName;

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
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getText("Sandbox_" + string);
		}

		public String getTooltip() {
			String string;
			String string2;
			if ("ZombieConfig".equals(this.tableName)) {
				string = this.translation == null ? this.getShortName() : this.translation;
				string2 = Translator.getTextOrNull("Sandbox_" + string + "_help");
			} else {
				string = this.translation == null ? this.getShortName() : this.translation;
				string2 = Translator.getTextOrNull("Sandbox_" + string + "_tooltip");
			}

			String string3 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
			if (string2 == null) {
				return string3;
			} else {
				return string3 == null ? string2 : string2 + "\\n" + string3;
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

		public void setCustom() {
			this.bCustom = true;
		}

		public boolean isCustom() {
			return this.bCustom;
		}

		public SandboxOptions.SandboxOption setPageName(String string) {
			this.pageName = string;
			return this;
		}

		public String getPageName() {
			return this.pageName;
		}
	}

	public static class DoubleSandboxOption extends DoubleConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;
		protected boolean bCustom;
		protected String pageName;

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
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getText("Sandbox_" + string);
		}

		public String getTooltip() {
			String string;
			String string2;
			if ("ZombieConfig".equals(this.tableName)) {
				string = this.translation == null ? this.getShortName() : this.translation;
				string2 = Translator.getTextOrNull("Sandbox_" + string + "_help");
			} else {
				string = this.translation == null ? this.getShortName() : this.translation;
				string2 = Translator.getTextOrNull("Sandbox_" + string + "_tooltip");
			}

			String string3 = Translator.getText("Sandbox_MinMaxDefault", String.format("%.02f", this.min), String.format("%.02f", this.max), String.format("%.02f", this.defaultValue));
			if (string2 == null) {
				return string3;
			} else {
				return string3 == null ? string2 : string2 + "\\n" + string3;
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

		public void setCustom() {
			this.bCustom = true;
		}

		public boolean isCustom() {
			return this.bCustom;
		}

		public SandboxOptions.SandboxOption setPageName(String string) {
			this.pageName = string;
			return this;
		}

		public String getPageName() {
			return this.pageName;
		}
	}

	public static class BooleanSandboxOption extends BooleanConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;
		protected boolean bCustom;
		protected String pageName;

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
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getText("Sandbox_" + string);
		}

		public String getTooltip() {
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getTextOrNull("Sandbox_" + string + "_tooltip");
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

		public void setCustom() {
			this.bCustom = true;
		}

		public boolean isCustom() {
			return this.bCustom;
		}

		public SandboxOptions.SandboxOption setPageName(String string) {
			this.pageName = string;
			return this;
		}

		public String getPageName() {
			return this.pageName;
		}
	}

	public static class StringSandboxOption extends StringConfigOption implements SandboxOptions.SandboxOption {
		protected String translation;
		protected String tableName;
		protected String shortName;
		protected boolean bCustom;
		protected String pageName;

		public StringSandboxOption(SandboxOptions sandboxOptions, String string, String string2, int int1) {
			super(string, string2, int1);
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
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getText("Sandbox_" + string);
		}

		public String getTooltip() {
			String string = this.translation == null ? this.getShortName() : this.translation;
			return Translator.getTextOrNull("Sandbox_" + string + "_tooltip");
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

		public void setCustom() {
			this.bCustom = true;
		}

		public boolean isCustom() {
			return this.bCustom;
		}

		public SandboxOptions.SandboxOption setPageName(String string) {
			this.pageName = string;
			return this;
		}

		public String getPageName() {
			return this.pageName;
		}
	}
}
