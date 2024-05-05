package zombie.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.LogSeverity;


public class MPStatistic {
	public static MPStatistic instance;
	private static boolean doPrintStatistic = false;
	private static boolean doCSVStatistic = false;
	private static int Period = 0;
	public MPStatistic.TasksStatistic LoaderThreadTasks = new MPStatistic.TasksStatistic();
	public MPStatistic.TasksStatistic RecalcThreadTasks = new MPStatistic.TasksStatistic();
	public MPStatistic.SaveTasksStatistic SaveTasks = new MPStatistic.SaveTasksStatistic();
	public MPStatistic.ServerCellStatistic ServerMapToLoad = new MPStatistic.ServerCellStatistic();
	public MPStatistic.ServerCellStatistic ServerMapLoadedCells = new MPStatistic.ServerCellStatistic();
	public MPStatistic.ServerCellStatistic ServerMapLoaded2 = new MPStatistic.ServerCellStatistic();
	private int countServerChunkThreadSaveNow = 0;
	public MPStatistic.MainThreadStatistic Main = new MPStatistic.MainThreadStatistic();
	public MPStatistic.ThreadStatistic ServerLOS = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic LoaderThread = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic RecalcAllThread = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic SaveThread = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic PolyPathThread = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic WorldReuser = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic PlayerDownloadServer = new MPStatistic.ThreadStatistic();
	public MPStatistic.ThreadStatistic MapCollisionThread = new MPStatistic.ThreadStatistic();
	public MPStatistic.ProbeStatistic ChunkChecksum = new MPStatistic.ProbeStatistic();
	public MPStatistic.ProbeStatistic Bullet = new MPStatistic.ProbeStatistic();
	public MPStatistic.ProbeStatistic AnimationPlayerUpdate = new MPStatistic.ProbeStatistic();
	public MPStatistic.ProbeStatistic ServerMapPreupdate = new MPStatistic.ProbeStatistic();
	public MPStatistic.ProbeStatistic ServerMapPostupdate = new MPStatistic.ProbeStatistic();
	public MPStatistic.ProbeStatistic IngameStateUpdate = new MPStatistic.ProbeStatistic();
	private long packetLength = 0L;
	private int countIncomePackets = 0;
	private int countOutcomePackets = 0;
	private int countIncomeBytes = 0;
	private int countOutcomeBytes = 0;
	private int maxIncomeBytesPerSecond = 0;
	private int maxOutcomeBytesPerSecond = 0;
	private int currentIncomeBytesPerSecond = 0;
	private int currentOutcomeBytesPerSecond = 0;
	private long lastCalculateBPS = 0L;
	private long lastReport = 0L;
	private long minUpdatePeriod = 9999L;
	private long maxUpdatePeriod = 0L;
	private long avgUpdatePeriod = 0L;
	private long currentAvgUpdatePeriod = 0L;
	private long teleports = 0L;
	private long counter1 = 0L;
	private long counter2 = 0L;
	private long counter3 = 0L;
	private long updatePeriods = 0L;
	private int loadCellFromDisk = 0;
	private int saveCellToDisk = 0;
	public static boolean clientStatisticEnable = false;
	private PrintStream csvStatisticFile = null;
	private PrintStream csvIncomePacketsFile = null;
	private PrintStream csvIncomeBytesFile = null;
	private PrintStream csvOutcomePacketsFile = null;
	private PrintStream csvOutcomeBytesFile = null;
	private PrintStream csvConnectionsFile = null;
	private final ArrayList csvConnections = new ArrayList();
	private KahluaTable table = null;

	public static MPStatistic getInstance() {
		if (instance == null) {
			instance = new MPStatistic();
		}

		return instance;
	}

	public void IncrementServerChunkThreadSaveNow() {
		++this.countServerChunkThreadSaveNow;
	}

	public void teleport() {
		++this.teleports;
	}

	public void count1(long long1) {
		this.counter1 += long1;
	}

	public void count2(long long1) {
		this.counter2 += long1;
	}

	public void count3(long long1) {
		this.counter3 += long1;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putLong(this.minUpdatePeriod);
		byteBufferWriter.putLong(this.maxUpdatePeriod);
		byteBufferWriter.putLong(this.currentAvgUpdatePeriod / this.updatePeriods);
		byteBufferWriter.putLong(this.updatePeriods / (long)Period);
		byteBufferWriter.putLong(this.teleports);
		byteBufferWriter.putLong((long)GameServer.udpEngine.connections.size());
		byteBufferWriter.putLong(this.counter1 / this.updatePeriods);
		byteBufferWriter.putLong(this.counter2 / this.updatePeriods);
		byteBufferWriter.putLong(this.counter3 / this.updatePeriods);
	}

	public void setPacketsLength(long long1) {
		this.packetLength = long1;
	}

	public void addIncomePacket(PacketTypes.PacketType packetType, int int1) {
		if (packetType != null) {
			++packetType.incomePackets;
			++this.countIncomePackets;
			packetType.incomeBytes += int1;
			this.countIncomeBytes += int1;
			this.currentIncomeBytesPerSecond += int1;
			this.calculateMaxBPS();
		}
	}

	public void addOutcomePacket(short short1, int int1) {
		PacketTypes.PacketType packetType = (PacketTypes.PacketType)PacketTypes.packetTypes.get(short1);
		if (packetType != null) {
			++packetType.outcomePackets;
			++this.countOutcomePackets;
			packetType.outcomeBytes += int1;
			this.countOutcomeBytes += int1;
			this.currentOutcomeBytesPerSecond += int1;
			this.calculateMaxBPS();
		}
	}

