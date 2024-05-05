package zombie.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;


public class CoopMaster {
	private Process serverProcess;
	private Thread serverThread;
	private PrintStream serverCommandStream;
	private final List incomingMessages = new LinkedList();
	private Pattern serverMessageParser = Pattern.compile("^([\\-\\w]+)(\\[(\\d+)\\])?@(.*)$");
	private CoopMaster.TerminationReason serverTerminationReason;
	private Thread timeoutWatchThread;
	private boolean serverResponded;
	public static final CoopMaster instance = new CoopMaster();
	private String adminUsername = null;
	private String adminPassword = null;
	private String serverName = null;
	private Long serverSteamID = null;
	private String serverIP = null;
	private Integer serverPort = null;
	private int autoCookie = 0;
	private static final int autoCookieOffset = 1000000;
	private static final int maxAutoCookie = 1000000;
	private final List listeners = new LinkedList();

	private CoopMaster() {
		this.adminPassword = UUID.randomUUID().toString();
	}

	public void launchServer(String string, String string2, int int1) throws IOException {
		this.launchServer(string, string2, int1, false);
	}

	public void softreset(String string, String string2, int int1) throws IOException {
		this.launchServer(string, string2, int1, true);
	}

	private void launchServer(String string, String string2, int int1, boolean boolean1) throws IOException {
		String string3 = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
		String[] stringArray = new String[]{"../bin", "../lib/lwjgl.jar", "../lib/uncommons-maths-1.2.3.jar", "../lib/lwjgl_util.jar", "../lib/sqlite-jdbc-3.8.10.1.jar", "../lib/trove-3.0.3.jar"};
		String string4 = "";
		String[] stringArray2 = stringArray;
		int int2 = stringArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			String string5 = stringArray2[int3];
			if (!string4.isEmpty()) {
				string4 = string4 + File.pathSeparator;
			}

			string4 = string4 + string5;
		}

		if (SteamUtils.isSteamModeEnabled()) {
			string2 = "admin";
		}

		ArrayList arrayList = new ArrayList();
		arrayList.add(string3);
		arrayList.add("-Xms" + int1 + "m");
		arrayList.add("-Xmx" + int1 + "m");
		String string6 = System.getProperty("java.library.path");
		arrayList.add("-Djava.library.path=" + string6);
		string6 = System.getProperty("java.class.path");
		arrayList.add("-Djava.class.path=" + string6);
		string6 = System.getProperty("user.home");
		arrayList.add("-Duser.home=" + string6);
		arrayList.add("-Dzomboid.znetlog=1");
		arrayList.add("-Dzomboid.steam=" + (SteamUtils.isSteamModeEnabled() ? "1" : "0"));
		if (boolean1) {
			arrayList.add("-Dsoftreset");
		}

		if (Core.bDebug) {
			arrayList.add("-Ddebug");
		}

