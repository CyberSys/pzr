package zombie.characters;

import java.util.Iterator;
import java.util.LinkedHashMap;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.iso.areas.NonPvpZone;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.util.Type;


public class SafetySystemManager {
	private static final LinkedHashMap playerCooldown = new LinkedHashMap();
	private static final LinkedHashMap playerSafety = new LinkedHashMap();
	private static final LinkedHashMap playerDelay = new LinkedHashMap();
	private static final long safetyDelay = 1500L;

	private static void updateTimers(Safety safety) {
		float float1 = GameTime.instance.getRealworldSecondsSinceLastUpdate();
		if (safety.getToggle() > 0.0F) {
			safety.setToggle(safety.getToggle() - float1);
			if (safety.getToggle() <= 0.0F) {
				safety.setToggle(0.0F);
				if (!safety.isLast()) {
					safety.setEnabled(!safety.isEnabled());
				}
			}
		} else if (safety.getCooldown() > 0.0F) {
			safety.setCooldown(safety.getCooldown() - float1);
		} else {
			safety.setCooldown(0.0F);
		}
	}

	private static void updateNonPvpZone(IsoPlayer player, boolean boolean1) {
		if (boolean1 && !player.networkAI.wasNonPvpZone) {
			storeSafety(player);
			GameServer.sendChangeSafety(player.getSafety());
		} else if (!boolean1 && player.networkAI.wasNonPvpZone) {
			restoreSafety(player);
			GameServer.sendChangeSafety(player.getSafety());
		}

		player.networkAI.wasNonPvpZone = boolean1;
	}

	static void update(IsoPlayer player) {
		boolean boolean1 = NonPvpZone.getNonPvpZone(PZMath.fastfloor(player.getX()), PZMath.fastfloor(player.getY())) != null;
		if (!boolean1) {
			updateTimers(player.getSafety());
		}

		if (GameServer.bServer) {
			updateNonPvpZone(player, boolean1);
		}
	}

	public static void clear() {
		playerCooldown.clear();
		playerSafety.clear();
		playerDelay.clear();
	}

	public static void clearSafety(IsoPlayer player) {
		if (player != null) {
			Safety safety = player.getSafety();
			LoggerManager.getLogger("pvp").write(String.format("user \"%s\" clear safety %s", player.getUsername(), safety.getDescription()), "INFO");
			playerCooldown.remove(player.getUsername());
			playerSafety.remove(player.getUsername());
			playerDelay.remove(player.getUsername());
		} else if (Core.bDebug) {
			DebugLog.Combat.debugln("ClearSafety: player not found");
		}
	}

	public static void storeSafety(IsoPlayer player) {
		try {
			if (player != null && player.isAlive()) {
				Safety safety = player.getSafety();
				LoggerManager.getLogger("pvp").write(String.format("user \"%s\" store safety %s", player.getUsername(), safety.getDescription()), "INFO");
				playerSafety.put(player.getUsername(), safety.isEnabled());
				playerCooldown.put(player.getUsername(), safety.getCooldown());
				playerDelay.put(player.getUsername(), System.currentTimeMillis());
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

				if (playerDelay.size() > ServerOptions.instance.MaxPlayers.getValue() * 1000) {
					iterator = playerDelay.entrySet().iterator();
					if (iterator.hasNext()) {
						iterator.next();
						iterator.remove();
					}
				}
			} else if (Core.bDebug) {
				DebugLog.Combat.debugln("StoreSafety: player not found");
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "StoreSafety failed", LogSeverity.Error);
		}
	}

	public static void restoreSafety(IsoPlayer player) {
		try {
			if (player != null) {
				Safety safety = player.getSafety();
				if (playerSafety.containsKey(player.getUsername())) {
					safety.setEnabled((Boolean)playerSafety.remove(player.getUsername()));
				}

				if (playerCooldown.containsKey(player.getUsername())) {
					safety.setCooldown((Float)playerCooldown.remove(player.getUsername()));
				}

				playerDelay.put(player.getUsername(), System.currentTimeMillis());
				LoggerManager.getLogger("pvp").write(String.format("user \"%s\" restore safety %s", player.getUsername(), safety.getDescription()), "INFO");
			} else if (Core.bDebug) {
				DebugLog.Combat.debugln("RestoreSafety: player not found");
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "RestoreSafety failed", LogSeverity.Error);
		}
	}

	public static void updateOptions() {
		boolean boolean1 = ServerOptions.instance.PVP.getValue();
		boolean boolean2 = ServerOptions.instance.SafetySystem.getValue();
		Iterator iterator;
		IsoPlayer player;
		if (!boolean1) {
			clear();
			iterator = GameServer.IDToPlayerMap.values().iterator();
			while (iterator.hasNext()) {
				player = (IsoPlayer)iterator.next();
				if (player != null) {
					player.getSafety().setEnabled(true);
					player.getSafety().setLast(false);
					player.getSafety().setCooldown(0.0F);
					player.getSafety().setToggle(0.0F);
					GameServer.sendChangeSafety(player.getSafety());
				}
			}
		} else if (!boolean2) {
			clear();
			iterator = GameServer.IDToPlayerMap.values().iterator();
			while (iterator.hasNext()) {
				player = (IsoPlayer)iterator.next();
				if (player != null) {
					player.getSafety().setEnabled(false);
					player.getSafety().setLast(false);
					player.getSafety().setCooldown(0.0F);
					player.getSafety().setToggle(0.0F);
					GameServer.sendChangeSafety(player.getSafety());
				}
			}
		}
	}

	public static boolean checkUpdateDelay(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2) {
		boolean boolean1 = false;
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		IsoPlayer player2 = (IsoPlayer)Type.tryCastTo(gameCharacter2, IsoPlayer.class);
		if (player != null && player2 != null) {
			long long1 = System.currentTimeMillis();
			long long2;
			boolean boolean2;
			if (playerDelay.containsKey(player.getUsername())) {
				long2 = long1 - (Long)playerDelay.getOrDefault(player.getUsername(), 0L);
				boolean2 = long2 < 1500L;
				boolean1 = boolean2;
				if (!boolean2) {
					playerDelay.remove(player.getUsername());
				}
			}

			if (playerDelay.containsKey(player2.getUsername())) {
				long2 = long1 - (Long)playerDelay.getOrDefault(player2.getUsername(), 0L);
				boolean2 = long2 < 1500L;
				if (!boolean1) {
					boolean1 = boolean2;
				}

				if (!boolean2) {
					playerDelay.remove(player2.getUsername());
				}
			}
		}

		return boolean1;
	}
}