	void calculateMaxBPS() {
		if (System.currentTimeMillis() - this.lastCalculateBPS > 1000L) {
			this.lastCalculateBPS = System.currentTimeMillis();
			if (this.currentIncomeBytesPerSecond > this.maxIncomeBytesPerSecond) {
				this.maxIncomeBytesPerSecond = this.currentIncomeBytesPerSecond;
			}

			if (this.currentOutcomeBytesPerSecond > this.maxOutcomeBytesPerSecond) {
				this.maxOutcomeBytesPerSecond = this.currentOutcomeBytesPerSecond;
			}

			this.currentIncomeBytesPerSecond = 0;
			this.currentOutcomeBytesPerSecond = 0;
		}
	}

	public void IncrementLoadCellFromDisk() {
		++this.loadCellFromDisk;
	}

	public void IncrementSaveCellToDisk() {
		++this.saveCellToDisk;
	}

	public void process(long long1) {
		if (long1 > this.maxUpdatePeriod) {
			this.maxUpdatePeriod = long1;
		}

		if (long1 < this.minUpdatePeriod) {
			this.minUpdatePeriod = long1;
		}

		this.avgUpdatePeriod = (long)((float)this.avgUpdatePeriod + (float)(long1 - this.avgUpdatePeriod) * 0.05F);
		this.currentAvgUpdatePeriod += long1;
		++this.updatePeriods;
		if (Period != 0 && System.currentTimeMillis() - this.lastReport >= (long)Period * 1000L) {
			this.lastReport = System.currentTimeMillis();
			this.printStatistic();
			this.printCSVStatistic();
			GameServer.sendShortStatistic();
			this.table = LuaManager.platform.newTable();
			this.table.rawset("lastReport", (double)this.lastReport);
			this.table.rawset("period", (double)Period);
			this.table.rawset("minUpdatePeriod", (double)this.minUpdatePeriod);
			this.table.rawset("maxUpdatePeriod", (double)this.maxUpdatePeriod);
			this.table.rawset("avgUpdatePeriod", (double)this.avgUpdatePeriod);
			this.maxUpdatePeriod = 0L;
			this.minUpdatePeriod = 9999L;
			this.currentAvgUpdatePeriod = 0L;
			this.updatePeriods = 0L;
			this.teleports = 0L;
			this.counter1 = 0L;
			this.counter2 = 0L;
			this.counter3 = 0L;
			this.table.rawset("loadCellFromDisk", (double)this.loadCellFromDisk);
			this.table.rawset("saveCellToDisk", (double)this.saveCellToDisk);
			this.loadCellFromDisk = 0;
			this.saveCellToDisk = 0;
			this.table.rawset("usedMemory", (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
			this.table.rawset("totalMemory", (double)Runtime.getRuntime().totalMemory());
			this.table.rawset("freeMemory", (double)Runtime.getRuntime().freeMemory());
			this.table.rawset("countConnections", (double)GameServer.udpEngine.connections.size());
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			KahluaTable kahluaTable2;
			KahluaTable kahluaTable3;
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				KahluaTable kahluaTable4 = LuaManager.platform.newTable();
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				kahluaTable4.rawset("ip", udpConnection.ip);
				kahluaTable4.rawset("username", udpConnection.username);
				kahluaTable4.rawset("accessLevel", udpConnection.accessLevel);
				KahluaTable kahluaTable5 = LuaManager.platform.newTable();
				for (int int2 = 0; int2 < udpConnection.players.length; ++int2) {
					if (udpConnection.players[int2] != null) {
						kahluaTable2 = LuaManager.platform.newTable();
						kahluaTable2.rawset("username", udpConnection.players[int2].username);
						kahluaTable2.rawset("x", (double)udpConnection.players[int2].x);
						kahluaTable2.rawset("y", (double)udpConnection.players[int2].y);
						kahluaTable2.rawset("z", (double)udpConnection.players[int2].z);
						kahluaTable5.rawset(int2, kahluaTable2);
					}
				}

				kahluaTable4.rawset("users", kahluaTable5);
				kahluaTable4.rawset("diff", (double)udpConnection.statistic.diff);
				kahluaTable4.rawset("pingAVG", (double)udpConnection.statistic.pingAVG);
				kahluaTable4.rawset("remotePlayersCount", (double)udpConnection.statistic.remotePlayersCount);
				kahluaTable4.rawset("remotePlayersDesyncAVG", (double)udpConnection.statistic.remotePlayersDesyncAVG);
				kahluaTable4.rawset("remotePlayersDesyncMax", (double)udpConnection.statistic.remotePlayersDesyncMax);
				kahluaTable4.rawset("remotePlayersTeleports", (double)udpConnection.statistic.remotePlayersTeleports);
				kahluaTable4.rawset("zombiesCount", (double)udpConnection.statistic.zombiesCount);
				kahluaTable4.rawset("zombiesLocalOwnership", (double)udpConnection.statistic.zombiesLocalOwnership);
				kahluaTable4.rawset("zombiesDesyncAVG", (double)udpConnection.statistic.zombiesDesyncAVG);
				kahluaTable4.rawset("zombiesDesyncMax", (double)udpConnection.statistic.zombiesDesyncMax);
				kahluaTable4.rawset("zombiesTeleports", (double)udpConnection.statistic.zombiesTeleports);
				kahluaTable4.rawset("FPS", (double)udpConnection.statistic.FPS);
				kahluaTable4.rawset("FPSMin", (double)udpConnection.statistic.FPSMin);
				kahluaTable4.rawset("FPSAvg", (double)udpConnection.statistic.FPSAvg);
				kahluaTable4.rawset("FPSMax", (double)udpConnection.statistic.FPSMax);
				kahluaTable3 = LuaManager.platform.newTable();
				short short1 = 0;
				for (int int3 = 0; int3 < 32; ++int3) {
					kahluaTable3.rawset(int3, (double)udpConnection.statistic.FPSHistogramm[int3]);
					if (short1 < udpConnection.statistic.FPSHistogramm[int3]) {
						short1 = udpConnection.statistic.FPSHistogramm[int3];
					}
				}

				kahluaTable4.rawset("FPSHistogram", kahluaTable3);
				kahluaTable4.rawset("FPSHistogramMax", (double)short1);
				kahluaTable.rawset(int1, kahluaTable4);
			}

			this.table.rawset("connections", kahluaTable);
			this.table.rawset("packetLength", (double)this.packetLength);
			this.table.rawset("countIncomePackets", (double)this.countIncomePackets);
			this.table.rawset("countIncomeBytes", (double)this.countIncomeBytes);
			this.table.rawset("maxIncomeBytesPerSecound", (double)this.maxIncomeBytesPerSecond);
			KahluaTable kahluaTable6 = LuaManager.platform.newTable();
			byte byte1 = -1;
			PacketTypes.PacketType packetType;
			for (Iterator iterator = PacketTypes.packetTypes.values().iterator(); iterator.hasNext(); packetType.incomeBytes = 0) {
				packetType = (PacketTypes.PacketType)iterator.next();
				if (packetType.incomePackets > 0) {
					kahluaTable3 = LuaManager.platform.newTable();
					kahluaTable3.rawset("name", packetType.name());
					kahluaTable3.rawset("count", (double)packetType.incomePackets);
					kahluaTable3.rawset("bytes", (double)packetType.incomeBytes);
					kahluaTable6.rawset(byte1, kahluaTable3);
				}

				packetType.incomePackets = 0;
			}

			this.table.rawset("incomePacketsTable", kahluaTable6);
			this.countIncomePackets = 0;
			this.countIncomeBytes = 0;
			this.maxIncomeBytesPerSecond = 0;
			this.table.rawset("countOutcomePackets", (double)this.countOutcomePackets);
			this.table.rawset("countOutcomeBytes", (double)this.countOutcomeBytes);
			this.table.rawset("maxOutcomeBytesPerSecound", (double)this.maxOutcomeBytesPerSecond);
			KahluaTable kahluaTable7 = LuaManager.platform.newTable();
			int int4 = -1;
			PacketTypes.PacketType packetType2;
			for (Iterator iterator2 = PacketTypes.packetTypes.values().iterator(); iterator2.hasNext(); packetType2.outcomeBytes = 0) {
				packetType2 = (PacketTypes.PacketType)iterator2.next();
				if (packetType2.outcomePackets > 0) {
					kahluaTable2 = LuaManager.platform.newTable();
					kahluaTable2.rawset("name", packetType2.name());
					kahluaTable2.rawset("count", (double)packetType2.outcomePackets);
					kahluaTable2.rawset("bytes", (double)packetType2.outcomeBytes);
					kahluaTable7.rawset(int4++, kahluaTable2);
				}

				packetType2.outcomePackets = 0;
			}

			this.table.rawset("outcomePacketsTable", kahluaTable7);
			this.countOutcomePackets = 0;
			this.countOutcomeBytes = 0;
			this.maxOutcomeBytesPerSecond = 0;
			this.LoaderThreadTasks.Clear();
			this.RecalcThreadTasks.Clear();
			this.SaveTasks.Clear();
			this.ServerMapToLoad.Clear();
			this.ServerMapLoadedCells.Clear();
			this.ServerMapLoaded2.Clear();
			this.countServerChunkThreadSaveNow = 0;
			this.Main.Clear();
			this.ServerLOS.Clear();
			this.LoaderThread.Clear();
			this.RecalcAllThread.Clear();
			this.SaveThread.Clear();
			this.PolyPathThread.Clear();
			this.WorldReuser.Clear();
			this.PlayerDownloadServer.Clear();
			this.MapCollisionThread.Clear();
			this.ChunkChecksum.Clear();
			this.Bullet.Clear();
			this.AnimationPlayerUpdate.Clear();
			this.ServerMapPreupdate.Clear();
			this.ServerMapPostupdate.Clear();
			this.IngameStateUpdate.Clear();
			GameServer.getStatisticFromClients();
			GameServer.sendStatistic();
		}
	}

	private void printStatistic() {
		if (doPrintStatistic) {
			DebugLog.Statistic.println("=== STATISTICS ===");
			DebugLog.Statistic.println("UpdatePeriod (mils) min:" + this.minUpdatePeriod + " max:" + this.maxUpdatePeriod + " avg:" + this.avgUpdatePeriod);
			DebugLog.Statistic.println("Server cell disk operations load:" + this.loadCellFromDisk + " save:" + this.saveCellToDisk);
			DebugLogStream debugLogStream = DebugLog.Statistic;
			long long1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			debugLogStream.println("Memory (bytes):" + long1 + " of " + Runtime.getRuntime().totalMemory());
			DebugLog.Statistic.println("== Connections:" + GameServer.udpEngine.connections.size() + " ==");
			String string;
			for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
				DebugLog.Statistic.println("Connection " + int1 + " " + udpConnection.ip + " " + udpConnection.username + " " + udpConnection.accessLevel);
				for (int int2 = 0; int2 < udpConnection.players.length; ++int2) {
					if (udpConnection.players[int2] != null) {
						string = udpConnection.players[int2].username;
						DebugLog.Statistic.println("  User " + string + " (" + udpConnection.players[int2].x + ", " + udpConnection.players[int2].y + ", " + udpConnection.players[int2].z + ")");
					}
				}

				int int3 = udpConnection.statistic.diff / 2;
				DebugLog.Statistic.println("  Ping:" + int3 + " AVG:" + udpConnection.statistic.pingAVG);
				DebugLog.Statistic.println("  Players count:" + udpConnection.statistic.remotePlayersCount + " desyncAVG:" + udpConnection.statistic.remotePlayersDesyncAVG + " desyncMAX:" + udpConnection.statistic.remotePlayersDesyncMax + " teleports:" + udpConnection.statistic.remotePlayersTeleports);
				DebugLog.Statistic.println("  Zombies count:" + udpConnection.statistic.zombiesCount + " LocalOwnership:" + udpConnection.statistic.zombiesLocalOwnership + " desyncAVG:" + udpConnection.statistic.zombiesDesyncAVG + " desyncMAX:" + udpConnection.statistic.zombiesDesyncMax + " teleports:" + udpConnection.statistic.zombiesTeleports);
				DebugLog.Statistic.println("  FPS:" + udpConnection.statistic.FPS + " Min:" + udpConnection.statistic.FPSMin + " Avg:" + udpConnection.statistic.FPSAvg + " Max:" + udpConnection.statistic.FPSMax);
			}

			DebugLog.Statistic.println("== Income Packets ==");
			DebugLog.Statistic.println("length of packet queue:" + this.packetLength);
			DebugLog.Statistic.println("count packets:" + this.countIncomePackets);
			DebugLog.Statistic.println("count bytes:" + this.countIncomeBytes);
			DebugLog.Statistic.println("max bps:" + this.maxIncomeBytesPerSecond);
			Iterator iterator = PacketTypes.packetTypes.values().iterator();
			PacketTypes.PacketType packetType;
			while (iterator.hasNext()) {
				packetType = (PacketTypes.PacketType)iterator.next();
				if (packetType.incomePackets > 0) {
					debugLogStream = DebugLog.Statistic;
					string = packetType.name();
					debugLogStream.println(string + "(" + packetType.getId() + ") count:" + packetType.incomePackets + " bytes:" + packetType.incomeBytes);
				}
			}

			DebugLog.Statistic.println("== Outcome Packets ==");
			DebugLog.Statistic.println("count packets:" + this.countOutcomePackets);
			DebugLog.Statistic.println("count bytes:" + this.countOutcomeBytes);
			DebugLog.Statistic.println("max bps:" + this.maxOutcomeBytesPerSecond);
			iterator = PacketTypes.packetTypes.values().iterator();
			while (iterator.hasNext()) {
				packetType = (PacketTypes.PacketType)iterator.next();
				if (packetType.outcomePackets > 0) {
					debugLogStream = DebugLog.Statistic;
					string = packetType.name();
					debugLogStream.println(string + "(" + packetType.getId() + ") count:" + packetType.outcomePackets + " bytes:" + packetType.outcomeBytes);
				}
			}

			DebugLog.Statistic.println("=== END STATISTICS ===");
		}
	}

