package zombie.vehicles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.util.list.PZArrayUtil;


public final class VehicleType {
	public final ArrayList vehiclesDefinition = new ArrayList();
	public int chanceToSpawnNormal = 80;
	public int chanceToSpawnBurnt = 0;
	public int spawnRate = 16;
	public int chanceOfOverCar = 0;
	public boolean randomAngle = false;
	public float baseVehicleQuality = 1.0F;
	public String name = "";
	private int chanceToSpawnKey = 70;
	public int chanceToPartDamage = 0;
	public boolean isSpecialCar = false;
	public boolean isBurntCar = false;
	public int chanceToSpawnSpecial = 5;
	public static final HashMap vehicles = new HashMap();
	public static final ArrayList specialVehicles = new ArrayList();

	public VehicleType(String string) {
		this.name = string;
	}

	public static void init() {
		initNormal();
		validate(vehicles.values());
		validate(specialVehicles);
	}

	private static void validate(Collection collection) {
	}

	private static void initNormal() {
		boolean boolean1 = DebugLog.isEnabled(DebugType.Lua);
		KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("VehicleZoneDistribution");
		Iterator iterator = kahluaTableImpl.delegate.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string = entry.getKey().toString();
			VehicleType vehicleType = new VehicleType(string);
			ArrayList arrayList = vehicleType.vehiclesDefinition;
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)entry.getValue();
			KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget("vehicles");
			Iterator iterator2 = kahluaTableImpl3.delegate.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry entry2 = (Entry)iterator2.next();
				String string2 = entry2.getKey().toString();
				VehicleScript vehicleScript = ScriptManager.instance.getVehicle(string2);
				if (vehicleScript == null) {
					DebugLog.General.warn("vehicle type \"" + string2 + "\" doesn\'t exist");
				}

				KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)entry2.getValue();
				arrayList.add(new VehicleType.VehicleTypeDefinition(string2, kahluaTableImpl4.rawgetInt("index"), kahluaTableImpl4.rawgetFloat("spawnChance")));
			}

			float float1 = 0.0F;
			int int1;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				float1 += ((VehicleType.VehicleTypeDefinition)arrayList.get(int1)).spawnChance;
			}

			float1 = 100.0F / float1;
			if (boolean1) {
				DebugLog.Lua.println("Vehicle spawn rate:");
			}

			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				VehicleType.VehicleTypeDefinition vehicleTypeDefinition = (VehicleType.VehicleTypeDefinition)arrayList.get(int1);
				vehicleTypeDefinition.spawnChance *= float1;
				if (boolean1) {
					DebugLog.Lua.println(string + ": " + ((VehicleType.VehicleTypeDefinition)arrayList.get(int1)).vehicleType + " " + ((VehicleType.VehicleTypeDefinition)arrayList.get(int1)).spawnChance + "%");
				}
			}

			if (kahluaTableImpl2.delegate.containsKey("chanceToPartDamage")) {
				vehicleType.chanceToPartDamage = kahluaTableImpl2.rawgetInt("chanceToPartDamage");
			}

			if (kahluaTableImpl2.delegate.containsKey("chanceToSpawnNormal")) {
				vehicleType.chanceToSpawnNormal = kahluaTableImpl2.rawgetInt("chanceToSpawnNormal");
			}

			if (kahluaTableImpl2.delegate.containsKey("chanceToSpawnSpecial")) {
				vehicleType.chanceToSpawnSpecial = kahluaTableImpl2.rawgetInt("chanceToSpawnSpecial");
			}

			if (kahluaTableImpl2.delegate.containsKey("specialCar")) {
				vehicleType.isSpecialCar = kahluaTableImpl2.rawgetBool("specialCar");
			}

			if (kahluaTableImpl2.delegate.containsKey("burntCar")) {
				vehicleType.isBurntCar = kahluaTableImpl2.rawgetBool("burntCar");
			}

			if (kahluaTableImpl2.delegate.containsKey("baseVehicleQuality")) {
				vehicleType.baseVehicleQuality = kahluaTableImpl2.rawgetFloat("baseVehicleQuality");
			}

			if (kahluaTableImpl2.delegate.containsKey("chanceOfOverCar")) {
				vehicleType.chanceOfOverCar = kahluaTableImpl2.rawgetInt("chanceOfOverCar");
			}

			if (kahluaTableImpl2.delegate.containsKey("randomAngle")) {
				vehicleType.randomAngle = kahluaTableImpl2.rawgetBool("randomAngle");
			}

			if (kahluaTableImpl2.delegate.containsKey("spawnRate")) {
				vehicleType.spawnRate = kahluaTableImpl2.rawgetInt("spawnRate");
			}

			if (kahluaTableImpl2.delegate.containsKey("chanceToSpawnKey")) {
				vehicleType.chanceToSpawnKey = kahluaTableImpl2.rawgetInt("chanceToSpawnKey");
			}

			if (kahluaTableImpl2.delegate.containsKey("chanceToSpawnBurnt")) {
				vehicleType.chanceToSpawnBurnt = kahluaTableImpl2.rawgetInt("chanceToSpawnBurnt");
			}

			vehicles.put(string, vehicleType);
			if (vehicleType.isSpecialCar) {
				specialVehicles.add(vehicleType);
			}
		}

		HashSet hashSet = new HashSet();
		Iterator iterator3 = vehicles.values().iterator();
		while (iterator3.hasNext()) {
			VehicleType vehicleType2 = (VehicleType)iterator3.next();
			Iterator iterator4 = vehicleType2.vehiclesDefinition.iterator();
			while (iterator4.hasNext()) {
				VehicleType.VehicleTypeDefinition vehicleTypeDefinition2 = (VehicleType.VehicleTypeDefinition)iterator4.next();
				hashSet.add(vehicleTypeDefinition2.vehicleType);
			}
		}

		iterator3 = ScriptManager.instance.getAllVehicleScripts().iterator();
		while (iterator3.hasNext()) {
			VehicleScript vehicleScript2 = (VehicleScript)iterator3.next();
			if (!hashSet.contains(vehicleScript2.getFullName())) {
				DebugLog.General.warn("vehicle type \"" + vehicleScript2.getFullName() + "\" isn\'t in VehicleZoneDistribution");
			}
		}
	}

	public static boolean hasTypeForZone(String string) {
		if (vehicles.isEmpty()) {
			init();
		}

		string = string.toLowerCase();
		return vehicles.containsKey(string);
	}

	public static VehicleType getRandomVehicleType(String string) {
		return getRandomVehicleType(string, true);
	}

	public static VehicleType getRandomVehicleType(String string, Boolean Boolean1) {
		if (vehicles.isEmpty()) {
			init();
		}

		string = string.toLowerCase();
		VehicleType vehicleType = (VehicleType)vehicles.get(string);
		if (vehicleType == null) {
			DebugLog.log(string + " Don\'t exist in VehicleZoneDistribution");
			return null;
		} else if (Rand.Next(100) < vehicleType.chanceToSpawnBurnt) {
			if (Rand.Next(100) < 80) {
				vehicleType = (VehicleType)vehicles.get("normalburnt");
			} else {
				vehicleType = (VehicleType)vehicles.get("specialburnt");
			}

			return vehicleType;
		} else {
			if (Boolean1 && vehicleType.isSpecialCar && Rand.Next(100) < vehicleType.chanceToSpawnNormal) {
				vehicleType = (VehicleType)vehicles.get("parkingstall");
			}

			if (!vehicleType.isBurntCar && !vehicleType.isSpecialCar && Rand.Next(100) < vehicleType.chanceToSpawnSpecial) {
				vehicleType = (VehicleType)PZArrayUtil.pickRandom((List)specialVehicles);
			}

			if (vehicleType.isBurntCar) {
				if (Rand.Next(100) < 80) {
					vehicleType = (VehicleType)vehicles.get("normalburnt");
				} else {
					vehicleType = (VehicleType)vehicles.get("specialburnt");
				}
			}

			return vehicleType;
		}
	}

	public static VehicleType getTypeFromName(String string) {
		if (vehicles.isEmpty()) {
			init();
		}

		return (VehicleType)vehicles.get(string);
	}

	public float getBaseVehicleQuality() {
		return this.baseVehicleQuality;
	}

	public float getRandomBaseVehicleQuality() {
		return Rand.Next(this.baseVehicleQuality - 0.1F, this.baseVehicleQuality + 0.1F);
	}

	public int getChanceToSpawnKey() {
		return this.chanceToSpawnKey;
	}

	public void setChanceToSpawnKey(int int1) {
		this.chanceToSpawnKey = int1;
	}

	public static void Reset() {
		vehicles.clear();
		specialVehicles.clear();
	}

	public static class VehicleTypeDefinition {
		public String vehicleType;
		public int index = -1;
		public float spawnChance = 0.0F;

		public VehicleTypeDefinition(String string, int int1, float float1) {
			this.vehicleType = string;
			this.index = int1;
			this.spawnChance = float1;
		}
	}
}