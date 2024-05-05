package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;


public class PacketTypes {
	public static final short ContainerDeadBody = 0;
	public static final short ContainerWorldObject = 1;
	public static final short ContainerObject = 2;
	public static final short ContainerVehicle = 3;
	public static final Map packetTypes = new TreeMap();
	public static final KahluaTable packetCountTable;

	public static void doPingPacket(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(28);
	}

	public static KahluaTable getPacketCounts(int int1) {
		packetCountTable.wipe();
		if (GameClient.bClient) {
			Iterator iterator = packetTypes.values().iterator();
			while (iterator.hasNext()) {
				PacketTypes.PacketType packetType = (PacketTypes.PacketType)iterator.next();
				if (int1 == 1) {
					packetCountTable.rawset(String.format("%03d-%s", packetType.id, packetType.name()), String.valueOf(packetType.serverPacketCount));
				} else {
					packetCountTable.rawset(String.format("%03d-%s", packetType.id, packetType.name()), String.valueOf(packetType.clientPacketCount));
				}
			}
		}

		return packetCountTable;
	}

	static  {
		packetCountTable = LuaManager.platform.newTable();
	PacketTypes.PacketType[] var0 = PacketTypes.PacketType.values();
	int var1 = var0.length;
	for (int var2 = 0; var2 < var1; ++var2) {
		PacketTypes.PacketType var3 = var0[var2];
		PacketTypes.PacketType var4 = (PacketTypes.PacketType)packetTypes.put(var3.getId(), var3);
		if (var4 != null) {
			DebugLog.Multiplayer.error(String.format("PacketType: duplicate \"%s\" \"%s\" id=%d", var4.name(), var3.name(), var3.getId()));
		}
	}
	}

	public static enum PacketType {

