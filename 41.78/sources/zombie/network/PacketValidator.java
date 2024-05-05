package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import zombie.GameWindow;
import zombie.SystemDisabler;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SafetySystemManager;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.areas.NonPvpZone;
import zombie.network.packets.hit.Character;
import zombie.network.packets.hit.Hit;
import zombie.network.packets.hit.IMovable;
import zombie.network.packets.hit.IPositional;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.Zombie;
import zombie.scripting.objects.Recipe;
import zombie.util.StringUtils;
import zombie.util.Type;


public class PacketValidator {
	private static final int SUSPICIOUS_ACTIVITIES_MAX = 4;
	private final UpdateLimit ulSuspiciousActivity = new UpdateLimit(150000L);
	public final HashMap details = new HashMap();
	public final HashMap detailsFromClient = new HashMap();
	private boolean failed = false;
	private static final long USER_LOG_INTERVAL_MS = 1000L;
	private static final int MAX_TYPE_3 = 10;
	private static final int MAX_TYPE_4 = 101;
	private final UdpConnection connection;
	private final UpdateLimit ulTimeMultiplier = new UpdateLimit(this.getTimeMultiplierTimeout());
	private final UpdateLimit ulRecipeChecksumInterval = new UpdateLimit(this.getChecksumInterval());
	private final UpdateLimit ulRecipeChecksumTimeout = new UpdateLimit(this.getChecksumTimeout());
	private int salt;
	private int suspiciousActivityCounter;
	private String suspiciousActivityDescription;

	public PacketValidator(UdpConnection udpConnection) {
		this.connection = udpConnection;
		this.suspiciousActivityCounter = 0;
	}

	public void reset() {
		this.salt = Rand.Next(Integer.MAX_VALUE);
	}

	private boolean isReady() {
		IsoPlayer player = GameServer.getPlayerByRealUserName(this.connection.username);
		return this.connection.isFullyConnected() && this.connection.isConnectionGraceIntervalTimeout() && !GameServer.bFastForward && player != null && player.isAlive();
	}

	public int getSalt() {
		return this.salt;
	}

	private long getChecksumDelay() {
		return (long)(1000.0 * ServerOptions.getInstance().AntiCheatProtectionType22ThresholdMultiplier.getValue());
	}

	private long getChecksumInterval() {
		return (long)(4000.0 * ServerOptions.getInstance().AntiCheatProtectionType22ThresholdMultiplier.getValue());
	}

	private long getChecksumTimeout() {
		return (long)(10000.0 * ServerOptions.getInstance().AntiCheatProtectionType22ThresholdMultiplier.getValue());
	}

	public void failChecksum() {
		if (ServerOptions.instance.AntiCheatProtectionType21.getValue() && checkUser(this.connection)) {
			DebugLog.Multiplayer.warn("Checksum fail for \"%s\" (Type21)", this.connection.username);
			this.failed = true;
		}

		this.ulRecipeChecksumTimeout.Reset(this.getChecksumDelay());
	}

	public boolean isFailed() {
		return this.failed;
	}

	private void timeoutChecksum() {
		if (this.failed) {
			doKickUser(this.connection, this.getClass().getSimpleName(), "Type21", this.getDescription());
		} else {
			if (ServerOptions.instance.AntiCheatProtectionType22.getValue() && checkUser(this.connection)) {
				doKickUser(this.connection, this.getClass().getSimpleName(), "Type22", (String)null);
			}

			this.ulRecipeChecksumTimeout.Reset(this.getChecksumTimeout());
		}
	}

	public void successChecksum() {
		this.ulRecipeChecksumTimeout.Reset(this.getChecksumTimeout());
	}

	public void sendChecksum(boolean boolean1, boolean boolean2, boolean boolean3) {
		this.salt = Rand.Next(Integer.MAX_VALUE);
		GameServer.sendValidatePacket(this.connection, boolean1, boolean2, boolean3);
		this.ulRecipeChecksumInterval.Reset(this.getChecksumInterval());
	}

	private long getTimeMultiplierTimeout() {
		return (long)(10000.0 * ServerOptions.getInstance().AntiCheatProtectionType24ThresholdMultiplier.getValue());
	}

	public void failTimeMultiplier(float float1) {
		if (ServerOptions.instance.AntiCheatProtectionType23.getValue() && checkUser(this.connection)) {
			doKickUser(this.connection, this.getClass().getSimpleName(), "Type23", String.valueOf(float1));
		}

		this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
	}

	public void timeoutTimeMultiplier() {
		if (ServerOptions.instance.AntiCheatProtectionType24.getValue() && checkUser(this.connection)) {
			doKickUser(this.connection, this.getClass().getSimpleName(), "Type24", (String)null);
		}

		this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
	}

	public void successTimeMultiplier() {
		this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
	}

