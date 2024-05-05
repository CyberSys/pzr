package zombie.network;

public class NetworkAIParams {
   public static final float PATH_SPEED = 0.00122F;
   public static final int ZOMBIE_UPDATE_INFO_BUNCH_RATE_MS = 200;
   public static final int CHARACTER_UPDATE_RATE_MS = 200;
   public static final int CHARACTER_EXTRAPOLATION_UPDATE_INTERVAL_MS = 500;
   public static final float ZOMBIE_ANTICIPATORY_UPDATE_MULTIPLIER = 0.6F;
   public static final int ZOMBIE_REMOVE_INTERVAL_MS = 4000;
   public static final int ZOMBIE_MAX_UPDATE_INTERVAL_MS = 3800;
   public static final int ZOMBIE_MIN_UPDATE_INTERVAL_MS = 200;
   public static final int CHARACTER_PREDICTION_INTERVAL_MS = 2000;
   public static final int ZOMBIE_OWNERSHIP_RANGE = 5;
   public static final int ZOMBIE_OWNERSHIP_RANGE_SQ = 25;
   public static final int ZOMBIE_MOVE_TO_TARGET_RANGE = 25;
   public static final int ZOMBIE_TELEPORT_PLAYER = 2;
   public static final int ZOMBIE_TELEPORT_DISTANCE_SQ = 9;
   public static final int ZOMBIE_UPDATE_RANGE = 40;
   public static final int ZOMBIE_UPDATE_RANGE_SQ = 1600;
   public static final int MAX_ZOMBIES_PER_UPDATE = 300;
}
