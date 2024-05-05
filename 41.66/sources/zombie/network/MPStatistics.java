package zombie.network;

import com.sun.management.OperatingSystemMXBean;
import java.lang.invoke.SerializedLambda;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.management.NotificationEmitter;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MovingObjectUpdateScheduler;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManagerData;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.ZNetStatistics;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.iso.IsoWorld;
import zombie.iso.WorldStreamer;
import zombie.popman.NetworkZombieManager;
import zombie.popman.NetworkZombieSimulator;
import zombie.util.StringUtils;


public class MPStatistics {
	private static final float MEM_USAGE_THRESHOLD = 0.95F;
	private static final long REQUEST_TIMEOUT = 10000L;
	private static final long STATISTICS_INTERVAL = 2000L;
	private static final long PING_INTERVAL = 1000L;
	private static final long PING_PERIOD = 10000L;
	private static final long PING_LIMIT_PERIOD = 60000L;
	private static final long PING_INTERVAL_COUNT = 60L;
	private static final long PING_LIMIT_COUNT = 20L;
	private static final long PING_LOG_COUNT = 120L;
	private static final long MAX_PING_TO_SUM = 1000L;
	private static final KahluaTable statsTable;
	private static final KahluaTable statusTable;
	private static final UpdateLimit ulRequestTimeout;
	private static final UpdateLimit ulStatistics;
	private static final UpdateLimit ulPing;
	private static boolean serverStatisticsEnabled;
	private static int serverPlayers;
	private static int clientPlayers;
	private static int clientLastPing;
	private static int clientAvgPing;
	private static int clientMinPing;
	private static String clientVOIPSource;
	private static String clientVOIPFreq;
	private static long clientVOIPRX;
	private static long clientVOIPTX;
	private static long serverVOIPRX;
	private static long serverVOIPTX;
	private static int serverWaitingRequests;
	private static int clientSentRequests;
	private static int requested1;
	private static int requested2;
	private static int pending1;
	private static int pending2;
	private static long serverCPUCores;
	private static long serverCPULoad;
	private static long serverMemMax;
	private static long serverMemFree;
	private static long serverMemTotal;
	private static long serverMemUsed;
	private static long serverRX;
	private static long serverTX;
	private static long serverResent;
	private static double serverLoss;
	private static float serverFPS;
	private static long serverNetworkingUpdates;
	private static long serverNetworkingFPS;
	private static String serverRevision;
	private static long clientCPUCores;
	private static long clientCPULoad;
	private static long clientMemMax;
	private static long clientMemFree;
	private static long clientMemTotal;
	private static long clientMemUsed;
	private static long clientRX;
	private static long clientTX;
	private static long clientResent;
	private static double clientLoss;
	private static float clientFPS;
	private static int serverStoredChunks;
	private static int serverRelevantChunks;
	private static int serverZombiesTotal;
	private static int serverZombiesLoaded;
	private static int serverZombiesSimulated;
	private static int serverZombiesCulled;
	private static int serverZombiesAuthorized;
	private static int serverZombiesUnauthorized;
	private static int serverZombiesReusable;
	private static int serverZombiesUpdated;
	private static int clientStoredChunks;
	private static int clientRelevantChunks;
	private static int clientZombiesTotal;
	private static int clientZombiesLoaded;
	private static int clientZombiesSimulated;
	private static int clientZombiesCulled;
	private static int clientZombiesAuthorized;
	private static int clientZombiesUnauthorized;
	private static int clientZombiesReusable;
	private static int clientZombiesUpdated;
	private static long zombieUpdates;
	private static long serverMinPing;
	private static long serverMaxPing;
	private static long serverAvgPing;
	private static long serverLastPing;
	private static long serverLossPing;
	private static long serverHandledPingPeriodStart;
	private static int serverHandledPingPacketIndex;
	private static final ArrayList serverHandledPingHistory;
	private static final HashSet serverHandledLossPingHistory;
	static long pingIntervalCount;
	static long pingLimitCount;
	static long maxPingToSum;

	private static boolean isClientStatisticsEnabled() {
		boolean boolean1 = false;
		IsoPlayer[] playerArray = IsoPlayer.players;
		int int1 = playerArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoPlayer player = playerArray[int2];
			if (player != null && player.isShowMPInfos()) {
				boolean1 = true;
				break;
			}
		}

