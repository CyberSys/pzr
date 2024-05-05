package zombie.network;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.areas.NonPvpZone;
import zombie.network.packets.hit.Character;
import zombie.network.packets.hit.Hit;
import zombie.network.packets.hit.IMovable;
import zombie.network.packets.hit.IPositional;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.Zombie;
import zombie.util.StringUtils;
import zombie.util.Type;


public class PacketValidator {
	private static final long USER_LOG_INTERVAL_MS = 300000L;
	private static final int MAX_TYPE_8 = 10;
	private static final int MAX_TYPE_4 = 101;

	public static boolean checkType1(UdpConnection udpConnection, Character character, Character character2, String string) {
		boolean boolean1 = checkPVP(character.getCharacter(), character2.getCharacter());
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type1")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type1", string));
		}

		return boolean1;
	}

	public static boolean checkType2(UdpConnection udpConnection, IMovable iMovable, String string) {
		float float1 = iMovable.getSpeed();
		double double1 = iMovable.isVehicle() ? ServerOptions.instance.SpeedLimit.getValue() : 10.0;
		boolean boolean1 = (double)float1 <= double1 * 3.0;
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type2")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%f\"", udpConnection.username, "UI_ValidationFailed_Type2", string, float1));
		}

		return boolean1;
	}

	public static boolean checkType3(UdpConnection udpConnection, IPositional iPositional, IPositional iPositional2, String string) {
		float float1 = IsoUtils.DistanceTo(iPositional2.getX(), iPositional2.getY(), iPositional.getX(), iPositional.getY());
		boolean boolean1 = float1 <= (float)(udpConnection.ReleventRange * 10);
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type3")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%f\"", udpConnection.username, "UI_ValidationFailed_Type3", string, float1));
		}

		return boolean1;
	}

	public static boolean checkType4(UdpConnection udpConnection, Hit hit, String string) {
		float float1 = hit.getDamage();
		boolean boolean1 = float1 <= 101.0F;
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type4")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%f\"", udpConnection.username, "UI_ValidationFailed_Type4", string, float1));
		}

		return boolean1;
	}

	public static boolean checkType5(UdpConnection udpConnection, Zombie zombie, String string) {
		UdpConnection udpConnection2 = ((IsoZombie)zombie.getCharacter()).authOwner;
		boolean boolean1 = udpConnection2 == udpConnection;
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type5")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type5", string, Optional.ofNullable(udpConnection2).map((udpConnectionx)->{
				return udpConnectionx.username;
			}).orElse("")));
		}

		return boolean1;
	}

	public static boolean checkType6(UdpConnection udpConnection, Player player, String string) {
		IsoPlayer player2 = (IsoPlayer)player.getCharacter();
		boolean boolean1 = Arrays.stream(udpConnection.players).anyMatch((playerx)->{
    return playerx.getOnlineID() == player2.getOnlineID();
});
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type6")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type6", string, player2.getUsername()));
		}

		return boolean1;
	}

	public static boolean checkType7(UdpConnection udpConnection, String string, String string2) {
		boolean boolean1 = StringUtils.isNullOrEmpty(string) || string.equals(udpConnection.username) || udpConnection.accessLevel >= 16;
		if (!boolean1 && doKickUser(udpConnection, string2, "UI_ValidationFailed_Type7")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%s\"", udpConnection.username, "UI_ValidationFailed_Type7", string2, string));
		}

		return boolean1;
	}

	public static boolean checkType8(UdpConnection udpConnection, IPositional iPositional, IPositional iPositional2, String string) {
		float float1 = IsoUtils.DistanceTo(iPositional2.getX(), iPositional2.getY(), iPositional.getX(), iPositional.getY());
		boolean boolean1 = float1 <= 10.0F;
		if (!boolean1 && doKickUser(udpConnection, string, "UI_ValidationFailed_Type3")) {
			LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" value=\"%f\"", udpConnection.username, "UI_ValidationFailed_Type3", string, float1));
		}

		return boolean1;
	}

	private static boolean isUntouchable(UdpConnection udpConnection) {
		return Core.bDebug || !udpConnection.isFullyConnected() || PlayerType.isPrivileged(udpConnection.accessLevel) || Arrays.stream(udpConnection.players).filter(Objects::nonNull).anyMatch(IsoGameCharacter::isGodMod);
	}

	public static boolean doLogUser(UdpConnection udpConnection, String string, String string2) {
		if (isUntouchable(udpConnection)) {
			return false;
		} else {
			long long1 = System.currentTimeMillis();
			if (long1 > udpConnection.lastUnauthorizedPacket) {
				udpConnection.lastUnauthorizedPacket = long1 + 300000L;
				ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.UnauthorizedPacket, string2, string, 1);
			}

			return true;
		}
	}

	public static boolean doKickUser(UdpConnection udpConnection, String string, String string2) {
		if (isUntouchable(udpConnection)) {
			return false;
		} else {
			ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Kicked, string2, string, 1);
			GameServer.kick(udpConnection, "UI_Policy_Kick", string2);
			udpConnection.forceDisconnect(string);
			GameServer.addDisconnect(udpConnection);
			return true;
		}
	}

	public static boolean doBanUser(UdpConnection udpConnection, String string, String string2) throws Exception {
		if (isUntouchable(udpConnection)) {
			return false;
		} else {
			ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Banned, string2, string, 1);
			ServerWorldDatabase.instance.banUser(udpConnection.username, true);
			if (SteamUtils.isSteamModeEnabled()) {
				String string3 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
				ServerWorldDatabase.instance.banSteamID(string3, string2, true);
			} else {
				ServerWorldDatabase.instance.banIp(udpConnection.ip, udpConnection.username, string2, true);
			}

			GameServer.kick(udpConnection, "UI_Policy_Ban", string2);
			udpConnection.forceDisconnect(string);
			GameServer.addDisconnect(udpConnection);
			return true;
		}
	}

	private static boolean checkPVP(IsoGameCharacter gameCharacter, IsoMovingObject movingObject) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		IsoPlayer player2 = (IsoPlayer)Type.tryCastTo(movingObject, IsoPlayer.class);
		if (player2 != null) {
			if (player2.isGodMod() || !ServerOptions.instance.PVP.getValue() || ServerOptions.instance.SafetySystem.getValue() && gameCharacter.isSafety() && ((IsoGameCharacter)movingObject).isSafety()) {
				return false;
			}

			if (NonPvpZone.getNonPvpZone((int)movingObject.getX(), (int)movingObject.getY()) != null) {
				return false;
			}

			if (player != null && NonPvpZone.getNonPvpZone((int)gameCharacter.getX(), (int)gameCharacter.getY()) != null) {
				return false;
			}

			if (player != null && !player.factionPvp && !player2.factionPvp) {
				Faction faction = Faction.getPlayerFaction(player);
				Faction faction2 = Faction.getPlayerFaction(player2);
				if (faction2 != null && faction == faction2) {
					return false;
				}
			}
		}

		return true;
	}

	static  {
		LoggerManager.createLogger("kick", Core.bDebug);
	}
}
