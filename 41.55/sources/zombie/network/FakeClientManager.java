package zombie.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.CRC32;
import org.json.JSONArray;
import org.json.JSONObject;
import zombie.characters.NetworkCharacter;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.secure.PZcrypt;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.ZNet;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;


public class FakeClientManager {
	private static final int SERVER_PORT = 16261;
	private static final int CLIENT_PORT = 17500;
	private static final String CLIENT_ADDRESS = "0.0.0.0";
	private static final String versionNumber = Core.getInstance().getVersionNumber();
	private static final DateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private static int logLevel = 0;

	private static void sleep(long long1) {
		try {
			Thread.sleep(long1);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

	private static HashMap load(String string) {
		HashMap hashMap = new HashMap();
		try {
			String string2 = new String(Files.readAllBytes(Paths.get(string)));
			JSONObject jSONObject = new JSONObject(string2);
			FakeClientManager.Movement.version = jSONObject.getString("version");
			JSONObject jSONObject2 = jSONObject.getJSONObject("config");
			JSONObject jSONObject3 = jSONObject2.getJSONObject("client");
			JSONObject jSONObject4 = jSONObject3.getJSONObject("connection");
			FakeClientManager.Client.connectionInterval = jSONObject4.getLong("interval");
			FakeClientManager.Client.connectionTimeout = jSONObject4.getLong("timeout");
			FakeClientManager.Client.connectionDelay = jSONObject4.getLong("delay");
			JSONObject jSONObject5 = jSONObject3.getJSONObject("statistics");
			FakeClientManager.Client.statisticsPeriod = jSONObject5.getInt("period");
			FakeClientManager.Client.statisticsClientID = Math.max(jSONObject5.getInt("id"), -1);
			jSONObject4 = jSONObject2.getJSONObject("player");
			FakeClientManager.Player.fps = jSONObject4.getInt("fps");
			FakeClientManager.Player.predictInterval = jSONObject4.getInt("predict");
			jSONObject5 = jSONObject2.getJSONObject("movement");
			FakeClientManager.Movement.defaultRadius = jSONObject5.getInt("radius");
			JSONObject jSONObject6 = jSONObject5.getJSONObject("motion");
			FakeClientManager.Movement.aimSpeed = jSONObject6.getInt("aim");
			FakeClientManager.Movement.sneakSpeed = jSONObject6.getInt("sneak");
			FakeClientManager.Movement.sneakRunSpeed = jSONObject6.getInt("sneakrun");
			FakeClientManager.Movement.walkSpeed = jSONObject6.getInt("walk");
			FakeClientManager.Movement.runSpeed = jSONObject6.getInt("run");
			FakeClientManager.Movement.sprintSpeed = jSONObject6.getInt("sprint");
			JSONObject jSONObject7 = jSONObject6.getJSONObject("pedestrian");
			FakeClientManager.Movement.pedestrianSpeedMin = jSONObject7.getInt("min");
			FakeClientManager.Movement.pedestrianSpeedMax = jSONObject7.getInt("max");
			JSONObject jSONObject8 = jSONObject6.getJSONObject("vehicle");
			FakeClientManager.Movement.vehicleSpeedMin = jSONObject8.getInt("min");
			FakeClientManager.Movement.vehicleSpeedMax = jSONObject8.getInt("max");
			JSONArray jSONArray = jSONObject.getJSONArray("movements");
			for (int int1 = 0; int1 < jSONArray.length(); ++int1) {
				jSONObject5 = jSONArray.getJSONObject(int1);
				int int2 = jSONObject5.getInt("id");
				String string3 = null;
				if (jSONObject5.has("description")) {
					string3 = jSONObject5.getString("description");
				}

				int int3 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
				int int4 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
				if (jSONObject5.has("spawn")) {
					JSONObject jSONObject9 = jSONObject5.getJSONObject("spawn");
					int3 = jSONObject9.getInt("x");
					int4 = jSONObject9.getInt("y");
				}

				FakeClientManager.Movement.Motion motion = Math.random() > 0.800000011920929 ? FakeClientManager.Movement.Motion.Vehicle : FakeClientManager.Movement.Motion.Pedestrian;
				if (jSONObject5.has("motion")) {
					motion = FakeClientManager.Movement.Motion.valueOf(jSONObject5.getString("motion"));
				}

				int int5 = 0;
				if (jSONObject5.has("speed")) {
					int5 = jSONObject5.getInt("speed");
				} else {
					switch (motion) {
					case Aim: 
						int5 = FakeClientManager.Movement.aimSpeed;
						break;
					
					case Sneak: 
						int5 = FakeClientManager.Movement.sneakSpeed;
						break;
					
					case SneakRun: 
						int5 = FakeClientManager.Movement.sneakRunSpeed;
						break;
					
					case Run: 
						int5 = FakeClientManager.Movement.runSpeed;
						break;
					
					case Sprint: 
						int5 = FakeClientManager.Movement.sprintSpeed;
						break;
					
					case Walk: 
						int5 = FakeClientManager.Movement.walkSpeed;
						break;
					
					case Pedestrian: 
						int5 = (int)Math.round(Math.random() * (double)(FakeClientManager.Movement.pedestrianSpeedMax - FakeClientManager.Movement.pedestrianSpeedMin) + (double)FakeClientManager.Movement.pedestrianSpeedMin);
						break;
					
					case Vehicle: 
						int5 = (int)Math.round(Math.random() * (double)(FakeClientManager.Movement.vehicleSpeedMax - FakeClientManager.Movement.vehicleSpeedMin) + (double)FakeClientManager.Movement.vehicleSpeedMin);
					
					}
				}

				FakeClientManager.Movement.Type type = FakeClientManager.Movement.Type.Line;
				if (jSONObject5.has("type")) {
					type = FakeClientManager.Movement.Type.valueOf(jSONObject5.getString("type"));
				}

				int int6 = FakeClientManager.Movement.defaultRadius;
				if (jSONObject5.has("radius")) {
					int6 = jSONObject5.getInt("radius");
				}

				IsoDirections directions = IsoDirections.fromIndex((int)Math.round(Math.random() * 7.0));
				if (jSONObject5.has("direction")) {
					directions = IsoDirections.valueOf(jSONObject5.getString("direction"));
				}

				boolean boolean1 = false;
				if (jSONObject5.has("ghost")) {
					boolean1 = jSONObject5.getBoolean("ghost");
				}

				long long1 = (long)int2 * FakeClientManager.Client.connectionInterval;
				if (jSONObject5.has("connect")) {
					long1 = jSONObject5.getLong("connect");
				}

				long long2 = 0L;
				if (jSONObject5.has("disconnect")) {
					long2 = jSONObject5.getLong("disconnect");
				}

				long long3 = 0L;
				if (jSONObject5.has("reconnect")) {
					long3 = jSONObject5.getLong("reconnect");
				}

				long long4 = 0L;
				if (jSONObject5.has("teleport")) {
					long4 = jSONObject5.getLong("teleport");
				}

				int int7 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
				int int8 = (int)Math.round(Math.random() * 6000.0 + 6000.0);
				if (jSONObject5.has("destination")) {
					JSONObject jSONObject10 = jSONObject5.getJSONObject("destination");
					int7 = jSONObject10.getInt("x");
					int8 = jSONObject10.getInt("y");
				}

				FakeClientManager.Movement movement = new FakeClientManager.Movement(int2, string3, int3, int4, motion, int5, type, int6, int7, int8, directions, boolean1, long1, long2, long3, long4);
				if (hashMap.containsKey(int2)) {
					error(int2, String.format("Client %d already exists", movement.id));
				} else {
					hashMap.put(int2, movement);
				}
			}

			return hashMap;
		} catch (Exception exception) {
			error(-1, "Scenarios file load failed");
			exception.printStackTrace();
			return hashMap;
		} finally {
			;
		}
	}

	private static void error(int int1, String string) {
		System.out.print(String.format("%5s : %s , [%2d] > %s\n", "ERROR", logDateFormat.format(Calendar.getInstance().getTime()), int1, string));
	}

	private static void info(int int1, String string) {
		if (logLevel >= 0) {
			System.out.print(String.format("%5s : %s , [%2d] > %s\n", "INFO", logDateFormat.format(Calendar.getInstance().getTime()), int1, string));
		}
	}

	private static void log(int int1, String string) {
		if (logLevel >= 1) {
			System.out.print(String.format("%5s : %s , [%2d] > %s\n", "LOG", logDateFormat.format(Calendar.getInstance().getTime()), int1, string));
		}
	}

	private static void trace(int int1, String string) {
		if (logLevel >= 2) {
			System.out.print(String.format("%5s : %s , [%2d] > %s\n", "TRACE", logDateFormat.format(Calendar.getInstance().getTime()), int1, string));
		}
	}

	public static void main(String[] stringArray) {
		String string = null;
		int int1 = -1;
		for (int int2 = 0; int2 < stringArray.length; ++int2) {
			if (stringArray[int2].startsWith("-scenarios=")) {
				string = stringArray[int2].replace("-scenarios=", "").trim();
			} else if (stringArray[int2].startsWith("-id=")) {
				int1 = Integer.parseInt(stringArray[int2].replace("-id=", "").trim());
			}
		}

		if (string == null || string.isBlank()) {
			error(-1, "Invalid scenarios file name");
			System.exit(0);
		}

		Rand.init();
		System.loadLibrary("RakNet64");
		System.loadLibrary("ZNetNoSteam64");
		try {
			String string2 = System.getProperty("zomboid.znetlog");
			if (string2 != null) {
				logLevel = Integer.parseInt(string2);
				ZNet.init();
				ZNet.setLogLevel(logLevel);
			}
		} catch (NumberFormatException numberFormatException) {
			error(-1, "Invalid log arguments");
		}

		DebugLog.disableLog(DebugType.General);
		HashMap hashMap = load(string);
		FakeClientManager.Network network;
		int int3;
		if (int1 != -1) {
			int3 = 17500 + int1;
			network = new FakeClientManager.Network(hashMap.size(), int3);
		} else {
			int3 = 17500;
			network = new FakeClientManager.Network(hashMap.size(), int3);
		}

		if (network.isStarted()) {
			HashSet hashSet = new HashSet();
			int int4 = 0;
			if (int1 != -1) {
				FakeClientManager.Movement movement = (FakeClientManager.Movement)hashMap.get(int1);
				if (movement != null) {
					hashSet.add(new FakeClientManager.Player(movement, network, int4, int3));
				} else {
					error(int1, "Client movement not found");
				}
			} else {
				Iterator iterator = hashMap.values().iterator();
				while (iterator.hasNext()) {
					FakeClientManager.Movement movement2 = (FakeClientManager.Movement)iterator.next();
					hashSet.add(new FakeClientManager.Player(movement2, network, int4++, int3));
				}
			}

			while (!hashSet.isEmpty()) {
				sleep(1000L);
			}
		}
	}

	private static class Movement {
		static String version;
		static int defaultRadius = 150;
		static int aimSpeed = 4;
		static int sneakSpeed = 6;
		static int walkSpeed = 7;
		static int sneakRunSpeed = 10;
		static int runSpeed = 13;
		static int sprintSpeed = 19;
		static int pedestrianSpeedMin = 5;
		static int pedestrianSpeedMax = 20;
		static int vehicleSpeedMin = 40;
		static int vehicleSpeedMax = 80;
		final int id;
		final String description;
		final Vector2 spawn;
		final FakeClientManager.Movement.Motion motion;
		final float speed;
		final FakeClientManager.Movement.Type type;
		final int radius;
		final IsoDirections direction;
		final Vector2 destination;
		final boolean ghost;
		final long connectDelay;
		final long disconnectDelay;
		final long reconnectDelay;
		final long teleportDelay;
		long timestamp;

		public Movement(int int1, String string, int int2, int int3, FakeClientManager.Movement.Motion motion, int int4, FakeClientManager.Movement.Type type, int int5, int int6, int int7, IsoDirections directions, boolean boolean1, long long1, long long2, long long3, long long4) {
			this.id = int1;
			this.description = string;
			this.spawn = new Vector2((float)int2, (float)int3);
			this.motion = motion;
			this.speed = (float)int4;
			this.type = type;
			this.radius = int5;
			this.direction = directions;
			this.destination = new Vector2((float)int6, (float)int7);
			this.ghost = boolean1;
			this.connectDelay = long1;
			this.disconnectDelay = long2;
			this.reconnectDelay = long3;
			this.teleportDelay = long4;
		}

		public void connect(int int1) {
			long long1 = System.currentTimeMillis();
			if (this.disconnectDelay != 0L) {
				FakeClientManager.info(this.id, String.format("Player %3d connect in %.3fs, disconnect in %.3fs", int1, (float)(long1 - this.timestamp) / 1000.0F, (float)this.disconnectDelay / 1000.0F));
			} else {
				FakeClientManager.info(this.id, String.format("Player %3d connect in %.3fs", int1, (float)(long1 - this.timestamp) / 1000.0F));
			}

			this.timestamp = long1;
		}

		public void disconnect(int int1) {
			long long1 = System.currentTimeMillis();
			if (this.reconnectDelay != 0L) {
				FakeClientManager.info(this.id, String.format("Player %3d disconnect in %.3fs, reconnect in %.3fs", int1, (float)(long1 - this.timestamp) / 1000.0F, (float)this.reconnectDelay / 1000.0F));
			} else {
				FakeClientManager.info(this.id, String.format("Player %3d disconnect in %.3fs", int1, (float)(long1 - this.timestamp) / 1000.0F));
			}

			this.timestamp = long1;
		}

		public boolean doTeleport() {
			return this.teleportDelay != 0L;
		}

		public boolean doDisconnect() {
			return this.disconnectDelay != 0L;
		}

		public boolean checkDisconnect() {
			return System.currentTimeMillis() - this.timestamp > this.disconnectDelay;
		}

		public boolean doReconnect() {
			return this.reconnectDelay != 0L;
		}

		public boolean checkReconnect() {
			return System.currentTimeMillis() - this.timestamp > this.reconnectDelay;
		}

		private static enum Motion {

			Aim,
			Sneak,
			Walk,
			SneakRun,
			Run,
			Sprint,
			Pedestrian,
			Vehicle;

			private static FakeClientManager.Movement.Motion[] $values() {
				return new FakeClientManager.Movement.Motion[]{Aim, Sneak, Walk, SneakRun, Run, Sprint, Pedestrian, Vehicle};
			}
		}
		private static enum Type {

			Line,
			Circle;

			private static FakeClientManager.Movement.Type[] $values() {
				return new FakeClientManager.Movement.Type[]{Line, Circle};
			}
		}
	}

	private static class Client {
		private static long connectionInterval = 1500L;
		private static long connectionTimeout = 10000L;
		private static long connectionDelay = 15000L;
		private static int statisticsClientID = -1;
		private static int statisticsPeriod = 1;
		private static long serverTimeShift = 0L;
		private static boolean serverTimeShiftIsSet = false;
		private final HashMap requests = new HashMap();
		private final FakeClientManager.Player player;
		private final FakeClientManager.Network network;
		private final int connectionIndex;
		private final int port;
		private long connectionGUID = -1L;
		private int requestId = 0;
		private long stateTime;
		private FakeClientManager.Client.State state;
		private String host;

		private Client(FakeClientManager.Player player, FakeClientManager.Network network, int int1, int int2) {
			this.connectionIndex = int1;
			this.network = network;
			this.player = player;
			this.port = int2;
			try {
				this.host = InetAddress.getByName("127.0." + int1 + ".1").getHostAddress();
				this.state = FakeClientManager.Client.State.CONNECT;
				Thread thread = new Thread(ThreadGroups.Workers, this::updateThread, player.username);
				thread.setDaemon(true);
				thread.start();
			} catch (UnknownHostException unknownHostException) {
				this.state = FakeClientManager.Client.State.QUIT;
				unknownHostException.printStackTrace();
			}
		}

		private void updateThread() {
			FakeClientManager.info(this.player.movement.id, String.format("Start client (%d) %s:%d => %s:%d / \"%s\"", this.connectionIndex, "0.0.0.0", this.port, this.host, 16261, this.player.movement.description));
			FakeClientManager.sleep(this.player.movement.connectDelay);
			switch (this.player.movement.type) {
			case Circle: 
				this.player.circleMovement();
				break;
			
			case Line: 
				this.player.lineMovement();
			
			}
			while (this.state != FakeClientManager.Client.State.QUIT) {
				this.update();
				FakeClientManager.sleep(1L);
			}

			FakeClientManager.info(this.player.movement.id, String.format("Stop client (%d) %s:%d => %s:%d / \"%s\"", this.connectionIndex, "0.0.0.0", this.port, this.host, 16261, this.player.movement.description));
		}

		private void updateTime() {
			this.stateTime = System.currentTimeMillis();
		}

		private long getServerTime() {
			return serverTimeShiftIsSet ? System.nanoTime() + serverTimeShift : 0L;
		}

		private boolean checkConnectionTimeout() {
			return System.currentTimeMillis() - this.stateTime > connectionTimeout;
		}

		private boolean checkConnectionDelay() {
			return System.currentTimeMillis() - this.stateTime > connectionDelay;
		}

		private void changeState(FakeClientManager.Client.State state) {
			this.updateTime();
			FakeClientManager.log(this.player.movement.id, String.format("%s >> %s", this.state, state));
			if (FakeClientManager.Client.State.RUN.equals(state)) {
				this.player.movement.connect(this.player.OnlineID);
				if (this.player.teleportLimiter == null) {
					this.player.teleportLimiter = new UpdateLimit(this.player.movement.teleportDelay);
				}

				if (this.player.movement.id == statisticsClientID) {
					this.sendTimeSync();
					this.sendInjuries();
					this.sendStatisticsEnable(statisticsPeriod);
				}
			} else if (FakeClientManager.Client.State.DISCONNECT.equals(state) && !FakeClientManager.Client.State.DISCONNECT.equals(this.state)) {
				this.player.movement.disconnect(this.player.OnlineID);
			}

			this.state = state;
		}

		private void update() {
			switch (this.state) {
			case CONNECT: 
				this.player.movement.timestamp = System.currentTimeMillis();
				this.network.connect(this.player.movement.id, this.host);
				this.changeState(FakeClientManager.Client.State.WAIT);
				break;
			
			case LOGIN: 
				this.sendPlayerLogin();
				this.changeState(FakeClientManager.Client.State.WAIT);
				break;
			
			case PLAYER_CONNECT: 
				this.sendPlayerConnect();
				this.changeState(FakeClientManager.Client.State.WAIT);
				break;
			
			case PLAYER_EXTRA_INFO: 
				this.sendPlayerExtraInfo(this.player.movement.ghost, this.player.movement.id == statisticsClientID);
				this.changeState(FakeClientManager.Client.State.WAIT);
				break;
			
			case LOAD: 
				this.requestId = 0;
				this.requests.clear();
				this.requestFullUpdate();
				this.requestLargeAreaZip();
				this.changeState(FakeClientManager.Client.State.WAIT);
				break;
			
			case RUN: 
				if (this.player.movement.doDisconnect() && this.player.movement.checkDisconnect()) {
					this.changeState(FakeClientManager.Client.State.DISCONNECT);
				} else {
					this.player.run();
				}

				break;
			
			case WAIT: 
				if (this.checkConnectionTimeout()) {
					this.changeState(FakeClientManager.Client.State.DISCONNECT);
				}

				break;
			
			case DISCONNECT: 
				if (this.network.isConnected()) {
					this.player.movement.timestamp = System.currentTimeMillis();
					this.network.disconnect(this.connectionGUID, this.player.movement.id, this.host);
				}

				if (this.player.movement.doReconnect() && this.player.movement.checkReconnect() || !this.player.movement.doReconnect() && this.checkConnectionDelay()) {
					this.changeState(FakeClientManager.Client.State.CONNECT);
				}

			
			case QUIT: 
			
			}
		}

		private void receive(short short1, ByteBuffer byteBuffer) {
			FakeClientManager.Network.logUserPacket(this.player.movement.id, short1);
			switch (short1) {
			case 1: 
			
			case 2: 
			
			case 3: 
			
			case 4: 
			
			case 5: 
			
			case 7: 
			
			case 8: 
			
			case 9: 
			
			case 11: 
			
			case 12: 
			
			case 13: 
			
			case 14: 
			
			case 15: 
			
			case 16: 
			
			case 17: 
			
			case 20: 
			
			case 22: 
			
			case 23: 
			
			case 24: 
			
			case 25: 
			
			case 27: 
			
			case 28: 
			
			case 29: 
			
			case 30: 
			
			case 31: 
			
			case 32: 
			
			case 33: 
			
			case 34: 
			
			case 35: 
			
			case 37: 
			
			case 38: 
			
			case 39: 
			
			case 40: 
			
			case 41: 
			
			case 42: 
			
			case 43: 
			
			case 44: 
			
			case 45: 
			
			case 46: 
			
			case 47: 
			
			case 48: 
			
			case 49: 
			
			case 50: 
			
			case 51: 
			
			case 52: 
			
			case 53: 
			
			case 54: 
			
			case 55: 
			
			case 56: 
			
			case 57: 
			
			case 58: 
			
			case 59: 
			
			case 60: 
			
			case 61: 
			
			case 62: 
			
			case 63: 
			
			case 64: 
			
			case 65: 
			
			case 66: 
			
			case 67: 
			
			case 68: 
			
			case 69: 
			
			case 70: 
			
			case 71: 
			
			case 72: 
			
			case 73: 
			
			case 74: 
			
			case 75: 
			
			case 76: 
			
			case 77: 
			
			case 78: 
			
			case 79: 
			
			case 80: 
			
			case 81: 
			
			case 82: 
			
			case 83: 
			
			case 85: 
			
			case 86: 
			
			case 87: 
			
			case 88: 
			
			case 89: 
			
			case 90: 
			
			case 91: 
			
			case 92: 
			
			case 93: 
			
			case 94: 
			
			case 95: 
			
			case 96: 
			
			case 97: 
			
			case 98: 
			
			case 99: 
			
			case 100: 
			
			case 101: 
			
			case 102: 
			
			case 103: 
			
			case 104: 
			
			case 105: 
			
			case 106: 
			
			case 107: 
			
			case 108: 
			
			case 109: 
			
			case 110: 
			
			case 111: 
			
			case 112: 
			
			case 113: 
			
			case 114: 
			
			case 115: 
			
			case 116: 
			
			case 117: 
			
			case 118: 
			
			case 119: 
			
			case 120: 
			
			case 121: 
			
			case 122: 
			
			case 123: 
			
			case 124: 
			
			case 125: 
			
			case 126: 
			
			case 127: 
			
			case 128: 
			
			case 129: 
			
			case 130: 
			
			case 131: 
			
			case 132: 
			
			case 133: 
			
			case 134: 
			
			case 135: 
			
			case 136: 
			
			case 137: 
			
			case 138: 
			
			case 139: 
			
			case 140: 
			
			case 141: 
			
			case 142: 
			
			case 143: 
			
			case 144: 
			
			case 145: 
			
			case 146: 
			
			case 147: 
			
			case 148: 
			
			case 149: 
			
			case 150: 
			
			case 151: 
			
			case 152: 
			
			case 153: 
			
			case 154: 
			
			case 155: 
			
			case 156: 
			
			case 157: 
			
			case 158: 
			
			case 159: 
			
			case 161: 
			
			case 162: 
			
			case 163: 
			
			case 164: 
			
			case 165: 
			
			case 166: 
			
			case 167: 
			
			case 168: 
			
			case 169: 
			
			case 170: 
			
			case 171: 
			
			case 172: 
			
			case 173: 
			
			case 174: 
			
			case 175: 
			
			case 176: 
			
			case 177: 
			
			case 178: 
			
			case 179: 
			
			case 180: 
			
			case 181: 
			
			case 182: 
			
			case 183: 
			
			case 184: 
			
			case 185: 
			
			case 186: 
			
			case 187: 
			
			case 188: 
			
			case 189: 
			
			case 190: 
			
			case 191: 
			
			case 192: 
			
			case 193: 
			
			case 194: 
			
			case 195: 
			
			case 196: 
			
			case 197: 
			
			case 198: 
			
			case 199: 
			
			case 200: 
			
			case 201: 
			
			case 202: 
			
			case 203: 
			
			case 204: 
			
			case 205: 
			
			case 206: 
			
			case 207: 
			
			case 208: 
			
			case 209: 
			
			case 210: 
			
			case 211: 
			
			case 213: 
			
			case 214: 
			
			case 215: 
			
			case 216: 
			
			default: 
				break;
			
			case 6: 
				if (this.receivePlayerConnect(byteBuffer)) {
					this.changeState(FakeClientManager.Client.State.PLAYER_EXTRA_INFO);
				}

				break;
			
			case 10: 
				this.receiveZombieUpdateInfo(byteBuffer);
				break;
			
			case 18: 
				if (this.state == FakeClientManager.Client.State.WAIT && this.receiveChunkPart(byteBuffer)) {
					this.updateTime();
					if (this.allChunkPartsReceived()) {
						this.changeState(FakeClientManager.Client.State.PLAYER_CONNECT);
					}
				}

				break;
			
			case 19: 
				this.receiveSyncClock(byteBuffer);
				break;
			
			case 21: 
				this.changeState(FakeClientManager.Client.State.LOAD);
				break;
			
			case 26: 
				short short2 = byteBuffer.getShort();
				byte byte1 = byteBuffer.get();
				short short3 = byteBuffer.getShort();
				if (short3 == this.player.OnlineID) {
					this.player.hit();
				}

				break;
			
			case 36: 
				if (this.state == FakeClientManager.Client.State.WAIT && this.receiveNotRequired(byteBuffer)) {
					this.updateTime();
					if (this.allChunkPartsReceived()) {
						this.changeState(FakeClientManager.Client.State.PLAYER_CONNECT);
					}
				}

				break;
			
			case 84: 
				if (this.receivePlayerExtraInfo(byteBuffer)) {
					this.changeState(FakeClientManager.Client.State.RUN);
				}

				break;
			
			case 160: 
				this.receiveTimeSync(byteBuffer);
				break;
			
			case 212: 
				this.receiveStatistics(byteBuffer);
			
			}
			byteBuffer.clear();
		}

		private void doPacket(short short1, ByteBuffer byteBuffer) {
			byteBuffer.put((byte)-122);
			byteBuffer.putShort(short1);
		}

		private void putUTF(ByteBuffer byteBuffer, String string) {
			if (string == null) {
				byteBuffer.putShort((short)0);
			} else {
				byte[] byteArray = string.getBytes();
				byteBuffer.putShort((short)byteArray.length);
				byteBuffer.put(byteArray);
			}
		}

		private void putBoolean(ByteBuffer byteBuffer, boolean boolean1) {
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
		}

		private void sendPlayerLogin() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)2, byteBuffer);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, FakeClientManager.versionNumber);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendPlayerConnect() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)6, byteBuffer);
			this.writePlayerConnectData(byteBuffer);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void writePlayerConnectData(ByteBuffer byteBuffer) {
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)13);
			byteBuffer.putFloat(this.player.x);
			byteBuffer.putFloat(this.player.y);
			byteBuffer.putFloat(this.player.z);
			byteBuffer.putInt(0);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, this.player.isFemale == 0 ? "Kate" : "Male");
			byteBuffer.putInt(this.player.isFemale);
			this.putUTF(byteBuffer, "fireofficer");
			byteBuffer.putInt(0);
			byteBuffer.putInt(4);
			this.putUTF(byteBuffer, "Sprinting");
			byteBuffer.putInt(1);
			this.putUTF(byteBuffer, "Fitness");
			byteBuffer.putInt(6);
			this.putUTF(byteBuffer, "Strength");
			byteBuffer.putInt(6);
			this.putUTF(byteBuffer, "Axe");
			byteBuffer.putInt(1);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)((int)Math.round(Math.random() * 5.0)));
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			int int1 = this.player.clothes.size();
			byteBuffer.put((byte)int1);
			Iterator iterator = this.player.clothes.iterator();
			while (iterator.hasNext()) {
				FakeClientManager.Player.Clothes clothes = (FakeClientManager.Player.Clothes)iterator.next();
				byteBuffer.put(clothes.flags);
				this.putUTF(byteBuffer, "Base." + clothes.name);
				this.putUTF(byteBuffer, (String)null);
				this.putUTF(byteBuffer, clothes.name);
				byteBuffer.put((byte)-1);
				byteBuffer.put((byte)-1);
				byteBuffer.put((byte)-1);
				byteBuffer.put(clothes.text);
				byteBuffer.putFloat(0.0F);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
				byteBuffer.put((byte)0);
			}

			this.putUTF(byteBuffer, "fake_str");
			byteBuffer.putShort((short)0);
			byteBuffer.putInt(2);
			this.putUTF(byteBuffer, "Fit");
			this.putUTF(byteBuffer, "Stout");
			byteBuffer.putFloat(0.0F);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(4);
			this.putUTF(byteBuffer, "Sprinting");
			byteBuffer.putFloat(75.0F);
			this.putUTF(byteBuffer, "Fitness");
			byteBuffer.putFloat(67500.0F);
			this.putUTF(byteBuffer, "Strength");
			byteBuffer.putFloat(67500.0F);
			this.putUTF(byteBuffer, "Axe");
			byteBuffer.putFloat(75.0F);
			byteBuffer.putInt(4);
			this.putUTF(byteBuffer, "Sprinting");
			byteBuffer.putInt(1);
			this.putUTF(byteBuffer, "Fitness");
			byteBuffer.putInt(6);
			this.putUTF(byteBuffer, "Strength");
			byteBuffer.putInt(6);
			this.putUTF(byteBuffer, "Axe");
			byteBuffer.putInt(1);
			byteBuffer.putInt(0);
			this.putBoolean(byteBuffer, true);
			this.putUTF(byteBuffer, "fake");
			byteBuffer.putFloat(this.player.tagColor.r);
			byteBuffer.putFloat(this.player.tagColor.g);
			byteBuffer.putFloat(this.player.tagColor.b);
			byteBuffer.putInt(0);
			byteBuffer.putDouble(0.0);
			byteBuffer.putInt(0);
			this.putUTF(byteBuffer, this.player.username);
			byteBuffer.putFloat(this.player.speakColor.r);
			byteBuffer.putFloat(this.player.speakColor.g);
			byteBuffer.putFloat(this.player.speakColor.b);
			this.putBoolean(byteBuffer, true);
			this.putBoolean(byteBuffer, false);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
		}

		private void sendPlayerExtraInfo(boolean boolean1, boolean boolean2) {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)84, byteBuffer);
			byteBuffer.putShort((short)this.player.OnlineID);
			this.putUTF(byteBuffer, boolean2 ? "admin" : "");
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private int getBooleanVariables() {
			int int1 = 0;
			if (this.player.movement.speed > 0.0F) {
				switch (this.player.movement.motion) {
				case Aim: 
					int1 |= 64;
					break;
				
				case Sneak: 
					int1 |= 1;
					break;
				
				case SneakRun: 
					int1 |= 17;
					break;
				
				case Run: 
					int1 |= 16;
					break;
				
				case Sprint: 
					int1 |= 32;
				
				}

				int1 |= 17408;
			}

			return int1;
		}

		private void sendPlayer(NetworkCharacter.Transform transform, int int1) {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)218, byteBuffer);
			byteBuffer.putShort((short)this.player.OnlineID);
			byteBuffer.putFloat(transform.position.x);
			byteBuffer.putFloat(transform.position.y);
			byteBuffer.put((byte)((int)this.player.z));
			byteBuffer.putInt(transform.time);
			byteBuffer.putFloat(transform.rotation.getDirection());
			byteBuffer.put((byte)0);
			byteBuffer.putShort((short)-1);
			byteBuffer.putShort((short)-1);
			byteBuffer.putInt(this.getBooleanVariables());
			byteBuffer.put((byte)((int)Math.min(this.player.movement.speed, 20.0F)));
			byteBuffer.putFloat(this.player.x);
			byteBuffer.putFloat(this.player.y);
			byteBuffer.put((byte)((int)this.player.z));
			byteBuffer.put((byte)IsoDirections.fromAngleActual(this.player.direction).index());
			byteBuffer.putInt(int1);
			byteBuffer.putShort((short)0);
			this.network.endPacketSuperHighUnreliable(this.connectionGUID);
		}

		private boolean receivePlayerConnect(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			if (short1 == -1) {
				byte byte1 = byteBuffer.get();
				short1 = byteBuffer.getShort();
				this.player.OnlineID = short1;
				return true;
			} else {
				return false;
			}
		}

		private boolean receivePlayerExtraInfo(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			return short1 == this.player.OnlineID;
		}

		private void receiveZombieUpdateInfo(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			for (short short2 = 0; short2 < short1; ++short2) {
				short short3 = byteBuffer.getShort();
				float float1 = byteBuffer.getFloat();
				float float2 = byteBuffer.getFloat();
				byte byte1 = byteBuffer.get();
				int int1 = byteBuffer.getInt();
				short short4 = byteBuffer.getShort();
				int int2 = byteBuffer.getInt();
				byte byte2 = byteBuffer.get();
				int int3 = byteBuffer.getInt();
				int int4 = byteBuffer.getInt();
				int int5 = byteBuffer.getInt();
				float float3 = byteBuffer.getFloat();
				float float4 = byteBuffer.getFloat();
				byte byte3 = byteBuffer.get();
				byte byte4 = byteBuffer.get();
			}
		}

		private boolean receiveChunkPart(ByteBuffer byteBuffer) {
			boolean boolean1 = false;
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			int int5 = byteBuffer.getInt();
			int int6 = byteBuffer.getInt();
			if (this.requests.remove(int1) != null) {
				boolean1 = true;
			}

			return boolean1;
		}

		private boolean receiveNotRequired(ByteBuffer byteBuffer) {
			boolean boolean1 = false;
			int int1 = byteBuffer.getInt();
			for (int int2 = 0; int2 < int1; ++int2) {
				int int3 = byteBuffer.getInt();
				boolean boolean2 = byteBuffer.get() == 1;
				if (this.requests.remove(int3) != null) {
					boolean1 = true;
				}
			}

			return boolean1;
		}

		private boolean allChunkPartsReceived() {
			return this.requests.size() == 0;
		}

		private void addChunkRequest(int int1, int int2, int int3, int int4) {
			FakeClientManager.Client.Request request = new FakeClientManager.Client.Request(int1, int2, this.requestId);
			++this.requestId;
			this.requests.put(request.id, request);
		}

		private void requestZipList() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)34, byteBuffer);
			byteBuffer.putInt(this.requests.size());
			Iterator iterator = this.requests.values().iterator();
			while (iterator.hasNext()) {
				FakeClientManager.Client.Request request = (FakeClientManager.Client.Request)iterator.next();
				byteBuffer.putInt(request.id);
				byteBuffer.putInt(request.wx);
				byteBuffer.putInt(request.wy);
				byteBuffer.putLong(request.crc);
			}

			this.network.endPacket(this.connectionGUID);
		}

		private void requestLargeAreaZip() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)24, byteBuffer);
			byteBuffer.putInt(this.player.WorldX);
			byteBuffer.putInt(this.player.WorldY);
			byteBuffer.putInt(13);
			this.network.endPacketImmediate(this.connectionGUID);
			int int1 = this.player.WorldX - 6 + 2;
			int int2 = this.player.WorldY - 6 + 2;
			int int3 = this.player.WorldX + 6 + 2;
			int int4 = this.player.WorldY + 6 + 2;
			for (int int5 = int2; int5 <= int4; ++int5) {
				for (int int6 = int1; int6 <= int3; ++int6) {
					FakeClientManager.Client.Request request = new FakeClientManager.Client.Request(int6, int5, this.requestId);
					++this.requestId;
					this.requests.put(request.id, request);
				}
			}

			this.requestZipList();
		}

		private void requestFullUpdate() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)202, byteBuffer);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void requestChunkObjectState() {
			Iterator iterator = this.requests.values().iterator();
			while (iterator.hasNext()) {
				FakeClientManager.Client.Request request = (FakeClientManager.Client.Request)iterator.next();
				ByteBuffer byteBuffer = this.network.startPacket();
				this.doPacket((short)151, byteBuffer);
				byteBuffer.putShort((short)request.wx);
				byteBuffer.putShort((short)request.wy);
				this.network.endPacket(this.connectionGUID);
			}
		}

		private void requestChunks() {
			if (!this.requests.isEmpty()) {
				this.requestZipList();
				this.requestChunkObjectState();
				this.requests.clear();
			}
		}

		private void sendStatisticsEnable(int int1) {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)212, byteBuffer);
			byteBuffer.put((byte)3);
			byteBuffer.putInt(int1);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void receiveStatistics(ByteBuffer byteBuffer) {
			long long1 = byteBuffer.getLong();
			long long2 = byteBuffer.getLong();
			long long3 = byteBuffer.getLong();
			long long4 = byteBuffer.getLong();
			long long5 = byteBuffer.getLong();
			long long6 = byteBuffer.getLong();
			long long7 = byteBuffer.getLong();
			long long8 = byteBuffer.getLong();
			long long9 = byteBuffer.getLong();
			FakeClientManager.info(this.player.movement.id, String.format("ServerStats: con=[%2d] fps=[%2d] tps=[%2d] upt=[%4d-%4d/%4d], c1=[%d] c2=[%d] c3=[%d]", long6, long4, long5, long1, long2, long3, long7, long8, long9));
		}

		private void sendTimeSync() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)160, byteBuffer);
			long long1 = System.nanoTime();
			byteBuffer.putLong(long1);
			byteBuffer.putLong(0L);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void receiveTimeSync(ByteBuffer byteBuffer) {
			long long1 = byteBuffer.getLong();
			long long2 = byteBuffer.getLong();
			long long3 = System.nanoTime();
			long long4 = long3 - long1;
			long long5 = long2 - long3 + long4 / 2L;
			long long6 = serverTimeShift;
			if (!serverTimeShiftIsSet) {
				serverTimeShift = long5;
			} else {
				serverTimeShift = (long)((float)serverTimeShift + (float)(long5 - serverTimeShift) * 0.05F);
			}

			long long7 = 10000000L;
			if (Math.abs(serverTimeShift - long6) > long7) {
				this.sendTimeSync();
			} else {
				serverTimeShiftIsSet = true;
			}
		}

		private void receiveSyncClock(ByteBuffer byteBuffer) {
			FakeClientManager.trace(this.player.movement.id, String.format("Player %3d sync clock", this.player.OnlineID));
		}

		private void sendInjuries() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket((short)179, byteBuffer);
			byteBuffer.put((byte)0);
			byteBuffer.putFloat(1.0F);
			byteBuffer.putFloat(0.0F);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private static enum State {

			CONNECT,
			LOGIN,
			PLAYER_CONNECT,
			PLAYER_EXTRA_INFO,
			LOAD,
			RUN,
			WAIT,
			DISCONNECT,
			QUIT;

			private static FakeClientManager.Client.State[] $values() {
				return new FakeClientManager.Client.State[]{CONNECT, LOGIN, PLAYER_CONNECT, PLAYER_EXTRA_INFO, LOAD, RUN, WAIT, DISCONNECT, QUIT};
			}
		}

		private static final class Request {
			private final int id;
			private final int wx;
			private final int wy;
			private final long crc;

			private Request(int int1, int int2, int int3) {
				this.id = int3;
				this.wx = int1;
				this.wy = int2;
				CRC32 cRC32 = new CRC32();
				cRC32.reset();
				cRC32.update(String.format("map_%d_%d.bin", int1, int2).getBytes());
				this.crc = cRC32.getValue();
			}
		}
	}

	private static class Player {
		private static final int cellSize = 50;
		private static final int spawnMinX = 3550;
		private static final int spawnMaxX = 14450;
		private static final int spawnMinY = 5050;
		private static final int spawnMaxY = 12950;
		private static final int ChunkGridWidth = 13;
		private static final int ChunksPerWidth = 10;
		private static int fps = 60;
		private static int predictInterval = 1000;
		private final NetworkCharacter networkCharacter;
		private final UpdateLimit updateLimiter;
		private final UpdateLimit predictLimiter;
		private final UpdateLimit timeSyncLimiter;
		private final FakeClientManager.Client client;
		private final FakeClientManager.Movement movement;
		private final ArrayList clothes;
		private final String username;
		private final int isFemale;
		private final Color tagColor;
		private final Color speakColor;
		private UpdateLimit teleportLimiter;
		private int OnlineID;
		private float x;
		private float y;
		private final float z;
		private Vector2 direction;
		private int WorldX;
		private int WorldY;
		private float angle;

		private Player(FakeClientManager.Movement movement, FakeClientManager.Network network, int int1, int int2) {
			this.username = String.format("Client%d", movement.id);
			this.tagColor = Colors.SkyBlue;
			this.speakColor = Colors.GetRandomColor();
			this.isFemale = (int)Math.round(Math.random());
			this.OnlineID = -1;
			this.clothes = new ArrayList();
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Shirt_FormalWhite"));
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)13, (byte)3, "Tie_Full"));
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Socks_Ankle"));
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)13, (byte)0, "Trousers_Suit"));
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)13, (byte)0, "Suit_Jacket"));
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Shoes_Black"));
			this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Glasses_Sun"));
			this.WorldX = (int)this.x / 10;
			this.WorldY = (int)this.y / 10;
			this.movement = movement;
			this.z = 0.0F;
			this.angle = 0.0F;
			this.x = movement.spawn.x;
			this.y = movement.spawn.y;
			this.direction = movement.direction.ToVector();
			this.networkCharacter = new NetworkCharacter();
			this.client = new FakeClientManager.Client(this, network, int1, int2);
			network.createdClients.put(int1, this.client);
			this.updateLimiter = new UpdateLimit((long)(1000 / fps));
			this.predictLimiter = new UpdateLimit((long)((float)predictInterval * 0.6F));
			this.timeSyncLimiter = new UpdateLimit(10000L);
		}

		private float getDistance(float float1) {
			return float1 / 3.6F / (float)fps;
		}

		private void teleportMovement() {
			float float1 = this.movement.destination.x;
			float float2 = this.movement.destination.y;
			FakeClientManager.info(this.movement.id, String.format("Player %3d teleport (%9.3f,%9.3f) => (%9.3f,%9.3f) / %9.3f, next in %.3fs", this.OnlineID, this.x, this.y, float1, float2, Math.sqrt(Math.pow((double)(float1 - this.x), 2.0) + Math.pow((double)(float2 - this.y), 2.0)), (float)this.movement.teleportDelay / 1000.0F));
			this.x = float1;
			this.y = float2;
			this.angle = 0.0F;
			this.teleportLimiter.Reset(this.movement.teleportDelay);
		}

		private void lineMovement() {
			float float1 = this.getDistance(this.movement.speed);
			float float2 = this.x + float1 * this.direction.x;
			float float3 = this.y + float1 * this.direction.y;
			if (float2 <= 3550.0F || float2 >= 14450.0F || float3 <= 5050.0F || float3 >= 12950.0F) {
				if (float2 < 3550.0F) {
					float2 = 3550.0F;
				}

				if (float2 > 14450.0F) {
					float2 = 14450.0F;
				}

				if (float3 < 5050.0F) {
					float3 = 5050.0F;
				}

				if (float3 > 12950.0F) {
					float3 = 12950.0F;
				}

				int int1 = IsoDirections.fromAngleActual(this.direction).index();
				this.direction = IsoDirections.fromIndex(int1 + 3).ToVector();
			}

			this.x = float2;
			this.y = float3;
		}

		private void circleMovement() {
			this.angle = (this.angle + (float)(2.0 * Math.asin((double)(this.getDistance(this.movement.speed) / 2.0F / (float)this.movement.radius)))) % 360.0F;
			float float1 = this.movement.spawn.x + (float)((double)this.movement.radius * Math.sin((double)this.angle));
			float float2 = this.movement.spawn.y + (float)((double)this.movement.radius * Math.cos((double)this.angle));
			this.x = float1;
			this.y = float2;
		}

		private void checkRequestChunks() {
			int int1 = (int)this.x / 10;
			int int2 = (int)this.y / 10;
			int int3;
			if (Math.abs(int1 - this.WorldX) < 13 && Math.abs(int2 - this.WorldY) < 13) {
				if (int1 != this.WorldX) {
					if (int1 < this.WorldX) {
						for (int3 = -6; int3 <= 6; ++int3) {
							this.client.addChunkRequest(this.WorldX - 6, this.WorldY + int3, 0, int3 + 6);
						}
					} else {
						for (int3 = -6; int3 <= 6; ++int3) {
							this.client.addChunkRequest(this.WorldX + 6, this.WorldY + int3, 12, int3 + 6);
						}
					}
				} else if (int2 != this.WorldY) {
					if (int2 < this.WorldY) {
						for (int3 = -6; int3 <= 6; ++int3) {
							this.client.addChunkRequest(this.WorldX + int3, this.WorldY - 6, int3 + 6, 0);
						}
					} else {
						for (int3 = -6; int3 <= 6; ++int3) {
							this.client.addChunkRequest(this.WorldX + int3, this.WorldY + 6, int3 + 6, 12);
						}
					}
				}
			} else {
				int3 = this.WorldX - 6;
				int int4 = this.WorldY - 6;
				int int5 = this.WorldX + 6;
				int int6 = this.WorldY + 6;
				for (int int7 = int3; int7 <= int5; ++int7) {
					for (int int8 = int4; int8 <= int6; ++int8) {
						this.client.addChunkRequest(int7, int8, int7 - int3, int8 - int4);
					}
				}
			}

			this.client.requestChunks();
			this.WorldX = int1;
			this.WorldY = int2;
		}

		private void hit() {
			FakeClientManager.info(this.movement.id, String.format("Player %3d hit", this.OnlineID));
		}

		private void run() {
			if (this.updateLimiter.Check()) {
				if (this.movement.doTeleport() && this.teleportLimiter.Check()) {
					this.teleportMovement();
				}

				switch (this.movement.type) {
				case Circle: 
					this.circleMovement();
					break;
				
				case Line: 
					this.lineMovement();
				
				}

				this.checkRequestChunks();
				int int1 = (int)(this.client.getServerTime() / 1000000L);
				this.networkCharacter.checkResetPlayer(int1);
				NetworkCharacter.Transform transform = this.networkCharacter.predict(predictInterval, int1, this.x, this.y, this.direction.x, this.direction.y);
				if (this.predictLimiter.Check()) {
					this.client.sendPlayer(transform, int1);
				}

				if (this.timeSyncLimiter.Check()) {
					this.client.sendTimeSync();
				}
			}
		}

		private static class Clothes {
			private final byte flags;
			private final byte text;
			private final String name;

			Clothes(byte byte1, byte byte2, String string) {
				this.flags = byte1;
				this.text = byte2;
				this.name = string;
			}
		}
	}

	private static class Network {
		private final HashMap createdClients = new HashMap();
		private final HashMap connectedClients = new HashMap();
		private final ByteBuffer rb = ByteBuffer.allocate(1000000);
		private final ByteBuffer wb = ByteBuffer.allocate(1000000);
		private final RakNetPeerInterface peer = new RakNetPeerInterface();
		private final int started;
		private int connected = -1;
		private static final HashMap systemPacketTypeNames = new HashMap();
		private static final HashMap userPacketTypeNames = new HashMap();

		boolean isConnected() {
			return this.connected == 0;
		}

		boolean isStarted() {
			return this.started == 0;
		}

		private Network(int int1, int int2) {
			this.peer.Init(false);
			this.peer.SetMaximumIncomingConnections(0);
			this.peer.SetClientPort(int2);
			this.peer.SetOccasionalPing(true);
			this.started = this.peer.Startup(int1);
			if (this.started == 0) {
				Thread thread = new Thread(ThreadGroups.Network, this::receiveThread, "PeerInterfaceReceive");
				thread.setDaemon(true);
				thread.start();
				FakeClientManager.log(-1, "Network start ok");
			} else {
				FakeClientManager.error(-1, String.format("Network start failed: %d", this.started));
			}
		}

		private void connect(int int1, String string) {
			this.connected = this.peer.Connect(string, 16261, PZcrypt.hash("", true));
			if (this.connected == 0) {
				FakeClientManager.log(int1, String.format("Client connected to %s:%d", string, 16261));
			} else {
				FakeClientManager.error(int1, String.format("Client connection to %s:%d failed: %d", string, 16261, this.connected));
			}
		}

		private void disconnect(long long1, int int1, String string) {
			if (long1 != 0L) {
				this.peer.disconnect(long1);
				this.connected = -1;
			}

			if (this.connected == -1) {
				FakeClientManager.log(int1, String.format("Client disconnected from %s:%d", string, 16261));
			} else {
				FakeClientManager.log(int1, String.format("Client disconnection from %s:%d failed: %d", string, 16261, long1));
			}
		}

		private ByteBuffer startPacket() {
			this.wb.clear();
			return this.wb;
		}

		private void cancelPacket() {
			this.wb.clear();
		}

		private void endPacket(long long1) {
			this.wb.flip();
			this.peer.Send(this.wb, 1, 3, (byte)0, long1, false);
		}

		private void endPacketImmediate(long long1) {
			this.wb.flip();
			this.peer.Send(this.wb, 0, 3, (byte)0, long1, false);
		}

		private void endPacketSuperHighUnreliable(long long1) {
			this.wb.flip();
			this.peer.Send(this.wb, 0, 1, (byte)0, long1, false);
		}

		private void receiveThread() {
			while (true) {
				if (this.peer.Receive(this.rb)) {
					this.decode(this.rb);
				} else {
					FakeClientManager.sleep(1L);
				}
			}
		}

		private static void logUserPacket(int int1, short short1) {
			String string = (String)userPacketTypeNames.getOrDefault(short1, "unknown user packet");
			FakeClientManager.trace(int1, String.format("## %s", string));
		}

		private static void logSystemPacket(int int1, int int2) {
			String string = (String)systemPacketTypeNames.getOrDefault(int2, "unknown system packet");
			FakeClientManager.trace(int1, String.format("# %s", string));
		}

		private void decode(ByteBuffer byteBuffer) {
			int int1 = byteBuffer.get() & 255;
			int int2 = -1;
			long long1 = -1L;
			FakeClientManager.Client client;
			switch (int1) {
			case 0: 
			
			case 1: 
			
			case 20: 
			
			case 25: 
			
			case 31: 
			
			case 33: 
			
			default: 
				break;
			
			case 16: 
				int2 = byteBuffer.get() & 255;
				long1 = this.peer.getGuidOfPacket();
				client = (FakeClientManager.Client)this.createdClients.get(int2);
				if (client != null) {
					client.connectionGUID = long1;
					this.connectedClients.put(long1, client);
					client.changeState(FakeClientManager.Client.State.LOGIN);
				}

				FakeClientManager.log(-1, String.format("Connected clients: %d (connection index %d)", this.connectedClients.size(), int2));
				break;
			
			case 17: 
			
			case 18: 
			
			case 23: 
			
			case 24: 
			
			case 32: 
				FakeClientManager.error(-1, "Connection failed: " + int1);
				break;
			
			case 19: 
				int2 = byteBuffer.get() & 255;
			
			case 44: 
			
			case 45: 
				long1 = this.peer.getGuidOfPacket();
				break;
			
			case 21: 
				int2 = byteBuffer.get() & 255;
				long1 = this.peer.getGuidOfPacket();
				client = (FakeClientManager.Client)this.connectedClients.get(long1);
				if (client != null) {
					this.connectedClients.remove(long1);
					client.changeState(FakeClientManager.Client.State.DISCONNECT);
				}

				FakeClientManager.log(-1, String.format("Connected clients: %d (connection index %d)", this.connectedClients.size(), int2));
				break;
			
			case 22: 
				int2 = byteBuffer.get() & 255;
				client = (FakeClientManager.Client)this.createdClients.get(int2);
				if (client != null) {
					client.changeState(FakeClientManager.Client.State.DISCONNECT);
				}

				break;
			
			case 134: 
				short short1 = byteBuffer.getShort();
				long1 = this.peer.getGuidOfPacket();
				client = (FakeClientManager.Client)this.connectedClients.get(long1);
				if (client != null) {
					client.receive((short)short1, byteBuffer);
					int2 = client.connectionIndex;
				}

			
			}
			logSystemPacket(int2, int1);
		}

		static  {
			systemPacketTypeNames.put(22, "connection lost");
			systemPacketTypeNames.put(21, "disconnected");
			systemPacketTypeNames.put(23, "connection banned");
			systemPacketTypeNames.put(17, "connection failed");
			systemPacketTypeNames.put(20, "no free connections");
			systemPacketTypeNames.put(16, "connection accepted");
			systemPacketTypeNames.put(18, "already connected");
			systemPacketTypeNames.put(44, "voice request");
			systemPacketTypeNames.put(45, "voice reply");
			systemPacketTypeNames.put(25, "wrong protocol version");
			systemPacketTypeNames.put(0, "connected ping");
			systemPacketTypeNames.put(1, "unconnected ping");
			systemPacketTypeNames.put(33, "new remote connection");
			systemPacketTypeNames.put(31, "remote disconnection");
			systemPacketTypeNames.put(32, "remote connection lost");
			systemPacketTypeNames.put(24, "invalid password");
			systemPacketTypeNames.put(19, "new connection");
			systemPacketTypeNames.put(134, "user packet");
		Field[] var0 = PacketTypes.class.getFields();
		Field[] var1 = var0;
		int var2 = var0.length;
		for (int var3 = 0; var3 < var2; ++var3) {
			Field var4 = var1[var3];
			if (var4.getType().equals(Short.TYPE) && Modifier.isStatic(var4.getModifiers())) {
				try {
					userPacketTypeNames.put(var4.getShort((Object)null), var4.getName());
				} catch (IllegalAccessException var6) {
					var6.printStackTrace();
				}
			}
		}
		}
	}
}