	public static String getStatisticDir() {
		String string = ZomboidFileSystem.instance.getCacheDirSub("Statistic");
		ZomboidFileSystem.ensureFolderExists(string);
		File file = new File(string);
		return file.getAbsolutePath();
	}

	private void removeCSVStatistics() {
		String string = getStatisticDir();
		File file;
		try {
			file = new File(string + File.separator + "Statistic.csv");
			file.delete();
		} catch (Exception exception) {
			DebugLog.Statistic.printException(exception, "Delete file failed: Statistic.csv", LogSeverity.Error);
		}

		try {
			file = new File(string + File.separator + "Connections.csv");
			file.delete();
		} catch (Exception exception2) {
			DebugLog.Statistic.printException(exception2, "Delete file failed: Connections.csv", LogSeverity.Error);
		}

		try {
			file = new File(string + File.separator + "IncomePackets.csv");
			file.delete();
		} catch (Exception exception3) {
			DebugLog.Statistic.printException(exception3, "Delete file failed: IncomePackets.csv", LogSeverity.Error);
		}

		try {
			file = new File(string + File.separator + "IncomeBytes.csv");
			file.delete();
		} catch (Exception exception4) {
			DebugLog.Statistic.printException(exception4, "Delete file failed: IncomeBytes.csv", LogSeverity.Error);
		}

		try {
			file = new File(string + File.separator + "OutcomePackets.csv");
			file.delete();
		} catch (Exception exception5) {
			DebugLog.Statistic.printException(exception5, "Delete file failed: OutcomePackets.csv", LogSeverity.Error);
		}

		try {
			file = new File(string + File.separator + "OutcomeBytes.csv");
			file.delete();
		} catch (Exception exception6) {
			DebugLog.Statistic.printException(exception6, "Delete file failed: OutcomeBytes.csv", LogSeverity.Error);
		}
	}