	public void update() {
		if (GameServer.bServer) {
			if (this.ulSuspiciousActivity.Check()) {
				this.updateSuspiciousActivityCounter();
			}

			if (this.isReady()) {
				if (!this.failed && this.ulRecipeChecksumInterval.Check()) {
					this.sendChecksum(false, false, false);
				}

				if (this.ulRecipeChecksumTimeout.Check()) {
					this.timeoutChecksum();
				}

				if (this.ulTimeMultiplier.Check()) {
					this.timeoutTimeMultiplier();
				}
			} else {
				this.ulRecipeChecksumInterval.Reset(this.getChecksumInterval());
				this.ulRecipeChecksumTimeout.Reset(this.getChecksumTimeout());
				this.ulTimeMultiplier.Reset(this.getTimeMultiplierTimeout());
				this.failed = false;
			}
		}
	}

	public static boolean checkPVP(UdpConnection udpConnection, Character character, Character character2, String string) {
		boolean boolean1 = checkPVP(character.getCharacter(), character2.getCharacter()) || SafetySystemManager.checkUpdateDelay(character.getCharacter(), character2.getCharacter());
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType1.getValue() && checkUser(udpConnection)) {
			doKickUser(udpConnection, string, "Type1", (String)null);
		}

		return boolean1;
	}

	public static boolean checkSpeed(UdpConnection udpConnection, IMovable iMovable, String string) {
		float float1 = iMovable.getSpeed();
		double double1 = iMovable.isVehicle() ? ServerOptions.instance.SpeedLimit.getValue() : 10.0;
		boolean boolean1 = (double)float1 <= double1 * ServerOptions.instance.AntiCheatProtectionType2ThresholdMultiplier.getValue();
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType2.getValue() && checkUser(udpConnection)) {
			doKickUser(udpConnection, string, "Type2", String.valueOf(float1));
		}

		return boolean1;
	}

	public static boolean checkLongDistance(UdpConnection udpConnection, IPositional iPositional, IPositional iPositional2, String string) {
		float float1 = IsoUtils.DistanceTo(iPositional2.getX(), iPositional2.getY(), iPositional.getX(), iPositional.getY());
		boolean boolean1 = (double)float1 <= (double)(udpConnection.ReleventRange * 10) * ServerOptions.instance.AntiCheatProtectionType3ThresholdMultiplier.getValue();
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType3.getValue() && checkUser(udpConnection)) {
			if (udpConnection.validator.checkSuspiciousActivity("Type3")) {
				doKickUser(udpConnection, string, "Type3", String.valueOf(float1));
			} else {
				doLogUser(udpConnection, Userlog.UserlogType.SuspiciousActivity, string, "Type3");
			}
		}

		return boolean1;
	}

	public static boolean checkDamage(UdpConnection udpConnection, Hit hit, String string) {
		float float1 = hit.getDamage();
		boolean boolean1 = (double)float1 <= 101.0 * ServerOptions.instance.AntiCheatProtectionType4ThresholdMultiplier.getValue();
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType4.getValue() && checkUser(udpConnection)) {
			doKickUser(udpConnection, string, "Type4", String.valueOf(float1));
		}

		return boolean1;
	}

	public static boolean checkOwner(UdpConnection udpConnection, Zombie zombie, String string) {
		IsoZombie zombie2 = (IsoZombie)zombie.getCharacter();
		UdpConnection udpConnection2 = zombie2.authOwner;
		boolean boolean1 = udpConnection2 == udpConnection && System.currentTimeMillis() - zombie2.lastChangeOwner > 2000L;
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType5.getValue() && checkUser(udpConnection)) {
			if (udpConnection.validator.checkSuspiciousActivity("Type5")) {
				doKickUser(udpConnection, string, "Type5", (String)Optional.ofNullable(udpConnection2).map((udpConnectionx)->{
					return udpConnectionx.username;
				}).orElse(""));
			} else {
				doLogUser(udpConnection, Userlog.UserlogType.SuspiciousActivity, string, "Type5");
			}
		}

		return boolean1;
	}

	public static boolean checkTarget(UdpConnection udpConnection, Player player, String string) {
		IsoPlayer player2 = player.getPlayer();
		boolean boolean1 = Arrays.stream(udpConnection.players).anyMatch((playerx)->{
    return playerx.getOnlineID() == player2.getOnlineID();
});
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType6.getValue() && checkUser(udpConnection)) {
			doKickUser(udpConnection, string, "Type6", player2.getUsername());
		}

		return boolean1;
	}

	public static boolean checkSafehouseAuth(UdpConnection udpConnection, String string, String string2) {
		boolean boolean1 = StringUtils.isNullOrEmpty(string) || string.equals(udpConnection.username) || udpConnection.accessLevel >= 16;
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType7.getValue() && checkUser(udpConnection)) {
			doKickUser(udpConnection, string2, "Type7", string);
		}

		return boolean1;
	}

	public static boolean checkShortDistance(UdpConnection udpConnection, IPositional iPositional, IPositional iPositional2, String string) {
		float float1 = IsoUtils.DistanceTo(iPositional2.getX(), iPositional2.getY(), iPositional.getX(), iPositional.getY());
		boolean boolean1 = (double)float1 <= 10.0 * ServerOptions.instance.AntiCheatProtectionType3ThresholdMultiplier.getValue();
		if (!boolean1 && ServerOptions.instance.AntiCheatProtectionType3.getValue() && checkUser(udpConnection)) {
			doKickUser(udpConnection, string, "Type3", String.valueOf(float1));
		}

		return boolean1;
	}

	private static boolean isUntouchable(UdpConnection udpConnection) {
		return !udpConnection.isFullyConnected() || PlayerType.isPrivileged(udpConnection.accessLevel) || Arrays.stream(udpConnection.players).filter(Objects::nonNull).anyMatch(IsoGameCharacter::isGodMod);
	}

	public static boolean checkUser(UdpConnection udpConnection) {
		return doAntiCheatProtection() && !isUntouchable(udpConnection);
	}

	public boolean checkSuspiciousActivity(String string) {
		if (this.suspiciousActivityCounter <= 4) {
			++this.suspiciousActivityCounter;
			this.suspiciousActivityDescription = String.format("player=\"%s\" type=\"%s\"", this.connection.username, string);
			DebugLog.Multiplayer.noise("SuspiciousActivity increase: counter=%d %s", this.suspiciousActivityCounter, this.suspiciousActivityDescription);
		}

		return this.suspiciousActivityCounter > 4;
	}

	public void updateSuspiciousActivityCounter() {
		if (this.suspiciousActivityCounter > 0) {
			--this.suspiciousActivityCounter;
			DebugLog.Multiplayer.warn("SuspiciousActivity decrease: counter=%d %s", this.suspiciousActivityCounter, this.suspiciousActivityDescription);
		} else {
			this.suspiciousActivityCounter = 0;
		}
	}

	public static void doLogUser(UdpConnection udpConnection, Userlog.UserlogType userlogType, String string, String string2) {
		long long1 = System.currentTimeMillis();
		DebugLog.Multiplayer.warn("Log: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, string, string2);
		if (long1 > udpConnection.lastUnauthorizedPacket) {
			udpConnection.lastUnauthorizedPacket = long1 + 1000L;
			ServerWorldDatabase.instance.addUserlog(udpConnection.username, userlogType, string, "AntiCheat" + string2, 1);
		}
	}

	public static void doKickUser(UdpConnection udpConnection, String string, String string2, String string3) {
		ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Kicked, string, "AntiCheat" + string2, 1);
		DebugLog.Multiplayer.warn("Kick: player=\"%s\" type=\"%s\" issuer=\"%s\" description=\"%s\"", udpConnection.username, string, string2, string3);
		GameServer.kick(udpConnection, "UI_Policy_Kick", string2);
		udpConnection.forceDisconnect(string);
		GameServer.addDisconnect(udpConnection);
	}

	public static void doBanUser(UdpConnection udpConnection, String string, String string2) throws Exception {
		ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.Banned, string, "AntiCheat" + string2, 1);
		DebugLog.Multiplayer.warn("Ban: player=\"%s\" type=\"%s\" issuer=\"%s\"", udpConnection.username, string, string2);
		ServerWorldDatabase.instance.banUser(udpConnection.username, true);
		if (SteamUtils.isSteamModeEnabled()) {
			String string3 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
			ServerWorldDatabase.instance.banSteamID(string3, string, true);
		} else {
			ServerWorldDatabase.instance.banIp(udpConnection.ip, udpConnection.username, string, true);
		}

		GameServer.kick(udpConnection, "UI_Policy_Ban", string2);
		udpConnection.forceDisconnect(string);
		GameServer.addDisconnect(udpConnection);
	}

	private static boolean checkPVP(IsoGameCharacter gameCharacter, IsoMovingObject movingObject) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		IsoPlayer player2 = (IsoPlayer)Type.tryCastTo(movingObject, IsoPlayer.class);
		if (player2 != null) {
			if (player2.isGodMod() || !ServerOptions.instance.PVP.getValue() || ServerOptions.instance.SafetySystem.getValue() && gameCharacter.getSafety().isEnabled() && ((IsoGameCharacter)movingObject).getSafety().isEnabled()) {
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

	public static boolean doAntiCheatProtection() {
		return !GameServer.bCoop && (!Core.bDebug || SystemDisabler.doKickInDebug);
	}

	public String getDescription() {
		StringBuilder stringBuilder = new StringBuilder("Recipes CRC details");
		if (GameServer.bServer) {
			Set set = (Set)this.details.entrySet().stream().filter((stringBuilderx)->{
				return this.detailsFromClient.get(stringBuilderx.getKey()) != null && ((PacketValidator.RecipeDetails)this.detailsFromClient.get(stringBuilderx.getKey())).crc == ((PacketValidator.RecipeDetails)stringBuilderx.getValue()).crc;
			}).map(Entry::getKey).collect(Collectors.toSet());

			set.forEach((stringBuilderx)->{
				this.detailsFromClient.remove(stringBuilderx);
				this.details.remove(stringBuilderx);
			});

			stringBuilder.append("\nServer start size=").append(this.details.size());
			this.details.values().forEach((stringBuilderx)->{
				stringBuilder.append(stringBuilderx.getDescription());
			});

			stringBuilder.append("\nServer end\nClient start size=").append(this.detailsFromClient.size());
			this.detailsFromClient.values().forEach((stringBuilderx)->{
				stringBuilder.append(stringBuilderx.getDescription());
			});

			stringBuilder.append("\nClient end");
		}

		return stringBuilder.toString();
	}

	public static class RecipeDetails {
		private final String name;
		private final long crc;
		private final int timeToMake;
		private final ArrayList skills = new ArrayList();
		private final ArrayList items = new ArrayList();
		private final String type;
		private final String module;
		private final int count;

		public long getCRC() {
			return this.crc;
		}

		public RecipeDetails(String string, long long1, int int1, ArrayList arrayList, ArrayList arrayList2, String string2, String string3, int int2) {
			this.name = string;
			this.crc = long1;
			this.timeToMake = int1;
			this.type = string2;
			this.module = string3;
			this.count = int2;
			Iterator iterator;
			if (arrayList != null) {
				iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					Recipe.RequiredSkill requiredSkill = (Recipe.RequiredSkill)iterator.next();
					this.skills.add(new PacketValidator.RecipeDetails.Skill(requiredSkill.getPerk().getName(), requiredSkill.getLevel()));
				}
			}

			iterator = arrayList2.iterator();
			while (iterator.hasNext()) {
				Recipe.Source source = (Recipe.Source)iterator.next();
				this.items.addAll(source.getItems());
			}
		}

		public RecipeDetails(ByteBuffer byteBuffer) {
			this.name = GameWindow.ReadString(byteBuffer);
			this.crc = byteBuffer.getLong();
			this.timeToMake = byteBuffer.getInt();
			this.type = GameWindow.ReadString(byteBuffer);
			this.module = GameWindow.ReadString(byteBuffer);
			this.count = byteBuffer.getInt();
			int int1 = byteBuffer.getInt();
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				this.items.add(GameWindow.ReadString(byteBuffer));
			}

			int2 = byteBuffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				this.skills.add(new PacketValidator.RecipeDetails.Skill(GameWindow.ReadString(byteBuffer), byteBuffer.getInt()));
			}
		}

		public void write(ByteBufferWriter byteBufferWriter) {
			byteBufferWriter.putUTF(this.name);
			byteBufferWriter.putLong(this.crc);
			byteBufferWriter.putInt(this.timeToMake);
			byteBufferWriter.putUTF(this.type);
			byteBufferWriter.putUTF(this.module);
			byteBufferWriter.putInt(this.count);
			byteBufferWriter.putInt(this.items.size());
			Iterator iterator = this.items.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				byteBufferWriter.putUTF(string);
			}

			byteBufferWriter.putInt(this.skills.size());
			iterator = this.skills.iterator();
			while (iterator.hasNext()) {
				PacketValidator.RecipeDetails.Skill skill = (PacketValidator.RecipeDetails.Skill)iterator.next();
				byteBufferWriter.putUTF(skill.name);
				byteBufferWriter.putInt(skill.value);
			}
		}

		public String getDescription() {
			String string = this.name;
			return "\n\tRecipe: name=\"" + string + "\" crc=" + this.crc + " time=" + this.timeToMake + " type=\"" + this.type + "\" module=\"" + this.module + "\" count=" + this.count + " items=[" + String.join(",", this.items) + "] skills=[" + (String)this.skills.stream().map((var0)->{
				return "\"" + var0.name + "\": " + var0.value;
			}).collect(Collectors.joining(",")) + "]";
		}

		public static class Skill {
			private final String name;
			private final int value;

			public Skill(String string, int int1) {
				this.name = string;
				this.value = int1;
			}
		}
	}

	public static enum CheckState {

		None,
		Sent,
		Success;

		private static PacketValidator.CheckState[] $values() {
			return new PacketValidator.CheckState[]{None, Sent, Success};
		}
	}
}
