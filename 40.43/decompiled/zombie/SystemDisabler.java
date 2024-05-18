package zombie;

public class SystemDisabler {
   public static boolean doCharacterStats = true;
   public static boolean doZombieCreation = true;
   public static boolean doSurvivorCreation = false;
   public static boolean doPlayerCreation = true;
   public static boolean doOverridePOVCharacters = true;
   public static boolean doVehiclesEverywhere = false;
   public static boolean doWorldSyncEnable = false;
   public static boolean doObjectStateSyncEnable = false;
   private static boolean doAllowDebugConnections = false;
   private static boolean doOverrideServerConnectDebugCheck = false;

   public static void setDoCharacterStats(boolean var0) {
      doCharacterStats = var0;
   }

   public static void setDoZombieCreation(boolean var0) {
      doZombieCreation = var0;
   }

   public static void setDoSurvivorCreation(boolean var0) {
      doSurvivorCreation = var0;
   }

   public static void setDoPlayerCreation(boolean var0) {
      doPlayerCreation = var0;
   }

   public static void setOverridePOVCharacters(boolean var0) {
      doOverridePOVCharacters = var0;
   }

   public static void setVehiclesEverywhere(boolean var0) {
      doVehiclesEverywhere = var0;
   }

   public static void setWorldSyncEnable(boolean var0) {
      doWorldSyncEnable = var0;
   }

   public static void setObjectStateSyncEnable(boolean var0) {
      doObjectStateSyncEnable = var0;
   }

   public static boolean getAllowDebugConnections() {
      return doAllowDebugConnections;
   }

   public static boolean getOverrideServerConnectDebugCheck() {
      return doOverrideServerConnectDebugCheck;
   }

   public static void Reset() {
      doCharacterStats = true;
      doZombieCreation = true;
      doSurvivorCreation = false;
      doPlayerCreation = true;
      doOverridePOVCharacters = true;
      doVehiclesEverywhere = false;
      doAllowDebugConnections = false;
      doWorldSyncEnable = false;
      doObjectStateSyncEnable = false;
   }
}