		Validate,
		Login,
		HumanVisual,
		KeepAlive,
		Vehicles,
		PlayerConnect,
		VehiclesUnreliable,
		VehicleAuthorization,
		MetaGrid,
		Helicopter,
		SyncIsoObject,
		PlayerTimeout,
		ServerMap,
		PassengerMap,
		AddItemToMap,
		SentChunk,
		SyncClock,
		AddInventoryItemToContainer,
		RemoveInventoryItemFromContainer,
		RemoveItemFromSquare,
		RequestLargeAreaZip,
		Equip,
		HitCharacter,
		AddCoopPlayer,
		WeaponHit,
		KillZombie,
		SandboxOptions,
		SmashWindow,
		PlayerDeath,
		RequestZipList,
		ItemStats,
		NotRequiredInZip,
		RequestData,
		GlobalObjects,
		ZombieDeath,
		AccessDenied,
		PlayerDamage,
		Bandage,
		EatFood,
		RequestItemsForContainer,
		Drink,
		SyncAlarmClock,
		PacketCounts,
		SendModData,
		RemoveContestedItemsFromInventory,
		ScoreboardUpdate,
		ReceiveModData,
		ServerQuit,
		PlaySound,
		WorldSound,
		AddAmbient,
		SyncClothing,
		ClientCommand,
		ObjectModData,
		ObjectChange,
		BloodSplatter,
		ZombieSound,
		ZombieDescriptors,
		SlowFactor,
		Weather,
		WorldMapPlayerPosition,
		RequestPlayerData,
		RemoveCorpseFromMap,
		AddCorpseToMap,
		BecomeCorpse,
		StartFire,
		UpdateItemSprite,
		StartRain,
		StopRain,
		WorldMessage,
		getModData,
		ReceiveCommand,
		ReloadOptions,
		Kicked,
		ExtraInfo,
		AddItemInInventory,
		ChangeSafety,
		Ping,
		WriteLog,
		AddXP,
		UpdateOverlaySprite,
		Checksum,
		ConstructedZone,
		RegisterZone,
		WoundInfection,
		Stitch,
		Disinfect,
		AdditionalPain,
		RemoveGlass,
		Splint,
		RemoveBullet,
		CleanBurn,
		SyncThumpable,
		SyncDoorKey,
		AddXpCommand,
		Teleport,
		RemoveBlood,
		AddExplosiveTrap,
		BodyDamageUpdate,
		SyncSafehouse,
		SledgehammerDestroy,
		StopFire,
		Cataplasm,
		AddAlarm,
		PlaySoundEveryPlayer,
		SyncFurnace,
		SendCustomColor,
		SyncCompost,
		ChangePlayerStats,
		AddXpFromPlayerStatsUI,
		SyncXP,
		PacketTypeShort,
		Userlog,
		AddUserlog,
		RemoveUserlog,
		AddWarningPoint,
		MessageForAdmin,
		WakeUpPlayer,
		SendTransactionID,
		GetDBSchema,
		GetTableResult,
		ExecuteQuery,
		ChangeTextColor,
		SyncNonPvpZone,
		SyncFaction,
		SendFactionInvite,
		AcceptedFactionInvite,
		AddTicket,
		ViewTickets,
		RemoveTicket,
		RequestTrading,
		TradingUIAddItem,
		TradingUIRemoveItem,
		TradingUIUpdateState,
		SendItemListNet,
		ChunkObjectState,
		ReadAnnotedMap,
		RequestInventory,
		SendInventory,
		InvMngReqItem,
		InvMngGetItem,
		InvMngRemoveItem,
		StartPause,
		StopPause,
		TimeSync,
		SyncIsoObjectReq,
		PlayerSave,
		SyncWorldObjectsReq,
		SyncObjects,
		SendPlayerProfile,
		LoadPlayerProfile,
		SpawnRegion,
		PlayerDamageFromCarCrash,
		PlayerAttachedItem,
		ZombieHelmetFalling,
		AddBrokenGlass,
		SyncPerks,
		SyncWeight,
		SyncInjuries,
		SyncEquippedRadioFreq,
		InitPlayerChat,
		PlayerJoinChat,
		PlayerLeaveChat,
		ChatMessageFromPlayer,
		ChatMessageToPlayer,
		PlayerStartPMChat,
		AddChatTab,
		RemoveChatTab,
		PlayerConnectedToChat,
		PlayerNotFound,
		SendSafehouseInvite,
		AcceptedSafehouseInvite,
		ClimateManagerPacket,
		IsoRegionServerPacket,
		IsoRegionClientRequestFullUpdate,
		EventPacket,
		Statistic,
		StatisticRequest,
		PlayerUpdateReliable,
		ActionPacket,
		ZombieControl,
		PlayWorldSound,
		StopSound,
		PlayerUpdate,
		ZombieSimulation,
		PingFromClient,
		ZombieSimulationReliable,
		EatBody,
		Thump,
		SyncRadioData,
		LoginQueueRequest2,
		LoginQueueDone2,
		ItemTransaction,
		KickOutOfSafehouse,
		SneezeCough,
		BurnCorpse,
		WaveSignal,
		PlayerListensChannel,
		RadioServerData,
		RadioDeviceDataState,
		SyncCustomLightSettings,
		ReplaceOnCooked,
		PlayerDataRequest,
		GlobalModData,
		GlobalModDataRequest,
		RadioPostSilenceEvent,
		id,
		requiredAccessLevel,
		unauthorizedPacketPolicy,
		PacketPriority,
		PacketReliability,
		OrderingChannel,
		serverProcess,
		mainLoopHandlePacketInternal,
		gameLoadingDealWithNetData,
		incomePackets,
		outcomePackets,
		incomeBytes,
		outcomeBytes,
		clientPacketCount,
		serverPacketCount;

