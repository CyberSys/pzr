package zombie.network;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.Sets.SetView;
import fmod.fmod.FMODManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
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
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManager;
import zombie.core.secure.PZcrypt;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.ZNet;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.network.packets.ZombiePacket;


public class FakeClientManager {
	private static final int SERVER_PORT = 16261;
	private static final int CLIENT_PORT = 17500;
	private static final String CLIENT_ADDRESS = "0.0.0.0";
	private static final String versionNumber = Core.getInstance().getVersionNumber();
	private static final DateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private static final ThreadLocal stringUTF = ThreadLocal.withInitial(FakeClientManager.StringUTF::new);
	private static int logLevel = 0;
	private static long startTime = System.currentTimeMillis();
	private static final HashSet players = new HashSet();

	public static String ReadStringUTF(ByteBuffer byteBuffer) {
		return ((FakeClientManager.StringUTF)stringUTF.get()).load(byteBuffer);
	}

	public static void WriteStringUTF(ByteBuffer byteBuffer, String string) {
		((FakeClientManager.StringUTF)stringUTF.get()).save(byteBuffer, string);
	}

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
			if (jSONObject4.has("serverHost")) {
				FakeClientManager.Client.connectionServerHost = jSONObject4.getString("serverHost");
			}

			FakeClientManager.Client.connectionInterval = jSONObject4.getLong("interval");
			FakeClientManager.Client.connectionTimeout = jSONObject4.getLong("timeout");
			FakeClientManager.Client.connectionDelay = jSONObject4.getLong("delay");
			JSONObject jSONObject5 = jSONObject3.getJSONObject("statistics");
			FakeClientManager.Client.statisticsPeriod = jSONObject5.getInt("period");
			FakeClientManager.Client.statisticsClientID = Math.max(jSONObject5.getInt("id"), -1);
			JSONObject jSONObject6;
			if (jSONObject3.has("checksum")) {
				jSONObject6 = jSONObject3.getJSONObject("checksum");
				FakeClientManager.Client.luaChecksum = jSONObject6.getString("lua");
				FakeClientManager.Client.scriptChecksum = jSONObject6.getString("script");
			}

			int int1;
			if (jSONObject2.has("zombies")) {
				jSONObject4 = jSONObject2.getJSONObject("zombies");
				FakeClientManager.ZombieSimulator.Behaviour behaviour = FakeClientManager.ZombieSimulator.Behaviour.Normal;
				if (jSONObject4.has("behaviour")) {
					behaviour = FakeClientManager.ZombieSimulator.Behaviour.valueOf(jSONObject4.getString("behaviour"));
				}

				FakeClientManager.ZombieSimulator.behaviour = behaviour;
				if (jSONObject4.has("maxZombiesPerUpdate")) {
					FakeClientManager.ZombieSimulator.maxZombiesPerUpdate = jSONObject4.getInt("maxZombiesPerUpdate");
				}

				if (jSONObject4.has("deleteZombieDistance")) {
					int1 = jSONObject4.getInt("deleteZombieDistance");
					FakeClientManager.ZombieSimulator.deleteZombieDistanceSquared = int1 * int1;
				}

				if (jSONObject4.has("forgotZombieDistance")) {
					int1 = jSONObject4.getInt("forgotZombieDistance");
					FakeClientManager.ZombieSimulator.forgotZombieDistanceSquared = int1 * int1;
				}

				if (jSONObject4.has("canSeeZombieDistance")) {
					int1 = jSONObject4.getInt("canSeeZombieDistance");
					FakeClientManager.ZombieSimulator.canSeeZombieDistanceSquared = int1 * int1;
				}

				if (jSONObject4.has("seeZombieDistance")) {
					int1 = jSONObject4.getInt("seeZombieDistance");
					FakeClientManager.ZombieSimulator.seeZombieDistanceSquared = int1 * int1;
				}

				if (jSONObject4.has("canChangeTarget")) {
					FakeClientManager.ZombieSimulator.canChangeTarget = jSONObject4.getBoolean("canChangeTarget");
				}
			}

			jSONObject4 = jSONObject2.getJSONObject("player");
			FakeClientManager.Player.fps = jSONObject4.getInt("fps");
			FakeClientManager.Player.predictInterval = jSONObject4.getInt("predict");
			if (jSONObject4.has("damage")) {
				FakeClientManager.Player.damage = (float)jSONObject4.getDouble("damage");
			}

			if (jSONObject4.has("voip")) {
				FakeClientManager.Player.isVOIPEnabled = jSONObject4.getBoolean("voip");
			}

