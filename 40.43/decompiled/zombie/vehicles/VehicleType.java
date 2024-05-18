package zombie.vehicles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;

public class VehicleType {
   public ArrayList vehiclesDefinition = new ArrayList();
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
   public static HashMap vehicles = new HashMap();
   public static HashMap specialVehicles = new HashMap();

   public VehicleType(String var1) {
      this.name = var1;
   }

   public static void init() {
      initNormal();
      validate(vehicles.values());
      validate(specialVehicles.values());
   }

   private static void validate(Collection var0) {
   }

   private static void initNormal() {
      KahluaTableImpl var0 = (KahluaTableImpl)LuaManager.env.rawget("VehicleZoneDistribution");
      Iterator var1 = var0.delegate.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         ArrayList var3 = new ArrayList();
         String var4 = var2.getKey().toString();
         KahluaTableImpl var5 = (KahluaTableImpl)var2.getValue();
         KahluaTableImpl var6 = (KahluaTableImpl)var5.rawget("vehicles");
         Iterator var7 = var6.delegate.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            String var9 = var8.getKey().toString();
            VehicleScript var10 = ScriptManager.instance.getVehicle(var9);
            if (var10 == null) {
               DebugLog.log("WARNING: vehicle type \"" + var9 + "\" doesn't exist");
            }

            KahluaTableImpl var11 = (KahluaTableImpl)var8.getValue();
            var3.add(new VehicleType.VehicleTypeDefinition(var9, var11.rawgetInt("index"), (float)var11.rawgetInt("spawnChance")));
         }

         float var18 = 0.0F;

         int var19;
         for(var19 = 0; var19 < var3.size(); ++var19) {
            var18 += ((VehicleType.VehicleTypeDefinition)var3.get(var19)).spawnChance;
         }

         var18 = 100.0F / var18;
         DebugLog.log("Vehicle spawn rate:");

         for(var19 = 0; var19 < var3.size(); ++var19) {
            VehicleType.VehicleTypeDefinition var10000 = (VehicleType.VehicleTypeDefinition)var3.get(var19);
            var10000.spawnChance *= var18;
            DebugLog.log(var4 + ": " + ((VehicleType.VehicleTypeDefinition)var3.get(var19)).vehicleType + " " + ((VehicleType.VehicleTypeDefinition)var3.get(var19)).spawnChance + "%");
         }

         VehicleType var20 = new VehicleType(var4);
         var20.vehiclesDefinition = var3;
         if (var5.delegate.containsKey("chanceToPartDamage")) {
            var20.chanceToPartDamage = var5.rawgetInt("chanceToPartDamage");
         }

         if (var5.delegate.containsKey("chanceToSpawnNormal")) {
            var20.chanceToSpawnNormal = var5.rawgetInt("chanceToSpawnNormal");
         }

         if (var5.delegate.containsKey("chanceToSpawnSpecial")) {
            var20.chanceToSpawnSpecial = var5.rawgetInt("chanceToSpawnSpecial");
         }

         if (var5.delegate.containsKey("specialCar")) {
            var20.isSpecialCar = var5.rawgetBool("specialCar");
         }

         if (var5.delegate.containsKey("burntCar")) {
            var20.isBurntCar = var5.rawgetBool("burntCar");
         }

         if (var5.delegate.containsKey("baseVehicleQuality")) {
            var20.baseVehicleQuality = var5.rawgetFloat("baseVehicleQuality");
         }

         if (var5.delegate.containsKey("chanceOfOverCar")) {
            var20.chanceOfOverCar = var5.rawgetInt("chanceOfOverCar");
         }

         if (var5.delegate.containsKey("randomAngle")) {
            var20.randomAngle = var5.rawgetBool("burrandomAnglentCar");
         }

         if (var5.delegate.containsKey("spawnRate")) {
            var20.spawnRate = var5.rawgetInt("spawnRate");
         }

         if (var5.delegate.containsKey("chanceToSpawnKey")) {
            var20.chanceToSpawnKey = var5.rawgetInt("chanceToSpawnKey");
         }

         if (var5.delegate.containsKey("chanceToSpawnBurnt")) {
            var20.chanceToSpawnBurnt = var5.rawgetInt("chanceToSpawnBurnt");
         }

         vehicles.put(var4, var20);
         if (var20.isSpecialCar) {
            specialVehicles.put(var4, var20);
         }
      }

      HashSet var12 = new HashSet();
      Iterator var13 = vehicles.values().iterator();

      while(var13.hasNext()) {
         VehicleType var14 = (VehicleType)var13.next();
         Iterator var16 = var14.vehiclesDefinition.iterator();

         while(var16.hasNext()) {
            VehicleType.VehicleTypeDefinition var17 = (VehicleType.VehicleTypeDefinition)var16.next();
            var12.add(var17.vehicleType);
         }
      }

      var13 = ScriptManager.instance.getAllVehicleScripts().iterator();

      while(var13.hasNext()) {
         VehicleScript var15 = (VehicleScript)var13.next();
         if (!var12.contains(var15.getFullName())) {
            DebugLog.log("WARNING: vehicle type \"" + var15.getFullName() + "\" isn't in VehicleZoneDistribution");
         }
      }

   }

   public static VehicleType getRandomVehicleType(String var0) {
      var0 = var0.toLowerCase();
      VehicleType var1 = (VehicleType)vehicles.get(var0);
      if (var1 == null) {
         DebugLog.log(var0 + " Don't exist in VehicleZoneDistribution");
         return null;
      } else if (Rand.Next(100) < var1.chanceToSpawnBurnt) {
         if (Rand.Next(100) < 80) {
            var1 = (VehicleType)vehicles.get("normalburnt");
         } else {
            var1 = (VehicleType)vehicles.get("specialburnt");
         }

         return var1;
      } else {
         if (var1.isSpecialCar && Rand.Next(100) < var1.chanceToSpawnNormal) {
            var1 = (VehicleType)vehicles.get("parkingstall");
         }

         if (!var1.isBurntCar && !var1.isSpecialCar && Rand.Next(100) < var1.chanceToSpawnSpecial) {
            String var2 = (String)specialVehicles.keySet().toArray()[Rand.Next(0, specialVehicles.keySet().toArray().length)];
            var1 = (VehicleType)specialVehicles.get(var2);
         }

         if (var1.isBurntCar) {
            if (Rand.Next(100) < 80) {
               var1 = (VehicleType)vehicles.get("normalburnt");
            } else {
               var1 = (VehicleType)vehicles.get("specialburnt");
            }
         }

         return var1;
      }
   }

   public static VehicleType getTypeFromName(String var0) {
      return (VehicleType)vehicles.get(var0);
   }

   public float getBaseVehicleQuality() {
      return Rand.Next(this.baseVehicleQuality - 0.1F, this.baseVehicleQuality + 0.1F);
   }

   public int getChanceToSpawnKey() {
      return this.chanceToSpawnKey;
   }

   public void setChanceToSpawnKey(int var1) {
      this.chanceToSpawnKey = var1;
   }

   public static class VehicleTypeDefinition {
      public String vehicleType;
      public int index = -1;
      public float spawnChance = 0.0F;

      public VehicleTypeDefinition(String var1, int var2, float var3) {
         this.vehicleType = var1;
         this.index = var2;
         this.spawnChance = var3;
      }
   }
}