		private PacketType(int int1, int int2, int int3, int int4, PacketTypes.PacketAuthorization.Policy policy, PacketTypes.CallbackServerProcess callbackServerProcess, PacketTypes.CallbackClientProcess callbackClientProcess, PacketTypes.CallbackClientProcess callbackClientProcess2) {
			this(int1, int2, int3, (byte)0, (byte)int4, policy, callbackServerProcess, callbackClientProcess, callbackClientProcess2);
		}
		private PacketType(int int1, int int2, int int3, byte byte1, byte byte2, PacketTypes.PacketAuthorization.Policy policy, PacketTypes.CallbackServerProcess callbackServerProcess, PacketTypes.CallbackClientProcess callbackClientProcess, PacketTypes.CallbackClientProcess callbackClientProcess2) {
			this.id = (short)int1;
			this.requiredAccessLevel = byte2;
			this.unauthorizedPacketPolicy = policy;
			this.PacketPriority = int2;
			this.PacketReliability = int3;
			this.OrderingChannel = byte1;
			this.serverProcess = callbackServerProcess;
			this.mainLoopHandlePacketInternal = callbackClientProcess;
			this.gameLoadingDealWithNetData = callbackClientProcess2;
			this.resetStatistics();
		}
		public void resetStatistics() {
			this.incomePackets = 0;
			this.outcomePackets = 0;
			this.incomeBytes = 0;
			this.outcomeBytes = 0;
			this.clientPacketCount = 0L;
			this.serverPacketCount = 0L;
		}
		public void send(UdpConnection udpConnection) {
			udpConnection.endPacket(this.PacketPriority, this.PacketReliability, this.OrderingChannel);
			DebugLog.Packet.noise("type=%s username=%s connection=%d ip=%s size=%d", this.name(), udpConnection.username, udpConnection.getConnectedGUID(), udpConnection.ip, udpConnection.getBufferPosition());
		}
		public void doPacket(ByteBufferWriter byteBufferWriter) {
			byteBufferWriter.putByte((byte)-122);
			byteBufferWriter.putShort(this.getId());
		}
		public short getId() {
			return this.id;
		}
		public void onServerPacket(ByteBuffer byteBuffer, UdpConnection udpConnection) throws Exception {
			if (PacketTypes.PacketAuthorization.isAuthorized(udpConnection, this)) {
				DebugLog.Packet.noise("type=%s username=%s connection=%d ip=%s", this.name(), udpConnection.username, udpConnection.getConnectedGUID(), udpConnection.ip);
				this.serverProcess.call(byteBuffer, udpConnection, this.getId());
			}
		}
		public void onMainLoopHandlePacketInternal(ByteBuffer byteBuffer) throws IOException {
			DebugLog.Packet.noise("type=%s", this.name());
			this.mainLoopHandlePacketInternal.call(byteBuffer, this.getId());
		}
		public boolean onGameLoadingDealWithNetData(ByteBuffer byteBuffer) {
			DebugLog.Packet.noise("type=%s", this.name());
			if (this.gameLoadingDealWithNetData == null) {
				DebugLog.Network.noise("Delay processing packet of type %s while loading game", this.name());
				return false;
			} else {
				try {
					this.gameLoadingDealWithNetData.call(byteBuffer, this.getId());
					return true;
				} catch (Exception exception) {
					return false;
				}
			}
		}
		public void onUnauthorized(UdpConnection udpConnection) {
			DebugLog.Multiplayer.warn(String.format("On unauthorized packet %s (%d) was received from user=\"%s\" (%d) ip %s %s", this.name(), this.requiredAccessLevel, udpConnection.username, udpConnection.accessLevel, udpConnection.ip, SteamUtils.isSteamModeEnabled() ? udpConnection.steamID : ""));
			try {
				this.unauthorizedPacketPolicy.apply(udpConnection, this.name());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		private static PacketTypes.PacketType[] $values() {
			return new PacketTypes.PacketType[]{Validate, Login, HumanVisual, KeepAlive, Vehicles, PlayerConnect, VehiclesUnreliable, VehicleAuthorization, MetaGrid, Helicopter, SyncIsoObject, PlayerTimeout, ServerMap, PassengerMap, AddItemToMap, SentChunk, SyncClock, AddInventoryItemToContainer, RemoveInventoryItemFromContainer, RemoveItemFromSquare, RequestLargeAreaZip, Equip, HitCharacter, AddCoopPlayer, WeaponHit, KillZombie, SandboxOptions, SmashWindow, PlayerDeath, RequestZipList, ItemStats, NotRequiredInZip, RequestData, GlobalObjects, ZombieDeath, AccessDenied, PlayerDamage, Bandage, EatFood, RequestItemsForContainer, Drink, SyncAlarmClock, PacketCounts, SendModData, RemoveContestedItemsFromInventory, ScoreboardUpdate, ReceiveModData, ServerQuit, PlaySound, WorldSound, AddAmbient, SyncClothing, ClientCommand, ObjectModData, ObjectChange, BloodSplatter, ZombieSound, ZombieDescriptors, SlowFactor, Weather, WorldMapPlayerPosition, RequestPlayerData, RemoveCorpseFromMap, AddCorpseToMap, BecomeCorpse, StartFire, UpdateItemSprite, StartRain, StopRain, WorldMessage, getModData, ReceiveCommand, ReloadOptions, Kicked, ExtraInfo, AddItemInInventory, ChangeSafety, Ping, WriteLog, AddXP, UpdateOverlaySprite, Checksum, ConstructedZone, RegisterZone, WoundInfection, Stitch, Disinfect, AdditionalPain, RemoveGlass, Splint, RemoveBullet, CleanBurn, SyncThumpable, SyncDoorKey, AddXpCommand, Teleport, RemoveBlood, AddExplosiveTrap, BodyDamageUpdate, SyncSafehouse, SledgehammerDestroy, StopFire, Cataplasm, AddAlarm, PlaySoundEveryPlayer, SyncFurnace, SendCustomColor, SyncCompost, ChangePlayerStats, AddXpFromPlayerStatsUI, SyncXP, PacketTypeShort, Userlog, AddUserlog, RemoveUserlog, AddWarningPoint, MessageForAdmin, WakeUpPlayer, SendTransactionID, GetDBSchema, GetTableResult, ExecuteQuery, ChangeTextColor, SyncNonPvpZone, SyncFaction, SendFactionInvite, AcceptedFactionInvite, AddTicket, ViewTickets, RemoveTicket, RequestTrading, TradingUIAddItem, TradingUIRemoveItem, TradingUIUpdateState, SendItemListNet, ChunkObjectState, ReadAnnotedMap, RequestInventory, SendInventory, InvMngReqItem, InvMngGetItem, InvMngRemoveItem, StartPause, StopPause, TimeSync, SyncIsoObjectReq, PlayerSave, SyncWorldObjectsReq, SyncObjects, SendPlayerProfile, LoadPlayerProfile, SpawnRegion, PlayerDamageFromCarCrash, PlayerAttachedItem, ZombieHelmetFalling, AddBrokenGlass, SyncPerks, SyncWeight, SyncInjuries, SyncEquippedRadioFreq, InitPlayerChat, PlayerJoinChat, PlayerLeaveChat, ChatMessageFromPlayer, ChatMessageToPlayer, PlayerStartPMChat, AddChatTab, RemoveChatTab, PlayerConnectedToChat, PlayerNotFound, SendSafehouseInvite, AcceptedSafehouseInvite, ClimateManagerPacket, IsoRegionServerPacket, IsoRegionClientRequestFullUpdate, EventPacket, Statistic, StatisticRequest, PlayerUpdateReliable, ActionPacket, ZombieControl, PlayWorldSound, StopSound, PlayerUpdate, ZombieSimulation, PingFromClient, ZombieSimulationReliable, EatBody, Thump, SyncRadioData, LoginQueueRequest2, LoginQueueDone2, ItemTransaction, KickOutOfSafehouse, SneezeCough, BurnCorpse, WaveSignal, PlayerListensChannel, RadioServerData, RadioDeviceDataState, SyncCustomLightSettings, ReplaceOnCooked, PlayerDataRequest, GlobalModData, GlobalModDataRequest, RadioPostSilenceEvent};
		}
	}

	public interface CallbackClientProcess {
		void call(ByteBuffer byteBuffer, short short1) throws IOException;
	}

	public interface CallbackServerProcess {

		void call(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws Exception;
	}

	private static class PacketAuthorization {

		private static void unauthorizedPacketPolicyLogUser(UdpConnection udpConnection, String string) {
			if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(udpConnection)) {
				PacketValidator.doLogUser(udpConnection, Userlog.UserlogType.UnauthorizedPacket, string, "Type8");
			}
		}

		private static void unauthorizedPacketPolicyKickUser(UdpConnection udpConnection, String string) {
			if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(udpConnection)) {
				PacketValidator.doKickUser(udpConnection, string, "Type8", (String)null);
			}
		}

		private static void unauthorizedPacketPolicyBanUser(UdpConnection udpConnection, String string) throws Exception {
			if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(udpConnection)) {
				PacketValidator.doBanUser(udpConnection, string, "Type8");
			}
		}

