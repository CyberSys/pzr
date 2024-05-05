package zombie.network;

import java.io.File;
import java.io.FileNotFoundException;
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


public class MPStatistic {
	public static MPStatistic instance;
	private static final boolean doPrintStatistic = false;
	private static final boolean doCSVStatistic = true;
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
	private ArrayList csvConnections = new ArrayList();
	private KahluaTable table = null;

	public MPStatistic() {
		if (GameServer.bServer) {
			this.openCSVStatistic();
		}
	}

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
		if (Period != 0 && System.currentTimeMillis() - this.lastReport >= (long)(Period * 1000)) {
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
	}

	public static String getStatisticDir() {
		String string = ZomboidFileSystem.instance.getCacheDirSub("Statistic");
		ZomboidFileSystem.ensureFolderExists(string);
		File file = new File(string);
		return file.getAbsolutePath();
	}

	private void openCSVStatistic() {
		String string = getStatisticDir();
		try {
			File file = new File(string + File.separator + "Statistic.csv");
			try {
				this.csvStatisticFile = new PrintStream(file);
			} catch (FileNotFoundException fileNotFoundException) {
				try {
					file = new File(string + File.separator + "Statistic_new.csv");
					this.csvStatisticFile = new PrintStream(file);
				} catch (FileNotFoundException fileNotFoundException2) {
					DebugLog.Statistic.error("The Statistic.csv was not open");
					fileNotFoundException2.printStackTrace();
				}
			}

			PrintStream printStream = this.csvStatisticFile;
			String string2 = this.Main.PrintTitle("MainThread");
			printStream.println("lastReport; minUpdatePeriod; maxUpdatePeriod; avgUpdatePeriod; loadCellFromDisk; saveCellToDisk; countLoaderThreadTasksAdded; countLoaderThreadTasksProcessed; countRecalcThreadTasksAdded; countRecalcThreadTasksProcessed; countSaveUnloadedTasksAdded; countSaveLoadedTasksAdded; countSaveGameTimeTasksAdded; countQuitThreadTasksAdded; countSaveThreadTasksProcessed; countServerMapToLoadAdded; countServerMapToLoadCanceled; countServerMapLoadedCellsAdded; countServerMapLoadedCellsCanceled; countServerMapLoaded2Added; countServerMapLoaded2Canceled; countServerChunkThreadSaveNow; " + string2 + this.ServerLOS.PrintTitle("ServerLOS") + this.LoaderThread.PrintTitle("LoaderThread") + this.RecalcAllThread.PrintTitle("RecalcAllThread") + this.SaveThread.PrintTitle("SaveThread") + this.PolyPathThread.PrintTitle("PolyPathThread") + this.WorldReuser.PrintTitle("WorldReuser") + this.PlayerDownloadServer.PrintTitle("WorldReuser") + this.MapCollisionThread.PrintTitle("MapCollisionThread") + this.ChunkChecksum.PrintTitle("ChunkChecksum") + this.Bullet.PrintTitle("Bullet") + this.AnimationPlayerUpdate.PrintTitle("AnimationPlayerUpdate") + this.ServerMapPreupdate.PrintTitle("ServerMapPreupdate") + this.ServerMapPostupdate.PrintTitle("ServerMapPostupdate") + this.IngameStateUpdate.PrintTitle("IngameStateUpdate") + " totalMemory; freeMemory; countConnections; packetLength; countIncomePackets; countIncomeBytes; maxIncomeBytesPerSecound; countOutcomePackets; countOutcomeBytes; maxOutcomeBytesPerSecound");
			File file2 = new File(string + File.separator + "IncomePackets.csv");
			this.csvIncomePacketsFile = new PrintStream(file2);
			Iterator iterator = PacketTypes.packetTypes.values().iterator();
			while (iterator.hasNext()) {
				PacketTypes.PacketType packetType = (PacketTypes.PacketType)iterator.next();
				printStream = this.csvIncomePacketsFile;
				string2 = packetType.name();
				printStream.print(string2 + "(" + packetType.getId() + "); ");
			}

			this.csvIncomePacketsFile.println();
			File file3 = new File(string + File.separator + "IncomeBytes.csv");
			this.csvIncomeBytesFile = new PrintStream(file3);
			Iterator iterator2 = PacketTypes.packetTypes.values().iterator();
			while (iterator2.hasNext()) {
				PacketTypes.PacketType packetType2 = (PacketTypes.PacketType)iterator2.next();
				printStream = this.csvIncomeBytesFile;
				string2 = packetType2.name();
				printStream.print(string2 + "(" + packetType2.getId() + "); ");
			}

			this.csvIncomeBytesFile.println();
			File file4 = new File(string + File.separator + "OutcomePackets.csv");
			this.csvOutcomePacketsFile = new PrintStream(file4);
			Iterator iterator3 = PacketTypes.packetTypes.values().iterator();
			while (iterator3.hasNext()) {
				PacketTypes.PacketType packetType3 = (PacketTypes.PacketType)iterator3.next();
				printStream = this.csvOutcomePacketsFile;
				string2 = packetType3.name();
				printStream.print(string2 + "(" + packetType3.getId() + "); ");
			}

			this.csvOutcomePacketsFile.println();
			File file5 = new File(string + File.separator + "OutcomeBytes.csv");
			this.csvOutcomeBytesFile = new PrintStream(file5);
			Iterator iterator4 = PacketTypes.packetTypes.values().iterator();
			while (iterator4.hasNext()) {
				PacketTypes.PacketType packetType4 = (PacketTypes.PacketType)iterator4.next();
				printStream = this.csvOutcomeBytesFile;
				string2 = packetType4.name();
				printStream.print(string2 + "(" + packetType4.getId() + "); ");
			}

			this.csvOutcomeBytesFile.println();
			File file6 = new File(string + File.separator + "Connections.csv");
			this.csvConnectionsFile = new PrintStream(file6);
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
			this.csvConnectionsFile.println("zombiesTeleports; ");
			this.csvConnectionsFile.print("FPS; ");
			this.csvConnectionsFile.print("FPSMin; ");
			this.csvConnectionsFile.print("FPSAvg; ");
			this.csvConnectionsFile.print("FPSMax; ");
			for (int int1 = 0; int1 < 32; ++int1) {
				this.csvConnectionsFile.print("FPSHistogramm[" + int1 + "]; ");
			}

			this.csvConnectionsFile.println();
		} catch (FileNotFoundException fileNotFoundException3) {
			fileNotFoundException3.printStackTrace();
			this.csvStatisticFile = null;
		}
	}

