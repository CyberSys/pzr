package zombie.network;

import java.util.Iterator;
import java.util.LinkedHashMap;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;


public class SafetyManager {
	private static final LinkedHashMap playerCooldown = new LinkedHashMap();
	private static final LinkedHashMap playerSafety = new LinkedHashMap();

	public static void clear() {
		playerCooldown.clear();
		playerSafety.clear();
	}

	public static void clearSafety(IsoPlayer player) {
		if (player != null) {
			LoggerManager.getLogger("pvp").write(String.format("user \"%s\" clear safety %b %f", player.getUsername(), player.isSafety(), player.getSafetyCooldown()), "INFO");
			playerCooldown.remove(player.getUsername());
			playerSafety.remove(player.getUsername());
		} else if (Core.bDebug) {
			DebugLog.Multiplayer.debugln("ClearSafety: player not found");
		}
	}

	public static void storeSafety(IsoPlayer player) {
		try {
			if (player != null && player.isAlive()) {
				LoggerManager.getLogger("pvp").write(String.format("user \"%s\" store safety %b %f", player.getUsername(), player.isSafety(), player.getSafetyCooldown()), "INFO");
				playerSafety.put(player.getUsername(), player.isSafety());
				playerCooldown.put(player.getUsername(), player.getSafetyCooldown());
				Iterator iterator;
				if (playerCooldown.size() > ServerOptions.instance.MaxPlayers.getValue() * 1000) {
					iterator = playerCooldown.entrySet().iterator();
					if (iterator.hasNext()) {
						iterator.next();
						iterator.remove();
					}
				}

				if (playerSafety.size() > ServerOptions.instance.MaxPlayers.getValue() * 1000) {
					iterator = playerSafety.entrySet().iterator();
					if (iterator.hasNext()) {
						iterator.next();
						iterator.remove();
					}
				}
			} else if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("StoreSafety: player not found");
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "StoreSafety failed", LogSeverity.Error);
		}
	}

	public static void restoreSafety(IsoPlayer player) {
		try {
			if (player != null) {
				if (playerSafety.containsKey(player.getUsername())) {
					player.setSafety((Boolean)playerSafety.remove(player.getUsername()));
				}

				if (playerCooldown.containsKey(player.getUsername())) {
					player.setSafetyCooldown((Float)playerCooldown.remove(player.getUsername()));
				}

				LoggerManager.getLogger("pvp").write(String.format("user \"%s\" restore safety %b %f", player.getUsername(), player.isSafety(), player.getSafetyCooldown()), "INFO");
			} else if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("RestoreSafety: player not found");
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "RestoreSafety failed", LogSeverity.Error);
		}
	}
}