	private void closeCSVStatistics() {
		if (this.csvStatisticFile != null) {
			this.csvStatisticFile.close();
		}

		this.csvStatisticFile = null;
		if (this.csvConnectionsFile != null) {
			this.csvConnectionsFile.close();
		}

		this.csvConnectionsFile = null;
		if (this.csvIncomePacketsFile != null) {
			this.csvIncomePacketsFile.close();
		}

		this.csvIncomePacketsFile = null;
		if (this.csvIncomeBytesFile != null) {
			this.csvIncomeBytesFile.close();
		}

		this.csvIncomeBytesFile = null;
		if (this.csvOutcomePacketsFile != null) {
			this.csvOutcomePacketsFile.close();
		}

		this.csvOutcomePacketsFile = null;
		if (this.csvOutcomeBytesFile != null) {
			this.csvOutcomeBytesFile.close();
		}

		this.csvOutcomeBytesFile = null;
	}

	private void openCSVStatistic() {
		if (doCSVStatistic) {
			String string = getStatisticDir();
			File file;
			PrintStream printStream;
			String string2;
			try {
				file = new File(string + File.separator + "Statistic.csv");
				if (file.exists()) {
					this.csvStatisticFile = new PrintStream(new FileOutputStream(file, true));
				} else {
					this.csvStatisticFile = new PrintStream(file);
					printStream = this.csvStatisticFile;
					string2 = this.Main.PrintTitle("MainThread");
					printStream.println("lastReport; minUpdatePeriod; maxUpdatePeriod; avgUpdatePeriod; loadCellFromDisk; saveCellToDisk; countLoaderThreadTasksAdded; countLoaderThreadTasksProcessed; countRecalcThreadTasksAdded; countRecalcThreadTasksProcessed; countSaveUnloadedTasksAdded; countSaveLoadedTasksAdded; countSaveGameTimeTasksAdded; countQuitThreadTasksAdded; countSaveThreadTasksProcessed; countServerMapToLoadAdded; countServerMapToLoadCanceled; countServerMapLoadedCellsAdded; countServerMapLoadedCellsCanceled; countServerMapLoaded2Added; countServerMapLoaded2Canceled; countServerChunkThreadSaveNow; " + string2 + this.ServerLOS.PrintTitle("ServerLOS") + this.LoaderThread.PrintTitle("LoaderThread") + this.RecalcAllThread.PrintTitle("RecalcAllThread") + this.SaveThread.PrintTitle("SaveThread") + this.PolyPathThread.PrintTitle("PolyPathThread") + this.WorldReuser.PrintTitle("WorldReuser") + this.PlayerDownloadServer.PrintTitle("WorldReuser") + this.MapCollisionThread.PrintTitle("MapCollisionThread") + this.ChunkChecksum.PrintTitle("ChunkChecksum") + this.Bullet.PrintTitle("Bullet") + this.AnimationPlayerUpdate.PrintTitle("AnimationPlayerUpdate") + this.ServerMapPreupdate.PrintTitle("ServerMapPreupdate") + this.ServerMapPostupdate.PrintTitle("ServerMapPostupdate") + this.IngameStateUpdate.PrintTitle("IngameStateUpdate") + "totalMemory; freeMemory; countConnections; packetLength; countIncomePackets; countIncomeBytes; maxIncomeBytesPerSecound; countOutcomePackets; countOutcomeBytes; maxOutcomeBytesPerSecound");
				}
			} catch (FileNotFoundException fileNotFoundException) {
				DebugLog.Statistic.printException(fileNotFoundException, "Open file failed: Statistic.csv", LogSeverity.Error);
				if (this.csvStatisticFile != null) {
					this.csvStatisticFile.close();
				}

				this.csvStatisticFile = null;
			}

			try {
				file = new File(string + File.separator + "Connections.csv");
				if (file.exists()) {
					this.csvConnectionsFile = new PrintStream(new FileOutputStream(file, true));
				} else {
					this.csvConnectionsFile = new PrintStream(file);
					this.csvConnectionsFile.print("ip; ");
					this.csvConnectionsFile.print("username; ");
					this.csvConnectionsFile.print("accessLevel; ");
					this.csvConnectionsFile.print("players.length; ");
					this.csvConnectionsFile.print("ping; ");
					this.csvConnectionsFile.print("pingAVG; ");
					this.csvConnectionsFile.print("remotePlayersCount; ");
					this.csvConnectionsFile.print("remotePlayersDesyncAVG; ");
					this.csvConnectionsFile.print("remotePlayersDesyncMax; ");
					this.csvConnectionsFile.print("remotePlayersTeleports; ");
					this.csvConnectionsFile.print("zombiesCount; ");
					this.csvConnectionsFile.print("zombiesLocalOwnership; ");
					this.csvConnectionsFile.print("zombiesDesyncAVG; ");
					this.csvConnectionsFile.print("zombiesDesyncMax; ");
					this.csvConnectionsFile.print("zombiesTeleports; ");
					this.csvConnectionsFile.print("FPS; ");
					this.csvConnectionsFile.print("FPSMin; ");
					this.csvConnectionsFile.print("FPSAvg; ");
					this.csvConnectionsFile.print("FPSMax; ");
					for (int int1 = 0; int1 < 32; ++int1) {
						this.csvConnectionsFile.print("FPSHistogramm[" + int1 + "]; ");
					}

					this.csvConnectionsFile.println();
				}
			} catch (FileNotFoundException fileNotFoundException2) {
				DebugLog.Statistic.printException(fileNotFoundException2, "Open file failed: Connections.csv", LogSeverity.Error);
				if (this.csvConnectionsFile != null) {
					this.csvConnectionsFile.close();
				}

				this.csvConnectionsFile = null;
			}

			PacketTypes.PacketType packetType;
			Iterator iterator;
			try {
				file = new File(string + File.separator + "IncomePackets.csv");
				if (file.exists()) {
					this.csvIncomePacketsFile = new PrintStream(new FileOutputStream(file, true));
				} else {
					this.csvIncomePacketsFile = new PrintStream(file);
					iterator = PacketTypes.packetTypes.values().iterator();
					while (iterator.hasNext()) {
						packetType = (PacketTypes.PacketType)iterator.next();
						printStream = this.csvIncomePacketsFile;
						string2 = packetType.name();
						printStream.print(string2 + "(" + packetType.getId() + "); ");
					}

					this.csvIncomePacketsFile.println();
				}
			} catch (FileNotFoundException fileNotFoundException3) {
				DebugLog.Statistic.printException(fileNotFoundException3, "Open file failed: IncomePackets.csv", LogSeverity.Error);
				if (this.csvIncomePacketsFile != null) {
					this.csvIncomePacketsFile.close();
				}

				this.csvIncomePacketsFile = null;
			}

			try {
				file = new File(string + File.separator + "IncomeBytes.csv");
				if (file.exists()) {
					this.csvIncomeBytesFile = new PrintStream(new FileOutputStream(file, true));
				} else {
					this.csvIncomeBytesFile = new PrintStream(file);
					iterator = PacketTypes.packetTypes.values().iterator();
					while (iterator.hasNext()) {
						packetType = (PacketTypes.PacketType)iterator.next();
						printStream = this.csvIncomeBytesFile;
						string2 = packetType.name();
						printStream.print(string2 + "(" + packetType.getId() + "); ");
					}

					this.csvIncomeBytesFile.println();
				}
			} catch (FileNotFoundException fileNotFoundException4) {
				DebugLog.Statistic.printException(fileNotFoundException4, "Open file failed: IncomeBytes.csv", LogSeverity.Error);
				if (this.csvIncomeBytesFile != null) {
					this.csvIncomeBytesFile.close();
				}

				this.csvIncomeBytesFile = null;
			}

			try {
				file = new File(string + File.separator + "OutcomePackets.csv");
				if (file.exists()) {
					this.csvOutcomePacketsFile = new PrintStream(new FileOutputStream(file, true));
				} else {
					this.csvOutcomePacketsFile = new PrintStream(file);
					iterator = PacketTypes.packetTypes.values().iterator();
					while (iterator.hasNext()) {
						packetType = (PacketTypes.PacketType)iterator.next();
						printStream = this.csvOutcomePacketsFile;
						string2 = packetType.name();
						printStream.print(string2 + "(" + packetType.getId() + "); ");
					}

					this.csvOutcomePacketsFile.println();
				}
			} catch (FileNotFoundException fileNotFoundException5) {
				DebugLog.Statistic.printException(fileNotFoundException5, "Open file failed: OutcomePackets.csv", LogSeverity.Error);
				if (this.csvOutcomePacketsFile != null) {
					this.csvOutcomePacketsFile.close();
				}

				this.csvOutcomePacketsFile = null;
			}

			try {
				file = new File(string + File.separator + "OutcomeBytes.csv");
				if (file.exists()) {
					this.csvOutcomeBytesFile = new PrintStream(new FileOutputStream(file, true));
				} else {
					this.csvOutcomeBytesFile = new PrintStream(file);
					iterator = PacketTypes.packetTypes.values().iterator();
					while (iterator.hasNext()) {
						packetType = (PacketTypes.PacketType)iterator.next();
						printStream = this.csvOutcomeBytesFile;
						string2 = packetType.name();
						printStream.print(string2 + "(" + packetType.getId() + "); ");
					}

					this.csvOutcomeBytesFile.println();
				}
			} catch (FileNotFoundException fileNotFoundException6) {
				DebugLog.Statistic.printException(fileNotFoundException6, "Open file failed: OutcomeBytes.csv", LogSeverity.Error);
				if (this.csvOutcomeBytesFile != null) {
					this.csvOutcomeBytesFile.close();
				}

				this.csvOutcomeBytesFile = null;
			}
		}
	}

