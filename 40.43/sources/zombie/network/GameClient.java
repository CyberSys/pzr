package zombie.network;

import fmod.javafmod;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.ai.states.DieState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.skills.PerkFactory;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.CGlobalObjects;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridOcclusionData;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectSyncRequests;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.SliceY;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.popman.MPDebugInfo;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ServerPulseGraph;
import zombie.util.AddCoopPlayer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;


public class GameClient {
	public static GameClient instance = new GameClient();
	public static final int DEFAULT_PORT = 16361;
	public static boolean bClient = false;
	public static UdpConnection connection;
	public static int count = 0;
	public static String ip = "localhost";
	public static String localIP = "";
	public static String password = "testpass";
	public static boolean allChatMuted = false;
	public static String username = "lemmy101";
	public static String serverPassword = "";
	public UdpEngine udpEngine;
	public static String accessLevel = "";
	public byte ID = -1;
	public float timeSinceKeepAlive = 0.0F;
	UpdateLimit itemSendFrequency = new UpdateLimit(3000L);
	public static int port;
	public HashMap PlayerToBody = new HashMap();
	public boolean bPlayerConnectSent = false;
	private boolean bClientStarted = false;
	private int ResetID = 0;
	private boolean bConnectionLost = false;
	public static String checksum;
	public static boolean checksumValid;
	public static List pingsList;
	public static String GameMap;
	public static boolean bFastForward;
	public static final ClientServerMap[] loadedCells;
	public int DEBUG_PING = 5;
	public IsoObjectSyncRequests objectSyncReq = new IsoObjectSyncRequests();
	public ObjectsSyncRequests worldObjectsSyncReq = new ObjectsSyncRequests(true);
	public static boolean bCoopInvite;
	private ArrayList connectedPlayers = new ArrayList();
	private static boolean isPaused;
	private int safehouseUpdateTimer = 0;
	private boolean delayPacket = false;
	private final long[] packetCountsFromAllClients = new long[256];
	private final long[] packetCountsFromServer = new long[256];
	private final KahluaTable packetCountsTable;
	private ArrayList delayedDisconnect;
	private volatile GameClient.RequestState request;
	public KahluaTable ServerSpawnRegions;
	public ArrayList RecentlyDied;
	static ArrayList MainLoopNetData;
	static ArrayList LoadingMainLoopNetData;
	static ArrayList DelayedCoopNetData;
	public boolean bConnected;
	public int TimeSinceLastUpdate;
	ByteBuffer staticTest;
	ByteBufferWriter wr;
	long StartHeartMilli;
	long EndHeartMilli;
	public int ping;
	public static float ServerPredictedAhead;
	public static HashMap IDToPlayerMap;
	static TShortObjectHashMap IDToZombieMap;
	public static boolean bIngame;
	public static boolean askPing;
	public ArrayList ServerMods;
	public ErosionConfig erosionConfig;
	public static Calendar startAuth;
	public static String poisonousBerry;
	public static String poisonousMushroom;
	ArrayList players;
	public boolean idMapDirty;
	final ArrayList incomingNetData;
	private final HashMap itemsToSend;
	private final HashMap itemsToSendRemove;
	private static int pingLimitTime;
	KahluaTable dbSchema;

	public GameClient() {
		this.packetCountsTable = LuaManager.platform.newTable();
		this.delayedDisconnect = new ArrayList();
		this.ServerSpawnRegions = null;
		this.RecentlyDied = new ArrayList();
		this.bConnected = false;
		this.TimeSinceLastUpdate = 0;
		this.staticTest = ByteBuffer.allocate(20000);
		this.wr = new ByteBufferWriter(this.staticTest);
		this.StartHeartMilli = 0L;
		this.EndHeartMilli = 0L;
		this.ping = 0;
		this.ServerMods = new ArrayList();
		this.players = new ArrayList();
		this.idMapDirty = true;
		this.incomingNetData = new ArrayList();
		this.itemsToSend = new HashMap();
		this.itemsToSendRemove = new HashMap();
	}

	public IsoPlayer getPlayerByOnlineID(int int1) {
		Iterator iterator = IDToPlayerMap.values().iterator();
		IsoPlayer player;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			player = (IsoPlayer)iterator.next();
		} while (player.getOnlineID() != int1);