	private void printCSVStatistic() {
		try {
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

					if (udpConnection != null) {
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
						this.csvConnectionsFile.println(udpConnection.statistic.zombiesTeleports + "; ");
						this.csvConnectionsFile.print(udpConnection.statistic.zombiesTeleports + "; ");
						this.csvConnectionsFile.print(udpConnection.statistic.FPS + "; ");
						this.csvConnectionsFile.print(udpConnection.statistic.FPSMin + "; ");
						this.csvConnectionsFile.print(udpConnection.statistic.FPSAvg + "; ");
						this.csvConnectionsFile.println(udpConnection.statistic.FPSMax + "; ");
						for (int2 = 0; int2 < 32; ++int2) {
							short short1 = udpConnection.statistic.FPSHistogramm[int2];
							this.csvConnectionsFile.println(short1 + "; ");
						}
					} else {
						for (int2 = 0; int2 < 51; ++int2) {
							this.csvConnectionsFile.print("; ");
						}

						this.csvConnectionsFile.println();
					}
				}

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
				this.csvStatisticFile.flush();
				this.csvConnectionsFile.flush();
				this.csvIncomePacketsFile.flush();
				this.csvIncomeBytesFile.flush();
				this.csvOutcomePacketsFile.flush();
				this.csvOutcomeBytesFile.flush();
			}
		} catch (NullPointerException nullPointerException2) {
			nullPointerException2.printStackTrace();
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
				this.table.load((ByteBuffer)byteBuffer, 186);
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

	public void setPeriod(int int1) {
		Period = int1;
		if (this.table != null) {
			this.table.rawset("period", (double)Period);
		}
	}

	public class TasksStatistic {
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
			String string = this.added + "; " + this.processed + "; ";
			return string;
		}

		public void Added() {
			++this.added;
		}

		public void Processed() {
			++this.processed;
		}
	}

	public class SaveTasksStatistic extends MPStatistic.TasksStatistic {
		private int SaveUnloadedTasksAdded = 0;
		private int SaveLoadedTasksAdded = 0;
		private int SaveGameTimeTasksAdded = 0;
		private int QuitThreadTasksAdded = 0;

		public SaveTasksStatistic() {
			super();
		}

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
			String string = this.SaveUnloadedTasksAdded + "; " + this.SaveLoadedTasksAdded + "; " + this.SaveGameTimeTasksAdded + "; " + this.QuitThreadTasksAdded + "; " + this.processed + "; ";
			return string;
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

	public class ServerCellStatistic {
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
			String string = this.added + "; " + this.canceled + "; ";
			return string;
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

		public MainThreadStatistic() {
			super();
		}

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

	public class ThreadStatistic {
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
			String string = this.timeWork + "; " + this.timeMax + "; " + this.timeSleep + "; " + this.timeCount + "; ";
			return string;
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

	public class ProbeStatistic {
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
			String string = long1 + "; " + this.timeMax / 1000000L + "; " + this.timeCount + "; ";
			return string;
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