	private void printCSVStatistic() {
		if (doCSVStatistic) {
			try {
				try {
					this.openCSVStatistic();
					if (this.csvStatisticFile != null) {
						this.csvStatisticFile.print(System.currentTimeMillis() + ";");
						this.csvStatisticFile.print(this.minUpdatePeriod + ";");
						this.csvStatisticFile.print(this.maxUpdatePeriod + ";");
						this.csvStatisticFile.print(this.avgUpdatePeriod + ";");
						this.csvStatisticFile.print(this.loadCellFromDisk + ";");
						this.csvStatisticFile.print(this.saveCellToDisk + ";");
						this.csvStatisticFile.print(this.LoaderThreadTasks.Print());
						this.csvStatisticFile.print(this.RecalcThreadTasks.Print());
						this.csvStatisticFile.print(this.SaveTasks.Print());
						this.csvStatisticFile.print(this.ServerMapToLoad.Print());
						this.csvStatisticFile.print(this.ServerMapLoadedCells.Print());
						this.csvStatisticFile.print(this.ServerMapLoaded2.Print());
						this.csvStatisticFile.print(this.countServerChunkThreadSaveNow + ";");
						this.csvStatisticFile.print(this.Main.Print());
						this.csvStatisticFile.print(this.ServerLOS.Print());
						this.csvStatisticFile.print(this.LoaderThread.Print());
						this.csvStatisticFile.print(this.RecalcAllThread.Print());
						this.csvStatisticFile.print(this.SaveThread.Print());
						this.csvStatisticFile.print(this.PolyPathThread.Print());
						this.csvStatisticFile.print(this.WorldReuser.Print());
						this.csvStatisticFile.print(this.PlayerDownloadServer.Print());
						this.csvStatisticFile.print(this.MapCollisionThread.Print());
						this.csvStatisticFile.print(this.ChunkChecksum.Print());
						this.csvStatisticFile.print(this.Bullet.Print());
						this.csvStatisticFile.print(this.AnimationPlayerUpdate.Print());
						this.csvStatisticFile.print(this.ServerMapPreupdate.Print());
						this.csvStatisticFile.print(this.ServerMapPostupdate.Print());
						this.csvStatisticFile.print(this.IngameStateUpdate.Print());
						this.csvStatisticFile.print(Runtime.getRuntime().totalMemory() + ";");
						this.csvStatisticFile.print(Runtime.getRuntime().freeMemory() + ";");
						this.csvStatisticFile.print(GameServer.udpEngine.connections.size() + ";");
						this.csvStatisticFile.print(this.packetLength + ";");
						this.csvStatisticFile.print(this.countIncomePackets + ";");
						this.csvStatisticFile.print(this.countIncomeBytes + ";");
						this.csvStatisticFile.print(this.maxIncomeBytesPerSecond + ";");
						this.csvStatisticFile.print(this.countOutcomePackets + ";");
						this.csvStatisticFile.print(this.countOutcomeBytes + ";");
						this.csvStatisticFile.println(this.maxOutcomeBytesPerSecond + ";");
						this.csvStatisticFile.flush();
					}

					if (this.csvConnectionsFile != null) {
						int int1;
						UdpConnection udpConnection;
						for (int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
							udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
							try {
								if (udpConnection != null && udpConnection.username != null && !this.csvConnections.contains(udpConnection.username.hashCode())) {
									this.csvConnections.add(udpConnection.username.hashCode());
								}
							} catch (NullPointerException nullPointerException) {
								nullPointerException.printStackTrace();
								return;
							}
						}

						for (int1 = 0; int1 < this.csvConnections.size(); ++int1) {
							udpConnection = null;
							int int2;
							for (int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
								UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
								if (udpConnection2 != null && udpConnection2.username != null && udpConnection2.username.hashCode() == (Integer)this.csvConnections.get(int1)) {
									udpConnection = udpConnection2;
								}
							}

							if (udpConnection == null) {
								for (int2 = 0; int2 < 51; ++int2) {
									this.csvConnectionsFile.print("; ");
								}
							} else {
								this.csvConnectionsFile.print(udpConnection.ip + "; ");
								this.csvConnectionsFile.print(udpConnection.username + "; ");
								this.csvConnectionsFile.print(udpConnection.accessLevel + "; ");
								this.csvConnectionsFile.print(udpConnection.players.length + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.diff / 2 + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.pingAVG + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.remotePlayersCount + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.remotePlayersDesyncAVG + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.remotePlayersDesyncMax + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.remotePlayersTeleports + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.zombiesCount + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.zombiesLocalOwnership + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.zombiesDesyncAVG + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.zombiesDesyncMax + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.zombiesTeleports + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.FPS + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.FPSMin + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.FPSAvg + "; ");
								this.csvConnectionsFile.print(udpConnection.statistic.FPSMax + "; ");
								for (int2 = 0; int2 < 32; ++int2) {
									short short1 = udpConnection.statistic.FPSHistogramm[int2];
									this.csvConnectionsFile.print(short1 + "; ");
								}
							}

							this.csvConnectionsFile.println();
						}

						this.csvConnectionsFile.flush();
					}

					if (this.csvIncomePacketsFile != null && this.csvOutcomePacketsFile != null && this.csvIncomeBytesFile != null && this.csvOutcomeBytesFile != null) {
						Iterator iterator = PacketTypes.packetTypes.values().iterator();
						while (iterator.hasNext()) {
							PacketTypes.PacketType packetType = (PacketTypes.PacketType)iterator.next();
							this.csvIncomePacketsFile.print(packetType.incomePackets + ";");
							this.csvIncomeBytesFile.print(packetType.incomeBytes + ";");
							this.csvOutcomePacketsFile.print(packetType.outcomePackets + ";");
							this.csvOutcomeBytesFile.print(packetType.outcomeBytes + ";");
						}

						this.csvIncomePacketsFile.println();
						this.csvIncomeBytesFile.println();
						this.csvOutcomePacketsFile.println();
						this.csvOutcomeBytesFile.println();
						this.csvIncomePacketsFile.flush();
						this.csvIncomeBytesFile.flush();
						this.csvOutcomePacketsFile.flush();
						this.csvOutcomeBytesFile.flush();
						return;
					}
				} catch (NullPointerException nullPointerException2) {
					nullPointerException2.printStackTrace();
				}
			} finally {
				this.closeCSVStatistics();
			}
		}
	}