			jSONObject5 = jSONObject2.getJSONObject("movement");
			FakeClientManager.Movement.defaultRadius = jSONObject5.getInt("radius");
			jSONObject6 = jSONObject5.getJSONObject("motion");
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
			for (int int2 = 0; int2 < jSONArray.length(); ++int2) {
				jSONObject5 = jSONArray.getJSONObject(int2);
				int1 = jSONObject5.getInt("id");
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

				long long1 = (long)int1 * FakeClientManager.Client.connectionInterval;
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

				FakeClientManager.HordeCreator hordeCreator = null;
				int int9;
				if (jSONObject5.has("createHorde")) {
					JSONObject jSONObject11 = jSONObject5.getJSONObject("createHorde");
					int int10 = jSONObject11.getInt("count");
					int9 = jSONObject11.getInt("radius");
					long long5 = jSONObject11.getLong("interval");
					if (long5 != 0L) {
						hordeCreator = new FakeClientManager.HordeCreator(int9, int10, long5);
					}
				}

				FakeClientManager.SoundMaker soundMaker = null;
				if (jSONObject5.has("makeSound")) {
					JSONObject jSONObject12 = jSONObject5.getJSONObject("makeSound");
					int9 = jSONObject12.getInt("interval");
					int int11 = jSONObject12.getInt("radius");
					String string4 = jSONObject12.getString("message");
					if (int9 != 0) {
						soundMaker = new FakeClientManager.SoundMaker(int9, int11, string4);
					}
				}

				FakeClientManager.Movement movement = new FakeClientManager.Movement(int1, string3, int3, int4, motion, int5, type, int6, int7, int8, directions, boolean1, long1, long2, long3, long4, hordeCreator, soundMaker);
				if (hashMap.containsKey(int1)) {
					error(int1, String.format("Client %d already exists", movement.id));
				} else {
					hashMap.put(int1, movement);
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

	public static boolean isVOIPEnabled() {
		return FakeClientManager.Player.isVOIPEnabled && getOnlineID() != -1L && getConnectedGUID() != -1L;
	}

	public static long getConnectedGUID() {
		return players.isEmpty() ? -1L : ((FakeClientManager.Player)players.iterator().next()).client.connectionGUID;
	}

	public static long getOnlineID() {
		return players.isEmpty() ? -1L : (long)((FakeClientManager.Player)players.iterator().next()).OnlineID;
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

		DebugLog.setLogEnabled(DebugType.General, false);
		HashMap hashMap = load(string);
		if (FakeClientManager.Player.isVOIPEnabled) {
			FMODManager.instance.init();
			VoiceManager.instance.InitVMClient();
			VoiceManager.instance.setMode(1);
		}

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
			int int4 = 0;
			if (int1 != -1) {
				FakeClientManager.Movement movement = (FakeClientManager.Movement)hashMap.get(int1);
				if (movement != null) {
					players.add(new FakeClientManager.Player(movement, network, int4, int3));
				} else {
					error(int1, "Client movement not found");
				}
			} else {
				Iterator iterator = hashMap.values().iterator();
				while (iterator.hasNext()) {
					FakeClientManager.Movement movement2 = (FakeClientManager.Movement)iterator.next();
					players.add(new FakeClientManager.Player(movement2, network, int4++, int3));
				}
			}

			while (!players.isEmpty()) {
				sleep(1000L);
			}
		}
	}

	private static class StringUTF {
		private char[] chars;
		private ByteBuffer byteBuffer;
		private CharBuffer charBuffer;
		private CharsetEncoder ce;
		private CharsetDecoder cd;

		private int encode(String string) {
			int int1;
			if (this.chars == null || this.chars.length < string.length()) {
				int1 = (string.length() + 128 - 1) / 128 * 128;
				this.chars = new char[int1];
				this.charBuffer = CharBuffer.wrap(this.chars);
			}

			string.getChars(0, string.length(), this.chars, 0);
			this.charBuffer.limit(string.length());
			this.charBuffer.position(0);
			if (this.ce == null) {
				this.ce = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
			}

			this.ce.reset();
			int1 = (int)((double)string.length() * (double)this.ce.maxBytesPerChar());
			int1 = (int1 + 128 - 1) / 128 * 128;
			if (this.byteBuffer == null || this.byteBuffer.capacity() < int1) {
				this.byteBuffer = ByteBuffer.allocate(int1);
			}

			this.byteBuffer.clear();
			CoderResult coderResult = this.ce.encode(this.charBuffer, this.byteBuffer, true);
			return this.byteBuffer.position();
		}

		private String decode(int int1) {
			if (this.cd == null) {
				this.cd = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
			}

			this.cd.reset();
			int int2 = (int)((double)int1 * (double)this.cd.maxCharsPerByte());
			if (this.chars == null || this.chars.length < int2) {
				int int3 = (int2 + 128 - 1) / 128 * 128;
				this.chars = new char[int3];
				this.charBuffer = CharBuffer.wrap(this.chars);
			}

			this.charBuffer.clear();
			CoderResult coderResult = this.cd.decode(this.byteBuffer, this.charBuffer, true);
			return new String(this.chars, 0, this.charBuffer.position());
		}

		void save(ByteBuffer byteBuffer, String string) {
			if (string != null && !string.isEmpty()) {
				int int1 = this.encode(string);
				byteBuffer.putShort((short)int1);
				this.byteBuffer.flip();
				byteBuffer.put(this.byteBuffer);
			} else {
				byteBuffer.putShort((short)0);
			}
		}

		String load(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			if (short1 <= 0) {
				return "";
			} else {
				int int1 = (short1 + 128 - 1) / 128 * 128;
				if (this.byteBuffer == null || this.byteBuffer.capacity() < int1) {
					this.byteBuffer = ByteBuffer.allocate(int1);
				}

				this.byteBuffer.clear();
				if (byteBuffer.remaining() < short1) {
					DebugLog.General.error("GameWindow.StringUTF.load> numBytes:" + short1 + " is higher than the remaining bytes in the buffer:" + byteBuffer.remaining());
				}

				int int2 = byteBuffer.limit();
				byteBuffer.limit(byteBuffer.position() + short1);
				this.byteBuffer.put(byteBuffer);
				byteBuffer.limit(int2);
				this.byteBuffer.flip();
				return this.decode(short1);
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
		static final float zombieLungeDistanceSquared = 100.0F;
		static final float zombieWalkSpeed = 3.0F;
		static final float zombieLungeSpeed = 6.0F;
		final int id;
		final String description;
		final Vector2 spawn;
		FakeClientManager.Movement.Motion motion;
		float speed;
		final FakeClientManager.Movement.Type type;
		final int radius;
		final IsoDirections direction;
		final Vector2 destination;
		final boolean ghost;
		final long connectDelay;
		final long disconnectDelay;
		final long reconnectDelay;
		final long teleportDelay;
		final FakeClientManager.HordeCreator hordeCreator;
		FakeClientManager.SoundMaker soundMaker;
		long timestamp;

		public Movement(int int1, String string, int int2, int int3, FakeClientManager.Movement.Motion motion, int int4, FakeClientManager.Movement.Type type, int int5, int int6, int int7, IsoDirections directions, boolean boolean1, long long1, long long2, long long3, long long4, FakeClientManager.HordeCreator hordeCreator, FakeClientManager.SoundMaker soundMaker) {
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
			this.hordeCreator = hordeCreator;
			this.soundMaker = soundMaker;
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

			Stay,
			Line,
			Circle,
			AIAttackZombies,
			AIRunAwayFromZombies,
			AIRunToAnotherPlayers,
			AINormal;

			private static FakeClientManager.Movement.Type[] $values() {
				return new FakeClientManager.Movement.Type[]{Stay, Line, Circle, AIAttackZombies, AIRunAwayFromZombies, AIRunToAnotherPlayers, AINormal};
			}
		}
	}

	private static class Client {
		private static String connectionServerHost = "127.0.0.1";
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
		public static String luaChecksum = "";
		public static String scriptChecksum = "";

		private Client(FakeClientManager.Player player, FakeClientManager.Network network, int int1, int int2) {
			this.connectionIndex = int1;
			this.network = network;
			this.player = player;
			this.port = int2;
			try {
				this.host = InetAddress.getByName(connectionServerHost).getHostAddress();
				this.state = FakeClientManager.Client.State.CONNECT;
				Thread thread = new Thread(ThreadGroups.Workers, this::updateThread, this.player.username);
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
				break;
			
			case AIAttackZombies: 
				this.player.aiAttackZombiesMovement();
				break;
			
			case AIRunAwayFromZombies: 
				this.player.aiRunAwayFromZombiesMovement();
				break;
			
			case AIRunToAnotherPlayers: 
				this.player.aiRunToAnotherPlayersMovement();
				break;
			
			case AINormal: 
				this.player.aiNormalMovement();
			
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
			
			case CHECKSUM: 
				this.sendChecksum();
				this.changeState(FakeClientManager.Client.State.WAIT);
				break;
			
			case PLAYER_EXTRA_INFO: 
				this.sendPlayerExtraInfo(this.player.movement.ghost, this.player.movement.hordeCreator != null || FakeClientManager.Player.isVOIPEnabled);
				this.sendEquip();
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
			PacketTypes.PacketType packetType = (PacketTypes.PacketType)PacketTypes.packetTypes.get(short1);
			FakeClientManager.Network.logUserPacket(this.player.movement.id, short1);
			switch (packetType) {
			case PlayerConnect: 
				if (this.receivePlayerConnect(byteBuffer)) {
					if (luaChecksum.isEmpty()) {
						this.changeState(FakeClientManager.Client.State.PLAYER_EXTRA_INFO);
					} else {
						this.changeState(FakeClientManager.Client.State.CHECKSUM);
					}
				}

				break;
			
			case ConnectionDetails: 
				this.changeState(FakeClientManager.Client.State.LOAD);
				break;
			
			case ExtraInfo: 
				if (this.receivePlayerExtraInfo(byteBuffer)) {
					this.changeState(FakeClientManager.Client.State.RUN);
				}

				break;
			
			case SentChunk: 
				if (this.state == FakeClientManager.Client.State.WAIT && this.receiveChunkPart(byteBuffer)) {
					this.updateTime();
					if (this.allChunkPartsReceived()) {
						this.changeState(FakeClientManager.Client.State.PLAYER_CONNECT);
					}
				}

				break;
			
			case NotRequiredInZip: 
				if (this.state == FakeClientManager.Client.State.WAIT && this.receiveNotRequired(byteBuffer)) {
					this.updateTime();
					if (this.allChunkPartsReceived()) {
						this.changeState(FakeClientManager.Client.State.PLAYER_CONNECT);
					}
				}

			
			case HitCharacter: 
			
			default: 
				break;
			
			case StatisticRequest: 
				this.receiveStatistics(byteBuffer);
				break;
			
			case TimeSync: 
				this.receiveTimeSync(byteBuffer);
				break;
			
			case SyncClock: 
				this.receiveSyncClock(byteBuffer);
				break;
			
			case ZombieSimulation: 
			
			case ZombieSimulationReliable: 
				this.receiveZombieSimulation(byteBuffer);
				break;
			
			case PlayerUpdate: 
			
			case PlayerUpdateReliable: 
				this.player.playerManager.parsePlayer(byteBuffer);
				break;
			
			case PlayerTimeout: 
				this.player.playerManager.parsePlayerTimeout(byteBuffer);
				break;
			
			case Kicked: 
				this.receiveKicked(byteBuffer);
				break;
			
			case Checksum: 
				this.receiveChecksum(byteBuffer);
				break;
			
			case KillZombie: 
				this.receiveKillZombie(byteBuffer);
				break;
			
			case Teleport: 
				this.receiveTeleport(byteBuffer);
			
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
			this.doPacket(PacketTypes.PacketType.Login.getId(), byteBuffer);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, FakeClientManager.versionNumber);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendPlayerConnect() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.PlayerConnect.getId(), byteBuffer);
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
			this.doPacket(PacketTypes.PacketType.ExtraInfo.getId(), byteBuffer);
			byteBuffer.putShort(this.player.OnlineID);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)(FakeClientManager.Player.isVOIPEnabled ? 1 : 0));
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendSyncRadioData() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.SyncRadioData.getId(), byteBuffer);
			byteBuffer.put((byte)(FakeClientManager.Player.isVOIPEnabled ? 1 : 0));
			byteBuffer.putInt(4);
			byteBuffer.putInt(0);
			byteBuffer.putInt((int)RakVoice.GetMaxDistance());
			byteBuffer.putInt((int)this.player.x);
			byteBuffer.putInt((int)this.player.y);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendEquip() {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.Equip.getId(), byteBuffer);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)1);
			byteBuffer.putInt(16);
			byteBuffer.putShort(this.player.registry_id);
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.player.weapon_id);
			byteBuffer.put((byte)0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			byteBuffer.put((byte)0);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendChatMessage(String string) {
			ByteBuffer byteBuffer = this.network.startPacket();
			byteBuffer.putShort(this.player.OnlineID);
			byteBuffer.putInt(2);
			this.putUTF(byteBuffer, this.player.username);
			this.putUTF(byteBuffer, string);
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

		private void sendPlayer(NetworkCharacter.Transform transform, int int1, Vector2 vector2) {
			PlayerPacket playerPacket = new PlayerPacket();
			playerPacket.id = this.player.OnlineID;
			playerPacket.x = transform.position.x;
			playerPacket.y = transform.position.y;
			playerPacket.z = (byte)((int)this.player.z);
			playerPacket.direction = vector2.getDirection();
			playerPacket.usePathFinder = false;
			playerPacket.moveType = NetworkVariables.PredictionTypes.None;
			playerPacket.VehicleID = -1;
			playerPacket.VehicleSeat = -1;
			playerPacket.booleanVariables = this.getBooleanVariables();
			playerPacket.footstepSoundRadius = 0;
			playerPacket.bleedingLevel = 0;
			playerPacket.realx = this.player.x;
			playerPacket.realy = this.player.y;
			playerPacket.realz = (byte)((int)this.player.z);
			playerPacket.realdir = (byte)IsoDirections.fromAngleActual(this.player.direction).index();
			playerPacket.realt = int1;
			playerPacket.collidePointX = -1.0F;
			playerPacket.collidePointY = -1.0F;
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.PlayerUpdateReliable.getId(), byteBuffer);
			ByteBufferWriter byteBufferWriter = new ByteBufferWriter(byteBuffer);
			playerPacket.write(byteBufferWriter);
			this.network.endPacket(this.connectionGUID);
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
			this.doPacket(PacketTypes.PacketType.RequestZipList.getId(), byteBuffer);
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
			this.doPacket(PacketTypes.PacketType.RequestLargeAreaZip.getId(), byteBuffer);
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
			this.doPacket(PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.getId(), byteBuffer);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void requestChunkObjectState() {
			Iterator iterator = this.requests.values().iterator();
			while (iterator.hasNext()) {
				FakeClientManager.Client.Request request = (FakeClientManager.Client.Request)iterator.next();
				ByteBuffer byteBuffer = this.network.startPacket();
				this.doPacket(PacketTypes.PacketType.ChunkObjectState.getId(), byteBuffer);
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
			this.doPacket(PacketTypes.PacketType.StatisticRequest.getId(), byteBuffer);
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
			this.doPacket(PacketTypes.PacketType.TimeSync.getId(), byteBuffer);
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

		private void receiveKicked(ByteBuffer byteBuffer) {
			String string = FakeClientManager.ReadStringUTF(byteBuffer);
			FakeClientManager.info(this.player.movement.id, String.format("Client kicked. Reason: %s", string));
		}

		private void receiveChecksum(ByteBuffer byteBuffer) {
			FakeClientManager.trace(this.player.movement.id, String.format("Player %3d receive Checksum", this.player.OnlineID));
			short short1 = byteBuffer.getShort();
			boolean boolean1 = byteBuffer.get() == 1;
			boolean boolean2 = byteBuffer.get() == 1;
			if (short1 != 1 || !boolean1 || !boolean2) {
				FakeClientManager.info(this.player.movement.id, String.format("checksum lua: %b, script: %b", boolean1, boolean2));
			}

			this.changeState(FakeClientManager.Client.State.PLAYER_EXTRA_INFO);
		}

		private void receiveKillZombie(ByteBuffer byteBuffer) {
			FakeClientManager.trace(this.player.movement.id, String.format("Player %3d receive KillZombie", this.player.OnlineID));
			short short1 = byteBuffer.getShort();
			FakeClientManager.Zombie zombie = (FakeClientManager.Zombie)this.player.simulator.zombies.get(Integer.valueOf(short1));
			if (zombie != null) {
				this.player.simulator.zombies4Delete.add(zombie);
			}
		}

		private void receiveTeleport(ByteBuffer byteBuffer) {
			byte byte1 = byteBuffer.get();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			FakeClientManager.info(this.player.movement.id, String.format("Player %3d teleport to (%d, %d)", this.player.OnlineID, (int)float1, (int)float2));
			this.player.x = float1;
			this.player.y = float2;
		}

		private void receiveZombieSimulation(ByteBuffer byteBuffer) {
			this.player.simulator.clear();
			boolean boolean1 = byteBuffer.get() == 1;
			short short1 = byteBuffer.getShort();
			short short2;
			short short3;
			for (short2 = 0; short2 < short1; ++short2) {
				short3 = byteBuffer.getShort();
				FakeClientManager.Zombie zombie = (FakeClientManager.Zombie)this.player.simulator.zombies.get(Integer.valueOf(short3));
				this.player.simulator.zombies4Delete.add(zombie);
			}

			short2 = byteBuffer.getShort();
			for (short3 = 0; short3 < short2; ++short3) {
				short short4 = byteBuffer.getShort();
				this.player.simulator.add(short4);
			}

			this.player.simulator.receivePacket(byteBuffer);
			this.player.simulator.process();
		}

		private void sendInjuries() {
			SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
			syncInjuriesPacket.id = this.player.OnlineID;
			syncInjuriesPacket.strafeSpeed = 1.0F;
			syncInjuriesPacket.walkSpeed = 1.0F;
			syncInjuriesPacket.walkInjury = 0.0F;
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.SyncInjuries.getId(), byteBuffer);
			ByteBufferWriter byteBufferWriter = new ByteBufferWriter(byteBuffer);
			syncInjuriesPacket.write(byteBufferWriter);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendChecksum() {
			if (!luaChecksum.isEmpty()) {
				FakeClientManager.trace(this.player.movement.id, String.format("Player %3d sendChecksum", this.player.OnlineID));
				ByteBuffer byteBuffer = this.network.startPacket();
				this.doPacket(PacketTypes.PacketType.Checksum.getId(), byteBuffer);
				byteBuffer.putShort((short)1);
				this.putUTF(byteBuffer, luaChecksum);
				this.putUTF(byteBuffer, scriptChecksum);
				this.network.endPacketImmediate(this.connectionGUID);
			}
		}

		public void sendCommand(String string) {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.ReceiveCommand.getId(), byteBuffer);
			FakeClientManager.WriteStringUTF(byteBuffer, string);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendEventPacket(short short1, int int1, int int2, int int3, byte byte1, String string) {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.EventPacket.getId(), byteBuffer);
			byteBuffer.putShort(short1);
			byteBuffer.putFloat((float)int1);
			byteBuffer.putFloat((float)int2);
			byteBuffer.putFloat((float)int3);
			byteBuffer.put(byte1);
			FakeClientManager.WriteStringUTF(byteBuffer, string);
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			byteBuffer.putFloat(1.0F);
			byteBuffer.putFloat(1.0F);
			byteBuffer.putFloat(0.0F);
			byteBuffer.putInt(0);
			byteBuffer.putShort((short)0);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private void sendWorldSound4Player(int int1, int int2, int int3, int int4, int int5) {
			ByteBuffer byteBuffer = this.network.startPacket();
			this.doPacket(PacketTypes.PacketType.WorldSound.getId(), byteBuffer);
			byteBuffer.putInt(int1);
			byteBuffer.putInt(int2);
			byteBuffer.putInt(int3);
			byteBuffer.putInt(int4);
			byteBuffer.putInt(int5);
			byteBuffer.put((byte)0);
			byteBuffer.putFloat(0.0F);
			byteBuffer.putFloat(1.0F);
			byteBuffer.put((byte)0);
			this.network.endPacketImmediate(this.connectionGUID);
		}

		private static enum State {

			CONNECT,
			LOGIN,
			CHECKSUM,
			PLAYER_CONNECT,
			PLAYER_EXTRA_INFO,
			LOAD,
			RUN,
			WAIT,
			DISCONNECT,
			QUIT;

			private static FakeClientManager.Client.State[] $values() {
				return new FakeClientManager.Client.State[]{CONNECT, LOGIN, CHECKSUM, PLAYER_CONNECT, PLAYER_EXTRA_INFO, LOAD, RUN, WAIT, DISCONNECT, QUIT};
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

	private static class ZombieSimulator {
		public static FakeClientManager.ZombieSimulator.Behaviour behaviour;
		public static int deleteZombieDistanceSquared;
		public static int forgotZombieDistanceSquared;
		public static int canSeeZombieDistanceSquared;
		public static int seeZombieDistanceSquared;
		private static boolean canChangeTarget;
		private static int updatePeriod;
		private static int attackPeriod;
		public static int maxZombiesPerUpdate;
		private final ByteBuffer bb = ByteBuffer.allocate(1000000);
		private UpdateLimit updateLimiter;
		private UpdateLimit attackLimiter;
		private FakeClientManager.Player player;
		private final ZombiePacket zombiePacket;
		private HashSet authoriseZombiesCurrent;
		private HashSet authoriseZombiesLast;
		private final ArrayList unknownZombies;
		private final HashMap zombies;
		private final ArrayDeque zombies4Add;
		private final ArrayDeque zombies4Delete;
		private final HashSet authoriseZombies;
		private final ArrayDeque SendQueue;
		private static Vector2 tmpDir;

		public ZombieSimulator(FakeClientManager.Player player) {
			this.updateLimiter = new UpdateLimit((long)updatePeriod);
			this.attackLimiter = new UpdateLimit((long)attackPeriod);
			this.player = null;
			this.zombiePacket = new ZombiePacket();
			this.authoriseZombiesCurrent = new HashSet();
			this.authoriseZombiesLast = new HashSet();
			this.unknownZombies = new ArrayList();
			this.zombies = new HashMap();
			this.zombies4Add = new ArrayDeque();
			this.zombies4Delete = new ArrayDeque();
			this.authoriseZombies = new HashSet();
			this.SendQueue = new ArrayDeque();
			this.player = player;
		}

		public void becomeLocal(FakeClientManager.Zombie zombie) {
			zombie.localOwnership = true;
		}

		public void becomeRemote(FakeClientManager.Zombie zombie) {
			zombie.localOwnership = false;
		}

		public void clear() {
			HashSet hashSet = this.authoriseZombiesCurrent;
			this.authoriseZombiesCurrent = this.authoriseZombiesLast;
			this.authoriseZombiesLast = hashSet;
			this.authoriseZombiesLast.removeIf((hashSetx)->{
				return this.zombies.get(Integer.valueOf(hashSetx)) == null;
			});
			this.authoriseZombiesCurrent.clear();
		}

		public void add(short short1) {
			this.authoriseZombiesCurrent.add(short1);
		}

		public void receivePacket(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			for (short short2 = 0; short2 < short1; ++short2) {
				this.parseZombie(byteBuffer);
			}
		}

		private void parseZombie(ByteBuffer byteBuffer) {
			ZombiePacket zombiePacket = this.zombiePacket;
			zombiePacket.parse(byteBuffer, (UdpConnection)null);
			FakeClientManager.Zombie zombie = (FakeClientManager.Zombie)this.zombies.get(Integer.valueOf(zombiePacket.id));
			if (!this.authoriseZombies.contains(zombiePacket.id) || zombie == null) {
				if (zombie == null) {
					zombie = new FakeClientManager.Zombie(zombiePacket.id);
					this.zombies4Add.add(zombie);
					FakeClientManager.trace(this.player.movement.id, String.format("New zombie %s", zombie.OnlineID));
				}

				zombie.lastUpdate = System.currentTimeMillis();
				zombie.zombiePacket.copy(zombiePacket);
				zombie.x = zombiePacket.realX;
				zombie.y = zombiePacket.realY;
				zombie.z = (float)zombiePacket.realZ;
			}
		}

		public void process() {
			SetView setView = Sets.difference(this.authoriseZombiesCurrent, this.authoriseZombiesLast);
			UnmodifiableIterator unmodifiableIterator = setView.iterator();
			while (unmodifiableIterator.hasNext()) {
				Short Short1 = (Short)unmodifiableIterator.next();
				FakeClientManager.Zombie zombie = (FakeClientManager.Zombie)this.zombies.get(Integer.valueOf(Short1));
				if (zombie != null) {
					this.becomeLocal(zombie);
				} else if (!this.unknownZombies.contains(Short1)) {
					this.unknownZombies.add(Short1);
				}
			}

			SetView setView2 = Sets.difference(this.authoriseZombiesLast, this.authoriseZombiesCurrent);
			UnmodifiableIterator unmodifiableIterator2 = setView2.iterator();
			while (unmodifiableIterator2.hasNext()) {
				Short Short2 = (Short)unmodifiableIterator2.next();
				FakeClientManager.Zombie zombie2 = (FakeClientManager.Zombie)this.zombies.get(Integer.valueOf(Short2));
				if (zombie2 != null) {
					this.becomeRemote(zombie2);
				}
			}

			synchronized (this.authoriseZombies) {
				this.authoriseZombies.clear();
				this.authoriseZombies.addAll(this.authoriseZombiesCurrent);
			}
		}

		public void send() {
			if (this.authoriseZombies.size() != 0 || this.unknownZombies.size() != 0) {
				FakeClientManager.Zombie zombie;
				if (this.SendQueue.isEmpty()) {
					synchronized (this.authoriseZombies) {
						Iterator iterator = this.authoriseZombies.iterator();
						while (iterator.hasNext()) {
							Short Short1 = (Short)iterator.next();
							zombie = (FakeClientManager.Zombie)this.zombies.get(Integer.valueOf(Short1));
							if (zombie != null && zombie.OnlineID != -1) {
								this.SendQueue.add(zombie);
							}
						}
					}
				}

				this.bb.clear();
				this.bb.putShort((short)0);
				int int1 = this.unknownZombies.size();
				this.bb.putShort((short)int1);
				int int2;
				for (int2 = 0; int2 < this.unknownZombies.size(); ++int2) {
					if (this.unknownZombies.get(int2) == null) {
						return;
					}

					this.bb.putShort((Short)this.unknownZombies.get(int2));
				}

				this.unknownZombies.clear();
				int2 = this.bb.position();
				this.bb.putShort((short)maxZombiesPerUpdate);
				int int3 = 0;
				while (!this.SendQueue.isEmpty()) {
					zombie = (FakeClientManager.Zombie)this.SendQueue.poll();
					if (zombie.OnlineID != -1) {
						zombie.zombiePacket.write(this.bb);
						++int3;
						if (int3 >= maxZombiesPerUpdate) {
							break;
						}
					}
				}

				if (int3 < maxZombiesPerUpdate) {
					int int4 = this.bb.position();
					this.bb.position(int2);
					this.bb.putShort((short)int3);
					this.bb.position(int4);
				}

				if (int3 > 0 || int1 > 0) {
					ByteBuffer byteBuffer = this.player.client.network.startPacket();
					this.player.client.doPacket(PacketTypes.PacketType.ZombieSimulation.getId(), byteBuffer);
					byteBuffer.put(this.bb.array(), 0, this.bb.position());
					this.player.client.network.endPacketSuperHighUnreliable(this.player.client.connectionGUID);
				}
			}
		}

		private void simulate(Integer integer, FakeClientManager.Zombie zombie) {
			float float1 = IsoUtils.DistanceToSquared(this.player.x, this.player.y, zombie.x, zombie.y);
			if (!(float1 > (float)deleteZombieDistanceSquared) && (zombie.localOwnership || zombie.lastUpdate + 5000L >= System.currentTimeMillis())) {
				tmpDir.set(-zombie.x + this.player.x, -zombie.y + this.player.y);
				float float2;
				if (zombie.isMoving) {
					float2 = 0.2F;
					zombie.x = PZMath.lerp(zombie.x, zombie.zombiePacket.x, float2);
					zombie.y = PZMath.lerp(zombie.y, zombie.zombiePacket.y, float2);
					zombie.z = 0.0F;
					zombie.dir = IsoDirections.fromAngle(tmpDir);
				}

				if (canChangeTarget) {
					synchronized (this.player.playerManager.players) {
						Iterator iterator = this.player.playerManager.players.values().iterator();
						while (iterator.hasNext()) {
							FakeClientManager.PlayerManager.RemotePlayer remotePlayer = (FakeClientManager.PlayerManager.RemotePlayer)iterator.next();
							float float3 = IsoUtils.DistanceToSquared(remotePlayer.x, remotePlayer.y, zombie.x, zombie.y);
							if (float3 < (float)seeZombieDistanceSquared) {
								zombie.zombiePacket.target = remotePlayer.OnlineID;
								break;
							}
						}
					}
				} else {
					zombie.zombiePacket.target = this.player.OnlineID;
				}

				if (behaviour == FakeClientManager.ZombieSimulator.Behaviour.Stay) {
					zombie.isMoving = false;
				} else if (behaviour == FakeClientManager.ZombieSimulator.Behaviour.Normal) {
					if (float1 > (float)forgotZombieDistanceSquared) {
						zombie.isMoving = false;
					}

					if (float1 < (float)canSeeZombieDistanceSquared && (Rand.Next(100) < 1 || zombie.dir == IsoDirections.fromAngle(tmpDir))) {
						zombie.isMoving = true;
					}

					if (float1 < (float)seeZombieDistanceSquared) {
						zombie.isMoving = true;
					}
				} else {
					zombie.isMoving = true;
				}

				float2 = 0.0F;
				if (zombie.isMoving) {
					Vector2 vector2 = zombie.dir.ToVector();
					float2 = 3.0F;
					if (float1 < 100.0F) {
						float2 = 6.0F;
					}

					long long1 = System.currentTimeMillis() - zombie.lastUpdate;
					zombie.zombiePacket.x = zombie.x + vector2.x * (float)long1 * 0.001F * float2;
					zombie.zombiePacket.y = zombie.y + vector2.y * (float)long1 * 0.001F * float2;
					zombie.zombiePacket.z = (byte)((int)zombie.z);
					zombie.zombiePacket.moveType = NetworkVariables.PredictionTypes.Moving;
				} else {
					zombie.zombiePacket.x = zombie.x;
					zombie.zombiePacket.y = zombie.y;
					zombie.zombiePacket.z = (byte)((int)zombie.z);
					zombie.zombiePacket.moveType = NetworkVariables.PredictionTypes.Static;
				}

				zombie.zombiePacket.booleanVariables = 0;
				if (float1 < 100.0F) {
					ZombiePacket zombiePacket = zombie.zombiePacket;
					zombiePacket.booleanVariables = (short)(zombiePacket.booleanVariables | 2);
				}

				zombie.zombiePacket.timeSinceSeenFlesh = zombie.isMoving ? 0 : 100000;
				zombie.zombiePacket.smParamTargetAngle = 0;
				zombie.zombiePacket.speedMod = 1000;
				zombie.zombiePacket.walkType = NetworkVariables.WalkType.values()[zombie.walkType];
				zombie.zombiePacket.realX = zombie.x;
				zombie.zombiePacket.realY = zombie.y;
				zombie.zombiePacket.realZ = (byte)((int)zombie.z);
				zombie.zombiePacket.realHealth = (short)((int)(zombie.health * 1000.0F));
				zombie.zombiePacket.realState = NetworkVariables.ZombieState.fromString("fakezombie-" + behaviour.toString().toLowerCase());
				if (zombie.isMoving) {
					zombie.zombiePacket.pfbType = 1;
					zombie.zombiePacket.pfbTarget = this.player.OnlineID;
				} else {
					zombie.zombiePacket.pfbType = 0;
				}

				if (float1 < 2.0F && this.attackLimiter.Check()) {
					zombie.health -= FakeClientManager.Player.damage;
					this.sendHitCharacter(zombie, FakeClientManager.Player.damage);
					if (zombie.health <= 0.0F) {
						this.player.client.sendChatMessage("DIE!!");
						this.zombies4Delete.add(zombie);
					}
				}

				zombie.lastUpdate = System.currentTimeMillis();
			} else {
				this.zombies4Delete.add(zombie);
			}
		}

		private void writeHitInfoToZombie(ByteBuffer byteBuffer, short short1, float float1, float float2, float float3) {
			byteBuffer.put((byte)2);
			byteBuffer.putShort(short1);
			byteBuffer.put((byte)0);
			byteBuffer.putFloat(float1);
			byteBuffer.putFloat(float2);
			byteBuffer.putFloat(0.0F);
			byteBuffer.putFloat(float3);
			byteBuffer.putFloat(1.0F);
			byteBuffer.putInt(100);
		}

		private void sendHitCharacter(FakeClientManager.Zombie zombie, float float1) {
			boolean boolean1 = false;
			ByteBuffer byteBuffer = this.player.client.network.startPacket();
			this.player.client.doPacket(PacketTypes.PacketType.HitCharacter.getId(), byteBuffer);
			byteBuffer.put((byte)3);
			byteBuffer.putShort(this.player.OnlineID);
			byteBuffer.putShort((short)0);
			byteBuffer.putFloat(this.player.x);
			byteBuffer.putFloat(this.player.y);
			byteBuffer.putFloat(this.player.z);
			byteBuffer.putFloat(this.player.direction.x);
			byteBuffer.putFloat(this.player.direction.y);
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			byteBuffer.putShort((short)((this.player.weapon_isBareHeads ? 2 : 0) + (boolean1 ? 8 : 0)));
			byteBuffer.putFloat(1.0F);
			byteBuffer.putFloat(1.0F);
			byteBuffer.putFloat(1.0F);
			FakeClientManager.WriteStringUTF(byteBuffer, "default");
			byte byte1 = 0;
			byte byte2 = (byte)(byte1 | (byte)(this.player.weapon_isBareHeads ? 9 : 0));
			byteBuffer.put(byte2);
			byteBuffer.put((byte)0);
			byteBuffer.putShort((short)0);
			byteBuffer.putFloat(1.0F);
			byteBuffer.putInt(0);
			byte byte3 = 1;
			byteBuffer.put(byte3);
			int int1;
			for (int1 = 0; int1 < byte3; ++int1) {
				this.writeHitInfoToZombie(byteBuffer, zombie.OnlineID, zombie.x, zombie.y, float1);
			}

			byte3 = 0;
			byteBuffer.put(byte3);
			byte3 = 1;
			byteBuffer.put(byte3);
			for (int1 = 0; int1 < byte3; ++int1) {
				this.writeHitInfoToZombie(byteBuffer, zombie.OnlineID, zombie.x, zombie.y, float1);
			}

			if (!this.player.weapon_isBareHeads) {
				byteBuffer.put((byte)0);
			} else {
				byteBuffer.put((byte)1);
				byteBuffer.putShort(this.player.registry_id);
				byteBuffer.put((byte)1);
				byteBuffer.putInt(this.player.weapon_id);
				byteBuffer.put((byte)0);
				byteBuffer.putInt(0);
				byteBuffer.putInt(0);
			}

			byteBuffer.putShort(zombie.OnlineID);
			byteBuffer.putShort((short)(float1 >= zombie.health ? 3 : 0));
			byteBuffer.putFloat(zombie.x);
			byteBuffer.putFloat(zombie.y);
			byteBuffer.putFloat(zombie.z);
			byteBuffer.putFloat(zombie.dir.ToVector().x);
			byteBuffer.putFloat(zombie.dir.ToVector().y);
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			byteBuffer.putShort((short)0);
			FakeClientManager.WriteStringUTF(byteBuffer, "");
			FakeClientManager.WriteStringUTF(byteBuffer, "FRONT");
			byteBuffer.put((byte)0);
			byteBuffer.putFloat(float1);
			byteBuffer.putFloat(1.0F);
			byteBuffer.putFloat(this.player.direction.x);
			byteBuffer.putFloat(this.player.direction.y);
			byteBuffer.putFloat(1.0F);
			byteBuffer.put((byte)0);
			if (tmpDir.getLength() > 0.0F) {
				zombie.dropPositionX = zombie.x + tmpDir.x / tmpDir.getLength();
				zombie.dropPositionY = zombie.y + tmpDir.y / tmpDir.getLength();
			} else {
				zombie.dropPositionX = zombie.x;
				zombie.dropPositionY = zombie.y;
			}

			byteBuffer.putFloat(zombie.dropPositionX);
			byteBuffer.putFloat(zombie.dropPositionY);
			byteBuffer.put((byte)((int)zombie.z));
			byteBuffer.putFloat(zombie.dir.toAngle());
			this.player.client.network.endPacketImmediate(this.player.client.connectionGUID);
		}

		private void sendSendDeadZombie(FakeClientManager.Zombie zombie) {
			ByteBuffer byteBuffer = this.player.client.network.startPacket();
			this.player.client.doPacket(PacketTypes.PacketType.ZombieDeath.getId(), byteBuffer);
			byteBuffer.putShort(zombie.OnlineID);
			byteBuffer.putFloat(zombie.x);
			byteBuffer.putFloat(zombie.y);
			byteBuffer.putFloat(zombie.z);
			byteBuffer.putFloat(zombie.dir.toAngle());
			byteBuffer.put((byte)zombie.dir.index());
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			this.player.client.network.endPacketImmediate(this.player.client.connectionGUID);
		}

		public void simulateAll() {
			FakeClientManager.Zombie zombie;
			while (!this.zombies4Add.isEmpty()) {
				zombie = (FakeClientManager.Zombie)this.zombies4Add.poll();
				this.zombies.put(Integer.valueOf(zombie.OnlineID), zombie);
			}

			this.zombies.forEach(this::simulate);
			while (!this.zombies4Delete.isEmpty()) {
				zombie = (FakeClientManager.Zombie)this.zombies4Delete.poll();
				this.zombies.remove(Integer.valueOf(zombie.OnlineID));
			}
		}

		public void update() {
			if (this.updateLimiter.Check()) {
				this.simulateAll();
				this.send();
			}
		}

		static  {
			behaviour = FakeClientManager.ZombieSimulator.Behaviour.Stay;
			deleteZombieDistanceSquared = 10000;
			forgotZombieDistanceSquared = 225;
			canSeeZombieDistanceSquared = 100;
			seeZombieDistanceSquared = 25;
			canChangeTarget = true;
			updatePeriod = 100;
			attackPeriod = 1000;
			maxZombiesPerUpdate = 300;
			tmpDir = new Vector2();
		}

		private static enum Behaviour {

			Stay,
			Normal,
			Attack;

			private static FakeClientManager.ZombieSimulator.Behaviour[] $values() {
				return new FakeClientManager.ZombieSimulator.Behaviour[]{Stay, Normal, Attack};
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
		private static float damage = 1.0F;
		private static boolean isVOIPEnabled = false;
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
		private short OnlineID;
		private float x;
		private float y;
		private final float z;
		private Vector2 direction;
		private int WorldX;
		private int WorldY;
		private float angle;
		private FakeClientManager.ZombieSimulator simulator;
		private FakeClientManager.PlayerManager playerManager;
		private boolean weapon_isBareHeads = false;
		private int weapon_id = 837602032;
		private short registry_id = 1202;
		static float distance = 0.0F;
		private int lastPlayerForHello = -1;

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
			this.simulator = new FakeClientManager.ZombieSimulator(this);
			this.playerManager = new FakeClientManager.PlayerManager(this);
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
			distance = this.getDistance(this.movement.speed);
			this.direction.set(this.movement.destination.x - this.x, this.movement.destination.y - this.y);
			this.direction.normalize();
			float float1 = this.x + distance * this.direction.x;
			float float2 = this.y + distance * this.direction.y;
			if (this.x < this.movement.destination.x && float1 > this.movement.destination.x || this.x > this.movement.destination.x && float1 < this.movement.destination.x || this.y < this.movement.destination.y && float2 > this.movement.destination.y || this.y > this.movement.destination.y && float2 < this.movement.destination.y) {
				float1 = this.movement.destination.x;
				float2 = this.movement.destination.y;
			}

			this.x = float1;
			this.y = float2;
		}

		private void circleMovement() {
			this.angle = (this.angle + (float)(2.0 * Math.asin((double)(this.getDistance(this.movement.speed) / 2.0F / (float)this.movement.radius)))) % 360.0F;
			float float1 = this.movement.spawn.x + (float)((double)this.movement.radius * Math.sin((double)this.angle));
			float float2 = this.movement.spawn.y + (float)((double)this.movement.radius * Math.cos((double)this.angle));
			this.x = float1;
			this.y = float2;
		}

		private FakeClientManager.Zombie getNearestZombie() {
			FakeClientManager.Zombie zombie = null;
			float float1 = Float.POSITIVE_INFINITY;
			Iterator iterator = this.simulator.zombies.values().iterator();
			while (iterator.hasNext()) {
				FakeClientManager.Zombie zombie2 = (FakeClientManager.Zombie)iterator.next();
				float float2 = IsoUtils.DistanceToSquared(this.x, this.y, zombie2.x, zombie2.y);
				if (float2 < float1) {
					zombie = zombie2;
					float1 = float2;
				}
			}

			return zombie;
		}

		private FakeClientManager.Zombie getNearestZombie(FakeClientManager.PlayerManager.RemotePlayer remotePlayer) {
			FakeClientManager.Zombie zombie = null;
			float float1 = Float.POSITIVE_INFINITY;
			Iterator iterator = this.simulator.zombies.values().iterator();
			while (iterator.hasNext()) {
				FakeClientManager.Zombie zombie2 = (FakeClientManager.Zombie)iterator.next();
				float float2 = IsoUtils.DistanceToSquared(remotePlayer.x, remotePlayer.y, zombie2.x, zombie2.y);
				if (float2 < float1) {
					zombie = zombie2;
					float1 = float2;
				}
			}

			return zombie;
		}

		private FakeClientManager.PlayerManager.RemotePlayer getNearestPlayer() {
			FakeClientManager.PlayerManager.RemotePlayer remotePlayer = null;
			float float1 = Float.POSITIVE_INFINITY;
			synchronized (this.playerManager.players) {
				Iterator iterator = this.playerManager.players.values().iterator();
				while (iterator.hasNext()) {
					FakeClientManager.PlayerManager.RemotePlayer remotePlayer2 = (FakeClientManager.PlayerManager.RemotePlayer)iterator.next();
					float float2 = IsoUtils.DistanceToSquared(this.x, this.y, remotePlayer2.x, remotePlayer2.y);
					if (float2 < float1) {
						remotePlayer = remotePlayer2;
						float1 = float2;
					}
				}

				return remotePlayer;
			}
		}

		private void aiAttackZombiesMovement() {
			FakeClientManager.Zombie zombie = this.getNearestZombie();
			float float1 = this.getDistance(this.movement.speed);
			if (zombie != null) {
				this.direction.set(zombie.x - this.x, zombie.y - this.y);
				this.direction.normalize();
			}

			float float2 = this.x + float1 * this.direction.x;
			float float3 = this.y + float1 * this.direction.y;
			this.x = float2;
			this.y = float3;
		}

		private void aiRunAwayFromZombiesMovement() {
			FakeClientManager.Zombie zombie = this.getNearestZombie();
			float float1 = this.getDistance(this.movement.speed);
			if (zombie != null) {
				this.direction.set(this.x - zombie.x, this.y - zombie.y);
				this.direction.normalize();
			}

			float float2 = this.x + float1 * this.direction.x;
			float float3 = this.y + float1 * this.direction.y;
			this.x = float2;
			this.y = float3;
		}

		private void aiRunToAnotherPlayersMovement() {
			FakeClientManager.PlayerManager.RemotePlayer remotePlayer = this.getNearestPlayer();
			float float1 = this.getDistance(this.movement.speed);
			float float2 = this.x + float1 * this.direction.x;
			float float3 = this.y + float1 * this.direction.y;
			if (remotePlayer != null) {
				this.direction.set(remotePlayer.x - this.x, remotePlayer.y - this.y);
				float float4 = this.direction.normalize();
				if (float4 > 2.0F) {
					this.x = float2;
					this.y = float3;
				} else if (this.lastPlayerForHello != remotePlayer.OnlineID) {
					this.lastPlayerForHello = remotePlayer.OnlineID;
				}
			}
		}

		private void aiNormalMovement() {
			float float1 = this.getDistance(this.movement.speed);
			FakeClientManager.PlayerManager.RemotePlayer remotePlayer = this.getNearestPlayer();
			if (remotePlayer == null) {
				this.aiRunAwayFromZombiesMovement();
			} else {
				float float2 = IsoUtils.DistanceToSquared(this.x, this.y, remotePlayer.x, remotePlayer.y);
				if (float2 > 36.0F) {
					this.movement.speed = 13.0F;
					this.movement.motion = FakeClientManager.Movement.Motion.Run;
				} else {
					this.movement.speed = 4.0F;
					this.movement.motion = FakeClientManager.Movement.Motion.Walk;
				}

				FakeClientManager.Zombie zombie = this.getNearestZombie();
				float float3 = Float.POSITIVE_INFINITY;
				if (zombie != null) {
					float3 = IsoUtils.DistanceToSquared(this.x, this.y, zombie.x, zombie.y);
				}

				FakeClientManager.Zombie zombie2 = this.getNearestZombie(remotePlayer);
				float float4 = Float.POSITIVE_INFINITY;
				if (zombie2 != null) {
					float4 = IsoUtils.DistanceToSquared(remotePlayer.x, remotePlayer.y, zombie2.x, zombie2.y);
				}

				if (float4 < 25.0F) {
					zombie = zombie2;
					float3 = float4;
				}

				if (!(float2 > 25.0F) && zombie != null) {
					if (float3 < 25.0F) {
						this.direction.set(zombie.x - this.x, zombie.y - this.y);
						this.direction.normalize();
						this.x += float1 * this.direction.x;
						this.y += float1 * this.direction.y;
					}
				} else {
					this.direction.set(remotePlayer.x - this.x, remotePlayer.y - this.y);
					float float5 = this.direction.normalize();
					if (float5 > 4.0F) {
						float float6 = this.x + float1 * this.direction.x;
						float float7 = this.y + float1 * this.direction.y;
						this.x = float6;
						this.y = float7;
					} else if (this.lastPlayerForHello != remotePlayer.OnlineID) {
						this.lastPlayerForHello = remotePlayer.OnlineID;
					}
				}
			}
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
			this.simulator.update();
			if (this.updateLimiter.Check()) {
				if (isVOIPEnabled) {
					FMODManager.instance.tick();
					VoiceManager.instance.update();
				}

				if (this.movement.doTeleport() && this.teleportLimiter.Check()) {
					this.teleportMovement();
				}

				switch (this.movement.type) {
				case Circle: 
					this.circleMovement();
					break;
				
				case Line: 
					this.lineMovement();
					break;
				
				case AIAttackZombies: 
					this.aiAttackZombiesMovement();
					break;
				
				case AIRunAwayFromZombies: 
					this.aiRunAwayFromZombiesMovement();
					break;
				
				case AIRunToAnotherPlayers: 
					this.aiRunToAnotherPlayersMovement();
					break;
				
				case AINormal: 
					this.aiNormalMovement();
				
				}

				this.checkRequestChunks();
				if (this.predictLimiter.Check()) {
					int int1 = (int)(this.client.getServerTime() / 1000000L);
					this.networkCharacter.checkResetPlayer(int1);
					NetworkCharacter.Transform transform = this.networkCharacter.predict(predictInterval, int1, this.x, this.y, this.direction.x, this.direction.y);
					this.client.sendPlayer(transform, int1, this.direction);
				}

				if (this.timeSyncLimiter.Check()) {
					this.client.sendTimeSync();
					this.client.sendSyncRadioData();
				}

				if (this.movement.hordeCreator != null && this.movement.hordeCreator.hordeCreatorLimiter.Check()) {
					this.client.sendCommand(this.movement.hordeCreator.getCommand((int)this.x, (int)this.y, (int)this.z));
				}

				if (this.movement.soundMaker != null && this.movement.soundMaker.soundMakerLimiter.Check()) {
					this.client.sendWorldSound4Player((int)this.x, (int)this.y, (int)this.z, this.movement.soundMaker.radius, this.movement.soundMaker.radius);
					this.client.sendChatMessage(this.movement.soundMaker.message);
					this.client.sendEventPacket(this.OnlineID, (int)this.x, (int)this.y, (int)this.z, (byte)4, "shout");
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

	private static class HordeCreator {
		private final int radius;
		private final int count;
		private final long interval;
		private final UpdateLimit hordeCreatorLimiter;

		public HordeCreator(int int1, int int2, long long1) {
			this.radius = int1;
			this.count = int2;
			this.interval = long1;
			this.hordeCreatorLimiter = new UpdateLimit(long1);
		}

		public String getCommand(int int1, int int2, int int3) {
			return String.format("/createhorde2 -x %d -y %d -z %d -count %d -radius %d -crawler false -isFallOnFront false -isFakeDead false -knockedDown false -health 1 -outfit", int1, int2, int3, this.count, this.radius);
		}
	}

	private static class SoundMaker {
		private final int radius;
		private final int interval;
		private final String message;
		private final UpdateLimit soundMakerLimiter;

		public SoundMaker(int int1, int int2, String string) {
			this.radius = int2;
			this.message = string;
			this.interval = int1;
			this.soundMakerLimiter = new UpdateLimit((long)int1);
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
			PacketTypes.PacketType packetType = (PacketTypes.PacketType)PacketTypes.packetTypes.get(short1);
			String string = packetType == null ? "unknown user packet" : packetType.name();
			FakeClientManager.trace(int1, String.format("## %s (%d)", string, short1));
		}

		private static void logSystemPacket(int int1, int int2) {
			String string = (String)systemPacketTypeNames.getOrDefault(int2, "unknown system packet");
			FakeClientManager.trace(int1, String.format("## %s (%d)", string, int2));
		}

		private void decode(ByteBuffer byteBuffer) {
			int int1 = byteBuffer.get() & 255;
			byte byte1 = -1;
			long long1 = -1L;
			logSystemPacket(byte1, int1);
			FakeClientManager.Client client;
			int int2;
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
					VoiceManager.instance.VoiceConnectReq(long1);
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
		}
	}

	private static class PlayerManager {
		private FakeClientManager.Player player = null;
		private final PlayerPacket playerPacket = new PlayerPacket();
		public final HashMap players = new HashMap();

		public PlayerManager(FakeClientManager.Player player) {
			this.player = player;
		}

		private void parsePlayer(ByteBuffer byteBuffer) {
			PlayerPacket playerPacket = this.playerPacket;
			playerPacket.parse(byteBuffer, (UdpConnection)null);
			synchronized (this.players) {
				FakeClientManager.PlayerManager.RemotePlayer remotePlayer = (FakeClientManager.PlayerManager.RemotePlayer)this.players.get(playerPacket.id);
				if (remotePlayer == null) {
					remotePlayer = new FakeClientManager.PlayerManager.RemotePlayer(playerPacket.id);
					this.players.put(Integer.valueOf(playerPacket.id), remotePlayer);
					FakeClientManager.trace(this.player.movement.id, String.format("New player %s", remotePlayer.OnlineID));
				}

				remotePlayer.playerPacket.copy(playerPacket);
				remotePlayer.x = playerPacket.realx;
				remotePlayer.y = playerPacket.realy;
				remotePlayer.z = (float)playerPacket.realz;
			}
		}

		private void parsePlayerTimeout(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			synchronized (this.players) {
				this.players.remove(short1);
			}
			FakeClientManager.trace(this.player.movement.id, String.format("Remove player %s", short1));
		}

		private class RemotePlayer {
			public float x;
			public float y;
			public float z;
			public short OnlineID;
			public PlayerPacket playerPacket = null;

			public RemotePlayer(short short1) {
				this.playerPacket = new PlayerPacket();
				this.playerPacket.id = short1;
				this.OnlineID = short1;
			}
		}
	}

	private static class Zombie {
		public long lastUpdate;
		public float x;
		public float y;
		public float z;
		public short OnlineID;
		public boolean localOwnership = false;
		public ZombiePacket zombiePacket = null;
		public IsoDirections dir;
		public float health;
		public byte walkType;
		public float dropPositionX;
		public float dropPositionY;
		public boolean isMoving;

		public Zombie(short short1) {
			this.dir = IsoDirections.N;
			this.health = 1.0F;
			this.walkType = (byte)Rand.Next(NetworkVariables.WalkType.values().length);
			this.isMoving = false;
			this.zombiePacket = new ZombiePacket();
			this.zombiePacket.id = short1;
			this.OnlineID = short1;
			this.localOwnership = false;
		}
	}
}
