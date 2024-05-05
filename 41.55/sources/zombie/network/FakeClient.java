package zombie.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.CRC32;
import zombie.GameTime;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.secure.PZcrypt;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;


public class FakeClient {
	private static final int PORT = 16261;
	private static final String IP = "localhost";
	private static final String PASSWORD = "";
	private static final String versionNumber = Core.getInstance().getVersionNumber();
	private static final Vector2 defaultSpawnPoint = new Vector2(9000.0F, 9000.0F);
	private static final int step = 300;
	private static final HashMap spawnPoints = new HashMap();
	private static final long stateWait = 60000L;
	private static int ID;
	private static long stateTime;
	private static long startTime;
	private static long connectionTime;
	private static boolean logNetwork;
	private static FakeClient.Player player;
	private static FakeClient.Networking networking;
	public static ArrayList startPositionsCircle = new ArrayList();
	public static Vector2[] startPositions;
	private static final DateFormat logDateFormat;
	private static final UpdateLimit logUpdateLimit;

	private static Vector2 getPlayerPositionCircle(float float1) {
		return new Vector2(3700.0F + (float)(10.0 * Math.sin((double)float1)), 5000.0F + (float)(10.0 * Math.cos((double)float1)));
	}

	private static void sleep(long long1) {
		try {
			Thread.sleep(long1);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}

	private static boolean isVerticalMovement() {
		return ID >= 1 && ID <= 37;
	}

	private static boolean isHorizontalMovement() {
		return ID >= 38 && ID <= 64;
	}

	private Vector2 initSpawnPoints() {
		spawnPoints.put(0, defaultSpawnPoint);
		int int1;
		int int2;
		int int3;
		for (int1 = 0; int1 < 37; ++int1) {
			int2 = int1 < 19 ? 3600 + int1 * 300 + 1 : 3600 + int1 * 300 - 1;
			int3 = (int1 + 1) % 2 == 0 ? 5101 : 12899;
			spawnPoints.put(int1 + 1, new Vector2((float)int2, (float)int3));
		}

		for (int1 = 0; int1 < 27; ++int1) {
			int2 = (int1 + 38) % 2 == 0 ? 3601 : 14399;
			int3 = int1 < 14 ? 5100 + int1 * 300 + 1 : 5100 + int1 * 300 - 1;
			spawnPoints.put(int1 + 38, new Vector2((float)int2, (float)int3));
		}

		for (int1 = 0; int1 < startPositions.length; ++int1) {
			spawnPoints.put(int1, startPositions[int1]);
		}

		for (int1 = 100; int1 < 112; ++int1) {
			spawnPoints.put(int1, (Vector2)startPositionsCircle.get(int1 - 100));
		}

		return (Vector2)spawnPoints.getOrDefault(ID, defaultSpawnPoint);
	}

	private void run() {
		DebugLog.disableLog(DebugType.General);
		log(String.format("FakeClient \"%s\"", versionNumber));
		Rand.init();
		Vector2 vector2 = this.initSpawnPoints();
		player = new FakeClient.Player(vector2.x, vector2.y, ID);
		networking = new FakeClient.Networking();
		networking.state = FakeClient.Networking.State.START;
		startTime = System.currentTimeMillis();
		while (networking.state != FakeClient.Networking.State.QUIT) {
			networking.update();
			sleep(1L);
		}

		networking = null;
	}

	private static void log(String string) {
		System.out.print(String.format("[%s] [client %d] %s\n", logDateFormat.format(Calendar.getInstance().getTime()), ID, string));
	}

	public static void main(String[] stringArray) {
		System.loadLibrary("RakNet64");
		System.loadLibrary("ZNetNoSteam64");
		ID = stringArray.length == 1 ? Integer.parseInt(stringArray[0]) : -1;
		logNetwork = System.getProperty("zomboid.znetlog") != null;
		FakeClient fakeClient = new FakeClient();
		fakeClient.run();
	}

	static  {
	for (int var0 = 0; var0 < 12; ++var0) {
		startPositionsCircle.add(getPlayerPositionCircle((float)(var0 * 30)));
	}

		startPositions = new Vector2[]{new Vector2(5788.0F, 5299.0F), new Vector2(6099.0F, 5299.0F), new Vector2(6609.0F, 5299.0F), new Vector2(6797.0F, 5299.0F), new Vector2(7225.0F, 8217.0F), new Vector2(7225.0F, 8472.0F), new Vector2(8109.0F, 11308.0F), new Vector2(8109.0F, 11490.0F), new Vector2(8109.0F, 11660.0F), new Vector2(8229.0F, 11660.0F), new Vector2(8384.0F, 11660.0F), new Vector2(9914.0F, 13031.0F), new Vector2(9914.0F, 12750.0F), new Vector2(10186.0F, 12750.0F), new Vector2(11656.0F, 10390.0F), new Vector2(11656.0F, 10000.0F), new Vector2(11656.0F, 9700.0F), new Vector2(11656.0F, 9400.0F), new Vector2(12000.0F, 10390.0F), new Vector2(12000.0F, 10000.0F), new Vector2(12000.0F, 9700.0F), new Vector2(12000.0F, 9400.0F), new Vector2(11025.0F, 6735.0F), new Vector2(11325.0F, 6735.0F), new Vector2(11625.0F, 6735.0F), new Vector2(11925.0F, 6735.0F), new Vector2(11025.0F, 7035.0F), new Vector2(11325.0F, 7035.0F), new Vector2(11625.0F, 7035.0F), new Vector2(11925.0F, 7035.0F)};
		logDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		logUpdateLimit = new UpdateLimit(1000L);
	}

	private static class Player {
		private static final int spawnMinX = 3600;
		private static final int spawnMaxX = 14400;
		private static final int spawnMinY = 5100;
		private static final int spawnMaxY = 12900;
		private static final int ChunkGridWidth = 13;
		private static final int ChunksPerWidth = 10;
		private static float speedKmph = 10.0F;
		private final UpdateLimit updateLimiter = new UpdateLimit(125L);
		private final int moveUpdateInterval = 250;
		private final UpdateLimit moveUpdateLimiter = new UpdateLimit(250L);
		private final int stopUpdateInterval = 4000;
		private final UpdateLimit stopUpdateLimiter = new UpdateLimit(4000L);
		private final ArrayList clothes;
		private final String username;
		private final int isFemale;
		private final Color tagColor;
		private final Color speakColor;
		private int OnlineID;
		private float x;
		private float y;
		private final float z;
		private IsoDirections dir;
		private int WorldX;
		private int WorldY;
		private float angleCircle;
		private boolean wasHit;

		private Player(float float1, float float2, int int1) {
			this.username = String.format("client_%d", int1);
			this.tagColor = Colors.SkyBlue;
			this.speakColor = Colors.GetRandomColor();
			this.isFemale = (int)Math.round(Math.random());
			this.OnlineID = -1;
			this.clothes = new ArrayList();
			this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Shirt_FormalWhite"));
			this.clothes.add(new FakeClient.Player.Clothes((byte)13, (byte)3, "Tie_Full"));
			this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Socks_Ankle"));
			this.clothes.add(new FakeClient.Player.Clothes((byte)13, (byte)0, "Trousers_Suit"));
			this.clothes.add(new FakeClient.Player.Clothes((byte)13, (byte)0, "Suit_Jacket"));
			this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Shoes_Black"));
			this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Glasses_Sun"));
			this.x = float1;
			this.y = float2;
			this.z = 0.0F;
			this.setDirection();
			this.WorldX = (int)this.x / 10;
			this.WorldY = (int)this.y / 10;
			speedKmph = (float)(Math.random() * 9.0) + 1.0F;
			this.angleCircle = (float)(int1 * 30);
		}

		private void setDirection() {
			if (FakeClient.isHorizontalMovement()) {
				this.dir = FakeClient.ID % 2 == 0 ? IsoDirections.E : IsoDirections.W;
			} else if (FakeClient.isVerticalMovement()) {
				this.dir = FakeClient.ID % 2 == 0 ? IsoDirections.S : IsoDirections.N;
			} else {
				this.setRandomDirection();
			}
		}

		private void updateDirection() {
			this.dir = IsoDirections.fromIndex(this.dir.index() + 3);
		}

		private void setRandomDirection() {
			int int1 = (int)Math.round(Math.random() * 7.0);
			if (this.dir == null || int1 != this.dir.index()) {
				this.dir = IsoDirections.fromIndex(int1);
			}
		}

		private float getDistance(float float1) {
			return float1 / 3.6F / 4.0F;
		}

		private void move() {
			if (FakeClient.ID >= 100 && FakeClient.ID < 112) {
				if (this.wasHit) {
					if (this.stopUpdateLimiter.Check()) {
						this.wasHit = false;
					}
				} else if (this.moveUpdateLimiter.Check()) {
					this.angleCircle = (this.angleCircle + 0.04F) % 360.0F;
					Vector2 vector2 = FakeClient.getPlayerPositionCircle(this.angleCircle);
					this.x = vector2.x;
					this.y = vector2.y;
					this.checkRequestChunks();
				}
			} else if (FakeClient.ID <= 64 && this.moveUpdateLimiter.Check()) {
				float float1 = this.getDistance(speedKmph);
				Vector2 vector22 = this.dir.ToVector();
				float float2 = this.x + float1 * vector22.x;
				float float3 = this.y + float1 * vector22.y;
				if (float2 > 3600.0F && float2 < 14400.0F && float3 > 5100.0F && float3 < 12900.0F) {
					this.x = float2;
					this.y = float3;
					this.checkRequestChunks();
				} else {
					if (float2 < 3600.0F) {
						float2 = 3600.0F;
					} else if (float2 > 14400.0F) {
						float2 = 14400.0F;
					} else if (float3 < 5100.0F) {
						float3 = 5100.0F;
					} else if (float3 > 12900.0F) {
						float3 = 12900.0F;
					}

					this.x = float2;
					this.y = float3;
					this.updateDirection();
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
							FakeClient.networking.addChunkRequest(this.WorldX - 6, this.WorldY + int3, 0, int3 + 6);
						}
					} else {
						for (int3 = -6; int3 <= 6; ++int3) {
							FakeClient.networking.addChunkRequest(this.WorldX + 6, this.WorldY + int3, 12, int3 + 6);
						}
					}
				} else if (int2 != this.WorldY) {
					if (int2 < this.WorldY) {
						for (int3 = -6; int3 <= 6; ++int3) {
							FakeClient.networking.addChunkRequest(this.WorldX + int3, this.WorldY - 6, int3 + 6, 0);
						}
					} else {
						for (int3 = -6; int3 <= 6; ++int3) {
							FakeClient.networking.addChunkRequest(this.WorldX + int3, this.WorldY + 6, int3 + 6, 12);
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
						FakeClient.networking.addChunkRequest(int7, int8, int7 - int3, int8 - int4);
					}
				}
			}

			FakeClient.networking.requestChunks();
			this.WorldX = int1;
			this.WorldY = int2;
		}

		private void update() {
			if (this.updateLimiter.Check()) {
				this.move();
				FakeClient.networking.sendPlayer();
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

	private static class Networking {
		private final HashMap requests = new HashMap();
		private final ByteBuffer rb = ByteBuffer.allocate(1000000);
		private final ByteBuffer wb = ByteBuffer.allocate(1000000);
		private final RakNetPeerInterface peer = new RakNetPeerInterface();
		private long connectedGUID;
		private Thread thread;
		private FakeClient.Networking.State state;
		private static final HashMap systemPacketTypeNames = new HashMap();
		private static final HashMap userPacketTypeNames = new HashMap();

		private static void doPacket(short short1, ByteBuffer byteBuffer) {
			byteBuffer.put((byte)-122);
			byteBuffer.putShort(short1);
		}

		private static void putUTF(ByteBuffer byteBuffer, String string) {
			if (string == null) {
				byteBuffer.putShort((short)0);
			} else {
				byte[] byteArray = string.getBytes();
				byteBuffer.putShort((short)byteArray.length);
				byteBuffer.put(byteArray);
			}
		}

		private static void putBoolean(ByteBuffer byteBuffer, boolean boolean1) {
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
		}

		private Networking() {
			this.peer.Init(false);
			this.peer.SetMaximumIncomingConnections(1);
			this.peer.SetClientPort(17495 + FakeClient.ID);
			this.peer.SetOccasionalPing(true);
		}

		private void update() {
			switch (this.state) {
			case START: 
				if (this.start()) {
					this.changeState(FakeClient.Networking.State.CONNECT);
				} else {
					this.changeState(FakeClient.Networking.State.RESTART);
				}

				break;
			
			case CONNECT: 
				if (this.connect()) {
					this.changeState(FakeClient.Networking.State.WAIT_CONNECT);
				} else {
					this.changeState(FakeClient.Networking.State.RESTART);
				}

				break;
			
			case LOGIN: 
				this.login();
				this.changeState(FakeClient.Networking.State.WAIT_LOGIN);
				break;
			
			case REGISTER: 
				this.register();
				this.changeState(FakeClient.Networking.State.WAIT_REGISTER);
				break;
			
			case SETUP: 
				this.setup();
				this.changeState(FakeClient.Networking.State.WAIT_SETUP);
				break;
			
			case LOAD: 
				this.load();
				this.changeState(FakeClient.Networking.State.WAIT_LOAD);
				break;
			
			case RUN: 
				FakeClient.player.update();
				break;
			
			case WAIT_CONNECT: 
				if (this.checkStateTime()) {
					this.changeState(FakeClient.Networking.State.RESTART);
				}

				break;
			
			case WAIT_LOGIN: 
				if (this.checkStateTime()) {
					FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
					this.changeState(FakeClient.Networking.State.DISCONNECT);
				}

			
			case WAIT_REGISTER: 
				if (this.checkStateTime()) {
					FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
					this.changeState(FakeClient.Networking.State.DISCONNECT);
				}

			
			case WAIT_SETUP: 
				if (this.checkStateTime()) {
					FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
					this.changeState(FakeClient.Networking.State.DISCONNECT);
				}

			
			case WAIT_LOAD: 
				if (this.checkLoadTime()) {
					FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
					this.changeState(FakeClient.Networking.State.DISCONNECT);
				}

				break;
			
			case DISCONNECT: 
				this.disconnect();
				this.changeState(FakeClient.Networking.State.WAIT_DISCONNECT);
				break;
			
			case WAIT_DISCONNECT: 
				if (!this.thread.isAlive() || this.checkStateTime()) {
					this.changeState(FakeClient.Networking.State.RESTART);
				}

				break;
			
			case RESTART: 
				this.stop();
				if (this.checkStateTime()) {
					this.changeState(FakeClient.Networking.State.START);
				}

			
			case QUIT: 
			
			}
		}

		private void changeState(FakeClient.Networking.State state) {
			long long1 = System.currentTimeMillis();
			if (FakeClient.logNetwork) {
				FakeClient.log(String.format("%s >> %s", this.state, state));
			}

			FakeClient.stateTime = long1;
			this.state = state;
			if (FakeClient.Networking.State.RUN.equals(state)) {
				FakeClient.connectionTime = System.currentTimeMillis() - FakeClient.startTime;
				FakeClient.log(String.format("Connected in %.3f seconds", (float)FakeClient.connectionTime / 1000.0F));
			}
		}

		private void updateTime() {
			FakeClient.stateTime = System.currentTimeMillis();
		}

		private boolean checkStateTime() {
			long long1 = System.currentTimeMillis();
			return long1 - FakeClient.stateTime > 60000L;
		}

		private boolean checkLoadTime() {
			long long1 = System.currentTimeMillis();
			return long1 - FakeClient.stateTime > 120000L;
		}

		private boolean start() {
			boolean boolean1 = this.peer.Startup(1) == 0;
			if (boolean1) {
				this.thread = new Thread(ThreadGroups.Network, this::receiveThread, String.format("client_%d_receive", FakeClient.ID));
				this.thread.setDaemon(true);
				this.thread.start();
			}

			return boolean1;
		}

		private boolean connect() {
			boolean boolean1 = false;
			try {
				InetAddress inetAddress = InetAddress.getByName("localhost");
				String string = inetAddress.getHostAddress();
				boolean1 = this.peer.Connect(string, 16261, PZcrypt.hash("", true)) == 0;
			} catch (UnknownHostException unknownHostException) {
				unknownHostException.printStackTrace();
			}

			return boolean1;
		}

		private void login() {
			this.sendPlayerLogin();
		}

		private void register() {
			this.sendPlayerConnect();
		}

		private void setup() {
			boolean boolean1 = FakeClient.ID < 100 || FakeClient.ID >= 112;
			this.sendPlayerExtraInfo(boolean1);
		}

		private void load() {
			FakeClient.Networking.Request.count = 0;
			this.requests.clear();
			this.requestFullUpdate();
			this.requestLargeAreaZip();
		}

		private void disconnect() {
			if (this.connectedGUID != 0L) {
				this.peer.disconnect(this.connectedGUID);
			}
		}

		private void stop() {
			this.peer.Shutdown();
		}

		private void receiveThread() {
			while (true) {
				if (this.state != FakeClient.Networking.State.RESTART) {
					if (!this.peer.Receive(this.rb)) {
						FakeClient.sleep(1L);
						continue;
					}

					if (this.state != FakeClient.Networking.State.WAIT_DISCONNECT) {
						this.decode(this.rb);
						continue;
					}
				}

				return;
			}
		}

		private void logSystemPacket(int int1) {
			String string = (String)systemPacketTypeNames.getOrDefault(int1, "unknown system packet");
			FakeClient.log(String.format("# %s", string));
		}

		private void decode(ByteBuffer byteBuffer) {
			int int1 = byteBuffer.get() & 255;
			switch (int1) {
			case 0: 
			
			case 1: 
			
			case 19: 
			
			case 24: 
			
			case 25: 
			
			case 31: 
			
			case 32: 
			
			case 33: 
			
			case 44: 
			
			case 45: 
			
			default: 
				break;
			
			case 16: 
				this.connectedGUID = this.peer.getGuidOfPacket();
				this.changeState(FakeClient.Networking.State.LOGIN);
				break;
			
			case 17: 
			
			case 18: 
			
			case 20: 
			
			case 21: 
			
			case 22: 
			
			case 23: 
				this.logSystemPacket(int1);
				this.changeState(FakeClient.Networking.State.RESTART);
				break;
			
			case 134: 
				this.receive(byteBuffer.getShort(), byteBuffer);
			
			}
		}

		private void logUserPacket(short short1) {
			if (FakeClient.logNetwork) {
				String string = (String)userPacketTypeNames.getOrDefault(short1, "unknown user packet");
				FakeClient.log(String.format("## %s", string));
			}
		}

		private void receive(short short1, ByteBuffer byteBuffer) {
			this.logUserPacket(short1);
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
			
			case 19: 
			
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
			
			case 160: 
			
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
			
			case 212: 
			
			case 213: 
			
			case 214: 
			
			case 215: 
			
			case 216: 
			
			default: 
				break;
			
			case 6: 
				if (this.receivePlayerConnect(byteBuffer)) {
					this.changeState(FakeClient.Networking.State.SETUP);
				}

				break;
			
			case 10: 
				this.receiveZombieUpdateInfo(byteBuffer);
				break;
			
			case 18: 
				if (this.state == FakeClient.Networking.State.WAIT_LOAD && this.receiveChunkPart(byteBuffer)) {
					this.updateTime();
					if (this.allChunkPartsReceived()) {
						this.changeState(FakeClient.Networking.State.REGISTER);
					}
				}

				break;
			
			case 21: 
				this.changeState(FakeClient.Networking.State.LOAD);
				break;
			
			case 26: 
				short short2 = byteBuffer.getShort();
				byte byte1 = byteBuffer.get();
				short short3 = byteBuffer.getShort();
				if (short3 == FakeClient.player.OnlineID) {
					FakeClient.player.wasHit = true;
					UpdateLimit updateLimit = FakeClient.player.stopUpdateLimiter;
					Objects.requireNonNull(FakeClient.player);
					updateLimit.Reset(4000L);
				}

				break;
			
			case 36: 
				if (this.state == FakeClient.Networking.State.WAIT_LOAD && this.receiveNotRequired(byteBuffer)) {
					this.updateTime();
					if (this.allChunkPartsReceived()) {
						this.changeState(FakeClient.Networking.State.REGISTER);
					}
				}

				break;
			
			case 84: 
				if (this.receivePlayerExtraInfo(byteBuffer)) {
					this.changeState(FakeClient.Networking.State.RUN);
				}

			
			}
			byteBuffer.clear();
		}

		private ByteBuffer startPacket() {
			this.wb.clear();
			return this.wb;
		}

		private void cancelPacket() {
			this.wb.clear();
		}

		private void endPacket() {
			this.wb.flip();
			this.peer.Send(this.wb, 1, 3, (byte)0, this.connectedGUID, false);
		}

		private void endPacketImmediate() {
			this.wb.flip();
			this.peer.Send(this.wb, 0, 3, (byte)0, this.connectedGUID, false);
		}

		private void endPacketSuperHighUnreliable() {
			this.wb.flip();
			this.peer.Send(this.wb, 0, 1, (byte)0, this.connectedGUID, false);
		}

		private void sendPlayerLogin() {
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)2, byteBuffer);
			putUTF(byteBuffer, FakeClient.player.username);
			putUTF(byteBuffer, FakeClient.player.username);
			putUTF(byteBuffer, FakeClient.versionNumber);
			this.endPacketImmediate();
		}

		private void sendPlayerConnect() {
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)6, byteBuffer);
			this.writePlayerConnectData(byteBuffer);
			this.endPacketImmediate();
		}

		private void writePlayerConnectData(ByteBuffer byteBuffer) {
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)13);
			byteBuffer.putFloat(FakeClient.player.x);
			byteBuffer.putFloat(FakeClient.player.y);
			byteBuffer.putFloat(FakeClient.player.z);
			byteBuffer.putInt(0);
			putUTF(byteBuffer, "fake");
			putUTF(byteBuffer, "fake");
			putUTF(byteBuffer, "fake");
			byteBuffer.putInt(FakeClient.player.isFemale);
			putUTF(byteBuffer, "fake");
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)((int)Math.round(Math.random() * 5.0)));
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			int int1 = FakeClient.player.clothes.size();
			byteBuffer.put((byte)int1);
			Iterator iterator = FakeClient.player.clothes.iterator();
			while (iterator.hasNext()) {
				FakeClient.Player.Clothes clothes = (FakeClient.Player.Clothes)iterator.next();
				byteBuffer.put(clothes.flags);
				putUTF(byteBuffer, "Base." + clothes.name);
				putUTF(byteBuffer, (String)null);
				putUTF(byteBuffer, clothes.name);
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

			putUTF(byteBuffer, "fake_str");
			byteBuffer.putShort((short)0);
			byteBuffer.putInt(0);
			byteBuffer.putFloat(0.0F);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
			putBoolean(byteBuffer, true);
			putUTF(byteBuffer, "fake");
			byteBuffer.putFloat(FakeClient.player.tagColor.r);
			byteBuffer.putFloat(FakeClient.player.tagColor.g);
			byteBuffer.putFloat(FakeClient.player.tagColor.b);
			byteBuffer.putInt(0);
			byteBuffer.putDouble(0.0);
			byteBuffer.putInt(0);
			putUTF(byteBuffer, FakeClient.player.username);
			byteBuffer.putFloat(FakeClient.player.speakColor.r);
			byteBuffer.putFloat(FakeClient.player.speakColor.g);
			byteBuffer.putFloat(FakeClient.player.speakColor.b);
			putBoolean(byteBuffer, true);
			putBoolean(byteBuffer, false);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.putInt(0);
			byteBuffer.putInt(0);
		}

		private void sendPlayerExtraInfo(boolean boolean1) {
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)84, byteBuffer);
			byteBuffer.putShort((short)FakeClient.player.OnlineID);
			putUTF(byteBuffer, "");
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
			byteBuffer.put((byte)(boolean1 ? 1 : 0));
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			byteBuffer.put((byte)0);
			this.endPacketImmediate();
		}

		private void sendPlayer() {
			int int1 = (int)(GameTime.getServerTime() / 1000000L);
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)218, byteBuffer);
			byteBuffer.putShort((short)FakeClient.player.OnlineID);
			byteBuffer.putFloat(FakeClient.player.x);
			byteBuffer.putFloat(FakeClient.player.y);
			byteBuffer.put((byte)((int)FakeClient.player.z));
			byteBuffer.putInt(int1 + 1000);
			byteBuffer.putFloat(FakeClient.player.dir.ToVector().getDirection());
			byteBuffer.put((byte)1);
			byteBuffer.putShort((short)-1);
			byteBuffer.putShort((short)-1);
			byteBuffer.putInt(0);
			byteBuffer.put((byte)1);
			byteBuffer.putFloat(FakeClient.player.x);
			byteBuffer.putFloat(FakeClient.player.y);
			byteBuffer.put((byte)((int)FakeClient.player.z));
			byteBuffer.put((byte)FakeClient.player.dir.index());
			byteBuffer.putInt(int1);
			byteBuffer.putShort((short)0);
			this.endPacketSuperHighUnreliable();
		}

		private boolean receivePlayerConnect(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			if (short1 == -1) {
				byte byte1 = byteBuffer.get();
				short1 = byteBuffer.getShort();
				FakeClient.player.OnlineID = short1;
				FakeClient.log(String.format("player:%s OnlineID:%d", FakeClient.player.username, short1));
				return true;
			} else {
				return false;
			}
		}

		private boolean receivePlayerExtraInfo(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			return short1 == FakeClient.player.OnlineID;
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
			FakeClient.Networking.Request request = new FakeClient.Networking.Request(int1, int2);
			this.requests.put(request.id, request);
		}

		private void requestZipList() {
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)34, byteBuffer);
			byteBuffer.putInt(this.requests.size());
			Iterator iterator = this.requests.values().iterator();
			while (iterator.hasNext()) {
				FakeClient.Networking.Request request = (FakeClient.Networking.Request)iterator.next();
				byteBuffer.putInt(request.id);
				byteBuffer.putInt(request.wx);
				byteBuffer.putInt(request.wy);
				byteBuffer.putLong(request.crc);
			}

			this.endPacket();
		}

		private void requestLargeAreaZip() {
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)24, byteBuffer);
			byteBuffer.putInt(FakeClient.player.WorldX);
			byteBuffer.putInt(FakeClient.player.WorldY);
			byteBuffer.putInt(13);
			this.endPacketImmediate();
			int int1 = FakeClient.player.WorldX - 6 + 2;
			int int2 = FakeClient.player.WorldY - 6 + 2;
			int int3 = FakeClient.player.WorldX + 6 + 2;
			int int4 = FakeClient.player.WorldY + 6 + 2;
			for (int int5 = int2; int5 <= int4; ++int5) {
				for (int int6 = int1; int6 <= int3; ++int6) {
					FakeClient.Networking.Request request = new FakeClient.Networking.Request(int6, int5);
					this.requests.put(request.id, request);
				}
			}

			this.requestZipList();
		}

		private void requestFullUpdate() {
			ByteBuffer byteBuffer = this.startPacket();
			doPacket((short)202, byteBuffer);
			this.endPacketImmediate();
		}

		private void requestChunkObjectState() {
			Iterator iterator = this.requests.values().iterator();
			while (iterator.hasNext()) {
				FakeClient.Networking.Request request = (FakeClient.Networking.Request)iterator.next();
				ByteBuffer byteBuffer = this.startPacket();
				doPacket((short)151, byteBuffer);
				byteBuffer.putShort((short)request.wx);
				byteBuffer.putShort((short)request.wy);
				this.endPacket();
			}
		}

		private void requestChunks() {
			if (!this.requests.isEmpty()) {
				this.requestZipList();
				this.requestChunkObjectState();
				this.requests.clear();
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

		private static enum State {

			START,
			CONNECT,
			WAIT_CONNECT,
			LOGIN,
			WAIT_LOGIN,
			LOAD,
			WAIT_LOAD,
			REGISTER,
			WAIT_REGISTER,
			SETUP,
			WAIT_SETUP,
			RUN,
			DISCONNECT,
			WAIT_DISCONNECT,
			RESTART,
			QUIT;

			private static FakeClient.Networking.State[] $values() {
				return new FakeClient.Networking.State[]{START, CONNECT, WAIT_CONNECT, LOGIN, WAIT_LOGIN, LOAD, WAIT_LOAD, REGISTER, WAIT_REGISTER, SETUP, WAIT_SETUP, RUN, DISCONNECT, WAIT_DISCONNECT, RESTART, QUIT};
			}
		}

		private static final class Request {
			private static final CRC32 crc32 = new CRC32();
			private static int count = 0;
			private final int id;
			private final int wx;
			private final int wy;
			private final long crc;

			private Request(int int1, int int2) {
				this.id = count++;
				this.wx = int1;
				this.wy = int2;
				crc32.reset();
				crc32.update(String.format("map_%d_%d.bin", int1, int2).getBytes());
				this.crc = crc32.getValue();
			}
		}
	}
}