	public void getStatisticTable(ByteBuffer byteBuffer) throws IOException {
		if (this.table != null) {
			this.table.save(byteBuffer);
		}
	}

	public void setStatisticTable(ByteBuffer byteBuffer) throws IOException {
		if (byteBuffer.remaining() != 0) {
			this.table = LuaManager.platform.newTable();
			try {
				this.table.load((ByteBuffer)byteBuffer, 195);
				this.table.rawset("lastReportTime", (double)System.currentTimeMillis());
			} catch (Exception exception) {
				this.table = null;
				ExceptionLogger.logException(exception);
			}
		}
	}

	public KahluaTable getStatisticTableForLua() {
		return this.table;
	}

	public void printEnabled(boolean boolean1) {
		doPrintStatistic = boolean1;
	}

	public void writeEnabled(boolean boolean1) {
		doCSVStatistic = boolean1;
		if (boolean1) {
			this.removeCSVStatistics();
		}
	}

	public void setPeriod(int int1) {
		Period = Math.max(int1, 0);
		if (this.table != null) {
			this.table.rawset("period", (double)Period);
		}
	}

	public static class TasksStatistic {
		protected long added = 0L;
		protected long processed = 0L;

		public void Clear() {
			this.added = 0L;
			this.processed = 0L;
		}

