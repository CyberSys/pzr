package zombie;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;


public class ZomboidGlobals {
	public static double RunningEnduranceReduce = 0.0;
	public static double ImobileEnduranceReduce = 0.0;
	public static double ThirstIncrease = 0.0;
	public static double ThirstSleepingIncrease = 0.0;
	public static double ThirstLevelToAutoDrink = 0.0;
	public static double ThirstLevelReductionOnAutoDrink = 0.0;
	public static double HungerIncrease = 0.0;
	public static double HungerIncreaseWhenWellFed = 0.0;
	public static double HungerIncreaseWhileAsleep = 0.0;
	public static double HungerIncreaseWhenExercise = 0.0;
	public static double FatigueIncrease = 0.0;
	public static double StressReduction = 0.0;
	public static double BoredomIncreaseRate = 0.0;
	public static double BoredomDecreaseRate = 0.0;
	public static double UnhappinessIncrease = 0.0;
	public static double StressFromSoundsMultiplier = 0.0;
	public static double StressFromBiteOrScratch = 0.0;
	public static double AngerDecrease = 0.0;
	public static double BroodingAngerDecreaseMultiplier = 0.0;
	public static double SleepFatigueReduction = 0.0;
	public static double WetnessIncrease = 0.0;
	public static double WetnessDecrease = 0.0;
	public static double CatchAColdIncreaseRate = 0.0;
	public static double CatchAColdDecreaseRate = 0.0;
	public static double PoisonLevelDecrease = 0.0;
	public static double PoisonHealthReduction = 0.0;
	public static double FoodSicknessDecrease = 0.0;

	public static void Load() {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ZomboidGlobals");
		RunningEnduranceReduce = (Double)kahluaTable.rawget("RunningEnduranceReduce");
		ImobileEnduranceReduce = (Double)kahluaTable.rawget("ImobileEnduranceIncrease");
		ThirstIncrease = (Double)kahluaTable.rawget("ThirstIncrease");
		ThirstSleepingIncrease = (Double)kahluaTable.rawget("ThirstSleepingIncrease");
		ThirstLevelToAutoDrink = (Double)kahluaTable.rawget("ThirstLevelToAutoDrink");
		ThirstLevelReductionOnAutoDrink = (Double)kahluaTable.rawget("ThirstLevelReductionOnAutoDrink");
		HungerIncrease = (Double)kahluaTable.rawget("HungerIncrease");
		HungerIncreaseWhenWellFed = (Double)kahluaTable.rawget("HungerIncreaseWhenWellFed");
		HungerIncreaseWhileAsleep = (Double)kahluaTable.rawget("HungerIncreaseWhileAsleep");
		HungerIncreaseWhenExercise = (Double)kahluaTable.rawget("HungerIncreaseWhenExercise");
		FatigueIncrease = (Double)kahluaTable.rawget("FatigueIncrease");
		StressReduction = (Double)kahluaTable.rawget("StressDecrease");
		BoredomIncreaseRate = (Double)kahluaTable.rawget("BoredomIncrease");
		BoredomDecreaseRate = (Double)kahluaTable.rawget("BoredomDecrease");
		UnhappinessIncrease = (Double)kahluaTable.rawget("UnhappinessIncrease");
		StressFromSoundsMultiplier = (Double)kahluaTable.rawget("StressFromSoundsMultiplier");
		StressFromBiteOrScratch = (Double)kahluaTable.rawget("StressFromBiteOrScratch");
		AngerDecrease = (Double)kahluaTable.rawget("AngerDecrease");
		BroodingAngerDecreaseMultiplier = (Double)kahluaTable.rawget("BroodingAngerDecreaseMultiplier");
		SleepFatigueReduction = (Double)kahluaTable.rawget("SleepFatigueReduction");
		WetnessIncrease = (Double)kahluaTable.rawget("WetnessIncrease");
		WetnessDecrease = (Double)kahluaTable.rawget("WetnessDecrease");
		CatchAColdIncreaseRate = (Double)kahluaTable.rawget("CatchAColdIncreaseRate");
		CatchAColdDecreaseRate = (Double)kahluaTable.rawget("CatchAColdDecreaseRate");
		PoisonLevelDecrease = (Double)kahluaTable.rawget("PoisonLevelDecrease");
		PoisonHealthReduction = (Double)kahluaTable.rawget("PoisonHealthReduction");
		FoodSicknessDecrease = (Double)kahluaTable.rawget("FoodSicknessDecrease");
	}

	public static void toLua() {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("ZomboidGlobals");
		if (kahluaTable == null) {
			DebugLog.log("ERROR: ZomboidGlobals table undefined in Lua");
		} else {
			double double1 = 1.0;
			if (SandboxOptions.instance.getFoodLootModifier() == 1) {
				double1 = 0.2;
			} else if (SandboxOptions.instance.getFoodLootModifier() == 2) {
				double1 = 0.6;
			} else if (SandboxOptions.instance.getFoodLootModifier() == 3) {
				double1 = 1.0;
			} else if (SandboxOptions.instance.getFoodLootModifier() == 4) {
				double1 = 2.0;
			} else if (SandboxOptions.instance.getFoodLootModifier() == 5) {
				double1 = 4.0;
			}

			kahluaTable.rawset("FoodLootModifier", double1);
			double double2 = 1.0;
			if (SandboxOptions.instance.getWeaponLootModifier() == 1) {
				double2 = 0.2;
			} else if (SandboxOptions.instance.getWeaponLootModifier() == 2) {
				double2 = 0.6;
			} else if (SandboxOptions.instance.getWeaponLootModifier() == 3) {
				double2 = 1.0;
			} else if (SandboxOptions.instance.getWeaponLootModifier() == 4) {
				double2 = 2.0;
			} else if (SandboxOptions.instance.getWeaponLootModifier() == 5) {
				double2 = 4.0;
			}

			kahluaTable.rawset("WeaponLootModifier", double2);
			double double3 = 1.0;
			if (SandboxOptions.instance.getOtherLootModifier() == 1) {
				double3 = 0.2;
			} else if (SandboxOptions.instance.getOtherLootModifier() == 2) {
				double3 = 0.6;
			} else if (SandboxOptions.instance.getOtherLootModifier() == 3) {
				double3 = 1.0;
			} else if (SandboxOptions.instance.getOtherLootModifier() == 4) {
				double3 = 2.0;
			} else if (SandboxOptions.instance.getOtherLootModifier() == 5) {
				double3 = 4.0;
			}

			kahluaTable.rawset("OtherLootModifier", double3);
		}
	}
}