		return boolean1;
	}

	private static void getClientZombieStatistics() {
		int int1 = (int)Math.max(MovingObjectUpdateScheduler.instance.getFrameCounter() - zombieUpdates, 1L);
		clientZombiesTotal = GameClient.IDToZombieMap.values().length;
		clientZombiesLoaded = IsoWorld.instance.getCell().getZombieList().size();
		clientZombiesSimulated = clientZombiesUpdated / int1;
		clientZombiesAuthorized = NetworkZombieSimulator.getInstance().getAuthorizedZombieCount();
		clientZombiesUnauthorized = NetworkZombieSimulator.getInstance().getUnauthorizedZombieCount();
		clientZombiesReusable = VirtualZombieManager.instance.reusableZombiesSize();
		clientZombiesCulled = 0;
		clientZombiesUpdated = 0;
		zombieUpdates = MovingObjectUpdateScheduler.instance.getFrameCounter();
		serverZombiesCulled = 0;
	}

	private static void getServerZombieStatistics() {
		int int1 = (int)Math.max(MovingObjectUpdateScheduler.instance.getFrameCounter() - zombieUpdates, 1L);
		serverZombiesTotal = ServerMap.instance.ZombieMap.size();
		serverZombiesLoaded = IsoWorld.instance.getCell().getZombieList().size();
		serverZombiesSimulated = serverZombiesUpdated / int1;
		serverZombiesAuthorized = 0;
		serverZombiesUnauthorized = NetworkZombieManager.getInstance().getUnauthorizedZombieCount();
		serverZombiesReusable = VirtualZombieManager.instance.reusableZombiesSize();
		serverZombiesCulled = 0;
		serverZombiesUpdated = 0;
		zombieUpdates = MovingObjectUpdateScheduler.instance.getFrameCounter();
	}

	private static void getClientChunkStatistics() {
		try {
			WorldStreamer.instance.getStatistics();
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "Error getting chunk statistics", LogSeverity.Error);
		}
	}

	public static void countChunkRequests(int int1, int int2, int int3, int int4, int int5) {
		clientSentRequests = int1;
		requested1 = int2;
		requested2 = int3;
		pending1 = int4;
		pending2 = int5;
	}

	private static void resetStatistic() {
		if (GameClient.bClient) {
			GameClient.connection.netStatistics = null;
		} else {
			UdpConnection udpConnection;
			if (GameServer.bServer) {
				for (Iterator iterator = GameServer.udpEngine.connections.iterator(); iterator.hasNext(); udpConnection.netStatistics = null) {
					udpConnection = (UdpConnection)iterator.next();
				}
			}
		}

		serverPlayers = 0;
		clientPlayers = 0;
		clientVOIPSource = "";
		clientVOIPFreq = "";
		clientVOIPRX = 0L;
		clientVOIPTX = 0L;
		serverVOIPRX = 0L;
		serverVOIPTX = 0L;
		serverCPUCores = 0L;
		serverCPULoad = 0L;
		serverRX = 0L;
		serverTX = 0L;
		serverResent = 0L;
		serverLoss = 0.0;
		serverFPS = 0.0F;
		serverNetworkingFPS = 0L;
		serverMemMax = 0L;
		serverMemFree = 0L;
		serverMemTotal = 0L;
		serverMemUsed = 0L;
		clientCPUCores = 0L;
		clientCPULoad = 0L;
		clientRX = 0L;
		clientTX = 0L;
		clientResent = 0L;
		clientLoss = 0.0;
		clientFPS = 0.0F;
		clientMemMax = 0L;
		clientMemFree = 0L;
		clientMemTotal = 0L;
		clientMemUsed = 0L;
		serverZombiesTotal = 0;
		serverZombiesLoaded = 0;
		serverZombiesSimulated = 0;
		serverZombiesCulled = 0;
		serverZombiesAuthorized = 0;
		serverZombiesUnauthorized = 0;
		serverZombiesReusable = 0;
		serverZombiesUpdated = 0;
		clientZombiesTotal = 0;
		clientZombiesLoaded = 0;
		clientZombiesSimulated = 0;
		clientZombiesCulled = 0;
		clientZombiesAuthorized = 0;
		clientZombiesUnauthorized = 0;
		clientZombiesReusable = 0;
		clientZombiesUpdated = 0;
		serverWaitingRequests = 0;
		clientSentRequests = 0;
		requested1 = 0;
		requested2 = 0;
		pending1 = 0;
		pending2 = 0;
	}

	private static void getClientStatistics() {
		try {
			clientVOIPRX = 0L;
			clientVOIPTX = 0L;
			clientRX = 0L;
			clientTX = 0L;
			clientResent = 0L;
			clientLoss = 0.0;
			ZNetStatistics zNetStatistics = GameClient.connection.getStatistics();
			if (zNetStatistics != null) {
				clientRX = zNetStatistics.lastActualBytesReceived / 1000L;
				clientTX = zNetStatistics.lastActualBytesSent / 1000L;
				clientResent = zNetStatistics.lastUserMessageBytesResent / 1000L;
				clientLoss = zNetStatistics.packetlossLastSecond / 1000.0;
			}

			long[] longArray = new long[]{-1L, -1L};
			if (RakVoice.GetChannelStatistics(GameClient.connection.getConnectedGUID(), longArray)) {
				clientVOIPRX = longArray[0] / 2000L;
				clientVOIPTX = longArray[1] / 2000L;
			}

			clientFPS = 60.0F / GameTime.instance.FPSMultiplier;
			clientCPUCores = (long)ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
			clientCPULoad = (long)(((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100.0);
			clientMemMax = Runtime.getRuntime().maxMemory() / 1000L / 1000L;
			clientMemFree = Runtime.getRuntime().freeMemory() / 1000L / 1000L;
			clientMemTotal = Runtime.getRuntime().totalMemory() / 1000L / 1000L;
			clientMemUsed = clientMemTotal - clientMemFree;
			clientPlayers = 0;
			IsoPlayer[] playerArray = IsoPlayer.players;
			int int1 = playerArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoPlayer player = playerArray[int2];
				if (player != null) {
					++clientPlayers;
				}
			}
		} catch (Exception exception) {
		}
	}

	private static void getServerStatistics() {
		try {
			serverVOIPRX = 0L;
			serverVOIPTX = 0L;
			serverRX = 0L;
			serverTX = 0L;
			serverResent = 0L;
			serverLoss = 0.0;
			long[] longArray = new long[]{-1L, -1L};
			Iterator iterator = GameServer.udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection = (UdpConnection)iterator.next();
				ZNetStatistics zNetStatistics = udpConnection.getStatistics();
				if (zNetStatistics != null) {
					serverRX += udpConnection.netStatistics.lastActualBytesReceived;
					serverTX += udpConnection.netStatistics.lastActualBytesSent;
					serverResent += udpConnection.netStatistics.lastUserMessageBytesResent;
					serverLoss += udpConnection.netStatistics.packetlossLastSecond;
				}

				if (RakVoice.GetChannelStatistics(udpConnection.getConnectedGUID(), longArray)) {
					serverVOIPRX += longArray[0];
					serverVOIPTX += longArray[1];
				}
			}

			serverRX /= 1000L;
			serverTX /= 1000L;
			serverResent /= 1000L;
			serverLoss /= 1000.0;
			serverVOIPRX /= 2000L;
			serverVOIPTX /= 2000L;
			serverFPS = 60.0F / GameTime.instance.FPSMultiplier;
			serverCPUCores = (long)ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
			serverCPULoad = (long)(((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100.0);
			serverNetworkingFPS = 1000L * serverNetworkingUpdates / 2000L;
			serverNetworkingUpdates = 0L;
			serverMemMax = Runtime.getRuntime().maxMemory() / 1000L / 1000L;
			serverMemFree = Runtime.getRuntime().freeMemory() / 1000L / 1000L;
			serverMemTotal = Runtime.getRuntime().totalMemory() / 1000L / 1000L;
			serverMemUsed = serverMemTotal - serverMemFree;
			serverPlayers = GameServer.IDToPlayerMap.size();
		} catch (Exception exception) {
		}
	}

	private static void resetPingCounters() {
		clientLastPing = -1;
		clientAvgPing = -1;
		clientMinPing = -1;
	}

	private static void getPing(UdpConnection udpConnection) {
		try {
			if (udpConnection != null) {
				clientLastPing = udpConnection.getLastPing();
				clientAvgPing = udpConnection.getAveragePing();
				clientMinPing = udpConnection.getLowestPing();
			}
		} catch (Exception exception) {
		}
	}

	static long checkLatest(UdpConnection udpConnection, long long1) {
		if ((long)udpConnection.pingHistory.size() >= pingIntervalCount) {
			long long2 = udpConnection.pingHistory.stream().limit(pingIntervalCount).filter((var2)->{
				return var2 > long1;
			}).count();

			if (long2 >= pingLimitCount) {
				return (long)Math.ceil((double)((float)udpConnection.pingHistory.stream().limit(pingIntervalCount).mapToLong((udpConnectionx)->{
					return Math.min(maxPingToSum, udpConnectionx);
				}).sum() / (float)pingIntervalCount));
			}
		}

		return 0L;
	}

	private static void limitPing() {
		int int1 = ServerOptions.instance.PingLimit.getValue();
		Iterator iterator = GameServer.udpEngine.connections.iterator();
		while (iterator.hasNext()) {
			UdpConnection udpConnection = (UdpConnection)iterator.next();
			serverAvgPing = (long)udpConnection.getAveragePing();
			serverLastPing = (long)udpConnection.getLastPing();
			udpConnection.pingHistory.addFirst(serverLastPing);
			long long1 = checkLatest(udpConnection, (long)int1);
			if (doKick(udpConnection, long1)) {
				GameServer.kick(udpConnection, "UI_Policy_Kick", "UI_OnConnectFailed_Ping");
				udpConnection.forceDisconnect("kick-ping-limit");
				GameServer.addDisconnect(udpConnection);
				LoggerManager.getLogger("kick").write(String.format("Kick: player=\"%s\" type=\"%s\"", udpConnection.username, "UI_OnConnectFailed_Ping"));
				LoggerManager.getLogger("kick").write(String.format("Ping: limit=%d/%d average-%d=%d", int1, pingLimitCount, pingIntervalCount, long1));
				LoggerManager.getLogger("kick").write(String.format("Ping: last-%d: %s", 120L, udpConnection.pingHistory.stream().map(Object::toString).collect(Collectors.joining(", "))));
			}

			if ((long)udpConnection.pingHistory.size() > 120L) {
				udpConnection.pingHistory.removeLast();
			}
		}
	}

	public static boolean doKickWhileLoading(UdpConnection udpConnection, long long1) {
		int int1 = ServerOptions.instance.PingLimit.getValue();
		return (double)int1 > ServerOptions.instance.PingLimit.getMin() && long1 > (long)int1 && !udpConnection.preferredInQueue && !PlayerType.isPrivileged(udpConnection.accessLevel);
	}

	public static boolean doKick(UdpConnection udpConnection, long long1) {
		return doKickWhileLoading(udpConnection, long1) && udpConnection.isFullyConnected() && udpConnection.isConnectionGraceIntervalTimeout();
	}

	private static void resetServerHandledPingCounters() {
		serverMinPing = 0L;
		serverMaxPing = 0L;
		serverAvgPing = 0L;
		serverLastPing = 0L;
		serverLossPing = 0L;
		serverHandledPingPeriodStart = 0L;
		serverHandledPingPacketIndex = 0;
		serverHandledPingHistory.clear();
		serverHandledLossPingHistory.clear();
	}

	private static void getServerHandledPing() {
		long long1 = System.currentTimeMillis();
		if ((long)serverHandledPingPacketIndex == 10L) {
			serverMinPing = serverHandledPingHistory.stream().mapToLong((long1x)->{
				return long1x;
			}).min().orElse(0L);

			serverMaxPing = serverHandledPingHistory.stream().mapToLong((long1x)->{
				return long1x;
			}).max().orElse(0L);

			serverAvgPing = (long)serverHandledPingHistory.stream().mapToLong((long1x)->{
				return long1x;
			}).average().orElse(0.0);

			serverHandledPingHistory.clear();
			serverHandledPingPacketIndex = 0;
			int int1 = serverHandledLossPingHistory.size();
			serverHandledLossPingHistory.removeIf((int1x)->{
				return long1 > int1x + 10000L;
			});

			serverLossPing += (long)(int1 - serverHandledLossPingHistory.size());
			serverHandledPingPeriodStart = long1;
		}

		GameClient.sendServerPing(long1);
		if (serverHandledLossPingHistory.size() > 1000) {
			serverHandledLossPingHistory.clear();
		}

		serverHandledLossPingHistory.add(long1);
		++serverHandledPingPacketIndex;
	}

	public static void setVOIPSource(VoiceManagerData.VoiceDataSource voiceDataSource, int int1) {
		clientVOIPSource = VoiceManagerData.VoiceDataSource.Unknown.equals(voiceDataSource) ? "" : voiceDataSource.name();
		clientVOIPFreq = int1 == 0 ? "" : String.valueOf((float)int1 / 1000.0F);
	}

	public static void countServerNetworkingFPS() {
		++serverNetworkingUpdates;
	}

	public static void increaseStoredChunk() {
		if (GameClient.bClient) {
			++clientStoredChunks;
		} else if (GameServer.bServer) {
			++serverStoredChunks;
		}

		decreaseRelevantChunk();
	}

	public static void decreaseStoredChunk() {
		if (GameClient.bClient) {
			--clientStoredChunks;
		} else if (GameServer.bServer) {
			--serverStoredChunks;
		}

		increaseRelevantChunk();
	}

	public static void increaseRelevantChunk() {
		if (GameClient.bClient) {
			++clientRelevantChunks;
		} else if (GameServer.bServer) {
			++serverRelevantChunks;
		}
	}

	public static void decreaseRelevantChunk() {
		if (GameClient.bClient) {
			--clientRelevantChunks;
		} else if (GameServer.bServer) {
			--serverRelevantChunks;
		}
	}

	public static void Init() {
		if (GameServer.bServer || GameClient.bClient) {
			try {
				Iterator iterator = ManagementFactory.getMemoryPoolMXBeans().iterator();
				while (iterator.hasNext()) {
					MemoryPoolMXBean memoryPoolMXBean = (MemoryPoolMXBean)iterator.next();
					if (MemoryType.HEAP.equals(memoryPoolMXBean.getType()) && memoryPoolMXBean.isUsageThresholdSupported()) {
						long long1 = memoryPoolMXBean.getCollectionUsageThreshold();
						String string = System.getProperty("zomboid.thresholdm");
						if (!StringUtils.isNullOrEmpty(string)) {
							long1 = Long.parseLong(string) * 1000000L;
						}

						if (long1 == 0L) {
							long1 = (long)((float)Runtime.getRuntime().maxMemory() * 0.95F);
							memoryPoolMXBean.setUsageThreshold(long1);
						}

						if (long1 > 0L) {
							((NotificationEmitter)ManagementFactory.getMemoryMXBean()).addNotificationListener((memoryPoolMXBeanx,long1x)->{
								DebugLog.Multiplayer.warn("[%s] %s (%d) free=%s", MPStatistics.class.getSimpleName(), "java.management.memory.threshold.exceeded", memoryPoolMXBean.getUsageThresholdCount(), NumberFormat.getNumberInstance().format(Runtime.getRuntime().freeMemory()));
							}, (iteratorx)->{
								return "java.management.memory.threshold.exceeded".equals(iteratorx.getType());
							}, (Object)null);
						}

						DebugLog.log(DebugType.Multiplayer, String.format("[%s] mem usage notification threshold=%s", MPStatistics.class.getSimpleName(), NumberFormat.getNumberInstance().format(long1)));
						break;
					}
				}
			} catch (Exception exception) {
				DebugLog.Multiplayer.printException(exception, String.format("[%s] init error", MPStatistics.class.getSimpleName()), LogSeverity.Error);
			}

			Reset();
		}
	}

	public static void Reset() {
		resetPingCounters();
		resetServerHandledPingCounters();
		resetStatistic();
	}

	public static void Update() {
		if (GameClient.bClient) {
			if (ulPing.Check()) {
				if (!isClientStatisticsEnabled() && !DebugOptions.instance.MultiplayerPing.getValue()) {
					resetPingCounters();
					resetServerHandledPingCounters();
				} else {
					getPing(GameClient.connection);
					if (isClientStatisticsEnabled()) {
						getServerHandledPing();
					} else {
						resetServerHandledPingCounters();
					}
				}
			}

			if (isClientStatisticsEnabled()) {
				if (ulStatistics.Check()) {
					getClientStatistics();
					getClientZombieStatistics();
					getClientChunkStatistics();
				}
			} else {
				resetStatistic();
			}
		} else if (GameServer.bServer) {
			if (ulPing.Check()) {
				limitPing();
			}

			if (ulRequestTimeout.Check()) {
				serverStatisticsEnabled = false;
			}

			if (serverStatisticsEnabled) {
				if (ulStatistics.Check()) {
					getServerStatistics();
					getServerZombieStatistics();
				}
			} else {
				resetStatistic();
			}
		}
	}

	public static void requested() {
		serverStatisticsEnabled = true;
		ulRequestTimeout.Reset(10000L);
	}

	public static void clientZombieCulled() {
		++clientZombiesCulled;
	}

	public static void serverZombieCulled() {
		++serverZombiesCulled;
	}

	public static void clientZombieUpdated() {
		++clientZombiesUpdated;
	}

	public static void serverZombieUpdated() {
		++serverZombiesUpdated;
	}

	public static void write(UdpConnection udpConnection, ByteBuffer byteBuffer) {
		byteBuffer.putLong(serverMemMax);
		byteBuffer.putLong(serverMemFree);
		byteBuffer.putLong(serverMemTotal);
		byteBuffer.putLong(serverMemUsed);
		byteBuffer.putLong(serverCPUCores);
		byteBuffer.putLong(serverCPULoad);
		byteBuffer.putLong(serverVOIPRX);
		byteBuffer.putLong(serverVOIPTX);
		byteBuffer.putLong(serverRX);
		byteBuffer.putLong(serverTX);
		byteBuffer.putLong(serverResent);
		byteBuffer.putDouble(serverLoss);
		byteBuffer.putFloat(serverFPS);
		byteBuffer.putLong(serverNetworkingFPS);
		byteBuffer.putInt(serverStoredChunks);
		byteBuffer.putInt(serverRelevantChunks);
		byteBuffer.putInt(serverZombiesTotal);
		byteBuffer.putInt(serverZombiesLoaded);
		byteBuffer.putInt(serverZombiesSimulated);
		byteBuffer.putInt(serverZombiesCulled);
		byteBuffer.putInt(NetworkZombieManager.getInstance().getAuthorizedZombieCount(udpConnection));
		byteBuffer.putInt(serverZombiesUnauthorized);
		byteBuffer.putInt(serverZombiesReusable);
		byteBuffer.putInt(udpConnection.playerDownloadServer.getWaitingRequests());
		byteBuffer.putInt(serverPlayers);
		GameWindow.WriteString(byteBuffer, "");
	}

	public static void parse(ByteBuffer byteBuffer) {
		long long1 = System.currentTimeMillis();
		long long2 = byteBuffer.getLong();
		serverMemMax = byteBuffer.getLong();
		serverMemFree = byteBuffer.getLong();
		serverMemTotal = byteBuffer.getLong();
		serverMemUsed = byteBuffer.getLong();
		serverCPUCores = byteBuffer.getLong();
		serverCPULoad = byteBuffer.getLong();
		serverVOIPRX = byteBuffer.getLong();
		serverVOIPTX = byteBuffer.getLong();
		serverRX = byteBuffer.getLong();
		serverTX = byteBuffer.getLong();
		serverResent = byteBuffer.getLong();
		serverLoss = byteBuffer.getDouble();
		serverFPS = byteBuffer.getFloat();
		serverNetworkingFPS = byteBuffer.getLong();
		serverStoredChunks = byteBuffer.getInt();
		serverRelevantChunks = byteBuffer.getInt();
		serverZombiesTotal = byteBuffer.getInt();
		serverZombiesLoaded = byteBuffer.getInt();
		serverZombiesSimulated = byteBuffer.getInt();
		serverZombiesCulled += byteBuffer.getInt();
		serverZombiesAuthorized = byteBuffer.getInt();
		serverZombiesUnauthorized = byteBuffer.getInt();
		serverZombiesReusable = byteBuffer.getInt();
		serverWaitingRequests = byteBuffer.getInt();
		serverPlayers = byteBuffer.getInt();
		serverRevision = GameWindow.ReadString(byteBuffer);
		serverHandledLossPingHistory.remove(long2);
		if (long2 >= serverHandledPingPeriodStart) {
			serverLastPing = long1 - long2;
			serverHandledPingHistory.add(serverLastPing);
		}
	}

	public static KahluaTable getLuaStatus() {
		statusTable.wipe();
		if (GameClient.bClient) {
			statusTable.rawset("serverTime", NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toSeconds(GameTime.getServerTime())));
			statusTable.rawset("svnRevision", "");
			statusTable.rawset("buildDate", "");
			statusTable.rawset("buildTime", "");
			statusTable.rawset("version", Core.getInstance().getVersionNumber());
			statusTable.rawset("pingEnabled", DebugOptions.instance.MultiplayerPing.getValue());
			statusTable.rawset("lastPing", String.valueOf(clientLastPing));
			statusTable.rawset("avgPing", String.valueOf(clientAvgPing));
			statusTable.rawset("minPing", String.valueOf(clientMinPing));
		}

		return statusTable;
	}

	public static KahluaTable getLuaStatistics() {
		statsTable.wipe();
		if (GameClient.bClient) {
			statsTable.rawset("clientTime", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
			statsTable.rawset("serverTime", NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toSeconds(GameTime.getServerTime())));
			statsTable.rawset("clientRevision", String.valueOf(""));
			statsTable.rawset("serverRevision", String.valueOf(serverRevision));
			statsTable.rawset("clientPlayers", String.valueOf(clientPlayers));
			statsTable.rawset("serverPlayers", String.valueOf(serverPlayers));
			statsTable.rawset("clientVOIPSource", String.valueOf(clientVOIPSource));
			statsTable.rawset("clientVOIPFreq", String.valueOf(clientVOIPFreq));
			statsTable.rawset("clientVOIPRX", String.valueOf(clientVOIPRX));
			statsTable.rawset("clientVOIPTX", String.valueOf(clientVOIPTX));
			statsTable.rawset("clientRX", String.valueOf(clientRX));
			statsTable.rawset("clientTX", String.valueOf(clientTX));
			statsTable.rawset("clientResent", String.valueOf(clientResent));
			statsTable.rawset("clientLoss", String.valueOf((int)clientLoss));
			statsTable.rawset("serverVOIPRX", String.valueOf(serverVOIPRX));
			statsTable.rawset("serverVOIPTX", String.valueOf(serverVOIPTX));
			statsTable.rawset("serverRX", String.valueOf(serverRX));
			statsTable.rawset("serverTX", String.valueOf(serverTX));
			statsTable.rawset("serverResent", String.valueOf(serverResent));
			statsTable.rawset("serverLoss", String.valueOf((int)serverLoss));
			statsTable.rawset("clientLastPing", String.valueOf(clientLastPing));
			statsTable.rawset("clientAvgPing", String.valueOf(clientAvgPing));
			statsTable.rawset("clientMinPing", String.valueOf(clientMinPing));
			statsTable.rawset("serverPingLast", String.valueOf(serverLastPing));
			statsTable.rawset("serverPingMin", String.valueOf(serverMinPing));
			statsTable.rawset("serverPingAvg", String.valueOf(serverAvgPing));
			statsTable.rawset("serverPingMax", String.valueOf(serverMaxPing));
			statsTable.rawset("serverPingLoss", String.valueOf(serverLossPing));
			statsTable.rawset("clientCPUCores", String.valueOf(clientCPUCores));
			statsTable.rawset("clientCPULoad", String.valueOf(clientCPULoad));
			statsTable.rawset("clientMemMax", String.valueOf(clientMemMax));
			statsTable.rawset("clientMemFree", String.valueOf(clientMemFree));
			statsTable.rawset("clientMemTotal", String.valueOf(clientMemTotal));
			statsTable.rawset("clientMemUsed", String.valueOf(clientMemUsed));
			statsTable.rawset("serverCPUCores", String.valueOf(serverCPUCores));
			statsTable.rawset("serverCPULoad", String.valueOf(serverCPULoad));
			statsTable.rawset("serverMemMax", String.valueOf(serverMemMax));
			statsTable.rawset("serverMemFree", String.valueOf(serverMemFree));
			statsTable.rawset("serverMemTotal", String.valueOf(serverMemTotal));
			statsTable.rawset("serverMemUsed", String.valueOf(serverMemUsed));
			statsTable.rawset("serverNetworkingFPS", String.valueOf((int)serverNetworkingFPS));
			statsTable.rawset("serverFPS", String.valueOf((int)serverFPS));
			statsTable.rawset("clientFPS", String.valueOf((int)clientFPS));
			statsTable.rawset("serverStoredChunks", String.valueOf(serverStoredChunks));
			statsTable.rawset("serverRelevantChunks", String.valueOf(serverRelevantChunks));
			statsTable.rawset("serverZombiesTotal", String.valueOf(serverZombiesTotal));
			statsTable.rawset("serverZombiesLoaded", String.valueOf(serverZombiesLoaded));
			statsTable.rawset("serverZombiesSimulated", String.valueOf(serverZombiesSimulated));
			statsTable.rawset("serverZombiesCulled", String.valueOf(serverZombiesCulled));
			statsTable.rawset("serverZombiesAuthorized", String.valueOf(serverZombiesAuthorized));
			statsTable.rawset("serverZombiesUnauthorized", String.valueOf(serverZombiesUnauthorized));
			statsTable.rawset("serverZombiesReusable", String.valueOf(serverZombiesReusable));
			statsTable.rawset("clientStoredChunks", String.valueOf(clientStoredChunks));
			statsTable.rawset("clientRelevantChunks", String.valueOf(clientRelevantChunks));
			statsTable.rawset("clientZombiesTotal", String.valueOf(clientZombiesTotal));
			statsTable.rawset("clientZombiesLoaded", String.valueOf(clientZombiesLoaded));
			statsTable.rawset("clientZombiesSimulated", String.valueOf(clientZombiesSimulated));
			statsTable.rawset("clientZombiesCulled", String.valueOf(clientZombiesCulled));
			statsTable.rawset("clientZombiesAuthorized", String.valueOf(clientZombiesAuthorized));
			statsTable.rawset("clientZombiesUnauthorized", String.valueOf(clientZombiesUnauthorized));
			statsTable.rawset("clientZombiesReusable", String.valueOf(clientZombiesReusable));
			statsTable.rawset("serverWaitingRequests", String.valueOf(serverWaitingRequests));
			statsTable.rawset("clientSentRequests", String.valueOf(clientSentRequests));
			statsTable.rawset("requested1", String.valueOf(requested1));
			statsTable.rawset("requested2", String.valueOf(requested2));
			statsTable.rawset("pending1", String.valueOf(pending1));
			statsTable.rawset("pending2", String.valueOf(pending2));
		}

		return statsTable;
	}

	private static Object $deserializeLambda$(SerializedLambda serializedLambda) {
		String string = serializedLambda.getImplMethodName();
		byte byte1 = -1;
		switch (string.hashCode()) {
		case 108861471: 
			if (string.equals("lambda$Init$93733d2f$1")) {
				byte1 = 0;
			}

		
		default: 
			switch (byte1) {
			case 0: 
				if (serializedLambda.getImplMethodKind() == 6 && serializedLambda.getFunctionalInterfaceClass().equals("javax/management/NotificationFilter") && serializedLambda.getFunctionalInterfaceMethodName().equals("isNotificationEnabled") && serializedLambda.getFunctionalInterfaceMethodSignature().equals("(Ljavax/management/Notification;)Z") && serializedLambda.getImplClass().equals("zombie/network/MPStatistics") && serializedLambda.getImplMethodSignature().equals("(Ljavax/management/Notification;)Z")) {
					return (serializedLambdax)->{
						return "java.management.memory.threshold.exceeded".equals(serializedLambdax.getType());
					};
				}

			
			default: 
				throw new IllegalArgumentException("Invalid lambda deserialization");
			
			}

		
		}
	}

	static  {
		statsTable = LuaManager.platform.newTable();
		statusTable = LuaManager.platform.newTable();
		ulRequestTimeout = new UpdateLimit(10000L);
		ulStatistics = new UpdateLimit(2000L);
		ulPing = new UpdateLimit(1000L);
		serverStatisticsEnabled = false;
		serverPlayers = 0;
		clientPlayers = 0;
		clientLastPing = -1;
		clientAvgPing = -1;
		clientMinPing = -1;
		clientVOIPSource = "";
		clientVOIPFreq = "";
		clientVOIPRX = 0L;
		clientVOIPTX = 0L;
		serverVOIPRX = 0L;
		serverVOIPTX = 0L;
		serverWaitingRequests = 0;
		clientSentRequests = 0;
		requested1 = 0;
		requested2 = 0;
		pending1 = 0;
		pending2 = 0;
		serverCPUCores = 0L;
		serverCPULoad = 0L;
		serverMemMax = 0L;
		serverMemFree = 0L;
		serverMemTotal = 0L;
		serverMemUsed = 0L;
		serverRX = 0L;
		serverTX = 0L;
		serverResent = 0L;
		serverLoss = 0.0;
		serverFPS = 0.0F;
		serverNetworkingUpdates = 0L;
		serverNetworkingFPS = 0L;
		serverRevision = "";
		clientCPUCores = 0L;
		clientCPULoad = 0L;
		clientMemMax = 0L;
		clientMemFree = 0L;
		clientMemTotal = 0L;
		clientMemUsed = 0L;
		clientRX = 0L;
		clientTX = 0L;
		clientResent = 0L;
		clientLoss = 0.0;
		clientFPS = 0.0F;
		serverStoredChunks = 0;
		serverRelevantChunks = 0;
		serverZombiesTotal = 0;
		serverZombiesLoaded = 0;
		serverZombiesSimulated = 0;
		serverZombiesCulled = 0;
		serverZombiesAuthorized = 0;
		serverZombiesUnauthorized = 0;
		serverZombiesReusable = 0;
		serverZombiesUpdated = 0;
		clientStoredChunks = 0;
		clientRelevantChunks = 0;
		clientZombiesTotal = 0;
		clientZombiesLoaded = 0;
		clientZombiesSimulated = 0;
		clientZombiesCulled = 0;
		clientZombiesAuthorized = 0;
		clientZombiesUnauthorized = 0;
		clientZombiesReusable = 0;
		clientZombiesUpdated = 0;
		zombieUpdates = 0L;
		serverMinPing = 0L;
		serverMaxPing = 0L;
		serverAvgPing = 0L;
		serverLastPing = 0L;
		serverLossPing = 0L;
		serverHandledPingPeriodStart = 0L;
		serverHandledPingPacketIndex = 0;
		serverHandledPingHistory = new ArrayList();
		serverHandledLossPingHistory = new HashSet();
		pingIntervalCount = 60L;
		pingLimitCount = 20L;
		maxPingToSum = 1000L;
	}
}
