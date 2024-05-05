package zombie.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.ZomboidFileSystem;
import zombie.core.znet.PortMapper;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;


public class CoopSlave {
	private static PrintStream stdout;
	private static PrintStream stderr;
	private Pattern serverMessageParser = Pattern.compile("^([\\-\\w]+)(\\[(\\d+)\\])?@(.*)$");
	private long nextPing = -1L;
	private long lastPong = -1L;
	public static CoopSlave instance;
	public String hostUser = null;
	public long hostSteamID = 0L;
	private boolean masterLost = false;
	private HashSet invites = new HashSet();
	private Long serverSteamID = null;

	public static void init() throws FileNotFoundException {
		instance = new CoopSlave();
	}

	public static void initStreams() throws FileNotFoundException {
		String string = ZomboidFileSystem.instance.getCacheDir();
		FileOutputStream fileOutputStream = new FileOutputStream(string + File.separator + "coop-console.txt");
		stdout = System.out;
		stderr = System.err;
		System.setOut(new PrintStream(fileOutputStream));
		System.setErr(System.out);
	}

	private CoopSlave() {
		this.notify("coop mode enabled");
		if (System.getProperty("hostUser") != null) {
			this.hostUser = System.getProperty("zomboid.hostUser").trim();
		}
	}

	public synchronized void notify(String string) {
		this.sendMessage("info", (String)null, string);
	}

	public synchronized void sendStatus(String string) {
		this.sendMessage("status", (String)null, string);
	}

	public static void status(String string) {
		if (instance != null) {
			instance.sendStatus(string);
		}
	}

	public synchronized void sendMessage(String string) {
		this.sendMessage("message", (String)null, string);
	}

	public synchronized void sendMessage(String string, String string2, String string3) {
		if (string2 != null) {
			stdout.println(string + "[" + string2 + "]@" + string3);
		} else {
			stdout.println(string + "@" + string3);
		}
	}

	public void sendExternalIPAddress(String string) {
		this.sendMessage("get-parameter", string, PortMapper.getExternalAddress());
	}

	public synchronized void sendSteamID(String string) {
		if (this.serverSteamID == null && SteamUtils.isSteamModeEnabled()) {
			this.serverSteamID = SteamGameServer.GetSteamID();
		}

		this.sendMessage("get-parameter", string, this.serverSteamID.toString());
	}

	public boolean handleCommand(String string) {
		Matcher matcher = this.serverMessageParser.matcher(string);
		if (matcher.find()) {
			String string2 = matcher.group(1);
			String string3 = matcher.group(3);
			String string4 = matcher.group(4);
			if (Objects.equals(string2, "set-host-user")) {
				System.out.println("Set host user:" + string4);
				this.hostUser = string4;
			}

			if (Objects.equals(string2, "set-host-steamid")) {
				this.hostSteamID = SteamUtils.convertStringToSteamID(string4);
			}

			Long Long1;
			if (Objects.equals(string2, "invite-add")) {
				Long1 = SteamUtils.convertStringToSteamID(string4);
				if (Long1 != -1L) {
					this.invites.add(Long1);
				}
			}

			if (Objects.equals(string2, "invite-remove")) {
				Long1 = SteamUtils.convertStringToSteamID(string4);
				if (Long1 != -1L) {
					this.invites.remove(Long1);
				}
			}

			if (Objects.equals(string2, "get-parameter")) {
				DebugLog.log("Got get-parameter command: tag = " + string2 + " payload = " + string4);
				if (Objects.equals(string4, "external-ip")) {
					this.sendExternalIPAddress(string3);
				} else if (Objects.equals(string4, "steam-id")) {
					this.sendSteamID(string3);
				}
			}

			if (Objects.equals(string2, "ping")) {
				this.lastPong = System.currentTimeMillis();
			}

			if (Objects.equals(string2, "process-status") && Objects.equals(string4, "eof")) {
				DebugLog.log("Master connection lost: EOF");
				this.masterLost = true;
			}

			return true;
		} else {
			DebugLog.log("Got malformed command: " + string);
			return false;
		}
	}

	public String getHostUser() {
		return this.hostUser;
	}

	public void update() {
		long long1 = System.currentTimeMillis();
		if (long1 >= this.nextPing) {
			this.sendMessage("ping", (String)null, "ping");
			this.nextPing = long1 + 5000L;
		}

		long long2 = (long)(Math.max(ServerOptions.instance.CoopMasterPingTimeout.getValue(), 30) * 1000);
		if (this.lastPong == -1L) {
			this.lastPong = long1;
		}

		this.masterLost = this.masterLost || long1 - this.lastPong > long2;
	}

	public boolean masterLost() {
		return this.masterLost;
	}

	public boolean isHost(long long1) {
		return long1 == this.hostSteamID;
	}

	public boolean isInvited(long long1) {
		return this.invites.contains(long1);
	}
}