		arrayList.add("zombie.network.GameServer");
		arrayList.add("-coop");
		arrayList.add("-servername");
		arrayList.add(this.serverName = string);
		arrayList.add("-adminusername");
		arrayList.add(this.adminUsername = string2);
		arrayList.add("-adminpassword");
		arrayList.add(this.adminPassword);
		arrayList.add("-cachedir=" + ZomboidFileSystem.instance.getCacheDir());
		ProcessBuilder processBuilder = new ProcessBuilder(arrayList);
		this.serverTerminationReason = CoopMaster.TerminationReason.NormalTermination;
		this.serverResponded = false;
		this.serverProcess = processBuilder.start();
		this.serverCommandStream = new PrintStream(this.serverProcess.getOutputStream());
		this.serverThread = new Thread(ThreadGroups.Workers, this::readServer);
		this.serverThread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
		this.serverThread.start();
		this.timeoutWatchThread = new Thread(ThreadGroups.Workers, this::watchServer);
		this.timeoutWatchThread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
		this.timeoutWatchThread.start();
	}

	private void readServer() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.serverProcess.getInputStream()));
		while (true) {
			try {
				int int1 = this.serverProcess.exitValue();
				break;
			} catch (IllegalThreadStateException illegalThreadStateException) {
				String string = null;
				try {
					string = bufferedReader.readLine();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				if (string != null) {
					this.storeMessage(string);
					this.serverResponded = true;
				}
			}
		}

		this.storeMessage("process-status@terminated");
	}

	public void abortServer() {
		this.serverProcess.destroy();
	}

	private void watchServer() {
		int int1 = Math.max(ServerOptions.instance.CoopServerLaunchTimeout.getValue(), 5);
		try {
			Thread.sleep((long)(1000 * int1));
			if (!this.serverResponded) {
				this.serverTerminationReason = CoopMaster.TerminationReason.Timeout;
				this.abortServer();
			}
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

	public boolean isRunning() {
		return this.serverThread != null && this.serverThread.isAlive();
	}

	public CoopMaster.TerminationReason terminationReason() {
		return this.serverTerminationReason;
	}

	private void storeMessage(String string) {
		synchronized (this.incomingMessages) {
			this.incomingMessages.add(string);
		}
	}

	public synchronized void sendMessage(String string, String string2, String string3) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(string);
		if (string2 == null) {
			stringBuilder.append("@");
		} else {
			stringBuilder.append("[");
			stringBuilder.append(string2);
			stringBuilder.append("]@");
		}

		stringBuilder.append(string3);
		String string4 = stringBuilder.toString();
		if (this.serverCommandStream != null) {
			this.serverCommandStream.println(string4);
			this.serverCommandStream.flush();
		}
	}

	public void sendMessage(String string, String string2) {
		this.sendMessage(string, (String)null, string2);
	}

	public synchronized void invokeServer(String string, String string2, ICoopServerMessageListener iCoopServerMessageListener) {
		this.autoCookie = (this.autoCookie + 1) % 1000000;
		String string3 = Integer.toString(1000000 + this.autoCookie);
		this.addListener(iCoopServerMessageListener, new CoopMaster.ListenerOptions(string, string3, true));
		this.sendMessage(string, string3, string2);
	}

	public String getMessage() {
		String string = null;
		synchronized (this.incomingMessages) {
			if (this.incomingMessages.size() != 0) {
				string = (String)this.incomingMessages.get(0);
				this.incomingMessages.remove(0);
				if (!"ping@ping".equals(string)) {
					System.out.println("SERVER: " + string);
				}
			}

			return string;
		}
	}

	public void update() {
		String string;
		while ((string = this.getMessage()) != null) {
			Matcher matcher = this.serverMessageParser.matcher(string);
			if (matcher.find()) {
				String string2 = matcher.group(1);
				String string3 = matcher.group(3);
				String string4 = matcher.group(4);
				LuaEventManager.triggerEvent("OnCoopServerMessage", string2, string3, string4);
				this.handleMessage(string2, string3, string4);
			} else {
				DebugLog.log(DebugType.Network, "[CoopMaster] Unknown message incoming from the slave server: " + string);
			}
		}
	}

	private void handleMessage(String string, String string2, String string3) {
		if (Objects.equals(string, "ping")) {
			this.sendMessage("ping", string2, "pong");
		} else if (Objects.equals(string, "steam-id")) {
			if (Objects.equals(string3, "null")) {
				this.serverSteamID = null;
			} else {
				this.serverSteamID = SteamUtils.convertStringToSteamID(string3);
			}
		} else if (Objects.equals(string, "server-address")) {
			DebugLog.log("Got server-address: " + string3);
			String string4 = "^(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)$";
			Pattern pattern = Pattern.compile(string4);
			Matcher matcher = pattern.matcher(string3);
			if (matcher.find()) {
				String string5 = matcher.group(1);
				String string6 = matcher.group(2);
				this.serverIP = string5;
				this.serverPort = Integer.valueOf(string6);
				DebugLog.log("Successfully parsed: address = " + this.serverIP + ", port = " + this.serverPort);
			} else {
				DebugLog.log("Failed to parse server address");
			}
		}

		this.invokeListeners(string, string2, string3);
	}

	public void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		kahluaTable2.rawset("launch", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				boolean kahluaTable2 = false;
				if (kahluaTable == 4) {
					Object var4 = platform.get(1);
					Object var5 = platform.get(2);
					Object var6 = platform.get(3);
					if (!(var4 instanceof String) || !(var5 instanceof String) || !(var6 instanceof Double)) {
						return 0;
					}

					try {
						CoopMaster.this.launchServer((String)var4, (String)var5, ((Double)var6).intValue());
						kahluaTable2 = true;
					} catch (IOException var8) {
						var8.printStackTrace();
					}
				} else {
					DebugLog.log(DebugType.Network, "[CoopMaster] wrong number of arguments: " + kahluaTable);
				}

				platform.push(kahluaTable2);
				return 1;
			}
		});
		kahluaTable2.rawset("softreset", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				boolean kahluaTable2 = false;
				if (kahluaTable == 4) {
					Object var4 = platform.get(1);
					Object var5 = platform.get(2);
					Object var6 = platform.get(3);
					if (!(var4 instanceof String) || !(var5 instanceof String) || !(var6 instanceof Double)) {
						return 0;
					}

					try {
						CoopMaster.this.softreset((String)var4, (String)var5, ((Double)var6).intValue());
						kahluaTable2 = true;
					} catch (IOException var8) {
						var8.printStackTrace();
					}
				} else {
					DebugLog.log(DebugType.Network, "[CoopMaster] wrong number of arguments: " + kahluaTable);
				}

				platform.push(kahluaTable2);
				return 1;
			}
		});
		kahluaTable2.rawset("isRunning", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				platform.push(CoopMaster.this.isRunning());
				return 1;
			}
		});
		kahluaTable2.rawset("sendMessage", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				Object kahluaTable2;
				Object var4;
				if (kahluaTable == 4) {
					kahluaTable2 = platform.get(1);
					var4 = platform.get(2);
					Object var5 = platform.get(3);
					if (kahluaTable2 instanceof String && var4 instanceof String && var5 instanceof String) {
						CoopMaster.this.sendMessage((String)kahluaTable2, (String)var4, (String)var5);
					}
				} else if (kahluaTable == 3) {
					kahluaTable2 = platform.get(1);
					var4 = platform.get(2);
					if (kahluaTable2 instanceof String && var4 instanceof String) {
						CoopMaster.this.sendMessage((String)kahluaTable2, (String)var4);
					}
				}

				return 0;
			}
		});
		kahluaTable2.rawset("getAdminPassword", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				platform.push(CoopMaster.this.adminPassword);
				return 1;
			}
		});
		kahluaTable2.rawset("getTerminationReason", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				platform.push(CoopMaster.this.serverTerminationReason.toString());
				return 1;
			}
		});
		kahluaTable2.rawset("getSteamID", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				if (CoopMaster.this.serverSteamID != null) {
					platform.push(SteamUtils.convertSteamIDToString(CoopMaster.this.serverSteamID));
					return 1;
				} else {
					return 0;
				}
			}
		});
		kahluaTable2.rawset("getAddress", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				platform.push(CoopMaster.this.serverIP);
				return 1;
			}
		});
		kahluaTable2.rawset("getPort", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				platform.push(CoopMaster.this.serverPort);
				return 1;
			}
		});
		kahluaTable2.rawset("abort", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				CoopMaster.this.abortServer();
				return 0;
			}
		});
		kahluaTable2.rawset("getServerSaveFolder", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				Object kahluaTable2 = platform.get(1);
				platform.push(CoopMaster.this.getServerSaveFolder((String)kahluaTable2));
				return 1;
			}
		});
		kahluaTable2.rawset("getPlayerSaveFolder", new JavaFunction(){
			
			public int call(LuaCallFrame platform, int kahluaTable) {
				Object kahluaTable2 = platform.get(1);
				platform.push(CoopMaster.this.getPlayerSaveFolder((String)kahluaTable2));
				return 1;
			}
		});
		kahluaTable.rawset("CoopServer", kahluaTable2);
	}

	public void addListener(ICoopServerMessageListener iCoopServerMessageListener, CoopMaster.ListenerOptions listenerOptions) {
		synchronized (this.listeners) {
			this.listeners.add(new CoopMaster.Pair(iCoopServerMessageListener, listenerOptions));
		}
	}

	public void addListener(ICoopServerMessageListener iCoopServerMessageListener) {
		this.addListener(iCoopServerMessageListener, (CoopMaster.ListenerOptions)null);
	}

	public void removeListener(ICoopServerMessageListener iCoopServerMessageListener) {
		synchronized (this.listeners) {
			int int1;
			for (int1 = 0; int1 < this.listeners.size() && ((CoopMaster.Pair)this.listeners.get(int1)).first != iCoopServerMessageListener; ++int1) {
			}

			if (int1 < this.listeners.size()) {
				this.listeners.remove(int1);
			}
		}
	}

	private void invokeListeners(String string, String string2, String string3) {
		synchronized (this.listeners) {
			Iterator iterator = this.listeners.iterator();
			while (true) {
				while (true) {
					ICoopServerMessageListener iCoopServerMessageListener;
					CoopMaster.ListenerOptions listenerOptions;
					do {
						if (!iterator.hasNext()) {
							return;
						}

						CoopMaster.Pair pair = (CoopMaster.Pair)iterator.next();
						iCoopServerMessageListener = (ICoopServerMessageListener)pair.first;
						listenerOptions = (CoopMaster.ListenerOptions)pair.second;
					}			 while (iCoopServerMessageListener == null);

					if (listenerOptions == null) {
						iCoopServerMessageListener.OnCoopServerMessage(string, string2, string3);
					} else if ((listenerOptions.tag == null || listenerOptions.tag.equals(string)) && (listenerOptions.cookie == null || listenerOptions.cookie.equals(string2))) {
						if (listenerOptions.autoRemove) {
							iterator.remove();
						}

						iCoopServerMessageListener.OnCoopServerMessage(string, string2, string3);
					}
				}
			}
		}
	}

	public String getServerName() {
		return this.serverName;
	}

	public String getServerSaveFolder(String string) {
		return LuaManager.GlobalObject.sanitizeWorldName(string);
	}

	public String getPlayerSaveFolder(String string) {
		return LuaManager.GlobalObject.sanitizeWorldName(string + "_player");
	}

	public static enum TerminationReason {

		NormalTermination,
		Timeout;

		private static CoopMaster.TerminationReason[] $values() {
			return new CoopMaster.TerminationReason[]{NormalTermination, Timeout};
		}
	}

	public class ListenerOptions {
		public String tag;
		public String cookie;
		public boolean autoRemove;

		public ListenerOptions(String string, String string2, boolean boolean1) {
			this.tag = null;
			this.cookie = null;
			this.autoRemove = false;
			this.tag = string;
			this.cookie = string2;
			this.autoRemove = boolean1;
		}

		public ListenerOptions(String string, String string2) {
			this(string, string2, false);
		}

		public ListenerOptions(String string) {
			this(string, (String)null, false);
		}
	}

	private class Pair {
		private final Object first;
		private final Object second;

		public Pair(Object object, Object object2) {
			this.first = object;
			this.second = object2;
		}

		public Object getFirst() {
			return this.first;
		}

		public Object getSecond() {
			return this.second;
		}
	}
}
