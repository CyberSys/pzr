package zombie.network;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkTeleport;
import zombie.characters.NetworkZombieVariables;
import zombie.characters.Safety;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.skills.PerkFactory;
import zombie.chat.ChatManager;
import zombie.commands.PlayerType;
import zombie.commands.serverCommands.LogCommand;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.raknet.VoiceManager;
import zombie.core.raknet.VoiceManagerData;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.ZNetSessionState;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.CGlobalObjectNetwork;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.AlarmClock;
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
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.packets.ActionPacket;
import zombie.network.packets.AddXp;
import zombie.network.packets.CleanBurn;
import zombie.network.packets.DeadPlayerPacket;
import zombie.network.packets.DeadZombiePacket;
import zombie.network.packets.Disinfect;
import zombie.network.packets.EventPacket;
import zombie.network.packets.PlaySoundPacket;
import zombie.network.packets.PlayWorldSoundPacket;
import zombie.network.packets.PlayerDataRequestPacket;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.RemoveBullet;
import zombie.network.packets.RemoveCorpseFromMap;
import zombie.network.packets.RemoveGlass;
import zombie.network.packets.SafetyPacket;
import zombie.network.packets.StartFire;
import zombie.network.packets.Stitch;
import zombie.network.packets.StopSoundPacket;
import zombie.network.packets.SyncClothingPacket;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.network.packets.SyncNonPvpZonePacket;
import zombie.network.packets.SyncSafehousePacket;
import zombie.network.packets.ValidatePacket;
import zombie.network.packets.VehicleAuthorizationPacket;
import zombie.network.packets.WaveSignal;
import zombie.network.packets.hit.HitCharacterPacket;
import zombie.network.packets.hit.PlayerHitPlayerPacket;
import zombie.network.packets.hit.PlayerHitSquarePacket;
import zombie.network.packets.hit.PlayerHitVehiclePacket;
import zombie.network.packets.hit.PlayerHitZombiePacket;
import zombie.network.packets.hit.VehicleHitPacket;
import zombie.network.packets.hit.VehicleHitPlayerPacket;
import zombie.network.packets.hit.VehicleHitZombiePacket;
import zombie.network.packets.hit.ZombieHitPlayerPacket;
import zombie.popman.MPDebugInfo;
import zombie.popman.NetworkZombieSimulator;
import zombie.popman.ZombieCountOptimiser;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.savefile.ClientPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.util.AddCoopPlayer;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.world.moddata.GlobalModData;


public class GameClient {
	public static final GameClient instance = new GameClient();
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
	public byte ID = -1;
	public float timeSinceKeepAlive = 0.0F;
	UpdateLimit itemSendFrequency = new UpdateLimit(3000L);
	public static int port;
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
	private final ArrayList players = new ArrayList();
	public boolean idMapDirty = true;
	private static final int sendZombieWithoutNeighbor = 4000;
	private static final int sendZombieWithNeighbor = 200;
	public final UpdateLimit sendZombieTimer = new UpdateLimit(4000L);
	public final UpdateLimit sendZombieRequestsTimer = new UpdateLimit(200L);
	private final UpdateLimit UpdateChannelsRoamingLimit = new UpdateLimit(3010L);
	private long disconnectTime = System.currentTimeMillis();
	private static final long disconnectTimeLimit = 10000L;
	public static final Map positions;
	private int safehouseUpdateTimer = 0;
	@Deprecated
	private boolean delayPacket = false;
	private final ArrayList delayedDisconnect = new ArrayList();
	private volatile GameClient.RequestState request;
	public KahluaTable ServerSpawnRegions = null;
	static final ConcurrentLinkedQueue MainLoopNetDataQ;
	static final ArrayList MainLoopNetData;
	static final ArrayList LoadingMainLoopNetData;
	static final ArrayList DelayedCoopNetData;
	public boolean bConnected = false;
	UpdateLimit PlayerUpdateReliableLimit = new UpdateLimit(2000L);
	public int TimeSinceLastUpdate = 0;
	ByteBuffer staticTest = ByteBuffer.allocate(20000);
	ByteBufferWriter wr;
	long StartHeartMilli;
	long EndHeartMilli;
	public int ping;
	public static float ServerPredictedAhead;
	public static final HashMap IDToPlayerMap;
	public static final TShortObjectHashMap IDToZombieMap;
	public static boolean bIngame;
	public static boolean askPing;
	public final ArrayList ServerMods;
	public ErosionConfig erosionConfig;
	public static Calendar startAuth;
	public static String poisonousBerry;
	public static String poisonousMushroom;
	final ArrayList incomingNetData;
	private final HashMap itemsToSend;
	private final HashMap itemsToSendRemove;
	KahluaTable dbSchema;

	public GameClient() {
		this.wr = new ByteBufferWriter(this.staticTest);
		this.StartHeartMilli = 0L;
		this.EndHeartMilli = 0L;
		this.ping = 0;
		this.ServerMods = new ArrayList();
		this.incomingNetData = new ArrayList();
		this.itemsToSend = new HashMap();
		this.itemsToSendRemove = new HashMap();
	}

	public IsoPlayer getPlayerByOnlineID(short short1) {
		return (IsoPlayer)IDToPlayerMap.get(short1);
	}