		return player;
	}

	public void init() {
		DebugLog.enableLog(DebugType.Network, true);
		LoadingMainLoopNetData.clear();
		MainLoopNetData.clear();
		DelayedCoopNetData.clear();
		bIngame = false;
		IDToPlayerMap.clear();
		IDToZombieMap.clear();
		pingsList.clear();
		this.bPlayerConnectSent = false;
		this.bConnectionLost = false;
		this.delayedDisconnect.clear();
		GameWindow.bServerDisconnected = false;
		this.ServerSpawnRegions = null;
		this.startClient();
	}

	public void startClient() {
		if (this.bClientStarted) {
			this.udpEngine.Connect(ip, port, serverPassword);
		} else {
			try {
				this.udpEngine = new UdpEngine(Rand.Next(10000) + 12345, 1, (String)null, false);
				this.udpEngine.Connect(ip, port, serverPassword);
				this.bClientStarted = true;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void Shutdown() {
		if (this.bClientStarted) {
			this.udpEngine.Shutdown();
		}
	}

	public void update() {
		if (this.safehouseUpdateTimer == 0 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
			this.safehouseUpdateTimer = 3000;
			SafeHouse.updateSafehousePlayersConnected();
		}

		if (this.safehouseUpdateTimer > 0) {
			--this.safehouseUpdateTimer;
		}

		int int1;
		ZomboidNetData zomboidNetData;
		if (this.bConnectionLost) {
			if (!this.bPlayerConnectSent) {
				synchronized (MainLoopNetData) {
					for (int1 = 0; int1 < MainLoopNetData.size(); ++int1) {
						zomboidNetData = (ZomboidNetData)MainLoopNetData.get(int1);
						this.gameLoadingDealWithNetData(zomboidNetData);
					}

					MainLoopNetData.clear();
				}
			} else {
				synchronized (MainLoopNetData) {
					for (int1 = 0; int1 < MainLoopNetData.size(); ++int1) {
						zomboidNetData = (ZomboidNetData)MainLoopNetData.get(int1);
						if (zomboidNetData.type == 83) {
							GameWindow.kickReason = GameWindow.ReadStringUTF(zomboidNetData.buffer);
						}
					}

					MainLoopNetData.clear();
				}
			}

			GameWindow.bServerDisconnected = true;
		} else {
			synchronized (this.delayedDisconnect) {
				while (!this.delayedDisconnect.isEmpty()) {
					int1 = (Integer)this.delayedDisconnect.remove(0);
					switch (int1) {
					case 17: 
						LuaEventManager.triggerEvent("OnConnectFailed", (Object)null);
						break;
					
					case 18: 
						LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_AlreadyConnected"));
					
					case 19: 
					
					case 20: 
					
					case 22: 
					
					case 25: 
					
					case 26: 
					
					case 27: 
					
					case 28: 
					
					case 29: 
					
					case 30: 
					
					case 31: 
					
					default: 
						break;
					
					case 21: 
						LuaEventManager.triggerEvent("OnDisconnect");
						break;
					
					case 23: 
						LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_Banned"));
						break;
					
					case 24: 
						LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_InvalidServerPassword"));
						break;
					
					case 32: 
						LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_ConnectionLost"));
					
					}
				}
			}

			if (!this.bPlayerConnectSent) {
				synchronized (MainLoopNetData) {
					for (int1 = 0; int1 < MainLoopNetData.size(); ++int1) {
						zomboidNetData = (ZomboidNetData)MainLoopNetData.get(int1);
						if (!this.gameLoadingDealWithNetData(zomboidNetData)) {
							LoadingMainLoopNetData.add(zomboidNetData);
						}
					}

					MainLoopNetData.clear();
					WorldStreamer.instance.updateMain();
				}
			} else {
				if (!LoadingMainLoopNetData.isEmpty()) {
					DebugLog.log(DebugType.Network, "Processing delayed packets...");
					synchronized (MainLoopNetData) {
						MainLoopNetData.addAll(0, LoadingMainLoopNetData);
						LoadingMainLoopNetData.clear();
					}
				}

				if (!DelayedCoopNetData.isEmpty() && IsoWorld.instance.AddCoopPlayers.isEmpty()) {
					DebugLog.log(DebugType.Network, "Processing delayed coop packets...");
					synchronized (MainLoopNetData) {
						MainLoopNetData.addAll(0, DelayedCoopNetData);
						DelayedCoopNetData.clear();
					}
				}

				synchronized (MainLoopNetData) {
					long long1 = System.currentTimeMillis();
					int int2 = 0;
					while (true) {
						if (int2 >= MainLoopNetData.size()) {
							break;
						}

						ZomboidNetData zomboidNetData2 = (ZomboidNetData)MainLoopNetData.get(int2);
						if (zomboidNetData2.time + (long)this.DEBUG_PING <= long1) {
							this.mainLoopDealWithNetData(zomboidNetData2);
							MainLoopNetData.remove(int2--);
						}

						++int2;
					}
				}

				for (int int3 = 0; int3 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int3) {
					IsoMovingObject movingObject = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(int3);
					if (movingObject instanceof IsoPlayer && !((IsoPlayer)movingObject).isLocalPlayer() && !this.getPlayers().contains(movingObject)) {
						if (Core.bDebug) {
							DebugLog.log("Disconnected/Distant player " + ((IsoPlayer)movingObject).username + " in CurrentCell.getObjectList() removed");
						}

						IsoWorld.instance.CurrentCell.getObjectList().remove(int3--);
					}
				}

				try {
					this.sendAddedRemovedItems(false);
				} catch (Exception exception) {
					connection.cancelPacket();
					ExceptionLogger.logException(exception);
				}

				if (ChunkRevisions.USE_CHUNK_REVISIONS) {
					ChunkRevisions.instance.updateClient();
				}

				try {
					VehicleManager.instance.clientUpdate();
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}

				this.objectSyncReq.sendRequests(connection);
				this.worldObjectsSyncReq.sendRequests(connection);
				WorldStreamer.instance.updateMain();
				this.timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
			}
		}
	}

	public void smashWindow(IsoWindow window, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)32, byteBufferWriter);
		byteBufferWriter.putInt(window.square.getX());
		byteBufferWriter.putInt(window.square.getY());
		byteBufferWriter.putInt(window.square.getZ());
		byteBufferWriter.putByte((byte)window.square.getObjects().indexOf(window));
		byteBufferWriter.putByte((byte)int1);
		connection.endPacketImmediate();
	}

	public static void getCustomModData() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)80, byteBufferWriter);
		connection.endPacketImmediate();
	}

	private void doStitch(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setStitched(boolean1);
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setStitchTime(float1);
		}
	}

	private void doBandage(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			boolean boolean2 = byteBuffer.get() == 1;
			String string = GameWindow.ReadStringUTF(byteBuffer);
			player.getBodyDamage().SetBandaged(int1, boolean1, float1, boolean2, string);
		}
	}

	private void doWoundInfection(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setInfectedWound(boolean1);
		}
	}

	private void doDisinfect(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setAlcoholLevel(bodyPart.getAlcoholLevel() + float1);
		}
	}

	private void doSplint(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			String string = boolean1 ? GameWindow.ReadStringUTF(byteBuffer) : null;
			float float1 = boolean1 ? byteBuffer.getFloat() : 0.0F;
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setSplint(boolean1, float1);
			bodyPart.setSplintItem(string);
		}
	}

	private void doRemoveGlass(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setHaveGlass(false);
		}
	}

	private void doRemoveBullet(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setHaveBullet(false, int2);
		}
	}

	private void doCleanBurn(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setNeedBurnWash(false);
		}
	}

	private void doAdditionalPain(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + float1);
		}
	}

	private void applyDamage(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null && player == IsoPlayer.instance) {
			try {
				player.getBodyDamage().load(byteBuffer, 143);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			float float1 = byteBuffer.getFloat();
			player.getStats().Pain = float1;
		}

		if (ServerOptions.instance.PlayerSaveOnDamage.getValue()) {
			GameWindow.savePlayer();
		}
	}

	private void delayPacket(int int1, int int2, int int3) {
		if (IsoWorld.instance != null) {
			for (int int4 = 0; int4 < IsoWorld.instance.AddCoopPlayers.size(); ++int4) {
				AddCoopPlayer addCoopPlayer = (AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(int4);
				if (addCoopPlayer.isLoadingThisSquare(int1, int2)) {
					this.delayPacket = true;
					return;
				}
			}
		}
	}

	private void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
		ByteBuffer byteBuffer = zomboidNetData.buffer;
		int int1 = byteBuffer.position();
		this.delayPacket = false;
		if (zomboidNetData.type >= 0 && zomboidNetData.type < 256) {
			int int2 = this.packetCountsFromServer[zomboidNetData.type]++;
		}

		try {
			int int3;
			label191: switch (zomboidNetData.type) {
			case 1: 
				if (ServerPulseGraph.instance != null) {
					ServerPulseGraph.instance.add(byteBuffer.getLong());
				}

			
			case 2: 
			
			case 14: 
			
			case 21: 
			
			case 24: 
			
			case 26: 
			
			case 28: 
			
			case 34: 
			
			case 38: 
			
			case 40: 
			
			case 43: 
			
			case 44: 
			
			case 45: 
			
			case 48: 
			
			case 54: 
			
			case 66: 
			
			case 67: 
			
			case 70: 
			
			case 71: 
			
			case 72: 
			
			case 73: 
			
			case 74: 
			
			case 80: 
			
			case 81: 
			
			case 88: 
			
			case 93: 
			
			case 95: 
			
			case 96: 
			
			case 113: 
			
			case 115: 
			
			case 129: 
			
			case 130: 
			
			case 131: 
			
			case 134: 
			
			case 137: 
			
			case 143: 
			
			case 145: 
			
			case 152: 
			
			case 162: 
			
			case 166: 
			
			case 167: 
			
			case 168: 
			
			case 169: 
			
			case 170: 
			
			case 171: 
			
			case 173: 
			
			case 174: 
			
			case 175: 
			
			case 176: 
			
			case 177: 
			
			case 178: 
			
			case 179: 
			
			case 180: 
			
			case 181: 
			
			case 185: 
			
			case 187: 
			
			case 188: 
			
			case 193: 
			
			case 194: 
			
			case 195: 
			
			case 196: 
			
			case 197: 
			
			case 198: 
			
			case 199: 
			
			default: 
				break;
			
			case 3: 
				if (ChunkRevisions.instance != null) {
					ChunkRevisions.instance.clientPacket(zomboidNetData.type, byteBuffer);
				}

				break;
			
			case 4: 
				MPDebugInfo.instance.clientPacket(byteBuffer);
				break;
			
			case 5: 
				VehicleManager.instance.clientPacket(byteBuffer);
				break;
			
			case 6: 
				this.receivePlayerConnect(byteBuffer);
				break;
			
			case 7: 
				this.receivePlayerInfo(byteBuffer);
				break;
			
			case 8: 
				this.createZombie(byteBuffer);
				break;
			
			case 9: 
				this.receiveMetaGrid(byteBuffer);
				break;
			
			case 10: 
				this.receiveZombieInfo(byteBuffer);
				break;
			
			case 11: 
				this.receiveHelicopter(byteBuffer);
				break;
			
			case 12: 
				this.SyncIsoObject(byteBuffer);
				break;
			
			case 13: 
				this.playerTimeout(byteBuffer);
				break;
			
			case 15: 
				ClientServerMap.receivePacket(byteBuffer);
				break;
			
			case 16: 
				PassengerMap.clientReceivePacket(byteBuffer);
				break;
			
			case 17: 
				this.AddItemToMap(byteBuffer);
				break;
			
			case 18: 
				assert false;
				break;
			
			case 19: 
				this.receiveSyncClock(byteBuffer);
				break;
			
			case 20: 
				this.sendItemsToContainer(byteBuffer);
				break;
			
			case 22: 
				this.removeItemFromContainer(byteBuffer);
				break;
			
			case 23: 
				this.RemoveItemFromMap(byteBuffer);
				break;
			
			case 25: 
				this.receiveEquip(byteBuffer);
				break;
			
			case 27: 
				this.receiveAddCoopPlayer(byteBuffer);
				break;
			
			case 29: 
				this.onZombieDie(byteBuffer);
				break;
			
			case 30: 
				this.removeZombieCompletely(byteBuffer);
				break;
			
			case 31: 
				this.receiveSandboxOptions(byteBuffer);
				break;
			
			case 32: 
				IsoObject object = this.getIsoObjectRefFromByteBuffer(byteBuffer);
				if (object instanceof IsoWindow) {
					byte byte1 = byteBuffer.get();
					if (byte1 == 1) {
						((IsoWindow)object).smashWindow(true);
					} else if (byte1 == 2) {
						((IsoWindow)object).setGlassRemoved(true);
					}
				} else if (Core.bDebug) {
					DebugLog.log("SmashWindow not a window!");
				}

				break;
			
			case 33: 
				this.onPlayerDeath(byteBuffer);
				break;
			
			case 35: 
				this.receiveItemStats(byteBuffer);
				break;
			
			case 36: 
				assert false;
				break;
			
			case 37: 
				this.receiveRequestData(byteBuffer);
				break;
			
			case 39: 
				this.doDeadZombie(byteBuffer);
				break;
			
			case 41: 
				this.applyDamage(byteBuffer);
				break;
			
			case 42: 
				this.doBandage(byteBuffer);
				break;
			
			case 46: 
				SyncAlarmClock(byteBuffer);
				break;
			
			case 47: 
				this.receivePacketCounts(byteBuffer);
				break;
			
			case 49: 
				this.RemoveContestedItemsFromInventory(byteBuffer);
				break;
			
			case 50: 
				int int4 = byteBuffer.getInt();
				this.connectedPlayers = new ArrayList();
				ArrayList arrayList = new ArrayList();
				ArrayList arrayList2 = new ArrayList();
				ArrayList arrayList3 = new ArrayList();
				int3 = 0;
				for (; int3 < int4; ++int3) {
					String string = GameWindow.ReadString(byteBuffer);
					String string2 = GameWindow.ReadString(byteBuffer);
					arrayList.add(string);
					arrayList2.add(string2);
					this.connectedPlayers.add(this.getPlayerFromUsername(string));
					if (SteamUtils.isSteamModeEnabled()) {
						String string3 = SteamUtils.convertSteamIDToString(byteBuffer.getLong());
						arrayList3.add(string3);
					}
				}

				LuaEventManager.triggerEvent("OnScoreboardUpdate", arrayList, arrayList2, arrayList3);
				break;
			
			case 51: 
				this.receiveModData(byteBuffer);
				break;
			
			case 52: 
				GameWindow.savePlayer();
				GameWindow.kickReason = "Server shut down safely. Players and map data saved.";
				GameWindow.bServerDisconnected = true;
				break;
			
			case 53: 
				this.receiveSound(byteBuffer);
				break;
			
			case 55: 
				this.receiveAmbient(byteBuffer);
				break;
			
			case 56: 
				this.receiveClothing(byteBuffer);
				break;
			
			case 57: 
				this.receiveServerCommand(byteBuffer);
				break;
			
			case 58: 
				this.receiveObjectModData(byteBuffer);
				break;
			
			case 59: 
				this.receiveObjectChange(byteBuffer);
				break;
			
			case 60: 
				this.receiveBloodSplatter(byteBuffer);
				break;
			
			case 61: 
				this.receiveZombieSound(byteBuffer);
				break;
			
			case 62: 
				this.receivePlayerZombieDescriptor(byteBuffer);
				break;
			
			case 63: 
				receiveSlowFactor(byteBuffer);
				break;
			
			case 64: 
				this.receiveWeather(byteBuffer);
				break;
			
			case 65: 
				this.SyncPlayerInventory(byteBuffer);
				break;
			
			case 68: 
				this.RemoveCorpseFromMap(byteBuffer);
				break;
			
			case 69: 
				this.AddCorpseToMap(byteBuffer);
				break;
			
			case 75: 
				this.startFire(byteBuffer);
				break;
			
			case 76: 
				this.updateItemSprite(byteBuffer);
				break;
			
			case 77: 
				this.startRain(byteBuffer);
				break;
			
			case 78: 
				RainManager.stopRaining();
				break;
			
			case 79: 
				this.receiveWorldMessage(byteBuffer);
				break;
			
			case 82: 
				int3 = byteBuffer.getInt();
				int int5 = 0;
				while (true) {
					if (int5 >= int3) {
						break label191;
					}

					ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
					++int5;
				}

			
			case 83: 
				kick(byteBuffer);
				break;
			
			case 84: 
				receivePlayerExtraInfo(byteBuffer);
				break;
			
			case 85: 
				AddItemInInventory(byteBuffer);
				break;
			
			case 86: 
				playerChangeSafety(byteBuffer);
				break;
			
			case 87: 
				getInfoFromPing(byteBuffer);
				break;
			
			case 89: 
				addXp(byteBuffer);
				break;
			
			case 90: 
				this.updateOverlay(byteBuffer);
				break;
			
			case 91: 
				NetChecksum.comparer.clientPacket(byteBuffer);
				break;
			
			case 92: 
				constructedZone(byteBuffer);
				break;
			
			case 94: 
				registerZone(byteBuffer);
				break;
			
			case 97: 
				this.doWoundInfection(byteBuffer);
				break;
			
			case 98: 
				this.doStitch(byteBuffer);
				break;
			
			case 99: 
				this.doDisinfect(byteBuffer);
				break;
			
			case 100: 
				this.doAdditionalPain(byteBuffer);
				break;
			
			case 101: 
				this.doRemoveGlass(byteBuffer);
				break;
			
			case 102: 
				this.doSplint(byteBuffer);
				break;
			
			case 103: 
				this.doRemoveBullet(byteBuffer);
				break;
			
			case 104: 
				this.doCleanBurn(byteBuffer);
				break;
			
			case 105: 
				this.syncThumpable(byteBuffer);
				break;
			
			case 106: 
				this.SyncDoorKey(byteBuffer);
				break;
			
			case 107: 
				addXpFromCommand(byteBuffer);
				break;
			
			case 108: 
				this.teleport(byteBuffer);
				break;
			
			case 109: 
				this.removeBlood(byteBuffer);
				break;
			
			case 110: 
				this.addExplosiveTrap(byteBuffer);
				break;
			
			case 111: 
				this.removeSpecialObjectFromSquare(byteBuffer);
				break;
			
			case 112: 
				this.receiveBodyDamageUpdate(byteBuffer);
				break;
			
			case 114: 
				this.syncSafehouse(byteBuffer);
				break;
			
			case 116: 
				this.stopFire(byteBuffer);
				break;
			
			case 117: 
				this.doCataplasm(byteBuffer);
				break;
			
			case 118: 
				this.addAlarm(byteBuffer);
				break;
			
			case 119: 
				this.receiveSoundEveryPlayer(byteBuffer);
				break;
			
			case 120: 
				this.receiveFurnaceChange(byteBuffer);
				break;
			
			case 121: 
				this.receiveCustomColor(byteBuffer);
				break;
			
			case 122: 
				this.syncCompost(byteBuffer);
				break;
			
			case 123: 
				this.receivePlayerStatsChanges(byteBuffer);
				break;
			
			case 124: 
				addXpFromPlayerStatsUI(byteBuffer);
				break;
			
			case 125: 
				receiveAddLevelUpPoint(byteBuffer);
				break;
			
			case 126: 
				syncXp(byteBuffer);
				break;
			
			case 127: 
				dealWithNetDataShort(zomboidNetData, byteBuffer);
				break;
			
			case 128: 
				this.receiveUserlog(byteBuffer);
				break;
			
			case 132: 
				receiveAdminMessage(byteBuffer);
				break;
			
			case 133: 
				receiveWakeUpOrder(byteBuffer);
				break;
			
			case 135: 
				this.receiveDBSchema(byteBuffer);
				break;
			
			case 136: 
				this.receiveTableResult(byteBuffer);
				break;
			
			case 138: 
				this.receiveNewTxtColor(byteBuffer);
				break;
			
			case 139: 
				this.syncNonPvpZone(byteBuffer);
				break;
			
			case 140: 
				this.syncFaction(byteBuffer);
				break;
			
			case 141: 
				this.receiveFactionInvite(byteBuffer);
				break;
			
			case 142: 
				this.AcceptedFactionInvite(byteBuffer);
				break;
			
			case 144: 
				gotTickets(byteBuffer);
				break;
			
			case 146: 
				isRequestedToTrade(byteBuffer);
				break;
			
			case 147: 
				tradingUIAddItem(byteBuffer);
				break;
			
			case 148: 
				tradingUIRemoveItem(byteBuffer);
				break;
			
			case 149: 
				tradingUIUpdateState(byteBuffer);
				break;
			
			case 150: 
				receiveItemListNet(byteBuffer);
				break;
			
			case 151: 
				this.receiveChunkObjectState(byteBuffer);
				break;
			
			case 153: 
				this.sendInventory(byteBuffer);
				break;
			
			case 154: 
				this.receiveInventory(byteBuffer);
				break;
			
			case 155: 
				this.invMngSendItem(byteBuffer);
				break;
			
			case 156: 
				this.invMngGotItem(byteBuffer);
				break;
			
			case 157: 
				this.invMngRemoveItem(byteBuffer);
				break;
			
			case 158: 
				this.PauseGame(byteBuffer);
				break;
			
			case 159: 
				this.UnpauseGame(byteBuffer);
				break;
			
			case 160: 
				GameTime.getInstance();
				GameTime.receiveTimeSync(byteBuffer, connection);
				break;
			
			case 161: 
				this.SyncIsoObjectReq(byteBuffer);
				break;
			
			case 163: 
				this.SyncWorldObjectsReq(byteBuffer);
				break;
			
			case 164: 
				this.SyncObjectsReq(byteBuffer);
				break;
			
			case 165: 
				this.onPlayerOnBeaten(byteBuffer);
				break;
			
			case 172: 
				this.receiveDamageFromCarCrash(byteBuffer);
				break;
			
			case 182: 
				ChatManager.getInstance().processInitPlayerChatPacket(byteBuffer);
				break;
			
			case 183: 
				ChatManager.getInstance().processJoinChatPacket(byteBuffer);
				break;
			
			case 184: 
				ChatManager.getInstance().processLeaveChatPacket(byteBuffer);
				break;
			
			case 186: 
				ChatManager.getInstance().processChatMessagePacket(byteBuffer);
				break;
			
			case 189: 
				ChatManager.getInstance().processAddTabPacket(byteBuffer);
				break;
			
			case 190: 
				ChatManager.getInstance().processRemoveTabPacket(byteBuffer);
				break;
			
			case 191: 
				ChatManager.getInstance().setFullyConnected();
				break;
			
			case 192: 
				ChatManager.getInstance().processPlayerNotFound();
				break;
			
			case 200: 
				receiveClimateManagerPacket(byteBuffer);
				break;
			
			case 201: 
				IsoRegion.receiveServerUpdatePacket(byteBuffer);
			
			}

			if (this.delayPacket) {
				byteBuffer.position(int1);
				DelayedCoopNetData.add(zomboidNetData);
				return;
			}
		} catch (Exception exception) {
			DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type);
			exception.printStackTrace();
		}

		ZomboidNetDataPool.instance.discard(zomboidNetData);
	}

	private void receiveDamageFromCarCrash(ByteBuffer byteBuffer) {
		float float1 = byteBuffer.getFloat();
		if (IsoPlayer.instance.getVehicle() == null) {
			DebugLog.log(DebugType.Network, "Receive damage from car crash, can\'t find vehicle");
		} else {
			IsoPlayer.instance.getVehicle().addRandomDamageFromCrash(IsoPlayer.instance, float1);
		}
	}

	private void receivePacketCounts(ByteBuffer byteBuffer) {
		for (int int1 = 0; int1 < 256; ++int1) {
			this.packetCountsFromAllClients[int1] = byteBuffer.getLong();
		}
	}

	public void requestPacketCounts() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)47, byteBufferWriter);
		connection.endPacket();
	}

	public KahluaTable getPacketCounts(int int1) {
		long[] longArray = int1 == 1 ? this.packetCountsFromAllClients : this.packetCountsFromServer;
		for (int int2 = 0; int2 < 256; ++int2) {
			this.packetCountsTable.rawset(int2 + 1, BoxedStaticValues.toDouble((double)longArray[int2]));
		}

		return this.packetCountsTable;
	}

	public static boolean IsClientPaused() {
		return isPaused;
	}

	private void PauseGame(ByteBuffer byteBuffer) {
		isPaused = true;
		LuaEventManager.triggerEvent("OnServerStartSaving");
	}

	private void UnpauseGame(ByteBuffer byteBuffer) {
		isPaused = false;
		LuaEventManager.triggerEvent("OnServerFinishSaving");
	}

	private void invMngRemoveItem(ByteBuffer byteBuffer) {
		long long1 = byteBuffer.getLong();
		InventoryItem inventoryItem = IsoPlayer.instance.getInventory().getItemWithIDRecursiv(long1);
		if (inventoryItem == null) {
			DebugLog.log("ERROR: invMngRemoveItem can not find " + long1 + " item.");
		} else {
			if (inventoryItem.getCategory().equals("Clothing")) {
				if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Top && inventoryItem == IsoPlayer.instance.getClothingItem_Torso()) {
					IsoPlayer.instance.setClothingItem_Torso((InventoryItem)null);
				} else if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Shoes && inventoryItem == IsoPlayer.instance.getClothingItem_Feet()) {
					IsoPlayer.instance.setClothingItem_Feet((InventoryItem)null);
				} else if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Bottoms && inventoryItem == IsoPlayer.instance.getClothingItem_Legs()) {
					IsoPlayer.instance.setClothingItem_Legs((InventoryItem)null);
				}

				LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.instance);
			}

			if (inventoryItem == IsoPlayer.instance.getPrimaryHandItem()) {
				IsoPlayer.instance.setPrimaryHandItem((InventoryItem)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.instance);
			} else if (inventoryItem == IsoPlayer.instance.getSecondaryHandItem()) {
				IsoPlayer.instance.setSecondaryHandItem((InventoryItem)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.instance);
			}

			boolean boolean1 = IsoPlayer.instance.getInventory().removeItemWithIDRecurse(long1);
			if (!boolean1) {
				DebugLog.log("ERROR: GameClient.invMngRemoveItem can not remove item " + long1);
			}
		}
	}

	private void invMngGotItem(ByteBuffer byteBuffer) throws IOException {
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		byte byte1 = byteBuffer.get();
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		inventoryItem.load(byteBuffer, 143, false);
		IsoPlayer.instance.getInventory().addItem(inventoryItem);
	}

	private void invMngSendItem(ByteBuffer byteBuffer) throws IOException {
		long long1 = 0L;
		String string = null;
		if (byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		} else {
			long1 = byteBuffer.getLong();
		}

		int int1 = byteBuffer.getInt();
		InventoryItem inventoryItem = null;
		if (string == null) {
			inventoryItem = IsoPlayer.instance.getInventory().getItemWithIDRecursiv(long1);
			if (inventoryItem == null) {
				DebugLog.log("ERROR: invMngRemoveItem can not find " + long1 + " item.");
				return;
			}
		} else {
			inventoryItem = InventoryItemFactory.CreateItem(string);
		}

		if (inventoryItem != null) {
			if (string != null) {
				IsoPlayer.instance.getInventory().RemoveOneOf(string.split("\\.")[1]);
			} else {
				if (inventoryItem.getCategory().equals("Clothing")) {
					if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Top && inventoryItem == IsoPlayer.instance.getClothingItem_Torso()) {
						IsoPlayer.instance.setClothingItem_Torso((InventoryItem)null);
					} else if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Shoes && inventoryItem == IsoPlayer.instance.getClothingItem_Feet()) {
						IsoPlayer.instance.setClothingItem_Feet((InventoryItem)null);
					} else if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Bottoms && inventoryItem == IsoPlayer.instance.getClothingItem_Legs()) {
						IsoPlayer.instance.setClothingItem_Legs((InventoryItem)null);
					}

					LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.instance);
				}

				if (inventoryItem == IsoPlayer.instance.getPrimaryHandItem()) {
					IsoPlayer.instance.setPrimaryHandItem((InventoryItem)null);
					LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.instance);
				} else if (inventoryItem == IsoPlayer.instance.getSecondaryHandItem()) {
					IsoPlayer.instance.setSecondaryHandItem((InventoryItem)null);
					LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.instance);
				}

				IsoPlayer.instance.getInventory().removeItemWithIDRecurse(inventoryItem.getID());
			}

			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)156, byteBufferWriter);
			byteBufferWriter.putInt(int1);
			inventoryItem.save(byteBufferWriter.bb, false);
			connection.endPacketImmediate();
		}
	}

	public static void invMngRequestItem(long long1, String string, IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)155, byteBufferWriter);
		if (string != null) {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putUTF(string);
		} else {
			byteBufferWriter.putByte((byte)0);
			byteBufferWriter.putLong(long1);
		}

		byteBufferWriter.putInt(IsoPlayer.instance.getOnlineID());
		byteBufferWriter.putInt(player.getOnlineID());
		connection.endPacketImmediate();
	}

	public static void invMngRequestRemoveItem(long long1, IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)157, byteBufferWriter);
		byteBufferWriter.putLong(long1);
		byteBufferWriter.putInt(player.getOnlineID());
		connection.endPacketImmediate();
	}

	private void syncFaction(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		Faction faction = Faction.getFaction(string);
		if (faction == null) {
			faction = new Faction(string, string2);
			Faction.getFactions().add(faction);
		}

		faction.getPlayers().clear();
		if (byteBuffer.get() == 1) {
			faction.setTag(GameWindow.ReadString(byteBuffer));
			faction.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		}

		for (int int2 = 0; int2 < int1; ++int2) {
			faction.getPlayers().add(GameWindow.ReadString(byteBuffer));
		}

		faction.setOwner(string2);
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			Faction.getFactions().remove(faction);
			DebugLog.log("faction: removed " + string + " owner=" + faction.getOwner());
		}

		LuaEventManager.triggerEvent("SyncFaction", string);
	}

	private void syncNonPvpZone(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		NonPvpZone nonPvpZone = NonPvpZone.getZoneByTitle(string);
		if (nonPvpZone == null) {
			nonPvpZone = NonPvpZone.addNonPvpZone(string, int1, int2, int3, int4);
		}

		if (nonPvpZone != null) {
			boolean boolean1 = byteBuffer.get() == 1;
			if (boolean1) {
				NonPvpZone.removeNonPvpZone(string, true);
			}
		}
	}

	private void receiveNewTxtColor(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			player.setSpeakColourInfo(new ColorInfo(float1, float2, float3, 1.0F));
		}
	}

	private void receiveSoundEveryPlayer(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		DebugLog.log(DebugType.Sound, "sound: received " + string + " at " + int1 + "," + int2 + "," + int3);
		if (!Core.SoundDisabled) {
			long long1 = javafmod.FMOD_Studio_System_GetEvent(string);
			long long2 = javafmod.FMOD_Studio_System_CreateEventInstance(long1);
			javafmod.FMOD_Studio_SetVolume(long2, (float)Core.getInstance().getOptionAmbientVolume() / 20.0F);
			javafmod.FMOD_Studio_EventInstance3D(long2, (float)int1, (float)int2, (float)int3);
			javafmod.FMOD_Studio_StartEvent(long2);
			javafmod.FMOD_Studio_ReleaseEventInstance(long2);
		}
	}

	private void doCataplasm(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			if (float1 > 0.0F) {
				player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setPlantainFactor(float1);
			}

			if (float2 > 0.0F) {
				player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setComfreyFactor(float2);
			}

			if (float3 > 0.0F) {
				player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setGarlicFactor(float3);
			}
		}
	}

	private void stopFire(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.stopFire();
		}
	}

	private void addAlarm(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, 0);
		if (square != null && square.room != null && square.room.def != null) {
			square.room.def.bExplored = false;
			AmbientStreamManager.instance.doAlarm(square.room.def);
		}
	}

	private void removeSpecialObjectFromSquare(ByteBuffer byteBuffer) {
		if (IsoWorld.instance.CurrentCell != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				this.delayPacket(int1, int2, int3);
			} else {
				if (square != null && int4 >= 0 && int4 < square.getSpecialObjects().size()) {
					IsoObject object = (IsoObject)square.getSpecialObjects().get(int4);
					object.removeFromWorld();
					object.removeFromSquare();
					square.getCell().getProcessIsoObjectRemove().add(object);
				} else if (Core.bDebug) {
					DebugLog.log("RemoveItemFromMap: sq is null or index is invalid");
				}
			}
		}
	}

	private void addExplosiveTrap(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
			byte byte1 = byteBuffer.get();
			HandWeapon handWeapon = (HandWeapon)inventoryItem;
			try {
				handWeapon.load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			IsoTrap trap = new IsoTrap(handWeapon, square.getCell(), square);
			int int4 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			boolean boolean2 = byteBuffer.get() == 1;
			if (!boolean2) {
				square.AddTileObject(trap);
			}
		}
	}

	private void teleport(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		IsoPlayer player = IsoPlayer.players[byte1];
		if (player != null && !player.isDead()) {
			if (player.getVehicle() != null) {
				player.getVehicle().exit(player);
				LuaEventManager.triggerEvent("OnExitVehicle", player);
			}

			player.setX(byteBuffer.getFloat());
			player.setY(byteBuffer.getFloat());
			player.setZ(byteBuffer.getFloat());
			player.setLx(player.getX());
			player.setLy(player.getY());
			player.setLz(player.getZ());
		}
	}

	private void removeBlood(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.removeBlood(true, boolean1);
		}
	}

	private void syncThumpable(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (byte1 >= 0 && byte1 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte1);
				if (object instanceof IsoThumpable) {
					IsoThumpable thumpable = (IsoThumpable)object;
					thumpable.lockedByCode = byteBuffer.getInt();
					thumpable.lockedByPadlock = byteBuffer.get() == 1;
					thumpable.keyId = byteBuffer.getInt();
				} else {
					DebugLog.log("syncThumpable: expected IsoThumpable index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
				}
			} else {
				DebugLog.log("syncThumpable: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		}
	}

	private void SyncDoorKey(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (byte1 >= 0 && byte1 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte1);
				if (object instanceof IsoDoor) {
					IsoDoor door = (IsoDoor)object;
					door.keyId = byteBuffer.getInt();
				} else {
					DebugLog.log("SyncDoorKey: expected IsoDoor index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
				}
			} else {
				DebugLog.log("SyncDoorKey: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		}
	}

	private static void constructedZone(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoMetaGrid.Zone zone = IsoWorld.instance.MetaGrid.getZoneAt(int1, int2, int3);
		if (zone != null) {
			zone.setHaveConstruction(true);
		}
	}

	private void receiveAddCoopPlayer(ByteBuffer byteBuffer) {
		boolean boolean1 = byteBuffer.get() == 1;
		byte byte1 = byteBuffer.get();
		if (boolean1) {
			for (int int1 = 0; int1 < IsoWorld.instance.AddCoopPlayers.size(); ++int1) {
				((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(int1)).accessGranted(byte1);
			}
		} else {
			String string = GameWindow.ReadStringUTF(byteBuffer);
			for (int int2 = 0; int2 < IsoWorld.instance.AddCoopPlayers.size(); ++int2) {
				((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(int2)).accessDenied(byte1, string);
			}
		}
	}

	private void receivePlayerZombieDescriptor(ByteBuffer byteBuffer) {
		try {
			SurvivorDesc survivorDesc = new SurvivorDesc();
			short short1 = byteBuffer.getShort();
			survivorDesc.loadCompact(byteBuffer);
			byte byte1 = byteBuffer.get();
			survivorDesc.setID(short1);
			SharedDescriptors.registerPlayerZombieDescriptor(survivorDesc, byte1);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void checksumServer() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)91, byteBufferWriter);
		byteBufferWriter.putUTF(checksum + ScriptManager.instance.getChecksum());
		connection.endPacketImmediate();
	}

	private static void registerZone(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		int int6 = byteBuffer.getInt();
		ArrayList arrayList = IsoWorld.instance.getMetaGrid().getZonesAt(int1, int2, int3);
		boolean boolean1 = false;
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)iterator.next();
			if (string2.equals(zone.getType())) {
				boolean1 = true;
				zone.setName(string);
				zone.setLastActionTimestamp(int6);
			}
		}

		if (!boolean1) {
			IsoWorld.instance.getMetaGrid().registerZone(string, string2, int1, int2, int3, int4, int5);
		}
	}

	private static void addXp(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		IsoPlayer player = IsoPlayer.players[byte1];
		if (player != null && !player.isDead()) {
			PerkFactory.Perks perks = PerkFactory.Perks.fromIndex(int1);
			player.getXp().AddXP(perks, (float)int2);
		}
	}

	private static void addXpFromCommand(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(byteBuffer.getShort()));
		PerkFactory.Perks perks = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
		if (player != null && !player.isDead()) {
			player.getXp().AddXP(perks, (float)byteBuffer.getInt());
		}
	}

	public void sendAddXpFromPlayerStatsUI(IsoPlayer player, PerkFactory.Perks perks, int int1, boolean boolean1, boolean boolean2) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)124, byteBufferWriter);
			byteBufferWriter.putInt(player.getOnlineID());
			if (!boolean2) {
				byteBufferWriter.putInt(0);
				byteBufferWriter.putInt(perks.index());
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
			} else {
				byteBufferWriter.putInt(1);
				byteBufferWriter.putInt(int1);
			}

			connection.endPacketImmediate();
		}
	}

	public void addLevelUpPoint(IsoPlayer player) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)125, byteBufferWriter);
			byteBufferWriter.putInt(player.getOnlineID());
			connection.endPacketImmediate();
		}
	}

	private static void receiveAddLevelUpPoint(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		if (player != null && !player.isDead()) {
			player.setNumberOfPerksToPick(player.getNumberOfPerksToPick() + 1);
		}
	}

	private static void syncXp(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		if (player != null && !player.isDead()) {
			try {
				player.getXp().load(byteBuffer, 143);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			player.setNumberOfPerksToPick(byteBuffer.getInt());
		}
	}

	public void sendSyncXp(IsoPlayer player) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)126, byteBufferWriter);
			byteBufferWriter.putInt(player.getOnlineID());
			try {
				player.getXp().save(byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			byteBufferWriter.putInt(player.getNumberOfPerksToPick());
			connection.endPacketImmediate();
		}
	}

	public void sendTransactionID(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)134, byteBufferWriter);
		byteBufferWriter.putInt(player.getOnlineID());
		byteBufferWriter.putInt(player.getTransactionID());
		connection.endPacketImmediate();
	}

	private void receiveUserlog(ByteBuffer byteBuffer) {
		ArrayList arrayList = new ArrayList();
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		for (int int2 = 0; int2 < int1; ++int2) {
			arrayList.add(new Userlog(string, Userlog.UserlogType.fromIndex(byteBuffer.getInt()).toString(), GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt()));
		}

		LuaEventManager.triggerEvent("OnReceiveUserlog", string, arrayList);
	}

	private static void addXpFromPlayerStatsUI(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		int int1 = byteBuffer.getInt();
		if (player != null && !player.isDead()) {
			if (int1 == 0) {
				PerkFactory.Perks perks = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
				player.getXp().AddXP(perks, (float)byteBuffer.getInt(), false, byteBuffer.get() == 1, false, true);
			} else if (int1 == 1) {
				player.getXp().addGlobalXP((float)byteBuffer.getInt());
			}
		}
	}

	private static void getInfoFromPing(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = byteBuffer.getInt() - 1 + "/" + byteBuffer.getInt();
		LuaEventManager.triggerEvent("ServerPinged", string, string2);
		connection.forceDisconnect();
		askPing = false;
	}

	private static void playerChangeSafety(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		if (player != null) {
			player.setSafety(byteBuffer.get() == 1);
		}
	}

	private static void AddItemInInventory(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null && !player.isDead()) {
			player.getInventory().AddItems(string, int1);
		}
	}

	private static void kick(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		if (string != null && !string.equals("")) {
			ChatManager.getInstance().showServerChatMessage(string);
		}

		connection.username = null;
		GameWindow.savePlayer();
		GameWindow.bServerDisconnected = true;
		GameWindow.kickReason = string;
		connection.forceDisconnect();
		connection.close();
	}

	public void addDisconnectPacket(int int1) {
		synchronized (this.delayedDisconnect) {
			this.delayedDisconnect.add(int1);
		}
	}

	public void connectionLost() {
		this.bConnectionLost = true;
	}

	public static void SendCommandToServer(String string) {
		if (ServerOptions.clientOptionsList == null) {
			ServerOptions.initClientCommandsHelp();
		}

		if (string.startsWith("/roll")) {
			try {
				int int1 = Integer.parseInt(string.split(" ")[1]);
				if (int1 > 100) {
					ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
					return;
				}
			} catch (Exception exception) {
				ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
				return;
			}

			if (!IsoPlayer.instance.getInventory().contains("Dice") && accessLevel.equals("")) {
				ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
				return;
			}
		}

		if (string.startsWith("/card") && !IsoPlayer.instance.getInventory().contains("CardDeck") && accessLevel.equals("")) {
			ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("card"));
		} else {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)81, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			connection.endPacketImmediate();
		}
	}

	private boolean gameLoadingDealWithNetData(ZomboidNetData zomboidNetData) {
		ByteBuffer byteBuffer = zomboidNetData.buffer;
		try {
			switch (zomboidNetData.type) {
			case 3: 
				if (ChunkRevisions.instance != null) {
					ChunkRevisions.instance.clientPacket(zomboidNetData.type, byteBuffer);
				}

			
			case 4: 
				break;
			
			case 5: 
				VehicleManager.loadingClientPacket(byteBuffer);
				return false;
			
			case 6: 
				int int1 = byteBuffer.position();
				if (!this.receivePlayerConnectWhileLoading(byteBuffer)) {
					byteBuffer.position(int1);
					return false;
				}

				break;
			
			case 13: 
				this.playerTimeout(byteBuffer);
				break;
			
			case 15: 
				ClientServerMap.receivePacket(byteBuffer);
				break;
			
			case 18: 
				assert false;
				break;
			
			case 21: 
				this.receiveConnectionDetails(byteBuffer);
				break;
			
			case 36: 
				assert false;
				break;
			
			case 37: 
				this.receiveRequestData(byteBuffer);
				break;
			
			case 40: 
				String string = GameWindow.ReadString(byteBuffer);
				String[] stringArray = string.split("##");
				LuaEventManager.triggerEvent("OnConnectFailed", stringArray.length > 0 ? Translator.getText("UI_OnConnectFailed_" + stringArray[0], stringArray.length > 1 ? stringArray[1] : null, stringArray.length > 2 ? stringArray[2] : null, stringArray.length > 3 ? stringArray[3] : null) : null);
				break;
			
			case 83: 
				GameWindow.kickReason = GameWindow.ReadStringUTF(byteBuffer);
				GameWindow.bServerDisconnected = true;
				break;
			
			case 87: 
				getInfoFromPing(byteBuffer);
				break;
			
			case 91: 
				NetChecksum.comparer.clientPacket(byteBuffer);
				break;
			
			case 171: 
				this.receiveSpawnRegion(byteBuffer);
				break;
			
			default: 
				if (Core.bDebug) {
					DebugLog.log(DebugType.Network, "Delay processing packet of type " + zomboidNetData.type + " while loading game");
				}

				return false;
			
			}
		} catch (Exception exception) {
			DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type);
			exception.printStackTrace();
		}

		ZomboidNetDataPool.instance.discard(zomboidNetData);
		return true;
	}

	private void receiveWorldMessage(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		string2 = string2.replaceAll("<", "&lt;");
		string2 = string2.replaceAll(">", "&gt;");
		ChatManager.getInstance().addMessage(string, string2);
	}

	private void startRain(ByteBuffer byteBuffer) {
		RainManager.setRandRainMin(byteBuffer.getInt());
		RainManager.setRandRainMax(byteBuffer.getInt());
		RainManager.startRaining();
		RainManager.RainDesiredIntensity = byteBuffer.getFloat();
	}

	private void receiveWeather(ByteBuffer byteBuffer) {
		GameTime gameTime = GameTime.getInstance();
		gameTime.setDawn(byteBuffer.get() & 255);
		gameTime.setDusk(byteBuffer.get() & 255);
		gameTime.setThunderDay(byteBuffer.get() == 1);
		gameTime.setMoon(byteBuffer.getFloat());
		gameTime.setAmbientMin(byteBuffer.getFloat());
		gameTime.setAmbientMax(byteBuffer.getFloat());
		gameTime.setViewDistMin(byteBuffer.getFloat());
		gameTime.setViewDistMax(byteBuffer.getFloat());
		IsoWorld.instance.setGlobalTemperature(byteBuffer.getFloat());
		IsoWorld.instance.setWeather(GameWindow.ReadStringUTF(byteBuffer));
		ErosionMain.getInstance().receiveState(byteBuffer);
	}

	private void receiveSyncClock(ByteBuffer byteBuffer) {
		GameTime gameTime = GameTime.getInstance();
		boolean boolean1 = bFastForward;
		bFastForward = byteBuffer.get() == 1;
		float float1 = byteBuffer.getFloat();
		float float2 = gameTime.getTimeOfDay() - gameTime.getLastTimeOfDay();
		gameTime.setTimeOfDay(float1);
		gameTime.setLastTimeOfDay(float1 - float2);
		if (gameTime.getLastTimeOfDay() < 0.0F) {
			gameTime.setLastTimeOfDay(float1 - float2 + 24.0F);
		}

		gameTime.ServerLastTimeOfDay = gameTime.ServerTimeOfDay;
		gameTime.ServerTimeOfDay = float1;
		if (gameTime.ServerLastTimeOfDay <= 7.0F && gameTime.ServerTimeOfDay > 7.0F) {
			gameTime.setNightsSurvived(gameTime.getNightsSurvived() + 1);
		}

		if (gameTime.ServerLastTimeOfDay > gameTime.ServerTimeOfDay) {
			++gameTime.ServerNewDays;
		}
	}

	private void receiveServerCommand(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		KahluaTable kahluaTable = null;
		if (boolean1) {
			kahluaTable = LuaManager.platform.newTable();
			try {
				TableNetworkUtils.load(kahluaTable, byteBuffer);
			} catch (Exception exception) {
				exception.printStackTrace();
				return;
			}
		}

		if (string.equals("thunder")) {
			if (string2.equals("start")) {
				boolean boolean2 = kahluaTable != null && kahluaTable.rawget("big") instanceof Boolean && kahluaTable.rawget("big").equals(Boolean.TRUE);
				GameTime.getInstance().thunderStart(boolean2);
			}

			if (string2.equals("stop")) {
				GameTime.getInstance().thunderStop();
			}
		}

		if (!CGlobalObjects.receiveServerCommand(string, string2, kahluaTable)) {
			LuaEventManager.triggerEvent("OnServerCommand", string, string2, kahluaTable);
		}
	}

	private boolean receiveLargeFilePart(ByteBuffer byteBuffer, String string) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		File file = new File(string);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, int2 > 0);
			Throwable throwable = null;
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				Throwable throwable2 = null;
				try {
					bufferedOutputStream.write(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
				} catch (Throwable throwable3) {
					throwable2 = throwable3;
					throw throwable3;
				} finally {
					if (bufferedOutputStream != null) {
						if (throwable2 != null) {
							try {
								bufferedOutputStream.close();
							} catch (Throwable throwable4) {
								throwable2.addSuppressed(throwable4);
							}
						} else {
							bufferedOutputStream.close();
						}
					}
				}
			} catch (Throwable throwable5) {
				throwable = throwable5;
				throw throwable5;
			} finally {
				if (fileOutputStream != null) {
					if (throwable != null) {
						try {
							fileOutputStream.close();
						} catch (Throwable throwable6) {
							throwable.addSuppressed(throwable6);
						}
					} else {
						fileOutputStream.close();
					}
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return int2 + int3 >= int1;
	}

	private void receiveRequestData(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		if ("descriptors.bin".equals(string)) {
			try {
				this.receiveZombieDescriptors(byteBuffer);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			this.request = GameClient.RequestState.ReceivedDescriptors;
		}

		if ("playerzombiedesc".equals(string)) {
			try {
				this.receivePlayerZombieDescriptors(byteBuffer);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}

			this.request = GameClient.RequestState.ReceivedPlayerZombieDescriptors;
		}

		boolean boolean1;
		if ("map_meta.bin".equals(string)) {
			boolean1 = this.receiveLargeFilePart(byteBuffer, GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_meta.bin");
			if (boolean1) {
				this.request = GameClient.RequestState.ReceivedMetaGrid;
			}
		}

		if ("map_zone.bin".equals(string)) {
			boolean1 = this.receiveLargeFilePart(byteBuffer, GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_zone.bin");
			if (boolean1) {
				this.request = GameClient.RequestState.ReceivedMapZone;
			}
		}
	}

	public void GameLoadingRequestData() {
		this.request = GameClient.RequestState.Start;
		while (this.request != GameClient.RequestState.Complete) {
			ByteBufferWriter byteBufferWriter;
			switch (this.request) {
			case Start: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)37, byteBufferWriter);
				byteBufferWriter.putUTF("descriptors.bin");
				connection.endPacketImmediate();
				this.request = GameClient.RequestState.RequestDescriptors;
			
			case RequestDescriptors: 
			
			case RequestMetaGrid: 
			
			case RequestMapZone: 
			
			case RequestPlayerZombieDescriptors: 
			
			case Complete: 
			
			default: 
				break;
			
			case ReceivedDescriptors: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)37, byteBufferWriter);
				byteBufferWriter.putUTF("map_meta.bin");
				connection.endPacketImmediate();
				this.request = GameClient.RequestState.RequestMetaGrid;
				break;
			
			case ReceivedMetaGrid: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)37, byteBufferWriter);
				byteBufferWriter.putUTF("map_zone.bin");
				connection.endPacketImmediate();
				this.request = GameClient.RequestState.RequestMapZone;
				break;
			
			case ReceivedMapZone: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)37, byteBufferWriter);
				byteBufferWriter.putUTF("playerzombiedesc");
				connection.endPacketImmediate();
				this.request = GameClient.RequestState.RequestPlayerZombieDescriptors;
				break;
			
			case ReceivedPlayerZombieDescriptors: 
				this.request = GameClient.RequestState.Complete;
			
			}

			try {
				Thread.sleep(30L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	private void receiveMetaGrid(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		if (short1 >= metaGrid.getMinX() && short1 <= metaGrid.getMaxX() && short2 >= metaGrid.getMinY() && short2 <= metaGrid.getMaxY()) {
			IsoMetaCell metaCell = metaGrid.getCellData(short1, short2);
			if (metaCell.info != null && short3 >= 0 && short3 < metaCell.info.RoomList.size()) {
				metaCell.info.getRoom(short3).def.bLightsActive = byteBuffer.get() == 1;
			}
		}
	}

	private void receiveCustomColor(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (square != null && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (object != null) {
					object.setCustomColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()));
				}
			}
		}
	}

	private void updateItemSprite(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
		if (square == null) {
			this.delayPacket(int2, int3, int4);
		} else {
			if (square != null && int5 < square.getObjects().size()) {
				try {
					IsoObject object = (IsoObject)square.getObjects().get(int5);
					if (object != null) {
						boolean boolean1 = object.sprite != null && object.sprite.getProperties().Is("HitByCar") && object.sprite.getProperties().Val("DamagedSprite") != null && !object.sprite.getProperties().Val("DamagedSprite").isEmpty();
						object.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(int1);
						if (object.sprite == null && !string.isEmpty()) {
							object.setSprite(string);
						}

						object.RemoveAttachedAnims();
						int int6 = byteBuffer.get() & 255;
						for (int int7 = 0; int7 < int6; ++int7) {
							int int8 = byteBuffer.getInt();
							IsoSprite sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(int8);
							if (sprite != null) {
								object.AttachExistingAnim(sprite, 0, 0, false, 0, false, 0.0F);
							}
						}

						if (object instanceof IsoThumpable && boolean1 && (object.sprite == null || !object.sprite.getProperties().Is("HitByCar"))) {
							((IsoThumpable)object).setBlockAllTheSquare(false);
						}

						square.RecalcAllWithNeighbours(true);
					}
				} catch (Exception exception) {
				}
			}
		}
	}

	private void updateOverlay(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (square != null && int4 < square.getObjects().size()) {
				try {
					IsoObject object = (IsoObject)square.getObjects().get(int4);
					if (object != null) {
						object.setOverlaySprite(string, float1, float2, float3, float4, false);
					}
				} catch (Exception exception) {
				}
			}
		}
	}

	private KahluaTable copyTable(KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = LuaManager.platform.newTable();
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			Object object = kahluaTableIterator.getKey();
			Object object2 = kahluaTableIterator.getValue();
			if (object2 instanceof KahluaTable) {
				kahluaTable2.rawset(object, this.copyTable((KahluaTable)object2));
			} else {
				kahluaTable2.rawset(object, object2);
			}
		}

		return kahluaTable2;
	}

	public KahluaTable getServerSpawnRegions() {
		return this.copyTable(this.ServerSpawnRegions);
	}

	public static void toggleSafety(IsoPlayer player) {
		if (player != null) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)86, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			connection.endPacketImmediate();
		}
	}

	private void startFire(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		int int5 = byteBuffer.getInt();
		int int6 = byteBuffer.getInt();
		int int7 = byteBuffer.getInt();
		boolean boolean2 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else if (!IsoFire.CanAddFire(square, boolean1, boolean2)) {
			DebugLog.log("not adding fire that is on the server " + int1 + "," + int2);
		} else {
			IsoFire fire = boolean2 ? new IsoFire(IsoWorld.instance.CurrentCell, square, boolean1, int4, int6, true) : new IsoFire(IsoWorld.instance.CurrentCell, square, boolean1, int4, int6);
			fire.SpreadDelay = int5;
			fire.numFlameParticles = int7;
			IsoFireManager.Add(fire);
			square.getObjects().add(fire);
		}
	}

	private void AddCorpseToMap(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
		object.loadFromRemoteBuffer(byteBuffer, false);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (square != null) {
				square.addCorpse((IsoDeadBody)object, true);
			}
		}
	}

	private void RemoveCorpseFromMap(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (square != null && int4 >= 0 && int4 < square.getStaticMovingObjects().size()) {
				IsoObject object = (IsoObject)square.getStaticMovingObjects().get(int4);
				square.removeCorpse((IsoDeadBody)object, true);
			}

			if (int4 < 0) {
				DebugLog.log("Remove corpse index <||> 0, index = " + int4 + " X:" + int1 + " Y:" + int2 + " Z:" + int3);
			}
		}
	}

	private void SyncPlayerInventory(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			DebugLog.log("SyncPlayerInventory " + player.username);
			player.setInventory(new ItemContainer());
			try {
				player.getInventory().load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			short short2 = byteBuffer.getShort();
			if (short2 >= 0 && short2 < player.getInventory().getItems().size()) {
				player.setClothingItem_Torso((InventoryItem)player.getInventory().getItems().get(short2));
			}

			short2 = byteBuffer.getShort();
			if (short2 >= 0 && short2 < player.getInventory().getItems().size()) {
				player.setClothingItem_Legs((InventoryItem)player.getInventory().getItems().get(short2));
			}

			short2 = byteBuffer.getShort();
			if (short2 >= 0 && short2 < player.getInventory().getItems().size()) {
				player.setClothingItem_Feet((InventoryItem)player.getInventory().getItems().get(short2));
			}

			if (this.PlayerToBody.containsKey(player)) {
				((IsoDeadBody)this.PlayerToBody.get(player)).container = player.getInventory();
				((IsoDeadBody)this.PlayerToBody.get(player)).ClothingItem_Torso = player.getClothingItem_Torso();
				((IsoDeadBody)this.PlayerToBody.get(player)).ClothingItem_Legs = player.getClothingItem_Legs();
				((IsoDeadBody)this.PlayerToBody.get(player)).ClothingItem_Feet = player.getClothingItem_Feet();
				player.setInventory(new ItemContainer());
				this.PlayerToBody.remove(player);
			}
		}
	}

	private void receiveModData(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null && IsoWorld.instance.isValidSquare(int1, int2, int3) && IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, int3) != null) {
			square = IsoGridSquare.getNew(IsoWorld.instance.getCell(), (SliceY)null, int1, int2, int3);
		}

		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			try {
				square.getModData().load((ByteBuffer)byteBuffer, 143);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			LuaEventManager.triggerEvent("onLoadModDataFromServer", square);
		}
	}

	private void receiveObjectModData(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (boolean1) {
					try {
						object.getModData().load((ByteBuffer)byteBuffer, 143);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				} else {
					object.getModData().wipe();
				}
			} else if (square != null) {
				DebugLog.log("receiveObjectModData: index=" + int4 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
			} else if (Core.bDebug) {
				DebugLog.log("receiveObjectModData: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		}
	}

	private void receiveObjectChange(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		int int1;
		String string;
		if (byte1 == 1) {
			int1 = byteBuffer.getInt();
			string = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string);
			}

			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
			if (player != null) {
				player.loadChange(string, byteBuffer);
			}
		} else if (byte1 == 2) {
			short short1 = byteBuffer.getShort();
			string = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string);
			}

			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short1);
			if (baseVehicle != null) {
				baseVehicle.loadChange(string, byteBuffer);
			} else if (Core.bDebug) {
				DebugLog.log("receiveObjectChange: unknown vehicle id=" + short1);
			}
		} else {
			int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			String string2 = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string2);
			}

			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				this.delayPacket(int1, int2, int3);
				return;
			}

			if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				object.loadChange(string2, byteBuffer);
			} else if (square != null) {
				if (Core.bDebug) {
					DebugLog.log("receiveObjectChange: index=" + int4 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
				}
			} else if (Core.bDebug) {
				DebugLog.log("receiveObjectChange: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		}
	}

	private void RemoveContestedItemsFromInventory(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			long long1 = byteBuffer.getLong();
			for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
				IsoPlayer player = IsoPlayer.players[int3];
				if (player != null && !player.isDead()) {
					player.getInventory().removeItemWithIDRecurse(long1);
				}
			}
		}
	}

	private void doDeadZombie(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short1);
		if (zombie != null) {
			zombie.setX(float1);
			zombie.setY(float2);
			zombie.setZ(float3);
			zombie.getInventory().removeAllItems();
			zombie.getInventory().setSourceGrid(zombie.getCurrentSquare());
			if (boolean1) {
				try {
					zombie.getInventory().load(byteBuffer, 143, false);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				short short2 = byteBuffer.getShort();
				if (short2 >= 0 && short2 < zombie.getInventory().getItems().size()) {
					zombie.setClothingItem_Torso((InventoryItem)zombie.getInventory().getItems().get(short2));
				}

				short2 = byteBuffer.getShort();
				if (short2 >= 0 && short2 < zombie.getInventory().getItems().size()) {
					zombie.setClothingItem_Legs((InventoryItem)zombie.getInventory().getItems().get(short2));
				}

				short2 = byteBuffer.getShort();
				if (short2 >= 0 && short2 < zombie.getInventory().getItems().size()) {
					zombie.setClothingItem_Feet((InventoryItem)zombie.getInventory().getItems().get(short2));
				}
			}

			IsoDeadBody deadBody = new IsoDeadBody(zombie);
			if (deadBody.getCurrentSquare() != null) {
				deadBody.getCurrentSquare().clientModify();
			}
		}
	}

	private static void doZomboidDataInMainLoop(ZomboidNetData zomboidNetData) {
	}

	private void onPlayerDeath(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			if (player.getCurrentSquare() != null) {
				player.setHealth(-1.0F);
				player.getBodyDamage().setOverallBodyHealth(-1.0F);
				player.getStateMachine().Lock = false;
				player.changeState(DieState.instance());
				player.setRemoteMoveX(0.0F);
				player.setRemoteMoveY(0.0F);
			}

			if (player.isLocalPlayer()) {
				ByteBufferWriter byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)65, byteBufferWriter);
				byteBufferWriter.putByte((byte)player.PlayerIndex);
				player.getInventory().type = player.bFemale ? "inventoryfemale" : "inventorymale";
				try {
					ArrayList arrayList = player.getInventory().save(byteBufferWriter.bb, false);
					if (player.getClothingItem_Torso() != null) {
						byteBufferWriter.bb.putShort((short)arrayList.indexOf(player.getClothingItem_Torso()));
					} else {
						byteBufferWriter.bb.putShort((short)-1);
					}

					if (player.getClothingItem_Legs() != null) {
						byteBufferWriter.bb.putShort((short)arrayList.indexOf(player.getClothingItem_Legs()));
					} else {
						byteBufferWriter.bb.putShort((short)-1);
					}

					if (player.getClothingItem_Feet() != null) {
						byteBufferWriter.bb.putShort((short)arrayList.indexOf(player.getClothingItem_Feet()));
					} else {
						byteBufferWriter.bb.putShort((short)-1);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				connection.endPacketImmediate();
			}
		}
	}

	private void onPlayerOnBeaten(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			player.doBeatenVehicle(float1, float2, float3, true);
		}
	}

	private void removeZombieCompletely(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short1);
		if (zombie != null) {
			assert false;
			if (zombie.getCurrentSquare() == null) {
				return;
			}

			new IsoDeadBody(zombie);
			assert !IDToZombieMap.containsKey(short1);
		}
	}

	private void RemoveItemFromMap(ByteBuffer byteBuffer) {
		if (IsoWorld.instance.CurrentCell != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				this.delayPacket(int1, int2, int3);
			} else {
				if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
					IsoObject object = (IsoObject)square.getObjects().get(int4);
					square.RemoveTileObject(object);
					if (object instanceof IsoWorldInventoryObject || object.getContainer() != null) {
						LuaEventManager.triggerEvent("OnContainerUpdate", object);
					}
				} else if (Core.bDebug) {
					DebugLog.log("RemoveItemFromMap: sq is null or index is invalid");
				}
			}
		}
	}

	private void removeItemFromContainer(ByteBuffer byteBuffer) {
		if (IsoWorld.instance.CurrentCell != null) {
			ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
			short short1 = byteBuffer.getShort();
			int int1 = byteBufferReader.getInt();
			int int2 = byteBufferReader.getInt();
			int int3 = byteBufferReader.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null) {
				byte byte1;
				if (short1 == 0) {
					byte1 = byteBufferReader.getByte();
					int int4 = byteBuffer.getInt();
					if (byte1 < 0 || byte1 >= square.getStaticMovingObjects().size()) {
						DebugLog.log("ERROR: removeItemFromContainer: invalid corpse index");
						return;
					}

					IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
					if (object != null && object.getContainer() != null) {
						for (int int5 = 0; int5 < int4; ++int5) {
							long long1 = byteBufferReader.getLong();
							object.getContainer().removeItemWithID(long1);
							object.getContainer().setExplored(true);
						}
					}
				} else {
					int int6;
					if (short1 == 1) {
						long long2 = byteBufferReader.getLong();
						int6 = byteBuffer.getInt();
						ItemContainer itemContainer = null;
						int int7;
						for (int7 = 0; int7 < square.getWorldObjects().size(); ++int7) {
							IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int7);
							if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == long2) {
								itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
								break;
							}
						}

						if (itemContainer == null) {
							DebugLog.log("ERROR removeItemFromContainer can\'t find world item with id=" + long2);
							return;
						}

						for (int7 = 0; int7 < int6; ++int7) {
							long long3 = byteBufferReader.getLong();
							itemContainer.removeItemWithID(long3);
							itemContainer.setExplored(true);
						}
					} else {
						byte byte2;
						if (short1 == 2) {
							byte1 = byteBufferReader.getByte();
							byte2 = byteBufferReader.getByte();
							int6 = byteBuffer.getInt();
							if (byte1 < 0 || byte1 >= square.getObjects().size()) {
								DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
								return;
							}

							IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
							ItemContainer itemContainer2 = object2 != null ? object2.getContainerByIndex(byte2) : null;
							if (itemContainer2 != null) {
								for (int int8 = 0; int8 < int6; ++int8) {
									long long4 = byteBufferReader.getLong();
									itemContainer2.removeItemWithID(long4);
									itemContainer2.setExplored(true);
								}
							}
						} else if (short1 == 3) {
							short short2 = byteBufferReader.getShort();
							byte2 = byteBufferReader.getByte();
							int6 = byteBuffer.getInt();
							BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
							if (baseVehicle == null) {
								DebugLog.log("ERROR: removeItemFromContainer: invalid vehicle id");
								return;
							}

							VehiclePart vehiclePart = baseVehicle.getPartByIndex(byte2);
							if (vehiclePart == null) {
								DebugLog.log("ERROR: removeItemFromContainer: invalid part index");
								return;
							}

							ItemContainer itemContainer3 = vehiclePart.getItemContainer();
							if (itemContainer3 == null) {
								DebugLog.log("ERROR: removeItemFromContainer: part " + vehiclePart.getId() + " has no container");
								return;
							}

							if (itemContainer3 != null) {
								for (int int9 = 0; int9 < int6; ++int9) {
									long long5 = byteBufferReader.getLong();
									itemContainer3.removeItemWithID(long5);
									itemContainer3.setExplored(true);
								}

								vehiclePart.setContainerContentAmount(itemContainer3.getCapacityWeight());
							}
						} else {
							DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
						}
					}
				}
			} else {
				this.delayPacket(int1, int2, int3);
			}
		}
	}

	private void sendItemsToContainer(ByteBuffer byteBuffer) {
		if (IsoWorld.instance.CurrentCell != null) {
			ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
			short short1 = byteBuffer.getShort();
			int int1 = byteBufferReader.getInt();
			int int2 = byteBufferReader.getInt();
			int int3 = byteBufferReader.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				this.delayPacket(int1, int2, int3);
			} else {
				ItemContainer itemContainer = null;
				VehiclePart vehiclePart = null;
				byte byte1;
				if (short1 == 0) {
					byte1 = byteBufferReader.getByte();
					if (byte1 < 0 || byte1 >= square.getStaticMovingObjects().size()) {
						DebugLog.log("ERROR: sendItemsToContainer: invalid corpse index");
						return;
					}

					IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
					if (object != null && object.getContainer() != null) {
						itemContainer = object.getContainer();
					}
				} else if (short1 == 1) {
					long long1 = byteBufferReader.getLong();
					for (int int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
						IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
						if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == long1) {
							itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
							break;
						}
					}

					if (itemContainer == null) {
						DebugLog.log("ERROR: sendItemsToContainer: can\'t find world item with id=" + long1);
						return;
					}
				} else {
					byte byte2;
					if (short1 == 2) {
						byte1 = byteBufferReader.getByte();
						byte2 = byteBufferReader.getByte();
						if (byte1 < 0 || byte1 >= square.getObjects().size()) {
							DebugLog.log("ERROR: sendItemsToContainer: invalid object index");
							return;
						}

						IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
						itemContainer = object2 != null ? object2.getContainerByIndex(byte2) : null;
					} else if (short1 == 3) {
						short short2 = byteBufferReader.getShort();
						byte2 = byteBufferReader.getByte();
						BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
						if (baseVehicle == null) {
							DebugLog.log("ERROR: sendItemsToContainer: invalid vehicle id");
							return;
						}

						vehiclePart = baseVehicle.getPartByIndex(byte2);
						if (vehiclePart == null) {
							DebugLog.log("ERROR: sendItemsToContainer: invalid part index");
							return;
						}

						itemContainer = vehiclePart.getItemContainer();
						if (itemContainer == null) {
							DebugLog.log("ERROR: sendItemsToContainer: part " + vehiclePart.getId() + " has no container");
							return;
						}
					} else {
						DebugLog.log("ERROR: sendItemsToContainer: unknown container type");
					}
				}

				if (itemContainer != null) {
					try {
						ArrayList arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 143, (ArrayList)null, (ArrayList)null);
						for (int int5 = 0; int5 < arrayList.size(); ++int5) {
							InventoryItem inventoryItem = (InventoryItem)arrayList.get(int5);
							if (inventoryItem != null) {
								if (itemContainer.containsID(inventoryItem.id)) {
									if (short1 != 0) {
										System.out.println("Error: Dupe item ID.");
									}
								} else {
									itemContainer.addItem(inventoryItem);
									itemContainer.setExplored(true);
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}

					if (vehiclePart != null) {
						vehiclePart.setContainerContentAmount(itemContainer.getCapacityWeight());
					}
				}
			}
		}
	}

	private void readItemStats(ByteBuffer byteBuffer, InventoryItem inventoryItem) {
		int int1 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		inventoryItem.setUses(int1);
		if (inventoryItem instanceof DrainableComboItem) {
			((DrainableComboItem)inventoryItem).setDelta(float1);
			((DrainableComboItem)inventoryItem).updateWeight();
		}

		if (boolean1 && inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			food.setHungChange(byteBuffer.getFloat());
			food.setCalories(byteBuffer.getFloat());
			food.setCarbohydrates(byteBuffer.getFloat());
			food.setLipids(byteBuffer.getFloat());
			food.setProteins(byteBuffer.getFloat());
			food.setThirstChange(byteBuffer.getFloat());
			food.setFluReduction(byteBuffer.getInt());
			food.setPainReduction(byteBuffer.getFloat());
			food.setEndChange(byteBuffer.getFloat());
			food.setReduceFoodSickness(byteBuffer.getInt());
			food.setStressChange(byteBuffer.getFloat());
			food.setFatigueChange(byteBuffer.getFloat());
		}
	}

	private void receiveItemStats(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		byte byte1;
		long long1;
		byte byte2;
		ItemContainer itemContainer;
		InventoryItem inventoryItem;
		switch (short1) {
		case 0: 
			byte2 = byteBuffer.get();
			long long2 = byteBuffer.getLong();
			if (square != null && byte2 >= 0 && byte2 < square.getStaticMovingObjects().size()) {
				IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(byte2);
				itemContainer = movingObject.getContainer();
				if (itemContainer != null) {
					inventoryItem = itemContainer.getItemWithID(long2);
					if (inventoryItem != null) {
						this.readItemStats(byteBuffer, inventoryItem);
					}
				}
			}

			break;
		
		case 1: 
			long long3 = byteBuffer.getLong();
			if (square != null) {
				for (int int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
					if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().id == long3) {
						this.readItemStats(byteBuffer, worldInventoryObject.getItem());
						break;
					}

					if (worldInventoryObject.getItem() instanceof InventoryContainer) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						inventoryItem = itemContainer.getItemWithID(long3);
						if (inventoryItem != null) {
							this.readItemStats(byteBuffer, inventoryItem);
							break;
						}
					}
				}
			}

			break;
		
		case 2: 
			byte2 = byteBuffer.get();
			byte1 = byteBuffer.get();
			long1 = byteBuffer.getLong();
			if (square != null && byte2 >= 0 && byte2 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte2);
				ItemContainer itemContainer2 = object.getContainerByIndex(byte1);
				if (itemContainer2 != null) {
					InventoryItem inventoryItem2 = itemContainer2.getItemWithID(long1);
					if (inventoryItem2 != null) {
						this.readItemStats(byteBuffer, inventoryItem2);
					}
				}
			}

			break;
		
		case 3: 
			short short2 = byteBuffer.getShort();
			byte1 = byteBuffer.get();
			long1 = byteBuffer.getLong();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(byte1);
				if (vehiclePart != null) {
					ItemContainer itemContainer3 = vehiclePart.getItemContainer();
					if (itemContainer3 != null) {
						InventoryItem inventoryItem3 = itemContainer3.getItemWithID(long1);
						if (inventoryItem3 != null) {
							this.readItemStats(byteBuffer, inventoryItem3);
						}
					}
				}
			}

		
		}
	}

	public static boolean canSeePlayerStats() {
		return !accessLevel.equals("");
	}

	public static boolean canModifyPlayerStats() {
		return accessLevel.equals("admin") || accessLevel.equals("moderator") || accessLevel.equals("overseer");
	}

	public void sendPersonalColor(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)138, byteBufferWriter);
		byteBufferWriter.putInt(player.getOnlineID());
		byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().r);
		byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().g);
		byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().b);
		connection.endPacketImmediate();
	}

	public void sendChangedPlayerStats(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		player.createPlayerStats(byteBufferWriter, username);
		connection.endPacketImmediate();
	}

	private void receivePlayerStatsChanges(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			String string = GameWindow.ReadString(byteBuffer);
			player.setPlayerStats(byteBuffer, string);
			allChatMuted = player.isAllChatMuted();
		}
	}

	public void writePlayerConnectData(ByteBufferWriter byteBufferWriter, IsoPlayer player) {
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putByte((byte)IsoChunkMap.ChunkGridWidth);
		byteBufferWriter.putFloat(player.x);
		byteBufferWriter.putFloat(player.y);
		byteBufferWriter.putFloat(player.z);
		try {
			player.getDescriptor().save(byteBufferWriter.bb);
			player.getXp().save(byteBufferWriter.bb);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		byteBufferWriter.putBoolean(player.isAllChatMuted());
		byteBufferWriter.putUTF(player.getTagPrefix());
		byteBufferWriter.putFloat(player.getTagColor().r);
		byteBufferWriter.putFloat(player.getTagColor().g);
		byteBufferWriter.putFloat(player.getTagColor().b);
		byteBufferWriter.putInt(player.getTransactionID());
		byteBufferWriter.putDouble(player.getHoursSurvived());
		byteBufferWriter.putInt(player.getZombieKills());
		byteBufferWriter.putUTF(player.getDisplayName());
		byteBufferWriter.putFloat(player.getSpeakColour().r);
		byteBufferWriter.putFloat(player.getSpeakColour().g);
		byteBufferWriter.putFloat(player.getSpeakColour().b);
		byteBufferWriter.putBoolean(player.showTag);
		byteBufferWriter.putBoolean(player.factionPvp);
		if (SteamUtils.isSteamModeEnabled()) {
			byteBufferWriter.putUTF(SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()));
		}

		connection.username = player.username;
	}

	public void sendPlayerConnect(IsoPlayer player) {
		player.OnlineID = -1;
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)6, byteBufferWriter);
		this.writePlayerConnectData(byteBufferWriter, player);
		connection.endPacketImmediate();
		allChatMuted = player.isAllChatMuted();
		player.setPrimaryHandItem(player.getPrimaryHandItem());
		player.setSecondaryHandItem(player.getSecondaryHandItem());
		this.bPlayerConnectSent = true;
	}

	public void sendPlayerSave(IsoPlayer player) {
		if (connection != null) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)162, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putInt(player.OnlineID);
			byteBufferWriter.putFloat(player.x);
			byteBufferWriter.putFloat(player.y);
			byteBufferWriter.putFloat(player.z);
			connection.endPacketImmediate();
		}
	}

	public void sendPlayer(IsoPlayer player) {
		if (player.OnlineID == -1) {
			System.out.println("OnlineID of player is -1");
		} else {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)7, byteBufferWriter);
			byteBufferWriter.putShort((short)player.OnlineID);
			byteBufferWriter.putByte((byte)player.dir.index());
			byteBufferWriter.putFloat(player.x);
			byteBufferWriter.putFloat(player.y);
			byteBufferWriter.putFloat(player.z);
			byteBufferWriter.putFloat(player.playerMoveDir.x);
			byteBufferWriter.putFloat(player.playerMoveDir.y);
			byteBufferWriter.putByte(player.NetRemoteState);
			if (player.legsSprite != null) {
				byteBufferWriter.putByte((byte)player.legsSprite.AnimStack.indexOf(player.legsSprite.CurrentAnim));
			} else {
				byteBufferWriter.putByte((byte)0);
			}

			byteBufferWriter.putByte((byte)((int)player.def.Frame));
			byteBufferWriter.putFloat(player.def.AnimFrameIncrease);
			byteBufferWriter.putFloat(player.CurrentSpeed);
			byteBufferWriter.putFloat(player.getLightDistance());
			byteBufferWriter.putFloat(player.getTorchStrength());
			BaseVehicle baseVehicle = player.getVehicle();
			if (baseVehicle == null) {
				byteBufferWriter.putShort((short)-1);
				byteBufferWriter.putShort((short)-1);
			} else {
				byteBufferWriter.putShort(baseVehicle.VehicleID);
				byteBufferWriter.putShort((short)baseVehicle.getSeat(player));
			}

			byte byte1 = 0;
			if (player.def.Finished) {
				byte1 = (byte)(byte1 | 1);
			}

			if (player.def.Looped) {
				byte1 = (byte)(byte1 | 2);
			}

			if (player.legsSprite != null && player.legsSprite.CurrentAnim != null && player.legsSprite.CurrentAnim.FinishUnloopedOnFrame == 0) {
				byte1 = (byte)(byte1 | 4);
			}

			if (player.bSneaking) {
				byte1 = (byte)(byte1 | 8);
			}

			if (player.isTorchCone()) {
				byte1 = (byte)(byte1 | 16);
			}

			if (player.isOnFire()) {
				byte1 = (byte)(byte1 | 32);
			}

			if (player.isAsleep()) {
				byte1 = (byte)(byte1 | 64);
			}

			if (player.isClimbing()) {
				byte1 = (byte)(byte1 | 128);
			}

			byteBufferWriter.putByte(byte1);
			connection.endPacketSuperHighUnreliable();
		}
	}

	public void sendSteamProfileName(long long1) {
		if (SteamUtils.isSteamModeEnabled()) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.getSteamID() == long1) {
					ByteBufferWriter byteBufferWriter = connection.startPacket();
					PacketTypes.doPacket((short)14, byteBufferWriter);
					byteBufferWriter.putShort((short)0);
					byteBufferWriter.putByte((byte)player.getPlayerNum());
					byteBufferWriter.putUTF(SteamFriends.GetFriendPersonaName(long1));
					connection.endPacketUnordered();
					return;
				}
			}
		}
	}

	public void heartBeat() {
		++count;
	}

	public static IsoZombie getZombie(short short1) {
		return (IsoZombie)IDToZombieMap.get(short1);
	}

	private void onZombieDie(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		short short1 = byteBuffer.getShort();
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get((short)int1);
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (zombie != null) {
			zombie.setHealth(-2.0F);
			zombie.DoDeath((HandWeapon)player.getPrimaryHandItem(), player);
		}
	}

	public void receiveZombieInfo(ByteBuffer byteBuffer) {
		ZombieUpdatePacker.instance.updateZombiesFromPacket(byteBuffer);
	}

	public static void sendPlayerExtraInfo(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)84, byteBufferWriter);
		byteBufferWriter.putShort((short)player.OnlineID);
		byteBufferWriter.putUTF(player.accessLevel);
		byteBufferWriter.putByte((byte)(player.godMod ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.invisible ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isNoClip() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isShowAdminTag() ? 1 : 0));
		connection.endPacketImmediate();
	}

	private static void receivePlayerExtraInfo(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		boolean boolean3 = byteBuffer.get() == 1;
		boolean boolean4 = byteBuffer.get() == 1;
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			if (Core.bDebug) {
				DebugLog.log("receivePlayerExtraInfo " + player.username + " godMod=" + boolean1 + " invisible=" + boolean2);
			}

			player.accessLevel = string;
			player.godMod = boolean1;
			player.invisible = boolean2;
			player.GhostMode = boolean2;
			player.setNoClip(boolean3);
			player.setShowAdminTag(boolean4);
			if (!player.bRemote) {
				accessLevel = string;
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player2 = IsoPlayer.players[int1];
					if (player2 != null && !player2.accessLevel.equals("")) {
						accessLevel = player2.accessLevel;
						break;
					}
				}
			}
		}
	}

	public void receivePlayerInfo(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		long long1 = byteBuffer.getLong();
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		float float5 = byteBuffer.getFloat();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		byte byte4 = byteBuffer.get();
		float float6 = byteBuffer.getFloat();
		float float7 = byteBuffer.getFloat();
		float float8 = byteBuffer.getFloat();
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
		byte byte5 = byteBuffer.get();
		boolean boolean1 = (byte5 & 1) != 0;
		boolean boolean2 = (byte5 & 2) != 0;
		boolean boolean3 = (byte5 & 4) != 0;
		boolean boolean4 = (byte5 & 16) != 0;
		boolean boolean5 = (byte5 & 32) != 0;
		boolean boolean6 = (byte5 & 64) != 0;
		boolean boolean7 = (byte5 & 128) != 0;
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player == null) {
			DebugLog.log("PlayerUpdateInfo for unknown player " + short1 + " -> RequestPlayerData");
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)67, byteBufferWriter);
			byteBufferWriter.putShort(short1);
			connection.endPacketImmediate();
		} else {
			player.setRemoteState(byte2);
			IsoAnim anim = (IsoAnim)player.legsSprite.AnimStack.get(byte3);
			player.setDir(byte1);
			if (baseVehicle == null) {
				player.setX(float1);
				player.setY(float2);
				player.setZ(float3);
			}

			if (byte2 != IsoPlayer.NetRemoteState_Attack) {
				player.setRemoteMoveX(float4);
				player.setRemoteMoveY(float5);
			} else {
				player.setRemoteMoveX(0.0F);
				player.setRemoteMoveY(0.0F);
			}

			player.TimeSinceLastNetData = 0;
			if (player.isAnimForecasted() && player.legsSprite.CurrentAnim.name.equals(anim.name)) {
				player.setAnimForecasted(0);
			}

			if (!player.isAnimForecasted()) {
				player.PlayAnim(anim.name);
				player.def.Frame = (float)byte4;
				player.def.Finished = boolean1;
				player.def.Looped = boolean2;
				if (player.legsSprite != null && player.legsSprite.CurrentAnim != null && boolean3) {
					player.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
				}

				player.def.AnimFrameIncrease = float6;
				player.angle.x = player.dir.ToVector().x;
				player.angle.y = player.dir.ToVector().y;
				player.mpTorchDist = float7;
				player.mpTorchStrength = float8;
				player.mpTorchCone = boolean4;
			}

			if (boolean5) {
				player.SetOnFire();
			} else {
				player.StopBurning();
			}

			player.setAsleep(boolean6);
			player.setbClimbing(boolean7);
			IsoGameCharacter gameCharacter;
			if (player.getVehicle() == null) {
				if (baseVehicle != null) {
					if (short3 >= 0 && short3 < baseVehicle.getMaxPassengers()) {
						gameCharacter = baseVehicle.getCharacter(short3);
						if (gameCharacter == null) {
							if (Core.bDebug) {
								DebugLog.log(player.getUsername() + " got in vehicle " + baseVehicle.VehicleID + " seat " + short3);
							}

							player.setVehicle(baseVehicle);
							baseVehicle.enterRSync(short3, player, baseVehicle);
						} else if (gameCharacter != player) {
							DebugLog.log(player.getUsername() + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
						}
					} else {
						DebugLog.log(player.getUsername() + " invalid seat vehicle " + baseVehicle.VehicleID + " seat " + short3);
					}
				}
			} else if (baseVehicle != null) {
				if (baseVehicle == player.getVehicle() && player.getVehicle().getSeat(player) != -1) {
					gameCharacter = baseVehicle.getCharacter(short3);
					if (gameCharacter == null) {
						if (baseVehicle.getSeat(player) != short3) {
							baseVehicle.switchSeatRSync(player, short3);
						}
					} else if (gameCharacter != player) {
						DebugLog.log(player.getUsername() + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
					}
				} else {
					DebugLog.log(player.getUsername() + " vehicle/seat remote " + baseVehicle.VehicleID + "/" + short3 + " local " + player.getVehicle().VehicleID + "/" + player.getVehicle().getSeat(player));
				}
			} else {
				player.getVehicle().exitRSync(player);
				player.setVehicle((BaseVehicle)null);
			}

			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
			if (square != null) {
				if (!IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
					IsoWorld.instance.CurrentCell.getObjectList().add(player);
				}
			} else if (IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
				player.removeFromWorld();
				player.removeFromSquare();
			}
		}
	}

	public void receiveConnectionDetails(ByteBuffer byteBuffer) {
		Calendar calendar = Calendar.getInstance();
		System.out.println("LOGGED INTO : " + (calendar.getTimeInMillis() - startAuth.getTimeInMillis()) + " millisecond");
		ConnectToServerState connectToServerState = new ConnectToServerState(byteBuffer);
		connectToServerState.enter();
		MainScreenState.getInstance().setConnectToServerState(connectToServerState);
	}

	public void setResetID(int int1) {
		this.loadResetID();
		if (this.ResetID != int1) {
			boolean boolean1 = true;
			ArrayList arrayList = IsoPlayer.getAllFileNames();
			arrayList.add("map_p.bin");
			int int2;
			File file;
			File file2;
			if (boolean1) {
				for (int2 = 0; int2 < arrayList.size(); ++int2) {
					try {
						file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + (String)arrayList.get(int2));
						if (file.exists()) {
							file2 = new File(GameWindow.getCacheDir() + File.separator + (String)arrayList.get(int2));
							if (file2.exists()) {
								file2.delete();
							}

							file.renameTo(file2);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}

			DebugLog.log("server was reset, deleting " + Core.GameSaveWorld);
			LuaManager.GlobalObject.deleteSave(Core.GameSaveWorld);
			LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
			if (boolean1) {
				for (int2 = 0; int2 < arrayList.size(); ++int2) {
					try {
						file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + (String)arrayList.get(int2));
						file2 = new File(GameWindow.getCacheDir() + File.separator + (String)arrayList.get(int2));
						if (file2 != null) {
							file2.renameTo(file);
						}
					} catch (Exception exception2) {
						exception2.printStackTrace();
					}
				}
			}
		}

		this.ResetID = int1;
		this.saveResetID();
	}

	public void loadResetID() {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "serverid.dat");
		if (file.exists()) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
			} catch (FileNotFoundException fileNotFoundException) {
				fileNotFoundException.printStackTrace();
			}

			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			try {
				this.ResetID = dataInputStream.readInt();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			try {
				fileInputStream.close();
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}
	}

	private void saveResetID() {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "serverid.dat");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		}

		DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
		try {
			dataOutputStream.writeInt(this.ResetID);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		try {
			fileOutputStream.close();
		} catch (IOException ioException2) {
			ioException2.printStackTrace();
		}
	}

	public void receivePlayerConnect(ByteBuffer byteBuffer) {
		boolean boolean1 = false;
		short short1 = byteBuffer.getShort();
		byte byte1 = -1;
		if (short1 == -1) {
			boolean1 = true;
			byte1 = byteBuffer.get();
			short1 = byteBuffer.getShort();
			try {
				GameTime.getInstance().load(byteBuffer);
				GameTime.getInstance().ServerTimeOfDay = GameTime.getInstance().getTimeOfDay();
				GameTime.getInstance().ServerNewDays = 0;
				GameTime.getInstance().setMinutesPerDay((float)SandboxOptions.instance.getDayLengthMinutes());
				LuaEventManager.triggerEvent("OnGameTimeLoaded");
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else if (IDToPlayerMap.containsKey(Integer.valueOf(short1))) {
			return;
		}

		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		IsoPlayer player = null;
		SurvivorDesc survivorDesc = null;
		if (!boolean1) {
			survivorDesc = SurvivorFactory.CreateSurvivor();
			try {
				survivorDesc.load(byteBuffer, 143, (IsoGameCharacter)null);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}

		String string = GameWindow.ReadString(byteBuffer);
		if (boolean1) {
			for (int int1 = 0; int1 < IsoWorld.instance.AddCoopPlayers.size(); ++int1) {
				((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(int1)).receivePlayerConnect(byte1);
			}

			player = IsoPlayer.players[byte1];
			player.username = string;
			player.OnlineID = short1;
		} else {
			try {
				player = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)float1, (int)float2, (int)float3);
				player.username = string;
				player.setModel(player.isFemale() ? "kate" : "male");
				player.updateUsername();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			player.bRemote = true;
			player.setX(float1);
			player.setY(float2);
			player.setZ(float3);
		}

		player.OnlineID = short1;
		if (SteamUtils.isSteamModeEnabled()) {
			player.setSteamID(byteBuffer.getLong());
		}

		player.godMod = byteBuffer.get() == 1;
		player.setSafety(byteBuffer.get() == 1);
		player.accessLevel = GameWindow.ReadString(byteBuffer);
		player.invisible = byteBuffer.get() == 1;
		player.GhostMode = player.invisible;
		if (!boolean1 && canSeePlayerStats()) {
			try {
				player.getXp().load(byteBuffer, 143);
			} catch (IOException ioException3) {
				ioException3.printStackTrace();
			}
		}

		player.setTagPrefix(GameWindow.ReadString(byteBuffer));
		player.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		player.setHoursSurvived(byteBuffer.getDouble());
		player.setZombieKills(byteBuffer.getInt());
		player.setDisplayName(GameWindow.ReadString(byteBuffer));
		player.setSpeakColour(new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		player.showTag = byteBuffer.get() == 1;
		player.factionPvp = byteBuffer.get() == 1;
		if (Core.bDebug) {
			DebugLog.log(DebugType.Network, "Player Connect received for player " + string + " id " + short1 + (boolean1 ? " (local)" : " (remote)"));
		}

		IDToPlayerMap.put(Integer.valueOf(short1), player);
		this.idMapDirty = true;
		LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
		if (boolean1) {
			getCustomModData();
		}

		if (!boolean1 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
			SafeHouse safeHouse = SafeHouse.hasSafehouse(player);
			if (safeHouse != null) {
				safeHouse.setPlayerConnected(safeHouse.getPlayerConnected() + 1);
			}
		}

		String string2 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
		if (boolean1 && string2 != null && !string2.equals("")) {
			ChatManager.getInstance().showServerChatMessage(string2);
		}
	}

	public boolean receivePlayerConnectWhileLoading(ByteBuffer byteBuffer) {
		boolean boolean1 = false;
		short short1 = byteBuffer.getShort();
		byte byte1 = -1;
		if (short1 != -1) {
			return false;
		} else {
			if (short1 == -1) {
				boolean1 = true;
				byte1 = byteBuffer.get();
				short1 = byteBuffer.getShort();
				try {
					GameTime.getInstance().load(byteBuffer);
					LuaEventManager.triggerEvent("OnGameTimeLoaded");
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}

			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			IsoPlayer player = null;
			SurvivorDesc survivorDesc = null;
			if (!boolean1) {
				survivorDesc = SurvivorFactory.CreateSurvivor();
				try {
					survivorDesc.load(byteBuffer, 143, (IsoGameCharacter)null);
				} catch (IOException ioException2) {
					ioException2.printStackTrace();
				}
			}

			String string = GameWindow.ReadString(byteBuffer);
			if (boolean1) {
				player = IsoPlayer.players[byte1];
				player.username = string;
				player.OnlineID = short1;
			} else {
				try {
					player = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)float1, (int)float2, (int)float3);
					player.username = string;
					player.setModel(player.isFemale() ? "kate" : "male");
					player.updateUsername();
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				player.bRemote = true;
				player.setX(float1);
				player.setY(float2);
				player.setZ(float3);
			}

			player.OnlineID = short1;
			if (Core.bDebug) {
				DebugLog.log(DebugType.Network, "Player Connect received for player " + string + " id " + short1 + (boolean1 ? " (me)" : " (not me)"));
			}

			int int1 = byteBuffer.getInt();
			for (int int2 = 0; int2 < int1; ++int2) {
				ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
			}

			player.godMod = byteBuffer.get() == 1;
			player.setSafety(byteBuffer.get() == 1);
			player.accessLevel = GameWindow.ReadString(byteBuffer);
			player.invisible = byteBuffer.get() == 1;
			player.GhostMode = player.invisible;
			IDToPlayerMap.put(Integer.valueOf(short1), player);
			this.idMapDirty = true;
			getCustomModData();
			String string2 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
			if (boolean1 && string2 != null && !string2.equals("")) {
				ChatManager.getInstance().showServerChatMessage(string2);
			}

			return true;
		}
	}

	public ArrayList getPlayers() {
		if (!this.idMapDirty) {
			return this.players;
		} else {
			this.players.clear();
			this.players.addAll(IDToPlayerMap.values());
			this.idMapDirty = false;
			return this.players;
		}
	}

	public void createZombie(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		for (int int1 = 0; int1 < short1; ++int1) {
			short short2 = byteBuffer.getShort();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
			if (square == null) {
				return;
			}

			VirtualZombieManager.instance.choices.clear();
			VirtualZombieManager.instance.choices.add(square);
			IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(0, false);
			IDToZombieMap.put(short2, zombie);
			zombie.OnlineID = short2;
			zombie.setFakeDead(false);
		}
	}

	public void loadingChunk(int int1, int int2) {
	}

	public void chunkDetails(ByteBufferReader byteBufferReader) {
	}

	public void doneLogin() {
	}

	private static void putIsoObjectRefToByteBuffer(IsoObject object, ByteBuffer byteBuffer) {
		byteBuffer.putInt(object.square.x);
		byteBuffer.putInt(object.square.y);
		byteBuffer.putInt(object.square.z);
		byteBuffer.put((byte)object.square.getObjects().indexOf(object));
	}

	private IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
			return null;
		} else {
			return byte1 >= 0 && byte1 < square.getObjects().size() ? (IsoObject)square.getObjects().get(byte1) : null;
		}
	}

	public void sendWeaponHit(IsoPlayer player, HandWeapon handWeapon, IsoObject object) {
		if (player != null && object != null) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)28, byteBufferWriter);
			byteBufferWriter.putInt(object.square.x);
			byteBufferWriter.putInt(object.square.y);
			byteBufferWriter.putInt(object.square.z);
			byteBufferWriter.putByte((byte)object.getObjectIndex());
			byteBufferWriter.putShort((short)player.OnlineID);
			byteBufferWriter.putUTF(handWeapon != null ? handWeapon.getFullType() : "");
			connection.endPacketImmediate();
		}
	}

	public static void SyncCustomLightSwitchSettings(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
			if (square.getObjects().get(byte1) instanceof IsoLightSwitch) {
				((IsoLightSwitch)square.getObjects().get(byte1)).receiveSyncCustomizedSettings(byteBuffer, (UdpConnection)null);
			} else {
				DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		} else if (square != null) {
			DebugLog.log("Sync Lightswitch custom settings: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
		} else if (Core.bDebug) {
			DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
		}
	}

	public void SyncIsoObjectReq(ByteBuffer byteBuffer) {
		if (SystemDisabler.doObjectStateSyncEnable) {
			short short1 = byteBuffer.getShort();
			for (int int1 = 0; int1 < short1; ++int1) {
				this.SyncIsoObject(byteBuffer);
			}
		}
	}

	public void SyncWorldObjectsReq(ByteBuffer byteBuffer) {
		DebugLog.log("SyncWorldObjectsReq client : ");
		short short1 = byteBuffer.getShort();
		for (int int1 = 0; int1 < short1; ++int1) {
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			instance.worldObjectsSyncReq.receiveSyncIsoChunk(int2, int3);
			short short2 = byteBuffer.getShort();
			DebugLog.log("[" + int2 + "," + int3 + "]:" + short2 + " ");
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2 * 10, int3 * 10, 0);
			if (square == null) {
				return;
			}

			IsoChunk chunk = square.getChunk();
			for (int int4 = 0; int4 < short2; ++int4) {
			}

			++chunk.ObjectsSyncCount;
			chunk.recalcHashCodeObjects();
		}

		DebugLog.log(";\n");
	}

	public void SyncObjectsReq(ByteBuffer byteBuffer) {
		if (SystemDisabler.doWorldSyncEnable) {
			short short1 = byteBuffer.getShort();
			if (short1 == 2) {
				instance.worldObjectsSyncReq.receiveGridSquareHashes(byteBuffer);
			}

			if (short1 == 4) {
				instance.worldObjectsSyncReq.receiveGridSquareObjectHashes(byteBuffer);
			}

			if (short1 == 6) {
				instance.worldObjectsSyncReq.receiveObject(byteBuffer);
			}
		}
	}

	public void SyncIsoObject(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		if (byte2 != 2) {
			this.objectSyncReq.receiveIsoSync(int1, int2, int3, byte1);
		}

		if (byte2 == 1) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				return;
			}

			if (byte1 >= 0 && byte1 < square.getObjects().size()) {
				((IsoObject)square.getObjects().get(byte1)).syncIsoObject(true, byte3, (UdpConnection)null, byteBuffer);
			} else {
				DebugLog.log("SyncIsoObject: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		}
	}

	public static void SyncAlarmClock(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		if (short1 == AlarmClock.PacketPlayer) {
			short short2 = byteBuffer.getShort();
			long long1 = byteBuffer.getLong();
			boolean boolean1 = byteBuffer.get() == 1;
			int int1 = boolean1 ? 0 : byteBuffer.getInt();
			int int2 = boolean1 ? 0 : byteBuffer.getInt();
			byte byte1 = boolean1 ? 0 : byteBuffer.get();
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short2));
			if (player != null) {
				for (int int3 = 0; int3 < player.getInventory().getItems().size(); ++int3) {
					InventoryItem inventoryItem = (InventoryItem)player.getInventory().getItems().get(int3);
					if (inventoryItem instanceof AlarmClock && inventoryItem.getID() == long1) {
						if (boolean1) {
							((AlarmClock)inventoryItem).stopRinging();
						} else {
							((AlarmClock)inventoryItem).setAlarmSet(byte1 == 1);
							((AlarmClock)inventoryItem).setHour(int1);
							((AlarmClock)inventoryItem).setMinute(int2);
						}

						break;
					}
				}
			}
		} else if (short1 == AlarmClock.PacketWorld) {
			int int4 = byteBuffer.getInt();
			int int5 = byteBuffer.getInt();
			int int6 = byteBuffer.getInt();
			long long2 = byteBuffer.getLong();
			boolean boolean2 = byteBuffer.get() == 1;
			int int7 = boolean2 ? 0 : byteBuffer.getInt();
			int int8 = boolean2 ? 0 : byteBuffer.getInt();
			byte byte2 = boolean2 ? 0 : byteBuffer.get();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int4, int5, int6);
			if (square != null) {
				for (int int9 = 0; int9 < square.getWorldObjects().size(); ++int9) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int9);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof AlarmClock && worldInventoryObject.getItem().id == long2) {
						AlarmClock alarmClock = (AlarmClock)worldInventoryObject.getItem();
						if (boolean2) {
							alarmClock.stopRinging();
						} else {
							alarmClock.setAlarmSet(byte2 == 1);
							alarmClock.setHour(int7);
							alarmClock.setMinute(int8);
						}

						break;
					}
				}
			}
		}
	}

	public void AddItemToMap(ByteBuffer byteBuffer) {
		if (IsoWorld.instance.CurrentCell != null) {
			IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
			object.loadFromRemoteBuffer(byteBuffer);
			if (object.square != null) {
				if (object instanceof IsoLightSwitch) {
					((IsoLightSwitch)object).addLightSourceFromSprite();
				}

				object.addToWorld();
				IsoWorld.instance.CurrentCell.checkHaveRoof(object.square.getX(), object.square.getY());
				if (!(object instanceof IsoWorldInventoryObject)) {
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						LosUtil.cachecleared[int1] = true;
					}

					IsoGridSquare.setRecalcLightTime(-1);
					GameTime.instance.lightSourceUpdate = 100.0F;
					MapCollisionData.instance.squareChanged(object.square);
					PolygonalMap2.instance.squareChanged(object.square);
					if (object == object.square.getPlayerBuiltFloor()) {
						IsoGridOcclusionData.SquareChanged();
					}
				}

				if (object instanceof IsoWorldInventoryObject || object.getContainer() != null) {
					LuaEventManager.triggerEvent("OnContainerUpdate", object);
				}
			}
		}
	}

	public void playerTimeout(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		if (player != null) {
			DebugLog.log("Received timeout for player " + player.username + " id " + player.OnlineID);
			if (player.getVehicle() != null) {
				int int1 = player.getVehicle().getSeat(player);
				if (int1 != -1) {
					player.getVehicle().clearPassenger(int1);
				}
			}

			player.removeFromWorld();
			player.removeFromSquare();
			IDToPlayerMap.remove(player.OnlineID);
			this.idMapDirty = true;
			LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
		}
	}

	public void disconnect() {
		this.bConnected = false;
		if (IsoPlayer.instance != null) {
			IsoPlayer.instance.OnlineID = -1;
		}
	}

	public void addIncoming(short short1, ByteBuffer byteBuffer) {
		if (connection != null) {
			if (short1 == 18) {
				WorldStreamer.instance.receiveChunkPart(byteBuffer);
			} else if (short1 == 36) {
				WorldStreamer.instance.receiveNotRequired(byteBuffer);
			} else {
				ZomboidNetData zomboidNetData = null;
				if (byteBuffer.remaining() > 2048) {
					zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
				} else {
					zomboidNetData = ZomboidNetDataPool.instance.get();
				}

				zomboidNetData.read(short1, byteBuffer, connection);
				zomboidNetData.time = System.currentTimeMillis();
				synchronized (MainLoopNetData) {
					MainLoopNetData.add(zomboidNetData);
				}
			}
		}
	}

	public void doDisconnect(String string) {
		if (this.bConnected && connection != null && connection.connected) {
			connection.forceDisconnect();
			this.bConnected = false;
			connection = null;
			bClient = false;
		}
	}

	public void removeZombieFromCache(IsoZombie zombie) {
		if (IDToZombieMap.containsKey(zombie.OnlineID)) {
			IDToZombieMap.remove(zombie.OnlineID);
		}
	}

	private void receiveEquip(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != IsoPlayer.getInstance()) {
			InventoryItem inventoryItem = null;
			if (byte2 == 1) {
				inventoryItem = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
				byte byte3 = byteBuffer.get();
				try {
					inventoryItem.load(byteBuffer, 143, false);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}

			if (player != null) {
				if (byte1 == 0) {
					player.setPrimaryHandItem(inventoryItem);
				} else {
					player.setSecondaryHandItem(inventoryItem);
				}
			}
		}
	}

	public void equip(IsoPlayer player, int int1, InventoryItem inventoryItem) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)25, byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putByte((byte)int1);
		if (inventoryItem == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				inventoryItem.save(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		connection.endPacketImmediate();
	}

	public void sendChat(String string, byte byte1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)185, byteBufferWriter);
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putUTF(string);
		connection.endPacketImmediate();
	}

	public void sendWorldMessage(String string) {
		ChatManager.getInstance().showInfoMessage(string);
	}

	private void convertGameSaveWorldDirectory(String string, String string2) {
		File file = new File(string);
		if (file.isDirectory()) {
			File file2 = new File(string2);
			boolean boolean1 = file.renameTo(file2);
			if (boolean1) {
				DebugLog.log("CONVERT: The GameSaveWorld directory was renamed from " + string + " to " + string2);
			} else {
				DebugLog.log("ERROR: The GameSaveWorld directory cannot rename from " + string + " to " + string2);
			}
		}
	}

	public void doConnect(String string, String string2, String string3, String string4, String string5, String string6) {
		username = string.trim();
		password = string2.trim();
		ip = string3.trim();
		localIP = string4.trim();
		port = Integer.parseInt(string5.trim());
		serverPassword = string6.trim();
		instance.init();
		Core.GameSaveWorld = ip + "_" + port + "_" + ServerWorldDatabase.encrypt(string);
		this.convertGameSaveWorldDirectory(GameWindow.getGameModeCacheDir() + File.separator + ip + "_" + port + "_" + string, GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld);
		if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
			Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
		}
	}

	public void doConnectCoop(String string) {
		username = SteamFriends.GetPersonaName();
		password = "";
		ip = string;
		localIP = "";
		port = 0;
		serverPassword = "";
		this.init();
		if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
			Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
		}
	}

	public void scoreboardUpdate() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)50, byteBufferWriter);
		connection.endPacketImmediate();
	}

	public void sendWorldSound(IsoObject object, int int1, int int2, int int3, int int4, int int5, boolean boolean1, float float1, float float2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)54, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.putInt(int4);
		byteBufferWriter.putInt(int5);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putByte((byte)(object != null && object instanceof IsoZombie ? 1 : 0));
		connection.endPacketImmediate();
	}

	private void receiveSound(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		DebugLog.log(DebugType.Sound, "sound: received " + string + " at " + int1 + "," + int2 + "," + int3);
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3 + 0.5F);
		if (!boolean1) {
			baseSoundEmitter.playSoundImpl(string, (IsoObject)null);
		} else {
			baseSoundEmitter.playSoundLoopedImpl(string);
		}
	}

	private void receiveZombieDescriptors(ByteBuffer byteBuffer) throws IOException {
		short short1 = byteBuffer.getShort();
		if (short1 >= 1 && short1 <= 10000) {
			DebugLog.log(DebugType.NetworkFileDebug, "received " + short1 + " zombie descriptors");
			SharedDescriptors.Descriptor[] descriptorArray = new SharedDescriptors.Descriptor[short1];
			for (short short2 = 0; short2 < short1; ++short2) {
				descriptorArray[short2] = new SharedDescriptors.Descriptor();
				descriptorArray[short2].desc = new SurvivorDesc();
				descriptorArray[short2].desc.loadCompact(byteBuffer);
				descriptorArray[short2].palette = byteBuffer.get();
			}

			SharedDescriptors.setSharedDescriptors(descriptorArray);
		} else {
			throw new IOException("receiveZombieDescriptors: count=" + short1);
		}
	}

	private void receivePlayerZombieDescriptors(ByteBuffer byteBuffer) throws IOException {
		short short1 = byteBuffer.getShort();
		DebugLog.log(DebugType.NetworkFileDebug, "received " + short1 + " player-zombie descriptors");
		for (short short2 = 0; short2 < short1; ++short2) {
			SurvivorDesc survivorDesc = new SurvivorDesc();
			short short3 = byteBuffer.getShort();
			survivorDesc.loadCompact(byteBuffer);
			byte byte1 = byteBuffer.get();
			survivorDesc.setID(short3);
			SharedDescriptors.registerPlayerZombieDescriptor(survivorDesc, byte1);
		}
	}

	private void receiveAmbient(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		DebugLog.log(DebugType.Sound, "ambient: received " + string + " at " + int1 + "," + int2 + " radius=" + int3);
		AmbientStreamManager.instance.addAmbient(string, int1, int2, int3, float1);
	}

	public void sendClientCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)57, byteBufferWriter);
		byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						DebugLog.log("ERROR: sendClientCommand: can\'t save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
					}
				}

				TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		connection.endPacketImmediate();
	}

	public void sendClientCommandV(IsoPlayer player, String string, String string2, Object[] objectArray) {
		if (objectArray.length == 0) {
			this.sendClientCommand(player, string, string2, (KahluaTable)null);
		} else if (objectArray.length % 2 != 0) {
			DebugLog.log("ERROR: sendClientCommand called with wrong number of arguments (" + string + " " + string2 + ")");
		} else {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			for (int int1 = 0; int1 < objectArray.length; int1 += 2) {
				Object object = objectArray[int1 + 1];
				if (object instanceof Float) {
					kahluaTable.rawset(objectArray[int1], ((Float)object).doubleValue());
				} else if (object instanceof Integer) {
					kahluaTable.rawset(objectArray[int1], ((Integer)object).doubleValue());
				} else if (object instanceof Short) {
					kahluaTable.rawset(objectArray[int1], ((Short)object).doubleValue());
				} else {
					kahluaTable.rawset(objectArray[int1], object);
				}
			}

			this.sendClientCommand(player, string, string2, kahluaTable);
		}
	}

	public void sendClothing(IsoPlayer player, int int1, InventoryItem inventoryItem) {
		if (player != null && player.OnlineID != -1) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)56, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putByte((byte)int1);
			if (inventoryItem == null) {
				byteBufferWriter.putByte((byte)0);
			} else {
				byteBufferWriter.putByte((byte)1);
				try {
					inventoryItem.save(byteBufferWriter.bb, false);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}

			connection.endPacketImmediate();
		}
	}

	private void receiveClothing(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null && player != IsoPlayer.getInstance()) {
			InventoryItem inventoryItem = null;
			if (byte2 == 1) {
				String string = GameWindow.ReadString(byteBuffer);
				byte byte3 = byteBuffer.get();
				inventoryItem = InventoryItemFactory.CreateItem(string);
				if (Core.bDebug) {
					DebugLog.log(DebugType.General, "player " + player.username + " wearing " + string + " location " + byte1);
				}

				if (inventoryItem == null) {
					return;
				}

				try {
					inventoryItem.load(byteBuffer, 143, false);
				} catch (IOException ioException) {
					ioException.printStackTrace();
					return;
				}
			} else if (Core.bDebug) {
				DebugLog.log(DebugType.General, "player " + player.username + " removing location " + byte1);
			}

			if (byte1 == Item.ClothingBodyLocation.Bottoms.ordinal()) {
				player.setClothingItem_Legs(inventoryItem);
			}

			if (byte1 == Item.ClothingBodyLocation.Shoes.ordinal()) {
				player.setClothingItem_Feet(inventoryItem);
			}

			if (byte1 == Item.ClothingBodyLocation.Top.ordinal()) {
				player.setClothingItem_Torso(inventoryItem);
			}

			player.initSpritePartsEmpty();
		}
	}

	private void receiveBloodSplatter(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		float float5 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		byte byte1 = byteBuffer.get();
		IsoCell cell = IsoWorld.instance.CurrentCell;
		IsoGridSquare square = cell.getGridSquare((double)float1, (double)float2, (double)float3);
		if (square == null) {
			this.delayPacket((int)float1, (int)float2, (int)float3);
		} else {
			int int1;
			if (boolean2 && SandboxOptions.instance.BloodLevel.getValue() > 1) {
				for (int1 = -1; int1 <= 1; ++int1) {
					for (int int2 = -1; int2 <= 1; ++int2) {
						if (int1 != 0 || int2 != 0) {
							new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, (float)int1 * Rand.Next(0.25F, 0.5F), (float)int2 * Rand.Next(0.25F, 0.5F));
						}
					}
				}

				new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, cell, float1, float2, float3, float4 * 0.8F, float5 * 0.8F);
			} else {
				if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
					for (int1 = 0; int1 < byte1; ++int1) {
						square.splatBlood(3, 0.3F);
					}

					square.getChunk().addBloodSplat(float1, float2, (float)((int)float3), Rand.Next(20));
					new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 1.5F, float5 * 1.5F);
				}

				byte byte2 = 3;
				byte byte3 = 0;
				byte byte4 = 1;
				switch (SandboxOptions.instance.BloodLevel.getValue()) {
				case 1: 
					byte4 = 0;
					break;
				
				case 2: 
					byte4 = 1;
					byte2 = 5;
					byte3 = 2;
				
				case 3: 
				
				default: 
					break;
				
				case 4: 
					byte4 = 3;
					byte2 = 2;
					break;
				
				case 5: 
					byte4 = 10;
					byte2 = 0;
				
				}

				for (int int3 = 0; int3 < byte4; ++int3) {
					if (Rand.Next(boolean1 ? 8 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 1.5F, float5 * 1.5F);
					}

					if (Rand.Next(boolean1 ? 8 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 1.5F, float5 * 1.5F);
					}

					if (Rand.Next(boolean1 ? 8 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 1.8F, float5 * 1.8F);
					}

					if (Rand.Next(boolean1 ? 8 : byte2) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 1.9F, float5 * 1.9F);
					}

					if (Rand.Next(boolean1 ? 4 : byte3) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 3.5F, float5 * 3.5F);
					}

					if (Rand.Next(boolean1 ? 4 : byte3) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 3.8F, float5 * 3.8F);
					}

					if (Rand.Next(boolean1 ? 4 : byte3) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 3.9F, float5 * 3.9F);
					}

					if (Rand.Next(boolean1 ? 4 : byte3) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 1.5F, float5 * 1.5F);
					}

					if (Rand.Next(boolean1 ? 4 : byte3) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 3.8F, float5 * 3.8F);
					}

					if (Rand.Next(boolean1 ? 4 : byte3) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, cell, float1, float2, float3, float4 * 3.9F, float5 * 3.9F);
					}

					if (Rand.Next(boolean1 ? 9 : 6) == 0) {
						new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, cell, float1, float2, float3, float4 * 0.8F, float5 * 0.8F);
					}
				}
			}
		}
	}

	private void receiveZombieSound(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		IsoZombie.ZombieSound zombieSound = IsoZombie.ZombieSound.fromIndex(byte1);
		DebugLog.log(DebugType.Sound, "sound: received " + byte1 + " for zombie " + short1);
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short1);
		if (zombie != null && zombie.getCurrentSquare() != null) {
			float float1 = (float)zombieSound.radius();
			String string;
			switch (zombieSound) {
			case Burned: 
				string = zombie.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
				zombie.getEmitter().playVocals(string);
				break;
			
			case DeadCloseKilled: 
				zombie.getEmitter().playSoundImpl("HeadStab", (IsoObject)null);
				string = zombie.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
				zombie.getEmitter().playVocals(string);
				zombie.getEmitter().tick();
				break;
			
			case DeadNotCloseKilled: 
				zombie.getEmitter().playSoundImpl("HeadSmash", (IsoObject)null);
				string = zombie.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
				zombie.getEmitter().playVocals(string);
				zombie.getEmitter().tick();
				break;
			
			case Hurt: 
				zombie.getEmitter().playVocals(zombie.getHurtSound());
				break;
			
			case Idle: 
				string = zombie.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
				zombie.getEmitter().playVocals(string);
				break;
			
			case Lunge: 
				string = zombie.isFemale() ? "FemaleZombieAttack" : "MaleZombieAttack";
				zombie.getEmitter().playVocals(string);
				break;
			
			default: 
				DebugLog.log("unhandled zombie sound " + zombieSound);
			
			}
		}
	}

	private static void receiveSlowFactor(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		IsoPlayer player = IsoPlayer.players[byte1];
		if (player != null && !player.isDead()) {
			player.setSlowTimer(float1);
			player.setSlowFactor(float2);
			DebugLog.log(DebugType.Combat, "slowTimer=" + float1 + " slowFactor=" + float2);
		}
	}

	public void sendCustomColor(IsoObject object) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)121, byteBufferWriter);
		byteBufferWriter.putInt(object.getSquare().getX());
		byteBufferWriter.putInt(object.getSquare().getY());
		byteBufferWriter.putInt(object.getSquare().getZ());
		byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
		byteBufferWriter.putFloat(object.getCustomColor().r);
		byteBufferWriter.putFloat(object.getCustomColor().g);
		byteBufferWriter.putFloat(object.getCustomColor().b);
		byteBufferWriter.putFloat(object.getCustomColor().a);
		connection.endPacketImmediate();
	}

	public void sendBandage(int int1, int int2, boolean boolean1, float float1, boolean boolean2, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)42, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putBoolean(boolean1);
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putBoolean(boolean2);
		GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
		connection.endPacketImmediate();
	}

	public void sendStitch(int int1, int int2, boolean boolean1, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)98, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putBoolean(boolean1);
		byteBufferWriter.putFloat(float1);
		connection.endPacketImmediate();
	}

	public void sendWoundInfection(int int1, int int2, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)97, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putBoolean(boolean1);
		connection.endPacketImmediate();
	}

	public void sendDisinfect(int int1, int int2, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)99, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putFloat(float1);
		connection.endPacketImmediate();
	}

	public void sendSplint(int int1, int int2, boolean boolean1, float float1, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)102, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putBoolean(boolean1);
		if (boolean1) {
			if (string == null) {
				string = "";
			}

			byteBufferWriter.putUTF(string);
			byteBufferWriter.putFloat(float1);
		}

		connection.endPacketImmediate();
	}

	public void sendAdditionalPain(int int1, int int2, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)100, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putFloat(float1);
		connection.endPacketImmediate();
	}

	public void sendRemoveGlass(int int1, int int2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)101, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		connection.endPacketImmediate();
	}

	public void sendRemoveBullet(int int1, int int2, int int3) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)103, byteBufferWriter);
		byteBufferWriter.putShort((short)((byte)int1));
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		connection.endPacketImmediate();
	}

	public void sendCleanBurn(int int1, int int2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)104, byteBufferWriter);
		byteBufferWriter.putShort((short)((byte)int1));
		byteBufferWriter.putInt(int2);
		connection.endPacketImmediate();
	}

	public void eatFood(IsoPlayer player, Food food, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)43, byteBufferWriter);
		try {
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putFloat(float1);
			food.save(byteBufferWriter.bb, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		connection.endPacketImmediate();
	}

	public void drink(IsoPlayer player, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)45, byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putFloat(float1);
		connection.endPacketImmediate();
	}

	public void sendDeath(IsoPlayer player) {
		if (player != null && player.OnlineID != -1) {
			player.setTransactionID(0);
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)33, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.bb.putFloat(player.getX());
			byteBufferWriter.bb.putFloat(player.getY());
			byteBufferWriter.bb.putFloat(player.getZ());
			byteBufferWriter.putBoolean(player.getBodyDamage().isInfected());
			byteBufferWriter.putFloat(player.getBodyDamage().getInfectionLevel());
			connection.endPacketImmediate();
		}
	}

	public void sendOnBeaten(IsoPlayer player, float float1, float float2, float float3) {
		if (player != null && player.OnlineID != -1) {
			player.setTransactionID(0);
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)165, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.OnlineID);
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putFloat(float3);
			connection.endPacketImmediate();
		}
	}

	public void addToItemSendBuffer(IsoObject object, ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (object instanceof IsoWorldInventoryObject) {
			InventoryItem inventoryItem2 = ((IsoWorldInventoryObject)object).getItem();
			if (inventoryItem == null || inventoryItem2 == null || !(inventoryItem2 instanceof InventoryContainer) || itemContainer != ((InventoryContainer)inventoryItem2).getInventory()) {
				DebugLog.log("ERROR: addToItemSendBuffer parent=" + object + " item=" + inventoryItem);
				if (Core.bDebug) {
					throw new IllegalStateException();
				} else {
					return;
				}
			}
		} else if (object instanceof BaseVehicle) {
			if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != object) {
				DebugLog.log("ERROR: addToItemSendBuffer parent=" + object + " item=" + inventoryItem);
				if (Core.bDebug) {
					throw new IllegalStateException();
				}

				return;
			}
		} else if (object == null || inventoryItem == null || object.getContainerIndex(itemContainer) == -1) {
			DebugLog.log("ERROR: addToItemSendBuffer parent=" + object + " item=" + inventoryItem);
			if (Core.bDebug) {
				throw new IllegalStateException();
			}

			return;
		}

		ArrayList arrayList;
		if (this.itemsToSendRemove.containsKey(itemContainer)) {
			arrayList = (ArrayList)this.itemsToSendRemove.get(itemContainer);
			if (arrayList.remove(inventoryItem)) {
				if (arrayList.isEmpty()) {
					this.itemsToSendRemove.remove(itemContainer);
				}

				return;
			}
		}

		if (this.itemsToSend.containsKey(itemContainer)) {
			((ArrayList)this.itemsToSend.get(itemContainer)).add(inventoryItem);
		} else {
			arrayList = new ArrayList();
			this.itemsToSend.put(itemContainer, arrayList);
			arrayList.add(inventoryItem);
		}
	}

	public void addToItemRemoveSendBuffer(IsoObject object, ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (object instanceof IsoWorldInventoryObject) {
			InventoryItem inventoryItem2 = ((IsoWorldInventoryObject)object).getItem();
			if (inventoryItem == null || inventoryItem2 == null || !(inventoryItem2 instanceof InventoryContainer) || itemContainer != ((InventoryContainer)inventoryItem2).getInventory()) {
				DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + object + " item=" + inventoryItem);
				if (Core.bDebug) {
					throw new IllegalStateException();
				} else {
					return;
				}
			}
		} else if (object instanceof BaseVehicle) {
			if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != object) {
				DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + object + " item=" + inventoryItem);
				if (Core.bDebug) {
					throw new IllegalStateException();
				}

				return;
			}
		} else if (object instanceof IsoDeadBody) {
			if (inventoryItem == null || itemContainer != object.getContainer()) {
				DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + object + " item=" + inventoryItem);
				if (Core.bDebug) {
					throw new IllegalStateException();
				}

				return;
			}
		} else if (object == null || inventoryItem == null || object.getContainerIndex(itemContainer) == -1) {
			DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + object + " item=" + inventoryItem);
			if (Core.bDebug) {
				throw new IllegalStateException();
			}

			return;
		}

		if (!SystemDisabler.doWorldSyncEnable) {
			ArrayList arrayList;
			if (this.itemsToSend.containsKey(itemContainer)) {
				arrayList = (ArrayList)this.itemsToSend.get(itemContainer);
				if (arrayList.remove(inventoryItem)) {
					if (arrayList.isEmpty()) {
						this.itemsToSend.remove(itemContainer);
					}

					return;
				}
			}

			if (this.itemsToSendRemove.containsKey(itemContainer)) {
				((ArrayList)this.itemsToSendRemove.get(itemContainer)).add(inventoryItem);
			} else {
				arrayList = new ArrayList();
				arrayList.add(inventoryItem);
				this.itemsToSendRemove.put(itemContainer, arrayList);
			}
		} else {
			Object object2 = itemContainer.getParent();
			if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
				object2 = itemContainer.getContainingItem().getWorldItem();
			}

			GameClient gameClient = instance;
			UdpConnection udpConnection = connection;
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)22, byteBufferWriter);
			if (object2 instanceof IsoDeadBody) {
				byteBufferWriter.putShort((short)0);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putByte((byte)((IsoObject)object2).getStaticMovingObjectIndex());
				byteBufferWriter.putInt(1);
				byteBufferWriter.putLong(inventoryItem.id);
			} else if (object2 instanceof IsoWorldInventoryObject) {
				byteBufferWriter.putShort((short)1);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putLong(((IsoWorldInventoryObject)object2).getItem().id);
				byteBufferWriter.putInt(1);
				byteBufferWriter.putLong(inventoryItem.id);
			} else if (object2 instanceof BaseVehicle) {
				byteBufferWriter.putShort((short)3);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putShort(((BaseVehicle)object2).VehicleID);
				byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
				byteBufferWriter.putInt(1);
				byteBufferWriter.putLong(inventoryItem.id);
			} else {
				byteBufferWriter.putShort((short)2);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putByte((byte)((IsoObject)object2).square.getObjects().indexOf(object2));
				byteBufferWriter.putByte((byte)((IsoObject)object2).getContainerIndex(itemContainer));
				byteBufferWriter.putInt(1);
				byteBufferWriter.putLong(inventoryItem.id);
			}

			udpConnection.endPacketUnordered();
		}
	}

	public void sendAddedRemovedItems(boolean boolean1) {
		boolean boolean2 = boolean1 || this.itemSendFrequency.Check();
		Iterator iterator;
		Entry entry;
		ItemContainer itemContainer;
		ArrayList arrayList;
		Object object;
		ByteBufferWriter byteBufferWriter;
		if (!SystemDisabler.doWorldSyncEnable && !this.itemsToSendRemove.isEmpty() && boolean2) {
			iterator = this.itemsToSendRemove.entrySet().iterator();
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				itemContainer = (ItemContainer)entry.getKey();
				arrayList = (ArrayList)entry.getValue();
				object = itemContainer.getParent();
				if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
					object = itemContainer.getContainingItem().getWorldItem();
				}

				byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)22, byteBufferWriter);
				if (object instanceof IsoDeadBody) {
					byteBufferWriter.putShort((short)0);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
				} else if (object instanceof IsoWorldInventoryObject) {
					byteBufferWriter.putShort((short)1);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putLong(((IsoWorldInventoryObject)object).getItem().id);
				} else if (object instanceof BaseVehicle) {
					byteBufferWriter.putShort((short)3);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
					byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
				} else {
					byteBufferWriter.putShort((short)2);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putByte((byte)((IsoObject)object).square.getObjects().indexOf(object));
					byteBufferWriter.putByte((byte)((IsoObject)object).getContainerIndex(itemContainer));
				}

				byteBufferWriter.putInt(arrayList.size());
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					InventoryItem inventoryItem = (InventoryItem)arrayList.get(int1);
					byteBufferWriter.putLong(inventoryItem.id);
				}

				if (boolean1) {
					connection.endPacket();
				} else {
					connection.endPacketUnordered();
				}
			}

			this.itemsToSendRemove.clear();
		}

		if (!this.itemsToSend.isEmpty() && boolean2) {
			iterator = this.itemsToSend.entrySet().iterator();
			while (iterator.hasNext()) {
				entry = (Entry)iterator.next();
				itemContainer = (ItemContainer)entry.getKey();
				arrayList = (ArrayList)entry.getValue();
				object = itemContainer.getParent();
				if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
					object = itemContainer.getContainingItem().getWorldItem();
				}

				byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)20, byteBufferWriter);
				if (object instanceof IsoDeadBody) {
					byteBufferWriter.putShort((short)0);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
					try {
						CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				} else if (object instanceof IsoWorldInventoryObject) {
					byteBufferWriter.putShort((short)1);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putLong(((IsoWorldInventoryObject)object).getItem().id);
					try {
						CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
					} catch (Exception exception2) {
						exception2.printStackTrace();
					}
				} else if (object instanceof BaseVehicle) {
					byteBufferWriter.putShort((short)3);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
					byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
					try {
						CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
					} catch (Exception exception3) {
						exception3.printStackTrace();
					}
				} else {
					byteBufferWriter.putShort((short)2);
					byteBufferWriter.putInt(((IsoObject)object).square.getX());
					byteBufferWriter.putInt(((IsoObject)object).square.getY());
					byteBufferWriter.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter.putByte((byte)((IsoObject)object).square.getObjects().indexOf(object));
					byteBufferWriter.putByte((byte)((IsoObject)object).getContainerIndex(itemContainer));
					try {
						CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
					} catch (Exception exception4) {
						exception4.printStackTrace();
					}
				}

				if (boolean1) {
					connection.endPacket();
				} else {
					connection.endPacketUnordered();
				}
			}

			this.itemsToSend.clear();
		}
	}

	public void checkAddedRemovedItems(IsoObject object) {
		if (object != null) {
			if (!this.itemsToSend.isEmpty() || !this.itemsToSendRemove.isEmpty()) {
				if (object instanceof IsoDeadBody) {
					if (this.itemsToSend.containsKey(object.getContainer()) || this.itemsToSendRemove.containsKey(object.getContainer())) {
						this.sendAddedRemovedItems(true);
					}
				} else {
					ItemContainer itemContainer;
					if (object instanceof IsoWorldInventoryObject) {
						InventoryItem inventoryItem = ((IsoWorldInventoryObject)object).getItem();
						if (inventoryItem instanceof InventoryContainer) {
							itemContainer = ((InventoryContainer)inventoryItem).getInventory();
							if (this.itemsToSend.containsKey(itemContainer) || this.itemsToSendRemove.containsKey(itemContainer)) {
								this.sendAddedRemovedItems(true);
							}
						}
					} else if (!(object instanceof BaseVehicle)) {
						for (int int1 = 0; int1 < object.getContainerCount(); ++int1) {
							itemContainer = object.getContainerByIndex(int1);
							if (this.itemsToSend.containsKey(itemContainer) || this.itemsToSendRemove.containsKey(itemContainer)) {
								this.sendAddedRemovedItems(true);
								return;
							}
						}
					}
				}
			}
		}
	}

	private void writeItemStats(ByteBufferWriter byteBufferWriter, InventoryItem inventoryItem) {
		byteBufferWriter.putLong(inventoryItem.id);
		byteBufferWriter.putInt(inventoryItem.getUses());
		byteBufferWriter.putFloat(inventoryItem instanceof DrainableComboItem ? ((DrainableComboItem)inventoryItem).getUsedDelta() : 0.0F);
		if (inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			byteBufferWriter.putBoolean(true);
			byteBufferWriter.putFloat(food.getHungChange());
			byteBufferWriter.putFloat(food.getCalories());
			byteBufferWriter.putFloat(food.getCarbohydrates());
			byteBufferWriter.putFloat(food.getLipids());
			byteBufferWriter.putFloat(food.getProteins());
			byteBufferWriter.putFloat(food.getThirstChange());
			byteBufferWriter.putInt(food.getFluReduction());
			byteBufferWriter.putFloat(food.getPainReduction());
			byteBufferWriter.putFloat(food.getEndChange());
			byteBufferWriter.putInt(food.getReduceFoodSickness());
			byteBufferWriter.putFloat(food.getStressChange());
			byteBufferWriter.putFloat(food.getFatigueChange());
		} else {
			byteBufferWriter.putBoolean(false);
		}
	}

	public void sendItemStats(InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			if (inventoryItem.getWorldItem() != null && inventoryItem.getWorldItem().getWorldObjectIndex() != -1) {
				IsoWorldInventoryObject worldInventoryObject = inventoryItem.getWorldItem();
				ByteBufferWriter byteBufferWriter = connection.startPacket();
				PacketTypes.doPacket((short)35, byteBufferWriter);
				byteBufferWriter.putShort((short)1);
				byteBufferWriter.putInt(worldInventoryObject.square.getX());
				byteBufferWriter.putInt(worldInventoryObject.square.getY());
				byteBufferWriter.putInt(worldInventoryObject.square.getZ());
				this.writeItemStats(byteBufferWriter, inventoryItem);
				connection.endPacket();
			} else if (inventoryItem.getContainer() == null) {
				DebugLog.log("ERROR: sendItemStats(): item is neither in a container nor on the ground");
				if (Core.bDebug) {
					throw new IllegalStateException();
				}
			} else {
				ItemContainer itemContainer = inventoryItem.getContainer();
				Object object = itemContainer.getParent();
				if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
					object = itemContainer.getContainingItem().getWorldItem();
				}

				if (object instanceof IsoWorldInventoryObject) {
					InventoryItem inventoryItem2 = ((IsoWorldInventoryObject)object).getItem();
					if (!(inventoryItem2 instanceof InventoryContainer) || itemContainer != ((InventoryContainer)inventoryItem2).getInventory()) {
						DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + inventoryItem);
						if (Core.bDebug) {
							throw new IllegalStateException();
						} else {
							return;
						}
					}
				} else if (object instanceof BaseVehicle) {
					if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != object) {
						DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + inventoryItem);
						if (Core.bDebug) {
							throw new IllegalStateException();
						}

						return;
					}
				} else if (object instanceof IsoDeadBody) {
					if (itemContainer != ((IsoObject)object).getContainer()) {
						DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + inventoryItem);
						if (Core.bDebug) {
							throw new IllegalStateException();
						}

						return;
					}
				} else if (object == null || ((IsoObject)object).getContainerIndex(itemContainer) == -1) {
					DebugLog.log("ERROR: sendItemStats() parent=" + object + " item=" + inventoryItem);
					if (Core.bDebug) {
						throw new IllegalStateException();
					}

					return;
				}

				ByteBufferWriter byteBufferWriter2 = connection.startPacket();
				PacketTypes.doPacket((short)35, byteBufferWriter2);
				if (object instanceof IsoDeadBody) {
					byteBufferWriter2.putShort((short)0);
					byteBufferWriter2.putInt(((IsoObject)object).square.getX());
					byteBufferWriter2.putInt(((IsoObject)object).square.getY());
					byteBufferWriter2.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter2.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
				} else if (object instanceof IsoWorldInventoryObject) {
					byteBufferWriter2.putShort((short)1);
					byteBufferWriter2.putInt(((IsoObject)object).square.getX());
					byteBufferWriter2.putInt(((IsoObject)object).square.getY());
					byteBufferWriter2.putInt(((IsoObject)object).square.getZ());
				} else if (object instanceof BaseVehicle) {
					byteBufferWriter2.putShort((short)3);
					byteBufferWriter2.putInt(((IsoObject)object).square.getX());
					byteBufferWriter2.putInt(((IsoObject)object).square.getY());
					byteBufferWriter2.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter2.putShort(((BaseVehicle)object).VehicleID);
					byteBufferWriter2.putByte((byte)itemContainer.vehiclePart.getIndex());
				} else {
					byteBufferWriter2.putShort((short)2);
					byteBufferWriter2.putInt(((IsoObject)object).square.getX());
					byteBufferWriter2.putInt(((IsoObject)object).square.getY());
					byteBufferWriter2.putInt(((IsoObject)object).square.getZ());
					byteBufferWriter2.putByte((byte)((IsoObject)object).getObjectIndex());
					byteBufferWriter2.putByte((byte)((IsoObject)object).getContainerIndex(itemContainer));
				}

				this.writeItemStats(byteBufferWriter2, inventoryItem);
				connection.endPacket();
			}
		}
	}

	public void PlayWorldSound(String string, boolean boolean1, int int1, int int2, int int3) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)53, byteBufferWriter);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		connection.endPacketImmediate();
	}

	public void startLocalServer() throws Exception {
		bClient = true;
		ip = "127.0.0.1";
		(new Thread(){
			
			public void run() {
				String var1 = System.getProperty("file.separator");
				String var2 = System.getProperty("java.class.path");
				String var3 = System.getProperty("java.home") + var1 + "bin" + var1 + "java";
				ProcessBuilder var4 = new ProcessBuilder(new String[]{var3, "-Xms2048m", "-Xmx2048m", "-Djava.library.path=../natives/", "-cp", "lwjgl.jar;lwjgl_util.jar;sqlitejdbc-v056.jar;../bin/", "zombie.network.GameServer"});
				var4.redirectErrorStream(true);
				Process var5 = null;
				try {
					var5 = var4.start();
				} catch (IOException var11) {
					var11.printStackTrace();
				}

				InputStreamReader var6 = new InputStreamReader(var5.getInputStream());
				boolean var7 = false;
				try {
					while (!var6.ready()) {
						int var8;
						try {
							while ((var8 = var6.read()) != -1) {
								System.out.print((char)var8);
							}
						} catch (IOException var12) {
							var12.printStackTrace();
						}

						try {
							var6.close();
						} catch (IOException var10) {
							var10.printStackTrace();
						}
					}
				} catch (IOException var13) {
					var13.printStackTrace();
				}
			}
		}).start();
	}

	public static void sendPing() {
		if (bClient) {
			ByteBufferWriter byteBufferWriter = connection.startPingPacket();
			PacketTypes.doPingPacket(byteBufferWriter);
			byteBufferWriter.putLong(System.currentTimeMillis());
			byteBufferWriter.putLong(0L);
			connection.endPingPacket();
		}
	}

	public static void registerZone(IsoMetaGrid.Zone zone, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)94, byteBufferWriter);
		byteBufferWriter.putUTF(zone.name);
		byteBufferWriter.putUTF(zone.type);
		byteBufferWriter.putInt(zone.x);
		byteBufferWriter.putInt(zone.y);
		byteBufferWriter.putInt(zone.z);
		byteBufferWriter.putInt(zone.w);
		byteBufferWriter.putInt(zone.h);
		byteBufferWriter.putInt(zone.getLastActionTimestamp());
		byteBufferWriter.putBoolean(boolean1);
		connection.endPacketImmediate();
	}

	private void receiveHelicopter(ByteBuffer byteBuffer) {
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		if (IsoWorld.instance != null && IsoWorld.instance.helicopter != null) {
			IsoWorld.instance.helicopter.clientSync(float1, float2, boolean1);
		}
	}

	public static void removeSpecialObjectFromSquare(IsoObject object) {
		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		int int3 = object.getSquare().getZ();
		int int4 = object.getSquare().getSpecialObjects().indexOf(object);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			if (square.getCell().getProcessIsoObjects().contains(object)) {
				square.getCell().getProcessIsoObjectRemove().add(object);
			}

			object.removeFromWorld();
			object.removeFromSquare();
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)111, byteBufferWriter);
			byteBufferWriter.putInt(int1);
			byteBufferWriter.putInt(int2);
			byteBufferWriter.putInt(int3);
			byteBufferWriter.putInt(int4);
			connection.endPacketImmediate();
		}
	}

	public static void sendSafehouse(SafeHouse safeHouse, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)114, byteBufferWriter);
		byteBufferWriter.putInt(safeHouse.getX());
		byteBufferWriter.putInt(safeHouse.getY());
		byteBufferWriter.putInt(safeHouse.getW());
		byteBufferWriter.putInt(safeHouse.getH());
		byteBufferWriter.putUTF(safeHouse.getOwner());
		byteBufferWriter.putInt(safeHouse.getPlayers().size());
		Iterator iterator = safeHouse.getPlayers().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			byteBufferWriter.putUTF(string);
		}

		byteBufferWriter.putBoolean(boolean1);
		byteBufferWriter.putUTF(safeHouse.getTitle());
		connection.endPacketImmediate();
	}

	private void syncSafehouse(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		int int5 = byteBuffer.getInt();
		SafeHouse safeHouse = SafeHouse.getSafeHouse(int1, int2, int3, int4);
		if (safeHouse == null) {
			safeHouse = SafeHouse.addSafeHouse(int1, int2, int3, int4, string, true);
		}

		if (safeHouse != null) {
			safeHouse.getPlayers().clear();
			for (int int6 = 0; int6 < int5; ++int6) {
				safeHouse.getPlayers().add(GameWindow.ReadString(byteBuffer));
			}

			if (byteBuffer.get() == 1) {
				SafeHouse.getSafehouseList().remove(safeHouse);
			}

			safeHouse.setTitle(GameWindow.ReadString(byteBuffer));
			safeHouse.setOwner(string);
			LuaEventManager.triggerEvent("OnSafehousesChanged");
		}
	}

	public IsoPlayer getPlayerFromUsername(String string) {
		ArrayList arrayList = this.getPlayers();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoPlayer player = (IsoPlayer)arrayList.get(int1);
			if (player.getUsername().equals(string)) {
				return player;
			}
		}

		return null;
	}

	public static void destroy(IsoObject object) {
		if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)115, byteBufferWriter);
			IsoGridSquare square = object.getSquare();
			byteBufferWriter.putInt(square.getX());
			byteBufferWriter.putInt(square.getY());
			byteBufferWriter.putInt(square.getZ());
			byteBufferWriter.putInt(square.getObjects().indexOf(object));
			connection.endPacketImmediate();
			square.RemoveTileObject(object);
		}
	}

	public static void sendTeleport(IsoPlayer player, float float1, float float2, float float3) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)108, byteBufferWriter);
		GameWindow.WriteString(byteBufferWriter.bb, player.getUsername());
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putFloat(float3);
		connection.endPacketImmediate();
	}

	public static void sendStopFire(IsoGridSquare square) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)116, byteBufferWriter);
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putInt(square.getX());
		byteBufferWriter.putInt(square.getY());
		byteBufferWriter.putInt(square.getZ());
		connection.endPacketImmediate();
	}

	public static void sendStopFire(IsoGameCharacter gameCharacter) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)116, byteBufferWriter);
		if (gameCharacter instanceof IsoPlayer) {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putShort((short)((IsoPlayer)gameCharacter).getOnlineID());
		}

		if (gameCharacter instanceof IsoZombie) {
			byteBufferWriter.putByte((byte)2);
			byteBufferWriter.putShort(((IsoZombie)gameCharacter).OnlineID);
		}

		connection.endPacketImmediate();
	}

	public void sendCataplasm(int int1, int int2, float float1, float float2, float float3) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)117, byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putFloat(float3);
		connection.endPacketImmediate();
	}

	private void receiveBodyDamageUpdate(ByteBuffer byteBuffer) {
		BodyDamageSync.instance.clientPacket(byteBuffer);
	}

	private static void dealWithNetDataShort(ZomboidNetData zomboidNetData, ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		switch (short1) {
		case 1000: 
			receiveWaveSignal(byteBuffer);
			break;
		
		case 1002: 
			receiveRadioServerData(byteBuffer);
			break;
		
		case 1004: 
			receiveRadioDeviceDataState(byteBuffer);
			break;
		
		case 1200: 
			SyncCustomLightSwitchSettings(byteBuffer);
		
		}
	}

	public static void receiveRadioDeviceDataState(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		int int1;
		if (byte1 == 1) {
			int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (object instanceof IsoWaveSignal) {
					DeviceData deviceData = ((IsoWaveSignal)object).getDeviceData();
					if (deviceData != null) {
						try {
							deviceData.receiveDeviceDataStatePacket(byteBuffer, (UdpConnection)null);
						} catch (Exception exception) {
							System.out.print(exception.getMessage());
						}
					}
				}
			}
		} else if (byte1 == 0) {
			int1 = byteBuffer.getInt();
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
			byte byte2 = byteBuffer.get();
			if (player != null) {
				Radio radio = null;
				if (byte2 == 1 && player.getPrimaryHandItem() instanceof Radio) {
					radio = (Radio)player.getPrimaryHandItem();
				}

				if (byte2 == 2 && player.getSecondaryHandItem() instanceof Radio) {
					radio = (Radio)player.getSecondaryHandItem();
				}

				if (radio != null && radio.getDeviceData() != null) {
					try {
						radio.getDeviceData().receiveDeviceDataStatePacket(byteBuffer, connection);
					} catch (Exception exception2) {
						System.out.print(exception2.getMessage());
					}
				}
			}
		} else if (byte1 == 2) {
			short short1 = byteBuffer.getShort();
			short short2 = byteBuffer.getShort();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short1);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(short2);
				if (vehiclePart != null) {
					DeviceData deviceData2 = vehiclePart.getDeviceData();
					if (deviceData2 != null) {
						try {
							deviceData2.receiveDeviceDataStatePacket(byteBuffer, (UdpConnection)null);
						} catch (Exception exception3) {
							System.out.print(exception3.getMessage());
						}
					}
				}
			}
		}
	}

	public static void sendRadioServerDataRequest() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypesShort.doPacket((short)1002, byteBufferWriter);
		connection.endPacketImmediate();
	}

	private static void receiveRadioServerData(ByteBuffer byteBuffer) {
		ZomboidRadio zomboidRadio = ZomboidRadio.getInstance();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			String string = GameWindow.ReadString(byteBuffer);
			int int3 = byteBuffer.getInt();
			for (int int4 = 0; int4 < int3; ++int4) {
				int int5 = byteBuffer.getInt();
				String string2 = GameWindow.ReadString(byteBuffer);
				zomboidRadio.addChannelName(string2, int5, string);
			}
		}

		zomboidRadio.setHasRecievedServerData(true);
	}

	public static void sendIsoWaveSignal(int int1, int int2, int int3, ChatMessage chatMessage, String string, float float1, float float2, float float3, int int4, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypesShort.doPacket((short)1000, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.putBoolean(chatMessage != null);
		if (chatMessage != null) {
			chatMessage.pack(byteBufferWriter);
		}

		byteBufferWriter.putByte((byte)(string != null ? 1 : 0));
		if (string != null) {
			byteBufferWriter.putUTF(string);
		}

		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putFloat(float3);
		byteBufferWriter.putInt(int4);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		connection.endPacketImmediate();
	}

	private static void receiveWaveSignal(ByteBuffer byteBuffer) {
		if (ChatManager.getInstance().isWorking()) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			ChatMessage chatMessage = null;
			byte byte1 = byteBuffer.get();
			if (byte1 == 1) {
				chatMessage = ChatManager.getInstance().unpackMessage(byteBuffer);
			}

			String string = null;
			if (byteBuffer.get() == 1) {
				string = GameWindow.ReadString(byteBuffer);
			}

			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			int int4 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			ZomboidRadio.getInstance().ReceiveTransmission(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean1);
		}
	}

	public static void sendPlayerListensChannel(int int1, boolean boolean1, boolean boolean2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypesShort.doPacket((short)1001, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
		connection.endPacketImmediate();
	}

	private void receiveFurnaceChange(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			this.delayPacket(int1, int2, int3);
		} else {
			if (square != null) {
				BSFurnace bSFurnace = null;
				for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
					if (square.getObjects().get(int4) instanceof BSFurnace) {
						bSFurnace = (BSFurnace)square.getObjects().get(int4);
						break;
					}
				}

				if (bSFurnace == null) {
					DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + int1 + "," + int2 + "," + int3);
					return;
				}

				bSFurnace.fireStarted = byteBuffer.get() == 1;
				bSFurnace.fuelAmount = byteBuffer.getFloat();
				bSFurnace.fuelDecrease = byteBuffer.getFloat();
				bSFurnace.heat = byteBuffer.getFloat();
				bSFurnace.sSprite = GameWindow.ReadString(byteBuffer);
				bSFurnace.sLitSprite = GameWindow.ReadString(byteBuffer);
				bSFurnace.updateLight();
			}
		}
	}

	public static void sendFurnaceChange(BSFurnace bSFurnace) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)120, byteBufferWriter);
		byteBufferWriter.putInt(bSFurnace.getSquare().getX());
		byteBufferWriter.putInt(bSFurnace.getSquare().getY());
		byteBufferWriter.putInt(bSFurnace.getSquare().getZ());
		byteBufferWriter.putByte((byte)(bSFurnace.isFireStarted() ? 1 : 0));
		byteBufferWriter.putFloat(bSFurnace.getFuelAmount());
		byteBufferWriter.putFloat(bSFurnace.getFuelDecrease());
		byteBufferWriter.putFloat(bSFurnace.getHeat());
		GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sSprite);
		GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sLitSprite);
		connection.endPacketImmediate();
	}

	public static void sendCompost(IsoCompost compost) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)122, byteBufferWriter);
		byteBufferWriter.putInt(compost.getSquare().getX());
		byteBufferWriter.putInt(compost.getSquare().getY());
		byteBufferWriter.putInt(compost.getSquare().getZ());
		byteBufferWriter.putFloat(compost.getCompost());
		connection.endPacketImmediate();
	}

	private void syncCompost(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			IsoCompost compost = square.getCompost();
			if (compost == null) {
				compost = new IsoCompost(square.getCell(), square);
				square.AddSpecialObject(compost);
			}

			compost.setCompost(byteBuffer.getFloat());
			compost.updateSprite();
		}
	}

	public void requestUserlog(String string) {
		if (canSeePlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)128, byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			connection.endPacketImmediate();
		}
	}

	public void addUserlog(String string, String string2, String string3) {
		if (canSeePlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)129, byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			GameWindow.WriteString(byteBufferWriter.bb, string2);
			GameWindow.WriteString(byteBufferWriter.bb, string3);
			connection.endPacketImmediate();
		}
	}

	public void removeUserlog(String string, String string2, String string3) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)130, byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			GameWindow.WriteString(byteBufferWriter.bb, string2);
			GameWindow.WriteString(byteBufferWriter.bb, string3);
			connection.endPacketImmediate();
		}
	}

	public void addWarningPoint(String string, String string2, int int1) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)131, byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			GameWindow.WriteString(byteBufferWriter.bb, string2);
			byteBufferWriter.putInt(int1);
			connection.endPacketImmediate();
		}
	}

	private static void receiveAdminMessage(ByteBuffer byteBuffer) {
		if (canSeePlayerStats()) {
			String string = GameWindow.ReadString(byteBuffer);
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			LuaEventManager.triggerEvent("OnAdminMessage", string, int1, int2, int3);
		}
	}

	public void wakeUpPlayer(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)133, byteBufferWriter);
		byteBufferWriter.putInt(player.OnlineID);
		connection.endPacketImmediate();
	}

	private static void receiveWakeUpOrder(ByteBuffer byteBuffer) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		if (player != null) {
			SleepingEvent.instance.wakeUp(player, true);
		}
	}

	public void getDBSchema() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)135, byteBufferWriter);
		connection.endPacketImmediate();
	}

	private void receiveDBSchema(ByteBuffer byteBuffer) {
		if (!accessLevel.equals("Observer") && !accessLevel.equals("")) {
			this.dbSchema = LuaManager.platform.newTable();
			int int1 = byteBuffer.getInt();
			for (int int2 = 0; int2 < int1; ++int2) {
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				String string = GameWindow.ReadString(byteBuffer);
				int int3 = byteBuffer.getInt();
				for (int int4 = 0; int4 < int3; ++int4) {
					KahluaTable kahluaTable2 = LuaManager.platform.newTable();
					String string2 = GameWindow.ReadString(byteBuffer);
					String string3 = GameWindow.ReadString(byteBuffer);
					kahluaTable2.rawset("name", string2);
					kahluaTable2.rawset("type", string3);
					kahluaTable.rawset(int4, kahluaTable2);
				}

				this.dbSchema.rawset(string, kahluaTable);
			}

			LuaEventManager.triggerEvent("OnGetDBSchema", this.dbSchema);
		}
	}

	public void getTableResult(String string, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)136, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putUTF(string);
		connection.endPacketImmediate();
	}

	private void receiveTableResult(ByteBuffer byteBuffer) {
		ArrayList arrayList = new ArrayList();
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		int int2 = byteBuffer.getInt();
		ArrayList arrayList2 = new ArrayList();
		for (int int3 = 0; int3 < int2; ++int3) {
			DBResult dBResult = new DBResult();
			dBResult.setTableName(string);
			int int4 = byteBuffer.getInt();
			for (int int5 = 0; int5 < int4; ++int5) {
				String string2 = GameWindow.ReadString(byteBuffer);
				String string3 = GameWindow.ReadString(byteBuffer);
				dBResult.getValues().put(string2, string3);
				if (int3 == 0) {
					arrayList2.add(string2);
				}
			}

			dBResult.setColumns(arrayList2);
			arrayList.add(dBResult);
		}

		LuaEventManager.triggerEvent("OnGetTableResult", arrayList, int1, string);
	}

	public void executeQuery(String string, KahluaTable kahluaTable) {
		if (accessLevel.equals("admin")) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.doPacket((short)137, byteBufferWriter);
			try {
				byteBufferWriter.putUTF(string);
				kahluaTable.save(byteBufferWriter.bb);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			} finally {
				connection.endPacketImmediate();
			}
		}
	}

	public ArrayList getConnectedPlayers() {
		return this.connectedPlayers;
	}

	public static void sendNonPvpZone(NonPvpZone nonPvpZone, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)139, byteBufferWriter);
		byteBufferWriter.putInt(nonPvpZone.getX());
		byteBufferWriter.putInt(nonPvpZone.getY());
		byteBufferWriter.putInt(nonPvpZone.getX2());
		byteBufferWriter.putInt(nonPvpZone.getY2());
		byteBufferWriter.putUTF(nonPvpZone.getTitle());
		byteBufferWriter.putBoolean(boolean1);
		connection.endPacketImmediate();
	}

	public static void sendFaction(Faction faction, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)140, byteBufferWriter);
		faction.writeToBuffer(byteBufferWriter, boolean1);
		connection.endPacketImmediate();
	}

	public static void sendFactionInvite(Faction faction, IsoPlayer player, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)141, byteBufferWriter);
		byteBufferWriter.putUTF(faction.getName());
		byteBufferWriter.putUTF(player.getUsername());
		byteBufferWriter.putUTF(string);
		connection.endPacketImmediate();
	}

	private void receiveFactionInvite(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		LuaEventManager.triggerEvent("ReceiveFactionInvite", string, string2);
	}

	public static void acceptFactionInvite(Faction faction, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)142, byteBufferWriter);
		byteBufferWriter.putUTF(faction.getName());
		byteBufferWriter.putUTF(string);
		connection.endPacketImmediate();
	}

	private void AcceptedFactionInvite(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		Faction faction = Faction.getFaction(string);
		if (faction != null) {
			faction.addPlayer(string2);
		}

		LuaEventManager.triggerEvent("AcceptedFactionInvite", string, string2);
	}

	public static void addTicket(String string, String string2, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)143, byteBufferWriter);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		byteBufferWriter.putInt(int1);
		connection.endPacketImmediate();
	}

	public static void getTickets(String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)144, byteBufferWriter);
		byteBufferWriter.putUTF(string);
		connection.endPacketImmediate();
	}

	private static void gotTickets(ByteBuffer byteBuffer) {
		ArrayList arrayList = new ArrayList();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			DBTicket dBTicket = new DBTicket(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
			arrayList.add(dBTicket);
			if (byteBuffer.get() == 1) {
				DBTicket dBTicket2 = new DBTicket(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
				dBTicket2.setIsAnswer(true);
				dBTicket.setAnswer(dBTicket2);
			}
		}

		LuaEventManager.triggerEvent("ViewTickets", arrayList);
	}

	public static void removeTicket(int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)145, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		connection.endPacketImmediate();
	}

	public static boolean sendItemListNet(IsoPlayer player, ArrayList arrayList, IsoPlayer player2, String string, String string2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)150, byteBufferWriter);
		byteBufferWriter.putByte((byte)(player2 != null ? 1 : 0));
		if (player2 != null) {
			byteBufferWriter.putShort((short)player2.getOnlineID());
		}

		byteBufferWriter.putByte((byte)(player != null ? 1 : 0));
		if (player != null) {
			byteBufferWriter.putShort((short)player.getOnlineID());
		}

		GameWindow.WriteString(byteBufferWriter.bb, string);
		byteBufferWriter.putByte((byte)(string2 != null ? 1 : 0));
		if (string2 != null) {
			GameWindow.WriteString(byteBufferWriter.bb, string2);
		}

		try {
			CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
		} catch (Exception exception) {
			exception.printStackTrace();
			connection.cancelPacket();
			return false;
		}

		connection.endPacketImmediate();
		return true;
	}

	private static void receiveItemListNet(ByteBuffer byteBuffer) {
		IsoPlayer player = null;
		if (byteBuffer.get() != 1) {
			player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(byteBuffer.getShort()));
		}

		IsoPlayer player2 = null;
		if (byteBuffer.get() == 1) {
			player2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(byteBuffer.getShort()));
		}

		String string = GameWindow.ReadString(byteBuffer);
		String string2 = null;
		if (byteBuffer.get() == 1) {
			string2 = GameWindow.ReadString(byteBuffer);
		}

		short short1 = byteBuffer.getShort();
		ArrayList arrayList = new ArrayList(short1);
		try {
			for (int int1 = 0; int1 < short1; ++int1) {
				String string3 = GameWindow.ReadString(byteBuffer);
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string3);
				inventoryItem.load(byteBuffer, 143, true);
				arrayList.add(inventoryItem);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		LuaEventManager.triggerEvent("OnReceiveItemListNet", player2, arrayList, player, string, string2);
	}

	public void requestTrading(IsoPlayer player, IsoPlayer player2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)146, byteBufferWriter);
		byteBufferWriter.putInt(player.OnlineID);
		byteBufferWriter.putInt(player2.OnlineID);
		byteBufferWriter.putByte((byte)0);
		connection.endPacketImmediate();
	}

	public void acceptTrading(IsoPlayer player, IsoPlayer player2, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)146, byteBufferWriter);
		byteBufferWriter.putInt(player2.OnlineID);
		byteBufferWriter.putInt(player.OnlineID);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 2));
		connection.endPacketImmediate();
	}

	private static void isRequestedToTrade(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			if (byte1 == 0) {
				LuaEventManager.triggerEvent("RequestTrade", player);
			} else {
				LuaEventManager.triggerEvent("AcceptedTrade", byte1 == 1);
			}
		}
	}

	public void tradingUISendAddItem(IsoPlayer player, IsoPlayer player2, InventoryItem inventoryItem) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)147, byteBufferWriter);
		byteBufferWriter.putInt(player.OnlineID);
		byteBufferWriter.putInt(player2.OnlineID);
		try {
			inventoryItem.save(byteBufferWriter.bb, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		connection.endPacketImmediate();
	}

	private static void tradingUIAddItem(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		if (inventoryItem != null) {
			byte byte1 = byteBuffer.get();
			try {
				inventoryItem.load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			}

			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
			if (player != null) {
				LuaEventManager.triggerEvent("TradingUIAddItem", player, inventoryItem);
			}
		}
	}

	public void tradingUISendRemoveItem(IsoPlayer player, IsoPlayer player2, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)148, byteBufferWriter);
		byteBufferWriter.putInt(player.OnlineID);
		byteBufferWriter.putInt(player2.OnlineID);
		byteBufferWriter.putInt(int1);
		connection.endPacketImmediate();
	}

	private static void tradingUIRemoveItem(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			LuaEventManager.triggerEvent("TradingUIRemoveItem", player, int2);
		}
	}

	public void tradingUISendUpdateState(IsoPlayer player, IsoPlayer player2, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)149, byteBufferWriter);
		byteBufferWriter.putInt(player.OnlineID);
		byteBufferWriter.putInt(player2.OnlineID);
		byteBufferWriter.putInt(int1);
		connection.endPacketImmediate();
	}

	private static void tradingUIUpdateState(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			LuaEventManager.triggerEvent("TradingUIUpdateState", player, int2);
		}
	}

	public static void sendBuildingStashToDo(String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)152, byteBufferWriter);
		byteBufferWriter.putUTF(string);
		connection.endPacketImmediate();
	}

	public static void sendRequestInventory(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)153, byteBufferWriter);
		byteBufferWriter.putInt(IsoPlayer.instance.getOnlineID());
		byteBufferWriter.putInt(player.getOnlineID());
		connection.endPacketImmediate();
	}

	private int sendInventoryPutItems(ByteBufferWriter byteBufferWriter, LinkedHashMap linkedHashMap, long long1) {
		int int1 = linkedHashMap.size();
		Iterator iterator = linkedHashMap.keySet().iterator();
		while (iterator.hasNext()) {
			InventoryItem inventoryItem = (InventoryItem)linkedHashMap.get(iterator.next());
			byteBufferWriter.putUTF(inventoryItem.getModule());
			byteBufferWriter.putUTF(inventoryItem.getType());
			byteBufferWriter.putLong(inventoryItem.getID());
			byteBufferWriter.putLong(long1);
			byteBufferWriter.putBoolean(IsoPlayer.instance.isEquipped(inventoryItem));
			if (inventoryItem instanceof DrainableComboItem) {
				byteBufferWriter.putFloat(((DrainableComboItem)inventoryItem).getUsedDelta());
			} else {
				byteBufferWriter.putFloat((float)inventoryItem.getCondition());
			}

			byteBufferWriter.putInt(inventoryItem.getCount());
			if (inventoryItem instanceof DrainableComboItem) {
				byteBufferWriter.putUTF(Translator.getText("IGUI_ItemCat_Drainable"));
			} else {
				byteBufferWriter.putUTF(inventoryItem.getCategory());
			}

			byteBufferWriter.putUTF(inventoryItem.getContainer().getType());
			byteBufferWriter.putBoolean(inventoryItem.getWorker() != null && inventoryItem.getWorker().equals("inInv"));
			if (inventoryItem instanceof InventoryContainer && ((InventoryContainer)inventoryItem).getItemContainer() != null && !((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) {
				LinkedHashMap linkedHashMap2 = ((InventoryContainer)inventoryItem).getItemContainer().getItems4Admin();
				int1 += linkedHashMap2.size();
				this.sendInventoryPutItems(byteBufferWriter, linkedHashMap2, inventoryItem.getID());
			}
		}

		return int1;
	}

	private void sendInventory(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)154, byteBufferWriter);
		byteBufferWriter.putInt(int1);
		int int2 = byteBufferWriter.bb.position();
		byteBufferWriter.putInt(0);
		LinkedHashMap linkedHashMap = IsoPlayer.instance.getInventory().getItems4Admin();
		int int3 = this.sendInventoryPutItems(byteBufferWriter, linkedHashMap, -1L);
		int int4 = byteBufferWriter.bb.position();
		byteBufferWriter.bb.position(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.bb.position(int4);
		connection.endPacketImmediate();
	}

	private void receiveInventory(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		for (int int2 = 0; int2 < int1; ++int2) {
			KahluaTable kahluaTable2 = LuaManager.platform.newTable();
			String string = GameWindow.ReadStringUTF(byteBuffer) + "." + GameWindow.ReadStringUTF(byteBuffer);
			long long1 = byteBuffer.getLong();
			long long2 = byteBuffer.getLong();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			int int3 = byteBuffer.getInt();
			String string2 = GameWindow.ReadStringUTF(byteBuffer);
			String string3 = GameWindow.ReadStringUTF(byteBuffer);
			boolean boolean2 = byteBuffer.get() == 1;
			kahluaTable2.rawset("fullType", string);
			kahluaTable2.rawset("itemId", long1);
			kahluaTable2.rawset("isEquip", boolean1);
			kahluaTable2.rawset("var", (double)Math.round((double)float1 * 100.0) / 100.0);
			kahluaTable2.rawset("count", int3 + "");
			kahluaTable2.rawset("cat", string2);
			kahluaTable2.rawset("parrentId", long2);
			kahluaTable2.rawset("hasParrent", long2 != -1L);
			kahluaTable2.rawset("container", string3);
			kahluaTable2.rawset("inInv", boolean2);
			kahluaTable.rawset(kahluaTable.size() + 1, kahluaTable2);
		}

		LuaEventManager.triggerEvent("MngInvReceiveItems", kahluaTable);
	}

	public static void sendGetItemInvMng(long long1) {
	}

	private void receiveSpawnRegion(ByteBuffer byteBuffer) {
		if (instance.ServerSpawnRegions == null) {
			instance.ServerSpawnRegions = LuaManager.platform.newTable();
		}

		int int1 = byteBuffer.getInt();
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		try {
			kahluaTable.load((ByteBuffer)byteBuffer, 143);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		instance.ServerSpawnRegions.rawset(int1, kahluaTable);
	}

	private static void receiveClimateManagerPacket(ByteBuffer byteBuffer) {
		ClimateManager climateManager = ClimateManager.getInstance();
		if (climateManager != null) {
			try {
				climateManager.receiveClimatePacket(byteBuffer, (UdpConnection)null);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static void sendIsoRegionDataRequest() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)202, byteBufferWriter);
		connection.endPacketImmediate();
	}

	public void sendSandboxOptionsToServer(SandboxOptions sandboxOptions) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.doPacket((short)31, byteBufferWriter);
		try {
			sandboxOptions.save(byteBufferWriter.bb);
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		} finally {
			connection.endPacket();
		}
	}

	private void receiveSandboxOptions(ByteBuffer byteBuffer) {
		try {
			SandboxOptions.instance.load(byteBuffer);
			SandboxOptions.instance.applySettings();
			SandboxOptions.instance.toLua();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	private void receiveChunkObjectState(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		short short2 = byteBuffer.getShort();
		IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(short1, short2);
		if (chunk != null) {
			chunk.loadObjectState(byteBuffer);
		}
	}

	static  {
		port = GameServer.DEFAULT_PORT;
		checksum = "";
		checksumValid = false;
		pingsList = new ArrayList();
		loadedCells = new ClientServerMap[4];
		isPaused = false;
		MainLoopNetData = new ArrayList();
		LoadingMainLoopNetData = new ArrayList();
		DelayedCoopNetData = new ArrayList();
		ServerPredictedAhead = 0.0F;
		IDToPlayerMap = new HashMap();
		IDToZombieMap = new TShortObjectHashMap();
		askPing = false;
		startAuth = null;
		poisonousBerry = null;
		poisonousMushroom = null;
		pingLimitTime = 0;
	}
	private static enum RequestState {

		Start,
		RequestDescriptors,
		ReceivedDescriptors,
		RequestMetaGrid,
		ReceivedMetaGrid,
		RequestMapZone,
		ReceivedMapZone,
		RequestPlayerZombieDescriptors,
		ReceivedPlayerZombieDescriptors,
		Complete;
	}
}