		private static boolean isAuthorized(UdpConnection udpConnection, PacketTypes.PacketType packetType) throws Exception {
			boolean boolean1 = (udpConnection.accessLevel & packetType.requiredAccessLevel) != 0;
			if ((!boolean1 || packetType.serverProcess == null) && (!Core.bDebug || SystemDisabler.doKickInDebug)) {
				DebugLog.Multiplayer.warn(String.format("Unauthorized packet %s (%s) was received from user=\"%s\" (%s) ip %s %s", packetType.name(), PlayerType.toString(packetType.requiredAccessLevel), udpConnection.username, PlayerType.toString(udpConnection.accessLevel), udpConnection.ip, SteamUtils.isSteamModeEnabled() ? udpConnection.steamID : ""));
				packetType.unauthorizedPacketPolicy.apply(udpConnection, packetType.name());
			}

			return boolean1;
		}

		public static enum Policy {

			Log,
			Kick,
			Ban,
			policy;

			private Policy(PacketTypes.PacketAuthorization.UnauthorizedPacketPolicy unauthorizedPacketPolicy) {
				this.policy = unauthorizedPacketPolicy;
			}
			private void apply(UdpConnection udpConnection, String string) throws Exception {
				this.policy.call(udpConnection, string);
			}
			private static PacketTypes.PacketAuthorization.Policy[] $values() {
				return new PacketTypes.PacketAuthorization.Policy[]{Log, Kick, Ban};
			}
		}

		public interface UnauthorizedPacketPolicy {
			void call(UdpConnection udpConnection, String string) throws Exception;
		}
	}
}
