package zombie.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;


public class MPStatistic {
	public static MPStatistic instance = new MPStatistic();
	private static final boolean doPrintStatistic = false;
	private static final boolean doCSVStatistic = true;
	private static int Period = 10;
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
	private int maxIncomeBytesPerSecound = 0;
	private int maxOutcomeBytesPerSecound = 0;
	private int currentIncomeBytesPerSecound = 0;
	private int currentOutcomeBytesPerSecound = 0;
	private long lastCalculateBPS = 0L;
	private ArrayList incomePackets = new ArrayList();
	private ArrayList outcomePackets = new ArrayList();
	private ArrayList incomeBytes = new ArrayList();
	private ArrayList outcomeBytes = new ArrayList();
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
		this.incomePackets.clear();
		this.outcomePackets.clear();
		this.incomeBytes.clear();
		this.outcomeBytes.clear();
		for (int int1 = 0; int1 < 220; ++int1) {
			this.incomePackets.add(0);
			this.outcomePackets.add(0);
			this.incomeBytes.add(0);
			this.outcomeBytes.add(0);
		}

		if (GameServer.bServer) {
			this.openCSVStatistic();
		}
	}

	public static MPStatistic getInstance() {
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

	public void addIncomePacket(short short1, int int1) {
		if (short1 < 220) {
			int int2 = (Integer)this.incomePackets.get(short1);
			this.incomePackets.set(short1, int2 + 1);
			int int3 = (Integer)this.incomeBytes.get(short1);
			this.incomeBytes.set(short1, int3 + int1);
			++this.countIncomePackets;
			this.countIncomeBytes += int1;
			this.currentIncomeBytesPerSecound += int1;
			this.calculateMaxBPS();
		}
	}

	public void addOutcomePacket(short short1, int int1) {
		if (short1 < 220) {
			int int2 = (Integer)this.outcomePackets.get(short1);
			this.outcomePackets.set(short1, int2 + 1);
			int int3 = (Integer)this.outcomeBytes.get(short1);
			this.outcomeBytes.set(short1, int3 + int1);
			++this.countOutcomePackets;
			this.countOutcomeBytes += int1;
			this.currentOutcomeBytesPerSecound += int1;
			this.calculateMaxBPS();
		}
	}

	void calculateMaxBPS() {
		if (System.currentTimeMillis() - this.lastCalculateBPS > 1000L) {
			this.lastCalculateBPS = System.currentTimeMillis();
			if (this.currentIncomeBytesPerSecound > this.maxIncomeBytesPerSecound) {
				this.maxIncomeBytesPerSecound = this.currentIncomeBytesPerSecound;
			}

			if (this.currentOutcomeBytesPerSecound > this.maxOutcomeBytesPerSecound) {
				this.maxOutcomeBytesPerSecound = this.currentOutcomeBytesPerSecound;
			}

			this.currentIncomeBytesPerSecound = 0;
			this.currentOutcomeBytesPerSecound = 0;
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
			int int1;
			KahluaTable kahluaTable3;
			for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
				kahluaTable2 = LuaManager.platform.newTable();
				UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int2);
				kahluaTable2.rawset("ip", udpConnection.ip);
				kahluaTable2.rawset("username", udpConnection.username);
				kahluaTable2.rawset("accessLevel", udpConnection.accessLevel);
				KahluaTable kahluaTable4 = LuaManager.platform.newTable();
				for (int1 = 0; int1 < udpConnection.players.length; ++int1) {
					if (udpConnection.players[int1] != null) {
						kahluaTable3 = LuaManager.platform.newTable();
						kahluaTable3.rawset("username", udpConnection.players[int1].username);
						kahluaTable3.rawset("x", (double)udpConnection.players[int1].x);
						kahluaTable3.rawset("y", (double)udpConnection.players[int1].y);
						kahluaTable3.rawset("z", (double)udpConnection.players[int1].z);
						kahluaTable4.rawset(int1, kahluaTable3);
					}
				}

				kahluaTable2.rawset("users", kahluaTable4);
				kahluaTable2.rawset("diff", (double)udpConnection.statistic.diff);
				kahluaTable2.rawset("pingAVG", (double)udpConnection.statistic.pingAVG);
				kahluaTable2.rawset("remotePlayersCount", (double)udpConnection.statistic.remotePlayersCount);
				kahluaTable2.rawset("remotePlayersDesyncAVG", (double)udpConnection.statistic.remotePlayersDesyncAVG);
				kahluaTable2.rawset("remotePlayersDesyncMax", (double)udpConnection.statistic.remotePlayersDesyncMax);
				kahluaTable2.rawset("remotePlayersTeleports", (double)udpConnection.statistic.remotePlayersTeleports);
				kahluaTable2.rawset("zombiesCount", (double)udpConnection.statistic.zombiesCount);
				kahluaTable2.rawset("zombiesLocalOwnership", (double)udpConnection.statistic.zombiesLocalOwnership);
				kahluaTable2.rawset("zombiesDesyncAVG", (double)udpConnection.statistic.zombiesDesyncAVG);
				kahluaTable2.rawset("zombiesDesyncMax", (double)udpConnection.statistic.zombiesDesyncMax);
				kahluaTable2.rawset("zombiesTeleports", (double)udpConnection.statistic.zombiesTeleports);
				kahluaTable.rawset(int2, kahluaTable2);
			}

			this.table.rawset("connections", kahluaTable);
			this.table.rawset("packetLength", (double)this.packetLength);
			this.table.rawset("countIncomePackets", (double)this.countIncomePackets);
			this.table.rawset("countIncomeBytes", (double)this.countIncomeBytes);
			this.table.rawset("maxIncomeBytesPerSecound", (double)this.maxIncomeBytesPerSecound);
			KahluaTable kahluaTable5 = LuaManager.platform.newTable();
			int int3;
			for (short short1 = 0; short1 < 220; ++short1) {
				int int4 = (Integer)this.incomePackets.get(short1);
				int3 = (Integer)this.incomeBytes.get(short1);
				if (int4 > 0) {
					KahluaTable kahluaTable6 = LuaManager.platform.newTable();
					kahluaTable6.rawset("name", PacketTypes.packetTypeToString(short1));
					kahluaTable6.rawset("count", (double)int4);
					kahluaTable6.rawset("bytes", (double)int3);
					kahluaTable5.rawset(short1, kahluaTable6);
				}

				this.incomePackets.set(short1, 0);
				this.incomeBytes.set(short1, 0);
			}

			this.table.rawset("incomePacketsTable", kahluaTable5);
			this.countIncomePackets = 0;
			this.countIncomeBytes = 0;
			this.maxIncomeBytesPerSecound = 0;
			this.table.rawset("countOutcomePackets", (double)this.countOutcomePackets);
			this.table.rawset("countOutcomeBytes", (double)this.countOutcomeBytes);
			this.table.rawset("maxOutcomeBytesPerSecound", (double)this.maxOutcomeBytesPerSecound);
			kahluaTable2 = LuaManager.platform.newTable();
			for (short short2 = 0; short2 < 220; ++short2) {
				int3 = (Integer)this.outcomePackets.get(short2);
				int1 = (Integer)this.outcomeBytes.get(short2);
				if (int3 > 0) {
					kahluaTable3 = LuaManager.platform.newTable();
					kahluaTable3.rawset("name", PacketTypes.packetTypeToString(short2));
					kahluaTable3.rawset("count", (double)int3);
					kahluaTable3.rawset("bytes", (double)int1);
					kahluaTable2.rawset(short2, kahluaTable3);
				}

				this.outcomePackets.set(short2, 0);
				this.outcomeBytes.set(short2, 0);
			}

			this.table.rawset("outcomePacketsTable", kahluaTable2);
			this.countOutcomePackets = 0;
			this.countOutcomeBytes = 0;
			this.maxOutcomeBytesPerSecound = 0;
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
			for (short short1 = 0; short1 < 220; ++short1) {
				printStream = this.csvIncomePacketsFile;
				string2 = PacketTypes.packetTypeToString(short1);
				printStream.print(string2 + "(" + short1 + "); ");
			}

			this.csvIncomePacketsFile.println();
			File file3 = new File(string + File.separator + "IncomeBytes.csv");
			this.csvIncomeBytesFile = new PrintStream(file3);
			for (short short2 = 0; short2 < 220; ++short2) {
				printStream = this.csvIncomeBytesFile;
				string2 = PacketTypes.packetTypeToString(short2);
				printStream.print(string2 + "(" + short2 + "); ");
			}

			this.csvIncomeBytesFile.println();
			File file4 = new File(string + File.separator + "OutcomePackets.csv");
			this.csvOutcomePacketsFile = new PrintStream(file4);
			for (short short3 = 0; short3 < 220; ++short3) {
				printStream = this.csvOutcomePacketsFile;
				string2 = PacketTypes.packetTypeToString(short3);
				printStream.print(string2 + "(" + short3 + "); ");
			}

			this.csvOutcomePacketsFile.println();
			File file5 = new File(string + File.separator + "OutcomeBytes.csv");
			this.csvOutcomeBytesFile = new PrintStream(file5);
			for (short short4 = 0; short4 < 220; ++short4) {
				printStream = this.csvOutcomeBytesFile;
				string2 = PacketTypes.packetTypeToString(short4);
				printStream.print(string2 + "(" + short4 + "); ");
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
				this.csvStatisticFile.print(this.maxIncomeBytesPerSecound + ";");
				this.csvStatisticFile.print(this.countOutcomePackets + ";");
				this.csvStatisticFile.print(this.countOutcomeBytes + ";");
				this.csvStatisticFile.println(this.maxOutcomeBytesPerSecound + ";");
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
					for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); ++int2) {
						UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int2);
						if (udpConnection2 != null && udpConnection2.username != null && udpConnection2.username.hashCode() == (Integer)this.csvConnections.get(int1)) {
							udpConnection = udpConnection2;
						}
					}

					if (udpConnection == null) {
						this.csvConnectionsFile.println("; ; ; ; ; ; ; ; ; ; ; ; ; ; ; ");
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
						this.csvConnectionsFile.println(udpConnection.statistic.zombiesTeleports + "; ");
					}
				}

				for (short short1 = 0; short1 < 220; ++short1) {
					PrintStream printStream = this.csvIncomePacketsFile;
					Object object = this.incomePackets.get(short1);
					printStream.print(object + ";");
					printStream = this.csvIncomeBytesFile;
					object = this.incomeBytes.get(short1);
					printStream.print(object + ";");
					printStream = this.csvOutcomePacketsFile;
					object = this.outcomePackets.get(short1);
					printStream.print(object + ";");
					printStream = this.csvOutcomeBytesFile;
					object = this.outcomeBytes.get(short1);
					printStream.print(object + ";");
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
		this.table = LuaManager.platform.newTable();
		try {
			this.table.load((ByteBuffer)byteBuffer, 184);
			this.table.rawset("lastReportTime", (double)System.currentTimeMillis());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public KahluaTable getStatisticTableForLua() {
		return this.table;
	}

	public void setPeriod(int int1) {
		Period = int1;
		this.table.rawset("period", (double)Period);
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