		public String PrintTitle(String string) {
			return string + "Added; " + string + "Processed; ";
		}

		public String Print() {
			return this.added + "; " + this.processed + "; ";
		}

		public void Added() {
			++this.added;
		}

		public void Processed() {
			++this.processed;
		}
	}

	public static class SaveTasksStatistic extends MPStatistic.TasksStatistic {
		private int SaveUnloadedTasksAdded = 0;
		private int SaveLoadedTasksAdded = 0;
		private int SaveGameTimeTasksAdded = 0;
		private int QuitThreadTasksAdded = 0;

		public void Clear() {
			super.Clear();
			this.SaveUnloadedTasksAdded = 0;
			this.SaveLoadedTasksAdded = 0;
			this.SaveGameTimeTasksAdded = 0;
			this.QuitThreadTasksAdded = 0;
		}

		public String PrintTitle(String string) {
			return string + "SaveUnloadedAdded; " + string + "SaveLoadedAdded; " + string + "SaveGameTimeAdded; " + string + "QuitThreadAdded; " + string + "Processed; ";
		}

		public String Print() {
			return this.SaveUnloadedTasksAdded + "; " + this.SaveLoadedTasksAdded + "; " + this.SaveGameTimeTasksAdded + "; " + this.QuitThreadTasksAdded + "; " + this.processed + "; ";
		}