	public void init() {
		LoadingMainLoopNetData.clear();
		MainLoopNetDataQ.clear();
		MainLoopNetData.clear();
		DelayedCoopNetData.clear();
		bIngame = false;
		IDToPlayerMap.clear();
		IDToZombieMap.clear();
		pingsList.clear();
		this.itemsToSend.clear();
		this.itemsToSendRemove.clear();
		IDToZombieMap.setAutoCompactionFactor(0.0F);
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
				DebugLog.Network.printException(exception, "Exception thrown during GameClient.startClient.", LogSeverity.Error);
			}
		}
	}

	static void receiveStatistic(ByteBuffer byteBuffer, short short1) {
		try {
			long long1 = byteBuffer.getLong();
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.Statistic.doPacket(byteBufferWriter);
			byteBufferWriter.putLong(long1);
			MPStatisticClient.getInstance().send(byteBufferWriter);
			PacketTypes.PacketType.Statistic.send(connection);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static void receiveStatisticRequest(ByteBuffer byteBuffer, short short1) {
		try {
			MPStatistic.getInstance().setStatisticTable(byteBuffer);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		LuaEventManager.triggerEvent("OnServerStatisticReceived");
	}

	static void receivePlayerUpdate(ByteBuffer byteBuffer, short short1) {
		PlayerPacket playerPacket = PlayerPacket.l_receive.playerPacket;
		playerPacket.parse(byteBuffer, connection);
		try {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(playerPacket.id);
			if (player == null) {
				PlayerDataRequestPacket playerDataRequestPacket = new PlayerDataRequestPacket();
				playerDataRequestPacket.set(playerPacket.id);
				ByteBufferWriter byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType.PlayerDataRequest.doPacket(byteBufferWriter);
				playerDataRequestPacket.write(byteBufferWriter);
				PacketTypes.PacketType.PlayerDataRequest.send(connection);
			} else {
				player.lastRemoteUpdate = System.currentTimeMillis();
				rememberPlayerPosition(player, playerPacket.realx, playerPacket.realy);
				if (!player.networkAI.isSetVehicleHit()) {
					player.networkAI.parse(playerPacket);
				}

				player.bleedingLevel = playerPacket.bleedingLevel;
				if (player.getVehicle() == null && !playerPacket.usePathFinder && (player.networkAI.distance.getLength() > 7.0F || IsoUtils.DistanceTo(playerPacket.x, playerPacket.y, (float)playerPacket.z, player.x, player.y, player.z) > 1.0F && (int)player.z != playerPacket.z)) {
					NetworkTeleport.update(player, playerPacket);
					NetworkTeleport.teleport(player, playerPacket, 1.0F);
				}

				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)playerPacket.x, (double)playerPacket.y, (double)playerPacket.z);
				if (square != null) {
					if (player.isAlive() && !IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
						IsoWorld.instance.CurrentCell.getObjectList().add(player);
						player.setCurrent(square);
					}
				} else if (IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
					player.removeFromWorld();
					player.removeFromSquare();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static void receiveZombieSimulation(ByteBuffer byteBuffer, short short1) {
		NetworkZombieSimulator.getInstance().clear();
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			instance.sendZombieTimer.setUpdatePeriod(200L);
		} else {
			instance.sendZombieTimer.setUpdatePeriod(4000L);
		}

		short short2 = byteBuffer.getShort();
		short short3;
		short short4;
		for (short3 = 0; short3 < short2; ++short3) {
			short4 = byteBuffer.getShort();
			IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short4);
			if (zombie != null) {
				VirtualZombieManager.instance.removeZombieFromWorld(zombie);
			}
		}

		short3 = byteBuffer.getShort();
		for (short4 = 0; short4 < short3; ++short4) {
			short short5 = byteBuffer.getShort();
			NetworkZombieSimulator.getInstance().add(short5);
		}

		NetworkZombieSimulator.getInstance().added();
		NetworkZombieSimulator.getInstance().receivePacket(byteBuffer, connection);
	}

	static void receiveZombieControl(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short2);
		if (zombie != null) {
			NetworkZombieVariables.setInt(zombie, short3, int1);
		}
	}

	public void Shutdown() {
		if (this.bClientStarted) {
			this.udpEngine.Shutdown();
			this.bClientStarted = false;
		}
	}

	public void update() {
		ZombieCountOptimiser.startCount();
		if (this.safehouseUpdateTimer == 0 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
			this.safehouseUpdateTimer = 3000;
			SafeHouse.updateSafehousePlayersConnected();
		}

		if (this.safehouseUpdateTimer > 0) {
			--this.safehouseUpdateTimer;
		}

		for (ZomboidNetData zomboidNetData = (ZomboidNetData)MainLoopNetDataQ.poll(); zomboidNetData != null; zomboidNetData = (ZomboidNetData)MainLoopNetDataQ.poll()) {
			MainLoopNetData.add(zomboidNetData);
		}

		synchronized (this.delayedDisconnect) {
			while (!this.delayedDisconnect.isEmpty()) {
				int int1 = (Integer)this.delayedDisconnect.remove(0);
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
		int int2;
		ZomboidNetData zomboidNetData2;
		if (!this.bConnectionLost) {
			if (!this.bPlayerConnectSent) {
				for (int2 = 0; int2 < MainLoopNetData.size(); ++int2) {
					zomboidNetData2 = (ZomboidNetData)MainLoopNetData.get(int2);
					if (!this.gameLoadingDealWithNetData(zomboidNetData2)) {
						LoadingMainLoopNetData.add(zomboidNetData2);
					}
				}

				MainLoopNetData.clear();
				WorldStreamer.instance.updateMain();
			} else {
				if (!LoadingMainLoopNetData.isEmpty()) {
					DebugLog.log(DebugType.Network, "Processing delayed packets...");
					MainLoopNetData.addAll(0, LoadingMainLoopNetData);
					LoadingMainLoopNetData.clear();
				}

				if (!DelayedCoopNetData.isEmpty() && IsoWorld.instance.AddCoopPlayers.isEmpty()) {
					DebugLog.log(DebugType.Network, "Processing delayed coop packets...");
					MainLoopNetData.addAll(0, DelayedCoopNetData);
					DelayedCoopNetData.clear();
				}

				long long1 = System.currentTimeMillis();
				int int3;
				for (int3 = 0; int3 < MainLoopNetData.size(); ++int3) {
					ZomboidNetData zomboidNetData3 = (ZomboidNetData)MainLoopNetData.get(int3);
					if (zomboidNetData3.time + (long)this.DEBUG_PING <= long1) {
						this.mainLoopDealWithNetData(zomboidNetData3);
						MainLoopNetData.remove(int3--);
					}
				}

				for (int3 = 0; int3 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int3) {
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
					exception.printStackTrace();
					ExceptionLogger.logException(exception);
				}

				try {
					VehicleManager.instance.clientUpdate();
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}

				if (this.UpdateChannelsRoamingLimit.Check()) {
					VoiceManager.getInstance().UpdateChannelsRoaming(connection);
				}

				this.objectSyncReq.sendRequests(connection);
				this.worldObjectsSyncReq.sendRequests(connection);
				WorldStreamer.instance.updateMain();
				MPStatisticClient.getInstance().update();
				this.timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
			}
		} else {
			if (!this.bPlayerConnectSent) {
				for (int2 = 0; int2 < MainLoopNetData.size(); ++int2) {
					zomboidNetData2 = (ZomboidNetData)MainLoopNetData.get(int2);
					this.gameLoadingDealWithNetData(zomboidNetData2);
				}

				MainLoopNetData.clear();
			} else {
				for (int2 = 0; int2 < MainLoopNetData.size(); ++int2) {
					zomboidNetData2 = (ZomboidNetData)MainLoopNetData.get(int2);
					if (zomboidNetData2.type == PacketTypes.PacketType.Kicked) {
						String string = Translator.getText(GameWindow.ReadString(zomboidNetData2.buffer));
						String string2 = Translator.getText(GameWindow.ReadString(zomboidNetData2.buffer));
						GameWindow.kickReason = string + " " + string2;
						DebugLog.Multiplayer.warn("ReceiveKickedDisconnect: " + string2);
					}
				}

				MainLoopNetData.clear();
			}

			GameWindow.bServerDisconnected = true;
		}
	}

	public void smashWindow(IsoWindow window, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SmashWindow.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(window.square.getX());
		byteBufferWriter.putInt(window.square.getY());
		byteBufferWriter.putInt(window.square.getZ());
		byteBufferWriter.putByte((byte)window.square.getObjects().indexOf(window));
		byteBufferWriter.putByte((byte)int1);
		PacketTypes.PacketType.SmashWindow.send(connection);
	}

	public static void getCustomModData() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.getModData.doPacket(byteBufferWriter);
		PacketTypes.PacketType.getModData.send(connection);
	}

	static void receiveStitch(ByteBuffer byteBuffer, short short1) {
		Stitch stitch = new Stitch();
		stitch.parse(byteBuffer, connection);
		if (stitch.isConsistent() && stitch.validate(connection)) {
			stitch.process();
		}
	}

	static void receiveBandage(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			boolean boolean2 = byteBuffer.get() == 1;
			String string = GameWindow.ReadStringUTF(byteBuffer);
			player.getBodyDamage().SetBandaged(int1, boolean1, float1, boolean2, string);
		}
	}

	static void receivePingFromClient(ByteBuffer byteBuffer, short short1) {
		MPStatistics.parse(byteBuffer);
	}

	@Deprecated
	static void receiveWoundInfection(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setInfectedWound(boolean1);
		}
	}

	static void receiveDisinfect(ByteBuffer byteBuffer, short short1) {
		Disinfect disinfect = new Disinfect();
		disinfect.parse(byteBuffer, connection);
		if (disinfect.isConsistent() && disinfect.validate(connection)) {
			disinfect.process();
		}
	}

	static void receiveSplint(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
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

	static void receiveRemoveGlass(ByteBuffer byteBuffer, short short1) {
		RemoveGlass removeGlass = new RemoveGlass();
		removeGlass.parse(byteBuffer, connection);
		if (removeGlass.isConsistent() && removeGlass.validate(connection)) {
			removeGlass.process();
		}
	}

	static void receiveRemoveBullet(ByteBuffer byteBuffer, short short1) {
		RemoveBullet removeBullet = new RemoveBullet();
		removeBullet.parse(byteBuffer, connection);
		if (removeBullet.isConsistent() && removeBullet.validate(connection)) {
			removeBullet.process();
		}
	}

	static void receiveCleanBurn(ByteBuffer byteBuffer, short short1) {
		CleanBurn cleanBurn = new CleanBurn();
		cleanBurn.parse(byteBuffer, connection);
		if (cleanBurn.isConsistent() && cleanBurn.validate(connection)) {
			cleanBurn.process();
		}
	}

	@Deprecated
	static void receiveAdditionalPain(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + float1);
		}
	}

	@Deprecated
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
		if (zomboidNetData.type == null) {
			ZomboidNetDataPool.instance.discard(zomboidNetData);
		} else {
			++zomboidNetData.type.clientPacketCount;
			try {
				this.mainLoopHandlePacketInternal(zomboidNetData, byteBuffer);
				if (this.delayPacket) {
					byteBuffer.position(int1);
					DelayedCoopNetData.add(zomboidNetData);
					return;
				}
			} catch (Exception exception) {
				DebugLog.Network.printException(exception, "Error with packet of type: " + zomboidNetData.type, LogSeverity.Error);
			}

			ZomboidNetDataPool.instance.discard(zomboidNetData);
		}
	}

	private void mainLoopHandlePacketInternal(ZomboidNetData zomboidNetData, ByteBuffer byteBuffer) throws IOException {
		if (DebugOptions.instance.Network.Client.MainLoop.getValue()) {
			zomboidNetData.type.onMainLoopHandlePacketInternal(byteBuffer);
		}
	}

	static void receiveAddBrokenGlass(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.addBrokenGlass();
		}
	}

	static void receivePlayerDamageFromCarCrash(ByteBuffer byteBuffer, short short1) {
		float float1 = byteBuffer.getFloat();
		if (IsoPlayer.getInstance().getVehicle() == null) {
			DebugLog.Multiplayer.error("Receive damage from car crash, can\'t find vehicle");
		} else {
			IsoPlayer.getInstance().getVehicle().addRandomDamageFromCrash(IsoPlayer.getInstance(), float1);
		}
	}

	static void receivePacketCounts(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			short short2 = byteBuffer.getShort();
			long long1 = byteBuffer.getLong();
			PacketTypes.PacketType packetType = (PacketTypes.PacketType)PacketTypes.packetTypes.get(short2);
			if (packetType != null) {
				packetType.serverPacketCount = long1;
			}
		}
	}

	public void requestPacketCounts() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PacketCounts.doPacket(byteBufferWriter);
		PacketTypes.PacketType.PacketCounts.send(connection);
	}

	public static boolean IsClientPaused() {
		return isPaused;
	}

	static void receiveStartPause(ByteBuffer byteBuffer, short short1) {
		isPaused = true;
		LuaEventManager.triggerEvent("OnServerStartSaving");
	}

	static void receiveStopPause(ByteBuffer byteBuffer, short short1) {
		isPaused = false;
		LuaEventManager.triggerEvent("OnServerFinishSaving");
	}

	static void receiveChatMessageToPlayer(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processChatMessagePacket(byteBuffer);
	}

	static void receivePlayerConnectedToChat(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().setFullyConnected();
	}

	static void receivePlayerJoinChat(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processJoinChatPacket(byteBuffer);
	}

	static void receiveInvMngRemoveItem(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		InventoryItem inventoryItem = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(int1);
		if (inventoryItem == null) {
			DebugLog.log("ERROR: invMngRemoveItem can not find " + int1 + " item.");
		} else {
			IsoPlayer.getInstance().removeWornItem(inventoryItem);
			if (inventoryItem.getCategory().equals("Clothing")) {
				LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
			}

			if (inventoryItem == IsoPlayer.getInstance().getPrimaryHandItem()) {
				IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
			} else if (inventoryItem == IsoPlayer.getInstance().getSecondaryHandItem()) {
				IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
			}

			boolean boolean1 = IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(int1);
			if (!boolean1) {
				DebugLog.log("ERROR: GameClient.invMngRemoveItem can not remove item " + int1);
			}
		}
	}

	static void receiveInvMngGetItem(ByteBuffer byteBuffer, short short1) throws IOException {
		short short2 = byteBuffer.getShort();
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = InventoryItem.loadItem(byteBuffer, 194);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (inventoryItem != null) {
			IsoPlayer.getInstance().getInventory().addItem(inventoryItem);
		}
	}

	static void receiveInvMngReqItem(ByteBuffer byteBuffer, short short1) throws IOException {
		int int1 = 0;
		String string = null;
		if (byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		} else {
			int1 = byteBuffer.getInt();
		}

		short short2 = byteBuffer.getShort();
		InventoryItem inventoryItem = null;
		if (string == null) {
			inventoryItem = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(int1);
			if (inventoryItem == null) {
				DebugLog.log("ERROR: invMngRemoveItem can not find " + int1 + " item.");
				return;
			}
		} else {
			inventoryItem = InventoryItemFactory.CreateItem(string);
		}

		if (inventoryItem != null) {
			if (string == null) {
				IsoPlayer.getInstance().removeWornItem(inventoryItem);
				if (inventoryItem.getCategory().equals("Clothing")) {
					LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
				}

				if (inventoryItem == IsoPlayer.getInstance().getPrimaryHandItem()) {
					IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
					LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
				} else if (inventoryItem == IsoPlayer.getInstance().getSecondaryHandItem()) {
					IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
					LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
				}

				IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(inventoryItem.getID());
			} else {
				IsoPlayer.getInstance().getInventory().RemoveOneOf(string.split("\\.")[1]);
			}

			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.InvMngGetItem.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(short2);
			inventoryItem.saveWithSize(byteBufferWriter.bb, false);
			PacketTypes.PacketType.InvMngGetItem.send(connection);
		}
	}

	public static void invMngRequestItem(int int1, String string, IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.InvMngReqItem.doPacket(byteBufferWriter);
		if (string != null) {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putUTF(string);
		} else {
			byteBufferWriter.putByte((byte)0);
			byteBufferWriter.putInt(int1);
		}

		byteBufferWriter.putShort(IsoPlayer.getInstance().getOnlineID());
		byteBufferWriter.putShort(player.getOnlineID());
		PacketTypes.PacketType.InvMngReqItem.send(connection);
	}

	public static void invMngRequestRemoveItem(int int1, IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.InvMngRemoveItem.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putShort(player.getOnlineID());
		PacketTypes.PacketType.InvMngRemoveItem.send(connection);
	}

	static void receiveSyncFaction(ByteBuffer byteBuffer, short short1) {
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

	static void receiveSyncNonPvpZone(ByteBuffer byteBuffer, short short1) {
		try {
			SyncNonPvpZonePacket syncNonPvpZonePacket = new SyncNonPvpZonePacket();
			syncNonPvpZonePacket.parse(byteBuffer, connection);
			if (syncNonPvpZonePacket.isConsistent()) {
				syncNonPvpZonePacket.process();
				if (Core.bDebug) {
					DebugLog.Multiplayer.debugln("ReceiveSyncNonPvpZone: %s", syncNonPvpZonePacket.getDescription());
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveSyncNonPvpZone: failed", LogSeverity.Error);
		}
	}

	static void receiveChangeTextColor(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			player.setSpeakColourInfo(new ColorInfo(float1, float2, float3, 1.0F));
		}
	}

	static void receivePlaySoundEveryPlayer(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		DebugLog.log(DebugType.Sound, "sound: received " + string + " at " + int1 + "," + int2 + "," + int3);
		if (!Core.SoundDisabled) {
			FMOD_STUDIO_EVENT_DESCRIPTION fMOD_STUDIO_EVENT_DESCRIPTION = FMODManager.instance.getEventDescription(string);
			if (fMOD_STUDIO_EVENT_DESCRIPTION == null) {
				return;
			}

			long long1 = javafmod.FMOD_Studio_System_CreateEventInstance(fMOD_STUDIO_EVENT_DESCRIPTION.address);
			if (long1 <= 0L) {
				return;
			}

			javafmod.FMOD_Studio_EventInstance_SetVolume(long1, (float)Core.getInstance().getOptionAmbientVolume() / 20.0F);
			javafmod.FMOD_Studio_EventInstance3D(long1, (float)int1, (float)int2, (float)int3);
			javafmod.FMOD_Studio_StartEvent(long1);
			javafmod.FMOD_Studio_ReleaseEventInstance(long1);
		}
	}

	static void receiveCataplasm(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
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

	static void receiveStopFire(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.stopFire();
		}
	}

	static void receiveAddAlarm(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		DebugLog.log(DebugType.Multiplayer, "ReceiveAlarm at [ " + int1 + " , " + int2 + " ]");
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, 0);
		if (square != null && square.getBuilding() != null && square.getBuilding().getDef() != null) {
			square.getBuilding().getDef().bAlarmed = true;
			AmbientStreamManager.instance.doAlarm(square.room.def);
		}
	}

	static void receiveAddExplosiveTrap(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			InventoryItem inventoryItem = null;
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 194);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			HandWeapon handWeapon = inventoryItem != null ? (HandWeapon)inventoryItem : null;
			IsoTrap trap = new IsoTrap(handWeapon, square.getCell(), square);
			square.AddTileObject(trap);
			trap.triggerExplosion(handWeapon.getSensorRange() > 0);
		}
	}

	static void receiveTeleport(ByteBuffer byteBuffer, short short1) {
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

	static void receiveRemoveBlood(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.removeBlood(true, boolean1);
		}
	}

	static void receiveSyncThumpable(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			instance.delayPacket(int1, int2, int3);
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

	static void receiveSyncDoorKey(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			instance.delayPacket(int1, int2, int3);
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

	static void receiveConstructedZone(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoMetaGrid.Zone zone = IsoWorld.instance.MetaGrid.getZoneAt(int1, int2, int3);
		if (zone != null) {
			zone.setHaveConstruction(true);
		}
	}

	static void receiveAddCoopPlayer(ByteBuffer byteBuffer, short short1) {
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

	static void receiveZombieDescriptors(ByteBuffer byteBuffer, short short1) {
		try {
			SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
			descriptor.load(byteBuffer, 194);
			SharedDescriptors.registerPlayerZombieDescriptor(descriptor);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void checksumServer() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
		String string = checksum;
		byteBufferWriter.putUTF(string + ScriptManager.instance.getChecksum());
		PacketTypes.PacketType.Checksum.send(connection);
	}

	static void receiveRegisterZone(ByteBuffer byteBuffer, short short1) {
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

	static void receiveAddXpCommand(ByteBuffer byteBuffer, short short1) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
		if (player != null && !player.isDead()) {
			player.getXp().AddXP(perk, (float)byteBuffer.getInt());
		}
	}

	public void sendAddXp(IsoPlayer player, PerkFactory.Perk perk, int int1) {
		AddXp addXp = new AddXp();
		addXp.set(player, perk, int1);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.AddXP.doPacket(byteBufferWriter);
		addXp.write(byteBufferWriter);
		PacketTypes.PacketType.AddXP.send(connection);
	}

	static void receiveSyncXP(ByteBuffer byteBuffer, short short1) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		if (player != null && !player.isDead()) {
			try {
				player.getXp().load(byteBuffer, 194);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void sendSyncXp(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncXP.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.getOnlineID());
		try {
			player.getXp().save(byteBufferWriter.bb);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		PacketTypes.PacketType.SyncXP.send(connection);
	}

	public void sendTransactionID(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SendTransactionID.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.getOnlineID());
		byteBufferWriter.putInt(player.getTransactionID());
		PacketTypes.PacketType.SendTransactionID.send(connection);
	}

	static void receiveUserlog(ByteBuffer byteBuffer, short short1) {
		ArrayList arrayList = new ArrayList();
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		for (int int2 = 0; int2 < int1; ++int2) {
			arrayList.add(new Userlog(string, Userlog.UserlogType.fromIndex(byteBuffer.getInt()).toString(), GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt()));
		}

		LuaEventManager.triggerEvent("OnReceiveUserlog", string, arrayList);
	}

	static void receiveAddXp(ByteBuffer byteBuffer, short short1) {
		AddXp addXp = new AddXp();
		addXp.parse(byteBuffer, connection);
		if (addXp.isConsistent()) {
			addXp.process();
		}
	}

	static void receivePing(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt() - 1;
		String string2 = int1 + "/" + byteBuffer.getInt();
		LuaEventManager.triggerEvent("ServerPinged", string, string2);
		connection.forceDisconnect("receive-ping");
		askPing = false;
	}

	static void receiveChecksumLoading(ByteBuffer byteBuffer, short short1) {
		NetChecksum.comparer.clientPacket(byteBuffer);
	}

	static void receiveKickedLoading(ByteBuffer byteBuffer, short short1) {
		String string = Translator.getText(GameWindow.ReadString(byteBuffer));
		String string2 = Translator.getText(GameWindow.ReadString(byteBuffer));
		LuaEventManager.triggerEvent("OnConnectFailed", string2);
		connection.username = null;
		GameWindow.bServerDisconnected = true;
		GameWindow.kickReason = string2;
		connection.forceDisconnect("receive-kick-loading");
		DebugLog.Multiplayer.warn("ReceiveKickedLoading: " + string2);
	}

	static void receiveServerMapLoading(ByteBuffer byteBuffer, short short1) {
		ClientServerMap.receivePacket(byteBuffer);
	}

	static void receiveChangeSafety(ByteBuffer byteBuffer, short short1) {
		try {
			SafetyPacket safetyPacket = new SafetyPacket();
			safetyPacket.parse(byteBuffer, connection);
			safetyPacket.log("ReceiveChangeSafety");
			safetyPacket.process();
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveChangeSafety: failed", LogSeverity.Error);
		}
	}

	public static void sendChangeSafety(Safety safety) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ChangeSafety.doPacket(byteBufferWriter);
		try {
			SafetyPacket safetyPacket = new SafetyPacket(safety);
			safetyPacket.write(byteBufferWriter);
			PacketTypes.PacketType.ChangeSafety.send(connection);
			safetyPacket.log("SendChangeSafety");
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendChangeSafety: failed", LogSeverity.Error);
		}
	}

	static void receiveAddItemInInventory(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null && !player.isDead()) {
			player.getInventory().AddItems(string, int1);
		}
	}

	static void receiveKicked(ByteBuffer byteBuffer, short short1) {
		String string = Translator.getText(GameWindow.ReadString(byteBuffer));
		String string2 = Translator.getText(GameWindow.ReadString(byteBuffer));
		String string3 = string + " " + string2;
		if (!StringUtils.isNullOrEmpty(string3)) {
			ChatManager.getInstance().showServerChatMessage(string3);
		}

		connection.username = null;
		GameWindow.bServerDisconnected = true;
		GameWindow.kickReason = string3;
		connection.forceDisconnect("receive-kick");
		DebugLog.Multiplayer.warn("ReceiveKicked: " + string2);
	}

	public void addDisconnectPacket(int int1) {
		synchronized (this.delayedDisconnect) {
			this.delayedDisconnect.add(int1);
		}
		ConnectionManager.log("Receive Disconnect [" + int1 + "]", (UdpConnection)null);
	}

	public void connectionLost() {
		this.bConnectionLost = true;
		positions.clear();
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

			if (!IsoPlayer.getInstance().getInventory().contains("Dice") && connection.accessLevel == 1) {
				ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
				return;
			}
		}

		if (string.startsWith("/card") && !IsoPlayer.getInstance().getInventory().contains("CardDeck") && connection.accessLevel == 1) {
			ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("card"));
		} else if (!string.startsWith("/log ")) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.ReceiveCommand.doPacket(byteBufferWriter);
			byteBufferWriter.putUTF(string);
			PacketTypes.PacketType.ReceiveCommand.send(connection);
		} else {
			String string2 = ChatManager.getInstance().getFocusTab().getTitleID();
			if ("UI_chat_admin_tab_title_id".equals(string2)) {
				ByteBufferWriter byteBufferWriter2 = connection.startPacket();
				PacketTypes.PacketType.ReceiveCommand.doPacket(byteBufferWriter2);
				byteBufferWriter2.putUTF(string);
				PacketTypes.PacketType.ReceiveCommand.send(connection);
			} else if ("UI_chat_main_tab_title_id".equals(string2)) {
				String[] stringArray = string.split(" ");
				if (stringArray.length == 3) {
					DebugType debugType = LogCommand.getDebugType(stringArray[1]);
					LogSeverity logSeverity = LogCommand.getLogSeverity(stringArray[2]);
					if (debugType != null && logSeverity != null) {
						DebugLog.enableLog(debugType, logSeverity);
						ChatManager.getInstance().showServerChatMessage(String.format("Client \"%s\" log level is \"%s\"", debugType.name().toLowerCase(), logSeverity.name().toLowerCase()));
					} else {
						ChatManager.getInstance().showServerChatMessage(Translator.getText("UI_ServerOptionDesc_SetLogLevel", debugType == null ? "\"type\"" : debugType.name().toLowerCase(), logSeverity == null ? "\"severity\"" : logSeverity.name().toLowerCase()));
					}
				}
			}
		}
	}

	public static void sendServerPing(long long1) {
		if (connection != null) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.PingFromClient.doPacket(byteBufferWriter);
			byteBufferWriter.putLong(long1);
			PacketTypes.PacketType.PingFromClient.send(connection);
			if (long1 == -1L) {
				DebugLog.Multiplayer.debugln("Player \"%s\" toggled lua debugger", connection.username);
			}
		}
	}

	private boolean gameLoadingDealWithNetData(ZomboidNetData zomboidNetData) {
		ByteBuffer byteBuffer = zomboidNetData.buffer;
		try {
			return zomboidNetData.type.onGameLoadingDealWithNetData(byteBuffer);
		} catch (Exception exception) {
			DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type);
			exception.printStackTrace();
			ZomboidNetDataPool.instance.discard(zomboidNetData);
			return true;
		}
	}

	static void receiveWorldMessage(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		string2 = string2.replaceAll("<", "&lt;");
		string2 = string2.replaceAll(">", "&gt;");
		ChatManager.getInstance().addMessage(string, string2);
	}

	static void receiveReloadOptions(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
		}
	}

	static void receiveStartRain(ByteBuffer byteBuffer, short short1) {
		RainManager.setRandRainMin(byteBuffer.getInt());
		RainManager.setRandRainMax(byteBuffer.getInt());
		RainManager.startRaining();
		RainManager.RainDesiredIntensity = byteBuffer.getFloat();
	}

	static void receiveStopRain(ByteBuffer byteBuffer, short short1) {
		RainManager.stopRaining();
	}

	static void receiveWeather(ByteBuffer byteBuffer, short short1) {
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

	static void receiveSyncClock(ByteBuffer byteBuffer, short short1) {
		GameTime gameTime = GameTime.getInstance();
		boolean boolean1 = bFastForward;
		bFastForward = byteBuffer.get() == 1;
		float float1 = byteBuffer.getFloat();
		int int1 = byteBuffer.getInt();
		float float2 = gameTime.getTimeOfDay() - gameTime.getLastTimeOfDay();
		gameTime.setTimeOfDay(float1);
		gameTime.setLastTimeOfDay(float1 - float2);
		if (gameTime.getLastTimeOfDay() < 0.0F) {
			gameTime.setLastTimeOfDay(float1 - float2 + 24.0F);
		}

		gameTime.ServerLastTimeOfDay = gameTime.ServerTimeOfDay;
		gameTime.ServerTimeOfDay = float1;
		gameTime.setNightsSurvived(int1);
		if (gameTime.ServerLastTimeOfDay > gameTime.ServerTimeOfDay) {
			++gameTime.ServerNewDays;
		}
	}

	static void receiveClientCommand(ByteBuffer byteBuffer, short short1) {
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

		LuaEventManager.triggerEvent("OnServerCommand", string, string2, kahluaTable);
	}

	static void receiveGlobalObjects(ByteBuffer byteBuffer, short short1) throws IOException {
		CGlobalObjectNetwork.receive(byteBuffer);
	}

	private boolean receiveLargeFilePart(ByteBuffer byteBuffer, String string) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		File file = new File(string);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, int2 > 0);
			try {
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				try {
					bufferedOutputStream.write(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
				} catch (Throwable throwable) {
					try {
						bufferedOutputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedOutputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileOutputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileOutputStream.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return int2 + int3 >= int1;
	}

	static void receiveRequestData(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		if ("descriptors.bin".equals(string)) {
			try {
				instance.receiveDataZombieDescriptors(byteBuffer);
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}

			instance.request = GameClient.RequestState.ReceivedDescriptors;
		}

		boolean boolean1;
		if ("playerzombiedesc".equals(string)) {
			boolean1 = instance.receiveLargeFilePart(byteBuffer, ZomboidFileSystem.instance.getFileNameInCurrentSave("reanimated.bin"));
			if (boolean1) {
				try {
					instance.receivePlayerZombieDescriptors();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}

				instance.request = GameClient.RequestState.ReceivedPlayerZombieDescriptors;
			}
		}

		if ("map_meta.bin".equals(string)) {
			boolean1 = instance.receiveLargeFilePart(byteBuffer, ZomboidFileSystem.instance.getFileNameInCurrentSave("map_meta.bin"));
			if (boolean1) {
				instance.request = GameClient.RequestState.ReceivedMetaGrid;
			}
		}

		if ("map_zone.bin".equals(string)) {
			boolean1 = instance.receiveLargeFilePart(byteBuffer, ZomboidFileSystem.instance.getFileNameInCurrentSave("map_zone.bin"));
			if (boolean1) {
				instance.request = GameClient.RequestState.ReceivedMapZone;
			}
		}

		if ("radio".equals(string)) {
			try {
				ZomboidRadio.receiveRequestData(byteBuffer);
			} catch (Exception exception2) {
				ExceptionLogger.logException(exception2);
			}

			instance.request = GameClient.RequestState.ReceivedRadioData;
		}
	}

	public void GameLoadingRequestData() {
		this.request = GameClient.RequestState.Start;
		while (this.request != GameClient.RequestState.Complete) {
			ByteBufferWriter byteBufferWriter;
			switch (this.request) {
			case Start: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF("descriptors.bin");
				PacketTypes.PacketType.RequestData.send(connection);
				this.request = GameClient.RequestState.RequestDescriptors;
			
			case RequestDescriptors: 
			
			case RequestMetaGrid: 
			
			case RequestMapZone: 
			
			case RequestPlayerZombieDescriptors: 
			
			case RequestRadioData: 
			
			case Complete: 
			
			default: 
				break;
			
			case ReceivedDescriptors: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF("map_meta.bin");
				PacketTypes.PacketType.RequestData.send(connection);
				this.request = GameClient.RequestState.RequestMetaGrid;
				break;
			
			case ReceivedMetaGrid: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF("map_zone.bin");
				PacketTypes.PacketType.RequestData.send(connection);
				this.request = GameClient.RequestState.RequestMapZone;
				break;
			
			case ReceivedMapZone: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF("playerzombiedesc");
				PacketTypes.PacketType.RequestData.send(connection);
				this.request = GameClient.RequestState.RequestPlayerZombieDescriptors;
				break;
			
			case ReceivedPlayerZombieDescriptors: 
				byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF("radio");
				PacketTypes.PacketType.RequestData.send(connection);
				this.request = GameClient.RequestState.RequestRadioData;
				break;
			
			case ReceivedRadioData: 
				this.request = GameClient.RequestState.Complete;
			
			}

			try {
				Thread.sleep(30L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	static void receiveMetaGrid(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		short short4 = byteBuffer.getShort();
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		if (short2 >= metaGrid.getMinX() && short2 <= metaGrid.getMaxX() && short3 >= metaGrid.getMinY() && short3 <= metaGrid.getMaxY()) {
			IsoMetaCell metaCell = metaGrid.getCellData(short2, short3);
			if (metaCell.info != null && short4 >= 0 && short4 < metaCell.info.RoomList.size()) {
				metaCell.info.getRoom(short4).def.bLightsActive = byteBuffer.get() == 1;
			}
		}
	}

	static void receiveSendCustomColor(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			instance.delayPacket(int1, int2, int3);
		} else {
			if (square != null && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (object != null) {
					object.setCustomColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()));
				}
			}
		}
	}

	static void receiveUpdateItemSprite(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
		if (square == null) {
			instance.delayPacket(int2, int3, int4);
		} else {
			if (square != null && int5 < square.getObjects().size()) {
				try {
					IsoObject object = (IsoObject)square.getObjects().get(int5);
					if (object != null) {
						boolean boolean1 = object.sprite != null && object.sprite.getProperties().Is("HitByCar") && object.sprite.getProperties().Val("DamagedSprite") != null && !object.sprite.getProperties().Val("DamagedSprite").isEmpty();
						object.sprite = IsoSpriteManager.instance.getSprite(int1);
						if (object.sprite == null && !string.isEmpty()) {
							object.setSprite(string);
						}

						object.RemoveAttachedAnims();
						int int6 = byteBuffer.get() & 255;
						for (int int7 = 0; int7 < int6; ++int7) {
							int int8 = byteBuffer.getInt();
							IsoSprite sprite = IsoSpriteManager.instance.getSprite(int8);
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

	static void receiveUpdateOverlaySprite(ByteBuffer byteBuffer, short short1) {
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
			instance.delayPacket(int1, int2, int3);
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

	static void receiveStartFire(ByteBuffer byteBuffer, short short1) {
		StartFire startFire = new StartFire();
		startFire.parse(byteBuffer, connection);
		if (startFire.isConsistent() && startFire.validate(connection)) {
			startFire.process();
		}
	}

	static void receiveBecomeCorpse(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		float float1 = byteBuffer.getFloat();
		byte byte1 = byteBuffer.get();
		IsoGameCharacter gameCharacter = null;
		if (byte1 == 1) {
			gameCharacter = (IsoGameCharacter)IDToZombieMap.get(short3);
		} else if (byte1 == 2) {
			gameCharacter = (IsoGameCharacter)IDToPlayerMap.get(short3);
		}

		if (gameCharacter != null) {
			IsoDeadBody deadBody = new IsoDeadBody(gameCharacter);
			deadBody.setObjectID(short2);
			deadBody.setOnlineID(short3);
			deadBody.setReanimateTime(float1);
			IsoDeadBody.addDeadBodyID(short2, deadBody);
		}
	}

	static void receiveAddCorpseToMap(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
		object.loadFromRemoteBuffer(byteBuffer, false);
		((IsoDeadBody)object).setObjectID(short2);
		((IsoDeadBody)object).setOnlineID(short3);
		IsoDeadBody.addDeadBodyID(short2, (IsoDeadBody)object);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			instance.delayPacket(int1, int2, int3);
		} else {
			square.addCorpse((IsoDeadBody)object, true);
		}
	}

	static void receiveReceiveModData(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null && IsoWorld.instance.isValidSquare(int1, int2, int3) && IsoWorld.instance.CurrentCell.getChunkForGridSquare(int1, int2, int3) != null) {
			square = IsoGridSquare.getNew(IsoWorld.instance.getCell(), (SliceY)null, int1, int2, int3);
		}

		if (square == null) {
			instance.delayPacket(int1, int2, int3);
		} else {
			try {
				square.getModData().load((ByteBuffer)byteBuffer, 194);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			LuaEventManager.triggerEvent("onLoadModDataFromServer", square);
		}
	}

	static void receiveObjectModData(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			instance.delayPacket(int1, int2, int3);
		} else {
			if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (boolean1) {
					try {
						object.getModData().load((ByteBuffer)byteBuffer, 194);
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

	static void receiveObjectChange(ByteBuffer byteBuffer, short short1) {
		byte byte1 = byteBuffer.get();
		short short2;
		String string;
		if (byte1 == 1) {
			short2 = byteBuffer.getShort();
			string = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string);
			}

			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
			if (player != null) {
				player.loadChange(string, byteBuffer);
			}
		} else if (byte1 == 2) {
			short2 = byteBuffer.getShort();
			string = GameWindow.ReadString(byteBuffer);
			if (Core.bDebug) {
				DebugLog.log("receiveObjectChange " + string);
			}

			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
			if (baseVehicle != null) {
				baseVehicle.loadChange(string, byteBuffer);
			} else if (Core.bDebug) {
				DebugLog.log("receiveObjectChange: unknown vehicle id=" + short2);
			}
		} else {
			int int1;
			String string2;
			IsoGridSquare square;
			int int2;
			int int3;
			int int4;
			if (byte1 == 3) {
				int2 = byteBuffer.getInt();
				int3 = byteBuffer.getInt();
				int4 = byteBuffer.getInt();
				int1 = byteBuffer.getInt();
				string2 = GameWindow.ReadString(byteBuffer);
				if (Core.bDebug) {
					DebugLog.log("receiveObjectChange " + string2);
				}

				square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
				if (square == null) {
					instance.delayPacket(int2, int3, int4);
					return;
				}

				for (int int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
					if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().getID() == int1) {
						worldInventoryObject.loadChange(string2, byteBuffer);
						return;
					}
				}

				if (Core.bDebug) {
					DebugLog.log("receiveObjectChange: itemID=" + int1 + " is invalid x,y,z=" + int2 + "," + int3 + "," + int4);
				}
			} else {
				IsoObject object;
				if (byte1 == 4) {
					int2 = byteBuffer.getInt();
					int3 = byteBuffer.getInt();
					int4 = byteBuffer.getInt();
					int1 = byteBuffer.getInt();
					string2 = GameWindow.ReadString(byteBuffer);
					square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
					if (square == null) {
						instance.delayPacket(int2, int3, int4);
						return;
					}

					if (int1 >= 0 && int1 < square.getStaticMovingObjects().size()) {
						object = (IsoObject)square.getStaticMovingObjects().get(int1);
						object.loadChange(string2, byteBuffer);
					} else if (Core.bDebug) {
						DebugLog.log("receiveObjectChange: index=" + int1 + " is invalid x,y,z=" + int2 + "," + int3 + "," + int4);
					}
				} else {
					int2 = byteBuffer.getInt();
					int3 = byteBuffer.getInt();
					int4 = byteBuffer.getInt();
					int1 = byteBuffer.getInt();
					string2 = GameWindow.ReadString(byteBuffer);
					if (Core.bDebug) {
						DebugLog.log("receiveObjectChange " + string2);
					}

					square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
					if (square == null) {
						instance.delayPacket(int2, int3, int4);
						return;
					}

					if (square != null && int1 >= 0 && int1 < square.getObjects().size()) {
						object = (IsoObject)square.getObjects().get(int1);
						object.loadChange(string2, byteBuffer);
					} else if (square != null) {
						if (Core.bDebug) {
							DebugLog.log("receiveObjectChange: index=" + int1 + " is invalid x,y,z=" + int2 + "," + int3 + "," + int4);
						}
					} else if (Core.bDebug) {
						DebugLog.log("receiveObjectChange: sq is null x,y,z=" + int2 + "," + int3 + "," + int4);
					}
				}
			}
		}
	}

	static void receiveKeepAlive(ByteBuffer byteBuffer, short short1) {
		MPDebugInfo.instance.clientPacket(byteBuffer);
	}

	static void receiveSmashWindow(ByteBuffer byteBuffer, short short1) {
		IsoObject object = instance.getIsoObjectRefFromByteBuffer(byteBuffer);
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
	}

	static void receiveRemoveContestedItemsFromInventory(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = byteBuffer.getInt();
			for (int int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
				IsoPlayer player = IsoPlayer.players[int4];
				if (player != null && !player.isDead()) {
					player.getInventory().removeItemWithIDRecurse(int3);
				}
			}
		}
	}

	static void receiveServerQuit(ByteBuffer byteBuffer, short short1) {
		GameWindow.kickReason = "Server shut down safely. Players and map data saved.";
		GameWindow.bServerDisconnected = true;
		ConnectionManager.log("Receive Server Quit", (UdpConnection)null);
	}

	static void receiveHitCharacter(ByteBuffer byteBuffer, short short1) {
		try {
			HitCharacterPacket hitCharacterPacket = HitCharacterPacket.process(byteBuffer);
			if (hitCharacterPacket != null) {
				hitCharacterPacket.parse(byteBuffer, connection);
				if (hitCharacterPacket.isConsistent()) {
					DebugLog.Damage.trace(hitCharacterPacket.getDescription());
					hitCharacterPacket.tryProcess();
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveHitCharacter: failed", LogSeverity.Error);
		}
	}

	public static boolean sendHitCharacter(IsoGameCharacter gameCharacter, IsoMovingObject movingObject, HandWeapon handWeapon, float float1, boolean boolean1, float float2, boolean boolean2, boolean boolean3, boolean boolean4) {
		boolean boolean5 = false;
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);
		try {
			Object object = null;
			if (gameCharacter instanceof IsoZombie) {
				if (movingObject instanceof IsoPlayer) {
					boolean boolean6 = ((IsoPlayer)movingObject).isLocalPlayer();
					boolean boolean7 = !((IsoZombie)gameCharacter).isRemoteZombie();
					if (boolean7 && boolean6) {
						ZombieHitPlayerPacket zombieHitPlayerPacket = new ZombieHitPlayerPacket();
						zombieHitPlayerPacket.set((IsoZombie)gameCharacter, (IsoPlayer)movingObject);
						object = zombieHitPlayerPacket;
					} else {
						DebugLog.Multiplayer.warn(String.format("SendHitCharacter: Wielder or target is not local (wielder=%b, target=%b)", boolean7, boolean6));
					}
				} else {
					DebugLog.Multiplayer.warn(String.format("SendHitCharacter: unknown target type (wielder=%s, target=%s)", gameCharacter.getClass().getName(), movingObject.getClass().getName()));
				}
			} else if (gameCharacter instanceof IsoPlayer) {
				if (movingObject == null) {
					PlayerHitSquarePacket playerHitSquarePacket = new PlayerHitSquarePacket();
					playerHitSquarePacket.set((IsoPlayer)gameCharacter, handWeapon, boolean2);
					object = playerHitSquarePacket;
				} else if (movingObject instanceof IsoPlayer) {
					PlayerHitPlayerPacket playerHitPlayerPacket = new PlayerHitPlayerPacket();
					playerHitPlayerPacket.set((IsoPlayer)gameCharacter, (IsoPlayer)movingObject, handWeapon, float1, boolean1, float2, boolean2, boolean4);
					object = playerHitPlayerPacket;
				} else if (movingObject instanceof IsoZombie) {
					PlayerHitZombiePacket playerHitZombiePacket = new PlayerHitZombiePacket();
					playerHitZombiePacket.set((IsoPlayer)gameCharacter, (IsoZombie)movingObject, handWeapon, float1, boolean1, float2, boolean2, boolean3, boolean4);
					object = playerHitZombiePacket;
				} else if (movingObject instanceof BaseVehicle) {
					PlayerHitVehiclePacket playerHitVehiclePacket = new PlayerHitVehiclePacket();
					playerHitVehiclePacket.set((IsoPlayer)gameCharacter, (BaseVehicle)movingObject, handWeapon, boolean2);
					object = playerHitVehiclePacket;
				} else {
					DebugLog.Multiplayer.warn(String.format("SendHitCharacter: unknown target type (wielder=%s, target=%s)", gameCharacter.getClass().getName(), movingObject.getClass().getName()));
				}
			} else {
				DebugLog.Multiplayer.warn(String.format("SendHitCharacter: unknown wielder type (wielder=%s, target=%s)", gameCharacter.getClass().getName(), movingObject.getClass().getName()));
			}

			if (object != null) {
				((HitCharacterPacket)object).write(byteBufferWriter);
				PacketTypes.PacketType.HitCharacter.send(connection);
				DebugLog.Damage.trace(((HitCharacterPacket)object).getDescription());
				boolean5 = true;
			}
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendHitCharacter: failed", LogSeverity.Error);
		}

		return boolean5;
	}

	public static void sendHitVehicle(IsoPlayer player, IsoGameCharacter gameCharacter, BaseVehicle baseVehicle, float float1, boolean boolean1, int int1, float float2, boolean boolean2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);
		try {
			Object object = null;
			if (gameCharacter instanceof IsoPlayer) {
				VehicleHitPlayerPacket vehicleHitPlayerPacket = new VehicleHitPlayerPacket();
				vehicleHitPlayerPacket.set(player, (IsoPlayer)gameCharacter, baseVehicle, float1, boolean1, int1, float2, boolean2);
				object = vehicleHitPlayerPacket;
			} else if (gameCharacter instanceof IsoZombie) {
				VehicleHitZombiePacket vehicleHitZombiePacket = new VehicleHitZombiePacket();
				vehicleHitZombiePacket.set(player, (IsoZombie)gameCharacter, baseVehicle, float1, boolean1, int1, float2, boolean2);
				object = vehicleHitZombiePacket;
			} else {
				DebugLog.Multiplayer.warn(String.format("SendHitVehicle: unknown target type (wielder=%s, target=%s)", player.getClass().getName(), gameCharacter.getClass().getName()));
			}

			if (object != null) {
				((VehicleHitPacket)object).write(byteBufferWriter);
				PacketTypes.PacketType.HitCharacter.send(connection);
				DebugLog.Damage.trace(((VehicleHitPacket)object).getDescription());
			}
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendHitVehicle: failed", LogSeverity.Error);
		}
	}

	static void receiveZombieDeath(ByteBuffer byteBuffer, short short1) {
		try {
			DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
			deadZombiePacket.parse(byteBuffer, connection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("ReceiveZombieDeath: %s", deadZombiePacket.getDescription());
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveZombieDeath: failed", LogSeverity.Error);
		}
	}

	public static void sendZombieDeath(IsoZombie zombie) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ZombieDeath.doPacket(byteBufferWriter);
		try {
			DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
			deadZombiePacket.set(zombie);
			deadZombiePacket.write(byteBufferWriter);
			PacketTypes.PacketType.ZombieDeath.send(connection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("SendZombieDeath: %s", deadZombiePacket.getDescription());
			}
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendZombieDeath: failed", LogSeverity.Error);
		}
	}

	static void receivePlayerDeath(ByteBuffer byteBuffer, short short1) {
		try {
			DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
			deadPlayerPacket.parse(byteBuffer, connection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("ReceivePlayerDeath: %s", deadPlayerPacket.getDeathDescription());
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceivePlayerDeath: failed", LogSeverity.Error);
		}
	}

	public static void sendPlayerDeath(IsoPlayer player) {
		player.setTransactionID(0);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlayerDeath.doPacket(byteBufferWriter);
		try {
			DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
			deadPlayerPacket.set(player);
			deadPlayerPacket.write(byteBufferWriter);
			PacketTypes.PacketType.PlayerDeath.send(connection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("SendPlayerDeath: %s", deadPlayerPacket.getDeathDescription());
			}
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendPlayerDeath: failed", LogSeverity.Error);
		}
	}

	static void receivePlayerDamage(ByteBuffer byteBuffer, short short1) {
		try {
			short short2 = byteBuffer.getShort();
			float float1 = byteBuffer.getFloat();
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
			if (player != null) {
				player.getBodyDamage().load(byteBuffer, IsoWorld.getWorldVersion());
				player.getStats().setPain(float1);
				if (Core.bDebug) {
					DebugLog.Multiplayer.debugln("ReceivePlayerDamage: \"%s\" %f", player.getUsername(), player.getBodyDamage().getOverallBodyHealth());
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceivePlayerDamage: failed", LogSeverity.Error);
		}
	}

	public static void sendPlayerDamage(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlayerDamage.doPacket(byteBufferWriter);
		try {
			byteBufferWriter.putShort((short)player.getPlayerNum());
			byteBufferWriter.putFloat(player.getStats().getPain());
			player.getBodyDamage().save(byteBufferWriter.bb);
			PacketTypes.PacketType.PlayerDamage.send(connection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("SendPlayerDamage: \"%s\" %f", player.getUsername(), player.getBodyDamage().getOverallBodyHealth());
			}
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendPlayerDamage: failed", LogSeverity.Error);
		}
	}

	static void receiveSyncInjuries(ByteBuffer byteBuffer, short short1) {
		try {
			SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
			syncInjuriesPacket.parse(byteBuffer, connection);
			DebugLog.Damage.trace(syncInjuriesPacket.getDescription());
			syncInjuriesPacket.process();
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceivePlayerInjuries: failed", LogSeverity.Error);
		}
	}

	public static void sendPlayerInjuries(IsoPlayer player) {
		SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
		syncInjuriesPacket.set(player);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncInjuries.doPacket(byteBufferWriter);
		try {
			syncInjuriesPacket.write(byteBufferWriter);
			PacketTypes.PacketType.SyncInjuries.send(connection);
			DebugLog.Damage.trace(syncInjuriesPacket.getDescription());
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendPlayerInjuries: failed", LogSeverity.Error);
		}
	}

	static void receiveRemoveCorpseFromMap(ByteBuffer byteBuffer, short short1) {
		RemoveCorpseFromMap removeCorpseFromMap = new RemoveCorpseFromMap();
		removeCorpseFromMap.parse(byteBuffer, connection);
		if (removeCorpseFromMap.isConsistent()) {
			removeCorpseFromMap.process();
		}
	}

	public static void sendRemoveCorpseFromMap(IsoDeadBody deadBody) {
		RemoveCorpseFromMap removeCorpseFromMap = new RemoveCorpseFromMap();
		removeCorpseFromMap.set(deadBody);
		DebugLog.Death.trace(removeCorpseFromMap.getDescription());
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RemoveCorpseFromMap.doPacket(byteBufferWriter);
		removeCorpseFromMap.write(byteBufferWriter);
		PacketTypes.PacketType.RemoveCorpseFromMap.send(connection);
	}

	public static void sendEvent(IsoPlayer player, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.EventPacket.doPacket(byteBufferWriter);
		try {
			EventPacket eventPacket = new EventPacket();
			if (eventPacket.set(player, string)) {
				eventPacket.write(byteBufferWriter);
				PacketTypes.PacketType.EventPacket.send(connection);
			} else {
				connection.cancelPacket();
			}
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendEvent: failed", LogSeverity.Error);
		}
	}

	static void receiveEventPacket(ByteBuffer byteBuffer, short short1) {
		try {
			EventPacket eventPacket = new EventPacket();
			eventPacket.parse(byteBuffer, connection);
			eventPacket.tryProcess();
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveEvent: failed", LogSeverity.Error);
		}
	}

	public static void sendAction(BaseAction baseAction, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ActionPacket.doPacket(byteBufferWriter);
		try {
			ActionPacket actionPacket = new ActionPacket();
			actionPacket.set(boolean1, baseAction);
			actionPacket.write(byteBufferWriter);
			PacketTypes.PacketType.ActionPacket.send(connection);
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendAction: failed", LogSeverity.Error);
		}
	}

	static void receiveActionPacket(ByteBuffer byteBuffer, short short1) {
		try {
			ActionPacket actionPacket = new ActionPacket();
			actionPacket.parse(byteBuffer, connection);
			actionPacket.process();
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveAction: failed", LogSeverity.Error);
		}
	}

	public static void sendEatBody(IsoZombie zombie, IsoMovingObject movingObject) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.EatBody.doPacket(byteBufferWriter);
		try {
			byteBufferWriter.putShort(zombie.getOnlineID());
			if (movingObject instanceof IsoDeadBody) {
				IsoDeadBody deadBody = (IsoDeadBody)movingObject;
				byteBufferWriter.putByte((byte)1);
				byteBufferWriter.putBoolean(zombie.getVariableBoolean("onknees"));
				byteBufferWriter.putFloat(zombie.getEatSpeed());
				byteBufferWriter.putFloat(zombie.getStateEventDelayTimer());
				byteBufferWriter.putInt(deadBody.getStaticMovingObjectIndex());
				byteBufferWriter.putFloat((float)deadBody.getSquare().getX());
				byteBufferWriter.putFloat((float)deadBody.getSquare().getY());
				byteBufferWriter.putFloat((float)deadBody.getSquare().getZ());
			} else if (movingObject instanceof IsoPlayer) {
				byteBufferWriter.putByte((byte)2);
				byteBufferWriter.putBoolean(zombie.getVariableBoolean("onknees"));
				byteBufferWriter.putFloat(zombie.getEatSpeed());
				byteBufferWriter.putFloat(zombie.getStateEventDelayTimer());
				byteBufferWriter.putShort(((IsoPlayer)movingObject).getOnlineID());
			} else {
				byteBufferWriter.putByte((byte)0);
			}

			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "SendEatBody");
			}

			PacketTypes.PacketType.EatBody.send(connection);
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "SendEatBody: failed", LogSeverity.Error);
			connection.cancelPacket();
		}
	}

	public static void receiveEatBody(ByteBuffer byteBuffer, short short1) {
		try {
			short short2 = byteBuffer.getShort();
			byte byte1 = byteBuffer.get();
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, String.format("ReceiveEatBody: zombie=%d type=%d", short2, byte1));
			}

			IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short2);
			if (zombie == null) {
				DebugLog.Multiplayer.error("ReceiveEatBody: zombie " + short2 + " not found");
				return;
			}

			boolean boolean1;
			float float1;
			float float2;
			if (byte1 == 1) {
				boolean1 = byteBuffer.get() != 0;
				float1 = byteBuffer.getFloat();
				float2 = byteBuffer.getFloat();
				int int1 = byteBuffer.getInt();
				float float3 = byteBuffer.getFloat();
				float float4 = byteBuffer.getFloat();
				float float5 = byteBuffer.getFloat();
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float3, (double)float4, (double)float5);
				if (square == null) {
					DebugLog.Multiplayer.error("ReceiveEatBody: incorrect square");
					return;
				}

				if (int1 >= 0 && int1 < square.getStaticMovingObjects().size()) {
					IsoDeadBody deadBody = (IsoDeadBody)square.getStaticMovingObjects().get(int1);
					if (deadBody != null) {
						zombie.setTarget((IsoMovingObject)null);
						zombie.setEatBodyTarget(deadBody, true, float1);
						zombie.setVariable("onknees", boolean1);
						zombie.setStateEventDelayTimer(float2);
					} else {
						DebugLog.Multiplayer.error("ReceiveEatBody: no corpse with index " + int1 + " on square");
					}
				} else {
					DebugLog.Multiplayer.error("ReceiveEatBody: no corpse on square");
				}
			} else if (byte1 == 2) {
				boolean1 = byteBuffer.get() != 0;
				float1 = byteBuffer.getFloat();
				float2 = byteBuffer.getFloat();
				short short3 = byteBuffer.getShort();
				IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short3);
				if (player == null) {
					DebugLog.Multiplayer.error("ReceiveEatBody: player " + short3 + " not found");
					return;
				}

				zombie.setTarget((IsoMovingObject)null);
				zombie.setEatBodyTarget(player, true, float1);
				zombie.setVariable("onknees", boolean1);
				zombie.setStateEventDelayTimer(float2);
			} else {
				zombie.setEatBodyTarget((IsoMovingObject)null, false);
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveEatBody: failed", LogSeverity.Error);
		}
	}

	public static void sendThump(IsoGameCharacter gameCharacter, Thumpable thumpable) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Thump.doPacket(byteBufferWriter);
		try {
			short short1 = gameCharacter.getOnlineID();
			String string = gameCharacter.getVariableString("ThumpType");
			byteBufferWriter.putShort(short1);
			byteBufferWriter.putByte((byte)NetworkVariables.ThumpType.fromString(string).ordinal());
			if (thumpable instanceof IsoObject) {
				IsoObject object = (IsoObject)thumpable;
				byteBufferWriter.putInt(object.getObjectIndex());
				byteBufferWriter.putFloat((float)object.getSquare().getX());
				byteBufferWriter.putFloat((float)object.getSquare().getY());
				byteBufferWriter.putFloat((float)object.getSquare().getZ());
			} else {
				byteBufferWriter.putInt(-1);
			}

			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, String.format("SendThump: zombie=%d type=%s target=%s", short1, string, thumpable == null ? "null" : thumpable.getClass().getSimpleName()));
			}

			PacketTypes.PacketType.Thump.send(connection);
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "SendThump: failed", LogSeverity.Error);
			connection.cancelPacket();
		}
	}

	public static void receiveSyncRadioData(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		VoiceManagerData voiceManagerData = VoiceManagerData.get(short2);
		synchronized (voiceManagerData.radioData) {
			voiceManagerData.isCanHearAll = byteBuffer.get() == 1;
			short short3 = (short)byteBuffer.getInt();
			voiceManagerData.radioData.clear();
			for (int int1 = 0; int1 < short3 / 4; ++int1) {
				int int2 = byteBuffer.getInt();
				int int3 = byteBuffer.getInt();
				int int4 = byteBuffer.getInt();
				int int5 = byteBuffer.getInt();
				voiceManagerData.radioData.add(new VoiceManagerData.RadioData(int2, (float)int3, (float)int4, (float)int5));
			}
		}
	}

	public static void receiveThump(ByteBuffer byteBuffer, short short1) {
		try {
			short short2 = byteBuffer.getShort();
			String string = NetworkVariables.ThumpType.fromByte(byteBuffer.get()).toString();
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, String.format("ReceiveThump: zombie=%d type=%s", short2, string));
			}

			IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short2);
			if (zombie == null) {
				DebugLog.Multiplayer.error("ReceiveThump: zombie " + short2 + " not found");
				return;
			}

			zombie.setVariable("ThumpType", string);
			int int1 = byteBuffer.getInt();
			if (int1 == -1) {
				zombie.setThumpTarget((Thumpable)null);
				return;
			}

			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
			if (square == null) {
				DebugLog.Multiplayer.error("ReceiveThump: incorrect square");
				return;
			}

			IsoObject object = (IsoObject)square.getObjects().get(int1);
			if (object instanceof Thumpable) {
				zombie.setThumpTarget(object);
			} else {
				DebugLog.Multiplayer.error("ReceiveThump: no thumpable with index " + int1 + " on square");
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveThump: failed", LogSeverity.Error);
		}
	}

	public void sendWorldSound(WorldSoundManager.WorldSound worldSound) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(worldSound.x);
		byteBufferWriter.putInt(worldSound.y);
		byteBufferWriter.putInt(worldSound.z);
		byteBufferWriter.putInt(worldSound.radius);
		byteBufferWriter.putInt(worldSound.volume);
		byteBufferWriter.putByte((byte)(worldSound.stresshumans ? 1 : 0));
		byteBufferWriter.putFloat(worldSound.zombieIgnoreDist);
		byteBufferWriter.putFloat(worldSound.stressMod);
		byteBufferWriter.putByte((byte)(worldSound.sourceIsZombie ? 1 : 0));
		PacketTypes.PacketType.WorldSound.send(connection);
	}

	static void receiveRemoveItemFromSquare(ByteBuffer byteBuffer, short short1) {
		if (IsoWorld.instance.CurrentCell != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				instance.delayPacket(int1, int2, int3);
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

	static void receiveLoadPlayerProfile(ByteBuffer byteBuffer, short short1) {
		ClientPlayerDB.getInstance().clientLoadNetworkCharacter(byteBuffer, connection);
	}

	public void sendLoginQueueRequest2() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
		PacketTypes.PacketType.LoginQueueRequest2.send(connection);
	}

	public void sendLoginQueueDone2(long long1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.LoginQueueDone2.doPacket(byteBufferWriter);
		byteBufferWriter.putLong(long1);
		PacketTypes.PacketType.LoginQueueDone2.send(connection);
	}

	static void receiveRemoveInventoryItemFromContainer(ByteBuffer byteBuffer, short short1) {
		if (IsoWorld.instance.CurrentCell != null) {
			ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
			short short2 = byteBuffer.getShort();
			int int1 = byteBufferReader.getInt();
			int int2 = byteBufferReader.getInt();
			int int3 = byteBufferReader.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null) {
				byte byte1;
				int int4;
				int int5;
				int int6;
				if (short2 == 0) {
					byte1 = byteBufferReader.getByte();
					int4 = byteBuffer.getInt();
					if (byte1 < 0 || byte1 >= square.getStaticMovingObjects().size()) {
						DebugLog.log("ERROR: removeItemFromContainer: invalid corpse index");
						return;
					}

					IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
					if (object != null && object.getContainer() != null) {
						for (int5 = 0; int5 < int4; ++int5) {
							int6 = byteBufferReader.getInt();
							object.getContainer().removeItemWithID(int6);
							object.getContainer().setExplored(true);
						}
					}
				} else if (short2 == 1) {
					int int7 = byteBufferReader.getInt();
					int4 = byteBuffer.getInt();
					ItemContainer itemContainer = null;
					for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
						IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
						if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == int7) {
							itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
							break;
						}
					}

					if (itemContainer == null) {
						DebugLog.log("ERROR removeItemFromContainer can\'t find world item with id=" + int7);
						return;
					}

					for (int5 = 0; int5 < int4; ++int5) {
						int6 = byteBufferReader.getInt();
						itemContainer.removeItemWithID(int6);
						itemContainer.setExplored(true);
					}
				} else {
					byte byte2;
					int int8;
					int int9;
					if (short2 == 2) {
						byte1 = byteBufferReader.getByte();
						byte2 = byteBufferReader.getByte();
						int8 = byteBuffer.getInt();
						if (byte1 < 0 || byte1 >= square.getObjects().size()) {
							DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
							return;
						}

						IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
						ItemContainer itemContainer2 = object2 != null ? object2.getContainerByIndex(byte2) : null;
						if (itemContainer2 != null) {
							for (int int10 = 0; int10 < int8; ++int10) {
								int9 = byteBufferReader.getInt();
								itemContainer2.removeItemWithID(int9);
								itemContainer2.setExplored(true);
							}
						}
					} else if (short2 == 3) {
						short short3 = byteBufferReader.getShort();
						byte2 = byteBufferReader.getByte();
						int8 = byteBuffer.getInt();
						BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
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
							for (int9 = 0; int9 < int8; ++int9) {
								int int11 = byteBufferReader.getInt();
								itemContainer3.removeItemWithID(int11);
								itemContainer3.setExplored(true);
							}

							vehiclePart.setContainerContentAmount(itemContainer3.getCapacityWeight());
						}
					} else {
						DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
					}
				}
			} else {
				instance.delayPacket(int1, int2, int3);
			}
		}
	}

	static void receiveAddInventoryItemToContainer(ByteBuffer byteBuffer, short short1) {
		if (IsoWorld.instance.CurrentCell != null) {
			ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
			short short2 = byteBuffer.getShort();
			int int1 = byteBufferReader.getInt();
			int int2 = byteBufferReader.getInt();
			int int3 = byteBufferReader.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square == null) {
				instance.delayPacket(int1, int2, int3);
			} else {
				ItemContainer itemContainer = null;
				VehiclePart vehiclePart = null;
				byte byte1;
				int int4;
				if (short2 == 0) {
					byte1 = byteBufferReader.getByte();
					if (byte1 < 0 || byte1 >= square.getStaticMovingObjects().size()) {
						DebugLog.log("ERROR: sendItemsToContainer: invalid corpse index");
						return;
					}

					IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
					if (object != null && object.getContainer() != null) {
						itemContainer = object.getContainer();
					}
				} else if (short2 == 1) {
					int int5 = byteBufferReader.getInt();
					for (int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
						IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
						if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == int5) {
							itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
							break;
						}
					}

					if (itemContainer == null) {
						DebugLog.log("ERROR: sendItemsToContainer: can\'t find world item with id=" + int5);
						return;
					}
				} else {
					byte byte2;
					if (short2 == 2) {
						byte1 = byteBufferReader.getByte();
						byte2 = byteBufferReader.getByte();
						if (byte1 < 0 || byte1 >= square.getObjects().size()) {
							DebugLog.log("ERROR: sendItemsToContainer: invalid object index");
							return;
						}

						IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
						itemContainer = object2 != null ? object2.getContainerByIndex(byte2) : null;
					} else if (short2 == 3) {
						short short3 = byteBufferReader.getShort();
						byte2 = byteBufferReader.getByte();
						BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
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
						ArrayList arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 194, (ArrayList)null, (ArrayList)null);
						for (int4 = 0; int4 < arrayList.size(); ++int4) {
							InventoryItem inventoryItem = (InventoryItem)arrayList.get(int4);
							if (inventoryItem != null) {
								if (itemContainer.containsID(inventoryItem.id)) {
									if (short2 != 0) {
										System.out.println("Error: Dupe item ID. id = " + inventoryItem.id);
									}
								} else {
									itemContainer.addItem(inventoryItem);
									itemContainer.setExplored(true);
									if (itemContainer.getParent() instanceof IsoMannequin) {
										((IsoMannequin)itemContainer.getParent()).wearItem(inventoryItem, (IsoGameCharacter)null);
									}
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

	static void receiveItemStats(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		byte byte1;
		int int4;
		byte byte2;
		int int5;
		ItemContainer itemContainer;
		InventoryItem inventoryItem;
		switch (short2) {
		case 0: 
			byte2 = byteBuffer.get();
			int5 = byteBuffer.getInt();
			if (square != null && byte2 >= 0 && byte2 < square.getStaticMovingObjects().size()) {
				IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(byte2);
				itemContainer = movingObject.getContainer();
				if (itemContainer != null) {
					inventoryItem = itemContainer.getItemWithID(int5);
					if (inventoryItem != null) {
						instance.readItemStats(byteBuffer, inventoryItem);
					}
				}
			}

			break;
		
		case 1: 
			int int6 = byteBuffer.getInt();
			if (square != null) {
				for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
					if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().id == int6) {
						instance.readItemStats(byteBuffer, worldInventoryObject.getItem());
						break;
					}

					if (worldInventoryObject.getItem() instanceof InventoryContainer) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						inventoryItem = itemContainer.getItemWithID(int6);
						if (inventoryItem != null) {
							instance.readItemStats(byteBuffer, inventoryItem);
							break;
						}
					}
				}
			}

			break;
		
		case 2: 
			byte2 = byteBuffer.get();
			byte1 = byteBuffer.get();
			int4 = byteBuffer.getInt();
			if (square != null && byte2 >= 0 && byte2 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte2);
				ItemContainer itemContainer2 = object.getContainerByIndex(byte1);
				if (itemContainer2 != null) {
					InventoryItem inventoryItem2 = itemContainer2.getItemWithID(int4);
					if (inventoryItem2 != null) {
						instance.readItemStats(byteBuffer, inventoryItem2);
					}
				}
			}

			break;
		
		case 3: 
			short short3 = byteBuffer.getShort();
			byte1 = byteBuffer.get();
			int4 = byteBuffer.getInt();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(byte1);
				if (vehiclePart != null) {
					ItemContainer itemContainer3 = vehiclePart.getItemContainer();
					if (itemContainer3 != null) {
						InventoryItem inventoryItem3 = itemContainer3.getItemWithID(int4);
						if (inventoryItem3 != null) {
							instance.readItemStats(byteBuffer, inventoryItem3);
						}
					}
				}
			}

		
		}
	}

	public static boolean canSeePlayerStats() {
		return connection.accessLevel != 1;
	}

	public static boolean canModifyPlayerStats() {
		return (connection.accessLevel & 56) != 0;
	}

	public void sendPersonalColor(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ChangeTextColor.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)player.getPlayerNum());
		byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().r);
		byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().g);
		byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().b);
		PacketTypes.PacketType.ChangeTextColor.send(connection);
	}

	public void sendChangedPlayerStats(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ChangePlayerStats.doPacket(byteBufferWriter);
		player.createPlayerStats(byteBufferWriter, username);
		PacketTypes.PacketType.ChangePlayerStats.send(connection);
	}

	static void receiveChangePlayerStats(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
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
			player.getHumanVisual().save(byteBufferWriter.bb);
			ItemVisuals itemVisuals = new ItemVisuals();
			player.getItemVisuals(itemVisuals);
			itemVisuals.save(byteBufferWriter.bb);
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

		InventoryItem inventoryItem = player.getPrimaryHandItem();
		if (inventoryItem == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				inventoryItem.saveWithSize(byteBufferWriter.bb, false);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}

		InventoryItem inventoryItem2 = player.getSecondaryHandItem();
		if (inventoryItem2 == null) {
			byteBufferWriter.putByte((byte)0);
		} else if (inventoryItem2 == inventoryItem) {
			byteBufferWriter.putByte((byte)2);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				inventoryItem2.saveWithSize(byteBufferWriter.bb, false);
			} catch (IOException ioException3) {
				ioException3.printStackTrace();
			}
		}

		byteBufferWriter.putInt(player.getAttachedItems().size());
		for (int int1 = 0; int1 < player.getAttachedItems().size(); ++int1) {
			byteBufferWriter.putUTF(player.getAttachedItems().get(int1).getLocation());
			byteBufferWriter.putUTF(player.getAttachedItems().get(int1).getItem().getFullType());
		}

		byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Sneak));
		connection.username = player.username;
	}

	public void sendPlayerConnect(IsoPlayer player) {
		player.setOnlineID((short)-1);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlayerConnect.doPacket(byteBufferWriter);
		this.writePlayerConnectData(byteBufferWriter, player);
		PacketTypes.PacketType.PlayerConnect.send(connection);
		allChatMuted = player.isAllChatMuted();
		sendPerks(player);
		player.updateEquippedRadioFreq();
		this.bPlayerConnectSent = true;
	}

	@Deprecated
	public void sendPlayerSave(IsoPlayer player) {
		if (connection != null) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.PlayerSave.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putShort(player.OnlineID);
			byteBufferWriter.putFloat(player.x);
			byteBufferWriter.putFloat(player.y);
			byteBufferWriter.putFloat(player.z);
			PacketTypes.PacketType.PlayerSave.send(connection);
		}
	}

	public void sendPlayer2(IsoPlayer player) {
		if (bClient && player.isLocalPlayer() && player.networkAI.isNeedToUpdate()) {
			if (PlayerPacket.l_send.playerPacket.set(player)) {
				ByteBufferWriter byteBufferWriter = connection.startPacket();
				PacketTypes.PacketType packetType;
				if (this.PlayerUpdateReliableLimit.Check()) {
					packetType = PacketTypes.PacketType.PlayerUpdateReliable;
				} else {
					packetType = PacketTypes.PacketType.PlayerUpdate;
				}

				packetType.doPacket(byteBufferWriter);
				PlayerPacket.l_send.playerPacket.write(byteBufferWriter);
				packetType.send(connection);
			}
		}
	}

	public void sendPlayer(IsoPlayer player) {
		player.networkAI.needToUpdate();
	}

	public void sendSteamProfileName(long long1) {
		if (SteamUtils.isSteamModeEnabled()) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.getSteamID() == long1) {
					ByteBufferWriter byteBufferWriter = connection.startPacket();
					PacketTypes.PacketType.SteamGeneric.doPacket(byteBufferWriter);
					byteBufferWriter.putShort((short)0);
					byteBufferWriter.putByte((byte)player.getPlayerNum());
					byteBufferWriter.putUTF(SteamFriends.GetFriendPersonaName(long1));
					PacketTypes.PacketType.SteamGeneric.send(connection);
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

	public static void sendPlayerExtraInfo(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ExtraInfo.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)player.getPlayerNum());
		byteBufferWriter.putByte((byte)(player.isGodMod() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isGhostMode() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isInvisible() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isNoClip() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isShowAdminTag() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isCanHearAll() ? 1 : 0));
		PacketTypes.PacketType.ExtraInfo.send(connection);
	}

	static void receiveExtraInfo(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		boolean boolean3 = byteBuffer.get() == 1;
		boolean boolean4 = byteBuffer.get() == 1;
		boolean boolean5 = byteBuffer.get() == 1;
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			player.accessLevel = string;
			player.setGodMod(boolean1);
			player.setInvisible(boolean3);
			player.setGhostMode(boolean2);
			player.setNoClip(boolean4);
			player.setShowAdminTag(boolean5);
			if (!player.bRemote) {
				connection.accessLevel = PlayerType.fromString(string);
			}
		}
	}

	static void receiveConnectionDetails(ByteBuffer byteBuffer, short short1) {
		Calendar calendar = Calendar.getInstance();
		PrintStream printStream = System.out;
		long long1 = calendar.getTimeInMillis();
		printStream.println("LOGGED INTO : " + (long1 - startAuth.getTimeInMillis()) + " millisecond");
		ConnectToServerState connectToServerState = new ConnectToServerState(byteBuffer);
		connectToServerState.enter();
		MainScreenState.getInstance().setConnectToServerState(connectToServerState);
	}

	public void setResetID(int int1) {
		this.ResetID = 0;
		this.loadResetID();
		if (this.ResetID != int1) {
			ArrayList arrayList = new ArrayList();
			arrayList.add("map_symbols.bin");
			arrayList.add("map_visited.bin");
			arrayList.add("recorded_media.bin");
			String string;
			int int2;
			File file;
			File file2;
			for (int2 = 0; int2 < arrayList.size(); ++int2) {
				try {
					file = ZomboidFileSystem.instance.getFileInCurrentSave((String)arrayList.get(int2));
					if (file.exists()) {
						string = ZomboidFileSystem.instance.getCacheDir();
						file2 = new File(string + File.separator + (String)arrayList.get(int2));
						if (file2.exists()) {
							file2.delete();
						}

						file.renameTo(file2);
					}
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}

			DebugLog.log("server was reset, deleting " + Core.GameMode + File.separator + Core.GameSaveWorld);
			LuaManager.GlobalObject.deleteSave(Core.GameMode + File.separator + Core.GameSaveWorld);
			LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
			for (int2 = 0; int2 < arrayList.size(); ++int2) {
				try {
					file = ZomboidFileSystem.instance.getFileInCurrentSave((String)arrayList.get(int2));
					string = ZomboidFileSystem.instance.getCacheDir();
					file2 = new File(string + File.separator + (String)arrayList.get(int2));
					if (file2.exists()) {
						file2.renameTo(file);
					}
				} catch (Exception exception2) {
					ExceptionLogger.logException(exception2);
				}
			}
		}

		this.ResetID = int1;
		this.saveResetID();
	}

	public void loadResetID() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
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
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
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

	static void receivePlayerConnect(ByteBuffer byteBuffer, short short1) {
		boolean boolean1 = false;
		short short2 = byteBuffer.getShort();
		byte byte1 = -1;
		if (short2 == -1) {
			boolean1 = true;
			byte1 = byteBuffer.get();
			short2 = byteBuffer.getShort();
			try {
				GameTime.getInstance().load(byteBuffer);
				GameTime.getInstance().ServerTimeOfDay = GameTime.getInstance().getTimeOfDay();
				GameTime.getInstance().ServerNewDays = 0;
				GameTime.getInstance().setMinutesPerDay((float)SandboxOptions.instance.getDayLengthMinutes());
				LuaEventManager.triggerEvent("OnGameTimeLoaded");
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else if (IDToPlayerMap.containsKey(short2)) {
			return;
		}

		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		IsoPlayer player = null;
		String string;
		int int1;
		if (boolean1) {
			string = GameWindow.ReadString(byteBuffer);
			for (int1 = 0; int1 < IsoWorld.instance.AddCoopPlayers.size(); ++int1) {
				((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(int1)).receivePlayerConnect(byte1);
			}

			player = IsoPlayer.players[byte1];
			player.username = string;
			player.setOnlineID(short2);
		} else {
			string = GameWindow.ReadString(byteBuffer);
			SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
			try {
				survivorDesc.load(byteBuffer, 194, (IsoGameCharacter)null);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}

			try {
				player = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)float1, (int)float2, (int)float3);
				player.bRemote = true;
				player.lastRemoteUpdate = System.currentTimeMillis();
				player.getHumanVisual().load(byteBuffer, 194);
				player.getItemVisuals().load(byteBuffer, 194);
				player.username = string;
				player.updateUsername();
				player.setSceneCulled(false);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			player.setX(float1);
			player.setY(float2);
			player.setZ(float3);
			player.networkAI.targetX = float1;
			player.networkAI.targetY = float2;
			player.networkAI.targetZ = (int)float3;
		}

		player.setOnlineID(short2);
		if (SteamUtils.isSteamModeEnabled()) {
			player.setSteamID(byteBuffer.getLong());
		}

		player.setGodMod(byteBuffer.get() == 1);
		player.setGhostMode(byteBuffer.get() == 1);
		player.getSafety().load(byteBuffer, IsoWorld.getWorldVersion());
		byte byte2 = byteBuffer.get();
		if (boolean1) {
			connection.accessLevel = byte2;
			ZNetSessionState zNetSessionState = connection.getP2PSessionState();
			DebugLog.General.warn("ReceivePlayerConnect: guid=%d mtu=%d %s", connection.getConnectedGUID(), connection.getMTUSize(), zNetSessionState == null ? "" : zNetSessionState.getDescription());
		}

		player.accessLevel = PlayerType.toString(byte2);
		player.setInvisible(byteBuffer.get() == 1);
		if (!boolean1) {
			try {
				player.getXp().load(byteBuffer, 194);
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
		int1 = byteBuffer.getInt();
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			String string2 = GameWindow.ReadString(byteBuffer);
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
			if (inventoryItem != null) {
				player.setAttachedItem(string2, inventoryItem);
			}
		}

		int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		player.remoteSneakLvl = int2;
		player.remoteStrLvl = int3;
		player.remoteFitLvl = int4;
		if (Core.bDebug) {
			DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + short2 + (boolean1 ? " (local)" : " (remote)"));
		}

		if (!boolean1) {
			rememberPlayerPosition(player, float1, float2);
		}

		IDToPlayerMap.put(short2, player);
		instance.idMapDirty = true;
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

		if (boolean1) {
			String string3 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
			if (string3 != null && !string3.equals("")) {
				ChatManager.getInstance().showServerChatMessage(string3);
			}

			VoiceManager.getInstance().UpdateChannelsRoaming(connection);
		}
	}

	static void receiveScoreboardUpdate(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		instance.connectedPlayers = new ArrayList();
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		for (int int2 = 0; int2 < int1; ++int2) {
			String string = GameWindow.ReadString(byteBuffer);
			String string2 = GameWindow.ReadString(byteBuffer);
			arrayList.add(string);
			arrayList2.add(string2);
			instance.connectedPlayers.add(instance.getPlayerFromUsername(string));
			if (SteamUtils.isSteamModeEnabled()) {
				String string3 = SteamUtils.convertSteamIDToString(byteBuffer.getLong());
				arrayList3.add(string3);
			}
		}

		LuaEventManager.triggerEvent("OnScoreboardUpdate", arrayList, arrayList2, arrayList3);
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
			String string;
			if (boolean1) {
				string = GameWindow.ReadString(byteBuffer);
				player = IsoPlayer.players[byte1];
				player.username = string;
				player.setOnlineID(short1);
			} else {
				string = GameWindow.ReadString(byteBuffer);
				SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
				try {
					survivorDesc.load(byteBuffer, 194, (IsoGameCharacter)null);
				} catch (IOException ioException2) {
					ioException2.printStackTrace();
				}

				try {
					player = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)float1, (int)float2, (int)float3);
					player.getHumanVisual().load(byteBuffer, 194);
					player.getItemVisuals().load(byteBuffer, 194);
					player.username = string;
					player.updateUsername();
					player.setSceneCulled(false);
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				player.bRemote = true;
				player.setX(float1);
				player.setY(float2);
				player.setZ(float3);
			}

			player.setOnlineID(short1);
			if (Core.bDebug) {
				DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + short1 + (boolean1 ? " (me)" : " (not me)"));
			}

			int int1 = byteBuffer.getInt();
			for (int int2 = 0; int2 < int1; ++int2) {
				ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
			}

			player.setGodMod(byteBuffer.get() == 1);
			player.setGhostMode(byteBuffer.get() == 1);
			player.getSafety().load(byteBuffer, IsoWorld.getWorldVersion());
			player.accessLevel = GameWindow.ReadString(byteBuffer);
			player.setInvisible(byteBuffer.get() == 1);
			IDToPlayerMap.put(short1, player);
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
		if (player != null && object != null && player.isLocalPlayer()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.WeaponHit.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(object.square.x);
			byteBufferWriter.putInt(object.square.y);
			byteBufferWriter.putInt(object.square.z);
			byteBufferWriter.putByte((byte)object.getObjectIndex());
			byteBufferWriter.putShort((short)player.getPlayerNum());
			byteBufferWriter.putUTF(handWeapon != null ? handWeapon.getFullType() : "");
			PacketTypes.PacketType.WeaponHit.send(connection);
		}
	}

	public static void receiveSyncCustomLightSettings(ByteBuffer byteBuffer, short short1) {
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

	static void receiveSyncIsoObjectReq(ByteBuffer byteBuffer, short short1) {
		if (SystemDisabler.doObjectStateSyncEnable) {
			short short2 = byteBuffer.getShort();
			for (int int1 = 0; int1 < short2; ++int1) {
				GameClient gameClient = instance;
				receiveSyncIsoObject(byteBuffer, short1);
			}
		}
	}

	static void receiveSyncWorldObjectsReq(ByteBuffer byteBuffer, short short1) {
		DebugLog.log("SyncWorldObjectsReq client : ");
		short short2 = byteBuffer.getShort();
		for (int int1 = 0; int1 < short2; ++int1) {
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			instance.worldObjectsSyncReq.receiveSyncIsoChunk(int2, int3);
			short short3 = byteBuffer.getShort();
			DebugLog.log("[" + int2 + "," + int3 + "]:" + short3 + " ");
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2 * 10, int3 * 10, 0);
			if (square == null) {
				return;
			}

			IsoChunk chunk = square.getChunk();
			++chunk.ObjectsSyncCount;
			chunk.recalcHashCodeObjects();
		}

		DebugLog.log(";\n");
	}

	static void receiveSyncObjects(ByteBuffer byteBuffer, short short1) {
		if (SystemDisabler.doWorldSyncEnable) {
			short short2 = byteBuffer.getShort();
			if (short2 == 2) {
				instance.worldObjectsSyncReq.receiveGridSquareHashes(byteBuffer);
			}

			if (short2 == 4) {
				instance.worldObjectsSyncReq.receiveGridSquareObjectHashes(byteBuffer);
			}

			if (short2 == 6) {
				instance.worldObjectsSyncReq.receiveObject(byteBuffer);
			}
		}
	}

	static void receiveSyncIsoObject(ByteBuffer byteBuffer, short short1) {
		if (DebugOptions.instance.Network.Client.SyncIsoObject.getValue()) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			byte byte1 = byteBuffer.get();
			byte byte2 = byteBuffer.get();
			byte byte3 = byteBuffer.get();
			if (byte2 != 2) {
				instance.objectSyncReq.receiveIsoSync(int1, int2, int3, byte1);
			}

			if (byte2 == 1) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
				if (square == null) {
					return;
				}

				if (byte1 >= 0 && byte1 < square.getObjects().size()) {
					((IsoObject)square.getObjects().get(byte1)).syncIsoObject(true, byte3, (UdpConnection)null, byteBuffer);
				} else {
					DebugLog.Network.warn("SyncIsoObject: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
				}
			}
		}
	}

	static void receiveSyncAlarmClock(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		int int1;
		int int2;
		if (short2 == AlarmClock.PacketPlayer) {
			short short3 = byteBuffer.getShort();
			int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			int2 = boolean1 ? 0 : byteBuffer.getInt();
			int int3 = boolean1 ? 0 : byteBuffer.getInt();
			byte byte1 = boolean1 ? 0 : byteBuffer.get();
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short3);
			if (player != null) {
				for (int int4 = 0; int4 < player.getInventory().getItems().size(); ++int4) {
					InventoryItem inventoryItem = (InventoryItem)player.getInventory().getItems().get(int4);
					if (inventoryItem instanceof AlarmClock && inventoryItem.getID() == int1) {
						if (boolean1) {
							((AlarmClock)inventoryItem).stopRinging();
						} else {
							((AlarmClock)inventoryItem).setAlarmSet(byte1 == 1);
							((AlarmClock)inventoryItem).setHour(int2);
							((AlarmClock)inventoryItem).setMinute(int3);
						}

						break;
					}
				}
			}
		} else if (short2 == AlarmClock.PacketWorld) {
			int int5 = byteBuffer.getInt();
			int1 = byteBuffer.getInt();
			int int6 = byteBuffer.getInt();
			int2 = byteBuffer.getInt();
			boolean boolean2 = byteBuffer.get() == 1;
			int int7 = boolean2 ? 0 : byteBuffer.getInt();
			int int8 = boolean2 ? 0 : byteBuffer.getInt();
			byte byte2 = boolean2 ? 0 : byteBuffer.get();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int5, int1, int6);
			if (square != null) {
				for (int int9 = 0; int9 < square.getWorldObjects().size(); ++int9) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int9);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof AlarmClock && worldInventoryObject.getItem().id == int2) {
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

	static void receiveAddItemToMap(ByteBuffer byteBuffer, short short1) {
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

					IsoGenerator.updateGenerator(object.getSquare());
				}

				if (object instanceof IsoWorldInventoryObject || object.getContainer() != null) {
					LuaEventManager.triggerEvent("OnContainerUpdate", object);
				}
			}
		}
	}

	static void skipPacket(ByteBuffer byteBuffer, short short1) {
	}

	static void receiveAccessDenied(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String[] stringArray = string.split("##");
		String string2 = stringArray.length > 0 ? Translator.getText("UI_OnConnectFailed_" + stringArray[0], stringArray.length > 1 ? stringArray[1] : null, stringArray.length > 2 ? stringArray[2] : null, stringArray.length > 3 ? stringArray[3] : null) : null;
		LuaEventManager.triggerEvent("OnConnectFailed", string2);
		DebugLog.Multiplayer.warn("ReceiveAccessDenied: " + string2);
	}

	static void receivePlayerTimeout(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		receivePlayerTimeout(short2);
	}

	public static void receivePlayerTimeout(short short1) {
		positions.remove(short1);
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short1);
		if (player != null) {
			DebugLog.log("Received timeout for player " + player.username + " id " + player.OnlineID);
			NetworkZombieSimulator.getInstance().clearTargetAuth(player);
			if (player.getVehicle() != null) {
				int int1 = player.getVehicle().getSeat(player);
				if (int1 != -1) {
					player.getVehicle().clearPassenger(int1);
				}

				VehicleManager.instance.sendRequestGetPosition(player.getVehicle().VehicleID, PacketTypes.PacketType.Vehicles);
			}

			player.removeFromWorld();
			player.removeFromSquare();
			IDToPlayerMap.remove(player.OnlineID);
			instance.idMapDirty = true;
			LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
		}
	}

	public void disconnect() {
		this.resetDisconnectTimer();
		this.bConnected = false;
		if (IsoPlayer.getInstance() != null) {
			IsoPlayer.getInstance().setOnlineID((short)-1);
		}
	}

	public void resetDisconnectTimer() {
		this.disconnectTime = System.currentTimeMillis();
	}

	public String getReconnectCountdownTimer() {
		return String.valueOf((int)Math.ceil((double)((10000L - System.currentTimeMillis() + this.disconnectTime) / 1000L)));
	}

	public boolean canConnect() {
		return System.currentTimeMillis() - this.disconnectTime > 10000L;
	}

	public void addIncoming(short short1, ByteBuffer byteBuffer) {
		if (connection != null) {
			if (short1 == PacketTypes.PacketType.SentChunk.getId()) {
				WorldStreamer.instance.receiveChunkPart(byteBuffer);
			} else if (short1 == PacketTypes.PacketType.NotRequiredInZip.getId()) {
				WorldStreamer.instance.receiveNotRequired(byteBuffer);
			} else if (short1 == PacketTypes.PacketType.LoadPlayerProfile.getId()) {
				ClientPlayerDB.getInstance().clientLoadNetworkCharacter(byteBuffer, connection);
			} else {
				ZomboidNetData zomboidNetData = null;
				if (byteBuffer.remaining() > 2048) {
					zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.remaining());
				} else {
					zomboidNetData = ZomboidNetDataPool.instance.get();
				}

				zomboidNetData.read(short1, byteBuffer, connection);
				zomboidNetData.time = System.currentTimeMillis();
				MainLoopNetDataQ.add(zomboidNetData);
			}
		}
	}

	public void doDisconnect(String string) {
		if (connection != null) {
			connection.forceDisconnect(string);
			this.bConnected = false;
			connection = null;
			bClient = false;
		} else {
			instance.Shutdown();
		}
	}

	public void removeZombieFromCache(IsoZombie zombie) {
		if (IDToZombieMap.containsKey(zombie.OnlineID)) {
			IDToZombieMap.remove(zombie.OnlineID);
		}
	}

	static void receiveEquip(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != IsoPlayer.getInstance()) {
			InventoryItem inventoryItem = null;
			if (byte2 == 1) {
				try {
					inventoryItem = InventoryItem.loadItem(byteBuffer, 194);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			if (player != null && byte1 == 1 && byte2 == 2) {
				inventoryItem = player.getPrimaryHandItem();
			}

			if (player != null) {
				if (byte1 == 0) {
					player.setPrimaryHandItem(inventoryItem);
				} else {
					player.setSecondaryHandItem(inventoryItem);
				}

				try {
					if (inventoryItem != null) {
						inventoryItem.setContainer(player.getInventory());
						if (byte2 == 1 && byteBuffer.get() == 1) {
							inventoryItem.getVisual().load(byteBuffer, 194);
						}
					}
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}
	}

	public void equip(IsoPlayer player, int int1) {
		InventoryItem inventoryItem = null;
		if (int1 == 0) {
			inventoryItem = player.getPrimaryHandItem();
		} else {
			inventoryItem = player.getSecondaryHandItem();
		}

		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Equip.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putByte((byte)int1);
		if (inventoryItem == null) {
			byteBufferWriter.putByte((byte)0);
		} else if (int1 == 1 && player.getPrimaryHandItem() == player.getSecondaryHandItem()) {
			byteBufferWriter.putByte((byte)2);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				inventoryItem.saveWithSize(byteBufferWriter.bb, false);
				if (inventoryItem.getVisual() != null) {
					byteBufferWriter.bb.put((byte)1);
					inventoryItem.getVisual().save(byteBufferWriter.bb);
				} else {
					byteBufferWriter.bb.put((byte)0);
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		PacketTypes.PacketType.Equip.send(connection);
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
		String string7 = ip;
		Core.GameSaveWorld = string7 + "_" + port + "_" + ServerWorldDatabase.encrypt(string);
		this.convertGameSaveWorldDirectory(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + ip + "_" + port + "_" + string, ZomboidFileSystem.instance.getCurrentSaveDir());
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
		PacketTypes.PacketType.ScoreboardUpdate.doPacket(byteBufferWriter);
		PacketTypes.PacketType.ScoreboardUpdate.send(connection);
	}

	public void sendWorldSound(Object object, int int1, int int2, int int3, int int4, int int5, boolean boolean1, float float1, float float2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.putInt(int4);
		byteBufferWriter.putInt(int5);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putByte((byte)(object instanceof IsoZombie ? 1 : 0));
		PacketTypes.PacketType.WorldSound.send(connection);
	}

	static void receivePlayWorldSound(ByteBuffer byteBuffer, short short1) {
		PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
		playWorldSoundPacket.parse(byteBuffer, connection);
		playWorldSoundPacket.process();
		DebugLog.log(DebugType.Sound, playWorldSoundPacket.getDescription());
	}

	static void receivePlaySound(ByteBuffer byteBuffer, short short1) {
		PlaySoundPacket playSoundPacket = new PlaySoundPacket();
		playSoundPacket.parse(byteBuffer, connection);
		playSoundPacket.process();
		DebugLog.log(DebugType.Sound, playSoundPacket.getDescription());
	}

	static void receiveStopSound(ByteBuffer byteBuffer, short short1) {
		StopSoundPacket stopSoundPacket = new StopSoundPacket();
		stopSoundPacket.parse(byteBuffer, connection);
		stopSoundPacket.process();
		DebugLog.log(DebugType.Sound, stopSoundPacket.getDescription());
	}

	static void receiveWorldSound(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		boolean boolean2 = byteBuffer.get() == 1;
		WorldSoundManager.instance.addSound((Object)null, int1, int2, int3, int4, int5, boolean1, float1, float2, boolean2, false, true);
	}

	private void receiveDataZombieDescriptors(ByteBuffer byteBuffer) throws IOException {
		DebugLog.log(DebugType.NetworkFileDebug, "received zombie descriptors");
		PersistentOutfits.instance.load(byteBuffer);
	}

	private void receivePlayerZombieDescriptors() throws IOException {
		File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("reanimated.bin"));
		FileInputStream fileInputStream = new FileInputStream(file);
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			try {
				synchronized (SliceY.SliceBufferLock) {
					ByteBuffer byteBuffer = SliceY.SliceBuffer;
					byteBuffer.clear();
					int int1 = bufferedInputStream.read(byteBuffer.array());
					byteBuffer.limit(int1);
					short short1 = byteBuffer.getShort();
					DebugLog.log(DebugType.NetworkFileDebug, "received " + short1 + " player-zombie descriptors");
					for (short short2 = 0; short2 < short1; ++short2) {
						SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
						descriptor.load(byteBuffer, 194);
						SharedDescriptors.registerPlayerZombieDescriptor(descriptor);
					}
				}
			} catch (Throwable throwable) {
				try {
					bufferedInputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			bufferedInputStream.close();
		} catch (Throwable throwable3) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable4) {
				throwable3.addSuppressed(throwable4);
			}

			throw throwable3;
		}

		fileInputStream.close();
	}

	static void receiveAddAmbient(ByteBuffer byteBuffer, short short1) {
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
		PacketTypes.PacketType.ClientCommand.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)(player != null ? player.PlayerIndex : -1));
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						Object object = kahluaTableIterator.getKey();
						DebugLog.log("ERROR: sendClientCommand: can\'t save key,value=" + object + "," + kahluaTableIterator.getValue());
					}
				}

				TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		PacketTypes.PacketType.ClientCommand.send(connection);
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

	public void sendClothing(IsoPlayer player, String string, InventoryItem inventoryItem) {
		if (player != null && player.OnlineID != -1) {
			SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
			syncClothingPacket.set(player, string, inventoryItem);
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.SyncClothing.doPacket(byteBufferWriter);
			syncClothingPacket.write(byteBufferWriter);
			PacketTypes.PacketType.SyncClothing.send(connection);
		}
	}

	static void receiveSyncClothing(ByteBuffer byteBuffer, short short1) {
		SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
		syncClothingPacket.parse(byteBuffer, connection);
	}

	public void sendAttachedItem(IsoPlayer player, String string, InventoryItem inventoryItem) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlayerAttachedItem.doPacket(byteBufferWriter);
		try {
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			if (inventoryItem != null) {
				byteBufferWriter.putByte((byte)1);
				GameWindow.WriteString(byteBufferWriter.bb, inventoryItem.getFullType());
			} else {
				byteBufferWriter.putByte((byte)0);
			}

			PacketTypes.PacketType.PlayerAttachedItem.send(connection);
		} catch (Throwable throwable) {
			connection.cancelPacket();
			ExceptionLogger.logException(throwable);
		}
	}

	static void receivePlayerAttachedItem(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null && !player.isLocalPlayer()) {
			String string = GameWindow.ReadString(byteBuffer);
			boolean boolean1 = byteBuffer.get() == 1;
			if (boolean1) {
				String string2 = GameWindow.ReadString(byteBuffer);
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string2);
				if (inventoryItem == null) {
					return;
				}

				player.setAttachedItem(string, inventoryItem);
			} else {
				player.setAttachedItem(string, (InventoryItem)null);
			}
		}
	}

	public void sendVisual(IsoPlayer player) {
		if (player != null && player.OnlineID != -1) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.HumanVisual.doPacket(byteBufferWriter);
			try {
				byteBufferWriter.putShort(player.OnlineID);
				player.getHumanVisual().save(byteBufferWriter.bb);
				PacketTypes.PacketType.HumanVisual.send(connection);
			} catch (Throwable throwable) {
				connection.cancelPacket();
				ExceptionLogger.logException(throwable);
			}
		}
	}

	static void receiveHumanVisual(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null && !player.isLocalPlayer()) {
			try {
				player.getHumanVisual().load(byteBuffer, 194);
				player.resetModelNextFrame();
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}
	}

	static void receiveBloodSplatter(ByteBuffer byteBuffer, short short1) {
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
			instance.delayPacket((int)float1, (int)float2, (int)float3);
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

	static void receiveZombieSound(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		IsoZombie.ZombieSound zombieSound = IsoZombie.ZombieSound.fromIndex(byte1);
		DebugLog.log(DebugType.Sound, "sound: received " + byte1 + " for zombie " + short2);
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short2);
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
				zombie.playHurtSound();
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

	static void receiveSlowFactor(ByteBuffer byteBuffer, short short1) {
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
		PacketTypes.PacketType.SendCustomColor.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(object.getSquare().getX());
		byteBufferWriter.putInt(object.getSquare().getY());
		byteBufferWriter.putInt(object.getSquare().getZ());
		byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
		byteBufferWriter.putFloat(object.getCustomColor().r);
		byteBufferWriter.putFloat(object.getCustomColor().g);
		byteBufferWriter.putFloat(object.getCustomColor().b);
		byteBufferWriter.putFloat(object.getCustomColor().a);
		PacketTypes.PacketType.SendCustomColor.send(connection);
	}

	public void sendBandage(int int1, int int2, boolean boolean1, float float1, boolean boolean2, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Bandage.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putBoolean(boolean1);
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putBoolean(boolean2);
		GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
		PacketTypes.PacketType.Bandage.send(connection);
	}

	public void sendStitch(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem, boolean boolean1) {
		Stitch stitch = new Stitch();
		stitch.set(gameCharacter, gameCharacter2, bodyPart, inventoryItem, boolean1);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Stitch.doPacket(byteBufferWriter);
		stitch.write(byteBufferWriter);
		PacketTypes.PacketType.Stitch.send(connection);
	}

	@Deprecated
	public void sendWoundInfection(int int1, int int2, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.WoundInfection.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putBoolean(boolean1);
		PacketTypes.PacketType.WoundInfection.send(connection);
	}

	public void sendDisinfect(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem) {
		Disinfect disinfect = new Disinfect();
		disinfect.set(gameCharacter, gameCharacter2, bodyPart, inventoryItem);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Disinfect.doPacket(byteBufferWriter);
		disinfect.write(byteBufferWriter);
		PacketTypes.PacketType.Disinfect.send(connection);
	}

	public void sendSplint(int int1, int int2, boolean boolean1, float float1, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Splint.doPacket(byteBufferWriter);
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

		PacketTypes.PacketType.Splint.send(connection);
	}

	public void sendAdditionalPain(int int1, int int2, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.AdditionalPain.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putFloat(float1);
		PacketTypes.PacketType.AdditionalPain.send(connection);
	}

	public void sendRemoveGlass(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, boolean boolean1) {
		RemoveGlass removeGlass = new RemoveGlass();
		removeGlass.set(gameCharacter, gameCharacter2, bodyPart, boolean1);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RemoveGlass.doPacket(byteBufferWriter);
		removeGlass.write(byteBufferWriter);
		PacketTypes.PacketType.RemoveGlass.send(connection);
	}

	public void sendRemoveBullet(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart) {
		RemoveBullet removeBullet = new RemoveBullet();
		removeBullet.set(gameCharacter, gameCharacter2, bodyPart);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RemoveBullet.doPacket(byteBufferWriter);
		removeBullet.write(byteBufferWriter);
		PacketTypes.PacketType.RemoveBullet.send(connection);
	}

	public void sendCleanBurn(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BodyPart bodyPart, InventoryItem inventoryItem) {
		CleanBurn cleanBurn = new CleanBurn();
		cleanBurn.set(gameCharacter, gameCharacter2, bodyPart, inventoryItem);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.CleanBurn.doPacket(byteBufferWriter);
		cleanBurn.write(byteBufferWriter);
		PacketTypes.PacketType.CleanBurn.send(connection);
	}

	public void eatFood(IsoPlayer player, Food food, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.EatFood.doPacket(byteBufferWriter);
		try {
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putFloat(float1);
			food.saveWithSize(byteBufferWriter.bb, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		PacketTypes.PacketType.EatFood.send(connection);
	}

	public void drink(IsoPlayer player, float float1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Drink.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putFloat(float1);
		PacketTypes.PacketType.Drink.send(connection);
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
			PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
			if (object2 instanceof IsoDeadBody) {
				byteBufferWriter.putShort((short)0);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putByte((byte)((IsoObject)object2).getStaticMovingObjectIndex());
				byteBufferWriter.putInt(1);
				byteBufferWriter.putInt(inventoryItem.id);
			} else if (object2 instanceof IsoWorldInventoryObject) {
				byteBufferWriter.putShort((short)1);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putInt(((IsoWorldInventoryObject)object2).getItem().id);
				byteBufferWriter.putInt(1);
				byteBufferWriter.putInt(inventoryItem.id);
			} else if (object2 instanceof BaseVehicle) {
				byteBufferWriter.putShort((short)3);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putShort(((BaseVehicle)object2).VehicleID);
				byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
				byteBufferWriter.putInt(1);
				byteBufferWriter.putInt(inventoryItem.id);
			} else {
				byteBufferWriter.putShort((short)2);
				byteBufferWriter.putInt(((IsoObject)object2).square.getX());
				byteBufferWriter.putInt(((IsoObject)object2).square.getY());
				byteBufferWriter.putInt(((IsoObject)object2).square.getZ());
				byteBufferWriter.putByte((byte)((IsoObject)object2).square.getObjects().indexOf(object2));
				byteBufferWriter.putByte((byte)((IsoObject)object2).getContainerIndex(itemContainer));
				byteBufferWriter.putInt(1);
				byteBufferWriter.putInt(inventoryItem.id);
			}

			PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection);
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
		int int1;
		Object object2;
		if (!SystemDisabler.doWorldSyncEnable && !this.itemsToSendRemove.isEmpty() && boolean2) {
			iterator = this.itemsToSendRemove.entrySet().iterator();
			label178: while (true) {
				do {
					do {
						if (!iterator.hasNext()) {
							this.itemsToSendRemove.clear();
							break label178;
						}

						entry = (Entry)iterator.next();
						itemContainer = (ItemContainer)entry.getKey();
						arrayList = (ArrayList)entry.getValue();
						object = itemContainer.getParent();
						if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
							object = itemContainer.getContainingItem().getWorldItem();
						}
					}			 while (object == null);
				}		 while (((IsoObject)object).square == null);

				try {
					byteBufferWriter = connection.startPacket();
					PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
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
						byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().id);
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
					for (int1 = 0; int1 < arrayList.size(); ++int1) {
						InventoryItem inventoryItem = (InventoryItem)arrayList.get(int1);
						byteBufferWriter.putInt(inventoryItem.id);
					}

					PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(connection);
				} catch (Exception exception) {
					DebugLog.log("sendAddedRemovedItems: itemsToSendRemove container:" + itemContainer + "." + object + " items:" + arrayList);
					if (arrayList != null) {
						for (int1 = 0; int1 < arrayList.size(); ++int1) {
							if (arrayList.get(int1) == null) {
								DebugLog.log("item:null");
							} else {
								object2 = arrayList.get(int1);
								DebugLog.log("item:" + ((InventoryItem)object2).getName());
							}
						}

						DebugLog.log("itemSize:" + arrayList.size());
					}

					exception.printStackTrace();
					connection.cancelPacket();
				}
			}
		}

		if (!this.itemsToSend.isEmpty() && boolean2) {
			iterator = this.itemsToSend.entrySet().iterator();
			while (true) {
				do {
					do {
						if (!iterator.hasNext()) {
							this.itemsToSend.clear();
							return;
						}

						entry = (Entry)iterator.next();
						itemContainer = (ItemContainer)entry.getKey();
						arrayList = (ArrayList)entry.getValue();
						object = itemContainer.getParent();
						if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
							object = itemContainer.getContainingItem().getWorldItem();
						}
					}			 while (object == null);
				}		 while (((IsoObject)object).square == null);

				try {
					byteBufferWriter = connection.startPacket();
					PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
					if (object instanceof IsoDeadBody) {
						byteBufferWriter.putShort((short)0);
						byteBufferWriter.putInt(((IsoObject)object).square.getX());
						byteBufferWriter.putInt(((IsoObject)object).square.getY());
						byteBufferWriter.putInt(((IsoObject)object).square.getZ());
						byteBufferWriter.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
						try {
							CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
						} catch (Exception exception2) {
							exception2.printStackTrace();
						}
					} else if (object instanceof IsoWorldInventoryObject) {
						byteBufferWriter.putShort((short)1);
						byteBufferWriter.putInt(((IsoObject)object).square.getX());
						byteBufferWriter.putInt(((IsoObject)object).square.getY());
						byteBufferWriter.putInt(((IsoObject)object).square.getZ());
						byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().id);
						try {
							CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
						} catch (Exception exception3) {
							exception3.printStackTrace();
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
						} catch (Exception exception4) {
							exception4.printStackTrace();
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
						} catch (Exception exception5) {
							exception5.printStackTrace();
						}
					}

					PacketTypes.PacketType.AddInventoryItemToContainer.send(connection);
				} catch (Exception exception6) {
					DebugLog.log("sendAddedRemovedItems: itemsToSend container:" + itemContainer + "." + object + " items:" + arrayList);
					if (arrayList != null) {
						for (int1 = 0; int1 < arrayList.size(); ++int1) {
							if (arrayList.get(int1) == null) {
								DebugLog.log("item:null");
							} else {
								object2 = arrayList.get(int1);
								DebugLog.log("item:" + ((InventoryItem)object2).getName());
							}
						}

						DebugLog.log("itemSize:" + arrayList.size());
					}

					exception6.printStackTrace();
					connection.cancelPacket();
				}
			}
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

	public void sendReplaceOnCooked(InventoryItem inventoryItem) {
		IsoObject object = inventoryItem.getOutermostContainer().getParent();
		if (object != null) {
			this.checkAddedRemovedItems(object);
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.ReplaceOnCooked.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(object.square.getX());
			byteBufferWriter.putInt(object.square.getY());
			byteBufferWriter.putInt(object.square.getZ());
			byteBufferWriter.putByte((byte)object.square.getObjects().indexOf(object));
			byteBufferWriter.putByte((byte)object.getContainerIndex(inventoryItem.getContainer()));
			byteBufferWriter.putInt(inventoryItem.getID());
			PacketTypes.PacketType.ReplaceOnCooked.send(connection);
		}
	}

	private void writeItemStats(ByteBufferWriter byteBufferWriter, InventoryItem inventoryItem) {
		byteBufferWriter.putInt(inventoryItem.id);
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
				PacketTypes.PacketType.ItemStats.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)1);
				byteBufferWriter.putInt(worldInventoryObject.square.getX());
				byteBufferWriter.putInt(worldInventoryObject.square.getY());
				byteBufferWriter.putInt(worldInventoryObject.square.getZ());
				this.writeItemStats(byteBufferWriter, inventoryItem);
				PacketTypes.PacketType.ItemStats.send(connection);
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
				PacketTypes.PacketType.ItemStats.doPacket(byteBufferWriter2);
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
				PacketTypes.PacketType.ItemStats.send(connection);
			}
		}
	}

	public void PlayWorldSound(String string, int int1, int int2, byte byte1) {
		PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
		playWorldSoundPacket.set(string, int1, int2, byte1);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlayWorldSound.doPacket(byteBufferWriter);
		playWorldSoundPacket.write(byteBufferWriter);
		PacketTypes.PacketType.PlayWorldSound.send(connection);
	}

	public void PlaySound(String string, boolean boolean1, IsoMovingObject movingObject) {
		PlaySoundPacket playSoundPacket = new PlaySoundPacket();
		playSoundPacket.set(string, boolean1, movingObject);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlaySound.doPacket(byteBufferWriter);
		playSoundPacket.write(byteBufferWriter);
		PacketTypes.PacketType.PlaySound.send(connection);
	}

	public void StopSound(IsoMovingObject movingObject, String string, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.StopSound.doPacket(byteBufferWriter);
		StopSoundPacket stopSoundPacket = new StopSoundPacket();
		stopSoundPacket.set(movingObject, string, boolean1);
		stopSoundPacket.write(byteBufferWriter);
		PacketTypes.PacketType.StopSound.send(connection);
	}

	public void startLocalServer() throws Exception {
		bClient = true;
		ip = "127.0.0.1";
		Thread thread = new Thread(ThreadGroups.Workers, ()->{
    String string = System.getProperty("file.separator");
    String thread = System.getProperty("java.class.path");
    String string2 = System.getProperty("java.home");
    String string3 = string2 + string + "bin" + string + "java";
    ProcessBuilder processBuilder = new ProcessBuilder(new String[]{string3, "-Xms2048m", "-Xmx2048m", "-Djava.library.path=../natives/", "-cp", "lwjgl.jar;lwjgl_util.jar;sqlitejdbc-v056.jar;../bin/", "zombie.network.GameServer"});
    processBuilder.redirectErrorStream(true);
    Process process = null;
    try {
        process = processBuilder.start();
    } catch (IOException ioException) {
        ioException.printStackTrace();
    }
    InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
    boolean boolean1 = false;
    try {
        while (!inputStreamReader.ready()) {
            int int1;
            try {
                while ((int1 = inputStreamReader.read()) != -1) {
                    System.out.print((char)int1);
                }
            } catch (IOException ioException2) {
                ioException2.printStackTrace();
            }
            try {
                inputStreamReader.close();
            } catch (IOException ioException3) {
                ioException3.printStackTrace();
            }
        }
    } catch (IOException ioException4) {
        ioException4.printStackTrace();
    }
});
		thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
		thread.start();
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
		PacketTypes.PacketType.RegisterZone.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(zone.name);
		byteBufferWriter.putUTF(zone.type);
		byteBufferWriter.putInt(zone.x);
		byteBufferWriter.putInt(zone.y);
		byteBufferWriter.putInt(zone.z);
		byteBufferWriter.putInt(zone.w);
		byteBufferWriter.putInt(zone.h);
		byteBufferWriter.putInt(zone.getLastActionTimestamp());
		byteBufferWriter.putBoolean(boolean1);
		PacketTypes.PacketType.RegisterZone.send(connection);
	}

	static void receiveHelicopter(ByteBuffer byteBuffer, short short1) {
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		if (IsoWorld.instance != null && IsoWorld.instance.helicopter != null) {
			IsoWorld.instance.helicopter.clientSync(float1, float2, boolean1);
		}
	}

	static void receiveVehicles(ByteBuffer byteBuffer, short short1) {
		VehicleManager.instance.clientPacket(byteBuffer);
	}

	static void receiveVehicleAuthorization(ByteBuffer byteBuffer, short short1) {
		VehicleAuthorizationPacket vehicleAuthorizationPacket = new VehicleAuthorizationPacket();
		vehicleAuthorizationPacket.parse(byteBuffer, connection);
		if (vehicleAuthorizationPacket.isConsistent()) {
			vehicleAuthorizationPacket.process();
		}
	}

	static void receiveTimeSync(ByteBuffer byteBuffer, short short1) {
		GameTime.receiveTimeSync(byteBuffer, connection);
	}

	public static void sendSafehouse(SafeHouse safeHouse, boolean boolean1) {
		SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
		syncSafehousePacket.set(safeHouse, boolean1);
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncSafehouse.doPacket(byteBufferWriter);
		syncSafehousePacket.write(byteBufferWriter);
		PacketTypes.PacketType.SyncSafehouse.send(connection);
	}

	static void receiveSyncSafehouse(ByteBuffer byteBuffer, short short1) {
		SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
		syncSafehousePacket.parse(byteBuffer, connection);
		syncSafehousePacket.process();
		LuaEventManager.triggerEvent("OnSafehousesChanged");
	}

	public static void sendKickOutOfSafehouse(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.KickOutOfSafehouse.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.getOnlineID());
		PacketTypes.PacketType.KickOutOfSafehouse.send(connection);
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
			PacketTypes.PacketType.SledgehammerDestroy.doPacket(byteBufferWriter);
			IsoGridSquare square = object.getSquare();
			byteBufferWriter.putInt(square.getX());
			byteBufferWriter.putInt(square.getY());
			byteBufferWriter.putInt(square.getZ());
			byteBufferWriter.putInt(square.getObjects().indexOf(object));
			PacketTypes.PacketType.SledgehammerDestroy.send(connection);
			square.RemoveTileObject(object);
		}
	}

	public static void sendTeleport(IsoPlayer player, float float1, float float2, float float3) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
		GameWindow.WriteString(byteBufferWriter.bb, player.getUsername());
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putFloat(float3);
		PacketTypes.PacketType.Teleport.send(connection);
	}

	public static void sendStopFire(IsoGridSquare square) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putInt(square.getX());
		byteBufferWriter.putInt(square.getY());
		byteBufferWriter.putInt(square.getZ());
		PacketTypes.PacketType.StopFire.send(connection);
	}

	public static void sendStopFire(IsoGameCharacter gameCharacter) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
		if (gameCharacter instanceof IsoPlayer) {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putShort(gameCharacter.getOnlineID());
		}

		if (gameCharacter instanceof IsoZombie) {
			byteBufferWriter.putByte((byte)2);
			byteBufferWriter.putShort(((IsoZombie)gameCharacter).OnlineID);
		}

		PacketTypes.PacketType.StopFire.send(connection);
	}

	public void sendCataplasm(int int1, int int2, float float1, float float2, float float3) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Cataplasm.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putFloat(float1);
		byteBufferWriter.putFloat(float2);
		byteBufferWriter.putFloat(float3);
		PacketTypes.PacketType.Cataplasm.send(connection);
	}

	static void receiveBodyDamageUpdate(ByteBuffer byteBuffer, short short1) {
		BodyDamageSync.instance.clientPacket(byteBuffer);
	}

	public static void receiveRadioDeviceDataState(ByteBuffer byteBuffer, short short1) {
		byte byte1 = byteBuffer.get();
		if (byte1 == 1) {
			int int1 = byteBuffer.getInt();
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
		} else {
			short short2;
			if (byte1 == 0) {
				short2 = byteBuffer.getShort();
				IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
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
				short2 = byteBuffer.getShort();
				short short3 = byteBuffer.getShort();
				BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
				if (baseVehicle != null) {
					VehiclePart vehiclePart = baseVehicle.getPartByIndex(short3);
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
	}

	public static void sendRadioServerDataRequest() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RadioServerData.doPacket(byteBufferWriter);
		PacketTypes.PacketType.RadioServerData.send(connection);
	}

	public static void receiveRadioServerData(ByteBuffer byteBuffer, short short1) {
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
		ZomboidRadio.POST_RADIO_SILENCE = byteBuffer.get() == 1;
	}

	public static void receiveRadioPostSilence(ByteBuffer byteBuffer, short short1) {
		ZomboidRadio.POST_RADIO_SILENCE = byteBuffer.get() == 1;
	}

	public static void sendIsoWaveSignal(int int1, int int2, int int3, String string, String string2, String string3, float float1, float float2, float float3, int int4, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.WaveSignal.doPacket(byteBufferWriter);
		try {
			WaveSignal waveSignal = new WaveSignal();
			waveSignal.set(int1, int2, int3, string, string2, string3, float1, float2, float3, int4, boolean1);
			waveSignal.write(byteBufferWriter);
			PacketTypes.PacketType.WaveSignal.send(connection);
		} catch (Exception exception) {
			connection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendIsoWaveSignal: failed", LogSeverity.Error);
		}
	}

	public static void receiveWaveSignal(ByteBuffer byteBuffer, short short1) {
		if (ChatManager.getInstance().isWorking()) {
			WaveSignal waveSignal = new WaveSignal();
			waveSignal.parse(byteBuffer, connection);
			waveSignal.process(connection);
		}
	}

	public static void sendPlayerListensChannel(int int1, boolean boolean1, boolean boolean2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.PlayerListensChannel.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
		PacketTypes.PacketType.PlayerListensChannel.send(connection);
	}

	static void receiveSyncFurnace(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			instance.delayPacket(int1, int2, int3);
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
		PacketTypes.PacketType.SyncFurnace.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(bSFurnace.getSquare().getX());
		byteBufferWriter.putInt(bSFurnace.getSquare().getY());
		byteBufferWriter.putInt(bSFurnace.getSquare().getZ());
		byteBufferWriter.putByte((byte)(bSFurnace.isFireStarted() ? 1 : 0));
		byteBufferWriter.putFloat(bSFurnace.getFuelAmount());
		byteBufferWriter.putFloat(bSFurnace.getFuelDecrease());
		byteBufferWriter.putFloat(bSFurnace.getHeat());
		GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sSprite);
		GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sLitSprite);
		PacketTypes.PacketType.SyncFurnace.send(connection);
	}

	public static void sendCompost(IsoCompost compost) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncCompost.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(compost.getSquare().getX());
		byteBufferWriter.putInt(compost.getSquare().getY());
		byteBufferWriter.putInt(compost.getSquare().getZ());
		byteBufferWriter.putFloat(compost.getCompost());
		PacketTypes.PacketType.SyncCompost.send(connection);
	}

	static void receiveSyncCompost(ByteBuffer byteBuffer, short short1) {
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
			PacketTypes.PacketType.Userlog.doPacket(byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			PacketTypes.PacketType.Userlog.send(connection);
		}
	}

	public void addUserlog(String string, String string2, String string3) {
		if (canSeePlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.AddUserlog.doPacket(byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			GameWindow.WriteString(byteBufferWriter.bb, string2);
			GameWindow.WriteString(byteBufferWriter.bb, string3);
			PacketTypes.PacketType.AddUserlog.send(connection);
		}
	}

	public void removeUserlog(String string, String string2, String string3) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.RemoveUserlog.doPacket(byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			GameWindow.WriteString(byteBufferWriter.bb, string2);
			GameWindow.WriteString(byteBufferWriter.bb, string3);
			PacketTypes.PacketType.RemoveUserlog.send(connection);
		}
	}

	public void addWarningPoint(String string, String string2, int int1) {
		if (canModifyPlayerStats()) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.AddWarningPoint.doPacket(byteBufferWriter);
			GameWindow.WriteString(byteBufferWriter.bb, string);
			GameWindow.WriteString(byteBufferWriter.bb, string2);
			byteBufferWriter.putInt(int1);
			PacketTypes.PacketType.AddWarningPoint.send(connection);
		}
	}

	static void receiveMessageForAdmin(ByteBuffer byteBuffer, short short1) {
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
		PacketTypes.PacketType.WakeUpPlayer.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)player.getPlayerNum());
		PacketTypes.PacketType.WakeUpPlayer.send(connection);
	}

	static void receiveWakeUpPlayer(ByteBuffer byteBuffer, short short1) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		if (player != null) {
			SleepingEvent.instance.wakeUp(player, true);
		}
	}

	public void getDBSchema() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.GetDBSchema.doPacket(byteBufferWriter);
		PacketTypes.PacketType.GetDBSchema.send(connection);
	}

	static void receiveGetDBSchema(ByteBuffer byteBuffer, short short1) {
		if ((connection.accessLevel & 3) <= 0) {
			instance.dbSchema = LuaManager.platform.newTable();
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

				instance.dbSchema.rawset(string, kahluaTable);
			}

			LuaEventManager.triggerEvent("OnGetDBSchema", instance.dbSchema);
		}
	}

	public void getTableResult(String string, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.GetTableResult.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.GetTableResult.send(connection);
	}

	static void receiveGetTableResult(ByteBuffer byteBuffer, short short1) {
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
		if (connection.accessLevel == 32) {
			ByteBufferWriter byteBufferWriter = connection.startPacket();
			PacketTypes.PacketType.ExecuteQuery.doPacket(byteBufferWriter);
			try {
				byteBufferWriter.putUTF(string);
				kahluaTable.save(byteBufferWriter.bb);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			} finally {
				PacketTypes.PacketType.ExecuteQuery.send(connection);
			}
		}
	}

	public ArrayList getConnectedPlayers() {
		return this.connectedPlayers;
	}

	public static void sendNonPvpZone(NonPvpZone nonPvpZone, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncNonPvpZone.doPacket(byteBufferWriter);
		nonPvpZone.save(byteBufferWriter.bb);
		byteBufferWriter.putBoolean(boolean1);
		PacketTypes.PacketType.SyncNonPvpZone.send(connection);
	}

	public static void sendFaction(Faction faction, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncFaction.doPacket(byteBufferWriter);
		faction.writeToBuffer(byteBufferWriter, boolean1);
		PacketTypes.PacketType.SyncFaction.send(connection);
	}

	public static void sendFactionInvite(Faction faction, IsoPlayer player, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SendFactionInvite.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(faction.getName());
		byteBufferWriter.putUTF(player.getUsername());
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.SendFactionInvite.send(connection);
	}

	static void receiveSendFactionInvite(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		LuaEventManager.triggerEvent("ReceiveFactionInvite", string, string2);
	}

	public static void acceptFactionInvite(Faction faction, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.AcceptedFactionInvite.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(faction.getName());
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.AcceptedFactionInvite.send(connection);
	}

	static void receiveAcceptedFactionInvite(ByteBuffer byteBuffer, short short1) {
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
		PacketTypes.PacketType.AddTicket.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		byteBufferWriter.putInt(int1);
		PacketTypes.PacketType.AddTicket.send(connection);
	}

	public static void getTickets(String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ViewTickets.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.ViewTickets.send(connection);
	}

	static void receiveViewTickets(ByteBuffer byteBuffer, short short1) {
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

	static void receiveChecksum(ByteBuffer byteBuffer, short short1) {
		NetChecksum.comparer.clientPacket(byteBuffer);
	}

	public static void removeTicket(int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RemoveTicket.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(int1);
		PacketTypes.PacketType.RemoveTicket.send(connection);
	}

	public static boolean sendItemListNet(IsoPlayer player, ArrayList arrayList, IsoPlayer player2, String string, String string2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SendItemListNet.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)(player2 != null ? 1 : 0));
		if (player2 != null) {
			byteBufferWriter.putShort(player2.getOnlineID());
		}

		byteBufferWriter.putByte((byte)(player != null ? 1 : 0));
		if (player != null) {
			byteBufferWriter.putShort(player.getOnlineID());
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

		PacketTypes.PacketType.SendItemListNet.send(connection);
		return true;
	}

	static void receiveSendItemListNet(ByteBuffer byteBuffer, short short1) {
		IsoPlayer player = null;
		if (byteBuffer.get() != 1) {
			player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		}

		IsoPlayer player2 = null;
		if (byteBuffer.get() == 1) {
			player2 = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		}

		String string = GameWindow.ReadString(byteBuffer);
		String string2 = null;
		if (byteBuffer.get() == 1) {
			string2 = GameWindow.ReadString(byteBuffer);
		}

		short short2 = byteBuffer.getShort();
		ArrayList arrayList = new ArrayList(short2);
		try {
			for (int int1 = 0; int1 < short2; ++int1) {
				InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, 194);
				if (inventoryItem != null) {
					arrayList.add(inventoryItem);
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		LuaEventManager.triggerEvent("OnReceiveItemListNet", player2, arrayList, player, string, string2);
	}

	public void requestTrading(IsoPlayer player, IsoPlayer player2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putShort(player2.OnlineID);
		byteBufferWriter.putByte((byte)0);
		PacketTypes.PacketType.RequestTrading.send(connection);
	}

	public void acceptTrading(IsoPlayer player, IsoPlayer player2, boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player2.OnlineID);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 2));
		PacketTypes.PacketType.RequestTrading.send(connection);
	}

	static void receiveRequestTrading(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
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
		PacketTypes.PacketType.TradingUIAddItem.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putShort(player2.OnlineID);
		try {
			inventoryItem.saveWithSize(byteBufferWriter.bb, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		PacketTypes.PacketType.TradingUIAddItem.send(connection);
	}

	static void receiveTradingUIAddItem(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = InventoryItem.loadItem(byteBuffer, 194);
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}

		if (inventoryItem != null) {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
			if (player != null) {
				LuaEventManager.triggerEvent("TradingUIAddItem", player, inventoryItem);
			}
		}
	}

	public void tradingUISendRemoveItem(IsoPlayer player, IsoPlayer player2, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.TradingUIRemoveItem.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putShort(player2.OnlineID);
		byteBufferWriter.putInt(int1);
		PacketTypes.PacketType.TradingUIRemoveItem.send(connection);
	}

	static void receiveTradingUIRemoveItem(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			LuaEventManager.triggerEvent("TradingUIRemoveItem", player, int1);
		}
	}

	public void tradingUISendUpdateState(IsoPlayer player, IsoPlayer player2, int int1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.TradingUIUpdateState.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putShort(player2.OnlineID);
		byteBufferWriter.putInt(int1);
		PacketTypes.PacketType.TradingUIUpdateState.send(connection);
	}

	static void receiveTradingUIUpdateState(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			LuaEventManager.triggerEvent("TradingUIUpdateState", player, int1);
		}
	}

	public static void sendBuildingStashToDo(String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ReadAnnotedMap.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.ReadAnnotedMap.send(connection);
	}

	public static void setServerStatisticEnable(boolean boolean1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.StatisticRequest.doPacket(byteBufferWriter);
		byteBufferWriter.putBoolean(boolean1);
		PacketTypes.PacketType.StatisticRequest.send(connection);
		MPStatistic.clientStatisticEnable = boolean1;
	}

	public static boolean getServerStatisticEnable() {
		return MPStatistic.clientStatisticEnable;
	}

	public static void sendRequestInventory(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.RequestInventory.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(IsoPlayer.getInstance().getOnlineID());
		byteBufferWriter.putShort(player.getOnlineID());
		PacketTypes.PacketType.RequestInventory.send(connection);
	}

	private int sendInventoryPutItems(ByteBufferWriter byteBufferWriter, LinkedHashMap linkedHashMap, long long1) {
		int int1 = linkedHashMap.size();
		Iterator iterator = linkedHashMap.keySet().iterator();
		while (iterator.hasNext()) {
			InventoryItem inventoryItem = (InventoryItem)linkedHashMap.get(iterator.next());
			byteBufferWriter.putUTF(inventoryItem.getModule());
			byteBufferWriter.putUTF(inventoryItem.getType());
			byteBufferWriter.putLong((long)inventoryItem.getID());
			byteBufferWriter.putLong(long1);
			byteBufferWriter.putBoolean(IsoPlayer.getInstance().isEquipped(inventoryItem));
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
				this.sendInventoryPutItems(byteBufferWriter, linkedHashMap2, (long)inventoryItem.getID());
			}
		}

		return int1;
	}

	static void receiveRequestInventory(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SendInventory.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(short2);
		int int1 = byteBufferWriter.bb.position();
		byteBufferWriter.putInt(0);
		LinkedHashMap linkedHashMap = IsoPlayer.getInstance().getInventory().getItems4Admin();
		int int2 = instance.sendInventoryPutItems(byteBufferWriter, linkedHashMap, -1L);
		int int3 = byteBufferWriter.bb.position();
		byteBufferWriter.bb.position(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.bb.position(int3);
		PacketTypes.PacketType.SendInventory.send(connection);
	}

	static void receiveSendInventory(ByteBuffer byteBuffer, short short1) {
		int int1 = byteBuffer.getInt();
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		for (int int2 = 0; int2 < int1; ++int2) {
			KahluaTable kahluaTable2 = LuaManager.platform.newTable();
			String string = GameWindow.ReadStringUTF(byteBuffer);
			String string2 = string + "." + GameWindow.ReadStringUTF(byteBuffer);
			long long1 = byteBuffer.getLong();
			long long2 = byteBuffer.getLong();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			int int3 = byteBuffer.getInt();
			String string3 = GameWindow.ReadStringUTF(byteBuffer);
			String string4 = GameWindow.ReadStringUTF(byteBuffer);
			boolean boolean2 = byteBuffer.get() == 1;
			kahluaTable2.rawset("fullType", string2);
			kahluaTable2.rawset("itemId", long1);
			kahluaTable2.rawset("isEquip", boolean1);
			kahluaTable2.rawset("var", (double)Math.round((double)float1 * 100.0) / 100.0);
			kahluaTable2.rawset("count", int3.makeConcatWithConstants < invokedynamic > (int3));
			kahluaTable2.rawset("cat", string3);
			kahluaTable2.rawset("parrentId", long2);
			kahluaTable2.rawset("hasParrent", long2 != -1L);
			kahluaTable2.rawset("container", string4);
			kahluaTable2.rawset("inInv", boolean2);
			kahluaTable.rawset(kahluaTable.size() + 1, kahluaTable2);
		}

		LuaEventManager.triggerEvent("MngInvReceiveItems", kahluaTable);
	}

	public static void sendGetItemInvMng(long long1) {
	}

	static void receiveSpawnRegion(ByteBuffer byteBuffer, short short1) {
		if (instance.ServerSpawnRegions == null) {
			instance.ServerSpawnRegions = LuaManager.platform.newTable();
		}

		int int1 = byteBuffer.getInt();
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		try {
			kahluaTable.load((ByteBuffer)byteBuffer, 194);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		instance.ServerSpawnRegions.rawset(int1, kahluaTable);
	}

	static void receivePlayerConnectLoading(ByteBuffer byteBuffer) throws IOException {
		int int1 = byteBuffer.position();
		if (!instance.receivePlayerConnectWhileLoading(byteBuffer)) {
			byteBuffer.position(int1);
			throw new IOException();
		}
	}

	static void receiveClimateManagerPacket(ByteBuffer byteBuffer, short short1) {
		ClimateManager climateManager = ClimateManager.getInstance();
		if (climateManager != null) {
			try {
				climateManager.receiveClimatePacket(byteBuffer, (UdpConnection)null);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	static void receiveServerMap(ByteBuffer byteBuffer, short short1) {
		ClientServerMap.receivePacket(byteBuffer);
	}

	static void receivePassengerMap(ByteBuffer byteBuffer, short short1) {
		PassengerMap.clientReceivePacket(byteBuffer);
	}

	static void receiveIsoRegionServerPacket(ByteBuffer byteBuffer, short short1) {
		IsoRegions.receiveServerUpdatePacket(byteBuffer);
	}

	public static void sendIsoRegionDataRequest() {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.doPacket(byteBufferWriter);
		PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.send(connection);
	}

	public void sendSandboxOptionsToServer(SandboxOptions sandboxOptions) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SandboxOptions.doPacket(byteBufferWriter);
		try {
			sandboxOptions.save(byteBufferWriter.bb);
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		} finally {
			PacketTypes.PacketType.SandboxOptions.send(connection);
		}
	}

	static void receiveSandboxOptions(ByteBuffer byteBuffer, short short1) {
		try {
			SandboxOptions.instance.load(byteBuffer);
			SandboxOptions.instance.applySettings();
			SandboxOptions.instance.toLua();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	static void receiveChunkObjectState(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(short2, short3);
		if (chunk != null) {
			try {
				chunk.loadObjectState(byteBuffer);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}
	}

	static void receivePlayerLeaveChat(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processLeaveChatPacket(byteBuffer);
	}

	static void receiveInitPlayerChat(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processInitPlayerChatPacket(byteBuffer);
	}

	static void receiveAddChatTab(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processAddTabPacket(byteBuffer);
	}

	static void receiveRemoveChatTab(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processRemoveTabPacket(byteBuffer);
	}

	static void receivePlayerNotFound(ByteBuffer byteBuffer, short short1) {
		ChatManager.getInstance().processPlayerNotFound();
	}

	public static void sendZombieHelmetFall(IsoPlayer player, IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.ZombieHelmetFalling.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putShort(gameCharacter.getOnlineID());
		byteBufferWriter.putUTF(inventoryItem.getType());
		PacketTypes.PacketType.ZombieHelmetFalling.send(connection);
	}

	static void receiveZombieHelmetFalling(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		IsoZombie zombie = (IsoZombie)IDToZombieMap.get(short2);
		String string = GameWindow.ReadString(byteBuffer);
		if (zombie != null && !StringUtils.isNullOrEmpty(string)) {
			zombie.helmetFall(true, string);
		}
	}

	public static void sendPerks(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncPerks.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Sneak));
		byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Strength));
		byteBufferWriter.putInt(player.getPerkLevel(PerkFactory.Perks.Fitness));
		PacketTypes.PacketType.SyncPerks.send(connection);
	}

	static void receiveSyncPerks(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null && !player.isLocalPlayer()) {
			player.remoteSneakLvl = int1;
			player.remoteStrLvl = int2;
			player.remoteFitLvl = int3;
		}
	}

	public static void sendWeight(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncWeight.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putDouble(player.getNutrition().getWeight());
		PacketTypes.PacketType.SyncWeight.send(connection);
	}

	static void receiveSyncWeight(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		double double1 = byteBuffer.getDouble();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null && !player.isLocalPlayer()) {
			player.getNutrition().setWeight(double1);
		}
	}

	static void receiveGlobalModData(ByteBuffer byteBuffer, short short1) {
		GlobalModData.instance.receive(byteBuffer);
	}

	public static void sendSafehouseInvite(SafeHouse safeHouse, IsoPlayer player, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SendSafehouseInvite.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(safeHouse.getTitle());
		byteBufferWriter.putUTF(player.getUsername());
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putInt(safeHouse.getX());
		byteBufferWriter.putInt(safeHouse.getY());
		byteBufferWriter.putInt(safeHouse.getW());
		byteBufferWriter.putInt(safeHouse.getH());
		PacketTypes.PacketType.SendSafehouseInvite.send(connection);
	}

	static void receiveSendSafehouseInvite(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		SafeHouse safeHouse = SafeHouse.getSafeHouse(int1, int2, int3, int4);
		LuaEventManager.triggerEvent("ReceiveSafehouseInvite", safeHouse, string2);
	}

	public static void acceptSafehouseInvite(SafeHouse safeHouse, String string) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.AcceptedSafehouseInvite.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(safeHouse.getTitle());
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(username);
		byteBufferWriter.putInt(safeHouse.getX());
		byteBufferWriter.putInt(safeHouse.getY());
		byteBufferWriter.putInt(safeHouse.getW());
		byteBufferWriter.putInt(safeHouse.getH());
		PacketTypes.PacketType.AcceptedSafehouseInvite.send(connection);
	}

	static void receiveAcceptedSafehouseInvite(ByteBuffer byteBuffer, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		SafeHouse safeHouse = SafeHouse.getSafeHouse(int1, int2, int3, int4);
		if (safeHouse != null) {
			safeHouse.addPlayer(string3);
		}

		LuaEventManager.triggerEvent("AcceptedSafehouseInvite", safeHouse.getTitle(), string2);
	}

	public static void sendEquippedRadioFreq(IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SyncEquippedRadioFreq.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)player.PlayerIndex);
		byteBufferWriter.putInt(player.invRadioFreq.size());
		for (int int1 = 0; int1 < player.invRadioFreq.size(); ++int1) {
			byteBufferWriter.putInt((Integer)player.invRadioFreq.get(int1));
		}

		PacketTypes.PacketType.SyncEquippedRadioFreq.send(connection);
	}

	static void receiveSyncEquippedRadioFreq(ByteBuffer byteBuffer, short short1) {
		short short2 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			player.invRadioFreq.clear();
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				player.invRadioFreq.add(byteBuffer.getInt());
			}

			for (int2 = 0; int2 < player.invRadioFreq.size(); ++int2) {
				System.out.println(player.invRadioFreq.get(int2));
			}
		}
	}

	public static void sendSneezingCoughing(short short1, int int1, byte byte1) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.SneezeCough.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(short1);
		byte byte2 = 0;
		if (int1 % 2 == 0) {
			byte2 = (byte)(byte2 | 1);
		}

		if (int1 > 2) {
			byte2 = (byte)(byte2 | 2);
		}

		if (byte1 > 1) {
			byte2 = (byte)(byte2 | 4);
		}

		byteBufferWriter.putByte(byte2);
		PacketTypes.PacketType.SneezeCough.send(connection);
	}

	static void receiveSneezeCough(ByteBuffer byteBuffer, short short1) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		if (player != null) {
			byte byte1 = byteBuffer.get();
			boolean boolean1 = (byte1 & 1) == 0;
			boolean boolean2 = (byte1 & 2) != 0;
			int int1 = (byte1 & 4) == 0 ? 1 : 2;
			player.setVariable("Ext", boolean1 ? "Sneeze" + int1 : "Cough");
			player.Say(Translator.getText("IGUI_PlayerText_" + (boolean1 ? "Sneeze" : "Cough") + (boolean2 ? "Muffled" : "")));
			player.reportEvent("EventDoExt");
		}
	}

	public static void sendBurnCorpse(short short1, short short2) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.BurnCorpse.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(short1);
		byteBufferWriter.putShort(short2);
		PacketTypes.PacketType.SneezeCough.send(connection);
	}

	private static void rememberPlayerPosition(IsoPlayer player, float float1, float float2) {
		if (player != null && !player.isLocalPlayer()) {
			if (positions.containsKey(player.getOnlineID())) {
				((Vector2)positions.get(player.getOnlineID())).set(float1, float2);
			} else {
				positions.put(player.getOnlineID(), new Vector2(float1, float2));
			}
		}
	}

	static void receiveValidatePacket(ByteBuffer byteBuffer, short short1) {
		ValidatePacket validatePacket = new ValidatePacket();
		validatePacket.parse(byteBuffer, connection);
		if (validatePacket.isConsistent()) {
			validatePacket.process(connection);
		}
	}

	public static void sendValidatePacket(ValidatePacket validatePacket) {
		ByteBufferWriter byteBufferWriter = connection.startPacket();
		PacketTypes.PacketType.Validate.doPacket(byteBufferWriter);
		validatePacket.write(byteBufferWriter);
		PacketTypes.PacketType.Validate.send(connection);
	}

	static  {
		port = GameServer.DEFAULT_PORT;
		checksum = "";
		checksumValid = false;
		pingsList = new ArrayList();
		loadedCells = new ClientServerMap[4];
		isPaused = false;
		positions = new HashMap(ServerOptions.getInstance().getMaxPlayers());
		MainLoopNetDataQ = new ConcurrentLinkedQueue();
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
		RequestRadioData,
		ReceivedRadioData,
		Complete;

		private static GameClient.RequestState[] $values() {
			return new GameClient.RequestState[]{Start, RequestDescriptors, ReceivedDescriptors, RequestMetaGrid, ReceivedMetaGrid, RequestMapZone, ReceivedMapZone, RequestPlayerZombieDescriptors, ReceivedPlayerZombieDescriptors, RequestRadioData, ReceivedRadioData, Complete};
		}
	}
}