		public void SaveUnloadedTasksAdded() {
			++this.SaveUnloadedTasksAdded;
		}

		public void SaveLoadedTasksAdded() {
			++this.SaveLoadedTasksAdded;
		}

		public void SaveGameTimeTasksAdded() {
			++this.SaveGameTimeTasksAdded;
		}

		public void QuitThreadTasksAdded() {
			++this.QuitThreadTasksAdded;
		}
	}

	public static class ServerCellStatistic {
		protected long added = 0L;
		protected long canceled = 0L;

		public void Clear() {
			this.added = 0L;
			this.canceled = 0L;
		}

		public String PrintTitle(String string) {
			return string + "Added; " + string + "Canceled; ";
		}

		public String Print() {
			return this.added + "; " + this.canceled + "; ";
		}

		public void Added() {
			++this.added;
		}

		public void Added(int int1) {
			this.added += (long)int1;
		}

		public void Canceled() {
			++this.canceled;
		}
	}

	public class MainThreadStatistic extends MPStatistic.ThreadStatistic {
		private long timeStartSleep = 0L;

		public void Start() {
			if (this.timeStart == 0L) {
				this.timeStart = System.currentTimeMillis();
			} else {
				long long1 = System.currentTimeMillis() - this.timeStart;
				this.timeStart = System.currentTimeMillis();
				this.timeWork += long1;
				if (this.timeMax < long1) {
					this.timeMax = long1;
				}

				++this.timeCount;
			}
		}

		public void End() {
		}

		public void StartSleep() {
			this.timeStartSleep = System.currentTimeMillis();
		}

		public void EndSleep() {
			long long1 = System.currentTimeMillis() - this.timeStartSleep;
			this.timeSleep += long1;
			this.timeStart += long1;
		}
	}

	public static class ThreadStatistic {
		protected boolean started = false;
		protected long timeStart = 0L;
		protected long timeWork = 0L;
		protected long timeMax = 0L;
		protected long timeSleep = 0L;
		protected long timeCount = 0L;

		public void Clear() {
			this.timeWork = 0L;
			this.timeMax = 0L;
			this.timeSleep = 0L;
			this.timeCount = 0L;
		}

		public String PrintTitle(String string) {
			return string + "Work; " + string + "Max; " + string + "Sleep; " + string + "Count;";
		}

		public String Print() {
			return this.timeWork + "; " + this.timeMax + "; " + this.timeSleep + "; " + this.timeCount + "; ";
		}

		public void Start() {
			if (this.started) {
				this.End();
			}

			if (this.timeStart != 0L) {
				this.timeSleep += System.currentTimeMillis() - this.timeStart;
			}

			this.timeStart = System.currentTimeMillis();
			++this.timeCount;
			this.started = true;
		}

		public void End() {
			if (this.timeStart != 0L && this.started) {
				long long1 = System.currentTimeMillis() - this.timeStart;
				this.timeStart = System.currentTimeMillis();
				this.timeWork += long1;
				if (this.timeMax < long1) {
					this.timeMax = long1;
				}

				this.started = false;
			}
		}
	}

	public static class ProbeStatistic {
		protected boolean started = false;
		protected long timeStart = 0L;
		protected long timeWork = 0L;
		protected long timeMax = 0L;
		protected long timeCount = 0L;

		public void Clear() {
			this.timeWork = 0L;
			this.timeMax = 0L;
			this.timeCount = 0L;
		}

		public String PrintTitle(String string) {
			return string + "Work; " + string + "Max; " + string + "Count;";
		}

		public String Print() {
			long long1 = this.timeWork / 1000000L;
			return long1 + "; " + this.timeMax / 1000000L + "; " + this.timeCount + "; ";
		}

		public void Start() {
			this.timeStart = System.nanoTime();
			++this.timeCount;
			this.started = true;
		}

		public void End() {
			if (this.started) {
				long long1 = System.nanoTime() - this.timeStart;
				this.timeWork += long1;
				if (this.timeMax < long1) {
					this.timeMax = long1;
				}

				this.started = false;
			}
		}
	}
}
